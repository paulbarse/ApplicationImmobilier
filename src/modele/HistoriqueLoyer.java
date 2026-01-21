package modele;

import java.util.Date;

/**
 * Modele pour l'historique des loyers des baux clotures.
 */
public class HistoriqueLoyer {

    private long idHistorique;
    private long idBail;
    private Long idLogement;
    private Long idGarage;
    private String nomBien;
    private String nomLocataire;
    private String mois;
    private int annee;
    private double montantLoyer;
    private double montantProvision;
    private Date datePaiement;
    private boolean quittanceGeneree;
    private Date dateCloture;
    private Long idBatiment;

    // Constructeur par defaut
    public HistoriqueLoyer() {
    }

    // Constructeur complet
    public HistoriqueLoyer(long idHistorique, long idBail, Long idLogement, Long idGarage,
                           String nomBien, String nomLocataire, String mois, int annee,
                           double montantLoyer, double montantProvision, Date datePaiement,
                           boolean quittanceGeneree, Date dateCloture, Long idBatiment) {
        this.idHistorique = idHistorique;
        this.idBail = idBail;
        this.idLogement = idLogement;
        this.idGarage = idGarage;
        this.nomBien = nomBien;
        this.nomLocataire = nomLocataire;
        this.mois = mois;
        this.annee = annee;
        this.montantLoyer = montantLoyer;
        this.montantProvision = montantProvision;
        this.datePaiement = datePaiement;
        this.quittanceGeneree = quittanceGeneree;
        this.dateCloture = dateCloture;
        this.idBatiment = idBatiment;
    }

    // GETTERS

    public long getIdHistorique() {
        return idHistorique;
    }

    public long getIdBail() {
        return idBail;
    }

    public Long getIdLogement() {
        return idLogement;
    }

    public Long getIdGarage() {
        return idGarage;
    }

    public String getNomBien() {
        return nomBien;
    }

    public String getNomLocataire() {
        return nomLocataire;
    }

    public String getMois() {
        return mois;
    }

    public int getAnnee() {
        return annee;
    }

    public double getMontantLoyer() {
        return montantLoyer;
    }

    public double getMontantProvision() {
        return montantProvision;
    }

    public Date getDatePaiement() {
        return datePaiement;
    }

    public boolean isQuittanceGeneree() {
        return quittanceGeneree;
    }

    public Date getDateCloture() {
        return dateCloture;
    }

    public Long getIdBatiment() {
        return idBatiment;
    }

    // SETTERS 
    
    public void setIdHistorique(long idHistorique) {
        this.idHistorique = idHistorique;
    }

    public void setIdBail(long idBail) {
        this.idBail = idBail;
    }

    public void setIdLogement(Long idLogement) {
        this.idLogement = idLogement;
    }

    public void setIdGarage(Long idGarage) {
        this.idGarage = idGarage;
    }

    public void setNomBien(String nomBien) {
        this.nomBien = nomBien;
    }

    public void setNomLocataire(String nomLocataire) {
        this.nomLocataire = nomLocataire;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public void setMontantLoyer(double montantLoyer) {
        this.montantLoyer = montantLoyer;
    }

    public void setMontantProvision(double montantProvision) {
        this.montantProvision = montantProvision;
    }

    public void setDatePaiement(Date datePaiement) {
        this.datePaiement = datePaiement;
    }

    public void setQuittanceGeneree(boolean quittanceGeneree) {
        this.quittanceGeneree = quittanceGeneree;
    }

    public void setDateCloture(Date dateCloture) {
        this.dateCloture = dateCloture;
    }

    public void setIdBatiment(Long idBatiment) {
        this.idBatiment = idBatiment;
    }

    //  METHODES UTILITAIRES

    /**
     * Retourne le total (loyer + provision)
     */
    public double getTotal() {
        return montantLoyer + montantProvision;
    }

    @Override
    public String toString() {
        return "HistoriqueLoyer{" +
                "idBail=" + idBail +
                ", nomBien='" + nomBien + '\'' +
                ", mois='" + mois + '\'' +
                ", loyer=" + montantLoyer +
                ", quittance=" + (quittanceGeneree ? "Oui" : "Non") +
                '}';
    }
}