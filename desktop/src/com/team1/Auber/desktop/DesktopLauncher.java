package com.team1.Auber.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.team1.Auber.AuberGame;

import java.awt.*;

/**
 * the desktop launcher holder with the main function
 */
public class DesktopLauncher {

	public static void main (String[] arg) {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Auber";
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		config.width = screenSize.width;
		config.height = screenSize.height;
		config.fullscreen = true;
		config.forceExit = false;
		new LwjglApplication(new AuberGame(), config);
		
	}
}