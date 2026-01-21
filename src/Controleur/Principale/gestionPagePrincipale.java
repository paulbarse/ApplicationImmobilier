package Controleur.Principale;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Controleur.refacto.NavigationHelper;
import vue.Principale.pageStatistiques;
import vue.Principale.page_principale;

// Contrôleur de la page principale (Tableau de bord) Gère les événements et la navigation

public class gestionPagePrincipale implements ActionListener {

	private final page_principale vue;

	public gestionPagePrincipale(page_principale vue) {
		this.vue = vue;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		switch (cmd) {

		case "OUVRIR_STATISTIQUES": {
			pageStatistiques dlgStats = new pageStatistiques(this.vue);
			dlgStats.setLocationRelativeTo(this.vue);
			dlgStats.setVisible(true);
			break;
		}

		case "RAFRAICHIR_TABLEAU_BORD": {
			// Rafraîchir les données du tableau de bord
			this.rafraichirTableauBord();
			break;
		}

		default:
			// Toute la navigation générique (listes, baux, régul, compte, etc.)
			NavigationHelper.handleNavigation(this.vue, cmd);
			break;
		}
	}


	public void rafraichirTableauBord() {
		this.vue.rafraichir();
	}
}
