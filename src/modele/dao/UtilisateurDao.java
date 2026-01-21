package modele.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import modele.dao.Requete.RequeteUtilisateur;
import modele.Utilisateur;

/**
 * DAO pour la table UTILISATEUR 
 */
public class UtilisateurDao {
    private Connection cn;

    public UtilisateurDao() {
        try {
            UtOracleDataSource.creerAcces();
            this.cn = UtOracleDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean create(Utilisateur u) {
        try (PreparedStatement st = this.cn.prepareStatement(RequeteUtilisateur.reqCreate())) {
            RequeteUtilisateur.paramCreate(st, u);
            st.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Utilisateur login(String email, String motDePasse) {

        try (PreparedStatement st = this.cn.prepareStatement(RequeteUtilisateur.reqLogin())) {
            RequeteUtilisateur.paramLogin(st, email, motDePasse);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new Utilisateur(
                        rs.getLong("ID_UTILISATEUR"), 
                        rs.getString("NOM"), 
                        rs.getString("PRENOM"),
                        rs.getString("EMAIL"), 
                        rs.getString("MOT_DE_PASSE"), 
                        rs.getDate("DATE_NAISSANCE")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * SUPPRESSION DU COMPTE UTILISATEUR UNIQUEMENT
     */
    public boolean delete(long idUtilisateur) {
        try {
            // A. On supprime les préférences
            try (PreparedStatement st = this.cn.prepareStatement(RequeteUtilisateur.reqDeletePreferences())) {
                RequeteUtilisateur.paramDeletePreferences(st, idUtilisateur);
                st.executeUpdate();
            }

            // B. On essaie de supprimer le profil Locataire correspondant
            try (PreparedStatement st = this.cn.prepareStatement(RequeteUtilisateur.reqDeleteLocataire())) {
                RequeteUtilisateur.paramDeleteLocataire(st, idUtilisateur);
                st.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Info : Pas de profil locataire supprimé ou profil lié à un bail.");
            }

            // C. On essaie de supprimer le profil Propriétaire correspondant
            try (PreparedStatement st = this.cn.prepareStatement(RequeteUtilisateur.reqDeleteProprietaire())) {
                RequeteUtilisateur.paramDeleteProprietaire(st, idUtilisateur);
                st.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Info : Pas de profil propriétaire supprimé.");
            }

            // D.  on supprime l'utilisateur
            try (PreparedStatement st = this.cn.prepareStatement(RequeteUtilisateur.reqDelete())) {
                RequeteUtilisateur.paramDelete(st, idUtilisateur);
                int lignes = st.executeUpdate();
                return lignes > 0;
            }

        } catch (SQLException e) {
            System.err.println("ERREUR SUPPRESSION : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public boolean update(Utilisateur u) {
        try (PreparedStatement st = this.cn.prepareStatement(RequeteUtilisateur.reqUpdate())) {
            RequeteUtilisateur.paramUpdate(st, u);
            int lignes = st.executeUpdate();
            return lignes > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) {
            cn.close();
        }
    }
}
