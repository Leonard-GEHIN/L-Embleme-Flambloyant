import java.awt.Graphics2D;

import javax.swing.ImageIcon;

public class Carte extends ObjetAffichable{
	/*
	 * Valeur des cases dans les cartes:
	 * 0 = case impassable
	 * 1 = case d'apparition des allies (Se transforme ensuite en case de type 3)
	 * 2 = case d'apparition des ennemis (Se transforme ensuite en case de type 3)
	 * 3 = case ou on peut passer normalement
	 * 4 = case consommamt 2 points de mouvement pour se deplacer (Un personnage en a 2)
	 */
	public static int[][] carte = new int[10][15];
	public static int numeroCarte;
	private ImageIcon imageCarte;

	public Carte() {
		super();
		this.chargerCarte(0);
	}
	
	@Override
	public void dessiner(Board board, Graphics2D g2d) {
		g2d.drawImage(imageCarte.getImage(), 0, 0, Application.largeur-1, Application.hauteur-1, board);
	}

	@Override
	public void chargerImage() {
		
	}
	
	@SuppressWarnings("unused")
	public void chargerCarte(int nouveauNumeroCarte){
		numeroCarte = nouveauNumeroCarte;
		
		imageCarte = new ImageIcon("Sprite/Carte/Carte" + numeroCarte + ".png");
		switch(numeroCarte) {
		case 1:
			break;
		default: //On charge la premiere carte
			int[][] carte ={{0,3,0,0,0,4,3,0,0,0,3,0,0,0,0},
							{0,3,0,0,0,3,3,3,3,3,3,0,3,3,0},
							{3,3,3,4,3,3,4,0,3,3,4,3,3,3,3},
							{4,3,3,3,3,3,3,3,0,3,3,0,3,3,3},
							{3,3,4,3,3,4,3,4,3,3,3,0,0,3,3},
							{3,3,3,4,3,3,3,3,4,3,3,3,3,3,3},
							{4,3,4,4,3,3,4,3,4,4,3,3,0,0,3},
							{4,3,3,4,3,3,4,3,3,4,3,3,3,0,3},
							{3,3,3,4,3,4,4,4,3,3,3,3,3,0,0},
							{3,4,3,3,3,3,4,3,4,3,0,0,0,0,0}};
			break;
		}
	}
	

	/**
	 * @return the imageCarte
	 */
	public ImageIcon getImageCarte() {
		return imageCarte;
	}
}
