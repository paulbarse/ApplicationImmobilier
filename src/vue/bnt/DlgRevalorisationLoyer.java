package vue.bnt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import Controleur.bnt.gestionRevalorisationLoyer;

/**
 * Dialogue de revalorisation du loyer avec IRL
 */
public class DlgRevalorisationLoyer extends JDialog {

    private static final long serialVersionUID = 1L;

    private JLabel lblLogement;
    private JLabel lblLocataire;
    private JLabel lblLoyerActuel;
    private JLabel lblNouveauLoyer;
    private JLabel lblPeutRevaloriser;

    private JTextField txtIrlAncien;
    private JTextField txtIrlNouveau;

    private JButton btnCalculer;
    private JButton btnValider;
    private JButton btnAnnuler;

    private long idBail;           
    private double loyerActuel;
    private boolean revalorisationEffectuee = false;

    private gestionRevalorisationLoyer gestion;

    public DlgRevalorisationLoyer(Window parent,
                                  long idBail,
                                  String nomLogement,
                                  String nomLocataire,
                                  double loyerActuel,
                                  boolean peutRevaloriser) {
        super(parent, "Revalorisation du loyer", ModalityType.APPLICATION_MODAL);
        this.idBail = idBail;
        this.loyerActuel = loyerActuel;

        setPreferredSize(new Dimension(500, 350));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(content);

        //  Titre 
        JLabel titre = new JLabel("Revalorisation du loyer - Bail N°" + idBail);
        titre.setFont(new Font("Verdana", Font.BOLD, 18));
        content.add(titre, BorderLayout.NORTH);

        // Centre : labels + champs dans un GridLayout 
        JPanel centre = new JPanel(new GridLayout(0, 2, 10, 8));
        centre.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Logement
        centre.add(new JLabel("Logement : "));
        lblLogement = new JLabel(nomLogement);
        lblLogement.setFont(new Font("SansSerif", Font.BOLD, 12));
        centre.add(lblLogement);

        // Locataire
        centre.add(new JLabel("Locataire : "));
        lblLocataire = new JLabel(nomLocataire);
        lblLocataire.setFont(new Font("SansSerif", Font.BOLD, 12));
        centre.add(lblLocataire);

        // Peut revaloriser 
        centre.add(new JLabel("Éligible (> 1 an) : "));
        lblPeutRevaloriser = new JLabel(peutRevaloriser ? "OUI" : "NON");
        lblPeutRevaloriser.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblPeutRevaloriser.setForeground(peutRevaloriser ? new Color(0, 128, 0) : Color.RED);
        centre.add(lblPeutRevaloriser);

        // Loyer actuel
        centre.add(new JLabel("Loyer actuel (€) : "));
        lblLoyerActuel = new JLabel(String.format("%.2f", loyerActuel));
        lblLoyerActuel.setFont(new Font("SansSerif", Font.BOLD, 12));
        centre.add(lblLoyerActuel);

        // IRL ancien
        centre.add(new JLabel("IRL ancien * : "));
        txtIrlAncien = new JTextField();
        centre.add(txtIrlAncien);

        // IRL nouveau
        centre.add(new JLabel("IRL nouveau * : "));
        txtIrlNouveau = new JTextField();
        centre.add(txtIrlNouveau);

        // Nouveau loyer calculé
        centre.add(new JLabel("Nouveau loyer max (€) : "));
        lblNouveauLoyer = new JLabel("—");
        lblNouveauLoyer.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblNouveauLoyer.setForeground(new Color(0, 100, 180));
        centre.add(lblNouveauLoyer);

        content.add(centre, BorderLayout.CENTER);

        // Bas : boutons 
        JPanel bas = new JPanel();
        bas.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnCalculer = new JButton("Calculer");
        btnCalculer.setActionCommand("CALCULER");

        btnValider = new JButton("Appliquer la revalorisation");
        btnValider.setActionCommand("VALIDER");
        btnValider.setEnabled(false); // Activé après calcul

        btnAnnuler = new JButton("Annuler");
        btnAnnuler.setActionCommand("ANNULER");

        bas.add(btnCalculer);
        bas.add(btnValider);
        bas.add(btnAnnuler);

        content.add(bas, BorderLayout.SOUTH);

        //  Contrôleur 
        this.gestion = new gestionRevalorisationLoyer(this);
        btnCalculer.addActionListener(gestion);
        btnValider.addActionListener(gestion);
        btnAnnuler.addActionListener(gestion);

        pack();
        setLocationRelativeTo(parent);
    }

    // Constructeur simplifié pour compatibilité avec ancien code
    public DlgRevalorisationLoyer(Window parent,
                                  String nomLogement,
                                  String nomLocataire,
                                  double loyerActuel) {
        this(parent, 0, nomLogement, nomLocataire, loyerActuel, true);
    }

    // Getters pour le contrôleur 

    public long getIdBail() {
        return idBail;
    }

    public JTextField getTxtIrlAncien() {
        return txtIrlAncien;
    }

    public JTextField getTxtIrlNouveau() {
        return txtIrlNouveau;
    }

    public JLabel getLblNouveauLoyer() {
        return lblNouveauLoyer;
    }

    public JButton getBtnValider() {
        return btnValider;
    }

    public double getLoyerActuel() {
        return loyerActuel;
    }

    public boolean isRevalorisationEffectuee() {
        return revalorisationEffectuee;
    }

    public void setRevalorisationEffectuee(boolean revalorisationEffectuee) {
        this.revalorisationEffectuee = revalorisationEffectuee;
    }

    public void fermer() {
        dispose();
    }
}