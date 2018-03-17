import java.awt.Graphics2D;
import java.util.ArrayList;

public abstract class Intelligence extends ObjetAffichable{
	protected ArrayList<Personnage> personnages = new ArrayList<Personnage>();

	public Intelligence() {
		super();
	}
	
	
	@Override
	public void dessiner(Board board, Graphics2D g2d) {
		for (Personnage personnage : this.personnages) {
			personnage.dessiner(board, g2d);
		}
	}
	
	
	public boolean caseEstRempliParPersonnage(int x, int y) {
		boolean retour = false;
		for (Personnage personnage : personnages) {
			if(personnage.getCaseX() == x && personnage.getCaseY() == y)
				retour = true;
		}
		return retour;
	}
	
	
	public int dessinerInformation(Board board, Graphics2D g2d, int offsetXEnCase, int offsetYEnPixel) {
	//Renvoie la taille total de ces information en nombre de caractere
		for (Personnage personnage : this.personnages) {
			offsetXEnCase += personnage.dessinerInformation(board, g2d, offsetXEnCase, offsetYEnPixel);
			offsetXEnCase += 1;
		}
		return offsetXEnCase;
	}
	
	
	public void ajouterPersonnage(Personnage personnage) {
		if(this.personnages.size() <= 3) {
			this.personnages.add(personnage);
		}
	}
	
	
	public void update() {
		for (Personnage personnage : this.personnages) {
			if(!personnage.isEstEnMouvement()) personnage.update();
		}
	}
	

	public int selectionPersonnageJouable(int x, int y) {
	//renvoie l'indice du personnage situe en (x,y). et -1 si il n'y a pas de personnage a cette case
		int indicePersonnage = this.selectionIndicePersonnage(x, y);
		
		if(!(indicePersonnage > -1 && !this.personnages.get(indicePersonnage).isTourTerminer())) {
		//Si le personnage n'est pas jouable, on renvoie -1
			indicePersonnage = -1;
		}
		return indicePersonnage;
	}
	
	public int selectionIndicePersonnage(int x, int y) {
		int indicePersonnage = -1, i = 0;
		boolean rechercheFinie = false;
		while( i < this.personnages.size() && !rechercheFinie ) {
			if(this.personnages.get(i).estPresent(x, y)) {
				// Il y a un seul personnage sur une case, on peut arreter la boucle
				indicePersonnage = i;
				rechercheFinie = true;
			}
			i++;
		}
		return indicePersonnage;
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
	

	public void retirerPersonnage(Personnage personnageARetirer) {
		// On recherche le personnage, on l'enleve apres
		this.personnages.remove(personnageARetirer);
	}
	
	
	public void attaquePersonnage(int indicePersonnageAttaquant, int indicePersonnageCible, Intelligence intelCible) {
		this.personnages.get(indicePersonnageAttaquant).attaque(
				intelCible.getPersonnages(indicePersonnageCible));
	}
	
	public void victoire() {
		for (Personnage perso : personnages) {
			perso.victoire();
		}
	}

	/**
	 * @return the personnages
	 */
	public ArrayList<Personnage> getPersonnages() {
		return this.personnages;
	}
	

	public Personnage getPersonnages(int indice) {
	//Renvoie null si l'indice n'existe pas, le personnage voulu sinon
	//Cela ajoute une securite
		return indice <= personnages.size()-1 ? personnages.get(indice) : null;
	}
}







