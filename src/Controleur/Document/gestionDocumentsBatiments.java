package Controleur.Document;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import Controleur.refacto.AbstractDocumentController;
import modele.Charges;
import modele.Entreprise;
import modele.Facture;
import modele.Garage;
import modele.Logement;
import modele.dao.ChargesDao;
import modele.dao.EntrepriseDao;
import modele.dao.FactureDao;
import modele.dao.GarageDao;
import modele.dao.LogementDao;
import vue.Document.pageDocumentsBatiments;
import vue.Document.pageSaisieDocumentBatiment;

/**
 * Controleur pour la gestion des documents des batiments
 * 
 */
public class gestionDocumentsBatiments extends AbstractDocumentController implements ActionListener {

    private final pageDocumentsBatiments vue;
    private long idBatiment;
    
    private static final String[] TYPES_CHARGES = {"EAU", "ELEC", "OM", "ENTRETIEN", "COPROPRIETE", "TAXE_FONCIERE", "AUTRE"};
    private static final String[] STATUTS_FACTURE = {"A payer", "Paye"};

    public gestionDocumentsBatiments(pageDocumentsBatiments vue) {
        super();
        this.vue = vue;
        if (vue.getBatiment() != null) {
            try {
                String idStr = vue.getBatiment().getIdBatiment(); 
                this.idBatiment = Long.parseLong(idStr);
            } catch (Exception e) { this.idBatiment = 0; }
        }
        chargerToutesDonnees();
    }

    @Override protected Component getVue() { return vue; }

    @Override
    public void chargerToutesDonnees() {
        if (idBatiment <= 0) return;
        chargerTableBiens();
        chargerTableCharges();
        chargerTableFactures();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "FERMER_DOCUMENTS": vue.dispose(); break;
            case "AJOUTER_CHARGE": ajouterCharge(); break;
            case "MODIFIER_CHARGE": modifierCharge(); break;
            case "SUPPRIMER_CHARGE": supprimerCharge(); break;
            case "MODIFIER_FACTURE": modifierFacture(); break;
            case "SUPPRIMER_FACTURE": supprimerFacture(); break;
            case "MARQUER_FACTURE_PAYEE": marquerFacturePayee(); break;
            default: break;
        }
    }


    // CHARGES


    private int getPctRecuperableParDefaut(String typeCharge) {
        if (typeCharge == null) return 100;
        switch (typeCharge.toUpperCase()) {
            case "EAU": case "OM": case "ELEC": case "ENTRETIEN": return 100;
            case "COPROPRIETE": case "TAXE_FONCIERE": return 0;
            default: return 100;
        }
    }
    
    private boolean isPctModifiable(String typeCharge) {
        if (typeCharge == null) return true;
        switch (typeCharge.toUpperCase()) {
            case "EAU": case "OM": case "ELEC": case "ENTRETIEN":
            case "COPROPRIETE": case "TAXE_FONCIERE": return false;
            default: return true;
        }
    }

    public void chargerTableCharges() {
        DefaultTableModel m = (DefaultTableModel) vue.getTableCharges().getModel();
        m.setRowCount(0);
        ChargesDao dao = null;
        try {
            dao = new ChargesDao();
            List<Charges> charges = dao.findAllByBatiment(idBatiment);
            for (Charges c : charges) {
                m.addRow(new Object[]{
                    c.getIdChargeLong(),
                    formatDate(c.getDateCharge()),
                    c.getNature(),
                    c.getTypeCharges(),
                    c.getMontant(),
                    c.getPctRecuperable() + "%"
                });
            }
        } catch (SQLException ex) { 
            showBddError(ex); 
        } finally {
            if (dao != null) try { dao.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    private void ajouterCharge() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField dateField = new JTextField(formatDate(new Date()));
        JTextField natureField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(TYPES_CHARGES);
        JTextField montantField = new JTextField("0.00");
        JTextField pctField = new JTextField("100");
        
        typeCombo.addActionListener(e -> {
            String type = (String) typeCombo.getSelectedItem();
            int pct = getPctRecuperableParDefaut(type);
            pctField.setText(String.valueOf(pct));
            pctField.setEnabled(isPctModifiable(type));
        });
        
        pctField.setText(String.valueOf(getPctRecuperableParDefaut("EAU")));
        pctField.setEnabled(isPctModifiable("EAU"));
        
        panel.add(new JLabel("Date (jj/mm/aaaa):"));
        panel.add(dateField);
        panel.add(new JLabel("Nature:"));
        panel.add(natureField);
        panel.add(new JLabel("Type:"));
        panel.add(typeCombo);
        panel.add(new JLabel("Montant:"));
        panel.add(montantField);
        panel.add(new JLabel("% Recuperable:"));
        panel.add(pctField);
        
        int result = JOptionPane.showConfirmDialog(vue, panel, "Ajouter Charge", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            ChargesDao dao = null;
            try {
                dao = new ChargesDao();
                String type = (String) typeCombo.getSelectedItem();
                double pct = Double.parseDouble(pctField.getText());
                
                if (!isPctModifiable(type)) {
                    pct = getPctRecuperableParDefaut(type);
                }
                
                Charges c = new Charges(
                    0,
                    natureField.getText(),
                    parseMontant(montantField.getText()),
                    parseDate(dateField.getText()),
                    type,
                    pct,
                    idBatiment
                );
                dao.create(c);
                chargerTableCharges();
                showSuccess("Charge ajoutee");
            } catch (Exception e) { 
                showError(e.getMessage()); 
            } finally {
                if (dao != null) try { dao.close(); } catch (SQLException e) { /* ignore */ }
            }
        }
    }

    private void modifierCharge() {
        JTable t = vue.getTableCharges();
        if (!hasSelection(t)) {
            showInfo("Veuillez selectionner une charge a modifier");
            return;
        }
        int row = t.getSelectedRow();
        
        ChargesDao dao = null;
        try {
            dao = new ChargesDao();
            long idCharge = getLongValue(t, row, 0);
            Charges c = dao.findById(idCharge);
            
            if (c == null) {
                showError("Charge non trouvee");
                return;
            }
            
            JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
            JTextField dateField = new JTextField(formatDate(c.getDateCharge()));
            JTextField natureField = new JTextField(c.getNature());
            JComboBox<String> typeCombo = new JComboBox<>(TYPES_CHARGES);
            typeCombo.setSelectedItem(c.getTypeCharges());
            JTextField montantField = new JTextField(String.valueOf(c.getMontant()));
            JTextField pctField = new JTextField(String.valueOf(c.getPctRecuperable()));
            
            typeCombo.addActionListener(e -> {
                String type = (String) typeCombo.getSelectedItem();
                int pct = getPctRecuperableParDefaut(type);
                pctField.setText(String.valueOf(pct));
                pctField.setEnabled(isPctModifiable(type));
            });
            
            String typeActuel = c.getTypeCharges();
            pctField.setEnabled(isPctModifiable(typeActuel));
            
            panel.add(new JLabel("Date:"));
            panel.add(dateField);
            panel.add(new JLabel("Nature:"));
            panel.add(natureField);
            panel.add(new JLabel("Type:"));
            panel.add(typeCombo);
            panel.add(new JLabel("Montant:"));
            panel.add(montantField);
            panel.add(new JLabel("% Recuperable:"));
            panel.add(pctField);
            
            int result = JOptionPane.showConfirmDialog(vue, panel, "Modifier Charge", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                String type = (String) typeCombo.getSelectedItem();
                double pct = Double.parseDouble(pctField.getText());
                
                if (!isPctModifiable(type)) {
                    pct = getPctRecuperableParDefaut(type);
                }
                
                dao.updateCharge(idCharge, parseDate(dateField.getText()), natureField.getText(),
                        type, parseMontant(montantField.getText()), pct);
                chargerTableCharges();
                showSuccess("Charge modifiee");
            }
        } catch (Exception e) { 
            showError("Erreur: " + e.getMessage()); 
        } finally {
            if (dao != null) try { dao.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    private void supprimerCharge() {
        JTable t = vue.getTableCharges();
        if (hasSelection(t) && confirmerSuppression("cette charge")) {
            ChargesDao dao = null;
            try {
                dao = new ChargesDao();
                dao.delete(getLongValue(t, t.getSelectedRow(), 0)); 
                chargerTableCharges();
                showSuccess("Charge supprimee");
            } catch (SQLException e) { 
                showBddError(e); 
            } finally {
                if (dao != null) try { dao.close(); } catch (SQLException ex) { /* ignore */ }
            }
        }
    }


    // FACTURES


    public void chargerTableFactures() {
        DefaultTableModel m = (DefaultTableModel) vue.getTableFactures().getModel();
        m.setRowCount(0);
        FactureDao dao = null;
        EntrepriseDao entrepriseDao = null;
        try {
            dao = new FactureDao();
            entrepriseDao = new EntrepriseDao();
            List<Facture> factures = dao.findByBatiment(idBatiment);
            
            for (Facture f : factures) {
                String nomEntreprise = String.valueOf(f.getSiret());
                try {
                    Entreprise ent = entrepriseDao.findById(String.valueOf(f.getSiret()));
                    if (ent != null && ent.getNomEntreprise() != null) {
                        nomEntreprise = ent.getNomEntreprise();
                    }
                } catch (Exception e) { /* garder le SIRET */ }
                
                m.addRow(new Object[]{
                    f.getIdFacture(),
                    formatDate(f.getDateEmission()),
                    f.getNature(),
                    nomEntreprise,
                    f.getMontantTTC(),
                    f.getStatut() != null ? f.getStatut() : "A payer"
                });
            }
        } catch (SQLException ex) { 
            showBddError(ex); 
        } finally {
            if (dao != null) try { dao.close(); } catch (SQLException e) { /* ignore */ }
            if (entrepriseDao != null) try { entrepriseDao.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    private void modifierFacture() {
        JTable t = vue.getTableFactures();
        if (!hasSelection(t)) {
            showInfo("Veuillez selectionner une facture a modifier");
            return;
        }
        int row = t.getSelectedRow();
        
        long idFacture;
        String dateStr;
        String nature;
        String travaux;
        double montantTTC;
        String statutActuel;
        
        FactureDao factureDao = null;
        try {
            factureDao = new FactureDao();
            idFacture = getLongValue(t, row, 0);
            Facture f = factureDao.findById(idFacture);
            
            if (f == null) {
                showError("Facture non trouvee");
                return;
            }
            
            dateStr = formatDate(f.getDateEmission());
            nature = f.getNature() != null ? f.getNature() : "";
            travaux = f.getTravaux() != null ? f.getTravaux() : "";
            montantTTC = f.getMontantTTC();
            statutActuel = f.getStatut() != null ? f.getStatut() : "A payer";
            
        } catch (SQLException e) { 
            e.printStackTrace();
            showError("Erreur lors du chargement: " + e.getMessage()); 
            return;
        } finally {
            if (factureDao != null) try { factureDao.close(); } catch (SQLException e) { /* ignore */ }
        }
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField dateField = new JTextField(dateStr);
        JTextField natureField = new JTextField(nature);
        JTextField travauxField = new JTextField(travaux);
        JTextField montantField = new JTextField(String.valueOf(montantTTC));
        JComboBox<String> statutCombo = new JComboBox<>(STATUTS_FACTURE);
        statutCombo.setSelectedItem(statutActuel);
        
        panel.add(new JLabel("Date:"));
        panel.add(dateField);
        panel.add(new JLabel("Nature:"));
        panel.add(natureField);
        panel.add(new JLabel("Travaux:"));
        panel.add(travauxField);
        panel.add(new JLabel("Montant TTC:"));
        panel.add(montantField);
        panel.add(new JLabel("Statut:"));
        panel.add(statutCombo);
        
        int result = JOptionPane.showConfirmDialog(vue, panel, "Modifier Facture", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            FactureDao daoUpdate = null;
            try {
                daoUpdate = new FactureDao();
                Facture fUpdate = daoUpdate.findById(idFacture);
                
                if (fUpdate != null) {
                    fUpdate.setDateEmission(parseDate(dateField.getText()));
                    fUpdate.setNature(natureField.getText());
                    fUpdate.setTravaux(travauxField.getText().isEmpty() ? null : travauxField.getText());
                    fUpdate.setMontantTTC(parseMontant(montantField.getText()));
                    fUpdate.setStatut((String) statutCombo.getSelectedItem());
                    
                    daoUpdate.update(fUpdate);
                    showSuccess("Facture modifiee avec succes");
                }
                chargerTableFactures();
                
            } catch (Exception e) { 
                e.printStackTrace();
                showError("Erreur lors de la modification: " + e.getMessage()); 
            } finally {
                if (daoUpdate != null) try { daoUpdate.close(); } catch (SQLException e) { /* ignore */ }
            }
        }
    }

    private void supprimerFacture() {
        JTable t = vue.getTableFactures();
        if (hasSelection(t) && confirmerSuppression("cette facture")) {
            FactureDao dao = null;
            try {
                dao = new FactureDao();
                dao.delete(getLongValue(t, t.getSelectedRow(), 0)); 
                chargerTableFactures();
                showSuccess("Facture supprimee");
            } catch (SQLException e) { 
                showBddError(e); 
            } finally {
                if (dao != null) try { dao.close(); } catch (SQLException ex) { /* ignore */ }
            }
        }
    }
    
    private void marquerFacturePayee() {
        JTable t = vue.getTableFactures();
        if (!hasSelection(t)) {
            showInfo("Veuillez selectionner une facture a marquer comme payee");
            return;
        }
        
        int row = t.getSelectedRow();
        String statutActuel = t.getValueAt(row, 5) != null ? t.getValueAt(row, 5).toString() : "A payer";
        
        if ("Paye".equalsIgnoreCase(statutActuel)) {
            showInfo("Cette facture est deja payee");
            return;
        }
        
        if (confirmer("Marquer cette facture comme payee ?")) {
            FactureDao dao = null;
            try {
                dao = new FactureDao();
                long idFacture = getLongValue(t, row, 0);
                Facture f = dao.findById(idFacture);
                if (f != null) {
                    f.setStatut("Paye");
                    dao.update(f);
                    chargerTableFactures();
                    showSuccess("Facture marquee comme payee");
                }
            } catch (SQLException e) { 
                showBddError(e); 
            } finally {
                if (dao != null) try { dao.close(); } catch (SQLException ex) { /* ignore */ }
            }
        }
    }


    // BIENS (LOGEMENTS ET GARAGES) - LECTURE SEULE


    public void chargerTableBiens() {
        DefaultTableModel m = (DefaultTableModel) vue.getTableBiens().getModel();
        m.setRowCount(0);
        LogementDao logDao = null;
        GarageDao garDao = null;
        try {
            logDao = new LogementDao();
            List<Logement> logements = logDao.findByBatiment(idBatiment);
            for (Logement l : logements) {
                Long idBailObj = l.getIdBail();
                String statut = (idBailObj != null && idBailObj > 0) ? "Loue" : "Vacant";
                m.addRow(new Object[]{
                    l.getAdresseLogement(),
                    l.getTypeLogement(),
                    l.getSurfaceDouble(),
                    l.getNbPieces(),
                    l.getNumFiscalLong(),
                    statut,
                    "L"
                });
            }
            
            garDao = new GarageDao();
            List<Garage> garages = garDao.findByBatiment(idBatiment);
            for (Garage g : garages) {
                Long idBailObj = g.getIdBail();
                String statut = (idBailObj != null && idBailObj > 0) ? "Loue" : "Vacant";
                m.addRow(new Object[]{
                    g.getAdresseGarage(),
                    "Garage",
                    g.getSurfaceGarage(),
                    "-",
                    g.getNumFiscalLong(),
                    statut,
                    "G"
                });
            }
        } catch (SQLException ex) { 
            showBddError(ex); 
        } finally {
            if (logDao != null) try { logDao.close(); } catch (SQLException e) { /* ignore */ }
            if (garDao != null) try { garDao.close(); } catch (SQLException e) { /* ignore */ }
        }
    }


    // UTILITAIRES


    private String[] ouvrirSaisie(String titre, String[] labels, String[] valeursInitiales) {
        pageSaisieDocumentBatiment dlg = new pageSaisieDocumentBatiment(titre, labels, valeursInitiales);
        new gestionSaisieDocument(dlg);
        dlg.setLocationRelativeTo(vue);
        dlg.setVisible(true);
        return dlg.getResultat();
    }
}
