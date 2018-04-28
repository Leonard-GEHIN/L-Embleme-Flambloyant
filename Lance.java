import javax.swing.ImageIcon;

public class Lance extends Personnage{
	/*
	 * Attributs static utilise pour chaque classes de Personnages mais qui doivent contenir differente informations
	 * Ces attributs sont accessibles via des ascesseur abstrait dans la classe Mere
	 */
	protected static String[] tabGenerationNom = new String[4];
	protected static String classe;
	protected static ImageIcon imageVictoire;
	protected static ImageIcon[] imageDebout = new ImageIcon[3];
	protected static ImageIcon[][] imageMouvement  = new ImageIcon[4][4];

	//Atributs servant a calculer les statistiques des personnages
	protected static double ratioAttaque;
	protected static double ratioDefence;
	protected static double ratioPointsDeVie;

	public Lance (boolean personnageJoueur) {
		super(personnageJoueur);
		this.nom = this.genererNom();
	}

	
	public static void chargerClasse() {
	//Charges les attributs de stats
		//Variables static
		classe = "Lance";
		ratioPointsDeVie = 0.5;
		ratioAttaque = 0.35;
		ratioDefence = 1 - ratioAttaque - ratioPointsDeVie;
		
		//Nom
		String[] tabTempNom = {"Effie", "Fjorm", "Abel", "Jagen"};
		tabGenerationNom = tabTempNom;
		
		//Charge principalement les images
		Personnage.chargerClasse(classe);
	}
	
	/*
	 * Ascesseur et mutateur pour que la classe Mere puisse acceder aux attribut
	 */
	
	@Override
	public double getAttaque(Personnage cible) {
		return (int) (this.attaque * 1.1);
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