package Controleur.Saisie;

import Controleur.refacto.AbstractSaisieController;
import modele.Batiment;
import vue.Saisie.pageSaisieBatiment;

// Contrôleur pour la page de saisie d'un bâtiment

public class gestionSaisieBatiment extends AbstractSaisieController<pageSaisieBatiment, Batiment> {

    public gestionSaisieBatiment(pageSaisieBatiment vue) {
        super(vue, "Bâtiment", "BATIMENT");
    }

    public gestionSaisieBatiment(pageSaisieBatiment vue, boolean modeModification) {
        super(vue, "Bâtiment", "BATIMENT", modeModification);
    }

    @Override
    protected boolean validateFields() throws ValidationException {
        // ID obligatoire
        validateNotEmpty(vue.getTxtIdBatiment(), "ID du bâtiment");
        
        // Valider que l'ID est un nombre positif
        validatePositiveLong(vue.getTxtIdBatiment(), "ID du bâtiment");
        
        // Adresse obligatoire
        validateNotEmpty(vue.getTxtAdresseComplete(), "Adresse complète");
        
        // Code postal obligatoire et valide
        validateCodePostal(vue.getTxtCodePostal());
        
        // Nombre d'étages optionnel mais doit être un nombre si renseigné
        validateOptionalInt(vue.getTxtNbEtages(), "Nombre d'étages");
        
        return true;
    }

    @Override
    protected Batiment createEntityFromFields() {
        try {
            long id = Long.parseLong(vue.getTxtIdBatiment().getText().trim());
            String adresse = vue.getTxtAdresseComplete().getText().trim();
            String type = vue.getCbType().getSelectedItem().toString().trim().toUpperCase();
            String rue = getTextOrNull(vue.getTxtRue());
            int cp = Integer.parseInt(vue.getTxtCodePostal().getText().trim());
            
            Integer nbEtages = null;
            String nbEtagesStr = vue.getTxtNbEtages().getText().trim();
            if (!nbEtagesStr.isEmpty()) {
                nbEtages = Integer.parseInt(nbEtagesStr);
            }
            
            return new Batiment(id, adresse, nbEtages, type, rue, cp);
            
        } catch (NumberFormatException e) {
            showError("Erreur de format numérique");
            return null;
        }
    }

    @Override
    protected void setResult(Batiment batiment) {
        if (batiment == null) {
            vue.setResultat(null);
        } else {
            vue.setResultat(new String[] {
                String.valueOf(batiment.getIdBatimentLong()),
                batiment.getAdresse(),
                (batiment.getNombreEtage() == null ? "" : String.valueOf(batiment.getNombreEtage())),
                (batiment.getTypeBatiment() == null ? "" : batiment.getTypeBatiment()),
                (batiment.getRue() == null ? "" : batiment.getRue()),
                String.valueOf(batiment.getCodePostal())
            });
        }
    }

    @Override
    protected void showSuccessMessage() {
        // Ne pas afficher de message, le parent le gère
    }
}
