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

import Controleur.Principale.gestionListeDesEntreprises;
import vue.refacto.UiUtils;
import modele.Entreprise;

/**
 * Page de liste des entreprises (prestataires, artisans, fournisseurs)
 */
public class pageListeDesEntreprises extends JDialog {

    private static final long serialVersionUID = 1L;

    private JPanel contentPane;

    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;

    private JList<EntrepriseItem> listeEntreprises;
    private DefaultListModel<EntrepriseItem> modeleEntreprises;

    private gestionListeDesEntreprises gestion;

    public static void main(String[] args) {
        try {
            pageListeDesEntreprises dialog = new pageListeDesEntreprises();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public pageListeDesEntreprises() {
        this.gestion = new gestionListeDesEntreprises(this);

        setTitle("Gestion des entreprises");
        this.setBounds(100, 100, 1225, 744);
        this.contentPane = new JPanel();
        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setContentPane(this.contentPane);
        this.contentPane.setLayout(new BorderLayout(0, 0));

        // BANDEAU HAUT
        JPanel encadrementHaut = UiUtils.createHeader(this.gestion, "ENTREPRISES");
        this.contentPane.add(encadrementHaut, BorderLayout.NORTH);

        // CENTRE
        JPanel centrePage = new JPanel();
        this.contentPane.add(centrePage, BorderLayout.CENTER);
        centrePage.setLayout(new BorderLayout(0, 0));

        //  En-tete de page 
        JPanel hautGrandeCarte = UiUtils.createListHeader(
                centrePage,
                "Liste des entreprises",
                "Prestataires, artisans et fournisseurs",
                "Entreprises enregistrees",
                "/logoEntreprise.png",
                20,
                20);

        //  Boutons d'action 
        JPanel panelBoutonsHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBoutonsHeader.setOpaque(false);
        panelBoutonsHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        this.btnAjouter = UiUtils.createMenuButton("Ajouter", "AJOUTER_ENTREPRISE", this.gestion, false);
        this.btnModifier = UiUtils.createMenuButton("Modifier", "MODIFIER_ENTREPRISE", this.gestion, false);
        this.btnSupprimer = UiUtils.createMenuButton("Supprimer", "SUPPRIMER_ENTREPRISE", this.gestion, false);

        panelBoutonsHeader.add(this.btnAjouter);
        panelBoutonsHeader.add(this.btnModifier);
        panelBoutonsHeader.add(this.btnSupprimer);
        hautGrandeCarte.add(panelBoutonsHeader, BorderLayout.EAST);

        // Liste des entreprises
        this.modeleEntreprises = new DefaultListModel<>();
        this.listeEntreprises = new JList<>(this.modeleEntreprises);
        this.listeEntreprises.setCellRenderer(new EntrepriseRenderer());
        this.listeEntreprises.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.listeEntreprises.setBackground(new Color(245, 245, 245));
        this.listeEntreprises.setFixedCellHeight(100); 

        JScrollPane scroll = new JScrollPane(this.listeEntreprises);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        centrePage.add(scroll, BorderLayout.CENTER);

        //  Chargement des donnees 
        this.gestion.chargerDonneesEntreprisesDepuisBDD();
    }

    // GETTERS
    public JList<EntrepriseItem> getListeEntreprises() {
        return this.listeEntreprises;
    }

    public DefaultListModel<EntrepriseItem> getModeleEntreprises() {
        return this.modeleEntreprises;
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
    public static class EntrepriseItem {
        private final Entreprise entreprise;
        private final String titre;
        private final String ligne2;
        private final String ligne3;
        private final String ligne4;  
        private int nbFactures;

        public EntrepriseItem(Entreprise entreprise, int nbFactures) {
            this.entreprise = entreprise;
            this.nbFactures = nbFactures;

            this.titre = entreprise.getNomEntreprise();

            // Ligne 2 : Specialite + Telephone
            StringBuilder sb2 = new StringBuilder();
            if (entreprise.getSpecialite() != null && !entreprise.getSpecialite().isEmpty()) {
                sb2.append("Specialite : ").append(entreprise.getSpecialite());
            }
            if (entreprise.getTelephone() != null && !entreprise.getTelephone().isEmpty()) {
                if (sb2.length() > 0) sb2.append(" - ");
                sb2.append("Tel : ").append(entreprise.getTelephone());
            }
            this.ligne2 = sb2.length() > 0 ? sb2.toString() : "Pas de specialite renseignee";

            // Ligne 3 : SIRET + Email
            StringBuilder sb3 = new StringBuilder();
            sb3.append("SIRET : ").append(entreprise.getSiretFormate());
            if (entreprise.getEmail() != null && !entreprise.getEmail().isEmpty()) {
                sb3.append(" - ").append(entreprise.getEmail());
            }
            this.ligne3 = sb3.toString();
            
            // AJOUT Ligne 4 : Adresse
            if (entreprise.getAdresse() != null && !entreprise.getAdresse().isEmpty()) {
                this.ligne4 = "Adresse : " + entreprise.getAdresse();
            } else {
                this.ligne4 = "Adresse : (non renseignee)";
            }
        }

        public Entreprise getEntreprise() {
            return this.entreprise;
        }

        public String getTitre() {
            return this.titre;
        }

        public String getLigne2() {
            return this.ligne2;
        }

        public String getLigne3() {
            return this.ligne3;
        }
        
        public String getLigne4() {
            return this.ligne4;
        }

        public int getNbFactures() {
            return nbFactures;
        }

        @Override
        public String toString() {
            return this.titre;
        }
    }

    // Renderer visuel
    public static class EntrepriseRenderer extends JPanel implements ListCellRenderer<EntrepriseItem> {

        private final JLabel lblTitre = new JLabel();
        private final JLabel lblLigne2 = new JLabel();
        private final JLabel lblLigne3 = new JLabel();
        private final JLabel lblLigne4 = new JLabel();  // AJOUT
        private final JLabel lblFactures = new JLabel();

        public EntrepriseRenderer() {
            this.setLayout(new BorderLayout(10, 5));
            this.setOpaque(true);
            this.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(6, 12, 6, 12),
                    BorderFactory.createLineBorder(new Color(220, 220, 220))));

            // Icone entreprise a gauche
            JLabel lblIcon = new JLabel();
            lblIcon.setIcon(UiUtils.loadIcon("/logoEntreprise.png", 40, 40));
            lblIcon.setBorder(new EmptyBorder(0, 5, 0, 10));
            this.add(lblIcon, BorderLayout.WEST);

            JPanel panelTexte = new JPanel();
            panelTexte.setLayout(new BoxLayout(panelTexte, BoxLayout.Y_AXIS));
            panelTexte.setOpaque(false);

            this.lblTitre.setFont(new Font("SansSerif", Font.BOLD, 14));
            this.lblLigne2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            this.lblLigne3.setFont(new Font("SansSerif", Font.PLAIN, 11));
            this.lblLigne3.setForeground(new Color(90, 90, 90));
            this.lblLigne4.setFont(new Font("SansSerif", Font.PLAIN, 11));
            this.lblLigne4.setForeground(new Color(90, 90, 90));

            panelTexte.add(this.lblTitre);
            panelTexte.add(Box.createVerticalStrut(2));
            panelTexte.add(this.lblLigne2);
            panelTexte.add(Box.createVerticalStrut(2));
            panelTexte.add(this.lblLigne3);
            panelTexte.add(Box.createVerticalStrut(2));
            panelTexte.add(this.lblLigne4);  // AJOUT

            this.add(panelTexte, BorderLayout.CENTER);

            // Droite : nombre de factures
            JPanel panelDroite = new JPanel();
            panelDroite.setLayout(new BoxLayout(panelDroite, BoxLayout.Y_AXIS));
            panelDroite.setOpaque(false);

            this.lblFactures.setOpaque(true);
            this.lblFactures.setHorizontalAlignment(SwingConstants.CENTER);
            this.lblFactures.setBorder(new EmptyBorder(2, 10, 2, 10));
            this.lblFactures.setFont(new Font("SansSerif", Font.BOLD, 11));

            panelDroite.add(Box.createVerticalGlue());
            panelDroite.add(this.lblFactures);
            panelDroite.add(Box.createVerticalGlue());

            this.add(panelDroite, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends EntrepriseItem> list,
                EntrepriseItem value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            this.lblTitre.setText(value.getTitre());
            this.lblLigne2.setText(value.getLigne2());
            this.lblLigne3.setText(value.getLigne3());
            this.lblLigne4.setText(value.getLigne4());  // AJOUT

            int nbFactures = value.getNbFactures();
            if (nbFactures > 0) {
                this.lblFactures.setText(nbFactures + " facture(s)");
                this.lblFactures.setBackground(new Color(30, 102, 245));
                this.lblFactures.setForeground(Color.WHITE);
            } else {
                this.lblFactures.setText("Aucune facture");
                this.lblFactures.setBackground(new Color(200, 200, 200));
                this.lblFactures.setForeground(Color.DARK_GRAY);
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