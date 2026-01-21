package Controleur.Principale;

import java.awt.Color;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import modele.Loyer;
import modele.dao.LoyerDao;
import vue.Principale.pageGestionLoyers;

// Contrôleur pour la page de gestion des loyers
 
public class gestionGestionLoyers implements ActionListener {

    private pageGestionLoyers vue;
    private List<Loyer> loyers;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public gestionGestionLoyers(pageGestionLoyers vue) {
        this.vue = vue;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        
        switch (cmd) {
            case "MARQUER_PAYE":
                marquerPaye();
                break;
            case "MARQUER_RETARD":
                marquerRetard();
                break;
            case "MARQUER_EN_ATTENTE":
                marquerEnAttente();
                break;
            case "GENERER_LOYERS":
                genererLoyers();
                break;
        }
    }

    // Charge les loyers du bail pour l'année sélectionnée
     
    public void chargerLoyers() {
        new Thread(() -> {
            try {
                LoyerDao dao = new LoyerDao();
                loyers = dao.findByBailAndAnnee(vue.getIdBail(), vue.getAnnee());
                dao.close();
                
                SwingUtilities.invokeLater(this::mettreAJourTableau);
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(vue,
                        "Erreur lors du chargement des loyers : " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void mettreAJourTableau() {
        JTable table = vue.getTableLoyers();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        
        if (loyers == null || loyers.isEmpty()) {
            return;
        }
        
        for (Loyer loyer : loyers) {
            double provision = loyer.getMontantProvision() != null ? loyer.getMontantProvision() : 0;
            double total = loyer.getMontantLoyer() + provision;
            String datePaiement = loyer.getDatePaiement() != null ? 
                                  sdf.format(loyer.getDatePaiement()) : "";
            
            model.addRow(new Object[]{
                loyer.getIdLoyerLong(),
                loyer.getMois(),
                String.format("%.2f", loyer.getMontantLoyer()),
                String.format("%.2f", provision),
                String.format("%.2f", total),
                loyer.getStatut(),
                datePaiement
            });
        }
        
        // Renderer pour colorer les statuts
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected && value != null) {
                    String statut = value.toString().toUpperCase();
                    if (statut.contains("PAYE")) {
                        c.setForeground(new Color(40, 167, 69));
                    } else if (statut.contains("RETARD")) {
                        c.setForeground(new Color(220, 53, 69));
                    } else {
                        c.setForeground(new Color(108, 117, 125));
                    }
                }
                return c;
            }
        });
    }

    private void marquerPaye() {
        int row = vue.getTableLoyers().getSelectedRow();
        if (row < 0 || loyers == null || row >= loyers.size()) {
            JOptionPane.showMessageDialog(vue, "Veuillez sélectionner un loyer.", 
                                          "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Loyer loyer = loyers.get(row);
        
        // Demander la date de paiement
        String dateStr = JOptionPane.showInputDialog(vue, 
            "Date de paiement (JJ/MM/AAAA) :", 
            sdf.format(new Date()));
        
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return;
        }
        
        try {
            Date datePaiement = sdf.parse(dateStr);
            
            new Thread(() -> {
                try {
                    LoyerDao dao = new LoyerDao();
                    dao.marquerPayeProcedure(loyer.getIdLoyerLong(), datePaiement);
                    dao.close();
                    
                    SwingUtilities.invokeLater(() -> {
                        chargerLoyers();
                        JOptionPane.showMessageDialog(vue, "Loyer marqué comme payé !",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(vue, "Erreur : " + ex.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vue, "Format de date invalide.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void marquerRetard() {
        int row = vue.getTableLoyers().getSelectedRow();
        if (row < 0 || loyers == null || row >= loyers.size()) {
            JOptionPane.showMessageDialog(vue, "Veuillez sélectionner un loyer.",
                                          "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Loyer loyer = loyers.get(row);
        
        int confirm = JOptionPane.showConfirmDialog(vue,
            "Marquer le loyer de " + loyer.getMois() + " en RETARD ?",
            "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        new Thread(() -> {
            try {
                LoyerDao dao = new LoyerDao();
                dao.marquerRetard(loyer.getIdLoyerLong());
                dao.close();
                
                SwingUtilities.invokeLater(() -> {
                    chargerLoyers();
                    JOptionPane.showMessageDialog(vue, "Loyer marqué en retard.",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(vue, "Erreur : " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void marquerEnAttente() {
        int row = vue.getTableLoyers().getSelectedRow();
        if (row < 0 || loyers == null || row >= loyers.size()) {
            JOptionPane.showMessageDialog(vue, "Veuillez sélectionner un loyer.",
                                          "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Loyer loyer = loyers.get(row);
        
        int confirm = JOptionPane.showConfirmDialog(vue,
            "Remettre le loyer de " + loyer.getMois() + " EN ATTENTE ?",
            "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        new Thread(() -> {
            try {
                LoyerDao dao = new LoyerDao();
                dao.marquerEnAttente(loyer.getIdLoyerLong());
                dao.close();
                
                SwingUtilities.invokeLater(() -> {
                    chargerLoyers();
                    JOptionPane.showMessageDialog(vue, "Loyer remis en attente.",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(vue, "Erreur : " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void genererLoyers() {
        int confirm = JOptionPane.showConfirmDialog(vue,
            "Générer les loyers manquants pour " + vue.getAnnee() + " ?",
            "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        new Thread(() -> {
            try {
                LoyerDao dao = new LoyerDao();
                dao.genererLoyers(vue.getIdBail(), vue.getAnnee());
                dao.close();
                
                SwingUtilities.invokeLater(() -> {
                    chargerLoyers();
                    JOptionPane.showMessageDialog(vue, 
                        "Loyers générés pour " + vue.getAnnee() + " !",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(vue, "Erreur : " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }
}