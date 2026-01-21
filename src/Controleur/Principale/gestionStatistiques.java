package Controleur.Principale;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import modele.dao.UtOracleDataSource;
import vue.Principale.pageStatistiques;

/**
 * Controleur pour la page Statistiques / Extractions
 * 
 */
public class gestionStatistiques implements ActionListener {

    private pageStatistiques vue;
    private Connection cn;

    public gestionStatistiques(pageStatistiques vue) {
        this.vue = vue;
    }
    
    private void ouvrirConnexion() throws SQLException {
        cn = UtOracleDataSource.getConnection();
        if (cn == null || cn.isClosed()) {
            UtOracleDataSource.creerAcces();
            cn = UtOracleDataSource.getConnection();
        }
    }
    
    private void fermerConnexion() {
        try {
            if (cn != null && !cn.isClosed()) {
                cn.close();
            }
        } catch (SQLException e) {
            // Ignorer
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "GENERER":
                genererStatistiques();
                break;
                
            case "FERMER":
                vue.fermer();
                break;
        }
    }

    /**
     * Genere les statistiques pour l'annee selectionnee
     */
    private void genererStatistiques() {
        String anneeStr = vue.getTxtAnnee().getText().trim();
        
        int annee;
        try {
            annee = Integer.parseInt(anneeStr);
            if (annee < 2000 || annee > 2100) {
                JOptionPane.showMessageDialog(vue, 
                    "Veuillez entrer une annee valide (2000-2100)",
                    "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vue, 
                "Veuillez entrer une annee valide",
                "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            ouvrirConnexion();
            
            // Vider les tableaux
            DefaultTableModel mBien = (DefaultTableModel) vue.getTableBien().getModel();
            DefaultTableModel mBat = (DefaultTableModel) vue.getTableBatiment().getModel();
            DefaultTableModel mTravauxBien = (DefaultTableModel) vue.getTableTravauxBien().getModel();
            DefaultTableModel mTravauxBat = (DefaultTableModel) vue.getTableTravauxBatiment().getModel();
            
            mBien.setRowCount(0);
            mBat.setRowCount(0);
            mTravauxBien.setRowCount(0);
            mTravauxBat.setRowCount(0);
            
            // Remplir les tableaux
            remplirTableauBiensLouables(mBien, annee);
            remplirTableauTravauxBiens(mTravauxBien, annee);
            remplirTableauBatiments(mBat, annee);
            remplirTableauTravauxBatiments(mTravauxBat, annee);
            
            fermerConnexion();
            
            JOptionPane.showMessageDialog(vue, 
                "Statistiques generees pour l'annee " + annee,
                "Succes", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(vue, 
                "Erreur lors de la generation des statistiques : " + ex.getMessage(),
                "Erreur BDD", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Remplit le tableau des biens louables avec loyers et provisions
     */
    private void remplirTableauBiensLouables(DefaultTableModel model, int annee) throws SQLException {
        // Pattern pour l'annee : "01/2026" ou "01/26"
        String patternAnnee4 = "%/" + annee;           // %/2026
        String patternAnnee2 = "%/" + (annee % 100);   // %/26
        
        //  LOGEMENTS
        String sqlLogements = 
            "SELECT " +
            "    'Logement: ' || lg.ADRESSE_LOGEMENT AS bien, " +
            "    lg.ID_LOGEMENT, " +
            "    lg.ID_BAIL " +
            "FROM LOGEMENT lg " +
            "ORDER BY lg.ADRESSE_LOGEMENT";
        
        try (Statement st = cn.createStatement();
             ResultSet rsLog = st.executeQuery(sqlLogements)) {
            
            while (rsLog.next()) {
                String bien = rsLog.getString("bien");
                long idLogement = rsLog.getLong("ID_LOGEMENT");
                Long idBail = rsLog.getLong("ID_BAIL");
                if (rsLog.wasNull()) idBail = null;
                
                double totalLoyers = 0;
                double totalProvisions = 0;
                
                // Chercher les loyers via le bail actuel
                if (idBail != null && idBail > 0) {
                    double[] loyersProv = getLoyersProvisions(idBail, patternAnnee4, patternAnnee2);
                    totalLoyers = loyersProv[0];
                    totalProvisions = loyersProv[1];
                }
                
                // Si pas de loyers sur le bail actuel, chercher sur tous les baux lies au logement
                if (totalLoyers == 0) {
                    double[] loyersProv = getLoyersProvisionsParLogement(idLogement, patternAnnee4, patternAnnee2);
                    totalLoyers = loyersProv[0];
                    totalProvisions = loyersProv[1];
                }
                
                model.addRow(new Object[] {
                    bien,
                    String.format("%.2f", totalLoyers),
                    String.format("%.2f", totalProvisions)
                });
            }
        }
        
        //  GARAGES INDEPENDANTS (sans logement associe)
        String sqlGarages = 
            "SELECT " +
            "    'Garage: ' || g.ADRESSE_GARAGE AS bien, " +
            "    g.ID_GARAGE, " +
            "    g.ID_BAIL " +
            "FROM GARAGE g " +
            "WHERE g.ID_LOGEMENT IS NULL " +
            "ORDER BY g.ADRESSE_GARAGE";
        
        try (Statement st = cn.createStatement();
             ResultSet rsGar = st.executeQuery(sqlGarages)) {
            
            while (rsGar.next()) {
                String bien = rsGar.getString("bien");
                Long idBail = rsGar.getLong("ID_BAIL");
                if (rsGar.wasNull()) idBail = null;
                
                double totalLoyers = 0;
                double totalProvisions = 0;
                
                if (idBail != null && idBail > 0) {
                    double[] loyersProv = getLoyersProvisions(idBail, patternAnnee4, patternAnnee2);
                    totalLoyers = loyersProv[0];
                    totalProvisions = loyersProv[1];
                }
                
                model.addRow(new Object[] {
                    bien,
                    String.format("%.2f", totalLoyers),
                    String.format("%.2f", totalProvisions)
                });
            }
        }
        
        // LOYERS ORPHELINS (baux avec loyers payes mais sans logement associe) 
        String sqlOrphelins = 
            "SELECT " +
            "    'Bail ' || b.ID_BAIL || ' (sans logement)' AS bien, " +
            "    NVL(SUM(CASE WHEN UPPER(lo.STATUT) = 'PAYE' THEN lo.MONTANT_LOYER ELSE 0 END), 0) AS total_loyers, " +
            "    NVL(SUM(CASE WHEN UPPER(lo.STATUT) = 'PAYE' THEN NVL(lo.MONTANT_PROVISION, 0) ELSE 0 END), 0) AS total_provisions " +
            "FROM BAIL b " +
            "JOIN LOYER lo ON lo.ID_BAIL = b.ID_BAIL " +
            "WHERE NOT EXISTS (SELECT 1 FROM LOGEMENT lg WHERE lg.ID_BAIL = b.ID_BAIL) " +
            "  AND NOT EXISTS (SELECT 1 FROM GARAGE g WHERE g.ID_BAIL = b.ID_BAIL) " +
            "  AND (TRIM(lo.MOIS) LIKE ? OR TRIM(lo.MOIS) LIKE ?) " +
            "  AND UPPER(lo.STATUT) = 'PAYE' " +
            "GROUP BY b.ID_BAIL " +
            "HAVING SUM(CASE WHEN UPPER(lo.STATUT) = 'PAYE' THEN lo.MONTANT_LOYER ELSE 0 END) > 0";
        
        try (PreparedStatement ps = cn.prepareStatement(sqlOrphelins)) {
            ps.setString(1, patternAnnee4);
            ps.setString(2, patternAnnee2);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[] {
                        rs.getString("bien"),
                        String.format("%.2f", rs.getDouble("total_loyers")),
                        String.format("%.2f", rs.getDouble("total_provisions"))
                    });
                }
            }
        }
    }
    
    /**
     * Recupere les loyers et provisions pour un bail et une annee
     */
    private double[] getLoyersProvisions(long idBail, String patternAnnee4, String patternAnnee2) throws SQLException {
        String sql = 
            "SELECT " +
            "    NVL(SUM(CASE WHEN UPPER(STATUT) = 'PAYE' THEN MONTANT_LOYER ELSE 0 END), 0) AS total_loyers, " +
            "    NVL(SUM(CASE WHEN UPPER(STATUT) = 'PAYE' THEN NVL(MONTANT_PROVISION, 0) ELSE 0 END), 0) AS total_provisions " +
            "FROM LOYER " +
            "WHERE ID_BAIL = ? " +
            "  AND (TRIM(MOIS) LIKE ? OR TRIM(MOIS) LIKE ?)";
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idBail);
            ps.setString(2, patternAnnee4);
            ps.setString(3, patternAnnee2);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new double[] {
                        rs.getDouble("total_loyers"),
                        rs.getDouble("total_provisions")
                    };
                }
            }
        }
        return new double[] {0, 0};
    }
    
    /**
     * Recupere les loyers et provisions pour un logement via tous ses baux (historique)
     */
    private double[] getLoyersProvisionsParLogement(long idLogement, String patternAnnee4, String patternAnnee2) throws SQLException {
        // Chercher via la table SIGNE et les baux associes au locataire du logement
        // Ou via les anciens baux du logement
        String sql = 
            "SELECT " +
            "    NVL(SUM(CASE WHEN UPPER(lo.STATUT) = 'PAYE' THEN lo.MONTANT_LOYER ELSE 0 END), 0) AS total_loyers, " +
            "    NVL(SUM(CASE WHEN UPPER(lo.STATUT) = 'PAYE' THEN NVL(lo.MONTANT_PROVISION, 0) ELSE 0 END), 0) AS total_provisions " +
            "FROM LOYER lo " +
            "WHERE lo.ID_BAIL IN (" +
            "    SELECT b.ID_BAIL FROM BAIL b " +
            "    WHERE b.ID_BAIL IN (" +
            "        SELECT lg2.ID_BAIL FROM LOGEMENT lg2 WHERE lg2.ID_LOGEMENT = ? AND lg2.ID_BAIL IS NOT NULL" +
            "    )" +
            ") " +
            "AND (TRIM(lo.MOIS) LIKE ? OR TRIM(lo.MOIS) LIKE ?)";
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idLogement);
            ps.setString(2, patternAnnee4);
            ps.setString(3, patternAnnee2);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new double[] {
                        rs.getDouble("total_loyers"),
                        rs.getDouble("total_provisions")
                    };
                }
            }
        }
        return new double[] {0, 0};
    }
    
    /**
     * Remplit le tableau des travaux par entreprise pour les biens louables
     */
    private void remplirTableauTravauxBiens(DefaultTableModel model, int annee) throws SQLException {
        // Travaux sur les logements (via ID_LOGEMENT ou via ID_BATIMENT du logement)
        String sql = 
            "SELECT " +
            "    lg.ADRESSE_LOGEMENT AS bien, " +
            "    e.NOM_ENTREPRISE AS entreprise, " +
            "    TO_CHAR(e.SIRET) AS siret, " +
            "    SUM(f.MONTANT_TTC) AS total_travaux " +
            "FROM FACTURE f " +
            "JOIN ENTREPRISE e ON f.SIRET = e.SIRET " +
            "JOIN LOGEMENT lg ON (f.ID_LOGEMENT = lg.ID_LOGEMENT OR f.ID_BATIMENT = lg.ID_BATIMENT) " +
            "WHERE EXTRACT(YEAR FROM f.DATE_EMISSION) = ? " +
            "GROUP BY lg.ADRESSE_LOGEMENT, e.NOM_ENTREPRISE, e.SIRET " +
            "HAVING SUM(f.MONTANT_TTC) > 0 " +
            "ORDER BY lg.ADRESSE_LOGEMENT, e.NOM_ENTREPRISE";
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, annee);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[] {
                        rs.getString("bien"),
                        rs.getString("entreprise"),
                        rs.getString("siret"),
                        String.format("%.2f", rs.getDouble("total_travaux"))
                    });
                }
            }
        }
    }
    
    /**
     * Remplit le tableau des batiments avec assurances, taxes foncieres et TEOM
     */
    private void remplirTableauBatiments(DefaultTableModel model, int annee) throws SQLException {
        String sqlBatiments = "SELECT ID_BATIMENT, ADRESSE FROM BATIMENT ORDER BY ADRESSE";
        
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sqlBatiments)) {
            
            while (rs.next()) {
                long idBat = rs.getLong("ID_BATIMENT");
                String adresse = rs.getString("ADRESSE");
                
                double totalAssurances = getTotalAssurances(idBat, annee);
                double taxesFoncieres = getTaxesFoncieres(idBat, annee);
                double teom = getTEOM(idBat, annee);
                
                model.addRow(new Object[] {
                    adresse,
                    String.format("%.2f", totalAssurances),
                    String.format("%.2f", taxesFoncieres),
                    String.format("%.2f", teom),
                    String.format("%.2f", taxesFoncieres-teom)
                });
            }
        }
    }
    
    /**
     * Remplit le tableau des travaux communs par entreprise pour les batiments
     */
    private void remplirTableauTravauxBatiments(DefaultTableModel model, int annee) throws SQLException {
        String sql = 
            "SELECT " +
            "    b.ADRESSE AS batiment, " +
            "    e.NOM_ENTREPRISE AS entreprise, " +
            "    TO_CHAR(e.SIRET) AS siret, " +
            "    SUM(f.MONTANT_TTC) AS total_travaux " +
            "FROM FACTURE f " +
            "JOIN ENTREPRISE e ON f.SIRET = e.SIRET " +
            "JOIN BATIMENT b ON f.ID_BATIMENT = b.ID_BATIMENT " +
            "WHERE EXTRACT(YEAR FROM f.DATE_EMISSION) = ? " +
            "GROUP BY b.ADRESSE, e.NOM_ENTREPRISE, e.SIRET " +
            "HAVING SUM(f.MONTANT_TTC) > 0 " +
            "ORDER BY b.ADRESSE, e.NOM_ENTREPRISE";
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, annee);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[] {
                        rs.getString("batiment"),
                        rs.getString("entreprise"),
                        rs.getString("siret"),
                        String.format("%.2f", rs.getDouble("total_travaux"))
                    });
                }
            }
        }
    }
    
    /**
     * Total des assurances pour un batiment
     * Cherche : assurances directement sur le batiment + assurances des logements du batiment
     */
    private double getTotalAssurances(long idBatiment, int annee) throws SQLException {
        double total = 0;
        
        // 1. Assurances directement sur le batiment
        String sql1 = 
            "SELECT NVL(SUM(PRIMEBASE), 0) AS total " +
            "FROM ASSURANCE " +
            "WHERE ID_BATIMENT = ? " +
            "  AND EXTRACT(YEAR FROM DATE_EFFET) = ?";
        
        try (PreparedStatement ps = cn.prepareStatement(sql1)) {
            ps.setLong(1, idBatiment);
            ps.setInt(2, annee);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total += rs.getDouble("total");
                }
            }
        }
        
        // 2. Assurances sur les logements du batiment
        String sql2 = 
            "SELECT NVL(SUM(a.PRIMEBASE), 0) AS total " +
            "FROM ASSURANCE a " +
            "JOIN LOGEMENT l ON a.ID_LOGEMENT = l.ID_LOGEMENT " +
            "WHERE l.ID_BATIMENT = ? " +
            "  AND EXTRACT(YEAR FROM a.DATE_EFFET) = ?";
        
        try (PreparedStatement ps = cn.prepareStatement(sql2)) {
            ps.setLong(1, idBatiment);
            ps.setInt(2, annee);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total += rs.getDouble("total");
                }
            }
        }
        
        // 3. Assurances sans ID_BATIMENT ni ID_LOGEMENT (globales)
        // On les compte pour le premier batiment seulement (eviter doublons)
        String sql3 = 
            "SELECT NVL(SUM(PRIMEBASE), 0) AS total " +
            "FROM ASSURANCE " +
            "WHERE ID_BATIMENT IS NULL " +
            "  AND ID_LOGEMENT IS NULL " +
            "  AND EXTRACT(YEAR FROM DATE_EFFET) = ? " +
            "  AND ? = (SELECT MIN(ID_BATIMENT) FROM BATIMENT)";
        
        try (PreparedStatement ps = cn.prepareStatement(sql3)) {
            ps.setInt(1, annee);
            ps.setLong(2, idBatiment);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total += rs.getDouble("total");
                }
            }
        }
        
        return total;
    }
    
    /**
     * Taxes foncieres pour un batiment
     * Cherche dans CHARGES avec TYPE_CHARGES contenant 'TAXE' ou 'FONCIER'
     */
    private double getTaxesFoncieres(long idBatiment, int annee) throws SQLException {
        String sql = 
            "SELECT NVL(SUM(MONTANT), 0) AS total " +
            "FROM CHARGES " +
            "WHERE ID_BATIMENT = ? " +
            "  AND (UPPER(TYPE_CHARGES) LIKE '%TAXE%' OR UPPER(TYPE_CHARGES) LIKE '%FONCIER%' " +
            "       OR UPPER(NATURE) LIKE '%TAXE%' OR UPPER(NATURE) LIKE '%FONCIER%') " +
            "  AND EXTRACT(YEAR FROM DATE_CHARGE) = ?";
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idBatiment);
            ps.setInt(2, annee);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0;
    }
    
    /**
     * TEOM pour un batiment
     * Cherche dans CHARGES (type TEOM, OM, ORDURE) + FACTURE.MONTANT_TEOM
     */
    private double getTEOM(long idBatiment, int annee) throws SQLException {
        double total = 0;
        
        // TEOM dans CHARGES - cherche TEOM, OM (Ordures Menageres), ORDURE
        String sql1 = 
            "SELECT NVL(SUM(MONTANT), 0) AS total " +
            "FROM CHARGES " +
            "WHERE ID_BATIMENT = ? " +
            "  AND (UPPER(TYPE_CHARGES) IN ('TEOM', 'OM') " +
            "       OR UPPER(TYPE_CHARGES) LIKE '%TEOM%' " +
            "       OR UPPER(TYPE_CHARGES) LIKE '%ORDURE%' " +
            "       OR UPPER(NATURE) LIKE '%TEOM%' " +
            "       OR UPPER(NATURE) LIKE '%ORDURE%') " +
            "  AND EXTRACT(YEAR FROM DATE_CHARGE) = ?";
        
        try (PreparedStatement ps = cn.prepareStatement(sql1)) {
            ps.setLong(1, idBatiment);
            ps.setInt(2, annee);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total += rs.getDouble("total");
                }
            }
        }
        
        // TEOM dans les factures
        String sql2 = 
            "SELECT NVL(SUM(MONTANT_TEOM), 0) AS total " +
            "FROM FACTURE " +
            "WHERE ID_BATIMENT = ? " +
            "  AND MONTANT_TEOM IS NOT NULL " +
            "  AND EXTRACT(YEAR FROM DATE_EMISSION) = ?";
        
        try (PreparedStatement ps = cn.prepareStatement(sql2)) {
            ps.setLong(1, idBatiment);
            ps.setInt(2, annee);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total += rs.getDouble("total");
                }
            }
        }
        
        return total;
    }
}