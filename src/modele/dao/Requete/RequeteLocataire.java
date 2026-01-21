package modele.dao.Requete;

import java.sql.*;
import modele.Locataire;
import vue.refacto.FormatUtils;

/**
 * Classe contenant les requêtes SQL pour LocataireDao
 */
public class RequeteLocataire {

    // --- CREATE ---
    public static String reqCreate() {
        return "INSERT INTO LOCATAIRE (ID_LOCATAIRE, NOM_LOC, PRENOM_LOC, TEL_LOC, MAIL_LOC, DATE_NAISSANCE) "
             + "VALUES (?, ?, ?, ?, ?, ?)";
    }

    public static void paramCreate(PreparedStatement st, Locataire locataire) throws SQLException {
        st.setLong(1, locataire.getIdLocataire());
        st.setString(2, FormatUtils.clean(locataire.getNomLoc()));
        st.setString(3, FormatUtils.clean(locataire.getPrenomLoc()));
        st.setString(4, FormatUtils.clean(locataire.getTelLoc()));
        st.setString(5, FormatUtils.clean(locataire.getMailLoc()));
        if (locataire.getDateNaissance() != null) {
            st.setDate(6, java.sql.Date.valueOf(locataire.getDateNaissance()));
        } else {
            st.setNull(6, Types.DATE);
        }
    }

    //  UPDATe
    public static String reqUpdate() {
        return "UPDATE LOCATAIRE SET NOM_LOC=?, PRENOM_LOC=?, TEL_LOC=?, MAIL_LOC=?, DATE_NAISSANCE=? "
             + "WHERE ID_LOCATAIRE=?";
    }

    public static void paramUpdate(PreparedStatement st, Locataire locataire) throws SQLException {
        st.setString(1, FormatUtils.clean(locataire.getNomLoc()));
        st.setString(2, FormatUtils.clean(locataire.getPrenomLoc()));
        st.setString(3, FormatUtils.clean(locataire.getTelLoc()));
        st.setString(4, FormatUtils.clean(locataire.getMailLoc()));
        // DATE_NAISSANCE
        if (locataire.getDateNaissance() != null) {
            st.setDate(5, java.sql.Date.valueOf(locataire.getDateNaissance()));
        } else {
            st.setNull(5, Types.DATE);
        }
        st.setLong(6, locataire.getIdLocataire());
    }

    // DELETE SIGNE 
    public static String reqDeleteSigne() {
        return "DELETE FROM SIGNE WHERE ID_LOCATAIRE=?";
    }

    public static void paramDeleteSigne(PreparedStatement st, Locataire locataire) throws SQLException {
        st.setLong(1, locataire.getIdLocataire());
    }

    //  DELETE 
    public static String reqDelete() {
        return "DELETE FROM LOCATAIRE WHERE ID_LOCATAIRE=?";
    }

    public static void paramDelete(PreparedStatement st, Locataire locataire) throws SQLException {
        st.setLong(1, locataire.getIdLocataire());
    }

    //  FIND BY ID
    public static String reqFindById() {
        return "SELECT * FROM LOCATAIRE WHERE ID_LOCATAIRE=?";
    }

    public static void paramFindById(PreparedStatement st, String id) throws SQLException {
        st.setLong(1, Long.parseLong(id));
    }

    // FIND ALL
    public static String reqFindAll() {
        return "SELECT * FROM LOCATAIRE ORDER BY NOM_LOC, PRENOM_LOC";
    }

    //  FIND ALL AVEC STATUT
    public static String reqFindAllAvecStatut() {
        return "SELECT * FROM V_LOCATAIRES_STATUT ORDER BY NOM_LOC, PRENOM_LOC";
    }

    //  FIND BY STATUT 
    public static String reqFindByStatut() {
        return "SELECT l.* FROM V_LOCATAIRES_STATUT l WHERE l.STATUT = ? ORDER BY NOM_LOC, PRENOM_LOC";
    }

    public static void paramFindByStatut(PreparedStatement st, String statut) throws SQLException {
        st.setString(1, statut.toUpperCase());
    }

    // GET STATUT 
    public static String reqGetStatut() {
        return "{? = call getStatutLocataire(?)}";
    }

    public static void paramGetStatut(CallableStatement cs, long idLocataire) throws SQLException {
        cs.registerOutParameter(1, Types.VARCHAR);
        cs.setLong(2, idLocataire);
    }

    // FIND BY BAIL
    public static String reqFindByBail() {
        return "SELECT l.* FROM LOCATAIRE l "
             + "JOIN SIGNE s ON l.ID_LOCATAIRE = s.ID_LOCATAIRE "
             + "WHERE s.ID_BAIL = ?";
    }

    public static void paramFindByBail(PreparedStatement st, long idBail) throws SQLException {
        st.setLong(1, idBail);
    }

    // fIND SANS BAIL ACTIF 
    public static String reqFindSansBailActif() {
        return "SELECT * FROM LOCATAIRE l "
             + "WHERE NOT EXISTS ("
             + "    SELECT 1 FROM SIGNE s JOIN BAIL b ON s.ID_BAIL = b.ID_BAIL "
             + "    WHERE s.ID_LOCATAIRE = l.ID_LOCATAIRE AND b.ETAT = 'EN_COURS'"
             + ") ORDER BY NOM_LOC, PRENOM_LOC";
    }

    //LIER AU BAIL 
    public static String reqLierAuBail() {
        return "INSERT INTO SIGNE (ID_BAIL, ID_LOCATAIRE) VALUES (?, ?)";
    }

    public static void paramLierAuBail(PreparedStatement st, long idLocataire, long idBail) throws SQLException {
        st.setLong(1, idBail);
        st.setLong(2, idLocataire);
    }

    // DELIER DU BAIL 
    public static String reqDelierDuBail() {
        return "DELETE FROM SIGNE WHERE ID_BAIL = ? AND ID_LOCATAIRE = ?";
    }

    public static void paramDelierDuBail(PreparedStatement st, long idLocataire, long idBail) throws SQLException {
        st.setLong(1, idBail);
        st.setLong(2, idLocataire);
    }
}