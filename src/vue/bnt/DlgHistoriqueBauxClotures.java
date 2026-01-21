package vue.bnt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Controleur.bnt.gestionHistoriqueBauxClotures;

/**
 * Dialogue affichant l'historique des loyers payes et quittances
 */
public class DlgHistoriqueBauxClotures extends JDialog {

    private static final long serialVersionUID = 1L;

    private JTable tableHistorique;
    private JLabel lblTotalLoyers;
    private JLabel lblTotalProvisions;
    private JLabel lblNbQuittances;
    private JButton btnFermer;
    private JButton btnActualiser;

    private gestionHistoriqueBauxClotures gestion;

    public DlgHistoriqueBauxClotures(Window parent) {
        super(parent, "Historique des baux clotures", ModalityType.APPLICATION_MODAL);

        setPreferredSize(new Dimension(950, 550));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(content);

        //  Titre 
        JPanel panelTitre = new JPanel(new BorderLayout());
        JLabel titre = new JLabel("Historique des loyers payes - Baux clotures");
        titre.setFont(new Font("Verdana", Font.BOLD, 18));
        panelTitre.add(titre, BorderLayout.WEST);
        
        JLabel sousTitre = new JLabel("Recapitulatif des loyers percus et quittances generees");
        sousTitre.setFont(new Font("Verdana", Font.PLAIN, 12));
        panelTitre.add(sousTitre, BorderLayout.SOUTH);
        
        content.add(panelTitre, BorderLayout.NORTH);

        //Centre : Tableau 
        String[] colonnes = {
            "Bien",
            "Locataire",
            "Annee",
            "Loyers payes (EUR)",
            "Provisions (EUR)",
            "Nb mois",
            "Quittances",
            "Date cloture"
        };
        
        DefaultTableModel model = new DefaultTableModel(colonnes, 0) {
            private static final long serialVersionUID = 1L;
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableHistorique = new JTable(model);
        tableHistorique.setRowHeight(25);
        tableHistorique.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        
        // Largeurs des colonnes
        tableHistorique.getColumnModel().getColumn(0).setPreferredWidth(180); // Bien
        tableHistorique.getColumnModel().getColumn(1).setPreferredWidth(150); // Locataire
        tableHistorique.getColumnModel().getColumn(2).setPreferredWidth(60);  // Annee
        tableHistorique.getColumnModel().getColumn(3).setPreferredWidth(110); // Loyers
        tableHistorique.getColumnModel().getColumn(4).setPreferredWidth(100); // Provisions
        tableHistorique.getColumnModel().getColumn(5).setPreferredWidth(70);  // Nb mois
        tableHistorique.getColumnModel().getColumn(6).setPreferredWidth(80);  // Quittances
        tableHistorique.getColumnModel().getColumn(7).setPreferredWidth(100); // Date cloture
        
        JScrollPane scrollPane = new JScrollPane(tableHistorique);
        content.add(scrollPane, BorderLayout.CENTER);

        // Bas : Totaux + Boutons 
        JPanel panelBas = new JPanel(new BorderLayout(10, 10));
        
        // Panel des totaux
        JPanel panelTotaux = new JPanel();
        panelTotaux.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        panelTotaux.add(new JLabel("Total loyers : "));
        lblTotalLoyers = new JLabel("0.00 EUR");
        lblTotalLoyers.setFont(new Font("SansSerif", Font.BOLD, 14));
        panelTotaux.add(lblTotalLoyers);
        
        panelTotaux.add(new JLabel("     |     Total provisions : "));
        lblTotalProvisions = new JLabel("0.00 EUR");
        lblTotalProvisions.setFont(new Font("SansSerif", Font.BOLD, 14));
        panelTotaux.add(lblTotalProvisions);
        
        panelTotaux.add(new JLabel("     |     Quittances generees : "));
        lblNbQuittances = new JLabel("0");
        lblNbQuittances.setFont(new Font("SansSerif", Font.BOLD, 14));
        panelTotaux.add(lblNbQuittances);
        
        panelBas.add(panelTotaux, BorderLayout.NORTH);
        
        // Panel des boutons
        JPanel panelBoutons = new JPanel();
        
        btnActualiser = new JButton("Actualiser");
        btnActualiser.setActionCommand("ACTUALISER");
        panelBoutons.add(btnActualiser);
        
        btnFermer = new JButton("Fermer");
        btnFermer.setActionCommand("FERMER");
        panelBoutons.add(btnFermer);
        
        panelBas.add(panelBoutons, BorderLayout.SOUTH);
        
        content.add(panelBas, BorderLayout.SOUTH);

        //  Controleur 
        gestion = new gestionHistoriqueBauxClotures(this);
        btnActualiser.addActionListener(gestion);
        btnFermer.addActionListener(gestion);

        pack();
        setLocationRelativeTo(parent);
        
        // Charger les donnees au demarrage
        gestion.chargerHistorique();
    }

    //  GETTERS

    public JTable getTableHistorique() {
        return tableHistorique;
    }

    public DefaultTableModel getModeleHistorique() {
        return (DefaultTableModel) tableHistorique.getModel();
    }

    public JLabel getLblTotalLoyers() {
        return lblTotalLoyers;
    }

    public JLabel getLblTotalProvisions() {
        return lblTotalProvisions;
    }

    public JLabel getLblNbQuittances() {
        return lblNbQuittances;
    }

    public void fermer() {
        dispose();
    }
}