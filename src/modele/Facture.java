package modele;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Classe Facture - Compatible avec la table FACTURE
 */
public class Facture {

    private long idFacture;                 // ID_FACTURE PK
    private Double acompte;                 // ACCOMPTE
    private String nature;                  // NATURE
    private String periodeDeb;              // PERIODE_DEB (stocké comme String)
    private Date dateEmission;              // DATE_EMISSION NOT NULL
    private Double montantHT;               // MONTANT_HT
    private double montantTTC;              // MONTANT_TTC NOT NULL
    private Double montantTEOM;             // MONTANT_TEOM
    private Boolean recuperableLocataire;   // RECUPERABLE_LOCATAIRE (1/0)
    private Boolean deductibleImpot;        // DEDUCTIBLE_IMPOT (1/0)
    private Date periodeFin;                // PERIODE_FIN
    private String travaux;                 // TRAVAUX
    private Date dateDevis;                 // DATEDEVIS
    private Double montantDevis;            // MONTANTDEVIS

    private long siret;                     // SIRET NOT NULL (FK)
    private Long idCharge;                  // ID_CHARGE (FK)
    private Long idBatiment;                // ID_BATIMENT (FK)
    private Long idBail;                    // ID_BAIL (FK)
    private Long idLogement;                // ID_LOGEMENT (FK)

    // Affichage
    private String nomEntreprise;           // join ENTREPRISE
    private String statut;                  // STATUT_PAIEMENT

    // constructeur
    
    public Facture(long idFacture, Double acompte, String nature, String periodeDeb,
                   Date dateEmission, Double montantHT, double montantTTC, Double montantTEOM,
                   Boolean recuperableLocataire, Boolean deductibleImpot, Date periodeFin,
                   String travaux, Date dateDevis, Double montantDevis,
                   long siret, Long idCharge, Long idBatiment, Long idBail, 
                   Long idLogement, String statut) {
        this.idFacture = idFacture;
        this.acompte = acompte;
        this.nature = nature;
        this.periodeDeb = periodeDeb;
        this.dateEmission = dateEmission;
        this.montantHT = montantHT;
        this.montantTTC = montantTTC;
        this.montantTEOM = montantTEOM;
        this.recuperableLocataire = recuperableLocataire;
        this.deductibleImpot = deductibleImpot;
        this.periodeFin = periodeFin;
        this.travaux = travaux;
        this.dateDevis = dateDevis;
        this.montantDevis = montantDevis;
        this.siret = siret;
        this.idCharge = idCharge;
        this.idBatiment = idBatiment;
        this.idBail = idBail;
        this.idLogement = idLogement;
        this.statut = (statut != null && !statut.trim().isEmpty()) ? statut : "A payer";
    }

    /**
     * Constructeur retrocompatible (sans idLogement)
     */
    public Facture(long idFacture, Double acompte, String nature, String periodeDeb,
                   Date dateEmission, Double montantHT, double montantTTC, Double montantTEOM,
                   Boolean recuperableLocataire, Boolean deductibleImpot, Date periodeFin,
                   String travaux, Date dateDevis, Double montantDevis,
                   long siret, Long idCharge, Long idBatiment, Long idBail, String statut) {
        this(idFacture, acompte, nature, periodeDeb, dateEmission, montantHT, montantTTC,
             montantTEOM, recuperableLocataire, deductibleImpot, periodeFin, travaux,
             dateDevis, montantDevis, siret, idCharge, idBatiment, idBail, null, statut);
    }

    /**
     * 
     * Ce constructeur accepte un Date pour periodeDeb et le convertit automatiquement
     * en String au format "dd/MM/yyyy" pour le stockage.
     * 
     * Utilise par pageSaisieFacture et gestionSaisieFacture (JSpinner).
     */
    public Facture(long idFacture, Double acompte, String nature, Date periodeDeb,
                   Date dateEmission, Double montantHT, double montantTTC, Double montantTEOM,
                   Boolean recuperableLocataire, Boolean deductibleImpot, Date periodeFin,
                   String travaux, Date dateDevis, Double montantDevis,
                   long siret, Long idCharge, Long idBatiment, Long idBail, String statut) {
        this(idFacture, acompte, nature, 
             dateToString(periodeDeb),  // Conversion Date -> String
             dateEmission, montantHT, montantTTC, montantTEOM, 
             recuperableLocataire, deductibleImpot, periodeFin, travaux,
             dateDevis, montantDevis, siret, idCharge, idBatiment, idBail, null, statut);
    }

    /**
     * Convertit une Date en String au format "dd/MM/yyyy"
     */
    private static String dateToString(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    /**
     * Constructeur simplifie (pour affichage)
     */
    public Facture(String idFacture, java.sql.Date dateEmission, String nature,
                   String travaux, String nomEntreprise, double montantTTC, String statut) {
        this.idFacture = Long.parseLong(idFacture);
        this.dateEmission = dateEmission;
        this.nature = nature;
        this.travaux = travaux;
        this.nomEntreprise = nomEntreprise;
        this.montantTTC = montantTTC;
        this.statut = (statut != null && !statut.trim().isEmpty()) ? statut : "A payer";
    }

    /**
     * Constructeur pour facture de travaux sur logement
     */
    public static Facture factureTravauxLogement(long idFacture, long idLogement, 
            long siret, String nature, double montantTTC, Date dateEmission,
            String travaux, boolean deductible, Double montantDevis) {
        return new Facture(idFacture, null, nature, (String) null, dateEmission, null, montantTTC,
                null, false, deductible, null, travaux, null, montantDevis,
                siret, null, null, null, idLogement, "A payer");
    }

    // GETTERS
    public long getIdFactureLong() { return idFacture; }
    public String getIdFacture() { return String.valueOf(idFacture); }

    public Double getAcompte() { return acompte; }
    public String getNature() { return nature; }
    public String getPeriodeDeb() { return periodeDeb; }
    public Date getDateEmission() { return dateEmission; }
    public Double getMontantHT() { return montantHT; }
    public double getMontantTTC() { return montantTTC; }
    public Double getMontantTEOM() { return montantTEOM; }
    public Boolean isRecuperableLocataire() { return recuperableLocataire; }
    public Boolean isDeductibleImpot() { return deductibleImpot; }
    public Date getPeriodeFin() { return periodeFin; }
    public String getTravaux() { return travaux; }
    public Date getDateDevis() { return dateDevis; }
    public Double getMontantDevis() { return montantDevis; }

    public long getSiret() { return siret; }
    public Long getIdCharge() { return idCharge; }
    public Long getIdBatiment() { return idBatiment; }
    public Long getIdBail() { return idBail; }
    public Long getIdLogement() { return idLogement; }

    public String getNomEntreprise() { return nomEntreprise; }
    public String getStatut() { return statut; }

    // SETTERS 
    public void setAcompte(Double acompte) { this.acompte = acompte; }
    public void setNature(String nature) { this.nature = nature; }
    public void setPeriodeDeb(String periodeDeb) { this.periodeDeb = periodeDeb; }
    public void setDateEmission(Date dateEmission) { this.dateEmission = dateEmission; }
    public void setMontantHT(Double montantHT) { this.montantHT = montantHT; }
    public void setMontantTTC(double montantTTC) { this.montantTTC = montantTTC; }
    public void setMontantTEOM(Double montantTEOM) { this.montantTEOM = montantTEOM; }
    public void setRecuperableLocataire(Boolean recuperableLocataire) { this.recuperableLocataire = recuperableLocataire; }
    public void setDeductibleImpot(Boolean deductibleImpot) { this.deductibleImpot = deductibleImpot; }
    public void setPeriodeFin(Date periodeFin) { this.periodeFin = periodeFin; }
    public void setTravaux(String travaux) { this.travaux = travaux; }
    public void setDateDevis(Date dateDevis) { this.dateDevis = dateDevis; }
    public void setMontantDevis(Double montantDevis) { this.montantDevis = montantDevis; }

    public void setSiret(long siret) { this.siret = siret; }
    public void setIdCharge(Long idCharge) { this.idCharge = idCharge; }
    public void setIdBatiment(Long idBatiment) { this.idBatiment = idBatiment; }
    public void setIdBail(Long idBail) { this.idBail = idBail; }
    public void setIdLogement(Long idLogement) { this.idLogement = idLogement; }

    public void setNomEntreprise(String nomEntreprise) { this.nomEntreprise = nomEntreprise; }
    public void setStatut(String statut) { this.statut = (statut != null && !statut.trim().isEmpty()) ? statut : "A payer"; }

    /**
     * Verifie si la facture est liee a un logement specifique
     */
    public boolean hasLogement() {
        return idLogement != null && idLogement > 0;
    }

    /**
     * Verifie si la facture concerne des travaux
     */
    public boolean isTravaux() {
        return travaux != null && !travaux.trim().isEmpty();
    }

    /**
     * Verifie si la facture est payee
     */
    public boolean isPayee() {
        return statut != null && statut.trim().equalsIgnoreCase("PAYE");
    }

    @Override
    public int hashCode() { return Objects.hash(idFacture); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Facture)) return false;
        Facture other = (Facture) obj;
        return idFacture == other.idFacture;
    }

    @Override
    public String toString() {
        return "Facture #" + idFacture + " - " + montantTTC + " EUR (" + (statut != null ? statut : "A payer") + ")";
    }
}
