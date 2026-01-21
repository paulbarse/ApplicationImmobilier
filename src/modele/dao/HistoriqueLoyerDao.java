package modele.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import modele.dao.Requete.RequeteHistoriqueLoyer;
import modele.HistoriqueLoyer;

/**
 * DAO pour l'historique des loyers des baux clotures.
 */
public class HistoriqueLoyerDao {

    private Connection cn;

    public HistoriqueLoyerDao() throws SQLException {
        cn = UtOracleDataSource.getConnection();
        if (cn == null || cn.isClosed()) {
            UtOracleDataSource.creerAcces();
            cn = UtOracleDataSource.getConnection();
        }
    }

    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) {
            cn.close();
        }
    }

    /**
     * Recupere tout l'historique
     */
    public List<HistoriqueLoyer> findAll() throws SQLException {
        List<HistoriqueLoyer> result = new ArrayList<>();
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteHistoriqueLoyer.reqFindAll());
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                result.add(mapHistoriqueLoyer(rs));
            }
        }
        
        return result;
    }

    /**
     * Recupere l'historique pour une annee donnee
     */
    public List<HistoriqueLoyer> findByAnnee(int annee) throws SQLException {
        List<HistoriqueLoyer> result = new ArrayList<>();
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteHistoriqueLoyer.reqFindByAnnee())) {
            RequeteHistoriqueLoyer.paramFindByAnnee(ps, annee);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapHistoriqueLoyer(rs));
                }
            }
        }
        
        return result;
    }

    /**
     * Recupere l'historique pour un bail donne
     */
    public List<HistoriqueLoyer> findByBail(long idBail) throws SQLException {
        List<HistoriqueLoyer> result = new ArrayList<>();
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteHistoriqueLoyer.reqFindByBail())) {
            RequeteHistoriqueLoyer.paramFindByBail(ps, idBail);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapHistoriqueLoyer(rs));
                }
            }
        }
        
        return result;
    }

    /**
     * Recupere les totaux pour une annee
     */
    public double[] getTotauxByAnnee(int annee) throws SQLException {
        double[] result = new double[3];
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteHistoriqueLoyer.reqGetTotauxByAnnee())) {
            RequeteHistoriqueLoyer.paramGetTotauxByAnnee(ps, annee);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result[0] = rs.getDouble("TOTAL_LOYERS");
                    result[1] = rs.getDouble("TOTAL_PROVISIONS");
                    result[2] = rs.getDouble("NB_LOYERS");
                }
            }
        }
        
        return result;
    }

    /**
     * Recupere les totaux pour un batiment et une annee (baux clotures uniquement)
     */
    public double[] getTotauxByBatimentAndAnnee(long idBatiment, int annee) throws SQLException {
        double[] result = new double[2];
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteHistoriqueLoyer.reqGetTotauxByBatimentAndAnnee())) {
            RequeteHistoriqueLoyer.paramGetTotauxByBatimentAndAnnee(ps, idBatiment, annee);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result[0] = rs.getDouble("TOTAL_LOYERS");
                    result[1] = rs.getDouble("TOTAL_PROVISIONS");
                }
            }
        }
        
        return result;
    }

    /**
     * Recupere un resume par bien (pour affichage dans le dialogue)
     */
    public List<Object[]> getResumeParBien() throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteHistoriqueLoyer.reqGetResumeParBien());
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Object[] row = new Object[8];
                row[0] = rs.getString("NOM_BIEN");
                row[1] = rs.getString("NOM_LOCATAIRE");
                row[2] = rs.getInt("ANNEE");
                row[3] = rs.getDouble("TOTAL_LOYERS");
                row[4] = rs.getDouble("TOTAL_PROVISIONS");
                row[5] = rs.getInt("NB_MOIS");
                row[6] = rs.getInt("NB_QUITTANCES");
                row[7] = rs.getDate("DATE_CLOTURE");
                result.add(row);
            }
        }
        
        return result;
    }

    /**
     * Mapping ResultSet -> HistoriqueLoyer
     */
    private HistoriqueLoyer mapHistoriqueLoyer(ResultSet rs) throws SQLException {
        HistoriqueLoyer h = new HistoriqueLoyer();
        
        h.setIdHistorique(rs.getLong("ID_HISTORIQUE"));
        h.setIdBail(rs.getLong("ID_BAIL"));
        
        long idLogement = rs.getLong("ID_LOGEMENT");
        h.setIdLogement(rs.wasNull() ? null : idLogement);
        
        long idGarage = rs.getLong("ID_GARAGE");
        h.setIdGarage(rs.wasNull() ? null : idGarage);
        
        h.setNomBien(rs.getString("NOM_BIEN"));
        h.setNomLocataire(rs.getString("NOM_LOCATAIRE"));
        h.setMois(rs.getString("MOIS"));
        h.setAnnee(rs.getInt("ANNEE"));
        h.setMontantLoyer(rs.getDouble("MONTANT_LOYER"));
        h.setMontantProvision(rs.getDouble("MONTANT_PROVISION"));
        h.setDatePaiement(rs.getDate("DATE_PAIEMENT"));
        h.setQuittanceGeneree("O".equals(rs.getString("QUITTANCE_GENEREE")));
        h.setDateCloture(rs.getDate("DATE_CLOTURE"));
        
        long idBatiment = rs.getLong("ID_BATIMENT");
        h.setIdBatiment(rs.wasNull() ? null : idBatiment);
        
        return h;
    }
}