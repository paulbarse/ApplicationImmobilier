package modele;

import java.util.Objects;

/**
 * Classe Garant - Compatible avec la table GARANT
 */
public class Garant {
    private String idGarant;           // VARCHAR2(50) 
    private String nomGarant;          // NOT NULL
    private String prenomGarant;       // Nullable
    private String adresseGarant;      // Nullable
    private String telGarant;          // Nullable
    private String mailGarant;         // Nullable

    // Constructeur complet
    public Garant(String idGarant, String nomGarant, String prenomGarant,
                  String adresseGarant, String telGarant, String mailGarant) {
        this.idGarant = idGarant;
        this.nomGarant = nomGarant;
        this.prenomGarant = prenomGarant;
        this.adresseGarant = adresseGarant;
        this.telGarant = telGarant;
        this.mailGarant = mailGarant;
    }

    // Constructeur simplifié
    public Garant(String idGarant, String nomGarant, String prenomGarant) {
        this(idGarant, nomGarant, prenomGarant, null, null, null);
    }

    // GETTERS 
    public String getIdGarant() {
        return idGarant;
    }

    public String getNomGarant() {
        return nomGarant;
    }

    public String getPrenomGarant() {
        return prenomGarant;
    }

    public String getAdresseGarant() {
        return adresseGarant;
    }

    public String getTelGarant() {
        return telGarant;
    }

    public String getMailGarant() {
        return mailGarant;
    }

    // SETTERS 
    public void setIdGarant(String idGarant) {
        this.idGarant = idGarant;
    }

    public void setNomGarant(String nomGarant) {
        this.nomGarant = nomGarant;
    }

    public void setPrenomGarant(String prenomGarant) {
        this.prenomGarant = prenomGarant;
    }

    public void setAdresseGarant(String adresseGarant) {
        this.adresseGarant = adresseGarant;
    }

    public void setTelGarant(String telGarant) {
        this.telGarant = telGarant;
    }

    public void setMailGarant(String mailGarant) {
        this.mailGarant = mailGarant;
    }

    // MÉTHODES UTILITAIRES 
    
    public String getNomComplet() {
        if (prenomGarant != null) {
            return prenomGarant + " " + nomGarant;
        }
        return nomGarant;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idGarant);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Garant)) return false;
        Garant other = (Garant) obj;
        return Objects.equals(idGarant, other.idGarant);
    }

    @Override
    public String toString() {
        return getNomComplet();
    }
}
