package com.nyc.hhs.model;

import com.nyc.hhs.constants.HHSConstants;

/**
 * This class is a bean which maintains the Faq Form information.
 * 
 */

public class PaymentAllocationBean extends CBGridBean
{

	private String id = HHSConstants.EMPTY_STRING;
	private String chartOfAccount = HHSConstants.EMPTY_STRING;
	private String subOc = HHSConstants.EMPTY_STRING;
	private String rc = HHSConstants.EMPTY_STRING;
	private String unitOfAppropriation = HHSConstants.EMPTY_STRING;
	private String budgetCode = HHSConstants.EMPTY_STRING;
	private String objectCode = HHSConstants.EMPTY_STRING;
	private String fiscalYear = HHSConstants.EMPTY_STRING;
	private String fy1 = HHSConstants.STRING_ZERO;
	private String fy2 = HHSConstants.STRING_ZERO;
	private String fy3 = HHSConstants.STRING_ZERO;
	private String fy4 = HHSConstants.STRING_ZERO;
	private String fy5 = HHSConstants.STRING_ZERO;
	private String fy6 = HHSConstants.STRING_ZERO;
	private String fy7 = HHSConstants.STRING_ZERO;
	private String fy8 = HHSConstants.STRING_ZERO;
	private String fy9 = HHSConstants.STRING_ZERO;
	//bean variables for fiscal year are extended 
	//till 31 build 2.6.1, enhancement id: 5707
	private String fy10 = HHSConstants.STRING_ZERO;
	private String fy11 = HHSConstants.STRING_ZERO;
	private String fy12 = HHSConstants.STRING_ZERO;
	private String fy13 = HHSConstants.STRING_ZERO;
	private String fy14 = HHSConstants.STRING_ZERO;
	private String fy15 = HHSConstants.STRING_ZERO;
	private String fy16 = HHSConstants.STRING_ZERO;
	private String fy17 = HHSConstants.STRING_ZERO;
	private String fy18 = HHSConstants.STRING_ZERO;
	private String fy19 = HHSConstants.STRING_ZERO;
	private String fy20 = HHSConstants.STRING_ZERO;
	private String fy21 = HHSConstants.STRING_ZERO;
	private String fy22 = HHSConstants.STRING_ZERO;
	private String fy23 = HHSConstants.STRING_ZERO;
	private String fy24 = HHSConstants.STRING_ZERO;
	private String fy25 = HHSConstants.STRING_ZERO;
	private String fy26 = HHSConstants.STRING_ZERO;
	private String fy27 = HHSConstants.STRING_ZERO;
	private String fy28 = HHSConstants.STRING_ZERO;
	private String fy29 = HHSConstants.STRING_ZERO;
	private String fy30 = HHSConstants.STRING_ZERO;
	private String fy31 = HHSConstants.STRING_ZERO;
	private String ammount=HHSConstants.EMPTY_STRING;
	private String docId=HHSConstants.EMPTY_STRING;
	private String activeFlag=HHSConstants.EMPTY_STRING;
	private String total = HHSConstants.EMPTY_STRING;
	private String delStatus = HHSConstants.EMPTY_STRING;
	private String contractStartFY;
	private String contractEndFY;

	/**
	 * @return the unitOfAppropriation
	 */
	public String getUnitOfAppropriation()
	{
		return unitOfAppropriation;
	}

	/**
	 * @param unitOfAppropriation the unitOfAppropriation to set
	 */
	public void setUnitOfAppropriation(String unitOfAppropriation)
	{
		this.unitOfAppropriation = unitOfAppropriation;
	}

	/**
	 * @return the ammount
	 */
	public String getAmmount()
	{
		return ammount;
	}

	/**
	 * @param ammount the ammount to set
	 */
	public void setAmmount(String ammount)
	{
		this.ammount = ammount;
	}

	/**
	 * @return the chartOfAccount
	 */
	public final String getChartOfAccount()
	{
		return chartOfAccount;
	}

	/**
	 * @return the budgetCode
	 */
	public String getBudgetCode()
	{
		return budgetCode;
	}

	/**
	 * @param budgetCode the budgetCode to set
	 */
	public void setBudgetCode(String budgetCode)
	{
		this.budgetCode = budgetCode;
	}

	/**
	 * @return the objectCode
	 */
	public String getObjectCode()
	{
		return objectCode;
	}

	/**
	 * @param objectCode the objectCode to set
	 */
	public void setObjectCode(String objectCode)
	{
		this.objectCode = objectCode;
	}

	/**
	 * @return the fiscalYear
	 */
	public String getFiscalYear()
	{
		return fiscalYear;
	}

	/**
	 * @param fiscalYear the fiscalYear to set
	 */
	public void setFiscalYear(String fiscalYear)
	{
		this.fiscalYear = fiscalYear;
	}

	/**
	 * @param chartOfAccount the chartOfAccount to set
	 */
	public final void setChartOfAccount(String chartOfAccount)
	{
		this.chartOfAccount = chartOfAccount;
	}

	/**
	 * @return the subOc
	 */
	public final String getSubOc()
	{
		return subOc;
	}

	/**
	 * @param subOc the subOc to set
	 */
	public final void setSubOc(String subOc)
	{
		this.subOc = subOc;
	}

	/**
	 * @return the rc
	 */
	public final String getRc()
	{
		return rc;
	}

	/**
	 * @param rc the rc to set
	 */
	public final void setRc(String rc)
	{
		this.rc = rc;
	}

	/**
	 * @return the fy1
	 */
	public final String getFy1()
	{
		return fy1;
	}

	/**
	 * @param fy1 the fy1 to set
	 */
	public final void setFy1(String fy1)
	{
		this.fy1 = fy1;
	}

	/**
	 * @return the fy2
	 */
	public final String getFy2()
	{
		return fy2;
	}

	/**
	 * @param fy2 the fy2 to set
	 */
	public final void setFy2(String fy2)
	{
		this.fy2 = fy2;
	}

	/**
	 * @return the fy3
	 */
	public final String getFy3()
	{
		return fy3;
	}

	/**
	 * @param fy3 the fy3 to set
	 */
	public final void setFy3(String fy3)
	{
		this.fy3 = fy3;
	}

	/**
	 * @return the fy4
	 */
	public final String getFy4()
	{
		return fy4;
	}

	/**
	 * @param fy4 the fy4 to set
	 */
	public final void setFy4(String fy4)
	{
		this.fy4 = fy4;
	}

	/**
	 * @return the fy5
	 */
	public final String getFy5()
	{
		return fy5;
	}

	/**
	 * @param fy5 the fy5 to set
	 */
	public final void setFy5(String fy5)
	{
		this.fy5 = fy5;
	}

	/**
	 * @return the fy6
	 */
	public final String getFy6()
	{
		return fy6;
	}

	/**
	 * @param fy6 the fy6 to set
	 */
	public final void setFy6(String fy6)
	{
		this.fy6 = fy6;
	}

	/**
	 * @return the fy7
	 */
	public final String getFy7()
	{
		return fy7;
	}

	/**
	 * @param fy7 the fy7 to set
	 */
	public final void setFy7(String fy7)
	{
		this.fy7 = fy7;
	}

	/**
	 * @return the fy8
	 */
	public final String getFy8()
	{
		return fy8;
	}

	/**
	 * @param fy8 the fy8 to set
	 */
	public final void setFy8(String fy8)
	{
		this.fy8 = fy8;
	}

	/**
	 * @return the fy9
	 */
	public final String getFy9()
	{
		return fy9;
	}

	/**
	 * @param fy9 the fy9 to set
	 */
	public final void setFy9(String fy9)
	{
		this.fy9 = fy9;
	}

	/**
	 * @return the fy10
	 */
	public String getFy10()
	{
		return fy10;
	}
	/**
	 * @param fy10 the fy10 to set
	 */
	public void setFy10(String fy10)
	{
		this.fy10 = fy10;
	}
	/**
	 * @return the fy11
	 */
	public String getFy11()
	{
		return fy11;
	}
	/**
	 * @param fy11 the fy11 to set
	 */
	public void setFy11(String fy11)
	{
		this.fy11 = fy11;
	}
	/**
	 * @return the fy12
	 */
	public String getFy12()
	{
		return fy12;
	}
	/**
	 * @param fy12 the fy12 to set
	 */
	public void setFy12(String fy12)
	{
		this.fy12 = fy12;
	}
	/**
	 * @return the fy13
	 */
	public String getFy13()
	{
		return fy13;
	}
	/**
	 * @param fy13 the fy13 to set
	 */
	public void setFy13(String fy13)
	{
		this.fy13 = fy13;
	}
	/**
	 * @return the fy14
	 */
	public String getFy14()
	{
		return fy14;
	}
	/**
	 * @param fy14 the fy14 to set
	 */
	public void setFy14(String fy14)
	{
		this.fy14 = fy14;
	}
	/**
	 * @return the fy15
	 */
	public String getFy15()
	{
		return fy15;
	}
	/**
	 * @param fy15 the fy15 to set
	 */
	public void setFy15(String fy15)
	{
		this.fy15 = fy15;
	}
	/**
	 * @return the fy16
	 */
	public String getFy16()
	{
		return fy16;
	}
	/**
	 * @param fy16 the fy16 to set
	 */
	public void setFy16(String fy16)
	{
		this.fy16 = fy16;
	}
	/**
	 * @return the fy17
	 */
	public String getFy17()
	{
		return fy17;
	}
	/**
	 * @param fy17 the fy17 to set
	 */
	public void setFy17(String fy17)
	{
		this.fy17 = fy17;
	}
	/**
	 * @return the fy18
	 */
	public String getFy18()
	{
		return fy18;
	}
	/**
	 * @param fy18 the fy18 to set
	 */
	public void setFy18(String fy18)
	{
		this.fy18 = fy18;
	}
	/**
	 * @return the fy19
	 */
	public String getFy19()
	{
		return fy19;
	}
	/**
	 * @param fy19 the fy19 to set
	 */
	public void setFy19(String fy19)
	{
		this.fy19 = fy19;
	}
	/**
	 * @return the fy20
	 */
	public String getFy20()
	{
		return fy20;
	}
	/**
	 * @param fy20 the fy20 to set
	 */
	public void setFy20(String fy20)
	{
		this.fy20 = fy20;
	}
	/**
	 * @return the fy21
	 */
	public String getFy21()
	{
		return fy21;
	}
	/**
	 * @param fy21 the fy21 to set
	 */
	public void setFy21(String fy21)
	{
		this.fy21 = fy21;
	}
	/**
	 * @return the fy22
	 */
	public String getFy22()
	{
		return fy22;
	}
	/**
	 * @param fy22 the fy22 to set
	 */
	public void setFy22(String fy22)
	{
		this.fy22 = fy22;
	}
	/**
	 * @return the fy23
	 */
	public String getFy23()
	{
		return fy23;
	}
	/**
	 * @param fy23 the fy23 to set
	 */
	public void setFy23(String fy23)
	{
		this.fy23 = fy23;
	}
	/**
	 * @return the fy24
	 */
	public String getFy24()
	{
		return fy24;
	}
	/**
	 * @param fy24 the fy24 to set
	 */
	public void setFy24(String fy24)
	{
		this.fy24 = fy24;
	}
	/**
	 * @return the fy25
	 */
	public String getFy25()
	{
		return fy25;
	}
	/**
	 * @param fy25 the fy25 to set
	 */
	public void setFy25(String fy25)
	{
		this.fy25 = fy25;
	}
	/**
	 * @return the fy26
	 */
	public String getFy26()
	{
		return fy26;
	}
	/**
	 * @param fy26 the fy26 to set
	 */
	public void setFy26(String fy26)
	{
		this.fy26 = fy26;
	}
	/**
	 * @return the fy27
	 */
	public String getFy27()
	{
		return fy27;
	}
	/**
	 * @param fy27 the fy27 to set
	 */
	public void setFy27(String fy27)
	{
		this.fy27 = fy27;
	}
	/**
	 * @return the fy28
	 */
	public String getFy28()
	{
		return fy28;
	}
	/**
	 * @param fy28 the fy28 to set
	 */
	public void setFy28(String fy28)
	{
		this.fy28 = fy28;
	}
	/**
	 * @return the fy29
	 */
	public String getFy29()
	{
		return fy29;
	}
	/**
	 * @param fy29 the fy29 to set
	 */
	public void setFy29(String fy29)
	{
		this.fy29 = fy29;
	}
	/**
	 * @return the fy30
	 */
	public String getFy30()
	{
		return fy30;
	}
	/**
	 * @param fy30 the fy30 to set
	 */
	public void setFy30(String fy30)
	{
		this.fy30 = fy30;
	}
	/**
	 * @return the fy31
	 */
	public String getFy31()
	{
		return fy31;
	}
	/**
	 * @param fy31 the fy31 to set
	 */
	public void setFy31(String fy31)
	{
		this.fy31 = fy31;
	}
	/**
	 * @return the total
	 */
	public final String getTotal()
	{
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public final void setTotal(String total)
	{
		this.total = total;
	}

	/**
	 * @return the id
	 */
	public final String getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public final void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return the delStatus
	 */
	public final String getDelStatus()
	{
		return delStatus;
	}

	/**
	 * @param delStatus the delStatus to set
	 */
	public final void setDelStatus(String delStatus)
	{
		this.delStatus = delStatus;
	}

	/**
	 * @return the contractStartFY
	 */
	public String getContractStartFY()
	{
		return contractStartFY;
	}

	/**
	 * @param contractStartFY the contractStartFY to set
	 */
	public void setContractStartFY(String contractStartFY)
	{
		this.contractStartFY = contractStartFY;
	}

	/**
	 * @return the contractEndFY
	 */
	public String getContractEndFY()
	{
		return contractEndFY;
	}

	/**
	 * @param contractEndFY the contractEndFY to set
	 */
	public void setContractEndFY(String contractEndFY)
	{
		this.contractEndFY = contractEndFY;
	}

	/**
	 * @return the docId
	 */
	public String getDocId()
	{
		return docId;
	}

	/**
	 * @param docId the docId to set
	 */
	public void setDocId(String docId)
	{
		this.docId = docId;
	}

	/**
	 * @return the activeFlag
	 */
	public String getActiveFlag()
	{
		return activeFlag;
	}

	/**
	 * @param activeFlag the activeFlag to set
	 */
	public void setActiveFlag(String activeFlag)
	{
		this.activeFlag = activeFlag;
	}

	@Override
	public String toString()
	{
		return "PaymentAllocationBean [id=" + id + ", chartOfAccount=" + chartOfAccount + ", subOc=" + subOc + ", rc="
				+ rc + ", unitOfAppropriation=" + unitOfAppropriation + ", budgetCode=" + budgetCode + ", objectCode="
				+ objectCode + ", fiscalYear=" + fiscalYear + ", fy1=" + fy1 + ", fy2=" + fy2 + ", fy3=" + fy3
				+ ", fy4=" + fy4 + ", fy5=" + fy5 + ", fy6=" + fy6 + ", fy7=" + fy7 + ", fy8=" + fy8 + ", fy9=" + fy9
				+ ", ammount=" + ammount + ", docId=" + docId + ", activeFlag=" + activeFlag + ", total=" + total
				+ ", delStatus=" + delStatus + ", contractStartFY=" + contractStartFY + ", contractEndFY="
				+ contractEndFY + "]";
	}

}
