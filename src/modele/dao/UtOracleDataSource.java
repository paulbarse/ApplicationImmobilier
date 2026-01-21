package modele.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class UtOracleDataSource {

	private static Connection connection = null;
	private static final String URL = "jdbc:oracle:thin:@telline.univ-tlse3.fr:1521:ETUPRE";
	private static final String login = "pgs5457a";
	private static final String mdp = "$";  // REMPLACER PAR VOTRE MOT DE PASSE

	private UtOracleDataSource() {
	}

	public static void creerAcces() throws SQLException {
		if (connection == null || connection.isClosed()) {
			connection = DriverManager.getConnection(URL, login, mdp);
			connection.setAutoCommit(true);
		}
	}

	public static Connection getConnection() {
		return connection;
	}

	public static void Deconnecter() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
			connection = null;
		} catch (SQLException e) {
			System.out.println("Erreur de deconnexion : " + e.getMessage());
		}
	}
}