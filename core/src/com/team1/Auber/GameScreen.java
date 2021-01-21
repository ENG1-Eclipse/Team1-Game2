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
import org.json.*;
import java.util.Base64;

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
    public static Boolean needToSave = false;
    public static Boolean needToExit = false;

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

    Preferences prefs = Gdx.app.getPreferences("Auber");

    public ArrayList<Operative> remainingOperatives = new ArrayList<>();

    /**
     * Create the game and start the background sounds playing
     *
     * @param game the AuberGame game
     */
    public GameScreen (AuberGame game, Integer difficulty, Boolean resumingSave){
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

        //Create the player and add it to the stage

        if(! resumingSave){
            player = new Player(map, gameData.getJSONArray("playerStartCoords").getInt(0), gameData.getJSONArray("playerStartCoords").getInt(1), difficulty);
            stage.addActor(player);
        }else{
            int newx = Math.round(prefs.getInteger("auberX") / 32);
            int newy = Math.round(prefs.getInteger("auberY") / 32);

            player = new Player(map, newx, newy, difficulty);
            stage.addActor(player);

            player.setHealth(prefs.getInteger("auberHealth", 100));
            player.setMaxHealth(prefs.getInteger("auberMaxHealth",100));


        }



        //Create the Heads up display
        HUD = new HUD(player, gameData, game);
        Gdx.input.setInputProcessor(HUD);


        if(this.resumingSave){
         HUD.saveNotification("Save Loaded Successfully");
         HUD.saveNotification("Welcome Back, Auber");
         }else{
            HUD.infoNotification("System Log started...");
        }

        //create systems + add them to the stage
        Systems.systemsRemaining.clear();
        if(! resumingSave){
            for (int i = 0; i < gameData.getJSONArray("rooms").length(); i++) {
                if (!gameData.getJSONArray("rooms").getJSONObject(i).isNull("systemCoords")) {
                    stage.addActor(new Systems(
                            gameData.getJSONArray("rooms").getJSONObject(i).getJSONArray("systemCoords").getInt(0),
                            gameData.getJSONArray("rooms").getJSONObject(i).getJSONArray("systemCoords").getInt(1),
                            map,
                            this.HUD,
                            gameData.getJSONArray("rooms").getJSONObject(i).getString("name"))
                    );
                }
            }
        }else{
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

        //create operatives + add them to the stage
        Operative.remainingOpers = 0;
        if(! resumingSave){
            for (int i = 0; i < gameData.getJSONArray("operativeData").length(); i++) {
                Operative newOp = new Operative(
                        gameData.getJSONArray("operativeData").getJSONArray(i).getInt(0),
                        gameData.getJSONArray("operativeData").getJSONArray(i).getInt(1),
                        map,
                        this.HUD,
                        this.difficulty,
                        gameData.getJSONArray("operativeData").getJSONArray(i).getInt(2)
                );
                stage.addActor(newOp);
                this.remainingOperatives.add(newOp);
            }

            //HUD.setValues(Operative.remainingOpers, Systems.systemsRemaining.size());
        }else{
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
                        newAbility
                );
                stage.addActor(newOp);
                this.remainingOperatives.add(newOp);
            }
        }
        HUD.setValues(8, 15);

        //--------------------------------------- P O W E R  U P S ---------------------------------------------------
        // Adding powerups to the map
        PowerUp pUp;
        /**
         * Type    : Health Boost
         * Location: Bathroom  
         * Xpos    : 43
         * Ypos    : 33
         */
        pUp = new PowerUp(map, 43, 33,0);
        stage.addActor(pUp);
        /**
         * Type    : Health Boost
         * Location: Stern Corridor  
         * Xpos    : 17
         * Ypos    : 13
         */
        pUp = new PowerUp(map, 17, 13,0);
        stage.addActor(pUp);

        /**
         * Type    : Speed Boost
         * Location: Lab
         * Xpos    : 48
         * Ypos    : 42
         */
        pUp = new PowerUp(map, 48, 42,1);
        stage.addActor(pUp);

        /**
         * Type    : Speed Boost
         * Location: Storage Room
         * Xpos    : 16
         * Ypos    : 6
         */
        pUp = new PowerUp(map, 16, 6,1);
        stage.addActor(pUp);

        /**
         * Type    : Special Attack
         * Location: MedBay
         * Xpos    : 18
         * Ypos    : 33
         */
        pUp = new PowerUp(map, 18, 33,3);
        stage.addActor(pUp);
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

        if(needToSave){
            needToSave = false;
            try {
                saveGame();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(needToExit){
            needToExit = false;
            exitGame();
        }

        //Show the map if the M key is pressed
        if (Gdx.input.isKeyPressed(Keys.M)){
            mapSpriteBatch.begin();
            mapSpriteBatch.draw(mapPopupTexture, 0, 0);
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
        ambience.stop();
        game.setScreen(new TitleScreen(game, false));
    }

    public void saveGame() throws IOException {
        prefs.clear();
        prefs.flush();

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

        prefs.putInteger("auberX", (int) player.getX());
        prefs.putInteger("auberY", (int) player.getY());
        prefs.putInteger("auberHealth", player.getHealth());
        prefs.putInteger("auberMaxHealth", player.getMaxHealth());

        ArrayList<ArrayList> savedSystems = new ArrayList<>();
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

        prefs.putBoolean("canBeResumed", true);

        prefs.putInteger("gameDifficulty", this.difficulty);

        prefs.flush();

        HUD.saveNotification("Game Saved Successfully");
        HUD.saveNotification("Press ESC to exit game");

    }
}
