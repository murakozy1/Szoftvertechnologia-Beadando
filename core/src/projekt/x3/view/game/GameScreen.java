package x3.view.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import x3.DetonatorCircle;
import x3.model.game.GameEngine;
import x3.model.game.objects.Player;
import x3.model.map.GameMap;
import x3.model.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * The screen of the game.
 * Draws the players, field objects and the UI.
 * Manages the UI, updates the time on the counter, shows the current score.
 * At the end of a round displays a round ending screen, revealing who won the round. Waits for users to start new round.
 * At the end of the game displays a game ending screen.
 */
public class GameScreen implements Screen {
    private final DetonatorCircle game;
    private final GameMap map;
    private final Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
    private final ExtendViewport gameView;
    private final ExtendViewport nameView;
    private final FillViewport overlayView;

    private final Stage playerNameStage;
    private final Stage overlayStage;

    private final GameEngine gameEngine;
    private final float PPM = 5;
    private final Texture boundaryTexture;
    private final Texture tileTexture;


    private final List<Container<Label>> playerNameLabels;
    private final float nameLabelScale = 0.05f;
    private final float nameLabelOffsetMultiplier = 1.2f;
    private final float uiLabelFontScale = 0.4f;

    private final Table ui;
    private final TimerLabel shrinkTimerLabel;

    private final List<Pair> tilePositions;

    private long endTime;
    private boolean waiting;

    /**
     * Creates the game screen.
     * Initialises UI elements, cameras. Creates a GameEngine for the game.
     *
     * @param game the object responsible for managing the game
     * @param map  the map the game is played on
     */
    public GameScreen(final DetonatorCircle game, final GameMap map) {
        this.waiting = false;
        this.game = game;
        this.map = map;
        this.gameEngine = new GameEngine(game, map, PPM);
        boundaryTexture = game.getTexture("boundary");
        tileTexture = game.getTexture("tile");

        gameView = new ExtendViewport(map.getMapWidth() * PPM, map.getMapHeight() * PPM);
        nameView = new ExtendViewport(map.getMapWidth() * PPM, map.getMapHeight() * PPM);

        Vector3 center = new Vector3(gameView.getMinWorldWidth() / 2, gameView.getMinWorldHeight() / 2, 0);
        gameView.getCamera().position.set(center.cpy());
        nameView.getCamera().position.set(center.cpy());

        playerNameStage = new Stage(nameView);
        playerNameStage.setDebugAll(true);
        LabelStyle style = new LabelStyle(game.font24, Color.BLACK);

        playerNameLabels = List.of(
            new Container<>(new Label(game.playerNames[0], style)),
            new Container<>(new Label(game.playerNames[1], style)),
            new Container<>(new Label(game.playerNames[2], style))
        );
        playerNameLabels.forEach(cont -> {
            cont.setTransform(true);
            cont.center();
            cont.center().setScale(nameLabelScale);
            Label label = cont.getActor();
            label.setAlignment(Align.center);
            Pixmap labelColor = new Pixmap((int) label.getWidth(), (int) label.getHeight(), Pixmap.Format.RGBA8888);
            labelColor.setColor(Color.argb8888(0.5f, 0.44f, 0.45f, 0.49f));
            labelColor.fill();
            label.getStyle().background = new Image(new Texture(labelColor)).getDrawable();
        });

        overlayView = new FillViewport(game.initialWindowWidth, game.initialWindowHeight);

        overlayStage = new Stage(overlayView);

        Table root = new Table();
        root.setFillParent(true);
        root.top();
        overlayStage.addActor(root);

        ui = new Table();
        root.add(ui);
        style = new LabelStyle(game.font48, Color.BLACK);
        Label roundLabel = new Label("Round #" + (game.roundCount - game.remainingRounds), style);
        roundLabel.setFontScale(uiLabelFontScale);
        shrinkTimerLabel = new TimerLabel("Next shrink in ", "s", style);
        shrinkTimerLabel.setValue("0.0");
        shrinkTimerLabel.setFontScale(uiLabelFontScale);
        Label p1Wins = new Label(game.playerNames[0] + ": " + game.wonRounds[0], style);
        p1Wins.setFontScale(uiLabelFontScale);
        Label p2Wins = new Label(game.playerNames[1] + ": " + game.wonRounds[1], style);
        p2Wins.setFontScale(uiLabelFontScale);
        Label p3Wins = new Label(game.playerNames[2] + ": " + game.wonRounds[2], style);
        p3Wins.setFontScale(uiLabelFontScale);

        ui.defaults().pad(10, 20, 10, 20);
        ui.add(roundLabel);
        ui.add(shrinkTimerLabel);
        ui.add(p1Wins).uniformX();
        ui.add(p2Wins).uniformX();
        if (game.is3Player()) {
            ui.add(p3Wins).uniformX();
        }

        tilePositions = new ArrayList<>();
        for (int x = 0; x < map.getMapWidth(); ++x) {
            for (int y = 0; y < map.getMapHeight(); ++y) {
                tilePositions.add(Pair.of(x, y));
            }
        }
    }

    private void drawPlayer(Player player) {
        Container<Label> label = playerNameLabels.get(player.ind);
        float lbWidth = label.getActor().getWidth() * nameLabelScale;
        float sub = lbWidth - player.getWidth();
        Vector2 projectedFromGame = gameView.project(new Vector2(player.getX(), player.getY()));
        projectedFromGame.y = Gdx.graphics.getHeight() - projectedFromGame.y;
        Vector2 labelPos = nameView.unproject(projectedFromGame);
        label.setPosition(labelPos.x - (sub / 2) + (lbWidth / 2), labelPos.y + nameLabelOffsetMultiplier * PPM);
        playerNameStage.addActor(label);
        game.draw(player);
    }

    private void drawGame() {
        gameView.apply();
        game.setProjectionMatrix(gameView.getCamera().combined);
        game.beginBatch();

        tilePositions
            .forEach(pair -> game.draw(tileTexture, pair.col * PPM, pair.row * PPM, PPM, PPM));
        gameEngine.getFieldObjects()
            .forEach(game::draw);
        gameEngine.getEffects()
            .forEach(game::draw);
        gameEngine.getPlayers()
            .forEach(this::drawPlayer);
        gameEngine.getMonsters()
            .forEach(game::draw);
        gameEngine.getBoundaryAffectedCells()
            .forEach(pair -> game.draw(boundaryTexture, pair.col * PPM, pair.row * PPM, PPM, PPM));
        gameEngine.getBombs()
            .forEach(game::draw);
        gameEngine.getFlames()
            .forEach(game::draw);

        game.endBatch();
    }

    private void drawUI() {
        nameView.apply();
        playerNameStage.act();
        playerNameStage.draw();
        playerNameLabels.forEach(Actor::remove);
        overlayView.apply();
        overlayStage.act();
        overlayStage.draw();
    }

    private void manageRoundEnd() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.ENTER) {
                    if (game.remainingRounds + 1 == 0) {
                        game.toMainMenu();
                    } else {
                        map.shuffle();
                        game.setScreen((new GameScreen(game, map)));
                    }
                }
                return true;
            }
        });
        game.remainingRounds--;
    }

    private String outcome() {
        if (game.remainingRounds > 0) {
            if (gameEngine.getPlayers().findAny().isEmpty()) {
                return "ROUND DRAW!";
            }
            Player winner = gameEngine.getPlayers().findFirst().orElseThrow();
            return game.playerNames[winner.ind] + " WON THE ROUND!";
        }

        ArrayList<String> winners = new ArrayList<>();
        int maxWins = 0;
        for (int i = 0; i < 3; ++i) {
            if (game.wonRounds[i] >= maxWins) {
                if (game.wonRounds[i] > maxWins) {
                    maxWins = game.wonRounds[i];
                    winners.clear();
                }
                winners.add(game.playerNames[i]);
            }
        }

        return switch (winners.size()) {
            case 1 -> winners.get(0) + " WON WITH ";
            case 2 -> winners.get(0) + ", " + winners.get(1) + " TIED WITH ";
            default -> "EVERYONE TIED WITH ";
        } + maxWins + " WIN" + (maxWins != 1 ? "S" : "") + "!";
    }

    private void endGame() {
        String endGameText = outcome();
        LabelStyle style = new LabelStyle(game.font48, Color.BLACK);
        Label result = new Label(endGameText, style);
        result.setFontScale(uiLabelFontScale);
        Label prompt = new Label("Press ENTER to continue.", style);
        prompt.setFontScale(uiLabelFontScale);

        ui.row();
        ui.add(result).colspan(2 + game.getPlayerCount());
        ui.row();
        ui.add(prompt).colspan(2 + game.getPlayerCount());

        manageRoundEnd();
    }

    @Override
    public void show() {
        debugRenderer.setDrawBodies(true);
        debugRenderer.setDrawContacts(true);
        debugRenderer.setDrawVelocities(true);
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);

        long timePassedSinceEnd = System.currentTimeMillis() - endTime;
        boolean gameInProgress = !gameEngine.isGameEnded() || timePassedSinceEnd < 2000;
        if (!gameEngine.isGameEnded()) {
            endTime = System.currentTimeMillis();
        }
        if (gameInProgress) {
            gameEngine.step(delta);
        }
        if (!waiting && gameEngine.isGameEnded() && timePassedSinceEnd > 2000) {
            endGame();
            waiting = true;
        }

        drawGame();
        if (gameInProgress) {
            if (!gameEngine.isAtMaxShrink()) {
                shrinkTimerLabel.setValue(String.format("%.2f", gameEngine.getSecondsUntilShrink()));
            } else {
                shrinkTimerLabel.setValue("-.--");
            }
        }
        // debugRenderer.render(gameEngine.getWorld(), gameView.getCamera().combined);
        drawUI();
    }

    @Override
    public void resize(int width, int height) {
        gameView.update(width, height);
        nameView.update(width, height);
        overlayView.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        gameEngine.dispose();
        playerNameStage.dispose();
        overlayStage.dispose();
    }
}
