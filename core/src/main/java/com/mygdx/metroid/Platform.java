package com.mygdx.metroid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Platform {
    public Vector2 position;
    private Texture texture;
    public Rectangle bounds;

    // Constructor por defecto (carga su propio texture)
    public Platform(float x, float y) {
        position = new Vector2(x, y);
        texture = new Texture("PNG/Tiles/platformPack_tile001.png");
        bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    // Nuevo constructor que recibe un texture compartido
    public Platform(float x, float y, Texture texture) {
        position = new Vector2(x, y);
        this.texture = texture;
        bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public Texture getTexture() {
        return texture;
    }

    // Al llamar dispose() se libera el texture (en el caso de objetos propios)
    public void dispose() {
        if(texture != null) {
            texture.dispose();
            texture = null;
        }
    }
}
