package vue.refacto;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


public abstract class AbstractSaisieDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    protected JPanel contentPanel;   // panneau racine
    protected JPanel headerPanel;    // header titre/sous-titre
    protected JPanel formPanel;      // à remplir par les sous-classes
    protected JPanel buttonPanel;    // contient btnValider / btnAnnuler

    protected JButton btnValider;
    protected JButton btnAnnuler;

 
    protected AbstractSaisieDialog(String titre, String sousTitre) {
        super();
        setTitle(titre);
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Panneau racine 
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPanel);

        // Header (titre + sous-titre) 
        headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        JLabel lblTitre = new JLabel(titre);
        lblTitre.setFont(new Font("Verdana", Font.BOLD, 20));
        lblTitre.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(lblTitre);

        if (sousTitre != null && !sousTitre.isEmpty()) {
            JLabel lblSousTitre = new JLabel(sousTitre);
            lblSousTitre.setFont(new Font("Verdana", Font.PLAIN, 12));
            lblSousTitre.setAlignmentX(Component.LEFT_ALIGNMENT);
            headerPanel.add(lblSousTitre);
        }

        // Panneau de formulaire 
        formPanel = new JPanel();
        formPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        contentPanel.add(formPanel, BorderLayout.CENTER);

        // Boutons Valider / Annuler 
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnValider = new JButton("Valider");
        btnAnnuler = new JButton("Annuler");
        buttonPanel.add(btnValider);
        buttonPanel.add(btnAnnuler);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Utilitaire pour centrer et ajuster la taille/niveau de pack.
     */
    protected void finalizeLayout() {
        pack();
        setLocationRelativeTo(getParent());
    }

    public JButton getBtnValider() {
        return btnValider;
    }

    public JButton getBtnAnnuler() {
        return btnAnnuler;
    }

    public JPanel getFormPanel() {
        return formPanel;
    }
}
