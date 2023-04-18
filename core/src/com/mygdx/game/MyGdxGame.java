package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;

	private ShapeRenderer shapeRenderer;
	private Circle circuloPassaro;
	private Rectangle retanguloCanoCima;
	private Rectangle rentaguloCanoBaixo;

	private float larguraDispositivo;
	private float alturaDispositivo;
	private float variacao = 0;
	private float gravidade = 2;
	private float posiçãoInicialVerticalPassaro = 0;
	private float posiçãoCanoHorizontal;
	private float posiçãoCanoVertical;
	private float espaçoEntreCanos;
	private Random random;
	private int pontos = 0;
	private int pontuacaoMaxima = 0;
	private boolean passsouCano = false;
	private int estadoJogo = 0;
	private float posicaoHorizontalPassaro = 0;

	BitmapFont textoPontuação;
	BitmapFont textoReiniciar;
	BitmapFont textoMelhorPontuação;

	Sound somVoando;
	Sound somColisão;
	Sound somPontuação;

	Preferences preferencias;

	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 720;
	private final float VIRTUAL_HEIGHT = 1280;
	
	@Override
	public void create () {
	inicializarTexturas();
	inicializarObjetos();
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisoes();
	}

	private void inicializarTexturas(){
		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
		gameOver = new Texture("game_over.png");
	}
	private void inicializarObjetos(){
		batch = new SpriteBatch();
		random = new Random();

		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;
		posiçãoInicialVerticalPassaro = alturaDispositivo / 2;
		posiçãoCanoHorizontal = larguraDispositivo;
		espaçoEntreCanos = 350;

		textoPontuação = new BitmapFont();
		textoPontuação.setColor(Color.WHITE);
		textoPontuação.getData().setScale(10);

		textoReiniciar = new BitmapFont();
		textoReiniciar.setColor(Color.GREEN);
		textoReiniciar.getData().setScale(2);

		textoMelhorPontuação = new BitmapFont();
		textoMelhorPontuação.setColor(Color.RED);
		textoMelhorPontuação.getData().setScale(2);

		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		rentaguloCanoBaixo = new Rectangle();
		retanguloCanoCima = new Rectangle();

		somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisão = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuação = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));

		preferencias = Gdx.app.getPreferences("flappyBird");
		pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima", 0);

		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

	}

	private void verificarEstadoJogo(){
		boolean toqueTela = Gdx.input.justTouched();
		if (estadoJogo == 0){
			if (toqueTela){
				gravidade = -15;
				estadoJogo = 1;
				somVoando.play();
			}
		}else if( estadoJogo == 1){
				if (toqueTela){
					gravidade = -15;
					somVoando.play();
				}
				posiçãoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;
				if (posiçãoCanoHorizontal <  -canoTopo.getWidth()){
					posiçãoCanoHorizontal = larguraDispositivo;
					posiçãoCanoVertical = random.nextInt(400) - 200;
					passsouCano = false;
				}
				if (posiçãoInicialVerticalPassaro > 0 || toqueTela)
					posiçãoInicialVerticalPassaro = posiçãoInicialVerticalPassaro - gravidade;
				gravidade++;

		}else if(estadoJogo == 2){
				if (pontos > pontuacaoMaxima){
					pontuacaoMaxima = pontos;
					preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);
					preferencias.flush();
				}
				posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime()*500;

				if (toqueTela){
					estadoJogo = 0;
					pontos = 0;
					gravidade = 0;
					posicaoHorizontalPassaro = 0;
					posiçãoInicialVerticalPassaro = alturaDispositivo / 2;
					posiçãoCanoHorizontal = larguraDispositivo;
				}
		}

	}
	//

	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
