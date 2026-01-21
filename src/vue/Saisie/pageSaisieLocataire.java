package vue.Saisie;

import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

import Controleur.Saisie.gestionSaisieLocataire;
import modele.Locataire;
import vue.refacto.AbstractSaisieDialog;

/**
 * Page de saisie pour créer ou modifier un locataire
 */
public class pageSaisieLocataire extends AbstractSaisieDialog {

    private static final long serialVersionUID = 1L;

    // Champs du formulaire
    private JTextField txtId;
    private JTextField txtNom;
    private JTextField txtPrenom;
    private JTextField txtTel;
    private JTextField txtMail;
    private JSpinner spinnerDateNaissance;  

    private gestionSaisieLocataire gestion;

    // Résultats
    private Locataire locataireCree = null;
    private boolean modeModification = false;

    public pageSaisieLocataire() {
        super("Saisie d'un locataire", "Renseignez les informations du locataire");

        this.setModal(true);
        this.setAlwaysOnTop(true);

        this.gestion = new gestionSaisieLocataire(this);

        // Layout du formulaire
        this.formPanel.setLayout(new GridLayout(0, 2, 10, 10));

        // ID 
        JLabel lblId = new JLabel("ID du locataire * :");
        this.txtId = new JTextField();

        JLabel lblNom = new JLabel("Nom * :");
        this.txtNom = new JTextField();

        JLabel lblPrenom = new JLabel("Prénom * :");
        this.txtPrenom = new JTextField();

        JLabel lblTel = new JLabel("Téléphone :");
        this.txtTel = new JTextField();

        JLabel lblMail = new JLabel("Email :");
        this.txtMail = new JTextField();

        // Date de naissance avec JSpinner
        JLabel lblDateNaissance = new JLabel("Date de naissance :");
        
        // Créer le modèle de date 
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -25);
        Date dateDefaut = cal.getTime();
        
        // Limite entre 1900 et aujourd'hui
        Calendar calMin = Calendar.getInstance();
        calMin.set(1900, 0, 1);
        Calendar calMax = Calendar.getInstance();
        
        SpinnerDateModel dateModel = new SpinnerDateModel(dateDefaut, calMin.getTime(), calMax.getTime(), Calendar.DAY_OF_MONTH);
        this.spinnerDateNaissance = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerDateNaissance, "dd/MM/yyyy");
        spinnerDateNaissance.setEditor(dateEditor);

        // Ajout des composants au formulaire
        this.formPanel.add(lblId);
        this.formPanel.add(this.txtId);

        this.formPanel.add(lblNom);
        this.formPanel.add(this.txtNom);

        this.formPanel.add(lblPrenom);
        this.formPanel.add(this.txtPrenom);

        this.formPanel.add(lblDateNaissance);
        this.formPanel.add(this.spinnerDateNaissance);

        this.formPanel.add(lblTel);
        this.formPanel.add(this.txtTel);

        this.formPanel.add(lblMail);
        this.formPanel.add(this.txtMail);

        // Boutons
        this.btnValider.setActionCommand("VALIDER_LOCATAIRE");
        this.btnAnnuler.setActionCommand("ANNULER_LOCATAIRE");
        this.btnValider.addActionListener(this.gestion);
        this.btnAnnuler.addActionListener(this.gestion);

        // Ajuster taille + centrer
        this.finalizeLayout();
    }

    /**
     * Remplit le formulaire avec les données d'un locataire existant 
     */
    public void remplirDepuis(Locataire loc) {
        if (loc == null) {
            return;
        }

        this.modeModification = true;

        // ID 
        this.txtId.setText(String.valueOf(loc.getIdLocataire()));
        this.txtId.setEnabled(false);

        this.txtNom.setText(loc.getNomLoc());
        this.txtPrenom.setText(loc.getPrenomLoc());

        if (loc.getTelLoc() != null) {
            this.txtTel.setText(loc.getTelLoc());
        }
        if (loc.getMailLoc() != null) {
            this.txtMail.setText(loc.getMailLoc());
        }
        // Date de naissance
        if (loc.getDateNaissance() != null) {
            Date date = Date.from(loc.getDateNaissance().atStartOfDay(ZoneId.systemDefault()).toInstant());
            this.spinnerDateNaissance.setValue(date);
        }

        this.setTitle("Modification d'un locataire");
    }

    /**
     * Ancienne méthode pour compatibilité
     */
    public void remplir(String[] data) {
        if (data == null) {
            return;
        }
        this.modeModification = true;

        if (data.length > 0) {
            this.txtId.setText(data[0]);
        }
        if (data.length > 1) {
            this.txtNom.setText(data[1]);
        }
        if (data.length > 2) {
            this.txtPrenom.setText(data[2]);
        }
        if (data.length > 3) {
            this.txtTel.setText(data[3]);
        }
        if (data.length > 4) {
            this.txtMail.setText(data[4]);
        }
        // serait la date de naissance en String à parser si nécessaire

        this.txtId.setEnabled(false);
        this.setTitle("Modification d'un locataire");
    }

    // GETTERS

    public JTextField getTxtId() {
        return this.txtId;
    }

    public JTextField getTxtNom() {
        return this.txtNom;
    }

    public JTextField getTxtPrenom() {
        return this.txtPrenom;
    }

    public JTextField getTxtTel() {
        return this.txtTel;
    }

    public JTextField getTxtMail() {
        return this.txtMail;
    }

    public JSpinner getSpinnerDateNaissance() {
        return this.spinnerDateNaissance;
    }
    
    /**
     * Récupère la date de naissance sélectionnée
     */
    public LocalDate getDateNaissance() {
        Date date = (Date) spinnerDateNaissance.getValue();
        if (date != null) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }

    public Locataire getLocataireCree() {
        return this.locataireCree;
    }

    public void setLocataireCree(Locataire locataireCree) {
        this.locataireCree = locataireCree;
    }

    public boolean isModeModification() {
        return this.modeModification;
    }

    /**
     * Ancienne méthode pour compatibilité
     */
    public String[] getResultat() {
        if (this.locataireCree == null) {
            return null;
        }
        String dateStr = "";
        if (this.locataireCree.getDateNaissance() != null) {
            dateStr = this.locataireCree.getDateNaissance().toString();
        }
        return new String[] { 
            String.valueOf(this.locataireCree.getIdLocataire()), 
            this.locataireCree.getNomLoc(),
            this.locataireCree.getPrenomLoc(), 
            this.locataireCree.getTelLoc(), 
            this.locataireCree.getMailLoc(),
            dateStr
        };
    }

    public void setResultat(String[] resultat) {
        // Pour compatibilité ne rien faire
    }

    public JTextField getTxtActuel() {
        // Pour compatibilité retourne un champ vide
        return new JTextField("Non");
    }
    

}