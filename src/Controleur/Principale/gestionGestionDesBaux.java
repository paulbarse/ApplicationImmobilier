package Controleur.Principale;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import Controleur.refacto.NavigationHelper;
import modele.Facture;
import modele.Loyer;
import modele.dao.BailDao;
import modele.dao.FactureDao;
import modele.dao.LoyerDao;
import modele.dao.UtOracleDataSource;
import vue.Principale.pageGestionDesBaux;
import vue.Saisie.pageSaisieBail;
import vue.Saisie.pageSaisieFacture;
import vue.Saisie.pageSaisieModificationBail;
import vue.bnt.DlgHistoriqueBauxClotures;
import vue.bnt.DlgRevalorisationLoyer;
import vue.bnt.DlgSoldeToutCompte;
import vue.bnt.pageImportLoyers;

/**
 * Controleur pour la page de gestion des baux.
 */
public class gestionGestionDesBaux implements ActionListener, ListSelectionListener {

    private final pageGestionDesBaux vue;
    private ArrayList<String> listeIdsBaux = new ArrayList<>();
    private ArrayList<Long> listeIdsFactures = new ArrayList<>();
    private String idBailSelectionne = null;
    private String nomLogementSelectionne = null;
    private Long idFactureSelectionnee = null;

    public gestionGestionDesBaux(pageGestionDesBaux vue) {
        this.vue = vue;
    }

    /**
     * Charge les données des baux depuis la BDD
     */
    public void chargerDonneesBaux() {
        try (BailDao dao = new BailDao()) {
            List<Object[]> lignes = dao.findAllForView();

            DefaultTableModel model = (DefaultTableModel) vue.getTableBails().getModel();
            model.setRowCount(0);
            listeIdsBaux.clear();

            for (Object[] row : lignes) {
                listeIdsBaux.add((String) row[0]);
                model.addRow(new Object[] { 
                    row[1], // Logement
                    row[3], // Locataire
                    row[4], // Date debut
                    row[5], // Date fin
                    row[6], // Loyer
                    row[7], // Caution
                    row[8]  // Etat
                });
            }
            
            viderTableLoyers();
            viderTableFactures();
            idBailSelectionne = null;
            nomLogementSelectionne = null;
            
            if (vue.getBtnAjouterFacture() != null) {
                vue.getBtnAjouterFacture().setEnabled(false);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vue,
                    "Erreur lors du chargement des baux : " + e.getMessage(),
                    "Erreur BDD",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Charge les loyers d'un bail
     */
    private void chargerLoyers(String idBail) {
        try (LoyerDao dao = new LoyerDao()) {
            List<Loyer> loyers = dao.findByBail(idBail);

            DefaultTableModel model = (DefaultTableModel) vue.getTableLoyers().getModel();
            model.setRowCount(0);

            for (Loyer l : loyers) {
                model.addRow(new Object[] { 
                    l.getMois(), 
                    l.getAnnee(),
                    l.getMontantLoyer(), 
                    l.getProvision(), 
                    l.getStatut() 
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement des loyers : " + e.getMessage());
        }
    }

    /**
     * Charge les factures d'un bail depuis la BDD
     */
    private void chargerFactures(String idBail) {
        try (FactureDao dao = new FactureDao()) {
            List<Facture> factures = dao.findByBail(Long.parseLong(idBail));

            DefaultTableModel model = (DefaultTableModel) vue.getTableFactures().getModel();
            model.setRowCount(0);
            listeIdsFactures.clear();
            idFactureSelectionnee = null;

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            for (Facture f : factures) {
                listeIdsFactures.add(f.getIdFactureLong());
                Object[] rowData = buildFactureRowData(f, sdf);
                model.addRow(rowData);
            }

            if (vue.getBtnPayerFacture() != null) {
                vue.getBtnPayerFacture().setEnabled(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur chargement factures : " + e.getMessage());
        }
    }

    /**
     * Construit les données d'une ligne de facture pour le tableau
     */
    private Object[] buildFactureRowData(Facture f, SimpleDateFormat sdf) {
        String dateStr = (f.getDateEmission() != null) ? sdf.format(f.getDateEmission()) : "";

        String entreprise = "";
        if (f.getNomEntreprise() != null && !f.getNomEntreprise().isEmpty()) {
            entreprise = f.getNomEntreprise();
        } else if (f.getSiret() > 0) {
            entreprise = String.valueOf(f.getSiret());
        }

        String ttcStr = String.format("%.2f", f.getMontantTTC());

        boolean hasTravaux = (f.getTravaux() != null && !f.getTravaux().trim().isEmpty());
        String travauxStr = hasTravaux ? "Oui" : "Non";

        String devisStr = "";
        if (hasTravaux && f.getMontantDevis() != null && f.getMontantDevis() > 0) {
            devisStr = String.format("%.2f", f.getMontantDevis());
        }

        String typeStr = "-";
        if (Boolean.TRUE.equals(f.isRecuperableLocataire())) {
            typeStr = "Recup. locataire";
        } else if (Boolean.TRUE.equals(f.isDeductibleImpot())) {
            typeStr = "Deduc. impots";
        }

        String statut = (f.getStatut() != null && !f.getStatut().trim().isEmpty()) 
            ? f.getStatut() : "A payer";

        return new Object[] {
            f.getIdFacture(),
            dateStr,
            f.getNature(),
            entreprise,
            ttcStr,
            travauxStr,
            devisStr,
            typeStr,
            statut
        };
    }

    private void viderTableLoyers() {
        DefaultTableModel model = (DefaultTableModel) vue.getTableLoyers().getModel();
        model.setRowCount(0);
    }

    private void viderTableFactures() {
        DefaultTableModel model = (DefaultTableModel) vue.getTableFactures().getModel();
        model.setRowCount(0);
        listeIdsFactures.clear();
        idFactureSelectionnee = null;
        if (vue.getBtnPayerFacture() != null) {
            vue.getBtnPayerFacture().setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "AJOUTER_BAIL":
                ouvrirSaisieBail();
                return;

            case "MODIFIER_BAIL":
                modifierBailSelectionne();
                return;

            case "CLORE_BAIL":
                cloturerBailSelectionne();
                return;

            case "REVALORISER_LOYER":
                revaloriserLoyerSelectionne();
                return;

            case "SOLDE_TOUT_COMPTE":
            case "HISTORIQUE_BAUX_CLOTURES":
                ouvrirHistoriqueBauxClotures();
                return;

            case "IMPORT_LOYERS_CSV":
                ouvrirImportLoyersCsv();
                return;

            case "AJOUTER_FACTURE":
                ajouterFacture();
                return;

            case "PAYER_FACTURE":
                payerFacture();
                return;

            case "OUVRIR_GESTION_BAILS":
                return;
                
            default:
                NavigationHelper.handleNavigation(vue, cmd);
                break;
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;

        if (e.getSource() == vue.getTableBails().getSelectionModel()) {
            handleBailSelection();
        }
        
        if (e.getSource() == vue.getTableFactures().getSelectionModel()) {
            handleFactureSelection();
        }
    }

    /**
     * Gère la sélection d'un bail
     */
    private void handleBailSelection() {
        int row = vue.getTableBails().getSelectedRow();
        if (row >= 0 && row < listeIdsBaux.size()) {
            idBailSelectionne = listeIdsBaux.get(row);
            nomLogementSelectionne = vue.getTableBails().getValueAt(row, 0) != null 
                ? vue.getTableBails().getValueAt(row, 0).toString() : "";
            
            chargerLoyers(idBailSelectionne);
            chargerFactures(idBailSelectionne);
            
            if (vue.getBtnAjouterFacture() != null) {
                vue.getBtnAjouterFacture().setEnabled(true);
            }
        }
    }

    /**
     * Gère la sélection d'une facture
     */
    private void handleFactureSelection() {
        int row = vue.getTableFactures().getSelectedRow();
        if (row >= 0 && row < listeIdsFactures.size()) {
            idFactureSelectionnee = listeIdsFactures.get(row);
            
            String statut = vue.getTableFactures().getValueAt(row, 8) != null 
                ? vue.getTableFactures().getValueAt(row, 8).toString() : "";
            
            if (vue.getBtnPayerFacture() != null) {
                vue.getBtnPayerFacture().setEnabled(!"Paye".equalsIgnoreCase(statut));
            }
        }
    }

    /**
     * Ouvre le formulaire de saisie d'un nouveau bail
     */
    private void ouvrirSaisieBail() {
        pageSaisieBail dlg = new pageSaisieBail();
        dlg.setLocationRelativeTo(vue);
        dlg.setVisible(true);
        chargerDonneesBaux();
    }

    /**
     * Modifier le bail sélectionné
     */
    private void modifierBailSelectionne() {
        JTable table = vue.getTableBails();
        int row = table.getSelectedRow();
        
        if (row < 0 || row >= listeIdsBaux.size()) {
            JOptionPane.showMessageDialog(vue,
                    "Veuillez selectionner un bail.",
                    "Aucune selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Vérifier si le bail est cloturé
        String etat = table.getValueAt(row, 6) != null ? table.getValueAt(row, 6).toString() : "";
        if ("CLOTURE".equalsIgnoreCase(etat)) {
            JOptionPane.showMessageDialog(vue,
                    "Impossible de modifier un bail cloture.\n\n" +
                    "Les informations d'un bail termine ne peuvent plus etre changees.",
                    "Bail cloture",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idBailStr = listeIdsBaux.get(row);
        long idBail = Long.parseLong(idBailStr);
        
        pageSaisieModificationBail dlg = new pageSaisieModificationBail(idBail);
        dlg.setLocationRelativeTo(vue);
        dlg.setVisible(true);
        
        chargerDonneesBaux();
    }

    /**
     * Cloturer le bail sélectionné
     */
    private void cloturerBailSelectionne() {
        JTable table = vue.getTableBails();
        int row = table.getSelectedRow();

        if (row < 0 || row >= listeIdsBaux.size()) {
            JOptionPane.showMessageDialog(vue,
                    "Veuillez selectionner un bail a cloturer.",
                    "Aucune selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String etat = table.getValueAt(row, 6) != null ? table.getValueAt(row, 6).toString() : "";
        if ("CLOTURE".equals(etat)) {
            JOptionPane.showMessageDialog(vue,
                    "Ce bail est deja cloture.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String idBailStr = listeIdsBaux.get(row);
        long idBail = Long.parseLong(idBailStr);

        String logement = table.getValueAt(row, 0) != null ? table.getValueAt(row, 0).toString() : "Non assigne";
        String locataire = table.getValueAt(row, 1) != null ? table.getValueAt(row, 1).toString() : "Aucun";

        double caution = extraireCaution(table, row);

        DlgSoldeToutCompte dlg = new DlgSoldeToutCompte(vue, logement, locataire, caution, idBail);
        dlg.setLocationRelativeTo(vue);
        dlg.setVisible(true);
        
        chargerDonneesBaux();
    }

    /**
     * Extrait le montant de la caution depuis le tableau
     */
    private double extraireCaution(JTable table, int row) {
        double caution = 0.0;
        try {
            Object valCaution = table.getValueAt(row, 5);
            if (valCaution != null) {
                String s = valCaution.toString().replace(',', '.').replaceAll("\\s+", "");
                caution = Double.parseDouble(s);
            }
        } catch (Exception e) {
            System.out.println("Erreur lecture caution : " + e.getMessage());
        }
        return caution;
    }

    /**
     * Ouvre l'historique des baux cloturés
     */
    private void ouvrirHistoriqueBauxClotures() {
        DlgHistoriqueBauxClotures dlg = new DlgHistoriqueBauxClotures(vue);
        dlg.setLocationRelativeTo(vue);
        dlg.setVisible(true);
    }

    /**
     * Ajoute une facture au bail sélectionné
     */
    private void ajouterFacture() {
        if (idBailSelectionne == null) {
            JOptionPane.showMessageDialog(vue,
                    "Veuillez selectionner un bail.",
                    "Aucune selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        long idBail = Long.parseLong(idBailSelectionne);
        
        pageSaisieFacture dlg = new pageSaisieFacture(idBail, nomLogementSelectionne);
        dlg.setLocationRelativeTo(vue);
        dlg.setVisible(true);
        
        chargerFactures(idBailSelectionne);
    }

    /**
     * Marque la facture sélectionnée comme payée
     */
    private void payerFacture() {
        if (idFactureSelectionnee == null) {
            JOptionPane.showMessageDialog(vue,
                    "Veuillez selectionner une facture.",
                    "Aucune selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTable tableFactures = vue.getTableFactures();
        int row = tableFactures.getSelectedRow();
        
        String nature = tableFactures.getValueAt(row, 2) != null ? 
            tableFactures.getValueAt(row, 2).toString() : "";
        String montant = tableFactures.getValueAt(row, 4) != null ? 
            tableFactures.getValueAt(row, 4).toString() : "";
        String entreprise = tableFactures.getValueAt(row, 3) != null ? 
            tableFactures.getValueAt(row, 3).toString() : "";

        int reponse = JOptionPane.showConfirmDialog(vue,
                "Marquer cette facture comme payee ?\n\n" +
                "Nature : " + nature + "\n" +
                "Montant : " + montant + " EUR\n" +
                "Entreprise : " + entreprise,
                "Confirmation de paiement",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        
        if (reponse != JOptionPane.YES_OPTION) {
            return;
        }


        try (FactureDao dao = new FactureDao()) {
            dao.marquerPayee(idFactureSelectionnee);
            
            JOptionPane.showMessageDialog(vue,
                    "Facture marquee comme payee !",
                    "Succes",
                    JOptionPane.INFORMATION_MESSAGE);
            
            chargerFactures(idBailSelectionne);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(vue,
                    "Erreur lors du paiement de la facture :\n" + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Revaloriser le loyer du bail sélectionné
     */
    private void revaloriserLoyerSelectionne() {
        JTable table = vue.getTableBails();
        int row = table.getSelectedRow();
        
        if (row < 0 || row >= listeIdsBaux.size()) {
            JOptionPane.showMessageDialog(vue,
                    "Veuillez selectionner un bail.",
                    "Aucune selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idBailStr = listeIdsBaux.get(row);
        long idBail = Long.parseLong(idBailStr);
        
        String logement = table.getValueAt(row, 0) != null ? table.getValueAt(row, 0).toString() : "Non assigne";
        String locataire = table.getValueAt(row, 1) != null ? table.getValueAt(row, 1).toString() : "Aucun";
        String etat = table.getValueAt(row, 6) != null ? table.getValueAt(row, 6).toString() : "";

        if (!"EN_COURS".equals(etat)) {
            JOptionPane.showMessageDialog(vue,
                    "Seuls les baux en cours peuvent etre revalorises.",
                    "Bail non actif",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double loyerActuel = extraireLoyerActuel(table, row);

        // Vérification stricte
        boolean peutRevaloriser = verifierPossibiliteRevalorisation(idBail);

        if (!peutRevaloriser) {
            afficherMessageErreurRevalorisation(idBail);
            return;
        }

        // Ouvrir le dialogue seulement si autorisé
        DlgRevalorisationLoyer dlg = new DlgRevalorisationLoyer(
                vue, idBail, logement, locataire, loyerActuel, peutRevaloriser);
        dlg.setVisible(true);

        if (dlg.isRevalorisationEffectuee()) {
            chargerDonneesBaux();
        }
    }

    /**
     * Extrait le loyer actuel depuis le tableau
     */
    private double extraireLoyerActuel(JTable table, int row) {
        double loyerActuel = 0.0;
        try {
            Object val = table.getValueAt(row, 4);
            if (val != null) {
                loyerActuel = Double.parseDouble(val.toString());
            }
        } catch (Exception e) {
            System.out.println("Impossible de recuperer le loyer actuel");
        }
        return loyerActuel;
    }

    /**
     * Vérifie si la revalorisation est possible
     */
    private boolean verifierPossibiliteRevalorisation(long idBail) {
        // CORRECTION : Try-with-resources
        try {
            UtOracleDataSource.creerAcces();
            try (BailDao dao = new BailDao()) {
                dao.setConnection(UtOracleDataSource.getConnection());
                return dao.peutRevaloriser(idBail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Affiche le message d'erreur de revalorisation
     */
    private void afficherMessageErreurRevalorisation(long idBail) {
        String messageErreur = null;
        
        try {
            UtOracleDataSource.creerAcces();
            try (BailDao dao = new BailDao()) {
                dao.setConnection(UtOracleDataSource.getConnection());
                messageErreur = dao.getMessageErreurRevalorisation(idBail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String message = "Revalorisation impossible.\n\n";
        if (messageErreur != null && !messageErreur.isEmpty()) {
            message += messageErreur;
        } else {
            message += "Le bail doit avoir au moins 12 mois et\n" +
                      "aucune revalorisation ne doit avoir ete\n" +
                      "effectuee dans les 12 derniers mois.";
        }
        
        JOptionPane.showMessageDialog(vue,
                message,
                "Revalorisation impossible",
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Ouvre l'import de loyers CSV
     */
    private void ouvrirImportLoyersCsv() {
        pageImportLoyers dlg = new pageImportLoyers(vue);
        dlg.setLocationRelativeTo(vue);
        dlg.setVisible(true);
        
        chargerDonneesBaux();
    }
}
