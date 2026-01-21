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

import Controleur.Principale.gestionListeDesLogements;
import vue.refacto.UiUtils;
import modele.Logement;

public class pageListeDesLogements extends JDialog {

    private static final long serialVersionUID = 1L;

    private JPanel contentPane;

    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;

    private JList<LogementItem> listeLogements;
    private DefaultListModel<LogementItem> modeleLogements;

    private gestionListeDesLogements gestion;

    public pageListeDesLogements() {
        // 1. D'ABORD : Créer la JList et son Modèle
        this.modeleLogements = new DefaultListModel<>();
        this.listeLogements = new JList<>(this.modeleLogements);
        
        // Configuration de la liste
        this.listeLogements.setCellRenderer(new LogementRenderer());
        this.listeLogements.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.listeLogements.setBackground(new Color(245, 245, 245));
        this.listeLogements.setFixedCellHeight(90);

        // 2. ENSUITE : Créer le Contrôleur
        this.gestion = new gestionListeDesLogements(this);

        // 3. ENFIN : Construction de l'interface (UI)
        this.setBounds(100, 100, 1225, 744);
        this.contentPane = new JPanel();
        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setContentPane(this.contentPane);
        this.contentPane.setLayout(new BorderLayout(0, 0));

        // Bandeau Haut
        JPanel encadrementHaut = UiUtils.createHeader(this.gestion, "LOGEMENTS");
        this.contentPane.add(encadrementHaut, BorderLayout.NORTH);

        // Centre
        JPanel centrePage = new JPanel();
        this.contentPane.add(centrePage, BorderLayout.CENTER);
        centrePage.setLayout(new BorderLayout(0, 0));

        // En-tête de liste
        JPanel hautGrandeCarte = UiUtils.createListHeader(
                centrePage,
                "Liste des logements",
                "Vue d'ensemble des logements",
                "Logements récents",
                "/logoPropriete.png",
                20,
                20);

        // Boutons d'action
        JPanel panelBoutonsHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBoutonsHeader.setOpaque(false);
        panelBoutonsHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        this.btnAjouter = UiUtils.createMenuButton("Ajouter", "AJOUTER_LOGEMENT", this.gestion, false);
        this.btnModifier = UiUtils.createMenuButton("Modifier", "MODIFIER_LOGEMENT", this.gestion, false);
        this.btnSupprimer = UiUtils.createMenuButton("Supprimer", "SUPPRIMER_LOGEMENT", this.gestion, false);

        panelBoutonsHeader.add(this.btnAjouter);
        panelBoutonsHeader.add(this.btnModifier);
        panelBoutonsHeader.add(this.btnSupprimer);
        hautGrandeCarte.add(panelBoutonsHeader, BorderLayout.EAST);

        // Ajout de la liste dans le ScrollPane
        JScrollPane scroll = new JScrollPane(this.listeLogements);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        centrePage.add(scroll, BorderLayout.CENTER);

        // Ajout du listener
        this.listeLogements.addMouseListener(this.gestion);

        // Chargement des données
        this.gestion.chargerDonneesLogementsDepuisBDD();
    }

    //  GETTERS 
    public JList<LogementItem> getListeLogements() {
        return this.listeLogements;
    }

    public DefaultListModel<LogementItem> getModeleLogements() {
        return this.modeleLogements;
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

    // Item de la liste 
    public static class LogementItem {
        private final Logement logement;
        private final String id;
        private final String titre;
        private final String ligne2;
        private final String ligne3;
        private final String statut;  // "Libre" ou "Loué"

        public LogementItem(Logement logement, String adresseComplete) {
            this(logement, adresseComplete, logement.isLoue() ? "Loué" : "Libre");
        }

        public LogementItem(Logement logement, String adresseComplete, String statut) {
            this.logement = logement;

            this.titre = (adresseComplete != null && !adresseComplete.trim().isEmpty())
                    ? adresseComplete
                    : "Adresse inconnue";
            
            this.id = logement.getIdLogement();

            this.ligne2 = (logement.getTypeLogement() != null
                    ? logement.getTypeLogement()
                    : "Type ?")
                    + " - " + logement.getSurface() + " m² - "
                    + logement.getNbPieces() + " pièces";

            this.ligne3 = "N° Fiscal : " + logement.getNumFiscal();
            this.statut = statut;
        }

        public Logement getLogement() { return this.logement; }
        public String getIdLogement() { return this.id; }
        public String getTitre() { return this.titre; }
        public String getLigne2() { return this.ligne2; }
        public String getLigne3() { return this.ligne3; }
        public String getStatut() { return this.statut; }

        @Override
        public String toString() { return this.titre; }
    }

    // Renderer visuel
    public static class LogementRenderer extends JPanel implements ListCellRenderer<LogementItem> {

        private final JLabel lblTitre = new JLabel();
        private final JLabel lblLigne2 = new JLabel();
        private final JLabel lblLigne3 = new JLabel();
        private final JLabel lblStatut = new JLabel();
        private final JButton btnDocs = new JButton("Documents");

        public LogementRenderer() {
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

            this.btnDocs.setFocusPainted(false);
            this.btnDocs.setFont(new Font("SansSerif", Font.PLAIN, 11));
            this.btnDocs.setBackground(new Color(240, 240, 240));
            this.btnDocs.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

            JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            panelBoutons.setOpaque(false);
            panelBoutons.add(this.btnDocs);

            panelDroite.add(this.lblStatut);
            panelDroite.add(Box.createVerticalStrut(12));
            panelDroite.add(panelBoutons);

            this.add(panelTexte, BorderLayout.CENTER);
            this.add(panelDroite, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends LogementItem> list,
                LogementItem value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            this.lblTitre.setText("[" + value.getIdLogement() + "] " +value.getTitre());
            this.lblLigne2.setText(value.getLigne2());
            this.lblLigne3.setText(value.getLigne3());
            this.lblStatut.setText(value.getStatut());

            // Couleur du statut selon disponibilité
            if ("Libre".equalsIgnoreCase(value.getStatut())) {
                this.lblStatut.setBackground(new Color(0, 160, 0));  // Vert
                this.lblStatut.setForeground(Color.WHITE);
            } else {
                this.lblStatut.setBackground(new Color(200, 0, 0));  // Rouge
                this.lblStatut.setForeground(Color.WHITE);
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