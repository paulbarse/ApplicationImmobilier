package vue.Saisie;

import java.awt.Color;

import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.border.TitledBorder;

import Controleur.Saisie.gestionSaisieFacture;
import modele.dao.UtOracleDataSource;
import modele.Facture;
import vue.refacto.AbstractSaisieDialog;

import modele.dao.Requete.RequeteEntreprise;
import modele.dao.Requete.RequeteCharges;
import modele.dao.Requete.RequeteBail;

/**
 * Formulaire de saisie de facture - VERSION CORRIGEE
 */
public class pageSaisieFacture extends AbstractSaisieDialog {

    private static final long serialVersionUID = 1L;

    // Champs obligatoires
    private JTextField txtIdFacture;
    private JTextField txtNature;
    private JSpinner spinnerDateEmission;
    private JTextField txtMontantTTC;

    // Travaux et devis (REGLE 12)
    private JCheckBox chkTravaux;
    private JTextField txtTravauxDesc;
    private JSpinner spinnerDateDevis;
    private JTextField txtMontantDevis;
    private JPanel panelTravaux;
    private JLabel lblTravauxDesc;
    private JLabel lblMontantDevis;

    // Recuperable / Deductible (REGLE 4)
    private JComboBox<String> comboType;
    private static final String[] TYPES_FACTURE = {
        "Aucun (Depense proprietaire)",
        "Recuperable locataire",
        "Deductible impots"
    };

    // Liaisons
    private JComboBox<String> comboCharge;
    private final List<Long> listeIdsCharges = new ArrayList<>();
    private final Map<Long, String> mapChargeIdToType = new HashMap<>();
    private JComboBox<EntrepriseComboItem> cbEntreprise;

    // Statut
    private JComboBox<String> comboStatut;

    private long idBail;
    private long idBatiment;
    private String nomLogement;

    private gestionSaisieFacture gestion;
    private Facture factureCree = null;

    public pageSaisieFacture(long idBail, String nomLogement) {
        super("Nouvelle facture", "Ajouter une facture au bail");

        this.setModal(true);
        this.setAlwaysOnTop(true);

        this.idBail = idBail;
        this.nomLogement = nomLogement;
        this.chargerIdBatimentDepuisBail();
        this.gestion = new gestionSaisieFacture(this);

        this.formPanel.setLayout(new GridLayout(0, 2, 10, 8));

        // SECTION 1 : INFORMATIONS OBLIGATOIRES
        
        // Info bail (lecture seule)
        this.formPanel.add(new JLabel("Bail N° :"));
        this.formPanel.add(new JLabel(String.valueOf(idBail) + " - " + nomLogement));

        // ID facture (obligatoire)
        this.formPanel.add(new JLabel("ID facture * :"));
        this.txtIdFacture = new JTextField();
        this.formPanel.add(this.txtIdFacture);

        // Nature (obligatoire)
        this.formPanel.add(new JLabel("Nature * :"));
        this.txtNature = new JTextField();
        this.formPanel.add(this.txtNature);

        // Date emission (obligatoire)
        this.formPanel.add(new JLabel("Date emission * :"));
        this.spinnerDateEmission = new JSpinner(new SpinnerDateModel());
        this.spinnerDateEmission.setEditor(new JSpinner.DateEditor(this.spinnerDateEmission, "dd/MM/yyyy"));
        this.formPanel.add(this.spinnerDateEmission);

        // Montant TTC (obligatoire)
        this.formPanel.add(new JLabel("Montant TTC (EUR) * :"));
        this.txtMontantTTC = new JTextField();
        this.formPanel.add(this.txtMontantTTC);

        // Entreprise (obligatoire)
        this.formPanel.add(new JLabel("Entreprise * :"));
        this.cbEntreprise = new JComboBox<>();
        this.formPanel.add(this.cbEntreprise);

        // SECTION 2 : TYPE DE DEPENSE (REGLE 4)
        
        this.formPanel.add(new JLabel("Type de depense :"));
        this.comboType = new JComboBox<>(TYPES_FACTURE);
        this.formPanel.add(this.comboType);

        // SECTION 3 : TRAVAUX ET DEVIS (REGLE 12)
        
        this.formPanel.add(new JLabel("Facture de travaux ?"));
        this.chkTravaux = new JCheckBox("Oui (devis obligatoire)");
        this.formPanel.add(this.chkTravaux);

        // Description travaux
        this.lblTravauxDesc = new JLabel("Description travaux * :");
        this.lblTravauxDesc.setForeground(Color.GRAY);
        this.formPanel.add(this.lblTravauxDesc);
        this.txtTravauxDesc = new JTextField();
        this.txtTravauxDesc.setEnabled(false);
        this.formPanel.add(this.txtTravauxDesc);

        // Date devis
        this.formPanel.add(new JLabel("Date devis :"));
        this.spinnerDateDevis = new JSpinner(new SpinnerDateModel());
        this.spinnerDateDevis.setEditor(new JSpinner.DateEditor(this.spinnerDateDevis, "dd/MM/yyyy"));
        this.spinnerDateDevis.setEnabled(false);
        this.formPanel.add(this.spinnerDateDevis);

        // Montant devis (OBLIGATOIRE si travaux)
        this.lblMontantDevis = new JLabel("Montant devis (EUR) * :");
        this.lblMontantDevis.setForeground(Color.GRAY);
        this.formPanel.add(this.lblMontantDevis);
        this.txtMontantDevis = new JTextField();
        this.txtMontantDevis.setEnabled(false);
        this.formPanel.add(this.txtMontantDevis);

        // Activation/desactivation des champs travaux (REGLE 12)
        this.chkTravaux.addActionListener(e -> {
            boolean on = this.chkTravaux.isSelected();
            this.txtTravauxDesc.setEnabled(on);
            this.spinnerDateDevis.setEnabled(on);
            this.txtMontantDevis.setEnabled(on);
            
            // Changer la couleur des labels pour indiquer obligatoire
            Color labelColor = on ? Color.BLACK : Color.GRAY;
            this.lblTravauxDesc.setForeground(labelColor);
            this.lblMontantDevis.setForeground(labelColor);
            
            // Pre-remplir le montant devis avec le montant TTC si vide
            if (on && this.txtMontantDevis.getText().isEmpty()) {
                this.txtMontantDevis.setText(this.txtMontantTTC.getText());
            }
        });

        // SECTION 4 : OPTIONS
        
        // Charge optionnelle
        this.formPanel.add(new JLabel("Charge liee (optionnel) :"));
        this.comboCharge = new JComboBox<>();
        this.formPanel.add(this.comboCharge);

        // Statut paiement
        this.formPanel.add(new JLabel("Statut paiement :"));
        this.comboStatut = new JComboBox<>(new String[]{"A payer", "Paye"});
        this.formPanel.add(this.comboStatut);

        // BOUTONS
        
        this.btnValider.setText("Creer la facture");
        this.btnValider.setActionCommand("VALIDER_FACTURE");
        this.btnAnnuler.setActionCommand("ANNULER_FACTURE");
        this.btnValider.addActionListener(this.gestion);
        this.btnAnnuler.addActionListener(this.gestion);

        // Charger les donnees
        this.chargerEntreprises();
        this.chargerCharges();

        this.finalizeLayout();
    }

    // Chargement des donnees

    /**
     * Charge les entreprises pour le ComboBox
     */
    private void chargerEntreprises() {
        this.cbEntreprise.removeAllItems();
        try {
            UtOracleDataSource.creerAcces();
            Connection cn = UtOracleDataSource.getConnection();
            String sql = RequeteEntreprise.reqFindAllForCombo();
            try (PreparedStatement st = cn.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    this.cbEntreprise.addItem(new EntrepriseComboItem(
                        rs.getLong("SIRET"),
                        rs.getString("NOM_ENTREPRISE"), 
                        rs.getString("SPECIALITE")
                    ));
                }
            }

            if (this.cbEntreprise.getItemCount() == 0) {
                this.cbEntreprise.addItem(new EntrepriseComboItem(0, "-- Aucune entreprise --", ""));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            this.cbEntreprise.addItem(new EntrepriseComboItem(0, "-- Erreur chargement --", ""));
        }
    }

    /**
     * Charge les charges du bâtiment pour le ComboBox
     */
    private void chargerCharges() {
        String sql = RequeteCharges.reqFindByBatimentForCombo();

        try {
            UtOracleDataSource.creerAcces();
            Connection cn = UtOracleDataSource.getConnection();

            this.mapChargeIdToType.clear();
            this.listeIdsCharges.clear();
            this.comboCharge.removeAllItems();
            this.comboCharge.addItem("-- Aucune --");

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                RequeteCharges.paramFindByBatimentForCombo(ps, this.idBatiment);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        long idCharge = rs.getLong("ID_CHARGE");
                        String nature = rs.getString("NATURE");
                        String type = rs.getString("TYPE_CHARGES");

                        String label = (nature != null ? nature : ("Charge " + idCharge))
                                + (type != null ? (" - " + type) : "");

                        this.comboCharge.addItem(label);
                        this.listeIdsCharges.add(idCharge);
                        this.mapChargeIdToType.put(idCharge, type);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Récupère l'ID du bâtiment associé au bail
     */
    private void chargerIdBatimentDepuisBail() {
        String sql = RequeteBail.reqGetBatimentFromBail();

        try {
            UtOracleDataSource.creerAcces();
            Connection cn = UtOracleDataSource.getConnection();
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                RequeteBail.paramGetBatimentFromBail(ps, this.idBail);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        this.idBatiment = rs.getLong("ID_BATIMENT");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.idBatiment = 0;
        }
    }

    //Classes internes 

    public static class EntrepriseComboItem {
        private final long siret;
        private final String nom;
        private final String specialite;

        public EntrepriseComboItem(long siret, String nom, String specialite) {
            this.siret = siret;
            this.nom = nom;
            this.specialite = specialite;
        }

        public long getSiret() {
            return this.siret;
        }

        @Override
        public String toString() {
            if (this.siret == 0) {
                return this.nom;
            }
            return this.nom + (this.specialite != null && !this.specialite.isEmpty() ? " (" + this.specialite + ")" : "");
        }
    }

    // GETTERS

    public long getIdBail() {
        return this.idBail;
    }

    public Long getIdBatiment() {
        return this.idBatiment;
    }

    public JTextField getTxtIdFacture() {
        return this.txtIdFacture;
    }

    public JTextField getTxtNature() {
        return this.txtNature;
    }

    public Date getDateEmission() {
        return (Date) this.spinnerDateEmission.getValue();
    }

    public JTextField getTxtMontantTTC() {
        return this.txtMontantTTC;
    }

    public boolean isTravaux() {
        return this.chkTravaux.isSelected();
    }

    public String getTravauxDesc() {
        String s = this.txtTravauxDesc.getText().trim();
        return s.isEmpty() ? null : s;
    }

    public Date getDateDevis() {
        return this.chkTravaux.isSelected() ? (Date) this.spinnerDateDevis.getValue() : null;
    }

    public JTextField getTxtMontantDevis() {
        return this.txtMontantDevis;
    }

  
    public int getTypeDepense() {
        return this.comboType.getSelectedIndex();
    }

    public boolean isRecuperableLoc() {
        return this.comboType.getSelectedIndex() == 1;
    }

    public boolean isDeductibleImpot() {
        return this.comboType.getSelectedIndex() == 2;
    }

    public Long getSelectedSiret() {
        EntrepriseComboItem it = (EntrepriseComboItem) this.cbEntreprise.getSelectedItem();
        return (it != null && it.getSiret() > 0) ? it.getSiret() : null;
    }

    public Long getSelectedChargeId() {
        int idx = this.comboCharge.getSelectedIndex();
        if (idx <= 0) {
            return null;
        }
        return this.listeIdsCharges.get(idx - 1);
    }

    public String getSelectedTypeCharge() {
        Long id = this.getSelectedChargeId();
        if (id == null) {
            return null;
        }
        return this.mapChargeIdToType.get(id);
    }

    public String getStatutPaiement() {
        return (String) this.comboStatut.getSelectedItem();
    }

    public Facture getFactureCree() {
        return this.factureCree;
    }

    public void setFactureCree(Facture factureCree) {
        this.factureCree = factureCree;
    }

    public void putChargeType(long idCharge, String typeCharges) {
        if (typeCharges == null) {
            return;
        }
        this.mapChargeIdToType.put(idCharge, typeCharges.trim().toUpperCase());
    }
}
