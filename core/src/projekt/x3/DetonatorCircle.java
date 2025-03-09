package x3;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import x3.model.game.objects.Explosion;
import x3.model.map.GameMap;
import x3.model.map.Map1;
import x3.model.map.Map2;
import x3.model.map.Map3;
import x3.view.controlsmenu.ControlsMenuScreen;
import x3.view.gamesetup.GameSetupScreen;
import x3.view.mainmenu.MainMenuScreen;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This object is responsible for managing the assets of the game
 * and displaying the game itself.
 * <br>
 * General information on game state, players and UI screens are stored
 * and managed here. The object also manages navigation between screens,
 * allows texture generation and caching, and has methods for drawing to
 * the screen through the internal SpriteBatch instance.
 */
public class DetonatorCircle extends Game {
    /**
     * The horizontal size of the window as specified in DesktopLauncher.
     */
    public final int initialWindowWidth;
    /**
     * The vertical size of the window as specified in DesktopLauncher.
     */
    public final int initialWindowHeight;
    /**
     * A general list containing all instances of playable maps in the game.
     */
    public final List<GameMap> maps;
    /**
     * An array containing the names of each player.
     * <br>
     * This array has a size of 3 at all times. Depending on player count,
     * the last element (index of 2) may be ignored.
     * <br>
     * Default values are: Player1, Player2, Player3
     */
    public final String[] playerNames = { "Player1", "Player2", "Player3" };
    private final List<SimpleEntry<FileHandle, FileHandle>> explosionParticles;
    /**
     * Font of pixel size 24.
     */
    public BitmapFont font24;
    /**
     * Font of pixel size 32.
     */
    public BitmapFont font32;
    /**
     * Font of pixel size 48.
     */
    public BitmapFont font48;
    /**
     * The number of rounds remaining in the currently running game.
     */
    public int remainingRounds;
    /**
     * The total number of rounds in the currently running game.
     */
    public int roundCount;
    /**
     * An array containing the nummber of wins for each player.
     * <br>
     * This array has a size of 3 at all times. Depending on player count,
     * the last element (index of 2) may be ignored.
     */
    public int[] wonRounds;
    private SpriteBatch batch;
    private TextureAtlas textureAtlas;
    private Map<String, Texture> textureCache;
    private int pCount;
    private MainMenuScreen mainMenuScreen;
    private ControlsMenuScreen controlsMenuScreen;
    private GameSetupScreen gameSetupScreen;

    /**
     * Only constructor.
     * <br>
     * Stores the provided window dimensions for rendering the UIs of the game,
     * and prepares the GameMap instances.
     *
     * @param windowWidth  the initial width of the window.
     * @param windowHeight the initial height of the window.
     */
    public DetonatorCircle(int windowWidth, int windowHeight) {
        initialWindowWidth = windowWidth;
        initialWindowHeight = windowHeight;
        explosionParticles = new ArrayList<>();
        maps = List.of(new Map1(), new Map2(), new Map3());
    }

    /**
     * Sets the screen of this Game instance to the stored instance of the MainMenuScreen class.
     */
    public void toMainMenu() {
        setScreen(mainMenuScreen);
    }

    /**
     * Sets the screen of this Game instance to the stored instance of the ControlsMenuScreen class.
     */
    public void toControlsMenu() {
        setScreen(controlsMenuScreen);
    }

    /**
     * Sets the screen of this Game instance to the stored instance of the GameSetupScreen class.
     */
    public void toGameSetup() {
        setScreen(gameSetupScreen);
    }

    /**
     * Returns the texture associated with the provided name.
     * <br>
     * Textures are stored in an internal mapping where <code>name</code> is the key, and the returned value
     * is the associated Texture. Textures are cached when first requested.
     *
     * @param name the key associated with the desired Texture object in the internal mapping.
     * @return the requested Texture.
     */
    public Texture getTexture(String name) {
        if (!textureCache.containsKey(name)) {
            textureCache.put(name, textureAtlas.createSprite(name).getTexture());
        }
        return textureCache.get(name);
    }

    /**
     * Returs the Drawable of the texture associated with the provided name.
     * <br>
     * This method uses the getTexture() instance method internally, meaning that
     * the requested Drawable's Texture is cached for later used. The Drawable is generated
     * from the specified Texture dynamically, but is not cached (since it has no real performance benefit).
     *
     * @param name the key associated with the Texture object.
     * @return the Drawable of the specified Texture.
     */
    public Drawable getDrawable(String name) {
        return new TextureRegionDrawable(getTexture(name));
    }

    /**
     * Draws a rectangle to the batch.
     * <br>
     * This method hides the internal SpriteBatch object's draw() method for simpler access.
     *
     * @param texture the texture to be drawn.
     * @param x       X coordinate of the bottom left corner of the rectangle.
     * @param y       Y coordinate of the bottom left corner of the rectangle.
     * @param width   width of the rectangle.
     * @param height  height of the rectangle.
     */
    public void draw(Texture texture, float x, float y, float width, float height) {
        batch.draw(texture, x, y, width, height);
    }

    /**
     * Draws the sprite as a rectangle to the batch.
     * <br>
     * Rectangle position and size are all queried from the Sprite argument.
     *
     * @param sprite the sprite to draw.
     */
    public void draw(Sprite sprite) {
        batch.draw(sprite.getTexture(), sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }

    /**
     * Draws a ParticleEffect to the batch.
     *
     * @param effect the ParticleEffect to draw.
     */
    public void draw(ParticleEffect effect) {
        effect.draw(batch);
    }

    /**
     * Encapsulates the SpriteBatch's begin() method.
     */
    public void beginBatch() {
        batch.begin();
    }

    /**
     * Encapsulates the SpriteBatch's end() method.
     */
    public void endBatch() {
        batch.end();
    }

    /**
     * Encapsulates the SpriteBatch's setProjectionMatrix() method.
     *
     * @param combined the projection matrix.
     */
    public void setProjectionMatrix(Matrix4 combined) {
        batch.setProjectionMatrix(combined);
    }

    /**
     * Returns the player count selected by the user at runtime.
     * Player count should always be either 2 or 3.
     *
     * @return the number of players specified for the next game run.
     */
    public int getPlayerCount() {
        return pCount;
    }

    /**
     * Returns whether the player count for the current game run is 2.
     *
     * @return true if the player count is 2.
     */
    public boolean is2Player() {
        return pCount == 2;
    }

    /**
     * Returns whether the player count for the current game run is 3.
     *
     * @return true if the player count is 3.
     */
    public boolean is3Player() {
        return pCount == 3;
    }

    /**
     * Sets the player count to 2.
     */
    public void setPlayerCount2() {
        pCount = 2;
    }

    /**
     * Sets the player count to 3.
     */
    public void setPlayerCount3() {
        pCount = 3;
    }

    private SimpleEntry<String, String> e(String name, String path) {
        return new SimpleEntry<>(name, path);
    }

    /**
     * Returns the paths of the particle effects that are supposed to be shown by {@link Explosion} objects.
     * <br>
     * Keys are the particle files, values are the image files for the effects.
     * @return the list of particle effects.
     */
    public List<SimpleEntry<FileHandle, FileHandle>> getExplosionParticles() {
        return explosionParticles.stream().toList();
    }

    /**
     * Initializes the game instance.
     * <br>
     * This method runs when all LibGDX contexts are initialized properly.
     * Initializes the internal SpriteBatch instance, the fonts, the TextureAtlas and the texture cache.
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        explosionParticles.addAll(List.of(
            new SimpleEntry<>(Gdx.files.internal("game/particles/smoke.p"), Gdx.files.internal("game/particles")),
            new SimpleEntry<>(Gdx.files.internal("game/particles/fire.p"), Gdx.files.internal("game/particles"))
        ));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("game/fonts/AvrileSansUI-Regular.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 24;
        font24 = generator.generateFont(parameter);
        parameter.size = 32;
        font32 = generator.generateFont(parameter);
        parameter.size = 48;
        font48 = generator.generateFont(parameter);
        generator.dispose();

        Map<String, String> nameToPathMap = Map.ofEntries(
            e("background", "common/background.png"),
            e("play", "common/play.png"),
            e("playhover", "common/playhover.png"),
            e("save", "common/save.png"),
            e("savehover", "common/savehover.png"),
            e("back", "common/back.png"),
            e("backhover", "common/backhover.png"),
            e("mainbackground", "mainmenu/background.png"),
            e("playas2", "mainmenu/playas2.png"),
            e("playas2hover", "mainmenu/playas2hover.png"),
            e("playas3", "mainmenu/playas3.png"),
            e("playas3hover", "mainmenu/playas3hover.png"),
            e("controls", "mainmenu/controls.png"),
            e("controlshover", "mainmenu/controlshover.png"),
            e("exit", "mainmenu/exit.png"),
            e("exithover", "mainmenu/exithover.png"),
            e("uplabel", "controlsmenu/up.png"),
            e("rightlabel", "controlsmenu/right.png"),
            e("downlabel", "controlsmenu/down.png"),
            e("leftlabel", "controlsmenu/left.png"),
            e("bomblabel", "controlsmenu/bomb.png"),
            e("more", "gamesetup/more.png"),
            e("morecant", "gamesetup/morecant.png"),
            e("morehover", "gamesetup/morehover.png"),
            e("less", "gamesetup/less.png"),
            e("lesscant", "gamesetup/lesscant.png"),
            e("lesshover", "gamesetup/lesshover.png"),
            e("1", "gamesetup/1.png"),
            e("2", "gamesetup/2.png"),
            e("3", "gamesetup/3.png"),
            e("4", "gamesetup/4.png"),
            e("5", "gamesetup/5.png"),
            e("map1", "mapselect/map1.png"),
            e("map2", "mapselect/map2.png"),
            e("map3", "mapselect/map3.png"),
            e("player1", "game/p1.png"),
            e("player2", "game/p2.png"),
            e("player3", "game/p3.png"),
            e("bomb", "game/bomb.png"),
            e("bomb2", "game/bomb-state-2.png"),
            e("bomb3", "game/bomb-state-3.png"),
            e("box", "game/static/box.png"),
            e("wall", "game/static/wall.png"),
            e("tile", "game/static/tile.png"),
            e("boundary", "game/static/boundary.png"),
            e("monster", "game/monster.png"),
            e("biggerbomb", "game/effects/biggerbomb.png"),
            e("bonusbomb", "game/effects/bonusbomb.png"),
            e("forcedbombs", "game/effects/forcedbombs.png"),
            e("nobombs", "game/effects/nobombs.png"),
            e("slow", "game/effects/slow.png"),
            e("smallbomb", "game/effects/smallbomb.png")
        );
        textureAtlas = new TextureAtlas();
        textureCache = new HashMap<>();
        nameToPathMap.forEach((name, path) -> textureAtlas.addRegion(name, new TextureRegion(new Texture(Gdx.files.internal(path)))));

        mainMenuScreen = new MainMenuScreen(this);
        controlsMenuScreen = new ControlsMenuScreen(this);
        gameSetupScreen = new GameSetupScreen(this);
        toMainMenu();
    }

    @Override
    public void dispose() {
        batch.dispose();
        textureAtlas.dispose();
        textureCache.forEach((name, texture) -> texture.dispose());
        font24.dispose();
        font32.dispose();
        font48.dispose();
        mainMenuScreen.dispose();
        controlsMenuScreen.dispose();
        gameSetupScreen.dispose();
    }
}
