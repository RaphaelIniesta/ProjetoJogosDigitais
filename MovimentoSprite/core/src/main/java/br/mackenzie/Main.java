package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    Array<Projetil> projeteis;
    PlayerObject player;

    // Fundo
    Texture fundo;
    float fundoOffsetY = 0f;
    float velocidadeParalaxe = 0.75f; // fundo mais lento que personagem

    // Animações
    Animation<TextureRegion> animacaoCorrida;
    float stateTimeCorrida;
    TextureRegion frameAtual;
    private ArduinoReaderThread arduinoReader;

    // Personagem
    Rectangle personagemRetangulo;
    float posX, posY;

    // Tamanho do personagem
    float personagemWidth = 1f;
    float personagemHeight = 1f;

    Texture projetilTexture;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
        fundo = new Texture("Background.png");
        projetilTexture = new Texture("Rectangle 1.png");

        TextureRegion[] framesCorrida = new TextureRegion[12];
        for (int i = 0; i < 12; i++) {
            Texture t = new Texture("Rectangle " + (i + 1) + ".png");
            framesCorrida[i] = new TextureRegion(t);
        }
        animacaoCorrida = new Animation<>(0.1f, framesCorrida);

        // Estado inicial
        frameAtual = framesCorrida[0];
        stateTimeCorrida = 0f;

        // Inicia a thread de leitura serial
        // Mude "COM3" para a porta do seu Arduino
//        arduinoReader = new ArduinoReaderThread("COM3");
//        arduinoReader.start();

        // Posição inicial
        posX = viewport.getWorldWidth() / 2;
        posY = viewport.getWorldHeight() / 2;
        personagemRetangulo = new Rectangle(posX, posY, personagemWidth, personagemHeight);

        projeteis = new Array<>();
        player = new PlayerObject()
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    private void input() {
//        float velocidade = 2f;
//        float delta = Gdx.graphics.getDeltaTime();
//
//        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
//        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
//        boolean up = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
//        boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);
//
////        int serialData = arduinoReader.getLastData();
////        System.out.println(serialData);
//
//        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
//            dispararProjetil();
//        }
//
//        if(up){
////        if(serialData == 1){
//            velocidadeParalaxe = 1f;
//            posY += velocidade * delta;
//            stateTimeCorrida += delta;
//        } else if(down){
////        }else if(serialData == -1){
//            velocidadeParalaxe = 0.5f;
//            posY -= velocidade * delta;
//            stateTimeCorrida += delta;
//        } else if(left) {
////            velocidadeParalaxe = 1f;
//            posX -= velocidade * delta;
//            stateTimeCorrida += delta;
//        } else if(right) {
////            velocidadeParalaxe = 0.5f;
//            posX += velocidade * delta;
//            stateTimeCorrida += delta;
//        }
//
//        frameAtual = animacaoCorrida.getKeyFrame(stateTimeCorrida, true);
//        fundoOffsetY -= velocidade * delta * velocidadeParalaxe;
//        velocidadeParalaxe = 0.75f;
    }

    private void logic() {
        float delta = Gdx.graphics.getDeltaTime();

        // Limite horizontal da tela
        float worldWidth = viewport.getWorldWidth();
        posX = MathUtils.clamp(posX, 0, worldWidth - personagemWidth);

        personagemRetangulo.set(posX, posY, personagemWidth, personagemHeight);

        // Limite vertical da tela
        float worldHeight = viewport.getWorldHeight();
        posY = MathUtils.clamp(posY, 0, worldHeight - personagemHeight);

        personagemRetangulo.set(posX, posY, personagemWidth, personagemHeight);

        for (int i = projeteis.size - 1; i >= 0; i--) {
            Projetil projetil = projeteis.get(i);
            projetil.update(delta);
            if(!projetil.isAtivo()) {
                projeteis.removeIndex(i);
            }
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.WHITE);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();

        // Fundo (preenchendo toda a tela)
        float fundoWidth = viewport.getWorldWidth();
        float fundoHeight = viewport.getWorldHeight();

        float y1 = fundoOffsetY % fundoHeight;
        if (y1 > 0) y1 -= fundoHeight;

        spriteBatch.draw(fundo, 0, y1, fundoWidth, fundoHeight);
        spriteBatch.draw(fundo, 0, y1 + fundoHeight, fundoWidth, fundoHeight);

        spriteBatch.draw(frameAtual, posX, posY, personagemWidth, personagemHeight);

        for (Projetil projetil : projeteis) {
            projetil.draw(spriteBatch);
        }

        spriteBatch.end();
    }

    private void dispararProjetil() {
        float spawnX = posX + personagemWidth / 2 - 0.1f;
        float spawnY = posY + personagemHeight;

        Projetil p = new Projetil(spawnX, spawnY, projetilTexture);
        projeteis.add(p);
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
