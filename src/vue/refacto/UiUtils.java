package vue.refacto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Classe utilitaire pour les composants UI
 */
public final class UiUtils {

    // Couleurs de l'application
    public static final Color PRIMARY_COLOR = new Color(13, 76, 196);
    public static final Color SECONDARY_COLOR = new Color(230, 240, 255);
    public static final Color SUCCESS_COLOR = new Color(0, 160, 0);
    public static final Color DANGER_COLOR = new Color(200, 0, 0);
    public static final Color WARNING_COLOR = new Color(255, 165, 0);
    public static final Color NEUTRAL_COLOR = new Color(150, 150, 150);

    private UiUtils() {
        // Classe utilitaire : pas d'instance
    }

    /**
     * Charge une icône depuis le classpath et la redimensionne.
     */
    public static ImageIcon loadIcon(String resourcePath, int width, int height) {
        URL url = UiUtils.class.getResource(resourcePath);
        if (url == null) {
            return null;
        }
        Image img = new ImageIcon(url).getImage()
                .getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    /**
     * Crée un bouton de menu standard (bandeau haut).
     */
    public static JButton createMenuButton(String text, String actionCommand, 
                                           ActionListener listener, boolean selected) {
        JButton button = new JButton(text);
        if (actionCommand != null) {
            button.setActionCommand(actionCommand);
        }
        if (listener != null) {
            button.addActionListener(listener);
        }

        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(30, 35, 45));
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));

        button.setBorder(new CompoundBorder(
            new LineBorder(new Color(255, 255, 255, 180), 1, true),
            new EmptyBorder(8, 14, 8, 14)
        ));

        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 28));

        if (selected) {
            button.setBackground(SECONDARY_COLOR);
            button.setFont(button.getFont().deriveFont(Font.BOLD));
        }

        return button;
    }

    /**
     * Crée un bouton d'action standard
     */
    public static JButton createActionButton(String text, String actionCommand, 
                                             ActionListener listener) {
        JButton button = new JButton(text);
        button.setActionCommand(actionCommand);
        button.addActionListener(listener);
        button.setFocusPainted(false);
        return button;
    }

    /**
     * Crée un bouton "icône" utilisé dans le bandeau.
     */
    public static JButton createIconButton(String resourcePath, int iconWidth, int iconHeight,
                                           String actionCommand, ActionListener listener, 
                                           Color background) {
        JButton button = new JButton();
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBackground(background);

        ImageIcon icon = loadIcon(resourcePath, iconWidth, iconHeight);
        if (icon != null) {
            button.setIcon(icon);
        }

        if (actionCommand != null) {
            button.setActionCommand(actionCommand);
        }
        if (listener != null) {
            button.addActionListener(listener);
        }

        return button;
    }

    /**
     * Crée le bandeau haut complet avec logo + menu + icônes à droite.
     */
    public static JPanel createHeader(ActionListener navigationListener, String currentSection) {
        JPanel encadrementHaut = new JPanel(new BorderLayout());
        encadrementHaut.setBackground(PRIMARY_COLOR);
        encadrementHaut.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        // Partie gauche : logo
        JPanel encadrementGauche = new JPanel();
        encadrementGauche.setBackground(PRIMARY_COLOR);
        JLabel logo = new JLabel();
        ImageIcon logoIcon = loadIcon("/logo.png", 100, 100);
        if (logoIcon != null) {
            logo.setIcon(logoIcon);
        }
        encadrementGauche.add(logo);
        encadrementHaut.add(encadrementGauche, BorderLayout.WEST);

        // Partie centre : boutons de navigation
        JPanel encadrementCentre = new JPanel(new GridLayout(0, 4, 12, 8));
        encadrementCentre.setOpaque(false);
        encadrementCentre.setBorder(BorderFactory.createEmptyBorder(30, 90, 30, 90));
        encadrementHaut.add(encadrementCentre, BorderLayout.CENTER);

        // Boutons de navigation
        String[][] navButtons = {
            {"Liste des Logements", "OUVRIR_LISTE_LOGEMENTS", "LOGEMENTS"},
            {"Liste des Bâtiments", "OUVRIR_LISTE_BAT", "BATIMENTS"},
            {"Liste des Garages", "OUVRIR_LISTE_GARAGES", "GARAGES"},
            {"Liste des Locataires", "OUVRIR_LISTE_LOCATAIRES", "LOCATAIRES"},
            {"Gestion des baux", "OUVRIR_GESTION_BAILS", "BAUX"},
            {"Régularisation des charges", "OUVRIR_REGU_CHARGE", "REGU_CHARGES"},
            {"Entreprises", "OUVRIR_LISTE_ENTREPRISES", "ENTREPRISES"},
            {"Page principale", "OUVRIR_PAGE_PRINCIPALE", "ACCUEIL"}
        };

        for (String[] nav : navButtons) {
            encadrementCentre.add(createMenuButton(
                nav[0], nav[1], navigationListener, nav[2].equals(currentSection)
            ));
        }

        // Partie droite : icônes
        JPanel encadrementDroit = new JPanel(new GridBagLayout());
        encadrementDroit.setBackground(PRIMARY_COLOR);
        encadrementDroit.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        encadrementDroit.add(createIconButton("/engrenage.png", 15, 15, 
            "OUVRIR_GESTION_PARAM", navigationListener, PRIMARY_COLOR));
        encadrementDroit.add(createIconButton("/profile.png", 15, 15, 
            "OUVRIR_GESTION_COMPTE", navigationListener, PRIMARY_COLOR));

        encadrementHaut.add(encadrementDroit, BorderLayout.EAST);

        return encadrementHaut;
    }

    /**
     * Crée l'en-tête d'une page de liste.
     */
    public static JPanel createListHeader(JPanel centrePage, String titrePage, 
                                          String sousTitre, String titreBloc,
                                          String iconPath, int iconWidth, int iconHeight) {
        JPanel panelHeader1 = new JPanel();
        panelHeader1.setLayout(new BoxLayout(panelHeader1, BoxLayout.Y_AXIS));
        panelHeader1.setOpaque(false);
        panelHeader1.setBorder(new EmptyBorder(10, 24, 8, 24));
        centrePage.add(panelHeader1, BorderLayout.NORTH);

        // Gros titre
        JLabel lblTitrePage = new JLabel(titrePage);
        lblTitrePage.setFont(new Font("Verdana", Font.BOLD, 30));
        lblTitrePage.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelHeader1.add(lblTitrePage);

        // Sous-titre
        if (sousTitre != null && !sousTitre.isEmpty()) {
            JLabel lblSousTitre = new JLabel(sousTitre);
            lblSousTitre.setFont(new Font("Verdana", Font.PLAIN, 13));
            lblSousTitre.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelHeader1.add(lblSousTitre);
        }

        // Bandeau "grande carte"
        JPanel hautGrandeCarte = new JPanel(new BorderLayout());
        hautGrandeCarte.setOpaque(false);
        hautGrandeCarte.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelHeader1.add(hautGrandeCarte);

        // Titre de la section
        JLabel lblTitreBloc = new JLabel(titreBloc);
        lblTitreBloc.setBorder(new EmptyBorder(10, 0, 0, 0));
        lblTitreBloc.setFont(new Font("Tahoma", Font.PLAIN, 20));

        ImageIcon icone = loadIcon(iconPath, iconWidth, iconHeight);
        if (icone != null) {
            lblTitreBloc.setIcon(icone);
        }
        lblTitreBloc.setIconTextGap(8);

        hautGrandeCarte.add(lblTitreBloc, BorderLayout.WEST);

        return hautGrandeCarte;
    }

    /**
     * Crée un panel de boutons d'action (Ajouter, Modifier, Supprimer)
     */
    public static JPanel createActionButtonsPanel(ActionListener listener, 
                                                   String entityName,
                                                   boolean withAdd, 
                                                   boolean withModify, 
                                                   boolean withDelete) {
        JPanel panel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        panel.setOpaque(false);

        if (withAdd) {
            panel.add(createMenuButton("Ajouter", "AJOUTER_" + entityName, listener, false));
        }
        if (withModify) {
            panel.add(createMenuButton("Modifier", "MODIFIER_" + entityName, listener, false));
        }
        if (withDelete) {
            panel.add(createMenuButton("Supprimer", "SUPPRIMER_" + entityName, listener, false));
        }

        return panel;
    }

    /**
     * Crée un label de statut coloré
     */
    public static JLabel createStatusLabel(String text, Color bgColor) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(bgColor);
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label.setBorder(new EmptyBorder(4, 12, 4, 12));
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        return label;
    }
}
