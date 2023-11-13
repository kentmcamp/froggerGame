import javax.swing.JLabel;

public class Log extends Sprite implements Runnable{

    // Attributes
    private boolean isMoving;
    private int speed;
    private Thread thread;
    private JLabel logLabel;

    // Constructor
    public Log()
    {
        super();
        isMoving = false;
    }

    // Secondary Constructor
    public Log(int posX, int posY, int width, int height, String image, boolean isMoving, int speed)
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
    public int getSpeed()
    {
        return this.speed;
    }
    public JLabel getLogLabel()
    {
        return this.logLabel;
    }
    // Setters
    public void setIsMoving(boolean isMoving)
    {
        this.isMoving = isMoving;
    }
    public void setSpeed(int speed)
    {
        this.speed = speed;
    }
    public void setLogLabel(JLabel logLabel)
    {
        this.logLabel = logLabel;
    }

    // Methods
        // Start and stop thread
    public void startThread() {
        this.isMoving = true;
        thread = new Thread(this, "Log Thread");
        thread.start();
    }
    public void stopThread() {
        this.isMoving = false;

        if (thread != null) {
            thread.interrupt();
        }

    }


    @Override
    public void run() {
        System.out.println("Log Thread Started");

        while(this.isMoving && !Thread.currentThread().isInterrupted()) {
            int x = this.getPosX();

            if (this.speed == 200) {
                x += Properties.LOG_SPEED; // Reverse speed
            } else {
                x -= Properties.LOG_SPEED; // Subtract speed from x position
            }

            this.setPosX(x); // Update x position
            logLabel.setLocation(x, this.getPosY()); // Update label location

            if (x > Properties.SCREEN_Y + 32) {
                x = -this.getWidth();
                this.setPosX(x);
            } else if (x < -this.getWidth() - 32) {
                x = Properties.SCREEN_X;
                this.setPosX(x);
            }

            try {
                Thread.sleep(speed);
            } catch (InterruptedException e) {
                System.out.println("Log Thread Interrupted");
                Thread.currentThread().interrupt();
                break;
            }


        }

        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }



}
