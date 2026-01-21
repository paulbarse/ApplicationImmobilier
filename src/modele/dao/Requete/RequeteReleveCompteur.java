package modele.dao.Requete;

import java.sql.*;
import modele.ReleveCompteur;

/**
 * Requêtes SQL pour ReleveCompteurDao
 */
public class RequeteReleveCompteur {

    // RECHERCHE
    
    public static String reqFindByBatiment() {
        return "SELECT r.* FROM RELEVE_COMPTEUR r " +
               "JOIN LOGEMENT l ON r.ID_RELEVE = l.ID_RELEVE " +
               "WHERE l.ID_BATIMENT = ? " +
               "UNION " +
               "SELECT r.* FROM RELEVE_COMPTEUR r " +
               "JOIN GARAGE g ON r.ID_RELEVE = g.ID_RELEVE " +
               "WHERE g.ID_BATIMENT = ? " +
               "ORDER BY 5 DESC";
    }
    
    public static void paramFindByBatiment(PreparedStatement ps, long idBatiment) throws SQLException {
        ps.setLong(1, idBatiment);
        ps.setLong(2, idBatiment);
    }
    
    public static String reqFindById() {
        return "SELECT * FROM RELEVE_COMPTEUR WHERE ID_RELEVE = ?";
    }
    
    public static void paramFindById(PreparedStatement ps, int idReleve) throws SQLException {
        ps.setInt(1, idReleve);
    }

    // CRÉATION
    
    public static String reqCreate() {
        return "INSERT INTO RELEVE_COMPTEUR (ID_RELEVE, UNITE, TYPE, NOUVELLE_INDEX, " +
               "DATE_, NUM_COMPTEUR, ANCIEN_INDEX) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }
    
    public static void paramCreate(PreparedStatement ps, ReleveCompteur releve) throws SQLException {
        ps.setInt(1, releve.getIdReleve());
        ps.setString(2, releve.getUnite());
        ps.setString(3, releve.getType());
        ps.setInt(4, releve.getNouvelIndex());
        ps.setDate(5, new java.sql.Date(releve.getDate().getTime()));
        ps.setInt(6, releve.getNumCompteur());
        ps.setInt(7, releve.getAncienIndex());
    }
    
    public static String reqLierAuLogement() {
        return "UPDATE LOGEMENT SET ID_RELEVE = ? " +
               "WHERE ID_LOGEMENT = (SELECT MIN(ID_LOGEMENT) FROM LOGEMENT WHERE ID_BATIMENT = ? AND ID_RELEVE IS NULL)";
    }
    
    public static void paramLierAuLogement(PreparedStatement ps, int idReleve, long idBatiment) throws SQLException {
        ps.setInt(1, idReleve);
        ps.setLong(2, idBatiment);
    }
    
    public static String reqLierAuGarage() {
        return "UPDATE GARAGE SET ID_RELEVE = ? " +
               "WHERE ID_GARAGE = (SELECT MIN(ID_GARAGE) FROM GARAGE WHERE ID_BATIMENT = ? AND ID_RELEVE IS NULL)";
    }
    
    public static void paramLierAuGarage(PreparedStatement ps, int idReleve, long idBatiment) throws SQLException {
        ps.setInt(1, idReleve);
        ps.setLong(2, idBatiment);
    }

    // MISE À JOUR
    
    public static String reqUpdate() {
        return "UPDATE RELEVE_COMPTEUR SET UNITE = ?, TYPE = ?, NOUVELLE_INDEX = ?, " +
               "DATE_ = ?, NUM_COMPTEUR = ?, ANCIEN_INDEX = ? WHERE ID_RELEVE = ?";
    }
    
    public static void paramUpdate(PreparedStatement ps, ReleveCompteur releve) throws SQLException {
        ps.setString(1, releve.getUnite());
        ps.setString(2, releve.getType());
        ps.setInt(3, releve.getNouvelIndex());
        ps.setDate(4, new java.sql.Date(releve.getDate().getTime()));
        ps.setInt(5, releve.getNumCompteur());
        ps.setInt(6, releve.getAncienIndex());
        ps.setInt(7, releve.getIdReleve());
    }
    
    public static String reqUpdateParams() {
        return "UPDATE RELEVE_COMPTEUR SET TYPE = ?, DATE_ = ?, NOUVELLE_INDEX = ?, " +
               "ANCIEN_INDEX = ?, UNITE = ? WHERE ID_RELEVE = ?";
    }
    
    public static void paramUpdateParams(PreparedStatement ps, int idReleve, String type, 
            java.util.Date date, int nouvelIndex, int ancienIndex, String unite) throws SQLException {
        ps.setString(1, type);
        ps.setDate(2, new java.sql.Date(date.getTime()));
        ps.setInt(3, nouvelIndex);
        ps.setInt(4, ancienIndex);
        ps.setString(5, unite);
        ps.setInt(6, idReleve);
    }

    // SUPPRESSION
    
    public static String reqDelierLogement() {
        return "UPDATE LOGEMENT SET ID_RELEVE = NULL WHERE ID_RELEVE = ?";
    }
    
    public static void paramDelierLogement(PreparedStatement ps, int idReleve) throws SQLException {
        ps.setInt(1, idReleve);
    }
    
    public static String reqDelierGarage() {
        return "UPDATE GARAGE SET ID_RELEVE = NULL WHERE ID_RELEVE = ?";
    }
    
    public static void paramDelierGarage(PreparedStatement ps, int idReleve) throws SQLException {
        ps.setInt(1, idReleve);
    }
    
    public static String reqDelete() {
        return "DELETE FROM RELEVE_COMPTEUR WHERE ID_RELEVE = ?";
    }
    
    public static void paramDelete(PreparedStatement ps, int idReleve) throws SQLException {
        ps.setInt(1, idReleve);
    }

    // UTILITAIRES
    
    public static String reqGetNextId() {
        return "SELECT NVL(MAX(ID_RELEVE), 0) + 1 AS NEXT_ID FROM RELEVE_COMPTEUR";
    }
}
