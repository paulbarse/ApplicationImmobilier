package Controleur.Saisie;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import modele.Batiment;
import modele.Logement;
import vue.Principale.pageListeDesLogements.LogementItem;
import vue.Saisie.pageSaisieLogement;

/**
 * Contrôleur pour la saisie d'un logement
 */
public class gestionSaisieLogement implements ActionListener {

    private final pageSaisieLogement vue;

    public gestionSaisieLogement(pageSaisieLogement vue) {
        this.vue = vue;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String cmd = e.getActionCommand();

        switch (cmd) {

        case "VALIDER_LOGEMENT": {

            // Récupération des valeurs 
            String idStr = vue.getTxtId().getText().trim();
            String complement = vue.getTxtAdresse().getText().trim();
            String type = vue.getTxtType().getText().trim();
            String surfaceStr = vue.getTxtSurface().getText().trim();
            String nbPiecesStr = vue.getTxtNbPieces().getText().trim();
            String numFiscalStr = vue.getTxtNumFiscal().getText().trim();

            // Bâtiment sélectionné (obligatoire)
            Long idBatiment = vue.getSelectedBatimentId();
            if (idBatiment == null) {
                JOptionPane.showMessageDialog(vue,
                    "Veuillez sélectionner un bâtiment.",
                    "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Récupérer le type du bâtiment sélectionné (MAISON / IMMEUBLE)
            pageSaisieLogement.BatimentComboItem selected =
                    (pageSaisieLogement.BatimentComboItem) vue.getCbBatiment().getSelectedItem();


            Batiment bat = (selected != null) ? selected.getBatiment() : null;
            

            if (bat == null) {
                JOptionPane.showMessageDialog(vue,
                    "Erreur: impossible de récupérer les informations du bâtiment.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String typeBatiment = (bat.getTypeBatiment() != null)
                    ? bat.getTypeBatiment().trim().toUpperCase()
                    : "";

            boolean estMaison = "MAISON".equals(typeBatiment);
            String adressePrincipal = bat.getAdresse();

            //  VALIDATIONS 
            
            // ID obligatoire
            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(vue,
                    "L'ID du logement est obligatoire.",
                    "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Numéro fiscal obligatoire
            if (numFiscalStr.isEmpty()) {
                JOptionPane.showMessageDialog(vue,
                    "Le numéro fiscal est obligatoire.",
                    "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Surface obligatoire
            if (surfaceStr.isEmpty()) {
                JOptionPane.showMessageDialog(vue,
                    "La surface est obligatoire.",
                    "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Nombre de pièces obligatoire
            if (nbPiecesStr.isEmpty()) {
                JOptionPane.showMessageDialog(vue,
                    "Le nombre de pièces est obligatoire.",
                    "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Règles spécifiques :
            // - Si MAISON : on force type=MAISON et on vide le complément
            // - Si IMMEUBLE : complément et type doivent être saisis
            if (estMaison) {
                type = "MAISON";
                complement = "";
            } else {
                // immeuble : type logement utile
                if (type.isEmpty()) {
                    JOptionPane.showMessageDialog(vue,
                        "Veuillez renseigner le type de logement (ex: Appartement, Studio...).",
                        "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // immeuble : complément conseillé/utile
                if (complement.isEmpty()) {
                    JOptionPane.showMessageDialog(vue,
                        "Veuillez renseigner un complément d'adresse (ex: Apt 3, Étage 2, Porte droite...).",
                        "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // PARSING 
            long id;
            long numFiscal;
            int nbPieces;
            double surface;

            try {
                id = Long.parseLong(idStr);
                numFiscal = Long.parseLong(numFiscalStr);
                nbPieces = Integer.parseInt(nbPiecesStr);
                surface = Double.parseDouble(surfaceStr.replace(",", "."));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vue,
                    "Erreur de format numérique: " + ex.getMessage(),
                    "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // CRÉATION DU LOGEMENT
            Logement nouveauLogement = new Logement(
                id,
                type,
                adressePrincipal,
                complement,
                numFiscal,
                surface,
                nbPieces,
                null,           // idReleve (nullable)
                idBatiment,     // idBatiment (obligatoire)
                null            // idBail (nullable)
            );


            vue.setLogementCree(nouveauLogement);
            vue.setResultat(new LogementItem(nouveauLogement, "")); 

            vue.dispose();
            break;
        }

        case "ANNULER_LOGEMENT": {
            vue.setLogementCree(null);
            vue.setResultat(null);
            vue.dispose();
            break;
        }

        default:
            break;
        }
    }
}
