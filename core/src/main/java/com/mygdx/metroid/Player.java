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

    public Player(float x, float y) {
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        texture = new Texture("PNG/Characters/platformChar_idle.png");
        bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        float footWidth = texture.getWidth() * 0.5f; // ancho reducido a la mitad

        // Definimos la hitbox de los pies en la parte inferior del sprite
        footBounds = new Rectangle(x + (texture.getWidth() - footWidth) / 2, y, footWidth, 10);
    }

    public void update(float delta) {
        velocity.y += GRAVITY;
        position.add(velocity.x * delta, velocity.y * delta);

        // Actualizamos ambas hitboxes a la posición actual del jugador
        bounds.setPosition(position.x, position.y);
        footBounds.setPosition(position.x + (texture.getWidth() - footBounds.width) / 2, position.y);
        // Limitar la posición en X para que no se salga de los bordes
        position.x = MathUtils.clamp(position.x, 0, 400 - texture.getWidth());
    }

    // Metodo para obtener la hitbox de los pies
    public Rectangle getFootBounds() {
        return footBounds;
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

    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }
}
