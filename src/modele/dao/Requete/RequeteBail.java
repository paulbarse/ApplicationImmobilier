package modele.dao.Requete;

import java.sql.*;
import modele.Bail;

/**
 * Classe contenant les requetes SQL pour BailDao

 */
public class RequeteBail {

    // CREATE
    public static String reqCreate() {
        return "INSERT INTO BAIL (ID_BAIL, DATE_DEBUT, DATE_FIN, LOYER_INITIAL, "
             + "PROVISION_INITIALES, SOLDE_TOUT_COMPTE, CAUTION, ETAT, JOUR_PAIEMENT) "
             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    public static void paramCreate(PreparedStatement st, Bail bail) throws SQLException {
        st.setLong(1, bail.getIdBail());
        st.setDate(2, new java.sql.Date(bail.getDateDebut().getTime()));

        if (bail.getDateFin() != null) st.setDate(3, new java.sql.Date(bail.getDateFin().getTime()));
        else st.setNull(3, Types.DATE);

        st.setDouble(4, bail.getLoyerInitial());
        st.setDouble(5, bail.getProvisionInitiales());

        if (bail.getSoldeToutCompte() != null) st.setDouble(6, bail.getSoldeToutCompte());
        else st.setNull(6, Types.NUMERIC);

        if (bail.getCaution() != null) st.setDouble(7, bail.getCaution());
        else st.setNull(7, Types.NUMERIC);

        st.setString(8, bail.getEtat());

        if (bail.getJourPaiement() != null) st.setInt(9, bail.getJourPaiement());
        else st.setNull(9, Types.INTEGER);
    }

    // UPDATE
    public static String reqUpdate() {
        return "UPDATE BAIL SET DATE_DEBUT=?, DATE_FIN=?, LOYER_INITIAL=?, "
             + "PROVISION_INITIALES=?, SOLDE_TOUT_COMPTE=?, CAUTION=?, ETAT=?, JOUR_PAIEMENT=? "
             + "WHERE ID_BAIL=?";
    }

    public static void paramUpdate(PreparedStatement st, Bail bail) throws SQLException {
        st.setDate(1, new java.sql.Date(bail.getDateDebut().getTime()));

        if (bail.getDateFin() != null) st.setDate(2, new java.sql.Date(bail.getDateFin().getTime()));
        else st.setNull(2, Types.DATE);

        st.setDouble(3, bail.getLoyerInitial());
        st.setDouble(4, bail.getProvisionInitiales());

        if (bail.getSoldeToutCompte() != null) st.setDouble(5, bail.getSoldeToutCompte());
        else st.setNull(5, Types.NUMERIC);

        if (bail.getCaution() != null) st.setDouble(6, bail.getCaution());
        else st.setNull(6, Types.NUMERIC);

        st.setString(7, bail.getEtat());

        if (bail.getJourPaiement() != null) st.setInt(8, bail.getJourPaiement());
        else st.setNull(8, Types.INTEGER);

        st.setLong(9, bail.getIdBail());
    }

    // DELETE 
    public static String reqDelete() {
        return "DELETE FROM BAIL WHERE ID_BAIL=?";
    }

    public static void paramDelete(PreparedStatement st, Bail bail) throws SQLException {
        st.setLong(1, bail.getIdBail());
    }

    // FIND BY ID 
    public static String reqFindById() {
        return "SELECT * FROM BAIL WHERE ID_BAIL=?";
    }

    public static void paramFindById(PreparedStatement st, String id) throws SQLException {
        st.setLong(1, Long.parseLong(id));
    }

    // FIND ALL
    public static String reqFindAll() {
        return "SELECT * FROM BAIL ORDER BY ID_BAIL";
    }

    // FIND ACTIFS
    public static String reqFindActifs() {
        return "SELECT * FROM BAIL WHERE ETAT = 'EN_COURS' ORDER BY DATE_DEBUT DESC";
    }

    // UPDATE LOYER 
    public static String reqUpdateLoyer() {
        return "UPDATE BAIL SET LOYER_INITIAL = ? WHERE ID_BAIL = ?";
    }

    public static void paramUpdateLoyer(PreparedStatement st, long idBail, double nouveauLoyer) throws SQLException {
        st.setDouble(1, nouveauLoyer);
        st.setLong(2, idBail);
    }

    //  UPDATE PROVISIONS 
    public static String reqUpdateProvisions() {
        return "UPDATE BAIL SET PROVISION_INITIALES = ? WHERE ID_BAIL = ?";
    }

    public static void paramUpdateProvisions(PreparedStatement st, long idBail, double nouvelleProvision) throws SQLException {
        st.setDouble(1, nouvelleProvision);
        st.setLong(2, idBail);
    }

    //  FIND ALL FOR VIEW
    public static String reqFindAllForView() {
        return "SELECT b.ID_BAIL, " +
               "COALESCE(log.ADRESSE_LOGEMENT, gar.ADRESSE_GARAGE, 'Bail cloture') AS ADRESSE, " +
               "CASE " +
               "  WHEN log.ID_LOGEMENT IS NOT NULL AND gar.ID_GARAGE IS NOT NULL THEN 'Logement + Garage' " +
               "  WHEN log.ID_LOGEMENT IS NOT NULL THEN 'Logement' " +
               "  WHEN gar.ID_GARAGE IS NOT NULL THEN 'Garage' " +
               "  ELSE 'Bail cloture' " +
               "END AS TYPE_BIEN, " +
               "(SELECT LISTAGG(loc.NOM_LOC || ' ' || loc.PRENOM_LOC, ', ') " +
               " WITHIN GROUP (ORDER BY loc.NOM_LOC) " +
               " FROM SIGNE s JOIN LOCATAIRE loc ON s.ID_LOCATAIRE = loc.ID_LOCATAIRE " +
               " WHERE s.ID_BAIL = b.ID_BAIL) AS LOCATAIRES, " +
               "b.DATE_DEBUT, b.DATE_FIN, b.LOYER_INITIAL, b.CAUTION, b.ETAT " +
               "FROM BAIL b " +
               "LEFT JOIN LOGEMENT log ON log.ID_BAIL = b.ID_BAIL " +
               "LEFT JOIN GARAGE gar ON gar.ID_BAIL = b.ID_BAIL AND log.ID_LOGEMENT IS NULL " +
               "ORDER BY b.ID_BAIL DESC";
    }

    // PROCEDURES STOCKEES 
    
    // CREER BAIL COMPLET
    public static String reqCreerBailComplet() {
        return "{call CREERBAILCOMPLET(?, ?, ?, ?, ?, ?, ?, ?)}";
    }

    public static void paramCreerBailComplet(CallableStatement cs, long idBail, java.util.Date dateDebut,
            double loyerInitial, double provisions, Double caution, Integer jourPaiement,
            long idLogement, long idLocataire) throws SQLException {
        cs.setLong(1, idBail);
        cs.setDate(2, new java.sql.Date(dateDebut.getTime()));
        cs.setDouble(3, loyerInitial);
        cs.setDouble(4, provisions);

        if (caution != null) cs.setDouble(5, caution);
        else cs.setNull(5, Types.NUMERIC);

        if (jourPaiement != null) cs.setInt(6, jourPaiement);
        else cs.setNull(6, Types.INTEGER);

        cs.setLong(7, idLogement);
        cs.setLong(8, idLocataire);
    }

    // CREER BAIL GARAGE
    public static String reqCreerBailGarage() {
        return "{call CREERBAILGARAGE(?, ?, ?, ?, ?, ?, ?, ?)}";
    }

    public static void paramCreerBailGarage(CallableStatement cs, long idBail, java.util.Date dateDebut,
            double loyerInitial, double provisions, Double caution, Integer jourPaiement,
            long idGarage, long idLocataire) throws SQLException {
        cs.setLong(1, idBail);
        cs.setDate(2, new java.sql.Date(dateDebut.getTime()));
        cs.setDouble(3, loyerInitial);
        cs.setDouble(4, provisions);

        if (caution != null) cs.setDouble(5, caution);
        else cs.setNull(5, Types.NUMERIC);

        if (jourPaiement != null) cs.setInt(6, jourPaiement);
        else cs.setNull(6, Types.INTEGER);

        cs.setLong(7, idGarage);
        cs.setLong(8, idLocataire);
    }

    // AJOUTER LOCATAIRE AU BAIL
    public static String reqAjouterLocataireAuBail() {
        return "{call AJOUTERLOCATAIREAUBAIL(?, ?)}";
    }

    public static void paramAjouterLocataireAuBail(CallableStatement cs, long idBail, long idLocataire) throws SQLException {
        cs.setLong(1, idBail);
        cs.setLong(2, idLocataire);
    }

    // LIER GARAGE AU BAIL
    public static String reqLierGarageAuBail() {
        return "{call LIERGARAGEAUBAIL(?, ?)}";
    }

    public static void paramLierGarageAuBail(CallableStatement cs, long idBail, long idGarage) throws SQLException {
        cs.setLong(1, idBail);
        cs.setLong(2, idGarage);
    }

    // RESILIER BAIL
    public static String reqResilierBail() {
        return "{call RESILIERBAIL(?, ?)}";
    }

    public static void paramResilierBail(CallableStatement cs, long idBail, java.util.Date dateFin) throws SQLException {
        cs.setLong(1, idBail);
        cs.setDate(2, new java.sql.Date(dateFin.getTime()));
    }

    // CLOTURER BAIL
    public static String reqCloturerBail() {
        return "{call cloturerBail(?, ?, ?)}";
    }

    public static void paramCloturerBail(CallableStatement cs, long idBail, java.sql.Date dateFin, double montantSolde) throws SQLException {
        cs.setLong(1, idBail);
        cs.setDate(2, dateFin);
        cs.setDouble(3, montantSolde);
    }

    // GENERER LOYERS ANNUELS
    public static String reqGenererLoyersAnnuels() {
        return "{call GENERERLOYERSANNUELS(?, ?)}";
    }

    public static void paramGenererLoyersAnnuels(CallableStatement cs, long idBail, int annee) throws SQLException {
        cs.setLong(1, idBail);
        cs.setInt(2, annee);
    }

    // REVALORISER LOYER
    public static String reqRevaloriserLoyer() {
        return "{call REVALORISERLOYER(?, ?, ?, ?)}";
    }

    public static void paramRevaloriserLoyer(CallableStatement cs, long idBail, double nouveauLoyer, 
            int nouvelIRL, int ancienIRL) throws SQLException {
        cs.setLong(1, idBail);
        cs.setDouble(2, nouveauLoyer);
        cs.setInt(3, nouvelIRL);
        cs.setInt(4, ancienIRL);
    }

    // CALCULER SOLDE BAIL
    public static String reqCalculerSoldeBail() {
        return "{? = call calculerSoldeBail(?)}";
    }

    public static void paramCalculerSoldeBail(CallableStatement cs, long idBail) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setLong(2, idBail);
    }

    // PEUT REVALORISER
    public static String reqPeutRevaloriser() {
        return "{? = call PEUTREVALORISER(?)}";
    }

    public static void paramPeutRevaloriser(CallableStatement cs, long idBail) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setLong(2, idBail);
    }

    // GET DETAILS SOLDE
    public static String reqGetDetailsSolde() {
        return "{call getDetailsSolde(?, ?, ?, ?)}";
    }

    public static void paramGetDetailsSolde(CallableStatement cs, long idBail) throws SQLException {
        cs.setLong(1, idBail);
        cs.registerOutParameter(2, Types.NUMERIC);
        cs.registerOutParameter(3, Types.NUMERIC);
        cs.registerOutParameter(4, Types.NUMERIC);
    }






    public static String reqGetBatimentFromBail() {
        return "SELECT COALESCE(log.ID_BATIMENT, gar.ID_BATIMENT) AS ID_BATIMENT " +
               "FROM BAIL b " +
               "LEFT JOIN LOGEMENT log ON log.ID_BAIL = b.ID_BAIL " +
               "LEFT JOIN GARAGE gar ON gar.ID_BAIL = b.ID_BAIL " +
               "WHERE b.ID_BAIL = ?";
    }

    public static void paramGetBatimentFromBail(PreparedStatement st, long idBail) throws SQLException {
        st.setLong(1, idBail);
    }
}