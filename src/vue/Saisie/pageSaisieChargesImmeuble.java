package vue.Saisie;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Fenetre de saisie des charges de l'immeuble
 */
public class pageSaisieChargesImmeuble extends JDialog {

    private static final long serialVersionUID = 1L;

    // Contexte
    private long idBatiment;
    private int annee;
    private String adresseBatiment;

    // Champs de saisie
    private JTextField txtEau;
    private JTextField txtOrduresMenageres;
    private JTextField txtElectricite;
    private JTextField txtEntretien;
    private JLabel lblTotal;

    // Boutons
    private JButton btnEnregistrer;
    private JButton btnAnnuler;

    // Resultat
    private boolean valide = false;

    public pageSaisieChargesImmeuble(Window parent, long idBatiment, int annee, String adresseBatiment) {
        super(parent, "Saisie des charges - " + annee, ModalityType.APPLICATION_MODAL);
        
        this.idBatiment = idBatiment;
        this.annee = annee;
        this.adresseBatiment = adresseBatiment;
        
        initUI();
    }

    private void initUI() {
        setSize(450, 350);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(15, 20, 15, 20));
        setContentPane(contentPane);

        //  HEADER 
        JPanel panelHeader = new JPanel(new BorderLayout());
        JLabel lblTitre = new JLabel("Charges de l'immeuble - Annee " + annee);
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 16));
        panelHeader.add(lblTitre, BorderLayout.NORTH);
        
        JLabel lblAdresse = new JLabel(adresseBatiment != null ? adresseBatiment : "Batiment #" + idBatiment);
        lblAdresse.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblAdresse.setForeground(Color.GRAY);
        panelHeader.add(lblAdresse, BorderLayout.SOUTH);
        
        contentPane.add(panelHeader, BorderLayout.NORTH);

        //  FORMULAIRE 
        JPanel panelForm = new JPanel(new GridLayout(5, 2, 10, 12));
        panelForm.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        // Eau
        panelForm.add(new JLabel("Eau (€) :"));
        txtEau = new JTextField("0.00");
        txtEau.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                calculerTotal();
            }
        });
        panelForm.add(txtEau);

        // Ordures menageres
        panelForm.add(new JLabel("Ordures menageres (€) :"));
        txtOrduresMenageres = new JTextField("0.00");
        txtOrduresMenageres.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                calculerTotal();
            }
        });
        panelForm.add(txtOrduresMenageres);

        // Electricite
        panelForm.add(new JLabel("Electricite (€) :"));
        txtElectricite = new JTextField("0.00");
        txtElectricite.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                calculerTotal();
            }
        });
        panelForm.add(txtElectricite);

        // Entretien
        panelForm.add(new JLabel("Entretien (€) :"));
        txtEntretien = new JTextField("0.00");
        txtEntretien.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                calculerTotal();
            }
        });
        panelForm.add(txtEntretien);

        // Total
        panelForm.add(new JLabel("TOTAL :"));
        lblTotal = new JLabel("0.00 €");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTotal.setForeground(new Color(0, 100, 0));
        panelForm.add(lblTotal);

        contentPane.add(panelForm, BorderLayout.CENTER);

        //  BOUTONS 
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        btnAnnuler = new JButton("Annuler");
        btnAnnuler.addActionListener(e -> {
            valide = false;
            dispose();
        });
        
        btnEnregistrer = new JButton("Enregistrer");
        btnEnregistrer.setBackground(new Color(40, 167, 69));
        btnEnregistrer.setForeground(Color.WHITE);
        btnEnregistrer.addActionListener(e -> enregistrer());
        
        panelBoutons.add(btnAnnuler);
        panelBoutons.add(btnEnregistrer);
        
        contentPane.add(panelBoutons, BorderLayout.SOUTH);
    }

    /**
     * Calcule et affiche le total
     */
    private void calculerTotal() {
        try {
            double eau = parseDouble(txtEau.getText());
            double om = parseDouble(txtOrduresMenageres.getText());
            double elec = parseDouble(txtElectricite.getText());
            double entretien = parseDouble(txtEntretien.getText());
            double total = eau + om + elec + entretien;
            lblTotal.setText(String.format("%.2f €", total));
        } catch (Exception e) {
            lblTotal.setText("Erreur");
        }
    }

    /**
     * Parse un double depuis une chaine (gere la virgule)
     */
    private double parseDouble(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        return Double.parseDouble(s.trim().replace(",", "."));
    }

    /**
     * Enregistre les charges
     */
    private void enregistrer() {
        try {
            double eau = parseDouble(txtEau.getText());
            double om = parseDouble(txtOrduresMenageres.getText());
            double elec = parseDouble(txtElectricite.getText());
            double entretien = parseDouble(txtEntretien.getText());

            // Validation
            if (eau < 0 || om < 0 || elec < 0 || entretien < 0) {
                JOptionPane.showMessageDialog(this,
                    "Les montants ne peuvent pas etre negatifs.",
                    "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double total = eau + om + elec + entretien;
            if (total == 0) {
                int reponse = JOptionPane.showConfirmDialog(this,
                    "Tous les montants sont a zero.\nVoulez-vous continuer ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (reponse != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // Confirmation
            int reponse = JOptionPane.showConfirmDialog(this,
                "Enregistrer les charges pour l'annee " + annee + " ?\n\n" +
                "Eau : " + String.format("%.2f", eau) + " €\n" +
                "Ordures menageres : " + String.format("%.2f", om) + " €\n" +
                "Electricite : " + String.format("%.2f", elec) + " €\n" +
                "Entretien : " + String.format("%.2f", entretien) + " €\n" +
                "─────────────────────\n" +
                "TOTAL : " + String.format("%.2f", total) + " €",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (reponse == JOptionPane.YES_OPTION) {
                valide = true;
                dispose();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Veuillez saisir des montants valides (nombres uniquement).",
                "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
        }
    }

    //  GETTERS 
    
    public long getIdBatiment() {
        return idBatiment;
    }

    public int getAnnee() {
        return annee;
    }

    public boolean isValide() {
        return valide;
    }

    public double getMontantEau() {
        return parseDouble(txtEau.getText());
    }

    public double getMontantOrduresMenageres() {
        return parseDouble(txtOrduresMenageres.getText());
    }

    public double getMontantElectricite() {
        return parseDouble(txtElectricite.getText());
    }

    public double getMontantEntretien() {
        return parseDouble(txtEntretien.getText());
    }

    //  SETTERS pour pre-remplir 
    
    public void setMontantEau(double montant) {
        txtEau.setText(String.format("%.2f", montant));
        calculerTotal();
    }

    public void setMontantOrduresMenageres(double montant) {
        txtOrduresMenageres.setText(String.format("%.2f", montant));
        calculerTotal();
    }

    public void setMontantElectricite(double montant) {
        txtElectricite.setText(String.format("%.2f", montant));
        calculerTotal();
    }

    public void setMontantEntretien(double montant) {
        txtEntretien.setText(String.format("%.2f", montant));
        calculerTotal();
    }
}