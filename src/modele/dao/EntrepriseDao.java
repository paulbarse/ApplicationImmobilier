package modele.dao;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

import modele.dao.Requete.RequeteEntreprise;
import modele.Entreprise;

/**
 * DAO pour la table ENTREPRISE
 */
public class EntrepriseDao implements dao<Entreprise>, AutoCloseable {

    private Connection cn;

    @Override
    public void setConnection(Connection cn) {
        this.cn = cn;
    }

    public EntrepriseDao() throws SQLException {
        cn = UtOracleDataSource.getConnection();
        if (cn == null || cn.isClosed()) {
            UtOracleDataSource.creerAcces();
            cn = UtOracleDataSource.getConnection();
        }
    }

    @Override
    public void create(Entreprise entreprise) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteEntreprise.reqCreate())) {
            RequeteEntreprise.paramCreate(st, entreprise);
            st.executeUpdate();
        }
    }

    @Override
    public void update(Entreprise entreprise) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteEntreprise.reqUpdate())) {
            RequeteEntreprise.paramUpdate(st, entreprise);
            st.executeUpdate();
        }
    }

    @Override
    public void delete(Entreprise entreprise) throws SQLException {
        // Verifier d'abord si des factures sont liees
        if (countFactures(entreprise.getSiret()) > 0) {
            throw new SQLException("Impossible de supprimer cette entreprise : des factures y sont associees.");
        }

        try (PreparedStatement st = cn.prepareStatement(RequeteEntreprise.reqDelete())) {
            RequeteEntreprise.paramDelete(st, entreprise);
            st.executeUpdate();
        }
    }

    @Override
    public Entreprise findById(String... id) throws SQLException {
        if (id.length < 1) {
            throw new SQLException("SIRET manquant");
        }

        try (PreparedStatement st = cn.prepareStatement(RequeteEntreprise.reqFindById())) {
            RequeteEntreprise.paramFindById(st, id[0]);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapEntreprise(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Entreprise> findAll() throws SQLException {
        List<Entreprise> list = new ArrayList<>();

        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteEntreprise.reqFindAll())) {

            while (rs.next()) {
                list.add(mapEntreprise(rs));
            }
        }
        return list;
    }

    /**
     * Trouve les entreprises par specialite
     */
    public List<Entreprise> findBySpecialite(String specialite) throws SQLException {
        List<Entreprise> list = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(RequeteEntreprise.reqFindBySpecialite())) {
            RequeteEntreprise.paramFindBySpecialite(st, specialite);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEntreprise(rs));
                }
            }
        }
        return list;
    }

    /**
     * Compte le nombre de factures NON PAYEES pour une entreprise
     */
    public int countFactures(long siret) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteEntreprise.reqCountFactures())) {
            RequeteEntreprise.paramCountFactures(cs, siret);
            cs.execute();
            return cs.getInt(1);
        }
    }

    // Methode de mapping
    private Entreprise mapEntreprise(ResultSet rs) throws SQLException {
        return new Entreprise(
            rs.getLong("SIRET"),
            rs.getString("NOM_ENTREPRISE"),
            rs.getString("ADRESSE"),
            rs.getString("TELEPHONE"),
            rs.getString("EMAIL"),
            rs.getString("SPECIALITE")
        );
    }

    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) {
            cn.close();
        }
    }
}
