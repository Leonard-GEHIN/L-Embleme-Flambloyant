import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class Sound {
    private Clip clip; //Clip est un objet permettant de stocker des données audio
    
    public Sound(String nom){
    	try {
            File file = new File(nom); //On charge le fichier audio
            if (file.exists()) {
                AudioInputStream sound = AudioSystem.getAudioInputStream(file); //On interprète les données
                clip = AudioSystem.getClip(); //On adapte le clip au mixeur audio utilisé
                clip.open(sound); //On remplit le clip avec les données audio
            }
            else {
                System.out.println("Pas de fichier appelé:" + nom);
            }
        }
        catch (MalformedURLException e) {
        }
        catch (UnsupportedAudioFileException e) {
        }
        catch (IOException e) {
        }
        catch (LineUnavailableException e) { // try/catch auto-généré
        }
    }
    public void play(){ // on démarre le son en le remettant au début d'abord
        clip.setFramePosition(0);
        clip.start();
    }
    public void loop(){
        clip.loop(Clip.LOOP_CONTINUOUSLY); // on permet au son de se répéter à sa fin
    }
    public void stop(){ // on arrête le son
            clip.stop();
        }
    }