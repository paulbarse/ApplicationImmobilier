package vue.connexion;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.Box;
import java.awt.Color;
import javax.swing.JPanel;

import Controleur.Connexion.gestionPageConnexionPrincipale;

import javax.swing.JButton;

public class pageConnexionPrincipale extends JFrame{

	private static final long serialVersionUID = 1L;
	private JButton Facebook;
    private JButton Google;
    private JButton Outlook;
    private JButton Apple;
    private JButton btnConnexion;
    private JButton btnconnexionImpossible;
    private gestionPageConnexionPrincipale gestion;

	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					pageConnexionPrincipale frame = new pageConnexionPrincipale();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	public pageConnexionPrincipale() {
		getContentPane().setBackground(new Color(255, 255, 255));
		setBounds(100, 100, 608, 705);
		setResizable(false);
		getContentPane().setLayout(null);
		gestion = new gestionPageConnexionPrincipale(this);
		
		JLabel Logo = new JLabel("");
		Logo.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/logobleurond.png")).getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH)));
		Logo.setBounds(179, 28, 287, 172);
		getContentPane().add(Logo);
		
		JLabel Connexion = new JLabel("Se connecter");
		Connexion.setFont(new Font("Times New Roman", Font.PLAIN, 40));
		Connexion.setBounds(199, 217, 211, 68);
		getContentPane().add(Connexion);
		
		Facebook = new JButton("");
		Facebook.setBorderPainted(false);
		Facebook.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/logoFacBook.png")).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
		Facebook.setBounds(156, 323, 60, 50);
		getContentPane().add(Facebook);
		
		Google = new JButton("");
		Google.setBorderPainted(false);
		Google.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/googleleLogo.png")).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
		Google.setBounds(226, 323, 70, 50);
		getContentPane().add(Google);
		
		Outlook = new JButton("");
		Outlook.setBorderPainted(false);
		Outlook.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/outlooklogo.png")).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
		Outlook.setBounds(306, 323, 60, 56);
		getContentPane().add(Outlook);
		
		Apple = new JButton("");
		Apple.setBorderPainted(false);
		Apple.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/appllelogo.png")).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
		Apple.setBounds(390, 323, 45, 56);
		getContentPane().add(Apple);
		
		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setBounds(30, 401, 1, 1);
		getContentPane().add(horizontalBox);
		
		JPanel panelséparateur = new JPanel();
		panelséparateur.setBackground(new Color(0,0,0));
		panelséparateur.setBounds(10, 401, 225, 1);
		getContentPane().add(panelséparateur);
		
		JLabel Ou = new JLabel("Ou");
		Ou.setFont(new Font("Tahoma", Font.PLAIN, 17));
		Ou.setBounds(289, 383, 30, 39);
		getContentPane().add(Ou);
		
		JPanel panelséparateur_1 = new JPanel();
		panelséparateur_1.setBackground(Color.BLACK);
		panelséparateur_1.setBounds(359, 401, 225, 1);
		getContentPane().add(panelséparateur_1);
		
		
		btnConnexion = new JButton("CONNEXION JOPAWIS");
		btnConnexion.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnConnexion.setBounds(78, 459, 454, 50); 
		btnConnexion.setForeground(Color.WHITE);  
		btnConnexion.setBackground(Color.BLACK);  
		btnConnexion.setFocusPainted(false);
		btnConnexion.setBorderPainted(false);
		getContentPane().add(btnConnexion);
		
		btnconnexionImpossible = new JButton("Impossible de se connecter ?");
		btnconnexionImpossible.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnconnexionImpossible.setBounds(156, 615, 310, 31);
		btnconnexionImpossible.setFocusPainted(false);
		btnconnexionImpossible.setBorderPainted(false);
		btnconnexionImpossible.setBackground(Color.WHITE);  
		getContentPane().add(btnconnexionImpossible);
		
		btnConnexion.addActionListener(gestion);
        btnconnexionImpossible.addActionListener(gestion);
	}
	
	
	
}
	
	
	
	
	
	
	
	
	
