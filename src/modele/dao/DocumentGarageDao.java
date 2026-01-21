package modele.dao;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

import modele.dao.Requete.RequeteDocumentGarage;

/**
 * DAO pour gerer les documents d'un garage
 */
public class DocumentGarageDao extends AbstractDocumentDao {

    public DocumentGarageDao() throws SQLException {
        super();
    }


    // UTILITAIRES


    public Long getBailActifGarage(long idGarage) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteDocumentGarage.reqGetBailActifGarage())) {
            RequeteDocumentGarage.paramGetBailActifGarage(cs, idGarage);
            cs.execute();
            long result = cs.getLong(1);
            return cs.wasNull() ? null : result;
        } catch (SQLException e) {
            // Fallback SQL direct si procedure non disponible
            String sql = "SELECT ID_BAIL FROM GARAGE WHERE ID_GARAGE = ? AND ID_BAIL IS NOT NULL";
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setLong(1, idGarage);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
            return null;
        }
    }


    // 1. LOCATAIRE


    public List<Object[]> getLocataireGarage(long idGarage) throws SQLException {
        List<Object[]> liste = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(RequeteDocumentGarage.reqGetLocataireGarage())) {
            RequeteDocumentGarage.paramGetLocataireGarage(st, idGarage);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                liste.add(new Object[]{
                    rs.getLong("id_locataire"),
                    rs.getString("nom_loc"),
                    rs.getString("prenom_loc"),
                    rs.getString("mail_loc"),
                    rs.getString("tel_loc"),
                    rs.getString("adresse_loc")
                });
            }
        }
        return liste;
    }


    // 2. BAIL


    public List<Object[]> getBauxGarage(long idGarage) throws SQLException {
        List<Object[]> liste = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(RequeteDocumentGarage.reqGetBauxGarage())) {
            RequeteDocumentGarage.paramGetBauxGarage(st, idGarage);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                liste.add(new Object[]{
                    rs.getLong("id_bail"),
                    rs.getDate("date_debut"),
                    rs.getDate("date_fin"),
                    rs.getDouble("loyer_initial"),
                    rs.getString("etat"),
                    rs.getString("locataires")
                });
            }
        }
        return liste;
    }


    // 3. DEPOT DE GARANTIE


    public List<Object[]> getDepotGarantieGarage(long idGarage) throws SQLException {
        List<Object[]> liste = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(RequeteDocumentGarage.reqGetDepotGarantieGarage())) {
            RequeteDocumentGarage.paramGetDepotGarantieGarage(st, idGarage);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                liste.add(new Object[]{
                    rs.getLong("id_mouvement"),
                    rs.getDate("date_mouvement"),
                    rs.getString("type_mouvement"),
                    rs.getDouble("montant"),
                    rs.getString("moyen_paiement"),
                    rs.getString("observations")
                });
            }
        }
        return liste;
    }

    public void ajouterDepotGarantie(long idGarage, java.util.Date date, String type,
                                     double montant, String moyenPaiement,
                                     String observations) throws SQLException {
        Long idBail = getBailActifGarage(idGarage);
        if (idBail == null) {
            throw new SQLException("Aucun bail actif pour ce garage");
        }
        try (CallableStatement cs = cn.prepareCall(RequeteDocumentGarage.reqAjouterMouvementCaution())) {
            RequeteDocumentGarage.paramAjouterMouvementCaution(cs, idBail, date, type, montant, moyenPaiement, observations);
            cs.execute();
        } catch (SQLException e) {
            // Fallback SQL direct
            String sql = "INSERT INTO MOUVEMENT_CAUTION (ID_MOUVEMENT, ID_BAIL, DATE_MOUVEMENT, TYPE_MOUVEMENT, MONTANT, MOYEN_PAIEMENT, OBSERVATIONS) " +
                         "VALUES ((SELECT NVL(MAX(ID_MOUVEMENT), 0) + 1 FROM MOUVEMENT_CAUTION), ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setLong(1, idBail);
                ps.setDate(2, new java.sql.Date(date.getTime()));
                ps.setString(3, type);
                ps.setDouble(4, montant);
                ps.setString(5, moyenPaiement);
                ps.setString(6, observations);
                ps.executeUpdate();
            }
        }
    }

    public void modifierDepotGarantie(long idMouvement, java.util.Date date, String type,
                                      double montant, String moyenPaiement,
                                      String observations) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteDocumentGarage.reqModifierDepotGarantie())) {
            RequeteDocumentGarage.paramModifierDepotGarantie(st, idMouvement, date, type, montant, moyenPaiement, observations);
            st.executeUpdate();
        }
    }

    public void supprimerDepotGarantie(long idMouvement) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteDocumentGarage.reqSupprimerDepotGarantie())) {
            RequeteDocumentGarage.paramSupprimerDepotGarantie(st, idMouvement);
            st.executeUpdate();
        }
    }


    // 4. RELEVES COMPTEURS - AVEC QUOTITES


    /**
     * Retourne les releves compteur d'un garage avec quotites
     */
    public List<Object[]> getRelevesCompteursGarage(long idGarage) throws SQLException {
        List<Object[]> liste = new ArrayList<>();

        String sql = "SELECT ID_RELEVE, TYPE, DATE_, ANCIEN_INDEX, NOUVELLE_INDEX, NUM_COMPTEUR, UNITE, " +
                     "NVL(QUOTITE_EAU, 100) AS QUOTITE_EAU, " +
                     "NVL(QUOTITE_ELECTRICITE, 100) AS QUOTITE_ELECTRICITE, " +
                     "NVL(QUOTITE_OM, 100) AS QUOTITE_OM, " +
                     "NVL(QUOTITE_ENTRETIEN, 100) AS QUOTITE_ENTRETIEN " +
                     "FROM RELEVE_COMPTEUR " +
                     "WHERE ID_GARAGE = ? " +
                     "ORDER BY DATE_ DESC, TYPE";

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idGarage);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long ancienIndex = rs.getLong("ANCIEN_INDEX");
                    long nouvelIndex = rs.getLong("NOUVELLE_INDEX");
                    long consommation = nouvelIndex - ancienIndex;

                    liste.add(new Object[]{
                        rs.getLong("ID_RELEVE"),           // 0
                        rs.getString("TYPE"),               // 1
                        rs.getDate("DATE_"),                // 2
                        ancienIndex,                        // 3
                        nouvelIndex,                        // 4
                        rs.getLong("NUM_COMPTEUR"),         // 5
                        rs.getString("UNITE"),              // 6
                        consommation,                       // 7
                        rs.getDouble("QUOTITE_EAU"),        // 8
                        rs.getDouble("QUOTITE_ELECTRICITE"),// 9
                        rs.getDouble("QUOTITE_OM"),         // 10
                        rs.getDouble("QUOTITE_ENTRETIEN")   // 11
                    });
                }
            }
        }
        return liste;
    }

    /**
     * Ajoute un releve compteur avec quotites pour un garage
     */
    public void ajouterReleveCompteurGarage(long idGarage, String type, java.util.Date date,
                                            long numCompteur, long ancienIndex, long nouvelIndex, String unite,
                                            double quotiteEau, double quotiteElec, double quotiteOM, double quotiteEntretien) throws SQLException {
        String sql = "INSERT INTO RELEVE_COMPTEUR " +
                     "(ID_RELEVE, ID_GARAGE, TYPE, DATE_, NUM_COMPTEUR, ANCIEN_INDEX, NOUVELLE_INDEX, UNITE, " +
                     "QUOTITE_EAU, QUOTITE_ELECTRICITE, QUOTITE_OM, QUOTITE_ENTRETIEN) " +
                     "VALUES ((SELECT NVL(MAX(ID_RELEVE), 0) + 1 FROM RELEVE_COMPTEUR), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idGarage);
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
            ps.executeUpdate();
        }
    }

    /**
     * Modifie un releve compteur avec quotites
     */
    public void modifierReleveCompteurGarage(long idReleve, String type, java.util.Date date,
                                              long numCompteur, long ancienIndex, long nouvelIndex, String unite,
                                              double quotiteEau, double quotiteElec, double quotiteOM, double quotiteEntretien) throws SQLException {
        String sql = "UPDATE RELEVE_COMPTEUR SET " +
                     "TYPE = ?, DATE_ = ?, NUM_COMPTEUR = ?, ANCIEN_INDEX = ?, NOUVELLE_INDEX = ?, UNITE = ?, " +
                     "QUOTITE_EAU = ?, QUOTITE_ELECTRICITE = ?, QUOTITE_OM = ?, QUOTITE_ENTRETIEN = ? " +
                     "WHERE ID_RELEVE = ?";

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
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
            ps.executeUpdate();
        }
    }

    /**
     * Supprime un releve compteur
     */
    public void supprimerReleveCompteurGarage(long idReleve) throws SQLException {
        String sql = "DELETE FROM RELEVE_COMPTEUR WHERE ID_RELEVE = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idReleve);
            ps.executeUpdate();
        }
    }


    // 5. ASSURANCES


    public List<Object[]> getAssurancesGarage(long idGarage) throws SQLException {
        List<Object[]> liste = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(RequeteDocumentGarage.reqGetAssurancesGarage())) {
            RequeteDocumentGarage.paramGetAssurancesGarage(st, idGarage);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                liste.add(new Object[]{
                    rs.getLong("numassurance"),
                    rs.getString("nom_compagnie"),
                    rs.getString("type"),
                    rs.getDouble("primebase"),
                    rs.getDate("date_effet"),
                    rs.getDate("date_echeance")
                });
            }
        }
        return liste;
    }

    public void ajouterAssurance(long idGarage, long numAssurance, String nomCompagnie,
                                 String type, double primeBase,
                                 java.util.Date dateEffet, java.util.Date dateEcheance) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteDocumentGarage.reqAjouterAssuranceGarage())) {
            RequeteDocumentGarage.paramAjouterAssuranceGarage(cs, numAssurance, nomCompagnie, type, primeBase, dateEffet, dateEcheance, idGarage);
            cs.execute();
        } catch (SQLException e) {
            // Fallback SQL direct
            String sql = "INSERT INTO ASSURANCE (NUMASSURANCE, NOM_COMPAGNIE, TYPE, PRIMEBASE, DATE_EFFET, ID_GARAGE) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setLong(1, numAssurance);
                ps.setString(2, nomCompagnie);
                ps.setString(3, type);
                ps.setDouble(4, primeBase);
                ps.setDate(5, new java.sql.Date(dateEffet.getTime()));
                ps.setLong(6, idGarage);
                ps.executeUpdate();
            }
            
            // Ajouter echeance
            if (dateEcheance != null) {
                String sqlEch = "INSERT INTO ECHEANCE (NUMASSURANCE, DATE_, MONTANT) VALUES (?, ?, ?)";
                try (PreparedStatement ps = cn.prepareStatement(sqlEch)) {
                    ps.setLong(1, numAssurance);
                    ps.setDate(2, new java.sql.Date(dateEcheance.getTime()));
                    ps.setDouble(3, primeBase);
                    ps.executeUpdate();
                }
            }
        }
    }

    public void modifierAssurance(long numAssurance, String nomCompagnie,
                                  String type, double primeBase,
                                  java.util.Date dateEffet, java.util.Date dateEcheance) throws SQLException {
        String sql = "UPDATE ASSURANCE SET NOM_COMPAGNIE = ?, TYPE = ?, PRIMEBASE = ?, DATE_EFFET = ? WHERE NUMASSURANCE = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nomCompagnie);
            ps.setString(2, type);
            ps.setDouble(3, primeBase);
            ps.setDate(4, new java.sql.Date(dateEffet.getTime()));
            ps.setLong(5, numAssurance);
            ps.executeUpdate();
        }
    }

    public void supprimerAssurance(long numAssurance) throws SQLException {
        // Supprimer echeances
        String sql1 = "DELETE FROM ECHEANCE WHERE NUMASSURANCE = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql1)) {
            ps.setLong(1, numAssurance);
            ps.executeUpdate();
        }
        
        // Supprimer assurance
        String sql2 = "DELETE FROM ASSURANCE WHERE NUMASSURANCE = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql2)) {
            ps.setLong(1, numAssurance);
            ps.executeUpdate();
        }
    }


    // 6. LOYERS EN ATTENTE


    public List<Object[]> getLoyersEnAttenteGarage(long idGarage) throws SQLException {
        List<Object[]> liste = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(RequeteDocumentGarage.reqGetLoyersEnAttenteGarage())) {
            RequeteDocumentGarage.paramGetLoyersEnAttenteGarage(st, idGarage);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                liste.add(new Object[]{
                    rs.getLong("id_loyer"),
                    rs.getString("periode"),
                    rs.getDouble("montant_loyer"),
                    rs.getDouble("montant_provision"),
                    rs.getDouble("total"),
                    rs.getString("statut") != null ? rs.getString("statut") : "En attente"
                });
            }
        }
        return liste;
    }


    // 7. QUITTANCES (LOYERS PAYES)


    public List<Object[]> getQuittancesPayeesGarage(long idGarage) throws SQLException {
        List<Object[]> liste = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(RequeteDocumentGarage.reqGetQuittancesPayeesGarage())) {
            RequeteDocumentGarage.paramGetQuittancesPayeesGarage(st, idGarage);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                liste.add(new Object[]{
                    rs.getLong("id_loyer"),
                    rs.getString("periode"),
                    rs.getDouble("montant_loyer"),
                    rs.getDouble("montant_provision"),
                    rs.getDouble("total"),
                    rs.getDate("date_paiement"),
                    rs.getString("quittance")
                });
            }
        }
        return liste;
    }


    // 8. OPERATIONS SUR LES LOYERS


    public void marquerLoyerPayeGarage(long idLoyer, java.util.Date datePaiement) throws SQLException {
        marquerLoyerPayeInternal(idLoyer, datePaiement);
    }

    public void annulerPaiementGarage(long idLoyer) throws SQLException {
        annulerPaiementInternal(idLoyer);
    }
}