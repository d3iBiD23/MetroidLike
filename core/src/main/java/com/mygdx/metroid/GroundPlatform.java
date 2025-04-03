package com.mygdx.metroid;

import com.badlogic.gdx.graphics.Texture;

public class GroundPlatform extends Platform {
    public GroundPlatform(float x, float y, Texture sharedTexture) {
        super(x, y, sharedTexture);
    }
    @Override
    public void dispose() {
        // No se llama a texture.dispose() porque es compartido
        bounds = null;
        position = null;
    }
}
