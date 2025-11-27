package com.nyc.hhs.daomanager.service;

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
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.BudgetAdvanceBean;
import com.nyc.hhs.model.BudgetList;
import com.nyc.hhs.service.filenetmanager.p8services.P8ProcessServiceForSolicitationFinancials;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * Service Class to Set the data for BudgetList Sets data for BudgetList Bean
 */

public class FinancialsBudgetService extends ServiceState
{

	private static final LogInfo LOG_OBJECT = new LogInfo(FinancialsBudgetService.class);

	/**
	 * <ol>
	 * <li>Method to fetch data from database.</li>
	 * <li>This method is invoked by getFinancialsBudgetList transaction id.</li>
	 * <li>This method invokes the SQL query in BudgetMapper.xml to populate
	 * data from database.</li>
	 * <li>Column values fetched by this service class queries from database
	 * are:-
	 * <ul>
	 * <li>budgetTitle</li>
	 * <li>budgetValue</li>
	 * <li>dateOfLastUpdate</li>
	 * <li>status</li>
	 * <li>fiscalYear</li>
	 * <li>name</li>
	 * <li>agency</li>
	 * </ul>
	 * <li>Query Ids fetchBudgetListForProvider,fetchBudgetListForAgency,
	 * fetchBudgetListForCity fetches the data from database from
	 * BudgetMApper.xml</li>
	 * </ol>
	 * 
	 * @param aoMybatisSession
	 * @param asUserType <ol>
	 *            <li>On the basis of the userType sent to the service class,its
	 *            corresponding query will be triggered</li>
	 *            <li>For different User Types City,Provider & Agency three
	 *            different query id's are invoked</li>
	 *            </ol>
	 * @param budgetFilterBean <li>This bean provides the sorting and pagination
	 *            parameters to this service class.</li> <li>The various sorting
	 *            and pagination parameters provided by this bean are:-</li>
	 *            <ol>
	 *            <li>pageIndex</li>
	 *            <li>startNode</li>
	 *            <li>endNode</li>
	 *            <li>firstSortType</li>
	 *            <li>secondSortType</li>
	 *            <li>firstSort</li>
	 *            <li>secondSort</li>
	 *            <li>sortColumnName</li>
	 *            </ol>
	 *            Made changes in this method for build 3.2.0, defect id 6384.
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<BudgetList> fetchBudgetListSummary(SqlSession aoMybatisSession, String asUserType,
			BudgetList aoBudgetBean) throws ApplicationException
	{   
		List<BudgetList> loBudgetList = null;
		String lsFandFPCount = null;
		List<String> loContractForRestriction = null;
       
		try
		{   		   
			if (null != aoBudgetBean.getLsRequestFromHomePage()
					&& aoBudgetBean.getLsRequestFromHomePage().equals(HHSConstants.FALSE))
			{
				defaultStatusOnBudget(aoBudgetBean);
			}

			if (aoBudgetBean.getBudgetStatusList() != null && aoBudgetBean.getBudgetTypeList() != null)
			{
				// remove comma from payment value
				if (aoBudgetBean.getBudgetValueFrom() != null)
				{
					aoBudgetBean.setBudgetValueFrom(aoBudgetBean.getBudgetValueFrom().replaceAll(HHSConstants.COMMA,
							HHSConstants.EMPTY_STRING));
				}
				if (aoBudgetBean.getBudgetValueTo() != null)
				{
					aoBudgetBean.setBudgetValueTo(aoBudgetBean.getBudgetValueTo().replaceAll(HHSConstants.COMMA,
							HHSConstants.EMPTY_STRING));
				}
				if (asUserType != null && asUserType.equals(ApplicationConstants.PROVIDER_ORG))
				{
					loBudgetList = (List<BudgetList>) DAOUtil.masterDAO(aoMybatisSession, aoBudgetBean,
							HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
							HHSConstants.FIN_BDG_SR_FETCH_BUDGET_LIST_FOR_PROVIDER,
							HHSConstants.FIN_BDG_SR_COM_NYC_HHS_MODEL_BUDGET_LIST);
					
					// build 3.2.0, defect id 6384
					lsFandFPCount = (String) DAOUtil.masterDAO(aoMybatisSession, aoBudgetBean,
							HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
							HHSConstants.FIN_BDG_SR_FETCH_BUDGET_LIST_FOR_PROVIDER_F_AND_FP,
							HHSConstants.FIN_BDG_SR_COM_NYC_HHS_MODEL_BUDGET_LIST);
					// Release 5 Contract Restriction
					loContractForRestriction = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoBudgetBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSR5Constants.FETCH_CONTRACT_FOR_RESTRICTION,
							HHSR5Constants.COM_NYC_HHS_MODEL_BASE_FILTER);
					// Release 5 Contract Restriction
				}
				else if (asUserType != null && asUserType.equals(ApplicationConstants.AGENCY_ORG))
				{
					// Added in R7: set the flag of filter Modification Budget
					// Redirection URL
					if (null != aoBudgetBean.getFilterModificationBudgetRedirection()
							&& aoBudgetBean.getFilterModificationBudgetRedirection() == true)
					{
						loBudgetList = fetchApprovedModificationsBudgets(aoMybatisSession, aoBudgetBean);
				 
					}
					// R7 End
					else
					{
						loBudgetList = (List<BudgetList>) DAOUtil.masterDAO(aoMybatisSession, aoBudgetBean,
								HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
								HHSConstants.FIN_BDG_SR_FETCH_BUDGET_LIST_FOR_AGENCY,
								HHSConstants.FIN_BDG_SR_COM_NYC_HHS_MODEL_BUDGET_LIST);
				
					}
				}
				else if (asUserType != null && asUserType.equals(ApplicationConstants.CITY_ORG))
				{
					// Added in R7: set the flag of filter Modification Budget
					// Redirection URL
					if (null != aoBudgetBean.getFilterModificationBudgetRedirection()
							&& aoBudgetBean.getFilterModificationBudgetRedirection() == true)
					{
						loBudgetList = fetchApprovedModificationsBudgets(aoMybatisSession, aoBudgetBean);

					}
					// R7 End
					else
					{
						loBudgetList = (List<BudgetList>) DAOUtil.masterDAO(aoMybatisSession, aoBudgetBean,
								HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
								HHSConstants.FIN_BDG_SR_FETCH_BUDGET_LIST_FOR_CITY,
								HHSConstants.FIN_BDG_SR_COM_NYC_HHS_MODEL_BUDGET_LIST);
					}
				}
			}
			
			//**Start  QC 9438 R 8.2 - check if Amendment for this FY is Registered/Pending Registration and Amendment Budget is Approved/Active 
			if(loBudgetList != null && !loBudgetList.isEmpty())
			{
				for (BudgetList loBudgetListBean : loBudgetList)
				{					
					if( "Contract Budget".equalsIgnoreCase(loBudgetListBean.getBudgetType()) 
							&& loBudgetListBean.getNegativeAmendCnt() != null && loBudgetListBean.getNegativeAmendCnt() > 0 )
					{
						String baseContractId = loBudgetListBean.getContractId();
						Integer cnt = null;
						cnt = getNegativeAmendmentCountForFY( aoMybatisSession, baseContractId,	loBudgetListBean.getFiscalYear());
						if(cnt == null || cnt == 0)
						{   
							loBudgetListBean.setNegativeAmendCnt(0);
							LOG_OBJECT.Debug("Reset Negative Amendment Cnt to 0 for Budget :: " 
									+ loBudgetListBean.getBudgetId() + " Base ContractId  :: " + baseContractId + " FY  :: " + loBudgetListBean.getFiscalYear());
						}
						
					}
				}
			}
			//**End  QC 9438 R 8.2 - check if Amendment for this FY is Registered/Pending Registration and Amendment Budget is Approved/Active 
			
			//**Start  QC 9490 R 8.4 - return Count of Budget Updates in Approved/Active Status
			if(loBudgetList != null && !loBudgetList.isEmpty())
			{
				for (BudgetList loBudgetListBean : loBudgetList)
				{					
					if( "Budget Update".equalsIgnoreCase(loBudgetListBean.getBudgetType())  )
					{   
						String asContractId = loBudgetListBean.getContractId();
						Integer cnt = null;
						cnt = countBudgetUpdateApproved( aoMybatisSession, asContractId);
						if(cnt != null && cnt > 0)
						{   
							loBudgetListBean.setDeleteBudgetUpdateFlag(cnt);
							LOG_OBJECT.Debug("Set DeleteBudgetUpdateFlag to :: "+ cnt + "  for Budget :: " 
									+ loBudgetListBean.getBudgetId() + " ContractId  :: " + asContractId );
						}
						
					}
				}
			}
			//**End  QC 9490 R 8.4  - return Count of Budget Updates in Approved/Active Status
						
			// build 3.2.0, defect id 6384
			if (lsFandFPCount != null && Integer.parseInt(lsFandFPCount) > 0)
			{
				for (BudgetList loBudgetListBean : loBudgetList)
				{
					loBudgetListBean.setIsFandFP(true);
				}
			}

			// Release 5 Contract Restriction
			if (loContractForRestriction != null)
			{
				for (BudgetList loBudget : loBudgetList)
				{
					for (String lsContractForRestriction : loContractForRestriction)
					{
						if (null != lsContractForRestriction
								&& (Integer.parseInt(lsContractForRestriction) == Integer.parseInt(loBudget
										.getContractId())))
						{
							loBudget.setContractAccess(false);
						}
					}
				}
			}
			// Release 5 Contract Restriction
			setMoState("Budget List fetched successfully for org Type:" + asUserType);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.BUDGET_ID_KEY, aoBudgetBean.getBudgetId());
			LOG_OBJECT.Error("Exception occured while fetching Budget details", loAppEx);
			setMoState("Error while fetching Budget List details for org Id:" + asUserType);
			throw loAppEx;
		}

		return loBudgetList;

	}

	/**
	 * <p>
	 * This method will get the count of budget record for city/provider/agency
	 * on budget list screen.
	 * <li>This method is invoked by getFinancialsBudgetList transaction id.</li>
	 * <li>This query used: fetchBudgetListForProviderCount</li>
	 * <li>This query used: fetchBudgetListForAgencyCount</li>
	 * <li>This query used: fetchBudgetListForCityCount</li>
	 * </p>
	 * Count will be displayed on the jsp and also help us to get page index
	 * which is result of (count of budget records/number allowed per page in
	 * grid).
	 * 
	 * @param aoMybatisSession
	 * @param asUserType
	 * @param aoBudgetBean
	 * @return Integer liBudgetListCount
	 * @throws ApplicationException
	 */
	public Integer getBudgetListCount(SqlSession aoMybatisSession, String asUserType, BudgetList aoBudgetBean)
			throws ApplicationException
	{   
		Integer liBudgetListCount = HHSConstants.INT_ZERO;
		try
		{
			if (aoBudgetBean.getBudgetStatusList() != null && aoBudgetBean.getBudgetTypeList() != null
					&& asUserType != null)
			{
				if (asUserType.equals(ApplicationConstants.PROVIDER_ORG))
				{
					liBudgetListCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoBudgetBean,
							HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
							HHSConstants.FIN_BDG_SR_FETCH_BUDGET_LIST_FOR_PROVIDER_COUNT,
							HHSConstants.FIN_BDG_SR_COM_NYC_HHS_MODEL_BUDGET_LIST);
				}
				else if (asUserType.equals(ApplicationConstants.AGENCY_ORG))
				{

					// Added in R7: set the flag of filter Modification Budget
					// Redirection URL
					if (null != aoBudgetBean.getFilterModificationBudgetRedirection()
							&& aoBudgetBean.getFilterModificationBudgetRedirection() == true)
					{
						liBudgetListCount = fetchApprovedModificationsBudgetsCount(aoMybatisSession,aoBudgetBean);
					}
					// R7 End
					else
					{
						liBudgetListCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoBudgetBean,
								HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
								HHSConstants.FIN_BDG_SR_FETCH_BUDGET_LIST_FOR_AGENCY_COUNT,
								HHSConstants.FIN_BDG_SR_COM_NYC_HHS_MODEL_BUDGET_LIST);
					}
				}
				else if (asUserType.equals(ApplicationConstants.CITY_ORG))
				{
					// Added in R7: set the flag of filter Modification Budget
					// Redirection URL
					if (null != aoBudgetBean.getFilterModificationBudgetRedirection()
							&& aoBudgetBean.getFilterModificationBudgetRedirection() == true)
					{
						liBudgetListCount = fetchApprovedModificationsBudgetsCount(aoMybatisSession,aoBudgetBean);
					}
					// R7 End
					else
					{
						liBudgetListCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoBudgetBean,
								HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
								HHSConstants.FIN_BDG_SR_FETCH_BUDGET_LIST_FOR_CITY_COUNT,
								HHSConstants.FIN_BDG_SR_COM_NYC_HHS_MODEL_BUDGET_LIST);
					}
				}
			}
			setMoState("Budget List fetched successfully for org Type:" + asUserType);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.BUDGET_ID_KEY, aoBudgetBean.getBudgetId());
			LOG_OBJECT.Error("Exception occured while fetching Budget details", loAppEx);
			setMoState("Error while fetching Budget List details for org Id:" + asUserType);
			throw loAppEx;
		}
		 
		return liBudgetListCount;

	}

	/**
	 * <ul>
	 * This method set the default status for city/agency/provider user
	 * </ul>
	 * 
	 * @param BudgetList aoBudgetBean
	 * @return void
	 * @throws ApplicationException
	 */
	private void defaultStatusOnBudget(BudgetList aoBudgetBean) throws ApplicationException
	{
		try
		{
			if (((aoBudgetBean.getBudgetStatusList() == null || aoBudgetBean.getBudgetStatusList().isEmpty() || aoBudgetBean
					.getBudgetStatusList().size() == HHSConstants.INT_ZERO) || (aoBudgetBean.getBudgetTypeList() == null
					|| aoBudgetBean.getBudgetTypeList().isEmpty() || aoBudgetBean.getBudgetTypeList().size() == HHSConstants.INT_ZERO))
					&& (aoBudgetBean.getIsFilter() != null && !aoBudgetBean.getIsFilter()))
			{
				aoBudgetBean.setBudgetStatusList(new ArrayList<String>());
				// STATUS_APPROVED
				aoBudgetBean.getBudgetStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_BUDGET_APPROVED));
				// STATUS_RETURNED_FOR_REVISION
				aoBudgetBean.getBudgetStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.BUDGET_RETURNED_FOR_REVISION));
				// STATUS_PENDING_APPROVAL
				aoBudgetBean.getBudgetStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_BUDGET_PENDING_APPROVAL));
				// STATUS_PENDING_SUBMISSION
				aoBudgetBean.getBudgetStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.BUDGET_PENDING_SUBMISSION));
				// STATUS_ACTIVE
				aoBudgetBean.getBudgetStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_BUDGET_ACTIVE));
				aoBudgetBean.setBudgetTypeList(new ArrayList<String>());
				// Default Budget Type
				aoBudgetBean.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE1);
				aoBudgetBean.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE2);
				aoBudgetBean.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE3);
				aoBudgetBean.getBudgetTypeList().add(HHSConstants.BUDGET_TYPE4);
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.BUDGET_ID_KEY, aoBudgetBean.getBudgetId());
			LOG_OBJECT.Error("Exception occured while fetching Budget details", loAppEx);
			setMoState("Error while fetching Budget List details");
			throw loAppEx;
		}

	}

	/**
	 * This method will return that whether a budget can be modified or not. If
	 * it can not be modified then it will return different errors depending
	 * upon condition which will be satisfied.
	 * <ul>
	 * <li>OnSelect of Modify Budget, if there is an Amendment which is in
	 * progress with Amendment Value less than zero dollars, and the <Amendment
	 * Configuration Task Status> or <Amendment Certification of Funds Task>
	 * Status> not equal to “Complete”is open OR if the <Budget Amendment
	 * Status> = “Pending Submission”, “Pending Approval” or “Returned for
	 * Revision” then display error message: “! Cannot initiate modification
	 * while a negative amendment is in progress and the amendment budget has
	 * not been approved.”</li>
	 * <li>If there is no amendment for this budget, then getErrorCheck() will
	 * be called to check for other errors.</li>
	 * 
	 * <li>This query used: numberOfNegAmendmentsInProgress</li>
	 * <li>This query used: contractNegAmendmentsIdsInProgress</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - Sql session
	 * @param asBudgetId - budget id
	 * @param P8UserSession aoFilenetSession
	 * @param String asContractId
	 * @return String- contains error message
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public String getModifyBudgetFeasibility(SqlSession aoMybatisSession, P8UserSession aoFilenetSession,
			String asBudgetId, String asContractId) throws ApplicationException
	{
		try
		{
			Integer loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asBudgetId,
					HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
					HHSConstants.FIN_BDG_SR_NUMBER_OF_NEG_AMENDMENTS_IN_PROGRESS, HHSConstants.JAVA_LANG_STRING);

			if (loCount > HHSConstants.INT_ZERO)
			{
				List<String> loList = null;
				loList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asBudgetId,
						HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.CONTRACT_IDS_NEG_AMENDMENTS_IN_PROGRESS,
						HHSConstants.JAVA_LANG_STRING);
				Integer loOpenTaskCount = 0;
				HashMap loHmWFProperties = new HashMap();
				for (String lsContractId : loList)
				{
					loHmWFProperties.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsContractId);
					loHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_AMENDMENT_COF);
					loOpenTaskCount = loOpenTaskCount
							+ new P8ProcessServiceForSolicitationFinancials().getOpenTaskCount(aoFilenetSession,
									loHmWFProperties);
				}
				if (loOpenTaskCount > HHSConstants.INT_ZERO)
				{
					return HHSConstants.NEGATIVE_AMENDMENT_IN_PROGRESS;
				}
				else
				{
					for (String lsContractId : loList)
					{
						loHmWFProperties.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsContractId);
						loHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE,
								HHSConstants.TASK_AMENDMENT_CONFIGURATION);
						loOpenTaskCount = loOpenTaskCount
								+ new P8ProcessServiceForSolicitationFinancials().getOpenTaskCount(aoFilenetSession,
										loHmWFProperties);
					}
					if (loOpenTaskCount > HHSConstants.INT_ZERO)
					{
						return HHSConstants.NEGATIVE_AMENDMENT_IN_PROGRESS;
					}
					else
					{
						HashMap<String, String> loBudgetMap = new HashMap<String, String>();
						loBudgetMap.put(HHSConstants.IN_SE_BUDGET_ID, asBudgetId);
						loBudgetMap.put(HHSConstants.BUDGET_TYPE, HHSConstants.ONE);
						loCount = (Integer) DAOUtil
								.masterDAO(
										aoMybatisSession,
										loBudgetMap,
										HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
										HHSConstants.FIN_BDG_SR_NUMBER_OF_BUDGET_AMENDMENTS_OR_MODIFICATIONS_OR_UPDATES_IN_PROGRESS,
										HHSConstants.JAVA_UTIL_HASH_MAP);
						if (loCount > HHSConstants.INT_ZERO)
						{
							return HHSConstants.NEGATIVE_AMENDMENT_IN_PROGRESS;
						}
						else
						{
							return getErrorCheck(aoMybatisSession, aoFilenetSession, asBudgetId, asContractId);
						}
					}
				}
			}
			else
			{
				return getErrorCheck(aoMybatisSession, aoFilenetSession, asBudgetId, asContractId);
			}
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting the amendments count for budget id:" + asBudgetId);
			throw loExp;
		}
		catch (Exception aoAppExp)
		{
			setMoState("Error while getting the amendments count for budget id:" + asBudgetId);
			LOG_OBJECT.Error("Error while getting the amendments count for budget id", aoAppExp);
			throw new ApplicationException("Error while getting the amendments count for budget id", aoAppExp);
		}
	}

	/**
	 * This method will return that whether a budget can be modified or not. If
	 * it can not be modified then it will return different errors depending
	 * upon condition which will be satisfied.
	 * <ul>
	 * <li>
	 * 1)OnSelect of Modify Budget, if there is a budget modification in
	 * progress for the selected budget and the <Budget Modification Status> =
	 * “Pending Submission” or “Pending Approval” or “Returned for Revision”,
	 * then display error message: “! Cannot modify budget while a modification
	 * is already in progress.”</li>
	 * <li>2) OnSelect of Modify Budget, if there is a configuration update
	 * already in progress for the selected budget and the <Configuration Update
	 * Task Status> = “In Review”</li>
	 * OR <Budget Update Status> = “Pending Submission” or “Pending Approval” or
	 * “Return for Revision” then display error message: “! Cannot initiate
	 * modification while configuration/budget update is in progress.”
	 * <li>3) OnSelect of Modify Budget, if there are invoices open for the
	 * selected budget with <Invoice Status> for any invoice associated with
	 * that budget = “Pending Submission”, “Pending Approval”, or “Returned for
	 * Revision” OR if <Payment Status> for any payments associated with that
	 * budget does not equal “Disbursed”</li>
	 * then display error message: “! Cannot initiate budget modification while
	 * there are outstanding Invoices of Payments.”
	 * <li>4) If none of the above conditions is true then null be returned.</li>
	 * <li>This query used:
	 * numberOfBudgetAmendmentsOrModificationsOrUpdatesInProgress</li>
	 * <li>This query used: contractUpdateIdsInProgress</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - Sql session
	 * @param asBudgetId - budget id
	 * @param P8UserSession aoFilenetSession
	 * @param String asContractId
	 * @return String- contains error message
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private String getErrorCheck(SqlSession aoMybatisSession, P8UserSession aoFilenetSession, String asBudgetId,
			String asContractId) throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = new HashMap<String, String>();
		loBudgetMap.put(HHSConstants.IN_SE_BUDGET_ID, asBudgetId);
		loBudgetMap.put(HHSConstants.BUDGET_TYPE, HHSConstants.THREE);

		Integer loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loBudgetMap,
				HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
				HHSConstants.FIN_BDG_SR_NUMBER_OF_BUDGET_AMENDMENTS_OR_MODIFICATIONS_OR_UPDATES_IN_PROGRESS,
				HHSConstants.JAVA_UTIL_HASH_MAP);
		if (loCount > HHSConstants.INT_ZERO)
		{
			return HHSConstants.BUDGET_MODIFICATION_IN_PROGRESS;
		}
		else
		{
			List<String> loList = null;
			loList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asBudgetId,
					HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.CONTRACT_IDS_UPDATES_IN_PROGRESS,
					HHSConstants.JAVA_LANG_STRING);
			String lsTaskStatus = HHSConstants.STRING_ZERO;
			if (loList.size() > HHSConstants.INT_ZERO)
			{
				HashMap loHmWFProperties = new HashMap();
				loHmWFProperties.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, loList.get(0));
				loHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_CONTRACT_UPDATE);
				lsTaskStatus = new P8ProcessServiceForSolicitationFinancials().fetchTaskStatusFromView(
						aoFilenetSession, loHmWFProperties);
			}
			if (lsTaskStatus.endsWith(HHSConstants.FIN_BDG_SR_78))
			{
				return HHSConstants.CONFIGURATION_BUDGET_UPDATE_IN_PROGRESS;
			}
			else
			{
				loBudgetMap.put(HHSConstants.BUDGET_TYPE, HHSConstants.FOUR);

				loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loBudgetMap,
						HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
						HHSConstants.FIN_BDG_SR_NUMBER_OF_BUDGET_AMENDMENTS_OR_MODIFICATIONS_OR_UPDATES_IN_PROGRESS,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				if (loCount > HHSConstants.INT_ZERO)
				{
					return HHSConstants.CONFIGURATION_BUDGET_UPDATE_IN_PROGRESS;
				}
				else
				{
					loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asBudgetId,
							HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
							HHSConstants.FIN_BDG_SR_NUMBER_OF_INVOICES_IN_PROGRESS, HHSConstants.JAVA_LANG_STRING);

					if (loCount > HHSConstants.INT_ZERO)
					{
						return HHSConstants.INVOICE_PAYMENT_OUTSTANDING;
					}
					else
					{
						loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asBudgetId,
								HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
								HHSConstants.FIN_BDG_SR_NUMBER_OF_PAYMENTS_IN_PROGRESS, HHSConstants.JAVA_LANG_STRING);
						if (loCount > HHSConstants.INT_ZERO)
						{
							return HHSConstants.INVOICE_PAYMENT_OUTSTANDING;
						}
						else
						{
							// Start Enhancement 6591 release 3.10.0
							// When invoice is approved but there is no entry in
							// payment table because component is down
							loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asBudgetId,
									HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
									HHSConstants.FIN_BDG_SR_NUMBER_OF_PAYMENTS_IN_PROGRESS_WHEN_INVOICE_APPROVED,
									HHSConstants.JAVA_LANG_STRING);
							if (loCount != HHSConstants.INT_ZERO)
							{
								return HHSConstants.INVOICE_PAYMENT_OUTSTANDING;
							}
							else
							{
								return HHSConstants.NO_ERROR;
							}
							// End Enhancement 6591 release 3.10.0
						}
					}
				}
			}
		}
	}

	/**
	 * This method is triggered from fetchRequestAdvanceInfo transaction which
	 * fetches budget advance details to be displayed on Request/Initiate
	 * advance screens.
	 * <ul>
	 * <li>1) fetchRequestAdvance query is executed from budgetmapper to fetch
	 * budget advance details for the selected budget.
	 * </ul>
	 * 
	 * @param aoMybatisSession Sql session as input parameter.
	 * @param asBudgetId
	 * @return BudgetAdvanceBean loBudgetAdvanceBean
	 * @throws ApplicationException thrown in case sql query fails.
	 * 
	 */
	public BudgetAdvanceBean fetchRequestAdvance(SqlSession aoMybatisSession, String asBudgetId)
			throws ApplicationException
	{
		BudgetAdvanceBean loBudgetAdvanceBean = null;

		try
		{
			loBudgetAdvanceBean = (BudgetAdvanceBean) DAOUtil.masterDAO(aoMybatisSession, asBudgetId,
					HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.FIN_BDG_SR_FETCH_REQUEST_ADVANCE,
					HHSConstants.JAVA_LANG_STRING);

			setMoState("Request Advance information fetched successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Transaction Failed:: BudgetListService:fetchRequestAdvance method -"
					+ " Error while fetching Request Advance information for Budget Id:" + asBudgetId);
			throw loExp;
		}
		return loBudgetAdvanceBean;
	}

	/**
	 * This method is used to cancel Modification Budget If Status Flag is true
	 * ,then setting Modification Budget status to Cancelled
	 * <ul>
	 * 1. Update the Modification budget as 'Cancelled' and set ACTIVE_FLAG as
	 * 0. 2. If successful update, then set lbCancelModificationBudgetStatus as
	 * 'True'
	 * <li>This query used: setModificationBudgetStatus</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SqlSession
	 * @param asBudgetId BudgetId
	 * @return lbCancelModificationBudgetStatus CancelModificationBudget Status
	 *         Flag
	 * @throws ApplicationException
	 */
	public boolean cancelModificationBudget(SqlSession aoMyBatisSession, String asBudgetId) throws ApplicationException
	{
		boolean lbCancelModificationBudgetStatus = Boolean.FALSE;
		try
		{
			Integer loUpdateCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, asBudgetId,
					HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.SET_MODIFICATION_BUDGET_STATUS,
					HHSConstants.JAVA_LANG_STRING);
			

			if (loUpdateCount != HHSConstants.INT_ZERO)
			{
				lbCancelModificationBudgetStatus = Boolean.TRUE;

				HashMap<String, String> asBudgetInfo = new HashMap<String, String> ();
				asBudgetInfo.put(  HHSConstants.BUDGET_ID_KEY, asBudgetId);
				/*[Start] R9.6.0 QC9605  */
				LOG_OBJECT.Debug( "[cancelModificationBudget] Service" );
				// if budget mod is inactive, the line items for it become inactive.
                DAOUtil.masterDAO(aoMyBatisSession, asBudgetInfo,
						HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.CANCEL_BUDGET_MODIFICATION,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				/*[End] R9.6.0 QC9605  */
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException occured while updating modification budget status in cancelModificationBudget ",
							loAppEx);
			setMoState("Transaction Failed:: cancelModificationBudget: cancelModificationBudget method -"
					+ " failed to update modification budget status for budgetId " + asBudgetId + " \n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT
					.Error("ApplicationException occured while updating modification budget status in cancelModificationBudget   ",
							loEx);
			setMoState("Transaction Failed:: cancelModificationBudget: cancelModificationBudget method -"
					+ " failed to update modification budget status for budgetId " + asBudgetId + " \n");
			throw new ApplicationException("Exception occured while updating the modification budget status in db",
					loEx);
		}

		return lbCancelModificationBudgetStatus;

	}

	/**
	 * The Method will terminate all workFlows based on workFlow id passed in
	 * the List in case of Cancel Contract
	 * <ul>
	 * <li>delete Contract Configuration Task .</li>
	 * <li>delete Contract CoF Task .</li>
	 * <li>delete Contract Budget Review Task .</li>
	 * </ul>
	 * 
	 * @param aoUserSession
	 * @param aoWorkFlowIdList List of WorkflowIds
	 * @return lbTerminationFlag Termination Flag
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Boolean terminateWorkflowForBudget(P8UserSession aoUserSession, String asBudgetId,
			Boolean aoCancelModificationBudgetStatus) throws ApplicationException
	{
		Boolean loTerminationFlag = Boolean.FALSE;

		if (aoCancelModificationBudgetStatus)
		{
			TaskService loTaskService = new TaskService();
			HashMap loWorkflowProperties = new HashMap();
			loWorkflowProperties.put(HHSConstants.PROPERTY_PE_BUDGET_ID, asBudgetId);
			loWorkflowProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.WF_CONTRACT_BUDGET_MODIFICATION);
			loTerminationFlag = loTaskService.closeAllOpenTask(aoUserSession, loWorkflowProperties);
		}
		return loTerminationFlag;
	}

	/**
	 * This method is triggered from updateAdvanceDetails Transaction. It
	 * generates an autogenerated Advance Number every time an advance is
	 * initiated or requested.
	 * <ul>
	 * <li>1. We get the asBudgetId from request parameters</li>
	 * <li>2. To get the count of number of advances requested for the selected.
	 * budget for the current month, execute fetchAdvanceCount select query from
	 * the budgetmapper</li>
	 * <li>3. The count returned from fetchAdvanceCount is set in
	 * liAdvanceCount.</li>
	 * <li>4. The private method generateAdvanceNumber is called, which takes
	 * liAdvanceCount and budget fiscal year as input and returns an
	 * autogenerated Advance Number whose value is set in
	 * lsAutoGeneratedAdvNumber.</li>
	 * <li>5. lsAutoGeneratedAdvNumber is set in advanceNumber of
	 * aoBudgetAdvanceBean and aoBudgetAdvanceBean is returned as an output
	 * parameter.
	 * </ul>
	 * 
	 * @param aoMybatisSession Sql session as input parameter.
	 * @param aoBudgetAdvanceBean BudgetAdvanceBean as input.This will contain
	 *            the parameter(budget id) for where clause used in select query
	 *            and fiscal year used for generation of advance number
	 * @return aoBudgetAdvanceBean populated with advance number for the advance
	 *         initiated or requested.
	 * @throws ApplicationException Exception thrown in case sql query fails.
	 */
	public BudgetAdvanceBean fetchAdvanceNumber(SqlSession aoMybatisSession, BudgetAdvanceBean aoBudgetAdvanceBean)
			throws ApplicationException
	{

		Integer liAdvanceCount = 0;
		String lsAutoGeneratedAdvNumber = null;
		try
		{
			liAdvanceCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, null,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.GET_SEQ_FOR_INVOICE_ADVANCE_NUMBER, null);
			lsAutoGeneratedAdvNumber = generateAdvanceNumber(liAdvanceCount);
			aoBudgetAdvanceBean.setAdvanceNumber(lsAutoGeneratedAdvNumber);
			setMoState("Advance Count fetched successfully for the current month Advances:" + liAdvanceCount);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.BUDGET_ID_KEY, aoBudgetAdvanceBean.getBudgetId());
			LOG_OBJECT.Error("Exception occured while fetching Advance Count", loAppEx);
			setMoState("Error while fetching Advance count for the current month Advances");
			throw loAppEx;
		}
		return aoBudgetAdvanceBean;
	}

	/**
	 * This method inserts the record for the advance(initiated/requested) or
	 * Invoice in DB. If 'Operation' key returns value returns 'Advance' it will
	 * insert Advance. If 'Operation' key returns 'Invoice', this function
	 * inserts Invoice. This method is synchronized to maintain unique
	 * constraint on ADVANCE_NUMBER and INVOICE_NUMBER as both share common
	 * pattern and common sequence
	 * <ul>
	 * <li>1. insertBudgetAdvanceDetail query is executed from budgetmapper to
	 * insert advance details into DB.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Sql session as input parameter.
	 * @param aoBudgetAdvanceBean BudgetAdvanceBean as input.This will contain
	 *            the parameters for inserting values into DB for the
	 *            initiated/requested advance.
	 * @return liRowsInserted
	 * @throws ApplicationException Exception thrown in case sql query fails.
	 */
	public int insertBudgetAdvance(SqlSession aoMybatisSession, HashMap<String, Object> aoHashMap)
			throws ApplicationException
	{
		int liRowsInserted = 0;
		String lsOrgTyp = "";
		// int liAdvanceNumberCount = 0;//COMMENTED ON 8-AUG for Synchronization
		Integer liAdvanceCount = 0;
		String lsAutoGeneratedAdvNumber = null;

		BudgetAdvanceBean loBudgetAdvanceBean = null;
		try
		{
			synchronized (this)
			{
				// Fetch Advance NUmber
				liAdvanceCount = (Integer) DAOUtil
						.masterDAO(aoMybatisSession, null, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
								HHSConstants.GET_SEQ_FOR_INVOICE_ADVANCE_NUMBER, null);
				lsAutoGeneratedAdvNumber = generateAdvanceNumber(liAdvanceCount);

				if (HHSConstants.ADVANCE.equalsIgnoreCase((String) aoHashMap.get(HHSConstants.PARAM_KEY_OPERATION)))
				{
					loBudgetAdvanceBean = (BudgetAdvanceBean) aoHashMap.get(HHSConstants.CBL_AO_BUDGET_ADVANCE_BEAN);
					loBudgetAdvanceBean.setAdvanceNumber(lsAutoGeneratedAdvNumber);

					loBudgetAdvanceBean.setAdvAmntRequested(loBudgetAdvanceBean.getAdvAmntRequested().replaceAll(
							HHSConstants.COMMA, HHSConstants.EMPTY_STRING));
					lsOrgTyp = loBudgetAdvanceBean.getOrgType();
					if (lsOrgTyp.equalsIgnoreCase(HHSConstants.PROVIDER_ORG))
					{
						loBudgetAdvanceBean.setModifyByProvider(loBudgetAdvanceBean.getUserId());
					}
					else
					{
						loBudgetAdvanceBean.setModifyByAgency(loBudgetAdvanceBean.getUserId());
					}

					liRowsInserted = (Integer) DAOUtil.masterDAO(aoMybatisSession, loBudgetAdvanceBean,
							HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
							HHSConstants.FIN_BDG_SR_INSERT_BUDGET_ADVANCE_DETAIL,
							HHSConstants.FIN_BDG_SR_COM_NYC_HHS_MODEL_BUDGET_ADVANCE_BEAN);
					setMoState("Budget Advance details inserted successfully for budgetId :"
							+ loBudgetAdvanceBean.getBudgetId());
				}
				else if (HHSConstants.INVOICE
						.equalsIgnoreCase((String) aoHashMap.get(HHSConstants.PARAM_KEY_OPERATION)))
				{
					aoHashMap.put(HHSConstants.IS_NUMBER, lsAutoGeneratedAdvNumber);
					DAOUtil.masterDAO(aoMybatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
							HHSConstants.CBY_FETCH_INVOICE_SEQUENCE_NUMBER, HHSConstants.JAVA_UTIL_HASH_MAP);
					// Current Seq for InvoiceId
					liRowsInserted = Integer.valueOf(aoHashMap.get(HHSConstants.AI_CURRENT_SEQ).toString());

					setMoState("Invoice inserted successfully for Invoice Id :" + liRowsInserted);

				}

				/****
				 * COMMENTED ON 8-AUG for Synchronization
				 * 
				 * liAdvanceNumberCount = (Integer)
				 * DAOUtil.masterDAO(aoMybatisSession,
				 * aoBudgetAdvanceBean.getAdvanceNumber(),
				 * HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
				 * HHSConstants.FETCH_ADVANCE_NUMBER_COUNT,
				 * HHSConstants.JAVA_LANG_STRING); if(liAdvanceNumberCount >
				 * HHSConstants.INT_ZERO){
				 * aoBudgetAdvanceBean.setAdvanceNumber(String
				 * .valueOf((Integer.parseInt
				 * (aoBudgetAdvanceBean.getAdvanceNumber()) +
				 * HHSConstants.INT_ONE))); }
				 */
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.BUDGET_ID_KEY, loBudgetAdvanceBean.getBudgetId());
			LOG_OBJECT.Error("Error while inserting budget advance details for budgetId ", loAppEx);
			setMoState("Error while inserting budget advance details for budgetId" + loBudgetAdvanceBean.getBudgetId());
			throw loAppEx;
		}
		return liRowsInserted;
	}

	/**
	 * This method is called from method : fetchAdvanceNumber to generate an
	 * auto-generated advance number.
	 * <ul>
	 * <li>1)It takes aiAdvanceCount (number of advances requested for the
	 * selected budget for the current month) and asFiscalYear(budget
	 * fiscalyear) as input.</li>
	 * <li>2)The String Builder object lsAdvanceNumber is created which is used
	 * to generate an autogenerated Advance Number.</li>
	 * <li>3)The resulting String Builder object is converted to string and
	 * returned to method : fetchAdvanceNumber.</li>
	 * 
	 * @param aiAdvanceCount
	 * @param asFiscalYear
	 * @return lsAdvanceNumber.toString()
	 */
	private String generateAdvanceNumber(Integer aiAdvanceCount)
	{
		StringBuilder lsAdvanceNumber = new StringBuilder();
		int liCurrentMonth = 0;
		Calendar loCalendar = Calendar.getInstance();
		liCurrentMonth = loCalendar.get(Calendar.MONTH) + 1;
		int liCurrentFiscalYear = loCalendar.get(Calendar.YEAR);
		if (liCurrentMonth >= 7)
		{
			liCurrentFiscalYear = liCurrentFiscalYear + 1;
		}
		lsAdvanceNumber.append(String.valueOf(liCurrentFiscalYear).substring(2));
		lsAdvanceNumber.append(String.format(HHSConstants.FIN_BDG_SR_02D, liCurrentMonth));
		lsAdvanceNumber.append(String.format(HHSConstants.FIN_BDG_SR_05D, aiAdvanceCount));
		return lsAdvanceNumber.toString();
	}

	/**
	 * This method retrieves Next Sequence from Budget Advance table <li>This
	 * query used: getBudgetAdvanceNextSeq</li>
	 * @param aoMyBatisSession to connect to database
	 * @return Integer liNextSeq next sequence from the organization table
	 * @throws ApplicationException if an application exception occurs
	 */
	public Integer getBudgetAdvanceNextSeq(SqlSession aoMyBatisSession) throws ApplicationException
	{
		int liNextSeq;
		try
		{
			liNextSeq = (Integer) DAOUtil.masterDAO(aoMyBatisSession, null, HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
					"getBudgetAdvanceNextSeq", null);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retreiving Current Sequence from Table", loAppEx);
			setMoState("Transaction Failed:: FinancialsBudgetService:getBudgetAdvanceNextSeq method -Exception occured while while retreiving Next "
					+ "Sequence from Table: /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: FinancialsBudgetService:getBudgetAdvanceNextSeq method - Next Sequence from Table have been retreived "
				+ "successfully: /n");
		return liNextSeq;
	}

	/**
	 * This method fetches Contract FY Budget Amount for the requested advance
	 * amount validation. It is part of Release 3.2.0 for enhancement 6262 <li>
	 * Query used: fetchContractAmountForValidation</li>
	 * @param aoMyBatisSession to connect to database
	 * @param aoBudgetAdvanceParam
	 * @return String loBudgetAmount
	 * @throws ApplicationException if an application exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public String fetchContractAmountForValidation(SqlSession aoMybatisSession, BudgetAdvanceBean aoBudgetAdvanceParam)
			throws ApplicationException
	{
		try
		{
			String loBudgetAmount = (String) DAOUtil.masterDAO(aoMybatisSession, aoBudgetAdvanceParam.getBudgetId(),
					HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.CBL_FETCH_CONTRACT_AMOUNT_FOR_VALIDATION,
					HHSConstants.JAVA_LANG_STRING);
			return loBudgetAmount;
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting the Budget amount for budget id:" + aoBudgetAdvanceParam.getBudgetId());
			throw loExp;
		}
		catch (Exception aoAppExp)
		{
			setMoState("Error while getting the Budget amount for budget id:" + aoBudgetAdvanceParam.getBudgetId());
			LOG_OBJECT.Error("Error while getting the Budget amount for budget id", aoAppExp);
			throw new ApplicationException("Error while getting the Budget amount for budget id", aoAppExp);
		}
	}
	
	/**
	 * This method added in R7.
	 * This method will fetch the approved modifications Budget list 
	 * when the user redirects to budget list from modification review task
	 * @param aoMybatisSession
	 * @param aoBudgetBean
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private List<BudgetList> fetchApprovedModificationsBudgets(SqlSession aoMybatisSession, BudgetList aoBudgetBean)
			throws ApplicationException
	{
		List<BudgetList> loBudgetList = null;
		loBudgetList = (List<BudgetList>) DAOUtil.masterDAO(aoMybatisSession, aoBudgetBean,
				HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSR5Constants.FETCH_BUDGET_LIST_FOR_MODIFICATIONS_BUDGET,
				HHSConstants.FIN_BDG_SR_COM_NYC_HHS_MODEL_BUDGET_LIST);
		
		return loBudgetList;
	}
	
	/**
	 * This method added in R7.
	 * This method will fetch the approved modifications budget count 
	 * when the user redirect to budget list from modification review task.
	 * @param aoMybatisSession
	 * @param aoBudgetBean
	 * @return
	 * @throws ApplicationException
	 */
	private Integer fetchApprovedModificationsBudgetsCount(SqlSession aoMybatisSession, BudgetList aoBudgetBean)
			throws ApplicationException
	{
		Integer liBudgetListCount = HHSConstants.INT_ZERO;
		liBudgetListCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoBudgetBean,
				HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSR5Constants.FETCH_MODIFICATION_BUDGET_LIST_COUNT,
				HHSConstants.FIN_BDG_SR_COM_NYC_HHS_MODEL_BUDGET_LIST);
		return liBudgetListCount;
	}
	
	/* Start QC 9438 R 8.2  return number of Amendments in Processing Status with budget that not approved yet for particular Fiscal Year */
	/**
	 * This method is for getting the count of  Amendments in Processing Status with budhet 
	 * that not approved yet for particular Fyscal Year
	 * <ul>
	 * <li>calls query 'fetchCountractBudgetCountForFY'</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession's object
	 * @param asContractId - String type object
	 * @param asFiscalYearId - String type object
	 * @return loCount - Integer object
	 * @throws ApplicationException
	 */
	public Integer getNegativeAmendmentCountForFY(SqlSession aoMybatisSession, String asContractId,
			String asFiscalYearId) throws ApplicationException
	{
		Integer negativeAmendCnt = 0;
		Map<String, String> loBudgetInfo = new HashMap<String, String>();
		try
		{
			// Set the parameters required in HashMap
			loBudgetInfo.put(HHSConstants.CONTRACT_ID1, asContractId);
			loBudgetInfo.put(HHSConstants.CLC_FISCAL_YEAR_ID, asFiscalYearId);

			negativeAmendCnt = (Integer) DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.GET_NEGATIVE_AMENDMENT_COUNT_FOR_FY,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			setMoState("Negative Amendment count for contract fetched successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting contract budget count in getNegativeAmendmentCountForFY() method :");
			throw loExp;
		}
		return negativeAmendCnt;
	}
	/* End QC 9438 R 8.2   return number of Amendments in Processing Status with budget that not approved yet for particular Fiscal Year */
	
	/* Start QC 9490 R 8.4.0  return Count of Budget Updates in Approved/Active Status  */
	/**
	 * This method added in R 8.4.
	 * This method will count the approved budget updates
	 * @param aoMybatisSession
	 * @param aoContractId
	 * @return count
	 * @throws ApplicationException
	 */
	public Integer countBudgetUpdateApproved(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		Integer count = HHSConstants.INT_ZERO;
		
		try
		{
			count = (Integer) DAOUtil.masterDAO(aoMybatisSession, asContractId,
				HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSR5Constants.COUNT_BUDGET_UPDATE_APPROVED,
				HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting budget update count in countBudgetUpdateApproved() method :: ");
			throw loExp;
		}
		return count;
	}
	/* End QC 9490 R 8.4.0  return Count of Budget Updates in Approved/Active Status  */
}