import java.util.ArrayList;

/*
 * Classe gerant l'intelligence artificielle.
 * Cette intelligence sert a faire jouer les ennemis du jeu.
 */
public class IntelligenceArtificiel { 
	private static boolean[] peutAttaquerIndiceJoueur = {false, false, false};
	protected static ArrayList<int[]> caseJouable = new ArrayList<int[]>();
	private static Joueur joueur;
	private static Ennemi ennemi;
	public static boolean personnageEnnemiPeutAttaquer = false;
	public static int personnageJoueurCible = -1;
	
	//Reinitialise les attribut de classe
	private static void reinitialisationIA() {
		for (int i = 0; i < peutAttaquerIndiceJoueur.length; i++) {
			peutAttaquerIndiceJoueur[i] = false;
		}
		
		personnageAAttaquer();
		caseJouable.clear();
	}
	
	//Methode qui calcul la prochaine action d'un personnage
	public static void activerIntelligenceArtificiel(Personnage perso, Joueur joueurTemp, Ennemi ennemiTemp) {
		boolean peutAttaquer = false, peutSeDeplacer;
		joueur = joueurTemp;
		ennemi = ennemiTemp;
		//On efface les ancienne donnees de l'IA
		reinitialisationIA();
		
		/*
		 * On recherche les actions possible par l'IA : attaquer ou se deplacer
		 */
		
		//parcoursCarte() rempli le tableau peutAttaquerIndiceJoueur et l'arrayList de caseJouable
		parcoursCarte(perso);
		for (int i = 0; i < peutAttaquerIndiceJoueur.length; i++) {
			if(peutAttaquerIndiceJoueur[i]) {
				peutAttaquer = true;
			}
		}
		
		//si l'arrayList de case jouable est vide, alors le personnage ne peut pas se deplacer
		if(caseJouable.isEmpty()) peutSeDeplacer = false;
		else peutSeDeplacer = true;
		
		
		/*
		 * On selectionne l'action utiliser sachant que:
		 * - L'attaque a la prioriter sur tout
		 * - Si le personnage ne peut pas faire d'action, alors il passe son tour
		 */
		
		if(peutAttaquer || peutSeDeplacer) {
		//Recherche de la meilleur cible
			boolean[] peutTuer = {false, false, false};
			double[] degat = new double[3];
			int[] pointDeLattaque = new int[3];
			/*
			 * Simulation d'attaque sur chaque joueur attaquable
			 * Selection de la meilleur attaque sachant qu'une attaque qui tue son adversaire est prioritaire aux autres
			 */
			
			for (int i = 0; i < degat.length; i++) {
				if(joueur.getPersonnages(i) != null) {
					degat[i] = perso.getDegat(joueur.getPersonnages(i));
					if(joueur.getPersonnages(i).getPointsDeVie() - degat[i] <= 0) {
					//Le personnage cible sera tue
						peutTuer[i] = true;
					}
				}
				else degat[i] = -1; //Les degat negatif seront toujours inferieur aux vrai degats
			}
			
			//Calcul des points de l'attaque
			for (int i = 0; i < pointDeLattaque.length; i++) {
				pointDeLattaque[i] = peutTuer[i] ? 200 : 0;
				pointDeLattaque[i] += (int)(degat[i]);
			}
			
			
			//Selection de l'attaque
			int meilleurindice = 0; //Indice pour diriger le personnage si il ne peut pas attaquer
			int meilleurindiceAttaque = -1; //Pour initialiser des qu'on trouve un perso a attaquer
			for (int i = 0; i < pointDeLattaque.length; i++) {
			//Recherche la meilleur attaque lorsque le personnage peut attaquer ou non
				if( pointDeLattaque[meilleurindice] < pointDeLattaque[i]) {
					meilleurindice = i;
				}
				
				if( peutAttaquerIndiceJoueur[i]) {
					if(meilleurindiceAttaque < 0) {
					//Si il faut initialiser l'attaque
						meilleurindiceAttaque = i;
					}
					else if(pointDeLattaque[meilleurindice] < pointDeLattaque[i]) {
					//L'indice est deja initialiser, on peux faire le teste pour avoir la meilleur attaque
						meilleurindiceAttaque = i;
					}
				}
				
				
			}
			
			//Deplacement du personnage
			Personnage cible;
			int xRecherche;
			int yRecherche;
			if(peutAttaquer) {
				//Attaque
					cible = joueur.getPersonnages(meilleurindiceAttaque);
					xRecherche = cible.getCaseX();
					yRecherche = cible.getCaseY();
				
					//Recherche de la case ou le personnage ira pour attaquer
					int xTest, yTest, i = 0;
					boolean caseAdversaireTrouve = false;
					while (i < caseJouable.size() && !caseAdversaireTrouve) {
						xTest = caseJouable.get(i)[0];
						yTest = caseJouable.get(i)[1];
						//if(Math.pow(yRecherche - yTest, 2) + Math.pow(xRecherche - xTest, 2) <= 1) {
						if(Methode.distance(xRecherche, yRecherche, xTest, yTest) <= 1) {
						// La case est a cote de la cible, on peut donc attaquer la cible
							caseAdversaireTrouve = true;
							if( !(xTest == perso.getCaseX() && yTest == perso.getCaseY()) ) {
							//Si le personnage a besoin de se deplacer
								perso.deplacer(xTest, yTest);
								personnageEnnemiPeutAttaquer = true;
								personnageJoueurCible = meilleurindiceAttaque;
							}
							else {
								perso.attaque(cible);
							}
						}	
						i++;
					}
				}
				else if(peutSeDeplacer) {
				//Deplacement
					cible = joueur.getPersonnages(meilleurindice);
					xRecherche = cible.getCaseX();
					yRecherche = cible.getCaseY();
					
					int indiceCaseLaPlusProche = 0;
					for (int i = 0; i < caseJouable.size(); i++) {
						if(Methode.distance(xRecherche, yRecherche, caseJouable.get(i)[0], caseJouable.get(i)[1]) < Methode.distance(xRecherche, yRecherche, caseJouable.get(indiceCaseLaPlusProche)[0], caseJouable.get(indiceCaseLaPlusProche)[1])) {
							indiceCaseLaPlusProche = i;
						}
					}
					
					perso.deplacer(caseJouable.get(indiceCaseLaPlusProche)[0], caseJouable.get(indiceCaseLaPlusProche)[1]);
				}
		}
		
		
		
		perso.terminerTour();
	}


	public static void personnageAAttaquer() {
	//Reinitialise quelques attribut apres l'attaque
		personnageEnnemiPeutAttaquer = false;
		personnageJoueurCible = -1;
	}
	
	
	//Initialise le parcours de carte
	public static void parcoursCarte(Personnage perso) {
		int[][] carte = Carte.getCarte();
		int profondeur = 3; //Profondeur de recursivite
		
		int x = perso.getCaseX();
		int y = perso.getCaseY();
		int[] intTemp = {x,y};
		caseJouable.add(intTemp); //Ajoute la case ou se trouve le personnage
		carte[y][x] = 200; //On indique que la case est deja visite (Son poid sera trop grand pour la traverser)
		
		//Le test sur les x et y pour ne pas sortir de la matrice carte[][] sont effectue dans la methode recursive
		parcoursCarteRecursif(x - 1, y, profondeur, carte);
		parcoursCarteRecursif(x + 1, y, profondeur, carte);
		parcoursCarteRecursif(x, y + 1, profondeur, carte);
		parcoursCarteRecursif(x, y - 1, profondeur, carte);
	}
	
	
	//Parcours la carte de maniere recursive
	//Rempli l'arrayList de caseJouable et le tableau peutAttaquerIndiceJoueur[]
	protected static void parcoursCarteRecursif(int x, int y, int profondeur, int[][] carte) {
		if(x >= 0 && y >= 0 && x < carte[0].length && y < carte.length) { //Test sur les coordonnees
			
			//On regarde si une personnage du joueur est sur la case
			int indicePersonnageEnnemi = joueur.selectionIndicePersonnage(x, y);
			if(indicePersonnageEnnemi > -1) {
			//Un personnage du joueur est trouve, on ne continue pas l'algorithme
				peutAttaquerIndiceJoueur[indicePersonnageEnnemi] = true;
			}
			else {
			//La case est vide, on avance
				//On regarde la profondeur consomme par la case ou se situe la methode
				int profondeurConsomee = 0;
				switch(carte[y][x]){
					case 3: //case normal
						profondeurConsomee = 1;
						break;
					case 4: //Case d'arbre
						profondeurConsomee = 2;
						//Les arbres consomme deux point de mouvements
						break;
					case 10: //Case deja valide
						profondeurConsomee = 100;
						break;
					case 0: //case vide
						profondeurConsomee = 100;
						break;
					default: //Autre case speciale deja valide
						profondeurConsomee = 100;
				}
	
				profondeur -= profondeurConsomee;
				if(profondeur > 0) {
				//Si on a assez de point pour aller dans la case, on poursuit le parcours
					carte[y][x] = 10; //On valide la case pour ne pas y retourner
					
					int[] temptab = {x, y};
					if(!ennemi.caseEstRempliParPersonnage(x, y)) {
					//Si un personnage allie est sur la case, on ne l'ajoute pas aux caseJouable
						caseJouable.add(temptab);
					}
					
					//On reutilise les methode recursive
					parcoursCarteRecursif(x - 1, y, profondeur, carte);
					parcoursCarteRecursif(x + 1, y, profondeur, carte);
					parcoursCarteRecursif(x, y + 1, profondeur, carte);
					parcoursCarteRecursif(x, y - 1, profondeur, carte);
				}
			}
		}
	}
	
	//Affiche la carte dans le terminal
	public static void afficherCarteTerminal(int[][] carte) {
		System.out.println("Carte de parcours :");
		for (int j = 0; j < carte.length; j++) {
			for (int i = 0; i < carte[0].length; i++) {
				System.out.print(carte[j][i] + " ");
			}
			System.out.println();
		}
	}
}
