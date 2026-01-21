package Controleur.bnt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import modele.dao.BailDao;
import modele.dao.UtOracleDataSource;
import vue.bnt.DlgRevalorisationLoyer;

/**
 * Contrôleur pour la revalorisation du loyer
 */
public class gestionRevalorisationLoyer implements ActionListener {

    private final DlgRevalorisationLoyer vue;
    private double nouveauLoyerCalcule = 0;
    private double augmentationMax = 0;

    public gestionRevalorisationLoyer(DlgRevalorisationLoyer vue) {
        this.vue = vue;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "CALCULER":
                calculerNouveauLoyer();
                break;
            case "VALIDER":
                appliquerRevalorisation();
                break;
            case "ANNULER":
                vue.fermer();
                break;
        }
    }

    /**
     * Calcule le nouveau loyer selon la formule IRL
     * Nouveau loyer = Loyer actuel × (Nouvel IRL / Ancien IRL)
     */
    private void calculerNouveauLoyer() {
        try {
            String irlAncienStr = vue.getTxtIrlAncien().getText().trim().replace(',', '.');
            String irlNouveauStr = vue.getTxtIrlNouveau().getText().trim().replace(',', '.');

            if (irlAncienStr.isEmpty() || irlNouveauStr.isEmpty()) {
                vue.getLblNouveauLoyer().setText("Remplir les IRL");
                vue.getBtnValider().setEnabled(false);
                return;
            }

            double irlAncien = Double.parseDouble(irlAncienStr);
            double irlNouveau = Double.parseDouble(irlNouveauStr);

            if (irlAncien <= 0) {
                vue.getLblNouveauLoyer().setText("IRL ancien invalide");
                vue.getBtnValider().setEnabled(false);
                return;
            }

            // Vérifier que le nouvel IRL est supérieur ou égal à l'ancien
            if (irlNouveau < irlAncien) {
                vue.getLblNouveauLoyer().setText("Nouvel IRL < Ancien IRL");
                vue.getBtnValider().setEnabled(false);
                JOptionPane.showMessageDialog(vue,
                        "Le nouvel IRL ne peut pas être inférieur à l'ancien IRL.",
                        "Erreur",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            double loyerActuel = vue.getLoyerActuel();
            
            // Formule de revalorisation : Loyer × (Nouvel IRL / Ancien IRL)
            nouveauLoyerCalcule = loyerActuel * (irlNouveau / irlAncien);
            nouveauLoyerCalcule = Math.round(nouveauLoyerCalcule * 100.0) / 100.0;

            // Calculer le % d'augmentation maximum autorisé
            augmentationMax = ((irlNouveau - irlAncien) / irlAncien) * 100;

            // Afficher le résultat avec le % d'augmentation
            vue.getLblNouveauLoyer().setText(String.format("%.2f € (max +%.2f%%)", 
                    nouveauLoyerCalcule, augmentationMax));
            
            // Activer le bouton valider
            vue.getBtnValider().setEnabled(true);

        } catch (NumberFormatException ex) {
            vue.getLblNouveauLoyer().setText("Erreur format IRL");
            vue.getBtnValider().setEnabled(false);
        }
    }

    /**
     * Applique la revalorisation en BDD via la procédure stockée
     */
    private void appliquerRevalorisation() {
        if (nouveauLoyerCalcule <= 0) {
            JOptionPane.showMessageDialog(vue,
                    "Veuillez d'abord calculer le nouveau loyer.",
                    "Erreur",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        long idBail = vue.getIdBail();
        if (idBail <= 0) {
            JOptionPane.showMessageDialog(vue,
                    "ID du bail invalide.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Récupérer les IRL
        double irlAncien, irlNouveau;
        try {
            irlAncien = Double.parseDouble(vue.getTxtIrlAncien().getText().trim().replace(',', '.'));
            irlNouveau = Double.parseDouble(vue.getTxtIrlNouveau().getText().trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vue,
                    "Format IRL invalide.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirmation
        int confirmation = JOptionPane.showConfirmDialog(vue,
                "Voulez-vous appliquer la revalorisation ?\n\n" +
                "• Ancien loyer : " + String.format("%.2f €", vue.getLoyerActuel()) + "\n" +
                "• Nouveau loyer : " + String.format("%.2f €", nouveauLoyerCalcule) + "\n" +
                "• IRL : " + irlAncien + " → " + irlNouveau + " (+" + String.format("%.2f", augmentationMax) + "%)\n\n" +
                "Cette action mettra à jour le bail et tous les loyers non payés.",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        // Appel de la procédure SQL
        Connection cn = null;
        CallableStatement cs = null;
        try {
            UtOracleDataSource.creerAcces();
            cn = UtOracleDataSource.getConnection();

            // Appeler la procédure revaloriserLoyer
            cs = cn.prepareCall("{CALL revaloriserLoyer(?, ?, ?, ?)}");
            cs.setLong(1, idBail);
            cs.setDouble(2, nouveauLoyerCalcule);
            cs.setDouble(3, irlNouveau);
            cs.setDouble(4, irlAncien);
            cs.execute();

            JOptionPane.showMessageDialog(vue,
                    "Revalorisation effectuée avec succès !\n\n" +
                    "Le loyer du bail et tous les loyers non payés\n" +
                    "ont été mis à jour.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

            vue.setRevalorisationEffectuee(true);
            vue.fermer();

        } catch (SQLException ex) {
            ex.printStackTrace();
            
            String message = ex.getMessage();
            
            // Analyser le code d'erreur Oracle
            if (message != null) {
                if (message.contains("-20107") || message.contains("-20108")) {
                    // Erreur de règle métier (bail trop récent, déjà revalorisé, ou IRL dépassé)
                    // Extraire le message utile
                    String messageUtile = extraireMessageErreur(message);
                    JOptionPane.showMessageDialog(vue,
                            messageUtile,
                            "Revalorisation impossible",
                            JOptionPane.WARNING_MESSAGE);
                } else if (message.contains("-20109")) {
                    JOptionPane.showMessageDialog(vue,
                            "Bail introuvable.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(vue,
                            "Erreur lors de la revalorisation :\n" + message,
                            "Erreur BDD",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(vue,
                        "Erreur inconnue lors de la revalorisation.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            try {
                if (cs != null) cs.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Extrait le message d'erreur utile depuis l'exception Oracle
     */
    private String extraireMessageErreur(String messageComplet) {
        if (messageComplet == null) return "Erreur inconnue";
        
        // Chercher "REGLE 7 VIOLEE :" et extraire le reste
        int idx = messageComplet.indexOf("REGLE 7 VIOLEE :");
        if (idx >= 0) {
            String suite = messageComplet.substring(idx);
            // Couper au premier retour à la ligne ou fin
            int finIdx = suite.indexOf('\n');
            if (finIdx > 0) {
                return suite.substring(0, finIdx);
            }
            return suite;
        }
        
        // Sinon, chercher "ORA-"
        idx = messageComplet.indexOf("ORA-");
        if (idx >= 0) {
            return messageComplet.substring(idx);
        }
        
        return messageComplet;
    }
}