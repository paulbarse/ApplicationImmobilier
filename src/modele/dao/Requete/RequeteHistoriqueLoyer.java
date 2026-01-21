package modele.dao.Requete;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Requetes SQL pour HistoriqueLoyerDao.
 */
public class RequeteHistoriqueLoyer {

    //  FIND ALL 
    
    public static String reqFindAll() {
        return "SELECT ID_HISTORIQUE, ID_BAIL, ID_LOGEMENT, ID_GARAGE, NOM_BIEN, " +
               "NOM_LOCATAIRE, MOIS, ANNEE, MONTANT_LOYER, MONTANT_PROVISION, " +
               "DATE_PAIEMENT, QUITTANCE_GENEREE, DATE_CLOTURE, ID_BATIMENT " +
               "FROM HISTORIQUE_LOYERS ORDER BY DATE_CLOTURE DESC, ANNEE DESC, MOIS DESC";
    }

    // FIND BY ANNEE 
    
    public static String reqFindByAnnee() {
        return "SELECT ID_HISTORIQUE, ID_BAIL, ID_LOGEMENT, ID_GARAGE, NOM_BIEN, " +
               "NOM_LOCATAIRE, MOIS, ANNEE, MONTANT_LOYER, MONTANT_PROVISION, " +
               "DATE_PAIEMENT, QUITTANCE_GENEREE, DATE_CLOTURE, ID_BATIMENT " +
               "FROM HISTORIQUE_LOYERS WHERE ANNEE = ? " +
               "ORDER BY MOIS DESC";
    }

    public static void paramFindByAnnee(PreparedStatement ps, int annee) throws SQLException {
        ps.setInt(1, annee);
    }

    // FIND BY BAIL 
    
    public static String reqFindByBail() {
        return "SELECT ID_HISTORIQUE, ID_BAIL, ID_LOGEMENT, ID_GARAGE, NOM_BIEN, " +
               "NOM_LOCATAIRE, MOIS, ANNEE, MONTANT_LOYER, MONTANT_PROVISION, " +
               "DATE_PAIEMENT, QUITTANCE_GENEREE, DATE_CLOTURE, ID_BATIMENT " +
               "FROM HISTORIQUE_LOYERS WHERE ID_BAIL = ? " +
               "ORDER BY ANNEE DESC, MOIS DESC";
    }

    public static void paramFindByBail(PreparedStatement ps, long idBail) throws SQLException {
        ps.setLong(1, idBail);
    }

    // TOTAUX PAR ANNEE 
    
    public static String reqGetTotauxByAnnee() {
        return "SELECT NVL(SUM(MONTANT_LOYER), 0) AS TOTAL_LOYERS, " +
               "NVL(SUM(MONTANT_PROVISION), 0) AS TOTAL_PROVISIONS, " +
               "COUNT(*) AS NB_LOYERS " +
               "FROM HISTORIQUE_LOYERS WHERE ANNEE = ?";
    }

    public static void paramGetTotauxByAnnee(PreparedStatement ps, int annee) throws SQLException {
        ps.setInt(1, annee);
    }

    //TOTAUX PAR BATIMENT ET ANNEE 
    
    public static String reqGetTotauxByBatimentAndAnnee() {
        return "SELECT NVL(SUM(MONTANT_LOYER), 0) AS TOTAL_LOYERS, " +
               "NVL(SUM(MONTANT_PROVISION), 0) AS TOTAL_PROVISIONS " +
               "FROM HISTORIQUE_LOYERS WHERE ID_BATIMENT = ? AND ANNEE = ?";
    }

    public static void paramGetTotauxByBatimentAndAnnee(PreparedStatement ps, long idBatiment, int annee) throws SQLException {
        ps.setLong(1, idBatiment);
        ps.setInt(2, annee);
    }

    //  RESUME PAR BIEN (pour affichage)
    
    public static String reqGetResumeParBien() {
        return "SELECT NOM_BIEN, NOM_LOCATAIRE, ANNEE, ID_BAIL, " +
               "SUM(MONTANT_LOYER) AS TOTAL_LOYERS, " +
               "SUM(NVL(MONTANT_PROVISION, 0)) AS TOTAL_PROVISIONS, " +
               "COUNT(*) AS NB_MOIS, " +
               "SUM(CASE WHEN QUITTANCE_GENEREE = 'O' THEN 1 ELSE 0 END) AS NB_QUITTANCES, " +
               "MAX(DATE_CLOTURE) AS DATE_CLOTURE " +
               "FROM HISTORIQUE_LOYERS " +
               "GROUP BY NOM_BIEN, NOM_LOCATAIRE, ANNEE, ID_BAIL " +
               "ORDER BY DATE_CLOTURE DESC, ANNEE DESC";
    }

    //  STATISTIQUES COMBINEES 
    
    public static String reqGetStatistiquesParBien() {
        return "SELECT NOM_BIEN, ID_BATIMENT, " +
               "SUM(LOYER_PAYE) AS TOTAL_LOYERS, " +
               "SUM(PROVISION_PAYEE) AS TOTAL_PROVISIONS " +
               "FROM V_STATISTIQUES_LOYERS " +
               "WHERE ANNEE = ? " +
               "GROUP BY NOM_BIEN, ID_BATIMENT " +
               "ORDER BY NOM_BIEN";
    }

    public static void paramGetStatistiquesParBien(PreparedStatement ps, int annee) throws SQLException {
        ps.setInt(1, annee);
    }
}