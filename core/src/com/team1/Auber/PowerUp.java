package com.team1.Auber;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.lang.Math;
import java.util.ArrayList;

/**
 * @author Jamie Hewison (Team 1)
 * 
 * Extends the {@link com.badlogic.gdx.scenes.scene2d.Actor} class.
 * Power up object for Player.java to interact with on the map.
 */



public class PowerUp extends Actor {
    public static AuberGame game;
    public MapRenderer map;
    private final Texture powerUpTexture;
    int powerType;
    private final String powerUpNames[] = {"health","speed","strength","specialAttackPowerUp","regen"};

    float xPos;
    float yPos;

    public static ArrayList<PowerUp> powerupsRemaining = new ArrayList<PowerUp>();

    /**
     * Create the powerup
     *
     * @param map the map
     * @param x the starting X coordinate
     * @param y the starting Y coordinate
     * @param powerUpType Number of the power up type. 0:Health 1:Speed 2:strength
     */
    public PowerUp(MapRenderer map, int x, int y,int powerUpType){
        powerType = powerUpType;
        powerUpTexture = new Texture(Gdx.files.internal("img/powerUps/"+powerUpNames[powerUpType]+".png"));
        this.map = map;
        xPos = map.worldPos(x);
        yPos = map.worldPos(y);
        powerupsRemaining.add(this);
        setBounds(map.worldPos(x), map.worldPos(y), 20f, 20f);
        map.autoEnter(this,getX(),getY(), getWidth(), getHeight());
    }
    
    public PowerUp(MapRenderer map, Float x, Float y,int powerUpType){
        powerType = powerUpType;
        powerUpTexture = new Texture(Gdx.files.internal("img/powerUps/"+powerUpNames[powerUpType]+".png"));
        this.map = map;
        xPos = x;
        yPos = y;
        powerupsRemaining.add(this);
        setBounds(xPos, yPos, 20f, 20f);
        map.autoEnter(this,getX(),getY(), getWidth(), getHeight());
    }

    float time;
    float animationRate = 1f;
    int size = 25;
    public void draw(Batch batch, float parentAlpha) {
        //Draw the powerup image
        time += Gdx.graphics.getDeltaTime();
        batch.draw(powerUpTexture, xPos, (float)(yPos+size*0.1*(1+Math.sin((2*Math.PI*time)/(animationRate)))), size,size);
        setBounds(xPos,(float)(yPos+size*0.1*(1+Math.sin((2*Math.PI*time)/(animationRate)))), size, size);
    }

    /**
   * The ablity has been picked up
   *
   * @param by the actor that picked the powerup
   */
    public void onHit(Actor by) {
        if (by instanceof Player){
            //Pickup ablity and apply effects
            if(this.powerType == 0){
                //Health boost pickup: increses player health by 
                ((Player)by).setMaxHealth(((Player)by).getMaxHealth()+20);
                ((Player)by).setHealth(((Player)by).getMaxHealth());
                powerupsRemaining.remove(this);
                map.autoLeave(this);
                remove();
                powerUpTexture.dispose();
            }else if(this.powerType == 1){
                //Speed Boost
                ((Player)by).setSpeed(((Player)by).getSpeed()*1.1f);
                powerupsRemaining.remove(this);
                map.autoLeave(this);
                remove();
                powerUpTexture.dispose();
                
            }
            else if(this.powerType == 2){
                //Attack Boost +5 hit damage
                ((Player)by).setDamage(((Player)by).getDamage()+5);
                powerupsRemaining.remove(this);
                map.autoLeave(this);
                remove();
                powerUpTexture.dispose();
                
            }
            else if(this.powerType == 3){
                //Special Attack is enabled by this
                ((Player)by).enableSpecialAttack();
                powerupsRemaining.remove(this);
                map.autoLeave(this);
                remove();
                powerUpTexture.dispose();
                
            }
            else if(this.powerType == 4){
                //Health Regen for 15 seconds
                ((Player)by).startRegen(15);
                powerupsRemaining.remove(this);
                map.autoLeave(this);
                remove();
                powerUpTexture.dispose();
                
            }

        
        }
    }

}
