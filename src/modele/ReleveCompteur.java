package modele;

import java.util.Date;
import java.util.Objects;

public class ReleveCompteur {
    private int idReleve;
    private String unite;
    private String type;
    private int nouvelIndex;
    private Date date;
    private int numCompteur;
    private int ancienIndex;

    // Constructeur
    public ReleveCompteur(int idReleve, String unite, String type,
        int nouvelIndex, Date date, int numCompteur, int ancienIndex) {
        this.idReleve = idReleve;
        this.unite = unite;
        this.type = type;
        this.nouvelIndex = nouvelIndex;
        this.date = date;
        this.numCompteur = numCompteur;
        this.ancienIndex = ancienIndex;
    }

    public int getIdReleve() {
        return idReleve;
    }

    public void setIdReleve(int idReleve) {
        this.idReleve = idReleve;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNouvelIndex() {
        return nouvelIndex;
    }

    public void setNouvelIndex(int nouvelIndex) {
        this.nouvelIndex = nouvelIndex;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNumCompteur() {
        return numCompteur;
    }

    public void setNumCompteur(int numCompteur) {
        this.numCompteur = numCompteur;
    }

    public int getAncienIndex() {
        return ancienIndex;
    }

    public void setAncienIndex(int ancienIndex) {
        this.ancienIndex = ancienIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ancienIndex, date, idReleve, nouvelIndex,
            numCompteur, type, unite);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ReleveCompteur)) {
            return false;
        }
        ReleveCompteur other = (ReleveCompteur) obj;
        return ancienIndex == other.ancienIndex
            && Objects.equals(date, other.date) && idReleve == other.idReleve
            && nouvelIndex == other.nouvelIndex
            && numCompteur == other.numCompteur
            && Objects.equals(type, other.type)
            && Objects.equals(unite, other.unite);
    }

}