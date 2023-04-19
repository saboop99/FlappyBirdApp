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
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {

	//declaração de todas as variáveis utilizadas no projeto
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;

	private ShapeRenderer shapeRenderer;
	private Circle circuloPassaro;
	private Rectangle retanguloCanoCima;
	private Rectangle retanguloCanoBaixo;

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

	//chamando os métodos para inicializar as texturas e os objetos
	@Override
	public void create () {
	inicializarTexturas();
	inicializarObjetos();
	}

	//chamando métodos que respectivamente: verifica o estado atual do jogo, valida os pontos que o player fez no jogo, "desenha" as texturas do jogo (cano, passaro etc) e detecta as colisões do passaro com os canos
	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisoes();
	}

	//criação do método inicializarTexturas, que inicializa as texturas no jogo (texturas do passaro voando, fundo do mapa, textura dos canos e tela de game over)
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
	//criação do método inicializarObjetos, que inicializar os "objetos" no jogo (tudo o que é necessário pro jogo rodar)
	private void inicializarObjetos(){
		batch = new SpriteBatch();
		random = new Random();

		//definição da altura da tela do dispositivo
		larguraDispositivo = VIRTUAL_WIDTH;
		//definição da  largura da tela do dispositivo
		alturaDispositivo = VIRTUAL_HEIGHT;
		//definindo a posição inicial do passaro
		posiçãoInicialVerticalPassaro = alturaDispositivo / 2;
		//definindo a posição horizontal do cano
		posiçãoCanoHorizontal = larguraDispositivo;
		//definindo a distância entre os canos
		espaçoEntreCanos = 350;

		//adicionando uma nova BitmapFont para o texto que mostra a pontuação do jogador
		textoPontuação = new BitmapFont();
		//setando a cor da BitmapFont
		textoPontuação.setColor(Color.WHITE);
		//setando o tamanho da BitmapFont
		textoPontuação.getData().setScale(10);

		//adicionando uma nova BitmapFont para o texto que mostra o "reiniciar" para o jogador
		textoReiniciar = new BitmapFont();
		//setando a cor da BitmapFont
		textoReiniciar.setColor(Color.GREEN);
		//setando o tamanho da BitmapFont
		textoReiniciar.getData().setScale(2);

		//adicionando uma nova BitmapFont para o texto que mostra a melhor pontuação do jogador
		textoMelhorPontuação = new BitmapFont();
		//setando a cor da BitmapFont
		textoMelhorPontuação.setColor(Color.RED);
		//setando o tamanho da BitmapFont
		textoMelhorPontuação.getData().setScale(2);

		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		retanguloCanoBaixo = new Rectangle();
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
	private void detectarColisoes(){
		circuloPassaro.set(
				50 + posicaoHorizontalPassaro + passaros[0].getWidth() / 2,
				posiçãoInicialVerticalPassaro + passaros[0].getHeight() / 2,
				passaros[0].getWidth() / 2
		);

		retanguloCanoBaixo.set(
				posiçãoCanoHorizontal,
				alturaDispositivo / 2 - canoBaixo.getHeight() - espaçoEntreCanos / 2 + posiçãoCanoVertical,
				canoBaixo.getWidth(), canoBaixo.getHeight()
		);

		retanguloCanoCima.set(
				posiçãoCanoHorizontal, alturaDispositivo / 2 + espaçoEntreCanos / 2 + posiçãoCanoVertical,
				canoTopo.getWidth(), canoTopo.getHeight()
		);

		boolean colidiuCanoCima = Intersector.overlaps(circuloPassaro, retanguloCanoCima);
		boolean colidiuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);

		if (colidiuCanoCima || colidiuCanoBaixo) {
			if (estadoJogo == 1) {
				somColisão.play();
				estadoJogo = 2;
			}
		}
	}
	private void desenharTexturas(){
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
		batch.draw(passaros[(int) variacao],
				50 + posicaoHorizontalPassaro, posiçãoInicialVerticalPassaro);
		batch.draw(canoBaixo, posiçãoCanoHorizontal,
				alturaDispositivo / 2 + espaçoEntreCanos / 2 + posiçãoCanoVertical);
		textoPontuação.draw(batch, String.valueOf(pontos), larguraDispositivo / 2,
				alturaDispositivo - 110);

		if (estadoJogo == 2){
			batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2,
					alturaDispositivo / 2);
			textoReiniciar.draw(batch,
					"Toque para reiniciar!", larguraDispositivo / 2 - 140,
					alturaDispositivo / 2 - gameOver.getHeight() / 2);
			textoMelhorPontuação.draw(batch,
					"Seu recorde é: " + pontuacaoMaxima + " pontos",
					larguraDispositivo / 2 - 140, alturaDispositivo / 2 - gameOver.getHeight());
		}
		batch.end();
	}

	public void validarPontos(){
		if (posiçãoCanoHorizontal < 50 - passaros[0].getWidth() ){
			if (!passsouCano){
				pontos++;
				passsouCano = true;
				somPontuação.play();
			}
		}

		variacao += Gdx.graphics.getDeltaTime() * 10;

		if (variacao > 3)
			variacao = 0;

	}


	@Override
	public void resize (int width, int height){
		viewport.update(width, height);
	}

	@Override
	public void dispose(){

	}
}
