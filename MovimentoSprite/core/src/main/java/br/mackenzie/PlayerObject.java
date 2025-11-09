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
    Array<Projetil> projeteis;
    private Texture projetilTexture;
    private Viewport viewport;
    private float fundoOffsetY;
    private float velocidadeParalaxe = 0.0f;
    private float maxSpeed = 10;
    private int lives = 3;
    private int score = 0;

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
        float velocidade = 20f;

        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean up = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            dispararProjetil();
        }

        if (up) {
            if(velocidadeParalaxe < maxSpeed) {
                velocidadeParalaxe += 2f;
            }
//            y += velocidade * delta;
            stateTime += delta;
        }
        if (down) {
            if(velocidadeParalaxe > 0f) {
                velocidadeParalaxe -= 2f;
            }
//            y -= velocidade * delta;
            stateTime += delta;
        }
        if (left && velocidadeParalaxe > 1f) {
            x -= (velocidade * delta) * 10;
//            stateTime += delta;
        }
        if (right && velocidadeParalaxe > 1f) {
            x += (velocidade * delta) * 10;
//            stateTime += delta;
        }

        frameAtual = animacaoCorrida.getKeyFrame(stateTime, true);
        fundoOffsetY -= velocidade * delta * velocidadeParalaxe;
        if(velocidadeParalaxe >= 1f) {
            velocidadeParalaxe -= 1f;
        }
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
        float spawnX = x + width / 2 - 1f;
        float spawnY = y + height;
        projeteis.add(new Projetil(spawnX, spawnY, projetilTexture));
    }

    public float getFundoOffsetY() {
        return fundoOffsetY;
    }

    public void checkCollisionWithObstacle(ObstacleObject obstacle) {
        if (obstacle.isActive() && bounds.overlaps(obstacle.getBounds())) {
            lives--;
            obstacle.destroy(); // Se quiser que o obstáculo desapareça ao colidir com o jogador
            System.out.println("Colisão! Vidas restantes: " + lives);

            if (lives <= 0) {
                active = false;
                System.out.println("Jogador derrotado!");
            }
        }
    }

    public int getLives() {
        return lives;
    }


    public void addScore(int amount) {
        score += amount;
    }

    public int getScore() {
        return score;
    }

}
