import java.awt.Graphics2D;

import javax.swing.ImageIcon;

public class Epee extends Personnage{
	
	public Epee (boolean personnageJoueur) {
		super(personnageJoueur);
		/*
		 * Variable a modifier sur chaque classe
		 */
		ratioPointsDeVie = 0.50;
		ratioAttaque = 0.30;
		ratioDefence = 1 - ratioAttaque - ratioPointsDeVie;
		String classe = "Epee";
		int pointsTotal = Methode.nombreAlea(50, 55);
		
		int pointsRestant = pointsTotal;
		String nom = "Manieur epee";
		//TODO generation des noms
		
		//Generation des stat du personnage
		int nombreStatRestanteACalcule = 3;
		this.attaque = generationStat(pointsTotal, pointsRestant, nombreStatRestanteACalcule, ratioAttaque);
		pointsRestant -= this.attaque;
		nombreStatRestanteACalcule--;
		this.defence = generationStat(pointsTotal, pointsRestant, nombreStatRestanteACalcule, ratioDefence);
		pointsRestant -= this.defence;
		nombreStatRestanteACalcule--;
		this.pointsDeVie = generationStat(pointsTotal, pointsRestant, nombreStatRestanteACalcule, ratioPointsDeVie);
		pointsRestant -= this.pointsDeVie;
		nombreStatRestanteACalcule--;
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
	public void casesJouable() {
		// TODO Auto-generated method stub
		
	}
}
