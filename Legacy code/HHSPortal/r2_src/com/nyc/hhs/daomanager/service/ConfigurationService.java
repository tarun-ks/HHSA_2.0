package com.nyc.hhs.daomanager.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.controllers.RFPReleaseController;
import com.nyc.hhs.controllers.util.BaseControllerUtil;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.AccountsAllocationBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractBean;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.ContractCOFDetails;
import com.nyc.hhs.model.ContractFinancialBean;
import com.nyc.hhs.model.CostCenterServicesMappingList;
import com.nyc.hhs.model.EPinDetailBean;
import com.nyc.hhs.model.FundingAllocationBean;
import com.nyc.hhs.model.MasterStatusBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.model.RefContractFMSBean;
import com.nyc.hhs.model.SiteDetailsBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.db.services.notification.NotificationService;
import com.nyc.hhs.service.filenetmanager.p8services.P8ProcessServiceForSolicitationFinancials;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * <p>
 * ConfigurationService: This service class is used to perform all Configuration
 * related modules operations for retrieve, save and update in configuration
 * </p>
 */
public class ConfigurationService extends ServiceState
{

	/**
	 * Log object to record all logs
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(ConfigurationService.class);

	/**
	 * Gets Procurement Details related to Financials and Certification of Funds
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>Procurement Title - procurementTitle</li>
	 * <li>Procurement Value - procurementValue</li>
	 * <li>Procurement Planned Start Date - contractStartDate</li>
	 * <li>Procurement Planned End Date - contractEndDate</li>
	 * <li>Certification of Funds Status - procurementStatus</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of procurementId, the above mentioned values are
	 * received from the DataBase by executing the
	 * <code>fetchProcurementCOFDetails</code> query in the procurementMapper</li>
	 * <li>It returns the values as ProcurementCOF Bean object</li>
	 * <li>The values returned are used in the <code>RFPReleaseController</code>
	 * which in turns helps to display the information on the financials.jsp</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @see RFPReleaseController
	 * @param aoMybatisSession SqlSession
	 * @param asProcurementId id on the basis of which Procurement details will
	 *            be fetched
	 * @return ProcurementCOF Bean
	 * @throws ApplicationException ApplicationException object
	 */
	public ProcurementCOF fetchProcurementDetails(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		/**
		 * This is the log object used to log errors into log file
		 */
		ProcurementCOF loProcurementCOF = null;
		String lsPCOFStatus;
		Map<String, String> loQueryMap = new HashMap<String, String>();
		loQueryMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loQueryMap.put(HHSConstants.TYPE, HHSConstants.TYPE_UPDATED);
		try
		{
			lsPCOFStatus = (String) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_FETCH_PROCUREMENT_COF_STATUS,
					HHSConstants.JAVA_LANG_STRING);
			loProcurementCOF = (ProcurementCOF) DAOUtil.masterDAO(aoMybatisSession, loQueryMap,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_FETCH_PROCUREMENT_DETAILS,
					HHSConstants.JAVA_UTIL_MAP);
			if (StringUtils.isEmpty(lsPCOFStatus))
			{
				loProcurementCOF.setProcurementStatus(HHSConstants.NOT_SUBMITTED);
			}
			else
			{
				loProcurementCOF.setProcurementStatus(lsPCOFStatus);
			}

			setMoState("ProcurementCOF details fetched successfully for ProcurementCOF Id:" + asProcurementId);
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting ProcurementCOF Details");
			throw loExp;
		}
		return loProcurementCOF;
	}

	/**
	 * Changed method - Reason: Build: 2.6.0 Enhancement id: 5653 added extra db
	 * call that sets latest dates as original dated when PCOF task is approved
	 * finally This method is used to set status for Procurement certification
	 * of funds screen in Procurement_Financials table.
	 * 
	 * *Query Id 'updateProcStatus'
	 * 
	 * Query Id 'updateStatusForProcurement'
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoTaskDetailsBean TaskDetailsBean
	 * @param aoFinalFinish Boolean
	 * @return lbStatus
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 * 
	 */
	public boolean procStatusSet(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean, Boolean aoFinalFinish)
			throws ApplicationException
	{
		boolean lbStatus = false;
		String lsProcurementId = aoTaskDetailsBean.getProcurementId();
		try
		{
			if (aoFinalFinish && aoTaskDetailsBean.getTaskStatus().equalsIgnoreCase(HHSConstants.STATUS_APPROVED))
			{
				DAOUtil.masterDAO(aoMybatisSession, lsProcurementId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_SET_PROCUREMENT_STATUS, HHSConstants.JAVA_LANG_STRING);
				DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_UPDATE_PROCUREMENT_COA_DETAILS_STATUS, HHSConstants.CS_TASK_DETAILS_BEAN);
				// extra db call is passed to set latest dates as original dated
				// when PCOF task is approved finally
				CBGridBean loCBGridBeanObj = new CBGridBean();
				loCBGridBeanObj.setProcurementID(lsProcurementId);
				DAOUtil.masterDAO(aoMybatisSession, loCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_SET_ORIGINAL_PROCUREMENT_VALUES, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				lbStatus = true;
			}
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Error while getting ProcurementCOF Details");
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException("Error while getting ProcurementCOF Details", aoExp);

			setMoState("Error while getting ProcurementCOF Details");
			throw loAppEx;
		}
		return lbStatus;
	}

	/**
	 * The method is updated for release 3.2.0 enhancement 5684. for creating
	 * replica of task rows as type approved This method is used to merge
	 * procurement financials and procurement funding table when procurement cof
	 * task is approved at final level
	 * 
	 * Query Id 'deleteRowsWithTypeUpdate' Query Id
	 * 'deleteProcFundRowsWithTypeUpdate' Query Id 'insertTaskRowsAsTypeUpdate'
	 * Query Id 'insertProcFundTaskRowsAsTypeUpdate' Query Id
	 * 'deleteRowsWithTypeTaskRows' Query Id
	 * 'deleteProcFundRowsWithTypeTaskRows'
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoTaskDetailsBean TaskDetailsBean
	 * @param aoFinalFinish Boolean
	 * @return lbStatus Boolean
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	public boolean mergeViewCofAndTaskRowsProcFinancials(SqlSession aoMybatisSession,
			TaskDetailsBean aoTaskDetailsBean, Boolean aoFinalFinish) throws ApplicationException
	{
		boolean lbStatus = false;
		try
		{
			if (aoFinalFinish && aoTaskDetailsBean.getTaskStatus().equalsIgnoreCase(HHSConstants.STATUS_APPROVED))
			{
				// hard delete rows with type Updated
				DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_DELETE_ROWS_WITH_TYPE_UPDATE, HHSConstants.CS_TASK_DETAILS_BEAN);

				DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_DELETE_PROC_FUND_ROWS_WITH_TYPE_UPDATE, HHSConstants.CS_TASK_DETAILS_BEAN);

				// hard delete rows with type Approved
				DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSR5Constants.DELETE_ROWS_WITH_TYPE_APPROVED, HHSConstants.CS_TASK_DETAILS_BEAN);

				DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSR5Constants.DELETE_PROC_FUND_ROWS_WITH_TYPE_APPROVED, HHSConstants.CS_TASK_DETAILS_BEAN);

				// insert replica of task rows as type updated
				DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_INSERT_TASK_ROWS_AS_TYPE_UPDATE, HHSConstants.CS_TASK_DETAILS_BEAN);

				DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_INSERT_PROC_FUND_TASK_ROWS_AS_TYPE_UPDATE, HHSConstants.CS_TASK_DETAILS_BEAN);

				// Start of changes for release 3.2.0 enhancement 5684
				// insert replica of task rows as type approved
				DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSR5Constants.INSERT_TASK_ROWS_AS_TYPE_APPROVED, HHSConstants.CS_TASK_DETAILS_BEAN);

				DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSR5Constants.INSERT_PROC_FUND_TASK_ROWS_AS_TYPE_APPROVED, HHSConstants.CS_TASK_DETAILS_BEAN);
				// End of changes for release 3.2.0 enhancement 5684

				// soft delete task rows
				DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_DELETE_ROWS_WITH_TYPE_TASKROWS, HHSConstants.CS_TASK_DETAILS_BEAN);

				DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_DELETE_PROC_FUND_ROWS_WITH_TYPE_TASKROWS, HHSConstants.CS_TASK_DETAILS_BEAN);

				lbStatus = true;
			}
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Error in mergeViewCofAndTaskRowsProcFinancials ");
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException("Error in mergeViewCofAndTaskRowsProcFinancials ",
					aoExp);

			setMoState("Error in mergeViewCofAndTaskRowsProcFinancials ");
			throw loAppEx;
		}
		return lbStatus;
	}

	/**
	 * 
	 * Changed method - By: Siddharth Bhola Reason: Build: 2.6.0 Enhancement id:
	 * 5653 This method is restructured again as below: This method is used to
	 * delete entries from procurement financials and funding table when
	 * contract start date and end dates are changed. When contract dates are
	 * changed such that it effects fiscal years change on procurement summary
	 * screen (S203) Then all the extra/discarded fiscal years are deleted from
	 * PROCUREMENT_FINANCIALS and PROCUREMENT_FIN_FUNDING_STREAM
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * <li>Query Id 'fetchProcurementAddendumCOFDetailsFinancials' is executed</li>
	 * <li>Query Id 'insertRowsCoaDatesChanged' is executed</li>
	 * <li>Query Id 'insertRowsFundingDatesChanged' is executed</li>
	 * <li>Query Id 'deleteRowsCoaDatesChanged' is executed</li>
	 * <li>Query Id 'deleteRowsFundingDatesChanged' is executed</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession Object
	 * @param aoProcurementBean Procurement Bean Object
	 * @return lbStatus Boolean Object
	 * @throws ApplicationException ApplicationException object
	 * 
	 */
	public Boolean delCoaFundingEntrieswhenContractDatesChanged(SqlSession aoMybatisSession,
			Procurement aoProcurementBean) throws ApplicationException
	{
		Boolean lbStatus = false;
		try
		{
			ProcurementCOF loProcurementCOFBean = null;
			String loTempStartDate = HHSConstants.EMPTY_STRING;
			String loTempEndDate = HHSConstants.EMPTY_STRING;
			loProcurementCOFBean = (ProcurementCOF) DAOUtil.masterDAO(aoMybatisSession,
					aoProcurementBean.getProcurementId(), HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.FETCH_PROC_DATES_PUBLISHED_AND_ADDENDUM, HHSConstants.JAVA_LANG_STRING);

			if (null != loProcurementCOFBean)
			{
				// check if addendum details are present
				if (null != loProcurementCOFBean.getUpdatedContractStartDate()
						&& !loProcurementCOFBean.getUpdatedContractStartDate().isEmpty()
						&& null != loProcurementCOFBean.getUpdatedContractEndDate()
						&& !loProcurementCOFBean.getUpdatedContractEndDate().isEmpty())
				{
					// below are updated dates in procurement_addendum table
					loTempStartDate = loProcurementCOFBean.getUpdatedContractStartDate();
					loTempEndDate = loProcurementCOFBean.getUpdatedContractEndDate();
				}
				else
				{
					// below are updated dates in procurement table
					loTempStartDate = loProcurementCOFBean.getContractStartDate();
					loTempEndDate = loProcurementCOFBean.getContractEndDate();
				}

				// equals is used as dates are in String data type
				if (!loTempStartDate.equalsIgnoreCase(aoProcurementBean.getContractStartDateUpdated())
						|| !loTempEndDate.equalsIgnoreCase(aoProcurementBean.getContractEndDateUpdated()))
				{
					Integer loPlndStartFY = null;
					Integer loPlndEndFY = null;
					Integer loUpdtdStartFY = null;
					Integer loUpdtdEndFY = null;

					Map loQueryMap = new HashMap<Object, Object>();
					// fetch fiscal year for planned dates
					BaseControllerUtil.getContractFiscalYearsUtil(loTempStartDate, loTempEndDate, loQueryMap);
					loPlndStartFY = (Integer) loQueryMap.get(HHSConstants.LI_START_YEAR);
					loPlndEndFY = (Integer) loQueryMap.get(HHSConstants.LI_END_YEAR);

					// fetch fiscal year for updated dates
					BaseControllerUtil.getContractFiscalYearsUtil(aoProcurementBean.getContractStartDateUpdated(),
							aoProcurementBean.getContractEndDateUpdated(), loQueryMap);
					loUpdtdStartFY = (Integer) loQueryMap.get(HHSConstants.LI_START_YEAR);
					loUpdtdEndFY = (Integer) loQueryMap.get(HHSConstants.LI_END_YEAR);

					loQueryMap.put(HHSConstants.PROCUREMENT_ID_KEY, aoProcurementBean.getProcurementId());
					loQueryMap.put(HHSConstants.TYPE, HHSConstants.STATUS_APPROVED);
					loQueryMap.put(HHSConstants.EXISTING_FY, loPlndStartFY);

					// Add new FY in set, start
					Set loPlndFYSet = new HashSet();
					Set loUpdtdFYSet = new HashSet();
					for (int i = loPlndStartFY; i <= loPlndEndFY; i++)
					{
						loPlndFYSet.add(i);
					}
					for (int i = loUpdtdStartFY; i <= loUpdtdEndFY; i++)
					{
						loUpdtdFYSet.add(i);
					}
					loUpdtdFYSet.removeAll(loPlndFYSet);
					// Add new FY in set, end

					loQueryMap.put(HHSConstants.NEWLY_ADDED_FY_SET, loUpdtdFYSet);
					if (null != loUpdtdFYSet && loUpdtdFYSet.size() > 0)
					{
						// Insert in PROCUREMENT_FINANCIALS,
						// PROCUREMENT_FIN_FUNDING_STREAM with type 'Approved'
						DAOUtil.masterDAO(aoMybatisSession, loQueryMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.CS_INSERT_ROWS_COA_DATES_CHANGED, HHSConstants.JAVA_UTIL_MAP);

						DAOUtil.masterDAO(aoMybatisSession, loQueryMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.CS_INSERT_ROWS_FUNDING_DATES_CHANGED, HHSConstants.JAVA_UTIL_MAP);

						// Insert in PROCUREMENT_FINANCIALS,
						// PROCUREMENT_FIN_FUNDING_STREAM with type 'TaskRows'
						loQueryMap.put(HHSConstants.TYPE, HHSConstants.TASK_ROWS);
						DAOUtil.masterDAO(aoMybatisSession, loQueryMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.CS_INSERT_ROWS_COA_DATES_CHANGED, HHSConstants.JAVA_UTIL_MAP);

						DAOUtil.masterDAO(aoMybatisSession, loQueryMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.CS_INSERT_ROWS_FUNDING_DATES_CHANGED, HHSConstants.JAVA_UTIL_MAP);
					}
					// hard delete rows from PROCUREMENT_FINANCIALS and
					// PROCUREMENT_FIN_FUNDING_STREAM with type
					// 'Approved' or 'TaskRows'
					DAOUtil.masterDAO(aoMybatisSession, loQueryMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.CS_DELETE_ROWS_COA_DATES_CHANGED, HHSConstants.JAVA_UTIL_MAP);

					DAOUtil.masterDAO(aoMybatisSession, loQueryMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.CS_DELETE_ROWS_FUNDING_DATES_CHANGED, HHSConstants.JAVA_UTIL_MAP);
				}
			}
			lbStatus = true;
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Error in delCoaFundingEntrieswhenContractDatesChanged");
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error in delCoaFundingEntrieswhenContractDatesChanged ", aoExp);

			setMoState("Error in delCoaFundingEntrieswhenContractDatesChanged ");
			throw loAppEx;
		}
		return lbStatus;
	}

	/**
	 * Gets account allocation details for a Procurement
	 * <ul>
	 * <p>
	 * Changed method - By: Siddharth Bhola Reason: Build 2.6.0 Enhancement id:
	 * 5653 This method is restructured again as below:
	 * 
	 * <ul>
	 * <li>
	 * This method details for chart of account grid in procurement financials
	 * based upon from where it is triggered:</li>
	 * <li>a. procurement financials screen</li>
	 * <li>b. view Cof button</li>
	 * <li>c. procurement certification of funds task screen</li>
	 * </ul>
	 * </p>
	 * Transaction id : <code>financialsAccountGridFetch</code></li>
	 * 
	 * @see BMCController
	 * @param aoCBGridBean CBGridBean
	 * @param aoMybatisSession SqlSession
	 * @return List<AccountsAllocationBean> loAccountsAllocationBeanRtrndList
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<AccountsAllocationBean> fetchPCOFCoADetails(CBGridBean aoCBGridBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		List<AccountsAllocationBean> loAccountsAllocationBeanList = null;
		List<AccountsAllocationBean> loAccountsAllocationBeanRtrndList = null;
		String lsPCOFStatus;
		try
		{
			lsPCOFStatus = fetchPCOFStatus(aoCBGridBean, aoMybatisSession);
			if (lsPCOFStatus == null)
			{
				lsPCOFStatus = HHSConstants.EMPTY_STRING;
			}
			aoCBGridBean.setType(HHSConstants.STATUS_APPROVED);

			if (aoCBGridBean.getCoaDocType())
			{
				aoCBGridBean.setType(HHSConstants.TYPE_UPDATED);
			}
			else if (aoCBGridBean.getIsProcCerTaskScreen())
			{
				aoCBGridBean.setType(HHSConstants.TASK_ROWS);
			}
			loAccountsAllocationBeanList = (List<AccountsAllocationBean>) DAOUtil.masterDAO(aoMybatisSession,
					aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_FETCH_PROCUREMENT_COA_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			loAccountsAllocationBeanRtrndList = createCoADetailsList(loAccountsAllocationBeanList,
					aoCBGridBean.getFiscalYearID());

			setMoState("Account Allocation Details for procurement fetched successfully:");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("aoCBGridBean", CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchPCOFCoADetails method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchPCOFCoADetails method - with aoCBGridBean::"
					+ aoCBGridBean + "\n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured in ConfigurationService: fetchPCOFCoADetails method::", loEx);
			loAppEx.addContextData("aoCBGridBean", CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchPCOFCoADetails method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchPCOFCoADetails method - with aoCBGridBean::"
					+ aoCBGridBean + "\n");
			throw loAppEx;
		}
		return loAccountsAllocationBeanRtrndList;
	}

	/**
	 * This method fethes the FundingSourceDetails details for a contract and it
	 * is called by Chart of Accounts JQGrid at Contract Configuration Task
	 * screen.
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>Federal Amount</li>
	 * <li>City Amount</li>
	 * <li>State Amount</li>
	 * <li>Other Amount</li>
	 * 
	 * These amounts are returned for all the fiscal years of contract duration.
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of contractId, the above mentioned values are received
	 * from the DataBase by executing the
	 * <code>fetchContractConfFundingDetails</code> query in the
	 * ConfigurationMapper</li>
	 * <li>It returns the values as List of FundingAllocationBean object</li>
	 * <li>List is iterated to check if it contains the data for all fiscal
	 * years.</li>
	 * <li>If there is no data for any fiscal year, then
	 * addContractConfFundingDetails query will be executed to insert a row in
	 * table with all values as 0.</li> so that it can be used by JQGrid to
	 * display the required information.</li>
	 * </ul>
	 * </li> </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoGridBean input bean on basis of which funding details will be
	 *            fetched
	 * @return List<FundingAllocationBean> list of type FundingAllocationBean
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<FundingAllocationBean> fetchContractConfFundingDetails(SqlSession aoMybatisSession,
			CBGridBean aoGridBean) throws ApplicationException
	{
		List<FundingAllocationBean> loFundingSourceDetails = null;
		try
		{
			loFundingSourceDetails = (List<FundingAllocationBean>) DAOUtil.masterDAO(aoMybatisSession,
					aoGridBean.getContractID(), HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_FETCH_CONTRACT_CONF_FUNDING_DETAILS, HHSConstants.JAVA_LANG_STRING);

			if (null == loFundingSourceDetails || loFundingSourceDetails.isEmpty())
			{
				insertDefaultFundingRows(aoMybatisSession, aoGridBean, loFundingSourceDetails);
				loFundingSourceDetails = (List<FundingAllocationBean>) DAOUtil.masterDAO(aoMybatisSession,
						aoGridBean.getContractID(), HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_FETCH_CONTRACT_CONF_FUNDING_DETAILS, HHSConstants.JAVA_LANG_STRING);
			}
			setMoState("Funding Source Allocation Details for contract fetched successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error in Configuration Service while getting contract configuration funding source details :");
			throw loExp;
		}
		catch (Exception aoAppExp)
		{
			setMoState("Error in Configuration Service while getting contract configuration funding source details");
			LOG_OBJECT.Error(
					"Error in Configuration Service while getting contract configuration funding source details",
					aoAppExp);
			throw new ApplicationException(
					"Error in Configuration Service while getting contract configuration funding source details",
					aoAppExp);
		}

		return loFundingSourceDetails;
	}

	/**
	 * <p>
	 * This is private method used to insert default entries with 0 values
	 * across all fiscal years in Federal, State, City, Other.
	 * </p>
	 * Query Id 'addContractConfFundingDetails'
	 * <ul>
	 * <li>Step executed are:</li>
	 * <li>For all fiscal years, check if data does not exist then insert row
	 * with 0 value</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param aoGridBean CBGridBean object
	 * @param aoFundingSourceDetails List of type <FundingAllocationBean>
	 * @throws Exception Exception class object
	 * 
	 * 
	 */
	private void insertDefaultFundingRows(SqlSession aoMybatisSession, CBGridBean aoGridBean,
			List<FundingAllocationBean> aoFundingSourceDetails) throws Exception
	{
		Integer loCounter = HHSConstants.INT_ONE;
		boolean lbFiscalYearDataExists = false;
		Integer loStartYear = Integer.parseInt(aoGridBean.getFiscalYearID());
		while (loCounter <= aoGridBean.getNoOfyears())
		{
			for (FundingAllocationBean loFABean : aoFundingSourceDetails)
			{
				if (loFABean.getFiscalYear().equalsIgnoreCase(loStartYear.toString()))
				{
					lbFiscalYearDataExists = true;
				}
			}
			if (!lbFiscalYearDataExists)
			{
				aoGridBean.setFiscalYearCounter(loStartYear);
				Integer loRowsAdded = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoGridBean,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_ADD_CONTRACT_CONFIG_DETAILS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				if (loRowsAdded > HHSConstants.INT_ZERO)
				{
					setMoState("Data inserted successfully for fiscal year:" + loStartYear + "for contract id"
							+ aoGridBean.getContractID());
				}
			}

			loCounter++;
			loStartYear++;
			lbFiscalYearDataExists = false;
		}
	}

	/**
	 * This method fethes the FundingSourceDetails details for an amendment and
	 * it is called by Chart of Accounts JQGrid at Contract Configuration Task
	 * screen.
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>Federal Amount</li>
	 * <li>City Amount</li>
	 * <li>State Amount</li>
	 * <li>Other Amount</li>
	 * 
	 * These amounts are returned for all the fiscal years of contract duration.
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of contractId, the above mentioned values are received
	 * from the DataBase by executing the
	 * <code>fetchContractConfFundingDetails</code> query in the
	 * ConfigurationMapper</li>
	 * <li>It returns the values as List of FundingAllocationBean object</li>
	 * <li>List is iterated to check if it contains the data for all fiscal
	 * years.</li>
	 * <li>If there is no data for any fiscal year, then
	 * addContractConfFundingDetails query will be executed to insert a row in
	 * table with all values as 0.</li> so that it can be used by JQGrid to
	 * display the required information.</li>
	 * </ul>
	 * </li> </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoGridBean input bean on basis of which funding details will be
	 *            fetched
	 * @return List<FundingAllocationBean>
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<FundingAllocationBean> fetchContractAmendmentFundingDetails(SqlSession aoMybatisSession,
			CBGridBean aoGridBean) throws ApplicationException
	{
		List<FundingAllocationBean> loFundingSourceDetails = null;

		try
		{
			loFundingSourceDetails = (List<FundingAllocationBean>) DAOUtil.masterDAO(aoMybatisSession, aoGridBean,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_FETCH_CONTRACT_AMENDMENT_FUNDING_DETAILS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			if (null == loFundingSourceDetails || loFundingSourceDetails.isEmpty())
			{
				insertDefaultFundingRowsAmendment(aoMybatisSession, aoGridBean, loFundingSourceDetails);
				loFundingSourceDetails = (List<FundingAllocationBean>) DAOUtil.masterDAO(aoMybatisSession, aoGridBean,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_FETCH_CONTRACT_AMENDMENT_FUNDING_DETAILS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			}
			setMoState("Amendment Funding Source Allocation Details for contract fetched successfully:");
		}
		// ApplicationException and exception are thrown while executing query
		catch (ApplicationException aoAppExp)
		{
			setMoState("Error in Configuration Service while getting "
					+ "contract configuration funding source details :");
			aoAppExp.addContextData("Error in Configuration Service while getting contract "
					+ "configuration funding source details ::", aoAppExp.toString());
			LOG_OBJECT.Error("Error in Configuration Service while getting "
					+ "contract configuration funding source details", aoAppExp);
			throw aoAppExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error in Configuration Service while getting contract configuration funding source details");
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: editContractConfUpdateSubBudgetDetails:: ", loExp);
			loAppEx.addContextData("Error in Configuration Service while getting contract "
					+ "configuration funding source details ::", loAppEx.toString());
			LOG_OBJECT.Error("Error in Configuration Service while getting "
					+ "contract configuration funding source details", loExp);
			throw new ApplicationException("Error in Configuration Service while "
					+ "getting contract configuration funding source details", loExp);
		}

		return loFundingSourceDetails;
	}

	/**
	 * <p>
	 * This is private method used to insert default entries with 0 values
	 * across all fiscal years in Federal, State, City, Other.
	 * </p>
	 * <ul>
	 * <li>Step executed are:</li>
	 * <li>For all fiscal years, check if data does not exist then insert row
	 * with 0 value</li>
	 * </ul>
	 * 
	 * Query Id 'addContractAmendmentFundingDetails'
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param aoGridBean CBGridBean object
	 * @param aoFundingSourceDetails List of type <FundingAllocationBean>
	 * @throws Exception Exception class object
	 * 
	 * 
	 * 
	 */
	private void insertDefaultFundingRowsAmendment(SqlSession aoMybatisSession, CBGridBean aoGridBean,
			List<FundingAllocationBean> aoFundingSourceDetails) throws Exception
	{
		Integer loCounter = HHSConstants.INT_ONE;
		boolean lbFiscalYearDataExists = false;
		Integer loStartYear = Integer.parseInt(aoGridBean.getFiscalYearID());
		while (loCounter <= aoGridBean.getNoOfyears())
		{
			for (FundingAllocationBean loFABean : aoFundingSourceDetails)
			{
				if (loFABean.getFiscalYear().equalsIgnoreCase(loStartYear.toString()))
				{
					lbFiscalYearDataExists = true;
				}
			}
			if (!lbFiscalYearDataExists)
			{
				aoGridBean.setFiscalYearCounter(loStartYear);
				Integer loRowsAdded = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoGridBean,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_ADD_CONTRACT_AMENDMENT_FUNDING_DETAILS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				if (loRowsAdded > HHSConstants.INT_ZERO)
				{
					setMoState("Data inserted successfully for fiscal year:" + loStartYear + "for contract id"
							+ aoGridBean.getContractID());
				}
			}
			loCounter++;
			loStartYear++;
			lbFiscalYearDataExists = false;
		}
	}

	/**
	 * <p>
	 * This is private method used to set write AccountsAllocationBean. This
	 * bean is used to render data on the screen S383
	 * </p>
	 * <ul>
	 * <li>Step executed are:</li>
	 * <li>1.Prepare method name of bean by appending aiFYNum to
	 * HHSConstants.SMALL_FY</li>
	 * <li>2.call setProperty method of BeanUtils</li>
	 * </ul>
	 * 
	 * @param asCoa chart of accounts
	 * @param asId unique id
	 * @param asRc return code
	 * @param asSuboc sub operation code
	 * @param asAmount amount
	 * @param aiFYNum financial year fy1..fyn(account allocation bean structure)
	 * @param aoAccAllocWriteBean AccountsAllocationBean object
	 * @param aoModDate modified date of chart of accounts
	 * @throws ApplicationException ApplicationException object
	 */
	private void setAccAllocWriteBean(String asCoa, String asId, String asRc, String asSuboc, String asAmount,
			int aiFYNum, AccountsAllocationBean aoAccAllocWriteBean, Date aoModDate, String asNewFYFiscalYearId)
			throws ApplicationException
	{
		if (asId.indexOf(HHSConstants.NEW_RECORD) != -1)
		{
			aoAccAllocWriteBean.setId(asCoa + HHSConstants.HYPHEN + asSuboc + HHSConstants.HYPHEN + asRc
					+ HHSConstants.NEW_RECORD);
		}
		else if (asId.indexOf(HHSConstants.CURRENT_REC) != -1)
		{
			aoAccAllocWriteBean.setId(asCoa + HHSConstants.HYPHEN + asSuboc + HHSConstants.HYPHEN + asRc
					+ HHSConstants.CURRENT_REC);
		}
		else
		{
			aoAccAllocWriteBean.setId(asCoa + HHSConstants.HYPHEN + asSuboc + HHSConstants.HYPHEN + asRc);
		}
		aoAccAllocWriteBean.setChartOfAccount(asCoa);
		aoAccAllocWriteBean.setSubOc(asSuboc);
		aoAccAllocWriteBean.setRc(asRc);
		aoAccAllocWriteBean.setCreatedDate(aoModDate);
		aoAccAllocWriteBean.setNewFYFiscalYearId(asNewFYFiscalYearId);
		setFYAmountInBean(aoAccAllocWriteBean, aiFYNum, asAmount);
	}

	/**
	 * <p>
	 * This is private method used to set property of bean via BeanUtils class
	 * It gets parameter as aoAccAllocWriteBean, aiFYNum, asAmount
	 * </p>
	 * <ul>
	 * <li>Step executed are:</li>
	 * <li>1.Prepare method name of bean by appending aiFYNum to
	 * HHSConstants.SMALL_FY</li>
	 * <li>2.call setProperty method of BeanUtils</li>
	 * </ul>
	 * 
	 * @param aoAccAllocWriteBean bean of type AccountsAllocationBean
	 * @param aiFYNum fiscal year number in bean fy1 to fyn
	 * @param asAmount amount to be set for fiscal year
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	private void setFYAmountInBean(AccountsAllocationBean aoAccAllocWriteBean, int aiFYNum, String asAmount)
			throws ApplicationException
	{
		String lsMethodName = HHSConstants.SMALL_FY + aiFYNum;
		try
		{
			BeanUtils.setProperty(aoAccAllocWriteBean, lsMethodName, asAmount);
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: setFYAmountInBean method:: ", loEx);

			LOG_OBJECT.Error("Error:: ConfigurationService: setFYAmountInBean method - Error occured::", loEx);
			throw loAppEx;
		}
	}

	/**
	 * Changed method - By: Siddharth Bhola Reason: Build: 2.6.0 Enhancement id:
	 * 5653 Corrected if else condition and message logging Gets fund allocation
	 * details for a Procurement
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>UoA Code - unitOfAppropriation</li>
	 * <li>Budget Code - budgetCode</li>
	 * <li>Object Code - objectCode</li>
	 * <li>Sub Object Code - subOc</li>
	 * <li>Reporting Category - rc</li>
	 * <li>Fiscal Year - fiscalYear</li>
	 * <li>Amount for fiscal year - total</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of procurementId, the above mentioned values are
	 * received from the DataBase by executing the <code>fetchCoADetails</code>
	 * query in the ConfigurationMapper</li>
	 * <li>It returns the values as AccountAllocationBean object</li>
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the financials.jsp</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * <li>
	 * Transaction id : <code>financialsFundingGridFetch</code></li>
	 * 
	 * Query Id 'fetchProcurementFundingSourceDetails'
	 * 
	 * Query Id 'fetchProcurementFundingSourceDetails'
	 * @see BMCController
	 * @param aoMybatisSession SqlSession
	 * @param aoCBGridBean CBGridBean
	 * @return AccountAllocationBean Bean
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<FundingAllocationBean> fetchPCOFFundingSourcesDetails(SqlSession aoMybatisSession,
			CBGridBean aoCBGridBean) throws ApplicationException
	{
		List<FundingAllocationBean> loFundAllocationBeanList = null;
		String lsPCOFStatus;
		aoCBGridBean.setType(HHSConstants.STATUS_APPROVED);
		try
		{
			lsPCOFStatus = fetchPCOFStatus(aoCBGridBean, aoMybatisSession);
			if (lsPCOFStatus == null)
			{
				lsPCOFStatus = HHSConstants.EMPTY_STRING;
			}
			if (aoCBGridBean.getIsProcCerTaskScreen())
			{
				aoCBGridBean.setType(HHSConstants.TASK_ROWS);
			}
			// Build 2.6.0, enchancement 5653, Simplified this check as if
			// request is from view cof button
			else if (aoCBGridBean.getCoaDocType())
			{
				aoCBGridBean.setType(HHSConstants.TYPE_UPDATED);
			}

			loFundAllocationBeanList = (List<FundingAllocationBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_FETCH_PROCUREMENT_FUNDING_DETAILS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			if (null == loFundAllocationBeanList || loFundAllocationBeanList.isEmpty())
			{
				insertDefaultFundingRowsProcurement(aoMybatisSession, aoCBGridBean, loFundAllocationBeanList);
				loFundAllocationBeanList = (List<FundingAllocationBean>) DAOUtil.masterDAO(aoMybatisSession,
						aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_FETCH_PROCUREMENT_FUNDING_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			}

			setMoState("Procurement funding details fetched successfully");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("aoCBGridBean", CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchPCOFFundingSourcesDetails:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchPCOFFundingSourcesDetails method - with aoCBGridBean::"
					+ aoCBGridBean + "\n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured in ConfigurationService: fetchPCOFFundingSourcesDetails method::", loEx);
			loAppEx.addContextData("aoCBGridBean", CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchPCOFFundingSourcesDetails method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchPCOFFundingSourcesDetails method - with aoCBGridBean::"
					+ aoCBGridBean + "\n");
			throw loAppEx;
		}
		return loFundAllocationBeanList;
	}

	/**
	 * <p>
	 * This is private method used to insert default entries with 0 values
	 * across all fiscal years in Federal, State, City, Other.
	 * </p>
	 * <ul>
	 * <li>Step executed are:</li>
	 * <li>For all fiscal years, check if data does not exist then insert row
	 * with 0 value</li>
	 * </ul>
	 * Query Id 'fetchProcurementFundingSourceDetails'
	 * @param aoMybatisSession SqlSession object
	 * @param aoGridBean CBGridBean object
	 * @param aoFundingSourceDetails List of type <FundingAllocationBean>
	 * @throws Exception Exception class object
	 * 
	 * 
	 */
	private void insertDefaultFundingRowsProcurement(SqlSession aoMybatisSession, CBGridBean aoCBGridBean,
			List<FundingAllocationBean> aoFundAllocationBean) throws ApplicationException
	{
		Integer loCounter = HHSConstants.INT_ONE;
		boolean lbFiscalYearDataExists = false;
		Integer loStartYear = Integer.parseInt(aoCBGridBean.getFiscalYearID());
		while (loCounter <= aoCBGridBean.getNoOfyears())
		{
			for (FundingAllocationBean loFABean : aoFundAllocationBean)
			{
				if (loFABean.getFiscalYear().equalsIgnoreCase(loStartYear.toString()))
				{
					lbFiscalYearDataExists = true;
				}

			}
			if (!lbFiscalYearDataExists)
			{
				aoCBGridBean.setFiscalYearCounter(loStartYear);
				DAOUtil.masterDAO(aoMybatisSession, aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_INSERT_PROCUREMENT_FUNDING_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			}
			loCounter++;
			loStartYear++;
			lbFiscalYearDataExists = false;
		}
	}

	/**
	 * This method is used to edit the Funding Source Allocation details
	 * (amount)in the DataBase for any financial Year using
	 * <code>editContractConfFundingDetails</code> Update query</li> <li>
	 * Transaction id : <code>contractConfigurationFundingGridEdit</code></li>
	 * Query Id 'editContractConfFundingDetails'
	 * @param aoMybatisSession SqlSession
	 * @param aoEditedRow FundingAllocationBean
	 * @param aoGridBean CBGridBean
	 * @return boolean
	 * @throws ApplicationException ApplicationException object
	 * @throws NoSuchMethodException NoSuchMethodException object
	 * @throws InvocationTargetException InvocationTargetException object
	 * @throws IllegalAccessException IllegalAccessException object
	 * 
	 * 
	 */
	public Boolean editContractConfFundingDetails(SqlSession aoMybatisSession, FundingAllocationBean aoEditedRow,
			CBGridBean aoGridBean) throws ApplicationException
	{
		boolean lbResultStatus = false;
		try
		{
			Integer loRowsUpdated = HHSConstants.INT_ZERO;
			Integer loStartYear = Integer.parseInt(aoGridBean.getFiscalYearID());
			int liCounter = HHSConstants.INT_ONE;
			Map<String, String> loSetClause = new HashMap<String, String>();

			while (liCounter <= aoGridBean.getNoOfyears())
			{
				StringBuffer lsQuerySetClause = new StringBuffer();
				if (aoEditedRow.getFundingSource().equalsIgnoreCase(HHSConstants.FEDERAL))
				{
					lsQuerySetClause.append(HHSConstants.CS_FEDERAL_AMOUNT_STRING);
				}
				else if (aoEditedRow.getFundingSource().equalsIgnoreCase(HHSConstants.STATE))
				{
					lsQuerySetClause.append(HHSConstants.CS_STATE_AMOUNT_STRING);
				}
				else if (aoEditedRow.getFundingSource().equalsIgnoreCase(HHSConstants.CITY))
				{
					lsQuerySetClause.append(HHSConstants.CS_CITY_AMOUNT_STRING);
				}
				else if (aoEditedRow.getFundingSource().equalsIgnoreCase(HHSConstants.OTHER))
				{
					lsQuerySetClause.append(HHSConstants.CS_OTHER_AMOUNT_STRING);
				}

				String lsFYear = HHSConstants.SMALL_FY + liCounter;
				String lsAmount = BeanUtils.getProperty(aoEditedRow, lsFYear);
				loSetClause.put(HHSConstants.CS_AMOUNT, lsAmount);
				lsQuerySetClause.append(loSetClause.get(HHSConstants.CS_AMOUNT)).append(HHSConstants.STR);
				lsQuerySetClause.append(HHSConstants.CS_CONTRACT_WHERE_CLAUSE).append(aoGridBean.getContractID())
						.append(HHSConstants.STR);
				lsQuerySetClause.append(HHSConstants.CS_FISCAL_YEAR_WHERE_CLAUSE).append(loStartYear.toString())
						.append(HHSConstants.STR);
				loSetClause.put(HHSConstants.CS_QUERY_CLAUSE, lsQuerySetClause.toString());
				loRowsUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, loSetClause,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_EDIT_CONTRACT_CONF_FUNDING_DETAILS, HHSConstants.JAVA_UTIL_HASH_MAP);
				loSetClause.remove(HHSConstants.CS_QUERY_CLAUSE);
				loStartYear++;
				liCounter++;
			}
			if (loRowsUpdated > HHSConstants.INT_ZERO)
			{
				lbResultStatus = true;
			}
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while updating funding source contract configuration details:");
			throw loExp;
		}
		catch (Exception aoAppExp)
		{
			setMoState("Error while updating funding source contract configuration details:");
			LOG_OBJECT.Error("Error while updating funding source contract configuration details:", aoAppExp);
			throw new ApplicationException("Error while updating funding source contract configuration details:",
					aoAppExp);
		}
		return lbResultStatus;
	}

	/**
	 * This method is used to edit the Funding Source Allocation details
	 * (amount)in the DataBase for any financial Year using
	 * <code>editContractConfFundingDetails</code> Update query</li> <li>
	 * Transaction id : <code>contractConfigurationFundingGridEdit</code></li>
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession Object
	 * @param aoEditedRow FundingAllocationBean Object
	 * @param aoGridBean CBGridBean
	 * @return lbUpdateStatus boolean
	 * @throws ApplicationException ApplicationException object
	 * 
	 */
	public Boolean editContractAmendmentFundingDetails(SqlSession aoMybatisSession, FundingAllocationBean aoEditedRow,
			CBGridBean aoGridBean) throws ApplicationException
	{
		boolean lbUpdateStatus = Boolean.TRUE;
		try
		{
			Integer loRowsUpdated = HHSConstants.INT_ZERO;
			Integer loStartYear = Integer.parseInt(aoGridBean.getFiscalYearID());
			int liCounter = HHSConstants.INT_ONE;
			Map<String, String> loSetClause = new HashMap<String, String>();

			while (liCounter <= aoGridBean.getNoOfyears())
			{
				StringBuffer lsQuerySetClause = new StringBuffer();
				if (aoEditedRow.getFundingSource().equalsIgnoreCase(HHSConstants.FEDERAL))
				{
					lsQuerySetClause.append(HHSConstants.CS_FEDERAL_AMOUNT_STRING);
				}
				else if (aoEditedRow.getFundingSource().equalsIgnoreCase(HHSConstants.STATE))
				{
					lsQuerySetClause.append(HHSConstants.CS_STATE_AMOUNT_STRING);
				}
				else if (aoEditedRow.getFundingSource().equalsIgnoreCase(HHSConstants.CITY))
				{
					lsQuerySetClause.append(HHSConstants.CS_CITY_AMOUNT_STRING);
				}
				else if (aoEditedRow.getFundingSource().equalsIgnoreCase(HHSConstants.OTHER))
				{
					lsQuerySetClause.append(HHSConstants.CS_OTHER_AMOUNT_STRING);
				}

				String lsFYear = HHSConstants.SMALL_FY + liCounter;
				String lsAmount = BeanUtils.getProperty(aoEditedRow, lsFYear);
				loSetClause.put(HHSConstants.CS_AMOUNT, lsAmount);
				lsQuerySetClause.append(loSetClause.get(HHSConstants.CS_AMOUNT)).append(HHSConstants.STR);
				lsQuerySetClause.append(HHSConstants.CS_CONTRACT_AMENDMENT_WHERE_CLAUSE)
						.append(aoGridBean.getAmendmentContractID()).append(HHSConstants.STR);
				lsQuerySetClause.append(HHSConstants.CS_FISCAL_YEAR_WHERE_CLAUSE).append(loStartYear.toString())
						.append(HHSConstants.STR);
				loSetClause.put(HHSConstants.CS_QUERY_CLAUSE, lsQuerySetClause.toString());
				loRowsUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, loSetClause,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_EDIT_CONTRACT_AMENDMENT_FUNDING_DETAILS, HHSConstants.JAVA_UTIL_HASH_MAP);
				loSetClause.remove(HHSConstants.CS_QUERY_CLAUSE);
				loStartYear++;
				liCounter++;
			}

			if (loRowsUpdated > HHSConstants.INT_ZERO)
			{
				return lbUpdateStatus;
			}
			else
			{
				return !lbUpdateStatus;
			}
		}
		// ApplicationException is thrown while executing the query
		// Exception is thrown while parsing the string to integer and
		// getting property from BeanUtil
		catch (ApplicationException loAppEx)
		{
			setMoState("Error while updating funding source contract configuration details:");
			loAppEx.addContextData("Error while updating funding source contract configuration details:::",
					loAppEx.toString());
			loAppEx.addContextData("Error while updating funding source contract configuration details:::",
					loAppEx.toString());
			LOG_OBJECT.Error("Error while updating funding source contract configuration details:", loAppEx);
			throw loAppEx;
		}
		catch (Exception aoAppExp)
		{
			ApplicationException loAppEx = new ApplicationException("Error while updating funding"
					+ " source contract configuration details::: ", aoAppExp);
			setMoState("Error while updating funding source contract configuration details:");
			loAppEx.addContextData("Error while updating funding" + " source contract configuration details:::",
					loAppEx.toString());
			LOG_OBJECT.Error("Error while updating funding source contract configuration details:", aoAppExp);
			throw new ApplicationException("Error while updating funding source contract configuration details:",
					aoAppExp);
		}
	}

	/**
	 * This method is used to insert the Chart of Accounts details in the
	 * DataBase.
	 * 
	 * <ul>
	 * <li>A list of fields are sent to the DataBase using <code>****</code>
	 * insert query</li>
	 * <li>The fields inserted in the table are -</li>
	 * <ul>
	 * <li>UNIT_OF_APPROPRIATION</li>
	 * <li>BUDGET_CODE</li>
	 * <li>OBJECT_CODE</li>
	 * <li>SUB_OBJECT_CODE</li>
	 * <li>REPORTING_CATEGORY</li>
	 * </ul>
	 * </ul>
	 * 
	 * <li>
	 * Transaction id : <code>financialsAccountGridAdd</code></li> Query Id
	 * 'insertProcurementCoADetails'
	 * 
	 * Query Id 'insertProcurementCoADetails'
	 * @param aoMybatisSession SqlSession
	 * @param aoCoABean AccountsAllocationBean
	 * @throws ApplicationException ApplicationException Object
	 * 
	 * 
	 */
	public void insertCoADetails(SqlSession aoMybatisSession, AccountsAllocationBean aoCoABean)
			throws ApplicationException
	{
		int liStartFY = Integer.parseInt(aoCoABean.getContractStartFY());
		int liEndFY = Integer.parseInt(aoCoABean.getContractEndFY());
		int liCounter = HHSConstants.INT_ONE;
		String lsPCOFStatus;
		try
		{
			lsPCOFStatus = fetchPCOFStatus(aoCoABean, aoMybatisSession);
			if (lsPCOFStatus == null)
			{
				lsPCOFStatus = HHSConstants.EMPTY_STRING;
			}
			for (int liCount = liStartFY; liCount <= liEndFY; liCount++)
			{
				String lsBeanFY = HHSConstants.SMALL_FY + liCounter;
				aoCoABean.setActiveFlag(HHSConstants.ONE);

				aoCoABean.setFiscalYearID(String.valueOf(liCount));
				String lsAmt = (String) BeanUtils.getProperty(aoCoABean, lsBeanFY);
				aoCoABean.setAmmount(lsAmt);
				if (aoCoABean.getCoaDocType() == false)
				{
					// corrected syntax as part of build 2.6.0, defect 5653
					// Start of changes for release 3.2.0 enhancement 5684
					if (!aoCoABean.getIsProcCerTaskScreen())
					{
						aoCoABean.setType(HHSConstants.STATUS_APPROVED);
						// PCOF status set to 'approved' for approved rows
						aoCoABean.setDelStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PCOF_APPROVED));
						DAOUtil.masterDAO(aoMybatisSession, aoCoABean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.CS_INSERT_PROCUREMENT_COA_DETAILS, HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
					}
					else if (aoCoABean.getIsProcCerTaskScreen())
					{
						// PCOF status set to 'in_review' for task rows
						aoCoABean.setDelStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PCOF_IN_REVIEW));
						aoCoABean.setType(HHSConstants.TASK_ROWS);
						DAOUtil.masterDAO(aoMybatisSession, aoCoABean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.CS_INSERT_PROCUREMENT_COA_DETAILS, HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
					}
					// End of changes for release 3.2.0 enhancement 5684
				}
				liCounter++;
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while inserting Chart of accounts details", aoAppEx);
			ApplicationException loAppEx = new ApplicationException(
					"Error occured in Configuration service: insertCoADetails method:: ", aoAppEx);
			throw loAppEx;

		}
		catch (Exception aoEx)
		{
			ApplicationException loEx = new ApplicationException(
					"Error occured in Configuration service: insertCoADetails method:: ", aoEx);
			LOG_OBJECT.Error("Exception occured while inserting Chart of accounts details", loEx);
			throw loEx;
		}

		setMoState("Chart Of Accounts details inserted successfully");
	}

	/**
	 * This method updates the Procurement Chart of Accounts details in the
	 * PROCUREMENT_FINANCIALS table in the DataBase
	 * 
	 * <li>
	 * Transaction id : <code>financialsAccountGridEdit</code></li> Query Id
	 * 'updateProcurementCoADetails'
	 * 
	 * Query Id 'updateProcurementCoADetails'
	 * 
	 * Query Id 'updateProcurementCoADetails'
	 * @param aoMybatisSession SqlSession
	 * @param aoCoABean AccountsAllocationBean
	 * @throws ApplicationException ApplicationException Object
	 * 
	 * 
	 * 
	 */
	public void updateCoADetails(SqlSession aoMybatisSession, AccountsAllocationBean aoCoABean)
			throws ApplicationException
	{
		int liStartFY = Integer.parseInt(aoCoABean.getContractStartFY());
		int liEndFY = Integer.parseInt(aoCoABean.getContractEndFY());
		int liCounter = HHSConstants.INT_ONE;
		String lsPCOFStatus;
		for (int liCount = liStartFY; liCount <= liEndFY; liCount++)
		{
			String lsBeanFY = HHSConstants.SMALL_FY + liCounter;

			try
			{
				aoCoABean.setFiscalYearID(String.valueOf(liCount));

				String lsAmt = (String) BeanUtils.getProperty(aoCoABean, lsBeanFY);
				aoCoABean.setAmmount(lsAmt);
				lsPCOFStatus = fetchPCOFStatus(aoCoABean, aoMybatisSession);
				if (lsPCOFStatus == null)
				{
					lsPCOFStatus = HHSConstants.EMPTY_STRING;
				}
				// corrected syntax as part of build 2.6.0, defect 5653
				if (!aoCoABean.getCoaDocType())
				{
					if (lsPCOFStatus != null && !aoCoABean.getIsProcCerTaskScreen()
							&& lsPCOFStatus.equalsIgnoreCase(HHSConstants.STATUS_APPROVED))
					{
						aoCoABean.setType(HHSConstants.STATUS_APPROVED);
						DAOUtil.masterDAO(aoMybatisSession, aoCoABean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.CS_UPDATE_PROCUREMENT_COA_DETAILS, HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
					}
					else if (!aoCoABean.getIsProcCerTaskScreen()
							&& !lsPCOFStatus.equalsIgnoreCase(HHSConstants.STATUS_APPROVED))
					{
						aoCoABean.setType(HHSConstants.STATUS_APPROVED);
						DAOUtil.masterDAO(aoMybatisSession, aoCoABean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.CS_UPDATE_PROCUREMENT_COA_DETAILS, HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
					}
					else if (aoCoABean.getIsProcCerTaskScreen())
					{
						aoCoABean.setType(HHSConstants.TASK_ROWS);
						DAOUtil.masterDAO(aoMybatisSession, aoCoABean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.CS_UPDATE_PROCUREMENT_COA_DETAILS, HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
					}
				}
				liCounter++;
			}
			catch (Exception aoExp)
			{
				LOG_OBJECT.Error("Exception occured while updateing CoADetails", aoExp);
				throw new ApplicationException("Error Occured while updateing CoADetails", aoExp);

			}
		}
	}

	/**
	 * This method is used for deleting the Procurement Chart of Accounts
	 * Details against Procurement ID
	 * 
	 * <li>
	 * Transaction id : <code>financialsAccountGridDel</code></li> Query Id
	 * 'deleteProcurementCoADetails' Query Id 'deleteProcurementCoADetails'
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoCoABean AccountsAllocationBean
	 * @throws ApplicationException ApplicationException Object
	 * 
	 * 
	 */
	public void deleteProcurementCoADetails(SqlSession aoMybatisSession, AccountsAllocationBean aoCoABean)
			throws ApplicationException
	{
		try
		{
			if (aoCoABean.getIsProcCerTaskScreen())
			{
				aoCoABean.setType(HHSConstants.TASK_ROWS);
				DAOUtil.masterDAO(aoMybatisSession, aoCoABean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_DELETE_PROCUREMENT_COA_DETAILS, HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
			}
			else
			{
				aoCoABean.setType(HHSConstants.STATUS_APPROVED);
				DAOUtil.masterDAO(aoMybatisSession, aoCoABean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_DELETE_PROCUREMENT_COA_DETAILS, HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
			}
			setMoState("Account Allocation Details for contract deleted successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while deleting procurement COA details :");
			throw loExp;
		}
	}

	/**
	 * This method updates the Procurement Funding Source details in the
	 * PROCUREMENT_FINANCIALS table in the DataBase
	 * 
	 * <li>
	 * Transaction id : <code>financialsFundingGridEdit</code></li> Query Id
	 * 'updateProcurementFundingSourceDetails'
	 * @param aoMybatisSession SqlSession
	 * @param aoEditedRow FundingAllocationBean
	 * @param aoGridBean CBGridBean
	 * @return boolean
	 * @throws ApplicationException ApplicationException object
	 * @throws NoSuchMethodException NoSuchMethodException object
	 * @throws InvocationTargetException InvocationTargetException object
	 * @throws IllegalAccessException IllegalAccessException object
	 * 
	 * 
	 */
	public Boolean updateFundingSourcesDetails(SqlSession aoMybatisSession, FundingAllocationBean aoEditedRow,
			CBGridBean aoGridBean) throws ApplicationException
	{

		Integer loStartYear = Integer.parseInt(aoGridBean.getFiscalYearID());
		int liCounter = HHSConstants.INT_ONE;
		Map<String, String> loSetClause = new HashMap<String, String>();
		String lsPCOFStatus;
		try
		{
			while (liCounter <= aoGridBean.getNoOfyears())
			{
				StringBuffer lsQuerySetClause = new StringBuffer();
				lsPCOFStatus = fetchPCOFStatus(aoGridBean, aoMybatisSession);
				if (lsPCOFStatus == null)
				{
					lsPCOFStatus = HHSConstants.EMPTY_STRING;
				}
				if (aoEditedRow.getFundingSource().equalsIgnoreCase(HHSConstants.FEDERAL))
				{
					lsQuerySetClause.append(HHSConstants.CS_FEDERAL_AMOUNT_STRING);
				}
				else if (aoEditedRow.getFundingSource().equalsIgnoreCase(HHSConstants.STATE))
				{
					lsQuerySetClause.append(HHSConstants.CS_STATE_AMOUNT_STRING);
				}
				else if (aoEditedRow.getFundingSource().equalsIgnoreCase(HHSConstants.CITY))
				{
					lsQuerySetClause.append(HHSConstants.CS_CITY_AMOUNT_STRING);
				}
				else if (aoEditedRow.getFundingSource().equalsIgnoreCase(HHSConstants.OTHER))
				{
					lsQuerySetClause.append(HHSConstants.CS_OTHER_AMOUNT_STRING);
				}

				String lsFYear = HHSConstants.SMALL_FY + liCounter;
				String lsAmount = BeanUtils.getProperty(aoEditedRow, lsFYear);
				loSetClause.put(HHSConstants.CS_AMOUNT, lsAmount);
				lsQuerySetClause.append(loSetClause.get(HHSConstants.CS_AMOUNT)).append(HHSConstants.STR);

				lsQuerySetClause.append(HHSConstants.CS_PROCUREMENT_WHERE_CLAUSE).append(aoGridBean.getProcurementID())
						.append(HHSConstants.STR);

				lsQuerySetClause.append(HHSConstants.CS_FISCAL_YEAR_WHERE_CLAUSE).append(loStartYear.toString())
						.append(HHSConstants.STR);
				if (aoGridBean.getIsProcCerTaskScreen())
				{
					lsQuerySetClause.append("and type ='").append(HHSConstants.TASK_ROWS).append(HHSConstants.STR);
				}
				else
				{
					lsQuerySetClause.append("and type ='").append(HHSConstants.STATUS_APPROVED)
							.append(HHSConstants.STR);
				}

				loSetClause.put(HHSConstants.CS_QUERY_CLAUSE, lsQuerySetClause.toString());
				// changes for R5 starts
				loSetClause.put(HHSConstants.PROCUREMENT_ID, aoGridBean.getProcurementID());
				// changes for R5 ends
				DAOUtil.masterDAO(aoMybatisSession, loSetClause, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_UPDATE_PROCUREMENT_FUNDING_DETAILS, HHSConstants.JAVA_UTIL_HASH_MAP);
				loSetClause.remove(HHSConstants.CS_QUERY_CLAUSE);
				loStartYear++;
				liCounter++;
			}
		}
		catch (ApplicationException loAppEx)
		{
			setMoState("Error while updating procurement COA details :");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			setMoState("Exception while executing updateFundingSourcesDetails method ");
			throw new ApplicationException("Exception while executing updateFundingSourcesDetails method", loEx);

		}

		return true;

	}

	/*---Screen S 204 ends---*/

	/**
	 * This method will be called after amendment certification of fund document
	 * has been successfully generated. <li>Release 3.6.0 Enhancement id 6484</li>
	 * Query Id 'updateAmendmentContractStatus'
	 * 
	 * Query Id 'updateAmendmentBudgetStatus'
	 * 
	 * @param aoMybatisSession aoMybatisSession
	 * @param aoUserSession P8UserSession
	 * @param aoFinishTaskStatus Boolean
	 * @param aoTaskDetailsBean TaskDetailsBean
	 * @return lbProcessFlag Finish Task Status
	 * @throws ApplicationException ApplicationException object
	 * 
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Boolean processContractAfterAmendmentCofTask(SqlSession aoMybatisSession, P8UserSession aoUserSession,
			Boolean aoFinishTaskStatus, TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Boolean lbProcessFlag = Boolean.FALSE;		
		HashMap loHashMap = new HashMap();
		BigDecimal contractAmount = null;
		EPinDetailBean loContractDetailByContract = null;
		Integer liContractFinancialsMaxYear =null;
		int liEndYear=0;
		
		if (aoFinishTaskStatus)
		{
			try
			{
				//Start R8.4.0 qc_8537 $0 Amendments do not appear in Amendment CoF task list after configuration				
				loContractDetailByContract = (EPinDetailBean) DAOUtil.masterDAO(aoMybatisSession,
						aoTaskDetailsBean.getContractId(), HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.FIND_CONTRACT_AMEND_INFO, HHSConstants.JAVA_LANG_STRING);
				if(loContractDetailByContract!=null && loContractDetailByContract.getContractAmount()!=null){				
					contractAmount =new BigDecimal (loContractDetailByContract.getContractAmount());
				}
				
				
				if(contractAmount!=null && contractAmount.compareTo(BigDecimal.ZERO) == HHSConstants.INT_ZERO){
					loHashMap.put(ApplicationConstants.STATUS_ID_KEY, PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_CONTRACT_PENDING_REGISTARTION));
					loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
					loHashMap.put(HHSConstants.CONTRACT_ID_KEY, aoTaskDetailsBean.getContractId());
					//get base contract max year use base id (parent contract id)
					
					String lsContractStartDate = loContractDetailByContract.getContractStart();	
					String lsContractEndDate = loContractDetailByContract.getContractEnd();				
					HashMap loContractMap = new HashMap();
					BaseControllerUtil.getContractFiscalYearsUtil(lsContractStartDate, lsContractEndDate, loContractMap);
					//liFiscalYearCount = (Integer) loContractMap.get(HHSConstants.LI_FYCOUNT);
					String lsEndFY = (String.valueOf(loContractMap.get(HHSConstants.LI_END_YEAR)));		
					
					
					liContractFinancialsMaxYear = (Integer) DAOUtil.masterDAO(aoMybatisSession, loContractDetailByContract.getParentContractId(),
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_CONTRACT_FINANCIALS_MAX_YEAR,
							HHSConstants.JAVA_LANG_STRING);
					
					if(lsEndFY!=null){
						try{
							liEndYear = Integer.parseInt(lsEndFY);
						}catch(Exception  e ){
							setMoState("ApplicationException while executing processContractAfterAmendmentCofTask method ");
							throw e;							
						}
						if(liEndYear>0 && liContractFinancialsMaxYear!=null && liContractFinancialsMaxYear > HHSConstants.INT_ZERO){
							int liContractFinancialsNextMaxYear = liContractFinancialsMaxYear+1;
							HashMap loTempHashMap = new HashMap();
							loTempHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW,loContractDetailByContract.getParentContractId());
							while(liContractFinancialsNextMaxYear <= liEndYear){							
								loTempHashMap.put("nextFiscalYear",liContractFinancialsNextMaxYear);
								DAOUtil.masterDAO(aoMybatisSession, loTempHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
										HHSConstants.ADD_NEXT_YEAR_CONTRACT_FINANCIALS_ZERO_DOLLAR_AMD,
										HHSConstants.JAVA_UTIL_HASH_MAP);
								//do contract fin funding stream insert
								DAOUtil.masterDAO(aoMybatisSession, loTempHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
										HHSConstants.ADD_NEXT_YEAR_CONTRACT_FIN_FUNDING_STREAM_ZERO_DOLLAR_AMD,
										HHSConstants.JAVA_UTIL_HASH_MAP);
								liContractFinancialsNextMaxYear++;
							}//while(liContractFinancialsNextMaxYear <= liEndYear)
						}
					}
					
				} //if(contractAmount!=null && contractAmount.compareTo(BigDecimal.ZERO) == HHSConstants.INT_ZERO)
				//End R8.4.0 qc_8537 $0 Amendments do not appear in Amendment CoF task list after configuration
				
				//existing logic
				else{
					// Release 3.6.0 Enhancement id 6263
					Boolean loNoBudgetAffected = checkIfBudgetAffected(aoMybatisSession, aoFinishTaskStatus,
							aoTaskDetailsBean);					
					loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
					if (loNoBudgetAffected)
					{
						// If Amendment did not affect any existing fiscal year
						// budgets for the contract then
						// Change <Amendment Status> to Pending Registration
						loHashMap
								.put(ApplicationConstants.STATUS_ID_KEY, PropertyLoader.getProperty(
										HHSConstants.PROPERTIES_STATUS_CONSTANT,
										HHSConstants.STATUS_CONTRACT_PENDING_REGISTARTION));
					}
					else
					{
						loHashMap.put(ApplicationConstants.STATUS_ID_KEY, PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_CONTRACT_PENDING_SUBMISSION));
						// 3.6.0 enhancement id 6484
						loHashMap.put(HHSConstants.CONTRACT_ID_KEY, aoTaskDetailsBean.getContractId());
						ArrayList<String> loSubBudgetDetails = (ArrayList<String>) DAOUtil.masterDAO(aoMybatisSession,
								aoTaskDetailsBean.getContractId(), HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.FETCH_SUB_BUDGET_DETAILS_FOR_AMENDMENT_CONF_TASK,
								HHSConstants.JAVA_LANG_STRING);
						for (String lsUpdateSubBudgetId : loSubBudgetDetails)
						{
							DAOUtil.masterDAO(aoMybatisSession, Integer.parseInt(lsUpdateSubBudgetId),
									HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
									HHSConstants.INSERT_SUB_BUDGET_DETAILS_SITE_DETAILS_FOR_UPDATE,
									HHSConstants.INTEGER_CLASS_PATH);
						}
					}
				}
				// R4, updating amendment contract status to pending submission
				DAOUtil.masterDAO(aoMybatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.UPDATE_AMENDMENT_STATUS_COF, HHSConstants.JAVA_UTIL_HASH_MAP);

				// Inserting rows in PDF_BATCH_DETAILS for budget amendment
				// summary
				loHashMap.put(HHSConstants.NEW_ENTITY_TYPE, HHSConstants.CONTRACT_AMENDMENT);
				loHashMap.put(HHSConstants.SUB_ENTITY_TYPE, HHSConstants.CONTRACT_AMENDMENT);
				loHashMap.put(HHSConstants.NEW_ENTITY_ID, aoTaskDetailsBean.getContractId());
				loHashMap.put(HHSConstants.SUB_ENTITY_ID, aoTaskDetailsBean.getContractId());
				loHashMap.put(HHSConstants.TT_USERID, aoTaskDetailsBean.getUserId());
				loHashMap.put(HHSConstants.STATUS_COLUMN, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.PDF_NOT_STARTED));
				DAOUtil.masterDAO(aoMybatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.INSERT_PDF_BACTH_FOR_AMENDMENT_COF, HHSConstants.JAVA_UTIL_HASH_MAP);

				lbProcessFlag = Boolean.TRUE;

			}
			catch (ApplicationException loExp)
			{
				setMoState("ApplicationException while executing processContractAfterAmendmentCofTask method ");
				throw loExp;

			}
			catch (Exception loEx)
			{
				setMoState("Exception while executing processContractAfterAmendmentCofTask method ");
				throw new ApplicationException("Exception while executing processContractAfterAmendmentCofTask method",
						loEx);

			}

		}
		return lbProcessFlag;

	}

	/**
	 * @param aoMybatisSession
	 * @param aoTaskDetailsBean
	 * @return
	 * @throws ApplicationException
	 */
	// Release 3.6.0 Enhancement id 6263
	public Boolean checkIfBudgetAffected(SqlSession aoMybatisSession, Boolean aoFinishTaskStatus,
			TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		if (aoFinishTaskStatus)
		{
			try
			{
				// Updating Budget Status from Pending Configuration to Pending
				// Submission.
				Integer liNoOfBudgetAffected = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_UPDATE_AMENDMENT_BUDGET_STATUS,
						HHSConstants.CS_TASK_DETAILS_BEAN);
				if (liNoOfBudgetAffected == 0)
				{
					return true;
				}
			}
			catch (ApplicationException loExp)
			{
				setMoState("ApplicationException while executing processContractAfterAmendmentCofTask method ");
				throw loExp;

			}
		}
		return false;
	}

	/**
	 * This method was change as part of release 3.6.0 enhancement 6263
	 * @param aoMybatisSession
	 * @param aoTaskDetailsBean
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean checkIfBudgetAffectedAndNegativeAmendment(SqlSession aoMybatisSession, Boolean aoFinishTaskStatus,
			TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Boolean loNegativeAmendmentNoBudgetEffected = false;
		if (aoFinishTaskStatus)
		{
			try
			{

				//Start R8.4.0 qc_8537 $0 Amendments do not appear in Amendment CoF task list after configuration
				BigDecimal loNegativeAmendmentAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession,
						aoTaskDetailsBean.getContractId(),
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_CONTRACT_AMENDMENT_AMOUNT, HHSConstants.JAVA_LANG_STRING);
				
				//if it's $0 Amendment, keep the original status '106' : budget pending configuration 
				if(loNegativeAmendmentAmount!=null && loNegativeAmendmentAmount.compareTo(BigDecimal.ZERO) == HHSConstants.INT_ZERO){
					  return false;			
				 }
				//existing logic, NegativeAmendment ,  Updating Budget Status
				else{
					loNegativeAmendmentNoBudgetEffected = checkIfBudgetAffected(aoMybatisSession,
								aoFinishTaskStatus, aoTaskDetailsBean);	
					if (loNegativeAmendmentAmount.compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO
							&& loNegativeAmendmentNoBudgetEffected)
						return true;
				 }
														
				//End R8.4.0 qc_8537 $0 Amendments do not appear in Amendment CoF task list after configuration
				
			}
			catch (ApplicationException loExp)
			{
				setMoState("ApplicationException while executing checkIfBudgetAffectedAndNegativeAmendment method");
				throw loExp;

			}
		}
		return false;
	}

	/**
	 * This method will be called after contract certification of fund document
	 * has been successfully generated.
	 * <ul>
	 * <li>If the Contract source is R2, Check to see if the Cert of Funds task
	 * has been approved for all other contracts awarded from the procurement.If
	 * yes, generate S247 - 'Configure Award Documents Task' else no action
	 * required.</li>
	 * <li>If the Contract source is R3(APT),update the contract status to
	 * 'Pending Registration' .</li>
	 * <li>Query Id 'fetchContractSourceId' is executed</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * 
	 * @param aoMybatisSession aoMybatisSession Object
	 * @param aoUserSession P8UserSession Object
	 * @param aoFinishTaskStatus Boolean Object
	 * @param aoTaskDetailsBean TaskDetailsBean Object
	 * @return lbProcessFlag Finish Task Status
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */

	@SuppressWarnings("unchecked")
	public Boolean processContractAfterCOFTask(SqlSession aoMybatisSession, P8UserSession aoUserSession,
			Boolean aoFinishTaskStatus, TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Boolean lbProcessFlag = Boolean.FALSE;
		if (aoFinishTaskStatus)
		{

			try
			{
				// fetch contract source id to determine contract is R2 (1) or
				// R3 (2)
				// contract
				String lsContractSourceId = (String) DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_FETCH_CONTRACT_SOURCE_ID,
						HHSConstants.CS_TASK_DETAILS_BEAN);
				
				if (lsContractSourceId == null)
				{
					throw new ApplicationException(
							"Contract SourceId is not found in processContractAfterCOFTask method while executing the fetchContractSourceId.");
				}
				else if (lsContractSourceId.equalsIgnoreCase(HHSConstants.ONE))
				{
					// if procurement of this contract is in "selections made"
					// status then update contract to pending registration and
					// budget from pending configuration to
					// pending submission
					DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.CS_UPDATE_CONTRACT_PROC_SELECTIONS_MADE, HHSConstants.CS_TASK_DETAILS_BEAN);

					DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.CS_UPDATE_BUDGET_PROC_SELECTIONS_MADE, HHSConstants.CS_TASK_DETAILS_BEAN);
				}
				else if (lsContractSourceId.equalsIgnoreCase(HHSConstants.TWO))
				{   
					// step1: update contract status to pending registration
					Integer liNoOfRowUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_UPDATE_CONTRACT_STATUS,
							HHSConstants.CS_TASK_DETAILS_BEAN);
					
					if (liNoOfRowUpdated <= HHSConstants.INT_ZERO)
					{
						throw new ApplicationException(
								"Exception occured in processContractAfterCOFTask method while executing the updateContractStatus.");
					}
					
					//Start QC 9145 R 8.6 - Update status to Registered if Contract already FMS Registered
					//LOG_OBJECT.Debug("====aoTaskDetailsBean ::  " +aoTaskDetailsBean);
					String contractId = aoTaskDetailsBean.getContractId();
					String parentContractId = aoTaskDetailsBean.getBaseContractId();
					String lsContractType = null;
					RefContractFMSBean fmsBean = null;
					RefContractFMSBean vendorBean = null;
					RefContractFMSBean commodityBean = null;

					lsContractType = (String) DAOUtil.masterDAO(aoMybatisSession, contractId,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_CONTRACT_TYPE,
							HHSConstants.JAVA_LANG_STRING);
					// Base contract only
					if((HHSConstants.ONE).equalsIgnoreCase(lsContractType))
					{
						// check if registered in FMS
						// don't be confuse by the transaction's name
						 
						HashMap loHashMap = new HashMap();
						loHashMap.put(HHSConstants.AMEND_CONTRACT_ID_WORKFLOW, contractId);
						Integer loIsAmendmentRegInFMS = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
								HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.CANCEL_AMENDMENT_CHECK_REG_IN_FMS,
								HHSConstants.JAVA_UTIL_HASH_MAP);
						
						if (null != loIsAmendmentRegInFMS && loIsAmendmentRegInFMS != 0)
						{// if yes
							//Start QC 9145 R 8.8 - add additional code
							ContractBean baseContractBean = null;
							baseContractBean = (ContractBean) DAOUtil.masterDAO(aoMybatisSession, contractId,
								HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_BASE_CONTRACT_START_END_DATE,
								HHSConstants.JAVA_LANG_STRING);
							//LOG_OBJECT.Debug("======baseContractBean ::  " +baseContractBean);
													
							fmsBean = (RefContractFMSBean) DAOUtil.masterDAO(aoMybatisSession, contractId,
									HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
									HHSConstants.FETCH_REF_CONTRACT_FMS, HHSConstants.JAVA_LANG_STRING);
							
							fmsBean.setAgencyId(baseContractBean.getAgencyId());
							fmsBean.setContractId(contractId);
							fmsBean.setContractTypeId(baseContractBean.getContractTypeId());
							fmsBean.setContractStartDate(baseContractBean.getContractStartDate());
							fmsBean.setContractEndDate(baseContractBean.getContractEndDate());
							fmsBean.setAwardEpin(baseContractBean.getAwardEpin());
							fmsBean.setContractAmount(baseContractBean.getContractAmount());
							fmsBean.setOrganizationId(baseContractBean.getOrganizationId());
							fmsBean.setUserId(baseContractBean.getUserId());
							if(fmsBean.getUserId()==null || fmsBean.getUserId().isEmpty())
								fmsBean.setUserId("system");
							
							vendorBean = (RefContractFMSBean) DAOUtil.masterDAO(aoMybatisSession, fmsBean,
									HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
									HHSConstants.FETCH_VENDOR_TIN_FMS, HHSConstants.CLASS_REF_CONTRACT_FMS_BEAN);
							//LOG_OBJECT.Debug("=======vendorBean ::  " +vendorBean);
							fmsBean.setVendCustCd(vendorBean.getVendCustCd());
							fmsBean.setVendFmsId(vendorBean.getVendFmsId());
							fmsBean.setVendTin(vendorBean.getVendTin());
							fmsBean.setAwardEpin(vendorBean.getAwardEpin());
							
							commodityBean = (RefContractFMSBean) DAOUtil.masterDAO(aoMybatisSession, fmsBean,
									HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
									HHSConstants.FETCH_DOC_VERSION_AND_COMMODITY_CODE_FMS, HHSConstants.CLASS_REF_CONTRACT_FMS_BEAN);
							//LOG_OBJECT.Debug("=======commodityBean ::  " +commodityBean);	 
							if(commodityBean!= null)
							{
								fmsBean.setExtCpmmodityCode(commodityBean.getExtCpmmodityCode());
								fmsBean.setExtDocVersNo(commodityBean.getExtDocVersNo());
							}
							//LOG_OBJECT.Debug("=======fmsBean ::  " +fmsBean);	
							
							String einId = (String) DAOUtil.masterDAO(aoMybatisSession, fmsBean.getOrganizationId(),
									HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
									HHSConstants.FETCH_EIN_ID, HHSConstants.JAVA_LANG_STRING);
							//LOG_OBJECT.Debug("=======einId ::  " +einId);
							
							String agencyId = (String) DAOUtil.masterDAO(aoMybatisSession, fmsBean,
									HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
									HHSConstants.FETCH_AGENCY_ID_FMS, HHSConstants.CLASS_REF_CONTRACT_FMS_BEAN);
							//LOG_OBJECT.Debug("=======agencyId ::  " +agencyId);
							
							if(agencyId != null && !agencyId.isEmpty() && einId != null && einId.equalsIgnoreCase(fmsBean.getVendTin()))
							{	
								LOG_OBJECT.Debug("Update CT and Contract Status to 118 if Contract is already Registered in FMS ");						
								//validate dates and amount
								fmsBean.setStatusId("118");
								if(fmsBean.getContractAmount().equalsIgnoreCase(fmsBean.getFmsContractAmount()) 
										&& fmsBean.getContractStartDate().equals(fmsBean.getFmsContractStartDate())
										&& fmsBean.getContractEndDate().equals(fmsBean.getFmsContractEndDate()) )
								{   						
									fmsBean.setDiscrepancyFlag("0");
									liNoOfRowUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, fmsBean, 
											HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
											HHSConstants.UPDATE_CONTRACT_WITH_CT_AND_STATUS, HHSConstants.CLASS_REF_CONTRACT_FMS_BEAN);
									if (liNoOfRowUpdated <= HHSConstants.INT_ZERO)
									{
										throw new ApplicationException(
												"Exception occured in processContractAfterCOFTask method while executing the updateContractWithCTandStatus.");
									}
									
								}
								else
								{   
									fmsBean.setDiscrepancyFlag("1");
									liNoOfRowUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, fmsBean, 
											HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
											HHSConstants.UPDATE_CONTRACT_WITH_CT_AND_FMSINFO, HHSConstants.CLASS_REF_CONTRACT_FMS_BEAN);
									if (liNoOfRowUpdated <= HHSConstants.INT_ZERO)
									{
										throw new ApplicationException(
												"Exception occured in processContractAfterCOFTask method while executing the updateContractWithCTandFMSInfo.");
									}
			
								}
								
							}
							
						}	
							
					}
					
					//End QC 9145 R 8.8 - Update status to Registerd if Contract already FMS Registered
					
					// update budget from pending configuration to pending
					// submission
					DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_FETCH_BUDGET_CONFIGURATION,
							HHSConstants.CS_TASK_DETAILS_BEAN);
				}
				lbProcessFlag = true;
			}
			catch (ApplicationException loExp)
			{
				setMoState("ApplicationException while executing processContractAfterCOFTask method ");
				throw loExp;

			}
			catch (Exception loEx)
			{
				setMoState("Exception while executing processContractAfterCOFTask method ");
				throw new ApplicationException("Exception while executing processContractAfterCOFTask method", loEx);

			}

		}
		return lbProcessFlag;

	}

	/**
	 * This method will be called after configure award document task if
	 * finished. Here all budgets of contracts under procurement are set to
	 * pending submission status
	 * 
	 * <ul>
	 * <li>Update budget table and status to pending submission</li>
	 * <li>Query Id 'updateR2ContractStatusToPendReg' is executed</li>
	 * <li>Query Id 'updateR2BudgetStatusToPendSub is executed</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param asEvaluationPoolMappingId String containing Evaluation PoolMapping
	 *            Id
	 * @throws ApplicationException ApplicationException object
	 */

	public void updateBudgetStatusToPendSub(SqlSession aoMybatisSession, String asEvaluationPoolMappingId)
			throws ApplicationException
	{
		try
		{    
			// procurement of this contract is in "selections made" status then
			// update contract from pending configuration to pending
			// registration
			DAOUtil.masterDAO(aoMybatisSession, asEvaluationPoolMappingId,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_UPDATE_R2_CONTRACT_STATUS_PENDING_REGISTRATION, HHSConstants.JAVA_LANG_STRING);
					
			// update budget from pending configuration to pending submission
			DAOUtil.masterDAO(aoMybatisSession, asEvaluationPoolMappingId,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_UPDATE_R2_BUDGET_STATUS_PENDING_SUBMISSION, HHSConstants.JAVA_LANG_STRING);

		}
		catch (ApplicationException loExp)
		{
			setMoState("ApplicationException while executing processContractAfterCOFTask method ");
			throw loExp;

		}
		catch (Exception loEx)
		{
			setMoState("Exception while executing processContractAfterCOFTask method ");
			throw new ApplicationException("Exception while executing processContractAfterCOFTask method", loEx);

		}

	}

	/**
	 * This method will be called after contract certification of fund document
	 * has been successfully generated.
	 * <ul>
	 * <li>If the Contract source is R2 or R3(APT),update the contract status to
	 * 'Pending COF' .</li>
	 * </ul>
	 * 
	 * Query Id 'updateContractStatusCof'
	 * 
	 * @param aoMybatisSession aoMybatisSession
	 * @param aoFinishTaskStatus Boolean
	 * @param aoTaskDetailsBean TaskDetailsBean
	 * @return lbProcessFlag Finish Task Status
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */

	public Boolean updateContractStatus(SqlSession aoMybatisSession, Boolean aoFinishTaskStatus,
			TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		Boolean lbProcessFlag = Boolean.FALSE;
		try
		{
			if (null != aoTaskDetailsBean.getLaunchCOF() && aoTaskDetailsBean.getLaunchCOF())
			{
				DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_UPDATE_CONTRACT_COF_STATUS, HHSConstants.CS_TASK_DETAILS_BEAN);
				lbProcessFlag = Boolean.TRUE;
			}
		}
		catch (ApplicationException loExp)
		{
			setMoState("ApplicationException while executing updateContractStatus method ");
			throw loExp;

		}
		catch (Exception loEx)
		{
			setMoState("Exception while executing updateContractStatus method ");
			throw new ApplicationException("Exception while executing updateContractStatus method", loEx);

		}

		return lbProcessFlag;
	}

	/**
	 * This method updated Contract Financials in DB. If Task is Contract
	 * Configuration then 'submiited by' and'submitted date' are updated in DB
	 * aqnd else if Task isContract Certification of Funds then 'approved by'
	 * and'approved date' are updated
	 * 
	 * Query Id 'updateSubmittedInfoForCOFDoc'
	 * 
	 * Query Id 'updateApprovedInfoForCOFDoc'
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @return
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	public Integer updateContractFinancials(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		Integer loSuccesfullUpdate = 0;
		try
		{
			if (aoTaskDetailsBean.getTaskName().equalsIgnoreCase(HHSConstants.TASK_CONTRACT_CONFIGURATION)
					|| aoTaskDetailsBean.getTaskName().equalsIgnoreCase(HHSConstants.TASK_AMENDMENT_CONFIGURATION))
			{
				loSuccesfullUpdate = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean,
						HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.UPDATE_SUBMITTED_INFO_FOR_COF_DOC,
						HHSConstants.CS_TASK_DETAILS_BEAN);
			}
			else if (aoTaskDetailsBean.getTaskName().equalsIgnoreCase(HHSConstants.TASK_CONTRACT_COF)
					|| aoTaskDetailsBean.getTaskName().equalsIgnoreCase(HHSConstants.TASK_AMENDMENT_COF))
			{
				loSuccesfullUpdate = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean,
						HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.UPDATE_APPROVED_INFO_FOR_COF_DOC,
						HHSConstants.CS_TASK_DETAILS_BEAN);
			}

		}
		catch (ApplicationException loExp)
		{
			setMoState("ApplicationException while executing updateContractFinancials method ");
			throw loExp;

		}
		catch (Exception loEx)
		{
			setMoState("Exception while executing updateContractFinancials method ");
			throw new ApplicationException("Exception while executing updateContractFinancials method", loEx);

		}
		return loSuccesfullUpdate;

	}

	/**
	 * This method updates Contract Status for a particular contract to
	 * "Pending Configuration".
	 * <ul>
	 * <li>Table affected - CONTRACT</li>
	 * <li>Column affected - STATUS_ID</li> *Query Id
	 * 'updateContractStatusToPendingConfig'
	 * @param aoMybatisSession SqlSession
	 * @param aoTaskDetailsBean TaskDetailsBean
	 * @param aoFinishTaskStatus Finish Task Status
	 * @return lbFinalFlag Boolean
	 * @throws ApplicationException ApplicationException Object
	 * 
	 */

	public Boolean updateContractStatusToPendingConfig(SqlSession aoMybatisSession, Boolean aoFinishTaskStatus,
			TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{

		Boolean lbFinalFlag = Boolean.FALSE;

		if (aoFinishTaskStatus)
		{
			try
			{
				Integer liNoOfRowUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_UPDATE_CONTRACT_STATUS_TO_PENDING_CONFIG, HHSConstants.CS_TASK_DETAILS_BEAN);
				if (liNoOfRowUpdated <= HHSConstants.INT_ZERO)
				{
					throw new ApplicationException(
							"Exception occured in updateContractStatusToPendingConfig method while updating Contract status.");
				}
				setMoState("Contract status updated successfully for Contract Id:" + aoTaskDetailsBean.getContractId());
				lbFinalFlag = Boolean.TRUE;

			}
			catch (ApplicationException loExp)
			{
				setMoState("Error while updating Contract status for Contract Id in updateContractStatusToPendingConfig method : "
						+ aoTaskDetailsBean.getContractId());
				throw loExp;

			}

		}
		return lbFinalFlag;
	}

	// s382 start
	/**
	 * Gets all the procurement and contract level details
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>Procurement value</li>
	 * <li>Contract value</li>
	 * <li>Contract start date</li>
	 * <li>Contract end date</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>On the basis of contractId, the above mentioned details are received
	 * from the DataBase by executing the
	 * <code>fetchContractConfigDetails</code> query in the ConfigurationMapper</li>
	 * <li>It returns the values in ProcurementCOFBean object</li>
	 * </ul>
	 * </li> </ul>
	 * 
	 * @param asContractId contract id under procurement
	 * @param aoMybatisSession SqlSession object
	 * @return ProcurementCOF bean object of type ProcurementCOF
	 * @throws ApplicationException ApplicationException object
	 */
	public ProcurementCOF fetchContractConfigDetails(String asContractId, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		ProcurementCOF loProcurementCOFBean = new ProcurementCOF();
		String lsContractSource = HHSConstants.EMPTY_STRING;
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		loQueryMap.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
		try
		{

			lsContractSource = (String) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.BMC_FETCH_CONTRACT_SOURCE,
					HHSConstants.JAVA_LANG_STRING);
			if (lsContractSource.equalsIgnoreCase(HHSConstants.ONE))
			{
				loProcurementCOFBean = (ProcurementCOF) DAOUtil.masterDAO(aoMybatisSession, loQueryMap,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_CONTRACT_CONFIG_DETAILS,
						HHSConstants.JAVA_UTIL_MAP);
			}
			else if (lsContractSource.equalsIgnoreCase(HHSConstants.TWO))
			{
				loProcurementCOFBean = (ProcurementCOF) DAOUtil.masterDAO(aoMybatisSession, loQueryMap,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.FETCH_CONTRACT_CONFIG_DETAILS_R3_CONTRACT, HHSConstants.JAVA_UTIL_MAP);
			}
			setMoState("Sub Budget details for contract fetched successfully:");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured in ConfigurationService: fetchContractConfigDetails method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchContractConfigDetails method - failed."
					+ " Exception occured while fetching procurement and contract data for contractId::" + asContractId
					+ " \n");
			throw loAppEx;
		}
		return loProcurementCOFBean;
	}

	/**
	 * Gets all the configured Budgets for read only screen
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>For a particular fiscal year for a budget retrieves all
	 * subbudgets/subprogram names</li>
	 * <li>It retrieves details of only configured budgets</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of contractId, the above mentioned details are received
	 * from the DataBase by executing the
	 * <code>fetchContractConfSubBudgetDetails</code> query in the
	 * ConfigurationMapper</li>
	 * <li>It returns the values as list ContractBudgetBean object</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param asContractId contract id under procurement
	 * @param aoMybatisSession SqlSession object
	 * @return List<ContractBudgetBean> list of type ContractBudgetBean
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<ContractBudgetBean> fetchContractConfigBudgetDetails(String asContractId, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		List<ContractBudgetBean> loContractBudgetBeanList = null;
		try
		{
			Map<String, Object> loQueryMap = new HashMap<String, Object>();
			loQueryMap.put(HHSConstants.CONTRACT_ID_KEY, asContractId);

			loContractBudgetBeanList = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession, loQueryMap,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.FETCH_CONTRACT_CONF_SUB_BUDGET_DETAILS, HHSConstants.JAVA_UTIL_MAP);
			setMoState("Sub Budget details for contract fetched successfully:");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchContractConfigBudgetDetails method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchContractConfigBudgetDetails method - failed."
					+ " Exception occured while fetching all configured budget details for contractId::" + asContractId
					+ " \n");
			throw loAppEx;
		}
		return loContractBudgetBeanList;
	}

	// s382 end

	// s390 start

	/**
	 * Gets account allocation details for a contract
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>UoA Code - unitOfAppropriation</li>
	 * <li>Budget Code - budgetCode</li>
	 * <li>Object Code - objectCode</li>
	 * <li>Sub Object Code - subOc</li>
	 * <li>Reporting Category - rc</li>
	 * <li>Fiscal Year - fiscalYear</li>
	 * <li>Amount for fiscal year - total</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of contractId, the above mentioned values are received
	 * from the DataBase by executing the
	 * <code>fetchContractConfCOADetails</code> query in the ConfigurationMapper
	 * </li>
	 * <li>It returns the values as list AccountAllocationBean object</li>
	 * <li>This list in turn is passed to createCoADetailsList method to further
	 * format as per grid input format</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param aoCBGridBean CBGridBean object
	 * @param aoMybatisSession SqlSession object
	 * @return List<AccountsAllocationBean> list of type AccountsAllocationBean
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<AccountsAllocationBean> fetchContractConfCOADetails(CBGridBean aoCBGridBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		List<AccountsAllocationBean> loAccountsAllocationBeanList = null;
		List<AccountsAllocationBean> loAccountsAllocationBeanRtrndList = null;
		String lsContractSourceType = null;
		try
		{
			lsContractSourceType = (String) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBean.getContractID(),
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_CONTRACT_SOURCE_TYPE,
					HHSConstants.JAVA_LANG_STRING);
			if (lsContractSourceType.equalsIgnoreCase(HHSConstants.R2_CONTRACT))
			{
				loAccountsAllocationBeanList = (List<AccountsAllocationBean>) DAOUtil.masterDAO(aoMybatisSession,
						aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.FETCH_R2_CONTRACT_CONF_COA_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				if (null == loAccountsAllocationBeanList || loAccountsAllocationBeanList.isEmpty())
				{
					copyProcFinToContractFin(aoCBGridBean, aoMybatisSession);
				}
			}

			loAccountsAllocationBeanList = (List<AccountsAllocationBean>) DAOUtil.masterDAO(aoMybatisSession,
					aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.FETCH_CONTRACT_CONF_COA_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			loAccountsAllocationBeanRtrndList = createCoADetailsList(loAccountsAllocationBeanList,
					aoCBGridBean.getFiscalYearID());
			if (aoCBGridBean.getIsNewFYScreen())
			{
				markNewRowInAccountAllocationBean(loAccountsAllocationBeanRtrndList);
			}
			setMoState("Account Allocation Details for contract fetched successfully:");
		}
		// ApplicationException is thrown while executing query and method
		// createCoADetailsList
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AO_CB_GRID_BEAN, CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchContractConfCOADetails method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchContractConfCOADetails method - failed."
					+ " Exception occured while fetching all Chart of account details for CBGridBean::"
					+ CommonUtil.convertBeanToString(aoCBGridBean) + " \n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: fetchContractConfigBudgetDetails:: ", loEx);
			loAppEx.addContextData(HHSConstants.AO_CB_GRID_BEAN, CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchContractConfCOADetails method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchContractConfCOADetails method - failed."
					+ " Exception occured while fetching all Chart of account details for CBGridBean::"
					+ CommonUtil.convertBeanToString(aoCBGridBean) + " \n");
			throw loAppEx;
		}
		return loAccountsAllocationBeanRtrndList;
	}

	/**
	 * 
	 * Gets account allocation details for a contract
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>UoA Code - unitOfAppropriation</li>
	 * <li>Budget Code - budgetCode</li>
	 * <li>Object Code - objectCode</li>
	 * <li>Sub Object Code - subOc</li>
	 * <li>Reporting Category - rc</li>
	 * <li>Fiscal Year - fiscalYear</li>
	 * <li>Amount for fiscal year - total</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of contract Type (fetch contract type info by calling
	 * 'fetchContractType' query) decide whether details need to be fetch for
	 * amendment contract or original contract</li>
	 * <li>For Amendment calls 'fetchContractConfCOADetailsAmendment'</li>
	 * <li>For Amendment calls 'fetchContractConfCOAOriginalDetails'</li>
	 * <li>This list in turn is passed to createCoADetailsList method to further
	 * format as per grid input format</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * @param aoCBGridBean CBGridBean object
	 * @param aoMybatisSession SqlSession object
	 * @return List<AccountsAllocationBean> list of type AccountsAllocationBean
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<AccountsAllocationBean> fetchContractCOFCOA(CBGridBean aoCBGridBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		List<AccountsAllocationBean> loAccountsAllocationBeanList = null;
		List<AccountsAllocationBean> loContractCOFCOAList = null;
		String lsContractType = null;
		try
		{
			lsContractType = (String) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBean.getContractID(),
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_CONTRACT_TYPE,
					HHSConstants.JAVA_LANG_STRING);
			if (lsContractType.equalsIgnoreCase(HHSConstants.TWO))
			{
				loAccountsAllocationBeanList = (List<AccountsAllocationBean>) DAOUtil.masterDAO(aoMybatisSession,
						aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.FETCH_CONTRACT_CONF_COA_DETAILS_AMENDMENT,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			}
			else
			{
				loAccountsAllocationBeanList = (List<AccountsAllocationBean>) DAOUtil.masterDAO(aoMybatisSession,
						aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.FETCH_CONTRACT_CONF_COA_ORIGINAL_DETAILS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			}
			loContractCOFCOAList = createCoADetailsList(loAccountsAllocationBeanList, aoCBGridBean.getFiscalYearID());
			setMoState("Account Allocation Details for contract fetched successfully:");
		}
		// ApplicationException is thrown while executing query and method
		// createCoADetailsList
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AO_CB_GRID_BEAN, CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchContractCOFCOA method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchContractCOFCOA method - failed."
					+ " Exception occured while fetching all Chart of account details for CBGridBean::"
					+ CommonUtil.convertBeanToString(aoCBGridBean) + " \n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: fetchContractCOFCOA:: ", loEx);
			loAppEx.addContextData(HHSConstants.AO_CB_GRID_BEAN, CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchContractCOFCOA method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchContractCOFCOA method - failed."
					+ " Exception occured while fetching all Chart of account details for CBGridBean::"
					+ CommonUtil.convertBeanToString(aoCBGridBean) + " \n");
			throw loAppEx;
		}
		return loContractCOFCOAList;
	}

	/**
	 * This method set new id for new FY fiscal Task by appending
	 * '_newrecord_coa' on each existing grid id.
	 * @param aoAccountsAllocationBeanRtrndList List of AccountsAllocationBean
	 *            object
	 */
	private void markNewRowInAccountAllocationBean(List<AccountsAllocationBean> aoAccountsAllocationBeanRtrndList)
	{
		for (AccountsAllocationBean loAllocationBean : aoAccountsAllocationBeanRtrndList)
		{
			if (!loAllocationBean.getNewFYFiscalYearId().equals(HHSConstants.EMPTY_STRING)
					&& Integer.parseInt(loAllocationBean.getNewFYFiscalYearId()) >= Integer.parseInt(BaseControllerUtil
							.getCurrentFiscalYear()))
			{
				loAllocationBean.setId(loAllocationBean.getId() + HHSConstants.NEW_RECORD_COA);
			}
		}
	}

	/**
	 * This method copies the procurement content to Contract Query Id
	 * 'fetchContractFiscalYears'
	 * @param aoCBGridBean CBGridBean object
	 * @param aoMybatisSession SqlSession object
	 * @throws ApplicationException ApplicationException object
	 * @throws IllegalAccessException IllegalAccessException object
	 * @throws InvocationTargetException InvocationTargetException object
	 * 
	 * 
	 */
	private void copyProcFinToContractFin(CBGridBean aoCBGridBean, SqlSession aoMybatisSession)
			throws ApplicationException, IllegalAccessException, InvocationTargetException
	{

		List<AccountsAllocationBean> loAccountsAllocationBeanList = null;
		Map loContractMap = null;
		int liFiscalYearCount = 0;
		Integer loCounter = 0;
		try
		{
			loAccountsAllocationBeanList = fetchPCOFCoADetails(aoCBGridBean, aoMybatisSession);
			loContractMap = (Map) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBean.getContractID(),
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_CONTRACT_FISCAL_YEARS,
					HHSConstants.JAVA_LANG_STRING);
			String lsContractStartDate = (String) loContractMap.get(HHSConstants.CONTRACT_START_DATE_KEY);
			String lsContractEndDate = (String) loContractMap.get(HHSConstants.CONTRACT_END_DATE_KEY);
			loContractMap = new HashMap();
			BaseControllerUtil.getContractFiscalYearsUtil(lsContractStartDate, lsContractEndDate, loContractMap);
			liFiscalYearCount = (Integer) loContractMap.get(HHSConstants.LI_FYCOUNT);
			String lsStartFY = (String.valueOf(loContractMap.get(HHSConstants.LI_START_YEAR)));
			String lsEndFY = (String.valueOf(loContractMap.get(HHSConstants.LI_END_YEAR)));

			for (AccountsAllocationBean loAccountsAllocationBean : loAccountsAllocationBeanList)
			{
				for (loCounter = 1; loCounter <= liFiscalYearCount; loCounter++)
				{
					String lsPropertyName = HHSConstants.SMALL_FY.concat(loCounter.toString());
					BeanUtils.setProperty(loAccountsAllocationBean, lsPropertyName, HHSConstants.STRING_ZERO);
				}
				loAccountsAllocationBean.setContractStartFY(lsStartFY);
				loAccountsAllocationBean.setContractEndFY(lsEndFY);
				loAccountsAllocationBean.setFiscalYearID(lsStartFY);

				String[] loChartOfAccount = loAccountsAllocationBean.getChartOfAccount().split(HHSConstants.HYPHEN);
				loAccountsAllocationBean.setUnitOfAppropriation(loChartOfAccount[0]);
				loAccountsAllocationBean.setBudgetCode(loChartOfAccount[1]);
				loAccountsAllocationBean.setObjectCode(loChartOfAccount[2]);
				loAccountsAllocationBean.setProcurementID(aoCBGridBean.getProcurementID());
				loAccountsAllocationBean.setContractID(aoCBGridBean.getContractID());
				loAccountsAllocationBean.setCreatedByUserId(aoCBGridBean.getCreatedByUserId());
				addContractConfCOADetails(loAccountsAllocationBean, aoMybatisSession);
			}

		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AO_CB_GRID_BEAN, CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: copyProcFinToContractFin method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: copyProcFinToContractFin method - failed."
					+ " Exception occured while fetching Contract Fiscal Years::"
					+ CommonUtil.convertBeanToString(aoCBGridBean) + " \n");
			throw loAppEx;
		}

	}

	/**
	 * This method is used to insert the Chart of Accounts details in the
	 * DataBase.
	 * 
	 * <ul>
	 * <li>A list of fields are sent to the DataBase using
	 * <code>insertContractConfCOADetails</code> insert query</li>
	 * <li>The fields inserted in the table are -</li>
	 * <ul>
	 * <li>UNIT_OF_APPROPRIATION</li>
	 * <li>BUDGET_CODE</li>
	 * <li>OBJECT_CODE</li>
	 * <li>SUB_OBJECT_CODE</li>
	 * <li>REPORTING_CATEGORY</li>
	 * </ul>
	 * </ul>
	 * 
	 * @param aoAccountsAllocationBean AccountsAllocationBean object
	 * @param aoMybatisSession SqlSession object
	 * @throws ApplicationException Exception thrown in case of any error
	 */
	public void addContractConfCOADetails(AccountsAllocationBean aoAccountsAllocationBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		try
		{
			boolean lbRowAlreadyExists = checkDeletedRowIsAddedAgain(aoAccountsAllocationBean, aoMybatisSession);
			if (lbRowAlreadyExists)
			{
				aoAccountsAllocationBean.setDeletedRecord(true);
				editContractConfCOADetails(aoAccountsAllocationBean, aoMybatisSession);
			}
			else
			{

				int liStartFY = Integer.parseInt(aoAccountsAllocationBean.getContractStartFY());
				int liEndFY = Integer.parseInt(aoAccountsAllocationBean.getContractEndFY());
				int liCounter = HHSConstants.INT_ONE;
				for (int liCount = liStartFY; liCount <= liEndFY; liCount++)
				{
					String lsBeanFY = HHSConstants.SMALL_FY + liCounter;
					aoAccountsAllocationBean.setActiveFlag(HHSConstants.ONE);
					aoAccountsAllocationBean.setFiscalYearID(String.valueOf(liCount));
					String lsAmt = (String) BeanUtils.getProperty(aoAccountsAllocationBean, lsBeanFY);
					if (null == lsAmt)
					{// Added for NewFY Configuration Task
						// for read-only FY's amount
						lsAmt = HHSConstants.ZERO;
					}
					aoAccountsAllocationBean.setAmmount(lsAmt);
					DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.ADD_CONTRACT_CONF_COA_DETAILS,
							HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
					liCounter++;
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AO_ACCOUNTS_ALLOCATION_BEAN,
					CommonUtil.convertBeanToString(aoAccountsAllocationBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: addContractConfCOADetails method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: addContractConfCOADetails method - failed."
					+ " Exception occured while inserting new record in Chart of allocation for AccountsAllocationBean::"
					+ CommonUtil.convertBeanToString(aoAccountsAllocationBean) + " \n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: addContractConfCOADetails:: ", loEx);
			loAppEx.addContextData(HHSConstants.AO_ACCOUNTS_ALLOCATION_BEAN,
					CommonUtil.convertBeanToString(aoAccountsAllocationBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: addContractConfCOADetails method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: addContractConfCOADetails method - failed."
					+ " Exception occured while inserting new record in Chart of allocation for AccountsAllocationBean::"
					+ CommonUtil.convertBeanToString(aoAccountsAllocationBean) + " \n");
			throw loAppEx;
		}

		setMoState("addContractConfCOADetails is executed successfully");
	}

	/**
	 * <p>
	 * This is private method used to check if the row added already exists with
	 * active flag as zero. Simply it checks if user is trying to add the same
	 * row which is deleted earlier
	 * </p>
	 * <ul>
	 * <li>Step executed are:</li>
	 * <li>1.fetch all records for same charge codes with active flag 0</li>
	 * <li>2.Set id in bean equivalent to different charge codes as below</li>
	 * </ul>
	 * Query Id 'fetchContractConfCOADeletedRows'
	 * @param aoAccAllocWriteBean bean of type AccountsAllocationBean
	 * @param aoMybatisSession SqlSession object
	 * @return boolean lbRowAlreadyExists
	 * @throws Exception Exception thrown in case of any application code
	 *             failure.
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	private boolean checkDeletedRowIsAddedAgain(AccountsAllocationBean aoAccountsAllocationBean,
			SqlSession aoMybatisSession) throws Exception
	{
		boolean lbRowAlreadyExists = false;
		List<AccountsAllocationBean> loAccountsAllocationBeanList = (List<AccountsAllocationBean>) DAOUtil.masterDAO(
				aoMybatisSession, aoAccountsAllocationBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
				HHSConstants.FETCH_CONTRACT_CONF_COA_DELETED_ROWS, HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
		if (null != loAccountsAllocationBeanList && !loAccountsAllocationBeanList.isEmpty())
		{
			aoAccountsAllocationBean.setId(aoAccountsAllocationBean.getUnitOfAppropriation() + HHSR5Constants.HYPHEN
					+ aoAccountsAllocationBean.getBudgetCode() + HHSR5Constants.HYPHEN
					+ aoAccountsAllocationBean.getObjectCode() + HHSR5Constants.HYPHEN
					+ aoAccountsAllocationBean.getSubOc() + HHSR5Constants.HYPHEN + aoAccountsAllocationBean.getRc());
			lbRowAlreadyExists = true;
		}
		return lbRowAlreadyExists;
	}

	/**
	 * This method is used to delete the Chart of Accounts details in the
	 * database on the basis chart of allocation, sub object code and reporting
	 * category.
	 * 
	 * <li>Soft delete is made disabling active flag to zero</li> *Query Id
	 * 'delContractConfCOADetails'
	 * @param aoAccountsAllocationBean AccountsAllocationBean object
	 * @param aoMybatisSession SqlSession object
	 * @throws ApplicationException Exception thrown in case of any error
	 * 
	 */
	public void delContractConfCOADetails(AccountsAllocationBean aoAccountsAllocationBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.DEL_CONTRACT_CONF_COA_DETAILS,
					HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
			setMoState("Chart of Account Allocation Details for contract deleted successfully:");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AO_ACCOUNTS_ALLOCATION_BEAN,
					CommonUtil.convertBeanToString(aoAccountsAllocationBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: delContractConfCOADetails method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: delContractConfCOADetails method - failed."
					+ " Exception occured while deleting chart of allocation record for AccountsAllocationBean::"
					+ CommonUtil.convertBeanToString(aoAccountsAllocationBean) + " \n");
			throw loAppEx;
		}
	}

	/**
	 * This method is used to edit the Chart of Accounts details in the
	 * DataBase.
	 * 
	 * <ul>
	 * <li>One or more fields of below list can be edited in DataBase using
	 * <code>editContractConfCOADetails</code> Update query</li>
	 * <li>The fields which can be edited in the table are -</li>
	 * <ul>
	 * <li>UNIT_OF_APPROPRIATION</li>
	 * <li>BUDGET_CODE</li>
	 * <li>OBJECT_CODE</li>
	 * <li>SUB_OBJECT_CODE</li>
	 * <li>REPORTING_CATEGORY</li>
	 * <li>AMOUNT</li>
	 * </ul>
	 * <ul>
	 * <li>Incase update calls return count of updated rows as zero then do an
	 * insert call to database</li>
	 * </ul>
	 * </ul>
	 * 
	 * @param aoAccountsAllocationBean AccountsAllocationBean object
	 * @param aoMybatisSession SqlSession object
	 * @throws ApplicationException Exception thrown in case of any error
	 */
	public void editContractConfCOADetails(AccountsAllocationBean aoAccountsAllocationBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		try
		{
			int liStartFY = Integer.parseInt(aoAccountsAllocationBean.getContractStartFY());
			int liEndFY = Integer.parseInt(aoAccountsAllocationBean.getContractEndFY());
			int liCounter = HHSConstants.INT_ONE;
			int liRowsUpdated = HHSConstants.INT_ZERO;
			for (int liCount = liStartFY; liCount <= liEndFY; liCount++)
			{
				String lsBeanFY = HHSConstants.SMALL_FY + liCounter;
				aoAccountsAllocationBean.setFiscalYearID(String.valueOf(liCount));
				String lsAmt = (String) BeanUtils.getProperty(aoAccountsAllocationBean, lsBeanFY);
				aoAccountsAllocationBean.setAmmount(lsAmt);
				liRowsUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.EDIT_CONTRACT_CONF_COA_DETAILS,
						HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
				if (liRowsUpdated == HHSConstants.INT_ZERO)
				{
					aoAccountsAllocationBean.setActiveFlag(HHSConstants.ONE);
					aoAccountsAllocationBean.setFiscalYearID(String.valueOf(liCount));
					DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.ADD_CONTRACT_CONF_COA_DETAILS,
							HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
				}
				liCounter++;
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AO_ACCOUNTS_ALLOCATION_BEAN,
					CommonUtil.convertBeanToString(aoAccountsAllocationBean));
			LOG_OBJECT
					.Error("Exception occured in ConfigurationService: editContractConfCOADetails method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: editContractConfCOADetails method - failed."
					+ " Exception occured while editing chart of allocation record for AccountsAllocationBean::"
					+ CommonUtil.convertBeanToString(aoAccountsAllocationBean) + " \n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: editContractConfCOADetails:: ", loEx);
			loAppEx.addContextData(HHSConstants.AO_ACCOUNTS_ALLOCATION_BEAN,
					CommonUtil.convertBeanToString(aoAccountsAllocationBean));
			LOG_OBJECT
					.Error("Exception occured in ConfigurationService: editContractConfCOADetails method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: editContractConfCOADetails method - failed."
					+ " Exception occured while editing record in Chart of allocation for AccountsAllocationBean::"
					+ CommonUtil.convertBeanToString(aoAccountsAllocationBean) + " \n");
			throw loAppEx;
		}
		setMoState("editContractConfCOADetails is executed successfully");
	}

	/**
	 * <p>
	 * This method reads each entry in the aoAccountsAllocationBeanList and
	 * clubs all entries which has same
	 * UnitOfAppropriation-BudgetCode-ObjectCode along with same Rc and SubOc It
	 * also take asContractStartFY as input which is used to set amount in
	 * aoAccountsAllocationBean in fy1..fyn where all codes are same
	 * </p>
	 * <ul>
	 * <li>Step executed are:</li>
	 * <li>1.Initialise previous key to spaces</li>
	 * <li>2.read list of bean aoAccountsAllocationBeanList one by one</li>
	 * <li>3.prepare key from this bean and check with previous key</li>
	 * <li>4.If key is same then within same bean assign amount to different fy
	 * <li>
	 * <li>5.Else create new bean of type AccountsAllocationBean
	 * </ul>
	 * 
	 * @param aoAccountsAllocationBeanList list of AccountsAllocationBean
	 * @param asContractStartFY contract start fiscal year
	 * @return List<AccountsAllocationBean> list of type AccountsAllocationBean
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	private List<AccountsAllocationBean> createCoADetailsList(
			List<AccountsAllocationBean> aoAccountsAllocationBeanList, String asContractStartFY)
			throws ApplicationException
	{
		// initializing the component of chart of account allocation from
		// loAccAllocReadBean
		// An Agency user must enter the Chart of Accounts (CoA)i.e variable
		// lsCoa and the
		// fiscal year dollar amounts for the Contract. Unit of
		// Appropriation (UoA), Budget Code (BC), and Object Code (OC) field
		// is required. Sub-Object Code (SubOC)i.e variable lsSuboc
		// and Reporting Category (RC)i.e variable lsRc
		// are optional fields. Id is the complete chart of allocation value.
		String lsPrevKey = null;
		String lsCurrentKey = HHSConstants.EMPTY_STRING;
		String lsNewFYFiscalYearId = HHSConstants.EMPTY_STRING;
		String lsCoa = HHSConstants.EMPTY_STRING;
		String lsId = HHSConstants.EMPTY_STRING;
		String lsUnitOfAppropriation = HHSConstants.EMPTY_STRING;
		String lsBudgetCode = HHSConstants.EMPTY_STRING;
		String lsObjectCode = HHSConstants.EMPTY_STRING;
		String lsRc = HHSConstants.EMPTY_STRING;
		String lsSuboc = HHSConstants.EMPTY_STRING;
		String lsAmount = HHSConstants.EMPTY_STRING;
		int liFYNum = HHSConstants.INT_ZERO;
		AccountsAllocationBean loAccAllocReadBean = null;
		AccountsAllocationBean loAccAllocWriteBean = null;
		ArrayList<AccountsAllocationBean> loWriteBeanList = new ArrayList<AccountsAllocationBean>();
		try
		{
			int liContractStartFY = Integer.parseInt(asContractStartFY);
			checkStartFiscalYearIsProper(aoAccountsAllocationBeanList, liContractStartFY);
			Iterator<AccountsAllocationBean> loItr = aoAccountsAllocationBeanList.iterator();
			while (loItr.hasNext())
			{
				liFYNum++;
				loAccAllocReadBean = (AccountsAllocationBean) loItr.next();
				lsCurrentKey = HHSConstants.EMPTY_STRING;

				// getting the component of chart of account allocation from
				// loAccAllocReadBean
				lsUnitOfAppropriation = loAccAllocReadBean.getUnitOfAppropriation();
				lsBudgetCode = loAccAllocReadBean.getBudgetCode();
				lsObjectCode = loAccAllocReadBean.getObjectCode();
				lsId = loAccAllocReadBean.getId();
				lsCoa = lsUnitOfAppropriation.concat(HHSConstants.HYPHEN).concat(lsBudgetCode)
						.concat(HHSConstants.HYPHEN).concat(lsObjectCode);
				lsRc = loAccAllocReadBean.getRc();
				lsSuboc = loAccAllocReadBean.getSubOc();
				lsAmount = loAccAllocReadBean.getAmmount();
				lsNewFYFiscalYearId = loAccAllocReadBean.getNewFYFiscalYearId();
				Date loModDate = loAccAllocReadBean.getCreatedDate();
				lsCurrentKey = lsCurrentKey.concat(lsCoa).concat(lsRc).concat(lsSuboc);
				if (null == lsPrevKey)
				{
					loAccAllocWriteBean = new AccountsAllocationBean();
					liFYNum = HHSConstants.INT_ONE;
					while (liContractStartFY != Integer.parseInt(loAccAllocReadBean.getFiscalYear()))
					{
						liContractStartFY++;
						liFYNum++;
					}
					setAccAllocWriteBean(lsCoa, lsId, lsRc, lsSuboc, lsAmount, liFYNum, loAccAllocWriteBean, loModDate,
							lsNewFYFiscalYearId);
				}
				else if (!lsPrevKey.equalsIgnoreCase(lsCurrentKey))
				{
					loWriteBeanList.add(loAccAllocWriteBean);
					loAccAllocWriteBean = new AccountsAllocationBean();
					liContractStartFY = Integer.parseInt(asContractStartFY);
					liFYNum = HHSConstants.INT_ONE;
					while (liContractStartFY != Integer.parseInt(loAccAllocReadBean.getFiscalYear()))
					{
						liContractStartFY++;
						liFYNum++;
					}
					setAccAllocWriteBean(lsCoa, lsId, lsRc, lsSuboc, lsAmount, liFYNum, loAccAllocWriteBean, loModDate,
							lsNewFYFiscalYearId);
				}
				else if (lsPrevKey.equalsIgnoreCase(lsCurrentKey))
				{
					while (liContractStartFY != Integer.parseInt(loAccAllocReadBean.getFiscalYear()))
					{
						liContractStartFY++;
						liFYNum++;
					}
					setFYAmountInBean(loAccAllocWriteBean, liFYNum, lsAmount);
				}
				liContractStartFY++;
				lsPrevKey = lsCurrentKey;
			}
			sortAccountsAllocationBean(loAccAllocWriteBean, loWriteBeanList);
		}
		// ApplicationException is thrown from method sortAccountsAllocationBean
		// ,setFYAmountInBean,setAccAllocWriteBean and
		// checkStartFiscalYearIsProper
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ConfigurationService: createCoADetailsList method:: ", loAppEx);
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: createCoADetailsList method:: ", loEx);
			throw loAppEx;
		}
		return loWriteBeanList;
	}

	/**
	 * <p>
	 * This is private method used to check if any data issue is present. Like
	 * records are present before start of fiscal year This check prevents
	 * createCoADetailsList method to go into infinite loop
	 * </p>
	 * 
	 * @param aoAccountsAllocationBeanList List of AccountsAllocationBean Object
	 * @param aiContractStartFY Contract start fiscal year
	 * @throws ApplicationException ApplicationException object
	 */
	private void checkStartFiscalYearIsProper(List<AccountsAllocationBean> aoAccountsAllocationBeanList,
			int aiContractStartFY) throws ApplicationException
	{
		if (null != aoAccountsAllocationBeanList && aoAccountsAllocationBeanList.size() > HHSConstants.INT_ZERO)
		{
			int liRecordFiscalYear = Integer.parseInt(aoAccountsAllocationBeanList.get(HHSConstants.INT_ZERO)
					.getFiscalYear());
			if (aiContractStartFY > liRecordFiscalYear)
			{
				throw new ApplicationException("Data issue: Records are present before "
						+ "start of fiscal year: aiContractStartFY::" + aiContractStartFY + "liRecordFiscalYear"
						+ liRecordFiscalYear);
			}
		}
	}

	/**
	 * <p>
	 * This is private method used to sort Account Allocation Bean based upon
	 * modified date
	 * </p>
	 * 
	 * @param aoAccAllocWriteBean AccountsAllocationBean Object
	 * @param aoWriteBeanList AccountsAllocationBean Object
	 * @throws ApplicationException ApplicationException object
	 */
	private void sortAccountsAllocationBean(AccountsAllocationBean aoAccAllocWriteBean,
			ArrayList<AccountsAllocationBean> aoWriteBeanList) throws ApplicationException
	{
		if (null != aoAccAllocWriteBean)
		{
			aoWriteBeanList.add(aoAccAllocWriteBean);
		}
		Collections.sort(aoWriteBeanList, new Comparator<AccountsAllocationBean>()
		{
			@Override
			public int compare(AccountsAllocationBean aoAccountsAllocationBean1,
					AccountsAllocationBean aoAccountsAllocationBean2)
			{
				return aoAccountsAllocationBean2.getCreatedDate().compareTo(aoAccountsAllocationBean1.getCreatedDate());
			}
		});
	}

	/**
	 * 
	 * Changed method - Build: 3.1.0 Enhancement id: 6020 Added db call to fetch
	 * budget start year for a contract, in order to fetch subbudget details
	 * 
	 * Gets sub budget details of a budget for current contract fiscal year
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>SUB_BUDGET_NAME</li>
	 * <li>SUB_BUDGET_AMOUNT</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of asBudgetId and asFiscalYearId, the above mentioned
	 * values are received from the DataBase by executing the
	 * <code>fetchContractSubBudgetDetails1</code> query in the BudgetMapper</li>
	 * <li>It returns the values as ContractBudgetBean object</li>
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the UI</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * Query Id 'fetchContractConfSubBudgetDetails1'
	 * @param aoMybatisSession SqlSession
	 * @param asContractId on the basis of which Contract Sub Budgets will be
	 *            fetched
	 * @param asFiscalYearId contract current fiscal year (may be Contract's
	 *            first FY if first FY is greater than Current FY)
	 * @return ContractBudgetBean Bean
	 * @throws ApplicationException ApplicationException Object
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<ContractBudgetBean> fetchContractConfSubBudgetDetails1(SqlSession aoMybatisSession,
			String asContractId, String asFiscalYearId) throws ApplicationException
	{
		List<ContractBudgetBean> loContractBudgetBean = null;
		Map<String, String> loBudgetInfo = new HashMap<String, String>();
		try
		{
			asFiscalYearId = getBudgetFiscalYearId(aoMybatisSession, asContractId, asFiscalYearId);

			loBudgetInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			loBudgetInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);

			loContractBudgetBean = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_FETCH_CONTRACT_CONF_SUBBUDGET_DETAILS1, HHSConstants.JAVA_UTIL_MAP);
			setMoState("Sub Budget details for contract fetched successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting contract sub budget details :");
			throw loExp;
		}

		return loContractBudgetBean;
	}

	/**
	 * Changed method - Build: 3.1.0 Enhancement id: 6020 Added db call to fetch
	 * budget start year for a contract
	 * @param aoMybatisSession sql session as input
	 * @param asContractId contract id as input
	 * @param asFiscalYearId fiscal year id as input
	 * @return asFiscalYearId budget start year id
	 * @throws ApplicationException ApplicationException object
	 */
	public String getBudgetFiscalYearId(SqlSession aoMybatisSession, String asContractId, String asFiscalYearId)
			throws ApplicationException
	{
		// start Build 3.1.0, enhancement id: 6020, get budget start year
		CBGridBean loCBGridBean = new CBGridBean();
		try
		{
			loCBGridBean.setContractID(asContractId);
			HashMap<String, Object> loContractDetailMap = (HashMap<String, Object>) DAOUtil.masterDAO(aoMybatisSession,
					loCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.BMC_FETCH_CONTRACT_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			asFiscalYearId = (null != ((BigDecimal) loContractDetailMap.get(HHSR5Constants.BUDGET_START_YEAR))) ? ((BigDecimal) loContractDetailMap
					.get(HHSR5Constants.BUDGET_START_YEAR)).toString() : asFiscalYearId;
			// end
		}
		catch (ApplicationException aoAppExp)
		{
			aoAppExp.addContextData(HHSConstants.CONTRACTID, asContractId);
			LOG_OBJECT.Error("Exception occured in ConfigurationService: getBudgetFiscalYearId method:: ", aoAppExp);
			setMoState("Exception occured in ConfigurationService: getBudgetFiscalYearId method::" + aoAppExp);
			throw aoAppExp;
		}
		return asFiscalYearId;
	}

	/**
	 * This method gets active/approved budget details of a budget for a
	 * particular contract fiscal year
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>BUDGET_ID</li>
	 * <li>BUDGET_AMOUNT</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of asBudgetId and asFiscalYearId, the above mentioned
	 * values are received from the DataBase by executing the
	 * <code>fetchBudgetDetailsActiveOrApproved</code> query in the
	 * ConfigurationMapper</li>
	 * <li>It returns the values as ContractBudgetBean object</li>
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the UI</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asContractId on the basis of which Contract Sub Budgets will be
	 *            fetched
	 * @param asFiscalYearId contract current fiscal year (may be Contract's
	 *            first FY if first FY is greater than Current FY)
	 * @return ContractBudgetBean Bean
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<ContractBudgetBean> fetchBudgetDetailsActiveOrApproved(SqlSession aoMybatisSession,
			String asContractId, String asFiscalYearId) throws ApplicationException
	{
		List<ContractBudgetBean> loContractBudgetBean = null;
		Map<String, String> loBudgetInfo = new HashMap<String, String>();
		try
		{
			loBudgetInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			loBudgetInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);

			loContractBudgetBean = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_FETCH_ACTIVE_APPROVED_BUDGET_DETAILS, HHSConstants.JAVA_UTIL_MAP);
			setMoState("Budget details for contract fetched successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting contract budget details in fetchBudgetDetailsActiveOrApproved() method :");
			throw loExp;
		}
		return loContractBudgetBean;
	}

	/**
	 * Release 3.14.0 This method gets active/approved budget details of a
	 * budget for a particular contract fiscal year
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>BUDGET_ID</li>
	 * <li>BUDGET_AMOUNT</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of asBudgetId and asFiscalYearId, the above mentioned
	 * values are received from the DataBase by executing the
	 * <code>fetchBudgetDetailsActiveOrApproved</code> query in the
	 * ConfigurationMapper</li>
	 * <li>It returns the values as ContractBudgetBean object</li>
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the UI</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asContractId on the basis of which Contract Sub Budgets will be
	 *            fetched
	 * @param asFiscalYearId contract current fiscal year (may be Contract's
	 *            first FY if first FY is greater than Current FY)
	 * @return ContractBudgetBean Bean
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<ContractBudgetBean> getNextNewFYBudgetDetails(SqlSession aoMybatisSession, String asContractId,
			String asFiscalYearId, CBGridBean aoCBGridBean) throws ApplicationException
	{
		List<ContractBudgetBean> loContractBudgetBean = null;
		Map<String, String> loBudgetInfo = new HashMap<String, String>();
		try
		{
			loBudgetInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			loBudgetInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);

			ContractBudgetBean aoContractBudgetBean = new ContractBudgetBean();
			loContractBudgetBean = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_FETCH_NEXT_NEW_FY_BUDGET_DETAILS,
					HHSConstants.JAVA_UTIL_MAP);
			if (loContractBudgetBean == null || loContractBudgetBean.isEmpty())
			{
				HashMap<String, Object> loContractDetailMap = (HashMap<String, Object>) DAOUtil.masterDAO(
						aoMybatisSession, aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.BMC_FETCH_CONTRACT_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				// insert into base budget and sub budget for its amendment
				// entry
				aoContractBudgetBean.setPlannedAmount(ApplicationConstants.ZERO);
				
				populateContractBudgetBean(aoContractBudgetBean, loContractDetailMap, aoCBGridBean, asFiscalYearId);
				
				insertNewBudgetDetails(aoMybatisSession, aoContractBudgetBean);
				aoContractBudgetBean.setBudgetId(checkIfBudgetExists(aoMybatisSession,
						aoContractBudgetBean.getContractId(), asFiscalYearId));
				copyPreviousFYSubBudgetToCurrentFY(aoMybatisSession, aoCBGridBean.getContractID(), asFiscalYearId,
						aoContractBudgetBean.getBudgetId(), aoCBGridBean.getModifyByAgency());
				copyPreviousCBCToCurrentCBC(aoMybatisSession, aoCBGridBean.getContractID(), asFiscalYearId,
						aoContractBudgetBean.getBudgetId(), aoCBGridBean.getModifyByAgency());
				loContractBudgetBean = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_FETCH_NEXT_NEW_FY_BUDGET_DETAILS, HHSConstants.JAVA_UTIL_MAP);
			}

			setMoState("Budget details for contract fetched successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting contract budget details in getNextNewFYBudgetDetails() method :");
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error while getting contract budget details in getNextNewFYBudgetDetails() method :");
			throw new ApplicationException(
					"Error while getting contract budget details in getNextNewFYBudgetDetails() method :", loExp);
		}
		return loContractBudgetBean;
	}

	/**
	 * This method gets active/approved budget details of a budget for a
	 * particular contract fiscal year
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>BUDGET_ID</li>
	 * <li>BUDGET_AMOUNT</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of asBudgetId and asFiscalYearId, the above mentioned
	 * values are received from the DataBase by executing the
	 * <code>fetchBudgetDetailsActiveOrApproved</code> query in the
	 * ConfigurationMapper</li>
	 * <li>It returns the values as ContractBudgetBean object</li>
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the UI</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asContractId on the basis of which Contract Sub Budgets will be
	 *            fetched
	 * @param asFiscalYearId contract current fiscal year (may be Contract's
	 *            first FY if first FY is greater than Current FY)
	 * @return ContractBudgetBean Bean
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public Integer getLastConfiguredFYBudgetYear(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		Integer liLastConfiguredFY = null;
		try
		{
			liLastConfiguredFY = (Integer) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_LAST_FY_CONFIGURED_AND_NEXT_NEW_FY,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Budget details for contract fetched successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting contract budget details in fetchBudgetDetailsActiveOrApproved() method :");
			throw loExp;
		}
		return liLastConfiguredFY;
	}

	/**
	 * This method gets budget details of a budget for a particular contract
	 * fiscal year
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>BUDGET_ID</li>
	 * <li>BUDGET_AMOUNT</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of asBudgetId and asFiscalYearId, the above mentioned
	 * values are received from the DataBase by executing the
	 * <code>fetchBudgetDetailsByFYAndContractId</code> query in the
	 * ConfigurationMapper</li>
	 * <li>It returns the values as ContractBudgetBean object</li>
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the UI</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asContractId on the basis of which Contract Sub Budgets will be
	 *            fetched
	 * @param asFiscalYearId contract current fiscal year (may be Contract's
	 *            first FY if first FY is greater than Current FY)
	 * @return ContractBudgetBean Bean
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<ContractBudgetBean> fetchBudgetDetailsByFYAndContractId(SqlSession aoMybatisSession,
			String asContractId, String asFiscalYearId) throws ApplicationException
	{
		List<ContractBudgetBean> loContractBudgetBean = null;
		Map<String, String> loBudgetInfo = new HashMap<String, String>();
		try
		{
			loBudgetInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			loBudgetInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);

			loContractBudgetBean = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_FETCH_BUDGET_DETAILS,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Budget details for contract fetched successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting contract budget details in fetchBudgetDetailsByFYAndContractId() method :");
			throw loExp;
		}
		return loContractBudgetBean;
	}

	/**
	 * This method gets budget details of a budget for a particular amendment
	 * fiscal year<br>
	 * Method Updated in R4
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>BUDGET_ID</li>
	 * <li>BUDGET_AMOUNT</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of asBudgetId and asFiscalYearId, the above mentioned
	 * values are received from the DataBase by executing the
	 * <code>fetchBudgetDetailsByFYAndContractId</code> query in the
	 * ConfigurationMapper</li>
	 * <li>It returns the values as ContractBudgetBean object</li>
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the UI</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession Object
	 * @param asAmendmentContractId on the basis of which Contract Sub Budgets
	 *            will be fetched
	 * @param asFiscalYearId contract current fiscal year (may be Contract's
	 *            first FY if first FY is greater than Current FY)
	 * @param asContractTypeId contract type Id
	 * @return List of ContractBudgetBean Bean
	 * @throws ApplicationException ApplicationException object
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<ContractBudgetBean> fetchAmendmentBudgetDetails(SqlSession aoMybatisSession,
			String asAmendmentContractId, String asFiscalYearId, String asContractTypeId) throws ApplicationException
	{
		List<ContractBudgetBean> loContractBudgetBean = null;
		Map<String, String> loBudgetInfo = new HashMap<String, String>();
		try
		{
			loBudgetInfo.put(HHSConstants.AMENDED_CONTRACT_ID, asAmendmentContractId);
			loBudgetInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);
			loBudgetInfo.put(HHSConstants.CONTRACT_TYPE_ID, asContractTypeId);

			loContractBudgetBean = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_FETCH_AMENDMENT_BUDGET_DETAILS,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Budget details for contract fetched successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting contract budget details in fetchBudgetDetailsByFYAndContractId() method :");
			throw loExp;
		}
		return loContractBudgetBean;
	}

	/**
	 * This method is used to insert the Sub budget details for a particular
	 * fiscal year of a contract in DataBase.
	 * 
	 * <ul>
	 * <li>Calls Transaction id :
	 * <code>contractConfigurationSubBudgetGridAdd</code></li>
	 * </ul>
	 * Query Id 'insertContractConfSubBudgetDetails'
	 * 
	 * Query Id 'insertContractConfSubBudgetDetailsWithParentId'
	 * @param aoMybatisSession - MyBatis Session
	 * @param aoSubBudgetBean - a ContractBudgetBean object
	 * @return Boolean insert Status
	 * @throws ApplicationException - ApplicationException object thrown
	 * 
	 * 
	 */
	public Boolean insertContractConfSubBudgetDetails(SqlSession aoMybatisSession, ContractBudgetBean aoSubBudgetBean)
			throws ApplicationException
	{
		try
		{
			if (aoSubBudgetBean != null)
			{
				if (null == aoSubBudgetBean.getParentId() || aoSubBudgetBean.getParentId().isEmpty())
				{
					DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.CS_INSERT_CONTRACT_CONF_SUBBUDGET_DETAILS,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
					setMoState("Sub Budget Details for contract updated successfully:");
				}
				else
				{
					DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.CS_INSERT_CONTRACT_CONF_SUBBUDGET_DETAILS_WITH_PARENT,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
					setMoState("Sub Budget Details for contract updated successfully:");
				}
			}
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while inserting Sub Budget details of contract:");
			throw loExp;
		}
		return true;
	}

	/**
	 * This method is used to insert the Sub budget details for a particular new
	 * fiscal year of a contract in DataBase. Query Id
	 * 'insertContractConfSubBudgetDetails'
	 * 
	 * @param aoMybatisSession - MyBatis Session
	 * @param aoSubBudgetBean - a ContractBudgetBean object
	 * @return Boolean insert Status
	 * @throws ApplicationException - ApplicationException object thrown
	 * 
	 * 
	 */
	public Boolean insertNewFiscalYearSubBudgetDetails(SqlSession aoMybatisSession, ContractBudgetBean aoSubBudgetBean)
			throws ApplicationException
	{
		try
		{
			if (aoSubBudgetBean != null)
			{
				DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_INSERT_CONTRACT_CONF_SUBBUDGET_DETAILS,
						HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
				setMoState("Sub Budget Details for contract updated successfully:");

			}
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while inserting Sub Budget details of contract:");
			throw loExp;
		}
		return true;
	}

	/**
	 * This method is used to insert the Budget details for a particular fiscal
	 * year of a contract in DataBase. Query Id 'insertNewBudgetDetails'
	 * <ul>
	 * <li>Calls Transaction id : <code>insertNewBudgetDetails</code></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoContractBudgetBean - a ContractBudgetBean object
	 * @return Boolean
	 * @throws ApplicationException ApplicationException Object
	 * 
	 * 
	 */
	public Boolean insertNewBudgetDetails(SqlSession aoMybatisSession, ContractBudgetBean aoContractBudgetBean)
			throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.BMC_INSERT_NEW_BUDGET_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			setMoState("Budget Details for contract inserted successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while Inserting Budget details of contract:");
			throw loExp;
		}
		return true;
	}

	/**
	 * This method is used to insert the Budget details for a particular fiscal
	 * year of an amendment in DataBase.
	 * 
	 * <ul>
	 * <li>Calls Transaction id : <code>insertNewBudgetDetails</code></li>
	 * <li>Query Id 'insertNewUpdateBudgetDetails' is executed</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession Object
	 * @param aoContractBudgetBean - a ContractBudgetBean Object
	 * @return Boolean
	 * @throws ApplicationException ApplicationException Object
	 * 
	 * 
	 */
	public Boolean insertNewUpdateBudgetDetails(SqlSession aoMybatisSession, ContractBudgetBean aoContractBudgetBean)
			throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.BMC_INSERT_NEW_UPDATE_BUDGET_DETAILS,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			setMoState("Budget Details for contract inserted successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while Inserting Budget details of contract:");
			throw loExp;
		}
		return true;
	}

	/**
	 * This method is used to insert the Budget details for a particular fiscal
	 * year of an amendment in DataBase.
	 * 
	 * <ul>
	 * <li>Calls Transaction id : <code>insertNewBudgetDetails</code></li>
	 * </ul>
	 * Query Id 'insertNewAmendmentBudgetDetails'
	 * @param aoMybatisSession SqlSession
	 * @param aoContractBudgetBean - a ContractBudgetBean object
	 * @return Boolean
	 * @throws ApplicationException ApplicationException Object
	 * 
	 * 
	 */
	public Boolean insertNewAmendmentBudgetDetails(SqlSession aoMybatisSession, ContractBudgetBean aoContractBudgetBean)
			throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.BMC_INSERT_NEW_AMENDMENT_BUDGET_DETAILS,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			setMoState("Budget Details for contract inserted successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while Inserting Budget details of contract:");
			throw loExp;
		}
		return true;
	}

	/**
	 * This method is used to update the Budget details (FY Amount) for a
	 * particular Budget
	 * 
	 * <ul>
	 * <li>Calls Transaction id : <code>insertNewBudgetDetails</code></li>
	 * </ul>
	 * Query Id 'updateBudgetFYTotalBudgetAmount'
	 * @param aoMybatisSession SqlSession
	 * @param aoContractBudgetBean - ContractBudgetBean object
	 * @return Boolean
	 * @throws ApplicationException ApplicationException Object
	 * 
	 * 
	 */
	public Boolean updateBudgetFYTotalBudgetAmount(SqlSession aoMybatisSession, ContractBudgetBean aoContractBudgetBean)
			throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.BMC_UPDATE_BUDGET_FY_TOTAL_BUDGET_AMOUNT,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			setMoState("Budget Details for contract updated successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while Updating Budget details of contract:");
			throw loExp;
		}
		return true;
	}

	/**
	 * This method is used to edit the sub budget details for a fiscal year of a
	 * contract using <code>editContractConfSubBudgetDetails</code> Update
	 * query</li> <li>
	 * Transaction id : <code>editContractConfSubBudgetDetails</code></li>
	 * 
	 * @param aoMybatisSession - MyBatisSession
	 * @param aoSubBudgetBean - SubBudget Bean
	 * @return Boolean update Status
	 * @throws ApplicationException - ApplicationException Object thrown
	 * 
	 */
	public Boolean editContractConfSubBudgetDetails(SqlSession aoMybatisSession, ContractBudgetBean aoSubBudgetBean)
			throws ApplicationException
 {
		try {
			if (aoSubBudgetBean != null) {
				// Removing "_new_row" appender, appended for distinguishing new
				// row
				// for New FY Contract Budget
				if (null == aoSubBudgetBean.getParentId()
						|| aoSubBudgetBean.getParentId().isEmpty()) {
					String[] loTempIdArray = aoSubBudgetBean.getId().split(
							HHSConstants.UNDERSCORE);
					aoSubBudgetBean.setId(loTempIdArray[HHSConstants.INT_ZERO]);
				}
				// Updating pre-existing line-items dollar value in Sub_budget
				// table
				if (null == aoSubBudgetBean.getSubbudgetName()
						|| aoSubBudgetBean.getSubbudgetName().isEmpty()) {
					DAOUtil.masterDAO(
							aoMybatisSession,
							aoSubBudgetBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.CS_EDIT_CONTRACT_CONF_SUBBUDGET_DETAILS1,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
				} else {
					DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.EDIT_CONTRACT_CONF_SUB_BUDGET_DETAILS,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);

					// Defect 6596 for Release 7 starts - will update amendment
					// sub budget name
					// Defect 8831 for Release 7 starts - updating sub budget name for Multiple Amendments
					List<String> loBudgetList = new ArrayList<String>();
					String lsBudgetId = null;

					loBudgetList = (List<String>) DAOUtil
							.masterDAO(
									aoMybatisSession,
									aoSubBudgetBean,
									HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
									HHSR5Constants.FETCH_BUDGET_LISTS_DETAILS,
									HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
					Iterator loItr = loBudgetList.iterator();
					while (loItr.hasNext()) {
						lsBudgetId = (String) loItr.next();
						aoSubBudgetBean.setBudgetId(lsBudgetId);
						DAOUtil.masterDAO(
								aoMybatisSession,
								aoSubBudgetBean,
								HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.UPDATE_SUBBUDGET_NAME,
								HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
					}
					// Defect 8831 for Release 7 ends
				}
				setMoState("Sub Budget Details for contract updated successfully:");
			}
		} catch (ApplicationException loExp) {
			setMoState("Error while updating Sub Budget details of contract:");
			throw loExp;
		}
		return true;
	}

	/**
	 * This method is used to delete the Sub budget details from DataBase on the
	 * basis of sub budget Id using <code>delContractConfSubBudgetDetails</code>
	 * Delete query
	 * 
	 * <li>
	 * Transaction id : <code>contractConfigurationSubBudgetGridDel</code></li>
	 * 
	 * @param aoMybatisSession - MyBatisSession
	 * @param aoSubBudgetBean - SubBudget Bean
	 * @return Boolean deleted Status
	 * @throws ApplicationException - ApplicationException object thrown
	 * 
	 */
	public Boolean delContractConfSubBudgetDetails(SqlSession aoMybatisSession, ContractBudgetBean aoSubBudgetBean)
			throws ApplicationException
	{
		boolean lbUpdateStatus = Boolean.TRUE;
		boolean lbError = false;
		try
		{
			if (aoSubBudgetBean != null)
			{
				Integer loSubBugdetExistingAmendment = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.SUB_BUDGET_EXISTING_AMENDMENT_ERROR_CHECK,
						HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);

				if (loSubBugdetExistingAmendment != null && loSubBugdetExistingAmendment > 0)
				{
					lbError = true;
					// throwing ApplicationException
					throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGE_PROP_FILE,
							HHSConstants.SUB_BUDGET_EXISTING_AMENDMENT_ERROR_CHECK_MESSAGE));
				}
				// Removing "_new_row" appender, appended for distinguishing new
				// row for New FY Contract Budget
				if (null == aoSubBudgetBean.getParentId() || aoSubBudgetBean.getParentId().isEmpty())
				{
					String[] loTempIdArray = aoSubBudgetBean.getId().split(HHSConstants.UNDERSCORE);
					aoSubBudgetBean.setId(loTempIdArray[HHSConstants.INT_ZERO]);
				}
				checkAndDeleteLineItemDefaultEntries(aoMybatisSession, aoSubBudgetBean);
				DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.DEL_CONTRACT_CONF_SUB_BUDGET_DETAILS,
						HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
				setMoState("Sub Budget Details for contract deleted successfully:");
			}
		}
		// ApplicationException is thrown while executing query
		catch (ApplicationException loExp)
		{
			if (lbError)
			{
				loExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loExp.toString());
			}
			loExp.addContextData("Error while deleting Sub Budget details" + " of contract:", loExp.toString());
			setMoState("Error while deleting Sub Budget details of contract:");
			LOG_OBJECT.Error("ApplicationException while deleting Sub Budget details of contract:"
					+ " Method - delContractConfSubBudgetDetails", loExp);
			throw loExp;
		}
		// ApplicationException is thrown while executing query
		catch (Exception loExcep)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error while deleting Sub Budget details of contract:", loExcep);
			loAppEx.addContextData("Error while deleting Sub Budget details" + " of contract:", loAppEx.toString());
			setMoState("Error while deleting Sub Budget details of contract:");
			LOG_OBJECT.Error("Exception while deleting Sub Budget details of contract:"
					+ " Method - delContractConfSubBudgetDetails", loExcep);
			throw loAppEx;
		}
		return lbUpdateStatus;
	}

	// s390 end

	/**
	 * Once the WF is launched successfully on click of submit button at S 204
	 * Financials screen.
	 * 
	 * The status is changed to "In Review" Query Id 'setPCOFStatus'
	 * @param aoCbGridBean CBGridBean
	 * @param aoMyBatisSession SqlSession
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	public void setProcurementCOFStatus(Boolean aoRowsStatus, CBGridBean aoCbGridBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		if (aoRowsStatus)
		{
			try
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoCbGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_SET_PCOF_STATUS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			}

			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT.Error("Exception occured while executing query setPCOFStatus ", aoAppEx);
				throw aoAppEx;
			}
		}
	}

	/**
	 * Gets account allocation details for a contract
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>UoA Code - unitOfAppropriation</li>
	 * <li>Budget Code - budgetCode</li>
	 * <li>Object Code - objectCode</li>
	 * <li>Sub Object Code - subOc</li>
	 * <li>Reporting Category - rc</li>
	 * <li>Fiscal Year - fiscalYear</li>
	 * <li>Amount for fiscal year - total</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of contractId, the above mentioned values are received
	 * from the DataBase by executing the
	 * <code>fetchContractConfCOADetails</code> query in the ConfigurationMapper
	 * </li>
	 * <li>It returns the values as list AccountAllocationBean object</li>
	 * <li>This list in turn is passed to createCoADetailsList method to further
	 * format as per grid input format</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param aoCBGridBean CBGridBean object
	 * @param aoMybatisSession SqlSession object
	 * @return List<AccountsAllocationBean> list of type AccountsAllocationBean
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<AccountsAllocationBean> fetchContractConfigGridCurr(CBGridBean aoCBGridBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		List<AccountsAllocationBean> loAccountsAllocationBeanList = null;
		List<AccountsAllocationBean> loAccountsAllocationBeanRtrndCurrentList = null;
		try
		{
			loAccountsAllocationBeanList = (List<AccountsAllocationBean>) DAOUtil.masterDAO(aoMybatisSession,
					aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.FETCH_CONTRACT_CONF_COA_CURR_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			loAccountsAllocationBeanRtrndCurrentList = createCoADetailsList(loAccountsAllocationBeanList,
					aoCBGridBean.getFiscalYearID());
			setMoState("Account Allocation Details for contract fetched successfully:");
		}
		// ApplicationException is thrown while executing query and method
		// createCoADetailsList
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AO_CB_GRID_BEAN, CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchContractConfigGridCurr method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchContractConfigGridCurr method - failed."
					+ " Exception occured while fetching all Chart of account details for CBGridBean::"
					+ CommonUtil.convertBeanToString(aoCBGridBean) + " \n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: fetchContractConfigGridCurr:: ", loEx);
			loAppEx.addContextData(HHSConstants.AO_CB_GRID_BEAN, CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchContractConfigGridCurr method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchContractConfigGridCurr method - failed."
					+ " Exception occured while fetching all Chart of account details for CBGridBean::"
					+ CommonUtil.convertBeanToString(aoCBGridBean) + " \n");
			throw loAppEx;
		}
		return loAccountsAllocationBeanRtrndCurrentList;
	}

	/**
	 * Gets account allocation details for an amendment
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>UoA Code - unitOfAppropriation</li>
	 * <li>Budget Code - budgetCode</li>
	 * <li>Object Code - objectCode</li>
	 * <li>Sub Object Code - subOc</li>
	 * <li>Reporting Category - rc</li>
	 * <li>Fiscal Year - fiscalYear</li>
	 * <li>Amount for fiscal year - total</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of contractId, the above mentioned values are received
	 * from the DataBase by executing the
	 * <code>fetchContractConfCOADetails</code> query in the BudgetMapper</li>
	 * <li>It returns the values as AccountAllocationBean object</li>
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the UI</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param aoCBGridBean - CBGridBean object
	 * @param aoMybatisSession SqlSession object
	 * @return List<AccountsAllocationBean> list of type AccountsAllocationBean
	 * @throws ApplicationException ApplicationException object
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public List<AccountsAllocationBean> fetchContractConfAmendmentDetails(CBGridBean aoCBGridBean,
			SqlSession aoMybatisSession) throws ApplicationException, ParseException
	{
		List<AccountsAllocationBean> loAccountsAllocationBeanList = null;
		List<AccountsAllocationBean> loContractConfAmendList = null;
		try
		{
			loAccountsAllocationBeanList = (List<AccountsAllocationBean>) DAOUtil.masterDAO(aoMybatisSession,
					aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_FETCH_CONTRACT_CONF_AMENDMENT_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			loContractConfAmendList = createCoADetailsList(loAccountsAllocationBeanList, aoCBGridBean.getFiscalYearID());

			setMoState("Account Allocation Details for amendment fetched successfully:");
		}
		// ApplicationException is thrown while executing query and from method
		// createCoADetailsList
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AO_CB_GRID_BEAN, CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchContractConfAmendmentDetails method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchContractConfAmendmentDetails method - failed."
					+ " Exception occured while fetching all Chart of account details for CBGridBean::"
					+ CommonUtil.convertBeanToString(aoCBGridBean) + " \n");
			throw loAppEx;
		}

		return loContractConfAmendList;
	}

	/**
	 * This method is used to edit the Chart of Accounts details in the
	 * DataBase.
	 * 
	 * <ul>
	 * <li>One or more fields of below list can be edited in DataBase using
	 * <code>editContractConfCOADetails</code> Update query</li>
	 * <li>The fields which can be edited in the table are -</li>
	 * <ul>
	 * <li>UNIT_OF_APPROPRIATION</li>
	 * <li>BUDGET_CODE</li>
	 * <li>OBJECT_CODE</li>
	 * <li>SUB_OBJECT_CODE</li>
	 * <li>REPORTING_CATEGORY</li>
	 * <li>AMOUNT</li>
	 * </ul>
	 * </ul>
	 * 
	 * <li>
	 * Transaction id : <code>contractConfigurationAccountGridEdit</code></li>
	 * Query Id 'editContractConfAmendmentDetails'
	 * 
	 * <br>
	 * This method was updated in R4.
	 * 
	 * @param aoAccountsAllocationBean - AccountAllocation Bean Object
	 * @param aoMybatisSession - MyBatisSession Object
	 * @throws ApplicationException - Exception object
	 * 
	 * */
	public void editContractConfAmendmentDetails(AccountsAllocationBean aoAccountsAllocationBean,
			SqlSession aoMybatisSession) throws ApplicationException
	{

		boolean lbError = false;
		boolean lbAmendmentSubBudgetError = false;
		Map<String, Object> loReturnMap = null;
		boolean lbNewRow = false;
		try
		{
			if (aoAccountsAllocationBean.getId().indexOf(HHSConstants.NEW_RECORD) != -1)
			{
				lbNewRow = true;
			}
			aoAccountsAllocationBean.setId(aoAccountsAllocationBean.getId().replace(HHSConstants.NEW_RECORD,
					HHSConstants.EMPTY_STRING));
			int liStartFY = Integer.parseInt(aoAccountsAllocationBean.getContractStartFY());
			int liEndFY = Integer.parseInt(aoAccountsAllocationBean.getContractEndFY());
			int liCounter = HHSConstants.INT_ONE;
			int liRowsUpdated = HHSConstants.INT_ZERO;
			for (int liCount = liStartFY; liCount <= liEndFY; liCount++)
			{
				String lsBeanFY = HHSConstants.SMALL_FY + liCounter;
				aoAccountsAllocationBean.setFiscalYearID(String.valueOf(liCount));
				String lsAmt = (String) BeanUtils.getProperty(aoAccountsAllocationBean, lsBeanFY);
				aoAccountsAllocationBean.setAmmount(lsAmt);
				loReturnMap = validateAmendmentAmount(aoMybatisSession, aoAccountsAllocationBean);
				if (!(Boolean) loReturnMap.get(HHSConstants.LB_ERROR))
				{
					lbAmendmentSubBudgetError = validateAmendmentBudgetZeroValue(aoAccountsAllocationBean,
							aoMybatisSession);
					if (!lbAmendmentSubBudgetError)
					{
						if (lbNewRow)
						{
							liRowsUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
									HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
									HHSConstants.CS_EDIT_CONTRACT_CONF_AMEND_TASK_DETAILS,
									HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
						}
						else
						{
							liRowsUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
									HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
									HHSConstants.CS_EDIT_CONTRACT_CONF_AMEND_TASK_DETAILS_OLD,
									HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
						}
						if (liRowsUpdated == HHSConstants.INT_ZERO)
						{
							aoAccountsAllocationBean.setActiveFlag(HHSConstants.ONE);
							aoAccountsAllocationBean.setFiscalYearID(String.valueOf(liCount));
							DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
									HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
									HHSConstants.CS_ADD_CONTRACT_CONF_AMEND_TASK_DETAILS,
									HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
						}
						liCounter++;
					}
					else
					{
						throw new ApplicationException(PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								HHSConstants.AMEND_BUDGET_ZERO_VALUE_ERROR_MSG));
					}
				}
				else
				{
					lbError = true;
					throw new ApplicationException((String) loReturnMap.get(HHSConstants.LS_ERROR_CONSTANT));
				}
			}
		}
		// ApplicationException is thrown while executing query
		catch (ApplicationException loAppEx)
		{
			if (lbError)
			{
				loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loAppEx.toString());
				LOG_OBJECT.Error("Updated Chart of Accounts value cannot be below the YTD Invoiced Amount for any fiscal year. "
								+ loAppEx);
				setMoState("Updated Chart of Accounts value cannot be below the YTD Invoiced Amount for any fiscal year.");
			}
			else if (lbAmendmentSubBudgetError)
			{
				loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loAppEx.toString());
				LOG_OBJECT.Error("validation failed: ConfigurationService editContractConfAmendmentDetails" + loAppEx);
				setMoState("validation failed: ConfigurationService editContractConfAmendmentDetails.");
			}
			else
			{
				loAppEx.addContextData(HHSConstants.AO_ACCOUNTS_ALLOCATION_BEAN,
						CommonUtil.convertBeanToString(aoAccountsAllocationBean));
				LOG_OBJECT.Error("Exception occured in ConfigurationService: editContractConfCOADetails method:: ",
						loAppEx);
				setMoState("Transaction Failed:: ConfigurationService: editContractConfCOADetails method - failed."
						+ " Exception occured while editing chart of allocation record for AccountsAllocationBean::"
						+ CommonUtil.convertBeanToString(aoAccountsAllocationBean) + " \n");
			}
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: editContractConfCOADetails:: ", loEx);
			loAppEx.addContextData(HHSConstants.AO_ACCOUNTS_ALLOCATION_BEAN,
					CommonUtil.convertBeanToString(aoAccountsAllocationBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: editContractConfCOADetails method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: editContractConfCOADetails method - failed."
					+ " Exception occured while editing record in Chart of allocation for AccountsAllocationBean::"
					+ CommonUtil.convertBeanToString(aoAccountsAllocationBean) + " \n");
			throw loAppEx;
		}
		setMoState("editContractConfCOADetails is executed successfully");
	}

	/**
	 * This method is used to insert the Chart of Accounts details in the
	 * DataBase.
	 * 
	 * <ul>
	 * <li>A list of fields are sent to the DataBase using
	 * <code>insertContractConfCOADetails</code> insert query</li>
	 * <li>The fields inserted in the table are -</li>
	 * <ul>
	 * <li>UNIT_OF_APPROPRIATION</li>
	 * <li>BUDGET_CODE</li>
	 * <li>OBJECT_CODE</li>
	 * <li>SUB_OBJECT_CODE</li>
	 * <li>REPORTING_CATEGORY</li>
	 * </ul>
	 * </ul>
	 * 
	 * @param aoAccountsAllocationBean AccountsAllocationBean object
	 * @param aoMybatisSession SqlSession object
	 * @throws ApplicationException Exception thrown in case of any error
	 */
	public void addContractConfAmendmentDetails(AccountsAllocationBean aoAccountsAllocationBean,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbError = false;
		try
		{
			boolean lbRowAlreadyExists = checkDeletedRowIsAddedAgain(aoAccountsAllocationBean, aoMybatisSession);
			if (lbRowAlreadyExists)
			{
				aoAccountsAllocationBean.setDeletedRecord(true);
				editContractConfAmendmentDetails(aoAccountsAllocationBean, aoMybatisSession);
			}
			else
			{

				int liStartFY = Integer.parseInt(aoAccountsAllocationBean.getContractStartFY());
				int liEndFY = Integer.parseInt(aoAccountsAllocationBean.getContractEndFY());
				int liCounter = HHSConstants.INT_ONE;

				Map<String, Object> loReturnMap = null;

				for (int liCount = liStartFY; liCount <= liEndFY; liCount++)
				{
					String lsBeanFY = HHSConstants.SMALL_FY + liCounter;
					aoAccountsAllocationBean.setActiveFlag(HHSConstants.ONE);
					aoAccountsAllocationBean.setFiscalYearID(String.valueOf(liCount));
					String lsAmt = (String) BeanUtils.getProperty(aoAccountsAllocationBean, lsBeanFY);
					aoAccountsAllocationBean.setAmmount(lsAmt);
					loReturnMap = validateAmendmentAmount(aoMybatisSession, aoAccountsAllocationBean);
					if (!(Boolean) loReturnMap.get(HHSConstants.LB_ERROR))
					{
						DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
								HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.CS_ADD_CONTRACT_CONF_AMEND_TASK_DETAILS,
								HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
						liCounter++;
					}
					else
					{
						lbError = true;
						throw new ApplicationException((String) loReturnMap.get(HHSConstants.LS_ERROR_CONSTANT));
					}
				}
			}
		}
		// ApplicationException is thrown while executing query
		catch (ApplicationException loAppEx)
		{
			if (lbError)
			{
				loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loAppEx.toString());
				LOG_OBJECT.Error("Updated Chart of Accounts value cannot be below the YTD Invoiced Amount for any fiscal year. "
								+ loAppEx);
				setMoState("Updated Chart of Accounts value cannot be below the YTD Invoiced Amount for any fiscal year.");
			}
			else
			{
				loAppEx.addContextData(HHSConstants.AO_ACCOUNTS_ALLOCATION_BEAN,
						CommonUtil.convertBeanToString(aoAccountsAllocationBean));
				LOG_OBJECT.Error("Exception occured in ConfigurationService: addContractConfCOADetails method:: ",
						loAppEx);
				setMoState("Transaction Failed:: ConfigurationService: addContractConfCOADetails method - failed."
						+ " Exception occured while inserting new record in Chart of allocation for AccountsAllocationBean::"
						+ CommonUtil.convertBeanToString(aoAccountsAllocationBean) + " \n");
			}
			throw loAppEx;
		}
		// Exception is thrown while executing query
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: addContractConfCOADetails:: ", loEx);
			loAppEx.addContextData(HHSConstants.AO_ACCOUNTS_ALLOCATION_BEAN,
					CommonUtil.convertBeanToString(aoAccountsAllocationBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: addContractConfCOADetails method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: addContractConfCOADetails method - failed."
					+ " Exception occured while inserting new record in Chart of allocation for AccountsAllocationBean::"
					+ CommonUtil.convertBeanToString(aoAccountsAllocationBean) + " \n");
			throw loAppEx;
		}

		setMoState("addContractConfCOADetails is executed successfully");
	}

	/**
	 * This method updated to modify error message updated as a part of release
	 * 3.12.0 enhancement 6631
	 * 
	 * This method validates the amendment amount in amendment chart of accounts
	 * for a particular line-item and returns FALSE if the #amount user is
	 * trying to update tries to make the total amount for a line-item to fall
	 * below zero, else it validates it as TRUE
	 * <ul>
	 * <li>Query Id 'validateAmendmentAmount' is executed</li>
	 * <li>Query Id 'fetchAmendmentAmountExceptChangedOne' is executed</li>
	 * <li>Query Id 'fetchFiscalYearAmount' is executed</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param aoAccountsAllocationBean AccountsAllocationBean object
	 * @return Map<String, Object> Object
	 * @throws Exception ApplicationException object
	 * 
	 */
	private Map<String, Object> validateAmendmentAmount(SqlSession aoMybatisSession,
			AccountsAllocationBean aoAccountsAllocationBean) throws Exception
	{
		Map<String, Object> loReturnMap = new HashMap<String, Object>();
		loReturnMap.put(HHSConstants.LB_ERROR, false);
		loReturnMap.put(HHSConstants.LS_ERROR_CONSTANT, HHSConstants.EMPTY_STRING);

		// Validation for positive amendment (For Positive Amendment only.)
		if (aoAccountsAllocationBean.getAmendmentType().equalsIgnoreCase(HHSConstants.POSITIVE)
				&& new BigDecimal(aoAccountsAllocationBean.getAmmount()).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO)
		{
			loReturnMap.put(HHSConstants.LB_ERROR, true);
			loReturnMap.put(HHSConstants.LS_ERROR_CONSTANT, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.POSITIVE_AMENDMENT_ERROR_MESSAGE));

		}
		// Validation for negative amendment (For Negative Amendment only)
		else if (aoAccountsAllocationBean.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE)
				&& new BigDecimal(aoAccountsAllocationBean.getAmmount()).compareTo(BigDecimal.ZERO) > HHSConstants.INT_ZERO)
		{
			loReturnMap.put(HHSConstants.LB_ERROR, true);
			loReturnMap.put(HHSConstants.LS_ERROR_CONSTANT, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.NEGATIVE_AMENDMENT_ERROR_MESSAGE));
		}

		else if (aoAccountsAllocationBean.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE))
		{
			BigDecimal lsInvoicedAmount = null;
			BigDecimal lsAmendmentAmount = null;
			BigDecimal lsFiscalYearAllocatedAmt = null;
			lsInvoicedAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.VALIDATE_AMENDMENT_AMOUNT,
					HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
			lsInvoicedAmount = ((null == lsInvoicedAmount) ? new BigDecimal(HHSConstants.INT_ZERO) : lsInvoicedAmount);
			lsAmendmentAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.FETCH_AMENDMENT_AMOUNT_EXCEPT_CHANGED_ONE, HHSConstants.ACCOUNTS_ALLOCATION_BEAN);

			lsAmendmentAmount = ((null == lsAmendmentAmount) ? new BigDecimal(HHSConstants.INT_ZERO)
					: lsAmendmentAmount);

			lsFiscalYearAllocatedAmt = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_FISCAL_YEAR_AMOUNT,
					HHSConstants.ACCOUNTS_ALLOCATION_BEAN);

			lsFiscalYearAllocatedAmt = ((null == lsFiscalYearAllocatedAmt) ? new BigDecimal(HHSConstants.INT_ZERO)
					: lsFiscalYearAllocatedAmt);
			// check amendment amount should not be less than total invoiced
			// amount
			if (null != lsInvoicedAmount
					&& (((lsFiscalYearAllocatedAmt.add(lsAmendmentAmount).add(new BigDecimal(aoAccountsAllocationBean
							.getAmmount()))).compareTo(lsInvoicedAmount)) < HHSConstants.INT_ZERO))
			{
				loReturnMap.put(HHSConstants.LB_ERROR, true);
				// error message updated as a part of release 3.12.0 enhancement
				// 6631
				loReturnMap.put(HHSConstants.LS_ERROR_CONSTANT, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.ERROR_NEGATIVE_AMENDMENT_VALIDATION_MESSAGE_1));
			}
		}
		return loReturnMap;
	}

	/**
	 * This method is used to delete the Chart of Accounts details in the
	 * database on the basis chart of allocation, sub object code and reporting
	 * category.
	 * <ul>
	 * <li>Soft delete is made disabling active flag to zero</li>
	 * <li>Query Id 'delContractConfAmendTaskDetails' is executed</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoAccountsAllocationBean AccountsAllocationBean object
	 * @param aoMybatisSession SqlSession object
	 * @throws ApplicationException Exception thrown in case of any error
	 * 
	 * 
	 */
	public void delContractConfAmendmentDetails(AccountsAllocationBean aoAccountsAllocationBean,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		try
		{
			aoAccountsAllocationBean.setId(aoAccountsAllocationBean.getId().replace(HHSConstants.NEW_RECORD,
					HHSConstants.EMPTY_STRING));
			DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.DEL_CONTRACT_CONF_AMEND_TASK_DETAILS,
					HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
			setMoState("Chart of Account Allocation Details for contract deleted successfully:");
		}
		// ApplicationException is thrown while executing query
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AO_ACCOUNTS_ALLOCATION_BEAN,
					CommonUtil.convertBeanToString(aoAccountsAllocationBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: delContractConfCOADetails method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: delContractConfCOADetails method - failed."
					+ " Exception occured while deleting chart of allocation record for AccountsAllocationBean::"
					+ CommonUtil.convertBeanToString(aoAccountsAllocationBean) + " \n");
			throw loAppEx;
		}
	}

	/**
	 * Gets account allocation details for a contract
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>UoA Code - unitOfAppropriation</li>
	 * <li>Budget Code - budgetCode</li>
	 * <li>Object Code - objectCode</li>
	 * <li>Sub Object Code - subOc</li>
	 * <li>Reporting Category - rc</li>
	 * <li>Fiscal Year - fiscalYear</li>
	 * <li>Amount for fiscal year - total</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of contractId, the above mentioned values are received
	 * from the DataBase by executing the
	 * <code>fetchContractConfCOADetails</code> query in the BudgetMapper</li>
	 * <li>It returns the values as AccountAllocationBean object</li>
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the UI</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param aoCBGridBean - CBGridBean object
	 * @param aoMybatisSession SqlSession object
	 * @return List<AccountsAllocationBean> list of type AccountsAllocationBean
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<AccountsAllocationBean> fetchContractConfUpdateDetails(CBGridBean aoCBGridBean,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		List<AccountsAllocationBean> loAccountsAllocationBeanOrgRecords = null;
		List<AccountsAllocationBean> loAccountsAllocationBeanNewRecords = null;
		List<AccountsAllocationBean> loAccountsAllocationBeanRtrndList = null;
		try
		{
			loAccountsAllocationBeanNewRecords = (List<AccountsAllocationBean>) DAOUtil.masterDAO(aoMybatisSession,
					aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_FETCH_CONTRACT_CONF_UPDATE_NEW_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			loAccountsAllocationBeanOrgRecords = (List<AccountsAllocationBean>) DAOUtil.masterDAO(aoMybatisSession,
					aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_FETCH_CONTRACT_CONF_UPDATE_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			loAccountsAllocationBeanNewRecords.addAll(loAccountsAllocationBeanOrgRecords);
			loAccountsAllocationBeanRtrndList = createCoADetailsList(loAccountsAllocationBeanNewRecords,
					aoCBGridBean.getFiscalYearID());

			setMoState("Account Allocation Details for contract fetched successfully:");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in BudgetManagementService: fetchContractConfUpdateDetails method:: ",
					loAppEx);
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "BudgetManagementService: fetchContractConfUpdateDetails method:: ", loEx);
			throw loAppEx;
		}
		return loAccountsAllocationBeanRtrndList;
	}

	/**
	 * This method is used to insert the Chart of Accounts details in the
	 * DataBase.
	 * 
	 * <ul>
	 * <li>A list of fields are sent to the DataBase using
	 * <code>insertContractConfCOADetails</code> insert query</li>
	 * <li>The fields inserted in the table are -</li>
	 * <ul>
	 * <li>UNIT_OF_APPROPRIATION</li>
	 * <li>BUDGET_CODE</li>
	 * <li>OBJECT_CODE</li>
	 * <li>SUB_OBJECT_CODE</li>
	 * <li>REPORTING_CATEGORY</li>
	 * </ul>
	 * </ul>
	 * 
	 * <li>
	 * Transaction id : <code>contractConfigurationAccountGridAdd</code></li>
	 * 
	 * @param aoAccountsAllocationBean - AccountsAllocationBean object
	 * @param aoMybatisSession - SqlSession object
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public void addContractConfUpdateTaskDetails(AccountsAllocationBean aoAccountsAllocationBean,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbError = false;
		try
		{
			boolean lbRowAlreadyExists = checkDeletedRowIsAddedAgain(aoAccountsAllocationBean, aoMybatisSession);
			if (lbRowAlreadyExists)
			{
				aoAccountsAllocationBean.setDeletedRecord(true);
				editContractConfUpdateDetails(aoAccountsAllocationBean, aoMybatisSession);
			}
			else
			{

				int liStartFY = Integer.parseInt(aoAccountsAllocationBean.getContractStartFY());
				int liEndFY = Integer.parseInt(aoAccountsAllocationBean.getContractEndFY());
				// made changes for 6601 11jan
				List<String> loFiscalYearList = (List<String>) DAOUtil
						.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
								HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.GET_PENDING_AMENDMENT_BUDGET_FISCAL_YEAR_ID,
								HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
				if (loFiscalYearList != null && !loFiscalYearList.isEmpty())
				{
					for (String lsFiscalYearId : loFiscalYearList)
					{
						int liCounterPeningAmendment = HHSConstants.INT_ONE;
						for (int liCount = liStartFY; liCount <= liEndFY; liCount++)
						{
							Integer loBeanFY = liStartFY + liCounterPeningAmendment - 1;
							String lsBeanFY = HHSConstants.SMALL_FY + liCounterPeningAmendment;
							aoAccountsAllocationBean.setFiscalYearID(String.valueOf(liCount));
							String lsAmt = (String) BeanUtils.getProperty(aoAccountsAllocationBean, lsBeanFY);
							if (loBeanFY != null && loBeanFY == (Integer.parseInt(lsFiscalYearId)))
							{
								String lsAmount = (String) DAOUtil.masterDAO(aoMybatisSession,
										aoAccountsAllocationBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
										HHSConstants.GET_CONTRACT_FINANCIALS_UPDATE_AMOUNT,
										HHSConstants.ACCOUNTS_ALLOCATION_BEAN);

								if (!(lsAmount != null && lsAmt != null && new BigDecimal(lsAmt)
										.compareTo(new BigDecimal(lsAmount)) == 0))
								{
									lbError = true;
									throw new ApplicationException(PropertyLoader.getProperty(
											HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
											HHSConstants.PENDING_AMENDMENT_VALIDATION_MESSAGE));
								}

							}
							liCounterPeningAmendment++;
						}
					}
				}
				// made changes for 6601 11jan
				int liCounter = HHSConstants.INT_ONE;
				for (int liCount = liStartFY; liCount <= liEndFY; liCount++)
				{
					String lsBeanFY = HHSConstants.SMALL_FY + liCounter;
					aoAccountsAllocationBean.setActiveFlag(HHSConstants.ONE);

					aoAccountsAllocationBean.setFiscalYearID(String.valueOf(liCount));
					String lsAmt = (String) BeanUtils.getProperty(aoAccountsAllocationBean, lsBeanFY);
					aoAccountsAllocationBean.setAmmount(lsAmt);
					DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.CS_ADD_CONTRACT_CONF_UPDATE_TASK_DETAILS,
							HHSConstants.ACCOUNTS_ALLOCATION_BEAN);

					liCounter++;
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			if (lbError)
			{
				loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loAppEx.toString());
				setMoState("Updated Chart of Accounts value cannot be below the YTD Invoiced Amount for any fiscal year.");
			}
			else
			{
				loAppEx.addContextData(HHSConstants.AO_ACCOUNTS_ALLOCATION_BEAN,
						CommonUtil.convertBeanToString(aoAccountsAllocationBean));
				LOG_OBJECT.Error(
						"Exception occured in ConfigurationService: addContractConfUpdateTaskDetails method:: ",
						loAppEx);
				setMoState("Transaction Failed:: ConfigurationService: addContractConfUpdateTaskDetails method - failed."
						+ " Exception occured while inserting new record in Chart of allocation for AccountsAllocationBean::"
						+ CommonUtil.convertBeanToString(aoAccountsAllocationBean) + " \n");
			}
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: addContractConfUpdateTaskDetails:: ", loEx);
			loAppEx.addContextData(HHSConstants.AO_ACCOUNTS_ALLOCATION_BEAN,
					CommonUtil.convertBeanToString(aoAccountsAllocationBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: addContractConfUpdateTaskDetails method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: addContractConfUpdateTaskDetails method - failed."
					+ " Exception occured while inserting new record in Chart of allocation for AccountsAllocationBean::"
					+ CommonUtil.convertBeanToString(aoAccountsAllocationBean) + " \n");
			throw loAppEx;
		}

	}

	/**
	 * This method is used to edit the Chart of Accounts details in the
	 * DataBase.
	 * 
	 * <ul>
	 * <li>One or more fields of below list can be edited in DataBase using
	 * <code>editContractConfCOADetails</code> Update query</li>
	 * <li>The fields which can be edited in the table are -</li>
	 * <ul>
	 * <li>UNIT_OF_APPROPRIATION</li>
	 * <li>BUDGET_CODE</li>
	 * <li>OBJECT_CODE</li>
	 * <li>SUB_OBJECT_CODE</li>
	 * <li>REPORTING_CATEGORY</li>
	 * <li>AMOUNT</li>
	 * </ul>
	 * </ul>
	 * 
	 * <li>
	 * Transaction id : <code>contractConfigurationAccountGridEdit</code></li>
	 * 
	 * @param aoAccountsAllocationBean - AccountAllocation Bean Object
	 * @param aoMybatisSession - MyBatisSession Object
	 * @throws ApplicationException - Exception object
	 */
	@SuppressWarnings("unchecked")
	public void editContractConfUpdateDetails(AccountsAllocationBean aoAccountsAllocationBean,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbError = false;
		Map<String, Object> loReturnMap = new HashMap<String, Object>();
		try
		{
			if (aoAccountsAllocationBean != null
					&& aoAccountsAllocationBean.getId().indexOf(HHSConstants.NEW_RECORD) != -1)
			{
				String[] loTempIdArray = aoAccountsAllocationBean.getId().split(HHSConstants.UNDERSCORE);
				aoAccountsAllocationBean.setId(loTempIdArray[0]);
			}
			int liStartFY = 0;
			if (null != aoAccountsAllocationBean && null != aoAccountsAllocationBean.getContractStartFY())
			{
				liStartFY = Integer.parseInt(aoAccountsAllocationBean.getContractStartFY());
			}
			int liEndFY = 0;
			if (null != aoAccountsAllocationBean && null != aoAccountsAllocationBean.getContractEndFY())
			{
				liEndFY = Integer.parseInt(aoAccountsAllocationBean.getContractEndFY());
			}

			// made changes for 6601 11jan

			List<String> loFiscalYearList = (List<String>) DAOUtil.masterDAO(aoMybatisSession,
					aoAccountsAllocationBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.GET_PENDING_AMENDMENT_BUDGET_FISCAL_YEAR_ID, HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
			if (loFiscalYearList != null && !loFiscalYearList.isEmpty())
			{
				for (String lsFiscalYearId : loFiscalYearList)
				{
					int liCounterPeningAmendment = HHSConstants.INT_ONE;
					for (int liCount = liStartFY; liCount <= liEndFY; liCount++)
					{
						Integer loBeanFY = liStartFY + liCounterPeningAmendment - 1;
						String lsBeanFY = HHSConstants.SMALL_FY + liCounterPeningAmendment;

						aoAccountsAllocationBean.setFiscalYearID(String.valueOf(liCount));
						String lsAmt = (String) BeanUtils.getProperty(aoAccountsAllocationBean, lsBeanFY);
						if (loBeanFY != null && loBeanFY == (Integer.parseInt(lsFiscalYearId)))
						{
							String lsAmount = (String) DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
									HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
									HHSConstants.GET_CONTRACT_FINANCIALS_UPDATE_AMOUNT,
									HHSConstants.ACCOUNTS_ALLOCATION_BEAN);

							if (!(lsAmount != null && lsAmt != null && new BigDecimal(lsAmt).compareTo(new BigDecimal(
									lsAmount)) == 0))
							{
								lbError = true;
								throw new ApplicationException(PropertyLoader.getProperty(
										HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
										HHSConstants.PENDING_AMENDMENT_VALIDATION_MESSAGE));
							}

						}
						liCounterPeningAmendment++;
					}
				}
			}
			// made changes for 6601 11jan
			int liCounter = HHSConstants.INT_ONE;

			for (int liCount = liStartFY; liCount <= liEndFY; liCount++)
			{
				String lsBeanFY = HHSConstants.SMALL_FY + liCounter;

				aoAccountsAllocationBean.setFiscalYearID(String.valueOf(liCount));
				String lsAmt = (String) BeanUtils.getProperty(aoAccountsAllocationBean, lsBeanFY);
				aoAccountsAllocationBean.setAmmount(lsAmt);

				loReturnMap = validateUpdateAmount(aoMybatisSession, aoAccountsAllocationBean);
				if (!(Boolean) loReturnMap.get(HHSConstants.LB_ERROR))
				{
					DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.CS_EDIT_CONTRACT_CONF_UPDATE_TASK_DETAILS,
							HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
					liCounter++;
				}
				else
				{
					lbError = true;
					throw new ApplicationException((String) loReturnMap.get(HHSConstants.LS_ERROR_CONSTANT));
				}

			}
		}
		catch (ApplicationException loAppEx)
		{
			if (lbError)
			{
				loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loAppEx.toString());
				setMoState("Updated Chart of Accounts value cannot be below the YTD Invoiced Amount for any fiscal year.");
			}
			else
			{
				loAppEx.addContextData(HHSConstants.AO_ACCOUNTS_ALLOCATION_BEAN,
						CommonUtil.convertBeanToString(aoAccountsAllocationBean));
				LOG_OBJECT.Error("Exception occured in ConfigurationService: editContractConfUpdateDetails method:: ",
						loAppEx);
				setMoState("Transaction Failed:: ConfigurationService: editContractConfUpdateDetails method - failed."
						+ " Exception occured while editing a record in Chart of allocation for AccountsAllocationBean::"
						+ CommonUtil.convertBeanToString(aoAccountsAllocationBean) + " \n");
			}
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: addContractConfUpdateTaskDetails:: ", loEx);
			loAppEx.addContextData(HHSConstants.AO_ACCOUNTS_ALLOCATION_BEAN,
					CommonUtil.convertBeanToString(aoAccountsAllocationBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: editContractConfUpdateDetails method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: addContractConfUpdateTaskDetails method - failed."
					+ " Exception occured while editing a record in Chart of allocation for AccountsAllocationBean::"
					+ CommonUtil.convertBeanToString(aoAccountsAllocationBean) + " \n");
			throw loAppEx;
		}

	}

	/**
	 * This method validates the update amount in update chart of accounts for a
	 * particular line-item and returns FALSE if the #amount user is trying to
	 * update tries to make the total amount for a line-item to fall below zero,
	 * else it validates it as TRUE
	 * 
	 * Query Id 'validateAmendmentAmount'
	 * 
	 * Query Id 'fetchFiscalYearAmount'
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param aoAccountsAllocationBean AccountsAllocationBean object
	 * @throws Exception ApplicationException object
	 * 
	 * 
	 */
	private Map<String, Object> validateUpdateAmount(SqlSession aoMybatisSession,
			AccountsAllocationBean aoAccountsAllocationBean) throws Exception
	{
		Map<String, Object> loReturnMap = new HashMap<String, Object>();
		loReturnMap.put(HHSConstants.LB_ERROR, false);
		loReturnMap.put(HHSConstants.LS_ERROR_CONSTANT, HHSConstants.EMPTY_STRING);

		BigDecimal lsInvoicedAmount = null;
		BigDecimal lsFiscalYearAllocatedAmt = null;
		lsInvoicedAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
				HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.VALIDATE_AMENDMENT_AMOUNT,
				HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
		// Start enhancement 6414 release 3.10.0
		lsFiscalYearAllocatedAmt = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
				HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_FISCAL_YEAR_AMOUNT_UPDATE_CONF,
				HHSConstants.ACCOUNTS_ALLOCATION_BEAN);

		if (null != lsInvoicedAmount
				&& (((lsFiscalYearAllocatedAmt.add(new BigDecimal(aoAccountsAllocationBean.getAmmount())))
						.compareTo(lsInvoicedAmount)) < HHSConstants.INT_ZERO))
		{
			loReturnMap.put(HHSConstants.LB_ERROR, true);
			loReturnMap.put(HHSConstants.LS_ERROR_CONSTANT, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.NEGATIVE_AMENDMENT_VALIDATION_MESSAGE));
		}
		// End enhancement 6414 release 3.10.0
		return loReturnMap;
	}

	/**
	 * Gets Procurement and Contract Details related to Contract<br>
	 * This method updated in R4.
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>Estimated Procurement Value - procurementValue</li>
	 * <li>Contract Value - contractValue</li>
	 * <li>Contract Start Date - contractStartDate</li>
	 * <li>Contract End Date - contractEndDate</li>
	 * <li>Certification of Funds Status - procurementStatus</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of contractId, the above mentioned values are received
	 * from the DataBase by executing the
	 * <code>fetchProcurementCONDetails</code> query in the procurementMapper</li>
	 * <li>It returns the values as ProcurementCOF Bean object</li>
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the contractFinancials.jsp</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @see BMCController
	 * @param aoMybatisSession SqlSession
	 * @param aoHashMap an hashmap on the basis of which Procurement and
	 *            Contract details will be fetched
	 * @return ProcurementCOF Bean
	 * @throws ApplicationException If an ApplicationException occurs
	 * 
	 */
	public ProcurementCOF fetchUpdateConfigurationDetails(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		ProcurementCOF loProcurementCOF = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.CONTRACT_ID_KEY, aoHashMap);

		LOG_OBJECT.Debug("Entered into getting ProcurementCOF Details for amendment:" + loContextDataMap.toString());
		try
		{

			loProcurementCOF = (ProcurementCOF) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_UPDATE_CONFIGURATION_DETAILS,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			setMoState("ProcurementCON details fetched successfully for amendment Contract Id:" + aoHashMap);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting ProcurementCOF Details for amendment");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting ProcurementCOF Details for amendment", loExp);
			throw loExp;
		}
		return loProcurementCOF;
	}

	/**
	 * Gets Procurement and Contract Details related to Contract
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>Estimated Procurement Value - procurementValue</li>
	 * <li>Contract Value - contractValue</li>
	 * <li>Contract Start Date - contractStartDate</li>
	 * <li>Contract End Date - contractEndDate</li>
	 * <li>Certification of Funds Status - procurementStatus</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of contractId, the above mentioned values are received
	 * from the DataBase by executing the
	 * <code>fetchProcurementCONDetails</code> query in the procurementMapper</li>
	 * <li>It returns the values as ProcurementCOF Bean object</li>
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the contractFinancials.jsp</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @see BMCController
	 * @param aoMybatisSession SqlSession
	 * @param aoHashMap an hashmap on the basis of which Procurement and
	 *            Contract details will be fetched
	 * @return ProcurementCOF Bean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public ProcurementCOF fetchContractAmendmentConfigurationDetails(SqlSession aoMybatisSession,
			HashMap<String, String> aoHashMap) throws ApplicationException
	{
		ProcurementCOF loProcurementCOF = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.CONTRACT_ID_KEY, aoHashMap);

		LOG_OBJECT.Debug("Entered into getting ProcurementCOF Details for amendment:" + loContextDataMap.toString());
		try
		{

			loProcurementCOF = (ProcurementCOF) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_AMENDMENT_DETAILS,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			setMoState("ProcurementCON details fetched successfully for amendment Contract Id:" + aoHashMap);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting ProcurementCOF Details for amendment");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting ProcurementCOF Details for amendment", loExp);
			throw loExp;
		}
		return loProcurementCOF;
	}

	/**
	 * This method is used to update the Chart of Accounts details in the
	 * DataBase.
	 * 
	 * <ul>
	 * <li>One or more fields of below list can be edited in DataBase using
	 * <code>updateFetchedContractDetails</code> Update query</li>
	 * <li>The fields which can be edited in the table are -</li>
	 * <ul>
	 * <li>contract_id,</li>
	 * <li>contract_type_id</li>
	 * <li>agency_id</li>
	 * <li>parent_contract_id</li>
	 * <li>REPORTING_CATEGORY</li>
	 * <li>AMOUNT</li>
	 * </ul>
	 * </ul>
	 * 
	 * <li>
	 * Transaction id : <code>fetchUpdatedConfigurationDetails</code></li>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asContractId String
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public void updateFetchedContractDetails(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		// call master dao to fetch records
		List<ContractFinancialBean> loContractFinancialBeanList = null;
		try
		{
			String lsUpdatedContractId = (String) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_CHECK_UPDATE_CONTRACT_DETAILS,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			if (lsUpdatedContractId == null)
			{
				loContractFinancialBeanList = (List<ContractFinancialBean>) DAOUtil.masterDAO(aoMybatisSession,
						aoHashMap.get(HHSConstants.CONTRACT_ID), HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_FETCHED_CONTRACT_DETAILS, HHSConstants.JAVA_LANG_STRING);

				if (loContractFinancialBeanList != null && loContractFinancialBeanList.size() == HHSConstants.INT_ONE)
				{
					Iterator<ContractFinancialBean> loIter = loContractFinancialBeanList.iterator();

					while (loIter.hasNext())
					{
						ContractFinancialBean loContractFinancialBean = loIter.next();
						loContractFinancialBean.setContractTypeId(HHSConstants.FOUR);
						loContractFinancialBean.setDeleteFlag(HHSConstants.STRING_ZERO);
						loContractFinancialBean.setDiscrepancyFlag(HHSConstants.STRING_ZERO);
						loContractFinancialBean.setStatusId(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_CONTRACT_PENDING_CONFIGURATION));
						Integer loInsertCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loContractFinancialBean,
								HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.CS_INSERT_FETCHED_CONTRACT_DETAILS,
								HHSConstants.CS_CONTRACT_FINANCIAL_BEAN);
						LOG_OBJECT.Debug("Row inserted into contract Table for Update " + loInsertCount);
					}
					updateFetchedContractFinancialDetails(aoMybatisSession, aoHashMap.get(HHSConstants.CONTRACT_ID));

				}
			}
		}
		catch (ApplicationException loAppEx)
		{

			LOG_OBJECT.Error("Exception occured in ConfigurationService: updateFetchedContractDetails method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: editContractConfUpdateDetails method - failed."
					+ " Exception occured while updating a record in Chart of allocation for AccountsAllocationBean::"
					+ " \n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: addContractConfUpdateTaskDetails:: ", loEx);

			LOG_OBJECT.Error("Exception occured in ConfigurationService: editContractConfUpdateDetails method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: editContractConfUpdateDetails method - failed."
					+ " Exception occured while editing a record in Chart of allocation for AccountsAllocationBean::"
					+ " \n");
			throw loAppEx;
		}

	}
	/**
	 * This method is used to update fetched contract financials details
	 * @param aoMybatisSession a SqlSession object
	 * @param asContractId 
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private void updateFetchedContractFinancialDetails(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		List<ContractFinancialBean> loContractFinancialBeanList = null;
		try
		{
			loContractFinancialBeanList = (List<ContractFinancialBean>) DAOUtil.masterDAO(aoMybatisSession,
					asContractId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_FETCH_CONTRACT_FINANCIAL_DETAILS, HHSConstants.JAVA_LANG_STRING);
			if (loContractFinancialBeanList != null)
			{
				Iterator<ContractFinancialBean> loIter = loContractFinancialBeanList.iterator();

				while (loIter.hasNext())
				{
					ContractFinancialBean loContractFinancialBean = loIter.next();
					loContractFinancialBean.setContractTypeId(HHSConstants.FOUR);
					DAOUtil.masterDAO(aoMybatisSession, loContractFinancialBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.CS_INSERT_FETCH_CONTRACT_FINANCIAL_DETAILS,
							HHSConstants.CS_CONTRACT_FINANCIAL_BEAN);
				}

			}

		}
		catch (ApplicationException loAppEx)
		{

			LOG_OBJECT.Error(
					"Exception occured in ConfigurationService: updateFetchedContractFinancialDetails method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: updateFetchedContractFinancialDetails method - failed."
					+ " Exception occured while updating a record in Chart of allocation for AccountsAllocationBean::"
					+ " \n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: addContractConfUpdateTaskDetails:: ", loEx);

			LOG_OBJECT.Error(
					"Exception occured in ConfigurationService: updateFetchedContractFinancialDetails method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: updateFetchedContractFinancialDetails method - failed."
					+ " Exception occured while editing a record in Chart of allocation for AccountsAllocationBean::"
					+ " \n");
			throw loAppEx;
		}
	}

	/**
	 * Gets all the details of a budget for current contract fiscal year
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>SUB_BUDGET_NAME</li>
	 * <li>SUB_BUDGET_AMOUNT</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of asBudgetId and asFiscalYearId, the above mentioned
	 * values are received from the DataBase by executing the
	 * <code>fetchContractConfUpdateSubBudgetDetails</code> query in the
	 * BudgetMapper</li>
	 * <li>It returns the values as ContractBudgetBean object</li>
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the UI</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * 
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoCBGridBean CBGridBean
	 * @return loAccountsAllocationBeanRtrndList List of AccountsAllocationBean
	 *         Bean
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<AccountsAllocationBean> fetchContractConfUpdateActualDetails(CBGridBean aoCBGridBean,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		List<AccountsAllocationBean> loAccountsAllocationBeanList = null;
		List<AccountsAllocationBean> loAccountsAllocationBeanRtrndList = null;
		try
		{
			loAccountsAllocationBeanList = (List<AccountsAllocationBean>) DAOUtil.masterDAO(aoMybatisSession,
					aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_FETCH_CONTRACT_CONF_UPDATE_ACTUAL_DETAILS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			loAccountsAllocationBeanRtrndList = createCoADetailsList(loAccountsAllocationBeanList,
					aoCBGridBean.getFiscalYearID());
			setMoState("Account Allocation Details for contract fetched successfully:");
		}
		// ApplicationException is thrown while executing query and method
		// createCoADetailsList
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error(
					"Exception occured in ConfigurationService: fetchContractConfUpdateActualDetails method:: ",
					loAppEx);
			throw loAppEx;
		}

		return loAccountsAllocationBeanRtrndList;
	}

	/**
	 * Gets all the distinct fiscal year for current contract id
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>fiscal_year_id</li>
	 * <li></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of asContractId, the above mentioned values are received
	 * from the DataBase by executing the <code>fetchFYAndContractId</code>
	 * query in the BudgetMapper</li>
	 * <li>It returns the list of all fiscal year</li>
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the UI</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * 
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asContractId on the basis of which Contract Sub Budgets will be
	 *            fetched
	 * @return List
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<String> fetchFYAndContractId(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		List<String> loFYIList = null;

		try
		{
			loFYIList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_FETCH_FY_AND_CONTRACT_ID,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Sub Budget details for fiscal year fetched successfully:");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchFYAndContractId method:: ", loAppEx);
			throw loAppEx;
		}

		return loFYIList;
	}

	/**
	 * Release 3.12.0 enhancement 6601 Gets all the distinct fiscal year for
	 * current contract id
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>fiscal_year_id</li>
	 * <li></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of asContractId, the above mentioned values are received
	 * from the DataBase by executing the <code>fetchFYAndContractId</code>
	 * query in the BudgetMapper</li>
	 * <li>It returns the list of all fiscal year</li>
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the UI</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * 
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asContractId on the basis of which Contract Sub Budgets will be
	 *            fetched
	 * @return List
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<String> fetchFYAndContractIdAmendment(SqlSession aoMybatisSession, String asContractId,
			String asAmendmentContractId) throws ApplicationException
	{
		List<String> loFYIList = null;
		Integer loNegativeAmendmentCount = 0;

		try
		{
			// start release 3.14.0
			loNegativeAmendmentCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asAmendmentContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.NEGATIVE_AMENDMENT_COUNT,
					HHSConstants.JAVA_LANG_STRING);

			if (loNegativeAmendmentCount != null && loNegativeAmendmentCount == 0)
			{
				loFYIList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asContractId,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_FETCH_FY_AND_CONTRACT_ID_AMENDMENT, HHSConstants.JAVA_LANG_STRING);
			}
			else
			{
				loFYIList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asContractId,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_FETCH_FY_AND_CONTRACT_ID_AMENDMENT_NEGATIVE, HHSConstants.JAVA_LANG_STRING);
			}
			setMoState("Sub Budget details for fiscal year fetched successfully:");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchFYAndContractId method:: ", loAppEx);
			throw loAppEx;
		}

		return loFYIList;
	}

	/**
	 * Gets sub budget details of a budget for current contract fiscal year<br>
	 * This method updated in R4.
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>SUB_BUDGET_NAME</li>
	 * <li>SUB_BUDGET_AMOUNT</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of asBudgetId and asFiscalYearId, the above mentioned
	 * values are received from the DataBase by executing the
	 * <code>fetchContractConfUpdateSubBudgetDetails</code> query in the
	 * BudgetMapper</li>
	 * <li>It returns the values as ContractBudgetBean object</li>
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the UI</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param asContractId on the basis of which Contract Sub Budgets will be
	 *            fetched
	 * @param asFiscalYearId contract current fiscal year (may be Contract's
	 *            first FY if first FY is greater than Current FY)
	 * @param asBudgetTypeId Budget type id
	 * @param asContractTypeId contract Type Id
	 * @param asAmendContractId amendment contract Id
	 * @return ContractBudgetBean Bean
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<ContractBudgetBean> fetchContractConfUpdateSubBudgetDetails(SqlSession aoMybatisSession,
			String asContractId, String asFiscalYearId, String asBudgetTypeId, String asContractTypeId,
			String asAmendContractId) throws ApplicationException
	{
		List<ContractBudgetBean> loContractBudgetBeanList = null;
		Map<String, String> loBudgetInfo = new HashMap<String, String>();
		// start release 3.14.0
		Boolean loNextNewFY = false;
		// end release 3.14.0
		try
		{
			loBudgetInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			loBudgetInfo.put(HHSConstants.AMENDED_CONTRACT_ID, asAmendContractId);
			loBudgetInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);
			loBudgetInfo.put(HHSConstants.AS_BUDGET_TYPE_ID, asBudgetTypeId);
			loBudgetInfo.put(HHSConstants.CONTRACT_TYPE_ID_KEY, asContractTypeId);

			if (asContractTypeId.equals(HHSConstants.TWO))
			{
				// fetch for amendment module
				// start release 3.14.0
				Integer loLastConfiguredYear = getLastConfiguredFYBudgetYear(aoMybatisSession, asContractId);
				if (null != loLastConfiguredYear && loLastConfiguredYear + 1 == Integer.parseInt(asFiscalYearId))
				{
					loNextNewFY = true;
				}
				// end release 3.14.0
				if (loNextNewFY)
				{
					loContractBudgetBeanList = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession,
							loBudgetInfo, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.CS_FETCH_CONTRACT_CONF_AMEND_SUBBUDGET_DETAILS_NEXT_NEW_FY,
							HHSConstants.JAVA_UTIL_MAP);
				}
				else
				{
					loContractBudgetBeanList = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession,
							loBudgetInfo, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.CS_FETCH_CONTRACT_CONF_AMEND_SUBBUDGET_DETAILS, HHSConstants.JAVA_UTIL_MAP);
				}
			}
			else
			{
				// fetch for update module
				loContractBudgetBeanList = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_FETCH_CONTRACT_CONF_UPDATE_SUBBUDGET_DETAILS, HHSConstants.JAVA_UTIL_MAP);
			}

			if (loContractBudgetBeanList != null)
			{
				// start release 3.14.0
				updateConfDetails(aoMybatisSession, asContractId, asFiscalYearId, asBudgetTypeId, asContractTypeId,
						asAmendContractId, loContractBudgetBeanList, loNextNewFY);
				// end release 3.14.0
			}
			setMoState("Sub Budget details for contract fetched successfully:");
		}
		// ApplicationException is thrown while executing the query
		// Exception is thrown while parsing the string to double and
		// to Integer
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ConfigurationService:"
					+ "fetchContractConfUpdateSubBudgetDetails method:: ", loAppEx);
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: fetchContractConfUpdateSubBudgetDetails method:: ", loEx);
			LOG_OBJECT.Error("ApplicationException occured in ConfigurationService:"
					+ "fetchContractConfUpdateSubBudgetDetails method:: ", loAppEx);

			throw loAppEx;
		}

		return loContractBudgetBeanList;
	}

	/**
	 * @param aoMybatisSession sql session as input
	 * @param asContractId contract id as input
	 * @param asFiscalYearId fiscal year as input
	 * @param asBudgetTypeId budget type id as input
	 * @param asContractTypeId contract type id as input
	 * @param asAmendContractId amend contract id as input
	 * @param loContractBudgetBeanList bean as input
	 * @throws NumberFormatException Exception in case a query fails
	 * @throws ApplicationException Exception in case a query fails
	 */
	private void updateConfDetails(SqlSession aoMybatisSession, String asContractId, String asFiscalYearId,
			String asBudgetTypeId, String asContractTypeId, String asAmendContractId,
			List<ContractBudgetBean> loContractBudgetBeanList, Boolean aoNextNewFY) throws NumberFormatException,
			ApplicationException
	{
		ContractBudgetBean loContractBudgetBean = null;
		Iterator<ContractBudgetBean> loIter = loContractBudgetBeanList.iterator();
		String lsParentId = HHSConstants.EMPTY_STRING;
		String lsInvoiceAmount = HHSConstants.EMPTY_STRING;

		while (loIter.hasNext())
		{
			String loSubBudgetAmount = HHSConstants.ZERO;
			String loSmodifiedAmount = HHSConstants.ZERO;
			BigDecimal loInvoicedAmount = new BigDecimal(HHSConstants.INT_ZERO);
			loContractBudgetBean = loIter.next();
			String lsSubBudgetParentId = null;
			if (loContractBudgetBean.getParentId() != null && loContractBudgetBean.getSubbudgetAmount() != null)
			{
				lsParentId = loContractBudgetBean.getParentId();
				loContractBudgetBean.setContractId(asContractId);
				loContractBudgetBean.setAmendmentContractId(asAmendContractId);
				loContractBudgetBean.setBudgetTypeId(Integer.parseInt(asBudgetTypeId));
				loContractBudgetBean.setContractTypeId(asContractTypeId);
				loContractBudgetBean.setBudgetfiscalYear(asFiscalYearId);
				if (!asContractTypeId.equals(HHSConstants.TWO))
				{
					lsInvoiceAmount = (String) DAOUtil.masterDAO(aoMybatisSession, lsParentId,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_FETCH_INVOICE_AMOUNT,
							HHSConstants.JAVA_LANG_STRING);
					loContractBudgetBean.setInvoiceAmount(lsInvoiceAmount);
					if (lsInvoiceAmount != null && !HHSConstants.EMPTY_STRING.equals(lsInvoiceAmount))
					{
						loInvoicedAmount = new BigDecimal(lsInvoiceAmount);
					}
					loContractBudgetBean.setInvoiceAmount(loInvoicedAmount.toString());

					lsSubBudgetParentId = (String) DAOUtil.masterDAO(aoMybatisSession, loContractBudgetBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_FETCH_SUB_BUDGET_PARENT_ID,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
				}

				else
				{
					lsSubBudgetParentId = (String) DAOUtil.masterDAO(aoMybatisSession, loContractBudgetBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.CS_FETCH_SUB_BUDGET_PARENT_ID_AMENDMENT,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
				}
			}
			// start release 3.14.0
			if ((lsSubBudgetParentId != null && lsParentId != null && lsParentId.equalsIgnoreCase(lsSubBudgetParentId) && loContractBudgetBean
					.getId().equalsIgnoreCase(loContractBudgetBean.getParentId())) || aoNextNewFY)
			{
				// end release 3.14.0
				StringBuffer lsNewSubBudgetId = new StringBuffer(loContractBudgetBean.getId());
				lsNewSubBudgetId.append(HHSConstants.NEW_RECORD);
				loContractBudgetBean.setId(lsNewSubBudgetId.toString());
			}
			if (loContractBudgetBean.getSubbudgetAmount() != null
					&& !HHSConstants.EMPTY_STRING.equals(loContractBudgetBean.getSubbudgetAmount()))
			{
				loSubBudgetAmount = loContractBudgetBean.getSubbudgetAmount();
			}
			else
			{
				loContractBudgetBean.setSubbudgetAmount(HHSConstants.STRING_ZERO);
			}
			if (loContractBudgetBean.getModifiedAmount() != null
					&& !HHSConstants.EMPTY_STRING.equals(loContractBudgetBean.getModifiedAmount()))
			{
				loSmodifiedAmount = loContractBudgetBean.getModifiedAmount();
			}
			loContractBudgetBean.setProposedBudgetAmount(String.valueOf(new BigDecimal(loSubBudgetAmount)
					.add(new BigDecimal(loSmodifiedAmount))));
		}
	}

	/**
	 * This method adds the sub budget for an amendment/update for a particular
	 * contract Query Id 'addContractConfAmendmentBudgetDetails'
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param aoContractBudgetBean ContractBudgetBean object
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void addContractConfUpdateBudgetDetails(SqlSession aoMybatisSession, ContractBudgetBean aoContractBudgetBean)
			throws ApplicationException
	{
		boolean lbError = false;
		try
		{
			if (new BigDecimal(aoContractBudgetBean.getModifiedAmount())
					.compareTo(new BigDecimal(HHSConstants.INT_ZERO)) < HHSConstants.INT_ZERO)
			{
				lbError = true;

				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						"ERROR_UPDATE_SUB_BUDGET"));
			}

			if (aoContractBudgetBean.getContractTypeId().equals(HHSConstants.TWO))
			{
				// start release 3.14.0
				Integer loLastConfiguredFY = (Integer) DAOUtil.masterDAO(aoMybatisSession,
						aoContractBudgetBean.getContractId(), HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.FETCH_LAST_FY_CONFIGURED, HHSConstants.JAVA_LANG_STRING);
				if (null != loLastConfiguredFY
						&& loLastConfiguredFY == Integer.parseInt(aoContractBudgetBean.getBudgetfiscalYear()))
				{
					List<String> lsSubBudgetNameList = (List<String>) DAOUtil.masterDAO(aoMybatisSession,
							aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.GET_SUB_BUDGET_NAME_FOR_SAME_AMENDMENT_BUDGET,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);

					if (lsSubBudgetNameList != null && !lsSubBudgetNameList.isEmpty()
							&& lsSubBudgetNameList.contains(aoContractBudgetBean.getSubbudgetName()))
					{
						lbError = true;
						throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGE_PROP_FILE,
								HHSConstants.ERROR_ADD_SUB_BUDGET_WITH_SAME_NAME));
					}

					// insert code for multiple amendment
					String lsParentSubBudgetId = (String) DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.GET_PARENT_SUB_BUDGET_ID_NEXT_NEW_FY,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);

					if (lsParentSubBudgetId != null && !lsParentSubBudgetId.isEmpty())
					{
						aoContractBudgetBean.setParentSubBudgetId(lsParentSubBudgetId);
					}
					else
					{
						// insert base before amending its sub budget entries.
						DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean,
								HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.CS_ADD_CONTRACT_CONF_AMENDMENT_BUDGET_DETAILS_ADD_ITS_PARENT,
								HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
					}
					DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.CS_ADD_CONTRACT_CONF_AMENDMENT_BUDGET_DETAILS_ADD_FOR_NEXT_NEW_FY,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
				}
				else
				{
					DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.CS_ADD_CONTRACT_CONF_AMENDMENT_BUDGET_DETAILS,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
				}
				// end release 3.14.0
			}
			else
			{
				DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_ADD_CONTRACT_CONF_UPDATE_BUDGET_DETAILS,
						HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			}
			setMoState("Sub Budget Details for contract updated successfully:");
		}
		// ApplicationException is thrown while executing the query
		catch (ApplicationException loExp)
		{
			if (lbError)
			{
				loExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loExp.toString());
			}

			LOG_OBJECT.Error("ApplicationException occured in ConfigurationService:"
					+ "addContractConfUpdateBudgetDetails method:: ", loExp);
			setMoState("Error while updating Sub Budget details of contract:");
			throw loExp;
		}
	}

	/**
	 * Method validates the modified amount
	 * <ul>
	 * <li>Query Id 'fetchSubBudgetAmountByParentId' is executed</li>
	 * <li>uery Id 'fetchInvoiceAmount' is executed</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param aoSubBudgetBeanForModify ContractBudgetBean object
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */

	private void validateModifiedAmount(SqlSession aoMybatisSession, ContractBudgetBean aoSubBudgetBeanForModify)
			throws ApplicationException
	{
		BigDecimal loSubBudgetAmount = new BigDecimal(HHSConstants.INT_ZERO);
		BigDecimal loInvoicedAmount = new BigDecimal(HHSConstants.INT_ZERO);
		BigDecimal loModifiedAmount = new BigDecimal(HHSConstants.INT_ZERO);
		boolean lbError = false;
		String lsParentId = null;
		try
		{
			lsParentId = aoSubBudgetBeanForModify.getId();
			if (lsParentId != null)
			{
				String lsSubBudgetModifiedAmount = (String) DAOUtil.masterDAO(aoMybatisSession,
						aoSubBudgetBeanForModify, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_FETCH_SUB_BUDGET_AMOUNT_BY_PARENT_ID,
						HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
				String lsInvoicedModifiedAmount = (String) DAOUtil.masterDAO(aoMybatisSession, lsParentId,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_FETCH_INVOICE_AMOUNT,
						HHSConstants.JAVA_LANG_STRING);
				String lsModifiedAmount = aoSubBudgetBeanForModify.getModifiedAmount();

				if (lsSubBudgetModifiedAmount != null && !HHSConstants.EMPTY_STRING.equals(lsSubBudgetModifiedAmount))
				{
					loSubBudgetAmount = loSubBudgetAmount.add(new BigDecimal(lsSubBudgetModifiedAmount));
				}
				if (lsInvoicedModifiedAmount != null && !HHSConstants.EMPTY_STRING.equals(lsInvoicedModifiedAmount))
				{
					loInvoicedAmount = loInvoicedAmount.add(new BigDecimal(lsInvoicedModifiedAmount));
				}
				if (lsModifiedAmount != null && !HHSConstants.EMPTY_STRING.equals(lsModifiedAmount))
				{
					loModifiedAmount = loModifiedAmount.add(new BigDecimal(lsModifiedAmount));
				}

				if ((((loSubBudgetAmount.subtract(loInvoicedAmount)).add(loModifiedAmount)).compareTo(new BigDecimal(
						HHSConstants.INT_ZERO))) < HHSConstants.INT_ZERO)
				{
					lbError = true;

					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSR5Constants.ERROR_UPDATE_SUB_BUDGET));

				}
			}

		}
		// ApplicationException is thrown while executing query and will loading
		// property
		catch (ApplicationException loExp)
		{
			if (lbError)
			{
				loExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loExp.toString());
			}
			LOG_OBJECT.Error("Exception occured in Configuration Service:" + " validate Modified Amount method:: ",
					loExp);
			setMoState("Error while updating Sub Budget details of contract:");
			throw loExp;

		}

	}

	/**
	 * Method validates the modified amount
	 * <ul>
	 * <li>Query Id 'fetchAmendBudgetDetails' is executed</li>
	 * <li>Query Id 'fetchTotSubBudgetAmt' is executed</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param aoSubBudgetBeanForAmend ContractBudgetBean object
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */

	private void validateSubBudgetAmendment(SqlSession aoMybatisSession, ContractBudgetBean aoSubBudgetBeanForAmend)
			throws ApplicationException
	{   
		LOG_OBJECT.Info("-------validateSubBudgetAmendment---------");
		boolean lbError = false;
		try
		{
			if (null == aoSubBudgetBeanForAmend.getBudgetId()
					|| aoSubBudgetBeanForAmend.getBudgetId().equals(HHSR5Constants.EMPTY_STRING))
			{
				ContractBudgetBean loContBudgetBean = (ContractBudgetBean) DAOUtil
						.masterDAO(aoMybatisSession, aoSubBudgetBeanForAmend,
								HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.CS_FETCH_AMEND_BUDGET_DETAILS,
								HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);

				aoSubBudgetBeanForAmend.setBudgetId(loContBudgetBean.getBudgetId());
				aoSubBudgetBeanForAmend.setTotalbudgetAmount(loContBudgetBean.getTotalbudgetAmount());
			}
			// Changes for R4 to validate all pending amendment sub budget
			// amount values
			
			// Start QC9025 R 9.2 - validateSubBudgetamountForNegativeAmendment only for negative amendment
			//validateSubBudgetamountForNegativeAmendment(aoMybatisSession, aoSubBudgetBeanForAmend);
			BigDecimal subBudgetAmount = new BigDecimal(HHSConstants.INT_ZERO);
			String subBudgetAmountString = aoSubBudgetBeanForAmend.getTotalbudgetAmount();
			
			if(subBudgetAmountString != null && !HHSConstants.EMPTY_STRING.equals(subBudgetAmountString) )
			{
				subBudgetAmount = subBudgetAmount.add(new BigDecimal(subBudgetAmountString));
			}	
			//LOG_OBJECT.Info("-------subBudgetAmount.compareTo(new BigDecimal(HHSConstants.INT_ZERO) :: "+subBudgetAmount.compareTo(new BigDecimal(HHSConstants.INT_ZERO)));
			if(subBudgetAmount.compareTo(new BigDecimal(HHSConstants.INT_ZERO)) < HHSConstants.INT_ZERO)
			{	
				validateSubBudgetamountForNegativeAmendment(aoMybatisSession, aoSubBudgetBeanForAmend);
			}
			// End QC9025 R 9.2 - validateSubBudgetamountForNegativeAmendment only for negative amendment
		}
		// ApplicationException is thrown while executing query and will loading
		// property
		catch (ApplicationException loAppExp)
		{
			if (lbError)
			{
				loAppExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loAppExp.toString());
			}
			LOG_OBJECT.Error("Exception occured in ConfigurationService:" + " validateSubBudgetAmendment method:: ",
					loAppExp);
			setMoState("Error while updating Sub Budget details of contract in validateSubBudgetAmendment:");
			throw loAppExp;

		}

	}

	/**
	 * For R4 code changes Validate if the absolute value of the Amendment
	 * amount entered is greater than the [Remaining Amount of the FY ? Negative
	 * amendment amounts entered for the FY in other pending amendments for
	 * which no budgets have been approved], then display grid level error
	 * message
	 * <ul>
	 * <li>Query Id 'fetchSubBudgetAmountByParentId' is executed</li>
	 * <li>Query Id 'fetchInvoiceAmount' is executed</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param aoSubBudgetBean ContractBudgetBean object
	 * @throws ApplicationException ApplicationException object
	 * 
	 */

	private void validateSubBudgetamountForNegativeAmendment(SqlSession aoMybatisSession,
			ContractBudgetBean aoSubBudgetBean) throws ApplicationException
	{
		BigDecimal loSubBudgetAmount = new BigDecimal(HHSConstants.INT_ZERO);
		BigDecimal loInvoicedAmount = new BigDecimal(HHSConstants.INT_ZERO);
		BigDecimal loModifiedAmount = new BigDecimal(HHSConstants.INT_ZERO);
		BigDecimal loAllAmendedSubBudgetAmount = new BigDecimal(HHSConstants.INT_ZERO);
		boolean lbError = false;
		String lsParentId = null;
		try
		{
			lsParentId = aoSubBudgetBean.getId();
			if (lsParentId != null)
			{
				String lsSubBudgetAmount = (String) DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_FETCH_SUB_BUDGET_AMOUNT_BY_PARENT_ID,
						HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
				String lsInvoicedAmount = (String) DAOUtil.masterDAO(aoMybatisSession, lsParentId,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_FETCH_INVOICE_AMOUNT,
						HHSConstants.JAVA_LANG_STRING);
				String lsModifiedAmount = aoSubBudgetBean.getModifiedAmount();

				String lsAllAmendedSubBudgetAmount = (String) DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.FETCH_PENDING_NE_AMENDMENT_SUBBUDGET_AMOUNT,
						HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);

				if (lsSubBudgetAmount != null && !HHSConstants.EMPTY_STRING.equals(lsSubBudgetAmount))
				{
					loSubBudgetAmount = loSubBudgetAmount.add(new BigDecimal(lsSubBudgetAmount));
				}
				if (lsInvoicedAmount != null && !HHSConstants.EMPTY_STRING.equals(lsInvoicedAmount))
				{
					loInvoicedAmount = loInvoicedAmount.add(new BigDecimal(lsInvoicedAmount));
				}
				if (lsModifiedAmount != null && !HHSConstants.EMPTY_STRING.equals(lsModifiedAmount))
				{
					loModifiedAmount = loModifiedAmount.add(new BigDecimal(lsModifiedAmount));
				}

				if (lsAllAmendedSubBudgetAmount != null
						&& !HHSConstants.EMPTY_STRING.equals(lsAllAmendedSubBudgetAmount))
				{
					loAllAmendedSubBudgetAmount = loAllAmendedSubBudgetAmount.add(new BigDecimal(
							lsAllAmendedSubBudgetAmount));
				}

				if ((((loSubBudgetAmount.subtract(loInvoicedAmount)).add(loModifiedAmount)
						.add(loAllAmendedSubBudgetAmount)).compareTo(new BigDecimal(HHSConstants.INT_ZERO))) < HHSConstants.INT_ZERO)
				{
					lbError = true;

					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.ERROR_NEGATIVE_AMENDMENT_VALIDATION_MESSAGE));
				}
			}

		}
		// ApplicationException is thrown while executing query and will loading
		// property
		catch (ApplicationException loExp)
		{
			if (lbError)
			{
				loExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loExp.toString());
			}
			LOG_OBJECT.Error("Exception occured in ConfigurationService:"
					+ " validateSubBudgetamountForNegativeAmendment method:: ", loExp);
			setMoState("Error while updating Sub Budget details of contract:");
			throw loExp;
		}
	}

	/**
	 * This method insert the updated sub budget details.
	 * 
	 * *Query Id 'insertUpdatedSubBudgetDetails'
	 * 
	 * Query Id 'insertContractConfUpdateSubBudgetDetails'
	 * @param aoMybatisSession SqlSession
	 * @param asContractId String
	 * @param asFiscalYearId String
	 * @throws ApplicationException ApplicationException object
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void insertUpdatedSubBudgetDetails(SqlSession aoMybatisSession, String asContractId, String asFiscalYearId)
			throws ApplicationException
	{

		List<ContractBudgetBean> loContractBudgetBeanList = null;
		Map<String, String> loBudgetInfo = new HashMap<String, String>();
		try
		{
			loBudgetInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			loBudgetInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);
			loContractBudgetBeanList = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.BMC_INSERT_UPDATED_SUB_BUDGET_DETAILS,
					HHSConstants.JAVA_UTIL_MAP);
			if (loContractBudgetBeanList != null)
			{
				Iterator<ContractBudgetBean> loIter = loContractBudgetBeanList.iterator();

				while (loIter.hasNext())
				{
					ContractBudgetBean loContractFinancialBean = loIter.next();
					loContractFinancialBean.setContractId(asContractId);
					loContractFinancialBean.setSubbudgetAmount(HHSConstants.STRING_ZERO);
					loContractFinancialBean.setContractTypeId(HHSConstants.FOUR);
					DAOUtil.masterDAO(aoMybatisSession, loContractFinancialBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.CS_INSERT_CONTRACT_CONF_UPDATE_SUB_BUDGET_DETAILS,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
				}
			}
			setMoState("Sub Budget details for contract fetched successfully:");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ConfigurationService: insertUpdatedSubBudgetDetails method:: ",
					loAppEx);
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: insertUpdatedSubBudgetDetails method:: ", loEx);
			throw loAppEx;
		}
	}

	/**
	 * This method insert the amendment sub budget details.
	 * <ul>
	 * <li>Query Id 'insertUpdatedSubBudgetDetails' is executed</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param asContractId String containing ContractId Id
	 * @param asFiscalYearId String containing FiscalYearId Id
	 * @param asAmendContractId String containing Amendment ContractId Id
	 * @throws ApplicationException ApplicationException object
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void insertAmendmentSubBudgetDetails(SqlSession aoMybatisSession, String asContractId,
			String asFiscalYearId, String asAmendContractId) throws ApplicationException
	{

		List<ContractBudgetBean> loContractBudgetBeanList = null;
		Map<String, String> loBudgetInfo = new HashMap<String, String>();
		try
		{
			loBudgetInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			loBudgetInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);
			loContractBudgetBeanList = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.BMC_INSERT_UPDATED_SUB_BUDGET_DETAILS,
					HHSConstants.JAVA_UTIL_MAP);
			if (loContractBudgetBeanList != null)
			{
				Iterator<ContractBudgetBean> loIter = loContractBudgetBeanList.iterator();

				while (loIter.hasNext())
				{
					ContractBudgetBean loContractFinancialBean = loIter.next();
					loContractFinancialBean.setContractId(asContractId);
					loContractFinancialBean.setSubbudgetAmount(HHSConstants.STRING_ZERO);
					loContractFinancialBean.setContractTypeId(HHSConstants.TWO);
					loContractFinancialBean.setAmendmentContractId(asAmendContractId);
					DAOUtil.masterDAO(aoMybatisSession, loContractFinancialBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.CS_INSERT_CONTRACT_CONF_AMENDMENT_SUB_BUDGET_DETAILS,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
				}
			}
			setMoState("Sub Budget details for amendment contract inserted successfully:");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ConfigurationService: insertAmendmentSubBudgetDetails method:: ",
					loAppEx);
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: insertAmendmentSubBudgetDetails method:: ", loEx);
			throw loAppEx;
		}
	}

	/**
	 * This method is used to edit the sub budget details for a fiscal year of a
	 * contract using <code>editContractConfSubBudgetDetails</code> Update
	 * query</li> <li>
	 * Transaction id : <code>editContractConfSubBudgetDetails</code></li><br>
	 * This method was updated in R4.
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoSubBudgetBean ContractBudgetBean
	 * @return Boolean
	 * @throws ApplicationException ApplicationException object
	 * 
	 */
	public Boolean editContractConfUpdateSubBudgetDetails(SqlSession aoMybatisSession,
			ContractBudgetBean aoSubBudgetBean, String asAmendment) throws ApplicationException
	{
		boolean lbUpdateStatus = Boolean.TRUE;
		Integer loNegativeAmendmentCount = 0;
		try
		{
			if (null != asAmendment && asAmendment.equalsIgnoreCase(HHSConstants.FALSE))
			{
				validateModifiedAmount(aoMybatisSession, aoSubBudgetBean);
				DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_EDIT_CONTRACT_CONF_UPDATE_SUB_BUDGET_DETAILS,
						HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
				setMoState("update module: Sub Budget Details for contract updated successfully:");
			}
			else if (null != asAmendment && asAmendment.equalsIgnoreCase(HHSConstants.TRUE))
			{
				validateSubBudgetAmendment(aoMybatisSession, aoSubBudgetBean);
				// start release 3.14.0
				loNegativeAmendmentCount = (Integer) DAOUtil.masterDAO(aoMybatisSession,
						aoSubBudgetBean.getAmendmentContractId(), HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.NEGATIVE_AMENDMENT_COUNT, HHSConstants.JAVA_LANG_STRING);

				Integer loLastConfiguredFY = (Integer) DAOUtil.masterDAO(aoMybatisSession,
						aoSubBudgetBean.getContractId(), HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.FETCH_LAST_FY_CONFIGURED, HHSConstants.JAVA_LANG_STRING);
				
				// Rel 3.15.0 ; QC 6797. Added null check because query returns null unless it is Out-Year
				if (null!=loLastConfiguredFY && loLastConfiguredFY == Integer.parseInt(aoSubBudgetBean.getBudgetfiscalYear())
						&& (loNegativeAmendmentCount != null && loNegativeAmendmentCount == 0))
				{
					Integer loSubBudgetIdCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.GET_AMENDMENT_SUB_BUDGET_NAME_SAME_WITH_ANOTHER_AMENDMENT,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
					// fetch sub budget name
					String lsSubBudgetNameBackUp = (String) DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.GET_SUB_BUDGET_NAME,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
					if (!aoSubBudgetBean.getSubbudgetName().equalsIgnoreCase(lsSubBudgetNameBackUp))
					{
						if (loSubBudgetIdCount == 0)
						{
							// code for insert parent
							// insert base before amending its sub budget
							// entries.
							DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
									HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
									HHSConstants.CS_ADD_CONTRACT_CONF_AMENDMENT_BUDGET_DETAILS_ADD_ITS_PARENT,
									HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
							aoSubBudgetBean.setSubbudgetNameBackup(lsSubBudgetNameBackUp);
							DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
									HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
									HHSConstants.UPDATE_PARENT_WHILE_EDITING_AMENDMENT,
									HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);

						}
						else
						{

							String lsParentSubBudgetId = (String) DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
									HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
									HHSConstants.GET_PARENT_SUB_BUDGET_ID_FOR_ALREADY_LINKED_AMENDMENT,
									HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
							aoSubBudgetBean.setParentSubBudgetId(lsParentSubBudgetId);
							// update parent id update query
							aoSubBudgetBean.setSubbudgetNameBackup(lsSubBudgetNameBackUp);
							DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
									HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
									HHSConstants.UPDATE_PARENT_WHILE_EDITING_AMENDMENT,
									HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
						}
						// code to delete base sub budget which are no longer
						// linked
						ContractBudgetBean aoSubBudgetBeanNew = aoSubBudgetBean;
						aoSubBudgetBeanNew.setSubbudgetName(lsSubBudgetNameBackUp);
						loSubBudgetIdCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBeanNew,
								HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.GET_AMENDMENT_SUB_BUDGET_NAME_SAME_WITH_ANOTHER_AMENDMENT,
								HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
						if (loSubBudgetIdCount == 0)
						{
							DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBeanNew,
									HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
									HHSConstants.CS_DEL_CONTRACT_CONF_AMEND_SUBBUDGET_PARENT_DETAILS_NEXT_NEW_FY,
									HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
						}
					}
					else
					{
						DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
								HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.CS_EDIT_CONTRACT_CONF_AMEND_SUB_BUDGET_DETAILS_PARENT_NAME_NEXT_NEW_FY,
								HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
						DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
								HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.CS_EDIT_CONTRACT_CONF_AMEND_SUB_BUDGET_DETAILS_NEXT_NEW_FY,
								HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);

					}
					// end release 3.14.0
				}
				else
				{
					DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.CS_EDIT_CONTRACT_CONF_AMEND_SUB_BUDGET_DETAILS,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
				}
				setMoState("Sub Budget Details for contract updated successfully:");
			}

		}
		// ApplicationException and Exception are thrown while executing query
		// and validateModifiedAmount
		catch (ApplicationException loExp)
		{
			setMoState("Error while updating Sub Budget details of contract:");
			loExp.addContextData("Error while updating Sub Budget details of contract::", loExp.toString());
			LOG_OBJECT.Error("ApplicationException while updating Sub Budget details of contract:"
					+ " Method - editContractConfUpdateSubBudgetDetails", loExp);
			throw loExp;
		}
		catch (Exception loExcep)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: editContractConfUpdateSubBudgetDetails:: ", loExcep);
			loAppEx.addContextData("Error while updating Sub Budget details of contract::", loAppEx.toString());
			setMoState("Error while updating Sub Budget details of contract:");
			LOG_OBJECT.Error("Error while updating Sub Budget details of contract:"
					+ " Method - editContractConfUpdateSubBudgetDetails", loExcep);
			throw loAppEx;
		}
		return lbUpdateStatus;
	}

	/**
	 * This method is used to update the Chart of Accounts details for New FY in
	 * the DataBase.
	 * 
	 * <ul>
	 * <li>One or more fields of below list can be edited in DataBase using
	 * <code>editNewFYConfCOADetails</code> Update query</li>
	 * <li>The fields which can be edited in the table are -</li>
	 * <ul>
	 * <li>UNIT_OF_APPROPRIATION</li>
	 * <li>BUDGET_CODE</li>
	 * <li>OBJECT_CODE</li>
	 * <li>SUB_OBJECT_CODE</li>
	 * <li>REPORTING_CATEGORY</li>
	 * <li>AMOUNT</li>
	 * </ul>
	 * <ul>
	 * <li>Incase update calls return count of updated rows as zero then do an
	 * insert call to database</li>
	 * </ul>
	 * </ul>
	 * 
	 * @param aoAccountsAllocationBean AccountsAllocationBean object
	 * @param aoMybatisSession SqlSession object
	 * @throws ApplicationException Exception thrown in case of any error
	 */
	public void editNewFYConfCOADetails(AccountsAllocationBean aoAccountsAllocationBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		try
		{
			int liStartFY = Integer.parseInt(aoAccountsAllocationBean.getContractStartFY());
			int liEndFY = Integer.parseInt(aoAccountsAllocationBean.getContractEndFY());
			int liCounter = HHSConstants.INT_ONE;
			updateAllocationBeanForNullMember(aoAccountsAllocationBean); // Required
																			// for
																			// updating
																			// null
																			// member
																			// in
																			// aoAllocationBean
			String lsBeanFY = null;
			String lsAmt = null;
			int liCount;
			for (liCount = liStartFY; liCount <= liEndFY; liCount++)
			{
				lsBeanFY = HHSConstants.SMALL_FY + liCounter;
				aoAccountsAllocationBean.setFiscalYearID(String.valueOf(liCount));
				lsAmt = (String) BeanUtils.getProperty(aoAccountsAllocationBean, lsBeanFY);
				int liEndIndex = aoAccountsAllocationBean.getId().lastIndexOf(HHSConstants.NEW_RECORD_COA);

				if (lsAmt != null)
				{
					aoAccountsAllocationBean.setAmmount(lsAmt);
				}
				else
				{
					if (liEndIndex != -1)// if newly added row
					{
						aoAccountsAllocationBean.setAmmount(HHSConstants.STRING_ZERO);
					}
					else
					{
						liCounter++;
					}
					continue;
				}

				DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_EDIT_NEW_FY_CONF_COA_DETAILS,
						HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
				liCounter++;
			}
			setMoState("Chart of Accounts details updated successfully");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AO_ACCOUNTS_ALLOCATION_BEAN,
					CommonUtil.convertBeanToString(aoAccountsAllocationBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: editNewFYConfCOADetails method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: editNewFYConfCOADetails method - failed."
					+ " Exception occured while editing chart of allocation record for AccountsAllocationBean::"
					+ CommonUtil.convertBeanToString(aoAccountsAllocationBean) + " \n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: editNewFYConfCOADetails:: ", loEx);
			loAppEx.addContextData(HHSConstants.AO_ACCOUNTS_ALLOCATION_BEAN,
					CommonUtil.convertBeanToString(aoAccountsAllocationBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: editNewFYConfCOADetails method:: ", loAppEx);
			setMoState("editNewFYConfCOADetails execution failed");
			throw loAppEx;
		}
		setMoState("editNewFYConfCOADetails is executed successfully");
	}

	/**
	 * This method updated null member variables in AccountsAllocationBean using
	 * <b>id</b> member variable. This is required for updating FY details in
	 * database.
	 * 
	 * <ul>
	 * <li>It uses AccountsAllocationBean Id to fetch values for other member
	 * variables</li>
	 * <ul>
	 * 
	 * @param aoAccountsAllocationBean AccountsAllocationBean
	 */
	private void updateAllocationBeanForNullMember(AccountsAllocationBean aoAccountsAllocationBean)
	{
		int liEndIndex = aoAccountsAllocationBean.getId().lastIndexOf(HHSConstants.NEW_RECORD_COA);
		if (liEndIndex != -1)
		{
			aoAccountsAllocationBean.setId(aoAccountsAllocationBean.getId().substring(0, liEndIndex));
		}
		else
		{
			String[] loSplitBeanIdArray = aoAccountsAllocationBean.getId().split(HHSConstants.HYPHEN);
			aoAccountsAllocationBean.setChartOfAccount(aoAccountsAllocationBean.getId().substring(
					HHSConstants.INT_ZERO, 11));
			aoAccountsAllocationBean.setUnitOfAppropriation(loSplitBeanIdArray[HHSConstants.INT_ZERO]);
			aoAccountsAllocationBean.setBudgetCode(loSplitBeanIdArray[HHSConstants.INT_ONE]);
			aoAccountsAllocationBean.setObjectCode(loSplitBeanIdArray[HHSConstants.INT_TWO]);
			aoAccountsAllocationBean
					.setSubOc((loSplitBeanIdArray.length > HHSConstants.INT_THREE) ? loSplitBeanIdArray[HHSConstants.INT_THREE]
							: HHSConstants.EMPTY_STRING);
			aoAccountsAllocationBean
					.setRc((loSplitBeanIdArray.length > HHSConstants.INT_FOUR) ? loSplitBeanIdArray[HHSConstants.INT_FOUR]
							: HHSConstants.EMPTY_STRING);
		}
	}

	/**
	 * This method is used to copy previous years sub-budget to the current
	 * configurable Fiscal year in NewFY Configuration Task screen. While
	 * Inserting the previous year's amount to the current year, dollar value of
	 * all line-items is defaulted to zero.
	 * 
	 * Before copying the previous year's sub-budget to the current one, it
	 * checks whether there exists any previous Fiscal Year for the given
	 * contract. Query Id 'fetchContractConfSubBudgetDetails1'
	 * 
	 * Query Id 'fetchContractConfSubBudgetDetails1'
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asContractId String
	 * @param asFiscalYearId String
	 * @param asBudgetId String
	 * @param asUserId String
	 * @return lbCopyDataToCurrentYear Boolean
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Boolean copyPreviousFYSubBudgetToCurrentFY(SqlSession aoMybatisSession, String asContractId,
			String asFiscalYearId, String asBudgetId, String asUserId) throws ApplicationException
	{
		Boolean loCopyDataToCurrentYear = true;
		List<ContractBudgetBean> loContractBudgetBeanList = null;
		Map<String, String> loBudgetInfo = new HashMap<String, String>();
		try
		{
			loBudgetInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			loBudgetInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);

			loContractBudgetBeanList = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_FETCH_CONTRACT_CONF_SUBBUDGET_DETAILS1, HHSConstants.JAVA_UTIL_MAP);

			if (!loContractBudgetBeanList.isEmpty())
			{
				loCopyDataToCurrentYear = false;
			}
			// if line item exists for current fiscal year
			if (loCopyDataToCurrentYear)
			{
				loBudgetInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY,
						String.valueOf((Integer.parseInt(asFiscalYearId)) - HHSConstants.INT_ONE));

				loContractBudgetBeanList = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_FETCH_CONTRACT_CONF_SUBBUDGET_DETAILS1, HHSConstants.JAVA_UTIL_MAP);

				// If previous FY exists, copy the sub-budget info to current
				// Fiscal
				// Year with Dollar value as $ 0.00
				if (!loContractBudgetBeanList.isEmpty())
				{
					ContractBudgetBean loContractBudgetBean = null;
					for (Iterator<ContractBudgetBean> loIterator = loContractBudgetBeanList.iterator(); loIterator
							.hasNext();)
					{
						loContractBudgetBean = (ContractBudgetBean) loIterator.next();
						loContractBudgetBean.setId(HHSConstants.EMPTY_STRING);
						loContractBudgetBean.setBudgetId(asBudgetId);
						loContractBudgetBean.setSubbudgetAmount(HHSConstants.STRING_ZERO);
						loContractBudgetBean.setBudgetfiscalYear(asFiscalYearId);
						loContractBudgetBean.setCreatedByUserId(asUserId);
						loContractBudgetBean.setModifiedByUserId(asUserId);
						// copy the sub-budget to the current fiscal year
						// sub-budget
						insertContractConfSubBudgetDetails(aoMybatisSession, loContractBudgetBean);
					}
				}
			}
			setMoState("copyPreviousFYSubBudgetToCurrentFY executed successfully");
		}
		catch (ApplicationException loExp)
		{
			setMoState("copyPreviousFYSubBudgetToCurrentFY executtion failed");
			throw loExp;
		}
		return loCopyDataToCurrentYear;
	}

	/**
	 * This method fetches sum total of amount from CONTRACT_FINANCIAL table for
	 * a given contract and fiscal year. *Query Id 'fetchFYPlannedAmount'
	 * @param aoMybatisSession SqlSession
	 * @param asContractId String
	 * @param asFiscalYearId String
	 * @return loFYPlannedAmount String
	 * @throws ApplicationException ApplicationException obejct
	 * 
	 */
	public String fetchFYPlannedAmount(SqlSession aoMybatisSession, String asContractId, String asFiscalYearId)
			throws ApplicationException
	{
		String loFYPlannedAmount = HHSConstants.STRING_ZERO;
		Map<String, String> loCBGridBeanMapInfo = new HashMap<String, String>();
		try
		{
			loCBGridBeanMapInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			loCBGridBeanMapInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);
			loFYPlannedAmount = (String) DAOUtil.masterDAO(aoMybatisSession, loCBGridBeanMapInfo,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_FETCH_FY_PLANNED_AMOUNT,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Sum of Amount for given Fiscal Year fetched successfully");
		}
		catch (ApplicationException loAppEx)
		{
			setMoState("Transaction Failed:: ConfigurationService: fetchFYPlannedAmount method - failed."
					+ " Exception occured while fetching FY Planned Amount:: \n");
			loAppEx.setContextData(loCBGridBeanMapInfo);
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchFYPlannedAmount method:: ", loAppEx);
			throw loAppEx;
		}
		return loFYPlannedAmount;
	}

	/**
	 * This method fetches sum total of amount from CONTRACT_FINANCIAL table for
	 * a given amendment and fiscal year. <br>
	 * Query Id 'fetchAmendmentFYPlannedAmount' is executed <br>
	 * This method was updated in R4.
	 * 
	 * @param aoMybatisSession SqlSession Object
	 * @param asContractId String containing ContractId
	 * @param asFiscalYearId String containing FiscalYearId
	 * @param asContractTypeId String containing ContractTypeId
	 * @param asAmendContractId String containing AmendContractId
	 * @return String containing amount
	 * @throws ApplicationException ApplicationException Object
	 * 
	 */
	public String fetchAmendmentFYPlannedAmount(SqlSession aoMybatisSession, String asContractId,
			String asFiscalYearId, String asContractTypeId, String asAmendContractId) throws ApplicationException
	{
		String loFYPlannedAmount = HHSConstants.STRING_ZERO;
		Map<String, String> loCBGridBeanMapInfo = new HashMap<String, String>();
		try
		{
			loCBGridBeanMapInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			loCBGridBeanMapInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);
			loCBGridBeanMapInfo.put(HHSConstants.CONTRACT_TYPE_ID, asContractTypeId);
			loCBGridBeanMapInfo.put(HHSConstants.AMENDED_CONTRACT_ID, asAmendContractId);

			loFYPlannedAmount = (String) DAOUtil.masterDAO(aoMybatisSession, loCBGridBeanMapInfo,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_FETCH_AMENDMENT_FY_PLANNED_AMOUNT,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Sum of Amount for given Fiscal Year fetched successfully");
		}
		catch (ApplicationException loAppEx)
		{
			setMoState("Transaction Failed:: ConfigurationService: fetchFYPlannedAmount method - failed."
					+ " Exception occured while fetching FY Planned Amount:: \n");
			loAppEx.setContextData(loCBGridBeanMapInfo);
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchFYPlannedAmount method:: ", loAppEx);
			throw loAppEx;
		}
		return (null != loFYPlannedAmount ? loFYPlannedAmount : HHSConstants.STRING_ZERO);
	}

	/**
	 * Gets sub budget details of a budget for current contract fiscal year
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>SUB_BUDGET_NAME</li>
	 * <li>SUB_BUDGET_AMOUNT</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of asBudgetId and asFiscalYearId, the above mentioned
	 * values are received from the DataBase by executing the
	 * <code>fetchContractSubBudgetDetails1</code> query in the BudgetMapper</li>
	 * <li>It returns the values as ContractBudgetBean object</li>
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the UI</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asContractId on the basis of which Contract Sub Budgets will be
	 *            fetched
	 * @param asFiscalYearId contract current fiscal year (may be Contract's
	 *            first FY if first FY is greater than Current FY)
	 * @return ContractBudgetBean Bean
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<ContractBudgetBean> fetchNewFYSubBudgetDetails(SqlSession aoMybatisSession, String asContractId,
			String asFiscalYearId) throws ApplicationException
	{
		List<ContractBudgetBean> loContractBudgetBean = null;
		Map<String, String> loBudgetInfo = new HashMap<String, String>();
		try
		{
			loBudgetInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			loBudgetInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);

			loContractBudgetBean = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_FETCH_CONTRACT_CONF_SUBBUDGET_DETAILS1, HHSConstants.JAVA_UTIL_MAP);
			loContractBudgetBean = markNewRowInContractBudgetBeanList(loContractBudgetBean, aoMybatisSession);
			setMoState("Sub Budget details for contract fetched successfully:");
		}
		catch (ApplicationException aoAppEx)
		{
			setMoState("Error while getting contract sub budget details :");
			throw aoAppEx;
		}
		return loContractBudgetBean;
	}

	/**
	 * This method iterates the SubBudgetBeanList and identifies the new row on
	 * the basis of <b>ParentId</b>.
	 * 
	 * After the new row is identified the Id for that particular Sub-Budget is
	 * appended with <b>"_newrecord"</b>, so that it can be distinguished from
	 * other Sub-Budgets.
	 * 
	 * @param aoContractBudgetBean List<ContractBudgetBean>
	 * @return loContractBudgetBeanList List<ContractBudgetBean>
	 * @throws ApplicationException
	 */
	private List<ContractBudgetBean> markNewRowInContractBudgetBeanList(List<ContractBudgetBean> aoContractBudgetBean,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		List<ContractBudgetBean> loContractBudgetBeanList = new ArrayList<ContractBudgetBean>();
		StringBuffer lsNewSubBudgetId = null;
	
		for (Iterator<ContractBudgetBean> loIterator = aoContractBudgetBean.iterator(); loIterator.hasNext();)
		{
			ContractBudgetBean loContractBudgetBean = (ContractBudgetBean) loIterator.next();
			//Code removed in R7 for defect 6596 to Allow Agency to Edit Existing Budget Titles During New FY Configuration Task
					lsNewSubBudgetId = new StringBuffer(loContractBudgetBean.getId());
					lsNewSubBudgetId.append(HHSConstants.NEW_RECORD);
					loContractBudgetBean.setId(lsNewSubBudgetId.toString());

			loContractBudgetBeanList.add(loContractBudgetBean);
		}
		return loContractBudgetBeanList;
	}

	/**
	 * This method is added for Release 3.6.0 for enhancement request #6496.
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
	 * This method is used to get notifications Map for NT 312
	 * @param asAmendContractId
	 * @param lsUserId
	 * @param aoMybatisSession
	 * @param loContractBudgetBean
	 * @return loNotificationMap
	 * @throws ApplicationException
	 */
	private HashMap<String, Object> getNotificationMapForNT312(String asAmendContractId, String lsUserId,
			SqlSession aoMybatisSession, ContractBudgetBean loContractBudgetBean) throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		try
		{
			HashMap<String, String> loRequestMap = new HashMap<String, String>();
			List<String> loNotificationAlertList = new ArrayList<String>();
			loNotificationAlertList.add(HHSP8Constants.NT312);
			loNotificationAlertList.add(HHSP8Constants.AL312);
			loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
			HashMap aoHMArgs = new HashMap();
			aoHMArgs.put(HHSConstants.FISCAL_YEAR_ID, loContractBudgetBean.getBudgetfiscalYear());
			aoHMArgs.put(HHSConstants.PARENT_CONTRACT_ID, loContractBudgetBean.getContractId());
			HashMap lsContractInfo = (HashMap) DAOUtil.masterDAO(aoMybatisSession, aoHMArgs,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.GET_CONTRACT_INFORMATION,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			loRequestMap.put(HHSConstants.NT_PROCUREMENT_TITLE,
					(String) lsContractInfo.get(HHSConstants.CONTRACT_TITLE));
			loRequestMap.put(HHSConstants.NT_CT, (String) lsContractInfo.get(HHSConstants.CT_NUMBER));
			HashMap<String, String> loLinkMap = new HashMap<String, String>();
			StringBuffer lsBfApplicationUrl = new StringBuffer();
			lsBfApplicationUrl = getNotificationLink();
			lsBfApplicationUrl.append(HHSConstants.BUDGET_LIST_URL);
			loLinkMap.put(HHSConstants.LINK, lsBfApplicationUrl.toString());
			List<String> loProvidersList = new ArrayList<String>();
			loProvidersList.add(loContractBudgetBean.getOrganizationId());
			NotificationDataBean loNotificationDataBean = new NotificationDataBean();
			loNotificationDataBean.setProviderList(loProvidersList);
			loNotificationDataBean.setLinkMap(loLinkMap);
			loNotificationMap.put(HHSP8Constants.NT312, loNotificationDataBean);
			loNotificationMap.put(HHSP8Constants.AL312, loNotificationDataBean);
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loNotificationMap.put(ApplicationConstants.ENTITY_ID, asAmendContractId);
			loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.CONTRACT);
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, lsUserId);
		}
		// handling Exception thrown by the application
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error was occurred while get notfication", aoEx);
			throw new ApplicationException("Error was occurred while get notfication", aoEx);
		}
		return loNotificationMap;
	}

	/**
	 * @param aoMybatisSession SqlSession
	 * @param aoContractBudgetBean ContractBudgetBean
	 * @return loTransactionSuccessful Boolean
	 * @throws ApplicationException ApplicationException object
	 * 
	 *             Query Id 'updateBudgetForNewFYConfigurationTask'
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Boolean updateBudgetForNewFYConfigurationTask(SqlSession aoMybatisSession,
			ContractBudgetBean aoContractBudgetBean) throws ApplicationException
	{
		Boolean loTransactionSuccessful = false;

		try
		{
			// start release 3.14.0
			// when amendment is not registered in FMS
			List<String> loAmendmentContractList = (ArrayList<String>) DAOUtil.masterDAO(aoMybatisSession,
					aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_SELECT_AMENDMENT_BUDGET_STATUS_NEXT_NEW_FY,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_UPDATE_AMENDMENT_CONTRACT_STATUS_NEXT_NEW_FY,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);

			DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_UPDATE_AMENDMENT_BUDGET_STATUS_NEXT_NEW_FY,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			if (loAmendmentContractList != null && !loAmendmentContractList.isEmpty())
			{
				for (String lsAmendContractId : loAmendmentContractList)
				{
					NotificationService loNotificationService = new NotificationService();
					loNotificationService.processNotification(
							aoMybatisSession,
							getNotificationMapForNT312(lsAmendContractId, HHSConstants.SYSTEM_USER, aoMybatisSession,
									aoContractBudgetBean));
				}
			}

			// end release 3.14.0
			DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.QUERY_UPD_NEW_FY_BUDGET_CONFIG, HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			setMoState("Budget Details for contract updated successfully:");
			loTransactionSuccessful = true;
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while Updating Budget details of contract:");
			throw loExp;
		}
		return loTransactionSuccessful;
	}

	/**
	 * This method fetch the Contract End Date for a given contract. Query Id
	 * 'getContractEndDate'
	 * @param aoMybatisSession SqlSession
	 * @param asContractId String
	 * @return loContractEndDate String
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */

	public String getContractEndDate(SqlSession aoMybatisSession, String asContractId) throws ApplicationException
	{
		String loContractEndDate = null;
		try
		{
			loContractEndDate = (String) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.TRN_GET_CONTRACT_END_DATE,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Contract End Date for contract fetched successfully");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting Contract End Date :");
			throw loExp;
		}

		return loContractEndDate;
	}

	/**
	 * This method validates whether there exists any Budget for a given
	 * Contract and Fiscal Year. Query Id 'fetchBudgetIdIfExists'
	 * @param aoMybatisSession SqlSession
	 * @param asContractId String
	 * @param asFiscalYearId String
	 * @return lsBudgetId String
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	public String checkIfBudgetExists(SqlSession aoMybatisSession, String asContractId, String asFiscalYearId)
			throws ApplicationException
	{
		String lsBudgetId = null;
		Map<String, String> loBudgetMapInfo = new HashMap<String, String>();
		try
		{
			loBudgetMapInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			loBudgetMapInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);
			lsBudgetId = (String) DAOUtil.masterDAO(aoMybatisSession, loBudgetMapInfo,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.QUERY_GET_BUDGET_ID,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Budget Id for contract fetched successfully");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting Budget Id :");
			throw loExp;
		}
		return lsBudgetId;
	}

	/**
	 * This method fetches the total budget amount for the configurable year.
	 * 
	 * <ul>
	 * <li>It queries the value from sub_budget table on basis of BudgetId and
	 * FiscalYearId</li>
	 * </ul>
	 * Query Id 'fetchSubBudgetTotalAmount'
	 * @param aoMybatisSession SqlSession
	 * @param asBudgetId String
	 * @param asFiscalYearId String
	 * @return lsFiscalYearAmount String
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	public String fetchConfigurableYearBudgetAmount(SqlSession aoMybatisSession, String asBudgetId,
			String asFiscalYearId) throws ApplicationException
	{
		String lsFiscalYearAmount = null;
		Map<String, String> loSubBudgetMapInfo = new HashMap<String, String>();
		try
		{
			loSubBudgetMapInfo.put(HHSConstants.BUDGET_ID_KEY, asBudgetId);
			loSubBudgetMapInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);
			lsFiscalYearAmount = (String) DAOUtil.masterDAO(aoMybatisSession, loSubBudgetMapInfo,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.QUERY_GET_SUB_BUDGET_AMOUNT,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Sub Budget Total Amount for Budget fetched successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting Sub Budget Total Amount :");
			throw loExp;
		}
		return lsFiscalYearAmount;
	}

	/**
	 * This method is used to validate the Sum of budgets which cannot exceed FY
	 * Planned Amount
	 * 
	 * <li>Soft delete is made disabling active flag to zero</li> <li>Query Id
	 * 'fetchUpdateFYPlannedAmount' is executed</li><br>
	 * This method was updated in R4.
	 * 
	 * @param aoMybatisSession SqlSessionobject
	 * @param aoFiscalYearList List object
	 * @param asContractID String containing ContractID
	 * @param asContractTypeId String containing ContractTypeId
	 * @param asBudgetTypeId String containing BudgetTypeId
	 * @param asAmendContractId String containing Amendment ContractId
	 * @return lsMessage String Object
	 * @throws ApplicationException Exception thrown in case of any error
	 * 
	 * 
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public String validateContractConfigUpdateAmount(SqlSession aoMybatisSession, List aoFiscalYearList,
			String asContractID, String asContractTypeId, String asBudgetTypeId, String asAmendContractId)
			throws ApplicationException
	{
		List<ContractBudgetBean> loContractBudgetBeanList = null;
		CBGridBean loCBGridBeanObj = null;
		boolean lbError = false;
		String lsMessage = null;
		Map<String, String> loBudgetInfo = new HashMap<String, String>();
		try
		{
			if (aoFiscalYearList != null)
			{
				Iterator<String> loIter = aoFiscalYearList.iterator();
				int liBigDecimalInitializer = 0;
				BigDecimal loTotalsmodifiedAmount, loTotalsPAmount, loZeroDecimalValue = new BigDecimal(
						liBigDecimalInitializer);
				while (loIter.hasNext())
				{
					String loFYPlannedAmount = null;
					loTotalsmodifiedAmount = loZeroDecimalValue;
					loTotalsPAmount = loZeroDecimalValue;
					String lsFiscalYear = loIter.next();
					loCBGridBeanObj = new CBGridBean();
					setBudgetData(asContractID, asContractTypeId, asBudgetTypeId, loBudgetInfo, lsFiscalYear);
					loContractBudgetBeanList = fetchContractConfUpdateSubBudgetDetails(aoMybatisSession, asContractID,
							lsFiscalYear, asBudgetTypeId, asContractTypeId, asAmendContractId);
					if (loContractBudgetBeanList != null)
					{
						Iterator<ContractBudgetBean> loIter1 = loContractBudgetBeanList.iterator();
						while (loIter1.hasNext())
						{
							ContractBudgetBean loContractBudgetBean = loIter1.next();
							if (loContractBudgetBean.getModifiedAmount() != null
									&& !HHSConstants.EMPTY_STRING.equals(loContractBudgetBean.getModifiedAmount()))
							{
								loTotalsmodifiedAmount = loTotalsmodifiedAmount.add(new BigDecimal(loContractBudgetBean
										.getModifiedAmount()));
							}
							if (loContractBudgetBean.getProposedBudgetAmount() != null
									&& !HHSConstants.EMPTY_STRING
											.equals(loContractBudgetBean.getProposedBudgetAmount()))
							{
								loTotalsPAmount = loTotalsPAmount.add(new BigDecimal(loContractBudgetBean
										.getProposedBudgetAmount()));
							}
						}
						loCBGridBeanObj.setFiscalYearID(lsFiscalYear);
						loCBGridBeanObj.setContractID(asContractID);
						if (asContractTypeId.equalsIgnoreCase(HHSConstants.FOUR))
						{
							loFYPlannedAmount = (String) DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo,
									HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
									HHSConstants.CS_FETCH_UPDATE_FY_PLANNED_AMOUNT, HHSConstants.JAVA_UTIL_MAP);
						}
						if (asContractTypeId.equalsIgnoreCase(HHSConstants.FOUR)
								&& loFYPlannedAmount != null
								&& (loTotalsPAmount.compareTo(new BigDecimal(loFYPlannedAmount)) != HHSConstants.INT_ZERO ? true
										: false))
						{
							lsMessage = HHSConstants.CS_VALIDATE_CONTRACT_CONFIG_UPDATE_MESSAGE;
							return lsMessage;
						}
						else if (asContractTypeId.equalsIgnoreCase(HHSConstants.FOUR))
						{
							updateBudgetFiscalYear(aoMybatisSession, loBudgetInfo, loTotalsmodifiedAmount);
						}
						else if (asContractTypeId.equalsIgnoreCase(HHSConstants.TWO))
						{
							BigDecimal loModAmount = loTotalsmodifiedAmount;
							List<ContractBudgetBean> loContractBudget = fetchAmendmentBudgetDetails(aoMybatisSession,
									asAmendContractId, lsFiscalYear, HHSConstants.TWO);
							String lsAmendmentPlannedAmount = fetchAmendmentFYPlannedAmount(aoMybatisSession,
									asContractID, lsFiscalYear, HHSConstants.TWO, asAmendContractId);
							if ((new BigDecimal(lsAmendmentPlannedAmount).compareTo(new BigDecimal(
									HHSConstants.INT_ZERO)) != HHSConstants.INT_ZERO)
									&& (loContractBudget.isEmpty()
											|| (new BigDecimal(lsAmendmentPlannedAmount).compareTo(new BigDecimal(
													loContractBudget.get(0).getTotalbudgetAmount())) != HHSConstants.INT_ZERO) || (new BigDecimal(
											loContractBudget.get(0).getTotalbudgetAmount()).compareTo(loModAmount) != HHSConstants.INT_ZERO)))
							{
								lsMessage = HHSConstants.CS_VALIDATE_AMENDMENT_CONFIG_MESSAGE;
								return lsMessage;
							}
						}
					}
				}
			}
		}
		catch (ApplicationException loExp)
		{
			if (lbError)
			{
				loExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loExp.toString());
			}
			setMoState("Error while updating Sub Budget details of contract:");
			throw loExp;
		}
		return lsMessage;
	}
	
	/**
	 * This method set budget data.
	 * @param asContractID contract id as input.
	 * @param asContractTypeId contract type id as input.
	 * @param asBudgetTypeId budget type id as input.
	 * @param loBudgetInfo budget info as input.
	 * @param lsFiscalYear fiscal year as input.
	 */
	private void setBudgetData(String asContractID, String asContractTypeId, String asBudgetTypeId,
			Map<String, String> loBudgetInfo, String lsFiscalYear)
	{
		loBudgetInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractID);
		loBudgetInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, lsFiscalYear);
		loBudgetInfo.put(HHSConstants.CONTRACT_TYPE_ID, asContractTypeId);
		loBudgetInfo.put(HHSConstants.AS_BUDGET_TYPE_ID, asBudgetTypeId);
	}

	/**
	 * This method update budget fiscal year information
	 * @param aoMybatisSession sql session as input
	 * @param loBudgetInfo budget info as input
	 * @param loTotalsmodifiedAmount modified amount as input.
	 * @throws ApplicationException Exception in case of code failure.
	 */
	private void updateBudgetFiscalYear(SqlSession aoMybatisSession, Map<String, String> loBudgetInfo,
			BigDecimal loTotalsmodifiedAmount) throws ApplicationException
	{
		loBudgetInfo.put(HHSConstants.AS_MODIFICATION_AMOUNT, loTotalsmodifiedAmount.toString());
		DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
				HHSConstants.UPDATE_BUDGET_FISCAL_YEAR_AMOUNT, HHSConstants.JAVA_UTIL_MAP);
	}

	/**
	 * This method is used to delete the Chart of Accounts details in the
	 * database on the basis chart of allocation, sub object code and reporting
	 * category.
	 * 
	 * <li>Soft delete is made disabling active flag to zero</li> Query Id
	 * 'delContractConfUpdateTaskDetails'
	 * @param aoAccountsAllocationBean AccountsAllocationBean object
	 * @param aoMybatisSession SqlSession object
	 * @throws ApplicationException Exception thrown in case of any error
	 * 
	 * 
	 */
	public void delContractConfUpdateTaskDetails(AccountsAllocationBean aoAccountsAllocationBean,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		try
		{
			if (aoAccountsAllocationBean != null
					&& aoAccountsAllocationBean.getId().indexOf(HHSConstants.NEW_RECORD) != HHSConstants.INT_MINUS_ONE)
			{
				String[] loTempIdArray = aoAccountsAllocationBean.getId().split(HHSConstants.UNDERSCORE);
				aoAccountsAllocationBean.setId(loTempIdArray[HHSConstants.INT_ZERO]);
				DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.DEL_CONTRACT_CONF_UPDATE_TASK_DETAILS, HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
				setMoState("Chart of Account Allocation Details for contract deleted successfully:");
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AO_ACCOUNTS_ALLOCATION_BEAN,
					CommonUtil.convertBeanToString(aoAccountsAllocationBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: delContractConfUpdateDetails method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: delContractConfUpdateDetails method - failed."
					+ " Exception occured while deleting chart of allocation record for AccountsAllocationBean::"
					+ CommonUtil.convertBeanToString(aoAccountsAllocationBean) + " \n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: fetchContractConfigBudgetDetails:: ", loEx);
			LOG_OBJECT.Error("Exception occured in ConfigurationService: delContractConfUpdateDetails method:: ", loEx);
			setMoState("Transaction Failed:: ConfigurationService: delContractConfUpdateDetails method - failed."
					+ " Exception occured while deleting chart of allocation record for AccountsAllocationBean::"
					+ CommonUtil.convertBeanToString(aoAccountsAllocationBean) + " \n");
			throw loAppEx;
		}
	}

	/**
	 * This method is used to delete the Sub budget details from DataBase on the
	 * basis of sub budget Id using
	 * <code>delContractConfUpdateSubBudgetDetails</code> Delete query
	 * 
	 * <li>
	 * Transaction id : <code>contractConfigurationSubBudgetGridDel</code></li>
	 * <li>Query Id 'delContractConfAmendSubBudgetDetails' is executed</li> <br>
	 * 
	 * This method was updated in R4.
	 * @param aoMybatisSession SqlSession object
	 * @param aoSubBudgetBean ContractBudgetBean object
	 * @return Boolean object
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	public Boolean delContractConfUpdateSubBudgetDetails(SqlSession aoMybatisSession, ContractBudgetBean aoSubBudgetBean)
			throws ApplicationException
	{
		try
		{
			// Removing "_new_row" appender, appended for distinguishing new row
			// for New FY Contract Budget
			if (null == aoSubBudgetBean.getParentId() || aoSubBudgetBean.getParentId().isEmpty())
			{
				String[] loTempIdArray = aoSubBudgetBean.getId().split(HHSConstants.UNDERSCORE);
				aoSubBudgetBean.setId(loTempIdArray[HHSConstants.INT_ZERO]);
			}
			if (aoSubBudgetBean.getContractTypeId().equals(HHSConstants.TWO))
			{

				// start release 3.14.0
				Integer loLastConfiguredFY = (Integer) DAOUtil.masterDAO(aoMybatisSession,
						aoSubBudgetBean.getContractId(), HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.FETCH_LAST_FY_CONFIGURED, HHSConstants.JAVA_LANG_STRING);
				// Rel 3.15.0 ; QC 6797. Added null check because query returns null unless it is Out-Year 
				if(null!=loLastConfiguredFY && loLastConfiguredFY == Integer.parseInt(aoSubBudgetBean.getBudgetfiscalYear()))
				{
					// fetch sub budget name
					String lsSubBudgetName = (String) DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.GET_SUB_BUDGET_NAME,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
					aoSubBudgetBean.setSubbudgetName(lsSubBudgetName);
					Integer loSubBudgetCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.GET_AMENDMENT_SUB_BUDGET_NAME_SAME_WITH_ANOTHER_AMENDMENT,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);

					if (loSubBudgetCount == 1)
					{
						DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
								HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.CS_DEL_CONTRACT_CONF_AMEND_SUBBUDGET_PARENT_DETAILS_NEXT_NEW_FY,
								HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
					}
					else
					{
						DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
								HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.CS_DEL_CONTRACT_CONF_AMEND_SUBBUDGET_DETAILS_NEXT_NEW_FY,
								HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
					}

				}
				// end release 3.14.0
				else
				{
				// [Start] QC 9462 R 8.5 
				// Default line items must be deleted before deleting sub_budget
										
					checkAndDeleteLineItemDefaultEntries(aoMybatisSession, aoSubBudgetBean);
					
				// [End] QC 9462 R 8.5 - need to delete all sub_budgets from default items table first
					
					DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.CS_DEL_CONTRACT_CONF_AMEND_SUBBUDGET_DETAILS,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
				}
			}
			else
			{
				DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_DEL_CONTRACT_CONF_UPDATE_SUBBUDGET_DETAILS,
						HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			}

			setMoState("Sub Budget Details for contract deleted successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while deleting Sub Budget details of contract:");
			throw loExp;
		}
		return true;
	}

	/**
	 * This method is used to merge the ContractFinancialDetails and sub budget
	 * data to base data
	 * 
	 * 
	 * <li>Soft delete is made disabling active flag to zero</li> Query Id
	 * 'updateBudgetStatus'
	 * @param aoFiscalYearList List object
	 * @param asContractID SqlSession object
	 * @param aoMybatisSession SqlSession
	 * @param aoTaskDetailsBean TaskDetailsBean
	 * @throws ApplicationException Exception thrown in case of any error
	 * 
	 * 
	 *             Release 3.6.0 Enhancement id 6484
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public void mergeContractConfUpdateFinishTask(SqlSession aoMybatisSession, String asContractID,
			String asBudgetTypeId) throws ApplicationException
	{
		boolean lbError = false;
		try
		{

			DAOUtil.masterDAO(aoMybatisSession, asContractID, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.UPDATE_BUDGET_STATUS, HHSConstants.JAVA_LANG_STRING);
			// Release 3.6.0 Enhancement id 6484
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.CONTRACT_ID_KEY, asContractID);
			loHashMap.put(HHSConstants.AS_BUDGET_TYPE_ID, asBudgetTypeId);
			ArrayList<String> loSubBudgetDetails = (ArrayList<String>) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.FETCH_SUB_BUDGET_DETAILS_FOR_UPDATE_CONF_TASK, HHSConstants.JAVA_UTIL_HASH_MAP);
			for (String lsUpdateSubBudgetId : loSubBudgetDetails)
			{
				DAOUtil.masterDAO(aoMybatisSession, Integer.parseInt(lsUpdateSubBudgetId),
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.INSERT_SUB_BUDGET_DETAILS_SITE_DETAILS_FOR_UPDATE, HHSConstants.INTEGER_CLASS_PATH);
			}

			lbError = true;
		}
		catch (ApplicationException loExp)
		{
			if (lbError)
			{
				loExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loExp.toString());
			}
			setMoState("Error while updating Sub Budget details of contract:");
			throw loExp;

		}

	}

	/**
	 * This method fetches sum total of amount from CONTRACT_FINANCIAL table for
	 * a given contract and fiscal year. Query Id
	 * 'fetchPlannedAmtForUpdatedContractId'
	 * @param aoMybatisSession SqlSession
	 * @param asContractId String
	 * @param asFiscalYearId String
	 * @return loFYPlannedAmount String
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	public String fetchPlannedAmtForUpdatedContractId(SqlSession aoMybatisSession, String asContractId,
			String asFiscalYearId) throws ApplicationException
	{
		String loFYPlannedAmount = null;
		Map<String, String> loCBGridBeanMapInfo = new HashMap<String, String>();
		try
		{
			loCBGridBeanMapInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			loCBGridBeanMapInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);
			loFYPlannedAmount = (String) DAOUtil.masterDAO(aoMybatisSession, loCBGridBeanMapInfo,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.BMC_FETCH_FY_PLANNED_AMOUNT_FOR_UPDATED, HHSConstants.JAVA_UTIL_MAP);
			setMoState("Sum of Amount for given Fiscal Year fetched successfully");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchFYPlannedAmount method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchFYPlannedAmount method - failed."
					+ " Exception occured while fetching FY Planned Amount::"
					+ CommonUtil.convertBeanToString(loCBGridBeanMapInfo) + " \n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ConfigurationService: fetchContractConfigBudgetDetails:: ", loEx);
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchFYPlannedAmount method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchFYPlannedAmount method - failed."
					+ " Exception occured while fetching FY Planned Amount::"
					+ CommonUtil.convertBeanToString(loCBGridBeanMapInfo) + " \n");
			throw loAppEx;
		}
		return loFYPlannedAmount;
	}

	/**
	 * This method gets budget details of a budget for a particular contract
	 * fiscal year
	 * <ul>
	 * <li>The fields values returned from this method are -
	 * <ul>
	 * <li>BUDGET_ID</li>
	 * <li>BUDGET_AMOUNT</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <ul>
	 * <li>Steps of execution are -
	 * <ul>
	 * <li>On the basis of asBudgetId and asFiscalYearId, the above mentioned
	 * values are received from the DataBase by executing the
	 * <code>fetchBudgetDetailsByFYAndContractId</code> query in the
	 * ConfigurationMapper</li>
	 * <li>It returns the values as ContractBudgetBean object</li>
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the UI</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * Query Id 'checkBudgetDetails'
	 * @param aoMybatisSession SqlSession
	 * @param asContractId on the basis of which Contract Sub Budgets will be
	 *            fetched
	 * @param asFiscalYearId contract current fiscal year (may be Contract's
	 *            first FY if first FY is greater than Current FY)
	 * @return ContractBudgetBean Bean
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	public String checkBudgetDetails(SqlSession aoMybatisSession, String asContractId, String asFiscalYearId)
			throws ApplicationException
	{
		String lsUpdatedContractId = null;
		Map<String, String> loBudgetInfo = new HashMap<String, String>();
		try
		{
			loBudgetInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			loBudgetInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);

			lsUpdatedContractId = (String) DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_CHECK_BUDGET_DETAILS,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Budget details for contract fetched successfully:");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting contract budget details in checkBudgetDetails() method :");
			throw loExp;
		}

		return lsUpdatedContractId;
	}

	/**
	 * This private method fetches the current status of Procurement
	 * Certification of Funds Task Status Query Id 'fetchProcurementCOFStatus'
	 * @param aoCBGridBean CBGridBean
	 * @param aoMybatisSession SqlSession
	 * @return lsPCOFStatus String
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	private String fetchPCOFStatus(CBGridBean aoCBGridBean, SqlSession aoMybatisSession) throws ApplicationException
	{
		String lsPCOFStatus;
		String lsProcurementId = aoCBGridBean.getProcurementID();
		try
		{
			lsPCOFStatus = (String) DAOUtil.masterDAO(aoMybatisSession, lsProcurementId,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_FETCH_PROCUREMENT_COF_STATUS,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("PCOF status successfully fetched for ProcurementId " + aoCBGridBean.getProcurementID());
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetching PCOF status for ProcurementId " + aoCBGridBean.getProcurementID());
			aoExp.addContextData(HHSConstants.PROCUREMENT_ID, aoCBGridBean.getProcurementID());
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchPCOFStatus method:: ", aoExp);
			throw aoExp;
		}
		return lsPCOFStatus;
	}

	/**
	 * This method creates Duplicate rows of COA grid in DB with type Updated
	 * when the PCOF task gets approved. Query Id
	 * 'insertProcurementCoADetailsDuplicate'
	 * 
	 * Query Id 'insertProcurementCoADetailsDuplicate' Query Id
	 * 'copyProcurementFundingSourceDetails' Query Id
	 * 'insertProcurementCoADetailsDuplicate'
	 * 
	 * Query Id 'copyProcurementFundingSourceDetails'
	 * @param aoMybatisSession SqlSession
	 * @param aoTaskDetailsBean TaskDetailsBean
	 * @param aoStatus Boolean
	 * @return loStatus Boolean
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public boolean createDuplicateRows(SqlSession aoMybatisSession, CBGridBean aoCbGridBean)
			throws ApplicationException
	{
		Boolean loStatus = false;
		try
		{
			// approved - means financial page grid entries
			// updated - means view cof page entries
			// task - means procurement cof page grid entries

			// coa grid - insert copy of all approved rows as updated entries
			aoCbGridBean.setType(HHSConstants.TYPE_UPDATED);
			DAOUtil.masterDAO(aoMybatisSession, aoCbGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_INSERT_PROCUREMENT_COA_DETAILS_DUPLICATE,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			// funding grid - insert copy of all approved rows as updated
			// entries
			DAOUtil.masterDAO(aoMybatisSession, aoCbGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.COPY_PROCUREMENT_FUNDING_SOURCE_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			// coa grid- insert copy of all approved rows as task entries
			aoCbGridBean.setType(HHSConstants.TASK_ROWS);
			DAOUtil.masterDAO(aoMybatisSession, aoCbGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_INSERT_PROCUREMENT_COA_DETAILS_DUPLICATE,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			// funding grid- insert copy of all approved rows as task entries
			DAOUtil.masterDAO(aoMybatisSession, aoCbGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.COPY_PROCUREMENT_FUNDING_SOURCE_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			loStatus = true;
			setMoState("Duplicated rows successfully created");
		}

		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoExp)
		{
			setMoState("Error while creating Duplicate Rows for ProcurementId " + aoCbGridBean.getProcurementID());
			aoExp.addContextData(HHSConstants.PROCUREMENT_ID, aoCbGridBean.getProcurementID());
			LOG_OBJECT.Error("Exception occured in ConfigurationService: createDuplicateRows method:: ", aoExp);
			throw aoExp;
		}

		// Handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException("Error while creating Duplicate Rows", aoExp);
			setMoState("Error while creating Duplicate Rows for ProcurementId " + aoCbGridBean.getProcurementID());
			loAppEx.addContextData(HHSConstants.PROCUREMENT_ID, aoCbGridBean.getProcurementID());
			LOG_OBJECT.Error("Exception occured in ConfigurationService: createDuplicateRows method:: ", loAppEx);
			throw loAppEx;
		}
		return loStatus;
	}

	/**
	 * <p>
	 * Changed method Build: 2.6.0 Enhancement id: 5653 Set procurement title
	 * from procurement table always instead of addendum
	 * 
	 * This method fetches details for procurement like procurement dates and
	 * title from procurement and addendum tables.It is called from screens:
	 * <ul>
	 * <li>procurement financials</li>
	 * <li>view cof on financials</li>
	 * <li>procurement certification of funds screen</li>
	 * </ul>
	 * 
	 * This method is responsible for fetching the procurement details for
	 * financials Query Id 'fetchProcurementAddendumCOFDetailsFinancials' Query
	 * Id 'fetchProcurementCOFDetailsFinancials' Query Id
	 * 'fetchProcurementOrigDetails' Query Id 'fetchProcurementCOFStatus'
	 * </p>
	 * @param aoMybatisSession
	 * @param asProcurementId
	 * @param aoIsOpenEndedOrZeroValue
	 * @return
	 * @throws ApplicationException
	 * 
	 * 
	 */
	public ProcurementCOF fetchProcurementDetailsForFinancials(SqlSession aoMybatisSession, String asProcurementId,
			Boolean aoIsOpenEndedOrZeroValue) throws ApplicationException
	{
		ProcurementCOF loProcurementCOF = null;
		String lsPCOFStatus;
		try
		{
			if (aoIsOpenEndedOrZeroValue)
			{
				return null;
			}
			loProcurementCOF = (ProcurementCOF) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_PROC_ADD_COF_DETAILS_FINANCIALS,
					HHSConstants.JAVA_LANG_STRING);
			if (null == loProcurementCOF)
			{
				loProcurementCOF = (ProcurementCOF) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_PROC_COF_DETAILS_FINANCIALS,
						HHSConstants.JAVA_LANG_STRING);
			}
			else
			{
				// fetch original values for contract, rest are fetched from
				// addendum
				ProcurementCOF loTempProcurementCOF = (ProcurementCOF) DAOUtil.masterDAO(aoMybatisSession,
						asProcurementId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.FETCH_PROC_COF_DETAILS_FINANCIALS_ORIGINAL, HHSConstants.JAVA_LANG_STRING);

				// Build 2.6.0 defect:5653 Set procurement title from
				// procurement table always, instead of addendum
				loProcurementCOF.setProcurementTitle(loTempProcurementCOF.getProcurementTitle());
				if (null != loTempProcurementCOF && null != loTempProcurementCOF.getOrigContractStartDate()
						&& !loTempProcurementCOF.getOrigContractStartDate().equals(HHSR5Constants.EMPTY_STRING))
				{
					loProcurementCOF.setOrigContractStartDate(loTempProcurementCOF.getOrigContractStartDate());
					loProcurementCOF.setOrigContractEndDate(loTempProcurementCOF.getOrigContractEndDate());
					loProcurementCOF.setOrigContractValue(loTempProcurementCOF.getOrigContractValue());

				}
				else
				{
					loProcurementCOF.setOrigContractStartDate(loProcurementCOF.getContractStartDate());
					loProcurementCOF.setOrigContractEndDate(loProcurementCOF.getContractEndDate());
					loProcurementCOF.setOrigContractValue(loProcurementCOF.getProcurementValue());
				}

			}

			lsPCOFStatus = (String) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_FETCH_PROCUREMENT_COF_STATUS,
					HHSConstants.JAVA_LANG_STRING);

			if (lsPCOFStatus == null || lsPCOFStatus.isEmpty())
			{
				loProcurementCOF.setProcurementStatus(HHSConstants.NOT_SUBMITTED);
			}
			else
			{
				loProcurementCOF.setProcurementStatus(lsPCOFStatus);
			}

			setMoState("Procurement COF details fetched successfully for Procurement COF Id:" + asProcurementId);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSR5Constants.PROCUREMENT_ID_KEY, asProcurementId);
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchProcurementDetailsForFinancials:: ",
					loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchProcurementDetailsForFinancials method - with asProcurementId::"
					+ asProcurementId + "\n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured in ConfigurationService: fetchProcurementDetailsForFinancials method::", loEx);
			loAppEx.addContextData(HHSR5Constants.PROCUREMENT_ID_KEY, asProcurementId);
			LOG_OBJECT.Error(
					"Exception occured in ConfigurationService: fetchProcurementDetailsForFinancials method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchProcurementDetailsForFinancials method - with asProcurementId::"
					+ asProcurementId + "\n");
			throw loAppEx;
		}
		return loProcurementCOF;
	}

	/**
	 * This method fetches base contract id for passed contract id. Query Id
	 * 'fetchBaseContractId'
	 * @param aoMybatisSession SqlSession
	 * @param asContractId Contract id whose base contract id is required
	 * @return String lsBaseContractId
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	public String fetchBaseContractId(SqlSession aoMybatisSession, String asContractId) throws ApplicationException
	{
		String lsBaseContractId = null;
		try
		{
			lsBaseContractId = (String) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.BMC_FETCH_BASE_CONTRACT_ID,
					HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Error occurred in fetchBaseContractId method in Configuration Service for contract id::"
					+ asContractId);
			LOG_OBJECT.Error("Exception occured in Configuration Service: fetchBaseContractId method:: ", aoExp);
			throw aoExp;
		}
		return lsBaseContractId;
	}

	/**
	 * This method fetches UPDATE contract id for passed contract id. *Query Id
	 * 'fetchUpdateContractId'
	 * @param aoMybatisSession SqlSession
	 * @param asContractId Contract id whose base contract id is required
	 * @return String lsBaseContractId
	 * @throws ApplicationException ApplicationException object
	 * 
	 */
	public String fetchUpdateContractId(SqlSession aoMybatisSession, String asContractId) throws ApplicationException
	{
		String lsUpdateContractId = null;
		try
		{
			lsUpdateContractId = (String) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.BMC_FETCH_UPDATE_CONTRACT_ID,
					HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Error occurred in fetchUpdateContractId method in ConfigurationServiceicate for contract id::"
					+ asContractId);
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchUpdateContractId method:: ", aoExp);
			throw aoExp;
		}
		return lsUpdateContractId;
	}

	/**
	 * This method fetches list of active or approved fiscal years of budgets
	 * for a particular contract id. Query Id 'fechActiveApprovedFiscalYears'
	 * @param aoMybatisSession SqlSession
	 * @param asContractId Contract id whose active/approved fiscal years are
	 *            required
	 * @return List fiscal year list
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<String> fechActiveApprovedFiscalYears(SqlSession aoMybatisSession, String asAmendContractId)
			throws ApplicationException
	{
		List<String> loFiscalYrList = null;
		try
		{
			loFiscalYrList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asAmendContractId,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_ACTIVE_APPROVED_FISCAL_YEARS,
					HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Error occurred in fechActiveApprovedFiscalYears method in ConfigurationService for contract id::"
					+ loFiscalYrList);
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fechActiveApprovedFiscalYears method:: ",
					aoExp);
			throw aoExp;
		}
		return loFiscalYrList;
	}

	/**
	 * Once the WF is launched successfully on click of submit button at S 204
	 * Financials screen.
	 * 
	 * update original contract start date, end date and value with existing
	 * field values This is required for view cof Query Id
	 * 'setOriginalProcurementValues'
	 * @param aoCbGridBean CBGridBean
	 * @param aoMyBatisSession SqlSession
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	public void setOriginalProcurementValues(CBGridBean aoCbGridBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoCbGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_SET_ORIGINAL_PROCUREMENT_VALUES, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}

		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query setPCOFStatus ", aoAppEx);
			throw aoAppEx;
		}
	}

	/**
	 * This method fetches data for budget tab (non grid data) on amendment
	 * configuration task screen
	 * <ul>
	 * <li>Query Id 'contractBudgetAmendFYData' is executed</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asContractId String containing ContractId
	 * @param asFiscalYearId String containing FiscalYearId
	 * @param asContractTypeId String containing Contract TypeId
	 * @param asAmendContractId String containing Amendment ContractId
	 * @return loContractBudgetBean ContractBudgetBean
	 * @throws ApplicationException ApplicationException obejct
	 * 
	 */
	public ContractBudgetBean contractBudgetAmendFYData(SqlSession aoMybatisSession, String asContractId,
			String asFiscalYearId, String asContractTypeId, String asAmendContractId) throws ApplicationException
	{
		ContractBudgetBean loContractBudgetBean = null;
		Map<String, String> loCBGridBeanMapInfo = new HashMap<String, String>();
		try
		{
			loCBGridBeanMapInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			loCBGridBeanMapInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);
			loCBGridBeanMapInfo.put(HHSConstants.CONTRACT_TYPE_ID, asContractTypeId);
			loCBGridBeanMapInfo.put(HHSConstants.AMENDED_CONTRACT_ID, asAmendContractId);

			loContractBudgetBean = (ContractBudgetBean) DAOUtil.masterDAO(aoMybatisSession, loCBGridBeanMapInfo,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.BMC_CONTRACT_BUDGET_AMEND_FY_DATA,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Sum of Amount for given Fiscal Year fetched successfully");
		}
		catch (ApplicationException loAppEx)
		{
			setMoState("Transaction Failed:: ConfigurationService: fetchFYPlannedAmount method - failed."
					+ " Exception occured while fetching FY Planned Amount:: \n");
			loAppEx.setContextData(loCBGridBeanMapInfo);
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchFYPlannedAmount method:: ", loAppEx);
			throw loAppEx;
		}
		return loContractBudgetBean;
	}

	/**
	 * This method check if amount value in chart allocation grid is zero than
	 * check whether its sub budgets exist or not .if exist set error to true
	 * else delete budget and sub budgets details if exist
	 * 
	 * @param aoAccountsAllocationBean AccountsAllocationBean object
	 * @param aoMybatisSession MyBatis SQL session object
	 * @return return true for error case
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private Boolean validateAmendmentBudgetZeroValue(AccountsAllocationBean aoAccountsAllocationBean,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbError = false;
		// check 1 if fiscal year amount is zero then
		if (aoAccountsAllocationBean.getAmmount().equalsIgnoreCase(HHSConstants.STRING_ZERO)
				|| aoAccountsAllocationBean.getAmmount().equalsIgnoreCase(HHSConstants.DECIMAL_ZERO))
		{

			// check 1.1 if sub budget exists then show error message
			HashMap<String, Object> loReturnMap = (HashMap<String, Object>) DAOUtil.masterDAO(aoMybatisSession,
					aoAccountsAllocationBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.FETCH_AMENDMENT_SUB_BUDGET_COUNT, HHSConstants.ACCOUNTS_ALLOCATION_BEAN);

			Integer loAmendSubBudgetCount = ((BigDecimal) loReturnMap.get(HHSConstants.SUB_BUDGET_COUNT)).intValue();
			Boolean loContFinAmtEqualZero = Boolean.parseBoolean((String) loReturnMap
					.get(HHSConstants.CONT_FIN_AMT_EQUAL_ZERO));

			if (loAmendSubBudgetCount != null && loContFinAmtEqualZero != null)
			{
				if (loContFinAmtEqualZero && loAmendSubBudgetCount > HHSConstants.INT_ZERO)
				{
					lbError = true;
				}
				else
				{
					if (loContFinAmtEqualZero)
					{
						// new QC fix, delete all dependencies tables 
						DAOUtil.masterDAO(aoMybatisSession, aoAccountsAllocationBean,
								HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.DELETE_BUDGET_WITH_ALL_SUB, HHSConstants.ACCOUNTS_ALLOCATION_BEAN);
					}
				}
			}
		}
		return lbError;
	}

	/**
	 * <p>
	 * This private method checks if default line item entries are present in
	 * table at sub budget level, then entries are deleted from individual line
	 * item table for that particular sub budget This scenario can come into
	 * picture when Contract COF is returned and at contract configuration task
	 * user is trying to delete already created sub budget
	 * <ul>
	 * <li>Check if sub budget has default line item entries</li>
	 * <li>If entries are present, then delete them from all default tables</li>
	 * </ul>
	 * <p>
	 * @param aoSubBudgetBean ContractBudgetBean object
	 * @param aoMybatisSession MyBatis SQL session object
	 * @throws ApplicationException
	 */
	private void checkAndDeleteLineItemDefaultEntries(SqlSession aoMybatisSession, ContractBudgetBean aoSubBudgetBean)
			throws ApplicationException
	{
		// step 1: check if default entries are present in one of seven default
		// tables, i.e. operations_and_support
		Integer loDefaultEntriesCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoSubBudgetBean,
				HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CHK_LINE_ITEM_DEFAULT_ENTRIES,
				HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);

		// step 1.1: if default entries are present then delete line item's
		// entries for that particular sub budget from all default tables
		if (null != loDefaultEntriesCount && loDefaultEntriesCount > HHSConstants.INT_ZERO)
		{
			HashMap<String, String> loQueryMap = new HashMap<String, String>();
			loQueryMap.put(HHSConstants.ID, aoSubBudgetBean.getId());
			List<String> loDefaultTableList = HHSConstants.DEFAULT_LINE_ITEM_TABLE_LIST;

			for (String loDefaultTableName : loDefaultTableList)
			{
				loQueryMap.put(HHSConstants.DEFAULT_TABLE_NAME, loDefaultTableName);
				DAOUtil.masterDAO(aoMybatisSession, loQueryMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.DEL_LINE_ITEM_DEFAULT_ENTRIES, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
		}
	}

	/**
	 * <p>
	 * New method - Reason: Build: 2.6.0 Enhancement id: 5653 added extra server
	 * side validation call when pcof task is approved
	 * 
	 * This method validates that procurement value and contract start and end
	 * dates are not changed
	 * </p>
	 * @param aoMybatisSession SqlSession
	 * @param aoTaskDetailsBean TaskDetailsBean
	 * @param aoFinalFinish Boolean
	 * @return lbStatus
	 * @throws ApplicationException ApplicationException object
	 */
	public boolean validateProcValueAndAllocatedValue(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean,
			Boolean aoFinalFinish) throws ApplicationException
	{
		boolean lbStatus = false;
		boolean lbValidationError = false;
		try
		{
			if (!aoTaskDetailsBean.getTaskStatus().equalsIgnoreCase(HHSP8Constants.TASK_STATUS_RETURNEDFORREVISION))
			{
				String loValidatedValue = (String) DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.VALIDATE_PROC_VAL_ALLOC_VAL,
						HHSConstants.CS_TASK_DETAILS_BEAN);
				if (loValidatedValue != null && loValidatedValue.equals(HHSConstants.ZERO))
				{
					lbValidationError = true;
					throw new ApplicationException("Pcof task allocated value and procurement value is not equal");
				}
			}
			lbStatus = true;
		}
		catch (ApplicationException aoAppEx)
		{
			if (lbValidationError)
			{
				aoAppEx.addContextData(HHSConstants.PCOF_VALIDATE_ERROR_MSG, HHSConstants.PCOF_VALIDATE_ERROR_MSG);
			}
			LOG_OBJECT
					.Error("Application Exception occured in ConfigurationService: validateProcValueAndAllocatedValue method:: ",
							aoAppEx);
			setMoState("ApplicationException occured in ConfigurationService: validateProcValueAndAllocatedValue method");
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured in ConfigurationService: validateProcValueAndAllocatedValue method:: ",
					aoExp);
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured in validateProcValueAndAllocatedValue", aoExp);
			setMoState("Exception occured in ConfigurationService: validateProcValueAndAllocatedValue method");
			throw loAppEx;
		}
		return lbStatus;
	}

	/**
	 * <p>
	 * This method is used to insert and fetch budget details for a particular
	 * fiscal year of a contract.
	 * 
	 * <ul>
	 * <li>This method gets called upon onload of contract configuration task
	 * and on hit of contract budget tab on same task</li>
	 * <li>fetch contract details</li>
	 * <li>calls another private method performBudgetProcessing that
	 * updates/inserts budget details</li>
	 * </ul>
	 * <p>
	 * @param aoMybatisSession SqlSession
	 * @param aoContractBudgetBean - a ContractBudgetBean object
	 * @return Boolean
	 * @throws ApplicationException ApplicationException Object
	 * 
	 */
	@SuppressWarnings("unchecked")
	public ContractBudgetBean contractBudgetProcessing(SqlSession aoMybatisSession, CBGridBean aoCBGridBean)
			throws ApplicationException
	{
		ContractBudgetBean loContractBudgetBean = new ContractBudgetBean();
		try
		{
			String lsFiscalYearId = aoCBGridBean.getBudgetStartYear();

			HashMap<String, Object> loContractDetailMap = (HashMap<String, Object>) DAOUtil.masterDAO(aoMybatisSession,
					aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.BMC_FETCH_CONTRACT_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			// start Build 3.1.0, enhancement id: 6020, get budget start year
			lsFiscalYearId = (null != ((BigDecimal) loContractDetailMap.get(HHSR5Constants.BUDGET_START_YEAR))) ? ((BigDecimal) loContractDetailMap
					.get(HHSR5Constants.BUDGET_START_YEAR)).toString() : lsFiscalYearId;
			// end

			loContractBudgetBean = performBudgetProcessing(aoMybatisSession, aoCBGridBean, loContractBudgetBean,
					lsFiscalYearId, loContractDetailMap);

			setMoState("configurationservice: contractBudgetProcessing method successfully passed");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSR5Constants.AO_CB_GRID_BEAN, CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: contractBudgetProcessing method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: contractBudgetProcessing method - with aoCBGridBean::"
					+ aoCBGridBean + "\n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured in ConfigurationService: contractBudgetProcessing method::", loEx);
			loAppEx.addContextData(HHSR5Constants.AO_CB_GRID_BEAN, CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: contractBudgetProcessing method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: contractBudgetProcessing method - with aoCBGridBean::"
					+ aoCBGridBean + "\n");
			throw loAppEx;
		}
		return loContractBudgetBean;
	}

	/**
	 * <p>
	 * This method is used to insert and fetch budget details for a new fiscal
	 * year budget configuration.
	 * 
	 * <ul>
	 * <li>This method gets called upon hit of contract budget tab on new fiscal
	 * year configuration task</li>
	 * <li>fetch contract details</li>
	 * <li>calls another private method performBudgetProcessing that
	 * updates/inserts budget details</li>
	 * <li>copies previous year sub budgets with zero values to this new budget
	 * getting configured</li>
	 * <li>copies previous year selected tabs to this new budget getting
	 * configured</li>
	 * </ul>
	 * <p>
	 * @param aoMybatisSession SqlSession
	 * @param aoContractBudgetBean - a ContractBudgetBean object
	 * @return Boolean
	 * @throws ApplicationException ApplicationException Object
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public ContractBudgetBean newFYBudgetProcessing(SqlSession aoMybatisSession, CBGridBean aoCBGridBean,
			Boolean aoNewFYTaskBudgetTab) throws ApplicationException
	{
		ContractBudgetBean loContractBudgetBean = new ContractBudgetBean();
		try
		{
			String lsFiscalYearId = aoCBGridBean.getBudgetStartYear();

			HashMap<String, Object> loContractDetailMap = (HashMap<String, Object>) DAOUtil.masterDAO(aoMybatisSession,
					aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.BMC_FETCH_CONTRACT_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			loContractBudgetBean = performBudgetProcessing(aoMybatisSession, aoCBGridBean, loContractBudgetBean,
					lsFiscalYearId, loContractDetailMap);

			if (aoNewFYTaskBudgetTab)
			{
				copyPreviousFYSubBudgetToCurrentFY(aoMybatisSession, aoCBGridBean.getContractID(), lsFiscalYearId,
						loContractBudgetBean.getBudgetId(), aoCBGridBean.getModifyByAgency());

				copyPreviousCBCToCurrentCBC(aoMybatisSession, aoCBGridBean.getContractID(), lsFiscalYearId,
						loContractBudgetBean.getBudgetId(), aoCBGridBean.getModifyByAgency());
			}
			// release 3.14.0
			loContractBudgetBean.setModifiedByUserId(aoCBGridBean.getModifyByAgency());
			DAOUtil.masterDAO(aoMybatisSession, loContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.UPDATE_BUDGET_CUSTOMIZATION_ON_NEW_FY_BUDGET,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			// release 3.14.0
			setMoState("configurationservice: newFYBudgetProcessing method successfully passed");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSR5Constants.AO_CB_GRID_BEAN, CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: newFYBudgetProcessing method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: newFYBudgetProcessing method - with aoCBGridBean::"
					+ aoCBGridBean + "\n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured in ConfigurationService: newFYBudgetProcessing method::", loEx);
			loAppEx.addContextData(HHSR5Constants.AO_CB_GRID_BEAN, CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ConfigurationService: newFYBudgetProcessing method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: newFYBudgetProcessing method - with aoCBGridBean::"
					+ aoCBGridBean + "\n");
			throw loAppEx;
		}
		return loContractBudgetBean;
	}

	/**
	 * <p>
	 * This is private method used to perform budget processing
	 * <ul>
	 * <li>fetchFYPlannedAmount fetches budget amount based upon values in coa
	 * grid</li>
	 * <li>fetchBudgetDetailsByFYAndContractId fetches budget amount based upon
	 * value entered in contractbudget tab</li>
	 * <li>If budget entry is found in table then inserts zero value budget</li>
	 * <li>update budget value whenever there is mismatch between coagrid budget
	 * value and budget tab budget value</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoCBGridBean CBGridBean object
	 * @param aoContractBudgetBean ContractBudgetBean object
	 * @param asFiscalYearId String object
	 * @param aoContractDetailMap HashMap with type <String, Object>
	 * @throws Exception Exception class object
	 * 
	 */
	private ContractBudgetBean performBudgetProcessing(SqlSession aoMybatisSession, CBGridBean aoCBGridBean,
			ContractBudgetBean aoContractBudgetBean, String asFiscalYearId, HashMap<String, Object> aoContractDetailMap)
			throws Exception
	{

		String lsFYBudgetPlannedAmt = fetchFYPlannedAmount(aoMybatisSession, aoCBGridBean.getContractID(),
				asFiscalYearId);
		lsFYBudgetPlannedAmt = lsFYBudgetPlannedAmt != null ? lsFYBudgetPlannedAmt : HHSR5Constants.ZERO;
		aoContractBudgetBean.setPlannedAmount(lsFYBudgetPlannedAmt);

		List<ContractBudgetBean> loReturnedBudgetDetails = fetchBudgetDetailsByFYAndContractId(aoMybatisSession,
				aoCBGridBean.getContractID(), asFiscalYearId);

		if (null == loReturnedBudgetDetails || loReturnedBudgetDetails.isEmpty())
		{
			populateContractBudgetBean(aoContractBudgetBean, aoContractDetailMap, aoCBGridBean, asFiscalYearId);

			insertNewBudgetDetails(aoMybatisSession, aoContractBudgetBean);
			loReturnedBudgetDetails = fetchBudgetDetailsByFYAndContractId(aoMybatisSession,
					aoCBGridBean.getContractID(), asFiscalYearId);
		}
		aoContractBudgetBean = (ContractBudgetBean) loReturnedBudgetDetails.get(HHSConstants.INT_ZERO);

		if (new BigDecimal(lsFYBudgetPlannedAmt).compareTo(new BigDecimal(aoContractBudgetBean.getTotalbudgetAmount())) != HHSConstants.INT_ZERO)
		{
			aoContractBudgetBean.setTotalbudgetAmount(lsFYBudgetPlannedAmt);
			updateBudgetFYTotalBudgetAmount(aoMybatisSession, aoContractBudgetBean);
		}

		aoContractBudgetBean
				.setContractValue(((BigDecimal) aoContractDetailMap.get(HHSR5Constants.CLC_CONTRACT_AMOUNT)).toString());
		return aoContractBudgetBean;
	}

	/**
	 * <p>
	 * This is private method used to populate ContractBudgetBean and called
	 * during new fiscal year configuration and contract configuration tasks
	 * <ul>
	 * <li>Populates bean variable</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoContractBudgetBean ContractBudgetBean object
	 * @param aoContractDetailMap HashMap with type <String, Object>
	 * @param aoCBGridBean CBGridBean object
	 * @param asBudgetFiscalYearId String object
	 * @throws Exception Exception class object
	 * 
	 */
	private void populateContractBudgetBean(ContractBudgetBean aoContractBudgetBean,
			HashMap<String, Object> aoContractDetailMap, CBGridBean aoCBGridBean, String asBudgetFiscalYearId)
			throws Exception
	{
		
		aoContractBudgetBean.setBudgetfiscalYear(asBudgetFiscalYearId);

		/* [Start] R9.4.0 QC9627  */
		aoContractBudgetBean.setBudgetStartDate(HHSUtil.getNewBudgetStartDate_MMDDYYYY(
				(String) aoContractDetailMap.get(HHSR5Constants.CONTRACT_START_DATE_KEY), asBudgetFiscalYearId));
		aoContractBudgetBean.setBudgetEndDate(HHSUtil.getNewBudgetEndDate_MMDDYYYY(
				(String) aoContractDetailMap.get(HHSR5Constants.CONTRACT_END_DATE_KEY), asBudgetFiscalYearId));
		/* [End] R9.4.0 QC9627  */

		// Start QC 9388 R 8.4
		// compare budget start and end dates with contract start and end dates. if out of range, replace them with contract start/end date
		Date dateBudgetStart = new SimpleDateFormat("MM/dd/yyyy").parse(aoContractBudgetBean.getBudgetStartDate()); 
		Date dateBudgetEnd = new SimpleDateFormat("MM/dd/yyyy").parse(aoContractBudgetBean.getBudgetEndDate()); 
		Date dateContractStart = new SimpleDateFormat("MM/dd/yyyy").parse((String) aoContractDetailMap.get(HHSR5Constants.CONTRACT_START_DATE_KEY));
		Date dateContractEnd = new SimpleDateFormat("MM/dd/yyyy").parse( (String) aoContractDetailMap.get(HHSR5Constants.CONTRACT_END_DATE_KEY));
				
		LOG_OBJECT.Debug("Budget Start Date :: " + aoContractBudgetBean.getBudgetStartDate());
		LOG_OBJECT.Debug("Budget End  Date :: " + aoContractBudgetBean.getBudgetEndDate());
		LOG_OBJECT.Debug("Contract Start Date :: " + (String) aoContractDetailMap.get(HHSR5Constants.CONTRACT_START_DATE_KEY));
		LOG_OBJECT.Debug("Contract End  Date :: " + (String) aoContractDetailMap.get(HHSR5Constants.CONTRACT_END_DATE_KEY));
				
		if (null != dateBudgetStart && null != dateContractStart && dateBudgetStart.before(dateContractStart))
	    {		    	
			aoContractBudgetBean.setBudgetStartDate((String) aoContractDetailMap.get(HHSR5Constants.CONTRACT_START_DATE_KEY));
	    }

		if (null != dateBudgetEnd && null != dateContractEnd && dateBudgetEnd.after(dateContractEnd))
	    { 
			aoContractBudgetBean.setBudgetEndDate((String) aoContractDetailMap.get(HHSR5Constants.CONTRACT_END_DATE_KEY));
	    }

		LOG_OBJECT.Debug("Budget Start Date After :: " + aoContractBudgetBean.getBudgetStartDate());
		LOG_OBJECT.Debug("Budget End  Date After :: " + aoContractBudgetBean.getBudgetEndDate());
		// End QC 9388 R 8.4
		
		aoContractBudgetBean.setTotalbudgetAmount(aoContractBudgetBean.getPlannedAmount());
		aoContractBudgetBean.setPlannedAmount(aoContractBudgetBean.getPlannedAmount());
		aoContractBudgetBean.setBudgetTypeId(Integer.parseInt(HHSConstants.TWO));
		aoContractBudgetBean.setContractId(aoCBGridBean.getContractID());
		aoContractBudgetBean.setCreatedByUserId(aoCBGridBean.getModifyByAgency());
		aoContractBudgetBean.setModifiedByUserId(aoCBGridBean.getModifyByAgency());
		aoContractBudgetBean.setStatusId(HHSConstants.BUDGET_PENDING_CONFIGURATION_STATUS_ID);
	}

	/**
	 * <ul>
	 * <li>This method is used to copy base Contract Budget Customization to the
	 * current Contract Budget Customization.<br>
	 * Before copying the base Contract Budget Customization to the current one,
	 * it checks for the current budget have data in budgetCustomization table.</li>
	 * <li>calls the query 'countEntryTypeFromCBC'</li>
	 * <li>calls the query 'getBudgetFromContractAndFiscalYEar'</li>
	 * <li>calls the query 'fetchEntryTypeDetails'</li>
	 * <li>calls the query 'insertBudgetCustomization'</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asContractId String
	 * @param asFiscalYearId String
	 * @param asBudgetId String
	 * @param asUserId String
	 * @return lbCopyDataToCurrentYear Boolean holds the result of
	 *         transaction;true=success;false=failure
	 * @throws ApplicationException ApplicationException object
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private Boolean copyPreviousCBCToCurrentCBC(SqlSession aoMybatisSession, String asContractId,
			String asFiscalYearId, String asBudgetId, String asUserId) throws ApplicationException
	{
		Boolean lbCopyDataToCurrentYear = Boolean.FALSE;
		HashMap loHashMap = new HashMap<String, String>();
		try
		{
			loHashMap.put(HHSConstants.CONTRACT_ID, asContractId);
			loHashMap.put(HHSConstants.BMC_BUDGET_FISCAL_YEAR, String.valueOf(Integer.valueOf(asFiscalYearId) - 1));
			loHashMap.put(HHSConstants.BUDGET_ID, asBudgetId);
			// check for the current budget No. of EntityType
			Integer loEntity = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.COUNT_ENTRY_TYPE_FROM_CBC,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			if (loEntity <= HHSConstants.INT_ZERO)
			{
				loHashMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
				HashMap loEntryTypeParams = new HashMap<String, String>();
				loEntryTypeParams.put(HHSConstants.CONTRACT_ID, asContractId);
				// set base budgetId
				loEntryTypeParams.put(HHSConstants.BUDGET_ID, (String) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.GET_BUDGET_FROM_CONTRACT_AND_FY,
						HHSConstants.JAVA_UTIL_HASH_MAP));
				List<String> loEntryTypeList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loEntryTypeParams,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_ENTRY_TYPE_DETAILS,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				loHashMap.put(HHSConstants.PUBLISHED, HHSConstants.ZERO);
				if (loEntryTypeList.size() > HHSConstants.INT_ZERO)
				{
					for (int liCount = 0; liCount < loEntryTypeList.size(); liCount++)
					{
						loHashMap.put(HHSConstants.ENTRY_TYPE_ID, loEntryTypeList.get(liCount)
								.split(HHSConstants.COLON)[HHSConstants.INT_ZERO]);
						DAOUtil.masterDAO(aoMybatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.INSERT_BUDGET_CUSTOMIZATION, HHSConstants.JAVA_UTIL_HASH_MAP);
					}
				}
				else
				{
					loEntryTypeList.addAll(HHSConstants.ENTRY_TYPE_PUBLISHED);
					for (int liCount = 0; liCount < loEntryTypeList.size(); liCount++)
					{
						loHashMap.put(HHSConstants.ENTRY_TYPE_ID, loEntryTypeList.get(liCount)
								.split(HHSConstants.COLON)[HHSConstants.INT_ZERO]);
						DAOUtil.masterDAO(aoMybatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.INSERT_BUDGET_CUSTOMIZATION, HHSConstants.JAVA_UTIL_HASH_MAP);
					}
				}
			}
			lbCopyDataToCurrentYear = Boolean.TRUE;
			setMoState("sucessfully executed copyPreviousCBCToCurrentCBC method");
		}
		catch (ApplicationException loExp)
		{
			setMoState("copyPreviousCBCToCurrentCBC execution failed");
			throw loExp;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsService: copyPreviousCBCToCurrentCBC method:: ", loEx);
			setMoState("Exception while executing copyPreviousCBCToCurrentCBC method ");
			throw new ApplicationException("Exception while executing copyPreviousCBCToCurrentCBC method", loEx);
		}
		return lbCopyDataToCurrentYear;
	}

	/**
	 * This method added for Release 3.2.0 enhancement 5684 This method fetches
	 * the current status of Procurement Certification of Funds Task Status
	 * Query Id 'fetchProcurementCOFStatus'
	 * @param aoCBGridBean CBGridBean
	 * @param aoMybatisSession SqlSession
	 * @return lsPCOFStatus String
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	public String fetchProcCOFStatus(SqlSession aoMybatisSession, String asProcurementID) throws ApplicationException
	{
		String lsPCOFStatus;
		try
		{
			lsPCOFStatus = (String) DAOUtil.masterDAO(aoMybatisSession, asProcurementID,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_PCOF_STATUS,
					HHSConstants.JAVA_LANG_STRING);
			if (lsPCOFStatus == null)
			{
				lsPCOFStatus = HHSConstants.EMPTY_STRING;
			}
			setMoState("PCOF status successfully fetched for ProcurementId " + asProcurementID);
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetching PCOF status for ProcurementId " + asProcurementID);
			aoExp.addContextData(HHSConstants.PROCUREMENT_ID, asProcurementID);
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchPCOFStatus method:: ", aoExp);
			throw aoExp;
		}
		return lsPCOFStatus;
	}

	/**
	 * Method changed for R5 This method added for Release 3.2.0 enhancement
	 * 5684
	 * @param aoFilenetSession
	 * @param aoMybatisSession
	 * @param asProcurementID
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean fetchPCOFTaskCount(P8UserSession aoFilenetSession, SqlSession aoMybatisSession,
			String asProcurementID) throws ApplicationException
	{
		Integer loOpenTaskCount = 0;
		Integer loProcFinancialCount = 0;
		Boolean lbTaskLaunch = false;
		try
		{
			HashMap loHmWFProperties = new HashMap();
			loHmWFProperties.put(HHSConstants.PROPERTY_PE_PROCURMENT_ID, asProcurementID);
			loHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_PROCUREMENT_COF);
			loOpenTaskCount = loOpenTaskCount
					+ new P8ProcessServiceForSolicitationFinancials().getOpenTaskCount(aoFilenetSession,
							loHmWFProperties);

			loHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSR5Constants.TASK_APPROVE_PSR);
			loOpenTaskCount = loOpenTaskCount
					+ new P8ProcessServiceForSolicitationFinancials().getOpenTaskCount(aoFilenetSession,
							loHmWFProperties);

			loHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSR5Constants.TASK_COMPLETE_PSR);
			loOpenTaskCount = loOpenTaskCount
					+ new P8ProcessServiceForSolicitationFinancials().getOpenTaskCount(aoFilenetSession,
							loHmWFProperties);

			if (loOpenTaskCount == 0)
			{
				loProcFinancialCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcurementID,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_PCOF_FIN_ENTRY,
						HHSConstants.JAVA_LANG_STRING);
				if (loProcFinancialCount > 0)
				{
					lbTaskLaunch = true;
				}
				else
				{
					Integer loPSRCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcurementID,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSR5Constants.FETCH_PSR_COUNT,
							HHSConstants.JAVA_LANG_STRING);
					if (loPSRCount > 0)
					{
						lbTaskLaunch = true;
					}
				}
			}
			else
			{
				lbTaskLaunch = true;
			}
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetching PCOF status for ProcurementId " + asProcurementID);
			aoExp.addContextData(HHSConstants.PROCUREMENT_ID, asProcurementID);
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchPCOFTaskCount method:: ", aoExp);
			throw aoExp;
		}
		return lbTaskLaunch;
	}

	/**
	 * This method is added for Release 3.6.0 for enhancement request #6496. The
	 * method fetches budget details for the contract for sending
	 * Notification/Alert NT403/AL403.
	 * @param aoMybatisSession SqlSession object
	 * @param asContractId String Contract Id
	 * @return
	 * @throws ApplicationException
	 */
	public HashMap fetchBudgetDetailsForNT403(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		HashMap loBudgetDetails = new HashMap();
		HashMap loHashMap = new HashMap<String, String>();
		try
		{
			loHashMap.put(HHSConstants.CONTRACT_ID1, asContractId);
			loBudgetDetails = (HashMap) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_BUD_DETAILS_NT403,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetching Budget Details for contractId " + asContractId);
			aoExp.addContextData(HHSConstants.PROCUREMENT_ID, asContractId);
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchBudgetDetailsForNT403 method:: ", aoExp);
			throw aoExp;
		}
		return loBudgetDetails;
	}

	/**
	 * This method is added for Release 3.8.0 #6483 This method deletes the
	 * entries associated with a contract update for canceling the contract
	 * update configuration.
	 * @param aoMybatisSession
	 * @param asContractId
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean deleteForCancelUpdate(SqlSession aoMybatisSession, String asContractId) throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.DELETE_BUDGET_CUST_CANCEL_UPDATE, HHSConstants.JAVA_LANG_STRING);
			DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.DELETE_SUB_BUDGET_CANCEL_UPDATE, HHSConstants.JAVA_LANG_STRING);
			DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.DELETE_BUDGET_CANCEL_UPDATE, HHSConstants.JAVA_LANG_STRING);
			DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.DELETE_CONTRACT_FIN_CANCEL_UPDATE, HHSConstants.JAVA_LANG_STRING);
			DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.DELETE_CONTRACT_FUNDING_CANCEL_UPDATE, HHSConstants.JAVA_LANG_STRING);
			DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.DELETE_UPDATE_DOCUMENT, HHSConstants.JAVA_LANG_STRING);
			DAOUtil.masterDAO(aoMybatisSession, asContractId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.DELETE_CONTRACT_CANCEL_UPDATE, HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Error while deleting budget and contract details for cancelling contract configurations "
					+ asContractId);
			aoExp.addContextData(HHSConstants.CONTRACT_ID, asContractId);
			LOG_OBJECT.Error("Exception occured in ConfigurationService: deleteForCancelUpdate method:: ", aoExp);
			throw aoExp;
		}
		return true;
	}
	/**
	 * This method is used to fetch update contract documents
	 * @param aoMybatisSession
	 * @param asContractId
	 * @return loUnlinkedContractDocsList
	 * @throws ApplicationException
	 */
	public List<String> fetchUpdateContractDocs(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		List<String> loContractDocsList = null;
		List<String> loUnlinkedContractDocsList = new ArrayList<String>();
		LOG_OBJECT.Debug("Entered into fetchUpdateContractDocs:: " + asContractId);
		try
		{
			loContractDocsList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSR5Constants.FETCH_UPDATE_CONTRACT_DOCS,
					HHSConstants.JAVA_LANG_STRING);
			if (loContractDocsList != null && !loContractDocsList.isEmpty())
			{
				for (String loContractDoc : loContractDocsList)
				{
					Integer liNoRowsExists = (Integer) DAOUtil.masterDAO(aoMybatisSession, loContractDoc,
							HHSConstants.MAPPER_CLASS_COMMON_MAPPER, HHSConstants.CHECK_DOCUMENT_EXISTS_IN_ANY_TABLE,
							HHSConstants.JAVA_LANG_STRING);
					if (liNoRowsExists <= HHSConstants.INT_ONE)
					{
						loUnlinkedContractDocsList.add(loContractDoc);
					}
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured for fetching Contract Details in FinancialsListService: fetchUpdateContractDocs ",
							loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchUpdateContractDocs method - failed to fetch"
					+ asContractId + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			// Handle the exception of type ApplicationException
			LOG_OBJECT
					.Error("Exception occured for fetching Contract details in FinancialsListService: fetchContractBudgetDocs ",
							loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchContractBudgetDocs method - failed to fetch"
					+ asContractId + " \n");
			throw new ApplicationException("Error occured while fetching Contract doc details", loAppEx);
		}
		return loUnlinkedContractDocsList;
	}

	/**
	 * This method is added for Release 3.8.0 #6483 This method fetches the
	 * contract id if fms discrepancy exists for the contract.
	 * @param aoMybatisSession SqlSession object
	 * @param asContractId String
	 * @return
	 * @throws ApplicationException
	 */
	public String fetchDiscrepencyDetailsForUpdateTask(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		String lsSelectedContractId = null;
		try
		{
			lsSelectedContractId = (String) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.FETCH_DISCREPENCY_DETAILS_UPDATES_TASK, HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetching Discrepency Details for contractId " + asContractId);
			aoExp.addContextData(HHSConstants.CONTRACT_ID, asContractId);
			LOG_OBJECT.Error(
					"Exception occured in ConfigurationService: fetchDiscrepencyDetailsForUpdateTask method:: ", aoExp);
			throw aoExp;
		}
		return lsSelectedContractId;
	}

	/**
	 * This method is added for Release 3.9.0 #6524 This method fetches info for
	 * CoF configuration
	 * @param aoMybatisSession SqlSession object
	 * @param asContractId String
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<ContractCOFDetails> fetchRenewalContractCoF(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		List<ContractCOFDetails> loC = null;

		try
		{
			loC = (List<ContractCOFDetails>) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CONTRACT_RENEWAL_DETAIL_INFO,
					HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Error while fetching Discrepency Details for contractId " + asContractId);
			aoExp.addContextData(HHSConstants.CONTRACT_ID, asContractId);
			LOG_OBJECT.Error(
					"Exception occured in ConfigurationService: fetchDiscrepencyDetailsForUpdateTask method:: ", aoExp);
			throw aoExp;
		}

		return loC;
	}

	/**
	 * This method is added for Release 3.12.0 #6602 This method deletes the
	 * entries associated with a contract for canceling the Configure New FY
	 * task.
	 * @param aoMybatisSession
	 * @param asContractId
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean deleteForCancelConfigureNewFY(SqlSession aoMybatisSession, ContractBudgetBean aoContractBudgetBean)
			throws ApplicationException
	{
		try
		{
			// Start: Added in R7 for cost center
			DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSR5Constants.DELETE_SERV_CONFIGURATIONS, HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			// End: Added in R7 for cost center
			DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.DELETE_NEWLY_ADDED_CONFIN_ROWS, HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.COPY_PREVIOUS_AMOUNT_TO_AMOUNT_FOR_FIN,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.COPY_PREVIOUS_AMOUNT_TO_AMOUNT_FOR_FIN_FUNDING,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.DELETE_BUDGET_CUST_FOR_CANCEL_CONFIGURE_NEWFY,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			/*Start: added in release 7 for defect 8884 :it will delete newly added subbudgets during NFY task on cancellation of NFY task  */
			List<String> loSubBudgetList = new ArrayList<String>();
					String lsSubBudgetId = null;
			loSubBudgetList = (List<String>) DAOUtil
								.masterDAO(
										aoMybatisSession,
										aoContractBudgetBean,
										HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
										HHSR5Constants.FETCH_SUB_BUDGET_LISTS_DETAILS,
										HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
						Iterator loItr = loSubBudgetList.iterator();
						while (loItr.hasNext()) {
							lsSubBudgetId = (String) loItr.next();
							aoContractBudgetBean.setSubBudgetId(lsSubBudgetId);
							DAOUtil.masterDAO(
									aoMybatisSession,
									aoContractBudgetBean,
									HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
									HHSConstants.DELETE_NEWLY_ADDED_SUB_BUDGETS,
									HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
						
						}
			/*Ends: added in release 7 for defect 8884   */
			// start release 3.14.0
			DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.UPDATE_SUB_BUDGET_CUST_FOR_CANCEL_CONFIGURE_NEWFY,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.UPDATE_BUDGET_FOR_CANCEL_CONFIGURE_NEWFY,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			// end release 3.14.0
			DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.DELETE_NEW_FY_DOCUMENTS, HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);

			setMoState("Details deleted successfully for Cancelling Configure NewFY");
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Error while deleting budget and contract details for cancelling ConfigureNewFY "
					+ aoContractBudgetBean.getContractId());
			LOG_OBJECT.Error("Exception occured in ConfigurationService: deleteForCancelUpdate method:: ", aoExp);
			throw aoExp;
		}
		return true;
	}

	/**
	 * This method is used to delete line items of sub budget while deleting
	 * contract
	 * @param aoMybatisSession
	 * @param asContractId
	 * @throws ApplicationException
	 */
	private void deleteSubBudgetLineItems(SqlSession aoMybatisSession, String asAmendmentBudgetId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into deleteSubBudgetLineItems:: " + asAmendmentBudgetId);
		DAOUtil.masterDAO(aoMybatisSession, asAmendmentBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
				HHSConstants.DELETE_PERSONNEL_SERVICE_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asAmendmentBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
				HHSConstants.DELETE_OP_SUPPORT_OTHERS_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asAmendmentBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
				HHSConstants.DELETE_OP_SUPPORT_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asAmendmentBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
				HHSConstants.DELETE_UTILITIES_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asAmendmentBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
				HHSConstants.DELETE_PROFF_SERVICE_OTHERS_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asAmendmentBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
				HHSConstants.DELETE_PROFF_SERVICE_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asAmendmentBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
				HHSConstants.DELETE_RENT_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asAmendmentBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
				HHSConstants.DELETE_CONTRACTED_SERVICE_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asAmendmentBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
				HHSConstants.DELETE_RATE_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asAmendmentBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
				HHSConstants.DELETE_MILESTONE_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asAmendmentBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
				HHSConstants.DELETE_UNALLOCATED_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asAmendmentBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
				HHSConstants.DELETE_INDIRECT_RATE_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asAmendmentBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
				HHSConstants.DELETE_PROG_INCOME_OTHERS_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asAmendmentBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
				HHSConstants.DELETE_PROGRAM_INCOME_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asAmendmentBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
				HHSConstants.DELETE_FRINGE_BENEFIT_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
		DAOUtil.masterDAO(aoMybatisSession, asAmendmentBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
				HHSConstants.DELETE_EQUIPMENT_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
	}

	/**
	 * This method is added for Release 3.12.0 #6602 This method copies the
	 * Fiscal Year Amount from Contract Financials table and copy to Previous
	 * Amount column before the Configure New FY task is launched..
	 * @param aoMybatisSession
	 * @param asContractId
	 * @param asUserId
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean copyFYAmountToPreviousAmountForFinancials(SqlSession aoMybatisSession, String asContractId,
			String asUserId) throws ApplicationException
	{
		try
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSConstants.AS_USER_ID, asUserId);
			loParamMap.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			DAOUtil.masterDAO(aoMybatisSession, loParamMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.COPY_FY_AMOUNT_TO_PREVIOUS_AMOUNT_FOR_FIN, HHSConstants.JAVA_UTIL_HASH_MAP);
			DAOUtil.masterDAO(aoMybatisSession, loParamMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.COPY_FY_AMOUNT_TO_PREVIOUS_AMOUNT_FOR_FIN_FUNDING, HHSConstants.JAVA_UTIL_HASH_MAP);
			setMoState("FY Amount copied successfully to previous amount");
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Error while copying fiscal year amounts: " + asContractId);
			LOG_OBJECT.Error(
					"Exception occured in ConfigurationService: copyFYAmountToPreviousAmountForFinancials method:: ",
					aoExp);
			throw aoExp;
		}
		return true;
	}
	/**
	 * This method is used to get details of already launched fiscal year task
	 * @param aoMybatisSession
	 * @param asContractId
	 * @return loIsAlreadyLaunchedFYTask
	 * @throws ApplicationException
	 */
	public Boolean isAlreadyLaunchedFYTask(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		Boolean loIsAlreadyLaunchedFYTask = false;
		try
		{
			BigDecimal lsPreviousAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSR5Constants.ALREADY_LAUNCHED_FY_TASK,
					HHSConstants.JAVA_LANG_STRING);
			if (null == lsPreviousAmount)
			{
				loIsAlreadyLaunchedFYTask = true;
			}
		}
		catch (ApplicationException aoExp)
		{
			setMoState("Error while checking the FY task is alreadyLaunched: " + asContractId);
			LOG_OBJECT.Error(
					"Exception occured in ConfigurationService: copyFYAmountToPreviousAmountForFinancials method:: ",
					aoExp);
			throw aoExp;
		}
		return loIsAlreadyLaunchedFYTask;
	}
	/**
	 * This method is used to fetch new fiscal year contract documents
	 * @param aoMybatisSession
	 * @param aoContractBudgetBean
	 * @return loUnlinkedContractDocsList
	 * @throws ApplicationException
	 */
	public List<String> fetcNewFYContractDocs(SqlSession aoMybatisSession, ContractBudgetBean aoContractBudgetBean)
			throws ApplicationException
	{
		List<String> loContractDocsList = null;
		List<String> loUnlinkedContractDocsList = new ArrayList<String>();
		LOG_OBJECT.Debug("Entered into fetcNewFYContractDocs:: " + aoContractBudgetBean.getContractId());
		try
		{
			loContractDocsList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSR5Constants.FETCH_NEW_FY_CONTRACT_DOCS,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			if (loContractDocsList != null && !loContractDocsList.isEmpty())
			{
				for (String loContractDoc : loContractDocsList)
				{
					Integer liNoRowsExists = (Integer) DAOUtil.masterDAO(aoMybatisSession, loContractDoc,
							HHSConstants.MAPPER_CLASS_COMMON_MAPPER, HHSConstants.CHECK_DOCUMENT_EXISTS_IN_ANY_TABLE,
							HHSConstants.JAVA_LANG_STRING);
					if (liNoRowsExists <= HHSConstants.INT_ONE)
					{
						loUnlinkedContractDocsList.add(loContractDoc);
					}
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured for fetching Contract Details in FinancialsListService: fetchContractBudgetDocs ",
							loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchContractBudgetDocs method - failed to fetch"
					+ aoContractBudgetBean.getContractId() + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			// Handle the exception of type ApplicationException
			LOG_OBJECT
					.Error("Exception occured for fetching Contract details in FinancialsListService: fetchContractBudgetDocs ",
							loAppEx);
			setMoState("Transaction Failed:: FinancialsListService: fetchContractBudgetDocs method - failed to fetch"
					+ aoContractBudgetBean.getContractId() + " \n");
			throw new ApplicationException("Error occured while fetching Contract doc details", loAppEx);
		}
		return loUnlinkedContractDocsList;
	}

	/**
	 * The method added for Release 3.12.0 defect 6585.
	 * @param aoMybatisSession
	 * @param asContractId
	 * @return
	 * @throws ApplicationException
	 */
	public String fetchContractSourceId(SqlSession aoMybatisSession, String asContractId) throws ApplicationException
	{
		String lsContractSource = HHSConstants.EMPTY_STRING;
		try
		{

			lsContractSource = (String) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.BMC_FETCH_CONTRACT_SOURCE,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Contract Source Id fetched successfully for Contract Id:" + asContractId);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchContractSourceId method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchContractSourceId method - failed."
					+ " Exception occured while fetching Contract Source Id for contractId::" + asContractId + " \n");
			throw loAppEx;
		}
		return lsContractSource;
	}
	/**
	 * This method is used top validate new fiscal year finish
	 * @param aoMybatisSession a SqlSession object 
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @return loHMReqdProp
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap newFYFinishValidate(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		HashMap loHMReqdProp = new HashMap();
		try
		{
			int liOpenTaskAmendmentConf = HHSConstants.INT_ZERO;

			List<String> loContractId = (List<String>) DAOUtil.masterDAO(aoMybatisSession,
					aoTaskDetailsBean.getContractId(), HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.FETCH_ALL_CONTRACT_ID_FOR_NEW_FY_CHECK, HHSConstants.JAVA_LANG_STRING);

			if (loContractId != null)
			{
				for (String lsContractId : loContractId)
				{
					loHMReqdProp.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsContractId);
					loHMReqdProp.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_AMENDMENT_CONFIGURATION);
					loHMReqdProp.put(HHSConstants.PROPERTY_PE_TASK_VISIBILITY, HHSConstants.ONE);
					liOpenTaskAmendmentConf = new P8ProcessServiceForSolicitationFinancials().getOpenTaskCount(
							aoTaskDetailsBean.getP8UserSession(), loHMReqdProp);
					if (liOpenTaskAmendmentConf > 0)
					{
						loHMReqdProp.put(HHSConstants.CLC_ERROR_CHECK, HHSConstants.ERROR_FLAG);
						loHMReqdProp.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGE_PROP_FILE, HHSConstants.AMENDMENT_CONFIGURATION_PENDING));
						return loHMReqdProp;
					}
				}
			}
			loHMReqdProp.put(HHSConstants.CLC_ERROR_CHECK, HHSConstants.SUCCESS);
			return loHMReqdProp;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ConfigurationService: newFYFinishValidate method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: newFYFinishValidate method - failed."
					+ " Exception occured while fetching Contract Source Id for contractId::");
			throw loAppEx;
		}
	}
	/**
	 * This method is used to return amendment C of validation
	 * @param aoMybatisSession
	 * @param aoTaskDetailsBean
	 * @return loHMReqdProp
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap returnAmendmentCofValidate(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		HashMap loHMReqdProp = new HashMap();
		try
		{
			Integer loNewFYCreatedWithMergedValues = (Integer) DAOUtil.masterDAO(aoMybatisSession,
					aoTaskDetailsBean.getContractId(), HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.IS_NEW_FY_CREATED_WITH_MERGED_VALUE_WHEN_AMENDMENT_IS_REG_IN_FMS,
					HHSConstants.JAVA_LANG_STRING);

			if (loNewFYCreatedWithMergedValues > 0)
			{
				loHMReqdProp.put(HHSConstants.CLC_ERROR_CHECK, HHSConstants.ERROR_FLAG);
				loHMReqdProp.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGE_PROP_FILE, HHSConstants.AMENDMENT_COF_RETURN));
				return loHMReqdProp;
			}
			loHMReqdProp.put(HHSConstants.CLC_ERROR_CHECK, HHSConstants.SUCCESS);
			return loHMReqdProp;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ConfigurationService: newFYFinishValidate method:: ", loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: newFYFinishValidate method - failed."
					+ " Exception occured while fetching Contract Source Id for contractId::");
			throw loAppEx;
		}
	}
	// Start : Added in R6
	/**
	 * This method is added in Release 6 for Default FTE calculations
	 * @param aoMybatisSession
	 * @param aoTaskDetailsBean
	 * @return loUsesFteUpdated
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Boolean updateUsesFte(SqlSession aoMyBatisSession, TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into updateUsesFte for contractId : " + aoTaskDetailsBean.getContractId());
		Boolean loUsesFteUpdated = false;
		try
		{
			if (aoTaskDetailsBean.getLevel().equals(aoTaskDetailsBean.getTotalLevel()))
			{
				HashMap loParam = new HashMap();
				loParam.put(HHSR5Constants.CONTRACT_ID, aoTaskDetailsBean.getContractId());
				loParam.put(HHSR5Constants.DEFAULT_CITY_FTE, HHSR5Constants.DEFAULT_CITY_FTE);
				loParam.put(HHSR5Constants.FISCAL_YEAR_ID, aoTaskDetailsBean.getNewFYId());
				DAOUtil.masterDAO(aoMyBatisSession, loParam, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSR5Constants.UPDATE_USES_FTES, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			loUsesFteUpdated = true;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while updating usesFte ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while updating usesFte ", loExp);
			setMoState("ApplicationException occured while updating usesFte for contract id = "
					+ aoTaskDetailsBean.getContractId());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"ApplicationException occured while updating usesFte ", loExp);
			loAppEx.addContextData("ApplicationException occured while updating usesFte ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while updating usesFte ", loExp);
			setMoState("ApplicationException occured while updating usesFte for contract id = "
					+ aoTaskDetailsBean.getContractId());
			throw loAppEx;
		}

		return loUsesFteUpdated;
	}
	
	/**
	 * Added as part of Release 6 In bound interfaces batch
	 * This method is used by In bound interface batch to process out of year amendment budgets 
	 * @param aoMybatisSession
	 * @param asContractId
	 * @param asAmendContractId
	 * @param asFiscalYearId
	 * @param aoCBGridBean
	 * @return
	 * @throws ApplicationException
	 */ 
	@SuppressWarnings("unchecked")
	public List<ContractBudgetBean> getNextNewFYBudgetDetailsForBatch(SqlSession aoMybatisSession, String asContractId, 
			String asAmendContractId, String asFiscalYearId, CBGridBean aoCBGridBean) throws ApplicationException
	{
		List<ContractBudgetBean> loContractBudgetBean = null;
		Map<String, String> loBudgetInfo = new HashMap<String, String>();
		try
		{
			setMoState("Started service getNextNewFYBudgetDetailsForBatch");
			
			loBudgetInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			loBudgetInfo.put(HHSConstants.AMENDED_CONTRACT_ID, asAmendContractId);
			loBudgetInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);

			ContractBudgetBean aoContractBudgetBean = new ContractBudgetBean();
			loContractBudgetBean = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.CS_FETCH_NEXT_NEW_FY_BUDGET_DETAILS,
					HHSConstants.JAVA_UTIL_MAP);
			if (loContractBudgetBean == null || loContractBudgetBean.isEmpty())
			{
				HashMap<String, Object> loContractDetailMap = (HashMap<String, Object>) DAOUtil.masterDAO(
						aoMybatisSession, aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.BMC_FETCH_CONTRACT_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				// insert into base budget and sub budget for its amendment
				// entry
				aoContractBudgetBean.setPlannedAmount(ApplicationConstants.ZERO);
				aoContractBudgetBean.setActiveFlag(HHSConstants.ONE);
				
				populateContractBudgetBean(aoContractBudgetBean, loContractDetailMap, aoCBGridBean, asFiscalYearId);
				
				insertNewBudgetDetails(aoMybatisSession, aoContractBudgetBean);
				String lsAddedBudgetId= checkIfBudgetExistsForAmendmentBatch(aoMybatisSession,
						aoContractBudgetBean.getContractId(), asFiscalYearId);
				aoContractBudgetBean.setBudgetId(lsAddedBudgetId);
				copyPreviousFYSubBudgetToCurrentFY(aoMybatisSession, aoCBGridBean.getContractID(), asFiscalYearId,
						aoContractBudgetBean.getBudgetId(), aoCBGridBean.getModifyByAgency());
				copyPreviousCBCToCurrentCBC(aoMybatisSession, aoCBGridBean.getContractID(), asFiscalYearId,
						aoContractBudgetBean.getBudgetId(), aoCBGridBean.getModifyByAgency());
				loContractBudgetBean = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.CS_FETCH_NEXT_NEW_FY_BUDGET_DETAILS, HHSConstants.JAVA_UTIL_MAP);
				
				/*
				 * Updating the above budget id as parent id of existing budgets of budget_type_id=1 added via ETL
				 */
				loBudgetInfo.put(HHSConstants.AS_PARENT_BUDGET_ID, lsAddedBudgetId);
				DAOUtil.masterDAO(aoMybatisSession, loBudgetInfo,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.UPDATE_PARENT_BUDGET_ID_FOR_ETL_AMENDMENT, HHSConstants.JAVA_UTIL_MAP);
			}

			setMoState("Default entries for out of year amendment");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting contract budget details in getNextNewFYBudgetDetailsForBatch() method :");
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error while getting contract budget details in getNextNewFYBudgetDetailsForBatch() method :");
			throw new ApplicationException(
					"Error while getting contract budget details in getNextNewFYBudgetDetailsForBatch() method :", loExp);
		}
		return loContractBudgetBean;
	}
	
	/**
	 * This method is added as part of release 6
	 * 
	 * Using this to get the budget_id of the recently added budget of type 2 with base contract
	 * @param aoMybatisSession SqlSession
	 * @param asContractId String
	 * @param asFiscalYearId String
	 * @return lsBudgetId String
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	private String checkIfBudgetExistsForAmendmentBatch(SqlSession aoMybatisSession, String asContractId, String asFiscalYearId)
			throws ApplicationException
	{
		String lsBudgetId = null;
		Map<String, String> loBudgetMapInfo = new HashMap<String, String>();
		try
		{
			loBudgetMapInfo.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
			loBudgetMapInfo.put(HHSConstants.FISCAL_YEAR_ID_KEY, asFiscalYearId);
			lsBudgetId = (String) DAOUtil.masterDAO(aoMybatisSession, loBudgetMapInfo,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.QUERY_GET_BUDGET_ID_AMEND_BATCH,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Budget Id for contract fetched successfully");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting Budget Id :");
			throw loExp;
		}
		return lsBudgetId;
	}
	// End : Added in R6
	//Start: Added in R7 for Cost Center
	/**
	 * This method is added in R7 for Cost Center.It is used to fetch enabled/disabled
	 * services list for contract configurations screen.For amendment/update configuration 
	 * screen, it fetches amended/updates services list also.
	 * @param aoMybatisSession
	 * @param aoCBGridBean
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap fetchCostCenterServicesDetails(SqlSession aoMybatisSession, CBGridBean aoCBGridBean)
			throws ApplicationException
	{
		LOG_OBJECT
				.Debug("Entered into fetchCostCenterServicesDetails for contractId : " + aoCBGridBean.getContractID());
		HashMap loHMReqdProp = new HashMap(); 
		try
		{
			List<CostCenterServicesMappingList> loServicesList = (ArrayList) DAOUtil.masterDAO(aoMybatisSession,
					aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSR5Constants.FETCH_SERVICES_FOR_AGENCY, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			List<CostCenterServicesMappingList> loSelectedServicesList = (ArrayList) DAOUtil.masterDAO(
					aoMybatisSession, aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSR5Constants.FETCH_SELECTED_SERVICES, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			if (!Arrays.asList(HHSR5Constants.CONTRACT_TYPE_ID_LIST).contains(aoCBGridBean.getContractTypeId()))
			{
				List<CostCenterServicesMappingList> loSelectedUpdServicesList = (ArrayList) DAOUtil.masterDAO(
						aoMybatisSession, aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSR5Constants.FETCH_UPDATED_SELECTED_SERVICES, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				loHMReqdProp.put(HHSR5Constants.DATA_LIST, loSelectedUpdServicesList);
			}
			loHMReqdProp.put(HHSR5Constants.ENABLED_SERVICES, loServicesList);
			loHMReqdProp.put(HHSR5Constants.SELECTED_SERVICES, loSelectedServicesList);
			return loHMReqdProp;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchCostCenterServicesDetails method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchCostCenterServicesDetails method - failed."
					+ " Exception occured while fetching fetchCostCenterServicesDetails");
			throw loAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"ApplicationException occured while fetchCostCenterServicesDetails ", loExp);
			loAppEx.addContextData("ApplicationException occured while fetchCostCenterServicesDetails ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while fetchCostCenterServicesDetails ", loExp);
			setMoState("ApplicationException occured while fetchCostCenterServicesDetails for contract id = "
					+ aoCBGridBean.getContractID());
			throw loAppEx;
		}
	}
	
	/**
	 * This method is added in R7 for cost center.This method returns the following services flag.
	 *  <ul>
	 * <li>Returns 0 if cost center is disabled for particular agency</li>
	 * <li>Returns 1 if cost center is configured for particular agency</li>
	 * <li>Returns 2 for cost center enabled agency</li>
	 * <li>Returns 3 if cost center is chosen by User action from contract Conf. screen</li>
	 * </ul>
	 * @param aoMyBatisSession
	 * @param aoCBGridBean
	 * @return lsServiceDetailFlag
	 * @throws ApplicationException
	 */
	public String fetchServicesStatusFlag(SqlSession aoMyBatisSession, CBGridBean aoCBGridBean)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into fetchServicesStatusFlag for contractId : " + aoCBGridBean.getContractID());
		String lsServiceDetailFlag = null;
		try
		{
			lsServiceDetailFlag = (String) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSR5Constants.FETCH_SERVICES_CONF_FLAG,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while fetchServicesStatusFlag ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while fetchServicesStatusFlag ", loExp);
			setMoState("ApplicationException occured while fetchServicesStatusFlag for contract id = "
					+ aoCBGridBean.getContractID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"ApplicationException occured while fetchServicesStatusFlag ", loExp);
			loAppEx.addContextData("ApplicationException occured while fetchServicesStatusFlag ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while fetchServicesStatusFlag ", loExp);
			setMoState("ApplicationException occured while fetchServicesStatusFlag for contract id = "
					+ aoCBGridBean.getContractID());
			throw loAppEx;
		}

		return lsServiceDetailFlag;
	}
	
	/** 
	 * This method is added in R7 for cost center.It is used to insert all services configurations on first render
	 * of Contract Configuration Task & New FY Task.It also chooses COST CENTER and PROGRAM INCOME Templates 
	 * for cost center enabled agencies.This method updates asServicesConfFlag to 3 in case of
	 * Cost Center enabled agencies.
	 * @param aoMyBatisSession
	 * @param aoCBGridBean
	 * @param asServicesConfFlag
	 * @throws ApplicationException
	 */
	public String insertDefaultServicesConf(SqlSession aoMyBatisSession, CBGridBean aoCBGridBean,
			String asServicesConfFlag) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into insertDefaultServicesConf for contractId : " + aoCBGridBean.getContractID());
		Integer lsServiceCount;
		HashMap<String, String> loMap = new HashMap<String, String>();
		try
		{
			lsServiceCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSR5Constants.FETCH_SERVICES_COUNT,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			//Condition Update for Defect 8776
			if (HHSConstants.BMC_CONTRACT_CONFIG_TASK.equalsIgnoreCase(aoCBGridBean.getTransactionName())
					&& Arrays.asList(HHSR5Constants.CONTRACT_TYPE_ID_LIST).contains(aoCBGridBean.getContractTypeId())
					&& (asServicesConfFlag == null))
			{
				String lsAgencyServiceFlag = (String) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSR5Constants.FETCH_AGENCY_SERVICE_FLAG,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				// Enabling Cost Center and Program Income for Cost Center
				// enable agencies
				loMap.put(HHSConstants.CONTRACT_ID, aoCBGridBean.getContractID());
				loMap.put(HHSConstants.CREATED_BY_USER_ID, aoCBGridBean.getCreatedByUserId());
				if (HHSConstants.TWO.equalsIgnoreCase(lsAgencyServiceFlag) && !HHSConstants.ONE.equalsIgnoreCase(asServicesConfFlag))
				{
					loMap.put(HHSConstants.STATUS_ID, HHSConstants.TWO);
					loMap.put(HHSConstants.BUDGET_ID, aoCBGridBean.getContractBudgetID());
					loMap.put(HHSConstants.BMC_BUDGET_FISCAL_YEAR, aoCBGridBean.getFiscalYearID());
					loMap.put(HHSConstants.ENTRY_TYPE_ID, HHSConstants.STRING_ELEVEN);
					loMap.put(HHSConstants.PUBLISHED, HHSConstants.ZERO);
					DAOUtil.masterDAO(aoMyBatisSession, loMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
							HHSR5Constants.UPDATE_COST_CENTER_ENABLED, HHSConstants.JAVA_UTIL_HASH_MAP);
					Integer liProgramIncomeCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loMap,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSR5Constants.FETCH_IF_PI_ALREADY_SELECTED,
							HHSConstants.JAVA_UTIL_HASH_MAP);
					if (liProgramIncomeCount == HHSConstants.INT_ZERO)
					{
						DAOUtil.masterDAO(aoMyBatisSession, loMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.INSERT_BUDGET_CUSTOMIZATION, HHSConstants.JAVA_UTIL_HASH_MAP);
					}
					asServicesConfFlag = HHSConstants.TWO;
				}
				else if (HHSConstants.ONE.equalsIgnoreCase(lsAgencyServiceFlag))
				{
					loMap.put(HHSConstants.STATUS_ID, HHSConstants.ONE);
					DAOUtil.masterDAO(aoMyBatisSession, loMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
							HHSR5Constants.UPDATE_COST_CENTER_ENABLED, HHSConstants.JAVA_UTIL_HASH_MAP);
				}
			}
			String lsBudgetStaus = (String) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSR5Constants.FETCH_BASE_BUDGET_STATUS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// Copying previous year services configuration in out-year 
			if (HHSConstants.BUDGET_PENDING_CONFIGURATION_STATUS_ID.equalsIgnoreCase(lsBudgetStaus)
					&& lsServiceCount == HHSConstants.INT_ZERO
					&& HHSConstants.TWO.equalsIgnoreCase(aoCBGridBean.getContractTypeId()))
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSR5Constants.INSERT_DFT_SERVICES_CONF_FROM_PREV_FY,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			}

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while insertDefaultServicesConf ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while insertDefaultServicesConf ", loExp);
			setMoState("ApplicationException occured while insertDefaultServicesConf for contract id = "
					+ aoCBGridBean.getContractID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"ApplicationException occured while insertDefaultServicesConf ", loExp);
			loAppEx.addContextData("ApplicationException occured while insertDefaultServicesConf ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while insertDefaultServicesConf ", loExp);
			setMoState("ApplicationException occured while insertDefaultServicesConf for contract id = "
					+ aoCBGridBean.getContractID());
			throw loAppEx;
		}
		return asServicesConfFlag;
	}
	
	/** This method is added in R7. It is used to validate At least one
	 * Services is chosen if cost center is opted. It will display M103.
	 * @param aoMybatisSession
	 * @param aoTaskDetailsBean
	 * @return lsmessage
	 * @throws ApplicationException
	 */
	public String validateServicesOpted(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		String lsmessage = null;
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		CBGridBean loCBGridBeanObj = new CBGridBean();
		try
		{
			LOG_OBJECT
					.Debug("Entered into validateServicesOpted for contractId : " + aoTaskDetailsBean.getContractId());
			loQueryMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
			String lsCostOpted = (String) DAOUtil.masterDAO(aoMybatisSession, loQueryMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.FETCH_COST_CENTER_OPTED,
					HHSConstants.JAVA_UTIL_MAP);
			if (HHSConstants.TWO.equals(lsCostOpted))
			{
				loCBGridBeanObj.setContractID(aoTaskDetailsBean.getContractId());
				loCBGridBeanObj.setTransactionName(HHSR5Constants.VALIDATE_SERVICES_OPTED);
				Integer lsServiceCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSR5Constants.FETCH_SERVICES_COUNT,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				if(lsServiceCount == HHSConstants.INT_ZERO)
				{
					lsmessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSR5Constants.MESSAGE_M103);
				}
			}
			
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("Application Exception occured in ConfigurationService: validateServicesOpted method:: ",
							aoAppEx);
			setMoState("ApplicationException occured in ConfigurationService: validateServicesOpted method");
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured in ConfigurationService: validateServicesOpted method:: ",
					aoExp);
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured in validateServicesOpted", aoExp);
			setMoState("Exception occured in ConfigurationService: validateServicesOpted method");
			throw loAppEx;
		}
		return lsmessage;
	}
	
	/** 
	 * This method is added in R7 for cost center.It is used to insert services configurations on first render
	 * of New FY Task from previous FY year .
	 * @param aoMyBatisSession
	 * @param aoCBGridBean
	 * @param asFiscalYearScreen
	 * @throws ApplicationException
	 */
	public void insertDefaultServicesConfForNewFy(SqlSession aoMyBatisSession, CBGridBean aoCBGridBean,
			String asFiscalYearScreen) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into insertDefaultServicesConfForNewFy for contractId : " + aoCBGridBean.getContractID());
		Integer lsServiceCount;
		HashMap<String, String> loMap = new HashMap<String, String>();
		try
		{
			lsServiceCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSR5Constants.FETCH_SERVICES_COUNT,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			if (null != lsServiceCount && lsServiceCount == HHSConstants.INT_ZERO
					&& HHSConstants.TRUE.equalsIgnoreCase(asFiscalYearScreen))
			{
				aoCBGridBean.setAmendmentContractID(aoCBGridBean.getContractID());
				DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSR5Constants.INSERT_DFT_SERVICES_CONF_FROM_PREV_FY, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			}

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while insertDefaultServicesConfForNewFy ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while insertDefaultServicesConfForNewFy ", loExp);
			setMoState("ApplicationException occured while insertDefaultServicesConfForNewFy for contract id = "
					+ aoCBGridBean.getContractID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"ApplicationException occured while insertDefaultServicesConfForNewFy ", loExp);
			loAppEx.addContextData("ApplicationException occured while insertDefaultServicesConfForNewFy ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while insertDefaultServicesConfForNewFy ", loExp);
			setMoState("ApplicationException occured while insertDefaultServicesConfForNewFy for contract id = "
					+ aoCBGridBean.getContractID());
			throw loAppEx;
		}
	}
	
	/**
	 * This method is added in R7 for cost center.This method returns COST_CENTER_OPTED value
	 * from contract table.
	 *  <ul>
	 * <li>Returns 0 if cost center is  not chosen</li>
	 * <li>Returns 1 if cost center is chosen</li>
	 * </ul>
	 * @param aoMyBatisSession
	 * @param aoCBGridBean
	 * @return lsServiceDetailFlag
	 * @throws ApplicationException
	 */
	public String fetchCostCenterFlag(SqlSession aoMyBatisSession, HashMap aoMap)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into fetchCostCenterFlag for contractId : " + aoMap.get(HHSConstants.CONTRACT_ID_WORKFLOW));
		String lsCostOpted = null;
		try
		{
			lsCostOpted = (String) DAOUtil.masterDAO(aoMyBatisSession, aoMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.FETCH_COST_CENTER_OPTED,
					HHSConstants.JAVA_UTIL_MAP);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while fetchCostCenterFlag ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while fetchCostCenterFlag ", loExp);
			setMoState("ApplicationException occured while fetchCostCenterFlag for contract id = "
					+ aoMap.get(HHSConstants.CONTRACT_ID_WORKFLOW));
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"ApplicationException occured while fetchCostCenterFlag ", loExp);
			loAppEx.addContextData("ApplicationException occured while fetchCostCenterFlag ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while fetchCostCenterFlag ", loExp);
			setMoState("ApplicationException occured while fetchCostCenterFlag for contract id = "
					+ aoMap.get(HHSConstants.CONTRACT_ID_WORKFLOW));
			throw loAppEx;
		}
		return lsCostOpted;
	}
	
	/** 
	 * This method is added in R7 for cost center.It is used to delete Services
	 * details on return of Contract COF.
	 * @param aoMyBatisSession
	 * @param asContractId
	 * @throws ApplicationException
	 */
	public void deleteServicesDetails(SqlSession aoMyBatisSession, TaskDetailsBean asTaskDetailsBean)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into deleteServicesDetails for contractId : " + asTaskDetailsBean.getContractId());
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CONTRACT_ID, asTaskDetailsBean.getContractId());
		loHashMap.put(HHSConstants.CREATED_BY_USER_ID, asTaskDetailsBean.getUserId());
		loHashMap.put(HHSConstants.STATUS_ID, HHSConstants.ZERO);
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, asTaskDetailsBean.getContractId(), HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSR5Constants.DELETE_SERVICES, HHSConstants.JAVA_LANG_STRING);
			DAOUtil.masterDAO(aoMyBatisSession, asTaskDetailsBean.getContractId(), HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSR5Constants.DELETE_COSTCENTER, HHSConstants.JAVA_LANG_STRING);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSR5Constants.UPDATE_COST_CENTER_FOR_CCOF_RETURN, HHSConstants.JAVA_UTIL_HASH_MAP);

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while deleteServicesDetails ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while deleteServicesDetails ", loExp);
			setMoState("ApplicationException occured while deleteServicesDetails for contract id = " + asTaskDetailsBean.getContractId());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"ApplicationException occured while deleteServicesDetails ", loExp);
			loAppEx.addContextData("ApplicationException occured while deleteServicesDetails ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while deleteServicesDetails ", loExp);
			setMoState("ApplicationException occured while deleteServicesDetails for contract id = " + asTaskDetailsBean.getContractId());
			throw loAppEx;
		}
	}
	
	/**
	 * This method is used to fetch enabled and selected services
	 * for the contract on the update overlay page for city.
	 * @param aoMybatisSession
	 * @param aoCBGridBean
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap fetchCostCenterServicesDetailsForUpdate(SqlSession aoMybatisSession, CBGridBean aoCBGridBean)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into fetchCostCenterServicesDetailsForUpdate for contractId : "
				+ aoCBGridBean.getContractID());
		HashMap loHMReqdProp = new HashMap();
		try
		{
			List<CostCenterServicesMappingList> loServicesList = (ArrayList) DAOUtil.masterDAO(aoMybatisSession,
					aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSR5Constants.FETCH_SERVICES_FOR_AGENCY_UPDATE, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			List<CostCenterServicesMappingList> loSelectedServicesList = (ArrayList) DAOUtil.masterDAO(
					aoMybatisSession, aoCBGridBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSR5Constants.FETCH_SELECTED_SERVICES_UPDATE, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			loHMReqdProp.put(HHSR5Constants.ENABLED_SERVICES, loServicesList);
			loHMReqdProp.put(HHSR5Constants.SELECTED_SERVICES, loSelectedServicesList);
			return loHMReqdProp;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error(
					"Exception occured in ConfigurationService: fetchCostCenterServicesDetailsForUpdate method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ConfigurationService: fetchCostCenterServicesDetailsForUpdate method - failed."
					+ " Exception occured while fetching fetchCostCenterServicesDetailsForUpdate");
			throw loAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"ApplicationException occured while fetchCostCenterServicesDetailsForUpdate ", loExp);
			loAppEx.addContextData("ApplicationException occured while fetchCostCenterServicesDetailsForUpdate ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while fetchCostCenterServicesDetailsForUpdate ", loExp);
			setMoState("ApplicationException occured while fetchCostCenterServicesDetailsForUpdate for contract id = "
					+ aoCBGridBean.getContractID());
			throw loAppEx;
		}
	}

	/** This method is added in R7. It is used to fetch Budget fiscal year
	 * for which the contract Configuration task is launched.
	 * @param aoMybatisSession
	 * @param aoTaskDetailsBean
	 * @return lsmessage
	 * @throws ApplicationException
	 */
	public String fetchContractConfBudgetyear(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		String lsFyYear = null;
		HashMap loHMArgs = new HashMap();
		try
		{
			LOG_OBJECT
					.Debug("Entered into fetchContractConfBudgetyear for contractId : " + asContractId);
			loHMArgs.put(HHSConstants.CONTRACT_ID_WORKFLOW, asContractId);
			lsFyYear = (String)DAOUtil.masterDAO(
					aoMybatisSession, loHMArgs, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.FETCH_FY_AMEND_CONTRACT_END_DATE, HHSConstants.JAVA_UTIL_HASH_MAP);
			
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("Application Exception occured in ConfigurationService: fetchContractConfBudgetyear method:: ",
							aoAppEx);
			setMoState("ApplicationException occured in ConfigurationService: fetchContractConfBudgetyear method");
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured in ConfigurationService: fetchContractConfBudgetyear method:: ",
					aoExp);
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured in fetchContractConfBudgetyear", aoExp);
			setMoState("Exception occured in ConfigurationService: fetchContractConfBudgetyear method");
			throw loAppEx;
		}
		return lsFyYear;
	}
	
	/** This method is added in R7. It is used to validate At least one
	 * Services is chosen if cost center is opted. It will display M103.
	 * @param aoMybatisSession
	 * @param aoTaskDetailsBean
	 * @return lsmessage
	 * @throws ApplicationException
	 */
	public String validateServicesOptedForNewFY(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		String lsmessage = null;
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		CBGridBean loCBGridBeanObj = new CBGridBean();
		try
		{
			LOG_OBJECT
					.Debug("Entered into validateServicesOptedForNewFY for contractId : " + aoTaskDetailsBean.getContractId());
			loQueryMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
			String lsCostOpted = (String) DAOUtil.masterDAO(aoMybatisSession, loQueryMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.FETCH_COST_CENTER_OPTED,
					HHSConstants.JAVA_UTIL_MAP);
			if (HHSConstants.TWO.equals(lsCostOpted))
			{
				loCBGridBeanObj.setAmendmentContractID(aoTaskDetailsBean.getContractId());
				loCBGridBeanObj.setFiscalYearID(aoTaskDetailsBean.getNewFYId());
				loCBGridBeanObj.setContractTypeId(HHSConstants.TWO);
				loCBGridBeanObj.setTransactionName(HHSR5Constants.VALIDATE_SERVICES_OPTED);
				Integer lsServiceCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSR5Constants.FETCH_SERVICES_COUNT,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				if(lsServiceCount == HHSConstants.INT_ZERO)
				{
					lsmessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSR5Constants.MESSAGE_M103);
				}
			}
			
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("Application Exception occured in ConfigurationService: validateServicesOpted method:: ",
							aoAppEx);
			setMoState("ApplicationException occured in ConfigurationService: validateServicesOpted method");
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured in ConfigurationService: validateServicesOpted method:: ",
					aoExp);
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured in validateServicesOpted", aoExp);
			setMoState("Exception occured in ConfigurationService: validateServicesOpted method");
			throw loAppEx;
		}
		return lsmessage;
	}
	
	/**
	 * This method is added in R7 for cost center.This method returns the value of Services
	 * configuuration from nyc_agency_details table.
	 *
	 * @param aoMyBatisSession
	 * @param aoCBGridBean
	 * @return lsServiceDetailFlag
	 * @throws ApplicationException
	 */
	public String fetchServicesAgencyFlag(SqlSession aoMyBatisSession, CBGridBean aoCBGridBean)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into fetchServicesAgencyFlag for contractId : " + aoCBGridBean.getContractID());
		String lsServiceDetailFlag = null;
		try
		{
			lsServiceDetailFlag = (String) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSR5Constants.FETCH_AGENCY_SERVICE_FLAG,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while fetchServicesAgencyFlag ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while fetchServicesAgencyFlag ", loExp);
			setMoState("ApplicationException occured while fetchServicesAgencyFlag for contract id = "
					+ aoCBGridBean.getContractID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"ApplicationException occured while fetchServicesAgencyFlag ", loExp);
			loAppEx.addContextData("ApplicationException occured while fetchServicesAgencyFlag ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while fetchServicesAgencyFlag ", loExp);
			setMoState("ApplicationException occured while fetchServicesAgencyFlag for contract id = "
					+ aoCBGridBean.getContractID());
			throw loAppEx;
		}

		return lsServiceDetailFlag;
	}	
// End: Added in R7 for Cost Center
	
	
	/**
	 * R 7.2.0 QC 8914
	 * This method will fetch all Actions that need to be excluded for ReadOnly Role
	 *
	 * @param aoMyBatisSession
	 * @return lsServiceDetailFlag
	 * @throws ApplicationException
	 */
	public List<String> getReadOnlyActionsToExclude(SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<String> actionsExcludeList = new ArrayList<String>();
		try
		{
			actionsExcludeList = (ArrayList<String>) DAOUtil.masterDAO(aoMyBatisSession, null,
					HHSConstants.MAPPER_CLASS_COMMON_MAPPER, HHSConstants.GET_READ_ONLY_ACTIONS_TO_EXCLUDE, null);
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		return actionsExcludeList;
	}	
	// End: R7.2.0 QC 8914 
	
	// Start R8.6.0 QC_9499 Multi-Tab Browsing letting Invoice and Advance tasks to be Approved Multiple times causing Duplicate Payments
	public String getTokenFlagConfig(SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		String lsTokenFlagConfig = null;
		try
		{
			lsTokenFlagConfig = (String) DAOUtil.masterDAO(aoMyBatisSession, null,
					HHSConstants.MAPPER_CLASS_COMMON_MAPPER, HHSR5Constants.GET_TOKEN_FLAG_CONFIG,null);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while getTokenFlagConfig ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while getTokenFlagConfig ", loExp);
			setMoState("ApplicationException occured while getTokenFlagConfig");
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"ApplicationException occured while getTokenFlagConfig ", loExp);
			loAppEx.addContextData("ApplicationException occured while getTokenFlagConfig ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while getTokenFlagConfig ", loExp);
			setMoState("ApplicationException occured while fetchServicesAgencyFlag");
			throw loAppEx;
		}

		return lsTokenFlagConfig;
	}
	// End R8.6.0 QC_9499 Multi-Tab Browsing letting Invoice and Advance tasks to be Approved Multiple times causing Duplicate Payments
	
	// Start QC 9682 R 9.5 
	
	
	/**
	 * QC 9682 R 9.5 
	 * This method will fetch Base Budget status for FY
	 *
	 * @param aoMyBatisSession
	 * @return status_id
	 * @throws ApplicationException
	 */
	public String fetchBaseBudgetStatus(SqlSession aoMyBatisSession, CBGridBean aoCBGridBean)
			throws ApplicationException
	{
		String status_id = null;
		try
		{
			status_id = (String) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSR5Constants.FETCH_BASE_BUDGET_STATUS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured while fetchBaseBudgetStatus ", aoAppEx);
			setMoState("ApplicationException occured while fetchBaseBudgetStatus");
			throw aoAppEx;
		}
		return status_id;
	}	
	
	
	// Start QC9681 R 9.5 
	/**
	 * QC9681 R 9.5 
	 * This method will fetch ContractUpdate status and Budget status Id for FY
	 *
	 * @param aoMyBatisSession
	 * @return HashMap<String,String> loStatusMap
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String,String> fetchContractUpdateBudgetStatus(SqlSession aoMyBatisSession, CBGridBean aoCBGridBean)
			throws ApplicationException
	{
		HashMap<String,String> loStatusMap = null;
		try
		{
			List<HashMap<String, String>> loStatusMapLst = (List<HashMap<String, String>>) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.FETCH_CONTRACT_UPDATE_BUDGET_STATUS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			if( loStatusMapLst == null || loStatusMapLst.size() < 1){
				loStatusMap = new HashMap<String,String>();
			} else {
				loStatusMap = loStatusMapLst.get(0);
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured while fetchBaseBudgetStatus ", aoAppEx);
			setMoState("ApplicationException occured while fetchBaseBudgetStatus");
			throw aoAppEx;
		}
		return loStatusMap;
	}	
}
