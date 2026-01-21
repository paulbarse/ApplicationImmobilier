package Controleur.Connexion;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import vue.connexion.pageConnexionPrincipale;
import vue.connexion.pageImpossibeConnexion;

//Controleur pour la fenetre de gestion de connection impossible

public class gestionPageImpossibleConnexion implements WindowListener {

    public gestionPageImpossibleConnexion(pageImpossibeConnexion vue) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        pageConnexionPrincipale principale = new pageConnexionPrincipale();
        principale.setVisible(true);
    }
    
    // Pareil pas enlever
    @Override public void windowOpened(WindowEvent e) {}
    @Override public void windowClosed(WindowEvent e) {}
    @Override public void windowIconified(WindowEvent e) {}
    @Override public void windowDeiconified(WindowEvent e) {}
    @Override public void windowActivated(WindowEvent e) {}
    @Override public void windowDeactivated(WindowEvent e) {}
}
