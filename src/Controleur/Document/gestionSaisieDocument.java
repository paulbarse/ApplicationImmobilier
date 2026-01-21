package Controleur.Document;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

/**
 * Contrôleur pour la saisie de tous types de documents.
 * 
 */
public class gestionSaisieDocument implements ActionListener {

    /**
     * Interface que les vues de saisie doivent implémenter
     */
    public interface DocumentSaisieVue {
        JTextField[] getChamps();
        JButton getBtnValider();
        JButton getBtnAnnuler();
        void setResultat(String[] res);
        void dispose();
    }

    private final DocumentSaisieVue vue;

    /**
     * Constructeur avec interface
     */
    public gestionSaisieDocument(DocumentSaisieVue vue) {
        this.vue = vue;
        vue.getBtnValider().addActionListener(this);
        vue.getBtnAnnuler().addActionListener(this);
    }

    /**
     * Constructeur alternatif pour compatibilité avec les anciennes vues
     * qui n'implémentent pas encore l'interface
     */
    public gestionSaisieDocument(JDialog dialog, JTextField[] champs, 
                                  JButton btnValider, JButton btnAnnuler,
                                  java.util.function.Consumer<String[]> setResultat) {
        this.vue = new DocumentSaisieVue() {
            @Override public JTextField[] getChamps() { return champs; }
            @Override public JButton getBtnValider() { return btnValider; }
            @Override public JButton getBtnAnnuler() { return btnAnnuler; }
            @Override public void setResultat(String[] res) { setResultat.accept(res); }
            @Override public void dispose() { dialog.dispose(); }
        };
        btnValider.addActionListener(this);
        btnAnnuler.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        
        // Gère tous les types de commandes VALIDER et ANNULER
        if (cmd != null && (cmd.contains("VALIDER") || cmd.equals("OK"))) {
            valider();
        } else if (cmd != null && (cmd.contains("ANNULER") || cmd.equals("CANCEL"))) {
            annuler();
        }
    }

    private void valider() {
        JTextField[] champs = vue.getChamps();
        String[] resultat = new String[champs.length];
        
        for (int i = 0; i < champs.length; i++) {
            resultat[i] = champs[i].getText().trim();
        }
        
        vue.setResultat(resultat);
        vue.dispose();
    }

    private void annuler() {
        vue.setResultat(null);
        vue.dispose();
    }
}
