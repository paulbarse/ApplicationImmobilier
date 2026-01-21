package vue.Document;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import Controleur.Document.gestionDocumentsLocataire;
import vue.Principale.pageListeDesLocataires.LocataireItem;
import vue.refacto.AbstractPageDocuments;

/**
 * Page affichant les documents lies a un locataire - Diagnostics (du logement)
 */
public class pageDocumentsLocataire
    extends AbstractPageDocuments<LocataireItem> {

    private static final long serialVersionUID = 1L;

    private gestionDocumentsLocataire gestion;

    private JTable tableDiagnostics;
    private JTable tableReleveEau; 
    private JTable tableCaution;
    private JTable tableLoyers;
    private JTable tableQuittances;

    public pageDocumentsLocataire(LocataireItem locataire) {
        super(locataire, "Documents du locataire", "Documents pour : "
            + (locataire != null ? locataire.getTitre() : ""));

        this.gestion = new gestionDocumentsLocataire(this);
        setController(this.gestion, "FERMER_DOCUMENTS_LOCATAIRE");

        // Charger les donnees depuis la BDD
        this.gestion.chargerToutesDonnees();
    }

    @Override
    protected void buildTabs() {
        // DIAGNOSTICS 
        tableDiagnostics = addTableTab("Diagnostics",
            "Diagnostics techniques (DPE, Amiante, etc.)",
            new String[] { "ID", "Type Diagnostic", "Reference",
                "Date Emission", "Date Expiration", "Statut" },
            "DIAGNOSTIC");
        hideColumn(tableDiagnostics, 0);
        tableDiagnostics.getColumnModel().getColumn(1).setPreferredWidth(200);

        // RELEVÉS D'EAU (AJOUTÉ) 
        tableReleveEau = addTableTab("Relevés d'eau",
            "Relevés de compteur d'eau", new String[] { "ID", "Date",
                "Ancien Index", "Nouveau Index", "Consommation (m³)" },
            "RELEVE_EAU");
        hideColumn(tableReleveEau, 0);
        tableReleveEau.getColumnModel().getColumn(1).setPreferredWidth(120);

        // CAUTION 
        tableCaution = addTableTab("Caution / Depot de garantie",
            "Suivi du depot de garantie", new String[] { "ID", "Date",
                "Type Mouvement", "Montant", "Moyen Paiement", "Observations" },
            "CAUTION");
        hideColumn(tableCaution, 0);

        //  LOYERS 
        tableLoyers = addTableTab("Loyers",
            "Loyers mensuels (ce qui est du) - Selectionner puis 'Marquer comme paye'",
            new String[] { "ID", "Periode", "Loyer Nu", "Charges", "Total",
                "Statut" },
            "LOYER", true, // Bouton "Marquer comme paye"
            false, // Pas de Modifier
            false, // Pas de Supprimer
            "Marquer comme paye" // Label personnalise
        );
        hideColumn(tableLoyers, 0);

        //  QUITTANCES (preuves de paiement)
        tableQuittances = addTableTab("Quittances",
            "Quittances de loyer (preuves de paiement) - 'Supprimer' annule le paiement",
            new String[] { "ID", "Periode", "Loyer Nu", "Charges", "Total",
                "Date Paiement", "N° Quittance" },
            "QUITTANCE", false, // Pas de bouton Ajouter
            false, // Pas de bouton Modifier
            true, // Bouton Supprimer (pour annuler le paiement)
            null);
        hideColumn(tableQuittances, 0);
    }

    /**
     * Cache une colonne d'une JTable (pour l'ID)
     */
    private void hideColumn(JTable table, int columnIndex) {
        TableColumn column = table.getColumnModel().getColumn(columnIndex);
        column.setMinWidth(0);
        column.setMaxWidth(0);
        column.setPreferredWidth(0);
        column.setResizable(false);
    }

    // GETTERS

    public LocataireItem getLocataire() {
        return entity;
    }

    public JTable getTableDiagnostics() {
        return tableDiagnostics;
    }

    // AJOUTÉ
    public JTable getTableReleveEau() {
        return tableReleveEau;
    }

    public JTable getTableCaution() {
        return tableCaution;
    }

    public JTable getTableLoyers() {
        return tableLoyers;
    }

    public JTable getTableQuittances() {
        return tableQuittances;
    }

    /**
     * Retourne le gestionnaire pour permettre le rafraichissement externe
     */
    public gestionDocumentsLocataire getGestion() {
        return gestion;
    }
}