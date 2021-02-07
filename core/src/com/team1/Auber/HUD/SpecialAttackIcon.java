package com.team1.Auber.HUD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.Texture;
import com.team1.Auber.GameScreen;
import com.team1.Auber.Player;

/**
 * Draws the special attack indicator
 *
 * @author Jamie Hewison (Team 1)
 * @author Harry Smith (Team 1)
 */

public class SpecialAttackIcon extends Actor {

    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    protected float size;
    protected Player player;
    protected BitmapFont font;
    private Texture icon = new Texture(Gdx.files.internal("img/specialAttackPowerUp.png"));

    /**
     * Used so that on the first draw, the font is scaled. This should be set to true whenever the window is resized.
     */
    protected Boolean setFontScale = true;

    /**
     * Create the Special Attack Icon with the LibGDX default font from {@link com.badlogic.gdx.graphics.g2d.BitmapFont}.
     *
     * @param player the player
     * @param size the size of the circle
     */
    public SpecialAttackIcon(Player player, float size){
        this(player, size, new BitmapFont());
    }

    /**
     * Create the Special Attack Icon.
     *
     * @param player the player
     * @param size the size of the circle
     * @param font the font of the text
     */
    public SpecialAttackIcon(Player player, float size, BitmapFont font){
        this.font = font;
        this.size = size;
        this.player = player;
        shapeRenderer.setAutoShapeType(true);
    }

    /**
     * Draw the special attack.
     *
     * @param batch The current batch from the stage
     * @param parentAlpha The parent alpha
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        

        //Draw the Special Attack Icon F
        
        if(setFontScale){
            //Set the scale on the first time through
            float textScale = size / (getTextLayout().width*4);
            font.getData().setScale(textScale);
            setFontScale = false;
        }

        //Checks whether the player can use the special attack
        if(player.getSpecialAttack()){
            //Attack has been unlocked
            if(player.getSpecialAttackDelay() == 0){
                //Attack can be used
                font.setColor(0,1,0,1f);
                font.draw(batch, getTextLayout(), getX()+size, getY()+getTextLayout().height);
                batch.draw(icon, getX(), getY(), size, size);
            }else if(player.getSpecialAttackDelay() <= 5){
                //Attack is nearly ready
                font.setColor(1f,0.8f,0,1f);
                font.draw(batch, getTextLayout(), getX()+size, getY()+getTextLayout().height);
                batch.draw(icon, getX(), getY(), size, size);
            }else{
                //Attack is powering up
                font.setColor(0.8f,0f,0f,1f);
                font.draw(batch, getTextLayout(), getX()+size, getY()+getTextLayout().height);
                batch.draw(icon, getX(), getY(), size, size);
            }
        }
        
        
    }

    /**
     * Get the label text layout
     * @return GlyphLayout for use in a label
     */
    private GlyphLayout getTextLayout(){

        if(player.getSpecialAttackDelay() == 0) {
            //Show F to indicate the button to be pressed
            return new GlyphLayout(font, String.valueOf("F"));
        }else{
            //Display how long is left on the recharge
            String secsLeft = String.valueOf((int) Math.ceil(player.getSpecialAttackDelay()));
            return new GlyphLayout(font, String.valueOf(secsLeft));
        }
    }
}
