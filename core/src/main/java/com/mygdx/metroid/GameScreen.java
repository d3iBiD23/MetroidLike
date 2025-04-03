package com.mygdx.metroid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class GameScreen extends ScreenAdapter {
    private Main game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Player player;
    private Array<Platform> platforms;

    public GameScreen(Main game) {
        this.game = game;
        this.batch = game.batch;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 400, 800);
        camera.position.set(200, 400, 0);
        camera.update();

        player = new Player(200, 100);

        platforms = new Array<Platform>();
        for (int i = 0; i < 10; i++) {
            platforms.add(new Platform((float) Math.random() * 350, i * 80));
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(player.getTexture(), player.position.x, player.position.y);

        for (Platform platform : platforms) {
            batch.draw(platform.getTexture(), platform.position.x, platform.position.y);
        }

        batch.end();
    }

    private void update(float delta) {
        // Actualizamos la física del jugador
        player.update(delta);

        // Colisión simple: si el jugador está cayendo y toca una plataforma, "rebota" (salta)
        if (player.velocity.y < 0) {
            for (Platform platform : platforms) {
                if (player.bounds.overlaps(platform.bounds)) {
                    player.jump();
                }
            }
        }

        // Mover la cámara hacia arriba si el jugador supera la posición actual de la cámara
        if (player.position.y > camera.position.y) {
            camera.position.y = player.position.y;
        }

        // Ejemplo de control lateral (para pruebas en escritorio)
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.moveLeft(delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.moveRight(delta);
        }
    }

    @Override
    public void dispose() {
        // Aquí podrías liberar recursos si es necesario
    }
}
