import java.awt.Graphics2D;

public abstract class ObjetAffichable {
//Force les objets affichable a avoir une methode dessiner()
	
	public abstract void dessiner(Board board, Graphics2D g2d);
}
