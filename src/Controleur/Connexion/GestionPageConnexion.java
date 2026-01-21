package Controleur.Connexion;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JOptionPane;

import modele.Session;
import modele.Utilisateur;
import modele.dao.UtilisateurDao;
import vue.Principale.page_principale;
import vue.connexion.pageConnexion;
import vue.connexion.pageConnexionPrincipale;
import vue.connexion.pageImpossibeConnexion;
import vue.connexion.pageSignIn;

// Controleur pour la fenetre de gestion de connection
public class GestionPageConnexion implements ActionListener, WindowListener {

	private final pageConnexion vue;

	public GestionPageConnexion(pageConnexion vue) {
		this.vue = vue;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		switch (cmd) {
		case "CREER_COMPTE":
			this.vue.dispose();
			pageSignIn fen = new pageSignIn();
			fen.setVisible(true);
			break;

		case "AIDE_CONNEXION":
			this.vue.dispose();
			pageImpossibeConnexion fen1 = new pageImpossibeConnexion();
			fen1.setVisible(true);
			break;

		case "SE_CONNECTER":
			String identifiant = this.vue.getIdentifiant();
			String motDePasse = this.vue.getMotDePasse();

			if (identifiant.isEmpty() || motDePasse.isEmpty()) {
				JOptionPane.showMessageDialog(this.vue, "Veuillez remplir tous les champs.", "Attention",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			UtilisateurDao dao = new UtilisateurDao();
			Utilisateur userTrouve = dao.login(identifiant, motDePasse);

			if (userTrouve != null) {
				Session.utilisateurConnecte = userTrouve;

				// 1. On prépare la nouvelle fenêtre
				try {
					page_principale fen2 = new page_principale();
					fen2.setVisible(true);

					// 2. Si tout s'est bien passé, on ferme l'ancienne
					this.vue.dispose();

				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this.vue,
							"Erreur critique au chargement de l'accueil : " + ex.getMessage(), "Erreur",
							JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(this.vue, "Identifiant ou mot de passe incorrect.", "Erreur de connexion",
						JOptionPane.ERROR_MESSAGE);
			}
			break;

		default:
			break;
		}
	}

	// Pour quand on ferme une page doc ou autre, retourne oage principal
	@Override
	public void windowClosing(WindowEvent e) {
		// Gère la croix rouge
		this.vue.dispose();
		pageConnexionPrincipale principale = new pageConnexionPrincipale();
		principale.setVisible(true);
	}

	// Ne pas enlever, méthode abstraite
	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}
}