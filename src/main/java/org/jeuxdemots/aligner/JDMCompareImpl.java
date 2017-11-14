package org.jeuxdemots.aligner;

public class JDMCompareImpl implements JDMCompare {
	
	private int renseignJdm;
	private String idJdm;
	private int comparisonValue;
	private String rep;
	
	JDMCompareImpl(final String jdmId, final int renseignJdm, final int value_comparison, final String rep)
	{
		idJdm = jdmId;
		this.renseignJdm =renseignJdm;
		comparisonValue =value_comparison;
		this.rep=rep;
	}
	
	@Override
	public String getIdJdm() {
		return idJdm;
	}
	
	@Override
	public void setIdJdm(final String idJdm) {
		this.idJdm = idJdm;
	}
	@Override
	public int getRenseignJdm() {
		return renseignJdm;
	}
	@Override
	public void setRenseignJdm(final int renseignJdm) {
		this.renseignJdm = renseignJdm;
	}
	@Override
	public int getComparisonValue() {
		return comparisonValue;
	}
	@Override
	public void setComparisonValue(final int comparisonValue) {
		this.comparisonValue = comparisonValue;
	}
	@Override
	public String getRep() {
		return rep;
	}
	@Override
	public void setRep(final String rep) {
		this.rep = rep;
	}
}
