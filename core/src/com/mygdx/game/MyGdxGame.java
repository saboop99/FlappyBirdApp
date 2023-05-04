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

	//test
	//declaração de todas as variáveis utilizadas no projeto
	private SpriteBatch batch;
	private Texture[] porcos;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private Texture logoStart;
	private Texture birdGoldCoin;
	private Texture birdSilverCoin;
	private Texture showBirdG;

	private ShapeRenderer shapeRenderer;
	private Circle circuloPorco;
	private Circle colliderCoin;
	private Rectangle retanguloCanoCima;
	private Rectangle retanguloCanoBaixo;

	private float larguraDispositivo;
	private float alturaDispositivo;
	private float variacao = 0;
	private float gravidade = 2;
	private float posiçãoInicialVerticalPassaro = 0;
	private float posiçãoCanoHorizontal;
	private float posiçãoCanoVertical;
	private float horizontalCoin;
	private float verticalCoin;
	private float espaçoEntreCanos;
	private Random random;
	private int pontos = 0;
	private int silverCoin = 5;
	private int goldCoin = 10;
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
	Sound coinPru;

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

	//criação do método inicializarTexturas, que inicializa as texturas no jogo (texturas do porco, fundo do mapa, textura dos canos, tela de game over e a tela de start)
	private void inicializarTexturas(){
		porcos = new Texture[3];
		porcos[0] = new Texture("pig1.png");
		porcos[1] = new Texture("pig2.png");
		porcos[2] = new Texture("pig3.png");

		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
		gameOver = new Texture("game_over.png");
		logoStart = new Texture("oincpig.png");
		birdGoldCoin = new Texture("passaro2.png");
		birdSilverCoin = new Texture("flappyBlue.png");
		showBirdG = birdGoldCoin;
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
		//definindo a posição horizontal da moeda dourada
		horizontalCoin = larguraDispositivo / 2;
		//
		verticalCoin = posiçãoCanoHorizontal + larguraDispositivo / 2;

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

		//definição da variavel shapeRenderer
		shapeRenderer = new ShapeRenderer();
		//criação de um círculo em volta do pássaro para servir de colisor
		circuloPorco = new Circle();
		//criação de um retângulo em volta do cano de cima para servir de colisor
		retanguloCanoBaixo = new Rectangle();
		//criação de um retângulo em volta do cano de baixo para servir de colisor
		retanguloCanoCima = new Rectangle();
		//criação de um círculo para servir como colisor para a moeda
		colliderCoin = new Circle();

		//definindo o som que sai quando o passaro bate as asas (quando o player toca na tela)
		somVoando = Gdx.audio.newSound(Gdx.files.internal("Oinc.wav"));
		//definindo o som que sai quando o passaro colide com o cano
		somColisão = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		//definindo o som que sai quando o passaro recebe pontos ao passar pelos canos
		somPontuação = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));
		//definindo o som que quando coleta moedas
		coinPru = Gdx.audio.newSound(Gdx.files.internal("Pigeon.wav"));

		//setando a variável preferencias, e pegando o preferences no Gdx
		preferencias = Gdx.app.getPreferences("flappyBird");
		//preferences serve para salvar coisas que não exigem tanta memória, nesse caso a pontuação máxima está sendo salva, e o valor inicial é 0
		pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima", 0);

		//adicionando uma camera
		camera = new OrthographicCamera();
		//definindo a posição da camera
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);
		//definindo o viewport, que vai servir para ajeitar a imagem no dispositivo
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

	}

	//criação do método verificarEstadoJogo, que verifica o estado atual do jogo
	private void verificarEstadoJogo(){
		//condicional para caso o jogador toque na tela e o estado do jogo seja 0, o passaro voa e o estado do jogo é definido pra 1
		boolean toqueTela = Gdx.input.justTouched();
		if (estadoJogo == 0){
			if (toqueTela){
				gravidade = -15;
				estadoJogo = 1;
				somVoando.play();
			}
			//condicional para caso o jogador toque na tela e o estado do jogo seja 1, o passaro voa
		}else if( estadoJogo == 1){
				if (toqueTela){
					gravidade = -15;
					somVoando.play();
				}
				//operação que define o surgimento das moedas douradas
			     horizontalCoin -= Gdx.graphics.getDeltaTime() * 200;
				//operação que definine o surgimento dos canos
				posiçãoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;
				if (posiçãoCanoHorizontal <  -canoTopo.getWidth()){
					posiçãoCanoHorizontal = larguraDispositivo;
					posiçãoCanoVertical = random.nextInt(400) - 200;
					passsouCano = false;
				}

				if (horizontalCoin <- showBirdG.getWidth() / 2){
					coinReset();
				}
				//condicional para o passaro voar, utilizando sua posição inicial e a gravidade
				if (posiçãoInicialVerticalPassaro > 0 || toqueTela)
					posiçãoInicialVerticalPassaro = posiçãoInicialVerticalPassaro - gravidade;
				gravidade++;

				//condicional para caso o estado do jogo seja 2, mostra a pontuação máxima e salva (caso você tenha feito mais pontos que a sua pontuação máxima
		}else if(estadoJogo == 2){
				if (pontos > pontuacaoMaxima){
					pontuacaoMaxima = pontos;
					preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);
					preferencias.flush();
				}
				//definie a posição horizontal do pássaro
				posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime()*500;

				//caso o player toque na tela, deixa tudo igual no inicio
				if (toqueTela){
					estadoJogo = 0;
					pontos = 0;
					gravidade = 0;
					posicaoHorizontalPassaro = 0;
					posiçãoInicialVerticalPassaro = alturaDispositivo / 2;
					posiçãoCanoHorizontal = larguraDispositivo;
					coinReset();
				}
		}

	}
	//criação do método detectarColisoes que detecta colisões
	private void detectarColisoes(){
		// setando o circulo do passaro (colisor)
		circuloPorco.set(
				50 + posicaoHorizontalPassaro + porcos[0].getWidth() / 2,
				posiçãoInicialVerticalPassaro + porcos[0].getHeight() / 2,
				porcos[0].getWidth() / 2
		);

		//setando o retangulo do cano de baixo (colisor)
		retanguloCanoBaixo.set(
				posiçãoCanoHorizontal,
				alturaDispositivo / 2 - canoBaixo.getHeight() - espaçoEntreCanos / 2 + posiçãoCanoVertical,
				canoBaixo.getWidth(), canoBaixo.getHeight()
		);

		//setando o retangulo do cano de cima (colisor)
		retanguloCanoCima.set(
				posiçãoCanoHorizontal, alturaDispositivo / 2 + espaçoEntreCanos / 2 + posiçãoCanoVertical,
				canoTopo.getWidth(), canoTopo.getHeight()
		);

		colliderCoin.set(
				horizontalCoin - (showBirdG.getWidth()),
				verticalCoin - (showBirdG.getHeight()),
				showBirdG.getWidth() / 2
		);

		//duas variavéis boolean para detectar colisão em cada cano (cima e baixo)
		boolean colidiuCanoCima = Intersector.overlaps(circuloPorco, retanguloCanoCima);
		boolean colidiuCanoBaixo = Intersector.overlaps(circuloPorco, retanguloCanoBaixo);
		//variável boolean para detectar colisão entre player e moeda
		boolean colidiuBirdCoin = Intersector.overlaps(circuloPorco, colliderCoin);

		if(colidiuBirdCoin == true){
			if (showBirdG == birdGoldCoin){
				pontos += goldCoin;

				//verticalCoin = alturaDispositivo * 2;

			}
			else
			{
				pontos += silverCoin;
			}
			verticalCoin = alturaDispositivo * 2;
			coinPru.play();
		}

		//condicional para caso o passaro colida, ele caia e morra
		if (colidiuCanoCima || colidiuCanoBaixo) {
			if (estadoJogo == 1) {
				somColisão.play();
				estadoJogo = 2;
			}
		}
	}
	//criação do metodo desenharTexturas que está definindo todas as texturas do jogo
	private void desenharTexturas(){
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		//operação para desenhar o fundo no lugar certo
		batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);

		//operação para desenhar os porcos no lugar certo
		batch.draw(porcos[(int) variacao],
				50 + posicaoHorizontalPassaro, posiçãoInicialVerticalPassaro);

		//operação para desenhar os canos nos lugares certos
		batch.draw(canoBaixo, posiçãoCanoHorizontal,
				alturaDispositivo / 2 -canoBaixo.getHeight() - espaçoEntreCanos/2 + posiçãoCanoVertical);
		batch.draw(canoTopo, posiçãoCanoHorizontal, alturaDispositivo / 2 + espaçoEntreCanos / 2 + posiçãoCanoVertical);

		//operação do desenho do texto que mostra seus pontos
		textoPontuação.draw(batch, String.valueOf(pontos), larguraDispositivo / 2,
				alturaDispositivo - 110);

        //condicional para caso o estado do jogo seja 0, ou seja, o estado inicial, mostrar uma tela de start
		if (estadoJogo == 0){
			batch.draw(logoStart, larguraDispositivo / 2 - gameOver.getWidth() / 2,
					alturaDispositivo / 2);
		}

		//condicional para que caso você perca o jogo, ele te mostre todas as informaçoes propostas (tela de game over, sua melhor pontuação e o "botão" de reiniciar
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

		//condicional para desenhar as moedas caso o estado do jogo for 1
		if (estadoJogo == 1){
			batch.draw(showBirdG, horizontalCoin - (showBirdG.getWidth()),
					verticalCoin -(showBirdG.getWidth()), showBirdG.getWidth(),
					showBirdG.getHeight());
		}




		batch.end();
	}

	//criação do método validarPontos que serve para validar os pontos conseguidos
	public void validarPontos(){
		//duas condicionais que servem para confirmar os pontos e adicionar
		if (posiçãoCanoHorizontal < 50 - porcos[0].getWidth() ){
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

	//criação de um método que serve para "resetar" as moedas
	private void coinReset(){
		horizontalCoin = posiçãoCanoHorizontal + canoBaixo.getWidth() + showBirdG.getWidth() +
				random.nextInt((int)(larguraDispositivo - (showBirdG.getWidth())));
		verticalCoin = showBirdG.getHeight() / 2 +
				random.nextInt((int)alturaDispositivo - showBirdG.getHeight() / 2);

		int randomCoin = random.nextInt(100);
		if (randomCoin < 30)
		{
			showBirdG = birdGoldCoin;
		}
		else
		{
			showBirdG = birdSilverCoin;
		}
	}


	//resize serve para redefinir o viewport do android
	@Override
	public void resize (int width, int height){
		viewport.update(width, height);
	}

	//descarta
	@Override
	public void dispose(){

	}
}
