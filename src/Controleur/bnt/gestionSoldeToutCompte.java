package Controleur.bnt;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

import modele.dao.BailDao;
import vue.bnt.DlgSoldeToutCompte;

/**
 * Controleur pour le dialogue Solde de tout compte.
 */
public class gestionSoldeToutCompte implements ActionListener {

    private DlgSoldeToutCompte vue;
    private long idBail;

    public gestionSoldeToutCompte(DlgSoldeToutCompte vue, long idBail) {
        this.vue = vue;
        this.idBail = idBail;
        initialiserDonnees();
    }

    private void initialiserDonnees() {
        BailDao dao = null;
        try {
            dao = new BailDao();
            double[] infos = dao.getDetailsSolde(this.idBail);
            
            vue.getTxtLoyersRestants().setText(String.valueOf(infos[0]));
            vue.getTxtCautionVersee().setText(String.valueOf(infos[1]));
            vue.getTxtRegulChargesAnnuelle().setText(String.valueOf(infos[2]));
            
            calculerMontantFinal();
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (dao != null) try { dao.close(); } catch (SQLException ex) { /* ignore */ }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
        case "CALCULER":
            calculerMontantFinal();
            break;
        case "VALIDER":
            validerCloture();
            break;
        case "ANNULER":
            vue.fermer();
            break;
        default:
            break;
        }
    }

    private void calculerMontantFinal() {
        try {
            double loyers = parseOrZero(vue.getTxtLoyersRestants().getText());
            double charges = parseOrZero(vue.getTxtCharges().getText());
            double degrad = parseOrZero(vue.getTxtDegradations().getText());
            double caution = parseOrZero(vue.getTxtCautionVersee().getText());
            double tropPercus = parseOrZero(vue.getTxtTropPercus().getText());
            double regulAnnuelle = parseOrZero(vue.getTxtRegulChargesAnnuelle().getText());
            double indemOccupation = parseOrZero(vue.getTxtIndemniteOccupation().getText());

            double aPayer = loyers + charges + degrad + indemOccupation;
            double aDeduire = caution + tropPercus;
            double total = aPayer - aDeduire + regulAnnuelle;

            total = Math.round(total * 100.0) / 100.0;
            
            if (total > 0) {
                vue.getLblMontantFinal().setForeground(Color.RED);
                vue.getLblMontantFinal().setText(String.format("%.2f EUR (Le locataire doit payer)", total));
            } else if (total < 0) {
                vue.getLblMontantFinal().setForeground(new Color(0, 128, 0));
                vue.getLblMontantFinal().setText(String.format("%.2f EUR (A rendre au locataire)", Math.abs(total)));
            } else {
                vue.getLblMontantFinal().setForeground(Color.BLACK);
                vue.getLblMontantFinal().setText("0.00 EUR (Compte solde)");
            }

        } catch (Exception ex) {
            vue.getLblMontantFinal().setText("Erreur de saisie");
        }
    }

    private void validerCloture() {
        String dateFinStr = vue.getTxtDateFin().getText().trim();
        java.sql.Date dateFin;
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            Date parsedDate = sdf.parse(dateFinStr);
            dateFin = new java.sql.Date(parsedDate.getTime());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vue, 
                "Format de date invalide.\nVeuillez entrer une date au format JJ/MM/AAAA",
                "Erreur de saisie", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        calculerMontantFinal();
        
        String texteMontant = vue.getLblMontantFinal().getText();
        String montantStr = texteMontant.split(" ")[0].replace(",", ".");
        double montant = parseOrZero(montantStr);
        
        if (texteMontant.contains("rendre")) {
            montant = -Math.abs(montant);
        }

        String message = "Voulez-vous cloturer ce bail ?\n\n" +
                         "Date de fin : " + dateFinStr + "\n" +
                         "Solde : " + String.format("%.2f", montant) + " EUR\n\n";
        
        if (montant > 0) {
            message += "Le locataire doit vous payer " + String.format("%.2f", montant) + " EUR";
        } else if (montant < 0) {
            message += "Vous devez rembourser " + String.format("%.2f", Math.abs(montant)) + " EUR au locataire";
        } else {
            message += "Le compte est solde";
        }
        
        message += "\n\nLes loyers payes seront archives dans l'historique.";
        
        int reponse = JOptionPane.showConfirmDialog(vue, message, 
            "Confirmation de cloture", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE);
        
        if (reponse != JOptionPane.YES_OPTION) {
            return;
        }

        BailDao bailDao = null;
        try {
            bailDao = new BailDao();
            bailDao.cloturerBail(this.idBail, dateFin, montant);
            
            JOptionPane.showMessageDialog(vue, 
                "Le bail a ete cloture avec succes !\n\n" +
                "Date de fin : " + dateFinStr + "\n" +
                "Le logement est maintenant libere.\n" +
                "Les loyers payes ont ete archives.",
                "Succes",
                JOptionPane.INFORMATION_MESSAGE);
            vue.fermer();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(vue, 
                "Erreur lors de la cloture : " + ex.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            if (bailDao != null) try { bailDao.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    private double parseOrZero(String txt) {
        if (txt == null || txt.trim().isEmpty() || txt.equals("-") || txt.equals("Erreur")) {
            return 0.0;
        }
        try {
            String clean = txt.replaceAll("[^0-9.,-]", "").replace(',', '.');
            if (clean.isEmpty()) return 0.0;
            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
