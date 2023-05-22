package Modele.Jeu;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

public class MusicPlayer {

    private Clip clip;
    private FloatControl volumeControl;

    public MusicPlayer(String path) {
        try {
            URL url = this.getClass().getClassLoader().getResource(path);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Impossible d'ouvrir le fichier son " + path);
            e.printStackTrace();
        }
    }

    public void drain(){clip.drain();}

    public void resetClip(){
        clip.setFramePosition(0);
    };
    public void play() {
        clip.start();
    }

    public void stop() {
        clip.stop();
    }

    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void setVolume(float volume) {
        if (volume < volumeControl.getMinimum()) {
            volume = volumeControl.getMinimum();
        } else if (volume > volumeControl.getMaximum()) {
            volume = volumeControl.getMaximum();
        }
        volumeControl.setValue(volume);
    }
}
