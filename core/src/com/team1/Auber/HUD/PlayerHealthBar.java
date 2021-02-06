package com.team1.Auber.HUD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.team1.Auber.Player;

/**
 * Draws the health bar of the game
 *
 * @author Robert Watts (Team 4)
 */
public class PlayerHealthBar extends Actor {

    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    protected float size;
    protected Player player;
    protected BitmapFont font;

    /**
     * Used so that on the first draw, the font is scaled. This should be set to true whenever the window is resized.
     */
    protected Boolean setFontScale = true;

    /**
     * Create the health bar with the LibGDX default font from {@link com.badlogic.gdx.graphics.g2d.BitmapFont}.
     *
     * @param player the player
     * @param size the size of the circle
     */
    public PlayerHealthBar(Player player, float size){
        this(player, size, new BitmapFont());
    }

    /**
     * Create the health bar.
     *
     * @param player the player
     * @param size the size of the circle
     * @param font the font of the text
     */
    public PlayerHealthBar(Player player, float size, BitmapFont font){
        this.font = font;
        this.size = size;
        this.player = player;
        shapeRenderer.setAutoShapeType(true);
    }

    /**
     * Draw the health bar using the shape render.
     *
     * @param batch The current batch from the satage
     * @param parentAlpha The parent alpha
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        float radius = size /2;
        float healthPercentage = (float) player.getHealth() / player.getMaxHealth();
        float circleCenterX = getX() + radius;
        float circleCenterY = getY() + radius;

        //Allow colours to blend
        batch.end();
        Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin();

        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);

        //Draw bg
        shapeRenderer.setColor(0,0,0,0.5f);
        shapeRenderer.circle(circleCenterX,circleCenterY,radius);

        //Draw health bar + use a gradient to change the colour depending on healthPercentage
        shapeRenderer.setColor(new Color().set(1,0,0,1).lerp(0,1,0,1, healthPercentage));
        shapeRenderer.arc(circleCenterX,circleCenterY, radius,270,361 - (360 * (1-healthPercentage)), 400);

        //Draw center circle
        shapeRenderer.setColor(0, 0, 0, 0.5f);
        shapeRenderer.circle(circleCenterX,circleCenterY,radius * 0.75f);

        //Stop colours blending for the next set of actors in the stage
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();

        //Draw the health bar text
        if (setFontScale){
            //Set the scale on the first time through
            float textScale = (radius * 0.75f ) / getTextLayout().width;
            font.getData().setScale(textScale);
            setFontScale = false;
        }

        font.setColor(1,1,1,1f);
        font.draw(batch, getTextLayout(), circleCenterX - (getTextLayout().width )  / 2, circleCenterY + (font.getXHeight())/2);
    }

    /**
     * Get the label text layout
     * @return GlyphLayout for use in a label
     */
    private GlyphLayout getTextLayout(){
        return new GlyphLayout(font, String.valueOf(player.getHealth()));
    }
}
