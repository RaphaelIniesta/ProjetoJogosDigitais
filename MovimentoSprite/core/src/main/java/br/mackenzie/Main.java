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
import com.badlogic.gdx.utils.Array;
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

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
        fundo = new Texture("Background.png");
        projetilTexture = new Texture("Rectangle 1.png");
        player = new PlayerObject(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 1f, 1f, viewport, projetilTexture);

        // Iniciar música
        music = Gdx.audio.newMusic(Gdx.files.internal("music.wav"));
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();

        // Inicia a thread de leitura serial
        // Mude "COM3" para a porta do seu Arduino
//        arduinoReader = new ArduinoReaderThread("COM3");
//        arduinoReader.start();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        player.update(delta);

        ScreenUtils.clear(Color.WHITE);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();

        // Fundo (loop vertical)
        float fundoWidth = viewport.getWorldWidth();
        float fundoHeight = viewport.getWorldHeight();

        float y1 = player.getFundoOffsetY() % fundoHeight;
        if (y1 > 0) y1 -= fundoHeight;
        spriteBatch.draw(fundo, 0, y1, fundoWidth, fundoHeight);
        spriteBatch.draw(fundo, 0, y1 + fundoHeight, fundoWidth, fundoHeight);

        player.draw(spriteBatch);
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
    }
}
