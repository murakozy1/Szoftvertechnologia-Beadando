package x3.view.mapselect;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

/**
 * A selectable animated MapOption which is an Image that stores whether it is selected.
 * When selected, the Image grows.
 */
public class MapOption extends Image {
    private final float targetScale = 1.05f;
    private final float scaleTime = 0.1f;
    private final double error = 0.005;
    private boolean selected = false;
    private boolean hovered = false;

    /**
     * Creates a MapOption. <br>
     * Initialises the texture of the image, adds click, and hover listeners.<br>
     * @param texture the texture of the image
     */
    public MapOption(final Texture texture) {
        super(texture);

        setOrigin(Align.center);

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selected = !selected;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                hovered = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                hovered = false;
            }
        });
    }

    public boolean isSelected() {
        return selected;
    }

    private float currentTarget() {
        return (selected || hovered) ? targetScale : 1.0f;
    }

    /**
     * Carries out an animation.<br>
     * Grows or shrinks the MapOption image, with an animation.<br>
     ** @param delta Time in seconds since the last frame.
     */
    @Override
    public void act(float delta) {
        float scaleFactor = (currentTarget() - getScaleX()) * delta / scaleTime;
        scaleBy(scaleFactor);
        if (Math.abs(currentTarget() - getScaleX()) < error) {
            setScale(currentTarget());
        }
    }
}