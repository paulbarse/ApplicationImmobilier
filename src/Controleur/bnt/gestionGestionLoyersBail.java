package Controleur.bnt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import modele.Loyer;
import modele.dao.BailDao;
import modele.dao.LoyerDao;
import modele.dao.UtOracleDataSource;
import modele.dao.Requete.RequeteLoyer;
import vue.bnt.pageGestionLoyersBail;

/**
 * Controleur pour la fenetre de gestion des loyers d'un bail
 */
public class gestionGestionLoyersBail implements ActionListener, ListSelectionListener {

    private pageGestionLoyersBail vue;
    private List<Long> listeIdsLoyers = new ArrayList<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public gestionGestionLoyersBail(pageGestionLoyersBail vue) {
        this.vue = vue;
        vue.setGestion(this);
    }

    /**
     * Charge les loyers du bail pour l'annee selectionnee
     */
    public void chargerLoyers() {
        try (LoyerDao dao = new LoyerDao()) {
            List<Loyer> loyers = dao.findByBail(vue.getIdBail());

            DefaultTableModel model = vue.getModelLoyers();
            model.setRowCount(0);
            listeIdsLoyers.clear();

            int annee = vue.getAnneeSelectionnee();
            LoyerStats stats = calculerStatsLoyers(loyers, model, annee);

            afficherInfoLoyers(stats, annee);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vue,
                "Erreur lors du chargement des loyers :\n" + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Calcule les statistiques des loyers et remplit le tableau
     */
    private LoyerStats calculerStatsLoyers(List<Loyer> loyers, DefaultTableModel model, int annee) {
        String suffixeAnnee = "/" + annee;
        String suffixeAnnee2 = "/" + (annee % 100);

        int nbLoyers = 0;
        double totalLoyer = 0;
        double totalProvision = 0;

        for (Loyer l : loyers) {
            String mois = l.getMois();
            if (mois != null && (mois.endsWith(suffixeAnnee) || mois.endsWith(suffixeAnnee2))) {
                listeIdsLoyers.add(l.getIdLoyerLong());
                
                double loyer = l.getMontantLoyer();
                double provision = getProvisionValue(l);
                double total = loyer + provision;
                
                String dateStr = formatDatePaiement(l);

                model.addRow(new Object[]{
                    l.getIdLoyerLong(),
                    mois,
                    String.format("%.2f", loyer),
                    String.format("%.2f", provision),
                    String.format("%.2f", total),
                    l.getStatut() != null ? l.getStatut() : "EN_ATTENTE",
                    dateStr
                });
                
                nbLoyers++;
                if ("PAYE".equalsIgnoreCase(l.getStatut())) {
                    totalLoyer += loyer;
                    totalProvision += provision;
                }
            }
        }

        return new LoyerStats(nbLoyers, totalLoyer, totalProvision);
    }

    /**
     * Récupère la valeur de provision d'un loyer
     */
    private double getProvisionValue(Loyer l) {
        Double provObj = l.getProvision();
        return (provObj != null) ? provObj.doubleValue() : 0;
    }

    /**
     * Formate la date de paiement
     */
    private String formatDatePaiement(Loyer l) {
        return (l.getDatePaiement() != null) ? sdf.format(l.getDatePaiement()) : "";
    }

    /**
     * Affiche les informations de statistiques des loyers
     */
    private void afficherInfoLoyers(LoyerStats stats, int annee) {
        vue.setInfo(stats.nbLoyers + " loyer(s) pour " + annee + 
                   " | Total paye: " + String.format("%.2f", stats.totalLoyer) + " € loyers + " +
                   String.format("%.2f", stats.totalProvision) + " € provisions");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "GENERER_LOYERS":
                genererLoyersManquants();
                break;
            case "MARQUER_PAYE":
                marquerLoyer("PAYE");
                break;
            case "MARQUER_RETARD":
                marquerLoyer("RETARD");
                break;
            case "REMETTRE_ATTENTE":
                marquerLoyer("EN_ATTENTE");
                break;
            default:
                break;
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // Pas d'action specifique sur la selection
    }

    /**
     * Genere les loyers manquants pour l'annee selectionnee
     */
    private void genererLoyersManquants() {
        int annee = vue.getAnneeSelectionnee();
        long idBail = vue.getIdBail();

        if (!confirmerGeneration(annee, idBail)) {
            return;
        }

        try {
            boolean procedureOK = essayerProcedureStockee(idBail, annee);

            if (!procedureOK) {
                genererLoyersJava(idBail, annee);
            }

            JOptionPane.showMessageDialog(vue,
                "Loyers generes avec succes pour " + annee + " !",
                "Succes", JOptionPane.INFORMATION_MESSAGE);

            chargerLoyers();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(vue,
                "Erreur lors de la generation des loyers :\n" + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Demande confirmation pour la génération des loyers
     */
    private boolean confirmerGeneration(int annee, long idBail) {
        int reponse = JOptionPane.showConfirmDialog(vue,
            "Generer les loyers manquants pour l'annee " + annee + " ?\n\n" +
            "Bail: #" + idBail + "\n" +
            "Locataire: " + vue.getNomLocataire() + "\n" +
            "Loyer mensuel: " + String.format("%.2f", vue.getLoyerMensuel()) + " €\n" +
            "Provision mensuelle: " + String.format("%.2f", vue.getProvisionMensuelle()) + " €",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        return reponse == JOptionPane.YES_OPTION;
    }

    /**
     * Essaie d'utiliser la procédure stockée pour générer les loyers
     */
    private boolean essayerProcedureStockee(long idBail, int annee) {
        try (BailDao bailDao = new BailDao()) {
            bailDao.genererLoyersAnnuels(idBail, annee);
            return true;
        } catch (Exception ex) {
            System.err.println("Procedure genererLoyersAnnuels non disponible, fallback Java");
            return false;
        }
    }

    /**
     * Genere les loyers en Java si la procedure n'existe pas
     */
    private void genererLoyersJava(long idBail, int annee) throws SQLException {
        try (LoyerDao loyerDao = new LoyerDao()) {
            List<Loyer> existants = loyerDao.findByBail(idBail);
            List<String> moisExistants = extraireMoisExistants(existants);

            String[] moisNoms = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
            
            Connection cn = UtOracleDataSource.getConnection();
            
            for (String moisNum : moisNoms) {
                creerLoyerSiManquant(loyerDao, cn, moisNum, annee, moisExistants, idBail);
            }
        }
    }

    /**
     * Extrait la liste des mois existants
     */
    private List<String> extraireMoisExistants(List<Loyer> existants) {
        List<String> moisExistants = new ArrayList<>();
        for (Loyer l : existants) {
            if (l.getMois() != null) {
                moisExistants.add(l.getMois());
            }
        }
        return moisExistants;
    }

    /**
     * Crée un loyer si le mois n'existe pas déjà
     */
    private void creerLoyerSiManquant(LoyerDao loyerDao, Connection cn, String moisNum, 
            int annee, List<String> moisExistants, long idBail) throws SQLException {
        
        String moisComplet = moisNum + "/" + annee;
        String moisCourt = moisNum + "/" + (annee % 100);
        
        boolean existe = moisExistants.stream()
            .anyMatch(m -> m.equals(moisComplet) || m.equals(moisCourt));
        
        if (!existe) {
            long newId = genererIdLoyer(cn);
            
            Loyer nouveau = new Loyer(
                newId,
                null,
                vue.getLoyerMensuel(),
                moisComplet,
                vue.getProvisionMensuelle(),
                "EN_ATTENTE",
                null, null, null,
                idBail
            );
            
            loyerDao.create(nouveau);
            System.out.println("[gestionGestionLoyersBail] Loyer cree: " + moisComplet);
        }
    }

    /**
     * Genere un nouvel ID pour un loyer
     */
    private long genererIdLoyer(Connection cn) throws SQLException {
        String sql = RequeteLoyer.reqGenererIdLoyer();
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return System.currentTimeMillis();
    }

    /**
     * Marque le loyer selectionne avec le statut indique
     */
    private void marquerLoyer(String nouveauStatut) {
        int row = vue.getLigneSelectionnee();
        
        if (!validerSelectionLoyer(row)) {
            return;
        }

        long idLoyer = listeIdsLoyers.get(row);
        String mois = vue.getTableLoyers().getValueAt(row, 1).toString();
        String statutActuel = vue.getTableLoyers().getValueAt(row, 5).toString();

        if (statutActuel.equalsIgnoreCase(nouveauStatut)) {
            JOptionPane.showMessageDialog(vue,
                "Ce loyer est deja en statut " + nouveauStatut,
                "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            executerMiseAJourStatut(idLoyer, nouveauStatut);
            chargerLoyers();
            vue.setInfo("Loyer " + mois + " marque comme " + nouveauStatut);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(vue,
                "Erreur lors de la mise a jour du statut :\n" + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Valide la sélection d'un loyer
     */
    private boolean validerSelectionLoyer(int row) {
        if (row < 0 || row >= listeIdsLoyers.size()) {
            JOptionPane.showMessageDialog(vue,
                "Veuillez selectionner un loyer dans le tableau.",
                "Aucune selection", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Exécute la mise à jour du statut en base
     */
    private void executerMiseAJourStatut(long idLoyer, String nouveauStatut) throws SQLException {
        Connection cn = UtOracleDataSource.getConnection();
        
        if ("PAYE".equals(nouveauStatut)) {
            String sql = RequeteLoyer.reqMarquerPayeAvecSysdate();
            try (PreparedStatement st = cn.prepareStatement(sql)) {
                RequeteLoyer.paramMarquerPayeAvecSysdate(st, idLoyer);
                st.executeUpdate();
            }
        } else {
            String sql = RequeteLoyer.reqMarquerStatut();
            try (PreparedStatement st = cn.prepareStatement(sql)) {
                RequeteLoyer.paramMarquerStatut(st, nouveauStatut, idLoyer);
                st.executeUpdate();
            }
        }
    }

    /**
     * Classe interne pour les statistiques des loyers
     */
    private static class LoyerStats {
        final int nbLoyers;
        final double totalLoyer;
        final double totalProvision;

        LoyerStats(int nbLoyers, double totalLoyer, double totalProvision) {
            this.nbLoyers = nbLoyers;
            this.totalLoyer = totalLoyer;
            this.totalProvision = totalProvision;
        }
    }
}
