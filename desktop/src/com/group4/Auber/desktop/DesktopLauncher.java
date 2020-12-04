package com.group4.Auber.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.group4.Auber.AuberGame;

/**
 * the desktop launcher holder with the main function
 *
 * @author Robert Watts
 * @author Bogdan Bodnariu-Lescinschi
 */
public class DesktopLauncher {
	public static void main (String[] arg) {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Auber - Group 4";
		config.width = 1920;
		config.height = 1080;
		config.fullscreen = true;
		config.forceExit = false;
		new LwjglApplication(new AuberGame(), config);
		
	}
}