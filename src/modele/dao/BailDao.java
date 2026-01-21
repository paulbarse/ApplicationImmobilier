package modele.dao;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import modele.Bail;
import modele.dao.Requete.RequeteBail;

/**
 * DAO pour la table BAIL 
 */
public class BailDao implements dao<Bail>, AutoCloseable {

    private Connection cn;

    @Override
    public void setConnection(Connection cn) {
        this.cn = cn;
    }

    public BailDao() {
        try {
            this.cn = UtOracleDataSource.getConnection();
            if (this.cn == null || this.cn.isClosed()) {
                UtOracleDataSource.creerAcces();
                this.cn = UtOracleDataSource.getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create(Bail bail) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteBail.reqCreate())) {
            RequeteBail.paramCreate(st, bail);
            st.executeUpdate();
        }
    }

    @Override
    public void update(Bail bail) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteBail.reqUpdate())) {
            RequeteBail.paramUpdate(st, bail);
            st.executeUpdate();
        }
    }

    @Override
    public void delete(Bail bail) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteBail.reqDelete())) {
            RequeteBail.paramDelete(st, bail);
            st.executeUpdate();
        }
    }

    @Override
    public Bail findById(String... id) throws SQLException {
        if (id.length < 1) throw new SQLException("ID manquant");
        
        try (PreparedStatement st = cn.prepareStatement(RequeteBail.reqFindById())) {
            RequeteBail.paramFindById(st, id[0]);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return mapBail(rs);
            }
        }
        return null;
    }

    @Override
    public List<Bail> findAll() throws SQLException {
        List<Bail> list = new ArrayList<>();
        
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteBail.reqFindAll())) {
            while (rs.next()) list.add(mapBail(rs));
        }
        return list;
    }

    public List<Bail> findActifs() throws SQLException {
        List<Bail> list = new ArrayList<>();
        
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteBail.reqFindActifs())) {
            while (rs.next()) list.add(mapBail(rs));
        }
        return list;
    }

    /**
     * Met a  jour le loyer d'un bail
     */
    public void updateLoyer(long idBail, double nouveauLoyer) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteBail.reqUpdateLoyer())) {
            RequeteBail.paramUpdateLoyer(st, idBail, nouveauLoyer);
            st.executeUpdate();
        }
    }

    /**
     * Met a  jour les provisions d'un bail
     */
    public void updateProvisions(long idBail, double nouvelleProvision) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteBail.reqUpdateProvisions())) {
            RequeteBail.paramUpdateProvisions(st, idBail, nouvelleProvision);
            st.executeUpdate();
        }
    }

    /**
     * Recupere les baux pour affichage dans une vue
     */
    public List<Object[]> findAllForView() {
        List<Object[]> liste = new ArrayList<>();

        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(RequeteBail.reqFindAllForView())) {

            while (rs.next()) {
                String id = rs.getString("ID_BAIL");
                String adresse = rs.getString("ADRESSE");
                String typeBien = rs.getString("TYPE_BIEN");
                String locataires = rs.getString("LOCATAIRES");
                String etat = rs.getString("ETAT");

                java.sql.Date dateDebut = rs.getDate("DATE_DEBUT");
                java.sql.Date dateFin = rs.getDate("DATE_FIN");
                double loyer = rs.getDouble("LOYER_INITIAL");
                double caution = rs.getDouble("CAUTION");

                liste.add(new Object[] { id, adresse, typeBien, locataires,
                        dateDebut, dateFin, loyer, caution, etat });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }


    // PROCEDURES STOCKEES PL/SQL


    /**
     * Cree un bail complet avec logement et locataire
     */
    public void creerBailComplet(long idBail, Date dateDebut, double loyerInitial,
                                 double provisions, Double caution, Integer jourPaiement,
                                 long idLogement, long idLocataire) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteBail.reqCreerBailComplet())) {
            RequeteBail.paramCreerBailComplet(cs, idBail, dateDebut, loyerInitial, 
                    provisions, caution, jourPaiement, idLogement, idLocataire);
            cs.execute();
        }
    }

    /**
     * Cree un bail pour un garage seul
     */
    public void creerBailGarage(long idBail, Date dateDebut, double loyerInitial,
                                double provisions, Double caution, Integer jourPaiement,
                                long idGarage, long idLocataire) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteBail.reqCreerBailGarage())) {
            RequeteBail.paramCreerBailGarage(cs, idBail, dateDebut, loyerInitial, 
                    provisions, caution, jourPaiement, idGarage, idLocataire);
            cs.execute();
        }
    }

    /**
     * Ajoute un locataire supplementaire a  un bail existant
     */
    public void ajouterLocataireAuBail(long idBail, long idLocataire) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteBail.reqAjouterLocataireAuBail())) {
            RequeteBail.paramAjouterLocataireAuBail(cs, idBail, idLocataire);
            cs.execute();
        }
    }

    /**
     * Lie un garage a  un bail existant
     */
    public void lierGarageAuBail(long idBail, long idGarage) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteBail.reqLierGarageAuBail())) {
            RequeteBail.paramLierGarageAuBail(cs, idBail, idGarage);
            cs.execute();
        }
    }

    /**
     * Resilie un bail
     */
    public void resilierBail(long idBail, Date dateFin) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteBail.reqResilierBail())) {
            RequeteBail.paramResilierBail(cs, idBail, dateFin);
            cs.execute();
        }
    }

    /**
     * Cloture un bail avec solde de tout compte
     */
    public void cloturerBail(long idBail, java.sql.Date dateFin, double montantSolde) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteBail.reqCloturerBail())) {
            RequeteBail.paramCloturerBail(cs, idBail, dateFin, montantSolde);
            cs.execute();
        }
    }
    

    public void cloturerBail(long idBail, Date dateFin, double montantSolde) throws SQLException {
        cloturerBail(idBail, new java.sql.Date(dateFin.getTime()), montantSolde);
    }

    /**
     * Genere les 12 loyers d'une annee pour un bail
     */
    public void genererLoyersAnnuels(long idBail, int annee) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteBail.reqGenererLoyersAnnuels())) {
            RequeteBail.paramGenererLoyersAnnuels(cs, idBail, annee);
            cs.execute();
        }
    }

    /**
     * Applique une revalorisation de loyer selon l'IRL
     */
    public void revaloriserLoyerProcedure(long idBail, double nouveauLoyer, int nouvelIRL, int ancienIRL) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteBail.reqRevaloriserLoyer())) {
            RequeteBail.paramRevaloriserLoyer(cs, idBail, nouveauLoyer, nouvelIRL, ancienIRL);
            cs.execute();
        }
    }

    /**
     * Calcule le solde total d'un bail
     */
    public double calculerSoldeBail(long idBail) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteBail.reqCalculerSoldeBail())) {
            RequeteBail.paramCalculerSoldeBail(cs, idBail);
            cs.execute();
            return cs.getDouble(1);
        }
    }

    /**
     * Verifie si peut etre revalorise (12 mois minimum)
     */
    public boolean peutRevaloriser(long idBail) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteBail.reqPeutRevaloriser())) {
            RequeteBail.paramPeutRevaloriser(cs, idBail);
            cs.execute();
            return cs.getInt(1) == 1;
        }
    }
    
    /**
	 * Recup details
     */
    public double[] getDetailsSolde(long idBail) throws SQLException {
        double[] resultats = new double[3];
        
        try (CallableStatement cs = cn.prepareCall(RequeteBail.reqGetDetailsSolde())) {
            RequeteBail.paramGetDetailsSolde(cs, idBail);
            cs.execute();
            
            resultats[0] = cs.getDouble(2);
            resultats[1] = cs.getDouble(3);
            resultats[2] = cs.getDouble(4);
        }
        return resultats;
    }

    //  MAPPING 

    private Bail mapBail(ResultSet rs) throws SQLException {
        Double soldeToutCompte = rs.getDouble("SOLDE_TOUT_COMPTE");
        if (rs.wasNull()) soldeToutCompte = null;

        Double caution = rs.getDouble("CAUTION");
        if (rs.wasNull()) caution = null;

        Integer jourPaiement = rs.getInt("JOUR_PAIEMENT");
        if (rs.wasNull()) jourPaiement = null;

        return new Bail(
            rs.getLong("ID_BAIL"),
            rs.getDate("DATE_DEBUT"),
            rs.getDate("DATE_FIN"),
            rs.getDouble("LOYER_INITIAL"),
            rs.getDouble("PROVISION_INITIALES"),
            soldeToutCompte,
            caution,
            rs.getString("ETAT"),
            jourPaiement
        );
    }
    
    public String getMessageErreurRevalorisation(long idBail) throws SQLException {
        String sql = "{? = call getMessageErreurRevalorisation(?)}";
        try (CallableStatement cs = cn.prepareCall(sql)) {
            cs.registerOutParameter(1, java.sql.Types.VARCHAR);
            cs.setLong(2, idBail);
            cs.execute();
            return cs.getString(1);
        } catch (SQLException e) {
            // Si la fonction n'existe pas, retourner null
            return null;
        }
    }

    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) {
            cn.close();
        }
    }
}
