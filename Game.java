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
  private Log[][] logs = new Log[4][3];

  // Frogger
  private Frogger frogger;
  private JLabel froggerLabel;
  private ImageIcon froggerImage;

  // For Game Loop
  private boolean isGameOver = false;
  private boolean controlsEnabled = true;
  private boolean isCollisionDetected = false;

  // Audio
  private Clip backgroundMusic;
  private Clip deathSound;
  private Clip moveSound;
  private Clip winSound;

  public static void main(String[] args) {
    Game game = new Game();
    game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    game.setVisible(true); // Display the game frame
  }

  public Game() {
    content = getContentPane(); // Initialize the content pane

    playerStart();

    initializeCars(4, 270, 200, 100, 0, "bike.gif");
    initializeCars(4, 400, 200, 200, 1, "car2.gif");
    initializeCars(4, 470, 200, 400, 2, "car.gif");

    initializeLogs(4, 80, 200, 0);
    initializeLogs(4, 144, 400, 1);
    initializeLogs(4, 208, 800, 2);

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
      getClass().getResource("images/background.png")
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

    // Frogger JFame Icon
    ImageIcon froggerIcon = new ImageIcon(getClass().getResource("images/frogIcon.png"));
    if (froggerIcon.getImage() != null) {
      setIconImage(froggerIcon.getImage());
      System.out.println("Image loaded successfully.");
    } else {
      System.out.println("Image not loaded.");
  }

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

  public void initializeLogs(int rowSize, int height, int speed, int row) {
    for (int i = 0; i < rowSize; i++) {
      int xSpace = i * 200;
      logStart(xSpace, height, speed, i, row);
    }
  }

  public void logStart(
    int xSpace,
    int posY,
    int speed,
    int indexNumber,
    int row
  ) {
    // Log Setup
    Log log = new Log(xSpace, posY, 64, 32, "log.gif", true, speed);

    // Set Image
    log.setImage("log.gif");
    JLabel logLabel = new JLabel();
    ImageIcon logImage = new ImageIcon(
      getClass().getResource("images/" + log.getImage())
    );

    // Set label properties
    logLabel.setIcon(logImage);
    logLabel.setSize(log.getWidth(), log.getHeight());
    logLabel.setLocation(log.getPosX(), log.getPosY());
    log.setLogLabel(logLabel);

    // Set speed
    log.setSpeed(speed);

    // Add log to content pane
    log.setIsMoving(true);
    log.startThread();
    content.add(logLabel);

    // Add log to array
    logs[indexNumber][row] = log;
  }

  // ---CONTROLS AND COLLISION---
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
        playMoveSound();
        y -= GameProperties.CHARACTER_STEP;
      } else if (
        e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S
      ) {
        playMoveSound();
        // Boundary Check for bottom
        if (y >= 530) {
          return;
        }
        y += GameProperties.CHARACTER_STEP;
      } else if (
        e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A
      ) {
        playMoveSound();
        // Boundary Check for left
        if (x <= 32) {
          return;
        }
        // Frogger moves half of a step when moving sideways to allow some more room for movement between cars.
        x -= GameProperties.CHARACTER_STEP / 2; 
      } else if (
        e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D
      ) {
        playMoveSound();
        // Boundary Check for right
        if (x >= 580) {
          return;
        }
        x += GameProperties.CHARACTER_STEP / 2;
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
    if (!isCollisionDetected) {
      Rectangle froggerRectangle = frogger.getRectangle();
      for (Car[] carRow : cars) {
        for (Car car : carRow) {
          if (froggerRectangle.intersects(car.getRectangle())) {
            isCollisionDetected = true;
            System.out.println("Collision Detected");
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
                isCollisionDetected = false;
              }
            };

            Timer timer = new Timer();
            timer.schedule(task, 300);
          }
        }
      }

      // Log Collision
      boolean isFroggerOnLog = false;
      for (Log[] logRow : logs) {
        for (Log log : logRow) {
          if (froggerRectangle.intersects(log.getRectangle())) {
            isFroggerOnLog = true;
            frogger.setPosX(log.getPosX());
            froggerLabel.setLocation(frogger.getPosX(), frogger.getPosY());
            break;
          }
        }
        if (isFroggerOnLog) {
          break;
        }
      }

      // Win Condition if Frogger is past river
      if (frogger.getPosY() <= 80 && isGameOver == false) {
        System.out.println("Victory!");
        // Play Win Sound
        playWinSound();

        // Reset Frogger
      }

      // Check if frogger is in river but not on log
      if (isFroggerAtRiver() && !isFroggerOnLog) {
        isCollisionDetected = true;
        System.out.println("Fogger is drowning!");
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
            isCollisionDetected = false;
          }
        };

        Timer timer = new Timer();
        timer.schedule(task, 300);
      }
    }
  }

  public boolean isFroggerAtRiver() {
    return (frogger.getPosY() >= 80 && frogger.getPosY() <= 240);
  }

  // ---AUDIO---
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
      File moveSoundFile = new File("audio/move.wav");
      audioInputStream = AudioSystem.getAudioInputStream(moveSoundFile);
      moveSound = AudioSystem.getClip();
      moveSound.open(audioInputStream);

      // Win Sound
      File winSoundFile = new File("audio/victory.wav");
      audioInputStream = AudioSystem.getAudioInputStream(winSoundFile);
      winSound = AudioSystem.getClip();
      winSound.open(audioInputStream);

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

  public void playMoveSound() {
    moveSound.setFramePosition(0);
    moveSound.start();
  }

  public void playDeathSound() {
    if (!deathSound.isRunning()) {
      deathSound.setFramePosition(0);
      deathSound.start();
    }
  }

  public void playWinSound() {
    if (!winSound.isRunning()) {
      winSound.setFramePosition(0);
      winSound.start();
    }
  }
}
