package com.team1.Auber;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


/**
 * AuberGame is responsible for handling multiple screens and provides some
 * helper methods for this purpose ({@link com.badlogic.gdx.Game#setScreen}),
 * alongside an implementation of {@link com.badlogic.gdx.Game} for use.
 *
 * @author Robert Watts (Team 4)
 * @author Bogdan Bodnariu-Lescinschi (Team 4)
 * @author Harry Smith (Team 1)
 */
public class AuberGame extends Game {

	SpriteBatch batch;
	Skin skin;

	public static Boolean isGameMuted = false;

	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new TitleScreen(this, false));
	}

	@Override
	public void dispose () {
		batch.dispose();
	}

}