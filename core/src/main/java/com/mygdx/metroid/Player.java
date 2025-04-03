package com.mygdx.metroid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {
    public enum PlayerState {
        ON_GROUND,
        ON_WALL_LEFT,
        ON_WALL_RIGHT,
        IN_AIR // (si hiciera falta para animaciones o transición)
    }

    public Vector2 position;
    public Vector2 velocity;
    private Texture texture;
    public Rectangle bounds;

    private Texture idleTexture;
    private Texture climbTexture;
    private float climbTimer = 0;
    private static final float CLIMB_DURATION = 0.2f;

    // Valores para la física y el salto
    private static final float GRAVITY = -20f;
    private static final float WALL_JUMP_HORIZONTAL_IMPULSE = 300;
    private static final float WALL_JUMP_VERTICAL_IMPULSE = 1000;
    public PlayerState currentState;

    public Player(float x, float y) {
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        // Cargamos las texturas: idle para estado normal y climb para el salto
        idleTexture = new Texture("PNG/Characters/platformChar_happy.png");
        climbTexture = new Texture("PNG/Characters/platformChar_idle.png");
        texture = idleTexture;
        // Inicializamos la hitbox en base a la textura idle
        bounds = new Rectangle(x, y, idleTexture.getWidth(), idleTexture.getHeight());
        // El jugador comienza en el suelo
        currentState = PlayerState.ON_GROUND;
    }


    public void update(float delta) {
        // Aplicamos la física según el estado actual
        switch (currentState) {
            case IN_AIR:
                // Durante el salto, se aplica la gravedad
                velocity.y += GRAVITY;
                break;
            case ON_WALL_LEFT:
            case ON_WALL_RIGHT:
                // Cuando el jugador está "pegado" a la pared, anulamos la caída
                velocity.y = 0;
                break;
            case ON_GROUND:
                // En el suelo no se aplica gravedad (puedes ajustar según necesites)
                break;
        }

        // Actualizamos la posición con base en la velocidad
        position.add(velocity.x * delta, velocity.y * delta);
        // Actualizamos la hitbox
        bounds.setPosition(position.x, position.y);

        // Controlamos el temporizador de la animación de "rebote" (climb)
        if (climbTimer > 0) {
            climbTimer -= delta;
            if (climbTimer <= 0) {
                texture = idleTexture;
            }
        }
    }

    /**
     * Método a llamar cuando se detecta un toque en pantalla.
     * Según el estado actual, decide a qué pared saltar.
     */
    public void onTap() {
        switch (currentState) {
            case ON_GROUND:
                // Al iniciar desde el suelo, asumimos que salta hacia la pared izquierda
                jumpToWallLeft();
                break;
            case ON_WALL_LEFT:
                // Si está pegado a la pared izquierda, salta hacia la derecha
                jumpToWallRight();
                break;
            case ON_WALL_RIGHT:
                // Si está pegado a la pared derecha, salta hacia la izquierda
                jumpToWallLeft();
                break;
            default:
                // Si ya está en el aire, se ignora el toque (o se puede implementar doble salto)
                break;
        }
    }

    // Realiza el salto hacia la pared izquierda
    private void jumpToWallLeft() {
        velocity.x = -WALL_JUMP_HORIZONTAL_IMPULSE;
        velocity.y = WALL_JUMP_VERTICAL_IMPULSE;
        currentState = PlayerState.IN_AIR;
        texture = climbTexture;
        climbTimer = CLIMB_DURATION;
    }

    // Realiza el salto hacia la pared derecha
    private void jumpToWallRight() {
        velocity.x = WALL_JUMP_HORIZONTAL_IMPULSE;
        velocity.y = WALL_JUMP_VERTICAL_IMPULSE;
        currentState = PlayerState.IN_AIR;
        texture = climbTexture;
        climbTimer = CLIMB_DURATION;
    }

    public Texture getTexture() {
        return texture;
    }

    public void dispose() {
        if (idleTexture != null) {
            idleTexture.dispose();
            idleTexture = null;
        }
        if (climbTexture != null) {
            climbTexture.dispose();
            climbTexture = null;
        }
        texture = null;
    }
}
