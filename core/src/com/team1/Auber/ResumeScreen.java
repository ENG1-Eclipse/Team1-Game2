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
import com.badlogic.gdx.Input.Keys;

/**
 * Resume Screen is an extension of {@link com.badlogic.gdx.ScreenAdapter} to create and render the resume screen.
 *
 * @author Harry Smith (Team 1)
 */
public class ResumeScreen extends ScreenAdapter {

    public AuberGame game;
    private Stage stage;
    private SpriteBatch batch = new SpriteBatch();

    /**
     * Load the background image
     */
    private final TextureRegion backgroundTexture = new TextureRegion(new Texture("img/tilesets/Nebula-Aqua-Pink.png"), 0, 0, 1920, 1080);

    /**
     * The sound made when the user selects a button
     */
    private final Sound menuSelect = Gdx.audio.newSound(Gdx.files.internal("audio/menuSelect.ogg"));

    //The difficulty of the resumed game
    public int resumingDifficulty = 0;

    //Saved Game
    Preferences prefs = Gdx.app.getPreferences("Auber");

    /**
     *
     * @param game
     * @param resumingDifficulty the difficulty of the resumed game
     */
    public ResumeScreen(AuberGame game, int resumingDifficulty){
        this.game = game;
        this.resumingDifficulty = resumingDifficulty;
    }

    @Override
    public void show() {
        //Create the stage and allow it to process inputs. Using an Extend Viewport for scalability of the product
        stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()));
        Gdx.input.setInputProcessor(stage);

        //Create & expand the table
        Table table = new Table();
        table.top();
        table.setFillParent(true);

        //Create and add the logo
        Texture logoTexture = new Texture(Gdx.files.internal("img/menu/auberLogo.png"));
        Image logo = new Image(logoTexture);
        table.add(logo).width(914.9f).height(270.9f).pad(20).align(Align.top);
        table.row();

        //Create and add the instructions image
        Texture resumeTexture = new Texture(Gdx.files.internal("img/menu/resume.png"));
        Image resumeImg = new Image(resumeTexture);
        table.add(resumeImg).pad(10).fillY().align(Align.center);
        table.row();

        //Create and add the YES button and add the event listener
        ImageButton.ImageButtonStyle yesStyle =  new ImageButton.ImageButtonStyle();
        yesStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/yesInactive.png"))));
        yesStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/yesActive.png"))));
        yesStyle.over = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/yesActive.png"))));
        ImageButton yesButton = new ImageButton(yesStyle);
        //yesButton.setPosition(20,20);
        yesButton.addListener(new InputListener(){
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
                game.setScreen(new GameScreen(game, resumingDifficulty, true, false));

            }
        });

        //Create and add the NO button and add the event listener
        ImageButton.ImageButtonStyle noStyle =  new ImageButton.ImageButtonStyle();
        noStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/noInactive.png"))));
        noStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/noActive.png"))));
        noStyle.over = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("img/menu/noActive.png"))));
        ImageButton noButton = new ImageButton(noStyle);
        //noButton.setPosition(20,20);
        noButton.addListener(new InputListener(){
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
                //Erase the currently saved game
                prefs.clear();
                prefs.flush();
                game.setScreen(new DifficultyScreen(game));

            }
        });

        //Add the table and button to the stage
        table.add(yesButton).center().pad(5);
        table.row();
        table.add(noButton).center().pad(5);
        table.row();
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

        //If the escape key is pressed go back to the main menu
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)){
            game.setScreen(new TitleScreen(game, false));
        }
    }

    @Override
    public void resize(int width, int height) {
        //Update the viewport side, and recenter it.
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

}