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
    private Texture wallTexture; // Usada para las plataformas que hacen de pared

    // Conjuntos de plataformas para las paredes
    private Array<Platform> leftWallPlatforms;
    private Array<Platform> rightWallPlatforms;

    private final float SCREEN_WIDTH = 400;
    private final float SCREEN_HEIGHT = 800;

    public GameScreen(Main game) {
        this.game = game;
        this.batch = game.batch;

        // Cargamos la imagen de fondo
        backgroundTexture = new Texture("Background/3.png");

        // Configuramos la cámara dinámica para el juego
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        camera.position.set(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, 0);
        camera.update();

        // Configuramos la cámara fija para el fondo
        fixedCamera = new OrthographicCamera();
        fixedCamera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        fixedCamera.update();

        // Cargamos la textura para el suelo (opcional)
        groundTexture = new Texture("PNG/Tiles/platformPack_tile015.png");

        // Creamos al jugador, centrado horizontalmente sobre el suelo
        float playerWidth = new Texture("PNG/Characters/platformChar_happy.png").getWidth();
        float playerX = (SCREEN_WIDTH - playerWidth) / 2;
        float playerY = groundTexture.getHeight(); // Inicia justo sobre el suelo
        player = new Player(playerX, playerY);

        // Cargamos la textura para las paredes (plataformas que actúan de pared)
        wallTexture = new Texture("PNG/Tiles/platformPack_tile040.png");

        // Generamos plataformas para las paredes (por ejemplo, 20 segmentos para cubrir una gran altura)
        leftWallPlatforms = new Array<Platform>();
        rightWallPlatforms = new Array<Platform>();
        int numWallSegments = 20; // Puedes ajustar este número para que cubra más o menos altura
        for (int i = 0; i < numWallSegments; i++) {
            float yPos = i * wallTexture.getHeight();
            // Pared izquierda en x = 0
            leftWallPlatforms.add(new Platform(0, yPos, wallTexture));
            // Pared derecha en x = SCREEN_WIDTH - wallTexture.getWidth()
            rightWallPlatforms.add(new Platform(SCREEN_WIDTH - wallTexture.getWidth(), yPos, wallTexture));
        }
    }

    @Override
    public void render(float delta) {
        // Limpiar la pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);
        camera.update();

        // Dibujar el fondo usando la cámara fija
        batch.setProjectionMatrix(fixedCamera.combined);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        batch.end();

        // Dibujar los elementos del juego usando la cámara dinámica
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // Dibujar el suelo (opcional)
        batch.draw(groundTexture, 0, 0, SCREEN_WIDTH, groundTexture.getHeight());
        // Dibujar las plataformas pared (para la pared izquierda)
        for (Platform wall : leftWallPlatforms) {
            batch.draw(wall.getTexture(), wall.position.x, wall.position.y);
        }
        // Dibujar las plataformas pared (para la pared derecha)
        for (Platform wall : rightWallPlatforms) {
            batch.draw(wall.getTexture(), wall.position.x, wall.position.y);
        }
        // Dibujar al jugador
        batch.draw(player.getTexture(), player.position.x, player.position.y);
        batch.end();
    }

    private void update(float delta) {
        // Detectar toque en pantalla para ejecutar la acción de salto
        if (Gdx.input.justTouched()) {
            player.onTap();
        }

        // Actualizar al jugador
        player.update(delta);

        // Comprobar colisión del jugador con las plataformas que hacen de pared

        // Colisión con la pared izquierda
        for (Platform wall : leftWallPlatforms) {
            if (player.bounds.overlaps(wall.bounds)) {
                player.position.x = wall.position.x; // Ajusta la posición para "pegarlo"
                player.currentState = Player.PlayerState.ON_WALL_LEFT;
                break;
            }
        }
        // Colisión con la pared derecha
        for (Platform wall : rightWallPlatforms) {
            if (player.bounds.overlaps(wall.bounds)) {
                player.position.x = wall.position.x; // Ajusta la posición para "pegarlo"
                player.currentState = Player.PlayerState.ON_WALL_RIGHT;
                break;
            }
        }

        // Actualizar la cámara para seguir al jugador verticalmente
        float minCameraY = SCREEN_HEIGHT / 2;
        // La cámara se mueve hacia arriba conforme el jugador sube
        float targetY = Math.max(player.position.y, minCameraY);
        camera.position.y = MathUtils.lerp(camera.position.y, targetY, 0.1f);
    }

    @Override
    public void dispose() {
        player.dispose();
        if (groundTexture != null) {
            groundTexture.dispose();
            groundTexture = null;
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
            backgroundTexture = null;
        }
        if (wallTexture != null) {
            wallTexture.dispose();
            wallTexture = null;
        }
        // También se podrían disponer las plataformas si fuera necesario
    }
}
