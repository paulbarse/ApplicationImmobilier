package modele;

import java.util.Collection;
import java.util.Objects;

/**
 * Classe Batiment - Compatible avec la table BATIMENT
 */
public class Batiment {

    //  ATTRIBUTS 
    private long idBatiment;           // NUMBER(22)
    private String adresse;            // NOT NULL
    private Integer nombreEtage;       // Nullable
    private String typeBatiment;       // Nullable
    private String ville;              // Nullable (colonne RUE en BDD, contient la ville)
    private int codePostal;            // NOT NULL

    // Relations
    private Collection<Logement> logements;
    private Collection<Garage> garages;

    // CONSTRUCTEURS 

    /**
     * Constructeur principal (utilise par le DAO)
     */
    public Batiment(long idBatiment, String adresse, Integer nombreEtage,
                    String typeBatiment, String ville, int codePostal) {
        this.idBatiment = idBatiment;
        this.adresse = adresse;
        this.nombreEtage = nombreEtage;
        this.typeBatiment = typeBatiment;
        this.ville = ville;
        this.codePostal = codePostal;
    }

    /**
     * Constructeur compatible String 
     */
    public Batiment(String idBatiment, String adresse, int nombreEtage,
                    String typeBatiment, String ville, int codePostal) {
        this(
            (idBatiment == null || idBatiment.isBlank()) ? 0L : Long.parseLong(idBatiment),
            adresse,
            nombreEtage,
            typeBatiment,
            ville,
            codePostal
        );
    }

    //  GETTERS

    public long getIdBatimentLong() {
        return idBatiment;
    }

    /**
     * Compatibilite avec ancien code utilisant String
     */
    public String getIdBatiment() {
        return String.valueOf(idBatiment);
    }

    public String getAdresse() {
        return adresse;
    }

    public Integer getNombreEtage() {
        return nombreEtage;
    }

    public String getTypeBatiment() {
        return typeBatiment;
    }

    /**
     * Retourne la ville du batiment
     */
    public String getVille() {
        return ville;
    }
    


    public String getRue() {
        return ville;
    }

    public int getCodePostal() {
        return codePostal;
    }

    public Collection<Logement> getLogements() {
        return logements;
    }

    public Collection<Garage> getGarages() {
        return garages;
    }

    // SETTERS 

    public void setIdBatiment(long idBatiment) {
        this.idBatiment = idBatiment;
    }

    /**
     * Setter String securise
     */
    public void setIdBatiment(String idBatiment) {
        this.idBatiment = (idBatiment == null || idBatiment.isBlank())
                ? 0L
                : Long.parseLong(idBatiment);
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setNombreEtage(Integer nombreEtage) {
        this.nombreEtage = nombreEtage;
    }

    public void setTypeBatiment(String typeBatiment) {
        this.typeBatiment = typeBatiment;
    }

    /**
     * Definit la ville du batiment
     */
    public void setVille(String ville) {
        this.ville = ville;
    }
    


    public void setRue(String rue) {
        this.ville = rue;
    }

    public void setCodePostal(int codePostal) {
        this.codePostal = codePostal;
    }

    public void setLogements(Collection<Logement> logements) {
        this.logements = logements;
    }

    public void setGarages(Collection<Garage> garages) {
        this.garages = garages;
    }

    // METHODES UTILITAIRES

    /**
     * Adresse lisible pour l'IHM
     */
    public String getAdresseComplete() {
        return adresse + ", " + codePostal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idBatiment);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Batiment)) return false;
        Batiment other = (Batiment) obj;
        return idBatiment == other.idBatiment;
    }

    @Override
    public String toString() {
        return adresse + " (" + codePostal + ")";
    }
}