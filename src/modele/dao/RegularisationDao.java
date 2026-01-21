package modele.dao;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import modele.dao.Requete.RequeteRegularisation;

/**
 * DAO pour la regularisation des charges
 */
public class RegularisationDao {

    private Connection cn;


    // CONSTRUCTEUR

    
    public RegularisationDao() throws SQLException {
        cn = UtOracleDataSource.getConnection();
        if (cn == null || cn.isClosed()) {
            UtOracleDataSource.creerAcces();
            cn = UtOracleDataSource.getConnection();
        }
    }


    // CLASSES INTERNES (DTOs)

    
    /**
     * Recapitulatif d'un locataire pour le tableau de regularisation
     */
    public static class RecapLocataire {
        private long idBail;
        private long idLocataire;
        private String nomLocataire;
        private String adresseLogement;
        private double totalLoyers;
        private double totalProvisions;
        private double regularisation;
        private double total;
        private int nbMoisOccupation;
        private double chargesEau;
        private double chargesOM;
        private double chargesElec;
        private double chargesEntretien;
        private double totalCharges;
        private double nouvelleProvision;
        private long idBatiment;

        // Getters et Setters
        public long getIdBail() { return idBail; }
        public void setIdBail(long idBail) { this.idBail = idBail; }
        
        public long getIdLocataire() { return idLocataire; }
        public void setIdLocataire(long idLocataire) { this.idLocataire = idLocataire; }
        
        public String getNomLocataire() { return nomLocataire; }
        public void setNomLocataire(String nomLocataire) { this.nomLocataire = nomLocataire; }
        
        public String getAdresseLogement() { return adresseLogement; }
        public void setAdresseLogement(String adresseLogement) { this.adresseLogement = adresseLogement; }
        
        public double getTotalLoyers() { return totalLoyers; }
        public void setTotalLoyers(double totalLoyers) { this.totalLoyers = totalLoyers; }
        
        public double getTotalProvisions() { return totalProvisions; }
        public void setTotalProvisions(double totalProvisions) { this.totalProvisions = totalProvisions; }
        
        public double getRegularisation() { return regularisation; }
        public void setRegularisation(double regularisation) { this.regularisation = regularisation; }
        
        public double getTotal() { return total; }
        public void setTotal(double total) { this.total = total; }
        
        public int getNbMoisOccupation() { return nbMoisOccupation; }
        public void setNbMoisOccupation(int nbMoisOccupation) { this.nbMoisOccupation = nbMoisOccupation; }
        
        public double getChargesEau() { return chargesEau; }
        public void setChargesEau(double chargesEau) { this.chargesEau = chargesEau; }
        
        public double getChargesOM() { return chargesOM; }
        public void setChargesOM(double chargesOM) { this.chargesOM = chargesOM; }
        
        public double getChargesElec() { return chargesElec; }
        public void setChargesElec(double chargesElec) { this.chargesElec = chargesElec; }
        
        public double getChargesEntretien() { return chargesEntretien; }
        public void setChargesEntretien(double chargesEntretien) { this.chargesEntretien = chargesEntretien; }
        
        public double getTotalCharges() { return totalCharges; }
        public void setTotalCharges(double totalCharges) { this.totalCharges = totalCharges; }
        
        public double getNouvelleProvision() { return nouvelleProvision; }
        public void setNouvelleProvision(double nouvelleProvision) { this.nouvelleProvision = nouvelleProvision; }
        
        public long getIdBatiment() { return idBatiment; }
        public void setIdBatiment(long idBatiment) { this.idBatiment = idBatiment; }
        

        public boolean estPositif() { return regularisation > 0; }
        

        public boolean estNegatif() { return regularisation < 0; }
    }
    
    /**
     * Recapitulatif des charges d'un batiment
     */
    public static class RecapBatiment {
        private long idBatiment;
        private String adresse;
        private double totalFactureEau;
        private double totalOM;
        private double totalElec;
        private double totalEntretien;
        private double totalCharges;
        private int annee;

        public long getIdBatiment() { return idBatiment; }
        public void setIdBatiment(long idBatiment) { this.idBatiment = idBatiment; }
        
        public String getAdresse() { return adresse; }
        public void setAdresse(String adresse) { this.adresse = adresse; }
        
        public double getTotalFactureEau() { return totalFactureEau; }
        public void setTotalFactureEau(double totalFactureEau) { this.totalFactureEau = totalFactureEau; }
        
        public double getTotalOM() { return totalOM; }
        public void setTotalOM(double totalOM) { this.totalOM = totalOM; }
        
        public double getTotalElec() { return totalElec; }
        public void setTotalElec(double totalElec) { this.totalElec = totalElec; }
        
        public double getTotalEntretien() { return totalEntretien; }
        public void setTotalEntretien(double totalEntretien) { this.totalEntretien = totalEntretien; }
        
        public double getTotalCharges() { return totalCharges; }
        public void setTotalCharges(double totalCharges) { this.totalCharges = totalCharges; }
        
        public int getAnnee() { return annee; }
        public void setAnnee(int annee) { this.annee = annee; }
    }


    // METHODES PRINCIPALES 

    

    public List<RecapLocataire> getRecapTousLocataires(int annee) throws SQLException {
        List<RecapLocataire> liste = new ArrayList<>();
        
        String sql = RequeteRegularisation.reqFindBauxPourRegularisation();
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
        	RequeteRegularisation.paramFindBauxPourRegularisation(ps, annee);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                long idBail = rs.getLong("ID_BAIL");
                long idLocataire = rs.getLong("ID_LOCATAIRE");
                long idBatiment = rs.getLong("ID_BATIMENT");
                String nomComplet = rs.getString("NOM_COMPLET");
                String adresseBat = rs.getString("ADRESSE_BAT");
                

                RecapLocataire recap = appelProcedureRecapRegularisation(idBail, annee);
                
                if (recap != null) {
                    recap.setIdLocataire(idLocataire);
                    recap.setNomLocataire(nomComplet);
                    recap.setIdBatiment(idBatiment);
                    recap.setAdresseLogement(adresseBat != null ? adresseBat : "-");
                    liste.add(recap);
                }
            }
        }
        
        return liste;
    }
    
    /**
     * Appelle la procedure PL/SQL getRecapRegularisation.
     */
    private RecapLocataire appelProcedureRecapRegularisation(long idBail, int annee) throws SQLException {
        RecapLocataire recap = new RecapLocataire();
        recap.setIdBail(idBail);
        

        String sql = "{call getRecapRegularisation(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        
        try (CallableStatement cs = cn.prepareCall(sql)) {

            cs.setLong(1, idBail);
            cs.setInt(2, annee);
            

            cs.registerOutParameter(3, Types.VARCHAR);   // p_nom_locataire
            cs.registerOutParameter(4, Types.NUMERIC);   // p_total_loyers
            cs.registerOutParameter(5, Types.NUMERIC);   // p_total_provisions
            cs.registerOutParameter(6, Types.NUMERIC);   // p_regularisation
            cs.registerOutParameter(7, Types.NUMERIC);   // p_total
            cs.registerOutParameter(8, Types.NUMERIC);   // p_nb_mois
            cs.registerOutParameter(9, Types.NUMERIC);   // p_charges_eau
            cs.registerOutParameter(10, Types.NUMERIC);  // p_charges_om
            cs.registerOutParameter(11, Types.NUMERIC);  // p_charges_elec
            cs.registerOutParameter(12, Types.NUMERIC);  // p_charges_entretien
            cs.registerOutParameter(13, Types.NUMERIC);  // p_total_charges
            cs.registerOutParameter(14, Types.NUMERIC);  // p_nouvelle_provision
            
            cs.execute();
            

            recap.setNomLocataire(cs.getString(3));
            recap.setTotalLoyers(cs.getDouble(4));
            recap.setTotalProvisions(cs.getDouble(5));
            recap.setRegularisation(cs.getDouble(6));
            recap.setTotal(cs.getDouble(7));
            recap.setNbMoisOccupation(cs.getInt(8));
            recap.setChargesEau(cs.getDouble(9));
            recap.setChargesOM(cs.getDouble(10));
            recap.setChargesElec(cs.getDouble(11));
            recap.setChargesEntretien(cs.getDouble(12));
            recap.setTotalCharges(cs.getDouble(13));
            recap.setNouvelleProvision(cs.getDouble(14));
            
        } catch (SQLException e) {

            System.err.println("Procedure getRecapRegularisation non disponible, " +
                             "utilisation du fallback SQL: " + e.getMessage());
            return getRecapLocataireFallback(idBail, annee);
        }
        
        return recap;
    }
    

    public RecapLocataire getRecapLocataire(long idBail, int annee) throws SQLException {
        long idBatiment = getIdBatiment(idBail);
        RecapLocataire recap = appelProcedureRecapRegularisation(idBail, annee);
        recap.setIdBatiment(idBatiment);
        return recap;
    }
    

    public RecapBatiment getRecapBatiment(long idBatiment, int annee) throws SQLException {
        RecapBatiment recap = new RecapBatiment();
        recap.setIdBatiment(idBatiment);
        recap.setAnnee(annee);
        

        String sql = "{call getRecapChargesBatiment(?, ?, ?, ?, ?, ?, ?, ?)}";
        
        try (CallableStatement cs = cn.prepareCall(sql)) {

            cs.setLong(1, idBatiment);
            cs.setInt(2, annee);
            

            cs.registerOutParameter(3, Types.VARCHAR);   // p_adresse
            cs.registerOutParameter(4, Types.NUMERIC);   // p_charges_eau
            cs.registerOutParameter(5, Types.NUMERIC);   // p_charges_om
            cs.registerOutParameter(6, Types.NUMERIC);   // p_charges_elec
            cs.registerOutParameter(7, Types.NUMERIC);   // p_charges_entretien
            cs.registerOutParameter(8, Types.NUMERIC);   // p_total_charges
            
            cs.execute();
            

            recap.setAdresse(cs.getString(3));
            recap.setTotalFactureEau(cs.getDouble(4));
            recap.setTotalOM(cs.getDouble(5));
            recap.setTotalElec(cs.getDouble(6));
            recap.setTotalEntretien(cs.getDouble(7));
            recap.setTotalCharges(cs.getDouble(8));
            
        } catch (SQLException e) {

            System.err.println("Procedure getRecapChargesBatiment non disponible, " +
                             "utilisation du fallback SQL: " + e.getMessage());
            return getRecapBatimentFallback(idBatiment, annee);
        }
        
        return recap;
    }
    

    public void saisirChargesImmeuble(long idBatiment, int annee, 
                                       double montantEau, double montantOM,
                                       double montantElec, double montantEntretien) 
            throws SQLException {
        
        String sql = "{call saisirToutesChargesImmeuble(?, ?, ?, ?, ?, ?)}";
        
        try (CallableStatement cs = cn.prepareCall(sql)) {
            cs.setLong(1, idBatiment);
            cs.setInt(2, annee);
            cs.setDouble(3, montantEau);
            cs.setDouble(4, montantOM);
            cs.setDouble(5, montantElec);
            cs.setDouble(6, montantEntretien);
            
            cs.execute();
        }
    }


    // METHODES FALLBACK


    private RecapLocataire getRecapLocataireFallback(long idBail, int annee) throws SQLException {
        RecapLocataire recap = new RecapLocataire();
        recap.setIdBail(idBail);
        
        // Appel des fonctions PL/SQL individuelles
        recap.setNbMoisOccupation(appelFonctionNbMoisOccupation(idBail, annee));
        recap.setTotalLoyers(appelFonctionTotalLoyers(idBail, annee));
        recap.setTotalProvisions(appelFonctionTotalProvisions(idBail, annee));
        
        long idBatiment = getIdBatiment(idBail);
        recap.setIdBatiment(idBatiment);
        
        if (idBatiment > 0 && recap.getNbMoisOccupation() > 0) {
            recap.setChargesEau(appelFonctionChargesLocataire(idBail, idBatiment, annee, "EAU"));
            recap.setChargesOM(appelFonctionChargesLocataire(idBail, idBatiment, annee, "OM"));
            recap.setChargesElec(appelFonctionChargesLocataire(idBail, idBatiment, annee, "ELEC"));
            recap.setChargesEntretien(appelFonctionChargesLocataire(idBail, idBatiment, annee, "ENTRETIEN"));
        }
        
        recap.setTotalCharges(recap.getChargesEau() + recap.getChargesOM() + 
                              recap.getChargesElec() + recap.getChargesEntretien());
        recap.setRegularisation(recap.getTotalCharges() - recap.getTotalProvisions());
        recap.setTotal(recap.getTotalLoyers() + recap.getRegularisation());
        
        if (recap.getNbMoisOccupation() > 0) {
            recap.setNouvelleProvision(recap.getTotalCharges() / recap.getNbMoisOccupation());
        }
        
        return recap;
    }
    

    private RecapBatiment getRecapBatimentFallback(long idBatiment, int annee) throws SQLException {
        RecapBatiment recap = new RecapBatiment();
        recap.setIdBatiment(idBatiment);
        recap.setAnnee(annee);
        
        // Adresse
        String sqlAdresse = RequeteRegularisation.reqFindAdresseBatiment();
        try (PreparedStatement ps = cn.prepareStatement(sqlAdresse)) {
            ps.setLong(1, idBatiment);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                recap.setAdresse(rs.getString("ADRESSE"));
            }
        }
        
        // Charges par type via fonctions PL/SQL
        recap.setTotalFactureEau(appelFonctionChargesBatiment(idBatiment, annee, "EAU"));
        recap.setTotalOM(appelFonctionChargesBatiment(idBatiment, annee, "OM"));
        recap.setTotalElec(appelFonctionChargesBatiment(idBatiment, annee, "ELEC"));
        recap.setTotalEntretien(appelFonctionChargesBatiment(idBatiment, annee, "ENTRETIEN"));
        
        recap.setTotalCharges(recap.getTotalFactureEau() + recap.getTotalOM() + 
                              recap.getTotalElec() + recap.getTotalEntretien());
        
        return recap;
    }


    // APPELS DES FONCTIONS PL/SQL INDIVIDUELLES

    
    /**
     * Appelle la fonction PL/SQL getNbMoisOccupation
     */
    private int appelFonctionNbMoisOccupation(long idBail, int annee) throws SQLException {
        String sql = "{? = call getNbMoisOccupation(?, ?)}";
        try (CallableStatement cs = cn.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setLong(2, idBail);
            cs.setInt(3, annee);
            cs.execute();
            return cs.getInt(1);
        } catch (SQLException e) {
            // Fallback SQL simple
            return getNbMoisOccupationSQL(idBail, annee);
        }
    }
    
    /**
     * Appelle la fonction PL/SQL getTotalLoyersBail
     */
    private double appelFonctionTotalLoyers(long idBail, int annee) throws SQLException {
        String sql = "{? = call getTotalLoyersBail(?, ?)}";
        try (CallableStatement cs = cn.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setLong(2, idBail);
            cs.setInt(3, annee);
            cs.execute();
            return cs.getDouble(1);
        } catch (SQLException e) {
            // Fallback SQL simple
            return getTotalLoyersSQL(idBail, annee);
        }
    }
    
    /**
     * Appelle la fonction PL/SQL getTotalProvisionsBailAvecFallback
     */
    private double appelFonctionTotalProvisions(long idBail, int annee) throws SQLException {
        String sql = "{? = call getTotalProvisionsBailAvecFallback(?, ?)}";
        try (CallableStatement cs = cn.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setLong(2, idBail);
            cs.setInt(3, annee);
            cs.execute();
            return cs.getDouble(1);
        } catch (SQLException e) {
            // Fallback SQL simple
            return getTotalProvisionsSQL(idBail, annee);
        }
    }
    
    /**
     * Appelle la fonction PL/SQL getChargesLocataire
     */
    private double appelFonctionChargesLocataire(long idBail, long idBatiment, 
                                                  int annee, String type) throws SQLException {
        String sql = "{? = call getChargesLocataire(?, ?, ?, ?)}";
        try (CallableStatement cs = cn.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setLong(2, idBail);
            cs.setLong(3, idBatiment);
            cs.setInt(4, annee);
            cs.setString(5, type);
            cs.execute();
            return cs.getDouble(1);
        } catch (SQLException e) {
            // Fallback SQL simple
            return getChargesBatimentSQL(idBatiment, annee, type);
        }
    }
    
    /**
     * Appelle la fonction PL/SQL getChargesBatimentByType
     */
    private double appelFonctionChargesBatiment(long idBatiment, int annee, 
                                                 String type) throws SQLException {
        String sql = "{? = call getChargesBatimentByType(?, ?, ?)}";
        try (CallableStatement cs = cn.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setLong(2, idBatiment);
            cs.setInt(3, annee);
            cs.setString(4, type);
            cs.execute();
            return cs.getDouble(1);
        } catch (SQLException e) {
            // Fallback SQL simple
            return getChargesBatimentSQL(idBatiment, annee, type);
        }
    }


    

    private long getIdBatiment(long idBail) throws SQLException {
        // Chercher dans LOGEMENT
    	String sql = RequeteRegularisation.reqFindBatimentFromBailViaLogement();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idBail);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("ID_BATIMENT");
            }
        }
        
        // Sinon chercher dans GARAGE
        sql = RequeteRegularisation.reqFindBatimentFromBailViaGarage();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idBail);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("ID_BATIMENT");
            }
        }
        
        return 0;
    }
    
    /**
     * Fallback SQL : Nombre de mois d'occupation
     */
    private int getNbMoisOccupationSQL(long idBail, int annee) throws SQLException {
    	String sql = RequeteRegularisation.reqFindDatesBail();
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idBail);
            ResultSet rs = ps.executeQuery();
            
            if (!rs.next()) return 0;
            
            java.sql.Date dateDebut = rs.getDate("DATE_DEBUT");
            java.sql.Date dateFin = rs.getDate("DATE_FIN");
            

            java.util.Calendar calDebut = java.util.Calendar.getInstance();
            calDebut.setTime(dateDebut);
            
            java.util.Calendar calFin = java.util.Calendar.getInstance();
            calFin.setTime(dateFin);
            
            java.util.Calendar debutAnnee = java.util.Calendar.getInstance();
            debutAnnee.set(annee, java.util.Calendar.JANUARY, 1);
            
            java.util.Calendar finAnnee = java.util.Calendar.getInstance();
            finAnnee.set(annee, java.util.Calendar.DECEMBER, 31);
            
            if (calDebut.after(finAnnee) || calFin.before(debutAnnee)) {
                return 0;
            }
            
            int moisDebut = calDebut.before(debutAnnee) ? 1 : calDebut.get(java.util.Calendar.MONTH) + 1;
            int moisFin = calFin.after(finAnnee) ? 12 : calFin.get(java.util.Calendar.MONTH) + 1;
            
            return Math.max(moisFin - moisDebut + 1, 0);
        }
    }
    

    private double getTotalLoyersSQL(long idBail, int annee) throws SQLException {
    	String sql = RequeteRegularisation.reqTotalLoyersBail();
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
        	RequeteRegularisation.paramTotalLoyersBail(ps, idBail, annee);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("TOTAL");
            }
        }
        return 0;
    }
    

    private double getTotalProvisionsSQL(long idBail, int annee) throws SQLException {
    	String sql = RequeteRegularisation.reqTotalProvisionsBail();
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idBail);
            ps.setString(2, "%/" + annee);
            ps.setString(3, "%/" + (annee % 100));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double total = rs.getDouble("TOTAL");
                if (total == 0) {
                    // Fallback depuis BAIL
                    return getProvisionsFallback(idBail, getNbMoisOccupationSQL(idBail, annee));
                }
                return total;
            }
        }
        return 0;
    }
    
    /**
     * Fallback : Provisions depuis BAIL.PROVISION_INITIALES
     */
    private double getProvisionsFallback(long idBail, int nbMois) throws SQLException {
    	String sql = RequeteRegularisation.reqProvisionInitialesBail();
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idBail);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("PROV") * nbMois;
            }
        }
        return 0;
    }
    
    /**
     * Fallback SQL : Charges par type
     */
    private double getChargesBatimentSQL(long idBatiment, int annee, String type) throws SQLException {
    	String sql = RequeteRegularisation.reqChargesBatimentParType();
        
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, idBatiment);
            ps.setInt(2, annee);
            ps.setString(3, type);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("TOTAL");
            }
        }
        return 0;
    }

    // ========================================================================
    // FERMETURE
    // ========================================================================
    

    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) {
            cn.close();
        }
    }
}
