package dungeontrivia;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 *
 * @author Luis, Adrian, Antonio and Rodrigo
 */
public abstract class Item {

    protected int x;        // to store x position
    protected int y;        // to store y position
    protected Rectangle rect;

    /**
     * Set the initial values to create the item
     *
     * @param x <b>x</b> position of the object
     * @param y <b>y</b> position of the object
     */
    public Item(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get x value
     *
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * Get y value
     *
     * @return y
     */
    public int getY() {
        return y;
    }

    public Rectangle getRect() {
        return rect;
    }

    /**
     * Set x value
     *
     * @param x to modify
     */
    public void setX(int x) {
        this.x = x;
        this.rect.setLocation(x, getY());
    }

    /**
     * Set y value
     *
     * @param y to modify
     */
    public void setY(int y) {
        this.y = y;
        this.rect.setLocation(getX(), y);
    }

    /**
     * To update positions of the item for every tick
     */
    public abstract void tick();

    /**
     * To paint the item
     *
     * @param g <b>Graphics</b> object to paint the item
     */
    public abstract void render(Graphics g);
}
