package vue.Document;

import vue.refacto.AbstractPageSaisieDocument;

/**
 * Vue de saisie pour les documents de logement
 */
public class pageSaisieDocumentLogement extends AbstractPageSaisieDocument {

    private static final long serialVersionUID = 1L;

    public pageSaisieDocumentLogement(String titreFenetre, String[] labelsChamps, String[] valeursInitiales) {
        super(titreFenetre, labelsChamps, valeursInitiales);
    }
}
