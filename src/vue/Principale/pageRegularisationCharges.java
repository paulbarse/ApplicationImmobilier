package vue.Principale;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Controleur.Principale.gestionRegularisationCharges;
import vue.refacto.UiUtils;

/**
 * Page de régularisation des charges - VERSION AUTONOME
 */
public class pageRegularisationCharges extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	// RECAP IMMEUBLE (charges totales de l'immeuble)
	private JTextField txtTotalFactureEau;
	private JTextField txtTotalOrduresMenageres;
	private JTextField txtTotalElectricite;
	private JTextField txtTotalEntretien;
	private JTextField txtTotalChargesImmeuble;
	
	// Label d'avertissement pour charges manquantes
	private JLabel lblAvertissementCharges;

	//  RECAP LOCATAIRE 
	private JTextField txtNbMoisOccupation;
	private JTextField txtEauLocation;
	private JTextField txtOrdureMenagere;
	private JTextField txtElectriciteLocation;
	private JTextField txtEntretien;
	private JTextField txtTotalCharge;
	private JTextField txtProvisionVersee;
	private JTextField txtResteACharge;
	private JTextField txtNouvelleProvision;

	// Table
	private JTable tableRecapLocataires;
	private JComboBox<Integer> comboAnnee;

	private gestionRegularisationCharges gestion;

	public static void main(String[] args) {
		pageRegularisationCharges dlg = new pageRegularisationCharges();
		dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dlg.setVisible(true);
	}

	public pageRegularisationCharges() {
		this.gestion = new gestionRegularisationCharges(this);

		this.setTitle("Régularisation des charges");
		this.setBounds(100, 100, 1225, 744);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(this.contentPane);
		this.contentPane.setLayout(new BorderLayout(0, 0));

		// BANDEAU HAUT
		JPanel encadrementHaut = UiUtils.createHeader(this.gestion, "REGU_CHARGES");
		this.contentPane.add(encadrementHaut, BorderLayout.NORTH);

		//CENTRE
		JPanel centrePage = new JPanel(new BorderLayout());
		this.contentPane.add(centrePage, BorderLayout.CENTER);

		// Titre avec sélecteur d'année
		JPanel panelHeader = new JPanel(new BorderLayout());
		panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 24, 8, 24));

		JLabel titrePage = new JLabel("Régularisation des charges");
		titrePage.setFont(new Font("Verdana", Font.BOLD, 26));
		panelHeader.add(titrePage, BorderLayout.WEST);

		// Sélecteur d'année
		JPanel panelAnnee = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel lblAnnee = new JLabel("Année : ");
		lblAnnee.setFont(new Font("Verdana", Font.PLAIN, 14));

		int anneeActuelle = Calendar.getInstance().get(Calendar.YEAR);
		Integer[] annees = new Integer[10];
		for (int i = 0; i < 10; i++) {
			annees[i] = anneeActuelle - i;
		}
		this.comboAnnee = new JComboBox<>(annees);
		this.comboAnnee.setFont(new Font("Verdana", Font.PLAIN, 14));
		this.comboAnnee.addActionListener(e -> {
			if (this.gestion != null) {
				this.gestion.setAnnee((Integer) this.comboAnnee.getSelectedItem());
			}
		});

		panelAnnee.add(lblAnnee);
		panelAnnee.add(this.comboAnnee);
		panelHeader.add(panelAnnee, BorderLayout.EAST);

		centrePage.add(panelHeader, BorderLayout.NORTH);

		// PANELS CHARGES
		JPanel panelFormes = new JPanel(new GridLayout(1, 2, 20, 0));
		panelFormes.setBorder(new EmptyBorder(10, 24, 10, 24));

		// RECAP IMMEUBLE 
		JPanel panelImmeuble = new JPanel(new BorderLayout());
		panelImmeuble.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200)), 
				"Charges de l'immeuble (récupérées depuis Documents Bâtiment)"));

		JPanel gridImmeuble = new JPanel(new GridLayout(6, 2, 10, 8));
		gridImmeuble.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Eau
		this.txtTotalFactureEau = this.createReadOnlyField();
		gridImmeuble.add(new JLabel("Eau :"));
		gridImmeuble.add(this.txtTotalFactureEau);

		// Ordures ménagères
		this.txtTotalOrduresMenageres = this.createReadOnlyField();
		gridImmeuble.add(new JLabel("Ordures ménagères :"));
		gridImmeuble.add(this.txtTotalOrduresMenageres);

		// Électricité parties communes
		this.txtTotalElectricite = this.createReadOnlyField();
		gridImmeuble.add(new JLabel("Électricité :"));
		gridImmeuble.add(this.txtTotalElectricite);

		// Entretien
		this.txtTotalEntretien = this.createReadOnlyField();
		gridImmeuble.add(new JLabel("Entretien :"));
		gridImmeuble.add(this.txtTotalEntretien);

		// Total immeuble
		this.txtTotalChargesImmeuble = this.createReadOnlyField();
		this.txtTotalChargesImmeuble.setFont(new Font("SansSerif", Font.BOLD, 12));
		this.txtTotalChargesImmeuble.setBackground(new Color(230, 230, 250));
		gridImmeuble.add(new JLabel("TOTAL IMMEUBLE :"));
		gridImmeuble.add(this.txtTotalChargesImmeuble);
		
		// Label d'avertissement 
		this.lblAvertissementCharges = new JLabel("");
		this.lblAvertissementCharges.setFont(new Font("SansSerif", Font.ITALIC, 11));
		this.lblAvertissementCharges.setForeground(new Color(255, 140, 0)); // Orange
		gridImmeuble.add(new JLabel("")); // Espace vide
		gridImmeuble.add(this.lblAvertissementCharges);

		panelImmeuble.add(gridImmeuble, BorderLayout.CENTER);
		panelFormes.add(panelImmeuble);

		// RECAP LOCATAIRE 
		JPanel panelLocation = new JPanel(new BorderLayout());
		panelLocation
				.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),
						"Quote-part locataire (sélectionner dans le tableau)"));

		JPanel gridLocation = new JPanel(new GridLayout(9, 2, 10, 6));
		gridLocation.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Mois d'occupation
		this.txtNbMoisOccupation = this.createReadOnlyField();
		gridLocation.add(new JLabel("Mois d'occupation :"));
		gridLocation.add(this.txtNbMoisOccupation);

		// Eau location
		this.txtEauLocation = this.createReadOnlyField();
		gridLocation.add(new JLabel("Eau :"));
		gridLocation.add(this.txtEauLocation);

		// Ordures ménagères
		this.txtOrdureMenagere = this.createReadOnlyField();
		gridLocation.add(new JLabel("Ordures ménagères :"));
		gridLocation.add(this.txtOrdureMenagere);

		// Électricité
		this.txtElectriciteLocation = this.createReadOnlyField();
		gridLocation.add(new JLabel("Électricité :"));
		gridLocation.add(this.txtElectriciteLocation);

		// Entretien
		this.txtEntretien = this.createReadOnlyField();
		gridLocation.add(new JLabel("Entretien :"));
		gridLocation.add(this.txtEntretien);

		// Total charges locataire
		this.txtTotalCharge = this.createReadOnlyField();
		this.txtTotalCharge.setBackground(new Color(255, 255, 200));
		this.txtTotalCharge.setFont(new Font("SansSerif", Font.BOLD, 12));
		gridLocation.add(new JLabel("TOTAL CHARGES :"));
		gridLocation.add(this.txtTotalCharge);

		// Provisions versées
		this.txtProvisionVersee = this.createReadOnlyField();
		gridLocation.add(new JLabel("Provisions versées :"));
		gridLocation.add(this.txtProvisionVersee);

		// Reste à charge (régularisation)
		this.txtResteACharge = this.createReadOnlyField();
		this.txtResteACharge.setFont(new Font("SansSerif", Font.BOLD, 13));
		gridLocation.add(new JLabel("RÉGULARISATION :"));
		gridLocation.add(this.txtResteACharge);

		// Nouvelle provision proposée
		this.txtNouvelleProvision = this.createReadOnlyField();
		this.txtNouvelleProvision.setBackground(new Color(230, 255, 230));
		gridLocation.add(new JLabel("Nouvelle provision :"));
		gridLocation.add(this.txtNouvelleProvision);

		panelLocation.add(gridLocation, BorderLayout.CENTER);
		panelFormes.add(panelLocation);

		// TABLE DES LOCATAIRES
		JPanel panelBas = new JPanel(new BorderLayout());
		panelBas.setBorder(new EmptyBorder(0, 24, 10, 24));

		// Split entre les formulaires et la table
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setTopComponent(panelFormes);
		split.setBottomComponent(panelBas);
		split.setResizeWeight(0.40);
		split.setDividerSize(6);

		centrePage.add(split, BorderLayout.CENTER);

		// Header de la table 
		JPanel headerTable = new JPanel(new BorderLayout());
		JLabel lblTable = new JLabel("Locataires - Sélectionnez une ligne pour voir la quote-part");
		lblTable.setFont(new Font("Tahoma", Font.PLAIN, 14));
		headerTable.add(lblTable, BorderLayout.WEST);
		
		// Note informative à droite 
		JLabel lblInfo = new JLabel("Les charges sont récupérées depuis Documents > Bâtiment > Charges");
		lblInfo.setFont(new Font("Tahoma", Font.ITALIC, 11));
		lblInfo.setForeground(new Color(100, 100, 100));
		headerTable.add(lblInfo, BorderLayout.EAST);
		
		panelBas.add(headerTable, BorderLayout.NORTH);

		// Tableau des locataires
		String[] colonnes = { "Locataire", "Logement", "Mois occup.", "Charges", "Provisions", "Régularisation" };
		DefaultTableModel modelTable = new DefaultTableModel(colonnes, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		this.tableRecapLocataires = new JTable(modelTable);
		this.tableRecapLocataires.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tableRecapLocataires.getTableHeader().setReorderingAllowed(false);
		this.tableRecapLocataires.setRowHeight(25);

		JScrollPane scrollTable = new JScrollPane(this.tableRecapLocataires);
		panelBas.add(scrollTable, BorderLayout.CENTER);
		this.tableRecapLocataires.getSelectionModel().addListSelectionListener(this.gestion);

		// Légende
		JPanel panelLegende = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel lblLegende = new JLabel("Légende : ");
		JLabel lblPositif = new JLabel("Rouge = locataire doit payer");
		lblPositif.setForeground(new Color(220, 53, 69));
		JLabel lblNegatif = new JLabel("   Vert = remboursement au locataire");
		lblNegatif.setForeground(new Color(34, 139, 34));
		panelLegende.add(lblLegende);
		panelLegende.add(lblPositif);
		panelLegende.add(lblNegatif);
		panelBas.add(panelLegende, BorderLayout.SOUTH);
	}

	/**
	 * Crée un champ de texte en lecture seule
	 */
	private JTextField createReadOnlyField() {
		JTextField field = new JTextField();
		field.setEditable(false);
		field.setBackground(new Color(245, 245, 245));
		field.setFont(new Font("SansSerif", Font.PLAIN, 12));
		return field;
	}

	// GETTERS 

	public JTable getTableRecapImpots() {
		return this.tableRecapLocataires;
	}

	public JComboBox<Integer> getComboAnnee() {
		return this.comboAnnee;
	}
	
	/**
	 * Retourne le label d'avertissement pour les charges manquantes
	 */
	public JLabel getLblAvertissementCharges() {
		return this.lblAvertissementCharges;
	}

	//  Charges immeuble 
	public JTextField getTxtTotalFactureEau() {
		return this.txtTotalFactureEau;
	}

	public JTextField getTxtTotalOrduresMenageres() {
		return this.txtTotalOrduresMenageres;
	}

	public JTextField getTxtTotalElectricite() {
		return this.txtTotalElectricite;
	}

	public JTextField getTxtTotalEntretien() {
		return this.txtTotalEntretien;
	}

	public JTextField getTxtTotalChargesImmeuble() {
		return this.txtTotalChargesImmeuble;
	}

	//  Quote-part locataire 
	public JTextField getTxtNbMoisOccupation() {
		return this.txtNbMoisOccupation;
	}

	public JTextField getTxtLocationEau() {
		return this.txtEauLocation;
	}

	public JTextField getTxtOrdureMenagere() {
		return this.txtOrdureMenagere;
	}

	public JTextField getTxtElectriciteLocation() {
		return this.txtElectriciteLocation;
	}

	public JTextField getTxtEntretien() {
		return this.txtEntretien;
	}

	public JTextField getTxtTotalCharge() {
		return this.txtTotalCharge;
	}

	public JTextField getTxtProvisionSurCharge() {
		return this.txtProvisionVersee;
	}

	public JTextField getTxtResteACharge() {
		return this.txtResteACharge;
	}

	public JTextField getTxtProvisionNouvelle() {
		return this.txtNouvelleProvision;
	}

	
	// retournent null car les champs ont été supprimé
	public JTextField getTxtTotalPartieFixe() {
		return null;
	}

	public JTextField getTxtTotalPartieVariable() {
		return null;
	}

	public JTextField getTxtTotalConsoEau() {
		return null;
	}

	public JTextField getTxtPrixMoyenConsoEau() {
		return null;
	}

	public JTextField getTxtTotalElectriciteImmeuble() {
		return this.txtTotalElectricite;
	}

	public JTextField getTxtConsoEauLocation() {
		return null;
	}

	public JTextField getTxtPrixConsoEauLocation() {
		return null;
	}

	public JTextField getTxtQuotiteJurisprudence() {
		return null;
	}

	public JTextField getTxtMontantQuotite() {
		return null;
	}

	public JTextField getTxtAugmentationAssurance() {
		return null;
	}

	public JTextField getTxtMontantTravauxDeclarer() {
		return null;
	}
}
