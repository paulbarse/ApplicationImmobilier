package vue.Principale;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Controleur.Principale.gestionGestionDesBaux;
import vue.refacto.UiUtils;

/**
 * Page Gestion des Baux
 */
public class pageGestionDesBaux extends JDialog {

    private static final long serialVersionUID = 1L;

    private JPanel contentPane;
    private JTable tableBails;
    private JTable tableLoyers;
    private JTable tableFactures;

    private JButton btnAjouterBail;
    private JButton btnModifierBail;
    private JButton btnCloturerBail;
    private JButton btnRevaloriserLoyer;
    private JButton btnHistoriqueBaux; 
    private JButton btnImportLoyersCsv;

    private JButton btnAjouterFacture;
    private JButton btnPayerFacture;

    private gestionGestionDesBaux gestion;

    public pageGestionDesBaux() {
        this.gestion = new gestionGestionDesBaux(this);

        setTitle("Gestion des baux");
        this.setBounds(100, 100, 1225, 744);
        this.contentPane = new JPanel();
        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setContentPane(this.contentPane);
        this.contentPane.setLayout(new BorderLayout(0, 0));

        JPanel encadrementHaut = UiUtils.createHeader(this.gestion, "BAUX");
        this.contentPane.add(encadrementHaut, BorderLayout.NORTH);

        JPanel centrePage = new JPanel(new BorderLayout());
        this.contentPane.add(centrePage, BorderLayout.CENTER);

        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBorder(new EmptyBorder(10, 24, 8, 24));
        panelHeader.setOpaque(false);

        JPanel panelHeaderLeft = new JPanel();
        panelHeaderLeft.setOpaque(false);
        panelHeaderLeft.setLayout(new BoxLayout(panelHeaderLeft, BoxLayout.Y_AXIS));

        JLabel titreDeLaPage = new JLabel("Gestion des baux");
        titreDeLaPage.setFont(new Font("Verdana", Font.BOLD, 30));
        titreDeLaPage.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelHeaderLeft.add(titreDeLaPage);

        JLabel specificationDeLaPage = new JLabel("Contrats, loyers et factures par logement");
        specificationDeLaPage.setFont(new Font("Verdana", Font.PLAIN, 13));
        specificationDeLaPage.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelHeaderLeft.add(specificationDeLaPage);

        JLabel titreListeBails = new JLabel("Liste des baux");
        titreListeBails.setFont(new Font("Tahoma", Font.PLAIN, 20));
        titreListeBails.setAlignmentX(Component.LEFT_ALIGNMENT);
        titreListeBails.setIcon(UiUtils.loadIcon("/logoBail.png", 20, 20));
        titreListeBails.setIconTextGap(8);
        panelHeaderLeft.add(titreListeBails);

        panelHeader.add(panelHeaderLeft, BorderLayout.WEST);

        JPanel panelBoutonsHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBoutonsHeader.setOpaque(false);

        btnAjouterBail = creerBoutonAction("Ajouter bail", "AJOUTER_BAIL");
        btnModifierBail = creerBoutonAction("Modifier", "MODIFIER_BAIL");
        btnCloturerBail = creerBoutonAction("Cloturer", "CLORE_BAIL");
        btnRevaloriserLoyer = creerBoutonAction("Revaloriser loyer", "REVALORISER_LOYER");
        
        btnHistoriqueBaux = creerBoutonAction("Historique baux", "HISTORIQUE_BAUX_CLOTURES");
        
        btnImportLoyersCsv = creerBoutonAction("Import CSV", "IMPORT_LOYERS_CSV");

        panelBoutonsHeader.add(btnAjouterBail);
        panelBoutonsHeader.add(btnModifierBail);
        panelBoutonsHeader.add(btnCloturerBail);
        panelBoutonsHeader.add(btnRevaloriserLoyer);
        panelBoutonsHeader.add(btnHistoriqueBaux);
        panelBoutonsHeader.add(btnImportLoyersCsv);

        panelHeader.add(panelBoutonsHeader, BorderLayout.EAST);
        centrePage.add(panelHeader, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.5);
        split.setBorder(null);
        split.setContinuousLayout(true);
        centrePage.add(split, BorderLayout.CENTER);

        // TABLEAU DES BAUX 
        String[] colonnesBails = { "Logement", "Locataire", "Date debut", "Date fin", "Loyer initial (EUR)", "Caution (EUR)", "Statut" };
        DefaultTableModel modelBails = new DefaultTableModel(colonnesBails, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        this.tableBails = new JTable(modelBails);
        this.tableBails.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.tableBails.getTableHeader().setReorderingAllowed(false);
        this.tableBails.getSelectionModel().addListSelectionListener(this.gestion);

        JScrollPane scrollBails = new JScrollPane(this.tableBails);
        scrollBails.setBorder(new EmptyBorder(10, 24, 5, 24));
        split.setTopComponent(scrollBails);

        // LOYERS + FACTURES
        JPanel panelDetails = new JPanel(new GridLayout(1, 2, 16, 0));
        panelDetails.setBorder(new EmptyBorder(10, 24, 10, 24));
        panelDetails.setOpaque(false);

        //  Loyers
        JPanel panelLoyers = new JPanel(new BorderLayout());
        panelLoyers.setOpaque(false);

        JPanel headerLoyers = new JPanel(new BorderLayout());
        headerLoyers.setOpaque(false);
        JLabel lblLoyers = new JLabel("Loyers");
        lblLoyers.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblLoyers.setIcon(UiUtils.loadIcon("/logoLoyer.png", 18, 18));
        lblLoyers.setIconTextGap(8);
        headerLoyers.setBorder(new EmptyBorder(0, 0, 4, 0));
        headerLoyers.add(lblLoyers, BorderLayout.WEST);
        panelLoyers.add(headerLoyers, BorderLayout.NORTH);

        String[] colonnesLoyers = { "Mois", "Exercice", "Montant loyer", "Montant provisions", "Statut" };
        DefaultTableModel modelLoyers = new DefaultTableModel(colonnesLoyers, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        this.tableLoyers = new JTable(modelLoyers);
        this.tableLoyers.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollLoyers = new JScrollPane(this.tableLoyers);
        panelLoyers.add(scrollLoyers, BorderLayout.CENTER);
        panelDetails.add(panelLoyers);

        //  Factures / Travaux 
        JPanel panelFactures = new JPanel(new BorderLayout());
        panelFactures.setOpaque(false);

        JPanel headerFactures = new JPanel(new BorderLayout());
        headerFactures.setOpaque(false);

        JLabel lblFactures = new JLabel("Factures / Travaux");
        lblFactures.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblFactures.setIcon(UiUtils.loadIcon("/logoFacture.png", 18, 18));
        lblFactures.setIconTextGap(8);
        headerFactures.add(lblFactures, BorderLayout.WEST);

        btnAjouterFacture = new JButton("+ Ajouter");
        btnAjouterFacture.setActionCommand("AJOUTER_FACTURE");
        btnAjouterFacture.addActionListener(gestion);
        btnAjouterFacture.setEnabled(false);

        btnPayerFacture = new JButton("Payer");
        btnPayerFacture.setActionCommand("PAYER_FACTURE");
        btnPayerFacture.addActionListener(gestion);
        btnPayerFacture.setEnabled(false);

        JPanel panelBtnFactures = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        panelBtnFactures.setOpaque(false);
        panelBtnFactures.add(btnPayerFacture);
        panelBtnFactures.add(btnAjouterFacture);
        headerFactures.add(panelBtnFactures, BorderLayout.EAST);

        headerFactures.setBorder(new EmptyBorder(0, 0, 4, 0));
        panelFactures.add(headerFactures, BorderLayout.NORTH);

        String[] colonnesFactures = {
            "ID",
            "Date",
            "Nature",
            "Entreprise",
            "Montant TTC",
            "Travaux",
            "Devis (EUR)",
            "Type",
            "Statut"
        };
        DefaultTableModel modelFactures = new DefaultTableModel(colonnesFactures, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        this.tableFactures = new JTable(modelFactures);
        this.tableFactures.getTableHeader().setReorderingAllowed(false);
        this.tableFactures.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.tableFactures.getSelectionModel().addListSelectionListener(gestion);

        JScrollPane scrollFactures = new JScrollPane(this.tableFactures);
        panelFactures.add(scrollFactures, BorderLayout.CENTER);
        panelDetails.add(panelFactures);

        split.setBottomComponent(panelDetails);
        split.setDividerLocation(250);

        gestion.chargerDonneesBaux();
    }

    private JButton creerBoutonAction(String texte, String commande) {
        JButton btn = new JButton(texte);
        btn.setActionCommand(commande);
        btn.addActionListener(this.gestion);
        btn.setBackground(new Color(59, 89, 152));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    // GETTERS 
    
    public JTable getTableBails() { return tableBails; }
    public JTable getTableLoyers() { return tableLoyers; }
    public JTable getTableFactures() { return tableFactures; }
    public JButton getBtnAjouterFacture() { return btnAjouterFacture; }
    public JButton getBtnPayerFacture() { return btnPayerFacture; }
}