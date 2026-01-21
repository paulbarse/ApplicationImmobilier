package modele.dao;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

import modele.dao.Requete.RequeteLoyer;
import modele.Loyer;

/**
 * DAO pour la table LOYER
 */
public class LoyerDao implements AutoCloseable {
    
    public static final String STATUT_EN_ATTENTE = "EN_ATTENTE";
    public static final String STATUT_PAYE = "PAYE";
    public static final String STATUT_RETARD = "RETARD";
    
    private Connection cn;

    public LoyerDao() throws SQLException {
        cn = UtOracleDataSource.getConnection();
        if (cn == null || cn.isClosed()) {
            UtOracleDataSource.creerAcces();
            cn = UtOracleDataSource.getConnection();
        }
    }

    public void create(Loyer loyer) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteLoyer.reqCreate())) {
            RequeteLoyer.paramCreate(st, loyer);
            st.executeUpdate();
        }
    }

    public void update(Loyer loyer) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteLoyer.reqUpdate())) {
            RequeteLoyer.paramUpdate(st, loyer);
            st.executeUpdate();
        }
    }

    public void delete(long idLoyer) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteLoyer.reqDelete())) {
            RequeteLoyer.paramDelete(st, idLoyer);
            st.executeUpdate();
        }
    }

    public Loyer findById(long idLoyer) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteLoyer.reqFindById())) {
            RequeteLoyer.paramFindById(st, idLoyer);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapLoyer(rs);
                }
            }
        }
        return null;
    }

    public List<Loyer> findAll() throws SQLException {
        List<Loyer> liste = new ArrayList<>();

        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteLoyer.reqFindAll())) {

            while (rs.next()) {
                liste.add(mapLoyer(rs));
            }
        }
        return liste;
    }

    public List<Loyer> findByBail(long idBail) throws SQLException {
        List<Loyer> liste = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(RequeteLoyer.reqFindByBail())) {
            RequeteLoyer.paramFindByBail(st, idBail);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    liste.add(mapLoyer(rs));
                }
            }
        }
        return liste;
    }
    
    public List<Loyer> findByBail(String idBail) {
        List<Loyer> liste = new ArrayList<>();
        try {
            liste = findByBail(Long.parseLong(idBail));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return liste;
    }
    
    public List<Loyer> findByBailAndAnnee(long idBail, int annee) throws SQLException {
        List<Loyer> liste = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(RequeteLoyer.reqFindByBailAndAnnee())) {
            RequeteLoyer.paramFindByBailAndAnnee(st, idBail, annee);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    liste.add(mapLoyer(rs));
                }
            }
        }
        return liste;
    }

    public List<Loyer> findImpayesByBail(long idBail) throws SQLException {
        List<Loyer> liste = new ArrayList<>();

        try (PreparedStatement st = cn.prepareStatement(RequeteLoyer.reqFindImpayesByBail())) {
            RequeteLoyer.paramFindImpayesByBail(st, idBail);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    liste.add(mapLoyer(rs));
                }
            }
        }
        return liste;
    }

    public void marquerPaye(long idLoyer, java.util.Date datePaiement) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteLoyer.reqMarquerPaye())) {
            RequeteLoyer.paramMarquerPaye(st, idLoyer, datePaiement);
            st.executeUpdate();
        }
    }

    public void marquerPayeProcedure(long idLoyer, java.util.Date datePaiement) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteLoyer.reqMarquerPayeProcedure())) {
            RequeteLoyer.paramMarquerPayeProcedure(cs, idLoyer, datePaiement);
            cs.execute();
        }
    }
    
    public void marquerRetard(long idLoyer) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteLoyer.reqMarquerRetard())) {
            RequeteLoyer.paramMarquerRetard(cs, idLoyer);
            cs.execute();
        }
    }
    
    public void marquerEnAttente(long idLoyer) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteLoyer.reqMarquerEnAttente())) {
            RequeteLoyer.paramMarquerEnAttente(cs, idLoyer);
            cs.execute();
        }
    }

    public void genererLoyers(long idBail, int annee) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteLoyer.reqGenererLoyers())) {
            RequeteLoyer.paramGenererLoyers(cs, idBail, annee);
            cs.execute();
        }
    }

    public double getTotalLoyersByBailAndAnnee(long idBail, int annee) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteLoyer.reqGetTotalLoyersByBailAndAnnee())) {
            RequeteLoyer.paramGetTotalLoyersByBailAndAnnee(cs, idBail, annee);
            cs.execute();
            return cs.getDouble(1);
        }
    }

    public double getTotalProvisionsByBailAndAnnee(long idBail, int annee) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteLoyer.reqGetTotalProvisionsByBailAndAnnee())) {
            RequeteLoyer.paramGetTotalProvisionsByBailAndAnnee(cs, idBail, annee);
            cs.execute();
            return cs.getDouble(1);
        }
    }
    
    public double getTotalProvisionsBailAvecFallback(long idBail, int annee) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteLoyer.reqGetTotalProvisionsBailAvecFallback())) {
            RequeteLoyer.paramGetTotalProvisionsBailAvecFallback(cs, idBail, annee);
            cs.execute();
            return cs.getDouble(1);
        }
    }
    
    public int countLoyersPayes(long idBail, int annee) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteLoyer.reqCountLoyersPayes())) {
            RequeteLoyer.paramCountLoyersPayes(st, idBail, annee);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    public int countLoyersRetard(long idBail) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteLoyer.reqCountLoyersRetard())) {
            RequeteLoyer.paramCountLoyersRetard(st, idBail);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    public void updateMontants(long idLoyer, double montantLoyer, Double montantProvision) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteLoyer.reqUpdateMontants())) {
            RequeteLoyer.paramUpdateMontants(st, idLoyer, montantLoyer, montantProvision);
            st.executeUpdate();
        }
    }
    
    private Loyer mapLoyer(ResultSet rs) throws SQLException {
        Double montantProvision = rs.getDouble("MONTANT_PROVISION");
        if (rs.wasNull()) montantProvision = null;
        
        Integer indiceIRL = rs.getInt("INDICE_IRL");
        if (rs.wasNull()) indiceIRL = null;
        
        Integer ancienIRL = rs.getInt("ANCIEN_IRL");
        if (rs.wasNull()) ancienIRL = null;
        
        return new Loyer(
            rs.getLong("ID_LOYER"),
            rs.getString("QUITTANCE"),
            rs.getDouble("MONTANT_LOYER"),
            rs.getString("MOIS"),
            montantProvision,
            rs.getString("STATUT"),
            indiceIRL,
            ancienIRL,
            rs.getDate("DATE_PAIEMENT"),
            rs.getLong("ID_BAIL")
        );
    }


    @Override
    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) {
            cn.close();
        }
    }
    
    public void importerPaiement(long idLogement, long idLocataire, String moisStr, double loyer, double provision) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteLoyer.reqImporterPaiement())) {
            RequeteLoyer.paramImporterPaiement(cs, idLogement, idLocataire, moisStr, loyer, provision);
            cs.execute();
        }
    }
}
