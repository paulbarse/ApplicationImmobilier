package vue.bnt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import Controleur.bnt.gestionSoldeToutCompte;

/**
 * Dialogue de solde de tout compte pour la cloture d'un bail.
 */
public class DlgSoldeToutCompte extends JDialog {

    private static final long serialVersionUID = 1L;

    private JLabel lblLogement;
    private JLabel lblLocataire;

    // Champ date de fin
    private JTextField txtDateFin;
    
    private JTextField txtLoyersRestants;
    private JTextField txtCharges;
    private JTextField txtDegradations;
    private JTextField txtCautionVersee;
    private JTextField txtTropPercus;
    private JTextField txtRegulChargesAnnuelle;
    private JTextField txtIndemniteOccupation;

    private JLabel lblMontantFinal;

    private JButton btnCalculer;
    private JButton btnValider;
    private JButton btnAnnuler;

    private gestionSoldeToutCompte gestion;

    public DlgSoldeToutCompte(Window parent,
                              String logement,
                              String locataire,
                              double cautionVersee,
                              long idBail) {
        super(parent, "Solde de tout compte - Cloture du bail", ModalityType.APPLICATION_MODAL);

        setPreferredSize(new Dimension(520, 450));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(content);

        // Titre 
        JLabel titre = new JLabel("Solde de tout compte");
        titre.setFont(new Font("Verdana", Font.BOLD, 18));
        content.add(titre, BorderLayout.NORTH);

        // Centre : labels + champs
        JPanel centre = new JPanel(new GridLayout(0, 2, 10, 8));

        // Logement
        centre.add(new JLabel("Logement : "));
        lblLogement = new JLabel(logement);
        lblLogement.setFont(new Font("SansSerif", Font.BOLD, 12));
        centre.add(lblLogement);

        // Locataire
        centre.add(new JLabel("Locataire : "));
        lblLocataire = new JLabel(locataire);
        lblLocataire.setFont(new Font("SansSerif", Font.BOLD, 12));
        centre.add(lblLocataire);

        // Date de fin du bail
        centre.add(new JLabel("Date de fin du bail : "));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        txtDateFin = new JTextField(sdf.format(new Date()));
        centre.add(txtDateFin);

        // Separateur visuel
        centre.add(new JLabel(""));
        centre.add(new JLabel("--- Calcul du solde ---"));

        // Loyers restants
        centre.add(new JLabel("Loyers restants (EUR) : "));
        txtLoyersRestants = new JTextField("0");
        centre.add(txtLoyersRestants);

        // Charges
        centre.add(new JLabel("Charges supplementaires (EUR) : "));
        txtCharges = new JTextField("0");
        centre.add(txtCharges);

        // Degradations
        centre.add(new JLabel("Degradations (EUR) : "));
        txtDegradations = new JTextField("0");
        centre.add(txtDegradations);

        // Caution versee
        centre.add(new JLabel("Caution versee (EUR) : "));
        txtCautionVersee = new JTextField(String.valueOf(cautionVersee));
        centre.add(txtCautionVersee);

        // Trop-percus
        centre.add(new JLabel("Trop-percus (EUR) : "));
        txtTropPercus = new JTextField("0");
        centre.add(txtTropPercus);

        // Regularisation charges annuelle
        centre.add(new JLabel("Regul. charges annuelle (EUR) : "));
        txtRegulChargesAnnuelle = new JTextField("0");
        centre.add(txtRegulChargesAnnuelle);

        // Indemnite d'occupation
        centre.add(new JLabel("Indemnite d'occupation (EUR) : "));
        txtIndemniteOccupation = new JTextField("0");
        centre.add(txtIndemniteOccupation);

        // Montant final
        centre.add(new JLabel("MONTANT FINAL : "));
        lblMontantFinal = new JLabel("-");
        lblMontantFinal.setFont(new Font("SansSerif", Font.BOLD, 14));
        centre.add(lblMontantFinal);

        content.add(centre, BorderLayout.CENTER);

        // Bas : boutons 
        JPanel bas = new JPanel();

        btnCalculer = new JButton("Calculer");
        btnCalculer.setActionCommand("CALCULER");

        btnValider = new JButton("Valider et Cloturer");
        btnValider.setActionCommand("VALIDER");

        btnAnnuler = new JButton("Annuler");
        btnAnnuler.setActionCommand("ANNULER");

        bas.add(btnCalculer);
        bas.add(btnValider);
        bas.add(btnAnnuler);

        content.add(bas, BorderLayout.SOUTH);

        // Controleur 
        gestion = new gestionSoldeToutCompte(this, idBail);
        btnCalculer.addActionListener(gestion);
        btnValider.addActionListener(gestion);
        btnAnnuler.addActionListener(gestion);

        pack();
        setLocationRelativeTo(parent);
    }

    //  GETTERS

    public JTextField getTxtDateFin() {
        return txtDateFin;
    }

    public JTextField getTxtLoyersRestants() {
        return txtLoyersRestants;
    }

    public JTextField getTxtCharges() {
        return txtCharges;
    }

    public JTextField getTxtDegradations() {
        return txtDegradations;
    }

    public JTextField getTxtCautionVersee() {
        return txtCautionVersee;
    }

    public JTextField getTxtTropPercus() {
        return txtTropPercus;
    }

    public JTextField getTxtRegulChargesAnnuelle() {
        return txtRegulChargesAnnuelle;
    }

    public JTextField getTxtIndemniteOccupation() {
        return txtIndemniteOccupation;
    }

    public JLabel getLblMontantFinal() {
        return lblMontantFinal;
    }

    public void fermer() {
        dispose();
    }
}