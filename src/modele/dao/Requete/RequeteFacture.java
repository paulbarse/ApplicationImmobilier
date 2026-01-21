package modele.dao.Requete;

import java.sql.*;
import modele.Facture;

/**
 * Classe contenant les requetes SQL pour FactureDao
 */
public class RequeteFacture {

    // CREATE 
    public static String reqCreate() {
        return "INSERT INTO FACTURE (" +
               "ID_FACTURE, ACCOMPTE, NATURE, PERIODE_DEB, DATE_EMISSION, " +
               "MONTANT_HT, MONTANT_TTC, MONTANT_TEOM, RECUPERABLE_LOCATAIRE, DEDUCTIBLE_IMPOT, " +
               "PERIODE_FIN, TRAVAUX, DATEDEVIS, MONTANTDEVIS, SIRET, ID_CHARGE, ID_BATIMENT, " +
               "STATUT_PAIEMENT, ID_BAIL, ID_LOGEMENT" +
               ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    public static void paramCreate(PreparedStatement st, Facture f) throws SQLException {
        st.setLong(1, f.getIdFactureLong());

        if (f.getAcompte() != null) st.setDouble(2, f.getAcompte());
        else st.setNull(2, Types.NUMERIC);

        st.setString(3, f.getNature());
        st.setString(4, f.getPeriodeDeb());
        st.setDate(5, new java.sql.Date(f.getDateEmission().getTime()));

        if (f.getMontantHT() != null) st.setDouble(6, f.getMontantHT());
        else st.setNull(6, Types.NUMERIC);

        st.setDouble(7, f.getMontantTTC());

        if (f.getMontantTEOM() != null) st.setDouble(8, f.getMontantTEOM());
        else st.setNull(8, Types.NUMERIC);

        if (f.isRecuperableLocataire() != null) st.setInt(9, f.isRecuperableLocataire() ? 1 : 0);
        else st.setNull(9, Types.INTEGER);

        if (f.isDeductibleImpot() != null) st.setInt(10, f.isDeductibleImpot() ? 1 : 0);
        else st.setNull(10, Types.INTEGER);

        if (f.getPeriodeFin() != null) st.setDate(11, new java.sql.Date(f.getPeriodeFin().getTime()));
        else st.setNull(11, Types.DATE);

        st.setString(12, f.getTravaux());

        if (f.getDateDevis() != null) st.setDate(13, new java.sql.Date(f.getDateDevis().getTime()));
        else st.setNull(13, Types.DATE);

        if (f.getMontantDevis() != null) st.setDouble(14, f.getMontantDevis());
        else st.setNull(14, Types.NUMERIC);

        st.setLong(15, f.getSiret());

        if (f.getIdCharge() != null) st.setLong(16, f.getIdCharge());
        else st.setNull(16, Types.NUMERIC);

        if (f.getIdBatiment() != null) st.setLong(17, f.getIdBatiment());
        else st.setNull(17, Types.NUMERIC);

        st.setString(18, (f.getStatut() != null ? f.getStatut() : "A payer"));

        if (f.getIdBail() != null) st.setLong(19, f.getIdBail());
        else st.setNull(19, Types.NUMERIC);

        if (f.getIdLogement() != null) st.setLong(20, f.getIdLogement());
        else st.setNull(20, Types.NUMERIC);
    }

    // FIND BY ID
    public static String reqFindById() {
        return "SELECT f.*, e.NOM_ENTREPRISE " +
               "FROM FACTURE f " +
               "LEFT JOIN ENTREPRISE e ON f.SIRET = e.SIRET " +
               "WHERE f.ID_FACTURE = ?";
    }

    public static void paramFindById(PreparedStatement st, long idFacture) throws SQLException {
        st.setLong(1, idFacture);
    }

    //  UPDATE 
    public static String reqUpdate() {
        return "UPDATE FACTURE SET " +
               "ACCOMPTE = ?, NATURE = ?, PERIODE_DEB = ?, DATE_EMISSION = ?, " +
               "MONTANT_HT = ?, MONTANT_TTC = ?, MONTANT_TEOM = ?, " +
               "RECUPERABLE_LOCATAIRE = ?, DEDUCTIBLE_IMPOT = ?, PERIODE_FIN = ?, " +
               "TRAVAUX = ?, DATEDEVIS = ?, MONTANTDEVIS = ?, SIRET = ?, " +
               "ID_CHARGE = ?, ID_BATIMENT = ?, STATUT_PAIEMENT = ?, ID_BAIL = ?, ID_LOGEMENT = ? " +
               "WHERE ID_FACTURE = ?";
    }

    public static void paramUpdate(PreparedStatement st, Facture f) throws SQLException {
        int idx = 1;
        
        if (f.getAcompte() != null) st.setDouble(idx++, f.getAcompte());
        else st.setNull(idx++, Types.NUMERIC);

        st.setString(idx++, f.getNature());
        st.setString(idx++, f.getPeriodeDeb());
        st.setDate(idx++, f.getDateEmission() != null ? new java.sql.Date(f.getDateEmission().getTime()) : null);

        if (f.getMontantHT() != null) st.setDouble(idx++, f.getMontantHT());
        else st.setNull(idx++, Types.NUMERIC);

        st.setDouble(idx++, f.getMontantTTC());

        if (f.getMontantTEOM() != null) st.setDouble(idx++, f.getMontantTEOM());
        else st.setNull(idx++, Types.NUMERIC);

        if (f.isRecuperableLocataire() != null) st.setInt(idx++, f.isRecuperableLocataire() ? 1 : 0);
        else st.setNull(idx++, Types.INTEGER);

        if (f.isDeductibleImpot() != null) st.setInt(idx++, f.isDeductibleImpot() ? 1 : 0);
        else st.setNull(idx++, Types.INTEGER);

        if (f.getPeriodeFin() != null) st.setDate(idx++, new java.sql.Date(f.getPeriodeFin().getTime()));
        else st.setNull(idx++, Types.DATE);

        st.setString(idx++, f.getTravaux());

        if (f.getDateDevis() != null) st.setDate(idx++, new java.sql.Date(f.getDateDevis().getTime()));
        else st.setNull(idx++, Types.DATE);

        if (f.getMontantDevis() != null) st.setDouble(idx++, f.getMontantDevis());
        else st.setNull(idx++, Types.NUMERIC);

        st.setLong(idx++, f.getSiret());

        if (f.getIdCharge() != null) st.setLong(idx++, f.getIdCharge());
        else st.setNull(idx++, Types.NUMERIC);

        if (f.getIdBatiment() != null) st.setLong(idx++, f.getIdBatiment());
        else st.setNull(idx++, Types.NUMERIC);

        st.setString(idx++, f.getStatut());

        if (f.getIdBail() != null) st.setLong(idx++, f.getIdBail());
        else st.setNull(idx++, Types.NUMERIC);

        if (f.getIdLogement() != null) st.setLong(idx++, f.getIdLogement());
        else st.setNull(idx++, Types.NUMERIC);

        st.setLong(idx++, f.getIdFactureLong());
    }

    // DELETE 
    public static String reqDelete() {
        return "DELETE FROM FACTURE WHERE ID_FACTURE = ?";
    }

    public static void paramDelete(PreparedStatement st, long idFacture) throws SQLException {
        st.setLong(1, idFacture);
    }

    // GET NEXT ID 
    public static String reqGetNextId() {
        return "SELECT NVL(MAX(ID_FACTURE), 0) + 1 FROM FACTURE";
    }

    // FIND ALL 
    public static String reqFindAll() {
        return "SELECT f.*, e.NOM_ENTREPRISE " +
               "FROM FACTURE f " +
               "LEFT JOIN ENTREPRISE e ON f.SIRET = e.SIRET " +
               "ORDER BY f.DATE_EMISSION DESC";
    }

    //  FIND BY BATIMENT 
    public static String reqFindByBatiment() {
        return "SELECT f.*, e.NOM_ENTREPRISE " +
               "FROM FACTURE f " +
               "LEFT JOIN ENTREPRISE e ON f.SIRET = e.SIRET " +
               "WHERE f.ID_BATIMENT = ? " +
               "ORDER BY f.DATE_EMISSION DESC";
    }

    public static void paramFindByBatiment(PreparedStatement st, long idBatiment) throws SQLException {
        st.setLong(1, idBatiment);
    }

    //  FIND BY LOGEMENT 
    public static String reqFindByLogement() {
        return "SELECT f.*, e.NOM_ENTREPRISE " +
               "FROM FACTURE f " +
               "LEFT JOIN ENTREPRISE e ON f.SIRET = e.SIRET " +
               "WHERE f.ID_LOGEMENT = ? " +
               "ORDER BY f.DATE_EMISSION DESC";
    }

    public static void paramFindByLogement(PreparedStatement st, long idLogement) throws SQLException {
        st.setLong(1, idLogement);
    }

    // FIND BY BAIL
    public static String reqFindByBail() {
        return "SELECT f.*, e.NOM_ENTREPRISE " +
               "FROM FACTURE f " +
               "LEFT JOIN ENTREPRISE e ON f.SIRET = e.SIRET " +
               "WHERE f.ID_BAIL = ? " +
               "ORDER BY f.DATE_EMISSION DESC";
    }

    public static void paramFindByBail(PreparedStatement st, long idBail) throws SQLException {
        st.setLong(1, idBail);
    }

    // FIND BY ENTREPRISE
    public static String reqFindByEntreprise() {
        return "SELECT f.*, e.NOM_ENTREPRISE " +
               "FROM FACTURE f " +
               "LEFT JOIN ENTREPRISE e ON f.SIRET = e.SIRET " +
               "WHERE f.SIRET = ? " +
               "ORDER BY f.DATE_EMISSION DESC";
    }

    public static void paramFindByEntreprise(PreparedStatement st, long siret) throws SQLException {
        st.setLong(1, siret);
    }

    //  GET TOTAL TRAVAUX (fonction PL/SQL)
    public static String reqGetTotalTravauxBatiment() {
        return "{? = call getTotalTravauxBatiment(?, ?)}";
    }

    public static void paramGetTotalTravauxBatiment(CallableStatement cs, long idBatiment, int annee) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setLong(2, idBatiment);
        cs.setInt(3, annee);
    }

    // GET TOTAL TRAVAUX 
    public static String reqGetTotalTravauxByBatimentAndAnneeSQL() {
        return "SELECT NVL(SUM(MONTANT_TTC), 0) AS TOTAL FROM FACTURE " +
               "WHERE ID_BATIMENT = ? " +
               "AND EXTRACT(YEAR FROM DATE_EMISSION) = ? " +
               "AND UPPER(NVL(STATUT_PAIEMENT, 'A PAYER')) IN ('PAYE', 'PAYEE') " +
               "AND (DEDUCTIBLE_IMPOT = 1 OR TRAVAUX IS NOT NULL)";
    }

    public static void paramGetTotalTravauxByBatimentAndAnneeSQL(PreparedStatement ps, long idBatiment, int annee) throws SQLException {
        ps.setLong(1, idBatiment);
        ps.setInt(2, annee);
    }

    // GET TOTAL FACTURES RECUPERABLES LOCATAIRE 
    public static String reqGetTotalFacturesRecuperablesLocataire() {
        return "SELECT NVL(SUM(MONTANT_TTC), 0) AS TOTAL FROM FACTURE " +
               "WHERE ID_BATIMENT = ? " +
               "AND EXTRACT(YEAR FROM DATE_EMISSION) = ? " +
               "AND RECUPERABLE_LOCATAIRE = 1 " +
               "AND UPPER(NVL(STATUT_PAIEMENT, 'A PAYER')) IN ('PAYE', 'PAYEE')";
    }

    public static void paramGetTotalFacturesRecuperablesLocataire(PreparedStatement ps, long idBatiment, int annee) throws SQLException {
        ps.setLong(1, idBatiment);
        ps.setInt(2, annee);
    }

    //  FIND FACTURES RECUPERABLES LOCATAIRE 
    public static String reqFindFacturesRecuperablesLocataire() {
        return "SELECT f.*, e.NOM_ENTREPRISE FROM FACTURE f " +
               "LEFT JOIN ENTREPRISE e ON f.SIRET = e.SIRET " +
               "WHERE f.ID_BATIMENT = ? " +
               "AND EXTRACT(YEAR FROM f.DATE_EMISSION) = ? " +
               "AND f.RECUPERABLE_LOCATAIRE = 1 " +
               "ORDER BY f.DATE_EMISSION DESC";
    }

    public static void paramFindFacturesRecuperablesLocataire(PreparedStatement ps, long idBatiment, int annee) throws SQLException {
        ps.setLong(1, idBatiment);
        ps.setInt(2, annee);
    }

    //  GET TOTAL FACTURES DEDUCTIBLES IMPOTS
    public static String reqGetTotalFacturesDeductiblesImpots() {
        return "SELECT NVL(SUM(MONTANT_TTC), 0) AS TOTAL FROM FACTURE " +
               "WHERE ID_BATIMENT = ? " +
               "AND EXTRACT(YEAR FROM DATE_EMISSION) = ? " +
               "AND DEDUCTIBLE_IMPOT = 1 " +
               "AND UPPER(NVL(STATUT_PAIEMENT, 'A PAYER')) IN ('PAYE', 'PAYEE')";
    }

    public static void paramGetTotalFacturesDeductiblesImpots(PreparedStatement ps, long idBatiment, int annee) throws SQLException {
        ps.setLong(1, idBatiment);
        ps.setInt(2, annee);
    }

    // MARQUER PAYEE 
    public static String reqMarquerPayee() {
        return "UPDATE FACTURE SET STATUT_PAIEMENT = 'PAYE' WHERE ID_FACTURE = ?";
    }

    public static void paramMarquerPayee(PreparedStatement st, long idFacture) throws SQLException {
        st.setLong(1, idFacture);
    }


    public static String reqGetTravauxParEntreprise() {
        return "SELECT e.NOM_ENTREPRISE, SUM(f.MONTANT_TTC) AS TOTAL " +
               "FROM FACTURE f " +
               "JOIN ENTREPRISE e ON f.SIRET = e.SIRET " +
               "WHERE f.TRAVAUX IS NOT NULL " +
               "AND EXTRACT(YEAR FROM f.DATE_EMISSION) = ? " +
               "AND (f.ID_BATIMENT = ? OR f.ID_LOGEMENT = ?) " +
               "GROUP BY e.NOM_ENTREPRISE " +
               "ORDER BY TOTAL DESC";
    }

    public static void paramGetTravauxParEntreprise(PreparedStatement ps, int annee, long idBatiment, long idLogement) throws SQLException {
        ps.setInt(1, annee);
        ps.setLong(2, idBatiment);
        ps.setLong(3, idLogement);
    }

    /**
     * Requete : Travaux propres au batiment regroupes par entreprise
     */
    public static String reqGetTravauxBatimentParEntreprise() {
        return "SELECT e.NOM_ENTREPRISE, SUM(f.MONTANT_TTC) AS TOTAL " +
               "FROM FACTURE f " +
               "JOIN ENTREPRISE e ON f.SIRET = e.SIRET " +
               "WHERE f.TRAVAUX IS NOT NULL " +
               "AND f.ID_BATIMENT = ? " +
               "AND f.ID_LOGEMENT IS NULL " +
               "AND EXTRACT(YEAR FROM f.DATE_EMISSION) = ? " +
               "GROUP BY e.NOM_ENTREPRISE " +
               "ORDER BY TOTAL DESC";
    }

    public static void paramGetTravauxBatimentParEntreprise(PreparedStatement ps, long idBatiment, int annee) throws SQLException {
        ps.setLong(1, idBatiment);
        ps.setInt(2, annee);
    }

    /**
     * Requete : Detail taxes foncieres (Total, OM, Reste)
     */
    public static String reqGetDetailTaxesFoncieres() {
        return "SELECT NVL(SUM(MONTANT_TTC), 0) AS TOTAL, " +
               "       NVL(SUM(MONTANT_TEOM), 0) AS OM " +
               "FROM FACTURE " +
               "WHERE ID_BATIMENT = ? " +
               "AND EXTRACT(YEAR FROM DATE_EMISSION) = ? " +
               "AND UPPER(NATURE) LIKE '%TAXE%FONCIERE%'";
    }

    public static void paramGetDetailTaxesFoncieres(PreparedStatement ps, long idBatiment, int annee) throws SQLException {
        ps.setLong(1, idBatiment);
        ps.setInt(2, annee);
    }
}