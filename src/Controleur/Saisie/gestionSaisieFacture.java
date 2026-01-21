package Controleur.Saisie;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.JOptionPane;

import modele.Facture;
import modele.dao.FactureDao;
import vue.Saisie.pageSaisieFacture;

/**
 * Controleur pour la saisie de facture
 */
public class gestionSaisieFacture implements ActionListener {

    private pageSaisieFacture vue;

    public gestionSaisieFacture(pageSaisieFacture vue) {
        this.vue = vue;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "VALIDER_FACTURE":
                creerFacture();
                break;
            case "ANNULER_FACTURE":
                vue.dispose();
                break;
            default:
                break;
        }
    }

    private void creerFacture() {
        FactureDao dao = null;
        try {

            // 1. VALIDATION DES CHAMPS OBLIGATOIRES DE BASE

            
            String idFactureStr = vue.getTxtIdFacture().getText().trim();
            if (idFactureStr.isEmpty()) {
                showError("Veuillez entrer un ID Facture");
                return;
            }
            
            long idFacture;
            try {
                idFacture = Long.parseLong(idFactureStr);
            } catch (NumberFormatException ex) {
                showError("L'ID Facture doit etre un nombre");
                return;
            }
            
            String nature = vue.getTxtNature().getText().trim();
            if (nature.isEmpty()) {
                showError("Veuillez remplir la nature de la facture");
                return;
            }
            
            Date dateEmission = vue.getDateEmission();
            if (dateEmission == null) {
                showError("Veuillez selectionner une date d'emission");
                return;
            }
            
            String montantTTCStr = vue.getTxtMontantTTC().getText().trim();
            if (montantTTCStr.isEmpty()) {
                showError("Veuillez entrer le montant TTC");
                return;
            }
            
            double montantTTC;
            try {
                montantTTC = Double.parseDouble(montantTTCStr.replace(",", "."));
            } catch (NumberFormatException ex) {
                showError("Le montant TTC doit etre un nombre valide");
                return;
            }
            
            Long siretObj = vue.getSelectedSiret();
            if (siretObj == null || siretObj == 0) {
                showError("Veuillez selectionner une entreprise");
                return;
            }
            long siret = siretObj.longValue();
            

            // 2. TRAVAUX ET DEVIS

            
            String travaux = null;
            Date dateDevis = null;
            Double montantDevis = null;
            
            if (vue.isTravaux()) {
                travaux = vue.getTravauxDesc();
                if (travaux == null || travaux.isEmpty()) {
                    showError("REGLE 12 : La description des travaux est obligatoire\n" +
                              "pour une facture de travaux.");
                    return;
                }
                
                String montantDevisStr = vue.getTxtMontantDevis().getText().trim();
                if (montantDevisStr.isEmpty()) {
                    showError("REGLE 12 : Le montant du devis est obligatoire\n" +
                              "pour une facture de travaux.");
                    return;
                }
                
                try {
                    montantDevis = Double.parseDouble(montantDevisStr.replace(",", "."));
                } catch (NumberFormatException ex) {
                    showError("Le montant du devis doit etre un nombre valide");
                    return;
                }
                
                dateDevis = vue.getDateDevis();
            }
            

            // 3. VERIFICATION ID UNIQUE

            
            dao = new FactureDao();
            Facture existante = dao.findById(idFacture);
            if (existante != null) {
                showError("Cet ID Facture existe deja. Choisissez-en un autre.");
                return;
            }
            

            // 4. RECUPERATION DES CHAMPS OPTIONNELS

            
            Boolean recuperableLoc = vue.isRecuperableLoc();
            Boolean deductibleImpot = vue.isDeductibleImpot();
            Long idCharge = vue.getSelectedChargeId();
            Long idBatiment = vue.getIdBatiment();
            long idBail = vue.getIdBail();
            String statut = vue.getStatutPaiement();
            

            // 5. CREATION DE LA FACTURE

            
            Facture f = new Facture(
                idFacture,
                null,
                nature,
                (String) null,
                dateEmission,
                null,
                montantTTC,
                null,
                recuperableLoc,
                deductibleImpot,
                null,
                travaux,
                dateDevis,
                montantDevis,
                siret,
                idCharge,
                idBatiment,
                idBail,
                statut
            );
            
            dao.create(f);
            
            vue.setFactureCree(f);
            
            String message = "Facture #" + idFacture + " creee avec succes !";
            if (travaux != null) {
                message += "\n\nTravaux : " + travaux;
                message += "\nMontant devis : " + String.format("%.2f", montantDevis) + " EUR";
            }
            
            JOptionPane.showMessageDialog(vue,
                message,
                "Succes",
                JOptionPane.INFORMATION_MESSAGE);
            
            vue.dispose();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Erreur lors de la creation : " + ex.getMessage());
        } finally {
            if (dao != null) try { dao.close(); } catch (SQLException e) { /* ignore */ }
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(vue, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
