import java.awt.Graphics2D;

import javax.swing.ImageIcon;

public class Carte extends ObjetAffichable {
	/*
	 * Valeur des cases dans les cartes:
	 * 0 = case impassable 
	 * 1 = case d'apparition des allies (Se transforme ensuite en case de type 3) 
	 * 2 = case d'apparition des ennemis (Se transforme ensuite en case de type 3) 
	 * 3 = case ou on peut passer normalement 
	 * 4 = case consommamt 2 points de mouvement pour se deplacer (Un personnage en a 2)
	 */

	private static int[][] carte = new int[10][15];
	private static int numeroCarte;
	private static ImageIcon imageCarte;
	private static ImageIcon imageTriangle;

	
	public Carte() {
		super();
		chargerCarte(0);
	}

	
	@Override
	public void dessiner(Board board, Graphics2D g2d) {
	//Dessine la carte et le triangle des armes 
		double sc = Application.SCALE*0.58; //Echelle pour ajuster la taille du triangle des armes

		//Carte
		g2d.drawImage(imageCarte.getImage(), 0, 0,
				getLargeurEnPixel(), getHauteurEnPixel(), board);
		//Triangle des armes
		g2d.drawImage(imageTriangle.getImage(),
				(int) (getLargeurEnPixel() * 0.99 - imageTriangle.getIconWidth() * sc),
				(int) (board.getHeight() * 0.99   - imageTriangle.getIconHeight()*sc),
				(int) (imageTriangle.getIconWidth()*sc),
				(int) (imageTriangle.getIconHeight()*sc), board);
	}

	
	public static void chargerCarte(int nouveauNumeroCarte) {
	//Methode pour charger la carte, facilement modifiable pour ajouter d'autres cartes
		numeroCarte = nouveauNumeroCarte;

		//Chargement
		imageCarte = new ImageIcon(Board.racine + "Carte/Carte" + numeroCarte + ".png");
		imageTriangle = new ImageIcon(Board.racine + "Carte/Faiblesse.png");

		//Charge la matrice de la carte
		switch (numeroCarte) {
		case 0: //Carte 0
			int[][] carteTemp =   { { 0, 3, 0, 0, 0, 4, 3, 0, 0, 0, 3, 0, 0, 0, 0 },
									{ 0, 3, 0, 0, 0, 3, 3, 3, 3, 3, 3, 0, 3, 3, 0 }, 
									{ 3, 3, 3, 4, 3, 3, 4, 0, 3, 3, 4, 2, 3, 3, 3 },
									{ 4, 3, 3, 3, 3, 3, 3, 3, 0, 2, 2, 0, 3, 3, 3 }, 
									{ 3, 3, 4, 3, 3, 4, 3, 4, 3, 3, 3, 0, 0, 3, 3 },
									{ 3, 3, 3, 4, 3, 3, 3, 3, 4, 3, 3, 2, 3, 3, 3 }, 
									{ 4, 3, 4, 4, 3, 3, 4, 3, 4, 4, 2, 3, 0, 0, 3 },
									{ 4, 1, 3, 4, 3, 3, 4, 3, 3, 4, 3, 3, 3, 0, 3 }, 
									{ 3, 3, 1, 4, 3, 4, 4, 4, 3, 3, 3, 3, 3, 0, 0 },
									{ 3, 4, 3, 3, 1, 3, 4, 3, 4, 3, 0, 0, 0, 0, 0 } };
			carte = carteTemp;
			break;
		}
	}
	

	public static void afficherCarteTerminal() {
		for (int j = 0; j < carte.length; j++) {
			for (int i = 0; i < carte[0].length; i++) {
				System.out.print(carte[j][i] + " ");
			}
			System.out.println();
		}
	}

	
	public static void libererCaseApparition(int x, int y) {
	//Si la case cible n'a pas eu de personnage apparue dessus, elle devient une case classique
		if (carte[y][x] == 1 || carte[y][x] == 2)
			carte[y][x] = 3;
	}
	

	//Enleve les case ou aucun personnage n'est apparu
	public static void enleverCaseApparition() {
		for (int j = 0; j < carte.length; j++) {
			for (int i = 0; i < carte[0].length; i++) {
				libererCaseApparition(i, j);
			}
		}
	}
	

	public static void deplacerPersonnage(int x, int y, Intelligence intelligence, int indicePersonnageSelectionner) {
	/*
	 * Deplace le personnage a la case indiquer
	 * Les test pour valider la case et la possibilite du personnage pour aller a la case on deja ete suffisemment evaluer
	 */
		intelligence.getPersonnages(indicePersonnageSelectionner).deplacer(x, y);
	}
	
	
	/**
	 * @return the imageCarte
	 */
	public ImageIcon getImageCarte() {
		return imageCarte;
	}
	

	/**
	 * retourne un clone de la carte afin que les modification externe ne modifie pas la vrai carte
	 */
	public static int[][] getCarte() {
		int[][] carteClone = new int[10][15];
		for (int i = 0; i < carteClone.length; i++) {
			for (int j = 0; j < carteClone[0].length; j++) {
				carteClone[i][j] = carte[i][j];
			}
		}
		
		return carteClone;
	}

	public static int getHauteurEnPixel() {
		return (int) (Application.SCALE * 160 - 1);
	}
	
	public static int getLargeurEnPixel() {
		return (int) (Application.SCALE * 240 - 1);
	}
	
	
}











