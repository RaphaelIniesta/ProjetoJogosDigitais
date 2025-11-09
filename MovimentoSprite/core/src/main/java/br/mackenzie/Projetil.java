package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Projetil {
    private float x, y;
    private float width = 15f;
    private float height = 15f;
    private float velocidade = 20f;
    private boolean ativo = true;
    private Texture texture;
    private Rectangle bounds;

    public Projetil(float x, float y, Texture texture) {
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.bounds = new Rectangle(x, y, width, height);
    }

    public void update(float delta) {
        y += velocidade * delta * 45f;
        bounds.setPosition(x, y);

        if (y > Gdx.graphics.getHeight()) {
            ativo = false;
        }
    }

    public void draw(SpriteBatch batch) {
        if (ativo)
            batch.draw(texture, x, y, width, height);
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void destroy() {
        ativo = false;
    }

    public void dispose() {
        texture.dispose();
    }
}
