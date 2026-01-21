package vue.Document;

import vue.refacto.AbstractPageSaisieDocument;

/**
 * Vue de saisie pour les documents de locataire
 */
public class pageSaisieDocumentLocataire extends AbstractPageSaisieDocument {

    private static final long serialVersionUID = 1L;

    public pageSaisieDocumentLocataire(String titreFenetre, String[] labelsChamps, String[] valeursInitiales) {
        super(titreFenetre, labelsChamps, valeursInitiales);
    }
}
