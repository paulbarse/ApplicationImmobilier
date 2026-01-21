package modele.dao.Requete;

import java.sql.*;
import modele.Preferences;

/**
 * Classe contenant les requêtes SQL pour PreferencesDao
 */
public class RequetePreferences {

    // FIND BY ID 
    public static String reqFindById() {
        return "SELECT * FROM PREFERENCES WHERE ID_LOCATAIRE = ?";
    }

    public static void paramFindById(PreparedStatement st, long idLocataire) throws SQLException {
        st.setLong(1, idLocataire);
    }

    //  UPDATE 
    public static String reqUpdate() {
        return "UPDATE PREFERENCES SET MODE_SOMBRE=?, NOTIF_EMAIL=?, RAPPEL_LOYER=? WHERE ID_LOCATAIRE=?";
    }

    public static void paramUpdate(PreparedStatement st, Preferences p) throws SQLException {
        st.setInt(1, p.isModeSombre() ? 1 : 0);
        st.setInt(2, p.isNotifEmail() ? 1 : 0);
        st.setInt(3, p.isRappelLoyer() ? 1 : 0);
        st.setLong(4, p.getIdLocataire());
    }

    // INSERT 
    public static String reqInsert() {
        return "INSERT INTO PREFERENCES (ID_LOCATAIRE, MODE_SOMBRE, NOTIF_EMAIL, RAPPEL_LOYER) VALUES (?, ?, ?, ?)";
    }

    public static void paramInsert(PreparedStatement st, Preferences p) throws SQLException {
        st.setLong(1, p.getIdLocataire());
        st.setInt(2, p.isModeSombre() ? 1 : 0);
        st.setInt(3, p.isNotifEmail() ? 1 : 0);
        st.setInt(4, p.isRappelLoyer() ? 1 : 0);
    }
}
