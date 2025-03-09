package x3.model.game.suppliers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Disposable;
import x3.DetonatorCircle;
import x3.model.effect.Effect;
import x3.model.effect.buff.BiggerBombEffect;
import x3.model.effect.buff.BonusBombEffect;
import x3.model.effect.debuff.ForcedBombsEffect;
import x3.model.effect.debuff.NoBombsEffect;
import x3.model.effect.debuff.SlowEffect;
import x3.model.effect.debuff.SmallBombEffect;
import x3.model.game.objects.Box;
import x3.model.game.objects.Monster;
import x3.model.game.objects.Player;
import x3.model.game.objects.Wall;
import x3.model.util.Pair;

import java.util.Optional;
import java.util.Random;

import static x3.model.game.suppliers.BodyFactory.EffectType.BUFF;
import static x3.model.game.suppliers.BodyFactory.EffectType.DEBUFF;
import static x3.model.util.CollisionFilters.*;

/**
 * A <code>BodyFactory</code> objects provides methods for generating bodies and objects that
 * the game only needs a predictable number of (e.g. walls, boxes, players, monsters).
 * <br>
 * Utility objects are reused during instantiation and freed when {@link Disposable#dispose()} is called.
 */
public class BodyFactory implements Disposable {
    private static final Random RANDOM = new Random();

    private final World world;
    private final float PPM;

    private final Texture wallTexture;
    private final Texture boxTexture;
    private final Texture monsterTexture;
    private final Texture player1Texture;
    private final Texture player2Texture;
    private final Texture player3Texture;
    private final Texture bonusBombTexture;
    private final Texture biggerBombTexture;
    private final Texture forcedBombsTexture;
    private final Texture noBombsTexture;
    private final Texture slowTexture;
    private final Texture smallBombTexture;

    private final BodyDef staticDef;
    private final BodyDef movingDef;

    private final PolygonShape unitShape;
    private final PolygonShape effectShape;
    private final PolygonShape movingShape;

    private final FixtureDef movingFDef;

    private final Filter wallFilter;
    private final Filter boxFilter;
    private final Filter monsterFilter;
    private final Filter effectFilter;

    private final float playerSize;
    private final float effectSize;

    /**
     * Only constructor.
     * <br>
     * Initializes the reusable utility objects and collects the textures that will be used during instantiation.
     *
     * @param game       the object that stores the {@link Texture textures}.
     * @param world      the {@link World} where the bodies will be created.
     * @param PPM        Pixels Per Meter.
     * @param playerSize size of the {@link Player Players}.
     * @param effectSize size of the {@link Effect Effects}.
     */
    public BodyFactory(final DetonatorCircle game, final World world, final float PPM, final float playerSize, final float effectSize) {
        this.world = world;
        this.PPM = PPM;
        this.playerSize = playerSize;
        this.effectSize = effectSize;

        wallTexture = game.getTexture("wall");
        boxTexture = game.getTexture("box");
        monsterTexture = game.getTexture("monster");
        player1Texture = game.getTexture("player1");
        player2Texture = game.getTexture("player2");
        player3Texture = game.getTexture("player3");
        bonusBombTexture = game.getTexture("bonusbomb");
        biggerBombTexture = game.getTexture("biggerbomb");
        forcedBombsTexture = game.getTexture("forcedbombs");
        noBombsTexture = game.getTexture("nobombs");
        slowTexture = game.getTexture("slow");
        smallBombTexture = game.getTexture("smallbomb");

        staticDef = new BodyDef();
        staticDef.type = BodyType.StaticBody;

        movingDef = new BodyDef();
        movingDef.type = BodyType.DynamicBody;
        movingDef.linearDamping = 20.0f;

        unitShape = new PolygonShape();
        unitShape.setAsBox(PPM / 2, PPM / 2, new Vector2(PPM / 2, PPM / 2), 0);

        effectShape = new PolygonShape();
        effectShape.setAsBox(effectSize / 2, effectSize / 2,
            new Vector2(effectSize / 2, effectSize / 2), 0);

        movingShape = new PolygonShape();
        movingShape.setAsBox(playerSize / 2, playerSize / 2,
            new Vector2(playerSize / 2, playerSize / 2), 0);

        movingFDef = new FixtureDef();
        movingFDef.shape = movingShape;
        movingFDef.density = 0;
        movingFDef.friction = 0;

        wallFilter = new Filter();
        wallFilter.categoryBits = WALL;
        wallFilter.maskBits = ALL_PLAYERS | MONSTER;

        boxFilter = new Filter();
        boxFilter.categoryBits = BOX;
        boxFilter.maskBits = ALL_PLAYERS | MONSTER;

        monsterFilter = new Filter();
        monsterFilter.categoryBits = MONSTER;
        monsterFilter.maskBits = WALL | BOX | ALL_PLAYERS | ALL_BOMBS | BOUNDARY;

        effectFilter = new Filter();
        effectFilter.categoryBits = EFFECT;
        effectFilter.maskBits = ALL_PLAYERS;
    }

    private Effect randomBuff() {
        if (RANDOM.nextInt(0, 2) == 0) {
            return new BonusBombEffect(bonusBombTexture);
        }
        return new BiggerBombEffect(biggerBombTexture);
    }

    private Effect randomDebuff() {
        return switch (RANDOM.nextInt(0, 4)) {
            case 0 -> new ForcedBombsEffect(forcedBombsTexture);
            case 1 -> new NoBombsEffect(noBombsTexture);
            case 2 -> new SlowEffect(slowTexture);
            default -> new SmallBombEffect(smallBombTexture);
        };
    }

    private void setStaticDefPosition(Pair position) {
        staticDef.position.set(position.col * PPM, position.row * PPM);
    }

    private void setStaticDefPosition(Pair position, float offset) {
        staticDef.position.set(position.col * PPM + offset, position.row * PPM + offset);
    }

    /**
     * Returns a newly created {@link Wall} and its enclosing {@link Body} at the specified position.
     * <br>
     * Walls are {@link BodyType#StaticBody static bodies} which are allowed to collide with all players and monsters.
     * Their size is always PPM*PPM.
     *
     * @param position the position of the new body.
     * @return the new body.
     */
    public Body buildWall(final Pair position) {
        setStaticDefPosition(position);
        Body body = world.createBody(staticDef);

        Fixture f = body.createFixture(unitShape, 0.0f);
        f.getFilterData().categoryBits = wallFilter.categoryBits;
        f.getFilterData().maskBits = wallFilter.maskBits;

        Wall wall = new Wall();
        wall.setTexture(wallTexture);
        wall.setSize(PPM, PPM);
        wall.setOriginCenter();
        wall.setPosition(body.getPosition().x, body.getPosition().y);

        body.setUserData(wall);

        return body;
    }

    /**
     * Returns a newly created {@link Box} and its enclosing {@link Body} at the specified position,
     * containing the specified type of {@link Effect} or nothing, if <code>type</code> is {@link EffectType#NONE}.
     * <br>
     * Boxes are {@link BodyType#StaticBody static bodies} which are allowed to collide with all players and monsters.
     * Their size is always PPM*PPM.
     * <br>
     * Effects are selected randomly from the specified category.
     *
     * @param position the position of the new body.
     * @return the new body.
     */
    public Body buildBox(final Pair position, EffectType type) {
        setStaticDefPosition(position);
        Body body = world.createBody(staticDef);

        Fixture f = body.createFixture(unitShape, 0.0f);
        f.getFilterData().categoryBits = boxFilter.categoryBits;
        f.getFilterData().maskBits = boxFilter.maskBits;

        Optional<Effect> effect;
        if (type == BUFF) {
            effect = Optional.of(randomBuff());
        } else if (type == DEBUFF) {
            effect = Optional.of(randomDebuff());
        } else {
            effect = Optional.empty();
        }

        Box box = new Box(effect);
        box.setTexture(boxTexture);
        box.setSize(PPM, PPM);
        box.setOriginCenter();
        box.setPosition(body.getPosition().x, body.getPosition().y);

        body.setUserData(box);

        return body;
    }

    /**
     * Returns a newly created {@link Monster} and its enclosing {@link Body} at the specified position.
     * <br>
     * Monsters are {@link BodyType#DynamicBody dynamic bodies}
     * which are allowed to collide with all players, walls, boxes, bombs and boundaries.
     * Their size is always the same as the players' size;
     *
     * @param position the position of the new body.
     * @return the new body.
     */
    public Body buildMonster(final Pair position) {
        movingDef.position.set(position.col * PPM, position.row * PPM);
        Body body = world.createBody(movingDef);

        Fixture f = body.createFixture(movingFDef);
        f.getFilterData().categoryBits = monsterFilter.categoryBits;
        f.getFilterData().maskBits = monsterFilter.maskBits;

        Monster monster = new Monster();
        monster.setTexture(monsterTexture);
        monster.setSize(playerSize, playerSize);
        monster.setOriginCenter();

        body.setUserData(monster);

        return body;
    }

    /**
     * Returns a newly created {@link Body} at the specified position,
     * containing the {@link Effect} passed to this method.
     * <br>
     * Effects are {@link BodyType#StaticBody static bodies} which are allowed to collide with all players,
     * but are {@link Fixture#isSensor() sensors}, meaning that they only detect and report collisions,
     * so an interacting player's momentum is not reduced.
     *
     * @param position the position of the new body.
     * @return the new body.
     */
    public Body buildEffect(final Pair position, final Effect effect) {
        setStaticDefPosition(position, (PPM - effectSize) / 2);
        Body body = world.createBody(staticDef);

        Fixture f = body.createFixture(effectShape, 0.0f);
        f.getFilterData().categoryBits = effectFilter.categoryBits;
        f.getFilterData().maskBits = effectFilter.maskBits;
        f.setSensor(true);

        effect.setSize(effectSize, effectSize);
        effect.setOriginCenter();
        effect.setPosition(body.getPosition().x, body.getPosition().y);
        body.setUserData(effect);

        return body;
    }

    /**
     * Returns a newly created {@link Player} and its enclosing {@link Body} at the specified position.
     * <br>
     * A player's texture is determined by its <code>index</code>.
     * Collision filtering is set up using the <code>category</code> and <code>mask</code> parameters.
     * <br>
     * Players' bodies are not allowed to sleep for smooth collision detection.
     *
     * @param position position of the newly created body.
     * @param index    {@link Player#ind index} of the player.
     * @param category collision filtering {@link Filter#categoryBits category}.
     * @param mask     collision filtering {@link Filter#maskBits mask}.
     * @return the newly created body.
     */
    public Body buildPlayer(final Pair position, int index, short category, short mask) {
        movingDef.position.set(position.col * PPM + ((PPM - playerSize) / 2), position.row * PPM + ((PPM - playerSize) / 2));
        Body body = world.createBody(movingDef);
        body.setSleepingAllowed(false);

        Fixture f = body.createFixture(movingFDef);
        Filter filter = new Filter();
        filter.categoryBits = category;
        filter.maskBits = mask;
        f.setFilterData(filter);

        Player player = new Player(index);
        if (index == 0) {
            player.setTexture(player1Texture);
        } else if (index == 1) {
            player.setTexture(player2Texture);
        } else {
            player.setTexture(player3Texture);
        }
        player.setSize(playerSize, playerSize);
        player.setOriginCenter();

        body.setUserData(player);

        return body;
    }

    @Override
    public void dispose() {
        unitShape.dispose();
        movingShape.dispose();
    }

    /**
     * An <code>EffectType</code> constant describes what type of effect a box will contain.
     */
    public enum EffectType {
        /**
         * Positive effects.
         */
        BUFF,
        /**
         * Negative effects.
         */
        DEBUFF,
        /**
         * No effect.
         */
        NONE
    }
}
