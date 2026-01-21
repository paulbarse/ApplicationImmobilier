package modele;

import java.util.Objects;

/**
 * Classe Garage - Compatible avec la table GARAGE
 */
public class Garage {
    private long idGarage;              // NUMBER(22)
    private Integer surfaceGarage;      // Nullable
    private long numFiscal;             // NOT NULL
    private String adresseGarage;       // Nullable
    
    // Clés étrangères
    private Long idBatiment;            // FK NOT NULL
    private Long idLogement;            // FK nullable (garage lié Ã  un logement)
    private Long idBail;                // FK nullable

    // Constructeur complet
    public Garage(long idGarage, Integer surfaceGarage, long numFiscal,
                  String adresseGarage, Long idBatiment, Long idLogement, Long idBail) {
        this.idGarage = idGarage;
        this.surfaceGarage = surfaceGarage;
        this.numFiscal = numFiscal;
        this.adresseGarage = adresseGarage;
        this.idBatiment = idBatiment;
        this.idLogement = idLogement;
        this.idBail = idBail;
    }

    // Constructeur simplifié (sans FK optionnelles)
    public Garage(long idGarage, Integer surfaceGarage, long numFiscal,
                  String adresseGarage, Long idBatiment) {
        this(idGarage, surfaceGarage, numFiscal, adresseGarage, idBatiment, null, null);
    }

    // Constructeur de compatibilité avec ancien code
    public Garage(int idGarage, int surfaceGarage, int numFiscal,
                  String assurance, String adresseGarage) {
        this.idGarage = idGarage;
        this.surfaceGarage = surfaceGarage;
        this.numFiscal = numFiscal;
        this.adresseGarage = adresseGarage;
        // assurance ignoré car n'existe plus en BDD
    }

    //  GETTERS 
    public long getIdGarageLong() {
        return idGarage;
    }
    
    // Compatibilité ancien code
    public int getIdGarage() {
        return (int) idGarage;
    }

    public Integer getSurfaceGarage() {
        return surfaceGarage;
    }

    public long getNumFiscalLong() {
        return numFiscal;
    }
    
    // Compatibilité ancien code
    public int getNumFiscal() {
        return (int) numFiscal;
    }

    public String getAdresseGarage() {
        return adresseGarage;
    }

    public Long getIdBatiment() {
        return idBatiment;
    }

    public Long getIdLogement() {
        return idLogement;
    }

    public Long getIdBail() {
        return idBail;
    }
    


    // SETTERS
    public void setIdGarage(long idGarage) {
        this.idGarage = idGarage;
    }

    public void setSurfaceGarage(Integer surfaceGarage) {
        this.surfaceGarage = surfaceGarage;
    }

    public void setNumFiscal(long numFiscal) {
        this.numFiscal = numFiscal;
    }

    public void setAdresseGarage(String adresseGarage) {
        this.adresseGarage = adresseGarage;
    }

    public void setIdBatiment(Long idBatiment) {
        this.idBatiment = idBatiment;
    }

    public void setIdLogement(Long idLogement) {
        this.idLogement = idLogement;
    }

    public void setIdBail(Long idBail) {
        this.idBail = idBail;
    }

    // MÉTHODES UTILITAIRES
    
    /**
     * Vérifie si le garage est loué
     */
    public boolean isLoue() {
        return idBail != null;
    }

    /**
     * Vérifie si le garage est lié a  un logement
     */
    public boolean isLieALogement() {
        return idLogement != null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idGarage);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Garage)) return false;
        Garage other = (Garage) obj;
        return idGarage == other.idGarage;
    }

    @Override
    public String toString() {
        return adresseGarage != null ? adresseGarage : "Garage #" + idGarage;
    }
    
    
}
