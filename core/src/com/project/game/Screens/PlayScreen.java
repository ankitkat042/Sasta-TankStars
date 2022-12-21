package com.project.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.project.game.SastaTankStars;

import java.util.Random;

public class PlayScreen implements Screen {
    // To Render Shapes and Shape Textures
    PolygonSpriteBatch polygonSpriteBatch;

    // To Have Additional Properties on the Textures like Rotation
    Sprite player1_sprite;
    Sprite player2_sprite;

    Texture player1_texture, player2_texture, groundTexture;
    Texture bulletTexture;
    Texture pointer;
    private OrthographicCamera camera;

    // For Box2D Debugging
    private Box2DDebugRenderer b2dr;

    // Box2d Physics World
    private World gameWorld;

    // Physics Bodies
    protected Body player1, player2, hill;

    // Size of Player
    private final float playerSize = 100;

    // Pixel Per Meter > To Render Sprites with respect to Box2D
    private float tilePerPixel = 32;


    Viewport viewport;

    // To Use Hill Pattern
    TextureRegion textureRegion;

    // To Define Hill Shape
    PolygonRegion polygonRegion;
    SastaTankStars Game;
    private float screenWidth, screenHeight;
    Rectangle btn1Bounds;

    Texture button1;
    private float middlex;
    private float middley;
    float bgWidth, bgHeight;
    float buttonwidth, buttonheight;

    // To Load Games CurrentScores using Scene2D
    CurrentScores scores;

    float tankheigth;
    //    float tankratio1;
    float tankwidth1;
    float tankwidth2;

    // Chosen Tank of Player 1
    public int tankId1;

    // Chosen Tank of Player 2
    public int tankId2;

    // Texture for Aiming
    TextureRegion aimRegion;


    float aimwidth, aimheight;
    float bulletsize;

    // Aiming Angles of Tank1 and Tank2
    float aimAngle = 0, aimAngle2 = 0;

    // Preferences to Load and Save Game Data
    Preferences loadPref;

    // To Store Hill Shape
    Vector2[] vertices;

    public PlayScreen(SastaTankStars game, String gameId, int tankId1, int tankId2) {
        System.out.println("GameScreen2");
        loadPref = Gdx.app.getPreferences("my loadPref");

        // Default Game Data for New Game
        String[] gameData = new String[]{"20000", "0", "23000", "0", "100", "100", "100", "100", "" + tankId1, "" + tankId2};

        // If Saved Game, then load game data from game id
        if (!gameId.equals("")) {
            // Load Hill Vertices
            String[] temp = loadPref.getString("vertices" + gameId).split(",");
            vertices = new Vector2[temp.length / 2];
            int j = 0;
            for (int i = 0; i < temp.length; i += 2) {
                vertices[j] = new Vector2(Float.parseFloat(temp[i]), Float.parseFloat(temp[i + 1]));
                j++;
            }

            // Load Player Positions, Health and Fuel
            gameData = loadPref.getString("game" + gameId).split(",");
            gameData[1] = "0";
            gameData[3] = "0";
            tankId1 = Integer.parseInt(gameData[8]);
            tankId2 = Integer.parseInt(gameData[9]);
        }

        this.Game = game;
        this.tankId1 = tankId1;
        this.tankId2 = tankId2;

        // Create New Polygon SB
        polygonSpriteBatch = new PolygonSpriteBatch();


        // Screen Width & Height with respect to tilePerPixel for Physics World
        screenWidth = Gdx.graphics.getWidth() / tilePerPixel;
        screenHeight = Gdx.graphics.getHeight() / tilePerPixel;

        // Menu
        button1 = new Texture("menuButton.png");
        float buttonratio = (float) button1.getWidth() / button1.getHeight();
        buttonwidth = bgWidth / 5f / tilePerPixel;
        buttonheight = buttonwidth / buttonratio / tilePerPixel;
        middlex = (Gdx.graphics.getWidth() / 2 - buttonwidth / 2) / tilePerPixel;
        middley = (Gdx.graphics.getHeight() / 2) / tilePerPixel;
        btn1Bounds = new Rectangle(middlex, middley, buttonwidth, buttonheight);

        // Textures
        groundTexture = prepareTexture("ground.png");

        // Repeat Wrap Applied to create repeat pattern
        groundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        // Texture Region From pattern
        textureRegion = new TextureRegion(groundTexture);

        // Texture Region Placement and Size According to Hill's Texture
        textureRegion.setRegion(0, 0, groundTexture.getWidth() * 10, groundTexture.getHeight() * 10);


        // Tank Textures Stored in Order of IDs
        String[] tankTextures1 = {"tank1.png", "tank3.png", "tank5.png"};
        String[] tankTextures2 = {"tank2.png", "tank4.png", "tank6.png"};

        player1_texture = prepareTexture(tankTextures1[tankId1]);

        player2_texture = prepareTexture(tankTextures2[tankId2]);

        bulletTexture = new Texture("missile.png");

        bulletsize = screenHeight * tilePerPixel / 60;


        // Load Aim Image and Repeat using Repear TextureWrap
        pointer = new Texture("aimHelper.png");
        pointer.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        aimRegion = new TextureRegion(pointer, 0, 0, 800, 32);

        aimheight = bulletsize;
        aimwidth = aimheight * ((float) pointer.getWidth() / pointer.getHeight());


        // Camera and VP
        camera = new OrthographicCamera();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        viewport.apply();


        // Physics World
        gameWorld = new World(new Vector2(0, -9.8f), false);
        b2dr = new Box2DDebugRenderer();

        // Breakpoints To Avoid Over speeding of Tanks
        speedbreaker = new Array<>();

        // Create Hill Body
        hill = createBody(0, -3000, 0, 0, true, false, "Hill");

        tankheigth = playerSize;
        float tankratio1 = (float) player1_texture.getWidth() / player1_texture.getHeight();
        float tankratio2 = (float) player2_texture.getWidth() / player2_texture.getHeight();
        tankwidth1 = tankratio1 * tankheigth;
        tankwidth2 = tankratio2 * tankheigth;


        player1_sprite = new Sprite(player1_texture);
        player1_sprite.setSize(tankwidth1, tankheigth);
        player1_sprite.setOriginCenter();

        player2_sprite = new Sprite(player2_texture);
        player2_sprite.setSize(tankwidth2, tankheigth);
        player2_sprite.setOriginCenter();

//        player1_sprite.setRotation(45);

        player1 = createBody(Float.parseFloat(gameData[0]), Float.parseFloat(gameData[1]), tankwidth1, tankheigth, false, true, "p1");
        player2 = createBody(Float.parseFloat(gameData[2]), Float.parseFloat(gameData[3]), tankwidth2, tankheigth, false, true, "p2");

        setNextEdges();

        scores = new CurrentScores(Game, this, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), gameId, tankId1, tankId2);

//        Preferences loadPref = Gdx.app.getPreferences("loadPref");

//        if (!gameId.equals("")) {
        // p1 position , p2 position , p1 health , p2 health
//            String gameData = loadPref.getString("game" + gameId, "0,0,0,0,0,0");
//            System.out.println("Load Saved Game: " + gameId + " Data: " + gameData);

//        }
        Handler_Collision();
    }

    private Texture prepareTexture(String file) {
        // Create New Texture Object from Image File
        Texture texture = new Texture(file);

        // Set Linear Filter for better resolution
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        return texture;
    }

    private void setNextEdges() {
        for (int i = 0; i < speedbreaker.size - 1; i++) {
            if (player1.getPosition().x / tilePerPixel > speedbreaker.get(i) && player1.getPosition().x / tilePerPixel < speedbreaker.get(i + 1)) {
                nextedge = i + 1;
            }

            if (player2.getPosition().x / tilePerPixel > speedbreaker.get(i) && player2.getPosition().x / tilePerPixel < speedbreaker.get(i + 1)) {
                nextedge2 = i + 1;
            }
        }

//    System.out.println(nextedge + "," + nextedge2);
//    System.out.println(speedbreaker.get(nextedge) + "," + speedbreaker.get(nextedge2));
    }

    private Body createBody(float x, float y, float width, float height, boolean isStatic, boolean isBox, String data) {

        // Definition of Body
        BodyDef bodyDef = new BodyDef();

        // Body Type
        if (isStatic) bodyDef.type = BodyDef.BodyType.StaticBody; // Stable
        else bodyDef.type = BodyDef.BodyType.DynamicBody; // Moving


        // Other Properties of Body
        bodyDef.fixedRotation = true;
        bodyDef.position.set(x / tilePerPixel, y / tilePerPixel);

        // Create Body in World
        Body body = gameWorld.createBody(bodyDef);
        body.setUserData(data); // Name of Body to detect Collisions Later


        // Set body Shape
        if (isBox) {
            // Circular Shape For Better Movement
            CircleShape shape = new CircleShape();
            shape.setRadius(height / 2 / tilePerPixel); // Set Body Size as Radius
            body.createFixture(shape, 1f);

            // Head of Tank
            if (data.equals("p1") || data.equals("p2")) {
                // Edge Shape for Tank Head
                EdgeShape edgeShape = new EdgeShape();
                edgeShape.set(new Vector2(-30 / tilePerPixel, 55 / tilePerPixel), new Vector2(30 / tilePerPixel, 55 / tilePerPixel)); // Position of Head

                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape = edgeShape;
                fixtureDef.isSensor = true; // Sensor To Prevent Collision With Main Body
                body.createFixture(fixtureDef).setUserData(data + "head"); // Named to Detect Collisions

                edgeShape.dispose();
            }

            // Dispose Shape After Body Creation
            shape.dispose();
        } else {
            // Chain Shape to draw custom shape of hill
            ChainShape shape = new ChainShape();

            // Starting Coordinates = Body Position
            float lastX = x / tilePerPixel;
            float lastY = y / tilePerPixel;

            // If New Game then Assign Vertices Array
            if (vertices == null) {
                vertices = new Vector2[302];
            }

            // Create Vertice from Vector2 Coords
            vertices[0] = new Vector2(lastX, lastY);

            // Draw From Bottom to Top
            lastY = screenHeight / 1.5f;

            // Create Next Vertice
            vertices[1] = new Vector2(lastX, lastY);


            // Loop For Drawing Next Vertices In Sets of Four
            for (int i = 2; i < vertices.length; i += 4) {
                // ---\
                lastX = randomPoint(true, lastX, true);
                speedbreaker.add(lastX);
                vertices[i] = new Vector2(lastX, lastY);

                // \__
                lastX = randomPoint(true, lastX, true);
                lastY = randomPoint(false, lastY, false);
                vertices[i + 1] = new Vector2(lastX, lastY);

                // __/
                lastX = randomPoint(true, lastX, true);
                vertices[i + 2] = new Vector2(lastX, lastY);

                // /--
                lastX = randomPoint(true, lastX, true);
                speedbreaker.add(lastX);
                lastY = randomPoint(false, lastY, true);
                vertices[i + 3] = new Vector2(lastX, lastY);
            }

            // Create Chain From Saved vertices
            shape.createChain(vertices);

            FixtureDef hillfixturedef = new FixtureDef();
            hillfixturedef.shape = shape;
            body.createFixture(hillfixturedef);

            shape.dispose();


            // Vector Array > Float Array
            float[] vertices2 = new float[vertices.length * 2];

            // Assign Values of Float Array
            int counter = 0;
            for (int i = 0; i < vertices.length; i++) {
                vertices2[counter] = vertices[i].x * tilePerPixel;
                vertices2[counter + 1] = vertices[i].y * tilePerPixel;

                counter += 2;
            }

            // Draw Triangles from Vertices For Texture Filling
            short[] triangles = new EarClippingTriangulator().computeTriangles(vertices2).toArray();

            // Triangles > Polygon Region
            polygonRegion = new PolygonRegion(textureRegion, vertices2, triangles);
        }

        // Return the body
        return body;
    }


    // To Create Random Hill
    private float randomPoint(boolean isXAxis, float lastPoint, boolean wantHigher) {
        Random random = new Random();

        float min = isXAxis ? (lastPoint + screenWidth / 2f) : (wantHigher ? (lastPoint + screenHeight / 4.5f) : (lastPoint - screenHeight / 4.5f));
        float max = isXAxis ? (lastPoint + screenWidth / 1.5f) : (wantHigher ? (lastPoint + screenHeight / 4f) : (lastPoint - screenHeight / 4f));

        return random.nextFloat() * (max - min) + min;
    }

    // To Store Breakpoints to lower tank speed
    Array<Float> speedbreaker;

    private void update() {
        // World Update at every frame
        gameWorld.step(1 / 60f, 6, 2);

        // Set Camera According to player 1 position
        Vector3 position = camera.position;
        position.x = (player1.getPosition().x) * tilePerPixel + Gdx.graphics.getWidth() / 2.3f;
        position.y = player1.getPosition().y * tilePerPixel + Gdx.graphics.getHeight() / 15f;
        camera.position.set(position); // Update Camera

        // Update Cam with new position
        camera.update();
    }


    @Override
    public void show() {

    }

    // Tank Moving Velocity - X and Y
    private float velocity = 180f;
    private float velocity_y = -150f;

    // Fuel Over Indicators
    boolean isFuelOver1, isFuelOver2;

    // To Handle Input Events
    private void handleTouch() {
        // Return if Tank in Air or Game Over
        if (!Lander_tank || isGameOver) return;

        // Zoom Out on - Press
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_ADD) && camera.zoom > 1) {
            camera.zoom--;
            camera.update();
        }

        // Zoom In on + Press
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_SUBTRACT) && camera.zoom < 4) {
            camera.zoom++;
            camera.update();
        }



        // Toggle Fire Mode of Tanks
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) && isTank1Turn) inFireMode1 = !inFireMode1;
        else if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT) && !isTank1Turn) inFireMode2 = !inFireMode2;

        // Move Tank If Tank Has Fuel and Tank's Turn
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && (!isFuelOver2) && !isTank1Turn) {
            scores.reducedfuel(false);
            if(scores.fuelbar2.getValue()<=0)
                onFuelOver(false);
            player2.applyForce(new Vector2(-velocity, velocity_y), player2.getWorldCenter(), true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && (!isFuelOver2) && !isTank1Turn) {
            scores.reducedfuel(false);
            if(scores.fuelbar2.getValue()<=0)
                onFuelOver(false);
            player2.applyForce(new Vector2(velocity, velocity_y), player2.getWorldCenter(), true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A) && (!isFuelOver1) && isTank1Turn) {
            scores.reducedfuel(true);
            if(scores.fuel_bar1.getValue()<=0)
                onFuelOver(true);
            player1.applyForce(new Vector2(-velocity, velocity_y), player1.getWorldCenter(), true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.D) && (!isFuelOver1) && isTank1Turn) {
            scores.reducedfuel(true);
            if(scores.fuel_bar1.getValue()<=0)
                onFuelOver(true);
            player1.applyForce(new Vector2(velocity, velocity_y), player1.getWorldCenter(), true);
        }

        // Aim Controls to Change Aim Angle
        else if (Gdx.input.isKeyPressed(Input.Keys.W) && isTank1Turn) {
            if (aimAngle < 90)
                aimAngle++;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S) && isTank1Turn) {
            if (aimAngle > -20)
                aimAngle--;
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP) && !isTank1Turn) {
            if (aimAngle2 > -90)
                aimAngle2--;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && !isTank1Turn) {
            if (aimAngle2 < 20)
                aimAngle2++;
        }

        // Fire Bullet At Custom Velocity (Scale: 1-3)
        else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            onFire(true, 1f);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            onFire(true, 2f);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            onFire(true, 3f);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) {
            onFire(false, 1f);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) {
            onFire(false, 2f);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3)) {
            onFire(false, 3f);
        }

    }


    // To Keep Track of Coming Speedbreakers
    private int nextedge, nextedge2;

    // Store Last Position Coords of Tank for Rotation Calculation
    private float p1y, p2y;
    private float p1x, p2x;

    // Rotation Transition Step Track
    private int transitionstep, transitionstep2;


    // Rotation Angle of Tank based on Rising or Falling State
    private float rising = -10;
    private float falling = 10;


    // Evaluate Speed and Rotation after Tank Movement
    public void aftermovement() {
        // Distance from next breaker
        float distance = speedbreaker.get(nextedge) - player1.getPosition().x;

        // Distance from Previous Breaker
        float backwards = 0;
        if (nextedge > 0) {
            backwards = player1.getPosition().x - speedbreaker.get(nextedge - 1);
        }


        // Stop Force - To Lower Speed on Edge
        float multiplier = 0.55f;
        if (nextedge > 0 && backwards < 2)
            nextedge--; // Crosses Backward > Next Edge Minus
        else if (distance < 2)
            nextedge++; // Crosses Forward  > Next Edge Plus

            // Lower the speed by Stop Force
        else if ((distance < 15 && player1.getLinearVelocity().x > 0)) { // Forward
            player1.applyLinearImpulse(-player1.getLinearVelocity().x * multiplier, 0, player1.getWorldCenter().x, player1.getWorldCenter().y, true);
        }
        else if ((nextedge > 0 && backwards < 15 && player1.getLinearVelocity().x < 0)) { // Backward
            player1.applyLinearImpulse(-player1.getLinearVelocity().x * multiplier, 0, player1.getWorldCenter().x, player1.getWorldCenter().y, true);
        }

        // New Position of Player to Compare with Old Position and Calculate Rotation Angle
        float p1ynew = player1.getPosition().y;
        float p1xnew = player1.getPosition().x;
        float currentrotation = player1_sprite.getRotation();
        float rotationangle = 0;

        if (p1ynew > p1y) { // Up Hill Case
            if (p1xnew < p1x) { // If Going Up Hill
                rotationangle = rising;
            }
            if (currentrotation == rising || (currentrotation == 0 && transitionstep < 30)) // in transition
                transitionstep++;
            else { // Going Downhill
                rotationangle = falling;
                transitionstep = 0;
            }
        } else if (p1ynew < p1y) {
            if (p1xnew < p1x) { //
                rotationangle = falling;
            }
            if (currentrotation == falling || (currentrotation == 0 && transitionstep < 30))
                transitionstep++;
            else {
                rotationangle = rising;
                transitionstep = 0;
            }
        }
        p1y = p1ynew;
        p1x = p1xnew;


        // Sprite Position in respect to Box2d Body
        player1_sprite.setPosition(player1.getPosition().x * tilePerPixel - tankwidth1 / 2, player1.getPosition().y * tilePerPixel - tankheigth / 2);

        // Apply Rotation to Sprite
        player1_sprite.setRotation(Lander_tank ? rotationangle : 0);
    }

    public void aftermovementTank2() {

        float distance = speedbreaker.get(nextedge2) - player2.getPosition().x;
//        float
        float backwards = 0;
        if (nextedge2 > 0) {
            backwards = player2.getPosition().x - speedbreaker.get(nextedge2 - 1);
        }
//            nextedge--;
//        System.out.println(backwards);
        float multiplier = 0.55f;
//        if(nextedge%2==0)
//            multiplier=0.45f;
        if (nextedge2 > 0 && backwards < 2)
            nextedge2--;
        else if (distance < 2)
            nextedge2++;
        else if ((distance < 15 && player2.getLinearVelocity().x > 0)) {

            player2.applyLinearImpulse(-player2.getLinearVelocity().x * multiplier, 0, player2.getWorldCenter().x, player2.getWorldCenter().y, true);

        } else if ((nextedge2 > 0 && backwards < 15 && player2.getLinearVelocity().x < 0)) {

            player2.applyLinearImpulse(-player2.getLinearVelocity().x * multiplier, 0, player2.getWorldCenter().x, player2.getWorldCenter().y, true);
        }
        float p1ynew = player2.getPosition().y;
        float p1xnew = player2.getPosition().x;
        float currentrotation = player2_sprite.getRotation();
        float rotationangle = 0;

        if (p1ynew > p2y) {
            if (p1xnew < p2x) {
                rotationangle = rising;
            }
            if (currentrotation == rising || (currentrotation == 0 && transitionstep2 < 30))
                transitionstep2++;
            else {
                rotationangle = falling;
                transitionstep2 = 0;
            }
        } else if (p1ynew < p2y) {
            if (p1xnew < p2x) {
                rotationangle = falling;
            }
            if (currentrotation == falling || (currentrotation == 0 && transitionstep2 < 30))
                transitionstep2++;
            else {
                rotationangle = rising;
                transitionstep2 = 0;
            }
        }
        p2y = p1ynew;
        p2x = p1xnew;

        player2_sprite.setPosition(player2.getPosition().x * tilePerPixel - tankwidth2 / 2, player2.getPosition().y * tilePerPixel - tankheigth / 2);

        player2_sprite.setRotation(Lander_tank ? rotationangle : 0);
    }


    // If we have to remove bullet body
    boolean removeBullet;
    float bulletspeed = 25f; // Speed of Bullet

    // If Tank is in fire mode and which tank's turn it is
    boolean inFireMode1, inFireMode2, isTank1Turn = true;

    Body bullet; // Body of bullet
    private void onFire(boolean isTank1, float multiplier) {
        // Tank is in fire mode and its the turn of tank
        if (isTank1 && (!inFireMode1 || !isTank1Turn)) return;
        if (!isTank1 && (!inFireMode2 || isTank1Turn)) return;

        // Swap Tank Turn on Fire
        isTank1Turn = !isTank1Turn;

        // Bullet name WRT tank
        String data = isTank1 ? "Bullet1" : "Bullet2";

        // Bullet position and Angle based on tank position
        float bulletX = player1.getPosition().x * tilePerPixel + playerSize + 1;
        float bulletY = player1.getPosition().y * tilePerPixel + playerSize / 2;
        float angle = aimAngle * ((float) Math.PI / 180f); // Rad -> Deg
        if (!isTank1) {
            bulletX = player2.getPosition().x * tilePerPixel - playerSize - 1;
            bulletY = player2.getPosition().y * tilePerPixel + playerSize / 2;
            angle = aimAngle2 * ((float) Math.PI / 180f);
        }

        // create bullet body
        bullet = createBody(bulletX, bulletY, bulletsize, bulletsize, false, true, data);

        // apply velocity in multiplier
        if (isTank1) bullet.setLinearVelocity(multiplier * bulletspeed * MathUtils.cos(angle), multiplier * bulletspeed * MathUtils.sin(angle));
        else bullet.setLinearVelocity(multiplier * -bulletspeed * MathUtils.cos(angle), multiplier * -bulletspeed * MathUtils.sin(angle));

        // refill fuelbar after fire
        if (isTank1) {
            scores.fuel_bar1.setValue(100f);
            isFuelOver1 = false;
        }
        else {
            scores.fuelbar2.setValue(100f);
            isFuelOver2 = false;
        }
    }

    // Track of game over
    boolean isGameOver;
    // Called on game over
    private void onGameOver(int tankId) {
        // Show Game Over message after 5s delay
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Game.setScreen(new MainMenuScreen(Game));
            }
        }, 5f);


        scores.gameOverShowing.setText("Game Over - Tank " + tankId + " Wins");
        scores.gameOverShowing.setVisible(true);
        isGameOver = true;
    }

    // called on fuel over
    private void onFuelOver(boolean isTank1) {
        float multiplier = 2f;

        if (isTank1) {
            // fuel over
            isFuelOver1 = true;

            // If Movemenet then apply negative impulse to stop tank
            if ((player1.getLinearVelocity().x > 0)) {
                player1.applyLinearImpulse(-player1.getLinearVelocity().x * multiplier, 0, player1.getWorldCenter().x, player1.getWorldCenter().y, true);
            }
        } else {
            isFuelOver2 = true;
            if ((player2.getLinearVelocity().x > 0)) {
                player2.applyLinearImpulse(-player2.getLinearVelocity().x * multiplier, 0, player2.getWorldCenter().x, player2.getWorldCenter().y, true);
            }
        }
    }

    boolean Lander_tank;


    // impulse after getting hit
    float hitImpulse = 100f, headHitImpulse = 80f;

    private void Handler_Collision() {
        // set to gameWorld
        gameWorld.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact touchDown) {
                // Get Body Names
                String f1data = touchDown.getFixtureA().getBody().getUserData().toString();
                String f2data = touchDown.getFixtureB().getBody().getUserData().toString();
                if (touchDown.getFixtureA().getUserData() != null) {
                    // If Hit on Tank2 Head
                    if (touchDown.getFixtureA().getUserData().equals("p2head") && f2data.equals("Bullet1")) {

                        player2.applyLinearImpulse(headHitImpulse, 0, player2.getWorldCenter().x, player2.getWorldCenter().y, true);


                        scores.hit(false, 40);


                        if (scores.hp_bar2.getValue() <= 0) onGameOver(1);
                    } else if (touchDown.getFixtureA().getUserData().equals("p1head") && f2data.equals("Bullet2")) {
                        player1.applyLinearImpulse(-headHitImpulse, 0, player1.getWorldCenter().x, player1.getWorldCenter().y, true);
                        scores.hit(true, 40);
                        if (scores.hp_bar1.getValue() <= 0) onGameOver(2);
                    }
                }


                else if (f1data.equals("Hill") && f2data.equals("p1")) {
                    Lander_tank = true;
                }


                else if (f1data.equals("p2") && f2data.equals("Bullet1")) {
                    player2.applyLinearImpulse(hitImpulse, 0, player2.getWorldCenter().x, player2.getWorldCenter().y, true);
                    scores.hit(false, 20);
                    if (scores.hp_bar2.getValue() <= 0) onGameOver(1);
                }

                else if (f1data.equals("p1") && f2data.equals("Bullet2")) {
                    player1.applyLinearImpulse(-hitImpulse, 0, player1.getWorldCenter().x, player1.getWorldCenter().y, true);
                    scores.hit(true, 20);
                    if (scores.hp_bar1.getValue() <= 0) onGameOver(2);
                }


                if (f2data.contains("Bullet")) {
                    removeBullet = true;
                }
            }

            @Override
            public void endContact(Contact touchDown) {

            }
            @Override
            public void preSolve(Contact touchDown, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact touchDown, ContactImpulse impulse) {

            }
        });
    }

    @Override
    public void render(float delta) {
        update();
        ScreenUtils.clear(0.5f, 0.5f, 0.5f, 1);

        // Project matrix attached to cam
        polygonSpriteBatch.setProjectionMatrix(camera.combined);

        // Begin for drawing
        polygonSpriteBatch.begin();

        // Draw Hill
        polygonSpriteBatch.draw(polygonRegion, 0, -3000);

        // Draw Tank Aiming
        if (Lander_tank) {
            if (inFireMode1 && isTank1Turn) polygonSpriteBatch.draw(aimRegion, player1_sprite.getX() + player1_sprite.getWidth(), player1_sprite.getY() + player1_sprite.getHeight() / 2, 0, 0, aimwidth * 8, aimheight, 1f, 1f, aimAngle);
            if (inFireMode2 && !isTank1Turn) polygonSpriteBatch.draw(aimRegion, player2_sprite.getX() - aimwidth * 8, player2_sprite.getY() + player2_sprite.getHeight() / 2, aimwidth * 8, 0, aimwidth * 8, aimheight, 1f, 1f, aimAngle2);
        }

        // Close
        polygonSpriteBatch.end();


        Game.batch.setProjectionMatrix(camera.combined);
        Game.batch.begin();

        // Draw bullet only if bullet exists
        if (bullet != null) Game.batch.draw(bulletTexture, bullet.getPosition().x * tilePerPixel - bulletsize / 2, bullet.getPosition().y * tilePerPixel - bulletsize / 2, bulletsize, bulletsize);

        // Draw Pause Menu Button
        Game.batch.draw(button1, middlex, middley, buttonwidth, buttonheight);

        // Draw Sprites of Tanks
        player1_sprite.draw(Game.batch);
        player2_sprite.draw(Game.batch);

        Game.batch.end();


        // CurrentScores  Drawing on Camera
        Game.batch.setProjectionMatrix(scores.stage.getCamera().combined);
        scores.stage.draw();

        // Dandle Input
        handleTouch();
        aftermovement();
        aftermovementTank2();


//    b2dr.render(gameWorld, camera.combined.scl(tilePerPixel));

        // If Bullet Qued for removal then destroy Bullet Body set to null
        if (removeBullet && bullet != null) {
            gameWorld.destroyBody(bullet);
            removeBullet = false;
            bullet = null;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);

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
//        batch.dispose();
        player1_texture.dispose();
        player2_texture.dispose();
        gameWorld.dispose();
        b2dr.dispose();
    }
}
