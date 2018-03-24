

public class Joueur extends Intelligence{
	private String nom;
	public Joueur(String nom) {
		super();
		this.nom = nom;
	}
	
	public String getNom() {
		return nom;
	}

	public void passerTour() {
		for (Personnage personnage : personnages) {
			personnage.terminerTour();
		}
	}	
}
