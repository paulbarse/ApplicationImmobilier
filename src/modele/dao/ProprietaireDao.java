package modele.dao;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

import modele.dao.Requete.RequeteProprietaire;
import modele.Proprietaire;

/**
 * DAO pour la table PROPRIETAIRE 
 */
public class ProprietaireDao implements dao<Proprietaire> {

    private Connection cn;

    @Override
    public void setConnection(Connection cn) {
        this.cn = cn;
    }

    public ProprietaireDao() throws SQLException {
        cn = UtOracleDataSource.getConnection();
        if (cn == null || cn.isClosed()) {
            UtOracleDataSource.creerAcces();
            cn = UtOracleDataSource.getConnection();
        }
    }

    @Override
    public void create(Proprietaire p) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteProprietaire.reqCreate())) {
            RequeteProprietaire.paramCreate(st, p);
            st.executeUpdate();
        }
    }

    @Override
    public void update(Proprietaire p) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteProprietaire.reqUpdate())) {
            RequeteProprietaire.paramUpdate(st, p);
            st.executeUpdate();
        }
    }

    @Override
    public void delete(Proprietaire p) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteProprietaire.reqDelete())) {
            RequeteProprietaire.paramDelete(st, p);
            st.executeUpdate();
        }
    }

    @Override
    public Proprietaire findById(String... id) throws SQLException {
        if (id.length < 1) {
            throw new SQLException("ID manquant");
        }

        try (PreparedStatement st = cn.prepareStatement(RequeteProprietaire.reqFindById())) {
            RequeteProprietaire.paramFindById(st, id[0]);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapProprietaire(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Proprietaire> findAll() throws SQLException {
        List<Proprietaire> list = new ArrayList<>();

        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteProprietaire.reqFindAll())) {

            while (rs.next()) {
                list.add(mapProprietaire(rs));
            }
        }
        return list;
    }

    /**
     * Trouve le propriétaire d'un bâtiment
     */
    public Proprietaire findByBatiment(long idBatiment) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteProprietaire.reqFindByBatiment())) {
            RequeteProprietaire.paramFindByBatiment(st, idBatiment);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapProprietaire(rs);
                }
            }
        }
        return null;
    }

    // Méthode de mapping
    private Proprietaire mapProprietaire(ResultSet rs) throws SQLException {
        Long idBatiment = rs.getLong("ID_BATIMENT");
        if (rs.wasNull()) idBatiment = null;
        
        return new Proprietaire(
            rs.getLong("ID_PROPIETAIRE"),
            rs.getString("NOM_PROP"),
            rs.getString("PRENOM_PROP"),
            rs.getString("ADRESSE_PROP"),
            rs.getString("TEL_PROP"),
            rs.getString("MAIL_PROP"),
            idBatiment
        );
    }

    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) {
            cn.close();
        }
    }
}
