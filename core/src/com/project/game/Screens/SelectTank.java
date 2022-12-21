package com.project.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.project.game.SastaTankStars;

public class SelectTank implements Screen {

    SastaTankStars Game;
    Texture tank1;
    Texture tank2;
    Texture tank3;


    // Message to Show Status
    String popUP;
    float X_popUP,Y_popUP; // Message Coordinates
    float X_tank1;
    float X_tank2;
    float X_tank3;
    float Y_tank;
    float width_tank1;
    float width_tank2;
    float width_tank3;
    float height_tank;

    int ID_tank1=-1;

    Rectangle tank1Bounds,tank2Bounds,tank3Bounds;

    public SelectTank(SastaTankStars game) {
        X_popUP=100;
        Y_popUP=Gdx.graphics.getHeight()-200;
        popUP="Player 01 : Choose TANK 01";
        this.Game = game;
        tank1 = new Texture("tank1.png");
        tank2 = new Texture("tank3.png");
        tank3=new Texture("tank5.png");

        height_tank = Gdx.graphics.getHeight() / 15f;
        float tankratio1 = (float) tank1.getWidth() / tank1.getHeight();
        float tankratio2=(float)tank2.getWidth()/ tank2.getHeight();
        float tankratio3=(float)tank3.getWidth()/ tank3.getHeight();
        width_tank1 = tankratio1 * height_tank;
        width_tank2=tankratio2*height_tank;
        width_tank3=tankratio3*height_tank;

        Y_tank=Gdx.graphics.getHeight()/2f-height_tank/2;


        X_tank2=Gdx.graphics.getWidth()/2f-width_tank2;
        X_tank1 =X_tank2-50-width_tank1 ;
        X_tank3=X_tank2+50+width_tank2;

        tank1Bounds = new Rectangle(X_tank1, Y_tank, width_tank1, height_tank);
        tank2Bounds = new Rectangle(X_tank2, Y_tank, width_tank2, height_tank);
        tank3Bounds= new Rectangle(X_tank3, Y_tank, width_tank3, height_tank);

    }


    @Override
    public void show() {

    }
    @Override
    public void render(float delta) {
        ScreenUtils.clear(38 / 255f, 31 / 255f, 88 / 255f, 1);


        Game.batch.begin();

        Game.batch.draw(tank1, X_tank1, Y_tank, width_tank1, height_tank);
        Game.batch.draw(tank2, X_tank2, Y_tank,width_tank2,height_tank);
        Game.batch.draw(tank3, X_tank3, Y_tank,width_tank3,height_tank);

        Game.font32.draw(Game.batch, popUP,X_popUP,Y_popUP);
        Game.batch.end();

        handleTouch();
    }
    private void handleTouch() {
        if (Gdx.input.justTouched()) {
            // Capture Touch Position
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
//            camera.unproject(touchPos);


            // If touched Tank 1
            if (tank1Bounds.contains(touchPos.x, touchPos.y)) {
                System.out.println("Tank1");

                // Chosen as Player 1's Tank
                if(ID_tank1==-1){
                    ID_tank1=0;
                    popUP="Choose TANK 01";
                }

                // Chosen as Player 2's Tank
                else {
                    Game.setScreen(new PlayScreen(Game, "", ID_tank1,0));
                    dispose();
                }
            }

            if (tank2Bounds.contains(touchPos.x, touchPos.y)) {
                System.out.println("Tank2");
                if(ID_tank1==-1) {
                    ID_tank1=1;
                    popUP="Choose TANK 02";
                }
                else {
                    Game.setScreen(new PlayScreen(Game, "", ID_tank1,1));
                    dispose();
                }
            }

            if (tank3Bounds.contains(touchPos.x, touchPos.y)) {
                System.out.println("Tank3");
                if(ID_tank1==-1) {
                    ID_tank1=2;
                    popUP="Choose TANK 02";
                }
                else {
                    Game.setScreen(new PlayScreen(Game, "", ID_tank1,2));
                    dispose();
                }
            }
        }
    }
    @Override
    public void resize(int width, int height) {

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

    }


}
