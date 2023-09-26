package th.ac.cmu.eng.cpe.oop.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;
public class GameScreen implements Screen {

    final Drop game;
    private OrthographicCamera camera;
    private Texture dropImage;
    private Sound dropSound;
    private Music rainMusic;
    private Player player1;
    private Player player2;
    private Array<Rectangle> raindrops;
    private long lastDropTime;
    private int player1Score = 0;
    private int player2Score = 0;
    private int player1Speed = 300;
    private int plater2Speed = 300;
    private int spawnDiff = 500000000;
    private int dropSpeed = 200;
    private int dropVib = 10;
    private int dropleaks = 0;

    public GameScreen(final Drop game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        player1 = new Player(704, 20, 64, 64, "bucket.png");
        player2 = new Player(32, 20, 64, 64, "bucket.png");

        dropImage = new Texture(Gdx.files.internal("droplet.png"));
        raindrops = new Array<Rectangle>();
        spawnRaindrop();

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);
    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800-64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render (float delta) {
        ScreenUtils.clear(0,0,0.4f,1);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) )
            player1.getRectangle().x -= player1Speed * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) )
            player1.getRectangle().x += player1Speed * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.UP) )
            player1.getRectangle().y += player1Speed * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) )
            player1.getRectangle().y -= player1Speed * Gdx.graphics.getDeltaTime();

        if(Gdx.input.isKeyPressed(Input.Keys.A))
            player2.getRectangle().x -= plater2Speed * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.D))
            player2.getRectangle().x += plater2Speed * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.W))
            player2.getRectangle().y += plater2Speed * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.S))
            player2.getRectangle().y -= plater2Speed * Gdx.graphics.getDeltaTime();

        if(player1.getRectangle().x < 0)
            player1.getRectangle().x = 0;
        if(player1.getRectangle().x > 800 - 64)
            player1.getRectangle().x = 800 - 64;
        if(player1.getRectangle().y < 0)
            player1.getRectangle().y = 0;
        if(player1.getRectangle().y > 480 - 64)
            player1.getRectangle().y = 480 -64;

        if(player2.getRectangle().x < 0)
            player2.getRectangle().x = 0;
        if(player2.getRectangle().x > 800 - 64)
            player2.getRectangle().x = 800 - 64;
        if(player2.getRectangle().y < 0)
            player2.getRectangle().y = 0;
        if(player2.getRectangle().y > 480 - 64)
            player2.getRectangle().y = 480 - 64;

        if(TimeUtils.nanoTime() - lastDropTime > spawnDiff)
            spawnRaindrop();

        for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
            Rectangle raindrop = iter.next();
            raindrop.y -= dropSpeed * Gdx.graphics.getDeltaTime();
            raindrop.x += MathUtils.random(-dropVib, dropVib) * Gdx.graphics.getDeltaTime();
            if(raindrop.y + 64 < 0) {
                dropleaks++;
                // more chaotic (more drops + faster + more vibration) --> every 6 drops leaked
                if (dropleaks % 6 == 0) {
                    if (dropSpeed > 20) {
                        dropSpeed += 60;
                    }
                    if (dropVib < 500) {
                        dropVib *= 5;
                    }
                    spawnDiff /= 2;
                }
                // reset to init state --> every 11 drops gathered
                if ((player1Score + player2Score) % 11 == 0) {
                    dropSpeed = 200;
                    dropVib = 10;
                    spawnDiff = 500000000;
                }
                iter.remove();
            }

            // player --> speed up when gathering 5 drops
            // other player --> slower

            if(raindrop.overlaps(player1.getRectangle())) {
                dropSound.play();
                player1Score++;
                if (player1Score % 5 == 0) {
                    player1Speed += 50;
                    if (plater2Speed > 50)
                        plater2Speed -= 50;
                }
                iter.remove();
            }
            if(raindrop.overlaps(player2.getRectangle())) {
                dropSound.play();
                player2Score++;
                if (player2Score % 5 == 0) {
                    if (player1Speed > 50)
                        player1Speed -= 50;
                    plater2Speed += 50;
                }
                iter.remove();
            }
        }

        ScreenUtils.clear(0, 0, 0.3f, 1);
        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        for(Rectangle raindrop: raindrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        game.batch.draw(player1.getTexture(), player2.getRectangle().x, player2.getRectangle().y);
        game.batch.draw(player1.getTexture(), player1.getRectangle().x, player1.getRectangle().y);
        game.font.draw(game.batch, "P1's score: "+ player1Score + ", speed: "+ player1Speed + "\n"
                + "P2's score: "+ player2Score + ", speed: "+ plater2Speed + "\n"
                + "Drop leaks: "+ dropleaks, 50, 460);
        game.batch.end();

    }
    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        rainMusic.play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        dropImage.dispose();
        player1.getTexture().dispose();
        player2.getTexture().dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }

}
