package com.nyc.hhs.daomanager.service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.AdvanceSummaryBean;
import com.nyc.hhs.model.AssignmentsSummaryBean;
import com.nyc.hhs.model.BudgetDetails;
import com.nyc.hhs.model.BudgetList;
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
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.ContractedServicesBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.LineItemMasterBean;
import com.nyc.hhs.model.MasterBean;
import com.nyc.hhs.model.PersonnelServiceBudget;
import com.nyc.hhs.model.PersonnelServicesData;
import com.nyc.hhs.model.RateBean;
import com.nyc.hhs.model.Rent;
import com.nyc.hhs.model.SiteDetailsBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.model.UnallocatedFunds;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * <p>
 * This service class will be used to fetch all the data for contract budget
 * screens. All render and action methods for Budget Summary, Utilities, OTPS,
 * Rent etc screens will use this service to fetch/insert/update data from/to
 * database. The class is updated in Release 7.
 * </p>
 * 
 */

public class ContractBudgetService extends ServiceState
{

	/**
	 * Logger Object Declared for ContractBudgetService
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(ContractBudgetService.class);

	/**
	 * <p>
	 * This method will hit the ContractBudgetMapper to get all the data for
	 * BudgetSummary screen.
	 * </p>
	 * <ul>
	 * <li>1.Initialize aoCBBudgetSummaryBean</li>
	 * <li>2.Call fetchBudgetSummary method of ContractBudgetMapper to fetch
	 * budget summary</li>
	 * <li>3.Iterate over all the list and check the title of each element</li>
	 * <li>4.Compare the title and populate ContractBudgetSummary</li>
	 * <li>5.Call setTotalAmounts method</li>
	 * <li>6.Catch exception if there is any error fetching budget summary</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Sql session object
	 * @param aoCBGridBeanObj a string object for subBudgetId
	 * @return loCBBudgetSummary- an object of ContractBudgetSummary
	 * @throws ApplicationException Application Exception thrown.
	 */
	@SuppressWarnings("unchecked")
	public ContractBudgetSummary fetchBudgetSummary(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj)
			throws ApplicationException
	{

		ContractBudgetSummary loCBBudgetSummary = new ContractBudgetSummary();
		List<BudgetDetails> loCBBudgetDetailsList = null;
		HashMap<String, String> loContextDataMap = new HashMap<String, String>();
		loContextDataMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoCBGridBeanObj.getContractBudgetID());
		loContextDataMap.put(HHSConstants.SUBBUDGET_ID, aoCBGridBeanObj.getSubBudgetID());
		try
		{
			loCBBudgetDetailsList = (List<BudgetDetails>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_BUDGET_SUMMARY,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			for (BudgetDetails loBudgetDetails : loCBBudgetDetailsList)
			{
				loBudgetDetails.setRemainingAmount((loBudgetDetails.getApprovedBudget().subtract(loBudgetDetails
						.getYtdInvoicedAmount())));

				Map<String, String> loMap = HHSConstants.CONTRACT_BUDGET_SUMMARY_MAP;
				String lsProprty = loMap.get(loBudgetDetails.getTitle());
				BeanUtils.setProperty(loCBBudgetSummary, lsProprty, loBudgetDetails);
			}
			BudgetDetails loCityFundedBudget = (BudgetDetails) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_FETCH_CITY_FUNDED_BUDGET,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			if (loCityFundedBudget == null)
			{
				loCityFundedBudget = new BudgetDetails();
			}
			loCBBudgetSummary.setTotalCityFundedBudget(loCityFundedBudget);
			setTotalAmounts(loCBBudgetSummary);

		}
		// catch any application exception thrown from the code due to SELECT
		// statement and throw it
		// forward
		catch (ApplicationException loAppExp)
		{
			setMoState("Error while fetching Budget Summary for sub budget id:" + aoCBGridBeanObj.getSubBudgetID());
			loAppExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching Budget Summary for sub budget id", loAppExp);
			throw loAppExp;
		}
		// Catch any Null pointer exception thrown from the code and wrap it
		// into application
		// Exception and throw it forward
		catch (Exception loExp)
		{
			setMoState("Error while fetching Budget Summary for sub budget id:" + aoCBGridBeanObj.getSubBudgetID());
			LOG_OBJECT.Error("Error while fetching Budget Summary for sub budget id", loExp);
			throw new ApplicationException("Error while fetching Budget Summary for sub budget id", loExp);
		}
		return loCBBudgetSummary;
	}

	/**
	 * <p>
	 * This method calculates the Total Salary and Fringes, Total OTPS, Total
	 * Directs Costs and Total Program Budget Amount
	 * <ul>
	 * <li>This methods set the ContractBudgetSummary object</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoBudgetSummary an object of ContractBudgetSummary
	 * 
	 */
	private void setTotalAmounts(ContractBudgetSummary aoBudgetSummary)
	{
		aoBudgetSummary.getOperationsSupportAndEquipmentAmount().setApprovedBudget(
				(aoBudgetSummary.getOperationsAndSupportAmount().getApprovedBudget().add(aoBudgetSummary
						.getEquipmentAmount().getApprovedBudget())));
		aoBudgetSummary.getOperationsSupportAndEquipmentAmount().setYtdInvoicedAmount(
				(aoBudgetSummary.getOperationsAndSupportAmount().getYtdInvoicedAmount().add(aoBudgetSummary
						.getEquipmentAmount().getYtdInvoicedAmount())));
		aoBudgetSummary.getOperationsSupportAndEquipmentAmount().setRemainingAmount(
				(aoBudgetSummary.getOperationsSupportAndEquipmentAmount().getApprovedBudget().subtract(aoBudgetSummary
						.getOperationsSupportAndEquipmentAmount().getYtdInvoicedAmount())));
		aoBudgetSummary.getTotalSalaryAndFringesAmount().setApprovedBudget(
				(aoBudgetSummary.getTotalFringes().getApprovedBudget().add(aoBudgetSummary.getTotalSalary()
						.getApprovedBudget())));
		aoBudgetSummary.getTotalSalaryAndFringesAmount().setYtdInvoicedAmount(
				(aoBudgetSummary.getTotalFringes().getYtdInvoicedAmount().add(aoBudgetSummary.getTotalSalary()
						.getYtdInvoicedAmount())));
		aoBudgetSummary.getTotalSalaryAndFringesAmount().setRemainingAmount(
				(aoBudgetSummary.getTotalSalaryAndFringesAmount().getApprovedBudget().subtract(aoBudgetSummary
						.getTotalSalaryAndFringesAmount().getYtdInvoicedAmount())));
		aoBudgetSummary.getTotalOTPSAmount().setApprovedBudget(
				(aoBudgetSummary.getOperationsSupportAndEquipmentAmount().getApprovedBudget()
						.add(aoBudgetSummary.getUtilitiesAmount().getApprovedBudget())
						.add(aoBudgetSummary.getProfessionalServicesAmount().getApprovedBudget())
						.add(aoBudgetSummary.getRentAndOccupancyAmount().getApprovedBudget()).add(aoBudgetSummary
						.getContractedServicesAmount().getApprovedBudget())));
		aoBudgetSummary.getTotalOTPSAmount().setYtdInvoicedAmount(
				(aoBudgetSummary.getOperationsSupportAndEquipmentAmount().getYtdInvoicedAmount()
						.add(aoBudgetSummary.getUtilitiesAmount().getYtdInvoicedAmount())
						.add(aoBudgetSummary.getProfessionalServicesAmount().getYtdInvoicedAmount())
						.add(aoBudgetSummary.getRentAndOccupancyAmount().getYtdInvoicedAmount()).add(aoBudgetSummary
						.getContractedServicesAmount().getYtdInvoicedAmount())));
		aoBudgetSummary.getTotalOTPSAmount().setRemainingAmount(
				(aoBudgetSummary.getTotalOTPSAmount().getApprovedBudget().subtract(aoBudgetSummary.getTotalOTPSAmount()
						.getYtdInvoicedAmount())));
		aoBudgetSummary.getTotalDirectsCosts().setApprovedBudget(
				aoBudgetSummary.getTotalSalaryAndFringesAmount().getApprovedBudget()
						.add(aoBudgetSummary.getTotalOTPSAmount().getApprovedBudget())
						.add(aoBudgetSummary.getTotalRateBasedAmount().getApprovedBudget())
						.add(aoBudgetSummary.getTotalMilestoneBasedAmount().getApprovedBudget())
						.add(aoBudgetSummary.getUnallocatedFunds().getApprovedBudget()));
		aoBudgetSummary.getTotalDirectsCosts().setYtdInvoicedAmount(
				aoBudgetSummary.getTotalSalaryAndFringesAmount().getYtdInvoicedAmount()
						.add(aoBudgetSummary.getTotalOTPSAmount().getYtdInvoicedAmount())
						.add(aoBudgetSummary.getTotalRateBasedAmount().getYtdInvoicedAmount())
						.add(aoBudgetSummary.getTotalMilestoneBasedAmount().getYtdInvoicedAmount())
						.add(aoBudgetSummary.getUnallocatedFunds().getYtdInvoicedAmount()));
		aoBudgetSummary.getTotalCityFundedBudget().setRemainingAmount(
				(aoBudgetSummary.getTotalCityFundedBudget().getApprovedBudget().subtract(aoBudgetSummary
						.getTotalCityFundedBudget().getYtdInvoicedAmount())));
		aoBudgetSummary.getTotalDirectsCosts().setRemainingAmount(
				aoBudgetSummary.getTotalDirectsCosts().getApprovedBudget()
						.subtract(aoBudgetSummary.getTotalDirectsCosts().getYtdInvoicedAmount()));
		aoBudgetSummary.getTotalProgramBudget().setApprovedBudget(
				(aoBudgetSummary.getTotalCityFundedBudget().getApprovedBudget().add(aoBudgetSummary
						.getTotalProgramIncome().getApprovedBudget())));
		aoBudgetSummary.getTotalProgramBudget().setYtdInvoicedAmount(
				(aoBudgetSummary.getTotalCityFundedBudget().getYtdInvoicedAmount().add(aoBudgetSummary
						.getTotalProgramIncome().getYtdInvoicedAmount())));
		aoBudgetSummary.getTotalProgramBudget().setRemainingAmount(
				(aoBudgetSummary.getTotalProgramBudget().getApprovedBudget().subtract(aoBudgetSummary
						.getTotalProgramBudget().getYtdInvoicedAmount())));
	}

	/**
	 * This method insert row in indirect table.
	 * <ul>
	 * <li>Execute the query id <b> getIndirectRateCount</b></li>
	 * <li>If loIndirectRateCount is equal to null or loIndirectRateCount is
	 * equal to zero then Execute the query id <b>insertIndirectRate</b></li>
	 * </ul>
	 * 
	 * @param aoCBGridBean CBGrid bean containing parameters set in the session.
	 * @param aoMyBatisSession MyBatis sql session object passed.
	 * @throws ApplicationException Application exception if error occur.
	 */
	private void insertStandardRowsIndirectRate(CBGridBean aoCBGridBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Integer loIndirectRateCount = HHSConstants.INT_ZERO;
		try
		{
			loIndirectRateCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean.getSubBudgetID(),
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBS_NEW_GET_INDIRECT_RATE_COUNT,
					HHSConstants.JAVA_LANG_STRING);
			if (loIndirectRateCount == null || loIndirectRateCount == HHSConstants.INT_ZERO)
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.INSERT_INDIRECT_RATE, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			}
			setMoState("insert was succes for budget id" + aoCBGridBean.getSubBudgetID());
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("loCBGridBeanList", CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT
					.Error("App Exception occured in ContractBudgetService: insertStandardRowsIndirectRate method:: ",
							loAppEx);
			setMoState("Error occured while inserting standard rows in indirect rate for BudgetId::"
					+ aoCBGridBean.getContractBudgetID() + " and SubBudget Id::" + aoCBGridBean.getSubBudgetID());
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			// Context Data is added into Exception object and Log is updated
			ApplicationException loAppEx = new ApplicationException("Error occurred in ContractBudgetService:"
					+ " insertStandardRowsIndirectRate method:: ", loEx);
			loAppEx.addContextData("loCBGridBeanList", CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: insertStandardRowsIndirectRate method",
					loAppEx);
			setMoState("Error occurred in ContractBudgetService: insertStandardRowsIndirectRate method"
					+ aoCBGridBean.getContractBudgetID() + " and SubBudget Id::" + aoCBGridBean.getSubBudgetID());
			throw loAppEx;
		}
	}

	/**
	 * This is private method used to insert row in Personnel Services(fringe
	 * benefits).
	 * <ul>
	 * <li>Execute the query id <b> getFringeBenefitCount </b></li>
	 * <li>If loFringeCount is equal to null and loFringeCount is equal to zero
	 * then Execute the query id <b> insertStandardFringeBenefits</b></li>
	 * <li>Updated in Release 6</li>
	 * </ul>
	 * 
	 * @param aoCBGridBeanForPersonnalService CBGrid bean containing parameters
	 *            set in the session.
	 * @param aoMyBatisSession MyBatis sql session object passed.
	 * @throws ApplicationException Application exception if error occur.
	 */
	@SuppressWarnings("unchecked")
	private void insertStandardRowsPersonnelServices(CBGridBean aoCBGridBeanForPersonnalService,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		try
		{
			String loBudgetExist = (String) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanForPersonnalService,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.GET_BUGET_EXIST_INFO,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// 1. fetch your tab data for this subBudget
			Integer loFringeCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession,
					aoCBGridBeanForPersonnalService.getSubBudgetID(), HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBS_GET_FRINGE_BENEFIT_COUNT, HHSConstants.JAVA_LANG_STRING);
			// for new Budget
			// If Budget is of R6
			if (loBudgetExist.equals(HHSConstants.ZERO))
			{
				// 2. if no rows are fetched then query master table to prepare
				// data
				if (null == loFringeCount || loFringeCount == HHSConstants.INT_ZERO)
				{
					List<String> loFringeBenefitsMasterIdList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession,
							null, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSR5Constants.FETCH_FRINGE_BENEFITS_MASTER_LIST, null);

					if (null != loFringeBenefitsMasterIdList
							&& loFringeBenefitsMasterIdList.size() > HHSConstants.INT_ZERO)
					{
						DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanForPersonnalService,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
								HHSConstants.CBY_INSERT_STANDARD_FRINGE_BENEFITS,
								HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
						// 3. traverse master bean and prepare line item data
						// for
						// fringe benefits
						for (String loFringeBenefitsMasterIdTemp : loFringeBenefitsMasterIdList)
						{
							PersonnelServiceBudget loPersonnelServiceBudgetBean = new PersonnelServiceBudget();
							loPersonnelServiceBudgetBean.setTypeId(loFringeBenefitsMasterIdTemp);
							loPersonnelServiceBudgetBean.setContractBudgetID(aoCBGridBeanForPersonnalService
									.getContractBudgetID());
							loPersonnelServiceBudgetBean.setSubBudgetID(aoCBGridBeanForPersonnalService
									.getSubBudgetID());
							loPersonnelServiceBudgetBean.setModifyByAgency(aoCBGridBeanForPersonnalService
									.getModifyByAgency());
							loPersonnelServiceBudgetBean.setModifyByProvider(aoCBGridBeanForPersonnalService
									.getModifyByProvider());
							loPersonnelServiceBudgetBean.setBudgetAmount(HHSConstants.ZERO);
							DAOUtil.masterDAO(aoMyBatisSession, loPersonnelServiceBudgetBean,
									HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
									HHSR5Constants.INSERT_FRINGE_BENEFIT_DETAIL, HHSConstants.PERSONNEL_SERVICE_BUDGET);
						}
						
					}
				}
			}
			else
			{
				if (loFringeCount == null || loFringeCount == HHSConstants.INT_ZERO)
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanForPersonnalService,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.CBY_INSERT_STANDARD_FRINGE_BENEFITS,
							HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				}
			}
			setMoState("insertStandardRowsPersonnelServices was success for budget id"
					+ aoCBGridBeanForPersonnalService.getSubBudgetID());

		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("loCBGridBeanList", CommonUtil.convertBeanToString(aoCBGridBeanForPersonnalService));
			LOG_OBJECT.Error(
					"App Exception occured in ContractBudgetService: insertStandardRowsPersonnelServices method:: ",
					loAppEx);
			setMoState("Error occured while inserting Standard Rows Personnel Services BudgetId::"
					+ aoCBGridBeanForPersonnalService.getContractBudgetID() + " and SubBudget Id::"
					+ aoCBGridBeanForPersonnalService.getSubBudgetID());
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			// Context Data is added into Exception object and Log is updated
			ApplicationException loAppEx = new ApplicationException("Error occurred in ContractBudgetService:"
					+ " insertStandardRowsPersonnelServices method:: ", loEx);
			loAppEx.addContextData("loCBGridBeanList", CommonUtil.convertBeanToString(aoCBGridBeanForPersonnalService));
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: insertStandardRowsPersonnelServices method",
					loAppEx);
			setMoState("Error occurred in ContractBudgetService: insertStandardRowsPersonnelServices method"
					+ aoCBGridBeanForPersonnalService.getContractBudgetID() + " and SubBudget Id::"
					+ aoCBGridBeanForPersonnalService.getSubBudgetID());
			throw loAppEx;
		}
	}

	/**
	 * <p>
	 * This method calculates the modificationAmount for
	 * operationsAndSupportAmount, Total Salary and Fringes, Total OTPS, Total
	 * Directs Costs, totalCityFundedBudget and Total Program Budget Amount.
	 * </p>
	 * <ul>
	 * <li>Fetch details Contract Budget Summary</li>
	 * </ul>
	 * 
	 * @param aoBudgetSummary an object of ContractBudgetSummary
	 * @return
	 */
	private void setTotalModificationAmounts(ContractBudgetSummary aoBudgetSummary)
	{
		aoBudgetSummary.getOperationsSupportAndEquipmentAmount().setModificationAmount(
				aoBudgetSummary.getOperationsAndSupportAmount().getModificationAmount()
						.add(aoBudgetSummary.getEquipmentAmount().getModificationAmount()));
		aoBudgetSummary.getTotalSalaryAndFringesAmount().setModificationAmount(
				(aoBudgetSummary.getTotalFringes().getModificationAmount().add(aoBudgetSummary.getTotalSalary()
						.getModificationAmount())));
		aoBudgetSummary.getTotalOTPSAmount().setModificationAmount(
				(aoBudgetSummary.getOperationsSupportAndEquipmentAmount().getModificationAmount()
						.add(aoBudgetSummary.getUtilitiesAmount().getModificationAmount())
						.add(aoBudgetSummary.getProfessionalServicesAmount().getModificationAmount())
						.add(aoBudgetSummary.getRentAndOccupancyAmount().getModificationAmount()).add(aoBudgetSummary
						.getContractedServicesAmount().getModificationAmount())));
		aoBudgetSummary.getTotalDirectsCosts().setModificationAmount(
				aoBudgetSummary.getTotalSalaryAndFringesAmount().getModificationAmount()
						.add(aoBudgetSummary.getTotalOTPSAmount().getModificationAmount())
						.add(aoBudgetSummary.getTotalRateBasedAmount().getModificationAmount())
						.add(aoBudgetSummary.getTotalMilestoneBasedAmount().getModificationAmount())
						.add(aoBudgetSummary.getUnallocatedFunds().getModificationAmount()));
		aoBudgetSummary.getTotalCityFundedBudget().setModificationAmount(
				(aoBudgetSummary.getTotalDirectsCosts().getModificationAmount().add(aoBudgetSummary
						.getTotalIndirectCosts().getModificationAmount())));
		aoBudgetSummary.getTotalProgramBudget().setModificationAmount(
				(aoBudgetSummary.getTotalCityFundedBudget().getModificationAmount().add(aoBudgetSummary
						.getTotalProgramIncome().getModificationAmount())));
	}

	/**
	 * <p>
	 * This method will hit the ContractBudgetMapper to get all the data for
	 * Modification BudgetSummary screen.
	 * </p>
	 * <ul>
	 * <li>1.Initialize loCBBudgetModificationSummary</li>
	 * <li>2.Initialize loCBGridBeanObj- an object of CBGridBean</li>
	 * <li>3.Set budgetId, subBudgetId, ModifiedBudgetId and ModifiedSubbudgetId
	 * from aoCBGridBeanObj</li>
	 * <li>4.Call fetchBudgetSummary method of ContractBudgetMapper to fetch
	 * budget summary which contains the FYBudget and remaining amount</li>
	 * <li>5.Call fetchModificationBudgetSummary to fetch the modification
	 * amounts
	 * <li>
	 * <li>6.Iterate over list of budgetDetails to collate the data in a single
	 * list.
	 * <li>
	 * <li>7.Call setTotalModificationAmounts which sets the modificationAmount
	 * for operationsAndSupportAmount,totalSalaryAndFringesAmount
	 * ,totalIndirectCosts,totalCityFundedBudget,totalProgramBudget
	 * <li>
	 * <li>8.Catch exception if there is any error fetching budget summary</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession MyBatis sql session object passed.
	 * @param aoCBGridBeanObj an object of CBGridBean
	 * @param aoMasterBean Master Bean
	 * @return loCBBudgetModificationSummary - an object of
	 *         ContractBudgetSummary
	 * @throws ApplicationException Application exception if error occur.
	 */
	@SuppressWarnings("unchecked")
	public ContractBudgetSummary fetchModificationBudgetSummary(SqlSession aoMybatisSession,
			CBGridBean aoCBGridBeanObj, MasterBean aoMasterBean) throws ApplicationException
	{

		ContractBudgetSummary loCBBudgetModificationSummary = null;
		List<BudgetDetails> loCBBudgetDetailsList = null;
		HashMap<String, String> loContextDataMap = new HashMap<String, String>();
		loContextDataMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoCBGridBeanObj.getContractBudgetID());
		loContextDataMap.put(HHSConstants.SUBBUDGET_ID, aoCBGridBeanObj.getSubBudgetID());
		try
		{
			CBGridBean loCBGridBeanObj = new CBGridBean();
			loCBGridBeanObj.setModifiedContractBudgetID(aoCBGridBeanObj.getContractBudgetID());
			loCBGridBeanObj.setModifiedSubBudgetID(aoCBGridBeanObj.getSubBudgetID());
			loCBGridBeanObj.setContractBudgetID(aoCBGridBeanObj.getParentBudgetId());
			loCBGridBeanObj.setSubBudgetID(aoCBGridBeanObj.getParentSubBudgetId());

			// Check if the Budget is approved, the list must be fetched from
			// XML else from DB
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBBudgetModificationSummary = fetchBudgetSummaryFromXML(lsSubBudgetId, aoMasterBean);
				setTotalAmounts(loCBBudgetModificationSummary);
			}
			else
			{
				loCBBudgetModificationSummary = fetchBudgetSummary(aoMybatisSession, loCBGridBeanObj);

				loCBBudgetDetailsList = (List<BudgetDetails>) DAOUtil.masterDAO(aoMybatisSession, loCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_MODIFICATION_BUDGET_SUMMARY, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				for (BudgetDetails loBudgetDetails : loCBBudgetDetailsList)
				{
					Map<String, String> loMap = HHSConstants.CONTRACT_BUDGET_SUMMARY_MAP;
					String lsProprty = loMap.get(loBudgetDetails.getTitle());
					BudgetDetails loDetails = (BudgetDetails) PropertyUtils.getProperty(loCBBudgetModificationSummary,
							lsProprty);
					loDetails.setModificationAmount(loBudgetDetails.getModificationAmount());
				}
			}
			setTotalModificationAmounts(loCBBudgetModificationSummary);
			Field[] loEachElement = loCBBudgetModificationSummary.getClass().getDeclaredFields();
			for (Field loField : loEachElement)
			{
				BudgetDetails loDetails = (BudgetDetails) PropertyUtils.getProperty(loCBBudgetModificationSummary,
						loField.getName());
				loDetails.setProposedBudget(loDetails.getApprovedBudget().add(loDetails.getModificationAmount()));
			}
		}
		// catch any application exception thrown from the code due to SELECT
		// statement and throw it
		// forward
		catch (ApplicationException loExp)
		{
			setMoState("Error while fetching Modification Budget Summary for sub budget id:"
					+ aoCBGridBeanObj.getSubBudgetID());
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching Budget Summary for sub budget id", loExp);
			throw loExp;
		}
		// Catch any Null pointer exception thrown from the code and wrap it
		// into application
		// Exception and throw it forward
		catch (Exception loAppExp)
		{
			setMoState("Error while fetching Modification Budget Summary for sub budget id:"
					+ aoCBGridBeanObj.getSubBudgetID());
			LOG_OBJECT.Error("Error while fetching Modification Budget Summary for sub budget id", loAppExp);
			throw new ApplicationException("Error while fetching Modification Budget Summary for sub budget id",
					loAppExp);
		}
		return loCBBudgetModificationSummary;
	}

	/**
	 * This method fetch approved budget summary details list from XML
	 * <ul>
	 * <li>This method iterates the List of LineItemMasterBean and checks for
	 * the if condition</li>
	 * </ul>
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return ContractBudgetSummary
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private ContractBudgetSummary fetchBudgetSummaryFromXML(String asSubBudgetId, MasterBean aoMasterBean)
			throws ApplicationException
	{
		ContractBudgetSummary loContractBudgetSummary = new ContractBudgetSummary();

		List<LineItemMasterBean> loMasterBeanList = null;

		loMasterBeanList = aoMasterBean.getMasterBeanList();
		Iterator<LineItemMasterBean> aoListIterator = loMasterBeanList.iterator();
		while (aoListIterator.hasNext())
		{
			LineItemMasterBean loLineItemBean = aoListIterator.next();
			if (loLineItemBean.getSubbudgetId().equals(asSubBudgetId))
			{
				loContractBudgetSummary = loLineItemBean.getLoBudgetSummary();
			}
		}
		return loContractBudgetSummary;
	}

	/**
	 * <p>
	 * This method update Unallocated fund details in DB
	 * <ul>
	 * <li>1.Get all unallocated funds details in UnallocatedFunds Bean</li>
	 * <li>2.Execute the query <b> updateUnallocatedFunds query</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession Sql session object
	 * @param aoUnallocatedFundsBean UnallocatedFunds Bean Object
	 * @return lbUpdateStatus boolean
	 * @throws ApplicationException Application exception if error occur.
	 */

	public boolean updateUnallocatedFunds(SqlSession aoMybatisSession, UnallocatedFunds aoUnallocatedFundsBean)
			throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		try
		{  	// fetch your tab data for this subBudget
			DAOUtil.masterDAO(aoMybatisSession, aoUnallocatedFundsBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.UPDATE_UNALLOCATED_FUNDS,
					HHSConstants.UNALLOCATED_FUNDS_BEAN);
			lbUpdateStatus = true;
			//[Start]R7.12.0 QC9311 Minimize Debug
			//LOG_OBJECT.Debug("=========================== updating  unallocated funds ");
			//[End]R7.12.0 QC9311 Minimize Debug
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppExp)
		{
			// setting the transaction state for exception and setting the
			// context
			setMoState("error occured while updating unallocated funds for business type id "
					+ aoUnallocatedFundsBean.getBudgetId());
			loAppExp.addContextData("Exception occured while  updating  unallocated funds ", loAppExp);
			LOG_OBJECT.Error("error occured while  updating  unallocated funds ", loAppExp);
			throw loAppExp;
		}
		return lbUpdateStatus;
	}

	/**
	 * <p>
	 * This method fetch unallocated funds details from DB
	 * <ul>
	 * <li>Call fetchUnallocatedFunds query set sub budget id as where clause</li>
	 * <li>If the user is coming for the first time than a blank value is insert
	 * </li>
	 * <li>against that record, to ease the update.
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession Sql session object
	 * @param aoCBGridBeanObj CBGridBean passed with attributes set in session.
	 * @return List<UnallocatedFunds> loUnallocatedFunds
	 * @throws ApplicationException Application exception if error occur.
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List fetchUnallocatedFunds(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj)
			throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		List<UnallocatedFunds> loUnallocatedFunds = null;

		try
		{
			if (null != aoCBGridBeanObj && null != aoCBGridBeanObj.getSubBudgetID())
			{
				// Setting the variable for functionality related Bean from
				// generic bean
				loUnallocatedFundsBean.setSubBudgetId(Integer.parseInt(aoCBGridBeanObj.getSubBudgetID()));
				loUnallocatedFundsBean.setBudgetId(Integer.parseInt(aoCBGridBeanObj.getContractBudgetID()));

				// fetching the details for unallocated Funds.
				loUnallocatedFunds = (List<UnallocatedFunds>) DAOUtil.masterDAO(aoMybatisSession,
						loUnallocatedFundsBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.FETCH_UNALLOCATED_FUNDS, HHSConstants.UNALLOCATED_FUNDS_BEAN);
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppExp)
		{
			// setting the transaction state for exception and setting the
			// context
			setMoState("error occured while fetching unallocated funds for business type id "
					+ loUnallocatedFundsBean.getBudgetId());
			loAppExp.addContextData("Exception occured while fetching unallocated funds ", loAppExp);
			LOG_OBJECT.Error("error occured while fetching unallocated funds ", loAppExp);
			throw loAppExp;
		}
		return loUnallocatedFunds;
	}

	
	// Start QC 8394 add add/delete action for Unallocated Funds
	
	/**
	 * <p>
	 * This method add Unallocated fund details in DB
	 * <ul>
	 * <li>1.Get all unallocated funds details in UnallocatedFunds Bean</li>
	 * <li>2.Execute the query <b> addUnallocatedFunds query</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession Sql session object
	 * @param aoUnallocatedFundsBean UnallocatedFunds Bean Object
	 * @return lbUpdateStatus boolean
	 * @throws ApplicationException Application exception if error occur.
	 */

	public boolean addUnallocatedFunds(SqlSession aoMybatisSession, UnallocatedFunds aoUnallocatedFundsBean)
			throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		try
		{
			// 1. fetch your tab data for this subBudget
			DAOUtil.masterDAO(aoMybatisSession, aoUnallocatedFundsBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.ADD_UNALLOCATED_FUNDS,
						HHSConstants.UNALLOCATED_FUNDS_BEAN);
			lbUpdateStatus = true;
			//[Start]R7.12.0 QC9311 Minimize Debug
			//LOG_OBJECT.Debug("Unallocated Funds line has been added!");
			//[End]R7.12.0 QC9311 Minimize Debug
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppExp)
		{
			// setting the transaction state for exception and setting the
			// context
			setMoState("error occured while updating unallocated funds for business type id "
					+ aoUnallocatedFundsBean.getBudgetId());
			loAppExp.addContextData("Exception occured while  adding  unallocated funds ", loAppExp);
			LOG_OBJECT.Error("error occured while  adding  unallocated funds ", loAppExp);
			throw loAppExp;
		}
		return lbUpdateStatus;
	}
	
	/**
	 * <p>
	 * This method update Unallocated fund details in DB
	 * <ul>
	 * <li>1.Get all unallocated funds details in UnallocatedFunds Bean</li>
	 * <li>2.Execute the query <b> updateUnallocatedFunds query</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession Sql session object
	 * @param aoUnallocatedFundsBean UnallocatedFunds Bean Object
	 * @return lbUpdateStatus boolean
	 * @throws ApplicationException Application exception if error occur.
	 */

	public boolean deleteUnallocatedFunds(SqlSession aoMybatisSession, UnallocatedFunds aoUnallocatedFundsBean)
			throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		try
		{
			// Setting the variable for functionality related Bean from generic
			// bean
			DAOUtil.masterDAO(aoMybatisSession, aoUnallocatedFundsBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.DELETE_UNALLOCATED_FUNDS,
					HHSConstants.UNALLOCATED_FUNDS_BEAN);
			lbUpdateStatus = true;
			//[Start]R7.12.0 QC9311 Minimize Debug
			//LOG_OBJECT.Debug("Unallocated Funds line has been deleted!");
			//[End]R7.12.0 QC9311 Minimize Debug
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppExp)
		{
			// setting the transaction state for exception and setting the
			// context
			setMoState("error occured while updating unallocated funds for business type id "
					+ aoUnallocatedFundsBean.getBudgetId());
			loAppExp.addContextData("Exception occured while  deleting  unallocated funds ID:\n "+aoUnallocatedFundsBean.getBudgetId(), loAppExp);
			LOG_OBJECT.Error("error occured while  updating  deleteing funds ", loAppExp);
			throw loAppExp;
		}
		return lbUpdateStatus;
	}
   // End QC 8394 add add/delete action for Unallocated Funds
	
	
	
	
	
	/**
	 * <p>
	 * We will fetch the list of rent matching to the contract budget. This
	 * method fetch Rent details from DB on the basis of budget type.<br/>
	 * Below is mapping of budget type id and its budget type : <br/>
	 * 1 = Budget Amendment; 2 = Contract Budget; 3 = Budget Modification; 4 =
	 * Budget Update <br/>
	 * <b>Functionalities by below method are :</b>
	 * <ul>
	 * <li>1.Get all Rent details in Rent Bean to fetch data.</li>
	 * <li>1.The query <code>fetchContractBudgetRent</code> will fetch the rent
	 * associated with that contract budget.
	 * <li>2.Call fetchContractBudgetRentUpdation query for Budget
	 * Update,fetchContractBudgetRentModification query for Budget Modification
	 * and fetchContractBudgetRentAmendment query for Budget Amendment</li>
	 * <li>Fetch all the rent information based on the select statement on basis
	 * of BudgetId</li>
	 * <li>Store the result of the DAO hit in the object of the Rent List
	 * created.
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession Sql session object as input.
	 * @param aoRentId Rent Bean as input.
	 * @return loRent Rent Bean as output with all Rent related Information.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<Rent> fetchContractBudgetRent(SqlSession aoMybatisSession, CBGridBean aoRentId)
			throws ApplicationException
	{
		List<Rent> loRent = null;
		try
		{ // 2= Contract Budget
			if (aoRentId.getBudgetTypeId() != null && aoRentId.getBudgetTypeId().equalsIgnoreCase(HHSConstants.TWO))
			{
				loRent = (List<Rent>) DAOUtil.masterDAO(aoMybatisSession, aoRentId,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_FETCH_CONTRACT_BUDGET_RENT,
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
			setMoState("error occured while fetching Rent for business type id " + aoRentId.getBudgetTypeId());
			loExp.addContextData("Exception occured while fetching rent ", loExp);
			LOG_OBJECT.Error("error occured while fetching rent ", loExp);
			throw loExp;
		}
		return loRent;
	}

	/**
	 * <p>
	 * This method update Rent details in DB on the basis of budget type.<br/>
	 * Below is mapping of budget type id and its budget type : <br/>
	 * 1 = Budget Amendment; 2 = Contract Budget; 3 = Budget Modification; 4 =
	 * Budget Update <br/>
	 * <b>Functionalities by below method are :</b>
	 * <ul>
	 * <li>1.Create a boolean UpdateStatus and initialise it with false.</li>
	 * <li>2.Get all Rent details in CBIndirectRateBean Bean to update data.</li>
	 * <li>3.Based on the budget Type the queries will be fired and the result
	 * set will be stored in the DB.
	 * <li>4.Once the DB result has been executed properly the lbUpdateStatus
	 * will be updated
	 * <li>5.Call <code>updateContractBudgetRent</code> query for Budget Update,
	 * <code>updateContractBudgetRentModification</code> query for Budget
	 * 
	 * Modification and <code>updateContractBudgetRentAmendment</code> query for
	 * Budget Amendment</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatiSession Sql session object
	 * @param aoRent Rent Bean Object
	 * @return lbUpdateStatus true if sql query execute without exception/false
	 *         if query fails.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */

	public boolean updateContractBudgetRent(SqlSession aoMybatiSession, Rent aoRent) throws ApplicationException
	{
		boolean lbUpdateStatus = true;
		Integer loRentCount = HHSConstants.INT_ZERO;
		try
		{
			// 2= Contract Budget
			if (aoRent.getBudgetTypeId() != null && aoRent.getBudgetTypeId().equalsIgnoreCase(HHSConstants.TWO))
			{
				loRentCount = (Integer) DAOUtil.masterDAO(aoMybatiSession, aoRent,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_UPDATE_CONTRACT_BUDGET_RENT,
						HHSConstants.CBY_COM_NYC_HHS_MODEL_RENT);
				// Check, if the integer is updated as '1' after doing the
				// modification, else will return as false status update
				if (loRentCount <= HHSConstants.INT_ZERO)
				{
					lbUpdateStatus = false;
				}
				// checking for edit status
				if (lbUpdateStatus)
				{
					setMoState("ContractBudgetService: updateRate() edit successfully.");
				}
				else
				{
					setMoState("ContractBudgetService: updateRate() failed to edit.");
					throw new ApplicationException(
							"Error occured while edit at ContractBudgetService: updateRate() for id:" + aoRent.getId());
				}
			}
			lbUpdateStatus = true;
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */

		catch (ApplicationException loExp)
		{
			setMoState("error occured while updating Rent for business type id " + aoRent.getBudgetTypeId());
			loExp.addContextData("Exception occured while updating Rent ", loExp);
			LOG_OBJECT.Error("error occured while updating Rent ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while edit in ContractBudgetService ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: updateRent method - failed to edit"
					+ aoRent.getId() + " \n");
			throw new ApplicationException("Exception occured while edit in ContractBudgetService ", loAppEx);
		}
		return lbUpdateStatus;
	}

	/**
	 * <p>
	 * This is the insertContractBudgetRent() method for adding any rent
	 * information for the particular contract Budget. <br/>
	 * Below is mapping of budget type id and its budget type : <br/>
	 * 1 = Budget Amendment; 2 = Contract Budget; 3 = Budget Modification; 4 =
	 * Budget Update <br/>
	 * <b>Functionalities by below method are :</b>
	 * <ul>
	 * <li>1.Create a boolean UpdateStatus and initialise it with false.</li>
	 * <li>2.Get all Rent details in Rent Bean to update data.</li>
	 * <li>3.Based on the budget Type the queries will be fired and the result
	 * set will be stored in the DB.
	 * <li>4.Once the DB result has been executed properly the lbUpdateStatus
	 * will be updated
	 * <li>5.Call <code>insertContractBudgetRent</code> query for Budget Update,
	 * <code>insertContractBudgetRentModification<code> query for Budget Modification and 
	 * <code>insertContractBudgetRentAmendment</code> query for Budget Amendment
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatiSession Sql session object
	 * @param aoRent Rent Bean Object
	 * @param aoCurrentSeq Integer parameter to get the sequence.
	 * @return lbUpdateStatus true if sql query execute without exception/false
	 *         if query fails.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public boolean insertContractBudgetRent(Integer aoCurrentSeq, SqlSession aoMybatiSession, Rent aoRent)
			throws ApplicationException
	{
		boolean lbInsertRentStatus = true;
		Integer loRentCount = HHSConstants.INT_ZERO;
		try
		{
			aoRent.setId(String.valueOf(aoCurrentSeq));// The current sequence
														// will be inserted from
														// the Dual table next
														// value
			loRentCount = (Integer) DAOUtil.masterDAO(aoMybatiSession, aoRent,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_INSERT_CONTRACT_BUDGET_RENT,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_RENT);
			// Check, if the integer is updated as '1' after doing the
			// modification, else will return as false status update
			if (loRentCount <= HHSConstants.INT_ZERO)
			{
				lbInsertRentStatus = false;
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("error occured while inserting Rent for business type id " + aoRent.getBudgetTypeId());
			loExp.addContextData("Exception occured while inserting Rent ", loExp);
			LOG_OBJECT.Error("error occured while inserting Rent ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while inserting in ContractBudgetService ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: Insert method - failed to insert" + aoRent.getId()
					+ " \n");
			throw new ApplicationException("Exception occured while insert in ContractBudgetService ", loAppEx);
		}
		return lbInsertRentStatus;
	}

	/**
	 * <p>
	 * This is the deleteContractBudgetRent() method for deleting any rent
	 * information for the particular contract RentId.<br/>
	 * Below is mapping of budget type id and its budget type : <br/>
	 * 1 = Budget Amendment; 2 = Contract Budget; 3 = Budget Modification; 4 =
	 * Budget Update <br/>
	 * <b>Functionalities by below method are :</b>
	 * <ul>
	 * <li>1.Create a boolean UpdateStatus and initialise it with false.</li>
	 * <li>2.Get all Rent details in Rent Bean to update data.</li>
	 * <li>3.Based on the budget Type the queries will be fired and the result
	 * set will be stored in the DB.
	 * <li>4.Once the DB result has been executed properly the lbUpdateStatus
	 * will be updated
	 * <li>5.Call <code>deleteContractBudgetRent</code> query for Budget Update,
	 * <code>deleteContractBudgetRentModification<code> query for Budget 

Modification and 
		 * <code>deleteContractBudgetRentAmendment</code> query for Budget Amendment
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatiSession Sql session object
	 * @param aoRent Rent Bean Object
	 * @return lbUpdateStatus true if sql query execute without exception/false
	 *         if query fails.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public boolean deleteContractBudgetRent(SqlSession aoMybatiSession, Rent aoRent) throws ApplicationException
	{
		boolean lbDeleteRentStatus = true;
		Integer loCountDelete = HHSConstants.INT_ZERO;
		try
		{
			loCountDelete = (Integer) DAOUtil.masterDAO(aoMybatiSession, aoRent,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_DELETE_CONTRACT_BUDGET_RENT,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_RENT);
			// Check, if the integer is updated as '1' after doing the
			// modification, else will return as false status update
			if (loCountDelete <= HHSConstants.INT_ZERO)
			{
				lbDeleteRentStatus = false;
			}
			// checking for delete status
			if (lbDeleteRentStatus)
			{
				setMoState("ContractBudgetService: Delete Rent Deleted Successfully");
			}
			else
			{
				setMoState("ContractBudgetService: DeleteRent() failed to Delete.");
				throw new ApplicationException(
						"Error occured while edit at ContractBudgetService: DeleteRent() for id:" + aoRent.getId());
			}

			if (aoRent.getBudgetTypeId() != null && aoRent.getBudgetTypeId().equalsIgnoreCase(HHSConstants.TWO))
			{
				DAOUtil.masterDAO(aoMybatiSession, aoRent, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.CBY_DELETE_CONTRACT_BUDGET_RENT, HHSConstants.CBY_COM_NYC_HHS_MODEL_RENT);
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("error occured while deleting Rent for business type id " + aoRent.getBudgetTypeId());
			loExp.addContextData("Exception occured while deleting Rent ", loExp);
			LOG_OBJECT.Error("error occured while deleting Rent ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while Delete in ContractBudgetService ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: DeletRent method - failed to edit" + aoRent.getId()
					+ " \n");
			throw new ApplicationException("Exception occured while edit in ContractBudgetService ", loAppEx);
		}
		return lbDeleteRentStatus;
	}

	/**
	 * This method is triggered Rent Grid.
	 * <ul>
	 * <li>Get the Sequence for rentId from Rent</li>
	 * <li>An integer value is returned which determines the sequence of rentId
	 * from Rent table.</li>
	 * <li>Execute the query id <b> getSeqForRent </b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Sql session object
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return liCurrentSeq
	 */
	public int getSeqForRent(SqlSession aoMybatisSession) throws ApplicationException
	{
		int liCurrentSeq = HHSConstants.INT_ZERO;
		liCurrentSeq = (Integer) DAOUtil.masterDAO(aoMybatisSession, null,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_GET_SEQ_FOR_RENT, null);
		return liCurrentSeq;
	}

	/**
	 * <p>
	 * This method fetches Program Income List corresponding to a Subbudget
	 * <ul>
	 * <li>Fetches Program Income List - against SubBudgetId</li>
	 * <li>Returns the list of Program Income Items with Program Income Title,
	 * FYBudget (Amount), Remaining Amount(FYBudget-YTD Invoiced Amount)</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBGridBeanObj - CBGridBean object containing key fields
	 * @param aoMybatisSession - SqlSession object
	 * @return loCBProgramIncomeBean - CBProgramIncomeBean
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<CBProgramIncomeBean> fetchProgramIncome(CBGridBean aoCBGridBeanObj, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		List<CBProgramIncomeBean> loCBProgramIncomeBean = null;
		try
		{
			// Fetch List of Program Income Items
			loCBProgramIncomeBean = (List<CBProgramIncomeBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_PROGRAM_INCOME,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			setMoState("error occured while fetching Program Income Details for budget type id "
					+ aoCBGridBeanObj.getBudgetTypeId());
			loAppEx.addContextData("Exception occured while fetching Program Income Details ", loAppEx);
			LOG_OBJECT.Error("error occured while fetching Program Income Details ", loAppEx);
			throw loAppEx;
		}
		return loCBProgramIncomeBean;
	}

	/**
	 * <p>
	 * This method updates Program Income Details corresponding to a
	 * ProgramIncomeId ( primary Key attribute of CBProgramsIncomeBean bean)
	 * <ul>
	 * <li>Updates the whole row of a Program Income</li>
	 * <li>Also updates the Program Income under Budget Modification</li>
	 * <li>Execute the query id <b>updateProgramIncome</b></li>
	 * <li>If the 'if condition' is satisfied then execute the query id
	 * <b>fetchProgramIncomeForOther</b></li>
	 * <li>Otherwise Execute the query id <b>updateProgramIncomeForOther</b>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession - SqlSessionId
	 * @param aoCBProgramIncomeBean - CBProgramsIncomeBean object
	 * @return Program Income object(CBProgramsIncomeBean type object)
	 * @throws ApplicationException - ApplicationException object
	 */
	public Boolean updateProgramIncome(SqlSession aoMybatisSession, CBProgramIncomeBean aoCBProgramIncomeBean)
			throws ApplicationException
	{
		Boolean lbUpdateStatus = Boolean.FALSE;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoCBProgramIncomeBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.UPDATE_PROGRAM_INCOME,
					HHSConstants.PROGRAM_INCOME_BEAN);

			// Check if the submitted Record is for Other line item - update
			// Program Title as well if for line item for 'Other' is
			// submitted
			if (aoCBProgramIncomeBean.getProgramTitle() != null
					&& !HHSConstants.EMPTY_STRING.equals(aoCBProgramIncomeBean.getProgramTitle())
					&& !HHSConstants.OTHER.equalsIgnoreCase(aoCBProgramIncomeBean.getProgramTitle()))
			{
				Integer loProgramIncomeId = Integer.parseInt(aoCBProgramIncomeBean.getId());

				// Fetch and check if a record for 'Other' field is already
				// submitted once
				Integer loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loProgramIncomeId,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_PROGRAM_INCOME_FOR_OTHER,
						HHSConstants.INTEGER_CLASS_PATH);

				if (loCount == HHSConstants.INT_ZERO)
				{
					// Add new entry for new text place in place of 'Other'
					// in
					// Other_details table
					DAOUtil.masterDAO(aoMybatisSession, aoCBProgramIncomeBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.ADD_PROGRAM_INCOME_FOR_OTHER, HHSConstants.PROGRAM_INCOME_BEAN);
				}
				else
				{
					// update existing entry for new text place in place of
					// 'Other' in Other_details table
					DAOUtil.masterDAO(aoMybatisSession, aoCBProgramIncomeBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.UPDATE_PROGRAM_INCOME_FOR_OTHER, HHSConstants.PROGRAM_INCOME_BEAN);
				}
			}

			lbUpdateStatus = Boolean.TRUE;
		}
		// Catch ApplicationExceptions thrown from DaoUtil
		catch (ApplicationException loAppEx)
		{
			setMoState("error occured while updating Program Income Line Item with programIncomeId = "
					+ aoCBProgramIncomeBean.getId());
			loAppEx.addContextData("Exception occured while updating Program Income Line Item", loAppEx);
			LOG_OBJECT.Error("error occured while updating Program Income ", loAppEx);
			throw loAppEx;
		}
		// Catch all Exceptions except ApplicationExceptions
		catch (Exception loExp)
		{
			// Create a new ApplicationException to throw back
			ApplicationException loAppEx = new ApplicationException("", loExp);
			setMoState("error occured while updating Program Income for budget type id ");
			loAppEx.addContextData("Exception occured while updating Program Income Line Item", loExp);
			LOG_OBJECT.Error("error occured while updating Program Income Line Item", loExp);
			throw loAppEx;
		}
		return lbUpdateStatus;
	}

	/**
	 * <p>
	 * This method update Indirect Rate details in DB on the basis of budget
	 * type.<br/>
	 * Below is mapping of budget type id and its budget type : <br/>
	 * 1 = Budget Amendment; 2 = Contract Budget; 3 = Budget Modification; 4 =
	 * Budget Update <br/>
	 * <b>Functionalities by below method are :</b>
	 * <ul>
	 * <li>1.Get all IndirectRate details in CBIndirectRateBean Bean to update
	 * data.</li>
	 * <li>2.Call updateIndirectRate query for Budget
	 * Update,updateIndirectRateModification query for Budget Modification and
	 * updateIndirectRateAmendment query for Budget Amendment</li>
	 * <li>
	 * If the absolute value of the Amendment amount entered is greater than the
	 * [Remaining Amount – Negative Amendment amounts entered for the line item
	 * in other pending Amendments for which no budgets have been approved],
	 * then display grid level error in case of amendments.</li>
	 * </ul>
	 * Updated Method in R4
	 * </p>
	 * 
	 * @param aoMybatiSession Sql session object
	 * @param aoIndirectRate CBIndirectRateBean Bean Object
	 * @return lbUpdateStatus true if sql query execute without exception/false
	 *         if query fails.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public boolean updateIndirectRate(SqlSession aoMybatiSession, CBIndirectRateBean aoIndirectRate)
			throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		BigDecimal loIndirectRemainingAmount = null;
		boolean lbError = false;
		try
		{
			if (aoIndirectRate != null && aoIndirectRate.getBudgetTypeId() != null)
			{
				String lsQuery = HHSConstants.INDIRECT_RATE_UPDATE_QUERY_MAP.get(aoIndirectRate.getBudgetTypeId());
				if (lsQuery != null)
				{
					// call directly for contract budget.2 = Contract Budget;
					if (aoIndirectRate.getBudgetTypeId().equalsIgnoreCase(HHSConstants.TWO))
					{
						lbUpdateStatus = updateInsertIndirectRate(aoMybatiSession, aoIndirectRate, lsQuery);
					}
					// check for Amendment,modification and update code. 3 =
					// Budget
					// Modification; 4 = Budget Update and 1 = Budget Amendment
					else
					{
						// 1 = Budget Amendment
						if (!aoIndirectRate.getAmendmentType().equalsIgnoreCase(HHSConstants.EMPTY_STRING))
						{
							Map<Boolean, ApplicationException> loMap = amendmentCheck(aoIndirectRate,
									aoIndirectRate.getIndirectModificationAmount());
							if (loMap.get(true) != null)
							{
								lbError = true;
								throw loMap.get(true);
							}
						}
						indirectRateConstant(aoIndirectRate);
						// get remaining amount for server side check.
						if (aoIndirectRate.getBudgetTypeId().equalsIgnoreCase(HHSConstants.ONE))
						{
							loIndirectRemainingAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession, aoIndirectRate,
									HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
									HHSConstants.GET_REMAINING_AMOUNT_INDIRECT_RATE,
									HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_INDIRECT);
						}
						else
						{
							loIndirectRemainingAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession, aoIndirectRate,
									HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
									HHSConstants.GET_REMAINING_AMOUNT_INDIRECT_RATE,
									HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_INDIRECT);
						}
						// sum of remaining amount and modification amount to be
						// positive
						if (loIndirectRemainingAmount.add(
								new BigDecimal(aoIndirectRate.getIndirectModificationAmount())).compareTo(
								BigDecimal.ZERO) >= HHSConstants.INT_ZERO)
						{

							lbUpdateStatus = updateInsertIndirectRate(aoMybatiSession, aoIndirectRate, lsQuery);
						}
						else if (aoIndirectRate.getBudgetTypeId().equalsIgnoreCase(HHSConstants.ONE))
						{
							lbError = true;
							throw new ApplicationException(PropertyLoader.getProperty(
									HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									HHSConstants.CBM_LESS_THAN_INVOICE_INCLUDING_PENDING_NEG_AMEND_ERROR_MESSAGE));
						}
						else
						{
							lbError = true;
							throw new ApplicationException(PropertyLoader.getProperty(
									HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									HHSConstants.BUDGET_MODIFICATION_RATE_AMNT_VALIDATION));
						}
					}
				}
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loAppExp)
		{
			// In case of failure, the exception will be thrown back to base
			// controller
			// along with the validation message
			if (lbError)
			{
				loAppExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoIndirectRate.getSubBudgetID());
				loAppExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_BUDGET, aoIndirectRate.getContractBudgetID());
				loAppExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loAppExp.toString());
				LOG_OBJECT.Error("Sum of remaining amount and modification amount is less than zero", loAppExp);
				setMoState("Sum of remaining amount and modification amount is less than zero");
			}
			else
			{
				loAppExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoIndirectRate.getSubBudgetID());
				loAppExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoIndirectRate.getContractBudgetID());
				LOG_OBJECT.Error("error occured while updating Indirect Rate ", loAppExp);
				setMoState("Error occured while updating Indirect Rate for business type id "
						+ aoIndirectRate.getBudgetTypeId());
			}
			throw loAppExp;
		}
		return lbUpdateStatus;
	}

	/**
	 * This method is used to perform error check for positive and negative
	 * amendment on server side
	 * <ul>
	 * <li>Fetch details from Indirect Rate</li>
	 * </ul>
	 * 
	 * @param aoIndirectRate CBGridBean as input
	 * @param asAmendmentAmount amendment amount as input
	 * @return loMap flag as key , exception as value map
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	private Map<Boolean, ApplicationException> amendmentCheck(CBGridBean aoIndirectRate, String asAmendmentAmount)
			throws ApplicationException
	{
		Map<Boolean, ApplicationException> loMap = new HashMap<Boolean, ApplicationException>();
		// Validation for positive amendment.
		if (aoIndirectRate.getAmendmentType().equalsIgnoreCase(HHSConstants.POSITIVE)
				&& new BigDecimal(asAmendmentAmount).compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO)
		{
			loMap.put(
					true,
					new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.POSITIVE_AMENDMENT_MSG)));
		}
		// Validation for negative amendment.
		else if (aoIndirectRate.getAmendmentType().equalsIgnoreCase(HHSConstants.NEGATIVE)
				&& new BigDecimal(asAmendmentAmount).compareTo(BigDecimal.ZERO) > HHSConstants.INT_ZERO)
		{
			loMap.put(
					true,
					new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.NEGATIVE_AMENDMENT_MSG)));
		}
		return loMap;
	}

	/**
	 * This method update/insert indirect rate table.
	 * <ul>
	 * <li>Execute query id <b> fecthPrevApprovedBudget</b></li>
	 * <li>Execute query id if loIndirectRateCount equal to zero <b>
	 * insertIndirectRateModification </b></li>
	 * </ul>
	 * 
	 * @param aoMybatiSession Sql session object
	 * @param aoIndirectRate CBIndirectRateBean Bean Object
	 * @param asQuery query id as input
	 * @return lbUpdateStatus true if success in query
	 * @throws ApplicationException thrown in case of any application code
	 *             failure.
	 */
	private boolean updateInsertIndirectRate(SqlSession aoMybatiSession, CBIndirectRateBean aoIndirectRate,
			String asQuery) throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		Integer loIndirectRateCount = (Integer) DAOUtil.masterDAO(aoMybatiSession, aoIndirectRate,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, asQuery,
				HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_INDIRECT);

		// only for modification, update and amendment
		if (loIndirectRateCount == HHSConstants.INT_ZERO)
		{
			aoIndirectRate.setPrevApprovedAmount((String) DAOUtil.masterDAO(aoMybatiSession, aoIndirectRate,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_PREV_BUDGET,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_INDIRECT));

			DAOUtil.masterDAO(aoMybatiSession, aoIndirectRate, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_INSERT_INDIRECT_RATE_MODIFICATION, HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_INDIRECT);
		}
		lbUpdateStatus = true;
		return lbUpdateStatus;
	}

	/**
	 * <p>
	 * The method is updated in Release 7. Added a parameter for showing
	 * approved modification. This method fetch Indirect Rate details from DB on
	 * the basis of budget type.<br/>
	 * Below is mapping of budget type id and its budget type : <br/>
	 * 1 = Budget Amendment; 2 = Contract Budget; 3 = Budget Modification; 4 =
	 * Budget Update <br/>
	 * <b>Functionalities by below method are :</b>
	 * <ul>
	 * <li>1.Get all Indirect Rate details in CBIndirectRateBean Bean to fetch
	 * data.</li>
	 * <li>1.Call fetchIndirectRate query for Budget
	 * Update,fetchIndirectRateModification query for Budget Modification and
	 * fetchIndirectRateAmendment query for Budget Amendment</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession Sql session object as input.
	 * @param aoIndirectRate CBGridBean Bean as input.
	 * @param aoMasterBean - MasterBean object
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
		try
		{
			// Start R7 changes ::Fetch the data from FileNet XML if Budget
			// Status is approved
			String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BUDGET_APPROVED);
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
			//R7 end
				if (aoIndirectRate != null && aoIndirectRate.getBudgetTypeId() != null)
				{
					indirectRateConstant(aoIndirectRate);
					String lsQuery = HHSConstants.INDIRECT_RATE_FETCH_QUERY_MAP.get(aoIndirectRate.getBudgetTypeId());
					if (lsQuery != null)
					{
						if (aoIndirectRate.getSubBudgetID() != null
								&& aoIndirectRate.getParentSubBudgetId() != null
								&& aoIndirectRate.getSubBudgetID().equalsIgnoreCase(
										aoIndirectRate.getParentSubBudgetId()))
						{
							loIndirectRate = (List<CBIndirectRateBean>) DAOUtil.masterDAO(aoMybatisSession,
									aoIndirectRate, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
									HHSConstants.CBY_FETCH_INDIRECT_RATE_AMENDMENT_NEW_RECORD,
									HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
						}
						else
						{
							loIndirectRate = (List<CBIndirectRateBean>) DAOUtil.masterDAO(aoMybatisSession,
									aoIndirectRate, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, lsQuery,
									HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
						}
						setMoState("Success while fetching Indirect Rate for business type id "
								+ aoIndirectRate.getBudgetTypeId());
					}
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
	 * This method set the indirect rate constant.
	 * 
	 * @param aoIndirectRate CBGridBean as input.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	private void indirectRateConstant(CBGridBean aoIndirectRate) throws ApplicationException
	{
		aoIndirectRate.setInvoiceStatusIdList(HHSConstants.INVOICE_STATUS_ID_INDIRECT_RATE);
		aoIndirectRate.setEntryTypeId(HHSConstants.STRING_TEN);
	}

	/**
	 * This method is used to calculate update and then fetch indirect rate for
	 * contract budget and modification. * 1 = Budget Amendment; 2 = Contract
	 * Budget; 3 = Budget Modification; 4 = Budget Update <br/>
	 * <b>Functionalities by below method are :</b>
	 * <ul>
	 * <li>1.update Indirect Rate percentage. updateIndirectRatePercentage for
	 * Contract Budget and updateIndirectRateModificaitonPercentage for Contract
	 * Modification</li>
	 * <li>1.Call fetchIndirectRatePercentage query get indirect rate percentage
	 * entry.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession Sql session object as input.
	 * @param aoCBGridBeanObj CBGridBean Bean as input.
	 * @param aoMasterBean MasterBean Bean as input.
	 * @return lsUpdatedIndirectRatePercentage as output.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public String updateIndirectRatePercentage(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj,
			MasterBean aoMasterBean) throws ApplicationException
	{
		String lsUpdatedIndirectRatePercentage = HHSConstants.STRING_ZERO;
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);
		try
		{
			if (aoCBGridBeanObj != null && aoCBGridBeanObj.getBudgetTypeId() != null)
			{
				String lsQuery = HHSConstants.INDIRECT_RATE_UPDATE_PERCENTAGE_QUERY_MAP.get(aoCBGridBeanObj
						.getBudgetTypeId());
				// TEST
				
				LOG_OBJECT.Info("CBGridBean getSubBudgetID() :: "+aoCBGridBeanObj.getSubBudgetID());
				//[Start]R7.12.0 QC9311 Minimize Debug
				//LOG_OBJECT.Info("CBGridBean getContractBudgetID() :: "+aoCBGridBeanObj.getContractBudgetID());
				//LOG_OBJECT.Info("CBGridBean getFiscalYearID() :: "+aoCBGridBeanObj.getFiscalYearID());
				//[End]R7.12.0 QC9311 Minimize Debug
				
				// Start QC 9202 R 7.10.0 - get fiscal year for subbudget or budget
				if ( null==aoCBGridBeanObj.getFiscalYearID() || aoCBGridBeanObj.getFiscalYearID().isEmpty() )
				{
					String lsFiscalYearId = getFiscalYearIDfromDB(aoMybatisSession, aoCBGridBeanObj);
					aoCBGridBeanObj.setFiscalYearID(lsFiscalYearId);
					//[Start]R7.12.0 QC9311 Minimize Debug
					//LOG_OBJECT.Debug("Fiscal Year Id from aoCBGridBeanObj :: "+aoCBGridBeanObj.getFiscalYearID());
					//[End]R7.12.0 QC9311 Minimize Debug
				}
				// End QC 9202 R 7.10.0 - get fiscal year for subbudget	or budget

				if (aoMasterBean != null && aoCBGridBeanObj.getBudgetTypeId().equals(HHSConstants.ONE)
						&& aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
				{
					String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
					List<LineItemMasterBean> loMasterBeanList = null;
					loMasterBeanList = aoMasterBean.getMasterBeanList();
					Iterator<LineItemMasterBean> loListIterator = loMasterBeanList.iterator();
					while (loListIterator.hasNext())
					{
						LineItemMasterBean loLineItemBean = loListIterator.next();
						if (loLineItemBean.getSubbudgetId().equals(lsSubBudgetId))
						{
							lsUpdatedIndirectRatePercentage = loLineItemBean.getIndirectRatePercent();
						}
					}
				}
				else if (lsQuery != null)
				{
					DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, lsQuery,
							HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

					lsUpdatedIndirectRatePercentage = (String) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.FETCH_INDIRECT_RATE_PERCENTAGE, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

					if (lsUpdatedIndirectRatePercentage == null)
					{
						lsUpdatedIndirectRatePercentage = HHSConstants.DECIMAL_ZERO;
					}

					setMoState("Success while fetching Indirect Rate percentage for business type id "
							+ aoCBGridBeanObj.getBudgetTypeId());
				}
			}
			if (lsUpdatedIndirectRatePercentage != null)
			{
				lsUpdatedIndirectRatePercentage = String.format("%1$,.2f",
						Double.parseDouble((lsUpdatedIndirectRatePercentage)));
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loAppExp)
		{
			// Set the state, added context data and added error log if any
			// application exception occurs
			setMoState("Error occured while fetching Indirect Rate percentage for business type id "
					+ aoCBGridBeanObj.getBudgetTypeId());
			loAppExp.addContextData(HHSConstants.BUDGET_TYPE_ID, aoCBGridBeanObj.getBudgetTypeId());
			loAppExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBGridBeanObj.getSubBudgetID());
			loAppExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBGridBeanObj.getContractBudgetID());
			LOG_OBJECT.Error("error occured while fetching Indirect Rate percentage \n Parameter :" + aoCBGridBeanObj.toString() + "  \n" + aoMasterBean.toString(), loAppExp);
			throw loAppExp;
		}

		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (Exception loExp)
		{
			// Set the state, added context data and added error log if any
			// exception occurs
			setMoState("Error occured while fetching Indirect Rate percentage for business type id "
					+ aoCBGridBeanObj.getBudgetTypeId());
			LOG_OBJECT.Error("error occured while fetching Indirect Rate percentage  \n Parameter :" + aoCBGridBeanObj.toString() + "  \n" + aoMasterBean.toString(), loExp);
			throw new ApplicationException("Exception occured while fetching Indirect Rate percentage ", loExp);
		}
		return lsUpdatedIndirectRatePercentage;
	}

	/**
	 * The method is updated in Release 7. Added a parameter for showing
	 * approved modification.
	 * <p>
	 * This method fetch Utility Details from DB on the basis of budget type.<br/>
	 * Below is mapping of budget type id and its budget type : <br/>
	 * 2 = Contract Budget; 3 = Budget Modification; 4 = Budget Update <br/>
	 * <b>Functionalities by below method are :</b>
	 * <ul>
	 * <li>1.Get all Utility details in CBGridBean Bean to fetch data.</li>
	 * <li>2.fetchUtilitiesDetails query is executed for Contract Budget and
	 * fetchUtilitiesModifyDetails query is executed for Budget
	 * Modification/Update to fetch Utility details which are displayed on
	 * Utility Grid.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession Sql session object as input.
	 * @param aoCBGridBeanObj CBGridBean Bean as input.
	 * @param aoMasterBean - MasterBean object
	 * @return loCBUtilities loCBUtilities Bean as output with all Utility
	 *         related Information.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<CBUtilities> fetchUtilities(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj,
			MasterBean aoMasterBean) throws ApplicationException
	{
		List<CBUtilities> loCBUtilities = null;
		aoCBGridBeanObj.setEntryTypeId(HHSConstants.THREE);
		// Start R7
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);
		//R7 end
		try
		{
			// 2 = Contract Budget
			if (aoCBGridBeanObj.getBudgetTypeId() != null
					&& aoCBGridBeanObj.getBudgetTypeId().equalsIgnoreCase(HHSConstants.TWO))
			{
				loCBUtilities = (List<CBUtilities>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_FETCH_UTILITIES_DETAILS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			}
			// 3 = Budget Modification or 4 = Budget Update
			else if (aoCBGridBeanObj.getBudgetTypeId() != null
					&& (aoCBGridBeanObj.getBudgetTypeId().equalsIgnoreCase(HHSConstants.THREE) || aoCBGridBeanObj
							.getBudgetTypeId().equalsIgnoreCase(HHSConstants.FOUR)))
			{
				// Start R7 changes ::Fetch the data from FileNet XML if Budget
				// Status is approved
				if (aoMasterBean != null && aoCBGridBeanObj.getBudgetTypeId().equals(HHSConstants.THREE)
						&& aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
				{
					String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
					loCBUtilities = new ContractBudgetAmendmentService().fetchUtilitiesDataFromXML(lsSubBudgetId,
							aoMasterBean);
				}
				else
				{
				//R7 end
					loCBUtilities = (List<CBUtilities>) DAOUtil
							.masterDAO(aoMybatisSession, aoCBGridBeanObj,
									HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
									HHSConstants.CBY_FETCH_UTILITIES_MODIFY_DETAILS,
									HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				}
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
		// Catch any Null pointer exception thrown from the code and wrap it
		// into application
		// Exception and throw it forward
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException("Exception occured while fetching Utilities ",
					loExp);
			setMoState("Exception occured while updating Utilities for budget id: "
					+ aoCBGridBeanObj.getContractBudgetID() + " and Sub-Budget id : "
					+ aoCBGridBeanObj.getSubBudgetID());
			loAppEx.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBGridBeanObj.getSubBudgetID());
			loAppEx.addContextData(HHSConstants.ADD_CONTEXT_DATA_BUDGET, aoCBGridBeanObj.getContractBudgetID());
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: fetchUtilities method:: ", loAppEx);
			throw loAppEx;
		}
		return loCBUtilities;

	}

	/**
	 * <p>
	 * This method update Utility details in DB on the basis of budget type.<br/>
	 * Below is mapping of budget type id and its budget type : <br/>
	 * 2 = Contract Budget; 3 = Budget Modification; 4 = Budget Update <br/>
	 * <b>Functionalities by below method are :</b>
	 * <ul>
	 * <li>1.Get all Utility details in CBUtilities Bean to update data.</li>
	 * <li>2.If budget type is Contract Budget, updateUtilitiesDetails query is
	 * executed to update FY Budget Amount entered from Utility Grid in DB.</li>
	 * <li>3.If budget type is Budget Modification or Budget Update, validation
	 * to check that Modified/Updated Amount entered from Utilities Grid would
	 * not cause the Total Proposed Budget for the line item to fall below the
	 * YTD Invoiced Amount.
	 * <li>4.mergeUpdateUtilities method is called in case of Budget
	 * Modification or Budget Update to update the Modified/Updated Amount
	 * entered from grid in DB of an already existing Utility line item or
	 * insert a new row in case no row exists for the respective line item.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatiSession Sql session object
	 * @param aoCBUtilities CBUtilities Bean Object
	 * @return lbUpdateStatus true if sql query execute without exception/false
	 *         if query fails.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public boolean updateUtilities(SqlSession aoMybatiSession, CBUtilities aoCBUtilities) throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		boolean lbError = false;
		try
		{
			// 2 = Contract Budget
			if (aoCBUtilities.getBudgetTypeId() != null
					&& aoCBUtilities.getBudgetTypeId().equalsIgnoreCase(HHSConstants.TWO))
			{
				DAOUtil.masterDAO(aoMybatiSession, aoCBUtilities, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.CBY_UPDATE_UTILITIES_DETAILS, HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_UTILITIES);
			}
			// 3 = Budget Modification or 4 = Budget Update
			else if (aoCBUtilities.getBudgetTypeId() != null
					&& (aoCBUtilities.getBudgetTypeId().equalsIgnoreCase(HHSConstants.THREE) || aoCBUtilities
							.getBudgetTypeId().equalsIgnoreCase(HHSConstants.FOUR)))
			{
				CBUtilities loCBUtilities = null;
				// Get Amount Details for server side validation
				loCBUtilities = (CBUtilities) DAOUtil.masterDAO(aoMybatiSession, aoCBUtilities.getId(),
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.CBM_FETCH_UTILITIES_DETAILS_FOR_VALIDATION, HHSConstants.JAVA_LANG_STRING);

				// Check if Modification/Updated Amount is not less than already
				// invoiced amount
				if ((new BigDecimal(loCBUtilities.getRemainingAmt()).add(new BigDecimal(aoCBUtilities
						.getLineItemModifiedAmt())).compareTo(BigDecimal.ZERO)) >= HHSConstants.INT_ZERO)
				{
					// Update/Insert Modification Row
					mergeUpdateUtilities(aoMybatiSession, aoCBUtilities);
				}
				else
				{
					lbError = true;
					throw new ApplicationException(
							PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									HHSConstants.CBM_LESS_THAN_INVOICE_FOR_NEG_AMEND));
				}
			}
			lbUpdateStatus = true;
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
			ApplicationException loAppEx = new ApplicationException("Exception occured while updating Utilities ",
					loExp);
			setMoState("Exception occured while updating Utilities for budget id: "
					+ aoCBUtilities.getContractBudgetID() + " and Sub-Budget id : " + aoCBUtilities.getSubBudgetID());
			loAppEx.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBUtilities.getSubBudgetID());
			loAppEx.addContextData(HHSConstants.ADD_CONTEXT_DATA_BUDGET, aoCBUtilities.getContractBudgetID());
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: updateUtilities method:: ", loExp);
			throw loAppEx;
		}
		return lbUpdateStatus;
	}

	/**
	 * <p>
	 * This method updates the Modified/Updated Amount entered from Utility Grid
	 * in DB of an already existing Utility line item or inserts a new row in
	 * case no row exists for the respective line item.<br/>
	 * <ul>
	 * <li>1. updateUtilitiesModifyDetails query is called to update amendment
	 * amount in DB.
	 * <li>2. If 0 rows are returned from updateUtilitiesModifyDetails query the
	 * insertUtilitiesModifyDetails is called to insert a new row record in DB.
	 * </p>
	 * 
	 * @param aoMybatiSession Sql session object
	 * @param aoCBUpdateUtilities CBUtilities Bean Object
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	private void mergeUpdateUtilities(SqlSession aoMybatiSession, CBUtilities aoCBUpdateUtilities)
			throws ApplicationException
	{
		int liUpdateCounter = HHSConstants.INT_ZERO;
		String lsUpdateUtilityTypeId = HHSConstants.EMPTY_STRING;
		// Update Modified Amount of Line Item for Modified
		// SubBudget
		liUpdateCounter = (Integer) DAOUtil.masterDAO(aoMybatiSession, aoCBUpdateUtilities,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_UPDATE_UTILITIES_MODIFY_DETAILS,
				HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_UTILITIES);

		// If modification row of Line Item does not exist then
		// insert a new modification row.
		if (liUpdateCounter < HHSConstants.INT_ONE)
		{
			// Fetch Utility type Id of Base line item row
			lsUpdateUtilityTypeId = (String) DAOUtil.masterDAO(aoMybatiSession, aoCBUpdateUtilities,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_FETCH_UTILITIES_TYPE_ID,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_UTILITIES);
			aoCBUpdateUtilities.setUtilitiesTypeID(lsUpdateUtilityTypeId);
			DAOUtil.masterDAO(aoMybatiSession, aoCBUpdateUtilities, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_INSERT_UTILITIES_MODIFY_DETAILS, HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_UTILITIES);
		}
	}

	/**
	 * This Method fetches the values of the <b>Professional Services</b> tab
	 * (Used by Provider Users) in the Contract Budget screen for every
	 * individual sub budget of the current Fiscal Year with the help of unique
	 * Sub budget ID
	 * 
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
	 * Invoiced Amount</li>
	 * <li>Execute query id <b>fetchProfessionalServicesDetails</b></li>
	 * <li>Execute query id <b>fetchProfessionalServicesDetails</b></li>
	 * </ul>
	 * </li>
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
	 * tab itself</li> </ul> Modified this method to remove code for default
	 * entries as part of build 3.2.0.
	 * 
	 * @param aoProfService CBGridBean attributes sent that are kept in session.
	 * @param aoMybatisSession SqlMyBatis Session Object
	 * @return loProfServicesDetails
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<CBProfessionalServicesBean> fetchProfServicesDetails(CBGridBean aoProfService,
			SqlSession aoMybatisSession) throws ApplicationException
	{

		List<CBProfessionalServicesBean> loProfServicesDetails = null;
		try
		{
			// Fetching Professional services details data to display on
			// Professional Service Grid
			loProfServicesDetails = (List<CBProfessionalServicesBean>) DAOUtil.masterDAO(aoMybatisSession,
					aoProfService, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_FETCH_PROFESSIONAL_SERVICES_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// removed code for default entries as part of build 3.2.0.
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while fetching Professional Service Details for budget id:"
					+ aoProfService.getContractBudgetID() + " and Sub-Budget id : " + aoProfService.getSubBudgetID());
			loExp.addContextData(
					"Error while fetching Professional Service Details budget id :  fetchProfessionalServicesDetails",
					loExp);
			LOG_OBJECT.Error("Error while fetching Professional Service Details budget "
					+ "id : fetchProfessionalServicesDetails ", loExp);
			throw loExp;
		}

		return loProfServicesDetails;
	}

	/**
	 * The Method will fetch the budget details of Salaried Employees grid of
	 * Personnel Services tab under contract budget module
	 * <ul>
	 * <li>Execute query id <b>addProfessionalServicesDetails</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session Object.
	 * @param aoProfService CBGridBean attributes sent that are kept in session.
	 * @return liRowsAdded
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * 
	 */

	private Integer addProfessionalServicesDetails(SqlSession aoMybatisSession, CBGridBean aoProfService)
			throws ApplicationException
	{

		Integer liRowsAdded = null;
		try
		{
			// Adding rows in PROFESSIONAL_SERVICE table for every Professional
			// Type available in PROFESSIONAL_SERVICE_TYPE_MSTR table
			liRowsAdded = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoProfService,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_ADD_PROFESSIONAL_SERVICES_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while adding Professional service details budget id:"
					+ aoProfService.getContractBudgetID() + "and Sub-Budget id : " + aoProfService.getSubBudgetID());
			loExp.addContextData(
					"Error while adding Professional service details budget id :  addProfessionalServicesDetails",
					loExp);
			LOG_OBJECT.Error("Error while adding Professional service details budget id  "
					+ ": addProfessionalServicesDetails ", loExp);
			throw loExp;
		}
		return liRowsAdded;
	}

	/**
	 * This Method updates the values of the <b>Professional Services</b> tab
	 * (Used by Provider Users) in the Contract Budget screen for every
	 * individual sub budget of the current Fiscal Year with the help of unique
	 * Sub budget ID
	 * 
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
	 * Invoiced Amount</li>
	 * </ul>
	 * <li>Execute query id <b>updateProfessionalServicesDetails</b></li>
	 * <li>Execute query id <b>fetchProfServicesForOther</b> if professional
	 * service is not null and some other condition aswell.
	 * <li>Execute query id <b> addProfServicesForOther </b> if liCount is equal
	 * to zero</li>
	 * </li>
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
	 * tab itself</li> </ul>
	 * 
	 * @param aoProfService CBGridBean attributes sent that are kept in session.
	 * @param aoMybatisSession Mybatis Session Object.
	 * @return lbUpdateStatus
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public boolean editProfServicesDetails(CBProfessionalServicesBean aoProfService, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		try
		{
			// Update budget amount in Professional Service table
			DAOUtil.masterDAO(aoMybatisSession, aoProfService, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_UPDATE_PROFESSIONAL_SERVICES_DETAILS,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN);

			// Management of "Other" field of professional service grid
			if (aoProfService.getProfessionalServiceName() != null
					&& !HHSConstants.EMPTY_STRING.equals(aoProfService.getProfessionalServiceName()))
			{
				Integer loProfServiceId = Integer.parseInt(aoProfService.getId());

				Integer liCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loProfServiceId,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.CBY_FETCH_PROF_SERVICES_FOR_OTHER, HHSConstants.INTEGER_CLASS_PATH);

				if (liCount == HHSConstants.INT_ZERO)
				{
					DAOUtil.masterDAO(aoMybatisSession, aoProfService,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.CBY_ADD_PROF_SERVICES_FOR_OTHER,
							HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN);
				}
				else
				{
					DAOUtil.masterDAO(aoMybatisSession, aoProfService,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.CBY_UPDATE_PROF_SERVICES_FOR_OTHER,
							HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN);
				}
			}

			lbUpdateStatus = true;
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("error occured while updating Utilities for budget type id "
					+ aoProfService.getContractBudgetID());
			loExp.addContextData("Exception occured while updating Utilities ", loExp);
			LOG_OBJECT.Error("error occured while updating Utilities ", loExp);
			throw loExp;
		}
		return lbUpdateStatus;
	}

	/**
	 * The Method will fetch the budget details of Salaried Employees grid of
	 * Personnel Services tab under contract budget module
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudget CBGridBean attributes sent that are kept
	 *            in session.
	 * @return loSalariedEmployess
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * 
	 */

	public List<PersonnelServiceBudget> fetchSalariedEmployeeBudget(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget) throws ApplicationException
	{

		List<PersonnelServiceBudget> loSalariedEmployess = null;

		try
		{

			loSalariedEmployess = fetchSalariedEmployeeForBase(aoMybatisSession, aoPersonnelServiceBudget);

		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while fetching Salaried Employee budget "
					+ ":  fetchSalariedEmployeeBudget", loExp);
			LOG_OBJECT.Error("ApplicationException occured while fetching Salaried Employee budget "
					+ ": fetchSalariedEmployeeBudget ", loExp);
			setMoState("ApplicationException occured while fetching Salaried Employee budget for budget id = "
					+ aoPersonnelServiceBudget.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudget.getSubBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while adding Salaried Emplyee budget :  fetchSalariedEmployeeBudget ", loExp);
			loAppEx.addContextData(
					"Exception occured while fetching Salaried Emplyee budget :  fetchSalariedEmployeeBudget", loExp);
			LOG_OBJECT.Error(
					"Exception occured while fetching Salaried Employee budget : fetchSalariedEmployeeBudget ", loExp);
			setMoState("Exception occured while adding fetching Employee budget for budget id = "
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
	 * <li>Execute query id <b> insertPersonnelServices </b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget bean
	 * @return lbStatus Insert status
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * 
	 */

	public Boolean addEmployeeBudget(SqlSession aoMybatisSession, PersonnelServiceBudget aoPersonnelServiceBudgetBean)
			throws ApplicationException
	{

		boolean lbInsertStatus = false;
		String lsTransactionName = aoPersonnelServiceBudgetBean.getTransactionName();
		setEmployeeTypeInBean(aoPersonnelServiceBudgetBean, lsTransactionName);

		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_INSERT_PERSONNEL_SERVICES,
					HHSConstants.PERSONNEL_SERVICE_BUDGET);

			lbInsertStatus = true;

		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while inserting Emplyee budget :  addEmployeeBudget",
					loExp);
			LOG_OBJECT
					.Error("ApplicationException occured while inserting Emplyee budget : addEmployeeBudget " + loExp);
			setMoState("ApplicationException occured while inserting Emplyee budget for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while inserting Emplyee budget :  addEmployeeBudget ", loExp);
			loAppEx.addContextData("Exception occured while inserting Emplyee budget :  addEmployeeBudget", loExp);
			LOG_OBJECT.Error("Exception occured while inserting Emplyee budget : addEmployeeBudget ", loExp);
			setMoState("Exception occured while inserting Emplyee budget for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loAppEx;
		}
		return lbInsertStatus;
	}

	/**
	 * The Method will delete budget details record of Salaried Employees/Hourly
	 * Employee/Seasoned Employee of Personnel Services tab under contract
	 * budget module. Mapper file being used is ContractBudgetMapper.xml.
	 * <ul>
	 * <li>Execute query id <b>deletePersonnelServices</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudgetBean PersonnelService Id
	 * @return lbDeleteStatus Delete status
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * 
	 */

	public Boolean delEmployeeBudget(SqlSession aoMybatisSession, PersonnelServiceBudget aoPersonnelServiceBudgetBean)
			throws ApplicationException
	{

		boolean lbDeleteStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_DELETE_PERSONNEL_SERVICES,
					HHSConstants.PERSONNEL_SERVICE_BUDGET);
			lbDeleteStatus = true;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while deleting Emplyee budget :  delEmployeeBudget",
					loExp);
			LOG_OBJECT.Error("ApplicationException occured while deleting Emplyee budget : delEmployeeBudget ", loExp);
			setMoState("ApplicationException occured while deleting Emplyee budget for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while deleting Emplyee budget :  delEmployeeBudget ", loExp);
			loAppEx.addContextData("Exception occured while deleting Emplyee budget :  delEmployeeBudget", loExp);
			LOG_OBJECT.Error("Exception occured while deleting Emplyee budget : delEmployeeBudget ", loExp);
			setMoState("Exception occured while deleting Emplyee budget for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loAppEx;
		}
		return lbDeleteStatus;

	}

	/**
	 * The Method will edit budget details record of Salaried Employees/Hourly
	 * Employee/Seasoned Employee of Personnel Services tab under contract
	 * budget module. Mapper file being used is ContractBudgetMapper.xml.
	 * <ul>
	 * <li>Execute query id <b>updatePersonnelServices</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget bean
	 * @return lbStatus Edit status
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * 
	 */

	public Boolean editEmployeeBudget(SqlSession aoMybatisSession, PersonnelServiceBudget aoPersonnelServiceBudgetBean)
			throws ApplicationException
	{

		boolean lbEditStatus = false;

		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_UPDATE_PERSONNEL_SERVICES,
					HHSConstants.PERSONNEL_SERVICE_BUDGET);
			lbEditStatus = true;

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while editing Emplyee budget :  editEmployeeBudget",
					loExp);
			LOG_OBJECT.Error("ApplicationException occured while editing Emplyee budget : editEmployeeBudget ", loExp);
			setMoState("ApplicationException occured while editing Emplyee budget for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while editing Emplyee budget :  editEmployeeBudget ", loExp);
			loAppEx.addContextData("Exception occured while editing Emplyee budget :  editEmployeeBudget", loExp);
			LOG_OBJECT.Error("Exception occured while editing Emplyee budget : editEmployeeBudget ", loExp);
			setMoState("Exception occured while editing Emplyee budget for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loAppEx;
		}
		return lbEditStatus;
	}

	/**
	 * The Method will fetch the budget details of Hourly Employees grid of
	 * Personnel Services tab under contract budget module
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudget List of PersonnelServiceBudget bean
	 * @return loSalariedEmployess
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * 
	 */

	public List<PersonnelServiceBudget> fetchHourlyEmployeeBudget(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget) throws ApplicationException
	{
		List<PersonnelServiceBudget> loSalariedEmployess = null;
		try
		{

			loSalariedEmployess = fetchHourlyEmployeeForBase(aoMybatisSession, aoPersonnelServiceBudget);

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData(
					"ApplicationException occured while fetching Hourly Employee budget :  fetchHourlyEmployeeBudget",
					loExp);
			LOG_OBJECT.Error("ApplicationException occured while fetching Hourly Employee budget "
					+ ": fetchHourlyEmployeeBudget ", loExp);
			setMoState("ApplicationException occured while fetching Hourly Employee budget for budget id = "
					+ aoPersonnelServiceBudget.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudget.getSubBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while fetching Hourly Emplyee budget :  fetchHourlyEmployeeBudget ", loExp);
			loAppEx.addContextData(
					"Exception occured while fetching Hourly Emplyee budget :  fetchHourlyEmployeeBudget", loExp);
			LOG_OBJECT.Error("Exception occured while fetching Hourly Employee budget : fetchHourlyEmployeeBudget ",
					loExp);
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
	 * @param aoPersonnelServiceBudget List of PersonnelServiceBudget bean
	 * @return loSalariedEmployess
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * 
	 */

	public List<PersonnelServiceBudget> fetchSeasonalEmployeeBudget(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget) throws ApplicationException
	{

		List<PersonnelServiceBudget> loSalariedEmployess = null;
		try
		{

			loSalariedEmployess = fetchSeasonalEmployeeForBase(aoMybatisSession, aoPersonnelServiceBudget);

		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while fetching Seasonal Employee budget "
					+ ":  fetchSeasonalEmployeeBudget", loExp);
			LOG_OBJECT.Error("ApplicationException occured while fetching Seasonal Employee budget "
					+ ": fetchSeasonalEmployeeBudget ", loExp);
			setMoState("ApplicationException occured while fetching Seasonal Employee budget for budget id = "
					+ aoPersonnelServiceBudget.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudget.getSubBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while fetching Seasonal Employee budget :  fetchSeasonalEmployeeBudget ", loExp);
			loAppEx.addContextData(
					"Exception occured while fetching Seasonal Emplyee budget :  fetchSeasonalEmployeeBudget", loExp);
			LOG_OBJECT.Error(
					"Exception occured while fetching Seasonal Employee budget : fetchSeasonalEmployeeBudget ", loExp);
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
	 * @param aoPersonnelServiceBudget List of PersonnelServiceBudget bean
	 * @return loSalariedEmployess
	 * @throws ApplicationException
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */

	public List<PersonnelServiceBudget> fetchFringeBenifits(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget) throws ApplicationException
	{

		List<PersonnelServiceBudget> loSalariedEmployess = null;

		try
		{
			loSalariedEmployess = fetchFringeBenefitsForBase(aoMybatisSession, aoPersonnelServiceBudget);
			if (loSalariedEmployess == null || loSalariedEmployess.size() == HHSConstants.INT_ZERO
					|| loSalariedEmployess.isEmpty())
			{
				loSalariedEmployess = new ArrayList<PersonnelServiceBudget>();
				loSalariedEmployess.add(new PersonnelServiceBudget());

			}

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData(
					"ApplicationException occured while fetching fringe benifits budget :  fetchFringeBenifits", loExp);
			LOG_OBJECT
					.Error("ApplicationException occured while fetching fringe benifits budget : fetchFringeBenifits "
							+ loExp);
			setMoState("ApplicationException occured while fetching fringe benifits budget for budget id = "
					+ aoPersonnelServiceBudget.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudget.getSubBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while adding Hourly Emplyee budget :  fetchFringeBenifits ", loExp);
			loAppEx.addContextData("Exception occured while fetching fringe benifits budget :  fetchFringeBenifits",
					loExp);
			LOG_OBJECT.Error("Exception occured while fetching fringe benifits budget : fetchFringeBenifits ", loExp);
			setMoState("Exception occured while fetching fringe benifits budget for budget id = "
					+ aoPersonnelServiceBudget.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudget.getSubBudgetID());
			throw loAppEx;
		}
		return loSalariedEmployess;
	}

	/**
	 * The Method will edit the fringe benefit details of Personnel Services tab
	 * under contract budget module
	 * <ul>
	 * <li>Execute query id <b>updateFringeBenifits</b></li>
	 * <li>Execute query id <b> insertFringeBenifits</b> if loFringeCount equals
	 * null and loFringeCount equals zero</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget bean
	 * @return lbStatus Edit status
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * 
	 */

	public Boolean editFringeBenifits(SqlSession aoMybatisSession, PersonnelServiceBudget aoPersonnelServiceBudgetBean)
			throws ApplicationException
	{

		boolean lbEditStatus = false;
		Integer loFringeCount = HHSConstants.INT_ZERO;

		try
		{
			loFringeCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_UPDATE_FRINGE_BENIFITS,
					HHSConstants.PERSONNEL_SERVICE_BUDGET);

			if (loFringeCount == null || loFringeCount == HHSConstants.INT_ZERO)
			{
				DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_INSERT_FRINGE_BENIFITS,
						HHSConstants.PERSONNEL_SERVICE_BUDGET);
			}

			lbEditStatus = true;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while editing Fringe Benifits:  editFringeBenifits",
					loExp);
			LOG_OBJECT
					.Error("ApplicationException occured while editing Fringe Benifits : editFringeBenifits " + loExp);
			setMoState("ApplicationException occured while editing Fringe Benifits for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while editing Fringe Benifits :  editFringeBenifits ", loExp);
			loAppEx.addContextData("Exception occured while editing Fringe Benifits :  editFringeBenifits", loExp);
			LOG_OBJECT.Error("Exception occured while editing Fringe Benifits : editFringeBenifits ", loExp);
			setMoState("Exception occured while editing Fringe Benifits for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loAppEx;
		}
		return lbEditStatus;

	}

	/**
	 * <p>
	 * This method is used for fetching values in Milestone grid for a
	 * particular sub-budget.
	 * <ul>
	 * <li>Fetches the milestone information from the database on load.</li>
	 * <li>Execute query id <b>fetchMilestoneDetails</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBGridBeanObj - CBGridBean object
	 * @param aoMybatisSession - SqlSession object
	 * @return loCBMileStoneBean - CBMileStoneBean
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<CBMileStoneBean> fetchMilestone(CBGridBean aoCBGridBeanObj, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		List<CBMileStoneBean> loCBMileStoneBean = null;
		try
		{
			aoCBGridBeanObj.setEntryTypeId(HHSConstants.EIGHT);
			loCBMileStoneBean = (List<CBMileStoneBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_FETCH_MILESTONE_DETAILS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("error occured while fetching MileStone Details for budget type id "
					+ aoCBGridBeanObj.getBudgetTypeId());
			aoExp.addContextData("Exception occured while fetching MileStone Details ", aoExp);
			LOG_OBJECT.Error("error occured while fetching MileStone Details ", aoExp);
			throw aoExp;
		}
		// handling exception other than ApplicationException
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while fetching Milestone in ContractBudgetService ", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchMilestone method - failed to fetch"
					+ aoCBGridBeanObj.getBudgetTypeId() + " \n");
			throw new ApplicationException("Error occured while fetching the details for Milestone", aoAppEx);
		}
		return loCBMileStoneBean;
	}

	/**
	 * This method is triggered Milestone Grid.
	 * <ul>
	 * <li>Get the Sequence for milestoneId from Milestone</li>
	 * <li>An integer value is returned which determines the sequence of
	 * milestoneId from Milestone table.</li>
	 * <li>Execute query id <b> getSeqForMileston</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @throws ApplicationException - ApplicationException object
	 * @return loCurrentSeq - Integer
	 */
	public Integer getSeqForMilestone(SqlSession aoMybatisSession) throws ApplicationException
	{
		Integer loCurrentSeq = HHSConstants.INT_ZERO;
		try
		{
			loCurrentSeq = (Integer) DAOUtil.masterDAO(aoMybatisSession, null,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_GET_SEQ_FOR_MILESTONE, null);
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			throw new ApplicationException("Error occured in getSeqforMilestone", aoEx);
		}
		return loCurrentSeq;
	}

	/**
	 * <p>
	 * This method is used for adding new rows in Milestone grid for a
	 * particular sub-budget.
	 * <ul>
	 * <li>Provider is able to Add new Milestone by creating a new row, typing
	 * in the Milestone title and entering the FY Budget amount.</li>
	 * <li>Execute query id <b>insertMilestoneDetails</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCurrentSeq - Integer
	 * @param aoCBMilestoneBean - CBMileStoneBean object
	 * @param aoMybatisSession - SqlSession object
	 * @return loAddMileStone - Boolean status
	 * @throws ApplicationException - ApplicationException object
	 */
	public Boolean addMilestone(Integer aoCurrentSeq, CBMileStoneBean aoCBMilestoneBean, SqlSession aoMybatisSession)

	throws ApplicationException
	{
		Boolean loAddMileStone = true;
		Integer loStatus = HHSConstants.INT_ZERO;
		try
		{
			aoCBMilestoneBean.setId(String.valueOf(aoCurrentSeq));

			loStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoCBMilestoneBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_INSERT_MILESTONE_DETAILS,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_MILE_STONE_BEAN);
			if (loStatus <= HHSConstants.INT_ZERO)
			{
				loAddMileStone = false;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("error occured while adding Milestone Details for budget type id "
					+ aoCBMilestoneBean.getBudgetTypeId());
			aoExp.addContextData("Exception occured while adding Milestone Details ", aoExp);
			LOG_OBJECT.Error("error occured while adding Milestone Details ", aoExp);
			throw aoExp;
		}
		// handling exception other than ApplicationException
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while adding Milestone in ContractBudgetService ", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: addMilestone method - failed to add"
					+ aoCBMilestoneBean.getBudgetTypeId() + " \n");
			throw new ApplicationException("Error occured while inserting the details for Milestone", aoAppEx);
		}
		return loAddMileStone;
	}

	/**
	 * <p>
	 * This method is used for editing the rows in Milestone grid for a
	 * particular sub-budget.
	 * <ul>
	 * <li>Provider is able to Edit the Milestone line items that have
	 * previously been added.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBMilestoneBean - CBMileStoneBean object containing key fields
	 * @param aoMybatisSession - SqlSession object
	 * @return loEditMileStone - Boolean status
	 * @throws ApplicationException - ApplicationException object
	 */
	public Boolean updateMilestone(CBMileStoneBean aoCBMilestoneBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		Boolean loUpdateMilestone = true;
		Integer loStatus = HHSConstants.INT_ZERO;
		try
		{
			loStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoCBMilestoneBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_UPDATE_MILESTONE_DETAILS,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_MILE_STONE_BEAN);
			if (loStatus <= HHSConstants.INT_ZERO)
			{
				loUpdateMilestone = false;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("error occured while editing Milestone for budget type id "
					+ aoCBMilestoneBean.getBudgetTypeId());
			aoExp.addContextData("Exception occured while editing Milestone ", aoExp);
			LOG_OBJECT.Error("error occured while editing Milestone ", aoExp);
			throw aoExp;
		}
		// handling exception other than ApplicationException
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while editing Milestone in ContractBudgetService ", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: updateMilestone method - failed to edit"
					+ aoCBMilestoneBean.getBudgetTypeId() + " \n");
			throw new ApplicationException("Error occured while editing Milestone", aoAppEx);
		}
		return loUpdateMilestone;
	}

	/**
	 * <p>
	 * This method is used for deleting the added rows in Milestone grid for a
	 * particular sub-budget.
	 * <ul>
	 * <li>Provider is able to Delete Milestone line items entirely on the
	 * Pending Submission budget submission for the newly added line items.</li>
	 * <li>Execute query id <b>deleteMilestoneDetails</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBMilestoneBean - CBMileStoneBean object containing key fields
	 * @param aoMybatisSession - SqlSession object
	 * @return loDeleteMileStone - Boolean status
	 * @throws ApplicationException - ApplicationException object
	 */
	public Boolean deleteMilestone(CBMileStoneBean aoCBMilestoneBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		Boolean loDeleteMilestone = true;
		Integer loStatus = HHSConstants.INT_ZERO;
		try
		{
			loStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoCBMilestoneBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_DELETE_MILESTONE_DETAILS,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_MILE_STONE_BEAN);
			if (loStatus <= HHSConstants.INT_ZERO)
			{
				loDeleteMilestone = false;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("error occured while deleting Milestone Details for budget type id " + aoCBMilestoneBean);
			aoExp.addContextData("Exception occured while deleting Milestone Details ", aoExp);
			LOG_OBJECT.Error("error occured while deleting Milestone Details ", aoExp);
			throw aoExp;
		}
		// handling exception other than ApplicationException
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while editing deleting milestone in ContractBudgetService ", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: deleteMilestone method - failed to delete"
					+ aoCBMilestoneBean.getBudgetTypeId() + " \n");
			throw new ApplicationException("Error occured while deleting Milestone", aoAppEx);
		}
		return loDeleteMilestone;
	}

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
	 * 
	 * @param aoCBGridBeanObj - CBGridBean object containing key fields
	 * @param aoMyBatisSession - SqlSession object
	 * @return List<CBOperationSupportBean> - returns list of bean of type
	 *         <CBOperationSupportBean>
	 * @throws ApplicationException - ApplicationException object
	 */
	public CBOperationSupportBean fetchOpAndSupportPageData(CBGridBean aoCBGridBeanObj, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		CBOperationSupportBean loCBOperationSupportBean = null;
		try
		{
			loCBOperationSupportBean = (CBOperationSupportBean) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_OP_SUPPORT_PAGE_DATA,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			setMoState("ContractBudgetService: fetchOpAndSupportPageData() passed");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("aoCBGridBeanObj : ", CommonUtil.convertBeanToString(aoCBGridBeanObj));
			LOG_OBJECT.Error("App Exception occured  at fetching ContractBudgetService: fetchRate() ", loAppEx);
			setMoState("ContractBudgetService: fetchOpAndSupportPageData() failed to fetch at subBudget-ID:" + loAppEx
					+ " \n");
			throw loAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieving in ContractBudgetService ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: "
					+ "fetchOpAndSupportPageData method - failed to retrieve for subBudget-ID"
					+ aoCBGridBeanObj.getSubBudgetID() + " \n");
			throw new ApplicationException("Exception occured while fetch in ContractBudgetService ", loAppEx);
		}
		return loCBOperationSupportBean;
	}

	/**
	 * <p>
	 * This method is used for fetching values in Operations and Support grid
	 * for a particular sub-budget based upon budget type as below: 1 = Budget
	 * Amendment; 2 = Contract Budget; 3 = Budget Modification; 4 = Budget
	 * Update <br/>
	 * <ul>
	 * <li>CBOperationSupportBean is used to populate values in grid</li>
	 * <li>Execute query id <b>fetchOperationAndSupportDetails</b></li>
	 * <li>Provider is able to fetch existing equipment details:</li>
	 * <li>1.the Amendment amount</li>
	 * <li>2.the FY Budget amount</li>
	 * <li>3.the Modification amount</li>
	 * <li>3.the Updated amount</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBGridBeanObj - CBGridBean object containing key fields
	 * @param aoMyBatisSession - SqlSession object
	 * @return List<CBOperationSupportBean> - returns list of bean of type
	 *         <CBOperationSupportBean>
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<CBOperationSupportBean> fetchOperationAndSupportDetails(CBGridBean aoCBGridBeanObj,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		List<CBOperationSupportBean> loCBOperationSupportBeanList = null;

		try
		{
			loCBOperationSupportBeanList = (List<CBOperationSupportBean>) DAOUtil.masterDAO(aoMyBatisSession,
					aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_FETCH_OPERATION_AND_SUPPORT_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			setMoState("ContractBudgetService: fetchOperationAndSupportDetails() passed");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("aoCBGridBeanObj", CommonUtil.convertBeanToString(aoCBGridBeanObj));
			LOG_OBJECT.Error(
					"App Exception occured  at fetching ContractBudgetService: fetchOperationAndSupportDetails() ",
					loAppEx);
			setMoState("ContractBudgetService: fetchOperationAndSupportDetails() failed to fetch at subBudget-ID:"
					+ aoCBGridBeanObj.getSubBudgetID() + " \n");
			throw loAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieving in ContractBudgetService ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchOperationAndSupportDetails method -"
					+ " failed to retrieve for subBudget-ID" + aoCBGridBeanObj.getSubBudgetID() + " \n");
			throw new ApplicationException("Exception occured while fetch in ContractBudgetService ", loAppEx);
		}
		return loCBOperationSupportBeanList;
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
	 * <li>Execute query id <b>editOperationAndSupportDetail</b></li>
	 * <li>If loCount is equal to zero then Execute query id
	 * <b>addOpAndSupprtForOther</b></li>
	 * <li>Execute query id <b>addOpAndSupprtForOther</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBOperationSupportBean - CBOperationSupportBean object
	 *            containing key fields
	 * @param aoMyBatisSession - SqlSession object
	 * @return Boolean - returns boolean status of edit/update query
	 * @throws ApplicationException - ApplicationException object
	 */
	public Boolean editOperationAndSupportDetails(CBOperationSupportBean aoCBOperationSupportBean,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		boolean lbEditStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoCBOperationSupportBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_EDIT_OPERATION_AND_SUPPORT_DETAILS, HHSConstants.MODEL_CB_OPERATION_SUPPORT_BEAN);

			if (aoCBOperationSupportBean.getOpAndSupportName() != null
					&& !HHSConstants.EMPTY_STRING.equals(aoCBOperationSupportBean.getOpAndSupportName()) )
				// Start R 8.2 QC 9358 allow change name back to Other
				//  && !HHSConstants.OTHER.equalsIgnoreCase(aoCBOperationSupportBean.getOpAndSupportName())
				// End R 8.2 QC 9358 allow change name back to Other - was 
			{
				Integer loOperSuppId = Integer.parseInt(aoCBOperationSupportBean.getId());

				Integer loCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loOperSuppId,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.CBY_FETCH_OP_AND_SUPPRT_FOR_OTHER, HHSConstants.INTEGER_CLASS_PATH);

				if (loCount == HHSConstants.INT_ZERO)
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoCBOperationSupportBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.CBY_ADD_OP_AND_SUPPRT_FOR_OTHER, HHSConstants.MODEL_CB_OPERATION_SUPPORT_BEAN);
				}
				else
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoCBOperationSupportBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.CBY_UPDATE_OP_AND_SUPPRT_FOR_OTHER,
							HHSConstants.MODEL_CB_OPERATION_SUPPORT_BEAN);
				}
			}
			lbEditStatus = true;
			setMoState("ContractBudgetService: editOperationAndSupportDetails() passed");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CB_OPERATION_SUPPORT_BEAN,
					CommonUtil.convertBeanToString(aoCBOperationSupportBean));
			LOG_OBJECT.Error("App Exception occured in ContractBudgetService: editOperationAndSupportDetails "
					+ "method:: ", loAppEx);
			setMoState("Transaction Failed::App Exception in ContractBudgetService: editOperationAndSupportDetails()"
					+ " method aoCBOperationSupportBean::" + aoCBOperationSupportBean + "\n");
			throw loAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetService: editOperationAndSupportDetails method:: ", loEx);
			loAppEx.addContextData(HHSConstants.CB_OPERATION_SUPPORT_BEAN,
					CommonUtil.convertBeanToString(aoCBOperationSupportBean));
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: editOperationAndSupportDetails method:: ",
					loAppEx);
			setMoState("Transaction Failed::Exception in ContractBudgetService: editOperationAndSupportDetails() method"
					+ " aoCBOperationSupportBean::" + aoCBOperationSupportBean + "\n");
			throw loAppEx;
		}
		return lbEditStatus;
	}

	/**
	 * <p>
	 * This method is used for fetching values in Equipment grid for a
	 * particular sub-budget based upon budget type as below: 1 = Budget
	 * Amendment; 2 = Contract Budget; 3 = Budget Modification; 4 = Budget
	 * Update <br/>
	 * <ul>
	 * <li>CBEquipmentBean is used to populate values in grid</li>
	 * <li>Provider is able to fetch existing equipment details:</li>
	 * <li>1.the number of Amendment Units and Amendment amount</li>
	 * <li>2.the number of Units and FY Budget amount</li>
	 * <li>3.the number of Modification Units and Modification amount</li>
	 * <li>3.the number of updated Units and updated amount</li>
	 * <li>Execute query id <b> fetchEquipmentDetails</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBGridBeanObj - CBGridBeanObj object containing key fields
	 * @param aoMyBatisSession - SqlSession object
	 * @return List<CBEquipmentBean> - returns list of bean of type
	 *         <CBEquipmentBean>
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<CBEquipmentBean> fetchEquipmentDetails(CBGridBean aoCBGridBeanObj, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<CBEquipmentBean> loCBEquipmentBeanList = null;

		try
		{
			loCBEquipmentBeanList = (List<CBEquipmentBean>) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_EQUIPMENT_DETAILS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			setMoState("ContractBudgetService: fetchEquipmentDetails() passed");
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CB_EQUIP_BEAN, CommonUtil.convertBeanToString(aoCBGridBeanObj));
			LOG_OBJECT.Error("App. Exception occured in ContractBudgetService: fetchEquipmentDetails method:: ",
					loAppEx);
			setMoState("Transaction Failed:: App Exception ContractBudgetService: fetchEquipmentDetails - failed"
					+ aoCBGridBeanObj + "\n");
			throw loAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetService: fetchEquipmentDetails method:: ", loEx);
			loAppEx.addContextData(HHSConstants.CB_EQUIP_BEAN, CommonUtil.convertBeanToString(aoCBGridBeanObj));
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: fetchEquipmentDetails method:: ", loAppEx);
			setMoState("Transaction Failed:: Exception ContractBudgetService: fetchEquipmentDetails - failed"
					+ aoCBGridBeanObj + "\n");
			throw loAppEx;
		}
		return loCBEquipmentBeanList;
	}

	/**
	 * <p>
	 * This method is used for adding values in Equipment grid for a particular
	 * sub-budget based upon budget type as below: 1 = Budget Amendment; 2 =
	 * Contract Budget; 3 = Budget Modification; 4 = Budget Update <br/>
	 * <ul>
	 * <li>CBEquipmentBean is used to populate values in grid</li>
	 * <li>Provider is able to add new equipment by creating a new row, typing
	 * in the equipment title and entering:</li>
	 * <li>1.the number of Amendment Units and Amendment amount</li>
	 * <li>2.the number of Units and FY Budget amount</li>
	 * <li>3.the number of Modification Units and Modification amount</li>
	 * <li>4.same as point 3 but in Update, final Total City Funded Budget
	 * Update Amount must be equal to the change in the FY Budget value</li>
	 * <li>Execute query id <b> addEquipmentDetails</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBEquipmentBean - CBEquipmentBean object containing key fields
	 * @param aoMyBatisSession - SqlSession object
	 * @return Boolean - returns boolean status of add query
	 * @throws ApplicationException ApplicationException object
	 */
	public Boolean addEquipmentDetails(CBEquipmentBean aoCBEquipmentBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Boolean loInsertStatus = false;

		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoCBEquipmentBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_ADD_EQUIPMENT_DETAILS, HHSConstants.MODEL_CB_EQUIPMENT_BEAN);
			setMoState("ContractBudgetService: addEquipmentDetails() passed");
			loInsertStatus = true;
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CB_EQUIP_BEAN, CommonUtil.convertBeanToString(aoCBEquipmentBean));
			LOG_OBJECT.Error("App. Exception occured in ContractBudgetService: addEquipmentDetails method:: ", loAppEx);
			setMoState("Transaction Failed:: App Exception ContractBudgetService: addEquipmentDetails - failed"
					+ aoCBEquipmentBean + "\n");
			throw loAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetService: addEquipmentDetails method:: ", loEx);
			loAppEx.addContextData(HHSConstants.CB_EQUIP_BEAN, CommonUtil.convertBeanToString(aoCBEquipmentBean));
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: addEquipmentDetails method:: ", loAppEx);
			setMoState("Transaction Failed:: Exception ContractBudgetService: addEquipmentDetails - failed"
					+ aoCBEquipmentBean + "\n");
			throw loAppEx;
		}
		return loInsertStatus;
	}

	/**
	 * <p>
	 * This method is used for editing/updating values in Equipment grid for a
	 * particular sub-budget based upon budget type as below: 1 = Budget
	 * Amendment; 2 = Contract Budget; 3 = Budget Modification; 4 = Budget
	 * Update <br/>
	 * <ul>
	 * <li>CBEquipmentBean is used to populate values in grid</li>
	 * <li>Provider is able to edit information for equipment line items:</li>
	 * <li>1.that have been added within the amendment budget</li>
	 * <li>2.that have previously been added</li>
	 * <li>3.that have been added within the Modification budget</li>
	 * <li>4.same as point 3 but in Update, final Total City Funded Budget
	 * Update Amount must be equal to the change in the FY Budget value</li>
	 * <li>Execute query id <b> editEquipmentDetails</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBEquipmentBean - CBEquipmentBean object containing key fields
	 * @param aoMyBatisSession - SqlSession object
	 * @return Boolean - returns boolean status of edit/update query
	 * @throws ApplicationException - ApplicationException object
	 */
	public Boolean editEquipmentDetails(CBEquipmentBean aoCBEquipmentBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		boolean lbEditStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoCBEquipmentBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.EDIT_EQUIPMENT_DETAILS, HHSConstants.MODEL_CB_EQUIPMENT_BEAN);

			setMoState("ContractBudgetService: editEquipmentDetails method - Equipment details updated successfully.");
			lbEditStatus = true;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CB_EQUIP_BEAN, CommonUtil.convertBeanToString(aoCBEquipmentBean));
			LOG_OBJECT
					.Error("App. Exception occured in ContractBudgetService: editEquipmentDetails method:: ", loAppEx);
			setMoState("Transaction Failed:: App Exception ContractBudgetService: editEquipmentDetails - failed"
					+ aoCBEquipmentBean + "\n");
			throw loAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetService: editEquipmentDetails method:: ", loEx);
			loAppEx.addContextData(HHSConstants.CB_EQUIP_BEAN, CommonUtil.convertBeanToString(aoCBEquipmentBean));
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: editEquipmentDetails method:: ", loAppEx);
			setMoState("Transaction Failed:: Exception ContractBudgetService: editEquipmentDetails - failed"
					+ aoCBEquipmentBean + "\n");
			throw loAppEx;
		}
		return lbEditStatus;

	}

	/**
	 * <p>
	 * This method is used for deleting values from Equipment grid for a
	 * particular sub-budget based upon budget type as below: 1 = Budget
	 * Amendment; 2 = Contract Budget; 3 = Budget Modification; 4 = Budget
	 * Update <br/>
	 * <ul>
	 * <li>CBEquipmentBean is used to populate values in grid</li>
	 * <li>Provider is able to delete equipment line items entirely on:</li>
	 * <li>1.the Pending Submission amendment budget submission</li>
	 * <li>2.the Pending Submission budget submission or if no invoices exist
	 * against the equipment item</li>
	 * <li>3.the draft Modification budget submission</li>
	 * <li>4.same as point 3 but in Update, final Total City Funded Budget
	 * Update Amount must be equal to the change in the FY Budget value</li>
	 * <li>Execute query id <b> deleteEquipmentDetails</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBEquipmentBean - CBEquipmentBean object containing key fields
	 * @param aoMyBatisSession - SqlSession object
	 * @return Boolean - returns boolean status of delete query
	 * @throws ApplicationException - ApplicationException object
	 */
	public Boolean deleteEquipmentDetails(CBEquipmentBean aoCBEquipmentBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		boolean lbDelStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoCBEquipmentBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_DELETE_EQUIPMENT_DETAILS, HHSConstants.MODEL_CB_EQUIPMENT_BEAN);

			setMoState("ContractBudgetService: deleteEquipmentDetails method -"
					+ " Equipment details deleted successfully.");

			lbDelStatus = true;
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CB_EQUIP_BEAN, CommonUtil.convertBeanToString(aoCBEquipmentBean));
			LOG_OBJECT.Error("App Exception occured in ContractBudgetService: deleteEquipmentDetails method:: ",
					loAppEx);
			setMoState("Transaction Failed::App Exception ContractBudgetService: deleteEquipmentDetails method - failed"
					+ "aoCBEquipmentBean::" + aoCBEquipmentBean + "\n");
			throw loAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetService: deleteEquipmentDetails:: ", loEx);
			loAppEx.addContextData(HHSConstants.CB_EQUIP_BEAN, CommonUtil.convertBeanToString(aoCBEquipmentBean));
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: deleteEquipmentDetails method:: ", loAppEx);
			setMoState("Transaction Failed::Exception ContractBudgetService: deleteEquipmentDetails method - failed"
					+ "aoCBEquipmentBean::" + aoCBEquipmentBean + "\n");
			throw loAppEx;
		}
		return lbDelStatus;
	}

	/**
	 * <ul>
	 * <li>This method is used to fetch data for
	 * ContractServices,ContractServicesModification,ContractServicesUpdate &
	 * ContractServicesAmendment Screen</li>
	 * <li>This method is invoked when loadgrid operation is performed for
	 * getContractedServicesFetch Transaction Id</li>
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
	 * <li>If it would, display error message: “!</li>
	 * <li>Entered value would cause the Proposed Budget to fall below the
	 * amount already invoiced for the line item. Please enter a new value.”</li>
	 * <li>Execute query id <b> fetchContractedServicesConsultants</b></li>
	 * </ul>
	 * </ol>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoCBGridBeanObj CBGridBean attributes.
	 * @return aoCBContractedServicesBean
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<ContractedServicesBean> fetchContractedServicesConsultants(SqlSession aoMyBatisSession,
			CBGridBean aoCBGridBeanObj) throws ApplicationException
	{
		List<ContractedServicesBean> loCBContractedServicesBean = null;
		try
		{
			aoCBGridBeanObj.setSubHeader(HHSConstants.ONE);
			loCBContractedServicesBean = (List<ContractedServicesBean>) DAOUtil.masterDAO(aoMyBatisSession,
					aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_FETCH_CONTRACTED_SERVICES_CONSULTANTS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: fetchContractedServices method:: ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchContractedServices method"
					+ " - failed Exception occured while fetching\n");
			throw loAppEx;
		}

		return loCBContractedServicesBean;
	}

	/**
	 * This method is used to fetch data for sub-contractors for contracted
	 * services <li>Execute query id <b>
	 * fetchContractedServicesSubContractors<b></li>
	 * 
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoCBGridBeanObj CBGridBean attributes.
	 * @return loCBContractedServicesBean
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<ContractedServicesBean> fetchContractedServicesSubContractors(SqlSession aoMyBatisSession,
			CBGridBean aoCBGridBeanObj) throws ApplicationException
	{
		List<ContractedServicesBean> loCBContractedServicesBean = null;
		try
		{
			aoCBGridBeanObj.setSubHeader(HHSConstants.TWO);
			loCBContractedServicesBean = (List<ContractedServicesBean>) DAOUtil.masterDAO(aoMyBatisSession,
					aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_FETCH_CONTRACTED_SERVICES_SUB_CONTRACTORS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: fetchContractedServices method:: ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchContractedServices "
					+ "method - failed Exception occured while fetching\n");
			throw loAppEx;
		}

		return loCBContractedServicesBean;
	}

	/**
	 * This method is used to fetch data for vendors for contracted services
	 * <ul>
	 * <li>Execute query id <b> fetchContractedServicesVendors</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoCBGridBeanObj CBGridBean attributes.
	 * @return loCBContractedServicesBean
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<ContractedServicesBean> fetchContractedServicesVendors(SqlSession aoMyBatisSession,
			CBGridBean aoCBGridBeanObj) throws ApplicationException
	{
		List<ContractedServicesBean> loCBContractedServicesBean = null;
		try
		{
			aoCBGridBeanObj.setSubHeader(HHSConstants.THREE);
			loCBContractedServicesBean = (List<ContractedServicesBean>) DAOUtil.masterDAO(aoMyBatisSession,
					aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_FETCH_CONTRACTED_SERVICES_VENDORS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: fetchContractedServices method:: ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchContractedServices "
					+ "method - failed Exception occured while fetching\n");
			throw loAppEx;
		}

		return loCBContractedServicesBean;
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
	 * <li>Execute query id <b> addContractedServices</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoCBGridBeanObj CBGridBean attributes.
	 * @return loVal
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public Integer addContractedServices(SqlSession aoMyBatisSession, ContractedServicesBean aoCBGridBeanObj)
			throws ApplicationException
	{
		Integer loVal;
		try
		{
			loVal = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_ADD_CONTRACTED_SERVICES,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CONTRACTED_SERVICES_BEAN);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: addContractedServices method:: ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: addContractedServices method"
					+ " - failed Exception occured while inserting data\n");
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
	 * <li>Execute query id <b> editContractedServices</b></li>
	 * </ol>
	 * 
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoCBGridBeanObj CBGridBean attributes.
	 * @return boolean
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public boolean editContractedServices(SqlSession aoMyBatisSession, ContractedServicesBean aoCBGridBeanObj)
			throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_EDIT_CONTRACTED_SERVICES,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CONTRACTED_SERVICES_BEAN);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: editContractedServices method:: ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: editContractedServices method"
					+ " - failed Exception occured while updating data\n");
			throw loAppEx;
		}
		return true;
	}

	/**
	 * <ol>
	 * <li>This method is used to delete data for
	 * ContractServices,ContractServicesModification,ContractServicesUpdate &
	 * ContractServicesAmendment Screen</li>
	 * <li>This method is invoked when gridOperation is performed for
	 * getContractedServicesDelete Transaction Id</li>
	 * <li>Execute query id <b> delContractedServices</b></li>
	 * </ol>
	 * 
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoCBGridBeanObj CBGridBean attributes.
	 * @return boolean
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public boolean deleteContractedServices(SqlSession aoMyBatisSession, ContractedServicesBean aoCBGridBeanObj)
			throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_DEL_CONTRACTED_SERVICES,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CONTRACTED_SERVICES_BEAN);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: deleteContractedServices method:: ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: deleteContractedServices method"
					+ " - failed Exception occured while deleting data\n");
			throw loAppEx;
		}
		return true;
	}

	/**
	 * This method is used to fetch non-grid data for contracted services grid.
	 * <ul>
	 * <li>Execute query id <b> fetchNonGridContractedServices</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoCBGridBeanObj CBGridBean attributes.
	 * @return ContractedServicesBean <li>In This return type Non-Grid data is
	 *         fetched.</li>
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public ContractedServicesBean fetchNonGridContractedServices(SqlSession aoMyBatisSession, CBGridBean aoCBGridBeanObj)
			throws ApplicationException
	{
		ContractedServicesBean loCBContractedServicesBean = null;
		try
		{
			loCBContractedServicesBean = (ContractedServicesBean) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_FETCH_NON_GRID_CONTRACTED_SERVICES, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		/**
		 * This catch block throws Application Exception that is caught by
		 * controller.
		 */
		catch (ApplicationException loAppEx)
		{
			// Logger to keep log for all exception caught inside
			// fetchNonGridContractedServices method
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: fetchNonGridContractedServices method:: ",
					loAppEx);
			// Setting mostate to failed.
			setMoState("Transaction Failed:: ContractBudgetService: fetchNonGridContractedServices "
					+ "method - failed Exception occured while fetching\n");
			throw loAppEx;
		}

		return loCBContractedServicesBean;
	}

	/**
	 * This is the fetchRate() method for returning the rate details for the
	 * particular contract Budget.
	 * 
	 * <ul>
	 * <li>fetch all the rate information based on the select statement on basis
	 * of subBudgetId</li>
	 * <li>Execute query id <b> fetchRateList</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session Object.
	 * @param aoCBGridBeanObj CBGridBean attributes.
	 * @return loRateList
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<RateBean> fetchRate(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj)
			throws ApplicationException
	{
		List<RateBean> loRateList = null;
		try
		{
			loRateList = (List<RateBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_FETCH_RATE_LIST,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("subBudget-ID : ", aoCBGridBeanObj.getSubBudgetID());
			LOG_OBJECT.Error("Exception occured  at fetching ContractBudgetService: fetchRate() ", loAppEx);
			setMoState("ContractBudgetService: fetchRate() failed to fetch at subBudget-ID:"
					+ aoCBGridBeanObj.getSubBudgetID() + " \n");
			throw loAppEx;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieving in ContractBudgetService ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchRate method"
					+ " - failed to retrieve for subBudget-ID" + aoCBGridBeanObj.getSubBudgetID() + " \n");
			throw new ApplicationException("Exception occured while fetch in ContractBudgetService ", loAppEx);
		}
		return loRateList;
	}

	/**
	 * This is the updateRate() method for updating Rate details for the
	 * particular contract Budget.
	 * 
	 * <ul>
	 * <li>Provider is able to Edit the Rate line items that have previously
	 * been added.</li>
	 * <li>Execute query id <b> updateRateList</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session Object.
	 * @param aoRateBean RateBean attributes sent.
	 * @return Boolean
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public Boolean updateRate(SqlSession aoMybatisSession, RateBean aoRateBean) throws ApplicationException
	{
		Boolean lbRate = true;
		Integer loContractEpin = HHSConstants.INT_ZERO;
		try
		{
			loContractEpin = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoRateBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_UPDATE_RATE_LIST,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN);
			if (loContractEpin <= HHSConstants.INT_ZERO)
			{
				lbRate = false;
			}
			// checking for edit status
			if (lbRate)
			{
				setMoState("ContractBudgetService: updateRate() edit successfully.");
			}
			else
			{
				setMoState("ContractBudgetService: updateRate() failed to edit.");
				throw new ApplicationException(
						"Error occured while edit at ContractBudgetService: updateRate() for id:" + aoRateBean.getId());
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("ID : ", aoRateBean.getId());
			LOG_OBJECT.Error("Exception occured  edit at ContractBudgetService: updateRate() ", loAppEx);
			setMoState("ContractBudgetService: updateRate() failed to edit at id:" + aoRateBean.getId() + " \n");
			throw loAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while edit in ContractBudgetService ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: updateRate method - failed to edit"
					+ aoRateBean.getId() + " \n");
			throw new ApplicationException("Exception occured while edit in ContractBudgetService ", loAppEx);
		}
		return lbRate;
	}

	/**
	 * This is the insertRate() method for adding the rate details for the
	 * particular contract Budget.
	 * 
	 * <ul>
	 * <li>Provider is able to Add new Rate by creating a new row</li>
	 * <li>Execute query id <b> insertRateList</li>
	 * </ul>
	 * 
	 * @param aiCurrentSeq Integer Parameter for sequence.
	 * @param aoMybatisSession Mybatis Session Object.
	 * @param aoRateBean RateBean attribute.
	 * @return lbinsertRate
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public Boolean insertRate(Integer aiCurrentSeq, SqlSession aoMybatisSession, RateBean aoRateBean)
			throws ApplicationException
	{
		Boolean loInsertRate = true;
		Integer loRateCount = HHSConstants.INT_ZERO;
		try
		{
			aoRateBean.setId(String.valueOf(aiCurrentSeq));
			loRateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoRateBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_INSERT_RATE_LIST,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN);
			if (loRateCount <= HHSConstants.INT_ZERO)
			{
				loInsertRate = false;
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("ID : ", aoRateBean.getId());
			LOG_OBJECT.Error("Exception occured  insert at ContractBudgetService: insertRate() ", loAppEx);
			setMoState("ContractBudgetService: insertRate() failed to edit at id:" + aoRateBean.getId() + " \n");
			throw loAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while inserting in ContractBudgetService ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: updateRate method - failed to insert"
					+ aoRateBean.getId() + " \n");
			throw new ApplicationException("Exception occured while insert in ContractBudgetService ", loAppEx);
		}
		return loInsertRate;
	}

	/**
	 * This is the deleteRate() method for deleting the rate details for the
	 * particular contract Budget.
	 * 
	 * <ul>
	 * <li>Provider is able to Delete Rate line items entirely.</li>
	 * <li>Execute query id <b> deleteRateList</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session Object.
	 * @param aoRateBean Ratebean attribute set.
	 * @return aoCBMileStoneBean
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public Boolean deleteRate(SqlSession aoMybatisSession, RateBean aoRateBean) throws ApplicationException
	{
		Boolean lbDeleteRate = true;
		Integer loContractEpin = HHSConstants.INT_ZERO;
		try
		{
			loContractEpin = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoRateBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_DELETE_RATE_LIST,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_RATE_BEAN);
			if (loContractEpin <= HHSConstants.INT_ZERO)
			{
				lbDeleteRate = false;
			}
			// checking for delete status
			if (lbDeleteRate)
			{
				setMoState("ContractBudgetService: deleteRate() edit successfully.");
			}
			else
			{
				setMoState("ContractBudgetService: deleteRate() failed to edit.");
				throw new ApplicationException(
						"Error occured while edit at ContractBudgetService: deleteRate() for id:" + aoRateBean.getId());
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("ID : ", aoRateBean.getId());
			LOG_OBJECT.Error("Exception occured  edit at ContractBudgetService: deleteRate() ", loAppEx);
			setMoState("ContractBudgetService: deleteRate() failed to edit at id:" + aoRateBean.getId() + " \n");
			throw loAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while edit in ContractBudgetService ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: deleteRate method - failed to edit"
					+ aoRateBean.getId() + " \n");
			throw new ApplicationException("Exception occured while edit in ContractBudgetService ", loAppEx);
		}
		return lbDeleteRate;
	}

	/**
	 * <p>
	 * This service class is invoked through fetchContractSummary transaction id
	 * for Contract budget screen
	 * <ul>
	 * <li>
	 * This method fetchContractSummary will get the contract Information on the
	 * basis of contractId</li>
	 * <li>Execute query id <b> fetchBudgetStatus</b></li>
	 * <li>Execute query id <b> fetchParentContractId</b></li>
	 * <li>Execute query id <b> fetchParentBudgetId</b></li>
	 * <li>Execute query id <b> fetchContractSummary</b></li>
	 * </ul>
	 * Updated methiod in R4.
	 * </p>
	 * 
	 * @param aoMybatisSession Mybatis Session Object.
	 * @param aoHashMap HashMap set.
	 * @return loContractList ContractList object.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("rawtypes")
	public ContractList fetchContractSummary(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		ContractList loContractList = null;
		String lsParentContractId = null;
		String lsParentBudgetId = null;
		String lsStatus = null;
		try
		{
			if (HHSConstants.ONE.equals(aoHashMap.get(HHSConstants.BUDGET_TYPE)))
			{
				lsStatus = (String) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBY_FETCH_BUDGET_STATUS, HHSConstants.JAVA_UTIL_MAP);

				// Fetch Parent ContractId
				lsParentContractId = (String) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_PARENT_CONTRACT_ID, HHSConstants.JAVA_UTIL_MAP);
				// Fetch ParentBudgetId
				lsParentBudgetId = (String) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_PARENT_BUDGET_ID, HHSConstants.JAVA_UTIL_MAP);

				aoHashMap.put(HHSConstants.PARENT_CONTRACT_ID, lsParentContractId);
				aoHashMap.put(HHSConstants.PARENT_BUDGET_ID, lsParentBudgetId);

				loContractList = (ContractList) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.FETCH_CONTRACT_SUMMARY_AMENDMENT, HHSConstants.JAVA_UTIL_HASH_MAP);
				loContractList.setBudgetStatus(lsStatus);

				// R4 Added for S346.60 and S346.61
				HashMap loAmendDetails = (HashMap) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.GET_AWARD_EPIN_AND_AMOUNT,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				loContractList.setAmendEpin(String.valueOf(loAmendDetails.get(HHSConstants.EXT_EPIN)));
				loContractList.setAmendAmount(String.valueOf(loAmendDetails.get(HHSConstants.CLC_CONTRACT_AMOUNT)));
				//[Start] R7.3.0 QC9018 Add Amendment title
				loContractList.setAmendmentTitle(String.valueOf(loAmendDetails.get(HHSConstants.AMENDMENT_TITLE.toUpperCase())));
                //[End] R7.3.0 QC9018 Add Amendment title
			}
			else
			{
				loContractList = (ContractList) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_CONTRACT_SUMMARY,
						HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			// Release 5 Contract Restriction
			Integer loContractRestrictCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSR5Constants.FETCH_CONTRACT_FOR_RETRICTED_COUNT,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			if (loContractRestrictCount != 0)
			{
				loContractList.setContractAccess(false);
			}
			// Release 5 Contract Restriction
			loContractList.setContractAgencyName(HHSUtil.getAgencyName(loContractList.getContractAgencyName()));
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			loAppEx.addContextData(HHSConstants.CONTRACTID, aoHashMap);
			LOG_OBJECT.Error("Exception occured while retrieveing Contract Information in ContractBudgetService ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchContractSummary method - failed to fetch"
					+ aoHashMap + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			LOG_OBJECT.Error("Exception occured while retrieveing Contract Information in ContractBudgetService ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchContractSummary method - failed to fetch"
					+ aoHashMap + " \n");
			throw new ApplicationException("Error occured while retrieving Contract Summary", loAppEx);
		}

		return loContractList;
	}

	/**
	 * This service class is invoked through fetchSubBudgetSummary transaction
	 * id for Contract budget screen
	 * <ul>
	 * <li>This method fetchSubBudgetSummary will get the SubBudget Information
	 * on the basis of budgetId</li>
	 * <li>Execute query id <b> fetchSubBudgetSummaryPrint</b></li>
	 * <li>Execute query id <b> fetchSubBudgetSummary</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session Object.
	 * @param aoHashMap HashMap set.
	 * @return loContractList
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */

	@SuppressWarnings("unchecked")
	public List<CBGridBean> fetchSubBudgetSummary(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		List<CBGridBean> loSubBudgetList = null;
		try
		{

			if (aoHashMap.get(HHSConstants.SUBBUDGET_ID) != null)
			{
				loSubBudgetList = (List<CBGridBean>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.CBY_FETCH_SUB_BUDGET_SUMMARY_PRINT, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			else
			{
				loSubBudgetList = (List<CBGridBean>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_SUB_BUDGET_SUMMARY,
						HHSConstants.JAVA_UTIL_HASH_MAP);

			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.BUDGET_ID, aoHashMap);
			LOG_OBJECT.Error("Exception occured while retrieveing SubBudget Information in ContractBudgetService ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchSubBudgetSummary method - failed to fetch"
					+ aoHashMap + " \n");
			throw loAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieveing Contract Information in ContractBudgetService ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchSubBudgetSummary method - failed to fetch"
					+ aoHashMap + " \n");
			throw new ApplicationException("Error occured while retrieving Contract Summary", loAppEx);
		}
		return loSubBudgetList;
	}

	/**
	 * This service class is invoked through fetchFyBudgetSummary transaction id
	 * for Contract budget screen
	 * <ul>
	 * <li>
	 * This method fetchFyBudgetSummary will get the Fiscal Year contract
	 * Information on the basis of contractId</li>
	 * <li>Execute query id <b> fetchBudgetType</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session Object.
	 * @param aoHashMap HashMap set.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return loContractList
	 */
	public BudgetDetails fetchFyBudgetSummary(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		BudgetDetails loFyBudget = new BudgetDetails();
		String lsBudgetType = HHSConstants.EMPTY_STRING;
		try
		{
			lsBudgetType = (String) DAOUtil.masterDAO(aoMybatisSession, aoHashMap.get(HHSConstants.BUDGET_ID_WORKFLOW),
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_BUDGET_TYPE,
					HHSConstants.JAVA_LANG_STRING);
			if (lsBudgetType != null && lsBudgetType.equalsIgnoreCase(HHSConstants.FOUR))
			{
				loFyBudget = (BudgetDetails) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_FY_UPDATE_BUDGET_SUMMARY,
						HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			else if (lsBudgetType != null && lsBudgetType.equalsIgnoreCase(HHSConstants.THREE))
			{
				loFyBudget = (BudgetDetails) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.FETCH_FY_MODIFICATION_BUDGET_SUMMARY, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			else
			{
				loFyBudget = (BudgetDetails) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_FY_BUDGET_SUMMARY,
						HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			if (loFyBudget == null)
			{
				loFyBudget = new BudgetDetails();
				return loFyBudget;
			}
			else
			{
				setBudgetDetails(aoMybatisSession, aoHashMap, loFyBudget, lsBudgetType);
			}
		}
		// Application Exception handled here
		catch (ApplicationException loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			loAppEx.addContextData(HHSConstants.CONTRACT_ID_WORKFLOW, aoHashMap);
			LOG_OBJECT.Error(
					"Exception occured while retrieveing Fiscal Year Contract Information in ContractBudgetService ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchFyBudgetSummary method - failed to fetch"
					+ aoHashMap + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			LOG_OBJECT.Error(
					"Exception occured while retrieveing Fiscal Year Contract Information in ContractBudgetService ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchFyBudgetSummary method - failed to fetch"
					+ aoHashMap + " \n");
			throw new ApplicationException("Error occured while retrieving Fiscal Year Contract Summary", loAppEx);
		}
		return loFyBudget;
	}

	/**
	 * Release 3.6.0 Enhancement id 6484 This service class is invoked through
	 * fetchFyBudgetSummary transaction id for Contract budget screen
	 * <ul>
	 * <li>
	 * This method fetchFyBudgetSummary will get the Fiscal Year contract
	 * Information on the basis of contractId</li>
	 * <li>Execute query id <b> fetchBudgetType</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session Object.
	 * @param aoHashMap HashMap set.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return loContractList
	 */
	public Integer subBudgetSiteCount(SqlSession aoMybatisSession, String asBudgetId) throws ApplicationException
	{
		Integer loSubBudgetSiteCount = 0;
		try
		{
			loSubBudgetSiteCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asBudgetId,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.SUB_BUDGET_SITE_COUNT,
					HHSConstants.JAVA_LANG_STRING);

		}
		// Application Exception handled here
		catch (ApplicationException loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			loAppEx.addContextData(HHSConstants.CONTRACT_ID_WORKFLOW, asBudgetId);
			LOG_OBJECT.Error(
					"Exception occured while retrieveing Fiscal Year Contract Information in ContractBudgetService ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchFyBudgetSummary method - failed to fetch"
					+ asBudgetId + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			LOG_OBJECT.Error(
					"Exception occured while retrieveing Fiscal Year Contract Information in ContractBudgetService ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchFyBudgetSummary method - failed to fetch"
					+ asBudgetId + " \n");
			throw new ApplicationException("Error occured while retrieving Fiscal Year Contract Summary", loAppEx);
		}
		return loSubBudgetSiteCount;
	}

	/**
	 * This method set the Budget details parameters on the basis of budgetType
	 * 
	 * @param aoMybatisSession SqlSession Object
	 * @param aoHashMap HashMap Object
	 * @param loFyBudget BudgetDetails Object
	 * @param lsBudgetType BudgetType Object
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	private void setBudgetDetails(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap,
			BudgetDetails loFyBudget, String lsBudgetType) throws ApplicationException
	{
		BigDecimal loInvoiceAmount = null;
		if (lsBudgetType != null
				&& (lsBudgetType.equalsIgnoreCase(HHSConstants.THREE) || lsBudgetType
						.equalsIgnoreCase(HHSConstants.FOUR)))
		{
			loInvoiceAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.GET_INVOICE_AMOUNT_FOR_MODIFICATION,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			if (lsBudgetType.equalsIgnoreCase(HHSConstants.FOUR))
			{
				BigDecimal loApprovedBudgetAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.GET_UPDATE_AMOUNT_FOR_BUDGET,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				if (loApprovedBudgetAmount != null)
				{
					loFyBudget.setApprovedBudget(loApprovedBudgetAmount);
				}
				else
				{
					loFyBudget.setApprovedBudget(BigDecimal.ZERO);
				}
				loFyBudget.setUpdateAmount(loFyBudget.getUpdateAmount().add(loFyBudget.getApprovedBudget()));
			}
		}
		else
		{
			loInvoiceAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_GET_INVOICE_AMOUNT,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		loFyBudget.setInvoicedAmount((loInvoiceAmount));

		BigDecimal loActPaidAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession,
				aoHashMap.get(HHSConstants.BUDGET_ID_WORKFLOW), HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
				HHSConstants.INV_FETCH_FY_BUDGET_ACTUAL_PAID, HHSConstants.JAVA_LANG_STRING);
		// Added for R6: Returned Payment
		String unRecoupedAmount = (String) DAOUtil.masterDAO(aoMybatisSession,
				aoHashMap.get(HHSConstants.BUDGET_ID_WORKFLOW), HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
				HHSR5Constants.GET_UNRECOUPED_AMOUNT, HHSConstants.JAVA_LANG_STRING);

		loFyBudget.setUnRecoupedAmount(unRecoupedAmount);
		// R6 changes end
		loFyBudget.setYtdActualPaid(loActPaidAmount);
		loFyBudget.setRemainingAmount(loFyBudget.getApprovedBudget().subtract(loInvoiceAmount));
	}

	/**
	 * This method is triggered to get the information in session.
	 * <ul>
	 * <li>A hashmap is passed to get the data.</li>
	 * <li>Execute query id <b> getCbGridDataForSession</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session Object.
	 * @param aoHashMap HashMap set.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return loCBGridBean
	 */
	@SuppressWarnings("rawtypes")
	public CBGridBean getCbGridDataForSession(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		CBGridBean loCBGridBean = null;
		try
		{
			loCBGridBean = (CBGridBean) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.GET_CB_GRID_DATA_FOR_SESSION,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		// Application Exception handled here
		catch (ApplicationException loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			loAppEx.addContextData(HHSConstants.BUDGET_ID, aoHashMap);
			LOG_OBJECT.Error("Exception occured while retrieveing CbGridDataForSession Information in InvoiceService ",
					loAppEx);
			setMoState("Transaction Failed:: InvoiceService: getCbGridDataForSession method - failed to fetch"
					+ aoHashMap + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			LOG_OBJECT.Error("Exception occured while retrieveing CbGridDataForSession in InvoiceService ", loAppEx);
			setMoState("Transaction Failed:: InvoiceService: getCbGridDataForSession method - failed to fetch"
					+ aoHashMap + " \n");
			throw new ApplicationException("Error occured while insertRate", loAppEx);
		}

		return loCBGridBean;
	}

	/**
	 * This method is triggered to get the Assignment Grid information. <li>This
	 * service class is invoked through fetchAssignmentSummary transaction id
	 * for Contract budget/Modification/Update/Amendment screen</li> <li>This
	 * method fetchAssignmentSummary will get the session Information on the
	 * basis of budgetId & contractId</li>
	 * <ul>
	 * <li>A aoCBGridBeanObj CBGridBean object is passed to get the data.</li>
	 * <li>Execute query id <b> fetchAssignmentSummary</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session Object.
	 * @param aoCBGridBeanObj CBGridBean attribute set.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return loAssignmentsSummaryBean List<AssignmentsSummaryBean>
	 */
	@SuppressWarnings("unchecked")
	public List<AssignmentsSummaryBean> fetchAssignmentSummary(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj,
			MasterBean aoMasterBeanObj) throws ApplicationException
	{
		List<AssignmentsSummaryBean> loAssignmentsList = null;
		try
		{
			if (aoCBGridBeanObj.getBudgetTypeId().equalsIgnoreCase(HHSConstants.TWO))
			{
				// For ContractBudget(Base) - fetch Assignment details for
				// Contract
				loAssignmentsList = (List<AssignmentsSummaryBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_FETCH_ASSIGNMENT_SUMMARY,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			}
			else
			{
				String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_BUDGET_APPROVED);
				// Only For Amendment fetch from XML
				if (aoMasterBeanObj != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
				{
					loAssignmentsList = aoMasterBeanObj.getAssignmentsSummaryBean();
				}
				else
				{
					// For Contract Budget Update/Modification/Amendment - Fetch
					// Assignment Details for Parent Budget
					loAssignmentsList = (List<AssignmentsSummaryBean>) DAOUtil.masterDAO(aoMybatisSession,
							aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.CBY_FETCH_ASSIGNMENT_SUMMARY_FOR_PARENT_BUDGET,
							HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				}
			}

		}
		// Application Exception handled here
		catch (ApplicationException loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			loAppEx.addContextData(HHSConstants.BUDGET_ID, aoCBGridBeanObj.getContractBudgetID());
			LOG_OBJECT.Error(
					"Exception occured  at fetching AssignmentGrid ContractBudgetService: fetchAssignmentSummary() ",
					loAppEx);
			setMoState("ContractBudgetService: fetchAssignmentSummary() failed to fetch AssignmentGrid at Budget-ID:"
					+ aoCBGridBeanObj.getContractBudgetID() + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			LOG_OBJECT.Error("Exception occured while retrieving AssignmentGrid in ContractBudgetService ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchAssignmentSummary method"
					+ " - failed to retrieve for Budget-ID" + aoCBGridBeanObj.getContractBudgetID() + " \n");
			throw new ApplicationException("Exception occured while fetch AssignmentGrid in ContractBudgetService ",
					loAppEx);
		}
		return loAssignmentsList;
	}

	/**
	 * This method will insert the details of the Budget documents uploaded or
	 * added by an provider
	 * <ul>
	 * <li>Get the parameter map from the channel</li>
	 * <li>Execute query with ID <b>insertBudgetDocumentDetails</b> from
	 * proposal mapper.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession sql Session
	 * @param aoParamMap Parameter map Object
	 * @return number of raws inserted
	 * @throws ApplicationException throws application exception
	 */
	public Integer insertBudgetDocumentDetails(SqlSession aoMybatisSession, Map<String, Object> aoParamMap)
			throws ApplicationException
	{
		Integer liRowsUpdated = null;
		try
		{
			liRowsUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_INSERT_BUDGET_DOCUMENT_DETAILS,
					HHSConstants.JAVA_UTIL_MAP);
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error Occured while fething the proposal document list :");
			throw loExp;
		}
		return liRowsUpdated;
	}

	/**
	 * This method will insert the details of the Invoice documents uploaded or
	 * added by an provider
	 * <ul>
	 * <li>Get the parameter map from the channel</li>
	 * <li>Execute query with ID <b>insertInvoiceDocumentDetails</b> from
	 * proposal mapper.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession sql Session
	 * @param aoParamMap Parameter map Object
	 * @return number of raws inserted
	 * @throws ApplicationException throws application exception
	 */
	public Integer insertInvoiceDocumentDetails(SqlSession aoMybatisSession, Map<String, Object> aoParamMap)
			throws ApplicationException
	{
		Integer liRowsUpdated = null;
		try
		{
			liRowsUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_INSERT_INVOICE_DOCUMENT_DETAILS,
					HHSConstants.JAVA_UTIL_MAP);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error Occured while fething the proposal document list :");
			throw loExp;
		}
		return liRowsUpdated;
	}

	/**
	 * This method is triggered Rate Grid.
	 * <ul>
	 * <li>Get the Sequence for rateId from Rate</li>
	 * <li>An integer value is returned which determines the sequence of rateId
	 * from Rate table.</li>
	 * <li>Execute query id <b> getSeqForRate</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session Object.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return liCurrentSeq
	 */
	public int getSeqForRate(SqlSession aoMybatisSession) throws ApplicationException
	{
		int liCurrentSeq = HHSConstants.INT_ZERO;
		liCurrentSeq = (Integer) DAOUtil.masterDAO(aoMybatisSession, null,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_GET_SEQ_FOR_RATE, null);
		return liCurrentSeq;
	}

	/**
	 * This method will get the RFP documents details Summary from data base
	 * 
	 * <ul>
	 * <li>1. Execute the query "fetchFinancialDocuments" specified in the
	 * ContractBudegetMapper.</li>
	 * <li>2. Return the doc Summary.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - valid SqlSession object
	 * @param aoParamMap - consist of Contract and BudgetId
	 * @return loFinancialDocList of ExtendedDocument bean
	 * @throws ApplicationException throws application exception when any error
	 *             situation occurred
	 */
	@SuppressWarnings("unchecked")
	public List<ExtendedDocument> fetchFinancialDocuments(SqlSession aoMybatisSession, Map<String, Object> aoParamMap)
			throws ApplicationException
	{
		List<ExtendedDocument> loFinancialDocList = null;
		try
		{	//Added for R6 - Fetching document for Returned Payment
			if (null != aoParamMap && null != aoParamMap.get(HHSConstants.RETURN_PAYMENT_DETAIL_ID)
					&& !aoParamMap.get(HHSConstants.RETURN_PAYMENT_DETAIL_ID).toString().isEmpty())
			{
				loFinancialDocList = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.RPD_FETCH_RETURNED_PAYMENT_DOCUMENTS, HHSConstants.JAVA_UTIL_MAP);
			}
			//Added for R6 - Fetching document for Returned Payment
			else
			{
			loFinancialDocList = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_FETCH_FINANCIAL_DOCUMENTS,
					HHSConstants.JAVA_UTIL_MAP);
			}
			setMoState("document details details fetched successfully ");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting Financial document Details");
			throw loExp;
		}
		return loFinancialDocList;
	}

	/**
	 * This method will insert the details of the contract documents uploaded or
	 * added by an provider
	 * <ul>
	 * <li>Get the parameter map from the channel</li>
	 * <li>Execute query with ID <b>insertBudgetDocumentDetails</b> from
	 * contract budget mapper.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession sql Session
	 * @param aoParamMap Parameter map Object
	 * @return number of raws inserted
	 * @throws ApplicationException throws application exception
	 */
	public Integer insertContractDocumentDetails(SqlSession aoMybatisSession, Map<String, Object> aoParamMap)
			throws ApplicationException
	{
		Integer liRowsUpdated = null;
		try
		{

			liRowsUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_INSERT_CONTRACT_DOCUMENT_DETAILS, HHSConstants.JAVA_UTIL_MAP);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error Occured while fething the proposal document list :");
			throw loExp;
		}
		return liRowsUpdated;
	}

	/**
	 * This method will delete all the details corresponding to the selected
	 * procurement and the document
	 * 
	 * execute query id <b>deleteRfpDocumentDetails</b> from Procurementmapper
	 * return the number of rows deleted. if the procurement status is Release
	 * then it will execute <b>deleteRfpAddendumDocumentDetails</b>
	 * 
	 * @param aoMybatisSession SqlSession Object
	 * @param aoParameterMap Map with procurementId and documentId
	 * @return int number of rows deleted
	 * @throws ApplicationException throws application exception when any error
	 *             occurred.
	 */
	public int removeFinancialDocs(SqlSession aoMybatisSession, Map<String, String> aoParameterMap)
			throws ApplicationException
	{
		int liRowsDeleted = HHSConstants.INT_ZERO;

		try
		{

			String lsDocumentUploadTo = (String) aoParameterMap.get(HHSConstants.HDN_TABLE_NAME);
			if (HHSConstants.CONTRACT_DOCUMENT_TABLE.equalsIgnoreCase(lsDocumentUploadTo))
			{
				liRowsDeleted = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParameterMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.CBY_DELETE_CONTRACT_FINANCIAL_DOC, HHSConstants.JAVA_UTIL_MAP);
			}
			else if (HHSConstants.BUDGET_DOCUMENT_TABLE.equalsIgnoreCase(lsDocumentUploadTo))
			{
				liRowsDeleted = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParameterMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_DELETE_BUDGET_FINANCIAL_DOC,
						HHSConstants.JAVA_UTIL_MAP);
			}
			//Added for R6- Deleting document for returned payment
			else if (HHSConstants.RETURNED_PAYMENT_DOCUMENT.equalsIgnoreCase(lsDocumentUploadTo))
			{
				liRowsDeleted = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParameterMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.RPD_DELETE_RETURNED_PAYMENT_FINANCIAL_DOC, HHSConstants.JAVA_UTIL_MAP);
			}
			//Added for R6- Deleting document for returned payment end
			else
			{
				liRowsDeleted = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParameterMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.CBY_DELETE_INVOICE_FINANCIAL_DOC, HHSConstants.JAVA_UTIL_MAP);
			}
			setMoState("Rfp document details Deleted successfully ");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error Occured while Deleting Document Details in removeFinancialDocs");
			throw loExp;
		}
		return liRowsDeleted;
	}

	/**
	 * This method set the budget status in Database on the basis of properties
	 * in input hashmap object.
	 * <ul>
	 * <li>Execute query id <b> setContractBudgetStatus</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoHMWFRequiredProps HashMap attribute set.
	 * @return boolean
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("rawtypes")
	public boolean setContractBudgetStatus(SqlSession aoMyBatisSession, HashMap aoHMWFRequiredProps)
			throws ApplicationException
	{
		boolean lbStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoHMWFRequiredProps, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_SET_CONTRACT_BUDGET_STATUS, HHSConstants.JAVA_UTIL_HASH_MAP);
			lbStatus = true;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			setMoState("Error Occured while executing query setContractBudgetStatus");
			LOG_OBJECT.Error("Exception occured while executing query setContractBudgetStatus ", loAppEx);
			throw loAppEx;
		}
		return lbStatus;
	}

	/**
	 * <p>
	 * This method sets amendment contract status to pending approval when all
	 * amendment budgets are in pending approval status
	 * <ul>
	 * <li>Execute query id <b> getAmendmentBudgetsCount</b></li>
	 * <li>Execute query id <b>setAmendmentContractStatusPendingApproval </b></li>
	 * </ul>
	 * Updated Method in R4
	 * </p>
	 * 
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoHMWFRequiredProps HashMap attribute set.
	 * @return boolean
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("rawtypes")
	public boolean setAmendmentContractStatus(SqlSession aoMyBatisSession, HashMap aoHMWFRequiredProps)
			throws ApplicationException
	{
		boolean lbStatus = false;
		try
		{
			HashMap loBudgetCountMap = (HashMap) DAOUtil.masterDAO(aoMyBatisSession, aoHMWFRequiredProps,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.GET_AMENDMENT_BUDGETS_COUNT,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			if (((BigDecimal) loBudgetCountMap.get(HHSConstants.BUDGET_PENDING_APPROVAL_COUNT)).intValue() == ((BigDecimal) loBudgetCountMap
					.get(HHSConstants.TOTAL_BUDGET_COUNT)).intValue())
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoHMWFRequiredProps,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.SET_AMEND_CONTRACT_STATUS_PEND_APPROVAL, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			lbStatus = true;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			setMoState("Error Occured while executing query setContractBudgetStatus");
			LOG_OBJECT.Error("Exception occured while executing query setContractBudgetStatus ", loAppEx);
			throw loAppEx;
		}
		return lbStatus;
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
	 * @param aoPersonnelServiceBudgetBean PersonnelService BudgetBean set.
	 * @param asTransactionName Transaction name attribute set.
	 */

	private void setEmployeeTypeInBean(PersonnelServiceBudget aoPersonnelServiceBudgetBean, String asTransactionName)
	{
		if (asTransactionName.equals(HHSConstants.CBY_SALARIED_EMPLOYEE_GRID_ADD))
		{
			aoPersonnelServiceBudgetBean.setEmpType(HHSConstants.ONE);
		}
		else if (asTransactionName.equals(HHSConstants.CBY_HOURLY_EMPLOYEE_GRID_ADD))
		{
			aoPersonnelServiceBudgetBean.setEmpType(HHSConstants.TWO);
		}
		else if (asTransactionName.equals(HHSConstants.CBY_SEASONAL_EMPLOYEE_GRID_ADD))
		{
			aoPersonnelServiceBudgetBean.setEmpType(HHSConstants.THREE);
		}
	}

	// Start: Updated in R6
	/**
	 * This method is used to fetch non-grid data which needs to be shown on
	 * Personnel Services screen of Contract budget
	 * <ul>
	 * <li>Execute query id <b> fetchTotalSalary</b></li>
	 * <li>Execute query id <b>fetchTotalFringes </b></li>
	 * <li>Execute query id <b>fetchSalariedYTDInvoicedAmount </b></li>
	 * <li>Execute query id <b>fetchFringesYTDInvoicedAmount</b></li>
	 * <li>Added in R6: Execute query id <b>fetchPsSummaryPositions</b></li>
	 * </ul>
	 * 
	 * @param aoCBGridBeanObj CBGridBean
	 * @param aoMybatisSession SqlSession
	 * @param aoMasterBean MasterBean
	 * @return loPersonnelServicesData PersonnelServicesData
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */

	public PersonnelServicesData fetchPersonnelServiceData(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj,
			MasterBean aoMasterBean) throws ApplicationException
	{
		PersonnelServicesData loPersonnelServicesData = new PersonnelServicesData();
		BigDecimal loTotalSalariedYtdInvoicedAmount = null;
		BigDecimal loTotalFringesYtdInvoicedAmount = null;
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);

		try
		{
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetTypeId().equals(HHSConstants.ONE)
					&& aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loPersonnelServicesData = fetchPersonnelServiceDataFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{
				String loTotalSalaryAmount = (String) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_TOTAL_SALARY,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				if (loTotalSalaryAmount == null || loTotalSalaryAmount.isEmpty())
				{
					loTotalSalaryAmount = HHSConstants.ZERO;
				}
				String loTotalFringesAmount = (String) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_TOTAL_FRINGES,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				if (aoCBGridBeanObj.getBudgetTypeId() != null
						&& Integer.parseInt(aoCBGridBeanObj.getBudgetTypeId()) != HHSConstants.INT_TWO)
				{
					CBGridBean loCBGridBeanObj = new CBGridBean();
					loCBGridBeanObj.setContractBudgetID(aoCBGridBeanObj.getParentBudgetId());
					loCBGridBeanObj.setSubBudgetID(aoCBGridBeanObj.getParentSubBudgetId());
					loTotalSalariedYtdInvoicedAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession,
							loCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.FETCH_SALARIED_YTD_AMOUNT, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
					loTotalFringesYtdInvoicedAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, loCBGridBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_FRINGES_YTD_AMOUNT,
							HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				}
				else if (aoCBGridBeanObj.getBudgetTypeId() != null
						&& Integer.parseInt(aoCBGridBeanObj.getBudgetTypeId()) == HHSConstants.INT_TWO)
				{
					loTotalSalariedYtdInvoicedAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession,
							aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.FETCH_SALARIED_YTD_AMOUNT, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
					loTotalFringesYtdInvoicedAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_FRINGES_YTD_AMOUNT,
							HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				}
				if (loTotalFringesAmount == null)
				{
					loTotalFringesAmount = HHSConstants.ZERO;
				}

				loPersonnelServicesData.setTotalSalaryAndFringeAmount(new BigDecimal(loTotalSalaryAmount)
						.add(new BigDecimal(loTotalFringesAmount)));
				loPersonnelServicesData.setTotalSalaryAmount(new BigDecimal(loTotalSalaryAmount));

				loPersonnelServicesData.setTotalFringeAmount(new BigDecimal(loTotalFringesAmount));
				if (loTotalSalariedYtdInvoicedAmount != null && loTotalFringesYtdInvoicedAmount != null)
				{
					loPersonnelServicesData.setTotalYtdInvoicedAmount((loTotalSalariedYtdInvoicedAmount)
							.add(loTotalFringesYtdInvoicedAmount));
				}
				else
				{
					loPersonnelServicesData.setTotalYtdInvoicedAmount(new BigDecimal(HHSConstants.ZERO));
				}
				if ((new BigDecimal(loTotalSalaryAmount).compareTo(BigDecimal.ZERO)) != HHSConstants.INT_ZERO)
				{
					if (!loTotalSalaryAmount.contains(HHSConstants.DOT))
					{
						loTotalSalaryAmount += HHSConstants.ZERO_AFTER_DECIMAL;
					}
					if (!loTotalFringesAmount.contains(HHSConstants.DOT))
					{
						loTotalFringesAmount += HHSConstants.ZERO_AFTER_DECIMAL;
					}
					loPersonnelServicesData.setFringePercentage((new BigDecimal(loTotalFringesAmount).multiply(
							new BigDecimal(HHSConstants.HUNDRED), new MathContext(HHSConstants.INT_HUNDRED))).divide(
							new BigDecimal(loTotalSalaryAmount, new MathContext(HHSConstants.INT_HUNDRED)),
							MathContext.DECIMAL128));
				}
				else
				{
					loPersonnelServicesData.setFringePercentage(BigDecimal.ZERO);
				}
				// Changes for R6 Starts
				loPersonnelServicesData.setTotalPositions((String) DAOUtil.masterDAO(aoMybatisSession,
						aoCBGridBeanObj.getSubBudgetID(), HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSR5Constants.FETCH_PS_SUMMARY_POSITIONS, HHSConstants.JAVA_LANG_STRING));
				// Changes for R6 ends
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured for personnel services "
					+ ": fetchPersonnelServiceData ", loExp);
			LOG_OBJECT.Error("ApplicationException occured for personnel services : fetchPersonnelServiceData ", loExp);
			setMoState("ApplicationException occured for personnel services for sub budget id = "
					+ aoCBGridBeanObj.getContractBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured for personnel services : fetchPersonnelServiceData ", loExp);
			loAppEx.addContextData("Exception occured for personnel services : fetchPersonnelServiceData ", loExp);
			LOG_OBJECT.Error("Exception occured for personnel services : fetchPersonnelServiceData ", loExp);
			setMoState("Exception occured while fetching non-grid data for personnel services for sub budget id = "
					+ aoCBGridBeanObj.getContractBudgetID());
			throw loAppEx;
		}
		return loPersonnelServicesData;
	}

	// End: Updated in R6

	/**
	 * <p>
	 * This method is used to set budget status on final Approve and returned
	 * for revision in case of contract Review Task.
	 * <ul>
	 * <li>IUpdate the status of the budget</li>
	 * <li>Execute query id <b> setContractBudgetStatusForReviewTask</b></li>
	 * <li>Execute query id <b> updateAmendmentContractStatus</b></li>
	 * </ul>
	 * Updated Method in R4
	 * </p>
	 * 
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param aoFinalFinish FinalFinish parameter.
	 * @param aoTaskDetailsBean TaskDetailsBean parameter.
	 * @param asBudgetStatus BudgetStatus parameter.
	 * @throws Exception an exception object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void setContractBudgetStatusForReviewTask(SqlSession aoMyBatisSession, Boolean aoFinalFinish,
			TaskDetailsBean aoTaskDetailsBean, String asBudgetStatus) throws Exception
	{
		try
		{
			if (aoFinalFinish)
			{
				Map loHashMap = new HashMap<String, String>();
				loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
				loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoTaskDetailsBean.getBudgetId());
				loHashMap.put(HHSConstants.STATUS_ID, asBudgetStatus);
				loHashMap.put(HHSConstants.MODIFY_BY, aoTaskDetailsBean.getUserId());
				DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.CBY_SET_CONTRACT_BUDGET_STATUS_FOR_REVIEW_TASK, HHSConstants.JAVA_UTIL_MAP);

				if (aoTaskDetailsBean.getEntityType().equals(HHSConstants.TASK_BUDGET_AMENDMENT))
				{
					if (asBudgetStatus.equals(HHSConstants.CBL__83))
					{
						DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
								HHSConstants.UPDATE_AMENDMENT_CONTRACT_STATUS, HHSConstants.JAVA_UTIL_MAP);
					}
					else if (asBudgetStatus.equals(HHSConstants.CBL_86))
					{
						DAOUtil.masterDAO(aoMyBatisSession, aoTaskDetailsBean,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
								HHSConstants.UPDATE_AMEND_CONTRACT_STATUS_PEND_REGISTRATION,
								HHSConstants.CS_TASK_DETAILS_BEAN);
					}
				}

				setMoState("Transaction Success:: ContractBudgetService:setContractBudgetStatusForReviewTask"
						+ " method - success to update record " + " \n");
			}

		}
		// catch any application exception thrown from the code due to UPDATE
		// statement and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException occured while executing query setContractBudgetStatusForReviewTask ",
							loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService:setContractBudgetStatusForReviewTask"
					+ " method - failed to update record " + " \n");
			loAppEx.addContextData("BudgetId passed: ", aoTaskDetailsBean.getBudgetId());
			throw loAppEx;
		}
		// catch any null exception thrown from the code due to UPDATE
		// statement and throw it
		// forward
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in setContractBudgetStatusForReviewTask ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService:setContractBudgetStatusForReviewTask method"
					+ " - failed to update record " + " \n");
			loAppEx = new ApplicationException(
					"Exception occured while executing query in setContractBudgetStatusForReviewTask", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * <p>
	 * This method is called when user clicks on finish task button on contract
	 * configuration task and new fiscal year task configuration The purpose of
	 * this method is to initialise all tabs in contract budget screen that has
	 * fixed set of line item with default entries
	 * 
	 * </p>
	 * <ul>
	 * <li>Gets Contract id and fiscals year as input parameters</li>
	 * <li>Fetch list of all subBudget and get Budget id for fiscal year and
	 * contract id provided as input parameter</li>
	 * </li>For each subBudget call all tabs that has fixed line items default
	 * entries</li>
	 * <li>Execute query id <b> fetchOriginalContractCount</b></li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBean used to get contract id and fiscal year
	 * @param aoMyBatisSession SqlSession object
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public void insertStandardRowsSubBudgetLevel(TaskDetailsBean aoTaskDetailsBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<CBGridBean> loCBGridBeanList = null;
		try
		{
			setMoState("AACCBB: 1. insertStandardRowsSubBudgetLevel method" + aoTaskDetailsBean.getContractId());

			// insert contract id of type original
			Map<String, Object> loQueryMap = new HashMap<String, Object>();

			loQueryMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());

			Integer loReplicaContractId = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_ORIGINAL_FETCH_CONTRACT_COUNT,
					HHSConstants.JAVA_UTIL_MAP);

			setMoState("AACCBB: 2. loReplicaContractId" + loReplicaContractId);

			/** Rel 3.16.0 QC 6814 BEGIN we need to add rows for only Type 1 & 3 */
			String lsContractType = (String) DAOUtil.masterDAO(aoMyBatisSession, aoTaskDetailsBean.getContractId(),
					HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_CONTRACT_TYPE,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("AACCBB: 10. lsContractType=" + lsContractType);

			// we need to add the Original row of type 6, ONLY for Base &
			// Renewal Contracts
			if (lsContractType.equalsIgnoreCase(HHSConstants.ONE)
					|| lsContractType.equalsIgnoreCase(HHSConstants.THREE))
			{
				// Create replica of Contract only if already does not exist
				if (loReplicaContractId == null)
				{
					loReplicaContractId = createContractReplica(aoMyBatisSession, loQueryMap);
				}
			}
			/** Rel 3.16.0 QC 6814 END */

			// insert replica of contract financial for original contract id
			loQueryMap.put(HHSConstants.CONTRACT_ID_ORIGINAL, loReplicaContractId);

			if (aoTaskDetailsBean.getTaskName().equals(HHSConstants.TASK_CONTRACT_CONFIGURATION))
			{
				updateContractFinForOriginalContract(aoMyBatisSession, loQueryMap);
				//Added in R7 to update isOldPI flag for contracts in pending configuration
				deleteDefaultPIForNewConf(aoMyBatisSession, aoTaskDetailsBean, HHSR5Constants.BOOLEAN_TRUE);	
				//R7 changes end
			}

			String lsContractId = aoTaskDetailsBean.getContractId();
			String lsFiscalYearId = aoTaskDetailsBean.getStartFiscalYear();

			loQueryMap.put(HHSConstants.CLC_FISCAL_YEAR_ID, lsFiscalYearId);

			setMoState("AACCBB: 7. lsContractId" + lsContractId);
			setMoState("AACCBB: 8. lsFiscalYearId" + lsFiscalYearId);

			if (null != lsContractId && !lsContractId.trim().isEmpty() && null != lsFiscalYearId
					&& !lsFiscalYearId.trim().isEmpty())
			{
				loCBGridBeanList = (List<CBGridBean>) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_FETCH_SUB_BUDGET_ID_LIST,
						HHSConstants.JAVA_UTIL_MAP);
                //R7 changes start
				boolean lbInsertPIForAmendUpdate = HHSR5Constants.BOOLEAN_FALSE;
				if (HHSConstants.TASK_CONTRACT_UPDATE.equals(aoTaskDetailsBean.getTaskName())
						|| HHSConstants.TASK_AMENDMENT_CONFIGURATION.equals(aoTaskDetailsBean.getTaskName()))
				{
					lbInsertPIForAmendUpdate = validateAndDeleteDefaultPI(aoMyBatisSession, aoTaskDetailsBean,
							HHSR5Constants.BOOLEAN_TRUE);
				}
				//R7 changes end
				for (CBGridBean loCBGridBean : loCBGridBeanList)
				{
					loCBGridBean.setModifyByAgency(aoTaskDetailsBean.getUserId());
					loCBGridBean.setModifyByProvider(HHSConstants.EMPTY_STRING);

					// For operation and support Static Line Items entries
					insertStandardRowsOperationSupport(loCBGridBean, aoMyBatisSession);

					// R7 changes start
					//Stop making default entries for program income in case of
					// new contract and new fiscal year
					// If Amendment and Update, make default entries, if PI
					// opted in base budget
					LOG_OBJECT.Info("Checking if condition for inserting program income default entries "
							+ aoTaskDetailsBean.getTaskName() + aoTaskDetailsBean.getEntityId());
					if (!HHSConstants.TASK_CONTRACT_CONFIGURATION.equals(aoTaskDetailsBean.getTaskName())
							&& !HHSConstants.TASK_NEW_FY_CONFIGURATION.equals(aoTaskDetailsBean.getTaskName())
							&& !lbInsertPIForAmendUpdate)
					{
						LOG_OBJECT.Info("Inserting default line items..." + aoTaskDetailsBean.getTaskName());
						insertStandardRowsProgramIncome(loCBGridBean, aoMyBatisSession);
					}
					//R7 changes end
					// For Professional Services Static Line Items entries
					insertStandardRowsProfServices(loCBGridBean, aoMyBatisSession);

					// For Utilities Static Line Items entries
					insertStandardRowsUtilities(loCBGridBean, aoMyBatisSession);

					// For Unallocated Funds Static Line Items entries
					// QC 8394 R 7.9.0 Make Unallocated Funds non-default line item
					// insertUnallocatedFunds(loCBGridBean, aoMyBatisSession);

					// For Indirect rate Static Line Items entries
					insertStandardRowsIndirectRate(loCBGridBean, aoMyBatisSession);

					// For Personnel services (fringe benefits)
					insertStandardRowsPersonnelServices(loCBGridBean, aoMyBatisSession);

				}
			}
			setMoState("Method passed successfully ContractBudgetService: insertStandardRowsSubBudgetLevel method");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CB_GRID_BEAN_LIST, CommonUtil.convertBeanToString(loCBGridBeanList));
			LOG_OBJECT.Error(
					"App Exception occured in ContractBudgetService: insertStandardRowsSubBudgetLevel method:: ",
					loAppEx);
			setMoState("App Exception occured in ContractBudgetService: insertStandardRowsSubBudgetLevel method");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			// Context Data is added into Exception object and Log is updated
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetService: insertStandardRowsSubBudgetLevel method:: ", loEx);
			loAppEx.addContextData(HHSConstants.CB_GRID_BEAN_LIST, CommonUtil.convertBeanToString(loCBGridBeanList));
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: insertStandardRowsSubBudgetLevel method:: ",
					loAppEx);
			setMoState("Exception occured in ContractBudgetService: insertStandardRowsSubBudgetLevel method");
			throw loAppEx;
		}
	}

	/**
	 * <p>
	 * This is a private method used to create replica of entries in contract
	 * financial table with contract type original for contract type base
	 * 
	 * </p>
	 * <ul>
	 * <li>deletes all entries for original contract, if any exists</li>
	 * <li>insert all entries</li>
	 * <li>Execute query id <b> deleteContractFinancialEntries</b></li>
	 * <li>Execute query id <b> insertContractFinReplicaForOriginal</b></li>
	 * <li>Execute query id <b>deleteContractFinFundingEntries</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SqlSession Object
	 * @param aoOrigContractId contract id with type original
	 * @param aoBaseContractId contract id with type base
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	private void updateContractFinForOriginalContract(SqlSession aoMyBatisSession, Map<String, Object> aoHashMap)
			throws ApplicationException
	{
		try
		{
			setMoState("AACCBB 5: updateContractFinForOriginalContract" + aoHashMap);
			// 1. delete all entries from contract_financials, if any, for
			// contract id of type original
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_DELETE_CONTRACT_FINANCIAL_ENTRIES, HHSConstants.JAVA_UTIL_MAP);

			// 2. insert replica of base contract entries in contract_financials
			// for original contract
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_INSERT_CONTRACT_FIN_REPLICA_FOR_ORIGINAL, HHSConstants.JAVA_UTIL_MAP);

			// 3. delete all entries from CONTRACT_FIN_FUNDING_STREAM, if any,
			// for contract id of type original
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_DELETE_CONTRACT_FIN_FUNDING_ENTRIES, HHSConstants.JAVA_UTIL_MAP);

			// 2. insert replica of base contract entries in
			// CONTRACT_FIN_FUNDING_STREAM for original contract
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_INSERT_CONTRACT_FIN_FUNDING_REPLICA_FOR_ORIGINAL, HHSConstants.JAVA_UTIL_MAP);

			setMoState("Method passed successfully ContractBudgetService: updateContractFinForOriginalContract method");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("loHashMap", aoHashMap);
			LOG_OBJECT.Error(
					"App Exception occured in ContractBudgetService: updateContractFinForOriginalContract method",
					loAppEx);
			setMoState("Error occurred in ContractBudgetService: updateContractFinForOriginalContract method"
					+ aoHashMap);
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			// Context Data is added into Exception object and Log is updated
			ApplicationException loAppEx = new ApplicationException("Error occurred in ContractBudgetService:"
					+ " updateContractFinForOriginalContract method:: ", loEx);
			loAppEx.addContextData("loCBGridBeanList", aoHashMap);
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: updateContractFinForOriginalContract method",
					loAppEx);
			setMoState("Error occurred in ContractBudgetService: updateContractFinForOriginalContract method"
					+ aoHashMap);
			throw loAppEx;
		}
	}

	/**
	 * <p>
	 * This is a private method used to insert standard rows in S316 Operation
	 * and Support screen. These line items are added as part of fixed input for
	 * Operation and support as default entries
	 * 
	 * </p>
	 * <ul>
	 * <li>Checks if the entries for a sub_budget already exist</li>
	 * <li>Fetch master Data for Operation and Support line items</li>
	 * <li>Inserts default data (with values '0') against each fixed operation
	 * and support line Item</li>
	 * <li>Execute query id <b> fetchOperationSupportItemsCount</b></li>
	 * <li>Execute query id <b> fetchOperationSuppMasterList</b></li>
	 * </ul>
	 * 
	 * @param aoCBGridBean CBGridBean object containing budget and subBudget id
	 * @param aoMyBatisSession SqlSession Object
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	private void insertStandardRowsOperationSupport(CBGridBean aoCBGridBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		try
		{
			// 1. fetch your tab data for this subBudget
			Integer loCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean.getSubBudgetID(),
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_FETCH_OPERATION_SUPPORT_ITEMS_COUNT, HHSConstants.JAVA_LANG_STRING);

			// 2. if no rows are fetched then query master table to prepare data
			if (null == loCount || loCount == HHSConstants.INT_ZERO)
			{
				List<CBOperationSupportBean> loCBOperationSupportBeanMasterList = (List<CBOperationSupportBean>) DAOUtil
						.masterDAO(aoMyBatisSession, null, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
								HHSConstants.CBY_FETCH_OPERATION_SUPP_MASTER_LIST, null);

				if (null != loCBOperationSupportBeanMasterList
						&& loCBOperationSupportBeanMasterList.size() > HHSConstants.INT_ZERO)
				{
					// 3. traverse master bean and prepare line item data for
					// operation support
					for (CBOperationSupportBean loCBOperationSupportBeanTemp : loCBOperationSupportBeanMasterList)
					{
						CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();

						loCBOperationSupportBean.setId(loCBOperationSupportBeanTemp.getId());
						loCBOperationSupportBean.setContractBudgetID(aoCBGridBean.getContractBudgetID());
						loCBOperationSupportBean.setSubBudgetID(aoCBGridBean.getSubBudgetID());
						loCBOperationSupportBean.setFyBudget(HHSConstants.STRING_ZERO);
						loCBOperationSupportBean.setModifyByAgency(aoCBGridBean.getModifyByAgency());
						loCBOperationSupportBean.setModifyByProvider(aoCBGridBean.getModifyByProvider());

						DAOUtil.masterDAO(aoMyBatisSession, loCBOperationSupportBean,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
								HHSConstants.CBY_INSERT_STANDARD_ROWS_OPERATION_SUPPORT,
								HHSConstants.MODEL_CB_OPERATION_SUPPORT_BEAN);
					}
				}
			}
			setMoState("Method passed successfully ContractBudgetService: insertStandardRowsOperationSupport method");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("loCBGridBeanList", CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error(
					"App Exception occured in ContractBudgetService: insertStandardRowsOperationSupport method",
					loAppEx);
			setMoState("Error occurred in ContractBudgetService: insertStandardRowsOperationSupport method"
					+ aoCBGridBean.getContractBudgetID() + " and SubBudget Id::" + aoCBGridBean.getSubBudgetID());
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			// Context Data is added into Exception object and Log is updated
			ApplicationException loAppEx = new ApplicationException("Error occurred in ContractBudgetService:"
					+ " insertStandardRowsOperationSupport method:: ", loEx);
			loAppEx.addContextData("loCBGridBeanList", CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: insertStandardRowsOperationSupport method",
					loAppEx);
			setMoState("Error occurred in ContractBudgetService: insertStandardRowsOperationSupport method"
					+ aoCBGridBean.getContractBudgetID() + " and SubBudget Id::" + aoCBGridBean.getSubBudgetID());
			throw loAppEx;
		}
	}

	/**
	 * <p>
	 * This is a private method used to insert standard rows in S323 Unallocated
	 * Funds screen These line items are added as part of fixed input for
	 * Unallocated Funds as default entries
	 * 
	 * </p>
	 * <ul>
	 * <li>Checks if the entries for a sub_budget already exist</li>
	 * <li>Inserts default data (with values '0') against each Unallocated Funds
	 * <li>Execute query id <b> insertUnallocatedFunds</b></li>
	 * </li>
	 * </ul>
	 * 
	 * @param aoCBGridBean CBGridBean object containing budget and subBudget id
	 * @param aoMyBatisSession SqlSession Object
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */

	private void insertUnallocatedFunds(CBGridBean aoCBGridBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		try
		{
			// 1. fetch your tab data for this subBudget
			Integer loCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean.getSubBudgetID(),
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_UNALLOCATED_FUNDS_COUNT,
					HHSConstants.JAVA_LANG_STRING);

			// 2. if no rows are fetched then insert the data
			if (null == loCount || loCount == HHSConstants.INT_ZERO)
			{
				UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();

				// Setting the variable for functionality related Bean from
				// generic bean
				loUnallocatedFundsBean.setBudgetId(Integer.parseInt(aoCBGridBean.getContractBudgetID()));
				loUnallocatedFundsBean.setSubBudgetID(aoCBGridBean.getSubBudgetID());
				loUnallocatedFundsBean.setModifyByAgency(aoCBGridBean.getModifyByAgency());
				loUnallocatedFundsBean.setModifyByProvider(aoCBGridBean.getModifyByProvider());

				DAOUtil.masterDAO(aoMyBatisSession, loUnallocatedFundsBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.INSERT_UNALLOCATED_FUNDS,
						HHSConstants.UNALLOCATED_FUNDS_BEAN);
			}
			// setting the transaction state
			setMoState("Method passed successfully ContractBudgetService: insertUnallocatedFunds method");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppExp)
		{
			// setting the transaction state for exception and setting the
			// context
			loAppExp.addContextData(HHSConstants.CB_GRID_BEAN_LIST, CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("App Exception occured in ContractBudgetService: insertUnallocatedFunds method", loAppExp);
			setMoState("Error occurred in ContractBudgetService: insertUnallocatedFunds method"
					+ aoCBGridBean.getContractBudgetID() + " and SubBudget Id::" + aoCBGridBean.getSubBudgetID());
			throw loAppExp;
		}
		catch (Exception loEx)
		{
			// Context Data is added into Exception object and Log is updated
			ApplicationException loAppEx = new ApplicationException("Error occurred in ContractBudgetService:"
					+ " insertUnallocatedFunds method:: ", loEx);
			loAppEx.addContextData("loCBGridBeanList", CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: insertUnallocatedFunds method", loAppEx);
			setMoState("Error occurred in ContractBudgetService: insertUnallocatedFunds method"
					+ aoCBGridBean.getContractBudgetID() + " and SubBudget Id::" + aoCBGridBean.getSubBudgetID());
			throw loAppEx;
		}
	}

	/**
	 * This method is used to insert the default entries into the program_income
	 * table for the first time for the fixed line items
	 * <ul>
	 * <li>Checks if the entries for a sub_budget are already exists</li>
	 * <li>fetched the Master Data for Program Income line items</li>
	 * <li>Inserts default data (with values '0') against every Program Income
	 * line items</li>
	 * <li>Execute query id <b> fetchProgramIncomeMasterTypes</b></li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBean
	 * @param aoCBGridBean CBGridBean set.
	 * @param aoMybatisSession Mybatis Session Object.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	private void insertStandardRowsProgramIncome(CBGridBean aoCBGridBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		try
		{
			// 1. fetch tab data for this subBudget
			Integer loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_PROGRAM_INCOME_ITEMS_COUNT,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			// 2. if no rows are fetched then query master table to prepare data
			if (null == loCount || loCount == HHSConstants.INT_ZERO)
			{
				CBGridBean loCBGridBeanObj = new CBGridBean();
				List<CBProgramIncomeBean> loCBProgramIncomeBeanForMasterTypes = (List<CBProgramIncomeBean>) DAOUtil
						.masterDAO(aoMybatisSession, loCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
								HHSConstants.FETCH_PROGRAM_INCOME_MASTER_TYPES,
								HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

				if (null != loCBProgramIncomeBeanForMasterTypes && !loCBProgramIncomeBeanForMasterTypes.isEmpty())
				{
					for (CBProgramIncomeBean loTempCBProgramIncomeBean : loCBProgramIncomeBeanForMasterTypes)
					{
						// Fill mandatory data required for new line item
						loTempCBProgramIncomeBean.setContractBudgetID(aoCBGridBean.getContractBudgetID());
						loTempCBProgramIncomeBean.setContractID(aoCBGridBean.getContractID());
						loTempCBProgramIncomeBean.setSubBudgetID(aoCBGridBean.getSubBudgetID());
						loTempCBProgramIncomeBean.setModifyByAgency(aoCBGridBean.getModifyByAgency());
						loTempCBProgramIncomeBean.setModifyByProvider(aoCBGridBean.getModifyByProvider());

						DAOUtil.masterDAO(aoMybatisSession, loTempCBProgramIncomeBean,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.INSERT_PROGRAM_INCOME,
								HHSConstants.PROGRAM_INCOME_BEAN);
					}
				}
			}
			setMoState("Method passed successfully ContractBudgetService: insertStandardRowsProgramIncome method");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CB_GRID_BEAN_LIST, CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("App Exception occured in ContractBudgetService: insertStandardRowsProgramIncome method",
					loAppEx);
			setMoState("Error occurred in ContractBudgetService: insertStandardRowsProgramIncome method"
					+ aoCBGridBean.getContractBudgetID() + " and SubBudget Id::" + aoCBGridBean.getSubBudgetID());
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			// Context Data is added into Exception object and Log is updated
			ApplicationException loAppEx = new ApplicationException("Error occurred in ContractBudgetService:"
					+ " insertStandardRowsProgramIncome method:: ", loEx);
			loAppEx.addContextData("loCBGridBeanList", CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: insertStandardRowsProgramIncome method",
					loAppEx);
			setMoState("Error occurred in ContractBudgetService: insertStandardRowsProgramIncome method"
					+ aoCBGridBean.getContractBudgetID() + " and SubBudget Id::" + aoCBGridBean.getSubBudgetID());
			throw loAppEx;
		}
	}

	/**
	 * This method is used to insert the default entries into the program_income
	 * table for the first time for the fixed line items
	 * <ul>
	 * <li>Checks if the entries for a sub_budget are already exists</li>
	 * <li>fetched the Master Data for Program Income line items</li>
	 * <li>Inserts defaulet data (with values '0') against every Program Income
	 * line items</li>
	 * <li>Execute query id <b> fetchProfServicesItemsCount</b></li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBean
	 * @param aoCBGridBean CBGridBean set.
	 * @param aoMybatisSession Mybatis Session Object.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	private void insertStandardRowsProfServices(CBGridBean aoCBGridBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		try
		{
			Integer liProfSubBudgetId = Integer.parseInt(aoCBGridBean.getSubBudgetID());

			// 1. fetch tab data for this subBudget
			Integer loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, liProfSubBudgetId,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_FETCH_PROF_SERVICES_ITEMS_COUNT,
					HHSConstants.INTEGER_CLASS_PATH);

			// 2. if no rows are fetched then query master table to prepare data
			if (null == loCount || loCount == HHSConstants.INT_ZERO)
			{

				List<CBProfessionalServicesBean> loCBProfServicesBeanForMasterTypes = (List<CBProfessionalServicesBean>) DAOUtil
						.masterDAO(aoMybatisSession, aoCBGridBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
								HHSConstants.CBY_FETCH_PROFESSIONAL_SERVICES_TYPE_ID,
								HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

				if (null != loCBProfServicesBeanForMasterTypes && !loCBProfServicesBeanForMasterTypes.isEmpty())
				{
					for (CBProfessionalServicesBean loTempCBProfServiceBean : loCBProfServicesBeanForMasterTypes)
					{
						loTempCBProfServiceBean.setContractBudgetID(aoCBGridBean.getContractBudgetID());
						loTempCBProfServiceBean.setContractID(aoCBGridBean.getContractID());
						loTempCBProfServiceBean.setSubBudgetID(aoCBGridBean.getSubBudgetID());
						loTempCBProfServiceBean.setModifyByAgency(aoCBGridBean.getModifyByAgency());
						loTempCBProfServiceBean.setModifyByProvider(aoCBGridBean.getModifyByProvider());

						DAOUtil.masterDAO(aoMybatisSession, loTempCBProfServiceBean,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
								HHSConstants.CBY_ADD_PROF_SERVICES_DETAILS,
								HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN);
					}
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("loCBGridBeanList", CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error(
					"App Exception occured in ContractBudgetServiceService: insertStandardRowsProfServices method:: ",
					loAppEx);
			setMoState("Error occurred in ContractBudgetService: insertStandardRowsProfServices method"
					+ aoCBGridBean.getContractBudgetID() + " and SubBudget Id::" + aoCBGridBean.getSubBudgetID());
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			// Context Data is added into Exception object and Log is updated
			ApplicationException loAppEx = new ApplicationException("Error occurred in ContractBudgetService:"
					+ " insertStandardRowsProfServices method:: ", loEx);
			loAppEx.addContextData("loCBGridBeanList", CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: insertStandardRowsProfServices method",
					loAppEx);
			setMoState("Error occurred in ContractBudgetService: insertStandardRowsProfServices method"
					+ aoCBGridBean.getContractBudgetID() + " and SubBudget Id::" + aoCBGridBean.getSubBudgetID());
			throw loAppEx;
		}
	}

	/**
	 * This method is used to insert the default entries into the Utilities
	 * table for the first time for the fixed line items
	 * <ul>
	 * <li>Checks if the entries for a sub_budget are already exists</li>
	 * <li>fetched the Master Data for Utiltities line items</li>
	 * <li>Inserts default data (with values '0') against every Program Income
	 * line items</li>
	 * <li>Execute query id <b> fetchUtilityItemsCount</b></li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBean
	 * @param aoCBGridBean CBGridBean set.
	 * @param aoMybatisSession Mybatis Session Object.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	private void insertStandardRowsUtilities(CBGridBean aoCBGridBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		try
		{

			// 1. fetch tab data for this subBudget
			Integer loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_FETCH_UTILITY_ITEMS_COUNT,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			// 2. if no rows are fetched then query master table to prepare data
			if (null == loCount || loCount == HHSConstants.INT_ZERO)
			{
				CBGridBean loCBGridBeanObj = new CBGridBean();
				List<CBUtilities> loCBUtilities = (List<CBUtilities>) DAOUtil.masterDAO(aoMybatisSession,
						loCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.CBY_FETCH_UTILITIES_TYPE_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

				if (null != loCBUtilities && !loCBUtilities.isEmpty())
				{
					for (CBGridBean loCBUtility : loCBUtilities)
					{

						loCBUtility.setSubBudgetID(aoCBGridBean.getSubBudgetID());
						loCBUtility.setContractBudgetID(aoCBGridBean.getContractBudgetID());
						loCBUtility.setCreatedByUserId(aoCBGridBean.getCreatedByUserId());
						loCBUtility.setContractID(aoCBGridBean.getContractID());
						loCBUtility.setModifyByAgency(aoCBGridBean.getModifyByAgency());
						loCBUtility.setModifyByProvider(aoCBGridBean.getModifyByProvider());
						DAOUtil.masterDAO(aoMybatisSession, loCBUtility,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
								HHSConstants.CBY_INSERT_UTILITIES_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

					}
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.AO_CB_GRID_BEAN, CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("App Exception occured in ContractBudgetService: insertStandardRowsUtilities method:: ",
					loAppEx);
			setMoState("Utilities Error while inserting standard rows in Utilities for BudgetId::"
					+ aoCBGridBean.getContractBudgetID() + " and SubBudget Id::" + aoCBGridBean.getSubBudgetID());
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			// Context Data is added into Exception object and Log is updated
			ApplicationException loAppEx = new ApplicationException("Error occurred in ContractBudgetService:"
					+ " insertStandardRowsUtilities method:: ", loEx);
			loAppEx.addContextData("loCBGridBeanList", CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: insertStandardRowsUtilities method", loAppEx);
			setMoState("Error occurred in ContractBudgetService: insertStandardRowsUtilities method"
					+ aoCBGridBean.getContractBudgetID() + " and SubBudget Id::" + aoCBGridBean.getSubBudgetID());
			throw loAppEx;
		}
	}

	/**
	 * This method is used to fetch the current budget status
	 * 
	 * <ul>
	 * <li>budget status will fetch on the basis of budgetId</li>
	 * <li>Execute query id <b> fetchCurrentCBStatus</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Mybatis Session Object.
	 * @param asBudgetID Budget Id to fetch CurrentCbStatus.
	 * @return lsbudgetStatus
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public String fetchCurrentCBStatus(SqlSession aoMyBatisSession, String asBudgetID) throws ApplicationException
	{
		String lsBudgetStatus = HHSConstants.EMPTY_STRING;
		try
		{
			lsBudgetStatus = (String) DAOUtil.masterDAO(aoMyBatisSession, asBudgetID,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_CURRENT_CB_STATUS,
					HHSConstants.JAVA_LANG_STRING);
		}

		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("Budget-ID : ", asBudgetID);
			LOG_OBJECT.Error("Exception occured while retrieving in ContractBudgetService ", loAppEx);

			setMoState("Transaction Failed:: ContractBudgetService:fetchCurrentCBStatus method"
					+ " - failed to fetch record " + " \n");
			throw loAppEx;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in fetchCurrentCBStatus ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService:fetchCurrentCBStatus "
					+ "method - failed to fetch record " + " \n");
			throw new ApplicationException("Exception occured while fetch in ContractBudgetService ", loAppEx);
		}

		return lsBudgetStatus;
	}

	/**
	 * This method is used to fetch POSITION table master data for personnel
	 * services screen on Contract Budget
	 * <ul>
	 * <li>Execute query id <b> fetchPersonnelServiceMasterData</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @return lsPersonnelServiceMasterData String in the form of (1:Project
	 *         Manager;2:PMO Analyst;3:Business Analyst)
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */

	@SuppressWarnings("unchecked")
	public String fetchPersonnelServiceMasterData(SqlSession aoMybatisSession) throws ApplicationException
	{

		String lsPersonnelServiceMasterData = null;
		List<String> loPositionList = null;
		try
		{
			// Query will return master data in concatenated form
			loPositionList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, HHSConstants.ONE,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_PERSONNEL_SERVICE_MASTER_DATA,
					HHSConstants.JAVA_LANG_STRING);
			if (loPositionList == null || loPositionList.isEmpty())
			{
				throw new ApplicationException(
						"POSITION table Master data not fetched successfully for Personnel Services ");
			}

			lsPersonnelServiceMasterData = convertPositionMasterList(loPositionList);

		}
		// catch any application exception thrown from the code due to SELECT
		// statement and throw it
		// forward
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while fetching POSITION table master data"
					+ " for personnel services : fetchPersonnelServiceMasterData ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while fetching POSITION table"
					+ " master data for personnel services : fetchPersonnelServiceMasterData ", loExp);
			setMoState("ApplicationException occured while fetching POSITION table"
					+ " master data for personnel services ");
			throw loExp;
		}
		// Catch any Null pointer exception thrown from the code and wrap it
		// into application
		// Exception and throw it forward
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while fetching POSITION table master data for personnel services :"
							+ " fetchPersonnelServiceMasterData ", loExp);
			loAppEx.addContextData(
					"Exception occured while fetching POSITION table master data for personnel services :"
							+ " fetchPersonnelServiceMasterData ", loExp);
			LOG_OBJECT.Error("Exception occured while fetching POSITION table master data for personnel "
					+ "services : fetchPersonnelServiceMasterData ", loExp);
			setMoState("Exception occured while fetching POSITION table master data for personnel "
					+ "services : fetchPersonnelServiceMasterData ");
			throw loAppEx;
		}

		return lsPersonnelServiceMasterData;

	}

	/**
	 * This method is used to fetch Base budget details of salaried employee
	 * <ul>
	 * <li>Execute query id <b> fetchSalariedEmployee</b></li>
	 * </ul>
	 * 
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @return loSalariedEmployessForModification List<PersonnelServiceBudget>
	 * @throws Exception Exception thrown.
	 */

	@SuppressWarnings("unchecked")
	public List<PersonnelServiceBudget> fetchSalariedEmployeeForBase(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget) throws Exception
	{

		List<PersonnelServiceBudget> loSalariedEmployessForBase = (List<PersonnelServiceBudget>) DAOUtil.masterDAO(
				aoMybatisSession, aoPersonnelServiceBudget, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
				HHSConstants.CBY_FETCH_SALRIED_EMPLOYEE, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

		return loSalariedEmployessForBase;

	}

	/**
	 * This method is used to fetch Base budget details of Hourly employee
	 * 
	 * <ul>
	 * <li>Execute query id <b> fetchHourlyEmployee</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @return loHourlyEmployessForModification List<PersonnelServiceBudget>
	 * @throws Exception Exception thrown.
	 */

	@SuppressWarnings("unchecked")
	public List<PersonnelServiceBudget> fetchHourlyEmployeeForBase(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget) throws Exception
	{

		List<PersonnelServiceBudget> loHourlyEmployessForBase = (List<PersonnelServiceBudget>) DAOUtil.masterDAO(
				aoMybatisSession, aoPersonnelServiceBudget, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
				HHSConstants.CBY_FETCH_HOURLY_EMPLOYEE, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

		return loHourlyEmployessForBase;

	}

	/**
	 * This method is used to fetch Base budget details of Seasonal employee
	 * <ul>
	 * <li>Execute query id <b> fetchSeasonalEmployee</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @return loSeasonalEmployessForBase List<PersonnelServiceBudget>
	 * @throws Exception Exception thrown.
	 */

	@SuppressWarnings("unchecked")
	public List<PersonnelServiceBudget> fetchSeasonalEmployeeForBase(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget) throws Exception
	{

		List<PersonnelServiceBudget> loSeasonalEmployessForBase = (List<PersonnelServiceBudget>) DAOUtil.masterDAO(
				aoMybatisSession, aoPersonnelServiceBudget, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
				HHSConstants.CBY_FETCH_SEASONAL_EMPLOYEE, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

		return loSeasonalEmployessForBase;

	}

	/**
	 * This method is used to fetch Base budget details of Fringe Benefits
	 * <ul>
	 * <li>Execute query id <b> fetchFringBenifits</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudget CBGridBean
	 * @return loFringeBenefitsForBase List<PersonnelServiceBudget>
	 * @throws Exception Exception thrown.
	 */

	@SuppressWarnings("unchecked")
	public List<PersonnelServiceBudget> fetchFringeBenefitsForBase(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget) throws Exception
	{

		List<PersonnelServiceBudget> loFringeBenefitsForBase = (List<PersonnelServiceBudget>) DAOUtil.masterDAO(
				aoMybatisSession, aoPersonnelServiceBudget, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
				HHSConstants.CBY_FETCH_FRING_BENEFITS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

		return loFringeBenefitsForBase;

	}

	/**
	 * This method inserts contract details from award task
	 * 
	 * <ul>
	 * <li>Get the contract map from input</li>
	 * <li>Check for status flag</li>
	 * <li>If true, Execute query with id "insertContractDetailsFromAwardTask"
	 * from contract budget mapper</li>
	 * <li>Return insert status to controller</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession sql session object
	 * @param abStatusFlag a boolean value of status flag
	 * @param aoContractMap HashMap parameter set.
	 * @return a boolean value of insert status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean insertContractDetailsFromAwardTask(SqlSession aoMybatisSession,
			HashMap<String, String> aoContractMap, Boolean abStatusFlag) throws ApplicationException
	{
		Boolean lbInsertStatus = false;
		try
		{
			if (abStatusFlag)
			{
				DAOUtil.masterDAO(aoMybatisSession, aoContractMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.INSERT_CONTRACT_DETAILS_FROM_AWARD_TASK, HHSConstants.JAVA_UTIL_HASH_MAP);
				lbInsertStatus = true;
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(ApplicationConstants.CONTRACT_MAP, aoContractMap);
			LOG_OBJECT.Error("Exception occurred while inserting contract details", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: "
					+ "insertContractDetailsFromAwardTask method - failed to fetch" + aoContractMap.toString() + " \n");
			throw loAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occurred while inserting contract details ", loEx);
			setMoState("Transaction Failed:: ContractBudgetService: "
					+ "insertContractDetailsFromAwardTask method - failed to fetch" + aoContractMap.toString() + " \n");
			throw new ApplicationException("Exception occurred while inserting contract details", loEx);
		}
		return lbInsertStatus;
	}

	/**
	 * This method is triggered to get the advance Grid information.
	 * <ul>
	 * <li>A bean is passed, CBGridBean</li>
	 * <li>Execute query id <b> fetchAdvanceDetails</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session Object.
	 * @param aoCBGridBeanObj CBGridBean attribute set.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return loAssignmentsSummaryBean
	 */
	@SuppressWarnings("unchecked")
	public List<AdvanceSummaryBean> fetchAdvanceDetails(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj,
			MasterBean aoMasterBeanObj) throws ApplicationException
	{
		List<AdvanceSummaryBean> loAdvancesList = null;
		try
		{

			if (aoCBGridBeanObj.getBudgetTypeId().equalsIgnoreCase(HHSConstants.TWO))
			{
				// For Contract Budget (Base) fetch Advance Details for Budget
				loAdvancesList = (List<AdvanceSummaryBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_FETCH_ADVANCE_SUMMARY,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			}
			else
			{
				String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_BUDGET_APPROVED);
				// Only For Amendment fetch from XML
				if (aoMasterBeanObj != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
				{
					loAdvancesList = aoMasterBeanObj.getAdvanceSummaryBean();
				}
				else
				{
					// For Contract Budget Update/Modification/Amendment - fetch
					// Advance Details for Parent Budget
					loAdvancesList = (List<AdvanceSummaryBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.CBY_FETCH_ADVANCE_SUMMARY_FOR_PARENT_BUDGET,
							HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				}
			}

		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.BUDGET_ID, aoCBGridBeanObj.getContractBudgetID());
			LOG_OBJECT.Error(
					"Exception occured  at fetching advance Grid ContractBudgetService: fetchAdvanceSummary() ",
					loAppEx);
			setMoState("ContractBudgetService: fetchAdvanceSummary() failed to fetch AdvanceGrid at Budget-ID:"
					+ aoCBGridBeanObj.getContractBudgetID() + " \n");
			throw loAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieving AdvanceGrid in ContractBudgetService ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchAdvanceSummary "
					+ "method - failed to retrieve for Budget-ID" + aoCBGridBeanObj.getContractBudgetID() + " \n");
			throw new ApplicationException("Exception occured while fetch AssignmentGrid in ContractBudgetService ",
					loAppEx);
		}
		return loAdvancesList;
	}

	/**
	 * This method is responsible for converting List of Master data to a String
	 * delimited by ';'
	 * 
	 * @param aoPositionBean List<String>
	 * @return lsFinalString String
	 */
	private String convertPositionMasterList(List<String> aoPositionBean)
	{
		Iterator<String> loListIterator = aoPositionBean.iterator();
		StringBuilder loPositionString = new StringBuilder();
		while (loListIterator.hasNext())
		{
			loPositionString.append(loListIterator.next()).append(HHSConstants.DELIMITER_SEMICOLON);
		}
		loPositionString.deleteCharAt(loPositionString.length() - HHSConstants.INT_ONE);
		loPositionString.insert(HHSConstants.INT_ZERO, HHSConstants.DROPDOWN_BLANK_FORMAT);
		String lsFinalString = loPositionString.toString();
		return lsFinalString;
	}

	/**
	 * This method is used to create replica of budget starting from Contract
	 * till line item level when Contract budget review task is final Approved.
	 * <ul>
	 * <li>1. Check if Original Contract record already exist for base contract.
	 * if count is zero create replica of contract with type as Original</li>
	 * <li>2. Check if Original Contract record already exist for base contract.
	 * if count is zero create replica of contract with type as Original</li>
	 * fetchOriginalContractCount</b></li>
	 * <li>Execute query id <b>
	 * <li>Execute query id <b> </b></li>
	 * </ul>
	 * Updated for defect QC 9156 R 7.5.0
	 * Added queries to create replica for Services, cost_center and Services_config
	 * 
	 * @param aoMyBatisSession sql session
	 * @param aoTaskDetailsBean task details bean
	 * @param asBudgetStatus budget status
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void createReplicaOfBudget(SqlSession aoMyBatisSession, TaskDetailsBean aoTaskDetailsBean,
			String asBudgetStatus) throws ApplicationException
	{
		Integer loReplicaBudgetId = null;
		Integer loReplicaSubBudgetId = null;
		try
		{
			LOG_OBJECT.Debug("**********STARTED createReplicaOfBUdget: " + GregorianCalendar.getInstance().getTime()
					+ "ms " + GregorianCalendar.getInstance().getTimeInMillis());

			Map loHashMap = new HashMap();
			loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoTaskDetailsBean.getBudgetId());
			loHashMap.put(HHSConstants.STATUS_ID, asBudgetStatus);
			loHashMap.put(HHSConstants.MODIFY_BY, aoTaskDetailsBean.getUserId());

			Integer loReplicaContractId = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_ORIGINAL_FETCH_CONTRACT_COUNT,
					HHSConstants.JAVA_UTIL_MAP);

			// Create replica of Budget
			loHashMap.put(HHSConstants.REP_CONTRACT_ID, loReplicaContractId);
			loReplicaBudgetId = createBudgetReplica(aoMyBatisSession, loHashMap);

			// Fetching subbudget list of Budget
			List<String> loSubBudgetIdList = fetchSubBudgetList(aoMyBatisSession, loHashMap);

			// For each subbudget, creating replica of line item tables
			loHashMap.put(HHSConstants.REP_BUDGET_ID, loReplicaBudgetId);
			//Start: QC 9156 R 7.5.0 Accenture fix: Cost Center Records not created for "Original" Budget (BUDGET_TYPE_ID = 5)
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.CBY_CREATE_SERVICES_CONFIG_REPLICA,
					HHSConstants.JAVA_UTIL_MAP);
			//End: QC 9156 R 7.5.0 Accenture fix: Cost Center Records not created for "Original" Budget (BUDGET_TYPE_ID = 5)
			
			Iterator<String> aoListIterator = loSubBudgetIdList.iterator();
			while (aoListIterator.hasNext())
			{
				loHashMap.put(HHSConstants.ORIG_SUB_BUDGET_ID, aoListIterator.next());
				
				//Start Release 6 for defect id 8428
				insertSubBudgetSiteForAmendment(aoMyBatisSession, loHashMap);
				//End Release 6 for defect id 8428
				loReplicaSubBudgetId = createSubBudgetReplica(aoMyBatisSession, loHashMap);
				loHashMap.put(HHSConstants.REP_SUB_BUDGET_ID, loReplicaSubBudgetId);
				//following method updated for defect QC 9156
				createLineItemReplica(aoMyBatisSession, loHashMap);

			}

			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_SET_CONTRACT_BUDGET_STATUS_FOR_REVIEW_TASK, HHSConstants.JAVA_UTIL_MAP);

			// START || Added as a part of release 3.12.0 for enhancement
			// request 6643
			// updateLineItemModifiedDate(aoMyBatisSession, loHashMap);
			// START || Added as a part of release 3.12.0 for enhancement
			// request 6643
			setMoState("Transaction Success:: ContractBudgetService:setContractBudgetStatusForReviewTask method - success to update record "
					+ " \n");
			LOG_OBJECT.Debug("**********ENDS createReplicaOfBUdget: " + GregorianCalendar.getInstance().getTime()
					+ " ms " + GregorianCalendar.getInstance().getTimeInMillis());

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
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
		// handling exception other than ApplicationException
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
	}

	/**
	 * This method is responsible for creating replica of Contract record when
	 * contract budget review task is finished.
	 * <ul>
	 * <li>Execute query id <b> createContractReplica</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession sql session object
	 * @param aoHashMap all reqired parameters map
	 * @throws ApplicationException if any exception occurs
	 * @return loReplicaContractId ContractId of newly inserted record
	 */

	private Integer createContractReplica(SqlSession aoMyBatisSession, Map<String, Object> aoHashMap)
			throws ApplicationException
	{
		Integer loReplicaContractId = null;
		try
		{
			setMoState("AACCBB: 3. createContractReplica" + aoHashMap);
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_CONTRACT_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			loReplicaContractId = (Integer) aoHashMap.get(HHSConstants.AI_CURRENT_SEQ);
			setMoState("AACCBB: 4. createContractReplica loReplicaContractId" + loReplicaContractId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("ContractId passed: ", aoHashMap.get(HHSConstants.CONTRACT_ID_WORKFLOW));
			LOG_OBJECT.Error("ApplicationException occured while executing query createContractReplica ", aoAppEx);
			throw aoAppEx;
		}
		return loReplicaContractId;
	}

	/**
	 * This method is responsible for creating replica of Budget record when
	 * contract budget review task is finished.
	 * <ul>
	 * <li>Execute query id <b> createBudgetReplica</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession sql session
	 * @param aoHashMap all required parameter map
	 * @throws ApplicationException if any exception occurs
	 * @return loReplicaBudgetId BudgetId of newly inserted record
	 */

	@SuppressWarnings("rawtypes")
	private Integer createBudgetReplica(SqlSession aoMyBatisSession, Map aoHashMap) throws ApplicationException
	{
		Integer loReplicaBudgetId = null;
		try
		{

			// Creating Budget record replica
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_BUDGET_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			loReplicaBudgetId = (Integer) aoHashMap.get(HHSConstants.AI_CURRENT_SEQ);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("BudgetId passed: ", aoHashMap.get(HHSConstants.BUDGET_ID_WORKFLOW));
			LOG_OBJECT.Error("ApplicationException occured while executing query createBudgetReplica ", aoAppEx);
			throw aoAppEx;
		}
		return loReplicaBudgetId;
	}

	/**
	 * This method is responsible for fetching list of subbudget id's of a
	 * Budget.
	 * <ul>
	 * <li>Execute query id <b> fetchSubBudgetList</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession sql session
	 * @param aoHashMap all required parameters bean
	 * @throws ApplicationException if any exception occurs
	 * @return List
	 */

	@SuppressWarnings("unchecked")
	public List<String> fetchSubBudgetList(SqlSession aoMyBatisSession, Map<String, String> aoHashMap)
			throws ApplicationException
	{
		List<String> loSubBudgetIdList = null;
		try
		{

			// Fetching sub-budget id list of the budget
			loSubBudgetIdList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_FETCH_SUB_BUDGET_LIST,
					HHSConstants.JAVA_UTIL_MAP);
			if (loSubBudgetIdList == null || loSubBudgetIdList.isEmpty())
			{
				throw new ApplicationException("SubBudget list not fetched successfully for BudgetId : " + aoHashMap);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("BudgetId passed: ", aoHashMap.get(HHSConstants.BUDGET_ID_WORKFLOW));
			LOG_OBJECT.Error("ApplicationException occured while executing query fetchSubBudgetList ", aoAppEx);
			throw aoAppEx;
		}
		return loSubBudgetIdList;
	}

	/**
	 * This method is responsible for creating replica of Subbudget records of a
	 * budget when contract budget review task is finished.
	 * <ul>
	 * <li>Execute query id <b> createSubBudgetReplica</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession sql session
	 * @param aoHashMap all required parameters map
	 * @throws ApplicationException if any exception occurs
	 * @return Integer
	 */

	@SuppressWarnings("rawtypes")
	private Integer createSubBudgetReplica(SqlSession aoMyBatisSession, Map aoHashMap) throws ApplicationException
	{
		Integer loReplicaSubBudgetId = null;
		try
		{

			// Creating Budget record replica
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_SUB_BUDGET_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			loReplicaSubBudgetId = (Integer) aoHashMap.get(HHSConstants.AI_CURRENT_SEQ);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("SubBudgetId passed: ", aoHashMap.get(HHSConstants.SUB_BUDGET_ID));
			LOG_OBJECT.Error("ApplicationException occured while executing query createSubBudgetReplica ", aoAppEx);
			throw aoAppEx;
		}
		return loReplicaSubBudgetId;
	}

	/**
	 * This method is responsible for insterting sub budget site information for
	 * any amendment budget when contract budget review task is finished.
	 * <ul>
	 * <li>Execute query id
	 * <b>softDeleteParent,insertSubBudgetSiteDetailsForUpdate </b></li>
	 * </ul>
	 * Release 6 defect id 8428
	 * @param aoMyBatisSession sql session
	 * @param aoHashMap all required parameters map
	 * @throws ApplicationException if any exception occurs
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void insertSubBudgetSiteForAmendment(SqlSession aoMyBatisSession, Map aoHashMap)
			throws ApplicationException
	{
		try
		{
			//Start Release 6 defect id 8527
			ArrayList<String> loSubBudgetDetails = (ArrayList<String>) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.FETCH_SUB_BUDGET_DETAILS_FOR_AMENDMENT_CONF_TASK, HHSConstants.JAVA_UTIL_HASH_MAP);
			SiteDetailsBean loSiteDetailsBean = new SiteDetailsBean();
			loSiteDetailsBean.setModifiedBy(HHSConstants.SYSTEM_USER);
			for (String lsUpdateSubBudgetId : loSubBudgetDetails)
			{
				loSiteDetailsBean.setParentSubBudgetId(lsUpdateSubBudgetId);
				DAOUtil.masterDAO(aoMyBatisSession,loSiteDetailsBean, HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.SOFT_DELETE_PARENT, HHSConstants.COM_NYC_HHS_MODEL_SITEDETAILSBEAN);
				DAOUtil.masterDAO(aoMyBatisSession, Integer.parseInt(lsUpdateSubBudgetId),
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER,
						HHSConstants.INSERT_SUB_BUDGET_DETAILS_SITE_DETAILS_FOR_UPDATE, HHSConstants.INTEGER_CLASS_PATH);
			}		
		}
		//End Release 6 defect id 8527
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("SubBudgetId passed: ", aoHashMap.get(HHSConstants.SUB_BUDGET_ID));
			LOG_OBJECT.Error("ApplicationException occured while executing query insertSubBudgetSiteForAmendment ",
					aoAppEx);
			throw aoAppEx;
		}
	}
	/**
	 * This method is responsible for creating replica of Line Item records of a
	 * budget when contract budget review task is finished.
	 * 
	 * <li>Execute query id <b> createPersonnelServiceReplica</b></li> <li>
	 * Execute query id <b>createOperationAndSupportReplica </b></li> <li>
	 * Execute query id <b>createOperationAndSupportReplicaNewFY </b></li> <li>
	 * Execute query id <b> createUtilitiesReplica</b></li> <li>Execute query id
	 * <b>createRentReplica </b></li> <li>Execute query id
	 * <b>createMilestoneReplica</b></li> <li>Execute query id
	 * <b>createFringeReplica</b></li>
	 * 
	 * @param aoMyBatisSession sql session
	 * @param aoHashMap all required parameters map
	 * @throws ApplicationException if any exception occurs
	 */

	@SuppressWarnings("rawtypes")
	private void createLineItemReplica(SqlSession aoMyBatisSession, Map aoHashMap) throws ApplicationException
	{
		try
		{

			// Creating Budget record replica
			// Added in Release 6: For type original
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSR5Constants.CBY_CREATE_PS_DETAIL_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSR5Constants.CBY_CREATE_FRINGE_DETAIL_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			// Release 6 change ends
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_PS_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_OPS_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_UTILITIES_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_PFS_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_RENT_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_CS_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_RATE_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_MILESTONE_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_UNALLOCATED_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_INDIRECT_RATE_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_PROGRAM_INCOME_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_FRINGE_REPLICA, HHSConstants.JAVA_UTIL_MAP);

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_EQUIPMENT_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			
			//Start: QC 9156 R 7.5.0 Accenture fix: Cost Center Records not created for "Original" Budget (BUDGET_TYPE_ID = 5)
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSR5Constants.CBY_CREATE_SERVICES_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSR5Constants.CBY_CREATE_COSTCENTER_REPLICA, HHSConstants.JAVA_UTIL_MAP);
			//End: QC 9156 R 7.5.0 Accenture fix: Cost Center Records not created for "Original" Budget (BUDGET_TYPE_ID = 5)

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("SubBudgetId passed: ", aoHashMap.get(HHSConstants.SUB_BUDGET_ID));
			LOG_OBJECT.Error("ApplicationException occured while executing query createSubBudgetReplica ", aoAppEx);
			throw aoAppEx;
		}

	}

	/**
	 * Added as a part of release 3.12.0 for enhancement request 6643
	 * 
	 * <li>Execute query id <b> createPersonnelServiceReplica</b></li> <li>
	 * Execute query id <b>createOperationAndSupportReplica </b></li> <li>
	 * Execute query id <b>createOperationAndSupportReplicaNewFY </b></li> <li>
	 * Execute query id <b> createUtilitiesReplica</b></li> <li>Execute query id
	 * <b>createRentReplica </b></li> <li>Execute query id
	 * <b>createMilestoneReplica</b></li> <li>Execute query id
	 * <b>createFringeReplica</b></li>
	 * 
	 * @param aoMyBatisSession sql session
	 * @param aoHashMap all required parameters map
	 * @throws ApplicationException if any exception occurs
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void updateLineItemModifiedDate(SqlSession aoMyBatisSession, String aoBudgetId, Boolean loFinalFinish,
			String asUserId) throws ApplicationException
	{
		HashMap loHashMap = new HashMap<String, String>();
		try
		{
			loHashMap.put(HHSConstants.MODIFY_BY, asUserId);
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoBudgetId);
			// START || Added as a part of release 3.12.0 for enhancement
			// request 6643
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_PS_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_OPS_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_UTILITIES_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_PFS_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_RENT_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_CS_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_RATE_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_MILESTONE_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_UNALLOCATED_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_INDIRECT_RATE_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_PROGRAM_INCOME_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_FRINGE_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_EQUIPMENT_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_SUB_BUDGET_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_BUDGET_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_SUB_BUDGET_YTD_INVOICE_AMOUNT, HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.UPDATE_BUDGET_YTD_INVOICE_AMOUNT, HHSConstants.JAVA_UTIL_MAP);

			// END || Added as a part of release 3.12.0 for enhancement request
			// 6643
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("BudgetId passed: ", loHashMap.get(HHSConstants.BUDGET_ID_WORKFLOW));
			LOG_OBJECT.Error("ApplicationException occured while executing query updateLineItemModifiedDate ", aoAppEx);
			throw aoAppEx;
		}

	}

	/**
	 * This method performs validation on total Amount on clicking Submit in
	 * Contract Budget.
	 * <ul>
	 * <li>Execute query id <b> fetchAmountTotal</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SqlSession object
	 * @param aoBudgetId : BudgetId of BudgetType
	 * @return Boolean: true for success and false for validation failure
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Boolean validateAmountTotal(SqlSession aoMyBatisSession, String aoBudgetId) throws ApplicationException
	{
		Boolean loValid = true;
		HashMap aoHashmap = new HashMap<String, String>();
		List<CBGridBean> loSubBudgetList = null;
		BigDecimal loBugetAmount = BigDecimal.ZERO;
		BigDecimal loUpdatedAmount = BigDecimal.ZERO;
		try
		{
			aoHashmap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoBudgetId);

			loSubBudgetList = (List<CBGridBean>) DAOUtil.masterDAO(aoMyBatisSession, aoHashmap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_SUB_BUDGET_SUMMARY,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			for (int liNumber = 0; liNumber < loSubBudgetList.size(); liNumber++)
			{
				aoHashmap.put(HHSConstants.SUBBUDGET_ID, loSubBudgetList.get(liNumber).getSubBudgetID());
				loUpdatedAmount = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, aoHashmap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBM_MAPPER_FETCH_AMOUNT_TOTAL,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				loBugetAmount = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, loSubBudgetList.get(liNumber)
						.getSubBudgetID(), HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.CBM_MAPPER_FETCH_SUB_BUDGET_AMOUNT, HHSConstants.JAVA_LANG_STRING);
				if (!loUpdatedAmount.equals(loBugetAmount))
				{
					loValid = false;
					break;
				}
			}
		}
		// Application Exception handled here
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ContractBudgetService:" + " validateUpdateAmountTotal method:: ",
					aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: validateUpdateAmountTotal "
					+ "method - failed Exception occured while validating Modification Total Amount\n");
			throw aoAppEx;
		}

		// Exception handled here - May occur for any unpredictable situation
		catch (Exception aoEx)
		{
			setMoState("Transaction Failed:: ContractBudgetService: validateUpdateAmountTotal"
					+ " method - failed to validate record " + " \n");
			throw new ApplicationException("Exception occured while validating Update Total Amount  :"
					+ " ContractBudgetService method", aoEx);
		}
		return loValid;
	}

	/**
	 * This method fetches non-grid data for Personnel Services from XML
	 * 
	 * @param asSubBudgetId String
	 * @param aoMasterBean MasterBean
	 * @return loReturnedData PersonnelServicesData
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private PersonnelServicesData fetchPersonnelServiceDataFromXML(String asSubBudgetId, MasterBean aoMasterBean)
			throws ApplicationException
	{
		List<LineItemMasterBean> loMasterBeanList = null;
		PersonnelServicesData loReturnedData = null;
		loMasterBeanList = aoMasterBean.getMasterBeanList();
		Iterator<LineItemMasterBean> aoListIterator = loMasterBeanList.iterator();
		while (aoListIterator.hasNext())
		{
			LineItemMasterBean loLineItemBean = aoListIterator.next();
			if (loLineItemBean.getSubbudgetId().equals(asSubBudgetId))
			{
				loReturnedData = loLineItemBean.getNonGridPSData();
			}
		}
		return loReturnedData;
	}

	/**
	 * The Method will fetch Contract status for given Contract Id
	 * <ul>
	 * <li>Execute query id <b> fetchContractStatus</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession : Mybatis Session
	 * @param asContractId :String object
	 * 
	 * @return lsContractStatus
	 * @throws ApplicationException - ApplicationException object
	 * 
	 */
	public String fetchContractStatusMethod(SqlSession aoMyBatisSession, String asContractId)
			throws ApplicationException
	{
		String lsContractStatus = HHSConstants.EMPTY_STRING;

		try
		{
			lsContractStatus = (String) DAOUtil.masterDAO(aoMyBatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.IS_FETCH_CONTRACT_STATUS,
					HHSConstants.JAVA_LANG_STRING);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppExp)
		{
			setMoState("Error while fetching contract status for contract id : " + asContractId);
			LOG_OBJECT.Error("Error while fetching contract status with Exception : ", aoAppExp);
			throw aoAppExp;
		}

		return lsContractStatus;

	}

	// start

	/**
	 * <p>
	 * This method is used to create replica of budget starting from Contract
	 * till line item level when Contract budget review task is final Approved.
	 * <ul>
	 * <li>1. Check if Original Contract record already exist for base contract.
	 * if count is zero create replica of contract with type as Original</li>
	 * <li>2. Check if Original Contract record already exist for base contract.
	 * if count is zero create replica of contract with type as Original</li>
	 * <li>Execute query id <b> fetchEntryTypeDetails</b></li>
	 * </ul>
	 * Updated Method in R4
	 * </p>
	 * 
	 * @param aoMyBatisSession sql session
	 * @param aoTaskDetailsBean task details bean
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void createReplicaOfBudgetNewFY(SqlSession aoMyBatisSession, TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		try
		{
			List<ContractBudgetBean> loContractBudgetBeanList = fetchSubBudgetListNewFY(aoMyBatisSession,
					aoTaskDetailsBean);
			List<String> loEntryType = null;
			// R4 Fetch only Selected EntryType Id's
			if (!loContractBudgetBeanList.isEmpty())
			{
				HashMap aoHashMap = new HashMap<String, String>();
				aoHashMap.put(HHSConstants.CONTRACT_ID, aoTaskDetailsBean.getContractId());
				aoHashMap
						.put(HHSConstants.BUDGET_ID, loContractBudgetBeanList.get(HHSConstants.INT_ZERO).getBudgetId());
				loEntryType = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_CONFIGURATION_MAPPER, HHSConstants.FETCH_ENTRY_TYPE_DETAILS,
						HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			for (ContractBudgetBean loContractBudgetBean : loContractBudgetBeanList)
			{
				createLineItemReplicaNewFY(aoMyBatisSession, loContractBudgetBean, loEntryType);
				updateSubBudgetParentIDNewFY(aoMyBatisSession, loContractBudgetBean);
			}
			// START || Added as a part of release 3.12.0 for enhancement
			// request 6643
			Map loHashMap = new HashMap();
			loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoTaskDetailsBean.getBudgetId());
			loHashMap.put(HHSConstants.MODIFY_BY, aoTaskDetailsBean.getUserId());
			// updateLineItemModifiedDate(aoMyBatisSession, loHashMap);
			// START || Added as a part of release 3.12.0 for enhancement
			// request 6643
			setMoState("Transaction Success:: ContractBudgetService:setContractBudgetStatusForReviewTask method - success to update record "
					+ " \n");
			LOG_OBJECT.Debug("**********ENDS createReplicaOfBUdget: " + GregorianCalendar.getInstance().getTime()
					+ " ms " + GregorianCalendar.getInstance().getTimeInMillis());
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
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
		// handling exception other than ApplicationException
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
	}

	/**
	 * This method is responsible for fetching list of subbudget id's which are
	 * present in old year during new fiscal year configuration.
	 * <ul>
	 * <li>
	 * Execute query id <b> fetchSubBudgetListNewFY</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession sql session
	 * @param aoHashMap all required parameters bean
	 * @throws ApplicationException if any exception occurs
	 * @return List
	 */

	@SuppressWarnings("unchecked")
	private List<ContractBudgetBean> fetchSubBudgetListNewFY(SqlSession aoMyBatisSession,
			TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		List<ContractBudgetBean> loContractBudgetBeanList = null;
		try
		{
			// retrieve list of subbudgets for new fiscal year config (excluding
			// new sub budget added on config screen)
			loContractBudgetBeanList = (List<ContractBudgetBean>) DAOUtil.masterDAO(aoMyBatisSession,
					aoTaskDetailsBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_FETCH_SUB_BUDGET_LIST_NEW_FY, HHSConstants.CS_TASK_DETAILS_BEAN);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured while executing query fetchSubBudgetList ", aoAppEx);
			throw aoAppEx;
		}
		return loContractBudgetBeanList;
	}

	/**
	 * This method updates parent id of subbudgets with same value as of primary
	 * key (making parent and primary key value same, this is done so that
	 * further task has no impact like update, amendment)
	 * <ul>
	 * <li>Execute query id <b> updateParentOfDerivedSubBudgets</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession sql session
	 * @param aoHashMap all required parameters bean
	 * @throws ApplicationException if any exception occurs
	 * @return List
	 */
	private Integer updateSubBudgetParentIDNewFY(SqlSession aoMyBatisSession, ContractBudgetBean aoContractBudgetBean)
			throws ApplicationException
	{
		Integer liCount = null;
		try
		{
			// update PARENT_ID to be same as SUB_BUDGET_ID for new fy
			// subbudgets that are derived from previous
			// fiscal year
			liCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoContractBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_UPDATE_PARENT_OF_DERIVED_SUB_BUDGETS,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured while executing query updateSubBudgetParentIDNewFY ",
					aoAppEx);
			throw aoAppEx;
		}
		return liCount;
	}

	// Start : Updated in R6
	/**
	 * <p>
	 * This method is responsible for creating replica of Line Item records of a
	 * budget when new fiscal year configuration task is finished.
	 * <ul>
	 * <li>Execute query id <b> createPersonnelServiceReplicaNewFY</b></li>
	 * <li>Execute query id <b>createFringeReplicaNewFY </b></li>
	 * <li>Execute query id <b>createOperationAndSupportReplicaNewFY </b></li>
	 * <li>Execute query id <b> createEquipmentReplicaNewFY</b></li>
	 * <li>Execute query id <b>createUtilitiesReplicaNewFY </b></li>
	 * <li>Execute query id <b>createIndirectRateReplicaNewFY</b></li>
	 * <li>Execute query id <b>createProgramIncomeReplicaNewFY</b></li>
	 * </ul>
	 * Updated Method in R4 Updated in R6
	 * Updated in R7 for Program Income
	 * </p>
	 * 
	 * @param aoMyBatisSession sql session
	 * @param aoContractBudgetBean ContractBudget Bean
	 * @param loEntryType List<String> EntryTypeId's
	 * @throws ApplicationException if any exception occurs
	 */

	@SuppressWarnings("unchecked")
	private void createLineItemReplicaNewFY(SqlSession aoMyBatisSession, ContractBudgetBean aoContractBudgetBean,
			List<String> loEntryType) throws ApplicationException
	{
		try
		{
			// Default Entries
			// Fringe Benefit
			DAOUtil.masterDAO(aoMyBatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_FRINGE_REPLICA_NEW_FY, HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			/*
			 * Start : R6- Added to copy fringe benefit details from previous
			 * financial year.
			 * 
			 * Updated for Defect-8459
			 */
			Integer loCountFringeDetail = (Integer) DAOUtil.masterDAO(aoMyBatisSession,
					aoContractBudgetBean.getSubBudgetId(), HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSR5Constants.GET_FRINGE_BENEFIT_DETAIL_COUNT, HHSConstants.JAVA_LANG_STRING);
			if (null == loCountFringeDetail || loCountFringeDetail == HHSConstants.INT_ZERO)
			{
				List<String> loFringeBenefitsMasterIdList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, null,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSR5Constants.FETCH_FRINGE_BENEFITS_MASTER_LIST, null);
				if (null != loFringeBenefitsMasterIdList && loFringeBenefitsMasterIdList.size() > HHSConstants.INT_ZERO)
				{
					for (String loFringeBenefitsMasterIdTemp : loFringeBenefitsMasterIdList)
					{
						PersonnelServiceBudget loPersonnelServiceBudgetBean = new PersonnelServiceBudget();
						loPersonnelServiceBudgetBean.setTypeId(loFringeBenefitsMasterIdTemp);
						loPersonnelServiceBudgetBean.setContractBudgetID(aoContractBudgetBean.getBudgetId());
						loPersonnelServiceBudgetBean.setSubBudgetID(aoContractBudgetBean.getSubBudgetId());
						loPersonnelServiceBudgetBean.setModifyByAgency(aoContractBudgetBean.getModifiedByUserId());
						loPersonnelServiceBudgetBean.setModifyByProvider(HHSConstants.EMPTY_STRING);
						loPersonnelServiceBudgetBean.setBudgetAmount(HHSConstants.ZERO);
						DAOUtil.masterDAO(aoMyBatisSession, loPersonnelServiceBudgetBean,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
								HHSR5Constants.INSERT_FRINGE_BENEFIT_DETAIL, HHSConstants.PERSONNEL_SERVICE_BUDGET);
					}
				}
			}
			else
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoContractBudgetBean,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSR5Constants.CBY_CREATE_FRINGE_DETAIL_REPLICA_NEW_FY,
						HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			}
			/*
			 * End : R6- Added to copy fringe benefit details from previous
			 * financial year
			 */

			// Operation & Support
			DAOUtil.masterDAO(aoMyBatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_OPS_REPLICA_NEW_FY, HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			// Utilities
			DAOUtil.masterDAO(aoMyBatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_UTILITIES_REPLICA_NEW_FY,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);

			// ProfessionalService
			DAOUtil.masterDAO(aoMyBatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_PFS_REPLICA_NEW_FY, HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);

			// UnallocatedFunds
			DAOUtil.masterDAO(aoMyBatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_UNALLOCATED_REPLICA_NEW_FY,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);

			// IndirectRate
			DAOUtil.masterDAO(aoMyBatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_INDIRECT_RATE_REPLICA_NEW_FY,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
			
			// Removing default entries copy of program income as new UI will be shown post 
            // R7 and we do not need default entries for ProgramIncome. So Commenting below query
			/*DAOUtil.masterDAO(aoMyBatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSConstants.CBY_CREATE_PROGRAM_INCOME_REPLICA_NEW_FY,
					HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);*/
            //R7 changes end
             
			// R4 - Budget Customization Copy only selected EntryType Id's
			// Creating Budget record replica
			for (String lsCount : loEntryType)
			{
				if (lsCount.split(HHSConstants.COLON)[HHSConstants.INT_ZERO].equals(HHSConstants.ONE))
				{
					// Commented for R6 PS Enhancement
					/*
					 * DAOUtil.masterDAO(aoMyBatisSession, aoContractBudgetBean,
					 * HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					 * HHSConstants.CBY_CREATE_PS_REPLICA_NEW_FY,
					 * HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
					 */
					// Start: Added in R6
					DAOUtil.masterDAO(aoMyBatisSession, aoContractBudgetBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSR5Constants.CBY_CREATE_PS_DETAIL_REPLICA_NEW_FY,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
					DAOUtil.masterDAO(aoMyBatisSession, aoContractBudgetBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSR5Constants.CBY_CREATE_PS_SUMMARY_REPLICA_NEW_FY,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
					DAOUtil.masterDAO(aoMyBatisSession, aoContractBudgetBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSR5Constants.CBY_UPDATE_PS_SUMMARY_ID_NEW_FY,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
					// End: Added in R6

				}
				else if (lsCount.split(HHSConstants.COLON)[HHSConstants.INT_ZERO].equals(HHSConstants.TWO))
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoContractBudgetBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.CBY_CREATE_EQUIPMENT_REPLICA_NEW_FY,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
				}
				else if (lsCount.split(HHSConstants.COLON)[HHSConstants.INT_ZERO].equals(HHSConstants.FIVE))
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoContractBudgetBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.CBY_CREATE_RENT_REPLICA_NEW_FY,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
				}
				else if (lsCount.split(HHSConstants.COLON)[HHSConstants.INT_ZERO].equals(HHSConstants.SIX))
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoContractBudgetBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.CBY_CREATE_CS_REPLICA_NEW_FY,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
				}
				else if (lsCount.split(HHSConstants.COLON)[HHSConstants.INT_ZERO].equals(HHSConstants.SEVEN))
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoContractBudgetBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.CBY_CREATE_RATE_REPLICA_NEW_FY,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
				}
				else if (lsCount.split(HHSConstants.COLON)[HHSConstants.INT_ZERO].equals(HHSConstants.EIGHT))
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoContractBudgetBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.CBY_CREATE_MILESTONE_REPLICA_NEW_FY,
							HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
				}
                //Start: Added in R7 for PI 
                else if (lsCount.split(HHSConstants.COLON)[HHSConstants.INT_ZERO].equals(HHSConstants.STRING_ELEVEN))
                {
                      DAOUtil.masterDAO(aoMyBatisSession, aoContractBudgetBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
                                  HHSConstants.CBY_CREATE_PROGRAM_INCOME_REPLICA_NEW_FY,
                                  HHSConstants.INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN);
                }
				//End: R7 changes for program income
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured while executing query createLineItemReplicaNewFY ", aoAppEx);
			throw aoAppEx;
		}
	}

	// End : Updated in R6

	// Start : Added in R6
	/**
	 * This method is used to fetch grid data for Salaried Position grid on
	 * Personnel Service detailed page
	 * <ul>
	 * <li>Execute query id <b> fetchPsDetailedEmployee</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoCBGridBean CBGridBean
	 * @return loSalariedPositions List<PersonnelServicesData>
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<PersonnelServiceBudget> fetchSalariedDetailGridData(SqlSession aoMybatisSession, CBGridBean aoCBGridBean)
			throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoCBGridBean);
		LOG_OBJECT.Debug("Entered into fetchSalariedDetailGridData for CBGridBean Bean: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		List<PersonnelServiceBudget> loSalariedPositions = null;
		try
		{
			loSalariedPositions = (List<PersonnelServiceBudget>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.FETCH_PS_DETAILED_EMPLOYEE,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while fetching Salaried Employee budget "
					+ ":  fetchSalariedEmployeeBudget", loExp);
			LOG_OBJECT.Error("ApplicationException occured while fetching Salaried Employee budget "
					+ ": fetchSalariedEmployeeBudget ", loExp);
			setMoState("ApplicationException occured while fetching Salaried Employee budget for budget id = "
					+ aoCBGridBean.getContractBudgetID() + " and subbudgetid = " + aoCBGridBean.getSubBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while adding Salaried Emplyee budget :  fetchSalariedEmployeeBudget ", loExp);
			loAppEx.addContextData(
					"Exception occured while fetching Salaried Emplyee budget :  fetchSalariedEmployeeBudget", loExp);
			LOG_OBJECT.Error(
					"Exception occured while fetching Salaried Employee budget : fetchSalariedEmployeeBudget ", loExp);
			setMoState("Exception occured while adding fetching Employee budget for budget id = "
					+ aoCBGridBean.getContractBudgetID() + " and subbudgetid = " + aoCBGridBean.getSubBudgetID());
			throw loAppEx;
		}
		return loSalariedPositions;
	}

	// End : Added in R6

	// Start : Added in R6
	/**
	 * This method is used to fetch grid data for Hourly grid on Personnel
	 * Service detailed page
	 * <ul>
	 * <li>Execute query id <b> fetchHourlyDetailedEmployee</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoCBGridBean CBGridBean
	 * @return loHourlyPositions List<PersonnelServicesData>
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<PersonnelServiceBudget> fetchHourlyDetailGridData(SqlSession aoMybatisSession, CBGridBean aoCBGridBean)
			throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoCBGridBean);
		LOG_OBJECT.Debug("Entered into fetchHourlyDetailGridData for CBGridBean Bean: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		List<PersonnelServiceBudget> loHourlyPositions = null;
		try
		{
			loHourlyPositions = (List<PersonnelServiceBudget>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.FETCH_HOURLY_DETAILED_EMPLOYEE,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while fetching Hourly Employee budget "
					+ ":  fetchHourlyDetailedEmployee", loExp);
			LOG_OBJECT.Error("ApplicationException occured while fetching Hourly Employee budget "
					+ ": fetchHourlyDetailedEmployee ", loExp);
			setMoState("ApplicationException occured while fetching Hourly Employee budget for budget id = "
					+ aoCBGridBean.getContractBudgetID() + " and subbudgetid = " + aoCBGridBean.getSubBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while fetching Hourly Emplyee budget :  fetchHourlyDetailedEmployee ", loExp);
			loAppEx.addContextData(
					"Exception occured while fetching Hourly Emplyee budget :  fetchHourlyDetailedEmployee", loExp);
			LOG_OBJECT.Error("Exception occured while fetching Hourly Employee budget : fetchHourlyDetailedEmployee ",
					loExp);
			setMoState("Exception occured while fetching fetching Employee budget for budget id = "
					+ aoCBGridBean.getContractBudgetID() + " and subbudgetid = " + aoCBGridBean.getSubBudgetID());
			throw loAppEx;
		}
		return loHourlyPositions;
	}

	// End : Added in R6

	// Start : Added in R6
	/**
	 * This method is used to Add grid data on Personnel Service detailed page
	 * <ul>
	 * <li>Execute query id <b> fetchPersonnelServicePositionId</b></li>
	 * <li>Execute query id <b> insertPersonnelServicesDetailed</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget
	 * @return lbInsertStatus Boolean
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public Boolean addDetailedEmployeeBudget(SqlSession aoMybatisSession,
			PersonnelServiceBudget aoPersonnelServiceBudgetBean) throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoPersonnelServiceBudgetBean);
		LOG_OBJECT.Debug("Entered into updatePersonnelServiceData for addDetailedEmployeeBudget Bean: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		boolean lbInsertStatus = false;
		String lsTransactionName = aoPersonnelServiceBudgetBean.getTransactionName();
		setDetailEmployeeTypeInBean(aoPersonnelServiceBudgetBean, lsTransactionName);
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSR5Constants.INSERT_PERSONNEL_SERVICES_DETAILED, HHSConstants.PERSONNEL_SERVICE_BUDGET);
			updatePersonnelServiceData(aoMybatisSession, aoPersonnelServiceBudgetBean);
			lbInsertStatus = true;

		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while inserting Emplyee budget :  addEmployeeBudget",
					loExp);
			LOG_OBJECT
					.Error("ApplicationException occured while inserting Emplyee budget : addEmployeeBudget " + loExp);
			setMoState("ApplicationException occured while inserting Emplyee budget for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while inserting Emplyee budget :  addEmployeeBudget ", loExp);
			loAppEx.addContextData("Exception occured while inserting Emplyee budget :  addEmployeeBudget", loExp);
			LOG_OBJECT.Error("Exception occured while inserting Emplyee budget : addEmployeeBudget ", loExp);
			setMoState("Exception occured while inserting Emplyee budget for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loAppEx;
		}
		return lbInsertStatus;
	}

	// End : Added in R6

	// Start : Added in R6
	/**
	 * This method is used to merge changes in Personnel Service table from
	 * Personnel Service Detail table
	 * <ul>
	 * <li>Execute query id <b> deletePersonnelServicesForDetail</b></li>
	 * <li>Execute query id <b> updatePersonnelServicesForDetail</b></li>
	 * <li>Execute query id <b> insertPersonnelServicesFromDetails</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget
	 * @return lbUpdateStatus update Status
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	private Boolean updatePersonnelServiceData(SqlSession aoMybatisSession,
			PersonnelServiceBudget aoPersonnelServiceBudgetBean) throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoPersonnelServiceBudgetBean);
		LOG_OBJECT.Debug("Entered into updatePersonnelServiceData for PersonnelServiceBudget Bean: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		boolean lbUpdateStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSR5Constants.DELETE_PERSONNEL_SERVICES_FOR_DETAIL, HHSConstants.PERSONNEL_SERVICE_BUDGET);

			DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSR5Constants.UPDATE_PERSONNEL_SERVICES_FOR_DETAIL, HHSConstants.PERSONNEL_SERVICE_BUDGET);
			DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSR5Constants.INSERT_PERSONNEL_SERVICES_FOR_DETAILS, HHSConstants.PERSONNEL_SERVICE_BUDGET);
			lbUpdateStatus = true;
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData(
					"ApplicationException occured while Updating PS Summary Data :  updatePersonnelServiceData", loExp);
			LOG_OBJECT
					.Error("ApplicationException occured while Updating PS Summary Data :  updatePersonnelServiceData "
							+ loExp);
			setMoState("ApplicationException occured Updating PS Summary Data budget for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while Updating PS Summary Data :  updatePersonnelServiceData", loExp);
			loAppEx.addContextData("Exception occured while Updating PS Summary Data :  updatePersonnelServiceData",
					loExp);
			LOG_OBJECT.Error("Exception occured while Updating PS Summary Data :  updatePersonnelServiceData", loExp);
			setMoState("Exception occured while Updating PS Summary Data for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loAppEx;
		}
		return lbUpdateStatus;
	}

	// End : Added in R6

	// Start : Added in R6
	/**
	 * This method is used to update Summary_ID in Personnel Service Detailed
	 * table from Personnel Service Summary
	 * <ul>
	 * <li>Execute query id <b> updatePsDetailSummaryId</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget
	 * @return lbUpdateStatus update Status
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public Boolean updateSummaryId(SqlSession aoMybatisSession, PersonnelServiceBudget aoPersonnelServiceBudgetBean)
			throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoPersonnelServiceBudgetBean);
		LOG_OBJECT.Debug("Entered into updateSummaryId for PersonnelServiceBudget Bean: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		boolean lbUpdateStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSR5Constants.UPDATE_PERSONNEL_SERVICE_SUMMARY_ID, HHSConstants.PERSONNEL_SERVICE_BUDGET);
			lbUpdateStatus = true;
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while updating SummaryId :  updateSummaryId", loExp);
			LOG_OBJECT.Error("ApplicationException occured while updating SummaryId :  updateSummaryId " + loExp);
			setMoState("ApplicationException occured while updating SummaryId for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while updating SummaryId :  updateSummaryId", loExp);
			loAppEx.addContextData("Exception occured while updating SummaryId :  updateSummaryId", loExp);
			LOG_OBJECT.Error("Exception occured while updating SummaryId :  updateSummaryId", loExp);
			setMoState("Exception occured while updating SummaryId for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loAppEx;
		}
		return lbUpdateStatus;
	}

	// End : Added in R6

	// Start : Added in R6
	/**
	 * This method is used to set Employee type into the Personnel Service bean
	 * on the basis of transaction name.
	 * <ul>
	 * <li>If transaction name is for "salariedPositionGrid", set Emp Type as
	 * "1"</li>
	 * <li>If transaction name is for "hourlyPositionGrid", set Emp Type as "2"</li>
	 * </li>
	 * </ul>
	 * 
	 * @param aoPersonnelServiceBudgetBean PersonnelService BudgetBean set.
	 * @param asTransactionName Transaction name attribute set.
	 */

	private void setDetailEmployeeTypeInBean(PersonnelServiceBudget aoPersonnelServiceBudgetBean,
			String asTransactionName)
	{
		if (asTransactionName.equals(HHSR5Constants.CBY_SALARIED_POSITION_EMPLOYEE_GRID_ADD)
				|| asTransactionName.equals(HHSR5Constants.CB_SALARIED_POSITION_EMPLOYEE_GRID_EDIT)
				|| asTransactionName.equals(HHSR5Constants.CB_SALARIED_POSITION_EMPLOYEE_GRID_DEL))
		{
			aoPersonnelServiceBudgetBean.setEmpType(HHSConstants.ONE);
		}
		else if (asTransactionName.equals(HHSR5Constants.CBY_HOURLY_POSITION_EMPLOYEE_GRID_ADD)
				|| asTransactionName.equals(HHSR5Constants.CB_HOURLY_POSITION_EMPLOYEE_GRID_EDIT)
				|| asTransactionName.equals(HHSR5Constants.CB_HOURLY_POSITION_EMPLOYEE_GRID_DEL))
		{
			aoPersonnelServiceBudgetBean.setEmpType(HHSConstants.TWO);
		}
	}

	// End : Added in R6

	// Start : Added in R6
	/**
	 * This method is used to Delete grid data on Personnel Service detailed
	 * page
	 * <ul>
	 * <li>Execute query id <b> fetchPersonnelServicePositionId</b></li>
	 * <li>Execute query id <b> deletePersonnelServicesDetails</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget
	 * @return loPersonnelServicesData PersonnelServicesData
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public Boolean delDetailEmployeeBudget(SqlSession aoMybatisSession,
			PersonnelServiceBudget aoPersonnelServiceBudgetBean) throws ApplicationException
	{
		//[Start]R7.12.0 QC9311 Minimize Debug
		//LOG_OBJECT.Debug("Entered into delDetailEmployeeBudget for PersonnelServiceBudget Bean:"				+ aoPersonnelServiceBudgetBean.toString());
		//[End]R7.12.0 QC9311 Minimize Debug

		boolean lbDeleteStatus = false;
		String lsPositionId = null;
		String lsTransactionName = aoPersonnelServiceBudgetBean.getTransactionName();
		setDetailEmployeeTypeInBean(aoPersonnelServiceBudgetBean, lsTransactionName);
		try
		{
			lsPositionId = (String) DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSR5Constants.FETCH_PERSONNEL_SERVICE_DETAIL_POSITION_ID, HHSConstants.PERSONNEL_SERVICE_BUDGET);
			DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.DELETE_PERSONNEL_SERVICE_DETAIL,
					HHSConstants.PERSONNEL_SERVICE_BUDGET);
			aoPersonnelServiceBudgetBean.setEmpPosition(lsPositionId);
			updatePersonnelServiceData(aoMybatisSession, aoPersonnelServiceBudgetBean);
			lbDeleteStatus = true;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while deleting Emplyee budget : delEmployeeBudget ",
					loExp);
			LOG_OBJECT.Error("ApplicationException occured while deleting Emplyee budget : delEmployeeBudget \n" +  aoPersonnelServiceBudgetBean.toString(), loExp);
			setMoState("ApplicationException occured while deleting Emplyee budget for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while deleting Emplyee budget :  delEmployeeBudget ", loExp);
			loAppEx.addContextData("Exception occured while deleting Emplyee budget :  delEmployeeBudget", loExp);
			LOG_OBJECT.Error("Exception occured while deleting Emplyee budget : delEmployeeBudget \n" +  aoPersonnelServiceBudgetBean.toString(), loExp);
			setMoState("Exception occured while deleting Emplyee budget for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loAppEx;
		}
		return lbDeleteStatus;

	}

	// End : Added in R6

	// Start : Added in R6
	/**
	 * This method is used to Edit grid data on Personnel Service detailed page
	 * <ul>
	 * <li>Execute query id <b> updatePersonnelServicesDetails</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget
	 * @return loPersonnelServicesData PersonnelServicesData
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public Boolean editDetailEmployeeBudget(SqlSession aoMybatisSession,
			PersonnelServiceBudget aoPersonnelServiceBudgetBean) throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoPersonnelServiceBudgetBean);
		LOG_OBJECT.Debug("Entered into updatePersonnelServicesDetails for PersonnelServiceBudget Bean: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		boolean lbEditStatus = false;
		String lsTransactionName = aoPersonnelServiceBudgetBean.getTransactionName();
		setDetailEmployeeTypeInBean(aoPersonnelServiceBudgetBean, lsTransactionName);
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.UPDATE_PERSONNEL_SERVICE_DETAIL,
					HHSConstants.PERSONNEL_SERVICE_BUDGET);
			updatePersonnelServiceData(aoMybatisSession, aoPersonnelServiceBudgetBean);
			lbEditStatus = true;

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while editing Emplyee budget :  editEmployeeBudget",
					loExp);
			LOG_OBJECT.Error("ApplicationException occured while editing Emplyee budget : editEmployeeBudget ", loExp);
			setMoState("ApplicationException occured while editing Emplyee budget for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while editing Emplyee budget :  editEmployeeBudget ", loExp);
			loAppEx.addContextData("Exception occured while editing Emplyee budget :  editEmployeeBudget", loExp);
			LOG_OBJECT.Error("Exception occured while editing Emplyee budget : editEmployeeBudget ", loExp);
			setMoState("Exception occured while editing Emplyee budget for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loAppEx;
		}
		return lbEditStatus;
	}

	// End : Added in R6

	// Start : Added in R6
	/**
	 * This method is used to fetch Personnel Service Detailed grid data for
	 * Fringe Benefit detailed page
	 * <ul>
	 * <li>Execute query id <b> fetchFringeBenefitsDetail</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoCBGridBeanObj CBGridBean
	 * @return loPersonnelServicesData PersonnelServicesData
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List fetchFringeBenefitsDetail(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj)
			throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoCBGridBeanObj);
		LOG_OBJECT.Debug("Entered into fetchFringeBenefitsDetail for CBGridBean Bean: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		List<PersonnelServiceBudget> loFringBenefitsDetail = null;
		try
		{
			if (null != aoCBGridBeanObj && null != aoCBGridBeanObj.getSubBudgetID())
			{
				// fetching the details for FringeBenefitsDetail Information.
				loFringBenefitsDetail = (List<PersonnelServiceBudget>) DAOUtil.masterDAO(aoMybatisSession,
						aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSR5Constants.FETCH_FRINGE_BENIFITS_DETAIL_DATA, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppExp)
		{
			// setting the transaction state for exception and setting the
			// context
			setMoState("error occured while fetching FringeBenefitsDetail for budget id "
					+ aoCBGridBeanObj.getContractBudgetID());
			loAppExp.addContextData("Exception occured while fetching FringeBenefitsDetail ", loAppExp);
			LOG_OBJECT.Error("error occured while fetching FringeBenefitsDetail ", loAppExp);
			throw loAppExp;
		}
		return loFringBenefitsDetail;
	}

	// End : Added in R6

	// Start : Added in R6
	/**
	 * This method is used to edit Fringe Benefit Labels in grid data for Fringe
	 * Benefit detailed page
	 * <ul>
	 * <li>Execute query id <b> updateFringeBenefitsDetails</b></li>
	 * <li>Execute query id <b> updateFringeBenefitsSummary</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoPersonnelServiceBudgetBean PersonnelServiceBudget
	 * @return loPersonnelServicesData PersonnelServicesData
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public Boolean editFringeBenefitsDetail(SqlSession aoMybatisSession,
			PersonnelServiceBudget aoPersonnelServiceBudgetBean) throws ApplicationException
	{
		
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoPersonnelServiceBudgetBean);
		LOG_OBJECT.Debug("Entered into editFringeBenefitsDetail for PersonnelServiceBudget Bean: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		boolean lbEditStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.UPDATE_FRINGE_BENIFITS_DETAIL,
					HHSConstants.PERSONNEL_SERVICE_BUDGET);

			DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudgetBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.UPDATE_FRINGE_BENIFITS_SUMMARY,
					HHSConstants.PERSONNEL_SERVICE_BUDGET);

			lbEditStatus = true;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while editing FringeBenefitsDetail ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while editing FringeBenefitsDetail ", loExp);
			setMoState("ApplicationException occured while editing FringeBenefitsDetail for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while editing FringeBenefitsDetail ", loExp);
			loAppEx.addContextData("Exception occured while editing FringeBenefitsDetail ", loExp);
			LOG_OBJECT.Error("Exception occured while editing FringeBenefitsDetail ", loExp);
			setMoState("Exception occured while editing FringeBenefitsDetail for budget id = "
					+ aoPersonnelServiceBudgetBean.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetBean.getSubBudgetID());
			throw loAppEx;
		}
		return lbEditStatus;
	}

	// End : Added in R6

	// Start : Added in R6
	/**
	 * This method is used to fetch Personnel Service Detailed non grid data.
	 * Updated in Release 5.1.0
	 * <ul>
	 * <li>Execute query id <b> fetchFringeBenefitsSummaryData</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoCBGridBeanObj CBGridBean
	 * @return loPersonnelServicesData PersonnelServicesData
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public PersonnelServicesData fetchNonGridForPSSummaryData(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj)
			throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoCBGridBeanObj);
		LOG_OBJECT.Debug("Entered into fetchNonGridForPSSummaryData for CBGridBean Bean: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		PersonnelServicesData loPersonnelServicesData = new PersonnelServicesData();
		try
		{
			// Commented as part of FTE calculations changes in Release 5.1.0
			// HashMap<String, String> loApplicationSettingMap =
			// (HashMap<String, String>) BaseCacheManagerWeb
			// .getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			// aoCBGridBeanObj.setDefaultCityFte((String)
			// loApplicationSettingMap.get(HHSR5Constants.DEFAULT_CITY_FTE
			// + HHSConstants.UNDERSCORE + HHSR5Constants.DEFAULT_VALUE));
			aoCBGridBeanObj.setDefaultCityFte((String) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.GET_FULL_TIME_EMP_DATA,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN));
			loPersonnelServicesData = (PersonnelServicesData) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.FETCH_FRINGE_BENIFITS_SUMMARY,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured for personnel services Summary and Detail"
					+ ": fetchNonGridForPSSummaryData ", loExp);
			LOG_OBJECT
					.Error("ApplicationException occured for personnel services Summary and Detail : fetchNonGridForPSSummaryData ",
							loExp);
			setMoState("ApplicationException occured for personnel services for sub budget id = "
					+ aoCBGridBeanObj.getContractBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured for personnel services : fetchNonGridForPSSummaryData ", loExp);
			loAppEx.addContextData(
					"Exception occured for personnel services Summary and Detail: fetchNonGridForPSSummaryData ", loExp);
			LOG_OBJECT
					.Error("Exception occured for personnel services Summary and Detail: fetchNonGridForPSSummaryData ",
							loExp);
			setMoState("Exception occured while fetching non-grid data for personnel services for sub budget id = "
					+ aoCBGridBeanObj.getContractBudgetID());
			throw loAppEx;
		}
		return loPersonnelServicesData;
	}

	// End : Added in R6

	// Start : Added in R6
	/**
	 * This method is used to fetch date when the Contract Review Task was
	 * finished
	 * <ul>
	 * <li>load message from messages.properties for
	 * <b>MESSAGE_BUDGET_APPROVED_DATE</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @return lsMessage String Budget Approval date
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */

	public String fetchBudgetApprovedDate(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj)
			throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoCBGridBeanObj);
		LOG_OBJECT.Debug("Entered into fetchBudgetApprovedDate for CBGridBean Bean: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		String lsMessage = null;
		try
		{
			lsMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSR5Constants.MESSAGE_BUDGET_APPROVED_DATE);
		}
		// catch any application exception thrown from the code due to SELECT
		// statement and throw it
		// forward
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while fetchBudgetApprovedDate"
					+ " for personnel services : fetchBudgetApprovedDate ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while fetchBudgetApprovedDate"
					+ " master data for personnel services : fetchBudgetApprovedDate ", loExp);
			setMoState("ApplicationException occured while fetchBudgetApprovedDate");
			throw loExp;
		}
		// Catch any Null pointer exception thrown from the code and wrap it
		// into application
		// Exception and throw it forward
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while fetching fetchBudgetApprovedDate :" + " fetchBudgetApprovedDate ", loExp);
			loAppEx.addContextData("Exception occured while fetchBudgetApprovedDate :" + " fetchBudgetApprovedDate ",
					loExp);
			LOG_OBJECT.Error(
					"Exception occured while fetchBudgetApprovedDate " + "services : fetchBudgetApprovedDate ", loExp);
			setMoState("Exception occured while fetchBudgetApprovedDate " + "services : fetchBudgetApprovedDate ");
			throw loAppEx;
		}

		return lsMessage;

	}

	// End : Added in R6

	// Start : Added in R6
	/**
	 * This method performs validation if Parent Budget is approved after
	 * contract ammendment.
	 * <ul>
	 * <li>Execute query id <b> checkIfOtherBudgetApproved</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SqlSession object
	 * @param aoBudgetId : BudgetId of BudgetType
	 * @return Boolean: true for success and false for validation failure
	 * @throws ApplicationException ApplicationException object
	 */
	public Boolean checkIfBaseBudgetApproved(SqlSession aoMyBatisSession, String aoBudgetId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into checkIfBaseBudgetApproved for BudgetId:" + aoBudgetId);
		Boolean loValid = true;
		Integer liPendingBudget = HHSConstants.INT_ZERO;
		try
		{
			liPendingBudget = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoBudgetId,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.CHECK_IF_PENDING_BUDGET,
					HHSConstants.JAVA_LANG_STRING);
			if (liPendingBudget > HHSConstants.INT_ZERO)
			{
				loValid = false;
			}
		}
		// Application Exception handled here
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ContractBudgetService:" + " checkIfBaseBudgetApproved method:: ",
					aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: checkIfBaseBudgetApproved "
					+ "method - failed Exception occured while validating Budget Task\n");
			throw aoAppEx;
		}

		// Exception handled here - May occur for any unpredictable situation
		catch (Exception aoEx)
		{
			setMoState("Transaction Failed:: ContractBudgetService: checkIfBaseBudgetApproved"
					+ " method - failed to validate record " + " \n");
			throw new ApplicationException("Exception occured while validating Budget Task :"
					+ " ContractBudgetService method", aoEx);
		}
		return loValid;
	}

	// End : Added in R6

	// Start : Added in R6
	/**
	 * This method is used to fetch Personnel Service Detailed non grid data.
	 * Updated in Release 5.1.0
	 * <ul>
	 * <li>Execute query id <b> fetchFringeBenefitsDetailData</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoCBGridBeanObj CBGridBean
	 * @return loPersonnelServicesData PersonnelServicesData
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public PersonnelServicesData fetchNonGridForPSDetailData(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj)
			throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoCBGridBeanObj);
		LOG_OBJECT.Debug("Entered into fetchNonGridForPSDetailData for CBGridBean Bean: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		PersonnelServicesData loPersonnelServicesData = new PersonnelServicesData();
		try
		{
			// Commented as part of FTE calculations changes in Release 5.1.0
			// HashMap<String, String> loApplicationSettingMap =
			// (HashMap<String, String>) BaseCacheManagerWeb
			// .getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			aoCBGridBeanObj.setDefaultCityFte((String) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.GET_FULL_TIME_EMP_DATA,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN));
			loPersonnelServicesData = (PersonnelServicesData) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.FETCH_FRINGE_BENIFITS_DETAIL,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured for personnel services Summary and Detail"
					+ ": fetchNonGridForPSDetailData ", loExp);
			LOG_OBJECT
					.Error("ApplicationException occured for personnel services Summary and Detail : fetchNonGridForPSDetailData ",
							loExp);
			setMoState("ApplicationException occured for personnel services for sub budget id = "
					+ aoCBGridBeanObj.getContractBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured for personnel services : fetchNonGridForPSDetailData ", loExp);
			loAppEx.addContextData(
					"Exception occured for personnel services Summary and Detail: fetchNonGridForPSDetailData ", loExp);
			LOG_OBJECT.Error(
					"Exception occured for personnel services Summary and Detail: fetchNonGridForPSDetailData ", loExp);
			setMoState("Exception occured while fetching non-grid data for personnel services for sub budget id = "
					+ aoCBGridBeanObj.getContractBudgetID());
			throw loAppEx;
		}
		return loPersonnelServicesData;
	}
	// End : Added in R6
	// end
	
	//Start R7: Program Income code
	/**
	 * On submission of Contract configuration, this method is used to delete 
	 * program income default entries from program income table. If deleted
	 * successfully, update is_old_pi flag of base budget in budget table Added
	 * in Release 7.0.0
	 * <ul>
	 * <li>Execute query id <b> deleteDefaultPIEntries</b></li>
	 * <li>Execute query id <b> updateIsOldPIFlag</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoTaskDetailsBean {@link TaskDetailsBean}
	 * @param aoFinalFininsh Boolean
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return Boolean
	 */
	public Boolean deleteDefaultPIForNewConf(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean,
			Boolean aoFinalFininsh) throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoTaskDetailsBean);
		LOG_OBJECT.Debug("Entered into deleteDefaultPIForNewConf for aoTaskDetailsBean Bean: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		try
		{
			if (aoFinalFininsh)
			{
				Map<String, String> loHashMap = new HashMap<String, String>();
				loHashMap.put(HHSConstants.CONTRACT_ID1, aoTaskDetailsBean.getContractId());
				loHashMap.put(HHSConstants.MODIFIED_BY_AGENCY, aoTaskDetailsBean.getUserId());
				loHashMap.put(HHSConstants.FISCAL_YEAR_ID,aoTaskDetailsBean.getStartFiscalYear());
				
				int liRowsDeleted = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.DELETE_DEFAULT_PI_ENTRIES,
						HHSConstants.JAVA_UTIL_MAP);
				LOG_OBJECT.Debug("Deleted default PI entries!! No. of rows deleted : " + liRowsDeleted);
				loHashMap.put(HHSR5Constants.PI_STATUS, HHSConstants.STRING_ZERO);
				int liRowsUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.UPDATE_IS_OLD_PI_FLAG,
						HHSConstants.JAVA_UTIL_MAP);
				LOG_OBJECT.Debug("Updated is_old_pi flag!! No. of rows updated : " + liRowsUpdated);
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ContractBudgetService:" + " deleteDefaultPIForNewConf method:: ",
					aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: deleteDefaultPIForNewConf "
					+ "method - failed Exception occured while deleting PI default entries for budget\n");
			throw aoAppEx;
		}

		// Exception handled here - May occur for any unpredictable situation
		catch (Exception aoEx)
		{
			setMoState("Transaction Failed:: ContractBudgetService: deleteDefaultPIForNewConf"
					+ " method - failed to validate record " + " \n");
			throw new ApplicationException("Exception occured while deleting default PI entries :"
					+ " ContractBudgetService method", aoEx);
		}
		return HHSR5Constants.BOOLEAN_TRUE;

	}
	
	/**
     * Added in R7 for program income
	 * On submission of Amendment Contract configuration or Update
	 * Configuration, this method is used to check if program income is
	 * configured for base budget or not. And check for amendment budget as well
	 * in budget_customization table If configured, delete program income
	 * default entries from program income table. If deleted successfully,
	 * update is_old_pi flag of base budget in budget table Added in Release
	 * 7.0.0
	 * <ul>
	 * <li>Execute query id <b> isPIInBudgetCustomizationForBaseBudget</b></li>
	 * <li>Execute query id <b> isPICategoryInBudgetCustomization</b></li>
	 * <li>Execute query id <b> deleteDefaultPIEntries</b></li>
	 * <li>Execute query id <b> updateIsOldPIFlag</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoTaskDetailsBean {@link TaskDetailsBean}
	 * @param aoFinalFininsh {@link Boolean}
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public Boolean validateAndDeleteDefaultPI(SqlSession aoMybatisSession, TaskDetailsBean aoTaskDetailsBean,
			Boolean aoFinalFininsh) throws ApplicationException
	{
		
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoTaskDetailsBean);
		LOG_OBJECT.Debug("Entered into validateAndDeleteDefaultPI to :" + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs
		boolean lbResult = HHSR5Constants.BOOLEAN_FALSE;
		try
		{
			if (aoFinalFininsh)
			{
				Map<String, String> loHashMap = new HashMap<String, String>();
				loHashMap.put(HHSConstants.CONTRACT_ID1, aoTaskDetailsBean.getContractId());
				loHashMap.put(HHSConstants.MODIFIED_BY_AGENCY, aoTaskDetailsBean.getUserId());
				//Get impacted budget list of particular contract. 
				// (i.e if amendment is done on 2 FYs. This will return list of
				// two budgets.)
				List<ContractBean> loAffectedBudgetFYList = (List<ContractBean>) DAOUtil.masterDAO(aoMybatisSession,
						loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSR5Constants.FETCH_AFFECTED_BUDGET_FY_LIST, HHSConstants.JAVA_UTIL_MAP);
				for (ContractBean affectedBudget : loAffectedBudgetFYList)
				{   
					//Added for setting is_old_pi flag to 1 when base budget has PI selected and amendment is 
					//being done on the budget to show old PI screen
					int lirowsUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession,
							affectedBudget.getAmendAffectedBudgetId(),
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,HHSR5Constants.UPDATE_IS_OLD_PI_FLAG_FOR_OLD_PI,
							HHSConstants.JAVA_LANG_STRING);
					LOG_OBJECT.Debug("Updated is_old_pi flag!! No. of rows updated with flag as 1 : "
							+ lirowsUpdated);
					
					//Check is_old_pi flag for base budget.
					String lsIsOldPI = (String) DAOUtil.masterDAO(aoMybatisSession,
							affectedBudget.getAmendAffectedBudgetId(),
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.IS_OLD_PI,
							HHSConstants.JAVA_LANG_STRING);

					if (!HHSConstants.ZERO.equals(lsIsOldPI))
					{
						loHashMap.put(HHSConstants.FISCAL_YEAR_ID, affectedBudget.getContractStartFiscalYear()
								.toString());
						//Check PI category opted in base budget or not
						int liBaseBudgetCustIdCount = Integer.parseInt((String) DAOUtil.masterDAO(aoMybatisSession,
								loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
								HHSR5Constants.IS_PI_CATEGORY_IN_BASE_BUDGET_CUST, HHSConstants.JAVA_UTIL_MAP));
						LOG_OBJECT.Debug("Checked budget_customization_id for base budget of budgetId :"
								+ aoTaskDetailsBean.getEntityId() + " and budget_customization_id is : "
								+ liBaseBudgetCustIdCount);
						boolean lbUpdateFlag = HHSR5Constants.BOOLEAN_TRUE;
						if (liBaseBudgetCustIdCount <= HHSConstants.INT_ZERO)
						{
							// Check Pi category opted in any Amendment or
							// Update Budget which has not been
							//merged with base budget yet
							int liPrevBudgetPICustCount = Integer.parseInt((String) DAOUtil.masterDAO(aoMybatisSession,
									loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
									HHSR5Constants.PREV_AMEND_UPDT_PI_BUDGET_CUST_COUNT, HHSConstants.JAVA_UTIL_MAP));
							if (liPrevBudgetPICustCount <= HHSConstants.INT_ZERO)
							{
									deleteDefaultPIForNewConf(aoMybatisSession, aoTaskDetailsBean, aoFinalFininsh);
									lbUpdateFlag = HHSR5Constants.BOOLEAN_FALSE;
									lbResult = HHSR5Constants.BOOLEAN_TRUE;
							}
						}
						if (lbUpdateFlag)
						{
							loHashMap.put(HHSR5Constants.PI_STATUS, HHSConstants.ONE);
							int rowsUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
									HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
									HHSR5Constants.UPDATE_IS_OLD_PI_FLAG, HHSConstants.JAVA_UTIL_MAP);
							LOG_OBJECT.Debug("Updated is_old_pi flag!! No. of rows updated with flag as 1 : "
									+ rowsUpdated);
						}
					}
					//Added for when new sub budget is added in update/ amendment
					else 
					{
						lbResult = HHSR5Constants.BOOLEAN_TRUE;
					}
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ContractBudgetService:" + " validateAndDeleteDefaultPI method:: ",
					aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: validateAndDeleteDefaultPI "
					+ "method - failed Exception occured while checking PI default entries for budget\n");
			throw aoAppEx;
		}

		// Exception handled here - May occur for any unpredictable situation
		catch (Exception aoEx)
		{
			setMoState("Transaction Failed:: ContractBudgetService: validateAndDeleteDefaultPI"
					+ " method - failed to validate record " + " \n");
			throw new ApplicationException("Exception occured while validating Budget Task :"
					+ " ContractBudgetService method", aoEx);
		}
		return lbResult;
	}
	
	/**
     * Added in R7 for program income
	 * This method is used to calculate update and then fetch program income 
	 * indirect rate for contract budget and modification if base budget is
	 * eligible for new program income (i.e post release 7). 1 = Budget
	 * Amendment; 2 = Contract Budget; 3 = Budget Modification; 4 = Budget
	 * Update <br/>
	 * <b>Functionalities by below method are :</b>
	 * <ul>
	 * <li>1.update program income Indirect Rate percentage.
	 * updateIndirectRatePercentage for Contract Budget and
	 * updateIndirectRateModificaitonPercentage for Contract Modification or
	 * Contract Update or Contract Amendment</li>
	 * <li>1.Call fetchIndirectRatePercentage query get program income indirect
	 * rate percentage entry.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession Sql session object as input.
	 * @param aoCBGridBeanObj CBGridBean Bean as input.
	 * @param aoMasterBean MasterBean Bean as input.
	 * @return lsUpdatedIndirectRatePercentage as output.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public String updatePIIndirectRatePercentage(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj,
			MasterBean aoMasterBean) throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoCBGridBeanObj);
		String param1 = CommonUtil.maskPassword(aoMasterBean);
		
		LOG_OBJECT.Debug("Entered into updatePIIndirectRatePercentage with :" + param + " and : "
				+ param1);

		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		String lsUpdatedIndirectRatePercentage = HHSConstants.STRING_ZERO;
		try
		{
			String isOldPI = (String) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj.getContractBudgetID(),
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.IS_OLD_PI,
					HHSConstants.JAVA_LANG_STRING);
			if (HHSConstants.ZERO.equals(isOldPI))
			{
				String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_BUDGET_APPROVED);
				
				// Start QC 9202 R 7.10.0 - get fiscal year for subbudget or budget
				if (null!= aoCBGridBeanObj && (null==aoCBGridBeanObj.getFiscalYearID() || aoCBGridBeanObj.getFiscalYearID().isEmpty()))
				{
					String lsFiscalYearId = getFiscalYearIDfromDB(aoMybatisSession, aoCBGridBeanObj);
					aoCBGridBeanObj.setFiscalYearID(lsFiscalYearId);
					LOG_OBJECT.Debug("Fiscal Year Id from aoCBGridBeanObj :: "+aoCBGridBeanObj.getFiscalYearID());
				}
				// End QC 9202 R 7.10.0 - get fiscal year for subbudget	or budget
					
				if (null!= aoCBGridBeanObj && null!= aoCBGridBeanObj.getBudgetTypeId())
				{
					String lsQuery = HHSR5Constants.PI_INDIRECT_RATE_UPDATE_PERCENTAGE_QUERY_MAP.get(aoCBGridBeanObj
							.getBudgetTypeId());

					if (aoMasterBean != null && HHSConstants.ONE.equals(aoCBGridBeanObj.getBudgetTypeId())
							&& aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
					{
						String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
						Iterator<LineItemMasterBean> loListIterator = aoMasterBean.getMasterBeanList().iterator();
						while (loListIterator.hasNext())
						{
							LineItemMasterBean loLineItemBean = loListIterator.next();
							if (loLineItemBean.getSubbudgetId().equals(lsSubBudgetId))
							{
								lsUpdatedIndirectRatePercentage = loLineItemBean.getPiIndirectRatePercent();
							}
						}
					}
					else if (lsQuery != null)
					{
						DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, lsQuery,
								HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

						lsUpdatedIndirectRatePercentage = (String) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
								HHSR5Constants.FETCH_PI_INDIRECT_RATE_PERCENTAGE,
								HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

						setMoState("Success while fetching Indirect Rate percentage for business type id "
								+ aoCBGridBeanObj.getBudgetTypeId());
					}
				}
				if (lsUpdatedIndirectRatePercentage == null)
				{
					lsUpdatedIndirectRatePercentage = HHSConstants.DECIMAL_ZERO;
				}

			}
			if (lsUpdatedIndirectRatePercentage != null)
			{
				lsUpdatedIndirectRatePercentage = String.format("%1$,.2f",
						Double.parseDouble((lsUpdatedIndirectRatePercentage)));
			}
		}
		catch (ApplicationException loAppExp)
		{
			// Set the state, added context data and added error log if any
			// application exception occurs
			setMoState("Error occured while fetching Program Income Indirect Rate percentage for business type id "
					+ aoCBGridBeanObj.getBudgetTypeId());
			loAppExp.addContextData(HHSConstants.BUDGET_TYPE_ID, aoCBGridBeanObj.getBudgetTypeId());
			loAppExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBGridBeanObj.getSubBudgetID());
			loAppExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBGridBeanObj.getContractBudgetID());
			LOG_OBJECT.Error("error occured while fetching Program Income Indirect Rate percentage", loAppExp);
			throw loAppExp;
		}
		catch (Exception loExp)
		{
			// Set the state, added context data and added error log if any
			// exception occurs
			setMoState("Error occured while fetching Program Income Indirect Rate percentage for business type id "
					+ aoCBGridBeanObj.getBudgetTypeId());
			LOG_OBJECT.Error("error occured while fetching Program Income Indirect Rate percentage", loExp);
			throw new ApplicationException("Exception occured while fetching Program Income Indirect Rate percentage ",
					loExp);
		}
		return lsUpdatedIndirectRatePercentage;
	}
	//End R7: Program Income code
	/**
	 * The method is added in Release 7. It will fetch the budget details. If
	 * the master is not null in case of approved modification, it will return
	 * the master bean data. Otherwise, it get get data from Budget Table.
	 * @param aoMybatisSession
	 * @param aoHashMap
	 * @param aoMasterBean
	 * @return
	 * @throws ApplicationException
	 */
	public BudgetDetails fetchFyModificationBudgetSummary(SqlSession aoMybatisSession,
			HashMap<String, String> aoHashMap, MasterBean aoMasterBean) throws ApplicationException
	{
		BudgetDetails loFyBudget = new BudgetDetails();
		String lsBudgetType = HHSConstants.EMPTY_STRING;
		try
		{
			lsBudgetType = (String) DAOUtil.masterDAO(aoMybatisSession, aoHashMap.get(HHSConstants.BUDGET_ID_WORKFLOW),
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_BUDGET_TYPE,
					HHSConstants.JAVA_LANG_STRING);
									
			if (aoMasterBean != null)
			{
				loFyBudget = aoMasterBean.getBudgetDetails();
			}
			else
			{
				if (lsBudgetType != null && lsBudgetType.equalsIgnoreCase(HHSConstants.FOUR))
				{
					loFyBudget = (BudgetDetails) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_FY_UPDATE_BUDGET_SUMMARY,
							HHSConstants.JAVA_UTIL_HASH_MAP);
				}
				else if (lsBudgetType != null && lsBudgetType.equalsIgnoreCase(HHSConstants.THREE))
				{
					loFyBudget = (BudgetDetails) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.FETCH_FY_MODIFICATION_BUDGET_SUMMARY, HHSConstants.JAVA_UTIL_HASH_MAP);
				}
				else
				{
					loFyBudget = (BudgetDetails) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_FY_BUDGET_SUMMARY,
							HHSConstants.JAVA_UTIL_HASH_MAP);
				}
				if (loFyBudget == null)
				{
					loFyBudget = new BudgetDetails();
					return loFyBudget;
				}
				else
				{
					setBudgetDetails(aoMybatisSession, aoHashMap, loFyBudget, lsBudgetType);
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CONTRACT_ID_WORKFLOW, aoHashMap);
			LOG_OBJECT.Error(
					"Exception occured while retrieveing Fiscal Year Contract Information in ContractBudgetService ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchFyModificationBudgetSummary method - failed to fetch"
					+ aoHashMap + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error(
					"Exception occured while retrieveing Fiscal Year Contract Information in ContractBudgetService ",
					loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchFyModificationBudgetSummary method - failed to fetch"
					+ aoHashMap + " \n");
			throw new ApplicationException("Error occured while retrieving Fiscal Year Contract Summary", loAppEx);
		}
		return loFyBudget;
	}
	//Added in R7 for Program Income 
	/**
	 * Added in R7 This method is used fetch sources that will be displayed in
	 * dropdown in sources column in program income grid in all budget
	 * categories Execute query id <b> fetchSources</b></li>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoCBGridBeanObj CBProgramIncomeBean object
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return String - list of sources
	 */
	@SuppressWarnings("unchecked")
	public String fetchSources(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj) throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoCBGridBeanObj);
		LOG_OBJECT.Debug("Entered into fetchSources with aoCBGridBeanObj Bean: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		String lsPISourcesData = null;
		List<String> loSourceList = null;
		try
		{
			// Query will return master data in concatenated form
			loSourceList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, HHSConstants.ONE,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.FETCH_SOURCES_FOR_PI,
					HHSConstants.JAVA_LANG_STRING);
			if (loSourceList == null || loSourceList.isEmpty())
			{
				throw new ApplicationException("Sources not fetched successfully ");
			}

			lsPISourcesData = convertPositionMasterList(loSourceList);

		}
		// catch any application exception thrown from the code due to SELECT
		// statement and throw it
		// forward
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while fetching Program income table master data"
					+ " for program income : fetchSources ", loExp);
			LOG_OBJECT.Error("ApplicationException occured while fetching data from Program income table"
					+ " master data for program income : fetchSources ", loExp);
			setMoState("ApplicationException occured while fetching Program income table"
					+ " master data for Program income ");
			throw loExp;
		}
		// Catch any Null pointer exception thrown from the code and wrap it
		// into application
		// Exception and throw it forward
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while fetching program income table master data for program income :"
							+ " fetchSources ", loExp);
			loAppEx.addContextData(
					"Exception occured while fetching program income table master data for program income:"
							+ " fetchSources ", loExp);
			LOG_OBJECT
					.Error("Exception occured while fetching program income table master data for program income : fetchSources ",
							loExp);
			setMoState("Exception occured while fetching program income table master data for program income : fetchSources ");
			throw loAppEx;
		}

		return lsPISourcesData;

	}

	/**
	 * Added in R7 This method is used to delete a row when delete operation is
	 * performed in program income grid in all budget categories Execute query
	 * id <b> fetchSources</b></li>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoCBProgramIncomeBean CBProgramIncomeBean object
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return Boolean
	 */
	public Boolean deleteProgramIncome(SqlSession aoMybatisSession, CBProgramIncomeBean aoCBProgramIncomeBean)
			throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoCBProgramIncomeBean);
		LOG_OBJECT.Debug("Entered into deleteProgramIncome with :" + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		
		Boolean lbDeletePI = true;
		Integer liStatus = HHSConstants.INT_ZERO;
		try
		{
			liStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoCBProgramIncomeBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,HHSR5Constants.DELETE_PROGRAM_INCOME,
					HHSConstants.PROGRAM_INCOME_BEAN);
			if (liStatus <= HHSConstants.INT_ZERO)
			{
				lbDeletePI = false;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoExp)
		{
			setMoState("error occured while deleting program income Details for budget type id "
					+ aoCBProgramIncomeBean.getBudgetTypeId());
			aoExp.addContextData("Exception occured while deleting Program income Details ", aoExp);
			LOG_OBJECT.Error("error occured while deleting Program income Details ", aoExp);
			throw aoExp;
		}
		// handling exception other than ApplicationException
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while deleting Program income in ContractBudgetService ", aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: deleteProgramIncome method - failed to delete"
					+ aoCBProgramIncomeBean.getBudgetTypeId() + " \n");
			throw new ApplicationException("Error occured while deleting Program income", aoAppEx);
		}
		return lbDeletePI;
	}

	/**
	 * Added in R7 This method is used to insert a new row in program income
	 * table when the user adds a row in program income grid in any budget
	 * category Execute query id <b> insertProgramIncomeGrid</b></li>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoCBGridBeanObj CBProgramIncomeBean object
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 * @return Boolean lbInsertStatus - insertion status
	 */
	public Boolean addProgramIncome(SqlSession aoMybatisSession, CBProgramIncomeBean aoCBGridBeanObj)
			throws ApplicationException
	{
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoCBGridBeanObj);
		LOG_OBJECT.Debug("Entered into addProgramIncome with :: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		boolean lbInsertStatus = false;

		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSR5Constants.INSERT_PROGRAM_INCOME_GRID, HHSConstants.PROGRAM_INCOME_BEAN);

			lbInsertStatus = true;

		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("ApplicationException occured while inserting program income :  addProgramIncome",
					loExp);
			LOG_OBJECT.Error("ApplicationException occured while inserting program income : addProgramIncome " + loExp);
			setMoState("ApplicationException occured while inserting program income for budget id = "
					+ aoCBGridBeanObj.getContractBudgetID() + " and subbudgetid = " + aoCBGridBeanObj.getSubBudgetID());
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured while inserting  program income :  addProgramIncome ", loExp);
			loAppEx.addContextData("Exception occured while inserting program income  :  addProgramIncome", loExp);
			LOG_OBJECT.Error("Exception occured while inserting program income  : addProgramIncome ", loExp);
			setMoState("Exception occured while inserting program income for budget id = "
					+ aoCBGridBeanObj.getContractBudgetID() + " and subbudgetid = " + aoCBGridBeanObj.getSubBudgetID());
			throw loAppEx;
		}
		return lbInsertStatus;
	}
	// R7 program income changes end

	// Start: Added in R7 for cost-Center
	/**
	 * This method is added in R7 for Cost Center Inserts in Cost_center_details
	 * and bc_Service_details if cost_center is enabled for contractID.This
	 * method is called on finish of Contract/Update/Amendment Configurations.
	 * @param aoTaskDetailsBean
	 * @param aoMyBatisSession
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public void insertServiceAndCostCenter(TaskDetailsBean aoTaskDetailsBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered ContractBudgetService-->insertServiceAndCostCenter method");
		List<CBGridBean> loCBGridBeanList = null;
		List<CBGridBean> loCBGridBeanListForUpdate = null;
		try
		{
			Map<String, Object> loQueryMap = new HashMap<String, Object>();
			loQueryMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
			LOG_OBJECT.Info("Checking whether CostCenter is enabled");
			String lsCostOpted = (String) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.FETCH_COST_CENTER_OPTED,
					HHSConstants.JAVA_UTIL_MAP);
			LOG_OBJECT.Info("CostCenter is enabled " + lsCostOpted);
			if (HHSConstants.TWO.equals(lsCostOpted)
					&& !HHSConstants.TASK_AMENDMENT_CONFIGURATION.equals(aoTaskDetailsBean.getTaskName()))
			{
				String lsContractId = aoTaskDetailsBean.getContractId();
				String lsFiscalYearId = aoTaskDetailsBean.getStartFiscalYear();
				loQueryMap.put(HHSConstants.CLC_FISCAL_YEAR_ID, lsFiscalYearId);

				if (null != lsContractId && !lsContractId.trim().isEmpty() && null != lsFiscalYearId
						&& !lsFiscalYearId.trim().isEmpty())
				{
					LOG_OBJECT.Info("Fetching BudgetList and SubBudgetList for contractID " + lsContractId);
					loCBGridBeanList = (List<CBGridBean>) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.CBY_FETCH_SUB_BUDGET_ID_LIST, HHSConstants.JAVA_UTIL_MAP);
					// Fetching base Sub-budgets and inserting newly added
					// Services data during Update/Amendment Configuration
					loCBGridBeanListForUpdate = (List<CBGridBean>) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSR5Constants.CBY_FETCH_SUB_BUDGET_ID_LIST_FOR_UPDATE, HHSConstants.JAVA_UTIL_MAP);
					LOG_OBJECT.Info("Fetched BudgetList and SubBudgetList for contractID " + lsContractId);
				}
				for (CBGridBean loCBGridBean : loCBGridBeanListForUpdate)
				{
					performServicesDefaultInsertion(aoTaskDetailsBean, aoMyBatisSession, loCBGridBean);
				}
				// Setting newRecord property to identify newly created
				// sub-budgets(i.e. Sub_budget_id=parent_id)
				for (CBGridBean loCBGridBean : loCBGridBeanList)
				{
					loCBGridBean.setNewRecord(HHSConstants.ONE);
					performServicesDefaultInsertion(aoTaskDetailsBean, aoMyBatisSession, loCBGridBean);
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.CB_GRID_BEAN_LIST, CommonUtil.convertBeanToString(loCBGridBeanList));
			LOG_OBJECT.Error("App Exception occured in ContractBudgetService: insertServiceAndCostCenter method:: ",
					loAppEx);
			setMoState("App Exception occured in ContractBudgetService: insertServiceAndCostCenter method");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			// Context Data is added into Exception object and Log is updated
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "ContractBudgetService: insertServiceAndCostCenter method:: ", loEx);
			loAppEx.addContextData(HHSConstants.CB_GRID_BEAN_LIST, CommonUtil.convertBeanToString(loCBGridBeanList));
			LOG_OBJECT.Error("Exception occured in ContractBudgetService: insertServiceAndCostCenter method:: ",
					loAppEx);
			setMoState("Exception occured in ContractBudgetService: insertServiceAndCostCenter method");
			throw loAppEx;
		}
	}

	/**
	 * Added in R7 This method is called from insertServiceAndCostCenter to
	 * insert Default Services and Cost-Center
	 * @param aoTaskDetailsBean
	 * @param aoMyBatisSession
	 * @param loCBGridBean
	 * @throws ApplicationException
	 */
	private void performServicesDefaultInsertion(TaskDetailsBean aoTaskDetailsBean, SqlSession aoMyBatisSession,
			CBGridBean loCBGridBean) throws ApplicationException
	{
		LOG_OBJECT.Info("Deleting from COST_CENTER_DETAILS and SERVICES_DETAILS for SubBudgetId:"
				+ loCBGridBean.getSubBudgetID() + "and BudgetID:" + loCBGridBean.getContractBudgetID());
		Boolean lbDeleteFlag = true;
		if(HHSR5Constants.UPDATE_SERVICES.equalsIgnoreCase(aoTaskDetailsBean.getEventName()))
		{
			lbDeleteFlag = false;
		}
		// Deletions for Deleting budget services configurations in case of
		// returning from Contract COF
		if (HHSConstants.ONE.equalsIgnoreCase(loCBGridBean.getNewRecord()) && lbDeleteFlag)
		{
			DAOUtil.masterDAO(aoMyBatisSession, loCBGridBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSR5Constants.DELETE_COST_CENTER_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			DAOUtil.masterDAO(aoMyBatisSession, loCBGridBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSR5Constants.DELETE_SERVICES_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			LOG_OBJECT.Info("Successfully Deleted from COST_CENTER_DETAILS and SERVICES_DETAILS for SubBudgetId:"
					+ loCBGridBean.getSubBudgetID() + "and BudgetID:" + loCBGridBean.getContractBudgetID());
		}
		loCBGridBean.setCreatedByUserId(aoTaskDetailsBean.getUserId());
		loCBGridBean.setModifiedByUserId(aoTaskDetailsBean.getUserId());
		// For Previous Year Sub-budgets carry forwarded in New FY Task- Entry like new Sub-budgets
		if(HHSConstants.TASK_NEW_FY_CONFIGURATION.equals(aoTaskDetailsBean.getTaskName()))
		{
			loCBGridBean.setNewRecord(HHSConstants.ONE);
		}
		LOG_OBJECT.Info("Inserting in COST_CENTER_DETAILS and SERVICES_DETAILS for SubBudgetId:"
				+ loCBGridBean.getSubBudgetID() + "and BudgetID:" + loCBGridBean.getContractBudgetID() + "and UserID:"
				+ loCBGridBean.getCreatedByUserId());
		DAOUtil.masterDAO(aoMyBatisSession, loCBGridBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
				HHSR5Constants.INSERT_COST_CENTER_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		DAOUtil.masterDAO(aoMyBatisSession, loCBGridBean, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
				HHSR5Constants.INSERT_SERVICES_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		LOG_OBJECT.Info("Successfully inserted in COST_CENTER_DETAILS and SERVICES_DETAILS for SubBudgetId:"
				+ loCBGridBean.getSubBudgetID() + "and BudgetID:" + loCBGridBean.getContractBudgetID());
	}
	// End: Added in R7 for cost-Center
	
	// Start : Added in R7 For Cost-Center
	/**
	 * This method is added in R7 for Cost Center.It is used to fetch Service
	 * Detailed grid data.
	 * @param aoMybatisSession
	 * @param aoCBGridBeanObj
	 * @return loServiceData - List of CBServicesBean Object
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<CBServicesBean> fetchServiceData(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into fetchServiceData for BudgetId:" + aoCBGridBeanObj.getContractBudgetID());
	
		List<CBServicesBean> loServiceData = null;
		try
		{
			loServiceData = (List<CBServicesBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.FETCH_SERVICE_DATA,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("subBudget-ID : ", aoCBGridBeanObj.getSubBudgetID());
			LOG_OBJECT.Error("Exception occured  at fetching ContractBudgetService: fetchServiceData() ", loAppEx);
			setMoState("ContractBudgetService: fetchServiceData() failed to fetch at subBudget-ID:"
					+ aoCBGridBeanObj.getSubBudgetID() + " \n");
			throw loAppEx;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieving in ContractBudgetService ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchServiceData method"
					+ " - failed to retrieve for subBudget-ID" + aoCBGridBeanObj.getSubBudgetID() + " \n");
			throw new ApplicationException("Exception occured while fetch in ContractBudgetService ", loAppEx);
		}
		LOG_OBJECT.Debug("Exited from fetchServiceData for BudgetId:" + aoCBGridBeanObj.getContractBudgetID());
		return loServiceData;
	}

	/**
	 * This method is added in R7 for Cost Center.It is used to update Service
	 * Detailed grid data.
	 * @param aoCBServices
	 * @param aoMybatisSession
	 * @return lbUpdateStatus - Boolean
	 * @throws ApplicationException
	 */
	public boolean editServicesDetails(CBServicesBean aoCBServices, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		try
		{
			LOG_OBJECT.Debug("Entered into editServicesDetails for ServicesDetailId:"
					+ aoCBServices.getServicesDetailId());
			DAOUtil.masterDAO(aoMybatisSession, aoCBServices, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSR5Constants.UPDATE_SERVICES_LIST, HHSR5Constants.COM_NYC_HHS_MODEL_CB_SERVICESBEAN);
			lbUpdateStatus = true;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("error occured while updating Services for budget type id "
					+ aoCBServices.getContractBudgetID());
			loExp.addContextData("Exception occured while updating Services ", loExp);
			LOG_OBJECT.Error("error occured while updating Services ", loExp);
			throw loExp;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieving in ContractBudgetService ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: editServicesDetails method");
			throw new ApplicationException("Exception occured while editing in ContractBudgetService ", loAppEx);
		}
		LOG_OBJECT.Debug("Exited from editServicesDetails for ServicesDetailId:" + aoCBServices.getServicesDetailId());
		return lbUpdateStatus;
	}

	/**
	 * This method is added in R7 for Cost Center.It is used to fetch Cost
	 * Center Detailed grid data.
	 * @param aoMybatisSession
	 * @param aoCBGridBeanObj
	 * @return loCostData - List
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<CBServicesBean> fetchCostCenter(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into fetchCostCenter for SubBudgetId:" + aoCBGridBeanObj.getSubBudgetID());
		List<CBServicesBean> loCostData = null;
		try
		{
			loCostData = (List<CBServicesBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.FETCH_COST_DATA,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("subBudget-ID : ", aoCBGridBeanObj.getSubBudgetID());
			LOG_OBJECT.Error("Exception occured  at fetching ContractBudgetService: fetchCostCenter() ", loAppEx);
			setMoState("ContractBudgetService: fetchCostCenter() failed to fetch at subBudget-ID:"
					+ aoCBGridBeanObj.getSubBudgetID() + " \n");
			throw loAppEx;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieving in ContractBudgetService ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchCostCenter method"
					+ " - failed to retrieve for subBudget-ID" + aoCBGridBeanObj.getSubBudgetID() + " \n");
			throw new ApplicationException("Exception occured while fetch in ContractBudgetService ", loAppEx);
		}
		LOG_OBJECT.Debug("Exited from fetchCostCenter for SubBudgetId:" + aoCBGridBeanObj.getSubBudgetID());
		return loCostData;
	}

	/**
	 * This method is added in R7 for Cost Center.This method is used to update
	 * Cost center Detailed grid data.
	 * @param aoCBServices
	 * @param aoMybatisSession
	 * @return lbUpdateStatus
	 * @throws ApplicationException
	 */
	public boolean editCostCenterDetails(CBServicesBean aoCBServices, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into editCostCenterDetails for SubBudgetId:" + aoCBServices.getSubBudgetID());
		boolean lbUpdateStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoCBServices, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					HHSR5Constants.UPDATE_COST_CENTER_LIST, HHSR5Constants.COM_NYC_HHS_MODEL_CB_SERVICESBEAN);
			lbUpdateStatus = true;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("error occured while updating Cost Center for budget type id "
					+ aoCBServices.getContractBudgetID());
			loExp.addContextData("Exception occured while updating Cost Center ", loExp);
			LOG_OBJECT.Error("error occured while updating Cost Center ", loExp);
			throw loExp;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while editing in ContractBudgetService ", loAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: editCostCenterDetails method");
			throw new ApplicationException("Exception occured while edit in ContractBudgetService ", loAppEx);
		}
		LOG_OBJECT.Debug("Exited from editCostCenterDetails for SubBudgetId:" + aoCBServices.getSubBudgetID());
		return lbUpdateStatus;
	}

	/**
	 * This method is added in R7 for Cost Center.This method is used to
	 * validate services amount for given Budget Id.
	 * @param aoMyBatisSession
	 * @param aoBudgetId
	 * @return loServicemap- HashMap
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap validateServicesAmount(SqlSession aoMyBatisSession, String aoBudgetId) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into validateServicesAmount for BudgetId:" + aoBudgetId);
		HashMap loServicemap = new HashMap();
		loServicemap.put(HHSConstants.SUCCESS, HHSConstants.STRING_TRUE);
		HashMap aoHashmap = new HashMap<String, String>();
		List<CBGridBean> loSubBudgetList = null;
		Integer loFlag = HHSConstants.INT_ZERO;
		BigDecimal loBugetAmount = BigDecimal.ZERO;
		BigDecimal loUpdatedAmount = BigDecimal.ZERO;
		try
		{
			loFlag = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoBudgetId,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.FETCH_FLAG,
					HHSConstants.JAVA_LANG_STRING);
			if (null != loFlag && loFlag == 2)
			{
				String lsBudgetType = (String) DAOUtil.masterDAO(aoMyBatisSession, aoBudgetId,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_BUDGET_TYPE,
						HHSConstants.JAVA_LANG_STRING);
				aoHashmap.put(HHSConstants.BUDGET_TYPE_ID, lsBudgetType);
				aoHashmap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoBudgetId);
				loSubBudgetList = (List<CBGridBean>) DAOUtil.masterDAO(aoMyBatisSession, aoHashmap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_SUB_BUDGET_SUMMARY,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				for (int liNumber = 0; liNumber < loSubBudgetList.size(); liNumber++)
				{
					aoHashmap.put(HHSConstants.SUBBUDGET_ID, loSubBudgetList.get(liNumber).getSubBudgetID());
					loUpdatedAmount = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, aoHashmap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSR5Constants.FETCH_COST_CENTER_AMOUNT_TOTAL, HHSConstants.JAVA_UTIL_HASH_MAP);
					loBugetAmount = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, loSubBudgetList.get(liNumber)
							.getSubBudgetID(), HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSConstants.CBM_MAPPER_FETCH_SUB_BUDGET_AMOUNT, HHSConstants.JAVA_LANG_STRING);
					if (loUpdatedAmount.compareTo(loBugetAmount) != HHSConstants.INT_ZERO)
					{
						loServicemap.put(HHSConstants.SUCCESS, HHSConstants.STRING_FALSE);
						if(HHSConstants.ONE.equalsIgnoreCase(lsBudgetType))
						{
							loServicemap.put(HHSConstants.CBL_MESSAGE,
									HHSR5Constants.MSG_KEY_COSTCENTER_AMENDMENT_MORE_THAN_CITYFUNDED_AMEND_AMOUNT);
						}
						else
						{
							loServicemap.put(HHSConstants.CBL_MESSAGE,
									HHSR5Constants.MSG_KEY_COST_CENTER_CONTRACT_SUBMIT_ERROR);
						}
						break;
					}
					loUpdatedAmount = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, aoHashmap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSR5Constants.FETCH_SERVICES_MOD_TOTAL_AMT, HHSConstants.JAVA_UTIL_HASH_MAP);
					loBugetAmount = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, aoHashmap,
							HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
							HHSR5Constants.FETCH_PI_MOD_TOTAL_AMT, HHSConstants.JAVA_UTIL_HASH_MAP);
					if (loUpdatedAmount.compareTo(loBugetAmount) != HHSConstants.INT_ZERO)
					{
						loServicemap.put(HHSConstants.SUCCESS, HHSConstants.STRING_FALSE);
						if (HHSConstants.ONE.equalsIgnoreCase(lsBudgetType))
						{
							loServicemap.put(HHSConstants.CBL_MESSAGE,
									HHSR5Constants.MSG_KEY_SERVICE_AMENDMENT_MORE_THAN_BUDGET_PI_AMEND_AMOUNT);
						}
						else
						{
							loServicemap.put(HHSConstants.CBL_MESSAGE,
									HHSR5Constants.MSG_KEY_SERVICES_CONTRACT_SUBMIT_ERROR);
						}
						break;
					}
				}
			}
		}
		// Application Exception handled here
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ContractBudgetService:" + " validateServicesAmount method:: ",
					aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: validateServicesAmount "
					+ "method - failed Exception occured while validating Service Amount\n");
			throw aoAppEx;
		}

		// Exception handled here - May occur for any unpredictable situation
		catch (Exception aoEx)
		{
			setMoState("Transaction Failed:: ContractBudgetService: validateServicesAmount"
					+ " method - failed to validate record " + " \n");
			throw new ApplicationException("Exception occured while validating Service Amount  :"
					+ " ContractBudgetService method", aoEx);
		}
		LOG_OBJECT.Debug("Exited from validateServicesAmount for BudgetId:" + aoBudgetId.getBytes());
		return loServicemap;
	}
	
	// Start: Added in R7 for cost-Center
		/**
		 * This method is added in R7 for Cost Center Inserts in Cost_center_details
		 * and bc_Service_details if cost_center is enabled for contractID.This
		 * method is called on finish of Contract/Update/Amendment Configurations.
		 * @param aoTaskDetailsBean
		 * @param aoMyBatisSession
		 * @throws ApplicationException
		 */
		@SuppressWarnings(
		{ "unchecked" })
		public void insertServicesForOutYearAmendmentBudgets(TaskDetailsBean aoTaskDetailsBean, SqlSession aoMyBatisSession)
				throws ApplicationException
		{
			LOG_OBJECT.Info("Entered ContractBudgetService-->insertServicesForOutYearAmendmentBudgets method");
			List<CBGridBean> loCBGridBeanListForServices = null;
			try
			{
				Map<String, Object> loQueryMap = new HashMap<String, Object>();
				loQueryMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
				LOG_OBJECT.Info("Checking whether CostCenter is enabled");
				String lsCostOpted = (String) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.FETCH_COST_CENTER_OPTED,
						HHSConstants.JAVA_UTIL_MAP);
				LOG_OBJECT.Info("CostCenter is enabled " + lsCostOpted);
				if (HHSConstants.TWO.equals(lsCostOpted))
				{
					String lsContractId = aoTaskDetailsBean.getContractId();
					String lsFiscalYearId = aoTaskDetailsBean.getStartFiscalYear();
					loQueryMap.put(HHSConstants.CLC_FISCAL_YEAR_ID, lsFiscalYearId);

					if (null != lsContractId && !lsContractId.trim().isEmpty() && null != lsFiscalYearId
							&& !lsFiscalYearId.trim().isEmpty())
					{
						LOG_OBJECT.Info("Fetching All Active Amendment BudgetList and SubBudgetList for contractID " + lsContractId);
						// Services data during Out year Amendment Configuration
						loCBGridBeanListForServices = (List<CBGridBean>) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
								HHSR5Constants.FETCH_OUT_YEAR_SERVICES_SUB_BUDGET_ID_LIST, HHSConstants.JAVA_UTIL_MAP);
						LOG_OBJECT.Info("Fetched BudgetList and SubBudgetList for contractID " + lsContractId);
					}
					for (CBGridBean loCBGridBean : loCBGridBeanListForServices)
					{
						performServicesDefaultInsertion(aoTaskDetailsBean, aoMyBatisSession, loCBGridBean);
					}
				}
			}
			catch (ApplicationException loAppEx)
			{
				loAppEx.addContextData(HHSConstants.CB_GRID_BEAN_LIST, CommonUtil.convertBeanToString(loCBGridBeanListForServices));
				LOG_OBJECT.Error("App Exception occured in ContractBudgetService: insertServicesForOutYearAmendmentBudgets method:: ",
						loAppEx);
				setMoState("App Exception occured in ContractBudgetService: insertServicesForOutYearAmendmentBudgets method");
				throw loAppEx;
			}
			catch (Exception loEx)
			{
				// Context Data is added into Exception object and Log is updated
				ApplicationException loAppEx = new ApplicationException("Error occured in "
						+ "ContractBudgetService: insertServicesForOutYearAmendmentBudgets method:: ", loEx);
				loAppEx.addContextData(HHSConstants.CB_GRID_BEAN_LIST, CommonUtil.convertBeanToString(loCBGridBeanListForServices));
				LOG_OBJECT.Error("Exception occured in ContractBudgetService: insertServicesForOutYearAmendmentBudgets method:: ",
						loAppEx);
				setMoState("Exception occured in ContractBudgetService: insertServicesForOutYearAmendmentBudgets method");
				throw loAppEx;
			}
		}
		
		/**
		 * This method is added in R7 for Program Income.It is getting called on
		 * Finish of New FY Task. For Base Budgets & Amendment Budgets which are created during out-year
		 * amendment configuration, it is updating IS_OLD_PI flag in budget table to 0.
		 * @param aoTaskDetailsBean
		 * @param aoMyBatisSession
		 * @throws ApplicationException
		 */
		@SuppressWarnings(
		{ "unchecked" })
		public void updateIsOldPiFlagForNewFyFinish(TaskDetailsBean aoTaskDetailsBean, SqlSession aoMyBatisSession)
			throws ApplicationException
		{
			LOG_OBJECT.Info("Entered ContractBudgetService-->updateIsOldPiFlagForNewFyFinish method");
			try
			{
				Map<String, Object> loQueryMap = new HashMap<String, Object>();
				loQueryMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
				loQueryMap.put(HHSConstants.CLC_FISCAL_YEAR_ID, aoTaskDetailsBean.getStartFiscalYear());
				DAOUtil.masterDAO(aoMyBatisSession, loQueryMap, HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
						HHSR5Constants.UPDATE_IS_OLD_PI_FOR_NEW_FY_FINISH, HHSConstants.JAVA_UTIL_HASH_MAP);
				LOG_OBJECT.Info("Exited ContractBudgetService-->updateIsOldPiFlagForNewFyFinish method");
			}
			catch (ApplicationException loAppEx)
			{
				LOG_OBJECT.Error(
						"App Exception occured in ContractBudgetService: updateIsOldPiFlagForNewFyFinish method:: ",
						loAppEx);
				setMoState("App Exception occured in ContractBudgetService: updateIsOldPiFlagForNewFyFinish method");
				throw loAppEx;
			}
			catch (Exception loEx)
			{
				// Context Data is added into Exception object and Log is updated
				ApplicationException loAppEx = new ApplicationException("Error occured in "
						+ "ContractBudgetService: updateIsOldPiFlagForNewFyFinish method:: ", loEx);
				LOG_OBJECT.Error("Exception occured in ContractBudgetService: updateIsOldPiFlagForNewFyFinish method:: ",
						loAppEx);
				setMoState("Exception occured in ContractBudgetService: updateIsOldPiFlagForNewFyFinish method");
				throw loAppEx;
			}
		}

	// End: Added in R7 for Cost-Center
	
    // Start : R7 changes for program income
	/**
	 * This method is added in R7. This method will update the indirect PI rate
	 * Percentage on submission of budget.
	 * 
	 * <ul>
	 * <li>update program income Indirect Rate percentage.<br>
	 * 1. updateIndirectRatePercentage for Contract Budget<br>
	 * 2. updateIndirectRateModificaitonPercentage for Contract Modification or
	 * Contract Update or Contract Amendment</li>
	 * </ul>
	 * @param aoMybatisSession
	 * @param aoHMWFRequiredProps
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public Boolean updatePIIndirectRatePercentage(SqlSession aoMybatisSession, HashMap aoHMWFRequiredProps)
			throws ApplicationException
	{

		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoHMWFRequiredProps);
		LOG_OBJECT.Debug("Entered into updatePIIndirectRatePercentage with input: " + param);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		Boolean loReturnUpdatedStatus = false;
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		Integer loReturnStatus = 0;
		try
		{
			LOG_OBJECT.Info("get BudgetID from aoHMWFRequiredProps input: "+aoHMWFRequiredProps.get(HHSConstants.BUDGET_ID_WORKFLOW));
			String isOldPI = (String) DAOUtil.masterDAO(aoMybatisSession,
					aoHMWFRequiredProps.get(HHSConstants.BUDGET_ID_WORKFLOW),
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.IS_OLD_PI,
					HHSConstants.JAVA_LANG_STRING);
			Map<String, String> loInputMap = aoHMWFRequiredProps;
			
			if (HHSConstants.ZERO.equals(isOldPI))
			{
				String lsBudgetEntryTypeId = (String) DAOUtil.masterDAO(aoMybatisSession,
						aoHMWFRequiredProps.get(HHSConstants.BUDGET_ID_WORKFLOW),
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.FETCH_BUDGET_TYPE_ID,
						HHSConstants.JAVA_LANG_STRING);
				if (null != lsBudgetEntryTypeId)
				{
					String lsQuery = HHSR5Constants.PI_INDIRECT_RATE_UPDATE_PERCENTAGE_QUERY_MAP
							.get(lsBudgetEntryTypeId);

					List<CBGridBean> loCBGridBeanList = (List<CBGridBean>) DAOUtil.masterDAO(aoMybatisSession,
							loInputMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
							HHSR5Constants.FETCH_SUB_BUDGET_DETAIL_lIST, HHSConstants.JAVA_UTIL_MAP);
					
					LOG_OBJECT.Debug("loCBGridBeanList :: "+loCBGridBeanList);
					
					// Start QC 9202 R 7.10.0 - get fiscal year for subbudget or budget
					String lsFiscalYearId = (String) DAOUtil.masterDAO(aoMybatisSession, aoHMWFRequiredProps.get(HHSConstants.BUDGET_ID_WORKFLOW),
										HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.GET_FISCAL_YEAR_ID_FROM_BUDGET,
										HHSConstants.JAVA_LANG_STRING);
					LOG_OBJECT.Debug("Fiscal Year Id from Budget :: "+lsFiscalYearId);
					// End QC 9202 R 7.10.0 - get fiscal year for subbudget or budget
					
					for (CBGridBean loCBGridBean : loCBGridBeanList)
					{
						// Start QC 9202 R 7.10.0 - get fiscal year for  budget
						aoCBGridBeanObj.setFiscalYearID(lsFiscalYearId);
						// End QC 9202 R 7.10.0 - get fiscal year for  budget
						
						aoCBGridBeanObj.setModifyByProvider(aoHMWFRequiredProps.get(HHSConstants.SUBMITTED_BY)
								.toString());
						aoCBGridBeanObj.setSubBudgetID(loCBGridBean.getSubBudgetID());
						aoCBGridBeanObj.setParentSubBudgetId(loCBGridBean.getParentSubBudgetId());
						loReturnStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, lsQuery,
								HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
					}
					if (loReturnStatus > 0)
					{
						loReturnUpdatedStatus = true;
					}
				}
			}
		}
		catch (ApplicationException loAppExp)
		{
			setMoState("Error occured while fetching Program Income Indirect Rate percentage for business type id ");
			throw loAppExp;
		}
		catch (Exception loExp)
		{
			throw new ApplicationException("Exception occured while fetching Program Income Indirect Rate percentage ",
					loExp);
		}
		return loReturnUpdatedStatus;
	}
	// End: R7 changes for program income
	
	/**
	 * Fix multi-tab Browsing QC8691 R7.1.0
	 * Fetch BudgetId from SUB_BUDGET, corresponding to given sub_budget_id
	 * @param aoMyBatisSession
	 * @param subBudgetId
	 * @return
	 * @throws ApplicationException
	 */
	public String getBudgetIdFromSubBudget(SqlSession aoMyBatisSession, String subBudgetId) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into getBudgetIdFromSubBudget for SubBudgetId: " + subBudgetId);
	
		try
		{
			String budgetId = (String) DAOUtil.masterDAO(aoMyBatisSession, subBudgetId,
					HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.GET_BUDGET_ID_FROM_SUB_BUDGET,
					HHSConstants.JAVA_LANG_STRING);
			return budgetId;
		}
		// Application Exception handled here
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ContractBudgetService:" + " getBudgetIdFromSubBudget method:: ",
					aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: getBudgetIdFromSubBudget "
					+ "method - failed while fetching BudgetId from table Sub_Budget\n");
			throw aoAppEx;
		}
		
		// Exception handled here - May occur for any unpredictable situation
		catch (Exception aoEx)
		{
			setMoState("Transaction Failed:: ContractBudgetService: getBudgetIdFromSubBudget"
					+ " method - failed while fetching BudgetId from table Sub_Budget " + " \n");
			throw new ApplicationException("Exception occured while fetching BudgetId from table Sub_Budget :"
					+ " ContractBudgetService.getBudgetIdFromSubBudget", aoEx);
		}

	}
	// End: QC8691 R7.1.0 Tab Browsing
	
	
    // [Start] R7.4.0 QC9008 Add abilities to delete budget update task	
    /**
     * Pull summary info for deleting budget update task
     * 
     * @param aoMyBatisSession
     * @param sBudgetId
     * @param asContractId
     * @param asBudgetTypeId
     * 
     * @return List<BudgetList>
     * @throws ApplicationException
     */
    @SuppressWarnings("unchecked")
    public List<BudgetList> pullUpBudgetUpdateSummery(SqlSession aoMyBatisSession, String sBudgetId, String asContractId, String asBudgetTypeId ) throws ApplicationException  
    {
        
        HashMap <String, String> loParamMap = new HashMap<String, String>();
        loParamMap.put(HHSConstants.BUDGET_ID_KEY, sBudgetId);
        loParamMap.put(HHSConstants.CONTRACT_ID_KEY, asContractId);
        loParamMap.put(HHSConstants.AS_BUDGET_TYPE_ID, asBudgetTypeId);

        List<BudgetList> loBudgetList = null;
        try {
            loBudgetList = (List<BudgetList>) DAOUtil.masterDAO(aoMyBatisSession, loParamMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, 
                     HHSConstants.FETCH_BUDGET_UPDATE_SUMMARY, HHSConstants.JAVA_UTIL_HASH_MAP);
        } catch (ApplicationException aoAppEx) {
            LOG_OBJECT.Error("Exception occured in ContractBudgetService:" + " pullUpBudgetUpdateSummery method:: ",
                    aoAppEx);
            setMoState("Transaction Failed:: ContractBudgetService: getOrgFromBusinessApp "
                    + "method - failed while fetching BudgetUpdate task data \n");
            throw aoAppEx;
        }
        // Exception handled here - May occur for any unpredictable situation
        catch (Exception aoEx)
        {
            setMoState("Transaction Failed:: ContractBudgetService: pullUpBudgetUpdateSummery"
                    + " method - failed while fetching BudgetUpdate task data  " + " \n");
            throw new ApplicationException("Exception occured while fetching BudgetUpdate task data :"
                    + " ContractBudgetService.pullUpBudgetUpdateSummery", aoEx);
        }

        return  loBudgetList;
    }

    /**
     * delete all data of budget update task in HACW
     * 
     * @param aoMyBatisSession
     * @param sBudgetId
     * @param asContractId
     * 
     * @return
     * @throws ApplicationException
     */
    public boolean deleteBudgetUpdateTask(SqlSession aoMyBatisSession,  String asContractId ) throws ApplicationException
    {
        HashMap <String, String> loParamMap = new HashMap<String, String>();

        loParamMap.put(HHSConstants.CONTRACT_ID_KEY, asContractId);

        List<BudgetList> loBudgetList = null;
        
        try {
            DAOUtil.masterDAO(aoMyBatisSession, loParamMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, 
                     HHSConstants.DELETE_BUDGET_UPDATE_TASK, HHSConstants.JAVA_UTIL_HASH_MAP);
        } catch (ApplicationException aoAppEx) {
            LOG_OBJECT.Error("Exception occured in ContractBudgetService:" + " deleteBudgetUpdateData method:: ", aoAppEx);
            setMoState("Transaction Failed:: ContractBudgetService: deleteBudgetUpdateData "  
            + "method - failed while fetching BudgetUpdate task data \n");

            throw aoAppEx;
         }
        // Exception handled here - May occur for any unpredictable situation
        catch (Exception aoEx)
        {
            setMoState("Transaction Failed:: ContractBudgetService: deleteBudgetUpdateData"
                    + " method - failed while fetching deleteBudgetUpdateData  " + " \n");
            throw new ApplicationException("Exception occured while deleteBudgetUpdateData :"
                    + " ContractBudgetService.deleteBudgetUpdateData", aoEx);
        }

        return true;
    }
    // [Start] R7.4.0 QC9008 Add abilities to delete budget update task

 // Start QC 9202 R 7.10.0 - get fiscal year for subbudget or budget
    /**
     * get FyscalYearID 
     * 
     * @param aoMyBatisSession
     * @param aoCBGridBeanObj
     * 
     * @return lsFiscalYearID
     * @throws ApplicationException
     */
    public String getFiscalYearIDfromDB(SqlSession aoMyBatisSession,  CBGridBean aoCBGridBeanObj ) throws ApplicationException
    {
		String lsFiscalYearId = null;
		
	    if (null!=aoCBGridBeanObj.getContractBudgetID() && !aoCBGridBeanObj.getContractBudgetID().isEmpty())
		{
			lsFiscalYearId = (String) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj.getContractBudgetID(),
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.GET_FISCAL_YEAR_ID_FROM_BUDGET,
					HHSConstants.JAVA_LANG_STRING);
			LOG_OBJECT.Debug("Fiscal Year Id from Budget :: "+lsFiscalYearId);
			
		}
		else if (null!=aoCBGridBeanObj.getSubBudgetID() && !aoCBGridBeanObj.getSubBudgetID().isEmpty())
		{
			lsFiscalYearId = (String) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBeanObj.getSubBudgetID(),
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.GET_FISCAL_YEAR_ID_FROM_SUB_BUDGET,
					HHSConstants.JAVA_LANG_STRING);
			LOG_OBJECT.Debug("Fiscal Year Id from Sub_Budget :: "+lsFiscalYearId);
			
		}
	    
		return lsFiscalYearId;
    }
	// End QC 9202 R 7.10.0 - get fiscal year for subbudget	or budget
 

    
 // Start QC 9438 R 8.2.0 - get fiscal year from budget
    /**
     * get FyscalYearID 
     * 
     * @param aoMyBatisSession
     * @param aoCBGridBeanObj
     * 
     * @return lsFiscalYearID
     * @throws ApplicationException
     */
    public String getFiscalYearIdFromBudget(SqlSession aoMyBatisSession,  String  contractBudgetID) throws ApplicationException
    {
		String lsFiscalYearId = null;
		 try {    
			lsFiscalYearId = (String) DAOUtil.masterDAO(aoMyBatisSession, contractBudgetID,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.GET_FISCAL_YEAR_ID_FROM_BUDGET,
					HHSConstants.JAVA_LANG_STRING);
			LOG_OBJECT.Debug("Fiscal Year Id from Budget :: "+lsFiscalYearId);
		    
		return lsFiscalYearId;
    }
		 catch (Exception aoEx)
			{
				setMoState("Transaction Failed:: ContractBudgetService: getFiscalYearIdFromBudget"
						+ " method - failed to retrieve FY from Budget " + " \n");
				LOG_OBJECT.Debug("Exited from getFiscalYearIdFromBudget for BudgetId:" + contractBudgetID);
				throw new ApplicationException("Exception occured while retrieving FY from Budget  :"
						+ " ContractBudgetService method", aoEx);
			}
			
	// Start QC 9438 R 8.2.0 - get fiscal year from budget
    }		
}
