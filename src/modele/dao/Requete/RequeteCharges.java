package modele.dao.Requete;

import java.sql.*;
import modele.Charges;

/**
 * Classe contenant les requêtes SQL pour ChargesDao
 */
public class RequeteCharges {

    // GET MONTANT BY TYPE  
    public static String reqGetMontantByType() {
        return "SELECT NVL(SUM(MONTANT * NVL(PCT_RECUPERABLE, 100) / 100), 0) AS TOTAL " +
               "FROM CHARGES WHERE ID_BATIMENT = ? " +
               "AND EXTRACT(YEAR FROM DATE_CHARGE) = ? " +
               "AND UPPER(TYPE_CHARGES) = UPPER(?)";
    }

    public static void paramGetMontantByType(PreparedStatement ps, long idBatiment, int annee, String typeCharge) throws SQLException {
        ps.setLong(1, idBatiment);
        ps.setInt(2, annee);
        ps.setString(3, typeCharge);
    }

    //  GET MONTANT BY TYPE
    public static String reqGetChargesBatimentByType() {
        return "{? = call getChargesBatimentByType(?, ?, ?)}";
    }

    public static void paramGetChargesBatimentByType(CallableStatement cs, long idBatiment, int annee, String typeCharge) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setLong(2, idBatiment);
        cs.setInt(3, annee);
        cs.setString(4, typeCharge);
    }

    // GET PCT RECUPERABLE BY TYPE
    public static String reqGetPctRecuperableByType() {
        return "SELECT NVL(PCT_RECUPERABLE, 100) AS PCT FROM CHARGES " +
               "WHERE ID_BATIMENT = ? " +
               "AND EXTRACT(YEAR FROM DATE_CHARGE) = ? " +
               "AND UPPER(TYPE_CHARGES) = UPPER(?) " +
               "AND ROWNUM = 1";
    }

    public static void paramGetPctRecuperableByType(PreparedStatement ps, long idBatiment, int annee, String typeCharge) throws SQLException {
        ps.setLong(1, idBatiment);
        ps.setInt(2, annee);
        ps.setString(3, typeCharge);
    }

    // SAISIR CHARGE IMMEUBLE (procédure) 
    public static String reqSaisirChargeImmeuble() {
        return "{call saisirChargeImmeuble(?, ?, ?, ?, ?)}";
    }

    public static void paramSaisirChargeImmeuble(CallableStatement cs, long idBatiment, int annee, 
            String typeCharge, double montant, double pctRecuperable) throws SQLException {
        cs.setLong(1, idBatiment);
        cs.setInt(2, annee);
        cs.setString(3, typeCharge);
        cs.setDouble(4, montant);
        cs.setDouble(5, pctRecuperable);
    }

    // SAISIR TOUTES CHARGES IMMEUBLE (procédure)
    public static String reqSaisirToutesChargesImmeuble() {
        return "{call saisirToutesChargesImmeuble(?, ?, ?, ?, ?, ?)}";
    }

    public static void paramSaisirToutesChargesImmeuble(CallableStatement cs, long idBatiment, int annee,
            double montantEau, double montantOM, double montantElec, double montantEntretien) throws SQLException {
        cs.setLong(1, idBatiment);
        cs.setInt(2, annee);
        cs.setDouble(3, montantEau);
        cs.setDouble(4, montantOM);
        cs.setDouble(5, montantElec);
        cs.setDouble(6, montantEntretien);
    }

    // GET TOTAL CHARGES (fonction PL/SQL) 
    public static String reqGetTotalChargesBatiment() {
        return "{? = call getTotalChargesBatiment(?, ?)}";
    }

    public static void paramGetTotalChargesBatiment(CallableStatement cs, long idBatiment, int annee) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setLong(2, idBatiment);
        cs.setInt(3, annee);
    }

    // DELETE BY TYPE 
    public static String reqDeleteByType() {
        return "DELETE FROM CHARGES WHERE ID_BATIMENT = ? AND EXTRACT(YEAR FROM DATE_CHARGE) = ? AND UPPER(TYPE_CHARGES) = UPPER(?)";
    }

    public static void paramDeleteByType(PreparedStatement ps, long idBatiment, int annee, String typeCharge) throws SQLException {
        ps.setLong(1, idBatiment);
        ps.setInt(2, annee);
        ps.setString(3, typeCharge);
    }

    // FIND ALL BY BATIMENT 
    public static String reqFindAllByBatiment() {
        return "SELECT ID_CHARGE, NATURE, TYPE_CHARGES, DATE_CHARGE, MONTANT, PCT_RECUPERABLE, ID_BATIMENT " +
               "FROM CHARGES WHERE ID_BATIMENT = ? ORDER BY DATE_CHARGE DESC";
    }

    public static void paramFindAllByBatiment(PreparedStatement ps, long idBatiment) throws SQLException {
        ps.setLong(1, idBatiment);
    }

    // FIND BY ID 
    public static String reqFindById() {
        return "SELECT ID_CHARGE, NATURE, TYPE_CHARGES, DATE_CHARGE, MONTANT, PCT_RECUPERABLE, ID_BATIMENT " +
               "FROM CHARGES WHERE ID_CHARGE = ?";
    }

    public static void paramFindById(PreparedStatement ps, long idCharge) throws SQLException {
        ps.setLong(1, idCharge);
    }

    // CREATE 
    public static String reqCreate() {
        return "INSERT INTO CHARGES (ID_CHARGE, ID_BATIMENT, DATE_CHARGE, TYPE_CHARGES, NATURE, MONTANT, PCT_RECUPERABLE) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    public static void paramCreate(PreparedStatement ps, long newId, Charges c) throws SQLException {
        ps.setLong(1, newId);
        ps.setLong(2, c.getIdBatiment());
        ps.setDate(3, new java.sql.Date(c.getDateCharge().getTime()));
        ps.setString(4, c.getTypeCharges());
        ps.setString(5, c.getNature());
        ps.setDouble(6, c.getMontant());
        ps.setDouble(7, c.getPctRecuperable());
    }

    // UPDATE CHARGE 
    public static String reqUpdateCharge() {
        return "UPDATE CHARGES SET DATE_CHARGE = ?, NATURE = ?, TYPE_CHARGES = ?, " +
               "MONTANT = ?, PCT_RECUPERABLE = ? WHERE ID_CHARGE = ?";
    }

    public static void paramUpdateCharge(PreparedStatement ps, long idCharge, java.util.Date date, 
            String nature, String type, double montant, double pctRecuperable) throws SQLException {
        ps.setDate(1, new java.sql.Date(date.getTime()));
        ps.setString(2, nature);
        ps.setString(3, type);
        ps.setDouble(4, montant);
        ps.setDouble(5, pctRecuperable);
        ps.setLong(6, idCharge);
    }

    // DELETE 
    public static String reqDelete() {
        return "DELETE FROM CHARGES WHERE ID_CHARGE = ?";
    }

    public static void paramDelete(PreparedStatement ps, long idCharge) throws SQLException {
        ps.setLong(1, idCharge);
    }

    // FIND CHARGE ID 
    public static String reqFindChargeId() {
        return "SELECT ID_CHARGE FROM CHARGES WHERE ID_BATIMENT = ? AND EXTRACT(YEAR FROM DATE_CHARGE) = ? AND UPPER(TYPE_CHARGES) = UPPER(?) AND ROWNUM = 1";
    }

    public static void paramFindChargeId(PreparedStatement ps, long idBatiment, int annee, String typeCharge) throws SQLException {
        ps.setLong(1, idBatiment);
        ps.setInt(2, annee);
        ps.setString(3, typeCharge);
    }

    // CREATE SQL (avec date annuelle) 
    public static String reqCreateSQL() {
        return "INSERT INTO CHARGES (ID_CHARGE, ID_BATIMENT, DATE_CHARGE, TYPE_CHARGES, NATURE, MONTANT, PCT_RECUPERABLE) " +
               "VALUES (?, ?, TO_DATE('31/12/' || ?, 'DD/MM/YYYY'), ?, ?, ?, ?)";
    }

    public static void paramCreateSQL(PreparedStatement ps, long newId, long idBatiment, int annee, 
            String typeCharge, String nature, double montant, double pctRecuperable) throws SQLException {
        ps.setLong(1, newId);
        ps.setLong(2, idBatiment);
        ps.setInt(3, annee);
        ps.setString(4, typeCharge.toUpperCase());
        ps.setString(5, nature);
        ps.setDouble(6, montant);
        ps.setDouble(7, pctRecuperable);
    }

    //  UPDATE SQL
    public static String reqUpdateSQL() {
        return "UPDATE CHARGES SET MONTANT = ?, PCT_RECUPERABLE = ? WHERE ID_CHARGE = ?";
    }

    public static void paramUpdateSQL(PreparedStatement ps, long idCharge, double montant, double pctRecuperable) throws SQLException {
        ps.setDouble(1, montant);
        ps.setDouble(2, pctRecuperable);
        ps.setLong(3, idCharge);
    }

    //  GENERER ID CHARGE 
    public static String reqGenererIdCharge() {
        return "SELECT NVL(MAX(ID_CHARGE), 0) + 1 AS NEW_ID FROM CHARGES";
    }



    public static String reqFindByBatimentForCombo() {
        return "SELECT ID_CHARGE, NATURE, TYPE_CHARGES FROM CHARGES WHERE ID_BATIMENT = ? ORDER BY NATURE";
    }

    public static void paramFindByBatimentForCombo(PreparedStatement ps, long idBatiment) throws SQLException {
        ps.setLong(1, idBatiment);
    }
}
