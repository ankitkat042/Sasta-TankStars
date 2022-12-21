package com.project.game.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.project.game.SastaTankStars;

public class Hud {
    public Stage stage;
    private Viewport viewport;
    private String player1Name;
    private Integer player1Health;
    private String player2Name;
    private Integer player2Health;
    private float timeCount;

    Label countDownLabel;
    Label player1HealthLabel;
    Label Player1NameLabel;
    Label player2HealthLabel;
    Label Player2NameLabel;



    public Hud(SpriteBatch sb){
        player1Health = 100;
        player2Health = 100;
        timeCount = 0;
        viewport = new FitViewport(SastaTankStars.V_WIDTH, SastaTankStars.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb); //this is where the stage is created

        Table table = new Table();
        table.top();
        table.setFillParent(true); //table is same size as stage

//        countDownLabel = new Label(String.format("%03f", timeCount), new Label.LabelStyle(new BitmapFont(), com.badlogic.gdx.graphics.Color.WHITE));
        player1HealthLabel = new Label(String.format("%03d", player1Health), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        Player1NameLabel = new Label("com.project.game.Player 1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        player2HealthLabel = new Label(String.format("%03d", player2Health), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        Player2NameLabel = new Label("com.project.game.Player 2", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(Player1NameLabel).expandX().padTop(100);
        table.add(Player2NameLabel).expandX().padTop(100);
//        table.row();
        table.add(player1HealthLabel).expandX();
        table.add(player2HealthLabel).expandX();
        table.row();
        table.add(countDownLabel).expandX();




    }


}
