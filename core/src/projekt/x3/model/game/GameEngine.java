package x3.model.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import x3.DetonatorCircle;
import x3.model.effect.Effect;
import x3.model.effect.debuff.ForcedBombsEffect;
import x3.model.game.objects.*;
import x3.model.game.suppliers.BodyFactory;
import x3.model.game.suppliers.BombPool;
import x3.model.game.suppliers.ExplosionPool;
import x3.model.map.GameMap;
import x3.model.util.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static x3.model.game.suppliers.BodyFactory.EffectType.*;
import static x3.model.util.CollisionFilters.*;

/**
 * A GameEngine object is responsible for creating and updating the objects involved in running and showing the game.
 * <br>
 * A GameEngine keepstrack of all players, monsters, walls, boxes, effects, bombs, explosions, particle effects
 * and the boundaries. This object maintains the physics simulation of the game and updates the involved members.
 */
public class GameEngine implements Disposable {
    private static final Random RANDOM = new Random();

    private final World world = new World(new Vector2(0, 0), true);
    private final BattleRoyaleManager brManager;
    private final List<Body> players = new ArrayList<>();
    private final List<Body> monsters = new ArrayList<>();
    private final List<Body> fObjects = new ArrayList<>();
    private final List<Body> effects = new ArrayList<>();
    private final List<Body> bombs = new ArrayList<>();
    private final List<Body> explosions = new ArrayList<>();
    private final List<ParticleEffect> flames = new ArrayList<>();
    private final List<Body> boundaries = new ArrayList<>();
    private final DetonatorCircle game;
    private final BodyFactory bodyFactory;
    private final BombPool bombPool;
    private final ExplosionPool explosionPool;
    private final float PPM;
    private final GameMap map;
    private boolean gameEnded;

    /**
     * Only constructor.
     * <br>
     * This constructor initializes the required properties and initializes the {@link GameMap map} that the
     * game will be played on.
     *
     * @param game the object responsible for providing the engine with assets and round information.
     * @param map  the object describing how a map should be built by the <code>GameEngine</code>.
     * @param PPM  Pixels Per Meter - required by {@link World} so physics units remain small.
     */
    public GameEngine(final DetonatorCircle game, final GameMap map, final float PPM) {
        this.PPM = PPM;
        this.game = game;
        this.map = map;
        brManager = new BattleRoyaleManager(map.getMaxShrinks());
        bodyFactory = new BodyFactory(game, world, PPM, PPM * 3 / 4, PPM * 1 / 2);
        bombPool = new BombPool(world, PPM, PPM * 1 / 2,
            game.getTexture("bomb"), game.getTexture("bomb2"), game.getTexture("bomb3"));
        explosionPool = new ExplosionPool(world, PPM, game.getExplosionParticles());

        buildMap();

        world.setContactListener(new CollisionListener());
    }

    private void buildMap() {
        buildWalls();
        buildBoxes();
        buildPlayers();
        buildMonsters();
        buildBoundaries();
    }

    /**
     * Updates the state of the game based on the time elapsed since the previously rendered frame.
     * <br>
     * Order of objects getting updated:
     * <ol>
     *     <li>Every bomb's detonation and {@link Bomb#hasBlown status};</li>
     *     <li>Every effect's {@link Effect#bodyDead status};</li>
     *     <li>Every monster's position, velocity, direction and {@link Monster#isDead status};</li>
     *     <li>
     *         Every player's position, response to movement commands, velocty, bomb placement,
     *         its {@link Player#manageEffects effects} and {@link Player#isDead status};
     *     </li>
     *     <li>Every explosion's collisions and {@link Explosion#ready status};</li>
     *     <li>Every flame particle effect's position, velocity and {@link ParticleEffect#isComplete status};</li>
     *     <li>The {@link BattleRoyaleManager Battle Royale} add-on's status and the game's borders;</li>
     *     <li>The {@link World world}'s internal physics;</li>
     *     <li>Every bomb's collision filtering;</li>
     * </ol>
     * Finally, the method checks for round-end conditions.
     *
     * @param delta time elapsed since the previously rendered frame.
     */
    public void step(float delta) {
        updateBombs();
        updateEffects();
        updateMonsters();
        updatePlayers();
        updateExplosions();
        updateFlames(delta);
        updateBattleRoyale();
        world.step(1 / 60f, 6, 2);
        updateBombCollisions();
        updateGameState();
    }

    /**
     * Returns the {@link World} that handles physics simulations and collision inside the game.
     * @return the {@link World} of this game.
     */
    public World getWorld() {
        return world;
    }

    /**
     * Returns a stream of the players' sprites.
     * @return a stream of the players' sprites.
     */
    public Stream<Player> getPlayers() {
        return players.stream().map(p -> (Player) p.getUserData());
    }

    /**
     * Returns a stream of the monsters' sprites.
     * @return a stream of the monsters' sprites.
     */
    public Stream<Monster> getMonsters() {
        return monsters.stream().map(m -> (Monster) m.getUserData());
    }

    /**
     * Returns a stream of the sprites of all walls and boxes;
     * @return a stream of the sprites of all walls and boxes;
     */
    public Stream<Sprite> getFieldObjects() {
        return fObjects.stream().map(f -> (Sprite) f.getUserData());
    }

    /**
     * Returns a stream of the effects' sprites.
     * @return a stream of the effects' sprites.
     */
    public Stream<Sprite> getEffects() {
        return effects.stream().map(e -> (Effect) e.getUserData());
    }

    /**
     * Returns a stream of the bombs' sprites.
     * @return a stream of the bombs' sprites.
     */
    public Stream<Bomb> getBombs() {
        return bombs.stream().map(b -> (Bomb) b.getUserData());
    }

    /**
     * Returns a stream of the flame particle effects.
     * @return a stream of the flame particle effects.
     */
    public Stream<ParticleEffect> getFlames() {
        return flames.stream();
    }

    /**
     * Returns whether the current round has ended.
     * @return true if the current round has ended.
     */
    public boolean isGameEnded() {
        return gameEnded;
    }

    /**
     * Returns the remaining number of seconds until the next {@link BattleRoyaleManager shrinking} of the map.
     * @return the remaining number of seconds.
     */
    public double getSecondsUntilShrink() {
        return brManager.timeUntilShrink() / 1000d;
    }

    /**
     * Returns whether the game is still allowed to shrink the map.
     * @return true if the map cannot shrink anymore.
     * @see BattleRoyaleManager#isAtMax()
     */
    public boolean isAtMaxShrink() {
        return brManager.isAtMax();
    }

    private void buildWalls() {
        map.getWalls().forEach(pair -> fObjects.add(bodyFactory.buildWall(pair)));
    }

    private void buildBoxes() {
        final int[] total = { map.getBuffCount() + map.getDebuffCount() };
        map.getBoxes()
            .forEach(pair -> {
                if (total[0] > 0) {
                    if (total[0] > map.getDebuffCount()) {
                        fObjects.add(bodyFactory.buildBox(pair, BUFF));
                    } else {
                        fObjects.add(bodyFactory.buildBox(pair, DEBUFF));
                    }
                    --total[0];
                } else {
                    fObjects.add(bodyFactory.buildBox(pair, NONE));
                }
            });
    }

    private void buildPlayers() {
        final short[] playerCategories = { PLAYER_1, PLAYER_2, PLAYER_3 };
        final short[] bombCategories = { BOMB_TO_P1, BOMB_TO_P2, BOMB_TO_P3 };
        final int[] i = { 0 };
        map.getPlayerSpawns(game.getPlayerCount())
            .forEach(pair -> {
                players.add(
                    bodyFactory.buildPlayer(
                        pair,
                        i[0],
                        playerCategories[i[0]],
                        (short) (bombCategories[i[0]] | ALL_PLAYERS | WALL | BOX | EFFECT | MONSTER | BOUNDARY)
                    )
                );
                i[0]++;
            });
    }

    private void buildMonsters() {
        map.getMonsterSpawns().forEach(pair -> monsters.add(bodyFactory.buildMonster(pair)));
    }

    private void buildBoundaries() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        bodyDef.position.set(0, (map.getMapHeight() - 1) * PPM);
        Body northBoundary = world.createBody(bodyDef);
        northBoundary.setUserData(Pair.of(0, -1));

        bodyDef.position.set(0, 0);
        Body southBoundary = world.createBody(bodyDef);
        southBoundary.setUserData(Pair.of(0, 1));

        bodyDef.position.set(0, 0);
        Body westBoundary = world.createBody(bodyDef);
        westBoundary.setUserData(Pair.of(1, 0));

        bodyDef.position.set((map.getMapWidth() - 1) * PPM, 0);
        Body eastBoundary = world.createBody(bodyDef);
        eastBoundary.setUserData(Pair.of(-1, 0));

        PolygonShape shape = new PolygonShape();

        shape.setAsBox(map.getMapWidth() * PPM / 2, PPM / 2 - 0.1f, new Vector2(map.getMapWidth() * PPM / 2, PPM / 2), 0);
        initBoundaryFixture(northBoundary, shape);
        boundaries.add(northBoundary);

        initBoundaryFixture(southBoundary, shape);
        boundaries.add(southBoundary);

        shape.setAsBox(PPM / 2 - 0.1f, map.getMapHeight() * PPM / 2, new Vector2(PPM / 2, map.getMapHeight() * PPM / 2), 0);
        initBoundaryFixture(westBoundary, shape);
        boundaries.add(westBoundary);

        initBoundaryFixture(eastBoundary, shape);
        boundaries.add(eastBoundary);

        shape.dispose();
    }

    private void initBoundaryFixture(Body body, Shape shape) {
        Filter filter = new Filter();
        filter.categoryBits = BOUNDARY;
        filter.maskBits = ALL_PLAYERS | MONSTER;

        Fixture fixture = body.createFixture(shape, 0.0f);
        fixture.setFilterData(filter);
        fixture.setSensor(true);
    }

    /**
     * Returns a stream of all cells that are closed off by the boundaries.
     * @return a stream of column-row index pairs.
     */
    public Stream<Pair> getBoundaryAffectedCells() {
        if (!brManager.getStartedShrinking()) {
            return Stream.empty();
        }
        int shrunk = brManager.getShrunk();
        int centerX = map.getMapWidth() / 2;
        int layersX = centerX - shrunk;
        int centerY = map.getMapHeight() / 2;
        int layersY = centerY - shrunk;
        return IntStream.range(0, map.getMapWidth())
            .mapToObj(col ->
                IntStream.range(0, map.getMapHeight())
                    .mapToObj(row -> Pair.of(col, row)))
            .flatMap(stream -> stream)
            .filter(pair -> Math.abs(pair.col - centerX) >= layersX || Math.abs(pair.row - centerY) >= layersY);
    }

    private void updatePlayers() {
        for (Iterator<Body> it = players.iterator(); it.hasNext(); ) {
            Body body = it.next();
            Player player = (Player) body.getUserData();
            player.setPosition(body.getPosition().x, body.getPosition().y);
            Map<Integer, Pair> offsetMap = Map.of(
                player.leftKey, Pair.of(-player.moveSpeed, 0),
                player.rightKey, Pair.of(player.moveSpeed, 0),
                player.upKey, Pair.of(0, player.moveSpeed),
                player.downKey, Pair.of(0, -player.moveSpeed)
            );
            Vector2 vel = body.getLinearVelocity();
            Vector2 pos = body.getPosition();
            offsetMap.forEach((key, value) -> {
                if (Gdx.input.isKeyPressed(key)
                    && Math.abs(vel.x) < player.moveSpeed
                    && Math.abs(vel.y) < player.moveSpeed) {
                    body.applyLinearImpulse(value.col, value.row, pos.x, pos.y, true);
                }
            });
            if ((Gdx.input.isKeyJustPressed(player.bombKey) || player.hasEffect(ForcedBombsEffect.class)) && player.canPlace()) {
                Vector2 targetCell = new Vector2(
                    Math.round(body.getPosition().x / PPM) * PPM,
                    Math.round(body.getPosition().y / PPM) * PPM
                );
                if (!getStaticQuery(targetCell).isBombFound()) {
                    player.bombPlace();
                    bombs.add(bombPool.getBomb(targetCell, body));
                }
            }
            player.manageEffects();
            if (player.isDead()) {
                it.remove();
                world.destroyBody(body);
            }
            if (!it.hasNext()) {
                break;
            }
        }
    }

    private void updateBombCollisions() {
        bombs.forEach(body -> {
            Bomb bomb = (Bomb) body.getUserData();
            Fixture mainFixture = body.getFixtureList().get(0);
            Filter filter = new Filter();

            Set<Integer> notAllowedIndexes = getPlayers()
                .filter(p -> !bomb.playersAllowedThrough.contains(p))
                .map(p -> p.ind)
                .collect(Collectors.toSet());
            filter.categoryBits = (short) ((notAllowedIndexes.contains(0) ? BOMB_TO_P1 : 0)
                | (notAllowedIndexes.contains(1) ? BOMB_TO_P2 : 0)
                | (notAllowedIndexes.contains(2) ? BOMB_TO_P3 : 0));
            filter.maskBits = (short) ((notAllowedIndexes.contains(0) ? PLAYER_1 : 0)
                | (notAllowedIndexes.contains(1) ? PLAYER_2 : 0)
                | (notAllowedIndexes.contains(2) ? PLAYER_3 : 0)
                | MONSTER);
            mainFixture.setFilterData(filter);
        });
    }

    private void updateMonsters() {
        for (Iterator<Body> it = monsters.iterator(); it.hasNext(); ) {
            Body body = it.next();
            Monster monster = (Monster) body.getUserData();
            Vector2 vel = body.getLinearVelocity();
            Vector2 pos = body.getPosition();
            monster.setPosition(pos.x, pos.y);

            if (Math.abs(vel.x) < monster.moveSpeed && Math.abs(vel.y) < monster.moveSpeed) {
                body.applyLinearImpulse(monster.direction.x, monster.direction.y, pos.x, pos.y, true);
            }

            if (Math.abs(vel.x) <= 1f && Math.abs(vel.y) <= 1f) {
                monster.setRandomDir();
            }

            if (RANDOM.nextInt(0, 100) == 1) {
                monster.setRandomDir();
            }

            if (monster.isDead()) {
                it.remove();
                world.destroyBody(body);
            }

            if (!it.hasNext()) {
                break;
            }
        }
    }

    private void updateBombs() {
        for (Iterator<Body> it = bombs.iterator(); it.hasNext(); ) {
            Body body = it.next();
            Bomb bomb = (Bomb) body.getUserData();
            bomb.stepAge();
            if (bomb.getAge() < 2) {
                bomb.stepAge();
            } else if (!bomb.hasSetPlayers) {
                bomb.hasSetPlayers = true;
            }
            bomb.checkBombState();
            if (bomb.hasBlown) {
                it.remove();
                bomb.owner.bombBlew();
                bombPool.returnBomb(body);
            } else if (bomb.canBlow()) {
                detonateBomb(body);
            }
            if (!it.hasNext()) {
                break;
            }
        }
    }

    private void updateEffects() {
        for (Iterator<Body> it = effects.iterator(); it.hasNext(); ) {
            Body body = it.next();
            Effect effect = (Effect) body.getUserData();
            if (effect.bodyDead) {
                it.remove();
                world.destroyBody(body);
            }
            if (!it.hasNext()) {
                break;
            }
        }
    }

    private void updateFlames(float delta) {
        for (Iterator<ParticleEffect> it = flames.iterator(); it.hasNext(); ) {
            ParticleEffect effect = it.next();
            effect.update(delta);
            if (effect.isComplete()) {
                it.remove();
            }
            if (!it.hasNext()) {
                break;
            }
        }
    }

    private void updateBattleRoyale() {
        if (brManager.canStartShrinking()) {
            brManager.startShrinking();
            return;
        }
        if (!brManager.canShrink()) {
            return;
        }
        brManager.shrink();
        boundaries.forEach(body -> {
            Pair direction = (Pair) body.getUserData();
            body.setTransform(body.getPosition().x + direction.col * PPM, body.getPosition().y + direction.row * PPM, 0);
        });
    }

    private StaticQuery getStaticQuery(final Vector2 position) {
        float give = PPM/16f;
        StaticQuery callback = new StaticQuery();
        world.QueryAABB(callback,
            position.x + give,
            position.y + give,
            position.x - give + PPM,
            position.y - give + PPM);
        return callback;
    }

    private void detonateBomb(final Body bomb) {
        List<SimpleEntry<Vector2, Integer>> targets = findBombAffectedCells(bomb);
        ((Bomb) bomb.getUserData()).hasBlown = true;

        targets.forEach(entry -> {
            explosions.add(explosionPool.getExplosion(entry.getKey(), entry.getValue()));
        });
    }

    private MovingQuery getMovingExplosionQuery(final Vector2 explosionPos) {
        MovingQuery callback = new MovingQuery();
        float lowerX = explosionPos.x; //  - playerWidth / 16;
        float lowerY = explosionPos.y; //  - playerHeight / 16;
        float upperX = explosionPos.x + PPM; //  + playerWidth / 16;
        float upperY = explosionPos.y + PPM; //  + playerHeight / 16;

        world.QueryAABB(callback, lowerX, lowerY, upperX, upperY);
        return callback;
    }

    private void updateExplosions() {
        for (Iterator<Body> it = explosions.iterator(); it.hasNext(); ) {
            Body body = it.next();
            Explosion explosion = (Explosion) body.getUserData();
            if (!explosion.ready()) {
                if (it.hasNext()) {
                    continue;
                } else {
                    break;
                }
            }

            flames.addAll(explosion.getParticleEffects());
            MovingQuery movingQuery = getMovingExplosionQuery(body.getPosition());
            movingQuery.getPlayers().forEach(Player::markDead);
            movingQuery.getMonsters().forEach(Monster::markDead);
            StaticQuery query = getStaticQuery(body.getPosition());
            if (query.isBoxFound()) {
                Body boxBody = query.getResult();
                Box box = (Box) boxBody.getUserData();
                if (box.getEffect().isPresent()) {
                    effects.add(bodyFactory.buildEffect(
                        Pair.of(
                            Math.round(boxBody.getPosition().x / PPM),
                            Math.round(boxBody.getPosition().y / PPM)
                        ), box.getEffect().get()));
                }
                fObjects.remove(boxBody);
                world.destroyBody(boxBody);
            } else if (query.isEffectFound()) {
                Body effectBody = query.getResult();
                effects.remove(effectBody);
                world.destroyBody(effectBody);
            } else if (query.isBombFound()) {
                ((Bomb) query.getResult().getUserData()).forceBlow = true;
            }
            it.remove();
            explosionPool.returnExplosion(body);

            if (!it.hasNext()) {
                break;
            }
        }
    }

    private List<SimpleEntry<Vector2, Integer>> findBombAffectedCells(final Body bomb) {
        Bomb b = (Bomb) bomb.getUserData();
        Vector2 center = bomb.getPosition();
        List<SimpleEntry<Vector2, Integer>> affectedCells = new ArrayList<>(List.of(new SimpleEntry<>(center, 0)));

        affectedCells.addAll(exploreExplosionPath(center, new Vector2(0, PPM), b.owner.bombRadius));
        affectedCells.addAll(exploreExplosionPath(center, new Vector2(PPM, 0), b.owner.bombRadius));
        affectedCells.addAll(exploreExplosionPath(center, new Vector2(0, -PPM), b.owner.bombRadius));
        affectedCells.addAll(exploreExplosionPath(center, new Vector2(-PPM, 0), b.owner.bombRadius));
        return affectedCells;
    }

    /**
     * Goes over map grid points and returns a list of all positions and their distance from the origin,
     * where an explosion is supposed to occur.
     * <br>
     * This method tracks how many steps it has taken, and while that value is below the maximum range,
     * it checks the next position for walls and boxes.
     * <br>
     * At the next position,
     * <ol>
     *     <li>
     *         If a wall is found, the path can no longer be explored and the method return
     *         since walls cannot be blown up and the explosions can't spread over them.
     *     </li>
     *     <li>
     *         The position is simply added to the list.
     *     </li>
     *     <li>
     *         If a box is found, the method returns,
     *         since boxes are allowed to blow up, but the explosions can't spread over them.
     *     </li>
     *     <li>
     *         Otherwise,  and the number of steps taken is incremented.
     *     </li>
     * </ol>
     *
     * @param center the origin of the explosion.
     * @param direction the direction that the loop takes for exploring position candidates.
     * @param maxRange the maximum number of positions this call is allowed to explore.
     * @return a list of (position, distance from origin) pairs.
     */
    private List<SimpleEntry<Vector2, Integer>> exploreExplosionPath(final Vector2 center, final Vector2 direction, int maxRange) {
        List<SimpleEntry<Vector2, Integer>> path = new ArrayList<>();
        Vector2 next = center.cpy().add(direction);
        int stepsTaken = 0;
        while (stepsTaken < maxRange) {
            StaticQuery query = getStaticQuery(next);
            if (query.isWallFound()) {
                break;
            }
            path.add(new SimpleEntry<>(next.cpy(), stepsTaken + 1));
            if (query.isBoxFound()) {
                break;
            }
            next.add(direction);
            stepsTaken++;
        }
        return path;
    }

    private void updateGameState() {
        if (gameEnded) {
            return;
        }

        if (players.isEmpty()) {
            gameEnded = true;
        } else if (players.size() == 1 && bombs.isEmpty() && explosions.isEmpty()) {
            Player winner = (Player) players.get(0).getUserData();
            game.wonRounds[winner.ind]++;
            gameEnded = true;
        }
    }

    @Override
    public void dispose() {
        world.dispose();
        bodyFactory.dispose();
        bombPool.dispose();
    }
}
