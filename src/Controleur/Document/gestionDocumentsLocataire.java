package Controleur.Document;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import modele.dao.DocumentLocataireDao;
import vue.Document.pageDocumentsLocataire;
import vue.Document.pageSaisieDocumentLocataire;

/**
 * Controleur pour la gestion des documents d'un locataire Connecte a la base de
 * donnees Oracle
 */
public class gestionDocumentsLocataire implements ActionListener {

    private final pageDocumentsLocataire vue;
    private DocumentLocataireDao dao;
    private long idLocataire;

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public gestionDocumentsLocataire(pageDocumentsLocataire vue) {
        this.vue = vue;
        this.idLocataire = vue.getLocataire().getIdLocataire();

        try {
            this.dao = new DocumentLocataireDao();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vue,
                "Erreur de connexion a la base de donnees", "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void chargerToutesDonnees() {
        chargerDiagnostics();
        chargerReleveEau(); // AJOUTÃ‰
        chargerCaution();
        chargerLoyers();
        chargerQuittances();
    }


    // CHARGEMENT DES DONNEES


    private void chargerDiagnostics() {
        try {
            DefaultTableModel model = (DefaultTableModel) vue
                .getTableDiagnostics()
                .getModel();
            model.setRowCount(0);

            List<Object[]> diagnostics = dao
                .getDiagnosticsLocataire(idLocataire);
            for (Object[] row : diagnostics) {
                model.addRow(new Object[] { row[0], row[1], row[2],
                    formatDate((Date) row[3]), formatDate((Date) row[4]),
                    row[5] });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Chargement des releves d'eau
    private void chargerReleveEau() {
        try {
            DefaultTableModel model = (DefaultTableModel) vue
                .getTableReleveEau()
                .getModel();
            model.setRowCount(0);

            List<Object[]> releves = dao.getRelevesEauLocataire(idLocataire);
            for (Object[] row : releves) {
                // row = [ID, Date, AncienIndex, NouveauIndex]
                int ancienIndex = ((Number) row[2]).intValue();
                int nouveauIndex = ((Number) row[3]).intValue();
                int consommation = nouveauIndex - ancienIndex;

                model.addRow(new Object[] { row[0], // ID
                    formatDate((Date) row[1]), // Date
                    ancienIndex, // Ancien Index
                    nouveauIndex, // Nouveau Index
                    consommation // Consommation calculÃ©e
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vue,
                "Erreur lors du chargement des relevÃ©s d'eau", "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chargerCaution() {
        try {
            DefaultTableModel model = (DefaultTableModel) vue.getTableCaution()
                .getModel();
            model.setRowCount(0);

            List<Object[]> cautions = dao.getMouvementsCaution(idLocataire);
            for (Object[] row : cautions) {
                model.addRow(new Object[] { row[0], formatDate((Date) row[1]),
                    row[2], formatMontant((Double) row[3]), row[4], row[5] });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerLoyers() {
        try {
            DefaultTableModel model = (DefaultTableModel) vue.getTableLoyers()
                .getModel();
            model.setRowCount(0);

            List<Object[]> loyers = dao.getLoyersEnAttente(idLocataire);
            for (Object[] row : loyers) {
                model.addRow(new Object[] { row[0], row[1],
                    formatMontant((Double) row[2]),
                    formatMontant((Double) row[3]),
                    formatMontant((Double) row[4]), row[5] });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerQuittances() {
        try {
            DefaultTableModel model = (DefaultTableModel) vue
                .getTableQuittances()
                .getModel();
            model.setRowCount(0);

            List<Object[]> quittances = dao.getQuittancesPayees(idLocataire);
            for (Object[] row : quittances) {
                model.addRow(new Object[] { row[0], row[1],
                    formatMontant((Double) row[2]),
                    formatMontant((Double) row[3]),
                    formatMontant((Double) row[4]), formatDate((Date) row[5]),
                    row[6] });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // GESTION DES ACTIONS


    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
        case "FERMER_DOCUMENTS_LOCATAIRE":
            if (dao != null) {
                try {
                    dao.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            vue.dispose();
            break;

        case "AJOUTER_DIAGNOSTIC":
            ajouterDiagnostic();
            break;
        case "MODIFIER_DIAGNOSTIC":
            modifierDiagnostic();
            break;
        case "SUPPRIMER_DIAGNOSTIC":
            supprimerDiagnostic();
            break;

        // Actions pour les releves d'eau
        case "AJOUTER_RELEVE_EAU":
            ajouterReleveEau();
            break;
        case "MODIFIER_RELEVE_EAU":
            modifierReleveEau();
            break;
        case "SUPPRIMER_RELEVE_EAU":
            supprimerReleveEau();
            break;

        case "AJOUTER_CAUTION":
            ajouterCaution();
            break;
        case "MODIFIER_CAUTION":
            modifierCaution();
            break;
        case "SUPPRIMER_CAUTION":
            supprimerCaution();
            break;

        case "AJOUTER_LOYER":
            marquerLoyerPaye();
            break;
        case "SUPPRIMER_QUITTANCE":
            annulerPaiement();
            break;
        }
    }


    // DIAGNOSTICS


    private void ajouterDiagnostic() {
        String[] labels = { "Type Diagnostic", "Reference",
            "Date Emission (dd/MM/yyyy)", "Date Expiration (dd/MM/yyyy)" };
        String[] res = ouvrirSaisie("Ajouter un diagnostic", labels, null);

        if (res != null) {
            try {
                Date dateEmission = sdf.parse(res[2]);
                Date dateExpiration = sdf.parse(res[3]);
                dao.ajouterDiagnostic(idLocataire, res[0], res[1], dateEmission,
                    dateExpiration);
                chargerDiagnostics();
                JOptionPane.showMessageDialog(vue,
                    "Diagnostic ajoute avec succes");
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(vue, "Format de date invalide",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(),
                    "Erreur BDD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifierDiagnostic() {
        JTable table = vue.getTableDiagnostics();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(vue,
                "Veuillez selectionner un diagnostic");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        long idDiag = Long.parseLong(model.getValueAt(row, 0).toString());

        String[] labels = { "Type Diagnostic", "Reference", "Date Emission",
            "Date Expiration" };
        String[] init = { str(model.getValueAt(row, 1)),
            str(model.getValueAt(row, 2)), str(model.getValueAt(row, 3)),
            str(model.getValueAt(row, 4)) };

        String[] res = ouvrirSaisie("Modifier diagnostic", labels, init);
        if (res != null) {
            try {
                Date dateEmission = sdf.parse(res[2]);
                Date dateExpiration = sdf.parse(res[3]);
                dao.modifierDiagnostic(idDiag, res[0], res[1], dateEmission,
                    dateExpiration);
                chargerDiagnostics();
                JOptionPane.showMessageDialog(vue,
                    "Diagnostic modifie avec succes");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerDiagnostic() {
        JTable table = vue.getTableDiagnostics();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(vue,
                "Veuillez selectionner un diagnostic");
            return;
        }

        if (confirmerSuppression("diagnostic")) {
            try {
                long idDiag = Long.parseLong(
                    ((DefaultTableModel) table.getModel()).getValueAt(row, 0)
                        .toString());
                dao.supprimerDiagnostic(idDiag);
                chargerDiagnostics();
                JOptionPane.showMessageDialog(vue, "Diagnostic supprime");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(),
                    "Erreur BDD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // RELEVES D'EAU 


    private void ajouterReleveEau() {
        String[] labels = { "Date (dd/MM/yyyy)", "Ancien Index",
            "Nouveau Index" };
        String[] res = ouvrirSaisie("Ajouter un relevÃ© d'eau", labels, null);

        if (res != null) {
            try {
                Date date = sdf.parse(res[0]);
                int ancienIndex = Integer.parseInt(res[1]);
                int nouveauIndex = Integer.parseInt(res[2]);

                if (nouveauIndex < ancienIndex) {
                    JOptionPane.showMessageDialog(vue,
                        "Le nouveau index ne peut pas Ãªtre infÃ©rieur Ã  l'ancien index",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                dao.ajouterReleveEau(idLocataire, date, ancienIndex,
                    nouveauIndex);
                chargerReleveEau();

                int consommation = nouveauIndex - ancienIndex;
                JOptionPane.showMessageDialog(vue,
                    "RelevÃ© ajoutÃ© avec succÃ¨s\nConsommation: " + consommation
                        + " mÂ³");
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(vue, "Format de date invalide",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vue,
                    "Index invalide (doit Ãªtre un nombre entier)", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(),
                    "Erreur BDD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifierReleveEau() {
        JTable table = vue.getTableReleveEau();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(vue,
                "Veuillez selectionner un relevÃ©");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        long idReleve = Long.parseLong(model.getValueAt(row, 0).toString());

        String[] labels = { "Date", "Ancien Index", "Nouveau Index" };
        String[] init = { str(model.getValueAt(row, 1)),
            str(model.getValueAt(row, 2)), str(model.getValueAt(row, 3)) };

        String[] res = ouvrirSaisie("Modifier relevÃ© d'eau", labels, init);
        if (res != null) {
            try {
                Date date = sdf.parse(res[0]);
                int ancienIndex = Integer.parseInt(res[1]);
                int nouveauIndex = Integer.parseInt(res[2]);

                if (nouveauIndex < ancienIndex) {
                    JOptionPane.showMessageDialog(vue,
                        "Le nouveau index ne peut pas Ãªtre infÃ©rieur Ã  l'ancien index",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                dao.modifierReleveEau(idReleve, date, ancienIndex,
                    nouveauIndex);
                chargerReleveEau();
                JOptionPane.showMessageDialog(vue,
                    "RelevÃ© modifiÃ© avec succÃ¨s");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerReleveEau() {
        JTable table = vue.getTableReleveEau();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(vue,
                "Veuillez selectionner un relevÃ©");
            return;
        }

        if (confirmerSuppression("relevÃ© d'eau")) {
            try {
                long idReleve = Long.parseLong(
                    ((DefaultTableModel) table.getModel()).getValueAt(row, 0)
                        .toString());
                dao.supprimerReleveEau(idReleve);
                chargerReleveEau();
                JOptionPane.showMessageDialog(vue, "RelevÃ© supprimÃ©");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(),
                    "Erreur BDD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // CAUTION


    private void ajouterCaution() {
        String[] labels = { "Date (dd/MM/yyyy)",
            "Type (Versement/Restitution/Retenue)", "Montant", "Moyen Paiement",
            "Observations" };
        String[] res = ouvrirSaisie("Ajouter un mouvement de caution", labels,
            null);

        if (res != null) {
            try {
                Date date = sdf.parse(res[0]);
                double montant = parseMontant(res[2]);
                dao.ajouterMouvementCaution(idLocataire, date, res[1], montant,
                    res[3], res[4]);
                chargerCaution();
                JOptionPane.showMessageDialog(vue,
                    "Mouvement ajoute avec succes");
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(vue, "Format de date invalide",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vue, "Montant invalide", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(),
                    "Erreur BDD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifierCaution() {
        JTable table = vue.getTableCaution();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(vue,
                "Veuillez selectionner un mouvement");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        long idMvt = Long.parseLong(model.getValueAt(row, 0).toString());

        String[] labels = { "Date", "Type Mouvement", "Montant",
            "Moyen Paiement", "Observations" };
        String[] init = { str(model.getValueAt(row, 1)),
            str(model.getValueAt(row, 2)), str(model.getValueAt(row, 3)),
            str(model.getValueAt(row, 4)), str(model.getValueAt(row, 5)) };

        String[] res = ouvrirSaisie("Modifier mouvement", labels, init);
        if (res != null) {
            try {
                Date date = sdf.parse(res[0]);
                double montant = parseMontant(res[2]);
                dao.modifierMouvementCaution(idMvt, date, res[1], montant,
                    res[3], res[4]);
                chargerCaution();
                JOptionPane.showMessageDialog(vue,
                    "Mouvement modifie avec succes");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerCaution() {
        JTable table = vue.getTableCaution();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(vue,
                "Veuillez selectionner un mouvement");
            return;
        }

        if (confirmerSuppression("mouvement")) {
            try {
                long idMvt = Long.parseLong(
                    ((DefaultTableModel) table.getModel()).getValueAt(row, 0)
                        .toString());
                dao.supprimerMouvementCaution(idMvt);
                chargerCaution();
                JOptionPane.showMessageDialog(vue, "Mouvement supprime");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(),
                    "Erreur BDD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // LOYERS / QUITTANCES


    private void marquerLoyerPaye() {
        JTable table = vue.getTableLoyers();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(vue,
                "Veuillez selectionner un loyer pour le marquer comme paye");
            return;
        }

        String[] labels = { "Date de paiement (dd/MM/yyyy)" };
        String[] defaut = { sdf.format(new Date()) };
        String[] res = ouvrirSaisie("Marquer comme paye", labels, defaut);

        if (res != null) {
            try {
                Date datePaiement = sdf.parse(res[0]);
                long idLoyer = Long.parseLong(
                    ((DefaultTableModel) table.getModel()).getValueAt(row, 0)
                        .toString());
                dao.marquerLoyerPaye(idLoyer, datePaiement);
                chargerLoyers();
                chargerQuittances();
                JOptionPane.showMessageDialog(vue,
                    "Loyer marque comme paye.\nQuittance generee.");
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(vue, "Format de date invalide",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(),
                    "Erreur BDD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void annulerPaiement() {
        JTable table = vue.getTableQuittances();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(vue,
                "Veuillez selectionner une quittance");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(vue,
            "Voulez-vous vraiment annuler ce paiement ?\nLe loyer reviendra en 'En attente'.",
            "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                long idLoyer = Long.parseLong(
                    ((DefaultTableModel) table.getModel()).getValueAt(row, 0)
                        .toString());
                dao.annulerPaiement(idLoyer);
                chargerLoyers();
                chargerQuittances();
                JOptionPane.showMessageDialog(vue, "Paiement annule");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vue, "Erreur: " + ex.getMessage(),
                    "Erreur BDD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // UTILITAIRES


    private String[] ouvrirSaisie(String titre, String[] labels,
        String[] valeursInitiales) {
        pageSaisieDocumentLocataire dlg = new pageSaisieDocumentLocataire(titre,
            labels, valeursInitiales);
        new gestionSaisieDocument(dlg);
        dlg.setLocationRelativeTo(vue);
        dlg.setVisible(true);
        return dlg.getResultat();
    }

    private boolean confirmerSuppression(String element) {
        return JOptionPane.showConfirmDialog(vue,
            "Voulez-vous vraiment supprimer ce " + element + " ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private String formatDate(Date date) {
        return date == null ? "" : sdf.format(date);
    }

    private String formatMontant(Double m) {
        return m == null ? "0,00 EUR" : String.format("%.2f EUR", m);
    }

    private double parseMontant(String s) {
        return Double.parseDouble(
            s.replace(",", ".").replace("EUR", "").replace(" ", "").trim());
    }

    private String str(Object o) {
        return o != null ? o.toString() : "";
    }
}