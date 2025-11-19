package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;
    FitViewport viewport;
    PlayerObject player;
    Music music;
    // Fundo
    Texture fundo;

    // Animações
    Animation<TextureRegion> animacaoCorrida;
    Texture projetilTexture;

    // Obstáculos
    Array<ObstacleObject> obstacles;
    Array<Projetil> enemyProjectiles;
    Texture[][] obstacleTextures;
    BitmapFont font;

    // Labels
    private Stage uiStage;
    private Label vidas;

    float elapsedTime = 0f; // adicione como atributo na classe Main
    public static int difficultyLevel = 2;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(800, 500);
        fundo = new Texture("Background.png");
        projetilTexture = new Texture("rock1.png");
        player = new PlayerObject(viewport.getWorldWidth() / 2.25f, viewport.getWorldHeight() / 6, 100f, 150f, viewport, projetilTexture);
        uiStage = new Stage(viewport);
        obstacleTextures = new Texture[3][3];
        enemyProjectiles = new Array<>();

        // Iniciar música
        music = Gdx.audio.newMusic(Gdx.files.internal("music.wav"));
        music.setLooping(true);
        music.setVolume(0.0f);
        music.play();

        font = new BitmapFont();
        font.getData().setScale(1.5f);
        Label.LabelStyle style = new Label.LabelStyle(font, Color.BLACK);

        vidas = new Label("Vidas: ", style);


        uiStage.addActor(vidas);

        // Inicia a thread de leitura serial
        // Mude "COM3" para a porta do seu Arduino
//        arduinoReader = new ArduinoReaderThread("COM3");
//        arduinoReader.start();

        obstacleTextures[0][0] = new Texture("tora1.png");
        obstacleTextures[0][1] = new Texture("tora2.png");
        obstacleTextures[0][2] = new Texture("tora3.png");

        obstacleTextures[1][0] = new Texture("lixo1.png");
        obstacleTextures[1][1] = new Texture("lixo2.png");
        obstacleTextures[1][2] = new Texture("lixo3.png");

        obstacleTextures[2][0] = new Texture("sapo1.png");
        obstacleTextures[2][1] = new Texture("sapo2.png");
        obstacleTextures[2][2] = new Texture("sapo3.png");

        obstacles = new Array<>();

        for (int i = 0; i < 5; i++) {
            int type = 0; // Começa com os simples
            float ox = MathUtils.random(0, viewport.getWorldWidth() - 80f);
            float oy = MathUtils.random(viewport.getWorldHeight(), viewport.getWorldHeight() * 2);
            obstacles.add(new ObstacleObject(ox, oy, 80f, 80f, obstacleTextures, viewport.getWorldWidth(), viewport.getWorldHeight(), type));
            obstacles.peek().setShootListener((ex, ey) -> {
                Projetil enemyProj = new Projetil(ex, ey, projetilTexture);
                enemyProj.setSpeed(-45f); // vai para baixo
                enemyProjectiles.add(enemyProj);
            });

        }

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        player.update(delta);

        if (player.getLives() <= 0) {
            Gdx.app.exit();
            return;
        }

        uiStage.act(delta);
        uiStage.draw();

        // Aumenta dificuldade gradualmente
        if (elapsedTime > 30 && difficultyLevel < 1) difficultyLevel = 1;
        if (elapsedTime > 60 && difficultyLevel < 2) difficultyLevel = 2;

        player.update(delta);
        uiStage.act(delta);
        uiStage.draw();

        for (ObstacleObject o : obstacles) {
            o.update(delta);

            // Filtro de spawn (tipos disponíveis)
            if (o.getType() > difficultyLevel) {
                o.destroy(); // ainda não liberado
                continue;
            }

            player.checkCollisionWithObstacle(o);

            for (Projetil p : new Array<>(player.projeteis)) {
                if (o.isActive() && p.isAtivo() && o.collidesWith(p.getBounds())) {
                    o.hit(); // reduz vida do obstáculo
                    p.destroy();
                }
            }
        }

        // Atualiza obstáculos
        for (ObstacleObject o : obstacles) {
            o.update(delta);

            // Colisão jogador ↔ obstáculo
            player.checkCollisionWithObstacle(o);

            // Colisão projétil ↔ obstáculo
            for (Projetil p : new Array<>(player.projeteis)) {
                if (o.isActive() && p.isAtivo() && o.collidesWith(p.getBounds())) {
                    o.destroy();
                    p.destroy();
                }
            }
        }


        ScreenUtils.clear(Color.WHITE);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        for (Projetil ep : new Array<>(enemyProjectiles)) {
            ep.update(delta);
            if (!ep.isAtivo()) {
                enemyProjectiles.removeValue(ep, true);
                continue;
            }

            // Colisão com o jogador
            if (player.getBounds().overlaps(ep.getBounds())) {
                ep.destroy();
                player.takeDamage(1);
                System.out.println("O jogador foi atingido!");
            }
        }

        spriteBatch.begin();
        // Fundo
        float fundoWidth = viewport.getWorldWidth();
        float fundoHeight = viewport.getWorldHeight();

        float y1 = player.getFundoOffsetY() % fundoHeight;
        if (y1 > 0) y1 -= fundoHeight;
        spriteBatch.draw(fundo, 0, y1, fundoWidth, fundoHeight);
        spriteBatch.draw(fundo, 0, y1 + fundoHeight, fundoWidth, fundoHeight);

        // Desenha os obstáculos
        for (ObstacleObject o : obstacles) {
            o.draw(spriteBatch);
        }

        for (Projetil ep : enemyProjectiles) {
            ep.draw(spriteBatch);
        }

        player.draw(spriteBatch);

        font.draw(spriteBatch, "Vidas: " + player.getLives(), 0.2f, viewport.getWorldHeight() - 0.2f);
        spriteBatch.end();
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void dispose() {
        spriteBatch.dispose();
        fundo.dispose();
        for (TextureRegion t : animacaoCorrida.getKeyFrames()) t.getTexture().dispose();
        font.dispose();
        for (ObstacleObject o : obstacles) o.dispose();

    }
}
