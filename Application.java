import java.awt.EventQueue;
import javax.swing.JFrame;
@SuppressWarnings("serial")
public class Application extends JFrame {
    
    public Application() {
        add(new Board()); // on ajoute un Board a l'application

        setResizable(false); //Desactive le redimensionnemet
        setTitle("Nom de la fenetre"); 
        setSize(800, 800); //Taille de fenetre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Active le bouton pour fermer la fenetre
        setLocationRelativeTo(null); //Indique que la n'est positionné par rapport à aucun autres composants
    }    
    
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() { //cree un thread pour executer l'application
            @Override
            public void run() { //demarre le jeu
                Application ex = new Application();
                ex.setVisible(true);
            }
        });
    }
}