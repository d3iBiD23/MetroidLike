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
    public OrthographicCamera camera;

    @Override
    public void create() {
        manager = new AssetManager();
        batch = new SpriteBatch();
        // Ajusta la cámara a una resolución vertical, por ejemplo 400x800
        camera = new OrthographicCamera(400, 800);
        camera.position.set(200, 400, 0);
        camera.update();

        // Iniciamos la pantalla del juego
        setScreen(new GameScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        manager.dispose();
    }

}
