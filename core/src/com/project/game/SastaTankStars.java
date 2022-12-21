package com.project.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.project.game.Screens.*;

public class SastaTankStars extends Game {


//	public SpriteBatch batch;
	public static final float V_WIDTH = 1920;
	public static final float V_HEIGHT = 1080;
	public OrthographicCamera camera;
	public SpriteBatch batch;

	public BitmapFont font32;
	public AssetManager assets;

	public LoadingScreen loadingScreen;
	public SplashScreen splashScreen;
	public MainMenuScreen mainMenuScreen;
	public PauseScreen pauseScreen;
	public PlayScreen playScreen;
	public SelectTank selectTank;

	private static SastaTankStars instance=null;

	public static SastaTankStars getInstance() {
		if (instance == null) {
			instance = new SastaTankStars();
		}
		return instance;
	}
	private SastaTankStars() {
	}
	@Override
	public void create () {
		assets = new AssetManager();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, V_WIDTH, V_HEIGHT);
		batch = new SpriteBatch();
		initFonts();

		loadingScreen = new LoadingScreen(this);
		splashScreen = new SplashScreen(this);
		mainMenuScreen = new MainMenuScreen(this);
		pauseScreen = new PauseScreen(this);
		selectTank = new SelectTank(this);


		this.setScreen(loadingScreen);

	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose () {
		batch.dispose();
		assets.dispose();
		font32.dispose();
		loadingScreen.dispose();
		splashScreen.dispose();
		mainMenuScreen.dispose();

	}
	private void initFonts() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Arcon.otf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 32;
		parameter.color = Color.WHITE;
		font32 = generator.generateFont(parameter);
	}


}
