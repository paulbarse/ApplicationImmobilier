package modele.dao.Requete;

import java.sql.*;
import modele.Loyer;

/**
 * Classe contenant les requêtes SQL pour LoyerDao
 */
public class RequeteLoyer {

    public static final String STATUT_EN_ATTENTE = "EN_ATTENTE";
    public static final String STATUT_PAYE = "PAYE";
    public static final String STATUT_RETARD = "RETARD";

    //  CREATE 
    public static String reqCreate() {
        return "INSERT INTO LOYER (ID_LOYER, QUITTANCE, MONTANT_LOYER, MOIS, "
             + "MONTANT_PROVISION, STATUT, INDICE_IRL, ANCIEN_IRL, DATE_PAIEMENT, ID_BAIL) "
             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    public static void paramCreate(PreparedStatement st, Loyer loyer) throws SQLException {
        st.setLong(1, loyer.getIdLoyerLong());
        st.setString(2, loyer.getQuittance());
        st.setDouble(3, loyer.getMontantLoyer());
        st.setString(4, loyer.getMois());
        
        if (loyer.getMontantProvision() != null) {
            st.setDouble(5, loyer.getMontantProvision());
        } else {
            st.setNull(5, Types.NUMERIC);
        }
        
        String statut = loyer.getStatut();
        if (statut == null || statut.trim().isEmpty()) {
            statut = STATUT_EN_ATTENTE;
        }
        st.setString(6, statut.toUpperCase().replace(" ", "_"));
        
        if (loyer.getIndiceIRL() != null) {
            st.setInt(7, loyer.getIndiceIRL());
        } else {
            st.setNull(7, Types.INTEGER);
        }
        
        if (loyer.getAncienIRL() != null) {
            st.setInt(8, loyer.getAncienIRL());
        } else {
            st.setNull(8, Types.INTEGER);
        }
        
        if (loyer.getDatePaiement() != null) {
            st.setDate(9, new java.sql.Date(loyer.getDatePaiement().getTime()));
        } else {
            st.setNull(9, Types.DATE);
        }
        
        st.setLong(10, loyer.getIdBailLong());
    }

    //  UPDATE 
    public static String reqUpdate() {
        return "UPDATE LOYER SET QUITTANCE=?, MONTANT_LOYER=?, MOIS=?, "
             + "MONTANT_PROVISION=?, STATUT=?, INDICE_IRL=?, ANCIEN_IRL=?, "
             + "DATE_PAIEMENT=?, ID_BAIL=? WHERE ID_LOYER=?";
    }

    public static void paramUpdate(PreparedStatement st, Loyer loyer) throws SQLException {
        st.setString(1, loyer.getQuittance());
        st.setDouble(2, loyer.getMontantLoyer());
        st.setString(3, loyer.getMois());
        
        if (loyer.getMontantProvision() != null) {
            st.setDouble(4, loyer.getMontantProvision());
        } else {
            st.setNull(4, Types.NUMERIC);
        }
        
        String statut = loyer.getStatut();
        if (statut != null) {
            statut = statut.toUpperCase().replace(" ", "_");
        }
        st.setString(5, statut);
        
        if (loyer.getIndiceIRL() != null) {
            st.setInt(6, loyer.getIndiceIRL());
        } else {
            st.setNull(6, Types.INTEGER);
        }
        
        if (loyer.getAncienIRL() != null) {
            st.setInt(7, loyer.getAncienIRL());
        } else {
            st.setNull(7, Types.INTEGER);
        }
        
        if (loyer.getDatePaiement() != null) {
            st.setDate(8, new java.sql.Date(loyer.getDatePaiement().getTime()));
        } else {
            st.setNull(8, Types.DATE);
        }
        
        st.setLong(9, loyer.getIdBailLong());
        st.setLong(10, loyer.getIdLoyerLong());
    }

    //  DELETE
    public static String reqDelete() {
        return "DELETE FROM LOYER WHERE ID_LOYER=?";
    }

    public static void paramDelete(PreparedStatement st, long idLoyer) throws SQLException {
        st.setLong(1, idLoyer);
    }

    //FIND BY ID 
    public static String reqFindById() {
        return "SELECT * FROM LOYER WHERE ID_LOYER=?";
    }

    public static void paramFindById(PreparedStatement st, long idLoyer) throws SQLException {
        st.setLong(1, idLoyer);
    }

    //  FIND ALL 
    public static String reqFindAll() {
        return "SELECT * FROM LOYER ORDER BY ID_LOYER DESC";
    }

    //FIND BY BAIL 
    public static String reqFindByBail() {
        return "SELECT * FROM LOYER WHERE ID_BAIL = ? ORDER BY MOIS";
    }

    public static void paramFindByBail(PreparedStatement st, long idBail) throws SQLException {
        st.setLong(1, idBail);
    }

    //  FIND BY BAIL AND ANNEE 
    public static String reqFindByBailAndAnnee() {
        return "SELECT * FROM LOYER WHERE ID_BAIL = ? " +
               "AND (MOIS LIKE ? OR MOIS LIKE ?) ORDER BY MOIS";
    }

    public static void paramFindByBailAndAnnee(PreparedStatement st, long idBail, int annee) throws SQLException {
        st.setLong(1, idBail);
        st.setString(2, "%/" + annee);
        st.setString(3, "%/" + (annee % 100));
    }

    //FIND IMPAYES BY BAIL
    public static String reqFindImpayesByBail() {
        return "SELECT * FROM LOYER WHERE ID_BAIL = ? " +
               "AND UPPER(STATUT) IN ('EN_ATTENTE', 'RETARD') ORDER BY MOIS";
    }

    public static void paramFindImpayesByBail(PreparedStatement st, long idBail) throws SQLException {
        st.setLong(1, idBail);
    }

    //  MARQUER PAYE 
    public static String reqMarquerPaye() {
        return "UPDATE LOYER SET STATUT = 'PAYE', DATE_PAIEMENT = ? WHERE ID_LOYER = ?";
    }

    public static void paramMarquerPaye(PreparedStatement st, long idLoyer, java.util.Date datePaiement) throws SQLException {
        st.setDate(1, new java.sql.Date(datePaiement.getTime()));
        st.setLong(2, idLoyer);
    }

    //  MARQUER PAYE
    public static String reqMarquerPayeProcedure() {
        return "{call MARQUERLOYERPAYE(?, ?)}";
    }

    public static void paramMarquerPayeProcedure(CallableStatement cs, long idLoyer, java.util.Date datePaiement) throws SQLException {
        cs.setLong(1, idLoyer);
        cs.setDate(2, new java.sql.Date(datePaiement.getTime()));
    }

    // MARQUER RETARD
    public static String reqMarquerRetard() {
        return "{call MARQUERLOYERRETARD(?)}";
    }

    public static void paramMarquerRetard(CallableStatement cs, long idLoyer) throws SQLException {
        cs.setLong(1, idLoyer);
    }

    // MARQUER EN ATTENTE 
    public static String reqMarquerEnAttente() {
        return "{call MARQUERLOYERENATTENTE(?)}";
    }

    public static void paramMarquerEnAttente(CallableStatement cs, long idLoyer) throws SQLException {
        cs.setLong(1, idLoyer);
    }

    //  GENERER LOYERS 
    public static String reqGenererLoyers() {
        return "{call GENERERLOYERSANNUELS(?, ?)}";
    }

    public static void paramGenererLoyers(CallableStatement cs, long idBail, int annee) throws SQLException {
        cs.setLong(1, idBail);
        cs.setInt(2, annee);
    }

    //  GET TOTAL LOYERS 
    public static String reqGetTotalLoyersByBailAndAnnee() {
        return "{? = call getTotalLoyersBail(?, ?)}";
    }

    public static void paramGetTotalLoyersByBailAndAnnee(CallableStatement cs, long idBail, int annee) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setLong(2, idBail);
        cs.setInt(3, annee);
    }

    //  GET TOTAL PROVISIONS 
    public static String reqGetTotalProvisionsByBailAndAnnee() {
        return "{? = call getTotalProvisionsBail(?, ?)}";
    }

    public static void paramGetTotalProvisionsByBailAndAnnee(CallableStatement cs, long idBail, int annee) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setLong(2, idBail);
        cs.setInt(3, annee);
    }

    // GET TOTAL PROVISIONS AVEC FALLBACK 
    public static String reqGetTotalProvisionsBailAvecFallback() {
        return "{? = call getTotalProvisionsBailAvecFallback(?, ?)}";
    }

    public static void paramGetTotalProvisionsBailAvecFallback(CallableStatement cs, long idBail, int annee) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setLong(2, idBail);
        cs.setInt(3, annee);
    }

    //  COUNT LOYERS PAYES
    public static String reqCountLoyersPayes() {
        return "SELECT COUNT(*) FROM LOYER WHERE ID_BAIL = ? " +
               "AND UPPER(STATUT) = 'PAYE' " +
               "AND (MOIS LIKE ? OR MOIS LIKE ?)";
    }

    public static void paramCountLoyersPayes(PreparedStatement st, long idBail, int annee) throws SQLException {
        st.setLong(1, idBail);
        st.setString(2, "%/" + annee);
        st.setString(3, "%/" + (annee % 100));
    }

    // COUNT LOYERS RETARD 
    public static String reqCountLoyersRetard() {
        return "SELECT COUNT(*) FROM LOYER WHERE ID_BAIL = ? AND UPPER(STATUT) = 'RETARD'";
    }

    public static void paramCountLoyersRetard(PreparedStatement st, long idBail) throws SQLException {
        st.setLong(1, idBail);
    }

    //  UPDATE MONTANTS 
    public static String reqUpdateMontants() {
        return "UPDATE LOYER SET MONTANT_LOYER = ?, MONTANT_PROVISION = ? WHERE ID_LOYER = ?";
    }

    public static void paramUpdateMontants(PreparedStatement st, long idLoyer, double montantLoyer, Double montantProvision) throws SQLException {
        st.setDouble(1, montantLoyer);
        if (montantProvision != null) {
            st.setDouble(2, montantProvision);
        } else {
            st.setNull(2, Types.NUMERIC);
        }
        st.setLong(3, idLoyer);
    }

    // IMPORTER PAIEMENT CSV
    public static String reqImporterPaiement() {
        return "{call IMPORTER_PAIEMENT_CSV(?, ?, ?, ?, ?)}";
    }

    public static void paramImporterPaiement(CallableStatement cs, long idLogement, long idLocataire, 
            String moisStr, double loyer, double provision) throws SQLException {
        cs.setLong(1, idLogement);
        cs.setLong(2, idLocataire);
        cs.setString(3, moisStr);
        cs.setDouble(4, loyer);
        cs.setDouble(5, provision);
    }

    // NOUVELLES METHODES POUR CORRIGER SQL EN DUR


    public static String reqGenererIdLoyer() {
        return "SELECT NVL(MAX(ID_LOYER), 0) + 1 FROM LOYER";
    }

    public static String reqMarquerPayeAvecSysdate() {
        return "UPDATE LOYER SET STATUT = 'PAYE', DATE_PAIEMENT = SYSDATE WHERE ID_LOYER = ?";
    }

    public static void paramMarquerPayeAvecSysdate(PreparedStatement st, long idLoyer) throws SQLException {
        st.setLong(1, idLoyer);
    }

    public static String reqMarquerStatut() {
        return "UPDATE LOYER SET STATUT = ?, DATE_PAIEMENT = NULL WHERE ID_LOYER = ?";
    }

    public static void paramMarquerStatut(PreparedStatement st, String statut, long idLoyer) throws SQLException {
        st.setString(1, statut);
        st.setLong(2, idLoyer);
    }
}
