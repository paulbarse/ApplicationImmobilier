package Controleur.Principale;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import Controleur.refacto.NavigationHelper;
import modele.Entreprise;
import modele.dao.EntrepriseDao;
import modele.dao.UtOracleDataSource;
import vue.Principale.pageListeDesEntreprises;
import vue.Principale.pageListeDesEntreprises.EntrepriseItem;
import vue.Saisie.pageSaisieEntreprise;

/**
 * Contrôleur pour la page liste des entreprises
 */
public class gestionListeDesEntreprises implements ActionListener {

    private final pageListeDesEntreprises vue;

    public gestionListeDesEntreprises(pageListeDesEntreprises vue) {
        this.vue = vue;
    }

    /**
     * Charge les données des entreprises depuis la BDD
     */
    public void chargerDonneesEntreprisesDepuisBDD() {
        DefaultListModel<EntrepriseItem> modele = this.vue.getModeleEntreprises();
        modele.clear();

        try {
            UtOracleDataSource.creerAcces();
            try (EntrepriseDao dao = new EntrepriseDao()) {
                dao.setConnection(UtOracleDataSource.getConnection());

                List<Entreprise> liste = dao.findAll();

                for (Entreprise e : liste) {
                    int nbFactures = dao.countFactures(e.getSiret());
                    modele.addElement(new EntrepriseItem(e, nbFactures));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.vue,
                    "Erreur lors du chargement des entreprises : " + e.getMessage(),
                    "Erreur BDD",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "AJOUTER_ENTREPRISE":
                ajouterEntreprise();
                break;

            case "MODIFIER_ENTREPRISE":
                modifierEntreprise();
                break;

            case "SUPPRIMER_ENTREPRISE":
                supprimerEntreprise();
                break;

            default:
                NavigationHelper.handleNavigation(this.vue, cmd);
                break;
        }
    }

    /**
     * Ouvre la page de saisie pour ajouter une entreprise
     */
    private void ajouterEntreprise() {
        pageSaisieEntreprise dlg = new pageSaisieEntreprise();
        dlg.setLocationRelativeTo(this.vue);
        dlg.setVisible(true);

        if (dlg.getEntrepriseCree() != null) {
            chargerDonneesEntreprisesDepuisBDD();
        }
    }

    /**
     * Ouvre la page de saisie pour modifier l'entreprise sélectionnée
     */
    private void modifierEntreprise() {
        JList<EntrepriseItem> liste = this.vue.getListeEntreprises();
        int index = liste.getSelectedIndex();

        if (index < 0) {
            JOptionPane.showMessageDialog(this.vue,
                    "Veuillez sélectionner une entreprise à modifier.",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        EntrepriseItem item = this.vue.getModeleEntreprises().getElementAt(index);
        Entreprise entreprise = item.getEntreprise();

        pageSaisieEntreprise dlg = new pageSaisieEntreprise();
        dlg.remplirDepuis(entreprise);
        dlg.setLocationRelativeTo(this.vue);
        dlg.setVisible(true);

        if (dlg.getEntrepriseCree() != null) {
            chargerDonneesEntreprisesDepuisBDD();
        }
    }

    /**
     * Supprime l'entreprise sélectionnée
     */
    private void supprimerEntreprise() {
        JList<EntrepriseItem> liste = this.vue.getListeEntreprises();
        int index = liste.getSelectedIndex();

        if (index < 0) {
            JOptionPane.showMessageDialog(this.vue,
                    "Veuillez sélectionner une entreprise à supprimer.",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        EntrepriseItem item = this.vue.getModeleEntreprises().getElementAt(index);
        Entreprise entreprise = item.getEntreprise();

        // Vérifier si des factures sont liées
        if (item.getNbFactures() > 0) {
            JOptionPane.showMessageDialog(this.vue,
                    "Impossible de supprimer cette entreprise :\n" +
                    "Elle a " + item.getNbFactures() + " facture(s) associée(s).",
                    "Suppression impossible",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirmation
        int confirmation = JOptionPane.showConfirmDialog(
                this.vue,
                "Voulez-vous vraiment supprimer l'entreprise :\n" +
                entreprise.getNomEntreprise() + " ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }


        try {
            UtOracleDataSource.creerAcces();
            try (EntrepriseDao dao = new EntrepriseDao()) {
                dao.setConnection(UtOracleDataSource.getConnection());
                dao.delete(entreprise);
            }

            JOptionPane.showMessageDialog(this.vue,
                    "Entreprise supprimée avec succès !",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

            chargerDonneesEntreprisesDepuisBDD();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this.vue,
                    "Erreur lors de la suppression :\n" + ex.getMessage(),
                    "Erreur BDD",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
