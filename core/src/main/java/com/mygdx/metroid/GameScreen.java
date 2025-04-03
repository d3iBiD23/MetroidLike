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

    public GameScreen(Main game) {
        this.game = game;
        this.batch = game.batch;

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
        int numJumpPlatforms = 10;
        float gap = 80; // espacio vertical entre plataformas
        for (int i = 1; i < numJumpPlatforms; i++) {
            float posX = (float) Math.random() * (400 - 50); // asumiendo que el tile tiene ancho ~50
            float posY = groundTexture.getHeight() + i * gap;
            platforms.add(new Platform(posX, posY)); // estas plataformas cargan su propio texture
        }
    }

    @Override
    public void render(float delta) {
        // Limpia la pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // Primero dibujamos todas las plataformas (el suelo y las de salto)
        for (Platform platform : platforms) {
            batch.draw(platform.getTexture(), platform.position.x, platform.position.y);
        }
        // Luego dibujamos al jugador
        batch.draw(player.getTexture(), player.position.x, player.position.y);
        batch.end();
    }

    private void update(float delta) {
        player.update(delta);

        // Manejo de controles táctiles (o de escritorio) para mover al personaje...
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
                if (player.bounds.overlaps(platform.bounds)) {
                    player.jump();
                }
            }
        }

        // Seguimiento de cámara con zona muerta y límite inferior
        float viewportHeight = 800;  // Altura de la cámara
        float deadZone = 50;         // Margen de zona muerta (puedes ajustar este valor)
        float cameraBottom = camera.position.y - viewportHeight / 2;
        float cameraTop = camera.position.y + viewportHeight / 2;
        float targetY = camera.position.y;

        // Si el personaje se acerca al borde inferior, mueve la cámara hacia abajo
        if (player.position.y < cameraBottom + deadZone) {
            targetY = player.position.y - deadZone + viewportHeight / 2;
        }
        // Si el personaje se acerca al borde superior, mueve la cámara hacia arriba
        else if (player.position.y > cameraTop - deadZone) {
            targetY = player.position.y + deadZone - viewportHeight / 2;
        }

        // Impide que la cámara muestre por debajo del suelo al inicio
        float minCameraY = viewportHeight / 2;  // Por ejemplo, si la pantalla es de 800, mínimo es 400
        if (targetY < minCameraY) {
            targetY = minCameraY;
        }

        // Interpola suavemente la posición de la cámara
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
