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

import Controleur.Principale.gestionListeDesGarages;
import vue.refacto.UiUtils;
import modele.Garage;

public class pageListeDesGarages extends JDialog {

    private static final long serialVersionUID = 1L;

    private JPanel contentPane;
    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;

    private gestionListeDesGarages gestion;

    private JList<GarageItem> listeGarages;
    private DefaultListModel<GarageItem> modeleGarages;

    public pageListeDesGarages() {
        // ÉTAPE 1 : Initialiser la JList EN PREMIER
        modeleGarages = new DefaultListModel<>();
        listeGarages = new JList<>(modeleGarages);
        
        listeGarages.setCellRenderer(new GarageRenderer());
        listeGarages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listeGarages.setBackground(new Color(245, 245, 245));
        listeGarages.setFixedCellHeight(90);

        // ÉTAPE 2 : Initialiser le Contrôleur
        gestion = new gestionListeDesGarages(this);

        // ÉTAPE 3 : Construction de l'interface (UI)
        setTitle("Gestion des Garages");
        setBounds(100, 100, 1225, 744);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        // Header Principal
        JPanel encadrementHaut = UiUtils.createHeader(gestion, "GARAGES");
        contentPane.add(encadrementHaut, BorderLayout.NORTH);

        JPanel centrePage = new JPanel(new BorderLayout());
        contentPane.add(centrePage, BorderLayout.CENTER);

        // Header de la liste
        JPanel hautGrandeCarte = UiUtils.createListHeader(
                centrePage,
                "Liste des garages",
                "Vue d'ensemble des garages",
                "Garages récents",
                "/logoPropriete.png",
                20, 20);

        JPanel panelBoutonsHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBoutonsHeader.setOpaque(false);

        btnAjouter = UiUtils.createMenuButton("Ajouter", "AJOUTER_GARAGE", gestion, false);
        btnModifier = UiUtils.createMenuButton("Modifier", "MODIFIER_GARAGE", gestion, false);
        btnSupprimer = UiUtils.createMenuButton("Supprimer", "SUPPRIMER_GARAGE", gestion, false);

        panelBoutonsHeader.add(btnAjouter);
        panelBoutonsHeader.add(btnModifier);
        panelBoutonsHeader.add(btnSupprimer);
        hautGrandeCarte.add(panelBoutonsHeader, BorderLayout.EAST);

        // Ajout de la liste  dans le ScrollPane
        JScrollPane scroll = new JScrollPane(listeGarages);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        centrePage.add(scroll, BorderLayout.CENTER);

        // Ajout du MouseListener
     
        listeGarages.addMouseListener(gestion);
        
        // Chargement final des données
        gestion.chargerDonnees();
    }

    public JList<GarageItem> getListeGarages() {
        return listeGarages;
    }

    public DefaultListModel<GarageItem> getModeleGarages() {
        return modeleGarages;
    }

    public JButton getBtnAjouter() { return btnAjouter; }
    public JButton getBtnModifier() { return btnModifier; }
    public JButton getBtnSupprimer() { return btnSupprimer; }

    // Item de la liste 
    public static class GarageItem {
        private final Garage garage;
        private final int id;
        private final String titre;
        private final String ligne2;
        private final String ligne3;
        private final String statut;

        public GarageItem(Garage garage) {
            this.garage = garage;
            this.id = garage.getIdGarage();
            this.titre = garage.getAdresseGarage() != null 
                    ? garage.getAdresseGarage() 
                    : "Garage #" + garage.getIdGarage();
            this.ligne2 = "Surface : " + 
                    (garage.getSurfaceGarage() != null ? garage.getSurfaceGarage() + " m²" : "Non renseignée") +
                    " - N° Fiscal : " + garage.getNumFiscal();
            this.ligne3 = "ID Bâtiment : " + 
                    (garage.getIdBatiment() != null ? garage.getIdBatiment() : "Non lié");
            this.statut = garage.isLoue() ? "Loué" : "Libre";
        }

        public Garage getGarage() { return garage; }
        public int getIdGarage() { return id; }
        public String getTitre() { return titre; }
        public String getLigne2() { return ligne2; }
        public String getLigne3() { return ligne3; }
        public String getStatut() { return statut; }
        @Override public String toString() { return titre; }
    }

    // Renderer 
    public static class GarageRenderer extends JPanel implements ListCellRenderer<GarageItem> {
        private final JLabel lblTitre = new JLabel();
        private final JLabel lblDetails = new JLabel();
        private final JLabel lblInfo = new JLabel();
        private final JLabel lblStatut = new JLabel();

        public GarageRenderer() {
            setLayout(new BorderLayout(10, 5));
            setOpaque(true);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(6, 12, 6, 12),
                    BorderFactory.createLineBorder(new Color(220, 220, 220))));

            JPanel panelTexte = new JPanel();
            panelTexte.setLayout(new BoxLayout(panelTexte, BoxLayout.Y_AXIS));
            panelTexte.setOpaque(false);

            lblTitre.setFont(new Font("SansSerif", Font.BOLD, 14));
            lblDetails.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblInfo.setForeground(new Color(90, 90, 90));

            panelTexte.add(lblTitre);
            panelTexte.add(Box.createVerticalStrut(2));
            panelTexte.add(lblDetails);
            panelTexte.add(Box.createVerticalStrut(2));
            panelTexte.add(lblInfo);

            JPanel panelDroite = new JPanel();
            panelDroite.setLayout(new BoxLayout(panelDroite, BoxLayout.Y_AXIS));
            panelDroite.setOpaque(false);

            lblStatut.setOpaque(true);
            lblStatut.setHorizontalAlignment(SwingConstants.CENTER);
            lblStatut.setBorder(new EmptyBorder(2, 10, 2, 10));
            lblStatut.setFont(new Font("SansSerif", Font.BOLD, 11));

            panelDroite.add(lblStatut);
            add(panelTexte, BorderLayout.CENTER);
            add(panelDroite, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends GarageItem> list,
                GarageItem item,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            lblTitre.setText("[" + item.getIdGarage() + "] " + item.getTitre());
            lblDetails.setText(item.getLigne2());
            lblInfo.setText(item.getLigne3());
            lblStatut.setText(item.getStatut());

            if ("Libre".equalsIgnoreCase(item.getStatut())) {
                lblStatut.setBackground(new Color(0, 160, 0));
                lblStatut.setForeground(Color.WHITE);
            } else {
                lblStatut.setBackground(new Color(200, 0, 0));
                lblStatut.setForeground(Color.WHITE);
            }

            setBackground(isSelected ? new Color(230, 238, 255) : Color.WHITE);
            return this;
        }
        
        
    }
}