package com.nyc.hhs.frameworks.grid;

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
import com.nyc.hhs.model.ActionStatusBean;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.util.ActionStatusUtil;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * This class is used to create custom select element for contract list grid
 * view. Values in Action column on contract list page for Acclerator/Agency are
 * populated using it as a decorator class in grid.
 * 
 * 
 */
public class AmendmentActionFinancialExtension implements DecoratorInterface
{

	/**
	 * This method is used to create actions drop down for finance contract list
	 * grid table.
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
	 * 
	 * @param aoEachObject Bean Name
	 * @param aoCol Column name
	 * @param aoSeqNo Sequence Number
	 * @return control
	 * @throws ApplicationException If an Application Exception occurs
	 */

	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{   
		ContractList loContractList = (ContractList) aoEachObject;
		// Observation Changes Release 3.4.0.
		loContractList.setContractTitle(StringEscapeUtils.escapeHtml(loContractList.getContractTitle()));
		String lsUserRole = loContractList.getActions();
		String lsOrgType = loContractList.getOrgType();
		
        /*[Start] R9.6.4 QC9701 */
		String lsConAgencyName = loContractList.getContractAgencyName();
		String lsConAgencyId = loContractList.getAgencyId();
		Map<Integer,String> loMap = ActionStatusUtil.getMoActionMapByAgency(lsConAgencyName);
		ActionStatusBean loActionStat = ActionStatusUtil.getMoActionByAgency(lsConAgencyName);

		
		StringBuilder lsControl = new StringBuilder();
		if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.BMC_CONTRACT_VALUE))
		{
			lsControl.append("<label class='tableContractValue'  " + "style=' text-align:right; float:right'>"
					+ loContractList.getContractValue() + "</label>");
		}
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.CONTRACT_STATUS))
		{
			if (loContractList.getContractStatus().equalsIgnoreCase(HHSConstants.ETL_REG))
			{
				lsControl.append(HHSConstants.SENT_FOR_REG);
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
		else
		{
			if (lsOrgType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG)
					|| lsOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG))
			{
				Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
						HHSConstants.AMENDMENT_MAPPING_FILE_NAME);
				String lsXPathTop = "//page[(@name=\"contractListAgencyOrCity\")]//status[(@name=\""
						+ loContractList.getContractStatusId() + "\")]//action";
				List<Element> loNodeList = XMLUtil.getElementList(lsXPathTop, loDoc);
				if (loContractList.isSuspended())
				{
					lsControl.append("<select name=action" + aoSeqNo + " class='contractAmend' disabled id='action"
							+ aoSeqNo + "' style='width: 150px' " + "contractTitle=\""
							+ loContractList.getContractTitle() + "\" ct=\"" + loContractList.getCtId()
							+ "\" baseContractTitle=\"" + loContractList.getBaseContractTitle() + "\" provider=\""
							+ loContractList.getProvider() + "\" contractTypeId=\""
							+ loContractList.getContractTypeId() + "\" status=\"" + loContractList.getContractStatus()
							+ "\" contractAmount=\"" + loContractList.getContractValue() + "\" contractStartDate=\""
							+ loContractList.getContractStartDate() + "\" contractEndDate=\""
							+ loContractList.getContractEndDate() + "\" contractId=\"" + loContractList.getContractId()
							+ "\" fyConfigFiscalYear=\"" + loContractList.getFyConfigFiscalYear()
							+ "\" functionName=\"amendContract" + "\"><option title=" + "'I need to...'"
							+ "value=I need to... >I need to...</option>");
				}
				else
				{
					lsControl.append("<select name=action" + aoSeqNo + " class='contractAmend' id='action" + aoSeqNo
							+ "' style='width: 150px' " + "contractTitle=\"" + loContractList.getContractTitle()
							+ "\" ct=\"" + loContractList.getCtId() + "\" baseContractTitle=\""
							+ loContractList.getBaseContractTitle() + "\" provider=\"" + loContractList.getProvider()
							+ "\" contractTypeId=\"" + loContractList.getContractTypeId() + "\" status=\""
							+ loContractList.getContractStatus() + "\" contractAmount=\""
							+ loContractList.getContractValue() + "\" contractStartDate=\""
							+ loContractList.getContractStartDate() + "\" contractEndDate=\""
							+ loContractList.getContractEndDate() + "\" contractId=\""
							+ loContractList.getParentContractId() + "\" amendContractId=\""
							+ loContractList.getContractId() + "\" fyConfigFiscalYear=\""
							+ loContractList.getFyConfigFiscalYear() + "\" functionName=\"amendContract"
							+ "\"><option title=" + "'I need to...'" + "value=I need to... >I need to...</option>");
				}

				for (Element loNode : loNodeList)
				{   // [Start] QC9517 R 8.7.0 Cancel option should not be available for a Negative Amendment after Budget has been Approved
					//System.out.println("=====Element loNode ::  "+ loNode);
					//System.out.println("=====Element loNode.getName()  ::  "+ loNode.getName());
					//System.out.println("=====Element loNode.getValue()  ::  "+ loNode.getValue());
					//System.out.println("=====lsControl  ::  "+ lsControl.toString());

					if("Cancel Amendment".equalsIgnoreCase(loNode.getValue())) 
					{	
						// [Start] QC9517 R 8.7.0 Cancel option should not be available for a Negative Amendment after Budget has been Approved
						if(loContractList.getNegativeAmend() && loContractList.getAmendBudgetApprovedCount() > 0
								// [Start] QC9304 R 8.8.0 Do not allow Cancel Amendment after an Out-Year Amendment has been Marked as Registered	
								|| (null != loContractList.getPartialMergeCount() && loContractList.getPartialMergeCount() > 0) )
								// [End] QC9304 R 8.8.0 Do not allow Cancel Amendment after an Out-Year Amendment has been Marked as Registered
						{	
							System.out.println("=====Do not add Cancel Amendment Action Option =============");
						}
						else
						{
							// [End] QC9517 R 8.7.0 Cancel option should not be available for a Negative Amendment after Budget has been Approved
							lsControl = getControle(lsControl, loContractList, loNode, lsUserRole,lsOrgType, lsConAgencyName);
						}
					}
					else
					{
						lsControl = getControle(lsControl, loContractList, loNode, lsUserRole,lsOrgType, lsConAgencyName);
					}
				}
			}
			else if (lsOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
			{
				Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
						HHSConstants.AMENDMENT_MAPPING_FILE_NAME);
				String lsXPathTop = "//page[(@name=\"contractListProvider\")]//status[(@name=\""
						+ loContractList.getContractStatusId() + "\")]//action";
				List<Element> loNodeList = XMLUtil.getElementList(lsXPathTop, loDoc);
				lsControl.append("<select name=action" + aoSeqNo + " class='contractAmend' id='action" + aoSeqNo
						+ "' style='width: 150px' " + "contractTitle=\"" + loContractList.getContractTitle()
						+ "\" ct=\"" + loContractList.getCtId() + "\" baseContractTitle=\""
						+ loContractList.getBaseContractTitle() + "\" provider=\"" + loContractList.getProvider()
						+ "\" contractTypeId=\"" + loContractList.getContractTypeId() + "\" status=\""
						+ loContractList.getContractStatus() + "\" contractAmount=\""
						+ loContractList.getContractValue() + "\" contractStartDate=\""
						+ loContractList.getContractStartDate() + "\" contractEndDate=\""
						+ loContractList.getContractEndDate() + "\" contractId=\""
						+ loContractList.getParentContractId() + "\" amendContractId=\""
						+ loContractList.getContractId() + "\" fyConfigFiscalYear=\""
						+ loContractList.getFyConfigFiscalYear() + "\" functionName=\"amendContract"
						+ "\"><option title=" + "'I need to...'" + "value=I need to... >I need to...</option>");
				for (Element loNode : loNodeList)
				{
					if (loNode.getValue().equalsIgnoreCase("view budget"))
					{
						if (StringUtils.isNotBlank(loContractList.getBudgetCount())
								&& Integer.parseInt(loContractList.getBudgetCount()) >= 1)
						{
							setNodeValue(lsControl, loNode);
						}
					}
					else
					{
						setNodeValue(lsControl, loNode);
					}

				}
			}
			/* End : Added in R5 */
			lsControl.append("</select>");
		}

        if(loContractList != null 
                && loContractList.getUserSubRole().equalsIgnoreCase(ApplicationConstants.ROLE_OBSERVER)){
               CommonUtil.keepReadOnlyActions(lsControl);
        }

		      
        return lsControl.toString();
	}

	/**
	 * This Method is used to check all the checkboxes if present.
	 * 
	 * @param aoCol column name
	 * @return String
	 */
	public String getControlForHeading(Column aoCol)
	{

		String lsControl = HHSConstants.RESUME;
		return lsControl;
	}

	/**
	 * This Method is used to build controle from node.
	 * 
	 * @param asControl - StringBuilder object
	 * @param aoContractList - ContractList object
	 * @param aoNode - Element object
	 * @param lsUserRole User Role
	 * @return StringBuilder
	 * @throws ApplicationException
	 */
	private StringBuilder getControle(StringBuilder asControl, ContractList aoContractList, Element aoNode,
			String lsUserRole,String asOrgType , String aoConAgencyid) throws ApplicationException
	{

		if (aoNode.getValue().equalsIgnoreCase(HHSConstants.VIEW_COF))
		{
			if (aoContractList.getIsViewCOF())
			{
				setNodeValue(asControl, aoNode);
			}
		}
		else if (!lsUserRole.equalsIgnoreCase(HHSConstants.PROGRAM_STAFF_ROLE)
				&& !lsUserRole.equalsIgnoreCase(HHSConstants.PROGRAM_ADMIN_STAFF_ROLE)
				&& !lsUserRole.equalsIgnoreCase(HHSConstants.PROGRAM_MANAGER_ROLE))
		{

			// Added in release 5
			if (aoNode.getValue().equalsIgnoreCase("view budget"))
			{
				//Start: R7 Defect 8644 part 3.
				/**
				 * condition defines when the option "Mark Amendment As Registered" will be visible in the City user's contract List screen.
				 */
				if (asOrgType.equalsIgnoreCase(HHSR5Constants.USER_CITY) 
						&& Integer.parseInt(aoContractList.getMarkAsFmsRegistered())>= HHSConstants.INT_ONE
						&& aoContractList.getOnGoingAmendCount() <= HHSConstants.INT_ONE)
				{
					/*[Start] R9.6.4 QC9701 */
					if( ActionStatusUtil.getActionEnabledStatus(aoConAgencyid, HHSR5Constants.ACTION_DROPDOWN_MARK_AS_REGISTERED_NAME )  ) {
					/*[End] R9.6.4 QC9701 */
					  asControl.append("<option title= " + "'Confirm Mark Amendment As Registered'"
							+ "value='Mark Amendment As Registered " + "Confirmation'>Mark as Registered</option>");
					}
				}

				// End: R7 Defect 8644 part 3.
				if (StringUtils.isNotBlank(aoContractList.getBudgetCount())
						&& Integer.parseInt(aoContractList.getBudgetCount()) >= 1)
				{
					setNodeValue(asControl, aoNode);
				}
			}
			else
			{
				/*[Start] R9.6.4 QC9701 */
				if(ActionStatusUtil.getActionInx(aoNode.getValue()) < 0 ) {
					setNodeValue(asControl, aoNode);
				} else {
					if( ActionStatusUtil.getActionEnabledStatus(aoConAgencyid, aoNode.getValue() ) ) {
						setNodeValue(asControl, aoNode);
					}
				}
				/*[End] R9.6.4 QC9701 */
			}
			// Added in release 5
		}
		
		return asControl;
	}

	/**
	 * This Method is used to build controle from node.
	 * 
	 * @param asControl - StringBuilder object
	 * @param aoNode - Element object
	 * @throws ApplicationException
	 */
	private void setNodeValue(StringBuilder asControl, Element aoNode)
	{
		String lsValue = aoNode.getValue();
		if (lsValue.equalsIgnoreCase("view budget") || lsValue.equalsIgnoreCase("view contract"))
		{
			lsValue = lsValue + " Amend";
		}
		asControl.append("<option title=" + "'" + aoNode.getValue() + "'" + " value=\"" + lsValue + "\">"
				+ aoNode.getValue() + "</option>");
	}
}
