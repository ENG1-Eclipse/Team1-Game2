package com.team1.Auber;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Input.Keys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * TitleScreen is an extension of {@link com.badlogic.gdx.ScreenAdapter} to create and render the title screen.
 *
 * @author Harry Smith (Team 1)
 */
public class DifficultyScreen extends ScreenAdapter {

    public AuberGame game;
    private Stage stage;
    private final SpriteBatch batch = new SpriteBatch();


    /**
     * The background music for the menu
     */
    public static Music menuMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/menuMusic.mp3"));


    /**
     * Load the background image
     */
    private final TextureRegion backgroundTexture = new TextureRegion(new Texture("img/tilesets/Nebula-Aqua-Pink.png"), 0, 0, 1920, 1080);

    /**
     * The sound made when the user selects a button
     */
    private final Sound menuSelect = Gdx.audio.newSound(Gdx.files.internal("audio/menuSelect.ogg"));


    /**
     * Create the difficulty menu.
     * @param game the AuberGame instance
     */
    public DifficultyScreen (AuberGame game){
        this.game = game;
    }

    @Override
    public void show() {

        //Create the stage and allow it to process inputs. Using an Extend Viewport for scalablity of the product
        stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()));
        Gdx.input.setInputProcessor(stage);

        //Create the table and expand it to fill the window
        Table table = new Table();
        table.setFillParent(true);

        //Create the logo and add it to the table
        Texture logoTexture = new Texture(Gdx.files.internal("img/menu/auberLogo.png"));
        Image logo = new Image(logoTexture);
        table.add(logo).pad(10).fillY().align(Align.center);
        table.row();

        //Create the easy button, add it to the table with its click event
        ImageButton.ImageButtonStyle easyStyle =  new ImageButton.ImageButtonStyle();
        easyStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/easyButtonInactive.png"))));
        easyStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/easyButtonActive.png"))));
        easyStyle.over = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/easyButtonActive.png"))));
        ImageButton easyButton = new ImageButton(easyStyle);
        table.add(easyButton).center().pad(5);
        table.row();

        easyButton.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                //As per libGDX docs this is needed to return true for the touchup event to trigger
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                //Stop the music playing and change the screen to the game screen
                if(! AuberGame.isGameMuted){
                    menuMusic.stop();
                    GameEndScreen.menuMusic.stop();
                    menuSelect.play(0.2f);
                }

                /** Difficulty of 0 represents EASY mode **/
                game.setScreen(new GameScreen(game, 0, false, false));
            }
        });

        //Create the normal button, add it to the table with its click event
        ImageButton.ImageButtonStyle normStyle =  new ImageButton.ImageButtonStyle();
        normStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/normalButtonInactive.png"))));
        normStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/normalButtonActive.png"))));
        normStyle.over = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/normalButtonActive.png"))));
        ImageButton normButton = new ImageButton(normStyle);
        table.add(normButton).center().pad(5);
        table.row();

        normButton.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                //As per libGDX docs this is needed to return true for the touchup event to trigger
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                //Stop the music playing and change the screen to the game screen
                if(! AuberGame.isGameMuted){
                    menuMusic.stop();
                    GameEndScreen.menuMusic.stop();
                    menuSelect.play(0.2f);
                }

                /** Difficulty of 1 represents NORMAL mode **/
                game.setScreen(new GameScreen(game, 1, false, false));
            }
        });

        //Create the hard button, add it to the table with its click event
        ImageButton.ImageButtonStyle hardStyle =  new ImageButton.ImageButtonStyle();
        hardStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/hardButtonInactive.png"))));
        hardStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/hardButtonActive.png"))));
        hardStyle.over = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/hardButtonActive.png"))));
        ImageButton hardButton = new ImageButton(hardStyle);
        table.add(hardButton).center().pad(5);
        table.row();

        hardButton.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                //As per libGDX docs this is needed to return true for the touchup event to trigger
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                //Stop the music playing and change the screen to the game screen
                if(! AuberGame.isGameMuted){
                    menuMusic.stop();
                    GameEndScreen.menuMusic.stop();
                    menuSelect.play(0.2f);
                }
                /** Difficulty of 2 represents HARD mode **/
                game.setScreen(new GameScreen(game, 2, false, false));
            }
        });

        //Create the Demo button, add it to the table with its click event
        ImageButton.ImageButtonStyle demoStyle =  new ImageButton.ImageButtonStyle();
        demoStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/demoButtonInactive.png"))));
        demoStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/demoButtonActive.png"))));
        demoStyle.over = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/demoButtonActive.png"))));
        ImageButton demoButton = new ImageButton(demoStyle);
        table.add(demoButton).center().pad(5);
        table.row();

        demoButton.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                //As per libGDX docs this is needed to return true for the touchup event to trigger
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                //Stop the music playing and change the screen to the game screen
                if(! AuberGame.isGameMuted){
                    menuMusic.stop();
                    GameEndScreen.menuMusic.stop();
                    menuSelect.play(0.2f);
                }
                /** Demo Mode Enabled - 0.25 chance of being NORMAL mode, 0.75 of being EASY. **/
                ArrayList<Integer> randomDiffList = new ArrayList<>(Arrays.asList(0,0,0,1));
                Random rand = new Random();
                int randDiff = rand.nextInt(4);
                int selectedDiff = randomDiffList.get(randDiff);
                game.setScreen(new GameScreen(game, selectedDiff, false,true));
            }
        });



        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        //Set the background colour & draw the stage
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Draw the batch and stage
        batch.begin();
        batch.draw(backgroundTexture, 0, 0);
        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        if (Gdx.input.isKeyPressed(Keys.ESCAPE)){
            game.setScreen(new TitleScreen(game, false));
        }
    }

    @Override
    public void resize(int width, int height) {
        //Update the viewport side, and recenter it.
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void dispose () {
        menuSelect.dispose();
        menuMusic.stop();
        menuMusic.dispose();
        batch.dispose();
        stage.dispose();
    }

}