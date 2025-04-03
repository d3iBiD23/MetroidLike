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

    public boolean hasAirBounced = false;
    public Vector2 position;
    public Vector2 velocity;
    private Texture texture;
    public Rectangle bounds;

    private Texture idleTexture;
    private Texture climbTexture;
    private float climbTimer = 0;
    private static final float CLIMB_DURATION = 0.2f;

    public boolean isChargingJump = false;
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

        if (isChargingJump) {
            jumpChargeTime += delta;
        }
    }

    /**
     * Metodo a llamar cuando se detecta un toque en pantalla.
     * Según el estado actual, decide a qué pared saltar.
     */
    public void onTap() {
        float factor = MathUtils.clamp(jumpChargeTime / MAX_JUMP_HOLD, 0.3f, 1f);
        switch (currentState) {
            case ON_GROUND:
                // Primer salto: lanzamos hacia la izquierda
                jumpToWallLeft(factor);
                break;
            case ON_WALL_LEFT:
                // Saltar hacia la derecha
                jumpToWallRight(factor);
                break;
            case ON_WALL_RIGHT:
                // Saltar hacia la izquierda
                jumpToWallLeft(factor);
                break;
            case IN_AIR:
                // Rebote en el aire → cambia de dirección y aplica impulso
                performAirBounce();
                break;
        }
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

    public void startJumpCharge() {
        if (currentState == PlayerState.ON_WALL_LEFT || currentState == PlayerState.ON_WALL_RIGHT || currentState == PlayerState.ON_GROUND) {
            isChargingJump = true;
            jumpChargeTime = 0f;
        }
    }

    public void releaseJumpCharge() {
        if (!isChargingJump) return;

        isChargingJump = false;

        // Determinar fuerza del salto en base al tiempo de carga
        float factor = MathUtils.clamp(jumpChargeTime / MAX_JUMP_HOLD, 0.3f, 1f);

        if (currentState == PlayerState.ON_WALL_LEFT) {
            jumpToWallRight(factor);
        } else if (currentState == PlayerState.ON_WALL_RIGHT) {
            jumpToWallLeft(factor);
        } else if (currentState == PlayerState.ON_GROUND) {
            jumpToWallLeft(factor); // o hacia la derecha si prefieres
        }
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
