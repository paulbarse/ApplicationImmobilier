package modele.dao;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

import modele.dao.Requete.RequeteBatiment;
import modele.Batiment;

/**
 * DAO pour la table BATIMENT
 */
public class BatimentDao extends AbstractDao<Batiment, Long> implements dao<Batiment> {

    public BatimentDao() throws SQLException {
        super("BATIMENT", "ID_BATIMENT");
    }


    // IMPLÉMENTATION DES MÉTHODES ABSTRAITES


    @Override
    public void create(Batiment b) throws SQLException {
        if (b.getIdBatimentLong() <= 0) {
            throw new SQLException("ID_BATIMENT invalide (doit être > 0).");
        }

        if (exists(b.getIdBatimentLong())) {
            throw new SQLException("ID_BATIMENT déjà utilisé : " + b.getIdBatimentLong());
        }

        try (PreparedStatement st = cn.prepareStatement(RequeteBatiment.reqCreate())) {
            RequeteBatiment.paramCreate(st, b);
            st.executeUpdate();
        }
    }

    @Override
    public void update(Batiment b) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteBatiment.reqUpdate())) {
            RequeteBatiment.paramUpdate(st, b);
            st.executeUpdate();
        }
    }

    @Override
    public void delete(Batiment b) throws SQLException {
        deleteById(b.getIdBatimentLong());
    }

    @Override
    public Batiment findById(Long id) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteBatiment.reqFindById())) {
            RequeteBatiment.paramFindById(st, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Implémentation pour l'interface dao<Batiment>
     */
    @Override
    public Batiment findById(String... id) throws SQLException {
        if (id.length < 1) {
            throw new SQLException("ID manquant");
        }
        return findById(Long.parseLong(id[0]));
    }

    @Override
    public List<Batiment> findAll() throws SQLException {
        List<Batiment> list = new ArrayList<>();

        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteBatiment.reqFindAll())) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    @Override
    protected Batiment mapResultSet(ResultSet rs) throws SQLException {
        return new Batiment(
            rs.getLong("ID_BATIMENT"),
            rs.getString("ADRESSE"),
            getIntOrNull(rs, "NOMBRE_D_ETAGE"),
            rs.getString("TYPE_BATIMENT"),
            rs.getString("RUE"),
            rs.getInt("CODE_POSTALE")
        );
    }


    // MÉTHODES SPÉCIFIQUES AU BÂTIMENT


    /**
     * Recherche un bâtiment par son adresse
     */
    public Batiment findByAdresse(String adresse) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteBatiment.reqFindByAdresse())) {
            RequeteBatiment.paramFindByAdresse(st, adresse);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Recherche les bâtiments par code postal
     */
    public List<Batiment> findByCodePostal(int codePostal) throws SQLException {
        List<Batiment> list = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(RequeteBatiment.reqFindByCodePostal())) {
            RequeteBatiment.paramFindByCodePostal(st, codePostal);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        }
        return list;
    }

    /**
     * Trouve les bâtiments disponibles pour créer un nouveau logement
     */
    public List<Batiment> findDisponiblesPourLogement() throws SQLException {
        List<Batiment> result = new ArrayList<>();
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteBatiment.reqFindDisponiblesPourLogement());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapResultSet(rs));
            }
        }
        
        return result;
    }

    /**
     * Délie les biens d'un bâtiment
     */
    public void delierBienABat(String adresse) throws SQLException {
        boolean trouve = false;

        // 1. Tenter de délier LOGEMENT
        try (CallableStatement cs = cn.prepareCall(RequeteBatiment.reqDelierLogementABat())) {
            RequeteBatiment.paramDelierLogementABat(cs, adresse);
            cs.execute();
            trouve = true;
        } catch (SQLException e) {
            if (e.getErrorCode() != 20105) throw e;
        }

        // 2. Si pas trouvé, tenter de délier GARAGE
        if (!trouve) {
            try (CallableStatement cs = cn.prepareCall(RequeteBatiment.reqDelierGarageABat())) {
                RequeteBatiment.paramDelierGarageABat(cs, adresse);
                cs.execute();
            } catch (SQLException e) {
                if (e.getErrorCode() != 20105) throw e;
            }
        }
    }

    /**
     * Récupère le total des assurances pour un bâtiment
     */
    public double getTotalAssuranceByBatiment(long idBatiment, int annee) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteBatiment.reqGetTotalAssuranceByBatiment())) {
            RequeteBatiment.paramGetTotalAssuranceByBatiment(cs, idBatiment, annee);
            cs.execute();
            return cs.getDouble(1);
        }
    }

    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) {
            cn.close();
        }
    }
}
