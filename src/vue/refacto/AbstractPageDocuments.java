package vue.refacto;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public abstract class AbstractPageDocuments<T> extends JDialog {

    private static final long serialVersionUID = 1L;

    /** Entité (Logement, LocataireItem, Garage, Batiment...) */
    protected final T entity;

    protected final JPanel contentPane;
    protected final JPanel header;
    protected final JTabbedPane tabs;
    protected final JPanel bottomPanel;
    protected final JButton btnFermer;

    /** Contrôleur MVC  */
    private ActionListener controller;
    /** ActionCommand à envoyer au contrôleur pour le bouton Fermer */
    private String closeCommand;

    protected AbstractPageDocuments(T entity,
                                    String windowTitle,
                                    String headerTitle) {
        this.entity = entity;

        setTitle(windowTitle);
        setBounds(200, 200, 900, 650);
        setModal(true);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        // HEADER 
        header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(0, 5, 10, 5));
        JLabel lblTitre = new JLabel(headerTitle);
        lblTitre.setFont(new Font("Verdana", Font.BOLD, 20));
        header.add(lblTitre, BorderLayout.WEST);
        contentPane.add(header, BorderLayout.NORTH);

        //  TABS
        tabs = new JTabbedPane();
        contentPane.add(tabs, BorderLayout.CENTER);

        // BAS 
        bottomPanel = new JPanel();
        btnFermer = new JButton("Fermer");
        btnFermer.setPreferredSize(new Dimension(120, 30));
        btnFermer.addActionListener(e -> {
            if (controller != null && closeCommand != null) {
                ActionEvent ev = new ActionEvent(
                        e.getSource(),
                        e.getID(),
                        closeCommand
                );
                controller.actionPerformed(ev);
            } else {
                dispose();
            }
        });
        bottomPanel.add(btnFermer);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        // Laisse la sous-classe construire ses onglets
        buildTabs();
    }

    /**
     * À appeler dans le constructeur concret après avoir créé le contrôleur.
     */
    protected void setController(ActionListener controller, String closeCommand) {
        this.controller = controller;
        this.closeCommand = closeCommand;
    }

    protected ActionListener getController() {
        return controller;
    }

    /**
     * Crée un onglet complet :
     * header (texte à gauche) + 3 boutons (Ajouter/Modifier/Supprimer) +
     * JTable non éditable.
     */
    protected JTable addTableTab(String tabTitle, String headerText, String[] columns, String actionKey) {
        // On appelle la nouvelle méthode en demandant d'afficher le bouton Modifier (true)
        return addTableTab(tabTitle, headerText, columns, actionKey, true);
    }

    /**
     * Permet de choisir si on affiche le bouton Modifier.
     */
    protected JTable addTableTab(String tabTitle,
                                 String headerText,
                                 String[] columns,
                                 String actionKey,
                                 boolean withModifyBtn) {
        return addTableTab(tabTitle, headerText, columns, actionKey, true, withModifyBtn, true, null);
    }

    /**
     * MÉTHODE COMPLÈTE : Permet de personnaliser tous les boutons
     */
    protected JTable addTableTab(String tabTitle,
                                 String headerText,
                                 String[] columns,
                                 String actionKey,
                                 boolean withAddBtn,
                                 boolean withModifyBtn,
                                 boolean withDeleteBtn,
                                 String addButtonLabel) {

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tabs.addTab(tabTitle, panel);

        // Header de l'onglet
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel(headerText);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        headerPanel.add(lbl, BorderLayout.WEST);

        JPanel boutons = new JPanel();

        // 1. Bouton Ajouter (Conditionnel)
        if (withAddBtn) {
            String label = (addButtonLabel != null) ? addButtonLabel : "Ajouter";
            JButton btnAjouter = new JButton(label);
            btnAjouter.setActionCommand("AJOUTER_" + actionKey);
            btnAjouter.addActionListener(e -> {
                if (controller != null) controller.actionPerformed(e);
            });
            boutons.add(btnAjouter);
        }

        // 2. Bouton Modifier (Conditionnel)
        if (withModifyBtn) {
            JButton btnModifier = new JButton("Modifier");
            btnModifier.setActionCommand("MODIFIER_" + actionKey);
            btnModifier.addActionListener(e -> {
                if (controller != null) controller.actionPerformed(e);
            });
            boutons.add(btnModifier);
        }

        // 3. Bouton Supprimer (Conditionnel)
        if (withDeleteBtn) {
            JButton btnSupprimer = new JButton("Supprimer");
            btnSupprimer.setActionCommand("SUPPRIMER_" + actionKey);
            btnSupprimer.addActionListener(e -> {
                if (controller != null) controller.actionPerformed(e);
            });
            boutons.add(btnSupprimer);
        }

        headerPanel.add(boutons, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // JTable
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return table;
    }

    /** Chaque sous-classe crée ses onglets ici. */
    protected abstract void buildTabs();
}