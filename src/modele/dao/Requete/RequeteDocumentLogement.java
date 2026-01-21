package modele.dao.Requete;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Requetes SQL pour DocumentLogementDao
 */
public class RequeteDocumentLogement {


    // RELEVES COMPTEUR - avec quotites


    /**
     * Recupere tous les releves d'un logement
     */
    public static String reqGetRelevesCompteurs() {
        return "SELECT ID_RELEVE, TYPE, TO_CHAR(DATE_, 'DD/MM/YYYY') AS DATE_RELEVE, " +
               "ANCIEN_INDEX, NOUVELLE_INDEX, NUM_COMPTEUR, UNITE, " +
               "NVL(QUOTITE_EAU, 100) AS QUOTITE_EAU, " +
               "NVL(QUOTITE_ELECTRICITE, 100) AS QUOTITE_ELECTRICITE, " +
               "NVL(QUOTITE_OM, 100) AS QUOTITE_OM, " +
               "NVL(QUOTITE_ENTRETIEN, 100) AS QUOTITE_ENTRETIEN " +
               "FROM RELEVE_COMPTEUR " +
               "WHERE ID_LOGEMENT = ? " +
               "ORDER BY DATE_ DESC, TYPE";
    }

    public static void paramGetRelevesCompteurs(PreparedStatement ps, long idLogement) throws SQLException {
        ps.setLong(1, idLogement);
    }

    /**
     * Ajoute un releve compteur avec quotites
     */
    public static String reqAjouterReleveCompteur() {
        return "INSERT INTO RELEVE_COMPTEUR " +
               "(ID_RELEVE, ID_LOGEMENT, TYPE, DATE_, NUM_COMPTEUR, ANCIEN_INDEX, NOUVELLE_INDEX, UNITE, " +
               "QUOTITE_EAU, QUOTITE_ELECTRICITE, QUOTITE_OM, QUOTITE_ENTRETIEN) " +
               "VALUES ((SELECT NVL(MAX(ID_RELEVE), 0) + 1 FROM RELEVE_COMPTEUR), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    public static void paramAjouterReleveCompteur(PreparedStatement ps, long idLogement, String type, 
            java.util.Date date, long numCompteur, long ancienIndex, long nouvelIndex, String unite,
            double quotiteEau, double quotiteElec, double quotiteOM, double quotiteEntretien) throws SQLException {
        ps.setLong(1, idLogement);
        ps.setString(2, type);
        ps.setDate(3, new java.sql.Date(date.getTime()));
        ps.setLong(4, numCompteur);
        ps.setLong(5, ancienIndex);
        ps.setLong(6, nouvelIndex);
        ps.setString(7, unite);
        ps.setDouble(8, quotiteEau);
        ps.setDouble(9, quotiteElec);
        ps.setDouble(10, quotiteOM);
        ps.setDouble(11, quotiteEntretien);
    }

    /**
     * Modifie un releve compteur avec quotites
     */
    public static String reqModifierReleveCompteur() {
        return "UPDATE RELEVE_COMPTEUR SET " +
               "TYPE = ?, DATE_ = ?, NUM_COMPTEUR = ?, ANCIEN_INDEX = ?, NOUVELLE_INDEX = ?, UNITE = ?, " +
               "QUOTITE_EAU = ?, QUOTITE_ELECTRICITE = ?, QUOTITE_OM = ?, QUOTITE_ENTRETIEN = ? " +
               "WHERE ID_RELEVE = ?";
    }

    public static void paramModifierReleveCompteur(PreparedStatement ps, long idReleve, String type,
            java.util.Date date, long numCompteur, long ancienIndex, long nouvelIndex, String unite,
            double quotiteEau, double quotiteElec, double quotiteOM, double quotiteEntretien) throws SQLException {
        ps.setString(1, type);
        ps.setDate(2, new java.sql.Date(date.getTime()));
        ps.setLong(3, numCompteur);
        ps.setLong(4, ancienIndex);
        ps.setLong(5, nouvelIndex);
        ps.setString(6, unite);
        ps.setDouble(7, quotiteEau);
        ps.setDouble(8, quotiteElec);
        ps.setDouble(9, quotiteOM);
        ps.setDouble(10, quotiteEntretien);
        ps.setLong(11, idReleve);
    }

    /**
     * Supprime un releve compteur
     */
    public static String reqSupprimerReleveCompteur() {
        return "DELETE FROM RELEVE_COMPTEUR WHERE ID_RELEVE = ?";
    }

    public static void paramSupprimerReleveCompteur(PreparedStatement ps, long idReleve) throws SQLException {
        ps.setLong(1, idReleve);
    }


    // BAUX DU LOGEMENT


    public static String reqGetBauxLogement() {
        return "SELECT b.ID_BAIL, " +
               "  (SELECT LISTAGG(l.NOM_LOC || ' ' || l.PRENOM_LOC, ', ') WITHIN GROUP (ORDER BY l.NOM_LOC) " +
               "   FROM SIGNE s JOIN LOCATAIRE l ON s.ID_LOCATAIRE = l.ID_LOCATAIRE WHERE s.ID_BAIL = b.ID_BAIL) AS LOCATAIRES, " +
               "  b.DATE_DEBUT, b.DATE_FIN, b.LOYER_INITIAL, b.PROVISION_INITIALES, b.CAUTION, b.ETAT " +
               "FROM BAIL b " +
               "WHERE b.ID_BAIL = (SELECT ID_BAIL FROM LOGEMENT WHERE ID_LOGEMENT = ?) " +
               "ORDER BY b.DATE_DEBUT DESC";
    }

    public static void paramGetBauxLogement(PreparedStatement ps, long idLogement) throws SQLException {
        ps.setLong(1, idLogement);
    }


    // GARAGES LIES AU LOGEMENT


    public static String reqGetGaragesLogement() {
        return "SELECT g.ID_GARAGE, g.ADRESSE_GARAGE, g.SURFACE_GARAGE, g.NUM_FISCAL, " +
               "  (SELECT a.NOM_COMPAGNIE FROM ASSURANCE a WHERE a.ID_GARAGE = g.ID_GARAGE AND ROWNUM = 1) AS ASSURANCE " +
               "FROM GARAGE g " +
               "WHERE g.ID_LOGEMENT = ? " +
               "ORDER BY g.ADRESSE_GARAGE";
    }

    public static void paramGetGaragesLogement(PreparedStatement ps, long idLogement) throws SQLException {
        ps.setLong(1, idLogement);
    }


    // GARANTS


    public static String reqGetGarantsLogement() {
        return "SELECT g.ID_GARANT, g.NOM_GARANT, g.PRENOM_GARANT, g.ADRESSE_GARANT, g.MAIL_GARANT, g.TEL_GARANT " +
               "FROM GARANT g " +
               "JOIN AJOUTE a ON g.ID_GARANT = a.ID_GARANT " +
               "WHERE a.ID_BAIL = (SELECT ID_BAIL FROM LOGEMENT WHERE ID_LOGEMENT = ?) " +
               "ORDER BY g.NOM_GARANT";
    }

    public static void paramGetGarantsLogement(PreparedStatement ps, long idLogement) throws SQLException {
        ps.setLong(1, idLogement);
    }


    // ETATS DES LIEUX


    public static String reqGetEtatsDesLieux() {
        return "SELECT e.ID_EDL, e.TYPE, e.DATE_EDL, e.OBSERVATIONS " +
               "FROM ETATDESLIEUX e " +
               "WHERE e.ID_BAIL = (SELECT ID_BAIL FROM LOGEMENT WHERE ID_LOGEMENT = ?) " +
               "ORDER BY e.DATE_EDL DESC";
    }

    public static void paramGetEtatsDesLieux(PreparedStatement ps, long idLogement) throws SQLException {
        ps.setLong(1, idLogement);
    }


    // ASSURANCES


    public static String reqGetAssurances() {
        return "SELECT NUMASSURANCE, NOM_COMPAGNIE, TYPE, PRIMEBASE, DATE_EFFET, " +
               "  (SELECT MAX(E.DATE_) FROM ECHEANCE E WHERE E.NUMASSURANCE = A.NUMASSURANCE) AS DATE_ECHEANCE " +
               "FROM ASSURANCE A " +
               "WHERE A.ID_LOGEMENT = ? " +
               "ORDER BY DATE_EFFET DESC";
    }

    public static void paramGetAssurances(PreparedStatement ps, long idLogement) throws SQLException {
        ps.setLong(1, idLogement);
    }


    // LOYERS EN ATTENTE


    public static String reqGetLoyersEnAttenteLogement() {
        return "SELECT l.ID_LOYER, l.MOIS, l.MONTANT_LOYER, l.MONTANT_PROVISION, " +
               "  (l.MONTANT_LOYER + NVL(l.MONTANT_PROVISION, 0)) AS TOTAL, l.STATUT " +
               "FROM LOYER l " +
               "WHERE l.ID_BAIL = (SELECT ID_BAIL FROM LOGEMENT WHERE ID_LOGEMENT = ?) " +
               "  AND UPPER(l.STATUT) IN ('EN_ATTENTE', 'RETARD') " +
               "ORDER BY l.MOIS";
    }

    public static void paramGetLoyersEnAttenteLogement(PreparedStatement ps, long idLogement) throws SQLException {
        ps.setLong(1, idLogement);
    }


    // QUITTANCES (loyers payes)


    public static String reqGetQuittancesLogement() {
        return "SELECT l.ID_LOYER, l.MOIS, l.MONTANT_LOYER, l.MONTANT_PROVISION, " +
               "  (l.MONTANT_LOYER + NVL(l.MONTANT_PROVISION, 0)) AS TOTAL, l.DATE_PAIEMENT, l.QUITTANCE " +
               "FROM LOYER l " +
               "WHERE l.ID_BAIL = (SELECT ID_BAIL FROM LOGEMENT WHERE ID_LOGEMENT = ?) " +
               "  AND UPPER(l.STATUT) = 'PAYE' " +
               "ORDER BY l.DATE_PAIEMENT DESC";
    }

    public static void paramGetQuittancesLogement(PreparedStatement ps, long idLogement) throws SQLException {
        ps.setLong(1, idLogement);
    }
}