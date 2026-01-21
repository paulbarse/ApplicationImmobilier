package modele.dao;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

import modele.dao.Requete.RequeteGarant;
import modele.Garant;

/**
 * DAO pour la table GARANT
 */
public class GarantDao implements dao<Garant> {

    private Connection cn;

    @Override
    public void setConnection(Connection cn) {
        this.cn = cn;
    }

    public GarantDao() throws SQLException {
        cn = UtOracleDataSource.getConnection();
        if (cn == null || cn.isClosed()) {
            UtOracleDataSource.creerAcces();
            cn = UtOracleDataSource.getConnection();
        }
    }

    @Override
    public void create(Garant g) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteGarant.reqCreate())) {
            RequeteGarant.paramCreate(st, g);
            st.executeUpdate();
        }
    }

    @Override
    public void update(Garant g) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteGarant.reqUpdate())) {
            RequeteGarant.paramUpdate(st, g);
            st.executeUpdate();
        }
    }

    @Override
    public void delete(Garant g) throws SQLException {
        // D'abord supprimer les liens dans la table AJOUTE
        try (PreparedStatement st = cn.prepareStatement(RequeteGarant.reqDeleteAjoute())) {
            RequeteGarant.paramDeleteAjoute(st, g);
            st.executeUpdate();
        }
        
        // Ensuite supprimer le garant
        try (PreparedStatement st = cn.prepareStatement(RequeteGarant.reqDelete())) {
            RequeteGarant.paramDelete(st, g);
            st.executeUpdate();
        }
    }

    @Override
    public Garant findById(String... id) throws SQLException {
        if (id.length < 1) {
            throw new SQLException("ID manquant");
        }

        try (PreparedStatement st = cn.prepareStatement(RequeteGarant.reqFindById())) {
            RequeteGarant.paramFindById(st, id[0]);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapGarant(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Garant> findAll() throws SQLException {
        List<Garant> list = new ArrayList<>();

        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteGarant.reqFindAll())) {

            while (rs.next()) {
                list.add(mapGarant(rs));
            }
        }
        return list;
    }

    /**
     * Trouve les garants liés à un bail via la table AJOUTE
     */
    public List<Garant> findByBail(long idBail) throws SQLException {
        List<Garant> list = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(RequeteGarant.reqFindByBail())) {
            RequeteGarant.paramFindByBail(st, idBail);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(mapGarant(rs));
                }
            }
        }
        return list;
    }

    /**
     * Lie un garant à un bail (table AJOUTE)
     */
    public void lierAuBail(String idGarant, long idBail) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteGarant.reqLierAuBail())) {
            RequeteGarant.paramLierAuBail(st, idGarant, idBail);
            st.executeUpdate();
        }
    }

    /**
     * Délie un garant d'un bail
     */
    public void delierDuBail(String idGarant, long idBail) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteGarant.reqDelierDuBail())) {
            RequeteGarant.paramDelierDuBail(st, idGarant, idBail);
            st.executeUpdate();
        }
    }

    // Méthode de mapping
    private Garant mapGarant(ResultSet rs) throws SQLException {
        return new Garant(
            rs.getString("ID_GARANT"),
            rs.getString("NOM_GARANT"),
            rs.getString("PRENOM_GARANT"),
            rs.getString("ADRESSE_GARANT"),
            rs.getString("TEL_GARANT"),
            rs.getString("MAIL_GARANT")
        );
    }

    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) {
            cn.close();
        }
    }
}
