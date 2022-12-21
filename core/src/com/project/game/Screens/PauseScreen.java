package com.project.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.project.game.SastaTankStars;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
public class PauseScreen implements Screen {

    private final SastaTankStars game;
//    public final String menuScreen = "menuscreen.png";

    private Stage stage;
    private Skin skin;
    //    public static Texture texture_back;
//    public TextureRegion textureRegion_back;
    private TextButton buttonSaveGame, buttonResume, buttonExit;

    private ShapeRenderer shapeRenderer;

    public PauseScreen(final SastaTankStars game) {

        this.game = game;
        this.stage = new Stage(new FitViewport(SastaTankStars.V_WIDTH, SastaTankStars.V_HEIGHT, game.camera));
        this.shapeRenderer = new ShapeRenderer();
//        this.texture_back = new Texture(Gdx.files.internal("menuscreen.png"));

//        this.textureRegion_back = new TextureRegion(texture_back, 0, 0, 1920, 1080);
        game.camera.setToOrtho(false, SastaTankStars.V_WIDTH, SastaTankStars.V_HEIGHT);
    }
    @Override
    public void show() {
        System.out.println("PAUSE MENU");
        Gdx.input.setInputProcessor(stage);
        stage.clear();
        this.skin = new Skin();
        this.skin.addRegions(game.assets.get("ui/uiskin.atlas", TextureAtlas.class));
        this.skin.add("default-font", game.font32);
        this.skin.load(Gdx.files.internal("ui/uiskin.json"));

//        initButtons();
    }

    private void update(float delta) {
        stage.act(delta);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(game.camera.combined);
//        game.batch.begin();
//        game.batch.draw(textureRegion_back, 0, 0, SastaTankStars.V_WIDTH, SastaTankStars.V_HEIGHT);
//        game.batch.end();

        stage.draw();
        initButtons();
        update(delta);

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }


    private void initButtons() {
        buttonSaveGame = new TextButton("Save Game", skin, "default");
        buttonSaveGame.setPosition(960-120, 540+100);
        buttonSaveGame.setSize(280, 60);
        buttonSaveGame.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonSaveGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });
        buttonResume = new TextButton("Resume", skin, "default");
        buttonResume.setPosition(840, 540);
        buttonResume.setSize(280, 60);
        buttonResume.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonResume.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.playScreen);
            }
        });


        buttonExit = new TextButton("Exit", skin, "default");
        buttonExit.setPosition(840, 540-100);
        buttonExit.setSize(280, 60);
        buttonExit.addAction(sequence(alpha(0), parallel(fadeIn(.5f), moveBy(0, -20, .5f, Interpolation.pow5Out))));
        buttonExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.mainMenuScreen);
            }
        });

        stage.addActor(buttonSaveGame);
        stage.addActor(buttonResume);
        stage.addActor(buttonExit);
    }
}