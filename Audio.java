import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Audio {
    private static Clip backgroundMusic;
    private static Clip moveSound;
    private static Clip deathSound;
    private static Clip winSound;
    private static Clip highScoreSound;
    private static Clip menuMusic;

    public static void initializeAudio() {
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

        // High Score Sound
        File highScoreSoundFile = new File("audio/highScore.wav");
        audioInputStream = AudioSystem.getAudioInputStream(highScoreSoundFile);
        highScoreSound = AudioSystem.getClip();
        highScoreSound.open(audioInputStream);

        // Menu Music
        File menuMusicFile = new File("audio/menuMusic.wav");
        audioInputStream = AudioSystem.getAudioInputStream(menuMusicFile);
        menuMusic = AudioSystem.getClip();
        menuMusic.open(audioInputStream);


        } catch ( UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    public static void playBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    public static void stopBackgroundMusic() {
        backgroundMusic.stop();

    }

    public static void playMoveSound() {
        moveSound.setFramePosition(0);
        moveSound.start();
    }

    public static void playDeathSound() {
        if (deathSound != null) {
            deathSound.setFramePosition(0);
            deathSound.start();
        }
    }

    public static void playWinSound() {
        if (winSound != null) {
            winSound.setFramePosition(0);
            winSound.start();
        }
    }

    public static void playHighScoreSound() {
        if (highScoreSound != null) {
            highScoreSound.setFramePosition(0);
            highScoreSound.start();
        }
    }
    public static void stopHighScoreSound() {
        if (highScoreSound != null) {
        highScoreSound.stop();
        }
    }
    public static void playMenuMusic() {
        if (menuMusic != null) {
            menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    public static void stopMenuMusic() {
        if (menuMusic != null) {
        menuMusic.stop();
        }
    }
}
