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
		for (Personnage perso : personnages) {
			if(!perso.isTourTerminer() && !Board.animationEnCours) {
			//L'ennemi ne joue que si son tour n'est pas terminer et qu'aucun autre personnage ne se deplace / attaque
				IntelligenceArtificiel.activerIntelligenceArtificiel(perso, Board.joueur, this);
			}
		}
	}
}
