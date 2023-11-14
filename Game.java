import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;

public class Game implements KeyListener{

    // ----- Attributes -----

    // Content Pane
    private Container contentPane;
    // Frogger
    private Frogger frogger;
    private JLabel froggerLabel;
    // Sprites
    private Car[][] cars = new Car[4][3];
    private Log[][] logs = new Log[4][3];
    // Points
    private int points = 0;
    private JLabel pointsLabel = new JLabel("  POINTS: " + points);
    // Game Logic
    private boolean isGameOver = false;
    private boolean isCollisionDetected = false;
    private boolean controlsEnabled = true;

    public static void main(String[] args) {
        Main main = new Main();
        main.setVisible(true);
    }

    public Game() {
        // Create new window and set properties
        JFrame window = new JFrame("Frogger");
        window.setSize(Properties.SCREEN_X, Properties.SCREEN_Y);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setIconImage(getWindowIcon());

        // Container contentPane;
        contentPane = window.getContentPane();

        // Initialize Audio and Start BG Music
        Audio.playBackgroundMusic();

        // Add GUI elements to the content pane
        contentPane.add(spawnPointsLabel());
        contentPane.add(spawnExitButton(window));

        // Add elements to the content pane
        contentPane.add(spawnFrogger());

        initializeCars(4, 250, 200, 100, 0, "bike.gif");
        initializeCars(4, 380, 200, 200, 1, "car2.gif");
        initializeCars(4, 440, 200, 400, 2, "car.gif");

        initializeLogs(4, 60, 200, 0);
        initializeLogs(4, 124, 400, 1);
        initializeLogs(4, 188, 800, 2);

        contentPane.add(setBackground());

        // Add key listener
        window.addKeyListener(this);

        // Some more window properties
        window.setFocusable(true);
        window.setVisible(true);

        startGameLoop();
    }

    // ----- WINDOW & GUI ELEMENTS -----
    public JLabel setBackground() {
        // Create background Icon
        ImageIcon backgroundIcon = new ImageIcon("images/background.png");
        // Set background Icon to the size of the screen
        backgroundIcon.setImage(backgroundIcon.getImage().getScaledInstance(Properties.SCREEN_X, Properties.SCREEN_Y, Image.SCALE_DEFAULT));
        // Create background Label from background Icon
        JLabel backgroundLabel = new JLabel(backgroundIcon);

        // Return background Label
        return backgroundLabel;
    }

    public Image getWindowIcon() {
        ImageIcon windowIcon = new ImageIcon("images/frogIcon.png");
        return windowIcon.getImage();
    }

    public JLabel spawnPointsLabel() {
        pointsLabel.setBounds(455, 565, 160, 30);
        pointsLabel.setOpaque(true);
        pointsLabel.setBackground(Color.BLACK);
        pointsLabel.setForeground(Color.WHITE);
        setFont(pointsLabel, 16);
        return pointsLabel;
    }

    public void updatePoints(boolean pointsUp) {
        if (pointsUp == true) {
            points += 50;
        } else {
            points -= 50;
        }
        pointsLabel.setText("  POINTS: " + points);
    }

    public JButton spawnExitButton(JFrame window) {
        JButton exitButton = new JButton("SAVE & EXIT");
        exitButton.setFocusable(false);
        exitButton.setBounds(10, 565, 160, 30);
        exitButton.setOpaque(true);
        exitButton.setBackground(Color.BLACK);
        exitButton.setForeground(Color.WHITE);
        exitButton.setBorder(new EmptyBorder(0,0,0,0));
        exitButton.addActionListener(e -> {
            isGameOver = true;
            Audio.stopBackgroundMusic();
            saveGame();
            window.dispose();
            Main main = new Main();
        });
        setFont(exitButton, 16);
        return exitButton;
    }

    public void setFont(JComponent component, int fontSize) {
        try {
            Font retroFont = Font.createFont(Font.TRUETYPE_FONT, new File("retroFont.ttf") );
            retroFont = retroFont.deriveFont(Font.PLAIN, fontSize);
            component.setFont(retroFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    // ----- SPRITES -----
    // --- FROGGER ---
    public JLabel spawnFrogger() {
        frogger = new Frogger();
        froggerLabel = new JLabel();
        ImageIcon froggerIcon = new ImageIcon(frogger.getImage());

        froggerLabel.setIcon(froggerIcon);
        froggerLabel.setSize(frogger.getWidth(), frogger.getHeight());
        froggerLabel.setLocation(frogger.getPosX(), frogger.getPosY());

        return froggerLabel;
    }

    public void resetFrogger() {
        System.out.println("Resetting Frogger");
        frogger.resetFrogger();
        froggerLabel.setIcon(new ImageIcon(frogger.getImage()));
        froggerLabel.setLocation(frogger.getPosX(), frogger.getPosY());
        controlsEnabled = true;
        isCollisionDetected = false;
    }

    public void froggerDeath() {
      Audio.playDeathSound();
      froggerLabel.setIcon(new ImageIcon("images/aniFrogRed.gif"));
      updatePoints(false);

      // Timer to play death animation
      TimerTask deathTimer = new TimerTask() {
          public void run() {
              resetFrogger();
          }
      };
      Timer timer = new Timer();
      timer.schedule(deathTimer, 300);
    }

    // ----- CARS -----
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
      contentPane.add(carLabel);
      car.startThread();

      // Add car to array
      cars[indexNumber][row] = car;
    }

    // ----- LOGS -----
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
      contentPane.add(logLabel);

      // Add log to array
      logs[indexNumber][row] = log;
    }

    // ----- GAME LOGIC -----
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

    public void checkCollision() {
        Rectangle froggerRectangle = frogger.getRectangle();
        boolean isFroggerOnLog = false;

        // Win Condition if Frogger is past river
        if (frogger.getPosY() <= 32) {
            Audio.playWinSound();
            System.out.println("Win!");
            updatePoints(true);
            resetFrogger();
            return;
        }

        // Collision Detection with Logs
        for (Log[] logRow : logs) {
            for (Log log : logRow) {
                if (froggerRectangle.intersects(log.getRectangle())) {
                    isFroggerOnLog = true;
                    frogger.setPosX(log.getPosX() + Properties.LOG_SPEED + 8);
                    froggerLabel.setLocation(frogger.getPosX(), frogger.getPosY());
                    break;
                }
            }
            if (isFroggerOnLog) {
                break;
            }
        }

      // Loose Condition if Frogger in Water but not on Log
      if (isFroggerAtRiver() && !isFroggerOnLog) {
          if (!isCollisionDetected) {
              System.out.println("Gulp!");
              froggerDeath();
              isCollisionDetected = true;
              return;
            }
        } else {
            isCollisionDetected = false;
        }

        // Collision Detection with Cars
        if (!isCollisionDetected) {
            for (Car[] carRow : cars) {
                for (Car car : carRow) {
                    if (froggerRectangle.intersects(car.getRectangle())) {
                        isCollisionDetected = true;
                        System.out.println("Splat!");
                        controlsEnabled = false;
                        froggerDeath();

                        // Add a delay before checking for collisions again
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }
            }
        }
    }

    public boolean isFroggerAtRiver() {
        return (frogger.getPosY() >= 32 && frogger.getPosY() <= 230);
    }

    // ----- CONTROLS -----
    @Override
    public void keyPressed(KeyEvent e) {
        if (!controlsEnabled) {
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
          Audio.playMoveSound();
          System.out.println("Forward Hop!");
          frogger.moveUp();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
          Audio.playMoveSound();
          System.out.println("Back Hop!");
            // Boundary Check for Bottom
          if (frogger.getPosY() >= 490) {
              System.out.println("You Shall Not Pass!");
              return;
          }
          frogger.moveDown();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
          Audio.playMoveSound();
          System.out.println("Left Hop!");
            // Boundary Check for Left
          if (frogger.getPosX() <= 32) {
              System.out.println("You Shall Not Pass!");
              return;
          }
          frogger.moveLeft();
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
          Audio.playMoveSound();
          System.out.println("Right Hop!");
            // Boundary Check for Right
          if (frogger.getPosX() >= 580) {
              System.out.println("You Shall Not Pass!");
              return;
          }
          frogger.moveRight();

        } else {
            System.out.println("Invalid Key!");
        }

        // Update froggerLabel position
        froggerLabel.setLocation(frogger.getPosX(), frogger.getPosY());
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

     // ---DATABASE ---
    public void saveGame() {
    // Get Name
    String name = JOptionPane.showInputDialog("Enter Your Name (Cancel To Not Save Score): ");
    if (name == null) {
      System.out.println("Please enter a name");
      return;
    }

    // Get Date
    java.util.Date utilDate = new java.util.Date();
    java.sql.Date date = new java.sql.Date(utilDate.getTime());

    // Declare a connection and a sql statement
    Connection conn = null;
    Statement stmt = null;

    // Try
    try {
      // Load DB Driver
      Class.forName("org.sqlite.JDBC");
      System.out.println("Driver loaded successfully.");

      //Create a connection string and connect to database
      String dbURL = "jdbc:sqlite:userScores.db";
      conn = DriverManager.getConnection(dbURL);

      // if successful
      if (conn != null) {
        // disable auto commit
        conn.setAutoCommit(false);

        // create a table statement and execute it
        stmt = conn.createStatement();

        // Check if table exists and create it if it doesn't
        String sql = "";
        sql += "CREATE TABLE IF NOT EXISTS SCORES ";
        sql += "(ID INTEGER PRIMARY KEY AUTOINCREMENT, ";
        sql += "NAME TEXT NOT NULL, ";
        sql += "SCORE INTEGER NOT NULL, ";
        sql += "DATE TEXT NOT NULL)";
        stmt.executeUpdate(sql);
        conn.commit();
        System.out.println("Table created successfully.");


        // INSERT, executeUpdate and commit
        sql = "INSERT INTO SCORES (NAME, SCORE, DATE) ";
        sql += "VALUES ('" + name + "', " + points + ", '" + date + "')";
        stmt.executeUpdate(sql);
        conn.commit();

        // Check if the user's score is in the top 5
        String compareScores = "SELECT COUNT(*) FROM SCORES WHERE SCORE > " + points;
        ResultSet resultSet = stmt.executeQuery(compareScores);
        resultSet.next();
        int count = resultSet.getInt(1) + 1;

        // If the user's score is in the top 5
        if (count <= 5) {
          Audio.playHighScoreSound();
          JOptionPane.showMessageDialog(null, "CONGRATULATIONS, YOU'RE A TOP FIVE FROGGER!");
        }

        // Close Connection
        conn.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
