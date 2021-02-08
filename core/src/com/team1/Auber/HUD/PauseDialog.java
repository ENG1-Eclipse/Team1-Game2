package com.team1.Auber.HUD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.team1.Auber.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;

/**
 * @author Harry Smith (Team 1)
 */

public class PauseDialog extends Dialog {

    private final Player player;
    private final HUD hud;
    public AuberGame game;

    /**
     * The time it takes for the dialog to fade in and out
     */
    private final float fadeTime = 0.2f;

    /**
     *
     * @param player - the player class
     * @param hud - the HUD in use
     * @param game - the running instance of the game
     */

    public PauseDialog(Player player, HUD hud, AuberGame game){
        super("Pause Menu", new Skin(Gdx.files.internal("skin/uiskin.json")));
        this.game = game;
        this.player = player;
        this.hud = hud;

        /**
         * Add buttons to pause menu
         */

        text("Options: ").center();
        button("Resume", 0);
        if(! GameScreen.demo) {
            //Prevent the save button from appearing in demo mode
            button("Save Game", 1);
        }
        button("Exit", 2);
    }

    /**
     * Centres and shows the menu
     * @param stage
     * @return
     */
    @Override
    public PauseDialog show(Stage stage){
        super.show(stage,fadeIn(fadeTime));
        setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 2));

        return this;
    }

    @Override
    public void hide(){
        super.hide(fadeOut(fadeTime));
    }

    /**
     * Does an action based on the selected button
     * @param object - the ID of the button selected
     */
    public void result(Object object){

        if((Integer) object == 0){
            //Resume
            GameScreen.gamePaused = false;
            return;
        } else if((Integer) object == 2){
            //Exit
            GameScreen.gamePaused = false;
            GameScreen.needToExit = true;
            return;
        }else if((Integer) object == 1){
            //Save
            GameScreen.gamePaused = false;
            GameScreen.needToSave = true;
            return;
        }
    }

}
