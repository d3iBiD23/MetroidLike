package com.mygdx.metroid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
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

        // Configuramos la cámara para vertical
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 400, 800);
        camera.position.set(200, 400, 0);
        camera.update();

        platforms = new Array<Platform>();

        // Creamos la plataforma del suelo (ground)
        // Asegúrate de que la imagen "platformPack_tile001.png" se vea bien para el suelo
        Platform ground = new Platform(0, 0);
        platforms.add(ground);

        // Posiciona al jugador sobre el suelo; usamos la altura de la plataforma para ajustar
        player = new Player(200, ground.getTexture().getHeight());

        // Generamos las plataformas superiores para saltar, comenzando justo arriba del suelo
        int numPlataformas = 10;
        float gap = 80; // espacio vertical entre plataformas
        for (int i = 1; i < numPlataformas; i++) {
            float posX = (float)Math.random() * (400 - 50); // asumiendo que la plataforma tiene un ancho aproximado de 50
            float posY = ground.getTexture().getHeight() + i * gap;
            platforms.add(new Platform(posX, posY));
        }
    }

    @Override
    public void render(float delta) {
        // Limpiamos la pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // Dibujamos el jugador
        batch.draw(player.getTexture(), player.position.x, player.position.y);
        // Dibujamos todas las plataformas
        for (Platform platform : platforms) {
            batch.draw(platform.getTexture(), platform.position.x, platform.position.y);
        }
        batch.end();
    }

    private void update(float delta) {
        player.update(delta);
        // Si el jugador cae y toca alguna plataforma, se hace que salte
        if (player.velocity.y < 0) {
            for (Platform platform : platforms) {
                if (player.bounds.overlaps(platform.bounds)) {
                    player.jump();
                }
            }
        }
        // Mover la cámara si el jugador sube
        if (player.position.y > camera.position.y) {
            camera.position.y = player.position.y;
        }
        // Controles laterales para pruebas en escritorio
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.moveLeft(delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.moveRight(delta);
        }
    }

    @Override
    public void dispose() {
        player.dispose();
        for (Platform platform : platforms) {
            platform.dispose();
        }
    }
}
