package x3.view.mapselect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import x3.DetonatorCircle;
import x3.model.map.GameMap;
import x3.view.game.GameScreen;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A screen that handles map selection.
 * Stores the map options in a list, manages UI elements.
 * Handles map selection with buttons.
 */
public class MapSelectScreen implements Screen {
    private static final Random RANDOM = new Random();
    private final Stage uiStage;
    private final DetonatorCircle game;
    private final List<MapOption> maps;
    private final Table root;
    private final Image background;
    private final Table ui;
    private final MapOption map1;
    private final MapOption map2;
    private final MapOption map3;
    private final ImageButton backBtn;
    private final ImageButton nextBtn;

    /**
     * Creates the MapSelectScreen.
     * Initialises the UI elements, buttons and button listeners.
     * @param game the object responsible for managing the game
     */
    public MapSelectScreen(final DetonatorCircle game) {
        this.game = game;

        FillViewport uiViewport = new FillViewport(game.initialWindowWidth, game.initialWindowHeight);
        uiStage = new Stage(uiViewport);
        // uiStage.setDebugAll(true);
        Gdx.input.setInputProcessor(uiStage);

        root = new Table();
        root.setFillParent(true);
        uiStage.addActor(root);

        background = new Image(game.getTexture("background"));
        background.setFillParent(true);

        ui = new Table();

        map1 = new MapOption(game.getTexture("map1"));
        map2 = new MapOption(game.getTexture("map2"));
        map3 = new MapOption(game.getTexture("map3"));
        maps = List.of(map1, map2, map3);

        backBtn = new ImageButton(game.getDrawable("back"));
        backBtn.getStyle().imageOver = game.getDrawable("backhover");

        nextBtn = new ImageButton(game.getDrawable("play"));
        nextBtn.getStyle().imageOver = game.getDrawable("playhover");

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.toMainMenu();
                dispose();
            }
        });

        nextBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startGame();
            }
        });
    }

    /**
     * Initializes the {@link GameScreen} instance and sets it as the game's screen.
     * <br>
     * This method loops over the maps displayed in the UI and filters based on which ones are selected.
     * If none are selected, a random map is loaded from the original list.
     * If at least one is selected, they are shuffled, and the first among them is loaded.
     */
    private void startGame() {
        List<Integer> selected = IntStream
                .range(0, maps.size())
                .filter(idx -> maps.get(idx).isSelected())
                .boxed()
                .collect(Collectors.toList());
        GameMap map;
        if (selected.isEmpty()) {
            map = game.maps.get(RANDOM.nextInt(0, maps.size()));
        } else {
            Collections.shuffle(selected);
            map = game.maps.get(selected.get(0));
        }
        game.setScreen(new GameScreen(game, map));
        dispose();
    }

    @Override
    public void show() {
        root.clearChildren();

        root.addActor(background);

        ui.row().size(180).pad(0, 10, 30, 10).colspan(2).uniformX();
        ui.add(map1);
        ui.add(map2);
        ui.add(map3);

        ui.row().width(150).height(40).space(0, 10, 0, 10).colspan(3).uniformX();
        ui.add(backBtn).right();
        ui.add(nextBtn).left();

        root.add(ui);
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
