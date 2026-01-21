package modele;

import java.util.Date;
import java.util.Objects;
import java.util.Collection;

/**
 * Classe Bail - Compatible avec la table BAIL
 */
public class Bail {
    
    // États possibles du bail (CHECK constraint)
    public static final String ETAT_EN_COURS = "EN_COURS";
    public static final String ETAT_RESILIE = "RESILIE";
    public static final String ETAT_CLOTURE = "CLOTURE";
    
    private long idBail;                    // NUMBER(22)
    private Date dateDebut;                 // NOT NULL
    private Date dateFin;                   // Nullable
    private double loyerInitial;            // NOT NULL
    private double provisionInitiales;      // DEFAULT 0 NOT NULL
    private Double soldeToutCompte;         // Nullable
    private Double caution;                 // Nullable
    private String etat;                    // DEFAULT 'EN_COURS' NOT NULL
    private Integer jourPaiement;           // Nullable
    
    // Relations (non en BDD directement)
    private Collection<Locataire> locataires;
    private Collection<Garant> garants;

    // Constructeur complet
    public Bail(long idBail, Date dateDebut, Date dateFin,
                double loyerInitial, double provisionInitiales, 
                Double soldeToutCompte, Double caution, String etat,
                Integer jourPaiement) {
        this.idBail = idBail;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.loyerInitial = loyerInitial;
        this.provisionInitiales = provisionInitiales;
        this.soldeToutCompte = soldeToutCompte;
        this.caution = caution;
        this.etat = (etat != null) ? etat : ETAT_EN_COURS;
        this.jourPaiement = jourPaiement;
    }

    // Constructeur simplifié (pour création)
    public Bail(long idBail, Date dateDebut, double loyerInitial, 
                double provisionInitiales, Double caution) {
        this(idBail, dateDebut, null, loyerInitial, provisionInitiales, 
             null, caution, ETAT_EN_COURS, null);
    }

    // Constructeur minimal
    public Bail(long idBail, Date dateDebut, Date dateFin,
                double loyerInitial, double provisionInitiales, 
                Double soldeToutCompte, Double caution) {
        this(idBail, dateDebut, dateFin, loyerInitial, provisionInitiales, 
             soldeToutCompte, caution, ETAT_EN_COURS, null);
    }

    // GETTERS 
    public long getIdBail() {
        return idBail;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public double getLoyerInitial() {
        return loyerInitial;
    }

    public double getProvisionInitiales() {
        return provisionInitiales;
    }

    public Double getSoldeToutCompte() {
        return soldeToutCompte;
    }

    public Double getCaution() {
        return caution;
    }

    public String getEtat() {
        return etat;
    }

    public Integer getJourPaiement() {
        return jourPaiement;
    }

    public Collection<Locataire> getLocataires() {
        return locataires;
    }

    public Collection<Garant> getGarants() {
        return garants;
    }

    //  SETTERS 
    public void setIdBail(long idBail) {
        this.idBail = idBail;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public void setLoyerInitial(double loyerInitial) {
        if (loyerInitial < 0) {
            throw new IllegalArgumentException("Le loyer ne peut pas être négatif");
        }
        this.loyerInitial = loyerInitial;
    }

    public void setProvisionInitiales(double provisionInitiales) {
        this.provisionInitiales = provisionInitiales;
    }

    public void setSoldeToutCompte(Double soldeToutCompte) {
        this.soldeToutCompte = soldeToutCompte;
    }

    public void setCaution(Double caution) {
        this.caution = caution;
    }

    public void setEtat(String etat) {
        // Vérification du CHECK constraint
        if (!ETAT_EN_COURS.equals(etat) && !ETAT_RESILIE.equals(etat) && !ETAT_CLOTURE.equals(etat)) {
            throw new IllegalArgumentException("État invalide: " + etat);
        }
        this.etat = etat;
    }

    public void setJourPaiement(Integer jourPaiement) {
        this.jourPaiement = jourPaiement;
    }

    public void setLocataires(Collection<Locataire> locataires) {
        this.locataires = locataires;
    }

    public void setGarants(Collection<Garant> garants) {
        this.garants = garants;
    }

    //  MÉTHODES MÉTIER 
    
    /**
     * Vérifie si le bail est actif
     */
    public boolean isActif() {
        return ETAT_EN_COURS.equals(etat);
    }

    /**
     * Vérifie si le bail est terminé
     */
    public boolean isTermine() {
        return ETAT_CLOTURE.equals(etat) || ETAT_RESILIE.equals(etat);
    }

    /**
     * Calcule le loyer total (loyer + provisions)
     */
    public double getLoyerTotal() {
        return loyerInitial + provisionInitiales;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idBail);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Bail)) return false;
        Bail other = (Bail) obj;
        return idBail == other.idBail;
    }

    @Override
    public String toString() {
        return "Bail #" + idBail + " [" + etat + "]";
    }
}
