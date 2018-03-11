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

	public Carte() {
		super();
		chargerCarte(0);
	}

	@Override
	public void dessiner(Board board, Graphics2D g2d) {
		g2d.drawImage(imageCarte.getImage(), 0, 0,
				(int) (Application.SCALE * 240 - 1), getHauteurEnPixel(), board);
	}

	public static void chargerCarte(int nouveauNumeroCarte) {
		numeroCarte = nouveauNumeroCarte;

		imageCarte = new ImageIcon("Sprite/Carte/Carte" + numeroCarte + ".png");

		switch (numeroCarte) {
		case 1:
			break;
		default: // On charge la premiere carte
			int[][] carteTemp =   { { 0, 3, 0, 0, 0, 4, 3, 0, 0, 0, 3, 0, 0, 0, 0 },
									{ 0, 3, 0, 0, 0, 3, 3, 3, 3, 3, 3, 0, 3, 3, 0 }, 
									{ 3, 3, 3, 4, 3, 3, 4, 0, 3, 2, 4, 3, 3, 3, 3 },
									{ 4, 3, 3, 3, 3, 3, 3, 3, 0, 1, 2, 0, 3, 3, 3 }, 
									{ 3, 3, 4, 3, 3, 4, 3, 4, 1, 2, 3, 0, 0, 3, 3 },
									{ 3, 3, 3, 4, 3, 3, 3, 3, 4, 1, 3, 3, 3, 3, 3 }, 
									{ 4, 3, 4, 4, 3, 3, 4, 3, 4, 4, 3, 3, 0, 0, 3 },
									{ 4, 3, 3, 4, 3, 3, 4, 3, 3, 4, 3, 3, 3, 0, 3 }, 
									{ 3, 3, 3, 4, 3, 4, 4, 4, 3, 3, 3, 3, 3, 0, 0 },
									{ 3, 4, 3, 3, 3, 3, 4, 3, 4, 3, 0, 0, 0, 0, 0 } };
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
		if (carte[y][x] == 1 || carte[y][x] == 2)
			carte[y][x] = 3;
	}

	//Enleve les case ou aucun personnage n'est apparu
	public static void enleverCaseApparition() {
		for (int j = 0; j < carte.length; j++) {
			for (int i = 0; i < carte[0].length; i++) {
				if(carte[j][i] == 1 || carte[j][i] == 2){
					carte[j][i] = 3;
				}
			}
		}
	}
	

	public static void deplacerPersonnage(int x, int y, Intelligence intelligence, int indicePersonnageSelectionner) {
	/*
	 * Deplace le personnage a la case indiquer
	 * Les test pour valider la case et la possibilite du personnage pour aller a la case on deja ete suffisemment evaluer
	 */
		//En modifiant le personnage ci-dessous, nous modifiont aussi celui contenu dans l'intelligence
		Personnage personnageADeplacer = intelligence.getPersonnages().get(indicePersonnageSelectionner);
		personnageADeplacer.deplacer(x, y);
	}
	
	/**
	 * @return the imageCarte
	 */
	public ImageIcon getImageCarte() {
		return imageCarte;
	}

	/**
	 * @return un clone de la carte afin que les modification externe ne modifie pas la vrai carte
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
}











