package modele.dao.Requete;

import java.sql.*;
import modele.Garage;

/**
 * Classe contenant les requêtes SQL pour GarageDao
 */
public class RequeteGarage {

    // CREATE 
    public static String reqCreate() {
        return "INSERT INTO GARAGE (ID_GARAGE, SURFACE_GARAGE, NUM_FISCAL, "
             + "ADRESSE_GARAGE, COMPLEMENT_ADRESSE, ID_BATIMENT, ID_LOGEMENT, ID_BAIL) "
             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }

    public static void paramCreate(PreparedStatement st, Garage g) throws SQLException {
        st.setLong(1, g.getIdGarageLong());
        
        if (g.getSurfaceGarage() != null) {
            st.setInt(2, g.getSurfaceGarage());
        } else {
            st.setNull(2, Types.INTEGER);
        }
        
        st.setLong(3, g.getNumFiscalLong());
        
        String complement = g.getAdresseGarage();
        st.setString(4, complement);
        st.setString(5, complement);
        
        st.setLong(6, g.getIdBatiment());
        
        if (g.getIdLogement() != null) {
            st.setLong(7, g.getIdLogement());
        } else {
            st.setNull(7, Types.NUMERIC);
        }
        
        if (g.getIdBail() != null) {
            st.setLong(8, g.getIdBail());
        } else {
            st.setNull(8, Types.NUMERIC);
        }
    }

    // UPDATE
    public static String reqUpdate() {
        return "UPDATE GARAGE SET SURFACE_GARAGE=?, NUM_FISCAL=?, ADRESSE_GARAGE=?, "
             + "COMPLEMENT_ADRESSE=?, ID_BATIMENT=?, ID_LOGEMENT=?, ID_BAIL=? "
             + "WHERE ID_GARAGE=?";
    }

    public static void paramUpdate(PreparedStatement st, Garage g) throws SQLException {
        if (g.getSurfaceGarage() != null) {
            st.setInt(1, g.getSurfaceGarage());
        } else {
            st.setNull(1, Types.INTEGER);
        }
        
        st.setLong(2, g.getNumFiscalLong());
        
        String complement = g.getAdresseGarage();
        st.setString(3, complement);
        st.setString(4, complement);
        
        st.setLong(5, g.getIdBatiment());
        
        if (g.getIdLogement() != null) {
            st.setLong(6, g.getIdLogement());
        } else {
            st.setNull(6, Types.NUMERIC);
        }
        
        if (g.getIdBail() != null) {
            st.setLong(7, g.getIdBail());
        } else {
            st.setNull(7, Types.NUMERIC);
        }
        
        st.setLong(8, g.getIdGarageLong());
    }

    // DELETE 
    public static String reqDelete() {
        return "DELETE FROM GARAGE WHERE ID_GARAGE=?";
    }

    public static void paramDelete(PreparedStatement st, long idGarage) throws SQLException {
        st.setLong(1, idGarage);
    }

    // FIND BY ID 
    public static String reqFindById() {
        return "SELECT * FROM GARAGE WHERE ID_GARAGE=?";
    }

    public static void paramFindById(PreparedStatement st, long idGarage) throws SQLException {
        st.setLong(1, idGarage);
    }

    // FIND ALL 
    public static String reqFindAll() {
        return "SELECT * FROM GARAGE ORDER BY ID_GARAGE";
    }

    // FIND ALL AVEC ADRESSE COMPLETE 
    public static String reqFindAllAvecAdresseComplete() {
        return "SELECT * FROM V_GARAGES_ADRESSE_COMPLETE ORDER BY ID_GARAGE";
    }

    // FIND BY BATIMENT 
    public static String reqFindByBatiment() {
        return "SELECT * FROM GARAGE WHERE ID_BATIMENT = ? ORDER BY COMPLEMENT_ADRESSE, ADRESSE_GARAGE";
    }

    public static void paramFindByBatiment(PreparedStatement st, long idBatiment) throws SQLException {
        st.setLong(1, idBatiment);
    }

    //  FIND BY LOGEMENT 
    public static String reqFindByLogement() {
        return "SELECT * FROM GARAGE WHERE ID_LOGEMENT = ?";
    }

    public static void paramFindByLogement(PreparedStatement st, long idLogement) throws SQLException {
        st.setLong(1, idLogement);
    }

    // FIND BY BAIL 
    public static String reqFindByBail() {
        return "SELECT * FROM GARAGE WHERE ID_BAIL = ?";
    }

    public static void paramFindByBail(PreparedStatement st, long idBail) throws SQLException {
        st.setLong(1, idBail);
    }

    // FIND LIBRES 
    public static String reqFindLibres() {
        return "SELECT * FROM GARAGE WHERE ID_BAIL IS NULL ORDER BY ID_BATIMENT, COMPLEMENT_ADRESSE";
    }

    // LIER AU BATIMENT 
    public static String reqLierAuBatiment() {
        return "UPDATE GARAGE SET ID_BATIMENT = ? WHERE ID_GARAGE = ?";
    }

    public static void paramLierAuBatiment(PreparedStatement st, long idGarage, long idBatiment) throws SQLException {
        st.setLong(1, idBatiment);
        st.setLong(2, idGarage);
    }

    // LIER AU BAIL 
    public static String reqLierAuBail() {
        return "UPDATE GARAGE SET ID_BAIL = ? WHERE ID_GARAGE = ?";
    }

    public static void paramLierAuBail(PreparedStatement st, long idGarage, long idBail) throws SQLException {
        st.setLong(1, idBail);
        st.setLong(2, idGarage);
    }

    // DELIER DU BAIL 
    public static String reqDelierDuBail() {
        return "UPDATE GARAGE SET ID_BAIL = NULL WHERE ID_GARAGE = ?";
    }

    public static void paramDelierDuBail(PreparedStatement st, long idGarage) throws SQLException {
        st.setLong(1, idGarage);
    }

    // GET NEXT ID 
    public static String reqGetNextId() {
        return "SELECT NVL(MAX(ID_GARAGE), 0) + 1 FROM GARAGE";
    }

    // LIER PAR ADRESSE
    public static String reqLierGarageABatParAdresse() {
        return "UPDATE GARAGE SET ID_BATIMENT = ? WHERE LOWER(TRIM(ADRESSE_GARAGE)) = LOWER(TRIM(?))";
    }

    public static void paramLierGarageABatParAdresse(PreparedStatement st, int idBat, String adresse) throws SQLException {
        st.setInt(1, idBat);
        st.setString(2, adresse);
    }
}
