package org.jeuxdemots.aligner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MotCompare {
	
	public int renseign_bn;
	public String id_bn;
	public String sens_princip;
	public String def;
	public String fulltext;
	public Map<String, Double> SacMots;
	public ArrayList<JDMCompare> jdmC;
	
	public MotCompare()
	{
		jdmC = new ArrayList<>();
		SacMots = new HashMap<>();
	}
	
	public String getDef() {
		return def;
	}
	
	public void setDef(String def) {
		this.def = def;
	}
	public String getFulltext() {
		return fulltext;
	}
	public void setFulltext(String f) {
		fulltext = f;
	}
	public int getRenseignBN() {
		return renseign_bn;
	}
	public void setRenseignBN(int renseign) {
		this.renseign_bn = renseign;
	}
	public String getId_bn() {
		return id_bn;
	}
	public void setId_bn(String id_bn) {
		this.id_bn = id_bn;
	}
	public String getSens_princip() {
		return sens_princip;
	}
	public void setSens_princip(String sens_princip) {
		this.sens_princip = sens_princip;
	}
	
	public void put(String s, Double d)
	{
		SacMots.put(s, d);
	}
	
	public void addJDMC(String i, int r, int v, String rep)
	{
		jdmC.add(new JDMCompareImpl(i, r,v,rep));
	}
	
	public int getJDMCsize()
	{
		return jdmC.size();
	}
	
	public JDMCompare getJDMC(int i)
	{
		return jdmC.get(i);
	}
}
