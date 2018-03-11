

public class Ennemi extends Intelligence{
	public Ennemi() {
		super();
	}
	
	@Override
	public void debutTour() {
		for (Personnage personnage : personnages) {
			personnage.debutTour();
		}
		this.tourEnnemi();
	}
	
	public void tourEnnemi() {
		for (Personnage perso : personnages) {
			
		}
	}
}
