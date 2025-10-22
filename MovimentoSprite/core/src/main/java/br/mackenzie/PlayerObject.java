package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PlayerObject extends GameObject {
    private Animation<TextureRegion> animacaoCorrida;
    private float stateTime;
    private TextureRegion frameAtual;
    private Array<Projetil> projeteis;
    private Texture projetilTexture;
    private Viewport viewport;
    private float fundoOffsetY;
    private float velocidadeParalaxe = 0.75f;

    public PlayerObject(float x, float y, float width, float height, Viewport viewport, Texture projetilTexture) {
        super(x, y, width, height);
        this.viewport = viewport;
        this.projeteis = new Array<>();
        this.projetilTexture = projetilTexture;

        TextureRegion[] frames = new TextureRegion[12];
        for (int i = 0; i < 12; i++) {
            Texture t = new Texture("Rectangle " + (i + 1) + ".png");
            frames[i] = new TextureRegion(t);
        }
        animacaoCorrida = new Animation<>(0.1f, frames);
        frameAtual = frames[0];
    }

    @Override
    public void update(float delta) {
        input(delta);
        logic(delta);

        for (int i = projeteis.size - 1; i >= 0; i--) {
            Projetil p = projeteis.get(i);
            p.update(delta);
            if (!p.isAtivo()) {
                projeteis.removeIndex(i);
            }
        }
    }

    private void input(float delta) {
        float velocidade = 2f;

        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean up = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            dispararProjetil();
        }

        if (up) {
            velocidadeParalaxe = 1f;
            y += velocidade * delta;
            stateTime += delta;
        } else if (down) {
            velocidadeParalaxe = 0.5f;
            y -= velocidade * delta;
            stateTime += delta;
        } else if (left) {
            x -= velocidade * delta;
            stateTime += delta;
        } else if (right) {
            x += velocidade * delta;
            stateTime += delta;
        }

        frameAtual = animacaoCorrida.getKeyFrame(stateTime, true);
        fundoOffsetY -= velocidade * delta * velocidadeParalaxe;
        velocidadeParalaxe = 0.75f;
    }

    private void logic(float delta) {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        x = MathUtils.clamp(x, 0, worldWidth - width);
        y = MathUtils.clamp(y, 0, worldHeight - height);

        bounds.setPosition(x, y);
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(frameAtual, x, y, width, height);
        for (Projetil p : projeteis) {
            p.draw(batch);
        }
    }

    private void dispararProjetil() {
        float spawnX = x + width / 2 - 0.1f;
        float spawnY = y + height;
        projeteis.add(new Projetil(spawnX, spawnY, projetilTexture));
    }

    public float getFundoOffsetY() {
        return fundoOffsetY;
    }
}
