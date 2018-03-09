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
		for (Personnage personnage : this.personnages) {
			personnage.dessiner(board, g2d);
		}
	}
	
	public void ajouterPersonnage(Personnage personnage) {
		if(this.personnages.size() <= 3) {
			this.personnages.add(personnage);
			System.out.println("Personnage ajoute au joueur");
		}
	}
	
	public void update() {
		for (Personnage personnage : this.personnages) {
			if(!personnage.isEstEnMouvement()) personnage.update();
		}
	}

	public int selectionPersonnage(int x, int y) {
		int retour = -1, i = 0;
		boolean rechercheFinie = false;

		while( i < this.personnages.size() && !rechercheFinie ) {
			if(this.personnages.get(i).estPresent(x, y) && !this.personnages.get(i).isTourTerminer()) {
				// Il y a un seul personnage sur une case, pas besoin d'arreter le for()
				retour = i;
				rechercheFinie = true;
			}
			i++;
		}
		return retour;
	}
	
	public boolean ATerminerSonTour() {
		boolean tourTerminer = true;
		for (int i = 0; i < this.personnages.size(); i++) {
			//Si le tour d'un des personnage n'est pas terminer, alors le tour n'est pas fini
			if(!this.personnages.get(i).isTourTerminer()) {
				tourTerminer = false;
			}
		}
		
		return tourTerminer;
	}
	
	public void debutTour() {
		for (Personnage personnage : personnages) {
			personnage.debutTour();
		}
	}

	/**
	 * @return the personnages
	 */
	public ArrayList<Personnage> getPersonnages() {
		return this.personnages;
	}
}







