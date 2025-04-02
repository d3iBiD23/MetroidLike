package com.mygdx.metroid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Json;
import com.mygdx.metroid.jsonloaders.LevelJson;

public class GameScreen extends ScreenAdapter {
    private Main game;
    private TileMap tileMap;
    private Player player;

    public GameScreen(Main game) {
        this.game = game;

        Json json = new Json();
        String jsonString = Gdx.files.internal("gothicvania church files/Assets/Level.json").readString();
        LevelJson levelData = json.fromJson(LevelJson.class, jsonString);

        // Crea TileMap a partir de la info del nivel
        tileMap = new TileMap(game.manager, game.batch);
        tileMap.loadFromLevel(levelData);

        player = new Player();
        player.setPosition(100,100);
    }

    @Override
    public void render(float delta) {
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);

        // Limpia la pantalla
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tileMap.render();

        game.batch.begin();
        player.render(game.batch);
        game.batch.end();
    }

    @Override
    public void dispose() {
        tileMap.dispose();
    }
}
