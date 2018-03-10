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

import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Board extends JPanel implements ActionListener, MouseListener {
	//Attribut relative a l'affichage
	public static int tailleCaractereY = (int)(6*Application.SCALE);
	public static int tailleCaractereX = (int)(tailleCaractereY*0.6);
	
	//Attribut relative au element de jeu
	private static Carte carte;
	protected static Joueur joueur = new Joueur();
	protected static Ennemi ennemi = new Ennemi();
	private static boolean personnageSelectionner = false;
	private static boolean tourEnnemi = false;
	private boolean enJeu = false;
	private static boolean attenteSelectionCibleAttaque = false;
	public static boolean animationEnCours = false;
	private static int indicePersonnageSelectionner;
	
	//Attribut relative au temps
	private Timer timer; // Sert à actualiser les positions des joueurs et ennemis
	private final static int IMAGE_PAR_SECONDE_VOULU = 20; // Nombre d'image par seconde souhaite (60 = bonne qualite)
	private final static int DELAY_IMAGE = 1000 / IMAGE_PAR_SECONDE_VOULU; // Temps entre deux d'image (en ms)
	private final static int DELAY_UPDATE = 350; // Temps entre deux actualisation (en ms)
	private double tempsTemp = System.currentTimeMillis();
	private int nombreImageParSeconde = 0;
	private static int imagePasseSansUpdate = 0;
	
	//Attribut de son
	//public Sound snd_loop = new Sound("");
	
	
	public Board() {
		addKeyListener(new TAdapter()); //Active l'écoute des touches du clavier
		
		this.addMouseListener(this);
		
		setFocusable(true); //Permet de pouvoir mettre la fenêtre en premier-plan 
		setBackground(Color.WHITE);
		timer = new Timer(DELAY_IMAGE,this); 
		timer.start(); //Le timer démarre ici
		//SOUND
		//snd_loop.play(); //Lance la musique
		//snd_loop.loop(); //Répète la musique lorsqu'elle est finie

		//Initialise mes variables
		chargerClasse();
		carte = new Carte();
		joueur.ajouterPersonnage(new Epee(true));
		joueur.ajouterPersonnage(new Epee(true));
		joueur.ajouterPersonnage(new Epee(true));
		ennemi.ajouterPersonnage(new Epee(false));
		ennemi.ajouterPersonnage(new Epee(false));
		ennemi.ajouterPersonnage(new Epee(false));
		Carte.enleverCaseApparition();
		Carte.afficherCarteTerminal();
		Case.genererCarte(joueur, ennemi, -1);
		
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
		this.enJeu = true;
	}
	

	private static void dessinerInformation(Board board, Graphics2D g2d) {
		int offsetYEnPixel = Carte.getHauteurEnPixel() + tailleCaractereY; //La fin de la carte
		int offsetXEnCase = 0;
		g2d.setFont(new Font("Monospaced", Font.PLAIN, tailleCaractereY));
		g2d.setColor(Color.BLACK);
		
		offsetXEnCase = joueur.dessinerInformation(board, g2d, offsetXEnCase, offsetYEnPixel);
		offsetXEnCase += 5;
		offsetXEnCase = ennemi.dessinerInformation(board, g2d, offsetXEnCase, offsetYEnPixel);
	}


	public void actionPerformed(ActionEvent e){
	//Mise à jour periodique des positions et index d'animation des entités mouvantes
		if(enJeu) {
			miseAJourDesIndicesDImage();
			//Action de jeu
			echangerTour();
		}
		repaint(); //Affiche l'image
	}

	private class TAdapter extends KeyAdapter{ // Méthode qui s'active quand l'état d'une touche change
		@Override
		public void keyReleased(KeyEvent e){ //Action quand une touche est relachee
		}
		@Override
		public void keyPressed(KeyEvent e){ //Action quand une touche est pressee
		}
	}

	//Methode qui charge les attribut static
	private static void chargerClasse() {
		Case.chargerClasse();
		Epee.chargerClasse();
		//TODO ajout chargerClasse() Hache et Lance
	}

/*
 * Gestion de la souris et de l'avancement de la partie
 */
	public void mouseClicked(MouseEvent e) { //Evenement quand il y a un click
	    int x=(int)(e.getX()/(Application.SCALE * 16)); //16 est la taille en pixel dune case avec un SCALE de 1
	    int y=(int)(e.getY()/(Application.SCALE * 16));	    

    	int caseCibleIndiceJoueur = joueur.selectionPersonnage(x, y);
    	int caseCibleIndiceEnnemi = ennemi.selectionPersonnage(x, y);
    	System.out.println("ennemi: "+caseCibleIndiceEnnemi + " joueur:"+ caseCibleIndiceJoueur);
	    if(!tourEnnemi && !animationEnCours && enJeu) {
	    //Les cliques durant le tour ennemi n'ont aucuns effets
	    	if(caseCibleIndiceJoueur > -1) {
	    	//Le joueur selectionne un personnage
	    		selectionnePersonnage(caseCibleIndiceJoueur);
	    	}
	    	else if(personnageSelectionner && indicePersonnageSelectionner > -1) {
	    	//Le joueur a deja selectionner un personnage et souhaite l'utiliser
	    		if(caseCibleIndiceEnnemi > -1 && personnageSelectionnerPeutAttaquer(caseCibleIndiceEnnemi)) {
	    		//Le joueur cible et peut attaquer un ennemi
    				joueur.attaquePersonnage(indicePersonnageSelectionner, caseCibleIndiceEnnemi, ennemi);
    				deselectionnePersonnage();
    				attenteSelectionCibleAttaque = false;
	    		}
	    		else if(Case.estCaseValidePourDeplacement(x, y) && caseCibleIndiceEnnemi == -1) {
	    		//Le joueur deplace son personnage <=> ne vise pas d'ennemi mais une case jouable
	    			Carte.deplacerPersonnage(x, y, joueur, indicePersonnageSelectionner);
	    			joueur.getPersonnages().get(indicePersonnageSelectionner).terminerTour();
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
	//Methode appele apres le deplacement (A la fin de l'animation
		if(personnageSelectionnerPeutAttaquer()) {
			attenteSelectionCibleAttaque = true;
		}
		else {
			deselectionnePersonnage();
		}
	}
	
	
	public static boolean personnageSelectionnerPeutAttaquer() {
		return joueur.getPersonnages().get(indicePersonnageSelectionner).peutAttaquer(ennemi);
	}
	
	public static boolean personnageSelectionnerPeutAttaquer(int indice){
		boolean peutAttaquer = false;
		 if(indice >= 0)
			 peutAttaquer = joueur.getPersonnages().get(indicePersonnageSelectionner).
									peutAttaquer(ennemi.getPersonnages().get(indice));
		
		return peutAttaquer;
	}
	

	public static void deselectionnePersonnage() {
		indicePersonnageSelectionner = -1;
		personnageSelectionner = false;
		Case.genererCarte(joueur, ennemi, indicePersonnageSelectionner);
	}
	
	
	public static void selectionnePersonnage(int indicePersoSelectionner) {
		personnageSelectionner = true;
		attenteSelectionCibleAttaque = false;
		indicePersonnageSelectionner = indicePersoSelectionner;
		Case.genererCarte(joueur, ennemi, indicePersonnageSelectionner);
	}
	
	
	private static void echangerTour(){
		if(!animationEnCours && !attenteSelectionCibleAttaque) {
		//Echange les tour des intelligences
			if(joueur.ATerminerSonTour() && !tourEnnemi) {
				System.out.println("C'est maintenant le tour des ennemis");
				tourEnnemi = true;
				ennemi.debutTour();
			}
			else if(ennemi.ATerminerSonTour() && tourEnnemi) {
				tourEnnemi = false;
				joueur.debutTour();
			}
		}
	}
	
	
	public static void miseAJourDesIndicesDImage() {
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



