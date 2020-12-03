package com.group4.Auber.HUD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.group4.Auber.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;

/**
 * Override the the {@link com.badlogic.gdx.scenes.scene2d.ui.Dialog} to add methods for the teleports.
 *
 * @author Robert Watts
 */
public class TeleporterDialog extends Dialog {
    private final Player player;
    private final HUD hud;
    private final ArrayList<int[]> teleporterPositions = new ArrayList<int[]>();

    /**
     * The size of the teleporter in TMX squares
     */
    private final int teleporterSize;

    /**
     * The time it takes for the dialogue to fade in and out
     */
    private final float fadeTime = 0.2f;

    /**
     * The teleporter sound. Played when the player moves position.
     */
    private final Sound teleporterSounds = Gdx.audio.newSound(Gdx.files.internal("audio/teleporter.mp3"));

    /**
     *
     * @param gameData A JSONObject with the game data. This is used to pull the locations of the teleporters.
     * @param player The player class
     * @param hud The HUD display - used for the notifications
     * @param teleporterSize - The size in map squares (from the TMX file) of the teleporter's (default should be 2 as they are 2x2)
     */
    public TeleporterDialog(JSONObject gameData, Player player, HUD hud, int teleporterSize){
        super("Teleporters", new Skin(Gdx.files.internal("skin/uiskin.json")));
        this.teleporterSize = teleporterSize;
        this.player = player;
        this.hud = hud;

        //The text displayed on the dialouge
        text("Where do you want to teleport?");

        //For all of the rooms check to see if it has a dialouge
        for (int i = 0; i < gameData.getJSONArray("rooms").length(); i++) {
            if (!gameData.getJSONArray("rooms").getJSONObject(i).isNull("teleporterCoords")){
                //Add the button
                button(gameData.getJSONArray("rooms").getJSONObject(i).getString("name"), gameData.getJSONArray("rooms").getJSONObject(i).getJSONArray("teleporterCoords"));

                //Add the position to a list for use when working out if the player is touching a teleporter
                teleporterPositions.add(new int[] {
                        gameData.getJSONArray("rooms").getJSONObject(i).getJSONArray("teleporterCoords").getInt(0),
                        gameData.getJSONArray("rooms").getJSONObject(i).getJSONArray("teleporterCoords").getInt(1)
                });
            }
        }
    }

    /**
     * Centers the dialog in the stage and the creates it, as long as the user is over a teleporter pad
     * @param stage the stage to add the dialogue to
     * @return this
     */
    @Override
    public TeleporterDialog show(Stage stage){
        if(!isPlayerTouchingTeleporter()){
            hud.errorNotification("You need to be standing on a teleporter pad to be able to teleport!");
        } else {
            super.show(stage,fadeIn(fadeTime));
            setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 2));
        }

        return this;
    }

    /**
     * Fade out the diolouge. Overriden then default so the fade time can easily be ajusted.
     */
    @Override
    public void hide(){
        super.hide(fadeOut(fadeTime));
    }

    /**
     * Called when there is a result from the dialogue
     *
     * @param object The object from the button
     */
    public void result(Object object){
        //Make sure the object is a JSONArray
        if (!(object instanceof JSONArray)){
            return;
        }

        //Make sure the player is still on a teleporter
        if(!isPlayerTouchingTeleporter()) {
            hud.errorNotification("You have moved off the teleporter pad! You need to be standing on a teleporter pad to be able to teleport!");
            return;
        }

        //Move the player to the center of the new teleporter and play teleporter the sound
        JSONArray coords = (JSONArray) object;
        float teleporterOffset = teleporterSize / 2f;
        teleporterSounds.play(0.13f);
        player.setPosition(player.map.worldPos(coords.getInt(0) + teleporterOffset -0.125f),
                player.map.worldPos(coords.getInt(1) + teleporterOffset - 0.25f));

    }

    /**
     * See if the player is touching (or standing on) a teleporter
     *
     * @return boolean if the player is touching a teleporter
     */
    public boolean isPlayerTouchingTeleporter(){

        float teleporterWorldSize = player.map.worldPos(teleporterSize);

         //For each of the teleports, check if the player is standing on it. If they are return true.
        for (int[] teleporterPosition : teleporterPositions) {
            if (!(player.map.worldPos(teleporterPosition[0]) >= player.getX() + player.getWidth() ||
                    player.getX() >= player.map.worldPos(teleporterPosition[0]) + teleporterWorldSize ||
                    player.map.worldPos(teleporterPosition[1]) >= player.getY() + player.getHeight() ||
                    player.getY() >= player.map.worldPos(teleporterPosition[1]) + teleporterWorldSize)) {
                return true;
            }

        }
        return false;
    }
}
