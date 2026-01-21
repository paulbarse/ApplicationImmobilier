package vue.Document;

import vue.refacto.AbstractPageSaisieDocument;

/**
 * Vue de saisie pour les documents de garage
 */
public class pageSaisieDocumentGarage extends AbstractPageSaisieDocument {

    private static final long serialVersionUID = 1L;

    public pageSaisieDocumentGarage(String titre, String[] labels, String[] valeursInitiales) {
        super(titre, labels, valeursInitiales);
    }
}
