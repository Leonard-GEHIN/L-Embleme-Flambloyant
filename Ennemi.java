public class Ennemi extends Intelligence{
	public Ennemi() {
		super();
	}
	
	@Override
	public void debutTour() {
		for (Personnage personnage : personnages) {
			personnage.debutTour();
		}
	}
	
	public void tourEnnemi() {
		try {
			for (Personnage perso : personnages) {
					if(!perso.isTourTerminer() && !Board.animationEnCours) {
					//L'ennemi ne joue que si son tour n'est pas terminer et qu'aucun autre personnage ne se deplace / attaque
						IntelligenceArtificiel.activerIntelligenceArtificiel(perso, Board.joueur, this);
					}
			}
		}
		catch(Exception e) {
			//Une erreur d'acces aux objet est cause par la boucle for ci dessus.
			//Lorsqu'un Personnage est manipuler par la classe IntelligenceArtificiel, il arrive que l'algorithme ressaye d'appeler la fonction alors que celle ci n'a pas fini son traitement.
		}
	}
}