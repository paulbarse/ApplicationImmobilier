package modele.dao;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

import modele.dao.Requete.RequeteDocumentLocataire;

/**
 * DAO pour gérer les documents d'un locataire
 */
public class DocumentLocataireDao {

    private Connection cn;

    public DocumentLocataireDao() throws SQLException {
        cn = UtOracleDataSource.getConnection();
        if (cn == null || cn.isClosed()) {
            UtOracleDataSource.creerAcces();
            cn = UtOracleDataSource.getConnection();
        }
    }


    // UTILITAIRES


    public Long getBailActifLocataire(long idLocataire) throws SQLException {
        try (CallableStatement cs = cn
            .prepareCall(RequeteDocumentLocataire.reqGetBailActifLocataire())) {
            RequeteDocumentLocataire.paramGetBailActifLocataire(cs,
                idLocataire);
            cs.execute();
            long result = cs.getLong(1);
            return cs.wasNull() ? null : result;
        }
    }

    public Long getLogementLocataire(long idLocataire) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(
            RequeteDocumentLocataire.reqGetLogementByLocataire())) {
            RequeteDocumentLocataire.paramGetLogementByLocataire(cs,
                idLocataire);
            cs.execute();
            long result = cs.getLong(1);
            return cs.wasNull() ? null : result;
        }
    }

    private long getNextId(String tableName, String columnName)
        throws SQLException {
        try (CallableStatement cs = cn
            .prepareCall(RequeteDocumentLocataire.reqGetNextId())) {
            RequeteDocumentLocataire.paramGetNextId(cs, tableName, columnName);
            cs.execute();
            return cs.getLong(1);
        }
    }


    // 1. DIAGNOSTICS


    public List<Object[]> getDiagnosticsLocataire(long idLocataire)
        throws SQLException {
        List<Object[]> liste = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(
            RequeteDocumentLocataire.reqGetDiagnosticsLocataire())) {
            RequeteDocumentLocataire.paramGetDiagnosticsLocataire(st,
                idLocataire);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                liste.add(new Object[] { rs.getLong("id_diagnostique"),
                    rs.getString("type_diag"), rs.getString("reference"),
                    rs.getDate("date_emission"), rs.getDate("date_expiration"),
                    rs.getString("statut") });
            }
        }
        return liste;
    }

    public void ajouterDiagnostic(long idLocataire, String typeDiag,
        String reference, java.util.Date dateEmission,
        java.util.Date dateExpiration) throws SQLException {
        Long idLogement = getLogementLocataire(idLocataire);
        if (idLogement == null) {
            throw new SQLException("Aucun logement trouvé pour ce locataire");
        }

        long newId = getNextId("DIAGNOSTIQUE", "id_diagnostique");

        try (PreparedStatement st = cn.prepareStatement(
            RequeteDocumentLocataire.reqAjouterDiagnostic())) {
            RequeteDocumentLocataire.paramAjouterDiagnostic(st, newId, typeDiag,
                reference, dateEmission, dateExpiration, idLogement);
            st.executeUpdate();
        }
    }

    public void modifierDiagnostic(long idDiagnostic, String typeDiag,
        String reference, java.util.Date dateEmission,
        java.util.Date dateExpiration) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(
            RequeteDocumentLocataire.reqModifierDiagnostic())) {
            RequeteDocumentLocataire.paramModifierDiagnostic(st, idDiagnostic,
                typeDiag, reference, dateEmission, dateExpiration);
            st.executeUpdate();
        }
    }

    public void supprimerDiagnostic(long idDiagnostic) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(
            RequeteDocumentLocataire.reqSupprimerDiagnostic())) {
            RequeteDocumentLocataire.paramSupprimerDiagnostic(st, idDiagnostic);
            st.executeUpdate();
        }
    }


    // 2. INDEX / COMPTEURS


    public List<Object[]> getIndexLocataire(long idLocataire)
        throws SQLException {
        List<Object[]> liste = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(
            RequeteDocumentLocataire.reqGetIndexLocataire())) {
            RequeteDocumentLocataire.paramGetIndexLocataire(st, idLocataire);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                liste.add(new Object[] { rs.getLong("id_releve"),
                    rs.getString("type"), rs.getDate("date_"),
                    rs.getLong("num_compteur"), rs.getLong("ancien_index"),
                    rs.getLong("nouvelle_index"), rs.getLong("conso"),
                    rs.getString("unite") });
            }
        }
        return liste;
    }

    public void ajouterIndex(long idLocataire, String type, java.util.Date date,
        long numCompteur, long ancienIndex, long nouvelIndex, String unite)
        throws SQLException {
        Long idLogement = getLogementLocataire(idLocataire);
        if (idLogement == null) {
            throw new SQLException("Aucun logement trouvé pour ce locataire");
        }

        long newId = getNextId("RELEVE_COMPTEUR", "id_releve");

        try (PreparedStatement st = cn
            .prepareStatement(RequeteDocumentLocataire.reqAjouterIndex())) {
            RequeteDocumentLocataire.paramAjouterIndex(st, newId, type, date,
                numCompteur, ancienIndex, nouvelIndex, unite);
            st.executeUpdate();
        }

        try (PreparedStatement st = cn.prepareStatement(
            RequeteDocumentLocataire.reqLierReleveAuLogement())) {
            RequeteDocumentLocataire.paramLierReleveAuLogement(st, newId,
                idLogement);
            st.executeUpdate();
        }
    }

    public void modifierIndex(long idReleve, String type, java.util.Date date,
        long numCompteur, long ancienIndex, long nouvelIndex, String unite)
        throws SQLException {
        try (PreparedStatement st = cn
            .prepareStatement(RequeteDocumentLocataire.reqModifierIndex())) {
            RequeteDocumentLocataire.paramModifierIndex(st, idReleve, type,
                date, numCompteur, ancienIndex, nouvelIndex, unite);
            st.executeUpdate();
        }
    }

    public void supprimerIndex(long idReleve) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(
            RequeteDocumentLocataire.reqDelierReleveLogement())) {
            RequeteDocumentLocataire.paramDelierReleveLogement(st, idReleve);
            st.executeUpdate();
        }

        try (PreparedStatement st = cn
            .prepareStatement(RequeteDocumentLocataire.reqSupprimerIndex())) {
            RequeteDocumentLocataire.paramSupprimerIndex(st, idReleve);
            st.executeUpdate();
        }
    }


    // MÉTHODES À AJOUTER DANS DocumentLocataireDao.java


    /**
     * Récupère tous les relevés d'eau pour un locataire
     */
    public List<Object[]> getRelevesEauLocataire(long idLocataire)
        throws SQLException {
        List<Object[]> list = new ArrayList<>();

        String sql = "SELECT r.ID_RELEVE, r.DATE_, r.ANCIEN_INDEX, r.NOUVELLE_INDEX "
            + "FROM RELEVE_COMPTEUR r "
            + "INNER JOIN LOGEMENT l ON r.ID_RELEVE = l.ID_RELEVE "
            + "INNER JOIN LOCATION loc ON l.ID_LOGEMENT = loc.ID_LOGEMENT "
            + "WHERE loc.ID_LOCATAIRE = ? " + "AND r.TYPE = 'Eau' "
            + "AND loc.DATE_FIN IS NULL " + // Seulement les locations actives
            "ORDER BY r.DATE_ DESC";

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idLocataire);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[] { rs.getLong("ID_RELEVE"),
                        rs.getDate("DATE_"), rs.getInt("ANCIEN_INDEX"),
                        rs.getInt("NOUVELLE_INDEX") });
                }
            }
        }

        return list;
    }

    /**
     * Ajoute un nouveau relevé d'eau pour le logement du locataire
     */
    public void ajouterReleveEau(long idLocataire, java.util.Date date,
        int ancienIndex, int nouveauIndex) throws SQLException {

        // 1. Récupérer l'ID du logement actuel du locataire
        String sqlLogement = "SELECT l.ID_LOGEMENT " + "FROM LOGEMENT l "
            + "INNER JOIN LOCATION loc ON l.ID_LOGEMENT = loc.ID_LOGEMENT "
            + "WHERE loc.ID_LOCATAIRE = ? AND loc.DATE_FIN IS NULL";

        long idLogement;
        try (PreparedStatement ps = cn.prepareStatement(sqlLogement)) {
            ps.setLong(1, idLocataire);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException(
                        "Aucun logement actif trouvé pour ce locataire");
                }
                idLogement = rs.getLong("ID_LOGEMENT");
            }
        }

        // 2. Récupérer le prochain ID de relevé
        String sqlNextId = "SELECT NVL(MAX(ID_RELEVE), 0) + 1 AS NEXT_ID FROM RELEVE_COMPTEUR";
        int nextId;
        try (Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sqlNextId)) {
            rs.next();
            nextId = rs.getInt("NEXT_ID");
        }

        // 3. Insérer le relevé
        String sqlInsert = "INSERT INTO RELEVE_COMPTEUR "
            + "(ID_RELEVE, UNITE, TYPE, NOUVELLE_INDEX, DATE_, NUM_COMPTEUR, ANCIEN_INDEX) "
            + "VALUES (?, 'm³', 'Eau', ?, ?, 1, ?)";

        try (PreparedStatement ps = cn.prepareStatement(sqlInsert)) {
            ps.setInt(1, nextId);
            ps.setInt(2, nouveauIndex);
            ps.setDate(3, new java.sql.Date(date.getTime()));
            ps.setInt(4, ancienIndex);
            ps.executeUpdate();
        }

        // 4. Lier le relevé au logement
        String sqlLier = "UPDATE LOGEMENT SET ID_RELEVE = ? WHERE ID_LOGEMENT = ?";
        try (PreparedStatement ps = cn.prepareStatement(sqlLier)) {
            ps.setInt(1, nextId);
            ps.setLong(2, idLogement);
            ps.executeUpdate();
        }
    }

    /**
     * Modifie un relevé d'eau existant
     */
    public void modifierReleveEau(long idReleve, java.util.Date date,
        int ancienIndex, int nouveauIndex) throws SQLException {

        String sql = "UPDATE RELEVE_COMPTEUR "
            + "SET DATE_ = ?, ANCIEN_INDEX = ?, NOUVELLE_INDEX = ? "
            + "WHERE ID_RELEVE = ?";

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(date.getTime()));
            ps.setInt(2, ancienIndex);
            ps.setInt(3, nouveauIndex);
            ps.setLong(4, idReleve);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Relevé non trouvé");
            }
        }
    }

    /**
     * Supprime un relevé d'eau
     */
    public void supprimerReleveEau(long idReleve) throws SQLException {

        // 1. Délier le relevé du logement
        String sqlDelier = "UPDATE LOGEMENT SET ID_RELEVE = NULL WHERE ID_RELEVE = ?";
        try (PreparedStatement ps = cn.prepareStatement(sqlDelier)) {
            ps.setLong(1, idReleve);
            ps.executeUpdate();
        }

        // 2. Supprimer le relevé
        String sqlDelete = "DELETE FROM RELEVE_COMPTEUR WHERE ID_RELEVE = ?";
        try (PreparedStatement ps = cn.prepareStatement(sqlDelete)) {
            ps.setLong(1, idReleve);
            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                throw new SQLException("Relevé non trouvé");
            }
        }
    }


    // 3. MOUVEMENTS DE CAUTION


    public List<Object[]> getMouvementsCaution(long idLocataire)
        throws SQLException {
        List<Object[]> liste = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(
            RequeteDocumentLocataire.reqGetMouvementsCaution())) {
            RequeteDocumentLocataire.paramGetMouvementsCaution(st, idLocataire);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                liste.add(new Object[] { rs.getLong("id_mouvement"),
                    rs.getDate("date_mouvement"),
                    rs.getString("type_mouvement"), rs.getDouble("montant"),
                    rs.getString("moyen_paiement"),
                    rs.getString("observations") });
            }
        }
        return liste;
    }

    public void ajouterMouvementCaution(long idLocataire, java.util.Date date,
        String type, double montant, String moyenPaiement, String observations)
        throws SQLException {
        Long idBail = getBailActifLocataire(idLocataire);
        if (idBail == null) {
            throw new SQLException("Aucun bail actif pour ce locataire");
        }

        try (CallableStatement cs = cn.prepareCall(
            RequeteDocumentLocataire.reqAjouterMouvementCaution())) {
            RequeteDocumentLocataire.paramAjouterMouvementCaution(cs, idBail,
                date, type, montant, moyenPaiement, observations);
            cs.execute();
        }
    }

    public void modifierMouvementCaution(long idMouvement, java.util.Date date,
        String type, double montant, String moyenPaiement, String observations)
        throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(
            RequeteDocumentLocataire.reqModifierMouvementCaution())) {
            RequeteDocumentLocataire.paramModifierMouvementCaution(st,
                idMouvement, date, type, montant, moyenPaiement, observations);
            st.executeUpdate();
        }
    }

    public void supprimerMouvementCaution(long idMouvement)
        throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(
            RequeteDocumentLocataire.reqSupprimerMouvementCaution())) {
            RequeteDocumentLocataire.paramSupprimerMouvementCaution(st,
                idMouvement);
            st.executeUpdate();
        }
    }


    // 4. LOYERS EN ATTENTE


    public List<Object[]> getLoyersEnAttente(long idLocataire)
        throws SQLException {
        List<Object[]> liste = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(
            RequeteDocumentLocataire.reqGetLoyersEnAttente())) {
            RequeteDocumentLocataire.paramGetLoyersEnAttente(st, idLocataire);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                liste.add(new Object[] { rs.getLong("id_loyer"),
                    rs.getString("mois"), rs.getDouble("montant_loyer"),
                    rs.getDouble("montant_provision"), rs.getDouble("total"),
                    rs.getString("statut") });
            }
        }
        return liste;
    }


    // 5. QUITTANCES


    public List<Object[]> getQuittancesPayees(long idLocataire)
        throws SQLException {
        List<Object[]> liste = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(
            RequeteDocumentLocataire.reqGetQuittancesPayees())) {
            RequeteDocumentLocataire.paramGetQuittancesPayees(st, idLocataire);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                liste.add(new Object[] { rs.getLong("id_loyer"),
                    rs.getString("mois"), rs.getDouble("montant_loyer"),
                    rs.getDouble("montant_provision"), rs.getDouble("total"),
                    rs.getDate("date_paiement"), rs.getString("quittance") });
            }
        }
        return liste;
    }


    // 6. GESTION DES LOYERS


    public void ajouterLoyer(long idLocataire, String mois, double loyerNu,
        double charges, String statut, java.util.Date datePaiement)
        throws SQLException {
        Long idBail = getBailActifLocataire(idLocataire);
        if (idBail == null) {
            throw new SQLException("Aucun bail actif trouvé pour ce locataire");
        }

        long newId = getNextId("LOYER", "id_loyer");

        try (PreparedStatement st = cn
            .prepareStatement(RequeteDocumentLocataire.reqAjouterLoyer())) {
            RequeteDocumentLocataire.paramAjouterLoyer(st, newId, mois, loyerNu,
                charges, statut, datePaiement, idBail);
            st.executeUpdate();
        }
    }

    public void modifierLoyer(long idLoyer, String mois, double loyerNu,
        double charges) throws SQLException {
        try (PreparedStatement st = cn
            .prepareStatement(RequeteDocumentLocataire.reqModifierLoyer())) {
            RequeteDocumentLocataire.paramModifierLoyer(st, idLoyer, mois,
                loyerNu, charges);
            st.executeUpdate();
        }
    }

    public void supprimerLoyer(long idLoyer) throws SQLException {
        try (PreparedStatement st = cn
            .prepareStatement(RequeteDocumentLocataire.reqSupprimerLoyer())) {
            RequeteDocumentLocataire.paramSupprimerLoyer(st, idLoyer);
            st.executeUpdate();
        }
    }

    public void marquerLoyerPaye(long idLoyer, java.util.Date datePaiement)
        throws SQLException {
        try (CallableStatement cs = cn
            .prepareCall(RequeteDocumentLocataire.reqMarquerLoyerPaye())) {
            RequeteDocumentLocataire.paramMarquerLoyerPaye(cs, idLoyer,
                datePaiement);
            cs.execute();
        }
    }

    public void annulerPaiement(long idLoyer) throws SQLException {
        try (CallableStatement cs = cn
            .prepareCall(RequeteDocumentLocataire.reqAnnulerPaiementLoyer())) {
            RequeteDocumentLocataire.paramAnnulerPaiementLoyer(cs, idLoyer);
            cs.execute();
        }
    }

    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) {
            cn.close();
        }
    }
}
