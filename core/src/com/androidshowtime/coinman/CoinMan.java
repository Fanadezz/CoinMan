package com.androidshowtime.coinman;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;

	//Texture is a way to add an image
	Texture background;

	//creating running-illusion with Texture array

	Texture[] man;

	//manState to keep track of the frame

	int manState = 0;

	//pause to slowdown the frames
	int pause = 0;


	//physics bit

	float gravity = 0.2f;
	float velocity = 0;
	int manY = 0;


	//Coin Array

	ArrayList<Integer> coinX = new ArrayList<>();
	ArrayList<Integer> coinY = new ArrayList<>();

	//Bomb array
	ArrayList<Integer> bombY = new ArrayList<>();
	ArrayList<Integer> bombX = new ArrayList<>();

	//coinRectangles Array to determine collisions
	ArrayList<Rectangle> coinRectangles = new ArrayList<>();
	//bombRectangles Array to determine collisions
	ArrayList<Rectangle> bombRectangles = new ArrayList<>();

	//Geometric shape around man
	Rectangle manRectangle;

	//coin count to maintain safe space between coins
	int coinCount;

	//Coin ImageView
	Texture coin;

	//random gen to determine height of coin
	Random random;


	//bombs count
	int bombCount = 0;

	//Texture
	Texture bomb;


	//Score counter

	int score = 0;

	//Bitmap font for drawing on the screen
	BitmapFont font;

	//game state int to toggle game state
	int gameState = 0;

	@Override
	public void create() {


		batch = new SpriteBatch();

		//set manY to be at the centre of the screen vertically
		manY = Gdx.graphics.getHeight() / 2;
		//takes the name of the image in Assets folder
		background = new Texture("bg.png");

		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");


		//initializing coin.png imageView
		coin = new Texture("coin.png");
		random = new Random();
		//initializing coin.png imageView
		bomb = new Texture("bomb.png");

		//initializing font, set color and size
		font = new BitmapFont();
		font.setColor(Color.FIREBRICK);
		font.getData().setScale(7);


	}

	//render method is caled over and over to draw
	@Override
	public void render() {

		//to start anything you call batch.begin()
		batch.begin();
		//to draw you call batch.draw()
		//args(texture, originX, originY, height, width)

		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


		//setting the game states - LIVE, ABOUT TO START and GAME OVER
		if (gameState == 1) {
			//GAME IS LIVE

			//BOMBS
			if (bombCount < 250) {

				bombCount++;
			} else {
				bombCount = 0;

				//make coin with every 200 iteration
				makeBomb();
			}

			//clear rectangles
			bombRectangles.clear();

			//for-loop to draw the coin
			for (int i = 0; i < bombX.size(); i++) {

				//use bombX and bombY to draw bombs
				batch.draw(bomb, bombX.get(i), bombY.get(i));

				//make bombs move along the X-axis
				bombX.set(i, bombX.get(i) - 8);
				bombRectangles.add(new Rectangle(bombX.get(i), bombY.get(i), bomb.getWidth(),
												 bomb.getHeight()));

			}


			//COINS

			if (coinCount < 100) {
				//up the coinCount with each iteration
				coinCount++;
			} else {

				coinCount = 0;

				//make coin with every hundredth iteration
				makeCoin();
			}


			//clear rectangles
			coinRectangles.clear();


			//for-loop to draw the coin
			for (int i = 0; i < coinX.size(); i++) {
				//use coinX and coinY to get positions
				batch.draw(coin, coinX.get(i), coinY.get(i));

				//make coin move along the X-axis
				coinX.set(i, coinX.get(i) - 4);
				coinRectangles.add(new Rectangle(coinX.get(i), coinY.get(i), coin.getWidth(),
												 coin.getHeight()));

			}


			if (Gdx.input.justTouched()) {

				velocity = -10;
			}


			if (pause < 8) {

				pause++;

			} else {

				pause = 0;
				//increasing frames

				if (manState < 3) {

					manState++;
				} else {

					manState = 0;
				}
			}


			//increasingly falling speed
			velocity += gravity;
			manY -= velocity;


			if (manY <= 0) {

				manY = 0;
			}


		} else if (gameState == 0) {
			//GAME ABOUT TO START

			if (Gdx.input.justTouched()) {

				gameState = 1;
			}
		} else if (gameState == 2) {

			//GAME OVER

			if (Gdx.input.justTouched()) {

				gameState = 1;
				manY = Gdx.graphics.getHeight() / 2;
				score = 0;
				velocity = 0;
				coinX.clear();
				coinY.clear();
				coinRectangles.clear();
				coinCount = 0;
				bombX.clear();
				bombY.clear();
				bombRectangles.clear();
				bombCount = 0;
			}
		}

		//draw the dizzy man
		if (gameState == 2) {
			batch.draw(new Texture("dizzy-1.png"),
					   (Gdx.graphics.getWidth() / 2f - man[manState].getWidth() / 2f),
					   manY);

		} else {
			//draw and set the runningMan on the centre(width)

			batch.draw(man[manState],
					   (Gdx.graphics.getWidth() / 2f - man[manState].getWidth() / 2f),
					   manY);

		}


		//draw the bitFont passing in the batch, score and screen position
		font.draw(batch, String.valueOf(score), 100, 200);


		//set man rectangle - man's X-Axis doesn't change

		manRectangle =
				new Rectangle((Gdx.graphics.getWidth() / 2f - man[manState].getWidth() / 2f),
									 manY,
									 man[manState].getWidth(), man[manState].getHeight());


		//to stop you call batch.end()
		batch.end();

		//check for overlapping/ collision with coins

		for (int i = 0; i < coinRectangles.size(); i++) {

			if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {

				//Gdx.app.log("Coin!", "Collision happened!");

				score += 10;

				coinRectangles.remove(i);
				coinX.remove(i);
				coinY.remove(i);
				break;
			}

		}


		//check for overlapping/ collision with bombs
		for (int i = 0; i < bombRectangles.size(); i++) {

			if (Intersector.overlaps(manRectangle, bombRectangles.get(i))) {

				//Gdx.app.log("Bomb!", "Collision happened!");
				gameState = 2;
			}

		}


	}

	@Override
	public void dispose() {
		batch.dispose();
	}


	public void makeCoin() {

		float height = random.nextFloat() * Gdx.graphics.getHeight();

		coinY.add((int) height);

		//place it off the edge
		coinX.add(Gdx.graphics.getWidth());
	}


	public void makeBomb() {

		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombY.add((int) height);


		bombX.add(Gdx.graphics.getWidth());
	}

}
