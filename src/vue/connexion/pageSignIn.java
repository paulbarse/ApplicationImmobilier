package vue.connexion;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

import Controleur.Connexion.gestionPageSignIn;

public class pageSignIn extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField mail;
	private JPasswordField passwordField;
	private JTextField nom;
	private JTextField prenom;
	private JComboBox<String> jourDateNaissance;
	private JComboBox<String> moisDateNaissance;
	private JComboBox<String> anneesDateNaissance;
	private JButton btnCreeCompte;
	private gestionPageSignIn gestion;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					pageSignIn frame = new pageSignIn();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public pageSignIn() {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setBounds(100, 100, 572, 812);
		this.setResizable(false);
		this.contentPane = new JPanel();
		this.contentPane.setBackground(new Color(255, 255, 255));
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.gestion = new gestionPageSignIn(this);
		this.addWindowListener(this.gestion);

		this.setContentPane(this.contentPane);
		this.contentPane.setLayout(null);

		JLabel Logo = new JLabel("");
		Logo.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/logobleurond.png")).getImage()
				.getScaledInstance(250, 250, Image.SCALE_SMOOTH)));
		Logo.setBounds(163, 10, 231, 219);
		this.contentPane.add(Logo);

		JLabel adresseMail = new JLabel("Adresse e-mail");
		adresseMail.setFont(new Font("Tahoma", Font.PLAIN, 15));
		adresseMail.setBounds(79, 318, 98, 26);
		this.contentPane.add(adresseMail);

		JLabel MDP = new JLabel("Mot de passe");
		MDP.setFont(new Font("Tahoma", Font.PLAIN, 15));
		MDP.setBounds(79, 381, 111, 26);
		this.contentPane.add(MDP);

		this.mail = new JTextField();
		this.mail.setBounds(91, 348, 380, 26);
		this.contentPane.add(this.mail);
		this.mail.setColumns(10);

		JLabel Titre = new JLabel("Créez votre compte");
		Titre.setFont(new Font("Tahoma", Font.PLAIN, 40));
		Titre.setBounds(98, 239, 419, 67);
		this.contentPane.add(Titre);

		this.passwordField = new JPasswordField();
		this.passwordField.setColumns(10);
		this.passwordField.setBounds(89, 411, 382, 26);
		this.contentPane.add(this.passwordField);

		JLabel etoile = new JLabel("*");
		etoile.setForeground(new Color(255, 0, 0));
		etoile.setFont(new Font("Tahoma", Font.PLAIN, 15));
		etoile.setBounds(187, 318, 45, 13);
		this.contentPane.add(etoile);

		JLabel etoile_1 = new JLabel("*");
		etoile_1.setForeground(Color.RED);
		etoile_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		etoile_1.setBounds(181, 388, 45, 13);
		this.contentPane.add(etoile_1);

		this.btnCreeCompte = new JButton("Créer mon compte");
		this.btnCreeCompte.setForeground(Color.WHITE);
		this.btnCreeCompte.setFont(new Font("Tahoma", Font.PLAIN, 20));
		this.btnCreeCompte.setFocusPainted(false);
		this.btnCreeCompte.setBorderPainted(false);
		this.btnCreeCompte.setBackground(Color.BLACK);
		this.btnCreeCompte.setBounds(114, 710, 332, 38);
		this.contentPane.add(this.btnCreeCompte);
		this.btnCreeCompte.addActionListener(this.gestion);
		this.contentPane.add(this.btnCreeCompte);

		JLabel Nom = new JLabel("Nom");
		Nom.setFont(new Font("Tahoma", Font.PLAIN, 15));
		Nom.setBounds(79, 440, 45, 26);
		this.contentPane.add(Nom);

		this.nom = new JTextField();
		this.nom.setColumns(10);
		this.nom.setBounds(91, 474, 380, 26);
		this.contentPane.add(this.nom);

		JLabel Prenom = new JLabel("Prénom");
		Prenom.setFont(new Font("Tahoma", Font.PLAIN, 15));
		Prenom.setBounds(79, 510, 57, 26);
		this.contentPane.add(Prenom);

		this.prenom = new JTextField();
		this.prenom.setColumns(10);
		this.prenom.setBounds(91, 537, 380, 26);
		this.contentPane.add(this.prenom);

		JLabel etoile_2 = new JLabel("*");
		etoile_2.setForeground(Color.RED);
		etoile_2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		etoile_2.setBounds(120, 440, 45, 13);
		this.contentPane.add(etoile_2);

		JLabel etoile_3 = new JLabel("*");
		etoile_3.setForeground(Color.RED);
		etoile_3.setFont(new Font("Tahoma", Font.PLAIN, 15));
		etoile_3.setBounds(139, 514, 45, 13);
		this.contentPane.add(etoile_3);

		this.jourDateNaissance = new JComboBox<>(
				new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
						"17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" });
		this.jourDateNaissance.setBounds(91, 613, 90, 31);
		this.jourDateNaissance.setSelectedIndex(-1);
		this.jourDateNaissance.setEditable(true);
		this.jourDateNaissance.getEditor().setItem("Jour");
		Component text = this.jourDateNaissance.getEditor().getEditorComponent();
		if (text instanceof JTextComponent verif) {
			verif.setEditable(false);
			verif.setForeground(Color.GRAY);
		}

		this.contentPane.add(this.jourDateNaissance);

		this.moisDateNaissance = new JComboBox<>(new String[] { "Janvier", "Fevrier", "Mars", "Avril", "Mai", "Juin",
				"Juillet", "Aout", "Septembre", "Octobre", "Novembre", "Décembre" });
		this.moisDateNaissance.setBounds(239, 613, 90, 31);
		this.moisDateNaissance.setSelectedIndex(-1);
		this.moisDateNaissance.setEditable(true);
		this.moisDateNaissance.getEditor().setItem("Mois");
		Component text1 = this.moisDateNaissance.getEditor().getEditorComponent();
		if (text1 instanceof JTextComponent verif) {
			verif.setEditable(false);
			verif.setForeground(Color.GRAY);
		}

		this.contentPane.add(this.moisDateNaissance);

		JLabel dateDeNaissance = new JLabel("Date de naissance");
		dateDeNaissance.setFont(new Font("Tahoma", Font.PLAIN, 15));
		dateDeNaissance.setBounds(79, 573, 123, 26);
		this.contentPane.add(dateDeNaissance);

		JLabel etoile_3_1 = new JLabel("*");
		etoile_3_1.setForeground(Color.RED);
		etoile_3_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		etoile_3_1.setBounds(212, 573, 45, 13);
		this.contentPane.add(etoile_3_1);

		String[] annees = new String[100];
		for (int i = 0; i < 100; i++) {
			annees[i] = String.valueOf(2025 - i);
		}

		this.anneesDateNaissance = new JComboBox<>(annees);
		this.anneesDateNaissance.setBounds(381, 613, 90, 31);
		this.anneesDateNaissance.setSelectedIndex(-1);
		this.anneesDateNaissance.setEditable(true);
		this.anneesDateNaissance.getEditor().setItem("Année");
		Component text2 = this.anneesDateNaissance.getEditor().getEditorComponent();
		if (text2 instanceof JTextComponent verif) {
			verif.setEditable(false);
			verif.setForeground(Color.GRAY);
		}

		this.contentPane.add(this.anneesDateNaissance);

	}


	public String getNom() {
		return this.nom.getText();
	}

	public String getPrenom() {
		return this.prenom.getText();
	}

	public String getEmail() {
		return this.mail.getText();
	}

	// Pour le mot de passe, on convertit le tableau de char en String
	public String getPassword() {
		return new String(this.passwordField.getPassword());
	}


	public java.sql.Date getDateNaissance() {
		try {
			// On récupère les valeurs sélectionnées
			String jour = (String) this.jourDateNaissance.getSelectedItem();
			String moisTxt = (String) this.moisDateNaissance.getSelectedItem();
			String annee = (String) this.anneesDateNaissance.getSelectedItem();

			// Vérification si l'utilisateur a laissé "Jour", "Mois" ou rien
			if (jour == null || jour.equals("Jour") || moisTxt == null || moisTxt.equals("Mois") || annee == null
					|| annee.equals("Années")) {
				return null; // Date invalide
			}

			int mois = 1;
			switch (moisTxt) {
			case "Janvier":
				mois = 1;
				break;
			case "Fevrier":
				mois = 2;
				break;
			case "Mars":
				mois = 3;
				break;
			case "Avril":
				mois = 4;
				break;
			case "Mai":
				mois = 5;
				break;
			case "Juin":
				mois = 6;
				break;
			case "Juillet":
				mois = 7;
				break;
			case "Aout":
				mois = 8;
				break;
			case "Septembre":
				mois = 9;
				break;
			case "Octobre":
				mois = 10;
				break;
			case "Novembre":
				mois = 11;
				break;
			case "Décembre":
				mois = 12;
				break;
			}

			
			java.time.LocalDate localDate = java.time.LocalDate.of(Integer.parseInt(annee), mois,
					Integer.parseInt(jour));

			return java.sql.Date.valueOf(localDate);

		} catch (Exception e) {
			e.printStackTrace();
			return null; // En cas d'erreur
		}
	}

}
