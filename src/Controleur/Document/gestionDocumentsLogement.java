package Controleur.Document;

import java.awt.BorderLayout;
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
import modele.dao.DocumentLogementDao;

import java.awt.Component;
import vue.Document.pageDocumentsLogement;
import vue.Document.pageSaisieDocumentLogement;

/**
 * Controleur pour la gestion des documents d'un logement
 * Connecte a la base de donnees Oracle
 */
public class gestionDocumentsLogement extends AbstractDocumentController implements ActionListener {

    private final pageDocumentsLogement vue;
    private DocumentLogementDao dao;
    private long idLogement;

    
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

    public gestionDocumentsLogement(pageDocumentsLogement vue) {
        this.vue = vue;
        if (vue.getLogement() != null) {
            this.idLogement = vue.getLogement().getIdLogementLong();
        }

        try {
            this.dao = new DocumentLogementDao();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vue, 
                "Erreur de connexion a la base de donnees", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     * Charge toutes les donnees depuis la BDD
     */
    
    public void chargerToutesDonnees() {
        if (idLogement <= 0) return;
        
        chargerBaux();
        chargerGarages();
        chargerGarants();
        chargerEtatDesLieux();
        chargerReleves();
        chargerAssurances();
        chargerLoyers();
        chargerQuittances();
    }


    // CHARGEMENT DES DONNEES


    private void chargerBaux() {
        try {
            DefaultTableModel model = (DefaultTableModel) vue.getTableBaux().getModel();
            model.setRowCount(0);

            List<Object[]> baux = dao.getBauxLogement(idLogement);
            for (Object[] row : baux) {
                model.addRow(new Object[]{
                    row[0],  // ID
                    row[1],  // Locataire
                    formatDate((Date) row[2]),  // Date debut
                    formatDate((Date) row[3]),  // Date fin
                    formatMontant((Double) row[4]),  // Loyer
                    formatMontant((Double) row[5]),  // Provisions
                    formatMontant((Double) row[6]),  // Caution
                    row[7]   // Etat
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerGarages() {
        try {
            DefaultTableModel model = (DefaultTableModel) vue.getTableGaragesLoues().getModel();
            model.setRowCount(0);

            List<Object[]> garages = dao.getGaragesLogement(idLogement);
            for (Object[] row : garages) {
                model.addRow(new Object[]{
                    row[0],  // ID
                    row[1],  // Adresse
                    row[2],  // Surface
                    row[3],  // N Fiscal
                    row[4]   // Assurance
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerGarants() {
        try {
            DefaultTableModel model = (DefaultTableModel) vue.getTableGarants().getModel();
            model.setRowCount(0);

            List<Object[]> garants = dao.getGarantsLogement(idLogement);
            for (Object[] row : garants) {
                model.addRow(new Object[]{
                    row[0],  // ID
                    row[1],  // Nom
                    row[2],  // Prenom
                    row[3],  // Adresse
                    row[4],  // Email
                    row[5]   // Telephone
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerEtatDesLieux() {
        try {
            DefaultTableModel model = (DefaultTableModel) vue.getTableEtatDesLieux().getModel();
            model.setRowCount(0);

            List<Object[]> edl = dao.getEtatsDesLieux(idLogement);
            for (Object[] row : edl) {
                model.addRow(new Object[]{
                    row[0],  // ID
                    row[1],  // Type
                    formatDate((Date) row[2]),  // Date
                    row[3]   // Observations
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerReleves() {
        try {
            DefaultTableModel model = (DefaultTableModel) vue.getTableReleves().getModel();
            model.setRowCount(0);

            List<Object[]> releves = dao.getRelevesCompteurs(idLogement);
            for (Object[] row : releves) {
                // DAO retourne : ID, Type, Date, AncienIndex, NouvelIndex, NumCompteur, Unite,
                //                Consommation, QuotiteEau, QuotiteElec, QuotiteOM, QuotiteEntretien
                model.addRow(new Object[]{
                    row[0],  // ID
                    row[1],  // Type
                    row[2],  // Date (deja formatee en String)
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
            e.printStackTrace();
        }
    }

    private void chargerAssurances() {
        try {
            DefaultTableModel model = (DefaultTableModel) vue.getTableAssurances().getModel();
            model.setRowCount(0);

            List<Object[]> assurances = dao.getAssurances(idLogement);
            
            SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");

            for (Object[] row : assurances) {
                String annee = "";
                if (row[4] != null) {
                    annee = sdfYear.format((Date) row[4]);
                }

                model.addRow(new Object[]{
                    row[0],                          // 0 - ID
                    annee,                           // 1 - Annee
                    row[1],                          // 2 - Compagnie
                    row[2],                          // 3 - Type
                    formatMontant((Double) row[3]),  // 4 - Prime
                    row[4],                          // 5 - Date effet
                    row[5]                           // 6 - Date echeance
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vue, 
                "Erreur lors du chargement des assurances: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chargerLoyers() {
        try {
            DefaultTableModel model = (DefaultTableModel) vue.getTableLoyers().getModel();
            model.setRowCount(0);

            List<Object[]> loyers = dao.getLoyersEnAttenteLogement(idLogement);
            for (Object[] row : loyers) {
                model.addRow(new Object[]{
                    row[0],  // ID
                    row[1],  // Periode
                    formatMontant((Double) row[2]),  // Loyer Nu
                    formatMontant((Double) row[3]),  // Charges
                    formatMontant((Double) row[4]),  // Total
                    row[5]   // Statut
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerQuittances() {
        try {
            DefaultTableModel model = (DefaultTableModel) vue.getTableQuittances().getModel();
            model.setRowCount(0);

            List<Object[]> quittances = dao.getQuittancesLogement(idLogement);
            for (Object[] row : quittances) {
                model.addRow(new Object[]{
                    row[0],  // ID
                    row[1],  // Periode
                    formatMontant((Double) row[2]),  // Loyer Nu
                    formatMontant((Double) row[3]),  // Charges
                    formatMontant((Double) row[4]),  // Total
                    formatDate((Date) row[5]),       // Date Paiement
                    row[6]   // N Quittance
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // GESTION DES ACTIONS


    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "FERMER_DOCUMENTS":
                if (dao != null) {
                    try { dao.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                }
                vue.dispose();
                break;

            // GARAGES LOUES
            case "AJOUTER_GARAGE_LOUE":
                ajouterGarageLoue();
                break;
            case "SUPPRIMER_GARAGE_LOUE":
                supprimerGarageLoue();
                break;

            // GARANTS 
            case "AJOUTER_GARANT":
                ajouterGarant();
                break;
            case "MODIFIER_GARANT":
                modifierGarant();
                break;
            case "SUPPRIMER_GARANT":
                supprimerGarant();
                break;

            // ETATS DES LIEUX 
            case "AJOUTER_EDL":
                ajouterEtatDesLieux();
                break;
            case "MODIFIER_EDL":
                modifierEtatDesLieux();
                break;
            case "SUPPRIMER_EDL":
                supprimerEtatDesLieux();
                break;

            // RELEVES 
            case "AJOUTER_RELEVE":
                ajouterReleve();
                break;
            case "MODIFIER_RELEVE":
                modifierReleve();
                break;
            case "SUPPRIMER_RELEVE":
                supprimerReleve();
                break;

            //ASSURANCES 
            case "AJOUTER_ASSURANCE":
                ajouterAssurance();
                break;
            case "MODIFIER_ASSURANCE":
                modifierAssurance();
                break;
            case "SUPPRIMER_ASSURANCE":
                supprimerAssurance();
                break;

            // LOYERS (en attente)
            case "AJOUTER_LOYER":
                marquerLoyerPaye();
                break;

            //  QUITTANCES (loyers payes) 
            case "SUPPRIMER_QUITTANCE":
                annulerPaiement();
                break;

            default:
                break;
        }
    }


    // GARANTS - CRUD


    private void ajouterGarant() {
        String[] labels = {"Nom", "Prenom", "Adresse", "Email", "Telephone"};
        String[] res = ouvrirSaisie("Ajouter un garant", labels, null);

        if (res != null) {
            try {
                dao.ajouterGarant(idLogement, res[0], res[1], res[2], res[3], res[4]);
                chargerGarants();
                JOptionPane.showMessageDialog(vue, "Garant ajoute avec succes");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(), 
                    "Erreur BDD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifierGarant() {
        JTable table = vue.getTableGarants();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(vue, "Veuillez selectionner un garant");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        String idGarant = model.getValueAt(row, 0).toString();

        String[] labels = {"Nom", "Prenom", "Adresse", "Email", "Telephone"};
        String[] init = {
            str(model.getValueAt(row, 1)),
            str(model.getValueAt(row, 2)),
            str(model.getValueAt(row, 3)),
            str(model.getValueAt(row, 4)),
            str(model.getValueAt(row, 5))
        };

        String[] res = ouvrirSaisie("Modifier garant", labels, init);

        if (res != null) {
            try {
                dao.modifierGarant(idGarant, res[0], res[1], res[2], res[3], res[4]);
                chargerGarants();
                JOptionPane.showMessageDialog(vue, "Garant modifie avec succes");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(), 
                    "Erreur BDD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerGarant() {
        JTable table = vue.getTableGarants();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(vue, "Veuillez selectionner un garant");
            return;
        }

        if (confirmerSuppression("garant")) {
            try {
                String idGarant = ((DefaultTableModel) table.getModel()).getValueAt(row, 0).toString();
                dao.supprimerGarant(idGarant);
                chargerGarants();
                JOptionPane.showMessageDialog(vue, "Garant supprime");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(), 
                    "Erreur BDD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // ETATS DES LIEUX - CRUD


    private void ajouterEtatDesLieux() {
        String[] labels = {"Type (Entree/Sortie)", "Date (dd/MM/yyyy)", "Observations"};
        String[] res = ouvrirSaisie("Ajouter un etat des lieux", labels, null);

        if (res != null) {
            try {
                Date date = sdf.parse(res[1]);
                dao.ajouterEtatDesLieux(idLogement, res[0], date, res[2]);
                chargerEtatDesLieux();
                JOptionPane.showMessageDialog(vue, "Etat des lieux ajoute avec succes");
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(vue, "Format de date invalide", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(), 
                    "Erreur BDD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifierEtatDesLieux() {
        JTable table = vue.getTableEtatDesLieux();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(vue, "Veuillez selectionner un etat des lieux");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        long idEdl = Long.parseLong(model.getValueAt(row, 0).toString());

        String[] labels = {"Type", "Date", "Observations"};
        String[] init = {
            str(model.getValueAt(row, 1)),
            str(model.getValueAt(row, 2)),
            str(model.getValueAt(row, 3))
        };

        String[] res = ouvrirSaisie("Modifier etat des lieux", labels, init);

        if (res != null) {
            try {
                Date date = sdf.parse(res[1]);
                dao.modifierEtatDesLieux(idEdl, res[0], date, res[2]);
                chargerEtatDesLieux();
                JOptionPane.showMessageDialog(vue, "Etat des lieux modifie avec succes");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerEtatDesLieux() {
        JTable table = vue.getTableEtatDesLieux();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(vue, "Veuillez selectionner un etat des lieux");
            return;
        }

        if (confirmerSuppression("etat des lieux")) {
            try {
                long idEdl = Long.parseLong(((DefaultTableModel) table.getModel()).getValueAt(row, 0).toString());
                dao.supprimerEtatDesLieux(idEdl);
                chargerEtatDesLieux();
                JOptionPane.showMessageDialog(vue, "Etat des lieux supprime");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(), 
                    "Erreur BDD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // RELEVES - CRUD avec JComboBox


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
                
                dao.ajouterReleveCompteur(idLogement, type, date, numCompteur, ancienIndex, nouvelIndex, unite,
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
            JOptionPane.showMessageDialog(vue, "Releve ajoute avec succes");
        }
    }
    
    /**
     * Parse une quotite (pourcentage) depuis une String
     */
    private double parseQuotite(String str) {
        try {
            String cleaned = str.replace("%", "").replace(",", ".").trim();
            double val = Double.parseDouble(cleaned);
            return Math.max(0, Math.min(100, val)); // Entre 0 et 100
        } catch (NumberFormatException e) {
            return 100; // Valeur par defaut
        }
    }

    private void modifierReleve() {
        JTable table = vue.getTableReleves();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(vue, "Veuillez selectionner un releve");
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
        
        // Utiliser Window au lieu de JFrame
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
                
                dao.modifierReleveCompteur(idReleve, type, date, numCompteur, ancienIndex, nouvelIndex, unite,
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
            JOptionPane.showMessageDialog(vue, "Releve modifie avec succes");
        }
    }

    private void supprimerReleve() {
        JTable table = vue.getTableReleves();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(vue, "Veuillez selectionner un releve");
            return;
        }

        if (confirmerSuppression("releve")) {
            try {
                long idReleve = Long.parseLong(((DefaultTableModel) table.getModel()).getValueAt(row, 0).toString());
                dao.supprimerReleveCompteur(idReleve);
                chargerReleves();
                JOptionPane.showMessageDialog(vue, "Releve supprime");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(), 
                    "Erreur BDD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // QUITTANCES - Marquer paye / Annuler


    private void marquerLoyerPaye() {
        JTable table = vue.getTableLoyers();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(vue, 
                "Veuillez d'abord selectionner un loyer dans le tableau\n" +
                "en cliquant sur la ligne correspondante.",
                "Aucun loyer selectionne",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] labels = {"Date de paiement (dd/MM/yyyy)"};
        String[] defaut = {sdf.format(new Date())};
        String[] res = ouvrirSaisie("Marquer comme paye", labels, defaut);

        if (res != null) {
            try {
                Date datePaiement = sdf.parse(res[0]);
                long idLoyer = Long.parseLong(((DefaultTableModel) table.getModel()).getValueAt(row, 0).toString());

                dao.marquerLoyerPaye(idLoyer, datePaiement);
                
                chargerLoyers();
                chargerQuittances();
                
                vue.getTableLoyers().updateUI();
                vue.getTableQuittances().updateUI();
                
                JOptionPane.showMessageDialog(vue, 
                    "Loyer marque comme paye avec succes !\n" +
                    "Une quittance a ete generee dans l'onglet 'Quittances'.");
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(vue, "Format de date invalide", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(), 
                    "Erreur BDD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void annulerPaiement() {
        JTable table = vue.getTableQuittances();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(vue, "Veuillez selectionner une quittance");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(vue, 
            "Voulez-vous vraiment annuler ce paiement ?\n" +
            "Le loyer repassera en 'En attente'.", 
            "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                long idLoyer = Long.parseLong(((DefaultTableModel) table.getModel()).getValueAt(row, 0).toString());
                dao.annulerPaiement(idLoyer);
                
                chargerLoyers();
                chargerQuittances();
                
                vue.getTableLoyers().updateUI();
                vue.getTableQuittances().updateUI();
                
                JOptionPane.showMessageDialog(vue, 
                    "Paiement annule avec succes !\n" +
                    "Le loyer est de nouveau 'En attente' dans l'onglet 'Loyers'.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(), 
                    "Erreur BDD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // ASSURANCES - CRUD


    private void ajouterAssurance() {
        String[] labels = {
            "N Assurance", 
            "Nom de la compagnie (ex: AXA, MAAF, Allianz...)", 
            "Type (Habitation/PNO/Proprietaire...)", 
            "Prime annuelle TTC (EUR)", 
            "Date d'effet (debut, dd/MM/yyyy)",
            "Date d'echeance annuelle (renouvellement, dd/MM/yyyy)"
        };
        String[] res = ouvrirSaisie("Ajouter une assurance annuelle", labels, null);

        if (res != null) {
            try {
                long numAssurance = Long.parseLong(res[0]);
                String nomCompagnie = res[1];
                
                if (nomCompagnie == null || nomCompagnie.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(vue, 
                        "Le nom de la compagnie est obligatoire", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String type = res[2];
                double primeTTC = Double.parseDouble(res[3].replace(",", "."));
                Date dateEffet = sdf.parse(res[4]);
                Date dateEcheance = sdf.parse(res[5]);

                dao.ajouterAssurance(idLogement, numAssurance, nomCompagnie, type, primeTTC, dateEffet, dateEcheance);
                chargerAssurances();
                JOptionPane.showMessageDialog(vue, 
                    "Assurance annuelle ajoutee avec succes !\n" +
                    "Compagnie: " + nomCompagnie + "\n" +
                    "Date d'effet: " + sdf.format(dateEffet) + "\n" +
                    "Echeance: " + sdf.format(dateEcheance));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vue, 
                    "Erreur: Les champs numeriques doivent contenir des nombres valides", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(vue, 
                    "Format de date invalide. Utilisez dd/MM/yyyy", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur BDD: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifierAssurance() {
        JTable table = vue.getTableAssurances();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(vue, "Veuillez selectionner une assurance");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        long numAssurance = Long.parseLong(model.getValueAt(row, 0).toString());

        String nomActuel = str(model.getValueAt(row, 2));
        String typeActuel = str(model.getValueAt(row, 3));
        String primeActuelle = str(model.getValueAt(row, 4)).replace(" EUR", "").replace(",", ".");
        
        Date dateEffetActuelle = (Date) model.getValueAt(row, 5);
        Date dateEcheanceActuelle = (Date) model.getValueAt(row, 6);

        String[] labels = {
            "Nom de la compagnie", 
            "Type", 
            "Prime annuelle TTC (EUR)",
            "Date d'effet (dd/MM/yyyy)",
            "Date d'echeance (dd/MM/yyyy)"
        };
        String[] init = {
            nomActuel,
            typeActuel,
            primeActuelle,
            dateEffetActuelle != null ? sdf.format(dateEffetActuelle) : "",
            dateEcheanceActuelle != null ? sdf.format(dateEcheanceActuelle) : ""
        };

        String[] res = ouvrirSaisie("Modifier assurance", labels, init);

        if (res != null) {
            try {
                String nomCompagnie = res[0];
                
                if (nomCompagnie == null || nomCompagnie.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(vue, 
                        "Le nom de la compagnie est obligatoire", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String type = res[1];
                double primeTTC = Double.parseDouble(res[2].replace(",", "."));
                Date dateEffet = sdf.parse(res[3]);
                Date dateEcheance = sdf.parse(res[4]);

                dao.modifierAssurance(numAssurance, nomCompagnie, type, primeTTC, dateEffet, dateEcheance);
                chargerAssurances();
                JOptionPane.showMessageDialog(vue, "Assurance modifiee avec succes");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vue, 
                    "Erreur: Montant invalide", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(vue, 
                    "Format de date invalide. Utilisez dd/MM/yyyy", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur BDD: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerAssurance() {
        JTable table = vue.getTableAssurances();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(vue, "Veuillez selectionner une assurance");
            return;
        }

        if (confirmerSuppression("assurance")) {
            try {
                long numAssurance = Long.parseLong(((DefaultTableModel) table.getModel()).getValueAt(row, 0).toString());
                dao.supprimerAssurance(numAssurance);
                chargerAssurances();
                JOptionPane.showMessageDialog(vue, "Assurance supprimee");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(), 
                    "Erreur BDD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // GARAGES LOUES - Lier/Delier


    private void ajouterGarageLoue() {
        String[] labels = {"ID du garage a lier a ce logement :"};
        String[] res = ouvrirSaisie("Lier un garage au logement", labels, null);

        if (res != null) {
            try {
                long idGarage = Long.parseLong(res[0]);
                dao.lierGarageAuLogement(idGarage, idLogement);
                chargerGarages();
                JOptionPane.showMessageDialog(vue, "Garage lie au logement avec succes");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vue, "L'ID doit etre un nombre",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(),
                    "Erreur BDD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerGarageLoue() {
        JTable table = vue.getTableGaragesLoues();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(vue, "Veuillez selectionner un garage");
            return;
        }

        if (confirmerSuppression("lien avec ce garage")) {
            try {
                long idGarage = Long.parseLong(((DefaultTableModel) table.getModel()).getValueAt(row, 0).toString());
                dao.delierGarageDuLogement(idGarage);
                chargerGarages();
                JOptionPane.showMessageDialog(vue, "Garage delie du logement");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(),
                    "Erreur BDD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // UTILITAIRES


    private String[] ouvrirSaisie(String titre, String[] labels, String[] valeursInitiales) {
        pageSaisieDocumentLogement dlg = new pageSaisieDocumentLogement(titre, labels, valeursInitiales);
        new gestionSaisieDocument(dlg);
        dlg.setLocationRelativeTo(vue);
        dlg.setVisible(true);
        return dlg.getResultat();
    }

    // Méthodes utilitaires héritées de AbstractDocumentController :
    // - formatDate(), formatMontant(), str(), confirmerSuppression()
    
    @Override
    protected Component getVue() {
        return vue;
    }
}
