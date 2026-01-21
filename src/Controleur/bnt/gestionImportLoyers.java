package Controleur.bnt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import modele.dao.LoyerDao;
import vue.bnt.pageImportLoyers;

/**
 * Contrôleur pour l'import des loyers :  Gestion
 * des ressources (fermeture automatique des fichiers/connexions).  Gestion des
 * exceptions spécifique.
 */
public class gestionImportLoyers implements ActionListener {

	private pageImportLoyers vue;

	public gestionImportLoyers(pageImportLoyers vue) {
		this.vue = vue;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		switch (cmd) {
		case "CHOISIR_FICHIER":
			this.choisirFichier();
			break;
		case "IMPORTER":
			this.importerFichier();
			break;
		case "FERMER":
			this.vue.fermer();
			break;
		default:
			break;
		}
	}

	private void choisirFichier() {
		JFileChooser chooser = new JFileChooser();
		int retour = chooser.showOpenDialog(this.vue);
		if (retour == JFileChooser.APPROVE_OPTION) {
			File fichierSelectionne = chooser.getSelectedFile();
			this.vue.setFichierSelectionne(fichierSelectionne);
			this.vue.getTxtCheminFichier().setText(fichierSelectionne.getAbsolutePath());
			this.chargerPreviewCsv(fichierSelectionne);
		}
	}

	private void chargerPreviewCsv(File fichier) {
		DefaultTableModel model = (DefaultTableModel) this.vue.getTablePreview().getModel();
		model.setRowCount(0);

		if (fichier == null) {
			return;
		}


		try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
			String ligne;
			int nbLignes = 0;

			while ((ligne = br.readLine()) != null && nbLignes < 50) {
				if (ligne.trim().isEmpty()) {
					continue;
				}

				String cleanLigne = ligne.replace("\"", "");
				String[] parts = cleanLigne.split("[,;]");

				if (parts.length >= 5) {
					model.addRow(new Object[] { parts[0], parts[1], parts[2], parts[3], parts[4] });
				} else {
					model.addRow(new Object[] { "ERREUR FORMAT", "Colonnes: " + parts.length, ligne, "", "" });
				}
				nbLignes++;
			}
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this.vue, "Erreur lecture : " + ex.getMessage());
		}
	}

	private void importerFichier() {
		File fichier = this.vue.getFichierSelectionne();
		if (fichier == null) {
			JOptionPane.showMessageDialog(this.vue, "Veuillez d'abord sélectionner un fichier CSV.", "Info",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		LoyerDao dao = null;
		int succes = 0;
		int echecs = 0;
		int lignesIgnorees = 0;
		StringBuilder logs = new StringBuilder();

		// Bloc principal pour gérer la connexion DAO
		try {
			dao = new LoyerDao();


			try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {

				String ligne;
				int numLigne = 0;

				while ((ligne = br.readLine()) != null) {
					numLigne++;
					if (ligne.trim().isEmpty()) {
						continue;
					}

					String cleanLigne = ligne.replace("\"", "");

					// Ignorer l'en-tête
					if (cleanLigne.toLowerCase().startsWith("id") || cleanLigne.toLowerCase().startsWith("logement")) {
						continue;
					}

					String[] parts = cleanLigne.split("[,;]");

					if (parts.length < 5) {
						lignesIgnorees++;
						logs.append("Ligne ").append(numLigne).append(" ignorée (Format) : ").append(cleanLigne)
								.append("\n");
						continue;
					}

					try {
						long idLogement = Long.parseLong(parts[0].trim());
						long idLocataire = Long.parseLong(parts[1].trim());
						String date = parts[2].trim();
						double loyer = Double.parseDouble(parts[3].trim().replace(',', '.'));
						double provision = Double.parseDouble(parts[4].trim().replace(',', '.'));

						dao.importerPaiement(idLogement, idLocataire, date, loyer, provision);
						succes++;

					} catch (NumberFormatException nfe) {
						echecs++;
						logs.append("Ligne ").append(numLigne).append(" : Chiffre invalide\n");
					} catch (Exception ex) {
						echecs++;
						String msg = ex.getMessage();
						// Nettoyage erreur Oracle
						if (msg != null && msg.contains("ORA-20010")) {
							msg = msg.substring(msg.indexOf("ORA-20010") + 10);
							int end = msg.indexOf("ORA-");
							if (end != -1) {
								msg = msg.substring(0, end);
							}
						}
						logs.append("Ligne ").append(numLigne).append(" : ").append(msg).append("\n");
					}
				}
			} // Le fichier se ferme ICI automatiquement grâce au try()

			// Affichage des résultats
			this.afficherRapport(succes, echecs, lignesIgnorees, logs);

		} catch (IOException | SQLException e) {
			e.printStackTrace(); // Log console pour le développeur
			JOptionPane.showMessageDialog(this.vue, "Erreur technique : " + e.getMessage(), "Erreur Critique",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			// On ferme le DAO dans le finally pour être sûr à 100%
			if (dao != null) {
				try {
					dao.close();
				} catch (Exception e) {
					/* Ignorer fermeture échouée */ }
			}
		}
	} 

	private void afficherRapport(int succes, int echecs, int lignesIgnorees, StringBuilder logs) {
		String message = "Rapport d'import :\n" + "✅ Succès : " + succes + "\n" + "❌ Échecs BDD : " + echecs + "\n"
				+ "⚠️ Lignes ignorées : " + lignesIgnorees;

		if (echecs > 0 || lignesIgnorees > 0) {
			message += "\n\nDétails :\n" + (logs.length() > 600 ? logs.substring(0, 600) + "..." : logs.toString());
		}

		JOptionPane.showMessageDialog(this.vue, message, "Résultat Import",
				(echecs == 0 && lignesIgnorees == 0) ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

		if (succes > 0) {
			this.vue.fermer();
		}
	}
}