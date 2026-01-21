package vue.Principale;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Controleur.Principale.gestionGestionLoyers;

/**
 * Page de gestion des loyers d'un bail
 */
public class pageGestionLoyers extends JDialog {

    private static final long serialVersionUID = 1L;
    
    private long idBail;
    private String nomLocataire;
    private int annee;
    
    private JTable tableLoyers;
    private JButton btnMarquerPaye;
    private JButton btnMarquerRetard;
    private JButton btnMarquerEnAttente;
    private JButton btnGenererLoyers;
    private JButton btnFermer;
    private JComboBox<Integer> comboAnnee;
    private JLabel lblInfoBail;
    
    private gestionGestionLoyers gestion;

    public pageGestionLoyers(long idBail, String nomLocataire, int annee) {
        this.idBail = idBail;
        this.nomLocataire = nomLocataire;
        this.annee = annee;
        
        gestion = new gestionGestionLoyers(this);
        initUI();
        
        // Charger les loyers
        gestion.chargerLoyers();
    }

    private void initUI() {
        setTitle("Gestion des loyers - " + nomLocataire);
        setModal(true);
        setBounds(100, 100, 900, 600);
        setLocationRelativeTo(null);
        
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        
        //HEADER 
        JPanel panelHeader = new JPanel(new BorderLayout());
        
        lblInfoBail = new JLabel("Bail #" + idBail + " - " + nomLocataire);
        lblInfoBail.setFont(new Font("Verdana", Font.BOLD, 16));
        panelHeader.add(lblInfoBail, BorderLayout.WEST);
        
        JPanel panelAnnee = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelAnnee.add(new JLabel("Année : "));
        
        int anneeActuelle = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        Integer[] annees = new Integer[5];
        for (int i = 0; i < 5; i++) {
            annees[i] = anneeActuelle - i + 1;
        }
        comboAnnee = new JComboBox<>(annees);
        comboAnnee.setSelectedItem(annee);
        comboAnnee.addActionListener(e -> {
            this.annee = (Integer) comboAnnee.getSelectedItem();
            gestion.chargerLoyers();
        });
        panelAnnee.add(comboAnnee);
        
        btnGenererLoyers = new JButton("Générer loyers manquants");
        btnGenererLoyers.setActionCommand("GENERER_LOYERS");
        btnGenererLoyers.addActionListener(gestion);
        panelAnnee.add(btnGenererLoyers);
        
        panelHeader.add(panelAnnee, BorderLayout.EAST);
        contentPane.add(panelHeader, BorderLayout.NORTH);
        
        //  TABLE 
        String[] colonnes = {"ID", "Mois", "Loyer (€)", "Provision (€)", "Total (€)", "Statut", "Date paiement"};
        DefaultTableModel model = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableLoyers = new JTable(model);
        tableLoyers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableLoyers.setRowHeight(25);
        tableLoyers.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableLoyers.getColumnModel().getColumn(1).setPreferredWidth(80);
        tableLoyers.getColumnModel().getColumn(5).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(tableLoyers);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        
        //  BOUTONS D'ACTION 
        JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        btnMarquerPaye = new JButton("✓ Marquer PAYÉ");
        btnMarquerPaye.setBackground(new Color(40, 167, 69));
        btnMarquerPaye.setForeground(Color.WHITE);
        btnMarquerPaye.setActionCommand("MARQUER_PAYE");
        btnMarquerPaye.addActionListener(gestion);
        panelActions.add(btnMarquerPaye);
        
        btnMarquerRetard = new JButton("⚠ Marquer RETARD");
        btnMarquerRetard.setBackground(new Color(220, 53, 69));
        btnMarquerRetard.setForeground(Color.WHITE);
        btnMarquerRetard.setActionCommand("MARQUER_RETARD");
        btnMarquerRetard.addActionListener(gestion);
        panelActions.add(btnMarquerRetard);
        
        btnMarquerEnAttente = new JButton("↺ Remettre EN ATTENTE");
        btnMarquerEnAttente.setBackground(new Color(108, 117, 125));
        btnMarquerEnAttente.setForeground(Color.WHITE);
        btnMarquerEnAttente.setActionCommand("MARQUER_EN_ATTENTE");
        btnMarquerEnAttente.addActionListener(gestion);
        panelActions.add(btnMarquerEnAttente);
        
        contentPane.add(panelActions, BorderLayout.SOUTH);
        
        // LÉGENDE 
        JPanel panelLegende = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelLegende.add(new JLabel("Légende : "));
        
        JLabel lblPaye = new JLabel("● PAYE");
        lblPaye.setForeground(new Color(40, 167, 69));
        panelLegende.add(lblPaye);
        
        JLabel lblRetard = new JLabel("   ● RETARD");
        lblRetard.setForeground(new Color(220, 53, 69));
        panelLegende.add(lblRetard);
        
        JLabel lblAttente = new JLabel("   ● EN_ATTENTE");
        lblAttente.setForeground(new Color(108, 117, 125));
        panelLegende.add(lblAttente);
        
        JPanel panelSud = new JPanel(new BorderLayout());
        panelSud.add(panelLegende, BorderLayout.NORTH);
        panelSud.add(panelActions, BorderLayout.SOUTH);
        contentPane.add(panelSud, BorderLayout.SOUTH);
    }
    
    //  GETTERS
    public long getIdBail() { return idBail; }
    public int getAnnee() { return annee; }
    public JTable getTableLoyers() { return tableLoyers; }
}