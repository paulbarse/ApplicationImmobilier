package Controleur.Saisie;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JOptionPane;

import modele.Bail;
import modele.Garage;
import modele.Locataire;
import modele.Logement;
import modele.dao.BailDao;
import modele.dao.GarageDao;
import modele.dao.LocataireDao;
import modele.dao.LogementDao;
import vue.Saisie.pageSaisieModificationBail;

/**
 * Contrôleur pour la modification d'un bail
 */
public class gestionSaisieModificationBail implements ActionListener {

    private pageSaisieModificationBail vue;
    private long idBail;
    private Bail bail;
    
    private Long idLogementAssocie = null;

    public gestionSaisieModificationBail(pageSaisieModificationBail vue, long idBail) {
        this.vue = vue;
        this.idBail = idBail;
        
        this.vue.setControleur(this);
        chargerDonnees();
    }

    private void chargerDonnees() {
        BailDao bailDao = null;
        LogementDao logDao = null;
        LocataireDao locDao = null;
        
        try {
            bailDao = new BailDao();
            this.bail = bailDao.findById(String.valueOf(idBail));
            
            if (bail == null) {
                JOptionPane.showMessageDialog(vue, "Bail introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
                vue.dispose();
                return;
            }

            // 1. Récupération Logement & Locataire pour affichage
            String txtLog = "Aucun (Bail Garage)";
            
            logDao = new LogementDao();
            Logement log = logDao.findByBail(idBail);
            if (log != null) {
                this.idLogementAssocie = log.getIdLogementLong(); 
                txtLog = (log.getAdresseLogement() != null) ? log.getAdresseLogement() : "Logement #" + log.getIdLogement();
            }

            String txtLoc = "Aucun";
            locDao = new LocataireDao();
            List<Locataire> locs = locDao.findByBail(idBail);
            if (!locs.isEmpty()) {
                txtLoc = locs.get(0).getNomComplet();
            }

            // 2. Envoi à la Vue
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            vue.setInfosBail(
                String.valueOf(bail.getIdBail()),
                bail.getDateDebut() != null ? sdf.format(bail.getDateDebut()) : "-",
                bail.getEtat(),
                txtLog,
                txtLoc
            );

            vue.setValeursModifiables(
                bail.getLoyerInitial(),
                bail.getProvisionInitiales(),
                bail.getCaution() != null ? bail.getCaution() : 0.0,
                bail.getJourPaiement() != null ? bail.getJourPaiement() : 5
            );
            
            // 3. Charger les tables
            rafraichirGarages();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vue, "Erreur chargement : " + e.getMessage());
        } finally {
            if (bailDao != null) try { bailDao.close(); } catch (SQLException ex) { /* ignore */ }
            if (logDao != null) try { logDao.close(); } catch (SQLException ex) { /* ignore */ }
            if (locDao != null) try { locDao.close(); } catch (SQLException ex) { /* ignore */ }
        }
    }

    private void rafraichirGarages() {
        GarageDao gDao = null;
        try {
            gDao = new GarageDao();
            vue.updateTableLies(gDao.findByBail(idBail));
            vue.updateTableDispos(gDao.findLibres());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (gDao != null) try { gDao.close(); } catch (SQLException ex) { /* ignore */ }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        
        switch (cmd) {
            case "AJOUTER_GARAGE":
                ajouterGarage();
                break;
            case "RETIRER_GARAGE":
                retirerGarage();
                break;
            case "VALIDER":
                enregistrerEtFermer();
                break;
            case "ANNULER":
                vue.dispose();
                break;
            default:
                break;
        }
    }

    private void ajouterGarage() {
        Long idGarage = vue.getIdGarageSelectionneDispo();
        if (idGarage == null) {
            JOptionPane.showMessageDialog(vue, "Sélectionnez un garage disponible.");
            return;
        }

        BailDao bailDao = null;
        GarageDao gDao = null;
        try {
            // 1. Liaison au Bail
            bailDao = new BailDao();
            bailDao.lierGarageAuBail(idBail, idGarage);

            // 2. Liaison au Logement (Cohérence)
            if (this.idLogementAssocie != null) {
                gDao = new GarageDao();
                Garage g = gDao.findById(String.valueOf(idGarage));
                if (g != null) {
                    g.setIdLogement(this.idLogementAssocie);
                    g.setIdBail(idBail);
                    gDao.update(g);
                }
            }

            rafraichirGarages();
            JOptionPane.showMessageDialog(vue, "Garage ajouté avec succès.");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vue, "Erreur ajout : " + ex.getMessage());
        } finally {
            if (bailDao != null) try { bailDao.close(); } catch (SQLException ex) { /* ignore */ }
            if (gDao != null) try { gDao.close(); } catch (SQLException ex) { /* ignore */ }
        }
    }

    private void retirerGarage() {
        Long idGarage = vue.getIdGarageSelectionneLie();
        if (idGarage == null) {
            JOptionPane.showMessageDialog(vue, "Sélectionnez un garage à retirer.");
            return;
        }

        if (JOptionPane.showConfirmDialog(vue, "Délier ce garage ?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        GarageDao gDao = null;
        try {
            gDao = new GarageDao();
            
            Garage g = gDao.findById(String.valueOf(idGarage));
            if (g != null) {
                g.setIdBail(null);
                g.setIdLogement(null);
                gDao.update(g);
            } else {
                gDao.delierDuBail(idGarage);
            }

            rafraichirGarages();
            JOptionPane.showMessageDialog(vue, "Garage retiré.");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vue, "Erreur retrait : " + ex.getMessage());
        } finally {
            if (gDao != null) try { gDao.close(); } catch (SQLException ex) { /* ignore */ }
        }
    }

    private void enregistrerEtFermer() {
        BailDao dao = null;
        try {
            double loyer = Double.parseDouble(vue.getLoyerSaisi().replace(",", "."));
            double prov = Double.parseDouble(vue.getProvisionSaisie().replace(",", "."));
            double caution = Double.parseDouble(vue.getCautionSaisie().replace(",", "."));
            int jour = vue.getJourPaiementSaisi();

            bail.setLoyerInitial(loyer);
            bail.setProvisionInitiales(prov);
            bail.setCaution(caution);
            bail.setJourPaiement(jour);

            dao = new BailDao();
            dao.update(bail);

            JOptionPane.showMessageDialog(vue, "Modifications enregistrées.");
            
            vue.setModifie(true);
            vue.dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vue, "Format de nombre invalide.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vue, "Erreur BDD : " + e.getMessage());
        } finally {
            if (dao != null) try { dao.close(); } catch (SQLException ex) { /* ignore */ }
        }
    }
}
