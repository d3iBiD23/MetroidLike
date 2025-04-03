package com.mygdx.metroid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {
    public Vector2 position;
    public Vector2 velocity;
    private Texture texture;
    public Rectangle bounds;
    private static final float GRAVITY = -20f;
    private static final float JUMP_VELOCITY = 500;
    private static final float MOVE_SPEED = 200;

    public Player(float x, float y) {
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        // Asegúrate de tener la imagen en la ruta correcta
        texture = new Texture("PNG/Characters/platformChar_idle.png");
        bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public void update(float delta) {
        velocity.y += GRAVITY;
        position.add(velocity.x * delta, velocity.y * delta);
        bounds.setPosition(position.x, position.y);
    }

    public void jump() {
        velocity.y = JUMP_VELOCITY;
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

    // Metodo para liberar el recurso
    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }
}
