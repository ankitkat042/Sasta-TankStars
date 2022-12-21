package com.project.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.project.game.SastaTankStars;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;


public class SplashScreen implements Screen {
    private SastaTankStars game;
    private Stage stage;
    private Image logo;


    public SplashScreen(SastaTankStars game) {
        this.game = game;
        this.stage = new Stage(new FitViewport(800, 600, game.camera));
        this.stage = new Stage();



    }


    @Override
    public void show() {
        System.out.println("logo");
        Gdx.input.setInputProcessor(stage);

        Runnable transitionRunnable = new Runnable() {
            @Override
            public void run() {
                game.setScreen(game.mainMenuScreen);
            }
        };
        Texture logoTexture = new Texture(Gdx.files.internal("logo.png"));
        logo = new Image(logoTexture);
        logo.setOrigin((logo.getWidth() / 2), (logo.getHeight() / 2));
        logo.setPosition(0,0);
        logo.addAction(sequence(alpha(0f),scaleTo(.1f,.1f),
                parallel(fadeIn(2f, Interpolation.pow2),
                        scaleTo(2f, 2f, 2.5f, Interpolation.pow5),
                        moveTo(stage.getWidth()/2-150, stage.getHeight()/2-75, 2f, Interpolation.swing)),
                delay(1.5f), fadeOut(1.25f), run(transitionRunnable)));

        stage.addActor(logo);

    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update(delta);
        stage.draw();
        game.batch.begin();
//        game.font.draw(game.batch, "Sasta Tank Stars", 100, 100);
        game.batch.end();

    }

    public void update(float delta) {
        stage.act(delta);
        stage.draw();
    }
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, false);
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


    }
}
