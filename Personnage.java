import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.Timer;

public abstract class Personnage extends ObjetAffichable implements ActionListener{
	protected int DELAY = 40;
	protected final int tempsParcourUneCase = 300; //Duree en miliseconde pour la traverse d'une case
	protected Timer timer_pas = new Timer(DELAY,this); // timer servant a mettre a jour les deplacement des personnages
	
	protected double defence, pointsDeVie, attaque;
	protected String nom;
	protected boolean aJouer = false, estEnMouvement = false, estAllie;
	
	//Utilise pour mouvement et animation
	protected double distanceAPourcourir = 0;
	protected int directionMouvement = 0, compteurSkin = Methode.nombreAlea(0, 3);
	protected int caseX, caseY, offsetMouvementX = 0, offsetMouvementY = 0, tickAnimationUtilise = 0;
	protected double vitesseX, vitesseY; // en px/ms
	protected int nouvelleCaseX, nouvelleCaseY; //Stock les prochaine valeur du personnage durant l'animation
	

	//Variables de classe chargees dans la methode static chargerClasse()
	//Variables servant a calculer les statistiques des personnages
	protected static double ratioAttaque;
	protected static double ratioDefence;
	protected static double ratioPointsDeVie;
	
	protected static String[] tabGenerationNom = new String[4];
	protected static String classe;

	protected static ImageIcon imageVictoire;
	protected static ImageIcon[] imageDebout = new ImageIcon[3];
	protected static ImageIcon[][] imageMouvement  = new ImageIcon[4][4];

	
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
	

	public void meurt() {
		//TODO methode meurt()
	}

	
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
		//les variables offsetMouvement sert a animer les personnages
		g2d.drawImage(image.getImage(),
				(int)(sc*(16*caseX)-16+offsetMouvementX), (int)(sc*(16*caseY)-16+offsetMouvementY),
				(int)(sc*31), (int)(sc*31),
				board);
	}
	
	
	public void update() {
		if(estEnMouvement)
			compteurSkin = ( compteurSkin + 1 ) % imageMouvement[0].length;
		else
			compteurSkin = ( compteurSkin + 1 ) % imageDebout.length;
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
	
	
	public void caseJouable() {
		int[][] carte = Carte.getCarte();
		int profondeur = 2;
		this.caseJouableRecursif(caseX, caseY, profondeur, carte);
	}
	
	
	protected void caseJouableRecursif(int x, int y, int profondeur, int[][] carte) {
		if(x >= 0 && y >= 0 && x < carte[0].length && y < carte.length) {
			if(carte[y][x] != 10 && profondeur >= 0) {
			//Si la case n'a pas ete visite
				profondeur--;
				carte[y][x] = 10; //On valide la case
				if(carte[y][x] != 0) {
				//La case est libre, on l'ajoute au caseJouable
					int [] temptab = {x, y};
					Case.ajouterValeur(temptab);
					this.caseJouableRecursif(x - 1, y, profondeur, carte);
					this.caseJouableRecursif(x + 1, y, profondeur, carte);
					this.caseJouableRecursif(x, y + 1, profondeur, carte);
					this.caseJouableRecursif(x, y - 1, profondeur, carte);
				}
			}
		}
	}


	public void deplacer(int nouvelleCaseX, int nouvelleCaseY) {
		this.estEnMouvement = true;
		this.nouvelleCaseX = nouvelleCaseX;
		this.nouvelleCaseY = nouvelleCaseY;
		//On utilise des nouveau nom de variable pour plus de clarete dans la suite de la methode
		int vecteurDeplacementX =  this.nouvelleCaseX - this.caseX;
		int vecteurDeplacementY =  this.nouvelleCaseY - this.caseY;
		
		double rapportVecteur = 2;
		try { //Gere le cas ou on ne se deplace pas en Y
			rapportVecteur = vecteurDeplacementX / vecteurDeplacementY;
		} catch (ArithmeticException e) {
			rapportVecteur = 2;
		}
		
		if(Math.abs(rapportVecteur) > 1) {
		//vectX > vectY
		//Donc deplacement horizontal, on regarde la quantite de deplacement en X
			if(vecteurDeplacementX > 0) { // Vers la droite
				this.directionMouvement = 0;
			}
			else { // Vers la gauche
				this.directionMouvement = 2;				
			}
		}
		else {
		//Deplacement Vertical donc on regarde la quantite de deplacement en Y
			if(vecteurDeplacementY < 0) { //Vers le haut
				this.directionMouvement = 1;
			}
			else { //Vers le bas
				this.directionMouvement = 3;
			}
		}
		
		//Calcul vitesse
		this.distanceAPourcourir = Math.sqrt( Math.pow(vecteurDeplacementX,2) + Math.pow(vecteurDeplacementY,2) );
		double tempsAnimation = this.distanceAPourcourir*tempsParcourUneCase; // en miliseconde
		double distanceAPourcourirPixelX = vecteurDeplacementX*16*Application.SCALE;
		double distanceAPourcourirPixelY = vecteurDeplacementY*16*Application.SCALE;
		this.vitesseX = distanceAPourcourirPixelX / tempsAnimation;
		this.vitesseY = distanceAPourcourirPixelY / tempsAnimation;

		timer_pas.start();
		this.aJouer = true;
	}


	@Override
	public void actionPerformed(ActionEvent e) { //actualise l'animation du joueur
		boolean finAnimation = false;
		double tempsAnimation = this.distanceAPourcourir*tempsParcourUneCase;// en miliseconde
		if(this.estEnMouvement) {
			this.offsetMouvementX += this.vitesseX*this.DELAY;
			this.offsetMouvementY += this.vitesseY*this.DELAY;

			if((int)(tempsAnimation / this.DELAY) < this.tickAnimationUtilise) {
				finAnimation = true;
			}
			
			if(this.tickAnimationUtilise%(int)(160 / DELAY) == 0) {
				this.compteurSkin = (this.compteurSkin+1)%4;
			}
			
			this.tickAnimationUtilise++;
		}
		
		if(finAnimation) {
		//Variable a reinitialiser a la fin d'une animation
			if(this.estEnMouvement) {
			//On actualise la position du personnage a la fin du mouvement
				this.caseX = this.nouvelleCaseX;
				this.caseY = this.nouvelleCaseY;
				
			}
			
			
			this.distanceAPourcourir = 0;
			this.vitesseX = 0;
			this.vitesseY = 0;
			this.offsetMouvementX = 0;
			this.offsetMouvementY = 0;
			this.compteurSkin = 0;
			this.directionMouvement = 0;
			this.estEnMouvement = false;
			this.tickAnimationUtilise = 0;
			this.nouvelleCaseX = 0;
			this.nouvelleCaseY = 0;
			this.timer_pas.stop();

			Case.genererCarte(Board.joueur/*, ennemi*/, -1);
			/*
			//Pour porter une attaque
			//On peut  faire une variale vaAttaquer et une variable cible
			this.attaque(ennemi); //Qui declenche l'anim d'attaque
			*/
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
