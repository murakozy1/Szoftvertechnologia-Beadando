package x3.view.controlsmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import x3.DetonatorCircle;
import x3.model.game.objects.Player;
import x3.model.util.KeybindManager;

import java.io.IOException;

/**
 * This class describes a {@link Screen} that allows the user
 * to set the key bindings for each {@link Player player}'s actions.
 * <br>
 * This screen's layout is static so UI elements are not stored and the UI is not rebuilt when
 * {@link Screen#show()} is called. However, button label texts are refreshed when the screen is shown.
 */
public class ControlsMenuScreen implements Screen {
    private final FillViewport uiViewport;
    private final Stage uiStage;

    private final KeybindingGroup p1;
    private final KeybindingGroup p2;
    private final KeybindingGroup p3;
    /**
     * Indicates whether a button has been pressed and is waiting for key input.
     */
    public boolean buttonPressed = false;

    /**
     * Only constructor.
     * <br>
     * Initializes the UI elements, builds the layout and adds the button listeners.
     *
     * @param game the object containing the {@link Texture textures}, fonts and window size.
     */
    public ControlsMenuScreen(final DetonatorCircle game) {
        p1 = new KeybindingGroup(0, this);
        p2 = new KeybindingGroup(1, this);
        p3 = new KeybindingGroup(2, this);

        uiViewport = new FillViewport(game.initialWindowWidth, game.initialWindowHeight);
        uiStage = new Stage(uiViewport);
        // uiStage.setDebugAll(true);

        Table root = new Table();
        root.setFillParent(true);
        uiStage.addActor(root);

        Image background = new Image(game.getTexture("background"));
        background.setFillParent(true);
        root.addActor(background);

        LabelStyle style = new LabelStyle(game.font48, Color.BLACK);
        Table editor = new Table();

        Label upLabel = new Label("Up", style);
        upLabel.setFontScale(0.5f);

        Label leftLabel = new Label("Left", style);
        leftLabel.setFontScale(0.5f);

        Label rightLabel = new Label("Right", style);
        rightLabel.setFontScale(0.5f);

        Label downLabel = new Label("Down", style);
        downLabel.setFontScale(0.5f);

        Label bombLabel = new Label("Bomb", style);
        bombLabel.setFontScale(0.5f);

        Table nav = new Table();

        ImageButton backBtn = new ImageButton(game.getDrawable("back"));
        backBtn.getStyle().imageOver = game.getDrawable("backhover");

        ImageButton saveBtn = new ImageButton(game.getDrawable("save"));
        saveBtn.getStyle().imageOver = game.getDrawable("savehover");

        editor.defaults().space(20, 10, 20, 10);
        editor.columnDefaults(0).width(70);
        editor.columnDefaults(1).uniformX();
        editor.columnDefaults(2).uniformX();
        editor.columnDefaults(3).uniformX();
        editor.columnDefaults(4).width(70);

        editor.row().width(40).height(40);
        editor.add();
        editor.add(new Image(game.getDrawable("player1")));
        editor.add(new Image(game.getDrawable("player2")));
        editor.add(new Image(game.getDrawable("player3")));
        editor.row();
        editor.add(upLabel);
        editor.add(p1.upKeyButton);
        editor.add(p2.upKeyButton);
        editor.add(p3.upKeyButton);
        editor.row();
        editor.add(leftLabel);
        editor.add(p1.leftKeyButton);
        editor.add(p2.leftKeyButton);
        editor.add(p3.leftKeyButton);
        editor.row();
        editor.add(rightLabel);
        editor.add(p1.rightKeyButton);
        editor.add(p2.rightKeyButton);
        editor.add(p3.rightKeyButton);
        editor.row();
        editor.add(downLabel);
        editor.add(p1.downKeyButton);
        editor.add(p2.downKeyButton);
        editor.add(p3.downKeyButton);
        editor.row();
        editor.add(bombLabel);
        editor.add(p1.bombKeyButton);
        editor.add(p2.bombKeyButton);
        editor.add(p3.bombKeyButton);
        editor.add();

        root.add(editor);

        nav.defaults().width(150).height(40).space(0, 10, 0, 10);
        nav.row().padTop(20);
        nav.add(backBtn);
        nav.add(saveBtn);

        root.row();
        root.add(nav);

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    KeybindManager.loadKeybinds();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                game.toMainMenu();
            }
        });

        saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                KeybindManager.saveKeybinds();
                p1.refreshButtons();
                p2.refreshButtons();
                p3.refreshButtons();
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(uiStage);
        p1.refreshButtons();
        p2.refreshButtons();
        p3.refreshButtons();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);
        uiViewport.apply();
        uiStage.act(delta);
        uiStage.draw();
        if (!buttonPressed) {
            Gdx.input.setInputProcessor(uiStage);
        }
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
