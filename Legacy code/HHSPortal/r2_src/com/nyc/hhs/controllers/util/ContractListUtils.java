package com.nyc.hhs.controllers.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.EPinDetailBean;
import com.nyc.hhs.model.ProgramNameInfo;

/**
 * <p>
 * This util class will be used for ContractListController. All decision making
 * or control flow is executed here.
 * </p>
 * 
 */
public class ContractListUtils
{

	private static final LogInfo LOG_OBJECT = new LogInfo(ContractListUtils.class);

	/**
	 * <p>
	 * This method takes inputs in channel object and fetches the program name
	 * list.
	 * <li>The transactio used: getProgramNameList</li>
	 * @param aoChannel a channel object
	 * @return loProgramNameList Program name List
	 * @throws ApplicationException </p>
	 * 
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static List<ProgramNameInfo> getProgramNameList(Channel aoChannel) throws ApplicationException
	{
		List<ProgramNameInfo> loProgramNameList = null;
		try
		{
			loProgramNameList = new ArrayList();
			HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.GET_PROG_NAME_LIST);
			loProgramNameList = (List<ProgramNameInfo>) aoChannel.getData(HHSConstants.PY_LO_PROGRAM_NAME_LIST);
		}
		// handling Application Exception if taking inputs in channel object
		catch (ApplicationException aoAppEx)
		{

			LOG_OBJECT.Error("ApplicationException Occured while taking inputs in channel object", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while taking inputs in channel object", aoEx);
		}
		return loProgramNameList;
	}

	/**
	 * <p>
	 * This method takes inputs in channel object and fetches the agency name
	 * list.
	 * <li>The transactio used: getAgencyDetails</li>
	 * @param aoChannel a channel object
	 * @return loAgencyDetails Agency Name, Code hashmap
	 * @throws ApplicationException </p>
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public static List<HashMap<String, String>> getAgencyDetails(Channel aoChannel) throws ApplicationException
	{
		List<HashMap<String, String>> loAgencyDetails = null;
		try
		{
			loAgencyDetails = new ArrayList<HashMap<String, String>>();
			HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.GET_AGENCY_DETAILS);
			loAgencyDetails = (List<HashMap<String, String>>) aoChannel.getData(HHSConstants.AGENCY_NAMES);
		}
		// handling Application Exception if taking inputs in channel object
		catch (ApplicationException aoAppEx)
		{

			LOG_OBJECT.Error("ApplicationException Occured while taking inputs in channel object", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while taking inputs in channel object", aoEx);
		}
		return loAgencyDetails;
	}

	/**
	 * <p>
	 * This method checks if renewal record exist or not.
	 * <li>The transactio used: renewalRecordExist</li>
	 * @param aoChannel a channel object
	 * @return lbIsRenewalRecordNotExist
	 * @throws ApplicationException </p>
	 * 
	 */
	public static Boolean getRenewalRecordExist(Channel aoChannel) throws ApplicationException
	{
		Boolean lbIsRenewalRecordNotExist = false;
		try
		{
			HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.RENEWAL_RECORD_EXIST);
			lbIsRenewalRecordNotExist = (Boolean) aoChannel.getData(HHSConstants.CONTRACT_RENEWAL_STATUS);

			if (lbIsRenewalRecordNotExist != null)
			{
				return lbIsRenewalRecordNotExist;
			}
		}
		// handling Application Exception if checking renewal record
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException Occured while checking if renewal record exist or not", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while checking if renewal record exist or not", aoEx);
		}
		return false;
	}

	/**
	 * <p>
	 * This method sets the modification user id in CBGridBean.
	 * @param aoGridBean a CBGridBean object
	 * @param asUserOrgType string containing usertype
	 * @param asUserId string containing userId
	 * @throws ApplicationException Application Exception
	 *             </p>
	 * 
	 */

	public static void setModifiedBy(CBGridBean aoGridBean, String asUserOrgType, String asUserId)
			throws ApplicationException
	{
		try
		{
			if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(asUserOrgType))
			{
				aoGridBean.setModifyByProvider(asUserId);
			}
			else
			{
				aoGridBean.setModifyByAgency(asUserId);
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While setting modification user id in CBGridBean",aoEx);
			LOG_OBJECT.Error("Error Occured While setting modification user id in CBGridBean", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * <p>
	 * This method checks if amendment error exist or not.
	 * <li>The transactio used: amendContractErrorCheck</li>
	 * Method updated in R4.
	 * </p>
	 * @param aoChannel a channel object
	 * @return loErrorCheckRule a hashmap of errors
	 * @throws ApplicationException
	 * 
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public static HashMap getAmendErrorCheck(Channel aoChannel) throws ApplicationException
	{
		HashMap loErrorCheckRule = null;
		try
		{
			loErrorCheckRule = new HashMap();
			HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.VALIDATE_CONTRACT_AMEND_BUSINESS_RULE);
			loErrorCheckRule = (HashMap) aoChannel.getData(HHSConstants.LO_ERROR_CHECK_RULE);
		}
		// handling Application Exception if amendment error exist
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException Occured while checking if amendment error exist or not", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while checking if amendment error exist or not", aoEx);
		}
		return loErrorCheckRule;
	}

	/**
	 * <p>
	 * This method gets the contract amendment id.
	 *  <li>The transactio used: selectContractAmendmentId</li>
	 * @param aoChannel a channel object
	 * @return loContractMap a map of errors
	 * @throws ApplicationException </p>
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public static Map selectContractAmendmentId(Channel aoChannel) throws ApplicationException
	{
		Map loContractMap = null;
		try
		{
			loContractMap = new HashMap();
			HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.SELECT_CONTRACT_AMENDMENT_ID);
			loContractMap = (Map) aoChannel.getData(HHSConstants.LO_CONTRACT_MAP);
		}
		// handling Application Exception if error exist while getting contract
		// amendment id
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException Occured while getting contract amendment id", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while getting contract amendment id", aoEx);
		}
		return loContractMap;
	}

	/**
	 * <p>
	 * This method validates the epin.
	 * <li>The transactio used: validateEpin</li>
	 * @param aoChannel a channel object
	 * @return lbIsEpinValid
	 * @throws ApplicationException </p>
	 * 
	 */
	public static boolean validateEpin(Channel aoChannel) throws ApplicationException
	{
		boolean lbIsEpinValid = false;
		try
		{
			HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.VALIDATE_EPIN);
			lbIsEpinValid = (Boolean) aoChannel.getData(HHSConstants.LB_SUCCESS_STATUS);
		}
		// handling Application Exception if error exists while validating the
		// epin
		catch (ApplicationException aoAppEx)
		{

			LOG_OBJECT.Error("ApplicationException Occured while validating the epin", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while validating the epin", aoEx);
		}

		return lbIsEpinValid;
	}

	/** 
	 * <p>
	 * This method validated the pin for Bulk upload batch
	 *  <li>The transactio used: validateEpinBulk</li>
	 *  This method was added in R4
	 *  </p>
	 * @param aoChannel - Channel Object
	 * @return lbIsEpinValid - boolean object
	 * @throws ApplicationException - ApplicationException Object
	 */
	public static boolean validateEpinBulk(Channel aoChannel) throws ApplicationException
	{
		boolean lbIsEpinValid = false;
		try
		{
			HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.VALIDATE_EPIN_BULK);
			lbIsEpinValid = (Boolean) aoChannel.getData(HHSConstants.LB_SUCCESS_STATUS);
		}
		// handling Application Exception if error exists while validating the
		// epin
		catch (ApplicationException aoAppEx)
		{

			LOG_OBJECT.Error("ApplicationException Occured while validating the epin", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while validating the epin", aoEx);
		}

		return lbIsEpinValid;
	}

	/**
	 * <p>
	 * This method validates the provider/accelerator user.
	 * <li>The transaction used: validateProviderInAccelerator</li>
	 * @param aoChannel a channel object
	 * @return lbProviderRegistered
	 * @throws ApplicationException </p>
	 * 
	 */
	public static boolean validateProviderAccelerator(Channel aoChannel) throws ApplicationException
	{
		boolean lbProviderRegistered = false;
		try
		{
			HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.VALIDATE_PROVIDER_ACCELERATOR);
			lbProviderRegistered = (Boolean) aoChannel.getData(HHSConstants.LB_PROVIDER_ACCELEARTOR);
		}
		// handling Application Exception if error exists while validating
		// provider/accelerator user
		catch (ApplicationException aoAppEx)
		{

			LOG_OBJECT.Error("ApplicationException Occured while validating provider/accelerator user", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while validating provider/accelerator user", aoEx);
		}
		return lbProviderRegistered;
	}

	/** 
	 * <p>
	 * This method validate the provider for bulk upload batch
	 * <li>The transaction used: validateProviderInAcceleratorBulk</li>
	 * This method was added in R4
	 * </p>
	 * @param aoChannel -Channel Object
	 * @return lbProviderRegistered - boolean Object
	 * @throws ApplicationException - ApplicationException Object
	 */
	public static boolean validateProviderAcceleratorBulk(Channel aoChannel) throws ApplicationException
	{
		boolean lbProviderRegistered = false;
		try
		{
			HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.VALIDATE_PROVIDER_ACCELERATOR_BULK);
			lbProviderRegistered = (Boolean) aoChannel.getData(HHSConstants.LB_PROVIDER_ACCELEARTOR);
		}
		// handling Application Exception if error exists while validating
		// provider/accelerator user
		catch (ApplicationException aoAppEx)
		{

			LOG_OBJECT.Error("ApplicationException Occured while validating provider/accelerator user", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while validating provider/accelerator user", aoEx);
		}
		return lbProviderRegistered;
	}

	/**
	 * R6: Checks if the combination of epin and agency is unique in contract table
	 * @param loEPinDetailBean : EPinDetailBean object containing Epin and agency division
	 * @return lbIsEpinValid : Boolean value returning success status
	 */
	public static boolean validateEpinUnique(EPinDetailBean aoEPinDetailBean) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered validateEpinUnique with parameters : "+ aoEPinDetailBean);
		Channel aoChannel = new Channel();
		boolean lbIsEpinValid=false;
		aoChannel.setData(HHSConstants.AO_CONTRACT_DETAILS_BY_EPIN, aoEPinDetailBean);
		try
		{
			HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.VALIDATE_EPIN_IS_UNIQUE);
			lbIsEpinValid = (Boolean) aoChannel.getData(HHSConstants.LB_SUCCESS_STATUS);
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while validating uniqueness of EPIN", aoEx);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while validating uniqueness of EPIN", aoEx);
		}
		return lbIsEpinValid;
	}
}
