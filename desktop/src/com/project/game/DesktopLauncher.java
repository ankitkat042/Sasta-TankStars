package com.project.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.project.game.SastaTankStars;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setWindowedMode(1920, 1080);
		config.setResizable(false);
		config.setTitle("Sasta com.project.game.Tank Stars");
		SastaTankStars game = SastaTankStars.getInstance();
		new Lwjgl3Application(game, config);
	}
}
