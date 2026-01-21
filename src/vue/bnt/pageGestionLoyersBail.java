package vue.bnt;

import java.awt.*;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import Controleur.bnt.gestionGestionLoyersBail;

/**
 * Fenetre de gestion des loyers pour un bail specifique
 */
public class pageGestionLoyersBail extends JDialog {

    private static final long serialVersionUID = 1L;

    // Contexte
    private long idBail;
    private String nomLocataire;
    private double loyerMensuel;
    private double provisionMensuelle;

    // Composants
    private JTable tableLoyers;
    private DefaultTableModel modelLoyers;
    private JComboBox<Integer> comboAnnee;
    private JButton btnGenerer;
    private JButton btnMarquerPaye;
    private JButton btnMarquerRetard;
    private JButton btnRemettreAttente;
    private JLabel lblInfo;

    // Controleur
    private gestionGestionLoyersBail gestion;

    /**
     * Constructeur
     */
    public pageGestionLoyersBail(Window parent, long idBail, String nomLocataire, 
                                  double loyerMensuel, double provisionMensuelle) {
        super(parent, "Gestion des loyers - " + nomLocataire, ModalityType.APPLICATION_MODAL);
        
        this.idBail = idBail;
        this.nomLocataire = nomLocataire;
        this.loyerMensuel = loyerMensuel;
        this.provisionMensuelle = provisionMensuelle;
        
        initUI();
        
        // Initialiser le controleur apres l'UI
        this.gestion = new gestionGestionLoyersBail(this);
        
        // Charger les donnees
        this.gestion.chargerLoyers();
    }

    private void initUI() {
        setSize(900, 550);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPane);

        // HEADER 
        JPanel panelHeader = new JPanel(new BorderLayout());
        
        // Titre
        JLabel lblTitre = new JLabel("Bail #" + idBail + " - " + nomLocataire);
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 18));
        panelHeader.add(lblTitre, BorderLayout.WEST);

        JPanel panelAnnee = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        JLabel lblAnnee = new JLabel("Annee :");
        lblAnnee.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        int anneeActuelle = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] annees = new Integer[5];
        for (int i = 0; i < 5; i++) {
            annees[i] = anneeActuelle - i + 1; // Annee prochaine incluse
        }
        comboAnnee = new JComboBox<>(annees);
        comboAnnee.setSelectedItem(anneeActuelle);
        comboAnnee.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        btnGenerer = new JButton("Generer loyers manquants");
        btnGenerer.setActionCommand("GENERER_LOYERS");
        
        panelAnnee.add(lblAnnee);
        panelAnnee.add(comboAnnee);
        panelAnnee.add(btnGenerer);
        
        panelHeader.add(panelAnnee, BorderLayout.EAST);
        contentPane.add(panelHeader, BorderLayout.NORTH);

        // TABLE DES LOYERS 
        String[] colonnes = {"ID", "Mois", "Loyer (€)", "Provision (€)", "Total (€)", "Statut", "Date paiement"};
        modelLoyers = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableLoyers = new JTable(modelLoyers);
        tableLoyers.setRowHeight(28);
        tableLoyers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableLoyers.getTableHeader().setReorderingAllowed(false);
        
        // Renderer pour colorer les statuts
        tableLoyers.getColumnModel().getColumn(5).setCellRenderer(new StatutRenderer());
        
        // Largeurs des colonnes
        tableLoyers.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        tableLoyers.getColumnModel().getColumn(1).setPreferredWidth(80);  // Mois
        tableLoyers.getColumnModel().getColumn(2).setPreferredWidth(90);  // Loyer
        tableLoyers.getColumnModel().getColumn(3).setPreferredWidth(90);  // Provision
        tableLoyers.getColumnModel().getColumn(4).setPreferredWidth(90);  // Total
        tableLoyers.getColumnModel().getColumn(5).setPreferredWidth(100); // Statut
        tableLoyers.getColumnModel().getColumn(6).setPreferredWidth(110); // Date
        
        JScrollPane scrollPane = new JScrollPane(tableLoyers);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // PANNEAU BAS 
        JPanel panelBas = new JPanel(new BorderLayout());
        
        // Legende
        JPanel panelLegende = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelLegende.add(new JLabel("Legende :"));
        
        JLabel lblPaye = new JLabel("● PAYE");
        lblPaye.setForeground(new Color(34, 139, 34));
        panelLegende.add(lblPaye);
        
        JLabel lblRetard = new JLabel("● RETARD");
        lblRetard.setForeground(new Color(220, 53, 69));
        panelLegende.add(lblRetard);
        
        JLabel lblAttente = new JLabel("● EN_ATTENTE");
        lblAttente.setForeground(new Color(128, 128, 128));
        panelLegende.add(lblAttente);
        
        panelBas.add(panelLegende, BorderLayout.NORTH);
        
        // Boutons d'action
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        btnMarquerPaye = new JButton("✓ Marquer PAYE");
        btnMarquerPaye.setBackground(new Color(40, 167, 69));
        btnMarquerPaye.setForeground(Color.WHITE);
        btnMarquerPaye.setFocusPainted(false);
        btnMarquerPaye.setActionCommand("MARQUER_PAYE");
        
        btnMarquerRetard = new JButton("⚠ Marquer RETARD");
        btnMarquerRetard.setBackground(new Color(220, 53, 69));
        btnMarquerRetard.setForeground(Color.WHITE);
        btnMarquerRetard.setFocusPainted(false);
        btnMarquerRetard.setActionCommand("MARQUER_RETARD");
        
        btnRemettreAttente = new JButton("↺ Remettre EN_ATTENTE");
        btnRemettreAttente.setBackground(new Color(108, 117, 125));
        btnRemettreAttente.setForeground(Color.WHITE);
        btnRemettreAttente.setFocusPainted(false);
        btnRemettreAttente.setActionCommand("REMETTRE_ATTENTE");
        
        panelBoutons.add(btnMarquerPaye);
        panelBoutons.add(btnMarquerRetard);
        panelBoutons.add(btnRemettreAttente);
        
        panelBas.add(panelBoutons, BorderLayout.CENTER);
        
        // Info
        lblInfo = new JLabel(" ");
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        lblInfo.setFont(new Font("SansSerif", Font.ITALIC, 11));
        panelBas.add(lblInfo, BorderLayout.SOUTH);
        
        contentPane.add(panelBas, BorderLayout.SOUTH);
    }

    /**
     * Ajoute les listeners au controleur
     */
    public void setGestion(gestionGestionLoyersBail gestion) {
        this.gestion = gestion;
        
        btnGenerer.addActionListener(gestion);
        btnMarquerPaye.addActionListener(gestion);
        btnMarquerRetard.addActionListener(gestion);
        btnRemettreAttente.addActionListener(gestion);
        comboAnnee.addActionListener(e -> gestion.chargerLoyers());
        tableLoyers.getSelectionModel().addListSelectionListener(gestion);
    }

    // GETTERS 
    
    public long getIdBail() {
        return idBail;
    }

    public String getNomLocataire() {
        return nomLocataire;
    }

    public double getLoyerMensuel() {
        return loyerMensuel;
    }

    public double getProvisionMensuelle() {
        return provisionMensuelle;
    }

    public JTable getTableLoyers() {
        return tableLoyers;
    }

    public DefaultTableModel getModelLoyers() {
        return modelLoyers;
    }

    public int getAnneeSelectionnee() {
        return (Integer) comboAnnee.getSelectedItem();
    }

    public int getLigneSelectionnee() {
        return tableLoyers.getSelectedRow();
    }

    public void setInfo(String message) {
        lblInfo.setText(message);
    }

    /**
     * Renderer personnalise pour colorer les statuts
     */
    private class StatutRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            String statut = value != null ? value.toString().toUpperCase() : "";
            
            if (!isSelected) {
                if (statut.contains("PAYE")) {
                    c.setForeground(new Color(34, 139, 34));
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if (statut.contains("RETARD")) {
                    c.setForeground(new Color(220, 53, 69));
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(new Color(128, 128, 128));
                    c.setFont(c.getFont().deriveFont(Font.PLAIN));
                }
            }
            
            setHorizontalAlignment(SwingConstants.CENTER);
            return c;
        }
    }
}