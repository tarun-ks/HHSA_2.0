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
import com.nyc.hhs.model.InvoiceList;
import com.nyc.hhs.service.filenetmanager.p8services.P8ContentService;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This service class will be used to handle all service request for the invoice
 * list controller
 */
public class FinancialsInvoiceListService extends ServiceState
{

	/**
	 * LogInfo object
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(FinancialsInvoiceListService.class);

	/**
	 * This method is used to fetch all the invoices based on user
	 * type(Accelerator/Agency/Provider). Data will be fetched on basis of
	 * filter values as received in InvoiceListFilter as an input.
	 * 
	 * <ul>
	 * <li>The Method <code>fetchInvoiceListSummary</code> calls different
	 * mapper methods based on the type of user. It has parameters
	 * <code>SqlSession, invioceList and
	 * userTYpe</code>.
	 * <li>With the help of the invoiceList bean it will fetch the different
	 * beans associated with that invoiceListItem. Once the controller hits the
	 * executeTransaction for the transaction to render the S309 screen, this is
	 * called.
	 * <li>Initialize a list of InvoiceList. Now compare the userType of the
	 * Organization. based on the different UserType DAO calls are done. The
	 * DaoUtil will hit the MasterDao and it will call its
	 * <code>MAPPER_CLASS_INVOICE_MAPPER</code> mapper as follows.
	 * <p>
	 * <li>1.If user type is Provider, then we will call the
	 * <code>fetchInvoiceListProvider</code> query to get the required invoices
	 * list</li>
	 * <li>2.If user type is Agency, then we will call the
	 * <code>fetchInvoiceListAgency</code> query to get the required invoices
	 * list</li>
	 * <li>3.If user type is Accelerator, then we will call the
	 * <code>fetchinvoiceListAccelerator</code> query to get the required
	 * invoices list</li>
	 * </p>
	 * <li>Now this will run for all the items present in the invioceList and
	 * will render the page.
	 * <li>Incase of any Exception we will be handling it in the
	 * ApplicationException and throwing it further.
	 * <li>Else return the invoiceList.
	 * </ul>
	 * 
	 * @param aoMybatisSession mybatis SQL session
	 * @param aoInvoiceFilterBean InvoiceListFilter
	 * @param asUserType String type of UserType
	 * @return List<InvoiceList> object
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<InvoiceList> fetchInvoiceListSummary(SqlSession aoMybatisSession, InvoiceList aoInvoiceFilterBean,
			String asUserType) throws ApplicationException
	{
		List<InvoiceList> loInvoiceList = null;
		List<String> loContractForRestriction = null;
		try
		{
			if (null != aoInvoiceFilterBean.getLsRequestFromHomePage()
					&& aoInvoiceFilterBean.getLsRequestFromHomePage().equals(HHSConstants.FALSE))
			{
				defaultStatusOnInvoice(aoInvoiceFilterBean, aoInvoiceFilterBean.getOrgType());
			}
			if (aoInvoiceFilterBean.getInvoiceStatusList() != null)
			{
				if (aoInvoiceFilterBean.getInvoiceValueFrom() != null)
				{
					aoInvoiceFilterBean.setInvoiceValueFrom(aoInvoiceFilterBean.getInvoiceValueFrom().replaceAll(
							HHSConstants.COMMA, HHSConstants.EMPTY_STRING));
				}
				if (aoInvoiceFilterBean.getInvoiceValueTo() != null)
				{
					aoInvoiceFilterBean.setInvoiceValueTo(aoInvoiceFilterBean.getInvoiceValueTo().replaceAll(
							HHSConstants.COMMA, HHSConstants.EMPTY_STRING));
				}
				if (aoInvoiceFilterBean.getOrgType() != null
						&& aoInvoiceFilterBean.getOrgType().equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
				{
					loInvoiceList = (List<InvoiceList>) DAOUtil.masterDAO(aoMybatisSession, aoInvoiceFilterBean,
							HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FILS_FETCH_INVOICE_LIST_PROVIDER,
							HHSConstants.FILS_COM_NYC_HHS_MODEL_INVOICE_LIST);
					// Release 5 Contract Restriction
					loContractForRestriction = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoInvoiceFilterBean,
							HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSR5Constants.FETCH_CONTRACT_FOR_RESTRICTION,
							HHSR5Constants.COM_NYC_HHS_MODEL_BASE_FILTER);
					// Release 5 Contract Restriction

				}
				else if (aoInvoiceFilterBean.getOrgType() != null
						&& aoInvoiceFilterBean.getOrgType().equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
				{
					loInvoiceList = (List<InvoiceList>) DAOUtil.masterDAO(aoMybatisSession, aoInvoiceFilterBean,
							HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FILS_FETCH_INVOICE_LIST_AGENCY,
							HHSConstants.FILS_COM_NYC_HHS_MODEL_INVOICE_LIST);
				}
				else if (aoInvoiceFilterBean.getOrgType() != null
						&& aoInvoiceFilterBean.getOrgType().equalsIgnoreCase(ApplicationConstants.CITY_ORG))
				{
					loInvoiceList = (List<InvoiceList>) DAOUtil.masterDAO(aoMybatisSession, aoInvoiceFilterBean,
							HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FILS_FETCH_INVOICE_LIST_ACCELERATOR,
							HHSConstants.FILS_COM_NYC_HHS_MODEL_INVOICE_LIST);
				}
			}

			// Release 5 Contract Restriction
			if (loContractForRestriction != null)
			{
				for (InvoiceList loInvoice : loInvoiceList)
				{
					for (String lsContractForRestriction : loContractForRestriction)
					{
						if (null != lsContractForRestriction
								&& (Integer.parseInt(lsContractForRestriction) == Integer.parseInt(loInvoice
										.getInvoiceContractId())))
						{
							loInvoice.setContractAccess(false);
						}
					}
				}
			}
			// Release 5 Contract Restriction
			setMoState("Invoice List fetched successfully for org Type:" + aoInvoiceFilterBean.getOrgType());

		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("asInvoiceId", aoInvoiceFilterBean.getInvoiceNumber());
			LOG_OBJECT.Error("Exception occured while fetching Invoice details", loAppEx);
			setMoState("Error while fetching Invoice details for user Type:" + aoInvoiceFilterBean.getOrgType());
			throw loAppEx;
		}
		return loInvoiceList;
	}

	/***
	 * This method set the default status for city/agency/provider user
	 * 
	 * @param aoInvoiceBean InvoiceSortAndFilter bean as input.
	 * @param asOrgType organization type as input.
	 */
	private void defaultStatusOnInvoice(InvoiceList aoInvoiceBean, String asOrgType)
	{
		if ((aoInvoiceBean.getInvoiceStatusList() == null || aoInvoiceBean.getInvoiceStatusList().size() == HHSConstants.INT_ZERO)
				&& (aoInvoiceBean.getIsFilter() != null && !aoInvoiceBean.getIsFilter()))
		{
			List<String> loInvoiceStatusList = new ArrayList<String>();
			aoInvoiceBean.setInvoiceStatusList(loInvoiceStatusList);
			if (asOrgType != null
					&& (asOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG) || asOrgType
							.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG)))
			{
				aoInvoiceBean.getInvoiceStatusList().add(HHSConstants.PENDING_APPROVAL);
				aoInvoiceBean.getInvoiceStatusList().add(HHSConstants.RETURNED_FOR_REVISION);
				aoInvoiceBean.getInvoiceStatusList().add(HHSConstants.APPROVED);
				aoInvoiceBean.getInvoiceStatusList().add(HHSConstants.WITHDRAWN);
				aoInvoiceBean.getInvoiceStatusList().add(HHSConstants.SUSPENDED);
			}
			else if (asOrgType != null && asOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
			{
				aoInvoiceBean.getInvoiceStatusList().add(HHSConstants.PENDING_SUBMISSION);
				aoInvoiceBean.getInvoiceStatusList().add(HHSConstants.PENDING_APPROVAL);
				aoInvoiceBean.getInvoiceStatusList().add(HHSConstants.RETURNED_FOR_REVISION);
				aoInvoiceBean.getInvoiceStatusList().add(HHSConstants.APPROVED);
				aoInvoiceBean.getInvoiceStatusList().add(HHSConstants.WITHDRAWN);
				aoInvoiceBean.getInvoiceStatusList().add(HHSConstants.SUSPENDED);
			}
		}
	}

	/**
	 * This method gets count of all invoices available for particular user.
	 * Based on the <code>UserType</code> the count will be populated by the
	 * <code>invoiceListCount</code> querying the DB from the mapper class
	 * <code>MAPPER_CLASS_INVOICE_MAPPER</code> which will store the result in
	 * the <code>invoiceCount</code> There are three cases for the
	 * <code>UserType</code> and the comparison will be done first thereafter
	 * which the respective query will be run.
	 * <ul>
	 * <li>1.If user type is Provider, then we will call the
	 * <code>fetchInvoiceCountProvider</code> query to get the required invoices
	 * list from invoiceMapper</li>
	 * <li>2.If user type is Agency, then we will call the
	 * <code>fetchInvoiceCountAgency</code> query to get the required invoices
	 * list from invoiceMapper</li>
	 * <li>3.If user type is Accelerator, then we will call the
	 * <code>fetchInvoiceCountAccelerator</code> query to get the required
	 * invoices list from invoiceMapper</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession mybatis SQL session
	 * @param aoInvoiceBean object of InvoiceList
	 * @param asUserType string containing user type
	 * @return liInvoiceCount an integer value of invoices count
	 * 
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Integer getInvoiceCount(SqlSession aoMybatisSession, InvoiceList aoInvoiceBean, String asUserType)
			throws ApplicationException
	{
		Integer liInvoiceCount = HHSConstants.INT_ZERO;
		try
		{
			if (aoInvoiceBean.getInvoiceStatusList() != null)
			{
				if (asUserType.equals(ApplicationConstants.PROVIDER_ORG))
				{
					liInvoiceCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoInvoiceBean,
							HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FILS_FETCH_INVOICE_COUNT_PROVIDER,
							HHSConstants.FILS_COM_NYC_HHS_MODEL_INVOICE_LIST);

				}
				else if (asUserType.equals(ApplicationConstants.AGENCY_ORG))
				{
					liInvoiceCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoInvoiceBean,
							HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.FILS_FETCH_INVOICE_COUNT_AGENCY,
							HHSConstants.FILS_COM_NYC_HHS_MODEL_INVOICE_LIST);
				}
				else
				{
					liInvoiceCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoInvoiceBean,
							HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
							HHSConstants.FILS_FETCH_INVOICE_COUNT_ACCELERATOR,
							HHSConstants.FILS_COM_NYC_HHS_MODEL_INVOICE_LIST);
				}
			}
			setMoState("Invoices count fetched successfully for org type:" + asUserType);
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting invoice count for org type:" + asUserType);
			throw loExp;
		}
		return liInvoiceCount;
	}

	/**
	 * The Method will update invoice status on the basis of invoice id
	 * <ul>
	 * <li>Condition 1: If Authentication Flag is false,We will not proceed
	 * further</li>
	 * <li>Condition 2: If Authentication Flag is true. <br>
	 * Update status to withdrawn in Invoice Table. <br>
	 * Save withdraw comment in Comment Table.</li>
	 * <li></li>
	 * <li>This query used: updateInvoiceWithdrawn</li>
	 * </ul>
	 * @param aoMybatisSession Mybatis Session
	 * @param asInvoiceNumber Invoice Id
	 * @param aoAuthStatusFlag Authentication Flag
	 * @return lbStatus
	 * @throws ApplicationException ApplicationException object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean withdrawInvoiceList(String asInvoiceNumber, SqlSession aoMybatisSession, Boolean aoAuthStatusFlag)
			throws ApplicationException
	{
		boolean lbStatus = false;
		HashMap loHMReqProp = new HashMap();
		try
		{
			if (aoAuthStatusFlag != null && aoAuthStatusFlag)
			{
				loHMReqProp.put(HHSConstants.INVOICE_NUMBER, Integer.parseInt(asInvoiceNumber));
				loHMReqProp.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_WITHDRAWN));
				DAOUtil.masterDAO(aoMybatisSession, loHMReqProp, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
						HHSConstants.FILS_UPDATE_INVOICE_WITHDRAWN, HHSConstants.JAVA_UTIL_HASH_MAP);
				setMoState("Invoice Withdrawn succesfully");
								
				/* Start QC8774 R 7.5.0 :: update invoice_advance table with amount_recoupement = 0 */
				DAOUtil.masterDAO(aoMybatisSession, asInvoiceNumber, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
						HHSConstants.UPDATE_INVOICE_ADVANCE_RECOUPED_AMNT, HHSConstants.JAVA_LANG_STRING);
				setMoState("Invoice Advance Recouped Amt updated with 0 succesfully");
				
				lbStatus = true;
				/* End  QC8774 R 7.5.0 :: update invoice_advance table with amount_recoupement = 0 */
			}
			else
			{
				setMoState("User Login Credentials Failed");
			}
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData("Error occured while withdraw operation", asInvoiceNumber);
			LOG_OBJECT.Error("Error occured while withdraw operation", loExp);
			setMoState("Error occured while withdraw operation");
			throw loExp;
		}

		return lbStatus;
	}

	/**
	 * The Method will delete the invoice from db.
	 * <ul>
	 * <li>1: If Authentication Flag is false,We will not proceed further.</li>
	 * <li>2: If Authentication Flag is true,delete the invoice from db</li>
	 * </ul>
	 * The Method will delete the invoice from db.
	 * <ul>
	 * <li>1: If Authentication Flag is false,We will not proceed further.</li>
	 * <li>2: If Authentication Flag is true,delete the invoice from db</li>
	 * <li>This query used: deleteInvoiceDetails</li>
	 * </ul>
	 * Modified this method for defect id 6445 release 3.3.0
	 * @param aoUserSession user session as input
	 * @param asInvoiceId invoice as input
	 * @param aoMybatisSession batis session as input
	 * @param aoAuthStatusFlag flag as input
	 * @param loHmDocReqProps map as input
	 * @return lbStatus true in case of success
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public boolean deleteInvoiceList(P8UserSession aoUserSession, String asInvoiceId, SqlSession aoMybatisSession,
			Boolean aoAuthStatusFlag, HashMap loHmDocReqProps) throws ApplicationException
	{
		boolean lbStatus = false;
		P8ContentService loP8ContentService = new P8ContentService();
		try
		{
			if (aoAuthStatusFlag != null && aoAuthStatusFlag)
			{
				// made changes for defect id 6445 release 3.3.0
				List<HashMap<String, String>> loInvoiceDocumentInfo = (List<HashMap<String, String>>) DAOUtil
						.masterDAO(aoMybatisSession, asInvoiceId, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
								HHSConstants.FETCH_INVOICE_DOCUMENT_IDS, HHSConstants.JAVA_LANG_STRING);
				DAOUtil.masterDAO(aoMybatisSession, asInvoiceId, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
						HHSConstants.DELETE_INVOICE_DOCUMENT, HHSConstants.JAVA_LANG_STRING);
				//Start: Added in R7 for Cost-Center
				DAOUtil.masterDAO(aoMybatisSession, asInvoiceId, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
						HHSR5Constants.DELETE_COSTCENTER_INVOICE_DETAILS, HHSConstants.JAVA_LANG_STRING);
				DAOUtil.masterDAO(aoMybatisSession, asInvoiceId, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
						HHSR5Constants.DELETE_SERVICES_INVOICE_DETAILS, HHSConstants.JAVA_LANG_STRING);
				//End: Added in R7 Cost-Center
				DAOUtil.masterDAO(aoMybatisSession, asInvoiceId, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
						HHSConstants.DELETE_INVOICE_DETAILS, HHSConstants.JAVA_LANG_STRING);
				DAOUtil.masterDAO(aoMybatisSession, asInvoiceId, HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
						HHSConstants.DELETE_INVOICE, HHSConstants.JAVA_LANG_STRING);
				for (HashMap<String, String> loInvoiceDocumentIdInfo : loInvoiceDocumentInfo)
				{
					Integer loNoRowsExists = (Integer) DAOUtil.masterDAO(aoMybatisSession,
							loInvoiceDocumentIdInfo.get(HHSConstants.DOCUMENT_IDENTIFIER_ID),
							HHSConstants.MAPPER_CLASS_COMMON_MAPPER, HHSConstants.CHECK_DOCUMENT_EXISTS_IN_ANY_TABLE,
							HHSConstants.JAVA_LANG_STRING);
					if (loNoRowsExists > 1)
					{
						loP8ContentService.saveDocumentProperties(aoUserSession,
								loInvoiceDocumentIdInfo.get(HHSConstants.DOCUMENT_IDENTIFIER_ID),
								loInvoiceDocumentIdInfo.get(HHSConstants.DOCUMENT_TYPE_INVOICE), loHmDocReqProps, true);
					}
				}
				setMoState("Invoice deleted succesfully");
				lbStatus = true;
			}
			else
			{
				setMoState("User Login Credentials Failed");
			}
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData("Error occured while delete invoice operation", asInvoiceId);
			LOG_OBJECT.Error("Error occured while delete invoice operation", loExp);
			setMoState("Error occured while delete invoice operation");
			throw loExp;
		}

		return lbStatus;
	}

	/**
	 * The Method will select the workFlowId from Invoice table and update the
	 * proc status based on workFlow Id by updating the proc status as Withdrawn
	 * <ul>
	 * <li>select the workFlow Id based on contract id and budget type from
	 * budget table.</li>
	 * <li>update the proc status on the basis of e workFlow Id</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Mybatis Session
	 * @param asInvoiceNumber String object for InvoiceNumber
	 * @param abAuthStatusFlag Boolean authentication flag
	 * @return lbStatus
	 * @throws ApplicationException ApplictaionException object
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap selectWithdrawInvoiceWorkFlowDetails(SqlSession aoMybatisSession, String asInvoiceNumber,
			Boolean abAuthStatusFlag) throws ApplicationException
	{
		String lsContractId = null;
		HashMap loHmWFProperties = new HashMap();

		try
		{
			if (abAuthStatusFlag != null & abAuthStatusFlag)
			{
				lsContractId = (String) DAOUtil.masterDAO(aoMybatisSession, asInvoiceNumber,
						HHSConstants.MAPPER_CLASS_INVOICE_MAPPER,
						HHSConstants.FILS_SELECT_WITHDRAW_INVOICE_WORK_FLOW_DETAILS, HHSConstants.JAVA_LANG_STRING);
				loHmWFProperties.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, lsContractId);
				loHmWFProperties.put(HHSConstants.PROPERTY_PE_INVOICE_ID, asInvoiceNumber);
			}
			else
			{
				setMoState("User Authentication Failed for Invoice Number" + asInvoiceNumber);
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while updating proc status in case of "
					+ "withdraw invoice in invoice table  ", loAppEx);
			setMoState("Transaction Failed:: FinancialsInvoiceListService:"
					+ "selectWithdrawInvoiceWorkFlowDetails method -Exception occured ");
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured while updating proc status in "
					+ "case of withdraw invoice in invoice table  ", loEx);
			setMoState("Transaction Failed:: FinancialsInvoiceListService:"
					+ "selectWithdrawInvoiceWorkFlowDetails method - failed to update record ");
			throw new ApplicationException("Exception occured while updating proc status in "
					+ "case of withdraw invoice in invoice table ", loEx);
		}

		setMoState("Transaction Successfull in selectWithdrawInvoiceWorkFlowDetails"
				+ " Service method for Invoice Number" + asInvoiceNumber);
		return loHmWFProperties;
	}
}
