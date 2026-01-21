package Controleur.Principale;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import Controleur.refacto.NavigationHelper;
import modele.Garage;
import modele.dao.GarageDao;
import modele.dao.UtOracleDataSource;
import vue.Document.pageDocumentsGarage;
import vue.Principale.pageListeDesGarages;
import vue.Principale.pageListeDesGarages.GarageItem;
import vue.Saisie.pageSaisieGarage;

/**
 * Contrôleur pour la page liste des garages 
 */
public class gestionListeDesGarages implements ActionListener, MouseListener {

    private final pageListeDesGarages vue;

    public gestionListeDesGarages(pageListeDesGarages vue) {
        this.vue = vue;

        this.vue.getListeGarages().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleDoubleClick(e);
            }
        });
    }

    /**
     * Charge les données des garages depuis la BDD
     */
    public void chargerDonnees() {
        DefaultListModel<GarageItem> modele = this.vue.getModeleGarages();
        modele.clear();


        try {
            UtOracleDataSource.creerAcces();
            Connection cn = UtOracleDataSource.getConnection();

            try (GarageDao garageDao = new GarageDao()) {
                garageDao.setConnection(cn);

                List<Garage> liste = garageDao.findAll();
                for (Garage g : liste) {
                    // GarageItem calcule automatiquement le statut via garage.isLoue()
                    modele.addElement(new GarageItem(g));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this.vue,
                    "Erreur lors du chargement des garages : " + e.getMessage(),
                    "Erreur BDD",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
        case "AJOUTER_GARAGE":
            ajouterGarage();
            break;

        case "MODIFIER_GARAGE":
            modifierGarage();
            break;

        case "SUPPRIMER_GARAGE":
            supprimerGarage();
            break;

        default:
            NavigationHelper.handleNavigation(this.vue, cmd);
            break;
        }
    }

    /**
     * Ajoute un nouveau garage
     */
    private void ajouterGarage() {
        pageSaisieGarage dlg = new pageSaisieGarage();
        dlg.setLocationRelativeTo(vue);
        dlg.setVisible(true);

        Garage nouveauGarage = dlg.getGarageCree();

        if (nouveauGarage == null) {
            return;
        }

        // Validation
        if (!validerGarage(nouveauGarage)) {
            return;
        }

        try {
            UtOracleDataSource.creerAcces();
            Connection cn = UtOracleDataSource.getConnection();

            try (GarageDao dao = new GarageDao()) {
                dao.setConnection(cn);
                dao.create(nouveauGarage);
            }

            chargerDonnees();
            JOptionPane.showMessageDialog(vue, 
                "Garage ajouté avec succès !\n\n" +
                "• ID: " + nouveauGarage.getIdGarageLong() + "\n" +
                "• Bâtiment ID: " + nouveauGarage.getIdBatiment());

        } catch (SQLException ex) {
            handleCreateError(ex);
        }
    }

    /**
     * Valide les données d'un garage
     */
    private boolean validerGarage(Garage garage) {
        // Vérifier que l'ID est fourni
        if (garage.getIdGarageLong() == 0) {
            JOptionPane.showMessageDialog(vue,
                "L'ID du garage est obligatoire.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Vérifier que le bâtiment est sélectionné (contrainte NOT NULL)
        if (garage.getIdBatiment() == null) {
            JOptionPane.showMessageDialog(vue,
                "Le bâtiment est obligatoire pour créer un garage.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    /**
     * Gère les erreurs de création
     */
    private void handleCreateError(SQLException ex) {
        ex.printStackTrace();
        
        String message = ex.getMessage();
        
        if (message.contains("ORA-00001") || message.contains("unique constraint")) {
            JOptionPane.showMessageDialog(vue,
                "Un garage avec cet ID existe déjà.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } else if (message.contains("ID_BATIMENT") && message.contains("obligatoire")) {
            JOptionPane.showMessageDialog(vue,
                "Le bâtiment est obligatoire.\nVeuillez sélectionner un bâtiment.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } else if (message.contains("ORA-02291")) {
            JOptionPane.showMessageDialog(vue,
                "Le bâtiment sélectionné n'existe pas.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(vue,
                "Erreur lors de l'ajout : " + message,
                "Erreur BDD", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Modifie un garage existant
     */
    private void modifierGarage() {
        JList<GarageItem> liste = vue.getListeGarages();
        int index = liste.getSelectedIndex();

        if (index < 0) {
            JOptionPane.showMessageDialog(vue, "Sélectionnez un garage à modifier.");
            return;
        }

        GarageItem item = vue.getModeleGarages().getElementAt(index);
        Garage garExistant = item.getGarage();

        pageSaisieGarage dlgModif = new pageSaisieGarage();
        dlgModif.remplirDepuis(garExistant);
        dlgModif.setLocationRelativeTo(vue);
        dlgModif.setVisible(true);

        Garage garModifie = dlgModif.getGarageCree();

        if (garModifie == null) {
            return;
        }

        garModifie.setIdGarage(garExistant.getIdGarageLong());


        try {
            UtOracleDataSource.creerAcces();
            Connection cn = UtOracleDataSource.getConnection();

            try (GarageDao dao = new GarageDao()) {
                dao.setConnection(cn);
                dao.update(garModifie);
            }
            
            chargerDonnees();
            JOptionPane.showMessageDialog(vue, "Garage modifié avec succès !");

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(vue,
                "Erreur modification : " + ex.getMessage());
        }
    }

    /**
     * Supprime un garage
     */
    private void supprimerGarage() {
        JList<GarageItem> liste = vue.getListeGarages();
        int index = liste.getSelectedIndex();

        if (index < 0) {
            JOptionPane.showMessageDialog(vue, "Sélectionnez un garage à supprimer.");
            return;
        }

        GarageItem item = vue.getModeleGarages().getElementAt(index);
        Garage garASupprimer = item.getGarage();

        // Vérifier si le garage est loué
        if (garASupprimer.isLoue()) {
            JOptionPane.showMessageDialog(vue,
                "Impossible de supprimer ce garage :\n" +
                "Il est actuellement loué (lié à un bail).\n\n" +
                "Veuillez d'abord clôturer le bail.",
                "Suppression impossible",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int rep = JOptionPane.showConfirmDialog(vue,
            "Voulez-vous supprimer le garage :\n" +
            "• ID: " + garASupprimer.getIdGarageLong() + "\n" +
            "• Adresse: " + garASupprimer.getAdresseGarage() + " ?",
            "Confirmation", JOptionPane.YES_NO_OPTION);

        if (rep != JOptionPane.YES_OPTION) {
            return;
        }


        try {
            UtOracleDataSource.creerAcces();
            Connection cn = UtOracleDataSource.getConnection();

            try (GarageDao dao = new GarageDao()) {
                dao.setConnection(cn);
                dao.delete(garASupprimer);
            }
            
            chargerDonnees();
            JOptionPane.showMessageDialog(vue, "Garage supprimé avec succès !");

        } catch (SQLException ex) {
            handleDeleteError(ex);
        }
    }

    /**
     * Gère les erreurs de suppression
     */
    private void handleDeleteError(SQLException ex) {
        ex.printStackTrace();
        
        String message = ex.getMessage();
        if (message.contains("ORA-02292") || message.contains("integrity constraint")) {
            JOptionPane.showMessageDialog(vue,
                "Impossible de supprimer ce garage car il est lié à d'autres données.",
                "Suppression impossible", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(vue,
                "Erreur suppression : " + message);
        }
    }

    /**
     * Gère le double-clic sur un élément de la liste
     */
    private void handleDoubleClick(MouseEvent e) {
        if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
            JList<GarageItem> liste = this.vue.getListeGarages();
            int index = liste.locationToIndex(e.getPoint());

            if (index >= 0) {
                liste.setSelectedIndex(index);
                GarageItem item = this.vue.getModeleGarages().getElementAt(index);
                Garage garageSelectionne = item.getGarage();

                pageDocumentsGarage dlgDocs = new pageDocumentsGarage(garageSelectionne);
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
