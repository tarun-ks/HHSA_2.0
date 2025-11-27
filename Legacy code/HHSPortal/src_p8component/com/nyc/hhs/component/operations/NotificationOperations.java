package com.nyc.hhs.component.operations;

import java.util.HashMap;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.ComponentNotificationLinkBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This class supports the Alert and notification actions that are performed by
 * the workflows *
 * 
 */
public class NotificationOperations
{
	private static final LogInfo LOG_OBJECT = new LogInfo(NotificationOperations.class);

	/**
	 * This method will create a Stringbuffer representation for the link that
	 * will be placed in the notification/alert message. Based on the given
	 * alert and notification name, the correct link will be generated. The
	 * information that must be added in the link will be retrieved from the
	 * aoProperties parameter.
	 * 
	 * @param asNotificationName
	 * @param asAlertName
	 * @param aoProperties
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public void getNotificationLink(String asNotificationName, String asAlertName, String[] aoProperties,
			NotificationDataBean aoNotificationBean, HashMap aoNotificationMap, String[] aoAudienceList)
			throws ApplicationException
	{
		StringBuffer loNotificationLINK = new StringBuffer();
		ComponentNotificationLinkBean loComponentNotificationLinkBean = new ComponentNotificationLinkBean();
		setComponentLinkBean(aoProperties, loComponentNotificationLinkBean);
		String lsProcurementID = loComponentNotificationLinkBean.getProcurementId();
		String lsContractID = loComponentNotificationLinkBean.getContractId();
		String lsBudgetID = loComponentNotificationLinkBean.getBudgetId();
		String lsFiscalYearID = loComponentNotificationLinkBean.getFiscalYearId();
		String lsInvoiceID = loComponentNotificationLinkBean.getInvoiceID();
		String lsLaunchByOrgType = loComponentNotificationLinkBean.getLaunchByOrgType();
		String lsEntityId = loComponentNotificationLinkBean.getEntityId();
		String lsEntityType = loComponentNotificationLinkBean.getEntityType();
		HashMap loBudMap = new HashMap();
		HashMap loLinkMap = new HashMap();
		try
		{
			loNotificationLINK = getNotificationLink();

			if (lsEntityId != null)
			{
				aoNotificationMap.put(ApplicationConstants.ENTITY_ID, lsEntityId);
				aoNotificationMap.put(ApplicationConstants.ENTITY_TYPE, lsEntityType);
			}

			if ((asNotificationName.equals(HHSP8Constants.NT203)) && (asAlertName.equals(HHSP8Constants.AL203)))
			{
				loNotificationLINK.append(HHSP8Constants.SELECTION_DETAILS_URL);
				loNotificationLINK.append(lsProcurementID);
				loLinkMap.put(HHSP8Constants.LINK1, loNotificationLINK.toString());
				loNotificationLINK = getNotificationLink();
				loNotificationLINK.append(HHSP8Constants.SELECTION_DETAILS_BUDGET_LIST_URL);
				loLinkMap.put(HHSP8Constants.LINK2, loNotificationLINK.toString());
			}
			else
			{
				// START || Changes for enhancement 5978 for Release 3.11.0
				if ((asNotificationName.equals(HHSP8Constants.NT207)) && (asAlertName.equals(HHSP8Constants.AL212)))
				{
					loNotificationLINK.append(HHSP8Constants.PROPOSAL_SUMMARY_URL);
					loNotificationLINK.append(lsProcurementID);
					aoNotificationMap.put(ApplicationConstants.ENTITY_ID, lsProcurementID);
					aoNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.PROCUREMENT);

				}
				else if ((asNotificationName.equals(HHSP8Constants.NT213))
						&& (asAlertName.equals(HHSP8Constants.AL218)))
				{
					loNotificationLINK.append(HHSP8Constants.PROPOSAL_SUMMARY_URL);
					loNotificationLINK.append(lsProcurementID);
					aoNotificationMap.put(ApplicationConstants.ENTITY_ID, lsProcurementID);
					aoNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.PROCUREMENT);
				}
				// END || Changes for enhancement 5978 for Release 3.11.0
				else if ((asNotificationName.equals(HHSP8Constants.NT221))
						&& (asAlertName.equals(HHSP8Constants.AL217)))
				{
					loNotificationLINK.append(HHSP8Constants.PROPOSAL_SUMMARY_URL);
					loNotificationLINK.append(lsProcurementID);
				}
				else if ((asNotificationName.equals(HHSP8Constants.NT220))
						|| (asNotificationName.equals(HHSP8Constants.NT210)))
				{
					loNotificationLINK = new StringBuffer();
					loNotificationLINK.append(PropertyLoader.getProperty(HHSP8Constants.PROPERTY_FILE,
							HHSP8Constants.PROP_CITY_URL));
					loNotificationLINK.append(HHSP8Constants.AGENCY_TASK_INBOX_URL);
				}
				else if ((asNotificationName.equals(HHSP8Constants.NT223))
						&& (asAlertName.equals(HHSP8Constants.AL222)))
				{
					loNotificationLINK.append(HHSP8Constants.PROPOSAL_SUMMARY_URL);
					loNotificationLINK.append(lsProcurementID);
				}
				else if ((asNotificationName.equals(HHSP8Constants.NT222))
						&& (asAlertName.equals(HHSP8Constants.AL221)))
				{
					loNotificationLINK.append(HHSP8Constants.SELECTION_DETAILS_URL);
					loNotificationLINK.append(lsProcurementID);
				}
				// R5 Change starts
				else if (asNotificationName.equals(HHSP8Constants.NT231) && asAlertName.equals(HHSP8Constants.AL230))
				{
					loNotificationLINK.append(HHSP8Constants.SELECTION_DETAILS_URL);
					loNotificationLINK.append(lsProcurementID);
				}
				// R5 change ends
				else if ((asNotificationName.equals(HHSP8Constants.NT301))
						&& (asAlertName.equals(HHSP8Constants.AL301)))
				{
					loNotificationLINK = getContractInfo(loNotificationLINK,
							HHSP8Constants.CONTRACT_BUDGET_REVISIONS_URL, lsContractID, lsBudgetID, lsFiscalYearID);
				}
				else if ((asNotificationName.equals(HHSP8Constants.NT303))
						&& (asAlertName.equals(HHSP8Constants.AL303)))
				{
					loNotificationLINK.append(HHSP8Constants.INVOICE_REVISIONS_URL);
					loNotificationLINK.append(lsInvoiceID);
				}
				else if ((asAlertName.equals(HHSP8Constants.AL305))
						|| (asNotificationName.equals(HHSP8Constants.NT305A) || asNotificationName
								.equals(HHSP8Constants.NT305B)))
				{
					if (lsLaunchByOrgType.equalsIgnoreCase(HHSP8Constants.PROVIDER_ORG))
					{
						loNotificationLINK.append(HHSP8Constants.ADVANCE_REJECTED_URL);
					}
					else
					{
						loNotificationLINK = new StringBuffer();
						loNotificationLINK.append(PropertyLoader.getProperty(HHSP8Constants.PROPERTY_FILE,
								HHSP8Constants.PROP_CITY_URL));
						loNotificationLINK.append(HHSP8Constants.ADVANCE_REJECTED_URL);
					}
					setUserIDForUserInitiatedAlert(aoNotificationBean, aoAudienceList);
				}
				else if ((asNotificationName.equals(HHSP8Constants.NT306))
						&& (asAlertName.equals(HHSP8Constants.AL306)))
				{
					loNotificationLINK = getContractInfo(loNotificationLINK,
							HHSP8Constants.COMPLETE_CONTRACT_BUDGET_URL, lsContractID, lsBudgetID, lsFiscalYearID);
				}
				else if ((asNotificationName.equals(HHSP8Constants.NT302))
						&& (asAlertName.equals(HHSP8Constants.AL302)))
				{
					loNotificationLINK = getContractInfo(loNotificationLINK, HHSP8Constants.CONTRACT_BUDGET_ACTIVE_URL,
							lsContractID, lsBudgetID, lsFiscalYearID);
				}
				else if ((asNotificationName.equals(HHSP8Constants.NT307))
						&& (asAlertName.equals(HHSP8Constants.AL307)))
				{
					loNotificationLINK.append(HHSP8Constants.BUDGET_LIST_URL);
				}
				else if ((asNotificationName.equals(HHSP8Constants.NT308))
						&& (asAlertName.equals(HHSP8Constants.AL308)))
				{
					loNotificationLINK = getContractInfo(loNotificationLINK,
							HHSP8Constants.CONTRACT_BUDGET_UPDATE_REVISIONS_URL, lsContractID, lsBudgetID,
							lsFiscalYearID);

				}
				else if ((asNotificationName.equals(HHSP8Constants.NT309))
						&& (asAlertName.equals(HHSP8Constants.AL309)))
				{
					Channel loChannelObj = new Channel();
					loBudMap.put(HHSP8Constants.BUDGET_ID, lsBudgetID);
					loChannelObj.setData(HHSP8Constants.LOHMAP, loBudMap);
					TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.FETCH_BASE_BUD_FROM_MOD);
					loBudMap = (HashMap) loChannelObj.getData(HHSP8Constants.PARENT_BUDGET_ID);
					lsContractID = (String) loBudMap.get(HHSP8Constants.CONTRACT_ID_WORKFLOW);
					loNotificationLINK = getContractInfo(loNotificationLINK, HHSP8Constants.CONTRACT_BUDGET_ACTIVE_URL,
							lsContractID, (String) loBudMap.get(HHSP8Constants.BUDGET_ID), lsFiscalYearID);

				}
				else if ((asNotificationName.equals(HHSP8Constants.NT310))
						&& (asAlertName.equals(HHSP8Constants.AL310)))
				{
					loNotificationLINK = getContractInfo(loNotificationLINK,
							HHSP8Constants.CONTRACT_BUDGET_MODIFICATION_REVISION_URL, lsContractID, lsBudgetID,
							lsFiscalYearID);

				}
				else if ((asNotificationName.equals(HHSP8Constants.NT311))
						&& (asAlertName.equals(HHSP8Constants.AL311)))
				{
					Channel loChannelObj = new Channel();
					loBudMap.put(HHSP8Constants.BUDGET_ID, lsBudgetID);
					loChannelObj.setData(HHSP8Constants.LOHMAP, loBudMap);
					TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.FETCH_BASE_BUD_FROM_MOD);
					loBudMap = (HashMap) loChannelObj.getData(HHSP8Constants.PARENT_BUDGET_ID);
					lsContractID = (String) loBudMap.get(HHSP8Constants.CONTRACT_ID_WORKFLOW);
					loNotificationLINK = getContractInfo(loNotificationLINK, HHSP8Constants.CONTRACT_BUDGET_ACTIVE_URL,
							lsContractID, (String) loBudMap.get(HHSP8Constants.BUDGET_ID), lsFiscalYearID);
				}
				else if ((asNotificationName.equals(HHSP8Constants.NT312))
						&& (asAlertName.equals(HHSP8Constants.AL312)))
				{

					loNotificationLINK.append(HHSP8Constants.BUDGET_LIST_URL);

				}
				else if ((asNotificationName.equals(HHSP8Constants.NT313))
						&& (asAlertName.equals(HHSP8Constants.AL313)))
				{
					loNotificationLINK = getContractInfo(loNotificationLINK,
							HHSP8Constants.CONTRACT_BUDGET_AMENDMENT_REVISION_URL, lsContractID, lsBudgetID,
							lsFiscalYearID);

				}
				else if ((asNotificationName.equals(HHSP8Constants.NT314))
						&& (asAlertName.equals(HHSP8Constants.AL314)))
				{
					loNotificationLINK = getContractInfo(loNotificationLINK,
							HHSP8Constants.CONTRACT_BUDGET_AMENDMENT_APPROVED_URL, lsContractID, lsBudgetID,
							lsFiscalYearID);

				}
				else if ((asNotificationName.equals(HHSP8Constants.NT315A))
						|| (asNotificationName.equals(HHSP8Constants.NT315B))
						|| (asAlertName.equals(HHSP8Constants.AL315)))
				{
					if (lsLaunchByOrgType.equalsIgnoreCase(HHSP8Constants.PROVIDER_ORG))
					{
						loNotificationLINK = getContractInfo(loNotificationLINK, HHSP8Constants.ADVANCE_APPROVED_URL,
								lsContractID, lsBudgetID, lsFiscalYearID);

					}
					else
					{
						loNotificationLINK = new StringBuffer();
						loNotificationLINK.append(PropertyLoader.getProperty(HHSP8Constants.PROPERTY_FILE,
								HHSP8Constants.PROP_CITY_URL));
						loNotificationLINK = getContractInfo(loNotificationLINK, HHSP8Constants.ADVANCE_APPROVED_URL,
								lsContractID, lsBudgetID, lsFiscalYearID);

					}
					setUserIDForUserInitiatedAlert(aoNotificationBean, aoAudienceList);
				}
				loLinkMap.put(HHSP8Constants.LINK, loNotificationLINK.toString());
			}
			aoNotificationBean.setLinkMap(loLinkMap);
			aoNotificationBean.setAgencyLinkMap(loLinkMap);
		}
		catch (ApplicationException aoAppEx)
		{
			throw new ApplicationException(
					"Error occured during executing the retrieveEvaluationStatusId method in WorkflowOperations",
					aoAppEx);
		}
	}

	/**
	 * This method set additional parameter in for notification
	 * 305a,305b,315a,315b
	 * @param aoNotificationBean Notification Bean object
	 * @param aoAudienceList audience list
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void setUserIDForUserInitiatedAlert(NotificationDataBean aoNotificationBean, String[] aoAudienceList)
	{
		HashMap loAddReqMap = new HashMap();
		String lsUserId = null;

		if (null != aoAudienceList)
		{
			lsUserId = aoAudienceList[0];
		}
		loAddReqMap.put(HHSConstants.USER_ID, lsUserId);
		aoNotificationBean.setAdditionalParameterMap(loAddReqMap);
	}

	/**
	 * This method split array string object and set in to componentLinkBean
	 * object which is required for making notification and alert's URL
	 * 
	 * @param aoProperties String array object
	 * @param aoComponentNotificationLinkBean ComponentNotificationLinkBean bean
	 *            object
	 * @throws ApplicationException
	 */
	private void setComponentLinkBean(String[] aoProperties,
			ComponentNotificationLinkBean aoComponentNotificationLinkBean) throws ApplicationException
	{
		LOG_OBJECT.Debug("-----------Inside setComponentLinkBean()-------------");
		String[] loValuePair = null;
		String lsBudgetAdvanceId = null;
		String lsContractId = null;
		for (String lsProperty : aoProperties)
		{
			LOG_OBJECT.Debug("--------------Processing Property-----------:" + lsProperty);
			loValuePair = lsProperty.split("[:]", 2);

			if (loValuePair[0].equalsIgnoreCase(HHSP8Constants.ADVANCE_NUMBER_ENTITY_TYPE))
			{
				if (loValuePair.length >= 2)
				{
					lsBudgetAdvanceId = loValuePair[1];

				}
			}

			if (loValuePair[0].equalsIgnoreCase(HHSP8Constants.PROPERTY_PE_PROCURMENT_ID))
			{
				if (loValuePair.length >= 2)
				{
					aoComponentNotificationLinkBean.setProcurementId(loValuePair[1]);
					if (null != aoComponentNotificationLinkBean.getEntityType()
							&& !aoComponentNotificationLinkBean.getEntityType().equalsIgnoreCase(
									HHSP8Constants.PROPERTY_PE_EVAL_POOL_MAPPING_ID))
					{
						aoComponentNotificationLinkBean.setEntityId(loValuePair[1]);
						aoComponentNotificationLinkBean.setEntityType(HHSConstants.PROCUREMENT);
					}
				}
				else
				{
					aoComponentNotificationLinkBean.setProcurementId("");
				}
			}
			if (loValuePair[0].equalsIgnoreCase(HHSP8Constants.PROPERTY_PE_EVAL_POOL_MAPPING_ID))
			{
				if (loValuePair.length >= 2)
				{
					aoComponentNotificationLinkBean.setEntityId(loValuePair[1]);
					aoComponentNotificationLinkBean.setEntityType(HHSP8Constants.PROPERTY_PE_EVAL_POOL_MAPPING_ID);

				}
			}

			if (loValuePair[0].equalsIgnoreCase(HHSP8Constants.PROPERTY_PE_CONTRACT_ID))
			{
				if (loValuePair.length >= 2)
				{
					lsContractId = (loValuePair[1]);
					aoComponentNotificationLinkBean.setContractId((loValuePair[1]));
					aoComponentNotificationLinkBean.setEntityId(loValuePair[1]);
					aoComponentNotificationLinkBean.setEntityType(HHSConstants.CONTRACT);
				}
				else
				{
					aoComponentNotificationLinkBean.setContractId("");
				}
			}

			if (loValuePair[0].equalsIgnoreCase(HHSP8Constants.PROPERTY_PE_BUDGET_ID))
			{
				if (loValuePair.length >= 2)
				{
					aoComponentNotificationLinkBean.setBudgetId((loValuePair[1]));
					if (!loValuePair[1].isEmpty())
					{
						aoComponentNotificationLinkBean.setEntityId(loValuePair[1]);
						aoComponentNotificationLinkBean.setEntityType(HHSConstants.BUDGETLIST_BUDGET);
					}
				}
				else
				{
					aoComponentNotificationLinkBean.setBudgetId("");
				}
			}

			if (loValuePair[0].equalsIgnoreCase(HHSP8Constants.PROPERTY_PE_NEW_FISCAL_YEAR_ID))
			{
				if (loValuePair.length >= 2)
				{
					aoComponentNotificationLinkBean.setFiscalYearId((loValuePair[1]));
				}
				else
				{
					aoComponentNotificationLinkBean.setFiscalYearId("");
				}
			}

			if (loValuePair[0].equalsIgnoreCase(HHSP8Constants.INVOICE_ID))
			{
				if (loValuePair.length >= 2)
				{
					aoComponentNotificationLinkBean.setInvoiceID((loValuePair[1]));
					aoComponentNotificationLinkBean.setEntityId(loValuePair[1]);
					aoComponentNotificationLinkBean.setEntityType(HHSConstants.INVOICE);
				}
				else
				{
					aoComponentNotificationLinkBean.setInvoiceID("");
				}
			}
			if (loValuePair[0].equalsIgnoreCase(HHSP8Constants.LAUNCH_ORG_TYPE))
			{
				if (loValuePair.length >= 2)
				{
					aoComponentNotificationLinkBean.setLaunchByOrgType(loValuePair[1]);
				}
				else
				{
					aoComponentNotificationLinkBean.setLaunchByOrgType("");
				}
			}
		}

		if (null != lsBudgetAdvanceId)
		{
			aoComponentNotificationLinkBean.setEntityId(lsContractId);
			aoComponentNotificationLinkBean.setEntityType(HHSP8Constants.BUDGET_ADVANCE);
		}

	}

	/**
	 * This method is used to get Notifications link.
	 * @return
	 * @throws ApplicationException
	 */
	private StringBuffer getNotificationLink() throws ApplicationException
	{

		StringBuffer loNotificationLINK = new StringBuffer();
		String lsServerName = PropertyLoader.getProperty(HHSP8Constants.PROPERTY_FILE,
				HHSP8Constants.SERVER_NAME_FOR_PROVIDER_BATCH);
		String lsServerPort = PropertyLoader.getProperty(HHSP8Constants.PROPERTY_FILE,
				HHSP8Constants.SERVER_PORT_FOR_PROVIDER_BATCH);
		String lsContextPath = PropertyLoader.getProperty(HHSP8Constants.PROPERTY_FILE,
				HHSP8Constants.CONTEXT_PATH_FOR_PROVIDER_BATCH);
		String lsAppProtocol = PropertyLoader.getProperty(HHSP8Constants.PROPERTY_FILE,
				HHSP8Constants.SERVER_PROTOCOL_FOR_PROVIDER_BATCH);
		loNotificationLINK.append(lsAppProtocol);
		loNotificationLINK.append("://");
		loNotificationLINK.append(lsServerName);
		loNotificationLINK.append(":");
		loNotificationLINK.append(lsServerPort);
		loNotificationLINK.append("/");
		loNotificationLINK.append(lsContextPath);

		return loNotificationLINK;
	}

	/**
	 * This method is used to get Contract Info.
	 * @param aoNotificationLINK
	 * @param asUrl
	 * @param lsContractID
	 * @param lsBudgetID
	 * @param lsFiscalYearID
	 * @return
	 */
	private StringBuffer getContractInfo(StringBuffer aoNotificationLINK, String asUrl, String lsContractID,
			String lsBudgetID, String lsFiscalYearID)
	{

		aoNotificationLINK.append(asUrl);
		aoNotificationLINK.append("&contractId=");
		aoNotificationLINK.append(lsContractID);
		aoNotificationLINK.append("&budgetId=");
		aoNotificationLINK.append(lsBudgetID);
		aoNotificationLINK.append("&fiscalYearId=");
		aoNotificationLINK.append(lsFiscalYearID);
		return aoNotificationLINK;

	}
}
