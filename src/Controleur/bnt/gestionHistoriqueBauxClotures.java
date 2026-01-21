package Controleur.bnt;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import modele.dao.HistoriqueLoyerDao;
import vue.bnt.DlgHistoriqueBauxClotures;

/**
 * Controleur pour le dialogue d'historique des baux clotures.
 */
public class gestionHistoriqueBauxClotures implements ActionListener {

    private DlgHistoriqueBauxClotures vue;

    public gestionHistoriqueBauxClotures(DlgHistoriqueBauxClotures vue) {
        this.vue = vue;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "ACTUALISER":
                chargerHistorique();
                break;
            case "FERMER":
                vue.fermer();
                break;
        }
    }

    /**
     * Charge l'historique des loyers des baux clotures
     */
    public void chargerHistorique() {
        try {
            HistoriqueLoyerDao dao = new HistoriqueLoyerDao();
            
            // Recuperer le resume par bien
            List<Object[]> resume = dao.getResumeParBien();
            
            // Vider et remplir le tableau
            DefaultTableModel model = vue.getModeleHistorique();
            model.setRowCount(0);
            
            double totalLoyers = 0;
            double totalProvisions = 0;
            int totalQuittances = 0;
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            for (Object[] row : resume) {
                String nomBien = (String) row[0];
                String nomLocataire = (String) row[1];
                int annee = (Integer) row[2];
                double loyers = (Double) row[3];
                double provisions = (Double) row[4];
                int nbMois = (Integer) row[5];
                int nbQuittances = (Integer) row[6];
                Date dateCloture = (Date) row[7];
                
                totalLoyers += loyers;
                totalProvisions += provisions;
                totalQuittances += nbQuittances;
                
                model.addRow(new Object[] {
                    nomBien != null ? nomBien : "-",
                    nomLocataire != null ? nomLocataire : "-",
                    annee,
                    String.format("%.2f", loyers),
                    String.format("%.2f", provisions),
                    nbMois,
                    nbQuittances + "/" + nbMois,
                    dateCloture != null ? sdf.format(dateCloture) : "-"
                });
            }
            
            // Mettre a jour les totaux
            vue.getLblTotalLoyers().setText(String.format("%.2f EUR", totalLoyers));
            vue.getLblTotalProvisions().setText(String.format("%.2f EUR", totalProvisions));
            vue.getLblNbQuittances().setText(String.valueOf(totalQuittances));
            
            dao.close();
            
            if (resume.isEmpty()) {
                JOptionPane.showMessageDialog(vue,
                    "Aucun historique disponible.\n\n" +
                    "L'historique est cree automatiquement lors de la cloture des baux.\n" +
                    "Les loyers payes et quittances generees seront conserves ici.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(vue,
                "Erreur lors du chargement de l'historique :\n" + ex.getMessage() +
                "\n\nAssurez-vous d'avoir execute le script HISTORIQUE_LOYERS.sql dans SQL Developer.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}