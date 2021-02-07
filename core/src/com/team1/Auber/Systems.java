package com.team1.Auber;
import java.util.ArrayList;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.team1.Auber.HUD.HUD;

/**
 * Create and maneges the systems that the players and operatives use. This extends
 * {@link com.badlogic.gdx.scenes.scene2d.Actor} purely so that it can use the draw function as its called every frame.
 *
 * @author Adam Wiegand (Team 4)
 * @author Robert Watts (Team 4)
 * @author Bogdan Bodnariu-Lescinschi (Team 4)
 * @author Harry Smith (Team 1)
 */
public class Systems extends Actor {
    public static AuberGame game;
    public int health;
    int healthMax;
    String roomName;
    MapRenderer map;
    HUD hud;
    int gridX;
    int gridY;

    /**
     * how long before the system starts healing
     */
    int healWait = 300;

    /**
     * The delay used
     */
    int delay = 0;

    /**
     * The current notification its on
     */
    int currentNotification = 0;

    /**
     * A static list of all the systems created
     */
    public static ArrayList<Systems> systemsRemaining = new ArrayList<Systems>();

    /**
     * Create the system
     *
     * @param x The x coordinate of the system on the TMX map
     * @param y The Y coordinate of the system on the TMX map
     * @param w The width of the system on the TMX map
     * @param h The height of the the system on the TMX map
     * @param map the map
     * @param hud the headsup display
     * @param roomName the name of the room the system is in
     * @param healthMax  the maximum health value
     */
    public Systems(int x, int y, int w, int h, MapRenderer map, HUD hud, String roomName, int healthMax){
        this.map = map;
        this.roomName = roomName;
        this.health = healthMax;
        this.healthMax = healthMax;
        this.hud = hud;
        gridX = x;
        gridY = y;
        systemsRemaining.add(this);
        int tilewidth = map.properties.get("tilewidth", Integer.class);
        setBounds((float) x*tilewidth,(float) y*tilewidth,(float) w*(tilewidth - 1),(float) h*(tilewidth - 1));
        map.autoEnter(this);
    }

    /**
     * Create a 1 by 1 system with a health value of 100.
     * @param x The x coordinate of the system on the TMX map
     * @param y The Y coordinate of the system on the TMX map
     * @param map the map
     * @param hud the headsup display
     * @param roomName the name of the room the system is in
     */
    public Systems(int x, int y, MapRenderer map, HUD hud, String roomName){//assumes 1x1 system
        this(x,y,1,1,map, hud, roomName,100);
    }

    /**
     * This is just used as it is called each frame. The systems class does not actually draw anything
     *
     * @param batch The current batch from the stage
     * @param parentAlpha The parent alpha
     */
    @Override
    public void draw(Batch batch, float parentAlpha){
        delay += 1;

        if (delay >= healWait && health < healthMax && delay % 10 == 0){
            //edit the delay % X to change rate of healing, fps/X = hps
            health += 1;
        }

        if(currentNotification != 0 && health == healthMax){
            currentNotification = 0;
        }
    }

    /**
     * The system has been attacked so decreases the health
     *
     * @param by the actor that attacked the system
     * @param amount the amount to reduce the health by
     */
    public void onHit(Actor by,int amount) {
        if (by instanceof Operative){
            delay = 0;
            health -= amount;
            if (health <= 0) {
                onDeath();
            }

            if (currentNotification == 0){
                hud.infoNotification("The system in the " + roomName + " is being attacked.");
                currentNotification += 1;
            } else if (currentNotification == 1 && health <= 50){
                hud.warningNotification("The system in the " + roomName + " is down to 50% health.");
                currentNotification += 1;
            }
        }
    }

    /**
     * Called when the player dies
     */
    public void onDeath(){
        map.autoLeave(this,getX(),getY(), getWidth(), getHeight());
        systemsRemaining.remove(this);
        remove(); //so its .draw() isn't called
        if (systemsRemaining.size() == 0){
            game.setScreen(new GameEndScreen(game, false));
        }
        hud.errorNotification("The system in " + roomName + " has been destroyed!");

    }
    
}