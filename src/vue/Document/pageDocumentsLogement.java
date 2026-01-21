package vue.Document;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import Controleur.Document.gestionDocumentsLogement;
import modele.Logement;
import vue.refacto.AbstractPageDocuments;

/**
 * Page affichant les documents lies a un logement
 */
public class pageDocumentsLogement extends AbstractPageDocuments<Logement> {

    private static final long serialVersionUID = 1L;

    private gestionDocumentsLogement gestion;

    private JTable tableBaux;
    private JTable tableGaragesLoues;
    private JTable tableGarants;
    private JTable tableEtatDesLieux;
    private JTable tableReleves;
    private JTable tableAssurances;
    private JTable tableLoyers;
    private JTable tableQuittances;

    public pageDocumentsLogement(Logement logement) {
        super(logement,
              "Documents - Logement",
              "Documents : " + (logement != null ? logement.getBatiment().getAdresse() + " (ID: " + logement.getIdLogement() + ")" : ""));

        this.gestion = new gestionDocumentsLogement(this);
        setController(this.gestion, "FERMER_DOCUMENTS");

        // Charger les donnees depuis la BDD
        this.gestion.chargerToutesDonnees();
    }

    @Override
    protected void buildTabs() {

        // BAUX 
        tableBaux = addTableTab(
                "Baux",
                "Historique des baux du logement",
                new String[]{"ID", "Locataire", "Date debut", "Date fin", 
                            "Loyer initial", "Provisions", "Caution", "Etat"},
                "BAIL",
                false, false, false, null
        );
        hideColumn(tableBaux, 0);

        //GARAGES LOUES 
        tableGaragesLoues = addTableTab(
                "Garages loues",
                "Garages attaches au logement",
                new String[]{"ID", "Adresse", "Surface (m2)", "N Fiscal", "Assurance"},
                "GARAGE_LOUE",
                true, false, true, "Lier un garage"
        );
        hideColumn(tableGaragesLoues, 0);

        //GARANTS 
        tableGarants = addTableTab(
                "Garants",
                "Garants lies au bail actif",
                new String[]{"ID", "Nom", "Prenom", "Adresse", "Email", "Telephone"},
                "GARANT"
        );
        hideColumn(tableGarants, 0);

        //  ETATS DES LIEUX 
        tableEtatDesLieux = addTableTab(
                "Etats des lieux",
                "Etats des lieux d'entree / sortie",
                new String[]{"ID", "Type (Entree/Sortie)", "Date", "Observations"},
                "EDL"
        );
        hideColumn(tableEtatDesLieux, 0);

        // RELEVES COMPTEURS - AVEC QUOTITES 
        tableReleves = addTableTab(
                "Releves compteurs",
                "Releves des compteurs avec quotites de repartition",
                new String[]{
                    "ID", "Type", "Date", "Ancien index", "Nouvel index", 
                    "N Compteur", "Unite", "Conso",
                    "% Eau", "% Elec", "% OM", "% Entretien"
                },
                "RELEVE"
        );
        hideColumn(tableReleves, 0);

        //  ASSURANCES
        tableAssurances = addTableTab(
                "Assurances",
                "Assurances du logement",
                new String[]{"N Assurance", "Annee", "Compagnie", "Type", "Prime TTC (EUR)", "Date effet", "Date echeance"},
                "ASSURANCE",
                true, true, true, null
        );

        // LOYERS (en attente) 
        tableLoyers = addTableTab(
                "Loyers",
                "Loyers en attente - Selectionner une ligne puis cliquer sur le bouton",
                new String[]{"ID", "Periode", "Loyer Nu", "Charges", "Total", "Statut"},
                "LOYER",
                true, false, false, "Marquer comme paye"
        );
        hideColumn(tableLoyers, 0);

        //  QUITTANCES (payes) 
        tableQuittances = addTableTab(
                "Quittances",
                "Quittances de loyer (preuves de paiement)",
                new String[]{"ID", "Periode", "Loyer Nu", "Charges", "Total", 
                            "Date Paiement", "N Quittance"},
                "QUITTANCE",
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

    //GETTERS

    public Logement getLogement() { return entity; }
    
    public JTable getTableBaux() { return tableBaux; }
    public JTable getTableGaragesLoues() { return tableGaragesLoues; }
    public JTable getTableGarants() { return tableGarants; }
    public JTable getTableEtatDesLieux() { return tableEtatDesLieux; }
    public JTable getTableReleves() { return tableReleves; }
    public JTable getTableAssurances() { return tableAssurances; }
    public JTable getTableLoyers() { return tableLoyers; }
    public JTable getTableQuittances() { return tableQuittances; }
    
    public gestionDocumentsLogement getGestion() { return gestion; }
}