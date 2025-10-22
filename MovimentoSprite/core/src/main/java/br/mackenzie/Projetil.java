package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Projetil {
    private float x, y;
    private float velocidade = 2f;
    private boolean ativo = true;
    private Texture texture;
    private Viewport viewport;

    public Projetil(float x, float y, Texture texture) {
        this.x = x;
        this.y = y;
        this.texture = texture;
    }

    public void update(float delta) {
        y += velocidade * delta * 5;

        if (y > Gdx.graphics.getHeight()) {
            ativo = false;
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, 0.15f, 0.3f);
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void dispose() {
        texture.dispose();
    }
}
