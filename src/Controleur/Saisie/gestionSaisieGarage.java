package Controleur.Saisie;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

import modele.Garage;
import vue.Saisie.pageSaisieGarage;

/**
 * Contrôleur pour la saisie d'un garage
 */
public class gestionSaisieGarage implements ActionListener {

    private pageSaisieGarage vue;

    public gestionSaisieGarage(pageSaisieGarage vue) {
        this.vue = vue;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if ("VALIDER_GARAGE".equals(cmd)) {
            validerSaisie();
        } else if ("ANNULER_GARAGE".equals(cmd)) {

            vue.setResultat(null);
            vue.dispose();
        }
    }

    private void validerSaisie() {
        try {
            // 1. Récupération des champs
            String idStr = vue.getTxtId().getText().trim();
            String adresse = vue.getTxtAdresse().getText().trim();
            String txtSurface = vue.getTxtSurface().getText().trim();
            String txtNumFiscal = vue.getTxtNumFiscal().getText().trim();
            Long idBatiment = vue.getSelectedBatimentId();

            // 2. Validations
            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(vue, "L'ID Garage est obligatoire.", "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (txtNumFiscal.isEmpty()) {
                JOptionPane.showMessageDialog(vue, "Le numéro fiscal est obligatoire.", "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (txtSurface.isEmpty()) {
                JOptionPane.showMessageDialog(vue, "La surface est obligatoire.", "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (idBatiment == null) {
                JOptionPane.showMessageDialog(vue, "Veuillez sélectionner un bâtiment.", "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 3. Parsing
            long idGarage = Long.parseLong(idStr);
            long numFiscal = Long.parseLong(txtNumFiscal);
            int surface = Integer.parseInt(txtSurface);

            // 4. Création objet avec l'ID manuel
            Garage garageCree = new Garage(
                    idGarage,
                    surface,    
                    numFiscal,
                    adresse,
                    idBatiment,
                    null,       
                    null        
            );
            
            vue.setResultat(garageCree);
            vue.dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vue,
                    "L'ID, la surface et le n° fiscal doivent être des chiffres valides.",
                    "Erreur de format", JOptionPane.ERROR_MESSAGE);
        }
    }
}
