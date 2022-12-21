package com.project.game.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class PauseButton extends TextButton {

    private int id;
    public PauseButton(String text, Skin skin, String style, int id) {
        super(text, skin, style);
        this.id = id;
    }

    public int getId() {
        return id;
    }

}

