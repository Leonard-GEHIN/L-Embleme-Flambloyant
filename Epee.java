import javax.swing.ImageIcon;

public class Epee extends Personnage{

	protected static String[] tabGenerationNom = new String[4];
	protected static String classe;
	protected static ImageIcon imageVictoire;
	protected static ImageIcon[] imageDebout = new ImageIcon[3];
	protected static ImageIcon[][] imageMouvement  = new ImageIcon[4][4];
	
	public Epee (boolean personnageJoueur) {
		super(personnageJoueur);
		/*
		 * Variable a modifier sur chaque classe
		 */
		int pointsTotal = Methode.nombreAlea(50, 55);
		
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
		classe = "Epee";
		ratioPointsDeVie = 0.50;
		ratioAttaque = 0.40;
		ratioDefence = 1 - ratioAttaque - ratioPointsDeVie;
		
		String[] tabTempNom = {"Shiida", "Lyndis", "Fir", "Hana"};
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
	
	//Les methodes getImage() et update() sont positionner dans les classes Epee, Hache et Lance et non dans Personnage afin de correctement charger et utiliser les images dans la memoire et. Si nous chargeons les images dans la classe Personnage, on peut ne pas charger des images de differentes classes en meme temps.
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
}






