import javax.swing.JLabel;

public class Car extends Sprite implements Runnable
{
    // Attributes
    private boolean isMoving;
    private int speed;
    private Thread thread;
    private JLabel carLabel;

    public Car()
    {
        super();
        isMoving = false;
    }

    public Car(int posX, int posY, int width, int height, String image, boolean isMoving, int speed)
    {
        super(posX, posY, width, height, image);
        this.isMoving = isMoving;
        this.speed = speed;
    }

    // Getters
    public boolean getIsMoving()
    {
        return this.isMoving;
    }
    public JLabel getCarLabel()
    {
        return this.carLabel;
    }
    public int getSpeed()
    {
        return this.speed;
    }

    // Setters
    public void setIsMoving(boolean isMoving)
    {
        this.isMoving = isMoving;
    }
    public void setCarLabel(JLabel carLabel)
    {
        this.carLabel = carLabel;
    }
    public void setSpeed(int speed)
    {
        this.speed = speed;
    }

    // Methods
        // Start and stop thread
    public void startThread()
     {
        this.isMoving = true;
        thread = new Thread(this, "Car Thread");
        thread.start();
    }

    public void stopThread()
    {
        this.isMoving = false;

        if (thread != null) {
            thread.interrupt();
        }

    }

        // Run method
    @Override
    public void run() {
        System.out.println("Car Thread Started");

        while(this.isMoving && !Thread.currentThread().isInterrupted()) {
            int x = this.getPosX(); // Get current x position

            if (this.speed == 200) {
                x += Properties.CAR_SPEED; // Reverse speed
            } else {
                x -= Properties.CAR_SPEED; // Subtract speed from x position
            }

            this.setPosX(x); // Set new x position
            carLabel.setLocation(x, this.getPosY()); // Update label position

            if (x > Properties.SCREEN_Y + 32) { // If car is off screen + 32 pixels
                x = -this.getWidth(); // Reset x position
                this.setPosX(x); // Set new x position
            } else if (x < -this.getWidth() - 32) { // If car is off screen + 32 pixels
                x = Properties.SCREEN_X; // Reset x position
                this.setPosX(x); // Set new x position
            }

            try {
                Thread.sleep(speed);
            } catch (InterruptedException e) {
                System.out.println("Car Thread Interrupted");
                Thread.currentThread().interrupt();
                break;
            }


        }

        throw new UnsupportedOperationException("Unimplemented method 'run'");

    }

}
