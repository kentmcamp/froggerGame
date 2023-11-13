import java.awt.Rectangle;

public class Sprite {
    // Base Class Properties
    protected int posX, posY, width, height;
    protected String image;
    protected Rectangle rectangle;

    // Default Constructor
    public Sprite() {
        this.posX = 0;
        this.posY = 0;
        this.width = 0;
        this.height = 0;
        this.image = "";
        this.rectangle = new Rectangle(this.posX, this.posY, this.width, this.height);
    }

    // Secondary Constructor
    public Sprite(int posX, int posY, int width, int height, String image) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.image = image;
        this.rectangle = new Rectangle(this.posX, this.posY, this.width, this.height);
    }

    // Getters
    public int getPosX() {
        return this.posX;
    }
    public int getPosY() {
        return this.posY;
    }
    public int getWidth() {
        return this.width;
    }
    public int getHeight() {
        return this.height;
    }
    public String getImage() {
        return this.image;
    }
    public Rectangle getRectangle() {
        return this.rectangle;
    }

    // Setters
    public void setPosX(int posX) {
        this.posX = posX;
        this.rectangle.setLocation(this.posX, this.posY);
    }
    public void setPosY(int posY) {
        this.posY = posY;
        this.rectangle.setLocation(this.posX, this.posY);
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

}
