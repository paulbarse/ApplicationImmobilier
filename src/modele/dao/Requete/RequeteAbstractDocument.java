package modele.dao.Requete;

import java.sql.*;
import java.util.Date;

/**
 * Requêtes SQL pour AbstractDocumentDao
 */
public class RequeteAbstractDocument {


    // ASSURANCES

    
    public static String reqUpdateAssurance() {
        return "UPDATE ASSURANCE SET nom_compagnie = ?, type = ?, primebase = ?, date_effet = ? " +
               "WHERE numassurance = ?";
    }
    
    public static void paramUpdateAssurance(PreparedStatement ps, long numAssurance, String nomCompagnie, 
            String type, double primeBase, Date dateEffet) throws SQLException {
        ps.setString(1, nomCompagnie);
        ps.setString(2, type);
        ps.setDouble(3, primeBase);
        ps.setDate(4, new java.sql.Date(dateEffet.getTime()));
        ps.setLong(5, numAssurance);
    }
    
    public static String reqDeleteEcheance() {
        return "DELETE FROM ECHEANCE WHERE numassurance = ?";
    }
    
    public static void paramDeleteEcheance(PreparedStatement ps, long numAssurance) throws SQLException {
        ps.setLong(1, numAssurance);
    }
    
    public static String reqInsertEcheance() {
        return "INSERT INTO ECHEANCE (numassurance, date_, montant) VALUES (?, ?, ?)";
    }
    
    public static void paramInsertEcheance(PreparedStatement ps, long numAssurance, 
            Date dateEcheance, double montant) throws SQLException {
        ps.setLong(1, numAssurance);
        ps.setDate(2, new java.sql.Date(dateEcheance.getTime()));
        ps.setDouble(3, montant);
    }
    
    public static String reqSupprimerAssurance() {
        return "{call supprimerAssurance(?)}";
    }
    
    public static void paramSupprimerAssurance(CallableStatement cs, long numAssurance) throws SQLException {
        cs.setLong(1, numAssurance);
    }

    // LOYERS

    
    public static String reqMarquerLoyerPaye() {
        return "{call marquerLoyerPaye(?, ?)}";
    }
    
    public static void paramMarquerLoyerPaye(CallableStatement cs, long idLoyer, Date datePaiement) throws SQLException {
        cs.setLong(1, idLoyer);
        cs.setDate(2, new java.sql.Date(datePaiement.getTime()));
    }
    
    public static String reqAnnulerPaiementLoyer() {
        return "{call annulerPaiementLoyer(?)}";
    }
    
    public static void paramAnnulerPaiementLoyer(CallableStatement cs, long idLoyer) throws SQLException {
        cs.setLong(1, idLoyer);
    }


    // GARANTS

    
    public static String reqUpdateGarant() {
        return "UPDATE GARANT SET nom_garant = ?, prenom_garant = ?, " +
               "adresse_garant = ?, mail_garant = ?, tel_garant = ? WHERE id_garant = ?";
    }
    
    public static void paramUpdateGarant(PreparedStatement ps, String idGarant, String nom, String prenom,
            String adresse, String email, String tel) throws SQLException {
        ps.setString(1, nom);
        ps.setString(2, prenom);
        ps.setString(3, adresse);
        ps.setString(4, email);
        ps.setString(5, tel);
        ps.setString(6, idGarant);
    }
    
    public static String reqSupprimerGarant() {
        return "{call supprimerGarant(?)}";
    }
    
    public static void paramSupprimerGarant(CallableStatement cs, String idGarant) throws SQLException {
        cs.setString(1, idGarant);
    }

    // ========================================================================
    // UTILITAIRES
    // ========================================================================
    
    public static String reqGetNextId() {
        return "{? = call getNextId(?, ?)}";
    }
    
    public static void paramGetNextId(CallableStatement cs, String tableName, String columnName) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setString(2, tableName);
        cs.setString(3, columnName);
    }
}
