package modele.dao;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

import modele.dao.Requete.RequeteGarage;
import modele.Garage;

/**
 * DAO pour la table GARAGE
 */
public class GarageDao implements dao<Garage>, AutoCloseable {

    private Connection cn;

    @Override
    public void setConnection(Connection cn) {
        this.cn = cn;
    }

    public GarageDao() throws SQLException {
        cn = UtOracleDataSource.getConnection();
        if (cn == null || cn.isClosed()) {
            UtOracleDataSource.creerAcces();
            cn = UtOracleDataSource.getConnection();
        }
    }

    @Override
    public void create(Garage g) throws SQLException {
        if (g.getIdBatiment() == null) {
            throw new SQLException("ID_BATIMENT est obligatoire pour crÃ©er un garage.");
        }
        
        try (PreparedStatement st = cn.prepareStatement(RequeteGarage.reqCreate())) {
            RequeteGarage.paramCreate(st, g);
            st.executeUpdate();
        }
    }

    @Override
    public void update(Garage g) throws SQLException {
        if (g.getIdBatiment() == null) {
            throw new SQLException("ID_BATIMENT est obligatoire.");
        }
        
        try (PreparedStatement st = cn.prepareStatement(RequeteGarage.reqUpdate())) {
            RequeteGarage.paramUpdate(st, g);
            st.executeUpdate();
        }
    }

    @Override
    public void delete(Garage g) throws SQLException {
        delete(g.getIdGarageLong());
    }

    public void delete(long idGarage) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteGarage.reqDelete())) {
            RequeteGarage.paramDelete(st, idGarage);
            st.executeUpdate();
        }
    }

    @Override
    public Garage findById(String... id) throws SQLException {
        if (id.length < 1) {
            throw new SQLException("ID manquant");
        }
        return findById(Long.parseLong(id[0]));
    }

    public Garage findById(long idGarage) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteGarage.reqFindById())) {
            RequeteGarage.paramFindById(st, idGarage);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapGarage(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Garage> findAll() throws SQLException {
        List<Garage> list = new ArrayList<>();

        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteGarage.reqFindAll())) {

            while (rs.next()) {
                list.add(mapGarage(rs));
            }
        }
        return list;
    }
    
    public List<Object[]> findAllAvecAdresseComplete() throws SQLException {
        List<Object[]> list = new ArrayList<>();

        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteGarage.reqFindAllAvecAdresseComplete())) {

            while (rs.next()) {
                Garage g = mapGarage(rs);
                String adresseComplete = rs.getString("ADRESSE_COMPLETE");
                String statut = rs.getString("STATUT");
                list.add(new Object[] { g, adresseComplete, statut });
            }
        }
        return list;
    }

    public List<Garage> findByBatiment(long idBatiment) throws SQLException {
        List<Garage> list = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(RequeteGarage.reqFindByBatiment())) {
            RequeteGarage.paramFindByBatiment(st, idBatiment);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(mapGarage(rs));
                }
            }
        }
        return list;
    }
    
    public List<Garage> findByBatiment(String idBatiment) throws SQLException {
        return findByBatiment(Long.parseLong(idBatiment));
    }

    public List<Garage> findByLogement(long idLogement) throws SQLException {
        List<Garage> list = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(RequeteGarage.reqFindByLogement())) {
            RequeteGarage.paramFindByLogement(st, idLogement);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(mapGarage(rs));
                }
            }
        }
        return list;
    }

    public List<Garage> findByBail(long idBail) throws SQLException {
        List<Garage> list = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(RequeteGarage.reqFindByBail())) {
            RequeteGarage.paramFindByBail(st, idBail);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(mapGarage(rs));
                }
            }
        }
        return list;
    }

    public List<Garage> findLibres() throws SQLException {
        List<Garage> list = new ArrayList<>();

        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteGarage.reqFindLibres())) {

            while (rs.next()) {
                list.add(mapGarage(rs));
            }
        }
        return list;
    }

    public void lierAuBatiment(long idGarage, long idBatiment) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteGarage.reqLierAuBatiment())) {
            RequeteGarage.paramLierAuBatiment(st, idGarage, idBatiment);
            int rows = st.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Garage non trouvÃ© (ID: " + idGarage + ")");
            }
        }
    }

    public void lierAuBail(long idGarage, long idBail) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteGarage.reqLierAuBail())) {
            RequeteGarage.paramLierAuBail(st, idGarage, idBail);
            st.executeUpdate();
        }
    }

    public void delierDuBail(long idGarage) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteGarage.reqDelierDuBail())) {
            RequeteGarage.paramDelierDuBail(st, idGarage);
            st.executeUpdate();
        }
    }
    



    // Methode de mapping
    private Garage mapGarage(ResultSet rs) throws SQLException {
        Integer surfaceGarage = rs.getInt("SURFACE_GARAGE");
        if (rs.wasNull()) surfaceGarage = null;
        
        Long idBatiment = rs.getLong("ID_BATIMENT");
        if (rs.wasNull()) idBatiment = null;
        
        Long idLogement = rs.getLong("ID_LOGEMENT");
        if (rs.wasNull()) idLogement = null;
        
        Long idBail = rs.getLong("ID_BAIL");
        if (rs.wasNull()) idBail = null;
        
        String complement = null;
        try {
            complement = rs.getString("COMPLEMENT_ADRESSE");
        } catch (SQLException e) {
            // Colonne n'existe pas
        }
        if (complement == null || complement.trim().isEmpty()) {
            complement = rs.getString("ADRESSE_GARAGE");
        }
        
        return new Garage(
            rs.getLong("ID_GARAGE"),
            surfaceGarage,
            rs.getLong("NUM_FISCAL"),
            complement,
            idBatiment,
            idLogement,
            idBail
        );
    }

    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) {
            cn.close();
        }
    }
}
