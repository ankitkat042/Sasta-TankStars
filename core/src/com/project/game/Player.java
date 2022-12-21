package com.project.game;

import com.project.game.Tank;

public class Player {
    public int health;
    public Tank tank;

    public Player(Tank tank) {
        this.tank = tank;
        health = 100;
    }

}
