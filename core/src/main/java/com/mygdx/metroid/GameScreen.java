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

    private final float WALL_GAP = 200f;

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

        // Cargamos la textura para las paredes (tiles de pared)
        wallTexture = new Texture("PNG/Tiles/platformPack_tile033.png");

        // Generamos plataformas para las paredes (por ejemplo, 20 segmentos para cubrir una gran altura)
        leftWallPlatforms = new Array<>();
        rightWallPlatforms = new Array<>();

        float currentY = 0f;
        int numWallSegments = 50;

        for (int i = 0; i < numWallSegments; i++) {
            // Separación vertical aleatoria
            float verticalGap = MathUtils.random(120f, 250f);
            currentY += verticalGap;

            // Probabilidad de generar plataformas en uno u otro lado
            boolean spawnLeft = MathUtils.randomBoolean(0.9f);  // 90% de probabilidad de que haya en la izquierda
            boolean spawnRight = MathUtils.randomBoolean(0.9f); // 90% de probabilidad de que haya en la derecha

            // Offset vertical adicional aleatorio entre lados
            float offsetY = MathUtils.random(-60f, 80f);

            if (spawnLeft) {
                leftWallPlatforms.add(new Platform(0, currentY, wallTexture, true));
            }

            if (spawnRight) {
                rightWallPlatforms.add(new Platform(SCREEN_WIDTH - wallTexture.getWidth(), currentY + offsetY, wallTexture, false));
            }
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

        // Dibujar las plataformas de la pared izquierda con rotación 90° + variación única
        for (Platform wall : leftWallPlatforms) {
            wall.draw(batch);
        }

        for (Platform wall : rightWallPlatforms) {
            wall.draw(batch);
        }

        // Dibujar al jugador
        batch.draw(player.getTexture(), player.position.x, player.position.y);
        batch.end();
    }

    private void update(float delta) {
        // Detectar toque en pantalla para ejecutar la acción de salto
        if (Gdx.input.isTouched()) {
            if (!player.isChargingJump) {
                player.startJumpCharge();
            }
        } else {
            if (player.isChargingJump) {
                player.releaseJumpCharge();
            }
        }
        // Actualizar al jugador
        player.update(delta);

        // Comprobar colisión del jugador con las plataformas que hacen de pared

        // Para la pared izquierda: queremos que el jugador se "pegue" en el borde derecho del tile
        for (Platform wall : leftWallPlatforms) {
            if (player.bounds.overlaps(wall.bounds)) {
                // El lado izquierdo del jugador se coloca en:
                float newX = wall.position.x + wall.getTexture().getWidth();
                player.position.x = newX;
                player.bounds.setPosition(player.position.x, player.position.y);
                player.currentState = Player.PlayerState.ON_WALL_LEFT;
                player.hasAirBounced = false;
                break;
            }
        }

        // Para la pared derecha: queremos que el lado derecho del jugador se alinee al borde izquierdo del tile
        for (Platform wall : rightWallPlatforms) {
            if (player.bounds.overlaps(wall.bounds)) {
                float newX = wall.position.x - player.getTexture().getWidth();
                player.position.x = newX;
                player.bounds.setPosition(player.position.x, player.position.y);
                player.currentState = Player.PlayerState.ON_WALL_RIGHT;
                player.hasAirBounced = false;
                break;
            }
        }

        // Actualizar la cámara para que siga al jugador verticalmente
        float minCameraY = SCREEN_HEIGHT / 2;
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
