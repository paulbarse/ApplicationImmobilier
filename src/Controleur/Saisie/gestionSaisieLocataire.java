package Controleur.Saisie;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

import modele.Locataire;
import modele.dao.LocataireDao;
import modele.dao.UtOracleDataSource;
import vue.Saisie.pageSaisieLocataire;

/**
 * Contrôleur pour la page de saisie d'un locataire
 */
public class gestionSaisieLocataire implements ActionListener {

    private final pageSaisieLocataire vue;

    public gestionSaisieLocataire(pageSaisieLocataire vue) {
        this.vue = vue;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {

            case "VALIDER_LOCATAIRE":
            case "VALIDER":
                sauvegarderLocataire();
                break;

            case "ANNULER_LOCATAIRE":
            case "ANNULER":
                vue.setLocataireCree(null);
                vue.dispose();
                break;

            default:
                break;
        }
    }

    /**
     * Valide les champs et sauvegarde le locataire en BDD
     */
    private void sauvegarderLocataire() {
        //  1. VALIDATION DES CHAMPS 
        
        // ID (obligatoire en création)
        String idStr = vue.getTxtId().getText().trim();
        if (idStr.isEmpty() && !vue.isModeModification()) {
            afficherErreur("L'ID du locataire est obligatoire.");
            vue.getTxtId().requestFocus();
            return;
        }
        
        long idLocataire = 0;
        try {
            idLocataire = Long.parseLong(idStr);
        } catch (NumberFormatException ex) {
            afficherErreur("L'ID doit être un nombre.");
            vue.getTxtId().requestFocus();
            return;
        }

        // Nom (obligatoire)
        String nom = vue.getTxtNom().getText().trim();
        if (nom.isEmpty()) {
            afficherErreur("Le nom est obligatoire.");
            vue.getTxtNom().requestFocus();
            return;
        }

        // Prénom (obligatoire)
        String prenom = vue.getTxtPrenom().getText().trim();
        if (prenom.isEmpty()) {
            afficherErreur("Le prénom est obligatoire.");
            vue.getTxtPrenom().requestFocus();
            return;
        }

        // Téléphone (optionnel)
        String tel = vue.getTxtTel().getText().trim();
        if (tel.isEmpty()) {
            tel = null;
        }

        // Email (optionnel)
        String mail = vue.getTxtMail().getText().trim();
        if (mail.isEmpty()) {
            mail = null;
        }

        // Date de naissance (optionnel mais recommandé)
        LocalDate dateNaissance = vue.getDateNaissance();

        // 2. CREATION DE L'OBJET LOCATAIRE 
        Locataire locataire = new Locataire(idLocataire, nom, prenom, tel, mail, dateNaissance);

        // 3. SAUVEGARDE EN BDD
        LocataireDao dao = null;
        try {
            UtOracleDataSource.creerAcces();
            dao = new LocataireDao();
            dao.setConnection(UtOracleDataSource.getConnection());

            if (vue.isModeModification()) {
                // Modification
                dao.update(locataire);
                JOptionPane.showMessageDialog(
                    vue,
                    "Locataire modifié avec succès !",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                // Création
                dao.create(locataire);
                
                String dateStr = "";
                if (dateNaissance != null) {
                    dateStr = "\n• Date de naissance : " + dateNaissance.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
                
                JOptionPane.showMessageDialog(
                    vue,
                    "Locataire créé avec succès !\n\n" +
                    "• ID : " + idLocataire + "\n" +
                    "• Nom : " + nom + " " + prenom +
                    dateStr,
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }

            vue.setLocataireCree(locataire);
            vue.dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            
            String message = ex.getMessage();
            
            // Messages d'erreur Oracle personnalisés
            if (message != null && (message.contains("ORA-00001") || message.contains("unique constraint"))) {
                afficherErreur("Un locataire avec cet ID existe déjà.");
            } else {
                afficherErreur("Erreur lors de la sauvegarde :\n" + message);
            }
            
        } finally {
            if (dao != null) {
                try {
                    dao.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Affiche un message d'erreur
     */
    private void afficherErreur(String message) {
        JOptionPane.showMessageDialog(
            vue,
            message,
            "Erreur",
            JOptionPane.ERROR_MESSAGE
        );
    }
}