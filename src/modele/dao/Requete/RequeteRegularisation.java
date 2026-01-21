package modele.dao.Requete;

import java.sql.*;

/**
 * Requêtes SQL pour RegularisationDao
 */
public class RequeteRegularisation {

    // REQUÊTE : Récupérer tous les baux actifs ou terminés dans l'année
    
    public static String reqFindBauxPourRegularisation() {
        return "SELECT DISTINCT b.ID_BAIL, " +
               "       l.ID_LOCATAIRE, " +
               "       l.NOM_LOC || ' ' || NVL(l.PRENOM_LOC, '') AS NOM_COMPLET, " +
               "       COALESCE(log.ID_BATIMENT, g.ID_BATIMENT) AS ID_BATIMENT, " +
               "       COALESCE(bat.ADRESSE, 'Non renseigné') AS ADRESSE_BAT " +
               "FROM BAIL b " +
               "JOIN SIGNE s ON b.ID_BAIL = s.ID_BAIL " +
               "JOIN LOCATAIRE l ON s.ID_LOCATAIRE = l.ID_LOCATAIRE " +
               "LEFT JOIN LOGEMENT log ON log.ID_BAIL = b.ID_BAIL " +
               "LEFT JOIN GARAGE g ON g.ID_BAIL = b.ID_BAIL " +
               "LEFT JOIN BATIMENT bat ON bat.ID_BATIMENT = COALESCE(log.ID_BATIMENT, g.ID_BATIMENT) " +
               "WHERE b.ETAT = 'EN_COURS' " +
               "   OR (b.DATE_FIN IS NOT NULL AND EXTRACT(YEAR FROM b.DATE_FIN) = ?) " +
               "ORDER BY 3";
    }
    
    public static void paramFindBauxPourRegularisation(PreparedStatement ps, int annee) throws SQLException {
        ps.setInt(1, annee);
    }

    // REQUÊTE : Récupérer l'adresse d'un bâtiment
    
    public static String reqFindAdresseBatiment() {
        return "SELECT ADRESSE FROM BATIMENT WHERE ID_BATIMENT = ?";
    }
    
    public static void paramFindAdresseBatiment(PreparedStatement ps, long idBatiment) throws SQLException {
        ps.setLong(1, idBatiment);
    }

    // REQUÊTE : Récupérer l'ID du bâtiment depuis un bail (via logement)
    
    public static String reqFindBatimentFromBailViaLogement() {
        return "SELECT ID_BATIMENT FROM LOGEMENT WHERE ID_BAIL = ? AND ROWNUM = 1";
    }
    
    public static void paramFindBatimentFromBailViaLogement(PreparedStatement ps, long idBail) throws SQLException {
        ps.setLong(1, idBail);
    }

    // REQUÊTE : Récupérer l'ID du bâtiment depuis un bail (via garage)
    
    public static String reqFindBatimentFromBailViaGarage() {
        return "SELECT ID_BATIMENT FROM GARAGE WHERE ID_BAIL = ? AND ROWNUM = 1";
    }
    
    public static void paramFindBatimentFromBailViaGarage(PreparedStatement ps, long idBail) throws SQLException {
        ps.setLong(1, idBail);
    }

    // REQUÊTE : Récupérer les dates d'un bail
    
    public static String reqFindDatesBail() {
        return "SELECT DATE_DEBUT, NVL(DATE_FIN, SYSDATE) AS DATE_FIN FROM BAIL WHERE ID_BAIL = ?";
    }
    
    public static void paramFindDatesBail(PreparedStatement ps, long idBail) throws SQLException {
        ps.setLong(1, idBail);
    }

    // REQUÊTE : Total des loyers payés pour un bail et une année
    
    public static String reqTotalLoyersBail() {
        return "SELECT NVL(SUM(NVL(MONTANT_LOYER, 0)), 0) AS TOTAL FROM LOYER " +
               "WHERE ID_BAIL = ? " +
               "AND UPPER(TRIM(STATUT)) = 'PAYE' " +
               "AND (TRIM(MOIS) LIKE '%/' || ? OR TRIM(MOIS) LIKE '%/' || ?)";
    }
    
    public static void paramTotalLoyersBail(PreparedStatement ps, long idBail, int annee) throws SQLException {
        ps.setLong(1, idBail);
        ps.setInt(2, annee);
        ps.setString(3, String.valueOf(annee % 100)); 
    }

    // REQUÊTE : Total des provisions payées pour un bail et une année
  
    
    public static String reqTotalProvisionsBail() {
        return "SELECT NVL(SUM(NVL(MONTANT_PROVISION, 0)), 0) AS TOTAL FROM LOYER " +
               "WHERE ID_BAIL = ? " +
               "AND UPPER(TRIM(STATUT)) = 'PAYE' " +
               "AND (TRIM(MOIS) LIKE '%/' || ? OR TRIM(MOIS) LIKE '%/' || ?)";
    }
    
    public static void paramTotalProvisionsBail(PreparedStatement ps, long idBail, int annee) throws SQLException {
        ps.setLong(1, idBail);
        ps.setInt(2, annee);
        ps.setString(3, String.valueOf(annee % 100));
    }

    // REQUÊTE : Récupérer les provisions initiales d'un bail
    
    public static String reqProvisionInitialesBail() {
        return "SELECT NVL(PROVISION_INITIALES, 0) AS PROV FROM BAIL WHERE ID_BAIL = ?";
    }
    
    public static void paramProvisionInitialesBail(PreparedStatement ps, long idBail) throws SQLException {
        ps.setLong(1, idBail);
    }

    // REQUÊTE : Total des charges par type pour un bâtiment
    
    public static String reqChargesBatimentParType() {
        return "SELECT NVL(SUM(c.MONTANT * NVL(c.PCT_RECUPERABLE, 100) / 100), 0) AS TOTAL " +
               "FROM CHARGES c " +
               "WHERE c.ID_BATIMENT = ? " +
               "AND EXTRACT(YEAR FROM c.DATE_CHARGE) = ? " +
               "AND UPPER(c.TYPE_CHARGES) = UPPER(?)";
    }
    
    public static void paramChargesBatimentParType(PreparedStatement ps, long idBatiment, int annee, String type) throws SQLException {
        ps.setLong(1, idBatiment);
        ps.setInt(2, annee);
        ps.setString(3, type);
    }

    // PROCÉDURE PL/SQL : Appel de getRecapRegularisation
    
    public static String reqCallRecapRegularisation() {
        return "{call getRecapRegularisation(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    }
    
    public static void paramCallRecapRegularisation(CallableStatement cs, long idBail, int annee) throws SQLException {
        // Paramètres d'entrée
        cs.setLong(1, idBail);
        cs.setInt(2, annee);
        
        // Paramètres de sortie
        cs.registerOutParameter(3, Types.NUMERIC);  // nb_mois
        cs.registerOutParameter(4, Types.NUMERIC);  // total_loyers
        cs.registerOutParameter(5, Types.NUMERIC);  // total_provisions
        cs.registerOutParameter(6, Types.NUMERIC);  // charges_eau
        cs.registerOutParameter(7, Types.NUMERIC);  // charges_om
        cs.registerOutParameter(8, Types.NUMERIC);  // charges_elec
        cs.registerOutParameter(9, Types.NUMERIC);  // charges_entretien
        cs.registerOutParameter(10, Types.NUMERIC); // total_charges
        cs.registerOutParameter(11, Types.NUMERIC); // regularisation
        cs.registerOutParameter(12, Types.NUMERIC); // nouvelle_provision
        cs.registerOutParameter(13, Types.NUMERIC); // id_batiment
        cs.registerOutParameter(14, Types.VARCHAR); // adresse
    }

    // FONCTION PL/SQL : getNbMoisOccupation
    
    public static String reqCallNbMoisOccupation() {
        return "{? = call getNbMoisOccupation(?, ?)}";
    }
    
    public static void paramCallNbMoisOccupation(CallableStatement cs, long idBail, int annee) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setLong(2, idBail);
        cs.setInt(3, annee);
    }

    // FONCTION PL/SQL : getChargesLocataire
    
    public static String reqCallChargesLocataire() {
        return "{? = call getChargesLocataire(?, ?, ?, ?)}";
    }
    
    public static void paramCallChargesLocataire(CallableStatement cs, long idBail, long idBatiment, int annee, String type) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setLong(2, idBail);
        cs.setLong(3, idBatiment);
        cs.setInt(4, annee);
        cs.setString(5, type);
    }

    // FONCTION PL/SQL : getRegularisationLocataire
    
    public static String reqCallRegularisationLocataire() {
        return "{? = call getRegularisationLocataire(?, ?, ?)}";
    }
    
    public static void paramCallRegularisationLocataire(CallableStatement cs, long idBail, long idBatiment, int annee) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setLong(2, idBail);
        cs.setLong(3, idBatiment);
        cs.setInt(4, annee);
    }

    // REQUÊTE : Récupérer les bâtiments avec charges pour une année
    
    public static String reqFindBatimentsAvecCharges() {
        return "SELECT DISTINCT b.ID_BATIMENT, b.ADRESSE " +
               "FROM BATIMENT b " +
               "WHERE EXISTS (SELECT 1 FROM CHARGES c WHERE c.ID_BATIMENT = b.ID_BATIMENT AND EXTRACT(YEAR FROM c.DATE_CHARGE) = ?) " +
               "   OR EXISTS (SELECT 1 FROM LOGEMENT l JOIN BAIL ba ON l.ID_BAIL = ba.ID_BAIL " +
               "              WHERE l.ID_BATIMENT = b.ID_BATIMENT AND ba.ETAT = 'EN_COURS') " +
               "ORDER BY b.ADRESSE";
    }
    
    public static void paramFindBatimentsAvecCharges(PreparedStatement ps, int annee) throws SQLException {
        ps.setInt(1, annee);
    }
}