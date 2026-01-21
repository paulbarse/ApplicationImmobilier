package modele.dao.Requete;

import java.sql.*;
import modele.Garant;

/**
 * Classe contenant les requêtes SQL pour GarantDao
 */
public class RequeteGarant {

    //  CREATE 
    public static String reqCreate() {
        return "INSERT INTO GARANT (ID_GARANT, NOM_GARANT, PRENOM_GARANT, "
             + "ADRESSE_GARANT, TEL_GARANT, MAIL_GARANT) "
             + "VALUES (?, ?, ?, ?, ?, ?)";
    }

    public static void paramCreate(PreparedStatement st, Garant g) throws SQLException {
        st.setString(1, g.getIdGarant());
        st.setString(2, g.getNomGarant());
        st.setString(3, g.getPrenomGarant());
        st.setString(4, g.getAdresseGarant());
        st.setString(5, g.getTelGarant());
        st.setString(6, g.getMailGarant());
    }

    //  UPDATE 
    public static String reqUpdate() {
        return "UPDATE GARANT SET NOM_GARANT=?, PRENOM_GARANT=?, "
             + "ADRESSE_GARANT=?, TEL_GARANT=?, MAIL_GARANT=? "
             + "WHERE ID_GARANT=?";
    }

    public static void paramUpdate(PreparedStatement st, Garant g) throws SQLException {
        st.setString(1, g.getNomGarant());
        st.setString(2, g.getPrenomGarant());
        st.setString(3, g.getAdresseGarant());
        st.setString(4, g.getTelGarant());
        st.setString(5, g.getMailGarant());
        st.setString(6, g.getIdGarant());
    }

    // DELETE AJOUTE
    public static String reqDeleteAjoute() {
        return "DELETE FROM AJOUTE WHERE ID_GARANT=?";
    }

    public static void paramDeleteAjoute(PreparedStatement st, Garant g) throws SQLException {
        st.setString(1, g.getIdGarant());
    }

    // DELETE 
    public static String reqDelete() {
        return "DELETE FROM GARANT WHERE ID_GARANT=?";
    }

    public static void paramDelete(PreparedStatement st, Garant g) throws SQLException {
        st.setString(1, g.getIdGarant());
    }

    // FIND BY ID 
    public static String reqFindById() {
        return "SELECT * FROM GARANT WHERE ID_GARANT=?";
    }

    public static void paramFindById(PreparedStatement st, String id) throws SQLException {
        st.setString(1, id);
    }

    //  FIND ALL 
    public static String reqFindAll() {
        return "SELECT * FROM GARANT ORDER BY NOM_GARANT, PRENOM_GARANT";
    }

    // FIND BY BAIL 
    public static String reqFindByBail() {
        return "SELECT g.* FROM GARANT g "
             + "JOIN AJOUTE a ON g.ID_GARANT = a.ID_GARANT "
             + "WHERE a.ID_BAIL = ?";
    }

    public static void paramFindByBail(PreparedStatement st, long idBail) throws SQLException {
        st.setLong(1, idBail);
    }

    // LIER AU BAIL
    public static String reqLierAuBail() {
        return "INSERT INTO AJOUTE (ID_BAIL, ID_GARANT) VALUES (?, ?)";
    }

    public static void paramLierAuBail(PreparedStatement st, String idGarant, long idBail) throws SQLException {
        st.setLong(1, idBail);
        st.setString(2, idGarant);
    }

    // DELIER DU BAIL
    public static String reqDelierDuBail() {
        return "DELETE FROM AJOUTE WHERE ID_BAIL = ? AND ID_GARANT = ?";
    }

    public static void paramDelierDuBail(PreparedStatement st, String idGarant, long idBail) throws SQLException {
        st.setLong(1, idBail);
        st.setString(2, idGarant);
    }
}
