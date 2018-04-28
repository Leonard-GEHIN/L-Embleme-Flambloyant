
@SuppressWarnings("serial")
public class ErreurNom extends Exception{
	private int indiceCharErreur = 0;
	private char charErreur = 0;
	private int longueur = 0;
	
	public ErreurNom(int indiceCharErreur, char charErreur) {
		this.indiceCharErreur = indiceCharErreur;
		this.charErreur = charErreur;
	}
	
	public ErreurNom(int longueur) {
		this.longueur = longueur;
	}

	public String recupererMessageErreur(){
		String messageErreur = "";
		
		if( 0 < longueur && longueur < 3) {
		//Le nom n'est pas assez long
			messageErreur = "Le nom doit comporter au moins trois lettres.";
		}
		else if( 10 < longueur) {
		//Le nom est trop long
			messageErreur = "Le nom doit comporter moins de dix lettres.";
		}
		else{
		//Le nom comporte un caractere qui n'est pas une lettre
			messageErreur = "Le caractere " + this.charErreur + " numero " + (this.indiceCharErreur+1) + " n'est pas une lettre."
							+ "\nVeillez recommencer.";
		}
		
		return messageErreur;
	}
}
