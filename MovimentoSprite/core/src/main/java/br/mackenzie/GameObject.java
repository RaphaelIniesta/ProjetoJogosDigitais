package br.mackenzie;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class GameObject {
    protected float x, y;
    protected float width, height;
    protected Rectangle bounds;
    protected boolean active = true;

    public GameObject(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(x, y, width, height);
    }

    public abstract void update(float delta);
    public abstract void draw(SpriteBatch batch);

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public void setX(float x) { this.x = x; bounds.x = x; }
    public void setY(float y) { this.y = y; bounds.y = y; }
}
