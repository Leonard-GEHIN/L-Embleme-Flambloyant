import java.awt.EventQueue;
import javax.swing.JFrame;
@SuppressWarnings("serial")
public class Application extends JFrame {
	public static final double SCALE = 2.5;
    public static int hauteur = (int) (160*SCALE), largeur = (int) (240*SCALE);
    public static Board board = new Board();
	
    public Application() {
        add(board); // on ajoute un Board a l'application

        setResizable(false); //Desactive le redimensionnemet
        setTitle("L'Embleme Flamboyant"); 
        setSize(largeur, hauteur); //Taille de fenetre
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