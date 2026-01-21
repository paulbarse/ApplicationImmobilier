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

/**
 * Classe abstraite commune pour les dialogues de saisie de documents.
 */
public abstract class AbstractSaisieDocumentDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    protected String[] labels;
    protected JTextField[] champs;
    protected String[] resultat;

    protected JButton btnValider;
    protected JButton btnAnnuler;
    protected JPanel contentPane;
    protected JPanel panelForm;

    /**
     * Constructeur commun pour tous les dialogues de saisie de documents
     */
    protected AbstractSaisieDocumentDialog(String titre, String[] labels, String[] valeursInitiales,
            int largeur, int hauteurBase, boolean avecTitre) {
        this.labels = labels;

        setTitle(titre);
        setModal(true);
        setResizable(false);
        setBounds(200, 200, largeur, hauteurBase + labels.length * 30);

        contentPane = new JPanel(new BorderLayout(0, 0));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        //  Titre (optionnel)
        if (avecTitre) {
            JLabel lblTitre = new JLabel(titre);
            lblTitre.setFont(new Font("Verdana", Font.BOLD, 18));
            lblTitre.setHorizontalAlignment(SwingConstants.CENTER);
            lblTitre.setBorder(new EmptyBorder(0, 0, 10, 0));
            contentPane.add(lblTitre, BorderLayout.NORTH);
        }

        // Formulaire 
        panelForm = new JPanel(new GridLayout(labels.length, 2, 8, 8));
        panelForm.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        champs = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            panelForm.add(new JLabel(labels[i] + " :"));
            champs[i] = new JTextField();
            if (valeursInitiales != null && i < valeursInitiales.length && valeursInitiales[i] != null) {
                champs[i].setText(valeursInitiales[i]);
            }
            panelForm.add(champs[i]);
        }

        contentPane.add(panelForm, BorderLayout.CENTER);

        // Boutons 
        JPanel panelBoutons = new JPanel();
        panelBoutons.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnValider = new JButton("Valider");
        btnAnnuler = new JButton("Annuler");

        panelBoutons.add(btnValider);
        panelBoutons.add(btnAnnuler);

        contentPane.add(panelBoutons, BorderLayout.SOUTH);
    }

    /**
     * Constructeur simplifié avec valeurs par défaut
     */
    protected AbstractSaisieDocumentDialog(String titre, String[] labels, String[] valeursInitiales) {
        this(titre, labels, valeursInitiales, 500, 250, true);
    }

    //  GETTERS / SETTERS COMMUNS 

    public JTextField[] getChamps() {
        return champs;
    }

    public String[] getResultat() {
        return resultat;
    }

    public void setResultat(String[] resultat) {
        this.resultat = resultat;
    }

    public JButton getBtnValider() {
        return btnValider;
    }

    public JButton getBtnAnnuler() {
        return btnAnnuler;
    }

    /**
     * Configure les commandes d'action des boutons
     */
    protected void configurerCommandes(String commandeValider, String commandeAnnuler) {
        btnValider.setActionCommand(commandeValider);
        btnAnnuler.setActionCommand(commandeAnnuler);
    }
}
