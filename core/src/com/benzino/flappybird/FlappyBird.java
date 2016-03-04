package com.benzino.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sun.prism.image.ViewPort;

import java.util.Random;

import javax.xml.soap.Text;

public class FlappyBird extends ApplicationAdapter {

    SpriteBatch batch;

    BitmapFont fontScore ;
    BitmapFont fontText ;
    GlyphLayout glyphLayout;
    FreeTypeFontGenerator generator;

    FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    public  static Preferences prefs;
    public int highScore;


    Texture blueCircle;
    Texture redCircle;
    Texture yellowCircle;

    Texture topTube;
    Texture bottomTube;

    Texture pipe;

    Texture floor;

    Texture obstacle;

    Texture[] obstacles ;

    Circle ballCircle;

    ShapeRenderer shapeRenderer;

    Rectangle[] topPipeRectangles;
    Rectangle[] bottomPipesRectangles;

    Rectangle topFloor;
    Rectangle bottomFloor;

    int score = 0;
    int scoringPipe = 0;

    //position of the circle on the screen
    float circleX = 0;
    float circleY = 0;

    float tubeX = 0;
    float topTubeY = 0;
    float bottomTubeY = 0;

    float maxOffset = 0;

    float gap = 200;

    Random randomGenerator;

    //float pipeOffset = 0;

    float pipeVelocity = 4;
    //float pipeX ;


    //velocity
    float velocity = 0;

    //gravity
    float gravity = 0.8f;

    //game state
    int gameState = 0;

    int numberOfPipes = 7;
    int numberOfColors = 7;
    float distanceBetweenPipes;

    int randomColor;

    float[] pipeX = new float[numberOfPipes];
    float[] pipeOffset = new float[numberOfPipes];
//    OrthographicCamera camera;
//    ExtendViewport viewport;
    //ViewPort viewport;

    public final static  float WIDTH = 600;
    public final  static float HEIGHT = 800;
	
	@Override
	public void create () {

        //camera = new OrthographicCamera();

        float aspectRatio = (float)Gdx.graphics.getHeight()/ (float)Gdx.graphics.getWidth();

        prefs = Gdx.app.getPreferences("FlappyBird");

        if (!prefs.contains("highScore")){
            prefs.putInteger("highScore", 0);
            prefs.flush();
        }

        //viewport = new ExtendViewport(WIDTH * aspectRatio, HEIGHT, camera);
        //viewport = new ScreenViewport();
        //viewport.apply();

        //camera.position.set(WIDTH / 2, HEIGHT / 2, 0);
        batch = new SpriteBatch();

        shapeRenderer = new ShapeRenderer();

        glyphLayout = new GlyphLayout();


        ballCircle = new Circle();

        topFloor = new Rectangle();
        bottomFloor = new Rectangle();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("gunplay.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 128;
        fontScore = generator.generateFont(parameter);
        fontScore.setColor(1, 1, 1, 0.4f);

        parameter.size = 64;
        fontText = generator.generateFont(parameter);
        fontText.setColor(1, 0, 0, 0.6f);



        generator.dispose(); // don't forget to dispose to avoid memory leaks!

        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");

        blueCircle = new Texture("green-circle-48.png");
        redCircle = new Texture("red_ball.png");
        yellowCircle = new Texture("yellow-circle-48.png");

        pipe  = new Texture("red_long_pipe.png");

        obstacle = new Texture("obstacle.png");

        obstacles = new Texture[numberOfPipes];

        floor = new Texture("floor.png");

        obstacles[0] = new Texture("0.png");
        obstacles[1] = new Texture("1.png");
        obstacles[2] = new Texture("2.png");
        obstacles[3] = new Texture("3.png");
        obstacles[4] = new Texture("4.png");
        obstacles[5] = new Texture("5.png");
        obstacles[6] = new Texture("6.png");

        circleX = (Gdx.graphics.getWidth() - blueCircle.getWidth())/2;


        tubeX = (Gdx.graphics.getWidth()/2 - topTube.getWidth()/2);
        topTubeY = Gdx.graphics.getHeight()/2 + gap/2;
        bottomTubeY = Gdx.graphics.getHeight()/2 - gap/2 - bottomTube.getHeight();

        maxOffset = Gdx.graphics.getHeight()/2 - gap/2 - 100;

        randomGenerator = new Random();

        distanceBetweenPipes = Gdx.graphics.getWidth()  ;

        topPipeRectangles = new Rectangle[numberOfPipes];
        bottomPipesRectangles = new Rectangle[numberOfPipes];


        startGame();

	}

    public  void startGame(){

        circleY = (Gdx.graphics.getHeight() - blueCircle.getHeight())/2;

        for (int i =0; i<numberOfPipes; i++){
            pipeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 50);

            pipeX[i] = Gdx.graphics.getWidth() / 2 - pipe.getWidth() / 2 + Gdx.graphics.getWidth() +i* distanceBetweenPipes;

            topPipeRectangles[i] = new Rectangle();
            bottomPipesRectangles[i] = new Rectangle();
        }

    }

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 0.3f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        topFloor = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 0.2f);
        shapeRenderer.rect(topFloor.x, topFloor.y, topFloor.getWidth(), topFloor.getHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        //camera.update();

        batch.begin();

        if (gameState == 1){

            if (pipeX[scoringPipe] <  (Gdx.graphics.getWidth()/2 - pipe.getWidth())){

                score ++;

                Gdx.app.log("Scoring", String.valueOf(score));

                if (scoringPipe < numberOfPipes - 1 ){

                    scoringPipe++;

                }else{

                    scoringPipe = 0;
                }
            }

            if(Gdx.input.justTouched()){

                velocity = -12;

            }

            for (int i = 0; i< numberOfPipes; i ++){

                if(pipeX[i] < - pipe.getWidth()){

                    pipeX[i] += numberOfPipes * distanceBetweenPipes;

                    pipeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 50);

                    //randomColor = randomGenerator.nextInt(i + 1);
                }else {
                    pipeX[i] = pipeX[i] - pipeVelocity;

                }

                batch.draw(pipe, pipeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + pipeOffset[i]);
                batch.draw(pipe, pipeX[i], Gdx.graphics.getHeight()/2 - gap/2 - pipe.getHeight() + pipeOffset[i]);
                batch.draw(obstacles[i],pipeX[i], Gdx.graphics.getHeight() / 2 - obstacle.getHeight()/2 + pipeOffset[i]);

                topPipeRectangles[i] = new Rectangle(pipeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + pipeOffset[i] , pipe.getWidth(), pipe.getHeight());
                bottomPipesRectangles[i] = new Rectangle(pipeX[i], Gdx.graphics.getHeight()/2 - gap/2 - pipe.getHeight() + pipeOffset[i], pipe.getWidth(), pipe.getHeight());

                ballCircle.set(Gdx.graphics.getWidth() / 2, circleY + redCircle.getHeight() / 2, redCircle.getHeight() / 4);

                batch.draw(redCircle, circleX, circleY);

            }

            if (circleY > floor.getHeight() &&  circleY < (Gdx.graphics.getHeight() - floor.getHeight())){
                velocity += gravity;
                circleY -= velocity;
            }else {
                gameState = 2;

            }


        }else if (gameState == 0){
            if (Gdx.input.justTouched()){
                gameState = 1 ;
            }else {
                glyphLayout.setText(fontText, "Tap to start");
                fontText.draw(batch, "Tap to Start", Gdx.graphics.getWidth() / 2 - glyphLayout.width/2, Gdx.graphics.getHeight() /2 + glyphLayout.height/2);

            }
        }else  if (gameState == 2){

            if (score > prefs.getInteger("highScore")){

                highScore = score;

                Gdx.app.log("highscore", "High Score: " + highScore);

                prefs.putInteger("highScore", highScore);
                prefs.flush();

            }

            glyphLayout.setText(fontText, "GAME OVER");
            fontText.draw(batch, "GAME OVER", Gdx.graphics.getWidth() / 2 - glyphLayout.width / 2, Gdx.graphics.getHeight() / 2 + glyphLayout.height / 2);
            glyphLayout.setText(fontText, "Tap to replay");
            fontText.draw(batch, "Tap to replay", Gdx.graphics.getWidth() / 2 - glyphLayout.width / 2, Gdx.graphics.getHeight() / 2 - glyphLayout.height * 3 / 2);
            glyphLayout.setText(fontText, "High score: " + String.valueOf(prefs.getInteger("highScore")) );
            fontText.draw(batch, "High score: " + String.valueOf(prefs.getInteger("highScore")), Gdx.graphics.getWidth() / 2 - glyphLayout.width/2, Gdx.graphics.getHeight() /2  -  glyphLayout.height * 7/2);


            if (Gdx.input.justTouched()){

                gameState = 1;
                startGame();
                score = 0;
                scoringPipe = 0;
                velocity = 0;
            }

        }

        //batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.draw(floor, 0, 0);
        batch.draw(floor, 0, Gdx.graphics.getHeight() - floor.getHeight());

        glyphLayout.setText(fontScore, String.valueOf(score));

        fontScore.draw(batch, String.valueOf(score), Gdx.graphics.getWidth() / 2 - glyphLayout.width / 2, Gdx.graphics.getHeight() - 2 * floor.getHeight());

        //Collision detection


        batch.end();

//        shapeRenderer.circle(ballCircle.x, ballCircle.y, ballCircle.radius);
//
//        shapeRenderer.rect(bottomFloor.x, bottomFloor.y, bottomFloor.getWidth(), bottomFloor.getHeight() );

        for (int i = 0; i < numberOfPipes; i++){

//            shapeRenderer.rect(topPipeRectangles[i].x, topPipeRectangles[i].y, topPipeRectangles[i].getWidth(), topPipeRectangles[i].getHeight());
//            shapeRenderer.rect(bottomPipesRectangles[i].x, bottomPipesRectangles[i].y, bottomPipesRectangles[i].getWidth(), bottomPipesRectangles[i].getHeight());

            if (Intersector.overlaps(ballCircle, topPipeRectangles[i])
                    || Intersector.overlaps(ballCircle, bottomPipesRectangles[i])
                    //|| Intersector.overlaps(ballCircle, topFloor)
                    //|| Intersector.overlaps(ballCircle, bottomFloor)
                    ){

                gameState = 2;


            }

        }
        //shapeRenderer.end();
	}

    @Override
    public void resize(int width, int height) {
        //viewport.update(width, height);
        //camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);
    }
}
