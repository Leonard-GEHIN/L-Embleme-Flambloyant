import javax.swing.ImageIcon;

public class Hache extends Personnage{
	protected static String[] tabGenerationNom = new String[4];
	protected static String classe;
	protected static ImageIcon imageVictoire;
	protected static ImageIcon[] imageDebout = new ImageIcon[3];
	protected static ImageIcon[][] imageMouvement  = new ImageIcon[4][4];

	public Hache (boolean personnageJoueur) {
		super(personnageJoueur);
		/*
		 * Variable a modifier sur chaque classe
		 */
		int pointsTotal = Methode.nombreAlea(55, 60);
		
		int pointsRestant = pointsTotal;
		
		//Generation des stat du personnage
		int nombreStatRestanteACalcule = 3;
		this.attaque = generationStat(pointsTotal, pointsRestant, nombreStatRestanteACalcule, ratioAttaque);
		pointsRestant -= this.attaque;
		nombreStatRestanteACalcule--;
		this.defense = generationStat(pointsTotal, pointsRestant, nombreStatRestanteACalcule, ratioDefence);
		pointsRestant -= this.defense;
		nombreStatRestanteACalcule--;
		this.pointsDeVie = generationStat(pointsTotal, pointsRestant, nombreStatRestanteACalcule, ratioPointsDeVie);
		pointsRestant -= this.pointsDeVie;
		nombreStatRestanteACalcule--;
		this.nom = this.genererNom();
		
		System.out.println("Epee genere : "+this.nom);
	}

	public static void chargerClasse() {
		//Variables static
		classe = "Hache";
		ratioPointsDeVie = 0.55;
		ratioAttaque = 0.30;
		ratioDefence = 1 - ratioAttaque - ratioPointsDeVie;
		
		String[] tabTempNom = {"Barte", "Arthur", "Hector", "Gunter"};
		tabGenerationNom = tabTempNom;
		
		imageVictoire = new ImageIcon("Sprite/" + classe + "/victoire.png");
		
		//Image debout
		for (int i = 0; i < imageDebout.length; i++) {
			imageDebout[i] = new ImageIcon("Sprite/" + classe + "/debout "+ i + ".png");
		}
		
		//Image de mouvement
		String[] ordreImage = {"droite", "haut", "gauche", "bas"};
		for (int j = 0; j < imageMouvement.length; j++) {
			for (int i = 0; i < imageMouvement[j].length; i++) {
				imageMouvement[j][i] = new ImageIcon("Sprite/" + classe + "/" + ordreImage[j] + " "+ i + ".png");
			}	
		}
	}
	
	
	@Override
	public ImageIcon getImage() {
	//Calcul de l'image
		ImageIcon image = null;
		if(estEnMouvement)
			image = imageMouvement[this.directionMouvement][this.compteurSkin];
		else if(attendDeselectionOuAttaque || victoire)
			image = imageVictoire;
		else
			image = imageDebout[this.compteurSkin];
		return image;
	}
	
	@Override
	public void update() {
		if(estEnMouvement)
			compteurSkin = ( compteurSkin + 1 ) % imageMouvement[0].length;
		else
			compteurSkin = ( compteurSkin + 1 ) % imageDebout.length;
	}

	
	@Override
	public double getAttaque(Personnage cible) {
		// TODO Auto-generated method stub
		return this.attaque;
	}
	

	@Override
	public double getDefense(Personnage cible) {
		// TODO Auto-generated method stub
		return this.defense+5;
	}
	
	@Override
	public String getClasse() {
		return classe;
	}

	@Override
	public String[] getTabGenerationNom() {
		return tabGenerationNom;
	}
}