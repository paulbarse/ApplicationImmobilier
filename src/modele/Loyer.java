package modele;

import java.util.Date;
import java.util.Objects;

/**
 * Classe Loyer - Compatible avec la table LOYER
 */
public class Loyer {
    private long idLoyer;              // NUMBER(22)
    private String quittance;          // Nullable
    private double montantLoyer;       // NOT NULL
    private String mois;               // NOT NULL (format: "01/2024" ou "Janvier")
    private Double montantProvision;   // Nullable
    private String statut;             // Nullable ("Payé", "En attente", etc.)
    private Integer indiceIRL;         // Nullable
    private Integer ancienIRL;         // Nullable
    private Date datePaiement;         // Nullable
    private long idBail;               // FK NOT NULL

    // Constructeur complet
    public Loyer(long idLoyer, String quittance, double montantLoyer, String mois,
                 Double montantProvision, String statut, Integer indiceIRL,
                 Integer ancienIRL, Date datePaiement, long idBail) {
        this.idLoyer = idLoyer;
        this.quittance = quittance;
        this.montantLoyer = montantLoyer;
        this.mois = mois;
        this.montantProvision = montantProvision;
        this.statut = statut;
        this.indiceIRL = indiceIRL;
        this.ancienIRL = ancienIRL;
        this.datePaiement = datePaiement;
        this.idBail = idBail;
    }

    // Constructeur simplifié
    public Loyer(long idLoyer, double montantLoyer, String mois, 
                 Double montantProvision, String statut, long idBail) {
        this(idLoyer, null, montantLoyer, mois, montantProvision, statut, 
             null, null, null, idBail);
    }

    // Constructeur 
    public Loyer(String idLoyer, String mois, int annee, double montantLoyer,
                 double provision, String statut, String idBail) {
        this.idLoyer = Long.parseLong(idLoyer);
        this.mois = mois + "/" + annee;
        this.montantLoyer = montantLoyer;
        this.montantProvision = provision;
        this.statut = statut;
        this.idBail = Long.parseLong(idBail);
    }

    // GETTERS 
    public long getIdLoyerLong() {
        return idLoyer;
    }
    
    public String getIdLoyer() {
        return String.valueOf(idLoyer);
    }

    public String getQuittance() {
        return quittance;
    }

    public double getMontantLoyer() {
        return montantLoyer;
    }

    public String getMois() {
        return mois;
    }

    public Double getMontantProvision() {
        return montantProvision;
    }
    

    public double getProvision() {
        return montantProvision != null ? montantProvision : 0;
    }

    public String getStatut() {
        return statut;
    }

    public Integer getIndiceIRL() {
        return indiceIRL;
    }

    public Integer getAncienIRL() {
        return ancienIRL;
    }

    public Date getDatePaiement() {
        return datePaiement;
    }

    public long getIdBailLong() {
        return idBail;
    }
    
    public String getIdBail() {
        return String.valueOf(idBail);
    }
    
    // extrait l'année du mois
    public int getAnnee() {
        if (mois != null && mois.contains("/")) {
            String[] parts = mois.split("/");
            if (parts.length > 1) {
                try {
                    return Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }

    // SETTERS 
    public void setIdLoyer(long idLoyer) {
        this.idLoyer = idLoyer;
    }

    public void setQuittance(String quittance) {
        this.quittance = quittance;
    }

    public void setMontantLoyer(double montantLoyer) {
        this.montantLoyer = montantLoyer;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public void setMontantProvision(Double montantProvision) {
        this.montantProvision = montantProvision;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setIndiceIRL(Integer indiceIRL) {
        this.indiceIRL = indiceIRL;
    }

    public void setAncienIRL(Integer ancienIRL) {
        this.ancienIRL = ancienIRL;
    }

    public void setDatePaiement(Date datePaiement) {
        this.datePaiement = datePaiement;
    }

    public void setIdBail(long idBail) {
        this.idBail = idBail;
    }

    // ===== MÉTHODES MÉTIER =====
    
    /**
     * Calcule le montant total (loyer + provisions)
     */
    public double getMontantTotal() {
        return montantLoyer + (montantProvision != null ? montantProvision : 0);
    }

    /**
     * Vérifie si le loyer est payé
     */
    public boolean isPaye() {
        return "Payé".equalsIgnoreCase(statut) || "Paye".equalsIgnoreCase(statut);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLoyer);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Loyer)) return false;
        Loyer other = (Loyer) obj;
        return idLoyer == other.idLoyer;
    }

    @Override
    public String toString() {
        return "Loyer " + mois + " : " + montantLoyer + "€";
    }
}
