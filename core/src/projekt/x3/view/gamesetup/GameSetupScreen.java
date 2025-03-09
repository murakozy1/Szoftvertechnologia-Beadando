package x3.view.gamesetup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import x3.DetonatorCircle;
import x3.model.game.objects.Player;
import x3.view.mapselect.MapSelectScreen;

import java.util.function.BiConsumer;

/**
 * This class describes a {@link Screen} that allows the user to set the names of the {@link Player Players}
 * and the number of rounds that will be played.
 * <br>
 * UI elements are stored for later used and the UI is rebuilt when {@link Screen#show()} is called.
 * UI elements are dynamically added to the UI based on {@link DetonatorCircle#getPlayerCount() player count} (2 or 3).
 */
public class GameSetupScreen implements Screen {
    private final DetonatorCircle game;
    private final Stage uiStage;
    private final Table root;
    private final Image background;
    private final Table nameInput;
    private final TextField name1Field;
    private final TextField name2Field;
    private final TextField name3Field;
    private final Table nav;
    private final ImageButton lessBtn;
    private final Image roundCountImg;
    private final ImageButton moreBtn;
    private final ImageButton backBtn;
    private final ImageButton nextBtn;
    private int roundCount = 1;

    /**
     * Only constructor.
     * <br>
     * This constructor initializes all UI elements and button listeners.
     *
     * @param game the object containing the {@link Texture textures} and window size.
     */
    public GameSetupScreen(final DetonatorCircle game) {
        this.game = game;
        Skin skin = new Skin(Gdx.files.internal("gamesetup/metal-ui.json"));

        FillViewport uiViewport = new FillViewport(game.initialWindowWidth, game.initialWindowHeight);
        uiStage = new Stage(uiViewport);
        // uiStage.setDebugAll(true);

        root = new Table();
        root.setFillParent(true);
        uiStage.addActor(root);

        background = new Image(game.getTexture("background"));
        background.setFillParent(true);

        nameInput = new Table();
        name1Field = new TextField("Player 1", skin);
        name2Field = new TextField("Player 2", skin);
        name3Field = new TextField("Player 3", skin);

        nav = new Table();

        lessBtn = new ImageButton(game.getDrawable("less"));
        lessBtn.getStyle().imageOver = game.getDrawable("lesshover");
        lessBtn.getStyle().imageDisabled = game.getDrawable("lesscant");
        lessBtn.setDisabled(true);

        roundCountImg = new Image();
        roundCountImg.setDrawable(game.getDrawable("1"));

        moreBtn = new ImageButton(game.getDrawable("more"));
        moreBtn.getStyle().imageOver = game.getDrawable("morehover");
        moreBtn.getStyle().imageDisabled = game.getDrawable("morecant");

        backBtn = new ImageButton(game.getDrawable("back"));
        backBtn.getStyle().imageOver = game.getDrawable("backhover");

        nextBtn = new ImageButton(game.getDrawable("play"));
        nextBtn.getStyle().imageOver = game.getDrawable("playhover");

        BiConsumer<Integer, Integer> updateRoundCount = (limit, off) -> {
            if (roundCount == limit) {
                return;
            }
            roundCount += off;
            updateRoundCountImg(roundCountImg, game);
            moreBtn.setDisabled(roundCount == 5);
            lessBtn.setDisabled(roundCount == 1);
        };

        moreBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateRoundCount.accept(5, 1);
            }
        });

        lessBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateRoundCount.accept(1, -1);
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.toMainMenu();
            }
        });

        nextBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!name1Field.getText().isEmpty()) {
                    game.playerNames[0] = name1Field.getText();
                }
                if (!name2Field.getText().isEmpty()) {
                    game.playerNames[1] = name2Field.getText();
                }
                if (!name3Field.getText().isEmpty()) {
                    game.playerNames[2] = name3Field.getText();
                }
                game.roundCount = roundCount;
                game.remainingRounds = roundCount - 1;
                game.wonRounds = new int[] { 0, 0, 0 };
                game.setScreen(new MapSelectScreen(game));
            }
        });
    }

    private void updateRoundCountImg(Image roundCountImg, DetonatorCircle game) {
        roundCountImg.setDrawable(game.getDrawable(String.valueOf(roundCount)));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(uiStage);

        root.clearChildren();
        nameInput.clearChildren();
        nav.clearChildren();

        root.addActor(background);

        nameInput.defaults().space(20);
        nameInput.add(name1Field);
        nameInput.add(name2Field);
        if (game.is3Player()) {
            nameInput.add(name3Field);
        }

        nameInput.row().size(50);
        nameInput.add(new Image(game.getDrawable("player1")));
        nameInput.add(new Image(game.getDrawable("player2")));
        if (game.is3Player()) {
            nameInput.add(new Image(game.getDrawable("player3")));
        }

        root.add(nameInput);
        root.row();

        nav.row().uniformX().space(0, 20, 0, 20).padTop(60);
        nav.add(lessBtn).size(50);
        nav.add(roundCountImg).size(70).colspan(2);
        nav.add(moreBtn).size(50);
        nav.row().width(150).height(40).space(0, 10, 0, 10).padTop(20);
        nav.add(backBtn).colspan(2);
        nav.add(nextBtn).colspan(2);
        root.add(nav);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);
        uiStage.getViewport().apply();
        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
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
        uiStage.dispose();
    }
}
