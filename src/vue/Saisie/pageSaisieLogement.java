package vue.Saisie;

import java.awt.GridLayout;

import java.sql.SQLException;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import Controleur.Saisie.gestionSaisieLogement;
import modele.dao.BatimentDao;
import modele.dao.UtOracleDataSource;
import modele.Batiment;
import modele.Logement;
import vue.Principale.pageListeDesLogements.LogementItem;
import vue.refacto.AbstractSaisieDialog;

//Page de saisie pour creer ou modifier un logement

public class pageSaisieLogement extends AbstractSaisieDialog {

    private static final long serialVersionUID = 1L;

    // Champs du formulaire
    private JTextField txtId;
    private JTextField txtComplement;
    private JTextField txtType;
    private JTextField txtSurface;
    private JTextField txtNbPieces;
    private JTextField txtNumFiscal;
    private JComboBox<BatimentComboItem> cbBatiment;

    private gestionSaisieLogement gestion;

    private LogementItem resultat = null;
    private Logement logementCree = null;

    public pageSaisieLogement() {
        super("Saisie d'un logement", "Renseignez les informations du logement");

        this.setModal(true);
        this.setAlwaysOnTop(true);

        this.gestion = new gestionSaisieLogement(this);

        // Layout du formulaire
        this.formPanel.setLayout(new GridLayout(0, 2, 10, 10));

        // ID (obligatoire)
        JLabel lblId = new JLabel("ID du logement * :");
        this.txtId = new JTextField();

        // Batiment (obligatoire)
        JLabel lblBatiment = new JLabel("Batiment * :");
        lblBatiment.setToolTipText("Le batiment definit l'adresse principale du logement");
        this.cbBatiment = new JComboBox<>();
        this.chargerBatiments();

        // Complement d'adresse
        JLabel lblComplement = new JLabel("Complement d'adresse :");
        lblComplement.setToolTipText("Ex: Apt 3, Etage 2, Porte droite... L'adresse principale vient du batiment.");
        this.txtComplement = new JTextField();

        // Type logement
        JLabel lblType = new JLabel("Type de logement :");
        lblType.setToolTipText("Ex: Appartement, Studio, Duplex... (si batiment = MAISON, ce champ est force)");
        this.txtType = new JTextField();

        JLabel lblSurface = new JLabel("Surface (m2) * :");
        this.txtSurface = new JTextField();

        JLabel lblNbPieces = new JLabel("Nombre de pieces * :");
        this.txtNbPieces = new JTextField();

        JLabel lblNumFiscal = new JLabel("N fiscal * :");
        lblNumFiscal.setToolTipText("Numero fiscal du logement (max 12 chiffres)");
        this.txtNumFiscal = new JTextField();

        // Ajout au panel (ordre logique)
        this.formPanel.add(lblId);
        this.formPanel.add(this.txtId);

        this.formPanel.add(lblBatiment);
        this.formPanel.add(this.cbBatiment);

        this.formPanel.add(lblComplement);
        this.formPanel.add(this.txtComplement);

        this.formPanel.add(lblType);
        this.formPanel.add(this.txtType);

        this.formPanel.add(lblSurface);
        this.formPanel.add(this.txtSurface);

        this.formPanel.add(lblNbPieces);
        this.formPanel.add(this.txtNbPieces);

        this.formPanel.add(lblNumFiscal);
        this.formPanel.add(this.txtNumFiscal);

        // Listener : quand on change de batiment, on applique les regles MAISON/IMMEUBLE
        this.cbBatiment.addActionListener(e -> this.appliquerReglesDepuisSelectionBatiment());

        // Appliquer une premiere fois (si un item est deja selectionne)
        this.appliquerReglesDepuisSelectionBatiment();

        // Boutons
        this.btnValider.setActionCommand("VALIDER_LOGEMENT");
        this.btnAnnuler.setActionCommand("ANNULER_LOGEMENT");
        this.btnValider.addActionListener(this.gestion);
        this.btnAnnuler.addActionListener(this.gestion);

        this.finalizeLayout();
    }


    private void chargerBatiments() {
        BatimentDao batDao = null;
        try {
            UtOracleDataSource.creerAcces();
            batDao = new BatimentDao();
            batDao.setConnection(UtOracleDataSource.getConnection());

            List<Batiment> batiments = batDao.findDisponiblesPourLogement();
            for (Batiment b : batiments) {
                this.cbBatiment.addItem(new BatimentComboItem(b));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur chargement batiments: " + e.getMessage());
        } finally {

            if (batDao != null) try { batDao.close(); } catch (SQLException ex) { /* ignore */ }
        }
    }


    //Applique les regles UI en fonction du batiment selectionne.

    private void appliquerReglesDepuisSelectionBatiment() {
        BatimentComboItem selected = (BatimentComboItem) this.cbBatiment.getSelectedItem();
        if (selected == null || selected.getBatiment() == null) {
            this.txtComplement.setEnabled(true);
            this.txtType.setEnabled(true);
            return;
        }
        String typeBatiment = selected.getBatiment().getTypeBatiment();
        this.appliquerReglesSelonBatiment(typeBatiment);
    }

 
	//Si batiment = MAISON : complement desactive/vide, type logement force a MAISON Sinon : champs actives

    public void appliquerReglesSelonBatiment(String typeBatiment) {
        boolean estMaison = "MAISON".equalsIgnoreCase(typeBatiment);

        // Complement d'adresse
        this.txtComplement.setEnabled(!estMaison);
        if (estMaison) {
            this.txtComplement.setText("");
        }

        // Type de logement
        this.txtType.setEnabled(!estMaison);
        if (estMaison) {
            this.txtType.setText("MAISON");
        }
    }

  
     //Classe interne pour afficher les batiments dans la ComboBox

    public static class BatimentComboItem {
        private final Batiment batiment;

        public BatimentComboItem(Batiment b) {
            this.batiment = b;
        }

        public Batiment getBatiment() {
            return this.batiment;
        }

        public long getIdBatiment() {
            return this.batiment.getIdBatimentLong();
        }

        @Override
        public String toString() {
            String type = this.batiment.getTypeBatiment();
            String typePrefix = (type != null && !type.isEmpty()) ? "[" + type + "] " : "";
            return typePrefix + this.batiment.getAdresse() + " (" + this.batiment.getCodePostal() + ")";
        }
    }

    
     //Remplit le formulaire pour MODIFIER un logement existant
     
    public void remplirDepuis(Logement l) {
        if (l == null) {
            return;
        }

        // ID (desactive en modification)
        this.txtId.setText(String.valueOf(l.getIdLogementLong()));
        this.txtId.setEnabled(false);

        String complement = l.getAdresseComplementaire();
        if (complement != null && !complement.isEmpty()) {
            this.txtComplement.setText(complement);
        } else {
            if (l.getAdresseLogement() != null) {
                this.txtComplement.setText(l.getAdresseLogement());
            }
        }
        
        if (l.getTypeLogement() != null) {
            this.txtType.setText(l.getTypeLogement());
        }
        this.txtSurface.setText(l.getSurface());
        this.txtNbPieces.setText(String.valueOf(l.getNbPieces()));
        this.txtNumFiscal.setText(String.valueOf(l.getNumFiscalLong()));

        // Selectionner le bon batiment
        if (l.getIdBatiment() != null) {
            for (int i = 0; i < this.cbBatiment.getItemCount(); i++) {
                BatimentComboItem item = this.cbBatiment.getItemAt(i);
                if (item.getIdBatiment() == l.getIdBatiment()) {
                    this.cbBatiment.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Appliquer les regles apres selection
        this.appliquerReglesDepuisSelectionBatiment();

        this.setTitle("Modification d'un logement");
    }

    // GETTERS

    public JTextField getTxtId() {
        return this.txtId;
    }

    public JTextField getTxtAdresse() {
        return this.txtComplement;
    }

    public JTextField getTxtComplement() {
        return this.txtComplement;
    }

    public JTextField getTxtType() {
        return this.txtType;
    }

    public JTextField getTxtSurface() {
        return this.txtSurface;
    }

    public JTextField getTxtNbPieces() {
        return this.txtNbPieces;
    }

    public JTextField getTxtNumFiscal() {
        return this.txtNumFiscal;
    }

    public JTextField getTxtCharges() {
        return this.txtNumFiscal;
    }

    public JComboBox<BatimentComboItem> getCbBatiment() {
        return this.cbBatiment;
    }

    public Long getSelectedBatimentId() {
        BatimentComboItem selected = (BatimentComboItem) this.cbBatiment.getSelectedItem();
        return (selected != null) ? selected.getIdBatiment() : null;
    }

    public LogementItem getResultat() {
        return this.resultat;
    }

    public Logement getLogementCree() {
        return this.logementCree;
    }

    public void setResultat(LogementItem resultat) {
        this.resultat = resultat;
    }

    public void setLogementCree(Logement logementCree) {
        this.logementCree = logementCree;
    }
}
