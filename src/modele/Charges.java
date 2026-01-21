package modele;

import java.util.Date;
import java.util.Objects;

/**
 * Classe Charges - Compatible avec la table CHARGES
 */
public class Charges {
    private long idCharge;             // NUMBER(22)
    private String nature;             // NOT NULL
    private Double montant;            // Nullable
    private Date dateCharge;           // Nullable (remplace mois/annee)
    private String typeCharges;        // NOT NULL
    private double pctRecuperable;     // DEFAULT 100 NOT NULL
    private Long idBatiment;           // FK NOT NULL

    // Constructeur complet
    public Charges(long idCharge, String nature, Double montant, Date dateCharge,
                   String typeCharges, double pctRecuperable, Long idBatiment) {
        this.idCharge = idCharge;
        this.nature = nature;
        this.montant = montant;
        this.dateCharge = dateCharge;
        this.typeCharges = typeCharges;
        this.pctRecuperable = pctRecuperable;
        this.idBatiment = idBatiment;
    }

    // Constructeur simplifié
    public Charges(long idCharge, String nature, Double montant, Date dateCharge,
                   String typeCharges, Long idBatiment) {
        this(idCharge, nature, montant, dateCharge, typeCharges, 100.0, idBatiment);
    }

    // Constructeur de compatibilité avec ancien code (mois/annee séparés)
    public Charges(String idCharge, String nature, double montant, String mois,
                   int annee, String typeCharges) {
        this.idCharge = Long.parseLong(idCharge);
        this.nature = nature;
        this.montant = montant;
        this.typeCharges = typeCharges;
        this.pctRecuperable = 100.0;
        // On ignore mois/annee car remplacé par dateCharge
    }

    // GETTERS 
    public long getIdChargeLong() {
        return idCharge;
    }
    
    // Compatibilité ancien code
    public String getIdCharge() {
        return String.valueOf(idCharge);
    }

    public String getNature() {
        return nature;
    }

    public Double getMontant() {
        return montant;
    }

    public Date getDateCharge() {
        return dateCharge;
    }

    public String getTypeCharges() {
        return typeCharges;
    }

    public double getPctRecuperable() {
        return pctRecuperable;
    }

    public Long getIdBatiment() {
        return idBatiment;
    }
    

    
 

    // SETTERS 
    public void setIdCharge(long idCharge) {
        this.idCharge = idCharge;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public void setDateCharge(Date dateCharge) {
        this.dateCharge = dateCharge;
    }

    public void setTypeCharges(String typeCharges) {
        this.typeCharges = typeCharges;
    }

    public void setPctRecuperable(double pctRecuperable) {
        this.pctRecuperable = pctRecuperable;
    }

    public void setIdBatiment(Long idBatiment) {
        this.idBatiment = idBatiment;
    }

    // MÉTHODES MÉTIER 
    
    /**
     * Calcule le montant récupérable auprès du locataire
     */
    public double getMontantRecuperable() {
        if (montant == null) return 0;
        return montant * (pctRecuperable / 100.0);
    }

    /**
     * Calcule le montant restant a  la charge du propriétaire
     */
    public double getMontantProprietaire() {
        if (montant == null) return 0;
        return montant - getMontantRecuperable();
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCharge);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Charges)) return false;
        Charges other = (Charges) obj;
        return idCharge == other.idCharge;
    }

    @Override
    public String toString() {
        return nature + " - " + montant + "€";
    }
}
