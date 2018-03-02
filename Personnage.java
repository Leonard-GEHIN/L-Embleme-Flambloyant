
public abstract class Personnage extends ObjetAffichable{
	protected double defence, pointsDeVie, attaque;
	protected String nom;
	protected String classe;
	protected boolean aJouer;

	protected static double ratioAttaque;
	protected static double ratioDefence;
	protected static double ratioPointsDeVie;

	//Constructeur utiliser dans les enfants
	public Personnage() {}

	public abstract void attaque();
	public abstract void caseJouable();
	
	@Override
	public void chargerImage() {
		//TODO chargerImage() personnage
	}
	

	public void meurt() {
		//TODO methode meurt()
	}

	public int generationStat(int statTotal, int statRestant, int nombreStatRestanteACalcule, double ratio) {
		int stat;
		
		if( nombreStatRestanteACalcule > 1) {
			int statMoyenne = (int) (statTotal / nombreStatRestanteACalcule * ratio);
			stat = Methode.nombreAlea((int)(statMoyenne*0.9), (int)(statMoyenne*1.1));			
		}
		else { //Quand il ne reste qu'une stat a calcule, on met tous les points restant dedans
			stat = statRestant;
		}
		
		return stat;
	}
}
