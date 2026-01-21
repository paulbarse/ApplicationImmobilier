package modele.dao.Requete;

import java.sql.*;
import modele.Assurance;

/**
 * Classe contenant les requêtes SQL pour AssuranceDao
 */
public class RequeteAssurance {

    // --- CREATE ---
    public static String reqCreate() {
        return "INSERT INTO ASSURANCE " +
                "(NUMASSURANCE, TYPE, PRIMEBASE, NOM_COMPAGNIE, DATE_EFFET, " +
                "ID_LOGEMENT, ID_GARAGE, ID_BATIMENT, SIRET) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    public static void paramCreate(PreparedStatement st, Assurance a) throws SQLException {
        st.setLong(1, a.getNumAssurance());
        st.setString(2, a.getType());
        
        if (a.getPrimeBase() != null) st.setDouble(3, a.getPrimeBase());
        else st.setNull(3, Types.NUMERIC);
        
        st.setString(4, a.getNomCompagnie());
        
        if (a.getDateEffet() != null) st.setDate(5, new java.sql.Date(a.getDateEffet().getTime()));
        else st.setNull(5, Types.DATE);
        
        if (a.getIdLogement() != null) st.setLong(6, a.getIdLogement());
        else st.setNull(6, Types.NUMERIC);
        
        if (a.getIdGarage() != null) st.setLong(7, a.getIdGarage());
        else st.setNull(7, Types.NUMERIC);
        
        if (a.getIdBatiment() != null) st.setLong(8, a.getIdBatiment());
        else st.setNull(8, Types.NUMERIC);
        
        if (a.getSiret() != null) st.setLong(9, a.getSiret());
        else st.setNull(9, Types.NUMERIC);
    }

    // --- UPDATE ---
    public static String reqUpdate() {
        return "UPDATE ASSURANCE SET " +
                "TYPE = ?, PRIMEBASE = ?, NOM_COMPAGNIE = ?, DATE_EFFET = ?, " +
                "ID_LOGEMENT = ?, ID_GARAGE = ?, ID_BATIMENT = ?, SIRET = ? " +
                "WHERE NUMASSURANCE = ?";
    }

    public static void paramUpdate(PreparedStatement st, Assurance a) throws SQLException {
        st.setString(1, a.getType());
        
        if (a.getPrimeBase() != null) st.setDouble(2, a.getPrimeBase());
        else st.setNull(2, Types.NUMERIC);
        
        st.setString(3, a.getNomCompagnie());
        
        if (a.getDateEffet() != null) st.setDate(4, new java.sql.Date(a.getDateEffet().getTime()));
        else st.setNull(4, Types.DATE);
        
        if (a.getIdLogement() != null) st.setLong(5, a.getIdLogement());
        else st.setNull(5, Types.NUMERIC);
        
        if (a.getIdGarage() != null) st.setLong(6, a.getIdGarage());
        else st.setNull(6, Types.NUMERIC);
        
        if (a.getIdBatiment() != null) st.setLong(7, a.getIdBatiment());
        else st.setNull(7, Types.NUMERIC);
        
        if (a.getSiret() != null) st.setLong(8, a.getSiret());
        else st.setNull(8, Types.NUMERIC);
        
        st.setLong(9, a.getNumAssurance());
    }

    // --- MODIFIER ASSURANCE (simplifiée) ---
    public static String reqModifierAssurance() {
        return "UPDATE ASSURANCE SET NOM_COMPAGNIE = ?, TYPE = ?, " +
               "PRIMEBASE = ?, DATE_EFFET = ? WHERE NUMASSURANCE = ?";
    }

    public static void paramModifierAssurance(PreparedStatement st, long numAssurance, 
            String nomCompagnie, String type, double primeBase, java.util.Date dateEffet) throws SQLException {
        st.setString(1, nomCompagnie);
        st.setString(2, type);
        st.setDouble(3, primeBase);
        st.setDate(4, new java.sql.Date(dateEffet.getTime()));
        st.setLong(5, numAssurance);
    }

    // --- DELETE (via procédure stockée) ---
    public static String reqDelete() {
        return "{call supprimerAssurance(?)}";
    }

    public static void paramDelete(CallableStatement cs, long numAssurance) throws SQLException {
        cs.setLong(1, numAssurance);
    }

    // --- AJOUTER ASSURANCE BATIMENT (procédure) ---
    public static String reqAjouterAssuranceBatiment() {
        return "{call ajouterAssuranceBatiment(?, ?, ?, ?, ?, ?, ?)}";
    }

    public static void paramAjouterAssuranceBatiment(CallableStatement cs, long idBatiment, 
            long numAssurance, String nomCompagnie, String type, double primeBase, 
            java.util.Date dateEffet, java.util.Date dateEcheance) throws SQLException {
        cs.setLong(1, idBatiment);
        cs.setLong(2, numAssurance);
        cs.setString(3, nomCompagnie);
        cs.setString(4, type);
        cs.setDouble(5, primeBase);
        cs.setDate(6, new java.sql.Date(dateEffet.getTime()));
        if (dateEcheance != null) {
            cs.setDate(7, new java.sql.Date(dateEcheance.getTime()));
        } else {
            cs.setNull(7, Types.DATE);
        }
    }

    // --- FIND BY ID ---
    public static String reqFindById() {
        return "SELECT a.*, e.DATE_ AS DATE_ECHEANCE, e.MONTANT AS MONTANT_ECHEANCE " +
                "FROM ASSURANCE a " +
                "LEFT JOIN ECHEANCE e ON a.NUMASSURANCE = e.NUMASSURANCE " +
                "WHERE a.NUMASSURANCE = ?";
    }

    public static void paramFindById(PreparedStatement st, long numAssurance) throws SQLException {
        st.setLong(1, numAssurance);
    }

    // --- FIND ALL ---
    public static String reqFindAll() {
        return "SELECT a.*, e.DATE_ AS DATE_ECHEANCE, e.MONTANT AS MONTANT_ECHEANCE " +
                "FROM ASSURANCE a " +
                "LEFT JOIN ECHEANCE e ON a.NUMASSURANCE = e.NUMASSURANCE " +
                "ORDER BY a.DATE_EFFET DESC";
    }

    // --- FIND BY LOGEMENT ---
    public static String reqFindByLogement() {
        return "SELECT a.*, e.DATE_ AS DATE_ECHEANCE, e.MONTANT AS MONTANT_ECHEANCE " +
                "FROM ASSURANCE a " +
                "LEFT JOIN ECHEANCE e ON a.NUMASSURANCE = e.NUMASSURANCE " +
                "WHERE a.ID_LOGEMENT = ? " +
                "ORDER BY a.DATE_EFFET DESC";
    }

    public static void paramFindByLogement(PreparedStatement st, long idLogement) throws SQLException {
        st.setLong(1, idLogement);
    }

    // --- FIND BY GARAGE ---
    public static String reqFindByGarage() {
        return "SELECT a.*, e.DATE_ AS DATE_ECHEANCE, e.MONTANT AS MONTANT_ECHEANCE " +
                "FROM ASSURANCE a " +
                "LEFT JOIN ECHEANCE e ON a.NUMASSURANCE = e.NUMASSURANCE " +
                "WHERE a.ID_GARAGE = ? " +
                "ORDER BY a.DATE_EFFET DESC";
    }

    public static void paramFindByGarage(PreparedStatement st, long idGarage) throws SQLException {
        st.setLong(1, idGarage);
    }

    // --- FIND BY BATIMENT ---
    public static String reqFindByBatiment() {
        return "SELECT a.*, e.DATE_ AS DATE_ECHEANCE, e.MONTANT AS MONTANT_ECHEANCE " +
                "FROM ASSURANCE a " +
                "LEFT JOIN ECHEANCE e ON a.NUMASSURANCE = e.NUMASSURANCE " +
                "WHERE a.ID_BATIMENT = ? " +
                "ORDER BY a.DATE_EFFET DESC";
    }

    public static void paramFindByBatiment(PreparedStatement st, long idBatiment) throws SQLException {
        st.setLong(1, idBatiment);
    }

    // --- FIND ALL BY BATIMENT (incluant logements et garages) ---
    public static String reqFindAllByBatiment() {
        return "SELECT a.*, e.DATE_ AS DATE_ECHEANCE, e.MONTANT AS MONTANT_ECHEANCE " +
                "FROM ASSURANCE a " +
                "LEFT JOIN ECHEANCE e ON a.NUMASSURANCE = e.NUMASSURANCE " +
                "WHERE a.ID_BATIMENT = ? " +
                "   OR a.ID_LOGEMENT IN (SELECT ID_LOGEMENT FROM LOGEMENT WHERE ID_BATIMENT = ?) " +
                "   OR a.ID_GARAGE IN (SELECT ID_GARAGE FROM GARAGE WHERE ID_BATIMENT = ?) " +
                "ORDER BY a.DATE_EFFET DESC";
    }

    public static void paramFindAllByBatiment(PreparedStatement st, long idBatiment) throws SQLException {
        st.setLong(1, idBatiment);
        st.setLong(2, idBatiment);
        st.setLong(3, idBatiment);
    }

    // --- GET TOTAL ASSURANCES BATIMENT ---
    public static String reqGetTotalAssurancesBatiment() {
        return "SELECT NVL(SUM(a.PRIMEBASE), 0) FROM ASSURANCE a " +
                "WHERE (a.ID_BATIMENT = ? " +
                "   OR a.ID_LOGEMENT IN (SELECT ID_LOGEMENT FROM LOGEMENT WHERE ID_BATIMENT = ?) " +
                "   OR a.ID_GARAGE IN (SELECT ID_GARAGE FROM GARAGE WHERE ID_BATIMENT = ?)) " +
                "AND EXTRACT(YEAR FROM a.DATE_EFFET) = ?";
    }

    public static void paramGetTotalAssurancesBatiment(PreparedStatement st, long idBatiment, int annee) throws SQLException {
        st.setLong(1, idBatiment);
        st.setLong(2, idBatiment);
        st.setLong(3, idBatiment);
        st.setInt(4, annee);
    }

    // --- FIND EXPIRANT BIENTOT ---
    public static String reqFindExpirantBientot() {
        return "SELECT a.*, e.DATE_ AS DATE_ECHEANCE, e.MONTANT AS MONTANT_ECHEANCE " +
                "FROM ASSURANCE a " +
                "JOIN ECHEANCE e ON a.NUMASSURANCE = e.NUMASSURANCE " +
                "WHERE e.DATE_ BETWEEN SYSDATE AND SYSDATE + ? " +
                "ORDER BY e.DATE_";
    }

    public static void paramFindExpirantBientot(PreparedStatement st, int jours) throws SQLException {
        st.setInt(1, jours);
    }

    // --- GET NEXT ID (fonction PL/SQL) ---
    public static String reqGetNextId() {
        return "{? = call getNextId(?, ?)}";
    }

    public static void paramGetNextId(CallableStatement cs) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setString(2, "ASSURANCE");
        cs.setString(3, "NUMASSURANCE");
    }

    // --- ECHEANCES ---
    public static String reqAjouterEcheance() {
        return "INSERT INTO ECHEANCE (NUMASSURANCE, DATE_, MONTANT) VALUES (?, ?, ?)";
    }

    public static void paramAjouterEcheance(PreparedStatement st, long numAssurance, 
            java.util.Date dateEcheance, Double montant) throws SQLException {
        st.setLong(1, numAssurance);
        st.setDate(2, new java.sql.Date(dateEcheance.getTime()));
        if (montant != null) st.setDouble(3, montant);
        else st.setNull(3, Types.NUMERIC);
    }

    public static String reqSupprimerEcheances() {
        return "DELETE FROM ECHEANCE WHERE NUMASSURANCE = ?";
    }

    public static void paramSupprimerEcheances(PreparedStatement st, long numAssurance) throws SQLException {
        st.setLong(1, numAssurance);
    }
}
