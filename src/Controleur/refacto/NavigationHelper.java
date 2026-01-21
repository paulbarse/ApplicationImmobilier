package Controleur.refacto;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;

import vue.Principale.pageGestionDesBaux;
import vue.Principale.pageListeDesBatiments;
import vue.Principale.pageListeDesEntreprises;
import vue.Principale.pageListeDesGarages;
import vue.Principale.pageListeDesLocataires;
import vue.Principale.pageListeDesLogements;
import vue.Principale.pageRegularisationCharges;
import vue.Principale.pageStatistiques;
import vue.Principale.page_principale;
import vue.Profil.pageCompte;
import vue.Profil.pageParam;

public final class NavigationHelper {

	private NavigationHelper() {
		// utilitaire, pas d'instance
	}

	// Méthode principale appelée par les contrôleurs
	public static void handleNavigation(Component current, String cmd) {

		switch (cmd) {

		case "OUVRIR_LISTE_LOGEMENTS":
			openDialogAndDispose(current, new pageListeDesLogements());
			break;

		case "OUVRIR_LISTE_LOCATAIRES":
			openDialogAndDispose(current, new pageListeDesLocataires());
			break;

		case "OUVRIR_LISTE_BAT":
			openDialogAndDispose(current, new pageListeDesBatiments());
			break;

		case "OUVRIR_LISTE_GARAGES":
			openDialogAndDispose(current, new pageListeDesGarages());
			break;

		case "OUVRIR_GESTION_BAILS":
			openDialogAndDispose(current, new pageGestionDesBaux());
			break;

		case "OUVRIR_REGU_CHARGE":
			openDialogAndDispose(current, new pageRegularisationCharges());
			break;

		case "OUVRIR_PAGE_PRINCIPALE":
			openFrameAndDispose(current, new page_principale());
			break;

		case "OUVRIR_LISTE_ENTREPRISES":
			openDialogAndDispose(current, new pageListeDesEntreprises());
			break;

		case "OUVRIR_PAGE_STAT": {
			Window parent = (current instanceof Window) ? (Window) current : null;
			pageStatistiques dlgStat = new pageStatistiques(parent);
			dlgStat.setLocationRelativeTo(current);
			dlgStat.setVisible(true);
			break;
		}

		case "OUVRIR_GESTION_PARAM": {
			pageParam dlgParam = new pageParam();
			dlgParam.setLocationRelativeTo(current);
			dlgParam.setVisible(true);
			break;
		}

		case "OUVRIR_GESTION_COMPTE": {
			pageCompte dlgCompte = new pageCompte();
			dlgCompte.setLocationRelativeTo(current);
			dlgCompte.setVisible(true);
			break;
		}

		default:

			break;
		}
	}



	private static void openDialogAndDispose(Component current, JDialog next) {
		next.setLocationRelativeTo(current);
		if (current instanceof Window) {
			((Window) current).dispose();
		}
		next.setVisible(true);
	}

	private static void openFrameAndDispose(Component current, JFrame next) {
		next.setLocationRelativeTo(current);
		if (current instanceof Window) {
			((Window) current).dispose();
		}
		next.setVisible(true);
	}
}