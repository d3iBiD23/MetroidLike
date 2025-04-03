package com.mygdx.metroid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Platform {
    public Vector2 position;
    private Texture texture;
    public Rectangle bounds;

    public Platform(float x, float y) {
        position = new Vector2(x, y);
        // Asegúrate de tener la imagen de la plataforma en la carpeta correspondiente (ej. "assets/Tiles/platform.png")
        texture = new Texture("PNG/Tiles/platformPack_tile001.png");
        bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public Texture getTexture() {
        return texture;
    }

}
