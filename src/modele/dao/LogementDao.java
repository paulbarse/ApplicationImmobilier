package modele.dao;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

import modele.dao.Requete.RequeteLogement;
import modele.Batiment;
import modele.Logement;

/**
 * DAO pour la table LOGEMENT
 */
public class LogementDao implements dao<Logement>, AutoCloseable {

    private Connection cn;

    @Override
    public void setConnection(Connection cn) {
        this.cn = cn;
    }

    public LogementDao() throws SQLException {
        if (cn == null) {
            cn = UtOracleDataSource.getConnection();
        }
        if (cn == null || cn.isClosed()) {
            UtOracleDataSource.creerAcces();
            cn = UtOracleDataSource.getConnection();
        }
    }

    @Override
    public void create(Logement logement) throws SQLException {
        // Validation métier
        if (logement.getIdBatiment() == null) {
            throw new SQLException("ID_BATIMENT est obligatoire pour créer un logement.");
        }
        
        try (PreparedStatement st = cn.prepareStatement(RequeteLogement.reqCreate())) {
            RequeteLogement.paramCreate(st, logement);
            st.executeUpdate();
        }
    }

    @Override
    public void update(Logement logement) throws SQLException {
        // Validation métier
        if (logement.getIdBatiment() == null) {
            throw new SQLException("ID_BATIMENT est obligatoire.");
        }
        
        try (PreparedStatement st = cn.prepareStatement(RequeteLogement.reqUpdate())) {
            RequeteLogement.paramUpdate(st, logement);
            st.executeUpdate();
        }
    }

    @Override
    public void delete(Logement logement) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteLogement.reqDelete())) {
            RequeteLogement.paramDelete(st, logement);
            st.executeUpdate();
        }
    }

    /**
     * Supprime un logement par son ID
     */
    public void delete(long idLogement) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteLogement.reqDeleteById())) {
            RequeteLogement.paramDeleteById(st, idLogement);
            st.executeUpdate();
        }
    }

    @Override
    public Logement findById(String... id) throws SQLException {
        if (id.length < 1) {
            throw new SQLException("ID manquant");
        }

        try (PreparedStatement st = cn.prepareStatement(RequeteLogement.reqFindById())) {
            RequeteLogement.paramFindById(st, id[0]);
            
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapLogement(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Logement> findAll() throws SQLException {
        List<Logement> list = new ArrayList<>();
        
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteLogement.reqFindAll())) {

            while (rs.next()) {
                list.add(mapLogement(rs));
            }
        }
        return list;
    }
    
    /**
     * Trouve tous les logements avec leur adresse complète (Vue)
     */
    public List<Object[]> findAllAvecAdresseComplete() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteLogement.reqFindAllAvecAdresseComplete())) {

            while (rs.next()) {
                Logement l = mapLogement(rs);
                String adresseComplete = rs.getString("ADRESSE_COMPLETE");
                String statut = rs.getString("STATUT");
                list.add(new Object[] { l, adresseComplete, statut });
            }
        }
        return list;
    }

    /**
     * Trouve les logements d'un bâtiment
     */
    public List<Logement> findByBatiment(long idBatiment) throws SQLException {
        List<Logement> list = new ArrayList<>();
        
        try (PreparedStatement st = cn.prepareStatement(RequeteLogement.reqFindByBatiment())) {
            RequeteLogement.paramFindByBatiment(st, idBatiment);
            
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(mapLogement(rs));
                }
            }
        }
        return list;
    }
    
    // Surcharge pour compatibilité
    public List<Logement> findByBatiment(String idBatiment) throws SQLException {
        return findByBatiment(Long.parseLong(idBatiment));
    }

    /**
     * Trouve le logement associé à un bail
     */
    public Logement findByBail(long idBail) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteLogement.reqFindByBail())) {
            RequeteLogement.paramFindByBail(st, idBail);
            
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapLogement(rs);
                }
            }
        }
        return null;
    }

    /**
     * Trouve les logements libres (sans bail)
     */
    public List<Logement> findLibres() throws SQLException {
        List<Logement> list = new ArrayList<>();
        
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteLogement.reqFindLibres())) {

            while (rs.next()) {
                list.add(mapLogement(rs));
            }
        }
        return list;
    }

    /**
     * Lie un logement à un bâtiment par ID
     */
    public void lierAuBatiment(long idLogement, long idBatiment) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteLogement.reqLierAuBatiment())) {
            RequeteLogement.paramLierAuBatiment(st, idLogement, idBatiment);
            
            int rows = st.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Logement non trouvé (ID: " + idLogement + ")");
            }
        }
    }

    /**
     * Lie un logement à un bail
     */
    public void lierAuBail(long idLogement, long idBail) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteLogement.reqLierAuBail())) {
            RequeteLogement.paramLierAuBail(st, idLogement, idBail);
            st.executeUpdate();
        }
    }

    /**
     * Délie un logement de son bail
     */
    public void delierDuBail(long idLogement) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteLogement.reqDelierDuBail())) {
            RequeteLogement.paramDelierDuBail(st, idLogement);
            st.executeUpdate();
        }
    }

    // Méthode de mapping 
    private Logement mapLogement(ResultSet rs) throws SQLException {
        Long idReleve = rs.getLong("ID_RELEVE");
        if (rs.wasNull()) idReleve = null;

        Long idBatiment = rs.getLong("ID_BATIMENT");
        if (rs.wasNull()) idBatiment = null;

        Long idBail = rs.getLong("ID_BAIL");
        if (rs.wasNull()) idBail = null;
        
        // Gestion de l'adresse / complément
        String complement = null;
        try {
            complement = rs.getString("COMPLEMENT_ADRESSE");
        } catch (SQLException e) {
            // Colonne n'existe pas encore, ignorer
        }
        if (complement == null || complement.trim().isEmpty()) {
            complement = rs.getString("ADRESSE_LOGEMENT");
        }
        
        // 1. Création du Logement avec les données brutes
        Logement logement = new Logement(
            rs.getLong("ID_LOGEMENT"),
            rs.getString("TYPE_LOGEMENT"),
            rs.getString("ADRESSE_LOGEMENT"),
            rs.getString("COMPLEMENT_ADRESSE"),
            rs.getLong("NUM_FISCAL"),
            rs.getDouble("SURFACE"),
            rs.getInt("NB_PIECES"),
            idReleve,
            idBatiment,
            idBail
        );


        if (idBatiment != null) {
            Batiment batiment = chargerBatimentDirectement(idBatiment);
            logement.setBatiment(batiment);
        }
        
        return logement;
    }
    
    /**
     * Charge un batiment directement via SQL sans créer de BatimentDao
     */
    private Batiment chargerBatimentDirectement(long idBatiment) throws SQLException {
        String sql = "SELECT ID_BATIMENT, ADRESSE, NOMBRE_D_ETAGE, TYPE_BATIMENT, RUE, CODE_POSTALE " +
                     "FROM BATIMENT WHERE ID_BATIMENT = ?";
        
        try (PreparedStatement st = cn.prepareStatement(sql)) {
            st.setLong(1, idBatiment);
            
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    // Gestion du nombre d'étages nullable
                    Integer nbEtages = rs.getInt("NOMBRE_D_ETAGE");
                    if (rs.wasNull()) {
                        nbEtages = null;
                    }
                    
                    return new Batiment(
                        rs.getLong("ID_BATIMENT"),
                        rs.getString("ADRESSE"),
                        nbEtages,
                        rs.getString("TYPE_BATIMENT"),
                        rs.getString("RUE"),
                        rs.getInt("CODE_POSTALE")
                    );
                }
            }
        }
        return null;
    }
    
    @Override
    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) {
            cn.close();
        }
    }
}