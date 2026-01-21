package vue.connexion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import Controleur.Connexion.GestionPageConnexion;

public class pageConnexion extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textFieldIdentifiant;
	private JPasswordField passwordField;
	private JButton btnConnexion;
	private JButton btnConnexionImpossible;
	private JButton btnCreerCompte;
	private GestionPageConnexion gestion;
	private Object textFieldLogin;

	public pageConnexion() {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setBounds(100, 100, 638, 760);
		this.setResizable(false);
		this.contentPane = new JPanel();
		this.contentPane.setBackground(new Color(255, 255, 255));
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.gestion = new GestionPageConnexion(this);
		this.addWindowListener(this.gestion);

		this.setContentPane(this.contentPane);
		this.contentPane.setLayout(null);

		JLabel Logo = new JLabel("");
		Logo.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/logobleurond.png")).getImage()
				.getScaledInstance(250, 250, Image.SCALE_SMOOTH)));
		Logo.setBounds(181, 10, 231, 219);
		this.contentPane.add(Logo);

		JLabel Id = new JLabel("Identifiant");
		Id.setFont(new Font("Tahoma", Font.PLAIN, 15));
		Id.setBounds(116, 323, 98, 26);
		this.contentPane.add(Id);

		JLabel MDP = new JLabel("Mot de passe");
		MDP.setFont(new Font("Tahoma", Font.PLAIN, 15));
		MDP.setBounds(91, 384, 111, 26);
		this.contentPane.add(MDP);

		this.textFieldIdentifiant = new JTextField();
		this.textFieldIdentifiant.setBounds(139, 348, 332, 26);
		this.contentPane.add(this.textFieldIdentifiant);
		this.textFieldIdentifiant.setColumns(10);

		JLabel Titre = new JLabel("Se connecter");
		Titre.setFont(new Font("Tahoma", Font.PLAIN, 40));
		Titre.setBounds(203, 237, 332, 67);
		this.contentPane.add(Titre);

		this.passwordField = new JPasswordField();
		this.passwordField.setColumns(10);
		this.passwordField.setBounds(139, 411, 332, 26);
		this.contentPane.add(this.passwordField);

		JLabel etoile = new JLabel("*");
		etoile.setForeground(new Color(255, 0, 0));
		etoile.setFont(new Font("Tahoma", Font.PLAIN, 15));
		etoile.setBounds(184, 325, 45, 13);
		this.contentPane.add(etoile);

		JLabel etoile_1 = new JLabel("*");
		etoile_1.setForeground(Color.RED);
		etoile_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		etoile_1.setBounds(181, 388, 45, 13);
		this.contentPane.add(etoile_1);

		this.btnConnexion = new JButton("Se connecter");
		this.btnConnexion.setForeground(Color.WHITE);
		this.btnConnexion.setFont(new Font("Tahoma", Font.PLAIN, 20));
		this.btnConnexion.setFocusPainted(false);
		this.btnConnexion.setBorderPainted(false);
		this.btnConnexion.setBackground(Color.BLACK);
		this.btnConnexion.setBounds(139, 465, 332, 38);
		this.contentPane.add(this.btnConnexion);

		this.btnConnexionImpossible = new JButton("Impossible de se connecter ?");
		this.btnConnexionImpossible.setFont(new Font("Tahoma", Font.PLAIN, 14));
		this.btnConnexionImpossible.setFocusPainted(false);
		this.btnConnexionImpossible.setBorderPainted(false);
		this.btnConnexionImpossible.setBackground(Color.WHITE);
		this.btnConnexionImpossible.setBounds(153, 555, 310, 26);
		this.contentPane.add(this.btnConnexionImpossible);

		JLabel PasDeCompte = new JLabel("Vous n'avez pas de compte JOPAWIS ?");
		PasDeCompte.setFont(new Font("Tahoma", Font.PLAIN, 15));
		PasDeCompte.setBounds(170, 616, 293, 13);
		this.contentPane.add(PasDeCompte);

		this.btnCreerCompte = new JButton("Créer un compte");
		this.btnCreerCompte.setFont(new Font("Tahoma", Font.PLAIN, 14));
		this.btnCreerCompte.setFocusPainted(false);
		this.btnCreerCompte.setBorderPainted(false);
		this.btnCreerCompte.setBackground(Color.WHITE);
		this.btnCreerCompte.setBounds(153, 639, 310, 31);
		this.contentPane.add(this.btnCreerCompte);

		this.btnConnexion.setActionCommand("SE_CONNECTER");
		this.btnConnexionImpossible.setActionCommand("AIDE_CONNEXION");
		this.btnCreerCompte.setActionCommand("CREER_COMPTE");

		this.btnConnexion.addActionListener(this.gestion);
		this.btnConnexionImpossible.addActionListener(this.gestion);
		this.btnCreerCompte.addActionListener(this.gestion);

	}

	public JTextField getTextFieldIdentifiant() {
		return this.textFieldIdentifiant;
	}

	public JPasswordField getPasswordField() {
		return this.passwordField;
	}

	public JButton getBtnConnexion() {
		return this.btnConnexion;
	}

	public JButton getBtnConnexionImpossible() {
		return this.btnConnexionImpossible;
	}

	public JButton getBtnCreerCompte() {
		return this.btnCreerCompte;
	}

	public String getIdentifiant() {
		return this.textFieldIdentifiant.getText(); // On récupère directement le texte
	}

	public String getMotDePasse() {
		return new String(this.passwordField.getPassword()); // Remplace par le vrai nom de ton champ mot de passe
	}
}
