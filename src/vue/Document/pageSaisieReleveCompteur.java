package vue.Document;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;

/**
 * Formulaire de saisie pour un relevé de compteur
 */
public class pageSaisieReleveCompteur extends JDialog {

    private static final long serialVersionUID = 1L;

    // Types de compteur disponibles
    private static final String[] TYPES_COMPTEUR = {
        "EAU", "ELECTRICITE", "GAZ"
    };
    
    // Unités disponibles
    private static final String[] UNITES = {
        "m³",      // Eau et Gaz
        "kWh",     // Electricité
        "L",       // Litres (eau)
        "MWh"      // Mégawattheure
    };

    private JComboBox<String> comboType;
    private JSpinner spinnerDate;
    private JSpinner spinnerAncienIndex;
    private JSpinner spinnerNouvelIndex;
    private JTextField txtNumCompteur;
    private JComboBox<String> comboUnite;

    private String[] resultat = null;
    private boolean modeModification = false;

    public pageSaisieReleveCompteur(String titre) {
        this.setTitle(titre);
        this.setModal(true);
        this.setSize(400, 350);
        this.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel formulaire
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        // Type (dropdown)
        formPanel.add(new JLabel("Type * :"));
        this.comboType = new JComboBox<>(TYPES_COMPTEUR);
        formPanel.add(this.comboType);

        // Date
        formPanel.add(new JLabel("Date * :"));
        this.spinnerDate = new JSpinner(new SpinnerDateModel());
        this.spinnerDate.setEditor(new JSpinner.DateEditor(spinnerDate, "dd/MM/yyyy"));
        formPanel.add(this.spinnerDate);

        // Ancien index
        formPanel.add(new JLabel("Ancien Index * :"));
        this.spinnerAncienIndex = new JSpinner(new SpinnerNumberModel(0L, 0L, 999999999L, 1L));
        formPanel.add(this.spinnerAncienIndex);

        // Nouvel index
        formPanel.add(new JLabel("Nouvel Index * :"));
        this.spinnerNouvelIndex = new JSpinner(new SpinnerNumberModel(0L, 0L, 999999999L, 1L));
        formPanel.add(this.spinnerNouvelIndex);

        // N° Compteur
        formPanel.add(new JLabel("N° Compteur :"));
        this.txtNumCompteur = new JTextField();
        formPanel.add(this.txtNumCompteur);

        // Unité (dropdown)
        formPanel.add(new JLabel("Unité :"));
        this.comboUnite = new JComboBox<>(UNITES);
        formPanel.add(this.comboUnite);

        // Auto-sélection de l'unité selon le type
        this.comboType.addActionListener(e -> {
            String type = (String) comboType.getSelectedItem();
            if ("EAU".equals(type)) {
                comboUnite.setSelectedItem("m³");
            } else if ("ELECTRICITE".equals(type)) {
                comboUnite.setSelectedItem("kWh");
            } else if ("GAZ".equals(type)) {
                comboUnite.setSelectedItem("m³");
            }
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Panel boutons
        JPanel buttonPanel = new JPanel();
        JButton btnValider = new JButton("Valider");
        JButton btnAnnuler = new JButton("Annuler");

        btnValider.addActionListener(e -> valider());
        btnAnnuler.addActionListener(e -> annuler());

        buttonPanel.add(btnValider);
        buttonPanel.add(btnAnnuler);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.setContentPane(mainPanel);
    }

    /**
     * Remplit le formulaire avec des valeurs existantes (pour modification)
     */
    public void remplir(String type, String date, String ancienIndex, String nouvelIndex, 
                        String numCompteur, String unite) {
        this.modeModification = true;
        
        // Type
        if (type != null) {
            for (int i = 0; i < TYPES_COMPTEUR.length; i++) {
                if (TYPES_COMPTEUR[i].equalsIgnoreCase(type)) {
                    comboType.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        // Date
        if (date != null && !date.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date d = sdf.parse(date);
                spinnerDate.setValue(d);
            } catch (Exception e) {
                
            }
        }
        
        // Ancien index
        if (ancienIndex != null && !ancienIndex.isEmpty()) {
            try {
                spinnerAncienIndex.setValue(Long.parseLong(ancienIndex));
            } catch (NumberFormatException e) {
                
            }
        }
        
        // Nouvel index
        if (nouvelIndex != null && !nouvelIndex.isEmpty()) {
            try {
                spinnerNouvelIndex.setValue(Long.parseLong(nouvelIndex));
            } catch (NumberFormatException e) {
              
            }
        }
        
        // N° Compteur
        if (numCompteur != null) {
            txtNumCompteur.setText(numCompteur);
        }
        
        // Unité
        if (unite != null) {
            for (int i = 0; i < UNITES.length; i++) {
                if (UNITES[i].equalsIgnoreCase(unite)) {
                    comboUnite.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void valider() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        String type = (String) comboType.getSelectedItem();
        String date = sdf.format((Date) spinnerDate.getValue());
        String ancienIndex = String.valueOf(spinnerAncienIndex.getValue());
        String nouvelIndex = String.valueOf(spinnerNouvelIndex.getValue());
        String numCompteur = txtNumCompteur.getText().trim();
        String unite = (String) comboUnite.getSelectedItem();
        
        this.resultat = new String[] {
            type, date, ancienIndex, nouvelIndex, numCompteur, unite
        };
        
        this.dispose();
    }

    private void annuler() {
        this.resultat = null;
        this.dispose();
    }

    public String[] getResultat() {
        return this.resultat;
    }
    
    public boolean isModeModification() {
        return this.modeModification;
    }
}