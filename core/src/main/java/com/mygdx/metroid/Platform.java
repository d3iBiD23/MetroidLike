package com.mygdx.metroid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Platform {
    public Vector2 position;
    public Rectangle bounds;
    private Texture texture;
    public Sprite sprite;

    public Platform(float x, float y, Texture texture, boolean rotateLeft) {
        this.position = new Vector2(x, y);
        this.texture = texture;
        this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());

        // Crear sprite con textura
        sprite = new Sprite(texture);
        sprite.setPosition(x, y);

        // Aplicar variación visual solo una vez
        float scale = MathUtils.random(0.95f, 1.05f);
        float rotationOffset = MathUtils.random(-5f, 5f);
        sprite.setScale(scale);

        if (rotateLeft) {
            sprite.setRotation(-90 + rotationOffset);
        } else {
            sprite.setRotation(90 + rotationOffset);
        }
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public Texture getTexture() {
        return texture;
    }
}
