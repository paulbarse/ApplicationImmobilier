package modele;

public class Session {
	/**
	 * Cette variable statique est accessible depuis n'importe où dans l'appli. Elle
	 * contient l'utilisateur actuellement connecté. Si elle est null, personne
	 * n'est connecté.
	 */
	public static Utilisateur utilisateurConnecte = null;
}