package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

/**
 * This class is a bean which maintains screen S316 OTPS - Operations & Support
 * details.
 * 
 * <ul>
 * <li>This bean is used for grid functionality and as buffer for to/for of
 * values in grid
 * </ul>
 * 
 */
public class CBOperationSupportBean extends CBGridBean
{

	//@RegExp(value ="^\\d{0,22}")
	private String id; // this is index key/primary key of table referenced
	private String opAndSupportName;
	private String invoiceDetailId;
	private String invoicedAmt = HHSConstants.STRING_ZERO;
	private String fyBudget = HHSConstants.STRING_ZERO;
	private String ytdInvoicedAmt = HHSConstants.STRING_ZERO;

	private String modificationAmt = HHSConstants.STRING_ZERO;
	private String updateAmt = HHSConstants.STRING_ZERO;
	private String amendAmt = HHSConstants.STRING_ZERO;
	private String remainingAmt = HHSConstants.STRING_ZERO;
	private CBGridBean cbGridBeanObj;
	private String proposedBudget = HHSConstants.STRING_ZERO;

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return the opAndSupportName
	 */
	public String getOpAndSupportName()
	{
		return opAndSupportName;
	}

	/**
	 * @param opAndSupportName the opAndSupportName to set
	 */
	public void setOpAndSupportName(String opAndSupportName)
	{
		this.opAndSupportName = opAndSupportName;
	}

	/**
	 * @return the fyBudget
	 */
	public String getFyBudget()
	{
		return fyBudget;
	}

	/**
	 * @param fyBudget the fyBudget to set
	 */
	public void setFyBudget(String fyBudget)
	{
		this.fyBudget = fyBudget;
	}

	/**
	 * @return the ytdInvoicedAmt
	 */
	public String getYtdInvoicedAmt()
	{
		return ytdInvoicedAmt;
	}

	/**
	 * @param ytdInvoicedAmt the ytdInvoicedAmt to set
	 */
	public void setYtdInvoicedAmt(String ytdInvoicedAmt)
	{
		this.ytdInvoicedAmt = ytdInvoicedAmt;
	}

	/**
	 * @return the modificationAmt
	 */
	public String getModificationAmt()
	{
		return modificationAmt;
	}

	/**
	 * @param modificationAmt the modificationAmt to set
	 */
	public void setModificationAmt(String modificationAmt)
	{
		this.modificationAmt = modificationAmt;
	}

	/**
	 * @return the updateAmt
	 */
	public String getUpdateAmt()
	{
		return updateAmt;
	}

	/**
	 * @param updateAmt the updateAmt to set
	 */
	public void setUpdateAmt(String updateAmt)
	{
		this.updateAmt = updateAmt;
	}

	/**
	 * @return the amendAmt
	 */
	public String getAmendAmt()
	{
		return amendAmt;
	}

	/**
	 * @param amendAmt the amendAmt to set
	 */
	public void setAmendAmt(String amendAmt)
	{
		this.amendAmt = amendAmt;
	}

	/**
	 * @return the remainingAmt
	 */
	public String getRemainingAmt()
	{
		return remainingAmt;
	}

	/**
	 * @param remainingAmt the remainingAmt to set
	 */
	public void setRemainingAmt(String remainingAmt)
	{
		this.remainingAmt = remainingAmt;
	}

	/**
	 * @return the cbGridBeanObj
	 */
	public CBGridBean getCbGridBeanObj()
	{
		return cbGridBeanObj;
	}

	/**
	 * @param cbGridBeanObj the cbGridBeanObj to set
	 */
	public void setCbGridBeanObj(CBGridBean cbGridBeanObj)
	{
		this.cbGridBeanObj = cbGridBeanObj;
	}

	/**
	 * @return the invoiceDetailId
	 */
	public String getInvoiceDetailId()
	{
		return invoiceDetailId;
	}

	/**
	 * @param invoiceDetailId the invoiceDetailId to set
	 */
	public void setInvoiceDetailId(String invoiceDetailId)
	{
		this.invoiceDetailId = invoiceDetailId;
	}

	/**
	 * @return the invoicedAmt
	 */
	public String getInvoicedAmt()
	{
		return invoicedAmt;
	}

	/**
	 * @param invoicedAmt the invoicedAmt to set
	 */
	public void setInvoicedAmt(String invoicedAmt)
	{
		this.invoicedAmt = invoicedAmt;
	}

	public String getProposedBudget()
	{
		return proposedBudget;
	}

	public void setProposedBudget(String proposedBudget)
	{
		this.proposedBudget = proposedBudget;
	}

	@Override
	public String toString()
	{
		return "CBOperationSupportBean [id=" + id + ", opAndSupportName=" + opAndSupportName + ", invoiceDetailId="
				+ invoiceDetailId + ", invoicedAmt=" + invoicedAmt + ", fyBudget=" + fyBudget + ", ytdInvoicedAmt="
				+ ytdInvoicedAmt + ", modificationAmt=" + modificationAmt + ", updateAmt=" + updateAmt + ", amendAmt="
				+ amendAmt + ", remainingAmt=" + remainingAmt + ", cbGridBeanObj=" + cbGridBeanObj
				+ ", proposedBudget=" + proposedBudget + "]";
	}

}
