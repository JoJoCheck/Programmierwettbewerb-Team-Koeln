package utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class MusicPlayer {
    private final Clip clip;
    private final FloatControl gainControl;
    private final long duration;

    public MusicPlayer() {
        try {
            AudioInputStream audioIn = getAudioInputStream(new File("sounds/Popcorn - KanRaf.wav"));
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            duration = clip.getMicrosecondLength();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    // Plays the audio
    public void play() {
        clip.loop(-1);
    }

    // Stops the playback
    public void stop() {
        clip.stop();
    }

    // Sets the time of the clip
    public void setTime(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Millis not valid: " + millis);
        }
        float percentage = (float) (((millis * 1000. / duration) * 100) % 100);
        setTimePosition(percentage);
    }

    // Sets the time position
    private void setTimePosition(float percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage not valid: " + percentage);
        }
        long time = (long) ((percentage / 100) * duration);
        clip.setMicrosecondPosition(time);
    }

    // Returns the volume of the clip
    public int getVolume() {
        return (int) Math.pow(10f, gainControl.getValue() / 20f) * 100;
    }

    // Returns the volume between 0 and 100
    public void setVolume(int volume) {
        if (volume < 0 || volume > 100) {
            throw new IllegalArgumentException("Volume not valid: " + volume);
        }
        gainControl.setValue(20f * (float) Math.log10(volume / 100.0f));
    }
}