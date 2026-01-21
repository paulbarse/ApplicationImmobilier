package modele;

import java.util.Objects;

public class DeclarationRevenusFoncier {
	
	private int idDeclaration;
	private int annee;
	private String regime;
	private double totalRecette;
	private double totalChargesDeductible;
	
	@Override
	public String toString() {
		return "DeclarationRevenusFoncier [idDeclaration=" + idDeclaration + ", annee=" + annee + ", regime=" + regime
				+ ", totalRecette=" + totalRecette + ", totalChargesDeductible=" + totalChargesDeductible + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(idDeclaration);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DeclarationRevenusFoncier)) {
			return false;
		}
		DeclarationRevenusFoncier other = (DeclarationRevenusFoncier) obj;
		return idDeclaration == other.idDeclaration;
	}

	public int getIdDeclaration() {
		return idDeclaration;
	}

	public void setIdDeclaration(int idDeclaration) {
		this.idDeclaration = idDeclaration;
	}

	public int getAnnee() {
		return annee;
	}

	public void setAnnee(int annee) {
		this.annee = annee;
	}

	public String getRegime() {
		return regime;
	}

	public void setRegime(String regime) {
		this.regime = regime;
	}

	public double getTotalRecette() {
		return totalRecette;
	}

	public void setTotalRecette(double totalRecette) {
		this.totalRecette = totalRecette;
	}

	public double getTotalChargesDeductible() {
		return totalChargesDeductible;
	}

	public void setTotalChargesDeductible(double totalChargesDeductible) {
		this.totalChargesDeductible = totalChargesDeductible;
	}

	public DeclarationRevenusFoncier(int idDeclaration, int annee, String regime, double totalRecette,
			double totalChargesDeductible) {
		this.idDeclaration = idDeclaration;
		this.annee = annee;
		this.regime = regime;
		this.totalRecette = totalRecette;
		this.totalChargesDeductible = totalChargesDeductible;
	}

}
