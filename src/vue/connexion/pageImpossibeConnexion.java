package vue.connexion;
import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Controleur.Connexion.gestionPageImpossibleConnexion;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Color;

public class pageImpossibeConnexion extends JFrame{

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton btnRenisialisationMdp;
	private JButton btnRenisialisationId;
	private gestionPageImpossibleConnexion gestion;

	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					pageImpossibeConnexion frame = new pageImpossibeConnexion();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	public pageImpossibeConnexion() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 828, 696);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		gestion = new gestionPageImpossibleConnexion(this);
	    addWindowListener(gestion);

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel Logo = new JLabel("");
		Logo.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/logobleurond.png")).getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH)));
		Logo.setBounds(279, 24, 221, 172);
		getContentPane().add(Logo);
		
		btnRenisialisationMdp = new JButton("");
		btnRenisialisationMdp.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/logoMdp.png")).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
		btnRenisialisationMdp.setBounds(89, 341, 259, 207);
		contentPane.add(btnRenisialisationMdp);
		
		
		JLabel Titre = new JLabel("Vous n'arrivez pas à vous connecter ?");
		Titre.setForeground(new Color(128, 0, 0));
		Titre.setFont(new Font("Tahoma", Font.PLAIN, 30));
		Titre.setBounds(179, 192, 529, 53);
		contentPane.add(Titre);
		
		JLabel Desc = new JLabel("Aidez-nous à vous aider !\r\n");
		Desc.setFont(new Font("Tahoma", Font.PLAIN, 22));
		Desc.setBounds(290, 236, 259, 40);
		contentPane.add(Desc);
		
		JLabel Desc2 = new JLabel("Indiquez-nous quel est votre souci pour vous connecter");
		Desc2.setForeground(new Color(0, 0, 0));
		Desc2.setFont(new Font("Tahoma", Font.PLAIN, 22));
		Desc2.setBounds(153, 270, 551, 21);
		contentPane.add(Desc2);
		
		JLabel TextResetMdp = new JLabel("JE NE CONNAIS PAS \r\n");
		TextResetMdp.setForeground(new Color(128, 0, 0));
		TextResetMdp.setFont(new Font("Tahoma", Font.PLAIN, 15));
		TextResetMdp.setBounds(153, 559, 142, 26);
		contentPane.add(TextResetMdp);
		
		JLabel TextResetMdp2 = new JLabel("MON MOT DE PASSE\r\n");
		TextResetMdp2.setForeground(new Color(128, 0, 0));
		TextResetMdp2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		TextResetMdp2.setBounds(153, 570, 160, 40);
		contentPane.add(TextResetMdp2);
		
		btnRenisialisationId = new JButton("");
		btnRenisialisationId.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/logoID.png")).getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
		btnRenisialisationId.setBounds(460, 341, 259, 207);
		contentPane.add(btnRenisialisationId);
		
		JLabel lblPasMonIdentifiant = new JLabel("PAS MON IDENTIFIANT");
		lblPasMonIdentifiant.setForeground(new Color(128, 0, 0));
		lblPasMonIdentifiant.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblPasMonIdentifiant.setBounds(512, 570, 160, 40);
		contentPane.add(lblPasMonIdentifiant);
		
		JLabel lblJeNeConnais = new JLabel("JE NE CONNAIS ");
		lblJeNeConnais.setForeground(new Color(128, 0, 0));
		lblJeNeConnais.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblJeNeConnais.setBounds(538, 559, 142, 26);
		contentPane.add(lblJeNeConnais);	
	}
}
