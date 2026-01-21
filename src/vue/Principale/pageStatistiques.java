package vue.Principale;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Controleur.Principale.gestionStatistiques;

/**
 * Page Statistiques / Extractions
 */
public class pageStatistiques extends JDialog {

    private static final long serialVersionUID = 1L;

    private JTextField txtAnnee;
    
    // Tableaux principaux
    private JTable tableBien;
    private JTable tableBatiment;
    
    // Tableaux travaux par entreprise
    private JTable tableTravauxBien;
    private JTable tableTravauxBatiment;

    private JButton btnGenerer;
    private JButton btnFermer;

    private gestionStatistiques gestion;

    public pageStatistiques(Window parent) {
        super(parent, "Statistiques / Extractions", ModalityType.APPLICATION_MODAL);

        setPreferredSize(new Dimension(900, 600));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(content);

        //  Bandeau haut 
        JPanel panneauHaut = new JPanel();
        panneauHaut.add(new JLabel("Annee : "));

        int anneeCourante = Calendar.getInstance().get(Calendar.YEAR);
        txtAnnee = new JTextField(String.valueOf(anneeCourante), 6);
        panneauHaut.add(txtAnnee);

        btnGenerer = new JButton("Generer");
        btnGenerer.setActionCommand("GENERER");
        panneauHaut.add(btnGenerer);

        btnFermer = new JButton("Fermer");
        btnFermer.setActionCommand("FERMER");
        panneauHaut.add(btnFermer);

        content.add(panneauHaut, BorderLayout.PAGE_START);

        //  Onglets 
        JTabbedPane onglets = new JTabbedPane();
        onglets.addTab("Par bien louable", creerPanneauBienLouable());
        onglets.addTab("Par batiment", creerPanneauBatiment());
        content.add(onglets, BorderLayout.CENTER);

        //  Controleur 
        gestion = new gestionStatistiques(this);
        btnGenerer.addActionListener(gestion);
        btnFermer.addActionListener(gestion);

        pack();
        setLocationRelativeTo(parent);
    }
    
    private JPanel creerPanneauBienLouable() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Tableau principal
        String[] colonnesBien = {
            "Bien louable",
            "Total loyers (EUR)",
            "Total provisions (EUR)"
        };
        tableBien = new JTable(new DefaultTableModel(colonnesBien, 0));
        JScrollPane scrollBien = new JScrollPane(tableBien);
        scrollBien.setBorder(BorderFactory.createTitledBorder("Recettes par bien"));
        scrollBien.setPreferredSize(new Dimension(850, 200));
        
        // Tableau travaux
        String[] colonnesTravaux = {
            "Bien",
            "Entreprise",
            "SIRET",
            "Total travaux (EUR)"
        };
        tableTravauxBien = new JTable(new DefaultTableModel(colonnesTravaux, 0));
        JScrollPane scrollTravaux = new JScrollPane(tableTravauxBien);
        scrollTravaux.setBorder(BorderFactory.createTitledBorder("Travaux par entreprise :"));
        scrollTravaux.setPreferredSize(new Dimension(850, 200));
        
        panel.add(scrollBien);
        panel.add(Box.createVerticalStrut(10));
        panel.add(scrollTravaux);
        
        return panel;
    }
    
    private JPanel creerPanneauBatiment() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Tableau principal
        String[] colonnesBat = {
            "Batiment",
            "Total assurances (EUR)",
            "Taxes foncieres (EUR)",
            "TEOM (EUR)",
            "Reste"
        };
        tableBatiment = new JTable(new DefaultTableModel(colonnesBat, 0));
        JScrollPane scrollBat = new JScrollPane(tableBatiment);
        scrollBat.setBorder(BorderFactory.createTitledBorder("Charges par batiment"));
        scrollBat.setPreferredSize(new Dimension(850, 200));
        
        // Tableau travaux
        String[] colonnesTravaux = {
            "Batiment",
            "Entreprise",
            "SIRET",
            "Total travaux (EUR)"
        };
        tableTravauxBatiment = new JTable(new DefaultTableModel(colonnesTravaux, 0));
        JScrollPane scrollTravaux = new JScrollPane(tableTravauxBatiment);
        scrollTravaux.setBorder(BorderFactory.createTitledBorder("Travaux communs par entreprise :"));
        scrollTravaux.setPreferredSize(new Dimension(850, 200));
        
        panel.add(scrollBat);
        panel.add(Box.createVerticalStrut(10));
        panel.add(scrollTravaux);
        
        return panel;
    }

    //  Getters 
    public JTextField getTxtAnnee() { return txtAnnee; }
    public JTable getTableBien() { return tableBien; }
    public JTable getTableBatiment() { return tableBatiment; }
    public JTable getTableTravauxBien() { return tableTravauxBien; }
    public JTable getTableTravauxBatiment() { return tableTravauxBatiment; }
    public void fermer() { dispose(); }
}