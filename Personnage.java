import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.Timer;

/*
 * La classe Personnage contient tous les attributs et methodes qui serait en doublon dans les classes fille.
 * L'utilisation de classe abstraite permet de factoriser le code. On peut aussi utiliser des methodes abstraite, qui seront obligatoirement implementer dans les classes fille.
 * Ces methodes abstraites permettent d'avoir une unicite entre les classes filles.
 */
public abstract class Personnage extends ObjetAffichable implements ActionListener{
	protected int DELAY = 40;
	protected final int tempsParcourUneCase = 300; //Duree en miliseconde pour la traverse d'une case
	protected Timer timer = new Timer(DELAY,this); // timer servant a mettre a jour les animations des personnages

	protected int ID; //ID utilise pour aue le joueur differencie les personnages (Indexe a 1)
	protected static int nombrePersonnage = 1;
	protected double defense, pointsDeVie, attaque;
	protected String nom;
	protected boolean tourTerminer = false, estAllie, estMort = false;
	
	//Utilise pour mouvement et animation
	protected double distanceAPourcourir = 0, multiplicateurVitesse = 1, tempsAnimation = 0;
	protected int directionMouvement = 0, compteurSkin = Methode.nombreAlea(0, 2);
	protected int caseX, caseY,  tickAnimationUtilise = 0, etapeAnimationCombat = 0;
	protected int offsetMouvementX = 0, offsetMouvementY = 0; //Decalage en pixel de l'image. Utilise pour les animations
	protected int nouvelleCaseX = -1, nouvelleCaseY = -1; //Stock les prochaine valeur de posiiton du personnage durant l'animation
	protected double vitesseX, vitesseY; // en pixel/ms
	protected boolean animationCombat = false, estEnMouvement = false, attendDeselectionOuAttaque = false, victoire = false;
	
	
	//Constructeur utiliser par les classes enfants
	public Personnage(boolean personnageJoueur) {
		//Position initiale
		estAllie = personnageJoueur;
		int caseATrouve = estAllie ? 1 : 2; //Identifie la case d'apparition a chercher dans la carte
		
		boolean positionTrouve = false;
		int i = 0, j = 0;
		int[][] carte = Carte.getCarte();
		while(i < carte[0].length && !positionTrouve) {
			while (j < carte.length && !positionTrouve) {
				if(carte[j][i] == caseATrouve) {
				//Une case valide est trouve.
					positionTrouve = true; //Permet de quitter la recherche
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
		
		//Generation des stat du personnage
		int pointsTotal = Methode.nombreAlea(50, 55);
		int pointsRestant = pointsTotal;

		int nombreStatRestanteACalcule = 3;
		this.attaque = generationStat(pointsTotal, pointsRestant, nombreStatRestanteACalcule, getRatioAttaque());
		pointsRestant -= this.attaque;
		nombreStatRestanteACalcule--;
		this.defense = generationStat(pointsTotal, pointsRestant, nombreStatRestanteACalcule, getRatioDefence());
		pointsRestant -= this.defense;
		nombreStatRestanteACalcule--;
		this.pointsDeVie = generationStat(pointsTotal, pointsRestant, nombreStatRestanteACalcule, getRatioPointsDeVie());
		pointsRestant -= this.pointsDeVie;
		nombreStatRestanteACalcule--;
	}

	
/*
 * Methodes abstraite
 */

	//Tous les mutateurs et accesseurs servent a manipuler les attributs des classes filles non presentes dans la classe Personnage
	//Ces attributs sont dans les classes filles car devait etre differentes pour chacunes de ces classes
	protected abstract ImageIcon getImageVictoire();
	protected abstract ImageIcon[] getImageDebout();
	protected abstract ImageIcon[][] getImageMouvement();
	protected abstract void setImageVictoire(ImageIcon image);
	protected abstract void setImageDebout(ImageIcon[] image);
	protected abstract void setImageMouvement(ImageIcon[][] image);
	public abstract double getAttaque(Personnage cible);
	public abstract double getDefense(Personnage cible);
	public abstract String getClasse();
	public abstract String[] getTabGenerationNom();

	protected abstract double getRatioAttaque();
	protected abstract double getRatioPointsDeVie();
	protected abstract double getRatioDefence();
	
/*
 * Methode de generation
 */

	//Donne un nom au personnage en fonction de sa classe
	public String genererNom() {
		return  this.getTabGenerationNom()[Methode.nombreAlea(0, 3)];
	}
	
	
	public int generationStat(int statTotal, int statRestant, int nombreStatRestanteACalcule, double ratio) {
		int stat;
		
		if( nombreStatRestanteACalcule > 1) {
			int statMoyenne = (int) (statTotal * ratio);
			stat = Methode.nombreAlea(statMoyenne*0.85, statMoyenne*1.15);		
		}
		else //Quand il ne reste qu'une stat a calcule, on met tous les points restant dedans
			stat = statRestant;
		
		return stat;
	}
	
/*
 * Methode utile au combat
 */

	public void attaque(Personnage cible) {
	// L'attaquant attaque et la cible riposte, la riposte est un peu moins efficace
		//Calcul des degats
		double degatAttaquant = this.getDegat(cible);
		double degatCible = cible.getDegat(this);
		
		//Debut du combat
		cible.setPointsDeVie(Methode.minorerParZero( cible.getPointsDeVie() - degatAttaquant ));
		
		if((int)(cible.getPointsDeVie()) == 0) {
		//La cible a 0 points de vie, elle est morte
			cible.meurt();
		}
		else {
		//La cible va contre attaquer
			degatCible *= 0.8; //La riposte est moins efficace que l'attaque
			this.setPointsDeVie(Methode.minorerParZero( this.getPointsDeVie() - degatCible ));
			
			if((int)(this.getPointsDeVie()) == 0) {
			//Le personnage est mort
				this.meurt();
			}
		}
		
		//Animation d'attaque
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
		String typeCible = cible.getClasse();
		if(this.getClasse().equals("Epee")) {
			if(typeCible.equals("Hache"))
				multiplicateur = 1.2;
			else if(typeCible.equals("Lance"))
				multiplicateur = 0.8;
		}
		else if(this.getClasse().equals("Hache")) {
			if(typeCible.equals("Lance"))
				multiplicateur = 1.2;
			else if(typeCible.equals("Epee"))
				multiplicateur = 0.8;
		}
		else if(this.getClasse().equals("Lance")) {
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
	
	public ImageIcon getImage() {
	//Calcul de l'image a utilise sur le moment
		ImageIcon image = null;
		if(estEnMouvement) //On utilise la meme image pour attaquer et pour se deplacer
			image = getImageMouvement()[this.directionMouvement][this.compteurSkin];
		else if(attendDeselectionOuAttaque || victoire)
			image = getImageVictoire();
		else
			image = getImageDebout()[this.compteurSkin];
		
		return image;
	}
	
	
	public void update() {
		if(estEnMouvement)
			compteurSkin = ( compteurSkin + 1 ) % this.getImageMouvement()[0].length;
		else
			compteurSkin = ( compteurSkin + 1 ) % this.getImageDebout().length;
	}
	
	
	public static void chargerClasse(String classe) {
	//Charge les images des personnages
		//perso sert a acceder aux image de la classe voulu
		Personnage perso = null;
		switch(classe.charAt(0)) {
		case 'E':
			perso = new Epee(false);
			break;
		case 'L':
			perso = new Lance(false);
			break;
		case 'H':
			perso = new Hache(false);
			break;
		}
		
		perso.setImageVictoire(new ImageIcon(Board.racine + classe + "/victoire.png"));
		
		//Image debout
		ImageIcon[] imageDebout = new ImageIcon[3];
		for (int i = 0; i < imageDebout.length; i++) {
			imageDebout[i] = new ImageIcon(Board.racine + classe + "/debout "+ i + ".png");
		}
		perso.setImageDebout(imageDebout);
		
		//Image de mouvement
		ImageIcon[][] imageMouvement = new ImageIcon[4][4];
		String[] ordreImage = {"droite", "haut", "gauche", "bas"};
		for (int j = 0; j < imageMouvement.length; j++) {
			for (int i = 0; i < imageMouvement[j].length; i++) {
				imageMouvement[j][i] = new ImageIcon(Board.racine + classe + "/" + ordreImage[j] + " "+ i + ".png");
			}	
		}
		
		perso.setImageMouvement(imageMouvement);
	}
	
	public boolean equals(Personnage personnageTest) {
	//Test si l'objet d'instance est egale a l'objetTest
	//Renvoie true si les deux objets sont les memes
	//Si le nom et les trois caracterisitiques sont identiques, alors les deux objets sont les memes
		boolean estIdentique = true;
		
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
	//Renvoie true si le personnage se situe sur la case enre en parametre
		boolean retour = false;
		if(this.caseX == x && this.caseY == y) {
			retour = true;
		}
		return retour;
	}
	
	
	//Initialise le parcours de carte
	public void caseJouable(Intelligence ennemi) {
		int[][] carte = Carte.getCarte();
		int profondeur = 3; //Profondeur de recursivite
		
		int x = this.caseX;
		int y = this.caseY;
		carte[y][x] = 10;  //On indique que la case est deja visite (Son poid sera trop grand pour la traverser)
		
		//Le test sur les x et y pour ne pas sortir de la matrice carte[][] sont effectue dans la methode recursive
		this.caseJouableRecursif(x - 1, y, profondeur, carte, ennemi);
		this.caseJouableRecursif(x + 1, y, profondeur, carte, ennemi);
		this.caseJouableRecursif(x, y + 1, profondeur, carte, ennemi);
		this.caseJouableRecursif(x, y - 1, profondeur, carte, ennemi);
	}
	
	
	//Parcours la carte de maniere recursive
	//Rempli l'arrayList de caseJouable de la classe Case
	protected void caseJouableRecursif(int x, int y, int profondeur, int[][] carte, Intelligence ennemi ) {
		if(x >= 0 && y >= 0 && x < carte[0].length && y < carte.length
				&& !ennemi.caseEstRempliParPersonnage(x, y) ) {
		//Les cases ou des allies se situent sont traversables, donc pas besoin de faire ce test
			
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
			}

			if(profondeurConsomee == 0 )System.out.println("Case inconnue");
			profondeur -= profondeurConsomee;
			if(profondeur > 0) {
				//Si on a assez de point pour aller dans la case, on poursuit le parcours
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
	//Renvoie true si le personnage peut attaquer un des personage ennemi
		boolean attaquePossible = false;
		
		//Test sur chaque personnage ennemi
		for (Personnage cible : ciblePossible.getPersonnages())
			if(peutAttaquer(cible))
				attaquePossible = true;
		
		return attaquePossible;
	}
	

	public boolean peutAttaquer(Personnage cible) {
	//Renvoie true si un personnage est assez proche pour attaquer la cible
		boolean attaquePossible = false;
		if(this.distance(cible) <= 1)
			attaquePossible = true;
		return attaquePossible;
	}
	
	public double distance(Personnage perso) {
	//Renvoie la distance entre deux personnages
		return Methode.distance(this.caseX, this.caseY, perso.getCaseX(), perso.getCaseY());
	}

	
	//Active le debut du tour du personnage
	public void debutTour() {
		this.tourTerminer = false;
	}

	
	public void terminerTour() {
		this.tourTerminer = true;
	}
	
	
	public void victoire() {
		this.victoire = true;
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
			ImageIcon image = this.getImage();

			//Changement de la police
			int taillePoliceY = (int)(Application.SCALE*4);
			Font font = new Font("Monospaced", Font.PLAIN, taillePoliceY);
			g2d.setColor(Color.WHITE);
			g2d.setFont(font);	
			
			//Affiche les images et le numero du personnage
			//les variables offsetMouvement sert a animer les personnages
			g2d.drawImage(image.getImage(),
					(int)(sc*(16*caseX)+offsetMouvementX-25), (int)(sc*(16*caseY)+offsetMouvementY - image.getIconHeight()-17),
					(int)(sc*image.getIconWidth()), (int)(sc*image.getIconWidth()),
					board);
			g2d.drawString(""+this.ID,
					(int)( sc*( 16*( caseX+1 ) )-taillePoliceY ),
					(int)( sc*( 16*( caseY ) ) + taillePoliceY ) );

			//Affiche les points de vie
			g2d.setColor(new Color(255, 255, 255, 160));
			g2d.fillRect((int)( sc*( 16*( caseX +1)) - taillePoliceY*1.5),
					(int)( sc*( 16*( caseY + 1)) - taillePoliceY*1.1),
					(int)(taillePoliceY*1.3),
					(int)(taillePoliceY*0.95) );
			g2d.setColor(new Color(150, 0, 150));
			g2d.drawString(""+(int)(this.pointsDeVie),
					(int)( sc*( 16*( caseX+1 ) )- taillePoliceY*1.5 ),
					(int)( sc*( 16*( caseY+1) ) - taillePoliceY*0.3 ) );
		}
	}


	//Affiche les informations sur les personnages
	//Renvoie la taille maximal des infomations sur l'axe X, en caractere.
	//Cette taille sert a placer corectement les informations du prochain personnage.
	public int dessinerInformation(Board board, Graphics2D g2d, int offsetXEnCase, int offsetYEnPixel) {
		String[] informationListe = new String[5]; //Nom, PV, attaque, defense
		
		//Construction des informations a afficher
		informationListe[0] = "|" + this.getClasse() + " " + this.ID;
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

	//Premiere methode d'initialisation de deplacement
	public void deplacer(int nouvelleCaseX, int nouvelleCaseY) {
		//La nouvelle case a ete tire un tableau de case valide, pas besoin de faire de test de validite
		this.nouvelleCaseX = nouvelleCaseX;
		this.nouvelleCaseY = nouvelleCaseY;

		initialiserAnimatiom(this.caseX, this.caseY, this.nouvelleCaseX, this.nouvelleCaseY, "Deplacement");
		this.terminerTour(); //Termine le tour apres avoir initier l'animation 
	}
	
	
	public void initialiserAnimatiom(int xIni, int yIni, int xDestination, int yDestination, String typeAnimation) {
		//Calcul du vecteur de distance
		int vecteurDeplacementX = xDestination - xIni;
		int vecteurDeplacementY = yDestination - yIni;
		double multiplicateurDistance = 1; //ratio qui ajuste facilement la distance final a parcourir.
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
		try {
			rapportVecteur = vecteurDeplacementX / vecteurDeplacementY;
		} catch (ArithmeticException e) {
			//Si on ne se deplace pas en Y, on a une division par 0
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
		this.estEnMouvement = true; // Active le deplacement du personnage
		timer.start(); //Enclenche le timer pour calculer les offset de l'animation
	}


	@Override
	public void actionPerformed(ActionEvent e) {
	//actualise l'animation du joueur
		boolean finAnimation = false;
		if(this.estEnMouvement) {
			this.offsetMouvementX += this.vitesseX*this.DELAY;
			this.offsetMouvementY += this.vitesseY*this.DELAY;
			
			if((int)(this.tempsAnimation / this.DELAY) < this.tickAnimationUtilise) {
			//Active la fin de l'animation
				if(this.animationCombat) {
				//On verifie l'etape de l'animation
				//En combat, l'animation est un aller sur l'annemi attaquer et un retour sur la case initiale
					if(this.etapeAnimationCombat == 1) {
					//La 2nd etape est termine, l'animation prend fin
						finAnimation = true;
					}
					else {
					//On passe a la 2nd etape de l'animation, on reinitialise quelques variables
						//Inversion de la vitesse
						this.vitesseX *= -1;
						this.vitesseY *= -1;
						
						//Reinitialisation des compteur
						this.etapeAnimationCombat = 1;
						this.tickAnimationUtilise = 0;
					}
				}
				else {
				//C'est une animation normale de deplacement, elle prend fin
					finAnimation = true;
				}
			}
			
			if(this.tickAnimationUtilise % (int) (160 / DELAY) == 0) {
			//Change l'indice de sprite utilise pour l'affichage
			//Il y a (160 / DELAY) tick entre chaque changement de srpite. ce qui correspond a un chanement d'indice toutes les 160 secondes
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
			
			//Reinitialisation de tous les attrinut servant uniquement a l'animation
			this.tempsAnimation = 0;
			this.distanceAPourcourir = 0;
			this.vitesseX = 0;
			this.vitesseY = 0;
			this.offsetMouvementX = 0;
			this.offsetMouvementY = 0;
			this.compteurSkin = 0;
			this.directionMouvement = 0;
			this.tickAnimationUtilise = 0;
			this.nouvelleCaseX = -1;
			this.nouvelleCaseY = -1;
			this.timer.stop(); //On arrete le timer des aue possible pour ne pas ralentir le jeu
			this.estEnMouvement = false;
			this.animationCombat = false;
			Board.animationEnCours = false; //Le jeu peut reprendre

			Case.genererCarte(Board.joueur, Board.ennemi, -1);
			
			if(IntelligenceArtificiel.personnageEnnemiPeutAttaquer) {
			//Si un ennemi a attaquer, son attaque se fait a la fin de l'animation
				this.attaque(Board.joueur.getPersonnages(IntelligenceArtificiel.personnageJoueurCible));
				IntelligenceArtificiel.personnageAAttaquer(); //termine le tour du personnage ennemi
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
}
