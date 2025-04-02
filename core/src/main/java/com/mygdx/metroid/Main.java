package com.mygdx.metroid;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends Game {
    private SpriteBatch batch;
    private Texture image;

    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
