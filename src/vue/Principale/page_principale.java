package vue.Principale;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import Controleur.Principale.gestionPagePrincipale;
import modele.dao.TableauBordDao;
import modele.dao.TableauBordDao.ActiviteRecente;
import modele.dao.TableauBordDao.ProprieteRecente;
import modele.dao.TableauBordDao.StatistiquesTableauBord;
import vue.refacto.UiUtils;

/**
 * Page principale - Tableau de bord Affiche les statistiques dynamiques depuis
 */
public class page_principale extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private gestionPagePrincipale gestion;

	// Labels pour les statistiques (pour mise a jour dynamique)
	private JLabel lblValeurProprietes;
	private JLabel lblVariationProprietes;
	private JLabel lblValeurLocataires;
	private JLabel lblVariationLocataires;
	private JLabel lblValeurContrats;
	private JLabel lblVariationContrats;
	private JLabel lblValeurRevenu;
	private JLabel lblVariationRevenu;

	// Panels pour les listes dynamiques
	private JPanel listDernieresProprietes;
	private JPanel listeActivites;

	// Formatage des montants
	private DecimalFormat formatMonetaire;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					page_principale frame = new page_principale();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public page_principale() {
		// Configuration du format monetaire
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
		symbols.setGroupingSeparator(' ');
		this.formatMonetaire = new DecimalFormat("#,##0", symbols);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 1225, 744);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.gestion = new gestionPagePrincipale(this);

		this.setContentPane(this.contentPane);
		this.contentPane.setLayout(new BorderLayout(0, 0));

		// BANDEAU HAUT
		JPanel encadrementHaut = UiUtils.createHeader(this.gestion, "PAGE_PRINCIPALE");
		this.contentPane.add(encadrementHaut, BorderLayout.NORTH);

		// CENTRE 
		JPanel centrePageAccueil = new JPanel();
		this.contentPane.add(centrePageAccueil, BorderLayout.CENTER);
		centrePageAccueil.setLayout(new BorderLayout(0, 0));

		// HEADER 
		JPanel panelHeader = new JPanel(new BorderLayout());
		panelHeader.setOpaque(false);
		panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 24, 8, 24));
		centrePageAccueil.add(panelHeader, BorderLayout.NORTH);

		// PARTIE GAUCHE 
		JPanel panelTextes = new JPanel();
		panelTextes.setLayout(new BoxLayout(panelTextes, BoxLayout.Y_AXIS));
		panelTextes.setOpaque(false);

		JLabel titreDeLaPage = new JLabel("Tableau de bord");
		titreDeLaPage.setFont(new Font("Verdana", Font.BOLD, 30));
		titreDeLaPage.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelTextes.add(titreDeLaPage);

		JLabel specificationDeLaPage = new JLabel("Vue d'ensemble de votre portefeuille immobilier");
		specificationDeLaPage.setFont(new Font("Verdana", Font.PLAIN, 13));
		specificationDeLaPage.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelTextes.add(specificationDeLaPage);

		panelHeader.add(panelTextes, BorderLayout.WEST);

		// PARTIE DROITE 
		JPanel panelBouton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panelBouton.setOpaque(false);

		JButton btnStatistiques = new JButton("Voir les Statistiques avancées");
		btnStatistiques.setFont(new Font("Verdana", Font.PLAIN, 12));
		btnStatistiques.setFocusPainted(false);
		btnStatistiques.setBackground(new Color(240, 240, 240));
		btnStatistiques.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

		// Configuration de l'action
		btnStatistiques.setActionCommand("OUVRIR_STATISTIQUES");
		btnStatistiques.addActionListener(this.gestion);

		panelBouton.add(btnStatistiques);

		// On ajoute le bouton à DROITE
		panelHeader.add(panelBouton, BorderLayout.EAST);

		// Panel principal scrollable
		JPanel panelPrincipal = new JPanel();
		panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
		panelPrincipal.setOpaque(false);

		JScrollPane scrollPane = new JScrollPane(panelPrincipal);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		centrePageAccueil.add(scrollPane, BorderLayout.CENTER);

		// CARTES STATISTIQUES
		JPanel panelCartes = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
		panelCartes.setOpaque(false);
		panelCartes.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));
		panelCartes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
		panelCartes.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelPrincipal.add(panelCartes);

		// Carte Proprietes
		JPanel carteProprietes = this.creerCarteStatistique("Proprietes", "/logoPropriete.png");
		this.lblValeurProprietes = this.getValeurLabel(carteProprietes);
		this.lblVariationProprietes = this.getVariationLabel(carteProprietes);
		panelCartes.add(carteProprietes);

		// Carte Locataires Actifs
		JPanel carteLocataires = this.creerCarteStatistique("Locataire Actif", "/logoLocataireActif.png");
		this.lblValeurLocataires = this.getValeurLabel(carteLocataires);
		this.lblVariationLocataires = this.getVariationLabel(carteLocataires);
		panelCartes.add(carteLocataires);

		// Carte Contrats en cours
		JPanel carteContrats = this.creerCarteStatistique("Contrat en cours", "/logoContratEnCour.png");
		this.lblValeurContrats = this.getValeurLabel(carteContrats);
		this.lblVariationContrats = this.getVariationLabel(carteContrats);
		panelCartes.add(carteContrats);

		// Carte Revenu Mensuel
		JPanel carteRevenu = this.creerCarteStatistique("Revenu Mensuel", "/revenuMensuel.png");
		this.lblValeurRevenu = this.getValeurLabel(carteRevenu);
		this.lblVariationRevenu = this.getVariationLabel(carteRevenu);
		panelCartes.add(carteRevenu);

		//  GRANDES CARTES
		JPanel panelGrandesCartes = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
		panelGrandesCartes.setOpaque(false);
		panelGrandesCartes.setBorder(BorderFactory.createEmptyBorder(0, 24, 24, 24));
		panelGrandesCartes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
		panelGrandesCartes.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelPrincipal.add(panelGrandesCartes);

		//Carte Total des proprietes 
		JPanel grandeCarteProprietes = new JPanel();
		grandeCarteProprietes.setLayout(new BorderLayout());
		grandeCarteProprietes.setPreferredSize(new Dimension(500, 350));
		grandeCarteProprietes.setBackground(new Color(245, 247, 252));
		grandeCarteProprietes.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
		panelGrandesCartes.add(grandeCarteProprietes);

		// Header de la carte
		JPanel hautCarteProprietes = new JPanel(new BorderLayout());
		hautCarteProprietes.setOpaque(false);
		hautCarteProprietes.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
		grandeCarteProprietes.add(hautCarteProprietes, BorderLayout.NORTH);

		JLabel titrePropRecentes = new JLabel("Dernieres proprietes");
		titrePropRecentes.setFont(new Font("Tahoma", Font.PLAIN, 20));
		try {
			Image imgProp = new ImageIcon(this.getClass().getResource("/logoPropriete.png")).getImage()
					.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
			titrePropRecentes.setIcon(new ImageIcon(imgProp));
		} catch (Exception e) {}
		titrePropRecentes.setIconTextGap(8);
		hautCarteProprietes.add(titrePropRecentes, BorderLayout.WEST);

		// Liste des proprietes
		this.listDernieresProprietes = new JPanel();
		this.listDernieresProprietes.setOpaque(false);
		this.listDernieresProprietes.setLayout(new BoxLayout(this.listDernieresProprietes, BoxLayout.Y_AXIS));
		this.listDernieresProprietes.setBorder(new EmptyBorder(8, 12, 8, 12));

		JScrollPane scrollProprietes = new JScrollPane(this.listDernieresProprietes);
		scrollProprietes.setBorder(null);
		scrollProprietes.setOpaque(false);
		scrollProprietes.getViewport().setOpaque(false);
		grandeCarteProprietes.add(scrollProprietes, BorderLayout.CENTER);

		// Carte Activite recente 
		JPanel grandeCarteActivite = new JPanel();
		grandeCarteActivite.setLayout(new BorderLayout());
		grandeCarteActivite.setPreferredSize(new Dimension(550, 350));
		grandeCarteActivite.setBackground(new Color(245, 247, 252));
		grandeCarteActivite.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
		panelGrandesCartes.add(grandeCarteActivite);

		// Header de la carte
		JPanel hautCarteActivite = new JPanel(new BorderLayout());
		hautCarteActivite.setOpaque(false);
		hautCarteActivite.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
		grandeCarteActivite.add(hautCarteActivite, BorderLayout.NORTH);

		JLabel titreActivite = new JLabel("Activite recente");
		titreActivite.setFont(new Font("Tahoma", Font.PLAIN, 20));
		try {
			Image imgAct = new ImageIcon(this.getClass().getResource("/activiteRecente.png")).getImage()
					.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
			titreActivite.setIcon(new ImageIcon(imgAct));
		} catch (Exception e) { }
		titreActivite.setIconTextGap(8);
		hautCarteActivite.add(titreActivite, BorderLayout.WEST);

		// Liste des activites
		this.listeActivites = new JPanel();
		this.listeActivites.setOpaque(false);
		this.listeActivites.setLayout(new BoxLayout(this.listeActivites, BoxLayout.Y_AXIS));
		this.listeActivites.setBorder(new EmptyBorder(8, 12, 8, 12));

		JScrollPane scrollActivites = new JScrollPane(this.listeActivites);
		scrollActivites.setBorder(null);
		scrollActivites.setOpaque(false);
		scrollActivites.getViewport().setOpaque(false);
		grandeCarteActivite.add(scrollActivites, BorderLayout.CENTER);

		// CHARGEMENT DES DONNEES
		this.chargerDonnees();
	}

	/**
	 * Cree une carte de statistique
	 */
	private JPanel creerCarteStatistique(String titre, String iconPath) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(200, 120));
		panel.setBackground(new Color(245, 247, 252));
		panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
		panel.setName("carte"); 

		// Haut de la carte (titre + icone)
		JPanel haut = new JPanel(new BorderLayout());
		haut.setOpaque(false);

		JLabel lblTitre = new JLabel(titre);
		lblTitre.setFont(new Font("Verdana", Font.PLAIN, 13));
		lblTitre.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
		haut.add(lblTitre, BorderLayout.WEST);

		JLabel lblIcon = new JLabel();
		try {
			lblIcon.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource(iconPath)).getImage()
					.getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
		} catch (Exception e) { }
		lblIcon.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 10));
		haut.add(lblIcon, BorderLayout.EAST);

		panel.add(haut, BorderLayout.NORTH);

		// Valeur (au centre)
		JLabel lblValeur = new JLabel("...");
		lblValeur.setName("valeur");
		lblValeur.setFont(new Font("Verdana", Font.BOLD, 28));
		lblValeur.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
		panel.add(lblValeur, BorderLayout.CENTER);

		// Variation (en bas)
		JLabel lblVariation = new JLabel("Chargement...");
		lblVariation.setName("variation");
		lblVariation.setFont(new Font("Verdana", Font.PLAIN, 12));
		lblVariation.setForeground(Color.GRAY);
		lblVariation.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
		panel.add(lblVariation, BorderLayout.SOUTH);

		return panel;
	}

	/**
	 * Recupere le label de valeur d'une carte
	 */
	private JLabel getValeurLabel(JPanel carte) {
		return (JLabel) carte.getComponent(1); // CENTER = index 1
	}

	/**
	 * Recupere le label de variation d'une carte
	 */
	private JLabel getVariationLabel(JPanel carte) {
		return (JLabel) carte.getComponent(2); // SOUTH = index 2
	}

	/**
	 * Charge toutes les donnees depuis la base de donnees
	 */
	public void chargerDonnees() {
		// Executer en arriere-plan pour ne pas bloquer l'UI
		new Thread(() -> {
			try {
				TableauBordDao dao = new TableauBordDao();

				// Charger les statistiques
				final StatistiquesTableauBord stats = dao.getStatistiques();

				// Charger les 5 dernieres proprietes
				final List<ProprieteRecente> proprietes = dao.getToutesProprietes(5);

				// Charger les activites recentes
				final List<ActiviteRecente> activites = dao.getActivitesRecentes(10);

				dao.close();

				SwingUtilities.invokeLater(() -> {
					this.mettreAJourStatistiques(stats);
					this.mettreAJourProprietes(proprietes);
					this.mettreAJourActivitesRecentes(activites);
				});

			} catch (Exception e) {
				e.printStackTrace();
				// Afficher des valeurs par defaut en cas d'erreur
				SwingUtilities.invokeLater(() -> {
					this.lblValeurProprietes.setText("0");
					this.lblVariationProprietes.setText("Erreur BDD");
					this.lblValeurLocataires.setText("0");
					this.lblVariationLocataires.setText("Erreur BDD");
					this.lblValeurContrats.setText("0");
					this.lblVariationContrats.setText("Erreur BDD");
					this.lblValeurRevenu.setText("0 EUR");
					this.lblVariationRevenu.setText("Erreur BDD");
				});
			}
		}).start();
	}

	/**
	 * Met a jour les labels des statistiques
	 */
	private void mettreAJourStatistiques(StatistiquesTableauBord stats) {
		// Proprietes
		this.lblValeurProprietes.setText(String.valueOf(stats.getNbProprietes()));
		this.lblVariationProprietes.setText(stats.getVariationProprietes());
		this.colorerVariation(this.lblVariationProprietes, stats.getVariationProprietes());

		// Locataires
		this.lblValeurLocataires.setText(String.valueOf(stats.getNbLocatairesActifs()));
		this.lblVariationLocataires.setText(stats.getVariationLocataires());
		this.colorerVariation(this.lblVariationLocataires, stats.getVariationLocataires());

		// Contrats
		this.lblValeurContrats.setText(String.valueOf(stats.getNbContratsEnCours()));
		this.lblVariationContrats.setText(stats.getVariationContrats());
		this.colorerVariation(this.lblVariationContrats, stats.getVariationContrats());

		// Revenu
		this.lblValeurRevenu.setText(this.formatMonetaire.format(stats.getRevenuMensuel()) + " EUR");
		this.lblVariationRevenu.setText(stats.getVariationRevenu());
		this.colorerVariation(this.lblVariationRevenu, stats.getVariationRevenu());
	}

	/**
	 * Colore le label de variation en fonction du texte
	 */
	private void colorerVariation(JLabel label, String texte) {
		if (texte == null) {
			label.setForeground(Color.GRAY);
		} else if (texte.startsWith("+")) {
			label.setForeground(new Color(34, 139, 34)); // Vert
		} else if (texte.startsWith("-")) {
			label.setForeground(new Color(220, 53, 69)); // Rouge
		} else {
			label.setForeground(Color.GRAY);
		}
	}

	/**
	 * Met a jour la liste des proprietes
	 */
	private void mettreAJourProprietes(List<ProprieteRecente> proprietes) {
		this.listDernieresProprietes.removeAll();

		if (proprietes.isEmpty()) {
			JLabel lblVide = new JLabel("Aucune propriete recente");
			lblVide.setFont(new Font("SansSerif", Font.ITALIC, 12));
			lblVide.setForeground(Color.GRAY);
			this.listDernieresProprietes.add(lblVide);
		} else {
			boolean first = true;
			for (ProprieteRecente prop : proprietes) {
				if (!first) {
					JSeparator sep = new JSeparator();
					sep.setForeground(new Color(220, 220, 220));
					sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
					this.listDernieresProprietes.add(sep);
				}
				this.listDernieresProprietes.add(this.creerLignePropriete(prop));
				first = false;
			}
		}

		this.listDernieresProprietes.revalidate();
		this.listDernieresProprietes.repaint();
	}

	/**
	 * Cree une ligne pour une propriete
	 */
	private JPanel creerLignePropriete(ProprieteRecente prop) {
		JPanel ligne = new JPanel(new BorderLayout());
		ligne.setOpaque(false);
		ligne.setBorder(new EmptyBorder(8, 4, 8, 4));
		ligne.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

		// Gauche : adresse et type
		JPanel gauche = new JPanel();
		gauche.setOpaque(false);
		gauche.setLayout(new BoxLayout(gauche, BoxLayout.Y_AXIS));

		JLabel lblAdresse = new JLabel(prop.getAdresse() != null ? prop.getAdresse() : "Adresse non definie");
		lblAdresse.setFont(new Font("SansSerif", Font.BOLD, 13));
		try {
			lblAdresse.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/logoPropriete.png")).getImage()
					.getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		} catch (Exception e) { }
		lblAdresse.setIconTextGap(8);

		String sousTexte = prop.getSousType() != null ? prop.getSousType() : "";
		if (prop.getLocataire() != null && !prop.getLocataire().isEmpty()) {
			sousTexte += " - " + prop.getLocataire();
		}
		JLabel lblSousTexte = new JLabel(sousTexte);
		lblSousTexte.setFont(new Font("SansSerif", Font.PLAIN, 12));
		lblSousTexte.setForeground(Color.GRAY);

		gauche.add(lblAdresse);
		gauche.add(lblSousTexte);

		// Droite : prix et statut
		JPanel droite = new JPanel();
		droite.setOpaque(false);
		droite.setLayout(new BoxLayout(droite, BoxLayout.Y_AXIS));

		String textePrix = prop.getLoyer() != null ? "EUR " + this.formatMonetaire.format(prop.getLoyer()) : "N/A";
		JLabel lblPrix = new JLabel(textePrix);
		lblPrix.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblPrix.setAlignmentX(Component.RIGHT_ALIGNMENT);

		JLabel lblStatut = new JLabel(prop.getStatut(), SwingConstants.CENTER);
		lblStatut.setFont(new Font("SansSerif", Font.BOLD, 11));
		lblStatut.setOpaque(true);
		if (prop.isOccupe()) {
			lblStatut.setBackground(new Color(30, 102, 245));
			lblStatut.setForeground(Color.WHITE);
		} else {
			lblStatut.setBackground(new Color(200, 200, 200));
			lblStatut.setForeground(Color.DARK_GRAY);
		}
		lblStatut.setBorder(new EmptyBorder(3, 10, 3, 10));
		lblStatut.setAlignmentX(Component.RIGHT_ALIGNMENT);

		droite.add(lblPrix);
		droite.add(Box.createVerticalStrut(6));
		droite.add(lblStatut);

		ligne.add(gauche, BorderLayout.WEST);
		ligne.add(droite, BorderLayout.EAST);

		return ligne;
	}

	/**
	 * Met a jour la liste des activites recentes
	 */
	private void mettreAJourActivitesRecentes(List<ActiviteRecente> activites) {
		this.listeActivites.removeAll();

		if (activites.isEmpty()) {
			JLabel lblVide = new JLabel("Aucune activite recente");
			lblVide.setFont(new Font("SansSerif", Font.ITALIC, 12));
			lblVide.setForeground(Color.GRAY);
			this.listeActivites.add(lblVide);
		} else {
			boolean first = true;
			for (ActiviteRecente act : activites) {
				if (!first) {
					JSeparator sep = new JSeparator();
					sep.setForeground(new Color(220, 220, 220));
					sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
					this.listeActivites.add(sep);
				}
				this.listeActivites.add(this.creerLigneActivite(act));
				first = false;
			}
		}

		this.listeActivites.revalidate();
		this.listeActivites.repaint();
	}

	/**
	 * Cree une ligne pour une activite
	 */
	private JPanel creerLigneActivite(ActiviteRecente act) {
		JPanel ligne = new JPanel(new BorderLayout());
		ligne.setOpaque(false);
		ligne.setBorder(new EmptyBorder(8, 4, 8, 4));
		ligne.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

		JPanel gauche = new JPanel();
		gauche.setOpaque(false);
		gauche.setLayout(new BoxLayout(gauche, BoxLayout.Y_AXIS));

		String description = act.getDescription() != null ? act.getDescription() : "Activite";
		JLabel lblDescription = new JLabel(description);
		lblDescription.setFont(new Font("SansSerif", Font.BOLD, 13));
		try {
			lblDescription.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/" + act.getIconName()))
					.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		} catch (Exception e) { }
		lblDescription.setIconTextGap(8);

		String tempsEcoule = act.getTempsEcoule() != null ? act.getTempsEcoule() : "";
		JLabel lblTemps = new JLabel(tempsEcoule);
		lblTemps.setFont(new Font("SansSerif", Font.PLAIN, 12));
		lblTemps.setForeground(Color.GRAY);

		gauche.add(lblDescription);
		gauche.add(lblTemps);

		ligne.add(gauche, BorderLayout.WEST);

		return ligne;
	}
	
	

	/**
	 * Rafraichit les donnees du tableau de bord
	 */
	public void rafraichir() {
		this.chargerDonnees();
	}
}