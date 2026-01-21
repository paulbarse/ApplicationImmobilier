package vue.Document;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import Controleur.Document.gestionSaisieDocument;

/**
 * Vue de saisie pour les documents de bâtiment
 */
public class pageSaisieDocumentBatiment extends JDialog implements gestionSaisieDocument.DocumentSaisieVue {

    private static final long serialVersionUID = 1L;

    private JTextField[] champs;
    private String[] resultat;
    private JButton btnValider;
    private JButton btnAnnuler;

    public pageSaisieDocumentBatiment(String titre, String[] labels, String[] valeursInitiales) {

        setTitle(titre);
        setModal(true);
        setResizable(false);
        setBounds(200, 200, 500, 250 + labels.length * 30);

        JPanel contentPane = new JPanel(new BorderLayout(0, 0));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        JLabel lblTitre = new JLabel(titre);
        lblTitre.setFont(new Font("Verdana", Font.BOLD, 18));
        lblTitre.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitre.setBorder(new EmptyBorder(0, 0, 10, 0));
        contentPane.add(lblTitre, BorderLayout.NORTH);

        JPanel panelForm = new JPanel(new GridLayout(labels.length, 2, 8, 8));
        panelForm.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        champs = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            panelForm.add(new JLabel(labels[i] + " :"));
            champs[i] = new JTextField();
            if (valeursInitiales != null && i < valeursInitiales.length) {
                champs[i].setText(valeursInitiales[i]);
            }
            panelForm.add(champs[i]);
        }

        contentPane.add(panelForm, BorderLayout.CENTER);

        JPanel panelBoutons = new JPanel();
        panelBoutons.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnValider = new JButton("Valider");
        btnValider.setActionCommand("VALIDER_DOCUMENT_BATIMENT");
        panelBoutons.add(btnValider);

        btnAnnuler = new JButton("Annuler");
        btnAnnuler.setActionCommand("ANNULER_DOCUMENT_BATIMENT");
        panelBoutons.add(btnAnnuler);

        contentPane.add(panelBoutons, BorderLayout.SOUTH);
        
        // Le contrôleur est créé 
    }

    public String[] getResultat() {
        return resultat;
    }

    @Override
    public void setResultat(String[] resultat) {
        this.resultat = resultat;
    }

    @Override
    public JTextField[] getChamps() {
        return champs;
    }

    @Override
    public JButton getBtnValider() {
        return btnValider;
    }

    @Override
    public JButton getBtnAnnuler() {
        return btnAnnuler;
    }
}
