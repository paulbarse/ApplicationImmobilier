package vue.Saisie;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import Controleur.Saisie.gestionSaisieChargesLocataire;

/**
 * Fenêtre de saisie des charges d'immeuble pour la régularisation
 */
public class pageSaisieChargesLocataire extends JDialog {

    private static final long serialVersionUID = 1L;

    //  CONTEXTE
    private long idBatiment;
    private int annee;
    private long idBail;
    private String nomLocataire;
    private String adresseBatiment;

    //  COMPOSANTS CHARGES IMMEUBLE 
    private JTextField txtMontantEau;
    private JTextField txtMontantOM;
    private JTextField txtMontantElec;
    private JTextField txtMontantEntretien;
    private JTextField txtPctRecuperable;

    //  COMPOSANTS INFO 
    private JLabel lblBatiment;
    private JLabel lblAnnee;
    private JLabel lblLocataire;

    //  BOUTONS 
    private JButton btnValider;
    private JButton btnAnnuler;

    //  CONTRÔLEUR
    private gestionSaisieChargesLocataire gestion;

    // RÉSULTAT 
    private boolean valide = false;
    
   

    
    public pageSaisieChargesLocataire(long idBatiment, int annee, long idBail, 
                                       String nomLocataire, String adresseBatiment) {
        this.idBatiment = idBatiment;
        this.annee = annee;
        this.idBail = idBail;
        this.nomLocataire = nomLocataire;
        this.adresseBatiment = adresseBatiment;

        gestion = new gestionSaisieChargesLocataire(this);
        initUI();
        
        // Charger les données existantes depuis la base
        gestion.chargerDonneesExistantes();
    }

    /**
     * Constructeur par défaut 
     */
    public pageSaisieChargesLocataire() {
        this(0, java.util.Calendar.getInstance().get(java.util.Calendar.YEAR), 0, "", "");
    }

    private void initUI() {
        setTitle("Saisie des charges d'immeuble - Année " + annee);
        setModal(true);
        setBounds(100, 100, 550, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPane);

        //PANEL INFO 
        JPanel panelInfo = new JPanel(new GridLayout(3, 2, 10, 8));
        panelInfo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Informations",
            TitledBorder.LEFT, TitledBorder.TOP));

        panelInfo.add(new JLabel("Bâtiment :"));
        lblBatiment = new JLabel(adresseBatiment != null && !adresseBatiment.isEmpty() 
                                  ? adresseBatiment : "Bâtiment #" + idBatiment);
        lblBatiment.setFont(lblBatiment.getFont().deriveFont(Font.BOLD));
        panelInfo.add(lblBatiment);

        panelInfo.add(new JLabel("Année de régularisation :"));
        lblAnnee = new JLabel(String.valueOf(annee));
        lblAnnee.setFont(lblAnnee.getFont().deriveFont(Font.BOLD));
        lblAnnee.setForeground(new Color(0, 100, 200));
        panelInfo.add(lblAnnee);

        panelInfo.add(new JLabel("Locataire sélectionné :"));
        lblLocataire = new JLabel(nomLocataire != null && !nomLocataire.isEmpty() 
                                   ? nomLocataire : "Tous les locataires");
        lblLocataire.setFont(lblLocataire.getFont().deriveFont(Font.BOLD));
        panelInfo.add(lblLocataire);

        contentPane.add(panelInfo, BorderLayout.NORTH);

        //  PANEL CHARGES 
        JPanel panelCharges = new JPanel(new GridBagLayout());
        panelCharges.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Charges d'immeuble (montants totaux pour l'année " + annee + ")",
            TitledBorder.LEFT, TitledBorder.TOP));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Eau
        gbc.gridx = 0; gbc.gridy = 0;
        panelCharges.add(new JLabel("Total Eau (€) :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtMontantEau = new JTextField(15);
        txtMontantEau.setToolTipText("Montant total de la facture d'eau de l'immeuble pour " + annee);
        panelCharges.add(txtMontantEau, gbc);

        // OM / TEOM
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panelCharges.add(new JLabel("Total TEOM / Ordures ménagères (€) :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtMontantOM = new JTextField(15);
        txtMontantOM.setToolTipText("Taxe d'enlèvement des ordures ménagères pour " + annee);
        panelCharges.add(txtMontantOM, gbc);

        // Électricité
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panelCharges.add(new JLabel("Total Électricité parties communes (€) :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtMontantElec = new JTextField(15);
        txtMontantElec.setToolTipText("Électricité des parties communes pour " + annee);
        panelCharges.add(txtMontantElec, gbc);

        // Entretien
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panelCharges.add(new JLabel("Total Entretien / Nettoyage (€) :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtMontantEntretien = new JTextField(15);
        txtMontantEntretien.setToolTipText("Entretien et nettoyage des parties communes pour " + annee);
        panelCharges.add(txtMontantEntretien, gbc);

        // Séparateur
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelCharges.add(new JSeparator(), gbc);

        // Pourcentage récupérable
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panelCharges.add(new JLabel("Pourcentage récupérable (%) :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtPctRecuperable = new JTextField("100", 15);
        txtPctRecuperable.setToolTipText("Pourcentage des charges récupérables auprès du locataire (généralement 100%)");
        panelCharges.add(txtPctRecuperable, gbc);

        contentPane.add(panelCharges, BorderLayout.CENTER);

        //  PANEL NOTE + BOUTONS 
        JPanel panelSud = new JPanel(new BorderLayout());
        
        // Note
        JPanel panelNote = new JPanel(new BorderLayout());
        panelNote.setBorder(new EmptyBorder(10, 0, 5, 0));
        JLabel lblNote = new JLabel("<html><i>Note : Ces charges seront réparties entre tous les locataires<br>" +
                                    "du bâtiment au prorata de leur durée d'occupation.</i></html>");
        lblNote.setForeground(Color.GRAY);
        lblNote.setFont(lblNote.getFont().deriveFont(11f));
        panelNote.add(lblNote, BorderLayout.CENTER);
        panelSud.add(panelNote, BorderLayout.NORTH);

        // Boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        btnAnnuler = new JButton("Annuler");
        btnAnnuler.setPreferredSize(new Dimension(120, 35));
        btnAnnuler.addActionListener(e -> {
            valide = false;
            dispose();
        });
        panelBoutons.add(btnAnnuler);

        btnValider = new JButton("Enregistrer");
        btnValider.setPreferredSize(new Dimension(120, 35));
        btnValider.setActionCommand("VALIDER_CHARGES");
        btnValider.addActionListener(gestion);
        btnValider.setBackground(new Color(0, 123, 255));
        btnValider.setForeground(Color.WHITE);
        btnValider.setFont(btnValider.getFont().deriveFont(Font.BOLD));
        panelBoutons.add(btnValider);

        panelSud.add(panelBoutons, BorderLayout.SOUTH);
        contentPane.add(panelSud, BorderLayout.SOUTH);
    }

    //  GETTERS CONTEXTE 
    
    public long getIdBatiment() { 
        return idBatiment; 
    }
    
    public int getAnnee() { 
        return annee; 
    }
    
    public long getIdBail() { 
        return idBail; 
    }
    
    public String getNomLocataire() {
        return nomLocataire;
    }
    
    public String getAdresseBatiment() {
        return adresseBatiment;
    }

    //  GETTERS CHAMPS
    
    public JTextField getTxtMontantEau() { 
        return txtMontantEau; 
    }
    
    public JTextField getTxtMontantOM() { 
        return txtMontantOM; 
    }
    
    public JTextField getTxtMontantElec() { 
        return txtMontantElec; 
    }
    
    public JTextField getTxtMontantEntretien() { 
        return txtMontantEntretien; 
    }
    
    public JTextField getTxtPctRecuperable() { 
        return txtPctRecuperable; 
    }

    // GETTERS/SETTERS RÉSULTAT 
    
    public boolean isValide() { 
        return valide; 
    }
    
    public void setValide(boolean valide) { 
        this.valide = valide; 
    }

    //  MÉTHODES UTILITAIRES 
    
    /**
     * Pré-remplit les champs avec les montants existants
     */
    public void setMontants(double eau, double om, double elec, double entretien) {
        if (eau > 0) txtMontantEau.setText(String.format("%.2f", eau));
        if (om > 0) txtMontantOM.setText(String.format("%.2f", om));
        if (elec > 0) txtMontantElec.setText(String.format("%.2f", elec));
        if (entretien > 0) txtMontantEntretien.setText(String.format("%.2f", entretien));
    }
    
    /**
     * Pré-remplit le pourcentage récupérable
     */
    public void setPctRecuperable(double pct) {
        txtPctRecuperable.setText(String.format("%.0f", pct));
    }

    
    public JTextField getTxtTotalFactureEau() { return txtMontantEau; }
    public JTextField getTxtPartieFixe() { return new JTextField(); }
    public JTextField getTxtPartieVariable() { return new JTextField(); }
    public JTextField getTxtTotalConsoEau() { return new JTextField(); }
    public JTextField getTxtElectriciteImmeuble() { return txtMontantElec; }
    public JTextField getTxtNbMoisOccupation() { return new JTextField(); }
    public JTextField getTxtConsoEauLocataire() { return new JTextField(); }
    public JTextField getTxtPrixConsoEauLocataire() { return new JTextField(); }
    public JTextField getTxtEauImputee() { return txtMontantEau; }
    public JTextField getTxtOM() { return txtMontantOM; }
    public JTextField getTxtElectriciteLocataire() { return txtMontantElec; }
    public JTextField getTxtEntretien() { return txtMontantEntretien; }
    public JTextField getTxtQuotite() { return new JTextField(); }
    public JTextField getTxtMontantQuotite() { return new JTextField(); }
    public JTextField getTxtAugmentationAssurance() { return new JTextField(); }
    public JTextField getTxtMontantTravaux() { return new JTextField(); }
}