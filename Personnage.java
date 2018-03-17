import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.Timer;

public abstract class Personnage extends ObjetAffichable implements ActionListener{
	protected int DELAY = 40;
	protected final int tempsParcourUneCase = 300; //Duree en miliseconde pour la traverse d'une case
	protected Timer timer = new Timer(DELAY,this); // timer servant a mettre a jour les deplacement des personnages

	protected int ID;
	protected static int nombrePersonnage = 1;
	protected double defense, pointsDeVie, attaque;
	protected String nom;
	protected boolean tourTerminer = false, estAllie, estMort = false;
	
	//Utilise pour mouvement et animation
	protected double distanceAPourcourir = 0, multiplicateurVitesse = 1, tempsAnimation = 0;
	protected int directionMouvement = 0, compteurSkin = Methode.nombreAlea(0, 3);
	protected int caseX, caseY, offsetMouvementX = 0, offsetMouvementY = 0, tickAnimationUtilise = 0, etapeAnimationCombat = 0;
	protected int nouvelleCaseX = -1, nouvelleCaseY = -1; //Stock les prochaine valeur du personnage durant l'animation
	protected double vitesseX, vitesseY; // en px/ms
	protected boolean animationCombat = false, estEnMouvement = false, attendDeselectionOuAttaque = false,
						victoire = false;

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
		this.ID = nombrePersonnage;
		nombrePersonnage++;
	}
	

	
/*
 * Methodes abstraite
 */
	
	public abstract double getAttaque(Personnage cible);
	public abstract double getDefense(Personnage cible);
	
	
/*
 * Methode de generation
 */

	//Donne un nom au personnage en fonction de sa classe
	public String genererNom() {
		return  tabGenerationNom[Methode.nombreAlea(0, 3)];
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
	
/*
 * Methode utile au combat
 */

	public void attaque(Personnage cible) {
	//attaquant attaque et la cible riposte, la riposte est un peu moins efficace
		//Calcul des degats
		double degatAttaquant = this.getDegat(cible);
		double degatCible = cible.getDegat(this);
		
		//Debut du combat
		cible.setPointsDeVie(Methode.minorerParZero( cible.getPointsDeVie() - degatAttaquant ));
		
		if((int)(cible.getPointsDeVie()) == 0) {
		//La cible est morte
			cible.meurt();
		}
		else {
		//La cible va contre attaquer
			degatCible *= 0.8;
			this.setPointsDeVie(Methode.minorerParZero( this.getPointsDeVie() - degatCible ));
			
			if((int)(this.getPointsDeVie()) == 0) {
				this.meurt();
			}
		}
		
		//Animation
		this.initialiserAnimatiom(this.caseX, this.caseY,
								  cible.getCaseX(), cible.getCaseY(), "Combat");
		this.terminerTour();
	}
	
	
	public double getDegat(Personnage cible) {
		double multiplicateur = this.calcBonusArme(cible);
		return  Methode.minorerParZero(this.getAttaque(null) - cible.getDefense(null)) * multiplicateur;
	}
	
	
	public void meurt() {
		this.estMort = true;

		//On enleve le personnage de l'equipe de son intelligence
		Intelligence maitre = this.estAllie ? Board.joueur : Board.ennemi;
		maitre.retirerPersonnage(this);
	}
	
	
	public double calcBonusArme(Personnage cible) {
	//Calcul l'efficacite de l'attaque
		double multiplicateur = 1; //Si les deux classes sont identiques
		String typeCible = classe;
		if(classe.equals("Epee")) {
			if(typeCible.equals("Hache"))
				multiplicateur = 1.2;
			else if(typeCible.equals("Lance"))
				multiplicateur = 0.8;
		}
		else if(classe.equals("Hache")) {
			if(typeCible.equals("Lance"))
				multiplicateur = 1.2;
			else if(typeCible.equals("Epee"))
				multiplicateur = 0.8;
		}
		else if(classe.equals("Lance")) {
			if(typeCible.equals("Epee"))
				multiplicateur = 1.2;
			else if(typeCible.equals("Hache"))
				multiplicateur = 0.8;
		}
		
		return multiplicateur;
	}
	
	
/*
 * Methodes utile a d'autre clase et / ou d'autre methodes
 */
	
	public void victoire() {
		this.victoire = true;
		System.out.println("salut");
	}
	
	public void terminerTour() {
		this.tourTerminer = true;
	}
	
	public boolean equals(Personnage personnageTest) {
		boolean estIdentique = true;
		/*
		 * protected double defence, pointsDeVie, attaque;
		 * protected String nom;
		 */
		if(this.getDefense(null) != personnageTest.getDefense(null))
			estIdentique = false;

		if(this.getAttaque(null) != personnageTest.getAttaque(null))
			estIdentique = false;

		if(this.getPointsDeVie() != personnageTest.getPointsDeVie())
			estIdentique = false;

		if( this.getNom().equals( personnageTest.getNom() ) )
			estIdentique = false;
		
		return estIdentique;
	}
	
	
	public boolean estPresent(int x, int y) {
		boolean retour = false;
		if(this.caseX == x && this.caseY == y) {
			retour = true;
		}
		return retour;
	}
	
	
	public void caseJouable(Intelligence ennemi) {
		int[][] carte = Carte.getCarte();
		int profondeur = 3;
		
		int x = this.caseX;
		int y = this.caseY;
		carte[y][x] = 10;
		this.caseJouableRecursif(x - 1, y, profondeur, carte, ennemi);
		this.caseJouableRecursif(x + 1, y, profondeur, carte, ennemi);
		this.caseJouableRecursif(x, y + 1, profondeur, carte, ennemi);
		this.caseJouableRecursif(x, y - 1, profondeur, carte, ennemi);
	}
	
	
	protected void caseJouableRecursif(int x, int y, int profondeur, int[][] carte, Intelligence ennemi ) {
		if(x >= 0 && y >= 0 && x < carte[0].length && y < carte.length
				&& !ennemi.caseEstRempliParPersonnage(x, y) ) {
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
			}

			if(profondeurConsomee == 0 )System.out.println("Case inconnue");
			profondeur -= profondeurConsomee;
			if(profondeur > 0) {
				//Si on a assez de point pour aller dans la case
				carte[y][x] = 10; //On valide la case
				
				int [] temptab = {x, y};
				Case.ajouterValeur(temptab);
				this.caseJouableRecursif(x - 1, y, profondeur, carte, ennemi);
				this.caseJouableRecursif(x + 1, y, profondeur, carte, ennemi);
				this.caseJouableRecursif(x, y + 1, profondeur, carte, ennemi);
				this.caseJouableRecursif(x, y - 1, profondeur, carte, ennemi);
			}
		}
	}


	public boolean peutAttaquer(Intelligence ciblePossible) {
		boolean attaquePossible = false;
		
		for (Personnage cible : ciblePossible.getPersonnages())
			if(peutAttaquer(cible))
				attaquePossible = true;		
		
		return attaquePossible;
	}
	

	public boolean peutAttaquer(Personnage cible) {
		boolean attaquePossible = false;
		if(this.distance(cible) <= 1)
			attaquePossible = true;
		return attaquePossible;
	}
	
	
	public double distance(Personnage perso) {
		return Methode.distance(this.caseX, this.caseY, perso.getCaseX(), perso.getCaseY());
	}

	
	//Active le debut du tour du personnage
	public void debutTour() {
		this.tourTerminer = false;
	}
	
	
	public void peutAttaquerApresDeplacement() {
		this.attendDeselectionOuAttaque = true;
	}
	
	
	public void deselectionner() {
		this.attendDeselectionOuAttaque = false;
	}

	
/*
 * Methode d'affichage et d'animation
 */
	
	@Override
	public void dessiner(Board board, Graphics2D g2d) {
		if(!this.estMort) {
			double sc = Application.SCALE; //Variable pour reduire la methode d'affichage plus bas dans la methode
			ImageIcon image;
			//Calcul de l'image
			if(estEnMouvement)
				image = imageMouvement[this.directionMouvement][this.compteurSkin];
			else if(attendDeselectionOuAttaque || victoire)
				image = imageVictoire;
			else
				image = imageDebout[this.compteurSkin];
	
			//Changement de la police
			int taillePoliceY = (int)(Application.SCALE*4);
			Font font = new Font("Monospaced", Font.PLAIN, taillePoliceY);
			g2d.setColor(Color.WHITE);
			g2d.setFont(font);
			
			//Affiche les images et le numero du personnage
			//les variables offsetMouvement sert a animer les personnages
			g2d.drawImage(image.getImage(),
					(int)(sc*(16*caseX)-16+offsetMouvementX), (int)(sc*(16*caseY)-16+offsetMouvementY)+1,
					(int)(sc*31), (int)(sc*31),
					board);
			g2d.drawString(""+this.ID,
					(int)( sc*( 16*( caseX+1 ) )-taillePoliceY ),
					(int)( sc*( 16*( caseY ) ) + taillePoliceY ) );
		}
	}


	public int dessinerInformation(Board board, Graphics2D g2d, int offsetXEnCase, int offsetYEnPixel) {
		String[] informationListe = new String[5]; //Nom, PV, attaque, defense
		//Construction des informations a afficher
		informationListe[0] = "|" + classe + " " + this.ID;
		informationListe[1] = "|" + this.nom;
		informationListe[2] = "|PdV:" + (int)(this.getPointsDeVie());
		informationListe[3] = "|Atk:" + (int)(this.getAttaque(null));
		informationListe[4] = "|Def:" + (int)(this.getDefense(null));
		
		//Recherche de la taille max et on affiche les informations
		int longueurMax = 0;
		for (int i = 0; i < informationListe.length; i++) {
			if(informationListe[i].length() > longueurMax) {
				longueurMax = informationListe[i].length();
			}
			g2d.drawString(informationListe[i], (int)(offsetXEnCase*Board.tailleCaractereX),
												offsetYEnPixel+Board.tailleCaractereY*i);
		}
		
		return longueurMax;
	}
	
	
	public void update() {
		if(estEnMouvement)
			compteurSkin = ( compteurSkin + 1 ) % imageMouvement[0].length;
		else
			compteurSkin = ( compteurSkin + 1 ) % imageDebout.length;
	}


	public void deplacer(int nouvelleCaseX, int nouvelleCaseY) {
		this.nouvelleCaseX = nouvelleCaseX;
		this.nouvelleCaseY = nouvelleCaseY;

		initialiserAnimatiom(this.caseX, this.caseY, this.nouvelleCaseX, this.nouvelleCaseY, "Deplacement");
		this.terminerTour();
	}
	
	
	public void initialiserAnimatiom(int xIni, int yIni, int xDestination, int yDestination, String typeAnimation) {
		int vecteurDeplacementX = xDestination - xIni;
		int vecteurDeplacementY = yDestination - yIni;
		double multiplicateurDistance = 1;
		if(typeAnimation.equals("Combat")) {
			this.multiplicateurVitesse = 3;
			multiplicateurDistance = 0.6;
			this.animationCombat = true;
		}
		else if(typeAnimation.equals("Deplacement")) {
			this.multiplicateurVitesse = 1;
			multiplicateurDistance = 1;
		}
		
		//Calcul de la direction du mouvement
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
		
		//Calcul de la vitesse
		this.distanceAPourcourir = Math.sqrt( Math.pow(vecteurDeplacementX,2)
			  + Math.pow(vecteurDeplacementY,2) );
	
		this.tempsAnimation = this.distanceAPourcourir*tempsParcourUneCase
												/ this.multiplicateurVitesse; // en miliseconde
		double distanceAPourcourirPixelX = vecteurDeplacementX * 16*Application.SCALE * multiplicateurDistance;
		double distanceAPourcourirPixelY = vecteurDeplacementY * 16*Application.SCALE * multiplicateurDistance;
		this.vitesseX = distanceAPourcourirPixelX / tempsAnimation;
		this.vitesseY = distanceAPourcourirPixelY / tempsAnimation;
		
		Board.animationEnCours = true;
		this.estEnMouvement = true;
		timer.start();
	}


	@Override
	public void actionPerformed(ActionEvent e) { //actualise l'animation du joueur
		boolean finAnimation = false;
		if(this.estEnMouvement) {
			this.offsetMouvementX += this.vitesseX*this.DELAY;
			this.offsetMouvementY += this.vitesseY*this.DELAY;
			
			if((int)(this.tempsAnimation / this.DELAY) < this.tickAnimationUtilise) {
			//Active la fin de l'animation
				if(this.animationCombat) {
				//On verifie l'etape de l'animation
					if(this.etapeAnimationCombat == 1) {
					//La 2nd etape est termine, l'animation prend fin
						finAnimation = true;
					}
					else {
					//On passe a la 2nd etape de l'animation, on reinitialise quelques variables
						this.vitesseX *= -1;
						this.vitesseY *= -1;
						this.etapeAnimationCombat = 1;
						this.tickAnimationUtilise = 0;
					}
				}
				else {
				//C'est une animation normale, elle prend fin
					finAnimation = true;
				}
			}
			
			if(this.tickAnimationUtilise%(int)(160 / DELAY) == 0) {
				this.compteurSkin = (this.compteurSkin+1)%4;
			}
			
			this.tickAnimationUtilise++;
		}
		
		if(finAnimation) {
		//Variable a reinitialiser a la fin d'une animation
			this.attendDeselectionOuAttaque = false;
			
			if( !(nouvelleCaseX == -1 && nouvelleCaseY == -1) ) {
			//Si il y a eu deplacement, on met a jour les positions
				this.caseX = this.nouvelleCaseX;
				this.caseY = this.nouvelleCaseY;
			}
			
			if(this.estEnMouvement && !this.animationCombat && this.estAllie) {
			//On test si le joueur peut attaquer apres son deplacement
				Board.personnagePeutAttaquerApresDeplacement();
			}
			
			if(animationCombat && this.estAllie) {
			//Si le joueur attaque, on deselectionne son personnage
				Board.deselectionnePersonnage();
			}
			
			
			this.tempsAnimation = 0;
			this.distanceAPourcourir = 0;
			this.vitesseX = 0;
			this.vitesseY = 0;
			this.offsetMouvementX = 0;
			this.offsetMouvementY = 0;
			this.compteurSkin = 0;
			this.directionMouvement = 0;
			this.estEnMouvement = false;
			this.tickAnimationUtilise = 0;
			this.nouvelleCaseX = -1;
			this.nouvelleCaseY = -1;
			this.timer.stop();
			this.animationCombat = false;
			Board.animationEnCours = false; //Le jeu peut reprendre

			Case.genererCarte(Board.joueur, Board.ennemi, -1);
			
			if(IntelligenceArtificiel.personnageEnnemiPeutAttaquer) {
				this.attaque(Board.joueur.getPersonnages(IntelligenceArtificiel.personnageJoueurCible));
				IntelligenceArtificiel.personnageAAttaquer();
			}
		}
	}


	/**
	 * @return the caseX
	 */
	public int getCaseX() {
		return caseX;
	}


	/**
	 * @return the caseY
	 */
	public int getCaseY() {
		return caseY;
	}


	/**
	 * @return the tourTerminer
	 */
	public boolean isTourTerminer() {
		return tourTerminer;
	}


	/**
	 * @return the estEnMouvement
	 */
	public boolean isEstEnMouvement() {
		return estEnMouvement;
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
	 * @return the nom
	 */
	public String getNom() {
		return nom;
	}


	/**
	 * @return the classe
	 */
	public static String getClasse() {
		return classe;
	}



	



}
