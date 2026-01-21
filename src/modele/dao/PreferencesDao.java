package modele.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import modele.dao.Requete.RequetePreferences;
import modele.Preferences;

/**
 * DAO pour la table PREFERENCES
 */
public class PreferencesDao {
    private Connection cn;

    public PreferencesDao() {
        try {
            UtOracleDataSource.creerAcces();
            this.cn = UtOracleDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Récupère les préférences d'un utilisateur
     */
    public Preferences findById(long idLocataire) {
        try (PreparedStatement st = this.cn.prepareStatement(RequetePreferences.reqFindById())) {
            RequetePreferences.paramFindById(st, idLocataire);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new Preferences(
                    rs.getLong("ID_LOCATAIRE"), 
                    rs.getInt("MODE_SOMBRE") == 1,
                    rs.getInt("NOTIF_EMAIL") == 1, 
                    rs.getInt("RAPPEL_LOYER") == 1
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sauvegarde (Update ou Insert)
     */
    public void save(Preferences p) {
        try (PreparedStatement st = this.cn.prepareStatement(RequetePreferences.reqUpdate())) {
            RequetePreferences.paramUpdate(st, p);
            int count = st.executeUpdate();

            // Si aucune ligne n'a été mise à jour, on crée la ligne
            if (count == 0) {
                this.insert(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insert(Preferences p) throws SQLException {
        try (PreparedStatement st = this.cn.prepareStatement(RequetePreferences.reqInsert())) {
            RequetePreferences.paramInsert(st, p);
            st.executeUpdate();
        }
    }

    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) {
            cn.close();
        }
    }
}
