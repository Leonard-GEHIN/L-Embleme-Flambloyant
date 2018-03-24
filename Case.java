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
		int transparence = 100;
		
		for (int i = 0; i < carteCase.length; i++) {
			for (int j = 0; j < carteCase[i].length; j++) {
				if(carteCase[i][j] != 0) {
					switch(carteCase[i][j]) {
					case 1: //Case de mouvement
						g2d.setColor(new Color(0, 255, 0, transparence));
						break;
					case 2: //Personnage allie
						g2d.setColor(new Color(0, 0, 255, transparence));
						break;
					case 3: //Personnage ennemi
						g2d.setColor(new Color(255, 0, 0, transparence));
						break;
					case 4: //Personnage qui a finis son tour
						g2d.setColor(new Color(0, 0, 0, transparence+25));
						break;
					case 5: //Personnage selectionner par le joueur
						g2d.setColor(new Color(0, 100, 255, transparence));
						break;
					}
					
					g2d.fillRect((int)(sc*(16*j)), (int)(sc*(16*i)),
							(int)(sc*15), (int)(sc*15));
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
			joueur.getPersonnages(indicePersonnageSelectionner).caseJouable(ennemi);

		if(!caseJouable.isEmpty()) {
		//Case ou le joueur peut deplacer son personnage
			for (int[] caseJouables : caseJouable) {
				carteCase[caseJouables[1]][caseJouables[0]] = 1;
			}
		}

		for (Personnage persoJoueur : joueur.getPersonnages()) {
			if(persoJoueur.isTourTerminer()) {
			//Case d'un personnage qui a fini son tour
				carteCase[persoJoueur.getCaseY()][persoJoueur.getCaseX()] = 4;
			}
			else {
			//Case perso joueur
				carteCase[persoJoueur.getCaseY()][persoJoueur.getCaseX()] = 2;
			}
		}
		
		for (Personnage persoEnnemi : ennemi.getPersonnages()) {
		//Case des ennemis
			carteCase[persoEnnemi.getCaseY()][persoEnnemi.getCaseX()] = 3;
		}
		
		if(Board.personnageSelectionner && Board.indicePersonnageSelectionner > -1) {
		//Personnage selectionner par le joueur
			carteCase[joueur.getPersonnages(Board.indicePersonnageSelectionner).getCaseY()]
					 [joueur.getPersonnages(Board.indicePersonnageSelectionner).getCaseX()] = 5;
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












