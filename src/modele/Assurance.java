package modele;

import java.util.Date;
import java.util.Objects;

/**
 * Classe Assurance - Compatible avec la table ASSURANCE
 */
public class Assurance {

    private long numAssurance;          // NUMASSURANCE PK
    private String type;                // TYPE (PROPRIETAIRE, PNO, GLI, etc.)
    private Double primeBase;           // PRIMEBASE
    private String nomCompagnie;        // NOM_COMPAGNIE
    private Date dateEffet;             // DATE_EFFET
    
    // Clés étrangères (une seule doit être renseignée)
    private Long idLogement;            // ID_LOGEMENT (FK)
    private Long idGarage;              // ID_GARAGE (FK)
    private Long idBatiment;            // ID_BATIMENT (FK) - NOUVEAU
    private Long siret;                 // SIRET (FK) - Entreprise/compagnie
    
    // Données d'échéance (jointure ECHEANCE)
    private Date dateEcheance;          // DATE_ de la table ECHEANCE
    private Double montantEcheance;     // MONTANT de la table ECHEANCE

    /**
     * Constructeur complet
     */
    public Assurance(long numAssurance, String type, Double primeBase, String nomCompagnie,
                     Date dateEffet, Long idLogement, Long idGarage, Long idBatiment, Long siret) {
        this.numAssurance = numAssurance;
        this.type = type;
        this.primeBase = primeBase;
        this.nomCompagnie = nomCompagnie;
        this.dateEffet = dateEffet;
        this.idLogement = idLogement;
        this.idGarage = idGarage;
        this.idBatiment = idBatiment;
        this.siret = siret;
    }

    /**
     * Constructeur simplifié (sans bâtiment - rétrocompatibilité)
     */
    public Assurance(long numAssurance, String type, Double primeBase, String nomCompagnie,
                     Date dateEffet, Long idLogement, Long idGarage, Long siret) {
        this(numAssurance, type, primeBase, nomCompagnie, dateEffet, idLogement, idGarage, null, siret);
    }

    /**
     * Factory pour assurance logement
     */
    public static Assurance assuranceLogement(long numAssurance, long idLogement,
            String nomCompagnie, String type, double primeBase, Date dateEffet) {
        return new Assurance(numAssurance, type, primeBase, nomCompagnie, dateEffet, 
                idLogement, null, null, null);
    }

    /**
     * Factory pour assurance garage
     */
    public static Assurance assuranceGarage(long numAssurance, long idGarage,
            String nomCompagnie, String type, double primeBase, Date dateEffet) {
        return new Assurance(numAssurance, type, primeBase, nomCompagnie, dateEffet, 
                null, idGarage, null, null);
    }

    /**
     * Factory pour assurance bâtiment entier
     */
    public static Assurance assuranceBatiment(long numAssurance, long idBatiment,
            String nomCompagnie, String type, double primeBase, Date dateEffet) {
        return new Assurance(numAssurance, type, primeBase, nomCompagnie, dateEffet, 
                null, null, idBatiment, null);
    }

    // GETTERS 
    public long getNumAssurance() { return numAssurance; }
    public String getNumAssuranceStr() { return String.valueOf(numAssurance); }
    public String getType() { return type; }
    public Double getPrimeBase() { return primeBase; }
    public String getNomCompagnie() { return nomCompagnie; }
    public Date getDateEffet() { return dateEffet; }
    public Long getIdLogement() { return idLogement; }
    public Long getIdGarage() { return idGarage; }
    public Long getIdBatiment() { return idBatiment; }
    public Long getSiret() { return siret; }
    public Date getDateEcheance() { return dateEcheance; }
    public Double getMontantEcheance() { return montantEcheance; }

    //  SETTERS 
    public void setNumAssurance(long numAssurance) { this.numAssurance = numAssurance; }
    public void setType(String type) { this.type = type; }
    public void setPrimeBase(Double primeBase) { this.primeBase = primeBase; }
    public void setNomCompagnie(String nomCompagnie) { this.nomCompagnie = nomCompagnie; }
    public void setDateEffet(Date dateEffet) { this.dateEffet = dateEffet; }
    public void setIdLogement(Long idLogement) { this.idLogement = idLogement; }
    public void setIdGarage(Long idGarage) { this.idGarage = idGarage; }
    public void setIdBatiment(Long idBatiment) { this.idBatiment = idBatiment; }
    public void setSiret(Long siret) { this.siret = siret; }
    public void setDateEcheance(Date dateEcheance) { this.dateEcheance = dateEcheance; }
    public void setMontantEcheance(Double montantEcheance) { this.montantEcheance = montantEcheance; }

    //  MÉTHODES UTILITAIRES 

    /**
     * Détermine le type de bien assuré
     */
    public enum TypeBien { LOGEMENT, GARAGE, BATIMENT, INCONNU }

    public TypeBien getTypeBienAssure() {
        if (idLogement != null && idLogement > 0) return TypeBien.LOGEMENT;
        if (idGarage != null && idGarage > 0) return TypeBien.GARAGE;
        if (idBatiment != null && idBatiment > 0) return TypeBien.BATIMENT;
        return TypeBien.INCONNU;
    }

    /**
     * Vérifie si l'assurance est pour un bâtiment entier
     */
    public boolean isAssuranceBatiment() {
        return idBatiment != null && idBatiment > 0;
    }

    /**
     * Vérifie si l'assurance est de type PNO
     */
    public boolean isPNO() {
        return type != null && type.equalsIgnoreCase("PNO");
    }

    /**
     * Vérifie si l'assurance est de type GLI
     */
    public boolean isGLI() {
        return type != null && type.equalsIgnoreCase("GLI");
    }

    /**
     * Retourne l'année de l'assurance (basée sur dateEffet)
     */
    public int getAnnee() {
        if (dateEffet == null) return 0;
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(dateEffet);
        return cal.get(java.util.Calendar.YEAR);
    }

    /**
     * Vérifie si l'assurance est expirée
     */
    public boolean isExpiree() {
        if (dateEcheance == null) return false;
        return dateEcheance.before(new Date());
    }

    /**
     * Vérifie si l'assurance expire dans les N jours
     */
    public boolean expireDans(int jours) {
        if (dateEcheance == null) return false;
        long diff = dateEcheance.getTime() - System.currentTimeMillis();
        long joursRestants = diff / (1000 * 60 * 60 * 24);
        return joursRestants >= 0 && joursRestants <= jours;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numAssurance);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Assurance)) return false;
        Assurance other = (Assurance) obj;
        return numAssurance == other.numAssurance;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Assurance #").append(numAssurance);
        if (nomCompagnie != null) {
            sb.append(" - ").append(nomCompagnie);
        }
        if (type != null) {
            sb.append(" (").append(type).append(")");
        }
        if (primeBase != null) {
            sb.append(" - ").append(String.format("%.2f€", primeBase));
        }
        return sb.toString();
    }
}
