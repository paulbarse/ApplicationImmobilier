package modele;

import java.util.Objects;

/**
 * Classe Proprietaire - Compatible avec la table PROPRIETAIRE
 */
public class Proprietaire {
    private long idProprietaire;       // NUMBER(22)
    private String nomProp;            // NOT NULL
    private String prenomProp;         // NOT NULL
    private String adresseProp;        // Nullable
    private String telProp;            // Nullable
    private String mailProp;           // Nullable
    private Long idBatiment;           // FK NOT NULL

    // Constructeur complet
    public Proprietaire(long idProprietaire, String nomProp, String prenomProp,
                        String adresseProp, String telProp, String mailProp,
                        Long idBatiment) {
        this.idProprietaire = idProprietaire;
        this.nomProp = nomProp;
        this.prenomProp = prenomProp;
        this.adresseProp = adresseProp;
        this.telProp = telProp;
        this.mailProp = mailProp;
        this.idBatiment = idBatiment;
    }


    public Proprietaire(String idProprietaire, String nomProp, String prenomProp,
                        String adresseProprio, String telProprio, String mailProprio,
                        String idDeclaration) {
        this.idProprietaire = Long.parseLong(idProprietaire);
        this.nomProp = nomProp;
        this.prenomProp = prenomProp;
        this.adresseProp = adresseProprio;
        this.telProp = telProprio;
        this.mailProp = mailProprio;

    }

    // GETTERS
    public long getIdProprietaireLong() {
        return idProprietaire;
    }
    
    public String getIdProprietaire() {
        return String.valueOf(idProprietaire);
    }

    public String getNomProp() {
        return nomProp;
    }

    public String getPrenomProp() {
        return prenomProp;
    }

    public String getAdresseProp() {
        return adresseProp;
    }
    

    public String getAdresseProprio() {
        return adresseProp;
    }

    public String getTelProp() {
        return telProp;
    }
    

    public String getTelProprio() {
        return telProp;
    }

    public String getMailProp() {
        return mailProp;
    }
    

    public String getMailProprio() {
        return mailProp;
    }

    public Long getIdBatiment() {
        return idBatiment;
    }
    


    // SETTERS 
    public void setIdProprietaire(long idProprietaire) {
        this.idProprietaire = idProprietaire;
    }

    public void setNomProp(String nomProp) {
        this.nomProp = nomProp;
    }

    public void setPrenomProp(String prenomProp) {
        this.prenomProp = prenomProp;
    }

    public void setAdresseProp(String adresseProp) {
        this.adresseProp = adresseProp;
    }

    public void setTelProp(String telProp) {
        this.telProp = telProp;
    }

    public void setMailProp(String mailProp) {
        this.mailProp = mailProp;
    }

    public void setIdBatiment(Long idBatiment) {
        this.idBatiment = idBatiment;
    }

    // MÉTHODES UTILITAIRES
    
    public String getNomComplet() {
        return prenomProp + " " + nomProp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProprietaire);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Proprietaire)) return false;
        Proprietaire other = (Proprietaire) obj;
        return idProprietaire == other.idProprietaire;
    }

    @Override
    public String toString() {
        return getNomComplet();
    }
}
