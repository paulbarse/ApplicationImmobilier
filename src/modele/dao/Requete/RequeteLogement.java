package modele.dao.Requete;

import java.sql.*;
import modele.Logement;

/**
 * Requetes SQL pour LogementDao
 */
public class RequeteLogement {

    public static String reqCreate() {
        return "INSERT INTO LOGEMENT (ID_LOGEMENT, TYPE_LOGEMENT, ADRESSE_LOGEMENT, "
             + "COMPLEMENT_ADRESSE, NUM_FISCAL, SURFACE, NB_PIECES, ID_RELEVE, ID_BATIMENT, ID_BAIL) "
             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    public static void paramCreate(PreparedStatement st, Logement logement) throws SQLException {
        st.setLong(1, logement.getIdLogementLong());
        st.setString(2, logement.getTypeLogement());
        st.setString(3, logement.getAdresseLogement()); 
        st.setString(4, logement.getAdresseComplementaire()); 
        st.setLong(5, logement.getNumFiscalLong());
        st.setDouble(6, logement.getSurfaceDouble());
        st.setInt(7, logement.getNbPieces());
        
        // FK nullable : id_releve
        if (logement.getIdReleve() != null) {
            st.setLong(8, logement.getIdReleve());
        } else {
            st.setNull(8, Types.NUMERIC);
        }
        
        // FK NOT NULL : id_batiment
        st.setLong(9, logement.getIdBatiment());
        
        // FK nullable : id_bail
        if (logement.getIdBail() != null) {
            st.setLong(10, logement.getIdBail());
        } else {
            st.setNull(10, Types.NUMERIC);
        }
    }

    // UPDATE 
    public static String reqUpdate() {
        return "UPDATE LOGEMENT SET TYPE_LOGEMENT=?, ADRESSE_LOGEMENT=?, COMPLEMENT_ADRESSE=?, "
             + "NUM_FISCAL=?, SURFACE=?, NB_PIECES=?, ID_RELEVE=?, ID_BATIMENT=?, ID_BAIL=? "
             + "WHERE ID_LOGEMENT=?";
    }

    public static void paramUpdate(PreparedStatement st, Logement logement) throws SQLException {
        st.setString(1, logement.getTypeLogement());
        st.setString(2, logement.getAdresseLogement());
        
        String complement = logement.getAdresseComplementaire();
        if (complement != null && !complement.isEmpty()) {
            st.setString(3, complement);
        } else {
            st.setString(3, logement.getAdresseLogement()); // Fallback
        }
        
        st.setLong(4, logement.getNumFiscalLong());
        st.setDouble(5, logement.getSurfaceDouble());
        st.setInt(6, logement.getNbPieces());
        
        if (logement.getIdReleve() != null) {
            st.setLong(7, logement.getIdReleve());
        } else {
            st.setNull(7, Types.NUMERIC);
        }
        
        st.setLong(8, logement.getIdBatiment());
        
        if (logement.getIdBail() != null) {
            st.setLong(9, logement.getIdBail());
        } else {
            st.setNull(9, Types.NUMERIC);
        }
        
        st.setLong(10, logement.getIdLogementLong());
    }

    //  DELETE 
    public static String reqDelete() {
        return "DELETE FROM LOGEMENT WHERE ID_LOGEMENT=?";
    }

    public static void paramDelete(PreparedStatement st, Logement logement) throws SQLException {
        st.setLong(1, logement.getIdLogementLong());
    }

    // DELETE BY ID 
    public static String reqDeleteById() {
        return "DELETE FROM LOGEMENT WHERE ID_LOGEMENT=?";
    }

    public static void paramDeleteById(PreparedStatement st, long idLogement) throws SQLException {
        st.setLong(1, idLogement);
    }

    // FIND BY ID 
    public static String reqFindById() {
        return "SELECT * FROM LOGEMENT WHERE ID_LOGEMENT=?";
    }
    
    public static void paramFindById(PreparedStatement st, String id) throws SQLException {
        st.setLong(1, Long.parseLong(id));
    }

    // FIND ALL 
    public static String reqFindAll() {
        return "SELECT * FROM LOGEMENT ORDER BY ID_LOGEMENT ASC";
    }

    // IND ALL AVEC ADRESSE COMPLETE
    public static String reqFindAllAvecAdresseComplete() {
        return "SELECT * FROM V_LOGEMENTS_ADRESSE_COMPLETE ORDER BY ID_LOGEMENT";
    }

    // FIND BY BATIMENT 
    public static String reqFindByBatiment() {
        return "SELECT * FROM LOGEMENT WHERE ID_BATIMENT = ? ORDER BY COMPLEMENT_ADRESSE, ADRESSE_LOGEMENT";
    }

    public static void paramFindByBatiment(PreparedStatement st, long idBatiment) throws SQLException {
        st.setLong(1, idBatiment);
    }

    // FIND BY BAIL 
    public static String reqFindByBail() {
        return "SELECT * FROM LOGEMENT WHERE ID_BAIL = ?";
    }

    public static void paramFindByBail(PreparedStatement st, long idBail) throws SQLException {
        st.setLong(1, idBail);
    }

    //  FIND LIBRES 
    public static String reqFindLibres() {
        return "SELECT * FROM LOGEMENT WHERE ID_BAIL IS NULL ORDER BY ID_BATIMENT, COMPLEMENT_ADRESSE";
    }

    //  LIER AU BATIMENT 
    public static String reqLierAuBatiment() {
        return "UPDATE LOGEMENT SET ID_BATIMENT = ? WHERE ID_LOGEMENT = ?";
    }

    public static void paramLierAuBatiment(PreparedStatement st, long idLogement, long idBatiment) throws SQLException {
        st.setLong(1, idBatiment);
        st.setLong(2, idLogement);
    }

    // LIER AU BAIL
    public static String reqLierAuBail() {
        return "UPDATE LOGEMENT SET ID_BAIL = ? WHERE ID_LOGEMENT = ?";
    }

    public static void paramLierAuBail(PreparedStatement st, long idLogement, long idBail) throws SQLException {
        st.setLong(1, idBail);
        st.setLong(2, idLogement);
    }

    // DELIER DU BAIL 
    public static String reqDelierDuBail() {
        return "UPDATE LOGEMENT SET ID_BAIL = NULL WHERE ID_LOGEMENT = ?";
    }

    public static void paramDelierDuBail(PreparedStatement st, long idLogement) throws SQLException {
        st.setLong(1, idLogement);
    }
}