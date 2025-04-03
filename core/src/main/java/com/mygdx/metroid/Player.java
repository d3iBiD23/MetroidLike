package com.mygdx.metroid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {
    public Vector2 position;
    public Vector2 velocity;
    private Texture texture;
    public Rectangle bounds;
    private Rectangle footBounds; // Nueva hitbox para los pies (por ejemplo, 10 píxeles de alto, ajústalo según tu sprite)
    private static final float GRAVITY = -20f;
    private static final float JUMP_VELOCITY = 1000;
    private static final float MOVE_SPEED = 200;
    private Texture idleTexture;
    private Texture climbTexture;
    private float climbTimer = 0;
    private static final float CLIMB_DURATION = 0.2f;

    public Player(float x, float y) {
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        // Cargamos ambas texturas
        idleTexture = new Texture("PNG/Characters/platformChar_happy.png");
        climbTexture = new Texture("PNG/Characters/platformChar_idle.png");
        texture = idleTexture; // Textura inicial es la idle
        bounds = new Rectangle(x, y, idleTexture.getWidth(), idleTexture.getHeight());
        float footWidth = idleTexture.getWidth() * 0.5f; // ancho reducido para los pies
        footBounds = new Rectangle(x + (idleTexture.getWidth() - footWidth) / 2, y, footWidth, 10);
    }

    public void update(float delta) {
        velocity.y += GRAVITY;
        position.add(velocity.x * delta, velocity.y * delta);

        // Actualizamos las hitboxes a la posición actual
        bounds.setPosition(position.x, position.y);
        footBounds.setPosition(position.x + (idleTexture.getWidth() - footBounds.width) / 2, position.y);

        // Limitar la posición en X para que el sprite no se salga de la pantalla
        position.x = MathUtils.clamp(position.x, 0, 400 - idleTexture.getWidth());

        // Si estamos en estado "climb", disminuimos el temporizador
        if (climbTimer > 0) {
            climbTimer -= delta;
            if (climbTimer <= 0) {
                texture = idleTexture; // Vuelve al estado idle cuando se agota el tiempo
            }
        }
    }

    // Al llamar jump() se activa el estado "climb" durante un corto período
    public void jump() {
        velocity.y = JUMP_VELOCITY;
        texture = climbTexture;
        climbTimer = CLIMB_DURATION;
    }

    public void moveLeft(float delta) {
        position.x -= MOVE_SPEED * delta;
    }

    public void moveRight(float delta) {
        position.x += MOVE_SPEED * delta;
    }

    public Texture getTexture() {
        return texture;
    }

    public Rectangle getFootBounds() {
        return footBounds;
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
