package vue.Saisie;

import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;

import Controleur.Saisie.gestionSaisieBail;
import modele.dao.BatimentDao;
import modele.dao.GarageDao;
import modele.dao.LocataireDao;
import modele.dao.LogementDao;
import modele.dao.UtOracleDataSource;
import modele.Batiment;
import modele.Garage;
import modele.Locataire;
import modele.Logement;
import vue.refacto.AbstractSaisieDialog;

/**
 * Page de saisie pour creer un nouveau bail
 */
public class pageSaisieBail extends AbstractSaisieDialog {

    private static final long serialVersionUID = 1L;

    // Type de bail
    private JRadioButton rbLogement;
    private JRadioButton rbGarage;
    private JRadioButton rbLogementEtGarage;
    private ButtonGroup bgTypeBail;

    // Champs
    private JTextField txtIdBail;
    private JSpinner spinnerDateDebut;
    private JCheckBox chkDateFinConnue;
    private JSpinner spinnerDateFin;
    private JTextField txtLoyerInitial;
    private JTextField txtProvisions;
    private JTextField txtCaution;
    private JSpinner spinnerJourPaiement;
    private JComboBox<LogementComboItem> cbLogement;
    private JComboBox<GarageComboItem> cbGarage;
    private JComboBox<LocataireComboItem> cbLocataire;
    private JSpinner spinnerAnneeLoyers;

    // Labels
    private JLabel lblLogement;
    private JLabel lblGarage;

    private gestionSaisieBail gestion;

    // Resultat
    private boolean bailCree = false;

    public pageSaisieBail() {
        super("Nouveau bail", "Creer un contrat de location");

        this.setModal(true);
        this.setAlwaysOnTop(true);

        this.gestion = new gestionSaisieBail(this);

        this.formPanel.setLayout(new GridLayout(0, 2, 10, 10));

        // TYPE DE BAIL 
        JLabel lblTypeBail = new JLabel("Type de location * :");

        this.rbLogement = new JRadioButton("Logement seul");
        this.rbGarage = new JRadioButton("Garage seul");
        this.rbLogementEtGarage = new JRadioButton("Logement + Garage");

        this.rbLogement.setSelected(true);

        this.bgTypeBail = new ButtonGroup();
        this.bgTypeBail.add(this.rbLogement);
        this.bgTypeBail.add(this.rbGarage);
        this.bgTypeBail.add(this.rbLogementEtGarage);

        javax.swing.JPanel panelTypeBail = new javax.swing.JPanel(
                new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        panelTypeBail.add(this.rbLogement);
        panelTypeBail.add(this.rbGarage);
        panelTypeBail.add(this.rbLogementEtGarage);

        ActionListener typeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pageSaisieBail.this.updateCombosVisibility();
            }
        };
        this.rbLogement.addActionListener(typeListener);
        this.rbGarage.addActionListener(typeListener);
        this.rbLogementEtGarage.addActionListener(typeListener);

        //  ID bail 
        JLabel lblId = new JLabel("ID du bail * :");
        this.txtIdBail = new JTextField();

        //  Date debut 
        JLabel lblDateDebut = new JLabel("Date de debut * :");
        SpinnerDateModel dateDebutModel = new SpinnerDateModel();
        this.spinnerDateDebut = new JSpinner(dateDebutModel);
        JSpinner.DateEditor dateDebutEditor = new JSpinner.DateEditor(this.spinnerDateDebut, "dd/MM/yyyy");
        this.spinnerDateDebut.setEditor(dateDebutEditor);

        //  Date fin 
        JLabel lblDateFinConnue = new JLabel("Date de fin connue ? :");
        this.chkDateFinConnue = new JCheckBox("Oui, specifier la date de fin");

        JLabel lblDateFin = new JLabel("Date de fin :");
        SpinnerDateModel dateFinModel = new SpinnerDateModel();
        this.spinnerDateFin = new JSpinner(dateFinModel);
        JSpinner.DateEditor dateFinEditor = new JSpinner.DateEditor(this.spinnerDateFin, "dd/MM/yyyy");
        this.spinnerDateFin.setEditor(dateFinEditor);
        this.spinnerDateFin.setEnabled(false);

        this.chkDateFinConnue
                .addActionListener(e -> this.spinnerDateFin.setEnabled(this.chkDateFinConnue.isSelected()));

        //  Loyer 
        JLabel lblLoyer = new JLabel("Loyer mensuel (EUR) * :");
        this.txtLoyerInitial = new JTextField();

        //  Provisions 
        JLabel lblProvisions = new JLabel("Provisions charges (EUR) * :");
        this.txtProvisions = new JTextField();

        // Caution 
        JLabel lblCaution =new JLabel("Depot de garantie (EUR) :");
        this.txtCaution = new JTextField();

        // Jour paiement 
        JLabel lblJourPaiement = new JLabel("Jour de paiement (1-31) :");
        SpinnerNumberModel jourModel = new SpinnerNumberModel(5, 1, 31, 1);
        this.spinnerJourPaiement = new JSpinner(jourModel);

        //  Logements libres 
        this.lblLogement = new JLabel("Logement a louer * :");
        this.cbLogement = new JComboBox<>();
        this.chargerLogementsLibres();

        // garages libres
        this.lblGarage = new JLabel("Garage a louer * :");
        this.cbGarage = new JComboBox<>();
        this.chargerGaragesLibres();

        //  Locataires 
        JLabel lblLocataire = new JLabel("Locataire * :");
        this.cbLocataire = new JComboBox<>();
        this.chargerLocataires();

        // Annee loyers 
        JLabel lblAnneeLoyers = new JLabel("Generer loyers pour l'annee :");
        int anneeActuelle = Calendar.getInstance().get(Calendar.YEAR);
        SpinnerNumberModel anneeModel = new SpinnerNumberModel(anneeActuelle, 2020, 2050, 1);
        this.spinnerAnneeLoyers = new JSpinner(anneeModel);

        // Ajout formulaire
        this.formPanel.add(lblTypeBail);
        this.formPanel.add(panelTypeBail);

        this.formPanel.add(lblId);
        this.formPanel.add(this.txtIdBail);

        this.formPanel.add(lblDateDebut);
        this.formPanel.add(this.spinnerDateDebut);

        this.formPanel.add(lblDateFinConnue);
        this.formPanel.add(this.chkDateFinConnue);

        this.formPanel.add(lblDateFin);
        this.formPanel.add(this.spinnerDateFin);

        this.formPanel.add(lblLoyer);
        this.formPanel.add(this.txtLoyerInitial);

        this.formPanel.add(lblProvisions);
        this.formPanel.add(this.txtProvisions);

        this.formPanel.add(lblCaution);
        this.formPanel.add(this.txtCaution);

        this.formPanel.add(lblJourPaiement);
        this.formPanel.add(this.spinnerJourPaiement);

        this.formPanel.add(this.lblLogement);
        this.formPanel.add(this.cbLogement);

        this.formPanel.add(this.lblGarage);
        this.formPanel.add(this.cbGarage);

        this.formPanel.add(lblLocataire);
        this.formPanel.add(this.cbLocataire);

        this.formPanel.add(lblAnneeLoyers);
        this.formPanel.add(this.spinnerAnneeLoyers);

        // Boutons
        this.btnValider.setText("Creer le bail");
        this.btnValider.setActionCommand("VALIDER_BAIL");
        this.btnAnnuler.setActionCommand("ANNULER_BAIL");
        this.btnValider.addActionListener(this.gestion);
        this.btnAnnuler.addActionListener(this.gestion);

        this.updateCombosVisibility();

        this.finalizeLayout();
    }

    private void updateCombosVisibility() {
        boolean showLogement = this.rbLogement.isSelected() || this.rbLogementEtGarage.isSelected();
        boolean showGarage = this.rbGarage.isSelected() || this.rbLogementEtGarage.isSelected();

        this.lblLogement.setVisible(showLogement);
        this.cbLogement.setVisible(showLogement);
        this.lblLogement.setEnabled(showLogement);
        this.cbLogement.setEnabled(showLogement);

        this.lblGarage.setVisible(showGarage);
        this.cbGarage.setVisible(showGarage);
        this.lblGarage.setEnabled(showGarage);
        this.cbGarage.setEnabled(showGarage);

        if (this.rbLogementEtGarage.isSelected()) {
            this.lblLogement.setText("Logement a louer * :");
            this.lblGarage.setText("Garage a louer * :");
        } else if (this.rbLogement.isSelected()) {
            this.lblLogement.setText("Logement a louer * :");
        } else if (this.rbGarage.isSelected()) {
            this.lblGarage.setText("Garage a louer * :");
        }

        this.formPanel.revalidate();
        this.formPanel.repaint();
    }

    private void chargerLogementsLibres() {
        LogementDao logDao = null;
        BatimentDao batDao = null;
        try {
            UtOracleDataSource.creerAcces();
            Connection cn = UtOracleDataSource.getConnection();

            logDao = new LogementDao();
            logDao.setConnection(cn);
            List<Logement> logements = logDao.findLibres();

            if (logements.isEmpty()) {
                this.cbLogement.addItem(new LogementComboItem(null, null));
            } else {
                batDao = new BatimentDao();
                batDao.setConnection(cn);
                List<Batiment> batiments = batDao.findAll();

                Map<Long, Batiment> batMap = new HashMap<>();
                for (Batiment b : batiments) {
                    batMap.put(b.getIdBatimentLong(), b);
                }

                for (Logement l : logements) {
                    String adresseComplete = null;

                    if (l.getIdBatiment() != null) {
                        Batiment b = batMap.get(l.getIdBatiment());
                        if (b != null) {
                            String complement = l.getAdresseComplementaire();
                            if (complement == null || complement.isEmpty()) {
                                complement = l.getAdresseLogement();
                                if (complement != null && b.getAdresse() != null 
                                        && complement.startsWith(b.getAdresse())) {
                                    complement = complement.substring(b.getAdresse().length()).trim();
                                    if (complement.startsWith(",")) {
                                        complement = complement.substring(1).trim();
                                    }
                                }
                            }

                            if (complement != null && !complement.trim().isEmpty()) {
                                adresseComplete = b.getAdresse() + ", " + complement.trim();
                            } else {
                                adresseComplete = b.getAdresse();
                            }
                        }
                    }
                    
                    if (adresseComplete == null) {
                        adresseComplete = l.getAdresseLogement();
                    }

                    this.cbLogement.addItem(new LogementComboItem(l, adresseComplete));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur chargement logements: " + e.getMessage());
        } finally {

            if (logDao != null) try { logDao.close(); } catch (SQLException ex) { /* ignore */ }
            if (batDao != null) try { batDao.close(); } catch (SQLException ex) { /* ignore */ }
        }
    }


    private void chargerGaragesLibres() {
        GarageDao garDao = null;
        BatimentDao batDao = null;
        try {
            UtOracleDataSource.creerAcces();
            Connection cn = UtOracleDataSource.getConnection();

            garDao = new GarageDao();
            garDao.setConnection(cn);
            List<Garage> garages = garDao.findLibres();

            if (garages.isEmpty()) {
                this.cbGarage.addItem(new GarageComboItem(null, null));
            } else {
                batDao = new BatimentDao();
                batDao.setConnection(cn);
                List<Batiment> batiments = batDao.findAll();

                Map<Long, Batiment> batMap = new HashMap<>();
                for (Batiment b : batiments) {
                    batMap.put(b.getIdBatimentLong(), b);
                }

                for (Garage g : garages) {
                    String adresseComplete = null;

                    if (g.getIdBatiment() != null) {
                        Batiment b = batMap.get(g.getIdBatiment());
                        if (b != null) {
                            String complement = g.getAdresseGarage();

                            if (complement != null && !complement.trim().isEmpty()) {
                                adresseComplete = b.getAdresse() + ", " + complement.trim();
                            } else {
                                adresseComplete = b.getAdresse();
                            }
                        }
                    }

                    this.cbGarage.addItem(new GarageComboItem(g, adresseComplete));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur chargement garages: " + e.getMessage());
        } finally {

            if (garDao != null) try { garDao.close(); } catch (SQLException ex) { /* ignore */ }
            if (batDao != null) try { batDao.close(); } catch (SQLException ex) { /* ignore */ }
        }
    }

  
    private void chargerLocataires() {
        LocataireDao locDao = null;
        try {
            UtOracleDataSource.creerAcces();
            locDao = new LocataireDao();
            locDao.setConnection(UtOracleDataSource.getConnection());

            List<Locataire> locataires = locDao.findAll();

            if (locataires.isEmpty()) {
                this.cbLocataire.addItem(new LocataireComboItem(null));
            } else {
                for (Locataire loc : locataires) {
                    this.cbLocataire.addItem(new LocataireComboItem(loc));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur chargement locataires: " + e.getMessage());
        } finally {
            if (locDao != null) try { locDao.close(); } catch (SQLException ex) { /* ignore */ }
        }
    }

    //  CLASSES INTERNES 

    public static class LogementComboItem {
        private final Logement logement;
        private final String adresseComplete;

        public LogementComboItem(Logement l, String adresseComplete) {
            this.logement = l;
            this.adresseComplete = adresseComplete;
        }

        public Logement getLogement() {
            return this.logement;
        }

        public Long getIdLogement() {
            return this.logement != null ? this.logement.getIdLogementLong() : null;
        }

        @Override
        public String toString() {
            if (this.logement == null) {
                return "-- Aucun logement libre --";
            }
            String adr = (this.adresseComplete != null && !this.adresseComplete.trim().isEmpty()) 
                    ? this.adresseComplete : "Sans adresse";
            String type = this.logement.getTypeLogement() != null ? this.logement.getTypeLogement() : "";
            return adr + " - " + type + " (" + this.logement.getNbPieces() + " pieces)";
        }
    }

    public static class GarageComboItem {
        private final Garage garage;
        private final String adresseComplete;

        public GarageComboItem(Garage garage, String adresseComplete) {
            this.garage = garage;
            this.adresseComplete = adresseComplete;
        }

        public Garage getGarage() {
            return this.garage;
        }

        public Long getIdGarage() {
            return this.garage != null ? this.garage.getIdGarageLong() : null;
        }

        @Override
        public String toString() {
            if (this.garage == null) {
                return "-- Aucun garage libre --";
            }

            String adr = (this.adresseComplete != null && !this.adresseComplete.trim().isEmpty()) 
                    ? this.adresseComplete : "Sans adresse";

            Integer surface = this.garage.getSurfaceGarage();
            String surfaceStr = surface != null ? surface + " m2" : "Surface ?";

            return adr + " - " + surfaceStr;
        }
    }

    public static class LocataireComboItem {
        private final Locataire locataire;

        public LocataireComboItem(Locataire loc) {
            this.locataire = loc;
        }

        public Locataire getLocataire() {
            return this.locataire;
        }

        public Long getIdLocataire() {
            return this.locataire != null ? this.locataire.getIdLocataire() : null;
        }

        @Override
        public String toString() {
            if (this.locataire == null) {
                return "-- Aucun locataire --";
            }
            String tel = this.locataire.getTelLoc() != null ? this.locataire.getTelLoc() : "";
            return this.locataire.getNomComplet() + (tel.isEmpty() ? "" : " - " + tel);
        }
    }

    // GETTERS 

    public boolean isLogementSeul() {
        return this.rbLogement.isSelected();
    }

    public boolean isGarageSeul() {
        return this.rbGarage.isSelected();
    }

    public boolean isLogementEtGarage() {
        return this.rbLogementEtGarage.isSelected();
    }

    public JTextField getTxtIdBail() {
        return this.txtIdBail;
    }

    public Date getDateDebut() {
        return (Date) this.spinnerDateDebut.getValue();
    }

    public Date getDateFin() {
        if (this.chkDateFinConnue.isSelected()) {
            return (Date) this.spinnerDateFin.getValue();
        }
        return null;
    }

    public JTextField getTxtLoyerInitial() {
        return this.txtLoyerInitial;
    }

    public JTextField getTxtProvisions() {
        return this.txtProvisions;
    }

    public JTextField getTxtCaution() {
        return this.txtCaution;
    }

    public Integer getJourPaiement() {
        return (Integer) this.spinnerJourPaiement.getValue();
    }

    public JComboBox<LogementComboItem> getCbLogement() {
        return this.cbLogement;
    }

    public JComboBox<GarageComboItem> getCbGarage() {
        return this.cbGarage;
    }

    public JComboBox<LocataireComboItem> getCbLocataire() {
        return this.cbLocataire;
    }

    public Long getSelectedLogementId() {
        LogementComboItem selected = (LogementComboItem) this.cbLogement.getSelectedItem();
        return selected != null ? selected.getIdLogement() : null;
    }

    public Long getSelectedGarageId() {
        GarageComboItem selected = (GarageComboItem) this.cbGarage.getSelectedItem();
        return selected != null ? selected.getIdGarage() : null;
    }

    public Long getSelectedLocataireId() {
        LocataireComboItem selected = (LocataireComboItem) this.cbLocataire.getSelectedItem();
        return selected != null ? selected.getIdLocataire() : null;
    }

    public int getAnneeLoyers() {
        return (Integer) this.spinnerAnneeLoyers.getValue();
    }

    public boolean isBailCree() {
        return this.bailCree;
    }

    public void setBailCree(boolean bailCree) {
        this.bailCree = bailCree;
    }
}
