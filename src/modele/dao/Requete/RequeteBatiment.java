package modele.dao.Requete;

import java.sql.*;
import modele.Batiment;

/**
 * Classe contenant les requêtes SQL pour BatimentDao
 */
public class RequeteBatiment {

    //  CREATE 
    public static String reqCreate() {
        return "INSERT INTO BATIMENT (ID_BATIMENT, ADRESSE, NOMBRE_D_ETAGE, TYPE_BATIMENT, RUE, CODE_POSTALE) "
             + "VALUES (?, ?, ?, ?, ?, ?)";
    }

    public static void paramCreate(PreparedStatement st, Batiment b) throws SQLException {
        st.setLong(1, b.getIdBatimentLong());
        st.setString(2, b.getAdresse());
        if (b.getNombreEtage() != null) {
            st.setInt(3, b.getNombreEtage());
        } else {
            st.setNull(3, Types.INTEGER);
        }
        st.setString(4, b.getTypeBatiment());
        st.setString(5, b.getRue());
        st.setInt(6, b.getCodePostal());
    }

    // UPDATE 
    public static String reqUpdate() {
        return "UPDATE BATIMENT SET ADRESSE=?, NOMBRE_D_ETAGE=?, TYPE_BATIMENT=?, RUE=?, CODE_POSTALE=? "
             + "WHERE ID_BATIMENT=?";
    }

    public static void paramUpdate(PreparedStatement st, Batiment b) throws SQLException {
        st.setString(1, b.getAdresse());
        if (b.getNombreEtage() != null) {
            st.setInt(2, b.getNombreEtage());
        } else {
            st.setNull(2, Types.INTEGER);
        }
        st.setString(3, b.getTypeBatiment());
        st.setString(4, b.getRue());
        st.setInt(5, b.getCodePostal());
        st.setLong(6, b.getIdBatimentLong());
    }

    // DELETE 
    public static String reqDelete() {
        return "DELETE FROM BATIMENT WHERE ID_BATIMENT=?";
    }

    public static void paramDelete(PreparedStatement st, long id) throws SQLException {
        st.setLong(1, id);
    }

    // FIND BY ID
    public static String reqFindById() {
        return "SELECT * FROM BATIMENT WHERE ID_BATIMENT=?";
    }

    public static void paramFindById(PreparedStatement st, long id) throws SQLException {
        st.setLong(1, id);
    }

    // FIND ALL 
    public static String reqFindAll() {
        return "SELECT * FROM BATIMENT ORDER BY ID_BATIMENT";
    }

    // FIND BY ADRESSE 
    public static String reqFindByAdresse() {
        return "SELECT * FROM BATIMENT WHERE ADRESSE = ?";
    }

    public static void paramFindByAdresse(PreparedStatement st, String adresse) throws SQLException {
        st.setString(1, adresse);
    }

    // FIND BY CODE POSTAL 
    public static String reqFindByCodePostal() {
        return "SELECT * FROM BATIMENT WHERE CODE_POSTALE = ? ORDER BY ADRESSE";
    }

    public static void paramFindByCodePostal(PreparedStatement st, int codePostal) throws SQLException {
        st.setInt(1, codePostal);
    }

    // FIND DISPONIBLES POUR LOGEMENT 
    public static String reqFindDisponiblesPourLogement() {
        return "SELECT b.* FROM BATIMENT b " +
               "WHERE isBatimentDisponiblePourLogement(b.ID_BATIMENT) = 1 " +
               "ORDER BY b.ID_BATIMENT";
    }

    // DELIER LOGEMENT A BATIMENT 
    public static String reqDelierLogementABat() {
        return "{call delierLogementABat(?)}";
    }

    public static void paramDelierLogementABat(CallableStatement cs, String adresse) throws SQLException {
        cs.setString(1, adresse);
    }

    // DELIER GARAGE A BATIMENT 
    public static String reqDelierGarageABat() {
        return "{call delierGarageABat(?)}";
    }

    public static void paramDelierGarageABat(CallableStatement cs, String adresse) throws SQLException {
        cs.setString(1, adresse);
    }

    // GET TOTAL ASSURANCE BY BATIMENT 
    public static String reqGetTotalAssuranceByBatiment() {
        return "{? = call GETTOTALASSURANCEBATIMENT(?, ?)}";
    }

    public static void paramGetTotalAssuranceByBatiment(CallableStatement cs, long idBatiment, int annee) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setLong(2, idBatiment);
        cs.setLong(3, annee);
    }

    // EXISTS 
    public static String reqExists() {
        return "SELECT COUNT(*) FROM BATIMENT WHERE ID_BATIMENT = ?";
    }

    public static void paramExists(PreparedStatement st, long id) throws SQLException {
        st.setLong(1, id);
    }

    // GET NEXT ID 
    public static String reqGetNextId() {
        return "{? = call getNextId(?, ?)}";
    }

    public static void paramGetNextId(CallableStatement cs, String tableName, String columnName) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setString(2, tableName);
        cs.setString(3, columnName);
    }
}
