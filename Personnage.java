import java.awt.Graphics2D;
import javax.swing.ImageIcon;

public abstract class Personnage extends ObjetAffichable{
	protected double defence, pointsDeVie, attaque;
	protected String nom;
	protected String classe;
	protected boolean aJouer = false, estEnMouvement = false;
	protected int directionMouvement = 0, compteurSkin = 0;
	protected int caseX, caseY, offsetMouvementX = 0, offsetMouvementY = 0;
	
	//Variable de classe
	//Variable servant a calculer les statistiques des personnages
	protected static double ratioAttaque;
	protected static double ratioDefence;
	protected static double ratioPointsDeVie;
	
	protected static ImageIcon imageVictoire;
	protected static ImageIcon[] imageDebout = new ImageIcon[3];
	protected static ImageIcon[][] imageMouvement  = new ImageIcon[4][5];
	

	//Constructeur utiliser dans les enfants
	public Personnage(boolean personnageJoueur) {
		//Position initiale
		boolean positionTrouve = false;
		int i = 0, j = 0;
		int[][] carte = Carte.getCarte();
		while(i < carte[0].length && !positionTrouve) {
			while (j < carte.length && !positionTrouve) {
				if(carte[j][i] == 1) {
					positionTrouve = true;
					caseX = i;
					caseY = j;
				}
				j++;
			}
			j = 0;
			i++;
		}
		System.out.println(caseX + "    "+ caseY);
		
	}

	public abstract void attaque();
	public abstract void casesJouable();	


	@Override
	public void chargerImage() {
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
	}
	
	@Override
	public void dessiner(Board board, Graphics2D g2d) {
		double sc = Application.SCALE;
		ImageIcon image;
		//Calcul de l'image
		if(estEnMouvement) {
			image = imageMouvement[this.directionMouvement][this.compteurSkin];
		}
		else {
			image = imageDebout[this.compteurSkin];
		}

		g2d.drawImage(image.getImage(),
				(int)(sc*(16*caseX)-15), (int)(sc*(16*caseY)-15),
				(int)(sc*31), (int)(sc*31),
				board);
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
	public String getClasse() {
		return classe;
	}

	/**
	 * @param classe the classe to set
	 */
	public void setClasse(String classe) {
		this.classe = classe;
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
