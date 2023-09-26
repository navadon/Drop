package th.ac.cmu.eng.cpe.oop.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    private final Rectangle rectangle = new Rectangle();
    private final Texture texture;

    public Player(int x, int y, int width, int height, String path) {
        this.rectangle.x = x;
        this.rectangle.y = y;
        this.rectangle.width = width;
        this.rectangle.height = height;
        this.texture = new Texture(Gdx.files.internal(path));
    }
    public Rectangle getRectangle() {
        return this.rectangle;
    }
    public Texture getTexture() {
        return this.texture;
    }
}
