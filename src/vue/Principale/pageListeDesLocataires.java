package vue.Principale;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import Controleur.Principale.gestionListeDesLocataires;
import modele.Locataire;
import vue.refacto.UiUtils;

/**
 * Page liste des locataires
 */
public class pageListeDesLocataires extends JDialog {

    private static final long serialVersionUID = 1L;

    private JPanel contentPane;

    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;

    private JToggleButton btnFiltreTous;
    private JToggleButton btnFiltreProspects;
    private JToggleButton btnFiltreActifs;
    private JToggleButton btnFiltreAnciens;

    private JLabel lblCompteur;

    private gestionListeDesLocataires gestion;

    private JList<LocataireItem> listeLocataires;
    private DefaultListModel<LocataireItem> modeleLocataires;

    public static void main(String[] args) {
        try {
            pageListeDesLocataires dialog = new pageListeDesLocataires();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public pageListeDesLocataires() {


        this.setBounds(100, 100, 1225, 744);
        this.contentPane = new JPanel();
        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        this.setContentPane(this.contentPane);
        this.contentPane.setLayout(new BorderLayout(0, 0));

        // BANDEAU HAUT 
        JPanel encadrementHaut = createHeaderWithoutGestion();
        this.contentPane.add(encadrementHaut, BorderLayout.NORTH);

        // CENTRE
        JPanel centrePage = new JPanel();
        this.contentPane.add(centrePage, BorderLayout.CENTER);
        centrePage.setLayout(new BorderLayout(0, 0));

        // En-tete de la page
        JPanel hautGrandeCarte = UiUtils.createListHeader(
                centrePage,
                "Liste des locataires",
                "Vue d'ensemble des locataires",
                "Locataires enregistres",
                "/logoPropriete.png",
                20,
                20);

        // Panel filtres + boutons
        JPanel panelActions = new JPanel(new BorderLayout());
        panelActions.setOpaque(false);

        // Filtres a gauche
        JPanel panelFiltres = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelFiltres.setOpaque(false);

        JLabel lblFiltre = new JLabel("Filtrer : ");
        lblFiltre.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panelFiltres.add(lblFiltre);

        ButtonGroup groupeFiltres = new ButtonGroup();

        btnFiltreTous = creerBoutonFiltre("Tous");
        btnFiltreTous.setSelected(true);
        btnFiltreProspects = creerBoutonFiltre("Prospects");
        btnFiltreActifs = creerBoutonFiltre("Actifs");
        btnFiltreAnciens = creerBoutonFiltre("Anciens");

        groupeFiltres.add(btnFiltreTous);
        groupeFiltres.add(btnFiltreProspects);
        groupeFiltres.add(btnFiltreActifs);
        groupeFiltres.add(btnFiltreAnciens);

        panelFiltres.add(btnFiltreTous);
        panelFiltres.add(btnFiltreProspects);
        panelFiltres.add(btnFiltreActifs);
        panelFiltres.add(btnFiltreAnciens);

        // Compteur
        lblCompteur = new JLabel("0 locataire(s)");
        lblCompteur.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblCompteur.setForeground(new Color(100, 100, 100));
        panelFiltres.add(Box.createHorizontalStrut(20));
        panelFiltres.add(lblCompteur);

        panelActions.add(panelFiltres, BorderLayout.WEST);

        // Boutons d'action a droite
        JPanel panelBoutonsHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBoutonsHeader.setOpaque(false);

        this.btnAjouter = new JButton("Ajouter");
        this.btnModifier = new JButton("Modifier");
        this.btnSupprimer = new JButton("Supprimer");

        styliserBoutonAction(this.btnAjouter);
        styliserBoutonAction(this.btnModifier);
        styliserBoutonAction(this.btnSupprimer);

        panelBoutonsHeader.add(this.btnAjouter);
        panelBoutonsHeader.add(this.btnModifier);
        panelBoutonsHeader.add(this.btnSupprimer);

        panelActions.add(panelBoutonsHeader, BorderLayout.EAST);
        hautGrandeCarte.add(panelActions, BorderLayout.SOUTH);

        // Liste des locataires
        this.modeleLocataires = new DefaultListModel<>();
        this.listeLocataires = new JList<>(this.modeleLocataires);
        this.listeLocataires.setCellRenderer(new LocataireRenderer());
        this.listeLocataires.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.listeLocataires.setBackground(new Color(245, 245, 245));
        this.listeLocataires.setFixedCellHeight(-1);

        JScrollPane scroll = new JScrollPane(this.listeLocataires);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        centrePage.add(scroll, BorderLayout.CENTER);

        // CREER LE CONTROLEUR MAINTENANT 
        this.gestion = new gestionListeDesLocataires(this);

        // ACTIONS
        this.btnAjouter.setActionCommand("AJOUTER_LOCATAIRE");
        this.btnModifier.setActionCommand("MODIFIER_LOCATAIRE");
        this.btnSupprimer.setActionCommand("SUPPRIMER_LOCATAIRE");

        // Actions des filtres
        btnFiltreTous.setActionCommand("FILTRE_TOUS");
        btnFiltreProspects.setActionCommand("FILTRE_PROSPECTS");
        btnFiltreActifs.setActionCommand("FILTRE_ACTIFS");
        btnFiltreAnciens.setActionCommand("FILTRE_ANCIENS");

        this.btnAjouter.addActionListener(this.gestion);
        this.btnModifier.addActionListener(this.gestion);
        this.btnSupprimer.addActionListener(this.gestion);
        btnFiltreTous.addActionListener(this.gestion);
        btnFiltreProspects.addActionListener(this.gestion);
        btnFiltreActifs.addActionListener(this.gestion);
        btnFiltreAnciens.addActionListener(this.gestion);


        this.gestion.chargerDonneesLocatairesDepuisBDD();
    }

    /**
     * Cree le header 
     */
    private JPanel createHeaderWithoutGestion() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 35, 45));
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel titre = new JLabel("LOCATAIRES");
        titre.setForeground(Color.WHITE);
        titre.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(titre, BorderLayout.WEST);
        
        return panel;
    }

    private JToggleButton creerBoutonFiltre(String texte) {
        JToggleButton btn = new JToggleButton(texte);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setBorder(new EmptyBorder(5, 12, 5, 12));
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return btn;
    }

    private void styliserBoutonAction(JButton bouton) {
        bouton.setFocusPainted(false);
        bouton.setContentAreaFilled(true);
        bouton.setOpaque(true);
        bouton.setBackground(Color.WHITE);
        bouton.setForeground(new Color(30, 35, 45));
        bouton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        bouton.setBorder(new javax.swing.border.CompoundBorder(
                new javax.swing.border.LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(8, 14, 8, 14)));
        bouton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    // GETTERS 
    public JList<LocataireItem> getListeLocataires() {
        return this.listeLocataires;
    }

    public DefaultListModel<LocataireItem> getModeleLocataires() {
        return this.modeleLocataires;
    }

    public JLabel getLblCompteur() {
        return this.lblCompteur;
    }

    //  Item de la liste
    public static class LocataireItem {
        private long idLocataire;
        private String nom;
        private String prenom;
        private String telephone;
        private String email;
        private String dateNaissance;
        private String statut;
        private String actuel;
        private Locataire locataire;

        public LocataireItem(Locataire loc, String statut, String actuel) {
            this.locataire = loc;
            this.idLocataire = loc.getIdLocataire();
            this.nom = loc.getNomLoc();
            this.prenom = loc.getPrenomLoc();
            this.telephone = loc.getTelLoc() != null ? loc.getTelLoc() : "";
            this.email = loc.getMailLoc() != null ? loc.getMailLoc() : "";
            this.dateNaissance = loc.getDateNaissance() != null 
                    ? loc.getDateNaissance().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) 
                    : "";
            this.statut = statut;
            this.actuel = actuel;
        }

        public LocataireItem(Locataire loc, String actuel) {
            this(loc, actuel.equals("Oui") ? "ACTIF" : "PROSPECT", actuel);
        }

        public LocataireItem(String nom, String prenom, String telephone, String email, String dateNaissance, String actuel) {
            this.nom = nom;
            this.prenom = prenom;
            this.telephone = telephone;
            this.email = email;
            this.dateNaissance = dateNaissance;
            this.actuel = actuel;
            this.statut = actuel.equals("Oui") ? "ACTIF" : "PROSPECT";
        }

        public long getIdLocataire() { return this.idLocataire; }
        public Locataire getLocataire() { return this.locataire; }
        public String getNom() { return this.nom; }
        public String getPrenom() { return this.prenom; }
        public String getTelephone() { return this.telephone; }
        public String getEmail() { return this.email; }
        public String getDateNaissance() { return this.dateNaissance; }
        public String getAdresse() { return this.dateNaissance; }
        public String getStatut() { return this.statut; }
        public String getActuel() { return this.actuel; }

        public String getTitre() {
            return this.nom.toUpperCase() + " " + this.prenom + " (ID: " + this.idLocataire + ")";
        }

        public String getLigne2() {
            return "Tel : " + this.telephone + "  -  Email : " + this.email;
        }

        public String getLigne3() {
            return "Date de naissance : " + (this.dateNaissance.isEmpty() ? "(non renseignee)" : this.dateNaissance);
        }

        public String getStatutLabel() {
            return "Statut : " + this.statut;
        }

        @Override
        public String toString() {
            return this.getTitre();
        }
    }

    /**
     * Renderer pour afficher les locataires avec couleurs de statut
     */
    public static class LocataireRenderer extends JPanel implements ListCellRenderer<LocataireItem> {

        private static final long serialVersionUID = 1L;
        
        private JLabel lblTitre = new JLabel();
        private JLabel lblLigne2 = new JLabel();
        private JLabel lblLigne3 = new JLabel();
        private JLabel lblStatut = new JLabel();

        private static final Color COULEUR_ACTIF = new Color(0, 160, 0);
        private static final Color COULEUR_PROSPECT = new Color(0, 120, 200);
        private static final Color COULEUR_ANCIEN = new Color(150, 150, 150);

        public LocataireRenderer() {
            this.setLayout(new BorderLayout(10, 5));
            this.setOpaque(true);
            this.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(6, 12, 6, 12),
                    BorderFactory.createLineBorder(new Color(220, 220, 220))));

            JPanel panelTexte = new JPanel();
            panelTexte.setLayout(new BoxLayout(panelTexte, BoxLayout.Y_AXIS));
            panelTexte.setOpaque(false);

            this.lblTitre.setFont(new Font("SansSerif", Font.BOLD, 14));
            this.lblLigne2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            this.lblLigne3.setFont(new Font("SansSerif", Font.PLAIN, 12));
            this.lblLigne3.setForeground(new Color(90, 90, 90));

            panelTexte.add(this.lblTitre);
            panelTexte.add(Box.createVerticalStrut(2));
            panelTexte.add(this.lblLigne2);
            panelTexte.add(Box.createVerticalStrut(2));
            panelTexte.add(this.lblLigne3);

            JPanel panelDroite = new JPanel();
            panelDroite.setLayout(new BoxLayout(panelDroite, BoxLayout.Y_AXIS));
            panelDroite.setOpaque(false);

            this.lblStatut.setOpaque(true);
            this.lblStatut.setHorizontalAlignment(SwingConstants.CENTER);
            this.lblStatut.setBorder(new EmptyBorder(4, 12, 4, 12));
            this.lblStatut.setFont(new Font("SansSerif", Font.BOLD, 11));

            panelDroite.add(this.lblStatut);
            panelDroite.add(Box.createVerticalStrut(12));

            this.add(panelTexte, BorderLayout.CENTER);
            this.add(panelDroite, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends LocataireItem> list, LocataireItem value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            this.lblTitre.setText(value.getTitre());
            this.lblLigne2.setText(value.getLigne2());
            this.lblLigne3.setText(value.getLigne3());
            this.lblStatut.setText(value.getStatut());

            switch (value.getStatut()) {
                case "ACTIF":
                    this.lblStatut.setBackground(COULEUR_ACTIF);
                    this.lblStatut.setForeground(Color.WHITE);
                    break;
                case "PROSPECT":
                    this.lblStatut.setBackground(COULEUR_PROSPECT);
                    this.lblStatut.setForeground(Color.WHITE);
                    break;
                case "ANCIEN":
                default:
                    this.lblStatut.setBackground(COULEUR_ANCIEN);
                    this.lblStatut.setForeground(Color.WHITE);
                    break;
            }

            if (isSelected) {
                this.setBackground(new Color(230, 238, 255));
            } else {
                this.setBackground(Color.WHITE);
            }

            return this;
        }
    }
}
