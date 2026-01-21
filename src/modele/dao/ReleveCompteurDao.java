package modele.dao;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

import modele.dao.Requete.RequeteReleveCompteur;
import modele.ReleveCompteur;

/**
 * DAO pour les relevés de compteur 
 */
public class ReleveCompteurDao {

    private Connection cn;

    public ReleveCompteurDao() throws SQLException {
        cn = UtOracleDataSource.getConnection();
        if (cn == null || cn.isClosed()) {
            UtOracleDataSource.creerAcces();
            cn = UtOracleDataSource.getConnection();
        }
    }

    public List<ReleveCompteur> findByBatiment(long idBatiment) throws SQLException {
        List<ReleveCompteur> list = new ArrayList<>();

        try (PreparedStatement ps = cn.prepareStatement(RequeteReleveCompteur.reqFindByBatiment())) {
            RequeteReleveCompteur.paramFindByBatiment(ps, idBatiment);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapReleve(rs));
                }
            }
        }
        return list;
    }

    public ReleveCompteur findById(int idReleve) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(RequeteReleveCompteur.reqFindById())) {
            RequeteReleveCompteur.paramFindById(ps, idReleve);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapReleve(rs);
                }
            }
        }
        return null;
    }

    public void create(ReleveCompteur releve, long idBatiment) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(RequeteReleveCompteur.reqCreate())) {
            RequeteReleveCompteur.paramCreate(ps, releve);
            ps.executeUpdate();
        }
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteReleveCompteur.reqLierAuLogement())) {
            RequeteReleveCompteur.paramLierAuLogement(ps, releve.getIdReleve(), idBatiment);
            int updated = ps.executeUpdate();
            
            if (updated == 0) {
                try (PreparedStatement ps2 = cn.prepareStatement(RequeteReleveCompteur.reqLierAuGarage())) {
                    RequeteReleveCompteur.paramLierAuGarage(ps2, releve.getIdReleve(), idBatiment);
                    ps2.executeUpdate();
                }
            }
        }
    }

    public void update(ReleveCompteur releve) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(RequeteReleveCompteur.reqUpdate())) {
            RequeteReleveCompteur.paramUpdate(ps, releve);
            ps.executeUpdate();
        }
    }

    public void update(int idReleve, String type, java.util.Date date, 
                      int nouvelIndex, int ancienIndex, String unite) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(RequeteReleveCompteur.reqUpdateParams())) {
            RequeteReleveCompteur.paramUpdateParams(ps, idReleve, type, date, nouvelIndex, ancienIndex, unite);
            ps.executeUpdate();
        }
    }

    public void delete(int idReleve) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(RequeteReleveCompteur.reqDelierLogement())) {
            RequeteReleveCompteur.paramDelierLogement(ps, idReleve);
            ps.executeUpdate();
        }
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteReleveCompteur.reqDelierGarage())) {
            RequeteReleveCompteur.paramDelierGarage(ps, idReleve);
            ps.executeUpdate();
        }
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteReleveCompteur.reqDelete())) {
            RequeteReleveCompteur.paramDelete(ps, idReleve);
            ps.executeUpdate();
        }
    }

    public int getNextId() throws SQLException {
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteReleveCompteur.reqGetNextId())) {
            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }
        return 1;
    }

    private ReleveCompteur mapReleve(ResultSet rs) throws SQLException {
        return new ReleveCompteur(
            rs.getInt("ID_RELEVE"),
            rs.getString("UNITE"),
            rs.getString("TYPE"),
            rs.getInt("NOUVELLE_INDEX"),
            rs.getDate("DATE_"),
            rs.getInt("NUM_COMPTEUR"),
            rs.getInt("ANCIEN_INDEX")
        );
    }

    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) cn.close();
    }
}
