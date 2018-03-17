public class Methode {
	public static int nombreAlea(double min, double max) {
		int retour = (int)(Math.random()*(max-min) + min);
		return retour;
	}
	
	public static double minorerParZero(double x) {
		return x>0 ? x:0;
	}
	
	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt( Math.pow( x1-x2, 2 ) + Math.pow( y1-y2, 2 ) );
	}
}
