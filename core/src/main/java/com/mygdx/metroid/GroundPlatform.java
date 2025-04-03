package com.mygdx.metroid;

import com.badlogic.gdx.graphics.Texture;

public class GroundPlatform {
    public GroundPlatform(float x, float y, Texture sharedTexture) {
    }
    public void dispose() {
        // No se llama a texture.dispose() porque es compartido
    }
}
