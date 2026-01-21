package modele;

import java.sql.Date;

public class Utilisateur {
	private long id;
	private String nom;
	private String prenom;
	private String email;
	private String motDePasse;
	private Date dateNaissance;

	public Utilisateur(long id, String nom, String prenom, String email, String motDePasse, Date dateNaissance) {
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.motDePasse = motDePasse;
		this.dateNaissance = dateNaissance;
	}

	// Constructeur pour l'inscription (sans ID)
	public Utilisateur(String nom, String prenom, String email, String motDePasse, Date dateNaissance) {
		this(0, nom, prenom, email, motDePasse, dateNaissance);
	}

	// Getters
	public long getId() {
		return this.id;
	}

	public String getNom() {
		return this.nom;
	}

	public String getPrenom() {
		return this.prenom;
	}

	public String getEmail() {
		return this.email;
	}

	public String getMotDePasse() {
		return this.motDePasse;
	}

	public Date getDateNaissance() {
		return this.dateNaissance;
	}
	
	// Setter
	
	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setMotDePasse(String motDePasse) {
		this.motDePasse = motDePasse;
	}
}