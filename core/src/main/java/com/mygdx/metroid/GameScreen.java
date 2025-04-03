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

        // Cargamos una textura para el suelo (opcional)
        groundTexture = new Texture("PNG/Tiles/platformPack_tile015.png");

        // Posiciona al jugador sobre el suelo, centrado horizontalmente
        // Se utiliza la anchura de la textura idle del jugador para centrarlo
        float playerWidth = new Texture("PNG/Characters/platformChar_happy.png").getWidth();
        float playerX = (SCREEN_WIDTH - playerWidth) / 2;
        float playerY = groundTexture.getHeight(); // Comienza justo sobre el suelo
        player = new Player(playerX, playerY);
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
        // Dibujar el jugador
        batch.draw(player.getTexture(), player.position.x, player.position.y);
        batch.end();
    }

    private void update(float delta) {
        // Detectar toque en pantalla para ejecutar la acción de salto
        if (Gdx.input.justTouched()) {
            player.onTap();
        }

        // Actualizar el jugador
        player.update(delta);

        // Comprobar colisión con las paredes y ajustar el estado
        if (player.position.x <= 0) {
            // Colisión con la pared izquierda
            player.position.x = 0;
            player.currentState = Player.PlayerState.ON_WALL_LEFT;
        } else if (player.position.x + player.getTexture().getWidth() >= SCREEN_WIDTH) {
            // Colisión con la pared derecha
            player.position.x = SCREEN_WIDTH - player.getTexture().getWidth();
            player.currentState = Player.PlayerState.ON_WALL_RIGHT;
        }

        // Actualizar la cámara para que siga al jugador verticalmente
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
    }
}
