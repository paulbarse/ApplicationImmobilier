package Controleur.Profil;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import modele.Session;
import modele.Utilisateur;
import modele.dao.UtilisateurDao;
import vue.Profil.pageCompte;

public class gestionPageCompte implements ActionListener {

	private pageCompte vue;
	private boolean enModeEdition = false;
	private UtilisateurDao utilisateurDAO;

	public gestionPageCompte(pageCompte vue) {
		this.vue = vue;

		this.utilisateurDAO = new UtilisateurDao();


		if (Session.utilisateurConnecte != null) {
			Utilisateur u = Session.utilisateurConnecte;
			this.vue.setNom(u.getNom());
			this.vue.setPrenom(u.getPrenom());
			this.vue.setEmail(u.getEmail());
			this.vue.setMotDePasse(u.getMotDePasse());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		switch (cmd) {
		case "FERMER_GESTION_COMPTE":
			this.vue.dispose();
			break;

		case "MODIFIER_COMPTE":
			if (this.enModeEdition) {

				Utilisateur u = Session.utilisateurConnecte;

				// Mise à jour de l'objet utilisateur en mémoire avec les valeurs de la vue
				u.setNom(this.vue.getNom());
				u.setPrenom(this.vue.getPrenom());
				u.setEmail(this.vue.getEmail());
				u.setMotDePasse(this.vue.getMotDePasse());

				Utilisateur u1 = Session.utilisateurConnecte;

				if (u1.getId() <= 0) {
					JOptionPane.showMessageDialog(this.vue, "ID Utilisateur manquant pour la sauvegarde.",
							"Erreur Critique", JOptionPane.ERROR_MESSAGE);
					return;
				}

				// Mise à jour de l'objet utilisateur en mémoire


				// Appel au DAO pour la mise à jour en base de données
				boolean succes = this.utilisateurDAO.update(u1);

				if (succes) {
					JOptionPane.showMessageDialog(this.vue, "Modifications enregistrées en base de données !");
					this.enModeEdition = false; // Sortir du mode édition
				} else {
					JOptionPane.showMessageDialog(this.vue, "Erreur lors de l'enregistrement des modifications.",
							"Erreur de Sauvegarde", JOptionPane.ERROR_MESSAGE);

					return;
				}
			
			} else {
				this.enModeEdition = true; // Entrer en mode édition
			}
			this.vue.setEtatEdition(this.enModeEdition);
			break;

		default:
			break;
		}
	}
}