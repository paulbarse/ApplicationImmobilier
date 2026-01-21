package modele.dao.Requete;

import java.sql.*;
import modele.Utilisateur;

/**
 * Classe contenant les requêtes SQL pour UtilisateurDao
 */
public class RequeteUtilisateur {

    // CREATE 
    public static String reqCreate() {
        return "INSERT INTO UTILISATEUR (ID_UTILISATEUR, NOM, PRENOM, EMAIL, MOT_DE_PASSE, DATE_NAISSANCE) "
             + "VALUES (SEQ_UTILISATEUR.NEXTVAL, ?, ?, ?, ?, ?)";
    }

    public static void paramCreate(PreparedStatement st, Utilisateur u) throws SQLException {
        st.setString(1, u.getNom());
        st.setString(2, u.getPrenom());
        st.setString(3, u.getEmail());
        st.setString(4, u.getMotDePasse());
        st.setDate(5, u.getDateNaissance());
    }

    //LOGIN 
    public static String reqLogin() {
        return "SELECT * FROM UTILISATEUR WHERE EMAIL = ? AND MOT_DE_PASSE = ?";
    }

    public static void paramLogin(PreparedStatement st, String email, String motDePasse) throws SQLException {
        st.setString(1, email);
        st.setString(2, motDePasse);
    }

    // DELETE PREFERENCES
    public static String reqDeletePreferences() {
        return "DELETE FROM PREFERENCES WHERE ID_LOCATAIRE = ?";
    }

    public static void paramDeletePreferences(PreparedStatement st, long idUtilisateur) throws SQLException {
        st.setLong(1, idUtilisateur);
    }

    //  DELETE LOCATAIRE 
    public static String reqDeleteLocataire() {
        return "DELETE FROM LOCATAIRE WHERE ID_LOCATAIRE = ?";
    }

    public static void paramDeleteLocataire(PreparedStatement st, long idUtilisateur) throws SQLException {
        st.setLong(1, idUtilisateur);
    }

    //  DELETE PROPRIETAIRE 
    public static String reqDeleteProprietaire() {
        return "DELETE FROM PROPRIETAIRE WHERE ID_PROPIETAIRE = ?";
    }

    public static void paramDeleteProprietaire(PreparedStatement st, long idUtilisateur) throws SQLException {
        st.setLong(1, idUtilisateur);
    }

    //  DELETE
    public static String reqDelete() {
        return "DELETE FROM UTILISATEUR WHERE ID_UTILISATEUR = ?";
    }

    public static void paramDelete(PreparedStatement st, long idUtilisateur) throws SQLException {
        st.setLong(1, idUtilisateur);
    }

    // UPDATE 
    public static String reqUpdate() {
        return "UPDATE UTILISATEUR SET NOM=?, PRENOM=?, EMAIL=?, MOT_DE_PASSE=? WHERE ID_UTILISATEUR=?";
    }

    public static void paramUpdate(PreparedStatement st, Utilisateur u) throws SQLException {
        st.setString(1, u.getNom());
        st.setString(2, u.getPrenom());
        st.setString(3, u.getEmail());
        st.setString(4, u.getMotDePasse());
        st.setLong(5, u.getId());
    }
}
