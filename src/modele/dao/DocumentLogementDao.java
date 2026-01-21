package modele.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import modele.dao.Requete.RequeteDocumentLogement;

/**
 * DAO pour les documents d'un logement
 */
public class DocumentLogementDao {

    private Connection cn;

    public DocumentLogementDao() throws SQLException {
        cn = UtOracleDataSource.getConnection();
        if (cn == null || cn.isClosed()) {
            UtOracleDataSource.creerAcces();
            cn = UtOracleDataSource.getConnection();
        }
    }


    // RELEVES COMPTEUR - avec quotites


    /**
     * Retourne les releves compteur d'un logement
     */
    public List<Object[]> getRelevesCompteurs(long idLogement) throws SQLException {
        List<Object[]> liste = new ArrayList<>();
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteDocumentLogement.reqGetRelevesCompteurs())) {
            RequeteDocumentLogement.paramGetRelevesCompteurs(ps, idLogement);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long ancienIndex = rs.getLong("ANCIEN_INDEX");
                    long nouvelIndex = rs.getLong("NOUVELLE_INDEX");
                    long consommation = nouvelIndex - ancienIndex;
                    
                    liste.add(new Object[] {
                        rs.getLong("ID_RELEVE"),           // 0
                        rs.getString("TYPE"),               // 1
                        rs.getString("DATE_RELEVE"),        // 2
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
     * Ajoute un releve compteur avec quotites
     */
    public void ajouterReleveCompteur(long idLogement, String type, Date date, 
            long numCompteur, long ancienIndex, long nouvelIndex, String unite,
            double quotiteEau, double quotiteElec, double quotiteOM, double quotiteEntretien) throws SQLException {
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteDocumentLogement.reqAjouterReleveCompteur())) {
            RequeteDocumentLogement.paramAjouterReleveCompteur(ps, idLogement, type, date, 
                    numCompteur, ancienIndex, nouvelIndex, unite,
                    quotiteEau, quotiteElec, quotiteOM, quotiteEntretien);
            ps.executeUpdate();
        }
    }

    /**
     * Modifie un releve compteur avec quotites
     */
    public void modifierReleveCompteur(long idReleve, String type, Date date, 
            long numCompteur, long ancienIndex, long nouvelIndex, String unite,
            double quotiteEau, double quotiteElec, double quotiteOM, double quotiteEntretien) throws SQLException {
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteDocumentLogement.reqModifierReleveCompteur())) {
            RequeteDocumentLogement.paramModifierReleveCompteur(ps, idReleve, type, date, 
                    numCompteur, ancienIndex, nouvelIndex, unite,
                    quotiteEau, quotiteElec, quotiteOM, quotiteEntretien);
            ps.executeUpdate();
        }
    }

    /**
     * Supprime un releve compteur
     */
    public void supprimerReleveCompteur(long idReleve) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(RequeteDocumentLogement.reqSupprimerReleveCompteur())) {
            RequeteDocumentLogement.paramSupprimerReleveCompteur(ps, idReleve);
            ps.executeUpdate();
        }
    }


    // BAUX


    public List<Object[]> getBauxLogement(long idLogement) throws SQLException {
        List<Object[]> liste = new ArrayList<>();
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteDocumentLogement.reqGetBauxLogement())) {
            RequeteDocumentLogement.paramGetBauxLogement(ps, idLogement);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(new Object[] {
                        rs.getLong("ID_BAIL"),
                        rs.getString("LOCATAIRES"),
                        rs.getDate("DATE_DEBUT"),
                        rs.getDate("DATE_FIN"),
                        rs.getDouble("LOYER_INITIAL"),
                        rs.getDouble("PROVISION_INITIALES"),
                        rs.getDouble("CAUTION"),
                        rs.getString("ETAT")
                    });
                }
            }
        }
        return liste;
    }


    // GARAGES


    public List<Object[]> getGaragesLogement(long idLogement) throws SQLException {
        List<Object[]> liste = new ArrayList<>();
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteDocumentLogement.reqGetGaragesLogement())) {
            RequeteDocumentLogement.paramGetGaragesLogement(ps, idLogement);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(new Object[] {
                        rs.getLong("ID_GARAGE"),
                        rs.getString("ADRESSE_GARAGE"),
                        rs.getDouble("SURFACE_GARAGE"),
                        rs.getLong("NUM_FISCAL"),
                        rs.getString("ASSURANCE")
                    });
                }
            }
        }
        return liste;
    }

    public void lierGarageAuLogement(long idGarage, long idLogement) throws SQLException {
        String sql = "UPDATE GARAGE SET ID_LOGEMENT = ? WHERE ID_GARAGE = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idLogement);
            ps.setLong(2, idGarage);
            ps.executeUpdate();
        }
    }

    public void delierGarageDuLogement(long idGarage) throws SQLException {
        String sql = "UPDATE GARAGE SET ID_LOGEMENT = NULL WHERE ID_GARAGE = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idGarage);
            ps.executeUpdate();
        }
    }


    // GARANTS


    public List<Object[]> getGarantsLogement(long idLogement) throws SQLException {
        List<Object[]> liste = new ArrayList<>();
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteDocumentLogement.reqGetGarantsLogement())) {
            RequeteDocumentLogement.paramGetGarantsLogement(ps, idLogement);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(new Object[] {
                        rs.getString("ID_GARANT"),
                        rs.getString("NOM_GARANT"),
                        rs.getString("PRENOM_GARANT"),
                        rs.getString("ADRESSE_GARANT"),
                        rs.getString("MAIL_GARANT"),
                        rs.getString("TEL_GARANT")
                    });
                }
            }
        }
        return liste;
    }

    public void ajouterGarant(long idLogement, String nom, String prenom, String adresse, String email, String tel) throws SQLException {
        // Recuperer l'ID du bail
        long idBail = getIdBailLogement(idLogement);
        if (idBail <= 0) throw new SQLException("Aucun bail actif pour ce logement");
        
        // Generer un ID garant
        String idGarant = "G" + System.currentTimeMillis();
        
        // Inserer le garant
        String sql1 = "INSERT INTO GARANT (ID_GARANT, NOM_GARANT, PRENOM_GARANT, ADRESSE_GARANT, MAIL_GARANT, TEL_GARANT) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql1)) {
            ps.setString(1, idGarant);
            ps.setString(2, nom);
            ps.setString(3, prenom);
            ps.setString(4, adresse);
            ps.setString(5, email);
            ps.setString(6, tel);
            ps.executeUpdate();
        }
        
        // Lier au bail
        String sql2 = "INSERT INTO AJOUTE (ID_BAIL, ID_GARANT) VALUES (?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql2)) {
            ps.setLong(1, idBail);
            ps.setString(2, idGarant);
            ps.executeUpdate();
        }
    }

    public void modifierGarant(String idGarant, String nom, String prenom, String adresse, String email, String tel) throws SQLException {
        String sql = "UPDATE GARANT SET NOM_GARANT = ?, PRENOM_GARANT = ?, ADRESSE_GARANT = ?, MAIL_GARANT = ?, TEL_GARANT = ? WHERE ID_GARANT = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nom);
            ps.setString(2, prenom);
            ps.setString(3, adresse);
            ps.setString(4, email);
            ps.setString(5, tel);
            ps.setString(6, idGarant);
            ps.executeUpdate();
        }
    }

    public void supprimerGarant(String idGarant) throws SQLException {
        String sql1 = "DELETE FROM AJOUTE WHERE ID_GARANT = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql1)) {
            ps.setString(1, idGarant);
            ps.executeUpdate();
        }
        
        String sql2 = "DELETE FROM GARANT WHERE ID_GARANT = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql2)) {
            ps.setString(1, idGarant);
            ps.executeUpdate();
        }
    }


    // ETATS DES LIEUX


    public List<Object[]> getEtatsDesLieux(long idLogement) throws SQLException {
        List<Object[]> liste = new ArrayList<>();
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteDocumentLogement.reqGetEtatsDesLieux())) {
            RequeteDocumentLogement.paramGetEtatsDesLieux(ps, idLogement);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(new Object[] {
                        rs.getLong("ID_EDL"),
                        rs.getString("TYPE"),
                        rs.getDate("DATE_EDL"),
                        rs.getString("OBSERVATIONS")
                    });
                }
            }
        }
        return liste;
    }

    public void ajouterEtatDesLieux(long idLogement, String type, Date date, String observations) throws SQLException {
        long idBail = getIdBailLogement(idLogement);
        if (idBail <= 0) throw new SQLException("Aucun bail actif pour ce logement");
        
        String sql = "INSERT INTO ETATDESLIEUX (ID_EDL, TYPE, DATE_EDL, OBSERVATIONS, ID_BAIL) " +
                     "VALUES ((SELECT NVL(MAX(ID_EDL), 0) + 1 FROM ETATDESLIEUX), ?, ?, ?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setDate(2, new java.sql.Date(date.getTime()));
            ps.setString(3, observations);
            ps.setLong(4, idBail);
            ps.executeUpdate();
        }
    }

    public void modifierEtatDesLieux(long idEdl, String type, Date date, String observations) throws SQLException {
        String sql = "UPDATE ETATDESLIEUX SET TYPE = ?, DATE_EDL = ?, OBSERVATIONS = ? WHERE ID_EDL = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setDate(2, new java.sql.Date(date.getTime()));
            ps.setString(3, observations);
            ps.setLong(4, idEdl);
            ps.executeUpdate();
        }
    }

    public void supprimerEtatDesLieux(long idEdl) throws SQLException {
        String sql = "DELETE FROM ETATDESLIEUX WHERE ID_EDL = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idEdl);
            ps.executeUpdate();
        }
    }


    // ASSURANCES


    public List<Object[]> getAssurances(long idLogement) throws SQLException {
        List<Object[]> liste = new ArrayList<>();
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteDocumentLogement.reqGetAssurances())) {
            RequeteDocumentLogement.paramGetAssurances(ps, idLogement);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(new Object[] {
                        rs.getLong("NUMASSURANCE"),
                        rs.getString("NOM_COMPAGNIE"),
                        rs.getString("TYPE"),
                        rs.getDouble("PRIMEBASE"),
                        rs.getDate("DATE_EFFET"),
                        rs.getDate("DATE_ECHEANCE")
                    });
                }
            }
        }
        return liste;
    }

    public void ajouterAssurance(long idLogement, long numAssurance, String nomCompagnie, String type, 
            double prime, Date dateEffet, Date dateEcheance) throws SQLException {
        String sql = "INSERT INTO ASSURANCE (NUMASSURANCE, NOM_COMPAGNIE, TYPE, PRIMEBASE, DATE_EFFET, ID_LOGEMENT) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, numAssurance);
            ps.setString(2, nomCompagnie);
            ps.setString(3, type);
            ps.setDouble(4, prime);
            ps.setDate(5, new java.sql.Date(dateEffet.getTime()));
            ps.setLong(6, idLogement);
            ps.executeUpdate();
        }
        
        // Ajouter l'echeance
        if (dateEcheance != null) {
            String sql2 = "INSERT INTO ECHEANCE (NUMASSURANCE, DATE_, MONTANT) VALUES (?, ?, ?)";
            try (PreparedStatement ps = cn.prepareStatement(sql2)) {
                ps.setLong(1, numAssurance);
                ps.setDate(2, new java.sql.Date(dateEcheance.getTime()));
                ps.setDouble(3, prime);
                ps.executeUpdate();
            }
        }
    }

    public void modifierAssurance(long numAssurance, String nomCompagnie, String type, 
            double prime, Date dateEffet, Date dateEcheance) throws SQLException {
        String sql = "UPDATE ASSURANCE SET NOM_COMPAGNIE = ?, TYPE = ?, PRIMEBASE = ?, DATE_EFFET = ? WHERE NUMASSURANCE = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nomCompagnie);
            ps.setString(2, type);
            ps.setDouble(3, prime);
            ps.setDate(4, new java.sql.Date(dateEffet.getTime()));
            ps.setLong(5, numAssurance);
            ps.executeUpdate();
        }
    }

    public void supprimerAssurance(long numAssurance) throws SQLException {
        String sql1 = "DELETE FROM ECHEANCE WHERE NUMASSURANCE = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql1)) {
            ps.setLong(1, numAssurance);
            ps.executeUpdate();
        }
        
        String sql2 = "DELETE FROM ASSURANCE WHERE NUMASSURANCE = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql2)) {
            ps.setLong(1, numAssurance);
            ps.executeUpdate();
        }
    }


    // LOYERS EN ATTENTE


    public List<Object[]> getLoyersEnAttenteLogement(long idLogement) throws SQLException {
        List<Object[]> liste = new ArrayList<>();
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteDocumentLogement.reqGetLoyersEnAttenteLogement())) {
            RequeteDocumentLogement.paramGetLoyersEnAttenteLogement(ps, idLogement);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(new Object[] {
                        rs.getLong("ID_LOYER"),
                        rs.getString("MOIS"),
                        rs.getDouble("MONTANT_LOYER"),
                        rs.getDouble("MONTANT_PROVISION"),
                        rs.getDouble("TOTAL"),
                        rs.getString("STATUT")
                    });
                }
            }
        }
        return liste;
    }

    public void marquerLoyerPaye(long idLoyer, Date datePaiement) throws SQLException {
        String sql = "UPDATE LOYER SET STATUT = 'PAYE', DATE_PAIEMENT = ? WHERE ID_LOYER = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(datePaiement.getTime()));
            ps.setLong(2, idLoyer);
            ps.executeUpdate();
        }
    }


    // QUITTANCES


    public List<Object[]> getQuittancesLogement(long idLogement) throws SQLException {
        List<Object[]> liste = new ArrayList<>();
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteDocumentLogement.reqGetQuittancesLogement())) {
            RequeteDocumentLogement.paramGetQuittancesLogement(ps, idLogement);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(new Object[] {
                        rs.getLong("ID_LOYER"),
                        rs.getString("MOIS"),
                        rs.getDouble("MONTANT_LOYER"),
                        rs.getDouble("MONTANT_PROVISION"),
                        rs.getDouble("TOTAL"),
                        rs.getDate("DATE_PAIEMENT"),
                        rs.getString("QUITTANCE")
                    });
                }
            }
        }
        return liste;
    }

    public void annulerPaiement(long idLoyer) throws SQLException {
        String sql = "UPDATE LOYER SET STATUT = 'EN_ATTENTE', DATE_PAIEMENT = NULL WHERE ID_LOYER = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idLoyer);
            ps.executeUpdate();
        }
    }


    // UTILITAIRES


    private long getIdBailLogement(long idLogement) throws SQLException {
        String sql = "SELECT ID_BAIL FROM LOGEMENT WHERE ID_LOGEMENT = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idLogement);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("ID_BAIL");
                }
            }
        }
        return 0;
    }

    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) {
            cn.close();
        }
    }
}