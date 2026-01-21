package vue.Profil;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import Controleur.Profil.gestionPageCompte;

public class pageCompte extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JPasswordField passwordField;

	private JButton btnModifier;

	private gestionPageCompte gestion;

	public pageCompte() {
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setBounds(100, 100, 638, 760);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.contentPane = new JPanel();
		this.contentPane.setBackground(new Color(255, 255, 255));
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));


		this.setContentPane(this.contentPane);
		this.contentPane.setLayout(null);

		JLabel Logo = new JLabel("");
		Logo.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/profile2.png")).getImage()
				.getScaledInstance(120, 120, Image.SCALE_SMOOTH)));
		Logo.setBounds(251, 30, 120, 120);
		this.contentPane.add(Logo);

		JLabel Titre = new JLabel("Mon compte", SwingConstants.CENTER);
		Titre.setFont(new Font("Times New Roman", Font.BOLD, 28));
		Titre.setBounds(72, 162, 484, 50);
		Titre.setForeground(new Color(33, 37, 41));
		this.contentPane.add(Titre);

		JLabel Nom = new JLabel("Nom");
		Nom.setFont(new Font("Times New Roman", Font.BOLD, 13));
		Nom.setBounds(145, 224, 360, 20);
		Nom.setForeground(new Color(100, 100, 100));
		this.contentPane.add(Nom);

		// Création du champ Nom
		this.textField = new JTextField();
		this.textField.setText("");
		this.textField.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		this.textField.setBounds(145, 249, 360, 40);
		this.textField.setMargin(new Insets(0, 10, 0, 10));
		this.textField.setEditable(false);
		this.textField.setBackground(new Color(245, 245, 245));
		this.contentPane.add(this.textField);

		JLabel Prenom = new JLabel("Prénom");
		Prenom.setFont(new Font("Times New Roman", Font.BOLD, 13));
		Prenom.setBounds(145, 301, 360, 20);
		Prenom.setForeground(new Color(100, 100, 100));
		this.contentPane.add(Prenom);

		// Création du champ Prénom
		this.textField_1 = new JTextField();
		this.textField_1.setText("");
		this.textField_1.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		this.textField_1.setBounds(145, 326, 360, 40);
		this.textField_1.setMargin(new Insets(0, 10, 0, 10));
		this.textField_1.setEditable(false);
		this.textField_1.setBackground(new Color(245, 245, 245));
		this.contentPane.add(this.textField_1);

		JLabel AdM = new JLabel("Adresse e-mail");
		AdM.setFont(new Font("Times New Roman", Font.BOLD, 13));
		AdM.setBounds(145, 390, 360, 20);
		AdM.setForeground(new Color(100, 100, 100));
		this.contentPane.add(AdM);

		// Création du champ Email
		this.textField_2 = new JTextField();
		this.textField_2.setText("");
		this.textField_2.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		this.textField_2.setBounds(145, 415, 360, 40);
		this.textField_2.setMargin(new Insets(0, 10, 0, 10));
		this.textField_2.setEditable(false);
		this.textField_2.setBackground(new Color(245, 245, 245));
		this.contentPane.add(this.textField_2);

		JLabel MDP = new JLabel("Mot de passe");
		MDP.setFont(new Font("Times New Roman", Font.BOLD, 13));
		MDP.setBounds(145, 470, 360, 20);
		MDP.setForeground(new Color(100, 100, 100));
		this.contentPane.add(MDP);

		// Création du champ Mot de passe
		this.passwordField = new JPasswordField();
		this.passwordField.setText("");
		this.passwordField.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		this.passwordField.setBounds(145, 495, 360, 40);
		this.passwordField.setMargin(new Insets(0, 10, 0, 10));
		this.passwordField.setEditable(false);
		this.passwordField.setBackground(new Color(245, 245, 245));
		this.contentPane.add(this.passwordField);

		this.gestion = new gestionPageCompte(this);

		JButton btnFermer = new JButton("Fermer");
		btnFermer.setActionCommand("FERMER_GESTION_COMPTE");
		btnFermer.addActionListener(this.gestion);
		btnFermer.setBounds(130, 579, 170, 45);
		btnFermer.setFont(new Font("Times New Roman", Font.BOLD, 14));
		btnFermer.setBackground(Color.WHITE);
		btnFermer.setForeground(Color.BLACK);
		btnFermer.setFocusPainted(false);
		btnFermer.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.contentPane.add(btnFermer);

		this.btnModifier = new JButton("Modifier");
		this.btnModifier.setActionCommand("MODIFIER_COMPTE");
		this.btnModifier.addActionListener(this.gestion);
		this.btnModifier.setBounds(330, 580, 170, 45);
		this.btnModifier.setFont(new Font("Times New Roman", Font.BOLD, 14));
		this.btnModifier.setBackground(Color.WHITE);
		this.btnModifier.setForeground(Color.BLACK);
		this.btnModifier.setFocusPainted(false);
		this.btnModifier.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.contentPane.add(this.btnModifier);
	}

	public void setEtatEdition(boolean modeEdition) {
		Color couleurFond;
		if (modeEdition) {
			couleurFond = Color.WHITE;
			this.btnModifier.setText("Enregistrer");
			this.btnModifier.setBackground(new Color(46, 204, 113));

		} else {
			couleurFond = new Color(245, 245, 245);
			this.btnModifier.setText("Modifier");
			this.btnModifier.setBackground(new Color(30, 144, 255));
		}

		this.textField.setEditable(modeEdition);
		this.textField.setBackground(couleurFond);

		this.textField_1.setEditable(modeEdition);
		this.textField_1.setBackground(couleurFond);

		this.textField_2.setEditable(modeEdition);
		this.textField_2.setBackground(couleurFond);

		this.passwordField.setEditable(modeEdition);
		this.passwordField.setBackground(couleurFond);
	}

	public void setNom(String nom) {
		this.textField.setText(nom);
	}

	public void setPrenom(String prenom) {
		this.textField_1.setText(prenom);
	}

	public void setEmail(String email) {
		this.textField_2.setText(email);
	}

	public void setMotDePasse(String mdp) {
		this.passwordField.setText(mdp);
	}

	public String getNom() {
		return this.textField.getText();
	}

	public String getPrenom() {
		return this.textField_1.getText();
	}

	public String getEmail() {
		return this.textField_2.getText();
	}

	public String getMotDePasse() {
		return new String(this.passwordField.getPassword());
	}
}