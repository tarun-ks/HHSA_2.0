package com.nyc.hhs.daomanager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.FiscalYear;
import com.nyc.hhs.model.NycAgencyDetails;
import com.nyc.hhs.model.PaymentSortAndFilter;
import com.nyc.hhs.model.ProgramNameInfo;
import com.nyc.hhs.model.StatusR3;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This service class will be used to get all data for Payment List page
 * 
 * @author a.rohilla
 * 
 */
public class PaymentListService extends ServiceState
{
	/**
	 * LOG OBJECT for PaymentListService class.
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(PaymentListService.class);

	/**
	 * This method return the list of payment record to be displayed in the grid
	 * on payment list jsp. Record that are fetched will vary on the basis of
	 * the organization type i.e. different record list will be displayed for
	 * provider/agency/accelerator and will be sorted as per the column name
	 * mentioned in design document. For sorting and pagination various variable
	 * like firstSort/firstSortType/startNode/endNode are set and corresponding
	 * results are displayed. <li>This query used:
	 * fetchPaymentListSummaryProvider</li> <li>This query used:
	 * fetchPaymentListSummaryAgency</li> <li>This query used:
	 * fetchPaymentListSummaryCity</li>
	 * @param aoMybatisSession Sql seesion as input parameter.
	 * @param aoPaymentBean Payment bean as input.This will contain the
	 *            parameters for where clause used in select query(like
	 *            organization type and sort type)
	 *            <p>
	 * @return loPaymentList List of the Payment on the basis of organization
	 *         type , their sort column name and type, second column name and
	 *         type.
	 * @throws ApplicationException Exception thrown in case sql query fails.
	 */
	@SuppressWarnings("unchecked")
	public List<PaymentSortAndFilter> fetchPaymentListSummary(SqlSession aoMybatisSession,
			PaymentSortAndFilter aoPaymentBean) throws ApplicationException
	{
		List<PaymentSortAndFilter> loPaymentList = null;
		List<String> loContractForRestriction = null;
		try
		{
			defaultStatusOnPayment(aoPaymentBean, aoPaymentBean.getOrgType());
			if (aoPaymentBean.getPaymentStatusList() != null)
			{
				// remove comma from payment value
				if (aoPaymentBean.getPaymentValueFrom() != null)
				{
					aoPaymentBean.setPaymentValueFrom(aoPaymentBean.getPaymentValueFrom().replaceAll(
							HHSConstants.COMMA, HHSConstants.EMPTY_STRING));
				}
				if (aoPaymentBean.getPaymentValueTo() != null)
				{
					aoPaymentBean.setPaymentValueTo(aoPaymentBean.getPaymentValueTo().replaceAll(HHSConstants.COMMA,
							HHSConstants.EMPTY_STRING));
				}
				if (aoPaymentBean.getOrgType() != null
						&& aoPaymentBean.getOrgType().equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
				{
					loPaymentList = (List<PaymentSortAndFilter>) DAOUtil.masterDAO(aoMybatisSession, aoPaymentBean,
							HHSConstants.MAPPER_CLASS_FINANCIAL_PAYMENT_MAPPER,
							HHSConstants.PL_FETCH_PAYMENT_LIST_SUMMARY_PROVIDER,
							HHSConstants.PL_PAYMENT_SORT_AND_FILTER);
					//Release 5 Contract Restriction 
					loContractForRestriction = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoPaymentBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
							HHSR5Constants.FETCH_CONTRACT_FOR_RESTRICTION,
							HHSR5Constants.COM_NYC_HHS_MODEL_BASE_FILTER);
					//Release 5 Contract Restriction 

				}
				else if (aoPaymentBean.getOrgType() != null
						&& aoPaymentBean.getOrgType().equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
				{
					loPaymentList = (List<PaymentSortAndFilter>) DAOUtil.masterDAO(aoMybatisSession, aoPaymentBean,
							HHSConstants.MAPPER_CLASS_FINANCIAL_PAYMENT_MAPPER,
							HHSConstants.PL_FETCH_PAYMENT_LIST_SUMMARY_AGENCY, HHSConstants.PL_PAYMENT_SORT_AND_FILTER);
				}
				else if (aoPaymentBean.getOrgType() != null
						&& aoPaymentBean.getOrgType().equalsIgnoreCase(ApplicationConstants.CITY_ORG))
				{
					loPaymentList = (List<PaymentSortAndFilter>) DAOUtil.masterDAO(aoMybatisSession, aoPaymentBean,
							HHSConstants.MAPPER_CLASS_FINANCIAL_PAYMENT_MAPPER,
							HHSConstants.PL_FETCH_PAYMENT_LIST_SUMMARY_CITY, HHSConstants.PL_PAYMENT_SORT_AND_FILTER);
				}
			}
			
			//Release 5 Contract Restriction 
			if (loContractForRestriction != null)
			{
				for (PaymentSortAndFilter loPayment : loPaymentList)
				{
					for (String lsContractForRestriction : loContractForRestriction)
					{
						if (null != lsContractForRestriction
								&& (Integer.parseInt(lsContractForRestriction) == Integer.parseInt(loPayment.getContractId())))
						{
							loPayment.setContractAccess(false);
						}
					}
				}
			}
			//Release 5 Contract Restriction 
			setMoState(HHSConstants.PL_PAYMENT_DETAILS + aoPaymentBean.getOrgType());
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.PL_AS_PAYMENT_ID, aoPaymentBean.getPaymentId());
			LOG_OBJECT.Error(HHSConstants.PL_FETCHING_PAYMENT_DETAILS, loAppEx);
			setMoState(HHSConstants.PL_FETCHING_PAYMENT + aoPaymentBean.getOrgType());
			throw loAppEx;
		}
		return loPaymentList;
	}

	/***
	 * This method set the default status for city/agency/provider user
	 * @param aoPaymentBean PaymentSortAndFilter bean as input.
	 * @param asOrgType organization type as input.
	 * @throws ApplicationException Exception thrown in case sql query fails.
	 */
	private void defaultStatusOnPayment(PaymentSortAndFilter aoPaymentBean, String asOrgType)
			throws ApplicationException
	{
		try
		{
			// Start || changes done for enhancement 6495 for Release 3.12.0
			if ((aoPaymentBean.getPaymentStatusList() == null || aoPaymentBean.getPaymentStatusList().isEmpty() || aoPaymentBean
					.getPaymentStatusList().size() == HHSConstants.INT_ZERO)
					&& (aoPaymentBean.getIsFilter() != null && !aoPaymentBean.getIsFilter()))
			{
				aoPaymentBean.setPaymentStatusList(new ArrayList<String>());

				if (asOrgType != null && asOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
				{
					aoPaymentBean.getPaymentStatusList().add(
							PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_PAYMENT_DISBURSED));
					aoPaymentBean.getPaymentStatusList().add(
							PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_PAYMENT_APPROVED));
					aoPaymentBean.getPaymentStatusList().add(
							PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_PAYMENT_PENDING_FMS_ACTION));
				}
				else{
				// End || changes done for enhancement 6495 for Release 3.12.0
				aoPaymentBean.getPaymentStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PAYMENT_PENDING_APPROVAL));
				aoPaymentBean.getPaymentStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PAYMENT_SUSPENDED));
				aoPaymentBean.getPaymentStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PAYMENT_APPROVED));
				aoPaymentBean.getPaymentStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PAYMENT_PENDING_FMS_ACTION));
				aoPaymentBean.getPaymentStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PAYMENT_DISBURSED));
				aoPaymentBean.getPaymentStatusList().add(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PAYMENT_WITHDRAWN));
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.PL_AS_PAYMENT_ID, aoPaymentBean.getPaymentId());
			LOG_OBJECT.Error(HHSConstants.PL_FETCHING_PAYMENT_DETAILS, loAppEx);
			setMoState(HHSConstants.PL_FETCHING_PAYMENT + aoPaymentBean.getOrgType());
		}
	}

	/**
	 * This method will get the count of payment record for city/provider/agency
	 * on payment list screen.
	 * <p>
	 * Count will be displayed on the jsp and also help us to get page index
	 * which is result of (count of payment records/number allowed per page in
	 * grid).
	 * <li>This query used: getPaymentCountProvider</li>
	 * <li>This query used: getPaymentCountAgency</li>
	 * <li>This query used: getPaymentCountCity</li>
	 * @param aoMybatisSession Sql session as input parameter.
	 * @param aoPaymentBean Payment bean as input.This will contain the
	 *            parameters for where clause used in select query(like
	 *            organization type)
	 * @return liPaymentListCount Count of records on payment screen.
	 * @throws ApplicationException Exception thrown in case sql query fails.
	 */
	public Integer getPaymentCount(SqlSession aoMybatisSession, PaymentSortAndFilter aoPaymentBean)
			throws ApplicationException
	{
		Integer loPaymentListCount = HHSConstants.INT_ZERO;
		try
		{
			if (aoPaymentBean.getPaymentStatusList() != null)
			{
				if (aoPaymentBean.getOrgType() != null
						&& aoPaymentBean.getOrgType().equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
				{
					loPaymentListCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoPaymentBean,
							HHSConstants.MAPPER_CLASS_FINANCIAL_PAYMENT_MAPPER,
							HHSConstants.PL_GET_PAYMENT_COUNT_PROVIDER, HHSConstants.PL_PAYMENT_SORT_AND_FILTER);
				}
				else if (aoPaymentBean.getOrgType() != null
						&& aoPaymentBean.getOrgType().equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
				{
					loPaymentListCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoPaymentBean,
							HHSConstants.MAPPER_CLASS_FINANCIAL_PAYMENT_MAPPER,
							HHSConstants.PL_GET_PAYMENT_COUNT_AGENCY, HHSConstants.PL_PAYMENT_SORT_AND_FILTER);
				}
				else if (aoPaymentBean.getOrgType() != null
						&& aoPaymentBean.getOrgType().equalsIgnoreCase(ApplicationConstants.CITY_ORG))
				{
					loPaymentListCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoPaymentBean,
							HHSConstants.MAPPER_CLASS_FINANCIAL_PAYMENT_MAPPER, HHSConstants.PL_GET_PAYMENT_COUNT_CITY,
							HHSConstants.PL_PAYMENT_SORT_AND_FILTER);
				}
			}
			setMoState(HHSConstants.PL_PAYMENT_COUNT_FETCHED_SUCCESSFULLY_FOR_USER_TYPE + aoPaymentBean.getOrgType());
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.PL_AS_PAYMENT_ID, aoPaymentBean.getPaymentId());
			LOG_OBJECT.Error(HHSConstants.PL_EXCEPTION_OCCURED_WHILE_FETCHING_PAYMENT_COUNT, loAppEx);
			setMoState(HHSConstants.PL_ERROR_WHILE_FETCHING_PAYMENT_COUNT_FOR_USER_TYPE + aoPaymentBean.getOrgType());
			throw loAppEx;
		}
		return loPaymentListCount;
	}

	/**
	 * This method get the fiscal year information for filter on list screen.
	 * <li>This query used: getFiscalInformation</li>
	 * @param aoMybatisSession Sql session as input parameter.
	 * @param asOrgType Organization type as input.
	 * @param asOrgId Organization Id as input.
	 * @return loFiscalInformation Fiscal year information.
	 * @throws ApplicationException Exception thrown in case sql query fails.
	 */
	@SuppressWarnings("unchecked")
	public List<FiscalYear> getFiscalInformation(SqlSession aoMybatisSession, String asOrgType, String asOrgId)
			throws ApplicationException
	{
		List<FiscalYear> loFiscalInformation = null;
		String lsActiveFlag = HHSConstants.ONE;
		try
		{
			loFiscalInformation = (List<FiscalYear>) DAOUtil.masterDAO(aoMybatisSession, lsActiveFlag,
					HHSConstants.MAPPER_CLASS_FINANCIAL_PAYMENT_MAPPER, HHSConstants.PL_GET_FISCAL_INFORMATION,
					HHSConstants.JAVA_LANG_STRING);
			setMoState(HHSConstants.PL_FISCAL_YEAR_INFORMATION + asOrgType);
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.PL_ORGANIZATION_ID, asOrgId);
			LOG_OBJECT.Error(HHSConstants.PL_EXCEPTION_FISCAL_YEAR_INFORMATION, loExp);
			setMoState(HHSConstants.PL_ERROR_FISCAL_YEAR_INFORMATION + asOrgId);
			throw loExp;
		}
		return loFiscalInformation;
	}

	/**
	 * This method get the status information for filter on list screen. <li>
	 * This query used: getStatusList</li>
	 * @param aoMybatisSession Sql session as input parameter.
	 * @param asOrgType Organization Type as input parameter.
	 * @param asOrgId Organization Id as input parameter.
	 * @param asProcessType Process type(like Payment,Invoice etc) as input
	 *            parameter.
	 * @return loPaymentStatus payment statuses as output.
	 * @throws ApplicationException Exception thrown in case sql query fails.
	 */
	@SuppressWarnings("unchecked")
	public List<StatusR3> getSatusList(SqlSession aoMybatisSession, String asOrgType, String asOrgId,
			String asProcessType) throws ApplicationException
	{
		List<StatusR3> loStatusList = null;
		try
		{
			loStatusList = (List<StatusR3>) DAOUtil.masterDAO(aoMybatisSession, asProcessType,
					HHSConstants.MAPPER_CLASS_FINANCIAL_PAYMENT_MAPPER, HHSConstants.PL_GET_STATUS_LIST,
					HHSConstants.JAVA_LANG_STRING);
			// Start || changes done for enhancement 6495 for Release 3.12.0
			if((null != asProcessType && asProcessType.equalsIgnoreCase(HHSConstants.PAYMENT)) && 
					(null != asOrgType && asOrgType.equalsIgnoreCase(HHSConstants.PROVIDER_ORG))){
				loStatusList = (List<StatusR3>) DAOUtil.masterDAO(aoMybatisSession, asProcessType,
						HHSConstants.MAPPER_CLASS_FINANCIAL_PAYMENT_MAPPER, HHSConstants.GET_PAYMENT_STATUS_LIST_PROVIDER,
						HHSConstants.JAVA_LANG_STRING);
			}
			// End || changes done for enhancement 6495 for Release 3.12.0

			setMoState(HHSConstants.PL_STATUS_INFORMATION + asOrgType);
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.PL_ORGANIZATION_ID, asOrgId);
			LOG_OBJECT.Error(HHSConstants.PL_EXCEPTION_STATUS_INFORMATION, loExp);
			setMoState(HHSConstants.PL_ERROR_STATUS_INFORMATION + asOrgId);
			throw loExp;
		}
		return loStatusList;
	}

	/**
	 * This method get the agency information for filter on list screen. <li>
	 * This query used: getAgencyList</li>
	 * @param aoMybatisSession Sql session as input parameter.
	 * @param asOrgType Organization Type as input parameter.
	 * @param asOrgId Organization Id as input parameter.
	 * @return loAgencyList agency list as output.
	 * @throws ApplicationException Exception thrown in case sql query fails.
	 */
	@SuppressWarnings("unchecked")
	public List<NycAgencyDetails> getAgencyList(SqlSession aoMybatisSession, String asOrgType, String asOrgId)
			throws ApplicationException
	{
		List<NycAgencyDetails> loAgencyList = null;
		try
		{
			loAgencyList = (List<NycAgencyDetails>) DAOUtil.masterDAO(aoMybatisSession, HHSConstants.PL_PAYMENT_AGENCY,
					HHSConstants.MAPPER_CLASS_FINANCIAL_PAYMENT_MAPPER, HHSConstants.PL_GET_AGENCY_LIST,
					HHSConstants.JAVA_LANG_STRING);
			setMoState(HHSConstants.PL_AGENCY_LIST_INFORMATION + asOrgType);
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.PL_ORGANIZATION_ID, asOrgId);
			LOG_OBJECT.Error(HHSConstants.PL_EXCEPTION_AGENCY_LIST, loExp);
			setMoState(HHSConstants.PL_AGENCY_LIST + asOrgId);
			throw loExp;
		}
		return loAgencyList;
	}

	/**
	 * This method get the Program Name List for filter on list screen. <li>This
	 * query used: getProgramNameAgency</li>
	 * @param aoMybatisSession Sql session as input parameter.
	 * @param asOrgType Organization Type as input parameter.
	 * @param asOrgId Organization Id as input parameter.
	 * @return loProgramNameList as output.
	 * @throws ApplicationException Exception thrown in case sql query fails.
	 */
	@SuppressWarnings("unchecked")
	public List<ProgramNameInfo> getProgramName(SqlSession aoMybatisSession, String asOrgType, String asOrgId)
			throws ApplicationException
	{
		List<ProgramNameInfo> loProgramNameList = null;
		String lsActiveFlag = HHSConstants.ONE;
		HashMap<String, String> loProgramMap = new HashMap<String, String>();
		loProgramMap.put(HHSConstants.ACTIVE_FLAG, lsActiveFlag);
		loProgramMap.put(HHSConstants.ORG_ID, asOrgId);
		try
		{
			loProgramNameList = (List<ProgramNameInfo>) DAOUtil.masterDAO(aoMybatisSession, loProgramMap,
					HHSConstants.MAPPER_CLASS_FINANCIAL_PAYMENT_MAPPER, HHSConstants.PL_GET_PROGRAM_NAME_AGENCY,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			setMoState(HHSConstants.PL_PROGRAM_NAME_INFORMATION + asOrgType);
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.PL_ORGANIZATION_ID, asOrgId);
			LOG_OBJECT.Error(HHSConstants.PL_FETCHING_PROGRAM_NAME_LIST, loExp);
			setMoState(HHSConstants.PL_PROGRAM_NAME + asOrgId);
			throw loExp;
		}
		return loProgramNameList;
	}

	/**
	 * This methdod get the list of budget id from contract id and budget type
	 * id who are currently visible on budget list. <li>This query used:
	 * fetchBudgetIdListFromContractId</li>
	 * @param aoMyBatisSession sql session as input
	 * @param contractId contract id as input
	 * @param budgetTypeId budget type id as input
	 * @return loBudgetIdList list of budget id
	 * @throws ApplicationException Exception thrown in case sql query fails.
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<HashMap> fetchBudgetIdListFromContractId(SqlSession aoMyBatisSession, String contractId,
			String budgetTypeId) throws ApplicationException
	{
		List<HashMap> loBudgetIdList = null;
		HashMap loInputParams = new HashMap<String, String>();
		loInputParams.put(HHSConstants.CONTRACT_ID_WORKFLOW, contractId);
		loInputParams.put(HHSConstants.BUDGET_TYPE_ID, budgetTypeId);
		loInputParams.put(HHSConstants.HHSUTIL_ACTIVEFLAG, HHSConstants.ONE);
		try
		{
			loBudgetIdList = (List<HashMap>) DAOUtil.masterDAO(aoMyBatisSession, loInputParams,
					HHSConstants.MAPPER_CLASS_FINANCIAL_PAYMENT_MAPPER, HHSConstants.FETCH_BUDGET_ID_FROM_CONTRACT_ID,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData(HHSConstants.CONTRACT_ID_WORKFLOW, contractId);
			LOG_OBJECT
					.Error("Exception occured while fetching budget list from its  contract id with active flag i.e visible on screen",
							loExp);
			setMoState(HHSConstants.CONTRACT_ID_WORKFLOW + contractId);
			throw loExp;
		}
		return loBudgetIdList;

	}

}
