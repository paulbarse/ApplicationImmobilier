package modele.dao.Requete;

import java.sql.*;

/**
 * Requêtes SQL pour DocumentGarageDao
 */
public class RequeteDocumentGarage {


    // UTILITAIRES

    
    public static String reqGetBailActifGarage() {
        return "{? = call getBailActifGarage(?)}";
    }
    
    public static void paramGetBailActifGarage(CallableStatement cs, long idGarage) throws SQLException {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setLong(2, idGarage);
    }


    // 1. LOCATAIRE

    public static String reqGetLocataireGarage() {

        return "SELECT loc.id_locataire, loc.nom_loc, loc.prenom_loc, " +
               "loc.mail_loc, loc.tel_loc, " +
               "NULL AS adresse_loc " +  
               "FROM LOCATAIRE loc " +
               "JOIN SIGNE s ON loc.id_locataire = s.id_locataire " +
               "JOIN GARAGE g ON g.id_bail = s.id_bail " +
               "JOIN BAIL b ON b.id_bail = g.id_bail " +
               "WHERE g.id_garage = ? AND b.etat = 'EN_COURS' ORDER BY loc.nom_loc";
    }
    public static void paramGetLocataireGarage(PreparedStatement ps, long idGarage) throws SQLException {
        ps.setLong(1, idGarage);
    }


    // 2. BAIL

    
    public static String reqGetBauxGarage() {
        return "SELECT b.id_bail, b.date_debut, b.date_fin, " +
               "b.loyer_initial, b.etat, " +
               "(SELECT LISTAGG(loc.nom_loc || ' ' || loc.prenom_loc, ', ') " +
               " WITHIN GROUP (ORDER BY loc.nom_loc) " +
               " FROM SIGNE s JOIN LOCATAIRE loc ON s.id_locataire = loc.id_locataire " +
               " WHERE s.id_bail = b.id_bail) AS locataires " +
               "FROM BAIL b " +
               "WHERE b.id_bail = (SELECT id_bail FROM GARAGE WHERE id_garage = ?) " +
               "ORDER BY b.date_debut DESC";
    }
    
    public static void paramGetBauxGarage(PreparedStatement ps, long idGarage) throws SQLException {
        ps.setLong(1, idGarage);
    }


    // 3. DÉPÔT DE GARANTIE

    
    public static String reqGetDepotGarantieGarage() {
        return "SELECT mc.id_mouvement, mc.date_mouvement, mc.type_mouvement, " +
               "mc.montant, mc.moyen_paiement, mc.observations " +
               "FROM MOUVEMENT_CAUTION mc " +
               "JOIN GARAGE g ON g.id_bail = mc.id_bail " +
               "WHERE g.id_garage = ? ORDER BY mc.date_mouvement DESC";
    }
    
    public static void paramGetDepotGarantieGarage(PreparedStatement ps, long idGarage) throws SQLException {
        ps.setLong(1, idGarage);
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
    
    public static String reqModifierDepotGarantie() {
        return "UPDATE MOUVEMENT_CAUTION SET date_mouvement = ?, type_mouvement = ?, " +
               "montant = ?, moyen_paiement = ?, observations = ? WHERE id_mouvement = ?";
    }
    
    public static void paramModifierDepotGarantie(PreparedStatement ps, long idMouvement, java.util.Date date,
            String type, double montant, String moyenPaiement, String observations) throws SQLException {
        ps.setDate(1, new java.sql.Date(date.getTime()));
        ps.setString(2, type);
        ps.setDouble(3, montant);
        ps.setString(4, moyenPaiement);
        ps.setString(5, observations);
        ps.setLong(6, idMouvement);
    }
    
    public static String reqSupprimerDepotGarantie() {
        return "DELETE FROM MOUVEMENT_CAUTION WHERE id_mouvement = ?";
    }
    
    public static void paramSupprimerDepotGarantie(PreparedStatement ps, long idMouvement) throws SQLException {
        ps.setLong(1, idMouvement);
    }


    // 4. RELEVÉS COMPTEURS

    
    public static String reqGetRelevesCompteursGarage() {
        return "SELECT rc.id_releve, rc.type, rc.date_, rc.ancien_index, " +
               "rc.nouvelle_index, rc.num_compteur, rc.unite, " +
               "(rc.nouvelle_index - rc.ancien_index) AS conso " +
               "FROM RELEVE_COMPTEUR rc " +
               "JOIN GARAGE g ON g.id_releve = rc.id_releve " +
               "WHERE g.id_garage = ? ORDER BY rc.date_ DESC";
    }
    
    public static void paramGetRelevesCompteursGarage(PreparedStatement ps, long idGarage) throws SQLException {
        ps.setLong(1, idGarage);
    }
    
    public static String reqAjouterReleveCompteurGarage() {
        return "INSERT INTO RELEVE_COMPTEUR (id_releve, type, date_, ancien_index, " +
               "nouvelle_index, num_compteur, unite) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }
    
    public static void paramAjouterReleveCompteurGarage(PreparedStatement ps, long idReleve, String type,
            java.util.Date date, long ancienIndex, long nouvelIndex, long numCompteur, String unite) throws SQLException {
        ps.setLong(1, idReleve);
        ps.setString(2, type);
        ps.setDate(3, new java.sql.Date(date.getTime()));
        ps.setLong(4, ancienIndex);
        ps.setLong(5, nouvelIndex);
        ps.setLong(6, numCompteur);
        ps.setString(7, unite);
    }
    
    public static String reqLierReleveAuGarage() {
        return "UPDATE GARAGE SET id_releve = ? WHERE id_garage = ?";
    }
    
    public static void paramLierReleveAuGarage(PreparedStatement ps, long idReleve, long idGarage) throws SQLException {
        ps.setLong(1, idReleve);
        ps.setLong(2, idGarage);
    }
    
    public static String reqModifierReleveCompteurGarage() {
        return "UPDATE RELEVE_COMPTEUR SET type = ?, date_ = ?, ancien_index = ?, " +
               "nouvelle_index = ?, num_compteur = ?, unite = ? WHERE id_releve = ?";
    }
    
    public static void paramModifierReleveCompteurGarage(PreparedStatement ps, long idReleve, String type,
            java.util.Date date, long ancienIndex, long nouvelIndex, long numCompteur, String unite) throws SQLException {
        ps.setString(1, type);
        ps.setDate(2, new java.sql.Date(date.getTime()));
        ps.setLong(3, ancienIndex);
        ps.setLong(4, nouvelIndex);
        ps.setLong(5, numCompteur);
        ps.setString(6, unite);
        ps.setLong(7, idReleve);
    }
    
    public static String reqDelierReleveGarage() {
        return "UPDATE GARAGE SET id_releve = NULL WHERE id_releve = ?";
    }
    
    public static void paramDelierReleveGarage(PreparedStatement ps, long idReleve) throws SQLException {
        ps.setLong(1, idReleve);
    }
    
    public static String reqSupprimerReleveCompteur() {
        return "DELETE FROM RELEVE_COMPTEUR WHERE id_releve = ?";
    }
    
    public static void paramSupprimerReleveCompteur(PreparedStatement ps, long idReleve) throws SQLException {
        ps.setLong(1, idReleve);
    }


    // 5. ASSURANCES

    
    public static String reqGetAssurancesGarage() {
        return "SELECT a.numassurance, a.nom_compagnie, a.type, a.primebase, a.date_effet, " +
               "(SELECT e.date_ FROM ECHEANCE e WHERE e.numassurance = a.numassurance AND ROWNUM = 1) AS date_echeance " +
               "FROM ASSURANCE a WHERE a.id_garage = ? ORDER BY a.date_effet DESC";
    }
    
    public static void paramGetAssurancesGarage(PreparedStatement ps, long idGarage) throws SQLException {
        ps.setLong(1, idGarage);
    }
    
    public static String reqAjouterAssuranceGarage() {
        return "{call ajouterAssuranceGarage(?, ?, ?, ?, ?, ?, ?)}";
    }
    
    public static void paramAjouterAssuranceGarage(CallableStatement cs, long numAssurance, String nomCompagnie,
            String type, double primeBase, java.util.Date dateEffet, java.util.Date dateEcheance,
            long idGarage) throws SQLException {
        cs.setLong(1, numAssurance);
        cs.setString(2, nomCompagnie);
        cs.setString(3, type);
        cs.setDouble(4, primeBase);
        cs.setDate(5, new java.sql.Date(dateEffet.getTime()));
        cs.setDate(6, new java.sql.Date(dateEcheance.getTime()));
        cs.setLong(7, idGarage);
    }


    // 6. LOYERS EN ATTENTE

    
    public static String reqGetLoyersEnAttenteGarage() {
        return "SELECT l.id_loyer, l.mois AS periode, l.montant_loyer, l.montant_provision, " +
               "(l.montant_loyer + NVL(l.montant_provision, 0)) AS total, l.statut " +
               "FROM LOYER l JOIN GARAGE g ON g.id_bail = l.id_bail " +
               "WHERE g.id_garage = ? AND (l.statut = 'En attente' OR l.statut IS NULL) " +
               "ORDER BY l.mois DESC";
    }
    
    public static void paramGetLoyersEnAttenteGarage(PreparedStatement ps, long idGarage) throws SQLException {
        ps.setLong(1, idGarage);
    }


    // 7. QUITTANCES (LOYERS PAYÉS)

    
    public static String reqGetQuittancesPayeesGarage() {
        return "SELECT l.id_loyer, l.mois AS periode, l.montant_loyer, l.montant_provision, " +
               "(l.montant_loyer + NVL(l.montant_provision, 0)) AS total, l.date_paiement, l.quittance " +
               "FROM LOYER l JOIN GARAGE g ON g.id_bail = l.id_bail " +
               "WHERE g.id_garage = ? AND l.statut = 'Paye' ORDER BY l.mois DESC";
    }
    
    public static void paramGetQuittancesPayeesGarage(PreparedStatement ps, long idGarage) throws SQLException {
        ps.setLong(1, idGarage);
    }
}
