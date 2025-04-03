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
    private OrthographicCamera camera;
    private Player player;
    private Array<Platform> platforms;
    private Texture groundTexture;
    private Texture backgroundTexture; // Textura de fondo

    public GameScreen(Main game) {
        this.game = game;
        this.batch = game.batch;

        // Cargamos la imagen de fondo
        backgroundTexture = new Texture("Background/1.png");

        // Configuramos la cámara para un juego vertical
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 400, 800);
        camera.position.set(200, 400, 0);
        camera.update();

        platforms = new Array<Platform>();

        // Cargamos el texture del suelo una única vez
        groundTexture = new Texture("PNG/Tiles/platformPack_tile001.png");
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
        int numJumpPlatforms = 20;
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
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // Dibujar el fondo primero (ajusta el tamaño según convenga)
        batch.draw(backgroundTexture, 0, 0, 400, 800);
        // Dibujar plataformas
        for (Platform platform : platforms) {
            batch.draw(platform.getTexture(), platform.position.x, platform.position.y);
        }
        // Dibujar al jugador
        batch.draw(player.getTexture(), player.position.x, player.position.y);
        batch.end();
    }

    private void update(float delta) {
        player.update(delta);
        // Manejo de controles y seguimiento de cámara (igual que antes)
        // ...
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
