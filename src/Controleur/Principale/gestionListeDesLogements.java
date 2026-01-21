package Controleur.Principale;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import Controleur.refacto.NavigationHelper;
import modele.Batiment;
import modele.Logement;
import modele.dao.BatimentDao;
import modele.dao.LogementDao;
import modele.dao.UtOracleDataSource;
import vue.Document.pageDocumentsLogement;
import vue.Principale.pageListeDesLogements;
import vue.Principale.pageListeDesLogements.LogementItem;
import vue.Saisie.pageSaisieLogement;

/**
 * Contrôleur pour la gestion de la liste des logements.
 */
public class gestionListeDesLogements implements ActionListener, MouseListener {

	private final pageListeDesLogements vue;

	public gestionListeDesLogements(pageListeDesLogements vue) {
		this.vue = vue;

		this.vue.getListeLogements().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				handleDoubleClick(e);
			}
		});
	}

	/**
	 * Charge la liste des logements depuis la BDD.
	 */
	public void chargerDonneesLogementsDepuisBDD() {
		DefaultListModel<LogementItem> modele = this.vue.getModeleLogements();
		modele.clear();


		try {
			UtOracleDataSource.creerAcces();

			try (LogementDao logementDao = new LogementDao();
				 BatimentDao batDao = new BatimentDao()) {

				List<Logement> listeLogements = logementDao.findAll();
				List<Batiment> listeBatiments = batDao.findAll();

				Map<Long, Batiment> mapBatiments = new HashMap<>();
				for (Batiment b : listeBatiments) {
					mapBatiments.put(b.getIdBatimentLong(), b);
				}

				for (Logement log : listeLogements) {
					String adresseAffichee = construireAdresseComplete(log, mapBatiments);
					modele.addElement(new LogementItem(log, adresseAffichee));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this.vue, 
				"Erreur chargement logements : " + e.getMessage(), 
				"Erreur BDD",
				JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Construit l'adresse complète d'un logement
	 */
	private String construireAdresseComplete(Logement log, Map<Long, Batiment> mapBatiments) {
		if (log.getIdBatiment() == null) {
			return "Bâtiment non assigné";
		}

		Batiment bat = mapBatiments.get(log.getIdBatiment());
		String base = (bat != null) ? bat.getAdresse() : "Bâtiment inconnu";
		String complement = log.getAdresseComplementaire();

		if (complement != null && !complement.trim().isEmpty()) {
			return base + ", " + complement.trim();
		}
		return base;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		switch (cmd) {
		case "AJOUTER_LOGEMENT":
			ajouterLogement();
			break;
		case "MODIFIER_LOGEMENT":
			modifierLogement();
			break;
		case "SUPPRIMER_LOGEMENT":
			supprimerLogement();
			break;
		default:
			NavigationHelper.handleNavigation(this.vue, cmd);
			break;
		}
	}

	/**
	 * Ajoute un nouveau logement
	 */
	private void ajouterLogement() {
		pageSaisieLogement dlg = new pageSaisieLogement();
		dlg.setLocationRelativeTo(this.vue);
		dlg.setVisible(true);

		Logement nouveau = dlg.getLogementCree();
		if (nouveau == null) {
			return;
		}


		try (LogementDao dao = new LogementDao()) {
			dao.create(nouveau);
			chargerDonneesLogementsDepuisBDD();
			JOptionPane.showMessageDialog(this.vue, "Logement ajouté avec succès !");
		} catch (SQLException ex) {
			gererErreurSQL(ex);
		}
	}

	/**
	 * Modifie un logement existant
	 */
	private void modifierLogement() {
		LogementItem selection = this.vue.getListeLogements().getSelectedValue();
		if (selection == null) {
			JOptionPane.showMessageDialog(this.vue, "Veuillez sélectionner un logement à modifier.");
			return;
		}

		Logement logExistant = selection.getLogement();

		pageSaisieLogement dlg = new pageSaisieLogement();
		dlg.remplirDepuis(logExistant);
		dlg.setLocationRelativeTo(this.vue);
		dlg.setVisible(true);

		Logement logModifie = dlg.getLogementCree();
		if (logModifie == null) {
			return;
		}

		logModifie.setIdLogement(logExistant.getIdLogementLong());


		try (LogementDao dao = new LogementDao()) {
			dao.update(logModifie);
			chargerDonneesLogementsDepuisBDD();
			JOptionPane.showMessageDialog(this.vue, "Logement modifié avec succès !");
		} catch (SQLException ex) {
			gererErreurSQL(ex);
		}
	}

	/**
	 * Supprime un logement
	 */
	private void supprimerLogement() {
		LogementItem selection = this.vue.getListeLogements().getSelectedValue();
		if (selection == null) {
			JOptionPane.showMessageDialog(this.vue, "Veuillez sélectionner un logement à supprimer.");
			return;
		}

		Logement log = selection.getLogement();

		if (log.isLoue()) {
			JOptionPane.showMessageDialog(this.vue,
					"Impossible de supprimer ce logement car il est actuellement loué.\n" +
					"Veuillez d'abord clôturer le bail.",
					"Action impossible", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int choix = JOptionPane.showConfirmDialog(this.vue,
				"Confirmez-vous la suppression du logement ID : " + log.getIdLogementLong() + " ?", 
				"Suppression",
				JOptionPane.YES_NO_OPTION);

		if (choix != JOptionPane.YES_OPTION) {
			return;
		}


		try (LogementDao dao = new LogementDao()) {
			dao.delete(log);
			chargerDonneesLogementsDepuisBDD();
			JOptionPane.showMessageDialog(this.vue, "Logement supprimé.");
		} catch (SQLException ex) {
			gererErreurSQL(ex);
		}
	}

	/**
	 * Gère les erreurs SQL courantes
	 */
	private void gererErreurSQL(SQLException ex) {
		String msg = ex.getMessage();
		if (msg.contains("ORA-00001") || msg.contains("unique constraint")) {
			JOptionPane.showMessageDialog(this.vue, 
				"Cet ID ou Numéro Fiscal existe déjà.", 
				"Doublon",
				JOptionPane.ERROR_MESSAGE);
		} else if (msg.contains("ORA-02292") || msg.contains("integrity constraint")) {
			JOptionPane.showMessageDialog(this.vue, 
				"Suppression impossible : ce logement est lié à d'autres données.",
				"Erreur Intégrité", 
				JOptionPane.ERROR_MESSAGE);
		} else {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this.vue, 
				"Erreur technique : " + msg, 
				"Erreur", 
				JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Gère le double-clic sur un élément de la liste
	 */
	private void handleDoubleClick(MouseEvent e) {
		if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
			JList<LogementItem> liste = this.vue.getListeLogements();
			int index = liste.locationToIndex(e.getPoint());

			if (index >= 0 && liste.getCellBounds(index, index).contains(e.getPoint())) {
				LogementItem item = this.vue.getModeleLogements().getElementAt(index);

				pageDocumentsLogement dlg = new pageDocumentsLogement(item.getLogement());
				dlg.setLocationRelativeTo(this.vue);
				dlg.setVisible(true);
			}
		}
	}
	@Override
    public void mouseClicked(MouseEvent e) {
        // Double-clic pour modifier
        if (e.getClickCount() == 2) {
            // Appeler la méthode modifier si elle existe
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
