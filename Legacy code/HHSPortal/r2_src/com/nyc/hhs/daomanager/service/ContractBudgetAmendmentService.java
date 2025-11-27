package com.nyc.hhs.daomanager.service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.jdom.Document;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.BudgetDetails;
import com.nyc.hhs.model.CBEquipmentBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CBIndirectRateBean;
import com.nyc.hhs.model.CBMileStoneBean;
import com.nyc.hhs.model.CBOperationSupportBean;
import com.nyc.hhs.model.CBProfessionalServicesBean;
import com.nyc.hhs.model.CBProgramIncomeBean;
import com.nyc.hhs.model.CBServicesBean;
import com.nyc.hhs.model.CBUtilities;
import com.nyc.hhs.model.ContractBean;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.ContractBudgetSummary;
import com.nyc.hhs.model.ContractedServicesBean;
import com.nyc.hhs.model.LineItemMasterBean;
import com.nyc.hhs.model.MasterBean;
import com.nyc.hhs.model.PersonnelServiceBudget;
import com.nyc.hhs.model.PersonnelServicesData;
import com.nyc.hhs.model.RateBean;
import com.nyc.hhs.model.Rent;
import com.nyc.hhs.model.SiteDetailsBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.model.UnallocatedFunds;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8services.P8ContentService;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * <p>
 * This service class will be used to fetch all the data for contract budget
 * Amendment screens. All render and action methods for Budget Summary,
 * Utilities, OTPS, Rent etc screens will use this service to
 * fetch/insert/update data from/to database.
 * </p>
 * 
 */

public class ContractBudgetAmendmentService extends ServiceState
{

	/**
	 * Logger object for ContractBudgetAmendmentService class.
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(ContractBudgetAmendmentService.class);
	private static final P8ContentService MOP8_CONTENT_SERVICE = new P8ContentService();

	/**
	 * <p>
	 * Updated in R7
	 * This method fetches Program Income List corresponding to a Contract Id Id
	 * and Budget Id
	 * <ul>
	 * <li>1. Fetches Program Income List - against SubBudgetId by calling query
	 * 'fetchProgramIncomeAmendment'</li>
	 * as well</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBGridBeanObj - CBGridBean object containing key fields
	 * @param aoMybatisSession - SqlSession object
	 * @return loCBProgramIncomeBean - CBProgramIncomeBean
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public List<CBProgramIncomeBean> fetchProgramIncomeAmendment(CBGridBean aoCBGridBeanObj,
			SqlSession aoMybatisSession, MasterBean aoMasterBean) throws ApplicationException
	{
		 /*[Start] QC_9153 add null exception handling*/  
		List<CBProgramIncomeBean> loCBProgramIncomeBean = new ArrayList<CBProgramIncomeBean>();
		List<CBProgramIncomeBean> loProgramIncomeAmendList = new ArrayList<CBProgramIncomeBean>();
		 /*[End] QC_9153 add null exception handling*/  
		try
		{

			// Check if the Budget is approved, the list must be fetched from
			// XML else from DB
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			/*[Start] QC_9153 add null exception handling*/  
			if (aoMasterBean != null && aoCBGridBeanObj !=null && lsBudgetStatus.equals(aoCBGridBeanObj.getBudgetStatusId()))
			/*[End] QC_9153 add null exception handling*/  
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				//Updated in R7 for PI Filter
				List<CBProgramIncomeBean> loTempProgramBean = new ArrayList<CBProgramIncomeBean>();
				loTempProgramBean = fetchProgramIncomeFromXML(lsSubBudgetId, aoMasterBean);
				loCBProgramIncomeBean = new ArrayList<CBProgramIncomeBean>();
				String lsEntryTypeId = aoCBGridBeanObj.getEntryTypeId();
				if (null != loCBProgramIncomeBean && null != lsEntryTypeId
						&& lsEntryTypeId != HHSR5Constants.ENTRY_TYPE_PROGRAM_INCOME)
				{
					Iterator<CBProgramIncomeBean> loPIBeanIterator = loTempProgramBean.iterator();
					while (loPIBeanIterator.hasNext())
					{
						CBProgramIncomeBean loFilterCBBean = loPIBeanIterator.next();
						if ((lsEntryTypeId.equalsIgnoreCase(loFilterCBBean.getEntryTypeId())))
						{
							loCBProgramIncomeBean.add(loFilterCBBean);
						}
					}
				}
				else
				{
					loCBProgramIncomeBean.addAll(loTempProgramBean);
				}
				//End R7 for PI Filter for Line items
			}
			else
			{
				// Fetch the list of modified rows and the rest static line
				// items from Base
				//Removing this and adding entry type id = 11 in query
				//aoCBGridBeanObj.setEntryTypeId(HHSConstants.STRING_ELEVEN);
				 /*[Start] QC_9153 add null exception handling*/ 
				Object loCBProgramIncomeBeanObj =null;
				if(aoCBGridBeanObj !=null){
					loCBProgramIncomeBeanObj = DAOUtil.masterDAO(aoMybatisSession,
						aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.FETCH_PROGRAM_INCOME_AMENDMENT, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
					
					
				} if(loCBProgramIncomeBeanObj!=null){
					 loCBProgramIncomeBean = (List<CBProgramIncomeBean>) loCBProgramIncomeBeanObj;
				 }
				
				// R7 For fetching rows added during amendment
				if(aoCBGridBeanObj !=null && !(aoCBGridBeanObj.getEntryTypeId()== null))
				{
				Object loProgramIncomeAmendListObj = DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSR5Constants.FETCH_PI_FOR_MODIFICATION,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				if(loProgramIncomeAmendListObj!=null){
					loProgramIncomeAmendList = (List<CBProgramIncomeBean>)loProgramIncomeAmendListObj;
				}
				 /*[End] QC_9153 add null exception handling*/  
				setAmendmentPIDetailsBean(loCBProgramIncomeBean,loProgramIncomeAmendList);
				//R7 changes end
				}
			}
		}
		catch (ApplicationException aoAppExp)
		{
			// Handle the ApplicationException type Exception and set moState
			// and context data
			setMoState("error occured while fetching Program Income Details for budget type id "
					+ aoCBGridBeanObj.getBudgetTypeId());
			aoAppExp.addContextData("Exception occured while fetching Program Income Details ", aoAppExp);
			LOG_OBJECT.Error("error occured while fetching Program Income Details " + aoAppExp);
			throw aoAppExp;
		}

		return loCBProgramIncomeBean;
	}

	/**
	 * The method is updated to Release 7. Accessibility is changed to public to
	 * re-use method for approved modifications. 
	 * This method fetch approved
	 * amendment Program Income details list from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List<CBProgramIncomeBean>
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public List<CBProgramIncomeBean> fetchProgramIncomeFromXML(String asSubBudgetId, MasterBean aoMasterBean)
			throws ApplicationException
	{
		List<CBProgramIncomeBean> loReturnedList = null;

		List<LineItemMasterBean> loMasterBeanList = null;

		loMasterBeanList = aoMasterBean.getMasterBeanList();
		 /*[Start] QC_9153 add null exception handling*/  
		if(loMasterBeanList!= null){		
			Iterator<LineItemMasterBean> aoListIterator = loMasterBeanList.iterator();
			while (aoListIterator.hasNext())
			{
				LineItemMasterBean loLineItemBean = aoListIterator.next();
				if (loLineItemBean.getSubbudgetId().equals(asSubBudgetId))
				{				
					loReturnedList = loLineItemBean.getProgramincomeBeanList();
				}
			}
		}		
	    if( loReturnedList == null || loReturnedList.isEmpty() ) 
	    	loReturnedList= new ArrayList<CBProgramIncomeBean>();
	    /*[End] QC_9153 add null exception handling*/  
		return loReturnedList;
	}

	/**
	 * <p>
	 * This method concatenates ProgramIncomeTypeId and cbmId with
	 * ProgramIncomeId and UNDERSCORE Character and set in ID
	 * 
	 * @param aoProgramIncomDetailsList List of CBProgramIncomeBean bean.
	 * @return void
	 */
	private void updateProgramIncomeBeanIds(List<CBProgramIncomeBean> aoProgramIncomDetailsList)
	{
		StringBuffer loConcat = null;
		// Iterate the List for setting the Ids for all Program Income items
		for (CBProgramIncomeBean loIterateBean : aoProgramIncomDetailsList)
		{
			loConcat = new StringBuffer(loIterateBean.getProgramIncomeId());
			loConcat.append(HHSConstants.UNDERSCORE).append(loIterateBean.getParentId());
			loConcat.append(HHSConstants.UNDERSCORE).append(loIterateBean.getProgramIncomeTypeId());
			loConcat.append(HHSConstants.UNDERSCORE).append(loIterateBean.getApprovedFYBudget());
			loConcat.append(HHSConstants.UNDERSCORE).append(loIterateBean.getRemainingAmount());
			loIterateBean.setId(loConcat.toString());
		}
	}

	/**
	 * <p>
	 * Updated in R7
	 * This method updates Program Income Details corresponding to a
	 * ProgramIncomeId ( primary Key attribute of CBProgramsIncomeBean bean)
	 * <ul>
	 * <li>Updates the whole row of a Program Income</li>
	 * <li>Also updates the Program Income under Budget Amendment by calling
	 * query 'insertProgramIncomeAmendment'</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession - SqlSessionId
	 * @param aoCBProgramIncomeBean - CBProgramsIncomeBean object
	 * @return loUpdateStatus a Boolean object
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public Boolean updateProgramIncomeAmendment(SqlSession aoMyBatisSession, CBProgramIncomeBean aoCBProgramIncomeBean)
			throws ApplicationException
	{
		boolean lbEditStatus = false;
		boolean lbError = false;
		Map<String, Object> loReturnMap = null;
		String lsSubBudgetId = aoCBProgramIncomeBean.getSubBudgetId();
		try
		{
			loReturnMap = validateAmendmentAmountForProgramIncome(aoMyBatisSession, aoCBProgramIncomeBean);

			if (!(Boolean) loReturnMap.get(HHSR5Constants.LB_ERROR))
			{
				//Added in R7 for setting value of description in base in amendment row 
				aoCBProgramIncomeBean = updateProgramIncomeDescription(aoMyBatisSession, aoCBProgramIncomeBean);
				//R7 changes end
				Integer loUpdatedRows = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBProgramIncomeBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.UPDATE_PROGRAM_INCOME_AMENDMENT, HHSConstants.PROGRAM_INCOME_BEAN);

				if (null != loUpdatedRows && loUpdatedRows == HHSConstants.INT_ZERO)
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoCBProgramIncomeBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
							HHSConstants.INSERT_PROGRAM_INCOME_AMENDMENT, HHSConstants.PROGRAM_INCOME_BEAN);
					
					String lsIsOldPI = (String) DAOUtil.masterDAO(aoMyBatisSession, lsSubBudgetId,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSR5Constants.FETCH_IS_OLD_PI_FOR_VALIDATION,
							HHSConstants.JAVA_LANG_STRING);
					
					if (!HHSConstants.ZERO.equals(lsIsOldPI))
					{
					insertIfOtherLineItemCaseProgIncome(aoMyBatisSession, aoCBProgramIncomeBean);
					}
				}
			}
			else
			{
				lbError = true;
				throw new ApplicationException((String) loReturnMap.get(HHSR5Constants.LS_ERROR_CONSTANT));
			}
			lbEditStatus = true;
			setMoState("ContractBudgetModificationService: updateProgramIncomeAmendment() passed");
		}
		// Exception occur if database is down or getting null in required
		// parameter
		catch (ApplicationException aoAppEx)
		{
			if (lbError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
				LOG_OBJECT.Error("Entered value would cause the Proposed Budget to fall below the "
						+ "amount already invoiced for the line item. Please enter a new value " + aoAppEx);
				setMoState("Entered value would cause the Proposed Budget to fall below the"
						+ "amount already invoiced for the line item. Please enter a new value");
			}
			else
			{
				aoAppEx.addContextData(HHSConstants.CB_PROGRAM_INCOME_BEAN,
						CommonUtil.convertBeanToString(aoCBProgramIncomeBean));
				LOG_OBJECT.Error("App Exception occured in ContractBudgetAmendmentService:"
						+ " updateProgramIncomeAmendment method:: ", aoAppEx);
				setMoState("Transaction Failed::App Exception in ContractBudgetAmendmentService: "
						+ "updateProgramIncomeAmendment() method aoCBProgramIncomeBean::" + aoCBProgramIncomeBean
						+ "\n");
			}
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetAmendmentService: updateProgramIncomeAmendment method:: ", aoEx);
			loAppEx.addContextData(HHSConstants.CB_PROGRAM_INCOME_BEAN,
					CommonUtil.convertBeanToString(aoCBProgramIncomeBean));
			LOG_OBJECT.Error(
					"Exception occured in ContractBudgetAmendmentService: updateProgramIncomeAmendment method:: ",
					loAppEx);
			setMoState("Transaction Failed::Exception in ContractBudgetAmendmentService: "
					+ "updateProgramIncomeAmendment() method aoCBProgramIncomeBean::" + aoCBProgramIncomeBean + "\n");
			throw loAppEx;
		}
		return lbEditStatus;
	}

	/**
	 * This method validates the modification amount in OTPS for a particular
	 * line-item and returns FALSE if the #amount user is trying to update tries
	 * to make the total amount for a line-item to fall below zero and also
	 * taking in to consideration Negative Amendment amounts entered for the
	 * Program income line item in other pending Amendments for which no budgets
	 * have been approved, else it validates it as TRUE
	 * <ul>
	 * <li>Calls query 'fetchProgIncomeDetailsForValidationInMultipleAmendments'
	 * </li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession object
	 * @param aoCBOperationSupportBean CBOperationSupportBean object
	 * @return boolean lbError valid/invalid
	 * @throws Exception ApplicationException object
	 * 
	 * 
	 */
	private Map<String, Object> validateAmendmentAmountForProgramIncome(SqlSession aoMybatisSession,
			CBProgramIncomeBean aoCBProgramIncomeBean) throws Exception
	{
		Map<String, Object> loReturnMap = new HashMap<String, Object>();
		loReturnMap.put(HHSR5Constants.LB_ERROR, false);
		loReturnMap.put(HHSR5Constants.LS_ERROR_CONSTANT, HHSConstants.EMPTY_STRING);

		// Validation for positive amendment (For Positive Amendment only.)
		if (aoCBProgramIncomeBean.getAmendmentType().equalsIgnoreCase(HHSConstants.POSITIVE)
				&& (new BigDecimal(aoCBProgramIncomeBean.getAmendmentAmount()).compareTo(BigDecimal.ZERO)) < HHSConstants.INT_ZERO)
		{
			loReturnMap.put(HHSR5Constants.LB_ERROR, true);
			loReturnMap.put(HHSR5Constants.LS_ERROR_CONSTANT, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.POSITIVE_AMENDMENT_MSG));

		}
		// Validation for negative amendment (For Negative Amendment only)
		else if (aoCBProgramIncomeBean.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE)
				&& (new BigDecimal(aoCBProgramIncomeBean.getAmendmentAmount()).compareTo(BigDecimal.ZERO)) > HHSConstants.INT_ZERO)
		{
			loReturnMap.put(HHSR5Constants.LB_ERROR, true);
			loReturnMap.put(HHSR5Constants.LS_ERROR_CONSTANT, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.NEGATIVE_AMENDMENT_MSG));
		}

		else if (aoCBProgramIncomeBean.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE))
		{
			BigDecimal loCBProgramIncomeRemAmt = null;
			loCBProgramIncomeRemAmt = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoCBProgramIncomeBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
					HHSConstants.CBM_FETCH_PROG_INCOME_DETAILS_FOR_VALIDATION_IN_MULTIPLE_AMENDMENTS,
					HHSConstants.PROGRAM_INCOME_BEAN);

			// check sum of modified amount and remaining amount should be
			// greater than zero
			if (((loCBProgramIncomeRemAmt).add(new BigDecimal(aoCBProgramIncomeBean.getAmendmentAmount())))
					.compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO)
			{
				loReturnMap.put(HHSR5Constants.LB_ERROR, true);
				loReturnMap.put(HHSR5Constants.LS_ERROR_CONSTANT, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CBM_LESS_THAN_INVOICE_INCLUDING_PENDING_NEG_AMEND_ERROR_MESSAGE));
			}
		}
		return loReturnMap;
	}

	/**
	 * This is private method used to insert entry in other_details table when
	 * in contract amendment budget, provider enters an amount in amendment
	 * column and that field in other's field of program income by calling query
	 * 'insertAmendRecInOtherDetailsProgInc'
	 * <ul>
	 * <li>Calls query 'insertAmendRecInOtherDetailsProgInc'</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoMyBatisSession SqlSession object
	 * @param aoCBProgramIncomeBean CBProgramIncomeBean object
	 * @throws Exception ApplicationException object
	 * 
	 * 
	 */
	private void insertIfOtherLineItemCaseProgIncome(SqlSession aoMyBatisSession,
			CBProgramIncomeBean aoCBProgramIncomeBean) throws Exception
	{
		Integer loCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBProgramIncomeBean,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
				HHSConstants.FETCH_OTHER_ENTRY_IF_EXISTS_PROG_INC, HHSConstants.PROGRAM_INCOME_BEAN);

		if (null != loCount && loCount > 0)
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoCBProgramIncomeBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
					HHSConstants.INSERT_AMEND_REC_IN_OTHER_DETAILS_PROG_INC, HHSConstants.PROGRAM_INCOME_BEAN);
		}
	}

	/**
	 * <ul>
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
	 * <li>If it would, display error message: ?!</li>
	 * <li>Entered value would cause the Proposed Budget to fall below the
	 * amount already invoiced for the line item. Please enter a new value.?</li>
	 * </ul>
	 * </ol>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoCBGridBeanObj CBGridBean attributes set.
	 * @return loNewCBContractedServicesBean
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<ContractedServicesBean> fetchContractedServicesAmendmentConsultants(SqlSession aoMyBatisSession,
			CBGridBean aoCBGridBeanObj, MasterBean aoMasterBean) throws ApplicationException
	{
		List<ContractedServicesBean> loCBContractedServicesBean = null;
		List<ContractedServicesBean> loNewCBContractedServicesBean = null;
		String lsParentSUbBudgetId = aoCBGridBeanObj.getParentSubBudgetId();
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);

		try
		{
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				loNewCBContractedServicesBean = new ArrayList<ContractedServicesBean>();
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBContractedServicesBean = fetchContractedServicesConsultantsFromXML(lsSubBudgetId, aoMasterBean);
				loNewCBContractedServicesBean.addAll(loCBContractedServicesBean);
			}
			else
			{
				aoCBGridBeanObj.setSubHeader(HHSConstants.ONE);
				loCBContractedServicesBean = (List<ContractedServicesBean>) DAOUtil.masterDAO(aoMyBatisSession,
						aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.CBY_FETCH_CONTRACTED_SERVICES_AMENDMENT_CONSULTANTS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				loNewCBContractedServicesBean = (List<ContractedServicesBean>) DAOUtil.masterDAO(aoMyBatisSession,
						aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.CBY_FETCH_CONTRACTED_SERVICES_NEW_AMENDMENT_CONSULTANTS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				appendNewRecord(loNewCBContractedServicesBean, lsParentSUbBudgetId);
				loNewCBContractedServicesBean.addAll(loCBContractedServicesBean);
			}

		}
		// Application Exception Handled Here.
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACTID, aoCBGridBeanObj.getContractID());
			LOG_OBJECT.Error("Exception occured in ContractBudgetAmendmentService:"
					+ " fetchContractedServicesAmendmentConsultants method:: ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService: "
					+ "fetchContractedServicesAmendmentConsultants method - failed Exception occured while fetching\n");
			throw loAppEx;
		}

		return loNewCBContractedServicesBean;
	}

	/**
	 * The method is updated to Release 7. Accessibility is changed to public to
	 * re-use method for approved modifications. 
	 * This method fetch ContractedServicesBean list from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List<ContractedServicesBean>
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public List<ContractedServicesBean> fetchContractedServicesConsultantsFromXML(String asSubBudgetId,
			MasterBean aoMasterBean) throws ApplicationException
	{
		List<ContractedServicesBean> loReturnedList = null;
		loReturnedList = generateContractedServicesList(asSubBudgetId, aoMasterBean);
		loReturnedList = fetchListForContractedServices(loReturnedList);
		return loReturnedList;
	}

	/**
	 * The method is updated to Release 7. Accessibility is changed to public to
	 * re-use method for approved modifications.
	 * This method fetch ContractedServicesBean list from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List<ContractedServicesBean>
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public List<ContractedServicesBean> fetchContractedServicesSubContractorsFromXML(String asSubBudgetId,
			MasterBean aoMasterBean) throws ApplicationException
	{
		List<ContractedServicesBean> loReturnedList = null;
		loReturnedList = generateContractedServicesList(asSubBudgetId, aoMasterBean);
		loReturnedList = fetchListForContractedServicesSubContractors(loReturnedList);
		return loReturnedList;
	}

	/**
	 * This method fetch ContractedServicesBean list from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List<ContractedServicesBean>
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private List<ContractedServicesBean> fetchContractedServicesVendorsFromXML(String asSubBudgetId,
			MasterBean aoMasterBean) throws ApplicationException
	{
		List<ContractedServicesBean> loReturnedList = null;
		loReturnedList = generateContractedServicesList(asSubBudgetId, aoMasterBean);
		loReturnedList = fetchListForContractedServicesVendors(loReturnedList);
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
	    /*[Start] R7.3.2 add null exception handling*/  
	    if( aoContractedServicesBeanList == null || aoContractedServicesBeanList.isEmpty() ) 
	        return new ArrayList<ContractedServicesBean>();
        /*[End] R7.3.2 add null exception handling*/

		List<ContractedServicesBean> loContractedServicesBeanList = null;
		if (aoContractedServicesBeanList != null)
		{
			Iterator<ContractedServicesBean> aoListIterator = aoContractedServicesBeanList.iterator();
			loContractedServicesBeanList = new ArrayList<ContractedServicesBean>();
			while (aoListIterator.hasNext())
			{
				ContractedServicesBean loContractedServicesBeanBean = aoListIterator.next();
				if (loContractedServicesBeanBean.getSubHeader().equals(HHSConstants.ONE))
				{
					loContractedServicesBeanList.add(loContractedServicesBeanBean);
				}
			}
		}
		return loContractedServicesBeanList;

	}

	/**
	 * This method fetch records of only type ContractedServicesBean from the
	 * consolidated list
	 * 
	 * @param aoContractedServicesBeanList List<ContractedServicesBean>
	 * @return loContractedServicesBeanList List<ContractedServicesBean>
	 */
	private List<ContractedServicesBean> fetchListForContractedServicesVendors(
			List<ContractedServicesBean> aoContractedServicesBeanList) throws ApplicationException
	{
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

	/**
	 * This method fetch records of only type ContractedServicesBean from the
	 * consolidated list
	 * 
	 * @param aoContractedServicesBeanList List<ContractedServicesBean>
	 * @return loContractedServicesBeanList List<ContractedServicesBean>
	 */
	private List<ContractedServicesBean> fetchListForContractedServicesSubContractors(
			List<ContractedServicesBean> aoContractedServicesBeanList) throws ApplicationException
	{
		List<ContractedServicesBean> loContractedServicesBeanList = null;
		if (aoContractedServicesBeanList != null)
		{
			Iterator<ContractedServicesBean> aoListIterator = aoContractedServicesBeanList.iterator();
			loContractedServicesBeanList = new ArrayList<ContractedServicesBean>();
			while (aoListIterator.hasNext())
			{
				ContractedServicesBean loContractedServicesBeanBean = aoListIterator.next();
				if (loContractedServicesBeanBean.getSubHeader().equals(HHSConstants.TWO))
				{
					loContractedServicesBeanList.add(loContractedServicesBeanBean);
				}

			}
		}
		return loContractedServicesBeanList;

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
	 * This method is used to fetch data for sub-contractors for contracted
	 * services
	 * <ul>
	 * <li>Calls query 'fetchContractedServicesAmendmentSubContractors'</li>
	 * <li>Calls query 'fetchContractedServicesNewAmendmentConsultants'</li>
	 * </ul>
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoCBGridBeanObj CBGridBean attributes set.
	 * @return loNewCBContractedServicesBean
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<ContractedServicesBean> fetchContractedServicesAmendmentSubContractors(SqlSession aoMyBatisSession,
			CBGridBean aoCBGridBeanObj, MasterBean aoMasterBean) throws ApplicationException
	{
		List<ContractedServicesBean> loCBContractedServicesBean = null;
		List<ContractedServicesBean> loNewCBContractedServicesBean = null;
		String lsParentSUbBudgetId = aoCBGridBeanObj.getParentSubBudgetId();
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);

		try
		{
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				loNewCBContractedServicesBean = new ArrayList<ContractedServicesBean>();
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBContractedServicesBean = fetchContractedServicesSubContractorsFromXML(lsSubBudgetId, aoMasterBean);
				loNewCBContractedServicesBean.addAll(loCBContractedServicesBean);
			}
			else
			{
				aoCBGridBeanObj.setSubHeader(HHSConstants.TWO);
				loCBContractedServicesBean = (List<ContractedServicesBean>) DAOUtil.masterDAO(aoMyBatisSession,
						aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.CBY_FETCH_CONTRACTED_SERVICES_AMENDMENT_SUB_CONTRACTORS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				loNewCBContractedServicesBean = (List<ContractedServicesBean>) DAOUtil.masterDAO(aoMyBatisSession,
						aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.CBY_FETCH_CONTRACTED_SERVICES_NEW_AMENDMENT_CONSULTANTS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				appendNewRecord(loNewCBContractedServicesBean, lsParentSUbBudgetId);
				loNewCBContractedServicesBean.addAll(loCBContractedServicesBean);
			}

		}
		// Application Exception Handled Here.
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACTID, aoCBGridBeanObj.getContractID());
			LOG_OBJECT.Error("Exception occured in ContractBudgetAmendmentService: "
					+ "fetchContractedServicesAmendmentSubContractors method:: ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService: "
					+ "fetchContractedServicesAmendmentSubContractors method - failed "
					+ "Exception occured while fetching\n");
			throw loAppEx;
		}

		return loNewCBContractedServicesBean;
	}

	/**
	 * This method is used to fetch data for vendors for contracted services
	 * <ul>
	 * <li>Calls query 'fetchContractedServicesAmendmentVendors'</li>
	 * <li>Calls query 'fetchContractedServicesNewAmendmentConsultants'</li>
	 * </ul>
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoCBGridBeanObj CBGridBean attribute set.
	 * @return loNewCBContractedServicesBean
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<ContractedServicesBean> fetchContractedServicesAmendmentVendors(SqlSession aoMyBatisSession,
			CBGridBean aoCBGridBeanObj, MasterBean aoMasterBean) throws ApplicationException
	{
		List<ContractedServicesBean> loCBContractedServicesBean = null;
		List<ContractedServicesBean> loNewCBContractedServicesBean = null;
		String lsParentSUbBudgetId = aoCBGridBeanObj.getParentSubBudgetId();
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);

		try
		{
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				loNewCBContractedServicesBean = new ArrayList<ContractedServicesBean>();
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBContractedServicesBean = fetchContractedServicesVendorsFromXML(lsSubBudgetId, aoMasterBean);
				loNewCBContractedServicesBean.addAll(loCBContractedServicesBean);
			}
			else
			{
				aoCBGridBeanObj.setSubHeader(HHSConstants.THREE);
				loCBContractedServicesBean = (List<ContractedServicesBean>) DAOUtil.masterDAO(aoMyBatisSession,
						aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.CBY_FETCH_CONTRACTED_SERVICES_AMENDMENT_VENDORS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				loNewCBContractedServicesBean = (List<ContractedServicesBean>) DAOUtil.masterDAO(aoMyBatisSession,
						aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.CBY_FETCH_CONTRACTED_SERVICES_NEW_AMENDMENT_CONSULTANTS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				appendNewRecord(loNewCBContractedServicesBean, lsParentSUbBudgetId);
				loNewCBContractedServicesBean.addAll(loCBContractedServicesBean);
			}

		}
		// Application Exception Handled Here.
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACTID, aoCBGridBeanObj.getContractID());
			LOG_OBJECT.Error("Exception occured in ContractBudgetAmendmentService: "
					+ "fetchContractedServicesAmendmentVendors method:: ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService: "
					+ "fetchContractedServicesAmendmentVendors method - failed Exception occured while fetching\n");
			throw loAppEx;
		}

		return loNewCBContractedServicesBean;
	}

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
	 * </ul>
	 * <ul>
	 * <li>Calls query 'addContractedServicesAmendment'</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoCBGridBeanObj ContractedServicesBean bean attributes set.
	 * @return loVal Wrapper boolean returned.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public Integer addContractedServicesAmendment(SqlSession aoMyBatisSession, ContractedServicesBean aoCBGridBeanObj)
			throws ApplicationException
	{
		Integer loVal = null;
		boolean lbError = false;
		HashMap loHashMap = null;
		try
		{
			if (aoCBGridBeanObj.getAmendmentType().equalsIgnoreCase(HHSConstants.POSITIVE)
					&& (new BigDecimal(aoCBGridBeanObj.getAmendmentAmt()).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO))
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.POSITIVE_AMENDMENT_MSG));
			}
			// Validation for negative amendment (For Negative Amendment only)
			else if (aoCBGridBeanObj.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE)
					&& (new BigDecimal(aoCBGridBeanObj.getAmendmentAmt()).compareTo(BigDecimal.ZERO) > HHSConstants.INT_ZERO))
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.NEGATIVE_AMENDMENT_MSG));
			}
			if (aoCBGridBeanObj != null)
			{
				loHashMap = validateContractedServicesAmendmentData(aoMyBatisSession, aoCBGridBeanObj);
				if ((Boolean) loHashMap.get(HHSConstants.IS_VALID_DATA))
				{
					loVal = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
							HHSConstants.CBY_ADD_CONTRACTED_SERVICES_AMENDMENT,
							HHSConstants.CBY_COM_NYC_HHS_MODEL_CONTRACTED_SERVICES_BEAN);
				}
				else
				{
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							(String) loHashMap.get(HHSConstants.ERROR_MESSAGE)));
				}

			}
		}
		// Application Exception Handled Here.
		catch (ApplicationException loAppEx)
		{
			if (lbError)
			{
				loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loAppEx.toString());
			}
			else
			{
				loAppEx.addContextData(HHSConstants.CONTRACTID, aoCBGridBeanObj.getContractID());
				LOG_OBJECT
						.Error("Exception occured in ContractBudgetAmendmentService: addContractedServicesAmendment method:: ",
								loAppEx);
				setMoState("Transaction Failed:: ContractBudgetAmendmentService:"
						+ " addContractedServicesAmendment method - failed Exception occured while inserting data\n");
			}
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
	 * </ol>
	 * <ul>
	 * <li>Calls query 'fetchInsertContractedServicesAmendment'</li>
	 * </ul>
	 * <ul>
	 * <li>Calls query 'updateContractedServicesAmendment'</li>
	 * </ul>
	 * <ul>
	 * <li>Calls query 'editContractedServicesAmendment'</li>
	 * </ul>
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoCBGridBeanObj ContractedServicesBean bean attributes set.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return boolean
	 */
	public boolean editContractedServicesAmendment(SqlSession aoMyBatisSession, ContractedServicesBean aoCBGridBeanObj)
			throws ApplicationException
	{
		Integer loVal = null;
		@SuppressWarnings("rawtypes")
		HashMap loHashMap = null;
		Boolean loStatus = false;
		ContractedServicesBean loCsBean = null;
		Boolean loError = false;
		try
		{

			if (aoCBGridBeanObj.getAmendmentType().equalsIgnoreCase(HHSConstants.POSITIVE)
					&& (new BigDecimal(aoCBGridBeanObj.getAmendmentAmt()).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO))
			{
				loError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.POSITIVE_AMENDMENT_MSG));
			}
			else if (aoCBGridBeanObj.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE)
					&& (new BigDecimal(aoCBGridBeanObj.getAmendmentAmt()).compareTo(BigDecimal.ZERO) > HHSConstants.INT_ZERO))
			{
				loError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.NEGATIVE_AMENDMENT_MSG));
			}
			if (aoCBGridBeanObj != null)
			{
				loHashMap = validateContractedServicesAmendmentData(aoMyBatisSession, aoCBGridBeanObj);
				if ((Boolean) loHashMap.get(HHSConstants.IS_VALID_DATA))
				{
					loCsBean = (ContractedServicesBean) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
							HHSConstants.CBY_FETCH_INSERT_CONTRACTED_SERVICES_AMENDMENT,
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
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
							HHSConstants.CBY_UPDATE_CONTRACTED_SERVICES_AMENDMENT,
							HHSConstants.CBY_COM_NYC_HHS_MODEL_CONTRACTED_SERVICES_BEAN);
					if (loVal < 1)
					{
						DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
								HHSConstants.CBY_EDIT_CONTRACTED_SERVICES_AMENDMENT,
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
				loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loAppEx.toString());
			}
			else
			{
				loAppEx.addContextData(HHSConstants.CONTRACTID, aoCBGridBeanObj.getContractID());
				LOG_OBJECT.Error("Exception occured in ContractBudgetAmendmentService:"
						+ " editContractedServicesAmendment method:: ", loAppEx);
				setMoState("Transaction Failed:: ContractBudgetAmendmentService: "
						+ "editContractedServicesAmendment method - failed Exception occured while updating data\n");
			}
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
	 * </ol>
	 * <ul>
	 * <li>Calls query 'delContractedServicesAmendment'</li>
	 * </ul>
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoCBGridBeanObj ContractedServicesBean bean attributes set.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return boolean loVal
	 */
	public boolean deleteContractedServicesAmendment(SqlSession aoMyBatisSession, ContractedServicesBean aoCBGridBeanObj)
			throws ApplicationException
	{
		Boolean loVal = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
					HHSConstants.CBY_DEL_CONTRACTED_SERVICES_AMENDMENT,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CONTRACTED_SERVICES_BEAN);
			loVal = true;
		}
		// Application Exception Handled Here.
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACTID, aoCBGridBeanObj.getContractID());
			LOG_OBJECT.Error(
					"Exception occured in ContractBudgetAmendmentService: deleteContractedServicesAmendment method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService: deleteContractedServicesAmendment"
					+ " method - failed Exception occured while deleting data\n");
			throw loAppEx;
		}
		return loVal;
	}

	/**
	 * <li>This method is used to append _newRecord for all the rows that are
	 * added at the time of modification.</li>
	 * 
	 * @param aoCBContractedServicesBean ContractedServicesBean attributes set.
	 * @param asParentSubBudgetId Parameter on the basis of which underscore
	 *            appended for delete functionality.
	 */
	private void appendNewRecord(List<ContractedServicesBean> aoCBContractedServicesBean, String asParentSubBudgetId)
	{
		StringBuffer loConcat = null;
		if (null != aoCBContractedServicesBean && aoCBContractedServicesBean.size() > HHSConstants.INT_ZERO)
		{
			for (ContractedServicesBean loCsBean : aoCBContractedServicesBean)
			{
				loConcat = new StringBuffer();
				if (!(loCsBean.getSubBudgetID().equals(asParentSubBudgetId)))
				{
					loConcat.append(loCsBean.getId());
					loConcat.append(HHSConstants.NEW_RECORD_CONTRACT_SERVICES);
					loCsBean.setId(loConcat.toString());
				}
			}
		}
	}

	/**
	 * This method validates the remaining amount for a particular line-item and
	 * returns FALSE if the modify amount is greater then the [remaining amount
	 * ? Negative Amendment amounts entered for the Contracted Services line
	 * item in other pending Amendments for which no budgets have been
	 * approved.] else it validates it as TRUE
	 * <ul>
	 * <li>Calls query
	 * 'getRemainingAmountContractedServicesInMultipleAmendments'</li>
	 * </ul>
	 * @param aoMybatisSession Session object
	 * @return HashMap
	 * @param aoContractedServicesObj ContractedServicesBean attribute set.
	 * @throws ApplicationException Application exception handled
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private HashMap validateContractedServicesAmendmentData(SqlSession aoMybatisSession,
			ContractedServicesBean aoContractedServicesObj) throws ApplicationException
	{
		Boolean loIsDataValid = true;
		HashMap loHashMap = new HashMap();
		BigDecimal loRemainingAmount = null;
		loHashMap.put(HHSConstants.IS_VALID_DATA, loIsDataValid);
		try
		{
			// validates the modification amount so that it should not fall
			// below YTD Amount
			if (loIsDataValid && aoContractedServicesObj.getId() != null
					&& !aoContractedServicesObj.getId().contains(HHSConstants.NEW_ROW_IDENTIFIER))
			{
				loRemainingAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoContractedServicesObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.QRY_GET_REMAINING_AMOUNT_AMENDMENT_CONTRACTED_SERVICES_IN_MULTIPLE_AMENDMENTS,
						HHSConstants.CBY_COM_NYC_HHS_MODEL_CONTRACTED_SERVICES_BEAN);
			}
			loRemainingAmount = ((loRemainingAmount == null) ? new BigDecimal(HHSConstants.INT_ZERO)
					: loRemainingAmount);

			if (aoContractedServicesObj.getAmendmentAmt() != null
					&& !((loRemainingAmount.add(new BigDecimal(aoContractedServicesObj.getAmendmentAmt())))
							.compareTo(BigDecimal.ZERO) >= HHSConstants.INT_ZERO))
			{
				loHashMap.put(HHSConstants.IS_VALID_DATA, Boolean.FALSE);
				// Error message thrown for the Proposed budget, we are using
				// the rate validation message as the error message is same
				loHashMap.put(HHSConstants.ERROR_MESSAGE,
						HHSConstants.CBM_LESS_THAN_INVOICE_INCLUDING_PENDING_NEG_AMEND_ERROR_MESSAGE);
			}
		}
		/**
		 * Application Exception handled here
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CBM_NEW_CONTRACTED_SERVICES, aoContractedServicesObj);
			LOG_OBJECT.Error("Exception while validating remaining amount", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService:"
					+ "validateContractedServicesAmendmentData method" + " \n");
			throw loAppEx;
		}
		return loHashMap;
	}

	/**
	 * <li>This method is used to fetch non-grid data for contracted services
	 * grid.</li>
	 * <ul>
	 * <li>Calls query 'fetchNonGridContractedServices'</li>
	 * </ul>
	 * @param aoMyBatisSession Session object
	 * @return ContractedServicesBean
	 * @param aoCBGridBeanObj CBGridBean attribute set.
	 * @throws ApplicationException Application exception handled
	 */
	public ContractedServicesBean fetchNonGridContractedServicesAmendment(SqlSession aoMyBatisSession,
			CBGridBean aoCBGridBeanObj, MasterBean aoMasterBean) throws ApplicationException
	{
		ContractedServicesBean loCBContractedServicesBean = null;
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);
		try
		{
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				loCBContractedServicesBean = new ContractedServicesBean();
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBContractedServicesBean = fetchContractedServicesBeanDataFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{
			    //[Start] R9.4.0 QC8522
				loCBContractedServicesBean = (ContractedServicesBean) DAOUtil
						.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
								HHSConstants.CBY_FETCH_NON_GRID_CONTRACTED_SERVICES_AMND,
								HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				 //[End] R9.4.0 QC8522
			}

		}
		/**
		 * Application Exception handled here
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CBM_NEW_CONTRACTED_SERVICES, aoCBGridBeanObj);
			LOG_OBJECT.Error(
					"Exception occured in ContractBudgetAmendmentService: fetchNonGridContractedServices method:: ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService: fetchNonGridContractedServices"
					+ " method - failed Exception occured while fetching\n");
			throw loAppEx;
		}

		return loCBContractedServicesBean;
	}

	/**
	 * This method fetches non-grid data for ContractedServicesBean from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedData ContractedServicesBean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public ContractedServicesBean fetchContractedServicesBeanDataFromXML(String asSubBudgetId, MasterBean aoMasterBean)
			throws ApplicationException
	{
		List<LineItemMasterBean> loMasterBeanList = null;
		ContractedServicesBean loReturnedData = null;
		loMasterBeanList = aoMasterBean.getMasterBeanList();
		Iterator<LineItemMasterBean> aoListIterator = loMasterBeanList.iterator();
		while (aoListIterator.hasNext())
		{
			LineItemMasterBean loLineItemBean = aoListIterator.next();
			if (loLineItemBean.getSubbudgetId().equals(asSubBudgetId))
			{
				loReturnedData = loLineItemBean.getNonGridConServiceData();
			}
		}
		return loReturnedData;
	}

	/**
	 * <li>This service class is invoked through fetchFyBudgetSummary
	 * transaction id for Contract budget screen</li> <li>
	 * This method fetchFyBudgetSummary will get the Fiscal Year contract
	 * Information on the basis of contractId</li>
	 * <ul>
	 * <li>Calls query 'fetchFyBudgetSummary'</li>
	 * <li>Calls query 'getInvoiceAmountForModification'</li>
	 * <li>Calls query 'getAmendAmount'</li>
	 * </ul>
	 * @param aoMybatisSession sqlsession object
	 * @param aoHashMap hashmap
	 * @return loContractList BudgetDetails
	 * @throws ApplicationException object
	 */
	public BudgetDetails fetchFyBudgetSummary(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap,
			MasterBean aoMasterBean, CBGridBean aoCBGridBeanObj) throws ApplicationException
	{
		BudgetDetails loFyBudget = null;
		try
		{
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);

			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				loFyBudget = aoMasterBean.getBudgetDetails();
			}
			else
			{
				loFyBudget = (BudgetDetails) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.FETCH_FY_BUDGET_SUMMARY, HHSConstants.JAVA_UTIL_HASH_MAP);
				if (loFyBudget == null)
				{
					loFyBudget = new BudgetDetails();
					return loFyBudget;
				}
				else
				{
					BigDecimal loYtdInvoiceAmount = null;
					loYtdInvoiceAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.GET_INVOICE_AMOUNT_FOR_MODIFICATION, HHSConstants.JAVA_UTIL_HASH_MAP);

					loFyBudget.setYtdInvoicedAmount(loYtdInvoiceAmount);

					BigDecimal loAmendAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession,
							aoHashMap.get(HHSConstants.BUDGET_ID_WORKFLOW),
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER, HHSConstants.GET_AMEND_AMOUNT,
							HHSConstants.JAVA_LANG_STRING);

					loFyBudget.setAmendmentAmount(loAmendAmount);
					loFyBudget.setRemainingAmount(loFyBudget.getApprovedBudget().subtract(loYtdInvoiceAmount));
					loFyBudget.setProposedBudget(loFyBudget.getApprovedBudget().add(loAmendAmount));
				}
			}
		}
		// Application Exception handled here
		catch (ApplicationException aoAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			aoAppEx.addContextData(HHSConstants.BUDGET_ID, aoHashMap);
			LOG_OBJECT
					.Error("Exception occured while retrieveing fetchFyBudgetSummary Information in ContractBudgetAmendmentService ",
							aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService: fetchFyBudgetSummary method - failed to fetch"
					+ aoHashMap + " \n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			LOG_OBJECT
					.Error("Exception occured while retrieveing Fiscal Year Contract Information in ContractBudgetAmendmentService ",
							aoEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService: fetchFyBudgetSummary method - failed to fetch"
					+ aoHashMap + " \n");
			throw new ApplicationException("Error occured while retrieving Fiscal Year Contract Summary", aoEx);
		}
		return loFyBudget;
	}

	/**
	 * This method is triggered to get the information in session. <li>This
	 * service class is invoked through getCbGridDataForSession transaction id
	 * for Contract budget Amendment screen</li> <li>This method
	 * getCbGridDataForSession will get the session Information on the basis of
	 * budgetId & contractId</li>
	 * <ul>
	 * <li>A hashmap is passed to get the data.</li>
	 * </ul>
	 * <ul>
	 * <li>Calls query 'getCbGridDataForSession'</li>
	 * </ul>
	 * @param aoMybatisSession Mybatis Session Object.
	 * @param aoHashMap HashMap object.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return loCBGridBean CBGridBean object
	 */
	@SuppressWarnings("rawtypes")
	public CBGridBean getCbGridDataForSession(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		CBGridBean loCBGridBean = null;
		try
		{
			loCBGridBean = (CBGridBean) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
					HHSConstants.GET_CB_GRID_DATA_FOR_SESSION, HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		// Application Exception handled here
		catch (ApplicationException loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			loAppEx.addContextData(HHSConstants.BUDGET_ID, aoHashMap);
			LOG_OBJECT.Error("Exception occured while retrieveing CbGridDataForSession "
					+ "Information in ContractBudgetAmendmentService ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService: "
					+ "getCbGridDataForSession method - failed to fetch" + aoHashMap + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			LOG_OBJECT.Error(
					"Exception occured while retrieveing CbGridDataForSession in ContractBudgetAmendmentService ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService: "
					+ "getCbGridDataForSession method - failed to fetch" + aoHashMap + " \n");
			throw new ApplicationException("Error occured while fetching data", loAppEx);
		}

		return loCBGridBean;
	}

	/**
	 * <li>This service class is invoked through fetchCMSubBudgetSummary
	 * transaction id for Contract budget Modification screen</li> <li>This
	 * method fetchCMSubBudgetSummary will get the SubBudget Information on the
	 * basis of budgetId</li>
	 * <ul>
	 * <li>Calls query 'fetchCMSubBudgetPrintSummary'</li>
	 * </ul>
	 * <ul>
	 * <li>Calls query 'fetchCMSubBudgetSummary'</li>
	 * </ul>
	 * @param aoMybatisSession Mybatis Session Object.
	 * @param aoHashmap HashMap object.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return loSubBudgetList
	 */

	@SuppressWarnings("unchecked")
	public List<CBGridBean> fetchCMSubBudgetSummary(SqlSession aoMybatisSession, HashMap<String, String> aoHashmap)
			throws ApplicationException
	{
		List<CBGridBean> loSubBudgetList = null;
		try
		{
			if (aoHashmap.get(HHSConstants.SUBBUDGET_ID) != null)
			{
				loSubBudgetList = (List<CBGridBean>) DAOUtil.masterDAO(aoMybatisSession, aoHashmap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_CM_SUB_BUDGET_PRINT_SUMMARY, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			else
			{
				loSubBudgetList = (List<CBGridBean>) DAOUtil.masterDAO(aoMybatisSession, aoHashmap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_CM_SUB_BUDGET_SUMMARY, HHSConstants.JAVA_UTIL_HASH_MAP);
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
			setMoState("Transaction Failed:: ContractBudgetModificationService: "
					+ "fetchCMSubBudgetSummary method - failed to fetch" + aoHashmap + " \n");
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
			setMoState("Transaction Failed:: ContractBudgetModificationService: "
					+ "fetchCMSubBudgetSummary method - failed to fetch" + aoHashmap + " \n");
			throw new ApplicationException("Error occured while retrieving Contract Modification SubBudgetSummary",
					loAppEx);
		}
		return loSubBudgetList;
	}

	/**
	 * <p>
	 * This method amend Unallocated fund details in DB
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
	public boolean updateAmendmentUnallocatedFunds(SqlSession aoMybatisSession, UnallocatedFunds aoUnallocatedFundsBean)
			throws ApplicationException
	{   
		boolean lbUpdateStatus = false; 
		boolean lbError = false;
		try
		{
			BigDecimal loUnallocatedFundsRemAmt = null;
			//[Start]R7.12.0 QC9311 Minimize Debug
			//LOG_OBJECT.Debug("param: aoUnallocatedFundsBean :: "+ aoUnallocatedFundsBean);
			//[End]R7.12.0 QC9311 Minimize Debug
			aoUnallocatedFundsBean.setParentId(aoUnallocatedFundsBean.getId());
			LOG_OBJECT.Debug("after setup parentId;  aoUnallocatedFundsBean :: "+ aoUnallocatedFundsBean);
			// fetching the details for unallocated Funds.
			loUnallocatedFundsRemAmt = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoUnallocatedFundsBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
					HHSConstants.GET_REMAINING_AMOUNT_UN_ALLOCATED_IN_MULTIPLE_AMENDMENTS,
					HHSConstants.UNALLOCATED_FUNDS_BEAN);
				
			if ((new BigDecimal(aoUnallocatedFundsBean.getModificationAmount()).add(loUnallocatedFundsRemAmt))
					.compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO)
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.LESS_THEN_APPROVED_FOR_NEGATIVE_AMENDMENT_ERROR_MESSAGE));
			}
			// checking whether amendment type is null or blank or not.
			// then checking the positive/negative amendment condition
			// if not throwing exception
			
			if ((null != aoUnallocatedFundsBean.getAmendmentType())
					&& !(HHSConstants.EMPTY_STRING.equals(aoUnallocatedFundsBean.getAmendmentType().trim())))
			{   // VALIDATION FOR POSITIVE AMENDMENT (FOR POSITIVE AMENDMENT ONLY)
				if (aoUnallocatedFundsBean.getAmendmentType().equalsIgnoreCase(HHSConstants.POSITIVE)
						&& (new BigDecimal(aoUnallocatedFundsBean.getModificationAmount()).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO))
				{
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.POSITIVE_AMENDMENT_MSG));
				}
				// VALIDATION FOR NEGATIVE AMENDMENT (FOR NEGATIVE AMENDMENT ONLY)
				else if (aoUnallocatedFundsBean.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE)
						&& (new BigDecimal(aoUnallocatedFundsBean.getModificationAmount()).compareTo(BigDecimal.ZERO) > HHSConstants.INT_ZERO))
				{
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.NEGATIVE_AMENDMENT_MSG));
				}
				else
				{   
				//* Start QC 8394 R 7.9	
					//aoUnallocatedFundsBean.setAmmount(aoUnallocatedFundsBean.getModificationAmount());
					UnallocatedFunds childUnallocatedFundsBean = null;
					
					childUnallocatedFundsBean = (UnallocatedFunds)DAOUtil.masterDAO(aoMybatisSession, aoUnallocatedFundsBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.FETCH_UNALLOCATED_FUNDS_CHILD_RECORD, HHSConstants.UNALLOCATED_FUNDS_BEAN);
					LOG_OBJECT.Debug(" childUnallocatedFundsBean :: "+ childUnallocatedFundsBean);							
					if (childUnallocatedFundsBean != null)
					{      
						aoUnallocatedFundsBean.setChildId(childUnallocatedFundsBean.getId());
						if(aoUnallocatedFundsBean.getUnallocatedFund() == null)
						{
							aoUnallocatedFundsBean.setUnallocatedFund(childUnallocatedFundsBean.getUnallocatedFund());
						}	
					}
					else
					{
						UnallocatedFunds parentUnallocatedFundsBean = null;
						
						parentUnallocatedFundsBean = (UnallocatedFunds)DAOUtil.masterDAO(aoMybatisSession, aoUnallocatedFundsBean,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
								HHSConstants.FETCH_UNALLOCATED_FUNDS_PARENT_RECORD, HHSConstants.UNALLOCATED_FUNDS_BEAN);
						//LOG_OBJECT.Debug(" parentUnallocatedFundsBean :: "+ parentUnallocatedFundsBean);	
						if(aoUnallocatedFundsBean.getUnallocatedFund() == null)
						{
							aoUnallocatedFundsBean.setUnallocatedFund(parentUnallocatedFundsBean.getUnallocatedFund());
						}	
					}
					
					LOG_OBJECT.Debug("aoUnallocatedFundsBean :: "+ aoUnallocatedFundsBean);
					// update child record with modification amount
					if(null!=aoUnallocatedFundsBean.getChildId())
					{
						DAOUtil.masterDAO(aoMybatisSession, aoUnallocatedFundsBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
							HHSConstants.AMENDMENT_UPDATE_UNALLOCATED_FUNDS, HHSConstants.UNALLOCATED_FUNDS_BEAN);
						lbUpdateStatus = true;
					}
					else
					{   // create child record with modification amount
						DAOUtil.masterDAO(aoMybatisSession, aoUnallocatedFundsBean,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
								HHSConstants.INSERT_NEW_UNALLOCATED_FUNDS_FOR_AMD, HHSConstants.UNALLOCATED_FUNDS_BEAN);
							lbUpdateStatus = true;
					}
					//*End QC 8394 R 7.9	
				}
			}
			else
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.AMEND_CONTRACT_FAILURE));
			}
		}
		// ApplicationException is thrown while executing the query.
		// and when the Amendment Type rule is not followed.
		// For positive amendment, amendment is not positive and same for
		// negative one, and if the sum of approved budget and amendment budget
		// is smaller
		// than zero
		catch (ApplicationException aoExp)
		{
			if (lbError)
			{
				aoExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoExp.toString());
			}

			setMoState("error occured while updating Amendment unallocated funds for business type id "
					+ aoUnallocatedFundsBean.getBudgetId());
			aoExp.addContextData("Exception occured while updating Amendment unallocated funds ", aoExp);
			LOG_OBJECT.Error("Transaction Failed:: ContractBudgetAmendmentService:"
					+ "updateAmendmentUnallocatedFunds method - failed to update " + aoExp.getMessage() + " \n");
			throw aoExp;
		}
		return lbUpdateStatus;
	}

	/**
	 * <p>
	 * This method fetch unallocated funds details from DB
	 * <ul>
	 * <li>Call fetchAmendmentUnallocatedFunds query set sub budget id as where
	 * clause</li>
	 * <li>Inserting default object, here we are inserting two rows if both Base
	 * and Amendment version is not there in the database</li>
	 * <li>Inserting default object, here we are inserting one rows for
	 * Amendment version if its not there</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession Sql session object
	 * @param aoCBGridBeanObj Grid bean object
	 * @return loUnallocatedFunds Channel object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List fetchAmendmentUnallocatedFunds(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj,
			MasterBean aoMasterBean) throws ApplicationException
	{
		List<UnallocatedFunds> loUnallocatedFunds = null;
		List<UnallocatedFunds> loAmendmentUnallocatedFunds = null;
		
		try
		{
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);

			// Fetch the data from FileNet XML if Budget Status is approved
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loUnallocatedFunds = fetchUnallocatedFundsFromXML(lsSubBudgetId, aoMasterBean);
			}
			else if (null != aoCBGridBeanObj && null != aoCBGridBeanObj.getSubBudgetID())
			{
				// fetching Base ad New for unallocated Funds.
				loUnallocatedFunds = (List<UnallocatedFunds>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.AMENDMENT_FETCH_UNALLOCATED_FUNDS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				
				//  Start QC 8394 R 7.9 add/delete Unallocated Fund
				// fetch Amendment of base line
				loAmendmentUnallocatedFunds = (List<UnallocatedFunds>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.FETCH_AMENDMENT_UNALLOCATED_FUNDS_TO_BASE_LINE, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
								
				// update modification amount for base unallocated line if it amendment line has been found
				if (loAmendmentUnallocatedFunds != null && !loAmendmentUnallocatedFunds.isEmpty())
				{   
					for (UnallocatedFunds unallocatedFundsAmnd : loAmendmentUnallocatedFunds)
					{   
						if (loUnallocatedFunds != null && !loUnallocatedFunds.isEmpty())
						{
							for (UnallocatedFunds unallocatedFundsBase : loUnallocatedFunds)
							{   
								if (unallocatedFundsBase.getId().equals(unallocatedFundsAmnd.getParentId()))
								{
									unallocatedFundsBase.setModificationAmount(unallocatedFundsAmnd.getModificationAmount());
									unallocatedFundsBase.setChildId(unallocatedFundsAmnd.getId());
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
				
				 
				/*
				// Inserting default object, here we are inserting two rows if
				// both Base and amendment version is not there in the
				// database
				
				if (loUnallocatedFunds.isEmpty())
				{
					loUnallocatedFunds = new ArrayList<UnallocatedFunds>();
					UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
					loUnallocatedFunds.add(loUnallocatedFundsBean);
				}
			
				// Inserting default object, here we are inserting one rows for
				// amendment version
				else if (HHSConstants.INT_ZERO == loUnallocatedFunds.get(HHSConstants.INT_ZERO).getModCount())
				{
					
					aoCBGridBeanObj.setSubBudgetAmount(loUnallocatedFunds.get(HHSConstants.INT_ZERO).getAmmount());
					
					
					DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
							HHSConstants.AMENDMENT_INSERT_AMN_AMOUNT, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
					
				}
			   */
			//  End QC 8394 R 7.9 add/delete Unallocated Fund
			}
		}
		// ApplicationException is thrown while executing the query.
		catch (ApplicationException aoAppExp)
		{
			// setting the transaction state for exception and setting the
			// context
			setMoState("error occured while fetching Amendment unallocated for business type id "
					+ aoCBGridBeanObj.getContractBudgetID());
			aoAppExp.addContextData("Exception occured while fetching Amendment unallocated ", aoAppExp);
			LOG_OBJECT.Error("Transaction Failed:: ContractBudgetAmendmentService:"
					+ "fetchAmendmentUnallocatedFunds method - failed to fetch or insert " + aoAppExp.getMessage()
					+ " \n");
			throw aoAppExp;
		}
		
		return loUnallocatedFunds;
	}

	/**
	 * This Method fetches the values of the <b>Professional Services</b> tab
	 * (Used by Provider Users) in the Contract Budget Amendment screen for
	 * every individual sub budget of the current Fiscal Year with the help of
	 * unique Sub budget ID
	 * <ul>
	 * <li>This service will behave differently for four scenarios :</li>
	 * <ul>
	 * <li>Amendment -
	 * <ul>
	 * <li>Only Amendment Modification Amount column is editable</li>
	 * <li>For - "Positive Amendment", Verify upon save that the amount entered
	 * is a positive number</li>
	 * <li>For - "Negative Amendment", Verify upon save that the amount entered
	 * is a negative number</li>
	 * <li>Verify upon save that the Modification Amount entered would not cause
	 * the Total Proposed Budget for the line item to fall below the YTD
	 * Invoiced Amount. t</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <ul>
	 * <li>Calls query 'fetchProfServicesDetailsAmendment'</li>
	 * </ul>
	 * @param aoProfService - CBGridBean object
	 * @param aoMybatisSession - SqlSession object
	 * @return loProfServicesDetails - List of CBProfessionalServicesBean
	 * @throws ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<CBProfessionalServicesBean> fetchProfServicesDetailsAmendment(CBGridBean aoProfService,
			SqlSession aoMybatisSession, MasterBean aoMasterBean) throws ApplicationException
	{
		List<CBProfessionalServicesBean> loProfServicesDetails = null;
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);

		try
		{
			// Fetching Professional services details data to display on
			// Contract Budget Amendment - Professional Service tab
			// Fetch the data from FileNet XML if Budget Status is approved
			if (aoMasterBean != null && aoProfService.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoProfService.getSubBudgetID();
				loProfServicesDetails = fetchProfessionalServiceListFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{
				loProfServicesDetails = (List<CBProfessionalServicesBean>) DAOUtil.masterDAO(aoMybatisSession,
						aoProfService, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.AMENDMENT_FETCH_PROF_SERVICES_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
/*  [Start] R7.5.0 QC9146 Professional Service Grid issue for Amendment
				if (loProfServicesDetails != null && loProfServicesDetails.size() > HHSConstants.INT_ZERO)
				{
					amendmentProfServiceBeanIds(loProfServicesDetails);
				}
 [End] R7.5.0 QC9146 Professional Service Grid issue for Amendment  */
			}

		}
		// Exception occur if database is down or getting null in required
		// parameter
		catch (ApplicationException loAppExp)
		{
			setMoState("Transaction Failed:: ContractBudgetAmendmentService: "
					+ "fetchProfServicesDetailsAmendment method - failed to fetch "
					+ "PROFESSIONAL_SERVICES details for sub_budget_id : " + aoProfService.getSubBudgetID() + " \n");

			loAppExp.addContextData("Error occured while fetching Professional Service Details"
					+ "for ContractBudgetAmendmentService: editProfServicesDetailsAmendment method", loAppExp);
			LOG_OBJECT.Error("Error occured while fetching Professional Service Details"
					+ "for ContractBudgetAmendmentService: editProfServicesDetailsAmendment method:" + loAppExp);
			throw loAppExp;
		}

		return loProfServicesDetails;
	}

	/**
	 * <p>
	 * This method concatenates profServiceTypeId, remainingAmount and cbmId
	 * with ProfessionalServiceId and UNDERSCORE Character and set in ID
	 * 
	 * @param aoProfServicesDetailsListForAmendement - List of
	 *            CBProfessionalServicesBean
	 */
	private void amendmentProfServiceBeanIds(List<CBProfessionalServicesBean> aoProfServicesDetailsListForAmendement)
	{
		StringBuffer loConcatString = null;
		for (CBProfessionalServicesBean loCBProfServIterateBean : aoProfServicesDetailsListForAmendement)
		{
			loConcatString = new StringBuffer(loCBProfServIterateBean.getId());
			loConcatString.append(HHSConstants.UNDERSCORE).append(loCBProfServIterateBean.getProfServiceTypeId());
			loConcatString.append(HHSConstants.UNDERSCORE).append(loCBProfServIterateBean.getFyBudget());

			if (loCBProfServIterateBean.getCbmId() != null
					&& !loCBProfServIterateBean.getCbmId().equals(HHSConstants.EMPTY_STRING))
			{
				loConcatString.append(HHSConstants.UNDERSCORE).append(loCBProfServIterateBean.getCbmId());
			}
			loCBProfServIterateBean.setId(loConcatString.toString());
		}
	}

	/**
	 * This Method updates the values of the <b>Professional Services</b> tab
	 * (Used by Provider Users) in the Contract Budget Amendment screen for
	 * every individual sub budget of the current Fiscal Year with the help of
	 * unique Sub budget ID.
	 * <ul>
	 * <li>This service will behave differently for four scenarios :</li>
	 * <ul>
	 * <li>Amendment -
	 * <ul>
	 * <li>Only Amendment Amount column is editable</li>
	 * <li>For - "Positive Amendment", Verify upon save that the amount entered
	 * is a positive number</li>
	 * <li>For - "Negative Amendment", Verify upon save that the amount entered
	 * is a negative number</li>
	 * <li>Verify upon save that the Amendment Amount entered would not cause
	 * the Total Proposed Budget for the line item to fall below the YTD
	 * Invoiced Amount and also taking in to consideration ? Negative Amendment
	 * amounts entered for the Professional Services line item in other pending
	 * Amendments for which no budgets have been approved.</li>
	 * <li>This method calls the query
	 * 'fetchProfServiceForValidationInMultipleAmendments'</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoProfService - CBProfessionalServicesBean object
	 * @param aoMybatisSession - SqlSession object
	 * @return lbUpdateStatus - boolean
	 * @throws ApplicationException object
	 * 
	 * 
	 * 
	 */
	public boolean editProfServicesDetailsAmendment(CBProfessionalServicesBean aoProfService,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		boolean lbError = false;
		boolean lbUpdateStatus = false;
		Integer loAddOrUpdateStatus = HHSConstants.INT_ZERO;
		try
		{
			// Checking, if amendment type is positive. Amendment type can be
			// positive or negative.
			if (aoProfService.getAmendmentType() != null
					&& !(HHSConstants.EMPTY_STRING.equalsIgnoreCase(aoProfService.getAmendmentType().trim())))
			{
				BigDecimal loAmendMentAmount = new BigDecimal(aoProfService.getModifyAmount());

				// Checking, when amendment type is positive and give negative
				// amendment amount.
				if (aoProfService.getAmendmentType().equalsIgnoreCase(HHSConstants.POSITIVE)
						&& (loAmendMentAmount.compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO))
				{
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.POSITIVE_AMENDMENT_MSG));
				}
				// Checking, if amendment type is negative. Amendment type can
				// be
				// positive or negative.
				else if (aoProfService.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE)
						&& (loAmendMentAmount.compareTo(BigDecimal.ZERO) > HHSConstants.INT_ZERO))
				{
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.NEGATIVE_AMENDMENT_MSG));
				}
				else
				{
					// Fetch and split all the parameters joined together in id
					// property
					// of CBProfessionalServicesBean (Joined while fetching the
					// data for
					// Professional Service Amendment)
					String[] loIds = aoProfService.getId().split(HHSConstants.UNDERSCORE);
					String lsId = loIds[HHSConstants.INT_ZERO];
					aoProfService.setId(lsId);
					
/*  [Start] R7.5.0 QC9146 Professional Service Grid issue for Amendment
					String lsProfServiceTypeId = loIds[HHSConstants.INT_ONE];
					String lsPrevApprovedBudget = loIds[HHSConstants.INT_TWO];

					aoProfService.setProfServiceTypeId(lsProfServiceTypeId);
					aoProfService.setFyBudget(lsPrevApprovedBudget);
[End] R7.5.0 QC9146 Professional Service Grid issue for Amendment  */

					// Retrieve remaining amount for line item
					BigDecimal loRemainingAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoProfService,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
							HHSConstants.FETCH_PROF_SERVICE_VALIDATION_IN_MULTIPLE_AMENDMENTS,
							HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN);
					// Check if Amendment amount + Remaining Amount is not less
					// than
					// zero
					if ((loRemainingAmount.add(loAmendMentAmount)).compareTo(BigDecimal.ZERO) >= HHSConstants.INT_ZERO)
					{
						loAddOrUpdateStatus = addOrUpdateAmendmentAmount(aoProfService, aoMybatisSession, loIds);
						if (loAddOrUpdateStatus == HHSConstants.INT_ONE)
						{
							lbUpdateStatus = true;
						}
						else
						{
							lbError = true;
							throw new ApplicationException("Error occured while updating "
									+ "Amendment Amount for Professional Service");
						}
					}
					else
					{
						lbError = true;
						throw new ApplicationException(PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								HHSConstants.CBM_LESS_THAN_INVOICE_INCLUDING_PENDING_NEG_AMEND_ERROR_MESSAGE));
					}
				}
			}
			else
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.AMEND_CONTRACT_FAILURE));
			}
		}
		// if amendment amount is failed to add or update due to database issue
		// or getting null in required parameter
		catch (ApplicationException loAppExp)
		{
			if (lbError)
			{
				loAppExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loAppExp.toString());
			}
			setMoState("Transaction Failed:: ContractBudgetAmendmentService:"
					+ "editProfServicesDetailsAmendment method - failed to add or update " + "PROFESSIONAL_SERVICES : "
					+ loAppExp.getMessage() + " \n");
			LOG_OBJECT.Error("Transaction Failed:: ContractBudgetAmendmentService:"
					+ "editProfServicesDetailsAmendment method - failed to add or update " + "PROFESSIONAL_SERVICES : "
					+ loAppExp.getMessage() + " \n");
			throw loAppExp;
		}
		catch (Exception loExp)
		{
			setMoState("Transaction Failed:: ContractBudgetAmendmentService: "
					+ "editProfServicesDetailsAmendment method - failed to add or update " + "PROFESSIONAL_SERVICES : "
					+ loExp.getMessage() + " \n");
			LOG_OBJECT.Error("Transaction Failed:: ContractBudgetAmendmentService:"
					+ "editProfServicesDetailsAmendment method - failed to add or update " + "PROFESSIONAL_SERVICES : "
					+ loExp.getMessage() + " \n");
			throw new ApplicationException("Error occured while adding/updating Amendment Amount"
					+ " for Professional Service", loExp);
		}
		return lbUpdateStatus;
	}

	/**
	 * <p>
	 * This method concatenates profServiceTypeId, remainingAmount and cbmId
	 * with ProfessionalServiceId and UNDERSCORE Character and set in ID
	 * <ul>
	 * <li>Calls query 'updateProfServicesAmendmentAmount'</li>
	 * <li>Calls query 'addProfServicesAmendmentAmount'</li>
	 * </ul>
	 * @param aoProfService - CBProfessionalServicesBean object
	 * @param aoMybatisSession - SqlSession object
	 * @param aoIds - String array
	 * @return loAddOrUpdateStatus - Integer
	 * @throws ApplicationException object
	 */
	private Integer addOrUpdateAmendmentAmount(CBProfessionalServicesBean aoProfService, SqlSession aoMybatisSession,
			String[] aoIds) throws ApplicationException
	{
		Integer loAddOrUpdateStatus = HHSConstants.INT_ZERO;

        loAddOrUpdateStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoProfService,
                HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
                HHSConstants.AMENDMENT_MERGE_PROF_SERVICES_AMOUNT,
                HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN);

/*		if (aoIds.length > HHSConstants.INT_THREE)
		{
			// Update budget amount in Professional Service table
			// Update Amendment amount
			aoProfService.setCbmId(aoIds[HHSConstants.INT_THREE]);

			loAddOrUpdateStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoProfService,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
					HHSConstants.AMENDMENT_UPDATE_PROF_SERVICES_AMOUNT,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN);
		}
		else
		{
			// Add amendment amount in PROFESSIONAL_SERVICES
			// table
			loAddOrUpdateStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoProfService,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
					HHSConstants.AMENDMENT_ADD_PROF_SERVICES_AMOUNT,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN);
		}*/

		return loAddOrUpdateStatus;
	}

	// S349 otps amendment start
	/**
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
	 * </ul>
	 * </p>
	 * <ul>
	 * <li>Calls query 'fetchOpAndSupportModPageData'</li>
	 * </ul>
	 * @param aoCBGridBeanObj - CBGridBean object containing key fields
	 * @param aoMyBatisSession - SqlSession object
	 * @return List<CBOperationSupportBean> - returns list of bean of type
	 *         <CBOperationSupportBean>
	 * @throws ApplicationException - ApplicationException object
	 */
	public CBOperationSupportBean fetchOpAndSupportAmendPageData(CBGridBean aoCBGridBeanObj,
			SqlSession aoMyBatisSession, MasterBean aoMasterBean) throws ApplicationException
	{
		CBOperationSupportBean loCBOperationSupportBean = null;
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);

		try
		{
			// Fetch the data from FileNet XML if Budget Status is approved
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBOperationSupportBean = fetchOpAndSupportAmendPageDataFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{

				loCBOperationSupportBean = (CBOperationSupportBean) DAOUtil.masterDAO(aoMyBatisSession,
						aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_OPERATION_SUPP_MOD_PAGE_DATA, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			}
			setMoState("ContractBudgetAmendmentService: fetchOpAndSupportAmendPageData() passed");
		}
		// Exception occur if database is down or getting null in required
		// parameter
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.CB_GRID_BEAN_OBJ, CommonUtil.convertBeanToString(aoCBGridBeanObj));
			LOG_OBJECT.Error("App Exception occured while retrieving in ContractBudgetAmendmentService ", aoAppEx);
			setMoState("ContractBudgetAmendmentService: fetchOpAndSupportAmendPageData() failed to fetch "
					+ "with details aoCBGridBeanObj:" + aoCBGridBeanObj + " \n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetAmendmentService: fetchOpAndSupportAmendPageData method:: ", aoEx);
			LOG_OBJECT.Error("Exception occured while retrieving in ContractBudgetAmendmentService ", loAppEx);
			setMoState("ContractBudgetAmendmentService: fetchOpAndSupportAmendPageData() failed to fetch "
					+ "with details aoCBGridBeanObj:" + aoCBGridBeanObj + " \n");
			throw loAppEx;
		}
		return loCBOperationSupportBean;
	}

	/**
	 * <p>
	 * This method retrieves all details for operation and support grid in
	 * contract budget amendment
	 * </p>
	 * <ul>
	 * <li>1.Fetch FY budget amount for operation and support in
	 * List<CBOperationSupportBean></li>
	 * <li>2.Fetch Amendment amount for operation and support in
	 * List<CBOperationSupportBean></li>
	 * <li>3.Merge both list into one of type List<CBOperationSupportBean></li>
	 * </ul>
	 * <ul>
	 * <li>Calls query 'fetchOperationAndSupportAmendDetails'</li>
	 * </ul>
	 * @param aoMybatisSession Sql session object
	 * @param aoCBGridBeanObj CBGridBeanObj object
	 * @return List<CBOperationSupportBean> CBOperationSupportBean list
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<CBOperationSupportBean> fetchAmendmentOTPS(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj,
			MasterBean aoMasterBean) throws ApplicationException
	{
		List<CBOperationSupportBean> loCBAmendOtpsList = null;
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);

		try
		{
			// Fetch the data from FileNet XML if Budget Status is approved
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBAmendOtpsList = fetchOTPSFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{
				aoCBGridBeanObj.setEntryTypeId(HHSConstants.TWO);
				loCBAmendOtpsList = (List<CBOperationSupportBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.FETCH_OPERATION_AND_SUPPORT_AMEND_DETAILS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			}

			setMoState("ContractBudgetAmendmentService: fetchAmendmentOTPS() passed");
		}
		// Exception occur if database is down or getting null in required
		// parameter
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.CB_GRID_BEAN_OBJ, CommonUtil.convertBeanToString(aoCBGridBeanObj));
			LOG_OBJECT.Error("App Exception occured while retrieving in ContractBudgetAmendmentService ", aoAppEx);
			setMoState("ContractBudgetAmendmentService: fetchAmendmentOTPS() failed to fetch "
					+ "with details aoCBGridBeanObj:" + aoCBGridBeanObj + " \n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetAmendmentService: fetchAmendmentOTPS method:: ", aoEx);
			LOG_OBJECT.Error("Exception occured while retrieving in ContractBudgetAmendmentService ", loAppEx);
			setMoState("ContractBudgetAmendmentService: fetchAmendmentOTPS() failed to fetch "
					+ "with details aoCBGridBeanObj:" + aoCBGridBeanObj + " \n");
			throw loAppEx;
		}
		return loCBAmendOtpsList;
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
	 * Update Amount must be equal to the change in the FY Budget value</li>
	 * </ul>
	 * </p>
	 * <ul>
	 * <li>Calls query 'editOperationAndSupportAmendDetails'</li>
	 * <li>Calls query 'insertOperationAndSupportAmendDetails'</li>
	 * </ul>
	 * @param aoCBOperationSupportBean - CBOperationSupportBean object
	 *            containing key fields
	 * @param aoMyBatisSession - SqlSession object
	 * @return Boolean - returns boolean status of edit/update query
	 * @throws ApplicationException - ApplicationException object
	 */
	public Boolean editAmendmentOTPS(CBOperationSupportBean aoCBOperationSupportBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		boolean lbEditStatus = false;
		boolean lbError = false;
		Map<String, Object> loReturnMap = null;
		try
		{
			loReturnMap = validateModificationAmountForOTPS(aoMyBatisSession, aoCBOperationSupportBean);

			if (!(Boolean) loReturnMap.get(HHSR5Constants.LB_ERROR))
			{
				Integer loUpdatedRows = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBOperationSupportBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.EDIT_OPERATION_AND_SUPPORT_AMEND_DETAILS,
						HHSConstants.MODEL_CB_OPERATION_SUPPORT_BEAN);

				if (null != loUpdatedRows && loUpdatedRows == HHSConstants.INT_ZERO)
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoCBOperationSupportBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
							HHSConstants.INSERT_OPERATION_AND_SUPPORT_AMEND_DETAILS,
							HHSConstants.MODEL_CB_OPERATION_SUPPORT_BEAN);

					insertIfOtherLineItemCase(aoMyBatisSession, aoCBOperationSupportBean);
				}
			}
			else
			{
				lbError = true;
				throw new ApplicationException((String) loReturnMap.get(HHSR5Constants.LS_ERROR_CONSTANT));
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
						+ "amount already invoiced for the line item. Please enter a new value " + aoAppEx);
				setMoState("Entered value would cause the Proposed Budget to fall below the"
						+ "amount already invoiced for the line item. Please enter a new value");
			}
			else
			{
				aoAppEx.addContextData(HHSConstants.CB_OPERATION_SUPPORT_BEAN,
						CommonUtil.convertBeanToString(aoCBOperationSupportBean));
				LOG_OBJECT.Error("App Exception occured in ContractBudgetAmendmentService:"
						+ " editAmendmentOTPS method:: ", aoAppEx);
				setMoState("Transaction Failed::App Exception in ContractBudgetAmendmentService: "
						+ "editAmendmentOTPS() method aoCBOperationSupportBean::" + aoCBOperationSupportBean + "\n");
			}
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetAmendmentService: editAmendmentOTPS method:: ", aoEx);
			loAppEx.addContextData(HHSConstants.CB_OPERATION_SUPPORT_BEAN,
					CommonUtil.convertBeanToString(aoCBOperationSupportBean));
			LOG_OBJECT.Error("Exception occured in ContractBudgetAmendmentService: editAmendmentOTPS method:: ",
					loAppEx);
			setMoState("Transaction Failed::Exception in ContractBudgetAmendmentService: "
					+ "editAmendmentOTPS() method aoCBOperationSupportBean::" + aoCBOperationSupportBean + "\n");
			throw loAppEx;
		}
		return lbEditStatus;
	}

	/**
	 * This is private method used to insert entry in other_details table when
	 * in contract amendment budget, provider enters an amount in amendment
	 * column and that field in other's field
	 * <ul>
	 * <li>Calls query 'fetchOtherEntryIfExists'</li>
	 * <li>Calls query 'insertAmendRecInOtherDetails'</li>
	 * </ul>
	 * @param aoMyBatisSession SqlSession object
	 * @param aoCBOperationSupportBean CBOperationSupportBean object
	 * @throws Exception ApplicationException object
	 */
	private void insertIfOtherLineItemCase(SqlSession aoMyBatisSession, CBOperationSupportBean aoCBOperationSupportBean)
			throws Exception
	{
		Integer loCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBOperationSupportBean,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER, HHSConstants.FETCH_OTHER_ENTRY_IF_EXISTS,
				HHSConstants.MODEL_CB_OPERATION_SUPPORT_BEAN);

		if (null != loCount && loCount > 0)
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoCBOperationSupportBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
					HHSConstants.INSERT_AMEND_REC_IN_OTHER_DETAILS, HHSConstants.MODEL_CB_OPERATION_SUPPORT_BEAN);
		}
	}

	/**
	 * This method validates the modification amount in OTPS for a particular
	 * line-item and returns FALSE if the #amount user is trying to update tries
	 * to make the total amount for a line-item to fall below zero and also
	 * taking in to consideration ? Negative Amendment amounts entered for the
	 * Operation and Support line item in other pending Amendments for which no
	 * budgets have been approved, else it validates it as TRUE
	 * 
	 * @param aoMybatisSession SqlSession object
	 * @param aoCBOperationSupportBean CBOperationSupportBean object
	 * @return boolean lbError valid/invalid
	 * @throws Exception ApplicationException object
	 */
	private Map<String, Object> validateModificationAmountForOTPS(SqlSession aoMybatisSession,
			CBOperationSupportBean aoCBOperationSupportBean) throws Exception
	{
		Map<String, Object> loReturnMap = new HashMap<String, Object>();
		loReturnMap.put(HHSR5Constants.LB_ERROR, false);
		loReturnMap.put(HHSR5Constants.LS_ERROR_CONSTANT, HHSConstants.EMPTY_STRING);

		// Validation for positive amendment (For Positive Amendment only.)
		if (aoCBOperationSupportBean.getAmendmentType().equalsIgnoreCase(HHSConstants.POSITIVE)
				&& (new BigDecimal(aoCBOperationSupportBean.getAmendAmt()).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO))
		{
			loReturnMap.put(HHSR5Constants.LB_ERROR, true);
			loReturnMap.put(HHSR5Constants.LS_ERROR_CONSTANT, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.POSITIVE_AMENDMENT_MSG));

		}
		// Validation for negative amendment (For Negative Amendment only)
		else if (aoCBOperationSupportBean.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE)
				&& (new BigDecimal(aoCBOperationSupportBean.getAmendAmt()).compareTo(BigDecimal.ZERO) > HHSConstants.INT_ZERO))
		{
			loReturnMap.put(HHSR5Constants.LB_ERROR, true);
			loReturnMap.put(HHSR5Constants.LS_ERROR_CONSTANT, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.NEGATIVE_AMENDMENT_MSG));
		}

		else if (aoCBOperationSupportBean.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE))
		{
			BigDecimal loPendingNegAmendmentAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession,
					aoCBOperationSupportBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
					HHSConstants.CBM_FETCH_OTPS_DETAILS_FOR_VALIDATION_IN_MULTIPLE_AMENDMENTS,
					HHSConstants.MODEL_CB_OPERATION_SUPPORT_BEAN);

			// check sum of modified amount and remaining amount should be
			// greater than zero
			if ((loPendingNegAmendmentAmount.add(new BigDecimal(aoCBOperationSupportBean.getAmendAmt())))
					.compareTo(BigDecimal.ZERO)

			< HHSConstants.INT_ZERO

			)

			{
				loReturnMap.put(HHSR5Constants.LB_ERROR, true);
				loReturnMap.put(HHSR5Constants.LS_ERROR_CONSTANT, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CBM_LESS_THAN_INVOICE_INCLUDING_PENDING_NEG_AMEND_ERROR_MESSAGE));
			}
		}
		return loReturnMap;
	}

	/**
	 * <p>
	 * This method retrieves all details for equipment grid in OTPS contract
	 * budget amendment
	 * </p>
	 * <ul>
	 * <li>1.Fetch FY budget amount for Equipment OTPS in List<CBEquipmentBean></li>
	 * <li>2.Fetch Amendment amount for Equipment OTPS in List<CBEquipmentBean></li>
	 * <li>3.Merge both list into one of type List<CBEquipmentBean></li>
	 * </ul>
	 * <ul>
	 * <li>Calls query 'fetchEquipmentAmendDetails'</li>
	 * <li>Calls query 'fetchEquipmentAmendAmtDetails'</li>
	 * </ul>
	 * @param aoMybatisSession Sql session object
	 * @param aoCBGridBeanObj CBGridBeanObj object
	 * @return List<CBEquipmentBean> CBEquipmentBean list
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<CBEquipmentBean> fetchAmendmentOTPSEquipment(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj,
			MasterBean aoMasterBean) throws ApplicationException
	{
		List<CBEquipmentBean> loCBOPAmendList = null;
		List<CBEquipmentBean> loCBEquipmentBeanList = null;
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);
		try
		{
			// Fetch the data from FileNet XML if Budget Status is approved
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBEquipmentBeanList = fetchEquipmentFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{

				loCBEquipmentBeanList = (List<CBEquipmentBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.FETCH_EQUIPMENT_AMEND_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

				loCBOPAmendList = (List<CBEquipmentBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.FETCH_EQUIPMENT_AMEND_AMT_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				mergeAmendEquipmentList(loCBEquipmentBeanList, loCBOPAmendList);

			}

			setMoState("ContractBudgetAmendmentService: fetchAmendmentOTPSEquipment() passed");
		}
		// Exception occur if database is down or getting null in required
		// parameter
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.CB_GRID_BEAN_OBJ, CommonUtil.convertBeanToString(aoCBGridBeanObj));
			LOG_OBJECT.Error("App Exception occured while retrieving in ContractBudgetAmendmentService ", aoAppEx);
			setMoState("ContractBudgetAmendmentService: fetchAmendmentOTPSEquipment() failed to fetch "
					+ "with details aoCBGridBeanObj:" + aoCBGridBeanObj + " \n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetAmendmentService: fetchAmendmentOTPSEquipment method:: ", aoEx);
			LOG_OBJECT.Error("Exception occured while retrieving in ContractBudgetAmendmentService ", loAppEx);
			setMoState("ContractBudgetAmendmentService: fetchAmendmentOTPSEquipment() failed to fetch "
					+ "with details aoCBGridBeanObj:" + aoCBGridBeanObj + " \n");
			throw loAppEx;
		}

		return loCBEquipmentBeanList;
	}

	/**
	 * <p>
	 * This is a private method called for otps amendment to merge FY budget
	 * amount and amendment amount
	 * </p>
	 * <ul>
	 * <li>It reads record id and if it is new record then add to screen
	 * population list at index zero for sorting on created date</li>
	 * <li>Else update amount in original list</li>
	 * </ul>
	 * 
	 * @param aoCBEquipmentBeanList List<CBEquipmentBean> object with FY budget
	 *            amount
	 * @param aoCBOPAmendList List<CBEquipmentBean> object with amendment amount
	 * @throws Exception Exception thrown in case of any application code
	 *             failure
	 */
	private void mergeAmendEquipmentList(List<CBEquipmentBean> aoCBEquipmentBeanList,
			List<CBEquipmentBean> aoCBOPAmendList) throws Exception
	{
		for (CBEquipmentBean loCBEquipmentBean : aoCBOPAmendList)
		{
			if (loCBEquipmentBean.getId().toLowerCase().contains(HHSConstants.NEW_RECORD))
			{
				aoCBEquipmentBeanList.add(HHSConstants.INT_ZERO, loCBEquipmentBean);
			}
			else
			{
				updateAmendAmtInEquipment(aoCBEquipmentBeanList, loCBEquipmentBean);
			}
		}
	}

	/**
	 * <p>
	 * This is a private method called for OTPS amendment to update amendment
	 * amount
	 * </p>
	 * <ul>
	 * <li>It reads all original equipment bean list and if passed id is equal
	 * then it updates amount in CBEquipmentBean and comes out of loop</li>
	 * </ul>
	 * 
	 * @param aoCBEquipmentBeanList List<CBEquipmentBean> object with FY budget
	 *            amount
	 * @param aoCBEquipmentBean CBEquipmentBean object with amendment amount
	 * @throws Exception Exception thrown in case of any application code
	 *             failure
	 */
	private void updateAmendAmtInEquipment(List<CBEquipmentBean> aoCBEquipmentBeanList,
			CBEquipmentBean aoCBEquipmentBean) throws Exception
	{
		for (CBEquipmentBean loOrigEquipBean : aoCBEquipmentBeanList)
		{
			if (aoCBEquipmentBean.getId().equalsIgnoreCase(loOrigEquipBean.getId()))
			{
				loOrigEquipBean.setAmendAmt(aoCBEquipmentBean.getAmendAmt());
				loOrigEquipBean.setUnits(aoCBEquipmentBean.getUnits());
				break;
			}
		}
	}

	/**
	 * <p>
	 * This method is to add new row in equipment grid in OTPS contract budget
	 * amendment
	 * </p>
	 * <ul>
	 * <li>1.Call masterDAO to insert new record entries</li>
	 * <li>2.new record details are put in bean aoCBEquipmentBean</li>
	 * </ul>
	 * <ul>
	 * <li>Calls query 'addEquipmentAmendDetails'</li>
	 * </ul>
	 * @param aoMyBatisSession Sql session object
	 * @param aoCBEquipmentBean CBEquipmentBean object
	 * @return Boolean boolean status for insert success/failure
	 * @throws ApplicationException ApplicationException object
	 */
	public Boolean addAmendmentOTPSEquipment(SqlSession aoMyBatisSession, CBEquipmentBean aoCBEquipmentBean)
			throws ApplicationException
	{
		boolean lbEditStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoCBEquipmentBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
					HHSConstants.ADD_EQUIPMENT_AMEND_DETAILS, HHSConstants.MODEL_CB_EQUIPMENT_BEAN);
			lbEditStatus = true;
			setMoState("ContractBudgetAmendmentService: addAmendmentOTPSEquipment() passed");
		}
		// Exception occur if database is down or getting null in required
		// parameter
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.CB_OPERATION_SUPPORT_BEAN,
					CommonUtil.convertBeanToString(aoCBEquipmentBean));
			LOG_OBJECT.Error("App Exception occured in ContractBudgetAmendmentService: addAmendmentOTPSEquipment "
					+ "method:: ", aoAppEx);
			setMoState("Transaction Failed::App Exception in ContractBudgetAmendmentService: "
					+ "addAmendmentOTPSEquipment() method aoCBOperationSupportBean::" + aoCBEquipmentBean + "\n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetAmendmentService: addAmendmentOTPSEquipment method:: ", aoEx);
			loAppEx.addContextData(HHSConstants.CB_OPERATION_SUPPORT_BEAN,
					CommonUtil.convertBeanToString(aoCBEquipmentBean));
			LOG_OBJECT
					.Error("Exception occured in ContractBudgetAmendmentService: addAmendmentOTPSEquipment method:: ",
							loAppEx);
			setMoState("Transaction Failed::Exception in ContractBudgetAmendmentService: "
					+ "addAmendmentOTPSEquipment() method aoCBOperationSupportBean::" + aoCBEquipmentBean + "\n");
			throw loAppEx;
		}
		return lbEditStatus;
	}

	/**
	 * <p>
	 * This method is to edit existing row in equipment grid in OTPS contract
	 * budget amendment
	 * </p>
	 * <ul>
	 * <li>1.Call masterDAO to edit existing record entry</li>
	 * <li>2.Record details are put in bean aoCBEquipmentBean</li>
	 * </ul>
	 * <ul>
	 * <li>Calls query 'editEquipmentAmendDetails'</li>
	 * <li>Calls query 'editInsertEquipmentAmendDetails'</li>
	 * </ul>
	 * @param aoMyBatisSession Sql session object
	 * @param aoCBEquipmentBean CBEquipmentBean object
	 * @return Boolean boolean status for insert success/failure
	 * @throws ApplicationException ApplicationException object
	 */
	public Boolean editAmendmentOTPSEquipment(SqlSession aoMyBatisSession, CBEquipmentBean aoCBEquipmentBean)
			throws ApplicationException
	{
		boolean lbEditStatus = false;
		boolean lbAmendUnitsValid = false;
		boolean lbEditAmtValid = false;
		boolean lbError = false;
		String lsErrorMsgConstant = HHSConstants.EMPTY_STRING;
		try
		{
			lbAmendUnitsValid = validateAmendmentUnitForEquipment(aoMyBatisSession, aoCBEquipmentBean);
			lbEditAmtValid = validateAmendmentAmountForEquipment(aoMyBatisSession, aoCBEquipmentBean);
			if (lbAmendUnitsValid && lbEditAmtValid)
			{
				Integer loUpdateRows = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBEquipmentBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.EDIT_EQUIPMENT_AMEND_DETAILS, HHSConstants.MODEL_CB_EQUIPMENT_BEAN);

				if (null != loUpdateRows && loUpdateRows == HHSConstants.INT_ZERO)
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoCBEquipmentBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
							HHSConstants.EDIT_INSERT_EQUIPMENT_AMEND_DETAILS, HHSConstants.MODEL_CB_EQUIPMENT_BEAN);
				}
				lbEditStatus = true;
			}
			else
			{
				lbError = true;
				if (!lbAmendUnitsValid)
				{
					lsErrorMsgConstant = HHSConstants.BUDGET_MODIFICATION_RATE_UNIT_VALIDATION;
				}
				else
				{
					lsErrorMsgConstant = HHSConstants.CBM_LESS_THAN_INVOICE_INCLUDING_PENDING_NEG_AMEND_ERROR_MESSAGE;
				}
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						lsErrorMsgConstant));
			}
			setMoState("ContractBudgetAmendmentService: editAmendmentOTPSEquipment() passed");
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
				LOG_OBJECT.Error("App Exception occured in ContractBudgetAmendmentService: "
						+ "editAmendmentOTPSEquipment method:: ", aoAppEx);
				setMoState("Transaction Failed::App Exception in ContractBudgetAmendmentService: "
						+ "editAmendmentOTPSEquipment() method aoCBEquipmentBean::" + aoCBEquipmentBean + "\n");
			}
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetAmendmentService: editAmendmentOTPSEquipment method:: ", aoEx);
			loAppEx.addContextData(HHSConstants.CB_EQUIP_BEAN, CommonUtil.convertBeanToString(aoCBEquipmentBean));
			LOG_OBJECT.Error(
					"Exception occured in ContractBudgetAmendmentService: editAmendmentOTPSEquipment method:: ",
					loAppEx);
			setMoState("Transaction Failed::Exception in ContractBudgetAmendmentService: "
					+ "editAmendmentOTPSEquipment() method aoCBEquipmentBean::" + aoCBEquipmentBean + "\n");
			throw loAppEx;
		}
		return lbEditStatus;
	}

	/**
	 * This method validates the Base Unit count for a particular line-item and
	 * returns FALSE if the #units user is trying to update tries to make the
	 * total #units for a line-item to fall below zero and also taking in to
	 * consideration ? Negative Amendment units entered for the Equipment line
	 * item in other pending Amendments for which no budgets have been approved,
	 * else it validates it as TRUE
	 * <ul>
	 * <li>Calls query 'getBaseUnitForEquipment'</li>
	 * <li>Calls query 'getPendingNegAmendmentUnitsEqp'</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession object
	 * @param aoCBEquipmentBean CBEquipmentBean object
	 * @return Boolean loModUnitIsValid valid/invalid
	 * @throws ApplicationException ApplicationException object
	 * 
	 * 
	 */
	private Boolean validateAmendmentUnitForEquipment(SqlSession aoMybatisSession, CBEquipmentBean aoCBEquipmentBean)
			throws ApplicationException
	{
		Boolean loModUnitIsValid = true;
		try
		{
			if (new BigDecimal(aoCBEquipmentBean.getUnits()).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO)

			{
				Integer loBaseUnit = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoCBEquipmentBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBM_GET_BASE_UNIT_EQUIPMENT, HHSConstants.MODEL_CB_EQUIPMENT_BEAN);

				Integer loPendingNegAmendmentUnits = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoCBEquipmentBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.GET_PENDING_NEGATIVE_AMENDMENT_UNITS_EQP, HHSConstants.MODEL_CB_EQUIPMENT_BEAN);

				loBaseUnit = ((loBaseUnit == null) ? HHSConstants.INT_ZERO : loBaseUnit);
				if (loBaseUnit + loPendingNegAmendmentUnits + Integer.parseInt((aoCBEquipmentBean.getUnits())) < HHSConstants.INT_ZERO)
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
			LOG_OBJECT.Error("Exception while validating amended units for Equipment", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService: validateAmendmentUnitForEquipment method"
					+ " \n");
			throw loAppEx;
		}
		return loModUnitIsValid;
	}

	/**
	 * This method validates the amendment amount in Equipment for a particular
	 * line-item and returns FALSE if the #amount user is trying to update tries
	 * to make the total amount for a line-item to fall below zero and also
	 * taking in to consideration ? Negative Amendment amounts entered for the
	 * Equipment line item in other pending Amendments for which no budgets have
	 * been approved, else it validates it as TRUE
	 * <ul>
	 * <li>Calls query 'fetchEquipmentDetailsForValidationInMultipleAmendments'</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession object
	 * @param aoCBEquipmentBean CBEquipmentBean object
	 * @return Boolean loEditAmtValid valid/invalid
	 * @throws Exception Exception object
	 */
	private boolean validateAmendmentAmountForEquipment(SqlSession aoMybatisSession, CBEquipmentBean aoCBEquipmentBean)
			throws Exception
	{
		Boolean loEditAmtValid = false;
		CBEquipmentBean loCBEquipmentBean = null;
		loCBEquipmentBean = (CBEquipmentBean) DAOUtil.masterDAO(aoMybatisSession, aoCBEquipmentBean,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
				HHSConstants.CBM_FETCH_EQUIPMENT_DETAILS_FOR_VALIDATION_IN_MULTIPLE_AMENDMENTS,
				HHSConstants.MODEL_CB_EQUIPMENT_BEAN);

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

		// check sum of amendment amount and remaining amount should be greater
		// than zero
		if ((new BigDecimal(loCBEquipmentBean.getRemainingAmt()).add(new BigDecimal(aoCBEquipmentBean.getAmendAmt())))
				.compareTo(BigDecimal.ZERO) >= HHSConstants.INT_ZERO)

		{
			loEditAmtValid = true;
		}
		return loEditAmtValid;
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
	 * </ul>
	 * <ul>
	 * <li>Calls query 'delEquipmentModificationDetails'</li>
	 * </ul>
	 * @param aoMyBatisSession Sql session object
	 * @param aoCBEquipmentBean CBEquipmentBean object
	 * @return Boolean boolean status for insert success/failure
	 * @throws ApplicationException ApplicationException object
	 */
	public Boolean delAmendmentOTPSEquipment(SqlSession aoMyBatisSession, CBEquipmentBean aoCBEquipmentBean)
			throws ApplicationException
	{
		boolean lbEditStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoCBEquipmentBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.DEL_EQUIPMENT_MODIFICATION_DETAILS, HHSConstants.MODEL_CB_EQUIPMENT_BEAN);
			lbEditStatus = true;
			setMoState("ContractBudgetAmendmentService: delAmendmentOTPSEquipment() passed");
		}
		// Exception occur if database is down or getting null in required
		// parameter
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.CB_OPERATION_SUPPORT_BEAN,
					CommonUtil.convertBeanToString(aoCBEquipmentBean));
			LOG_OBJECT.Error("App Exception occured in ContractBudgetAmendmentService: delAmendmentOTPSEquipment "
					+ "method:: ", aoAppEx);
			setMoState("Transaction Failed::App Exception in ContractBudgetAmendmentService: "
					+ "delAmendmentOTPSEquipment() method aoCBOperationSupportBean::" + aoCBEquipmentBean + "\n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetAmendmentService: delAmendmentOTPSEquipment method:: ", aoEx);
			loAppEx.addContextData(HHSConstants.CB_OPERATION_SUPPORT_BEAN,
					CommonUtil.convertBeanToString(aoCBEquipmentBean));
			LOG_OBJECT
					.Error("Exception occured in ContractBudgetAmendmentService: delAmendmentOTPSEquipment method:: ",
							loAppEx);
			setMoState("Transaction Failed::Exception in ContractBudgetAmendmentService: "
					+ "delAmendmentOTPSEquipment() method aoCBOperationSupportBean::" + aoCBEquipmentBean + "\n");
			throw loAppEx;
		}
		return lbEditStatus;
	}

	// S349 otps amendment end

	/**
	 * Release 3.6.0 Enhancement id 6484
	 * <p>
	 * This method fetches Proposal Site Details corresponding to a proposal Id
	 * and user type
	 * <ul>
	 * <li>1. Add multiple input parameters to a Map</li>
	 * <li>2. Fetch list of Proposal Site Details for the provided Proposal Id,
	 * user type using <b>fetchProposalSiteDetails</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProposalId - Proposal Id
	 * @param asUserType - User Type
	 * @return loSiteDetailList - list of site details
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<SiteDetailsBean> fetchSubBudgetSiteDetails(SqlSession aoMybatisSession, CBGridBean aoCBGridBean,
			MasterBean aoMasterBeanObj, Boolean aoRecordBeforeRelease) throws ApplicationException
	{
		List<SiteDetailsBean> loSiteDetailList = null;
		try
		{
			if (!aoRecordBeforeRelease)
			{
				if (aoCBGridBean != null && aoCBGridBean.getContractBudgetID() != null)
				{
					if (aoMasterBeanObj != null
							&& aoCBGridBean.getBudgetStatusId().equals(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_BUDGET_APPROVED)))
					{
						String lsSubBudgetId = aoCBGridBean.getSubBudgetID();
						loSiteDetailList = fetchSiteDetailsListFromXML(lsSubBudgetId, aoMasterBeanObj);
					}
					else
					{
						Map<String, String> loMap = new HashMap<String, String>();
						loMap.put(HHSConstants.SUBBUDGET_ID_KEY, aoCBGridBean.getSubBudgetID());
						loMap.put(HHSConstants.BUDGET_ID_KEY, aoCBGridBean.getContractBudgetID());
						loSiteDetailList = (List<SiteDetailsBean>) DAOUtil.masterDAO(aoMybatisSession, loMap,
								HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_SUB_BUDGET_SITE_DETAILS,
								HHSConstants.JAVA_UTIL_MAP);
					}
				}
				else
				{
					throw new ApplicationException(
							"Proposal Id cannot be null while fetching the proposal site details");
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
			setMoState("Error while fetching proposal site details for user Type:" + aoCBGridBean.getContractBudgetID());
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error while fetching proposal site details for user Type:" + aoCBGridBean.getContractBudgetID());
			throw new ApplicationException("Error while fetching proposal site details for user Type:", loExp);
		}
		setMoState("Successfully fetched proposal site details for user Type:" + aoCBGridBean.getContractBudgetID());
		return loSiteDetailList;
	}

	/**
	 * This method fetch approved amendment Professional service details list
	 * from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List<CBProfessionalServicesBean>
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private List<SiteDetailsBean> fetchSiteDetailsListFromXML(String asSubBudgetId, MasterBean aoMasterBean)
			throws ApplicationException
	{
		List<LineItemMasterBean> loMasterBeanList = null;
		List<SiteDetailsBean> loReturnedList = null;

		loMasterBeanList = aoMasterBean.getMasterBeanList();
		Iterator<LineItemMasterBean> aoListIterator = loMasterBeanList.iterator();
		while (aoListIterator.hasNext())
		{
			LineItemMasterBean loLineItemBean = aoListIterator.next();
			if (loLineItemBean.getSubbudgetId().equals(asSubBudgetId))
			{
				loReturnedList = loLineItemBean.getSiteDetailsBeanList();
			}
		}
		return loReturnedList;
	}

	/**
	 * The Method will fetch the budget details and amendment details of
	 * Salaried Employees grid of Personnel Services tab under contract budget
	 * amendment module
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @param aoMasterBean MasterBean
	 * @return loSalariedEmployess List of PersonnelServiceBudget bean
	 * @throws ApplicationException - ApplicationException object
	 * 
	 */

	public List<PersonnelServiceBudget> fetchSalariedEmployeeBudgetForAmendment(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget, MasterBean aoMasterBean) throws ApplicationException
	{

		List<PersonnelServiceBudget> loSalariedEmployess = null;
		ContractBudgetService loCBService = new ContractBudgetService();
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);

		try
		{
			// Fetch the data from FileNet XML if Budget Status is approved
			if (aoMasterBean != null && aoPersonnelServiceBudget.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoPersonnelServiceBudget.getSubBudgetID();
				loSalariedEmployess = fetchSalariedEmployeeFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{
				// Fetching list of records for amendment budget
				List<PersonnelServiceBudget> loSalariedEmployessForAmendment = fetchSalariedEmployeeForAmendment(
						aoMybatisSession, aoPersonnelServiceBudget);

				// Fetching list of records for base budget
				CBGridBean loCBGridBeanObj = getCBGridBeanForBaseBudget(aoPersonnelServiceBudget);

				loSalariedEmployess = loCBService.fetchSalariedEmployeeForBase(aoMybatisSession, loCBGridBeanObj);

				setAmendmentBudgetDetailsinBean(loSalariedEmployessForAmendment, loSalariedEmployess);
			}

		}
		// catch any application exception thrown from the code due to SELECT
		// statement and throw it
		// forward
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData(
					"ApplicationException occured while fetching Salaried Employee budget :  fetchSalariedEmployeeBudgetForAmendment",
					aoExp);
			LOG_OBJECT
					.Error("ApplicationException occured while fetching Salaried Employee budget : fetchSalariedEmployeeBudgetForAmendment "
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
					"Exception occured while fetching Salaried Emplyee budget :  fetchSalariedEmployeeBudgetForAmendment ",
					aoExp);
			loAppEx.addContextData(
					"Exception occured while fetching Salaried Emplyee budget :  fetchSalariedEmployeeBudgetForAmendment",
					aoExp);
			LOG_OBJECT
					.Error("Exception occured while fetching Salaried Employee budget : fetchSalariedEmployeeBudgetForAmendment "
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
	 * 
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
	 * The Method will fetch the budget details of Hourly Employees grid of
	 * Personnel Services tab under contract budget module
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @param aoMasterBean MasterBean
	 * @return loSalariedEmployess List of PersonnelServiceBudget bean
	 * @throws ApplicationException - ApplicationException object
	 * 
	 */

	public List<PersonnelServiceBudget> fetchHourlyEmployeeBudgetForAmendment(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget, MasterBean aoMasterBean) throws ApplicationException
	{
		List<PersonnelServiceBudget> loSalariedEmployess = null;
		ContractBudgetService loCBService = new ContractBudgetService();
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);

		try
		{
			// Fetch the data from FileNet XML if Budget Status is approved
			if (aoMasterBean != null && aoPersonnelServiceBudget.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoPersonnelServiceBudget.getSubBudgetID();
				loSalariedEmployess = fetchHourlyEmployeeFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{
				// Fetching list of records for amendment budget
				List<PersonnelServiceBudget> loSalariedEmployessForAmendment = fetchHourlyEmployeeForAmendment(
						aoMybatisSession, aoPersonnelServiceBudget);

				// Fetching list of records for base budget
				CBGridBean loCBGridBeanObj = getCBGridBeanForBaseBudget(aoPersonnelServiceBudget);

				loSalariedEmployess = loCBService.fetchHourlyEmployeeForBase(aoMybatisSession, loCBGridBeanObj);

				setAmendmentBudgetDetailsinBean(loSalariedEmployessForAmendment, loSalariedEmployess);
			}

		}
		// catch any application exception thrown from the code due to SELECT
		// statement and throw it
		// forward
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData(
					"ApplicationException occured while fetching Hourly Employee budget :  fetchHourlyEmployeeBudgetForAmendment",
					aoExp);
			LOG_OBJECT
					.Error("ApplicationException occured while fetching Hourly Employee budget : fetchHourlyEmployeeBudgetForAmendment "
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
					"Exception occured while fetching Hourly Emplyee budget :  fetchHourlyEmployeeBudgetForAmendment ",
					aoExp);
			loAppEx.addContextData(
					"Exception occured while fetching Hourly Emplyee budget :  fetchHourlyEmployeeBudgetForAmendment",
					aoExp);
			LOG_OBJECT
					.Error("Exception occured while fetching Hourly Employee budget : fetchHourlyEmployeeBudgetForAmendment "
							+ aoExp);
			setMoState("Exception occured while fetching Hourly Employee budget for budget id = "
					+ aoPersonnelServiceBudget.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudget.getSubBudgetID());
			throw loAppEx;
		}
		return loSalariedEmployess;
	}

	/**
	 * The Method will fetch the budget details of Seasonal Employees grid of
	 * Personnel Services tab under contract budget module
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @param aoMasterBean MasterBean
	 * @return loSalariedEmployess List of PersonnelServiceBudget bean
	 * @throws ApplicationException - ApplicationException object
	 * 
	 */

	public List<PersonnelServiceBudget> fetchSeasonalEmployeeBudgetForAmendment(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget, MasterBean aoMasterBean) throws ApplicationException
	{

		List<PersonnelServiceBudget> loSalariedEmployess = null;
		ContractBudgetService loCBService = new ContractBudgetService();
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);

		try
		{
			// Fetch the data from FileNet XML if Budget Status is approved
			if (aoMasterBean != null && aoPersonnelServiceBudget.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoPersonnelServiceBudget.getSubBudgetID();
				loSalariedEmployess = fetchSeasonalEmployeeFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{
				// Fetching list of records for amendment budget
				List<PersonnelServiceBudget> loSalariedEmployessForAmendment = fetchSeasonalEmployeeForAmendment(
						aoMybatisSession, aoPersonnelServiceBudget);

				// Fetching list of records for base budget
				CBGridBean loCBGridBeanObj = getCBGridBeanForBaseBudget(aoPersonnelServiceBudget);

				loSalariedEmployess = loCBService.fetchSeasonalEmployeeForBase(aoMybatisSession, loCBGridBeanObj);

				setAmendmentBudgetDetailsinBean(loSalariedEmployessForAmendment, loSalariedEmployess);
			}
		}
		// catch any application exception thrown from the code due to SELECT
		// statement and throw it
		// forward
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData(
					"ApplicationException occured while fetching Seasonal Employee budget :  fetchSeasonalEmployeeBudgetForAmendment",
					aoExp);
			LOG_OBJECT
					.Error("ApplicationException occured while fetching Seasonal Employee budget : fetchSeasonalEmployeeBudgetForAmendment "
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
					"Exception occured while fetching Seasonal Emplyee budget :  fetchSeasonalEmployeeBudgetForAmendment ",
					aoExp);
			loAppEx.addContextData(
					"Exception occured while fetching Seasonal Emplyee budget :  fetchSeasonalEmployeeBudgetForAmendment",
					aoExp);
			LOG_OBJECT
					.Error("Exception occured while fetching Seasonal Employee budget : fetchSeasonalEmployeeBudgetForAmendment "
							+ aoExp);
			setMoState("Exception occured while fetching Seasonal Employee budget for budget id = "
					+ aoPersonnelServiceBudget.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudget.getSubBudgetID());
			throw loAppEx;
		}
		return loSalariedEmployess;
	}

	/**
	 * The Method will fetch the fringe benefit details of Personnel Services
	 * tab under contract budget module
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @param aoMasterBean MasterBean
	 * @return loSalariedEmployess List of PersonnelServiceBudget bean
	 * @throws ApplicationException - ApplicationException object
	 * 
	 */

	public List<PersonnelServiceBudget> fetchFringeBenefitsForAmendment(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget, MasterBean aoMasterBean) throws ApplicationException
	{

		List<PersonnelServiceBudget> loSalariedEmployess = new ArrayList<PersonnelServiceBudget>();
		ContractBudgetService loCBService = new ContractBudgetService();
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);

		try
		{
			// Fetch the data from FileNet XML if Budget Status is approved
			if (aoMasterBean != null && aoPersonnelServiceBudget.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoPersonnelServiceBudget.getSubBudgetID();
				loSalariedEmployess = fetchFringeEmployeeFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{
				// Fetching list of records for amendment budget
				List<PersonnelServiceBudget> loSalariedEmployessForAmendment = fetchFringeBenefitsForAmendment(
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
				setAmendmentBudgetDetailsinBean(loSalariedEmployessForAmendment, loSalariedEmployess);
			}

		}
		// catch any application exception thrown from the code due to SELECT
		// statement and throw it
		// forward
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData(
					"ApplicationException occured while fetching fringe benefits budget :  fetchFringebenefitsForAmendment",
					aoExp);
			LOG_OBJECT
					.Error("ApplicationException occured while fetching fringe benefits budget : fetchFringebenefitsForAmendment "
							+ aoExp);
			setMoState("ApplicationException occured while fetching fringe benefits budget for budget id = "
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
					"Exception occured while fetching fringe benefits budget :  fetchFringebenefitsForAmendment ",
					aoExp);
			loAppEx.addContextData(
					"Exception occured while fetching fringe benefits budget :  fetchFringebenefitsForAmendment", aoExp);
			LOG_OBJECT
					.Error("Exception occured while fetching fringe benefits budget : fetchFringebenefitsForAmendment "
							+ aoExp);
			setMoState("Exception occured while fetching fringe benefits budget for budget id = "
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
	 * used is ContractBudgetMapper.xml.
	 * <ul>
	 * <li>Calls query 'insertPersonnelServicesAmendment'</li>
	 * </ul>
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget bean
	 * @return lbInsertStatus Insert status
	 * @throws ApplicationException - ApplicationException object
	 */

	public Boolean addEmployeeBudgetForAmendment(SqlSession aoMybatisSession,
			PersonnelServiceBudget aoPersonnelServiceBudgetBean) throws ApplicationException
	{

		boolean lbInsertStatus = false;
		boolean lbUnitError = false;
		boolean lbAmountError = false;
		boolean lbError = false;
		try
		{
			if (aoPersonnelServiceBudgetBean.getAmendmentType().equalsIgnoreCase(HHSConstants.POSITIVE)
					&& (new BigDecimal(aoPersonnelServiceBudgetBean.getAmendmentAmount()).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO))
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.POSITIVE_AMENDMENT_MSG));
			}
			// Validation for negative amendment (For Negative Amendment only)
			else if (aoPersonnelServiceBudgetBean.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE)
					&& (new BigDecimal(aoPersonnelServiceBudgetBean.getAmendmentAmount()).compareTo(BigDecimal.ZERO)) > HHSConstants.INT_ZERO)
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.NEGATIVE_AMENDMENT_MSG));
			}

			lbUnitError = validateAmendmentUnitForPersonnelServices(aoMybatisSession, aoPersonnelServiceBudgetBean);

			if (!lbUnitError)
			{
				lbError = true;
				// Start : Updated in R6
				String lsMsg = fetchValidationMessage(aoPersonnelServiceBudgetBean.getTransactionName(),
						aoPersonnelServiceBudgetBean.getUsesFte());
				// End : Updated in R6
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						lsMsg));
			}

			lbAmountError = validateAmendmentAmountForPersonnelServices(aoMybatisSession, aoPersonnelServiceBudgetBean);

			if (!lbAmountError)
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CBM_LESS_THAN_INVOICE_INCLUDING_PENDING_FOR_NEG_AMEND));
			}
			// Start: Added for Defect-8478
			if (HHSConstants.ZERO.equals(aoPersonnelServiceBudgetBean.getUsesFte())
					&& aoPersonnelServiceBudgetBean.getId().equals(HHSConstants.NEW_ROW_IDENTIFIER)
					&& new BigDecimal(aoPersonnelServiceBudgetBean.getAmendmentUnit()).compareTo(BigDecimal.ZERO) <= HHSConstants.INT_ZERO)
			{
				lbError = true;
				setPositionErrorMessage();
			}
			// End: Added for Defect-8478
			String lsTransactionName = aoPersonnelServiceBudgetBean.getTransactionName();
			setEmployeeTypeInBean(aoPersonnelServiceBudgetBean, lsTransactionName);

			DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
					HHSConstants.CBY_INSERT_PERSONNEL_SERVICES_AMENDMENT, HHSConstants.PERSONNEL_SERVICE_BUDGET);
			lbInsertStatus = true;

		}
		// catch any application exception thrown from the code due to INSERT
		// statement failure and throw it
		// forward
		catch (ApplicationException aoExp)
		{
			if ((lbError))
			{
				aoExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoExp.toString());
			}
			else
			{
				aoExp.addContextData(
						"ApplicationException occured while inserting Emplyee budget :  addEmployeeBudgetForAmendment",
						aoExp);
				LOG_OBJECT
						.Error("ApplicationException occured while inserting Emplyee budget : addEmployeeBudgetForAmendment "
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
					"Exception occured while inserting Emplyee budget :  addEmployeeBudgetForAmendment ", aoExp);
			loAppEx.addContextData("Exception occured while inserting Emplyee budget :  addEmployeeBudgetForAmendment",
					aoExp);
			LOG_OBJECT.Error("Exception occured while inserting Emplyee budget : addEmployeeBudgetForAmendment "
					+ aoExp);
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

	public Boolean editEmployeeBudgetForAmendment(SqlSession aoMybatisSession,
			PersonnelServiceBudget aoPersonnelServiceBudgetBean) throws ApplicationException
	{
		boolean lbEditStatus = false;
		boolean lbUnitError = false;
		boolean lbAmountError = false;
		boolean lbError = false;

		try
		{
			// Start: Added for Defect-8478
			if (HHSConstants.ZERO.equals(aoPersonnelServiceBudgetBean.getUsesFte())
					&& aoPersonnelServiceBudgetBean.getNewRecord().endsWith(HHSConstants.NEW_RECORD)
					&& new BigDecimal(aoPersonnelServiceBudgetBean.getAmendmentUnit()).compareTo(BigDecimal.ZERO) <= HHSConstants.INT_ZERO)
			{
				lbError = true;
				setPositionErrorMessage();
			}
			// End: Added for Defect-8478
			if (aoPersonnelServiceBudgetBean.getAmendmentType().equalsIgnoreCase(HHSConstants.POSITIVE)
					&& (new BigDecimal(aoPersonnelServiceBudgetBean.getAmendmentAmount()).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO))
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.POSITIVE_AMENDMENT_MSG));
			}
			// Validation for negative amendment (For Negative Amendment only)
			else if (aoPersonnelServiceBudgetBean.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE)
					&& (new BigDecimal(aoPersonnelServiceBudgetBean.getAmendmentAmount()).compareTo(BigDecimal.ZERO) > HHSConstants.INT_ZERO))
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.NEGATIVE_AMENDMENT_MSG));
			}

			lbUnitError = validateAmendmentUnitForPersonnelServices(aoMybatisSession, aoPersonnelServiceBudgetBean);

			if (!lbUnitError)
			{
				lbError = true;
				// Start : Updated in R6
				String lsMsg = fetchValidationMessage(aoPersonnelServiceBudgetBean.getTransactionName(),
						aoPersonnelServiceBudgetBean.getUsesFte());
				// End : Updated in R6
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						lsMsg));
			}

			lbAmountError = validateAmendmentAmountForPersonnelServices(aoMybatisSession, aoPersonnelServiceBudgetBean);

			if (!lbAmountError)
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CBM_LESS_THAN_INVOICE_INCLUDING_PENDING_NEG_AMEND_ERROR_MESSAGE));
			}

			lbEditStatus = updateInsertPersonnelServices(aoMybatisSession, aoPersonnelServiceBudgetBean);

		}
		// catch any application exception thrown from the code due to
		// UPDATE/INSERT statement failure and throw it forward
		catch (ApplicationException aoExp)
		{
			if (lbError)
			{
				aoExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoExp.toString());
			}
			else
			{
				aoExp.addContextData(
						"ApplicationException occured while editing Emplyee budget :  editEmployeeBudgetForAmendment",
						aoExp);
				LOG_OBJECT
						.Error("ApplicationException occured while editing Emplyee budget : editEmployeeBudgetForAmendment "
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
					"Exception occured while editing Emplyee budget :  editEmployeeBudgetForAmendment ", aoExp);
			loAppEx.addContextData("Exception occured while editing Emplyee budget :  editEmployeeBudgetForAmendment",
					aoExp);
			LOG_OBJECT
					.Error("Exception occured while editing Emplyee budget : editEmployeeBudgetForAmendment " + aoExp);
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
	private void setPositionErrorMessage() throws ApplicationException
	{
		String lsMessage = MessageFormat.format(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
				HHSR5Constants.POSITION_NEW_ROW_ZERO), HHSR5Constants.BUDGET_AMENDMENT);
		throw new ApplicationException(lsMessage);
	}

	/**
	 * The Method will first try to Update the personnel row and if update count
	 * is zero, it insert new personnel service row.
	 * <ul>
	 * <li>Calls query 'insertFirstPersonnelServicesForAmendment'</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession object
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget object
	 * @return lbEditStatus boolean
	 * @throws ApplicationException ApplicationException object
	 */
	private boolean updateInsertPersonnelServices(SqlSession aoMybatisSession,
			PersonnelServiceBudget aoPersonnelServiceBudgetBean) throws ApplicationException
	{
		boolean lbEditStatus = false;
		// Update personal service data
		Integer loNoOfUpdateRows = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
				HHSConstants.CBY_UPDATE_PERSONNEL_SERVICES_FOR_AMENDMENT, HHSConstants.PERSONNEL_SERVICE_BUDGET);
		if (loNoOfUpdateRows <= HHSConstants.INT_ZERO)
		{
			// Insert data for personal service
			DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
					HHSConstants.CBY_INSERT_FIRST_PERSONNEL_SERVICES_FOR_AMENDMENT,
					HHSConstants.PERSONNEL_SERVICE_BUDGET);
		}
		lbEditStatus = true;
		return lbEditStatus;
	}

	/**
	 * The Method will edit the fringe benefit details of Personnel Services tab
	 * under contract budget module
	 * 
	 * This method calls the query 'insertFirstFringeBenefitsForAmendment' <li>
	 * Method Updated in R4</li>
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget
	 * @return lbEditStatus Edit status
	 * @throws ApplicationException - ApplicationException object
	 * 
	 * 
	 */

	public Boolean editFringeBenefitsForAmendment(SqlSession aoMybatisSession,
			PersonnelServiceBudget aoPersonnelServiceBudgetBean) throws ApplicationException
	{
		boolean lbEditStatus = false;
		Integer loFringeCount = HHSConstants.INT_ZERO;
		boolean lbAmountError = true;
		boolean lbError = false;
		try
		{
			if (aoPersonnelServiceBudgetBean.getAmendmentType().equalsIgnoreCase(HHSConstants.POSITIVE)
					&& (new BigDecimal(aoPersonnelServiceBudgetBean.getAmendmentAmount()).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO))
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.POSITIVE_AMENDMENT_MSG));
			}
			// Validation for negative amendment (For Negative Amendment only)
			else if (aoPersonnelServiceBudgetBean.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE)
					&& (new BigDecimal(aoPersonnelServiceBudgetBean.getAmendmentAmount()).compareTo(BigDecimal.ZERO) > HHSConstants.INT_ZERO))
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.NEGATIVE_AMENDMENT_MSG));
			}

			// If the amendment amount is less than 0, then only do the
			// validation
			if (new BigDecimal(aoPersonnelServiceBudgetBean.getAmendmentAmount()).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO)
			{
				lbAmountError = validateAmendmentAmountForFringe(aoMybatisSession, aoPersonnelServiceBudgetBean);
			}

			if (!lbAmountError)
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CBM_LESS_THAN_INVOICE_INCLUDING_PENDING_NEG_AMEND_ERROR_MESSAGE));
			}
			else
			{
				loFringeCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.CBY_UPDATE_FRINGE_BENIFITS_FOR_AMENDMENT, HHSConstants.PERSONNEL_SERVICE_BUDGET);
				if (loFringeCount <= HHSConstants.INT_ZERO)
				{
					DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
							HHSConstants.CBY_INSERT_FIRST_FRINGE_BENEFITS_FOR_AMENDMENT,
							HHSConstants.PERSONNEL_SERVICE_BUDGET);
				}
				lbEditStatus = true;
			}
		}
		// catch any application exception thrown from the code due to
		// UPDATE/INSERT statement failure and throw it
		// forward
		catch (ApplicationException aoExp)
		{
			if (lbError)
			{
				aoExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoExp.toString());
			}
			else
			{
				aoExp.addContextData(
						"ApplicationException occured while editing Fringe benefits:  editFringebenefitsForAmendment",
						aoExp);
				LOG_OBJECT
						.Error("ApplicationException occured while editing Fringe benefits : editFringebenefitsForAmendment "
								+ aoExp);
				setMoState("ApplicationException occured while editing Fringe benefits for budget id = "
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
					"Exception occured while editing Fringe benefits :  editFringebenefitsForAmendment ", aoExp);
			loAppEx.addContextData("Exception occured while editing Fringe benefits :  editFringebenefitsForAmendment",
					aoExp);
			LOG_OBJECT.Error("Exception occured while editing Fringe benefits : editFringebenefitsForAmendment "
					+ aoExp);
			setMoState("Exception occured while editing Fringe benefits for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loAppEx;
		}
		return lbEditStatus;
	}

	/**
	 * This method is used to fetch amendment details of salaried employee
	 * 
	 * <ul>
	 * <li>Calls query 'fetchSalriedEmployeeForAmendment'</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @return loSalariedEmployessForAmendment List<PersonnelServiceBudget>
	 * @throws Exception object
	 */

	@SuppressWarnings("unchecked")
	private List<PersonnelServiceBudget> fetchSalariedEmployeeForAmendment(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget) throws Exception
	{
		List<PersonnelServiceBudget> loSalariedEmployessForAmendment = (List<PersonnelServiceBudget>) DAOUtil
				.masterDAO(aoMybatisSession, aoPersonnelServiceBudget,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.CBY_FETCH_SALRIED_EMPLOYEE_FOR_AMENDMENT,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

		return loSalariedEmployessForAmendment;

	}

	/**
	 * This method is used to fetch amendment details of hourly employee
	 * 
	 * <ul>
	 * <li>Calls query 'fetchHourlyEmployeeForAmendment'</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @return loHourlyEmployessForAmendment List<PersonnelServiceBudget>
	 * @throws Exception object
	 */

	@SuppressWarnings("unchecked")
	private List<PersonnelServiceBudget> fetchHourlyEmployeeForAmendment(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget) throws Exception
	{
		List<PersonnelServiceBudget> loHourlyEmployessForAmendment = (List<PersonnelServiceBudget>) DAOUtil.masterDAO(
				aoMybatisSession, aoPersonnelServiceBudget, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
				HHSConstants.CBY_FETCH_HOURLY_EMPLOYEE_FOR_AMENDMENT, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

		return loHourlyEmployessForAmendment;

	}

	/**
	 * This method is used to fetch amendment details of seasonal employee
	 * 
	 * <ul>
	 * <li>Calls query 'fetchSeasonalEmployeeForAmendment'</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @return loSeasonalEmployessForAmendment List<PersonnelServiceBudget>
	 * @throws Exception object
	 */

	@SuppressWarnings("unchecked")
	private List<PersonnelServiceBudget> fetchSeasonalEmployeeForAmendment(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget) throws Exception
	{
		List<PersonnelServiceBudget> loSeasonalEmployessForAmendment = (List<PersonnelServiceBudget>) DAOUtil
				.masterDAO(aoMybatisSession, aoPersonnelServiceBudget,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.CBY_FETCH_SEASONAL_EMPLOYEE_FOR_AMENDMENT,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

		return loSeasonalEmployessForAmendment;

	}

	/**
	 * This method is used to fetch amendment details of Fringe Benefits
	 * 
	 * <ul>
	 * <li>Calls query 'fetchFringeBenefitsForAmendment'</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @return loFringeBenefitsForAmendment List<PersonnelServiceBudget>
	 * @throws Exception object
	 */

	@SuppressWarnings("unchecked")
	private List<PersonnelServiceBudget> fetchFringeBenefitsForAmendment(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget) throws Exception
	{
		List<PersonnelServiceBudget> loFringeBenefitsForAmendment = (List<PersonnelServiceBudget>) DAOUtil.masterDAO(
				aoMybatisSession, aoPersonnelServiceBudget, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
				HHSConstants.CBY_FETCH_FRINGE_BENEFITS_FOR_AMENDMENT, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

		return loFringeBenefitsForAmendment;

	}

	/**
	 * This method is used to set amendment details (units, amount) in
	 * SalariedEmployeesList <li>1. For each record of aoAmendmentList, if
	 * parentid equalls to id of any record of aoSalariedEmployessList,
	 * amendment unit and amendment amount of first list will be set to second
	 * list.
	 * 
	 * @param aoAmendmentList CBGridBean
	 * @param aoSalariedEmployessList CBGridBean
	 * @throws Exception object
	 */
	private void setAmendmentBudgetDetailsinBean(List<PersonnelServiceBudget> aoAmendmentList,
			List<PersonnelServiceBudget> aoSalariedEmployessList) throws Exception
	{
		if (aoAmendmentList != null && !aoAmendmentList.isEmpty())
		{
			for (PersonnelServiceBudget loPsBase : aoSalariedEmployessList)
			{
				for (PersonnelServiceBudget loPsAmendment : aoAmendmentList)
				{
					if (loPsBase.getId().equals(loPsAmendment.getParentId()))
					{
						loPsBase.setAmendmentUnit(loPsAmendment.getAmendmentUnit());
						loPsBase.setAmendmentAmount(loPsAmendment.getAmendmentAmount());
						break;
					}

				}
			}

			for (PersonnelServiceBudget loPsAmendment : aoAmendmentList)
			{
				if (loPsAmendment.getId().equals(loPsAmendment.getParentId()))
				{
					loPsAmendment.setId(loPsAmendment.getId() + HHSConstants.NEW_RECORD);
					aoSalariedEmployessList.add(HHSConstants.INT_ZERO, loPsAmendment);

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
		if (asTransactionName.equals(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_ADD))
		{
			aoPersonnelServiceBudgetBean.setEmpType(HHSConstants.ONE);
		}
		else if (asTransactionName.equals(HHSConstants.CBY_AMEND_HOURLY_EMPLOYEE_GRID_ADD))
		{
			aoPersonnelServiceBudgetBean.setEmpType(HHSConstants.TWO);
		}
		else if (asTransactionName.equals(HHSConstants.CBY_AMEND_SEASONAL_EMPLOYEE_GRID_ADD))
		{
			aoPersonnelServiceBudgetBean.setEmpType(HHSConstants.THREE);
		}
	}

	/**
	 * This method updated for release 3.3.0 for defect 6444. This method
	 * validates the Base Unit count for a particular line-item and returns
	 * FALSE if the #units user is trying to update tries to make the total
	 * #units for a line-item to fall below zero, else it validates it as TRUE
	 * <ul>
	 * <li>Calls query 'getBaseUnitForPersonnelService'</li>
	 * <li>Calls query 'getPendingNegAmendmentUnitsPS'</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget
	 * @return loIsValid Boolean
	 * @throws ApplicationException ApplicationException object
	 */
	private Boolean validateAmendmentUnitForPersonnelServices(SqlSession aoMybatisSession,
			PersonnelServiceBudget aoPersonnelServiceBudgetBean) throws ApplicationException
	{
		Boolean loIsValid = true;
		try
		{
			// No validation for adding new row
			if (!aoPersonnelServiceBudgetBean.getId().equalsIgnoreCase(HHSConstants.NEW_ROW_IDENTIFIER)
					&& Double.parseDouble(aoPersonnelServiceBudgetBean.getAmendmentUnit()) < HHSConstants.INT_ZERO)
			{
				// Start of changes for release 3.3.0 for defect 6444
				// type casting to Double instead of Integer

				Double loPendingNegAmendmentUnits = (Double) DAOUtil.masterDAO(aoMybatisSession,
						aoPersonnelServiceBudgetBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.GET_PENDING_NEGATIVE_AMENDMENT_UNITS_PS, HHSConstants.PERSONNEL_SERVICE_BUDGET);

				loPendingNegAmendmentUnits = ((loPendingNegAmendmentUnits == null) ? HHSConstants.DOUBLE_DECIMAL_ZERO
						: loPendingNegAmendmentUnits);
				// End of changes for release 3.3.0 for defect 6444

				if (loPendingNegAmendmentUnits + Double.parseDouble((aoPersonnelServiceBudgetBean.getAmendmentUnit())) < HHSConstants.INT_ZERO)
				{
					loIsValid = false;
				}
			}
			else if (aoPersonnelServiceBudgetBean.getId().equalsIgnoreCase(HHSConstants.NEW_ROW_IDENTIFIER)
					&& Double.parseDouble(aoPersonnelServiceBudgetBean.getAmendmentUnit()) < HHSConstants.INT_ZERO)
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
			setMoState("Transaction Failed:: ContractBudgetService:validateAmendmentUnitForPersonnelServices method"
					+ " \n");
			throw loAppEx;
		}
		return loIsValid;
	}

	// Start : Updated in R6
	/**
	 * This method is modified in Release 6. The Parameter asExistingBudget is
	 * added to display the validation message for new screens. This method is
	 * used to set Error message on the basis of transaction type. on the basis
	 * of transaction name.
	 * 
	 * @param asTransactionName String as input
	 * @param asExistingBudget String as input - Added in R6
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
			if (asTransactionName.equals(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_EDIT)
					|| asTransactionName.equals(HHSConstants.CBY_AMEND_SALARIED_EMPLOYEE_GRID_ADD))
			{
				lsMsg = HHSConstants.BUDGET_MODIFICATION_PS_SAL_UNIT_VALIDATION;
			}
			else if (asTransactionName.equals(HHSConstants.CBY_AMEND_HOURLY_EMPLOYEE_GRID_EDIT)
					|| asTransactionName.equals(HHSConstants.CBY_AMEND_SEASONAL_EMPLOYEE_GRID_EDIT)
					|| asTransactionName.equals(HHSConstants.CBY_AMEND_HOURLY_EMPLOYEE_GRID_ADD)
					|| asTransactionName.equals(HHSConstants.CBY_AMEND_SEASONAL_EMPLOYEE_GRID_ADD))
			{
				lsMsg = HHSConstants.BUDGET_MODIFICATION_PS_HOURLY_UNIT_VALIDATION;
			}
		}
		return lsMsg;
	}

	// End : Updated in R6

	/**
	 * This method validates the amendment amount for a particular line-item and
	 * returns FALSE if the amendment amount user is trying to update makes the
	 * total proposed budget less than YTD invoice amount and also taking in to
	 * consideration ? Negative Amendment amounts entered for the Personnel
	 * Services line item in other pending Amendments for which no budgets have
	 * been approved, else it validates it as TRUE
	 * <ul>
	 * <li>Calls query 'fetchPSDetailsForValidationInMultipleAmendments'</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession object
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget object
	 * @return loIsValid Boolean
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	private Boolean validateAmendmentAmountForPersonnelServices(SqlSession aoMybatisSession,
			PersonnelServiceBudget aoPersonnelServiceBudgetBean) throws ApplicationException
	{
		Boolean loIsValid = true;
		String lsLocalRemainingAmt = null;
		try
		{
			if (!aoPersonnelServiceBudgetBean.getId().equalsIgnoreCase(HHSConstants.NEW_ROW_IDENTIFIER))
			{
				Map<Object, Object> loVal = (HashMap<Object, Object>) DAOUtil.masterDAO(aoMybatisSession,
						aoPersonnelServiceBudgetBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.CBM_FETCH_PS_DETAILS_FOR_VALIDATION_IN_MULTIPLE_AMENDMENT,
						HHSConstants.PERSONNEL_SERVICE_BUDGET);

				if (loVal == null)
				{
					throw new ApplicationException(
							"Exception occured in validateAmendmentAmountForPersonnelServices method while executing the fetchPSDetailsForValidation query");
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

				if (((new BigDecimal(lsLocalRemainingAmt).add(new BigDecimal(aoPersonnelServiceBudgetBean
						.getAmendmentAmount()))).compareTo(BigDecimal.ZERO)) < HHSConstants.INT_ZERO)
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
			else if (new BigDecimal(aoPersonnelServiceBudgetBean.getAmendmentAmount()).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO)
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
			setMoState("Transaction Failed:: ContractBudgetAmendmentService:validateAmendmentAmountForPersonnelServices method"
					+ " \n");
			throw loAppEx;
		}
		return loIsValid;
	}

	/**
	 * This method validates the amendment amount for a particular line-item and
	 * returns FALSE if the amendment amount user is trying to update makes the
	 * total proposed budget less than YTD invoice amount and also taking in to
	 * consideration ? Negative Amendment amounts entered for the fringes line
	 * item in other pending Amendments for which no budgets have been approved,
	 * else it validates it as TRUE for Fringe Benefits
	 * <ul>
	 * <li>Calls query 'fetchFringeAmountForValidationInMultipleAmendments'</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget
	 * @return loIsValid Boolean
	 * @throws ApplicationException ApplicationException object
	 */
	private Boolean validateAmendmentAmountForFringe(SqlSession aoMybatisSession,
			PersonnelServiceBudget aoPersonnelServiceBudgetBean) throws ApplicationException
	{
		Boolean loIsValid = true;

		try
		{
			BigDecimal loRemainingAmt = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
					HHSConstants.CBM_FETCH_FRINGE_AMOUNT_FOR_VALIDATION_IN_MULTIPLE_AMENDMENTS,
					HHSConstants.PERSONNEL_SERVICE_BUDGET);

			if (loRemainingAmt == null)
			{
				throw new ApplicationException(
						"Exception occured in validateAmendmentAmountForFringe method while executing the fetchPSDetailsForValidation query");
			}

			if ((loRemainingAmt.add(new BigDecimal(aoPersonnelServiceBudgetBean.getAmendmentAmount())))
					.compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO)
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
			setMoState("Transaction Failed:: ContractBudgetAmendmentService:validateAmendmentAmountForFringe method"
					+ " \n");
			throw loAppEx;
		}
		return loIsValid;
	}

	/**
	 * <p>
	 * This method is used for fetching values in Milestone grid for a
	 * particular sub-budget in Contract Budget Amendment.
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
		aoCBGridBeanObj.setEntryTypeId(HHSConstants.EIGHT);
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);
		try
		{
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBMileStoneBean = generateMilestoneList(lsSubBudgetId, aoMasterBean);
				
			}
			else
			{
				// Fetching list of records for amendment budget
				List<CBMileStoneBean> loCBMileStoneBeanForAmendment = fetchMilestoneForAmendment(aoMybatisSession,
						aoCBGridBeanObj);
				// Fetching list of records for base budget
				loCBMileStoneBean = fetchMilestoneForBase(aoMybatisSession, aoCBGridBeanObj);
				setAmendmentBudgetDetailsBean(loCBMileStoneBeanForAmendment, loCBMileStoneBean);
			}
		}
		// Handles Application Exceptions occurred in mapping
		catch (ApplicationException aoAppEx)
		{
			setMoState("error occured while fetching MileStone Details for budget type id "
					+ aoCBGridBeanObj.getBudgetTypeId());
			aoAppEx.addContextData("Exception occured while fetching Milestone Details ", aoAppEx);
			LOG_OBJECT.Error("error occured while fetching Milestone Details ", aoAppEx);
			throw aoAppEx;
		}
		// Handles Exceptions here
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured while fetching Milestone in ContractBudgetAmendmentService ", aoEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService: fetchMilestone method - failed to fetch"
					+ aoCBGridBeanObj.getBudgetTypeId() + " \n");
			throw new ApplicationException("Error occured while fetching the details for Milestone", aoEx);
		}
		
		return loCBMileStoneBean;
	}

	/**
	 * This method is used to fetch amendment line item details of milestone
	 * 
	 * <ul>
	 * <li>Calls query 'fetchMilestoneForAmendment'</li>
	 * </ul>
	 * @param aoMybatisSession - SqlSession
	 * @param aoCBGridBeanObj - CBGridBean
	 * @return loMilestoneForAmendment - List of CBMileStoneBean
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	private List<CBMileStoneBean> fetchMilestoneForAmendment(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj)
			throws ApplicationException
	{
		List<CBMileStoneBean> loMilestoneForAmendment = (List<CBMileStoneBean>) DAOUtil.masterDAO(aoMybatisSession,
				aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
				HHSConstants.FETCH_MILESTONE_FOR_AMENDMENT, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

		return loMilestoneForAmendment;
	}

	/**
	 * This method is used to fetch Base budget details of Milestone
	 * <ul>
	 * <li>Calls query 'fetchMilestoneBaseDetails'</li>
	 * </ul>
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
		List<CBMileStoneBean> loMilestoneForBase = (List<CBMileStoneBean>) DAOUtil.masterDAO(aoMybatisSession,
				aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
				HHSConstants.FETCH_MILESTONE_BASE_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

		return loMilestoneForBase;
	}

	/**
	 * This method is used to set amendment details (amount) in MilestoneList
	 * <li>1. For each record of aoAmendmentList, if ParentId equals to id of
	 * any record of aoMilestoneList, amendment amount of first list will be set
	 * to second list.
	 * 
	 * @param aoAmendmentList - List of CBMileStoneBean
	 * @param aoMilestoneList - List of CBMileStoneBean
	 * @throws ApplicationException - ApplicationException object
	 */
	private void setAmendmentBudgetDetailsBean(List<CBMileStoneBean> aoAmendmentList,
			List<CBMileStoneBean> aoMilestoneList) throws ApplicationException
	{		
		if (aoAmendmentList != null && !aoAmendmentList.isEmpty())
		{   
			for (CBMileStoneBean loMsBase : aoMilestoneList)
			{   
				for (CBMileStoneBean loMsAmendment : aoAmendmentList)
				{   
					if (loMsBase.getId().equals(loMsAmendment.getParentId()))
					{
						loMsBase.setModificationAmount(loMsAmendment.getModificationAmount());
						break;
					}
				}
			}
			for (CBMileStoneBean loMsAmendment : aoAmendmentList)
			{
				if (loMsAmendment.getId().equals(loMsAmendment.getParentId()))
				{
					loMsAmendment.setId(loMsAmendment.getId() + HHSConstants.NEW_RECORD);
					aoMilestoneList.add(HHSConstants.INT_ZERO, loMsAmendment);
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
	 * </ul>
	 * <ul>
	 * <li>Calls query 'getSeqForMilestone'</li>
	 * </ul>
	 * @param aoMybatisSession - SqlSession
	 * @return loCurrentSeq - Integer
	 * @throws ApplicationException - ApplicationException object
	 */
	public Integer getSeqForMilestone(SqlSession aoMybatisSession) throws ApplicationException
	{
		Integer loCurrentSeq = HHSConstants.INT_ZERO;
		loCurrentSeq = (Integer) DAOUtil.masterDAO(aoMybatisSession, null,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER, HHSConstants.CBY_GET_SEQ_FOR_MILESTONE,
				null);
		return loCurrentSeq;
	}

	/**
	 * <p>
	 * This method is used for adding new rows in Milestone grid for Contract
	 * Budget Amendment for a particular budget and sub-budgets.
	 * <ul>
	 * <li>Provider is able to Add new Milestone by creating a new row, typing
	 * in the Milestone title and entering the FY Budget amount.</li>
	 * </ul>
	 * </p>
	 * <ul>
	 * <li>Calls query 'insertMilestoneDetails'</li>
	 * </ul>
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
		try
		{  
			
			// ADD NEW MILESTONE WITH AMENDMENT AMOUNT
			aoCBMilestoneBean.setId(String.valueOf(aoCurrentSeq));
		
			loStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoCBMilestoneBean,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
				HHSConstants.CBY_INSERT_MILESTONE_DETAILS, HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_MILE_STONE_BEAN);
			if (loStatus > HHSConstants.INT_ZERO)
			{
				loAddMilestone = true;
			}
			
		}
		// HANDLING APPLICATION EXCEPTION
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("Exception occured while adding Milestone Details ", aoAppEx);
			LOG_OBJECT.Error("error occured while adding Milestone Details ", aoAppEx);
			setMoState("error occured while adding Milestone Details for budget type id "
					+ aoCBMilestoneBean.getBudgetTypeId());
			throw aoAppEx;
		}
		// HANDLING EXCEPTION
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured while adding Milestone in ContractBudgetAmendmentService ", aoEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService: addMilestone method - failed to add"
					+ aoCBMilestoneBean.getBudgetTypeId() + " \n");
			throw new ApplicationException("Error occured while inserting the details for Milestone", aoEx);
		}
		
		return loAddMilestone;
	}

	/**
	 * <p>
	 * This method is used for editing the rows in Milestone grid for Contract
	 * Budget Amendment for a particular budget and sub-budgets.
	 * <ul>
	 * <li>Provider is able to Edit the Milestone line items that have
	 * previously been added.</li>
	 * <li>
	 * If the absolute value of the Amendment amount entered is greater than the
	 * [Remaining Amount ? Negative Amendment amounts entered for the line item
	 * in other pending Amendments for which no budgets have been approved],
	 * then display grid level error.</li>
	 * </ul>
	 * </p>
	 * <ul>
	 * <li>Calls query 'fetchMilestoneDetailsForValidationInMultipleAmendments'</li>
	 * </ul>
	 * @param aoCBMilestoneBean - CBMileStoneBean
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
		{   // VALIDATION FOR POSITIVE AMENDMENT (FOR POSITIVE AMENDMENT ONLY)
			if (aoCBMilestoneBean.getAmendmentType().equalsIgnoreCase(HHSConstants.POSITIVE)
					&& (new BigDecimal(aoCBMilestoneBean.getModificationAmount()).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO))
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.POSITIVE_AMENDMENT_MSG));
			}
			// VALIDATION FOR NEGATIVE AMENDMENT (FOR NEGATIVE AMENDMENT ONLY)
			else if (aoCBMilestoneBean.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE)
					&& (new BigDecimal(aoCBMilestoneBean.getModificationAmount()).compareTo(BigDecimal.ZERO) > HHSConstants.INT_ZERO))
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.NEGATIVE_AMENDMENT_MSG));
			}
			// GET AMOUNT DETAILS FOR SERVER SIDE VALIDATION
			loCBMileStoneBean = (CBMileStoneBean) DAOUtil.masterDAO(aoMybatisSession, aoCBMilestoneBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
					HHSConstants.CBM_FETCH_MILESTONE_DETAILS_FOR_VALIDATION_IN_MULTIPLE_AMENDMENTS,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_MILE_STONE_BEAN);
			
			if (aoCBMilestoneBean.getMileStone().equals(HHSConstants.EMPTY_STRING)
					|| aoCBMilestoneBean.getMileStone() == null)
			{
				aoCBMilestoneBean.setMileStone(loCBMileStoneBean.getMileStone());
				aoCBMilestoneBean.setAmount(loCBMileStoneBean.getAmount());
			}
			// NEWLY ADDED ROWS AMOUNT WOULD NOT BE FY APPROVED BUDGET
			if (loCBMileStoneBean.getSubBudgetID().equals(aoCBMilestoneBean.getSubBudgetID()))
			{
				loCBMileStoneBean.setRemainAmt(HHSConstants.STRING_ZERO);
			}
			// CHECK IF AMENDMENT AMOUNT IS NOT LESS THAN ALREADY INVOICED
			// AMOUNT
			// BEFORE UPDATING THE DETAILS
			if ((new BigDecimal(loCBMileStoneBean.getRemainAmt()).add(new BigDecimal(aoCBMilestoneBean
					.getModificationAmount()))).compareTo(BigDecimal.ZERO) >= HHSConstants.INT_ZERO)
			{
				loUpdateMilestone = updateMilestoneDetails(aoCBMilestoneBean, aoMybatisSession);
				
			}
			else
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.CBM_LESS_THAN_INVOICE_INCLUDING_PENDING_NEG_AMEND_ERROR_MESSAGE));
			}
		}
		// HANDLING APPLICATION EXCEPTION
		catch (ApplicationException aoAppExp)
		{
			if (lbError)
			{
				aoAppExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppExp.toString());
				LOG_OBJECT.Error("server side validation fail in Milestone ", aoAppExp);
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
			LOG_OBJECT.Error("Exception occured while editing Milestone in ContractBudgetAmendmentService ", aoEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService: updateMilestone method - failed to edit"
					+ aoCBMilestoneBean.getBudgetTypeId() + " \n");
			throw new ApplicationException("Error occured while editing Milestone", aoEx);
		}
		return loUpdateMilestone;
	}

	/**
	 * This method is used to Update the amendment line item details of
	 * Milestone.
	 * <ul>
	 * <li>Calls query 'updateMilestoneDetails'</li>
	 * <li>Calls query 'insertNewMilestoneForAmd'</li>
	 * </ul>
	 * @param aoCBMilestoneBean - CBMileStoneBean
	 * @param aoMybatisSession - SqlSession
	 * @return loUpdateMilestone - Boolean status
	 * @throws ApplicationException - ApplicationException object
	 */
	private Boolean updateMilestoneDetails(CBMileStoneBean aoCBMilestoneBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{   
		Integer loStatus = HHSConstants.INT_ZERO;
		Boolean loUpdateMilestone = false;
		// UPDATE MILESTONE AMOUNT FOR THE SELECTED ROW ID
		loStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoCBMilestoneBean,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER, HHSConstants.CBY_UPDATE_MILESTONE_DETAILS,
				HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_MILE_STONE_BEAN);

		// INSERT MILESTONE TO BE UPDATED ON SELECT
		if (loStatus <= HHSConstants.INT_ZERO)
		{ 
			DAOUtil.masterDAO(aoMybatisSession, aoCBMilestoneBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
					HHSConstants.INSERT_NEW_MILESTONE_FOR_AMD, HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_MILE_STONE_BEAN);
		}
		loUpdateMilestone = true;
		return loUpdateMilestone;
	}

	/**
	 * <p>
	 * This method is used for deleting the added rows in Milestone grid for
	 * Contract Budget Amendment for a particular budget and sub-budgets.
	 * <ul>
	 * <li>Provider is able to Delete Milestone line items entirely on the
	 * Pending Submission budget submission for the newly added line items.</li>
	 * </ul>
	 * </p>
	 * <ul>
	 * <li>Calls query 'deleteMilestoneDetails'</li>
	 * </ul>
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
			
			loStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoCBMilestoneBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
					HHSConstants.CBY_DELETE_MILESTONE_DETAILS, HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_MILE_STONE_BEAN);
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
			LOG_OBJECT.Error("Exception occured while editing deleting milestone in ContractBudgetAmendmentService ",
					aoEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService: deleteMilestone method - failed to delete"
					+ aoCBMilestoneBean.getBudgetTypeId() + " \n");
			throw new ApplicationException("Error occured while deleting Milestone", aoEx);
		}
		return loDeleteMilestone;
	}

	/**
	 * <ul>
	 * <li>This method is used to fetch data for Rent
	 * 
	 * <li>For Rent Amendment Screen</li>
	 * <ol>
	 * <li>Rent grid column will be editable</li>
	 * <ul>
	 * <li>Editable field only for new rows that are added from within the
	 * Amendment budget.</li>
	 * <li>If the row was a part of the approved budget at the point the
	 * Amendment budget was created, the field will be read only</li>
	 * </ul>
	 * <li>Approved FY Budget column is read only</li>
	 * <li>For Amendment Amount column will be editable</li>
	 * <ul>
	 * <li>Verify upon save that the Amendment Amount entered</li>
	 * <li>would not cause the Total Proposed Budget for the line item to fall
	 * below the YTD Invoiced Amount.</li>
	 * <li>If it would, display error message: ?!</li>
	 * <li>Entered value would cause the Proposed Budget to fall below the
	 * amount already invoiced for the line item. Please enter a new value.?</li>
	 * </ul>
	 * </ol>
	 * </ul>
	 * <ul>
	 * <li>Calls query 'fetchContractBudgetModificationRent'</li>
	 * <li>Calls query 'fetchContractBudgetModificationRentNew'</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SqlSession object
	 * @param aoCBGridBeanObj CBGridBean object
	 * @param aoMasterBean MasterBean object
	 * @return loRent List<Rent> object will return the list of rent
	 * @throws ApplicationException ApplicationException to catch application
	 *             throwing
	 */
	@SuppressWarnings("unchecked")
	public List<Rent> fetchAmendmentRent(SqlSession aoMyBatisSession, CBGridBean aoCBGridBeanObj,
			MasterBean aoMasterBean) throws ApplicationException
	{
		List<Rent> loRent = new ArrayList<Rent>();
		List<Rent> loNewRent = new ArrayList<Rent>();
		String lsParentSubBudgetId = aoCBGridBeanObj.getParentSubBudgetId();
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);
		try
		{
			// Fetch the data from FileNet XML if Budget Status is approved
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loRent = fetchRentFromXML(lsSubBudgetId, aoMasterBean);
				if (loRent != null)
				{
					loNewRent.addAll(loRent);
				}
			}

			else
			{
				// For the previous entered records matching the subbudgetId
				if (aoCBGridBeanObj.getParentSubBudgetId() != null && aoCBGridBeanObj.getSubBudgetID() != null
						&& !aoCBGridBeanObj.getParentSubBudgetId().equalsIgnoreCase(aoCBGridBeanObj.getSubBudgetID()))
				{
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
			LOG_OBJECT.Error("Exception occured while retrieving ContractBudgetAmendmentRent", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService:fetchContractBudgetAmendmentRent"
					+ " method " + "- failed to fetch record " + " \n");
			throw aoAppEx;
		}
		/**
		 * Exception handled here
		 */
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in " + "ContractBudgetAmendmentRent ", aoEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService:fetchContractBudgetAmendmentRent"
					+ " method - failed to fetch record " + " \n");
			throw new ApplicationException("Exception occured while fetch in ContractBudgetAmendmentService" + " ",
					aoEx);
		}

		return loNewRent;
	}

	/**
	 * The method is updated to Release 7. Accessibility is changed to public to
	 * re-use method for approved modifications. 
	 * This method fetch Salaried Employee list from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List<PersonnelServiceBudget>
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public List<Rent> fetchRentFromXML(String asSubBudgetId, MasterBean aoMasterBean) throws ApplicationException
	{
		List<Rent> loRentList = null;
		loRentList = generateRentList(asSubBudgetId, aoMasterBean);
		loRentList = fetchListForRent(loRentList);
		return loRentList;
	}

	/**
	 * This method fetch consolidated list of PersonnelServices which includes
	 * all type of Employees from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List<PersonnelServiceBudget>
	 * @throws ApplicationException If an Application Exception occurs
	 */

	private List<Rent> generateRentList(String asSubBudgetId, MasterBean aoMasterBean) throws ApplicationException
	{
		List<LineItemMasterBean> loMasterBeanList = null;
		List<Rent> loRentList = null;

		loMasterBeanList = aoMasterBean.getMasterBeanList();
		Iterator<LineItemMasterBean> aoListIterator = loMasterBeanList.iterator();
		while (aoListIterator.hasNext())
		{
			LineItemMasterBean loLineItemBean = aoListIterator.next();
			if (loLineItemBean.getSubbudgetId().equals(asSubBudgetId))
			{
				loRentList = loLineItemBean.getRentBeanList();
			}
		}
		return loRentList;
	}

	/**
	 * This method fetch records of only type Rent from the consolidated list
	 * 
	 * @param aoRentListForAmendment List<Rent>
	 * @return loRentList List<Rent>
	 */
	private List<Rent> fetchListForRent(List<Rent> aoRentListForAmendment) throws ApplicationException
	{
		List<Rent> loRentListForBudgetAmendment = null;
		if (aoRentListForAmendment != null)
		{
			Iterator<Rent> aoListIterator = aoRentListForAmendment.iterator();
			loRentListForBudgetAmendment = new ArrayList<Rent>();
			while (aoListIterator.hasNext())
			{
				Rent loRentBean = aoListIterator.next();
				loRentListForBudgetAmendment.add(loRentBean);
			}
		}
		return loRentListForBudgetAmendment;

	}

	/**
	 * <li>This method is used to append _newRecord for all the rows that are
	 * added at the time of Amendment.</li>
	 * 
	 * @param aoRentForBudgetAmendment List<Rent> object to add the list
	 * @param asParentSubBudgetIdForAmendment String ParentSubBudgetId
	 * 
	 */
	private void concatNewRecord(List<Rent> aoRentForBudgetAmendment, String asParentSubBudgetIdForAmendment)
	{

		StringBuffer loConcatForAmendment = null;
		if (null != aoRentForBudgetAmendment && aoRentForBudgetAmendment.size() > HHSConstants.INT_ZERO)
		{
			for (Rent loRent : aoRentForBudgetAmendment)
			{
				loConcatForAmendment = new StringBuffer();
				if (!(loRent.getSubBudgetID() == (asParentSubBudgetIdForAmendment)))
				{
					loConcatForAmendment.append(loRent.getId());
					// Adding new_record to all Amended ONE
					loConcatForAmendment.append(HHSConstants.NEW_RECORD_RENT);
					loRent.setId(loConcatForAmendment.toString());
				}
			}
		}
	}

	/**
	 * <ol>
	 * <li>This method is used to edit data for Rent Amendment on rent Amendment
	 * Screen</li>
	 * <li>This method is invoked when gridOperation is performed for update
	 * Amendment Rent Transaction Id</li>
	 * </ol>
	 * 
	 * @param aoMyBatisSession SqlSe ssion object
	 * @param aoRent Rent object
	 * @return boolean loStatus boolean to store the status of updates
	 * @throws ApplicationException Application Exception thrown
	 */
	@SuppressWarnings("rawtypes")
	public boolean updateAmendmentRent(SqlSession aoMyBatisSession, Rent aoRent) throws ApplicationException
	{
		Boolean loStatus = false;
		HashMap loHashMap = null;
		Boolean loError = false;
		try
		{
			// Validation for positive amendment (For Positive Amendment only.)
			if (aoRent.getAmendmentType().equalsIgnoreCase(HHSConstants.POSITIVE)
					&& (new BigDecimal(aoRent.getModifyAmount()).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO))
			{
				loError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.POSITIVE_AMENDMENT_MSG));
			}
			// Validation for negative amendment (For Negative Amendment only)
			else if (aoRent.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE)
					&& (new BigDecimal(aoRent.getModifyAmount()).compareTo(BigDecimal.ZERO) > HHSConstants.INT_ZERO))
			{
				loError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.NEGATIVE_AMENDMENT_MSG));
			}
			if (aoRent != null)
			{
				// Validation will be done for remaining amount
				loHashMap = validateRentAmendmentData(aoMyBatisSession, aoRent);
				if ((Boolean) loHashMap.get(HHSConstants.IS_VALID_DATA))
				{
					loStatus = updateInsertRent(aoMyBatisSession, aoRent);
				}
				else
				{
					// Error message thrown once the amendment amount is an
					// invalid
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
				// In case of Amendment data validation failure, the
				// exception will be thrown back to base controller
				// along with the validation message
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			LOG_OBJECT.Error("App Exception occured in ContractBudgetAmendmentService:"
					+ " updateAmendmentRent method:: ", aoAppEx);
			setMoState("Transaction Failed::App Exception in ContractBudgetAmendmentService: "
					+ "updateAmendmentRent()" + " method aoCBRent::" + aoRent + "\n");
			throw aoAppEx;
		}
		// Exception handled here
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetAmendmentService: editRent method:: ", aoEx);
			LOG_OBJECT.Error("Exception occured in ContractBudgetAmendmentService: " + "updateAmendmentRent method:: ",
					aoEx);
			setMoState("Transaction Failed::Exception in ContractBudgetAmendmentService: "
					+ "updateAmendmentRent() method" + " aoCBRent::" + aoRent + "\n");
			throw loAppEx;
		}
		return loStatus;
	}

	/**
	 * This private method updates or inserts Rent row
	 * <ul>
	 * <li>Calls query 'updateModificationRent'</li>
	 * <li>Calls query 'editRentModification'</li>
	 * </ul>
	 * @param aoMyBatisSession SqlSession object
	 * @param aoRentForAmendment Rent object
	 * @return Boolean loStatus
	 * @throws ApplicationException ApplicationException object
	 */
	private Boolean updateInsertRent(SqlSession aoMyBatisSession, Rent aoRentForAmendment) throws ApplicationException
	{
		Integer loVal = 0;
		Boolean loStatus = false;
		Rent loRentForAmendment = null;
		loRentForAmendment = (Rent) DAOUtil.masterDAO(aoMyBatisSession, aoRentForAmendment,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
				HHSConstants.FETCH_CONTRACT_BUDGET_MODIFICATION_RENT_AMOUNT, HHSConstants.CBY_COM_NYC_HHS_MODEL_RENT);
		// add the bean items received to your bean being
		// updated
		if (null == aoRentForAmendment.getLocation() || aoRentForAmendment.getLocation().isEmpty())
		{
			aoRentForAmendment.setLocation(loRentForAmendment.getLocation());
		}
		if (null == aoRentForAmendment.getManagementCompanyName()
				|| aoRentForAmendment.getManagementCompanyName().isEmpty())
		{
			aoRentForAmendment.setManagementCompanyName(loRentForAmendment.getManagementCompanyName());
		}
		if (null == aoRentForAmendment.getPropertyOwner() || aoRentForAmendment.getPropertyOwner().isEmpty())
		{
			aoRentForAmendment.setPropertyOwner(loRentForAmendment.getPropertyOwner());
		}
		if (null == aoRentForAmendment.getPublicSchoolSpace() || aoRentForAmendment.getPublicSchoolSpace().isEmpty())
		{
			aoRentForAmendment.setPublicSchoolSpace(loRentForAmendment.getPublicSchoolSpace());
		}
		if (null == aoRentForAmendment.getPercentChargedToContract()
				|| aoRentForAmendment.getPercentChargedToContract().isEmpty())
		{
			aoRentForAmendment.setPercentChargedToContract(loRentForAmendment.getPercentChargedToContract());
		}
		loVal = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoRentForAmendment,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.MODIFICATION_UPDATE_RENT,
				HHSConstants.CBY_COM_NYC_HHS_MODEL_RENT);
		// If the update is for the first time it will insert a new
		// record for the same as Amendment
		if (loVal < HHSConstants.INT_ONE)
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoRentForAmendment,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER, HHSConstants.EDIT_RENT_MODIFICATION,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_RENT);
		}
		loStatus = true;
		return loStatus;
	}

	/**
	 * <ul>
	 * <li>This method is used to add data for Rent on Amendment of Rent Screen</li>
	 * <li>This method is invoked when gridOperation is performed for insert
	 * Contract Budget Amendment Rent Transaction Id</li>
	 * <li>For Rent Amendment Screen</li>
	 * <ol>
	 * <li>By default, the field will be blank when a new row is added</li>
	 * </ol>
	 * </ul>
	 * <ul>
	 * <li>Calls query 'insertContractBudgetModificationRent'</li>
	 * </ul>
	 * @param aoMyBatisSession SqlSession object
	 * @param aoRent Rent object
	 * @return loVal Integer loVal
	 * 
	 * @throws ApplicationException ApplictaionException caught
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public Integer insertContractBudgetAmendmentRent(SqlSession aoMyBatisSession, Rent aoRent)
			throws ApplicationException
	{
		Integer loVal = HHSConstants.INT_ZERO;
		HashMap loHashMap = null;
		Boolean loError = false;
		try
		{
			if (aoRent != null)
			{
				// Validation will be done for remaining amount
				loHashMap = validateRentAmendmentData(aoMyBatisSession, aoRent);
				if ((Boolean) loHashMap.get(HHSConstants.IS_VALID_DATA))
				{
					loVal = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoRent,
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
							(String) loHashMap.get(HHSConstants.ERROR_MESSAGE)));
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
				// In case of Amendment data validation failure, the
				// exception will be thrown back to base controller
				// along with the validation message
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			aoAppEx.addContextData("Rent : ", aoRent);
			LOG_OBJECT.Error("Exception occured while inserting ContractBudgetAmendmentRent", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService:" + "insertContractBudgetAmendmentRent"
					+ " method - failed to insert record " + " \n");
			throw aoAppEx;
		}
		/**
		 * Exception handled here
		 */
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception while validating remaining amount", aoEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService:" + "insertContractBudgetAmendmentRent"
					+ " method - failed to insert record " + " \n");
			throw new ApplicationException("Exception occured while inserting in" + " ContractBudgetAmendmentService :"
					+ " insertContractBudgetAmendmentRent method", aoEx);
		}

		return loVal;
	}

	/**
	 * This method validates the remaining amount for a particular line-item and
	 * returns FALSE if the amended amount is greater then the [remaining amount
	 * ? Negative Amendment amounts entered for the Personnel Services line item
	 * in other pending Amendments for which no budgets have been approved] else
	 * it validates it as TRUE
	 * <ul>
	 * <li>Calls query 'getRemainingAmountRentInMultipleAmendments'</li>\
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoMybatisSession Session object
	 * @param aoRent Rent object
	 * @return loHashMap Hash map returning the value
	 * @throws ApplicationException Application exception handled
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private HashMap validateRentAmendmentData(SqlSession aoMybatisSession, Rent aoRent) throws ApplicationException
	{
		Boolean loIsDataValid = true;
		HashMap loHashMap = new HashMap();
		BigDecimal loRemainingAmount = null;
		loHashMap.put(HHSConstants.IS_VALID_DATA, loIsDataValid);
		try
		{
			// validates the Amendment amount so that it should not fall
			// below YTD Amount
			if (loIsDataValid && aoRent.getId() != null && !aoRent.getId().contains(HHSConstants.NEW_ROW_IDENTIFIER))
			{
				loRemainingAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoRent,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.GET_REMAINING_AMOUNT_RENT_IN_MULTIPLE_AMENDMENTS,
						HHSConstants.CBY_COM_NYC_HHS_MODEL_RENT);
			}
			loRemainingAmount = ((loRemainingAmount == null) ? new BigDecimal(HHSConstants.INT_ZERO)
					: loRemainingAmount);

			if (aoRent.getModifyAmount() != null
					&& !((loRemainingAmount.add(new BigDecimal(aoRent.getModifyAmount()))).compareTo(BigDecimal.ZERO) >= HHSConstants.INT_ZERO))
			{
				loHashMap.put(HHSConstants.IS_VALID_DATA, Boolean.FALSE);
				// Error message thrown for the Proposed budget, we are using
				// the rate validation message as the error message is same
				loHashMap.put(HHSConstants.ERROR_MESSAGE,
						HHSConstants.CBM_LESS_THAN_INVOICE_INCLUDING_PENDING_NEG_AMEND_ERROR_MESSAGE);
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
			setMoState("Transaction Failed:: ContractBudgetAmendmentService:" + "validateRentAmendmentData method"
					+ " \n");
			throw aoAppEx;
		}
		return loHashMap;
	}

	/**
	 * <ol>
	 * <li>This method is used to delete data for Rent Screen</li>
	 * <li>This method is invoked when gridOperation is performed for delete
	 * Rent Amendment Transaction Id</li>
	 * </ol>
	 * <ul>
	 * <li>Calls query 'delRentModification'</li>
	 * </ul>
	 * @param aoMyBatisSession SqlSession object
	 * @param aoRent Rent object
	 * @return boolean
	 * 
	 * @throws ApplicationException Application Exception returned
	 */
	public boolean deleteRentAmendment(SqlSession aoMyBatisSession, Rent aoRent) throws ApplicationException
	{
		boolean lbStatus = false;
		try
		{
			// The user will onle be able to delete the rows of Amendment budget
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
			LOG_OBJECT.Error("Exception occured in ContractBudgetAmendmentService:" + " deleteRentAmendment method:: ",
					aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService: deleteRentModification "
					+ "method - failed Exception occured while deleting data\n");
			throw aoAppEx;
		}
		/**
		 * Exception handled here
		 */
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception while validating remaining amount", aoEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService:deleteRentAmendment"
					+ " method - failed to insert record " + " \n");
			throw new ApplicationException("Exception occured while inserting in ContractBudgetAmendmentService :"
					+ " deleteRentAmendment method", aoEx);
		}

		return lbStatus;
	}

	/**
	 * This method is used to fetch rate line-items from Rate table.
	 * 
	 * It queries into DB to fetch two RateBeanList and then merge them using
	 * <b>mergeAmendmentAmount</b> method. It then calls
	 * <b>markNewRowInContractBudgetBeanList</b> to mark the newly added rows.
	 * <ul>
	 * <li>Calls query 'fetchContractBudgetRateInfo'</li>
	 * <li>Calls query 'fetchContractBudgetModificationAmount'</li>
	 * </ul>
	 * @param aoMybatisSession : MyBatis session for SQL Session
	 * @param aoRateBeanObj : CBGridBean object
	 * @return loRateBeanList - List<RateBean> : returns the merged rateBean
	 *         list to be shown in Rate Grid
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<RateBean> fetchContractBudgetAmendmentRate(SqlSession aoMybatisSession, CBGridBean aoRateBeanObj,
			MasterBean aoMasterBean) throws ApplicationException
	{
		List<RateBean> loRateBeanList = null;
		List<RateBean> loRateBeanAmendmentAmntList = null;
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);

		try
		{
			if (aoRateBeanObj != null)
			{
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
					String lsParentSubBudgetId = aoRateBeanObj.getParentSubBudgetId();
					loRateBeanList = (List<RateBean>) DAOUtil.masterDAO(aoMybatisSession, aoRateBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBF_FETCH_CONTRACT_BUDGET_RATE_INFO,
							HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

					loRateBeanAmendmentAmntList = (List<RateBean>) DAOUtil.masterDAO(aoMybatisSession, aoRateBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBF_FETCH_CONTRACT_BUDGET_MODIFICATION_AMOUNT,
							HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

					// Merge Amendment Amount
					mergeAmendmentAmount(loRateBeanList, loRateBeanAmendmentAmntList);
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
			LOG_OBJECT.Error("Exception occured while retrieving ContractBudgetAmendmentRateInfo", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetAmendmentService:fetchContractBudgetAmendmentRate method - failed to fetch record "
					+ " \n");
			throw aoAppEx;
		}
		return loRateBeanList;
	}

	/**
	 * This method is called from fetchContractBudgetAmendmentRate method.
	 * 
	 * It takes two rateBeanList as parameter and merge the Amendment Amount
	 * from second list into first one.
	 * 
	 * @param aoRateBeanList : rate bean list with remaining and FY amount.
	 * @param aoRateBeanAmendmentAmntList : rate bean list with Amendment
	 *            amount.
	 */
	private void mergeAmendmentAmount(List<RateBean> aoRateBeanList, List<RateBean> aoRateBeanAmendmentAmntList)
	{
		for (int loCountOut = 0; loCountOut < aoRateBeanList.size(); loCountOut++)
		{
			for (int loCountIn = 0; loCountIn < aoRateBeanAmendmentAmntList.size(); loCountIn++)
			{
				if (aoRateBeanList.get(loCountOut).getId()
						.equals(aoRateBeanAmendmentAmntList.get(loCountIn).getLsParentId()))
				{
					aoRateBeanList.get(loCountOut).setLsModifyUnits(
							aoRateBeanAmendmentAmntList.get(loCountIn).getLsModifyUnits());
					aoRateBeanList.get(loCountOut).setLsModifyAmount(
							aoRateBeanAmendmentAmntList.get(loCountIn).getLsModifyAmount());
					break;
				}
			}
		}
	}

	/**
	 * This method iterates the RateBeanList and identifies the new row on the
	 * basis of <b>Sub-BudgetId</b>.
	 * 
	 * After the new row is identified the Id for that particular Sub-Budget is
	 * appended with <b>"_newrecord"</b>, so that it can be distinguished from
	 * other line-items.
	 * 
	 * @param aoRateBeanList : returns the merged bean List with distinguished
	 *            new row.
	 * 
	 * @param asParentSubBudgetId : passes the parent sub-budget which
	 *            differentiates the new row
	 */
	private void markNewRowInContractBudgetBeanList(List<RateBean> aoRateBeanList, String asParentSubBudgetId)
	{
		for (RateBean loRateBean : aoRateBeanList)
		{
			if (!loRateBean.getSubBudgetID().equalsIgnoreCase(asParentSubBudgetId))
			{
				loRateBean.setId(loRateBean.getId() + HHSConstants.NEW_RECORD);
			}
		}
	}

	/**
	 * This method is used to insert a new line-item entry into RATE table for
	 * amendment. It first validates the entered data on the basis of Negative
	 * and Positive amendment and in case of Negative amendment does the further
	 * validations.
	 * <ul>
	 * <li>Calls query 'insertContractBudgetModificationRateInfo'</li>
	 * </ul>
	 * @param aoMybatisSession : MyBatis session for SQL Session
	 * @param aoRateBeanObj - RateBean : RateBean object containing the required
	 *            parameters
	 * @return loRowInserted : returns the number of row inserted successfully
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("rawtypes")
	public Integer insertContractBudgetAmendmentRateInfo(SqlSession aoMybatisSession, RateBean aoRateBeanObj)
			throws ApplicationException
	{
		Integer loRowInserted = HHSConstants.INT_ZERO;
		Boolean loError = false;
		HashMap loHashMap = null;
		try
		{
			if (aoRateBeanObj != null)
			{
				if (!aoRateBeanObj.getAmendmentType().equalsIgnoreCase(HHSConstants.EMPTY_STRING))
				{
					Map<Boolean, ApplicationException> loMap = amendmentAmountCheck(aoRateBeanObj,
							aoRateBeanObj.getLsModifyAmount());
					if (loMap.get(Boolean.TRUE) != null)
					{
						loError = true;
						throw loMap.get(Boolean.TRUE);
					}
				}
				if (new BigDecimal(aoRateBeanObj.getLsModifyAmount()).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO)
				{
					loHashMap = validateRateAmendmentData(aoMybatisSession, aoRateBeanObj);
				}
				if ((new BigDecimal(aoRateBeanObj.getLsModifyAmount()).compareTo(BigDecimal.ZERO) >= HHSConstants.INT_ZERO)
						|| (Boolean) loHashMap.get(HHSConstants.IS_VALID_DATA))
				{
					loRowInserted = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoRateBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBF_INSERT_CONTRACT_BUDGET_MODIFICATION_RATE_INFO,
							HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN);
					setMoState("Method insertContractBudgetAmendmentRateInfo executed succesfully");
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
				// In case of Amendment data validation failure, the
				// exception will be thrown back to base controller
				// along with the validation message
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			aoAppEx.addContextData(HHSConstants.AO_RATE_BEAN_OBJ, aoRateBeanObj);
			LOG_OBJECT.Error("Exception occured while inserting ContractBudgetAmendmentRateInfo", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService:insertContractBudgetAmendmentRateInfo method - failed to insert record "
					+ " \n");
			throw aoAppEx;
		}
		return loRowInserted;
	}

	/**
	 * This method is called on update of any rate line-item in Contract Budget
	 * Amendment on the basis of modifiedSubBudgetId, parentId.
	 * 
	 * In case a matching row is not found in DB, a new row is inserted in DB.
	 * 
	 * @param aoMybatisSession : MyBatis session for SQL Session
	 * @param aoRateBeanObj : rateBean object containing required information
	 * @return loRowUpdated : returns the number of rows updated
	 * @throws ApplicationException : ApplicationException object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Integer updateContractBudgetAmendmentRateInfo(SqlSession aoMybatisSession, RateBean aoRateBeanObj)
			throws ApplicationException
	{
		Integer loRowUpdated = HHSConstants.INT_ZERO;
		Boolean loError = false;
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.IS_VALID_DATA, true);
		try
		{
			if (aoRateBeanObj != null)
			{
				if (!aoRateBeanObj.getAmendmentType().equalsIgnoreCase(HHSConstants.EMPTY_STRING))
				{
					Map<Boolean, ApplicationException> loMap = amendmentAmountCheck(aoRateBeanObj,
							aoRateBeanObj.getLsModifyAmount());
					if (loMap.get(Boolean.TRUE) != null)
					{
						loError = true;
						throw loMap.get(Boolean.TRUE);
					}
				}
				if (aoRateBeanObj.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE))
				{
					loHashMap = validateRateAmendmentData(aoMybatisSession, aoRateBeanObj);
					if ((Boolean) loHashMap.get(HHSConstants.IS_VALID_DATA))
					{
						loRowUpdated = insertUpdateRate(aoMybatisSession, aoRateBeanObj);
					}
					else
					{
						loError = true;
						throw new ApplicationException(PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
								(String) loHashMap.get(HHSConstants.ERROR_MESSAGE)));
					}

				}
				if (aoRateBeanObj.getAmendmentType().equalsIgnoreCase(HHSConstants.POSITIVE))
				{
					loRowUpdated = insertUpdateRate(aoMybatisSession, aoRateBeanObj);
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
				// In case of Amendment data validation failure, the
				// exception will be thrown back to base controller
				// along with the validation message
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			aoAppEx.addContextData(HHSConstants.AO_RATE_BEAN_OBJ, aoRateBeanObj);
			LOG_OBJECT.Error("Exception occured while updating ContractBudgetAmendmentRateInfo", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService:updateContractBudgetAmendmentRateInfo method - failed to insert record "
					+ " \n");
			throw aoAppEx;
		}
		return loRowUpdated;
	}

	/**
	 * Insert update rate grid.
	 * <ul>
	 * <li>Calls query 'updateContractBudgetModificationRateInfo'</li>
	 * <li>Calls query 'updateBudgetModificationRateUnit'</li>
	 * <li>Calls query 'getUnitDescContractBudgetModificationRate'</li>
	 * <li>Calls query 'getFYBudgetContractBudgetModificationRate'</li>
	 * <li>Calls query 'insertNewContractBudgetModificationRate'</li>
	 * </ul>
	 * @param aoMybatisSession sql session as input.
	 * @param aoRateBeanObj rate grid as input.
	 * @return loRowUpdated number of rows updated as output
	 * @throws ApplicationException : ApplicationException object
	 */
	private Integer insertUpdateRate(SqlSession aoMybatisSession, RateBean aoRateBeanObj) throws ApplicationException
	{
		Integer loRowUpdated = HHSConstants.INT_ZERO;

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
			// Update for the base rows. Returns zero if no
			// Amendment row exists already
			loRowUpdated = (Integer) DAOUtil
					.masterDAO(aoMybatisSession, aoRateBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBF_UPDATE_BUDGET_MODIFICATION_RATE_UNIT,
							HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN);
			if (loRowUpdated == HHSConstants.INT_ZERO)
			{
				// In case no Amendment row exists for the base
				// row, fetch the UnitDesc and then insert
				// a new Amendment row.
				aoRateBeanObj.setUnitDesc((String) DAOUtil.masterDAO(aoMybatisSession, aoRateBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBF_GET_UNIT_DESC_CONTRACT_BUDGET_MODIFICATION_RATE,
						HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN));
				aoRateBeanObj.setFyBudget((String) DAOUtil.masterDAO(aoMybatisSession, aoRateBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBF_GET_FY_BUDGET_CONTRACT_BUDGET_MODIFICATION_RATE,
						HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN));
				DAOUtil.masterDAO(aoMybatisSession, aoRateBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBF_INSERT_NEW_CONTRACT_BUDGET_MODIFICATION_RATE,
						HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN);
			}
		}
		setMoState("Method updateContractBudgetAmendmentRateInfo executed succesfully");
		return loRowUpdated;
	}

	/**
	 * This method deletes a budget Amendment rate line-item on the basis of
	 * <b>id</b> and <b>subBudgetID</b>.
	 * 
	 * <ul>
	 * <li>Line-item will be deleted from DB only if it is a newly added row.</li>
	 * </ul>
	 * <ul>
	 * <li>Calls query 'deleteContractBudgetModificationRateInfo'</li>
	 * </ul>
	 * @param aoMybatisSession : MyBatis session for SQL Session
	 * @param aoRateBeanObj - RateBean : RateBean object containing the required
	 *            parameters
	 * @return loRowDeleted : returns the number of rows deleted
	 * @throws ApplicationException - ApplicationException object
	 */
	public Integer deleteContractBudgetAmendmentRateInfo(SqlSession aoMybatisSession, RateBean aoRateBeanObj)
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
				setMoState("Method deleteContractBudgetAmendmentRateInfo executed succesfully");
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.AO_RATE_BEAN_OBJ, aoRateBeanObj);
			LOG_OBJECT.Error("Exception occured while deleting ContractBudgetAmendmentRateInfo", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService:deleteContractBudgetAmendmentRateInfo method - failed to insert record "
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
	 * 
	 * It also validates the amount entered so that the entered negative
	 * amendment amount should not make the Proposed Budget to fall below YTD
	 * Amount.
	 * <ul>
	 * <li>Calls query 'getBaseUnitContractBudgetRateInMultipleAmendments'</li>
	 * <li>Method Updated in R4
	 * </ul>
	 * @param aoMybatisSession : MyBatis session for SQL Session
	 * @param aoRateBeanObj : RateBean object containing the required parameters
	 * @return loHashMap : returns the hashmap containing the validation flag
	 *         and message.
	 * @throws ApplicationException - ApplicationException object
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private HashMap validateRateAmendmentData(SqlSession aoMybatisSession, RateBean aoRateBeanObj)
			throws ApplicationException
	{
		Boolean loIsDataValid = true;
		HashMap loHashMap = new HashMap();
		Integer loRemainingUnit = null;
		BigDecimal loRemainingAmount = null;
		loHashMap.put(HHSConstants.IS_VALID_DATA, loIsDataValid);
		try
		{
			// No validation for adding new row
			if (aoRateBeanObj.getId() != null && !aoRateBeanObj.getId().contains(HHSConstants.NEW_ROW_IDENTIFIER))
			{
				loRemainingUnit = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoRateBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.FETCH_RATE_VALIDATION_REMNG_UNITS_FOR_MULTIPLE_AMENDMENTS,
						HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN);
			}
			loRemainingUnit = ((loRemainingUnit == null) ? HHSConstants.INT_ZERO : loRemainingUnit);
			// validates the Amendment unit with remaining units
			if (aoRateBeanObj.getLsModifyUnits() != null
					&& (loRemainingUnit + Integer.parseInt((aoRateBeanObj.getLsModifyUnits())) < HHSConstants.INT_ZERO))
			{
				loHashMap.put(HHSConstants.IS_VALID_DATA, Boolean.FALSE);
				loHashMap.put(HHSConstants.ERROR_MESSAGE, HHSConstants.BUDGET_MODIFICATION_RATE_UNIT_VALIDATION);
			}

			// validates the Amendment amount so that it should not fall
			// below YTD Amount
			if (loIsDataValid && aoRateBeanObj.getId() != null
					&& !aoRateBeanObj.getId().contains(HHSConstants.NEW_ROW_IDENTIFIER))
			{
				loRemainingAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoRateBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.QRY_GET_REMAINING_AMOUNT_RATE_IN_MULTIPLE_AMENDMENTS,
						HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN);
			}
			loRemainingAmount = ((loRemainingAmount == null) ? new BigDecimal(HHSConstants.INT_ZERO)
					: loRemainingAmount);
			if (aoRateBeanObj.getLsModifyAmount() != null
					&& !(((loRemainingAmount.add(new BigDecimal(aoRateBeanObj.getLsModifyAmount())))
							.compareTo(BigDecimal.ZERO)) >= HHSConstants.INT_ZERO))
			{
				loHashMap.put(HHSConstants.IS_VALID_DATA, Boolean.FALSE);
				loHashMap.put(HHSConstants.ERROR_MESSAGE,
						HHSConstants.CBM_LESS_THAN_INVOICE_INCLUDING_PENDING_NEG_AMEND_ERROR_MESSAGE);
			}
		}
		catch (ApplicationException aoAppEx)
		{
			// Any Exception from DAO class will be thrown as Application
			// Exception will be handled over here. It throws Application
			// Exception
			// back to calling method.
			aoAppEx.addContextData(HHSConstants.AO_RATE_BEAN_OBJ, aoRateBeanObj);
			LOG_OBJECT.Error("Exception while validating Amendment data", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService:validateRateAmendmentData method" + " \n");
			throw aoAppEx;
		}
		return loHashMap;
	}

	/**
	 * This method is used to perform error check for positive and negative
	 * amendment on server side
	 * 
	 * @param aoCBGridBean CBGridBean as input
	 * @param asAmendmentAmount amendment amount as input
	 * @return loMap flag as key , exception as vaue map
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	private Map<Boolean, ApplicationException> amendmentAmountCheck(CBGridBean aoCBGridBean, String asAmendmentAmount)
			throws ApplicationException
	{
		Map<Boolean, ApplicationException> loMap = new HashMap<Boolean, ApplicationException>();
		// Validation for positive amendment.
		if (aoCBGridBean.getAmendmentType().equalsIgnoreCase(HHSConstants.POSITIVE)
				&& new BigDecimal(asAmendmentAmount).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO)
		{
			loMap.put(
					Boolean.TRUE,
					new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.POSITIVE_AMENDMENT_MSG)));
		}
		// Validation for negative amendment.
		else if (aoCBGridBean.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE)
				&& new BigDecimal(asAmendmentAmount).compareTo(BigDecimal.ZERO) > HHSConstants.INT_ZERO)
		{
			loMap.put(
					Boolean.TRUE,
					new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.NEGATIVE_AMENDMENT_MSG)));
		}
		return loMap;
	}

	/**
	 * <p>
	 * This method fetches Utility Details of Amendment Budget from DB.<br/>
	 * <ul>
	 * <li>1.Get all Utility details in CBGridBean Bean to fetch data.</li>
	 * <li>2.fetchUtilitiesModifyDetails query is executed to fetch Utility
	 * details which are displayed on Utility Grid.</li>
	 * </ul>
	 * </p>
	 * <ul>
	 * <li>Calls query 'fetchUtilitiesAmendmentDetails'</li>
	 * </ul>
	 * @param aoMybatisSession Sql session object as input.
	 * @param aoCBGridBeanObj CBGridBean Bean as input.
	 * @return loCBUtilities loCBUtilities Bean as output with all Utility
	 *         related Information.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<CBUtilities> fetchUtilitiesAmendment(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj,
			MasterBean aoMasterBean) throws ApplicationException
	{
		List<CBUtilities> loCBUtilities = null;
		aoCBGridBeanObj.setEntryTypeId(HHSConstants.THREE);
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);
		try
		{
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetTypeId().equals(HHSConstants.ONE)
					&& aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBUtilities = fetchUtilitiesDataFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{
				loCBUtilities = (List<CBUtilities>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.CBY_FETCH_UTILITIES_AMENDMENT_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			}

		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException loExp)
		{
			setMoState("Error occured while fetching Utility Details for budget id: "
					+ aoCBGridBeanObj.getContractBudgetID() + " and Sub-Budget id : "
					+ aoCBGridBeanObj.getSubBudgetID());
			loExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBGridBeanObj.getSubBudgetID());
			loExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_BUDGET, aoCBGridBeanObj.getContractBudgetID());
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: fetchUtilities method:: ", loExp);
			throw loExp;
		}
		return loCBUtilities;
	}

	/**
	 * The method is updated to Release 7. Accessibility is changed to public to
	 * re-use method for approved modifications.
	 * This method fetch Utilities list from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List<CBUtilities>
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public List<CBUtilities> fetchUtilitiesDataFromXML(String asSubBudgetId, MasterBean aoMasterBean)
			throws ApplicationException
	{
		List<CBUtilities> loReturnedList = null;
		loReturnedList = generateUtilitiesList(asSubBudgetId, aoMasterBean);
		return loReturnedList;
	}

	/**
	 * This method fetch consolidated list of Utilities which includes all type
	 * of Employees from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List<CBUtilities>
	 * @throws ApplicationException If an Application Exception occurs
	 */

	private List<CBUtilities> generateUtilitiesList(String asSubBudgetId, MasterBean aoMasterBean)
			throws ApplicationException
	{
		List<LineItemMasterBean> loMasterBeanList = null;
		List<CBUtilities> loReturnedList = null;

		loMasterBeanList = aoMasterBean.getMasterBeanList();
		Iterator<LineItemMasterBean> aoListIterator = loMasterBeanList.iterator();
		while (aoListIterator.hasNext())
		{
			LineItemMasterBean loLineItemBean = aoListIterator.next();
			if (loLineItemBean.getSubbudgetId().equals(asSubBudgetId))
			{
				loReturnedList = loLineItemBean.getUtilityBeanList();
			}
		}
		return loReturnedList;
	}

	/**
	 * <p>
	 * This method updates Utility details for Amendment Budget in DB.<br/>
	 * <ul>
	 * <li>1.Get all Utility details in CBUtilities Bean to update data.</li>
	 * <li>2.If "Positive Amendment", validation to check that the amount
	 * entered is a positive number</li>
	 * <li>3.If "Negative Amendment", validation to check that the amount
	 * entered is a negative number</li>
	 * <li>4.If "Negative Amendment" then validation to check that Amendment
	 * Amount entered from Utilities Grid would not cause the Total Proposed
	 * Budget for the line item to fall below the YTD Invoiced Amount and also
	 * taking in to consideration ? Negative Amendment amounts entered for the
	 * utilities line item in other pending Amendments for which no budgets have
	 * been approved.</li>
	 * <li>5.mergeUpdateUtilities method is called to update the Amendment
	 * Amount entered from grid in DB of an already existing Utility line item
	 * or insert a new row in case no row exists for the respective line item.</li>
	 * </ul>
	 * </p>
	 * <ul>
	 * <li>Calls query 'fetchUtilitiesDetailsForValidationInMultipleAmendments'</li>
	 * </ul>
	 * @param aoMybatiSession Sql session object
	 * @param aoCBUtilities CBUtilities Bean Object
	 * @return lbUpdateStatus true if sql query execute without exception/false
	 *         if query fails.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public boolean updateUtilitiesAmendment(SqlSession aoMybatiSession, CBUtilities aoCBUtilities)
			throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		Boolean loError = false;
		try
		{
			// Validation for positive amendment (For Positive Amendment only.)
			if (aoCBUtilities.getAmendmentType().equalsIgnoreCase(HHSConstants.POSITIVE)
					&& (new BigDecimal(aoCBUtilities.getLineItemModifiedAmt()).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO))
			{
				loError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.POSITIVE_AMENDMENT_MSG));
			}
			// Validation for negative amendment (For Negative Amendment only)
			else if (aoCBUtilities.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE)
					&& (new BigDecimal(aoCBUtilities.getLineItemModifiedAmt()).compareTo(BigDecimal.ZERO) > HHSConstants.INT_ZERO))
			{
				loError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.NEGATIVE_AMENDMENT_MSG));
			}
			if (aoCBUtilities.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE))
			{
				CBUtilities loCBUtilities = null;
				// GET AMOUNT DETAILS FOR SERVER SIDE VALIDATION
				loCBUtilities = (CBUtilities) DAOUtil.masterDAO(aoMybatiSession, aoCBUtilities,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.CBM_FETCH_UTILITIES_DETAILS_FOR_VALIDATION_IN_MULTIPLE_AMENDMENT,
						HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_UTILITIES);
				// CHECK IF MODIFICATION/AMENDED AMOUNT IS NOT LESS THAN ALREADY
				// INVOICED AMOUNT
				if ((new BigDecimal(loCBUtilities.getRemainingAmt()).add(new BigDecimal(aoCBUtilities
						.getLineItemModifiedAmt()))).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO)
				{
					loError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.CBM_LESS_THAN_INVOICE_INCLUDING_PENDING_NEG_AMEND_ERROR_MESSAGE));
				}
			}
			// Update/Insert Modification Row
			mergeUpdateUtilities(aoMybatiSession, aoCBUtilities);

			lbUpdateStatus = true;
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException loExp)
		{
			if (loError)
			{
				loExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loExp.toString());
			}
			else
			{
				loExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBUtilities.getSubBudgetID());
				loExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_BUDGET, aoCBUtilities.getContractBudgetID());
				LOG_OBJECT.Error("Exception occured in ContractBudgetService: updateUtilities method:: ", loExp);
				setMoState("Exception occured while updating Utilities for budget id: "
						+ aoCBUtilities.getContractBudgetID() + " and Sub-Budget id : "
						+ aoCBUtilities.getSubBudgetID());
			}
			throw loExp;
		}
		// Catch any Null pointer exception thrown from the code and wrap it
		// into application
		// Exception and throw it forward
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Null Pointer Exception occured in ContractBudgetService: updateUtilities method:: ", loExp);
			setMoState("Exception occured while updating Utilities for budget id: "
					+ aoCBUtilities.getContractBudgetID() + " and Sub-Budget id : " + aoCBUtilities.getSubBudgetID());
			loAppEx.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBUtilities.getSubBudgetID());
			loAppEx.addContextData(HHSConstants.ADD_CONTEXT_DATA_BUDGET, aoCBUtilities.getContractBudgetID());
			LOG_OBJECT.Error("Null Pointer Exception occured in ContractBudgetService: updateUtilities method:: ",
					loExp);
			throw loAppEx;
		}
		return lbUpdateStatus;
	}

	/**
	 * <p>
	 * This method updates the Amendment Amount entered from Utility Grid in DB
	 * of an already existing Utility line item or insert a new row in case no
	 * row exists for the respective line item.<br/>
	 * <ul>
	 * <li>1. updateUtilitiesModifyDetails query is called to update amendment
	 * amount in DB.
	 * <li>2. If 0 rows are returned from updateUtilitiesModifyDetails query the
	 * insertUtilitiesModifyDetails is called to insert a new row record in DB.
	 * </p>
	 * <ul>
	 * <li>Calls query 'updateUtilitiesModifyDetails'</li>
	 * <li>Calls query 'fetchUtilitiesTypeId'</li>
	 * <li>Calls query 'fetchApprovedBudgetAmnt'</li>
	 * </ul>
	 * @param aoMybatiSession Sql session object
	 * @param aoCBUtilities CBUtilities Bean Object
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	private void mergeUpdateUtilities(SqlSession aoMybatiSession, CBUtilities aoCBUtilities)
			throws ApplicationException
	{
		int liUpdateCount = HHSConstants.INT_ZERO;
		// Update Modified Amount of Line Item for Modified
		// SubBudget
		liUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatiSession, aoCBUtilities,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_UPDATE_UTILITIES_MODIFY_DETAILS,
				HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_UTILITIES);

		// If modification row of Line Item does not exist then
		// insert a new modification row.
		if (liUpdateCount < HHSConstants.INT_ONE)
		{
			// Fetch Utility type Id and FyBudgetAmount of Base line item row
			aoCBUtilities.setUtilitiesTypeID((String) DAOUtil.masterDAO(aoMybatiSession, aoCBUtilities,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_FETCH_UTILITIES_TYPE_ID,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_UTILITIES));
			aoCBUtilities.setPrevApprovedBudget((String) DAOUtil.masterDAO(aoMybatiSession, aoCBUtilities,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_APPROVED_BUDGET_AMNT,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_UTILITIES));
			DAOUtil.masterDAO(aoMybatiSession, aoCBUtilities, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_INSERT_UTILITIES_MODIFY_DETAILS, HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_UTILITIES);
		}
	}

	/**
	 * <p>
	 * This method fetch Indirect Rate details from DB on the basis of budget
	 * type.<br/>
	 * <b>Functionalities by below method are :</b>
	 * <ul>
	 * <li>1.Get all Indirect Rate details in CBIndirectRateBean Bean to fetch
	 * data.</li>
	 * <li>1.Call fetchIndirectRateModification query for Budget Amendment</li>
	 * </ul>
	 * </p>
	 * <ul>
	 * <li>Calls query 'fetchIndirectRateAmendmentNewRecord'</li>
	 * <li>Calls query 'fetchIndirectRateModification'</li>
	 * </ul>
	 * @param aoMybatisSession Sql session object as input.
	 * @param aoIndirectRate CBGridBean Bean as input.
	 * @param aoMasterBean MasterBean Bean as input.
	 * @return loIndirectRate CBIndirectRateBean Bean as output with all
	 *         Indirect rate related Information.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<CBIndirectRateBean> fetchIndirectRate(SqlSession aoMybatisSession, CBGridBean aoIndirectRate,
			MasterBean aoMasterBean) throws ApplicationException
	{
		List<CBIndirectRateBean> loIndirectRate = null;
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);
		try
		{
			// Fetch the data from FileNet XML if Budget Status is approved
			if (aoMasterBean != null && aoIndirectRate.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoIndirectRate.getSubBudgetID();
				List<LineItemMasterBean> loMasterBeanList = null;

				loMasterBeanList = aoMasterBean.getMasterBeanList();
				Iterator<LineItemMasterBean> loListIterator = loMasterBeanList.iterator();
				while (loListIterator.hasNext())
				{
					LineItemMasterBean loLineItemBean = loListIterator.next();
					if (loLineItemBean.getSubbudgetId().equals(lsSubBudgetId))
					{
						loIndirectRate = loLineItemBean.getIndirectBeanList();
					}
				}
			}
			else
			{
				if (aoIndirectRate != null)
				{
					aoIndirectRate.setInvoiceStatusIdList(HHSConstants.INVOICE_STATUS_ID_INDIRECT_RATE);
					aoIndirectRate.setEntryTypeId(HHSConstants.STRING_TEN);
					if (aoIndirectRate.getSubBudgetID().equalsIgnoreCase(aoIndirectRate.getParentSubBudgetId()))
					{
						loIndirectRate = (List<CBIndirectRateBean>) DAOUtil.masterDAO(aoMybatisSession, aoIndirectRate,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
								HHSConstants.CBY_FETCH_INDIRECT_RATE_AMENDMENT_NEW_RECORD,
								HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
					}
					else
					{
						loIndirectRate = (List<CBIndirectRateBean>) DAOUtil.masterDAO(aoMybatisSession, aoIndirectRate,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
								HHSConstants.CBY_FETCH_INDIRECT_RATE_MODIFICATION,
								HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
					}
					setMoState("Success while fetching Indirect Rate for business type id "
							+ aoIndirectRate.getBudgetTypeId());
				}
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loExp)
		{
			// Set the state, added context data and added error log if any
			// application exception occurs.
			setMoState("error occured while fetching Indirect Rate for business type id "
					+ aoIndirectRate.getBudgetTypeId());
			loExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoIndirectRate.getSubBudgetID());
			loExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoIndirectRate.getContractBudgetID());
			LOG_OBJECT.Error("error occured while fetching Indirect Rate ", loExp);
			throw loExp;
		}
		return loIndirectRate;
	}

	/**
	 * This method generates the MasterBean object for a particular Amendment
	 * Budget, which will be further converted to XML and will be stored in
	 * FileNet db. The method is invoked when Contract Budget Amendment review
	 * task is in final approval stage.
	 * <ul>
	 * <li>Calls query 'fetchParentBudgetId'</li>
	 * <li>Calls query 'fetchSubBudgetDetails'</li>
	 * <li>Calls query 'fetchSubBudgetDetails'</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoFinalFinish Boolean
	 * @param aoTaskDetailsBean TaskDetailsBean
	 * @param aoUserSession P8UserSession
	 * @return lsConvertedXml XML String
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public String generateAmendmentBudgetData(SqlSession aoMyBatisSession, Boolean aoFinalFinish,
			TaskDetailsBean aoTaskDetailsBean, P8UserSession aoUserSession) throws ApplicationException
	{
		String lsAmendmentBudgetId = null;
		String lsConvertedXml = null;
		String lsDocId = null;
		FileInputStream loFIS = null;
		HashMap<String, Object> loReturnMap = new HashMap<String, Object>();

		try
		{
			if (aoFinalFinish)
			{
				List<HashMap<String, Object>> loSubBudgetDetails;
				List<LineItemMasterBean> loLineItemList = new ArrayList<LineItemMasterBean>();
				MasterBean loMasterBean = new MasterBean();
				ContractBudgetService loCBService = new ContractBudgetService();
				P8ContentService loP8Service = new P8ContentService();
				Map loHashMap = new HashMap();

				lsAmendmentBudgetId = aoTaskDetailsBean.getBudgetId();
				loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsAmendmentBudgetId);
				// Fetching Parent base budget id of Amendment Budget
				String lsParentBudgetId = (String) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_PARENT_BUDGET_ID, HHSConstants.JAVA_UTIL_MAP);

				// Fetching list of subbudget details
				loSubBudgetDetails = (List<HashMap<String, Object>>) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.FETCH_SUB_BUDGET_DETAILS, HHSConstants.JAVA_UTIL_MAP);

				// Fetching ststus of Amendment Budget
				String lsBudgetStatusId = (String) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.FETCH_AMENDMENT_BUDGET_STATUS, HHSConstants.JAVA_UTIL_MAP);

				CBGridBean loCBGridBean = new CBGridBean();
				loCBGridBean.setAmendmentContractID(aoTaskDetailsBean.getContractId());
				loCBGridBean.setContractBudgetID(lsAmendmentBudgetId);
				loCBGridBean.setParentBudgetId(lsParentBudgetId);
				loCBGridBean.setBudgetStatusId(lsBudgetStatusId);
				loCBGridBean.setBudgetTypeId(HHSConstants.ONE);
				// For each subbudget, generate LineItemMasterBean
				Iterator<HashMap<String, Object>> aoListIterator = loSubBudgetDetails.iterator();
				loMasterBean.setBudgetId(lsAmendmentBudgetId);

				while (aoListIterator.hasNext())
				{
					HashMap<String, Object> loInnerHashMap = aoListIterator.next();
					String lsAmendmentSubBudgetId = loInnerHashMap.get(HHSConstants.CBM_SUB_BUDGET_ID).toString();
					String lsParentSubBudgetId = loInnerHashMap.get(HHSConstants.CBA_PARENT_ID).toString();
					loCBGridBean.setSubBudgetID(lsAmendmentSubBudgetId);
					loCBGridBean.setParentSubBudgetId(lsParentSubBudgetId);
					//Added in R7 to reset entry type Id while iterating sub busget list
					loCBGridBean.setEntryTypeId(null);
					//R7 End
					LineItemMasterBean loLineItemBean = generateMasterBean(aoMyBatisSession, loCBGridBean, loCBService);
					loLineItemList.add(loLineItemBean);
				}
				// Set data into MasterBean
				loMasterBean.setMasterBeanList(loLineItemList);

				// Start : Amendment Preserve for Fiscal, Advance, Assignment
				loMasterBean.setBudgetDetails(fetchFyBudgetSummary(aoMyBatisSession,
						(HashMap<String, String>) loHashMap, null, loCBGridBean));
				loMasterBean.setAdvanceSummaryBean(loCBService
						.fetchAdvanceDetails(aoMyBatisSession, loCBGridBean, null));
				loMasterBean.setAssignmentsSummaryBean(loCBService.fetchAssignmentSummary(aoMyBatisSession,
						loCBGridBean, null));
				// End : Amendment Preserve for Fiscal, Advance, Assignment

				// Convert MasterBean object to XML String
				lsConvertedXml = convertMasterListToXml(loMasterBean);

				// Convert XML String to FileInputStream object
				loFIS = HHSUtil.convertXmlToStream(lsConvertedXml);

				// Uplaod XML documnent to FileNet
				HashMap loFileNetHashMap = new HashMap();
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_DOC_TYPE, HHSConstants.BUDGET_DOC_TYPE);
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, HHSConstants.XML_DOC_TITLE
						+ lsAmendmentBudgetId);
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID, aoTaskDetailsBean.getUserId());
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_ORGANIZATION_ID, HHSConstants.USER_AGENCY);
				loFileNetHashMap.put(HHSR5Constants.Org_Id, aoTaskDetailsBean.getUserId());
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, Boolean.FALSE);
				loFileNetHashMap.put(P8Constants.MIME_TYPE, HHSConstants.XML_MIME_TYPE);
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_FILE_TYPE, HHSConstants.XML_FILE_TYPE);
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY, aoTaskDetailsBean.getUserId());
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, aoTaskDetailsBean.getUserId());

				loReturnMap = loP8Service.createDVdocument(aoUserSession, loFIS, loFileNetHashMap, false, false);
				lsDocId = (String) loReturnMap.get(HHSConstants.DOC_ID);

				// putting doc-id returned from Filenet against Amendment budget
				loHashMap.put(HHSConstants.DOC_ID, lsDocId);
				Integer loUpdateCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.UPDATE_BUDGET_WITH_DOC_ID, HHSConstants.JAVA_UTIL_MAP);
				if (loUpdateCount != 1)
				{
					throw new ApplicationException("DocumentId of XML not inserted successfully for BudgetId : "
							+ lsAmendmentBudgetId);
				}
			}
		}
		// catch any application exception thrown from the code due to SELECT
		// statement failure and throw it
		// forward
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData(
					"ApplicationException occured while generating Amendment Budget data :  generateAmendmentBudgetData",
					aoExp);
			LOG_OBJECT
					.Error("ApplicationException occured while generating Amendment Budget data : generateAmendmentBudgetData "
							+ aoExp);
			setMoState("ApplicationException occured while generating Amendment Budget data for budget id = "
					+ lsAmendmentBudgetId);
			throw aoExp;
		}
		// Catch any Null pointer exception thrown from the code and wrap it
		// into application
		// Exception and throw it forward
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while generating Amendment Budget data :  generateAmendmentBudgetData", aoExp);
			loAppEx.addContextData(
					"Exception occured while generating Amendment Budget data :  generateAmendmentBudgetData", aoExp);
			LOG_OBJECT.Error("Exception occured while generating Amendment Budget data : generateAmendmentBudgetData "
					+ aoExp);
			setMoState("Exception occured while generating Amendment Budget data for budget id = "
					+ lsAmendmentBudgetId);
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
				ApplicationException loAppEx = new ApplicationException(
						"Exception occured while closing the FileInputStream in finally block :  generateAmendmentBudgetData",
						loExp);
				throw loAppEx;
			}
		}
		return lsDocId;
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
	private LineItemMasterBean generateMasterBean(SqlSession aoMyBatisSession, CBGridBean aoCBGridBean,
			ContractBudgetService aoCBService) throws ApplicationException
	{
		List<CBProgramIncomeBean> loProgramincomeBeanList = fetchProgramIncomeAmendment(aoCBGridBean, aoMyBatisSession,
				null);
		List<SiteDetailsBean> loSiteDetailsBean = fetchSubBudgetSiteDetails(aoMyBatisSession, aoCBGridBean, null, false);
		List<PersonnelServiceBudget> loPersonnelServiceEmployee = generatePersonnelService(aoMyBatisSession,
				aoCBGridBean);
		List<RateBean> loRateBeanList = fetchContractBudgetAmendmentRate(aoMyBatisSession, aoCBGridBean, null);
		// seeting entry type id
		List<CBMileStoneBean> loMilestoneBeanList = fetchMilestone(aoCBGridBean, aoMyBatisSession, null); 
		aoCBGridBean.getEntryTypeId();  
		List<CBEquipmentBean> loEquipmentBeanList = fetchAmendmentOTPSEquipment(aoMyBatisSession, aoCBGridBean, null);
		List<CBIndirectRateBean> loIndirectBeanList = fetchIndirectRate(aoMyBatisSession, aoCBGridBean, null);
		List<CBOperationSupportBean> loOpsBeanList = fetchAmendmentOTPS(aoMyBatisSession, aoCBGridBean, null);
		List<CBProfessionalServicesBean> loProfserviceBeanList = fetchProfServicesDetailsAmendment(aoCBGridBean,
				aoMyBatisSession, null);

		List<CBUtilities> loUtilityBeanList = fetchUtilitiesAmendment(aoMyBatisSession, aoCBGridBean, null);
		List<Rent> loRentBeanList = fetchAmendmentRent(aoMyBatisSession, aoCBGridBean, null);
		List<UnallocatedFunds> loUnallocatedBeanList = fetchAmendmentUnallocatedFunds(aoMyBatisSession, aoCBGridBean, null);     
		List<ContractedServicesBean> loContractedserviceBeanList = generateContractedServices(aoMyBatisSession,
				aoCBGridBean);
		// Start:Added in R7 for Cost-Center
		List<CBServicesBean> loServicesBeanList = new ContractBudgetModificationService()
				.fetchContractServicesModificationGrid(aoCBGridBean, aoMyBatisSession, null);
		List<CBServicesBean> loCostCenterBeanList = new ContractBudgetModificationService()
				.fetchContractCostCenterModificationGrid(aoCBGridBean, aoMyBatisSession, null);
		// End: Added in R7 for Cost-Center
		String lsIndirectRatePercent = aoCBService.updateIndirectRatePercentage(aoMyBatisSession, aoCBGridBean, null);
		// added in R7: Update PI Indirect Rate percent
		String lsIndirectPIRatePercent = aoCBService.updatePIIndirectRatePercentage(aoMyBatisSession, aoCBGridBean,
				null);
		// End in R7: Update PI Indirect Rate percent
		PersonnelServicesData loNonGridPSData = aoCBService.fetchPersonnelServiceData(aoMyBatisSession, aoCBGridBean,
				null);
		CBOperationSupportBean loNonGridOPSData = fetchOpAndSupportAmendPageData(aoCBGridBean, aoMyBatisSession, null);
		ContractedServicesBean loNonGridConServiceData = fetchNonGridContractedServicesAmendment(aoMyBatisSession,
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
	 * This method will merge list of Salaried, Hourly, Seasonal and Fringe
	 * Employees into a single List which is required to be stored in XML of
	 * amendment budget.
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
		List<PersonnelServiceBudget> loSalariedEmployee = fetchSalariedEmployeeBudgetForAmendment(aoMyBatisSession,
				aoCBGridBean, null);
		List<PersonnelServiceBudget> loHourlyEmployee = fetchHourlyEmployeeBudgetForAmendment(aoMyBatisSession,
				aoCBGridBean, null);
		List<PersonnelServiceBudget> loSeasonalEmployee = fetchSeasonalEmployeeBudgetForAmendment(aoMyBatisSession,
				aoCBGridBean, null);
		List<PersonnelServiceBudget> loFringeEmployee = fetchFringeBenefitsForAmendment(aoMyBatisSession, aoCBGridBean,
				null);

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
	 * This method will merge list of Consultants, Sub-Contractors and Vendors
	 * into a single List which is required to be stored in XML of amendment
	 * budget.
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
		List<ContractedServicesBean> loContractedServicesForConsultants = fetchContractedServicesAmendmentConsultants(
				aoMyBatisSession, aoCBGridBean, null);
		List<ContractedServicesBean> loContractedServicesForSubContractor = fetchContractedServicesAmendmentSubContractors(
				aoMyBatisSession, aoCBGridBean, null);
		List<ContractedServicesBean> loContractedServicesForVendor = fetchContractedServicesAmendmentVendors(
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
	 * This method fetch Document Id corresponding to a BudgetId
	 * <ul>
	 * <li>Calls query 'fetchDocIdForBudget'</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession object
	 * @param asBudgetId String
	 * @return lsDocId String
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private String fetchDocIdOfBudget(SqlSession aoMybatisSession, String asBudgetId) throws ApplicationException
	{
		String lsDocId = (String) DAOUtil.masterDAO(aoMybatisSession, asBudgetId,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER, HHSConstants.FETCH_DOC_ID_OF_BUDGET,
				HHSConstants.JAVA_LANG_STRING);
		return lsDocId;

	}

	/**
	 * The method is updated to Release 7. Accessibility is changed to public to
	 * re-use method for approved modifications. 
	 * This method fetch Salaried Employee list from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List<PersonnelServiceBudget>
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public List<PersonnelServiceBudget> fetchSalariedEmployeeFromXML(String asSubBudgetId, MasterBean aoMasterBean)
			throws ApplicationException
	{
		List<PersonnelServiceBudget> loReturnedList = null;
		loReturnedList = generatePersonnelServiceList(asSubBudgetId, aoMasterBean);
		loReturnedList = fetchListForSalaried(loReturnedList);
		return loReturnedList;
	}

	/**
	 * The method is updated to Release 7. Accessibility is changed to public to
	 * re-use method for approved modifications. 
	 * This method fetch Salaried Employee list from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List<PersonnelServiceBudget>
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public List<UnallocatedFunds> fetchUnallocatedFundsFromXML(String asSubBudgetId, MasterBean aoMasterBean)
			throws ApplicationException
	{
		List<UnallocatedFunds> loReturnedList = null;
		List<LineItemMasterBean> loMasterBeanList = aoMasterBean.getMasterBeanList();
		for (LineItemMasterBean loLineItemMasterObj : loMasterBeanList)
		{
			if (loLineItemMasterObj.getSubbudgetId().equals(asSubBudgetId))
			{
				loReturnedList = loLineItemMasterObj.getUnallocatedBeanList();
			}
		}
		return loReturnedList;
	}
  

	/**
	 * This method fetch records of only type Salaried from the consolidated
	 * list
	 * 
	 * @param aoReturnedList List<PersonnelServiceBudget>
	 * @return loReturnedList List<PersonnelServiceBudget>
	 */
	private List<PersonnelServiceBudget> fetchListForSalaried(List<PersonnelServiceBudget> aoReturnedList)
			throws ApplicationException
	{
		List<PersonnelServiceBudget> loReturnedList = null;
		if (aoReturnedList != null)
		{
			Iterator<PersonnelServiceBudget> aoListIterator = aoReturnedList.iterator();
			loReturnedList = new ArrayList<PersonnelServiceBudget>();
			while (aoListIterator.hasNext())
			{
				PersonnelServiceBudget loPSBean = aoListIterator.next();
				if (loPSBean.getEmpType().equals(HHSConstants.ONE))
				{
					loReturnedList.add(loPSBean);
				}
			}
		}
		return loReturnedList;

	}

	/**
	 * The method is updated to Release 7. Accessibility is changed to public to
	 * re-use method for approved modifications.
	 * This method fetch Hourly Employee list from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List<PersonnelServiceBudget>
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public List<PersonnelServiceBudget> fetchHourlyEmployeeFromXML(String asSubBudgetId, MasterBean aoMasterBean)
			throws ApplicationException
	{
		List<PersonnelServiceBudget> loReturnedList = null;
		loReturnedList = generatePersonnelServiceList(asSubBudgetId, aoMasterBean);
		loReturnedList = fetchListForHourly(loReturnedList);
		return loReturnedList;
	}

	/**
	 * This method fetch records of only type Hourly from the consolidated list
	 * 
	 * @param aoReturnedList List<PersonnelServiceBudget>
	 * @return loReturnedList List<PersonnelServiceBudget>
	 */
	private List<PersonnelServiceBudget> fetchListForHourly(List<PersonnelServiceBudget> aoReturnedList)
			throws ApplicationException
	{
		List<PersonnelServiceBudget> loReturnedList = null;
		if (aoReturnedList != null)
		{
			Iterator<PersonnelServiceBudget> aoListIterator = aoReturnedList.iterator();
			loReturnedList = new ArrayList<PersonnelServiceBudget>();
			while (aoListIterator.hasNext())
			{
				PersonnelServiceBudget loPSBean = aoListIterator.next();
				if (loPSBean.getEmpType().equals(HHSConstants.TWO))
				{
					loReturnedList.add(loPSBean);
				}
			}
		}
		return loReturnedList;

	}

	/**
	 * The method is updated to Release 7. Accessibility is changed to public to
	 * re-use method for approved modifications.
	 * This method fetch Seasonal Employee list from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List<PersonnelServiceBudget>
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public List<PersonnelServiceBudget> fetchSeasonalEmployeeFromXML(String asSubBudgetId, MasterBean aoMasterBean)
			throws ApplicationException
	{
		List<PersonnelServiceBudget> loReturnedList = null;
		loReturnedList = generatePersonnelServiceList(asSubBudgetId, aoMasterBean);
		loReturnedList = fetchListForSeasonal(loReturnedList);
		return loReturnedList;
	}

	/**
	 * This method fetch records of only type Seasonal from the consolidated
	 * list
	 * 
	 * @param aoReturnedList List<PersonnelServiceBudget>
	 * @return loReturnedList List<PersonnelServiceBudget>
	 */
	private List<PersonnelServiceBudget> fetchListForSeasonal(List<PersonnelServiceBudget> aoReturnedList)
			throws ApplicationException
	{
		List<PersonnelServiceBudget> loReturnedList = null;
		if (aoReturnedList != null)
		{
			Iterator<PersonnelServiceBudget> aoListIterator = aoReturnedList.iterator();
			loReturnedList = new ArrayList<PersonnelServiceBudget>();
			while (aoListIterator.hasNext())
			{
				PersonnelServiceBudget loPSBean = aoListIterator.next();
				if (loPSBean.getEmpType().equals(HHSConstants.THREE))
				{
					loReturnedList.add(loPSBean);
				}
			}
		}
		return loReturnedList;

	}

	/**
	 * The method is updated to Release 7. Accessibility is changed to public to
	 * re-use method for approved modifications. 
	 * This method fetch Salaried Employee list from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List<PersonnelServiceBudget>
	 * @throws ApplicationException If an Application Exception occurs
	 */

	public List<PersonnelServiceBudget> fetchFringeEmployeeFromXML(String asSubBudgetId, MasterBean aoMasterBean)
			throws ApplicationException 
	{
		List<PersonnelServiceBudget> loReturnedList = null;
		loReturnedList = generatePersonnelServiceList(asSubBudgetId, aoMasterBean);
		loReturnedList = fetchListForFringe(loReturnedList);
		return loReturnedList;
	}

	/**
	 * This method fetch records of only type Fringe from the consolidated list
	 * 
	 * @param aoReturnedList List<PersonnelServiceBudget>
	 * @return loReturnedList List<PersonnelServiceBudget>
	 */
	private List<PersonnelServiceBudget> fetchListForFringe(List<PersonnelServiceBudget> aoReturnedList)
			throws ApplicationException
	{
		List<PersonnelServiceBudget> loReturnedList = null;
		if (aoReturnedList != null)
		{
			Iterator<PersonnelServiceBudget> aoListIterator = aoReturnedList.iterator();
			loReturnedList = new ArrayList<PersonnelServiceBudget>();
			while (aoListIterator.hasNext())
			{
				PersonnelServiceBudget loPSBean = aoListIterator.next();
				if (loPSBean.getEmpType().equals(HHSConstants.FOUR))
				{
					loReturnedList.add(loPSBean);
				}
			}
		}
		return loReturnedList;

	}

	/**
	 * This method fetch consolidated list of PersonnelServices which includes
	 * all type of Employees from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List<PersonnelServiceBudget>
	 * @throws ApplicationException If an Application Exception occurs
	 */

	private List<PersonnelServiceBudget> generatePersonnelServiceList(String asSubBudgetId, MasterBean aoMasterBean)
			throws ApplicationException
	{
		List<LineItemMasterBean> loMasterBeanList = null;
		List<PersonnelServiceBudget> loReturnedList = null;

		loMasterBeanList = aoMasterBean.getMasterBeanList();
		Iterator<LineItemMasterBean> aoListIterator = loMasterBeanList.iterator();
		while (aoListIterator.hasNext())
		{
			LineItemMasterBean loLineItemBean = aoListIterator.next();
			if (loLineItemBean.getSubbudgetId().equals(asSubBudgetId))
			{
				loReturnedList = loLineItemBean.getPersonnelserviceBeanList();
			}
		}
		return loReturnedList;
	}

	/**
	 * This method first fetches XML document from FileNet and then convert it
	 * into MasterBean object
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asBudgetId String
	 * @param aoP8UserSession P8UserSession
	 * @return loMasterBeanObj MasterBean
	 * @throws ApplicationException If an Application Exception occurs
	 */

	@SuppressWarnings("rawtypes")
	public MasterBean generateMasterBeanObjectFromXML(SqlSession aoMybatisSession, String asBudgetId,
			P8UserSession aoP8UserSession) throws ApplicationException
	{
		InputStream loContent = null;

		// Fetching XML docId of budget
		String lsDocId = fetchDocIdOfBudget(aoMybatisSession, asBudgetId);

		if (null == lsDocId)
		{
			return null;
		}

		// Call FileNet Service to fetch XML String...
		HashMap loDocumentMap = MOP8_CONTENT_SERVICE.getDocumentContent(aoP8UserSession, lsDocId);
		loContent = (InputStream) loDocumentMap.get(HHSConstants.CONTENT_ELEMENT);

		// Converting Input Stream returned from FileNet to String XML
		String lsReturnedXml = HHSUtil.convertInputStreamToXml(loContent);
		// Unmarshalling the String XML to MasterBean object
		MasterBean loMasterBeanObj = (MasterBean) HHSUtil.unmarshalObject(lsReturnedXml);
		return loMasterBeanObj;
	}

	/**
	 * The method is updated to Release 7. Accessibility is changed to public to
	 * re-use method for approved modifications. 
	 * This method fetch approved amendment Professional service details list
	 * from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List<CBProfessionalServicesBean>
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public List<CBProfessionalServicesBean> fetchProfessionalServiceListFromXML(String asSubBudgetId,
			MasterBean aoMasterBean) throws ApplicationException
	{
		List<LineItemMasterBean> loMasterBeanList = null;
		List<CBProfessionalServicesBean> loReturnedList = null;

		loMasterBeanList = aoMasterBean.getMasterBeanList();
		Iterator<LineItemMasterBean> aoListIterator = loMasterBeanList.iterator();
		while (aoListIterator.hasNext())
		{
			LineItemMasterBean loLineItemBean = aoListIterator.next();
			if (loLineItemBean.getSubbudgetId().equals(asSubBudgetId))
			{
				loReturnedList = loLineItemBean.getProfserviceBeanList();
			}
		}
		return loReturnedList;
	}

	/**
	 * The method is updated to Release 7. Accessibility is changed to public to
	 * re-use method for approved modifications. 
	 * This method fetch consolidated list of Milestone details from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List of CBMileStoneBean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public List<CBMileStoneBean> generateMilestoneList(String asSubBudgetId, MasterBean aoMasterBean)
			throws ApplicationException
	{
		List<LineItemMasterBean> loMasterBeanList = null;
		List<CBMileStoneBean> loReturnedList = null;

		loMasterBeanList = aoMasterBean.getMasterBeanList();
		Iterator<LineItemMasterBean> aoListIterator = loMasterBeanList.iterator();
		while (aoListIterator.hasNext())
		{
			LineItemMasterBean loLineItemBean = aoListIterator.next();
			if (loLineItemBean.getSubbudgetId().equals(asSubBudgetId))
			{
				loReturnedList = loLineItemBean.getMilestoneBeanList();
			}
		}
		return loReturnedList;
	}

	/**
	 * This method fetch Salaried Employee list from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List<PersonnelServiceBudget>
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public List<CBEquipmentBean> fetchEquipmentFromXML(String asSubBudgetId, MasterBean aoMasterBean)
			throws ApplicationException
	{
		List<CBEquipmentBean> loReturnedList = null;
		List<LineItemMasterBean> loMasterBeanList = null;

		loMasterBeanList = aoMasterBean.getMasterBeanList();
		Iterator<LineItemMasterBean> aoListIterator = loMasterBeanList.iterator();
		while (aoListIterator.hasNext())
		{
			LineItemMasterBean loLineItemBean = aoListIterator.next();
			if (loLineItemBean.getSubbudgetId().equals(asSubBudgetId))
			{
				loReturnedList = loLineItemBean.getEquipmentBeanList();
			}
		}
		return loReturnedList;
	}

	/**
	 * The method is updated to Release 7. Accessibility is changed to public to
	 * re-use method for approved modifications. 
	 * This method fetch Salaried Employee list from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List<PersonnelServiceBudget>
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public List<CBOperationSupportBean> fetchOTPSFromXML(String asSubBudgetId, MasterBean aoMasterBean)
			throws ApplicationException
	{
		List<CBOperationSupportBean> loReturnedList = null;
		List<LineItemMasterBean> loMasterBeanList = null;

		loMasterBeanList = aoMasterBean.getMasterBeanList();
		Iterator<LineItemMasterBean> aoListIterator = loMasterBeanList.iterator();
		while (aoListIterator.hasNext())
		{
			LineItemMasterBean loLineItemBean = aoListIterator.next();
			if (loLineItemBean.getSubbudgetId().equals(asSubBudgetId))
			{
				loReturnedList = loLineItemBean.getOpsBeanList();
			}
		}
		return loReturnedList;
	}

	/**
	 * The method is updated to Release 7. Accessibility is changed to public to
	 * re-use method for approved modifications.
	 * This method fetch Salaried Employee list from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedList List<PersonnelServiceBudget>
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public CBOperationSupportBean fetchOpAndSupportAmendPageDataFromXML(String asSubBudgetId, MasterBean aoMasterBean)
			throws ApplicationException
	{
		CBOperationSupportBean loReturnedList = null;
		List<LineItemMasterBean> loMasterBeanList = null;

		loMasterBeanList = aoMasterBean.getMasterBeanList();
		Iterator<LineItemMasterBean> aoListIterator = loMasterBeanList.iterator();
		while (aoListIterator.hasNext())
		{
			LineItemMasterBean loLineItemBean = aoListIterator.next();
			if (loLineItemBean.getSubbudgetId().equals(asSubBudgetId))
			{
				loReturnedList = loLineItemBean.getNonGridOPSData();
			}
		}
		return loReturnedList;
	}

	/**
	 * The method is updated to Release 7. Accessibility is changed to public to
	 * re-use method for approved modifications.
	 * This method converts MasterBean to XML String
	 * 
	 * @param aoMasterBean MasterBean object
	 * @return lsConvertedXml String
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public String convertMasterListToXml(MasterBean aoMasterBean) throws ApplicationException
	{
		Document loDoc = HHSUtil.marshalObject(aoMasterBean);

		String lsConvertedXml = HHSUtil.convertDocumentToXML(loDoc);

		return lsConvertedXml;

	}


	// Added in R7 for Program Income
	/**
	 * The method is added in Release 7. 
	 * This method is used to insert a row in program income table.
	 * @param aoMyBatisSession SqlSession object
	 * @param aoCBGridBeanObj CBProgramIncomeBean object
	 * @return boolean insertion status
	 * @throws ApplicationException
	 */
	public Boolean addProgramIncomeAmendment(SqlSession aoMybatisSession, CBProgramIncomeBean aoCBGridBeanObj)
			throws ApplicationException
	{

		boolean lbInsertStatus = false;

		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,HHSR5Constants.INSERT_PI_AMENDMENT,
					HHSConstants.PROGRAM_INCOME_BEAN);

			lbInsertStatus = true;

		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
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

	// This method is added in R7 to append new record with ID when new row is
	// added in PI grid during amendment
	private void setAmendmentPIDetailsBean(List<CBProgramIncomeBean> aoBaseProgramIncomeList,
			List<CBProgramIncomeBean> aoProgramIncomeAmendList) throws ApplicationException
	{
		if (aoProgramIncomeAmendList != null && !aoProgramIncomeAmendList.isEmpty())
		{
			 /*[Start] QC_9153 add null exception handling*/  
			if (aoBaseProgramIncomeList != null && !aoBaseProgramIncomeList.isEmpty()){
				for (CBProgramIncomeBean loPIBase : aoBaseProgramIncomeList)
				{
					for (CBProgramIncomeBean loPIAmend : aoProgramIncomeAmendList)
					{
						if (loPIBase.getId().equals(loPIAmend.getId()))
						{
							loPIBase.setId(loPIAmend.getId() + HHSConstants.NEW_RECORD_CONTRACT_SERVICES);
						}
					}
				}
			}
			 /*[End] QC_9153 add null exception handling*/  
		}
	}

	//R7: Added for copying value of description of base row in amendment row
	private CBProgramIncomeBean updateProgramIncomeDescription(SqlSession aoMyBatisSession,CBProgramIncomeBean aoCBProgramIncomeBean) throws ApplicationException
	{

		/*[Start] QC_9153 add null exception handling*/  
		//CBProgramIncomeBean loPIForAmendment = null;
		CBProgramIncomeBean loPIForAmendment = new CBProgramIncomeBean();
		Object loPIForAmendmentObj= DAOUtil.masterDAO(aoMyBatisSession, aoCBProgramIncomeBean,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
				HHSR5Constants.FETCH_AMENDMENT_PI_DETAILS, HHSConstants.PROGRAM_INCOME_BEAN);
		if(loPIForAmendmentObj!=null){
			loPIForAmendment = (CBProgramIncomeBean) loPIForAmendmentObj;
		}
		 /*[End] QC_9153 add null exception handling*/  
		// add the bean items received to your bean being
		// updated
		if (null == aoCBProgramIncomeBean.getDescription() || aoCBProgramIncomeBean.getDescription().isEmpty())
		{
			aoCBProgramIncomeBean.setDescription(loPIForAmendment.getDescription());
		}
		return aoCBProgramIncomeBean ; 
	}
	
	// Start : added in R7
		/** This method is called for Cancel out year 
		 *  functionality
		 * @param aoMybatisSession
		 * @param asContractId
		 * @return
		 * @throws ApplicationException
		 */
		public boolean mergeCancelledAmendmentBudget(SqlSession aoMybatisSession, String asContractId, Boolean abMergingFlag)
				throws ApplicationException
		{
			if(!abMergingFlag)
			{
				HashMap loHashMap = new HashMap();
				ContractBudgetModificationService loCBModificationService = new ContractBudgetModificationService();
				loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, asContractId);
				loHashMap.put(HHSConstants.MODIFY_BY, HHSConstants.SYSTEM_USER);
				loCBModificationService.mergeBudgetLineItemsAmendmentNoBudgetEffected(aoMybatisSession, loHashMap,
							true, true);
			}
			
			Boolean lbStatus = false;
			Map<String, String> loParentHashMap = new HashMap<String, String>();
			loParentHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, asContractId);
			String lsParentContractId = (String) DAOUtil.masterDAO(aoMybatisSession, loParentHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
					HHSConstants.FETCH_PARENT_CONTRACT_ID, HHSConstants.JAVA_UTIL_MAP);
			ContractBudgetBean loContractBudgetBean = new ContractBudgetBean();
			loContractBudgetBean.setContractId(lsParentContractId);
			loContractBudgetBean.setAmendmentContractId(asContractId);
			Integer liLastConfiguredFY = (Integer) DAOUtil.masterDAO(aoMybatisSession, lsParentContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.FETCH_LAST_FY_CONFIGURED_AND_NEXT_NEW_FY,
					HHSConstants.JAVA_LANG_STRING);
			liLastConfiguredFY = liLastConfiguredFY + 1;
			loContractBudgetBean.setBudgetfiscalYear(liLastConfiguredFY.toString());
			performAmendmentMerging(aoMybatisSession, loContractBudgetBean);
			DAOUtil.masterDAO(aoMybatisSession, loContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.CS_UPDATE_AMENDMENT_REGISTERED_IN_FMS_BUDGET_STATUS_NEXT_NEW_FY,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			lbStatus = true;
			return lbStatus;
		}
		// End : added in R7
		
		/** This methos is called from mergeCancelledAmendmentBudget
		 * @param aoMybatisSession
		 * @param loContractBudgetBean
		 * @throws ApplicationException
		 */
		private void performAmendmentMerging(SqlSession aoMybatisSession, ContractBudgetBean loContractBudgetBean)
				throws ApplicationException
		{
			@SuppressWarnings("unchecked")
			List<ContractBudgetBean> loBudgetAmendmentRegisteredInFMS = (List<ContractBudgetBean>) DAOUtil.masterDAO(
					aoMybatisSession, loContractBudgetBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
					HHSConstants.BUDGET_AMENDMENT_REGISTERED_FMS, HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			if (loBudgetAmendmentRegisteredInFMS != null && !loBudgetAmendmentRegisteredInFMS.isEmpty())
			{
				for (ContractBudgetBean loBudgetAmendmentRegisteredInFMSBean : loBudgetAmendmentRegisteredInFMS)
				{
					Map<String, String> loHashMap = new HashMap<String, String>();
					loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW,
							loBudgetAmendmentRegisteredInFMSBean.getContractId());
					loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, loBudgetAmendmentRegisteredInFMSBean.getBudgetId());
					loHashMap.put(HHSConstants.MODIFY_BY, loContractBudgetBean.getModifiedByUserId());
					loHashMap.put(HHSConstants.FISCAL_YEAR_ID_KEY, loContractBudgetBean.getBudgetfiscalYear());
					loHashMap.put(HHSConstants.PARENT_CONTRACT_ID, loContractBudgetBean.getContractId());

					// Update Amount for Base Sub_Budget using Amendment Amount
					DAOUtil.masterDAO(aoMybatisSession, loHashMap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBM_UPDATE_BASE_SUBBUDGET_AMOUNT, HHSConstants.JAVA_UTIL_MAP);
					// Update Amount for Base Budget using Amendment Amount
					DAOUtil.masterDAO(aoMybatisSession, loHashMap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBM_UPDATE_BASE_BUDGET_AMOUNT, HHSConstants.JAVA_UTIL_MAP);

					// Logical deletoin of all Modification Sub_Budget
					DAOUtil.masterDAO(aoMybatisSession, loHashMap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBM_DELETE_SUBBUDGET_REPLICA, HHSConstants.JAVA_UTIL_MAP);

					// Logical Deletion of Budget
					DAOUtil.masterDAO(aoMybatisSession, loHashMap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSConstants.CBM_DELETE_BUDGET_REPLICA, HHSConstants.JAVA_UTIL_MAP);

					// merge customization
					FinancialsService loFinancialsService = new FinancialsService();
					HashMap loHMArgs = new HashMap();
					ContractBean loContractBean = new ContractBean();
					loContractBean.setContractId(loBudgetAmendmentRegisteredInFMSBean.getContractId());
					loContractBean.setParentContractId(loContractBudgetBean.getContractId());
					loContractBean.setAmendAffectedBudgetId(loBudgetAmendmentRegisteredInFMSBean.getBudgetId());
					loHMArgs.put(HHSConstants.AO_CONTRACT_BEAN, loContractBean);
					loFinancialsService.mergeBaseBudCustomization(aoMybatisSession, loHMArgs);

					loBudgetAmendmentRegisteredInFMSBean.setModifiedByUserId(HHSConstants.SYSTEM_USER);
					DAOUtil.masterDAO(aoMybatisSession, loBudgetAmendmentRegisteredInFMSBean,
							HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
							HHSConstants.UPDATE_BUDGET_CUSTOMIZATION_ON_NEW_FY_BUDGET,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);

				}
			}
		}
		
		// Start QC 8394 R 7.9.0 add Add/Delete lines functionality to Unallocated funds
		
		/**
		 * <p>
		 * This method amend Unallocated fund details in DB
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
		public boolean deleteAmendmentUnallocatedFunds(SqlSession aoMybatisSession, UnallocatedFunds aoUnallocatedFundsBean ) 
				throws ApplicationException
		{
			boolean lbDeleteStatus = false;
			boolean lbError = false;
			Integer lbStatus = HHSConstants.INT_ZERO;
			try
			{  			   
				DAOUtil.masterDAO(aoMybatisSession, aoUnallocatedFundsBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.AMENDMENT_DELETE_UNALLOCATED_FUNDS, HHSConstants.UNALLOCATED_FUNDS_BEAN);
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

				setMoState("error occured while deleting Amendment unallocated funds for business type id "
						+ aoUnallocatedFundsBean.getBudgetId());
				aoExp.addContextData("Exception occured while deleting Amendment unallocated funds ", aoExp);
				LOG_OBJECT.Error("Transaction Failed:: ContractBudgetAmendmentService:"
						+ "deleteAmendmentUnallocatedFunds method - failed to delete " + aoExp.getMessage() + " \n");
				throw aoExp;
			}
			return lbDeleteStatus;
		}

		/**
		 * <p>
		 * This method amend Unallocated fund details in DB
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
		public boolean addAmendmentUnallocatedFunds(SqlSession aoMybatisSession, UnallocatedFunds aoUnallocatedFundsBean)
				throws ApplicationException
		{
			boolean lbUpdateStatus = false;
			boolean lbError = false;
			try
			{
				
						aoUnallocatedFundsBean.setAmmount(aoUnallocatedFundsBean.getModificationAmount());
						DAOUtil.masterDAO(aoMybatisSession, aoUnallocatedFundsBean,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
								HHSConstants.AMENDMENT_ADD_UNALLOCATED_FUNDS, HHSConstants.UNALLOCATED_FUNDS_BEAN);
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

				setMoState("error occured while adding Amendment unallocated funds for business type id "
						+ aoUnallocatedFundsBean.getBudgetId());
				aoExp.addContextData("Exception occured while adding Amendment unallocated funds ", aoExp);
				LOG_OBJECT.Error("Transaction Failed:: ContractBudgetAmendmentService:"
						+ "addAmendmentUnallocatedFunds method - failed to add " + aoExp.getMessage() + " \n");
				throw aoExp;
			}
			return lbUpdateStatus;
			
		}

		// End QC 8394 R 7.9.0 add Add/Delete lines functionality to Unallocated funds

}
