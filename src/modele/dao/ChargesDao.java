package modele.dao;

import java.sql.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modele.dao.Requete.RequeteCharges;
import modele.Charges;

/**
 * DAO pour la gestion des charges.
 */
public class ChargesDao {

    private Connection cn;

    public static final String TYPE_EAU = "EAU";
    public static final String TYPE_OM = "OM";
    public static final String TYPE_ELEC = "ELEC";
    public static final String TYPE_ENTRETIEN = "ENTRETIEN";

    public ChargesDao() throws SQLException {
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


    // PARTIE 1 : LOGIQUE "REGULARISATION" (PL/SQL & ANNUEL)


    public double getMontantByType(long idBatiment, int annee, String typeCharge) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteCharges.reqGetChargesBatimentByType())) {
            RequeteCharges.paramGetChargesBatimentByType(cs, idBatiment, annee, typeCharge);
            cs.execute();
            return cs.getDouble(1);
        } catch (SQLException e) {
            return getMontantByTypeSQL(idBatiment, annee, typeCharge);
        }
    }

    private double getMontantByTypeSQL(long idBatiment, int annee, String typeCharge) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(RequeteCharges.reqGetMontantByType())) {
            RequeteCharges.paramGetMontantByType(ps, idBatiment, annee, typeCharge);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("TOTAL");
        }
        return 0;
    }

    /**
     *  Recupere le montant BRUT d'une charge par type
     * (sans appliquer le pourcentage recuperable)
     */
    public double getMontantBrutByType(long idBatiment, int annee, String typeCharge) throws SQLException {
        String sql = "SELECT NVL(SUM(MONTANT), 0) AS TOTAL " +
                     "FROM CHARGES " +
                     "WHERE ID_BATIMENT = ? " +
                     "AND EXTRACT(YEAR FROM DATE_CHARGE) = ? " +
                     "AND UPPER(TYPE_CHARGES) = UPPER(?)";
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idBatiment);
            ps.setInt(2, annee);
            ps.setString(3, typeCharge);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("TOTAL");
                }
            }
        }
        return 0;
    }

    public double getPctRecuperableByType(long idBatiment, int annee, String typeCharge) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(RequeteCharges.reqGetPctRecuperableByType())) {
            RequeteCharges.paramGetPctRecuperableByType(ps, idBatiment, annee, typeCharge);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("PCT");
            }
        }
        return 100.0;
    }

    public void createOrUpdate(long idBatiment, int annee, String typeCharge, 
                               double montant, double pctRecuperable) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteCharges.reqSaisirChargeImmeuble())) {
            RequeteCharges.paramSaisirChargeImmeuble(cs, idBatiment, annee, typeCharge, montant, pctRecuperable);
            cs.execute();
        } catch (SQLException e) {
            createOrUpdateSQL(idBatiment, annee, typeCharge, montant, pctRecuperable);
        }
    }

    private void createOrUpdateSQL(long idBatiment, int annee, String typeCharge, 
                                    double montant, double pctRecuperable) throws SQLException {
        Long idExistant = findChargeId(idBatiment, annee, typeCharge);
        if (idExistant != null) {
            updateSQL(idExistant, montant, pctRecuperable);
        } else {
            createSQL(idBatiment, annee, typeCharge, montant, pctRecuperable);
        }
    }

    public void saisirToutesCharges(long idBatiment, int annee,
                                     double montantEau, double montantOM,
                                     double montantElec, double montantEntretien) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteCharges.reqSaisirToutesChargesImmeuble())) {
            RequeteCharges.paramSaisirToutesChargesImmeuble(cs, idBatiment, annee, 
                    montantEau, montantOM, montantElec, montantEntretien);
            cs.execute();
        } catch (SQLException e) {
            createOrUpdate(idBatiment, annee, TYPE_EAU, montantEau, 100);
            createOrUpdate(idBatiment, annee, TYPE_OM, montantOM, 100);
            createOrUpdate(idBatiment, annee, TYPE_ELEC, montantElec, 100);
            createOrUpdate(idBatiment, annee, TYPE_ENTRETIEN, montantEntretien, 100);
        }
    }
    
    public Map<String, Double> getAllMontantsByBatimentAndAnnee(long idBatiment, int annee) throws SQLException {
        Map<String, Double> montants = new HashMap<>();
        montants.put(TYPE_EAU, getMontantByType(idBatiment, annee, TYPE_EAU));
        montants.put(TYPE_OM, getMontantByType(idBatiment, annee, TYPE_OM));
        montants.put(TYPE_ELEC, getMontantByType(idBatiment, annee, TYPE_ELEC));
        montants.put(TYPE_ENTRETIEN, getMontantByType(idBatiment, annee, TYPE_ENTRETIEN));
        return montants;
    }
    
    public double getTotalCharges(long idBatiment, int annee) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteCharges.reqGetTotalChargesBatiment())) {
            RequeteCharges.paramGetTotalChargesBatiment(cs, idBatiment, annee);
            cs.execute();
            return cs.getDouble(1);
        } catch (SQLException e) {
            return getMontantByType(idBatiment, annee, TYPE_EAU) +
                   getMontantByType(idBatiment, annee, TYPE_OM) +
                   getMontantByType(idBatiment, annee, TYPE_ELEC) +
                   getMontantByType(idBatiment, annee, TYPE_ENTRETIEN);
        }
    }
    
    public void deleteByType(long idBatiment, int annee, String typeCharge) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(RequeteCharges.reqDeleteByType())) {
            RequeteCharges.paramDeleteByType(ps, idBatiment, annee, typeCharge);
            ps.executeUpdate();
        }
    }


    // PARTIE 2 : LOGIQUE "DOCUMENTS" (PRECISION DATE & LISTES)


    public List<Charges> findAllByBatiment(long idBatiment) throws SQLException {
        List<Charges> list = new ArrayList<>();
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteCharges.reqFindAllByBatiment())) {
            RequeteCharges.paramFindAllByBatiment(ps, idBatiment);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCharges(rs));
                }
            }
        }
        return list;
    }

    public Charges findById(long idCharge) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(RequeteCharges.reqFindById())) {
            RequeteCharges.paramFindById(ps, idCharge);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCharges(rs);
                }
            }
        }
        return null;
    }

    public void create(Charges c) throws SQLException {
        long newId = genererIdCharge();
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteCharges.reqCreate())) {
            RequeteCharges.paramCreate(ps, newId, c);
            ps.executeUpdate();
        }
    }

    public void updateCharge(long idCharge, java.util.Date date, String nature, 
                            String type, double montant, double pctRecuperable) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(RequeteCharges.reqUpdateCharge())) {
            RequeteCharges.paramUpdateCharge(ps, idCharge, date, nature, type, montant, pctRecuperable);
            ps.executeUpdate();
        }
    }

    public void update(Charges c) throws SQLException {
        double montant = c.getMontant() != null ? c.getMontant() : 0.0;
        updateCharge(c.getIdChargeLong(), c.getDateCharge(), c.getNature(), 
                    c.getTypeCharges(), montant, c.getPctRecuperable());
    }

    public void delete(long idCharge) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(RequeteCharges.reqDelete())) {
            RequeteCharges.paramDelete(ps, idCharge);
            ps.executeUpdate();
        }
    }


    // UTILITAIRES PRIVES


    private Long findChargeId(long idBatiment, int annee, String typeCharge) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(RequeteCharges.reqFindChargeId())) {
            RequeteCharges.paramFindChargeId(ps, idBatiment, annee, typeCharge);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getLong("ID_CHARGE");
        }
        return null;
    }

    private void createSQL(long idBatiment, int annee, String typeCharge, 
                           double montant, double pctRecuperable) throws SQLException {
        long newId = genererIdCharge();
        String nature = getNatureFromType(typeCharge);
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteCharges.reqCreateSQL())) {
            RequeteCharges.paramCreateSQL(ps, newId, idBatiment, annee, typeCharge, nature, montant, pctRecuperable);
            ps.executeUpdate();
        }
    }

    private void updateSQL(long idCharge, double montant, double pctRecuperable) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(RequeteCharges.reqUpdateSQL())) {
            RequeteCharges.paramUpdateSQL(ps, idCharge, montant, pctRecuperable);
            ps.executeUpdate();
        }
    }

    private long genererIdCharge() throws SQLException {
        try (Statement st = cn.createStatement(); 
             ResultSet rs = st.executeQuery(RequeteCharges.reqGenererIdCharge())) {
            if (rs.next()) return rs.getLong("NEW_ID");
        }
        return 1;
    }

    private String getNatureFromType(String type) {
        switch (type.toUpperCase()) {
            case "EAU": return "Facture eau";
            case "OM": return "Ordures menageres";
            case "ELEC": return "Electricite parties communes";
            case "ENTRETIEN": return "Entretien immeuble";
            case "TAXE_FONCIERE": return "Taxe fonciere";
            default: return type;
        }
    }

    private Charges mapCharges(ResultSet rs) throws SQLException {
        return new Charges(
            rs.getLong("ID_CHARGE"),
            rs.getString("NATURE"),
            rs.getDouble("MONTANT"),
            rs.getDate("DATE_CHARGE"),
            rs.getString("TYPE_CHARGES"),
            rs.getDouble("PCT_RECUPERABLE"),
            rs.getLong("ID_BATIMENT")
        );
    }
}