package modele.dao.Requete;

import java.sql.*;
import modele.Proprietaire;

/**
 * Classe contenant les requêtes SQL pour ProprietaireDao
 */
public class RequeteProprietaire {

    // CREATE 
    public static String reqCreate() {
        return "INSERT INTO PROPRIETAIRE (ID_PROPIETAIRE, NOM_PROP, PRENOM_PROP, "
             + "ADRESSE_PROP, TEL_PROP, MAIL_PROP, ID_BATIMENT) "
             + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    public static void paramCreate(PreparedStatement st, Proprietaire p) throws SQLException {
        st.setLong(1, p.getIdProprietaireLong());
        st.setString(2, p.getNomProp());
        st.setString(3, p.getPrenomProp());
        st.setString(4, p.getAdresseProp());
        st.setString(5, p.getTelProp());
        st.setString(6, p.getMailProp());
        
        if (p.getIdBatiment() != null) {
            st.setLong(7, p.getIdBatiment());
        } else {
            throw new SQLException("ID_BATIMENT est obligatoire pour un propriétaire");
        }
    }

    // UPDATE 
    public static String reqUpdate() {
        return "UPDATE PROPRIETAIRE SET NOM_PROP=?, PRENOM_PROP=?, ADRESSE_PROP=?, "
             + "TEL_PROP=?, MAIL_PROP=?, ID_BATIMENT=? "
             + "WHERE ID_PROPIETAIRE=?";
    }

    public static void paramUpdate(PreparedStatement st, Proprietaire p) throws SQLException {
        st.setString(1, p.getNomProp());
        st.setString(2, p.getPrenomProp());
        st.setString(3, p.getAdresseProp());
        st.setString(4, p.getTelProp());
        st.setString(5, p.getMailProp());
        
        if (p.getIdBatiment() != null) {
            st.setLong(6, p.getIdBatiment());
        } else {
            throw new SQLException("ID_BATIMENT est obligatoire");
        }
        
        st.setLong(7, p.getIdProprietaireLong());
    }

    // DELETE 
    public static String reqDelete() {
        return "DELETE FROM PROPRIETAIRE WHERE ID_PROPIETAIRE=?";
    }

    public static void paramDelete(PreparedStatement st, Proprietaire p) throws SQLException {
        st.setLong(1, p.getIdProprietaireLong());
    }

    //  FIND BY ID 
    public static String reqFindById() {
        return "SELECT * FROM PROPRIETAIRE WHERE ID_PROPIETAIRE=?";
    }

    public static void paramFindById(PreparedStatement st, String id) throws SQLException {
        st.setLong(1, Long.parseLong(id));
    }

    //  FIND ALL 
    public static String reqFindAll() {
        return "SELECT * FROM PROPRIETAIRE ORDER BY NOM_PROP, PRENOM_PROP";
    }

    //  FIND BY BATIMENT
    public static String reqFindByBatiment() {
        return "SELECT * FROM PROPRIETAIRE WHERE ID_BATIMENT = ?";
    }

    public static void paramFindByBatiment(PreparedStatement st, long idBatiment) throws SQLException {
        st.setLong(1, idBatiment);
    }
}
