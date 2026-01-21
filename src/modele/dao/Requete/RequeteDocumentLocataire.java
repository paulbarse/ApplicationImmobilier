package modele.dao.Requete;

import java.sql.*;

/**
 * Requêtes SQL pour DocumentLocataireDao
 */
public class RequeteDocumentLocataire {

    // ========================================================================
    // UTILITAIRES
    // ========================================================================
    
    public static String reqGetBailActifLocataire() {
        return "{? = call getBailActifLocataire(?)}";
    }
    
    public static void paramGetBailActifLocataire(CallableStatement cs, long idLocataire) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setLong(2, idLocataire);
    }
    
    public static String reqGetLogementByLocataire() {
        return "{? = call getLogementByLocataire(?)}";
    }
    
    public static void paramGetLogementByLocataire(CallableStatement cs, long idLocataire) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setLong(2, idLocataire);
    }
    
    public static String reqGetNextId() {
        return "{? = call getNextId(?, ?)}";
    }
    
    public static void paramGetNextId(CallableStatement cs, String tableName, String columnName) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setString(2, tableName);
        cs.setString(3, columnName);
    }

    // ========================================================================
    // 1. DIAGNOSTICS
    // ========================================================================
    
    public static String reqGetDiagnosticsLocataire() {
        return "SELECT d.id_diagnostique, d.type_diag, d.reference, " +
               "d.date_emission, d.date_expiration, " +
               "getStatutDiagnostic(d.date_expiration) AS statut " +
               "FROM SIGNE s " +
               "JOIN BAIL b ON s.id_bail = b.id_bail " +
               "JOIN LOGEMENT lg ON lg.id_bail = b.id_bail " +
               "JOIN DIAGNOSTIQUE d ON d.id_logement = lg.id_logement " +
               "WHERE s.id_locataire = ? " +
               "ORDER BY d.date_expiration DESC";
    }
    
    public static void paramGetDiagnosticsLocataire(PreparedStatement ps, long idLocataire) throws SQLException {
        ps.setLong(1, idLocataire);
    }
    
    public static String reqAjouterDiagnostic() {
        return "INSERT INTO DIAGNOSTIQUE (id_diagnostique, type_diag, reference, " +
               "date_emission, date_expiration, id_logement) " +
               "VALUES (?, ?, ?, ?, ?, ?)";
    }
    
    public static void paramAjouterDiagnostic(PreparedStatement ps, long idDiagnostic, String typeDiag, 
            String reference, java.util.Date dateEmission, java.util.Date dateExpiration, 
            long idLogement) throws SQLException {
        ps.setLong(1, idDiagnostic);
        ps.setString(2, typeDiag);
        ps.setString(3, reference);
        ps.setDate(4, new java.sql.Date(dateEmission.getTime()));
        if (dateExpiration != null) {
            ps.setDate(5, new java.sql.Date(dateExpiration.getTime()));
        } else {
            ps.setNull(5, Types.DATE);
        }
        ps.setLong(6, idLogement);
    }
    
    public static String reqModifierDiagnostic() {
        return "UPDATE DIAGNOSTIQUE SET type_diag = ?, reference = ?, " +
               "date_emission = ?, date_expiration = ? WHERE id_diagnostique = ?";
    }
    
    public static void paramModifierDiagnostic(PreparedStatement ps, long idDiagnostic, String typeDiag, 
            String reference, java.util.Date dateEmission, java.util.Date dateExpiration) throws SQLException {
        ps.setString(1, typeDiag);
        ps.setString(2, reference);
        ps.setDate(3, new java.sql.Date(dateEmission.getTime()));
        if (dateExpiration != null) {
            ps.setDate(4, new java.sql.Date(dateExpiration.getTime()));
        } else {
            ps.setNull(4, Types.DATE);
        }
        ps.setLong(5, idDiagnostic);
    }
    
    public static String reqSupprimerDiagnostic() {
        return "DELETE FROM DIAGNOSTIQUE WHERE id_diagnostique = ?";
    }
    
    public static void paramSupprimerDiagnostic(PreparedStatement ps, long idDiagnostic) throws SQLException {
        ps.setLong(1, idDiagnostic);
    }

    // ========================================================================
    // 2. INDEX / COMPTEURS
    // ========================================================================
    
    public static String reqGetIndexLocataire() {
        return "SELECT rc.id_releve, rc.type, rc.date_, rc.num_compteur, " +
               "rc.ancien_index, rc.nouvelle_index, " +
               "(rc.nouvelle_index - rc.ancien_index) AS conso, rc.unite " +
               "FROM SIGNE s " +
               "JOIN BAIL b ON s.id_bail = b.id_bail " +
               "JOIN LOGEMENT lg ON lg.id_bail = b.id_bail " +
               "JOIN RELEVE_COMPTEUR rc ON rc.id_releve = lg.id_releve " +
               "WHERE s.id_locataire = ? " +
               "ORDER BY rc.date_ DESC";
    }
    
    public static void paramGetIndexLocataire(PreparedStatement ps, long idLocataire) throws SQLException {
        ps.setLong(1, idLocataire);
    }
    
    public static String reqAjouterIndex() {
        return "INSERT INTO RELEVE_COMPTEUR (id_releve, type, date_, num_compteur, " +
               "ancien_index, nouvelle_index, unite) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }
    
    public static void paramAjouterIndex(PreparedStatement ps, long idReleve, String type, 
            java.util.Date date, long numCompteur, long ancienIndex, long nouvelIndex, 
            String unite) throws SQLException {
        ps.setLong(1, idReleve);
        ps.setString(2, type);
        ps.setDate(3, new java.sql.Date(date.getTime()));
        ps.setLong(4, numCompteur);
        ps.setLong(5, ancienIndex);
        ps.setLong(6, nouvelIndex);
        ps.setString(7, unite);
    }
    
    public static String reqLierReleveAuLogement() {
        return "UPDATE LOGEMENT SET id_releve = ? WHERE id_logement = ?";
    }
    
    public static void paramLierReleveAuLogement(PreparedStatement ps, long idReleve, long idLogement) throws SQLException {
        ps.setLong(1, idReleve);
        ps.setLong(2, idLogement);
    }
    
    public static String reqModifierIndex() {
        return "UPDATE RELEVE_COMPTEUR SET type = ?, date_ = ?, num_compteur = ?, " +
               "ancien_index = ?, nouvelle_index = ?, unite = ? WHERE id_releve = ?";
    }
    
    public static void paramModifierIndex(PreparedStatement ps, long idReleve, String type, 
            java.util.Date date, long numCompteur, long ancienIndex, long nouvelIndex, 
            String unite) throws SQLException {
        ps.setString(1, type);
        ps.setDate(2, new java.sql.Date(date.getTime()));
        ps.setLong(3, numCompteur);
        ps.setLong(4, ancienIndex);
        ps.setLong(5, nouvelIndex);
        ps.setString(6, unite);
        ps.setLong(7, idReleve);
    }
    
    public static String reqDelierReleveLogement() {
        return "UPDATE LOGEMENT SET id_releve = NULL WHERE id_releve = ?";
    }
    
    public static void paramDelierReleveLogement(PreparedStatement ps, long idReleve) throws SQLException {
        ps.setLong(1, idReleve);
    }
    
    public static String reqSupprimerIndex() {
        return "DELETE FROM RELEVE_COMPTEUR WHERE id_releve = ?";
    }
    
    public static void paramSupprimerIndex(PreparedStatement ps, long idReleve) throws SQLException {
        ps.setLong(1, idReleve);
    }

    // ========================================================================
    // 3. MOUVEMENTS DE CAUTION
    // ========================================================================
    
    public static String reqGetMouvementsCaution() {
        return "SELECT mc.id_mouvement, mc.date_mouvement, mc.type_mouvement, " +
               "mc.montant, mc.moyen_paiement, mc.observations " +
               "FROM SIGNE s " +
               "JOIN BAIL b ON s.id_bail = b.id_bail " +
               "JOIN MOUVEMENT_CAUTION mc ON mc.id_bail = b.id_bail " +
               "WHERE s.id_locataire = ? " +
               "ORDER BY mc.date_mouvement DESC";
    }
    
    public static void paramGetMouvementsCaution(PreparedStatement ps, long idLocataire) throws SQLException {
        ps.setLong(1, idLocataire);
    }
    
    public static String reqAjouterMouvementCaution() {
        return "{call ajouterMouvementCaution(?, ?, ?, ?, ?, ?)}";
    }
    
    public static void paramAjouterMouvementCaution(CallableStatement cs, long idBail, java.util.Date date,
            String type, double montant, String moyenPaiement, String observations) throws SQLException {
        cs.setLong(1, idBail);
        cs.setDate(2, new java.sql.Date(date.getTime()));
        cs.setString(3, type);
        cs.setDouble(4, montant);
        cs.setString(5, moyenPaiement);
        cs.setString(6, observations);
    }
    
    public static String reqModifierMouvementCaution() {
        return "UPDATE MOUVEMENT_CAUTION SET date_mouvement = ?, type_mouvement = ?, " +
               "montant = ?, moyen_paiement = ?, observations = ? WHERE id_mouvement = ?";
    }
    
    public static void paramModifierMouvementCaution(PreparedStatement ps, long idMouvement, java.util.Date date,
            String type, double montant, String moyenPaiement, String observations) throws SQLException {
        ps.setDate(1, new java.sql.Date(date.getTime()));
        ps.setString(2, type);
        ps.setDouble(3, montant);
        ps.setString(4, moyenPaiement);
        ps.setString(5, observations);
        ps.setLong(6, idMouvement);
    }
    
    public static String reqSupprimerMouvementCaution() {
        return "DELETE FROM MOUVEMENT_CAUTION WHERE id_mouvement = ?";
    }
    
    public static void paramSupprimerMouvementCaution(PreparedStatement ps, long idMouvement) throws SQLException {
        ps.setLong(1, idMouvement);
    }

    // ========================================================================
    // 4. LOYERS EN ATTENTE
    // ========================================================================
    
    public static String reqGetLoyersEnAttente() {
        return "SELECT l.id_loyer, l.mois, l.montant_loyer, l.montant_provision, " +
               "(l.montant_loyer + NVL(l.montant_provision, 0)) AS total, " +
               "NVL(l.statut, 'En attente') AS statut " +
               "FROM SIGNE s " +
               "JOIN BAIL b ON s.id_bail = b.id_bail " +
               "JOIN LOYER l ON l.id_bail = b.id_bail " +
               "WHERE s.id_locataire = ? " +
               "AND (l.statut IS NULL OR l.statut != 'Paye') " +
               "ORDER BY l.mois DESC";
    }
    
    public static void paramGetLoyersEnAttente(PreparedStatement ps, long idLocataire) throws SQLException {
        ps.setLong(1, idLocataire);
    }

    // ========================================================================
    // 5. QUITTANCES (loyers payés)
    // ========================================================================
    
    public static String reqGetQuittancesPayees() {
        return "SELECT l.id_loyer, l.mois, l.montant_loyer, l.montant_provision, " +
               "(l.montant_loyer + NVL(l.montant_provision, 0)) AS total, " +
               "l.date_paiement, NVL(l.quittance, 'QUI-' || l.id_loyer) AS quittance " +
               "FROM SIGNE s " +
               "JOIN BAIL b ON s.id_bail = b.id_bail " +
               "JOIN LOYER l ON l.id_bail = b.id_bail " +
               "WHERE s.id_locataire = ? " +
               "AND l.statut = 'Paye' " +
               "ORDER BY l.mois DESC";
    }
    
    public static void paramGetQuittancesPayees(PreparedStatement ps, long idLocataire) throws SQLException {
        ps.setLong(1, idLocataire);
    }

    // ========================================================================
    // 6. GESTION DES LOYERS
    // ========================================================================
    
    public static String reqAjouterLoyer() {
        return "INSERT INTO LOYER (id_loyer, mois, montant_loyer, montant_provision, " +
               "statut, date_paiement, id_bail) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }
    
    public static void paramAjouterLoyer(PreparedStatement ps, long idLoyer, String mois, double loyerNu,
            double charges, String statut, java.util.Date datePaiement, long idBail) throws SQLException {
        ps.setLong(1, idLoyer);
        ps.setString(2, mois);
        ps.setDouble(3, loyerNu);
        ps.setDouble(4, charges);
        ps.setString(5, statut);
        if (datePaiement != null) {
            ps.setDate(6, new java.sql.Date(datePaiement.getTime()));
        } else {
            ps.setNull(6, Types.DATE);
        }
        ps.setLong(7, idBail);
    }
    
    public static String reqModifierLoyer() {
        return "UPDATE LOYER SET mois = ?, montant_loyer = ?, montant_provision = ? " +
               "WHERE id_loyer = ?";
    }
    
    public static void paramModifierLoyer(PreparedStatement ps, long idLoyer, String mois, 
            double loyerNu, double charges) throws SQLException {
        ps.setString(1, mois);
        ps.setDouble(2, loyerNu);
        ps.setDouble(3, charges);
        ps.setLong(4, idLoyer);
    }
    
    public static String reqSupprimerLoyer() {
        return "DELETE FROM LOYER WHERE id_loyer = ?";
    }
    
    public static void paramSupprimerLoyer(PreparedStatement ps, long idLoyer) throws SQLException {
        ps.setLong(1, idLoyer);
    }
    
    public static String reqMarquerLoyerPaye() {
        return "{call marquerLoyerPaye(?, ?)}";
    }
    
    public static void paramMarquerLoyerPaye(CallableStatement cs, long idLoyer, 
            java.util.Date datePaiement) throws SQLException {
        cs.setLong(1, idLoyer);
        cs.setDate(2, new java.sql.Date(datePaiement.getTime()));
    }
    
    public static String reqAnnulerPaiementLoyer() {
        return "{call annulerPaiementLoyer(?)}";
    }
    
    public static void paramAnnulerPaiementLoyer(CallableStatement cs, long idLoyer) throws SQLException {
        cs.setLong(1, idLoyer);
    }
}
