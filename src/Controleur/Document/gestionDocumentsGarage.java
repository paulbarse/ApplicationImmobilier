package Controleur.Document;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import Controleur.refacto.AbstractDocumentController;
import modele.dao.DocumentGarageDao;
import vue.Document.pageDocumentsGarage;

/**
 * Controleur pour la gestion des documents de garage
 */
public class gestionDocumentsGarage extends AbstractDocumentController implements ActionListener {

    private final pageDocumentsGarage vue;
    private DocumentGarageDao dao;
    private long idGarage;
    
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    // Types de compteur/charges disponibles
    private static final String[] TYPES_COMPTEUR = {
        "Eau", 
        "Gaz", 
        "Electricite", 
        "Ordures menageres", 
        "Entretien"
    };
    
    // Unites de mesure disponibles
    private static final String[] UNITES_COMPTEUR = {
        "m3",       // Eau, Gaz
        "kWh",      // Electricite
        "L",        // Eau (litres)
        "unite",    // Comptage unitaire
        "forfait"   // Forfait (OM, Entretien)
    };
    
    private static final String[] TYPES_ASSURANCE = {"PROPRIETAIRE", "PNO", "GLI", "AIDE_JURIDIQUE"};
    private static final String[] TYPES_DEPOT = {"Versement", "Restitution", "Retenue"};
    private static final String[] MOYENS_PAIEMENT = {"Virement", "Cheque", "Especes", "Prelevement"};

    public gestionDocumentsGarage(pageDocumentsGarage vue) {
        super();
        this.vue = vue;
        if (vue.getGarage() != null) {
            this.idGarage = vue.getGarage().getIdGarageLong();
        }
        try {
            this.dao = new DocumentGarageDao();
        } catch (SQLException e) { showBddError(e); }
        
        chargerToutesDonnees();
    }

    @Override
    protected Component getVue() { return vue; }

    @Override
    public void chargerToutesDonnees() {
        if (idGarage <= 0) return;
        chargerLocataire();
        chargerBaux();
        chargerDepotGarantie();
        chargerReleves();
        chargerAssurances();
        chargerLoyers();
        chargerQuittances();
    }

    //  CHARGEMENT 
    
    @FunctionalInterface
    private interface DataProvider { List<Object[]> get() throws SQLException; }

    private void reloadTable(JTable table, DataProvider provider) {
        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            List<Object[]> rows = provider.get();
            if (rows != null) {
                for (Object[] row : rows) model.addRow(row);
            }
        } catch (SQLException e) { showBddError(e); }
    }

    private void chargerLocataire() {
        try {
            DefaultTableModel model = (DefaultTableModel) vue.getTableLocataire().getModel();
            model.setRowCount(0);
            List<Object[]> liste = dao.getLocataireGarage(idGarage);
            for (Object[] row : liste) model.addRow(row);
        } catch (SQLException e) { showBddError(e); }
    }
    
    private void chargerBaux() { reloadTable(vue.getTableBail(), () -> dao.getBauxGarage(idGarage)); }
    private void chargerDepotGarantie() { reloadTable(vue.getTableDepot(), () -> dao.getDepotGarantieGarage(idGarage)); }
    private void chargerAssurances() { reloadTable(vue.getTableAssurances(), () -> dao.getAssurancesGarage(idGarage)); }
    private void chargerLoyers() { reloadTable(vue.getTableLoyers(), () -> dao.getLoyersEnAttenteGarage(idGarage)); }
    private void chargerQuittances() { reloadTable(vue.getTableQuittances(), () -> dao.getQuittancesPayeesGarage(idGarage)); }

    /**
     * Charge les releves avec quotites
     */
    private void chargerReleves() {
        try {
            DefaultTableModel model = (DefaultTableModel) vue.getTableReleves().getModel();
            model.setRowCount(0);

            List<Object[]> releves = dao.getRelevesCompteursGarage(idGarage);
            for (Object[] row : releves) {
                // DAO retourne : ID, Type, Date, AncienIndex, NouvelIndex, NumCompteur, Unite,
                //                Consommation, QuotiteEau, QuotiteElec, QuotiteOM, QuotiteEntretien
                model.addRow(new Object[]{
                    row[0],  // ID
                    row[1],  // Type
                    formatDate((Date) row[2]),  // Date
                    row[3],  // Ancien index
                    row[4],  // Nouvel index
                    row[5],  // N Compteur
                    row[6],  // Unite
                    row[7],  // Consommation calculee
                    String.format("%.0f%%", (Double) row[8]),   // % Eau
                    String.format("%.0f%%", (Double) row[9]),   // % Elec
                    String.format("%.0f%%", (Double) row[10]),  // % OM
                    String.format("%.0f%%", (Double) row[11])   // % Entretien
                });
            }
        } catch (SQLException e) {
            showBddError(e);
        }
    }

    // ACTIONS

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "FERMER_DOCUMENTS_GARAGE": vue.dispose(); break;
            // Depot de garantie
            case "AJOUTER_DEPOT_GARAGE": ajouterDepotGarantie(); break;
            case "MODIFIER_DEPOT_GARAGE": modifierDepotGarantie(); break;
            case "SUPPRIMER_DEPOT_GARAGE": supprimerDepotGarantie(); break;
            // Releve compteur
            case "AJOUTER_RELEVE": ajouterReleve(); break;
            case "MODIFIER_RELEVE": modifierReleve(); break;
            case "SUPPRIMER_RELEVE": supprimerReleve(); break;
            // Assurance
            case "AJOUTER_ASSURANCE": ajouterAssurance(); break;
            case "MODIFIER_ASSURANCE": modifierAssurance(); break;
            case "SUPPRIMER_ASSURANCE": supprimerAssurance(); break;
            // Loyers
            case "AJOUTER_LOYER_GARAGE": marquerLoyerPaye(); break;
            case "SUPPRIMER_QUITTANCE_GARAGE": annulerPaiement(); break;
        }
    }

    // DEPOT DE GARANTIE


    private void ajouterDepotGarantie() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField dateField = new JTextField(formatDate(new Date()));
        JComboBox<String> typeCombo = new JComboBox<>(TYPES_DEPOT);
        JTextField montantField = new JTextField("0.00");
        JComboBox<String> moyenCombo = new JComboBox<>(MOYENS_PAIEMENT);
        JTextField obsField = new JTextField();
        
        panel.add(new JLabel("Date (jj/mm/aaaa):"));
        panel.add(dateField);
        panel.add(new JLabel("Type:"));
        panel.add(typeCombo);
        panel.add(new JLabel("Montant:"));
        panel.add(montantField);
        panel.add(new JLabel("Moyen de paiement:"));
        panel.add(moyenCombo);
        panel.add(new JLabel("Observations:"));
        panel.add(obsField);
        
        int result = JOptionPane.showConfirmDialog(vue, panel, "Nouveau Depot de Garantie", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                dao.ajouterDepotGarantie(idGarage, parseDate(dateField.getText()), 
                        (String) typeCombo.getSelectedItem(), parseMontant(montantField.getText()), 
                        (String) moyenCombo.getSelectedItem(), obsField.getText());
                chargerDepotGarantie();
                showSuccess("Depot de garantie ajoute");
            } catch (Exception e) { showError(e.getMessage()); }
        }
    }

    private void modifierDepotGarantie() {
        JTable table = vue.getTableDepot();
        if (!hasSelection(table)) {
            showInfo("Veuillez selectionner un depot a modifier");
            return;
        }
        int row = table.getSelectedRow();
        try {
            long idMouvement = getLongValue(table, row, 0);
            String dateActuelle = formatDate((Date) table.getValueAt(row, 1));
            String typeActuel = (String) table.getValueAt(row, 2);
            String montantActuel = String.valueOf(table.getValueAt(row, 3));
            String moyenActuel = (String) table.getValueAt(row, 4);
            String obsActuelles = (String) table.getValueAt(row, 5);

            JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
            JTextField dateField = new JTextField(dateActuelle);
            JComboBox<String> typeCombo = new JComboBox<>(TYPES_DEPOT);
            typeCombo.setSelectedItem(typeActuel);
            JTextField montantField = new JTextField(montantActuel);
            JComboBox<String> moyenCombo = new JComboBox<>(MOYENS_PAIEMENT);
            moyenCombo.setSelectedItem(moyenActuel);
            JTextField obsField = new JTextField(obsActuelles != null ? obsActuelles : "");
            
            panel.add(new JLabel("Date:"));
            panel.add(dateField);
            panel.add(new JLabel("Type:"));
            panel.add(typeCombo);
            panel.add(new JLabel("Montant:"));
            panel.add(montantField);
            panel.add(new JLabel("Moyen de paiement:"));
            panel.add(moyenCombo);
            panel.add(new JLabel("Observations:"));
            panel.add(obsField);
            
            int result = JOptionPane.showConfirmDialog(vue, panel, "Modifier Depot de Garantie", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                dao.modifierDepotGarantie(idMouvement, parseDate(dateField.getText()), 
                        (String) typeCombo.getSelectedItem(), parseMontant(montantField.getText()), 
                        (String) moyenCombo.getSelectedItem(), obsField.getText());
                chargerDepotGarantie();
                showSuccess("Depot modifie");
            }
        } catch (Exception e) { showError("Erreur: " + e.getMessage()); }
    }

    private void supprimerDepotGarantie() {
        JTable table = vue.getTableDepot();
        if (hasSelection(table) && confirmerSuppression("ce depot")) {
            try {
                dao.supprimerDepotGarantie(getLongValue(table, table.getSelectedRow(), 0));
                chargerDepotGarantie();
                showSuccess("Depot supprime");
            } catch (Exception e) { showBddError(e); }
        }
    }


    // RELEVES COMPTEURS - AVEC QUOTITES


    private void ajouterReleve() {
        Window parentWindow = SwingUtilities.getWindowAncestor(vue);
        
        JDialog dlg = new JDialog(parentWindow, "Ajouter un releve compteur", 
                                  Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setLayout(new BorderLayout(10, 10));
        dlg.setSize(500, 450);
        dlg.setLocationRelativeTo(vue);
        
        JPanel formPanel = new JPanel(new GridLayout(11, 2, 10, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Type (dropdown)
        JComboBox<String> cbType = new JComboBox<>(TYPES_COMPTEUR);
        formPanel.add(new JLabel("Type :"));
        formPanel.add(cbType);
        
        // Date
        JTextField txtDate = new JTextField(sdf.format(new Date()));
        formPanel.add(new JLabel("Date (dd/MM/yyyy) :"));
        formPanel.add(txtDate);
        
        // Ancien Index
        JTextField txtAncienIndex = new JTextField("0");
        formPanel.add(new JLabel("Ancien Index :"));
        formPanel.add(txtAncienIndex);
        
        // Nouvel Index
        JTextField txtNouvelIndex = new JTextField();
        formPanel.add(new JLabel("Nouvel Index :"));
        formPanel.add(txtNouvelIndex);
        
        // N Compteur
        JTextField txtNumCompteur = new JTextField();
        formPanel.add(new JLabel("N Compteur :"));
        formPanel.add(txtNumCompteur);
        
        // Unite (dropdown)
        JComboBox<String> cbUnite = new JComboBox<>(UNITES_COMPTEUR);
        formPanel.add(new JLabel("Unite :"));
        formPanel.add(cbUnite);
        
        // Separator
        formPanel.add(new JLabel("--- Quotites (%) ---"));
        formPanel.add(new JLabel(""));
        
        // Quotite Eau
        JTextField txtQuotiteEau = new JTextField("100");
        formPanel.add(new JLabel("Quotite Eau (%) :"));
        formPanel.add(txtQuotiteEau);
        
        // Quotite Electricite
        JTextField txtQuotiteElec = new JTextField("100");
        formPanel.add(new JLabel("Quotite Electricite (%) :"));
        formPanel.add(txtQuotiteElec);
        
        // Quotite OM
        JTextField txtQuotiteOM = new JTextField("100");
        formPanel.add(new JLabel("Quotite Ordures M. (%) :"));
        formPanel.add(txtQuotiteOM);
        
        // Quotite Entretien
        JTextField txtQuotiteEntretien = new JTextField("100");
        formPanel.add(new JLabel("Quotite Entretien (%) :"));
        formPanel.add(txtQuotiteEntretien);
        
        dlg.add(formPanel, BorderLayout.CENTER);
        
        // Boutons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnValider = new JButton("Valider");
        JButton btnAnnuler = new JButton("Annuler");
        btnPanel.add(btnValider);
        btnPanel.add(btnAnnuler);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        
        final boolean[] validated = {false};
        
        btnValider.addActionListener(e -> {
            try {
                String dateStr = txtDate.getText().trim();
                String ancienIndexStr = txtAncienIndex.getText().trim();
                String nouvelIndexStr = txtNouvelIndex.getText().trim();
                String numCompteurStr = txtNumCompteur.getText().trim();
                
                if (dateStr.isEmpty() || ancienIndexStr.isEmpty() || 
                    nouvelIndexStr.isEmpty() || numCompteurStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, 
                        "Tous les champs sont obligatoires.",
                        "Erreur", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                Date date = sdf.parse(dateStr);
                long ancienIndex = Long.parseLong(ancienIndexStr);
                long nouvelIndex = Long.parseLong(nouvelIndexStr);
                long numCompteur = Long.parseLong(numCompteurStr);
                
                // VALIDATION : Nouvel index >= Ancien index
                if (nouvelIndex < ancienIndex) {
                    JOptionPane.showMessageDialog(dlg, 
                        "Le nouvel index doit etre superieur ou egal a l'ancien index.\n\n" +
                        "Ancien index: " + ancienIndex + "\n" +
                        "Nouvel index: " + nouvelIndex,
                        "Erreur de saisie", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Quotites
                double quotiteEau = parseQuotite(txtQuotiteEau.getText());
                double quotiteElec = parseQuotite(txtQuotiteElec.getText());
                double quotiteOM = parseQuotite(txtQuotiteOM.getText());
                double quotiteEntretien = parseQuotite(txtQuotiteEntretien.getText());
                
                String type = (String) cbType.getSelectedItem();
                String unite = (String) cbUnite.getSelectedItem();
                
                dao.ajouterReleveCompteurGarage(idGarage, type, date, numCompteur, ancienIndex, nouvelIndex, unite,
                        quotiteEau, quotiteElec, quotiteOM, quotiteEntretien);
                
                validated[0] = true;
                dlg.dispose();
                
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(dlg, 
                    "Format de date invalide. Utilisez dd/MM/yyyy",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, 
                    "Les index et le numero de compteur doivent etre des nombres entiers.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg, 
                    "Erreur BDD: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnAnnuler.addActionListener(e -> dlg.dispose());
        
        dlg.setVisible(true);
        
        if (validated[0]) {
            chargerReleves();
            showSuccess("Releve ajoute avec succes");
        }
    }

    private void modifierReleve() {
        JTable table = vue.getTableReleves();
        int row = table.getSelectedRow();
        if (row < 0) {
            showInfo("Veuillez selectionner un releve a modifier");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        long idReleve = Long.parseLong(model.getValueAt(row, 0).toString());
        
        // Recuperer les valeurs actuelles
        String currentType = str(model.getValueAt(row, 1));
        String currentDate = str(model.getValueAt(row, 2));
        String currentAncienIndex = str(model.getValueAt(row, 3));
        String currentNouvelIndex = str(model.getValueAt(row, 4));
        String currentNumCompteur = str(model.getValueAt(row, 5));
        String currentUnite = str(model.getValueAt(row, 6));
        // Quotites (enlever le %)
        String currentQuotiteEau = str(model.getValueAt(row, 8)).replace("%", "");
        String currentQuotiteElec = str(model.getValueAt(row, 9)).replace("%", "");
        String currentQuotiteOM = str(model.getValueAt(row, 10)).replace("%", "");
        String currentQuotiteEntretien = str(model.getValueAt(row, 11)).replace("%", "");
        
        Window parentWindow = SwingUtilities.getWindowAncestor(vue);
        
        JDialog dlg = new JDialog(parentWindow, "Modifier un releve compteur", 
                                  Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setLayout(new BorderLayout(10, 10));
        dlg.setSize(500, 450);
        dlg.setLocationRelativeTo(vue);
        
        JPanel formPanel = new JPanel(new GridLayout(11, 2, 10, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Type (dropdown)
        JComboBox<String> cbType = new JComboBox<>(TYPES_COMPTEUR);
        cbType.setSelectedItem(currentType);
        formPanel.add(new JLabel("Type :"));
        formPanel.add(cbType);
        
        // Date
        JTextField txtDate = new JTextField(currentDate);
        formPanel.add(new JLabel("Date (dd/MM/yyyy) :"));
        formPanel.add(txtDate);
        
        // Ancien Index
        JTextField txtAncienIndex = new JTextField(currentAncienIndex);
        formPanel.add(new JLabel("Ancien Index :"));
        formPanel.add(txtAncienIndex);
        
        // Nouvel Index
        JTextField txtNouvelIndex = new JTextField(currentNouvelIndex);
        formPanel.add(new JLabel("Nouvel Index :"));
        formPanel.add(txtNouvelIndex);
        
        // N Compteur
        JTextField txtNumCompteur = new JTextField(currentNumCompteur);
        formPanel.add(new JLabel("N Compteur :"));
        formPanel.add(txtNumCompteur);
        
        // Unite (dropdown)
        JComboBox<String> cbUnite = new JComboBox<>(UNITES_COMPTEUR);
        cbUnite.setSelectedItem(currentUnite);
        formPanel.add(new JLabel("Unite :"));
        formPanel.add(cbUnite);
        
        // Separator
        formPanel.add(new JLabel("--- Quotites (%) ---"));
        formPanel.add(new JLabel(""));
        
        // Quotite Eau
        JTextField txtQuotiteEau = new JTextField(currentQuotiteEau);
        formPanel.add(new JLabel("Quotite Eau (%) :"));
        formPanel.add(txtQuotiteEau);
        
        // Quotite Electricite
        JTextField txtQuotiteElec = new JTextField(currentQuotiteElec);
        formPanel.add(new JLabel("Quotite Electricite (%) :"));
        formPanel.add(txtQuotiteElec);
        
        // Quotite OM
        JTextField txtQuotiteOM = new JTextField(currentQuotiteOM);
        formPanel.add(new JLabel("Quotite Ordures M. (%) :"));
        formPanel.add(txtQuotiteOM);
        
        // Quotite Entretien
        JTextField txtQuotiteEntretien = new JTextField(currentQuotiteEntretien);
        formPanel.add(new JLabel("Quotite Entretien (%) :"));
        formPanel.add(txtQuotiteEntretien);
        
        dlg.add(formPanel, BorderLayout.CENTER);
        
        // Boutons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnValider = new JButton("Valider");
        JButton btnAnnuler = new JButton("Annuler");
        btnPanel.add(btnValider);
        btnPanel.add(btnAnnuler);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        
        final boolean[] validated = {false};
        
        btnValider.addActionListener(e -> {
            try {
                Date date = sdf.parse(txtDate.getText().trim());
                long ancienIndex = Long.parseLong(txtAncienIndex.getText().trim());
                long nouvelIndex = Long.parseLong(txtNouvelIndex.getText().trim());
                long numCompteur = Long.parseLong(txtNumCompteur.getText().trim());
                
                // VALIDATION : Nouvel index >= Ancien index
                if (nouvelIndex < ancienIndex) {
                    JOptionPane.showMessageDialog(dlg, 
                        "Le nouvel index doit etre superieur ou egal a l'ancien index.",
                        "Erreur", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Quotites
                double quotiteEau = parseQuotite(txtQuotiteEau.getText());
                double quotiteElec = parseQuotite(txtQuotiteElec.getText());
                double quotiteOM = parseQuotite(txtQuotiteOM.getText());
                double quotiteEntretien = parseQuotite(txtQuotiteEntretien.getText());
                
                String type = (String) cbType.getSelectedItem();
                String unite = (String) cbUnite.getSelectedItem();
                
                dao.modifierReleveCompteurGarage(idReleve, type, date, numCompteur, ancienIndex, nouvelIndex, unite,
                        quotiteEau, quotiteElec, quotiteOM, quotiteEntretien);
                
                validated[0] = true;
                dlg.dispose();
                
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(dlg, 
                    "Format de date invalide. Utilisez dd/MM/yyyy",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, 
                    "Les index doivent etre des nombres entiers.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg, 
                    "Erreur: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnAnnuler.addActionListener(e -> dlg.dispose());
        
        dlg.setVisible(true);
        
        if (validated[0]) {
            chargerReleves();
            showSuccess("Releve modifie avec succes");
        }
    }

    private void supprimerReleve() {
        JTable table = vue.getTableReleves();
        if (hasSelection(table) && confirmerSuppression("ce releve")) {
            try {
                dao.supprimerReleveCompteurGarage(getLongValue(table, table.getSelectedRow(), 0));
                chargerReleves();
                showSuccess("Releve supprime");
            } catch (Exception e) { showBddError(e); }
        }
    }


    // ASSURANCE


    private void ajouterAssurance() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        JTextField numField = new JTextField();
        JTextField compagnieField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(TYPES_ASSURANCE);
        JTextField primeField = new JTextField("0.00");
        JTextField dateEffetField = new JTextField(formatDate(new Date()));
        JTextField dateEcheanceField = new JTextField(formatDate(new Date()));
        
        panel.add(new JLabel("N Assurance:"));
        panel.add(numField);
        panel.add(new JLabel("Compagnie:"));
        panel.add(compagnieField);
        panel.add(new JLabel("Type:"));
        panel.add(typeCombo);
        panel.add(new JLabel("Prime annuelle:"));
        panel.add(primeField);
        panel.add(new JLabel("Date Effet (jj/mm/aaaa):"));
        panel.add(dateEffetField);
        panel.add(new JLabel("Date Echeance (jj/mm/aaaa):"));
        panel.add(dateEcheanceField);
        
        int result = JOptionPane.showConfirmDialog(vue, panel, "Nouvelle Assurance", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                long numAssurance = Long.parseLong(numField.getText().trim());
                double prime = parseMontant(primeField.getText());
                dao.ajouterAssurance(idGarage, numAssurance, compagnieField.getText(), 
                        (String) typeCombo.getSelectedItem(), prime, 
                        parseDate(dateEffetField.getText()), parseDate(dateEcheanceField.getText()));
                chargerAssurances();
                showSuccess("Assurance ajoutee");
            } catch (Exception e) { showError("Erreur: " + e.getMessage()); }
        }
    }

    private void modifierAssurance() {
        JTable table = vue.getTableAssurances();
        if (!hasSelection(table)) {
            showInfo("Veuillez selectionner une assurance a modifier");
            return;
        }
        int row = table.getSelectedRow();
        try {
            long numAssurance = getLongValue(table, row, 0);
            String compagnieActuelle = (String) table.getValueAt(row, 1);
            String typeActuel = (String) table.getValueAt(row, 2);
            String primeActuelle = String.valueOf(table.getValueAt(row, 3));
            String dateEffetActuelle = formatDate((Date) table.getValueAt(row, 4));
            Object dateEcheance = table.getValueAt(row, 5);
            String dateEcheanceActuelle = dateEcheance != null ? formatDate((Date) dateEcheance) : formatDate(new Date());

            JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
            JTextField numField = new JTextField(String.valueOf(numAssurance));
            numField.setEditable(false);
            JTextField compagnieField = new JTextField(compagnieActuelle);
            JComboBox<String> typeCombo = new JComboBox<>(TYPES_ASSURANCE);
            typeCombo.setSelectedItem(typeActuel);
            JTextField primeField = new JTextField(primeActuelle);
            JTextField dateEffetField = new JTextField(dateEffetActuelle);
            JTextField dateEcheanceField = new JTextField(dateEcheanceActuelle);
            
            panel.add(new JLabel("N Assurance (non modifiable):"));
            panel.add(numField);
            panel.add(new JLabel("Compagnie:"));
            panel.add(compagnieField);
            panel.add(new JLabel("Type:"));
            panel.add(typeCombo);
            panel.add(new JLabel("Prime annuelle:"));
            panel.add(primeField);
            panel.add(new JLabel("Date Effet:"));
            panel.add(dateEffetField);
            panel.add(new JLabel("Date Echeance:"));
            panel.add(dateEcheanceField);
            
            int result = JOptionPane.showConfirmDialog(vue, panel, "Modifier Assurance", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                double prime = parseMontant(primeField.getText());
                dao.modifierAssurance(numAssurance, compagnieField.getText(), 
                        (String) typeCombo.getSelectedItem(), prime, 
                        parseDate(dateEffetField.getText()), parseDate(dateEcheanceField.getText()));
                chargerAssurances();
                showSuccess("Assurance modifiee");
            }
        } catch (Exception e) { showError("Erreur: " + e.getMessage()); }
    }

    private void supprimerAssurance() {
        JTable table = vue.getTableAssurances();
        if (hasSelection(table) && confirmerSuppression("cette assurance")) {
            try {
                dao.supprimerAssurance(getLongValue(table, table.getSelectedRow(), 0));
                chargerAssurances();
                showSuccess("Assurance supprimee");
            } catch (Exception e) { showBddError(e); }
        }
    }

    // LOYERS


    private void marquerLoyerPaye() {
        JTable table = vue.getTableLoyers();
        if (hasSelection(table)) {
            try {
                dao.marquerLoyerPayeGarage(getLongValue(table, table.getSelectedRow(), 0), new java.sql.Date(new Date().getTime()));
                chargerLoyers(); chargerQuittances();
                showSuccess("Loyer marque comme paye");
            } catch (Exception e) { showBddError(e); }
        }
    }

    private void annulerPaiement() {
        JTable table = vue.getTableQuittances();
        if (hasSelection(table) && confirmer("Annuler ce paiement ?")) {
            try {
                dao.annulerPaiementGarage(getLongValue(table, table.getSelectedRow(), 0));
                chargerLoyers(); chargerQuittances();
                showSuccess("Paiement annule");
            } catch(Exception e) { showBddError(e); }
        }
    }


    // UTILITAIRES

    protected String str(Object obj) {
        return obj != null ? obj.toString() : "";
    }
    
    private double parseQuotite(String str) {
        try {
            String cleaned = str.replace("%", "").replace(",", ".").trim();
            double val = Double.parseDouble(cleaned);
            return Math.max(0, Math.min(100, val)); // Entre 0 et 100
        } catch (NumberFormatException e) {
            return 100; // Valeur par defaut
        }
    }
}