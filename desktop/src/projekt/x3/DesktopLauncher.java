package projekt.x3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.physics.box2d.Box2D;
import x3.DetonatorCircle;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 450;

    public static void main (String[] arg) {
        Box2D.init();
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setTitle("Detonator Circle");
        config.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
        new Lwjgl3Application(new DetonatorCircle(WINDOW_WIDTH, WINDOW_HEIGHT), config);
    }
}
