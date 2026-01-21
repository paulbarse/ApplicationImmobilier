package vue.Saisie;

import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import Controleur.Saisie.gestionSaisieBatiment;
import vue.refacto.AbstractSaisieDialog;

/**
 * Formulaire de saisie d'un batiment 

 */
public class pageSaisieBatiment extends AbstractSaisieDialog {

	private static final long serialVersionUID = 1L;

	private JTextField txtIdBatiment;
	private JTextField txtAdresseComplete;
	private JTextField txtNbEtages;
	private JComboBox<String> cbType;
	private JTextField txtVille; 
	private JTextField txtCodePostal;

	private gestionSaisieBatiment gestion;
	private String[] resultat;

	public pageSaisieBatiment() {
		super("Saisie d'un batiment", "Veuillez renseigner les informations du batiment");

		this.setModal(true);
		this.setAlwaysOnTop(true);

		this.gestion = new gestionSaisieBatiment(this);

		// Mise en forme du formulaire
		this.formPanel.setLayout(new GridLayout(0, 2, 10, 10));

		JLabel lblId = new JLabel("ID batiment :");
		this.txtIdBatiment = new JTextField();

		JLabel lblAdresse = new JLabel("Adresse complete :");
		this.txtAdresseComplete = new JTextField();

		JLabel lblNbEtages = new JLabel("Nombre d'etages :");
		this.txtNbEtages = new JTextField();

		JLabel lblType = new JLabel("Type :");

		this.cbType = new JComboBox<>(new String[] { "MAISON", "IMMEUBLE", "GARAGE" });
		this.cbType.setSelectedIndex(0);


		JLabel lblVille = new JLabel("Ville * :");
		this.txtVille = new JTextField();

		JLabel lblCP = new JLabel("Code postal :");
		this.txtCodePostal = new JTextField();

		this.formPanel.add(lblId);
		this.formPanel.add(this.txtIdBatiment);

		this.formPanel.add(lblAdresse);
		this.formPanel.add(this.txtAdresseComplete);

		this.formPanel.add(lblNbEtages);
		this.formPanel.add(this.txtNbEtages);

		this.formPanel.add(lblType);
		this.formPanel.add(this.cbType);


		this.formPanel.add(lblVille);
		this.formPanel.add(this.txtVille);

		this.formPanel.add(lblCP);
		this.formPanel.add(this.txtCodePostal);

		// Boutons
		this.btnValider.setActionCommand("VALIDER_BATIMENT");
		this.btnAnnuler.setActionCommand("ANNULER_BATIMENT");
		this.btnValider.addActionListener(this.gestion);
		this.btnAnnuler.addActionListener(this.gestion);

		this.finalizeLayout();
	}

	/**
	 * Pre-remplissage pour modification
	 * data[0] = id, data[1] = adresse, data[2] = nbEtages, data[3] = type, 
	 * data[4] = ville, data[5] = codePostal
	 */
	public void remplirDepuis(String[] data) {
		if (data == null) {
			return;
		}

		if (data.length > 0) {
			this.txtIdBatiment.setText(data[0]);
		}
		if (data.length > 1) {
			this.txtAdresseComplete.setText(data[1]);
		}
		if (data.length > 2) {
			this.txtNbEtages.setText(data[2]);
		}

		if (data.length > 3 && data[3] != null) {
			String t = data[3].trim().toUpperCase();

			if ("MAISON".equals(t) || "IMMEUBLE".equals(t) || "GARAGE".equals(t)) {
				this.cbType.setSelectedItem(t);
			} else {
				this.cbType.setSelectedIndex(0);
			}
		}

		if (data.length > 4) {
			this.txtVille.setText(data[4]);
		}
		if (data.length > 5) {
			this.txtCodePostal.setText(data[5]);
		}
	}

	// GETTERS / SETTERS

	public String[] getResultat() {
		return this.resultat;
	}

	public void setResultat(String[] resultat) {
		this.resultat = resultat;
	}

	public JTextField getTxtIdBatiment() {
		return this.txtIdBatiment;
	}

	public JTextField getTxtAdresseComplete() {
		return this.txtAdresseComplete;
	}

	public JTextField getTxtNbEtages() {
		return this.txtNbEtages;
	}

	public JComboBox<String> getCbType() {
		return this.cbType;
	}
	
	public JTextField getTxtVille() {
		return this.txtVille;
	}
	
	public JTextField getTxtRue() {
		return this.txtVille;
	}

	public JTextField getTxtCodePostal() {
		return this.txtCodePostal;
	}
}