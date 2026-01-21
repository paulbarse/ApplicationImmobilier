package vue.Document;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import Controleur.Document.gestionDocumentsGarage;
import modele.Garage;
import vue.refacto.AbstractPageDocuments;

/**
 * Page affichant les documents lies a un garage
 */
public class pageDocumentsGarage extends AbstractPageDocuments<Garage> {

    private static final long serialVersionUID = 1L;

    private gestionDocumentsGarage gestion;

    private JTable tableLocataire;
    private JTable tableBail;
    private JTable tableDepot;
    private JTable tableReleves;
    private JTable tableAssurances;
    private JTable tableLoyers;
    private JTable tableQuittances;

    public pageDocumentsGarage() {
        this(null);
    }

    public pageDocumentsGarage(Garage garage) {
        super(
                garage,
                "Documents du garage",
                "Garage : " + (garage != null && garage.getAdresseGarage() != null 
                    ? garage.getAdresseGarage() + " (ID: " + garage.getIdGarage() + ")"
                    : "Non specifie")
        );

        this.gestion = new gestionDocumentsGarage(this);
        setController(this.gestion, "FERMER_DOCUMENTS_GARAGE");

        // Charger les donnees depuis la BDD
        if (garage != null) {
            this.gestion.chargerToutesDonnees();
        }
    }

    @Override
    protected void buildTabs() {
        //  LOCATAIRE 
        tableLocataire = addTableTab(
                "Locataire",
                "Locataire actuel du garage (via le bail)",
                new String[]{"ID", "Nom", "Prenom", "Email", "Telephone", "Adresse"},
                "LOCATAIRE_GARAGE",
                false, false, false, null
        );
        hideColumn(tableLocataire, 0);

        // BAIL 
        tableBail = addTableTab(
                "Bail",
                "Bail du garage",
                new String[]{"ID", "Date debut", "Date fin", "Loyer", "Etat", "Locataire(s)"},
                "BAIL_GARAGE",
                false, false, false, null
        );
        hideColumn(tableBail, 0);

        // DEPOT DE GARANTIE
        tableDepot = addTableTab(
                "Depot de garantie",
                "Mouvements de caution du garage",
                new String[]{"ID", "Date", "Type Mouvement", "Montant", "Moyen Paiement", "Observations"},
                "DEPOT_GARAGE"
        );
        hideColumn(tableDepot, 0);

        // RELEVES COMPTEURS - AVEC QUOTITES 
        tableReleves = addTableTab(
                "Releves compteur",
                "Releves des compteurs avec quotites de repartition",
                new String[]{
                    "ID", "Type", "Date", "Ancien Index", "Nouvel Index", 
                    "N Compteur", "Unite", "Conso",
                    "% Eau", "% Elec", "% OM", "% Entretien"
                },
                "RELEVE"
        );
        hideColumn(tableReleves, 0);

        // ASSURANCES 
        tableAssurances = addTableTab(
                "Assurances",
                "Assurances du garage (si loue)",
                new String[]{"N Assurance", "Compagnie", "Type", "Prime TTC (EUR)", "Date effet", "Date echeance"},
                "ASSURANCE",
                true, true, true, null
        );

        //  LOYERS (en attente) 
        tableLoyers = addTableTab(
                "Loyers",
                "Loyers du garage (en attente) - Selectionner puis 'Marquer comme paye'",
                new String[]{"ID", "Periode", "Loyer Nu", "Charges", "Total", "Statut"},
                "LOYER_GARAGE",
                true, false, false, "Marquer comme paye"
        );
        hideColumn(tableLoyers, 0);

        //  QUITTANCES (payes)
        tableQuittances = addTableTab(
                "Quittances",
                "Quittances de loyer du garage (preuves de paiement)",
                new String[]{"ID", "Periode", "Loyer Nu", "Charges", "Total", "Date Paiement", "N Quittance"},
                "QUITTANCE_GARAGE",
                false, false, true, null
        );
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

    //  GETTERS 

    public JTable getTableLocataire() { return tableLocataire; }
    public JTable getTableBail() { return tableBail; }
    public JTable getTableDepot() { return tableDepot; }
    public JTable getTableReleves() { return tableReleves; }
    public JTable getTableAssurances() { return tableAssurances; }
    public JTable getTableLoyers() { return tableLoyers; }
    public JTable getTableQuittances() { return tableQuittances; }

    public Garage getGarage() { return entity; }
    
    public gestionDocumentsGarage getGestion() { return gestion; }
}