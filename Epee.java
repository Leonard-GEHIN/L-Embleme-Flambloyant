import javax.swing.ImageIcon;

public class Epee extends Personnage{	
	public Epee (boolean personnageJoueur) {
		super(personnageJoueur);
		/*
		 * Variable a modifier sur chaque classe
		 */
		int pointsTotal = Methode.nombreAlea(50, 55);
		
		int pointsRestant = pointsTotal;
		//TODO generation des noms
		
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
		ratioAttaque = 0.30;
		ratioDefence = 1 - ratioAttaque - ratioPointsDeVie;
		
		String[] tabTempNom = {"Shiida", "Lyndis", "Fir", "Hana"};
		tabGenerationNom = tabTempNom;
		
		imageVictoire = new ImageIcon(classe + "/victoire.png");
		
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
		System.out.println("Classe Epee Charger.");
	}
	

	@Override
	public void meurt() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getAttaque(Personnage cible) {
		return this.attaque;
	}

	@Override
	public double getDefense(Personnage cible) {
		return this.defense;
	}
}






