import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Case extends ObjetAffichable {
	/*
	 * 1 = Case de mouvement
	 * 2 = case joueur
	 * 3 = case ennemi
	 */
	
	protected static int[][] carteCase = new int[10][15];
	protected static ImageIcon imageCaseMouvement;
	protected static ImageIcon imageCaseAllie;
	protected static ImageIcon imageCaseEnnemi;
	
	//Sert a stocker les case ou le personnage peut aller
	protected static ArrayList<int[]> caseJouable = new ArrayList<int[]>();

	@Override
	public void dessiner(Board board, Graphics2D g2d) {
		double sc = Application.SCALE;
		ImageIcon image = null;
		/*
		float[] scales = {1f, 1f, 1f, 0.1f};
		float[] offsets = new float[4];
		RescaleOp rop = new RescaleOp(scales, offsets, null);
		*/
		Color couleur = new Color(Color.TRANSLUCENT); //Sert a mettre de la transparence pour l'image
		for (int i = 0; i < carteCase.length; i++) {
			for (int j = 0; j < carteCase[i].length; j++) {
				if(carteCase[i][j] != 0) {
					switch(carteCase[i][j]) {
					case 1:
						image = imageCaseMouvement;
						break;
					case 2:
						image = imageCaseAllie;
						break;
					case 3:
						image = imageCaseEnnemi;
						break;
					}
					g2d.drawImage(image.getImage(),
							(int)(sc*(16*j)), (int)(sc*(16*i)),
							(int)(sc*15), (int)(sc*15),
							couleur, board);
				}
			}
		}
	}

	private static void reinitialiserCases() {
		while(!caseJouable.isEmpty()) {
			caseJouable.remove(0);
		}
		for (int i = 0; i < carteCase.length; i++) {
			for (int j = 0; j < carteCase[i].length; j++) {
				carteCase[i][j] = 0;
			}
		}
	}

	//Utiliser a chaque mouvement et chaque clic
	public static void genererCarte(Intelligence joueur, Intelligence ennemi, int indicePersonnageSelectionner) {
		reinitialiserCases();
		
		//si indicePersonnageSelectionner vaut -1, aucun personnage n'est selectionner
		if(indicePersonnageSelectionner > -1)
			joueur.getPersonnages().get(indicePersonnageSelectionner).caseJouable(ennemi);

		if(!caseJouable.isEmpty()) {
			for (int[] caseJouables : caseJouable) {
				carteCase[caseJouables[1]][caseJouables[0]] = 1;
			}
		}

		for (Personnage persoJoueur : joueur.getPersonnages()) {
			carteCase[persoJoueur.getCaseY()][persoJoueur.getCaseX()] = 2;
		}
		
		for (Personnage persoEnnemi : ennemi.getPersonnages()) {
			carteCase[persoEnnemi.getCaseY()][persoEnnemi.getCaseX()] = 3;
		}
	}

	public static void afficherCarteTerminal() {
		System.out.println();
		for (int j = 0; j < carteCase.length; j++) {
			for (int i = 0; i < carteCase[0].length; i++) {
				System.out.print(carteCase[j][i] + " ");
			}
			System.out.println();
		}
	}

	public static void chargerClasse() {
		imageCaseAllie = new ImageIcon("Sprite/Carte/Case Allie.png");
		imageCaseEnnemi = new ImageIcon("Sprite/Carte/Case Ennemi.png");
		imageCaseMouvement = new ImageIcon("Sprite/Carte/Case Mouvement.png");
	}


	public static void ajouterValeur(int[] temptab) {
		caseJouable.add(temptab);
	}
	
	public static boolean estCaseValidePourDeplacement(int x, int y) {
		boolean estCaseValidePourDeplacementRetour = false;
		
		for (int[] is : caseJouable) {
			if(x == is[0] && y == is[1]) {
			//Si la case teste est dans cet arrayList, alors la case est valide
				estCaseValidePourDeplacementRetour = true;
			}
		}
		
		return estCaseValidePourDeplacementRetour;
	}
}












