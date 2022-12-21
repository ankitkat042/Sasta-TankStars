package com.project.game.Screens;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.Disposable;
import com.project.game.SastaTankStars;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CurrentScores implements Disposable, Serializable {
    SpriteBatch sprite;
    Stage stage;

    Viewport viewport;

    int p1score;
    int p2score;
    int p3score;
    int p4score;
    // To Keep Track of Pause Menu Visibility
    boolean menuShowing;

    // Game Over Message Label
    protected Label gameOverShowing;
    private Label label_score_player1;
    private Label label_score_player2;
    private Label label_score_player3;
    private Label label_score_player4;
    private float PPM = 32;
    Image menu;
    ProgressBar hp_bar1;
    ProgressBar hp_bar2;
    ProgressBar fuel_bar1;
    ProgressBar fuelbar2;
    Image resumegamebutton;
    Image savegamebutton;
    Image exitgamebutton;



  
    Skin skin;
    Skin skin1;
    SastaTankStars game;
    //serializable
    private static final long serialVersionUID = 1234567890L;


    public CurrentScores(SastaTankStars game, final PlayScreen playScreen, float width, float height, String gameId, final int tankId1, final int tankId2) {
        this.sprite = game.batch;
        this.game = game;

        final Preferences savedPref = Gdx.app.getPreferences("my preferences");


        // Default Game Data
        final String saved = savedPref.getString("saved", "");
        String[] gameData = new String[]{"0", "0", "0", "0", "100", "100", "100", "100", "" + tankId1, "" + tankId2};
        Collection C = new ArrayList();
        for(int i = 0; i < gameData.length; i++) {
            C.add(gameData[i]);
        }
        Iterator e = C.iterator();
        while(e.hasNext()) {
            System.out.println(e.next());
        }

        // Load Saved Game from Preferences
        if (!gameId.equals(""))
            gameData = savedPref.getString("game" + gameId, "0,0,0,0,100,100,100,100," + tankId1 + "," + tankId2).split(",");


        viewport = new FitViewport(width, height, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);

        final Table table = new Table();
        table.top();
        table.setFillParent(true);
        skin = new Skin(Gdx.files.internal("comic/skin/comic-ui.json"));
        skin.getFont("font").getData().setScale(4);
        skin1 = new Skin(Gdx.files.internal("pixthulhu/skin/pixthulhu-ui.json"));
        skin1.getFont("font").getData().setScale(4);
        label_score_player1 = new Label("HP", skin);
        label_score_player2 = new Label("HP", skin);
        label_score_player3 = new Label("Fuel", skin);
        label_score_player4 = new Label("Fuel", skin);

        Texture texture = new Texture("menuButton.png");
        Texture texture3 = new Texture("resumebutton.png");
        Texture texture4 = new Texture("savegamebutton.png");
        Texture texture5 = new Texture("exitbutton.png");

        menu = new Image(texture);
        hp_bar1 = new ProgressBar(0, 100, 5, false, skin);
        hp_bar2 = new ProgressBar(0, 100, 5, false, skin);

        fuel_bar1 = new ProgressBar(0, 100, 0.5f, false, skin1);
        fuelbar2 = new ProgressBar(0, 100, 0.5f, false, skin1);
        // Set Progressbar values from game data
        fuel_bar1.setValue(Float.parseFloat(gameData[6]));
        fuelbar2.setValue(Float.parseFloat(gameData[7]));

        hp_bar1.setValue(Float.parseFloat(gameData[4]));
        hp_bar2.setValue(Float.parseFloat(gameData[5]));
        // Pause Menu Button Listeners
        menu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                menuShowing = !menuShowing;
                resumegamebutton.setVisible(menuShowing);
                savegamebutton.setVisible(menuShowing);
                exitgamebutton.setVisible(menuShowing);
            }
        });
        table.add(menu).align(Align.left).pad(10);
        table.row();
        table.add(label_score_player1).align(Align.left).pad(10).expandX();
        
        table.add(label_score_player2).align(Align.right).pad(10);
        
        table.row().pad(20);
        table.add(hp_bar1).width(width / 3 -40).align(Align.left).pad(10).expandX();
//        table.row()
        table.add(hp_bar2).width(width / 3 - 40).padLeft(20);
        table.row();
        table.add(label_score_player3).align(Align.left).pad(10).expandX();
        table.add(label_score_player4).align(Align.right).pad(10);
        table.row().pad(20);
        table.add(fuel_bar1).width(width / 3 - 40).align(Align.left);
        table.add(fuelbar2).width(width / 3 - 40).align(Align.right).padLeft(20);
        table.row();

        resumegamebutton = new Image(texture3);
        savegamebutton = new Image(texture4);
        exitgamebutton = new Image(texture5);
        resumegamebutton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                menuShowing = !menuShowing;
                resumegamebutton.setVisible(menuShowing);
                savegamebutton.setVisible(menuShowing);
                exitgamebutton.setVisible(menuShowing);
            }
        });

        savegamebutton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
             
                long gameId = System.currentTimeMillis();
                String newSaved = gameId + "," + saved;
                
                String verticesarray = "";
                for (int i = 0; i < playScreen.vertices.length; i++) {
                    String comma = i == 0 ? "" : ",";
                    verticesarray += comma + playScreen.vertices[i].x + "," + playScreen.vertices[i].y;

                }
                savedPref.putString("saved", newSaved); // list of games
                savedPref.putString("game" + gameId, playScreen.player1.getPosition().x * PPM + "," + playScreen.player1.getPosition().y * PPM + "," + playScreen.player2.getPosition().x * PPM + "," + playScreen.player2.getPosition().y * PPM + "," + hp_bar1.getValue() + "," + hp_bar2.getValue() + "," + fuel_bar1.getValue() + "," + fuelbar2.getValue() + "," + tankId1 + "," + tankId2); // single game
                savedPref.putString("vertices" + gameId, verticesarray);
                savedPref.flush();

                menuShowing = !menuShowing;
                resumegamebutton.setVisible(menuShowing);
                savegamebutton.setVisible(menuShowing);
                exitgamebutton.setVisible(menuShowing);

            }
        });

        exitgamebutton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                goToMainScreen();
            }
        });
        Table table2 = new Table();
        table2.setFillParent(true);
        table2.top().padTop(300);
        table2.add(resumegamebutton);
        table2.row().padTop(20).padBottom(10);
        table2.add(savegamebutton);
        table2.row().pad(10);
        table2.add(exitgamebutton);
        gameOverShowing = new Label("Game Over", skin);
        table2.row().padTop(100);
        table2.add(gameOverShowing).expandX();
        gameOverShowing.setVisible(false);
        resumegamebutton.setVisible(menuShowing);
        savegamebutton.setVisible(menuShowing);
        exitgamebutton.setVisible(menuShowing);


        stage.addActor(table);
        stage.addActor(table2);

        Gdx.input.setInputProcessor(stage);
    }

    //initialisation of serialisation
    public void setScores(int p1score, int p2score, int p3score, int p4score) {
        this.p1score = p1score;
        this.p2score = p2score;
        this.p3score = p3score;
        this.p4score = p4score;
    }



    public void hit(boolean isTank1, int deduct) {
        if (isTank1) hp_bar1.setValue(hp_bar1.getValue() - deduct);
        else hp_bar2.setValue((hp_bar2.getValue() - deduct));
    }


    public void reducedfuel(boolean isTank1) {
        if (isTank1) fuel_bar1.setValue(fuel_bar1.getValue() - 0.5f);
        else fuelbar2.setValue((fuelbar2.getValue() - 0.5f));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void goToMainScreen() {
        game.setScreen(new MainMenuScreen(game));
        dispose();
    }
}
