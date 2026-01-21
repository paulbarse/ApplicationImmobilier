package modele;

import java.util.Objects;

/**
 * Classe Logement - Compatible avec la table LOGEMENT
 */
public class Logement {
    private long idLogement;           // NUMBER(22)
    private String typeLogement;       // Nullable
    private String adresseLogement;    // Nullable
    private String adresseComplementaire;    // Nullable
    private long numFiscal;            // NOT NULL
    private double surface;            // NOT NULL (NUMBER(10,2))
    private int nbPieces;              // NOT NULL
    
    // Cles etrangeres (IDs)
    private Long idReleve;             // FK nullable
    private Long idBatiment;           // FK NOT NULL
    private Long idBail;               // FK nullable
    
    // Relations (objets)
    private ReleveCompteur releve;
    private Batiment batiment;
    private Bail bail;

    // Constructeur complet avec FK
    public Logement(long idLogement, String typeLogement, String adresseLogement,
                    String adresseComplementaire, long numFiscal, double surface, int nbPieces,
                    Long idReleve, Long idBatiment, Long idBail) {
        this.idLogement = idLogement;
        this.typeLogement = typeLogement;
        this.adresseLogement = adresseLogement;
        this.adresseComplementaire = adresseComplementaire;
        this.numFiscal = numFiscal;
        this.surface = surface;
        this.nbPieces = nbPieces;
        this.idReleve = idReleve;
        this.idBatiment = idBatiment;
        this.idBail = idBail;
    }

    // Constructeur simplifie (sans FK)
    public Logement(long idLogement, String typeLogement, String adresseLogement,
            String adresseComplementaire, long numFiscal, double surface, int nbPieces) {
        this(idLogement, typeLogement, adresseLogement, adresseComplementaire, numFiscal, surface, nbPieces, 
             null, null, null);
    }

    // Constructeur de compatibilite avec ancien code (String pour surface)
    public Logement(String idLogement, String typeLogement, String adresseLogement,
                    int numFiscal, String surface, int nbPieces, String assurance) {
        this.idLogement = Long.parseLong(idLogement);
        this.typeLogement = typeLogement;
        this.adresseLogement = adresseLogement;
        this.adresseComplementaire = adresseLogement;
        this.numFiscal = numFiscal;
        this.surface = Double.parseDouble(surface.replace(",", "."));
 
    }

    // GETTERS 
    public long getIdLogementLong() {
        return idLogement;
    }
    

    public String getIdLogement() {
        return String.valueOf(idLogement);
    }

    public String getTypeLogement() {
        return typeLogement;
    }

    public String getAdresseLogement() {
        return adresseLogement;
    }
    
    public String getAdresseComplementaire() {
        return adresseComplementaire;
    }

    public long getNumFiscalLong() {
        return numFiscal;
    }
    

    public String getNumFiscal() {
        return String.valueOf(numFiscal);
    }

    public double getSurfaceDouble() {
        return surface;
    }
    

    public String getSurface() {
        return String.valueOf(surface);
    }

    public int getNbPieces() {
        return nbPieces;
    }

    public Long getIdReleve() {
        return idReleve;
    }

    public Long getIdBatiment() {
        return idBatiment;
    }

    public Long getIdBail() {
        return idBail;
    }

    public ReleveCompteur getReleve() {
        return releve;
    }

    public Batiment getBatiment() {
        return batiment;
    }

    public Bail getBail() {
        return bail;
    }
    


    // SETTERS
    public void setIdLogement(long idLogement) {
        this.idLogement = idLogement;
    }

    public void setTypeLogement(String typeLogement) {
        this.typeLogement = typeLogement;
    }

    public void setAdresseLogement(String adresseLogement) {
        this.adresseLogement = adresseLogement;
    }
    
    public void setAdresseComplementaire(String adresseComplementaire) {
        this.adresseComplementaire = adresseComplementaire;
    }

    public void setNumFiscal(long numFiscal) {
        this.numFiscal = numFiscal;
    }

    public void setSurface(double surface) {
        this.surface = surface;
    }
    
    public void setSurface(String surface) {
        this.surface = Double.parseDouble(surface.replace(",", "."));
    }

    public void setNbPieces(int nbPieces) {
        this.nbPieces = nbPieces;
    }

    public void setIdReleve(Long idReleve) {
        this.idReleve = idReleve;
    }

    public void setIdBatiment(Long idBatiment) {
        this.idBatiment = idBatiment;
    }

    public void setIdBail(Long idBail) {
        this.idBail = idBail;
    }

    public void setReleve(ReleveCompteur releve) {
        this.releve = releve;
    }

    public void setBatiment(Batiment batiment) {
        this.batiment = batiment;
    }

    public void setBail(Bail bail) {
        this.bail = bail;
    }

    // ===== METHODES UTILITAIRES =====
    
    /**
     * Verifie si le logement est loue (a un bail actif)
     */
    public boolean isLoue() {
        return idBail != null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLogement);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Logement)) return false;
        Logement other = (Logement) obj;
        return idLogement == other.idLogement;
    }

    @Override
    public String toString() {
        return adresseLogement != null ? adresseLogement : "Logement #" + idLogement;
    }
}