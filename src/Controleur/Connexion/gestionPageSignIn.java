package Controleur.Connexion;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Date;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import modele.Utilisateur;
import modele.dao.UtilisateurDao;
import vue.connexion.pageConnexion;
import vue.connexion.pageConnexionPrincipale;
import vue.connexion.pageSignIn;

//Controleur pour la fenetre de gestion pour s'inscrire

public class gestionPageSignIn implements ActionListener, WindowListener {

	private final pageSignIn vue;

	public gestionPageSignIn(pageSignIn vue) {
		this.vue = vue;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton item = (JButton) e.getSource();
		String texte = item.getText();

		switch (texte) {

		// Cas du bouton "Créer mon compte"
		case "Créer mon compte":

			// 1. Récupérer les données
			String nom = this.vue.getNom();
			String prenom = this.vue.getPrenom();
			String email = this.vue.getEmail();
			String mdp = this.vue.getPassword();
			Date dateNaiss = this.vue.getDateNaissance(); // Utilise notre super méthode

			//  Vérifications de base
			if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || mdp.isEmpty()) {
				JOptionPane.showMessageDialog(this.vue, "Veuillez remplir tous les champs obligatoires (*).", "Erreur",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			if (dateNaiss == null) {
				JOptionPane.showMessageDialog(this.vue, "Veuillez sélectionner une date de naissance valide.", "Erreur",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			//  Création de l'objet Modèle
			Utilisateur nouvelUser = new Utilisateur(nom, prenom, email, mdp, dateNaiss);

			// Envoi à la Base de Données
			UtilisateurDao dao = new UtilisateurDao();
			boolean succes = dao.create(nouvelUser);

			if (succes) {
				JOptionPane.showMessageDialog(this.vue, "Compte créé avec succès ! Connectez-vous.");

				// Fermer l'inscription et ouvrir la connexion
				this.vue.dispose();
				pageConnexion fen = new pageConnexion();
				fen.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(this.vue, "Erreur : Cet email est déjà utilisé ou problème technique.",
						"Erreur", JOptionPane.ERROR_MESSAGE);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {
		pageConnexionPrincipale principale = new pageConnexionPrincipale();
		principale.setVisible(true);
	}

	// Pareil, pas enlever
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