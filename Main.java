import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Main extends JFrame {

    JLabel[] scoreLabels = new JLabel[5];

    public Main() {
        this.setTitle("FROGGER");
        this.setSize(Properties.SCREEN_X, Properties.SCREEN_Y);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        // Stop High Score Music if it's playing
        Audio.stopHighScoreSound();
        Audio.initializeAudio();
        Audio.playMenuMusic();

        // Frogger JFame Icon
        ImageIcon froggerIcon = new ImageIcon(getClass().getResource("images/frogIcon.png"));
        if (froggerIcon.getImage() != null) {
            setIconImage(froggerIcon.getImage());
            System.out.println("Image loaded successfully.");
        } else {
            System.out.println("Image not loaded.");
        }

        // Main Content Pane
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(Color.BLACK);
        this.add(mainContent);

        // Top Title Pane
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.setBackground(Color.BLACK);
        // Space
        topPanel.add(Box.createVerticalStrut(75));

        JLabel titleLabel = new JLabel("FROGGER");
        titleLabel.setForeground(Color.GREEN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        JLabel authorLabel = new JLabel("CREATED BY KENT CAMPBELL, 2023");
        authorLabel.setForeground(Color.GREEN);
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(authorLabel);

        mainContent.add(topPanel, BorderLayout.NORTH);

        // Space
        mainContent.add(Box.createVerticalStrut(75));

        // Middle Buttons Pane
        Dimension buttonSize = new Dimension(100, 45);

        JPanel middlePanel = new JPanel();
        middlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        middlePanel.setBackground(Color.BLACK);
        mainContent.add(middlePanel);

        JButton startButton = new JButton("START");
        startButton.setFocusable(false);
        startButton.setPreferredSize(buttonSize);
        startButton.setBorder(new EmptyBorder(0,0,0,0));
        startButton.setBackground(Color.GREEN);
        startButton.setForeground(Color.BLACK);
        startButton.addActionListener(e -> {
            Audio.stopMenuMusic();
            this.dispose();
            Game game = new Game();
        });

        JButton exitButton = new JButton("EXIT");
        exitButton.setFocusable(false);
        exitButton.setPreferredSize(buttonSize);
        exitButton.setBorder(new EmptyBorder(0,0,0,0));
        exitButton.setBackground(Color.GREEN);
        exitButton.setForeground(Color.BLACK);
        exitButton.addActionListener(e -> {
            System.exit(0);
        });

        middlePanel.add(startButton);
        middlePanel.add(Box.createHorizontalStrut(50));
        middlePanel.add(exitButton);

        // Bottom High Scores Pane
        JPanel bottomPanel = new JPanel();
        bottomPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.setBackground(Color.BLACK);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        mainContent.add(bottomPanel);

        JLabel topFroggersTitle = new JLabel("TOP FIVE FROGGERS:");
        topFroggersTitle.setForeground(Color.GREEN);
        bottomPanel.add(topFroggersTitle);

        // Space
        bottomPanel.add(new JLabel(" "));
        bottomPanel.add(new JLabel(" "));

        for (int i = 0; i < 5; i++) {
            Box scoreBox = Box.createVerticalBox();

            scoreLabels[i] = new JLabel();
            scoreLabels[i].setForeground(Color.GREEN);

            scoreBox.add(scoreLabels[i]);
            bottomPanel.add(scoreBox);
        }

        updateScores();
        bottomPanel.add(Box.createVerticalGlue());

        try {
            Font titleFont = Font.createFont(Font.TRUETYPE_FONT, new File("retroFont.ttf"));
            titleFont = titleFont.deriveFont(Font.PLAIN, 50);
            titleLabel.setFont(titleFont);

            Font authorFont = Font.createFont(Font.TRUETYPE_FONT, new File("retroFont.ttf"));
            authorFont = authorFont.deriveFont(Font.PLAIN, 13);
            authorLabel.setFont(authorFont);

            Font buttonFont = Font.createFont(Font.TRUETYPE_FONT, new File("retroFont.ttf"));
            buttonFont = buttonFont.deriveFont(Font.PLAIN, 18);
            startButton.setFont(buttonFont);
            exitButton.setFont(buttonFont);

            Font scoreFont = Font.createFont(Font.TRUETYPE_FONT, new File("retroFont.ttf"));
            scoreFont = scoreFont.deriveFont(Font.PLAIN, 16);
            topFroggersTitle.setFont(scoreFont);
            for (int i = 0; i < 5; i++) {
                scoreLabels[i].setFont(scoreFont);
            }
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        this.setVisible(true);
    }

        public void updateScores() {
        // Declare a connection and a SQL statement
        Connection conn = null;
        Statement stmt = null;

        try {
            // Load DB Driver
            Class.forName("org.sqlite.JDBC");
            System.out.println("Driver loaded successfully.");

            // Create a connection string and connect to the database
            String dbURL = "jdbc:sqlite:userScores.db";
            conn = DriverManager.getConnection(dbURL);

            // If successful
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
                    scoreLabels[i].setText(name + " ..... " + score);
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
    }
}
