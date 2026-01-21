package modele.dao;

import java.sql.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import modele.Assurance;
import modele.dao.Requete.RequeteAssurance;

/**
 * DAO pour la table ASSURANCE 
 * Sépare les requêtes SQL et le paramétrage JDBC via RequeteAssurance.
 */
public class AssuranceDao {

    private Connection cn;

    public AssuranceDao() throws SQLException {
        cn = UtOracleDataSource.getConnection();
        if (cn == null || cn.isClosed()) {
            UtOracleDataSource.creerAcces();
            cn = UtOracleDataSource.getConnection();
        }
    }

    /**
     * Crée une nouvelle assurance
     */
    public void create(Assurance a) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteAssurance.reqCreate())) {
            RequeteAssurance.paramCreate(st, a);
            st.executeUpdate();
        }
        
        // Ajouter l'échéance si présente
        if (a.getDateEcheance() != null) {
            ajouterEcheance(a.getNumAssurance(), a.getDateEcheance(), 
                    a.getMontantEcheance() != null ? a.getMontantEcheance() : a.getPrimeBase());
        }
    }

    /**
     * Ajoute une assurance pour un bâtiment entier via procédure PL/SQL
     */
    public void ajouterAssuranceBatiment(long idBatiment, long numAssurance, 
            String nomCompagnie, String type, double primeBase, 
            Date dateEffet, Date dateEcheance) throws SQLException {
        
        try (CallableStatement cs = cn.prepareCall(RequeteAssurance.reqAjouterAssuranceBatiment())) {
            RequeteAssurance.paramAjouterAssuranceBatiment(cs, idBatiment, numAssurance, 
                    nomCompagnie, type, primeBase, dateEffet, dateEcheance);
            cs.execute();
        }
    }

    /**
     * Ajoute une assurance pour un logement
     */
    public void ajouterAssuranceLogement(long idLogement, long numAssurance,
            String nomCompagnie, String type, double primeBase,
            Date dateEffet, Date dateEcheance) throws SQLException {
        
        Assurance a = Assurance.assuranceLogement(numAssurance, idLogement, 
                nomCompagnie, type, primeBase, dateEffet);
        a.setDateEcheance(dateEcheance);
        create(a);
    }

    /**
     * Ajoute une assurance pour un garage
     */
    public void ajouterAssuranceGarage(long idGarage, long numAssurance,
            String nomCompagnie, String type, double primeBase,
            Date dateEffet, Date dateEcheance) throws SQLException {
        
        Assurance a = Assurance.assuranceGarage(numAssurance, idGarage,
                nomCompagnie, type, primeBase, dateEffet);
        a.setDateEcheance(dateEcheance);
        create(a);
    }

    /**
     * Met à jour une assurance
     */
    public void update(Assurance a) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteAssurance.reqUpdate())) {
            RequeteAssurance.paramUpdate(st, a);
            st.executeUpdate();
        }
    }

    /**
     * Modifie une assurance (méthode simplifiée)
     */
    public void modifierAssurance(long numAssurance, String nomCompagnie, String type,
            double primeBase, Date dateEffet, Date dateEcheance) throws SQLException {
        
        // 1. Mettre à jour l'assurance
        try (PreparedStatement st = cn.prepareStatement(RequeteAssurance.reqModifierAssurance())) {
            RequeteAssurance.paramModifierAssurance(st, numAssurance, nomCompagnie, type, primeBase, dateEffet);
            st.executeUpdate();
        }
        
        // 2. Mettre à jour l'échéance
        supprimerEcheances(numAssurance);
        if (dateEcheance != null) {
            ajouterEcheance(numAssurance, dateEcheance, primeBase);
        }
    }

    /**
     * Supprime une assurance via procédure PL/SQL
     */
    public void delete(long numAssurance) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteAssurance.reqDelete())) {
            RequeteAssurance.paramDelete(cs, numAssurance);
            cs.execute();
        }
    }

    /**
     * Alias pour delete 
     */
    public void supprimerAssurance(long numAssurance) throws SQLException {
        delete(numAssurance);
    }

    /**
     * Recherche une assurance par ID
     */
    public Assurance findById(long numAssurance) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteAssurance.reqFindById())) {
            RequeteAssurance.paramFindById(st, numAssurance);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapAssurance(rs);
                }
            }
        }
        return null;
    }

    /**
     * Recherche toutes les assurances
     */
    public List<Assurance> findAll() throws SQLException {
        List<Assurance> list = new ArrayList<>();
        
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteAssurance.reqFindAll())) {
            while (rs.next()) {
                list.add(mapAssurance(rs));
            }
        }
        return list;
    }

    /**
     * Recherche les assurances par logement
     */
    public List<Assurance> findByLogement(long idLogement) throws SQLException {
        List<Assurance> list = new ArrayList<>();
        
        try (PreparedStatement st = cn.prepareStatement(RequeteAssurance.reqFindByLogement())) {
            RequeteAssurance.paramFindByLogement(st, idLogement);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAssurance(rs));
                }
            }
        }
        return list;
    }

    /**
     * Recherche les assurances par garage
     */
    public List<Assurance> findByGarage(long idGarage) throws SQLException {
        List<Assurance> list = new ArrayList<>();
        
        try (PreparedStatement st = cn.prepareStatement(RequeteAssurance.reqFindByGarage())) {
            RequeteAssurance.paramFindByGarage(st, idGarage);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAssurance(rs));
                }
            }
        }
        return list;
    }

    /**
     * Recherche les assurances par bâtiment
     */
    public List<Assurance> findByBatiment(long idBatiment) throws SQLException {
        List<Assurance> list = new ArrayList<>();
        
        try (PreparedStatement st = cn.prepareStatement(RequeteAssurance.reqFindByBatiment())) {
            RequeteAssurance.paramFindByBatiment(st, idBatiment);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAssurance(rs));
                }
            }
        }
        return list;
    }

    /**
     * Recherche toutes les assurances d'un bâtiment (incluant logements et garages)
     */
    public List<Assurance> findAllByBatiment(long idBatiment) throws SQLException {
        List<Assurance> list = new ArrayList<>();
        
        try (PreparedStatement st = cn.prepareStatement(RequeteAssurance.reqFindAllByBatiment())) {
            RequeteAssurance.paramFindAllByBatiment(st, idBatiment);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAssurance(rs));
                }
            }
        }
        return list;
    }

    /**
     * Calcule le total des assurances pour un bâtiment et une année
     */
    public double getTotalAssurancesBatiment(long idBatiment, int annee) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteAssurance.reqGetTotalAssurancesBatiment())) {
            RequeteAssurance.paramGetTotalAssurancesBatiment(st, idBatiment, annee);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0;
    }

    /**
     * Recherche les assurances expirant bientôt (dans les N jours)
     */
    public List<Assurance> findExpirantBientot(int jours) throws SQLException {
        List<Assurance> list = new ArrayList<>();
        
        try (PreparedStatement st = cn.prepareStatement(RequeteAssurance.reqFindExpirantBientot())) {
            RequeteAssurance.paramFindExpirantBientot(st, jours);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAssurance(rs));
                }
            }
        }
        return list;
    }

    /**
     * Génère le prochain ID d'assurance
     */
    public long getNextId() throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteAssurance.reqGetNextId())) {
            RequeteAssurance.paramGetNextId(cs);
            cs.execute();
            return cs.getLong(1);
        }
    }

    // GESTION DES ÉCHÉANCES 

    private void ajouterEcheance(long numAssurance, Date dateEcheance, Double montant) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteAssurance.reqAjouterEcheance())) {
            RequeteAssurance.paramAjouterEcheance(st, numAssurance, dateEcheance, montant);
            st.executeUpdate();
        }
    }

    private void supprimerEcheances(long numAssurance) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteAssurance.reqSupprimerEcheances())) {
            RequeteAssurance.paramSupprimerEcheances(st, numAssurance);
            st.executeUpdate();
        }
    }

    // MAPPING 

    private Assurance mapAssurance(ResultSet rs) throws SQLException {
        Long idLogement = rs.getLong("ID_LOGEMENT");
        if (rs.wasNull()) idLogement = null;
        
        Long idGarage = rs.getLong("ID_GARAGE");
        if (rs.wasNull()) idGarage = null;
        
        Long idBatiment = rs.getLong("ID_BATIMENT");
        if (rs.wasNull()) idBatiment = null;
        
        Long siret = rs.getLong("SIRET");
        if (rs.wasNull()) siret = null;
        
        Double primeBase = rs.getDouble("PRIMEBASE");
        if (rs.wasNull()) primeBase = null;

        Assurance a = new Assurance(
            rs.getLong("NUMASSURANCE"),
            rs.getString("TYPE"),
            primeBase,
            rs.getString("NOM_COMPAGNIE"),
            rs.getDate("DATE_EFFET"),
            idLogement,
            idGarage,
            idBatiment,
            siret
        );

        // Données d'échéance si présentes
        try {
            Date dateEcheance = rs.getDate("DATE_ECHEANCE");
            if (!rs.wasNull()) {
                a.setDateEcheance(dateEcheance);
            }
            
            Double montantEcheance = rs.getDouble("MONTANT_ECHEANCE");
            if (!rs.wasNull()) {
                a.setMontantEcheance(montantEcheance);
            }
        } catch (SQLException e) {
            // Les colonnes d'échéance ne sont pas présentes - ignorer
        }

        return a;
    }

    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) {
            cn.close();
        }
    }
}
