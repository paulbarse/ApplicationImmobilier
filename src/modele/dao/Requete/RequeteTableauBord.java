package modele.dao.Requete;

import java.sql.*;

/**
 * Requêtes SQL pour TableauBordDao
 */
public class RequeteTableauBord {

    // FONCTIONS PL/SQL - STATISTIQUES
    
    public static String reqGetNbProprietes() {
        return "{? = call getNbProprietes}";
    }
    
    public static void paramGetNbProprietes(CallableStatement cs) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
    }
    
    public static String reqGetNbLocatairesActifs() {
        return "{? = call getNbLocatairesActifs}";
    }
    
    public static void paramGetNbLocatairesActifs(CallableStatement cs) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
    }
    
    public static String reqGetNbContratsEnCours() {
        return "{? = call getNbContratsEnCours}";
    }
    
    public static void paramGetNbContratsEnCours(CallableStatement cs) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
    }
    
    public static String reqGetRevenuMensuel() {
        return "{? = call getRevenuMensuel}";
    }
    
    public static void paramGetRevenuMensuel(CallableStatement cs) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
    }

    // REQUÊTES SQL - VARIATIONS
    
    public static String reqGetVariationLocataires() {
        return "SELECT COUNT(DISTINCT s.ID_LOCATAIRE) AS nb " +
               "FROM SIGNE s " +
               "JOIN BAIL b ON s.ID_BAIL = b.ID_BAIL " +
               "WHERE b.DATE_DEBUT >= TRUNC(SYSDATE, 'IW') " +
               "AND b.ETAT = 'EN_COURS'";
    }
    
    public static String reqGetNouveauxContratsCeMois() {
        return "SELECT COUNT(*) AS nb FROM BAIL " +
               "WHERE DATE_DEBUT >= TRUNC(SYSDATE, 'MM') AND ETAT = 'EN_COURS'";
    }
    
    public static String reqGetContratsTerminesCeMois() {
        return "SELECT COUNT(*) AS nb FROM BAIL " +
               "WHERE DATE_FIN >= TRUNC(SYSDATE, 'MM') AND ETAT IN ('RESILIE', 'CLOTURE')";
    }
    
    public static String reqGetRevenuMoisPrecedent() {
        return "SELECT NVL(SUM(MONTANT_LOYER + NVL(MONTANT_PROVISION, 0)), 0) AS total " +
               "FROM LOYER " +
               "WHERE UPPER(STATUT) = 'PAYE' " +
               "AND TO_CHAR(DATE_PAIEMENT, 'MM/YYYY') = TO_CHAR(ADD_MONTHS(SYSDATE, -1), 'MM/YYYY')";
    }

    // REQUÊTES SQL - PROPRIÉTÉS
    
    public static String reqGetDerniersLogements() {
        return "SELECT * FROM (" +
               "SELECT " +
               "    'LOGEMENT' AS type_bien, " +
               "    l.ID_LOGEMENT AS id_bien, " +
               "    l.ADRESSE_LOGEMENT AS adresse, " +
               "    l.TYPE_LOGEMENT AS sous_type, " +
               "    CASE WHEN l.ID_BAIL IS NOT NULL THEN 'Occupe' ELSE 'Libre' END AS statut, " +
               "    b.LOYER_INITIAL AS loyer, " +
               "    (SELECT loc.NOM_LOC || ' ' || loc.PRENOM_LOC " +
               "     FROM SIGNE s JOIN LOCATAIRE loc ON s.ID_LOCATAIRE = loc.ID_LOCATAIRE " +
               "     WHERE s.ID_BAIL = l.ID_BAIL AND ROWNUM = 1) AS locataire " +
               "FROM LOGEMENT l " +
               "LEFT JOIN BAIL b ON l.ID_BAIL = b.ID_BAIL AND b.ETAT = 'EN_COURS' " +
               "ORDER BY l.ID_LOGEMENT DESC" +
               ") WHERE ROWNUM <= ?";
    }
    
    public static void paramGetDerniersLogements(PreparedStatement ps, int limit) throws SQLException {
        ps.setInt(1, limit);
    }
    
    public static String reqGetDerniersGarages() {
        return "SELECT * FROM (" +
               "SELECT " +
               "    'GARAGE' AS type_bien, " +
               "    g.ID_GARAGE AS id_bien, " +
               "    g.ADRESSE_GARAGE AS adresse, " +
               "    'Garage' AS sous_type, " +
               "    CASE WHEN g.ID_BAIL IS NOT NULL THEN 'Occupe' ELSE 'Libre' END AS statut, " +
               "    b.LOYER_INITIAL AS loyer, " +
               "    (SELECT loc.NOM_LOC || ' ' || loc.PRENOM_LOC " +
               "     FROM SIGNE s JOIN LOCATAIRE loc ON s.ID_LOCATAIRE = loc.ID_LOCATAIRE " +
               "     WHERE s.ID_BAIL = g.ID_BAIL AND ROWNUM = 1) AS locataire " +
               "FROM GARAGE g " +
               "LEFT JOIN BAIL b ON g.ID_BAIL = b.ID_BAIL AND b.ETAT = 'EN_COURS' " +
               "ORDER BY g.ID_GARAGE DESC" +
               ") WHERE ROWNUM <= ?";
    }
    
    public static void paramGetDerniersGarages(PreparedStatement ps, int limit) throws SQLException {
        ps.setInt(1, limit);
    }

    // REQUÊTES SQL - ACTIVITÉS RÉCENTES
    
    public static String reqTableExists() {
        return "SELECT COUNT(*) AS nb FROM USER_TABLES WHERE TABLE_NAME = ?";
    }
    
    public static void paramTableExists(PreparedStatement ps, String tableName) throws SQLException {
        ps.setString(1, tableName.toUpperCase());
    }
    
    public static String reqGetActivitesRecentes() {
        return "SELECT * FROM (" +
               "SELECT " +
               "    id_activite, type_activite, description_activite, date_activite, " +
               "    entite_type, entite_id, montant, adresse, " +
               "    CASE " +
               "        WHEN date_activite > SYSDATE - 1/24 THEN " +
               "            'Il y a ' || ROUND((SYSDATE - CAST(date_activite AS DATE)) * 24 * 60) || ' min' " +
               "        WHEN date_activite > SYSDATE - 1 THEN " +
               "            'Il y a ' || ROUND((SYSDATE - CAST(date_activite AS DATE)) * 24) || ' heures' " +
               "        WHEN date_activite > SYSDATE - 2 THEN " +
               "            'Hier' " +
               "        WHEN date_activite > SYSDATE - 7 THEN " +
               "            'Il y a ' || ROUND(SYSDATE - CAST(date_activite AS DATE)) || ' jours' " +
               "        ELSE " +
               "            TO_CHAR(date_activite, 'DD/MM/YYYY') " +
               "    END AS temps_ecoule " +
               "FROM HISTORIQUE_ACTIVITE " +
               "ORDER BY date_activite DESC" +
               ") WHERE ROWNUM <= ?";
    }
    
    public static void paramGetActivitesRecentes(PreparedStatement ps, int limit) throws SQLException {
        ps.setInt(1, limit);
    }
}
