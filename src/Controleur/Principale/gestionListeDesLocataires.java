package Controleur.Principale;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import Controleur.refacto.NavigationHelper;
import modele.Locataire;
import modele.dao.LocataireDao;
import modele.dao.UtOracleDataSource;
import vue.Document.pageDocumentsLocataire;
import vue.Principale.pageListeDesLocataires;
import vue.Principale.pageListeDesLocataires.LocataireItem;
import vue.Saisie.pageSaisieLocataire;

/**
 * Controleur pour la page liste des locataires
 */
public class gestionListeDesLocataires implements ActionListener {

    private final pageListeDesLocataires vue;
    
    // Filtre actuel : "TOUS", "PROSPECT", "ACTIF", "ANCIEN"
    private String filtreActuel = "TOUS";

    public gestionListeDesLocataires(pageListeDesLocataires vue) {
        this.vue = vue;
        
        // Ajouter le MouseListener pour le double-clic
        this.vue.getListeLocataires().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleDoubleClick(e);
            }
        });
    }

    public void chargerDonneesLocatairesDepuisBDD() {
        DefaultListModel<LocataireItem> modele = this.vue.getModeleLocataires();
        modele.clear();

        LocataireDao locataireDao = null;
        try {
            UtOracleDataSource.creerAcces();
            locataireDao = new LocataireDao();
            locataireDao.setConnection(UtOracleDataSource.getConnection());

            // Utilise la methode qui retourne le statut a 3 valeurs
            List<Object[]> listeAvecStatut = locataireDao.findAllAvecStatut();

            for (Object[] row : listeAvecStatut) {
                Locataire loc = (Locataire) row[0];
                String statut = (String) row[1];  // PROSPECT, ACTIF, ANCIEN
                String actuel = (String) row[2];  // Oui/Non (compatibilite)
                
                // Appliquer le filtre
                if (filtreActuel.equals("TOUS") || filtreActuel.equals(statut)) {
                    LocataireItem item = new LocataireItem(loc, statut, actuel);
                    modele.addElement(item);
                }
            }

            // Mettre a jour le compteur
            if (vue.getLblCompteur() != null) {
                vue.getLblCompteur().setText(modele.size() + " locataire(s)");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.vue,
                    "Erreur lors du chargement des locataires : " + e.getMessage(),
                    "Erreur BDD",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            if (locataireDao != null) {
                try { locataireDao.close(); } catch (SQLException ex) { /* ignore */ }
            }
        }
    }
    
    /**
     * Change le filtre et recharge les donnees
     */
    public void setFiltre(String filtre) {
        this.filtreActuel = filtre;
        chargerDonneesLocatairesDepuisBDD();
    }
    
    /**
     * Retourne le filtre actuel
     */
    public String getFiltreActuel() {
        return filtreActuel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "AJOUTER_LOCATAIRE":
                ajouterLocataire();
                break;
            case "MODIFIER_LOCATAIRE":
                modifierLocataire();
                break;
            case "SUPPRIMER_LOCATAIRE":
                supprimerLocataire();
                break;
            case "FILTRE_TOUS":
                setFiltre("TOUS");
                break;
            case "FILTRE_PROSPECTS":
                setFiltre("PROSPECT");
                break;
            case "FILTRE_ACTIFS":
                setFiltre("ACTIF");
                break;
            case "FILTRE_ANCIENS":
                setFiltre("ANCIEN");
                break;
            default:
                NavigationHelper.handleNavigation(this.vue, cmd);
                break;
        }
    }

    /**
     * Ouvre la page de saisie pour AJOUTER un nouveau locataire (prospect)
     */
    private void ajouterLocataire() {
        pageSaisieLocataire dlg = new pageSaisieLocataire();
        dlg.setLocationRelativeTo(this.vue);
        dlg.setVisible(true);

        if (dlg.getLocataireCree() != null) {
            chargerDonneesLocatairesDepuisBDD();
            
            JOptionPane.showMessageDialog(this.vue,
                "Locataire ajoute avec succes !\n\n" +
                "Statut : PROSPECT\n" +
                "(Deviendra ACTIF apres signature d'un bail)",
                "Succes",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Ouvre la page de saisie pour MODIFIER le locataire selectionne
     */
    private void modifierLocataire() {
        JList<LocataireItem> liste = this.vue.getListeLocataires();
        int index = liste.getSelectedIndex();

        if (index < 0) {
            JOptionPane.showMessageDialog(this.vue,
                    "Veuillez selectionner un locataire a modifier.",
                    "Aucune selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocataireItem item = this.vue.getModeleLocataires().getElementAt(index);
        Locataire locataire = item.getLocataire();

        if (locataire == null) {
            JOptionPane.showMessageDialog(this.vue,
                    "Impossible de modifier ce locataire.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        pageSaisieLocataire dlg = new pageSaisieLocataire();
        dlg.remplirDepuis(locataire);
        dlg.setLocationRelativeTo(this.vue);
        dlg.setVisible(true);

        if (dlg.getLocataireCree() != null) {
            chargerDonneesLocatairesDepuisBDD();
        }
    }

    /**
     * Supprime le locataire selectionne de la BDD
     */
    private void supprimerLocataire() {
        JList<LocataireItem> liste = this.vue.getListeLocataires();
        int index = liste.getSelectedIndex();

        if (index < 0) {
            JOptionPane.showMessageDialog(this.vue,
                    "Veuillez selectionner un locataire a supprimer.",
                    "Aucune selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocataireItem item = this.vue.getModeleLocataires().getElementAt(index);
        Locataire locataire = item.getLocataire();

        if (locataire == null) {
            JOptionPane.showMessageDialog(this.vue,
                    "Impossible de supprimer ce locataire.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verifier si le locataire est actif (a un bail en cours)
        if ("ACTIF".equals(item.getStatut())) {
            JOptionPane.showMessageDialog(this.vue,
                    "Impossible de supprimer ce locataire :\n" +
                    "Il a actuellement un bail en cours.\n\n" +
                    "Veuillez d'abord cloturer le bail.",
                    "Suppression impossible",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Message de confirmation adapte selon le statut
        String messageConfirm = buildConfirmationMessage(locataire, item);

        int confirmation = JOptionPane.showConfirmDialog(
                this.vue,
                messageConfirm,
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        // Suppression avec try-finally
        LocataireDao dao = null;
        try {
            UtOracleDataSource.creerAcces();
            dao = new LocataireDao();
            dao.setConnection(UtOracleDataSource.getConnection());
            dao.delete(locataire);

            JOptionPane.showMessageDialog(this.vue,
                    "Locataire supprime avec succes !",
                    "Succes",
                    JOptionPane.INFORMATION_MESSAGE);

            chargerDonneesLocatairesDepuisBDD();

        } catch (SQLException ex) {
            handleDeleteError(ex);
        } finally {

            if (dao != null) {
                try { dao.close(); } catch (SQLException ex) { /* ignore */ }
            }
        }
    }

    /**
     * Construit le message de confirmation de suppression
     */
    private String buildConfirmationMessage(Locataire locataire, LocataireItem item) {
        StringBuilder messageConfirm = new StringBuilder();
        messageConfirm.append("Voulez-vous vraiment supprimer le locataire :\n")
                     .append(locataire.getNomComplet())
                     .append(" (ID: ")
                     .append(locataire.getIdLocataire())
                     .append(")\n\nStatut actuel : ")
                     .append(item.getStatut());
        
        if ("ANCIEN".equals(item.getStatut())) {
            messageConfirm.append("\n\nATTENTION : Cet ancien locataire a un historique de baux.");
        }
        
        return messageConfirm.toString();
    }

    /**
     * Gere les erreurs de suppression
     */
    private void handleDeleteError(SQLException ex) {
        ex.printStackTrace();
        
        String message = ex.getMessage();
        
        if (message != null && (message.contains("ORA-02292") || message.contains("integrity constraint"))) {
            JOptionPane.showMessageDialog(this.vue,
                    "Impossible de supprimer ce locataire car il est lie a un bail.\n" +
                    "Veuillez d'abord cloturer le bail associe.",
                    "Suppression impossible",
                    JOptionPane.ERROR_MESSAGE);
        } else if (message != null && message.contains("bail actif")) {
            JOptionPane.showMessageDialog(this.vue,
                    "Impossible de supprimer ce locataire :\n" + message,
                    "Suppression impossible",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this.vue,
                    "Erreur lors de la suppression :\n" + message,
                    "Erreur BDD",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Gere le double-clic sur un element de la liste
     */
    private void handleDoubleClick(MouseEvent e) {
        if (e.getClickCount() == 2 && javax.swing.SwingUtilities.isLeftMouseButton(e)) {
            JList<LocataireItem> liste = this.vue.getListeLocataires();
            int index = liste.locationToIndex(e.getPoint());

            if (index >= 0) {
                liste.setSelectedIndex(index);
                LocataireItem item = this.vue.getModeleLocataires().getElementAt(index);
                
                pageDocumentsLocataire dlg = new pageDocumentsLocataire(item);
                dlg.setLocationRelativeTo(this.vue);
                dlg.setVisible(true);
            }
        }
    }
}
