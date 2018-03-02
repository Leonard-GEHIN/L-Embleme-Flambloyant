public class Methode {
	public static int nombreAlea(int min, int max) {
		int retour = (int)(Math.random()*(max-min)) % max;
		return retour;
		//TODO execption si min > max
	}
}
