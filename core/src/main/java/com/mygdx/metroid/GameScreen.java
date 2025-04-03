package com.mygdx.metroid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class GameScreen extends ScreenAdapter {
    private Main game;
    private SpriteBatch batch;
    private OrthographicCamera camera; // Cámara dinámica (se mueve con el jugador)
    private OrthographicCamera fixedCamera; // Cámara fija para el fondo
    private Player player;
    private Array<Platform> platforms;
    private Texture groundTexture;
    private Texture backgroundTexture; // Textura de fondo
    private boolean initialFrame = true;

    public GameScreen(Main game) {
        this.game = game;
        this.batch = game.batch;

        // Cargamos la imagen de fondo
        backgroundTexture = new Texture("Background/3.png");

        // Configuramos la cámara dinámica para el juego
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 400gga, 800);
        camera.position.set(200, 400, 0);
        camera.update();

        // Creamos una cámara fija para el fondo
        fixedCamera = new OrthographicCamera();
        fixedCamera.setToOrtho(false, 400, 800);
        fixedCamera.update();

        platforms = new Array<Platform>();

        // Cargamos el texture del suelo una única vez
        groundTexture = new Texture("PNG/Tiles/platformPack_tile015.png");
        int numTiles = (int) Math.ceil(400 / (float) groundTexture.getWidth()) + 1;
        for (int i = 0; i < numTiles; i++) {
            platforms.add(new GroundPlatform(i * groundTexture.getWidth(), 0, groundTexture));
        }

        // Posiciona al jugador sobre el suelo, centrado horizontalmente
        float playerWidth = new Texture("PNG/Characters/platformChar_idle.png").getWidth();
        float playerX = 200 - (playerWidth / 2);
        float playerY = groundTexture.getHeight();
        player = new Player(playerX, playerY);

        // Generamos plataformas adicionales para saltar
        int numJumpPlatforms = 200;
        float gap = 150;
        for (int i = 1; i < numJumpPlatforms; i++) {
            float posX = (float) Math.random() * (400 - 50);
            float posY = groundTexture.getHeight() + i * gap;
            platforms.add(new Platform(posX, posY));
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);
        camera.update();

        // Dibujar el fondo usando la cámara fija
        batch.setProjectionMatrix(fixedCamera.combined);
        batch.begin();
        batch.setColor(0.7f, 0.7f, 0.7f, 1f);
        batch.draw(backgroundTexture, 0, 0, 400, 800);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();

        // Dibujar el resto del juego usando la cámara dinámica
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Platform platform : platforms) {
            batch.draw(platform.getTexture(), platform.position.x, platform.position.y);
        }
        batch.draw(player.getTexture(), player.position.x, player.position.y);
        batch.end();
    }

    private void update(float delta) {
        player.update(delta);

        if (initialFrame) {
            initialFrame = false;
        } else {
            if (player.velocity.y < 0) {
                for (Platform platform : platforms) {
                    if (player.getFootBounds().overlaps(platform.bounds)) {
                        player.jump();
                    }
                }
            }
        }

        if (Gdx.input.isTouched()) {
            float touchX = Gdx.input.getX();
            float screenWidth = Gdx.graphics.getWidth();
            if (touchX < screenWidth / 2) {
                player.moveLeft(delta);
            } else {
                player.moveRight(delta);
            }
        }

        float viewportHeight = 800;
        float minCameraY = viewportHeight / 2;
        float relativeY = player.position.y - camera.position.y;
        float targetY = camera.position.y;
        float deadZoneUp = 20;
        float deadZoneDown = -100;

        if (relativeY > deadZoneUp) {
            targetY = player.position.y - deadZoneUp;
        } else if (relativeY < deadZoneDown) {
            targetY = player.position.y - deadZoneDown;
        }

        targetY = Math.max(targetY, minCameraY);
        camera.position.y = MathUtils.lerp(camera.position.y, targetY, 0.1f);
    }

    @Override
    public void dispose() {
        player.dispose();
        for (Platform platform : platforms) {
            platform.dispose();
        }
        if (groundTexture != null) {
            groundTexture.dispose();
            groundTexture = null;
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
            backgroundTexture = null;
        }
    }
}
