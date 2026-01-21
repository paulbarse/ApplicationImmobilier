package modele;

import java.util.Date;

public class Diagnostique {
    private int idDiagnostique;
    private String typeDiag;
    private Date dateEmission;
    private Date dateExpiration;
    private String reference;

    // Constructeur
    public Diagnostique(int idDiagnostique, String typeDiag, Date dateEmission,
        Date dateExpiration, String reference) {
        this.idDiagnostique = idDiagnostique;
        this.typeDiag = typeDiag;
        this.dateEmission = dateEmission;
        this.dateExpiration = dateExpiration;
        this.reference = reference;
    }

    // Getters et setters
    public int getIdDiagnostique() {
        return idDiagnostique;
    }

    public void setIdDiagnostique(int idDiagnostique) {
        this.idDiagnostique = idDiagnostique;
    }

    public String getTypeDiag() {
        return typeDiag;
    }

    public void setTypeDiag(String typeDiag) {
        this.typeDiag = typeDiag;
    }

    public Date getDateEmission() {
        return dateEmission;
    }

    public void setDateEmission(Date dateEmission) {
        this.dateEmission = dateEmission;
    }

    public Date getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Date dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}