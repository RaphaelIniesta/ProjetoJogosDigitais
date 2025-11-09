package br.mackenzie;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class ObstacleObject extends GameObject {
    private Texture texture;
    private boolean destroyed = false;
    private float speed = 150f; // velocidade de queda
    private float worldWidth, worldHeight;
    private float respawnTimer = 0f;

    public ObstacleObject(float x, float y, float width, float height, Texture texture, float worldWidth, float worldHeight) {
        super(x, y, width, height);
        this.texture = texture;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    @Override
    public void update(float delta) {
        if (!active && destroyed) {
            // Respawn automático 1 segundo após destruição
            respawnTimer += delta;
            if (respawnTimer > 1.0f) {
                respawn();
                respawnTimer = 0;
            }
            return;
        }

        if (!active) return;

        // Movimento: obstáculo desce
        y -= speed * delta;
        bounds.setPosition(x, y);

        // Saiu da tela -> respawn
        if (y + height < 0) {
            respawn();
        }
    }


    private void respawn() {
        x = MathUtils.random(0, worldWidth - width);
        y = worldHeight + MathUtils.random(1f, 3f);
        active = true;
        destroyed = false;
        bounds.setPosition(x, y);
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (active) {
            batch.draw(texture, x, y, width, height);
        }
    }

    public boolean collidesWith(com.badlogic.gdx.math.Rectangle other) {
        return active && bounds.overlaps(other);
    }

    public void destroy() {
        active = false;
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void dispose() {
        texture.dispose();
    }
}
