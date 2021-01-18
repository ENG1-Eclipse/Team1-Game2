package com.team1.Auber;


import com.team1.Auber.PowerUp;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import java.lang.Math;

/**
 * The player sprite. Extends the {@link com.badlogic.gdx.scenes.scene2d.Actor} class.
 * Handles key presses for play moment, as wll as drawing the player each frame.
 * The key press are polled rather than using events so that the player can move diagonally.
 *
 * @author Robert Watts (Team 4)
 * @author Adam Wiegand (Team 4)
 * @author Bogdan Bodnariu-Lescinschi (Team 4)
 *
 * @@author Harry Smith (Team 1 - Implement Difficulty)
 */

public class Player extends Actor {
    public static AuberGame game;
    public MapRenderer map;

    //The images of the player looking in different directions
    private final Texture imageDown = new Texture(Gdx.files.internal("img/player.png"));
    private final Texture imageUp = new Texture(Gdx.files.internal("img/player_up.png"));
    private final Texture imageLeft = new Texture(Gdx.files.internal("img/player_left.png"));
    private final Texture imageRight = new Texture(Gdx.files.internal("img/player_right.png"));
    private final Texture imageAttack = new Texture(Gdx.files.internal("img/player_attack.png")); //assumed to be square
    private final Texture imageTarget = new Texture(Gdx.files.internal("img/player_target.png"));
    private Texture currentImage = imageDown;

    //Different sound effects for different conditions
    private Sound step = Gdx.audio.newSound(Gdx.files.internal("audio/footstep.mp3"));
    private Sound swing = Gdx.audio.newSound(Gdx.files.internal("audio/swing.mp3"));
    private Sound punch1 = Gdx.audio.newSound(Gdx.files.internal("audio/punch1.mp3"));
    private Sound punch2 = Gdx.audio.newSound(Gdx.files.internal("audio/punch2.mp3"));

    /**
     * The speed multiplier at which the player moves
     */
    private float playerSpeed = 1.5f;

    /**
     * The health of the player
     */
    private int health = 100;

    /**
     * Max health of the player
     */
    private int maxHealth = 100;

    /**
     * The health timer. This is used when the player is in the medbay to add 1 health on 0.01 seconds
     */
    private float healthTimer = 0;

    /**
     * Used as a timer so that the footsteps sound is played evey 0.32 seconds
     */
    private long audioStartTimer = 0;

    /**
     * The attack delay
     */
    private int attackDelay = 0;

    private Integer difficulty = 0;

    /**
     * Create the player
     *
     * @param map the map
     * @param x the starting X coordinate
     * @param y the starting Y coordinate
     * @param difficulty the difficulty of the game
     */
    public Player(MapRenderer map, int x, int y, Integer difficulty){
        this.difficulty = difficulty;

        /** Increase player speed slightly if on EASY mode **/
        if (this.difficulty == 0) {
            this.playerSpeed = 1.7f;
        }

        this.map = map;
        setBounds(map.worldPos(x), map.worldPos(y), 20f, 20f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        //Move the player by a set amount if the keys are pressed.
        float deltaX = 0;
        float deltaY = 0;
        if(Gdx.input.isKeyPressed(Input.Keys.W)){
            deltaY += playerSpeed;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)){
            deltaY -= playerSpeed;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            deltaX -= playerSpeed;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            deltaX += playerSpeed;
        }

        //Check the space is empty before moving into it
        map.autoLeave(this,getX(),getY(), getWidth(), getHeight());
        if (map.Empty(getX() + deltaX, getY(), getWidth(), getHeight())){
            moveBy(deltaX, 0);
        }
        if (map.Empty(getX(), getY() + deltaY, getWidth(), getHeight())){
            moveBy(0, deltaY);
        }
        map.autoEnter(this,getX(),getY(), getWidth(), getHeight());

        //See if the player has moved
        if (Math.abs(deltaX) > 0 || Math.abs(deltaY) > 0){

            //Sets the footstep sound effect to play at 0.32 sec intervals when the player is moving
            if (TimeUtils.timeSinceNanos(audioStartTimer) > 320000000) {
                if(! AuberGame.isGameMuted){
                    step.play(0.3f);
                }

                audioStartTimer = TimeUtils.nanoTime();
            }

            //Change the image
            if (Math.abs(deltaX) >= Math.abs(deltaY)) {
                if(deltaX > 0){
                    currentImage = imageRight;
                } else {
                    currentImage = imageLeft;
                }
            } else {
                if(deltaY > 0){
                    currentImage = imageUp;
                } else {
                    currentImage = imageDown;
                }
            }

        }

        //If the space bar is down attack
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            float xAtt = getX() - 12f;
            float yAtt = getY() - 6f;

            //assuming the imageAttack square so just get the width
            float wAtt = imageAttack.getWidth();

            //What direction to attack in?
            if(currentImage == imageRight){
                //attack right
                xAtt += 32;
            }else if (currentImage == imageLeft) {
                //attack left
                xAtt -= 32;
            }else if(currentImage == imageUp){
                //attack up
                yAtt  += 32;
            }else if (currentImage == imageDown) {
                //attack down
                yAtt  -= 32;
            }

            //do the attack
            if (attackDelay == 0){
                Operative target = null;
                for (Actor thing : map.InArea(xAtt, yAtt, wAtt, wAtt)) {
                    if (thing instanceof Operative){
                        target = (Operative) thing;
                        target.onHit(this, 20);
                        if(! AuberGame.isGameMuted){
                            punch1.play(0.20f);
                        }

                    }else if(thing instanceof PowerUp){
                        ((PowerUp)thing).onHit(this);
                    }
                }
                if (target == null) {
                    if(! AuberGame.isGameMuted){
                        swing.play(0.45f);
                    }

                }
                attackDelay = 61;
                //display attack
                batch.draw(imageAttack, xAtt, yAtt, wAtt, wAtt);
            } else {
                //display uncharged attack
                batch.draw(imageTarget, xAtt, yAtt, wAtt, wAtt);
            }
        }

        //attack delay
        if (attackDelay > 0){
            attackDelay -= 1;
        }

        //Player Health
        if (map.Effect(2,this)){
            healthTimer += Gdx.graphics.getDeltaTime();
            if(healthTimer >= 0.1f && health < maxHealth) {
                health += 1;
                healthTimer = 0f;
            }
        }

        //Draw the player image
        batch.draw(currentImage, getX() - 6, getY(), currentImage.getWidth(), currentImage.getHeight());
    }

    @Override
    public float getHeight(){
        return currentImage.getHeight();
    }

    @Override
    public float getWidth(){
        return currentImage.getWidth();
    }

    /**
     * The player has been attacked so decreases the health
     *
     * @param by the actor that attacked the player
     * @param amount the amount to reduce the health by
     */
    public void onHit(Actor by,int amount) {
        //See if it was the operative that attacked
        if (by instanceof Operative){
            if(! AuberGame.isGameMuted){
                punch2.play(0.30f);
            }


            //Reduce the health if the player, and make sure the operative is not dead
            health -= amount;
            if (health <= 0) {
                onDeath();
            }
        }
    }

    /**
     * Called when the player dies
     */
    public void onDeath(){
        map.autoLeave(this);
        game.setScreen(new GameEndScreen(game, false));
    }

    /**
     * Get the current health of the player
     * @return the current player heath
     */
    public int getHealth(){
        return health;
    }
    public int getMaxHealth(){
        return this.maxHealth;
    }

    public float getSpeed(){
        return this.playerSpeed;
    }
    public void setSpeed(float newSpeed){
        this.playerSpeed = newSpeed;
    }

    public void setMaxHealth(int newHealth){
        this.maxHealth = newHealth;
    }

    public void setHealth(int newHealth){
        health = newHealth;
    }
}