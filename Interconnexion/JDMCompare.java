package jenaJDM;

public class JDMCompare {
	
	public int renseign_JDM;
	public String id_JDM;
	public int valeur_comparaison;
	public String rep;
	
	public JDMCompare(String i, int r, int v, String rep)
	{
		id_JDM = i;
		renseign_JDM=r;
		valeur_comparaison=v;
		this.rep=rep;
	}
	
	public String getId_JDM() {
		return id_JDM;
	}
	
	public void setId_JDM(String id_JDM) {
		this.id_JDM = id_JDM;
	}
	public int getRenseign_JDM() {
		return renseign_JDM;
	}
	public void setRenseign_JDM(int renseign_JDM) {
		this.renseign_JDM = renseign_JDM;
	}
	public int getValeur_comparaison() {
		return valeur_comparaison;
	}
	public void setValeur_comparaison(int valeur_comparaison) {
		this.valeur_comparaison = valeur_comparaison;
	}
	public String getRep() {
		return rep;
	}
	public void setRep(String rep) {
		this.rep = rep;
	}
}
