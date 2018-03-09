import java.awt.Graphics2D;
import java.util.ArrayList;

public abstract class Intelligence extends ObjetAffichable{
	protected ArrayList<Personnage> personnages = new ArrayList<Personnage>();

	public Intelligence() {
		super();
	}
      
	/*
	 * Si joueur: selectionne le personnage a deplacer
	 * Si ennemi: selectionne l'ennemi a attaquer
	 */
	
	@Override
	public void dessiner(Board board, Graphics2D g2d) {
		for (Personnage personnage : personnages) {
			personnage.dessiner(board, g2d);
		}
	}
	
	public void ajouterPersonnage(Personnage personnage) {
		if(personnages.size() <= 3) {
			this.personnages.add(personnage);
			System.out.println("Personnage ajoute au joueur");
		}
	}
	
	public void update() {
		for (Personnage personnage : personnages) {
			if(!personnage.isEstEnMouvement()) personnage.update();
		}
	}

	public int selectionPersonnage(int x, int y) {
		int retour = -1, i = 0;
		boolean rechercheFinie = false;

		while( i < personnages.size() && !rechercheFinie ) {
			if(personnages.get(i).estPresent(x, y) && !personnages.get(i).isaJouer()) {
				// Il y a un seul personnage sur une case, pas besoin d'arreter le for()
				retour = i;
				rechercheFinie = true;
			}
			i++;
		}
		return retour;
	}

	/**
	 * @return the personnages
	 */
	public ArrayList<Personnage> getPersonnages() {
		return personnages;
	}
}







