package modele.dao;

import java.sql.*;

import java.util.Date;

import modele.dao.Requete.RequeteAbstractDocument;

/**
 * DAO abstrait pour les documents
 */
public abstract class AbstractDocumentDao {

    protected Connection cn;

    protected AbstractDocumentDao() throws SQLException {
        cn = UtOracleDataSource.getConnection();
        if (cn == null || cn.isClosed()) {
            UtOracleDataSource.creerAcces();
            cn = UtOracleDataSource.getConnection();
        }
    }


    // MÉTHODES COMMUNES - ASSURANCES


    public void modifierAssurance(long numAssurance, String nomCompagnie, String type,
                                  double primeBase, Date dateEffet, Date dateEcheance) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteAbstractDocument.reqUpdateAssurance())) {
            RequeteAbstractDocument.paramUpdateAssurance(st, numAssurance, nomCompagnie, type, primeBase, dateEffet);
            st.executeUpdate();
        }

        try (PreparedStatement st = cn.prepareStatement(RequeteAbstractDocument.reqDeleteEcheance())) {
            RequeteAbstractDocument.paramDeleteEcheance(st, numAssurance);
            st.executeUpdate();
        }

        if (dateEcheance != null) {
            try (PreparedStatement st = cn.prepareStatement(RequeteAbstractDocument.reqInsertEcheance())) {
                RequeteAbstractDocument.paramInsertEcheance(st, numAssurance, dateEcheance, primeBase);
                st.executeUpdate();
            }
        }
    }

    public void supprimerAssurance(long numAssurance) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteAbstractDocument.reqSupprimerAssurance())) {
            RequeteAbstractDocument.paramSupprimerAssurance(cs, numAssurance);
            cs.execute();
        }
    }


    // MÉTHODES COMMUNES - LOYERS


    protected void marquerLoyerPayeInternal(long idLoyer, Date datePaiement) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteAbstractDocument.reqMarquerLoyerPaye())) {
            RequeteAbstractDocument.paramMarquerLoyerPaye(cs, idLoyer, datePaiement);
            cs.execute();
        }
    }

    protected void annulerPaiementInternal(long idLoyer) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteAbstractDocument.reqAnnulerPaiementLoyer())) {
            RequeteAbstractDocument.paramAnnulerPaiementLoyer(cs, idLoyer);
            cs.execute();
        }
    }


    // MÉTHODES COMMUNES - GARANTS


    public void modifierGarant(String idGarant, String nom, String prenom,
                               String adresse, String email, String tel) throws SQLException {
        try (PreparedStatement st = cn.prepareStatement(RequeteAbstractDocument.reqUpdateGarant())) {
            RequeteAbstractDocument.paramUpdateGarant(st, idGarant, nom, prenom, adresse, email, tel);
            st.executeUpdate();
        }
    }

    public void supprimerGarant(String idGarant) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteAbstractDocument.reqSupprimerGarant())) {
            RequeteAbstractDocument.paramSupprimerGarant(cs, idGarant);
            cs.execute();
        }
    }


    // UTILITAIRES


    protected long getNextId(String tableName, String columnName) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(RequeteAbstractDocument.reqGetNextId())) {
            RequeteAbstractDocument.paramGetNextId(cs, tableName, columnName);
            cs.execute();
            return cs.getLong(1);
        }
    }

    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) cn.close();
    }
}
