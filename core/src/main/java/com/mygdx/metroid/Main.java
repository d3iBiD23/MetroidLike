package com.mygdx.metroid;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends Game {
    public AssetManager manager;
    public SpriteBatch batch;
    public Texture image;
    public OrthographicCamera camera;

    @Override
    public void create() {
        manager = new AssetManager();
        batch = new SpriteBatch();
        camera = new OrthographicCamera(800, 400);
        camera.position.set(400,240,0);
        camera.update();


    }

    @Override
    public void dispose() {
        batch.dispose();
        manager.dispose();
    }
}
