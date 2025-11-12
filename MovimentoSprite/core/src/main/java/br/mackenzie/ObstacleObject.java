package br.mackenzie;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class ObstacleObject extends GameObject {
    private Texture[][] obstacleTextures;
    private Texture texture;

    private boolean destroyed = false;
    private float speed = 100f;
    private float worldWidth, worldHeight;
    private float respawnTimer = 1f;

    private int type;
    private int hitsToDestroy;
    private int hitCount = 0;

    private float shootTimer = 0;
    private float shootInterval = 3f;

    public interface OnEnemyShootListener {
        void onEnemyShoot(float x, float y);
    }

    private OnEnemyShootListener shootListener;

    public void setShootListener(OnEnemyShootListener listener) {
        this.shootListener = listener;
    }

    public ObstacleObject(float x, float y, float width, float height, Texture[][] obstacleTextures, float worldWidth, float worldHeight, int type) {
        super(x, y, width, height);

        this.bounds = new Rectangle(x, y, width, height);
        this.obstacleTextures = obstacleTextures;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.type = type;
        this.active = true;

        // Define resistência conforme o tipo
        switch (type) {
            case 0: hitsToDestroy = 1; break; // simples
            case 1: hitsToDestroy = 3; break; // resistente
            case 2: hitsToDestroy = 1; break; // atirador
        }

        // textura inicial
        texture = obstacleTextures[type][MathUtils.random(0, 2)];
    }

    @Override
    public void update(float delta) {
        if (!active && destroyed) {
            respawnTimer += delta;
            if (respawnTimer > 1.0f) {
                respawn();
                respawnTimer = 0;
            }
            return;
        }

        if (!active) return;

        // Movimento: desce
        y -= speed * delta;
        bounds.setPosition(x, y);

        // Tipo 2: atira periodicamente
        if (type == 2) {
            shootTimer += delta;
            if (shootTimer > shootInterval) {
                shootTimer = 0;
                System.out.println("Obstáculo tipo 2 disparou!");
                shootAtPlayer();
            }
        }

        // Saiu da tela -> respawn
        if (y + height < 0) {
            respawn();
        }
    }

    private void respawn() {
        // Escolhe o tipo conforme o nível de dificuldade global
        // dificuldade: 0 -> só tipo 0, 1 -> tipos 0 e 1, 2 -> tipos 0, 1 e 2
        int maxType = Math.min(2, Main.difficultyLevel);
        type = MathUtils.random(0, maxType);

        // Reseta atributos
        x = MathUtils.random(0, worldWidth - width);
        y = worldHeight + MathUtils.random(50f, 300f);
        destroyed = false;
        active = true;
        hitCount = 0;
        respawnTimer = 0;

        // Reatribui a textura e a quantidade de hits
        texture = obstacleTextures[type][MathUtils.random(0, 2)];
        switch (type) {
            case 0:
                hitsToDestroy = 1;
                break;
            case 1:
                hitsToDestroy = 3;
                break;
            case 2:
                hitsToDestroy = 1;
                break;
        }

        bounds.setPosition(x, y);
    }


    @Override
    public void draw(SpriteBatch batch) {
        if (active && texture != null) {
            batch.draw(texture, x, y, width, height);
        }
    }

    public boolean collidesWith(Rectangle other) {
        return active && bounds.overlaps(other);
    }

    public int getType() {
        return type;
    }

    public void hit() {
        hitCount++;
        if (hitCount >= hitsToDestroy) {
            destroy();
        }
    }

    public void destroy() {
        active = false;
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    private void shootAtPlayer() {
        if (shootListener != null) {
            shootListener.onEnemyShoot(x + width / 2f, y);
        }
    }

    public void dispose() {
        // texturas são compartilhadas, não dispose aqui
    }
}
