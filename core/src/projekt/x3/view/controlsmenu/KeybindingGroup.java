package x3.view.controlsmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import x3.model.game.objects.Player;
import x3.model.util.KeybindManager;

import java.util.Map;

/**
 * A <code>KeybindingGroud</code> contains buttons for editing a given {@link Player Player's} keybindings
 * and saving them using the {@link KeybindManager}.
 */
public class KeybindingGroup extends VerticalGroup {
    /**
     * The button that allows the user to set this player's key binding for upwards movement.
     */
    public final TextButton upKeyButton;
    /**
     * The button that allows the user to set this player's key binding for right movement.
     */
    public final TextButton rightKeyButton;
    /**
     * The button that allows the user to set this player's key binding for downwards movement.
     */
    public final TextButton downKeyButton;
    /**
     * The button that allows the user to set this player's key binding for left movement.
     */
    public final TextButton leftKeyButton;
    /**
     * The button that allows the user to set this player's key binding for placing bombs.
     */
    public final TextButton bombKeyButton;
    private final int playerCode;

    /**
     * Only constructor.
     * <br>
     * Creates the buttons for the owner's keybindings and adds the button listeners to each.
     *
     * @param playerCode the index of the {@link Player} whose buttons are stored. [0-2]
     * @param parent     the {@link Screen} that contains this object and tracks button pressing status.
     */
    public KeybindingGroup(int playerCode, ControlsMenuScreen parent) {
        this.playerCode = playerCode;
        Map<String, Integer> map = KeybindManager.getKeybinds(playerCode);
        Skin skin = new Skin(Gdx.files.internal("gamesetup/metal-ui.json"));
        upKeyButton = new TextButton(Input.Keys.toString(map.get("UP")), skin);
        leftKeyButton = new TextButton(Input.Keys.toString(map.get("LEFT")), skin);
        downKeyButton = new TextButton(Input.Keys.toString(map.get("DOWN")), skin);
        rightKeyButton = new TextButton(Input.Keys.toString(map.get("RIGHT")), skin);
        bombKeyButton = new TextButton(Input.Keys.toString(map.get("BOMB")), skin);

        upKeyButton.addListener(new KeybindingSetterListener(playerCode, parent, upKeyButton, "UP"));
        leftKeyButton.addListener(new KeybindingSetterListener(playerCode, parent, leftKeyButton, "LEFT"));
        downKeyButton.addListener(new KeybindingSetterListener(playerCode, parent, downKeyButton, "DOWN"));
        rightKeyButton.addListener(new KeybindingSetterListener(playerCode, parent, rightKeyButton, "RIGHT"));
        bombKeyButton.addListener(new KeybindingSetterListener(playerCode, parent, bombKeyButton, "BOMB"));
    }

    /**
     * Refreshes the text content of each button by pulling the keybindings from {@link KeybindManager}.
     */
    public void refreshButtons() {
        Map<String, Integer> map = KeybindManager.getKeybinds(playerCode);
        upKeyButton.setText(Input.Keys.toString(map.get("UP")));
        downKeyButton.setText(Input.Keys.toString(map.get("DOWN")));
        leftKeyButton.setText(Input.Keys.toString(map.get("LEFT")));
        rightKeyButton.setText(Input.Keys.toString(map.get("RIGHT")));
        bombKeyButton.setText(Input.Keys.toString(map.get("BOMB")));
    }
}
