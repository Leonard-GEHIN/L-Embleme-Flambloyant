import java.awt.Color;
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
	private Timer timer; // Sert à actualiser les positions des joueurs et ennemis
	private final int DELAY = 350; // Temps entre deux actualisation (en ms)
	private static Carte carte;
	protected static Joueur joueur = new Joueur();
	//private Ennemi ennemi = new Ennemi();
	private boolean personnageSelectionner = false, tourEnnemi = false;
	private int indicePersonnageSelectionner;
	
	//SOUND
	//public Sound snd_loop = new Sound("");
	/*
	 * Chargement des images dans la mémoire
	 * On charge ici les images utilisées par plusieurs instances d'objet ou celle qui servent dans cette classe
	 */
	
	public Board() {
		addKeyListener(new TAdapter()); //Active l'écoute des touches du clavier
		
		this.addMouseListener(this);
		
		setFocusable(true); //Permet de pouvoir mettre la fenêtre en premier-plan 
		setBackground(Color.WHITE);
		timer = new Timer(DELAY,this); 
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
		Carte.afficherCarteTerminal();
		Case.genererCarte(joueur, -1);
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		doDrawing(g);
		Toolkit.getDefaultToolkit().sync(); //Nécessaire au fonctionnement de swing
	}

	//méthode appelée pour mettre à jour l'affichage
	private void doDrawing(Graphics g){ 
		Graphics2D g2d = (Graphics2D) g; //On cast g en graphics2D(bibliothèque Java) pour utiliser la méthode drawImage()
		carte.dessiner(this, g2d);
		(new Case()).dessiner(this, g2d);
		joueur.dessiner(this, g2d);
		//ennemi.dessiner(this, g2d); // l'ennemi s'affiche apres le joueur pour qu'il recouvre les casesJouables

		//g2d.drawString("Score: ", 4, 12);
		//nomDeImage = nomDeImageIcon.getImage();
	}

	public void actionPerformed(ActionEvent e){
	//Mise à jour periodique des positions et index d'animation des entités mouvantes
		joueur.update();
		//On calcul ici la grille ou le perso selectionner peut aller
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

	public void mouseClicked(MouseEvent e) { //Evenement quand il y a un click
	    int x=(int)(e.getX()/(Application.SCALE * 16)); //16 est la taille en pixel dune case avec un SCALE de 1
	    int y=(int)(e.getY()/(Application.SCALE * 16));
	    int caseCibleIndice = -1;
	    System.out.println(x+", "+y);

	    if(!tourEnnemi) {
	    //Les cliques durant le tour ennemi n'ont aucuns effets
	    	//Le joueur essaye de selectionner son personnage
	    	caseCibleIndice = joueur.selectionPersonnage(x, y);
	    	if(caseCibleIndice > -1) {
	    	//Si le joueur clique sur un de ses personnage
	    		indicePersonnageSelectionner = caseCibleIndice;
	    		Case.genererCarte(joueur/*, ennemi*/, indicePersonnageSelectionner);
	    		personnageSelectionner = true;
	    	}
	    	else if(personnageSelectionner && indicePersonnageSelectionner > -1) {
		    //Le joueur deplace son personnage, il ne vise pas un de ses personnage et il a un personnage selectionne
		    	
		    	//caseCibleIndice = ennemi.selectionPersonnage(x, y);
		    	caseCibleIndice = -1; //Simulation que l'ennemi n'est pas la
		    	if(caseCibleIndice == -1) {
		    	//Aucun ennemi n'est sur la case
		    		if(Case.estCaseValidePourDeplacement(x, y)) {
		    			//Le joueur se deplace
		    			Carte.deplacerPersonnage(x, y, joueur, indicePersonnageSelectionner);
		    			//Deselectionne ensuite le joueur
		    			indicePersonnageSelectionner = -1;
		    			personnageSelectionner = false;
		    		}
		    		else{
		    		//Si aucune case valide n'est selectionner
		    			indicePersonnageSelectionner = -1;
		    			personnageSelectionner = false;
		    			Case.genererCarte(joueur/*, ennemi*/, indicePersonnageSelectionner);
		    		}
		    	}
		    }
		    else {
		    	
		    }
	    }
	}

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



