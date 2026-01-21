package vue.refacto;

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
 * Classe abstraite pour les dialogues de saisie de documents
 */
public abstract class AbstractPageSaisieDocument extends JDialog 
        implements gestionSaisieDocument.DocumentSaisieVue {

    private static final long serialVersionUID = 1L;

    protected JTextField[] champs;
    protected String[] resultat;
    protected JButton btnValider;
    protected JButton btnAnnuler;

    /**
     * Constructeur commun pour tous les dialogues de saisie de document
     */
    public AbstractPageSaisieDocument(String titre, String[] labels, String[] valeursInitiales) {
        setTitle(titre);
        setModal(true);
        setResizable(false);
        
        // Taille adaptative selon le nombre de champs
        int hauteur = 200 + labels.length * 40;
        setBounds(200, 200, 550, Math.min(hauteur, 600));

        JPanel contentPane = new JPanel(new BorderLayout(0, 0));
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPane);

        // Titre en haut
        JLabel lblTitre = new JLabel(titre);
        lblTitre.setFont(new Font("Verdana", Font.BOLD, 16));
        lblTitre.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitre.setBorder(new EmptyBorder(0, 0, 15, 0));
        contentPane.add(lblTitre, BorderLayout.NORTH);

        // Panel du formulaire
        JPanel panelForm = new JPanel(new GridLayout(labels.length, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        champs = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i] + " :");
            panelForm.add(lbl);
            
            champs[i] = new JTextField();
            if (valeursInitiales != null && i < valeursInitiales.length && valeursInitiales[i] != null) {
                champs[i].setText(valeursInitiales[i]);
            }
            panelForm.add(champs[i]);
        }

        contentPane.add(panelForm, BorderLayout.CENTER);

        // Panel des boutons
        JPanel panelBoutons = new JPanel();
        panelBoutons.setBorder(new EmptyBorder(15, 0, 0, 0));

        btnValider = new JButton("Valider");
        btnValider.setActionCommand("VALIDER");
        panelBoutons.add(btnValider);

        btnAnnuler = new JButton("Annuler");
        btnAnnuler.setActionCommand("ANNULER");
        panelBoutons.add(btnAnnuler);

        contentPane.add(panelBoutons, BorderLayout.SOUTH);
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

    public String[] getResultat() {
        return resultat;
    }

    @Override
    public void setResultat(String[] resultat) {
        this.resultat = resultat;
    }
}
