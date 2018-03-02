import java.awt.Graphics2D;
import java.util.ArrayList;

public class Intelligence extends ObjetAffichable{
	protected ArrayList<Personnage> personnages = new ArrayList<Personnage>();

	public Intelligence() {
		super();
	}
	
	@Override
	public void dessiner(Board board, Graphics2D g2d) {
		for (Personnage personnage : personnages) {
			personnage.dessiner(board, g2d);
		}
	}

	@Override
	public void chargerImage() {
		// TODO Auto-generated method stub
	}
	
	public void ajouterPersonnage(Personnage personnage) {
		this.personnages.add(personnage);
	}
}
