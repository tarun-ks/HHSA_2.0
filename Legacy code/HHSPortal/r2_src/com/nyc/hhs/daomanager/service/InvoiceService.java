package com.nyc.hhs.daomanager.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.AdvanceSummaryBean;
import com.nyc.hhs.model.AssignmentsSummaryBean;
import com.nyc.hhs.model.AutoCompleteBean;
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
import com.nyc.hhs.model.ContractBudgetSummary;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.ContractedServicesBean;
import com.nyc.hhs.model.InvoiceList;
import com.nyc.hhs.model.PersonnelServiceBudget;
import com.nyc.hhs.model.PersonnelServicesData;
import com.nyc.hhs.model.RateBean;
import com.nyc.hhs.model.Rent;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.model.UnallocatedFunds;
import com.nyc.hhs.service.filenetmanager.p8services.P8ProcessServiceForSolicitationFinancials;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * <p>
 * This service class will be used to fetch all the data for Invoice screens.
 * All render and action methods for Budget Summary, Utilities, OTPS, Rent etc
 * screens will use this service to fetch/insert/update data from/to database.
 * </p>
 * <li>This service is updated in R7</li>
 */

public class InvoiceService extends ServiceState
{

	/**
	 * LogInfo object for Logging
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(InvoiceService.class);

	/**
	 * <p>
	 * This method is used for fetching values in Operations and Support grid
	 * for a particular sub-budget. <br/>
	 * 
	 * </p>
	 * <li>Query Used: fetchInvoiceOperationSupport</li> <li>Query Used:
	 * fetchInvoiceOperationSupportDetails</li>
	 * 
	 * @param aoCBGridBean - CBGridBean object
	 * @param aoMyBatisSession aoMyBatisSession
	 * @return List<CBOperationSupportBean> - returns list of bean of type
	 *         <CBOperationSupportBean>
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<CBOperationSupportBean> fetchOperationAndSupportDetails(CBGridBean aoCBGridBean,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		List<CBOperationSupportBean> loCBOperationSupportBean = null;
		List<CBOperationSupportBean> loInvoicedAmountBean = null;
		try
		{

			// get list of operation_and_support entries
			loCBOperationSupportBean = (List<CBOperationSupportBean>) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_INVOICE_OPERATION_SUPPORT,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			// get list of remaininn amounts under each entry in
			// operation_support table (budgetamount-totalinvoicedamount)
			calculateInvoiceRemainingAmount(loCBOperationSupportBean);

			// get current invoice amount for operation support to display in
			// grid
			loInvoicedAmountBean = (List<CBOperationSupportBean>) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_INVOICE_OPERATION_SUPPORT_DETAILS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			mergeInvoiceDetails(loCBOperationSupportBean, loInvoicedAmountBean);

			updateOpSupportBeanIds(loCBOperationSupportBean);

		}
		// Handling ApplicationException while fetching Operation Support
		// details
		catch (ApplicationException aoAppExp)
		{
			aoAppExp.addContextData("aoCBGridBean", CommonUtil.convertBeanToString(aoCBGridBean));
			LOG_OBJECT.Error("Application Exception Occured while getting the Vendor list from the database", aoAppExp);
			setMoState("Error while fetching Invoice Operation Support Amount for Invoice id:"
					+ aoCBGridBean.getInvoiceId() + " SubBudgetId = " + aoCBGridBean.getSubBudgetID());
			throw aoAppExp;
		}

		return loCBOperationSupportBean;
	}

	/**
	 * <p>
	 * This method is used to Calculate RemainingAmount in List of Operation
	 * SupportBeans
	 * </p>
	 * 
	 * @param aoResultBeanList :List of CBOperationSupportBean having
	 *            OperationSupport details
	 */

	private void calculateInvoiceRemainingAmount(List<CBOperationSupportBean> aoResultBeanList)
	{
		BigDecimal ldRemainingAmount = BigDecimal.ZERO;
		// Get Invoiced Amount and put in a Map with respective
		// operationSupportID as Key

		for (CBOperationSupportBean loResultIterate : aoResultBeanList)
		{

			ldRemainingAmount = new BigDecimal(loResultIterate.getFyBudget()).subtract(new BigDecimal(loResultIterate
					.getYtdInvoicedAmt()));
			loResultIterate.setRemainingAmt(String.valueOf(ldRemainingAmount));
		}

	}

	/**
	 * <p>
	 * This method is used to concatenate invoiceDetails in List of Operation
	 * SupportBeans
	 * </p>
	 * 
	 * @param aoResultBeanList :List of CBOperationSupportBean having
	 *            OperationSupport details
	 * @param aoInputBeanList :List of CBOperationSupportBean having Invoice
	 *            details
	 */
	private void mergeInvoiceDetails(List<CBOperationSupportBean> aoResultBeanList,
			List<CBOperationSupportBean> aoInputBeanList)
	{

		// Get Invoiced Amount and put in a Map with respective
		// operationSupportID as Key
		Map<String, CBOperationSupportBean> loInvoiceDetailMap = new HashMap<String, CBOperationSupportBean>();

		for (CBOperationSupportBean loInputIterate : aoInputBeanList)
		{
			loInvoiceDetailMap.put(loInputIterate.getId(), loInputIterate);
		}
		for (CBOperationSupportBean loResultIterate : aoResultBeanList)
		{
			if (loInvoiceDetailMap.containsKey(loResultIterate.getId()))
			{

				loResultIterate
						.setInvoiceDetailId(loInvoiceDetailMap.get(loResultIterate.getId()).getInvoiceDetailId());
				loResultIterate.setInvoicedAmt(loInvoiceDetailMap.get(loResultIterate.getId()).getInvoicedAmt());
			}
			else
			{
				loResultIterate.setInvoiceDetailId(HHSConstants.EMPTY_STRING);
				loResultIterate.setInvoicedAmt(String.valueOf(HHSConstants.INT_ZERO));
			}
		}

	}

	/**
	 * <p>
	 * This method concatenates InvoiceDetailId with OperationSupportId and
	 * UNDERSCORE Character and set in ID
	 * 
	 * @param aoResultBeanList - List<CBOperationSupportBean> containing
	 *            OperationSupport details
	 */
	private void updateOpSupportBeanIds(List<CBOperationSupportBean> aoResultBeanList)
	{
		StringBuffer loConcat = null;
		for (CBOperationSupportBean loIterateBean : aoResultBeanList)
		{
			if (!loIterateBean.getInvoiceDetailId().equals(HHSConstants.EMPTY_STRING))
			{
				loConcat = new StringBuffer(loIterateBean.getId());
				loConcat.append(HHSConstants.UNDERSCORE);
				loConcat.append(loIterateBean.getInvoiceDetailId());
				loIterateBean.setId(loConcat.toString());
			}

		}

	}

	/**
	 * <p>
	 * This method is used to validate OperationSupport Invoice Amount. If
	 * invoice amount is less than remaining amount then it will be
	 * updated/inserted else not <br/>
	 * 
	 * 
	 * </p>
	 * 
	 * @param aoCBOperationSupportBean - CBOperationSupportBean object
	 *            containing key fields <li>Query Used:
	 *            fetchBudgetAllocatedForAnOpSupport</li> <li>Query Used:
	 *            fetchInvAmountForAnOpSupportLineItem</li>
	 * @param aoMyBatisSession MyBatisSession
	 * @return Boolean - returns boolean status of edit/update query
	 * @throws ApplicationException - ApplicationException object
	 */

	public Boolean validateOpSupportInvoiceAmount(CBOperationSupportBean aoCBOperationSupportBean,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		Boolean loValid = false;
		boolean lbError = false;
		// String lsBudgetForAnOpSupport = null;
		String lsInvAmoutForOpSupport = null;

		CBOperationSupportBean loCopyOpSupportBean = new CBOperationSupportBean();

		try
		{
			// Copy properties in loCopyOpSupportBean
			if (aoCBOperationSupportBean.getId().indexOf(HHSConstants.UNDERSCORE) > HHSConstants.INT_ZERO)
			{
				String[] loIds = aoCBOperationSupportBean.getId().split(HHSConstants.UNDERSCORE);
				loCopyOpSupportBean.setId(loIds[HHSConstants.INT_ZERO]);
				loCopyOpSupportBean.setInvoiceDetailId(loIds[1]);
			}
			else
			{
				loCopyOpSupportBean.setId(aoCBOperationSupportBean.getId());
				loCopyOpSupportBean.setInvoiceDetailId(HHSConstants.EMPTY_STRING);
			}
			loCopyOpSupportBean.setSubBudgetID(aoCBOperationSupportBean.getSubBudgetID());
			loCopyOpSupportBean.setInvoiceAmountCurrent(aoCBOperationSupportBean.getInvoicedAmt());
			// Get BudgetAmount for An OPSupport
			/*
			 * lsBudgetForAnOpSupport = (String)
			 * DAOUtil.masterDAO(aoMyBatisSession, loCopyOpSupportBean.getId(),
			 * HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
			 * HHSConstants.FETCH_BUDGET_ALLOCATED_FOR_AN_OPSUPPORT,
			 * HHSConstants.JAVA_LANG_STRING);
			 */
			// get invoice amount for an operation support
			lsInvAmoutForOpSupport = (String) DAOUtil.masterDAO(aoMyBatisSession, loCopyOpSupportBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_INV_AMOUNT_FOR_AN_OPSUPPORT_LINEITEM,
					HHSConstants.MODEL_CB_OPERATION_SUPPORT_BEAN);

			// Start Enhancement 6535 Release 3.8.0
			aoCBOperationSupportBean.setInvoiceAmountCurrent(aoCBOperationSupportBean.getInvoicedAmt());
			aoCBOperationSupportBean.setTableAmountColumn(HHSConstants.GET_AMOUNT_DETAILS
					.get(HHSConstants.OPERATIONS_AND_SUPPORT));
			aoCBOperationSupportBean.setTableName(HHSConstants.OPERATIONS_AND_SUPPORT);
			aoCBOperationSupportBean.setTableId(HHSConstants.GET_ID_DETAILS.get(HHSConstants.OPERATIONS_AND_SUPPORT));
			aoCBOperationSupportBean.setEntryTypeId(HHSConstants.TWO);
			if (aoCBOperationSupportBean.getId().indexOf(HHSConstants.UNDERSCORE) == -1)
			{
				aoCBOperationSupportBean.setLineItemId(aoCBOperationSupportBean.getId());
			}
			else
			{
				aoCBOperationSupportBean.setLineItemId(aoCBOperationSupportBean.getId().substring(0,
						aoCBOperationSupportBean.getId().indexOf(HHSConstants.UNDERSCORE)));
			}

			BigDecimal loRemainingAmountPaymentDisbursed = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession,
					aoCBOperationSupportBean, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.FETCH_RATE_REMAINING_PAYMENT_DISBURSED, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			BigDecimal loFYBudgetLineItem = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, aoCBOperationSupportBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_FY_BUDGET_LINE_ITEM,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// End Enhancement 6535 Release 3.8.0
			BigDecimal loRemainingAmount = loFYBudgetLineItem.subtract(new BigDecimal(lsInvAmoutForOpSupport));
			// Start Enhancement 6535 Release 3.8.0
			loRemainingAmount = loRemainingAmount.subtract(loRemainingAmountPaymentDisbursed);
			// End Enhancement 6535 Release 3.8.0
			if (new BigDecimal(aoCBOperationSupportBean.getInvoicedAmt()).compareTo(new BigDecimal(0)) >= 0)
			{
				if (loRemainingAmount.subtract(new BigDecimal(aoCBOperationSupportBean.getInvoicedAmt())).compareTo(
						BigDecimal.ZERO) >= HHSConstants.INT_ZERO)
				{
					loValid = true;
				}
				else
				{

					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.MSG_KEY_INVOICE_AMOUNT_MORE_THAN_REMAINING_AMOUNT));

				}
			}
			else
			{
				// Start Enhancement 6535 Release 3.8.0
				// For invoicing less than zero scenarios
				if ((loFYBudgetLineItem.compareTo(loRemainingAmount.subtract(new BigDecimal(aoCBOperationSupportBean
						.getInvoicedAmt())))) >= 0)
				{
					// Insert new row of invoice details against this line item
					loValid = true;

				}
				else
				{
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.REMAINING_AMOUNT_LESS_THAN_FY_BUDGET));

				}
				// End Enhancement 6535 Release 3.8.0

			}
		}
		// handling Application Exception while validating OPeration Support
		// Inovice Amount
		catch (ApplicationException aoAppEx)
		{
			if (lbError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			else
			{

				LOG_OBJECT.Error("Exception occured in InvoiceService: editEquipmentDetails method:: ", aoAppEx);
				setMoState("Transaction Failed:: InvoiceService: editEquipmentDetails method - failed to update INVOICE_DETAIL"
						+ aoCBOperationSupportBean.getId() + " \n");
			}
			throw aoAppEx;
		}
		// handling Exception while validating OPeration Support Inovice Amount
		catch (Exception aoEx)
		{

			LOG_OBJECT.Error("Exception occured in InvoiceService: validateOpSupportInvoiceAmount method:: ", aoEx);
			setMoState("Transaction Failed:: InvoiceService: validateOpSupportInvoiceAmount method - failed to validate"
					+ aoCBOperationSupportBean.getId() + " \n");
			throw new ApplicationException("Error occured while updating Invoice Amount for Operation Support", aoEx);
		}

		return loValid;

	}

	/**
	 * <p>
	 * This method is used to update/insert OperationSupport Invoice Amount. If
	 * invoice detail for OpSupport item already exist, the invoice amount will
	 * be updated to newly entered Amount Else new Invoice Detail is created <br/>
	 * 
	 * The decision is taken on the basis of CBOperationSupportBean.id, which is
	 * either opSupportId or opSupportId_invoiceDetailId
	 * </p>
	 * <li>Query Used: editInvoiceOperationSupportDetails</li> <li>Query Used:
	 * getSeqForInvoiceDetail</li>
	 * @param aoValid - Flag for Validation result
	 * @param aoCBOperationSupportBean - CBOperationSupportBean object
	 *            containing key fields
	 * 
	 * @param aoMyBatisSession - SqlSession object
	 * @return Boolean - returns boolean status of edit/update query
	 * @throws ApplicationException - ApplicationException object
	 */
	public Boolean editOperationAndSupportDetails(Boolean aoValid, CBOperationSupportBean aoCBOperationSupportBean,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		boolean lbError = false;
		Boolean loEditStatus = true;
		Integer loRows = HHSConstants.INT_ZERO;
		try
		{
			if (aoValid)
			{
				if (aoCBOperationSupportBean.getId().indexOf(HHSConstants.UNDERSCORE) > HHSConstants.INT_ZERO)
				{
					String[] loIds = aoCBOperationSupportBean.getId().split(HHSConstants.UNDERSCORE);
					aoCBOperationSupportBean.setId(loIds[HHSConstants.INT_ZERO]);
					aoCBOperationSupportBean.setInvoiceDetailId(loIds[1]);

					loRows = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBOperationSupportBean,
							HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
							HHSConstants.EDIT_INVOICE_OPERATION_SUPPORT_DETAILS,
							HHSConstants.MODEL_CB_OPERATION_SUPPORT_BEAN);
				}
				else
				{
					// get sequence for nextID
					int liCurrentSeq = HHSConstants.INT_ZERO;
					liCurrentSeq = (Integer) DAOUtil.masterDAO(aoMyBatisSession, null,
							HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.GET_SEQ_FOR_INVOICE_DETAIL, null);

					aoCBOperationSupportBean.setInvoiceDetailId(String.valueOf(liCurrentSeq));
					loRows = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBOperationSupportBean,
							HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
							HHSConstants.INSERT_INVOICE_OPERATION_SUPPORT_DETAILS,
							HHSConstants.MODEL_CB_OPERATION_SUPPORT_BEAN);
				}

				if (loRows <= HHSConstants.INT_ZERO)
				{
					loEditStatus = false;
				}
			}
		}
		// handling Application Exception while editOperationAndSupportDetails
		catch (ApplicationException aoAppEx)
		{
			if (lbError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			else
			{

				setMoState("Transaction Failed:: InvoiceService: editOperationAndSupportDetails method - failed to update INVOICE_DETAIL"
						+ aoCBOperationSupportBean.getId() + " \n");
				LOG_OBJECT.Error("error occured while updating Invoice OperationAndSupport ", aoAppEx);
			}
			throw aoAppEx;
		}
		// handling Exception while editOperationAndSupportDetails
		catch (Exception aoEx)
		{

			LOG_OBJECT.Error("Exception occured in InvoiceService: editOperationAndSupportDetails method:: ", aoEx);
			setMoState("Transaction Failed:: InvoiceService: editOperationAndSupportDetails method - failed to update INVOICE_DETAIL"
					+ aoCBOperationSupportBean.getId() + " \n");
			throw new ApplicationException("Error occured while updating Invoice Amount for Operation Support", aoEx);
		}

		return loEditStatus;
	}

	/**
	 * <p>
	 * This method is used for fetching values in Equipment grid for a
	 * particular sub-budget <br/>
	 * <ul>
	 * <li>CBEquipmentBean is used to populate values in grid</li>
	 * <li>Provider is able to edit invoiced amount for equipment</li>
	 * <li>1.the number of Amendment Units and Amendment amount</li>
	 * <li>2.the number of Units and FY Budget amount</li>
	 * <li>3.the number of Modification Units and Modification amount</li>
	 * <li>3.the number of updated Units and updated amount</li>
	 * <li>Query Used: fetchEquipmentDetails</li>
	 * <li>Query Used: fetchEquipmentInvoiceDetails</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBGridBean - CBGridBean object containing key fields
	 * @param aoMyBatisSession - SqlSession object
	 * @return List<CBEquipmentBean> - returns list of bean of type
	 *         <CBEquipmentBean>
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<CBEquipmentBean> fetchEquipmentDetails(CBGridBean aoCBGridBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{

		List<CBEquipmentBean> loEquipmentBean = null;
		List<CBEquipmentBean> loEquipInvoiceBean = null;

		try
		{

			// get all equipments defined for a sub_budget
			loEquipmentBean = (List<CBEquipmentBean>) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_EQUIPMENT_DETAILS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// get list of remaining amounts under each entry in
			// operation_support table (budget amount-total invoiced amount)
			calculateEquipmentRemainingAmount(loEquipmentBean);

			// get equipment invoice details for current invoice corresponding
			// to a sub-budget
			loEquipInvoiceBean = (List<CBEquipmentBean>) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_EQUIPMENT_INVOICE_DETAILS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// merge invoice_amount to equipment_bean_list
			mergeEquipmentInvoiceDetails(loEquipmentBean, loEquipInvoiceBean);
			// concatenate opSupportId+"_"+invoiceDetailId
			updateEquipmentBeanIds(loEquipmentBean);

		}
		// handling Application Exception while fetching Equipment Details
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in InvoiceService: fetchEquipmentDetails method:: ", aoAppEx);
			setMoState("Error while fetching Equipments for sub-budget id:" + aoCBGridBean.getSubBudgetID()
					+ " InvoiceId : " + aoCBGridBean.getInvoiceId());
			throw aoAppEx;
		}
		// handling Exception while fetching Equipment Details
		catch (Exception aoEx)
		{

			LOG_OBJECT.Error("Exception occured in InvoiceService: fetchEquipmentDetails method:: ", aoEx);
			setMoState("Transaction Failed:: InvoiceService: fetchEquipmentDetails method - failed to fetch Equipment Detail for SubBudget"
					+ aoCBGridBean.getSubBudgetID() + " \n");
			throw new ApplicationException("Error occured while fetching equipment", aoEx);
		}
		return loEquipmentBean;
	}

	/**
	 * <p>
	 * This method concatenates InvoiceDetailId with equipmentId and UNDERSCORE
	 * Character and set in ID
	 * 
	 * @param aoResultBeanList :List of CBEquipmentBean
	 */
	private void updateEquipmentBeanIds(List<CBEquipmentBean> aoResultBeanList)
	{
		StringBuffer loConcat = null;
		for (CBEquipmentBean loIterateBean : aoResultBeanList)
		{
			if (!loIterateBean.getInvoiceDetailId().equals(HHSConstants.EMPTY_STRING))
			{
				loConcat = new StringBuffer(loIterateBean.getId());
				loConcat.append(HHSConstants.UNDERSCORE);
				loConcat.append(loIterateBean.getInvoiceDetailId());
				loIterateBean.setId(loConcat.toString());
			}
		}
	}

	/**
	 * <p>
	 * This method is used to concatenate invoiceDetails in List of Equipment
	 * </p>
	 * 
	 * @param aoResultBeanList List of CBOperationSupportBean having
	 *            OperationSupport details
	 * @param aoInputBeanList List of CBOperationSupportBean having Invoice
	 *            details
	 */
	private void mergeEquipmentInvoiceDetails(List<CBEquipmentBean> aoResultBeanList,
			List<CBEquipmentBean> aoInputBeanList)
	{

		// Get Invoiced Amount and put in a Map with respective
		// operationSupportID as Key
		Map<String, CBEquipmentBean> loInvoiceDetailMap = new HashMap<String, CBEquipmentBean>();

		for (CBEquipmentBean loInputIterate : aoInputBeanList)
		{
			loInvoiceDetailMap.put(loInputIterate.getId(), loInputIterate);
		}
		//

		for (CBEquipmentBean loResultIterate : aoResultBeanList)
		{
			if (loInvoiceDetailMap.containsKey(loResultIterate.getId()))
			{

				loResultIterate
						.setInvoiceDetailId(loInvoiceDetailMap.get(loResultIterate.getId()).getInvoiceDetailId());
				loResultIterate.setInvoicedAmt(loInvoiceDetailMap.get(loResultIterate.getId()).getInvoicedAmt());
			}
			else
			{
				loResultIterate.setInvoiceDetailId(HHSConstants.EMPTY_STRING);
				loResultIterate.setInvoicedAmt(String.valueOf(HHSConstants.INT_ZERO));
			}
		}

	}

	/**
	 * <p>
	 * This method is used to Calculate RemainingAmount in List of Operation
	 * SupportBeans
	 * </p>
	 * 
	 * @param aoResultBeanList List of CBEquipmentBean having OperationSupport
	 *            details
	 */

	private void calculateEquipmentRemainingAmount(List<CBEquipmentBean> aoResultBeanList)
	{

		BigDecimal ldRemainingAmount = BigDecimal.ZERO;
		// Get Invoiced Amount and put in a Map with respective
		// operationSupportID as Key

		for (CBEquipmentBean loResultIterate : aoResultBeanList)
		{

			ldRemainingAmount = new BigDecimal(loResultIterate.getFyBudget()).subtract(new BigDecimal(loResultIterate
					.getYtdInvoicedAmt()));
			loResultIterate.setRemainingAmt(String.valueOf(ldRemainingAmount));
		}

	}

	/**
	 * <p>
	 * This method is used to validate OperationSupport Invoice Amount. If
	 * invoice amount is less than remaining amount then it will be
	 * updated/inserted else not <br/>
	 * 
	 * 
	 * </p>
	 * <li>Query Used: fetchBudgetAllocatedForAnEquipment</li> <li>Query Used:
	 * fetchInvAmountForAnEquipment</li>
	 * 
	 * @param aoCBEquipmentBean - CBEquipmentBean object containing key fields
	 * @param aoMyBatisSession - SqlSession object
	 * @return Boolean - returns boolean status of edit/update query
	 * @throws ApplicationException - ApplicationException object
	 */

	public Boolean validateEquipmentInvoiceAmount(CBEquipmentBean aoCBEquipmentBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Boolean loValid = false;
		boolean lbError = false;
		// String lsBudgetForAnOpSupport = null;
		String lsInvAmoutForEquipment = null;

		CBEquipmentBean loCopyEquipmentBean = new CBEquipmentBean();

		try
		{
			// Copy properties in loCopyOpSupportBean
			if (aoCBEquipmentBean.getId().indexOf(HHSConstants.UNDERSCORE) > HHSConstants.INT_ZERO)
			{
				String[] loIds = aoCBEquipmentBean.getId().split(HHSConstants.UNDERSCORE);
				loCopyEquipmentBean.setId(loIds[HHSConstants.INT_ZERO]);
				loCopyEquipmentBean.setInvoiceDetailId(loIds[HHSConstants.INT_ONE]);
			}
			else
			{
				loCopyEquipmentBean.setId(aoCBEquipmentBean.getId());
				loCopyEquipmentBean.setInvoiceDetailId(HHSConstants.EMPTY_STRING);
			}
			loCopyEquipmentBean.setSubBudgetID(aoCBEquipmentBean.getSubBudgetID());
			loCopyEquipmentBean.setInvoiceAmountCurrent(aoCBEquipmentBean.getInvoicedAmt());
			// Get BudgetAmount for An OPSupport
			/*
			 * lsBudgetForAnOpSupport = (String)
			 * DAOUtil.masterDAO(aoMyBatisSession, loCopyEquipmentBean.getId(),
			 * HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
			 * HHSConstants.FETCH_BUDGET_ALLOCATED_FOR_AN_EQUIPMENT,
			 * HHSConstants.JAVA_LANG_STRING);
			 */

			// get invoice amount for an operation support
			lsInvAmoutForEquipment = (String) DAOUtil.masterDAO(aoMyBatisSession, loCopyEquipmentBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_INV_AMOUNT_FOR_AN_EQUIPMENT,
					HHSConstants.MODEL_CB_EQUIPMENT_BEAN);

			// Start Enhancement 6535 Release 3.8.0
			aoCBEquipmentBean.setInvoiceAmountCurrent(aoCBEquipmentBean.getInvoicedAmt());
			aoCBEquipmentBean.setTableAmountColumn(HHSConstants.GET_AMOUNT_DETAILS.get(HHSConstants.EQUIPMENT));
			aoCBEquipmentBean.setTableName(HHSConstants.EQUIPMENT);
			aoCBEquipmentBean.setTableId(HHSConstants.GET_ID_DETAILS.get(HHSConstants.EQUIPMENT));
			aoCBEquipmentBean.setEntryTypeId(HHSConstants.EQUI_ENTRY_TYPE);
			if (aoCBEquipmentBean.getId().indexOf(HHSConstants.UNDERSCORE) == -1)
			{
				aoCBEquipmentBean.setLineItemId(aoCBEquipmentBean.getId());
			}
			else
			{
				aoCBEquipmentBean.setLineItemId(aoCBEquipmentBean.getId().substring(0,
						aoCBEquipmentBean.getId().indexOf(HHSConstants.UNDERSCORE)));
			}
			BigDecimal loRemainingAmountPaymentDisbursed = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession,
					aoCBEquipmentBean, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.FETCH_RATE_REMAINING_PAYMENT_DISBURSED, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			BigDecimal loFYBudgetLineItem = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, aoCBEquipmentBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_FY_BUDGET_LINE_ITEM,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// End Enhancement 6535 Release 3.8.0
			BigDecimal loRemainingAmount = loFYBudgetLineItem.subtract(new BigDecimal(lsInvAmoutForEquipment));
			// Start Enhancement 6535 Release 3.8.0
			loRemainingAmount = loRemainingAmount.subtract(loRemainingAmountPaymentDisbursed);
			// End Enhancement 6535 Release 3.8.0
			if (new BigDecimal(aoCBEquipmentBean.getInvoicedAmt()).compareTo(new BigDecimal(0)) >= 0)
			{
				if (loRemainingAmount.subtract(new BigDecimal(aoCBEquipmentBean.getInvoicedAmt())).compareTo(
						BigDecimal.ZERO) >= HHSConstants.INT_ZERO)
				{
					loValid = true;
				}
				else
				{

					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.MSG_KEY_INVOICE_AMOUNT_MORE_THAN_REMAINING_AMOUNT));
				}
			}
			// for negative invoices
			else
			{
				// Start Enhancement 6535 Release 3.8.0
				// For invoicing less than zero scenarios
				if ((loFYBudgetLineItem.compareTo(loRemainingAmount.subtract(new BigDecimal(aoCBEquipmentBean
						.getInvoicedAmt())))) >= 0)
				{
					// Insert new row of invoice details against this line item
					loValid = true;

				}
				else
				{
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.REMAINING_AMOUNT_LESS_THAN_FY_BUDGET));
				}
				// End Enhancement 6535 Release 3.8.0
			}

		}
		// handling Application Exception while Validating Equipment Invoice
		// Amount
		catch (ApplicationException aoAppEx)
		{
			if (lbError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			else
			{

				LOG_OBJECT.Error("Exception occured in InvoiceService: editEquipmentDetails method:: ", aoAppEx);
				setMoState("Transaction Failed:: InvoiceService: editEquipmentDetails method - failed to update INVOICE_DETAIL"
						+ aoCBEquipmentBean.getId() + " \n");
			}
			throw aoAppEx;
		}
		// handling Exception while Validating Equipment Invoice Amount
		catch (Exception aoEx)
		{

			LOG_OBJECT.Error("Exception occured in InvoiceService: validateEquipmentInvoiceAmount method:: ", aoEx);
			setMoState("Transaction Failed:: InvoiceService: validateEquipmentInvoiceAmount method - failed to validate"
					+ aoCBEquipmentBean.getId() + " \n");
			throw new ApplicationException("Error occured while validating Invoice Amount for Equipment", aoEx);
		}

		return loValid;

	}

	/**
	 * <p>
	 * This method is used for editing/updating values in Equipment grid for a
	 * particular sub-budget <br/>
	 * 
	 * </p>
	 * <li>Query Used: editEquipmentDetails</li> <li>Query Used:
	 * getSeqForInvoiceDetail</li> <li>Query Used: insertEquipmentDetails</li>
	 * @param asValid - Flag for Validation result
	 * @param aoCBEquipmentBean - CBEquipmentBean object containing key fields
	 * @param aoMyBatisSession - SqlSession object
	 * @return Boolean - returns boolean status of edit/update query
	 * @throws ApplicationException - ApplicationException object
	 */
	public Boolean editEquipmentDetails(Boolean asValid, CBEquipmentBean aoCBEquipmentBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		boolean lbError = false;
		Boolean loEditStatus = true;
		Integer loRows = HHSConstants.INT_ZERO;
		try
		{
			if (asValid)
			{
				if (aoCBEquipmentBean.getId().indexOf(HHSConstants.UNDERSCORE) > HHSConstants.INT_ZERO)
				{
					String[] loIds = aoCBEquipmentBean.getId().split(HHSConstants.UNDERSCORE);
					aoCBEquipmentBean.setId(loIds[HHSConstants.INT_ZERO]);
					aoCBEquipmentBean.setInvoiceDetailId(loIds[1]);

					loRows = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBEquipmentBean,
							HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.EDIT_EQUIPMENT_DETAILS,
							HHSConstants.MODEL_CB_EQUIPMENT_BEAN);
				}
				else
				{
					// get sequence for nextID
					int liCurrentSeq = HHSConstants.INT_ZERO;
					liCurrentSeq = (Integer) DAOUtil.masterDAO(aoMyBatisSession, null,
							HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.GET_SEQ_FOR_INVOICE_DETAIL, null);

					aoCBEquipmentBean.setInvoiceDetailId(String.valueOf(liCurrentSeq));
					loRows = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBEquipmentBean,
							HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.INSERT_EQUIPMENT_DETAILS,
							HHSConstants.MODEL_CB_EQUIPMENT_BEAN);
				}

				if (loRows <= HHSConstants.INT_ZERO)
				{
					loEditStatus = false;
				}
			}
		}
		// handling Application Exception while updating Equipment Invoice
		// Amount
		catch (ApplicationException aoAppEx)
		{
			if (lbError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			else
			{

				LOG_OBJECT.Error("Exception occured in InvoiceService: editEquipmentDetails method:: ", aoAppEx);
				setMoState("Transaction Failed:: InvoiceService: editEquipmentDetails method - failed to update INVOICE_DETAIL"
						+ aoCBEquipmentBean.getId() + " \n");
			}
			throw aoAppEx;
		}
		// handling Application Exception while updating Equipment Invoice
		// Amount
		catch (Exception aoEx)
		{

			LOG_OBJECT.Error("Exception occured in InvoiceService: editEquipmentDetails method:: ", aoEx);
			setMoState("Transaction Failed:: InvoiceService: editEquipmentDetails method - failed to update INVOICE_DETAIL"
					+ aoCBEquipmentBean.getId() + " \n");
			throw new ApplicationException("Error occured while updating Invoice Amount for Operation Support", aoEx);
		}

		return loEditStatus;
	}

	/**
	 * This method retrieves Invoice Status for a given InvoiceID <li>Query
	 * Used: fetchInvoiceStatus</li>
	 * @param asInvoiceId :Invoice Id
	 * @param aoMyBatisSession : MyBatis Session
	 * @return String statusId for invoice
	 * @throws ApplicationException : Application Exception
	 */
	public String fetchInvoiceStatus(String asInvoiceId, SqlSession aoMyBatisSession) throws ApplicationException
	{
		String lsInvoiceStatus = null;
		try
		{
			lsInvoiceStatus = (String) DAOUtil.masterDAO(aoMyBatisSession, asInvoiceId,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_INVOICE_STATUS,
					HHSConstants.JAVA_LANG_STRING);
		}
		// handling Application Exception while fetching Invoice Status
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Application Exception occured in InvoiceService: fetchInvoiceStatus method:: ", aoAppEx);
			setMoState("Transaction Failed:: InvoiceService: fetchInvoiceStatus method - failed to fetch Invoiced Status"
					+ asInvoiceId + " \n");
			throw aoAppEx;
		}
		// handling Exception while fetching Invoice Status
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in InvoiceService: fetchInvoiceStatus method:: ", aoEx);
			setMoState("Transaction Failed:: InvoiceService: fetchInvoiceStatus method - failed to fetch Invoiced Status"
					+ asInvoiceId + " \n");
			throw new ApplicationException("Error occured while fetching Invoice status", aoEx);
		}
		return lsInvoiceStatus;

	}

	/**
	 * This method retrieves total invoiced amount under Operation Support for a
	 * given InvoiceId <li>Query Used: fetchInvoiceTotalForOTPS</li>
	 * @param asInvoiceId : Invoice Id
	 * @param aoMyBatisSession :My Batis Session
	 * @return String Sum of Invoice Amount for OperationSupport and Equipment
	 *         for input Invoice
	 * @throws ApplicationException Application Exception
	 */
	public String fetchInvoiceTotalForOTPS(CBGridBean aoCBGridBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		String lsInvoiceTotalAmounts = null;
		try
		{
			lsInvoiceTotalAmounts = (String) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_INVOICE_TOTAL_FOR_OTPS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		// handling Application Exception while fetching Invoice Amount
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Application Exception occured in InvoiceService: fetchInvoiceTotalForOTPS method:: ",
					aoAppEx);
			setMoState("Transaction Failed:: InvoiceService: fetchInvoiceTotalForOTPS method - failed to fetch Invoiced Amount"
					+ aoCBGridBean.getInvoiceId() + " \n");
			throw aoAppEx;
		}
		// handling Exception while fetching Invoice Amount
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in InvoiceService: fetchInvoiceTotalForOTPS method:: ", aoEx);
			setMoState("Transaction Failed:: InvoiceService: editEquipmentDetails method - failed to update EQUIPMENT"
					+ aoCBGridBean.getInvoiceId() + " \n");
			throw new ApplicationException("Error occured while updating Invoice Amount for Equipment", aoEx);
		}
		return lsInvoiceTotalAmounts;

	}

	/**
	 * This method retrieves total invoiced amount under Contracted Services for
	 * a given InvoiceId <li>Query Used: fetchInvoiceTotalForContractedServices</li>
	 * @param asInvoiceId : Invoice Id
	 * @param aoMyBatisSession :My Batis Session
	 * @return String Sum of Invoice Amount for Contracted Services for input
	 *         Invoice
	 * @throws ApplicationException Application Exception
	 */
	public String fetchInvoiceTotalForContractedServices(CBGridBean aoCBGridBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		String lsInvoiceTotalAmounts = null;
		try
		{
			lsInvoiceTotalAmounts = (String) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_INVOICE_TOTAL_FOR_CONTRACTED_SERVICES,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		// handling Application Exception while fetching Invoice Amount
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Application Exception occured in InvoiceService: fetchInvoiceTotalForOTPS method:: ",
					aoAppEx);
			setMoState("Transaction Failed:: InvoiceService: fetchInvoiceTotalForOTPS method - failed to fetch Invoiced Amount"
					+ aoCBGridBean + " \n");
			throw aoAppEx;
		}
		// handling Exception while fetching Invoice Amount
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in InvoiceService: fetchInvoiceTotalForOTPS method:: ", aoEx);
			setMoState("Transaction Failed:: InvoiceService: editEquipmentDetails method - failed to update EQUIPMENT"
					+ aoCBGridBean + " \n");
			throw new ApplicationException("Error occured while updating Invoice Amount for Equipment", aoEx);
		}
		return lsInvoiceTotalAmounts;

	}

	/**
	 * This method retrieves YTD total invoiced amount under Contracted Services
	 * for a given subBudgetId <li>Query Used: fetchCSYTDInvoiced</li>
	 * @param asSubBudgetID Sub-Budget Id
	 * @param aoMyBatisSession : MyBatis Session
	 * @return String ytd Total Invoiced Amount for input sub-budgetId
	 * @throws ApplicationException : Application Exception
	 */

	public String fetchContractedServicesYTDInvoiced(String asSubBudgetID, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		String lsYtdInvoicedAmount = null;
		try
		{
			lsYtdInvoicedAmount = (String) DAOUtil.masterDAO(aoMyBatisSession, asSubBudgetID,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_CS_YTD_INVOICED,
					HHSConstants.JAVA_LANG_STRING);
		}
		// handling Application Exception while fetching YTD Total Invoiced
		// Amount
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(
					"Application Exception occured in InvoiceService: fetchContractedServicesYTDInvoiced method:: ",
					aoAppEx);
			setMoState("Transaction Failed:: InvoiceService: fetchContractedServicesYTDInvoiced method - failed to fetch Invoiced Amount"
					+ asSubBudgetID + " \n");
			throw aoAppEx;
		}
		// handling Exception while fetching YTD Total Invoiced Amount
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in InvoiceService: fetchContractedServicesYTDInvoiced method:: ", aoEx);
			setMoState("Transaction Failed:: InvoiceService: fetchContractedServicesYTDInvoiced method - failed to fetch YTD Invoiced Amount"
					+ asSubBudgetID + " \n");
			throw new ApplicationException("Error occured while updating Invoice Amount for Contracted Services", aoEx);
		}
		return lsYtdInvoicedAmount;

	}

	/**
	 * This method retrieves YTD total invoiced amount under Operation Support
	 * and Equipment for a given subBudgetId <li>Query Used: fetchYTDInvoiced</li>
	 * @param asSubBudgetID Sub-Budget Id
	 * @param aoMyBatisSession : MyBatis Session
	 * @return String ytd Total Invoiced Amount for input sub-budgetId
	 * @throws ApplicationException : Application Exception
	 */

	public String fetchYTDInvoiced(String asSubBudgetID, SqlSession aoMyBatisSession) throws ApplicationException
	{
		String lsYtdInvoicedAmount = null;
		try
		{
			lsYtdInvoicedAmount = (String) DAOUtil.masterDAO(aoMyBatisSession, asSubBudgetID,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_YTD_INVOICED,
					HHSConstants.JAVA_LANG_STRING);
		}
		// handling Application Exception while fetching YTD Total Invoiced
		// Amount
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Application Exception occured in InvoiceService: fetchYTDInvoiced method:: ", aoAppEx);
			setMoState("Transaction Failed:: InvoiceService: fetchInvoiceTotalForOTPS method - failed to fetch Invoiced Amount"
					+ asSubBudgetID + " \n");
			throw aoAppEx;
		}
		// handling Exception while fetching YTD Total Invoiced Amount
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in InvoiceService: fetchYTDInvoiced method:: ", aoEx);
			setMoState("Transaction Failed:: InvoiceService: fetchYTDInvoiced method - failed to fetch YTD Invoiced Amount"
					+ asSubBudgetID + " \n");
			throw new ApplicationException("Error occured while updating Invoice Amount for Equipment", aoEx);
		}
		return lsYtdInvoicedAmount;

	}

	/**
	 * This method retrieves total invoiced amount under Personnel Services for
	 * a given InvoiceId <li>Query Used: fetchInvoiceTotalForPersonnelServices</li>
	 * <li>Query Used: fetchYTDTotalSalaryAndFringe</li>
	 * @param asInvoiceId : Invoice Id
	 * @param aoMyBatisSession :My Batis Session
	 * @return String Sum of Invoice Amount for PersonnelServices for input
	 *         Invoice
	 * @throws ApplicationException Application Exception
	 */
	public PersonnelServicesData fetchInvoiceTotalForPersonnelServices(CBGridBean aoCBGridBean,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		PersonnelServicesData loPersonnelServicesData = null;
		String lsTotalYtdInvoicedAmount, loTmpFringAmt = null, loTmpTotSalarAmt = null;
		try
		{
			loPersonnelServicesData = (PersonnelServicesData) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.INV_FETCH_INVOICETOTAL_PERSONNELSERVICES,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// Fetch YTD Total for SalaryAnd Fringe
			lsTotalYtdInvoicedAmount = (String) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean.getSubBudgetID(),
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.INV_FETCH_INVOICE_YTDTOTAL_PERSONNELSERVICES, HHSConstants.JAVA_LANG_STRING);
			// Set YTD InvoicedAmount in personnelServicesData
			loPersonnelServicesData.setTotalYtdInvoicedAmount(new BigDecimal(lsTotalYtdInvoicedAmount));
			loPersonnelServicesData.setTotalSalaryAndFringeAmount(loPersonnelServicesData.getTotalFringeAmount().add(
					loPersonnelServicesData.getTotalSalaryAmount()));
			// Set fringe percentage
			if ((loPersonnelServicesData.getTotalSalaryAmount().compareTo(BigDecimal.ZERO)) != HHSConstants.INT_ZERO)
			{
				// Start of changes for Defect :6240
				loTmpFringAmt = String.valueOf(loPersonnelServicesData.getTotalFringeAmount());
				loTmpTotSalarAmt = String.valueOf(loPersonnelServicesData.getTotalSalaryAmount());
				// End of changes for Defect :6240
				if (loTmpFringAmt == null || loTmpFringAmt.isEmpty())
				{
					loTmpFringAmt = HHSConstants.STRING_ZERO;
				}
				if (loTmpTotSalarAmt == null || loTmpTotSalarAmt.isEmpty())
				{
					loTmpTotSalarAmt = HHSConstants.STRING_ZERO;
				}
				loPersonnelServicesData.setFringePercentage((new BigDecimal(loTmpFringAmt).multiply(new BigDecimal(
						HHSConstants.HUNDRED), new MathContext(HHSConstants.INT_HUNDRED))).divide(new BigDecimal(
						loTmpTotSalarAmt, new MathContext(HHSConstants.INT_HUNDRED)), MathContext.DECIMAL128));
			}
			else
			{
				loPersonnelServicesData.setFringePercentage(BigDecimal.ZERO);
			}
		}
		// handling Application Exception while fetching Invoice Amount
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(
					"Application Exception occured in InvoiceService: fetchInvoiceTotalForPersonnelServices method:: ",
					aoAppEx);
			setMoState("Transaction Failed:: InvoiceService: fetchInvoiceTotalForPersonnelServices method - failed to fetch Invoiced Amount for Personnel Services"
					+ aoCBGridBean.getInvoiceId() + " \n");
			throw aoAppEx;
		}
		// handling Exception while fetching Invoice Amount
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in InvoiceService: fetchInvoiceTotalForPersonnelServices method:: ",
					aoEx);
			setMoState("Transaction Failed:: InvoiceService: fetchInvoiceTotalForPersonnelServices method - failed to fetch Invoiced Amount for Personnel Services"
					+ aoCBGridBean.getInvoiceId() + " \n");
			throw new ApplicationException("Error occured while fetching Invoice Amount for Personnel Services", aoEx);
		}
		return loPersonnelServicesData;

	}

	/**
	 * <p>
	 * This method fetch invoice unallocated funds details from DB
	 * <ul>
	 * <li>Call fetchInvoiceUnallocatedFunds query set sub budget id as where
	 * clause</li>
	 * <li>This method will check invoice details are there, if not than
	 * inserting the default row in database</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession Sql session object
	 * @param aoCBGridBeanObj CBGridBean
	 * @return List<UnallocatedFunds> loUnallocatedFunds
	 * @throws ApplicationException :Application Exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List fetchInvoiceUnallocatedFunds(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj)
			throws ApplicationException
	{

		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();

		List<UnallocatedFunds> loUnallocatedFunds = new ArrayList<UnallocatedFunds>();
		try
		{
			if (null != aoCBGridBeanObj.getSubBudgetID())
			{
				// Setting the variable for functionality related Bean from
				// generic bean
				loUnallocatedFundsBean.setSubBudgetId(Integer.parseInt(aoCBGridBeanObj.getSubBudgetID()));
				loUnallocatedFundsBean.setBudgetId(Integer.parseInt(aoCBGridBeanObj.getContractBudgetID()));
				loUnallocatedFundsBean.setCreatedUserId(aoCBGridBeanObj.getCreatedByUserId());

				// fetching the details for unallocated Funds.
				loUnallocatedFunds = (List<UnallocatedFunds>) DAOUtil.masterDAO(aoMybatisSession,
						loUnallocatedFundsBean, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
						HHSConstants.INVOICE_FETCH_UNALLOCATED_FUNDS, HHSConstants.UNALLOCATED_FUNDS_BEAN);
				// Start: QC 8694b R 7.9.0 apply add/delete functions for Unallocated Fund  
				/*
				// Inserting default object, when the user comes for first time
				if (loUnallocatedFunds.size() == HHSConstants.INT_ZERO)
				{
					DAOUtil.masterDAO(aoMybatisSession, loUnallocatedFundsBean,
							HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.INVOICE_INSERT_UNALLOCATED_FUNDS,
							HHSConstants.UNALLOCATED_FUNDS_BEAN);

					// adding a blank bean for display
					loUnallocatedFunds.add(loUnallocatedFundsBean);
				}
				*/
				// End: QC 8694b R 7.9.0 apply add/delete functions for Unallocated Fund  
			}
		}
		catch (ApplicationException aoAppExp)
		{
			// Any Exception from DAO class will be thrown as Application
			// Exception
			// which will be handles over here. It throws Application Exception
			// back
			setMoState("error occured while fetching invoice unallocated funds for business type id "
					+ loUnallocatedFundsBean.getBudgetId());
			aoAppExp.addContextData("Exception occured while fetching invoice unallocated funds ", aoAppExp);
			throw aoAppExp;
		}

		return loUnallocatedFunds;

	}

	/**
	 * <p>
	 * This method fetches Program Income List corresponding to a Contract Id Id
	 * and Budget Id
	 * <ul>
	 * <li>1. Fetches Program Income List - against ContractId and BudgetId</li>
	 * <li>2. Additionally fetches information from Budget and ProgramIncomeType
	 * as well</li>
	 * <li>Query Used: fetchProgramIncomeInvoice</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBGridBeanObj - CBGridBeanObj object containing key fields
	 * @param aoMybatisSession - SqlSession object
	 * @return loCBProgramIncomeBean - List of CBProgramIncomeBean
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<CBProgramIncomeBean> fetchProgramIncomeInvoice(CBGridBean aoCBGridBeanObj, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		List<CBProgramIncomeBean> loCBProgramIncomeBean = null;
		try
		{
			if (null == aoCBGridBeanObj)
			{
				// Throw exception if bean parameter found null
				throw new ApplicationException("Error occured while fetching the details for Program Income Invoices");
			}

			loCBProgramIncomeBean = (List<CBProgramIncomeBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_PROGRAM_INCOME_INVOICE,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

		}
		catch (ApplicationException aoAppExp)
		{
			// Any Exception from DAO class will be thrown as Application
			// Exception
			// which will be handles over here. It throws Application Exception
			// back
			if (null != aoCBGridBeanObj && null != aoCBGridBeanObj.getBudgetTypeId())
			{
				setMoState("error occured while fetching Program Income Details for budget type id "
						+ aoCBGridBeanObj.getBudgetTypeId());
			}
			aoAppExp.addContextData("Exception occured while fetching Program Income Invoice Details ", aoAppExp);
			LOG_OBJECT.Error("Error occurred while fetching Program Income Invoice Details ", aoAppExp);
			throw aoAppExp;
		}

		return loCBProgramIncomeBean;
	}

	/**
	 * <p>
	 * This method updates Program Income Invoice Details (Income column)
	 * corresponding to a ProgramIncomeId ( primary Key attribute of
	 * CBProgramsIncomeBean bean)
	 * <ul>
	 * <li>Updates the Income of a Program Income line item</li>
	 * 
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatiSession - SqlSessionId
	 * @param aoCBProgramIncomeBean - CBProgramsIncomeBean object
	 * @return Boolean lbUpdateStatus
	 * @throws ApplicationException :Application Exception
	 */
	public Boolean updateProgramIncomeInvoice(SqlSession aoMybatiSession, CBProgramIncomeBean aoCBProgramIncomeBean)
			throws ApplicationException
	{
		Boolean lbUpdateStatus = Boolean.FALSE;

		HashMap<String, Object> loQueryParam = new HashMap<String, Object>();
		try
		{
			// Throw exception if bean parameter found null
			if (null == aoCBProgramIncomeBean)
			{
				throw new ApplicationException("Error occured while fetching the details for Program Income Invoices");
			}

			// Prepare data to be sent for inserting the new row in invoice
			// detail
			loQueryParam.put(HHSConstants.ID, aoCBProgramIncomeBean.getId());
			loQueryParam.put(HHSConstants.INVOICE_ID, aoCBProgramIncomeBean.getInvoiceId());
			loQueryParam.put(HHSConstants.ENTRY_TYPE_ID, HHSConstants.STRING_ELEVEN);
			loQueryParam.put(HHSConstants.IS_LINE_ITEM_INVOICE_AMT, aoCBProgramIncomeBean.getIncome());
			loQueryParam.put(HHSConstants.MODIFIED_BY_AGENCY, aoCBProgramIncomeBean.getModifyByAgency());
			loQueryParam.put(HHSConstants.MODIFIED_BY_PROVIDER, aoCBProgramIncomeBean.getModifyByProvider());
			loQueryParam.put(HHSConstants.SUB_BUDGET_ID, aoCBProgramIncomeBean.getSubBudgetID());
			// Added in R7 for new program Income UI
			loQueryParam.put(HHSR5Constants.PI_ENTRY_TYPE_ID,aoCBProgramIncomeBean.getPIEntryTypeId());
			//R7 changes end
			// Insert new row of invoice details against this line item
			insertUpdateInvoiceDetail(aoMybatiSession, loQueryParam);

			lbUpdateStatus = Boolean.TRUE;
		}
		catch (ApplicationException aoAppExp)
		{
			// Any Exception from DAO class will be thrown as Application
			// Exception
			// which will be handles over here. It throws Application Exception
			// back
			if (null != aoCBProgramIncomeBean && null != aoCBProgramIncomeBean.getId())
			{
				setMoState("Transaction Failed:: InvoiceService: updateProgramIncomeInvoice method - failed to update INVOICE_DETAIL"
						+ aoCBProgramIncomeBean.getId() + " \n");
			}
			aoAppExp.addContextData("Exception occured while updating Program Income Invoices", aoAppExp);
			LOG_OBJECT.Error("Error occurred while fetching Program Income Invoice Details ", aoAppExp);
			throw aoAppExp;
		}
		return lbUpdateStatus;

	}

	/**
	 * <p>
	 * This method is used for Invoicing to fetch the values from Utility grid
	 * for a particular sub-budget.
	 * <ul>
	 * <li>fetchInvoicingUtilitiesDetails query is executed from InvoiceMapper
	 * that fetches the Utility information from the database for Contract
	 * Invoicing on load.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession Sql session object as input.
	 * @param aoCBGridBeanObj CBGridBean Bean as input.
	 * @return loCBUtilities loCBUtilities Bean as output with all Utility
	 *         related Information.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<CBUtilities> fetchInvoicingUtilities(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj)
			throws ApplicationException
	{

		List<CBUtilities> loCBUtilities = null;
		try
		{
			aoCBGridBeanObj.setEntryTypeId(HHSConstants.THREE);
			loCBUtilities = (List<CBUtilities>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.IS_FETCH_INVOICING_UTILITIES_DETAILS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppExp)
		{
			setMoState("error occured while fetching Utility Details for subBudget Id "
					+ aoCBGridBeanObj.getSubBudgetID());
			aoAppExp.addContextData("Exception occured while fetching Utility Details ", aoAppExp);
			throw aoAppExp;
		}
		return loCBUtilities;

	}

	/**
	 * <p>
	 * This method is used for Invoicing to edit the rows in Utility grid for a
	 * particular sub-budget.
	 * <ul>
	 * <li>1)Provider is able to Edit the Utility line for Invoicing items</li>
	 * <li>2)It Updates Invoice Amount entered in INVOICE_DETAIL table if there
	 * is an entry in the table for for a particular Invoice Id, Line Item Id,
	 * Entry Type and INSERTS a new row in the table if no entry exists.</li>
	 * <li>Query Used: fetchUtilitiesRemainingAmount</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatiSession Sql session object as input.
	 * @param aoCBUtilities aoCBUtilities Bean as input
	 * @return lbUpdateStatus lbUpdateStatus as output
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public boolean updateInvoicingUtilities(SqlSession aoMybatiSession, CBUtilities aoCBUtilities)
			throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		boolean lbError = false;
		BigDecimal loRemainingAmount = BigDecimal.ZERO;
		HashMap<String, Object> loQueryParam = new HashMap<String, Object>();
		loQueryParam.put(HHSConstants.ID, aoCBUtilities.getId());
		loQueryParam.put(HHSConstants.INVOICE_ID, aoCBUtilities.getInvoiceId());
		loQueryParam.put(HHSConstants.ENTRY_TYPE_ID, HHSConstants.THREE);
		loQueryParam.put(HHSConstants.IS_LINE_ITEM_INVOICE_AMT, aoCBUtilities.getLineItemInvoiceAmt());
		loQueryParam.put(HHSConstants.MODIFIED_BY_AGENCY, aoCBUtilities.getModifyByAgency());
		loQueryParam.put(HHSConstants.MODIFIED_BY_PROVIDER, aoCBUtilities.getModifyByProvider());
		loQueryParam.put(HHSConstants.SUB_BUDGET_ID, aoCBUtilities.getSubBudgetID());
		BigDecimal loAmount = new BigDecimal(aoCBUtilities.getLineItemInvoiceAmt());
		try
		{

			aoCBUtilities.setEntryTypeId(HHSConstants.THREE);
			// Start Enhancement 6535 Release 3.8.0
			aoCBUtilities.setInvoiceAmountCurrent(aoCBUtilities.getLineItemInvoiceAmt());
			aoCBUtilities.setTableAmountColumn(HHSConstants.GET_AMOUNT_DETAILS.get(HHSConstants.UTILITIES_TABLE));
			aoCBUtilities.setTableName(HHSConstants.UTILITIES_TABLE);
			aoCBUtilities.setTableId(HHSConstants.GET_ID_DETAILS.get(HHSConstants.UTILITIES_TABLE));
			aoCBUtilities.setEntryTypeId(HHSConstants.THREE);
			aoCBUtilities.setLineItemId(aoCBUtilities.getId());
			BigDecimal loRemainingAmountPaymentDisbursed = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession,
					aoCBUtilities, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.FETCH_RATE_REMAINING_PAYMENT_DISBURSED, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			BigDecimal loFYBudgetLineItem = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession, aoCBUtilities,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_FY_BUDGET_LINE_ITEM,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// End Enhancement 6535 Release 3.8.0
			loRemainingAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession, aoCBUtilities,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.IS_FETCH_UTILITIES_REMAINING_AMOUNT,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_UTILITIES);
			// Start Enhancement 6535 Release 3.8.0
			loRemainingAmount = loRemainingAmount.subtract(loRemainingAmountPaymentDisbursed);
			// End Enhancement 6535 Release 3.8.0
			// postive invoices
			if (new BigDecimal(aoCBUtilities.getLineItemInvoiceAmt()).compareTo(new BigDecimal(0)) >= 0)
			{
				if (loAmount.compareTo(loRemainingAmount) <= 0)
				{
					insertUpdateInvoiceDetail(aoMybatiSession, loQueryParam);
				}

				else
				{
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.MSG_KEY_INVOICE_AMOUNT_MORE_THAN_REMAINING_AMOUNT));

				}
			}
			// negative invoices
			else
			{

				// Start Enhancement 6535 Release 3.8.0
				// For invoicing less than zero scenarios
				if ((loFYBudgetLineItem.compareTo(loRemainingAmount.subtract(new BigDecimal(aoCBUtilities
						.getLineItemInvoiceAmt())))) >= 0)
				{
					// Insert new row of invoice details against this line item
					InvoiceService.insertUpdateInvoiceDetail(aoMybatiSession, loQueryParam);

				}
				else
				{
					lbError = true;

					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.REMAINING_AMOUNT_LESS_THAN_FY_BUDGET));

				}
				lbUpdateStatus = true;
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppExp)
		{
			if (lbError)
			{
				aoAppExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppExp.toString());
				setMoState("Invoice Amount entered: " + loAmount + " is greater than remaining amount: "
						+ loRemainingAmount + " \n");
				aoAppExp.addContextData("Invoice Amount entered is greater than remaining amount ", aoAppExp);
			}
			else
			{
				setMoState("Transaction Failed:: InvoiceService: updateInvoicingUtilities method - failed to update INVOICE_DETAIL"
						+ aoCBUtilities.getId() + " \n");
				aoAppExp.addContextData("Exception occured while updating Utilities ", aoAppExp);
			}
			throw aoAppExp;

		}
		return lbUpdateStatus;

	}

	/**
	 * This method fetch Indirect Rate details from DB on the basis of current
	 * invoice id <br/>
	 * <b>Functionalities by below method are :</b>
	 * <ul>
	 * <li>1.Get all Indirect Rate details in CBIndirectRateBean Bean to fetch
	 * data.</li>
	 * <li>2.Call fetchInvoiceIndirectRate query for invoice indirect details</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession sql session as input.
	 * @param aoCBGridBeanObj CBGridBean Bean as input.
	 * @return loCBIndirectRateBean CBIndirectRateBean Bean as output with all
	 *         Indirect rate related Information.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<CBIndirectRateBean> fetchInvoiceIndirectRate(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj)
			throws ApplicationException
	{
		List<CBIndirectRateBean> loCBIndirectRateBean = null;
		try
		{
			aoCBGridBeanObj.setEntryTypeId(HHSConstants.STRING_TEN);
			aoCBGridBeanObj.setInvoiceStatusIdList(HHSConstants.INVOICE_STATUS_ID_INDIRECT_RATE);
			loCBIndirectRateBean = (List<CBIndirectRateBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.IS_FETCH_INVOICE_INDIRECT_DETAILS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		// Application exception is handled here.
		catch (ApplicationException aoAppExp)
		{
			// Set the state, added context data and added error log if any
			// application exception occurs
			setMoState("error occured while fetching indirect Details for subBudget Id "
					+ aoCBGridBeanObj.getSubBudgetID());
			aoAppExp.addContextData("Exception occured while fetching indirect Details ", aoAppExp);
			throw aoAppExp;
		}
		return loCBIndirectRateBean;

	}

	/**
	 * This method is used for updating invoice amount in database for indirect
	 * rate table. <b>Functionalities by below method are :</b>
	 * <ul>
	 * <li>1. fetchIndirectRemainingAmount query to fetch the remaining amount
	 * for invoice indirect (Remaining amount calculated from difference of
	 * approved budget and ytd amount which have been in status Pending for
	 * submission, Returned for Revision, Pending Approval, Approved).</li>
	 * <li>2.Check if invoice amount is less than the remaining amount.If true
	 * then insert entry into indirect table.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatiSession sql session as input.
	 * @param aoCBIndirectRateBean CBGridBean Bean as input.
	 * @return lbUpdateStatus true if success, false if failure
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public boolean updateInvoicingIndirectRate(SqlSession aoMybatiSession, CBIndirectRateBean aoCBIndirectRateBean)
			throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		boolean lbError = false;
		BigDecimal loRemainingAmount = BigDecimal.ZERO;
		HashMap<String, Object> loQueryParam = new HashMap<String, Object>();
		loQueryParam.put(HHSConstants.ID, aoCBIndirectRateBean.getId());
		loQueryParam.put(HHSConstants.INVOICE_ID, aoCBIndirectRateBean.getInvoiceId());
		loQueryParam.put(HHSConstants.ENTRY_TYPE_ID, HHSConstants.STRING_TEN);
		loQueryParam.put(HHSConstants.MODIFIED_BY_AGENCY, aoCBIndirectRateBean.getModifyByAgency());
		loQueryParam.put(HHSConstants.MODIFIED_BY_PROVIDER, aoCBIndirectRateBean.getModifyByProvider());
		loQueryParam.put(HHSConstants.SUB_BUDGET_ID, aoCBIndirectRateBean.getSubBudgetID());
		loQueryParam.put(HHSConstants.IS_LINE_ITEM_INVOICE_AMT, aoCBIndirectRateBean.getIndirectInvoiceAmount());
		BigDecimal loAmount = new BigDecimal(aoCBIndirectRateBean.getIndirectInvoiceAmount());
		aoCBIndirectRateBean.setInvoiceStatusIdList(HHSConstants.INVOICE_STATUS_ID_REMAINING_AMOUNT);
		aoCBIndirectRateBean.setEntryTypeId(HHSConstants.STRING_TEN);
		try
		{
			// Start Enhancement 6535 Release 3.8.0
			aoCBIndirectRateBean.setInvoiceAmountCurrent(aoCBIndirectRateBean.getIndirectInvoiceAmount());
			aoCBIndirectRateBean.setTableAmountColumn(HHSConstants.GET_AMOUNT_DETAILS
					.get(HHSConstants.INDIRECT_RATE_TABLE));
			aoCBIndirectRateBean.setTableName(HHSConstants.INDIRECT_RATE_TABLE);
			aoCBIndirectRateBean.setTableId(HHSConstants.GET_ID_DETAILS.get(HHSConstants.INDIRECT_RATE_TABLE));
			aoCBIndirectRateBean.setEntryTypeId(HHSConstants.STRING_TEN);
			aoCBIndirectRateBean.setLineItemId(aoCBIndirectRateBean.getId());
			BigDecimal loRemainingAmountPaymentDisbursed = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession,
					aoCBIndirectRateBean, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.FETCH_RATE_REMAINING_PAYMENT_DISBURSED, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			BigDecimal loFYBudgetLineItem = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession, aoCBIndirectRateBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_FY_BUDGET_LINE_ITEM,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// End Enhancement 6535 Release 3.8.0
			loRemainingAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession, aoCBIndirectRateBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.IS_FETCH_INDIRECT_REMAINING_AMOUNT,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_INDIRECT);
			// Start Enhancement 6535 Release 3.8.0
			loRemainingAmount = loRemainingAmount.subtract(loRemainingAmountPaymentDisbursed);
			// End Enhancement 6535 Release 3.8.0
			// postive invoices
			if (new BigDecimal(aoCBIndirectRateBean.getIndirectInvoiceAmount()).compareTo(new BigDecimal(0)) >= 0)
			{
				if (loRemainingAmount == null || loAmount.compareTo(loRemainingAmount) > 0)
				{
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.MSG_KEY_INVOICE_AMOUNT_MORE_THAN_REMAINING_AMOUNT));
				}
				else
				{
					insertUpdateInvoiceDetail(aoMybatiSession, loQueryParam);
				}
			}
			// negative invoices
			else
			{

				// Start Enhancement 6535 Release 3.8.0
				// For invoicing less than zero scenarios
				if ((loFYBudgetLineItem.compareTo(loRemainingAmount.subtract(new BigDecimal(aoCBIndirectRateBean
						.getIndirectInvoiceAmount())))) >= 0)
				{
					// Insert new row of invoice details against this line item
					InvoiceService.insertUpdateInvoiceDetail(aoMybatiSession, loQueryParam);

				}
				else
				{
					lbError = true;

					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.REMAINING_AMOUNT_LESS_THAN_FY_BUDGET));

				}
				// End Enhancement 6535 Release 3.8.0
				lbUpdateStatus = true;
			}
		}
		// Application exception is handled here.
		catch (ApplicationException aoAppExp)
		{
			// if error occur then Set the state, added context data and added
			// error log if
			// invoice Amount entered is greater than remaining amount
			if (lbError)
			{
				setMoState("Invoice Amount entered: " + loAmount + " is greater than remaining amount: "
						+ loRemainingAmount + " \n");
				aoAppExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBIndirectRateBean.getSubBudgetID());
				aoAppExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_BUDGET,
						aoCBIndirectRateBean.getContractBudgetID());
				aoAppExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppExp.toString());
				aoAppExp.addContextData("Invoice Amount entered is greater than remaining amount ", aoAppExp);
			}
			// if error occur then Set the state, added context data and added
			// error log if any application exception occurs
			else
			{
				setMoState("Transaction Failed:: InvoiceService: updateInvoicingIndirectRate method - failed to update INVOICE_DETAIL"
						+ aoCBIndirectRateBean.getId() + " \n");
				aoAppExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBIndirectRateBean.getSubBudgetID());
				aoAppExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_BUDGET,
						aoCBIndirectRateBean.getContractBudgetID());
				LOG_OBJECT.Error("Invoice Amount entered is greater than remaining amount for indirect rate", aoAppExp);
			}
			throw aoAppExp;

		}
		return lbUpdateStatus;

	}

	/**
	 * <p>
	 * This method is used for Invoicing to fetch the values from MileStone grid
	 * for a particular sub-budget.
	 * <ul>
	 * <li>Fetches the milestone information from the database for Contract
	 * Invoicing on load.</li>
	 * <li>Query Used: fetchMilestoneInvoiceDetails</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBGridBeanObj - CBMileStoneBean object containing key fields
	 * @param aoMybatisSession - SqlSession object
	 * @return loCBMileStoneBean - CBMileStoneBean
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public List<CBMileStoneBean> fetchMilestoneInvoice(CBGridBean aoCBGridBeanObj, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		List<CBMileStoneBean> loCBMileStoneBean = null;
		try
		{
			// get the invoice amount details for milestone corresponding to
			// invoice id
			aoCBGridBeanObj.setEntryTypeId(HHSConstants.EIGHT);
			loCBMileStoneBean = (List<CBMileStoneBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.CBY_FETCH_MILESTONE_INVOICE_DETAILS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		// any exception from dao class will be thrown as application exception
		// which will be handles over here. it throws application exception back
		catch (ApplicationException aoAppExp)
		{
			setMoState("error occured while fetching MileStone Details for budget type id "
					+ aoCBGridBeanObj.getBudgetTypeId());
			aoAppExp.addContextData("Exception occured while fetching MileStone Details ", aoAppExp);
			LOG_OBJECT.Error("error occured while fetching MileStone Details ", aoAppExp);
			throw aoAppExp;
		}
		return loCBMileStoneBean;
	}

	/**
	 * <p>
	 * This method is used for Invoicing to edit the rows in Milestone grid for
	 * a particular sub-budget.
	 * <ul>
	 * <li>Provider is able to Edit the Milestone line for Invoicing items that
	 * have previously been added.</li>
	 * <li>Query Used: fetchRemainingAmountMilestone</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoCBMilestoneBean - CBMileStoneBean object containing key fields
	 * @param aoMybatisSession - SqlSession object
	 * @return loUpdateMiletone - Boolean status
	 * @throws ApplicationException - ApplicationException object
	 */
	public Boolean updateMilestoneInvoice(CBMileStoneBean aoCBMilestoneBean, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		Boolean loUpdateMilestone = true;
		boolean lbError = false;
		HashMap<String, Object> loQueryParam = new HashMap<String, Object>();
		BigDecimal loRemainAmountCheck = BigDecimal.ZERO;

		aoCBMilestoneBean.setEntryTypeId(HHSConstants.EIGHT);
		loQueryParam.put(HHSConstants.ID, aoCBMilestoneBean.getId());
		loQueryParam.put(HHSConstants.INVOICE_ID, aoCBMilestoneBean.getInvoiceId());
		loQueryParam.put(HHSConstants.ENTRY_TYPE_ID, aoCBMilestoneBean.getEntryTypeId());
		loQueryParam.put(HHSConstants.IS_LINE_ITEM_INVOICE_AMT, aoCBMilestoneBean.getInvoiceAmount());
		loQueryParam.put(HHSConstants.MODIFIED_BY_AGENCY, aoCBMilestoneBean.getModifyByAgency());
		loQueryParam.put(HHSConstants.MODIFIED_BY_PROVIDER, aoCBMilestoneBean.getModifyByProvider());
		loQueryParam.put(HHSConstants.SUB_BUDGET_ID, aoCBMilestoneBean.getSubBudgetID());
		BigDecimal loInvoiceAmountCheck = new BigDecimal(aoCBMilestoneBean.getInvoiceAmount());
		try
		{
			// Start Enhancement 6535 Release 3.8.0
			aoCBMilestoneBean.setInvoiceAmountCurrent(aoCBMilestoneBean.getInvoiceAmount());
			aoCBMilestoneBean.setTableAmountColumn(HHSConstants.GET_AMOUNT_DETAILS.get(HHSConstants.MILESTONE));
			aoCBMilestoneBean.setTableName(HHSConstants.MILESTONE);
			aoCBMilestoneBean.setTableId(HHSConstants.GET_ID_DETAILS.get(HHSConstants.MILESTONE));
			aoCBMilestoneBean.setEntryTypeId(HHSConstants.EIGHT);
			aoCBMilestoneBean.setLineItemId(aoCBMilestoneBean.getId());
			BigDecimal loRemainingAmountPaymentDisbursed = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession,
					aoCBMilestoneBean, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.FETCH_RATE_REMAINING_PAYMENT_DISBURSED, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			BigDecimal loFYBudgetLineItem = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoCBMilestoneBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_FY_BUDGET_LINE_ITEM,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// End Enhancement 6535 Release 3.8.0
			// get the remaining amount for server side check before updating
			// invoice details
			loRemainAmountCheck = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoCBMilestoneBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.IS_FETCH_REMAINING_AMOUNT_MILESTONE,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_MILE_STONE_BEAN);
			// Start Enhancement 6535 Release 3.8.0
			loRemainAmountCheck = loRemainAmountCheck.subtract(loRemainingAmountPaymentDisbursed);
			// End Enhancement 6535 Release 3.8.0
			// postive invoices
			if (new BigDecimal(aoCBMilestoneBean.getInvoiceAmount()).compareTo(new BigDecimal(0)) >= 0)
			{
				// if invoice amount is not greater than remaining amount,
				// update
				// method called
				if (loRemainAmountCheck.compareTo(loInvoiceAmountCheck) >= 0)
				{
					insertUpdateInvoiceDetail(aoMybatisSession, loQueryParam);
				}

				else
				{
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.MSG_KEY_INVOICE_AMOUNT_MORE_THAN_REMAINING_AMOUNT));

				}
			}
			// negative invoices
			else
			{

				// Start Enhancement 6535 Release 3.8.0
				// For invoicing less than zero scenarios
				if ((loFYBudgetLineItem.compareTo(loRemainAmountCheck.subtract(new BigDecimal(aoCBMilestoneBean
						.getInvoiceAmount())))) >= 0)
				{
					// Insert new row of invoice details against this line item
					InvoiceService.insertUpdateInvoiceDetail(aoMybatisSession, loQueryParam);

				}
				else
				{
					lbError = true;

					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.REMAINING_AMOUNT_LESS_THAN_FY_BUDGET));

				}
			}
			loUpdateMilestone = true;
		}
		// any exception from dao class will be thrown as application exception
		// which will be handles over here. it throws application exception back
		catch (ApplicationException aoAppExp)
		{
			if (lbError)
			{
				aoAppExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppExp.toString());
				setMoState("Invoice Amount entered: " + loInvoiceAmountCheck + " is greater than remaining amount: "
						+ loRemainAmountCheck + " \n");
				aoAppExp.addContextData("Invoice Amount entered is greater than remaining amount ", aoAppExp);
			}
			else
			{
				setMoState("error occured while editing Milestone for budget type id "
						+ aoCBMilestoneBean.getBudgetTypeId());
				aoAppExp.addContextData("Exception occured while editing Milestone ", aoAppExp);
				LOG_OBJECT.Error("error occured while editing Milestone ", aoAppExp);
			}
			throw aoAppExp;
		}
		return loUpdateMilestone;
	}

	/**
	 * <p>
	 * This method is used to Update Invoice Amount entered in INVOICE_DETAIL
	 * table if there is an entry in the table for for a particular Invoice Id,
	 * Line Item Id, Entry Type and INSERTS a new row in the table if no entry
	 * exists.
	 * <ul>
	 * <li>1. Execute updateInvoicingDetails query to update Invoice Amount from
	 * grid in INVOICE_DETAIL table</li>
	 * <li>2. If the updateInvoicingDetails query returns a count <1 then no
	 * entry exist in the INVOICE_DETAIL table, so execute
	 * insertInvoicingLineItemDetails query to insert a new row in the table.</li>
	 * <li>This method updated in R4</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatiSession - SqlSession object
	 * @param aoQueryParam - contains line item details such as amount, id,
	 *            entry type.This is the map containing Id, InvoiceId,
	 *            EntryTypeId, IsLineItemInvoiceAmount,
	 *            createdByUsedId,ModByUserId,SubBudgetId
	 * @throws ApplicationException - ApplicationException object
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public static void insertUpdateInvoiceDetail(SqlSession aoMybatiSession, HashMap aoQueryParam)
			throws ApplicationException
	{

		int liUpdateCount = HHSConstants.INT_ZERO;
		try
		{
			// update the invoice detail if it has entry for that line item
			// otherwise insert row for the line item
			liUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatiSession, aoQueryParam,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.IS_UPDATE_INVOICING_DETAILS,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			if (liUpdateCount < 1)
			{
				DAOUtil.masterDAO(aoMybatiSession, aoQueryParam, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
						HHSConstants.IS_INSERT_INVOICING_LINE_ITEM_DETAILS, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppExp)
		{
			aoAppExp.addContextData("Exception occured while updating Utilities ", aoAppExp);
			LOG_OBJECT.Error("Exception occured in InvoiceService: insertUpdateInvoiceDetail method:: ", aoAppExp);
			throw aoAppExp;
		}
	}

	/**
	 * This method fetch the invoice list summary page. <li>Query Used:
	 * fetchInvoiceSummary</li>
	 * 
	 * @param aoMyBatisSession sql session as input
	 * @param aoCBGridBean cb grid bean as input
	 * @return loCBInvoiceSummary ContractBudgetSummary as return value
	 * @throws ApplicationException application exception if any error occurs
	 * @throws IllegalAccessException exception if any error occurs
	 * @throws InvocationTargetException exception if any error occurs
	 */
	@SuppressWarnings("unchecked")
	public ContractBudgetSummary fetchInvoiceSummary(SqlSession aoMyBatisSession, CBGridBean aoCBGridBean)
			throws ApplicationException
	{
		List<BudgetDetails> loInvoiceSummary = null;
		ContractBudgetSummary loCBInvoiceSummary = new ContractBudgetSummary();
		Map<String, Object> loInvSumInputParam = new HashMap<String, Object>();
		try
		{
			setInvoiceInputParam(aoCBGridBean, loInvSumInputParam);
			loInvoiceSummary = (List<BudgetDetails>) DAOUtil.masterDAO(aoMyBatisSession, loInvSumInputParam,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_INVOICING_SUMMARY_METHOD,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			for (BudgetDetails loBudgetDetails : loInvoiceSummary)
			{
				Map<String, String> loMap = HHSConstants.CONTRACT_BUDGET;
				String lsProperty = loMap.get(loBudgetDetails.getTitle());
				BeanUtils.setProperty(loCBInvoiceSummary, lsProperty, loBudgetDetails);
			}
			setTotalAmounts(loCBInvoiceSummary);
			setMoState("Success while fetching invoice summary sub budget id" + aoCBGridBean.getSubBudgetID());
		}
		// Application exception is handled here.
		catch (ApplicationException aoAppExp)
		{
			// Set the state, added context data and added error log if any
			// application exception occurs
			setMoState("Error occured while fetching invoice summary for sub budget id" + aoCBGridBean.getSubBudgetID());
			aoAppExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_SUB_BUDGET, aoCBGridBean.getSubBudgetID());
			aoAppExp.addContextData(HHSConstants.ADD_CONTEXT_DATA_BUDGET, aoCBGridBean.getContractBudgetID());
			LOG_OBJECT.Error("Error occured while fetching invoice summary:", aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoExp)
		{

			LOG_OBJECT.Error("Exception occured in InvoiceService: fetchInvoiceSummary method:: ", aoExp);
			setMoState("Transaction Failed:: InvoiceService: fetchInvoiceSummary method - failed for ContractId : "
					+ aoCBGridBean.getContractID() + " \n");
			throw new ApplicationException("Error occured while fetching Invoice Summary", aoExp);
		}
		return loCBInvoiceSummary;
	}

	/**
	 * This method set the default key value for entry type and bean name.
	 * 
	 * @param aoCBGridBean CBGridBean as input
	 * @param aoInvSumInputParam map as input
	 */
	private void setInvoiceInputParam(CBGridBean aoCBGridBean, Map<String, Object> aoInvSumInputParam)
	{
		if (aoCBGridBean != null && aoInvSumInputParam != null)
		{
			aoInvSumInputParam.put(HHSConstants.CONTRACT_BUDGET_ID, aoCBGridBean.getContractBudgetID());
			aoInvSumInputParam.put(HHSConstants.SUB_BUDGET_ID, aoCBGridBean.getSubBudgetID());
			aoInvSumInputParam.put(HHSConstants.INVOICE_ID, aoCBGridBean.getInvoiceId());
			aoInvSumInputParam.put(HHSConstants.INVOICE_LIST, HHSConstants.INVOICE_STATUS_ID_INDIRECT_RATE);
			aoInvSumInputParam.putAll(HHSConstants.INVOICE_CONSTANT);
		}
	}

	/**
	 * <p>
	 * This method calculates the Total Operations,Support and Equipment,Total
	 * OTPS, Total Directs Costs and Total Program Budget Amount
	 * 
	 * </p>
	 * 
	 * @param aoBudgetSummary an object of ContractBudgetSummary
	 * @return
	 */
	private void setTotalAmounts(ContractBudgetSummary aoBudgetSummary)
	{
		aoBudgetSummary.getTotalSalaryAndFringesAmount().setTitle(HHSConstants.TSF);
		aoBudgetSummary.getTotalSalaryAndFringesAmount().setApprovedBudget(
				aoBudgetSummary.getTotalSalary().getApprovedBudget()
						.add(aoBudgetSummary.getTotalFringes().getApprovedBudget()));
		aoBudgetSummary.getTotalSalaryAndFringesAmount().setYtdInvoicedAmount(
				aoBudgetSummary.getTotalSalary().getYtdInvoicedAmount()
						.add(aoBudgetSummary.getTotalFringes().getYtdInvoicedAmount()));
		aoBudgetSummary.getTotalSalaryAndFringesAmount().setInvoicedAmount(
				aoBudgetSummary.getTotalSalary().getInvoicedAmount()
						.add(aoBudgetSummary.getTotalFringes().getInvoicedAmount()));
		aoBudgetSummary.getTotalSalaryAndFringesAmount().setRemainingAmount(
				aoBudgetSummary.getTotalSalary().getRemainingAmount()
						.add(aoBudgetSummary.getTotalFringes().getRemainingAmount()));

		aoBudgetSummary.getOperationsAndSupportAmount().setTitle(HHSConstants.OS);
		aoBudgetSummary.getOperationsAndSupportAmount().setApprovedBudget(
				aoBudgetSummary.getOperationsAndSupportAmount().getApprovedBudget()
						.add(aoBudgetSummary.getEquipmentAmount().getApprovedBudget()));
		aoBudgetSummary.getOperationsAndSupportAmount().setYtdInvoicedAmount(
				aoBudgetSummary.getOperationsAndSupportAmount().getYtdInvoicedAmount()
						.add(aoBudgetSummary.getEquipmentAmount().getYtdInvoicedAmount()));
		aoBudgetSummary.getOperationsAndSupportAmount().setInvoicedAmount(
				aoBudgetSummary.getOperationsAndSupportAmount().getInvoicedAmount()
						.add(aoBudgetSummary.getEquipmentAmount().getInvoicedAmount()));
		aoBudgetSummary.getOperationsAndSupportAmount().setRemainingAmount(
				aoBudgetSummary.getOperationsAndSupportAmount().getApprovedBudget()
						.subtract(aoBudgetSummary.getOperationsAndSupportAmount().getYtdInvoicedAmount()));

		aoBudgetSummary.getTotalOTPSAmount().setTitle(HHSConstants.TOTPS);
		aoBudgetSummary.getTotalOTPSAmount().setApprovedBudget(
				(aoBudgetSummary.getOperationsAndSupportAmount().getApprovedBudget()
						.add(aoBudgetSummary.getUtilitiesAmount().getApprovedBudget())
						.add(aoBudgetSummary.getProfessionalServicesAmount().getApprovedBudget())
						.add(aoBudgetSummary.getRentAndOccupancyAmount().getApprovedBudget()).add(aoBudgetSummary
						.getContractedServicesAmount().getApprovedBudget())));
		aoBudgetSummary.getTotalOTPSAmount().setYtdInvoicedAmount(
				(aoBudgetSummary.getOperationsAndSupportAmount().getYtdInvoicedAmount()
						.add(aoBudgetSummary.getUtilitiesAmount().getYtdInvoicedAmount())
						.add(aoBudgetSummary.getProfessionalServicesAmount().getYtdInvoicedAmount())
						.add(aoBudgetSummary.getRentAndOccupancyAmount().getYtdInvoicedAmount()).add(aoBudgetSummary
						.getContractedServicesAmount().getYtdInvoicedAmount())));
		aoBudgetSummary.getTotalOTPSAmount().setInvoicedAmount(
				(aoBudgetSummary.getOperationsAndSupportAmount().getInvoicedAmount()
						.add(aoBudgetSummary.getUtilitiesAmount().getInvoicedAmount())
						.add(aoBudgetSummary.getProfessionalServicesAmount().getInvoicedAmount())
						.add(aoBudgetSummary.getRentAndOccupancyAmount().getInvoicedAmount()).add(aoBudgetSummary
						.getContractedServicesAmount().getInvoicedAmount())));
		aoBudgetSummary.getTotalOTPSAmount().setRemainingAmount(
				(aoBudgetSummary.getTotalOTPSAmount().getApprovedBudget().subtract(aoBudgetSummary.getTotalOTPSAmount()
						.getYtdInvoicedAmount())));

		aoBudgetSummary.getTotalDirectsCosts().setTitle(HHSConstants.TDC);
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
		aoBudgetSummary.getTotalDirectsCosts().setInvoicedAmount(
				aoBudgetSummary.getTotalSalaryAndFringesAmount().getInvoicedAmount()
						.add(aoBudgetSummary.getTotalOTPSAmount().getInvoicedAmount())
						.add(aoBudgetSummary.getTotalRateBasedAmount().getInvoicedAmount())
						.add(aoBudgetSummary.getTotalMilestoneBasedAmount().getInvoicedAmount())
						.add(aoBudgetSummary.getUnallocatedFunds().getInvoicedAmount()));
		aoBudgetSummary.getTotalDirectsCosts().setRemainingAmount(
				aoBudgetSummary.getTotalDirectsCosts().getApprovedBudget()
						.subtract(aoBudgetSummary.getTotalDirectsCosts().getYtdInvoicedAmount()));

		aoBudgetSummary.getTotalCityFundedBudget().setTitle(HHSConstants.TCFB);
		aoBudgetSummary.getTotalCityFundedBudget().setApprovedBudget(
				(aoBudgetSummary.getTotalDirectsCosts().getApprovedBudget().add(aoBudgetSummary.getTotalIndirectCosts()
						.getApprovedBudget())));
		aoBudgetSummary.getTotalCityFundedBudget().setYtdInvoicedAmount(
				(aoBudgetSummary.getTotalDirectsCosts().getYtdInvoicedAmount().add(aoBudgetSummary
						.getTotalIndirectCosts().getYtdInvoicedAmount())));
		aoBudgetSummary.getTotalCityFundedBudget().setInvoicedAmount(
				(aoBudgetSummary.getTotalDirectsCosts().getInvoicedAmount().add(aoBudgetSummary.getTotalIndirectCosts()
						.getInvoicedAmount())));
		aoBudgetSummary.getTotalCityFundedBudget().setRemainingAmount(
				(aoBudgetSummary.getTotalCityFundedBudget().getApprovedBudget().subtract(aoBudgetSummary
						.getTotalCityFundedBudget().getYtdInvoicedAmount())));

		aoBudgetSummary.getTotalProgramBudget().setTitle(HHSConstants.TPB);
		aoBudgetSummary.getTotalProgramBudget().setApprovedBudget(
				(aoBudgetSummary.getTotalCityFundedBudget().getApprovedBudget().add(aoBudgetSummary
						.getTotalProgramIncome().getApprovedBudget())));
		aoBudgetSummary.getTotalProgramBudget().setYtdInvoicedAmount(
				(aoBudgetSummary.getTotalCityFundedBudget().getYtdInvoicedAmount().add(aoBudgetSummary
						.getTotalProgramIncome().getYtdInvoicedAmount())));
		aoBudgetSummary.getTotalProgramBudget().setInvoicedAmount(
				(aoBudgetSummary.getTotalCityFundedBudget().getInvoicedAmount().add(aoBudgetSummary
						.getTotalProgramIncome().getInvoicedAmount())));
		aoBudgetSummary.getTotalProgramBudget().setRemainingAmount(
				(aoBudgetSummary.getTotalProgramBudget().getApprovedBudget().subtract(aoBudgetSummary
						.getTotalProgramBudget().getYtdInvoicedAmount())));
	}

	/**
	 * <p>
	 * This method is used for fetching values in Invoicing - Professional
	 * Services grid for a particular sub-budget based upon budget type as
	 * below: Invoice Amendment <br/>
	 * <ul>
	 * <li>CBGridBean is used to populate values in grid</li>
	 * <li>Provider is able to fetch existing equipment details:</li>
	 * <li>1.the Remaining amount</li>
	 * <li>2.the Invoice amount</li>
	 * <li>Query Used: fetchInvoiceProfServices</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoProfService - CBGridBean object containing key fields
	 * @param aoMybatisSession - SqlSession object
	 * @return List<CBProfessionalServicesBean> - returns list of bean of type
	 *         <CBProfessionalServicesBean>
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<CBProfessionalServicesBean> fetchProfServicesDetails(CBGridBean aoProfService,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		List<CBProfessionalServicesBean> loProfServicesDetails = null;

		try
		{
			// Fetching data for Invoicing - Professional Service grid.
			loProfServicesDetails = (List<CBProfessionalServicesBean>) DAOUtil.masterDAO(aoMybatisSession,
					aoProfService, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.INV_FETCH_INVOICE_PROF_SERVICES, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppExp)
		{
			aoAppExp.addContextData(
					"Error while fetching Invoicing - Professional Service Details : fetchProfServicesDetails",
					aoAppExp);
			LOG_OBJECT
					.Error("Error while fetching Invoicing - Professional Service Details : fetchProfServicesDetails "
							+ aoAppExp);
			setMoState("Error while fetching Invoicing - Professional Service Details for budget id:"
					+ aoProfService.getContractBudgetID() + " and Sub-Budget id : " + aoProfService.getSubBudgetID()
					+ " and Invoice_ID : " + aoProfService.getInvoiceId());
			throw aoAppExp;
		}

		return loProfServicesDetails;
	}

	/**
	 * <p>
	 * This method is used for editing/updating values in Invoicing -
	 * Professional Service grid of Invoicing module
	 * <ul>
	 * <li>CBProfessionalServicesBean is used to populate values in grid</li>
	 * <li>Provider is able to edit the :</li>
	 * <li>1.Invoice Amount for the pre-defined Professional Service line items</li>
	 * <li>2.Other field for the pre-defined Professional Service line items</li>
	 * <li>Query Used: getInvoiceDetailId</li>
	 * <li>Query Used: updateInvoiceForProfServices</li>
	 * <li>Query Used: addInvoiceForProfServices</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoProfService - CBProfessionalServicesBean object containing key
	 *            fields
	 * @param aoMybatisSession - SqlSession object
	 * @return Boolean - returns boolean status of edit/update query
	 * @throws ApplicationException - ApplicationException object
	 */
	public boolean editProfServicesDetails(CBProfessionalServicesBean aoProfService, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbError = false;
		boolean lbUpdateStatus = false;

		try
		{
			BigDecimal loInvoiceAmount = new BigDecimal(aoProfService.getInvoiceAmount());
			// Get invoice detail ID
			String lsInvoiceDetailID = (String) DAOUtil.masterDAO(aoMybatisSession, aoProfService,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.INV_GET_INVOICE_DETAIL_ID,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN);
			// Start Enhancement 6535 Release 3.8.0
			aoProfService.setInvoiceAmountCurrent(aoProfService.getInvoiceAmount());
			aoProfService.setTableAmountColumn(HHSConstants.GET_AMOUNT_DETAILS.get(HHSConstants.PROFESSIONAL_SERVICE));
			aoProfService.setTableName(HHSConstants.PROFESSIONAL_SERVICE);
			aoProfService.setTableId(HHSConstants.GET_ID_DETAILS.get(HHSConstants.PROFESSIONAL_SERVICE));
			aoProfService.setEntryTypeId(HHSConstants.FOUR);
			aoProfService.setLineItemId(aoProfService.getId());
			BigDecimal loRemainingAmountPaymentDisbursed = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession,
					aoProfService, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.FETCH_RATE_REMAINING_PAYMENT_DISBURSED, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			BigDecimal loFYBudgetLineItem = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoProfService,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_FY_BUDGET_LINE_ITEM,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// End Enhancement 6535 Release 3.8.0
			BigDecimal loTotalRemainingAmnt = totalRemainingAmount(lsInvoiceDetailID, aoProfService, aoMybatisSession);
			// Start Enhancement 6535 Release 3.8.0
			loTotalRemainingAmnt = loTotalRemainingAmnt.subtract(loRemainingAmountPaymentDisbursed);
			// End Enhancement 6535 Release 3.8.0
			if (new BigDecimal(aoProfService.getInvoiceAmount()).compareTo(new BigDecimal(0)) >= 0)
			{
				if (loTotalRemainingAmnt.compareTo(loInvoiceAmount) >= 0)
				{
					if (lsInvoiceDetailID != null && !HHSConstants.EMPTY_STRING.equalsIgnoreCase(lsInvoiceDetailID))
					{
						// Update invoice amount of invoice detail for
						// professional
						// service line item
						DAOUtil.masterDAO(aoMybatisSession, aoProfService, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
								HHSConstants.INV_UPDATE_INVOICE_FOR_PROF_SERVICES,
								HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN);
					}
					else
					{
						// Add new invoice for professional service line item
						DAOUtil.masterDAO(aoMybatisSession, aoProfService, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
								HHSConstants.INV_ADD_INVOICE_FOR_PROF_SERVICES,
								HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN);

					}

					lbUpdateStatus = true;

				}
				else
				{
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.MSG_KEY_INVOICE_AMOUNT_MORE_THAN_REMAINING_AMOUNT));
				}
			}
			// for negative invoices
			else
			{
				// Start Enhancement 6535 Release 3.8.0
				// For invoicing less than zero scenarios
				if ((loFYBudgetLineItem.compareTo(loTotalRemainingAmnt.subtract(new BigDecimal(aoProfService
						.getInvoiceAmount())))) >= 0)
				{
					if (lsInvoiceDetailID != null && !HHSConstants.EMPTY_STRING.equalsIgnoreCase(lsInvoiceDetailID))
					{
						// Update invoice amount of invoice detail for
						// professional
						// service line item
						DAOUtil.masterDAO(aoMybatisSession, aoProfService, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
								HHSConstants.INV_UPDATE_INVOICE_FOR_PROF_SERVICES,
								HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN);
					}
					else
					{
						// Add new invoice for professional service line item
						DAOUtil.masterDAO(aoMybatisSession, aoProfService, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
								HHSConstants.INV_ADD_INVOICE_FOR_PROF_SERVICES,
								HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN);

					}

					lbUpdateStatus = true;

				}
				else
				{
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.REMAINING_AMOUNT_LESS_THAN_FY_BUDGET));

				}
				// End Enhancement 6535 Release 3.8.0
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppExp)
		{
			if (lbError)
			{
				aoAppExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppExp.toString());
			}

			setMoState("Transaction Failed:: InvoiceService: editProfServicesDetails method - failed to update INVOICE_DETAIL"
					+ aoProfService.getInvoiceId() + " \n");
			throw aoAppExp;
		}
		// handling exception other than Application Exception
		catch (Exception aoExp)
		{

			setMoState("Transaction Failed:: InvoiceService: editProfServicesDetails method - failed to update INVOICE_DETAIL"
					+ aoProfService.getInvoiceId() + " \n");
			throw new ApplicationException("Error occured while updating Invoice Amount for Professional Service",
					aoExp);
		}
		return lbUpdateStatus;
	}

	/**
	 * The Method will fetch total invoice amount for Invoicing - Professional
	 * Services grid <li>Query Used: fetchTotalRemainingAmnt</li> <li>Query
	 * Used: fetchRemainingAmnt</li>
	 * @param asInvoiceDetailID : Invoice Detail Id
	 * @param aoProfService :CBProfessionalServicesBean
	 * @param aoMybatisSession - Mybatis Session
	 * 
	 * @return ldTotalRemainingAmnt
	 * @throws ApplicationException - ApplicationException object
	 * 
	 */
	private BigDecimal totalRemainingAmount(String asInvoiceDetailID, CBProfessionalServicesBean aoProfService,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		BigDecimal loTotalRemainingAmnt = BigDecimal.ZERO;

		try
		{
			if (asInvoiceDetailID != null && !HHSConstants.EMPTY_STRING.equalsIgnoreCase(asInvoiceDetailID))
			{
				aoProfService.setInvoiceDetailId(asInvoiceDetailID);
				loTotalRemainingAmnt = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoProfService,
						HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.INV_FETCH_TOTAL_REMAINING_AMNT,
						HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN);
			}
			else
			{
				loTotalRemainingAmnt = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoProfService,
						HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.INV_FETCH_REMAINING_AMNT,
						HHSConstants.CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN);
			}

		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppExp)
		{
			setMoState("Error while fetching total invoice amount for Professional service in Invoicing : budget id:"
					+ aoProfService.getContractBudgetID() + "and Sub-Budget id : " + aoProfService.getSubBudgetID()
					+ " and line_item_id : " + aoProfService.getId());
			aoAppExp.addContextData(
					"Error while fetching total invoice amount for Professional service in Invoicing :  totalRemainingAmount ",
					aoAppExp);
			LOG_OBJECT
					.Error("Error while fetching total invoice amount for Professional service in Invoicing :  totalRemainingAmount "
							+ aoAppExp);
			throw aoAppExp;
		}

		return loTotalRemainingAmnt;

	}

	/**
	 * <p>
	 * This method is used for fetching values in Rent grid for a particular
	 * sub-budget. <br/>
	 * 
	 * </p>
	 * <li>Query Used: fetchInvoicingRent</li>
	 * @param aoCBGridBean - Bean object for contract budget
	 * @param aoMyBatisSession SqlSession object
	 * @return List<Rent> - returns list of bean of type <Rent>
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings("unchecked")
	public List<Rent> fetchContractInvoiceRent(CBGridBean aoCBGridBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<Rent> loRentBean = null;

		try
		{
			aoCBGridBean.setEntryTypeId(HHSConstants.FIVE);
			loRentBean = (List<Rent>) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.IS_FETCH_INVOICING_RENT,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

		}
		/**
		 * Throw application exception in case the query fails and set the
		 * context data as "Exception Occured"
		 */
		catch (ApplicationException aoAppEx)
		{
			setMoState("error occured while fetching Rent Details for subBudget Id " + aoCBGridBean.getSubBudgetID());
			aoAppEx.addContextData("Exception occured while fetching Rent Details ", aoAppEx);
			throw aoAppEx;
		}

		return loRentBean;
	}

	/**
	 * To update Invoicing rent type taking the parameters as the current rentId
	 * and session object <li>Query Used: fetchRentRemainingAmount</li>
	 * @param aoMybatiSession SqlSession object
	 * @param aoRent Rent object for invoicing
	 * @return boolean lbUpdateStatus
	 * @throws ApplicationException - ApplicationException object
	 */
	public boolean updateContractInvoiceRent(SqlSession aoMybatiSession, Rent aoRent) throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		boolean lbError = false;
		try
		{

			HashMap<String, Object> loQueryParam = new HashMap<String, Object>();
			BigDecimal loRemainingAmount = BigDecimal.ZERO;
			loQueryParam.put(HHSConstants.ID, aoRent.getId());
			loQueryParam.put(HHSConstants.INVOICE_ID, aoRent.getInvoiceId());
			loQueryParam.put(HHSConstants.ENTRY_TYPE_ID, HHSConstants.FIVE);
			loQueryParam.put(HHSConstants.IS_LINE_ITEM_INVOICE_AMT, aoRent.getLineItemInvoiceAmt());
			loQueryParam.put(HHSConstants.MODIFIED_BY_AGENCY, aoRent.getModifyByAgency());
			loQueryParam.put(HHSConstants.MODIFIED_BY_PROVIDER, aoRent.getModifyByProvider());
			loQueryParam.put(HHSConstants.SUB_BUDGET_ID, aoRent.getSubBudgetID());

			BigDecimal loAmount = new BigDecimal(aoRent.getLineItemInvoiceAmt());
			// Start Enhancement 6535 Release 3.8.0
			aoRent.setInvoiceAmountCurrent(aoRent.getLineItemInvoiceAmt());
			aoRent.setTableAmountColumn(HHSConstants.GET_AMOUNT_DETAILS.get(HHSConstants.RENT_TABLE));
			aoRent.setTableName(HHSConstants.RENT_TABLE);
			aoRent.setTableId(HHSConstants.GET_ID_DETAILS.get(HHSConstants.RENT_TABLE));
			aoRent.setEntryTypeId(HHSConstants.FIVE);
			aoRent.setLineItemId(aoRent.getId());
			BigDecimal loRemainingAmountPaymentDisbursed = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession, aoRent,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_RATE_REMAINING_PAYMENT_DISBURSED,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			BigDecimal loFYBudgetLineItem = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession, aoRent,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_FY_BUDGET_LINE_ITEM,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// End Enhancement 6535 Release 3.8.0
			loRemainingAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession, aoRent,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.IS_FETCH_RENT_REMAINING_AMOUNT,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_RENT);
			// Start Enhancement 6535 Release 3.8.0
			loRemainingAmount = loRemainingAmount.subtract(loRemainingAmountPaymentDisbursed);
			// End Enhancement 6535 Release 3.8.0
			// postive invoices
			if (new BigDecimal(aoRent.getLineItemInvoiceAmt()).compareTo(new BigDecimal(0)) >= 0)
			{
				if (loRemainingAmount.compareTo(loAmount) >= 0)
				{
					insertUpdateInvoiceDetail(aoMybatiSession, loQueryParam);
				}

				else
				{
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.MSG_KEY_INVOICE_AMOUNT_MORE_THAN_REMAINING_AMOUNT));

				}
			}
			// negative invoices
			else
			{

				// Start Enhancement 6535 Release 3.8.0
				// For invoicing less than zero scenarios
				if ((loFYBudgetLineItem.compareTo(loRemainingAmount.subtract(loAmount))) >= 0)
				{
					// Insert new row of invoice details against this line item
					InvoiceService.insertUpdateInvoiceDetail(aoMybatiSession, loQueryParam);

				}
				else
				{
					lbError = true;

					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.REMAINING_AMOUNT_LESS_THAN_FY_BUDGET));

				}
				// End Enhancement 6535 Release 3.8.0

			}
			lbUpdateStatus = true;
		}
		/**
		 * Throw application exception in case the query fails and set the
		 * context data as "Exception Occured"
		 */
		catch (ApplicationException aoAppEx)
		{
			if (lbError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			setMoState("Transaction Failed:: InvoiceService: updateInvoicingRent method - failed to update INVOICE_DETAIL"
					+ aoRent.getId() + " \n");
			aoAppEx.addContextData("Exception occured while updating Rent ", aoAppEx);
			throw aoAppEx;
		}
		return lbUpdateStatus;

	}

	/**
	 * <ul>
	 * <li>This service class is invoked through fetchInvFyBudgetSummary
	 * transaction id for Contract Invoice screen</li>
	 * <li>This method fetchContractInvoiceFyBudgetSummary will get the Fiscal
	 * Year contract Information on the basis of contractId and budgetId</li>
	 * <li>Query Used: fetchInvFyBudgetSummary</li>
	 * <li>Query Used: fetchInvFyBudgetActualPaid</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Session Object
	 * @param aoContractList :ContractList
	 * @return loFyBudget
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public BudgetDetails fetchContractInvoiceFyBudgetSummary(SqlSession aoMybatisSession, ContractList aoContractList)
			throws ApplicationException
	{
		BudgetDetails loFyBudget = null;
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoContractList.getBudgetId());
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoContractList.getContractId());
		loHashMap.put(HHSConstants.INVOICE_ID, aoContractList.getInvoiceId());
		try
		{
			loFyBudget = (BudgetDetails) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.INV_FETCH_FY_BUDGET_SUMMARY,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			if (loFyBudget == null)
			{
				return new BudgetDetails();
			}
			else
			{
				BigDecimal loActPaidAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession,
						aoContractList.getBudgetId(), HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
						HHSConstants.INV_FETCH_FY_BUDGET_ACTUAL_PAID, HHSConstants.JAVA_LANG_STRING);

				loFyBudget.setYtdActualPaid(loActPaidAmount);
				loFyBudget.setRemainingAmount(loFyBudget.getApprovedBudget()
						.subtract(loFyBudget.getYtdInvoicedAmount()));
				loFyBudget.setCashBalance(loFyBudget.getApprovedBudget().subtract(loActPaidAmount));
			}
			// Starts R6 : Returned Payment - to display unrecouped amount in
			// fiscal year budget table
			String unRecoupedAmount = (String) DAOUtil.masterDAO(aoMybatisSession, aoContractList.getBudgetId(),
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.GET_UNRECOUPED_AMOUNT,
					HHSConstants.JAVA_LANG_STRING);

			loFyBudget.setUnRecoupedAmount(unRecoupedAmount);
			// Ends R6 : Returned Payment
		}
		// Application Exception handled here
		catch (ApplicationException aoAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			aoAppEx.addContextData(HHSConstants.CLC_CONTRACT_ID, loHashMap);
			LOG_OBJECT.Error("Exception occured while retrieveing Fiscal Year Contract Information in InvoiceService ",
					aoAppEx);
			setMoState("Transaction Failed:: InvoiceService: fetchContractInvoiceInformation method - failed to fetch"
					+ loHashMap + " \n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			LOG_OBJECT.Error("Exception occured while retrieveing Fiscal Year Contract Information in InvoiceService ",
					aoEx);
			setMoState("Transaction Failed:: InvoiceService: fetchContractInvoiceInformation method - failed to fetch"
					+ loHashMap + " \n");
			throw new ApplicationException("Error occured while retrieving Fiscal Year Contract Summary", aoEx);
		}

		return loFyBudget;
	}

	/**
	 * <ul>
	 * <li>This service class is invoked through fetchContractInvoiceSummary
	 * transaction id for Contract Invoice screen</li>
	 * <li>This method fetchContractInvoiceSummary will get the contract
	 * Information on the basis of contractId, budgetId, Invoice Id</li>
	 * <li>Query Used: fetchContractInvoiceSummary</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Session Object
	 * @param aoHashMap HashMap containing ContractId, BudgetId ad InvoiceID
	 * @return loContractList
	 * @throws ApplicationException - ApplicationException object
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public ContractList fetchContractInvoiceSummary(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		ContractList loContractList = null;
		try
		{
			loContractList = (ContractList) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.CI_FETCH_CONTRACT_INVOICE_SUMMARY,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			loContractList.setContractAgencyName(HHSUtil.getAgencyName(loContractList.getContractAgencyName()));

			Integer loContractRestrictCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSR5Constants.FETCH_CONTRACT_FOR_RETRICTED_COUNT_INVOICE, HHSConstants.JAVA_UTIL_HASH_MAP);
			if (loContractRestrictCount != 0)
			{
				loContractList.setContractAccess(false);
			}
		}
		// Application Exception handled here
		catch (ApplicationException aoAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			aoAppEx.addContextData(HHSConstants.CLC_CONTRACT_ID, aoHashMap);
			LOG_OBJECT.Error("Exception occured while retrieveing Contract Information in InvoiceService ", aoAppEx);
			setMoState("Transaction Failed:: InvoiceService: fetchContractInvoiceSummary method - failed to fetch"
					+ aoHashMap + " \n");
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			LOG_OBJECT.Error("Exception occured while retrieveing Contract Information in InvoiceService ", aoExp);
			setMoState("Transaction Failed:: InvoiceService: fetchContractInvoiceSummary method - failed to fetch"
					+ aoHashMap + " \n");
			throw new ApplicationException("Error occured while retrieving Contract Summary", aoExp);
		}

		return loContractList;
	}

	/**
	 * <ul>
	 * <li>This service class is invoked through fetchContractInvoiceInformation
	 * transaction id for Contract budget screen</li>
	 * <li>This method fetchContractInvoiceInformation will get the Fiscal Year
	 * contract Information on the basis of contractId</li>
	 * <li>Query Used: fetchInvoiceAmountAssignment</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Session Object
	 * @param aoHashMap HashMap containing ContractId, BudgetId ad InvoiceID
	 * @return loContractList
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public InvoiceList fetchContractInvoiceInformation(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		InvoiceList loInvoiceList = null;
		HashMap loAmountMap = null;
		try
		{
			loInvoiceList = (InvoiceList) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.CI_FETCH_CONTRACT_INVOICE_INFORMATION,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			loAmountMap = (HashMap<String, Long>) DAOUtil.masterDAO(aoMybatisSession,
					aoHashMap.get(HHSConstants.INVOICE_ID), HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.FETCH_INVOICE_AMOUNT_ASSIGNMENT, HHSConstants.JAVA_LANG_STRING);
			loInvoiceList.setInvoiceValue(String.valueOf(loAmountMap.get(HHSConstants.IS_TOTAL_INVOICE_AMOUNT)));
			loInvoiceList
					.setAssignmentValue(String.valueOf(loAmountMap.get(HHSConstants.IS_ASSIGNMENT_INVOICE_AMOUNT)));
			loInvoiceList.setAdvanceValue(String.valueOf(loAmountMap.get(HHSConstants.HASH_KEY_AMOUNT_RECOUPED)));

			loInvoiceList.setTotalValue(String.valueOf(new BigDecimal(loInvoiceList.getInvoiceValue()).subtract(
					new BigDecimal(loInvoiceList.getAssignmentValue())).subtract(
					new BigDecimal(loInvoiceList.getAdvanceValue()))));
		}
		// Application Exception handled here
		catch (ApplicationException aoAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			aoAppEx.addContextData(HHSConstants.CLC_CONTRACT_ID, aoHashMap);
			LOG_OBJECT.Error("Exception occured while retrieveing Fiscal Year Contract Information in InvoiceService ",
					aoAppEx);
			setMoState("Transaction Failed:: InvoiceService: fetchContractInvoiceInformation method - failed to fetch"
					+ aoHashMap + " \n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			LOG_OBJECT.Error("Exception occured while retrieveing Fiscal Year Contract Information in InvoiceService ",
					aoEx);
			setMoState("Transaction Failed:: InvoiceService: fetchContractInvoiceInformation method - failed to fetch"
					+ aoHashMap + " \n");
			throw new ApplicationException("Error occured while retrieving Fiscal Year Contract Summary", aoEx);
		}

		return loInvoiceList;
	}

	/**
	 * <ul>
	 * This method is for R 3.7.0 enhancement #
	 * <li>This method provides a Invoice Start date in for Invoice Review Task
	 * through fetchContractInvoiceInfoList transaction id in order for</li>
	 * <li>This method fetchContractInvoiceInfo will get a list of the Fiscal
	 * Year contract Information on the basis of contractId</li>
	 * <li>Query Used: fetchContractInvoiceInfoList</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Session Object
	 * @param aoHashMap HashMap containing Multi InvoiceIDs
	 * @return List<InvoiceList>
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<InvoiceList> fetchContractInvoiceInfoList(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		List<InvoiceList> loInvoiceList = null;
		HashMap loAmountMap = null;
		try
		{
			loInvoiceList = (List<InvoiceList>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.CI_FETCH_CONTRACT_INVOICE_INFO_LIST,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		// Application Exception handled here
		catch (ApplicationException aoAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			aoAppEx.addContextData(HHSConstants.CLC_CONTRACT_ID, aoHashMap);
			LOG_OBJECT.Error("Exception occured while retrieveing Fiscal Year Contract Information in InvoiceService ",
					aoAppEx);
			setMoState("Transaction Failed:: InvoiceService: fetchContractInvoiceInformation method - failed to fetch"
					+ aoHashMap + " \n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			LOG_OBJECT.Error("Exception occured while retrieveing Fiscal Year Contract Information in InvoiceService ",
					aoEx);
			setMoState("Transaction Failed:: InvoiceService: fetchContractInvoiceInformation method - failed to fetch"
					+ aoHashMap + " \n");
			throw new ApplicationException("Error occured while retrieving Fiscal Year Contract Summary", aoEx);
		}

		return loInvoiceList;
	}

	/**
	 * This method updates the invoice status from the "Pending Submission" or
	 * "Returned for Revision" to "Pending Approval" on successful
	 * "WF305 - Invoice Review" workflow launch . <li>Query Used:
	 * updateInvoiceStatus</li>
	 * 
	 * @param aoMyBatisSession : MyBatis session for SQL Session
	 * @param aoHmInvoiceRequiredProps : contains the input parameter for Query
	 *            execution
	 * @return loInvoiceStatusUpdated : returns the updated status; TRUE in case
	 *         of successful update or FALSE otherwise.
	 * @throws ApplicationException :Application Exception
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public Boolean updateInvoiceStatus(SqlSession aoMyBatisSession, HashMap aoHmInvoiceRequiredProps)
			throws ApplicationException
	{
		Boolean loInvoiceStatusUpdated = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoHmInvoiceRequiredProps, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.IC_UPD_INVOICE_STATUS, HHSConstants.JAVA_UTIL_HASH_MAP);
			setMoState("Invoice status updated succesfully");
			loInvoiceStatusUpdated = true;
		}
		//
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		//
		catch (ApplicationException aoAppEx)
		{
			setMoState("Invoice status not updated");
			aoAppEx.setContextData(aoHmInvoiceRequiredProps);
			LOG_OBJECT.Error("Invoice status not updated for invoice Id : ", aoAppEx);
			throw aoAppEx;
		}
		return loInvoiceStatusUpdated;
	}

	/**
	 * This method is used to fetch the current budget status
	 * 
	 * <ul>
	 * <li>budget status will fetch on the basis of budgetId</li>
	 * <li>Query Used: fetchCurrInvoiceStatus</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Session Object
	 * @param aoInvoiceId :InvoiceId
	 * @return lsbudgetStatus
	 * @throws ApplicationException :Application Exception
	 */
	public String fetchCurrInvoiceStatus(SqlSession aoMyBatisSession, String aoInvoiceId) throws ApplicationException
	{
		String lsInvoiceStatus = HHSConstants.EMPTY_STRING;
		try
		{
			lsInvoiceStatus = (String) DAOUtil.masterDAO(aoMyBatisSession, aoInvoiceId,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_CURRENT_INVOICE_STATUS,
					HHSConstants.JAVA_LANG_STRING);
		}

		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("Invoice-ID : ", aoInvoiceId);
			LOG_OBJECT.Error("Exception occured while retrieving in InvoiceService ", loAppEx);

			setMoState("Transaction Failed:: InvoiceService:fetchCurrInvoiceStatus method - failed to fetch record "
					+ " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in fetchCurrentCBStatus ", loAppEx);
			setMoState("Transaction Failed:: InvoiceService:fetchCurrInvoiceStatus method - failed to fetch record "
					+ " \n");
			throw new ApplicationException("Exception occured while fetch in InvoiceService ", loAppEx);
		}

		return lsInvoiceStatus;
	}

	/**
	 * <ul>
	 * This method fetches Agency Id from contract on the basis of Contract Id
	 * <li>Query Used: getAgencyIdByContractForWF</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession :Session Object
	 * @param asContractId ContractId
	 * @return String AgencyId
	 * @throws ApplicationException : Application Exception
	 */
	public String getAgencyIdByContractForWF(SqlSession aoMyBatisSession, String asContractId)
			throws ApplicationException
	{
		String lsAgencyId = null;
		try
		{
			lsAgencyId = (String) DAOUtil.masterDAO(aoMyBatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_AGENCY_ID_FRM_CONTRACT,
					HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("Contract ID : ", asContractId);
			LOG_OBJECT.Error("Exception occured while retrieving in getAgencyIdByContractForWF ", loAppEx);

			setMoState("Execution failed : getAgencyIdByContractForWF");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in getAgencyIdByContractForWF ", loAppEx);
			setMoState("Transaction Failed:: InvoiceService:getAgencyIdByContractForWF method - failed to fetch record "
					+ " \n");
			throw new ApplicationException("Exception occured while fetch in InvoiceService ", loAppEx);
		}
		return lsAgencyId;
	}

	/**
	 * This method update/Insert invoice details for Fringe Benefits. If there
	 * is already invoice exist for line item in invoice details tables invoice
	 * amount update else new record is inserted in table
	 * 
	 * <ul>
	 * <li>Set Line item id,Inovice Id etc in HashMap object</li>
	 * <li>Get total remainig Invoice ammount</li>
	 * <li>If Invoice amount is less than remaining amount call
	 * insertUpdateInvoiceDetail method</li>
	 * <li>Else throw error message</li>
	 * <li>Query Used: fetchPersonnelAmount</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SQL session
	 * @param aoPersonnelServiceBudget Personnel bean object
	 * @return boolean boolean value stating success or failure of transaction
	 * @throws ApplicationException : Application Exception
	 */
	public Boolean editEmployeeInvoice(SqlSession aoMybatisSession, PersonnelServiceBudget aoPersonnelServiceBudget)
			throws ApplicationException
	{
		HashMap<String, Object> loQueryParam = new HashMap<String, Object>();
		boolean lbError = false;
		boolean lbStatus = false;
		BigDecimal loAmount = BigDecimal.ZERO;
		try
		{
			loQueryParam.put(HHSConstants.ID, aoPersonnelServiceBudget.getId());
			loQueryParam.put(HHSConstants.INVOICE_ID, aoPersonnelServiceBudget.getInvoiceId());
			loQueryParam.put(HHSConstants.ENTRY_TYPE_ID, HHSConstants.ONE);
			loQueryParam.put(HHSConstants.IS_LINE_ITEM_INVOICE_AMT, aoPersonnelServiceBudget.getInvoicedAmount());
			loQueryParam.put(HHSConstants.MODIFIED_BY_AGENCY, aoPersonnelServiceBudget.getModifyByAgency());
			loQueryParam.put(HHSConstants.MODIFIED_BY_PROVIDER, aoPersonnelServiceBudget.getModifyByProvider());
			loQueryParam.put(HHSConstants.SUB_BUDGET_ID, aoPersonnelServiceBudget.getSubBudgetID());

			loAmount = new BigDecimal(aoPersonnelServiceBudget.getInvoicedAmount());
			BigDecimal loRemainingAmount = BigDecimal.ZERO;
			// Start Enhancement 6535 Release 3.8.0
			aoPersonnelServiceBudget.setInvoiceAmountCurrent(aoPersonnelServiceBudget.getInvoicedAmount());
			aoPersonnelServiceBudget.setTableAmountColumn(HHSConstants.GET_AMOUNT_DETAILS
					.get(HHSConstants.PERSONNEL_SERVICE));
			aoPersonnelServiceBudget.setTableName(HHSConstants.PERSONNEL_SERVICE);
			aoPersonnelServiceBudget.setTableId(HHSConstants.GET_ID_DETAILS.get(HHSConstants.PERSONNEL_SERVICE));
			aoPersonnelServiceBudget.setEntryTypeId(HHSConstants.ONE);
			aoPersonnelServiceBudget.setLineItemId(aoPersonnelServiceBudget.getId());
			BigDecimal loRemainingAmountPaymentDisbursed = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession,
					aoPersonnelServiceBudget, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.FETCH_RATE_REMAINING_PAYMENT_DISBURSED, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			BigDecimal loFYBudgetLineItem = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudget,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_FY_BUDGET_LINE_ITEM,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// End Enhancement 6535 Release 3.8.0
			loRemainingAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudget,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_PERSONNEL_AMOUNT,
					HHSConstants.PERSONNEL_SERVICE_BUDGET);
			// Start Enhancement 6535 Release 3.8.0
			loRemainingAmount = loRemainingAmount.subtract(loRemainingAmountPaymentDisbursed);
			// End Enhancement 6535 Release 3.8.0
			// postive invoices
			if (new BigDecimal(aoPersonnelServiceBudget.getInvoicedAmount()).compareTo(new BigDecimal(0)) >= 0)
			{
				if (null != loRemainingAmount && loRemainingAmount.compareTo(loAmount) >= 0)
				{

					insertUpdateInvoiceDetail(aoMybatisSession, loQueryParam);
					lbStatus = true;

				}
				else
				{
					lbError = true;
					lbStatus = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.MSG_KEY_INVOICE_AMOUNT_MORE_THAN_REMAINING_AMOUNT));

				}
			}
			else
			{

				// Start Enhancement 6535 Release 3.8.0
				// For invoicing less than zero scenarios
				if ((loFYBudgetLineItem.compareTo(loRemainingAmount.subtract(new BigDecimal(aoPersonnelServiceBudget
						.getInvoicedAmount())))) >= 0)
				{

					// Insert new row of invoice details against this line item
					InvoiceService.insertUpdateInvoiceDetail(aoMybatisSession, loQueryParam);

				}
				else
				{
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.REMAINING_AMOUNT_LESS_THAN_FY_BUDGET));

				}
				// End Enhancement 6535 Release 3.8.0

			}

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			if (lbError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			setMoState("Transaction Failed:: InvoiceService: editEmployeeInvoice method - failed to update INVOICE_DETAIL"
					+ aoPersonnelServiceBudget.getId() + " \n");
			aoAppEx.addContextData("Exception occured while updating Utilities ", aoAppEx);
			throw aoAppEx;
		}
		return lbStatus;
	}

	/**
	 * This method update/Insert invoice details for Fringe Benefits. If there
	 * is already invoice exist for line item in invoice details tables invoice
	 * amount update else new record is inserted in table
	 * <ul>
	 * <li>Set Line item id,Inovice Id etc in HashMap object</li>
	 * <li>Get total remainig Invoice ammount</li>
	 * <li>If Invoice amount is less than remaining amount call
	 * insertUpdateInvoiceDetail method</li>
	 * <li>Else throw error message</li>
	 * <li>Query Used: fetchPersonnelFringeAmount</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SQL session
	 * @param aoPersonnelServiceBudget Personnel bean object
	 * @return boolean boolean value stating success or failure of transaction
	 * @throws ApplicationException : Application Exception
	 */
	public Boolean editFringeBenefits(SqlSession aoMybatisSession, PersonnelServiceBudget aoPersonnelServiceBudget)
			throws ApplicationException
	{
		HashMap<String, Object> loQueryParam = new HashMap<String, Object>();
		boolean lbError = false;
		boolean lbStatus = false;
		BigDecimal loAmount = BigDecimal.ZERO;
		try
		{
			loQueryParam.put(HHSConstants.ID, aoPersonnelServiceBudget.getId());
			loQueryParam.put(HHSConstants.INVOICE_ID, aoPersonnelServiceBudget.getInvoiceId());
			loQueryParam.put(HHSConstants.ENTRY_TYPE_ID, HHSConstants.STRING_THIRTEEN);
			loQueryParam.put(HHSConstants.IS_LINE_ITEM_INVOICE_AMT, aoPersonnelServiceBudget.getInvoicedAmount());
			loQueryParam.put(HHSConstants.MODIFIED_BY_AGENCY, aoPersonnelServiceBudget.getModifyByAgency());
			loQueryParam.put(HHSConstants.MODIFIED_BY_PROVIDER, aoPersonnelServiceBudget.getModifyByProvider());
			loQueryParam.put(HHSConstants.SUB_BUDGET_ID, aoPersonnelServiceBudget.getSubBudgetID());
			loAmount = new BigDecimal(aoPersonnelServiceBudget.getInvoicedAmount());
			// Start Enhancement 6535 Release 3.8.0
			aoPersonnelServiceBudget.setInvoiceAmountCurrent(aoPersonnelServiceBudget.getInvoicedAmount());
			aoPersonnelServiceBudget.setTableAmountColumn(HHSConstants.GET_AMOUNT_DETAILS
					.get(HHSConstants.FRINGE_BENEFIT));
			aoPersonnelServiceBudget.setTableName(HHSConstants.FRINGE_BENEFIT);
			aoPersonnelServiceBudget.setTableId(HHSConstants.GET_ID_DETAILS.get(HHSConstants.FRINGE_BENEFIT));
			aoPersonnelServiceBudget.setEntryTypeId(HHSConstants.STRING_THIRTEEN);
			aoPersonnelServiceBudget.setLineItemId(aoPersonnelServiceBudget.getId());
			BigDecimal loRemainingAmountPaymentDisbursed = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession,
					aoPersonnelServiceBudget, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.FETCH_RATE_REMAINING_PAYMENT_DISBURSED, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			BigDecimal loFYBudgetLineItem = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudget,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_FY_BUDGET_LINE_ITEM,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// End Enhancement 6535 Release 3.8.0
			BigDecimal loRemainingAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoPersonnelServiceBudget,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_PERSONNEL_FRINGE_AMOUNT,
					HHSConstants.PERSONNEL_SERVICE_BUDGET);
			// Start Enhancement 6535 Release 3.8.0
			loRemainingAmount = loRemainingAmount.subtract(loRemainingAmountPaymentDisbursed);
			// End Enhancement 6535 Release 3.8.0
			if (null == loRemainingAmount)
			{
				loRemainingAmount = BigDecimal.ZERO;
			}
			// positive invoices
			if (new BigDecimal(aoPersonnelServiceBudget.getInvoicedAmount()).compareTo(new BigDecimal(0)) >= 0)
			{
				if (loRemainingAmount.compareTo(loAmount) >= 0)
				{

					insertUpdateInvoiceDetail(aoMybatisSession, loQueryParam);
					lbStatus = true;

				}
				else
				{
					lbError = true;
					lbStatus = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.MSG_KEY_INVOICE_AMOUNT_MORE_THAN_REMAINING_AMOUNT));

				}
			}
			// negative invoices
			else
			{

				// Start Enhancement 6535 Release 3.8.0
				// For invoicing less than zero scenarios
				if ((loFYBudgetLineItem.compareTo(loRemainingAmount.subtract(new BigDecimal(aoPersonnelServiceBudget
						.getInvoicedAmount())))) >= 0)
				{
					// Insert new row of invoice details against this line item
					InvoiceService.insertUpdateInvoiceDetail(aoMybatisSession, loQueryParam);

				}
				else
				{
					lbError = true;

					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.REMAINING_AMOUNT_LESS_THAN_FY_BUDGET));

				}
				// End Enhancement 6535 Release 3.8.0
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			if (lbError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			setMoState("Transaction Failed:: InvoiceService: editEmployeeInvoice method - failed to update INVOICE_DETAIL"
					+ aoPersonnelServiceBudget.getId() + " \n");
			aoAppEx.addContextData("Exception occured while updating Utilities ", aoAppEx);
			throw aoAppEx;
		}
		return lbStatus;
	}

	/**
	 * The Method will fetch the budget details of Salaried Employees grid of
	 * Personnel Services tab under contract Invoice module.
	 * <ul>
	 * <li>Set Service Type Id for Salaried employees in Bean object</li>
	 * <li>Call transaction 'fetchSalriedEmployee'</li>
	 * <li>Return list of all Salaried Employees details</li>
	 * <li>Query Used: fetchSalariedEmployeeForRemainingAmt</li>
	 * <li>Query Used: fetchSalariedEmployeeForInvoicedAmt</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SQL session
	 * @param aoPersonnelServiceBudget PersonnelServiceBudget Bean object
	 * @return List of PersonnelServiceBudget bean object
	 * @throws ApplicationException : Application Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PersonnelServiceBudget> fetchSalariedEmployeeBudget(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget) throws ApplicationException
	{

		List<PersonnelServiceBudget> loSalariedEmployessRemainingAmtList = null;
		List<PersonnelServiceBudget> loSalariedEmployessInvoiceAmtList = null;
		try
		{
			aoPersonnelServiceBudget.setPersonnelServiceTypeId(HHSConstants.ONE);
			loSalariedEmployessRemainingAmtList = (List<PersonnelServiceBudget>) DAOUtil.masterDAO(aoMybatisSession,
					aoPersonnelServiceBudget, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.CBY_FETCH_SALRIED_EMPLOYEE_REMAINING_AMT, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			loSalariedEmployessInvoiceAmtList = (List<PersonnelServiceBudget>) DAOUtil.masterDAO(aoMybatisSession,
					aoPersonnelServiceBudget, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.CBY_FETCH_SALRIED_EMPLOYEE_INVOICED_AMT, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			mergePSInvoiceAndRemainingAmt(loSalariedEmployessRemainingAmtList, loSalariedEmployessInvoiceAmtList);
			setMoState("Transaction passed:: InvoiceService: fetchSalariedEmployeeBudget.");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(
					"ApplicationException occured while fetching Salaried Employee budget :  fetchSalariedEmployeeBudget",
					aoAppEx);
			LOG_OBJECT
					.Error("ApplicationException occured while fetching Salaried Employee budget : fetchSalariedEmployeeBudget "
							+ aoAppEx);
			setMoState("ApplicationException occured while fetching Salaried Employee budget for budget id = "
					+ aoPersonnelServiceBudget.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudget.getSubBudgetID());
			throw aoAppEx;
		}
		return loSalariedEmployessRemainingAmtList;
		// }
	}

	/**
	 * The Method will fetch the budget details of Hourly Employees grid of
	 * Personnel Services tab under Invoice module
	 * 
	 * <ul>
	 * <li>Set Service Type Id for Hourly employees in Bean object</li>
	 * <li>Call transaction 'fetchSalriedEmployee'</li>
	 * <li>Return list of all Hourly Employees details</li>
	 * <li>Query Used: fetchHourlyEmployeeForRemainingAmt</li>
	 * <li>Query Used: fetchHourlyEmployeeForInvoicedAmt</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param aoPersonnelServiceBudgetForHourlyEmp Personnel Service Budget bean
	 *            object
	 * @return List of PersonnelServiceBudget bean object
	 * @throws ApplicationException : Application Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PersonnelServiceBudget> fetchHourlyEmployeeBudget(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudgetForHourlyEmp) throws ApplicationException
	{
		List<PersonnelServiceBudget> loHourlyEmployessRemainingAmtList = null;
		List<PersonnelServiceBudget> loHourlyEmployessInvoiceAmtList = null;
		try
		{

			aoPersonnelServiceBudgetForHourlyEmp.setPersonnelServiceTypeId(HHSConstants.TWO);
			loHourlyEmployessRemainingAmtList = (List<PersonnelServiceBudget>) DAOUtil.masterDAO(aoMybatisSession,
					aoPersonnelServiceBudgetForHourlyEmp, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.CBY_FETCH_HOURLY_EMPLOYEE_REMAINING_AMT, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			loHourlyEmployessInvoiceAmtList = (List<PersonnelServiceBudget>) DAOUtil.masterDAO(aoMybatisSession,
					aoPersonnelServiceBudgetForHourlyEmp, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.CBY_FETCH_HOURLY_EMPLOYEE_INVOICED_AMT, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			mergePSInvoiceAndRemainingAmt(loHourlyEmployessRemainingAmtList, loHourlyEmployessInvoiceAmtList);
			setMoState("Transaction passed:: InvoiceService: fetchHourlyEmployeeBudget.");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(
					"ApplicationException occured while fetching Hourly Employee budget :  fetchHourlyEmployeeBudget",
					aoAppEx);
			LOG_OBJECT
					.Error("ApplicationException occured while fetching Hourly Employee budget : fetchHourlyEmployeeBudget "
							+ aoAppEx);
			setMoState("ApplicationException occured while fetching Salaried Employee budget for budget id = "
					+ aoPersonnelServiceBudgetForHourlyEmp.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudgetForHourlyEmp.getSubBudgetID());
			throw aoAppEx;
		}
		return loHourlyEmployessRemainingAmtList;
	}

	/**
	 * The Method will fetch the budget details of Seasonal Employees grid of
	 * Personnel Services tab under Invoice module
	 * 
	 * <ul>
	 * <li>Set Service Type Id for Seasonal employees in Bean object</li>
	 * <li>Call transaction 'fetchSalriedEmployee'</li>
	 * 
	 * <li>Return list of all Seasonal Employees details</li>
	 * <li>Query Used: fetchSeasonalEmployeeForRemainingAmt</li>
	 * <li>Query Used: fetchSeasonalEmployeeForInvoicedAmt</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession mybatis session
	 * @param aoPersonnelServiceBudget CBGridBean bean object
	 * @return List of PersonnelServiceBudget bean object
	 * @throws ApplicationException : Application Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PersonnelServiceBudget> fetchSeasonalEmployeeBudget(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget) throws ApplicationException
	{

		List<PersonnelServiceBudget> loSalariedEmployessRemainingAmtList = null;
		List<PersonnelServiceBudget> loSalariedEmployessInvoiceAmtList = null;
		try
		{

			aoPersonnelServiceBudget.setPersonnelServiceTypeId(HHSConstants.THREE);
			loSalariedEmployessRemainingAmtList = (List<PersonnelServiceBudget>) DAOUtil.masterDAO(aoMybatisSession,
					aoPersonnelServiceBudget, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.CBY_FETCH_SEASONAL_EMPLOYEE_REMAINING_AMT, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			loSalariedEmployessInvoiceAmtList = (List<PersonnelServiceBudget>) DAOUtil.masterDAO(aoMybatisSession,
					aoPersonnelServiceBudget, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.CBY_FETCH_SEASONAL_EMPLOYEE_INVOICED_AMT, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			mergePSInvoiceAndRemainingAmt(loSalariedEmployessRemainingAmtList, loSalariedEmployessInvoiceAmtList);
			setMoState("Transaction passed:: InvoiceService: fetchSalariedEmployeeBudget.");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(
					"ApplicationException occured while fetching Seasonal Employee budget :  fetchSeasonalEmployeeBudget",
					aoAppEx);
			LOG_OBJECT
					.Error("ApplicationException occured while fetching Seasonal Employee budget : fetchSeasonalEmployeeBudget "
							+ aoAppEx);
			setMoState("ApplicationException occured while fetching Seasonal Employee budget for budget id = "
					+ aoPersonnelServiceBudget.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudget.getSubBudgetID());
			throw aoAppEx;
		}
		return loSalariedEmployessRemainingAmtList;
	}

	/**
	 * The Method will fetch the fringe benefit details of Personnel Services
	 * tab under Invoice module
	 * 
	 * <ul>
	 * <li>Call transaction 'fetchSalriedEmployee'</li>
	 * <li>Return list of all fringe benefit details</li>
	 * <li>Query Used: fetchFringeForRemainingAmt</li>
	 * <li>Query Used: fetchFringeForInvoicedAmt</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession mybatis session
	 * @param aoPersonnelServiceBudget CBGridBean bean object
	 * @return List of PersonnelServiceBudget bean object
	 * @throws ApplicationException : Application Exception
	 */

	@SuppressWarnings("unchecked")
	public List<PersonnelServiceBudget> fetchFringeBenefits(SqlSession aoMybatisSession,
			CBGridBean aoPersonnelServiceBudget) throws ApplicationException
	{
		List<PersonnelServiceBudget> loSalariedEmployessRemainingAmtList = null;
		List<PersonnelServiceBudget> loSalariedEmployessInvoiceAmtList = null;
		try
		{
			loSalariedEmployessRemainingAmtList = (List<PersonnelServiceBudget>) DAOUtil.masterDAO(aoMybatisSession,
					aoPersonnelServiceBudget, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.CBY_FETCH_FRINGE_REMAINING_AMT, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			loSalariedEmployessInvoiceAmtList = (List<PersonnelServiceBudget>) DAOUtil.masterDAO(aoMybatisSession,
					aoPersonnelServiceBudget, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.CBY_FETCH_FRINGE_INVOICED_AMT, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			if (null != loSalariedEmployessRemainingAmtList)
			{
				mergePSInvoiceAndRemainingAmt(loSalariedEmployessRemainingAmtList, loSalariedEmployessInvoiceAmtList);
			}
			if (loSalariedEmployessRemainingAmtList == null
					|| loSalariedEmployessRemainingAmtList.size() == HHSConstants.INT_ZERO
					|| loSalariedEmployessRemainingAmtList.isEmpty())
			{
				loSalariedEmployessRemainingAmtList = new ArrayList<PersonnelServiceBudget>();
				loSalariedEmployessRemainingAmtList.add(new PersonnelServiceBudget());

			}
			setMoState("Transaction passed:: InvoiceService: fetchSalariedEmployeeBudget.");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(
					"ApplicationException occured while fetching fringe benifits budget :  fetchFringeBenifits",
					aoAppEx);
			LOG_OBJECT
					.Error("ApplicationException occured while fetching fringe benifits budget : fetchFringeBenifits "
							+ aoAppEx);
			setMoState("ApplicationException occured while fetching fringe benifits budget for budget id = "
					+ aoPersonnelServiceBudget.getContractBudgetID() + " and subbudgetid = "
					+ aoPersonnelServiceBudget.getSubBudgetID());
			throw aoAppEx;
		}
		return loSalariedEmployessRemainingAmtList;
	}

	/**
	 * This method is triggered to get the Assignment Grid information.
	 * <ul>
	 * <li>A bean is passed, CBGridBean</li>
	 * <li>Query Used: contractInvAssignmentSummary</li>
	 * <li>Query Used: fetchInvoiceAssignmentDetail</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession :Session Object
	 * @param aoCBGridBeanObj
	 * @throws ApplicationException : Application Exception
	 * @return loAssignmentsSummaryBean
	 */
	@SuppressWarnings("unchecked")
	public List<AssignmentsSummaryBean> fetchInvoiceAssignmentSummary(SqlSession aoMybatisSession,
			CBGridBean aoCBGridBeanObj) throws ApplicationException
	{
		List<AssignmentsSummaryBean> loAssignmentsList = null;
		List<AssignmentsSummaryBean> loInvoiceAssignmentDetails = null;

		try
		{
			// Fetch first two columns of Assignment grid - Assignee name and
			// YTD_Assignment Amount
			loAssignmentsList = (List<AssignmentsSummaryBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.CI_FETCH_ASSIGNMENT_SUMMARY,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// Fetch Invoice_AssignmentAmount
			loInvoiceAssignmentDetails = (List<AssignmentsSummaryBean>) DAOUtil.masterDAO(aoMybatisSession,
					aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.CI_FETCH_INVOICE_ASSIGNMET_DETAIL, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// Merge Advance_Assignment Amount to loAssignmentsList
			mergeAssignmentDetails(loAssignmentsList, loInvoiceAssignmentDetails);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("Invoice ID : ", aoCBGridBeanObj.getInvoiceId());
			LOG_OBJECT.Error("Exception occured  fetch at InvoiceService: fetchInvoiceAssignmentSummary() ", loAppEx);
			setMoState("InvoiceService: fetchInvoiceAssignmentSummary() failed to edit at Invoice Id:"
					+ aoCBGridBeanObj.getInvoiceId() + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while edit in InvoiceService ", loAppEx);
			setMoState("Transaction Failed:: InvoiceService: fetchInvoiceAssignmentSummary method - failed to edit"
					+ aoCBGridBeanObj.getInvoiceId() + " \n");
			throw new ApplicationException("Exception occured while edit in InvoiceService ", loAppEx);
		}

		return loAssignmentsList;
	}

	/**
	 * <p>
	 * This method is used to concatenate Assignment Amount in aoInputBeanList
	 * and other Assignment Details in aoResultBeanList
	 * </p>
	 * 
	 * @param aoResultBeanListForMerge List of AssignmentsSummaryBean having
	 *            OperationSupport details
	 * @param aoInputBeanList List of AssignmentsSummaryBean having Assignment
	 *            Amount
	 * 
	 */
	private void mergeAssignmentDetails(List<AssignmentsSummaryBean> aoResultBeanListForMerge,
			List<AssignmentsSummaryBean> aoInputBeanList)
	{

		// Get Invoiced Amount and put in a Map with respective
		// operationSupportID as Key
		Map<String, AssignmentsSummaryBean> loDetailMap = new HashMap<String, AssignmentsSummaryBean>();

		for (AssignmentsSummaryBean loInputIterate : aoInputBeanList)
		{
			loDetailMap.put(loInputIterate.getId(), loInputIterate);
		}
		//
		for (AssignmentsSummaryBean loResultIterateForMerge : aoResultBeanListForMerge)
		{
			// If loDetailMap has entry for
			// loResultIterate.id +"_"+invoiceDetailId
			if (loDetailMap.containsKey(loResultIterateForMerge.getId()))
			{

				loResultIterateForMerge.setInvoiceAmount(loDetailMap.get(loResultIterateForMerge.getId())
						.getInvoiceAmount());
			}
			else
			{
				loResultIterateForMerge.setInvoiceAmount(String.valueOf(HHSConstants.INT_ZERO));
			}
		}

	}

	/**
	 * This is the updateRate() method for updating Rate details for the
	 * particular contract Budget.
	 * 
	 * <ul>
	 * <li>Provider is able to Edit the Rate line items that have previously
	 * been added.</li>
	 * <li>Query Used: invoiceTotal</li>
	 * <li>Query Used: assignmentAmountExceptCurrentLineItem</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Session
	 * @param aoAssignmentsSummaryBean AssignmentSummaryBean
	 * @return Boolean Status of Update operation
	 * @throws ApplicationException : Application Exception
	 */
	public Boolean editInvoiceAssignmentSummary(SqlSession aoMybatisSession,
			AssignmentsSummaryBean aoAssignmentsSummaryBean) throws ApplicationException
	{
		HashMap<String, Object> loQueryParam = new HashMap<String, Object>();
		boolean lbError = Boolean.FALSE;

		try
		{
			BigDecimal loTotalInvoice = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession,
					aoAssignmentsSummaryBean.getInvoiceId(), HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.INVOICE_DETAILS, HHSConstants.JAVA_LANG_STRING);
			BigDecimal loAssignment = (BigDecimal) DAOUtil.masterDAO(aoMybatisSession, aoAssignmentsSummaryBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.ASSIGNEMNT_AMOUNT_EXCEPT_CUREENT_LINEITEM,
					HHSConstants.INPUT_PARAM_CLASS_ASSIGNMENT_BEAN);
			if (loTotalInvoice.compareTo(loAssignment.add(new BigDecimal(aoAssignmentsSummaryBean.getInvoiceAmount()))) >= 0)
			{
				loQueryParam.put(HHSConstants.ID, aoAssignmentsSummaryBean.getId());
				loQueryParam.put(HHSConstants.INVOICE_ID, aoAssignmentsSummaryBean.getInvoiceId());
				loQueryParam.put(HHSConstants.ENTRY_TYPE_ID, HHSConstants.ASSIGNMENT_ENTRY_TYPE_ID);
				loQueryParam.put(HHSConstants.IS_LINE_ITEM_INVOICE_AMT, aoAssignmentsSummaryBean.getInvoiceAmount());
				loQueryParam.put(HHSConstants.MODIFIED_BY_AGENCY, aoAssignmentsSummaryBean.getModifyByAgency());
				loQueryParam.put(HHSConstants.MODIFIED_BY_PROVIDER, aoAssignmentsSummaryBean.getModifyByProvider());
				loQueryParam.put(HHSConstants.SUB_BUDGET_ID, HHSConstants.EMPTY_STRING);
				insertUpdateInvoiceDetail(aoMybatisSession, loQueryParam);
			}
			else
			{

				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.MSG_KEY_INVOICE_EXCEED_ERR_GRIDOPERATION));
			}

		}
		catch (ApplicationException loAppEx)
		{
			if (lbError)
			{
				loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loAppEx.toString());
			}
			loAppEx.addContextData("ID : ", aoAssignmentsSummaryBean.getId());
			LOG_OBJECT.Error("Exception occured  edit at InvoiceService: editInvoiceAssignmentSummary() ", loAppEx);
			setMoState("InvoiceService: updateRate() failed to edit at id:" + aoAssignmentsSummaryBean.getId() + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while edit in InvoiceService ", loAppEx);
			setMoState("Transaction Failed:: InvoiceService: editInvoiceAssignmentSummary method - failed to edit"
					+ aoAssignmentsSummaryBean.getId() + " \n");
			throw new ApplicationException("Exception occured while edit in InvoiceService ", loAppEx);
		}
		return true;
	}

	/**
	 * <p>
	 * This method fetch data for screen S337 invoicing for rate screen.<br/>
	 * Below is mapping of budget type id and its budget type : <br/>
	 * 1 = Budget Amendment; 2 = Contract Budget; 3 = Budget Modification; 4 =
	 * Budget Update <br/>
	 * <b>Functionalities by below method are :</b>
	 * <ul>
	 * <li>1.Fetch data for remaining amount column on screen, get data in
	 * List<RateBean></li>
	 * <li>2.Fetch data for invoice amount column on screen, get data in
	 * List<RateBean></li>
	 * <li>Query Used: fetchInvoiceRateRemainingAmt</li>
	 * <li>Query Used: fetchInvoiceRateInvoiceAmt</li>
	 * <li>This method is updated in R4.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession Sql session object as input.
	 * @param aoCBGridBeanObj CBGridBean Bean as input.
	 * @return List<RateBean> list of RateBean Object
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<RateBean> fetchInvoiceRateGrid(CBGridBean aoCBGridBeanObj, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		List<RateBean> loRateBeanRemainingAmtList = null;
		List<RateBean> loRateBeanInvoiceAmtList = null;
		try
		{
			loRateBeanRemainingAmtList = (List<RateBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.CBI_FETCH_REMAINING_AMOUNT,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			loRateBeanInvoiceAmtList = (List<RateBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.CBI_FETCH_INVOICED_AMOUNT,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			mergeRateInvoiceAndRemainingAmt(loRateBeanRemainingAmtList, loRateBeanInvoiceAmtList);
			setMoState("Transaction passed:: InvoiceService: fetchInvoiceRateGrid.");
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Application Exception occured in InvoiceService: fetchInvoiceRateGrid method.", aoAppEx);
			setMoState("Transaction Failed:: InvoiceService: fetchInvoiceRateGrid method - failed."
					+ " Exception occured while fetching rate list for details aoCBGridBeanObj::" + aoCBGridBeanObj
					+ ". \n");
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in InvoiceService: fetchInvoiceRateGrid method.", aoEx);
			setMoState("Transaction Failed:: InvoiceService: fetchInvoiceRateGrid method - failed."
					+ " Exception occured while fetching rate list for details aoCBGridBeanObj::" + aoCBGridBeanObj
					+ ". \n");
			throw new ApplicationException("Error occured in  InvoiceService: fetchInvoiceRateGrid method:: ", aoEx);
		}
		return loRateBeanRemainingAmtList;
	}

	/**
	 * <p>
	 * This is private method used to merge remaining amount and invoice amount
	 * present in two different list<RateBean> and add it to one List<RateBean>
	 * <ul>
	 * <li>1.Read full remaining amount list<RateBean></li>
	 * <li>2.Get data from invoice list using index and assign it to amount list
	 * </li>
	 * </ul>
	 * <br>
	 * Method update for R4
	 * </p>
	 * 
	 * @param aoRateBeanRemainingAmtList List<RateBean> containing remaining
	 *            amount.
	 * @param aoRateBeanInvoiceAmtList List<RateBean> containing invoice amount.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	private void mergeRateInvoiceAndRemainingAmt(List<RateBean> aoRateBeanRemainingAmtList,
			List<RateBean> aoRateBeanInvoiceAmtList) throws ApplicationException
	{
		int liCount = HHSConstants.INT_ZERO;
		for (RateBean loRateBean : aoRateBeanRemainingAmtList)
		{
			loRateBean.setYtdInvoiceAmt(aoRateBeanInvoiceAmtList.get(liCount).getYtdInvoiceAmt());
			loRateBean.setInvUnits(aoRateBeanInvoiceAmtList.get(liCount).getInvUnits());
			liCount++;
		}
	}

	/**
	 * <p>
	 * This method is used for Invoicing to edit the rows in Contracted Services
	 * grid for a particular sub-budget.
	 * <ul>
	 * <li>1)Provider is able to Edit the Contracted Services line for Invoicing
	 * items</li>
	 * <li>2)It Updates Invoice Amount entered in INVOICE_DETAIL table if there
	 * is an entry in the table for for a particular Invoice Id, Line Item Id,
	 * Entry Type and INSERTS a new row in the table if no entry exists.</li>
	 * <li>Query Used: fetchContractedServicesRemainingAmount</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatiSession :Session Object
	 * @param aoCBGridBeanObj :ContractedServicesBean
	 * @return boolean lbUpdateStatus Returns true if rows are edited otherwise
	 *         returns false.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public boolean editContractedServicesInvoicing(SqlSession aoMybatiSession, ContractedServicesBean aoCBGridBeanObj)
			throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		boolean lbError = false;
		BigDecimal loRemainingAmount = BigDecimal.ZERO;
		HashMap<String, Object> loQueryParam = new HashMap<String, Object>();
		aoCBGridBeanObj.setEntryTypeId(HHSConstants.CS_ENTRY_TYPE);
		aoCBGridBeanObj.setCreatedByUserId(aoCBGridBeanObj.getModifiedByUserId());
		aoCBGridBeanObj.setModifiedByUserId(aoCBGridBeanObj.getModifiedByUserId());
		loQueryParam.put(HHSConstants.ID, aoCBGridBeanObj.getId());
		loQueryParam.put(HHSConstants.INVOICE_ID, aoCBGridBeanObj.getInvoiceId());
		loQueryParam.put(HHSConstants.ENTRY_TYPE_ID, HHSConstants.CS_ENTRY_TYPE);
		loQueryParam.put(HHSConstants.IS_LINE_ITEM_INVOICE_AMT, aoCBGridBeanObj.getInvoiceAmt());
		loQueryParam.put(HHSConstants.MODIFIED_BY_AGENCY, aoCBGridBeanObj.getModifyByAgency());
		loQueryParam.put(HHSConstants.MODIFIED_BY_PROVIDER, aoCBGridBeanObj.getModifyByProvider());
		loQueryParam.put(HHSConstants.SUB_BUDGET_ID, aoCBGridBeanObj.getSubBudgetID());
		BigDecimal loAmount = new BigDecimal(aoCBGridBeanObj.getInvoiceAmt());
		try
		{
			// Start Enhancement 6535 Release 3.8.0
			aoCBGridBeanObj.setInvoiceAmountCurrent(aoCBGridBeanObj.getInvoiceAmt());
			aoCBGridBeanObj.setTableAmountColumn(HHSConstants.GET_AMOUNT_DETAILS.get(HHSConstants.CONTRACTED_SERVICE));
			aoCBGridBeanObj.setTableName(HHSConstants.CONTRACTED_SERVICE);
			aoCBGridBeanObj.setTableId(HHSConstants.GET_ID_DETAILS.get(HHSConstants.CONTRACTED_SERVICE));
			aoCBGridBeanObj.setEntryTypeId(HHSConstants.SIX);
			aoCBGridBeanObj.setLineItemId(aoCBGridBeanObj.getId());
			BigDecimal loRemainingAmountPaymentDisbursed = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession,
					aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.FETCH_RATE_REMAINING_PAYMENT_DISBURSED, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			BigDecimal loFYBudgetLineItem = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_FY_BUDGET_LINE_ITEM,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// End Enhancement 6535 Release 3.8.0
			loRemainingAmount = (BigDecimal) DAOUtil.masterDAO(aoMybatiSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.IS_FETCH_CONTRACTED_SERVICES_REMAINING_AMOUNT,
					HHSConstants.CBY_COM_NYC_HHS_MODEL_CONTRACTED_SERVICES_BEAN);
			// Start Enhancement 6535 Release 3.8.0
			loRemainingAmount = loRemainingAmount.subtract(loRemainingAmountPaymentDisbursed);
			// End Enhancement 6535 Release 3.8.0
			if (loAmount.compareTo(new BigDecimal(0)) >= 0)
			{
				if (loRemainingAmount.compareTo(loAmount) >= 0)
				{
					insertUpdateInvoiceDetail(aoMybatiSession, loQueryParam);
				}

				else
				{
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.MSG_KEY_INVOICE_AMOUNT_MORE_THAN_REMAINING_AMOUNT));

				}
			}
			// for negative invoices
			else
			{
				// Start Enhancement 6535 Release 3.8.0
				// For invoicing less than zero scenarios
				if ((loFYBudgetLineItem.compareTo(loRemainingAmount.subtract(loAmount))) >= 0)
				{
					// Insert new row of invoice details against this line item
					InvoiceService.insertUpdateInvoiceDetail(aoMybatiSession, loQueryParam);

				}
				else
				{
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.REMAINING_AMOUNT_LESS_THAN_FY_BUDGET));

				}
				// End Enhancement 6535 Release 3.8.0
				lbUpdateStatus = true;
			}
		}
		/**
		 * Application Exception Thrown
		 */
		catch (ApplicationException aoAppEx)
		{
			if (lbError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
				setMoState("Invoice Amount entered: " + loAmount + " is greater than remaining amount: "
						+ loRemainingAmount + " \n");
				aoAppEx.addContextData("Invoice Amount entered is greater than remaining amount ", aoAppEx);
			}
			else
			{
				setMoState("Transaction Failed:: InvoiceService: editContractedServicesInvoicing method - failed to update INVOICE_DETAIL"
						+ aoCBGridBeanObj.getId() + " \n");
				aoAppEx.addContextData("Exception occured while updating Contracted Services ", aoAppEx);
			}
			throw aoAppEx;

		}
		return lbUpdateStatus;

	}

	/**
	 * <ul>
	 * <li>This method is used to fetch data for
	 * ContractServices,ContractServices
	 * Invoicing,ContractServicesModification,ContractServicesUpdate &
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
	 * <li>Query Used: fetchContractedServicesInvoicingConsultants</li>
	 * </ul>
	 * </ol>
	 * </ul>
	 * 
	 * @param aoMyBatisSession : Session Object
	 * @param aoCBGridBeanObj : CB Grid Bean Object
	 * @return aoCBContractedServicesBean Returns list of records of type
	 *         consultant.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<ContractedServicesBean> fetchContractedServicesInvoicingConsultants(SqlSession aoMyBatisSession,
			CBGridBean aoCBGridBeanObj) throws ApplicationException
	{
		List<ContractedServicesBean> loCBContractedServicesBean = null;
		try
		{
			aoCBGridBeanObj.setSubHeader(HHSConstants.ONE);
			loCBContractedServicesBean = (List<ContractedServicesBean>) DAOUtil.masterDAO(aoMyBatisSession,
					aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.CBY_FETCH_CONTRACTED_SERVICES_INVOICING_CONSULTANTS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		/**
		 * Application Exception Thrown
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(
					"Exception occured in InvoiceService: fetchContractedServicesInvoicingConsultants method:: ",
					aoAppEx);
			setMoState("Transaction Failed:: InvoiceService: fetchContractedServicesInvoicingConsultants method - failed Exception occured while fetching\n");
			throw aoAppEx;
		}

		return loCBContractedServicesBean;
	}

	/**
	 * This method is used to fetch data for sub-contractors for contracted
	 * services <li>Query Used: fetchContractedServicesInvoicingSubContractors</li>
	 * @param aoMyBatisSession :Session Object
	 * @param aoCBGridBeanObj : CB GridBean
	 * @return loCBContractedServicesBean Returns list of records of type
	 *         Sub-Contractors.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<ContractedServicesBean> fetchContractedServicesInvoicingSubContractors(SqlSession aoMyBatisSession,
			CBGridBean aoCBGridBeanObj) throws ApplicationException
	{
		List<ContractedServicesBean> loCBContractedServicesBean = null;
		try
		{
			aoCBGridBeanObj.setSubHeader(HHSConstants.TWO);
			loCBContractedServicesBean = (List<ContractedServicesBean>) DAOUtil.masterDAO(aoMyBatisSession,
					aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.CBY_FETCH_CONTRACTED_SERVICES_INVOICING_SUB_CONTRACTORS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		/**
		 * Application Exception Thrown
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(
					"Exception occured in InvoiceService: fetchContractedServicesInvoicingSubContractors method:: ",
					aoAppEx);
			setMoState("Transaction Failed:: InvoiceService: fetchContractedServicesInvoicingSubContractors method - failed Exception occured while fetching\n");
			throw aoAppEx;
		}

		return loCBContractedServicesBean;
	}

	/**
	 * This method is used to fetch data for vendors for contracted services <li>
	 * Query Used: fetchContractedServicesInvoicingVendors</li>
	 * @param aoMyBatisSession :Session Object
	 * @param aoCBGridBeanObj CB GridBean Object
	 * @return loCBContractedServicesBean Returns list of records of type
	 *         Vendors.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	public List<ContractedServicesBean> fetchContractedServicesInvoicingVendors(SqlSession aoMyBatisSession,
			CBGridBean aoCBGridBeanObj) throws ApplicationException
	{
		List<ContractedServicesBean> loCBContractedServicesBean = null;
		try
		{
			aoCBGridBeanObj.setSubHeader(HHSConstants.THREE);
			loCBContractedServicesBean = (List<ContractedServicesBean>) DAOUtil.masterDAO(aoMyBatisSession,
					aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.CBY_FETCH_CONTRACTED_SERVICES_INVOICING_VENDORS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		/**
		 * Application Exception Thrown
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in InvoiceService: fetchContractedServicesInvoicingVendors method:: ",
					aoAppEx);
			setMoState("Transaction Failed:: InvoiceService: fetchContractedServicesInvoicingVendors method - failed Exception occured while fetching\n");
			throw aoAppEx;
		}

		return loCBContractedServicesBean;
	}

	/**
	 * Method modified as a part of enhancement 6576 release 3.10.0 to add
	 * additional error check while approving invoice review task.
	 * 
	 * This method does error check on click of Finish Task button on the
	 * Invoice Review Task Screen.
	 * 
	 * <li>Query Used: fetchContractStatus</li> <li>Query Used:
	 * contactStatusSuspendedMsg</li> <li>Query Used:
	 * fetchInvoiceAmountAssignment</li>
	 * 
	 * @param aoMyBatisSession Session Object
	 * @param asInvoiceId :InvoiceId
	 * @param asContractId :ContractId
	 * @param asReviewLevel : Review Level
	 * @return loErrorMap
	 * @throws ApplicationException : Application Exception
	 */

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap<String, Object> errorCheckInvoiceReviewTask(SqlSession aoMyBatisSession, String asInvoiceId,
			String asContractId, String asReviewLevel, String asBudgetId, P8UserSession aoFilenetSession)
			throws ApplicationException
	{
		String lsContractStatus = HHSConstants.EMPTY_STRING;
		HashMap<String, Object> loErrorMap = new HashMap<String, Object>();
		loErrorMap.put(HHSConstants.ERROR_CODE, HHSConstants.ONE);
		loErrorMap.put(HHSConstants.CLC_ERROR_MSG, HHSConstants.EMPTY_STRING);
		try
		{
			lsContractStatus = (String) DAOUtil.masterDAO(aoMyBatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.IS_FETCH_CONTRACT_STATUS,
					HHSConstants.JAVA_LANG_STRING);

			// If Contract Status is equal to Suspended then Error Message is
			// set
			if (lsContractStatus.equalsIgnoreCase(HHSConstants.CONTRACT_SUSPEND_STATUS_ID))
			{
				loErrorMap.put(HHSConstants.ERROR_CODE, HHSConstants.STRING_ZERO);
				loErrorMap.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.INV_CONTRACT_STATUS_SUSPENDED_MSG));
			}
			else if (asReviewLevel.equalsIgnoreCase(HHSConstants.ONE))
			{
				HashMap loAmountMap = null;
				loAmountMap = (HashMap<String, Long>) DAOUtil.masterDAO(aoMyBatisSession, asInvoiceId,
						HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_INVOICE_AMOUNT_ASSIGNMENT,
						HHSConstants.JAVA_LANG_STRING);

				// If Advance Recoupment Total + Assignments Total is NOT less
				// than or equal to Invoice Total then Error Message is set
				if ((new BigDecimal(String.valueOf(loAmountMap.get(HHSConstants.IS_ASSIGNMENT_INVOICE_AMOUNT)))
						.add(new BigDecimal(String.valueOf(loAmountMap.get(HHSConstants.HASH_KEY_AMOUNT_RECOUPED)))))
						.compareTo(new BigDecimal(String.valueOf(loAmountMap.get(HHSConstants.IS_TOTAL_INVOICE_AMOUNT)))) > 0)
				{
					loErrorMap.put(HHSConstants.ERROR_CODE, HHSConstants.STRING_ZERO);
					loErrorMap.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.INV_ASSIGNMENT_AMOUNT_ERROR_MSG));
				}
				// release 3.10.0 enhancement 6576 - start
				HashMap loHmWFProperties = new HashMap();
				loHmWFProperties.put(HHSConstants.PROPERTY_PE_BUDGET_ID, asBudgetId);
				loHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_INVOICE_REVIEW);
				loHmWFProperties.put(HHSConstants.CURR_LEVEL, HHSConstants.CURRENT_LEVEL);
				loHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_VISIBILITY, HHSConstants.ONE);
				List<String> loPendingApprovalInvocies = new P8ProcessServiceForSolicitationFinancials()
						.fetchInvoiceNumbersForPAInvoicesApprovedAtLevel1(aoFilenetSession, loHmWFProperties);
				loPendingApprovalInvocies = !loPendingApprovalInvocies.isEmpty() ? loPendingApprovalInvocies : null;
				Map loMap = new HashMap();
				loMap.put(HHSConstants.INVOICE_ID, asInvoiceId);
				loMap.put(HHSConstants.BUDGET_ID, asBudgetId);
				loMap.put(HHSConstants.INVOICE_LIST, loPendingApprovalInvocies);
				String loBudgetAmtRemaining = (String) DAOUtil.masterDAO(aoMyBatisSession, loMap,
						HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.PREVENT_PAYMENT_OVER_BUDGET_TOTAL,
						HHSConstants.JAVA_UTIL_MAP);
				Double loBudgetAmtRemainingInDouble = Double.parseDouble(loBudgetAmtRemaining);
				if (loBudgetAmtRemainingInDouble < 0)
				{
					loErrorMap.put(HHSConstants.ERROR_CODE, HHSConstants.STRING_ZERO);
					loErrorMap.put(HHSConstants.CLC_ERROR_MSG, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.PREVENT_PAYMENT_OVER_BUDGET_TOTAL));
				}
				// release 3.10.0 enhancement 6576 - end
			}

		}
		// ApplicationException handled here
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("Invoice-ID : ", asInvoiceId);
			LOG_OBJECT.Error("Exception occured while retrieving in InvoiceService ", aoAppEx);

			setMoState("Transaction Failed:: InvoiceService:errorCheckInvoiceReviewTask method - failed to fetch record "
					+ " \n");
			throw aoAppEx;
		}
		// Exception handled here
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in errorCheckInvoiceReviewTask ", aoEx);
			setMoState("Transaction Failed:: InvoiceService:errorCheckInvoiceReviewTask method - failed to fetch record "
					+ " \n");
			throw new ApplicationException("Exception occured while fetch in InvoiceService ", aoEx);
		}

		return loErrorMap;
	}

	/**
	 * This method is used to set budget status on final Approve and returned
	 * for revision in case of contract Review Task.
	 * <ul>
	 * <li>IUpdate the status of the budget</li>
	 * <li>Query Used: setStatusForInvoiceReviewTask</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Session Object
	 * @param abFinalFinish :Final Finish
	 * @param aoTaskDetailsBean : Task Detail Bean
	 * @param asBudgetStatus :BUdget Status
	 * @throws ApplicationException : Application Exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void setStatusForInvoiceReviewTask(SqlSession aoMyBatisSession, Boolean abFinalFinish,
			TaskDetailsBean aoTaskDetailsBean, String asBudgetStatus) throws ApplicationException
	{
		try
		{
			if (abFinalFinish)
			{
				Map loHashMap = new HashMap<String, String>();
				loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoTaskDetailsBean.getContractId());
				loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoTaskDetailsBean.getBudgetId());
				loHashMap.put(HHSConstants.STATUS_ID, asBudgetStatus);
				loHashMap.put(HHSConstants.MODIFY_BY, aoTaskDetailsBean.getUserId());
				loHashMap.put(HHSConstants.INVOICE_ID, aoTaskDetailsBean.getInvoiceId());
				DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.CBY_SET_STATUS_FOR_INVOICE_REVIEW_TASK, HHSConstants.JAVA_UTIL_MAP);
				
				/* QC9710 */
				DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.UPDATE_INVOICE_ADVANCE_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);
				
				/* QC9721 */
				DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.UPDATE_INVOICE_DETAILS_MODIFIED_DATE, HHSConstants.JAVA_UTIL_MAP);				
								
				// changes for agency outbound interafce
				DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.UPDATE_MODIFIED_DATE_ASSIGNMENT, HHSConstants.JAVA_UTIL_MAP);
				// changes for agency outbound interafce
				setMoState("Transaction Success:: InvoiceService:setStatusForInvoiceReviewTask method - success to update record\n");
			}

		}
		// ApplicationException handled here
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("BudgetId passed: ", aoTaskDetailsBean.getBudgetId());
			LOG_OBJECT.Error("ApplicationException occured while executing query setStatusForInvoiceReviewTask ", aoAppEx);
			setMoState("Transaction Failed:: InvoiceService:setStatusForInvoiceReviewTask method - failed to update record\n");
			throw aoAppEx;
		}
		// Exception handled here
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in setStatusForInvoiceReviewTask ", aoAppEx);
			setMoState("Transaction Failed:: InvoiceService:setStatusForInvoiceReviewTask method - failed to update record\n");
			ApplicationException loAppEx = new ApplicationException("Exception occured while executing query in setStatusForInvoiceReviewTask", aoAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method is used to fetch the current budget status
	 * 
	 * <ul>
	 * <li>budget status will fetch on the basis of budgetId</li>
	 * <li>Query Used: fetchInvoiceAmountAssignment</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Session Object
	 * @param asInvoiceId InvoiceId
	 * @return Boolean lsbudgetStatus
	 * @throws ApplicationException : Application Exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Boolean fetchCurrentAssignmentStatus(SqlSession aoMyBatisSession, String asInvoiceId)
			throws ApplicationException
	{
		Boolean lbAssignment = true;
		try
		{
			HashMap loAmountMap = null;
			loAmountMap = (HashMap<String, Long>) DAOUtil.masterDAO(aoMyBatisSession, asInvoiceId,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_INVOICE_AMOUNT_ASSIGNMENT,
					HHSConstants.JAVA_LANG_STRING);
			if (new BigDecimal((String) loAmountMap.get(HHSConstants.IS_ASSIGNMENT_INVOICE_AMOUNT))
					.compareTo(new BigDecimal((String) loAmountMap.get(HHSConstants.IS_TOTAL_INVOICE_AMOUNT))) > 0)
			{
				lbAssignment = false;
			}
		}

		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("Invoice-ID : ", asInvoiceId);
			LOG_OBJECT.Error("Exception occured while fetchCurrentAssignmentStatus in InvoiceService ", loAppEx);

			setMoState("Transaction Failed:: InvoiceService:fetchCurrentAssignmentStatus method - failed to fetch record "
					+ " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in fetchCurrentAssignmentStatus ", loAppEx);
			setMoState("Transaction Failed:: InvoiceService:fetchCurrentAssignmentStatus method - failed to fetch record "
					+ " \n");
			throw new ApplicationException("Exception occured while fetchCurrentAssignmentStatus in InvoiceService ",
					loAppEx);
		}

		return lbAssignment;
	}

	/**
	 * This method is used to save AgencyInvoiceNumber from contract invoice
	 * Review task Screen
	 * 
	 * <ul>
	 * <li>AgencyInvoiceNumber is updated</li>
	 * <li>Query Used: insertAgencyInvoiceNumber</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Session Object
	 * @param aoInvoiceMap Containing invoice and contract information.
	 * @throws ApplicationException : Application Exception
	 */

	@SuppressWarnings(
	{ "rawtypes" })
	public void saveAgencyInvoiceNumber(SqlSession aoMyBatisSession, Map aoInvoiceMap) throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoInvoiceMap, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.IS_INSERT_AGENCY_INVOICE_NUMBER, HHSConstants.JAVA_UTIL_MAP);
			setMoState("Transaction Success:: InvoiceService:saveAgencyInvoiceNumber method - success to update record "
					+ " \n");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("Invoice-ID : ", aoInvoiceMap.get(HHSConstants.INVOICE_ID));
			LOG_OBJECT.Error("Exception occured while executing query in insertAgencyInvoiceNumber ", loAppEx);

			setMoState("Transaction Failed:: InvoiceService:saveAgencyInvoiceNumber method - failed to insert record "
					+ " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in insertAgencyInvoiceNumber ", loAppEx);
			setMoState("Transaction Failed:: InvoiceService:saveAgencyInvoiceNumber method - failed to insert record "
					+ " \n");
			throw new ApplicationException("Exception occured while saveAgencyInvoiceNumber in InvoiceService ",
					loAppEx);
		}

	}

	/**
	 * This method is used to fetch Assignment and advance amount information
	 * 
	 * <ul>
	 * <li>Assignment and Advance information is fetch on the basis of contract
	 * Id</li>
	 * <li>Query Used: fetchContractInvoiceInformation</li>
	 * <li>Query Used: fetchInvoiceAmountAssignment</li>
	 * 
	 * @param aoMyBatisSession Session Object
	 * @param aoInvoiceMap Containing invoice and contract information.
	 * @return InvoiceList
	 * @throws ApplicationException : Application Exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public InvoiceList fetchInvoiceInfo(SqlSession aoMyBatisSession, Map aoInvoiceMap) throws ApplicationException
	{
		InvoiceList loInvoiceList = null;
		HashMap loAmountMap = null;
		try
		{
			loInvoiceList = (InvoiceList) DAOUtil.masterDAO(aoMyBatisSession, aoInvoiceMap,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.CI_FETCH_CONTRACT_INVOICE_INFORMATION,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			loAmountMap = (HashMap<String, Long>) DAOUtil.masterDAO(aoMyBatisSession,
					aoInvoiceMap.get(HHSConstants.INVOICE_ID), HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.FETCH_INVOICE_AMOUNT_ASSIGNMENT, HHSConstants.JAVA_LANG_STRING);
			loInvoiceList.setInvoiceValue(String.valueOf(loAmountMap.get(HHSConstants.IS_TOTAL_INVOICE_AMOUNT)));
			loInvoiceList
					.setAssignmentValue(String.valueOf(loAmountMap.get(HHSConstants.IS_ASSIGNMENT_INVOICE_AMOUNT)));
			loInvoiceList.setAdvanceValue(String.valueOf(loAmountMap.get(HHSConstants.HASH_KEY_AMOUNT_RECOUPED)));

			loInvoiceList.setTotalValue(String.valueOf(new BigDecimal(loInvoiceList.getInvoiceValue()).subtract(
					new BigDecimal(loInvoiceList.getAssignmentValue())).subtract(
					new BigDecimal(loInvoiceList.getAdvanceValue()))));
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("Invoice-ID : ", aoInvoiceMap.get(HHSConstants.INVOICE_ID));
			LOG_OBJECT.Error("Exception occured while executing query in fetchInvoiceAmountAssignment ", loAppEx);

			setMoState("Transaction Failed:: InvoiceService:fetchInvoiceInfo method - failed to insert record " + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in fetchInvoiceAmountAssignment ", loAppEx);
			setMoState("Transaction Failed:: InvoiceService:fetchInvoiceInfo method - failed to insert record " + " \n");
			throw new ApplicationException("Exception occured while fetchInvoiceInfo in InvoiceService ", loAppEx);
		}
		return loInvoiceList;
	}

	/**
	 * This method is used to fetch VendorList to populate AutoComplete text-box
	 * for add Assignee <li>Query Used: fetchVendorList</li>
	 * @param aoMybatisSession :Session Object
	 * @return List<AutoCompleteBean> containing Vendor List
	 * @throws ApplicationException : Application Exception
	 */
	@SuppressWarnings("unchecked")
	public List<AutoCompleteBean> fetchVendorList(SqlSession aoMybatisSession, String aoVendorName)
			throws ApplicationException
	{
		List<AutoCompleteBean> loVendorList = null;
		try
		{
			String loVendor = (new StringBuffer(aoVendorName).append("%")).toString();
			loVendorList = (List<AutoCompleteBean>) DAOUtil.masterDAO(aoMybatisSession, loVendor,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_VENDOR_LIST,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Vendor list retrieved successfully for Add Assignee");
		}
		// handling Application Exception
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Application Exception Occured while getting the Vendor list from the database", aoAppExp);
			setMoState("Application Exception Occured while getting the Vendor list from the database");
			throw new ApplicationException(
					"Application Exception Occured while getting the Vendor list from the database" + aoAppExp);
		}
		return loVendorList;
	}

	/**
	 * This method is used to fetch ProviderList to populate AutoComplete
	 * text-box for ProviderName.
	 * 
	 * @param aoMybatisSession :Session Object
	 * @return List<AutoCompleteBean> containing Provider List
	 * @throws ApplicationException : Application Exception
	 */
	@SuppressWarnings("unchecked")
	public List<AutoCompleteBean> fetchProviderList(SqlSession aoMybatisSession, String aoProviderName)
			throws ApplicationException
	{
		List<AutoCompleteBean> loProviderList = null;
		try
		{
			StringBuffer lsProvider = new StringBuffer();
			lsProvider.append("%");
			lsProvider.append(aoProviderName);
			String loProviderName = lsProvider.append("%").toString();
			loProviderList = (List<AutoCompleteBean>) DAOUtil.masterDAO(aoMybatisSession, loProviderName,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_PROVIDER_LIST,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Provider list retrieved successfully");
		}
		// handling Application Exception
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Application Exception Occured while getting the Provider list from the database",
					aoAppExp);
			setMoState("Application Exception Occured while getting the Provider list from the database");
			throw new ApplicationException(
					"Application Exception Occured while getting the Provider list from the database" + aoAppExp);
		}
		return loProviderList;
	}

	/**
	 * THis method validates an Assignee, Returns true if assignee does not
	 * exist in DB and false if it already exist It returns true if Assignee is
	 * valid and does not exist It returns false if assignee already exist in DB
	 * 
	 * @param aoMyBatisSession Session Object
	 * @param asVendorId Vendor Id
	 * @param asBudgetId Budget Id
	 * @return Boolean, validation result: true if vendor can be added, false if
	 *         vendor already exist so cannot be added
	 * @throws ApplicationException : Application Exception
	 */
	public Boolean validateAssignee(SqlSession aoMyBatisSession, String asVendorId, String asBudgetId)
			throws ApplicationException
	{
		Boolean lbValid = false;

		try
		{
			Map<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.S431_CHANNEL_VENDOR, asVendorId);
			loHashMap.put(HHSConstants.BUDGET_ID_KEY, asBudgetId);

			int liRow = HHSConstants.INT_ZERO;

			liRow = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.VALIDATE_ASSIGNEE, HHSConstants.JAVA_UTIL_HASH_MAP);
			setMoState("Organization Info retrieved successfully");

			if (liRow <= HHSConstants.INT_ZERO)
			{
				// Assignment not exist, valid candidate for addition to DB

				lbValid = true;

			}
		}
		// handling ApplicationException if Vendor cannot be validated
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Application Exception Occured in validateAssignee", aoAppExp);
			setMoState("Could not validate :" + asVendorId + " as Assignee for BudgetId " + asBudgetId);
			throw new ApplicationException(
					"Exception occured while validating Organization as Assignment in addAssigneeForBudget " + aoAppExp);

		}
		// handling other Exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception Occured in validateAssignee", aoExp);
			setMoState("Could not validate :" + asVendorId + " as Assignee for BudgetId " + asBudgetId);
			throw new ApplicationException(
					"Exception occured while validating Organization as Assignment in addAssigneeForBudget " + aoExp);

		}

		return lbValid;

	}

	/**
	 * This method adds Assignment to Database. <li>Query Used: addAssignment</li>
	 * @param aoMyBatisSession Session Object
	 * @param asVendorId Vendor Id
	 * @param asBudgetId BudgetId
	 * @param aoCBGridBean : GridBean with UserId information
	 * @return Boolean insert operation result, true for success and false for
	 *         failure
	 * @throws ApplicationException : Application Exception
	 */
	public Boolean addAssigneeForBudget(SqlSession aoMyBatisSession, String asVendorId, String asBudgetId,
			CBGridBean aoCBGridBean) throws ApplicationException
	{
		Boolean loSuccessStatus = Boolean.FALSE;
		boolean lbError = Boolean.FALSE;

		try
		{
			Map<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.S431_CHANNEL_VENDOR, asVendorId);
			loHashMap.put(HHSConstants.BUDGET_ID_KEY, asBudgetId);

			// Add Assignment to DB
			loHashMap.put(HHSConstants.MODIFIED_BY_PROVIDER, aoCBGridBean.getModifyByProvider());
			loHashMap.put(HHSConstants.MODIFIED_BY_AGENCY, aoCBGridBean.getModifyByAgency());

			DAOUtil.masterDAO(aoMyBatisSession, loHashMap, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.INSERT_ASSIGNEE, HHSConstants.JAVA_UTIL_HASH_MAP);

			loSuccessStatus = Boolean.TRUE;

		}
		// handling ApplicationException if Assignee cannot be validated
		catch (ApplicationException aoAppExp)
		{
			if (lbError)
			{
				aoAppExp.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppExp.toString());
			}
			LOG_OBJECT.Error("ApplicationException Occured in addAssigneeForBudget", aoAppExp);
			setMoState("Could not add Organization :" + asVendorId + " as Assignee for BudgetId " + asBudgetId);
			throw new ApplicationException(
					"Exception occured while inserting Organization as Assignment in addAssigneeForBudget " + aoAppExp);

		}
		// handling other Exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception Occured in addAssigneeForBudget", aoExp);
			setMoState("Could not add Organization :" + asVendorId + " as Assignee for BudgetId " + asBudgetId);
			throw new ApplicationException(
					"Exception occured while inserting Organization as Assignment in addAssigneeForBudget " + aoExp);

		}

		return loSuccessStatus;

	}

	/**
	 * <li>Query Used: addAssignment</li> <li>Query Used: fetchBudgetAmount</li>
	 * <li>Query Used: fetchDiscFlagAmount</li>
	 * @param aoMyBatisSession SqlSession Object
	 * @param aoHashMap HashMap Object Passed
	 * @return BudgetList
	 * @throws ApplicationException Application exception thrown.
	 */
	@SuppressWarnings("rawtypes")
	public BudgetList getBudgetTaskStatus(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{
		BudgetList loBudgetList = null;
		Integer loAmount = null;
		String lsDisFlag = null;
		try
		{
			loBudgetList = (BudgetList) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.CBY_FETCH_BUDGET_STATUS,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			loAmount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_BUDGET_AMOUNT,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			lsDisFlag = (String) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.CBY_FETCH_DISCRIPENCY_FLAG_AMOUNT,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			if ((null != loBudgetList && null != loBudgetList.getBudgetId() && Integer.parseInt(loBudgetList
					.getBudgetId()) > 0) || (null != loAmount && loAmount < 0))
			{
				if (null == loBudgetList)
				{
					loBudgetList = new BudgetList();
				}
				loBudgetList.setLsInvoiceBudgetStatus(false);
				if (null != loAmount)
				{
					loBudgetList.setAmendAmount(loAmount);
				}
				setMoState("! Cannot submit invoices while modification,update or amendment is"
						+ " in progress and there status is not approved");
			}
			else if (null != lsDisFlag && lsDisFlag.equals(HHSConstants.ONE))
			{
				loBudgetList = new BudgetList();
				loBudgetList.setLsInvoiceBudgetStatus(false);
				setMoState("! Cannot submit invoices at this time");
			}
			else
			{
				loBudgetList = new BudgetList();
				loBudgetList.setLsInvoiceBudgetStatus(true);
			}
		}
		/**
		 * Application Exception Thrown
		 */
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in InvoiceService: getBudgetTaskStatus method:: ", loAppEx);
			setMoState("Transaction Failed:: InvoiceService: getBudgetTaskStatus method"
					+ " - failed Exception occured while fetching\n");
			throw loAppEx;
		}

		return loBudgetList;
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
	 * <li>Query Used:
	 * numberOfBudgetAmendmentsOrModificationsOrUpdatesInProgress</li>
	 * <li>Query Used: contractUpdateIdsInProgress</li>
	 * <li>Query Used: numberOfAmendmentsInProgress</li>
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
	public HashMap<String, String> checkSubmitInvoiceFeasibility(SqlSession aoMybatisSession,
			P8UserSession aoFilenetSession, String asBudgetId, String asContractId) throws ApplicationException
	{

		HashMap<String, String> loBudgetMap = new HashMap<String, String>();
		loBudgetMap.put(HHSConstants.IN_SE_BUDGET_ID, asBudgetId);
		loBudgetMap.put(HHSConstants.BUDGET_TYPE, HHSConstants.THREE);

		HashMap<String, String> loReturnMap = new HashMap<String, String>();
		loReturnMap.put(HHSConstants.IS_SUCCESS, HHSConstants.FALSE);
		Integer loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loBudgetMap,
				HHSConstants.MAPPER_CLASS_BUDGET_MAPPER,
				HHSConstants.FIN_BDG_SR_NUMBER_OF_BUDGET_AMENDMENTS_OR_MODIFICATIONS_OR_UPDATES_IN_PROGRESS,
				HHSConstants.JAVA_UTIL_HASH_MAP);
		if (loCount > HHSConstants.INT_ZERO)
		{
			loReturnMap.put(HHSConstants.ERROR_MESSAGE, HHSConstants.SUBMITINVOICE_MODIFICATION_FAILURE);
			return loReturnMap;
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
				loReturnMap.put(HHSConstants.ERROR_MESSAGE, HHSConstants.SUBMITINVOICE_UPDATE_FAILURE);
				return loReturnMap;
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
					loReturnMap.put(HHSConstants.ERROR_MESSAGE, HHSConstants.SUBMITINVOICE_UPDATE_FAILURE);
					return loReturnMap;
				}
				else
				{
					return getAmendmentCheck(aoMybatisSession, aoFilenetSession, asBudgetId, asContractId);
				}

			}
		}
	}

	/**
	 * This method fetches the amendments in progress <li>Query Used:
	 * numberOfAmendmentsInProgress</li> <li>Query Used:
	 * contractAmendmentsIdsInProgress</li> <li>Query Used:
	 * numberOfBudgetAmendmentsOrModificationsOrUpdatesInProgress</li>
	 * @param aoMybatisSession
	 * @param aoFilenetSession
	 * @param asBudgetId
	 * @param asContractId
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private HashMap<String, String> getAmendmentCheck(SqlSession aoMybatisSession, P8UserSession aoFilenetSession,
			String asBudgetId, String asContractId) throws ApplicationException
	{
		HashMap<String, String> loReturnMap = new HashMap<String, String>();
		loReturnMap.put(HHSConstants.IS_SUCCESS, HHSConstants.FALSE);
		Integer loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asBudgetId,
				HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.FIN_BDG_SR_NUMBER_OF_AMENDMENTS_IN_PROGRESS,
				HHSConstants.JAVA_LANG_STRING);
		if (loCount > HHSConstants.INT_ZERO)
		{
			List<String> loAmendList = new ArrayList<String>();
			loAmendList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asBudgetId,
					HHSConstants.MAPPER_CLASS_BUDGET_MAPPER, HHSConstants.CONTRACT_IDS_AMENDMENTS_IN_PROGRESS,
					HHSConstants.JAVA_LANG_STRING);
			Integer loOpenTaskCount = 0;
			HashMap loHmWFProperties = new HashMap();
			for (String lsContractId : loAmendList)
			{
				loHmWFProperties.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsContractId);
				loHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_AMENDMENT_COF);
				loOpenTaskCount = loOpenTaskCount
						+ new P8ProcessServiceForSolicitationFinancials().getOpenTaskCount(aoFilenetSession,
								loHmWFProperties);
			}
			if (loOpenTaskCount > HHSConstants.INT_ZERO)
			{
				loReturnMap.put(HHSConstants.ERROR_MESSAGE, HHSConstants.SUBMITINVOICE_FAILURE);
				return loReturnMap;
			}
			else
			{
				for (String lsContractId : loAmendList)
				{
					loHmWFProperties.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsContractId);
					loHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_AMENDMENT_CONFIGURATION);
					loOpenTaskCount = loOpenTaskCount
							+ new P8ProcessServiceForSolicitationFinancials().getOpenTaskCount(aoFilenetSession,
									loHmWFProperties);
				}
				if (loOpenTaskCount > HHSConstants.INT_ZERO)
				{
					loReturnMap.put(HHSConstants.ERROR_MESSAGE, HHSConstants.SUBMITINVOICE_FAILURE);
					return loReturnMap;
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
						loReturnMap.put(HHSConstants.ERROR_MESSAGE, HHSConstants.SUBMITINVOICE_FAILURE);
						return loReturnMap;
					}
					else
					{
						String lsDisFlag = null;
						HashMap<String, String> loHashMap = new HashMap<String, String>();
						loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, asContractId);
						lsDisFlag = (String) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
								HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
								HHSConstants.CBY_FETCH_DISCRIPENCY_FLAG_AMOUNT, HHSConstants.JAVA_UTIL_HASH_MAP);
						if (null != lsDisFlag && lsDisFlag.equals(HHSConstants.ONE))
						{
							loReturnMap.put(HHSConstants.ERROR_MESSAGE, HHSConstants.SUBMIT_AMEND_AMOUNT_FAILURE);
							return loReturnMap;
						}
						else
						{
							loReturnMap.put(HHSConstants.IS_SUCCESS, HHSConstants.TRUE);
							return loReturnMap;
						}
					}
				}
			}
		}

		else
		{
			String lsDisFlag = null;
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, asContractId);
			lsDisFlag = (String) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.CBY_FETCH_DISCRIPENCY_FLAG_AMOUNT,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			if (null != lsDisFlag && lsDisFlag.equals(HHSConstants.ONE))
			{
				loReturnMap.put(HHSConstants.ERROR_MESSAGE, HHSConstants.SUBMIT_AMEND_AMOUNT_FAILURE);
				return loReturnMap;
			}
			else
			{
				loReturnMap.put(HHSConstants.IS_SUCCESS, HHSConstants.TRUE);
				return loReturnMap;
			}
		}
	}

	/**
	 * <ul>
	 * <li>This method is used to fetch the invoice table next sequential
	 * invoice id.</li>
	 * <li>This invoice id will be returned to InvoiceContyroller to launch
	 * COntract Invoicing Screen.</li>
	 * <li>Query Used: fetchInvoiceSeqNo</li>
	 * <li>Query Used: fetchBudgetInvoiceSeqNo</li>
	 * <li>Query Used: fetchContractInvoiceSeqNo</li>
	 * <li>Query Used: fetchInvoiceSeqNo</li>
	 * <li>Query Used: fetchContractInvoiceSeqNo</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession :Session Object
	 * @param aoHashMap HashMap for input to SQL
	 * @param aoBudgetList BudgetList object.
	 * @return String aiCurrentSeq
	 * @throws ApplicationException : Application Exception
	 */
	@SuppressWarnings("rawtypes")
	public String getNextSeqFromInvoiceTable(SqlSession aoMyBatisSession, HashMap<String, String> aoHashMap,
			BudgetList aoBudgetList) throws ApplicationException
	{
		String lsCurrentSeq = null;
		Integer loInvoiceCount = null;
		Integer loCount = null;
		StringBuilder lsNumber = new StringBuilder();
		String lsFiscalYr = null;
		Integer liLen = null;
		Integer liCurrentMonth = HHSConstants.INT_ZERO;
		int liCount;
		try
		{
			if (aoBudgetList.getLsInvoiceBudgetStatus())
			{
				loInvoiceCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, null,
						HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.CBY_FETCH_INVOICE_NUMBER, null);
				loInvoiceCount = loInvoiceCount + HHSConstants.INT_ONE;
				Calendar loCalendar = Calendar.getInstance();
				liCurrentMonth = loCalendar.get(Calendar.MONTH) + HHSConstants.INT_ONE;
				lsFiscalYr = HHSUtil.GetFiscalYear().toString();
				lsNumber.append(lsFiscalYr.substring(HHSConstants.INT_TWO));
				lsNumber.append(String.format(HHSConstants.FIN_BDG_SR_02D, liCurrentMonth));
				liLen = loInvoiceCount.toString().length();
				for (liCount = HHSConstants.INT_ZERO; liCount < HHSConstants.INT_FIVE - liLen; liCount++)
				{
					lsNumber.append(HHSConstants.TRAILING_ZEROS);
				}
				lsNumber.append(loInvoiceCount);
				aoHashMap.put(HHSConstants.IS_NUMBER, lsNumber.toString());
				loCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
						HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
						HHSConstants.CBY_FETCH_INVOICE_BUDGET_SEQUENCE_NUMBER, HHSConstants.JAVA_UTIL_HASH_MAP);
				if (loCount < HHSConstants.INT_ONE)
				{
					aoHashMap.put(HHSConstants.IS_NUMBER, lsNumber.toString());
					DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
							HHSConstants.CBY_FETCH_INVOICE_SEQUENCE_NUMBER, HHSConstants.JAVA_UTIL_HASH_MAP);
					lsCurrentSeq = aoHashMap.get(HHSConstants.AI_CURRENT_SEQ);
				}
				else
				{
					lsNumber = new StringBuilder();
					loInvoiceCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, null,
							HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.CBY_FETCH_INVOICE_NUMBER, null);
					loInvoiceCount = loInvoiceCount + HHSConstants.INT_ONE;
					lsFiscalYr = (String) aoHashMap.get(HHSConstants.FISCAL_YEAR_ID);
					lsNumber.append(lsFiscalYr.substring(2));
					loCalendar = Calendar.getInstance();
					liCurrentMonth = loCalendar.get(Calendar.MONTH) + HHSConstants.INT_ONE;
					lsNumber.append(String.format(HHSConstants.FIN_BDG_SR_02D, liCurrentMonth));
					liLen = loInvoiceCount.toString().length();
					for (liCount = HHSConstants.INT_ZERO; liCount < 5 - liLen; liCount++)
					{
						lsNumber.append(HHSConstants.TRAILING_ZEROS);
					}
					lsNumber.append(loInvoiceCount);
					aoHashMap.put(HHSConstants.IS_NUMBER, lsNumber.toString());
					DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
							HHSConstants.CBY_FETCH_INVOICE_SEQUENCE_NUMBER, HHSConstants.JAVA_UTIL_HASH_MAP);
					lsCurrentSeq = aoHashMap.get(HHSConstants.AI_CURRENT_SEQ);
				}

			}
		}
		/**
		 * Application Exception Thrown
		 */
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in InvoiceService: getNextSeqFromInvoiceTable method:: ", loAppEx);
			setMoState("Transaction Failed:: InvoiceService: getNextSeqFromInvoiceTable method"
					+ " - failed Exception occured while fetching\n");
			throw loAppEx;
		}

		return lsCurrentSeq;
	}

	/**
	 * <ul>
	 * <li>This service class is invoked through fetchSubBudgetSummary
	 * transaction id for Contract budget screen</li>
	 * <li>This method fetchSubBudgetSummary will get the SubBudget Information
	 * on the basis of budgetId</li>
	 * <li>Query Used: fetchSubBudgetSummaryPrint</li>
	 * <li>Query Used: fetchSubBudgetSummary</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession :Session Object
	 * @param aoContractList : Contract List
	 * @return List loContractList
	 * @throws ApplicationException - ApplicationException object
	 */

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<CBGridBean> fetchSubBudgetSummary(SqlSession aoMybatisSession, ContractList aoContractList)
			throws ApplicationException
	{
		List<CBGridBean> loSubBudgetList = null;
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoContractList.getBudgetId());
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoContractList.getContractId());
		try
		{

			if (loHashMap.get(HHSConstants.SUBBUDGET_ID) != null)
			{
				loSubBudgetList = (List<CBGridBean>) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
						HHSConstants.CBY_FETCH_SUB_BUDGET_SUMMARY_PRINT, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			else
			{
				loSubBudgetList = (List<CBGridBean>) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_SUB_BUDGET_SUMMARY,
						HHSConstants.JAVA_UTIL_HASH_MAP);

			}
		}
		// Application Exception handled here
		catch (ApplicationException aoAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			aoAppEx.addContextData(HHSConstants.IN_SE_BUDGET_ID, loHashMap);
			LOG_OBJECT.Error("Exception occured while retrieveing SubBudget Information in ContractBudgetService ",
					aoAppEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchSubBudgetSummary method - failed to fetch"
					+ loHashMap + " \n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			LOG_OBJECT
					.Error("Exception occured while retrieveing Contract Information in ContractBudgetService ", aoEx);
			setMoState("Transaction Failed:: ContractBudgetService: fetchSubBudgetSummary method - failed to fetch"
					+ loHashMap + " \n");
			throw new ApplicationException("Error occured while retrieving Contract Summary", aoEx);
		}
		return loSubBudgetList;
	}

	/**
	 * This method is triggered to get the information in session.
	 * <ul>
	 * <li>A hashmap is passed to get the data.</li>
	 * <li>Query Used: getCbGridDataForSession</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession :Session Object
	 * @param aoContractList :ContractList
	 * @return CBGridBean loCBGridBean
	 * @throws ApplicationException - ApplicationException object
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public CBGridBean getCbGridDataForSession(SqlSession aoMybatisSession, ContractList aoContractList)
			throws ApplicationException
	{
		CBGridBean loCBGridBean = null;
		HashMap loHashMap = new HashMap<String, String>();
		// setting BudgetID and ContractId in HashMap
		loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, aoContractList.getBudgetId());
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoContractList.getContractId());
		try
		{
			loCBGridBean = (CBGridBean) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.GET_CB_GRID_DATA_FOR_SESSION,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		// Application Exception handled here
		catch (ApplicationException aoAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			aoAppEx.addContextData(HHSConstants.IN_SE_BUDGET_ID, loHashMap);
			LOG_OBJECT.Error("Exception occured while retrieveing CbGridDataForSession Information in InvoiceService ",
					aoAppEx);
			setMoState("Transaction Failed:: InvoiceService: getCbGridDataForSession method - failed to fetch"
					+ loHashMap + " \n");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			// And setting the transaction state
			LOG_OBJECT.Error("Exception occured while retrieveing CbGridDataForSession in InvoiceService ", aoEx);
			setMoState("Transaction Failed:: InvoiceService: getCbGridDataForSession method - failed to fetch"
					+ loHashMap + " \n");
			throw new ApplicationException("Error occured while insertRate", aoEx);
		}

		return loCBGridBean;
	}

	/**
	 * This method is used to save InvoiceDetails from contract invoice Screen
	 * 
	 * <ul>
	 * <li>Invoice Provider is updated</li>
	 * <li>Invoice StartDate is updated</li>
	 * <li>Invoice EndDate is updated</li>
	 * <li>Query Used: updateInvoiceDetails</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession :Session Object
	 * @param aoInvoiceMap Containing invoice.
	 * @throws ApplicationException : Application Exception
	 */
	@SuppressWarnings("rawtypes")
	public void updateInvoiceDetails(SqlSession aoMyBatisSession, Map aoInvoiceMap) throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoInvoiceMap, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.UPDATE_INVOICE_DETAILS, HHSConstants.JAVA_UTIL_MAP);
			setMoState("Transaction Success:: InvoiceService:updateInvoiceDetails method - success to update record "
					+ " \n");
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("Invoice-ID : ", aoInvoiceMap.get(HHSConstants.INVOICE_ID));
			LOG_OBJECT.Error("Exception occured while executing query in updateInvoiceDetails ", loAppEx);

			setMoState("Transaction Failed:: InvoiceService:updateInvoiceDetails method - failed to insert record "
					+ " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while executing query in updateInvoiceDetails ", loAppEx);
			setMoState("Transaction Failed:: InvoiceService:updateInvoiceDetails method - failed to insert record "
					+ " \n");
			throw new ApplicationException("Exception occured while updateInvoiceDetails in InvoiceService ", loAppEx);
		}

	}

	/**
	 * This method is used to delete Assignment Details from contract invoice
	 * Screen <li>Query Used: fetchCountPaymentAssignment</li> <li>Query Used:
	 * fetchCountInvoiceDetails</li> <li>Query Used: delAdvanceAssignment</li>
	 * <li>Query Used: delInvoiceDetails</li> <li>Query Used: delAssignment</li>
	 * 
	 * @param aoMybatisSession Session Object
	 * @param aoAssignmentsSummaryBean : Assignment Summary
	 * @return Boolean Status of Delete Assignment
	 * @throws ApplicationException : Application Exception
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Boolean delContractInvoiceAssignment(SqlSession aoMybatisSession,
			AssignmentsSummaryBean aoAssignmentsSummaryBean) throws ApplicationException
	{
		boolean lbError = false;
		Boolean lbDelStatus = true;
		Integer loFetchCount = HHSConstants.INT_ZERO;
		Integer loFetchInvoiceCount = HHSConstants.INT_ZERO;
		HashMap loHashMapForInvAssign = new HashMap<String, String>();
		loHashMapForInvAssign.put(HHSConstants.ASSIGNMENT_ID, aoAssignmentsSummaryBean.getId());
		loHashMapForInvAssign.put(HHSConstants.BUDGET_ADVANCE_ID, aoAssignmentsSummaryBean.getBudgetAdvanceId());
		loHashMapForInvAssign.put(HHSConstants.CONTRACT_BUDGET_ID, aoAssignmentsSummaryBean.getContractBudgetID());
		loHashMapForInvAssign.put(HHSConstants.STATUS, HHSConstants.APPROVED);

		try
		{

			// Check Assignments from S400_Advance Payment Request Screen
			loFetchCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHashMapForInvAssign,
					HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.FETCH_COUNT_PAYMENT_ASSIGNMENT,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			// Set Parameters for fetchCountInvoiceDetails
			loHashMapForInvAssign.put(HHSConstants.INVOICE_ENTRY_TYPE_ID, HHSConstants.ASSIGNMENT_ENTRY_TYPE_ID);
			loHashMapForInvAssign.put(HHSConstants.INVOICE_LINE_ITEM_ID, aoAssignmentsSummaryBean.getId());

			// Check Assignments from S329_Invoice Screen
			loFetchInvoiceCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHashMapForInvAssign,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_COUNT_INVOICE_DETAILS,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			if ((loFetchCount + loFetchInvoiceCount) == HHSConstants.INT_ZERO)
			{
				// Delete Advance_Assignment
				DAOUtil.masterDAO(aoMybatisSession, aoAssignmentsSummaryBean,
						HHSConstants.MAPPER_CLASS_PAYMENT_MODULE_MAPPER, HHSConstants.PM_DELETE_ADVANCE_ASSIGNMENT,
						HHSConstants.INPUT_PARAM_CLASS_ASSIGNMENT_BEAN);
				// Delete from Invoice_Details if Assignee has un-approved
				// assignments
				DAOUtil.masterDAO(aoMybatisSession, loHashMapForInvAssign, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
						HHSConstants.DEL_INVOICE_DETAILS, HHSConstants.JAVA_UTIL_HASH_MAP);
				// Delete Assignee
				DAOUtil.masterDAO(aoMybatisSession, aoAssignmentsSummaryBean, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
						HHSConstants.CI_DEL_ASSIGNMENT, HHSConstants.INPUT_PARAM_CLASS_ASSIGNMENT_BEAN);
			}
			else
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.MSG_KEY_INVOICE_ASSIGNMENT_DELETE_CHECK));
			}
		}

		catch (ApplicationException loAppEx)
		{
			if (lbError)
			{
				loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, loAppEx.toString());
			}
			loAppEx.addContextData("ID : ", aoAssignmentsSummaryBean.getId());
			LOG_OBJECT.Error("Exception occured  delete at InvoiceService: delContractInvoiceAssignment() ", loAppEx);
			setMoState("InvoiceService: DEL_INVOICE_DETAILS() failed to delete at id:"
					+ aoAssignmentsSummaryBean.getId() + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while delete in InvoiceService ", loAppEx);
			setMoState("Transaction Failed:: InvoiceService: delContractInvoiceAssignment method - failed to delete"
					+ aoAssignmentsSummaryBean.getId() + " \n");
			throw new ApplicationException("Exception occured while delete in InvoiceService ", loAppEx);
		}
		return lbDelStatus;
	}

	// INVOiCE ADVANCES STARTS HERE

	/**
	 * This method validates that Invoice Recoupment Amount should not be more
	 * that Advance Amount <li>Query Used: validateBudgetAdvanceStatus</li>
	 * @param aoAdvanceSummaryBean Advance Summary
	 * @param aoMyBatisSession Session Object
	 * @return Boolean Validation Result
	 * @throws ApplicationException : Application Exception
	 */
	@SuppressWarnings("unchecked")
	public Boolean validateInvoiceAdvanceStatus(AdvanceSummaryBean aoAdvanceSummaryBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Boolean loValidRecoup = true;
		boolean lbError = false;
		int liRows = HHSConstants.INT_ZERO;

		try
		{
			// Check BUdget_Advance_Status is disbursed
			liRows = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoAdvanceSummaryBean.getId(),
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.VALIDATE_BUDGET_ADVANCE_STATUS,
					HHSConstants.JAVA_LANG_STRING);
			if (liRows <= HHSConstants.INT_ZERO)
			{
				// Budget Advance not in disbursed status
				lbError = true;
				// ApplicationException: Advance can be recouped only for
				// disbursed advances
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.MSG_KEY_RECOUP_ONLY_FOR_DISBURSED));
			}
			validateRecoupmentAmount(aoAdvanceSummaryBean, aoMyBatisSession, lbError);

		}
		// Application Exception while Validating Equipment Invoice Amount
		catch (ApplicationException aoAppEx)
		{
			if (lbError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			LOG_OBJECT.Error("Exception occured in InvoiceService: validateInvoiceAdvance method:: ", aoAppEx);
			setMoState("Transaction Failed:: InvoiceService: validateInvoiceAdvance method - failed to validate "
					+ aoAdvanceSummaryBean.getId() + " \n");
			throw aoAppEx;
		}
		// handling Exception while Validating Equipment Invoice Amount
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in InvoiceService: validateInvoiceAdvance method:: ", aoEx);
			setMoState("Transaction Failed:: InvoiceService: validateInvoiceAdvance method - failed to validate"
					+ aoAdvanceSummaryBean.getId() + " \n");
			throw new ApplicationException("Error occured while validating Invoice Advance Recoupment Amount", aoEx);
		}
		return loValidRecoup;
	}

	/**
	 * This method validates the recoup amount <li>Query Used:
	 * fetchInvoiceRecoupAmount</li> <li>Query Used:
	 * validateInvAdvanceRecoupAmount</li>
	 * @param aoAdvanceSummaryBean
	 * @param aoMyBatisSession
	 * @param abError
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private void validateRecoupmentAmount(AdvanceSummaryBean aoAdvanceSummaryBean, SqlSession aoMyBatisSession,
			boolean abError) throws ApplicationException
	{

		Map<String, Object> loInvoiceAdvRecoupDetails = null;
		// Recoupment Amount for current Invoice

		try
		{
			String loRecoupedAmount = (String) DAOUtil.masterDAO(aoMyBatisSession, aoAdvanceSummaryBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_INVOICE_RECOUP_AMOUNT,
					HHSConstants.MODEL_CB_ADVANCESUMMARY_BEAN);
			// Total Recoupment Amount for Advance in Approved Invoices
			loInvoiceAdvRecoupDetails = (HashMap<String, Object>) DAOUtil.masterDAO(aoMyBatisSession,
					aoAdvanceSummaryBean.getId(), HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSConstants.VALIDATE_INVOICE_ADVANCE_RECOUP_AMOUNT, HHSConstants.JAVA_LANG_STRING);
			if (loInvoiceAdvRecoupDetails != null)
			{
				BigDecimal loAdvanceAmount = (BigDecimal) loInvoiceAdvRecoupDetails.get(HHSConstants.ADVANCE_AMOUNT);
				BigDecimal loTotalRecoupedAmount = (BigDecimal) loInvoiceAdvRecoupDetails
						.get(HHSConstants.HASH_KEY_TOTAL_RECOUPED_AMOUNT);
				BigDecimal loTotalRecoupAmountReview = (BigDecimal) loInvoiceAdvRecoupDetails
						.get(HHSConstants.HASH_KEY_TOTAL_RECOUPED_AMOUNT_REVIEW);

				// Total Recoupment Amount for Advance in Approved Invoices
				if (loAdvanceAmount.subtract(loTotalRecoupedAmount)
						.subtract(new BigDecimal(aoAdvanceSummaryBean.getInvoiceRecoupedAmt()))
						.compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO)
				{
					abError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.MSG_KEY_INVOICE_RECOUP_AMOUNT_MORE_THAN_ADVANCE));
				}

				// Total Recoupment Amount for Advance in Approved and Pending
				// Approval Invoices
				if (loAdvanceAmount.add(new BigDecimal(loRecoupedAmount)).subtract(loTotalRecoupAmountReview)
						.subtract(new BigDecimal(aoAdvanceSummaryBean.getInvoiceRecoupedAmt()))
						.compareTo(BigDecimal.ZERO) < HHSConstants.INT_ZERO)
				{
					abError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.MSG_KEY_INVOICE_RECOUP_AMOUNT_UNDER_REVIEW));
				}

			}
		}
		catch (ApplicationException aoAppEx)
		{
			if (abError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			LOG_OBJECT.Error("Exception occured in InvoiceService: validateInvoiceAdvance method:: ", aoAppEx);
			setMoState("Transaction Failed:: InvoiceService: validateInvoiceAdvance method - failed to validate "
					+ aoAdvanceSummaryBean.getId() + " \n");
			throw aoAppEx;
		}

	}

	/**
	 * This method updates InvocieAdvance details <li>Query Used:
	 * editInvoiceAdvanceRecouped</li>
	 * @param aoValid :Result of Business Validation on entered Change
	 * @param aoAdvanceSummaryBean : Advance Summary Bean
	 * @param aoMyBatisSession :Session Object
	 * @return Boolean
	 * @throws ApplicationException : Application Exception
	 */
	public Boolean editInvoiceAdvanceDetails(Boolean aoValid, AdvanceSummaryBean aoAdvanceSummaryBean,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		boolean lbError = false;
		Boolean loEditStatus = false;
		Integer loRows = HHSConstants.INT_ZERO;
		try
		{
			// Update Invoice Recoupment Amount if validation result is true
			if (aoValid)
			{
				// Update row in DB
				loRows = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoAdvanceSummaryBean,
						HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.EDIT_INVOICE_ADVANCES_RECOUPED,
						HHSConstants.MODEL_CB_ADVANCESUMMARY_BEAN);
				// Insert in case if rows updated is Zero
				if (loRows < 1)
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoAdvanceSummaryBean, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
							HHSConstants.INSERT_INVOICE_ADVANCES_RECOUPED, HHSConstants.MODEL_CB_ADVANCESUMMARY_BEAN);
				}
				loEditStatus = true;

			}
			else
			{
				lbError = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.MSG_KEY_INVOICE_RECOUP_AMOUNT_MORE_THAN_ADVANCE));

			}

		}
		// handling Application Exception while updating Equipment Invoice
		// Amount
		catch (ApplicationException aoAppEx)
		{
			if (lbError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}

			LOG_OBJECT.Error("Exception occured in InvoiceService: editInvoiceAdvanceDetails method:: ", aoAppEx);
			setMoState("Transaction Failed:: InvoiceService: editInvoiceAdvanceDetails method - failed to update INVOICE_ADVANCES for BUdget_Advance_Id"
					+ aoAdvanceSummaryBean.getId() + " \n");
			throw aoAppEx;
		}
		// handling Application Exception while updating Equipment Invoice
		// Amount
		catch (Exception aoEx)
		{

			LOG_OBJECT.Error("Exception occured in InvoiceService: editInvoiceAdvanceDetails method:: ", aoEx);
			setMoState("Transaction Failed:: InvoiceService: editInvoiceAdvanceDetails method - failed to update INVOICE_ADVANCES for BUdget_Advance_Id"
					+ aoAdvanceSummaryBean.getId() + " \n");
			throw new ApplicationException("Error occured while updating Invoice Recoupment Amount for Budget Advance",
					aoEx);
		}

		return loEditStatus;
	}

	/**
	 * This method retrieves Advance DEtails for an Invoice and merge it with
	 * Advance Details for its Budget <li>Query Used: fetchAdvanceDetails</li>
	 * <li>Query Used: fetchInvoiceAdvancesDetails</li>
	 * @param aoCBGridBean : Advance Details for an Invoice of a Budget
	 * @param aoMyBatisSession : MyBatis Session
	 * @return List<AdvanceSummaryBean> Advance Details for a Budget and Invoice
	 * @throws ApplicationException : Application Exception
	 */
	@SuppressWarnings("unchecked")
	public List<AdvanceSummaryBean> fetchInvoiceAdvanceDetails(CBGridBean aoCBGridBean, SqlSession aoMyBatisSession)
			throws ApplicationException
	{

		List<AdvanceSummaryBean> loAdvanceSummaryBeans = null;

		List<AdvanceSummaryBean> loInvoiceAdvanceSummaryBeans = null;
		try
		{
			loAdvanceSummaryBeans = (List<AdvanceSummaryBean>) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.CBY_FETCH_ADVANCE_SUMMARY,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

			// Get Invoice recoupment Details for current invoice for a Budget
			loInvoiceAdvanceSummaryBeans = (List<AdvanceSummaryBean>) DAOUtil.masterDAO(aoMyBatisSession, aoCBGridBean,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_INVOICE_ADVANCES_DETAILS,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			// Merge Invoice Advance Amount in Budget Advance Details
			mergeAdvanceDetails(loAdvanceSummaryBeans, loInvoiceAdvanceSummaryBeans);

		}
		// handling Application Exception while fetching Equipment Details
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured in InvoiceService: fetchInvoiceAdvanceDetails method:: ",
					aoAppEx);
			setMoState("Error while fetching Invoice Advance details for budget id:"
					+ aoCBGridBean.getContractBudgetID() + " InvoiceId : " + aoCBGridBean.getInvoiceId());
			throw aoAppEx;
		}
		// handling Exception while fetching Equipment Details
		catch (Exception aoEx)
		{

			LOG_OBJECT.Error("Exception occured in InvoiceService: fetchInvoiceAdvanceDetails method:: ", aoEx);
			setMoState("Transaction Failed:: InvoiceService: fetchInvoiceAdvanceDetails method - failed to fetch Invoice Advances for BUdget"
					+ aoCBGridBean.getContractBudgetID() + "and InvoiceId " + aoCBGridBean.getInvoiceId() + " \n");
			throw new ApplicationException("Error occured while fetching equipment", aoEx);
		}
		return loAdvanceSummaryBeans;

	}

	/**
	 * <p>
	 * This method is used to concatenate invoiceAdvances with BudgetAdvance
	 * details
	 * </p>
	 * 
	 * @param aoResultBeanList , List of AdvanceSummaryBean having Budget
	 *            Advance details
	 * @param aoInputBeanList , List of AdvanceSummaryBean having Invoice
	 *            Advance details
	 */
	private void mergeAdvanceDetails(List<AdvanceSummaryBean> aoResultBeanList, List<AdvanceSummaryBean> aoInputBeanList)
	{

		// Get Invoiced Amount and put in a Map with respective
		// operationSupportID as Key
		Map<String, AdvanceSummaryBean> loInvoiceAdvanceMap = new HashMap<String, AdvanceSummaryBean>();

		for (AdvanceSummaryBean loInputIterate : aoInputBeanList)
		{
			loInvoiceAdvanceMap.put(loInputIterate.getId(), loInputIterate);
		}
		//

		for (AdvanceSummaryBean loResultIterate : aoResultBeanList)
		{
			// If InvoiceDetail exist for OPSupport, concatenate
			if (loInvoiceAdvanceMap.containsKey(loResultIterate.getId()))
			{

				loResultIterate.setInvoiceId(loInvoiceAdvanceMap.get(loResultIterate.getId()).getInvoiceId());
				loResultIterate.setInvoiceRecoupedAmt(loInvoiceAdvanceMap.get(loResultIterate.getId())
						.getInvoiceRecoupedAmt());
				loResultIterate.setInvoiceAdvanceId(loInvoiceAdvanceMap.get(loResultIterate.getId())
						.getInvoiceAdvanceId());
			}
			else
			{
				loResultIterate.setInvoiceId(HHSConstants.EMPTY_STRING);
				loResultIterate.setInvoiceAdvanceId(HHSConstants.EMPTY_STRING);
				loResultIterate.setInvoiceRecoupedAmt(String.valueOf(HHSConstants.INT_ZERO));
			}
		}

	}

	// INVOICE ADVANCES ENDS HERE

	/**
	 * This method check the validation for
	 * <p>
	 * If a negative Amendment Budget exists for the Contract and is "In
	 * ProgressPending Approval",
	 * </p>
	 * <li>Query Used: invoiceNegativeAmendCheck</li>
	 * 
	 * @param aoMyBatisSession : MyBatis Session
	 * @param asBudgetId : budget id
	 * @return loValidateStatus Boolean object
	 * @throws ApplicationException : Application Exception
	 */
	public Boolean invoiceNegativeAmendCheck(SqlSession aoMyBatisSession, String asBudgetId)
			throws ApplicationException
	{
		Boolean loValidateStatus = false;
		try
		{
			Integer loCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, asBudgetId,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.INVOICE_NEGATIVE_AMEND_CHECK,
					HHSConstants.JAVA_LANG_STRING);
			if (loCount == 0)
			{
				loValidateStatus = true;
			}
		}
		// handling Application Exception while fetching Equipment Details
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured in InvoiceService: invoiceNegativeAmendCheck method:: ",
					aoAppEx);
			setMoState("Error while fetching Invoice Advance details for budget id:" + asBudgetId);
			throw aoAppEx;
		}
		// handling Exception while fetching Equipment Details
		catch (Exception aoEx)
		{

			LOG_OBJECT.Error("Exception occured in InvoiceService: invoiceNegativeAmendCheck method:: ", aoEx);
			setMoState("Transaction Failed:: InvoiceService: invoiceNegativeAmendCheck method - failed to validate for Budget"
					+ asBudgetId + " \n");
			throw new ApplicationException("Error occured while validating negative Amendment Budget", aoEx);
		}
		return loValidateStatus;
	}

	/**
	 * This method check the validation for
	 * <p>
	 * the Total Invoice Amount for the Assignments is greater than the Invoice
	 * Amount
	 * </p>
	 * <li>Query Used: invoiceTotal</li> <li>Query Used: assignmentAmount</li>
	 * @param aoMyBatisSession : MyBatis Session
	 * @param asInvoiceId : budget id
	 * @return loValidateStatus Boolean object
	 * @throws ApplicationException : Application Exception
	 */
	public Boolean invoiceAmountAssignmentValidation(SqlSession aoMyBatisSession, String asInvoiceId)
			throws ApplicationException
	{
		Boolean loValidateStatus = false;
		try
		{
			BigDecimal loTotalInvoice = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, asInvoiceId,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.INVOICE_DETAILS,
					HHSConstants.JAVA_LANG_STRING);
			BigDecimal loAssignment = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, asInvoiceId,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.ASSIGNEMNT_AMOUNT,
					HHSConstants.JAVA_LANG_STRING);
			if (loTotalInvoice.compareTo(loAssignment) >= 0)
			{
				loValidateStatus = true;
			}
		}
		// handling Application Exception while fetching Equipment Details
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(
					"ApplicationException occured in InvoiceService: invoiceAmountAssignmentValidation method:: ",
					aoAppEx);
			setMoState("Error while fetching Invoice validation for invoiceId:" + asInvoiceId);
			throw aoAppEx;
		}
		// handling Exception while fetching Equipment Details
		catch (Exception aoEx)
		{

			LOG_OBJECT.Error("Exception occured in InvoiceService: invoiceAmountAssignmentValidation method:: ", aoEx);
			setMoState("Transaction Failed:: InvoiceService: invoiceAmountAssignmentValidation method - failed to validate for Invoice"
					+ asInvoiceId + " \n");
			throw new ApplicationException(
					"Error occured while validating the Total Invoice Amount for the Assignments is greater than the Invoice Amount",
					aoEx);
		}
		return loValidateStatus;
	}

	/**
	 * This method check the validation for
	 * <p>
	 * If the Invoice Amount is equal to Zero
	 * </p>
	 * <li>Query Used: assignmentAmount</li>
	 * @param aoMyBatisSession : MyBatis Session
	 * @param asInvoiceId : budget id
	 * @return loValidateStatus Boolean object
	 * @throws ApplicationException : Application Exception
	 */
	public Boolean invoiceAmountZeroValidation(SqlSession aoMyBatisSession, String asInvoiceId)
			throws ApplicationException
	{
		Boolean loValidateStatus = true;
		try
		{
			BigDecimal loTotalInvoice = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, asInvoiceId,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.INVOICE_DETAILS,
					HHSConstants.JAVA_LANG_STRING);
			// Start Enhancement 6535 Release 3.8.0
			if (loTotalInvoice.compareTo(BigDecimal.ZERO) <= 0)
			{
				// End Enhancement 6535 Release 3.8.0
				loValidateStatus = false;
			}
		}
		// handling Application Exception while fetching Equipment Details
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured in InvoiceService: invoiceAmountZeroValidation method:: ",
					aoAppEx);
			setMoState("Error while fetching Invoice amount for invocie Id:" + asInvoiceId);
			throw aoAppEx;
		}
		// handling Exception while fetching Equipment Details
		catch (Exception aoEx)
		{

			LOG_OBJECT.Error("Exception occured in InvoiceService: invoiceAmountZeroValidation method:: ", aoEx);
			setMoState("Transaction Failed:: InvoiceService: invoiceAmountZeroValidation method - failed to validate for Budget"
					+ asInvoiceId + " \n");
			throw new ApplicationException("Error occured while validating the Invoice Amount for equal to zero", aoEx);
		}
		return loValidateStatus;
	}

	/**
	 * <p>
	 * This is private method used to merge remaining amount and invoice amount
	 * present in two different list<PersonnelServiceBudget> and add it to one
	 * List<PersonnelServiceBudget>
	 * <ul>
	 * <li>1.Read full remaining amount list<PersonnelServiceBudget></li>
	 * <li>2.Get data from invoice list using index and assign it to amount list
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoRateBeanRemainingAmtList List<RateBean> containing remaining
	 *            amount.
	 * @param aoRateBeanInvoiceAmtList List<RateBean> containing invoice amount.
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	private void mergePSInvoiceAndRemainingAmt(List<PersonnelServiceBudget> aoSalariedEmployessRemainingAmtList,
			List<PersonnelServiceBudget> aoSalariedEmployessInvoiceAmtList) throws ApplicationException
	{
		int liCount = HHSConstants.INT_ZERO;
		for (PersonnelServiceBudget loPSBean : aoSalariedEmployessRemainingAmtList)
		{
			loPSBean.setInvoicedAmount(aoSalariedEmployessInvoiceAmtList.get(liCount).getInvoicedAmount());
			liCount++;
		}
	}

	/**
	 * This method added as a part of release 3.12.0 enhancement 6578 to fetch
	 * payments at level 1
	 * 
	 * @param aoMyBatisSession
	 * @param aoFilenetSession
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<String> fetchPaymentsAtLevel1AndUpdateFlag(SqlSession aoMyBatisSession, P8UserSession aoFilenetSession)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in fetchPaymentsAtLevel1 method ::");
		LOG_OBJECT.Info("fetching updated records from ref_contracts_a table ::");
		List<String> loPendingApprovedPaymentIdList = null;
		List<String> loPaymentIdListFromRefTable = null;
		// fetching payment list from ref_contracts_a corresponding to ct#
		// updated as on current date
		loPaymentIdListFromRefTable = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, null,
				HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSConstants.FETCH_UPDATED_ACCOUNTING_LINES, null);

		if (null != loPaymentIdListFromRefTable && !loPaymentIdListFromRefTable.isEmpty())
		{
			LOG_OBJECT.Info("updated records fetched successfully from ref_contracts_a table ::");
			Map loInvoiceIDAdvanceIDHashMap = new HashMap();

			HashMap loHmWFProperties = new HashMap();
			loHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_LIST);
			loHmWFProperties.put(HHSConstants.CURR_LEVEL, HHSConstants.ONE);
			loHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_VISIBILITY, HHSConstants.ONE);
			loInvoiceIDAdvanceIDHashMap = new P8ProcessServiceForSolicitationFinancials()
					.fetchPaymentIDsForPaymentsAtLevel1(aoFilenetSession, loHmWFProperties);

			loInvoiceIDAdvanceIDHashMap.put(HHSConstants.UPDATED_PAYMENT_LIST_FROM_FMS_FEED,
					loPaymentIdListFromRefTable);

			LOG_OBJECT.Info("Invoice ids and budget advance ids map fetched successfully ::: "
					+ loInvoiceIDAdvanceIDHashMap);
			// Start changes on 8th Feb (error in logs)
			if (loInvoiceIDAdvanceIDHashMap != null
					&& ((loInvoiceIDAdvanceIDHashMap.get(HHSConstants.INVOICE) != null && !((List) loInvoiceIDAdvanceIDHashMap
							.get(HHSConstants.INVOICE)).isEmpty()) || (loInvoiceIDAdvanceIDHashMap
							.get(HHSConstants.BUDGET_ADVANCE_ID) != null && !((List) loInvoiceIDAdvanceIDHashMap
							.get(HHSConstants.BUDGET_ADVANCE_ID)).isEmpty())))
			{
				loPendingApprovedPaymentIdList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession,
						loInvoiceIDAdvanceIDHashMap, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
						HHSConstants.PAYMENTS_AT_LEVEL_1, HHSConstants.JAVA_UTIL_MAP);

				if (null != loPendingApprovedPaymentIdList && !loPendingApprovedPaymentIdList.isEmpty())
				{
					// deleting accounting lines from payment allocation for
					// payments at level 1
					Map loPaymentIDtoBeDeletedMap = new HashMap();
					loPaymentIDtoBeDeletedMap.put(HHSConstants.DELETE_PENDING_APPROVAL_PAYMENTS_AT_LEVEL_1,
							loPendingApprovedPaymentIdList);
					loPaymentIDtoBeDeletedMap.put(HHSConstants.BATCH_IN_PROGRESS_FLAG, HHSConstants.ONE);

					DAOUtil.masterDAO(aoMyBatisSession, loPaymentIDtoBeDeletedMap,
							HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
							HHSConstants.UPDATE_BATCH_IN_PROGRESS_FLAG, HHSConstants.JAVA_UTIL_MAP);
					LOG_OBJECT
							.Info("Exiting fetchPaymentsAtLevel1AndUpdateFlag + loPendingApprovedPaymentIdList size ::  +  "
									+ loPendingApprovedPaymentIdList.toString());
				}
				// End changes on 8th Feb (error in logs)
			}
		}
		return loPendingApprovedPaymentIdList;
	}

	/**
	 * This method added as a part of release 3.12.0 enhancement 6578 to fetch
	 * payments at level 1
	 * 
	 * @param aoMyBatisSession
	 * @param aoFilenetSession
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap deleteAccountingLines(SqlSession aoMyBatisSession, List<String> loPendingApprovedPaymentIdList)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in deleteAccountingLines method ::");
		HashMap loPayementIdMap = new HashMap();
		if (null != loPendingApprovedPaymentIdList && !loPendingApprovedPaymentIdList.isEmpty())
		{
			LOG_OBJECT.Info("loPendingApprovedPaymentIdList size :: " + loPendingApprovedPaymentIdList.size());
			// deleting accounting lines from payment allocation for payments at
			// level 1
			Map loPaymentIDtoBeDeletedMap = new HashMap();
			loPaymentIDtoBeDeletedMap.put(HHSConstants.DELETE_PENDING_APPROVAL_PAYMENTS_AT_LEVEL_1,
					loPendingApprovedPaymentIdList);

			DAOUtil.masterDAO(aoMyBatisSession, loPaymentIDtoBeDeletedMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSConstants.DELETE_PENDING_APPROVAL_PAYMENTS_AT_LEVEL_1, HHSConstants.JAVA_UTIL_MAP);

			LOG_OBJECT.Info("accounting lines deleted successfully ::: ");
			String[] loPaymentIDsArray = Arrays.copyOf(loPendingApprovedPaymentIdList.toArray(),
					loPendingApprovedPaymentIdList.toArray().length, String[].class);
			loPayementIdMap.put(HHSP8Constants.PAYMENT_ID_ARRAY, loPaymentIDsArray);
			loPayementIdMap.put(HHSP8Constants.CREATED_BY_USERID, HHSConstants.SYSTEM_USER);
		}
		LOG_OBJECT.Info("payment map returned successfully :: " + loPayementIdMap);
		return loPayementIdMap;
	}

	/**
	 * This method will update the flag of batch which is currently in progress
	 * @param aoMyBatisSession
	 * @throws ApplicationException
	 */
	public void updateBatchInProgressFlag(SqlSession aoMyBatisSession) throws ApplicationException
	{
		LOG_OBJECT.Info("updating batch_in_progress flag to 0 ::: ");
		DAOUtil.masterDAO(aoMyBatisSession, null, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
				HHSConstants.UPDATE_BATCH_IN_PROGRESS_FLAG_FOR_ACTIVE_RECORDS, null);
		LOG_OBJECT.Info(" batch_in_progress flag updated to  0  successfully::: ");
	}

	/**
	 * This method added in R7. This method will fetch the count of modified
	 * budget.
	 * @param aoMyBatisSession
	 * @param aomap
	 * @return
	 * @throws ApplicationException
	 */
	public String getModificationBudgetCount(SqlSession aoMyBatisSession, HashMap<String, String> aomap)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in getModificationBudgetCount Method");
		String lsCount = HHSConstants.ZERO;
		try
		{
			lsCount = (String) DAOUtil.masterDAO(aoMyBatisSession, (String) aomap.get(HHSConstants.BUDGET_ID_WORKFLOW),
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSR5Constants.FETCH_MODIFICATION_URL_DETAIL,
					HHSConstants.JAVA_LANG_STRING);
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

	// Start: Added in R7 for Cost Center
	/**
	 * This method is added in R7 for Cost Center.It is used to fetch Services Detailed grid data.
	 * @param aoMybatisSession
	 * @param aoCBGridBeanObj
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<CBServicesBean> fetchServicesInvoiceGrid(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into fetchServicesInvoiceGrid for BudgetId:" + aoCBGridBeanObj.getContractBudgetID());
		List<CBServicesBean> loInvoiceServiceData = null;
		try
		{
			loInvoiceServiceData = (List<CBServicesBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,HHSR5Constants.FETCH_INVOICE_SERVICE_DATA,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("subBudget-ID : ", aoCBGridBeanObj.getSubBudgetID());
			LOG_OBJECT.Error("Exception occured  at fetching InvoiceService: fetchServicesInvoiceGrid() ", loAppEx);
			setMoState("InvoiceService: fetchServicesInvoiceGrid() failed to fetch at subBudget-ID:"
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
			LOG_OBJECT.Error("Exception occured while retrieving in InvoiceService ", loAppEx);
			setMoState("Transaction Failed:: InvoiceService: fetchServicesInvoiceGrid method"
					+ " - failed to retrieve for subBudget-ID" + aoCBGridBeanObj.getSubBudgetID() + " \n");
			throw new ApplicationException("Exception occured while fetch in InvoiceService ", loAppEx);
		}
		LOG_OBJECT.Debug("Exited from fetchServicesInvoiceGrid for BudgetId:" + aoCBGridBeanObj.getContractBudgetID());
		return loInvoiceServiceData;
	}
	
	
	/** This method is added in R7 for Cost Center.It is used to update Service Detailed grid data.
	 * @param aoValid
	 * @param aoCBServices
	 * @param aoMyBatisSession
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean editServiceInvoiceDetails(CBServicesBean aoCBServices,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into editServiceInvoiceDetails for BudgetId:" + aoCBServices.getContractBudgetID());
		Boolean loEditStatus = true;
		Integer loRows = HHSConstants.INT_ZERO;
		try
		{

			loRows = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBServices,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSR5Constants.EDIT_INVOICE_SERVICES,
					HHSR5Constants.COM_NYC_HHS_MODEL_CB_SERVICESBEAN);
			if (loRows == HHSConstants.INT_ZERO)
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoCBServices, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
						HHSR5Constants.INSERT_INVOICE_SERVICES, HHSR5Constants.COM_NYC_HHS_MODEL_CB_SERVICESBEAN);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			setMoState("Transaction Failed:: InvoiceService: editServiceInvoiceDetails method - failed to update INVOICE_DETAIL"
					+ aoCBServices.getId() + " \n");
			LOG_OBJECT.Error("error occured while updating Invoice Services ", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in InvoiceService: editServiceInvoiceDetails method:: ", aoEx);
			setMoState("Transaction Failed:: InvoiceService: editServiceInvoiceDetails method - failed to update INVOICE_DETAIL"
					+ aoCBServices.getId() + " \n");
			throw new ApplicationException("Error occured while updating Invoice Amount for Services", aoEx);
		}
		LOG_OBJECT.Debug("Exited from editServiceInvoiceDetails for BudgetId:" + aoCBServices.getContractBudgetID());
		return loEditStatus;
	}
	
	/**
	 * This method is added in R7 for Cost Center.It is used to fetch Cost Center Detailed grid data.
	 * @param aoMybatisSession
	 * @param aoCBGridBeanObj
	 * @return loInvoiceCostData - List
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<CBServicesBean> fetchCostCenterInvoiceGrid(SqlSession aoMybatisSession, CBGridBean aoCBGridBeanObj)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into fetchCostCenter for SubBudgetId:" + aoCBGridBeanObj.getSubBudgetID());
		List<CBServicesBean> loInvoiceCostData = null;
		try
		{
			loInvoiceCostData = (List<CBServicesBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSR5Constants.FETCH_INVOICE_COST_DATA,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("subBudget-ID : ", aoCBGridBeanObj.getSubBudgetID());
			LOG_OBJECT.Error("Exception occured  at fetching InvoiceService: fetchCostCenterInvoiceGrid() ", loAppEx);
			setMoState("InvoiceService: fetchCostCenterInvoiceGrid() failed to fetch at subBudget-ID:"
					+ aoCBGridBeanObj.getSubBudgetID() + " \n");
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieving in InvoiceService ", loAppEx);
			setMoState("Transaction Failed:: InvoiceService: fetchCostCenterInvoiceGrid method"
					+ " - failed to retrieve for subBudget-ID" + aoCBGridBeanObj.getSubBudgetID() + " \n");
			throw new ApplicationException("Exception occured while fetch in InvoiceService ", loAppEx);
		}
		LOG_OBJECT.Debug("Exited from fetchCostCenterInvoiceGrid for SubBudgetId:" + aoCBGridBeanObj.getSubBudgetID());
		return loInvoiceCostData;
	}
	
	/**
	 * This method is added in R7 for Cost Center.This method is used to update Cost center Detailed grid data.
	 * @param aoCBServices
	 * @param aoMybatisSession
	 * @return lbUpdateStatus
	 * @throws ApplicationException
	 */
	public Boolean editCostCenterInvoiceDetails(Boolean aoValid, CBServicesBean aoCBServices,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into editCostCenterInvoiceDetails for SubBudgetId:" + aoCBServices.getSubBudgetID());
		Boolean loEditStatus = true;
		Integer loRows = HHSConstants.INT_ZERO;
		try
		{
			if (aoValid)
			{
				loRows = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoCBServices,
						HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSR5Constants.EDIT_INVOICE_COST_CENTER,
						HHSR5Constants.COM_NYC_HHS_MODEL_CB_SERVICESBEAN);
				if (loRows == HHSConstants.INT_ZERO)
				{
					DAOUtil.masterDAO(aoMyBatisSession, aoCBServices, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
							HHSR5Constants.INSERT_INVOICE_COST_CENTER,
							HHSR5Constants.COM_NYC_HHS_MODEL_CB_SERVICESBEAN);
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
				setMoState("Transaction Failed:: InvoiceService: editCostCenterInvoiceDetails method - failed to update INVOICE_DETAIL"
						+ aoCBServices.getId() + " \n");
				LOG_OBJECT.Error("error occured while updating Invoice Services ", aoAppEx);
			throw aoAppEx;
		}
		
		  catch (Exception aoEx) {
		  LOG_OBJECT.Error(
		  "Exception occured in InvoiceService: editCostCenterInvoiceDetails method:: "
		  , aoEx); setMoState(
		  "Transaction Failed:: InvoiceService: editCostCenterInvoiceDetails method - failed to update INVOICE_DETAIL"
		  + aoCBServices.getId() + " \n"); throw new ApplicationException(
		  "Error occured while updating Invoice Amount for Cost Center", aoEx);
		  }
		return loEditStatus;
	}

	/**
	 * This method is added in R7 for Cost Center.This method is used to validate cost center remaining amount with invoice amount updated
	 * @param aoCBServices
	 * @param aoMyBatisSession
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean validateCostCenterInvoiceAmount(CBServicesBean aoCBServices,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		Boolean loValid = false;
		boolean lbError = false;
		try
		{			
			aoCBServices.setInvoiceAmountCurrent(aoCBServices.getYtdInvoicedAmt());
			aoCBServices.setTableAmountColumn(HHSConstants.GET_AMOUNT_DETAILS.get(HHSR5Constants.COST_CENTER_DETAILS));
			aoCBServices.setTableName(HHSR5Constants.COST_CENTER_DETAILS);
			aoCBServices.setTableId(HHSConstants.GET_ID_DETAILS.get(HHSR5Constants.COST_CENTER_DETAILS));
			aoCBServices.setLineItemId(aoCBServices.getId());
			BigDecimal loRemainingAmountPaymentDisbursed = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession,
					aoCBServices, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
					HHSR5Constants.FETCH_CC_REMAINING_PAYMENT_DISBURSED, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			BigDecimal loFYBudgetLineItem = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, aoCBServices,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FETCH_FY_BUDGET_LINE_ITEM,
					HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
			BigDecimal loRemainingAmount = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, aoCBServices,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSR5Constants.FETCH_INVOICE_AMOUNT_FOR_CC_LINE_ITEM,
					HHSR5Constants.COM_NYC_HHS_MODEL_CB_SERVICESBEAN);
			loRemainingAmount = loRemainingAmount.subtract(loRemainingAmountPaymentDisbursed);
			if (new BigDecimal(aoCBServices.getYtdInvoicedAmt()).compareTo(BigDecimal.ZERO) >= HHSConstants.INT_ZERO)
			{
				// case of positive invoices
				if (null != loRemainingAmount
						&& loRemainingAmount.compareTo(new BigDecimal(aoCBServices.getYtdInvoicedAmt())) >= 0)
				{
					loValid = true;
				}
				else
				{
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.MSG_KEY_INVOICE_AMOUNT_MORE_THAN_REMAINING_AMOUNT));
				}
			}
			else
			{
				// case of negative invoices
				if ((loFYBudgetLineItem.compareTo(loRemainingAmount.subtract(new BigDecimal(aoCBServices
						.getYtdInvoicedAmt())))) >= 0)
				{
					loValid = true;
				}
				else
				{
					lbError = true;
					throw new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSConstants.REMAINING_AMOUNT_LESS_THAN_FY_BUDGET));
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			if (lbError)
			{
				aoAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, aoAppEx.toString());
			}
			else
			{
				LOG_OBJECT.Error("Exception occured in InvoiceService: validateCostCenterInvoiceAmount method:: ",
						aoAppEx);
				setMoState("Transaction Failed:: InvoiceService: validateCostCenterInvoiceAmount method - failed to update INVOICE_DETAIL"
						+ aoCBServices.getId() + " \n");
			}
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in InvoiceService: validateCostCenterInvoiceAmount method:: ", aoEx);
			setMoState("Transaction Failed:: InvoiceService: validateCostCenterInvoiceAmount method - failed to validate"
					+ aoCBServices.getId() + " \n");
			throw new ApplicationException("Error occured while updating Invoice Amount for Cost Center Services", aoEx);
		}
		return loValid;
	}
	
	/**
	 * This method is added in R7 for Cost Center.This method is used to validate invoice amount services
	 * @param aoMyBatisSession
	 * @param asInvoiceId
	 * @param asBudgetId
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public HashMap invoiceAmountServicesAssignmentValidation(SqlSession aoMyBatisSession, String asInvoiceId, String asBudgetId)
			throws ApplicationException
	{
		HashMap loServicemap = new HashMap();
		Integer loFlag = HHSConstants.INT_ZERO;
		loServicemap.put(HHSConstants.SUCCESS, HHSConstants.STRING_TRUE);
		HashMap aoHashmap = new HashMap<String, String>();
		try
		{
			if(null != asBudgetId)
			{
				loFlag = (Integer) DAOUtil.masterDAO(aoMyBatisSession, asBudgetId,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSR5Constants.FETCH_FLAG,
						HHSConstants.JAVA_LANG_STRING);
				if (null != loFlag && loFlag == HHSConstants.INT_TWO)
				{
					performInvoiceServicesValidation(aoMyBatisSession, asInvoiceId, asBudgetId, loServicemap, aoHashmap);
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException occured in InvoiceService: invoiceAmountServicesAssignmentValidation method:: ",
							aoAppEx);
			setMoState("Error while fetching Invoice services validation for invoiceId:" + asInvoiceId);
			throw aoAppEx;
		}
		catch (Exception aoEx) 
		{
		    LOG_OBJECT.Error(
		    "Exception occured in InvoiceService: invoiceAmountServicesAssignmentValidation method:: ", aoEx); 
		    setMoState( "Transaction Failed:: InvoiceService: invoiceAmountServicesAssignmentValidation method - failed to validate for Invoice"
				 + asInvoiceId + " \n");
		    throw new ApplicationException( "Error occured while validating the Total Invoice Amount for the Assignments is greater than the Invoice Amount"
				 , aoEx); 
		 }
		return loServicemap;
	}

	/** This method is Added in R7 for Cost Center. It is getting called from
	 * invoiceAmountServicesAssignmentValidation method.
	 * @param aoMyBatisSession
	 * @param asInvoiceId
	 * @param asBudgetId
	 * @param loServicemap
	 * @param aoHashmap
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private void performInvoiceServicesValidation(SqlSession aoMyBatisSession, String asInvoiceId, String asBudgetId,
			HashMap loServicemap, HashMap aoHashmap) throws ApplicationException
	{
		List<CBGridBean> loSubBudgetList;
		aoHashmap.put(HHSConstants.BUDGET_ID_WORKFLOW, asBudgetId);
		loSubBudgetList = (List<CBGridBean>) DAOUtil.masterDAO(aoMyBatisSession, aoHashmap,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER, HHSConstants.FETCH_SUB_BUDGET_SUMMARY,
				HHSConstants.JAVA_UTIL_HASH_MAP);
		for (int liNumber = 0; liNumber < loSubBudgetList.size(); liNumber++)
		{
			BigDecimal loTotalInvoice = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, asInvoiceId,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSR5Constants.FETCH_COST_CENTER_AMOUNT_TOTAL,
					HHSConstants.JAVA_LANG_STRING);
			BigDecimal loAssignment = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, asInvoiceId,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSR5Constants.TOTAL_COST_CENTER_AMOUNT,
					HHSConstants.JAVA_LANG_STRING);
			if (!loTotalInvoice.equals(loAssignment))
			{
				loServicemap.put(HHSConstants.SUCCESS, HHSConstants.STRING_FALSE);
				loServicemap.put(HHSConstants.CBL_MESSAGE,
						HHSR5Constants.MSG_KEY_COST_CENTER_INVOICE_AMOUNT_MORE_THAN_REMAINING_AMOUNT);
				break;
			}
			loTotalInvoice = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, asInvoiceId,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSR5Constants.INVOICE_SERVICES_TOTAL,
					HHSConstants.JAVA_LANG_STRING);
			loAssignment = (BigDecimal) DAOUtil.masterDAO(aoMyBatisSession, asInvoiceId,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSR5Constants.ASSIGNMENT_AMOUNT_SERVICES,
					HHSConstants.JAVA_LANG_STRING);
			if (!loTotalInvoice.equals(loAssignment))
			{
				loServicemap.put(HHSConstants.SUCCESS, HHSConstants.STRING_FALSE);
				loServicemap.put(HHSConstants.CBL_MESSAGE,
						HHSR5Constants.MSG_KEY_SERVICE_INVOICE_AMOUNT_MORE_THAN_REMAINING_AMOUNT);
				break;
			}
		}
	}
	// End: Added in R7 for Cost Center
	
	/** Method Added in R7-To Fix Defect #7211
	 * (delete payment Accounting lines while doing 'Return for Revision' apart from level 1)
	 * @param aoMyBatisSession
	 * @param aoPaymentDetailMap
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<String> fetchPaymentsnotAtLevel1(SqlSession aoMyBatisSession,
			HashMap<String, String> aoPaymentDetailMap)
			throws ApplicationException {
		List<String> loPendingApprovedPaymentIdList = null;
		try {
			loPendingApprovedPaymentIdList = (List<String>) DAOUtil.masterDAO(
					aoMyBatisSession, aoPaymentDetailMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSConstants.FETCH_PAYMENTS_NOT_AT_LEVEL_ONE,
					HHSConstants.JAVA_UTIL_MAP);
			LOG_OBJECT
					.Info("Exiting fetchPaymentsnotAtLevel1 + loPendingApprovedPaymentIdList size ::  +  "
							+ loPendingApprovedPaymentIdList.toString());
		} catch (ApplicationException aoAppEx) {
			LOG_OBJECT
					.Error("ApplicationException occured in InvoiceService: fetchPaymentsnotAtLevel1 method:: ",
							aoAppEx);
			setMoState("Error while fetching Invoice services validation from method fetchPaymentsnotAtLevel1 for invoiceId:"
					+ aoPaymentDetailMap);
			throw aoAppEx;
		} catch (Exception aoEx) {
			LOG_OBJECT
					.Error("Exception occured in InvoiceService: fetchPaymentsnotAtLevel1 method:: ",
							aoEx);
			setMoState("Transaction Failed:: InvoiceService: fetchPaymentsnotAtLevel1 method - failed to validate for Invoice"
					+ aoPaymentDetailMap + " \n");
			throw new ApplicationException(
					"Error occured while validating the Total Invoice Amount for the Assignments is greater than the Invoice Amount in method fetchPaymentsnotAtLevel1",
					aoEx);
		}
		return loPendingApprovedPaymentIdList;
	}

}
