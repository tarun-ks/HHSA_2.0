package com.nyc.hhs.frameworks.grid;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.ActionStatusBean;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.util.ActionStatusUtil;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * This class is used to create custom select element for contract list grid
 * view. Values in Action column on contract list page for Acclerator/Agency are
 * populated using it as a decorator class in grid.
 * 
 * 
 */
public class ContractActionFinancialExtension implements DecoratorInterface
{

	/**
	 * This method is used to create actions drop down for finance contract list
	 * grid table. <br>
	 * Updated Method in R4
	 * 
	 * <ul>
	 * <li>1. Contract Status to Action mappings are defined in
	 * ContractStatusToActionMapping.xml</li>
	 * <li>2. We get the document object by retrieving the above xml mapping
	 * from cache using its key name financeMapping.xml defined in
	 * cachenew.properties.</li>
	 * <li>3. Then we define the path by passing ContractStatus as a variable so
	 * that we get the the required node list</li>
	 * <li>4. We get the required node list by using getElementList of XMLUtil.</li>
	 * <li>5. Then we declare a string builder and append all node values to
	 * option attribute so that it will be displayed in dropdown on screen.</li>
	 * </ul>
	 * <br>
	 * 
	 * 
	 * @param aoEachObject Bean Name
	 * @param aoCol Column name
	 * @param aoSeqNo Sequence Number
	 * @return control - string
	 * @throws ApplicationException - ApplicationException Object
	 * 
	 */

	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{
		ContractList loContractList = (ContractList) aoEachObject;
		// Observation Changes Release 3.4.0.
		loContractList.setContractTitle(StringEscapeUtils.escapeHtml(loContractList.getContractTitle()));
		String lsUserRole = loContractList.getActions();
		String lsUserOrg = loContractList.getOrgType();

        /*[Start] R9.6.4 QC9701 */
		String lsConAgencyName = loContractList.getContractAgencyName();
		Map<Integer,String> loMap = ActionStatusUtil.getMoActionMapByAgency(lsConAgencyName);
		ActionStatusBean loActionStat = ActionStatusUtil.getMoActionByAgency(lsConAgencyName);
        /*[End] R9.6.4 QC9701 */

		//<!-- [Start] R9.7.3 QC9719 -->
		boolean  lbActionEnableFlag = true ;
		if(  loContractList.getActionDisable()  > 0  )  { lbActionEnableFlag  = false;}
		//<!-- [End] R9.7.3 QC9719 -->   
		//<!-- [Start] R9.7.6 QC9730 -->
		boolean  lbActionExceptionFlag = true ;
		if(  loContractList.getActionException()  > 0 )  {	lbActionExceptionFlag  = true;
		}else {   lbActionExceptionFlag  = false;  }
		//<!-- [End] R9.7.6 QC9730 -->
		StringBuilder lsControl = new StringBuilder();
		// Updated in R7 to check contract is flagged or not
		if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.BUDGET_CONTRACT_TITLE))
		{
			if (null != loContractList.getContractFlagged()
					&& loContractList.getContractFlagged().equalsIgnoreCase(HHSR5Constants.STRING_ONE))
			{
				if (loContractList.isPendingAmendment() && loContractList.getContractFlagged().equalsIgnoreCase(HHSR5Constants.STRING_ONE))
				{
					lsControl.append("<span class='contractInfoTitle'><span class='infoMessageForContract'><label title="
							+ "'This contract has been flagged." + "'>&nbsp;</label>" +"</span>");
					lsControl.append("<span class='exclIcon'> <label title="
							+ "'An amendment is currently in progress for this contract." + "'>&nbsp;</label>"+ "</span>");
					lsControl.append("<span class='breakAll'>"+loContractList.getContractTitle()+"</span></span>");
				}
				else
				{
					if (!loContractList.isPendingAmendment() && loContractList.getContractFlagged().equalsIgnoreCase(HHSR5Constants.STRING_ONE))
					{
						lsControl.append("<span class='infoMessageForContract'> <label title="
								+ "'This contract has been flagged." + "'>&nbsp;</label>" + loContractList.getContractTitle()
								+ "</span>");
					}
					else
					{
						lsControl.append(loContractList.getContractTitle());
					}
				}
			}
			else
			{
				// R7 end
				if (loContractList.isPendingAmendment())
				{
					lsControl.append("<span class='exclIcon'> <label title="
							+ "'An amendment is currently in progress for this contract." + "'>&nbsp;</label>"
							+ loContractList.getContractTitle() + "</span>");
				}
				else
				{
					lsControl.append(loContractList.getContractTitle());
				}
			}
		//R7 Update End	
		}
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.BMC_CONTRACT_VALUE))
		{
			lsControl.append("<label class='tableContractValue'  " + "style=' text-align:right; float:right'>"
					+ loContractList.getContractValue() + "</label>");
		}
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.CONTRACT_STATUS))
		{
			if (loContractList.getContractStatus().equalsIgnoreCase(HHSConstants.ETL_REG))
			{
				lsControl.append(HHSConstants.PENDING_REG);
			}
			else
			{
				lsControl.append(loContractList.getContractStatus());
			}
		}
		/* Start : Added in R5 */
		else if (aoCol.getColumnName().equalsIgnoreCase("provider"))
		{
			lsControl.append("<a href =\"javascript:void(0)\" onClick=\"viewOrganizationInformation(\'"
					+ loContractList.getProviderOrgId() + "\',\'"
					+ StringEscapeUtils.escapeJavaScript(loContractList.getProvider()) + "\')\" >"
					+ loContractList.getProvider() + "</a>");
		}
		/* End : Added in R5 */
		else
		{
			Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					HHSConstants.ACTION_MAPPING_FILE_NAME);
			/* R6: Added attribute contractAgencyId in below append */
			//
			/* R7: Added attribute contractFlagged in below append */
			lsControl.append("<select name=action" + aoSeqNo + " class='contractAmend' id='action" + aoSeqNo
					+ "' style='width: 150px' " + "contractTitle=\"" + loContractList.getContractTitle() + "\" ct=\""
					+ loContractList.getCtId() + "\" provider=\"" + loContractList.getProvider()
					+ "\" contractFlagged=\"" + loContractList.getContractFlagged() + "\" contractTypeId=\""
					+ loContractList.getContractTypeId() + "\" status=\"" + loContractList.getContractStatus()
					+ "\" contractAmount=\"" + loContractList.getContractValue() + "\" contractStartDate=\""
					+ loContractList.getContractStartDate() + "\" contractEndDate=\""
					+ loContractList.getContractEndDate() + "\" contractId=\"" + loContractList.getContractId()
					+ "\" fyConfigFiscalYear=\"" + loContractList.getFyConfigFiscalYear() + "\" statusId=\""
					+ loContractList.getContractStatusId() + "\" contractAgencyId=\""
					+ loContractList.getContractAgencyName() + "\" functionName=\"amendContract" + "\"><option title="
					+ "'I need to...'" + "value=I need to... >I need to...</option>");

			//R7 End
			if (lsUserOrg.equalsIgnoreCase(HHSR5Constants.USER_AGENCY)
					|| lsUserOrg.equalsIgnoreCase(HHSR5Constants.USER_CITY))
			{
				String lsXPathTop = "//page[(@name=\"contractList\")]//status[(@name=\""
						+ loContractList.getContractStatusId() + "\")]//action";
				List<Element> loNodeList = XMLUtil.getElementList(lsXPathTop, loDoc);
				if (!(lsUserRole.equalsIgnoreCase(HHSConstants.PROGRAM_STAFF_ROLE)
						|| lsUserRole.equalsIgnoreCase(HHSConstants.PROGRAM_ADMIN_STAFF_ROLE) 
						|| lsUserRole.equalsIgnoreCase(HHSConstants.PROGRAM_MANAGER_ROLE)
						))
				{
					if (loContractList.getIsNewFYConfigPending() == true
								// Start: Updated in R7 defect 8644 part 2(added the check for Budget pending approval)
									&& Integer.parseInt(loContractList.getIsBudgetApproved())>0)
					{
						/*[Start] R9.6.4 QC9701 */
						if( /*[Start] R9.7.5 QC9719*/
								lbActionEnableFlag &&  
								/*[End] R9.7.5 QC9719*/
								(//<!-- [Start] R9.7.6 QC9730 -->
										( lbActionExceptionFlag || 
								 //<!-- [End] R9.7.6 QC9730 -->
								   (loMap != null &&  loMap.get(HHSR5Constants.ACTION_DROPDOWN_NEW_FISCAL_YEAR_INX) != null 
								      && loMap.get(HHSR5Constants.ACTION_DROPDOWN_NEW_FISCAL_YEAR_INX).equalsIgnoreCase("1") ) ) 
								)
							) {
							// End : Updated in R7 defect 8644 part 2
							lsControl.append("<option title= " + "'New FY Configuration Confirmation'"
									+ "value='New FY Configuration " + "Confirmation'>Configure New Fiscal Year</option>");
						}
						/*[End] R9.6.4 QC9701 */
					}
				}
				for (Element loNode : loNodeList)
				{
					if (lsUserRole.equalsIgnoreCase(HHSConstants.PROGRAM_STAFF_ROLE)
							|| lsUserRole.equalsIgnoreCase(HHSConstants.PROGRAM_ADMIN_STAFF_ROLE)
							|| lsUserRole.equalsIgnoreCase(HHSConstants.PROGRAM_MANAGER_ROLE))
					{
						if (loNode.getValue().equalsIgnoreCase(HHSConstants.VIEW_CONTRACT_COF)
								|| loNode.getValue().equalsIgnoreCase(HHSConstants.VIEW_CONTRACT_CONFIGURATION)
								|| loNode.getValue().equalsIgnoreCase(HHSConstants.VIEW_AMENDMENTS)
								|| loNode.getValue().equalsIgnoreCase(HHSR5Constants.VIEW_BUDGETS)
								|| loNode.getValue().equalsIgnoreCase(HHSR5Constants.VIEW_INVOICES)
								|| loNode.getValue().equalsIgnoreCase(HHSR5Constants.VIEW_PAYMENTS)
								|| loNode.getValue().equalsIgnoreCase(HHSR5Constants.FLAG_CONTRACT_ACTION)
								|| loNode.getValue().equalsIgnoreCase(HHSR5Constants.UNFLAG_CONTRACT_ACTION)
								)
						{
							lsControl = getControle(lsControl, loContractList, loNode, lsConAgencyName);
						}
					}
					else
					{
						lsControl = getControle(lsControl, loContractList, loNode, lsConAgencyName);
 					}
				}
			}
			else
			{
				String lsXPathTop = "//page[(@name=\"contractListProvider\")]//status[(@name=\""
						+ loContractList.getContractStatusId() + "\")]//action";
				List<Element> loNodeList = XMLUtil.getElementList(lsXPathTop, loDoc);
				for (Element loNode : loNodeList)
				{
					// Release 5 Contract Restriction
					if (loNode.getValue().equalsIgnoreCase(HHSR5Constants.VIEW_BUDGETS))
					{
						setOption(loContractList.getBudgetCount(), lsControl, loNode);
					}
					// Release 5 Contract Restriction
					else if (loNode.getValue().equalsIgnoreCase(HHSR5Constants.VIEW_INVOICES))
					{
						setOption(loContractList.getInvoiceCount(), lsControl, loNode);
					}
					// Release 5 Contract Restriction
					else if (loNode.getValue().equalsIgnoreCase(HHSR5Constants.VIEW_PAYMENTS))
					{
						setOption(loContractList.getPaymentCount(), lsControl, loNode);
					}
					// Start Release 5 user notification
					else if (loNode.getValue().equalsIgnoreCase(HHSR5Constants.USER_ACCESS))

					{
						if (loContractList.isUserAccess())
						{
							setOption(loContractList.getProviderAdmin(), lsControl, loNode);
						}
					}
					// End Release 5 user notification
					else
					{   
						/*[Start] R9.6.4 QC9701 */
						if(ActionStatusUtil.getActionInx(loNode.getValue()) < 0 ) {
							setNodeValue(lsControl, loNode);
						} else {
							if( /*[Start] R9.7.5 QC9719*/
									lbActionEnableFlag &&  
									/*[End] R9.7.5 QC9719*/
									//<!-- [Start] R9.7.6 QC9730 -->
									( lbActionExceptionFlag || 
								  //<!-- [End] R9.7.6 QC9730 -->
									ActionStatusUtil.getActionEnabledStatus(lsConAgencyName, loNode.getValue())
									)
							  ) {
								setNodeValue(lsControl, loNode);
							}
						}
						/*[End] R9.6.4 QC9701 */
					}
				}
			}

			lsControl.append("</select>");
		}
		
		if(loContractList != null 
		        && loContractList.getUserSubRole().equalsIgnoreCase(ApplicationConstants.ROLE_OBSERVER)){
		    CommonUtil.keepReadOnlyActions(lsControl);
		}

		return lsControl.toString();
	}

	/**
	 * This Method gets internal call from getControlForColumn which is used to
	 * create actions drop down for finance contract list grid table.
	 * 
	 * @param lsControl - StringBuilder object
	 * @param loNode - Element object
	 * @param lsCount - String
	 * @throws ApplicationException
	 */
	private void setOption(String lsCount, StringBuilder lsControl, Element loNode)
	{
		if (StringUtils.isNotBlank(lsCount) && Integer.parseInt(lsCount) >= 1)
		{
			setNodeValue(lsControl, loNode);
		}
	}

	/**
	 * This Method is used to build controle from node.
	 * 
	 * @param lsControl - StringBuilder object
	 * @param loNode - Element object
	 * @throws ApplicationException
	 */
	private void setNodeValue(StringBuilder lsControl, Element loNode)
	{
		lsControl.append("<option title=" + "'" + loNode.getValue() + "'" + " value=\"" + loNode.getValue() + "\">"
				+ loNode.getValue() + "</option>");
	}

	/**
	 * This Method is used to check all the checkboxes if present.
	 * 
	 * @param aoCol column name
	 * @return lsControl - String
	 */
	public String getControlForHeading(Column aoCol)
	{

		String lsControl = HHSConstants.RESUME;
		return lsControl;
	}

	/**
	 * This Method is used to build controle from node.<br>
	 * Updated Method in R4
	 * 
	 * @param asControl - StringBuilder object
	 * @param aoContractList - ContractList object
	 * @param aoNode - Element object
	 * @return asControl - StringBuilder Object
	 * @throws ApplicationException - ApplicationException Object
	 * 
	 */
	private StringBuilder getControle(StringBuilder asControl, ContractList aoContractList, Element aoNode , String aoAgencyName)
			throws ApplicationException
	{
		//<!-- [Start] R9.7.3 QC9719 -->
		boolean  lbActionEnableFlag = true ;
		if( aoContractList.getActionDisable()  > 0 )  { lbActionEnableFlag  = false;}
		//<!-- [End] R9.7.3 QC9719 -->
		//<!-- [Start] R9.7.6 QC9730 -->
		boolean  lbActionExceptionFlag = true ;
		if(  aoContractList.getActionException()  > 0 )  {	lbActionExceptionFlag  = true;
		}else {   lbActionExceptionFlag  = false;  }
		//<!-- [End] R9.7.6 QC9730 -->

		if (aoNode.getValue().equalsIgnoreCase(HHSConstants.AMEND_CONTRACT) )
		{
			if( aoContractList.getNeedAmendRemoved() 
					
				/*[Start] R9.6.4 QC9701 */
				||  (   !(  /*//<!-- [Start] R9.7.6 QC9730 -->*/
						   lbActionExceptionFlag || 
								  /*<!-- [End] R9.7.6 QC9730 -->*/
						   ActionStatusUtil.getActionEnabledStatus(aoAgencyName, aoNode.getValue()   ) 
						 )
				/*[End] R9.6.4 QC9701 */
				/*[Start] R9.7.5 QC9719*/
				||   !lbActionEnableFlag ) 
				/*[End] R9.7.5 QC9719*/
				
				){
					return asControl;
			}
				setNodeValue(asControl, aoNode);
		}
		else if (aoNode.getValue().equalsIgnoreCase(HHSConstants.CAFE_CLOSE_CONTRACT))
		{
			/*[Start] R9.6.4 QC9701 */
			if(  !(  /*//<!-- [Start] R9.7.6 QC9730 -->*/
					 lbActionExceptionFlag || 
					 /*<!-- [End] R9.7.6 QC9730 -->*/
					 ActionStatusUtil.getActionEnabledStatus(aoAgencyName, aoNode.getValue() ) 
				  )
					/*[Start] R9.7.5 QC9719*/
					||   !lbActionEnableFlag  
					/*[End] R9.7.5 QC9719*/
			   ) {
				return asControl;
			}
			/*[End] R9.6.4 QC9701 */
			Date loCurrentDate = new Date();

			if (HHSUtil.getZeroTimeDate(loCurrentDate).compareTo(
					HHSUtil.getZeroTimeDate(aoContractList.getContractEndDate())) >= 0)
			{
				asControl.append("<option title=" + "'Close Contract'"
						+ " value=\"Close Contract\">Close Contract</option>");
			}
		}
		else if (aoNode.getValue().equalsIgnoreCase(HHSConstants.CAFE_CANCEL_CONTRACT))
		{
			/*[Start] R9.6.4 QC9701 */
			if( !(  /*//<!-- [Start] R9.7.6 QC9730 -->*/
					lbActionExceptionFlag || 
					/*<!-- [End] R9.7.6 QC9730 -->*/
					ActionStatusUtil.getActionEnabledStatus(aoAgencyName, aoNode.getValue() ) 
				  ) 
					/*[Start] R9.7.5 QC9719*/
					|| !lbActionEnableFlag  
					/*[End] R9.7.5 QC9719*/
					) {
				return asControl;
			}
			/*[End] R9.6.4 QC9701 */
			
			if (aoContractList.getOrgType().equalsIgnoreCase(HHSConstants.USER_CITY)
					&& aoContractList.getContractSourceId().equals(HHSConstants.TWO)
					&& (aoContractList.getContractTypeId().equals(HHSConstants.ONE) || aoContractList
							.getContractTypeId().equals(HHSConstants.THREE)))
			{
				asControl.append("<option title=" + "'Cancel Contract'"
						+ "value=\"Cancel Contract\">Cancel Contract</option>");
			}
			else if (aoContractList.getOrgType().equalsIgnoreCase(HHSConstants.USER_AGENCY)
					&& aoContractList.getContractSourceId().equals(HHSConstants.TWO)
					&& aoContractList.getContractTypeId().equals(HHSConstants.THREE))
			{
				asControl.append("<option title=" + "'Cancel Contract'"
						+ " value=\"Cancel Contract\">Cancel Contract</option>");
			}
		}
		// Start || Added For Enhancement 6000 for Release 3.8.0
		else if (aoNode.getValue().equalsIgnoreCase(HHSConstants.CAFE_DELETE_CONTRACT))
		{
			/*[Start] R9.6.4 QC9701 */
			if( !(/*//<!-- [Start] R9.7.6 QC9730 -->*/
					lbActionExceptionFlag || 
					/*<!-- [End] R9.7.6 QC9730 -->*/
					ActionStatusUtil.getActionEnabledStatus(aoAgencyName, aoNode.getValue() )
				)
				/*[Start] R9.7.5 QC9719*/
				||   !lbActionEnableFlag  
				/*[End] R9.7.5 QC9719*/
				) {
				return asControl;
			}
			/*[End] R9.6.4 QC9701 */
			
			if (aoContractList.getOrgType().equalsIgnoreCase(HHSConstants.USER_CITY)
					&& aoContractList.getContractSourceId().equals(HHSConstants.TWO)
					&& (aoContractList.getContractTypeId().equals(HHSConstants.ONE) 
							|| aoContractList.getContractTypeId().equals(HHSConstants.THREE))
					&& !aoContractList.getActions().equalsIgnoreCase(HHSConstants.STAFF_ROLE))
			{
				asControl.append("<option title=" + "'Delete Contract'"
						+ "value=\"Delete Contract\">Delete Contract</option>");
			}
		}
		// End || Added For Enhancement 6000 for Release 3.8.0
		// Start || Added For Enhancement 6482 for Release 3.8.0
		else if (aoNode.getValue().equalsIgnoreCase(HHSConstants.UPDATE_CONTRACT_INFO))
		{
			/*[Start] R9.6.4 QC9701 */
			if( !(  /*//<!-- [Start] R9.7.6 QC9730 -->*/
					lbActionExceptionFlag || 
					/*<!-- [End] R9.7.6 QC9730 -->*/
					ActionStatusUtil.getActionEnabledStatus(aoAgencyName, aoNode.getValue() ) 
				)
					/*[Start] R9.7.5 QC9719*/
					||   !lbActionEnableFlag  
					/*[End] R9.7.5 QC9719*/
					) {
				return asControl;
			}
			/*[End] R9.6.4 QC9701 */

			if (aoContractList.getOrgType().equalsIgnoreCase(HHSConstants.USER_CITY)
					&& !aoContractList.getActions().equalsIgnoreCase(HHSConstants.STAFF_ROLE))
			{
				asControl.append("<option title=" + "'Update Contract Information'"
						+ "value=\"Update Contract Information\">Update Contract Information</option>");
			}
		}
		// End || Added For Enhancement 6482 for Release 3.8.0

		else if (aoNode.getValue().equalsIgnoreCase(HHSConstants.VIEW_CONTRACT_COF))
		{
			if (aoContractList.getIsViewCOF())
			{
				asControl.append("<option title=" + "'View Contract CoF'"
						+ " value=\"View Contract CoF\">View Contract CoF</option>");
			}
		}
		else if (aoNode.getValue().equalsIgnoreCase(HHSConstants.RENEW_CONTRACT))
		{
			if (!aoContractList.isAlreadyRenewed())
			{
				setNodeValue(asControl, aoNode);
			}
		}
		// Release 3.8.0 Enhancement 6481
		else if (aoNode.getValue().equalsIgnoreCase(HHSConstants.UPDATE_CONTRACT_CONFIGURATION))
		{
			/*[Start] R9.6.4 QC9701 */
			if( !( /*//<!-- [Start] R9.7.6 QC9730 -->*/
					lbActionExceptionFlag || 
					/*<!-- [End] R9.7.6 QC9730 -->*/
					ActionStatusUtil.getActionEnabledStatus(aoAgencyName, aoNode.getValue() )  
				)
					/*[Start] R9.7.5 QC9719*/
					||   !lbActionEnableFlag  
					/*[End] R9.7.5 QC9719*/
				) {
				return asControl;
			}
			/*[End] R9.6.4 QC9701 */
			
			if (aoContractList.isContractUpdate())
			{
				setNodeValue(asControl, aoNode);
			}
		}// added in release 5
		else if (aoNode.getValue().equalsIgnoreCase(HHSR5Constants.VIEW_BUDGETS))
		{
			setOption(aoContractList.getBudgetCount(), asControl, aoNode);
		}
		else if (aoNode.getValue().equalsIgnoreCase(HHSR5Constants.VIEW_INVOICES))
		{
			setOption(aoContractList.getInvoiceCount(), asControl, aoNode);
		}
		else if (aoNode.getValue().equalsIgnoreCase(HHSR5Constants.VIEW_PAYMENTS))
		{
			setOption(aoContractList.getPaymentCount(), asControl, aoNode);
		}
		else if (aoNode.getValue().equalsIgnoreCase(HHSR5Constants.USER_ACCESS))
		{
			if (aoContractList.getOrgType().equalsIgnoreCase(HHSConstants.USER_CITY))
			{
				setNodeValue(asControl, aoNode);
			}
		}// added in release 5
		// Added in R7
		else if (aoNode.getValue().equalsIgnoreCase(HHSR5Constants.FLAG_CONTRACT_ACTION))
		{
			/*[Start] R9.6.4 QC9701 */
			if( !(/*//<!-- [Start] R9.7.6 QC9730 -->*/
					lbActionExceptionFlag || 
					/*<!-- [End] R9.7.6 QC9730 -->*/
					ActionStatusUtil.getActionEnabledStatus(aoAgencyName, aoNode.getValue() )
				) 
					/*[Start] R9.7.5 QC9719*/
					||   !lbActionEnableFlag  
					/*[End] R9.7.5 QC9719*/
					) {
				return asControl;
		    }
			/*[End] R9.6.4 QC9701 */

			
			if (aoContractList.getOrgType().equalsIgnoreCase(HHSR5Constants.USER_CITY)
					|| (null != aoContractList.getActions()
							&& aoContractList.getOrgType().equalsIgnoreCase(HHSR5Constants.USER_AGENCY) && (aoContractList
							.getActions().equalsIgnoreCase(HHSR5Constants.CFO_ROLE)
							|| aoContractList.getActions().equalsIgnoreCase(HHSR5Constants.PROGRAM_MANAGER_ROLE) || aoContractList
							.getActions().equalsIgnoreCase(HHSR5Constants.FINANCE_MANAGER_ROLE))))
			{
				if (null != aoContractList.getContractFlagged()
						&& aoContractList.getContractFlagged().equalsIgnoreCase(HHSR5Constants.STRING_ZERO))
				{
					setNodeValue(asControl, aoNode);
				}
			}
		}
		else if (aoNode.getValue().equalsIgnoreCase(HHSR5Constants.UNFLAG_CONTRACT_ACTION))
		{
			/*[Start] R9.6.4 QC9701 */
			if( !(/*//<!-- [Start] R9.7.6 QC9730 -->*/
					lbActionExceptionFlag || 
					/*<!-- [End] R9.7.6 QC9730 -->*/
					ActionStatusUtil.getActionEnabledStatus(aoAgencyName, aoNode.getValue() )
					)
					/*[Start] R9.7.5 QC9719*/
					||   !lbActionEnableFlag  
					/*[End] R9.7.5 QC9719*/
					) {
					return asControl;
			}
			/*[End] R9.6.4 QC9701 */

			if (aoContractList.getOrgType().equalsIgnoreCase(HHSR5Constants.USER_CITY)
					|| (null != aoContractList.getActions()
							&& aoContractList.getOrgType().equalsIgnoreCase(HHSR5Constants.USER_AGENCY) && (aoContractList
							.getActions().equalsIgnoreCase(HHSR5Constants.CFO_ROLE)
							|| aoContractList.getActions().equalsIgnoreCase(HHSR5Constants.PROGRAM_MANAGER_ROLE) || aoContractList
							.getActions().equalsIgnoreCase(HHSR5Constants.FINANCE_MANAGER_ROLE))))
			{
				if (null != aoContractList.getContractFlagged()
						&& aoContractList.getContractFlagged().equalsIgnoreCase(HHSR5Constants.STRING_ONE))
				{
					setNodeValue(asControl, aoNode);
				}
			}
		}
		// r7 end
		else if ( aoNode.getValue().equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_SUSPEND_CONTRACT_NAME) 
				|| aoNode.getValue().equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_UNSUSPEND_CONTRACT_NAME) )
		{
			/*[Start] R9.6.4 QC9701 */
			if( ( /*//<!-- [Start] R9.7.6 QC9730 -->*/
					lbActionExceptionFlag || 
					/*<!-- [End] R9.7.6 QC9730 -->*/
					ActionStatusUtil.getActionEnabledStatus(aoAgencyName, aoNode.getValue() ) 
					) 
					/*[Start] R9.7.5 QC9719*/
					&&   lbActionEnableFlag  
					/*[End] R9.7.5 QC9719*/
					) {
				setNodeValue(asControl, aoNode);
			}
			/*[End] R9.6.4 QC9701 */

		}// added in release 5
		else
		{ 
			setNodeValue(asControl, aoNode);
		}

		return asControl;
	}

}
