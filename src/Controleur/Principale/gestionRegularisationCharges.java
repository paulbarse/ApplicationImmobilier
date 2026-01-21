package Controleur.Principale;

import java.awt.Color;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import Controleur.refacto.NavigationHelper;
import modele.dao.ChargesDao;
import modele.dao.FactureDao;
import modele.dao.RegularisationDao;
import modele.dao.RegularisationDao.RecapLocataire;
import vue.Principale.pageRegularisationCharges;

/**
 * Contrôleur pour la page de régularisation des charges
 */
public class gestionRegularisationCharges implements ActionListener, ListSelectionListener {

    private pageRegularisationCharges vue;
    private List<RecapLocataire> listeLocataires;
    private int anneeSelectionnee;
    private DecimalFormat formatMonetaire;

    public gestionRegularisationCharges(pageRegularisationCharges vue) {
        this.vue = vue;
        this.anneeSelectionnee = Calendar.getInstance().get(Calendar.YEAR);
        
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');
        formatMonetaire = new DecimalFormat("#,##0.00", symbols);
        
        SwingUtilities.invokeLater(() -> chargerDonnees());
    }

    public void setAnnee(int annee) {
        this.anneeSelectionnee = annee;
        chargerDonnees();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
        case "RAFRAICHIR":
            chargerDonnees();
            break;
        default:
            NavigationHelper.handleNavigation(this.vue, cmd);
            break;
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }

        JTable table = this.vue.getTableRecapImpots();
        int row = table.getSelectedRow();

        if (row < 0 || listeLocataires == null || row >= listeLocataires.size()) {
            return;
        }

        RecapLocataire recap = listeLocataires.get(row);
        afficherDetailsLocataire(recap);
        
        if (recap.getIdBatiment() > 0) {
            chargerChargesImmeuble(recap.getIdBatiment());
        }
    }

    public void chargerDonnees() {
        RegularisationDao dao = null;
        try {
            dao = new RegularisationDao();
            listeLocataires = dao.getRecapTousLocataires(anneeSelectionnee);
            mettreAJourTableau();
            viderChampsDetails();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(vue, 
                "Erreur lors du chargement des données: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (dao != null) try { dao.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    private void chargerChargesImmeuble(long idBatiment) {
        ChargesDao chargesDao = null;
        FactureDao factureDao = null;
        try {
            chargesDao = new ChargesDao();
            factureDao = new FactureDao();
            
            double eau = chargesDao.getMontantByType(idBatiment, anneeSelectionnee, ChargesDao.TYPE_EAU);
            double om = chargesDao.getMontantByType(idBatiment, anneeSelectionnee, ChargesDao.TYPE_OM);
            double elec = chargesDao.getMontantByType(idBatiment, anneeSelectionnee, ChargesDao.TYPE_ELEC);
            double entretien = chargesDao.getMontantByType(idBatiment, anneeSelectionnee, ChargesDao.TYPE_ENTRETIEN);
            
            double facturesRecuperables = factureDao.getTotalFacturesRecuperablesLocataire(idBatiment, anneeSelectionnee);
            
            List<String> chargesManquantes = new ArrayList<>();
            if (eau <= 0) chargesManquantes.add("Eau");
            if (om <= 0) chargesManquantes.add("Ordures ménagères");
            if (elec <= 0) chargesManquantes.add("Électricité");
            if (entretien <= 0 && facturesRecuperables <= 0) chargesManquantes.add("Entretien");
            
            JLabel lblAvertissement = vue.getLblAvertissementCharges();
            if (lblAvertissement != null) {
                if (!chargesManquantes.isEmpty()) {
                    String message = "⚠ Non saisies : " + String.join(", ", chargesManquantes);
                    lblAvertissement.setText(message);
                    lblAvertissement.setToolTipText(
                        "Ces charges ne sont pas saisies dans Documents > Bâtiment > Charges pour l'année " + anneeSelectionnee);
                } else {
                    lblAvertissement.setText("✓ Toutes les charges sont saisies");
                    lblAvertissement.setForeground(new Color(34, 139, 34));
                }
            }
            
            afficherChampCharge(vue.getTxtTotalFactureEau(), eau, "Non saisie");
            afficherChampCharge(vue.getTxtTotalOrduresMenageres(), om, "Non saisie");
            afficherChampCharge(vue.getTxtTotalElectricite(), elec, "Non saisie");
            
            double entretienTotal = entretien + facturesRecuperables;
            if (entretienTotal > 0) {
                String suffixe = facturesRecuperables > 0 ? " *" : "";
                vue.getTxtTotalEntretien().setText(formatMontant(entretienTotal) + suffixe);
                vue.getTxtTotalEntretien().setForeground(Color.BLACK);
                if (facturesRecuperables > 0) {
                    vue.getTxtTotalEntretien().setToolTipText(
                        "Inclut " + formatMontant(facturesRecuperables) + " € de factures récupérables locataire");
                }
            } else {
                vue.getTxtTotalEntretien().setText("Non saisie");
                vue.getTxtTotalEntretien().setForeground(new Color(255, 140, 0));
            }
            
            double total = eau + om + elec + entretienTotal;
            if (vue.getTxtTotalChargesImmeuble() != null) {
                if (total > 0) {
                    vue.getTxtTotalChargesImmeuble().setText(formatMontant(total));
                } else {
                    vue.getTxtTotalChargesImmeuble().setText("Aucune charge saisie");
                }
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JLabel lblAvertissement = vue.getLblAvertissementCharges();
            if (lblAvertissement != null) {
                lblAvertissement.setText("⚠ Erreur lors de la récupération des charges");
                lblAvertissement.setForeground(Color.RED);
            }
        } finally {
            if (chargesDao != null) try { chargesDao.close(); } catch (SQLException e) { /* ignore */ }
            if (factureDao != null) try { factureDao.close(); } catch (SQLException e) { /* ignore */ }
        }
    }
    
    private void afficherChampCharge(JTextField champ, double montant, String messageVide) {
        if (champ == null) return;
        
        if (montant > 0) {
            champ.setText(formatMontant(montant));
            champ.setForeground(Color.BLACK);
            champ.setToolTipText(null);
        } else {
            champ.setText(messageVide);
            champ.setForeground(new Color(255, 140, 0));
            champ.setToolTipText("Saisissez cette charge dans Documents > Bâtiment > Charges");
        }
    }

    private void mettreAJourTableau() {
        JTable table = this.vue.getTableRecapImpots();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        model.setRowCount(0);
        
        if (listeLocataires == null || listeLocataires.isEmpty()) {
            model.addRow(new Object[]{"Aucun locataire", "-", "-", "-", "-", "-"});
            return;
        }
        
        for (RecapLocataire recap : listeLocataires) {
            double chargesAvecFactures = recap.getTotalCharges();
            
            FactureDao factureDao = null;
            try {
                factureDao = new FactureDao();
                double facturesRecup = factureDao.getTotalFacturesRecuperablesLocataire(
                    recap.getIdBatiment(), anneeSelectionnee);
                
                if (facturesRecup > 0 && recap.getNbMoisOccupation() > 0) {
                    double quotePart = (facturesRecup / 12.0) * recap.getNbMoisOccupation();
                    chargesAvecFactures += quotePart;
                }
            } catch (SQLException e) {
                // Ignorer l'erreur, garder les charges existantes
            } finally {
                if (factureDao != null) try { factureDao.close(); } catch (SQLException ex) { /* ignore */ }
            }
            
            double regul = chargesAvecFactures - recap.getTotalProvisions();
            String regulStr = formatMontant(regul);
            if (regul > 0) {
                regulStr = "+" + regulStr;
            }
            
            String adresse = recap.getAdresseLogement();
            if (adresse == null || adresse.isEmpty()) {
                adresse = "-";
            }
            
            String chargesStr = formatMontant(chargesAvecFactures);
            if (chargesAvecFactures <= 0) {
                chargesStr = "! 0,00";
            }
            
            model.addRow(new Object[]{
                recap.getNomLocataire(),
                adresse,
                String.valueOf(recap.getNbMoisOccupation()),
                chargesStr,
                formatMontant(recap.getTotalProvisions()),
                regulStr
            });
        }
        
        // Renderer pour colorer la régularisation
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected && value != null) {
                    String val = value.toString().replace(" ", "").replace(",", ".");
                    try {
                        double montant = Double.parseDouble(val.replace("+", ""));
                        if (val.startsWith("+") || montant > 0) {
                            c.setForeground(new Color(220, 53, 69));
                        } else if (montant < 0) {
                            c.setForeground(new Color(34, 139, 34));
                        } else {
                            c.setForeground(Color.GRAY);
                        }
                    } catch (NumberFormatException e) {
                        c.setForeground(Color.BLACK);
                    }
                }
                
                setHorizontalAlignment(JLabel.RIGHT);
                return c;
            }
        });
        
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected && value != null) {
                    String val = value.toString();
                    if (val.startsWith("!")) {
                        c.setForeground(new Color(255, 140, 0));
                        setToolTipText("Charges non saisies - Voir Documents > Bâtiment > Charges");
                    } else {
                        c.setForeground(Color.BLACK);
                        setToolTipText(null);
                    }
                }
                
                setHorizontalAlignment(JLabel.RIGHT);
                return c;
            }
        });
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
    }

    private void afficherDetailsLocataire(RecapLocataire recap) {
        vue.getTxtNbMoisOccupation().setText(String.valueOf(recap.getNbMoisOccupation()));
        
        afficherChampCharge(vue.getTxtLocationEau(), recap.getChargesEau(), "N/A");
        afficherChampCharge(vue.getTxtOrdureMenagere(), recap.getChargesOM(), "N/A");
        afficherChampCharge(vue.getTxtElectriciteLocation(), recap.getChargesElec(), "N/A");
        afficherChampCharge(vue.getTxtEntretien(), recap.getChargesEntretien(), "N/A");
        
        double chargesAvecFactures = recap.getTotalCharges();
        
        FactureDao factureDao = null;
        try {
            factureDao = new FactureDao();
            double facturesRecup = factureDao.getTotalFacturesRecuperablesLocataire(
                recap.getIdBatiment(), anneeSelectionnee);
            
            if (facturesRecup > 0 && recap.getNbMoisOccupation() > 0) {
                double quotePart = (facturesRecup / 12.0) * recap.getNbMoisOccupation();
                chargesAvecFactures += quotePart;
            }
        } catch (SQLException e) {
            // Ignorer
        } finally {
            if (factureDao != null) try { factureDao.close(); } catch (SQLException ex) { /* ignore */ }
        }
        
        vue.getTxtTotalCharge().setText(formatMontant(chargesAvecFactures));
        vue.getTxtProvisionSurCharge().setText(formatMontant(recap.getTotalProvisions()));
        
        double regul = chargesAvecFactures - recap.getTotalProvisions();
        vue.getTxtResteACharge().setText(formatMontant(regul));
        if (regul > 0) {
            vue.getTxtResteACharge().setForeground(new Color(220, 53, 69));
        } else if (regul < 0) {
            vue.getTxtResteACharge().setForeground(new Color(34, 139, 34));
        } else {
            vue.getTxtResteACharge().setForeground(Color.GRAY);
        }
        
        vue.getTxtProvisionNouvelle().setText(formatMontant(recap.getNouvelleProvision()));
    }

    private void viderChampsDetails() {
        vue.getTxtTotalFactureEau().setText("-");
        vue.getTxtTotalFactureEau().setForeground(Color.BLACK);
        if (vue.getTxtTotalOrduresMenageres() != null) {
            vue.getTxtTotalOrduresMenageres().setText("-");
            vue.getTxtTotalOrduresMenageres().setForeground(Color.BLACK);
        }
        if (vue.getTxtTotalElectricite() != null) {
            vue.getTxtTotalElectricite().setText("-");
            vue.getTxtTotalElectricite().setForeground(Color.BLACK);
        }
        if (vue.getTxtTotalEntretien() != null) {
            vue.getTxtTotalEntretien().setText("-");
            vue.getTxtTotalEntretien().setForeground(Color.BLACK);
        }
        if (vue.getTxtTotalChargesImmeuble() != null) {
            vue.getTxtTotalChargesImmeuble().setText("-");
        }
        
        JLabel lblAvertissement = vue.getLblAvertissementCharges();
        if (lblAvertissement != null) {
            lblAvertissement.setText("Sélectionnez un locataire pour voir les charges");
            lblAvertissement.setForeground(new Color(100, 100, 100));
        }
        
        vue.getTxtNbMoisOccupation().setText("-");
        vue.getTxtLocationEau().setText("-");
        vue.getTxtLocationEau().setForeground(Color.BLACK);
        vue.getTxtOrdureMenagere().setText("-");
        vue.getTxtOrdureMenagere().setForeground(Color.BLACK);
        vue.getTxtElectriciteLocation().setText("-");
        vue.getTxtElectriciteLocation().setForeground(Color.BLACK);
        vue.getTxtEntretien().setText("-");
        vue.getTxtEntretien().setForeground(Color.BLACK);
        vue.getTxtTotalCharge().setText("-");
        vue.getTxtProvisionSurCharge().setText("-");
        vue.getTxtResteACharge().setText("-");
        vue.getTxtResteACharge().setForeground(Color.BLACK);
        vue.getTxtProvisionNouvelle().setText("-");
    }

    private String formatMontant(double montant) {
        return formatMonetaire.format(montant);
    }
}
