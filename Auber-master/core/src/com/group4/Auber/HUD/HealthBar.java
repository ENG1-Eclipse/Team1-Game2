package com.group4.Auber.HUD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Draws a rectangular health bar with black background. The start value is 100% with the current value
 * being the value that changes as the health changes.
 *
 * @author Robert Watts
 */
public class HealthBar extends Actor {

    /**
     * The initial value of the health
     */
    protected int startValue;

    /**
     * The current value of the health
     */
    protected int currentValue;

    protected float width;
    protected float height;
    protected String title;
    protected BitmapFont font;
    protected final float barMargin = 10;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    /**
     * Construct the class, and save the initial variables
     *
     * @param height the height of the bar
     * @param width the width of the bar
     * @param title the title used within the health bar
     * @param startValue the start value
     * @param font the font to use
     */
    public HealthBar(float height, float width, String title, int startValue, BitmapFont font){
        this.startValue = startValue;
        this.currentValue = startValue;
        this.font = font;
        this.height = height;
        this.width = width;
        this.title = title;
        shapeRenderer.setAutoShapeType(true);
    }

    /**
     *
     * @param height the height of the bar
     * @param width the width of the bar
     * @param title the title used
     * @param startValue the start value
     */
    public HealthBar(float height, float width, String title, int startValue){
        this(height,width,title,startValue,new BitmapFont());
    }


    /**
     * Draw the health bar.
     *
     * @param batch The current batch from the satage
     * @param parentAlpha The parent alpha
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        //Allow colours to blend
        batch.end();
        Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);

        //Draw bg
        shapeRenderer.setColor(0,0,0,0.5f);
        shapeRenderer.rect(getX(),getY(), width, height);

        //Calculate the bar dimensions
        float barX = getX() + barMargin;
        float barY = getY() + barMargin;
        float barWidth = width - 2*barMargin;
        float barHeight = height - 2*barMargin;

        //Draw bar
        float barValue = (float)currentValue/(float)startValue;
        shapeRenderer.setColor(new Color().set(1,0,0,1).lerp(0,1,0,1, barValue));
        shapeRenderer.rect(barX,barY, barWidth * barValue, barHeight);

        //Stop colours blending for the next set of actors in the stage
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();

        //Render the text
        font.setColor(1,1,1,1f);
        font.draw(batch, getTextLayout(), barX + barMargin, barY+(barHeight/2) + (font.getXHeight()));
    }

    /**
     * Set the current value of the health. This value is divided by startValue to get a percentage, which is then
     * used to calculate the width of the health bar.
     *
     * @param currentValue the current value (integer)
     */
    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    /**
     * Get the label text layout
     * @return GlyphLayout for use in a label
     */
    private GlyphLayout getTextLayout(){
        return new GlyphLayout(font, String.valueOf(title + ": " + currentValue + "/" + startValue));
    }

    /**
     * Set the max value of the health. This value is used in conjunction with the current value to calculate the width
     * of the health bar.
     *
     * @param startValue the initial value.
     */
    public void setStartValue(int startValue) {
        this.startValue = startValue;
    }
}
