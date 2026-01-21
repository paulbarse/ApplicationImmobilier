package modele;

import java.util.Objects;

/**
 * Classe Entreprise - Compatible avec la table ENTREPRISE
 */
public class Entreprise {
    private long siret;              // NUMBER(14) PRIMARY KEY
    private String nomEntreprise;    // NOT NULL
    private String adresse;          // Nullable
    private String telephone;        // Nullable
    private String email;            // Nullable
    private String specialite;       // Nullable

    // Constructeur complet
    public Entreprise(long siret, String nomEntreprise, String adresse, 
                      String telephone, String email, String specialite) {
        this.siret = siret;
        this.nomEntreprise = nomEntreprise;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.specialite = specialite;
    }

    // Constructeur simplifié
    public Entreprise(long siret, String nomEntreprise) {
        this(siret, nomEntreprise, null, null, null, null);
    }

    // GETTERS 
    public long getSiret() {
        return siret;
    }

    public String getSiretFormate() {
        String s = String.valueOf(siret);
        if (s.length() == 14) {
            return s.substring(0, 3) + " " + s.substring(3, 6) + " " + 
                   s.substring(6, 9) + " " + s.substring(9, 14);
        }
        return s;
    }

    public String getNomEntreprise() {
        return nomEntreprise;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }

    public String getSpecialite() {
        return specialite;
    }

    // SETTERS 
    public void setSiret(long siret) {
        this.siret = siret;
    }

    public void setNomEntreprise(String nomEntreprise) {
        this.nomEntreprise = nomEntreprise;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    // MÉTHODES UTILITAIRES 
    @Override
    public int hashCode() {
        return Objects.hash(siret);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Entreprise)) return false;
        Entreprise other = (Entreprise) obj;
        return siret == other.siret;
    }

    @Override
    public String toString() {
        return nomEntreprise + (specialite != null ? " (" + specialite + ")" : "");
    }
}