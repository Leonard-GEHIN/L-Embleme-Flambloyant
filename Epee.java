import java.awt.Graphics2D;

public class Epee extends Personnage{
	
	public Epee () {
		super();
		/*
		 * Variable a modifier sur chaque classe
		 */
		ratioPointsDeVie = 0.50;
		ratioAttaque = 0.30;
		ratioDefence = 1 - ratioAttaque - ratioPointsDeVie;
		String classe = "Epee";
		int pointsTotal = Methode.nombreAlea(50, 55);
		
		int pointsRestant = pointsTotal;
		double attaque, defence, pointDeVie; 
		String nom = "Manieur epee";
		//TODO generation des noms
		
		//Generation des stat du personnage
		this.attaque = generationStat(pointsTotal, pointsRestant, 3, ratioAttaque);
		pointsRestant -= this.attaque;
		this.defence = generationStat(pointsTotal, pointsRestant, 2, ratioDefence);
		pointsRestant -= this.defence;
		this.pointsDeVie = generationStat(pointsTotal, pointsRestant, 1, ratioPointsDeVie);
		
		this.classe = classe;
		this.nom = nom;
	}

	@Override
	public void attaque() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void meurt() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseJouable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dessiner(Board board, Graphics2D g2d) {
		// TODO Auto-generated method stub
		
	}
}
