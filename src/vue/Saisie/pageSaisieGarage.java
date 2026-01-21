package vue.Saisie;

import java.awt.GridLayout;

import java.sql.SQLException;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import Controleur.Saisie.gestionSaisieGarage;
import modele.dao.BatimentDao;
import modele.dao.UtOracleDataSource;
import modele.Batiment;
import modele.Garage;
import vue.refacto.AbstractSaisieDialog;

/**
 * Page de saisie pour creer ou modifier un garage
 */
public class pageSaisieGarage extends AbstractSaisieDialog {

    private static final long serialVersionUID = 1L;

    // Champs du formulaire
    private JTextField txtId;
    private JTextField txtComplement;
    private JTextField txtSurface;
    private JTextField txtNumFiscal;
    private JComboBox<BatimentComboItem> cbBatiment;

    private gestionSaisieGarage gestion;
    private Garage resultat = null;

    public pageSaisieGarage() {
        super("Saisie d'un garage", "Renseignez les informations du garage");

        this.setModal(true);
        this.setAlwaysOnTop(true);

        this.gestion = new gestionSaisieGarage(this);

        this.formPanel.setLayout(new GridLayout(0, 2, 10, 10));

        // ID 
        JLabel lblId = new JLabel("ID Garage * :");
        this.txtId = new JTextField();

        // Batiment EN PREMIER 
        JLabel lblBatiment = new JLabel("Batiment * :");
        lblBatiment.setToolTipText("Le batiment definit l'adresse principale du garage");
        this.cbBatiment = new JComboBox<>();
        this.chargerBatiments();

        // Label "Complement d'adresse" au lieu de "Adresse"
        JLabel lblComplement = new JLabel("Complement d'adresse :");
        lblComplement.setToolTipText("Ex: Box 5, Niveau -1, Place 12... L'adresse principale vient du batiment.");
        this.txtComplement = new JTextField();

        JLabel lblSurface = new JLabel("Surface (m2) * :");
        this.txtSurface = new JTextField();

        JLabel lblNumFiscal = new JLabel("N fiscal * :");
        this.txtNumFiscal = new JTextField();

        // Ajout au panel de formulaire (ordre logique)
        this.formPanel.add(lblId);
        this.formPanel.add(this.txtId);

        this.formPanel.add(lblBatiment);
        this.formPanel.add(this.cbBatiment);

        this.formPanel.add(lblComplement);
        this.formPanel.add(this.txtComplement);

        this.formPanel.add(lblSurface);
        this.formPanel.add(this.txtSurface);

        this.formPanel.add(lblNumFiscal);
        this.formPanel.add(this.txtNumFiscal);

        this.btnValider.setActionCommand("VALIDER_GARAGE");
        this.btnAnnuler.setActionCommand("ANNULER_GARAGE");
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

            List<Batiment> batiments = batDao.findAll();
            for (Batiment b : batiments) {
                this.cbBatiment.addItem(new BatimentComboItem(b));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (batDao != null) try { 
            	batDao.close(); 
            } catch (SQLException ex) {  }
            
        }
    }

    /**
     * Classe interne pour afficher les batiments dans la ComboBox
     */
    public static class BatimentComboItem {
        private Batiment batiment;

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

    /**
     * Remplit le formulaire pour MODIFIER un garage existant
     */
    public void remplirDepuis(Garage g) {
        if (g == null) {
            return;
        }

        this.txtId.setText(String.valueOf(g.getIdGarageLong()));
        this.txtId.setEnabled(false);

        this.txtComplement.setText(g.getAdresseGarage());

        if (g.getSurfaceGarage() != null) {
            this.txtSurface.setText(String.valueOf(g.getSurfaceGarage()));
        }

        this.txtNumFiscal.setText(String.valueOf(g.getNumFiscalLong()));

        if (g.getIdBatiment() != null) {
            for (int i = 0; i < this.cbBatiment.getItemCount(); i++) {
                BatimentComboItem item = this.cbBatiment.getItemAt(i);
                if (item.getIdBatiment() == g.getIdBatiment()) {
                    this.cbBatiment.setSelectedIndex(i);
                    break;
                }
            }
        }

        this.setTitle("Modification d'un garage");
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

    public JTextField getTxtSurface() {
        return this.txtSurface;
    }

    public JTextField getTxtNumFiscal() {
        return this.txtNumFiscal;
    }

    public JComboBox<BatimentComboItem> getCbBatiment() {
        return this.cbBatiment;
    }

    public Long getSelectedBatimentId() {
        BatimentComboItem selected = (BatimentComboItem) this.cbBatiment.getSelectedItem();
        return (selected != null) ? selected.getIdBatiment() : null;
    }

    public Garage getGarageCree() {
        return this.resultat;
    }

    public Garage getResultat() {
        return this.resultat;
    }

    public void setResultat(Garage resultat) {
        this.resultat = resultat;
    }
}
