package modele.dao.Requete;

import java.sql.*;
import modele.Entreprise;

/**
 * Classe contenant les requêtes SQL pour EntrepriseDao
 */
public class RequeteEntreprise {

    // CREATE 
    public static String reqCreate() {
        return "INSERT INTO ENTREPRISE (SIRET, NOM_ENTREPRISE, ADRESSE, TELEPHONE, EMAIL, SPECIALITE) "
             + "VALUES (?, ?, ?, ?, ?, ?)";
    }

    public static void paramCreate(PreparedStatement st, Entreprise entreprise) throws SQLException {
        st.setLong(1, entreprise.getSiret());
        st.setString(2, entreprise.getNomEntreprise());
        st.setString(3, entreprise.getAdresse());
        st.setString(4, entreprise.getTelephone());
        st.setString(5, entreprise.getEmail());
        st.setString(6, entreprise.getSpecialite());
    }

    //  UPDATE 
    public static String reqUpdate() {
        return "UPDATE ENTREPRISE SET NOM_ENTREPRISE=?, ADRESSE=?, TELEPHONE=?, EMAIL=?, SPECIALITE=? "
             + "WHERE SIRET=?";
    }

    public static void paramUpdate(PreparedStatement st, Entreprise entreprise) throws SQLException {
        st.setString(1, entreprise.getNomEntreprise());
        st.setString(2, entreprise.getAdresse());
        st.setString(3, entreprise.getTelephone());
        st.setString(4, entreprise.getEmail());
        st.setString(5, entreprise.getSpecialite());
        st.setLong(6, entreprise.getSiret());
    }

    // DELETE 
    public static String reqDelete() {
        return "DELETE FROM ENTREPRISE WHERE SIRET=?";
    }

    public static void paramDelete(PreparedStatement st, Entreprise entreprise) throws SQLException {
        st.setLong(1, entreprise.getSiret());
    }

    // FIND BY ID
    public static String reqFindById() {
        return "SELECT * FROM ENTREPRISE WHERE SIRET=?";
    }

    public static void paramFindById(PreparedStatement st, String id) throws SQLException {
        st.setLong(1, Long.parseLong(id));
    }

    // FIND ALL 
    public static String reqFindAll() {
        return "SELECT * FROM ENTREPRISE ORDER BY NOM_ENTREPRISE";
    }

    // FIND BY SPECIALITE 
    public static String reqFindBySpecialite() {
        return "SELECT * FROM ENTREPRISE WHERE UPPER(SPECIALITE) LIKE UPPER(?) ORDER BY NOM_ENTREPRISE";
    }

    public static void paramFindBySpecialite(PreparedStatement st, String specialite) throws SQLException {
        st.setString(1, "%" + specialite + "%");
    }

    // COUNT FACTURES (fonction PL/SQL) 
    public static String reqCountFactures() {
        return "{? = call countFacturesNonPayeesEntreprise(?)}";
    }

    public static void paramCountFactures(CallableStatement cs, long siret) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setLong(2, siret);
    }



    public static String reqFindAllForCombo() {
        return "SELECT SIRET, NOM_ENTREPRISE, SPECIALITE FROM ENTREPRISE ORDER BY NOM_ENTREPRISE";
    }
}
