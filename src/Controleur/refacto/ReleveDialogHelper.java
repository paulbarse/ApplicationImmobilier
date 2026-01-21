package Controleur.refacto;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Component;

/**
 * Classe utilitaire pour créer les dialogues de relevés compteur
 */
public class ReleveDialogHelper {


    private static SimpleDateFormat createDateFormat() {
        return new SimpleDateFormat("dd/MM/yyyy");
    }
    
    // Types de compteur/charges disponibles
    public static final String[] TYPES_COMPTEUR = {
        "Eau", 
        "Gaz", 
        "Electricite", 
        "Ordures menageres", 
        "Entretien"
    };
    
    // Unites de mesure disponibles
    public static final String[] UNITES_COMPTEUR = {
        "m3",       // Eau, Gaz
        "kWh",      // Electricite
        "L",        // Eau (litres)
        "unite",    // Comptage unitaire
        "forfait"   // Forfait (OM, Entretien)
    };

    /**
     * Interface fonctionnelle pour la sauvegarde des relevés
     */
    @FunctionalInterface
    public interface ReleveSaveHandler {
        void save(long idEntite, String type, Date date, long numCompteur, 
                  long ancienIndex, long nouvelIndex, String unite,
                  double quotiteEau, double quotiteElec, double quotiteOM, double quotiteEntretien) 
            throws SQLException;
    }

    /**
     * Interface fonctionnelle pour la modification des relevés
     */
    @FunctionalInterface
    public interface ReleveUpdateHandler {
        void update(long idReleve, String type, Date date, long numCompteur, 
                   long ancienIndex, long nouvelIndex, String unite,
                   double quotiteEau, double quotiteElec, double quotiteOM, double quotiteEntretien) 
            throws SQLException;
    }

    /**
     * Données d'un relevé pour pré-remplir le formulaire de modification
     */
    public static class ReleveData {
        public final String type;
        public final String date;
        public final String ancienIndex;
        public final String nouvelIndex;
        public final String numCompteur;
        public final String unite;
        public final String quotiteEau;
        public final String quotiteElec;
        public final String quotiteOM;
        public final String quotiteEntretien;

        public ReleveData(String type, String date, String ancienIndex, String nouvelIndex,
                         String numCompteur, String unite, String quotiteEau, String quotiteElec,
                         String quotiteOM, String quotiteEntretien) {
            this.type = type;
            this.date = date;
            this.ancienIndex = ancienIndex;
            this.nouvelIndex = nouvelIndex;
            this.numCompteur = numCompteur;
            this.unite = unite;
            this.quotiteEau = quotiteEau.replace("%", "");
            this.quotiteElec = quotiteElec.replace("%", "");
            this.quotiteOM = quotiteOM.replace("%", "");
            this.quotiteEntretien = quotiteEntretien.replace("%", "");
        }
    }

    /**
     * Ouvre un dialogue pour ajouter un relevé compteur
     */
    public static boolean ouvrirDialogueAjoutReleve(Component parent, long idEntite, 
            ReleveSaveHandler saveHandler) {
        
        Window parentWindow = SwingUtilities.getWindowAncestor(parent);
        
        JDialog dlg = new JDialog(parentWindow, "Ajouter un releve compteur", 
                                  Dialog.ModalityType.APPLICATION_MODAL);
        
        FormComponents form = createFormComponents(null);
        setupDialog(dlg, form, parent);
        
        final boolean[] validated = {false};
        
        form.btnValider.addActionListener(e -> {
            try {
                ReleveFormData data = validateAndExtractData(form, dlg);
                if (data != null) {
                    saveHandler.save(idEntite, data.type, data.date, data.numCompteur,
                            data.ancienIndex, data.nouvelIndex, data.unite,
                            data.quotiteEau, data.quotiteElec, data.quotiteOM, data.quotiteEntretien);
                    validated[0] = true;
                    dlg.dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg, 
                    "Erreur BDD: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        form.btnAnnuler.addActionListener(e -> dlg.dispose());
        
        dlg.setVisible(true);
        
        return validated[0];
    }

    /**
     * Ouvre un dialogue pour modifier un relevé compteur
     */
    public static boolean ouvrirDialogueModificationReleve(Component parent, long idReleve,
            ReleveData currentData, ReleveUpdateHandler updateHandler) {
        
        Window parentWindow = SwingUtilities.getWindowAncestor(parent);
        
        JDialog dlg = new JDialog(parentWindow, "Modifier un releve compteur", 
                                  Dialog.ModalityType.APPLICATION_MODAL);
        
        FormComponents form = createFormComponents(currentData);
        setupDialog(dlg, form, parent);
        
        final boolean[] validated = {false};
        
        form.btnValider.addActionListener(e -> {
            try {
                ReleveFormData data = validateAndExtractData(form, dlg);
                if (data != null) {
                    updateHandler.update(idReleve, data.type, data.date, data.numCompteur,
                            data.ancienIndex, data.nouvelIndex, data.unite,
                            data.quotiteEau, data.quotiteElec, data.quotiteOM, data.quotiteEntretien);
                    validated[0] = true;
                    dlg.dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg, 
                    "Erreur BDD: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        form.btnAnnuler.addActionListener(e -> dlg.dispose());
        
        dlg.setVisible(true);
        
        return validated[0];
    }

    /**
     * Crée les composants du formulaire
     */
    private static FormComponents createFormComponents(ReleveData initialData) {
        FormComponents form = new FormComponents();
        SimpleDateFormat sdf = createDateFormat(); // Instance locale thread-safe
        
        form.cbType = new JComboBox<>(TYPES_COMPTEUR);
        form.txtDate = new JTextField(initialData != null ? initialData.date : sdf.format(new Date()));
        form.txtAncienIndex = new JTextField(initialData != null ? initialData.ancienIndex : "0");
        form.txtNouvelIndex = new JTextField(initialData != null ? initialData.nouvelIndex : "");
        form.txtNumCompteur = new JTextField(initialData != null ? initialData.numCompteur : "");
        form.cbUnite = new JComboBox<>(UNITES_COMPTEUR);
        form.txtQuotiteEau = new JTextField(initialData != null ? initialData.quotiteEau : "100");
        form.txtQuotiteElec = new JTextField(initialData != null ? initialData.quotiteElec : "100");
        form.txtQuotiteOM = new JTextField(initialData != null ? initialData.quotiteOM : "100");
        form.txtQuotiteEntretien = new JTextField(initialData != null ? initialData.quotiteEntretien : "100");
        form.btnValider = new JButton("Valider");
        form.btnAnnuler = new JButton("Annuler");
        
        if (initialData != null) {
            form.cbType.setSelectedItem(initialData.type);
            form.cbUnite.setSelectedItem(initialData.unite);
        }
        
        return form;
    }

    /**
     * Configure le dialogue avec les composants du formulaire
     */
    private static void setupDialog(JDialog dlg, FormComponents form, Component parent) {
        dlg.setLayout(new BorderLayout(10, 10));
        dlg.setSize(500, 450);
        dlg.setLocationRelativeTo(parent);
        
        JPanel formPanel = new JPanel(new GridLayout(11, 2, 10, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        formPanel.add(new JLabel("Type :"));
        formPanel.add(form.cbType);
        
        formPanel.add(new JLabel("Date (dd/MM/yyyy) :"));
        formPanel.add(form.txtDate);
        
        formPanel.add(new JLabel("Ancien Index :"));
        formPanel.add(form.txtAncienIndex);
        
        formPanel.add(new JLabel("Nouvel Index :"));
        formPanel.add(form.txtNouvelIndex);
        
        formPanel.add(new JLabel("N Compteur :"));
        formPanel.add(form.txtNumCompteur);
        
        formPanel.add(new JLabel("Unite :"));
        formPanel.add(form.cbUnite);
        
        formPanel.add(new JLabel("--- Quotites (%) ---"));
        formPanel.add(new JLabel(""));
        
        formPanel.add(new JLabel("Quotite Eau (%) :"));
        formPanel.add(form.txtQuotiteEau);
        
        formPanel.add(new JLabel("Quotite Electricite (%) :"));
        formPanel.add(form.txtQuotiteElec);
        
        formPanel.add(new JLabel("Quotite Ordures M. (%) :"));
        formPanel.add(form.txtQuotiteOM);
        
        formPanel.add(new JLabel("Quotite Entretien (%) :"));
        formPanel.add(form.txtQuotiteEntretien);
        
        dlg.add(formPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(form.btnValider);
        btnPanel.add(form.btnAnnuler);
        dlg.add(btnPanel, BorderLayout.SOUTH);
    }

    /**
     * Valide et extrait les données du formulaire
     */
    private static ReleveFormData validateAndExtractData(FormComponents form, JDialog dlg) {
        SimpleDateFormat sdf = createDateFormat(); // Instance locale thread-safe
        
        try {
            String dateStr = form.txtDate.getText().trim();
            String ancienIndexStr = form.txtAncienIndex.getText().trim();
            String nouvelIndexStr = form.txtNouvelIndex.getText().trim();
            String numCompteurStr = form.txtNumCompteur.getText().trim();
            
            // Validation des champs obligatoires
            if (dateStr.isEmpty() || ancienIndexStr.isEmpty() || 
                nouvelIndexStr.isEmpty() || numCompteurStr.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, 
                    "Tous les champs sont obligatoires.",
                    "Erreur", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            
            Date date = sdf.parse(dateStr);
            long ancienIndex = Long.parseLong(ancienIndexStr);
            long nouvelIndex = Long.parseLong(nouvelIndexStr);
            long numCompteur = Long.parseLong(numCompteurStr);
            
            // Validation: Nouvel index >= Ancien index
            if (nouvelIndex < ancienIndex) {
                JOptionPane.showMessageDialog(dlg, 
                    "Le nouvel index doit etre superieur ou egal a l'ancien index.\n\n" +
                    "Ancien index: " + ancienIndex + "\n" +
                    "Nouvel index: " + nouvelIndex,
                    "Erreur de saisie", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            
            // Quotites
            double quotiteEau = parseQuotite(form.txtQuotiteEau.getText());
            double quotiteElec = parseQuotite(form.txtQuotiteElec.getText());
            double quotiteOM = parseQuotite(form.txtQuotiteOM.getText());
            double quotiteEntretien = parseQuotite(form.txtQuotiteEntretien.getText());
            
            String type = (String) form.cbType.getSelectedItem();
            String unite = (String) form.cbUnite.getSelectedItem();
            
            return new ReleveFormData(type, date, numCompteur, ancienIndex, nouvelIndex, unite,
                    quotiteEau, quotiteElec, quotiteOM, quotiteEntretien);
            
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(dlg, 
                "Format de date invalide. Utilisez dd/MM/yyyy",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dlg, 
                "Les index et le numero de compteur doivent etre des nombres entiers.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Parse une quotite (pourcentage) depuis une String
     */
    public static double parseQuotite(String str) {
        try {
            String cleaned = str.replace("%", "").replace(",", ".").trim();
            double val = Double.parseDouble(cleaned);
            return Math.max(0, Math.min(100, val)); // Entre 0 et 100
        } catch (NumberFormatException e) {
            return 100; // Valeur par defaut
        }
    }

    /**
     * Classe interne pour les composants du formulaire
     */
    private static class FormComponents {
        JComboBox<String> cbType;
        JTextField txtDate;
        JTextField txtAncienIndex;
        JTextField txtNouvelIndex;
        JTextField txtNumCompteur;
        JComboBox<String> cbUnite;
        JTextField txtQuotiteEau;
        JTextField txtQuotiteElec;
        JTextField txtQuotiteOM;
        JTextField txtQuotiteEntretien;
        JButton btnValider;
        JButton btnAnnuler;
    }

    /**
     * Classe interne pour les données du formulaire validées
     */
    private static class ReleveFormData {
        final String type;
        final Date date;
        final long numCompteur;
        final long ancienIndex;
        final long nouvelIndex;
        final String unite;
        final double quotiteEau;
        final double quotiteElec;
        final double quotiteOM;
        final double quotiteEntretien;

        ReleveFormData(String type, Date date, long numCompteur, long ancienIndex, long nouvelIndex,
                      String unite, double quotiteEau, double quotiteElec, double quotiteOM, double quotiteEntretien) {
            this.type = type;
            this.date = date;
            this.numCompteur = numCompteur;
            this.ancienIndex = ancienIndex;
            this.nouvelIndex = nouvelIndex;
            this.unite = unite;
            this.quotiteEau = quotiteEau;
            this.quotiteElec = quotiteElec;
            this.quotiteOM = quotiteOM;
            this.quotiteEntretien = quotiteEntretien;
        }
    }
}