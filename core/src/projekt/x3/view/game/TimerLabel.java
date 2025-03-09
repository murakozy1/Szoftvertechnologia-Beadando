package x3.view.game;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * This label allows time to be formatted in a string without needing to store the original string outside.
 */
public class TimerLabel extends Label {
    private final String before;
    private final String after;

    /**
     * Only constructor.
     * <br>
     * Sets the text of the label with a placeholder value of <code>"0.0"</code>.
     * @param before text before the timer value.
     * @param after text after the timer value.
     * @param style style of the label.
     */
    public TimerLabel(CharSequence before, CharSequence after, LabelStyle style) {
        super(before + "0.0" + after, style);
        this.before = before.toString();
        this.after = after.toString();
    }

    /**
     * Replaces the timer value in the label text.
     * @param value the new value of the timer.
     */
    public void setValue(String value) {
        setText(before + value + after);
    }

    /**
     * Replaces the timer value in the label text.
     * @param value the new value of the timer.
     */
    public void setValue(int value) {
        setText(before + value + after);
    }
}
