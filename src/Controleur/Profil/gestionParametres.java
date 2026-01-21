package Controleur.Profil;

import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modele.Preferences;
import javax.swing.JOptionPane;

import modele.Session;
import modele.dao.PreferencesDao;
import modele.dao.UtilisateurDao;
import vue.Profil.pageParam;
import vue.connexion.pageConnexionPrincipale;

public class gestionParametres implements ActionListener {

	private pageParam vue;
	private PreferencesDao daoPrefs;

	public gestionParametres(pageParam vue) {
		this.vue = vue;
		this.daoPrefs = new PreferencesDao();

		// Charger les préférences si un utilisateur est connecté
		if (Session.utilisateurConnecte != null) {
			long id = Session.utilisateurConnecte.getId();
			Preferences prefs = this.daoPrefs.findById(id);

			if (prefs != null) {
				this.vue.setModeSombre(prefs.isModeSombre());
				this.vue.setNotifEmail(prefs.isNotifEmail());
				this.vue.setRappelLoyer(prefs.isRappelLoyer());
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		switch (cmd) {

		case "FERMER_PARAMETRES":
			this.vue.dispose();
			break;

		case "ENREGISTRER_PARAMETRES":
			if (Session.utilisateurConnecte != null) {
				// Création de l'objet avec TON modèle Preferences
				Preferences p = new Preferences(Session.utilisateurConnecte.getId(), this.vue.isModeSombre(),
						this.vue.isNotifEmail(), this.vue.isRappelLoyer());
				this.daoPrefs.save(p);
				JOptionPane.showMessageDialog(this.vue, "Préférences sauvegardées !");
			}
			this.vue.dispose();
			break;

		case "DECONNEXION":
			int reponse = JOptionPane.showConfirmDialog(this.vue, "Voulez-vous vraiment vous déconnecter ?",
					"Déconnexion", JOptionPane.YES_NO_OPTION);

			if (reponse == JOptionPane.YES_OPTION) {
				// On vide la session
				Session.utilisateurConnecte = null;

				// On ouvre la page de connexion
				pageConnexionPrincipale login = new pageConnexionPrincipale();
				login.setLocationRelativeTo(null);
				login.setVisible(true);

				// On ferme toutes les autres fenêtres
				for (Window window : Window.getWindows()) {
					if (window != login) {
						window.dispose();
					}
				}
			}
			break;

		case "SUPPRIMER_COMPTE":
			int reponseSup = JOptionPane.showConfirmDialog(this.vue,
					"ATTENTION : Cette action est irréversible.\nVoulez-vous vraiment supprimer votre compte ?",
					"Suppression de compte", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

			if (reponseSup == JOptionPane.YES_OPTION) {
				if (Session.utilisateurConnecte != null) {
					UtilisateurDao daoUser = new UtilisateurDao();
					boolean succes = daoUser.delete(Session.utilisateurConnecte.getId());

					if (succes) {
						JOptionPane.showMessageDialog(this.vue, "Compte supprimé avec succès. Au revoir !");

						// Déconnexion forcée et retour à l'accueil
						Session.utilisateurConnecte = null;
						pageConnexionPrincipale login = new pageConnexionPrincipale();
						login.setVisible(true);

						for (Window window : Window.getWindows()) {
							if (window != login) {
								window.dispose();
							}
						}
					} else {
						JOptionPane.showMessageDialog(this.vue, "Erreur lors de la suppression.", "Erreur",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			break;

		default:
			break;
		}
	}
}