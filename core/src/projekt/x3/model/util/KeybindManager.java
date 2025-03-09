package x3.model.util;

import com.badlogic.gdx.Input;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Manages the keybindings.
 * Stores, loads, changes the keybindings of the players in a list of maps.
 * Stores the keybindings in a file, so they are available after restarting the game.
 */
public final class KeybindManager {
    private static final Path path = Path.of("./.keybinds");
    private static List<Map<String, Integer>> mappings;

    private KeybindManager() {
    }

    /**
     * Returns the keybindings for a player.
     *
     * @param playerIndex the index of the player
     * @return the player's keybindings
     */
    public static Map<String, Integer> getKeybinds(int playerIndex) {
        if (mappings == null) {
            try {
                loadKeybinds();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new HashMap<>(mappings.get(playerIndex));
    }

    /**
     * Loads the keybindings into the mappings list either from the file in the assets, or calls loadDefault() to load default keybindings.
     *
     * @throws IOException if an I/O error occurs when reading from the file
     */
    public static void loadKeybinds() throws IOException {
        // initialize empty maps
        mappings = List.of(new HashMap<>(), new HashMap<>(), new HashMap<>());

        String[] keys = { "RIGHT", "DOWN", "UP", "LEFT", "BOMB" };

        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        // read all keybinds into list of strings
        List<String> lines = Files.readAllLines(path);
        if (lines.size() != 3) {
            loadDefault();
            saveKeybinds();
            return;
        }

        // iterate over lines
        for (int i = 0; i < lines.size(); i++) {
            // split each line at the commas
            Scanner sc = new Scanner(lines.get(i));
            sc.useDelimiter(",");
            int j = 0;
            while (sc.hasNextInt()) {
                // map the next keycode to the corresponding direction string in the corresponding map
                mappings.get(i).put(keys[j], sc.nextInt());
                j++;
            }
        }
    }

    /**
     * Sets the mappings list to its default state, containing the default keybindings.
     */
    private static void loadDefault() {
        Map<String, Integer> p1 = new HashMap<>(Map.of(
            "UP", Input.Keys.W,
            "RIGHT", Input.Keys.D,
            "DOWN", Input.Keys.S,
            "LEFT", Input.Keys.A,
            "BOMB", Input.Keys.R
        ));

        Map<String, Integer> p2 = new HashMap<>(Map.of(
            "UP", Input.Keys.U,
            "RIGHT", Input.Keys.J,
            "DOWN", Input.Keys.K,
            "LEFT", Input.Keys.H,
            "BOMB", Input.Keys.T
        ));

        Map<String, Integer> p3 = new HashMap<>(Map.of(
            "UP", Input.Keys.UP,
            "RIGHT", Input.Keys.RIGHT,
            "DOWN", Input.Keys.DOWN,
            "LEFT", Input.Keys.LEFT,
            "BOMB", Input.Keys.SLASH
        ));
        mappings = List.of(p1, p2, p3);
    }

    /**
     * Saves the mappings list to a file.
     * File is located at assets\.keybinds, each line represents a player.
     * The lines contain 5 keycodes separated by commas, the keybindings stored in order are:
     * Right,Down,Up,Left,Bomb
     */
    public static void saveKeybinds() {
        final StringBuilder sb = new StringBuilder();
        mappings.forEach(map -> {
            for (String key : Arrays.asList("RIGHT", "DOWN", "UP", "LEFT", "BOMB")) {
                if (map.containsKey(key)) {
                    sb.append(map.get(key)).append(",");
                }
            }
            sb.append("\n");
        });
        try {
            Files.write(path, sb.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns whether a given keycode is already mapped to any keybinding.
     *
     * @param keycode a keycode.
     * @return true if the keycode is already mapped to a keybinding
     */
    public static boolean containsKeycode(int keycode) {
        return mappings.get(0).containsValue(keycode) || mappings.get(1).containsValue(keycode) || mappings.get(2).containsValue(keycode);
    }

    /**
     * Sets a player's control to a value in the mappings list.
     *
     * @param playerIndex the index of the player.
     * @param keyName     the name of the Key, valid values: DOWN, RIGHT, UP, BOMB, LEFT
     * @param keyCode     a keycode.
     */
    public static void setKey(int playerIndex, String keyName, int keyCode) {
        mappings.get(playerIndex).replace(keyName, keyCode);
    }
}
