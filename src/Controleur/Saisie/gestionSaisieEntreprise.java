package Controleur.Saisie;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import modele.Entreprise;
import modele.dao.EntrepriseDao;
import modele.dao.UtOracleDataSource;
import vue.Saisie.pageSaisieEntreprise;

// Contrôleur pour la page de saisie d'une entreprise
 
public class gestionSaisieEntreprise implements ActionListener {

    private final pageSaisieEntreprise vue;

    public gestionSaisieEntreprise(pageSaisieEntreprise vue) {
        this.vue = vue;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "VALIDER_ENTREPRISE":
                enregistrerEntreprise();
                break;

            case "ANNULER_ENTREPRISE":
                vue.setEntrepriseCree(null);
                vue.dispose();
                break;

            default:
                break;
        }
    }

    // Valide les champs et enregistre l'entreprise
     
    private void enregistrerEntreprise() {

        
        // SIRET
        String siretStr = vue.getTxtSiret().getText().trim().replace(" ", "");
        if (siretStr.isEmpty()) {
            afficherErreur("Le numéro SIRET est obligatoire.");
            vue.getTxtSiret().requestFocus();
            return;
        }
        
        long siret;
        try {
            siret = Long.parseLong(siretStr);
            if (siretStr.length() != 14) {
                afficherErreur("Le SIRET doit contenir exactement 14 chiffres.");
                vue.getTxtSiret().requestFocus();
                return;
            }
        } catch (NumberFormatException ex) {
            afficherErreur("Le SIRET ne doit contenir que des chiffres.");
            vue.getTxtSiret().requestFocus();
            return;
        }

        // Nom
        String nom = vue.getTxtNom().getText().trim();
        if (nom.isEmpty()) {
            afficherErreur("Le nom de l'entreprise est obligatoire.");
            vue.getTxtNom().requestFocus();
            return;
        }

        // Autres champs (optionnels)
        String specialite = vue.getTxtSpecialite().getText().trim();
        if (specialite.isEmpty()) specialite = null;

        String telephone = vue.getTxtTelephone().getText().trim();
        if (telephone.isEmpty()) telephone = null;

        String email = vue.getTxtEmail().getText().trim();
        if (email.isEmpty()) email = null;

        String adresse = vue.getTxtAdresse().getText().trim();
        if (adresse.isEmpty()) adresse = null;

        //créer l'objet
        Entreprise entreprise = new Entreprise(siret, nom, adresse, telephone, email, specialite);

        // enregistre dans la bdd
        EntrepriseDao dao = null;
        try {
            UtOracleDataSource.creerAcces();
            dao = new EntrepriseDao();
            dao.setConnection(UtOracleDataSource.getConnection());

            if (vue.isModeModification()) {
                dao.update(entreprise);
                JOptionPane.showMessageDialog(
                    vue,
                    "Entreprise modifiée avec succès !",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                dao.create(entreprise);
                JOptionPane.showMessageDialog(
                    vue,
                    "Entreprise créée avec succès !\n\n" +
                    "• " + nom + "\n" +
                    "• SIRET : " + entreprise.getSiretFormate(),
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }

            vue.setEntrepriseCree(entreprise);
            vue.dispose();

        } catch (SQLException ex) {
            ex.printStackTrace();
            
            String message = ex.getMessage();
            
            if (message != null && message.contains("ORA-00001")) {
                afficherErreur("Une entreprise avec ce SIRET existe déjà.");
            } else {
                afficherErreur("Erreur lors de l'enregistrement :\n" + message);
            }
        } finally {
            if (dao != null) {
                try {
                    dao.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void afficherErreur(String message) {
        JOptionPane.showMessageDialog(
            vue,
            message,
            "Erreur",
            JOptionPane.ERROR_MESSAGE
        );
    }
}