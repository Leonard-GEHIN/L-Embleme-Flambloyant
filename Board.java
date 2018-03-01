import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Board extends JPanel implements ActionListener, MouseListener {
	private Timer timer; // Sert à actualiser les positions des joueurs et ennemis
	private final int DELAY = 10; // Temps entre deux actualisation (en ms)

	//SOUND
	public Sound snd_loop = new Sound("");
	/*
	 * Chargement des images dans la mémoire
	 * On charge ici les images utilisées par plusieurs instances d'objet ou celle qui servent dans cette classe
	 */	
	private ImageIcon image_joueur = new ImageIcon("sprites/personnage/spr_right_1.png");
	
	protected Image image_joueur_final = image_joueur.getImage();
	
	public Board() {
		addKeyListener(new TAdapter()); //Active l'écoute des touches du clavier
		
		this.addMouseListener(this);
		
		setFocusable(true); //Permet de pouvoir mettre la fenêtre en premier-plan 
		setBackground(Color.WHITE);
		timer = new Timer(DELAY,this); 
		timer.start(); //Le timer démarre ici
		snd_loop.play(); //Lance la musique
		snd_loop.loop(); //Répète la musique lorsqu'elle est finie
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		doDrawing(g);
		Toolkit.getDefaultToolkit().sync(); //Nécessaire au fonctionnement de swing (obscur)
	}

	private void doDrawing(Graphics g){ //méthode appelée pour mettre à jour l'affichage
		Graphics2D g2d = (Graphics2D) g; //On cast g en graphics2D(bibliothèque Java) pour utiliser la méthode drawImage()
		//g2d.drawImage(Image, x(zéro en haut à gauche), y(zéro en haut à gauche), le "component" où afficher l'image)
		//nomDeImage = nomDeImageIcon.getImage();
	}

	
	public void actionPerformed(ActionEvent e){
	//Mise à jour periodique des positions et index d'animation des entités mouvantes
		
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
	
	public void mouseClicked(MouseEvent e) { //Evenement quand il y a un click
	    int x=e.getX();
	    int y=e.getY();
	    System.out.println(x+","+y);//these co-ords are relative to the component
	}

	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
}
