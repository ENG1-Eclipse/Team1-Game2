package com.team1.Auber;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;

import java.io.*;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.team1.Auber.HUD.HUD;
import com.team1.Auber.Smoke.Smoke;

import org.json.*;
import java.util.Base64;
import java.util.Random;

import javax.print.FlavorException;

import com.team1.Auber.PowerUp;


/**
 * GameScreen is an extension of {@link com.badlogic.gdx.ScreenAdapter} to create and render the game.
 *
 * @author Robert Watts (Team 4)
 * @author Adam Wiegand (Team 4)
 *
 * @author Harry Smith (Team 1)
 */
public class GameScreen extends ScreenAdapter {

    public AuberGame game;
    private Stage stage;
    private Player player;
    private MapRenderer map;
    private OrthographicCamera camera;
    private com.team1.Auber.HUD.HUD HUD;
    public Integer difficulty;
    public Boolean resumingSave;

    /**
     * These are all Boolean values that control aspects of the game and/or what needs to occur.
     */
    public static Boolean needToSave = false;
    public static Boolean needToExit = false;
    public static Boolean gameOverWin = false;
    public static Boolean gameOverLose = false;
    public static Boolean gamePaused = false;
    public static Boolean demo = false;

    /**
     * The sprite batch for everything except the map popup
     */
    private final SpriteBatch batch = new SpriteBatch();

    /**
     * Used for the map pop up. This batch renders over the top of the normal batch when the map is open.
     */
    private final SpriteBatch mapSpriteBatch = new SpriteBatch();

    /**
     * The background sounds
     */
    public Music ambience = Gdx.audio.newMusic(Gdx.files.internal("audio/ambience.mp3"));

    /**
     * The background image. Used for the paralax scrolling
     */
    private final TextureRegion backgroundTexture = new TextureRegion(new Texture("img/tilesets/Nebula-Aqua-Pink.png"), 0, 0, 1920, 1080);

    /**
     * The image displayed in the map popup
     */
    private final TextureRegion mapPopupTexture = new TextureRegion(new Texture("img/mapScreen.png"), 0, 0, 1920, 1080);

    /**
     * The tiled map, from a TMX file.
     */
    private final TiledMap tiledMap = new TmxMapLoader().load("auber_map.tmx");

    /**
     * The game data from a json file.
     */
    JSONObject gameData = new JSONObject(Gdx.files.internal("mapdata.json").readString());

    /**
     * Loads saved game if available.
     */
    Preferences prefs = Gdx.app.getPreferences("Auber");

    /**
     * Create arrays to store Operatives and PowerUps that remain, in order to save them.
     */
    public ArrayList<Operative> remainingOperatives = new ArrayList<>();
    public ArrayList<PowerUp> remainingPowerups = new ArrayList<>();

    /**
     * Create the game and start the background sounds playing
     *
     * @param game the AuberGame game
     * @param difficulty the difficulty of the game
     * @param resumingSave whether or not the game is resuming from a save file
     * @param demo whether the game is the demo version or not
     */
    public GameScreen (AuberGame game, Integer difficulty, Boolean resumingSave, Boolean demo){
        this.demo = demo;
        this.difficulty = difficulty;
        this.game = game;
        this.resumingSave = resumingSave;
        if(! AuberGame.isGameMuted){
            ambience.play();
            ambience.setLooping(true);
            ambience.setVolume(0.7f);
        }
    }

    
    @Override
    public void show() {
        //Create the camera
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        camera.update();

        //Create the stage and allow it to process inputs. Using an Extend Viewport for scalability of the product
        stage = new Stage(new ExtendViewport(w/3f, h/3f, camera));


        //Load the map and create it
        map = new MapRenderer(tiledMap, Gdx.files.internal("walkable_map.txt").readString());

        //Give the actors the game class
        Player.game = game;
        Systems.game = game;
        Operative.game = game;
        PlayerDemo.game = game;

        //Create the player and add it to the stage
        if(demo){
            player = new PlayerDemo(map, gameData.getJSONArray("playerStartCoords").getInt(0), gameData.getJSONArray("playerStartCoords").getInt(1), difficulty,this);
            stage.addActor(player);
        }else{
            if(! resumingSave){
                player = new Player(map, gameData.getJSONArray("playerStartCoords").getInt(0), gameData.getJSONArray("playerStartCoords").getInt(1), difficulty);
                stage.addActor(player);
            }else{
                int newx = Math.round(prefs.getInteger("auberX") / 32);
                int newy = Math.round(prefs.getInteger("auberY") / 32);

                player = new Player(map, newx, newy, difficulty, prefs.getFloat("auberSpeed"), prefs.getInteger("auberAttackDamage"), prefs.getBoolean("auberSpecialAttack"));
                stage.addActor(player);

                player.setHealth(prefs.getInteger("auberHealth", 100));
                player.setMaxHealth(prefs.getInteger("auberMaxHealth",100));

            }
        }

        //Create the Heads up display
        HUD = new HUD(player, gameData, game);
        Gdx.input.setInputProcessor(HUD);

        //Print welcome messages to the HUD
        if(this.resumingSave){
            HUD.saveNotification("Save Loaded Successfully");
            HUD.infoNotification("System Log resumed...");
         }else if(this.demo){
            HUD.infoNotification("Game in demo mode.");
            HUD.infoNotification("System Log started...");
        }else{
            HUD.infoNotification("System Log started...");
        }

        //create systems + add them to the stage
        Systems.systemsRemaining.clear();

        final String roomsDataTag = "rooms";
        final String systemCoardsDataTag = "systemCoords";

        if(! resumingSave){
            //Load the default systems from the game info
            for (int i = 0; i < gameData.getJSONArray(roomsDataTag).length(); i++) {
                if (!gameData.getJSONArray(roomsDataTag).getJSONObject(i).isNull(systemCoardsDataTag)) {
                    stage.addActor(new Systems(
                            gameData.getJSONArray(roomsDataTag).getJSONObject(i).getJSONArray(systemCoardsDataTag).getInt(0),
                            gameData.getJSONArray(roomsDataTag).getJSONObject(i).getJSONArray(systemCoardsDataTag).getInt(1),
                            map,
                            this.HUD,
                            gameData.getJSONArray(roomsDataTag).getJSONObject(i).getString("name"))
                    );
                }
            }
        }else{
            //Load the systems from the save file
            String sysSaveString = prefs.getString("remainingSystems");
            ByteArrayInputStream sysIn = new ByteArrayInputStream(Base64.getDecoder().decode(sysSaveString));
            ArrayList<ArrayList> sysInList = null;
            try {
                Object sysInObj = new ObjectInputStream(sysIn).readObject();
                sysInList = (ArrayList<ArrayList>) sysInObj;
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }

            assert sysInList != null;
            for (ArrayList arrayList : sysInList) {
                int newx = (int) arrayList.get(0);
                int newy = (int) arrayList.get(1);
                String newRoom = (String) arrayList.get(2);
                stage.addActor(new Systems(
                        newx,
                        newy,
                        map,
                        this.HUD,
                        newRoom)
                );

            }

        }


        HUD.setValues(8, 15);
        final String opDataTag = "operativeData";
        //create operatives + add them to the stage
        Operative.remainingOpers = 0;
        if(! resumingSave){
            //Create the default operatives
            for (int i = 0; i < gameData.getJSONArray(opDataTag).length(); i++) {
                Operative newOp = new Operative(
                        gameData.getJSONArray(opDataTag).getJSONArray(i).getInt(0),
                        gameData.getJSONArray(opDataTag).getJSONArray(i).getInt(1),
                        map,
                        this.HUD,
                        this.difficulty,
                        gameData.getJSONArray(opDataTag).getJSONArray(i).getInt(2),
                        this
                );
                stage.addActor(newOp);
                this.remainingOperatives.add(newOp);
            }
        }else{
            //Create the saved operatives
            String operSaveString = prefs.getString("remainingOperatives");
            ByteArrayInputStream operIn = new ByteArrayInputStream(Base64.getDecoder().decode(operSaveString));
            ArrayList<ArrayList> operInList = null;
            try {
                Object operInObj = new ObjectInputStream(operIn).readObject();
                operInList = (ArrayList<ArrayList>) operInObj;
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }

            assert operInList != null;
            for (ArrayList arrayList : operInList) {
                int newx = Math.round((int) arrayList.get(0) / 32);
                int newy = Math.round((int) arrayList.get(1) / 32);
                int newAbility = (int) arrayList.get(2);
                Operative newOp = new Operative(
                        newx,
                        newy,
                        map,
                        this.HUD,
                        this.difficulty,
                        newAbility,
                        this
                );
                stage.addActor(newOp);
                this.remainingOperatives.add(newOp);
            }
        }
        HUD.setValues(8, 15);

        // Adding powerups to the map
        final String powerupDataTag = "powerupData";
        if(! resumingSave){
            //Create the default power ups
            for (int i = 0; i < gameData.getJSONArray(powerupDataTag).length(); i++) {
                PowerUp newPUP = new PowerUp(
                        map,
                        gameData.getJSONArray(powerupDataTag).getJSONArray(i).getInt(0),
                        gameData.getJSONArray(powerupDataTag).getJSONArray(i).getInt(1),
                        gameData.getJSONArray(powerupDataTag).getJSONArray(i).getInt(2),
                        this.HUD
                );
                stage.addActor(newPUP);
                this.remainingPowerups.add(newPUP);
            }

        }else{
            //Create the saved power ups
            String pupSaveString = prefs.getString("remainingPowerups");
            ByteArrayInputStream pupIn = new ByteArrayInputStream(Base64.getDecoder().decode(pupSaveString));
            ArrayList<ArrayList> pupInList = null;
            try {
                Object pupInObj = new ObjectInputStream(pupIn).readObject();
                pupInList = (ArrayList<ArrayList>) pupInObj;
            }catch (ClassNotFoundException | IOException e){
                e.printStackTrace();
            }

            assert pupInList != null;

            for (ArrayList arrayList : pupInList){
                float newx = (float) arrayList.get(0);
                float newy = (float) arrayList.get(1);
                int newtype = (int) arrayList.get(2);
                PowerUp pUp = new PowerUp(map, newx, newy, newtype, this.HUD);
                stage.addActor(pUp);
                this.remainingPowerups.add(pUp);
            }

        }

        //The first random operative for the demo player to attack
        //After the first, the demo player will auto-generate the next one
        if(demo){
            Random rand = new Random();
            PlayerDemo.randOper = rand.nextInt(remainingOperatives.size());
        }

    }

    @Override
    public void render(float delta) {
        //Set the background colour & draw the stage
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

        //Move the camera to follow the player
        /*
          The lerp of the camera, used for linear interpolation on the player movement to calculate the camera position
         */
        float cameraLerp = 2f;
        camera.position.x += (player.getX() + delta - camera.position.x) * cameraLerp * delta;
        camera.position.y += (player.getY() + delta - camera.position.y) * cameraLerp * delta;
        camera.update();
        map.setView(camera);

        //Render the objects. Render the bg layers, then the player, then the foreground layers to give the effect of
        //3d (as the player can go behind certain objects)
        batch.begin();
        batch.draw(backgroundTexture, 0, 0);
        batch.end();
        map.render(new int[]{0,1,2,3,4,5});
        stage.draw();
        map.render(new int[]{6,7});

        //Draw the HUD
        HUD.draw();

        //Save the game if required
        if(needToSave){
            needToSave = false;
            try {
                saveGame();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Exit the game if required
        if(needToExit){
            needToExit = false;
            exitGame();
        }

        //Perform this if Auber has won
        if(gameOverWin){
            gameOverWin = false;
            wonGame();
        }

        //Perform this if Auber has lost
        if(gameOverLose){
            gameOverLose = false;
            lostGame();
        }

        //Show the map if the M key is pressed and (Assessment 2) making it scalable
        if (Gdx.input.isKeyPressed(Keys.M)){
            mapSpriteBatch.begin();
            mapSpriteBatch.draw(mapPopupTexture, 0, 0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
            mapSpriteBatch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        //Update the viewport side, and recenter it.
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    }

    @Override
    public void dispose(){
        ambience.dispose();
        batch.dispose();
        mapSpriteBatch.dispose();
        map.dispose();
        stage.dispose();
        HUD.dispose();
    }

    public void exitGame(){
        //Code required to execute when the game is exiting
        ambience.stop();
        game.setScreen(new TitleScreen(game, false));
    }

    public void wonGame(){
        //Code required to execute when Auber has won the game
        ambience.stop();
        map.autoLeave(player);
        game.setScreen(new GameEndScreen(game, true));
    }

    public void lostGame(){
        //Code required to execute when Auber has lost the game
        ambience.stop();
        map.autoLeave(player);
        game.setScreen(new GameEndScreen(game, false));
    }

    public void saveGame() throws IOException {
        //Save the current state of the game to be resumed later
        if(demo){
            //Prevent saving in demo mode
            //This is a precaution as the user shouldn't be able to save the demo anyway
            HUD.saveNotification("Can't save game in demo mode!");
        }else{
            //Clear any previous saves
            prefs.clear();
            prefs.flush();

            //Save the remaining operatives
            ArrayList<ArrayList> savedOperatives = new ArrayList<>();
            for(Operative remainingOper : remainingOperatives){
                if(! remainingOper.dead){
                    ArrayList<Integer> thisOper = new ArrayList<>();
                    thisOper.add((int) remainingOper.getX());
                    thisOper.add((int) remainingOper.getY());
                    thisOper.add(remainingOper.specialAbilityID);
                    savedOperatives.add(thisOper);
                }
            }
            ByteArrayOutputStream operOut = new ByteArrayOutputStream();
            new ObjectOutputStream(operOut).writeObject(savedOperatives);
            String operSaveString = Base64.getEncoder().encodeToString(operOut.toByteArray());
            prefs.putString("remainingOperatives", operSaveString);

            //Save data about Auber
            prefs.putInteger("auberX", (int) player.getX());
            prefs.putInteger("auberY", (int) player.getY());
            prefs.putInteger("auberHealth", player.getHealth());
            prefs.putInteger("auberMaxHealth", player.getMaxHealth());
            prefs.putFloat("auberSpeed", player.getSpeed());
            prefs.putInteger("auberAttackDamage", player.getDamage());
            prefs.putBoolean("auberSpecialAttack", player.getSpecialAttack());

            //Save the remaining systems
            ArrayList<ArrayList<Object>> savedSystems = new ArrayList<>();
            for(Systems remainingSystem : Systems.systemsRemaining){
                ArrayList<Object> remainingSys = new ArrayList<>();
                remainingSys.add(remainingSystem.gridX);
                remainingSys.add(remainingSystem.gridY);
                remainingSys.add(remainingSystem.roomName);
                savedSystems.add(remainingSys);
            }
            ByteArrayOutputStream sysOut = new ByteArrayOutputStream();
            new ObjectOutputStream(sysOut).writeObject(savedSystems);
            String sysSaveString = Base64.getEncoder().encodeToString(sysOut.toByteArray());
            prefs.putString("remainingSystems", sysSaveString);

            //Save the remaining powerups
            ArrayList<ArrayList<Object>> savedPowerUps = new ArrayList<>();
            for (PowerUp remainingPowerUp : remainingPowerups){
                if(! remainingPowerUp.collected){
                    ArrayList<Object> remainingPUP = new ArrayList<>();
                    remainingPUP.add(remainingPowerUp.xPos);
                    remainingPUP.add(remainingPowerUp.yPos);
                    remainingPUP.add(remainingPowerUp.powerType);
                    savedPowerUps.add(remainingPUP);
                }
            }
            ByteArrayOutputStream pupOut = new ByteArrayOutputStream();
            new ObjectOutputStream(pupOut).writeObject(savedPowerUps);
            String pupSaveString = Base64.getEncoder().encodeToString(pupOut.toByteArray());
            prefs.putString("remainingPowerups", pupSaveString);

            //Indicate that the save file is valid to be resumed from
            prefs.putBoolean("canBeResumed", true);

            //Save the difficulty of the game
            prefs.putInteger("gameDifficulty", this.difficulty);

            //Ensure the save is written to disk
            prefs.flush();

            //Indicate to the user that the save has been successful
            HUD.saveNotification("Game Saved Successfully");
            HUD.saveNotification("Press ESC to exit game");
        }

    }


    /**
     * Used to drop a new powerUp.
     * @param x x pos
     * @param y y pos
     * @param type powerup type
     *
     */
    void createPowerUp(Float x, Float y,int type){
        //Drop Powerup on death
        PowerUp pUp;
        pUp = new PowerUp(map, x, y,type, this.HUD);
        stage.addActor(pUp);
        this.remainingPowerups.add(pUp);
    }

    //Create Smoke Animation
    void createSmoke(float x, float y, float xVel,float yVel){
        stage.addActor(new Smoke(x, y, xVel, yVel));
    }
}
