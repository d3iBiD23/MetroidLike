package com.mygdx.metroid;

import com.badlogic.gdx.Gdx;
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

    public boolean hasAirBounced = false;
    public Vector2 position;
    public Vector2 velocity;
    private Texture texture;
    public Rectangle bounds;

    private Texture idleTexture;
    private Texture climbTexture;
    private float climbTimer = 0;
    private static final float CLIMB_DURATION = 0.2f;

    public boolean isJumping = false; // En lugar de isChargingJump.
    private float jumpHoldTime = 0f;    // Tiempo que se mantiene presionado el salto.
    private final float MAX_JUMP_DURATION = 0.5f; // Tiempo máximo (en segundos) en que se aplica la potencia extra.
    private final float INITIAL_JUMP_VELOCITY = 1000f; // Impulso inicial del salto (ajustable según tus necesidades).
    private final float CONTINUOUS_JUMP_BOOST = 50f;    // Impulso extra continuo por segundo mientras se mantenga el toque.
    private float jumpChargeTime = 0f;
    private final float MAX_JUMP_HOLD = 0.5f; // medio segundo para salto completo


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
        // Aplicar física según el estado
        switch (currentState) {
            case IN_AIR:
                velocity.y += GRAVITY;
                break;
            case ON_WALL_LEFT:
            case ON_WALL_RIGHT:
                velocity.y = 0;
                // Opcional: si el jugador está en la pared, desactivar el boost
                isJumping = false;
                break;
            case ON_GROUND:
                isJumping = false;
                break;
        }

        // Actualiza la posición basándose en la velocidad
        position.add(velocity.x * delta, velocity.y * delta);
        bounds.setPosition(position.x, position.y);

        // Aplicar el impulso continuo si se mantiene presionado y no se excede el límite
        if (isJumping) {
            if (Gdx.input.isTouched() && jumpHoldTime < MAX_JUMP_DURATION) {
                jumpHoldTime += delta;
                velocity.y += CONTINUOUS_JUMP_BOOST * delta;
            } else {
                isJumping = false;
            }
        }

        // Actualiza animaciones u otros timers si es necesario...
    }

    /**
     * Metodo a llamar cuando se detecta un toque en pantalla.
     * Según el estado actual, decide a qué pared saltar.
     */
    public void onTap() {
        float baseFactor = 0.3f; // Factor base para el impulso inicial
        switch (currentState) {
            case ON_GROUND:
                jumpToWallLeft(baseFactor);
                break;
            case ON_WALL_LEFT:
                jumpToWallRight(baseFactor);
                break;
            case ON_WALL_RIGHT:
                jumpToWallLeft(baseFactor);
                break;
            case IN_AIR:
                performAirBounce();
                return;
        }
        isJumping = true;
        jumpHoldTime = 0f;
    }

    private void performAirBounce() {
        if (hasAirBounced) return; // ya se hizo un rebote → no hacer nada

        // Cambia dirección horizontal
        if (velocity.x < 0) {
            velocity.x = WALL_JUMP_HORIZONTAL_IMPULSE;
        } else {
            velocity.x = -WALL_JUMP_HORIZONTAL_IMPULSE;
        }

        // Impulso vertical
        velocity.y = WALL_JUMP_VERTICAL_IMPULSE;

        // Animación y textura
        texture = climbTexture;
        climbTimer = CLIMB_DURATION;

        // Marcar que ya se usó el rebote en este salto
        hasAirBounced = true;

        currentState = PlayerState.IN_AIR;
    }

    private void jumpToWallLeft(float powerFactor) {
        velocity.x = -WALL_JUMP_HORIZONTAL_IMPULSE * powerFactor;
        velocity.y = WALL_JUMP_VERTICAL_IMPULSE * powerFactor;
        currentState = PlayerState.IN_AIR;
        texture = climbTexture;
        climbTimer = CLIMB_DURATION;
        hasAirBounced = false;
    }

    private void jumpToWallRight(float powerFactor) {
        velocity.x = WALL_JUMP_HORIZONTAL_IMPULSE * powerFactor;
        velocity.y = WALL_JUMP_VERTICAL_IMPULSE * powerFactor;
        currentState = PlayerState.IN_AIR;
        texture = climbTexture;
        climbTimer = CLIMB_DURATION;
        hasAirBounced = false;
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
