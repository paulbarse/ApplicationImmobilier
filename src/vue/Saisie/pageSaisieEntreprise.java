package vue.Saisie;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;

import Controleur.Saisie.gestionSaisieEntreprise;
import modele.Entreprise;
import vue.refacto.AbstractSaisieDialog;

/**
 * Page de saisie pour créer ou modifier une entreprise
 */
public class pageSaisieEntreprise extends AbstractSaisieDialog {

	private static final long serialVersionUID = 1L;

	// Champs du formulaire
	private JTextField txtSiret;
	private JTextField txtNom;
	private JTextField txtSpecialite;
	private JTextField txtTelephone;
	private JTextField txtEmail;
	private JTextField txtAdresse;

	private gestionSaisieEntreprise gestion;

	// Mode modification
	private boolean modeModification = false;

	// Résultat
	private Entreprise entrepriseCree = null;

	public pageSaisieEntreprise() {
		super("Nouvelle entreprise", "Ajouter une entreprise");

		this.setModal(true); 
		this.setAlwaysOnTop(true);

		this.gestion = new gestionSaisieEntreprise(this);

		// Layout du formulaire
		this.formPanel.setLayout(new GridLayout(0, 2, 10, 10));

		// SIRET (obligatoire) 
		JLabel lblSiret = new JLabel("SIRET (14 chiffres) * :");
		this.txtSiret = new JTextField();

		// Nom de l'entreprise (obligatoire)
		JLabel lblNom = new JLabel("Nom de l'entreprise * :");
		this.txtNom = new JTextField();

		//  Spécialité 
		JLabel lblSpecialite = new JLabel("Spécialité :");
		this.txtSpecialite = new JTextField();

		//  Téléphone 
		JLabel lblTelephone = new JLabel("Téléphone :");
		this.txtTelephone = new JTextField();

		//  Email
		JLabel lblEmail = new JLabel("Email :");
		this.txtEmail = new JTextField();

		//  Adresse 
		JLabel lblAdresse = new JLabel("Adresse :");
		this.txtAdresse = new JTextField();

		// Ajout au panel
		this.formPanel.add(lblSiret);
		this.formPanel.add(this.txtSiret);

		this.formPanel.add(lblNom);
		this.formPanel.add(this.txtNom);

		this.formPanel.add(lblSpecialite);
		this.formPanel.add(this.txtSpecialite);

		this.formPanel.add(lblTelephone);
		this.formPanel.add(this.txtTelephone);

		this.formPanel.add(lblEmail);
		this.formPanel.add(this.txtEmail);

		this.formPanel.add(lblAdresse);
		this.formPanel.add(this.txtAdresse);

		// Boutons
		this.btnValider.setText("Enregistrer");
		this.btnValider.setActionCommand("VALIDER_ENTREPRISE");
		this.btnAnnuler.setActionCommand("ANNULER_ENTREPRISE");
		this.btnValider.addActionListener(this.gestion);
		this.btnAnnuler.addActionListener(this.gestion);

		this.finalizeLayout();
	}

	/**
	 * Remplit le formulaire avec les données d'une entreprise existante (mode
	 * modification)
	 */
	public void remplirDepuis(Entreprise e) {
		this.modeModification = true;
		this.setTitle("Modifier l'entreprise");

		this.txtSiret.setText(String.valueOf(e.getSiret()));
		this.txtSiret.setEditable(false); // Le SIRET ne peut pas être modifié

		this.txtNom.setText(e.getNomEntreprise());
		this.txtSpecialite.setText(e.getSpecialite() != null ? e.getSpecialite() : "");
		this.txtTelephone.setText(e.getTelephone() != null ? e.getTelephone() : "");
		this.txtEmail.setText(e.getEmail() != null ? e.getEmail() : "");
		this.txtAdresse.setText(e.getAdresse() != null ? e.getAdresse() : "");
	}

	//GETTERS

	public JTextField getTxtSiret() {
		return this.txtSiret;
	}

	public JTextField getTxtNom() {
		return this.txtNom;
	}

	public JTextField getTxtSpecialite() {
		return this.txtSpecialite;
	}

	public JTextField getTxtTelephone() {
		return this.txtTelephone;
	}

	public JTextField getTxtEmail() {
		return this.txtEmail;
	}

	public JTextField getTxtAdresse() {
		return this.txtAdresse;
	}

	public boolean isModeModification() {
		return this.modeModification;
	}

	public Entreprise getEntrepriseCree() {
		return this.entrepriseCree;
	}

	public void setEntrepriseCree(Entreprise entrepriseCree) {
		this.entrepriseCree = entrepriseCree;
	}
}