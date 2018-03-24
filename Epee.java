import javax.swing.ImageIcon;

public class Epee extends Personnage{

	protected static String[] tabGenerationNom = new String[4];
	protected static String classe;
	protected static ImageIcon imageVictoire;
	protected static ImageIcon[] imageDebout = new ImageIcon[3];
	protected static ImageIcon[][] imageMouvement  = new ImageIcon[4][4];
	

	//Variables servant a calculer les statistiques des personnages
	protected static double ratioAttaque;
	protected static double ratioDefence;
	protected static double ratioPointsDeVie;
	
	public Epee (boolean personnageJoueur) {
		super(personnageJoueur);
		this.nom = this.genererNom();
	}

	
	public static void chargerClasse() {
		//Variables static
		classe = "Epee";
		ratioPointsDeVie = 0.50;
		ratioAttaque = 0.40;
		ratioDefence = 1 - ratioAttaque - ratioPointsDeVie;
		
		String[] tabTempNom = {"Shiida", "Lyndis", "Fir", "Hana"};
		tabGenerationNom = tabTempNom;
		
		Personnage.chargerClasse(classe);
	}

	@Override
	public double getAttaque(Personnage cible) {
		return this.attaque;
	}

	@Override
	public double getDefense(Personnage cible) {
		return this.defense;
	}

	@Override
	public String getClasse() {
		return classe;
	}

	@Override
	public String[] getTabGenerationNom() {
		return tabGenerationNom;
	}

	@Override
	protected void setImageVictoire(ImageIcon image) {
		imageVictoire = image;
	}

	@Override
	protected void setImageDebout(ImageIcon[] image) {
		imageDebout = image;
	}

	@Override
	protected void setImageMouvement(ImageIcon[][] image) {
		imageMouvement = image;
	}

	@Override
	protected ImageIcon getImageVictoire() {
		return imageVictoire;
	}

	@Override
	protected ImageIcon[] getImageDebout() {
		return imageDebout;
	}

	@Override
	protected ImageIcon[][] getImageMouvement() {
		return imageMouvement;
	}

	@Override
	protected double getRatioAttaque() {
		return ratioAttaque;
	}

	@Override
	protected double getRatioPointsDeVie() {
		return ratioPointsDeVie;
	}

	@Override
	protected double getRatioDefence() {
		return ratioDefence;
	}
}






