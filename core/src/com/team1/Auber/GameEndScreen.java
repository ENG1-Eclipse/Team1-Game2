package com.team1.Auber;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;

/**
 * GameEndScreen is an extension of {@link com.badlogic.gdx.ScreenAdapter} to create and render the end game screen.
 *
 * @author Bogdan Bodnariu-Lescinschi
 */
public class GameEndScreen extends ScreenAdapter {
    public AuberGame game;
    private boolean playerWon;
    private Stage stage;
    private SpriteBatch batch = new SpriteBatch();

    Preferences prefs = Gdx.app.getPreferences("Auber");

    /**
     * The menu music
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
     * Create the a end game screen, with either a game over message or a won game message
     * @param game the AuberGame instance
     * @param playerWon whether the player won the game
     */
    public GameEndScreen (AuberGame game, boolean playerWon){
        this.game = game;
        this.playerWon = playerWon;

        prefs.clear();
        prefs.flush();
    }

    @Override
    public void show() {
        //Create the stage
        stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()));
        Gdx.input.setInputProcessor(stage);

        //Create the table = used for the layout
        Table table = new Table();
        table.setFillParent(true);

        //Load the correct image depending on if the player won the game
        if(playerWon){
            Texture gameWin = new Texture(Gdx.files.internal("img/gameWin.png"));
            Image win = new Image(gameWin);
            table.add(win).width(win.getWidth()*2.3f).height(win.getHeight()*2.3f).pad(40).align(Align.center);
        } else {
            Texture gameEnd = new Texture(Gdx.files.internal("img/gameOver.png"));
            Image end = new Image(gameEnd);
            table.add(end).width(end.getWidth()*2.5f).height(end.getHeight()*2.5f).pad(40).align(Align.center);
        }
        table.row();

        //Start the music playing
        if(! AuberGame.isGameMuted){
            menuMusic.play();
        }
        menuMusic.setVolume(0.1f);

        //Load and draw the main menu button and creat the click listener
        ImageButton.ImageButtonStyle menuStyle =  new ImageButton.ImageButtonStyle();
        menuStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/menuButtonInactive.png"))));
        menuStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/menuButtonActive.png"))));
        menuStyle.over = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/menuButtonActive.png"))));
        ImageButton menuButton = new ImageButton(menuStyle);
        table.add(menuButton).pad(30).align(Align.center);
        menuButton.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                //As per libGDX docs this is needed to return true for the touchup event to trigger
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if(! AuberGame.isGameMuted){
                    menuSelect.play(0.2f);
                }

                menuMusic.stop();
                game.setScreen(new TitleScreen(game, false));

            }
        });

        //Add the table to the stage
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        //Set the background colour & draw the stage
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Draw the stage & batch
        batch.begin();
        batch.draw(backgroundTexture, 0, 0);
        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }
    
    @Override
    public void dispose () {
        menuSelect.dispose();
        menuMusic.dispose();
        batch.dispose();
        stage.dispose();
    }
    
}