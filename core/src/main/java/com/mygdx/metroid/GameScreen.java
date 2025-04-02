package com.mygdx.metroid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class GameScreen extends ScreenAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Texture playerTexture;
    private Animation<TextureRegion> playerAnimation;
    private float stateTime;

    public GameScreen() {
        // Inicializamos el SpriteBatch y la cámara (por ejemplo, de 800x480)
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // Cargamos el spritesheet del jugador
        playerTexture = new Texture(Gdx.files.internal("gothicvania church files/Assets/SPRITES/player/spritesheet/player.png"));

        // Dividimos la textura en frames. Se asume que todos los frames son de 82x60 píxeles.
        TextureRegion[][] tmp = TextureRegion.split(playerTexture, 82, 60);
        // Por simplicidad, usamos la primera fila para una animación "Idle"
        TextureRegion[] idleFrames = new TextureRegion[tmp[0].length];
        for (int i = 0; i < tmp[0].length; i++) {
            idleFrames[i] = tmp[0][i];
        }
        // Creamos una animación con una duración de 0.1 segundos por frame
        playerAnimation = new Animation<TextureRegion>(0.1f, idleFrames);
        stateTime = 0f;
    }

    @Override
    public void render(float delta) {
        // Actualizamos el tiempo de la animación
        stateTime += delta;

        // Limpiamos la pantalla con un color de fondo
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualizamos la cámara y la aplicamos al SpriteBatch
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        // Obtenemos el frame actual de la animación y lo dibujamos
        TextureRegion currentFrame = playerAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, 100, 100);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        playerTexture.dispose();
    }
}
