import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;

public class MainMenu extends JFrame {

  // Score labels for the top 5 scores
  private JLabel[] scoreLabels = new JLabel[5];

  public MainMenu() {
    // Create the menu bar
    JMenuBar menuBar = new JMenuBar();

    // Create a menu
    JMenu menu = new JMenu("Menu");
    menuBar.add(menu);

    // Create menu items
    JMenuItem startGame = new JMenuItem("Start Game");
    JMenuItem exit = new JMenuItem("Exit");

    // Add menu items to menu
    menu.add(startGame);
    menu.add(exit);

    // Set the menu bar for this frame
    setJMenuBar(menuBar);

    // Add action listeners for the menu items
    startGame.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // Start the game
          dispose();
          Game game = new Game();
          game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          game.setVisible(true);
        }
      }
    );

    exit.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // Exit the game
          System.exit(0);
        }
      }
    );

    // Set frame properties
    setSize(600, 600);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    // Create and add score labels
    for (int i = 0; i < 5; i++) {
      scoreLabels[i] = new JLabel();
      scoreLabels[i].setBounds(10, 50 + (i * 50), 200, 30);
      add(scoreLabels[i]);
    }

    // Set frame layout to null
    setLayout(null);
    // Set the score labels
    updateScores();
  }

  public void updateScores() {
    // Declare a connection and a sql statement
    Connection conn = null;
    Statement stmt = null;

    try {
      // Load DB Driver
      Class.forName("org.sqlite.JDBC");
      System.out.println("Driver loaded successfully.");

      // Create a connection string and connect to database
      String dbURL = "jdbc:sqlite:userScores.db";
      conn = DriverManager.getConnection(dbURL);

      // if successful
      if (conn != null) {
        // Create a statement and execute it
        stmt = conn.createStatement();

        // Query to get the top 5 scores
        ResultSet resultSet = stmt.executeQuery(
          "SELECT * FROM SCORES ORDER BY SCORE DESC LIMIT 5"
        );

        // Update score labels
        int i = 0;
        while (resultSet.next() && i < 5) {
          String name = resultSet.getString("NAME");
          int score = resultSet.getInt("SCORE");
          scoreLabels[i].setText(name + ": " + score);
          i++;
        }

        // Clear any remaining labels
        while (i < 5) {
          scoreLabels[i].setText("");
          i++;
        }
      }
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    } finally {
      // Close connection and statement
      try {
        if (stmt != null) stmt.close();
        if (conn != null) conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

     try {
    Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("retroFont.ttf"));
    customFont = customFont.deriveFont(Font.PLAIN, 16);
    for (JLabel scoreLabel : scoreLabels) {
      scoreLabel.setFont(customFont);
    }
  } catch (FontFormatException | IOException e) {
    e.printStackTrace();
  }
  }
}
