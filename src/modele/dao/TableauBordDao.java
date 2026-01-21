package modele.dao;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

import modele.dao.Requete.RequeteTableauBord;

/**
 * DAO pour les statistiques du Tableau de Bord
 */
public class TableauBordDao {

    private Connection cn;

    public TableauBordDao() throws SQLException {
        cn = UtOracleDataSource.getConnection();
        if (cn == null || cn.isClosed()) {
            UtOracleDataSource.creerAcces();
            cn = UtOracleDataSource.getConnection();
        }
    }


    // CLASSES INTERNES

    
    public static class StatistiquesTableauBord {
        private int nbProprietes;
        private String variationProprietes;
        private int nbLocatairesActifs;
        private String variationLocataires;
        private int nbContratsEnCours;
        private String variationContrats;
        private double revenuMensuel;
        private String variationRevenu;

        public int getNbProprietes() { return nbProprietes; }
        public void setNbProprietes(int nbProprietes) { this.nbProprietes = nbProprietes; }
        public String getVariationProprietes() { return variationProprietes; }
        public void setVariationProprietes(String variationProprietes) { this.variationProprietes = variationProprietes; }
        public int getNbLocatairesActifs() { return nbLocatairesActifs; }
        public void setNbLocatairesActifs(int nbLocatairesActifs) { this.nbLocatairesActifs = nbLocatairesActifs; }
        public String getVariationLocataires() { return variationLocataires; }
        public void setVariationLocataires(String variationLocataires) { this.variationLocataires = variationLocataires; }
        public int getNbContratsEnCours() { return nbContratsEnCours; }
        public void setNbContratsEnCours(int nbContratsEnCours) { this.nbContratsEnCours = nbContratsEnCours; }
        public String getVariationContrats() { return variationContrats; }
        public void setVariationContrats(String variationContrats) { this.variationContrats = variationContrats; }
        public double getRevenuMensuel() { return revenuMensuel; }
        public void setRevenuMensuel(double revenuMensuel) { this.revenuMensuel = revenuMensuel; }
        public String getVariationRevenu() { return variationRevenu; }
        public void setVariationRevenu(String variationRevenu) { this.variationRevenu = variationRevenu; }
    }

    public static class ProprieteRecente {
        private String typeBien;
        private long idBien;
        private String adresse;
        private String sousType;
        private String statut;
        private Double loyer;
        private String locataire;
        private String adresseBatiment;

        public String getTypeBien() { return typeBien; }
        public void setTypeBien(String typeBien) { this.typeBien = typeBien; }
        public long getIdBien() { return idBien; }
        public void setIdBien(long idBien) { this.idBien = idBien; }
        public String getAdresse() { return adresse; }
        public void setAdresse(String adresse) { this.adresse = adresse; }
        public String getSousType() { return sousType; }
        public void setSousType(String sousType) { this.sousType = sousType; }
        public String getStatut() { return statut; }
        public void setStatut(String statut) { this.statut = statut; }
        public Double getLoyer() { return loyer; }
        public void setLoyer(Double loyer) { this.loyer = loyer; }
        public String getLocataire() { return locataire; }
        public void setLocataire(String locataire) { this.locataire = locataire; }
        public String getAdresseBatiment() { return adresseBatiment; }
        public void setAdresseBatiment(String adresseBatiment) { this.adresseBatiment = adresseBatiment; }
        public boolean isOccupe() { return "Occupe".equals(statut); }
    }

    public static class ActiviteRecente {
        private long idActivite;
        private String typeActivite;
        private String description;
        private Timestamp dateActivite;
        private String entiteType;
        private Long entiteId;
        private Double montant;
        private String adresse;
        private String tempsEcoule;

        public long getIdActivite() { return idActivite; }
        public void setIdActivite(long idActivite) { this.idActivite = idActivite; }
        public String getTypeActivite() { return typeActivite; }
        public void setTypeActivite(String typeActivite) { this.typeActivite = typeActivite; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Timestamp getDateActivite() { return dateActivite; }
        public void setDateActivite(Timestamp dateActivite) { this.dateActivite = dateActivite; }
        public String getEntiteType() { return entiteType; }
        public void setEntiteType(String entiteType) { this.entiteType = entiteType; }
        public Long getEntiteId() { return entiteId; }
        public void setEntiteId(Long entiteId) { this.entiteId = entiteId; }
        public Double getMontant() { return montant; }
        public void setMontant(Double montant) { this.montant = montant; }
        public String getAdresse() { return adresse; }
        public void setAdresse(String adresse) { this.adresse = adresse; }
        public String getTempsEcoule() { return tempsEcoule; }
        public void setTempsEcoule(String tempsEcoule) { this.tempsEcoule = tempsEcoule; }
        
        public String getIconName() {
            if (typeActivite == null) return "activiteRecente.png";
            switch (typeActivite) {
                case "NOUVEAU_LOCATAIRE": return "nouveauLocataire.png";
                case "PAIEMENT_RECU": return "paimentRecu.png";
                case "CONTRAT_SIGNE":
                case "CONTRAT_RESILIE":
                case "CONTRAT_CLOTURE": return "logoContratEnCour.png";
                case "NOUVEAU_LOGEMENT":
                case "NOUVEAU_GARAGE":
                case "NOUVEAU_BATIMENT": return "logoPropriete.png";
                case "NOUVELLE_FACTURE":
                case "FACTURE_PAYEE": return "paimentRecu.png";
                default: return "activiteRecente.png";
            }
        }
    }


    // MÉTHODES PRINCIPALES


    public StatistiquesTableauBord getStatistiques() throws SQLException {
        StatistiquesTableauBord stats = new StatistiquesTableauBord();
        
        stats.setNbProprietes(getNbProprietes());
        stats.setVariationProprietes("Total");
        stats.setNbLocatairesActifs(getNbLocatairesActifs());
        stats.setVariationLocataires(getVariationLocataires());
        stats.setNbContratsEnCours(getNbContratsEnCours());
        stats.setVariationContrats(getVariationContrats());
        stats.setRevenuMensuel(getRevenuMensuel());
        stats.setVariationRevenu(getVariationRevenuTexte());
        
        return stats;
    }

    public int getNbProprietes() throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteTableauBord.reqGetNbProprietes())) {
            RequeteTableauBord.paramGetNbProprietes(cs);
            cs.execute();
            return cs.getInt(1);
        }
    }

    public int getNbLocatairesActifs() throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteTableauBord.reqGetNbLocatairesActifs())) {
            RequeteTableauBord.paramGetNbLocatairesActifs(cs);
            cs.execute();
            return cs.getInt(1);
        }
    }

    private String getVariationLocataires() throws SQLException {
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteTableauBord.reqGetVariationLocataires())) {
            if (rs.next()) {
                int nb = rs.getInt("nb");
                if (nb > 0) return "+" + nb + " cette semaine";
            }
        }
        return "Stable";
    }

    public int getNbContratsEnCours() throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteTableauBord.reqGetNbContratsEnCours())) {
            RequeteTableauBord.paramGetNbContratsEnCours(cs);
            cs.execute();
            return cs.getInt(1);
        }
    }

    private String getVariationContrats() throws SQLException {
        int nouveaux = 0, termines = 0;
        
        try (Statement st = cn.createStatement()) {
            ResultSet rs = st.executeQuery(RequeteTableauBord.reqGetNouveauxContratsCeMois());
            if (rs.next()) nouveaux = rs.getInt("nb");
            rs.close();
            
            rs = st.executeQuery(RequeteTableauBord.reqGetContratsTerminesCeMois());
            if (rs.next()) termines = rs.getInt("nb");
            rs.close();
        }
        
        int diff = nouveaux - termines;
        if (diff > 0) return "+" + diff + " ce mois";
        if (diff < 0) return diff + " ce mois";
        return "Stable";
    }

    public double getRevenuMensuel() throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteTableauBord.reqGetRevenuMensuel())) {
            RequeteTableauBord.paramGetRevenuMensuel(cs);
            cs.execute();
            return cs.getDouble(1);
        }
    }

    private double getRevenuMoisPrecedent() throws SQLException {
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteTableauBord.reqGetRevenuMoisPrecedent())) {
            if (rs.next()) return rs.getDouble("total");
        }
        return 0;
    }

    private String getVariationRevenuTexte() throws SQLException {
        double revenuActuel = getRevenuMensuel();
        double revenuPrecedent = getRevenuMoisPrecedent();
        
        if (revenuPrecedent == 0) {
            if (revenuActuel > 0) return "+100% vs mois dernier";
            return "Pas de données";
        }
        
        double variation = ((revenuActuel - revenuPrecedent) / revenuPrecedent) * 100;
        variation = Math.round(variation * 10) / 10.0;
        
        if (variation > 0) return "+" + variation + "% vs mois dernier";
        if (variation < 0) return variation + "% vs mois dernier";
        return "Stable vs mois dernier";
    }

    public List<ProprieteRecente> getToutesProprietes(int limit) throws SQLException {
        List<ProprieteRecente> liste = new ArrayList<>();
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteTableauBord.reqGetDerniersLogements())) {
            RequeteTableauBord.paramGetDerniersLogements(ps, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(mapPropriete(rs));
            }
        }
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteTableauBord.reqGetDerniersGarages())) {
            RequeteTableauBord.paramGetDerniersGarages(ps, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(mapPropriete(rs));
            }
        }
        
        liste.sort((a, b) -> Long.compare(b.getIdBien(), a.getIdBien()));
        if (liste.size() > limit) {
            liste = new ArrayList<>(liste.subList(0, limit));
        }
        return liste;
    }
    
    private ProprieteRecente mapPropriete(ResultSet rs) throws SQLException {
        ProprieteRecente prop = new ProprieteRecente();
        prop.setTypeBien(rs.getString("type_bien"));
        prop.setIdBien(rs.getLong("id_bien"));
        prop.setAdresse(rs.getString("adresse"));
        prop.setSousType(rs.getString("sous_type"));
        prop.setStatut(rs.getString("statut"));
        double loyer = rs.getDouble("loyer");
        prop.setLoyer(rs.wasNull() ? null : loyer);
        prop.setLocataire(rs.getString("locataire"));
        return prop;
    }

    public List<ActiviteRecente> getActivitesRecentes(int limit) throws SQLException {
        List<ActiviteRecente> liste = new ArrayList<>();
        
        if (!tableExists("HISTORIQUE_ACTIVITE")) return liste;
        
        try (PreparedStatement ps = cn.prepareStatement(RequeteTableauBord.reqGetActivitesRecentes())) {
            RequeteTableauBord.paramGetActivitesRecentes(ps, limit);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ActiviteRecente act = new ActiviteRecente();
                act.setIdActivite(rs.getLong("id_activite"));
                act.setTypeActivite(rs.getString("type_activite"));
                act.setDescription(rs.getString("description_activite"));
                act.setDateActivite(rs.getTimestamp("date_activite"));
                act.setEntiteType(rs.getString("entite_type"));
                Long entiteId = rs.getLong("entite_id");
                act.setEntiteId(rs.wasNull() ? null : entiteId);
                Double montant = rs.getDouble("montant");
                act.setMontant(rs.wasNull() ? null : montant);
                act.setAdresse(rs.getString("adresse"));
                act.setTempsEcoule(rs.getString("temps_ecoule"));
                liste.add(act);
            }
        }
        return liste;
    }

    private boolean tableExists(String tableName) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(RequeteTableauBord.reqTableExists())) {
            RequeteTableauBord.paramTableExists(ps, tableName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("nb") > 0;
        }
        return false;
    }

    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) cn.close();
    }
}
