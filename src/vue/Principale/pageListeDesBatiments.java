package vue.Principale;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import Controleur.Principale.gestionListeDesBatiments;
import vue.refacto.UiUtils;

public class pageListeDesBatiments extends JDialog {

    private static final long serialVersionUID = 1L;

    private JPanel contentPane;

    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;

    private JList<BatimentItem> listeBatiments;
    private DefaultListModel<BatimentItem> modeleBatiments;

    private gestionListeDesBatiments gestion;

    public pageListeDesBatiments() {
        // 1. CRUCIAL : Initialiser la liste EN PREMIER
        this.modeleBatiments = new DefaultListModel<>();
        this.listeBatiments = new JList<>(this.modeleBatiments);
        
        // Configuration de la liste
        this.listeBatiments.setCellRenderer(new BatimentRenderer());
        this.listeBatiments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.listeBatiments.setBackground(new Color(245, 245, 245));
        this.listeBatiments.setFixedCellHeight(-1);

        // 2. Initialiser le contrôleur EN DEUXIÈME
        this.gestion = new gestionListeDesBatiments(this);

        // 3. Construction de l'interface graphique
        this.setBounds(100, 100, 1225, 744);
        this.contentPane = new JPanel();
        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setContentPane(this.contentPane);
        this.contentPane.setLayout(new BorderLayout(0, 0));

        // Bandeau Haut
        JPanel encadrementHaut = UiUtils.createHeader(this.gestion, "BATIMENTS");
        this.contentPane.add(encadrementHaut, BorderLayout.NORTH);

        // Centre
        JPanel centrePage = new JPanel();
        this.contentPane.add(centrePage, BorderLayout.CENTER);
        centrePage.setLayout(new BorderLayout(0, 0));

        JPanel hautGrandeCarte = UiUtils.createListHeader(
                centrePage,
                "Liste des batiments",
                "Vue d'ensemble des batiments",
                "Batiments",
                "/logoPropriete.png",
                20,
                20);

        // Boutons
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
        hautGrandeCarte.add(panelBoutonsHeader, BorderLayout.EAST);

        // Ajout de la liste dans le ScrollPane
        JScrollPane scroll = new JScrollPane(this.listeBatiments);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        centrePage.add(scroll, BorderLayout.CENTER);

        // 4. Connexion des événements
        this.btnAjouter.setActionCommand("AJOUTER_BATIMENT");
        this.btnModifier.setActionCommand("MODIFIER_BATIMENT");
        this.btnSupprimer.setActionCommand("SUPPRIMER_BATIMENT");

        this.btnAjouter.addActionListener(this.gestion);
        this.btnModifier.addActionListener(this.gestion);
        this.btnSupprimer.addActionListener(this.gestion);
        
        // Ajout du listener sur la liste
        this.listeBatiments.addMouseListener(this.gestion);

        // Chargement des données
        this.gestion.chargerDonneesBatimentsDepuisBDD();
    }

    private void styliserBoutonAction(JButton bouton) {
        bouton.setFocusPainted(false);
        bouton.setContentAreaFilled(true);
        bouton.setOpaque(true);
        bouton.setBackground(Color.WHITE);
        bouton.setForeground(new Color(30, 35, 45));
        bouton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        bouton.setBorder(new javax.swing.border.CompoundBorder(
                new javax.swing.border.LineBorder(new Color(255, 255, 255, 180), 1, true),
                new EmptyBorder(8, 14, 8, 14)));
        bouton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    // GETTERS
    public JList<BatimentItem> getListeBatiments() {
        return this.listeBatiments;
    }

    public DefaultListModel<BatimentItem> getModeleBatiments() {
        return this.modeleBatiments;
    }

    public JButton getBtnAjouter() {
        return this.btnAjouter;
    }

    public JButton getBtnModifier() {
        return this.btnModifier;
    }

    public JButton getBtnSupprimer() {
        return this.btnSupprimer;
    }

    //Item de la liste 
    public static class BatimentItem {
        private String idBatiment;
        private String adresseComplete;
        private String type;
        private String ville; 
        private String codePostal;
        private int nbEtages;

        public BatimentItem(String idBatiment, String adresseComplete, String type, String ville, String codePostal, int nbEtages) {
            this.idBatiment = idBatiment;
            this.adresseComplete = adresseComplete;
            this.type = type;
            this.ville = ville; 
            this.codePostal = codePostal;
            this.nbEtages = nbEtages;
        }

        public String getIdBatiment() { return this.idBatiment; }
        public String getAdresseComplete() { return this.adresseComplete; }
        public String getType() { return this.type; }
        public String getVille() { return this.ville; }
        public String getCodePostal() { return this.codePostal; }
        public int getNbEtages() { return this.nbEtages; }

        @Override
        public String toString() {
            return "[" + idBatiment + "] " + adresseComplete;
        }
    }

    //Renderer visuel 
    public static class BatimentRenderer extends JPanel implements ListCellRenderer<BatimentItem> {
        private JLabel lblTitre = new JLabel();
        private JLabel lblLigne2 = new JLabel();
        private JLabel lblLigne3 = new JLabel();
        private JLabel lblStatut = new JLabel();

        public BatimentRenderer() {
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
            this.lblStatut.setBorder(new EmptyBorder(2, 10, 2, 10));
            this.lblStatut.setFont(new Font("SansSerif", Font.BOLD, 11));

            panelDroite.add(this.lblStatut);
            panelDroite.add(Box.createVerticalStrut(12));

            this.add(panelTexte, BorderLayout.CENTER);
            this.add(panelDroite, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends BatimentItem> list,
                BatimentItem value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            this.lblTitre.setText("[" + value.getIdBatiment() + "] " + value.getAdresseComplete());
            this.lblLigne2.setText(value.getType() + " - " + value.getNbEtages() + " etages");
            this.lblLigne3.setText("Ville : " + value.getVille() + "  |  CP : " + value.getCodePostal());

            this.lblStatut.setText("Actif");
            this.lblStatut.setBackground(new Color(0, 140, 0));
            this.lblStatut.setForeground(Color.WHITE);

            if (isSelected) {
                this.setBackground(new Color(230, 238, 255));
            } else {
                this.setBackground(Color.WHITE);
            }

            return this;
        }
    }
}