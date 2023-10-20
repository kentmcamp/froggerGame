import java.awt.Container;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Game extends JFrame implements KeyListener {

  // Variables
  private Container content;
  private Car[][] cars = new Car[4][3];
  // private Log[][] logs = new Log[4][3];

  // Frogger
  private Frogger frogger;
  private JLabel froggerLabel;
  private ImageIcon froggerImage;

  // For Game Loop
  private boolean isGameOver = false;
  private boolean controlsEnabled = true;

  // Audio
  private Clip backgroundMusic;
  private Clip deathSound;

  public static void main(String[] args) {
    Game game = new Game();
    game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    game.setVisible(true); // Display the game frame
  }

  public Game() {
    content = getContentPane(); // Initialize the content pane

    initializeCars(4, 270, 200, 100, 0, "bike.gif");
    initializeCars(4, 400, 200, 200, 1, "car2.gif");
    initializeCars(4, 470, 200, 400, 2, "car.gif");



    playerStart();
    background();

    // Add key listener
    content.addKeyListener(this);

    // Initialize and Play Audio
    initializeAudio();
    playBackgroundMusic();

    // Start Game Loop
    startGameLoop();
  }

  public void startGameLoop() {
    Thread gameLoop = new Thread(
      new Runnable() {
        @Override
        public void run() {
          while (!isGameOver) {
            checkCollision();

            // Frame Rate
            try {
              Thread.sleep(10);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      }
    );
    gameLoop.start();
  }

  public void background() {
    // Create image icon for background
    ImageIcon background = new ImageIcon(
      getClass().getResource("images/background.gif")
    );
    // Scale image to fit screen
    background.setImage(
      background
        .getImage()
        .getScaledInstance(
          GameProperties.SCREEN_WIDTH,
          GameProperties.SCREEN_HEIGHT,
          Image.SCALE_DEFAULT
        )
    );
    // Create label for background
    JLabel backgroundLabel = new JLabel(background);
    // Set label bounds
    backgroundLabel.setBounds(
      0,
      0,
      GameProperties.SCREEN_WIDTH,
      GameProperties.SCREEN_HEIGHT
    );
    // Set frame bounds
    setSize(GameProperties.SCREEN_WIDTH, GameProperties.SCREEN_HEIGHT);

    // Other JFrame properties
    setTitle("Frogger");
    content.setLayout(null);
    setResizable(false);
    setLocationRelativeTo(null);
    content.setFocusable(true);

    // Add background label to content pane
    content.add(backgroundLabel);
  }

  public void playerStart() {
    // Frogger Setup
    frogger = new Frogger(300, 530, 32, 32, "aniFrog.gif");
    frogger.setImage("aniFrog.gif");
    froggerLabel = new JLabel();
    froggerImage =
      new ImageIcon(getClass().getResource("images/" + frogger.getImage()));

    froggerLabel.setIcon(froggerImage);
    froggerLabel.setSize(frogger.getWidth(), frogger.getHeight());
    froggerLabel.setLocation(frogger.getPosX(), frogger.getPosY());

    content.add(froggerLabel);
  }

  public void initializeCars(int rowSize, int height, int widthOffset, int speed, int row, String image) {
    for (int i = 0; i < rowSize; i++) {
      int xSpace = i * widthOffset;
      carStart(xSpace, height, image, speed, i, row);
    }
  }

  public void carStart(int width, int height, String image, int speed, int indexNumber, int row) {
    // Car Setup
    Car car = new Car(width, height, 64, 32, image, true, speed);

    // Set image
    car.setImage(image);
    JLabel carLabel = new JLabel();
    ImageIcon carImage = new ImageIcon(
      getClass().getResource("images/" + car.getImage())
    );

    // Set label properties
    carLabel.setIcon(carImage);
    carLabel.setSize(car.getWidth(), car.getHeight());
    carLabel.setLocation(car.getPosX(), car.getPosY());
    car.setCarLabel(carLabel);

    // Set speed
    car.setSpeed(speed);

    // Add car to content pane
    car.setIsMoving(true);
    car.startThread();
    content.add(carLabel);

    // Add car to array
    cars[indexNumber][row] = car;
  }

  // Key Listener Methods
  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyPressed(KeyEvent e) {
    if (controlsEnabled) {
      // Get current x and y position
      int x = frogger.getPosX();
      int y = frogger.getPosY();

      // On KeyEvent, update Frogger's position by 1 character step
      if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
        y -= GameProperties.CHARACTER_STEP;
      } else if (
        e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S
      ) {
        // Boundary Check for bottom
        if (y >= 530) {
          return;
        }
        y += GameProperties.CHARACTER_STEP;
      } else if (
        e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A
      ) {
        // Boundary Check for left
        if (x <= 32) {
          return;
        }
        x -= GameProperties.CHARACTER_STEP;
      } else if (
        e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D
      ) {
        // Boundary Check for right
        if (x >= 580) {
          return;
        }
        x += GameProperties.CHARACTER_STEP;
      } else {
        System.out.println("Invalid Key Pressed");
        return;
      }

      // Update Frogger's position
      frogger.setPosX(x);
      frogger.setPosY(y);
      froggerLabel.setLocation(frogger.getPosX(), frogger.getPosY());
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {}

  public void checkCollision() {
    Rectangle froggerRectangle = frogger.getRectangle();

    for (Car[] carRow : cars) {
      for (Car car : carRow) {
        if (froggerRectangle.intersects(car.getRectangle())) {
          System.out.println("Collision Detected");
          // isGameOver = true; // This fixes multiple collisions issues, but causes collision to no longer be detected
          controlsEnabled = false;
          playDeathSound();

          // Change Frogger Image
          frogger.setImage("aniFrogRed.gif");
          ImageIcon deadFrogger = new ImageIcon(
            getClass().getResource("images/" + frogger.getImage())
          );
          froggerLabel.setIcon(deadFrogger);

          // Reset Frogger (in timer) to give time for death animation and sound to play
          TimerTask task = new TimerTask() {
            public void run() {
              System.out.println("Resetting Frogger");
              frogger.setImage("aniFrog.gif");
              ImageIcon normalFrogger = new ImageIcon(
                getClass().getResource("images/" + frogger.getImage())
              );
              froggerLabel.setIcon(normalFrogger);
              frogger.setPosX(300);
              frogger.setPosY(530);
              froggerLabel.setLocation(frogger.getPosX(), frogger.getPosY());
              controlsEnabled = true;
            }
          };

          Timer timer = new Timer();
          timer.schedule(task, 300);
        }
      }
    }
  }

  public void initializeAudio() {
    try {
      // Background Music
      File musicFile = new File("audio/music.wav");
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
        musicFile
      );
      backgroundMusic = AudioSystem.getClip();
      backgroundMusic.open(audioInputStream);

      // Death Sound
      File deathSoundFile = new File("audio/death.wav");
      audioInputStream = AudioSystem.getAudioInputStream(deathSoundFile);
      deathSound = AudioSystem.getClip();
      deathSound.open(audioInputStream);

      // Move Sound

      // Win Sound

      // High Score Fanfare (must disable background music for this)

    } catch (
      UnsupportedAudioFileException | IOException | LineUnavailableException e
    ) {
      e.printStackTrace();
    }
  }

  public void playBackgroundMusic() {
    if (backgroundMusic != null) {
      backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
    }
  }

  public void playDeathSound() {
    if (!deathSound.isRunning()) {
      deathSound.setFramePosition(0);
      deathSound.start();
    }
  }
}
