import java.awt.Rectangle;

public class Sprite {
    // Variables
    protected int posX, posY, width, height;
    protected String image;
    protected Rectangle rectangle;

    // Default constructor
    public Sprite() {
        posX = 0;
        posY = 0;
        width = 0;
        height = 0;
        image = "";
        rectangle = new Rectangle(posX, posY, width, height);
    }

    // Secondary Constructor
    public Sprite(int posX, int posY, int width, int height, String image) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.image = image;
        rectangle = new Rectangle(posX, posY, width, height);
    }

    // Getters
    public int getPosX() {
        return posX;
    }
    public int getPosY() {
        return posY;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public String getImage() {
        return image;
    }
    public Rectangle getRectangle() {
        return rectangle;
    }

    // Setters
    public void setPosX(int posX) {
        this.posX = posX;
        rectangle.setLocation(posX, posY);
    }

    public void setPosY(int posY) {
        this.posY = posY;
        rectangle.setLocation(posX, posY);
    }

    public void setWidth(int width) {
        this.width = width;
        rectangle.setSize(width, height);
    }

    public void setHeight(int height) {
        this.height = height;
        rectangle.setSize(width, height);
    }

    public void setImage(String image) {
        this.image = image;
    }


}
