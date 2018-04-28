

public class Joueur extends Intelligence{
	private String nom;
	
	public Joueur(String nom) {
		super();
		this.nom = nom;
		this.tailleMax = 3; //Un joueur a 3 personnages au maximum
	}
	
	public String getNom() {
		return nom;
	}

	public void passerTour() {
	//Termine le tour de tous les personnages du joueur
	//Methode appele quand le joueur veut passer son tour
		for (Personnage personnage : personnages) {
			personnage.terminerTour();
		}
	}
}
