public class Methode {
	public static int nombreAlea(double min, double max) {
	//Renvoie un nombre entre min et max compris
		int retour = (int)(Math.random()*(max-min+1) + min);
		return retour;
	}
	
	public static double minorerParZero(double valeur) {
	//Retourne la valeur ou 0 si la valeur est negative
		return valeur > 0 ? valeur : 0;
	}
	
	public static double distance(double x1, double y1, double x2, double y2) {
	//Calcul la distance entre deux points
		return Math.sqrt( Math.pow( x1-x2, 2 ) + Math.pow( y1-y2, 2 ) );
	}
}
