package modele.dao;

import java.sql.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import modele.dao.Requete.RequeteLocataire;
import modele.Locataire;

/**
 * DAO pour la table LOCATAIRE
 */
public class LocataireDao implements dao<Locataire>, AutoCloseable {

    private Connection cn;

    @Override
    public void setConnection(Connection cn) {
        this.cn = cn;
    }

    public LocataireDao() throws SQLException {
        cn = UtOracleDataSource.getConnection();
        if (cn == null || cn.isClosed()) {
            UtOracleDataSource.creerAcces();
            cn = UtOracleDataSource.getConnection();
        }
    }

    @Override
    public void create(Locataire locataire) throws SQLException {
        boolean oldAutoCommit = cn.getAutoCommit();
        try {
            if (oldAutoCommit) cn.setAutoCommit(false);

            try (PreparedStatement st = cn.prepareStatement(RequeteLocataire.reqCreate())) {
                RequeteLocataire.paramCreate(st, locataire);
                st.executeUpdate();
            }

            cn.commit();

        } catch (SQLException ex) {
            try { cn.rollback(); } catch (SQLException ignore) { /* Ignorer */ }
            throw ex;
        } finally {
            try { cn.setAutoCommit(oldAutoCommit); } catch (SQLException ignore) { /* Ignorer */ }
        }
    }

    @Override
    public void update(Locataire locataire) throws SQLException {
        boolean oldAutoCommit = cn.getAutoCommit();
        try {
            if (oldAutoCommit) cn.setAutoCommit(false);

            try (PreparedStatement st = cn.prepareStatement(RequeteLocataire.reqUpdate())) {
                RequeteLocataire.paramUpdate(st, locataire);
                st.executeUpdate();
            }

            cn.commit();

        } catch (SQLException ex) {
            try { cn.rollback(); } catch (SQLException ignore) { /* Ignorer */ }
            throw ex;
        } finally {
            try { cn.setAutoCommit(oldAutoCommit); } catch (SQLException ignore) { /* Ignorer */ }
        }
    }

    @Override
    public void delete(Locataire locataire) throws SQLException {
        // Vérifier d'abord si le locataire a un bail actif
        if (estLocataireActif(locataire.getIdLocataire())) {
            throw new SQLException("Impossible de supprimer ce locataire : il a un bail actif.");
        }
        
        // D'abord supprimer les liens dans la table SIGNE
        try (PreparedStatement st = cn.prepareStatement(RequeteLocataire.reqDeleteSigne())) {
            RequeteLocataire.paramDeleteSigne(st, locataire);
            st.executeUpdate();
        }
        
        // Ensuite supprimer le locataire
        try (PreparedStatement st = cn.prepareStatement(RequeteLocataire.reqDelete())) {
            RequeteLocataire.paramDelete(st, locataire);
            st.executeUpdate();
        }
    }

    @Override
    public Locataire findById(String... id) throws SQLException {
        if (id.length < 1) {
            throw new SQLException("ID manquant");
        }

        try (PreparedStatement st = cn.prepareStatement(RequeteLocataire.reqFindById())) {
            RequeteLocataire.paramFindById(st, id[0]);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapLocataire(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Locataire> findAll() throws SQLException {
        List<Locataire> list = new ArrayList<>();

        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteLocataire.reqFindAll())) {

            while (rs.next()) {
                list.add(mapLocataire(rs));
            }
        }
        return list;
    }

    /**
     * Trouve tous les locataires avec leur statut
     */
    public List<Object[]> findAllAvecStatut() throws SQLException {
        List<Object[]> list = new ArrayList<>();

        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteLocataire.reqFindAllAvecStatut())) {

            while (rs.next()) {
                Locataire loc = mapLocataire(rs);
                String statut = rs.getString("STATUT");
                String actuel = rs.getString("ACTUEL");
                list.add(new Object[] { loc, statut, actuel });
            }
        }
        return list;
    }

    /**
     * Trouve les locataires par statut
     */
    public List<Locataire> findByStatut(String statut) throws SQLException {
        List<Locataire> list = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(RequeteLocataire.reqFindByStatut())) {
            RequeteLocataire.paramFindByStatut(st, statut);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(mapLocataire(rs));
                }
            }
        }
        return list;
    }

    public List<Locataire> findProspects() throws SQLException {
        return findByStatut("PROSPECT");
    }

    public List<Locataire> findActifs() throws SQLException {
        return findByStatut("ACTIF");
    }

    public List<Locataire> findAnciens() throws SQLException {
        return findByStatut("ANCIEN");
    }

    /**
     * Récupère le statut d'un locataire via fonction PL/SQL
     */
    public String getStatut(long idLocataire) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteLocataire.reqGetStatut())) {
            RequeteLocataire.paramGetStatut(cs, idLocataire);
            cs.execute();
            return cs.getString(1);
        }
    }

    /**
     * Vérifie si un locataire a un bail actif
     */
    public boolean estLocataireActif(long idLocataire) throws SQLException {
        String statut = getStatut(idLocataire);
        return "ACTIF".equals(statut);
    }

    /**
     * Vérifie si un locataire a les coordonnées minimales pour signer un bail
     */
    public boolean aCoordonneesSuffisantes(long idLocataire) throws SQLException {
        Locataire loc = findById(String.valueOf(idLocataire));
        if (loc == null) return false;
        
        // Nom obligatoire
        if (loc.getNomLoc() == null || loc.getNomLoc().trim().isEmpty()) return false;
        // Prénom obligatoire
        if (loc.getPrenomLoc() == null || loc.getPrenomLoc().trim().isEmpty()) return false;
        // Date de naissance obligatoire
        if (loc.getDateNaissance() == null) return false;
        // Téléphone obligatoire
        if (loc.getTelLoc() == null || loc.getTelLoc().trim().isEmpty()) return false;
        // Email obligatoire
        if (loc.getMailLoc() == null || loc.getMailLoc().trim().isEmpty()) return false;
        
        return true;
    }

    /**
     * Trouve les locataires liés à un bail
     */
    public List<Locataire> findByBail(long idBail) throws SQLException {
        List<Locataire> list = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(RequeteLocataire.reqFindByBail())) {
            RequeteLocataire.paramFindByBail(st, idBail);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(mapLocataire(rs));
                }
            }
        }
        return list;
    }

    /**
     * Trouve les locataires sans bail actif
     */
    public List<Locataire> findSansBailActif() throws SQLException {
        List<Locataire> list = new ArrayList<>();

        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteLocataire.reqFindSansBailActif())) {

            while (rs.next()) {
                list.add(mapLocataire(rs));
            }
        }
        return list;
    }

    /**
     * Lie un locataire à un bail
     */
    public void lierAuBail(long idLocataire, long idBail) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteLocataire.reqLierAuBail())) {
            RequeteLocataire.paramLierAuBail(st, idLocataire, idBail);
            st.executeUpdate();
        }
    }

    /**
     * Délie un locataire d'un bail
     */
    public void delierDuBail(long idLocataire, long idBail) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteLocataire.reqDelierDuBail())) {
            RequeteLocataire.paramDelierDuBail(st, idLocataire, idBail);
            st.executeUpdate();
        }
    }

    /**
     * Méthode de mapping - MODIFIÉ pour DATE_NAISSANCE
     */
    private Locataire mapLocataire(ResultSet rs) throws SQLException {
        // Récupérer la date de naissance
        LocalDate dateNaissance = null;
        java.sql.Date sqlDate = rs.getDate("DATE_NAISSANCE");
        if (sqlDate != null) {
            dateNaissance = sqlDate.toLocalDate();
        }
        
        return new Locataire(
            rs.getLong("ID_LOCATAIRE"),
            rs.getString("NOM_LOC"),
            rs.getString("PRENOM_LOC"),
            rs.getString("TEL_LOC"),
            rs.getString("MAIL_LOC"),
            dateNaissance
        );
    }

    /**
     * CORRECTION : Implémentation de AutoCloseable
     */
    @Override
    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) {
            cn.close();
        }
    }
}
