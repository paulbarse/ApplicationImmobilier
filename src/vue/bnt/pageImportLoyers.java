package vue.bnt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Controleur.bnt.gestionImportLoyers;

public class pageImportLoyers extends JDialog {

    private static final long serialVersionUID = 1L;

    private JTextField txtCheminFichier;
    private JTable tablePreview;
    private File fichierSelectionne;

    private JButton btnParcourir;
    private JButton btnImporter;
    private JButton btnFermer;

    private gestionImportLoyers gestion;

    public pageImportLoyers(Window parent) {
        super(parent, "Import des loyers (CSV)", ModalityType.APPLICATION_MODAL);

        setPreferredSize(new Dimension(800, 450));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(content);

        // Titre 
        JLabel titre = new JLabel("Import des loyers à partir d'un fichier CSV");
        titre.setFont(new Font("Verdana", Font.BOLD, 18));
        content.add(titre, BorderLayout.NORTH);

        // Bandeau haut : choix du fichier
        JPanel panelHaut = new JPanel(new BorderLayout(5, 5));

        JLabel lblFichier = new JLabel("Fichier CSV : ");
        panelHaut.add(lblFichier, BorderLayout.WEST);

        txtCheminFichier = new JTextField();
        txtCheminFichier.setEditable(false);
        panelHaut.add(txtCheminFichier, BorderLayout.CENTER);

        btnParcourir = new JButton("Parcourir...");
        btnParcourir.setActionCommand("CHOISIR_FICHIER");
        panelHaut.add(btnParcourir, BorderLayout.EAST);

        content.add(panelHaut, BorderLayout.PAGE_START);

        // Table de prévisualisation
        String[] colonnes = {
                "id logement", "id locataire", "Mois/Année", "Loyer (€)", "Provision charges (€)"
        };
        DefaultTableModel model = new DefaultTableModel(colonnes, 0);
        tablePreview = new JTable(model);
        JScrollPane scroll = new JScrollPane(tablePreview);
        scroll.setBorder(BorderFactory.createTitledBorder("Prévisualisation du fichier CSV"));

        content.add(scroll, BorderLayout.CENTER);

        //  Bas : boutons 
        JPanel panelBas = new JPanel();

        btnImporter = new JButton("Importer");
        btnImporter.setActionCommand("IMPORTER");

        btnFermer = new JButton("Fermer");
        btnFermer.setActionCommand("FERMER");

        panelBas.add(btnImporter);
        panelBas.add(btnFermer);

        content.add(panelBas, BorderLayout.SOUTH);

        // Contrôleur 
        gestion = new gestionImportLoyers(this);
        btnParcourir.addActionListener(gestion);
        btnImporter.addActionListener(gestion);
        btnFermer.addActionListener(gestion);

        pack();
        setLocationRelativeTo(parent);
    }

    // Getters / setters pour le contrôleur 

    public JTextField getTxtCheminFichier() {
        return txtCheminFichier;
    }

    public JTable getTablePreview() {
        return tablePreview;
    }

    public File getFichierSelectionne() {
        return fichierSelectionne;
    }

    public void setFichierSelectionne(File fichierSelectionne) {
        this.fichierSelectionne = fichierSelectionne;
    }

    public void fermer() {
        dispose();
    }
}
