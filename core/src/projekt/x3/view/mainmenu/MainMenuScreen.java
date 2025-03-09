package x3.view.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import x3.DetonatorCircle;

/**
 * This class describes a {@link Screen} that the user first interacts with when the game starts.
 * <br>
 * A <code>MainMenuScreen</code> object allows navigation to other menus.
 * <br>
 * Since this screen's contents are static, UI elements are not stored and the UI is not rebuilt when
 * {@link Screen#show()} is called.
 */
public class MainMenuScreen implements Screen {
    private final FillViewport uiViewport;
    private final Stage uiStage;

    /**
     * Only constructor.
     * <br>
     * Initializes the UI elements, builds the layout and adds the button listeners.
     *
     * @param game the object containing the {@link Texture textures} and window size.
     */
    public MainMenuScreen(final DetonatorCircle game) {
        uiViewport = new FillViewport(game.initialWindowWidth, game.initialWindowHeight);
        uiStage = new Stage(uiViewport);

        Table root = new Table();
        root.setFillParent(true);
        uiStage.addActor(root);

        Image background = new Image(game.getTexture("mainbackground"));
        background.setFillParent(true);

        Table buttons = new Table();

        ImageButton play2Btn = new ImageButton(game.getDrawable("playas2"));
        play2Btn.getStyle().imageOver = game.getDrawable("playas2hover");

        ImageButton play3Btn = new ImageButton(game.getDrawable("playas3"));
        play3Btn.getStyle().imageOver = game.getDrawable("playas3hover");

        ImageButton controlsBtn = new ImageButton(game.getDrawable("controls"));
        controlsBtn.getStyle().imageOver = game.getDrawable("controlshover");

        ImageButton exitBtn = new ImageButton(game.getDrawable("exit"));
        exitBtn.getStyle().imageOver = game.getDrawable("exithover");

        root.addActor(background);

        buttons.defaults().width(150).height(40);
        buttons.row().padTop(30);

        buttons.add(play2Btn);

        buttons.row();
        buttons.add(play3Btn);
        buttons.row();
        buttons.add(controlsBtn);
        buttons.row();
        buttons.add(exitBtn);

        root.add(buttons);

        play2Btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setPlayerCount2();
                game.toGameSetup();
            }
        });

        play3Btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setPlayerCount3();
                game.toGameSetup();
            }
        });

        controlsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.toControlsMenu();
            }
        });

        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(uiStage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);
        uiViewport.apply();
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