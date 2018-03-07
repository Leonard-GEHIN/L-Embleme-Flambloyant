import java.awt.Graphics2D;
import javax.swing.ImageIcon;

public abstract class Personnage extends ObjetAffichable{
	protected double defence, pointsDeVie, attaque;
	protected String nom;
	protected boolean aJouer = false, estEnMouvement = false, estAllie;
	protected int directionMouvement = 0, compteurSkin = 0;
	protected int caseX, caseY, offsetMouvementX = 0, offsetMouvementY = 0;
	
	//Variables de classe chargees dans la methode static chargerClasse()
	//Variables servant a calculer les statistiques des personnages
	protected static double ratioAttaque;
	protected static double ratioDefence;
	protected static double ratioPointsDeVie;
	
	
	protected static String[] tabGenerationNom = new String[4];
	protected static String classe;

	protected static ImageIcon imageVictoire;
	protected static ImageIcon[] imageDebout = new ImageIcon[3];
	protected static ImageIcon[][] imageMouvement  = new ImageIcon[4][5];

	//Constructeur utiliser dans les enfants
	public Personnage(boolean personnageJoueur) {
		//Position initiale
		estAllie = personnageJoueur;
		int caseATrouve = estAllie ? 1 : 2;
		
		boolean positionTrouve = false;
		int i = 0, j = 0;
		int[][] carte = Carte.getCarte();
		while(i < carte[0].length && !positionTrouve) {
			while (j < carte.length && !positionTrouve) {
				if(carte[j][i] == caseATrouve) {
					positionTrouve = true;
					Carte.libererCaseApparition(i, j); //On enleve la possibilite d'apparition
					caseX = i;
					caseY = j;
				}
				j++;
			}
			j = 0;
			i++;
		}
		System.out.println("Position personnage : " + caseX + " " + caseY);
	}

	public abstract void attaque();

	//Donne un nom au personnage en fonction de sa classe
	public String genererNom() {
		return  tabGenerationNom[Methode.nombreAlea(0, 3)];
	}
	
	@Override
	public void dessiner(Board board, Graphics2D g2d) {
		double sc = Application.SCALE; //Variable pour reduire la methode d'affichage plus bas dans la methode
		ImageIcon image;
		//Calcul de l'image
		if(estEnMouvement)
			image = imageMouvement[this.directionMouvement][this.compteurSkin];
		else
			image = imageDebout[this.compteurSkin];

		//Affiche les images
		g2d.drawImage(image.getImage(),
				(int)(sc*(16*caseX)-16), (int)(sc*(16*caseY)-16),
				(int)(sc*31), (int)(sc*31),
				board);
	}
	
	public void update() {
		if(estEnMouvement)
			compteurSkin = ( compteurSkin + 1 ) % imageMouvement[0].length;
		else
			compteurSkin = ( compteurSkin + 1 ) % imageDebout.length;
	}

	public void meurt() {
		//TODO methode meurt()
	}

	public int generationStat(int statTotal, int statRestant, int nombreStatRestanteACalcule, double ratio) {
		int stat;
		
		if( nombreStatRestanteACalcule > 1) {
			int statMoyenne = (int) (statTotal * ratio);
			stat = Methode.nombreAlea(statMoyenne*0.85, statMoyenne*1.15);		
		}
		else { //Quand il ne reste qu'une stat a calcule, on met tous les points restant dedans
			stat = statRestant;
		}
		
		return stat;
	}
	


	public boolean estPresent(int x, int y) {
		boolean retour = false;
		if(this.caseX == x && this.caseY == y) {
			retour = true;
		}
		return retour;
	}
	
	public void caseJouable(Case tabCase) {
		int[][] carte = Carte.getCarte();
		int profondeur = 2;
		this.caseJouableRecursif(caseX, caseY, profondeur, carte, tabCase);
	}
	
	protected void caseJouableRecursif(int x, int y, int profondeur, int[][] carte, Case tabCase) {
		if(x >= 0 && y >= 0 && x < carte[0].length && y < carte.length) {
			if(carte[y][x] != 10 && profondeur >= 0) {
			//Si la case n'a pas ete visite
				profondeur--;
				carte[y][x] = 10; //On valide la case
				if(carte[y][x] != 0) {
				//La case est libre, on l'ajoute au caseJouable
					int [] temptab = {x, y};
					tabCase.ajouterValeur(temptab);
					this.caseJouableRecursif(x - 1, y, profondeur, carte, tabCase);
					this.caseJouableRecursif(x + 1, y, profondeur, carte, tabCase);
					this.caseJouableRecursif(x, y + 1, profondeur, carte, tabCase);
					this.caseJouableRecursif(x, y - 1, profondeur, carte, tabCase);
				}
			}
		}
	}
	
	/**
	 * @return the defence
	 */
	public double getDefence() {
		return defence;
	}

	/**
	 * @param defence the defence to set
	 */
	public void setDefence(double defence) {
		this.defence = defence;
	}

	/**
	 * @return the pointsDeVie
	 */
	public double getPointsDeVie() {
		return pointsDeVie;
	}

	/**
	 * @param pointsDeVie the pointsDeVie to set
	 */
	public void setPointsDeVie(double pointsDeVie) {
		this.pointsDeVie = pointsDeVie;
	}

	/**
	 * @return the attaque
	 */
	public double getAttaque() {
		return attaque;
	}

	/**
	 * @param attaque the attaque to set
	 */
	public void setAttaque(double attaque) {
		this.attaque = attaque;
	}

	/**
	 * @return the nom
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * @param nom the nom to set
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}

	/**
	 * @return the classe
	 */
	public static String getClasse() {
		return classe;
	}
	
	/**
	 * @return the aJouer
	 */
	public boolean isaJouer() {
		return aJouer;
	}

	/**
	 * @param aJouer the aJouer to set
	 */
	public void setaJouer(boolean aJouer) {
		this.aJouer = aJouer;
	}

	/**
	 * @return the estEnMouvement
	 */
	public boolean isEstEnMouvement() {
		return estEnMouvement;
	}

	/**
	 * @param estEnMouvement the estEnMouvement to set
	 */
	public void setEstEnMouvement(boolean estEnMouvement) {
		this.estEnMouvement = estEnMouvement;
	}

	/**
	 * @return the directionMouvement
	 */
	public int getDirectionMouvement() {
		return directionMouvement;
	}

	/**
	 * @param directionMouvement the directionMouvement to set
	 */
	public void setDirectionMouvement(int directionMouvement) {
		this.directionMouvement = directionMouvement;
	}

	/**
	 * @return the caseX
	 */
	public int getCaseX() {
		return caseX;
	}

	/**
	 * @param caseX the caseX to set
	 */
	public void setCaseX(int caseX) {
		this.caseX = caseX;
	}

	/**
	 * @return the caseY
	 */
	public int getCaseY() {
		return caseY;
	}

	/**
	 * @param caseY the caseY to set
	 */
	public void setCaseY(int caseY) {
		this.caseY = caseY;
	}
}
