package vue.Profil;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import Controleur.Profil.gestionParametres;

public class pageParam extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private JCheckBox chkModeSombre;
	private JCheckBox chkNotifications;
	private JCheckBox chkRappelLoyer;

	private gestionParametres gestion;

	public pageParam() {
		this.setTitle("Paramètres");

		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setBounds(100, 100, 450, 617);
		this.setResizable(false);

		this.setLocationRelativeTo(null);

		this.contentPane = new JPanel();
		this.contentPane.setBackground(new Color(255, 255, 255));
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));


		this.setContentPane(this.contentPane);
		this.contentPane.setLayout(null);

		JLabel Titre = new JLabel("Paramètres", SwingConstants.CENTER);
		Titre.setFont(new Font("Times New Roman", Font.BOLD, 28));
		Titre.setBounds(0, 30, 436, 40);
		Titre.setForeground(new Color(33, 37, 41));
		this.contentPane.add(Titre);

		JLabel Apparence = new JLabel("Apparence");
		Apparence.setFont(new Font("Times New Roman", Font.BOLD, 16));
		Apparence.setForeground(new Color(30, 144, 255));
		Apparence.setBounds(40, 94, 200, 25);
		this.contentPane.add(Apparence);

		JSeparator sep1 = new JSeparator();
		sep1.setBounds(40, 117, 350, 2);
		this.contentPane.add(sep1);

		this.chkModeSombre = new JCheckBox("Activer le mode sombre");
		this.chkModeSombre.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		this.chkModeSombre.setBackground(Color.WHITE);
		this.chkModeSombre.setFocusPainted(false);
		this.chkModeSombre.setBounds(40, 131, 300, 30);
		this.contentPane.add(this.chkModeSombre);

		JLabel lblNotif = new JLabel("Notifications");
		lblNotif.setFont(new Font("Times New Roman", Font.BOLD, 16));
		lblNotif.setForeground(new Color(30, 144, 255));
		lblNotif.setBounds(40, 185, 200, 25);
		this.contentPane.add(lblNotif);

		JSeparator sep2 = new JSeparator();
		sep2.setBounds(40, 208, 350, 2);
		this.contentPane.add(sep2);

		this.chkNotifications = new JCheckBox("Recevoir les alertes par e-mail");
		this.chkNotifications.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		this.chkNotifications.setBackground(Color.WHITE);
		this.chkNotifications.setFocusPainted(false);
		this.chkNotifications.setSelected(true); // Valeur par défaut graphique
		this.chkNotifications.setBounds(40, 222, 300, 30);
		this.contentPane.add(this.chkNotifications);

		this.chkRappelLoyer = new JCheckBox("Rappels automatiques de loyer");
		this.chkRappelLoyer.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		this.chkRappelLoyer.setBackground(Color.WHITE);
		this.chkRappelLoyer.setFocusPainted(false);
		this.chkRappelLoyer.setBounds(40, 264, 300, 30);
		this.contentPane.add(this.chkRappelLoyer);

		JLabel lblSys = new JLabel("Système");
		lblSys.setFont(new Font("Times New Roman", Font.BOLD, 16));
		lblSys.setForeground(new Color(30, 144, 255));
		lblSys.setBounds(40, 319, 200, 25);
		this.contentPane.add(lblSys);

		JSeparator sep3 = new JSeparator();
		sep3.setBounds(40, 342, 350, 2);
		this.contentPane.add(sep3);

	

		this.gestion = new gestionParametres(this);

		JButton btnDeconnexion = new JButton("Déconnexion");
		btnDeconnexion.setActionCommand("DECONNEXION");
		btnDeconnexion.addActionListener(this.gestion);
		btnDeconnexion.setBounds(40, 378, 150, 35);
		btnDeconnexion.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		btnDeconnexion.setBackground(new Color(255, 230, 230));
		btnDeconnexion.setForeground(Color.RED);
		btnDeconnexion.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnDeconnexion.setFocusPainted(false);
		btnDeconnexion.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.contentPane.add(btnDeconnexion);

		JButton btnSupprimerMonCompte = new JButton("Supprimer mon compte");
		btnSupprimerMonCompte.setForeground(Color.RED);
		btnSupprimerMonCompte.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		btnSupprimerMonCompte.setFocusPainted(false);
		btnSupprimerMonCompte.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnSupprimerMonCompte.setBackground(new Color(255, 230, 230));
		//La commande doit être SUPPRIMER_COMPTE
		btnSupprimerMonCompte.setActionCommand("SUPPRIMER_COMPTE");
		btnSupprimerMonCompte.addActionListener(this.gestion);
		btnSupprimerMonCompte.setBounds(40, 469, 150, 35);
		this.contentPane.add(btnSupprimerMonCompte);

		JLabel lblSys_1 = new JLabel("Compte");
		lblSys_1.setForeground(new Color(30, 144, 255));
		lblSys_1.setFont(new Font("Times New Roman", Font.BOLD, 16));
		lblSys_1.setBounds(40, 423, 200, 25);
		this.contentPane.add(lblSys_1);

		JButton btnAnnuler = new JButton("Annuler");
		btnAnnuler.setActionCommand("FERMER_PARAMETRES");
		btnAnnuler.addActionListener(this.gestion);
		btnAnnuler.setBounds(80, 529, 130, 40);
		btnAnnuler.setFont(new Font("Times New Roman", Font.BOLD, 14));
		btnAnnuler.setBackground(Color.WHITE);
		btnAnnuler.setForeground(Color.BLACK);
		btnAnnuler.setFocusPainted(false);
		btnAnnuler.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.contentPane.add(btnAnnuler);

		JButton btnEnregistrer = new JButton("Enregistrer");
		btnEnregistrer.setActionCommand("ENREGISTRER_PARAMETRES");
		btnEnregistrer.addActionListener(this.gestion);
		btnEnregistrer.setBounds(230, 529, 130, 40);
		btnEnregistrer.setFont(new Font("Times New Roman", Font.BOLD, 14));
		btnEnregistrer.setBackground(new Color(30, 144, 255));
		btnEnregistrer.setForeground(Color.BLACK);
		btnEnregistrer.setFocusPainted(false);
		btnEnregistrer.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.contentPane.add(btnEnregistrer);
	}

	public boolean isModeSombre() {
		return this.chkModeSombre.isSelected();
	}

	//  MÉTHODES POUR LE CONTRÔLEUR 

	public void setModeSombre(boolean actif) {
		this.chkModeSombre.setSelected(actif);
	}

	public void setNotifEmail(boolean actif) {
		this.chkNotifications.setSelected(actif);
	}

	public void setRappelLoyer(boolean actif) {
		this.chkRappelLoyer.setSelected(actif);
	}

	public boolean isNotifEmail() {
		return this.chkNotifications.isSelected();
	}

	public boolean isRappelLoyer() {
		return this.chkRappelLoyer.isSelected();
	}
}