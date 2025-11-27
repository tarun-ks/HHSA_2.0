package com.nyc.hhs.daomanager.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.ApplicationAuditBean;
import com.nyc.hhs.model.CommentsHistoryBean;
import com.nyc.hhs.model.EvaluationDetailBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.DAOUtil;

/**
 * <p>
 * Release 5 Proposal Activity and Char 500 History This service class will be
 * used to fetch the organization's filing audit information for the accelerator
 * user from manager organization screen and the values of filings tab Like-
 * status, last approved date, due date etc. is set.All render and action
 * methods for Budget Summary, Utilities, OTPS,Rent etc screens will use this
 * service to fetch/insert/update data from/to database.
 * </p>
 */

public class AuditHistoryService extends ServiceState
{
	
	/**
	 * Log object to record all logs
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(AuditHistoryService.class);
	
	/**
	 * Release 5 Proposal Activity and Char 500 History This method will fetch
	 * the organization's filing audit information for the accelerator user from
	 * manager organization screen.
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of organizationId, the filing audit information are
	 * received from the DataBase by executing the
	 * <code>fetchOrganizationFilingsAuditView</code> query in the
	 * AuditHistoryMapper</li>
	 * <li>
	 * For sorting, pagination and filter extended BaseFilter class, which will
	 * pass required information as input.</li>
	 * <li>It returns the values as List of ApplicationAuditBean Bean</li>
	 * <li>The values returned are used in the
	 * <code>ProviderAgencyHomeController</code> which in turns helps to display
	 * the information on the filingsAudit.jsp</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param aoApplicationAuditBean ApplicationAuditBean as input
	 * @param aoMybatisSession sql session as input
	 * @return List of ApplicationAuditBean
	 * @throws ApplicationException Exception in case a query fails
	 */
	
	@SuppressWarnings("unchecked")
	public List<ApplicationAuditBean> fetchOrganizationFilingsAuditView(ApplicationAuditBean aoApplicationAuditBean,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		List<ApplicationAuditBean> loApplicationAuditBean = new ArrayList<ApplicationAuditBean>();
		try
		{
			loApplicationAuditBean = (List<ApplicationAuditBean>) DAOUtil.masterDAO(aoMybatisSession,
					aoApplicationAuditBean, HHSR5Constants.MAPPER_CLASS_AUDIT_HISTORY_MAPPER,
					HHSR5Constants.FETCH_ORGANIZATION_FILINGS_AUDIT_VIEW, HHSR5Constants.APPLICATION_AUDIT_BEAN);
			
			LOG_OBJECT.Debug("Successfully fetched the organizationm information");
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Organization's filing audit information for the accelerator user", aoAppExp);
			throw aoAppExp;
		}
		return loApplicationAuditBean;
	}
	
	/**
	 * This method is used to get a list of documents
	 * 
	 * @param aoApplicationAuditBeanList list of type ApplicationAuditBean
	 * @param aoMybatisSession SqlSession object
	 * @return loDocumentList List of documents
	 * @throws ApplicationException Exception in case a query fails
	 */
	public List<String> getDocumentList(List<ApplicationAuditBean> aoApplicationAuditBeanList,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		 List<String> loDocumentList = new ArrayList<String>();
		 
		 for (ApplicationAuditBean loApplicationAuditBean : aoApplicationAuditBeanList)
		{
			 if(loApplicationAuditBean.getMsEntityId()!=null && loApplicationAuditBean.getMsEntityId().startsWith("{") && loApplicationAuditBean.getMsEntityId().endsWith("}"))
			 {
				 loDocumentList.add(loApplicationAuditBean.getMsEntityId());
			 }
		}
		 return loDocumentList;
	}
	
	/**
	 * This method is used to fetch Organzation Filings 
	 * 
	 * @param aoApplicationAuditBeanList ApplicationAuditBean object
	 * @param loDocProp document properties hashmap
	 * @return aoApplicationAuditBeanList 
	 * @throws ApplicationException Exception in case a query fails
	 */
	@SuppressWarnings("unchecked")
	public List<ApplicationAuditBean> fetchOrganizationFilingsAuditViewFinal(List<ApplicationAuditBean> aoApplicationAuditBeanList,
			HashMap loDocProp) throws ApplicationException
	{
		for (ApplicationAuditBean loApplicationAuditBean : aoApplicationAuditBeanList)
		{
			
			if(loApplicationAuditBean.getMsEntityId()!=null && loApplicationAuditBean.getMsEntityId().startsWith("{") && loApplicationAuditBean.getMsEntityId().endsWith("}"))
			{
				loApplicationAuditBean.setUserIdContractRestriction((String)((HashMap)loDocProp.get(loApplicationAuditBean.getMsEntityId())).get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
			}
			
		}
		return aoApplicationAuditBeanList;
	}
	/**
	 * Release 5 Proposal Activity and Char 500 History This method will fetch
	 * the organization's filing count on the provider home page.
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of organizationId, the organization filing count is
	 * received from the DataBase by executing the
	 * <code>fetchOrganizationFilingsAuditViewCount</code> query in the
	 * AuditHistoryMapper</li>
	 * <li>It returns the values as Integer with count related information</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param aoMybatisSession sql session as input
	 * @param aoApplicationAuditBean ApplicationAuditBean as input
	 * @return Integer Having filings count
	 * @throws ApplicationException Exception in case a query fails
	 */
	
	public Integer getFilingsCount(ApplicationAuditBean aoApplicationAuditBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		Integer loFilingsCount = HHSConstants.INT_ZERO;
		try
		{
			loFilingsCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoApplicationAuditBean,
					HHSR5Constants.MAPPER_CLASS_AUDIT_HISTORY_MAPPER,
					HHSR5Constants.FETCH_ORGANIZATION_FILINGS_AUDIT_VIEW_COUNT, HHSR5Constants.APPLICATION_AUDIT_BEAN);
			setMoState("Amended Contract count fetched successfully for org type:");
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsService: getContractsCount method:: ", loExp);
			setMoState("Error while getting amended contract count for org type:");
			throw loExp;
		}
		return loFilingsCount;
	}
	
	/**
	 * Release 5 Proposal Activity and Char 500 History
	 * 
	 * @param aoApplicationAuditBean ApplicationAuditBean as input
	 * @param aoMybatisSession sql session as input
	 * @return loFilingsAuditDropDown List of String
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public List<String> fetchOrganizationFilingsAuditViewFilingDropDown(ApplicationAuditBean aoApplicationAuditBean,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		List<String> loFilingsAuditDropDown = new ArrayList<String>();
		try
		{
			loFilingsAuditDropDown = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoApplicationAuditBean,
					HHSR5Constants.MAPPER_CLASS_AUDIT_HISTORY_MAPPER,
					HHSR5Constants.ORGANIZATION_FILINGS_AUDIT_VIEW_FILINGS_DROPDOWN,
					HHSR5Constants.APPLICATION_AUDIT_BEAN);
			setMoState("Amended Contract count fetched successfully for org type:");
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsService: getContractsCount method:: ", loExp);
			setMoState("Error while getting amended contract count for org type:");
			throw loExp;
		}
		return loFilingsAuditDropDown;
	}
	
	/**
	 * Release 5 Proposal Activity and Char 500 History This method will fetch
	 * the organization's filing information on the provider home page.
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of organizationId, the organization filing information
	 * are received from the DataBase by executing the
	 * <code>getFilingsInformationHomePage</code> query in the
	 * AuditHistoryMapper</li>
	 * <li>It returns the values as Map with CHAR500 related information</li>
	 * <li>The values returned are used in the
	 * <code>HomeAccountMaintenanceController</code> which in turns helps to
	 * display the information on the filingHomePageProvider.jsp</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param aoMybatisSession sql session as input
	 * @param asOrgId Organisation id as input
	 * @return Map Fining information map
	 * @throws ApplicationException Exception in case a query fails
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Map getFilingsInformationHomePage(SqlSession aoMybatisSession, String asOrgId) throws ApplicationException
	{
		Map<String, Object> loFilingsMap = new HashMap<String, Object>();
		try
		{
			loFilingsMap = (Map<String, Object>) DAOUtil.masterDAO(aoMybatisSession, asOrgId,
					HHSR5Constants.MAPPER_CLASS_AUDIT_HISTORY_MAPPER, HHSR5Constants.GET_FILING_INFORMATION_HOMEPAGE,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Due date fetched successfully for Provider Id:" + asOrgId);
			
			Calendar loCalender = Calendar.getInstance();
			java.util.Date loCurrentDate = new java.util.Date(loCalender.getTimeInMillis());
			loCalender.add(Calendar.DAY_OF_MONTH, 60);
			java.util.Date loCurrentDateToDueDate = new java.util.Date(loCalender.getTimeInMillis());
			
			if (loFilingsMap != null)
			{
				setTextMessageProviderFilingsHomepages(loFilingsMap, loCurrentDate, loCurrentDateToDueDate);
				setStatusProviderFilingsHomepages(loFilingsMap, loCurrentDate, loCurrentDateToDueDate);
			}
		}
		catch (ApplicationException aoAoExp)
		{
			setMoState("Error occurred while fetching due date for Provider Id:" + asOrgId);
			throw aoAoExp;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occurred while fetching due date for Provider Id:", aoExp);
			setMoState("Error occurred while fetching due date for Provider Id:");
			throw new ApplicationException("Error occurred while fetching due date for Provider Id:", aoExp);
		}
		return loFilingsMap;
	}
	
	/**
	 * Release 5 Proposal Activity and Char 500 History This method will set the
	 * organization's filing status on the provider home page.
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of Corporate ID, Exempt from filing, Approved count and
	 * Due Dates the status of Filings is set in the Map</li></li>
	 * </ul>
	 * @param aoFilingsMap HashMap as Input
	 * @param aoDueDate as date input
	 * @param aoCurrentDateToDueDate 
	 * @param loFilingsMap Map as Input
	 * @param loDueDate Date as Input
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	private void setStatusProviderFilingsHomepages(Map<String, Object> aoFilingsMap, java.util.Date aoDueDate,
			java.util.Date aoCurrentDateToDueDate) throws ApplicationException
	{
		
		// non profit
		if (((BigDecimal) aoFilingsMap.get(HHSR5Constants.APPROVED_BUSINESS_APP_COUNT))
				.compareTo(new BigDecimal("0.00")) == 0
				&& ((String) aoFilingsMap.get(HHSR5Constants.CORPORATE_STRUCTURE))
						.equalsIgnoreCase(ApplicationConstants.ORG_CORPORATE_NON_PROFIT))
		{
			aoFilingsMap.put(HHSR5Constants.FILING_STATUS, HHSR5Constants.AWAITING_APPLICATION_APPROVAL);
			
		}
		
		if (aoFilingsMap.get(HHSR5Constants.EXEMPT_FROM_FILING) != null
				&& aoFilingsMap.get(HHSR5Constants.CORPORATE_STRUCTURE) != null
				&& ((String) aoFilingsMap.get(HHSR5Constants.CORPORATE_STRUCTURE))
						.equalsIgnoreCase(ApplicationConstants.ORG_CORPORATE_NON_PROFIT))
		{
			if (aoFilingsMap.get(HHSR5Constants.DUE_DATES) != null
					&& ((String) aoFilingsMap.get(HHSR5Constants.EXEMPT_FROM_FILING))
							.equalsIgnoreCase(ApplicationConstants.SYSTEM_NO)
					&& aoCurrentDateToDueDate.before((java.util.Date) aoFilingsMap.get(HHSR5Constants.DUE_DATES)))
			{
				aoFilingsMap.put(HHSR5Constants.FILING_STATUS, HHSR5Constants.CURRENT);
			}
			else if (((String) aoFilingsMap.get(HHSR5Constants.EXEMPT_FROM_FILING))
					.equalsIgnoreCase(ApplicationConstants.SYSTEM_YES))
			{
				aoFilingsMap.put(HHSR5Constants.FILING_STATUS, HHSR5Constants.EXEMPT);
			}
			else if (aoFilingsMap.get(HHSR5Constants.DUE_DATES) != null
					&& ((String) aoFilingsMap.get(HHSR5Constants.EXEMPT_FROM_FILING))
							.equalsIgnoreCase(ApplicationConstants.SYSTEM_NO)
					&& ((java.util.Date) aoFilingsMap.get(HHSR5Constants.DUE_DATES)).before(aoDueDate))
			{
				aoFilingsMap.put(HHSR5Constants.FILING_STATUS, ApplicationConstants.STATUS_EXPIRED);
			}
			else if (aoFilingsMap.get(HHSR5Constants.DUE_DATES) != null
					&& ((String) aoFilingsMap.get(HHSR5Constants.EXEMPT_FROM_FILING))
							.equalsIgnoreCase(ApplicationConstants.SYSTEM_NO)
					&& aoCurrentDateToDueDate.after((java.util.Date) aoFilingsMap.get(HHSR5Constants.DUE_DATES)))
			{
				aoFilingsMap.put(HHSR5Constants.FILING_STATUS, HHSR5Constants.EXPIRINIG_SOON);
			}
		}
		
		// for profit
		
		if (aoFilingsMap.get(HHSR5Constants.CORPORATE_STRUCTURE) != null
				&& aoFilingsMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID) != null
				&& ((String) aoFilingsMap.get(HHSR5Constants.CORPORATE_STRUCTURE))
						.equalsIgnoreCase(ApplicationConstants.ORG_CORPORATE_FOR_PROFIT))
		{
			if (((String) aoFilingsMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID))
					.equalsIgnoreCase(HHSR5Constants.CORPORATION_ANY))
			{
				aoFilingsMap.put(HHSR5Constants.FILING_STATUS, HHSR5Constants.PROFIT_CORPORATION);
			}
			else if (((String) aoFilingsMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID)).equalsIgnoreCase(HHSR5Constants.LLC))
			{
				aoFilingsMap.put(HHSR5Constants.FILING_STATUS, HHSR5Constants.PROFIT_LLC);
			}
			else if (((String) aoFilingsMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID))
					.equalsIgnoreCase(HHSR5Constants.JOINT_VENTURE))
			{
				aoFilingsMap.put(HHSR5Constants.FILING_STATUS, HHSR5Constants.PROFIT_JOINT_VENTURE);
			}
			else if (((String) aoFilingsMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID))
					.equalsIgnoreCase(HHSR5Constants.PARTNERSHIP_ANY))
			{
				aoFilingsMap.put(HHSR5Constants.FILING_STATUS, HHSR5Constants.PROFIT_PARTNERSHIP);
			}
			else if (((String) aoFilingsMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID))
					.equalsIgnoreCase(HHSR5Constants.SOLE_PROPRIETER))
			{
				aoFilingsMap.put(HHSR5Constants.FILING_STATUS, HHSR5Constants.PROFIT_SOLE_PROPRIETER);
			}
			else if (((String) aoFilingsMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID)).equalsIgnoreCase(HHSConstants.OTHER))
			{
				aoFilingsMap.put(HHSR5Constants.FILING_STATUS, HHSR5Constants.PROFIT_OTHER);
			}
		}
	}
	
	/**
	 * This method will set the Message to be displayed on the provider home
	 * page.
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of Corporate ID, Exemp from filing, Procurement status
	 * and Due Dates the Message is set in the Map</li></li>
	 * </ul>
	 * @param aoFilingsMap HashMap as Input
	 * @param aoDueDate as Date Input
	 * @param aoCurrentDateToDueDate as Date Input
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	private void setTextMessageProviderFilingsHomepages(Map<String, Object> aoFilingsMap, java.util.Date aoDueDate,
			java.util.Date aoCurrentDateToDueDate) throws ApplicationException
	{
		
		if (aoFilingsMap.get(HHSR5Constants.PROC_STATUS) != null
				&& aoFilingsMap.get(HHSR5Constants.EXEMPT_FROM_FILING) != null
				&& aoFilingsMap.get(HHSR5Constants.CORPORATE_STRUCTURE) != null
				&& ((String) aoFilingsMap.get(HHSR5Constants.CORPORATE_STRUCTURE))
						.equalsIgnoreCase(ApplicationConstants.ORG_CORPORATE_NON_PROFIT))
		{
			if (aoFilingsMap.get(HHSR5Constants.DUE_DATES) != null
					&& ((java.util.Date) aoFilingsMap.get(HHSR5Constants.DUE_DATES)).before(aoDueDate))
			{
				aoFilingsMap.put(HHSR5Constants.TEXT_MESSAGE, HHSR5Constants.CHAR500_EXPIRED);
				aoFilingsMap.put(HHSR5Constants.CLASS_NAME, HHSR5Constants.RED_EX_MARK);
			}
			else if (aoFilingsMap.get(HHSR5Constants.DUE_DATES) != null
					&& ((java.util.Date) aoFilingsMap.get(HHSR5Constants.DUE_DATES)).before(aoCurrentDateToDueDate))
			{
				aoFilingsMap.put(HHSR5Constants.TEXT_MESSAGE,
						HHSR5Constants.CHAR500_EXPIRE + aoFilingsMap.get(HHSR5Constants.DUE_DATE_TO_DISPLAY));
				aoFilingsMap.put(HHSR5Constants.CLASS_NAME, HHSR5Constants.RED_EX_MARK);
			}
			
			
			if (((String) aoFilingsMap.get(HHSR5Constants.PROC_STATUS))
					.equalsIgnoreCase(HHSConstants.BULK_UPLOAD_FILE_STATUS_IN_PROGRESS))
			{
				aoFilingsMap.put(HHSR5Constants.TEXT_MESSAGE2, HHSR5Constants.CHAR500_PENDING);
				aoFilingsMap.put(HHSR5Constants.CLASS_NAME2, HHSR5Constants.INFO_ICON);
			}
			else if (((String) aoFilingsMap.get(HHSR5Constants.PROC_STATUS)).equalsIgnoreCase(HHSConstants.RETURNED))
			{
				aoFilingsMap.put(HHSR5Constants.TEXT_MESSAGE2,HHSR5Constants.CHAR500_RETURNED);
				aoFilingsMap.put(HHSR5Constants.CLASS_NAME2, HHSR5Constants.RED_EX_MARK);
			}
		}
	}
	
	/**
	 * Release 5 Proposal Activity and Char 500 History This method will fetch
	 * proposal audit history on view proposal screen.
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of proposalId, the proposal information are received
	 * from the DataBase by executing the <code>fetchProposalTaskHistory</code>
	 * query in the AuditHistoryMapper</li>
	 * <li>It returns the values as List of bean with proposal history
	 * information.</li>
	 * <li>The values returned are used in the <code>BaseControllerSM</code>
	 * which in turns helps to display the information on the viewResponse.jsp</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param aoHMApplicationAudit HashMap as input
	 * @param aoMybatisSession sql session as input
	 * @return List as output
	 * @throws ApplicationException exception in case a query fails
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<CommentsHistoryBean> fetchProposalTaskHistory(HashMap aoHMApplicationAudit, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		List<CommentsHistoryBean> loResultList = new ArrayList<CommentsHistoryBean>();
		try
		{
			
			loResultList = (List<CommentsHistoryBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMApplicationAudit,
					HHSR5Constants.MAPPER_CLASS_AUDIT_HISTORY_MAPPER, HHSR5Constants.FETCH_PROPOSAL_TASK_HISTORY,
					HHSR5Constants.JAVA_UTIL_HASH_MAP);
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Exception occured while fetching proposal task history", aoAppExp);
			throw aoAppExp;
		}
		return loResultList;
	}
	
	/**
	 * Release 5 Proposal Activity and Char 500 History
	 * <p>
	 * This method will call on Click of View Progress on Procurement Summary
	 * screen, it will fetch Evaluator list to check the status of evaluations.
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of
	 * evaluationPoolMappingId,proposalStatusId,procurementId,statusId the
	 * evaluators information are received from the DataBase by executing the
	 * <code>fetchEvaluationProgress</code> query in the EvaluationMapper</li>
	 * <li>It returns the values as List of bean with Evaluation Progress
	 * Information.</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession SqlSession session as input
	 * @param aoParameterMap Map<String, String> HashMap as input
	 * @return loEvaluationDetailsList List<EvaluationDetailBean> as output
	 * @throws ApplicationException exception in case a query fails
	 */
	@SuppressWarnings("unchecked")
	public List<EvaluationDetailBean> getEvaluationProgress(SqlSession aoMybatisSession,
			Map<String, String> aoParameterMap) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entering getEvaluationProgress");
		List<EvaluationDetailBean> loEvaluationDetailsList = null;
		try
		{
			loEvaluationDetailsList = (List<EvaluationDetailBean>) DAOUtil.masterDAO(aoMybatisSession, aoParameterMap,
					HHSR5Constants.MAPPER_CLASS_EVALUATION_MAPPER, HHSR5Constants.FETCH_EVALUATION_PROGRESS,
					HHSR5Constants.JAVA_UTIL_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while getEvaluationProgress", aoAppEx);
			setMoState("Error while getEvaluationProgress");
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while getEvaluationProgress", aoExp);
			setMoState("Error while getEvaluationProgress");
			throw new ApplicationException(
					"Error while getEvaluationProgress", aoExp);
		}
		return loEvaluationDetailsList;
	}

	
	/**
	 * This method is used to return status of fetching export tasks
	 * 
	 * @param aoFilingsMap HashMap<String, String> Filings map
	 * @param aoMybatisSession SqlSession object
	 * @return boolean value loStatus 
	 * @throws ApplicationException exception in case a query fails
	 */
	
	
	public Boolean exportAllTask(HashMap<String, String> aoFilingsMap, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		Boolean loStatus = false;
		try
		{
			
		 DAOUtil.masterDAO(aoMybatisSession, aoFilingsMap,
					HHSR5Constants.MAPPER_CLASS_AUDIT_HISTORY_MAPPER, HHSR5Constants.EXPORT_ALL_TASK,
					HHSR5Constants.JAVA_UTIL_HASH_MAP);
		 loStatus = true;
			
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Exception occured while fetching All Tasks", aoAppExp);
			throw aoAppExp;
		}
		return loStatus;
	}
	
}