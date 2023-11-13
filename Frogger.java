public class Frogger extends Sprite{
    // Constructors
    public Frogger() {
        super(300, 490, 32, 64, "images/aniFrog.gif");
    }
    public Frogger(int posX, int posY, int width, int height, String image) {
        super(posX, posY, width, height, image);
    }

    // Methods
    public void moveUp() {
        this.setPosY(this.getPosY() - Properties.FROGGER_STEP);
    }
    public void moveDown() {
        this.setPosY(this.getPosY() + Properties.FROGGER_STEP);
    }
    public void moveLeft() {
        this.setPosX(this.getPosX() - Properties.FROGGER_STEP / 2);
    }
    public void moveRight() {
        this.setPosX(this.getPosX() + Properties.FROGGER_STEP / 2);
    }

    public void resetFrogger() {
        this.setImage("images/aniFrog.gif");
        this.setPosX(300);
        this.setPosY(490);
    }
}
