/**
 * 
 */
package com.nyc.hhs.frameworks.grid;

import org.apache.commons.lang.StringUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.DocumentBean;
import com.nyc.hhs.util.DateUtil;

/**
 * This class is used to generate an extension which creates a drop down for
 * provider users.
 * 
 */

public class LinkageGrid implements DecoratorInterface
{
	private static final LogInfo LOG_OBJECT = new LogInfo(LinkageGrid.class);

	/**
	 * This method will generate a grid and populate the table
	 */
	@Override
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo)
	{				
		DocumentBean loDocumentBean = (DocumentBean) aoEachObject;
		String lsUserOrgType = loDocumentBean.getOrgID();
		/** start  QC 8914 read only role R 7.2 **/
		String lsUserSubRole = loDocumentBean.getUserSubRole();
		/** end  QC 8914 read only role R 7.2 **/		
		String lsControl = "";
		if (aoCol.getColumnName().equalsIgnoreCase("date"))
		{
			if (null != loDocumentBean.getDate())
			{
				String lsDate = loDocumentBean.getDate();
				try
				{
					lsControl = DateUtil.getDateByFormat("yyyy-MM-dd hh:mm:ss", "MM/dd/yyyy", lsDate);
				}
				catch (ApplicationException loAppEx)
				{
					LOG_OBJECT.Error("Application Exception in DateUtil", loAppEx);
				}
			}
			else
			{
				lsControl = null;
			}
		}
		
		if (aoCol.getColumnName().equalsIgnoreCase("entityLinked"))
		{
			if (lsUserOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
			{
				lsControl = setEntityLinkedForProvider(loDocumentBean);
			}
			else if (lsUserOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG))
			{
				/** start  QC 8914 read only role R 7.2 **/
				if(ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase(lsUserSubRole)) 
				{
					lsControl = setEntityLinkedForCityObserver(loDocumentBean);
				}else
				{
					lsControl = setEntityLinkedForCity(loDocumentBean);
				}
				/** end QC 8914 read only role R 7.2 **/
			}
			else if (lsUserOrgType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
			{
				lsControl = setEntityLinkedForAgency(loDocumentBean);
			}
		}
		
		
		return lsControl;
	}
	/**
	 * This method is used to set entity linked for agency
	 * @param loDocumentBean
	 * @return lsControl
	 */
	private String setEntityLinkedForAgency(DocumentBean loDocumentBean)
	{
		String lsControl;
		if (null == loDocumentBean.getEntityLinked())
		{
			lsControl = null;
		}
		else
		{
			//Added for Release 6 document linked Returned payment entity
			if ((loDocumentBean.getPermissionType() != null && ((loDocumentBean.getPermissionType().equalsIgnoreCase(
					"P") || loDocumentBean.getPermissionType().equalsIgnoreCase("FP")) && (loDocumentBean
					.getEntityLinked().equalsIgnoreCase("Award") || loDocumentBean.getEntityLinked().equalsIgnoreCase(
					"Proposal"))))
					|| (loDocumentBean.getPermissionType() != null && ((loDocumentBean.getPermissionType()
							.equalsIgnoreCase("F") || loDocumentBean.getPermissionType().equalsIgnoreCase("FP")) && (loDocumentBean
							.getEntityLinked().equalsIgnoreCase("Budget")  || loDocumentBean.getEntityLinked()
							.equalsIgnoreCase("Invoice"))))
					|| ((loDocumentBean.getUserRole() != null && loDocumentBean.getUserRole().contains("ACCO")) && loDocumentBean
							.getEntityLinked().equalsIgnoreCase("Agency Award"))
					|| (loDocumentBean.getEntityLinked().equalsIgnoreCase("Business Application")
							|| loDocumentBean.getEntityLinked().equalsIgnoreCase("Procurement") || loDocumentBean
							.getEntityLinked().equalsIgnoreCase("Service Application"))
							|| loDocumentBean.getEntityLinked().equalsIgnoreCase("Returned Payment"))
				//Added for Release 6 document linked Returned payment entity end
							{
				if (StringUtils.isBlank(loDocumentBean.getParentEntityName())
						|| loDocumentBean.getParentEntityName().equalsIgnoreCase("null"))
				{
					lsControl = "<a href=\"javascript:void(0);\" onclick=\"getEntityLinked(" + "'"
							+ loDocumentBean.getFiscalYearId() + "','" + loDocumentBean.getBudgetTypeId() + "','"
							+ loDocumentBean.getBusinessId() + "','" + loDocumentBean.getBusinessAppId() + "','"
							+ loDocumentBean.getAppStatus() + "','" + loDocumentBean.getOrgID() + "','"
							+ loDocumentBean.getEntityLinked() + "','" + loDocumentBean.getCtNum() + "','"
							+ loDocumentBean.getEntityId() + "','" + loDocumentBean.getParentId() + "','"
							+ loDocumentBean.getOrganizationId() + "','" + loDocumentBean.getStatusIdProcurement()
							+ "','" + loDocumentBean.getContractId() + "','" + loDocumentBean.getAppId() + "','"
							+ loDocumentBean.getServiceAppId() + "');\"> " + loDocumentBean.getEntityLinked() + "</a>";
				}
				else
				{
					lsControl = "<a href=\"javascript:void(0);\" onclick=\"getEntityLinked(" + "'"
							+ loDocumentBean.getFiscalYearId() + "','" + loDocumentBean.getBudgetTypeId() + "','"
							+ loDocumentBean.getBusinessId() + "','" + loDocumentBean.getBusinessAppId() + "','"
							+ loDocumentBean.getAppStatus() + "','" + loDocumentBean.getOrgID() + "','"
							+ loDocumentBean.getEntityLinked() + "','" + loDocumentBean.getCtNum() + "','"
							+ loDocumentBean.getEntityId() + "','" + loDocumentBean.getParentId() + "','"
							+ loDocumentBean.getOrganizationId() + "','" + loDocumentBean.getStatusIdProcurement()
							+ "','" + loDocumentBean.getContractId() + "','" + loDocumentBean.getAppId() + "','"
							+ loDocumentBean.getServiceAppId() + "');\"> " + loDocumentBean.getEntityLinked() + "("
							+ loDocumentBean.getParentEntityName() + ")" + "</a>";
				}
			}
			else
			{
				if (StringUtils.isBlank(loDocumentBean.getParentEntityName())
						|| loDocumentBean.getParentEntityName().equalsIgnoreCase("null"))
				{
					lsControl = loDocumentBean.getEntityLinked();
				}
				else
				{
					lsControl = loDocumentBean.getEntityLinked() + "(" + loDocumentBean.getParentEntityName() + ")";
				}
			}
		}
		return lsControl;
	}
	/**
	 * This method is used to set entity linked for city
	 * @param loDocumentBean
	 * @return lsControl
	 */
	private String setEntityLinkedForCity(DocumentBean loDocumentBean)
	{
		String lsControl;
		if (null == loDocumentBean.getEntityLinked())
		{
			lsControl = null;
		}
		else
		{
			if ((loDocumentBean.getPermissionType() != null && ((loDocumentBean.getPermissionType().equalsIgnoreCase(
					"P") || loDocumentBean.getPermissionType().equalsIgnoreCase("FP")) && (loDocumentBean
					.getEntityLinked().equalsIgnoreCase("Award") || loDocumentBean.getEntityLinked().equalsIgnoreCase(
					"Proposal"))))
					|| (loDocumentBean.getPermissionType() != null && ((loDocumentBean.getPermissionType()
							.equalsIgnoreCase("F") || loDocumentBean.getPermissionType().equalsIgnoreCase("FP")) && (loDocumentBean
							.getEntityLinked().equalsIgnoreCase("Budget") || loDocumentBean.getEntityLinked()
							.equalsIgnoreCase("Invoice"))))
					|| ((loDocumentBean.getUserRole() != null && loDocumentBean.getUserRole().contains("ACCO")) && loDocumentBean
							.getEntityLinked().equalsIgnoreCase("Agency Award"))
					|| (loDocumentBean.getEntityLinked().equalsIgnoreCase("Business Application")
							|| loDocumentBean.getEntityLinked().equalsIgnoreCase("Procurement") || loDocumentBean
							.getEntityLinked().equalsIgnoreCase("Service Application")))

			{
				if (StringUtils.isBlank(loDocumentBean.getParentEntityName())
						|| loDocumentBean.getParentEntityName().equalsIgnoreCase("null"))
				{
					lsControl = "<a href=\"javascript:void(0);\" onclick=\"getEntityLinked(" + "'"
							+ loDocumentBean.getFiscalYearId() + "','" + loDocumentBean.getBudgetTypeId() + "','"
							+ loDocumentBean.getBusinessId() + "','" + loDocumentBean.getBusinessAppId() + "','"
							+ loDocumentBean.getAppStatus() + "','" + loDocumentBean.getOrgID() + "','"
							+ loDocumentBean.getEntityLinked() + "','" + loDocumentBean.getCtNum() + "','"
							+ loDocumentBean.getEntityId() + "','" + loDocumentBean.getParentId() + "','"
							+ loDocumentBean.getOrganizationId() + "','" + loDocumentBean.getStatusIdProcurement()
							+ "','" + loDocumentBean.getContractId() + "','" + loDocumentBean.getAppId() + "','"
							+ loDocumentBean.getServiceAppId() + "');\"> " + loDocumentBean.getEntityLinked() + "</a>";
				}
				else
				{
					lsControl = "<a href=\"javascript:void(0);\" onclick=\"getEntityLinked(" + "'"
							+ loDocumentBean.getFiscalYearId() + "','" + loDocumentBean.getBudgetTypeId() + "','"
							+ loDocumentBean.getBusinessId() + "','" + loDocumentBean.getBusinessAppId() + "','"
							+ loDocumentBean.getAppStatus() + "','" + loDocumentBean.getOrgID() + "','"
							+ loDocumentBean.getEntityLinked() + "','" + loDocumentBean.getCtNum() + "','"
							+ loDocumentBean.getEntityId() + "','" + loDocumentBean.getParentId() + "','"
							+ loDocumentBean.getOrganizationId() + "','" + loDocumentBean.getStatusIdProcurement()
							+ "','" + loDocumentBean.getContractId() + "','" + loDocumentBean.getAppId() + "','"
							+ loDocumentBean.getServiceAppId() + "');\"> " + loDocumentBean.getEntityLinked() + "("
							+ loDocumentBean.getParentEntityName() + ")" + "</a>";
				}
			}
			else
			{
				if (StringUtils.isBlank(loDocumentBean.getParentEntityName())
						|| loDocumentBean.getParentEntityName().equalsIgnoreCase("null"))
				{
					lsControl = loDocumentBean.getEntityLinked();
				}
				else
				{
					lsControl = loDocumentBean.getEntityLinked() + "(" + loDocumentBean.getParentEntityName() + ")";
				}
			}
		}
		return lsControl;
	}

	//** Start QC 8914 read only role R 7.2 ***//
	
	/**
	 * This method is used to set entity linked for city
	 * @param loDocumentBean
	 * @return lsControl
	 */
	private String setEntityLinkedForCityObserver(DocumentBean loDocumentBean)
	{
		String lsControl;
		if (null == loDocumentBean.getEntityLinked())
		{
			lsControl = null;
		}
		else
		{		
			
			if (StringUtils.isBlank(loDocumentBean.getParentEntityName())
					|| loDocumentBean.getParentEntityName().equalsIgnoreCase("null"))
			{
				lsControl = loDocumentBean.getEntityLinked();
			}
			else
			{
				lsControl = loDocumentBean.getEntityLinked() + "(" + loDocumentBean.getParentEntityName() + ")";
			}
			
		}
		return lsControl;
	}
	
	//** End QC 8914 read only role R 7.2 ***//
	
	/**
	 * @param loDocumentBean
	 * @return
	 */
	private String setEntityLinkedForProvider(DocumentBean loDocumentBean)
	{
		String lsControl;
		if (null == loDocumentBean.getEntityLinked())
		{
			lsControl = null;
		}
		else
		{
			if ((HHSConstants.STRING_TRUE.equalsIgnoreCase(loDocumentBean.getContractAccess()))
					&& ((loDocumentBean.getPermissionType() != null && ((loDocumentBean.getPermissionType()
							.equalsIgnoreCase("P") || loDocumentBean.getPermissionType().equalsIgnoreCase("FP")) && (loDocumentBean
							.getEntityLinked().equalsIgnoreCase("Award") || loDocumentBean.getEntityLinked()
							.equalsIgnoreCase("Proposal"))))
							|| (loDocumentBean.getPermissionType() != null && ((loDocumentBean.getPermissionType()
									.equalsIgnoreCase("F") || loDocumentBean.getPermissionType().equalsIgnoreCase("FP")) && (loDocumentBean
									.getEntityLinked().equalsIgnoreCase("Budget") || loDocumentBean.getEntityLinked()
									.equalsIgnoreCase("Invoice"))))
							|| ((loDocumentBean.getUserRole() != null && loDocumentBean.getUserRole().contains("ACCO")) && loDocumentBean
									.getEntityLinked().equalsIgnoreCase("Agency Award")) || (loDocumentBean
							.getEntityLinked().equalsIgnoreCase("Business Application")
							|| loDocumentBean.getEntityLinked().equalsIgnoreCase("Procurement") || loDocumentBean
							.getEntityLinked().equalsIgnoreCase("Service Application"))))
			{
				if (StringUtils.isBlank(loDocumentBean.getParentEntityName())
						|| loDocumentBean.getParentEntityName().equalsIgnoreCase("null"))
				{
					lsControl = "<a href=\"javascript:void(0);\" onclick=\"getEntityLinked(" + "'"
							+ loDocumentBean.getFiscalYearId() + "','" + loDocumentBean.getBudgetTypeId() + "','"
							+ loDocumentBean.getBusinessId() + "','" + loDocumentBean.getBusinessAppId() + "','"
							+ loDocumentBean.getAppStatus() + "','" + loDocumentBean.getOrgID() + "','"
							+ loDocumentBean.getEntityLinked() + "','" + loDocumentBean.getCtNum() + "','"
							+ loDocumentBean.getEntityId() + "','" + loDocumentBean.getParentId() + "','"
							+ loDocumentBean.getOrganizationId() + "','" + loDocumentBean.getStatusIdProcurement()
							+ "','" + loDocumentBean.getContractId() + "','" + loDocumentBean.getAppId() + "','"
							+ loDocumentBean.getServiceAppId() + "');\"> " + loDocumentBean.getEntityLinked() + "</a>";
				}
				else
				{
					lsControl = "<a href=\"javascript:void(0);\" onclick=\"getEntityLinked(" + "'"
							+ loDocumentBean.getFiscalYearId() + "','" + loDocumentBean.getBudgetTypeId() + "','"
							+ loDocumentBean.getBusinessId() + "','" + loDocumentBean.getBusinessAppId() + "','"
							+ loDocumentBean.getAppStatus() + "','" + loDocumentBean.getOrgID() + "','"
							+ loDocumentBean.getEntityLinked() + "','" + loDocumentBean.getCtNum() + "','"
							+ loDocumentBean.getEntityId() + "','" + loDocumentBean.getParentId() + "','"
							+ loDocumentBean.getOrganizationId() + "','" + loDocumentBean.getStatusIdProcurement()
							+ "','" + loDocumentBean.getContractId() + "','" + loDocumentBean.getAppId() + "','"
							+ loDocumentBean.getServiceAppId() + "');\"> " + loDocumentBean.getEntityLinked() + "("
							+ loDocumentBean.getParentEntityName() + ")" + "</a>";
				}
			}
			else
			{
				if (StringUtils.isBlank(loDocumentBean.getParentEntityName())
						|| loDocumentBean.getParentEntityName().equalsIgnoreCase("null"))
				{
					lsControl = loDocumentBean.getEntityLinked();
				}
				else
				{
					lsControl = loDocumentBean.getEntityLinked() + "(" + loDocumentBean.getParentEntityName() + ")";
				}
			}
		}
		return lsControl;
	}

	/**
	 * This method will generate html code for a particular column header of
	 * table depending upon the input column name
	 * 
	 * @param aoCol a column object
	 * 
	 * @return a string value of html code formed
	 */
	public String getControlForHeading(Column aoCol)
	{
		String lsControlTxt = "RESUME";

		return lsControlTxt;
	}
}
