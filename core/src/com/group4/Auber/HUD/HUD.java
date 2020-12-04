package com.group4.Auber.HUD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.group4.Auber.Systems;
import com.group4.Auber.Operative;
import com.group4.Auber.Player;
import org.json.JSONObject;

/**
 * Creates and loads the heads up display.
 *
 * @author Robert Watts
 */
public class HUD extends Stage {

    protected HealthBar systemsHealthBar;
    protected HealthBar operativesHealthBar;
    protected Player player;
    protected PlayerHealthBar playerHealthBar;
    protected NotificationWindow notificationWindow;
    protected TeleporterDialog teleporterDialog;

    /**
     * Used to calculate the height of the HUD as a fractional value of the screen
     */
    final protected float heightScale = 1/7f;

    /**
     * Used to calculate the width of the notification window as a fractional value of the screen
     */
    final protected float notificationWindowWidthScale = 3/8f;

    /**
     * Used to calculate the offset from the left or right of the screen
     */
    final protected float xOffset = 10;

    /**
     * Used to calculate the offset from the top or bottom of the screen
     */
    final protected float yOffset = 10;

    /**
     * Create the player health bar, notification window, system health bar, operative health bar and teleporter
     * dialogue
     * @param player The player object
     * @param gameData the game data as a JSONObject
     */
    public HUD(Player player, JSONObject gameData){
        this.player = player;
        float scaledHeight = Gdx.graphics.getHeight() * heightScale;
        float scaledWidth = Gdx.graphics.getWidth() * notificationWindowWidthScale;

        //Create the health bar and add it to the stage
        playerHealthBar = new PlayerHealthBar(player,scaledHeight);
        playerHealthBar.setPosition(xOffset,yOffset);
        this.addActor(playerHealthBar);

        //Create the notification window and add it to the stage
        notificationWindow = new NotificationWindow(scaledHeight, scaledWidth);
        notificationWindow.setPosition(Gdx.graphics.getWidth() - scaledWidth - xOffset, yOffset);
        this.addActor(notificationWindow);

        //Create the system health bar and add it to the stage
        systemsHealthBar = new HealthBar(50, scaledWidth, "System Health", Systems.systemsRemaining.size());
        systemsHealthBar.setPosition(Gdx.graphics.getWidth() - scaledWidth - xOffset, yOffset + scaledHeight);
        this.addActor(systemsHealthBar);

        //Create the system health bar and add it to the stage
        operativesHealthBar = new HealthBar(50, scaledWidth, "Operatives Health", Operative.remainingOpers);
        operativesHealthBar.setPosition(Gdx.graphics.getWidth() - scaledWidth - xOffset, yOffset + scaledHeight + 50);
        this.addActor(operativesHealthBar);

        //Create the teleporter and the event listener
        teleporterDialog = new TeleporterDialog(gameData, player, this,2);
        addListener(new InputListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char key)
            {
                //if the letter to is typed the show the teleporter dialouge
                if(key == 'e'){
                    teleporterDialog.show(getStage());
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Add a success notification
     *
     * @param text the notification
     */
    public void successNotification(String text){
        notificationWindow.addNotification(text, new Color(0,1,0,1));
    }

    /**
     * Add a info notification
     *
     * @param text the notification
     */
    public void infoNotification(String text){
        notificationWindow.addNotification(text, new Color(1,1,1,1));
    }

    /**
     * Add a error notification
     *
     * @param text the notification
     */
    public void warningNotification(String text){
        notificationWindow.addNotification(text, new Color(1,0.647f,0,1));
    }

    /**
     * Add a game notification
     *
     * @param text the notification
     */
    public void gameNotification(String text){
        notificationWindow.addNotification(text, new Color(1,1,0,1));
    }


    /**
     * Add a error notification
     *
     * @param text the notification
     */
    public void errorNotification(String text){
        notificationWindow.addNotification(text, new Color(1,0,0,1));
    }

    /**
     * Draw the stage. Overridden to update the health bars every render
     */
    @Override
    public void draw(){
        super.draw();
        //Set the current values
        systemsHealthBar.setCurrentValue(Systems.systemsRemaining.size());
        operativesHealthBar.setCurrentValue(Operative.remainingOpers);

        this.act();

        //If the player moves off a teleporter pad hide the dialogue
        if (!teleporterDialog.isPlayerTouchingTeleporter()){
            teleporterDialog.hide();
        }
    }

    /**
     * Set the start values of the operatives and system's
     * @param numOfOperatives int number of operatives
     * @param numOfSystems int number of systems
     */
    public void setValues(int numOfOperatives, int numOfSystems){
        operativesHealthBar.setStartValue(numOfOperatives);
        systemsHealthBar.setStartValue(numOfSystems);
    }

    /**
     * Gets this stage
     *
     * @return returns the value of this
     */
    private Stage getStage(){
        return this;
    }
}
