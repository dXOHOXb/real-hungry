package Modules;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.AudioSystem;

public class MAudioPlayer {
    public Clip getBulkAudio(String location){ //ambil instansi audio saja, pengolahannya nanti
        try {
            AudioInputStream newAudio = AudioSystem.getAudioInputStream(new File(location));
            Clip clip = AudioSystem.getClip();
            clip.open(newAudio);
            return clip;
        } catch (Exception e) {
            System.out.println("AUDIOERROR"+e);
            return null;
        }
    }

    public void adjustVolume(Clip clip,int volume){ //pengubah volume suara/audio
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(volume);
    }

    public void playAudio(String location,int loops){ //langsung mainkan audio
        try{
            File audio = new File(location);
            if (audio.exists()) {
                AudioInputStream newAudio = AudioSystem.getAudioInputStream(audio);
                Clip clip = AudioSystem.getClip();
                clip.open(newAudio);
                clip.start();
                clip.loop(loops);
            } else{
                System.out.println("Can't find "+location);
            }
        } catch (Exception e){
            System.out.println("AUDIOERROR"+e);
        }
    }
}
