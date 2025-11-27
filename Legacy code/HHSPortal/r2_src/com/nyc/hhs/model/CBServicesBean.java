package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

/**
 * @author anushka.goel
 *This bean is Added in R7 for Cost-Center
 */
public class CBServicesBean extends CBGridBean
{

	private String servicesDetailId;
	private String servicesInvoiceDetailId;
	private String costCenterInvoiceDetailId;
	private String serviceName;
	private String costCenterDetailId;
	@Length(max = 50)
	private String costCenterName;
	private String costCenter;
	//@RegExp(value ="^\\d{0,22}")
	//BC_SERVICES_DETAIL_ID, COST_CENTER_DETAILS_ID
	private String id;
	private String units = HHSConstants.STRING_ZERO;
	private String fyBudget = HHSConstants.STRING_ZERO;
	private String ytdInvoicedAmt = HHSConstants.STRING_ZERO;
	private String remainingAmt = HHSConstants.STRING_ZERO;
	private String modificationAmt = HHSConstants.STRING_ZERO;
	private String activeFlag;
	private String modUnits = HHSConstants.STRING_ZERO;
	private String approvedUnits = HHSConstants.STRING_ZERO;
	private String proposedUnits = HHSConstants.STRING_ZERO;
	private String invUnits = HHSConstants.STRING_ZERO;
	private String ytdUnits = HHSConstants.STRING_ZERO;
	private String remUnits = HHSConstants.STRING_ZERO;
	private String proposedIncome = HHSConstants.STRING_ZERO;
	
	public String getModificationAmt()
	{
		return modificationAmt;
	}
	public void setModificationAmt(String modificationAmt)
	{
		this.modificationAmt = modificationAmt;
	}
	public String getModUnits()
	{
		return modUnits;
	}
	public void setModUnits(String modUnits)
	{
		this.modUnits = modUnits;
	}
	public String getProposedIncome()
	{
		return proposedIncome;
	}
	public void setProposedIncome(String proposedIncome)
	{
		this.proposedIncome = proposedIncome;
	}
	
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
	 * @return the serviceMasterId
	 */
	
	
	/**
	 * @return the serviceName
	 */
	public String getServiceName()
	{
		return serviceName;
	}
	public String getServicesInvoiceDetailId()
	{
		return servicesInvoiceDetailId;
	}
	public void setServicesInvoiceDetailId(String servicesInvoiceDetailId)
	{
		this.servicesInvoiceDetailId = servicesInvoiceDetailId;
	}
	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}
	
	/**
	 * @return the costCenter
	 */
	public String getCostCenter()
	{
		return costCenter;
	}
	/**
	 * @param costCenter the costCenter to set
	 */
	public void setCostCenter(String costCenter)
	{
		this.costCenter = costCenter;
	}

	/**
	 * @return the costCenterName
	 */
	public String getCostCenterName()
	{
		return costCenterName;
	}
	/**
	 * @param costCenterName the costCenterName to set
	 */
	public void setCostCenterName(String costCenterName)
	{
		this.costCenterName = costCenterName;
	}

	/**
	 * @return the remainAmt
	 */
	public String getRemainingAmt()
	{
		return remainingAmt;
	}
	/**
	 * @param remainAmt the remainAmt to set
	 */
	public void setRemainingAmt(String remainingAmt)
	{
		this.remainingAmt = remainingAmt;
	}
 
	/**
	 * @return the invUnits
	 */
	public String getInvUnits()
	{
		return invUnits;
	}
	/**
	 * @param invUnits the invUnits to set
	 */
	public void setInvUnits(String invUnits)
	{
		this.invUnits = invUnits;
	}
	
	/**
	 * @return the ytdUnits
	 */
	public String getYtdUnits()
	{
		return ytdUnits;
	}
	/**
	 * @param ytdUnits the ytdUnits to set
	 */
	public void setYtdUnits(String ytdUnits)
	{
		this.ytdUnits = ytdUnits;
	}
	
	/**
	 * @return the remUnits
	 */
	public String getRemUnits()
	{
		return remUnits;
	}
	/**
	 * @param remUnits the remUnits to set
	 */
	public void setRemUnits(String remUnits)
	{
		this.remUnits = remUnits;
	}
	
	/**
	 * @return the servicesDetailId
	 */
	public String getServicesDetailId()
	{
		return servicesDetailId;
	}
	/**
	 * @param servicesDetailId the servicesDetailId to set
	 */
	public void setServicesDetailId(String servicesDetailId)
	{
		this.servicesDetailId = servicesDetailId;
	}
	
	/**
	 * @return the units
	 */
	public String getUnits()
	{
		return units;
	}
	/**
	 * @param units the units to set
	 */
	public void setUnits(String Units)
	{
		this.units = Units;
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
	 * @return activeFlag
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
		return "CBServicesBean [servicesDetailId=" + servicesDetailId + ",servicesInvoiceDetailId="+ servicesInvoiceDetailId+", serviceName=" + serviceName
				
				+ ", units= " +units + ", fyBudget = " + fyBudget 
				+ ", ytdInvoicedAmt=" + ytdInvoicedAmt + ", remainingAmt=" + remainingAmt + ", activeFlag="
				+  activeFlag + ", lsModifyUnits="  
				+ ", invUnits=" + invUnits + ", ytdUnits=" + ytdUnits + ",remUnits="
				+ remUnits + " , costCenterName=" +costCenterName +" , costCenter="+ costCenter +"]";
	}
	public String getCostCenterInvoiceDetailId()
	{
		return costCenterInvoiceDetailId;
	}
	public void setCostCenterInvoiceDetailId(String costCenterInvoiceDetailId)
	{
		this.costCenterInvoiceDetailId = costCenterInvoiceDetailId;
	}
	public String getCostCenterDetailId()
	{
		return costCenterDetailId;
	}
	public void setCostCenterDetailId(String costCenterDetailId)
	{
		this.costCenterDetailId = costCenterDetailId;
	}
	public String getApprovedUnits() {
		return approvedUnits;
	}
	public void setApprovedUnits(String approvedUnits) {
		this.approvedUnits = approvedUnits;
	}
	public String getProposedUnits() {
		return proposedUnits;
	}
	public void setProposedUnits(String proposedUnits) {
		this.proposedUnits = proposedUnits;
	}

}
