public class Methode {
	public static int nombreAlea(double min, double max) {
		int retour = (int)(Math.random()*(max-min) + min);
		return retour;
		//TODO execption si min > max
	}
}
