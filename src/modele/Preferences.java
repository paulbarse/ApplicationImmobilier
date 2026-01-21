package modele;

public class Preferences {
	private long idLocataire;
	private boolean modeSombre;
	private boolean notifEmail;
	private boolean rappelLoyer;

	public Preferences(long idLocataire, boolean modeSombre, boolean notifEmail, boolean rappelLoyer) {
		this.idLocataire = idLocataire;
		this.modeSombre = modeSombre;
		this.notifEmail = notifEmail;
		this.rappelLoyer = rappelLoyer;
	}

	public long getIdLocataire() {
		return this.idLocataire;
	}

	public boolean isModeSombre() {
		return this.modeSombre;
	}

	public boolean isNotifEmail() {
		return this.notifEmail;
	}

	public boolean isRappelLoyer() {
		return this.rappelLoyer;
	}

	// Setters si besoin
	public void setModeSombre(boolean modeSombre) {
		this.modeSombre = modeSombre;
	}

	public void setNotifEmail(boolean notifEmail) {
		this.notifEmail = notifEmail;
	}

	public void setRappelLoyer(boolean rappelLoyer) {
		this.rappelLoyer = rappelLoyer;
	}
}