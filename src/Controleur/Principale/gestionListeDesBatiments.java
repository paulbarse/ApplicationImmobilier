package Controleur.Principale;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import Controleur.refacto.NavigationHelper;
import modele.Batiment;
import modele.dao.BatimentDao;
import modele.dao.UtOracleDataSource;
import vue.Document.pageDocumentsBatiments;
import vue.Principale.pageListeDesBatiments;
import vue.Principale.pageListeDesBatiments.BatimentItem;
import vue.Saisie.pageSaisieBatiment;

/**
 * Controleur Liste des Batiments 
 * 
 */
public class gestionListeDesBatiments implements ActionListener, MouseListener  {

	private final pageListeDesBatiments vue;

	public gestionListeDesBatiments(pageListeDesBatiments vue) {
		this.vue = vue;
		// Ajout du MouseListener via MouseAdapter pour éviter le code mort
		this.vue.getListeBatiments().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				handleDoubleClick(e);
			}
		});
	}

	// Chargement des bâtiments depuis la BDD et remplissage du modèle de la vue.
	public void chargerDonneesBatimentsDepuisBDD() {
		DefaultListModel<BatimentItem> modele = this.vue.getModeleBatiments();
		modele.clear();


		try {
			UtOracleDataSource.creerAcces();
			try (BatimentDao batDao = new BatimentDao()) {
				batDao.setConnection(UtOracleDataSource.getConnection());
				List<Batiment> liste = batDao.findAll();

				for (Batiment b : liste) {
					BatimentItem item = creerBatimentItem(b);
					modele.addElement(item);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this.vue, 
				"Erreur lors du chargement des batiments : " + e.getMessage(),
				"Erreur BDD", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Crée un BatimentItem à partir d'un Batiment
	 */
	private BatimentItem creerBatimentItem(Batiment b) {
		String id = b.getIdBatiment();
		String adresseComplete = b.getAdresse();
		String type = b.getTypeBatiment();
		String ville = b.getRue();
		String cp = String.valueOf(b.getCodePostal());

		Integer et = b.getNombreEtage();
		int nbEtages = (et == null) ? 0 : et;

		return new BatimentItem(id, adresseComplete, type, ville, cp, nbEtages);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		try {
			switch (cmd) {
			case "AJOUTER_BATIMENT":
				ajouterBatiment();
				break;

			case "MODIFIER_BATIMENT":
				modifierBatiment();
				break;

			case "SUPPRIMER_BATIMENT":
				supprimerBatiment();
				break;

			default:
				NavigationHelper.handleNavigation(this.vue, cmd);
				break;
			}
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			JOptionPane.showMessageDialog(this.vue,
				"Erreur : un champ numerique est invalide (ID, etages, code postal).");
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this.vue, "Erreur Base de donnees : " + ex.getMessage());
		}
	}

	/**
	 * Gère l'ajout d'un nouveau bâtiment
	 */
	private void ajouterBatiment() throws SQLException {
		pageSaisieBatiment dlg = new pageSaisieBatiment();
		dlg.setLocationRelativeTo(this.vue);
		dlg.setVisible(true);

		String[] res = dlg.getResultat();
		if (res == null) {
			return;
		}

		Batiment nouveauBatiment = creerBatimentDepuisResultat(res);
		if (nouveauBatiment == null) {
			return;
		}


		UtOracleDataSource.creerAcces();
		try (BatimentDao batDao = new BatimentDao()) {
			batDao.setConnection(UtOracleDataSource.getConnection());
			batDao.create(nouveauBatiment);
		}

		this.chargerDonneesBatimentsDepuisBDD();
	}

	/**
	 * Gère la modification d'un bâtiment existant
	 */
	private void modifierBatiment() throws SQLException {
		JList<BatimentItem> liste = this.vue.getListeBatiments();
		int index = liste.getSelectedIndex();
		if (index < 0) {
			JOptionPane.showMessageDialog(this.vue, "Veuillez selectionner un batiment a modifier.");
			return;
		}

		BatimentItem item = this.vue.getModeleBatiments().getElementAt(index);

		// Pré-remplissage dans le NOUVEL ORDRE
		String[] init = { 
			item.getIdBatiment(), 
			item.getAdresseComplete(), 
			String.valueOf(item.getNbEtages()),
			item.getType(), 
			item.getVille(),
			item.getCodePostal() 
		};

		pageSaisieBatiment dlg = new pageSaisieBatiment();
		dlg.remplirDepuis(init);
		dlg.setLocationRelativeTo(this.vue);
		dlg.setVisible(true);

		String[] res = dlg.getResultat();
		if (res == null) {
			return;
		}

		Batiment batimentModifie = creerBatimentDepuisResultat(res);
		if (batimentModifie == null) {
			return;
		}


		UtOracleDataSource.creerAcces();
		try (BatimentDao batDao = new BatimentDao()) {
			batDao.setConnection(UtOracleDataSource.getConnection());
			batDao.update(batimentModifie);
		}

		this.chargerDonneesBatimentsDepuisBDD();
	}

	/**
	 * Gère la suppression d'un bâtiment
	 */
	private void supprimerBatiment() throws SQLException {
		JList<BatimentItem> liste = this.vue.getListeBatiments();
		int index = liste.getSelectedIndex();
		if (index < 0) {
			JOptionPane.showMessageDialog(this.vue, "Veuillez selectionner un batiment a supprimer.");
			return;
		}

		int validation = JOptionPane.showConfirmDialog(this.vue, 
			"Voulez-vous vraiment supprimer ce batiment ?",
			"Confirmation", JOptionPane.YES_NO_OPTION);

		if (validation != JOptionPane.YES_OPTION) {
			return;
		}

		BatimentItem item = this.vue.getModeleBatiments().getElementAt(index);
		long id = Long.parseLong(item.getIdBatiment());
		Batiment aSupprimer = new Batiment(id, "", null, "", "", 0);


		UtOracleDataSource.creerAcces();
		try (BatimentDao batDao = new BatimentDao()) {
			batDao.setConnection(UtOracleDataSource.getConnection());
			batDao.delete(aSupprimer);
		}

		this.chargerDonneesBatimentsDepuisBDD();
	}

	/**
	 * Crée un objet Batiment à partir du résultat du dialogue de saisie
	 */
	private Batiment creerBatimentDepuisResultat(String[] res) {
		// res[0]=id, res[1]=adresse, res[2]=nbEtages, res[3]=type, res[4]=ville, res[5]=cp
		long id = Long.parseLong(res[0].trim());
		String adresse = res[1].trim();

		Integer nbEtages = null;
		String nbEtagesStr = (res[2] == null) ? "" : res[2].trim();
		if (!nbEtagesStr.isEmpty()) {
			nbEtages = Integer.parseInt(nbEtagesStr);
		}

		String type = (res[3] == null) ? "" : res[3].trim();
		String ville = (res[4] == null) ? "" : res[4].trim();

		// VALIDATION : Ville obligatoire
		if (ville.isEmpty()) {
			JOptionPane.showMessageDialog(this.vue, "La ville est obligatoire.", "Erreur de saisie",
					JOptionPane.WARNING_MESSAGE);
			return null;
		}

		int cp = Integer.parseInt(res[5].trim());

		// Le constructeur Batiment utilise toujours le parametre "rue"
		// mais il contient maintenant la ville
		return new Batiment(id, adresse, nbEtages, type, ville, cp);
	}

	/**
	 * Gère le double-clic sur un élément de la liste
	 */
	private void handleDoubleClick(MouseEvent e) {
		if (e.getClickCount() == 2 && javax.swing.SwingUtilities.isLeftMouseButton(e)) {
			JList<BatimentItem> liste = this.vue.getListeBatiments();
			int index = liste.locationToIndex(e.getPoint());
			if (index >= 0) {
				liste.setSelectedIndex(index);
				BatimentItem item = this.vue.getModeleBatiments().getElementAt(index);

				pageDocumentsBatiments dlgDocs = new pageDocumentsBatiments(item);
				dlgDocs.setLocationRelativeTo(this.vue);
				dlgDocs.setVisible(true);
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
