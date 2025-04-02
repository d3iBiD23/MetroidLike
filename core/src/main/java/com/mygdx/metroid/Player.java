package com.mygdx.metroid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Player {
    private float x, y;
    private Texture texture;

    public Player() {
        texture = new Texture(Gdx.files.internal("gothicvania church files/Assets/SPRITES/player/spritesheet/player.png"));
    }

    public void render(SpriteBatch batch){
        batch.draw(texture, x, y);
    }

    public void setPosition(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
}

