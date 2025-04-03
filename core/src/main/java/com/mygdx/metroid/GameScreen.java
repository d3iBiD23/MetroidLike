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
    private boolean initialFrame = true;

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
        // Calculamos cuántos tiles necesitamos para cubrir la anchura (400)
        int numTiles = (int) Math.ceil(400 / (float) groundTexture.getWidth()) + 1;
        // Creamos los tiles del suelo y los agregamos a la lista
        for (int i = 0; i < numTiles; i++) {
            platforms.add(new GroundPlatform(i * groundTexture.getWidth(), 0, groundTexture));
        }

        // Posiciona al jugador sobre el suelo, centrado horizontalmente
        float playerWidth = new Texture("PNG/Characters/platformChar_idle.png").getWidth();
        float playerX = 200 - (playerWidth / 2);
        float playerY = groundTexture.getHeight();
        player = new Player(playerX, playerY);

        // Generamos plataformas adicionales para saltar (arriba del suelo)
        int numJumpPlatforms = 200;
        float gap = 150; // espacio vertical entre plataformas
        for (int i = 1; i < numJumpPlatforms; i++) {
            float posX = (float) Math.random() * (400 - 50); // asumiendo que el tile tiene ancho ~50
            float posY = groundTexture.getHeight() + i * gap;
            platforms.add(new Platform(posX, posY)); // estas plataformas cargan su propio texture
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

        // Evitar la detección de colisiones en el primer frame para que el jugador no se "salte" al inicio
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

        // Manejo de controles táctiles (o de escritorio) para mover al personaje
        if (Gdx.input.isTouched()) {
            float touchX = Gdx.input.getX();
            float screenWidth = Gdx.graphics.getWidth();
            if (touchX < screenWidth / 2) {
                player.moveLeft(delta);
            } else {
                player.moveRight(delta);
            }
        }

        // Control de salto: si el personaje cae y toca alguna plataforma, salta
        if (player.velocity.y < 0) {
            for (Platform platform : platforms) {
                if (player.getFootBounds().overlaps(platform.bounds)) {
                    player.jump();
                }
            }
        }

        // Seguimiento de cámara:
        float viewportHeight = 800; // Altura del viewport
        float minCameraY = viewportHeight / 2; // La cámara no baja más allá de la mitad inferior de la pantalla (por ejemplo, 400)

        // Calculamos la diferencia entre la posición del jugador y la de la cámara
        float relativeY = player.position.y - camera.position.y;
        float targetY = camera.position.y;

        // Si el jugador está un poco por encima (por ejemplo, más de +20) se mueve la cámara hacia arriba
        float deadZoneUp = 20;
        // Si el jugador cae mucho (por ejemplo, menos de -100) se mueve la cámara hacia abajo
        float deadZoneDown = -100;

        if (relativeY > deadZoneUp) {
            // El jugador está por encima de la zona "segura", la cámara se mueve para dejarlo 20 píxeles por debajo del tope
            targetY = player.position.y - deadZoneUp;
        } else if (relativeY < deadZoneDown) {
            // El jugador está muy abajo, la cámara se mueve para dejarlo 100 píxeles por encima del fondo
            targetY = player.position.y - deadZoneDown;
        }

        // La cámara no puede bajar más allá del suelo
        targetY = Math.max(targetY, minCameraY);

        // Movemos la cámara suavemente
        camera.position.y = MathUtils.lerp(camera.position.y, targetY, 0.1f);

    }

    @Override
    public void dispose() {
        player.dispose();
        for (Platform platform : platforms) {
            platform.dispose();
        }
        // Liberamos el texture compartido del suelo (ya que GroundPlatform no lo libera)
        if (groundTexture != null) {
            groundTexture.dispose();
            groundTexture = null;
        }
    }
}
