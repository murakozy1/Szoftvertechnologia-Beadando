package x3.view.controlsmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import x3.model.game.objects.Player;
import x3.model.util.KeybindManager;

/**
 * This listener is used by buttons of a {@link KeybindingGroup} object to display keycodes
 * and save changes.
 */
public class KeybindingSetterListener extends ClickListener {
    private final int playerCode;
    private final ControlsMenuScreen parent;
    private final TextButton button;
    private final String keyName;

    /**
     * Only constructor.
     * @param playerCode code of the {@link Player} whose keybinding buttons are listened to.
     * @param parent the screen that tracks whether a button is waiting for input.
     * @param button the button that has this listener applied.
     * @param keyName the name of the key that is being tracked.
     * @see KeybindManager
     */
    public KeybindingSetterListener(int playerCode, ControlsMenuScreen parent, TextButton button, String keyName) {
        this.playerCode = playerCode;
        this.parent = parent;
        this.button = button;
        this.keyName = keyName;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        parent.buttonPressed = true;
        String previousText = button.getText().toString();
        button.setText("");
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (!KeybindManager.containsKeycode(keycode)) {
                    button.setText(Input.Keys.toString(keycode) + "*");
                    KeybindManager.setKey(playerCode, keyName, keycode);
                } else {
                    button.setText(previousText);
                }
                parent.buttonPressed = false;
                return true;
            }
        });
    }
}
