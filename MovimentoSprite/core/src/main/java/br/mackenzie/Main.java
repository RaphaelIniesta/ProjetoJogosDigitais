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
    Texture obstacleTexture;
    BitmapFont font;
    int scoreToAdd = 100;

    // Labels
    private Stage uiStage;
    private Label vidas;
    private Label score;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(800, 500);
        fundo = new Texture("Background.png");
        projetilTexture = new Texture("rock1.png");
        player = new PlayerObject(viewport.getWorldWidth() / 2.25f, viewport.getWorldHeight() / 6, 100f, 100f, viewport, projetilTexture);
        uiStage = new Stage(viewport);

        // Iniciar música
        music = Gdx.audio.newMusic(Gdx.files.internal("music.wav"));
        music.setLooping(true);
        music.setVolume(0.0f);
        music.play();

        font = new BitmapFont();
        font.getData().setScale(1.5f);
        Label.LabelStyle style = new Label.LabelStyle(font, Color.BLACK);

        vidas = new Label("Vidas: ", style);

        score = new Label("Score: ", style);

        uiStage.addActor(vidas);
        uiStage.addActor(score);

        // Inicia a thread de leitura serial
        // Mude "COM3" para a porta do seu Arduino
//        arduinoReader = new ArduinoReaderThread("COM3");
//        arduinoReader.start();

        obstacleTexture = new Texture("Rectangle 11.png");
        obstacles = new Array<>();

        for (int i = 0; i < 5; i++) {
            float ox = MathUtils.random(0, viewport.getWorldWidth() - 1f);
            float oy = MathUtils.random(2f, viewport.getWorldHeight() * 2); // acima da tela
            obstacles.add(new ObstacleObject(ox, oy, 80f, 80f, obstacleTexture, viewport.getWorldWidth(), viewport.getWorldHeight()));
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

        uiStage.act(delta);
        uiStage.draw();

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
                    player.addScore(scoreToAdd);
                    System.out.println("Obstáculo destruído! Pontuação: " + player.getScore());
                }
            }
        }


        ScreenUtils.clear(Color.WHITE);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

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

        player.draw(spriteBatch);

        font.draw(spriteBatch, "Vidas: " + player.getLives(), 0.2f, viewport.getWorldHeight() - 0.2f);
        font.draw(spriteBatch, "Pontos: " + player.getScore(), 0.2f, viewport.getWorldHeight() - 0.6f);

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
        obstacleTexture.dispose();
        for (ObstacleObject o : obstacles) o.dispose();

    }
}
