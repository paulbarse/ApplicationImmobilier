package vue.Saisie;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import Controleur.Saisie.gestionSaisieModificationBail;
import modele.Garage;

public class pageSaisieModificationBail extends JDialog {

    private static final long serialVersionUID = 1L;

    // Champs du formulaire
    private JLabel lblIdBail;
    private JLabel lblLogement;
    private JLabel lblLocataire;
    private JLabel lblDateDebut;
    private JLabel lblEtat;
    
    private JTextField txtLoyerInitial;
    private JTextField txtProvisions;
    private JTextField txtCaution;
    private JSpinner spinnerJourPaiement;

    // Tables
    private JTable tableGaragesLies;
    private DefaultTableModel modelGaragesLies;
    private JTable tableGaragesDisponibles;
    private DefaultTableModel modelGaragesDisponibles;

    // Boutons
    private JButton btnAjouterGarage;
    private JButton btnRetirerGarage;
    private JButton btnValider;
    private JButton btnAnnuler;

    // Listes techniques pour mapper les lignes du tableau aux IDs
    private ArrayList<Long> listeIdsGaragesLies = new ArrayList<>();
    private ArrayList<Long> listeIdsGaragesDisponibles = new ArrayList<>();

    public pageSaisieModificationBail(long idBail) {
        this.setModal(true);
        this.setTitle("Modifier le bail #" + idBail);
        this.setSize(850, 650);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.initComponents();
        
        // On attache le contrôleur immédiatement
        // Attention : assurez-vous que le package du contrôleur est correct
        new gestionSaisieModificationBail(this, idBail);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // TITRE
        JLabel lblTitre = new JLabel("Modification du bail");
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 18));
        mainPanel.add(lblTitre, BorderLayout.NORTH);

        // CENTRE
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        // -- Formulaire --
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Informations du bail"));

        // Labels
        formPanel.add(new JLabel("ID Bail :"));
        lblIdBail = new JLabel("-");
        lblIdBail.setFont(lblIdBail.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblIdBail);

        formPanel.add(new JLabel("Logement associé :"));
        lblLogement = new JLabel("-");
        formPanel.add(lblLogement);

        formPanel.add(new JLabel("Locataire principal :"));
        lblLocataire = new JLabel("-");
        formPanel.add(lblLocataire);

        formPanel.add(new JLabel("Date début :"));
        lblDateDebut = new JLabel("-");
        formPanel.add(lblDateDebut);

        formPanel.add(new JLabel("État :"));
        lblEtat = new JLabel("-");
        formPanel.add(lblEtat);

        // Champs modifiables
        formPanel.add(new JLabel("Loyer mensuel (€) :"));
        txtLoyerInitial = new JTextField();
        formPanel.add(txtLoyerInitial);

        formPanel.add(new JLabel("Provisions charges (€) :"));
        txtProvisions = new JTextField();
        formPanel.add(txtProvisions);

        formPanel.add(new JLabel("Caution (€) :"));
        txtCaution = new JTextField();
        formPanel.add(txtCaution);

        formPanel.add(new JLabel("Jour de paiement :"));
        spinnerJourPaiement = new JSpinner(new SpinnerNumberModel(5, 1, 31, 1));
        formPanel.add(spinnerJourPaiement);

        centerPanel.add(formPanel, BorderLayout.NORTH);

        // -- Garages --
        JPanel garagesPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        garagesPanel.setBorder(BorderFactory.createTitledBorder("Gestion des Garages"));

        // Table Liés
        JPanel pLies = new JPanel(new BorderLayout());
        pLies.add(new JLabel("Garages inclus :"), BorderLayout.NORTH);
        modelGaragesLies = new DefaultTableModel(new String[]{"Adresse", "Surf."}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableGaragesLies = new JTable(modelGaragesLies);
        tableGaragesLies.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pLies.add(new JScrollPane(tableGaragesLies), BorderLayout.CENTER);

        // Table Dispos
        JPanel pDispos = new JPanel(new BorderLayout());
        pDispos.add(new JLabel("Garages disponibles :"), BorderLayout.NORTH);
        modelGaragesDisponibles = new DefaultTableModel(new String[]{"Adresse", "Surf."}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableGaragesDisponibles = new JTable(modelGaragesDisponibles);
        tableGaragesDisponibles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pDispos.add(new JScrollPane(tableGaragesDisponibles), BorderLayout.CENTER);

        // Boutons Action
        JPanel pAction = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5); 
        gbc.gridx=0; gbc.gridy=0; gbc.fill=GridBagConstraints.HORIZONTAL;
        
        btnAjouterGarage = new JButton("<< Ajouter");
        btnAjouterGarage.setActionCommand("AJOUTER_GARAGE");
        pAction.add(btnAjouterGarage, gbc);

        gbc.gridy=1;
        btnRetirerGarage = new JButton("Retirer >>");
        btnRetirerGarage.setActionCommand("RETIRER_GARAGE");
        pAction.add(btnRetirerGarage, gbc);

        garagesPanel.add(pLies);
        garagesPanel.add(pAction);
        garagesPanel.add(pDispos);

        centerPanel.add(garagesPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // FOOTER
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAnnuler = new JButton("Annuler");
        btnAnnuler.setActionCommand("ANNULER");
        
        btnValider = new JButton("Enregistrer");
        btnValider.setActionCommand("VALIDER");
        
        footer.add(btnAnnuler);
        footer.add(btnValider);
        mainPanel.add(footer, BorderLayout.SOUTH);

        this.setContentPane(mainPanel);
    }

    // === MÉTHODES POUR LE CONTRÔLEUR ===

    public void setControleur(gestionSaisieModificationBail ctrl) {
        btnAjouterGarage.addActionListener(ctrl);
        btnRetirerGarage.addActionListener(ctrl);
        btnValider.addActionListener(ctrl);
        btnAnnuler.addActionListener(ctrl);
    }

    // Setters pour l'affichage des infos statiques
    public void setInfosBail(String id, String dateDeb, String etat, String log, String loc) {
        lblIdBail.setText(id);
        lblDateDebut.setText(dateDeb);
        lblEtat.setText(etat);
        lblLogement.setText(log);
        lblLocataire.setText(loc);
    }

    public void setValeursModifiables(double loyer, double prov, double caution, int jour) {
        txtLoyerInitial.setText(String.valueOf(loyer));
        txtProvisions.setText(String.valueOf(prov));
        txtCaution.setText(String.valueOf(caution));
        spinnerJourPaiement.setValue(jour);
    }

    // Mise à jour des tables
    public void updateTableLies(List<Garage> garages) {
        modelGaragesLies.setRowCount(0);
        listeIdsGaragesLies.clear();
        for (Garage g : garages) {
            listeIdsGaragesLies.add(g.getIdGarageLong());
            modelGaragesLies.addRow(new Object[]{g.getAdresseGarage(), g.getSurfaceGarage() + " m²"});
        }
    }

    public void updateTableDispos(List<Garage> garages) {
        modelGaragesDisponibles.setRowCount(0);
        listeIdsGaragesDisponibles.clear();
        for (Garage g : garages) {
            listeIdsGaragesDisponibles.add(g.getIdGarageLong());
            modelGaragesDisponibles.addRow(new Object[]{g.getAdresseGarage(), g.getSurfaceGarage() + " m²"});
        }
    }

    // Getters pour récupérer les saisies
    public String getLoyerSaisi() { return txtLoyerInitial.getText(); }
    public String getProvisionSaisie() { return txtProvisions.getText(); }
    public String getCautionSaisie() { return txtCaution.getText(); }
    public int getJourPaiementSaisi() { return (Integer) spinnerJourPaiement.getValue(); }

    public Long getIdGarageSelectionneDispo() {
        int row = tableGaragesDisponibles.getSelectedRow();
        return (row >= 0) ? listeIdsGaragesDisponibles.get(row) : null;
    }

    public Long getIdGarageSelectionneLie() {
        int row = tableGaragesLies.getSelectedRow();
        return (row >= 0) ? listeIdsGaragesLies.get(row) : null;
    }
    

    // GESTION DU RETOUR

    private boolean modifie = false;

    public void setModifie(boolean modifie) {
        this.modifie = modifie;
    }

   
     //Méthode appelée par gestionGestionDesBaux pour savoir s'il faut rafraîchir
    
    public boolean isBailModifie() {
        return this.modifie;
    }
}