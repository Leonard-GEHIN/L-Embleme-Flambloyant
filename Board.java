import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JPanel;
import javax.swing.Timer;


@SuppressWarnings("serial")
public class Board extends JPanel implements ActionListener, MouseListener {
	//Attribut relative a l'affichage
	public static int tailleCaractereY = (int)(6*Application.SCALE);
	public static int tailleCaractereX = (int)(tailleCaractereY*0.6);
	public static final String racine = "Sprite/";

	//Attribut relative au element de jeu
	private static Carte carte;
	protected static Joueur joueur;
	protected static Ennemi ennemi = new Ennemi();
	private static boolean attendDeselectionOuAttaque = false, partieGagner, partieFinie = false;
	public static boolean personnageSelectionner = false,animationEnCours = false, tourEnnemi = false;
	private boolean enJeu = false;
	public static int indicePersonnageSelectionner = -1;

	//Attribut relative au temps
	private Timer timer; // Sert à actualiser les positions des joueurs et ennemis
	private final static int IMAGE_PAR_SECONDE_VOULU = 60; // Nombre d'image par seconde souhaite (60 = bonne qualite)
	private final static int DELAY_IMAGE = 1000 / IMAGE_PAR_SECONDE_VOULU; // Temps entre deux d'image (en ms)
	private final static int DELAY_UPDATE = 300; // Temps entre deux actualisation (en ms)
	private double tempsTemp = System.currentTimeMillis();
	private int nombreImageParSeconde = 0;
	private static int imagePasseSansUpdate = 0;
	
	
	public Board() {
		this.addKeyListener(new TAdapter()); //Active l'écoute des touches du clavier
		this.addMouseListener(this); //Active l'écoute des evenement de souris
		
		setFocusable(true); //Permet de pouvoir mettre la fenêtre en premier-plan 
		setBackground(new Color(210, 180, 140));
		timer = new Timer(DELAY_IMAGE,this); 
		timer.start(); //Le timer démarre ici

		//Initialise les attributs
		chargerClasse();
		carte = new Carte();
		creationJoueur();
		Carte.enleverCaseApparition();
		Case.genererCarte(joueur, ennemi, -1);
	}
	
	
	private static void creationJoueur() {
	//Recupere le nom du joueur et son equipe
		//Le nom
		boolean selectionNomFinie = false;
    	Scanner sc = new Scanner(System.in); //Scanner utilise pour recuperer les entrees claviers
    	String nom = "";
    	char charCourant;
    	System.out.println("Entrez votre nom."
    					+ "\n(Uniquement des lettres et entre 3 et 10 caracteres)");
    	while(!selectionNomFinie) {
    		selectionNomFinie = true;
    		try {
    			nom = sc.nextLine();
    			
    			//test de longueur du nom
    			if( !(3 <= nom.length() && nom.length() <= 10)) {
    				throw new ErreurNom(nom.length());
    			}
    			
    			//Test de presence de caracteres non desires dans le nom
    			for (int i = 0; i < nom.length(); i++) {
    				charCourant = nom.charAt(i);
    				if(!('a' <= charCourant && charCourant <= 'z' || 'A' <= charCourant && charCourant <= 'Z') ) {
    	    			throw new ErreurNom(i, charCourant);
    				}
    			}
    		} catch (ErreurNom e) {
    			System.out.println(e.recupererMessageErreur());
    			selectionNomFinie = false;
    		}
    	}
    	Board.joueur = new Joueur(nom);

    	//L'equipe
    	System.out.println("Voulez-vous une equipe equilibre preparee ? (y/n)"
    					+ "\n(Un guerrier de chaque classe)");
    	char choix = sc.nextLine().charAt(0);
    	if(choix == 'y') {
    		Board.joueur.ajouterPersonnage(new Hache(true));
    		Board.joueur.ajouterPersonnage(new Epee(true));
    		Board.joueur.ajouterPersonnage(new Lance(true));
    	}
    	else {
    		int nombreGuerrierChoisi = 0, choixGuerrier;
    		String classe = "";
			System.out.println("Choississez vos trois guerriers parmis les trois classes suivante :");
    		while(nombreGuerrierChoisi < 3) {
    			System.out.println("-1 : Epeiste (Guerrier equilibre)\n"
	    			+ "-2 : Barbare (Guerrier ayant une plus grande defence)\n"
	    			+ "-3 : Halbardier (Guerrier une plus grande attaque)\n");
    			choixGuerrier = sc.nextInt();
    			
    			if(0 < choixGuerrier && choixGuerrier < 4) {
        			nombreGuerrierChoisi++;
    				switch(choixGuerrier) {
    				case 1:
    		    		Board.joueur.ajouterPersonnage(new Epee(true));
    		    		classe = "Epeiste";
    					break;
    				case 2:
    		    		Board.joueur.ajouterPersonnage(new Hache(true));
    		    		classe = "Barbare";
    					break;
    				case 3:
    		    		Board.joueur.ajouterPersonnage(new Lance(true));
    		    		classe = "Halbardier";
    					break;
    				}
    				System.out.println(classe + " ajoute avec succes.");
    			}
    			else {
    				System.out.println("Entrez clavier non comprise, veillez ressayer.");
    			}
    		}
    	}
    	
    	//Difficulte
    	int nbEnnemi = 3;
    	System.out.println("Choisissez votre difficulte."
    			+ "\nF - Facile"
    			+ "\nN - Normal"
    			+ "\nD - Difficile");
    	choix = sc.nextLine().charAt(0);
    	if(choix == 'f' || choix == 'F') {
    		nbEnnemi = 2;
    	}
    	else if(choix == 'n' || choix == 'N') {
    		nbEnnemi = 3;
    	}
    	else if(choix == 'd' || choix == 'D') {
    		nbEnnemi = 4;
    	}
    	
		creationEnnemi(nbEnnemi);
    	sc.close();
    }
	
	
	private static void creationEnnemi(int nbPersonnage) {
	//Genere les personnage de l'IA
		Personnage persoTemp = null;
		for (int i = 0; i < nbPersonnage; i++) {
			switch(Methode.nombreAlea(1, 3)) {
			case 1:
				persoTemp = new Epee(false);
				break;
			case 2:
				persoTemp = new Hache(false);
				break;
			case 3:
				persoTemp = new Lance(false);
				break;
			}
			
			ennemi.ajouterPersonnage(persoTemp);
		}
	}

	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		doDrawing(g);
		Toolkit.getDefaultToolkit().sync(); //Nécessaire au fonctionnement de swing
		
		//Calcul et affiche le nombre d'image par seconde (IPS)
		this.nombreImageParSeconde++;
		if(System.currentTimeMillis() - this.tempsTemp > 1000) {
		//Chaque seconde, on met a jour les IPS
			Application.ex.setTitle(Application.titreFenetre + " | IPS: " + this.nombreImageParSeconde);
			this.tempsTemp = System.currentTimeMillis();
			this.nombreImageParSeconde = 0;
		}
	}
	

	//méthode appelée pour mettre à jour l'affichage
	private void doDrawing(Graphics g){ 
		Graphics2D g2d = (Graphics2D) g; //On cast g en graphics2D(bibliothèque Java) pour utiliser la méthode drawImage()
		carte.dessiner(this, g2d);
		(new Case()).dessiner(this, g2d);
		joueur.dessiner(this, g2d);
		ennemi.dessiner(this, g2d);
		dessinerInformation(this, g2d);
		
		if(animationEnCours && personnageSelectionner) {
		//Si il y a une animation du joueur, on le redessine
			joueur.getPersonnages(indicePersonnageSelectionner).dessiner(this, g2d);
		}
		
		if(partieFinie) {
			dessinerFinPartie(this, g2d);
		}
		
		this.enJeu = true;
	}
	

	private static void dessinerInformation(Board board, Graphics2D g2d) {
	//Affiche les informations des personnage vivants
		//On utilise des offset en X et en Y pour que chaque information de personnage ne se chevauche pas
		int offsetYEnPixel = Carte.getHauteurEnPixel() + tailleCaractereY; //La fin de la carte
		int offsetXEnCase = 0;
		
		//Definission de la couleur et de la police du texte
		g2d.setFont(new Font("Monospaced", Font.PLAIN, tailleCaractereY));
		g2d.setColor(Color.BLACK);
		
		offsetXEnCase = joueur.dessinerInformation(board, g2d, offsetXEnCase, offsetYEnPixel);
		offsetXEnCase += 5;
		offsetXEnCase = ennemi.dessinerInformation(board, g2d, offsetXEnCase, offsetYEnPixel);
	}
	
	
	private static void dessinerFinPartie(Board board, Graphics2D g2d) {
	//Affiche la boite de dialogue avec le nessage de fin de partie
		int longueurMax = 0;
		int x, y, longueur, largeur,  epaisseurBord, posX, posY;
		double espace;
		ArrayList<String> messageCoupe = new ArrayList<String>(); //Contient chaque ligne du message de fin
		if(partieGagner) {
			messageCoupe.add("Bravo " + joueur.getNom() + ", vous avez");
			messageCoupe.add("vaincu l'equipe ennemie et");
			messageCoupe.add("recupere l'embleme flamboyant !");
		}
		else {
			messageCoupe.add("L'equipe ennemie s'empare");
			messageCoupe.add("de l'embleme flamboyant !");
			messageCoupe.add("Vous ferrez mieux la");
			messageCoupe.add("prochaine fois.");
		}
		
		//Dessine le carre qui sert de fond
		for (String message : messageCoupe) {	
			if(message.length() > longueurMax) {
				longueurMax = message.length();
			}
		}
		
		//Calcul des longueur et position du texte
		posX = 20;
		posY = 5;
		epaisseurBord = 5;
		espace = 0.5;
		x = (int) (Board.tailleCaractereX * (posX-espace));
		y = (int) (Board.tailleCaractereY * posY);
		longueur = (int) (Board.tailleCaractereX * (longueurMax + 3 + 2 * espace));
		largeur = (int) (Board.tailleCaractereY * (messageCoupe.size() + 0.4));

		//Borne exterieur
		g2d.setColor(new Color(97, 78, 26));
		g2d.fillRect(x-epaisseurBord, y-epaisseurBord, longueur+2*epaisseurBord, largeur+2*epaisseurBord);
		//Fond de la boite de dialogue
		g2d.setColor(Color.WHITE);
		g2d.fillRect(x, y, longueur, largeur);

		
		//Affichage du texte
		g2d.setColor(Color.BLACK);
		g2d.setFont(new Font("Monospaced", Font.PLAIN, tailleCaractereY));
		for (int i = 0; i < messageCoupe.size(); i++) {
			g2d.drawString(messageCoupe.get(i), Board.tailleCaractereX * posX, Board.tailleCaractereY * ( i + posY +1 ) );
		}		
	}


	public void actionPerformed(ActionEvent e){
	//Mise à jour periodique des positions et index d'animation des entités mouvantes
		if(enJeu) {
			miseAJourDesIndicesDImage();
			
			//Echange de tour et active l'IA si c'est le tour de l'ennemi
			if(!finPartie()) {
				echangerTour();
				if(tourEnnemi) {
					ennemi.tourEnnemi();
				}
			}
		}

		repaint(); //Affiche l'image
	}


	private boolean finPartie() {
	//Test si la partie est terminer et enclenche sa fin si besoin
		boolean partieFinie = false;
		if(!animationEnCours) {
			if(joueur.getPersonnages().size() == 0) {
			//Victoire de l'ennemi
				ennemi.victoire(); //Change l'image a afficher pour les personnage
				partieFinie = true;
				partieGagner = false;
			}
			if(ennemi.getPersonnages().size() == 0) {
			//Victoire du joueur
				joueur.victoire(); //Change l'image a afficher pour les personnage
				partieFinie = true;
				partieGagner = true;
			}

			if(partieFinie) {
				Board.partieFinie = true;
				Case.genererCarte(joueur, ennemi, indicePersonnageSelectionner);
				repaint();
			}
		}
		return partieFinie;
	}


	private class TAdapter extends KeyAdapter{ // Méthode qui s'active quand l'état d'une touche change
		@Override
		public void keyReleased(KeyEvent e){
		//Action quand une touche est relachee
			if(e.getKeyCode()==KeyEvent.VK_SPACE) {
				// Si le joueur tape sur la barre espace
				if(!tourEnnemi) {
					joueur.passerTour();
				}
			}
		}
	}

	private static void chargerClasse() {
	//Methode qui charge les attribut static
		Epee.chargerClasse();
		Lance.chargerClasse();
		Hache.chargerClasse();
	}

/*
 * Gestion de la souris et de l'avancement de la partie
 */
	public void mouseClicked(MouseEvent e) {
	//Evenement quand il y a un click
	    int x=(int)(e.getX()/(Application.SCALE * 16)); //16 est la taille en pixel dune case avec un SCALE de 1
	    int y=(int)(e.getY()/(Application.SCALE * 16));	    

    	int caseCibleIndiceJoueur = joueur.selectionPersonnageJouable(x, y);
    	int caseCibleIndiceEnnemi = ennemi.selectionIndicePersonnage(x, y);
    	if(!tourEnnemi && !animationEnCours && enJeu) {
	    //Les cliques durant le tour ennemi n'ont aucuns effets
	    	if(caseCibleIndiceJoueur > -1) {
	    	//Le joueur selectionne un personnage
	    		
	    		if(indicePersonnageSelectionner == caseCibleIndiceJoueur) {
	    		//Le joueur veut terminer le tour d'un personnage
	    			joueur.getPersonnages(caseCibleIndiceJoueur).terminerTour();
	    			deselectionnePersonnage();
	    		}
	    		else {
	    		//Le joueur veut selectionner un nouveau personnage
	    			if(personnageSelectionner) deselectionnePersonnage();
		    		selectionnePersonnage(caseCibleIndiceJoueur);
	    		}
	    	}
	    	else if(personnageSelectionner && indicePersonnageSelectionner > -1) {
	    	//Le joueur a deja selectionner un personnage et souhaite l'utiliser
	    		if(caseCibleIndiceEnnemi > -1 && personnageSelectionnerPeutAttaquer(caseCibleIndiceEnnemi)) {
	    		//Le joueur cible et peut attaquer un ennemi
    				joueur.attaquePersonnage(indicePersonnageSelectionner, caseCibleIndiceEnnemi, ennemi);
    				attendDeselectionOuAttaque = false;
	    		}
	    		else if(Case.estCaseValidePourDeplacement(x, y) && caseCibleIndiceEnnemi == -1 && caseCibleIndiceJoueur == -1) {
	    		//Le joueur deplace son personnage <=> ne vise pas d'ennemi ni un joueur mais une case jouable
	    		//Les Personnages sont traversables
	    			Carte.deplacerPersonnage(x, y, joueur, indicePersonnageSelectionner);
	    			joueur.getPersonnages(indicePersonnageSelectionner).terminerTour();
	    			//Le personnage est deselectionner via la fonction d'animation
	    		}
	    		else if(caseCibleIndiceEnnemi == -1 && caseCibleIndiceJoueur == -1) {
	    		//Le joueur Deselectionne son personnage en visant une case vide
	    			deselectionnePersonnage();
	    		}
	    	}
	    }
	}
	
	
	public static void personnagePeutAttaquerApresDeplacement() {
	//Methode appele apres le deplacement (A la fin de l'animation)
		if(personnageSelectionnerPeutAttaquer()) {
		//Active l'attente que le joueur attaque ou deselectionne
			attendDeselectionOuAttaque = true;
			joueur.getPersonnages(indicePersonnageSelectionner).peutAttaquerApresDeplacement();
		}
		else {
		//Le joueur ne peut pas ataquer avec son personnage, on le deselectionne donc
			deselectionnePersonnage();
		}
	}
	
	
	public static boolean personnageSelectionnerPeutAttaquer() {
	//Test si le personnage est capable d'attaquer un adversaire au corps a corps
		return joueur.getPersonnages(indicePersonnageSelectionner).peutAttaquer(ennemi);
	}
	
	public static boolean personnageSelectionnerPeutAttaquer(int indice){
	//Test si le personnage est capable d'attaquer un adversaire au corps a corps un personnage particulier
		boolean peutAttaquer = false;
		if(indice >= 0)
			 peutAttaquer = joueur.getPersonnages(indicePersonnageSelectionner)
									.peutAttaquer(ennemi.getPersonnages(indice));
		
		return peutAttaquer;
	}
	

	public static void deselectionnePersonnage() {
		joueur.getPersonnages(indicePersonnageSelectionner).deselectionner();
		indicePersonnageSelectionner = -1;
		personnageSelectionner = false;
		attendDeselectionOuAttaque = false;
		Case.genererCarte(joueur, ennemi, indicePersonnageSelectionner); //On recalcul la carte pour avoir la bonne couleur de case
	}
	
	
	public static void selectionnePersonnage(int indicePersoSelectionner) {
	//Selectionne le personnage voulu
		personnageSelectionner = true;
		attendDeselectionOuAttaque = false;
		indicePersonnageSelectionner = indicePersoSelectionner;
		Case.genererCarte(joueur, ennemi, indicePersonnageSelectionner);
	}
	
	
	private static void echangerTour(){
	//Echange les tour des intelligences
		if(!animationEnCours && !attendDeselectionOuAttaque) {
		//Si aucune action n'est en cours, on fait les test d'echange
			if(joueur.ATerminerSonTour() && !tourEnnemi) {
			//Termine le tour du joueur
				tourEnnemi = true;
				ennemi.debutTour();
				Case.genererCarte(joueur, ennemi, -1);
			}
			else if(ennemi.ATerminerSonTour() && tourEnnemi) {
				//Termine le tour de l'ennemi
				tourEnnemi = false;
				joueur.debutTour();
				Case.genererCarte(joueur, ennemi, -1);
			}
		}
	}
	
	
	public static void miseAJourDesIndicesDImage() {
	//Met a jour les indice d'image pour les personnage qui attendent
		imagePasseSansUpdate++;
		if(DELAY_UPDATE - DELAY_IMAGE*imagePasseSansUpdate  < 0){
			ennemi.update();
			joueur.update();
			imagePasseSansUpdate = 0;
		}
	}
	
	
/*
 * Methode implementer du listener de la souris
 */
	//Le listener de la souris etant un template, on doit override les methode abstract meme si elles sont vides.
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}

	/**
	 * @return the carte
	 */
	public Carte getCarte() {
		return carte;
	}
}



