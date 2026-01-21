package vue.Document;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Formulaire de modification de facture
 */
public class pageSaisieModificationFacture extends JDialog {

    private static final long serialVersionUID = 1L;
    
    private static final String[] STATUTS = {"A payer", "Paye"};

    // Champs du formulaire
    private JTextField txtDate;
    private JTextField txtNature;
    private JTextField txtTravaux;
    private JTextField txtMontantTTC;
    private JComboBox<EntrepriseItem> comboEntreprise;
    private JComboBox<String> comboStatut;
    private JLabel lblInfoDevis;

    // Données
    private long idFacture;
    private long siretOriginal;
    private boolean aUnDevis;
    private List<EntrepriseItem> entreprises = new ArrayList<>();
    
    // Résultat
    private boolean valide = false;
    private String[] resultat = null;

    public pageSaisieModificationFacture(long idFacture, String date, String nature, 
            String travaux, String montantTTC, long siretActuel, String statutActuel,
            boolean aUnDevis, List<EntrepriseItem> entreprises) {
        
        this.setTitle("Modifier Facture #" + idFacture);
        this.setModal(true);
        this.setSize(500, 350);
        this.setLocationRelativeTo(null);
        
        this.idFacture = idFacture;
        this.siretOriginal = siretActuel;
        this.aUnDevis = aUnDevis;
        this.entreprises = entreprises;

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel formulaire
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        // Date
        formPanel.add(new JLabel("Date (jj/mm/aaaa) :"));
        this.txtDate = new JTextField(date);
        formPanel.add(this.txtDate);

        // Nature
        formPanel.add(new JLabel("Nature :"));
        this.txtNature = new JTextField(nature);
        formPanel.add(this.txtNature);

        // Travaux
        formPanel.add(new JLabel("Travaux :"));
        this.txtTravaux = new JTextField(travaux != null ? travaux : "");
        formPanel.add(this.txtTravaux);

        // Montant TTC
        formPanel.add(new JLabel("Montant TTC :"));
        this.txtMontantTTC = new JTextField(montantTTC);
        formPanel.add(this.txtMontantTTC);

        // Entreprise 
        formPanel.add(new JLabel("Entreprise :"));
        this.comboEntreprise = new JComboBox<>();
        int selectedIndex = 0;
        int i = 0;
        for (EntrepriseItem ent : entreprises) {
            this.comboEntreprise.addItem(ent);
            if (ent.getSiret() == siretActuel) {
                selectedIndex = i;
            }
            i++;
        }
        this.comboEntreprise.setSelectedIndex(selectedIndex);
        formPanel.add(this.comboEntreprise);

        // Info devis
        formPanel.add(new JLabel(""));
        this.lblInfoDevis = new JLabel();
        if (aUnDevis) {
            this.lblInfoDevis.setText("⚠ Facture avec devis : entreprise verrouillée");
            this.lblInfoDevis.setForeground(new Color(180, 100, 0));
        } else {
            this.lblInfoDevis.setText("✓ Pas de devis : entreprise modifiable");
            this.lblInfoDevis.setForeground(new Color(0, 128, 0));
        }
        formPanel.add(this.lblInfoDevis);

        // Statut
        formPanel.add(new JLabel("Statut :"));
        this.comboStatut = new JComboBox<>(STATUTS);
        if (statutActuel != null) {
            for (int j = 0; j < STATUTS.length; j++) {
                if (STATUTS[j].equalsIgnoreCase(statutActuel)) {
                    this.comboStatut.setSelectedIndex(j);
                    break;
                }
            }
        }
        formPanel.add(this.comboStatut);

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
     * Valide le formulaire
     */
    private void valider() {
        // Récupérer l'entreprise sélectionnée
        EntrepriseItem entrepriseSelectionnee = (EntrepriseItem) comboEntreprise.getSelectedItem();
        long nouveauSiret = entrepriseSelectionnee.getSiret();
        
        if (aUnDevis && nouveauSiret != siretOriginal) {
            JOptionPane.showMessageDialog(this,
                    "RÈGLE 12 VIOLÉE :\n\n" +
                    "Cette facture est associée à un devis.\n" +
                    "L'entreprise ne peut pas être modifiée.\n\n" +
                    "Si vous devez changer d'entreprise, créez une nouvelle facture.",
                    "Modification interdite",
                    JOptionPane.ERROR_MESSAGE);
            
            // Remettre l'entreprise originale
            for (int i = 0; i < comboEntreprise.getItemCount(); i++) {
                if (comboEntreprise.getItemAt(i).getSiret() == siretOriginal) {
                    comboEntreprise.setSelectedIndex(i);
                    break;
                }
            }
            return;
        }
        
        // Créer le résultat
        this.resultat = new String[] {
            txtDate.getText().trim(),
            txtNature.getText().trim(),
            txtTravaux.getText().trim(),
            txtMontantTTC.getText().trim(),
            String.valueOf(nouveauSiret),
            (String) comboStatut.getSelectedItem()
        };
        
        this.valide = true;
        this.dispose();
    }

    private void annuler() {
        this.valide = false;
        this.resultat = null;
        this.dispose();
    }

    public boolean isValide() {
        return this.valide;
    }

    public String[] getResultat() {
        return this.resultat;
    }
    
    public long getNouveauSiret() {
        if (resultat != null && resultat.length > 4) {
            return Long.parseLong(resultat[4]);
        }
        return siretOriginal;
    }

    // Classe interne pour les items de la combo
    
    public static class EntrepriseItem {
        private final long siret;
        private final String nom;

        public EntrepriseItem(long siret, String nom) {
            this.siret = siret;
            this.nom = nom;
        }

        public long getSiret() {
            return siret;
        }

        public String getNom() {
            return nom;
        }

        @Override
        public String toString() {
            return nom + " (" + siret + ")";
        }
    }
}