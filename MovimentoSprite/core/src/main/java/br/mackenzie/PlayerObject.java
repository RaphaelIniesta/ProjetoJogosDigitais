package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PlayerObject extends GameObject{
    private Viewport viewport;
    private float posX, posY;
    private float velocidadeParalaxe;
    private TextureRegion frameAtual;
    private float stateTimeCorrida;

    private float personagemWidth = 1f;
    private float personagemHeight = 1f;

    Animation<TextureRegion> animacaoCorrida;
    Array<Projetil> projeteis;
    Texture projetilTexture;

    float fundoOffsetY;

    public PlayerObject(Texture texture, float x, float y, float height, float width, float paralaxe, float stateTimeCorrida, Animation<TextureRegion> animacaoCorrida, Array<Projetil> projeteis, float fundoOffsetY, Viewport viewport) {
        super(texture, x, y, width, height);

        this.viewport = viewport;
        this.posX = x;
        this.posY = y;
        this.velocidadeParalaxe = paralaxe;
        this.stateTimeCorrida = stateTimeCorrida;
        this.animacaoCorrida = animacaoCorrida;
        this.projeteis = projeteis;
    }

    @Override
    public void update(float delta) {
        float velocidade = 2f;
//        float delta = Gdx.graphics.getDeltaTime();

        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean up = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);

//        int serialData = arduinoReader.getLastData();
//        System.out.println(serialData);

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            dispararProjetil();
        }

        if(up){
//        if(serialData == 1){
            velocidadeParalaxe = 1f;
            posY += velocidade * delta;
            stateTimeCorrida += delta;
        } else if(down){
//        }else if(serialData == -1){
            velocidadeParalaxe = 0.5f;
            posY -= velocidade * delta;
            stateTimeCorrida += delta;
        } else if(left) {
//            velocidadeParalaxe = 1f;
            posX -= velocidade * delta;
            stateTimeCorrida += delta;
        } else if(right) {
//            velocidadeParalaxe = 0.5f;
            posX += velocidade * delta;
            stateTimeCorrida += delta;
        }

        frameAtual = animacaoCorrida.getKeyFrame(stateTimeCorrida, true);
        fundoOffsetY -= velocidade * delta * velocidadeParalaxe;
        velocidadeParalaxe = 0.75f;
    }

    @Override
    public void draw(SpriteBatch batch) {

    }

    private void dispararProjetil() {
        float spawnX = posX + personagemWidth / 2 - 0.1f;
        float spawnY = posY + personagemHeight;

        Projetil p = new Projetil(spawnX, spawnY, projetilTexture);
        projeteis.add(p);
    }
}
