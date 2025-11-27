package com.nyc.hhs.daomanager.service;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.CBEquipmentBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CBIndirectRateBean;
import com.nyc.hhs.model.CBMileStoneBean;
import com.nyc.hhs.model.CBOperationSupportBean;
import com.nyc.hhs.model.CBProfessionalServicesBean;
import com.nyc.hhs.model.CBProgramIncomeBean;
import com.nyc.hhs.model.CBServicesBean;
import com.nyc.hhs.model.CBUtilities;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.ContractBudgetSummary;
import com.nyc.hhs.model.ContractedServicesBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.LineItemMasterBean;
import com.nyc.hhs.model.MasterBean;
import com.nyc.hhs.model.PersonnelServiceBudget;
import com.nyc.hhs.model.PersonnelServicesData;
import com.nyc.hhs.model.RateBean;
import com.nyc.hhs.model.Rent;
import com.nyc.hhs.model.SiteDetailsBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.model.UnallocatedFunds;
import com.nyc.hhs.preprocessor.PreprocessorUpdateApproval;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8ProcessOperations;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8services.P8ContentService;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

import filenet.vw.api.VWFetchType;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueElement;
import filenet.vw.api.VWQueueQuery;
import filenet.vw.api.VWSession;
import filenet.vw.api.VWStepElement;

/**
 * <p>
 * This service class will be used to fetch all the data for contract budget
 * Modification screens. All render and action methods for Budget Summary,
 * Utilities, OTPS, Rent etc screens will use this service to
 * fetch/insert/update data from/to database.
 * </p>
 * 
 */
public class ContractBudgetModificationService extends ServiceState
{

	private static final LogInfo LOG_OBJECT = new LogInfo(ContractBudgetModificationService.class);
	private static final CBMileStoneBean aoUnallocatedFundsBean = null;

	/**
	 * <li>This service class is invoked through fetchCMSubBudgetSummary
	 * transaction id for Contract budget Modification screen</li> <li>This
	 * method fetchCMSubBudgetSummary will get the SubBudget Information on the
	 * basis of budgetId</li>
	 * 
	 * <li>query used : fetchCMSubBudgetSummary</li>
	 * @param aoMybatisSession : SqlSession object
	 * @param aoHashmap : HashMap object
	 * @return loSubBudgetList : CBGridBean List returned
	 * @throws ApplicationException : Application Exception if it occurs
	 */

	@SuppressWarnings("unchecked")
	public List<CBGridBean> fetchCMSubBudgetSummary(SqlSession aoMybatisSession, HashMap<String, String> aoHashmap)
			throws ApplicationException
	{
		List<CBGridBean> loSubBudgetList = null;
		try
		{
			loSubBudgetList = (List<CBGridBean>) DAOUtil.masterDAO(aoMybatisSession, aoHashmap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.FETCH_CM_SUB_BUDGET_SUMMARY, HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			loAppEx.addContextData(HHSConstants.IN_SE_BUDGET_ID, aoHashmap);
			LOG_OBJECT.Error(
					"Exception occured while retrieveing SubBudget Information in ContractBudgetModificationService ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: fetchCMSubBudgetSummary method - failed to fetch"
					+ aoHashmap + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			LOG_OBJECT.Error(
					"Exception occured while retrieveing Contract Information in ContractBudgetModificationService ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: fetchCMSubBudgetSummary method - failed to fetch"
					+ aoHashmap + " \n");
			throw new ApplicationException("Error occured while retrieving Contract Modification SubBudgetSummary",
					loAppEx);
		}
		return loSubBudgetList;
	}

	/**
	 * The method is updated in Release 7 to fetch sub-budget list in case of
	 * approved modification. <li>This service class is invoked through
	 * fetchModificationSubBudgetSummary transaction id for Contract budget
	 * Modification screen</li> <li>This method fetchCMSubBudgetSummary will get
	 * the SubBudget Information on the basis of budgetId</li>
	 * 
	 * @param aoMybatisSession : SqlSession object
	 * @param aoHashmap : HashMap object
	 * @return loSubBudgetList : CBGridBean List returned
	 * @throws ApplicationException : Application Exception if it occurs
	 */

	@SuppressWarnings("unchecked")
	public List<CBGridBean> fetchModificationSubBudgetSummary(SqlSession aoMybatisSession,
			HashMap<String, String> aoHashmap) throws ApplicationException
	{
		List<CBGridBean> loSubBudgetList = null;
		String lsStatusId = null;
		try
		{
			/*[Start] QC_9153 add null exception handling*/  
			String loBudgetType="";
			if(aoHashmap.get(HHSConstants.BUDGET_TYPE)!=null)
				loBudgetType = (String) aoHashmap.get(HHSConstants.BUDGET_TYPE);

			if (HHSConstants.FOUR.equals(loBudgetType))
			{
				Object loSubBudgetListObj = DAOUtil.masterDAO(aoMybatisSession, aoHashmap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_UPDATE_SUB_BUDGET_SUMMARY, HHSConstants.JAVA_UTIL_HASH_MAP);
				if(loSubBudgetListObj!=null)
					loSubBudgetList = (List<CBGridBean>) loSubBudgetListObj;
			}
			 /*[End] QC_9153 add null exception handling*/  
			else
			{
				// Release 7 start: Query to get budget status, type, active
				// flag getStatusIdForModification
				/*[Start] QC_9153 add null exception handling*/  
				String budgetIdFlowString = (aoHashmap.get(HHSConstants.BUDGET_ID_WORKFLOW)!=null) ? (String) aoHashmap.get(HHSConstants.BUDGET_ID_WORKFLOW): "";
				Object objStatus = DAOUtil.masterDAO(aoMybatisSession,
						budgetIdFlowString,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSR5Constants.GET_STATUS_ID_QUERY, HHSConstants.JAVA_LANG_STRING);
				if(objStatus!=null)
					lsStatusId = (String)objStatus;
				
				if (null != lsStatusId && lsStatusId.equalsIgnoreCase(HHSConstants.CBL_86))
				{
					Object loSubBudgetListObj= DAOUtil.masterDAO(aoMybatisSession, aoHashmap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSR5Constants.FETCH_APPROVED_MOD_SUB_BUDGET, HHSConstants.JAVA_UTIL_HASH_MAP);
					if(loSubBudgetListObj!=null)
						loSubBudgetList = (List<CBGridBean>) loSubBudgetListObj;
				}
				else
				{
					Object loSubBudgetListObj = DAOUtil.masterDAO(aoMybatisSession, aoHashmap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.FETCH_MODIFICATION_SUB_BUDGET_SUMMARY, HHSConstants.JAVA_UTIL_HASH_MAP);
					if(loSubBudgetListObj!=null)
						loSubBudgetList =(List<CBGridBean>)loSubBudgetListObj;
					
				}
				 /*[End] QC_9153 add null exception handling*/  
				// R7 end
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			loAppEx.addContextData(HHSConstants.IN_SE_BUDGET_ID, aoHashmap);
			LOG_OBJECT.Error(
					"Exception occured while retrieveing SubBudget Information in ContractBudgetModificationService ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: fetchModificationSubBudgetSummary method - failed to fetch"
					+ aoHashmap + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			LOG_OBJECT.Error(
					"Exception occured while retrieveing Contract Information in ContractBudgetModificationService ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: fetchModificationSubBudgetSummary method - failed to fetch"
					+ aoHashmap + " \n");
			throw new ApplicationException("Error occured while retrieving Contract Modification SubBudgetSummary",
					loAppEx);
		}
		return loSubBudgetList;
	}

	/**
	 * <li>This method inserts a new row for modification in budget table with
	 * fiscal_year_amount as 0.</li> <li>query used :
	 * insertNewBudgetModificationDetails</li>
	 * @param aoMybatisSession - SqlSession object
	 * @param aoContractBudgetBean - an object of ContractBudgetBean
	 * @return loRowsAdded - no of rows added
	 * @throws ApplicationException - Application Exception if it occurs
	 */

	public Integer insertModificationBudgetDetails(SqlSession aoMybatisSession, ContractBudgetBean aoContractBudgetBean)
			throws ApplicationException
	{
		Integer loRowsAdded = HHSConstants.INT_ZERO;
		try
		{
			/*[Start] QC_9153 add null exception handling*/  
			Object loRowsAddedObj = DAOUtil.masterDAO(aoMybatisSession, aoContractBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.INSERT_NEW_BUDGET_MODIFICATION_BUDGET_DETAILS,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			if(loRowsAddedObj!=null)
				loRowsAdded = (Integer) loRowsAddedObj;
			/*[End] QC_9153 add null exception handling*/  
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.IN_SE_BUDGET_ID, aoContractBudgetBean.getParentId());
			LOG_OBJECT
					.Error("Exception occured while inserting a row for modification in budget in ContractBudgetModificationService",
							loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService:" + " insertModificationBudgetDetails "
					+ "method " + "- " + "" + "failed to insert" + aoContractBudgetBean.getParentId() + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while inserting a row for modification in budget in ContractBudgetModificationService",
							loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: insertModificationBudgetDetails method - failed to insert"
					+ aoContractBudgetBean.getParentId() + " \n");
			throw new ApplicationException(
					"Exception occured while inserting a row for modification in budget in ContractBudgetModificationService",
					loAppEx);
		}
		return loRowsAdded;
	}

	/**
	 * <li>This method fetches the modified budget id for base budget id.</li>
	 * <li>Query used : fetchModifiedBudgetId</li>
	 * @param aoMybatisSession - SqlSession object
	 * @param aoInputMap - a hashmap
	 * @return lsModifiedBudgetId - modified budget id
	 * @throws ApplicationException - Application Exception if it occurs
	 */
	public String fetchModifiedBudgetId(SqlSession aoMybatisSession, HashMap<String, String> aoInputMap)
			throws ApplicationException
	{
		String lsModifiedBudgetId = HHSConstants.EMPTY_STRING;
		try
		{
			/*[Start] QC_9153 add null exception handling*/  
			Object lsModifiedBudgetIdObj = DAOUtil.masterDAO(aoMybatisSession, aoInputMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.FETCH_MODIFIED_BUDGET_ID, HHSConstants.JAVA_UTIL_HASH_MAP);
			if(lsModifiedBudgetIdObj!=null)
				lsModifiedBudgetId = (String) lsModifiedBudgetIdObj;
			/*[End] QC_9153 add null exception handling*/  
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.IN_SE_BUDGET_ID, aoInputMap);
			LOG_OBJECT
					.Error("Exception occured while retrieveing modified budget id in fetchModifiedBudgetId in ContractBudgetModificationService",
							loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: fetchCMSubBudgetSummary method - failed to fetch"
					+ aoInputMap + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while retrieveing modified budget id in fetchModifiedBudgetId in ContractBudgetModificationService",
							loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: fetchModifiedBudgetId method - failed to fetch"
					+ aoInputMap + " \n");
			throw new ApplicationException(
					"Exception occured while retrieveing modified budget id in fetchModifiedBudgetId in ContractBudgetModificationService",
					loAppEx);
		}
		return lsModifiedBudgetId;
	}

	/**
	 * <li>This method inserts modification sub budget for a sub budget.</li>
	 * <li>Query used : insertNewSubBudgetModificationDetails</li>
	 * @param aoMybatisSession - SqlSession object
	 * @param aoInputMap - a hashmap
	 * @return loRowsAdded - no of rows added
	 * @throws ApplicationException - Application Exception if it occurs
	 */
	public Integer insertModificationSubBudgetDetails(SqlSession aoMybatisSession, HashMap aoInputMap)
			throws ApplicationException
	{
		Integer loRowsAdded = HHSConstants.INT_ZERO;
		/*[Start] QC_9153 add null exception handling*/  
		Integer lsSubBudgetId= HHSConstants.INT_ZERO;
		try
		{
			Object lsSubBudgetIdObj = DAOUtil.masterDAO(aoMybatisSession, null,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.GET_SEQ_ID_FOR_AUB_BUDGET_ID, null);
			if(lsSubBudgetIdObj!=null)				
				lsSubBudgetId = (Integer) lsSubBudgetIdObj;
			
			aoInputMap.put(HHSConstants.SUB_BUDGET_ID, lsSubBudgetId);
			
			Object loRowsAddedObj = DAOUtil.masterDAO(aoMybatisSession, aoInputMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.INSERT_NEW_SUB_BUDGET_MODIFICATION_BUDGET_DETAILS, HHSConstants.JAVA_UTIL_HASH_MAP);
			if(loRowsAddedObj!=null)
				loRowsAdded = (Integer) loRowsAddedObj;
			/*[End] QC_9153 add null exception handling*/  
			// Release 3.6.0 Enhancement id 6484
			DAOUtil.masterDAO(aoMybatisSession, lsSubBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.INSERT_SUB_BUDGET_DETAILS_SITE_DETAILS_FOR_UPDATE, HHSConstants.INTEGER_CLASS_PATH);

		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.IN_SE_BUDGET_ID, aoInputMap.get(HHSConstants.BUDGET_ID_WORKFLOW));
			LOG_OBJECT
					.Error("Exception occured while inserting SubBudget in insertModificationSubBudgetDetails in ContractBudgetModificationService ",
							loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: insertModificationSubBudgetDetails method - failed to insert"
					+ aoInputMap.get(HHSR5Constants.BUDGET_ID_WORKFLOW) + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while inserting SubBudget in insertModificationSubBudgetDetails in ContractBudgetModificationService ",
							loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: insertModificationSubBudgetDetails method - failed to insert"
					+ aoInputMap.get("budgetId") + " \n");
			throw new ApplicationException(
					"Exception occured while inserting SubBudget in insertModificationSubBudgetDetails in ContractBudgetModificationService ",
					loAppEx);
		}
		return loRowsAdded;
	}

	/**
	 * The method is updated in Release 7. Added a parameter for showing
	 * approved modification. This Method fetches the values of the
	 * <b>Professional Services</b> tab (Used by Provider Users) in the Contract
	 * Budget Modification screen for every individual sub budget of the current
	 * Fiscal Year with the help of unique Sub budget ID
	 * 
	 * <ul>
	 * <li>This service will behave differently for four scenarios :</li>
	 * <ul>
	 * <li>Modification - Only Modified Amount column is editable</li>
	 * <li>Update - Only Update Amount column is editable</li>
	 * </ul>
	 * <li>"Other" field will be editable by user in the base scenario ONLY. In
	 * all other cases, it will be populated by the approved name from the base
	 * tab itself</li>
	 * 
	 * <li>Query used : cbmFetchProfServicesDetails</li></ul>
	 * 
	 * @param aoProfService - CBGridBean object
	 * @param aoMybatisSession - SqlSession object
	 * @param aoMasterBean - MasterBean object
	 * @return loProfServicesDetails - list of CBProfessionalServicesBean
	 * @throws ApplicationException - Application Exception if it occurs
	 */
	@SuppressWarnings("unchecked")
	public List<CBProfessionalServicesBean> cbmFetchProfServicesDetails(CBGridBean aoProfService,
			SqlSession aoMybatisSession, MasterBean aoMasterBean) throws ApplicationException
	{
		List<CBProfessionalServicesBean> loProfServicesDetails = null;

		try
		{
			// Start R7 changes ::Fetch the data from FileNet XML if Budget
			// Status is approved
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			if (aoMasterBean != null && aoProfService !=null && aoProfService.getBudgetStatusId()!=null && aoProfService.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoProfService.getSubBudgetID();
				loProfServicesDetails = new ContractBudgetAmendmentService().fetchProfessionalServiceListFromXML(
						lsSubBudgetId, aoMasterBean);
			}
			else
			{
				// R7 changes end
				// Fetching Professional services details data to display on
				// Contract Budget Modification - Professional Service tab
				/*[Start] QC_9153 add null exception handling*/  
				Object loProfServicesDetailsObj = DAOUtil.masterDAO(aoMybatisSession,
						aoProfService, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBM_FETCH_PROF_SERVICES_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				if(loProfServicesDetailsObj!=null)
					loProfServicesDetails = (List<CBProfessionalServicesBean>) loProfServicesDetailsObj;
			    /*[End] QC_9153 add null exception handling*/  
/* [Start] R7.5.0 QC9146 Professional Service Grid issue for MOD
				if (loProfServicesDetails != null && loProfServicesDetails.size() > HHSConstants.INT_ZERO)
				{
					updateProfServiceBeanIds(loProfServicesDetails);
				}
   [End] R7.5.0 QC9146 Professional Service Grid issue for MOD */

			}
		}// Exception occur if database is down or getting null in required
			// parameter
		catch (ApplicationException aoAppExp)
		{
			setMoState("Error while fetching Professional Service Details for budget id:"
					+ aoProfService.getContractBudgetID() + " and Sub-Budget id : " + aoProfService.getSubBudgetID());
			aoAppExp.addContextData(
					"Error while fetching Professional Service Details budget id :  fetchProfessionalServicesDetails",
					aoAppExp);
			LOG_OBJECT
					.Error("Error while fetching Professional Service Details budget id : fetchProfessionalServicesDetails "
							+ aoAppExp);
			throw aoAppExp;
		}

		return loProfServicesDetails;
	}

	/**
	 * <p>
	 * This method concatenates profServiceTypeId, remainingAmount and cbmId
	 * with ProfessionalServiceId and UNDERSCORE Character and set in ID
	 * @param aoProfServicesDetailsListForUpdate : list of
	 *            CBProfessionalServicesBean
	 * 
	 */
	private void updateProfServiceBeanIds(List<CBProfessionalServicesBean> aoProfServicesDetailsListForUpdate)
	{
		StringBuffer loConcatForUpdate = null;
		 /*[Start] QC_9153 add null exception handling*/  
		if(aoProfServicesDetailsListForUpdate!=null && !aoProfServicesDetailsListForUpdate.isEmpty()){
			for (CBProfessionalServicesBean loIterateBean : aoProfServicesDetailsListForUpdate)
			{
				loConcatForUpdate = new StringBuffer(loIterateBean.getId());
				loConcatForUpdate.append(HHSConstants.UNDERSCORE).append(loIterateBean.getProfServiceTypeId());
				loConcatForUpdate.append(HHSConstants.UNDERSCORE).append(loIterateBean.getFyBudget());

				if (loIterateBean.getCbmId() != null && !loIterateBean.getCbmId().equals(HHSConstants.EMPTY_STRING))
				{
					loConcatForUpdate.append(HHSConstants.UNDERSCORE).append(loIterateBean.getCbmId());
				}
				loIterateBean.setId(loConcatForUpdate.toString());
			}
		}
		 /*[End] QC_9153 add null exception handling*/  
	}

	/**
	 * This Method updates the values of the <b>Professional Services</b> tab
	 * (Used by Provider Users) in the Contract Budget Modification screen for
	 * every individual sub budget of the current Fiscal Year with the help of
	 * unique Sub budget ID
	 * 
	 * <ul>
	 * <li>This service will behave differently for four scenarios :</li>
	 * <li>Modification - Only Modified Amount column is editable
	 * <ul>
	 * <li>Verify upon save that the Modification Amount entered would not cause
	 * the Total Proposed Budget for the line item to fall below the YTD
	 * Invoiced Amount</li>
	 * </ul>
	 * </li>
	 * <li>Update - Only Update Amount column is editable</li>
	 * </ul>
	 * <li>"Other" field will be editable by user in the base scenario ONLY. In
	 * all other cases, it will be populated by the approved name from the base
	 * tab itself</li> <li>Query used : fetchRemainingAmnt</li> <li>Query used :
	 * addProfServicesModificationAmount</li> </ul>
	 * 
	 * @param aoProfService - CBProfessionalServicesBean object
	 * @param aoMybatisSession - SqlSession object
	 * @return lbUpdateStatus - boolean
	 * @throws ApplicationException - Application Exception if it occurs
	 */
	public boolean cbmEditProfServicesDetails(CBProfessionalServicesBean aoProfService, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbError = false;
		boolean lbUpdateStatus = false;
		/*[Start] QC_9153 add null exception handling*/  
		BigDecimal loRemainingAmount= new BigDecimal(0);
		try
		{
			String lsProfId="";
			lsProfId = (aoProfService.getId()!=null)? aoProfService.getId() : "";
			StringTokenizer loStringToken = new StringTokenizer(lsProfId, HHSConstants.UNDERSCORE);
/*
 *   [Start] R7.5.0 QC9146 Professional Service Grid issue for MOD  

			int liCountTokens = loStringToken.countTokens();
			if (liCountTokens >= HHSConstants.INT_THREE)
			{
*/
			    aoProfService.setId(loStringToken.nextToken());
/*				aoProfService.setProfServiceTypeId(loStringToken.nextToken());
				aoProfService.setFyBudget(loStringToken.nextToken());
*/				Object loRemainingAmountObj =  DAOUtil.masterDAO(aoMybatisSession, aoProfService,
												HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
												HHSConstants.INV_FETCH_REMAINING_AMNT,
												HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN);
				if(loRemainingAmountObj!=null)
					loRemainingAmount = (BigDecimal) loRemainingAmountObj;
				/*[End] QC_9153 add null exception handling*/  
				LOG_OBJECT.Debug("RemainingAmount :: "+loRemainingAmount);
                // Start QC 9206 R 7.7.1- uncommented validations against remaining amount for Professional Services item line
				// Check if modification amount + Remaining Amount is not less
				// than zero
				if ((loRemainingAmount.add(new BigDecimal(aoProfService.getModifyAmount()))).compareTo(BigDecimal.ZERO) >= HHSConstants.INT_ZERO)
				{
				// End QC 9206 R 7.7.1- uncommented validations against remaining amount for Professional Services item line
                    DAOUtil.masterDAO(aoMybatisSession, aoProfService,
                            HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
                            HHSConstants.CBM_MERGE_PROF_SERVICES_MODIFICATION_AMOUNT,
                            HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN);
/*
					if (liCountTokens > HHSConstants.INT_THREE)
					{
						// Update budget amount in Professional Service table
						// Update modification amount
						aoProfService.setCbmId(loStringToken.nextToken());
						DAOUtil.masterDAO(aoMybatisSession, aoProfService,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
								HHSConstants.CBM_UPDATE_PROF_SERVICES_MODIFICATION_AMOUNT,
								HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN);
					}
					else
					{
						// Add modification amount in PROFESSIONAL_SERVICES
						// table
						DAOUtil.masterDAO(aoMybatisSession, aoProfService,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
								HHSConstants.CBM_ADD_PROF_SERVICES_MODIFICATION_AMOUNT,
								HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN);
					}
*/
                  
					lbUpdateStatus = true;
			   // Start QC 9206 R 7.7.1- uncommented validations against remaining amount for Professional Services item line			
				}
				else
				{
					lbError = true;
					throw new ApplicationException(
							PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									HHSConstants.CBM_LESS_THAN_INVOICE_FOR_NEG_AMEND));
				}
				// End QC 9206 R 7.7.1- uncommented validations against remaining amount for Professional Services item line
/*			}
			else
			{
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.ERROR_MESSAGE_UPDATE_PROFSERVICE_ID) + loStringToken.toString());
			}
*
*   [End] R7.5.0 QC9146 Professional Service Grid issue for MOD  
*/
				
		}
		// if modification amount is failed to add or update due to database
		// issue or getting null in required parameter
		catch (ApplicationException aoAppExp)
		{
			if (lbError)
			{
				aoAppExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppExp.toString());
			}
			setMoState("Transaction Failed:: ContractBudgetModificationService: cbmEditProfServicesDetails method - failed to add or update PROFESSIONAL_SERVICES : "
					+ aoAppExp.getMessage() + " \n");
			LOG_OBJECT
					.Error("Transaction Failed:: ContractBudgetModificationService: cbmEditProfServicesDetails method - failed to add or update PROFESSIONAL_SERVICES : "
							+ aoAppExp.getMessage());
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{
			setMoState("Transaction Failed:: ContractBudgetModificationService: cbmEditProfServicesDetails method - failed to update PROFESSIONAL_SERVICES : "
					+ aoExp.getMessage() + " \n");
			LOG_OBJECT
					.Error("Transaction Failed:: ContractBudgetModificationService: cbmEditProfServicesDetails method - failed to add or update PROFESSIONAL_SERVICES : "
							+ aoExp.getMessage());
			throw new ApplicationException("Error occured while updating Modification Amount for Professional Service",
					aoExp);
		}
		return lbUpdateStatus;
	}

	/**
	 * This Method get the values on <b>Contract Modification/update</b>
	 * 
	 * <ul>
	 * <li>This service will get the respective values from DB and put into
	 * session. To use for Grid retrieve/Edit/Delete</li>
	 * <li>Query used : getCbGridDataForSession</li>
	 * <ul>
	 * 
	 * @param aoMybatisSession - sql session
	 * @param aoHashMap hashmap - object
	 * @throws ApplicationException - Application Exception if it occurs
	 * @return loCBGridBean - CBGridBean object
	 */
	public CBGridBean getCbGridDataForSession(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		CBGridBean loCBGridBean = null;
		try
		{
			/*[Start] QC_9153 add null exception handling*/  
			Object loCBGridBeanObj= DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.GET_CB_GRID_DATA_FOR_SESSION, HHSConstants.JAVA_UTIL_HASH_MAP);
			if(loCBGridBeanObj!=null) {
				loCBGridBean = (CBGridBean)loCBGridBeanObj;
				if(aoHashMap.get(HHSConstants.BUDGET_ID_WORKFLOW)!=null)
					loCBGridBean.setContractBudgetID(aoHashMap.get(HHSConstants.BUDGET_ID_WORKFLOW));
			}
			/*[End] QC_9153 add null exception handling*/  
		}
		// Application Exception handled here
		catch (ApplicationException loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			loAppEx.addContextData(HHSConstants.CONTRACTID, aoHashMap);
			LOG_OBJECT.Error("Exception occured while retrieveing Contract Information in getCbGridDataForSession ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: getCbGridDataForSession method - failed to fetch"
					+ aoHashMap + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			LOG_OBJECT.Error("Exception occured while retrieveing Contract Information in getCbGridDataForSession ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: getCbGridDataForSession method - failed to fetch"
					+ aoHashMap + " \n");
			throw new ApplicationException("Error occured while retrieving Contract Information for session", loAppEx);
		}

		return loCBGridBean;
	}

	/**
	 * <p>
	 * This method update Unallocated fund details in DB
	 * <ul>
	 * <li>1.Get all unallocated funds details in UnallocatedFunds Bean</li>
	 * <li>2.Call updateUnallocatedFunds query</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - Sql session object
	 * @param aoUnallocatedFundsBean - UnallocatedFunds Bean Object
	 * @return boolean
	 * @throws ApplicationException - Application Exception if it occurs
	 */
	@SuppressWarnings("unchecked")
	public boolean updateModificationUnallocatedFunds(SqlSession aoMybatisSession,
			UnallocatedFunds aoUnallocatedFundsBean) throws ApplicationException
	{
		boolean lbUpdateStatus = false; 
		boolean lbError = false;
		
		try
		{  // Start: QC 8394 R 7.9.0 add Multiple lines for Unallocated Funds 	
			//[Start]R7.12.0 QC9311 Minimize Debug
			//LOG_OBJECT.Debug(" :: param :: aoUnallocatedFundsBean :: "+ aoUnallocatedFundsBean);
			//[End]R7.12.0 QC9311 Minimize Debug
			/*[Start] QC_9153 add null exception handling*/  
			if(aoUnallocatedFundsBean!=null){
				aoUnallocatedFundsBean.setParentId(aoUnallocatedFundsBean.getId());
				LOG_OBJECT.Debug(" :: after setup parentId ::  aoUnallocatedFundsBean :: "+ aoUnallocatedFundsBean);
			
				UnallocatedFunds parentUnallocatedFundsBean = new UnallocatedFunds();
				
				Object parentUnallocatedFundsBeanObj = DAOUtil.masterDAO(aoMybatisSession, aoUnallocatedFundsBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_UNALLOCATED_FUNDS_PARENT_RECORD, HHSConstants.UNALLOCATED_FUNDS_BEAN);
			
				if(parentUnallocatedFundsBeanObj!=null)
					parentUnallocatedFundsBean = (UnallocatedFunds)parentUnallocatedFundsBeanObj;
				
				LOG_OBJECT.Debug(" :: parentUnallocatedFundsBean :: "+ parentUnallocatedFundsBean);
				//[Start]R7.12.0 QC9311 Minimize Debug
				//LOG_OBJECT.Debug(" :: FY Budget Amount :: "+ parentUnallocatedFundsBean.getAmmount());
				//[End]R7.12.0 QC9311 Minimize Debug
								
				if (((new BigDecimal(aoUnallocatedFundsBean.getModificationAmount())).add(new BigDecimal(parentUnallocatedFundsBean.getAmmount()))).compareTo(BigDecimal.ZERO) 
					< HHSConstants.INT_ZERO)
				{	
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.LESS_THEN_APPROVED_BUDGET_ERROR_MESSAGE));
				
				}
				else
				{	
					// QC 8394 R 7.9.0 add Multiple lines for Unallocated Funds 	
					//aoUnallocatedFundsBean.setAmmount(aoUnallocatedFundsBean.getModificationAmount());
	
					// update child record with modification amount
					UnallocatedFunds childUnallocatedFundsBean = null;
				
					Object childUnallocatedFundsBeanObj = DAOUtil.masterDAO(aoMybatisSession, aoUnallocatedFundsBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.FETCH_UNALLOCATED_FUNDS_CHILD_RECORD, HHSConstants.UNALLOCATED_FUNDS_BEAN);
					if(childUnallocatedFundsBeanObj!=null)
						childUnallocatedFundsBean = (UnallocatedFunds)childUnallocatedFundsBeanObj;
				
					LOG_OBJECT.Debug(" childUnallocatedFundsBean :: "+ childUnallocatedFundsBean);	
				
					if (childUnallocatedFundsBean != null)
					{      
						aoUnallocatedFundsBean.setChildId(childUnallocatedFundsBean.getId());
						// for a new fund record that has been added during modification or updates set up approved budget to 0
						if(aoUnallocatedFundsBean.getChildId().equals(aoUnallocatedFundsBean.getId()) ) 
						{
							aoUnallocatedFundsBean.setAmmount("0");
							if (((new BigDecimal(aoUnallocatedFundsBean.getModificationAmount()))).compareTo(BigDecimal.ZERO) 
								< HHSConstants.INT_ZERO)
							{	
								lbError = true;
								throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									HHSConstants.LESS_THEN_APPROVED_BUDGET_ERROR_MESSAGE));
							
							}
						}
						if(aoUnallocatedFundsBean.getUnallocatedFund() == null)
						{
							aoUnallocatedFundsBean.setUnallocatedFund(childUnallocatedFundsBean.getUnallocatedFund());
						}	
					}
					else
					{  
						if(aoUnallocatedFundsBean.getUnallocatedFund() == null)
						{
							aoUnallocatedFundsBean.setUnallocatedFund(parentUnallocatedFundsBean.getUnallocatedFund());
						}	
					}
				
					if(null!=aoUnallocatedFundsBean.getChildId())
						{
							DAOUtil.masterDAO(aoMybatisSession, aoUnallocatedFundsBean,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
								HHSConstants.MODIFICATION_UPDATE_UNALLOCATED_FUNDS, HHSConstants.UNALLOCATED_FUNDS_BEAN);
							lbUpdateStatus = true;
						}
					else
						{   // create child record with modification amount
					    	LOG_OBJECT.Debug(" insert new child record ");	
					    	DAOUtil.masterDAO(aoMybatisSession, aoUnallocatedFundsBean,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
								HHSConstants.INSERT_NEW_UNALLOCATED_FUNDS_FOR_MOD, HHSConstants.UNALLOCATED_FUNDS_BEAN);
						
					    	lbUpdateStatus = true;
						}
							
					}
					//* End: QC 8394 R 7.9.0 add Multiple lines for Unallocated Funds 	
				}//if(aoUnallocatedFundsBean!=null)
			 /*[End] QC_9153 add null exception handling*/  
		}
		catch (ApplicationException aoAppExp)
		{
			if (lbError)
			{
				aoAppExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppExp.toString());
			}
			// setting the transaction state for exception and setting the
			// context, ApplicationException is thrown while executing the query

			setMoState("error occured while updating Modification unallocated for business type id "
					+ aoUnallocatedFundsBean.getBudgetId());
			aoAppExp.addContextData("Exception occured while updating Modification unallocated ", aoAppExp);
			LOG_OBJECT.Error("error occured while updating Modification unallocated ", aoAppExp);
			throw aoAppExp;
		}
		return lbUpdateStatus;
	}

	/**
	 * <p>
	 * The method is updated in Release 7. Added a parameter for showing
	 * approved modification. This method fetch unallocated funds details from
	 * DB
	 * <ul>
	 * <li>Call fetchModificationUnallocatedFunds query set sub budget id and
	 * Parent sub budget id as where clause</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession - Sql session object
	 * @param aoCBGridBeanObj - CBGridBean object
	 * @param aoMasterBean - MasterBean object
	 * @return loUnallocatedFunds - List<UnallocatedFunds>
	 * @throws ApplicationException - Application Exception if it occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List fetchModificationUnallocatedFunds(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj,
			MasterBean aoMasterBean) throws ApplicationException
	{
		List<UnallocatedFunds> loUnallocatedFunds = null;
		// Start QC 8394 R 7.9 add/delete Unallocated Fund
		List<UnallocatedFunds> loModifiedUnallocatedFunds = null;
		// End QC 8394 R 7.9 add/delete Unallocated Fund
		try
		{
			// Start R7 changes ::Fetch the data from FileNet XML if Budget
			// Status is approved
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loUnallocatedFunds = new ContractBudgetAmendmentService().fetchUnallocatedFundsFromXML(lsSubBudgetId,
						aoMasterBean);
			}
			else if (null != aoCBGridBeanObj && null != aoCBGridBeanObj.getSubBudgetID())
			{
				// R7 end
				// fetching the details for unallocated Funds: base and newly added
				/*[Start] QC_9153 add null exception handling*/  
				Object loUnallocatedFundsObj = DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.MODIFICATION_FETCH_UNALLOCATED_FUNDS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				if(loUnallocatedFundsObj!=null)
					loUnallocatedFunds = (List<UnallocatedFunds>)loUnallocatedFundsObj;
				
			   //  Start QC 8394 R 7.9 add/delete Unallocated Fund
				// fetch Amendment of base line
				Object loModifiedUnallocatedFundsObj = DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_MODIFICATION_UNALLOCATED_FUNDS_TO_BASE_LINE, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				if(loModifiedUnallocatedFundsObj!=null)
					loModifiedUnallocatedFunds = (List<UnallocatedFunds>) loModifiedUnallocatedFundsObj;
				/*[End] QC_9153 add null exception handling*/  
				// update modification amount for base unallocated line if it amendment line has been found
				if (loModifiedUnallocatedFunds != null && !loModifiedUnallocatedFunds.isEmpty())
				{   
					for (UnallocatedFunds unallocatedFundsMod : loModifiedUnallocatedFunds)
					{   
						if (loUnallocatedFunds != null && !loUnallocatedFunds.isEmpty())
						{
							for (UnallocatedFunds unallocatedFundsBase : loUnallocatedFunds)
							{   
								if (unallocatedFundsBase.getId().equals(unallocatedFundsMod.getParentId()))
								{
									unallocatedFundsBase.setModificationAmount(unallocatedFundsMod.getModificationAmount());
									unallocatedFundsBase.setChildId(unallocatedFundsMod.getId());
									unallocatedFundsBase.setModCount(1);
									break;
								}
							}
						}
					}
					
				}
				// add _newrecord to newrecord
				
				if (loUnallocatedFunds != null && !loUnallocatedFunds.isEmpty())
				{
					for (UnallocatedFunds uf : loUnallocatedFunds)
					{   
						if ("new".equalsIgnoreCase(uf.getType()))
						{
							uf.setId(uf.getId() + HHSConstants.NEW_RECORD);
				
						}
					}
				}
				/* Unallocated Funds is nor default item 
				 * 
				// Inserting default object, here we are inserting two rows if
				// both Base and modification version is not there in the
				// database
				if (loUnallocatedFunds.isEmpty())
				{
					loUnallocatedFunds = new ArrayList<UnallocatedFunds>();
					UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
					loUnallocatedFunds.add(loUnallocatedFundsBean);
				}
				// Inserting default object, here we are inserting one rows for
				// modification version
				else if (HHSConstants.INT_ZERO == loUnallocatedFunds.get(HHSConstants.INT_ZERO).getModCount())
				{
					DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.MODIFICATION_INSERT_MOD_AMOUNT, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				}
				*/
			//  End QC 8394 R 7.9 add/delete Unallocated Fund	
			}
		}
		catch (ApplicationException aoAppExp)
		{
			// setting the transaction state for exception and setting the
			// context
			setMoState("error occured while fetching Modification unallocated for business type id "
					+ aoCBGridBeanObj.getContractBudgetID());
			aoAppExp.addContextData("Exception occured while fetching Modification unallocated ", aoAppExp);
			LOG_OBJECT.Error("error occured while fetching Modification unallocated ", aoAppExp);
			throw aoAppExp;
		}
		// HANDLING EXCEPTION
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured while fetching UnallocatedFunds in ContractBudgetModificationService ", aoEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: fetchUnallocatedFunds method - failed to fetch"
					+ aoCBGridBeanObj.getBudgetTypeId() + " \n");
			throw new ApplicationException("Error occured while fetching the details for Unallocated Funds", aoEx);
		}

		return loUnallocatedFunds;
	}

	/**
	 * <p>
	 * The method is updated in Release 7. Added a parameter for showing approved modification.
	 * This method is used for fetching values in Milestone grid for a
	 * particular sub-budget for Contract Budget Modification and Update.
	 * <ul>
	 * <li>Fetches the milestone information from the database on load.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBGridBeanObj - CBGridBean object
	 * @param aoMybatisSession - SqlSession object
	 * @param aoMasterBean - MasterBean object
	 * @return loCBMileStoneBean - CBMileStoneBean
	 * @throws ApplicationException - ApplicationException object
	 */
	public List<CBMileStoneBean> fetchMilestone(CBGridBean aoCBGridBeanObj, SqlSession aoMybatisSession,
			MasterBean aoMasterBean) throws ApplicationException
	{
		List<CBMileStoneBean> loCBMileStoneBean = null;
		try
		{
			/*[Start] QC_9153 add null exception handling*/  
			if(aoCBGridBeanObj!=null){
				aoCBGridBeanObj.setEntryTypeId(HHSConstants.EIGHT);
				// Start R7 changes ::Fetch the data from FileNet XML if Budget Status is approved
				String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
				if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
				{
					String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
					loCBMileStoneBean = new ContractBudgetAmendmentService().generateMilestoneList(lsSubBudgetId,
						aoMasterBean);
				}
				else
				{
					// R7 end
					// Fetching list of records for modification budget
					List<CBMileStoneBean> loCBMileStoneBeanForModification = fetchMilestoneForModification(
						aoMybatisSession, aoCBGridBeanObj);

					// Fetching list of records for base budget
					if(loCBMileStoneBeanForModification!=null)
						loCBMileStoneBean = fetchMilestoneForBase(aoMybatisSession, aoCBGridBeanObj);

					if(loCBMileStoneBean!=null)
						setModificationBudgetDetailsBean(loCBMileStoneBeanForModification, loCBMileStoneBean);
				}
			}//if(aoCBGridBeanObj!=null)
			/*[End] QC_9153 add null exception handling*/  
		}
		// HANDLING APPLICATION EXCEPTION
		catch (ApplicationException aoAppEx)
		{
			setMoState("error occured while fetching MileStone Details for budget type id "
					+ aoCBGridBeanObj.getBudgetTypeId());
			aoAppEx.addContextData("Exception occured while fetching MileStone Details ", aoAppEx);
			LOG_OBJECT.Error("error occured while fetching MileStone Details ", aoAppEx);
			throw aoAppEx;
		}
		// HANDLING EXCEPTION
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured while fetching Milestone in ContractBudgetModificationService ", aoEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: fetchMilestone method - failed to fetch"
					+ aoCBGridBeanObj.getBudgetTypeId() + " \n");
			throw new ApplicationException("Error occured while fetching the details for Milestone", aoEx);
		}
		return loCBMileStoneBean;
	}

	/**
	 * This method is used to fetch modification details of milestone
	 * 
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param aoCBGridBeanObj - CBGridBean
	 * @return loMilestoneForModification - List of CBMileStoneBean
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	private List<CBMileStoneBean> fetchMilestoneForModification(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj)
			throws ApplicationException
			
	{
		/*[Start] QC_9153 add null exception handling*/  
		List<CBMileStoneBean> loMilestoneForModification = new ArrayList<CBMileStoneBean>();
		
		Object loMilestoneForModificationObj = DAOUtil.masterDAO(aoMybatisSession,
				aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
				HHSConstants.FETCH_MILESTONE_FOR_MODIFICATION, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		if(loMilestoneForModificationObj!=null)
			loMilestoneForModification = (List<CBMileStoneBean>) loMilestoneForModificationObj;
		/*[End] QC_9153 add null exception handling*/  
		return loMilestoneForModification;
	}

	/**
	 * This method is used to fetch Base budget details of Milestone <li>Query
	 * used : fetchMilestoneBaseDetails</li>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param aoCBGridBeanObj - CBGridBean
	 * @return loMilestoneForBase - List of CBMileStoneBean
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	private List<CBMileStoneBean> fetchMilestoneForBase(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj)
			throws ApplicationException
	{
		/*[Start] QC_9153 add null exception handling*/  
		List<CBMileStoneBean> loMilestoneForBase = new ArrayList<CBMileStoneBean>();
		
		Object loMilestoneForBaseObj = DAOUtil.masterDAO(aoMybatisSession,
				aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
				HHSConstants.FETCH_MILESTONE_BASE_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		if(loMilestoneForBaseObj!=null)
			loMilestoneForBase = (List<CBMileStoneBean>) loMilestoneForBaseObj;
		/*[End] QC_9153 add null exception handling*/  
		return loMilestoneForBase;
	}

	/**
	 * This method is used to set modification details (amount) in MilestoneList
	 * <li>1. For each record of aoModificationList, if ParentId equals to id of
	 * any record of aoMilestoneList, modification amount of first list will be
	 * set to second list.
	 * 
	 * @param aoModificationList - List of CBMileStoneBean
	 * @param aoMilestoneList - List of CBMileStoneBean
	 * @throws ApplicationException - ApplicationException object
	 */
	private void setModificationBudgetDetailsBean(List<CBMileStoneBean> aoModificationList,
			List<CBMileStoneBean> aoMilestoneList) throws ApplicationException
	{
		if (aoModificationList != null && !aoModificationList.isEmpty())
		{
			for (CBMileStoneBean loMsBase : aoMilestoneList)
			{
				for (CBMileStoneBean loMsModification : aoModificationList)
				{
					if (loMsBase.getId().equals(loMsModification.getParentId()))
					{
						loMsBase.setModificationAmount(loMsModification.getModificationAmount());
						break;
					}
				}
			}
			for (CBMileStoneBean loMsModification : aoModificationList)
			{
				if (loMsModification.getId().equals(loMsModification.getParentId()))
				{
					loMsModification.setId(loMsModification.getId() + HHSConstants.NEW_RECORD);
					aoMilestoneList.add(HHSConstants.INT_ZERO, loMsModification);
				}
			}
		}
	}

	/**
	 * This method is triggered Milestone Grid.
	 * <ul>
	 * <li>Get the Sequence for milestoneId from Milestone</li>
	 * <li>An integer value is returned which determines the sequence of
	 * milestoneId from Milestone table.</li>
	 * <li>Query used : getSeqForMilestone</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @return loCurrentSeq - Integer
	 * @throws ApplicationException - ApplicationException object
	 */
	public Integer getSeqForMilestone(SqlSession aoMybatisSession) throws ApplicationException
	{
		Integer loCurrentSeq = HHSConstants.INT_ZERO;
		/*[Start] QC_9153 add null exception handling*/  
		Object loCurrentSeqObj = DAOUtil.masterDAO(aoMybatisSession, null,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBY_GET_SEQ_FOR_MILESTONE,
				null);
		if(loCurrentSeqObj!=null)
			loCurrentSeq = (Integer) loCurrentSeqObj;
		/*[End] QC_9153 add null exception handling*/  
		return loCurrentSeq;
	}

	/**
	 * <p>
	 * This method is used for adding new rows in Milestone grid for Contract
	 * Budget Modification for a particular budget and sub-budgets.
	 * <ul>
	 * <li>Provider is able to Add new Milestone by creating a new row, typing
	 * in the Milestone title and entering the FY Budget amount.</li>
	 * <li>Query used : insertMilestoneDetails</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCurrentSeq - Integer
	 * @param aoCBMilestoneBean - CBMileStoneBean object
	 * @param aoMybatisSession - SqlSession object
	 * @return loAddMilestone - Boolean status
	 * @throws ApplicationException - ApplicationException object
	 */
	public Boolean addMilestone(Integer aoCurrentSeq, CBMileStoneBean aoCBMilestoneBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		Boolean loAddMilestone = false;
		Integer loStatus = HHSConstants.INT_ZERO;
		boolean lbError = false;
		try
		{
			// Start QC 9206 R 7.7.1
			// CHECK IF MODIFICATION AMOUNT IS NOT LESS THAN ALREADY INVOICED
			// AMOUNT
			/*[Start] QC_9153 add null exception handling*/  
			if(aoCBMilestoneBean!=null){
				if (new BigDecimal(aoCBMilestoneBean.getModificationAmount()).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO)
				{
				
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.CBM_LESS_THAN_INVOICE_FOR_NEG_AMEND));
				}
				else
				{	
					// End QC 9206 R 7.7.1
					// ADD NEW MILESTONE WITH MODIFICATION AMOUNT
					aoCBMilestoneBean.setId(String.valueOf(aoCurrentSeq));
					Object loStatusObj = DAOUtil.masterDAO(aoMybatisSession, aoCBMilestoneBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBY_INSERT_MILESTONE_DETAILS, HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_MILE_STONE_BEAN);
					if(loStatusObj!=null)
						loStatus = (Integer) loStatusObj;
					
					if (loStatus > HHSConstants.INT_ZERO)
					{
						loAddMilestone = true;
					}
				}
			}
			/*[End] QC_9153 add null exception handling*/  
		}
		// HANDLING APPLICATION EXCEPTION
		catch (ApplicationException aoAppEx)
		{
			// Start QC 9206 R 7.7.1
			if (lbError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
				LOG_OBJECT.Error("server side validation fail for negative amount in Milestone ", aoAppEx);
			}
			else 
			{
				aoAppEx.addContextData("Exception occured while adding Milestone Details ", aoAppEx);
				LOG_OBJECT.Error("error occured while adding Milestone Details ", aoAppEx);
			}
			// End QC 9206 R 7.7.1
			setMoState("error occured while adding Milestone Details for budget type id "
					+ aoCBMilestoneBean.getBudgetTypeId());
						
			throw aoAppEx;
		}
		// HANDLING EXCEPTION
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured while adding Milestone in ContractBudgetModificationService ", aoEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: addMilestone method - failed to add"
					+ aoCBMilestoneBean.getBudgetTypeId() + " \n");
			throw new ApplicationException("Error occured while inserting the details for Milestone", aoEx);
		}
		
		return loAddMilestone;
	}

	/**
	 * <p>
	 * This method is used for editing the rows in Milestone grid for Contract
	 * Budget Modification for a particular budget and sub-budgets.
	 * <ul>
	 * <li>Provider is able to Edit the Milestone line items that have
	 * previously been added.</li>
	 * <li>Query used : fetchMilestoneDetailsForValidation</li>
	 * <li>Query used : updateMilestoneDetails</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBMilestoneBean - CBMileStoneBean object
	 * @param aoMybatisSession - SqlSession object
	 * @return loUpdateMilestone - Boolean status
	 * @throws ApplicationException - ApplicationException object
	 */
	public Boolean updateMilestone(CBMileStoneBean aoCBMilestoneBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		Boolean loUpdateMilestone = false;
		boolean lbError = false;
		CBMileStoneBean loCBMileStoneBean = null;

		try
		{
			/*[Start] QC_9153 add null exception handling*/  
			if(aoCBMilestoneBean!=null){
				Integer loStatus = HHSConstants.INT_ZERO;

				// GET AMOUNT DETAILS FOR SERVER SIDE VALIDATION
				Object loCBMileStoneBeanObj=DAOUtil.masterDAO(aoMybatisSession, aoCBMilestoneBean.getId(),
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_FETCH_MILESTONE_DETAILS_FOR_VALIDATION, HHSConstants.JAVA_LANG_STRING);
				if(loCBMileStoneBeanObj!=null)
					loCBMileStoneBean = (CBMileStoneBean) loCBMileStoneBeanObj;

				if (aoCBMilestoneBean.getMileStone().equals(HHSConstants.EMPTY_STRING)
						|| aoCBMilestoneBean.getMileStone() == null)
				{
					aoCBMilestoneBean.setMileStone(loCBMileStoneBean.getMileStone());
					aoCBMilestoneBean.setAmount(loCBMileStoneBean.getAmount());
				}
				// FOR NEWLY ADDED ROWS FROM MODIFICATION FOR EDIT OPERATION
				// AMOUNT ADDED WOULD NOT BE FY APPROVED BUDGET
				if (loCBMileStoneBean.getSubBudgetID().equals(aoCBMilestoneBean.getSubBudgetID()))
				{
					loCBMileStoneBean.setRemainAmt(HHSConstants.STRING_ZERO);
				}
				// CHECK IF MODIFICATION AMOUNT IS NOT LESS THAN ALREADY INVOICED
				// AMOUNT
				if ((new BigDecimal(loCBMileStoneBean.getRemainAmt()).add(new BigDecimal(aoCBMilestoneBean
					.getModificationAmount()))).compareTo(BigDecimal.ZERO) >= HHSConstants.INT_ZERO)
				{
					// UPDATE MILESTONE AMOUNT FOR THE SELECTED ROW ID
					loStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoCBMilestoneBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBY_UPDATE_MILESTONE_DETAILS,
						HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_MILE_STONE_BEAN);

					// INSERT MILESTONE TO BE UPDATED ON SELECT
					if (loStatus <= HHSConstants.INT_ZERO)
					{
						DAOUtil.masterDAO(aoMybatisSession, aoCBMilestoneBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.INSERT_NEW_MILESTONE_FOR_MOD,
							HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_MILE_STONE_BEAN);
					}
					loUpdateMilestone = true;
				}
				else
				{
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CBM_LESS_THAN_INVOICE_FOR_NEG_AMEND));
				}
			}//if(aoCBMilestoneBean!=null)
			/*[End] QC_9153 add null exception handling*/  
		}
		// HANDLING APPLICATION EXCEPTION
		catch (ApplicationException aoAppExp)
		{
			if (lbError)
			{
				aoAppExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppExp.toString());
				LOG_OBJECT.Error("server side validation fail for negative amount in Milestone ", aoAppExp);
			}
			else
			{
				aoAppExp.addContextData("Exception occured while editing Milestone ", aoAppExp);
				LOG_OBJECT.Error("error occured while editing Milestone ", aoAppExp);
			}
			setMoState("error occured while editing Milestone for budget type id "
					+ aoCBMilestoneBean.getBudgetTypeId());
			throw aoAppExp;
		}
		// HANDLING EXCEPTION
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured while editing Milestone in ContractBudgetModificationService ", aoEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: updateMilestone method - failed to edit"
					+ aoCBMilestoneBean.getBudgetTypeId() + " \n");
			throw new ApplicationException("Error occured while editing Milestone", aoEx);
		}
		return loUpdateMilestone;
	}

	/**
	 * <p>
	 * This method is used for deleting the added rows in Milestone grid for
	 * Contract Budget Modification for a particular budget and sub-budgets.
	 * <ul>
	 * <li>Provider is able to Delete Milestone line items entirely on the
	 * Pending Submission budget submission for the newly added line items.</li>
	 * <li>Query used : deleteMilestoneDetails</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBMilestoneBean - CBMileStoneBean object
	 * @param aoMybatisSession - SqlSession object
	 * @return loDeleteMilestone - Boolean status
	 * @throws ApplicationException - ApplicationException object
	 */
	public Boolean deleteMilestone(CBMileStoneBean aoCBMilestoneBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		Boolean loDeleteMilestone = false;
		Integer loStatus = HHSConstants.INT_ZERO;
		try
		{
			// DELETE THE LINE ITEM FOR MILESTONE
			/*[Start] QC_9153 add null exception handling*/  
			Object loStatusObj =  DAOUtil.masterDAO(aoMybatisSession, aoCBMilestoneBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_DELETE_MILESTONE_DETAILS, HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_MILE_STONE_BEAN);
			if(loStatusObj!=null)
				loStatus = (Integer) loStatusObj;
			/*[End] QC_9153 add null exception handling*/  
			
			if (loStatus > HHSConstants.INT_ZERO)
			{
				loDeleteMilestone = true;
			}
		}
		// HANDLING APPLICATION EXCEPTION
		catch (ApplicationException aoAppEx)
		{
			setMoState("error occured while deleting Milestone Details for budget type id " + aoCBMilestoneBean);
			aoAppEx.addContextData("Exception occured while deleting Milestone Details ", aoAppEx);
			LOG_OBJECT.Error("error occured while deleting Milestone Details ", aoAppEx);
			throw aoAppEx;
		}
		// HANDLING EXCEPTION
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error(
					"Exception occured while editing deleting milestone in ContractBudgetModificationService ", aoEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: deleteMilestone method - failed to delete"
					+ aoCBMilestoneBean.getBudgetTypeId() + " \n");
			throw new ApplicationException("Error occured while deleting Milestone", aoEx);
		}
		return loDeleteMilestone;
	}

	/**
	 * The method is updated in Release 7. Added a parameter for showing approved modification.
	 * This method is used to fetch rate line-items from Rate table.
	 * 
	 * It queries into DB to fetch two RateBeanList and then merge them using
	 * <b>mergeModificationAmount</b> method. It then calls
	 * <b>markNewRowInContractBudgetBeanList</b> to mark the newly added rows.
	 * 
	 * @param aoMybatisSession : MyBatis session for SQL Session
	 * @param aoRateBeanObj : CBGridBean object
	 * @param aoMasterBean - MasterBean object
	 * @return loRateBeanList - List<RateBean> : returns the merged rateBean
	 *         list to be shown in Rate Grid
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<RateBean> fetchContractBudgetModificationRate(SqlSession aoMybatisSession, CBGridBean aoRateBeanObj,
			MasterBean aoMasterBean) throws ApplicationException
	{
		List<RateBean> loRateBeanList = null;
		List<RateBean> loRateBeanModificationAmntList = null;
		try
		{
			if (aoRateBeanObj != null)
			{
				// Start R7 changes ::Fetch the data from FileNet XML if Budget Status is approved
				String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_BUDGET_APPROVED);
				if (aoMasterBean != null && aoRateBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
				{
					String lsSubBudgetId = aoRateBeanObj.getSubBudgetID();
					List<LineItemMasterBean> loMasterBeanList = null;
					loMasterBeanList = aoMasterBean.getMasterBeanList();
					Iterator<LineItemMasterBean> aoListIterator = loMasterBeanList.iterator();
					while (aoListIterator.hasNext())
					{
						LineItemMasterBean loLineItemBean = aoListIterator.next();
						if (loLineItemBean.getSubbudgetId().equals(lsSubBudgetId))
						{
							loRateBeanList = loLineItemBean.getRateBeanList();
						}
					}
				}
				else
				{
					// R7 end
					String lsParentSubBudgetId = aoRateBeanObj.getParentSubBudgetId();
					loRateBeanList = (List<RateBean>) DAOUtil.masterDAO(aoMybatisSession, aoRateBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBF_FETCH_CONTRACT_BUDGET_RATE_INFO,
							HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

					loRateBeanModificationAmntList = (List<RateBean>) DAOUtil.masterDAO(aoMybatisSession,
							aoRateBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBF_FETCH_CONTRACT_BUDGET_MODIFICATION_AMOUNT,
							HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

					// Merge Modification Amount
					mergeModificationAmount(loRateBeanList, loRateBeanModificationAmntList);
					// Identify and Mark New rows
					markNewRowInContractBudgetBeanList(loRateBeanList, lsParentSubBudgetId);
				}
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled. It throws Application Exception back
		// to Controller's calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.CB_GRID_BEAN_OBJ, CommonUtil.convertBeanToString(aoRateBeanObj));
			LOG_OBJECT.Error("Exception occured while retrieving ContractBudgetModificationRateInfo", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService:fetchContractBudgetModificationRate method - failed to fetch record "
					+ " \n");
			throw aoAppEx;
		}
		return loRateBeanList;
	}

	/**
	 * Method is called from fetchContractBudgetModificationRate method. It
	 * takes two rateBeanList as parameter and merge the modification Amount
	 * from second list into first one.
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoRateBeanList : rate bean list with remaining and FY amount.
	 * @param aoRateBeanModificationAmntList : rate bean list with modification
	 *            amount.
	 */
	private void mergeModificationAmount(List<RateBean> aoRateBeanList, List<RateBean> aoRateBeanModificationAmntList)
	{
		for (int loCountOut = 0; loCountOut < aoRateBeanList.size(); loCountOut++)
		{
			for (int loCountIn = 0; loCountIn < aoRateBeanModificationAmntList.size(); loCountIn++)
			{
				if (aoRateBeanList.get(loCountOut).getId()
						.equals(aoRateBeanModificationAmntList.get(loCountIn).getLsParentId()))
				{
					aoRateBeanList.get(loCountOut).setLsModifyUnits(
							aoRateBeanModificationAmntList.get(loCountIn).getLsModifyUnits());
					aoRateBeanList.get(loCountOut).setLsModifyAmount(
							aoRateBeanModificationAmntList.get(loCountIn).getLsModifyAmount());
					break;
				}
			}
		}
	}

	/**
	 * Method iterates the RateBeanList and identifies the new row on the basis
	 * of <b>Sub-BudgetId</b>. After the new row is identified the Id for that
	 * particular Sub-Budget is appended with <b>"_newrecord"</b>, so that it
	 * can be distinguished from other line-items.
	 * @param aoRateBeanList : returns the merged bean List with distinguished
	 *            new row.
	 * @param asParentSubBudgetId : passes the parent sub-budget which
	 *            differentiates the new row
	 */
	private void markNewRowInContractBudgetBeanList(List<RateBean> aoRateBeanList, String asParentSubBudgetId)
	{
		for (RateBean loRateObj : aoRateBeanList)
		{
			if (!loRateObj.getSubBudgetID().equalsIgnoreCase(asParentSubBudgetId))
			{
				loRateObj.setId(loRateObj.getId() + HHSConstants.NEW_RECORD);
			}
		}
	}

	/**
	 * Method is used to insert a new line-item entry into RATE table. <li>
	 * Query used : insertContractBudgetModificationRateInfo</li>
	 * @param aoMybatisSession : MyBatis session for SQL Session
	 * @param aoRateBeanObj - RateBean : RateBean object containing the required
	 *            parameters
	 * @return loRowInserted : returns the number of row inserted successfully
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("rawtypes")
	public Integer insertContractBudgetModificationRateInfo(SqlSession aoMybatisSession, RateBean aoRateBeanObj)
			throws ApplicationException
	{
		Integer loRowInserted = HHSConstants.INT_ZERO;
		Boolean loError = false;
		HashMap loHashMap = null;
		try
		{
			if (aoRateBeanObj != null)
			{
				loHashMap = validateRateModificationData(aoMybatisSession, aoRateBeanObj);
				if ((Boolean) loHashMap.get(HHSConstants.IS_VALID_DATA))
				{
					loRowInserted = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoRateBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBF_INSERT_CONTRACT_BUDGET_MODIFICATION_RATE_INFO,
							HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN);
					setMoState("Method insertContractBudgetModificationRateInfo executed succesfully");
				}
				else
				{
					loError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							(String) loHashMap.get(HHSConstants.ERROR_MESSAGE)));
				}
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			if (loError)
			{
				// In case of modification data validation failure, the
				// exception will be thrown back to base controller
				// along with the validation message
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			aoAppEx.addContextData(HHSConstants.AO_RATE_BEAN_OBJ, aoRateBeanObj);
			LOG_OBJECT.Error("Exception occured while inserting ContractBudgetModificationRateInfo", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService:insertContractBudgetModificationRateInfo method - failed to insert record "
					+ " \n");
			throw aoAppEx;
		}
		return loRowInserted;
	}

	/**
	 * This method is called on update of any rate line-item in Contract Budget
	 * Modification on the basis of modifiedSubBudgetId, parentId. <li>Query
	 * used : updateContractBudgetModificationRateInfo</li> In case a matching
	 * row is not found in DB, a new row is inserted in DB.
	 * 
	 * @param aoMybatisSession : MyBatis session for SQL Session
	 * @param aoRateBeanObj : rateBean object containing required information
	 * @return loRowUpdated : returns the number of rows updated
	 * @throws ApplicationException : ApplicationException object
	 */
	@SuppressWarnings("rawtypes")
	public Integer updateContractBudgetModificationRateInfo(SqlSession aoMybatisSession, RateBean aoRateBeanObj)
			throws ApplicationException
	{
		Integer loRowUpdated = HHSConstants.INT_ZERO;
		String lsUnitDesc = HHSConstants.EMPTY_STRING;
		Boolean loError = false;
		HashMap loHashMap = null;
		try
		{
			if (aoRateBeanObj != null)
			{
				loHashMap = validateRateModificationData(aoMybatisSession, aoRateBeanObj);
				if ((Boolean) loHashMap.get(HHSConstants.IS_VALID_DATA))
				{
					// Update method for newly added row
					if (aoRateBeanObj.getUnitDesc() != null && !aoRateBeanObj.getUnitDesc().isEmpty())
					{
						loRowUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoRateBeanObj,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
								HHSConstants.CBF_UPDATE_CONTRACT_BUDGET_MODIFICATION_RATE_INFO,
								HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN);
					}
					else
					{
						// Update for the base rows. Returns zero is no
						// modification row exists already
						loRowUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoRateBeanObj,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
								HHSConstants.CBF_UPDATE_BUDGET_MODIFICATION_RATE_UNIT,
								HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN);
						if (loRowUpdated == HHSConstants.INT_ZERO)
						{
							// In case no modification row exists for the base
							// row, fetch the UnitDesc and then insert
							// a new modification row.
							lsUnitDesc = (String) DAOUtil.masterDAO(aoMybatisSession, aoRateBeanObj,
									HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
									HHSConstants.CBF_GET_UNIT_DESC_CONTRACT_BUDGET_MODIFICATION_RATE,
									HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN);
							aoRateBeanObj.setUnitDesc(lsUnitDesc);
							DAOUtil.masterDAO(aoMybatisSession, aoRateBeanObj,
									HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
									HHSConstants.CBF_INSERT_NEW_CONTRACT_BUDGET_MODIFICATION_RATE,
									HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN);
						}
					}
					setMoState("Method updateContractBudgetModificationRateInfo executed succesfully");
				}
				else
				{
					loError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							(String) loHashMap.get(HHSConstants.ERROR_MESSAGE)));
				}
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			if (loError)
			{
				// In case of modification data validation failure, the
				// exception will be thrown back to base controller
				// along with the validation message
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			aoAppEx.addContextData(HHSConstants.AO_RATE_BEAN_OBJ, aoRateBeanObj);
			LOG_OBJECT.Error("Exception occured while updating ContractBudgetModificationRateInfo", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService:updateContractBudgetModificationRateInfo method - failed to insert record "
					+ " \n");
			throw aoAppEx;
		}
		return loRowUpdated;
	}

	/**
	 * This method deletes a budget modification rate line-item on the basis of
	 * <b>id</b> and <b>subBudgetID</b>.
	 * 
	 * <ul>
	 * <li>Line-item will be deleted from DB only if it is a newly added row.</li>
	 * <li>Query used : deleteContractBudgetModificationRateInfo</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession : MyBatis session for SQL Session
	 * @param aoRateBeanObj - RateBean : RateBean object containing the required
	 *            parameters
	 * @return loRowDeleted : returns the number of rows deleted
	 * @throws ApplicationException - ApplicationException object
	 */
	public Integer deleteContractBudgetModificationRateInfo(SqlSession aoMybatisSession, RateBean aoRateBeanObj)
			throws ApplicationException
	{
		Integer loRowDeleted = HHSConstants.INT_ZERO;
		try
		{
			if (aoRateBeanObj != null)
			{
				loRowDeleted = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoRateBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBF_DELETE_CONTRACT_BUDGET_MODIFICATION_RATE_INFO,
						HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN);
				setMoState("Method deleteContractBudgetModificationRateInfo executed succesfully");
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.AO_RATE_BEAN_OBJ, aoRateBeanObj);
			LOG_OBJECT.Error("Exception occured while deleting ContractBudgetModificationRateInfo", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService:deleteContractBudgetModificationRateInfo method - failed to insert record "
					+ " \n");
			throw aoAppEx;
		}
		return loRowDeleted;
	}

	/**
	 * This method validates the Base Unit count for a particular line-item and
	 * returns FALSE if the #units user is trying to update tries to make the
	 * total #units for a line-item to fall below zero, else it validates it as
	 * TRUE
	 * <ul>
	 * <li>Query used :fetchRateValidationRemngUnits</li>
	 * <li>This method was updated in R4</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession : MyBatis session for SQL Session
	 * @param aoRateBeanObj : RateBean object containing the required parameters
	 * @return loHashMap : returns the hashmap containing the validation flag
	 *         and message.
	 * @throws ApplicationException - ApplicationException object
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private HashMap validateRateModificationData(SqlSession aoMybatisSession, RateBean aoRateBeanObj)
			throws ApplicationException
	{
		Boolean loIsDataValid = true;
		HashMap loHashMap = new HashMap();
		BigDecimal loRemainingAmount = null;
		Integer loRemainingUnit = null;
		loHashMap.put(HHSConstants.IS_VALID_DATA, loIsDataValid);
		try
		{
			// No validation for adding new row
			if (aoRateBeanObj.getId() != null && !aoRateBeanObj.getId().contains(HHSConstants.NEW_ROW_IDENTIFIER))
			{
				loRemainingUnit = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoRateBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_RATE_VALIDATION_REMNG_UNITS, HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN);
			}
			loRemainingUnit = ((loRemainingUnit == null) ? HHSConstants.INT_ZERO : loRemainingUnit);
			// validates the modification unit with remaining units
			if (aoRateBeanObj.getLsModifyUnits() != null
					&& (loRemainingUnit + Integer.parseInt((aoRateBeanObj.getLsModifyUnits())) < HHSConstants.INT_ZERO))
			{
				loHashMap.put(HHSConstants.IS_VALID_DATA, Boolean.FALSE);
				loHashMap.put(HHSConstants.ERROR_MESSAGE, HHSConstants.BUDGET_MODIFICATION_RATE_UNIT_VALIDATION);
			}

			// validates the modification amount so that it should not fall
			// below YTD Amount
			if (loIsDataValid && aoRateBeanObj.getId() != null
					&& !aoRateBeanObj.getId().contains(HHSConstants.NEW_ROW_IDENTIFIER))
			{
				loRemainingAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoRateBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.QRY_GET_REMAINING_AMOUNT_MODIFICATION_RATE,
						HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN);
			}
			loRemainingAmount = ((loRemainingAmount == null) ? BigDecimal.ZERO : loRemainingAmount);
			if (aoRateBeanObj.getLsModifyAmount() != null
					&& !(loRemainingAmount.add(new BigDecimal(aoRateBeanObj.getLsModifyAmount())).compareTo(
							BigDecimal.ZERO) >= HHSConstants.INT_ZERO))
			{
				loHashMap.put(HHSConstants.IS_VALID_DATA, Boolean.FALSE);
				loHashMap.put(HHSConstants.ERROR_MESSAGE, HHSConstants.BUDGET_MODIFICATION_RATE_AMNT_VALIDATION);
			}
		}
		catch (ApplicationException aoAppEx)
		{
			// Any Exception from DAO class will be thrown as Application
			// Exception will be handled over here. It throws Application
			// Exception
			// back to calling method.
			aoAppEx.addContextData(HHSConstants.AO_RATE_BEAN_OBJ, aoRateBeanObj);
			LOG_OBJECT.Error("Exception while validating modification data", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService:validateRateModificationData method" + " \n");
			throw aoAppEx;
		}
		return loHashMap;
	}

	/**
	 * <p>
	 * The method is updated in Release 7. Added a parameter for showing approved modification.
	 * Updated in R7 This method fetches Program Income List corresponding to a
	 * Contract Id Id and Budget Id
	 * <ul>
	 * <li>1. Fetches Program Income List - against SubBudgetId</li>
	 * as well</li>
	 * <li>Query used : fetchProgramIncomeModification</li>
	 * <li>Query used : fetchProgramIncomeModificationParentEqualSub</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBGridBeanObj - CBGridBean object containing key fields
	 * @param aoMybatisSession - SqlSession object
	 * @param aoMasterBean - MasterBean object
	 * @return loCBProgramIncomeBean - CBProgramIncomeBean
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<CBProgramIncomeBean> fetchProgramIncomeModification(CBGridBean aoCBGridBeanObj,
			SqlSession aoMybatisSession, MasterBean aoMasterBean) throws ApplicationException
	{

		List<CBProgramIncomeBean> loCBOPModList = null;
		List<CBProgramIncomeBean> loCBProgramIncomeBeanList = null;
		String lsParentBudgetId = aoCBGridBeanObj.getParentBudgetId();
		String lsParentSubBudgetId = aoCBGridBeanObj.getParentSubBudgetId();
		try
		{
			// Start R7 changes ::Fetch the data from FileNet XML if Budget Status is approved
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				loCBProgramIncomeBeanList=fetchPIDetailsforApprovedModifications(aoCBGridBeanObj,aoMasterBean);
			}
			else
			{
				HashMap<String, CBProgramIncomeBean> loInputMapBean = new HashMap<String, CBProgramIncomeBean>();
				// R7 end
				if (aoCBGridBeanObj.getParentSubBudgetId() != null && aoCBGridBeanObj.getSubBudgetID() != null
						&& !aoCBGridBeanObj.getParentSubBudgetId().equalsIgnoreCase(aoCBGridBeanObj.getSubBudgetID()))
				{
					loCBProgramIncomeBeanList = (List<CBProgramIncomeBean>) DAOUtil.masterDAO(aoMybatisSession,
							aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.FETCH_PROGRAM_INCOME_MODIFICATION, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
					// Start: Added in R7 for PI
					Iterator<CBProgramIncomeBean> loItrListParent = loCBProgramIncomeBeanList.iterator();
					while (loItrListParent.hasNext())
					{
						CBProgramIncomeBean loBean = loItrListParent.next();
						loInputMapBean.put(loBean.getId(), loBean);
					}
					// End: Added in R7 for PI
				}
				else
				{
					loCBProgramIncomeBeanList = (List<CBProgramIncomeBean>) DAOUtil.masterDAO(aoMybatisSession,
							aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.FETCH_PROGRAM_INCOME_MODIFICATION_PARENT_EQUAL_SUB,
							HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				}
				//added in R7
				// Added in R7 to fetch old PI flag
				String lsIsOldPI = (String) DAOUtil.masterDAO(aoMybatisSession, lsParentBudgetId,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSR5Constants.FETCH_IS_OLD_PI,
						HHSConstants.JAVA_LANG_STRING);

				aoCBGridBeanObj.setIsOldPI(lsIsOldPI);
				loCBOPModList = (List<CBProgramIncomeBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_PROGRAM_INCOME_MOD_AMT_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				loCBProgramIncomeBeanList = mergeApprovedPIDetailsForModifications(lsIsOldPI, loInputMapBean,
						loCBProgramIncomeBeanList, loCBOPModList, lsParentSubBudgetId,aoCBGridBeanObj);
			}
		}
		// Exception occur if database is down or getting null in required
		// parameter
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.CB_GRID_BEAN_OBJ, CommonUtil.convertBeanToString(aoCBGridBeanObj));
			LOG_OBJECT.Error("App Exception occured while retrieving in ContractBudgetModificationService ", aoAppEx);
			setMoState("ContractBudgetModificationService: fetchProgramIncomeModification() failed to fetch "
					+ "with details aoCBGridBeanObj:" + aoCBGridBeanObj + " \n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetModificationService: fetchProgramIncomeModification method:: ", aoEx);
			LOG_OBJECT.Error("Exception occured while retrieving in ContractBudgetModificationService ", loAppEx);
			setMoState("ContractBudgetModificationService: fetchProgramIncomeModification() failed to fetch "
					+ "with details aoCBGridBeanObj:" + aoCBGridBeanObj + " \n");
			throw loAppEx;
		}
		return loCBProgramIncomeBeanList;
	}

	/**
	 * <p>
	 * Updated in R7
	 * This method updates Program Income Details corresponding to a
	 * ProgramIncomeId ( primary Key attribute of CBProgramsIncomeBean bean)
	 * <ul>
	 * <li>Updates the whole row of a Program Income</li>
	 * <li>Also updates the Program Income under Budget Modification</li>
	 * <li>Query used : updateProgramIncomeModification</li>
	 * <li>Query used : insertProgramIncomeModification</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMyBatisSession - SqlSessionId
	 * @param aoCBProgramIncomeBean - CBProgramsIncomeBean object
	 * @return Program Income object(CBProgramsIncomeBean type object)
	 * @throws ApplicationException - ApplicationException object
	 */
	public Boolean updateProgramIncomeModification(SqlSession aoMyBatisSession,
			CBProgramIncomeBean aoCBProgramIncomeBean) throws ApplicationException
	{
		boolean lbEditStatus = false;
		boolean lbEditAmtValid = false;
		boolean lbError = false;
		//Added in R7 to add validation for new PI grid
		String lsSubBudgetId = aoCBProgramIncomeBean.getSubBudgetId();
		HashMap loHashMapForModification = null;
		try
		{
			String lsIsOldPI = (String) DAOUtil.masterDAO(aoMyBatisSession, lsSubBudgetId,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSR5Constants.FETCH_IS_OLD_PI_FOR_VALIDATION,
					HHSConstants.JAVA_LANG_STRING);
			if (HHSConstants.ZERO.equals(lsIsOldPI))
			{
				loHashMapForModification = validateProgramIncomeModificationData(aoMyBatisSession, aoCBProgramIncomeBean);
				lbEditAmtValid= (Boolean)loHashMapForModification.get(HHSConstants.IS_VALID_DATA); 
			}
		    else
		    {
			lbEditAmtValid = validateModificationAmountForProgIncome(aoMyBatisSession, aoCBProgramIncomeBean);
		    }
			
			if (lbEditAmtValid)
			{
				Integer loUpdatedRows = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBProgramIncomeBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.UPDATE_PROGRAM_INCOME_MODIFICATION, HHSConstants.PROGRAM_INCOME_BEAN);

				if (null != loUpdatedRows && loUpdatedRows == HHSConstants.INT_ZERO)
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoCBProgramIncomeBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.INSERT_PROGRAM_INCOME_MODIFICATION, HHSConstants.PROGRAM_INCOME_BEAN);
				}
			}
			else
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSR5Constants.CBM_LESS_THAN_INVOICE_FOR_PI));
			}

			lbEditStatus = true;
			setMoState("ContractBudgetModificationService: updateProgramIncomeModification() passed");
		}
		// Exception occur if database is down or getting null in required
		// parameter
		catch (ApplicationException aoAppEx)
		{
			if (lbError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
				LOG_OBJECT.Error("Entered value would cause the Proposed Budget to fall below the "
						+ "amount already invoiced for the line item. Please enter a new value ", aoAppEx);
				setMoState("Entered value would cause the Proposed Budget to fall below the"
						+ "amount already invoiced for the line item. Please enter a new value");
			}
			else
			{
				aoAppEx.addContextData(HHSConstants.CB_PROGRAM_INCOME_BEAN,
						CommonUtil.convertBeanToString(aoCBProgramIncomeBean));
				LOG_OBJECT.Error("App Exception occured in ContractBudgetModificationService:"
						+ " updateProgramIncomeModification method:: ", aoAppEx);
				setMoState("Transaction Failed::App Exception in ContractBudgetModificationService: "
						+ "updateProgramIncomeModification() method aoCBProgramIncomeBean::" + aoCBProgramIncomeBean
						+ "\n");
			}
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetModificationService: updateProgramIncomeModification method:: ", aoEx);
			loAppEx.addContextData(HHSConstants.CB_PROGRAM_INCOME_BEAN,
					CommonUtil.convertBeanToString(aoCBProgramIncomeBean));
			LOG_OBJECT
					.Error("Exception occured in ContractBudgetModificationService: updateProgramIncomeModification method:: ",
							loAppEx);
			setMoState("Transaction Failed::Exception in ContractBudgetModificationService: "
					+ "updateProgramIncomeModification() method aoCBProgramIncomeBean::" + aoCBProgramIncomeBean + "\n");
			throw loAppEx;
		}
		return lbEditStatus;
	}

	/**
	 * This method validates the modification amount in Program income for a
	 * particular line-item and returns FALSE if the #amount user is trying to
	 * update tries to make the total amount for a line-item to fall below zero,
	 * else it validates it as TRUE <li>Query used :
	 * fetchProgIncomeDetailsForValidation</li>
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param aoCBOperationSupportBean CBOperationSupportBean object
	 * @return Boolean loModUnitIsValid valid/invalid
	 * @throws Exception ApplicationException object
	 */
	private Boolean validateModificationAmountForProgIncome(SqlSession aoMybatisSession,
			CBProgramIncomeBean aoCBProgramIncomeBean) throws Exception
	{
		Boolean loEditAmtValid = false;
		CBProgramIncomeBean loCBProgramIncomeBean = null;
		loCBProgramIncomeBean = (CBProgramIncomeBean) DAOUtil.masterDAO(aoMybatisSession,
				aoCBProgramIncomeBean.getId(), HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
				HHSConstants.CBM_FETCH_PROG_INCOME_DETAILS_FOR_VALIDATION, HHSConstants.JAVA_LANG_STRING);

		// check sum of modified amount and remaining amount should be greater
		// than zero
		if ((new BigDecimal(loCBProgramIncomeBean.getRemainingAmount()).add(new BigDecimal(aoCBProgramIncomeBean
				.getModificationAmount()))).compareTo(BigDecimal.ZERO) >= HHSConstants.INT_ZERO)
		{
			loEditAmtValid = true;
		}
		return loEditAmtValid;
	}

	/**
	 * The method is updated in Release 7. Added a parameter for showing approved modification.
	 * The Method will fetch the budget details and modification details of
	 * Salaried Employees grid of Personnel Services tab under contract budget
	 * modification module
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @param aoMasterBean - MasterBean object
	 * @return loSalariedEmployess List of PersonnelServiceBudget bean
	 * @throws ApplicationException - ApplicationException object
	 * 
	 */

	public List<PersonnelServiceBudget> fetchSalariedEmployeeBudgetForModification(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget, MasterBean aoMasterBean) throws ApplicationException
	{

		List<PersonnelServiceBudget> loSalariedEmployess = null;
		ContractBudgetService loCBService = new ContractBudgetService();

		try
		{
			// R7 changes Start: Fetch the data from FileNet XML if Budget Status is approved
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			if (aoMasterBean != null && aoPersonnelServiceBudget.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoPersonnelServiceBudget.getSubBudgetID();
				loSalariedEmployess = new ContractBudgetAmendmentService().fetchSalariedEmployeeFromXML(lsSubBudgetId,
						aoMasterBean);
			}
			else
			{
				// R7 changes End
				// Fetching list of records for modification budget
				List<PersonnelServiceBudget> loSalariedEmployessForModification = fetchSalariedEmployeeForModification(
						aoMybatisSession, aoPersonnelServiceBudget);

				// Fetching list of records for base budget
				CBGridBean loCBGridBeanObj = getCBGridBeanForBaseBudget(aoPersonnelServiceBudget);

				loSalariedEmployess = loCBService.fetchSalariedEmployeeForBase(aoMybatisSession, loCBGridBeanObj);

				setModificationBudgetDetailsinBean(loSalariedEmployessForModification, loSalariedEmployess);
			}

		}
		// catch any application exception thrown from the code due to SELECT
		// statement and throw it
		// forward
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData(
					"ApplicationException occured while fetching Salaried Employee budget :  fetchSalariedEmployeeBudgetForModification",
					aoExp);
			LOG_OBJECT
					.Error("ApplicationException occured while fetching Salaried Employee budget : fetchSalariedEmployeeBudgetForModification "
							+ aoExp);
			setMoState("ApplicationException occured while fetching Salaried Employee budget for budget id = "
					+ aoPersonnelServiceBudget.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudget.getSubBudgetID());
			throw aoExp;
		}
		// Catch any Null pointer exception thrown from the code and wrap it
		// into application
		// Exception and throw it forward
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while fetching Salaried Emplyee budget :  fetchSalariedEmployeeBudgetForModification ",
					aoExp);
			loAppEx.addContextData(
					"Exception occured while fetching Salaried Emplyee budget :  fetchSalariedEmployeeBudgetForModification",
					aoExp);
			LOG_OBJECT
					.Error("Exception occured while fetching Salaried Employee budget : fetchSalariedEmployeeBudgetForModification "
							+ aoExp);
			setMoState("Exception occured while adding fetching Employee budget for budget id = "
					+ aoPersonnelServiceBudget.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudget.getSubBudgetID());
			throw loAppEx;
		}
		return loSalariedEmployess;
	}

	/**
	 * The Method will return new object of CBGridBean with budgetid and
	 * subbudgetid of parent budget
	 * @param aoPersonnelServiceBudget CBGridBean object
	 * @return loCBGridBeanObj CBGridBean
	 */
	private CBGridBean getCBGridBeanForBaseBudget(CBGridBean aoPersonnelServiceBudget)
	{
		CBGridBean loCBGridBeanObj = new CBGridBean();
		loCBGridBeanObj.setContractBudgetID(aoPersonnelServiceBudget.getParentBudgetId());
		loCBGridBeanObj.setSubBudgetID(aoPersonnelServiceBudget.getParentSubBudgetId());
		return loCBGridBeanObj;
	}

	/**
	 * * The method is updated in Release 7. Added a parameter for showing approved modification.
	 * The Method will fetch the budget details of Hourly Employees grid of
	 * Personnel Services tab under contract budget module
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @param aoMasterBean - MasterBean object
	 * @return loSalariedEmployess List of PersonnelServiceBudget bean
	 * @throws ApplicationException - ApplicationException object
	 * 
	 */

	public List<PersonnelServiceBudget> fetchHourlyEmployeeBudgetForModification(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget, MasterBean aoMasterBean) throws ApplicationException
	{
		List<PersonnelServiceBudget> loSalariedEmployess = null;
		ContractBudgetService loCBService = new ContractBudgetService();
		try
		{
			// Start R7 changes ::Fetch the data from FileNet XML if Budget Status is approved
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			// Fetch the data from FileNet XML if Budget Status is approved
			if (aoMasterBean != null && aoPersonnelServiceBudget.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoPersonnelServiceBudget.getSubBudgetID();
				loSalariedEmployess = new ContractBudgetAmendmentService().fetchHourlyEmployeeFromXML(lsSubBudgetId,
						aoMasterBean);
			}
			else
			{// R7 end
				// Fetching list of records for modification budget
				List<PersonnelServiceBudget> loSalariedEmployessForModification = fetchHourlyEmployeeForModification(
						aoMybatisSession, aoPersonnelServiceBudget);

				// Fetching list of records for base budget
				CBGridBean loCBGridBeanObj = getCBGridBeanForBaseBudget(aoPersonnelServiceBudget);

				loSalariedEmployess = loCBService.fetchHourlyEmployeeForBase(aoMybatisSession, loCBGridBeanObj);

				setModificationBudgetDetailsinBean(loSalariedEmployessForModification, loSalariedEmployess);
			}
		}
		// catch any application exception thrown from the code due to SELECT
		// statement and throw it
		// forward
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData(
					"ApplicationException occured while fetching Hourly Employee budget :  fetchHourlyEmployeeBudgetForModification",
					aoExp);
			LOG_OBJECT
					.Error("ApplicationException occured while fetching Hourly Employee budget : fetchHourlyEmployeeBudgetForModification "
							+ aoExp);
			setMoState("ApplicationException occured while fetching Hourly Employee budget for budget id = "
					+ aoPersonnelServiceBudget.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudget.getSubBudgetID());
			throw aoExp;
		}
		// Catch any Null pointer exception thrown from the code and wrap it
		// into application
		// Exception and throw it forward
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while fetching Hourly Emplyee budget :  fetchHourlyEmployeeBudgetForModification ",
					aoExp);
			loAppEx.addContextData(
					"Exception occured while fetching Hourly Emplyee budget :  fetchHourlyEmployeeBudgetForModification",
					aoExp);
			LOG_OBJECT
					.Error("Exception occured while fetching Hourly Employee budget : fetchHourlyEmployeeBudgetForModification "
							+ aoExp);
			setMoState("Exception occured while fetching Hourly Employee budget for budget id = "
					+ aoPersonnelServiceBudget.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudget.getSubBudgetID());
			throw loAppEx;
		}
		return loSalariedEmployess;
	}

	/**
	 * The method is updated in Release 7. Added a parameter for showing approved modification.
	 * The Method will fetch the budget details of Seasonal Employees grid of
	 * Personnel Services tab under contract budget module
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @param aoMasterBean - MasterBean object
	 * @return loSalariedEmployess List of PersonnelServiceBudget bean
	 * @throws ApplicationException - ApplicationException object
	 * 
	 */

	public List<PersonnelServiceBudget> fetchSeasonalEmployeeBudgetForModification(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget, MasterBean aoMasterBean) throws ApplicationException
	{

		List<PersonnelServiceBudget> loSalariedEmployess = null;
		ContractBudgetService loCBService = new ContractBudgetService();
		try
		{
			// R7 start Fetch the data from FileNet XML if Budget Status is approved
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			if (aoMasterBean != null && aoPersonnelServiceBudget.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoPersonnelServiceBudget.getSubBudgetID();
				loSalariedEmployess = new ContractBudgetAmendmentService().fetchSeasonalEmployeeFromXML(lsSubBudgetId,
						aoMasterBean);
			}
			else
			{
				// R7 end
				// Fetching list of records for modification budget
				List<PersonnelServiceBudget> loSalariedEmployessForModification = fetchSeasonalEmployeeForModification(
						aoMybatisSession, aoPersonnelServiceBudget);

				// Fetching list of records for base budget
				CBGridBean loCBGridBeanObj = getCBGridBeanForBaseBudget(aoPersonnelServiceBudget);

				loSalariedEmployess = loCBService.fetchSeasonalEmployeeForBase(aoMybatisSession, loCBGridBeanObj);

				setModificationBudgetDetailsinBean(loSalariedEmployessForModification, loSalariedEmployess);
			}
		}
		// catch any application exception thrown from the code due to SELECT
		// statement and throw it
		// forward
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData(
					"ApplicationException occured while fetching Seasonal Employee budget :  fetchSeasonalEmployeeBudgetForModification",
					aoExp);
			LOG_OBJECT
					.Error("ApplicationException occured while fetching Seasonal Employee budget : fetchSeasonalEmployeeBudgetForModification "
							+ aoExp);
			setMoState("ApplicationException occured while fetching Seasonal Employee budget for budget id = "
					+ aoPersonnelServiceBudget.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudget.getSubBudgetID());
			throw aoExp;
		}
		// Catch any Null pointer exception thrown from the code and wrap it
		// into application
		// Exception and throw it forward
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while fetching Seasonal Emplyee budget :  fetchSeasonalEmployeeBudgetForModification ",
					aoExp);
			loAppEx.addContextData(
					"Exception occured while fetching Seasonal Emplyee budget :  fetchSeasonalEmployeeBudgetForModification",
					aoExp);
			LOG_OBJECT
					.Error("Exception occured while fetching Seasonal Employee budget : fetchSeasonalEmployeeBudgetForModification "
							+ aoExp);
			setMoState("Exception occured while fetching Seasonal Employee budget for budget id = "
					+ aoPersonnelServiceBudget.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudget.getSubBudgetID());
			throw loAppEx;
		}
		return loSalariedEmployess;
	}

	/**
	 * The method is updated in Release 7. Added a parameter for showing approved modification.
	 * The Method will fetch the fringe benefit details of Personnel Services
	 * tab under contract budget module
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @param aoMasterBean - MasterBean object
	 * @return loSalariedEmployess List of PersonnelServiceBudget bean
	 * @throws ApplicationException - ApplicationException object
	 * 
	 */

	public List<PersonnelServiceBudget> fetchFringeBenifitsForModification(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget, MasterBean aoMasterBean) throws ApplicationException
	{

		List<PersonnelServiceBudget> loSalariedEmployess = new ArrayList<PersonnelServiceBudget>();
		ContractBudgetService loCBService = new ContractBudgetService();

		try
		{
			// Release 7 start Fetch the data from FileNet XML if Budget Status is approved
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			if (aoMasterBean != null && aoPersonnelServiceBudget.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoPersonnelServiceBudget.getSubBudgetID();
				loSalariedEmployess = new ContractBudgetAmendmentService().fetchFringeEmployeeFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{
				// R7 end
				// Fetching list of records for modification budget
				List<PersonnelServiceBudget> loSalariedEmployessForModification = fetchFringeBenefitsForModification(
						aoMybatisSession, aoPersonnelServiceBudget);

				// Fetching list of records for base budget
				CBGridBean loCBGridBeanObj = getCBGridBeanForBaseBudget(aoPersonnelServiceBudget);

				if (aoPersonnelServiceBudget.getParentSubBudgetId() != null
						&& aoPersonnelServiceBudget.getSubBudgetID() != null
						&& !aoPersonnelServiceBudget.getParentSubBudgetId().equalsIgnoreCase(
								aoPersonnelServiceBudget.getSubBudgetID()))
				{
					loSalariedEmployess = loCBService.fetchFringeBenifits(aoMybatisSession, loCBGridBeanObj);
				}

				setModificationBudgetDetailsinBean(loSalariedEmployessForModification, loSalariedEmployess);
			}

		}
		// catch any application exception thrown from the code due to SELECT
		// statement and throw it
		// forward
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData(
					"ApplicationException occured while fetching fringe benifits budget :  fetchFringeBenifitsForModification",
					aoExp);
			LOG_OBJECT
					.Error("ApplicationException occured while fetching fringe benifits budget : fetchFringeBenifitsForModification "
							+ aoExp);
			setMoState("ApplicationException occured while fetching fringe benifits budget for budget id = "
					+ aoPersonnelServiceBudget.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudget.getSubBudgetID());
			throw aoExp;
		}
		// Catch any Null pointer exception thrown from the code and wrap it
		// into application
		// Exception and throw it forward
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while fetching fringe benifits budget :  fetchFringeBenifitsForModification ",
					aoExp);
			loAppEx.addContextData(
					"Exception occured while fetching fringe benifits budget :  fetchFringeBenifitsForModification",
					aoExp);
			LOG_OBJECT
					.Error("Exception occured while fetching fringe benifits budget : fetchFringeBenifitsForModification "
							+ aoExp);
			setMoState("Exception occured while fetching fringe benifits budget for budget id = "
					+ aoPersonnelServiceBudget.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudget.getSubBudgetID());
			throw loAppEx;
		}
		return loSalariedEmployess;
	}

	/**
	 * The Method will add budget details of Salaried Employees/Hourly
	 * Employee/Seasoned Employee of Personnel Services tab under contract
	 * budget module depending upon type(Salaried/Hourly/Seasonal) and
	 * position(Project Manager, Business Analyst, Dancer).Mapper file being
	 * used is ContractBudgetMapper.xml. <li>Query used :
	 * insertPersonnelServicesModification</li>
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget bean
	 * @return lbStatus Insert status
	 * @throws ApplicationException - ApplicationException object
	 * 
	 */

	public Boolean addEmployeeBudgetForModification(SqlSession aoMybatisSession,
			PersonnelServiceBudget aoPersonnelServiceBudgetBean) throws ApplicationException
	{

		boolean lbInsertStatus = false;
		boolean lbUnitError = false;
		boolean lbAmountError = false;
		try
		{
			// Start: Added for Defect-8478
			if (HHSConstants.ZERO.equals(aoPersonnelServiceBudgetBean.getUsesFte())
					&& aoPersonnelServiceBudgetBean.getId().equals(HHSConstants.NEW_ROW_IDENTIFIER)
					&& new BigDecimal(aoPersonnelServiceBudgetBean.getModificationUnit()).compareTo(BigDecimal.ZERO) <= HHSConstants.INT_ZERO)
			{
				lbUnitError = true;
				setPositionErrorMessage(aoPersonnelServiceBudgetBean);
			}
			// End: Added for Defect-8478
			lbUnitError = validateModificationUnitForPersonnelServices(aoMybatisSession, aoPersonnelServiceBudgetBean);
			if (lbUnitError)
			{
				lbAmountError = validateModificationAmountForPersonnelServices(aoMybatisSession,
						aoPersonnelServiceBudgetBean);
			}
			if (lbUnitError && lbAmountError)
			{

				String lsTransactionName = aoPersonnelServiceBudgetBean.getTransactionName();
				setEmployeeTypeInBean(aoPersonnelServiceBudgetBean, lsTransactionName);

				DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBY_INSERT_PERSONNEL_SERVICES_MODIFICATION, HHSConstants.PERSONNEL_SERVICE_BUDGET);
				lbInsertStatus = true;
			}
			else
			{
				if (!lbUnitError)
				{
					lbUnitError = true;
					// Start : Updated in R6
					String lsMsg = fetchValidationMessage(aoPersonnelServiceBudgetBean.getTransactionName(),
							aoPersonnelServiceBudgetBean.getUsesFte());
					// End : Updated in R6
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, lsMsg));
				}
				else if (!lbAmountError)
				{
					lbUnitError = true;
					throw new ApplicationException(
							PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									HHSConstants.CBM_LESS_THAN_INVOICE_FOR_NEG_AMEND));
				}
			}

		}
		// catch any application exception thrown from the code due to INSERT
		// statement failure and throw it
		// forward
		catch (ApplicationException aoExp)
		{
			if (lbUnitError)
			{
				aoExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoExp.toString());
				LOG_OBJECT
						.Error("Entered value would cause the Units to fall below zero in the Contract Budget. Please enter a new value. : addEmployeeBudgetForModification "
								+ aoExp);
				setMoState("Entered value would cause the Units to fall below zero in the Contract Budget. Please enter a new value.");
			}
			else if (lbAmountError)
			{
				aoExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoExp.toString());
				LOG_OBJECT
						.Error("Entered value would cause the Proposed Budget to fall below the amount already invoiced for the line item. Please enter a new value. : addEmployeeBudgetForModification "
								+ aoExp);
				setMoState("Entered value would cause the Proposed Budget to fall below the amount already invoiced for the line item. Please enter a new value.");
			}
			else
			{
				aoExp.addContextData(
						"ApplicationException occured while inserting Emplyee budget :  addEmployeeBudgetForModification",
						aoExp);
				LOG_OBJECT
						.Error("ApplicationException occured while inserting Emplyee budget : addEmployeeBudgetForModification "
								+ aoExp);
				setMoState("ApplicationException occured while inserting Emplyee budget for budget id = "
						+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
						+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			}
			throw aoExp;
		}
		// Catch any Null pointer exception thrown from the code and wrap it
		// into application
		// Exception and throw it forward
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while inserting Emplyee budget :  addEmployeeBudgetForModification ", aoExp);
			loAppEx.addContextData(
					"Exception occured while inserting Emplyee budget :  addEmployeeBudgetForModification", aoExp);
			LOG_OBJECT.Error("Exception occured while inserting Emplyee budget : addEmployeeBudgetForModification ",
					aoExp);
			setMoState("Exception occured while inserting Emplyee budget for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loAppEx;
		}
		return lbInsertStatus;
	}

	/**
	 * The Method will edit budget details record of Salaried Employees/Hourly
	 * Employee/Seasoned Employee of Personnel Services tab under contract
	 * budget module. Mapper file being used is ContractBudgetMapper.xml.
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget bean
	 * @return lbEditStatus Edit status
	 * @throws ApplicationException - ApplicationException object
	 * 
	 */

	public Boolean editEmployeeBudgetForModification(SqlSession aoMybatisSession,
			PersonnelServiceBudget aoPersonnelServiceBudgetBean) throws ApplicationException
	{
		boolean lbEditStatus = false;
		boolean lbUnitError = false;
		boolean lbAmountError = false;
		try
		{
			// Start: Added for Defect-8478
			if (HHSConstants.ZERO.equals(aoPersonnelServiceBudgetBean.getUsesFte())
					&& aoPersonnelServiceBudgetBean.getNewRecord().endsWith(HHSConstants.NEW_RECORD)
					&& new BigDecimal(aoPersonnelServiceBudgetBean.getModificationUnit()).compareTo(BigDecimal.ZERO) <= HHSConstants.INT_ZERO)
			{
				lbUnitError = true;
				setPositionErrorMessage(aoPersonnelServiceBudgetBean);
			}
			// End: Added for Defect-8478

			lbUnitError = validateModificationUnitForPersonnelServices(aoMybatisSession, aoPersonnelServiceBudgetBean);
			if (lbUnitError)
			{
				lbAmountError = validateModificationAmountForPersonnelServices(aoMybatisSession,
						aoPersonnelServiceBudgetBean);
			}
			if (lbUnitError && lbAmountError)
			{
				lbEditStatus = updateInsertPersonnelServices(aoMybatisSession, aoPersonnelServiceBudgetBean);
			}
			else
			{
				if (!lbUnitError)
				{
					lbUnitError = true;
					// Start : Updated in R6
					String lsMsg = fetchValidationMessage(aoPersonnelServiceBudgetBean.getTransactionName(),
							aoPersonnelServiceBudgetBean.getUsesFte());
					// End : Updated in R6
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, lsMsg));
				}
				else if (!lbAmountError)
				{
					lbUnitError = true;
					throw new ApplicationException(
							PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									HHSConstants.CBM_LESS_THAN_INVOICE_FOR_NEG_AMEND));
				}
			}
		}
		// catch any application exception thrown from the code due to
		// UPDATE/INSERT statement failure and throw it forward
		catch (ApplicationException aoExp)
		{
			if (lbUnitError)
			{
				aoExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoExp.toString());
				LOG_OBJECT
						.Error("Entered value would cause the Units to fall below zero in the Contract Budget. Please enter a new value. : editEmployeeBudgetForModification "
								+ aoExp);
				setMoState("Entered value would cause the Units to fall below zero in the Contract Budget. Please enter a new value.");
			}
			else if (lbAmountError)
			{
				aoExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoExp.toString());
				LOG_OBJECT
						.Error("Entered value would cause the Proposed Budget to fall below the amount already invoiced for the line item. Please enter a new value. : editEmployeeBudgetForModification "
								+ aoExp);
				setMoState("Entered value would cause the Proposed Budget to fall below the amount already invoiced for the line item. Please enter a new value.");
			}
			else
			{
				aoExp.addContextData(
						"ApplicationException occured while editing Emplyee budget :  editEmployeeBudgetForModification",
						aoExp);
				LOG_OBJECT
						.Error("ApplicationException occured while editing Emplyee budget : editEmployeeBudgetForModification "
								+ aoExp);
				setMoState("ApplicationException occured while editing Emplyee budget for budget id = "
						+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
						+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			}
			throw aoExp;
		}
		// Catch any Null pointer exception thrown from the code and wrap it
		// into application Exception and throw it forward
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while editing Emplyee budget :  editEmployeeBudgetForModification ", aoExp);
			loAppEx.addContextData(
					"Exception occured while editing Emplyee budget :  editEmployeeBudgetForModification", aoExp);
			LOG_OBJECT.Error("Exception occured while editing Emplyee budget : editEmployeeBudgetForModification ",
					aoExp);
			setMoState("Exception occured while editing Emplyee budget for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loAppEx;
		}
		return lbEditStatus;
	}

	/**
	 * This method set the error message for Position column.
	 * 
	 * <li>Added for Defect-8478</li>
	 * @param aoPersonnelServiceBudgetBean
	 * @throws ApplicationException
	 */
	private void setPositionErrorMessage(PersonnelServiceBudget aoPersonnelServiceBudgetBean)
			throws ApplicationException
	{
		String lsMessage = MessageFormat
				.format(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSR5Constants.POSITION_NEW_ROW_ZERO),
						(aoPersonnelServiceBudgetBean.getBudgetTypeId().equals(HHSConstants.THREE) ? HHSR5Constants.BUDGET_MODIFICATION
								: HHSR5Constants.BUDGET_UPDATE));
		throw new ApplicationException(lsMessage);
	}

	/**
	 * This method updates the personal services.
	 * 
	 * <li>Query used : updatePersonnelServicesForModification</li> <li>Query
	 * used : insertFirstPersonnelServicesForModification</li>
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget object
	 * @return boolean
	 * @throws ApplicationException ApplicationException object
	 */
	private boolean updateInsertPersonnelServices(SqlSession aoMybatisSession,
			PersonnelServiceBudget aoPersonnelServiceBudgetBean) throws ApplicationException
	{
		boolean lbEditStatus;
		Integer loNoOfUpdateRows = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
				HHSConstants.CBY_UPDATE_PERSONNEL_SERVICES_FOR_MODIFICATION, HHSConstants.PERSONNEL_SERVICE_BUDGET);
		if (loNoOfUpdateRows <= HHSConstants.INT_ZERO)
		{
			DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_INSERT_FIRST_PERSONNEL_SERVICES_FOR_MODIFICATION,
					HHSConstants.PERSONNEL_SERVICE_BUDGET);
		}
		lbEditStatus = true;
		return lbEditStatus;
	}

	/**
	 * The Method will edit the fringe benefit details of Personnel Services tab
	 * under contract budget module
	 * 
	 * <li>Query used : updateFringeBenifitsForModification</li> <li>Query used
	 * : insertFirstFringeBenefitsForModification</li>
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget
	 * @return lbEditStatus Edit status
	 * @throws ApplicationException - ApplicationException object
	 * 
	 */

	public Boolean editFringeBenifitsForModification(SqlSession aoMybatisSession,
			PersonnelServiceBudget aoPersonnelServiceBudgetBean) throws ApplicationException
	{
		boolean lbEditStatus = false;
		Integer loFringeCount = 0;
		boolean lbAmountError = true;
		try
		{
			// If the modification amount is less than 0, then only do the
			// validation
			if ((new BigDecimal(aoPersonnelServiceBudgetBean.getModificationAmount())).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO)
			{
				lbAmountError = validateModificationAmountForFringe(aoMybatisSession, aoPersonnelServiceBudgetBean);
			}
			if (lbAmountError)
			{
				loFringeCount = (Integer) DAOUtil
						.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
								HHSConstants.CBY_UPDATE_FRINGE_BENIFITS_FOR_MODIFICATION,
								HHSConstants.PERSONNEL_SERVICE_BUDGET);
				if (loFringeCount <= HHSConstants.INT_ZERO)
				{
					DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBY_INSERT_FIRST_FRINGE_BENEFITS_FOR_MODIFICATION,
							HHSConstants.PERSONNEL_SERVICE_BUDGET);
				}
				lbEditStatus = true;
			}
			else
			{
				if (!lbAmountError)
				{
					lbAmountError = true;
					throw new ApplicationException(
							PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									HHSConstants.CBM_LESS_THAN_INVOICE_FOR_NEG_AMEND));
				}
			}
		}
		// catch any application exception thrown from the code due to
		// UPDATE/INSERT statement failure and throw it
		// forward
		catch (ApplicationException aoExp)
		{
			if (lbAmountError)
			{
				aoExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoExp.toString());
				LOG_OBJECT
						.Error("Entered value would cause the Proposed Budget to fall below the amount already invoiced for the line item. Please enter a new value. : editFringeBenifitsForModification "
								+ aoExp);
				setMoState("Entered value would cause the Proposed Budget to fall below the amount already invoiced for the line item. Please enter a new value.");
			}
			else
			{
				aoExp.addContextData(
						"ApplicationException occured while editing Fringe Benifits:  editFringeBenifitsForModification",
						aoExp);
				LOG_OBJECT
						.Error("ApplicationException occured while editing Fringe Benifits : editFringeBenifitsForModification "
								+ aoExp);
				setMoState("ApplicationException occured while editing Fringe Benifits for budget id = "
						+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
						+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			}
			throw aoExp;
		}
		// Catch any Null pointer/NumberFormat exception thrown from the code
		// and wrap it into application
		// Exception and throw it forward
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while editing Fringe Benifits :  editFringeBenifitsForModification ", aoExp);
			loAppEx.addContextData(
					"Exception occured while editing Fringe Benifits :  editFringeBenifitsForModification", aoExp);
			LOG_OBJECT.Error("Exception occured while editing Fringe Benifits : editFringeBenifitsForModification ",
					aoExp);
			setMoState("Exception occured while editing Fringe Benifits for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loAppEx;
		}
		return lbEditStatus;
	}

	/**
	 * This method is used to fetch modification details of salaried employee
	 * 
	 * <li>Query used : fetchSalriedEmployeeForModification</li>
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @return loSalariedEmployessForModification List<PersonnelServiceBudget>
	 * @throws Exception object
	 */

	@SuppressWarnings("unchecked")
	private List<PersonnelServiceBudget> fetchSalariedEmployeeForModification(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget) throws Exception
	{
		List<PersonnelServiceBudget> loSalariedEmployessForModification = (List<PersonnelServiceBudget>) DAOUtil
				.masterDAO(aoMybatisSession, aoPersonnelServiceBudget,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBY_FETCH_SALRIED_EMPLOYEE_FOR_MODIFICATION,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

		return loSalariedEmployessForModification;

	}

	/**
	 * This method is used to fetch modification details of hourly employee
	 * 
	 * <li>Query used : fetchHourlyEmployeeForModification</li>
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @return loHourlyEmployessForModification List<PersonnelServiceBudget>
	 * @throws Exception object
	 */

	@SuppressWarnings("unchecked")
	private List<PersonnelServiceBudget> fetchHourlyEmployeeForModification(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget) throws Exception
	{
		List<PersonnelServiceBudget> loHourlyEmployessForModification = (List<PersonnelServiceBudget>) DAOUtil
				.masterDAO(aoMybatisSession, aoPersonnelServiceBudget,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBY_FETCH_HOURLY_EMPLOYEE_FOR_MODIFICATION,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

		return loHourlyEmployessForModification;

	}

	/**
	 * This method is used to fetch modification details of seasonal employee
	 * 
	 * <li>Query used : fetchSeasonalEmployeeForModification</li>
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @return loSeasonalEmployessForModification List<PersonnelServiceBudget>
	 * @throws Exception object
	 */

	@SuppressWarnings("unchecked")
	private List<PersonnelServiceBudget> fetchSeasonalEmployeeForModification(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget) throws Exception
	{
		List<PersonnelServiceBudget> loSeasonalEmployessForModification = (List<PersonnelServiceBudget>) DAOUtil
				.masterDAO(aoMybatisSession, aoPersonnelServiceBudget,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBY_FETCH_SEASONAL_EMPLOYEE_FOR_MODIFICATION,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

		return loSeasonalEmployessForModification;

	}

	/**
	 * This method is used to fetch modification details of Fringe Benefits
	 * 
	 * <li>Query used : fetchFringeBenefitsForModification</li>
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @return loFringeBenefitsForModification List<PersonnelServiceBudget>
	 * @throws Exception object
	 */

	@SuppressWarnings("unchecked")
	private List<PersonnelServiceBudget> fetchFringeBenefitsForModification(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget) throws Exception
	{
		List<PersonnelServiceBudget> loFringeBenefitsForModification = (List<PersonnelServiceBudget>) DAOUtil
				.masterDAO(aoMybatisSession, aoPersonnelServiceBudget,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBY_FETCH_FRINGE_BENEFITS_FOR_MODIFICATION,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

		return loFringeBenefitsForModification;

	}

	/**
	 * This method is used to set modification details (units, amount) in
	 * SalariedEmployeesList <li>1. For each record of aoModificationList, if
	 * parentid equalls to id of any record of aoSalariedEmployessList,
	 * modification unit and modification amount of first list will be set to
	 * second list.
	 * @param aoModificationList CBGridBean
	 * @param aoSalariedEmployessList CBGridBean
	 * @throws Exception object
	 */
	private void setModificationBudgetDetailsinBean(List<PersonnelServiceBudget> aoModificationList,
			List<PersonnelServiceBudget> aoSalariedEmployessList) throws Exception
	{
		if (aoModificationList != null && !aoModificationList.isEmpty())
		{
			for (PersonnelServiceBudget loPsBase : aoSalariedEmployessList)
			{
				for (PersonnelServiceBudget loPsModification : aoModificationList)
				{
					if (loPsBase.getId().equals(loPsModification.getParentId()))
					{
						loPsBase.setModificationUnit(loPsModification.getModificationUnit());
						loPsBase.setModificationAmount(loPsModification.getModificationAmount());
						break;
					}

				}
			}

			for (PersonnelServiceBudget loPsModification : aoModificationList)
			{
				if (loPsModification.getId().equals(loPsModification.getParentId()))
				{
					loPsModification.setId(loPsModification.getId() + HHSConstants.NEW_RECORD);
					aoSalariedEmployessList.add(HHSConstants.INT_ZERO, loPsModification);

				}

			}
		}
	}

	/**
	 * This method is used to set Employee type into the Personnel Service bean
	 * on the basis of transaction name.
	 * <ul>
	 * <li>If transaction name is "salariedEmployeeGridAdd", set Emp Type as "1"
	 * </li>
	 * <li>If transaction name is "hourlyEmployeeGridAdd", set Emp Type as "2"</li>
	 * <li>If transaction name is "seasonalEmployeeGridAdd", set Emp Type as "3"
	 * </li>
	 * </ul>
	 * 
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget object
	 * @param asTransactionName String as input
	 */

	private void setEmployeeTypeInBean(PersonnelServiceBudget aoPersonnelServiceBudgetBean, String asTransactionName)
	{
		if (asTransactionName.equals(HHSConstants.CBY_MOD_SALARIED_EMPLOYEE_GRID_ADD))
		{
			aoPersonnelServiceBudgetBean.setEmpType(HHSConstants.ONE);
		}
		else if (asTransactionName.equals(HHSConstants.CBY_MOD_HOURLY_EMPLOYEE_GRID_ADD))
		{
			aoPersonnelServiceBudgetBean.setEmpType(HHSConstants.TWO);
		}
		else if (asTransactionName.equals(HHSConstants.CBY_MOD_SEASONAL_EMPLOYEE_GRID_ADD))
		{
			aoPersonnelServiceBudgetBean.setEmpType(HHSConstants.THREE);
		}
	}

	// S365 screen otps modification start
	/**
	 *  The method is updated in Release 7. Added a parameter for showing approved modification.
	 * <p>
	 * This method is used to fetch operation and support page data(not part of
	 * grid) for a particular sub-budget based upon budget type as below: 1 =
	 * Budget Amendment; 2 = Contract Budget; 3 = Budget Modification; 4 =
	 * Budget Update <br/>
	 * <ul>
	 * <li>CBOperationSupportBean is used to populate values in grid</li>
	 * <li>Provider is able to fetch existing equipment details:</li>
	 * <li>1.the Amendment amount</li>
	 * <li>2.the FY Budget amount</li>
	 * <li>3.the Modification amount</li>
	 * <li>3.the Updated amount</li>
	 * <li>Query used : fetchOpAndSupportModPageData</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBGridBeanObj - CBGridBean object containing key fields
	 * @param aoMyBatisSession - SqlSession object
	 * @return List<CBOperationSupportBean> - returns list of bean of type
	 *         <CBOperationSupportBean>
	 * @throws ApplicationException - ApplicationException object
	 */
	public CBOperationSupportBean fetchOpAndSupportModPageData(CBGridBean aoCBGridBeanObj, SqlSession aoMyBatisSession,
			MasterBean aoMasterBean) throws ApplicationException
	{
		CBOperationSupportBean loCBOperationSupportBean = null;
		try
		{
			//Start Release 7::: Fetch the data from FileNet XML if Budget Status is approved
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBOperationSupportBean = new ContractBudgetAmendmentService().fetchOpAndSupportAmendPageDataFromXML(
						lsSubBudgetId, aoMasterBean);
			}
			else
			{
				// Release 7 end
				loCBOperationSupportBean = (CBOperationSupportBean) DAOUtil.masterDAO(aoMyBatisSession,
						aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_OPERATION_SUPP_MOD_PAGE_DATA, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				setMoState("ContractBudgetModificationService: fetchOpAndSupportModPageData() passed");
			}
		}
		// Exception occur if database is down or getting null in required
		// parameter
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.CB_GRID_BEAN_OBJ, CommonUtil.convertBeanToString(aoCBGridBeanObj));
			LOG_OBJECT.Error("App Exception occured while retrieving in ContractBudgetModificationService ", aoAppEx);
			setMoState("ContractBudgetModificationService: fetchOpAndSupportModPageData() failed to fetch "
					+ "with details aoCBGridBeanObj:" + aoCBGridBeanObj + " \n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetModificationService: fetchOpAndSupportModPageData method:: ", aoEx);
			LOG_OBJECT.Error("Exception occured while retrieving in ContractBudgetModificationService ", loAppEx);
			setMoState("ContractBudgetModificationService: fetchOpAndSupportModPageData() failed to fetch "
					+ "with details aoCBGridBeanObj:" + aoCBGridBeanObj + " \n");
			throw loAppEx;
		}
		return loCBOperationSupportBean;
	}

	/**
	 * <p>
	 * The method is updated in Release 7. Added a parameter for showing approved modification.
	 * This method retrieves all details for operation and support grid in
	 * contract budget modification
	 * </p>
	 * <ul>
	 * <li>1.Fetch FY budget amount for operation and support in
	 * List<CBOperationSupportBean></li>
	 * <li>2.Fetch Modification amount for operation and support in
	 * List<CBOperationSupportBean></li>
	 * <li>3.Merge both list into one of type List<CBOperationSupportBean></li>
	 * <li>Query used : fetchOperationAndSupportModDetails</li>
	 * <li>Query used : fetchOperationAndSupportModDetailsParentEqualSub</li>
	 * <li>Query used : fetchOperationAndSupportModAmtDetails</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Sql session object
	 * @param aoCBGridBeanObj CBGridBeanObj object
	 * @param aoMasterBean - MasterBean object
	 * @return List<CBOperationSupportBean> CBOperationSupportBean list
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<CBOperationSupportBean> fetchModificationOTPS(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj,
			MasterBean aoMasterBean)
			throws ApplicationException
	{
		List<CBOperationSupportBean> loCBOPModList = null;
		List<CBOperationSupportBean> loCBOperationSupportBeanList = null;
		try
		{
			// R7 changes Start ::: Fetch the data from FileNet XML if Budget Status is approved
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBOperationSupportBeanList = new ContractBudgetAmendmentService().fetchOTPSFromXML(lsSubBudgetId,
						aoMasterBean);
			}
			else
			{
				// R7 changes end
				if (aoCBGridBeanObj.getParentSubBudgetId() != null && aoCBGridBeanObj.getSubBudgetID() != null
						&& !aoCBGridBeanObj.getParentSubBudgetId().equalsIgnoreCase(aoCBGridBeanObj.getSubBudgetID()))
				{
					loCBOperationSupportBeanList = (List<CBOperationSupportBean>) DAOUtil.masterDAO(aoMybatisSession,
							aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.FETCH_OPERATION_AND_SUPPORT_MOD_DETAILS,
							HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				}
				else
				{
					loCBOperationSupportBeanList = (List<CBOperationSupportBean>) DAOUtil.masterDAO(aoMybatisSession,
							aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.FETCH_OPERATION_AND_SUPPORT_MOD_DETAILS_PARENT_EQUAL_SUB,
							HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				}
				loCBOPModList = (List<CBOperationSupportBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_OPERATION_AND_SUPPORT_MOD_AMT_DETAILS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

				// set modification amounts in OP Bean
				for (int liCount = HHSConstants.INT_ZERO; liCount < loCBOperationSupportBeanList.size(); liCount++)
				{
					String lsID = loCBOperationSupportBeanList.get(liCount).getId();
					String lsModID = loCBOPModList.get(liCount).getId();
					if (lsModID != null && lsID.equalsIgnoreCase(lsModID))
					{
						loCBOperationSupportBeanList.get(liCount).setModificationAmt(
								loCBOPModList.get(liCount).getModificationAmt());
					}
				}
				setMoState("ContractBudgetModificationService: fetchModificationOTPS() passed");
			}
		}
		// Exception occur if database is down or getting null in required
		// parameter
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.CB_GRID_BEAN_OBJ, CommonUtil.convertBeanToString(aoCBGridBeanObj));
			LOG_OBJECT.Error("App Exception occured while retrieving in ContractBudgetModificationService ", aoAppEx);
			setMoState("ContractBudgetModificationService: fetchModificationOTPS() failed to fetch "
					+ "with details aoCBGridBeanObj:" + aoCBGridBeanObj + " \n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetModificationService: fetchModificationOTPS method:: ", aoEx);
			LOG_OBJECT.Error("Exception occured while retrieving in ContractBudgetModificationService ", loAppEx);
			setMoState("ContractBudgetModificationService: fetchModificationOTPS() failed to fetch "
					+ "with details aoCBGridBeanObj:" + aoCBGridBeanObj + " \n");
			throw loAppEx;
		}
		return loCBOperationSupportBeanList;
	}

	/**
	 * The method is updated in Release 7. Added a parameter for showing approved modification.
	 * <p>
	 * This method retrieves all details for equipment grid in OTPS contract
	 * budget modification
	 * </p>
	 * <ul>
	 * <li>1.Fetch FY budget amount for Equipment OTPS in List<CBEquipmentBean></li>
	 * <li>2.Fetch Modification amount for Equipment OTPS in
	 * List<CBEquipmentBean></li>
	 * <li>3.Merge both list into one of type List<CBEquipmentBean></li>
	 * <li>Query used : fetchEquipmentModDetails</li>
	 * <li>Query used : fetchEquipmentModAmtDetails</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Sql session object
	 * @param aoCBGridBeanObj CBGridBeanObj object
	 * @return List<CBEquipmentBean> CBEquipmentBean list
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<CBEquipmentBean> fetchModificationEquipment(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj,
			MasterBean aoMasterBean) throws ApplicationException
	{
		List<CBEquipmentBean> loCBOPModList = null;
		List<CBEquipmentBean> loCBEquipmentBeanList = null;
		try
		{
			// Start R7 changes ::Fetch the data from FileNet XML if Budget Status is approved
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			// Fetch the data from FileNet XML if Budget Status is approved
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBEquipmentBeanList = new ContractBudgetAmendmentService().fetchEquipmentFromXML(lsSubBudgetId,
						aoMasterBean);
			}
			else
			{
				// R7 end
				loCBEquipmentBeanList = (List<CBEquipmentBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_EQUIPMENT_MOD_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

				loCBOPModList = (List<CBEquipmentBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_EQUIPMENT_MOD_AMT_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

				mergeModificationList(loCBEquipmentBeanList, loCBOPModList);
				setMoState("ContractBudgetModificationService: fetchModificationEquipment() passed");
			}
		}
		// Exception occur if database is down or getting null in required
		// parameter
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.CB_GRID_BEAN_OBJ, CommonUtil.convertBeanToString(aoCBGridBeanObj));
			LOG_OBJECT.Error("App Exception occured while retrieving in ContractBudgetModificationService ", aoAppEx);
			setMoState("ContractBudgetModificationService: fetchModificationEquipment() failed to fetch "
					+ "with details aoCBGridBeanObj:" + aoCBGridBeanObj + " \n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetModificationService: fetchModificationEquipment method:: ", aoEx);
			LOG_OBJECT.Error("Exception occured while retrieving in ContractBudgetModificationService ", loAppEx);
			setMoState("ContractBudgetModificationService: fetchModificationEquipment() failed to fetch "
					+ "with details aoCBGridBeanObj:" + aoCBGridBeanObj + " \n");
			throw loAppEx;
		}
		return loCBEquipmentBeanList;
	}

	/**
	 * <p>
	 * This is a private method called for otps modification to merge FY budget
	 * amount and modification amount
	 * </p>
	 * <ul>
	 * <li>It reads record id and if it is new record then add to screen
	 * population list at index zero for sorting on created date</li>
	 * <li>Else update amount in original list</li>
	 * </ul>
	 * @param aoCBEquipmentBeanList List<CBEquipmentBean> object with FY budget
	 *            amount
	 * @param aoCBOPModList List<CBEquipmentBean> object with modification
	 *            amount
	 * @throws Exception Exception thrown in case of any application code
	 *             failure
	 */
	private void mergeModificationList(List<CBEquipmentBean> aoCBEquipmentBeanList, List<CBEquipmentBean> aoCBOPModList)
			throws Exception
	{
		for (CBEquipmentBean loCBEquipmentBean : aoCBOPModList)
		{
			if (loCBEquipmentBean.getId().toLowerCase().contains(HHSConstants.NEW_RECORD))
			{
				aoCBEquipmentBeanList.add(HHSConstants.INT_ZERO, loCBEquipmentBean);
			}
			else
			{
				updateModifiedAmtInEquipment(aoCBEquipmentBeanList, loCBEquipmentBean);
			}
		}
	}

	/**
	 * <p>
	 * This is a private method called for OTPS modification to update
	 * modification amount
	 * </p>
	 * <ul>
	 * <li>It reads all original equipment bean list and if passed id is equal
	 * then it updates amount in CBEquipmentBean and comes out of loop</li>
	 * </ul>
	 * @param aoCBEquipmentBeanList List<CBEquipmentBean> object with FY budget
	 *            amount
	 * @param aoCBEquipmentBean CBEquipmentBean object with modification amount
	 * @throws Exception Exception thrown in case of any application code
	 *             failure
	 */
	private void updateModifiedAmtInEquipment(List<CBEquipmentBean> aoCBEquipmentBeanList,
			CBEquipmentBean aoCBEquipmentBean) throws Exception
	{
		for (CBEquipmentBean loOrigEquipBean : aoCBEquipmentBeanList)
		{
			if (aoCBEquipmentBean.getId().equalsIgnoreCase(loOrigEquipBean.getId()))
			{
				loOrigEquipBean.setModificationAmt(aoCBEquipmentBean.getModificationAmt());
				loOrigEquipBean.setUnits(aoCBEquipmentBean.getUnits());
				break;
			}
		}
	}

	/**
	 * <p>
	 * This method is used for editing/updating values in Operations and Support
	 * grid for a particular sub-budget based upon budget type as below: 1 =
	 * Budget Amendment; 2 = Contract Budget; 3 = Budget Modification; 4 =
	 * Budget Update <br/>
	 * <ul>
	 * <li>CBOperationSupportBean is used to populate values in grid</li>
	 * <li>Provider is able to edit the :</li>
	 * <li>1.amendment amounts for the pre-defined OTPS Operations & Support
	 * line items</li>
	 * <li>2.budget amounts for the pre-defined OTPS Operations & Support line
	 * items</li>
	 * <li>3.Modification amounts for the pre-defined OTPS Operations & Support
	 * line items</li>
	 * <li>4.same as point 3 but in Update, final Total City Funded Budget
	 * <li>Query used : editOperationAndSupportModDetails</li>
	 * Update Amount must be equal to the change in the FY Budget value</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBOperationSupportBean - CBOperationSupportBean object
	 *            containing key fields
	 * @param aoMyBatisSession - SqlSession object
	 * @return Boolean - returns boolean status of edit/update query
	 * @throws ApplicationException - ApplicationException object
	 */
	public Boolean editOperationAndSupportModificationDetails(CBOperationSupportBean aoCBOperationSupportBean,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		boolean lbEditStatus = false;
		boolean lbEditAmtValid = false;
		boolean lbError = false;
		try
		{
			lbEditAmtValid = validateModificationAmountForOTPS(aoMyBatisSession, aoCBOperationSupportBean);

			if (lbEditAmtValid)
			{
				Integer loUpdatedRows = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBOperationSupportBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.EDIT_OPERATION_AND_SUPPORT_MOD_DETAILS,
						HHSConstants.MODEL_CB_OPERATION_SUPPORT_BEAN);

				if (null != loUpdatedRows && loUpdatedRows == HHSConstants.INT_ZERO)
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoCBOperationSupportBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.INSERT_OPERATION_AND_SUPPORT_MOD_DETAILS,
							HHSConstants.MODEL_CB_OPERATION_SUPPORT_BEAN);
				}
			}
			else
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CBM_LESS_THAN_INVOICE_FOR_NEG_AMEND));
			}

			lbEditStatus = true;
			setMoState("ContractBudgetModificationService: editOperationAndSupportDetails() passed");
		}
		// Exception occur if database is down or getting null in required
		// parameter
		catch (ApplicationException aoAppEx)
		{
			if (lbError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
				LOG_OBJECT.Error("Entered value would cause the Proposed Budget to fall below the "
						+ "amount already invoiced for the line item. Please enter a new value ", aoAppEx);
				setMoState("Entered value would cause the Proposed Budget to fall below the"
						+ "amount already invoiced for the line item. Please enter a new value");
			}
			else
			{
				aoAppEx.addContextData(HHSConstants.CB_OPERATION_SUPPORT_BEAN,
						CommonUtil.convertBeanToString(aoCBOperationSupportBean));
				LOG_OBJECT.Error("App Exception occured in ContractBudgetModificationService:"
						+ " editOperationAndSupportDetails method:: ", aoAppEx);
				setMoState("Transaction Failed::App Exception in ContractBudgetModificationService: "
						+ "editOperationAndSupportDetails() method aoCBOperationSupportBean::"
						+ aoCBOperationSupportBean + "\n");
			}
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetModificationService: editOperationAndSupportDetails method:: ", aoEx);
			loAppEx.addContextData(HHSConstants.CB_OPERATION_SUPPORT_BEAN,
					CommonUtil.convertBeanToString(aoCBOperationSupportBean));
			LOG_OBJECT.Error(
					"Exception occured in ContractBudgetModificationService: editOperationAndSupportDetails method:: ",
					loAppEx);
			setMoState("Transaction Failed::Exception in ContractBudgetModificationService: "
					+ "editOperationAndSupportDetails() method aoCBOperationSupportBean::" + aoCBOperationSupportBean
					+ "\n");
			throw loAppEx;
		}
		return lbEditStatus;
	}

	/**
	 * <p>
	 * This method is to add new row in equipment grid in OTPS contract budget
	 * modification
	 * </p>
	 * <ul>
	 * <li>1.Call masterDAO to insert new record entries</li>
	 * <li>2.new record details are put in bean aoCBEquipmentBean</li>
	 * <li>Query used : addEquipmentModificationDetails</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Sql session object
	 * @param aoCBEquipmentBean CBEquipmentBean object
	 * @return Boolean boolean status for insert success/failure
	 * @throws ApplicationException ApplicationException object
	 */
	public Boolean addEquipmentModificationDetails(SqlSession aoMyBatisSession, CBEquipmentBean aoCBEquipmentBean)
			throws ApplicationException
	{
		boolean lbAddStatus = false;
		boolean lbError = false;
		String lsErrorMsgConstant = HHSConstants.EMPTY_STRING;
		try
		{
			if (Double.parseDouble(aoCBEquipmentBean.getUnits()) < HHSConstants.INT_ZERO)
			{
				lbError = true;
				lsErrorMsgConstant = HHSConstants.BUDGET_MODIFICATION_RATE_UNIT_VALIDATION;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						lsErrorMsgConstant));

			}
			else if ((new BigDecimal(aoCBEquipmentBean.getModificationAmt())).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO)
			{
				lbError = true;
				lsErrorMsgConstant = HHSConstants.CBM_LESS_THAN_INVOICE_FOR_NEG_AMEND;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						lsErrorMsgConstant));
			}
			DAOUtil.masterDAO(aoMyBatisSession, aoCBEquipmentBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.ADD_EQUIPMENT_MODIFICATION_DETAILS, HHSConstants.MODEL_CB_EQUIPMENT_BEAN);
			lbAddStatus = true;
			setMoState("ContractBudgetModificationService: addEquipmentModificationDetails() passed");
		}
		// Exception occur if database is down or getting null in required
		// parameter
		catch (ApplicationException aoAppEx)
		{
			if (lbError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
				LOG_OBJECT
						.Error("Entered value would cause either budgeted units/amount to fall below zero." + aoAppEx);
				setMoState("Entered value would cause either budgeted units/amount to fall below zero.");
			}
			else
			{
				aoAppEx.addContextData(HHSConstants.CB_EQUIP_BEAN, CommonUtil.convertBeanToString(aoCBEquipmentBean));
				LOG_OBJECT.Error("App Exception occured in ContractBudgetModificationService: "
						+ "editEquipmentModificationDetails method:: ", aoAppEx);
				setMoState("Transaction Failed::App Exception in ContractBudgetModificationService: "
						+ "addEquipmentModificationDetails() method aoCBEquipmentBean::" + aoCBEquipmentBean + "\n");
			}
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetModificationService: editOperationAndSupportDetails method:: ", aoEx);
			loAppEx.addContextData(HHSConstants.CB_OPERATION_SUPPORT_BEAN,
					CommonUtil.convertBeanToString(aoCBEquipmentBean));
			LOG_OBJECT
					.Error("Exception occured in ContractBudgetModificationService: addEquipmentModificationDetails method:: ",
							loAppEx);
			setMoState("Transaction Failed::Exception in ContractBudgetModificationService: "
					+ "addEquipmentModificationDetails() method aoCBOperationSupportBean::" + aoCBEquipmentBean + "\n");
			throw loAppEx;
		}
		return lbAddStatus;
	}

	/**
	 * <p>
	 * This method is to edit existing row in equipment grid in OTPS contract
	 * budget modification
	 * </p>
	 * <ul>
	 * <li>1.Call masterDAO to edit existing record entry</li>
	 * <li>2.Record details are put in bean aoCBEquipmentBean</li>
	 * <li>Query used : editEquipmentModificationDetails</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Sql session object
	 * @param aoCBEquipmentBean CBEquipmentBean object
	 * @return Boolean boolean status for insert success/failure
	 * @throws ApplicationException ApplicationException object
	 */
	public Boolean editEquipmentModificationDetails(SqlSession aoMyBatisSession, CBEquipmentBean aoCBEquipmentBean)
			throws ApplicationException
	{
		boolean lbEditStatus = false;
		boolean lbModUnitsValid = false;
		boolean lbEditAmtValid = false;
		boolean lbError = false;
		String lsErrorMsgConstant = HHSConstants.EMPTY_STRING;
		try
		{
			lbModUnitsValid = validateModificationUnitForEquipment(aoMyBatisSession, aoCBEquipmentBean);
			lbEditAmtValid = validateModificationAmountForEquipment(aoMyBatisSession, aoCBEquipmentBean);
			if (lbModUnitsValid && lbEditAmtValid)
			{
				Integer loUpdateRows = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBEquipmentBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.EDIT_EQUIPMENT_MODIFICATION_DETAILS, HHSConstants.MODEL_CB_EQUIPMENT_BEAN);
				lbEditStatus = true;

				if (null != loUpdateRows && loUpdateRows == HHSConstants.INT_ZERO)
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoCBEquipmentBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.EDIT_INSERT_EQUIPMENT_MODIFICATION_DETAILS,
							HHSConstants.MODEL_CB_EQUIPMENT_BEAN);
				}
			}
			else
			{
				lbError = true;
				if (!lbModUnitsValid)
				{
					lsErrorMsgConstant = HHSConstants.BUDGET_MODIFICATION_RATE_UNIT_VALIDATION;
				}
				else
				{
					lsErrorMsgConstant = HHSConstants.CBM_LESS_THAN_INVOICE_FOR_NEG_AMEND;
				}
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						lsErrorMsgConstant));
			}
			setMoState("ContractBudgetModificationService: editEquipmentModificationDetails() passed");
		}
		// Exception occur if database is down or getting null in required
		// parameter
		catch (ApplicationException aoAppEx)
		{
			if (lbError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
				LOG_OBJECT
						.Error("Entered value would cause either budgeted units/amount to fall below zero." + aoAppEx);
				setMoState("Entered value would cause either budgeted units/amount to fall below zero.");
			}
			else
			{
				aoAppEx.addContextData(HHSConstants.CB_EQUIP_BEAN, CommonUtil.convertBeanToString(aoCBEquipmentBean));
				LOG_OBJECT.Error("App Exception occured in ContractBudgetModificationService: "
						+ "editEquipmentModificationDetails method:: ", aoAppEx);
				setMoState("Transaction Failed::App Exception in ContractBudgetModificationService: "
						+ "editEquipmentModificationDetails() method aoCBEquipmentBean::" + aoCBEquipmentBean + "\n");
			}
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetModificationService: editEquipmentModificationDetails method:: ", aoEx);
			loAppEx.addContextData(HHSConstants.CB_EQUIP_BEAN, CommonUtil.convertBeanToString(aoCBEquipmentBean));
			LOG_OBJECT
					.Error("Exception occured in ContractBudgetModificationService: editEquipmentModificationDetails method:: ",
							loAppEx);
			setMoState("Transaction Failed::Exception in ContractBudgetModificationService: "
					+ "editEquipmentModificationDetails() method aoCBEquipmentBean::" + aoCBEquipmentBean + "\n");
			throw loAppEx;
		}
		return lbEditStatus;
	}

	/**
	 * <p>
	 * This method is used to delete existing row(which was newly added as part
	 * of contract budget modification) in equipment grid in OTPS contract
	 * budget modification
	 * </p>
	 * <ul>
	 * <li>1.Call masterDAO to delete existing record entry</li>
	 * <li>2.Record details are put in bean aoCBEquipmentBean</li>
	 * <li>Query used : delEquipmentModificationDetails</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Sql session object
	 * @param aoCBEquipmentBean CBEquipmentBean object
	 * @return Boolean boolean status for insert success/failure
	 * @throws ApplicationException ApplicationException object
	 */
	public Boolean delEquipmentModificationDetails(SqlSession aoMyBatisSession, CBEquipmentBean aoCBEquipmentBean)
			throws ApplicationException
	{
		boolean lbEditStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoCBEquipmentBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.DEL_EQUIPMENT_MODIFICATION_DETAILS, HHSConstants.MODEL_CB_EQUIPMENT_BEAN);
			lbEditStatus = true;
			setMoState("ContractBudgetModificationService: delEquipmentModificationDetails() passed");
		}
		// Exception occur if database is down or getting null in required
		// parameter
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.CB_OPERATION_SUPPORT_BEAN,
					CommonUtil.convertBeanToString(aoCBEquipmentBean));
			LOG_OBJECT.Error(
					"App Exception occured in ContractBudgetModificationService: delEquipmentModificationDetails "
							+ "method:: ", aoAppEx);
			setMoState("Transaction Failed::App Exception in ContractBudgetModificationService: "
					+ "delEquipmentModificationDetails() method aoCBOperationSupportBean::" + aoCBEquipmentBean + "\n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetModificationService: delEquipmentModificationDetails method:: ", aoEx);
			loAppEx.addContextData(HHSConstants.CB_OPERATION_SUPPORT_BEAN,
					CommonUtil.convertBeanToString(aoCBEquipmentBean));
			LOG_OBJECT
					.Error("Exception occured in ContractBudgetModificationService: delEquipmentModificationDetails method:: ",
							loAppEx);
			setMoState("Transaction Failed::Exception in ContractBudgetModificationService: "
					+ "delEquipmentModificationDetails() method aoCBOperationSupportBean::" + aoCBEquipmentBean + "\n");
			throw loAppEx;
		}
		return lbEditStatus;
	}

	/**
	 * This method validates the Base Unit count for a particular line-item and
	 * returns FALSE if the #units user is trying to update tries to make the
	 * total #units for a line-item to fall below zero, else it validates it as
	 * TRUE <li>Query used : getBaseUnitForEquipment</li>
	 * @param aoMybatisSession SqlSession object
	 * @param aoCBEquipmentBean CBEquipmentBean object
	 * @return Boolean loModUnitIsValid valid/invalid
	 * @throws ApplicationException ApplicationException object
	 */
	private Boolean validateModificationUnitForEquipment(SqlSession aoMybatisSession, CBEquipmentBean aoCBEquipmentBean)
			throws ApplicationException
	{
		Boolean loModUnitIsValid = true;
		try
		{
			if (Double.parseDouble(aoCBEquipmentBean.getUnits()) < HHSConstants.INT_ZERO)
			{
				Integer loBaseUnit = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoCBEquipmentBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBM_GET_BASE_UNIT_EQUIPMENT, HHSConstants.MODEL_CB_EQUIPMENT_BEAN);

				loBaseUnit = ((loBaseUnit == null) ? HHSConstants.INT_ZERO : loBaseUnit);
				if (loBaseUnit + Integer.parseInt((aoCBEquipmentBean.getUnits())) < HHSConstants.INT_ZERO)
				{
					loModUnitIsValid = false;
				}
			}
		}
		// Exception occur if database is down or getting null in required
		// parameter
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("aoCBEquipmentBean bean : ", CommonUtil.convertBeanToString(aoCBEquipmentBean));
			LOG_OBJECT.Error("Exception while validating modified units for Equipment", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: validateModificationUnitForEquipment method"
					+ " \n");
			throw loAppEx;
		}
		return loModUnitIsValid;
	}

	/**
	 * This method validates the modification amount in Equipment for a
	 * particular line-item and returns FALSE if the #amount user is trying to
	 * update tries to make the total amount for a line-item to fall below zero,
	 * else it validates it as TRUE <li>Query used :
	 * fetchEquipmentDetailsForValidation</li>
	 * @param aoMybatisSession SqlSession object
	 * @param aoCBEquipmentBean CBEquipmentBean object
	 * @return Boolean loModUnitIsValid valid/invalid
	 * @throws Exception Exception object
	 */
	private Boolean validateModificationAmountForEquipment(SqlSession aoMybatisSession,
			CBEquipmentBean aoCBEquipmentBean) throws Exception
	{
		Boolean loEditAmtValid = false;
		CBEquipmentBean loCBEquipmentBean = null;
		loCBEquipmentBean = (CBEquipmentBean) DAOUtil.masterDAO(aoMybatisSession, aoCBEquipmentBean.getId(),
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
				HHSConstants.CBM_FETCH_EQUIPMENT_DETAILS_FOR_VALIDATION, HHSConstants.JAVA_LANG_STRING);

		if (null == aoCBEquipmentBean.getEquipment()
				|| aoCBEquipmentBean.getEquipment().equals(HHSConstants.EMPTY_STRING))

		{
			aoCBEquipmentBean.setEquipment(loCBEquipmentBean.getEquipment());
		}

		// check if both subbudget ids are equal, means data was for new record
		// so remaining amount will be zero
		if (loCBEquipmentBean.getSubBudgetID().equals(aoCBEquipmentBean.getSubBudgetID()))
		{
			loCBEquipmentBean.setRemainingAmt(HHSConstants.STRING_ZERO);
		}

		// check sum of modified amount and remaining amount should be greater
		// than zero
		if ((new BigDecimal(loCBEquipmentBean.getRemainingAmt()).add(new BigDecimal(aoCBEquipmentBean
				.getModificationAmt()))).compareTo(BigDecimal.ZERO) >= HHSConstants.INT_ZERO)
		{
			loEditAmtValid = true;
		}
		return loEditAmtValid;
	}

	/**
	 * This method validates the modification amount in OTPS for a particular
	 * line-item and returns FALSE if the #amount user is trying to update tries
	 * to make the total amount for a line-item to fall below zero, else it
	 * validates it as TRUE <li>Query used : fetchOTPSDetailsForValidation</li>
	 * @param aoMybatisSession SqlSession object
	 * @param aoCBOperationSupportBean CBOperationSupportBean object
	 * @return Boolean loModUnitIsValid valid/invalid
	 * @throws Exception ApplicationException object
	 */
	private Boolean validateModificationAmountForOTPS(SqlSession aoMybatisSession,
			CBOperationSupportBean aoCBOperationSupportBean) throws Exception
	{
		Boolean loEditAmtValid = false;
		CBOperationSupportBean loCBOperationSupportBean = null;
		loCBOperationSupportBean = (CBOperationSupportBean) DAOUtil.masterDAO(aoMybatisSession,
				aoCBOperationSupportBean.getId(), HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
				HHSConstants.CBM_FETCH_OTPS_DETAILS_FOR_VALIDATION, HHSConstants.JAVA_LANG_STRING);

		// check sum of modified amount and remaining amount should be greater
		// than zero
		if ((new BigDecimal(loCBOperationSupportBean.getRemainingAmt()).add(new BigDecimal(aoCBOperationSupportBean
				.getModificationAmt()))).compareTo(BigDecimal.ZERO) >= HHSConstants.INT_ZERO)
		{
			loEditAmtValid = true;
		}
		return loEditAmtValid;
	}

	/**
	 * <li>This method is used to fetch number of approved Amendment Budgets for
	 * an Amendment Contract</li> <li>Query used : fetchCountOfApprovedBudget</li>
	 * @param aoMyBatisSession SqlSession object
	 * @param aoCBGridBeanObj CBGridBean object
	 * @return ContractedServicesBean
	 * @throws ApplicationException ApplicationException object
	 */
	public Integer fetchApprovedBudgetCountForAmendment(SqlSession aoMyBatisSession, TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		Integer loCount = null;
		try
		{
			loCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoTaskDetailsBean.getContractId(),
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_FETCH_COUNT_APPROVED_BUDGET, HHSConstants.JAVA_LANG_STRING);
		}
		/**
		 * Application Exception handled here
		 */
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured in ContractBudgetModificationService: fetchApprovedBudgetCountForAmendment method for Contract: "
							+ aoTaskDetailsBean.getContractId());
			setMoState("Transaction Failed:: ContractBudgetModificationService: fetchApprovedBudgetCountForAmendment"
					+ " method - failed Exception occured while fetching\n");
			throw loAppEx;
		}

		return loCount;
	}

	/**
	 * Made change for release 3.14.0
	 * @param aoMyBatisSession
	 * @param aoTaskDetailsBean
	 * @param loAmendmentBudgetIds
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void mergeOnlyConfigurationForAmendment(SqlSession aoMyBatisSession, TaskDetailsBean aoTaskDetailsBean,
			List<String> loAmendmentBudgetIds) throws ApplicationException
	{
		try
		{   
			LOG_OBJECT.Debug("**********mergeOnlyConfigurationForAmendment ");
			Map loHashMap = new HashMap();
			loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
			// loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW,
			// aoTaskDetailsBean.getBudgetId());
			// loHashMap.put(HHSConstants.STATUS_ID, aoBudgetStatus);
			loHashMap.put(HHSConstants.MODIFY_BY, aoTaskDetailsBean.getUserId());
			loHashMap.put(HHSConstants.LO_AMENDMENT_BUDGET_LIST, loAmendmentBudgetIds);
			
			// Fetch Parent ContractId
			String lsParentContractId = (String) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.FETCH_PARENT_CONTRACT_ID, HHSConstants.JAVA_UTIL_MAP);
			
			loHashMap.put(HHSConstants.PARENT_CONTRACT_ID, lsParentContractId);
						
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_AMEND_BASE_CONTRACT_AMOUNT, HHSConstants.JAVA_UTIL_MAP);
			// Update Base Contract End date as proposed end date of
			// Amendment Contract
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_UPDATE_BASE_CONTRACT_END_DATE, HHSConstants.JAVA_UTIL_MAP);
									
			// Update Contract financial amount for base contract
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_UPDATE_BASE_CONTRACT_FINANCIAL_AMOUNT, HHSConstants.JAVA_UTIL_MAP);
			// Insert Additional Line Items
			
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBY_CREATE_CF_REPLICA,
					HHSConstants.JAVA_UTIL_MAP);
			// mark all Contract Financial LineItems for deletion
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBM_DELETE_CF_REPLICA,
					HHSConstants.JAVA_UTIL_MAP);
		}
		catch (ApplicationException aoAppEx)
		{
			setMoState("Transaction Failed:: ContractBudgetModificationService:mergeOnlyConfigurationForAmendment method - failed to update record "
					+ " \n");
			aoAppEx.addContextData("BudgetId passed: ", aoTaskDetailsBean.getBudgetId());
			LOG_OBJECT.Error("ApplicationException occured while executing query mergeOnlyConfigurationForAmendment ",
					aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT
					.Error("Transaction Failed:: ContractBudgetModificationService:mergeOnlyConfigurationForAmendment method ",
							aoEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService:mergeOnlyConfigurationForAmendment method "
					+ " \n");
			ApplicationException loAppEx = new ApplicationException(
					"Transaction Failed:: ContractBudgetModificationService:mergeOnlyConfigurationForAmendment method ",
					aoEx);
			throw loAppEx;
		}
	}

	/**
	 * This method is used to merge budget, subBudget and line-items with their
	 * respective modification entries when Contract budget amendment review
	 * task is final Approved.
	 * <ul>
	 * <li>1. Merge LineItems where-ever we have modification in existing line
	 * itmes</li>
	 * <li>2. Create new Line Item and link with Base SubBudget for newly added
	 * line-items during Update</li>
	 * <li>Query used :fetchParentBudgetId</li>
	 * <li>This method was updated in R4</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession :Session Object
	 * @param aoTaskDetailsBean : Task Detail Bean
	 * @param aoBudgetStatus : BudgetStatus
	 * @param aoApprovedBudgetCount : Integer
	 * @throws ApplicationException :Application Exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked", "unused" })
	public void mergeBudgetLineItemsForAmendment(SqlSession aoMyBatisSession, TaskDetailsBean aoTaskDetailsBean,
			String aoBudgetStatus, Integer aoApprovedBudgetCount) throws ApplicationException
	{
		try
		{   //System.out.println("========ContractBudgetModificationService====[mergeBudgetLineItemsForAmendment]=====");
			Boolean isAmendmentRegisteredInFMS = false;
			// 6601
			Integer loAmendmentRegisteredInFMSFlag = (Integer) DAOUtil.masterDAO(aoMyBatisSession,
					aoTaskDetailsBean.getContractId(), HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.IS_AMENDMNET_REGISTERED_IN_FMS, HHSConstants.JAVA_LANG_STRING);
			if (loAmendmentRegisteredInFMSFlag != null && loAmendmentRegisteredInFMSFlag == 1)
			{
				isAmendmentRegisteredInFMS = true;
			}
			HashMap loHMArgs = new HashMap();
			loHMArgs.put(HHSConstants.CONTRACT_ID, aoTaskDetailsBean.getContractId());
			
			// [Start] QC 9505 R 8.6.0 Amendment Double merge issue 
			//System.out.println("========ContractBudgetModificationService [mergeBudgetLineItemsForAmendment] QC 9505 R 8.6.0 Amendment Double merge issue");
			/*
			List<ContractBudgetBean> loAffectedBudgets = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMyBatisSession,
					loHMArgs, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.FETCH_AMENDMENT_CONTRACT_BUDGETS_ALREADY_MERGED, HHSConstants.JAVA_UTIL_HASH_MAP);
			System.out.println("========ContractBudgetModificationService [mergeBudgetLineItemsForAmendment] loAffectedBudgetsOld :: "+loAffectedBudgetsOld);
			*/
			List<ContractBudgetBean> loAffectedBudgets = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMyBatisSession,
					loHMArgs, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.FETCH_AMENDMENT_CONTRACT_BUDGETS, HHSConstants.JAVA_UTIL_HASH_MAP);
			
			//System.out.println("========ContractBudgetModificationService [mergeBudgetLineItemsForAmendment] loAffectedBudgets :: "+loAffectedBudgets);
			LOG_OBJECT.Debug("********** loAffectedBudgets :: "+loAffectedBudgets);
			
			// [End] QC 9505 R 8.6.0 Amendment Double merge issue
			// fetchAmendmentContractBudgets
			
			List<String> loAmendmentBudgetIds = new ArrayList<String>();
			if (loAffectedBudgets != null && !loAffectedBudgets.isEmpty())
			{
				for (ContractBudgetBean loContractBudgetBean : loAffectedBudgets)
				{
					loAmendmentBudgetIds.add(loContractBudgetBean.getBudgetfiscalYear());
				}
			}
			// 6601
			//System.out.println("========ContractBudgetModificationService [mergeBudgetLineItemsForAmendment] loAmendmentBudgetIds :: "+loAmendmentBudgetIds);
			LOG_OBJECT.Debug("**********STARTED mergeBudgetLineItemsForAmendment: "
					+ GregorianCalendar.getInstance().getTime() + "ms "
					+ GregorianCalendar.getInstance().getTimeInMillis());

			Map loHashMap = new HashMap();
			loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoTaskDetailsBean.getBudgetId());
			loHashMap.put(HHSConstants.STATUS_ID, aoBudgetStatus);
			loHashMap.put(HHSConstants.MODIFY_BY, aoTaskDetailsBean.getUserId());
			// 6601
			loHashMap.put(HHSConstants.LO_AMENDMENT_BUDGET_LIST, loAmendmentBudgetIds);
			// 6601
			// Fetch Parent BudgetId
			String lsParentBudgetId = (String) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.FETCH_PARENT_BUDGET_ID,
					HHSConstants.JAVA_UTIL_MAP);

			if (lsParentBudgetId == null)
			{
				setMoState("Transaction Failed:: ContractBudgetModificationService:  mergeBudgetLineItemsForAmendment method - Unable to fetch parent budget id for budget id "
						+ aoTaskDetailsBean.getBudgetId() + " \n");
				throw new ApplicationException(
						"Unable to fetch parent budget id for budget in mergeBudgetLineItemsForAmendment method");
			}

			// Fetch Parent ContractId
			String lsParentContractId = (String) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.FETCH_PARENT_CONTRACT_ID, HHSConstants.JAVA_UTIL_MAP);

			if (lsParentContractId == null)
			{
				setMoState("Transaction Failed:: ContractBudgetModificationService:mergeBudgetLineItemsForAmendment method - Unable to fetch parent contract id for contract id "
						+ aoTaskDetailsBean.getContractId() + " \n");
				throw new ApplicationException(
						"Unable to fetch parent contract id for contract in mergeBudgetLineItemsForAmendment method");
			}

			loHashMap.put(HHSConstants.PARENT_BUDGET_ID, lsParentBudgetId);
			loHashMap.put(HHSConstants.PARENT_CONTRACT_ID, lsParentContractId);
			loHashMap.put(HHSConstants.ENTITY_ID, aoTaskDetailsBean.getBudgetId());
			loHashMap.put(HHSConstants.ENTITY_TYPE, aoTaskDetailsBean.getTaskName());
			loHashMap.put(HHSConstants.USER_ID, aoTaskDetailsBean.getUserId());
			loHashMap.put(HHSConstants.NEW_ENTITY_ID, lsParentBudgetId);
			loHashMap.put(HHSConstants.NEW_ENTITY_TYPE, HHSConstants.TASK_BUDGET_REVIEW);

			// Merge LineItem for Budget
			System.out.println("========ContractBudgetModificationService mergeLineItemReplica");
			mergeLineItemReplica(aoMyBatisSession, loHashMap);
			// Fetching sub_budget list of Budget
			List<String> loSubBudgetIdList = fetchSubBudgetListForParentBudget(aoMyBatisSession, lsParentBudgetId);
			// Link new LineItems for all SubBudgets to Parent SubBUdget and
			// Budget
			Iterator<String> loListIterator = loSubBudgetIdList.iterator();
			while (loListIterator.hasNext())
			{
				loHashMap.put(HHSConstants.SUBBUDGET_ID, loListIterator.next());
				// insert newly added LineItems and link with Parent Budget and
				// SubBudgetId
				linkAdditionalLineItemsToBase(aoMyBatisSession, loHashMap);

			}

			// Fetching list of newly added sub-budget which were added during
			// update configuration task
			LOG_OBJECT.Debug("1*******Fetching list of newly added sub-budget which were added during update configuration task");
			List<String> loNewAddedSubBudgetIdList = fetchNewAddedSubBudgetList(aoMyBatisSession, aoTaskDetailsBean.getBudgetId()); 
			LOG_OBJECT.Debug("1*******loNewAddedSubBudgetIdList :: "+loNewAddedSubBudgetIdList);
			if (loNewAddedSubBudgetIdList != null && !loNewAddedSubBudgetIdList.isEmpty())
			{
				Iterator<String> loNewListIterator = loNewAddedSubBudgetIdList.iterator();
				while (loNewListIterator.hasNext())
				{
					loHashMap.put(HHSConstants.SUBBUDGET_ID, loNewListIterator.next());
					// link LineItems of newly added sub budgets to base budget
					linkNewAddedSubBudgetToBase(aoMyBatisSession, loHashMap); 

				}
			}

			// Override Base Budget, SubBudget,Contract and contract financial
			// Amounts amounts with
			// Update Amount
			System.out.println("----ContractBudgetModificationService mergeBudgetLineItemsForAmendment :: overrideBaseAmountForAmendment");
			System.out.println("+++ overrideBaseAmountForAmendment param :: \n");
			System.out.println("+++ param aoApprovedBudgetCount :: "+aoApprovedBudgetCount+"\n");
			System.out.println("+++ param isAmendmentRegisteredInFMS :: "+isAmendmentRegisteredInFMS+"\n");
			System.out.println("+++ param loAmendmentBudgetIds :: "+loAmendmentBudgetIds+"\n");
			overrideBaseAmountForAmendment(aoMyBatisSession, loHashMap, aoApprovedBudgetCount,
					isAmendmentRegisteredInFMS, loAmendmentBudgetIds);

			if (isAmendmentRegisteredInFMS)
			{
				/*
				 * //start release 3.14.0 // Insert Additional Line Items
				 * DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
				 * HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER
				 * ,
				 * HHSConstants.CBY_CREATE_CF_REPLICA_FOR_FMS_REGISTERED_CONTRACT
				 * , HHSConstants.JAVA_UTIL_MAP); //end release 3.14.0
				 */
			}
			else
			{
				// Link newly added contract financial line items into base
				// contract
				linkAdditionalCFLineItemsToBase(aoMyBatisSession, loHashMap);
			}
			// mark all LineItems for deletion
			// changes for agency outbound interafce 6644
			markLineItemsForDeletion(aoMyBatisSession, loHashMap);
			// mark all Contract Financial LineItems for deletion
			if (isAmendmentRegisteredInFMS)
			{
				/*
				 * //start release 3.14.0 //changes for agency outbound
				 * interafce 6644 DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
				 * HHSConstants
				 * .MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
				 * HHSConstants
				 * .CBM_DELETE_CF_REPLICA_FOR_FMS_REGISTERED_CONTRACT,
				 * HHSConstants.JAVA_UTIL_MAP); //end release 3.14.0
				 */
			}
			else
			{
				// changes for agency outbound interafce 6644
				markCFLineItemsForDeletion(aoMyBatisSession, loHashMap); 
			}

			String lsBudgetId = (String) loHashMap.get(HHSConstants.BUDGET_ID_WORKFLOW);
			// Mark All Modification SubBudget for Deletion
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_DELETE_SUBBUDGET_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			// Merge newly added Documents with Base Budget and Base Contract
			mergeDocumentsForUpdation(aoMyBatisSession, loHashMap);
			// Change Update_Budget Status to Approved
			// start release 3.14.0
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_SET_CONTRACT_BUDGET_STATUS_FOR_REVIEW_TASK_FOR_AMENDMENT,
					HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_SET_CONTRACT_BUDGET_STATUS_FOR_REVIEW_TASK_FOR_AMENDMENT_IN_CONFIGURATION_STATUS,
					HHSConstants.JAVA_UTIL_MAP);
			// end release 3.14.0
			// R4 Changes:Set amendment contract status to pending registration
			// if all budgets are approved
			DAOUtil.masterDAO(aoMyBatisSession, aoTaskDetailsBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
					HHSConstants.UPDATE_AMEND_CONTRACT_STATUS_PEND_REGISTRATION, HHSConstants.CS_TASK_DETAILS_BEAN);

			// Release 3.6.0 Enhancement id 6484
			FinancialsService loFinancialsService = new FinancialsService();
			loFinancialsService.mergeSubBudetSite(aoMyBatisSession, aoTaskDetailsBean, true);

			// ** [Start] QC 9550 R 8.7.0 Prevent Double Merge Negative Amendment
			//LOG_OBJECT.Debug("**********[Start] QC 9550 R 8.7.0 Prevent Double Merge Negative Amendment****");
			LOG_OBJECT.Debug("*********aoTaskDetailsBean.getAmendmentType() :: "+aoTaskDetailsBean.getAmendmentType());
			LOG_OBJECT.Debug("*********aoTaskDetailsBean.getContractId() :: "+aoTaskDetailsBean.getContractId());
			LOG_OBJECT.Debug("*********isAmendmentRegisteredInFMS :: " + isAmendmentRegisteredInFMS); // 1 - true
			
			Integer loAmdStatusId = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoTaskDetailsBean.getContractId(),
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.CHECK_STATUS_FOR_SUS_OR_UN_SUS,
					HHSConstants.JAVA_LANG_STRING);
			LOG_OBJECT.Debug("*********amdStatusId :: " + loAmdStatusId); 
			if("negative".equalsIgnoreCase(aoTaskDetailsBean.getAmendmentType()) 
					&& isAmendmentRegisteredInFMS && loAmdStatusId == 129)
			{
				LOG_OBJECT.Debug("**********Do not reset IS_AMENDMENT_REGISTERED_IN_FMS to 0 for Negative Amendment that is already registered in FMS & partially merged****** "
						+ " \n");
			}
			else
			{
			// *** [End] QC 9550 R 8.7.0 Prevent Double Merge Negative Amendment
				// 6601
				DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
						HHSConstants.RESET_FLAG_AMENDMENT_REGISTERED_IN_FMS, HHSConstants.JAVA_UTIL_HASH_MAP);
				// 6601
			// *** [Start] QC 9550 R 8.7.0 Prevent Double Merge Negative Amendment	
			}
			// *** [End] QC 9550 R 8.7.0 Prevent Double Merge Negative Amendment
			//LOG_OBJECT.Debug("**********[End] QC 9550 R 8.7.0 Prevent Double Merge Negative Amendment****");
			
			LOG_OBJECT.Debug("**********ENDS mergeBudgetLineItemsForUpdate: "
					+ GregorianCalendar.getInstance().getTime() + " ms "
					+ GregorianCalendar.getInstance().getTimeInMillis());
			setMoState("Transaction Success:: ContractBudgetService:setContractBudgetStatusForReviewTask method - success to update record "
					+ " \n");

		}
		catch (ApplicationException aoAppEx)
		{
			setMoState("Transaction Failed:: ContractBudgetModificationService:mergeBudgetLineItemsForAmendment method - failed to update record "
					+ " \n");
			aoAppEx.addContextData("BudgetId passed: ", aoTaskDetailsBean.getBudgetId());
			LOG_OBJECT.Error("ApplicationException occured while executing query mergeBudgetLineItemsForAmendment ",
					aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in mergeBudgetLineItemsForAmendment ", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService:mergeBudgetLineItemsForAmendment method - failed to update record "
					+ " \n");
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while executing query in mergeBudgetLineItemsForAmendment", aoAppEx);
			throw loAppEx;
		}
	}

	// S365 screen otps modification end

	/**
	 * Release 3.6.0 Enhancement id 6263
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Boolean mergeBudgetLineItemsAmendmentNoBudgetEffected(SqlSession aoMyBatisSession, HashMap aoHashMap,
			Boolean aoFinishTaskStatus, Boolean loNegativeAmendmentNoBudgetEffected) throws ApplicationException
	{
		Boolean loMergeSuccess = false;
		try
		{
			System.out.println("\n ---------------------------" + "[mergeBudgetLineItemsAmendmentNoBudgetEffected] STARTS\n");
			if (aoFinishTaskStatus && loNegativeAmendmentNoBudgetEffected)
			{
				// Fetch Parent ContractId
				String lsParentContractId = (String) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_PARENT_CONTRACT_ID, HHSConstants.JAVA_UTIL_MAP);
System.out.println("\n ---------------------------" + "[mergeBudgetLineItemsAmendmentNoBudgetEffected] PARENT_CONTRACT_ID"+  lsParentContractId  +"  \n");

				aoHashMap.put(HHSConstants.PARENT_CONTRACT_ID, lsParentContractId);

System.out.println("\n ---------------------------" + "[mergeBudgetLineItemsAmendmentNoBudgetEffected] into overrideBaseAmountForAmendmentInContract  \n");
				// Override Base Budget, SubBudget,Contract and contract
				// financial
				// Amounts amounts with
				// Update Amount
				overrideBaseAmountForAmendmentInContract(aoMyBatisSession, aoHashMap, false, null);
				// Link newly added contract financial line items into base
				// contract

System.out.println("\n ---------------------------" + "[mergeBudgetLineItemsAmendmentNoBudgetEffected] into linkAdditionalCFLineItemsToBase  \n");
				linkAdditionalCFLineItemsToBase(aoMyBatisSession, aoHashMap);
				// mark all Contract Financial LineItems for deletion
				// changes for agency outbound interafce 6644

System.out.println("\n ---------------------------" + "[mergeBudgetLineItemsAmendmentNoBudgetEffected] into linkAdditionalCFLineItemsToBase  \n");
				markCFLineItemsForDeletion(aoMyBatisSession, aoHashMap);
				// Merge newly added Documents with Base Budget and Base
				// Contract

System.out.println("\n ---------------------------" + "[mergeBudgetLineItemsAmendmentNoBudgetEffected] into linkAdditionalCFLineItemsToBase  \n");
				mergeDocumentsForUpdationInContract(aoMyBatisSession, aoHashMap);
				loMergeSuccess = true;
			}
		}
		catch (ApplicationException aoAppEx)
		{
			setMoState("Transaction Failed:: ContractBudgetModificationService:mergeBudgetLineItemsForAmendmentt method - failed to update record "
					+ " \n");
			aoAppEx.addContextData("ContractId passed: ", (String) aoHashMap.get(HHSConstants.CONTRACT_ID_WORKFLOW));
			LOG_OBJECT.Error("ApplicationException occured while executing query mergeBudgetLineItemsForAmendment ",
					aoAppEx);
			throw aoAppEx;
		}
		return loMergeSuccess;
	}

	/**
	 * This method is used to merge budget, subBudget and line-items with their
	 * respective modification entries when Contract budget Update review task
	 * is final Approved.
	 * <ul>
	 * <li>1. Merge LineItems where-ever we have modification in existing line
	 * itmes</li>
	 * <li>2. Create new Line Item and link with Base SubBudget for newly added
	 * line-items during Update</li>
	 * <li>Query used : fetchParentBudgetId</li>
	 * <li>Query used : fetchParentContractId</li>
	 * <li>Query used : setContractBudgetStatusForReviewTask</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession :Session Object
	 * @param abFinalFinish :
	 * @param aoTaskDetailsBeanForMerge : Task Detail Bean
	 * @param aoBudgetStatus : BudgetStatus
	 * @throws ApplicationException :Application Exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void mergeBudgetLineItemsForUpdate(SqlSession aoMyBatisSession, TaskDetailsBean aoTaskDetailsBeanForMerge,
			String aoBudgetStatus) throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Debug("**********STARTED mergeBudgetLineItemsForUpdate: "
					+ GregorianCalendar.getInstance().getTime() + "ms "
					+ GregorianCalendar.getInstance().getTimeInMillis());

			Map loHashMapForBudgetMerge = new HashMap();
			loHashMapForBudgetMerge.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBeanForMerge.getContractId());
			loHashMapForBudgetMerge.put(HHSConstants.BUDGET_ID_WORKFLOW, aoTaskDetailsBeanForMerge.getBudgetId());
			loHashMapForBudgetMerge.put(HHSConstants.STATUS_ID, aoBudgetStatus);
			loHashMapForBudgetMerge.put(HHSConstants.MODIFY_BY, aoTaskDetailsBeanForMerge.getUserId());
			// Fetch Parent BudgetId and Parent ContractId
			String lsParentBudgetIdForMerge = (String) DAOUtil.masterDAO(aoMyBatisSession, loHashMapForBudgetMerge,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.FETCH_PARENT_BUDGET_ID,
					HHSConstants.JAVA_UTIL_MAP);
			LOG_OBJECT.Debug("fetchParentBudgetId Query Finish with lsParentBudgetIdForMerge:"+ lsParentBudgetIdForMerge);
			if (lsParentBudgetIdForMerge == null)
			{
				setMoState("Transaction Failed:: ContractBudgetModificationService:mergeBudgetLineItemsForUpdate method - Unable to fetch parent budget id for budget id "
						+ aoTaskDetailsBeanForMerge.getBudgetId() + " \n");
				throw new ApplicationException(
						"Unable to fetch parent budget id for budget in mergeBudgetLineItemsForUpdate method");
			}

			String lsParentContractId = (String) DAOUtil.masterDAO(aoMyBatisSession, loHashMapForBudgetMerge,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.FETCH_PARENT_CONTRACT_ID, HHSConstants.JAVA_UTIL_MAP);
			if (lsParentContractId == null)
			{
				setMoState("Transaction Failed:: ContractBudgetModificationService:mergeBudgetLineItemsForUpdate method - Unable to fetch parent contract id for contract id "
						+ aoTaskDetailsBeanForMerge.getContractId() + " \n");
				throw new ApplicationException(
						"Unable to fetch parent contract id for contract in mergeBudgetLineItemsForUpdate method");
			}

			loHashMapForBudgetMerge.put(HHSConstants.PARENT_BUDGET_ID, lsParentBudgetIdForMerge);
			loHashMapForBudgetMerge.put(HHSConstants.PARENT_CONTRACT_ID, lsParentContractId);
			loHashMapForBudgetMerge.put(HHSConstants.ENTITY_ID, aoTaskDetailsBeanForMerge.getBudgetId());
			loHashMapForBudgetMerge.put(HHSConstants.ENTITY_TYPE, aoTaskDetailsBeanForMerge.getTaskName());
			loHashMapForBudgetMerge.put(HHSConstants.USER_ID, aoTaskDetailsBeanForMerge.getUserId());
			loHashMapForBudgetMerge.put(HHSConstants.NEW_ENTITY_ID, lsParentBudgetIdForMerge);
			loHashMapForBudgetMerge.put(HHSConstants.NEW_ENTITY_TYPE, HHSConstants.TASK_BUDGET_REVIEW);

			// Merge LineItem for Budget
			// Start:Updated in R7
			mergeLineItemReplica(aoMyBatisSession, loHashMapForBudgetMerge);
			// Fetching sub_budget list of Budget
			List<String> loSubBudgetIdList = fetchSubBudgetListForParentBudget(aoMyBatisSession,
					lsParentBudgetIdForMerge);
			// Link new LineItems for all SubBudgets to Parent SubBudget and
			// Budget
			Iterator<String> loListIterator = loSubBudgetIdList.iterator();
			while (loListIterator.hasNext())
			{
				loHashMapForBudgetMerge.put(HHSConstants.SUBBUDGET_ID, loListIterator.next());
				// insert newly added LineItems and link with Parent Budget and
				// SubBudgetId
				linkAdditionalLineItemsToBase(aoMyBatisSession, loHashMapForBudgetMerge);

			}

			// Fetching list of newly added sub-budget which were added during
			// update configuration task
			LOG_OBJECT.Debug("2*******Fetching list of newly added sub-budget which were added during update configuration task");
			List<String> loNewAddedSubBudgetIdList = fetchNewAddedSubBudgetList(aoMyBatisSession, aoTaskDetailsBeanForMerge.getBudgetId());
			LOG_OBJECT.Debug("2*******loNewAddedSubBudgetIdList :: "+loNewAddedSubBudgetIdList);
			if (loNewAddedSubBudgetIdList != null && !loNewAddedSubBudgetIdList.isEmpty())
			{
				Iterator<String> loNewListIterator = loNewAddedSubBudgetIdList.iterator();
				while (loNewListIterator.hasNext())
				{
					loHashMapForBudgetMerge.put(HHSConstants.SUBBUDGET_ID, loNewListIterator.next());
					// link LineItems of newly added sub budgets to base budget
					linkNewAddedSubBudgetToBase(aoMyBatisSession, loHashMapForBudgetMerge);

				}
			}

			// Override Base Budget, SubBudget and Contract Amounts amounts with
			// Update Amount
			overrideBaseAmountForUpdation(aoMyBatisSession, loHashMapForBudgetMerge);
			// Link newly added contract financial line items into base contract
			// added for defect 6230
			linkAdditionalCFLineItemsToBase(aoMyBatisSession, loHashMapForBudgetMerge);
			// mark all LineItems for deletion
			// changes for agency outbound interafce 6644
			markLineItemsForDeletion(aoMyBatisSession, loHashMapForBudgetMerge);
			// Mark All Modification SubBudget and Budget for Deletion
			// changes for agency outbound interafce 6644
			markBudgetsForDeletion(aoMyBatisSession, loHashMapForBudgetMerge);
			// Mark Contract with type update for Deletion
			// changes for agency outbound interafce 6644
			markContractForDeletion(aoMyBatisSession, loHashMapForBudgetMerge);
			// Merge newly added Documents with Base Budget and Base Contract
			mergeDocumentsForUpdation(aoMyBatisSession, loHashMapForBudgetMerge);
			// Change Update_Budget Status to Approved
			DAOUtil.masterDAO(aoMyBatisSession, loHashMapForBudgetMerge,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_SET_CONTRACT_BUDGET_STATUS_FOR_REVIEW_TASK, HHSConstants.JAVA_UTIL_MAP);
			LOG_OBJECT.Debug("**********ENDS mergeBudgetLineItemsForUpdate: "
					+ GregorianCalendar.getInstance().getTime() + " ms "
					+ GregorianCalendar.getInstance().getTimeInMillis());
			setMoState("Transaction Success:: ContractBudgetService:setContractBudgetStatusForReviewTask method - success to update record "
					+ " \n");

		}
		catch (ApplicationException aoAppEx)
		{
			setMoState("Transaction Failed:: ContractBudgetService:mergeBudgetLineItemsForUpdate method - failed to update record "
					+ " \n");
			aoAppEx.addContextData("BudgetId passed: ", aoTaskDetailsBeanForMerge.getBudgetId());
			LOG_OBJECT.Error("ApplicationException occured while executing query mergeBudgetLineItemsForUpdate ",
					aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in mergeBudgetLineItemsForUpdate ", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService:mergeBudgetLineItemsForUpdate method - failed to update record "
					+ " \n");
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while executing query in mergeBudgetLineItemsForUpdate", aoAppEx);
			throw loAppEx;
		}
	}

	// Updated in R7 for Cost Center Merging changes
	/**
	 * This method is used to merge budget, subBudget and line-items with their
	 * respective modification entries when Contract budget Modification review
	 * task is final Approved.
	 * <ul>
	 * <li>1. Merge LineItems where-ever we have modification in existing line
	 * itmes</li>
	 * <li>2. Create new Line Item and link with Base SubBudget for newly added
	 * line-items during Modification</li>
	 * <li>Query used : fetchParentBudgetId</li>
	 * <li>Query used : mergeBudgetModificationDocument</li>
	 * <li>Query used : setContractBudgetStatusForReviewTask</li>
	 * <li>3. This method is updated for merging grid data in R7 for Cost Center
	 * </ul>
	 * 
	 * @param aoMyBatisSession :Session Object
	 * @param abFinalFinish :
	 * @param aoTaskDetailsBean : Task Detail Bean
	 * @param aoBudgetStatus : BudgetStatus
	 * @return boolean : Status of operation
	 * @throws ApplicationException :Application Exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean mergeBudgetLineItemsForModification(SqlSession aoMyBatisSession, TaskDetailsBean aoTaskDetailsBean,
			String aoBudgetStatus) throws ApplicationException
	{
		boolean lbState = false;
		try
		{
			LOG_OBJECT.Debug("**********STARTED mergeBudgetLineItemsForModification: "
					+ GregorianCalendar.getInstance().getTime() + "ms "
					+ GregorianCalendar.getInstance().getTimeInMillis());

			Map loHashMap = new HashMap();
			loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoTaskDetailsBean.getBudgetId());
			loHashMap.put(HHSConstants.STATUS_ID, aoBudgetStatus);
			loHashMap.put(HHSConstants.MODIFY_BY, aoTaskDetailsBean.getUserId());
			String lsParentBudgetId = (String) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.FETCH_PARENT_BUDGET_ID,
					HHSConstants.JAVA_UTIL_MAP);
			if (lsParentBudgetId == null)
			{
				setMoState("Transaction Failed:: ContractBudgetModificationService:mergeBudgetLineItemsForModification method - Unable to fetch parent budget id for budget id "
						+ aoTaskDetailsBean.getBudgetId() + " \n");
				throw new ApplicationException(
						"Unable to fetch parent budget id for budget in mergeBudgetLineItemsForModification method");
			}

			loHashMap.put(HHSConstants.PARENT_BUDGET_ID, lsParentBudgetId);
			loHashMap.put(HHSConstants.ENTITY_ID, aoTaskDetailsBean.getBudgetId());
			loHashMap.put(HHSConstants.ENTITY_TYPE, aoTaskDetailsBean.getTaskName());
			loHashMap.put(HHSConstants.USER_ID, aoTaskDetailsBean.getUserId());
			loHashMap.put(HHSConstants.NEW_ENTITY_ID, lsParentBudgetId);
			loHashMap.put(HHSConstants.NEW_ENTITY_TYPE, HHSConstants.TASK_BUDGET_REVIEW);

			// Merge LineItem for Budget
			mergeLineItemReplica(aoMyBatisSession, loHashMap);

			// Fetching sub_budget list of Budget
			List<String> loSubBudgetIdList = fetchSubBudgetListForParentBudget(aoMyBatisSession, lsParentBudgetId);
			// Link new LineItems for all SubBudgets to Parent SubBUdget and
			// Budget
			Iterator<String> loListIterator = loSubBudgetIdList.iterator();
			while (loListIterator.hasNext())
			{
				loHashMap.put(HHSConstants.SUBBUDGET_ID, loListIterator.next());
				// insert newly added LineItems and link with Parent Budget and
				// SubBudgetId
				linkAdditionalLineItemsToBase(aoMyBatisSession, loHashMap);

			}

			// mark all LineItems for deletion
			// changes for agency outbound interafce 6644
			markLineItemsForDeletion(aoMyBatisSession, loHashMap);
			// Mark All Modification SubBudget and Budget for Deletion
			// changes for agency outbound interafce 6644
			markBudgetsForDeletion(aoMyBatisSession, loHashMap);
			// Merge BudgetMOdification Documents
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.MERGE_BUDGET_MODIFICATION_DOCUMENT, HHSConstants.JAVA_UTIL_MAP);
			setMoState("Transaction Success:: ContractBudgetModificationService:mergeBudgetModificationDocument method - success to update record "
					+ " \n");

			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_SET_CONTRACT_BUDGET_STATUS_FOR_REVIEW_TASK, HHSConstants.JAVA_UTIL_MAP);

			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_SUB_BUDGET_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_BUDGET_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_CONTRACT_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);
			// changes for agency outbound interafce 6644
			LOG_OBJECT.Debug("**********ENDS createReplicaOfBUdget: " + GregorianCalendar.getInstance().getTime()
					+ " ms " + GregorianCalendar.getInstance().getTimeInMillis());
			setMoState("Transaction Success:: ContractBudgetService:setContractBudgetStatusForReviewTask method - success to update record "
					+ " \n");
			lbState = true;

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException occured while executing query setContractBudgetStatusForReviewTask ",
							aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService:setContractBudgetStatusForReviewTask method - failed to update record "
					+ " \n");
			aoAppEx.addContextData("BudgetId passed: ", aoTaskDetailsBean.getBudgetId());
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in setContractBudgetStatusForReviewTask ",
					aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService:setContractBudgetStatusForReviewTask method - failed to update record "
					+ " \n");
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while executing query in setContractBudgetStatusForReviewTask", aoAppEx);
			throw loAppEx;
		}
		return lbState;
	}

	/**
	 * This method is responsible for fetching list of subbudget id's of a
	 * Budget. <li>Query used : fetchSubBudgetList</li>
	 * @param aoMybatisSession SqlSession
	 * @param aoBudgetId String
	 * @return loSubBudgetIdList List<String>
	 * @throws ApplicationException
	 */

	@SuppressWarnings("unchecked")
	private List<String> fetchSubBudgetListForParentBudget(SqlSession aoMyBatisSession, String aoBudgetId)
			throws ApplicationException
	{
		List<String> loSubBudgetIdList = null;
		try
		{

			// Fetching sub-budget id list of the budget
			loSubBudgetIdList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, aoBudgetId,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_FETCH_SUB_BUDGET_LIST, HHSConstants.JAVA_LANG_STRING);
			if (loSubBudgetIdList == null || loSubBudgetIdList.isEmpty())
			{
				throw new ApplicationException("SubBudget list not fetched successfully for BudgetId : " + aoBudgetId);
			}
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("BudgetId passed: ", aoBudgetId);
			LOG_OBJECT.Error("ApplicationException occured while executing query fetchSubBudgetList ", aoAppEx);
			throw aoAppEx;
		}
		return loSubBudgetIdList;
	}

	/**
	 * This method is responsible for fetching list of sub budget id's added
	 * during update/amendment of Update/Amendment Budget. <li>Query used :
	 * fetchNewlyAddedSubBudgetList</li>
	 * @param aoMybatisSession SqlSession
	 * @param aoBudgetId String
	 * @return loSubBudgetIdList List<String>
	 * @throws ApplicationException
	 */

	@SuppressWarnings("unchecked")
	private List<String> fetchNewAddedSubBudgetList(SqlSession aoMyBatisSession, String aoBudgetId)
			throws ApplicationException
	{
		List<String> loSubBudgetIdList = null;
		try
		{

			// Fetching sub-budget id list of the budget
			loSubBudgetIdList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, aoBudgetId,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_FETCH_NEW_ADDED_SUB_BUDGET_LIST, HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("BudgetId passed: ", aoBudgetId);
			LOG_OBJECT.Error("ApplicationException occured while executing query fetchNewlyAddedSubBudgetList ",
					aoAppEx);
			throw aoAppEx;
		}
		return loSubBudgetIdList;
	}

	// Updated in R7 for Cost-Center
	/**
	 * This method is responsible for merging Modification Line Item records of
	 * a sub-budget with their respective rows when contract budget modification
	 * review task is finished. <li>Query used : mergePersonnelServiceReplica</li>
	 * <li>Query used : mergeOperationAndSupportReplica</li> <li>Query used :
	 * mergeProfessionalServiceReplica</li> <li>Query used :
	 * mergeUtilitiesReplica</li> <li>Query used :
	 * mergeProfessionalServiceReplica</li> <li>Query used : mergeRentReplica</li>
	 * <li>Query used : mergeContractedServicesReplica</li> <li>Query used :
	 * mergeMilestoneReplica</li> <li>Query used : mergeProgramIncomeReplica</li>
	 * <li>Query used : mergeFringeReplica</li> <li>Query used :
	 * mergeEquipmentReplica</li>
	 * Updated in R7 for Cost-Center
	 * <li>Query added : mergeServicesReplica</li> <li>Query added :
	 * mergeCostCenterReplica</li>
	 * @param aoMybatisSession
	 * @param aoHashMap
	 * @throws ApplicationException
	 */

	@SuppressWarnings("rawtypes")
	private void mergeLineItemReplica(SqlSession aoMyBatisSession, Map aoHashMap) throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Debug("Inside mergeLineItemReplica with aoHashMap:"+ aoHashMap.toString());

			// Creating Budget record replica
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBM_MERGE_PS_REPLICA,
					HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBM_MERGE_OPS_REPLICA,
					HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_MERGE_UTILITIES_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBM_MERGE_PFS_REPLICA,
					HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBM_MERGE_RENT_REPLICA,
					HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBM_MERGE_CS_REPLICA,
					HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBM_MERGE_RATE_REPLICA,
					HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_MERGE_MILESTONE_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_MERGE_UNALLOCATED_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_MERGE_INDIRECT_RATE_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_MERGE_PROGRAM_INCOME_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_MERGE_FRINGE_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_MERGE_EQUIPMENT_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			// Start:Added in R7 for Cost-Center
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.CBM_MERGE_SERVICES_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.CBM_MERGE_COSTCENTER_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			// End:Added in R7 for Cost-Center
			LOG_OBJECT.Debug("Exiting mergeLineItemReplica:");

		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("SubBudgetId passed: ", aoHashMap.get(HHSConstants.SUB_BUDGET_ID));
			LOG_OBJECT.Error("ApplicationException occured while executing query mergeLineItemReplica ", aoAppEx);
			throw aoAppEx;
		}

	}

	// Updated in R7 for Cost-Center
	/**
	 * This method is responsible for inserting newly added Line Item and
	 * linking them with Base sub-budget when contract budget modification
	 * review task is finished. <li>Query used : createPersonnelServiceReplica</li>
	 * <li>Query used : createOperationAndSupportReplica</li> <li>Query used :
	 * createProfessionalServiceReplica</li> <li>Query used : createRentReplica</li>
	 * <li>Query used : createContractedServicesReplica</li> <li>Query used :
	 * createRateReplica</li> <li>Query used : createUnallocatedReplica</li> <li>
	 * Query used : mergeEquipmentReplica</li> <li>Query used :
	 * createIndirectRateReplica</li> <li>Query used : createFringeReplica</li>
	 * <li>Query used : createEquipmentReplica</li>
	 * Updated in R7 for Cost-Center
	 * <li>Query Added : createServicesReplica</li>
	 * <li>Query Added : createCostCenterReplica</li>
	 * @param aoMybatisSession
	 * @param aoHashMap
	 * @throws ApplicationException
	 */

	@SuppressWarnings("rawtypes")
	private void linkAdditionalLineItemsToBase(SqlSession aoMyBatisSession, Map aoHashMap) throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Debug("Inside linkAdditionalLineItemsToBase:");

			// Insert Additional Line Items
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBY_CREATE_PS_REPLICA,
					HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBY_CREATE_OPS_REPLICA,
					HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_CREATE_UTILITIES_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBY_CREATE_PFS_REPLICA,
					HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_CREATE_RENT_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBY_CREATE_CS_REPLICA,
					HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_CREATE_RATE_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_CREATE_MILESTONE_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_CREATE_UNALLOCATED_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_CREATE_INDIRECT_RATE_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_CREATE_PROGRAM_INCOME_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_CREATE_FRINGE_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_CREATE_EQUIPMENT_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			// Start:Added in R7 for Cost-Center
			// For Amendment insert new Added services
			LOG_OBJECT.Debug("Executing createServicesReplica query:");
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.CBY_CREATE_SERVICES_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.CBY_CREATE_COSTCENTER_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			// For Amendment: merge amendment changes to base budget.
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.CBY_MERGE_SERVICES_FOR_AMENDMENT, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.CBY_MERGE_COSTCENTER_FOR_AMENDMENT, HHSConstants.JAVA_UTIL_MAP);
			LOG_OBJECT.Debug("Executing insertAmendmentServicesToBase query:");
			// Insert in base_Budget_configuration
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.CBY_INSERT_SERVICES_TO_BASE_CONFIGURATION, HHSConstants.JAVA_UTIL_MAP);
			// End:Added in R7 for Cost-Center
			LOG_OBJECT.Debug("Exited linkAdditionalLineItemsToBase:");

		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("SubBudgetId passed: ", aoHashMap.get(HHSConstants.SUB_BUDGET_ID));
			LOG_OBJECT.Error("ApplicationException occured while executing query mergeLineItemReplica ", aoAppEx);
			throw aoAppEx;
		}

	}

	/**
	 * Updated in R7 for Cost-Center
	 * This method is responsible for linking new added sub budget during
	 * update/amendment to base budget
	 * <ul>
	 * <li>Query used : linkSubBudgetToBase</li>
	 * <li>Query used : linkPersonnelServicesToBase</li>
	 * <li>Query used : linkOperationsAndSupportToBase</li>
	 * <li>Query used : linkUtilitiesToBase</li>
	 * <li>Query used : linkProfessionalServiceToBase</li>
	 * <li>Query used : linkRentToBase</li>
	 * <li>Query used : linkContractedServicesToBase</li>
	 * <li>Query used : linkRateToBase</li>
	 * <li>Query used : linkIndirectToBase</li>
	 * <li>Query used : linkFringeToBase</li>
	 * <li>Query used : linkEquipmemtToBase</li>
	 * Start: Added in R7
	 * <li>Query used : linkServicesToBase</li>
	 * <li>Query used : linkCostCenterToBase</li>
	 * End: Added in R7
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoHashMapForSubBudget Map
	 * @throws ApplicationException
	 */

	@SuppressWarnings("rawtypes")
	private void linkNewAddedSubBudgetToBase(SqlSession aoMyBatisSession, Map aoHashMapForSubBudget)
			throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Debug("*******linkNewAddedSubBudgetToBase******");
			// Insert Additional Line Items
			LOG_OBJECT.Debug("******Start linkSubBudgetToBase******");
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMapForSubBudget,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_LINK_SUB_BUDGET_TO_BASE, HHSConstants.JAVA_UTIL_MAP);
			LOG_OBJECT.Debug("******End linkSubBudgetToBase******");

			// Start Release 6 defect id 8480
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMapForSubBudget,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.INSERT_NEW_SUB_BUDGET_MODIFICATION_DETAILS_IN_AMENDMENT, HHSConstants.JAVA_UTIL_MAP);
			// Start Release 6 defect id 8529
			DAOUtil.masterDAO(aoMyBatisSession,
					Integer.parseInt((String) aoHashMapForSubBudget.get(HHSR5Constants.MOD_SUB_BUDGET_ID)),
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.INSERT_SUB_BUDGET_DETAILS_SITE_DETAILS_FOR_UPDATE, HHSConstants.INTEGER_CLASS_PATH);
			// End Release 6 defect id 8529
			// End Release 6 defect id 8480

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMapForSubBudget,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBY_LINK_PS_TO_BASE,
					HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMapForSubBudget,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBY_LINK_OPS_TO_BASE,
					HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMapForSubBudget,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_LINK_UTILITIES_TO_BASE, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMapForSubBudget,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBY_LINK_PFS_TO_BASE,
					HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMapForSubBudget,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBY_LINK_RENT_TO_BASE,
					HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMapForSubBudget,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBY_LINK_CS_TO_BASE,
					HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMapForSubBudget,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBY_LINK_RATE_TO_BASE,
					HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMapForSubBudget,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_LINK_MILESTONE_TO_BASE, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMapForSubBudget,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_LINK_UNALLOCATED_TO_BASE, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMapForSubBudget,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_LINK_INDIRECT_TO_BASE, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMapForSubBudget,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBY_LINK_PI_TO_BASE,
					HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMapForSubBudget,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_LINK_FRINGE_TO_BASE, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMapForSubBudget,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_LINK_EQUIPMENT_TO_BASE, HHSConstants.JAVA_UTIL_MAP);
			// Start: Added in R7 to include Site-details in new Sub-budget: Defect 8916
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMapForSubBudget,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.LINK_SITE_DETAILS_TO_BASE, HHSConstants.JAVA_UTIL_MAP);
			// end: Added in R7 to include Site-details in new Sub-budget
			// Start: Added in R7 for Cost-Center
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMapForSubBudget,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.CBY_LINK_SERVICES_TO_BASE, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMapForSubBudget,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.CBY_LINK_COSTCENTER_TO_BASE, HHSConstants.JAVA_UTIL_MAP);
			// End: Added in R7 for Cost-Center

		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("SubBudgetId passed: ", aoHashMapForSubBudget.get(HHSConstants.SUB_BUDGET_ID));
			LOG_OBJECT.Error("ApplicationException occured while executing query mergeLineItemReplica ", aoAppEx);
			throw aoAppEx;
		}

	}

	// Updated in R7 for Cost-Center
	/**
	 * This method is responsible for logical deletion of Modification Line Item
	 * records of a Modification Budget when contract budget modification review
	 * task is finished. It sets Active_flag=0 for all Line Items in a
	 * Modification Budget.Changes for agency outbound interafce 6644
	 * <ul>
	 * <li>Query used : markPersonnelServiceAsDeleted</li>
	 * <li>Query used : markOperationAndSupportAsDeleted</li>
	 * <li>Query used : markUtilitiesAsDeleted</li>
	 * <li>Query used : markProfessionalServiceAsDeleted</li>
	 * <li>Query used : markRentAsDeleted</li>
	 * <li>Query used : markMilestoneAsDeleted</li>
	 * <li>Query used : markUnallocatedAsDeleted</li>
	 * <li>Query used : markIndirectRateAsDeleted</li>
	 * <li>Query used : markProgramIncomeAsDeleted</li>
	 * <li>Query used : markFringeAsDeleted</li>
	 * <li>Query used : markEquipmentAsDeleted</li>
	 * 
	 * </ul>
	 * Updated in R7 for Cost-Center
	 * <li>Query Added : markServicesAsDeleted</li>
	 * <li>Query Added : markCostCenterAsDeleted</li>
	 * @param aoMybatisSession
	 * @param aoHashMap
	 * @throws ApplicationException
	 */

	private void markLineItemsForDeletion(SqlSession aoMyBatisSession, Map loHashMap) throws ApplicationException
	{
		try
		{

			// Logical deletoin of all Line Items for a Modification Budget
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBM_DELETE_PS_REPLICA,
					HHSConstants.JAVA_UTIL_MAP);
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBM_DELETE_OPS_REPLICA,
					HHSConstants.JAVA_UTIL_MAP);
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_DELETE_UTILITIES_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBM_DELETE_PFS_REPLICA,
					HHSConstants.JAVA_UTIL_MAP);
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_DELETE_RENT_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBM_DELETE_CS_REPLICA,
					HHSConstants.JAVA_UTIL_MAP);
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_DELETE_RATE_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_DELETE_MILESTONE_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_DELETE_UNALLOCATED_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_DELETE_INDIRECT_RATE_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_DELETE_PROGRAM_INCOME_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_DELETE_FRINGE_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_DELETE_EQUIPMENT_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			// Start: Added in R7 for Cost-Center
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.CBM_DELETE_SERVICE_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.CBM_DELETE_COSTCENTER_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			// End: Added in R7 for Cost-Center

		}
		catch (ApplicationException aoAppEx)
		{
			// changes for agency outbound interafce 6644
			aoAppEx.addContextData("SubBudgetId passed: ", loHashMap);
			LOG_OBJECT.Error("ApplicationException occured while executing query markLineItemsForDeletion ", aoAppEx);
			throw aoAppEx;
		}

	}

	/**
	 * This method is used for both BUdget Modification and Update Review Task
	 * This method is responsible for logical deletion of Modification/Update
	 * SubBudget and Budget when contract budget modification review task is
	 * finished. It sets Active_flag=0 for all Modification SubBudget and
	 * budget. Changes for agency outbound interafce 6644
	 * <ul>
	 * <li>Query used : markSubBudgetsAsDeleted</li>
	 * <li>Query used : markBudgetAsDeleted</li>
	 * 
	 * </ul>
	 * @param aoMybatisSession
	 * @param aoHashMap
	 * @throws ApplicationException
	 */

	private void markBudgetsForDeletion(SqlSession aoMyBatisSession, Map<String, String> aoHashMap)
			throws ApplicationException
	{
		try
		{

			// Logical deletoin of all Modification Sub_Budget
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_DELETE_SUBBUDGET_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			// Logical Deletion of Budget
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_DELETE_BUDGET_REPLICA, HHSConstants.JAVA_UTIL_MAP);

		}
		catch (ApplicationException aoAppEx)
		{
			// changes for agency outbound interafce 6644
			aoAppEx.addContextData("SubBudgetId passed: ", aoHashMap);
			LOG_OBJECT.Error("ApplicationException occured while executing query markBudgetsForDeletion ", aoAppEx);
			throw aoAppEx;
		}

	}

	/**
	 * This method is used for Update Review Task This method is responsible for
	 * logical deletion of Update Contract when contract budget update review
	 * task is finished. It sets Active_flag=0 changes for agency outbound
	 * interafce 6644
	 * Updated for Defect 9152: markContractFinacialsAsInactive query added to set contract_financials 
	 * entries as inactive.
	 * <ul>
	 * <li>Query used : markContractAsDeleted</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoContractId String
	 * @throws ApplicationException
	 */

	private void markContractForDeletion(SqlSession aoMyBatisSession, Map<String, String> aoHashMap)
			throws ApplicationException
	{
		try
		{
			// Logical deletoin of update contract
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_DELETE_CONTRACT_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			//Start: QC 9152 r 7.5.0 Accenture fix: Update Task (Auto-Approval) Never Finished
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.MARK_CONTRACT_FINANCIALS_INACTIVE, HHSConstants.JAVA_UTIL_MAP);
			//End: QC 9152 r 7.5.0 Accenture fix: Update Task (Auto-Approval) Never Finished
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("Contract Id passed: ", aoHashMap);
			LOG_OBJECT.Error("ApplicationException occured while executing query markContractForDeletion ", aoAppEx);
			throw aoAppEx;
		}

	}

	/**
	 * THis method is used for Budget Update Review Task It is responsible for
	 * overriding Base Budget and SubBudget Amount with Update Budget and
	 * subbudget when contract budget update review task is finished.
	 * <ul>
	 * <li>Query used : mergeSubBudgetForUpdate</li>
	 * <li>Query used : mergeBudgetForUpdate</li>
	 * <li>Query used : mergeContractForUpdate</li>
	 * <li>Query used : mergeContractFinancialForUpdate</li>
	 * </ul>
	 * @param aoMybatisSession
	 * @param aoHashMap
	 * @throws ApplicationException
	 */

	@SuppressWarnings("rawtypes")
	private void overrideBaseAmountForUpdation(SqlSession aoMyBatisSession, Map aoHashMap) throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Debug("Inside overrideBaseAmountForUpdation:");
			// start changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_UPDATE_BASE_SUBBUDGET_AMOUNT, HHSConstants.JAVA_UTIL_MAP);

			// Update Amount for Base Budget using Update Amount
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_UPDATE_BASE_BUDGET_AMOUNT, HHSConstants.JAVA_UTIL_MAP);
			// Update Amount for Base Contract using Update Amount
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_UPDATE_BASE_CONTRACT_AMOUNT, HHSConstants.JAVA_UTIL_MAP);

			// Update Contract financial amount for base contract
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_UPDATE_BASE_CONTRACT_FINANCIALS, HHSConstants.JAVA_UTIL_MAP);
			// end changes for agency outbound interafce 6644
			LOG_OBJECT.Debug("Exited overrideBaseAmountForUpdation:");
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("SubBudgetId passed: ", aoHashMap.get(HHSConstants.BUDGET_ID_WORKFLOW));
			LOG_OBJECT.Error("ApplicationException occured while executing query overrideBaseAmountForUpdation ",
					aoAppEx);
			throw aoAppEx;
		}

	}

	/**
	 * THis method is used for Budget Amendment Review Task It is responsible
	 * for overriding Base Budget and SubBudget Amount with Update Budget and
	 * subbudget when contract budget Amendment review task is finished.
	 * <ul>
	 * <li>Query used : mergeSubBudgetForUpdate</li>
	 * <li>Query used : mergeBudgetForUpdate</li>
	 * <li>Query used : mergeContractForAmendment</li>
	 * <li>Query used : updateContractEndDate</li>
	 * </ul>
	 * @param aoMybatisSession
	 * @param aoHashMap
	 * @throws ApplicationException
	 */

	@SuppressWarnings("rawtypes")
	private void overrideBaseAmountForAmendment(SqlSession aoMyBatisSession, Map aoHashMap,
			Integer aoApprovedBudgetCount, Boolean isAmendmentRegisteredInFMS, List<String> loAmendmentBudgetIds)
			throws ApplicationException
	{   //System.out.println(" ======ContractBudgetModificationService :: overrideBaseAmountForAmendment====");
		try
		{
			System.out.println("\n ======ContractBudgetModificationService :: " +
					 "  aoApprovedBudgetCount :: "+aoApprovedBudgetCount +
					 "  isAmendmentRegisteredInFMS :: "+ isAmendmentRegisteredInFMS+
					 "  loAmendmentBudgetIds :: " + loAmendmentBudgetIds);
			// Update Amount for Base Sub_Budget using Amendment Amount
			// start changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_UPDATE_BASE_SUBBUDGET_AMOUNT, HHSConstants.JAVA_UTIL_MAP);

			// Update Amount for Base Budget using Amendment Amount
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_UPDATE_BASE_BUDGET_AMOUNT, HHSConstants.JAVA_UTIL_MAP);
			// Update Amount for Base Contract using Update Amount if this is
			// first budget under Review
			// end changes for agency outbound interafce 6644
			
			if (aoApprovedBudgetCount == HHSConstants.INT_ZERO)
			{
				System.out.println("\n ========ContractBudgetModificationService :: " +
		                 "  aoApprovedBudgetCount == 0 " +
						"[overrideBaseAmountForAmendment] - ContractId: No budget  \n");
				overrideBaseAmountForAmendmentInContract(aoMyBatisSession, aoHashMap, isAmendmentRegisteredInFMS,
						loAmendmentBudgetIds);
			}

		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("SubBudgetId passed: ", aoHashMap.get(HHSConstants.BUDGET_ID_WORKFLOW));
			LOG_OBJECT.Error("ApplicationException occured while executing query overrideBaseAmountForUpdation ",
					aoAppEx);
			throw aoAppEx;
		}

	}

	/**
	 * Release 3.6.0 Enhancement id 6263
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @throws ApplicationException
	 */
	private void overrideBaseAmountForAmendmentInContract(SqlSession aoMyBatisSession, Map aoHashMap,
			Boolean isAmendmentRegisteredInFMS, List<String> loAmendmentBudgetIds) throws ApplicationException
	{   
		

		if (isAmendmentRegisteredInFMS && isAmendmentRegisteredInFMS != null 
				&& loAmendmentBudgetIds != null
				&& !loAmendmentBudgetIds.isEmpty()
				)
		{
			
			System.out.println("\n ***********ContractBudgetModificationService :: " +
					"[overrideBaseAmountForAmendmentInContract]isAmendmentRegisteredInFMS=" + isAmendmentRegisteredInFMS + 
					";loAmendmentBudgetIds:'" + loAmendmentBudgetIds + "'.\n");

			/*
			 * //start release 3.14.0 //start changes for agency outbound
			 * interafce 6644 DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
			 * HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
			 * HHSConstants
			 * .CBM_AMEND_BASE_CONTRACT_AMOUNT_FOR_FMS_REGISTERED_CONTRACT,
			 * HHSConstants.JAVA_UTIL_MAP);
			 */
			// Update Base Contract End date as proposed end date of
			// Amendment Contract
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_UPDATE_BASE_CONTRACT_END_DATE, HHSConstants.JAVA_UTIL_MAP);
			
			/*
			 * // Update Contract financial amount for base contract //changes
			 * for agency outbound interafce 6644
			 * DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
			 * HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
			 * HHSConstants.
			 * CBM_UPDATE_BASE_CONTRACT_FINANCIAL_AMOUNT_FOR_FMS_REGISTERED_CONTRACT
			 * , HHSConstants.JAVA_UTIL_MAP); //end changes for agency outbound
			 * interafce 6644 //end release 3.14.0
			 */
		}
		else
		{
			System.out.println("\n************ContractBudgetModificationService :: " +
					"[overrideBaseAmountForAmendmentInContract]mergeAmendment INto ContractAmountFor=.\n");
			// start changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_AMEND_BASE_CONTRACT_AMOUNT, HHSConstants.JAVA_UTIL_MAP);
			System.out.println("\n***********ContractBudgetModificationService :: " +
			"[overrideBaseAmountForAmendmentInContract]    mergeContractForAmendment=.\n");
			// Update Base Contract End date as proposed end date of
			// Amendment Contract
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_UPDATE_BASE_CONTRACT_END_DATE, HHSConstants.JAVA_UTIL_MAP);
			
			// Update Contract financial amount for base contract
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_UPDATE_BASE_CONTRACT_FINANCIAL_AMOUNT, HHSConstants.JAVA_UTIL_MAP);
			// end changes for agency outbound interafce 6644
		}
	}

	/**
	 * This method is used for both BUdget Update and Amendment Review Task It
	 * is used for linking newly added Documents with Base Budget and Base
	 * Contract when contract budget Update/Amendment review task is finished.
	 * <ul>
	 * <li>Query used : mergeBudgetUpdateDocument</li>
	 * <li>Query used : mergeContractUpdateDocument</li>
	 * </ul>
	 * @param aoMybatisSession
	 * @param aoHashMap
	 * @throws ApplicationException
	 */

	private void mergeDocumentsForUpdation(SqlSession aoMyBatisSession, Map aoHashMap) throws ApplicationException
	{
		try
		{
			// Merge update Budget Documents with Base Budget
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.MERGE_BUDGET_UPDATE_DOCUMENT, HHSConstants.JAVA_UTIL_MAP);
			setMoState("Transaction Success:: ContractBudgetModificationService:mergeBudgetUpdateDocument method - success to update record "
					+ " \n");
			mergeDocumentsForUpdationInContract(aoMyBatisSession, aoHashMap);

		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("SubBudgetId passed: ", aoHashMap.get(HHSConstants.BUDGET_ID_WORKFLOW));
			LOG_OBJECT.Error("ApplicationException occured while executing query mergeDocumentsForUpdation ", aoAppEx);
			throw aoAppEx;
		}

	}

	/**
	 * Release 3.6.0 Enhancement id 6263
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @throws ApplicationException
	 */
	private void mergeDocumentsForUpdationInContract(SqlSession aoMyBatisSession, Map aoHashMap)
			throws ApplicationException
	{
		// Merge update Contract Documents with Base Contract
		DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
				HHSConstants.MERGE_CONTRACT_UPDATE_DOCUMENT, HHSConstants.JAVA_UTIL_MAP);
		setMoState("Transaction Success:: ContractBudgetModificationService:mergeBudgetUpdateDocument method - success to update record "
				+ " \n");
	}

	/**
	 * This method is used to merge the documents upload for modification back
	 * to original budget for approval of contract budget modification
	 * <ul>
	 * <li>get the parent budget id</li>
	 * <li>override the modification budget id in budget_document with parent
	 * budget id</li>
	 * <li>Query used : fetchParentBudgetId</li>
	 * <li>Query used : mergeBudgetModificationDocument</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession session object
	 * @param aoFinalFinish Boolean
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @param asBudgetStatus String
	 * @throws ApplicationException ApplicationException object
	 * @return loHashMap Map object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Map mergeBudgetModificationDocument(SqlSession aoMyBatisSession, Boolean aoFinalFinish,
			TaskDetailsBean aoTaskDetailsBean, String asBudgetStatus) throws ApplicationException
	{
		Map loHashMap = null;
		try
		{
			if (aoFinalFinish)
			{
				loHashMap = new HashMap<String, String>();
				loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
				loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoTaskDetailsBean.getBudgetId());
				loHashMap.put(HHSConstants.STATUS_ID, asBudgetStatus);
				loHashMap.put(HHSConstants.MODIFY_BY, aoTaskDetailsBean.getUserId());
				String lsParentBudgetId = (String) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_PARENT_BUDGET_ID, HHSConstants.JAVA_UTIL_MAP);
				if (lsParentBudgetId == null)
				{
					setMoState("Transaction Failed:: ContractBudgetModificationService:mergeBudgetModificationDocument method - Unable to fetch parent budget id for budget id "
							+ aoTaskDetailsBean.getBudgetId() + " \n");
					throw new ApplicationException(
							"Unable to fetch parent budget id for budget in mergeBudgetModificationDocument method");
				}

				loHashMap.put(HHSConstants.PARENT_BUDGET_ID, lsParentBudgetId);
				loHashMap.put(HHSConstants.ENTITY_ID, aoTaskDetailsBean.getBudgetId());
				loHashMap.put(HHSConstants.ENTITY_TYPE, aoTaskDetailsBean.getTaskName());
				loHashMap.put(HHSConstants.USER_ID, aoTaskDetailsBean.getUserId());
				loHashMap.put(HHSConstants.NEW_ENTITY_ID, lsParentBudgetId);
				loHashMap.put(HHSConstants.NEW_ENTITY_TYPE, HHSConstants.TASK_BUDGET_REVIEW);
				DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.MERGE_BUDGET_MODIFICATION_DOCUMENT, HHSConstants.JAVA_UTIL_MAP);
				setMoState("Transaction Success:: ContractBudgetModificationService:mergeBudgetModificationDocument method - success to update record "
						+ " \n");
			}
			return loHashMap;
		}
		// catch any application exception thrown from the code due to UPDATE
		// statement and throw it
		// forward
		catch (ApplicationException aoAppEx)
		{
			setMoState("Transaction Failed:: ContractBudgetModificationService:"
					+ "mergeBudgetModificationDocument method - failed to update record " + " \n");
			aoAppEx.addContextData("BudgetId passed: ", aoTaskDetailsBean.getBudgetId());
			LOG_OBJECT.Error("ApplicationException occured while executing query mergeBudgetModificationDocument ",
					aoAppEx);
			throw aoAppEx;
		}
		// catch any exception thrown from the code due to UPDATE
		// statement and throw it
		// forward
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in mergeBudgetModificationDocument ", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService:"
					+ "mergeBudgetModificationDocument method - failed to update record " + " \n");
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while executing query in mergeBudgetModificationDocument", aoAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method updated for release 3.3.0 for defect 6444. This method
	 * validates the Base Unit count for a particular line-item and returns
	 * FALSE if the #units user is trying to update tries to make the total
	 * #units for a line-item to fall below zero, else it validates it as TRUE
	 * <ul>
	 * <li>Query used : getBaseUnitForPersonnelService</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget
	 * @return loIsValid Boolean
	 * @throws ApplicationException ApplicationException object
	 */
	private Boolean validateModificationUnitForPersonnelServices(SqlSession aoMybatisSession,
			PersonnelServiceBudget aoPersonnelServiceBudgetBean) throws ApplicationException
	{
		Boolean loIsValid = true;
		try
		{
			// No validation for adding new row
			if (!aoPersonnelServiceBudgetBean.getId().equalsIgnoreCase(HHSConstants.NEW_ROW_IDENTIFIER)
					&& Double.parseDouble(aoPersonnelServiceBudgetBean.getModificationUnit()) < HHSConstants.INT_ZERO)
			{
				// Start of changes for release 3.3.0 for defect 6444
				// type casting to Double instead of Integer
				Double loBaseUnit = (Double) DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBF_GET_BASE_UNIT_PERSONNEL_SERVICE, HHSConstants.PERSONNEL_SERVICE_BUDGET);

				loBaseUnit = ((loBaseUnit == null) ? HHSConstants.DOUBLE_DECIMAL_ZERO : loBaseUnit);
				// End of changes for release 3.3.0 for defect 6444
				if (loBaseUnit + Double.parseDouble((aoPersonnelServiceBudgetBean.getModificationUnit())) < HHSConstants.INT_ZERO)
				{
					loIsValid = false;
				}
			}
			else if (aoPersonnelServiceBudgetBean.getId().equalsIgnoreCase(HHSConstants.NEW_ROW_IDENTIFIER)
					&& Double.parseDouble(aoPersonnelServiceBudgetBean.getModificationUnit()) < HHSConstants.INT_ZERO)
			{
				loIsValid = false;
			}
		}
		// catch any application exception thrown from the code due to SELECT
		// statement failure and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("PersonnelServiceBudget bean : ", aoPersonnelServiceBudgetBean);
			LOG_OBJECT.Error("Exception while validating modified units", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService:validateModificationUnitForPersonnelServices method"
					+ " \n");
			throw loAppEx;
		}
		return loIsValid;
	}

	/**
	 * <ul>
	 * The method is updated in Release 7. Added a parameter for showing approved modification.
	 * <li>This method is used to fetch data for
	 * ContractServices,ContractServices
	 * Invoicing,ContractServicesModification,ContractServicesUpdate &
	 * ContractServicesAmendment Screen</li>
	 * <li>For Contracted Services & Contracted Services Modification Screen</li>
	 * <ol>
	 * <li>OTPS Contracted Services grid column will be editable</li>
	 * <ul>
	 * <li>Editable field only for new rows that are added from within the
	 * Modification budget.</li>
	 * <li>If the row was a part of the approved budget at the point the
	 * Modification budget was created, the field will be read only</li>
	 * </ul>
	 * <li>Description of Service grid column will be editable</li>
	 * <ul>
	 * <li>Editable field only for new rows that are added from within the
	 * Modification budget.</li>
	 * <li>If the row was a part of the approved budget at the point the
	 * Modification budget was created, the field will be read only</li>
	 * </ul>
	 * <li>Approved FY Budget column is read only</li>
	 * <li>For Modification Amount column</li>
	 * <ul>
	 * <li>Verify upon save that the Modification Amount entered</li>
	 * <li>would not cause the Total Proposed Budget for the line item to fall
	 * below the YTD Invoiced Amount.</li>
	 * <li>If it would, display error message: "!</li>
	 * <li>Entered value would cause the Proposed Budget to fall below the
	 * amount already invoiced for the line item. Please enter a new value."</li>
	 * </ul>
	 * </ol>
	 * <li>Query used : fetchContractedServicesModificationConsultants</li>
	 * <li>Query used : fetchContractedServicesNewModificationConsultants</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession sql session
	 * @param aoCBGridBeanObj CBGridBean object
	 * @param aoMasterBean - MasterBean object
	 * @return loNewCBContractedServicesBean
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<ContractedServicesBean> fetchContractedServicesModificationConsultants(SqlSession aoMyBatisSession,
			CBGridBean aoCBGridBeanObj, MasterBean aoMasterBean) throws ApplicationException
	{
		List<ContractedServicesBean> loCBContractedServicesBean = null;
		List<ContractedServicesBean> loNewCBContractedServicesBean = null;
		String lsParentSUbBudgetId = aoCBGridBeanObj.getParentSubBudgetId();
		try
		{
			// Start R7 changes ::Fetch the data from FileNet XML if Budget Status is approved
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				loNewCBContractedServicesBean = new ArrayList<ContractedServicesBean>();
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBContractedServicesBean = new ContractBudgetAmendmentService().fetchContractedServicesConsultantsFromXML(lsSubBudgetId, aoMasterBean);
				
		        /*[Start] R7.3.2 add null exception handling*/
				if(loCBContractedServicesBean == null) return null;
			    /*[End] R7.3.2 add null exception handling*/

				loNewCBContractedServicesBean.addAll(loCBContractedServicesBean);
			}
			else
			{
				// R7 end
				aoCBGridBeanObj.setSubHeader(HHSConstants.ONE);
				loCBContractedServicesBean = (List<ContractedServicesBean>) DAOUtil.masterDAO(aoMyBatisSession,
						aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBY_FETCH_CONTRACTED_SERVICES_MODIFICATION_CONSULTANTS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				loNewCBContractedServicesBean = (List<ContractedServicesBean>) DAOUtil.masterDAO(aoMyBatisSession,
						aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBY_FETCH_CONTRACTED_SERVICES_NEW_MODIFICATION_CONSULTANTS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				appendNewRecord(loNewCBContractedServicesBean, lsParentSUbBudgetId);
				loNewCBContractedServicesBean.addAll(loCBContractedServicesBean);
			}

		}
		// Application Exception Handled Here.
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACTID, aoCBGridBeanObj.getContractID());
			LOG_OBJECT.Error("Exception occured in ContractBudgetModificationService:"
					+ " fetchContractedServicesModificationConsultants method:: ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService:"
					+ " fetchContractedServicesModificationConsultants method - "
					+ "failed Exception occured while fetching\n");
			throw loAppEx;
		}

		return loNewCBContractedServicesBean;
	}

	/**
	 * The method is updated in Release 7. Added a parameter for showing approved modification.
	 * This method is used to fetch data for sub-contractors for contracted
	 * services
	 * <ul>
	 * <li>Query used : fetchContractedServicesModificationSubContractors</li>
	 * <li>Query used : fetchContractedServicesNewModificationConsultants</li>
	 * </ul>
	 * @param aoMyBatisSession sql session
	 * @param aoCBGridBeanObj CBGridBean object
	 * @return loNewCBContractedServicesBean
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<ContractedServicesBean> fetchContractedServicesModificationSubContractors(SqlSession aoMyBatisSession,
			CBGridBean aoCBGridBeanObj, MasterBean aoMasterBean) throws ApplicationException
	{
		List<ContractedServicesBean> loCBContractedServicesBean = null;
		List<ContractedServicesBean> loNewCBContractedServicesBean = null;
		String lsParentSUbBudgetId = aoCBGridBeanObj.getParentSubBudgetId();
		try
		{
			// Start R7 changes ::Fetch the data from FileNet XML if Budget Status is approved
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				loNewCBContractedServicesBean = new ArrayList<ContractedServicesBean>();
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBContractedServicesBean = new ContractBudgetAmendmentService()
						.fetchContractedServicesSubContractorsFromXML(lsSubBudgetId, aoMasterBean);
				
		        /*[Start] R7.3.2 add null exception handling*/
				if( loCBContractedServicesBean == null ) return new ArrayList <ContractedServicesBean>();
		        /*[End] R7.3.2 add null exception handling*/
				
				loNewCBContractedServicesBean.addAll(loCBContractedServicesBean);
			}
			else
			{
				// R7 end
				aoCBGridBeanObj.setSubHeader(HHSConstants.TWO);
				loCBContractedServicesBean = (List<ContractedServicesBean>) DAOUtil.masterDAO(aoMyBatisSession,
						aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBY_FETCH_CONTRACTED_SERVICES_MODIFICATION_SUB_CONTRACTORS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				loNewCBContractedServicesBean = (List<ContractedServicesBean>) DAOUtil.masterDAO(aoMyBatisSession,
						aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBY_FETCH_CONTRACTED_SERVICES_NEW_MODIFICATION_CONSULTANTS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				appendNewRecord(loNewCBContractedServicesBean, lsParentSUbBudgetId);
				loNewCBContractedServicesBean.addAll(loCBContractedServicesBean);
			}

		}
		// Application Exception Handled Here.
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACTID, aoCBGridBeanObj.getContractID());
			LOG_OBJECT.Error("Exception occured in ContractBudgetModificationService:"
					+ " fetchContractedServicesModificationSubContractors method:: ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService:"
					+ " fetchContractedServicesModificationSubContractors method - "
					+ "failed Exception occured while fetching\n");
			throw loAppEx;
		}

		return loNewCBContractedServicesBean;
	}

	/**
	 * The method is updated in Release 7. Added a parameter for showing approved modification.
	 * This method is used to fetch data for vendors for contracted services
	 * <ul>
	 * <li>Query used : fetchContractedServicesModificationVendors</li>
	 * <li>Query used : fetchContractedServicesNewModificationConsultants</li>
	 * </ul>
	 * @param aoMyBatisSession sql session
	 * @param aoCBGridBeanObj CBGridBean
	 * @return loNewCBContractedServicesBean
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<ContractedServicesBean> fetchContractedServicesModificationVendors(SqlSession aoMyBatisSession,
			CBGridBean aoCBGridBeanObj, MasterBean aoMasterBean) throws ApplicationException
	{
		List<ContractedServicesBean> loCBContractedServicesBean = null;
		List<ContractedServicesBean> loNewCBContractedServicesBean = null;
		String lsParentSUbBudgetId = aoCBGridBeanObj.getParentSubBudgetId();
		try
		{
			// Start R7 changes ::Fetch the data from FileNet XML if Budget Status is approved
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				loNewCBContractedServicesBean = new ArrayList<ContractedServicesBean>();
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBContractedServicesBean = fetchContractedServicesConsultantsModFromXML(lsSubBudgetId, aoMasterBean);
				
		        /*[Start] R7.3.2 add null exception handling*/
				if( loCBContractedServicesBean == null  )  return new ArrayList<ContractedServicesBean>();
                /*[End] R7.3.2 add null exception handling*/

				loNewCBContractedServicesBean.addAll(loCBContractedServicesBean);
			}
			else
			{
				// R7 end
				aoCBGridBeanObj.setSubHeader(HHSConstants.THREE);
				loCBContractedServicesBean = (List<ContractedServicesBean>) DAOUtil.masterDAO(aoMyBatisSession,
						aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBY_FETCH_CONTRACTED_SERVICES_MODIFICATION_VENDORS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				loNewCBContractedServicesBean = (List<ContractedServicesBean>) DAOUtil.masterDAO(aoMyBatisSession,
						aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBY_FETCH_CONTRACTED_SERVICES_NEW_MODIFICATION_CONSULTANTS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				appendNewRecord(loNewCBContractedServicesBean, lsParentSUbBudgetId);
				loNewCBContractedServicesBean.addAll(loCBContractedServicesBean);
			}
		}
		// Application Exception Handled Here.
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACTID, aoCBGridBeanObj.getContractID());
			LOG_OBJECT.Error("Exception occured in ContractBudgetModificationService:"
					+ " fetchContractedServicesModificationVendors method:: ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService:"
					+ " fetchContractedServicesModificationVendors method - failed Exception occured while fetching\n");
			throw loAppEx;
		}

		return loNewCBContractedServicesBean;
	}

	/*
	 * [Start] R7.3.2 Emergency release Approved MOD doesn't populated value in Contracted Service
	 * */
	/**
     * The method is updated to R7.3.2 Emergency releas. Create method for approved modifications only with out re-use. 
     * This method fetch ContractedServicesBean list from XML
     * 
     * @param asSubBudgetId String
     * @param aoMasterBean MasterBean
     * @return loReturnedList List<ContractedServicesBean>
     * @throws ApplicationException If an Application Exception occurs
     */
    public List<ContractedServicesBean> fetchContractedServicesConsultantsModFromXML(String asSubBudgetId,
            MasterBean aoMasterBean) throws ApplicationException
    {
        List<ContractedServicesBean> loReturnedList = null;
        loReturnedList = generateContractedServicesList(asSubBudgetId, aoMasterBean);
        loReturnedList = fetchListForContractedServices(loReturnedList);
        return loReturnedList;
    }
    
    /**
     * This method fetch consolidated list of ContractedServicesBean which
     * includes all type of Employees from XML
     * 
     * @param asSubBudgetId String
     * @param aoMasterBean MasterBean
     * @return loReturnedList List<ContractedServicesBean>
     * @throws ApplicationException If an Application Exception occurs
     */

    private List<ContractedServicesBean> generateContractedServicesList(String asSubBudgetId, MasterBean aoMasterBean)
            throws ApplicationException
    {
        List<LineItemMasterBean> loMasterBeanList = null;
        List<ContractedServicesBean> loReturnedList = null;

        loMasterBeanList = aoMasterBean.getMasterBeanList();
        Iterator<LineItemMasterBean> aoListIterator = loMasterBeanList.iterator();
        while (aoListIterator.hasNext())
        {
            LineItemMasterBean loLineItemBean = aoListIterator.next();
            if (loLineItemBean.getSubbudgetId().equals(asSubBudgetId))
            {
                loReturnedList = loLineItemBean.getContractedserviceBeanList();
            }
        }
        return loReturnedList;
    }

    /**
     * This method fetch records of only type ContractedServicesBean from the
     * consolidated list
     * 
     * @param aoContractedServicesBeanList List<ContractedServicesBean>
     * @return loContractedServicesBeanList List<ContractedServicesBean>
     */
    private List<ContractedServicesBean> fetchListForContractedServices(
            List<ContractedServicesBean> aoContractedServicesBeanList) throws ApplicationException
    {
        if( aoContractedServicesBeanList == null || aoContractedServicesBeanList.isEmpty() ) 
            return new ArrayList<ContractedServicesBean>();

        List<ContractedServicesBean> loContractedServicesBeanList = null;
        if (aoContractedServicesBeanList != null)
        {
            Iterator<ContractedServicesBean> aoListIterator = aoContractedServicesBeanList.iterator();
            loContractedServicesBeanList = new ArrayList<ContractedServicesBean>();
            while (aoListIterator.hasNext())
            {
                ContractedServicesBean loContractedServicesBeanBean = aoListIterator.next();
                if (loContractedServicesBeanBean.getSubHeader().equals(HHSConstants.THREE))
                {
                    loContractedServicesBeanList.add(loContractedServicesBeanBean);
                }
            }
        }
        return loContractedServicesBeanList;
    }

/*
 * [End] R7.3.2 Emergency release Approved MOD doesn't populated value in Contracted Service
 * */

	/**
	 * <ul>
	 * <li>This method is used to add data for
	 * ContractServices,ContractServicesModification,ContractServicesUpdate &
	 * ContractServicesAmendment Screen</li>
	 * <li>This method is invoked when gridOperation is performed for
	 * getContractedServicesAdd Transaction Id</li>
	 * <li>For Contracted Services & Contracted Services Modification Screen</li>
	 * <ol>
	 * <li>By default, the field will be blank when a new row is added</li>
	 * </ol>
	 * <li>Query used : addContractedServicesModification</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Sql Session object
	 * @param aoCBGridBeanObj ContractedServicesBean object
	 * @return loVal Integer object
	 * @throws ApplicationException ApplicationException object
	 */
	public Integer addContractedServicesModification(SqlSession aoMyBatisSession, ContractedServicesBean aoCBGridBeanObj)
			throws ApplicationException
	{
		Integer loVal = null;
		@SuppressWarnings("rawtypes")
		HashMap loHashMap = null;
		Boolean loError = false;
		try
		{
			if (aoCBGridBeanObj != null)

			{
				loHashMap = validateContractedServicesModificationData(aoMyBatisSession, aoCBGridBeanObj);
				if ((Boolean) loHashMap.get(HHSConstants.IS_VALID_DATA))
				{
					loVal = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBY_ADD_CONTRACTED_SERVICES_MODIFICATION,
							HHSConstants.CBY_COM_NYC_HHS_MODEL_CONTRACTED_SERVICES_BEAN);
				}
				else
				{
					loError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							(String) loHashMap.get(HHSConstants.ERROR_MESSAGE)));
				}
			}
		}
		// Application Exception Handled Here.
		catch (ApplicationException loAppEx)
		{
			if (loError)
			{
				// In case of modification data validation failure, the
				// exception will be thrown back to base controller
				// along with the validation message
				loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loAppEx.toString());
			}
			loAppEx.addContextData(HHSConstants.CONTRACTID, aoCBGridBeanObj.getContractID());
			LOG_OBJECT.Error("Exception occured in ContractBudgetModificationService:"
					+ " addContractedServicesModification method:: ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService:"
					+ " addContractedServicesModification method - failed Exception occured while inserting data\n");
			throw loAppEx;
		}

		return loVal;
	}

	/**
	 * <ol>
	 * <li>This method is used to edit data for
	 * ContractServices,ContractServicesModification,ContractServicesUpdate &
	 * ContractServicesAmendment Screen</li>
	 * <li>This method is invoked when gridOperation is performed for
	 * getContractedServicesEdit Transaction Id</li>
	 * <li>Query used : fetchInsertContractedServicesModification</li>
	 * <li>Query used : updateContractedServicesModification</li>
	 * <li>Query used : editContractedServicesModification</li>
	 * </ol>
	 * 
	 * @param aoMyBatisSession SqlSession object
	 * @param aoCBGridBeanObj ContractedServicesBean object
	 * @return loStatus Boolean returned as output
	 * @throws ApplicationException ApplicationException object
	 */
	public boolean editContractedServicesModification(SqlSession aoMyBatisSession,
			ContractedServicesBean aoCBGridBeanObj) throws ApplicationException
	{
		Integer loVal = null;
		@SuppressWarnings("rawtypes")
		HashMap loHashMap = null;
		Boolean loStatus = false;
		ContractedServicesBean loCsBean = null;
		Boolean loError = false;
		try
		{

			if (aoCBGridBeanObj != null)
			{
				loHashMap = validateContractedServicesModificationData(aoMyBatisSession, aoCBGridBeanObj);
				if ((Boolean) loHashMap.get(HHSConstants.IS_VALID_DATA))
				{

					loCsBean = (ContractedServicesBean) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBY_FETCH_INSERT_CONTRACTED_SERVICES_MODIFICATION,
							HHSConstants.CBY_COM_NYC_HHS_MODEL_CONTRACTED_SERVICES_BEAN);
					if (null == aoCBGridBeanObj.getCsName())
					{
						aoCBGridBeanObj.setCsName(loCsBean.getCsName());
					}
					if (null == aoCBGridBeanObj.getDescOfService())
					{
						aoCBGridBeanObj.setDescOfService(loCsBean.getDescOfService());
					}
					if (null == aoCBGridBeanObj.getFyBudget())
					{
						aoCBGridBeanObj.setFyBudget(loCsBean.getFyBudget());
					}

					loVal = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBY_UPDATE_CONTRACTED_SERVICES_MODIFICATION,
							HHSConstants.CBY_COM_NYC_HHS_MODEL_CONTRACTED_SERVICES_BEAN);
					if (loVal < HHSConstants.INT_ONE)
					{
						DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
								HHSConstants.CBY_EDIT_CONTRACTED_SERVICES_MODIFICATION,
								HHSConstants.CBY_COM_NYC_HHS_MODEL_CONTRACTED_SERVICES_BEAN);

					}
					loStatus = true;

				}
				else
				{
					loError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							(String) loHashMap.get(HHSConstants.ERROR_MESSAGE)));
				}
			}
		}
		// Application Exception Handled Here.
		catch (ApplicationException loAppEx)
		{

			if (loError)
			{
				// In case of modification data validation failure, the
				// exception will be thrown back to base controller
				// along with the validation message
				loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loAppEx.toString());
			}
			loAppEx.addContextData(HHSConstants.CONTRACTID, aoCBGridBeanObj.getContractID());
			LOG_OBJECT.Error("Exception occured in ContractBudgetModificationService: "
					+ "editContractedServicesModification method:: ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: "
					+ "editContractedServicesModification method - failed Exception occured while updating data\n");
			throw loAppEx;
		}
		return loStatus;
	}

	/**
	 * <ol>
	 * <li>This method is used to delete data for
	 * ContractServices,ContractServicesModification,ContractServicesUpdate &
	 * ContractServicesAmendment Screen</li>
	 * <li>This method is invoked when gridOperation is performed for
	 * getContractedServicesDelete Transaction Id</li>
	 * <li>Query used : delContractedServicesModification</li>
	 * </ol>
	 * 
	 * @param aoMyBatisSession SqlSession Object
	 * @param aoCBGridBeanObj ContractedServicesBean Object
	 * @return boolean loStatus
	 * @throws ApplicationException ApplicationException object
	 */
	public boolean deleteContractedServicesModification(SqlSession aoMyBatisSession,
			ContractedServicesBean aoCBGridBeanObj) throws ApplicationException
	{
		Boolean loStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_DEL_CONTRACTED_SERVICES_MODIFICATION,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CONTRACTED_SERVICES_BEAN);
			loStatus = true;
		}
		// Application Exception Handled Here.
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACTID, aoCBGridBeanObj.getContractID());
			LOG_OBJECT.Error("Exception occured in ContractBudgetModificationService: "
					+ "deleteContractedServicesModification method:: ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: "
					+ "deleteContractedServicesModification method - failed Exception occured while deleting data\n");
			throw loAppEx;
		}
		return loStatus;
	}

	/**
	 * <li>This method is used to append _newRecord for all the rows that are
	 * added at the time of budget modification.</li>
	 * @param aoCBContractedServicesBean ContractedServicesBean List Object
	 * @param asParentSubBudgetId String returned as Output
	 */
	private void appendNewRecord(List<ContractedServicesBean> aoCBContractedServicesBean, String asParentSubBudgetId)
	{
		StringBuffer loSBConcat = null;
		if (null != aoCBContractedServicesBean && aoCBContractedServicesBean.size() > HHSConstants.INT_ZERO)
		{
			for (ContractedServicesBean loCsBeanObj : aoCBContractedServicesBean)
			{
				loSBConcat = new StringBuffer();
				if (!(loCsBeanObj.getSubBudgetID().equals(asParentSubBudgetId)))
				{
					loSBConcat.append(loCsBeanObj.getId());
					loSBConcat.append(HHSConstants.NEW_RECORD_CONTRACT_SERVICES);
					loCsBeanObj.setId(loSBConcat.toString());
				}
			}
		}
	}

	/**
	 * Method validates the remaining amount for a particular line-item and
	 * returns FALSE if the modify amount is greater then the remaining amount,
	 * else it validates it as TRUE
	 * <ul>
	 * <li>Query used : getRemainingAmountModificationContractedServices</li>
	 * </ul>
	 * @param aoMybatisSession Session object
	 * @param aoContractedServicesObj ContractedServices object
	 * @return loHashMap Hash map returning the value
	 * @throws ApplicationException Application exception handled
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private HashMap validateContractedServicesModificationData(SqlSession aoMybatisSession,
			ContractedServicesBean aoContractedServicesObj) throws ApplicationException
	{
		Boolean loIsDataValid = true;
		HashMap loHMContactService = new HashMap();
		BigDecimal loRemainingAmount = null;
		loHMContactService.put(HHSConstants.IS_VALID_DATA, loIsDataValid);
		try
		{
			// validates the modification amount so that it should not fall
			// below YTD Amount
			if (loIsDataValid && aoContractedServicesObj.getId() != null
					&& !aoContractedServicesObj.getId().contains(HHSConstants.NEW_ROW_IDENTIFIER))
			{
				loRemainingAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoContractedServicesObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.QRY_GET_REMAINING_AMOUNT_MODIFICATION_CONTRACTED_SERVICES,
						HHSConstants.CBY_COM_NYC_HHS_MODEL_CONTRACTED_SERVICES_BEAN);
			}
			loRemainingAmount = ((loRemainingAmount == null) ? BigDecimal.ZERO : loRemainingAmount);
			if (aoContractedServicesObj.getModificationAmt() != null
					&& !((loRemainingAmount.add(new BigDecimal((aoContractedServicesObj.getModificationAmt()))))
							.compareTo(BigDecimal.ZERO) >= HHSConstants.INT_ZERO))
			{
				loHMContactService.put(HHSConstants.IS_VALID_DATA, Boolean.FALSE);
				// Error message thrown for the Proposed budget, we are using
				// the rate validation message as the error message is same
				loHMContactService.put(HHSConstants.ERROR_MESSAGE,
						HHSConstants.BUDGET_MODIFICATION_RATE_AMNT_VALIDATION);
			}
		}
		/**
		 * Application Exception handled here
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.CBM_NEW_CONTRACTED_SERVICES, aoContractedServicesObj);
			LOG_OBJECT.Error("Exception while validating remaining amount", loExp);
			setMoState("Transaction Failed:: ContractBudgetModificationService:"
					+ "validateContractedServicesModificationData method" + " \n");
			throw loExp;
		}
		return loHMContactService;
	}

	/**
	 * * The method is updated in Release 7. Added a parameter for showing approved modification.
	 * <li>This method is used to fetch non-grid data for budget contracted
	 * services grid.</li>
	 * <ul>
	 * <li>Query used : fetchNonGridContractedServices</li>
	 * </ul>
	 * @param aoMyBatisSession SqlSession object
	 * @param aoCBGridBeanObj CBGridBean object
	 * @return ContractedServicesBean
	 * @throws ApplicationException ApplicationException object
	 */
	public ContractedServicesBean fetchNonGridContractedServicesModification(SqlSession aoMyBatisSession,
			CBGridBean aoCBGridBeanObj, MasterBean aoMasterBean) throws ApplicationException
	{
		ContractedServicesBean loContractedServicesBean = new ContractedServicesBean();
		try
		{
			// Start R7 changes ::Fetch the data from FileNet XML if Budget Status is approved
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loContractedServicesBean = new ContractBudgetAmendmentService().fetchContractedServicesBeanDataFromXML(
						lsSubBudgetId, aoMasterBean);
			}
			else
			{
				// R7 end
			loContractedServicesBean = (ContractedServicesBean) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_FETCH_NON_GRID_CONTRACTED_SERVICES, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			}
		}
		/**
		 * Application Exception handled here
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CBM_NEW_CONTRACTED_SERVICES, aoCBGridBeanObj);
			LOG_OBJECT.Error(
					"Exception occured in ContractBudgetModificationService: fetchNonGridContractedServices method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: fetchNonGridContractedServices"
					+ " method - failed Exception occured while fetching\n");
			throw loAppEx;
		}

		return loContractedServicesBean;
	}

	/**
	 * <ul>
	 * The method is updated in Release 7. Added a parameter for showing
	 * approved modification.
	 * <li>This method is used to fetch data for Rent
	 * 
	 * <li>For Rent Modification Screen</li>
	 * <ol>
	 * <li>Rent grid column will be editable</li>
	 * <ul>
	 * <li>Editable field only for new rows that are added from within the
	 * Modification budget.</li>
	 * <li>If the row was a part of the approved budget at the point the
	 * Modification budget was created, the field will be read only</li>
	 * </ul>
	 * 
	 * <ul>
	 * <li>Editable field only for new rows that are added from within the
	 * Modification budget.</li>
	 * <li>If the row was a part of the approved budget at the point the
	 * Modification budget was created, the field will be read only</li>
	 * </ul>
	 * <li>Approved FY Budget column is read only</li>
	 * <li>For Modification Amount column will be editable</li>
	 * <ul>
	 * <li>Verify upon save that the Modification Amount entered</li>
	 * <li>would not cause the Total Proposed Budget for the line item to fall
	 * below the YTD Invoiced Amount.</li>
	 * <li>If it would, display error message: !</li>
	 * <li>Entered value would cause the Proposed Budget to fall below the
	 * amount already invoiced for the line item. Please enter a new value.</li>
	 * <li>Query used : fetchContractBudgetModificationRent</li>
	 * <li>Query used : fetchContractBudgetModificationRentNew</li>
	 * </ul>
	 * </ol>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SqlSession object
	 * @param aoCBGridBeanObj CBGridBean object
	 * @param aoMasterBean - MasterBean object
	 * @return loRent List<Rent> object will return the list of rent
	 * @throws ApplicationException ApplicationException to catch application
	 *             throwing
	 */
	@SuppressWarnings("unchecked")
	public List<Rent> fetchModificationRent(SqlSession aoMyBatisSession, CBGridBean aoCBGridBeanObj,
			MasterBean aoMasterBean) throws ApplicationException
	{
		List<Rent> loRent = new ArrayList<Rent>();
		String lsParentSubBudgetId = aoCBGridBeanObj.getParentSubBudgetId();
		// R7 changes
		List<Rent> loNewRent = new ArrayList<Rent>();
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);
		// R7 end
		try
		{
			// R7 changes start Fetch the data from FileNet XML if Budget Status
			// is approved
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loRent = new ContractBudgetAmendmentService().fetchRentFromXML(lsSubBudgetId, aoMasterBean);
				if (loRent != null)
				{
					loNewRent.addAll(loRent);
				}
			}
			else
			{
				// R7 end
				if (aoCBGridBeanObj.getParentSubBudgetId() != null && aoCBGridBeanObj.getSubBudgetID() != null
						&& !aoCBGridBeanObj.getParentSubBudgetId().equalsIgnoreCase(aoCBGridBeanObj.getSubBudgetID()))
				{
					// For the previous entered records matching the subbudgetId
					loRent = (List<Rent>) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.FETCH_CONTRACT_BUDGET_MODIFICATION_RENT,
							HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				}
				// For the new records records matching the parentId
				loNewRent = (List<Rent>) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_CONTRACT_BUDGET_MODIFICATION_RENT_NEW,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				// Concat the new and previous records
				concatNewRecord(loNewRent, lsParentSubBudgetId);

				loNewRent.addAll(loRent);

			}
		}
		/**
		 * Application Exception handled here
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("CBGridBean : ", aoCBGridBeanObj);
			LOG_OBJECT.Error("Exception occured while retrieving ContractBudgetModificationRent", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService:fetchContractBudgetModificationRent" + " method "
					+ "- failed to fetch record " + " \n");
			throw aoAppEx;
		}
		/**
		 * Exception handled here
		 */
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in " + "ContractBudgetModificationRent ", aoEx);
			setMoState("Transaction Failed:: ContractBudgetService:fetchContractBudgetModificationRent"
					+ " method - failed to fetch record " + " \n");
			throw new ApplicationException("Exception occured while fetch in ContractBudgetService" + " ", aoEx);
		}

		return loNewRent;
	}

	/**
	 * <li>This method is used to append _newRecord for all the rows that are
	 * added at the time of modification.</li>
	 * @param aoRentForBudgetModification List<Rent> object to add the list
	 * @param asParentSubBudgetIdForBudgetModification String ParentSubBudgetId
	 * 
	 */
	private void concatNewRecord(List<Rent> aoRentForBudgetModification, String asParentSubBudgetIdForBudgetModification)
	{

		StringBuffer loConcat = null;
		if (null != aoRentForBudgetModification && aoRentForBudgetModification.size() > HHSConstants.INT_ZERO)
		{
			for (Rent loRent : aoRentForBudgetModification)
			{
				loConcat = new StringBuffer();
				if (!(loRent.getSubBudgetID() == (asParentSubBudgetIdForBudgetModification)))
				{
					loConcat.append(loRent.getId());
					// Adding new_record to all modified ONE
					loConcat.append(HHSConstants.NEW_RECORD_RENT);
					loRent.setId(loConcat.toString());
				}
			}
		}
	}

	/**
	 * <ol>
	 * <li>This method is used to edit data for RentModification on rent
	 * modification Screen</li>
	 * <li>This method is invoked when gridOperation is performed for
	 * updateModificationRent Transaction Id</li>
	 * </ol>
	 * 
	 * @param aoMyBatisSession SqlSe ssion object
	 * @param aoRent Rent object
	 * @return boolean loStatus boolean to store the status of updates
	 * @throws ApplicationException Application Exception thrown
	 */
	@SuppressWarnings("rawtypes")
	public boolean updateModificationRent(SqlSession aoMyBatisSession, Rent aoRent) throws ApplicationException
	{
		Boolean loStatus = false;
		HashMap loHashMap = null;
		Boolean loError = false;
		try
		{
			if (aoRent != null)
			{
				// Validation will be done for remaining amount
				loHashMap = validateRentModificationData(aoMyBatisSession, aoRent);
				if ((Boolean) loHashMap.get(HHSConstants.IS_VALID_DATA))
				{
					loStatus = updateInsertRent(aoMyBatisSession, aoRent);
				}
				else
				{
					// Error message thrown once the modify amount is an invalid
					// amount
					loError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							(String) loHashMap.get(HHSConstants.ERROR_MESSAGE)));
				}

			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			if (loError)
			{
				// In case of modification data validation failure, the
				// exception will be thrown back to base controller
				// along with the validation message
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			LOG_OBJECT.Error("App Exception occured in ContractBudgetModificationService:"
					+ " updateModificationRent method:: ", aoAppEx);
			setMoState("Transaction Failed::App Exception in ContractBudgetModificationService: "
					+ "updateModificationRent()" + " method aoCBRent::" + aoRent + "\n");
			throw aoAppEx;
		}
		// Exception handled here
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetService: editRent method:: ", aoEx);
			LOG_OBJECT.Error("Exception occured in ContractBudgetModificationService: "
					+ "updateModificationRent method:: ", aoEx);
			setMoState("Transaction Failed::Exception in ContractBudgetModificationService: "
					+ "updateModificationRent() method" + " aoCBRent::" + aoRent + "\n");
			throw loAppEx;
		}
		return loStatus;
	}

	/**
	 * This private method updates or inserts Rent row
	 * <ul>
	 * <li>Query used : fetchContractBudgetModificationRentAmount</li>
	 * <li>Query used : updateModificationRent</li>
	 * <li>Query used : editRentModification</li>
	 * </ul>
	 * @param aoMyBatisSession SqlSession object
	 * @param aoRentForBudgetModification Rent object
	 * @return Boolean
	 * @throws ApplicationException ApplicationException object
	 */
	private Boolean updateInsertRent(SqlSession aoMyBatisSession, Rent aoRentForBudgetModification)
			throws ApplicationException
	{
		Integer loVal = 0;
		Boolean loStatus = false;
		Rent loRentForBudgetModification = null;
		loRentForBudgetModification = (Rent) DAOUtil.masterDAO(aoMyBatisSession, aoRentForBudgetModification,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
				HHSConstants.FETCH_CONTRACT_BUDGET_MODIFICATION_RENT_AMOUNT, HHSConstants.CBY_COM_NYC_HHS_MODEL_RENT);
		// add the bean items received to your bean being
		// updated
		if (aoRentForBudgetModification.getLocation().equalsIgnoreCase(HHSConstants.EMPTY_STRING))
		{
			aoRentForBudgetModification.setLocation(loRentForBudgetModification.getLocation());
		}
		if (aoRentForBudgetModification.getManagementCompanyName().equalsIgnoreCase(HHSConstants.EMPTY_STRING))
		{
			aoRentForBudgetModification
					.setManagementCompanyName(loRentForBudgetModification.getManagementCompanyName());
		}
		if (aoRentForBudgetModification.getPropertyOwner().equalsIgnoreCase(HHSConstants.EMPTY_STRING))
		{
			aoRentForBudgetModification.setPropertyOwner(loRentForBudgetModification.getPropertyOwner());
		}
		if (aoRentForBudgetModification.getPublicSchoolSpace().equalsIgnoreCase(HHSConstants.EMPTY_STRING))
		{
			aoRentForBudgetModification.setPublicSchoolSpace(loRentForBudgetModification.getPublicSchoolSpace());
		}
		if (aoRentForBudgetModification.getPercentChargedToContract().equalsIgnoreCase(HHSConstants.EMPTY_STRING))
		{
			aoRentForBudgetModification.setPercentChargedToContract(loRentForBudgetModification
					.getPercentChargedToContract());
		}
		loVal = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoRentForBudgetModification,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.MODIFICATION_UPDATE_RENT,
				HHSConstants.CBY_COM_NYC_HHS_MODEL_RENT);
		// If the update is for the first time it will insert a new
		// record for the same as modification
		if (loVal < HHSConstants.INT_ONE)
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoRentForBudgetModification,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.EDIT_RENT_MODIFICATION,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_RENT);
		}
		loStatus = true;
		return loStatus;
	}

	/**
	 * <ul>
	 * <li>This method is used to add data for Rent on Modification of Rent
	 * Screen</li>
	 * <li>This method is invoked when gridOperation is performed for
	 * insertContractBudgetModificationRent Transaction Id</li>
	 * <li>For Rent Modification Screen</li>
	 * <ol>
	 * <li>By default, the field will be blank when a new row is added</li>
	 * <li>Query used : insertContractBudgetModificationRent</li>
	 * </ol>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SqlSession object
	 * @param aoModificationRent Rent object
	 * @return loVal Integer liVal
	 * @throws ApplicationException ApplictaionException caught
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public Integer insertContractBudgetModificationRent(SqlSession aoMyBatisSession, Rent aoModificationRent)
			throws ApplicationException
	{
		Integer loVal = HHSConstants.INT_ZERO;
		HashMap loHashMapForModification = null;
		Boolean loError = false;
		try
		{
			if (aoModificationRent != null)
			{
				// Validation will be done for remaining amount
				loHashMapForModification = validateRentModificationData(aoMyBatisSession, aoModificationRent);
				if ((Boolean) loHashMapForModification.get(HHSConstants.IS_VALID_DATA))
				{
					loVal = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoModificationRent,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBF_INSERT_CONTRACT_BUDGET_MODIFICATION_RENT,
							HHSConstants.CBY_COM_NYC_HHS_MODEL_RENT);
				}
				else
				{
					// Error message thrown once the modify amount is an invalid
					// amount
					loError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							(String) loHashMapForModification.get(HHSConstants.ERROR_MESSAGE)));
				}

			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			if (loError)
			{
				// In case of modification data validation failure, the
				// exception will be thrown back to base controller
				// along with the validation message
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			aoAppEx.addContextData("Rent : ", aoModificationRent);
			LOG_OBJECT.Error("Exception occured while inserting ContractBudgetModificationRent", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService:"
					+ "insertContractBudgetModificationRent" + " method - failed to insert record " + " \n");
			throw aoAppEx;
		}
		/**
		 * Exception handled here
		 */
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception while validating remaining amount", aoEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService:"
					+ "insertContractBudgetModificationRent" + " method - failed to insert record " + " \n");
			throw new ApplicationException("Exception occured while inserting in"
					+ " ContractBudgetModificationService :" + " insertContractBudgetModificationRent method", aoEx);
		}

		return loVal;
	}

	/**
	 * This method validates the remaining amount for a particular line-item and
	 * returns FALSE if the modify amount is greater then the remaining amount,
	 * else it validates it as TRUE
	 * <ul>
	 * <li>Query used : getRemainingAmountModificationRent</li>
	 * </ul>
	 * @param aoMybatisSession Session object
	 * @param aoRent Rent object
	 * @return loHashMap Hash map returning the value
	 * @throws ApplicationException Application exception handled
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private HashMap validateRentModificationData(SqlSession aoMybatisSession, Rent aoRent) throws ApplicationException
	{
		Boolean loIsDataValid = true;
		HashMap loHashMap = new HashMap();
		BigDecimal loRemainingAmount = null;
		loHashMap.put(HHSConstants.IS_VALID_DATA, loIsDataValid);
		try
		{
			// validates the modification amount so that it should not fall
			// below YTD Amount
			if (loIsDataValid && aoRent.getId() != null && !aoRent.getId().contains(HHSConstants.NEW_ROW_IDENTIFIER))
			{
				loRemainingAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoRent,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.QRY_GET_REMAINING_AMOUNT_MODIFICATION_RENT,
						HHSConstants.CBY_COM_NYC_HHS_MODEL_RENT);
			}
			loRemainingAmount = ((loRemainingAmount == null) ? BigDecimal.ZERO : loRemainingAmount);
			if (aoRent.getModifyAmount() != null
					&& !(loRemainingAmount.add(new BigDecimal((aoRent.getModifyAmount()))).compareTo(BigDecimal.ZERO) >= HHSConstants.INT_ZERO))
			{
				loHashMap.put(HHSConstants.IS_VALID_DATA, Boolean.FALSE);
				// Error message thrown for the Proposed budget, we are using
				// the rate validation message as the error message is same
				loHashMap.put(HHSConstants.ERROR_MESSAGE, HHSConstants.BUDGET_MODIFICATION_RATE_AMNT_VALIDATION);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("Rent : ", aoRent);
			LOG_OBJECT.Error("Exception while validating remaining amount", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService:"
					+ "validateRentModificationData method" + " \n");
			throw aoAppEx;
		}
		return loHashMap;
	}

	/**
	 * <ol>
	 * <li>This method is used to delete data for Rent Screen</li>
	 * <li>This method is invoked when gridOperation is performed for
	 * deleteRentModification Transaction Id</li>
	 * <li>Query used : delRentModification</li>
	 * </ol>
	 * 
	 * @param aoMyBatisSession SqlSession object
	 * @param aoRent Rent object
	 * @return boolean
	 * 
	 * @throws ApplicationException Application Exception returned
	 */
	public boolean deleteRentModification(SqlSession aoMyBatisSession, Rent aoRent) throws ApplicationException
	{
		boolean lbStatus = false;
		try
		{
			// The user will onle be able to delete the rows of modified budget
			// type
			DAOUtil.masterDAO(aoMyBatisSession, aoRent, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBY_DEL_RENT_MODIFICATION, HHSConstants.CBY_COM_NYC_HHS_MODEL_RENT);
			lbStatus = true;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction
		 * frameworkApplication Exception handled here
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("Rent : ", aoRent);
			LOG_OBJECT.Error("Exception occured in ContractBudgetModificationService:"
					+ " deleteRentModification method:: ", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: deleteRentModification "
					+ "method - failed Exception occured while deleting data\n");
			throw aoAppEx;
		}
		/**
		 * Exception handled here
		 */
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception while validating remaining amount", aoEx);
			setMoState("Transaction Failed:: ContractBudgetService:deleteRentModification"
					+ " method - failed to insert record " + " \n");
			throw new ApplicationException("Exception occured while inserting in ContractBudgetService :"
					+ " deleteRentModification method", aoEx);
		}

		return lbStatus;
	}

	// Start : Updated in R6
	/**
	 * This method is modified in Release 6. The Parameter asExistingBudget is
	 * added to display the validation message for new screens. This method is
	 * used to set Error message on the basis of transaction type. on the basis
	 * of transaction name.
	 * 
	 * @param asTransactionName String as input
	 * @param asExistingBudget String as input - Added in R5
	 * @return String
	 */
	private String fetchValidationMessage(String asTransactionName, String asExistingBudget)
	{
		String lsMsg = null;
		// Start : Added in R6
		if (HHSConstants.ZERO.equalsIgnoreCase(asExistingBudget))
		{
			lsMsg = HHSR5Constants.BUDGET_MODIFICATION_PS_SUMMARY_VALIDATION;
		}
		// End : Added in R6
		else
		{
			if (asTransactionName.equals(HHSConstants.CBY_MOD_SALARIED_EMPLOYEE_GRID_EDIT)
					|| asTransactionName.equals(HHSConstants.CBY_MOD_SALARIED_EMPLOYEE_GRID_ADD))
			{
				lsMsg = HHSConstants.BUDGET_MODIFICATION_PS_SAL_UNIT_VALIDATION;
			}
			else if (asTransactionName.equals(HHSConstants.CBY_MOD_HOURLY_EMPLOYEE_GRID_EDIT)
					|| asTransactionName.equals(HHSConstants.CBY_MOD_SEASONAL_EMPLOYEE_GRID_EDIT)
					|| asTransactionName.equals(HHSConstants.CBY_MOD_HOURLY_EMPLOYEE_GRID_ADD)
					|| asTransactionName.equals(HHSConstants.CBY_MOD_SEASONAL_EMPLOYEE_GRID_ADD))
			{
				lsMsg = HHSConstants.BUDGET_MODIFICATION_PS_HOURLY_UNIT_VALIDATION;
			}
		}
		return lsMsg;
	}

	// End : Updated in R6

	/**
	 * This method validates the modification amount for a particular line-item
	 * and returns FALSE if the modification amount user is trying to update
	 * makes the total proposed budget less than YTD invoice amount, else it
	 * validates it as TRUE
	 * <ul>
	 * <li>Query used : fetchPSDetailsForValidation</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession object
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget object
	 * @return loIsValid Boolean
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings(
	{ "unchecked", "null" })
	private Boolean validateModificationAmountForPersonnelServices(SqlSession aoMybatisSession,
			PersonnelServiceBudget aoPersonnelServiceBudgetBean) throws ApplicationException
	{
		Boolean loIsValid = true;
		String lsLocalRemainingAmt = null;

		try
		{
			if (!aoPersonnelServiceBudgetBean.getId().equalsIgnoreCase(HHSConstants.NEW_ROW_IDENTIFIER))
			{
				Map<Object, Object> loVal = (HashMap<Object, Object>) DAOUtil.masterDAO(aoMybatisSession,
						aoPersonnelServiceBudgetBean.getId(),
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBM_FETCH_PS_DETAILS_FOR_VALIDATION, HHSConstants.JAVA_LANG_STRING);

				if (loVal == null)
				{
					loVal = new HashMap<Object, Object>();
					loVal.put(HHSConstants.CBM_REMAINING_AMT, HHSConstants.INT_ZERO);
				}

				if (aoPersonnelServiceBudgetBean.getSubBudgetID().equals(
						loVal.get(HHSConstants.CBM_SUB_BUDGET_ID).toString()))
				{
					lsLocalRemainingAmt = HHSConstants.STRING_ZERO;
				}
				else
				{
					lsLocalRemainingAmt = loVal.get(HHSConstants.CBM_REMAINING_AMT).toString();
				}

				if ((new BigDecimal(lsLocalRemainingAmt).add(new BigDecimal(aoPersonnelServiceBudgetBean
						.getModificationAmount())).compareTo(BigDecimal.ZERO)) < HHSConstants.INT_ZERO)
				{
					loIsValid = false;
				}

				if (aoPersonnelServiceBudgetBean.getEmpPosition().equals(HHSConstants.EMPTY_STRING)
						|| aoPersonnelServiceBudgetBean.getEmpPosition() == null
						|| aoPersonnelServiceBudgetBean.getEmpPosition().equals(HHSConstants.STRING_ZERO))
				{
					aoPersonnelServiceBudgetBean.setEmpPosition(loVal.get(HHSConstants.CBM_POSITION_ID).toString());
				}
			}
			else if (new BigDecimal(aoPersonnelServiceBudgetBean.getModificationAmount()).compareTo(new BigDecimal(
					HHSConstants.INT_ZERO)) < HHSConstants.INT_ZERO)
			{
				loIsValid = false;
			}

		}
		// catch any application exception thrown from the code due to SELECT
		// statement failure and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("PersonnelServiceBudget bean : ", aoPersonnelServiceBudgetBean);
			LOG_OBJECT.Error("Exception while validating modified amount for personnel services", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService:validateModificationAmountForPersonnelServices method"
					+ " \n");
			throw loAppEx;
		}
		return loIsValid;
	}

	/**
	 * This method validates the modification amount for a particular line-item
	 * and returns FALSE if the modification amount user is trying to update
	 * makes the total proposed budget less than YTD invoice amount, else it
	 * validates it as TRUE for Fringe Benefits
	 * <ul>
	 * <li>Query used : fetchFringeAmountForValidation</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget
	 * @return loIsValid Boolean
	 * @throws ApplicationException ApplicationException object
	 */
	private Boolean validateModificationAmountForFringe(SqlSession aoMybatisSession,
			PersonnelServiceBudget aoPersonnelServiceBudgetBean) throws ApplicationException
	{
		Boolean loIsValid = true;

		try
		{
			BigDecimal loRemainingAmt = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession,
					aoPersonnelServiceBudgetBean.getId(),
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.CBM_FETCH_FRINGE_AMOUNT_FOR_VALIDATION, HHSConstants.JAVA_LANG_STRING);

			if (loRemainingAmt == null)
			{
				loRemainingAmt = BigDecimal.ZERO;
			}

			if (loRemainingAmt.add(new BigDecimal(aoPersonnelServiceBudgetBean.getModificationAmount())).compareTo(
					BigDecimal.ZERO) < HHSConstants.INT_ZERO)
			{
				loIsValid = false;
			}
		}
		// catch any application exception thrown from the code due to SELECT
		// statement failure and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("PersonnelServiceBudget bean : ", aoPersonnelServiceBudgetBean);
			LOG_OBJECT.Error("Exception while validating modified amount for Fringe", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService:validateModificationAmountForFringe method"
					+ " \n");
			throw loAppEx;
		}
		return loIsValid;
	}

	/**
	 * This method performs validation on total modification Amount on clicking
	 * Submit in Contract Budget Modification.
	 * <ul>
	 * <li>Query used : fetchSubBudgetList</li>
	 * <li>Query used : fetchModificationAmountTotal</li>
	 * </ul>
	 * @param aoMyBatisSession SqlSession object
	 * @param aoBudgetId : BudgetId of Modification BudgetType
	 * @return Boolean: true for success and false for validation failure
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public Boolean validateModificationAmountTotal(SqlSession aoMyBatisSession, String aoBudgetId)
			throws ApplicationException
	{
		Boolean loValid = false;
		String loAmountTotal = null;
		List<String> loSubBudgetIdList = null;
		Map<String, String> loHashMap = new HashMap<String, String>();
		try
		{
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoBudgetId);
			loSubBudgetIdList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_FETCH_SUB_BUDGET_LIST,
					HHSConstants.JAVA_UTIL_MAP);
			for (int liCount = 0; liCount < loSubBudgetIdList.size(); liCount++)
			{
				// Fetch sum of modification amount in all line items in Budget
				loAmountTotal = (String) DAOUtil.masterDAO(aoMyBatisSession, loSubBudgetIdList.get(liCount),
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBM_MAPPER_FETCH_MODIFICATION_AMOUNT_TOTAL, HHSConstants.JAVA_LANG_STRING);
				// If Total modification amount is 0, Validation successful
				if (new BigDecimal(loAmountTotal).compareTo(BigDecimal.ZERO) == HHSConstants.INT_ZERO)
				{
					loValid = true;
				}
				else
				{
					loValid = false;
					break;
				}
			}
		}
		// Application Exception handled here
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ContractBudgetModificationService:"
					+ " validateModificationAmountTotal method:: ", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: validateModificationAmountTotal "
					+ "method - failed Exception occured while validating Modification Total Amount\n");
			throw aoAppEx;
		}

		// Exception handled here - May occur for any unpredictable situation
		catch (Exception aoEx)
		{
			setMoState("Transaction Failed:: ContractBudgetService:validateModificationAmountTotal"
					+ " method - failed to insert record " + " \n");
			throw new ApplicationException("Exception occured while validating Modification Total Amount  :"
					+ " validateModificationAmountTotal method", aoEx);
		}

		return loValid;
	}

	/**
	 * This method performs validation on total Update Amount on clicking Submit
	 * in Contract Budget Update.
	 * <ul>
	 * <li>Query used : fetchModificationSubBudgetSummary</li>
	 * <li>Query used : fetchUpdateAmountTotal</li>
	 * <li>Query used : fetchSubBudgetAmount</li>
	 * </ul>
	 * @param aoMyBatisSession SqlSession object
	 * @param aoBudgetId : BudgetId of Modification BudgetType
	 * @return Boolean: true for success and false for validation failure
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Boolean validateUpdateAmountTotal(SqlSession aoMyBatisSession, String aoBudgetId)
			throws ApplicationException
	{
		Boolean loValid = true;
		HashMap aoHashmap = new HashMap<String, String>();
		List<CBGridBean> loSubBudgetList = null;
		BigDecimal loSubBugetAmount = BigDecimal.ZERO;
		BigDecimal loUpdateAmount = BigDecimal.ZERO;
		try
		{
			aoHashmap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoBudgetId);

			loSubBudgetList = (List<CBGridBean>) DAOUtil.masterDAO(aoMyBatisSession, aoHashmap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.FETCH_MODIFICATION_SUB_BUDGET_SUMMARY, HHSConstants.JAVA_UTIL_HASH_MAP);

			for (int liCount = 0; liCount < loSubBudgetList.size(); liCount++)
			{
				loUpdateAmount = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, loSubBudgetList.get(liCount)
						.getSubBudgetID(), HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBM_MAPPER_FETCH_UPDATE_AMOUNT_TOTAL, HHSConstants.JAVA_LANG_STRING);
				if (loUpdateAmount == null)
				{
					loUpdateAmount = BigDecimal.ZERO;
				}
				loSubBugetAmount = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, loSubBudgetList.get(liCount)
						.getSubBudgetID(), HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBM_MAPPER_FETCH_SUB_BUDGET_AMOUNT, HHSConstants.JAVA_LANG_STRING);
				if (loSubBugetAmount == null)
				{
					loSubBugetAmount = BigDecimal.ZERO;
				}
				if (!loUpdateAmount.equals(loSubBugetAmount))
				{
					loValid = false;
					break;
				}
			}
		}
		// Application Exception handled here
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ContractBudgetModificationService:"
					+ " validateUpdateAmountTotal method:: ", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: validateUpdateAmountTotal "
					+ "method - failed Exception occured while validating Modification Total Amount\n");
			throw aoAppEx;
		}

		// Exception handled here - May occur for any unpredictable situation
		catch (Exception aoEx)
		{
			setMoState("Transaction Failed:: ContractBudgetModificationService: validateUpdateAmountTotal"
					+ " method - failed to insert record " + " \n");
			throw new ApplicationException("Exception occured while validating Update Total Amount  :"
					+ " ContractBudgetModificationService method", aoEx);
		}
		return loValid;
	}

	/**
	 * This method is responsible for inserting newly added contract financial
	 * Line Item added during amendment against base contract
	 * <ul>
	 * <li>Query used : createContractFinancialReplica</li>
	 * </ul>
	 * @param aoMybatisSession
	 * @param aoHashMapForCFLineItems
	 * @throws ApplicationException
	 */

	@SuppressWarnings("rawtypes")
	private void linkAdditionalCFLineItemsToBase(SqlSession aoMyBatisSession, Map aoHashMapForCFLineItems)
			throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMapForCFLineItems,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBY_CREATE_CF_REPLICA,
					HHSConstants.JAVA_UTIL_MAP);

		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("SubBudgetId passed: ", aoHashMapForCFLineItems.get(HHSConstants.SUB_BUDGET_ID));
			LOG_OBJECT.Error("ApplicationException occured while executing query linkAdditionalCFLineItemsToBase ",
					aoAppEx);
			throw aoAppEx;
		}

	}

	/**
	 * This method is responsible for logical deletion of contract financial
	 * Line Item records of amendment contract. Changes for agency outbound
	 * interafce 6644
	 * <ul>
	 * <li>Query used : markContractFinancialAsDeleted</li>
	 * </ul>
	 * @param aoMybatisSession
	 * @param aoHashMap
	 * @throws ApplicationException
	 */

	private void markCFLineItemsForDeletion(SqlSession aoMyBatisSession, Map loHashMap) throws ApplicationException
	{
		try
		{
			// changes for agency outbound interafce 6644
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.CBM_DELETE_CF_REPLICA,
					HHSConstants.JAVA_UTIL_MAP);
		}
		catch (ApplicationException aoAppEx)
		{
			// changes for agency outbound interafce 6644
			aoAppEx.addContextData("Contract Id passed: ", loHashMap);
			LOG_OBJECT.Error("ApplicationException occured while executing query markCFLineItemsForDeletion ", aoAppEx);
			throw aoAppEx;
		}

	}

	// Added for R7: Modification Auto Approval
	/**
	 * This method is used to validate if a modification is eligible for auto
	 * approval or not
	 * 
	 * @param aoMybatisSession : MyBatis session for SQL Session
	 * @param aoHMWFRequiredProps: Hashmap
	 * @return Boolean value that returns true if modification is eligible for
	 *         auto approval
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public Boolean fetchBudgetsForModificationAutoApproval(SqlSession aoMybatisSession,
            HashMap<String, Object> aoHMWFRequiredProps) throws ApplicationException
      {
      LOG_OBJECT.Info("Entering into fetchBudgetsForModificationAutoApproval method with parameters::: "
                  + aoHMWFRequiredProps);
      
      BigDecimal liThresholdValue = new BigDecimal(0);
      BigDecimal liVersionId = new BigDecimal(0);
      boolean isModAutoApproved = true;
      List<String> loSubBudgetIdList = null;
      ContractBudgetBean loContractBean = null;
      ContractBudgetBean loContractBeanForCostCenter = null;
      ContractBudgetBean loContractBeanForModifiedUnits = null;
      String lsBudgetId = (String) aoHMWFRequiredProps.get(HHSConstants.BUDGET_ID_WORKFLOW);
      String lsContractId = (String) aoHMWFRequiredProps.get(HHSConstants.CONTRACT_ID1);
      LOG_OBJECT.Info("Budget Id::: " + lsBudgetId);
      LOG_OBJECT.Info("Contract Id::: " + lsContractId);
      HashMap<String, Object> loHashMapForBudgetUpdate = new HashMap<String, Object>();
      HashMap<String, Object> loHashMapFordetails = null;
      Map<String, Object> subBudgetsThresholdDetails = new HashMap<String, Object>();
      Float lfTotalBudgetAmt = 0f;
      Float lfTotalModificationAmt = 0f;
      
      LOG_OBJECT.Info("Updated HashMap::: " + aoHMWFRequiredProps);
      try
      {
	    	//Start: Added for defect QC 9151 R 7.3.2
    	  	String lsBudgetAutoApproveFlag = (String) DAOUtil.masterDAO(aoMybatisSession, aoHMWFRequiredProps,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSR5Constants.GET_AUTO_APPROVAL_FLAG,
					HHSConstants.JAVA_UTIL_HASH_MAP);
  	  		if(HHSConstants.ZERO.equalsIgnoreCase(lsBudgetAutoApproveFlag))
  	  		{
  	  			return false;
  	  		}
  	  		else if(HHSConstants.ONE.equalsIgnoreCase(lsBudgetAutoApproveFlag)){
  	  			return true;
  	  		}
	  	  	
	  	  	//End: Added for defect QC 9151 R 7.3.2
    	  	// The subbudget list
            loSubBudgetIdList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, lsBudgetId,
                        HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
                        HHSConstants.CBY_FETCH_SUB_BUDGET_LIST, HHSConstants.JAVA_LANG_STRING);
            LOG_OBJECT.Info("SubBudget List::: " + loSubBudgetIdList);
            /*
            * Checks if agency for which the contract was made has threshold
            * configured . If not it will go under normal approval process
            */
            loHashMapFordetails = (HashMap<String, Object>) DAOUtil.masterDAO(aoMybatisSession, lsContractId,
                        HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
                        HHSR5Constants.FETCH_THRESHOLD_CONFIGURED, HHSConstants.JAVA_LANG_STRING);
            LOG_OBJECT.Info("HashMap for configuration of an agency::: " + loHashMapFordetails);
            if (null == loHashMapFordetails)
            {
                  LOG_OBJECT.Info("No configuration found for this agency or provider::: ");
                  for (int subBudgetCount = 0; subBudgetCount < loSubBudgetIdList.size(); subBudgetCount++)
                  {
                        Map<String, Object> subBudgetData = new HashMap<String, Object>();
                        addingSubBugetDataForInsertion(liThresholdValue, liVersionId, loSubBudgetIdList, false, lsBudgetId,
                                    subBudgetsThresholdDetails, subBudgetCount, subBudgetData, HHSConstants.ZERO,
                                    HHSConstants.ZERO, HHSR5Constants.MESSAGE_THRESHOLD_NOT_CONFIGURED);
                  }
                  LOG_OBJECT.Info("Manual Approval ::: Sub-BudgetList to be inserted in the Approval_Details table ::: "
                              + subBudgetsThresholdDetails);
            }
            else
            {
				HashMap<String, Float> loAmounts = checkThresholdConsumedInCaseOfThresholdConfigured(aoMybatisSession,
						loSubBudgetIdList,  loContractBean, loContractBeanForCostCenter,
						loContractBeanForModifiedUnits, lsBudgetId, loHashMapFordetails, subBudgetsThresholdDetails);
                 lfTotalBudgetAmt = loAmounts.get(HHSR5Constants.TOTAL_BUDGET);
                 lfTotalModificationAmt = loAmounts.get(HHSR5Constants.TOTAL_MODIFICATION);
                 liThresholdValue = (BigDecimal) loHashMapFordetails.get(HHSR5Constants.COLUMN_THRESHOLD_PERCENTAGE);
            }
            for (Map.Entry<String, Object> entry : subBudgetsThresholdDetails.entrySet())
            {
            	  Float lfCurrentSubBudgetMod = (lfTotalBudgetAmt * liThresholdValue.floatValue())/100; 
            	  LOG_OBJECT.Info("Total Modification :::" + lfCurrentSubBudgetMod);
            	  if(null != loHashMapFordetails && lfTotalModificationAmt > lfCurrentSubBudgetMod)
				{
					((HashMap<String, Object>) entry.getValue()).put(HHSR5Constants.IS_ELIGIBLE_AUTO_APPROVAL, false);
					((HashMap<String, Object>) entry.getValue()).put(HHSR5Constants.AUTO_APPROVAL_REASON,
							HHSR5Constants.TOTAL_MOD_AMT_EXCEED_MSG);
				}
                  DAOUtil.masterDAO(aoMybatisSession, entry.getValue(),
                              HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
                              HHSR5Constants.INSERT_AUTO_APPROVAL_DETAILS, HHSConstants.JAVA_UTIL_HASH_MAP);
                  if (!(Boolean) ((HashMap<String, Object>) (entry.getValue()))
                              .get(HHSR5Constants.IS_ELIGIBLE_AUTO_APPROVAL))
                  {
                        isModAutoApproved = false;
                  }
            }
            LOG_OBJECT.Info("Final Auto approval state::: " + isModAutoApproved);
            loHashMapForBudgetUpdate.put(HHSR5Constants.IS_ELIGIBLE_AUTO_APPROVAL, isModAutoApproved);
            loHashMapForBudgetUpdate.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
            // Update budget table with auto approval and show info message flag
            updateBudgetDetailsForAutoApproval(aoMybatisSession, loHashMapForBudgetUpdate);
      }
      catch (ApplicationException aoAppEx)
      {
            LOG_OBJECT.Error("Exception occured while fetching budgets in fetchBudgetsForModificationAutoApproval",
                        aoAppEx);
            setMoState("Transaction Failed:: ContractBudgetModificationService:fetchBudgetsForModificationAutoApproval method - failed to fetch record "
                        + " \n");
            throw aoAppEx;
      }
      return isModAutoApproved;
}

	/** This method is added in R7 for Auto Mod approval. It is getting called from fetchBudgetsForModificationAutoApproval :
	 * ContractBudgetModificationService. It decides if modification has to go through auto-approval or manual approval.
	 * @param aoMybatisSession
	 * @param loSubBudgetIdList
	 * @param loContractBean
	 * @param loContractBeanForCostCenter
	 * @param loContractBeanForModifiedUnits
	 * @param lsBudgetId
	 * @param loHashMapFordetails
	 * @param subBudgetsThresholdDetails
	 * @return loModValues - Modification Amounts for Sub-budgets
	 * @throws ApplicationException
	 */
	private HashMap<String, Float> checkThresholdConsumedInCaseOfThresholdConfigured(SqlSession aoMybatisSession,
	List<String> loSubBudgetIdList, ContractBudgetBean loContractBean,
			ContractBudgetBean loContractBeanForCostCenter, ContractBudgetBean loContractBeanForModifiedUnits,
			String lsBudgetId, HashMap<String, Object> loHashMapFordetails,
			Map<String, Object> subBudgetsThresholdDetails) throws ApplicationException
	{
		String lsReasonForApproval = null;
		BigDecimal loActualThresholdAmount = new BigDecimal(0);
		BigDecimal liThresholdValue = new BigDecimal(0);
		BigDecimal liVersionId = new BigDecimal(0);
		Boolean lbIsEligibleForAutoApproval = false;
		Integer loNewLineItemAdded;
		Float lfTotalModAmt = 0f;
		Float lftotalSubBudgetAmount = 0f;
		LOG_OBJECT.Info("Configuration found for this agency or provider");
		  if (null != loHashMapFordetails.get(HHSR5Constants.COLUMN_THRESHOLD_PERCENTAGE)
		              && null != loHashMapFordetails.get(HHSR5Constants.COLUMN_VERSION_ID))
		  {
		        liThresholdValue = (BigDecimal) loHashMapFordetails.get(HHSR5Constants.COLUMN_THRESHOLD_PERCENTAGE);
		        LOG_OBJECT.Info("Threshold value::: " + liThresholdValue);
		        liVersionId = (BigDecimal) loHashMapFordetails.get(HHSR5Constants.COLUMN_VERSION_ID);
		        LOG_OBJECT.Info("Version Id::: " + liVersionId);
		  }
		  HashMap<String, Float> loModValues = new HashMap<String, Float>();
		  for (int liCount = 0; liCount < loSubBudgetIdList.size(); liCount++)
		  {
			  	HashMap<String, Object> loMapForContractBean = new HashMap<String, Object>();
		        Integer loSubBudgetId = Integer.parseInt(loSubBudgetIdList.get(liCount));
		        loMapForContractBean.put(HHSConstants.SUB_BUDGET_ID, loSubBudgetIdList.get(liCount));
		        loMapForContractBean.put(HHSR5Constants.SUB_BUDGET_NUM, loSubBudgetId);
		        loMapForContractBean.put(HHSR5Constants.BUDGET_ID,lsBudgetId );
		        loContractBean = (ContractBudgetBean) DAOUtil.masterDAO(aoMybatisSession, loMapForContractBean,
                        HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
                        HHSR5Constants.CALCULATE_PERCENTAGE_FOR_AUTO_APPROVAL, HHSConstants.JAVA_UTIL_HASH_MAP);
		        lfTotalModAmt = lfTotalModAmt + Float.parseFloat(loContractBean.getTotalModification());
		        lftotalSubBudgetAmount = lftotalSubBudgetAmount + Float.parseFloat(loContractBean.getSubbudgetAmount());
		        LOG_OBJECT.Info("Total Modification Amt:"+ lfTotalModAmt);
		        LOG_OBJECT.Info("Total Sub-budget Amt:"+ lftotalSubBudgetAmount);
		        LOG_OBJECT.Info("Iterating over sub budgets");
		        LOG_OBJECT.Info("Map for getting modification percentage::: " + loMapForContractBean);
		        LOG_OBJECT.Info("Iterating over sub budgets");
		        // check if new line item is getting added or not
		        loNewLineItemAdded = (Integer) DAOUtil.masterDAO(aoMybatisSession, loSubBudgetIdList.get(liCount),
		                    HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
		                    HHSR5Constants.FETCH_INFO_FOR_AUTO_APPROVAL, HHSConstants.JAVA_LANG_STRING);
		        LOG_OBJECT.Info("Checking if new line item added or not for any of the subbudgets:::"
		                    + loNewLineItemAdded);
		        if (null != loNewLineItemAdded && loNewLineItemAdded >= 1)
		        {
		              Map<String, Object> subBudgetData = new HashMap<String, Object>();
		              addingSubBugetDataForInsertion(liThresholdValue, liVersionId, loSubBudgetIdList, false,
		                          lsBudgetId, subBudgetsThresholdDetails, liCount, subBudgetData, HHSConstants.ZERO,
		                          HHSConstants.ZERO, HHSR5Constants.MESSAGE_NEW_LINE_ITEM);
		              LOG_OBJECT
		                          .Info("Subbudget data to be inerted in details table:::" + subBudgetsThresholdDetails);
		              continue;
		        }
		        LOG_OBJECT
		                    .Info("If in case no line item added, moving to check if the mod value is under threshold limit :::");
		        loContractBeanForCostCenter = (ContractBudgetBean) DAOUtil.masterDAO(aoMybatisSession,
		                    loMapForContractBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
		                    HHSR5Constants.CALCULATE_PERCENTAGE_FOR_COST_CENTER_AUTO_APPROVAL,
		                    HHSConstants.JAVA_UTIL_HASH_MAP);
		        // R7 Changes Starts: query will fetch the sum of modified units for lsBudgetId.
		        loContractBeanForModifiedUnits = (ContractBudgetBean) DAOUtil.masterDAO(aoMybatisSession,
		                    loMapForContractBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
		                    HHSR5Constants.SUM_OF_MODIFIED_UNITS_FOR_COST_CENTER_AUTO_APPROVAL, 
		                    HHSConstants.JAVA_UTIL_HASH_MAP);
		        
		        LOG_OBJECT.Info("Contract Bean for Modified units::: " + loContractBeanForModifiedUnits);
		        // R7 Changes Ends: 
		        LOG_OBJECT.Info("Cost Centre Bean Data::: " + loContractBeanForCostCenter);
		        Map<String, Object> autoApprovedSubBudgetData = new HashMap<String, Object>();
		        // R7 Changes : or condition is added that will check the count of modified units.
		        //QC 9115 R 7.3.0 BEGIN - Fix from Accenture

		        System.out.println("--------------------------------------------------------------- \n " +
		        		"##########    ContractBeanForCostCenter::" + loContractBeanForCostCenter ) ;
		        System.out.println("---------------------------------------------------------------"  ) ;
		        if (
/* [Start] R8.5.0 QC 9465  Adding Auto Approval into Services Tab
		        		(null != loContractBeanForCostCenter && Float.parseFloat(loContractBeanForCostCenter.getTotalModification()) > 0) */
		        		(null != loContractBeanForModifiedUnits && Float.parseFloat(loContractBeanForModifiedUnits.getUnallocatedFundCount()) > 0 )   /* check non zero unallocated fund count */
/*[End] R8.5.0 QC 9465*/ 
		        		|| 

	                    (null != loContractBeanForModifiedUnits && Float.parseFloat(loContractBeanForModifiedUnits.getModifiedUnits()) > 0 ))
		        {
//		              LOG_OBJECT.Info("If in case cost center data is added :::");
			        System.out.println("---------------------------------------------------------------If"  ) ;
		              lbIsEligibleForAutoApproval = false;
		              lsReasonForApproval = HHSR5Constants.COST_CENTER_LINE_ITEM_ADDED;
		              addingSubBugetDataForInsertion(liThresholdValue, liVersionId, loSubBudgetIdList,
		                          lbIsEligibleForAutoApproval, lsBudgetId, subBudgetsThresholdDetails, liCount,
		                          autoApprovedSubBudgetData, loContractBeanForCostCenter.getModificationPercentage(),
		                          loContractBeanForCostCenter.getTotalModification(), lsReasonForApproval);
		        }
		      //QC 9115 R 7.3.0 END - Fix from Accenture
		        else
		        {
			        System.out.println("---------------------------------------------------------------Else"  ) ;
		        	// Added for sub_budget mods
		        		if(!isSubBudgetAmountModified(aoMybatisSession, loMapForContractBean))
		        		{
		        			lsReasonForApproval = HHSR5Constants.NO_MODIFICATION_MSG;
		        			addingSubBugetDataForInsertion(liThresholdValue, liVersionId, loSubBudgetIdList,
			                          true, lsBudgetId, subBudgetsThresholdDetails, liCount,
			                          autoApprovedSubBudgetData, loContractBeanForCostCenter.getModificationPercentage(),
			                          loContractBeanForCostCenter.getTotalModification(), lsReasonForApproval);
		        			continue;
		        		}
		        	// Added for sub_budget mods
		              //LOG_OBJECT.Info("If in case cost center data is not added :::");
		              LOG_OBJECT.Info("All Categories Bean Data except cost center category::: " + loContractBean);
		              loActualThresholdAmount = ((new BigDecimal(loContractBean.getSubbudgetAmount())).multiply(liThresholdValue)).divide(new BigDecimal(100));
		              LOG_OBJECT.Info("New Actual::: "+loActualThresholdAmount+ "Float Value::: "+loActualThresholdAmount.floatValue());
		              LOG_OBJECT.Info("In Auto Approval::: Mod Percent:: " + loContractBean.getTotalModification());
		              if (null != loContractBean.getTotalModification()
		                          && Float.parseFloat(loContractBean.getTotalModification()) <= loActualThresholdAmount.floatValue())
		              {
		                    LOG_OBJECT.Info("In Auto Approval::: Mod Percent:: " + loContractBean.getModificationPercentage());
		                    LOG_OBJECT.Info("New Actual::: "+loContractBean.getTotalModification());
		                    lbIsEligibleForAutoApproval = true;
		                    lsReasonForApproval = HHSR5Constants.MESSAGE_AUTO_APPROVAL_CONSIDERED;
		              }
		              else
		              {
		                    LOG_OBJECT.Info("In Manual Approval::: Mod Percent:: " + loContractBean.getModificationPercentage());
		                    lbIsEligibleForAutoApproval = false;
		                    lsReasonForApproval = HHSR5Constants.MESSAGE_THRESHOLD_CONSUMED;
		              }

		              addingSubBugetDataForInsertion(liThresholdValue, liVersionId, loSubBudgetIdList,
		                          lbIsEligibleForAutoApproval, lsBudgetId, subBudgetsThresholdDetails, liCount,
		                          autoApprovedSubBudgetData, loContractBean.getModificationPercentage(),
		                          loContractBean.getTotalModification(), lsReasonForApproval);
		        }
		  }
		  loModValues.put(HHSR5Constants.TOTAL_BUDGET, lftotalSubBudgetAmount);
		  loModValues.put(HHSR5Constants.TOTAL_MODIFICATION, lfTotalModAmt);
		  return loModValues;
	}

	/**
	 * The method is added in R7 for making a list of subbudget details to be
	 * inserted in budget approval table.
	 * @param aiThresholdValue
	 * @param aiVersionId
	 * @param aoSubBudgetIdList
	 * @param abIsEligibleForAutoApproval
	 * @param asBudgetId
	 * @param aosubBudgetsThresholdDetails
	 * @param aisubBudgetCount
	 * @param aosubBudgetData
	 * @param asModPercentage
	 * @param asTotalMod
	 * @return lbFinalAutoApprovalFlag
	 */
	private void addingSubBugetDataForInsertion(BigDecimal aiThresholdValue, BigDecimal aiVersionId,
			List<String> aoSubBudgetIdList, Boolean abIsEligibleForAutoApproval, String asBudgetId,
			Map<String, Object> aosubBudgetsThresholdDetails, int aisubBudgetCount,
			Map<String, Object> aosubBudgetData, String asModPercentage, String asTotalMod, String asApprovalReason)
	{
		aosubBudgetData.put(HHSConstants.BUDGET_ID_WORKFLOW, asBudgetId);
		aosubBudgetData.put(HHSConstants.SUB_BUDGET_ID, aoSubBudgetIdList.get(aisubBudgetCount));
		aosubBudgetData.put(HHSR5Constants.MODIFICATION_PERCENTAGE, asModPercentage);
		aosubBudgetData.put(HHSR5Constants.TOTAL_MODIFICATION, asTotalMod);
		aosubBudgetData.put(HHSR5Constants.AUTO_APPROVAL_REASON, asApprovalReason);
		aosubBudgetData.put(HHSR5Constants.VERSION_ID, aiVersionId);
		aosubBudgetData.put(HHSR5Constants.THRESHOLD_VALUE, aiThresholdValue);
		aosubBudgetData.put(HHSR5Constants.IS_ELIGIBLE_AUTO_APPROVAL, abIsEligibleForAutoApproval);
		aosubBudgetsThresholdDetails.put(aoSubBudgetIdList.get(aisubBudgetCount), aosubBudgetData);
	}

	/**
	 * The method is added in R7 for updating the budget table with auto
	 * approval flag and show info flag.
	 * @param aoMybatisSession
	 * @param aoHashMapForBudgetUpdate
	 * @throws ApplicationException
	 */
	private void updateBudgetDetailsForAutoApproval(SqlSession aoMybatisSession,
			HashMap<String, Object> aoHashMapForBudgetUpdate) throws ApplicationException
	{
		String lsFinalFlagInBudget = new String();
		try
		{
			LOG_OBJECT.Info("Inserting details in Budget Table::: " + aoHashMapForBudgetUpdate);
			boolean lbAutoApprovalState = (Boolean) aoHashMapForBudgetUpdate.get(HHSR5Constants.IS_ELIGIBLE_AUTO_APPROVAL);
			DAOUtil.masterDAO(aoMybatisSession, aoHashMapForBudgetUpdate,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.UPDATE_APPROVAL_DETAILS_IN_BUDGET, HHSConstants.JAVA_UTIL_HASH_MAP);
			String lsModCount = (String) DAOUtil.masterDAO(aoMybatisSession, aoHashMapForBudgetUpdate,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, "getTotalModificationOfBaseBudget",
					HHSConstants.JAVA_UTIL_HASH_MAP);
			if (null != lsModCount && lsModCount.equals(HHSConstants.ZERO))
			{
				lsFinalFlagInBudget = lbAutoApprovalState ? HHSConstants.ONE : HHSConstants.ZERO;
			}
			else
			{
				lsFinalFlagInBudget = (String) DAOUtil.masterDAO(aoMybatisSession, aoHashMapForBudgetUpdate,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSR5Constants.FETCH_FINAL_SHOW_MSG_FLAG, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			LOG_OBJECT.Info("Final Flag ::: " + lsFinalFlagInBudget);
			if (null != lsFinalFlagInBudget)
			{
				aoHashMapForBudgetUpdate.put(HHSR5Constants.LS_FINAL_FLAG, lsFinalFlagInBudget);
				DAOUtil.masterDAO(aoMybatisSession, aoHashMapForBudgetUpdate,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSR5Constants.UPDATE_SHOW_INFO_FLAG_IN_BUDGET, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while fetching budgets in updateBudgetDetailsForAutoApproval", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService:updateBudgetDetailsForAutoApproval method - failed to update record "
					+ " \n");
			throw aoAppEx;
		}
	}

	/**
	 * The method is added in R7 for getting auto approver user for an agency.
	 * @param aoMybatisSession
	 * @param asAgencyId
	 * @return lsUserName
	 * @throws ApplicationException
	 */
	public String getAutoApproverUserNameForAgency(SqlSession aoMybatisSession) throws ApplicationException
	{
		String lsUserName = null;
		try
		{
			LOG_OBJECT.Info("Entering into getAutoApproverUserNameForAgency method");
			lsUserName = (String) DAOUtil.masterDAO(aoMybatisSession, null,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.GET_AUTO_APPROVER_USER_NAME, null);
			LOG_OBJECT.Info("Auto Approver User Name:::" + lsUserName);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while fetching budgets in updateBudgetDetailsForAutoApproval", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService:updateBudgetDetailsForAutoApproval method - failed to update record "
					+ " \n");
			throw aoAppEx;
		}
		return lsUserName;
	}

	/**
	 * This method added in R7. This method will fetch the count of modified
	 * budget.
	 * @param aoMyBatisSession
	 * @param aoHashmap
	 * @return
	 * @throws ApplicationException
	 */
	public String getModificationBudgetCount(SqlSession aoMyBatisSession, HashMap<String, String> aoHashmap)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in getModificationBudgetCount Method");
		String lsCount = HHSConstants.ZERO;
		try
		{
			lsCount = (String) DAOUtil.masterDAO(aoMyBatisSession,
					(String) aoHashmap.get(HHSConstants.BUDGET_ID_WORKFLOW),
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.FETCH_BUDGET_MODIFICATION_URL_COUNT, HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Application Exception occured in getModificationBudgetCount while fetching details",
					loAppEx);
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception Exception occured while fetching details:", loEx);
			throw new ApplicationException(" Exception occured while fetching details:", loEx);
		}
		return lsCount;
	}

	/**
	 * This method added in R7. The method will update the show info flag in
	 * budget table.
	 * @param aoMyBatisSession
	 * @param asBudgetId
	 * @param aoApprovedFlag
	 * @return lbCount
	 * @throws ApplicationException
	 */
	public Integer updateAutoApprovalShowInfoFlag(SqlSession aoMyBatisSession, String asBudgetId, Boolean aoApprovedFlag)
			throws ApplicationException
	{
		Integer lbCount = 0;
		try
		{
			if (aoApprovedFlag && null != asBudgetId && !asBudgetId.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING))
			{
				lbCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, asBudgetId,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSR5Constants.UPDATE_AUTO_APPROVAL_REVIEW_FLAG, HHSConstants.JAVA_LANG_STRING);
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error(
					"Application Exception occured in updateAutoApprovalDetailReviewFlag while fetching details:"
							+ asBudgetId, loAppEx);
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception Exception occured while fetching details:", loEx);
			throw new ApplicationException(" Exception occured while fetching details:", loEx);
		}
		return lbCount;
	}

	// Start: Added in R7 for Cost Center
	/**
	 * This method fetches List of Services, for Cost-Center Services Grid
	 * @param aoCBGridBeanObj
	 * @param aoMyBatisSession
	 * @param aoMasterBeanObj
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public List<CBServicesBean> fetchContractServicesModificationGrid(CBGridBean aoCBGridBeanObj,
			SqlSession aoMyBatisSession, MasterBean aoMasterBeanObj) throws ApplicationException
	{
		List<CBServicesBean> loCBServicesBeansMod = null;
		List<CBServicesBean> loCBServicesBeansBase = null;
		List<LineItemMasterBean> loMasterBeanList = null;
		try
		{
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			if (aoMasterBeanObj != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loMasterBeanList = aoMasterBeanObj.getMasterBeanList();
				Iterator<LineItemMasterBean> aoListIterator = loMasterBeanList.iterator();
				while (aoListIterator.hasNext())
				{
					LineItemMasterBean loLineItemBean = aoListIterator.next();
					if (loLineItemBean.getSubbudgetId().equals(lsSubBudgetId))
					{
						loCBServicesBeansMod = loLineItemBean.getServicesBeanList();
					}
				}
			}
			else
			{
				LOG_OBJECT.Debug("Entered inside method fetchContractServicesModificationGrid:");
				loCBServicesBeansMod = (List<CBServicesBean>) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSR5Constants.FETCH_CONTRACT_SERVICES_MOD, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				loCBServicesBeansBase = (List<CBServicesBean>) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSR5Constants.FETCH_CONTRACT_SERVICES_BASE, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				HashMap<String, CBServicesBean> loInputMapBean = new HashMap<String, CBServicesBean>();
				Iterator<CBServicesBean> loItrListParent = loCBServicesBeansMod.iterator();
				while (loItrListParent.hasNext())
				{
					CBServicesBean loBean = loItrListParent.next();
					loInputMapBean.put(loBean.getCostCenter(), loBean);
				}
				if (loCBServicesBeansBase != null && !loCBServicesBeansBase.isEmpty())
				{
					for (CBServicesBean loProgramBean : loCBServicesBeansBase)
					{
						String lsCostCenterID = loProgramBean.getCostCenter();
						int liProposedUnits= Integer.valueOf(loProgramBean.getUnits());
						if (loInputMapBean.containsKey(lsCostCenterID))
						{	
							liProposedUnits+=Integer.valueOf(loInputMapBean.get(lsCostCenterID).getModUnits());
							loInputMapBean.get(lsCostCenterID).setFyBudget(loProgramBean.getFyBudget());
							loInputMapBean.get(lsCostCenterID).setRemUnits(loProgramBean.getRemUnits());
							loInputMapBean.get(lsCostCenterID).setRemainingAmt(loProgramBean.getRemainingAmt());
							loInputMapBean.get(lsCostCenterID).setProposedUnits(String.valueOf(liProposedUnits));
							loInputMapBean.get(lsCostCenterID).setUnits(loProgramBean.getUnits());
						}
						else
						{	loProgramBean.setProposedUnits(String.valueOf(liProposedUnits));
							loInputMapBean.put(lsCostCenterID, loProgramBean);
						}
						
					}
					loCBServicesBeansMod.clear();
					for (Map.Entry<String, CBServicesBean> loProgramIncomeOutputBean : loInputMapBean.entrySet())
					{
						loCBServicesBeansMod.add(loProgramIncomeOutputBean.getValue());
					}
				}
				setMoState("ContractBudgetService: fetchContractServicesModificationGrid() passed");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error occured while fetching ContractServices Details for budget id: "
					+ aoCBGridBeanObj.getContractBudgetID() + " and Sub-Budget id : "
					+ aoCBGridBeanObj.getSubBudgetID());
			loExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBGridBeanObj.getSubBudgetID());
			loExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_BUDGET, aoCBGridBeanObj.getContractBudgetID());
			LOG_OBJECT.Error(
					"Exception occured in ContractBudgetService: fetchContractServicesModificationGrid method:: ",
					loExp);
			throw loExp;
		}
		// Catch any Null pointer exception thrown from the code and wrap it
		// into application
		// Exception and throw it forward
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while fetching ContractServices ", loExp);
			setMoState("Exception occured while fetching ContractServices for budget id: "
					+ aoCBGridBeanObj.getContractBudgetID() + " and Sub-Budget id : "
					+ aoCBGridBeanObj.getSubBudgetID());
			loAppEx.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBGridBeanObj.getSubBudgetID());
			loAppEx.addContextData(HHSConstants.ADD_CONTEXT_DATA_BUDGET, aoCBGridBeanObj.getContractBudgetID());
			LOG_OBJECT.Error(
					"Exception occured in ContractBudgetService: fetchContractServicesModificationGrid method:: ",
					loAppEx);
			throw loAppEx;
		}
		//[Start]R7.12.0 QC9311 Minimize Debug
		//LOG_OBJECT.Debug("Exited from method fetchContractServicesModificationGrid with:" + loCBServicesBeansMod);
		LOG_OBJECT.Debug("Exited from method fetchContractServicesModificationGrid with:" );
		//[End]R7.12.0 QC9311 Minimize Debug
		return loCBServicesBeansMod;
	}

	/**
	 * This method fetches List of Cost-Centers selected, for Cost-Center Grid
	 * @param aoCBGridBeanObj
	 * @param aoMyBatisSession
	 * @param aoMasterBeanObj
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public List<CBServicesBean> fetchContractCostCenterModificationGrid(CBGridBean aoCBGridBeanObj,
			SqlSession aoMyBatisSession, MasterBean aoMasterBeanObj) throws ApplicationException
	{
		List<CBServicesBean> loCBServicesBeansMod = null;
		List<CBServicesBean> loCBServicesBeansBase = null;
		try
		{
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			if (aoMasterBeanObj != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				List<LineItemMasterBean> loMasterBeanList = null;
				loMasterBeanList = aoMasterBeanObj.getMasterBeanList();
				Iterator<LineItemMasterBean> aoListIterator = loMasterBeanList.iterator();
				while (aoListIterator.hasNext())
				{
					LineItemMasterBean loLineItemBean = aoListIterator.next();
					if (loLineItemBean.getSubbudgetId().equals(lsSubBudgetId))
					{
						loCBServicesBeansMod = loLineItemBean.getCostCenterBeanList();
					}
				}
			}
			else
			{
				LOG_OBJECT.Debug("Entered inside method fetchContractCostCenterModificationGrid:");
				loCBServicesBeansMod = (List<CBServicesBean>) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSR5Constants.FETCH_CONTRACT_COST_CENTER_MOD, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				loCBServicesBeansBase = (List<CBServicesBean>) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSR5Constants.FETCH_CONTRACT_COST_CENTER_BASE, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				HashMap<String, CBServicesBean> loInputMapBean = new HashMap<String, CBServicesBean>();
				Iterator<CBServicesBean> loItrListParent = loCBServicesBeansMod.iterator();
				while (loItrListParent.hasNext())
				{
					CBServicesBean loBean = loItrListParent.next();
					loInputMapBean.put(loBean.getCostCenter(), loBean);
				}
				if (loCBServicesBeansBase != null && !loCBServicesBeansBase.isEmpty())
				{
					for (CBServicesBean loProgramBean : loCBServicesBeansBase)
					{
						String lsCostCenterID = loProgramBean.getCostCenter();
						if (loInputMapBean.containsKey(lsCostCenterID))
						{
							loInputMapBean.get(lsCostCenterID).setFyBudget(loProgramBean.getFyBudget());
							loInputMapBean.get(lsCostCenterID).setRemainingAmt(loProgramBean.getRemainingAmt());
						}
						else
						{
							loInputMapBean.put(lsCostCenterID, loProgramBean);
						}
					}
					loCBServicesBeansMod.clear();
					for (Map.Entry<String, CBServicesBean> loProgramIncomeOutputBean : loInputMapBean.entrySet())
					{
						loCBServicesBeansMod.add(loProgramIncomeOutputBean.getValue());
					}
				}
				setMoState("ContractBudgetService: fetchContractCostCenterModificationGrid() passed");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error occured while fetching ContractCostCenters Details for budget id: "
					+ aoCBGridBeanObj.getContractBudgetID() + " and Sub-Budget id : "
					+ aoCBGridBeanObj.getSubBudgetID());
			loExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBGridBeanObj.getSubBudgetID());
			loExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_BUDGET, aoCBGridBeanObj.getContractBudgetID());
			LOG_OBJECT.Error(
					"Exception occured in ContractBudgetService: fetchContractCostCenterModificationGrid method:: ",
					loExp);
			throw loExp;
		}
		// Catch any Null pointer exception thrown from the code and wrap it
		// into application
		// Exception and throw it forward
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while fetching ContractCostCenters ", loExp);
			setMoState("Exception occured while fetching ContractCostCenters for budget id: "
					+ aoCBGridBeanObj.getContractBudgetID() + " and Sub-Budget id : "
					+ aoCBGridBeanObj.getSubBudgetID());
			loAppEx.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBGridBeanObj.getSubBudgetID());
			loAppEx.addContextData(HHSConstants.ADD_CONTEXT_DATA_BUDGET, aoCBGridBeanObj.getContractBudgetID());
			LOG_OBJECT.Error(
					"Exception occured in ContractBudgetService: fetchContractCostCenterModificationGrid method:: ",
					loAppEx);
			throw loAppEx;
		}
		//[Start]R7.12.0 QC9311 Minimize Debug
		//LOG_OBJECT.Debug("Exited from method fetchContractCostCenterModificationGrid with:" + loCBServicesBeansMod);
		LOG_OBJECT.Debug("Exited from method fetchContractCostCenterModificationGrid with:" );
		//[End]R7.12.0 QC9311 Minimize Debug
		return loCBServicesBeansMod;
	}

	/**
	 * This method modifies the Services for Services Grid. After validating the
	 * details
	 * @param aoCBGridBeanObj
	 * @param aoMyBatisSession
	 * @return
	 * @throws ApplicationException
	 */
	public boolean editServiceModificationDetails(CBServicesBean aoCBGridBeanObj, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		boolean lbEditStatus = false;
		boolean lbError = false;
		try
		{
			CBServicesBean loCBServicesBean = null;
			// Get Amount Details for server side validation
			loCBServicesBean = (CBServicesBean) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj.getId(),
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.FETCH_CONTRACT_SERVICES_DETAILS_FOR_VALIDATION, HHSConstants.JAVA_LANG_STRING);
			// Check if Modification/Updated Amount is not less than already
			// invoiced amount
			if ((new BigDecimal(loCBServicesBean.getRemainingAmt()).add(new BigDecimal(aoCBGridBeanObj
					.getModificationAmt())).compareTo(BigDecimal.ZERO)) >= HHSConstants.INT_ZERO
					|| new BigDecimal((aoCBGridBeanObj.getModificationAmt())).compareTo(BigDecimal.ZERO) == HHSConstants.INT_ZERO)
			{
				// Update/Insert Modification Row
				mergeUpdatesServices(aoMyBatisSession, aoCBGridBeanObj);
			}
			else
			{
				lbError = true;
				if (HHSConstants.ONE.equals(aoCBGridBeanObj.getBudgetTypeId()))
				{
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSR5Constants.NEW_CBM_LESS_THAN_INVOICE_FOR_NEG_AMEND));
				}
				else
				{
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSR5Constants.CBM_LESS_THAN_INVOICE_FOR_PI));
				}
			}
			lbEditStatus = true;
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException loExp)
		{
			if (lbError)
			{
				loExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loExp.toString());
			}
			else
			{
				loExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBGridBeanObj.getSubBudgetID());
				loExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_BUDGET, aoCBGridBeanObj.getContractBudgetID());
				LOG_OBJECT
						.Error("Exception occured in ContractBudgetModificationService: editServiceModificationDetails method:: ",
								loExp);
				setMoState("Exception occured while updating Services for budget id: "
						+ aoCBGridBeanObj.getContractBudgetID() + " and Sub-Budget id : "
						+ aoCBGridBeanObj.getSubBudgetID());
			}
			throw loExp;
		}
		// Catch any Null pointer exception thrown from the code and wrap it
		// into application
		// Exception and throw it forward
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException("Exception occured while updating Services ", loExp);
			setMoState("Exception occured while updating Services for budget id: "
					+ aoCBGridBeanObj.getContractBudgetID() + " and Sub-Budget id : "
					+ aoCBGridBeanObj.getSubBudgetID());
			loAppEx.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBGridBeanObj.getSubBudgetID());
			loAppEx.addContextData(HHSConstants.ADD_CONTEXT_DATA_BUDGET, aoCBGridBeanObj.getContractBudgetID());
			LOG_OBJECT.Error(
					"Exception occured in ContractBudgetModificationService: editServiceModificationDetails method:: ",
					loExp);
			throw loAppEx;
		}
		return lbEditStatus;
	}
	/**
	 * This method is called from editServiceModificationDetails method
	 * Inserts/Updates Services
	 * @param aoMyBatisSession
	 * @param aoCBGridBeanObj
	 * @throws ApplicationException
	 */
	private void mergeUpdatesServices(SqlSession aoMyBatisSession, CBServicesBean aoCBGridBeanObj)
			throws ApplicationException
	{
		// Update Modified Amount of Line Item for Modified
		// SubBudget
		int liUpdateCounter = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
				HHSR5Constants.UPDATE_CONTRACT_SERVICES_MOD_DETAILS, HHSR5Constants.COM_NYC_HHS_MODEL_CB_SERVICESBEAN);
		// If modification row of Line Item does not exist then
		// insert a new modification row.
		if (liUpdateCounter < HHSConstants.INT_ONE)
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.INSERT_CONTRACT_SERVICES_MOD_DETAILS,
					HHSR5Constants.COM_NYC_HHS_MODEL_CB_SERVICESBEAN);
		}
	}

	/**
	 * This method Updates/Modifies the details in Cost-Center Grid Called For
	 * Edit OPeration. Added in R7
	 * @param aoCBGridBeanObj
	 * @param aoMyBatisSession
	 * @return
	 * @throws ApplicationException
	 */
	public boolean editCostCenterModificationDetails(CBServicesBean aoCBGridBeanObj, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		boolean lbEditStatus = false;
		boolean lbError = false;
		try
		{
			// Get Amount Details for server side validation
			CBServicesBean loCBServicesBean = (CBServicesBean) DAOUtil.masterDAO(aoMyBatisSession,
					aoCBGridBeanObj.getId(), HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.FETCH_CONTRACT_COSTCENTER_DETAILS_FOR_VALIDATION, HHSConstants.JAVA_LANG_STRING);
			// Check if Modification/Updated Amount is not less than already
			// invoiced amount
			if ((new BigDecimal(loCBServicesBean.getRemainingAmt()).add(new BigDecimal(aoCBGridBeanObj
					.getModificationAmt())).compareTo(BigDecimal.ZERO)) >= HHSConstants.INT_ZERO)
			{
				// Update/Insert Modification Row
				mergeUpdatesCostCenter(aoMyBatisSession, aoCBGridBeanObj);
			}
			else
			{
				lbError = true;
				if (HHSConstants.ONE.equals(aoCBGridBeanObj.getBudgetTypeId()))
				{
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSR5Constants.NEW_CBM_LESS_THAN_INVOICE_FOR_NEG_AMEND));
				}
				else
				{
					throw new ApplicationException(
							PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									HHSR5Constants.CBM_LESS_THAN_INVOICE_FOR_PI));
				}

			}
			lbEditStatus = true;
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException loExp)
		{
			if (lbError)
			{
				loExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loExp.toString());
			}
			else
			{
				loExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBGridBeanObj.getSubBudgetID());
				loExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_BUDGET, aoCBGridBeanObj.getContractBudgetID());
				LOG_OBJECT
						.Error("Exception occured in ContractBudgetModificationService: editCostCenterModificationDetails method:: ",
								loExp);
				setMoState("Exception occured while updating CostCenter for budget id: "
						+ aoCBGridBeanObj.getContractBudgetID() + " and Sub-Budget id : "
						+ aoCBGridBeanObj.getSubBudgetID());
			}
			throw loExp;
		}
		// Catch any Null pointer exception thrown from the code and wrap it
		// into application
		// Exception and throw it forward
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException("Exception occured while updating Cost-Center ",
					loExp);
			setMoState("Exception occured while updating CostCenter for budget id: "
					+ aoCBGridBeanObj.getContractBudgetID() + " and Sub-Budget id : "
					+ aoCBGridBeanObj.getSubBudgetID());
			loAppEx.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBGridBeanObj.getSubBudgetID());
			loAppEx.addContextData(HHSConstants.ADD_CONTEXT_DATA_BUDGET, aoCBGridBeanObj.getContractBudgetID());
			LOG_OBJECT
					.Error("Exception occured in ContractBudgetModificationService: editCostCenterModificationDetails method:: ",
							loExp);
			throw loAppEx;
		}
		return lbEditStatus;
	}

	/**
	 * This method is called from editCostCenterModificationDetails method.
	 * Update/Modifies the Cost-Center Details.
	 * @param aoMyBatisSession
	 * @param aoCBGridBeanObj
	 * @throws ApplicationException
	 */
	private void mergeUpdatesCostCenter(SqlSession aoMyBatisSession, CBServicesBean aoCBGridBeanObj)
			throws ApplicationException
	{
		// Update Modified Amount of Line Item for Modified
		// SubBudget
		int liUpdateCounter = (Integer) DAOUtil
				.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSR5Constants.UPDATE_CONTRACT_COSTCENTER_MOD_DETAILS,
						HHSR5Constants.COM_NYC_HHS_MODEL_CB_SERVICESBEAN);

		// If modification row of Line Item does not exist then
		// insert a new modification row.
		if (liUpdateCounter < HHSConstants.INT_ONE)
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.INSERT_CONTRACT_COSTCENTER_MOD_DETAILS,
					HHSR5Constants.COM_NYC_HHS_MODEL_CB_SERVICESBEAN);
		}
	}

	/**
	 * This method is added in R7 for Cost Center.This method is used to
	 * validate services amount.
	 * @param aoMyBatisSession
	 * @param aoBudgetId
	 * @return loServicemap- HashMap
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap validateServicesModAmount(SqlSession aoMyBatisSession, String aoBudgetId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into validateServicesModAmount for BudgetId:" + aoBudgetId.getBytes());
		HashMap loServicemap = new HashMap();
		loServicemap.put(HHSConstants.SUCCESS, HHSConstants.STRING_TRUE);
		HashMap aoHashmap = new HashMap<String, String>();
		Integer loFlag = HHSConstants.INT_ZERO;
		BigDecimal loCostCenterModAmountTotal = BigDecimal.ZERO;
		try
		{
			loFlag = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoBudgetId,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.FETCH_FLAG,
					HHSConstants.JAVA_LANG_STRING);
			if (null != loFlag && loFlag == 2)
			{
				costCenterAndServicesSubmitValidation(aoMyBatisSession, aoBudgetId, loServicemap, aoHashmap,
						loCostCenterModAmountTotal);
			}
		}
		// Application Exception handled here
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ContractBudgetModificationService:"
					+ " validateServicesModAmount method:: ", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService: validateServicesModAmount "
					+ "method - failed Exception occured while validating Services Modification Total Amount\n");
			throw aoAppEx;
		}

		// Exception handled here - May occur for any unpredictable situation
		catch (Exception aoEx)
		{
			setMoState("Transaction Failed:: ContractBudgetModificationService: validateServicesModAmount"
					+ " method - failed to validate record " + " \n");
			throw new ApplicationException("Exception occured while validating Services Modification Total Amount  :"
					+ " ContractBudgetModificationService method", aoEx);
		}
		LOG_OBJECT.Debug("Exited from validateServicesModAmount for BudgetId:" + aoBudgetId.getBytes());
		return loServicemap;
	}

	/**
	 * This method is called from validateServicesModAmount method Performs
	 * validation on Submit for Cost-Center and Services Grid
	 * @param aoMyBatisSession
	 * @param aoBudgetId
	 * @param loServicemap
	 * @param aoHashmap
	 * @param loCostCenterModAmountTotal
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private void costCenterAndServicesSubmitValidation(SqlSession aoMyBatisSession, String aoBudgetId,
			HashMap loServicemap, HashMap aoHashmap, BigDecimal loCostCenterModAmountTotal) throws ApplicationException
	{
		List<CBGridBean> loSubBudgetList = null;
		BigDecimal loPIModAmount = BigDecimal.ZERO;
		BigDecimal loServicesModAmountTotal = BigDecimal.ZERO;
		aoHashmap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoBudgetId);
		String lsBudgetType = (String) DAOUtil.masterDAO(aoMyBatisSession, aoBudgetId,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_BUDGET_TYPE,
				HHSConstants.JAVA_LANG_STRING);
		aoHashmap.put(HHSConstants.BUDGET_TYPE_ID, lsBudgetType);
		loSubBudgetList = (List<CBGridBean>) DAOUtil.masterDAO(aoMyBatisSession, aoHashmap,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_SUB_BUDGET_SUMMARY,
				HHSConstants.JAVA_UTIL_HASH_MAP);
		for (int liNumber = 0; liNumber < loSubBudgetList.size(); liNumber++)
		{
			aoHashmap.put(HHSConstants.SUBBUDGET_ID, loSubBudgetList.get(liNumber).getSubBudgetID());
			if(HHSConstants.THREE.equalsIgnoreCase(lsBudgetType))
			{
				loCostCenterModAmountTotal = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, aoHashmap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSR5Constants.FETCH_COST_CENTER_AMOUNT_TOTAL, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			else
			{
				loCostCenterModAmountTotal = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, aoHashmap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.FETCH_COSTCENTER_MOD_TOTAL_AMT, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			if (loCostCenterModAmountTotal.compareTo(BigDecimal.ZERO) != HHSConstants.INT_ZERO)
			{
				loServicemap.put(HHSConstants.SUCCESS, HHSConstants.STRING_FALSE);
				loServicemap.put(HHSConstants.CBL_MESSAGE, HHSR5Constants.CBL_COSTCENTER_MOD_TOTAL_ERROR);
				break;
			}
			loServicesModAmountTotal = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, aoHashmap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.FETCH_SERVICES_MOD_TOTAL_AMT, HHSConstants.JAVA_UTIL_HASH_MAP);
			loPIModAmount = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, aoHashmap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.FETCH_PI_MOD_TOTAL_AMT, HHSConstants.JAVA_UTIL_HASH_MAP);
			if (loServicesModAmountTotal.compareTo(loPIModAmount) != HHSConstants.INT_ZERO)
			{
				loServicemap.put(HHSConstants.SUCCESS, HHSConstants.STRING_FALSE);
				loServicemap.put(HHSConstants.CBL_MESSAGE, HHSR5Constants.CBL_SERVICES_MOD_TOTAL_ERROR);
				break;
			}
		}
	}

	// End: Added in R7 for Cost Center
	// Added in R7 for Program Income
	/**
	 * This method is used to insert a row in program income table when a new
	 * row is added during modification
	 * @param aoMyBatisSession SqlSession
	 * @param aoCBGridBeanObj CBProgramIncomeBean object
	 * @return Boolean insertion status
	 * @throws ApplicationException
	 */
	public Boolean addProgramIncomeModification(SqlSession aoMybatisSession, CBProgramIncomeBean aoCBGridBeanObj)
			throws ApplicationException
	{

		boolean lbInsertStatus = false;
		HashMap loHashMapForModification = null;
		Boolean loError = false;

		try
		{
			if (aoCBGridBeanObj != null)
			{
				// Validation will be done for remaining amount
				loHashMapForModification = validateProgramIncomeModificationData(aoMybatisSession, aoCBGridBeanObj);
				if ((Boolean) loHashMapForModification.get(HHSConstants.IS_VALID_DATA))
				{
					DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSR5Constants.INSERT_PI_MODIFICATION, HHSConstants.PROGRAM_INCOME_BEAN);
					lbInsertStatus = true;
				}
				else
				{
					// Error message thrown once the modify amount is an invalid
					// amount
					loError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							(String) loHashMapForModification.get(HHSConstants.ERROR_MESSAGE)));
				}
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			if (loError)
			{
				// In case of modification data validation failure, the
				// exception will be thrown back to base controller
				// along with the validation message
				loExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loExp.toString());
			}
			loExp.addContextData(
					"ApplicationException occured while inserting program income :  addProgramIncomeAmendment", loExp);
			LOG_OBJECT.Error("ApplicationException occured while inserting program income : addProgramIncomeAmendment "
					+ loExp);
			setMoState("ApplicationException occured while inserting  program income for budget id = "
					+ aoCBGridBeanObj.getContractBudgetID() + " and subbudgetid = " + aoCBGridBeanObj.getSubBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while inserting  program income :  addProgramIncomeAmendment ", loExp);
			loAppEx.addContextData("Exception occured while inserting program income :  addProgramIncomeAmendment",
					loExp);
			LOG_OBJECT.Error("Exception occured while inserting program income: addProgramIncomeAmendment ", loExp);
			setMoState("Exception occured while inserting Emplyee budget for budget id = "
					+ aoCBGridBeanObj.getContractBudgetID() + " and subbudgetid = " + aoCBGridBeanObj.getSubBudgetID());
			throw loAppEx;
		}
		return lbInsertStatus;
	}

	/**
	 * This method generates the LineItemMasterBean object for a particular
	 * Amendment SubBudget. 1. Ftech List of bean for all the sub-budget
	 * tabs(Rent, Rate, Personnel Service) 2. Set all the list into
	 * LineItemMasterBean object
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoCBGridBean CBGridBean
	 * @param aoCBService ContractBudgetService
	 * @return loLineItemBean LineItemMasterBean
	 * @throws ApplicationException ApplicationException object
	 */

	@SuppressWarnings("unchecked")
	private LineItemMasterBean generateMasterBeanModification(SqlSession aoMyBatisSession, CBGridBean aoCBGridBean,
			ContractBudgetService aoCBService) throws ApplicationException
	{
		List<CBProgramIncomeBean> loProgramincomeBeanList = fetchProgramIncomeModification(aoCBGridBean,
				aoMyBatisSession, null);
		List<SiteDetailsBean> loSiteDetailsBean = new ContractBudgetAmendmentService().fetchSubBudgetSiteDetails(
				aoMyBatisSession, aoCBGridBean, null, false);
		List<PersonnelServiceBudget> loPersonnelServiceEmployee = generatePersonnelService(aoMyBatisSession,
				aoCBGridBean);
		List<RateBean> loRateBeanList = fetchContractBudgetModificationRate(aoMyBatisSession, aoCBGridBean, null);
		List<CBMileStoneBean> loMilestoneBeanList = fetchMilestone(aoCBGridBean, aoMyBatisSession, null);
		List<CBEquipmentBean> loEquipmentBeanList = fetchModificationEquipment(aoMyBatisSession, aoCBGridBean, null);
		List<CBIndirectRateBean> loIndirectBeanList = new ContractBudgetAmendmentService().fetchIndirectRate(
				aoMyBatisSession, aoCBGridBean, null);
		List<CBOperationSupportBean> loOpsBeanList = fetchModificationOTPS(aoMyBatisSession, aoCBGridBean, null);
		List<CBProfessionalServicesBean> loProfserviceBeanList = cbmFetchProfServicesDetails(aoCBGridBean,
				aoMyBatisSession, null);
		List<CBUtilities> loUtilityBeanList = new ContractBudgetAmendmentService().fetchUtilitiesAmendment(
				aoMyBatisSession, aoCBGridBean, null);
		List<Rent> loRentBeanList = fetchModificationRent(aoMyBatisSession, aoCBGridBean, null);
		List<UnallocatedFunds> loUnallocatedBeanList = fetchModificationUnallocatedFunds(aoMyBatisSession,
				aoCBGridBean, null);
		List<ContractedServicesBean> loContractedserviceBeanList = generateContractedServices(aoMyBatisSession,
				aoCBGridBean);
		// Start:Added in R7 for Cost-Center
		List<CBServicesBean> loServicesBeanList =fetchContractServicesModificationGrid(aoCBGridBean, aoMyBatisSession, null);
		List<CBServicesBean> loCostCenterBeanList = fetchContractCostCenterModificationGrid(aoCBGridBean, aoMyBatisSession,
				null);
		// End: Added in R7 for Cost-Center
		String lsIndirectRatePercent = aoCBService.updateIndirectRatePercentage(aoMyBatisSession, aoCBGridBean, null);
		// added in R7: Update PI Indirect Rate percent
		String lsIndirectPIRatePercent = aoCBService.updatePIIndirectRatePercentage(aoMyBatisSession, aoCBGridBean,
				null);
		// End in R7: Update PI Indirect Rate percent
		PersonnelServicesData loNonGridPSData = aoCBService.fetchPersonnelServiceData(aoMyBatisSession, aoCBGridBean,
				null);
		CBOperationSupportBean loNonGridOPSData = fetchOpAndSupportModPageData(aoCBGridBean, aoMyBatisSession, null);
		ContractedServicesBean loNonGridConServiceData = fetchNonGridContractedServicesModification(aoMyBatisSession,
				aoCBGridBean, null);
		ContractBudgetSummary loContractBudgetSummary = aoCBService.fetchModificationBudgetSummary(aoMyBatisSession,
				aoCBGridBean, null);
		LineItemMasterBean loLineItemBean = new LineItemMasterBean();
		loLineItemBean.setSubbudgetId(aoCBGridBean.getSubBudgetID());
		loLineItemBean.setContractedserviceBeanList(loContractedserviceBeanList);
		loLineItemBean.setEquipmentBeanList(loEquipmentBeanList);
		loLineItemBean.setIndirectBeanList(loIndirectBeanList);
		loLineItemBean.setMilestoneBeanList(loMilestoneBeanList);
		loLineItemBean.setOpsBeanList(loOpsBeanList);
		loLineItemBean.setPersonnelserviceBeanList(loPersonnelServiceEmployee);
		loLineItemBean.setProfserviceBeanList(loProfserviceBeanList);
		loLineItemBean.setProgramincomeBeanList(loProgramincomeBeanList);
		loLineItemBean.setRateBeanList(loRateBeanList);
		loLineItemBean.setRentBeanList(loRentBeanList);
		loLineItemBean.setUnallocatedBeanList(loUnallocatedBeanList);
		loLineItemBean.setUtilityBeanList(loUtilityBeanList);
		loLineItemBean.setIndirectRatePercent(lsIndirectRatePercent);
		// added in R7: Update PI Indirect Rate percent
		loLineItemBean.setPiIndirectRatePercent(lsIndirectPIRatePercent);
		// End in R7: Update PI Indirect Rate percent
		// Start:Added in R7 for Cost-Center
		loLineItemBean.setServicesBeanList(loServicesBeanList);
		loLineItemBean.setCostCenterBeanList(loCostCenterBeanList);
		// End: Added in R7 for Cost-Center
		loLineItemBean.setNonGridPSData(loNonGridPSData);
		loLineItemBean.setNonGridOPSData(loNonGridOPSData);
		loLineItemBean.setNonGridConServiceData(loNonGridConServiceData);
		loLineItemBean.setLoBudgetSummary(loContractBudgetSummary);
		loLineItemBean.setSiteDetailsBeanList(loSiteDetailsBean);
		return loLineItemBean;

	}

	/**
	 * The method is added in Release 7 for making snapshot of approved
	 * modification. This method will merge list of Salaried, Hourly, Seasonal
	 * and Fringe Employees into a single List which is required to be stored in
	 * XML of amendment budget.
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoCBGridBean CBGridBean
	 * @return loPersonnelServiceEmployee List of PersonnelServiceBudget
	 * @throws ApplicationException ApplicationException object
	 */

	private List<PersonnelServiceBudget> generatePersonnelService(SqlSession aoMyBatisSession, CBGridBean aoCBGridBean)
			throws ApplicationException
	{
		List<PersonnelServiceBudget> loPersonnelServiceEmployee = new ArrayList<PersonnelServiceBudget>();
		List<PersonnelServiceBudget> loSalariedEmployee = fetchSalariedEmployeeBudgetForModification(aoMyBatisSession,
				aoCBGridBean, null);
		List<PersonnelServiceBudget> loHourlyEmployee = fetchHourlyEmployeeBudgetForModification(aoMyBatisSession,
				aoCBGridBean, null);
		List<PersonnelServiceBudget> loSeasonalEmployee = fetchSeasonalEmployeeBudgetForModification(aoMyBatisSession,
				aoCBGridBean, null);
		List<PersonnelServiceBudget> loFringeEmployee = fetchFringeBenifitsForModification(aoMyBatisSession,
				aoCBGridBean, null);

		PersonnelServiceBudget loPSObject = null;
		Iterator<PersonnelServiceBudget> aoListIterator = loSalariedEmployee.iterator();
		while (aoListIterator.hasNext())
		{
			loPSObject = aoListIterator.next();
			loPSObject.setEmpType(HHSConstants.ONE);
			loPersonnelServiceEmployee.add(loPSObject);
		}
		aoListIterator = loHourlyEmployee.iterator();
		while (aoListIterator.hasNext())
		{
			loPSObject = aoListIterator.next();
			loPSObject.setEmpType(HHSConstants.TWO);
			loPersonnelServiceEmployee.add(loPSObject);
		}
		aoListIterator = loSeasonalEmployee.iterator();
		while (aoListIterator.hasNext())
		{
			loPSObject = aoListIterator.next();
			loPSObject.setEmpType(HHSConstants.THREE);
			loPersonnelServiceEmployee.add(loPSObject);
		}
		aoListIterator = loFringeEmployee.iterator();
		while (aoListIterator.hasNext())
		{
			loPSObject = aoListIterator.next();
			loPSObject.setEmpType(HHSConstants.FOUR);
			loPersonnelServiceEmployee.add(loPSObject);
		}
		return loPersonnelServiceEmployee;
	}

	/**
	 * The method is added in Release 7 for making snapshot of approved
	 * modification. This method will merge list of Consultants, Sub-Contractors
	 * and Vendors into a single List which is required to be stored in XML of
	 * amendment budget.
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoCBGridBean CBGridBean
	 * @return loContractedServices List of ContractedServicesBean
	 * @throws ApplicationException ApplicationException object
	 */

	private List<ContractedServicesBean> generateContractedServices(SqlSession aoMyBatisSession, CBGridBean aoCBGridBean)
			throws ApplicationException
	{
		List<ContractedServicesBean> loContractedServices = new ArrayList<ContractedServicesBean>();
		List<ContractedServicesBean> loContractedServicesForConsultants = fetchContractedServicesModificationConsultants(
				aoMyBatisSession, aoCBGridBean, null);
		List<ContractedServicesBean> loContractedServicesForSubContractor = fetchContractedServicesModificationSubContractors(
				aoMyBatisSession, aoCBGridBean, null);
		List<ContractedServicesBean> loContractedServicesForVendor = fetchContractedServicesModificationVendors(
				aoMyBatisSession, aoCBGridBean, null);

		ContractedServicesBean loCSObject = null;
		Iterator<ContractedServicesBean> aoListIterator = loContractedServicesForConsultants.iterator();
		while (aoListIterator.hasNext())
		{
			loCSObject = aoListIterator.next();
			loCSObject.setSubHeader(HHSConstants.ONE);
			loContractedServices.add(loCSObject);
		}
		aoListIterator = loContractedServicesForSubContractor.iterator();
		while (aoListIterator.hasNext())
		{
			loCSObject = aoListIterator.next();
			loCSObject.setSubHeader(HHSConstants.TWO);
			loContractedServices.add(loCSObject);
		}
		aoListIterator = loContractedServicesForVendor.iterator();
		while (aoListIterator.hasNext())
		{
			loCSObject = aoListIterator.next();
			loCSObject.setSubHeader(HHSConstants.THREE);
			loContractedServices.add(loCSObject);
		}

		return loContractedServices;
	}

	/**
	 * The method is added in Release 7. It generates the MasterBean object for
	 * a particular Modification Budget, which will be further converted to XML
	 * and will be stored in FileNet db. The method is invoked when Contract
	 * Budget Modification review task is in final approval stage.
	 * @param aoMyBatisSession
	 * @param aoTaskDetailsBean
	 * @param aoRequiredMap
	 * @return lsConvertedXml
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public String generateModificationBudget(SqlSession aoMyBatisSession, TaskDetailsBean aoTaskDetailsBean,
			HashMap aoRequiredMap) throws ApplicationException
	{
		LOG_OBJECT.Info("Modification document inserting in filenet db");
		String lsDocId = null;
		String lsModificationBudgetId = null;
		String lsConvertedXml = null;
		aoRequiredMap.put(HHSR5Constants.FINAL_TASK_LEVEL_FLAG, true);
		List<HashMap<String, Object>> loSubBudgetDetails;
		List<LineItemMasterBean> loLineItemList = new ArrayList<LineItemMasterBean>();
		MasterBean loMasterBean = new MasterBean();
		ContractBudgetService loCBService = new ContractBudgetService();
		try
		{
			lsModificationBudgetId = aoTaskDetailsBean.getBudgetId();
			aoRequiredMap.put(HHSR5Constants.Org_Id, aoTaskDetailsBean.getUserId());
			aoRequiredMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsModificationBudgetId);
			String lsParentBudgetId = (String) DAOUtil.masterDAO(aoMyBatisSession, aoRequiredMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.FETCH_PARENT_BUDGET_ID,
					HHSConstants.JAVA_UTIL_MAP);
			// Fetching list of subbudget details
			loSubBudgetDetails = (List<HashMap<String, Object>>) DAOUtil.masterDAO(aoMyBatisSession, aoRequiredMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER, HHSConstants.FETCH_SUB_BUDGET_DETAILS,
					HHSConstants.JAVA_UTIL_MAP);
			// Fetching ststus of Amendment Budget
			String lsBudgetStatusId = (String) DAOUtil.masterDAO(aoMyBatisSession, aoRequiredMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
					HHSConstants.FETCH_AMENDMENT_BUDGET_STATUS, HHSConstants.JAVA_UTIL_MAP);

			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setAmendmentContractID(aoTaskDetailsBean.getContractId());
			loCBGridBean.setContractBudgetID(lsModificationBudgetId);
			loCBGridBean.setParentBudgetId(lsParentBudgetId);
			loCBGridBean.setBudgetStatusId(lsBudgetStatusId);
			loCBGridBean.setBudgetTypeId(HHSConstants.THREE);
			// For each subbudget, generate LineItemMasterBean
			Iterator<HashMap<String, Object>> aoListIterator = loSubBudgetDetails.iterator();
			loMasterBean.setBudgetId(lsModificationBudgetId);
			LOG_OBJECT.Info("CBGrid bean" + loCBGridBean);
			while (aoListIterator.hasNext())
			{
				HashMap<String, Object> loInnerHashMap = aoListIterator.next();
				String lsAmendmentSubBudgetId = loInnerHashMap.get(HHSConstants.CBM_SUB_BUDGET_ID).toString();
				String lsParentSubBudgetId = loInnerHashMap.get(HHSConstants.CBA_PARENT_ID).toString();
				loCBGridBean.setSubBudgetID(lsAmendmentSubBudgetId);
				loCBGridBean.setParentSubBudgetId(lsParentSubBudgetId);
				// Added in R7 to reset entry type Id while iterating sub busget
				// list
				loCBGridBean.setEntryTypeId(null);
				// R7 End
				LineItemMasterBean loLineItemBean = generateMasterBeanModification(aoMyBatisSession, loCBGridBean,
						loCBService);
				loLineItemList.add(loLineItemBean);
			}
			LOG_OBJECT.Info("Final Line Item list:::" + loLineItemList);
			// Set data into MasterBean
			loMasterBean.setMasterBeanList(loLineItemList);
			loMasterBean.setBudgetDetails(new ContractBudgetAmendmentService().fetchFyBudgetSummary(aoMyBatisSession,
					(HashMap<String, String>) aoRequiredMap, null, loCBGridBean));
			loMasterBean.setAdvanceSummaryBean(loCBService.fetchAdvanceDetails(aoMyBatisSession, loCBGridBean, null));
			loMasterBean.setAssignmentsSummaryBean(loCBService.fetchAssignmentSummary(aoMyBatisSession, loCBGridBean,
					null));
			// Convert MasterBean object to XML String
			lsConvertedXml = new ContractBudgetAmendmentService().convertMasterListToXml(loMasterBean);
			LOG_OBJECT.Info("XML inserted:::" + lsDocId);
		}
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData(
					"ApplicationException occured while generating Modification Budget data :  generateModificationBudget",
					aoExp);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException(
					"Exception occured while generating Modification Budget data :  generateModificationBudget", aoExp);
		}
		return lsConvertedXml;

	}

	/**
	 * The method is added in Release 7. It will be stored in FileNet db. The
	 * method is invoked when Contract Budget Modification review task is in
	 * final approval stage.
	 * @param aoMyBatisSession
	 * @param aoTaskDetailsBean
	 * @param aoUserSession
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public String uploadModificationBudgetData(SqlSession aoMyBatisSession, TaskDetailsBean aoTaskDetailsBean,
			P8UserSession aoUserSession, String aoConvertedXml, HashMap<String, Object> aoRequiredMap)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Modification document inserting in filenet db" + aoTaskDetailsBean);
		String lsDocId = null;
		FileInputStream loFIS = null;
		String lsModificationBudgetId = (String) aoRequiredMap.get(HHSConstants.BUDGET_ID_WORKFLOW);
		Boolean lbFinalLevel = (Boolean) aoRequiredMap.get(HHSR5Constants.FINAL_TASK_LEVEL_FLAG);
		try
		{
			if (lbFinalLevel && null != aoConvertedXml)
			{
				LOG_OBJECT.Info("Started uploading snapshot");
				String lsUserId = (String) aoRequiredMap.get(HHSR5Constants.Org_Id);
				loFIS = HHSUtil.convertXmlToStream(aoConvertedXml);
				HashMap loFileNetHashMap = new HashMap();
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_DOC_TYPE, HHSR5Constants.BUDGET_MODIFICATION_TEMPLATE);
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, HHSR5Constants.BUDGET_MODIFICATION_XML
						+ lsModificationBudgetId);
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID, lsUserId);
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_ORGANIZATION_ID, HHSConstants.USER_AGENCY);
				loFileNetHashMap.put(HHSR5Constants.Org_Id, lsUserId);
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, Boolean.FALSE);
				loFileNetHashMap.put(P8Constants.MIME_TYPE, HHSConstants.XML_MIME_TYPE);
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_FILE_TYPE, HHSConstants.XML_FILE_TYPE);
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY, lsUserId);
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, lsUserId);
				HashMap<String, Object> loReturnMap = new P8ContentService().createDVdocument(aoUserSession, loFIS,
						loFileNetHashMap, false, false);
				lsDocId = (String) loReturnMap.get(HHSConstants.DOC_ID);
				aoRequiredMap.put(HHSConstants.DOC_ID, lsDocId);
				DAOUtil.masterDAO(aoMyBatisSession, aoRequiredMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.UPDATE_BUDGET_WITH_DOC_ID, HHSConstants.JAVA_UTIL_MAP);
				LOG_OBJECT.Info("DocID inserted:::" + lsDocId);
			}
		}
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData(
					"ApplicationException occured while generating Modification Budget data :  uploadModificationBudgetData",
					aoExp);
			LOG_OBJECT
					.Error("ApplicationException occured while generating Modification Budget data : uploadModificationBudgetData "
							+ aoExp);
			setMoState("ApplicationException occured while generating Modification Budget data for budget id = "
					+ lsModificationBudgetId);
			throw aoExp;
		}
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while generating Modification Budget data :  uploadModificationBudgetData",
					aoExp);
			loAppEx.addContextData(
					"Exception occured while generating Modification Budget data :  uploadModificationBudgetData",
					aoExp);
			LOG_OBJECT
					.Error("Exception occured while generating Modification Budget data : uploadModificationBudgetData "
							+ aoExp);
			setMoState("Exception occured while generating Modification Budget data for budget id = "
					+ lsModificationBudgetId);
			throw loAppEx;
		}
		finally
		{
			try
			{
				if (loFIS != null)
				{
					loFIS.close();
				}
			}
			catch (Exception loExp)
			{
				throw new ApplicationException(
						"Exception occured while closing the FileInputStream in finally block :  uploadModificationBudgetData",
						loExp);
			}
		}
		return lsDocId;
	}

	// Start : added in R7
	/**
	 * This method is called for Cancel and merge out year functionality
	 * @param aoMybatisSession
	 * @param asContractId
	 * @return
	 * @throws ApplicationException
	 */
	public boolean deleteCancelledAmendmentBudget(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into deleteCancelledAmendmentBudget::");
		Boolean lbStatus = false;
		String lsParentContractId = null;
		// String[] lsConractList = new String[2];
		List<String> lsBudgetList = null;
		String lsContractId = null;
		TaskDetailsBean loTaskBean = new TaskDetailsBean();
		// Fetch Parent ContractId
		lsParentContractId = (String) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.FETCH_PARENT_CONTRACT_ID,
				HHSConstants.JAVA_UTIL_MAP);
		List<String> lsContractList = new ArrayList<String>();
		lsContractId = (String) aoHashMap.get(HHSConstants.CONTRACT_ID1);
		lsContractList.add(lsContractId);
		lsContractList.add(lsParentContractId);

		aoHashMap.put(HHSR5Constants.CONTRACT_ID_FOR_LIST, lsContractList);
		lsBudgetList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
				HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.FETCH_BUDGET_IDS_FOR_DELETION, HHSConstants.JAVA_UTIL_MAP);
		deleteOutYearBudgets(aoMybatisSession, lsBudgetList);
		// R7.2.0 BEGIN :Added for 9051
		loTaskBean.setContractId(lsContractId);
		DAOUtil.masterDAO(aoMybatisSession, loTaskBean,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
				HHSConstants.UPDATE_AMEND_CONTRACT_STATUS_PEND_REGISTRATION,
				HHSConstants.CS_TASK_DETAILS_BEAN);
		// R7.2.0 END: Added for 9051


		lbStatus = true;
		LOG_OBJECT.Debug("Exited deleteCancelledAmendmentBudget::");
		return lbStatus;
	}

	/**
	 * This methos is called from mergeCancelledAmendmentBudget
	 * @param aoMybatisSession
	 * @param loContractBudgetBean
	 * @throws ApplicationException
	 */
	private void deleteOutYearBudgets(SqlSession aoMybatisSession, List<String> asBudgetList)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into deleteOutYearBudgets:: " + asBudgetList);
		try
		{
			if (asBudgetList != null)
			{
				List<String> loBudgetIdList = asBudgetList;
				for (String lsBudgetId : loBudgetIdList)
				{
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
							HHSR5Constants.DELETE_PERSONNEL_SERVICE_DETAIL_FORFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
							HHSR5Constants.DELETE_FRINGE_DETAIL_FORFY, HHSConstants.JAVA_LANG_STRING);
					// Add cost center as well
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
							HHSR5Constants.DELETE_SERVICES, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
							HHSR5Constants.DELETE_SERVICES_BUDGET, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
							HHSR5Constants.DELETE_COSTCENTER, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
							HHSR5Constants.DELETE_SERVICES_CONFIG, HHSConstants.JAVA_LANG_STRING);
					
					
					//
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_PERSONNEL_SERVICE_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_OP_SUPPORT_OTHERS_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_OP_SUPPORT_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_UTILITIES_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_PROFF_SERVICE_OTHERS_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_PROFF_SERVICE_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_RENT_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_CONTRACTED_SERVICE_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_RATE_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_MILESTONE_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_UNALLOCATED_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_INDIRECT_RATE_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_PROG_INCOME_OTHERS_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_PROGRAM_INCOME_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_FRINGE_BENEFIT_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_EQUIPMENT_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_SUB_BUDGET_SITE_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_SUB_BUDGET_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_BUDGET_CUSTOMIZ_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_BUDGET_DOC_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, lsBudgetId, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.DELETE_ASSOCIATED_BUDGETS_CANCEL_NEWFY, HHSConstants.JAVA_LANG_STRING);
				}
				LOG_OBJECT.Debug("Exited deleteOutYearBudgets:: " + asBudgetList);
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Application Exception occured while deleting OutYearBudgets", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService:deleteOutYearBudgets method - failed to delete record "
					+ ": /n");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured while deleting OutYearBudgets", loEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService:deleteOutYearBudgets method - failed to delete record "
					+ " \n");
			throw new ApplicationException("Exception occured while deleting OutYearBudgets", loEx);
		}
	}

	// End : added in R7
	//R7 Start: PI indirect percentage
	/**
	 * This method is added in R7. This method will update the PI indirect rate
	 * percentage on the budget approval
	 * @param aoMybatisSession
	 * @param aoTaskDetailsBean
	 * @param aoSubBudgetDetails
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean updatePIPercentforSubBudget(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean,
			ArrayList<SiteDetailsBean> aoSubBudgetDetails) throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		Boolean loPIUpdatedStatus = false;
		try
		{
			LOG_OBJECT.Info("Entered inside updatePIPercentforSubBudget for Amendment");
			String lsBudgetEntryTypeId = (String) DAOUtil.masterDAO(aoMybatisSession, aoTaskDetailsBean.getBudgetId(),
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.FETCH_BUDGET_TYPE_ID,
					HHSConstants.JAVA_LANG_STRING);
			loCBGridBean.setBudgetTypeId(lsBudgetEntryTypeId);
			loCBGridBean.setBudgetStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSR5Constants.STATUS_BUDGET_APPROVED));
			loCBGridBean.setModifyByAgency(aoTaskDetailsBean.getUserId());
			loCBGridBean.setContractBudgetID(aoTaskDetailsBean.getBudgetId());
			ContractBudgetService aoCBService = new ContractBudgetService();
			for (SiteDetailsBean loSiteDetailsBean : aoSubBudgetDetails)
			{
				loCBGridBean.setSubBudgetID(loSiteDetailsBean.getSubBudgetId());
				loCBGridBean.setParentSubBudgetId(loSiteDetailsBean.getParentSubBudgetId());
				aoCBService.updatePIIndirectRatePercentage(aoMybatisSession, loCBGridBean, null);
			}
			loPIUpdatedStatus = true;
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting the message for Contract Id:");
			throw loExp;
		}
		catch (Exception aoAppExp)
		{
			LOG_OBJECT.Error("Error while getting the message", aoAppExp);
			throw new ApplicationException("Error while getting the message", aoAppExp);
		}
		return loPIUpdatedStatus;
	}
	//R7 End: PI indirect percentage
	//Added in R7 for program income
	/**
	 * This method is added in R7. This method will update old_pi_flag in modification budget if 
	 * base budget created pre R7 has old PI and we are initiating modification post R7
	 * @param aoMybatisSession
	 * @param aoContractFilterBean
	 * @param asBudgetId
	 * @return Integer 
	 * @throws ApplicationException
	 */
	public Integer updateIsOldPIFlagForModification(SqlSession aoMybatisSession, String asBudgetId)
			throws ApplicationException
	{

		Integer lirowsUpdated = HHSConstants.INT_ZERO;
		try
		{
			LOG_OBJECT.Info("Entered inside updateIsOldPIFlagForModification");
			lirowsUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, asBudgetId,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.UPDATE_IS_OLD_PI_FLAG_FOR_OLD_PI,
					HHSConstants.JAVA_LANG_STRING);
			LOG_OBJECT.Debug("Updated is_old_pi flag!! No. of rows updated with flag as 1 : " + lirowsUpdated);

		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while updating flag in updateIsOldPIFlagForModification:");
			throw loExp;
		}
		catch (Exception aoAppExp)
		{
			LOG_OBJECT.Error("Error while updating flag in updateIsOldPIFlagForModification", aoAppExp);
			throw new ApplicationException("Error while updating flag in updateIsOldPIFlagForModification", aoAppExp);
		}
		return lirowsUpdated;
	}
	//R7 End: Program income changes
	
	/**
	 * Method added in R7 for fetching Approved modifications details.
	 * @param aoCBGridBeanObj
	 * @param aoMasterBean
	 * @return
	 * @throws ApplicationException
	 */
	private List<CBProgramIncomeBean> fetchPIDetailsforApprovedModifications(CBGridBean aoCBGridBeanObj,
			MasterBean aoMasterBean) throws ApplicationException
	{
		List<CBProgramIncomeBean> loCBProgramIncomeBeanList = null;
		String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
		// Updated in R7 for PI Filter
		try
		{
			List<CBProgramIncomeBean> loTempProgramBean =  new ContractBudgetAmendmentService().fetchProgramIncomeFromXML(lsSubBudgetId,aoMasterBean);
           /*[Start] R7.3.2 add null exception handling*/
			if(loTempProgramBean ==  null)  {
			    loTempProgramBean = new ArrayList<CBProgramIncomeBean>();
			}
           /*[End] R7.3.2 add null exception handling*/
			
			String lsEntryTypeId = aoCBGridBeanObj.getEntryTypeId();
			loCBProgramIncomeBeanList = new ArrayList<CBProgramIncomeBean>();
			if (null != loCBProgramIncomeBeanList && null != lsEntryTypeId
					&& lsEntryTypeId != HHSR5Constants.ENTRY_TYPE_PROGRAM_INCOME)
			{
				Iterator<CBProgramIncomeBean> loPIBeanIterator = loTempProgramBean.iterator();
				while (loPIBeanIterator.hasNext())
				{
					CBProgramIncomeBean loFilterCBBean = loPIBeanIterator.next();
					if ((lsEntryTypeId.equalsIgnoreCase(loFilterCBBean.getEntryTypeId())))
					{
						loCBProgramIncomeBeanList.add(loFilterCBBean);
					}
				}
			}
			else
			{
				loCBProgramIncomeBeanList.addAll(loTempProgramBean);
			}
		}
		catch (ApplicationException loApx)
		{
			setMoState("Error in fetching details for approved modifications:");
			throw loApx;
		}
		catch (Exception aoAppExp)
		{
			LOG_OBJECT.Error("Error in fetching details for approved modifications:", aoAppExp);
			throw new ApplicationException("Error in fetching details for approved modifications:", aoAppExp);
		}
		return loCBProgramIncomeBeanList;
	}
	
	/**
	 * This method will merge the modification amount if modification is done on existing PI.
	 * if new PI row added, it will append the new row.
	 * @param asIsOldPI
	 * @param aoInputMapBean
	 * @param aoCBProgramIncomeBeanList
	 * @param aoCBOPModList
	 * @param asParentSubBudgetId
	 * @return
	 */
	private List<CBProgramIncomeBean> mergeApprovedPIDetailsForModifications(String asIsOldPI,
			HashMap<String, CBProgramIncomeBean> aoInputMapBean, List<CBProgramIncomeBean> aoCBProgramIncomeBeanList,
			List<CBProgramIncomeBean> aoCBOPModList,String asParentSubBudgetId,CBGridBean aoCBGridBeanObj)
	{
		if (HHSConstants.ZERO.equals(asIsOldPI))
		{
			if (aoCBOPModList != null && !aoCBOPModList.isEmpty())
			{
				for (CBProgramIncomeBean loProgramBean : aoCBOPModList)
				{
					String lsModID = loProgramBean.getId();
					if (aoInputMapBean.containsKey(lsModID))
					{
						aoInputMapBean.get(lsModID)
								.setModificationAmount(loProgramBean.getModificationAmount());
					}
					else
					{
						if(!(aoCBGridBeanObj.getEntryTypeId()== null))
						{
						//Appended new record for new rows added during modification/update
						loProgramBean.setId(loProgramBean.getId() + HHSConstants.NEW_RECORD_CONTRACT_SERVICES);
						}
						aoInputMapBean.put(lsModID, loProgramBean);
					}
				}
				aoCBProgramIncomeBeanList.clear();
				for (Map.Entry<String, CBProgramIncomeBean> loProgramIncomeOutputBean : aoInputMapBean
						.entrySet())
				{
					aoCBProgramIncomeBeanList.add(loProgramIncomeOutputBean.getValue());
					// To sort the list in increasing order of entry type id
					Collections.sort(aoCBProgramIncomeBeanList, new Comparator<CBProgramIncomeBean>()
					{
						public int compare(final CBProgramIncomeBean object1, final CBProgramIncomeBean object2)
						{
							return object1.getEntryTypeId().compareTo(object2.getEntryTypeId());
						}
					});
				}
			}
		}
		else
		{
			// set modification amounts in program income Bean
			for (int liCount = HHSConstants.INT_ZERO; liCount < aoCBProgramIncomeBeanList.size(); liCount++)
			{
				String lsID = aoCBProgramIncomeBeanList.get(liCount).getId();
				String lsModID = aoCBOPModList.get(liCount).getId();
				if (lsModID != null && lsID.equalsIgnoreCase(lsModID))
				{
					aoCBProgramIncomeBeanList.get(liCount).setModificationAmount(
							aoCBOPModList.get(liCount).getModificationAmount());
				}
			}
		}
		return aoCBProgramIncomeBeanList;
	}
	//Added for R7 for Auto approval
	/**
	 * The method is added in Release 7. At the time of setAssignnee component,
	 * it will check the update budget if it's IS_AUTO_APPROVED flag in budget
	 * table is true or false. If it is true, it satisfies the auto approval
	 * criteria.
	 * @param aoUserSession
	 * @param aoMybatisSession
	 * @param aoTaskDetailsBean
	 * @param asContractId
	 * @return lbAutoUpdate
	 * @throws ApplicationException
	 */
	public Boolean checkAutoApprovalForContractConfigurationUpdateTask(P8UserSession aoUserSession,
			SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean, String asContractId)
			throws ApplicationException
	{
		boolean lbAutoUpdate = false;
		try
		{
			LOG_OBJECT.Info("TASK BEAN:::" + aoTaskDetailsBean);
			// if class name is not null execute preprocessing and if satisfy
			// threshold put system user in loReturnData
			Object loObj = Class.forName("com.nyc.hhs.preprocessor.PreprocessorUpdateApproval").newInstance();
			PreprocessorUpdateApproval loPreProcessing = (PreprocessorUpdateApproval) loObj;
			lbAutoUpdate = loPreProcessing.checkAutoApprovalForUpdateBudgetTask(aoUserSession, aoMybatisSession,
					aoTaskDetailsBean, asContractId);
			LOG_OBJECT.Info("Auto Update status for Contract Id:" + asContractId + " is :::" + lbAutoUpdate);
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting the message for Contract Id:");
			throw loExp;
		}
		catch (Exception aoAppExp)
		{
			LOG_OBJECT.Error("Error while getting the message", aoAppExp);
			throw new ApplicationException("Error while getting the message", aoAppExp);
		}
		return lbAutoUpdate;
	}

	/**
	 * The method is added in Release 7.
	 * @param aoMybatisSession
	 * @param aoHMWFRequiredProps
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean checkBudgetsForUpdateAutoApproval(SqlSession aoMybatisSession,
			HashMap<String, Object> aoHMWFRequiredProps) throws ApplicationException
	{
		LOG_OBJECT.Info("Entering into checkBudgetsForUpdateAutoApproval method with parameters::: "
				+ aoHMWFRequiredProps);
		boolean isUpdateAutoApproved = false;
		try
		{
			String lsUpdateAutoApproved = (String) DAOUtil.masterDAO(aoMybatisSession, aoHMWFRequiredProps,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, "getUpdateBudgeAutoApproveFlag",
					HHSConstants.JAVA_UTIL_HASH_MAP);
			if (null != lsUpdateAutoApproved && lsUpdateAutoApproved.equals(HHSConstants.ONE))
				isUpdateAutoApproved = true;
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while fetching budgets in fetchBudgetsForModificationAutoApproval",
					aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService:checkBudgetsForUpdateAutoApproval method - failed to fetch record "
					+ " \n");
			throw aoAppEx;
		}
		return isUpdateAutoApproved;
	}
	/**
	 * Added in Release 7.
	 * The method will merge budget whose task is approved by auto approve batch
	 * @param aoMyBatisSession
	 * @param aoUserSession
	 * @param aoTaskDetailBean
	 * @param abdispatchFinishStatus
	 * @return loStatus
	 * @throws ApplicationException
	 */
	public Boolean mergeBudget(SqlSession aoMyBatisSession, P8UserSession aoUserSession,
			TaskDetailsBean aoTaskDetailBean, Boolean abdispatchFinishStatus) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered ContractBudgetModificationService.mergeBudget():::");
		Boolean loStatus = HHSConstants.BOOLEAN_FALSE;
		try
		{
			String lsCurrentLevel = aoTaskDetailBean.getLevel();
			String lsTotalLevelOfTask = aoTaskDetailBean.getTotalLevel();
			if (lsCurrentLevel.equalsIgnoreCase(lsTotalLevelOfTask))
			{
				loStatus = mergeBudgets(aoMyBatisSession, aoUserSession, aoTaskDetailBean);
			}
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while ContractBudgetModificationService.mergeBudget()");
			throw aoAppex;
		}
		LOG_OBJECT.Debug("Exited ContractBudgetModificationService.mergeBudget():::");
		return loStatus;
	}
	/**
	 * The method is added in release 7. It will create property hashmap for
	 * task.
	 * @param aoHmReqdOutputProps
	 */
	private void createFilterForTask(HashMap<Object, Object> aoHmReqdOutputProps)
	{
		aoHmReqdOutputProps.put(HHSConstants.PROPERTY_PE_ENTITY_ID, HHSConstants.EMPTY_STRING);
		aoHmReqdOutputProps.put(HHSConstants.CURR_LEVEL, HHSConstants.EMPTY_STRING);
		aoHmReqdOutputProps.put(HHSConstants.PROPERTY_PE_TASK_TOTAL_LEVEL, HHSConstants.EMPTY_STRING);
		aoHmReqdOutputProps.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, HHSConstants.EMPTY_STRING);
		aoHmReqdOutputProps.put(HHSConstants.PROPERTY_PE_BUDGET_ID, HHSConstants.EMPTY_STRING);
		aoHmReqdOutputProps.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.EMPTY_STRING);
		aoHmReqdOutputProps.put(HHSConstants.PROPERTY_PE_LAUNCH_ORG_TYPE, HHSConstants.EMPTY_STRING);
		aoHmReqdOutputProps.put(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE, HHSConstants.EMPTY_STRING);
		aoHmReqdOutputProps.put(HHSConstants.PROPERTY_PE_ASSIGNED_TO, HHSConstants.EMPTY_STRING);
		aoHmReqdOutputProps.put(HHSConstants.PROPERTY_PE_ASSIGNED_TO_NAME, HHSConstants.EMPTY_STRING);
		aoHmReqdOutputProps.put(HHSConstants.F_WOB_NUM, HHSConstants.EMPTY_STRING);
		aoHmReqdOutputProps.put(HHSConstants.PROPERTY_PE_TASK_STATUS_LOWER, HHSConstants.EMPTY_STRING);
	}

	/**
	 * The method is Added in Release 7. It is set the audit history for final
	 * approved status to channel when task is last level.
	 * @param aoTaskDetailsBean
	 * @return loAuditList
	 */
	public List<HhsAuditBean> setAuditForAutoApprovedStatus(TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		StringBuilder loDataSb = new StringBuilder();
		StringBuilder loDataSbModification = new StringBuilder();
		List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
		try
		{
			LOG_OBJECT.Info("Entered ContractBudgetModificationService.setAuditForAutoApprovedStatus()");
			if (null != aoTaskDetailsBean)
			{
				loDataSb.append(HHSConstants.TASK_ASSIGNED_TO + HHSConstants.COLON
						+ aoTaskDetailsBean.getAssignedToUserName());
				loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.TASK_ASSIGNMENT, HHSConstants.TASK_ASSIGNMENT,
						loDataSb.toString(), aoTaskDetailsBean.getTaskName(), aoTaskDetailsBean.getEntityId(),
						HHSR5Constants.AUTO_APPROVER_ID, HHSR5Constants.AGENCY_AUDIT));
				if (aoTaskDetailsBean.getLevel().equals(aoTaskDetailsBean.getTotalLevel()))
				{
					String lsEntityType = HHSConstants.AGENCY_PROVIDER_ENTITY_TYPE_MAP.get(aoTaskDetailsBean.getTaskType());
					loDataSbModification.append(HHSConstants.STATUS_CHANGED_FROM).append(HHSConstants.DOUBLE_QUOTE)
							.append(HHSConstants.STATUS_PENDING_APPROVAL).append(HHSConstants.STR)
							.append(HHSConstants._TO_).append(HHSConstants.STR).append(HHSConstants.STR_BUDGET_APPROVED);
					loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE, HHSConstants.STATUS_CHANGE,
							loDataSbModification.toString(), lsEntityType, aoTaskDetailsBean.getEntityId(),
							HHSR5Constants.AUTO_APPROVER_ID, HHSR5Constants.PROVIDER_AUDIT));
				}
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"ContractBudgetModificationService.setAuditForAutoApprovedStatus() method ::: Error Occured While saving audit data for approved status ",
					aoEx);

			LOG_OBJECT.Error("Error:: setAuditForAutoApprovedStatus:" + "setAuditForApprovedStatus method - "
					+ "Error Occured While saving audit data for approved status ", aoEx);
			throw loAppEx;
		}
		return loAuditList;
	}

	/**
	 * The method will merge the budget once the task is approved at all levels.
	 * @param aoMyBatisSession
	 * @param aoUserSession
	 * @param aoTaskDetailBean
	 * @param abFinalFlag
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean mergeBudgets(SqlSession aoMyBatisSession, P8UserSession aoUserSession,
			TaskDetailsBean aoTaskDetailBean) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered ContractBudgetModificationService.mergeBudget()");
		Boolean lbFinalUploadFlag = false;
		HashMap aoRequiredMap = new HashMap();
		HashMap loHashMap = new HashMap();
		ContractBudgetModificationService loCBAS = new ContractBudgetModificationService();
		try
		{
			String lsProcessId = String.valueOf(HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.get(aoTaskDetailBean
					.getTaskType()));
			if (null != lsProcessId && lsProcessId.equalsIgnoreCase(HHSConstants.FIVE))
			{
				lbFinalUploadFlag = mergeModificationBudget(aoMyBatisSession, aoUserSession, aoTaskDetailBean,
						aoRequiredMap, loHashMap, loCBAS);
			}
			else if (null != lsProcessId && lsProcessId.equalsIgnoreCase(HHSConstants.SEVEN))
			{
				lbFinalUploadFlag = mergeUpdateBudget(aoMyBatisSession, aoUserSession, aoTaskDetailBean,
						aoRequiredMap, loHashMap, loCBAS);
			}
		}
		catch (Exception aoAppex)
		{
			setMoState("Error while ContractBudgetModificationService.mergeBudget()");
			ApplicationException loAppex = new ApplicationException(aoAppex.getMessage(), aoAppex);
			LOG_OBJECT.Error("Exception in ContractBudgetModificationService.mergeBudget()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ProcessOperation.mergeBudget()");
		return lbFinalUploadFlag;
	}

	/**
	 * The method will merge the modification budget.
	 * @param aoMyBatisSession
	 * @param aoUserSession
	 * @param aoTaskDetailBean
	 * @param aoRequiredMap
	 * @param aoHashMap
	 * @param aoCBAS
	 * @return
	 * @throws ApplicationException
	 */
	private Boolean mergeModificationBudget(SqlSession aoMyBatisSession, P8UserSession aoUserSession,
			TaskDetailsBean aoTaskDetailBean, HashMap aoRequiredMap, HashMap aoHashMap,
			ContractBudgetModificationService aoCBAS) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered ContractBudgetModificationService.mergeModificationBudget()");
		Boolean lbFinalUploadFlag = false;
		String lsXmlGenerated;
		String lsDocId = null;
		Boolean lbMergeFlag;
		Boolean lbYTDRemainingAmountFlag;
		String lsModificationBudgetId = aoTaskDetailBean.getBudgetId();
		aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsModificationBudgetId);
		// Fetching Parent base budget id of Modification Budget
		try
		{
			String lsParentBudgetId = (String) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.FETCH_PARENT_BUDGET_ID,
					HHSConstants.JAVA_UTIL_MAP);
			// Fetching Agency id of Parent base budget of Modification
			// Budget
			String lsAgencyId = HHSConstants.SYSTEM_USER;
			aoTaskDetailBean.setUserId(HHSConstants.SYSTEM_USER);
			lsXmlGenerated = aoCBAS.generateModificationBudget(aoMyBatisSession, aoTaskDetailBean, aoRequiredMap);
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			// Merging with the base budget
			lbMergeFlag = new ContractBudgetModificationService().mergeBudgetLineItemsForModification(aoMyBatisSession,
					aoTaskDetailBean, lsBudgetStatus);
			//
			lbYTDRemainingAmountFlag = new FinancialsService().updateYtdAndRemainingAmountInLineItems(aoMyBatisSession,
					lsModificationBudgetId, lbMergeFlag, lsAgencyId);
			if (lbYTDRemainingAmountFlag)
			{
				lsDocId = aoCBAS.uploadModificationBudgetData(aoMyBatisSession, aoTaskDetailBean, aoUserSession,
						lsXmlGenerated, aoRequiredMap);
			}
			if (null != lsDocId && !lsDocId.isEmpty())
			{
				lbFinalUploadFlag = true;
			}
		}
		catch (Exception aoAppex)
		{
			setMoState("Error while ContractBudgetModificationService.mergeModificationBudget()");
			ApplicationException loAppex = new ApplicationException(aoAppex.getMessage(), aoAppex);
			LOG_OBJECT.Error("Exception in P8ProcessOperation.mergeModificationBudget()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited ContractBudgetModificationService.mergeModificationBudget()");
		return lbFinalUploadFlag;
	}
	/**
	 * The method will update the update budget.
	 * @param aoMyBatisSession
	 * @param aoUserSession
	 * @param aoTaskDetailBean
	 * @param aoRequiredMap
	 * @param aoHashMap
	 * @param aoCBAS
	 * @return updateYtdAndRemainingAmountInBudgetAndSubBudget
	 * @throws ApplicationException
	 */
	private Boolean mergeUpdateBudget(SqlSession aoMyBatisSession, P8UserSession aoUserSession,
			TaskDetailsBean aoTaskDetailBean, HashMap aoRequiredMap, HashMap aoHashMap,
			ContractBudgetModificationService aoCBAS) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered ContractBudgetModificationService.mergeModificationBudget()");
		boolean updateYtdAndRemainingAmountInBudgetAndSubBudget =false;
		String lsModificationBudgetId = aoTaskDetailBean.getBudgetId();
		aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsModificationBudgetId);
		// Fetching Parent base budget id of Modification Budget
		try
		{
			String lsAgencyId = HHSConstants.SYSTEM_USER;
			aoTaskDetailBean.setUserId(HHSConstants.SYSTEM_USER);
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			// Merging with the base budget
			new ContractBudgetModificationService().mergeBudgetLineItemsForUpdate(aoMyBatisSession,
					aoTaskDetailBean, lsBudgetStatus);
			//
			boolean lbYTDRemainingAmountFlag = new FinancialsService().updateYtdAndRemainingAmountInLineItems(aoMyBatisSession,
					lsModificationBudgetId, true, lsAgencyId); 
			updateYtdAndRemainingAmountInBudgetAndSubBudget = new FinancialsService().updateYtdAndRemainingAmountInBudgetAndSubBudget(aoMyBatisSession,
					lsModificationBudgetId, true, lsAgencyId); 
			
			
		}
		catch (Exception aoAppex)
		{
			setMoState("Error while ContractBudgetModificationService.mergeModificationBudget()");
			ApplicationException loAppex = new ApplicationException(aoAppex.getMessage(), aoAppex);
			LOG_OBJECT.Error("Exception in P8ProcessOperation.mergeModificationBudget()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited ContractBudgetModificationService.mergeModificationBudget()");
		return updateYtdAndRemainingAmountInBudgetAndSubBudget;
	}
	/**
	 * The method is added in Release 7. The method will get the modification
	 * work flows that are in auto approval status.
	 * @param aoMyBatisSession
	 * @param aoUserSession
	 * @param aoTaskDetailBean
	 * @return loStatus as the status of work flows dispatch
	 * @throws ApplicationException
	 */
	public List<TaskDetailsBean> getWFAutoApproval(SqlSession aoMyBatisSession, P8UserSession aoUserSession)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered ContractBudgetModificationService.getWFAutoApproval() method");
		List<TaskDetailsBean> loTaskDetailBeanList = null;
		try
		{
			loTaskDetailBeanList = getWFListAutoApproval(aoMyBatisSession, aoUserSession);
		}
		catch (Exception aoAppex)
		{
			setMoState("Error while ContractBudgetModificationService.getWFAutoApproval()");
			ApplicationException loAppex = new ApplicationException(aoAppex.getMessage(), aoAppex);
			LOG_OBJECT.Error("Exception in ContractBudgetModificationService.getWFAutoApproval()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited ContractBudgetModificationService.getWFAutoApproval() method");
		return loTaskDetailBeanList;
	}
	/**
	 * The method is added in Release 7. The method will get the work flows
	 * whose task owner is one of the auto approver user present in our system.
	 * @param aoMyBatisSession
	 * @param aoUserSession
	 * @return loTaskDetailBeans list of taskbean
	 * @throws ApplicationException
	 */
	private List<TaskDetailsBean> getWFListAutoApproval(SqlSession aoMyBatisSession, P8UserSession aoUserSession)
			throws ApplicationException
	{

		LOG_OBJECT.Info("Entered ContractBudgetModificationService.getWFAutoApproval()");
		VWSession loVWSession = new P8SecurityOperations().getPESession(aoUserSession);
		VWQueue loVWQueue = null;
		VWQueueQuery loVWQueueQuery = null;
		VWStepElement loStepElement = null;
		String lsQueueFilter = null;
		HashMap<Object, Object> aoHmReqdOutputProps = new HashMap();
		Map loHashMap = new HashMap();
		List<TaskDetailsBean> loTaskDetailBeans = new ArrayList<TaskDetailsBean>();
		String lsApproverUserName = null;
		try
		{
			// Get the auto approver list
			lsApproverUserName = getAutoApproverUserNameForAgency(aoMyBatisSession);
			// To create select clause
			createFilterForTask(aoHmReqdOutputProps);
			// create where clause, fetching list of user
			lsQueueFilter = P8Constants.PROPERTY_PE_ASSIGNED_TO + " ";
			StringBuilder lsQueueFilterBuffer = new StringBuilder(lsQueueFilter);
			lsQueueFilterBuffer.append("= '");
			lsQueueFilterBuffer.append(lsApproverUserName);
			lsQueueFilterBuffer.append("'");
			// Start: Added for Defect QC 9151 R 7.3.2
			lsQueueFilterBuffer.append(" AND ");
			lsQueueFilterBuffer.append(P8Constants.PROPERTY_PE_TASK_STATUS);
			lsQueueFilterBuffer.append("<> '");
			lsQueueFilterBuffer.append(HHSConstants.TASK_RFR);
			lsQueueFilterBuffer.append("'");
			// End: Added for Defect QC 9151 R 7.3.2
			lsQueueFilter = lsQueueFilterBuffer.toString();
			loVWQueue = loVWSession.getQueue(P8Constants.HSS_QUEUE_NAME);
			loVWQueueQuery = loVWQueue.createQuery(null, null, null, VWQueue.QUERY_READ_LOCKED, lsQueueFilter, null,
					VWFetchType.FETCH_TYPE_QUEUE_ELEMENT);
			while (loVWQueueQuery.hasNext())
			{
				TaskDetailsBean aoTaskDetailBean = new TaskDetailsBean();
				VWQueueElement loVWQueueElement = (VWQueueElement) loVWQueueQuery.next();
				if (null != loVWQueueElement)
				{
					loStepElement = loVWQueueElement.fetchStepElement(true, true);
					String lsWobNum = loVWQueueElement.getDataField(HHSConstants.F_WOB_NUM).getStringValue();
					HashMap loHmWorkItemProps = new P8ProcessOperations().getVWParamtersValues(loStepElement, aoHmReqdOutputProps);
					aoTaskDetailBean.setTotalLevel(loHmWorkItemProps.get(HHSConstants.PROPERTY_PE_TASK_TOTAL_LEVEL)
							.toString());
					aoTaskDetailBean.setLevel(loHmWorkItemProps.get(HHSConstants.CURR_LEVEL).toString());
					aoTaskDetailBean.setEntityId((String) loHmWorkItemProps.get(HHSConstants.PROPERTY_PE_ENTITY_ID));
					aoTaskDetailBean
							.setContractId((String) loHmWorkItemProps.get(HHSConstants.PROPERTY_PE_CONTRACT_ID));
					aoTaskDetailBean.setBudgetId((String) loHmWorkItemProps.get(HHSConstants.PROPERTY_PE_BUDGET_ID));
					aoTaskDetailBean.setTaskType((String) loHmWorkItemProps.get(HHSConstants.PROPERTY_PE_TASK_TYPE));
					aoTaskDetailBean.setLaunchOrgType((String) loHmWorkItemProps
							.get(HHSConstants.PROPERTY_PE_LAUNCH_ORG_TYPE));
					aoTaskDetailBean.setProcurementTitle((String) loHmWorkItemProps
							.get(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE));
					aoTaskDetailBean
							.setAssignedTo((String) loHmWorkItemProps.get(HHSConstants.PROPERTY_PE_ASSIGNED_TO));
					aoTaskDetailBean.setAssignedToUserName((String) loHmWorkItemProps
							.get(HHSConstants.PROPERTY_PE_ASSIGNED_TO_NAME));
					aoTaskDetailBean.setTaskName((String) loHmWorkItemProps.get(HHSConstants.PROPERTY_PE_TASK_TYPE));
					String lsModificationBudgetId = aoTaskDetailBean.getBudgetId();
					loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsModificationBudgetId);
					aoTaskDetailBean.setUserId(HHSR5Constants.AUTO_APPROVER_ID);
					aoTaskDetailBean.setWorkFlowId(lsWobNum);
					aoTaskDetailBean.setTaskStatus(HHSConstants.STR_BUDGET_APPROVED);
					aoTaskDetailBean.setEntityType((String) loHmWorkItemProps.get(HHSConstants.PROPERTY_PE_TASK_TYPE));
				}
				else
				{
					LOG_OBJECT.Info("No workflows in auto approval state found in VWWork Queue");
				}
				loTaskDetailBeans.add(aoTaskDetailBean);
				//*** Start QC 9585 R 8.9 do not expose password for service account in logs
				String param = CommonUtil.maskPassword(aoTaskDetailBean);
				LOG_OBJECT.Info("TaskDetailBean :::" + param);
				//*** End QC 9585 R 8.9 do not expose password for service account in logs
				
			}
		}
		catch (Exception aoAppex)
		{
			setMoState("Error while ContractBudgetModificationService.getWFAutoApproval()");
			ApplicationException loAppex = new ApplicationException(aoAppex.getMessage(), aoAppex);
			LOG_OBJECT.Error("Exception in ContractBudgetModificationService.getWFAutoApproval()::", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exited ContractBudgetModificationService.getWFAutoApproval() with task auto approval status as::: "
				+ loTaskDetailBeans);
		return loTaskDetailBeans;
	}
	//End Release 7 for auto approval
	//Added for R7 Program income
	/**
	 * This method validates the remaining amount for a particular line-item and
	 * returns FALSE if the modify amount is greater then the remaining amount,
	 * else it validates it as TRUE
	 * <ul>
	 * <li>Query used : getRemainingAmountModificationPI</li>
	 * </ul>
	 * @param aoMybatisSession Session object
	 * @param aoCBProgramIncomeBean CBProgramIncomeBean object
	 * @return loHashMap Hash map returning the value
	 * @throws ApplicationException Application exception handled
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private HashMap validateProgramIncomeModificationData(SqlSession aoMybatisSession, CBProgramIncomeBean aoCBProgramIncomeBean) throws ApplicationException
	{
		Boolean loIsDataValid = true;
		HashMap loHashMap = new HashMap();
		BigDecimal loRemainingAmount = null;
		loHashMap.put(HHSConstants.IS_VALID_DATA, loIsDataValid);
		try
		{
			// validates the modification amount so that it should not fall
			// below YTD Amount
			if (loIsDataValid && aoCBProgramIncomeBean.getId() != null && !aoCBProgramIncomeBean.getId().contains(HHSConstants.NEW_ROW_IDENTIFIER))
			{
				loRemainingAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoCBProgramIncomeBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					    HHSR5Constants.GET_REMAINING_AMOUNT_MOD_PI,
						HHSConstants.PROGRAM_INCOME_BEAN);
			}
			loRemainingAmount = ((loRemainingAmount == null) ? BigDecimal.ZERO : loRemainingAmount);
			if (aoCBProgramIncomeBean.getModificationAmount() != null
					&& !(loRemainingAmount.add(new BigDecimal((aoCBProgramIncomeBean.getModificationAmount()))).compareTo(BigDecimal.ZERO) >= HHSConstants.INT_ZERO)
					&&  new BigDecimal((aoCBProgramIncomeBean.getModificationAmount())).compareTo(BigDecimal.ZERO) != HHSConstants.INT_ZERO)
			{
				loHashMap.put(HHSConstants.IS_VALID_DATA, Boolean.FALSE);
				// Error message thrown for the Proposed budget, we are using
				// the rate validation message as the error message is same
				loHashMap.put(HHSConstants.ERROR_MESSAGE, HHSR5Constants.CBM_LESS_THAN_INVOICE_FOR_PI);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("Program income : ", aoCBProgramIncomeBean);
			LOG_OBJECT.Error("Exception while validating remaining amount", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetModificationService:"
					+ "validateProgramIncomeModificationData method" + " \n");
			throw aoAppEx;
		}
		return loHashMap;
	}
	// End: Changes for R7 pogram income
	/** This method is added in R7 for auto approval modification. It is getting called from 
	 * checkThresholdConsumedInCaseOfThresholdConfigured. This method checks if any amount modification 
	 * is carried out for a sub-budgets. If amount is modified , it returns true.Otherwise false.
	 * @param aoMybatisSession - Sql Session
	 * @param aoMapForContractBean - Modification Sub-budget-id details HashMap
	 * @return - lbAmountModified
	 * @throws ApplicationException
	 */
	private Boolean isSubBudgetAmountModified(SqlSession aoMybatisSession, HashMap aoMapForContractBean)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered ContractBudgetModificationService.isSubBudgetAmountModified() method");
		Boolean lbAmountModified = false;
		ContractBudgetBean loContractBean = (ContractBudgetBean) DAOUtil.masterDAO(aoMybatisSession,
				aoMapForContractBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
				HHSR5Constants.FETCH_SUBBUDGET_MODIFICATIONAMT, HHSConstants.JAVA_UTIL_HASH_MAP);
		BigDecimal ldModificationamt = new BigDecimal(loContractBean.getTotalModification());
		LOG_OBJECT.Debug("Modification Amount:" + ldModificationamt);
		if (ldModificationamt.floatValue() > 0 )
		{
			lbAmountModified = true;
		}
		LOG_OBJECT.Debug("Exited ContractBudgetModificationService.isSubBudgetAmountModified() method");
		return lbAmountModified;
	}
	
	// Start : Added for Defect QC 9151 R 7.3.2
	/**
	 * This method is added for Defect 9151. This method will reset is_auto_approve
	 * flag for modification Budget
	 * 
	 * @param aoMybatisSession
	 * @param aoHMWFRequiredProps
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public Boolean resetBudgetAutoApproveFlag(SqlSession aoMybatisSession, HashMap aoHMWFRequiredProps)
			throws ApplicationException
	{

		LOG_OBJECT.Info("Entered into resetBudgetAutoApproveFlag with input:"+aoHMWFRequiredProps);
		Boolean loupdateStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoHMWFRequiredProps,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSR5Constants.RESET_AUTO_APPROVAL_FLAG, HHSConstants.JAVA_UTIL_MAP);
			loupdateStatus = true;
		}
		catch (ApplicationException loAppExp)
		{
			setMoState("Error occured while resetBudgetAutoApproveFlag");
			throw loAppExp;
		}
		catch (Exception loExp)
		{
			throw new ApplicationException("Exception occured while resetBudgetAutoApproveFlag",
					loExp);
		}
		return loupdateStatus;
	}
	// End: Added for defect QC 9151 R 7.3.2
	
	// Start   QC 8394 R 7.9.0 add Add/Delete action for Unallocated Fund 
	
	/**
	 * <p>
	 * This method delete Unallocated fund line in DB
	 * <ul>
	 * <li>1.check if the sum of approved budget and amendment budget is greater
	 * than zero and also taking in to consideration Negative Amendment amounts
	 * entered for the Unallocated line item in other pending Amendments for
	 * which no budgets have been approved.</li>
	 * <li>2.First Check if the Amendment is positive/negative and update is
	 * according and if not then throw the error</li>
	 * <li>3.Call fetchAmendmentUnallocatedFunds query</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession Sql session object
	 * @param aoUnallocatedFundsBean UnallocatedFunds Bean Object
	 * @return lbUpdateStatus whether amendment was successful
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             ApplicationException
	 */
	public boolean deleteModificationUnallocatedFunds(SqlSession aoMybatisSession, UnallocatedFunds aoUnallocatedFundsBean ) 
			throws ApplicationException
	{
		boolean lbDeleteStatus = false;
		boolean lbError = false;
		Integer lbStatus = HHSConstants.INT_ZERO;
		try
		{  	
			DAOUtil.masterDAO(aoMybatisSession, aoUnallocatedFundsBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.MODIFICATION_DELETE_UNALLOCATED_FUNDS, HHSConstants.UNALLOCATED_FUNDS_BEAN);
			if (lbStatus > HHSConstants.INT_ZERO)
			{
				lbDeleteStatus = true;
				LOG_OBJECT.Debug("Unallocated Funds line has been deleted!");
			}
			else{
				lbError = true;
			}
		
	}
		catch (ApplicationException aoExp)
		{
			if (lbError)
			{
				aoExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoExp.toString());
			}

			setMoState("error occured while deleting Modification unallocated funds for business type id "
					+ aoUnallocatedFundsBean.getBudgetId());
			aoExp.addContextData("Exception occured while deleting Amendment unallocated funds ", aoExp);
			LOG_OBJECT.Error("Transaction Failed:: ContractBudgetModificationService:"
					+ "deleteModificationUnallocatedFunds method - failed to delete " + aoExp.getMessage() + " \n");
			throw aoExp;
		}
		return lbDeleteStatus;
	}

	/**
	 * <p>
	 * This method add Unallocated fund details in DB
	 * <ul>
	 * <li>1.check if the sum of approved budget and amendment budget is greater
	 * than zero and also taking in to consideration Negative Modification amounts
	 * entered for the Unallocated line item in other pending Modification for
	 * which no budgets have been approved.</li>
	 * <li>2.First Check if the Amendment is positive/negative and update is
	 * according and if not then throw the error</li>
	 * <li>3.Call fetchAmendmentUnallocatedFunds query</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession Sql session object
	 * @param aoUnallocatedFundsBean UnallocatedFunds Bean Object
	 * @return lbUpdateStatus whether amendment was successful
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             ApplicationException
	 */
	public boolean addModificationUnallocatedFunds(SqlSession aoMybatisSession, UnallocatedFunds aoUnallocatedFundsBean)
			throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		boolean lbError = false;
		try
		{
			// CHECK IF MODIFICATION AMOUNT IS NOT LESS THAN ALREADY INVOICED
			// AMOUNT
			if (new BigDecimal(aoUnallocatedFundsBean.getModificationAmount()).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO)
			{
				
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.BUDGET_MODIFICATION_RATE_UNIT_VALIDATION));
			}
			
			aoUnallocatedFundsBean.setAmmount(aoUnallocatedFundsBean.getModificationAmount());
			DAOUtil.masterDAO(aoMybatisSession, aoUnallocatedFundsBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.MODIFICATION_ADD_UNALLOCATED_FUNDS, HHSConstants.UNALLOCATED_FUNDS_BEAN);
			lbUpdateStatus = true;
								
			LOG_OBJECT.Debug("Unallocated Funds line has been added during Amendment!");
		
		// ApplicationException is thrown while executing the query.
		// and when the Amendment Type rule is not followed.
		// For positive amendment, amendment is not positive and same for
		// negative one, and if the sum of approved budget and amendment budget
		// is smaller
		// than zero
		} catch (ApplicationException aoExp)
		{
			if (lbError)
			{
				aoExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoExp.toString());
			}
			setMoState("error occured while adding Modification unallocated funds for business type id "
					+ aoUnallocatedFundsBean.getBudgetId());
			aoExp.addContextData("Exception occured while adding Modification unallocated funds ", aoExp);
			LOG_OBJECT.Error("Transaction Failed:: ContractBudgetModificationService:"
					+ "addModificationUnallocatedFunds method - failed to add " + aoExp.getMessage() + " \n");
			throw aoExp;
		}
		return lbUpdateStatus;
		
	}

	// End    QC 8394 R 7.9.0 add Add/Delete action for Unallocated Fund 
	
	
}
