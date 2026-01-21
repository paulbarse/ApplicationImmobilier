package vue.Document;

import javax.swing.JTable;

import Controleur.Document.gestionDocumentsBatiments;
import vue.Principale.pageListeDesBatiments.BatimentItem;
import vue.refacto.AbstractPageDocuments;

/**
 * Page Documents du Batiment - VERSION CORRIGEE
 */
public class pageDocumentsBatiments extends AbstractPageDocuments<BatimentItem> {

    private static final long serialVersionUID = 1L;

    private final gestionDocumentsBatiments gestion;

    private JTable tableCharges;
    private JTable tableFactures;
    private JTable tableBiens;

    public pageDocumentsBatiments() {
        this(null);
    }

    public pageDocumentsBatiments(BatimentItem batiment) {
        super(
                batiment,
                "Documents du batiment",
                batiment != null && batiment.getAdresseComplete() != null
                        ? batiment.getAdresseComplete()
                        : "Documents du batiment"
        );

        this.gestion = new gestionDocumentsBatiments(this);
        setController(this.gestion, "FERMER_DOCUMENTS");
    }


    @Override
    protected void buildTabs() {
        // CHARGES 
        tableCharges = addTableTab(
                "Charges",
                "Charges du batiment",
                new String[]{"ID", "Date", "Nature", "Type", "Montant (EUR)", "% Recuperable"},
                "CHARGE"
        );

        // FACTURES
        tableFactures = addTableTab(
                "Factures",
                "Factures du batiment",
                new String[]{"ID", "Date", "Nature", "Entreprise (SIRET)", "Montant TTC (EUR)", "Statut"},
                "FACTURE",
                false,
                true, 
                true,  
                null
        );

    
        
        // BIENS
        tableBiens = addTableTab(
        		"Biens",
        		"Biens du batiment",
        		new String[] {"Adresse", "Type", "Surface (m2)", "Nb Pieces", "Num Fiscal", "Statut", "Logement/Garage"},
        		"BIENS",
        		false);
    }

    // Getters pour le controleur
    public JTable getTableCharges()  { return tableCharges; }
    public JTable getTableFactures() { return tableFactures; }
    public JTable getTableBiens() { return tableBiens; }

    public BatimentItem getBatiment() { return entity; }
}