package modele.dao;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

import modele.dao.Requete.RequeteFacture;
import modele.Facture;

/**
 * DAO pour la table FACTURE
 */
public class FactureDao implements AutoCloseable {

    private Connection cn;

    public FactureDao() throws SQLException {
        cn = UtOracleDataSource.getConnection();
        if (cn == null || cn.isClosed()) {
            UtOracleDataSource.creerAcces();
            cn = UtOracleDataSource.getConnection();
        }
    }


    // METHODES CRUD


    public void create(Facture f) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteFacture.reqCreate())) {
            RequeteFacture.paramCreate(st, f);
            st.executeUpdate();
        }
    }

    public Facture findById(long idFacture) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteFacture.reqFindById())) {
            RequeteFacture.paramFindById(st, idFacture);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Facture f = mapFacture(rs);
                    f.setNomEntreprise(rs.getString("NOM_ENTREPRISE"));
                    return f;
                }
            }
        }
        return null;
    }

    public void update(Facture f) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteFacture.reqUpdate())) {
            RequeteFacture.paramUpdate(st, f);
            st.executeUpdate();
        }
    }

    public void delete(long idFacture) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteFacture.reqDelete())) {
            RequeteFacture.paramDelete(st, idFacture);
            st.executeUpdate();
        }
    }

    public long getNextId() throws SQLException {
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteFacture.reqGetNextId())) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 1;
    }


    // METHODES DE RECHERCHE


    public List<Facture> findAll() throws SQLException {
        List<Facture> liste = new ArrayList<>();
        
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteFacture.reqFindAll())) {
            while (rs.next()) {
                Facture f = mapFacture(rs);
                f.setNomEntreprise(rs.getString("NOM_ENTREPRISE"));
                liste.add(f);
            }
        }
        return liste;
    }

    public List<Facture> findByBatiment(long idBatiment) throws SQLException {
        List<Facture> liste = new ArrayList<>();
        
        try (PreparedStatement st = cn.prepareStatement(RequeteFacture.reqFindByBatiment())) {
            RequeteFacture.paramFindByBatiment(st, idBatiment);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Facture f = mapFacture(rs);
                    f.setNomEntreprise(rs.getString("NOM_ENTREPRISE"));
                    liste.add(f);
                }
            }
        }
        return liste;
    }

    public List<Facture> findByLogement(long idLogement) throws SQLException {
        List<Facture> liste = new ArrayList<>();
        
        try (PreparedStatement st = cn.prepareStatement(RequeteFacture.reqFindByLogement())) {
            RequeteFacture.paramFindByLogement(st, idLogement);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Facture f = mapFacture(rs);
                    f.setNomEntreprise(rs.getString("NOM_ENTREPRISE"));
                    liste.add(f);
                }
            }
        }
        return liste;
    }

    public List<Facture> findByBail(long idBail) throws SQLException {
        List<Facture> liste = new ArrayList<>();
        
        try (PreparedStatement st = cn.prepareStatement(RequeteFacture.reqFindByBail())) {
            RequeteFacture.paramFindByBail(st, idBail);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Facture f = mapFacture(rs);
                    f.setNomEntreprise(rs.getString("NOM_ENTREPRISE"));
                    liste.add(f);
                }
            }
        }
        return liste;
    }

    public List<Facture> findByEntreprise(long siret) throws SQLException {
        List<Facture> liste = new ArrayList<>();
        
        try (PreparedStatement st = cn.prepareStatement(RequeteFacture.reqFindByEntreprise())) {
            RequeteFacture.paramFindByEntreprise(st, siret);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Facture f = mapFacture(rs);
                    f.setNomEntreprise(rs.getString("NOM_ENTREPRISE"));
                    liste.add(f);
                }
            }
        }
        return liste;
    }


    // METHODES DE CALCUL


    public double getTotalTravauxByBatimentAndAnnee(long idBatiment, int annee) throws SQLException {
        try {
            try (CallableStatement cs = cn.prepareCall(RequeteFacture.reqGetTotalTravauxBatiment())) {
                RequeteFacture.paramGetTotalTravauxBatiment(cs, idBatiment, annee);
                cs.execute();
                return cs.getDouble(1);
            }
        } catch (SQLException e) {
            return getTotalTravauxByBatimentAndAnneeSQL(idBatiment, annee);
        }
    }

    private double getTotalTravauxByBatimentAndAnneeSQL(long idBatiment, int annee) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(RequeteFacture.reqGetTotalTravauxByBatimentAndAnneeSQL())) {
            RequeteFacture.paramGetTotalTravauxByBatimentAndAnneeSQL(ps, idBatiment, annee);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("TOTAL");
                }
            }
        }
        return 0;
    }


    // FACTURES RECUPERABLES/DEDUCTIBLES

    
    public double getTotalFacturesRecuperablesLocataire(long idBatiment, int annee) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(RequeteFacture.reqGetTotalFacturesRecuperablesLocataire())) {
            RequeteFacture.paramGetTotalFacturesRecuperablesLocataire(ps, idBatiment, annee);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("TOTAL");
                }
            }
        }
        return 0;
    }
    
    public List<Facture> findFacturesRecuperablesLocataire(long idBatiment, int annee) throws SQLException {
        List<Facture> liste = new ArrayList<>();
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteFacture.reqFindFacturesRecuperablesLocataire())) {
            RequeteFacture.paramFindFacturesRecuperablesLocataire(ps, idBatiment, annee);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Facture f = mapFacture(rs);
                    f.setNomEntreprise(rs.getString("NOM_ENTREPRISE"));
                    liste.add(f);
                }
            }
        }
        return liste;
    }
    
    public double getTotalFacturesDeductiblesImpots(long idBatiment, int annee) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(RequeteFacture.reqGetTotalFacturesDeductiblesImpots())) {
            RequeteFacture.paramGetTotalFacturesDeductiblesImpots(ps, idBatiment, annee);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("TOTAL");
                }
            }
        }
        return 0;
    }



    /**
     * Recupere les travaux d'un logement regroupes par entreprise
     */
    public String getTravauxParEntreprise(long idBatiment, long idLogement, int annee) throws SQLException {
        StringBuilder result = new StringBuilder();
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteFacture.reqGetTravauxParEntreprise())) {
            RequeteFacture.paramGetTravauxParEntreprise(ps, annee, idBatiment, idLogement);
            
            try (ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) result.append(", ");
                    String nomEntreprise = rs.getString("NOM_ENTREPRISE");
                    double total = rs.getDouble("TOTAL");
                    result.append(nomEntreprise).append(": ").append(String.format("%.2f", total)).append(" euros");
                    first = false;
                }
            }
        }
        
        return result.length() > 0 ? result.toString() : "Aucun travaux";
    }

    /**
     * Recupere les travaux d'un batiment regroupes par entreprise
     */
    public String getTravauxBatimentParEntreprise(long idBatiment, int annee) throws SQLException {
        StringBuilder result = new StringBuilder();
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteFacture.reqGetTravauxBatimentParEntreprise())) {
            RequeteFacture.paramGetTravauxBatimentParEntreprise(ps, idBatiment, annee);
            
            try (ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) result.append(", ");
                    String nomEntreprise = rs.getString("NOM_ENTREPRISE");
                    double total = rs.getDouble("TOTAL");
                    result.append(nomEntreprise).append(": ").append(String.format("%.2f", total)).append(" euros");
                    first = false;
                }
            }
        }
        
        return result.length() > 0 ? result.toString() : "Aucun travaux";
    }

    /**
     * Recupere le detail des taxes foncieres
     */
    public double[] getDetailTaxesFoncieres(long idBatiment, int annee) throws SQLException {
        double[] result = new double[3]; // [Total, OM, Reste]
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteFacture.reqGetDetailTaxesFoncieres())) {
            RequeteFacture.paramGetDetailTaxesFoncieres(ps, idBatiment, annee);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result[0] = rs.getDouble("TOTAL"); // Total taxes foncieres
                    result[1] = rs.getDouble("OM");    // Ordures menageres (TEOM)
                    result[2] = result[0] - result[1]; // Reste
                }
            }
        }
        
        return result;
    }


    // MAPPING RESULTSET -> FACTURE


    private Facture mapFacture(ResultSet rs) throws SQLException {
        Double acompte = rs.getDouble("ACCOMPTE");
        if (rs.wasNull()) acompte = null;

        Double montantHT = rs.getDouble("MONTANT_HT");
        if (rs.wasNull()) montantHT = null;

        Double montantTEOM = rs.getDouble("MONTANT_TEOM");
        if (rs.wasNull()) montantTEOM = null;

        Integer recupLoc = rs.getInt("RECUPERABLE_LOCATAIRE");
        Boolean recuperableLocataire = rs.wasNull() ? null : (recupLoc == 1);

        Integer dedImpot = rs.getInt("DEDUCTIBLE_IMPOT");
        Boolean deductibleImpot = rs.wasNull() ? null : (dedImpot == 1);

        Double montantDevis = rs.getDouble("MONTANTDEVIS");
        if (rs.wasNull()) montantDevis = null;

        Long idCharge = rs.getLong("ID_CHARGE");
        if (rs.wasNull()) idCharge = null;

        Long idBatiment = rs.getLong("ID_BATIMENT");
        if (rs.wasNull()) idBatiment = null;

        Long idBail = rs.getLong("ID_BAIL");
        if (rs.wasNull()) idBail = null;

        Long idLogement = null;
        try {
            idLogement = rs.getLong("ID_LOGEMENT");
            if (rs.wasNull()) idLogement = null;
        } catch (SQLException e) {
            idLogement = null;
        }

        String statut = rs.getString("STATUT_PAIEMENT");

        return new Facture(
            rs.getLong("ID_FACTURE"),
            acompte,
            rs.getString("NATURE"),
            rs.getString("PERIODE_DEB"),
            rs.getDate("DATE_EMISSION"),
            montantHT,
            rs.getDouble("MONTANT_TTC"),
            montantTEOM,
            recuperableLocataire,
            deductibleImpot,
            rs.getDate("PERIODE_FIN"),
            rs.getString("TRAVAUX"),
            rs.getDate("DATEDEVIS"),
            montantDevis,
            rs.getLong("SIRET"),
            idCharge,
            idBatiment,
            idBail,
            idLogement,
            statut
        );
    }


    // METHODES DE MISE A JOUR DE STATUT


    /**
     * Marque une facture comme payee
     */
    public void marquerPayee(long idFacture) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteFacture.reqMarquerPayee())) {
            RequeteFacture.paramMarquerPayee(st, idFacture);
            st.executeUpdate();
        }
    }

    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) cn.close();
    }
    
    public String getDetailTravauxParEntreprise(long idBatiment, int annee) throws SQLException {
        StringBuilder sb = new StringBuilder();
        
        String sql = "SELECT e.NOM_ENTREPRISE, SUM(f.MONTANT_TTC) as TOTAL " +
                     "FROM FACTURE f " +
                     "JOIN ENTREPRISE e ON f.SIRET = e.SIRET " +
                     "WHERE f.ID_BATIMENT = ? " +
                     "AND EXTRACT(YEAR FROM f.DATE_EMISSION) = ? " +
                     "AND f.TRAVAUX IS NOT NULL " +
                     "GROUP BY e.NOM_ENTREPRISE " +
                     "ORDER BY TOTAL DESC";
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idBatiment);
            ps.setInt(2, annee);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    String nom = rs.getString("NOM_ENTREPRISE");
                    double total = rs.getDouble("TOTAL");
                    sb.append(nom).append(": ").append(String.format("%.2f", total)).append(" euros");
                }
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Récupère les travaux par entreprise pour un logement donné et une année
     */
    public List<Object[]> getTravauxParEntrepriseLogement(long idLogement, int annee) throws SQLException {
        List<Object[]> liste = new ArrayList<>();
        
        String sql = "SELECT e.NOM_ENTREPRISE, e.SIRET, NVL(SUM(f.MONTANT_TTC), 0) AS TOTAL " +
                     "FROM FACTURE f " +
                     "JOIN ENTREPRISE e ON f.SIRET = e.SIRET " +
                     "WHERE f.ID_LOGEMENT = ? " +
                     "AND EXTRACT(YEAR FROM f.DATE_EMISSION) = ? " +
                     "AND (f.TRAVAUX IS NOT NULL OR f.DEDUCTIBLE_IMPOT = 1) " +
                     "GROUP BY e.NOM_ENTREPRISE, e.SIRET " +
                     "ORDER BY TOTAL DESC";
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idLogement);
            ps.setInt(2, annee);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(new Object[] {
                        rs.getString("NOM_ENTREPRISE"),
                        rs.getLong("SIRET"),
                        rs.getDouble("TOTAL")
                    });
                }
            }
        }
        
        return liste;
    }

    /**
     * Récupère les travaux communs par entreprise pour un bâtiment donné et une année
     */
    public List<Object[]> getTravauxParEntrepriseBatiment(long idBatiment, int annee) throws SQLException {
        List<Object[]> liste = new ArrayList<>();
        
        String sql = "SELECT e.NOM_ENTREPRISE, e.SIRET, NVL(SUM(f.MONTANT_TTC), 0) AS TOTAL " +
                     "FROM FACTURE f " +
                     "JOIN ENTREPRISE e ON f.SIRET = e.SIRET " +
                     "WHERE f.ID_BATIMENT = ? " +
                     "AND EXTRACT(YEAR FROM f.DATE_EMISSION) = ? " +
                     "AND (f.TRAVAUX IS NOT NULL OR f.DEDUCTIBLE_IMPOT = 1) " +
                     "AND f.ID_LOGEMENT IS NULL " +  // Travaux communs uniquement
                     "GROUP BY e.NOM_ENTREPRISE, e.SIRET " +
                     "ORDER BY TOTAL DESC";
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idBatiment);
            ps.setInt(2, annee);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(new Object[] {
                        rs.getString("NOM_ENTREPRISE"),
                        rs.getLong("SIRET"),
                        rs.getDouble("TOTAL")
                    });
                }
            }
        }
        
        return liste;
    }

    /**
     * Récupère TOUS les travaux par entreprise pour un bâtiment 
     */
    public List<Object[]> getTousTravauxParEntrepriseBatiment(long idBatiment, int annee) throws SQLException {
        List<Object[]> liste = new ArrayList<>();
        
        String sql = "SELECT e.NOM_ENTREPRISE, e.SIRET, NVL(SUM(f.MONTANT_TTC), 0) AS TOTAL " +
                     "FROM FACTURE f " +
                     "JOIN ENTREPRISE e ON f.SIRET = e.SIRET " +
                     "WHERE f.ID_BATIMENT = ? " +
                     "AND EXTRACT(YEAR FROM f.DATE_EMISSION) = ? " +
                     "AND (f.TRAVAUX IS NOT NULL OR f.DEDUCTIBLE_IMPOT = 1) " +
                     "GROUP BY e.NOM_ENTREPRISE, e.SIRET " +
                     "ORDER BY TOTAL DESC";
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idBatiment);
            ps.setInt(2, annee);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(new Object[] {
                        rs.getString("NOM_ENTREPRISE"),
                        rs.getLong("SIRET"),
                        rs.getDouble("TOTAL")
                    });
                }
            }
        }
        
        return liste;
    }
}