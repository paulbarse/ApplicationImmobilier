package Controleur.Connexion;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import vue.connexion.pageConnexion;
import vue.connexion.pageConnexionPrincipale;
import vue.connexion.pageImpossibeConnexion;

//Controleur pour la fenetre de gestion de connection principal

public class gestionPageConnexionPrincipale implements ActionListener {

    private final pageConnexionPrincipale vue;

    public gestionPageConnexionPrincipale(pageConnexionPrincipale vue) {
        this.vue = vue;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton item = (JButton) e.getSource();
        String texte = item.getText();

        switch (texte) {
            case "CONNEXION JOPAWIS":
                vue.dispose();
                pageConnexion fen = new pageConnexion();
                fen.setVisible(true);
                break;

            case "Impossible de se connecter ?":
                vue.dispose();
                pageImpossibeConnexion fen1 = new pageImpossibeConnexion();
                fen1.setVisible(true);
                break;

            default:
        }
    }
}
