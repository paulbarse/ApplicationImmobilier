package modele;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Classe Locataire - Compatible avec la table LOCATAIRE
 */
public class Locataire {
    private long idLocataire;      // NUMBER(22) -> long
    private String nomLoc;         // NOT NULL
    private String prenomLoc;      // NOT NULL
    private String telLoc;
    private String mailLoc;
    private LocalDate dateNaissance;  // Remplace adresseLoc

    // Constructeur complet
    public Locataire(long idLocataire, String nomLoc, String prenomLoc,
                     String telLoc, String mailLoc, LocalDate dateNaissance) {
        this.idLocataire = idLocataire;
        this.nomLoc = nomLoc;
        this.prenomLoc = prenomLoc;
        this.telLoc = telLoc;
        this.mailLoc = mailLoc;
        this.dateNaissance = dateNaissance;
    }

    // Constructeur sans ID (pour création)
    public Locataire(String nomLoc, String prenomLoc, String telLoc, 
                     String mailLoc, LocalDate dateNaissance) {
        this(0, nomLoc, prenomLoc, telLoc, mailLoc, dateNaissance);
    }

    // GETTERS
    public long getIdLocataire() {
        return idLocataire;
    }

    public String getNomLoc() {
        return nomLoc;
    }

    public String getPrenomLoc() {
        return prenomLoc;
    }

    public String getTelLoc() {
        return telLoc;
    }

    public String getMailLoc() {
        return mailLoc;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    // SETTERS 
    public void setIdLocataire(long idLocataire) {
        this.idLocataire = idLocataire;
    }

    public void setNomLoc(String nomLoc) {
        this.nomLoc = nomLoc;
    }

    public void setPrenomLoc(String prenomLoc) {
        this.prenomLoc = prenomLoc;
    }

    public void setTelLoc(String telLoc) {
        this.telLoc = telLoc;
    }

    public void setMailLoc(String mailLoc) {
        this.mailLoc = mailLoc;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    // UTILITAIRES 
    public String getNomComplet() {
        return prenomLoc + " " + nomLoc;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLocataire);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Locataire)) return false;
        Locataire other = (Locataire) obj;
        return idLocataire == other.idLocataire;
    }

    @Override
    public String toString() {
        return getNomComplet();
    }
}