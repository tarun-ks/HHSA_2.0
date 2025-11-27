package com.nyc.hhs.daomanager.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.ApprovedProvidersBean;
import com.nyc.hhs.model.BaseFilter;
import com.nyc.hhs.model.EPinDetailBean;
import com.nyc.hhs.model.EvidenceBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.model.ProcurementInfo;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.model.ProposalQuestionAnswerBean;
import com.nyc.hhs.model.ProposalReportBean;
import com.nyc.hhs.model.RFPReleaseBean;
import com.nyc.hhs.model.SelectedServicesBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8services.P8ProcessServiceForSolicitationFinancials;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.BusinessApplicationUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

/**
 * This service class handles actions like creating procurements, fetching
 * active procurements, adding services to procurements, publishing a
 * procurement, adding configurations to procurement, adding competition pools,
 * adding evaluation criteria and releasing a procurement This service class
 * will get the method calls from controller through transaction layer. Execute
 * queries by calling mapper and return query output back to controller. If any
 * error exists, wrap the exception into Application Exception and throw it to
 * controller.
 */
public class ProcurementService extends ServiceState
{
	/**
	 * This is a log object used to log any exception into log file.
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(ProcurementService.class);

	/**
	 * This method fetches all active procurements for provider, accelerator and
	 * agency users.
	 * 
	 * <ul>
	 * <li>Check for the user type from input</li>
	 * <li>If user is provider user, execute fetchActiveProcurementsForProvider
	 * select query from the procurementmapper</li>
	 * <li>If user is Accelerator/Agency user, execute fetchActiveProcurements
	 * select query from the procurementmapper</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession mybatis SQL session
	 * @param aoProcBean a procurement bean object
	 * @param asUserType a string value of user type
	 * @return a list of active procurements
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<Procurement> fetchActiveProcurements(SqlSession aoMybatisSession, Procurement aoProcBean,
			String asUserType) throws ApplicationException
	{
		List<Procurement> loProcurementList = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AS_USER_TYPE, asUserType);
		LOG_OBJECT.Debug("Entered into fetching details fetched successfully for user Type:"
				+ loContextDataMap.toString());

		try
		{
			if (null != aoProcBean)
			{
				StringBuffer loProcTitleSb = new StringBuffer(HHSConstants.PERCENT).append(
						aoProcBean.getProcurementTitle()).append(HHSConstants.PERCENT);
				aoProcBean.setProcurementTitle(loProcTitleSb.toString());
				// when user is provider user
				if (null != asUserType && asUserType.equals(ApplicationConstants.PROVIDER_ORG))
				{
					// Begin QC 6531 REL 3.9.0. Need to qualify column with
					// table name only for provider and when firstSort is
					// STATUS_PROCESS_TYPE_ID
					if (HHSConstants.STATUS_PROCESS_TYPE_ID.equalsIgnoreCase(aoProcBean.getFirstSort()))
						aoProcBean.setFirstSort("stm1." + aoProcBean.getFirstSort());
					// End QC 6531 REL 3.9.0.
					loProcurementList = (List<Procurement>) DAOUtil.masterDAO(aoMybatisSession, aoProcBean,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_ACTIVE_PROC_FOR_PROVIDER,
							HHSConstants.COM_NYC_HHS_MODEL_PROC);
					loContextDataMap.put(HHSConstants.AS_USER_TYPE, asUserType);
				}
				else
				{
					loProcurementList = (List<Procurement>) DAOUtil.masterDAO(aoMybatisSession, aoProcBean,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_ACTIVE_PROCUREMENTS,
							HHSConstants.COM_NYC_HHS_MODEL_PROC);
				}
				String lsProcurementTitle = aoProcBean.getProcurementTitle();
				if (null != lsProcurementTitle)
				{
					lsProcurementTitle = lsProcurementTitle.substring(HHSConstants.INT_ONE);
					lsProcurementTitle = lsProcurementTitle.substring(HHSConstants.INT_ZERO,
							lsProcurementTitle.length() - HHSConstants.INT_ONE);
					aoProcBean.setProcurementTitle(lsProcurementTitle);
				}
			}
			setMoState("Procurement details fetched successfully for user Type:" + asUserType);
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while fetching Procurement details for user Type:" + asUserType);
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching Procurement details for user Type:", loExp);
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while fetching Procurement details for user Type:", asUserType);
			setMoState("Error while fetching Procurement details for user Type");
			throw new ApplicationException("Error while fetching Procurement details for user Type:" + asUserType, loEx);
		}
		return loProcurementList;
	}

	/**
	 * This method gets count of active procurements for provider, accelerator
	 * and agency users
	 * 
	 * <ul>
	 * <li>Check for the user type from input</li>
	 * <li>If user is provider user, execute getProcurementCountForProvider
	 * select query from the procurementmapper</li>
	 * <li>If user is Accelerator/Agency user, execute getProcurementCount
	 * select query from the procurementmapper</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession mybatis SQL session
	 * @param aoProcBean a procurement bean object
	 * @param asUserType UserType
	 * @return loProcurementCount an integer value of active procurement count
	 * @throws ApplicationException If an ApplicationException occurs
	 */

	public Integer getProcurementCount(SqlSession aoMybatisSession, Procurement aoProcBean, String asUserType)
			throws ApplicationException
	{
		Integer loProcurementCount = HHSConstants.INT_ZERO;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		LOG_OBJECT.Debug("Entered into getting procurement count for org type:" + loContextDataMap.toString());
		try
		{
			if (null != aoProcBean)
			{
				StringBuffer loProcTitleSb = new StringBuffer(HHSConstants.PERCENT).append(
						aoProcBean.getProcurementTitle()).append(HHSConstants.PERCENT);
				aoProcBean.setProcurementTitle(loProcTitleSb.toString());
				// when user is a provider user
				if (null != asUserType && asUserType.equals(ApplicationConstants.PROVIDER_ORG))
				{
					loProcurementCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoProcBean,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_PROC_COUNT_FOR_PROVIDER,
							HHSConstants.COM_NYC_HHS_MODEL_PROC);
					loContextDataMap.put(HHSConstants.AS_USER_TYPE, asUserType);
				}
				else
				{
					loProcurementCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoProcBean,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_PROC_COUNT,
							HHSConstants.COM_NYC_HHS_MODEL_PROC);
				}
				String lsProcurementTitle = aoProcBean.getProcurementTitle();
				if (null != lsProcurementTitle)
				{
					lsProcurementTitle = lsProcurementTitle.substring(HHSConstants.INT_ONE);
					lsProcurementTitle = lsProcurementTitle.substring(HHSConstants.INT_ZERO,
							lsProcurementTitle.length() - HHSConstants.INT_ONE);
					aoProcBean.setProcurementTitle(lsProcurementTitle);
				}
			}
			setMoState("Procurement count fetched successfully for org type:" + asUserType);

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting procurement count for org type:" + asUserType);
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting procurement count for org type:", loExp);
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while getting procurement count for org type:", asUserType);
			setMoState("Error while getting procurement count for org type");
			throw new ApplicationException("Error while getting procurement count for org type:" + asUserType, loEx);
		}
		return loProcurementCount;
	}

	/**
	 * This method is used to delete & insert selected services into
	 * procurement_services and addendum table based on the selection
	 * <ul>
	 * <li>1. Respective to selected services,retrieve list of selected services
	 * on basis of element id</li>
	 * <li>2.if procurement status is Draft then call query id
	 * saveSelectedServicesList to save details respective to selected services,
	 * deleteSelectedServicesList from DB if services are in not in the service
	 * selection</li>
	 * <li>3.if procurement status is Planned then call query id
	 * saveAddendumServicesList to save details respective to selected services,
	 * deleteAddendumServicesList from DB if services are in not in the service
	 * selection</li>
	 * <li>3. Insert list of selected services in DB</li>
	 * <li>4. Return true if list of services have been updated/deleted/saved in
	 * DB.</li>
	 * </ul>
	 * @param aoInsertSelectedServiceList list of selected services
	 * @param aoMybatisSession : Sql session object
	 * @return loSuccessStatus boolean true false
	 * @throws ApplicationException loAppEx
	 */

	public Boolean insertUpdateServiceAcceleratorService(final SqlSession aoMybatisSession,
			List<SelectedServicesBean> aoInsertSelectedServiceList) throws ApplicationException
	{
		Boolean loSuccessStatus = Boolean.FALSE;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.LO_INSERT_SEL_SERVICES_LIST, aoInsertSelectedServiceList);
		LOG_OBJECT.Debug("Entered into loading services details::" + loContextDataMap.toString());
		try
		{
			if (aoInsertSelectedServiceList != null && !aoInsertSelectedServiceList.isEmpty())
			{
				String lsProcurementId = aoInsertSelectedServiceList.get(HHSConstants.INT_ZERO).getProcurementId();
				String lsProcStatus = (String) DAOUtil.masterDAO(aoMybatisSession, lsProcurementId,
						HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.GET_PROC_STATUS,
						HHSConstants.JAVA_LANG_STRING);

				Map<String, Object> loSelectedServiceMap = new HashMap<String, Object>();
				loSelectedServiceMap.put(HHSConstants.LO_INSERT_SEL_SERVICES_LIST, aoInsertSelectedServiceList);
				loSelectedServiceMap.put(HHSConstants.PROCUREMENT_ID,
						aoInsertSelectedServiceList.get(HHSConstants.INT_ZERO).getProcurementId());
				if (lsProcStatus != null && !lsProcStatus.isEmpty())
				{
					// If procurement status is draft
					if (lsProcStatus.equalsIgnoreCase(PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_DRAFT)))
					{
						DAOUtil.masterDAO(aoMybatisSession, loSelectedServiceMap,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.DEL_SEL_SERV_LIST,
								HHSConstants.JAVA_UTIL_MAP);
						for (SelectedServicesBean loSelectedServicesBean : aoInsertSelectedServiceList)
						{
							if (loSelectedServicesBean.getElementId() != null)
							{
								DAOUtil.masterDAO(aoMybatisSession, loSelectedServicesBean,
										HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.SAVE_SEL_SERV_LIST,
										HHSConstants.COM_NYC_HHS_MODEL_SELSERV_BEAN);
								setMoState("Selected Services List saved successfully in DB");
							}
						}

					}
					else if (lsProcStatus.equalsIgnoreCase(PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_PLANNED)))
					{
						DAOUtil.masterDAO(aoMybatisSession, loSelectedServiceMap,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.DEL_ADD_SERV_LIST,
								HHSConstants.JAVA_UTIL_MAP);
						for (SelectedServicesBean loSelectedServicesBean : aoInsertSelectedServiceList)
						{
							if (loSelectedServicesBean.getElementId() != null)
							{
								DAOUtil.masterDAO(aoMybatisSession, loSelectedServicesBean,
										HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.SAVE_ADD_SERV_LIST,
										HHSConstants.COM_NYC_HHS_MODEL_SELSERV_BEAN);
								setMoState("Selected Services List saved successfully in DB");
							}
						}
					}
				}
				loSuccessStatus = true;
			}
		}

		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loExp)
		{
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while saving Selected Services List in DB :", loExp);
			throw loExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loEx)
		{
			ApplicationException loExp = new ApplicationException("Error while saving Selected Services List in DB :",
					loEx);
			throw loExp;
		}

		return loSuccessStatus;
	}

	/**
	 * This method will fetch the details of the e-pin and generate the e-pin
	 * details bean which later will be displayed
	 * Updated in Release 6 for new Epin format
	 * <ul>
	 * <li>Get all the parameter Set into the Channel Object</li>
	 * <li>Execute query id <b>fetchEpinDetails</b> from the procurement mapper</li>
	 * <li>Return the e-pin Details bean</li>
	 * </ul>
	 * @param aoMybatisSession valid sql session to establish connection with
	 *            data base
	 * @param asEpinKey e-pin key selected by user
	 * @return EPinDetailBean bean of all the details of the selected bean
	 * @throws ApplicationException application exception object
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public EPinDetailBean fetchEpinDetails(SqlSession aoMybatisSession, String asEpinKey) throws ApplicationException
	{
		EPinDetailBean loEPinDetailBean = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AS_EPIN_KEY, asEpinKey);
		LOG_OBJECT.Debug("Entered into getting form data from Taxonomy Maser" + loContextDataMap.toString());
		try
		{

			if (null != asEpinKey)
			{
				/*R6: parameters increased to epin and agencyId for EPIN change */
				String lsEpinVal = asEpinKey.split(HHSConstants.HYPHEN)[0];
				String lsEpinAgencyId = asEpinKey.split(HHSConstants.HYPHEN)[1];
				Map paramMap = new HashMap();
				paramMap.put(HHSConstants.E_PIN_ID, lsEpinVal.trim());
				paramMap.put(HHSConstants.AGENCYID, lsEpinAgencyId.trim());
				loEPinDetailBean = (EPinDetailBean) DAOUtil.masterDAO(aoMybatisSession, paramMap,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_EPIN_DETAILS,
						HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			else
			{
				throw new ApplicationException("Epin key entered can not be null");
			}
		}
		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			setMoState("Transaction Failed:: ProcurementService:fetchEpinDetails method - failed to get the Epin Details for E-pin: "
					+ asEpinKey + "\n");
			loAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting Epin Details for E-pin :" + asEpinKey, loAppEx);
			throw loAppEx;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Transaction Failed:: ProcurementService:fetchEpinList method - failed to get the Epin Details for E-pin: "
					+ asEpinKey + "\n");
			LOG_OBJECT.Error("Error while getting Epin Details for E-pin :", loAppEx);
			throw new ApplicationException("Error while getting Epin Details for E-pin ", loAppEx);
		}
		return loEPinDetailBean;
	}

	/**
	 * This method inserts the procurement data in the database.
	 * 
	 * <ul>
	 * <li>1. Check for status of procurement.</li>
	 * <li>2. If status is Released/Planned, call method
	 * "saveProcurementAddendumDetails".</li>
	 * <li>3. Else check procurement id.</li>
	 * <li>4. If procurement id is null then execute query
	 * "insertNewProcurementDetails".</li>
	 * <li>5. Else check for procurement status, if status is draft then execute
	 * query "updateDraftProcurementDetails".</li>
	 * <li>6. Else execute query "updateProcurementDetails".</li>
	 * </ul>
	 * 
	 * Return insert status.
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param aoProcurementBean - Procurement reference
	 * @param aoStatusId aoStatusId
	 * @return loInsertStatus Boolean
	 * @throws ApplicationException - throws ApplicationException
	 */
	public Boolean saveProcurementSummary(SqlSession aoMybatisSession, Procurement aoProcurementBean, Integer aoStatusId)
			throws ApplicationException
	{
		Boolean loInsertStatus = Boolean.FALSE;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AS_STATUS_ID, aoStatusId);
		LOG_OBJECT.Debug("Entered into saving Procurement Summary:::" + loContextDataMap.toString());
		try
		{
			if (aoProcurementBean != null)
			{
				aoProcurementBean.setStatus(aoStatusId);
				loContextDataMap.put(HHSConstants.AS_STATUS_ID, aoStatusId);
				if (aoStatusId != null
						&& aoStatusId != Integer.parseInt(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_DRAFT)))
				{
					saveProcurementAddendumDetails(aoMybatisSession, aoProcurementBean, aoStatusId);
					loInsertStatus = Boolean.TRUE;
				}
				else
				{
					if (aoProcurementBean.getPreProposalConferenceDatePlanned() != null
							&& aoProcurementBean.getPreProposalConferenceDatePlanned()
									.equals(HHSConstants.EMPTY_STRING))
					{
						aoProcurementBean.setPreProposalConferenceDatePlanned(null);
					}
					if (aoProcurementBean.getPreProposalConferenceDateUpdated() != null
							&& aoProcurementBean.getPreProposalConferenceDateUpdated()
									.equals(HHSConstants.EMPTY_STRING))
					{
						aoProcurementBean.setPreProposalConferenceDateUpdated(null);
					}
					if (aoProcurementBean.getProcurementId() == null || aoProcurementBean.getProcurementId().isEmpty())
					{
						DAOUtil.masterDAO(aoMybatisSession, aoProcurementBean,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.INS_NEW_PROC_DETAILS,
								HHSConstants.COM_NYC_HHS_MODEL_PROC);
						loInsertStatus = Boolean.TRUE;
					}
					else
					{
						DAOUtil.masterDAO(aoMybatisSession, aoProcurementBean,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.UPDATE_DRAFT_PROC_DETAILS,
								HHSConstants.COM_NYC_HHS_MODEL_PROC);
					}
					setMoState("procurement details saved successfully");
				}
				// changes for R5 starts
				if (aoProcurementBean.getProcurementId() != null && !aoProcurementBean.getProcurementId().isEmpty()
						&& aoProcurementBean.getGenerateTaskFlag() != null)
				{
					DAOUtil.masterDAO(aoMybatisSession, aoProcurementBean,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSR5Constants.UPDATE_GENERATE_PDF_FLAG,
							HHSConstants.COM_NYC_HHS_MODEL_PROC);
				}
				if (aoProcurementBean.getProcurementId() != null && !aoProcurementBean.getProcurementId().isEmpty()
						&& aoProcurementBean.getRegeneratePDFFlag() != null && aoProcurementBean.getRegeneratePDFFlag().equals(HHSConstants.ONE))
				{
					loContextDataMap.put(ApplicationConstants.PROCUREMENT_ID, aoProcurementBean.getProcurementId());
					loContextDataMap.put(HHSR5Constants.PDF_FLAG, HHSConstants.ONE);
					DAOUtil.masterDAO(aoMybatisSession, loContextDataMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
							HHSR5Constants.UPDATE_PSR_PDF_FLAG, ApplicationConstants.JAVA_UTIL_HASHMAP);
				}
					
				// changes for R5 ends
			}
			else
			{
				setMoState("Procurement bean is null so not able to save data");
				throw new ApplicationException("Procurement bean is null so not able to save data");
			}
		}
		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			loAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while saving procurement details :", loAppEx);
			setMoState("Error while saving procurement details");
			throw loAppEx;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Error while saving procurement details :", loAppEx);
			setMoState("Error while saving procurement details");
			throw new ApplicationException("Error occured while saving procurement details", loAppEx);
		}
		return loInsertStatus;
	}

	/**
	 * This method save the procurement addendum data in database.
	 * 
	 * <ul>
	 * <li>1. Check for status of procurement.</li>
	 * <li>2. If status is planned set addendum flag as 'Y', else set 'N'.</li>
	 * <li>3. Execute query id "updateProcurementAddendumDetails" to update
	 * addendum data.</li>
	 * <li>4. If above query returns 0, execute query
	 * "insertProcurementAddendumDetails".</li>
	 * <li>5. If procurement epin is not pending ten execute query
	 * "updateProcurementEpinInMasterTable".</li>
	 * </ul>
	 * 
	 * 
	 * @param aoMybatisSession MybatisSession
	 * @param aoProcurementBean ProcurementBean
	 * @param aoStatusId StatusId
	 * @throws NumberFormatException NumberFormatException
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	private void saveProcurementAddendumDetails(SqlSession aoMybatisSession, Procurement aoProcurementBean,
			Integer aoStatusId) throws NumberFormatException, ApplicationException
	{
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AS_STATUS_ID, aoStatusId);
		try
		{

			if (aoStatusId != null
					&& (aoStatusId == Integer.parseInt(PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_PLANNED))))
			{
				aoProcurementBean.setAddendumFlag(HHSConstants.STRING_ZERO);
			}
			else
			{
				aoProcurementBean.setAddendumFlag(HHSConstants.ONE);
			}
			if (aoProcurementBean.getProcurementId() != null)
			{
				Integer liRowsUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoProcurementBean,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.UPDATE_PROC_ADD_DETAILS,
						HHSConstants.COM_NYC_HHS_MODEL_PROC);
				if (liRowsUpdated == null || liRowsUpdated == HHSConstants.INT_ZERO)
				{
					DAOUtil.masterDAO(aoMybatisSession, aoProcurementBean,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.INS_PROC_ADD_DETAILS,
							HHSConstants.COM_NYC_HHS_MODEL_PROC);
				}
				if (null != aoProcurementBean.getProcurementEpin()
						&& !aoProcurementBean.getProcurementEpin().equalsIgnoreCase(HHSConstants.PENDING))
				{
					HashMap<String, String> loProcMap = new HashMap<String, String>();
					loProcMap.put(HHSConstants.EPIN, aoProcurementBean.getProcurementEpin());
					loProcMap.put(HHSConstants.PROCUREMENT_ID, aoProcurementBean.getProcurementId());
					//R6 emergency build - defect-8646
					loProcMap.put(HHSConstants.REF_APT_EPIN_ID, aoProcurementBean.getRefEpinId());
					DAOUtil.masterDAO(aoMybatisSession, loProcMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
							HHSConstants.UPDATE_PROC_EPIN_MASTER_TABLE, HHSConstants.JAVA_UTIL_HASH_MAP);
				}
				setMoState("Procurement details saved successfully.");
			}
		}
		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loAppExp)
		{
			loAppExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error was occurred while saving the Addendum Detail", loAppExp);
			setMoState("Error was occurred while saving the Addendum Detail");
			throw loAppExp;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error was occurred while saving the Addendum Detail", aoExp);
			setMoState("Error was occurred while saving the Addendum Detail");
			throw new ApplicationException("Error was occurred while saving the Addendum Detail", aoExp);
		}
	}

	/**
	 * This method fetch the status id from the database.
	 * 
	 * <ul>
	 * <li>1. Make the null check for the procurement bean.</li>
	 * <li>2. If the procurement bean is not null : .</li>
	 * <li>a. Set the status in the Map.</li>
	 * <li>b. Set the processType in the Map.</li>
	 * <li>3. Execute the query "getStatusId" specified in the
	 * procurementMapper.</li>
	 * <li>4. Return the status Id.</li>
	 * <li>5. If the procurement bean is null then throw the null application
	 * exception.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param aoProcBean - Procurement reference
	 * @return loStatusId Integer
	 * @throws ApplicationException - throws ApplicationException
	 */
	public Integer getStatusId(SqlSession aoMybatisSession, Procurement aoProcBean) throws ApplicationException
	{
		Integer loStatusId = 0;
		Map<String, String> loStatusInfo = new HashMap<String, String>();
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		try
		{
			if (aoProcBean != null)
			{
				loContextDataMap.put(HHSConstants.PROCUREMENT_ID, aoProcBean.getProcurementId());
				LOG_OBJECT.Debug("Entered into fetching status data::" + loContextDataMap.toString());
				if (aoProcBean.getProcurementId() == null || aoProcBean.getProcurementId().isEmpty())
				{
					loStatusInfo.put(HHSConstants.STAT, aoProcBean.getProcurementStatus());
					loStatusInfo.put(HHSConstants.PROCESS_TYPE, HHSConstants.PROCUREMENT);
					loStatusId = (Integer) DAOUtil.masterDAO(aoMybatisSession, loStatusInfo,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_STATUS_ID,
							HHSConstants.JAVA_UTIL_MAP);
				}
				else
				{
					loStatusId = Integer.parseInt((String) DAOUtil.masterDAO(aoMybatisSession,
							aoProcBean.getProcurementId(), HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER,
							HHSConstants.GET_PROC_STATUS, HHSConstants.JAVA_LANG_STRING));
				}
				setMoState("status fetched successfully :");
			}
			else
			{
				setMoState("Procurement bean is null so not able to fetch data");
				throw new ApplicationException("Procurement bean is null so not able to fetch data");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting status id :", loExp);
			setMoState("Error while getting status id :");
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while getting status id :", loEx);
			setMoState("Error while getting status id :");
			throw new ApplicationException("Error while getting program Name List :", loEx);
		}
		return loStatusId;
	}

	/**
	 * This method gets the procurement Summary corresponding to a procurement
	 * Id
	 * 
	 * <ul>
	 * <li>1. Retrieve procurement Id and procurement status</li>
	 * <li>2. Set procurement id and procurement status in a map for context
	 * data to be logged in case of exception.</li>
	 * <li>3. If the retrieved procurement status is not null and is equal to
	 * "2" or "3" then execute the query "getReleasedProcurementSummary"
	 * specified in the procurementMapper.</li>
	 * <li>4. If retrieved procurement summary is null then execute query
	 * <b>getProcurementSummary</b> to fetch the required procurement summary</li>
	 * <li>5. Return the Procurement Summary.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param asProcurementId - Procurement Id
	 * @param asProcurementStatus - procurement status
	 * @return loProcurementSummary- Procurement
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Procurement getProcurementSummary(SqlSession aoMybatisSession, String asProcurementId,
			String asProcurementStatus) throws ApplicationException
	{
		Procurement loProcurementSummary = null;
		HashMap<String, String> loContextDataMap = new HashMap<String, String>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loContextDataMap.put(HHSConstants.PROCUREMENT_STATUS_KEY, asProcurementStatus);
		LOG_OBJECT.Debug("Entered into getting Procurement Details::" + loContextDataMap.toString());
		try
		{
			if (asProcurementStatus != null
					&& !(asProcurementStatus.equalsIgnoreCase(PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_DRAFT))))
			{
				loProcurementSummary = (Procurement) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_RELEASES_PROC_SUMMARY,
						HHSConstants.JAVA_LANG_STRING);
			}
			if (loProcurementSummary == null)
			{
				loProcurementSummary = (Procurement) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_PROCUREMENT_SUMMARY,
						HHSConstants.JAVA_LANG_STRING);
			}
			setMoState("Procurement details fetched successfully for Procurement Id:" + asProcurementId);
		}

		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loExp)
		{
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting Procurement Details", loExp);
			setMoState("Error while getting Procurement Details");
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while getting Procurement Details", loEx);
			setMoState("Error while getting Procurement Details");
			throw new ApplicationException("Error while getting Procurement Details", loEx);
		}
		return loProcurementSummary;
	}

	/**
	 * 
	 * <p>
	 * Changed method - By: Siddharth Bhola Reason: Build: 2.6.0 Enhancement id:
	 * 5667, added extra check to identify whether provider/city/agency is
	 * logged in and based upon that fetch rfp released docs. This flag is in
	 * RFP_DOCUMENT TABLE, 0 means this document is replaced as part of
	 * addendum, 1 means this document is present in the procurement, later on
	 * when addendum is released then all 0 entries corresponding to Procurement
	 * are hard deleted
	 * 
	 * This method will get the RFP documents details Summary from data base
	 * 
	 * <ul>
	 * <li>1. Execute the query "fetchRfpReleaseDocsDetails" specified in the
	 * procurementMapper.</li>
	 * <li>It will return the details of the document from <b>RFP_DOCUMENT</b>
	 * and <b>RFP_ADDENDUM_DOCUMENT</b> table</li>
	 * <li>2. Return the Procurement Summary.</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - valid SqlSession object
	 * @param aoDocumentBean - document bean object
	 * @return loRfpDocumentsBeans- Procurement Bean
	 * @throws ApplicationException- throws application exception when any error
	 *             situation occurred
	 */
	@SuppressWarnings("unchecked")
	public List<ExtendedDocument> fetchRfpReleaseDocsDetails(SqlSession aoMybatisSession,
			ExtendedDocument aoDocumentBean) throws ApplicationException
	{
		List<ExtendedDocument> loRfpDocumentsBeans = null;
		HashMap<String, String> loContextDataMap = new HashMap<String, String>();
		try
		{
			loContextDataMap.put(HHSConstants.PROCUREMENT_ID, aoDocumentBean.getProcurementId());
			LOG_OBJECT.Debug("Entered into getting Procurement Documents Details::" + loContextDataMap.toString());
			// build 2.6.0, defect id 5667. set doc delete flag based upon
			// organization type
			if (null != aoDocumentBean)
			{
				aoDocumentBean.setDocDeleteFlag(HHSConstants.ZERO);
			}
			loRfpDocumentsBeans = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMybatisSession, aoDocumentBean,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_RFP_RELEASE_DOCS_DETAILS,
					HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
			setMoState("document details details fetched successfully for Procurement Id:"
					+ aoDocumentBean.getProcurementId());
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting Procurement Details");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting Procurement Details :", loExp);
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while getting Procurement Details :", loEx);
			setMoState("Error while getting Procurement Details");
			throw new ApplicationException("Error while getting Procurement Details", loEx);
		}
		return loRfpDocumentsBeans;
	}

	/**
	 * Gets the procurement Id from the database.
	 * 
	 * <ul>
	 * <li>1. Check for procurement id, If procurement id is null then execute
	 * the query "getProcurementId" specified in the procurementMapper</li>
	 * <li>2. Assign the result of the query to a variable "liProcurementId".</li>
	 * <li>3. If procurement id specified in step 1 is NotNull then assign this
	 * id to variable "liProcurementId".</li>
	 * <li>4. Return liProcurementId.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param aoProcBean ProcBean
	 * @return String lsProcurementId
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public String getProcurementId(SqlSession aoMybatisSession, Procurement aoProcBean) throws ApplicationException
	{
		String lsProcurementId = null;
		HashMap<String, String> loContextDataMap = new HashMap<String, String>();
		loContextDataMap.put(HHSConstants.LS_PRO_ID, lsProcurementId);
		LOG_OBJECT.Debug("Entered into fetching Procurement id details::" + loContextDataMap.toString());
		try
		{
			if (aoProcBean != null)
			{
				if (aoProcBean.getProcurementId() == null || aoProcBean.getProcurementId().isEmpty())
				{

					lsProcurementId = (String) DAOUtil.masterDAO(aoMybatisSession, null,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_PROC_ID, null);
				}
				else
				{
					lsProcurementId = aoProcBean.getProcurementId();
				}
			}
			setMoState("Procurement id fetched successfully. ");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting Procurement id", loExp);
			setMoState("Error while getting Procurement id");
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while getting Procurement id", loEx);
			setMoState("Error while getting Procurement id");
			throw new ApplicationException("Error while getting Procurement id", loEx);
		}
		return lsProcurementId;
	}

	/**
	 * This method fetches the approved providers list for S232 corresponding to
	 * the procurement Id
	 * <ul>
	 * <li>1. Retrieve procurement id</li>
	 * <li>2. Execute query <b>displayAppProviderOnPageLoad</b> corresponding to
	 * the procurement Id to fetch the list of Approved Providers</li>
	 * <li>3. Returns list of approved providers - loApprovedProviders</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - a String representation of procurement id
	 * @return loApprovedProviders - List of approved providers
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<ApprovedProvidersBean> displayApprovedProvidersList(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		List<ApprovedProvidersBean> loApprovedProviders = null;
		HashMap<String, String> loContextDataMap = new HashMap<String, String>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		LOG_OBJECT
				.Info("Entered into fetching approved providers for Services & Providers corresponding to the procurement Id:"
						+ loContextDataMap.toString());
		try
		{
			loApprovedProviders = (List<ApprovedProvidersBean>) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.DISPLAY_APP_PROVIDER_ON_PAGE_LOAD,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Approved Providers fetched successfully for Services & Providers corresponding to Procurement Id:"
					+ asProcurementId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while fetching approved providers for Services & Providers corresponding to the procurement Id",
							loAppEx);
			setMoState("Transaction Failed:: RFPReleaseService:displayApprovedProvidersList method -  fetching approved providers for Services & Providers corresponding to the procurement Id"
					+ asProcurementId + " \n");
			loAppEx.setContextData(loContextDataMap);
			throw loAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT
					.Error("Exception occured while fetching approved providers for Services & Providers corresponding to the procurement Id",
							aoExp);
			setMoState("Transaction Failed:: RFPReleaseService:displayApprovedProvidersList method -  fetching approved providers for Services & Providers corresponding to the procurement Id"
					+ asProcurementId + " \n");
			throw new ApplicationException(
					"Exception occured while fetching approved providers for Services & Providers corresponding to the procurement Id",
					aoExp);
		}
		return loApprovedProviders;
	}

	/**
	 * This service method will fetch services from the cache
	 * 
	 * <ul>
	 * <li>1. Create an instance of Base Cache</li>
	 * <li>2.Setting Cache instance as taxonomy element</li>
	 * <li>3.Setting FromCache as true</li>
	 * <li>4.return services list from the cache</li>
	 * </ul>
	 * @param asElementType : top element name
	 * @param asFromCache : cache or database
	 * @param aoMyBatisSession : Sql session object
	 * @return String : lsCompleteTree
	 * @throws ApplicationException if any exception occurred
	 */

	public String getTaxonomyTree(final String asElementType, String asFromCache, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		String lsCompleteTree = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		try
		{
			org.jdom.Document loTaxonomyDom = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					HHSConstants.TAXONOMY_ELEMENT);
			String lsXPathTop = "//element[((@name=\"" + ApplicationConstants.TAXONOMY_TYPE_MAP.get(asElementType)
					+ "\" or @name=\"Function\")" + "and lower-case(@parentid)=\"root\")]";
			List<Element> loNodeList = XMLUtil.getElementList(lsXPathTop, loTaxonomyDom);
			lsCompleteTree = BusinessApplicationUtil.getTree(loTaxonomyDom, loNodeList, HHSConstants.BUTTON);
			setMoState("Transaction successfully :::  fetch the data from taxonomy master in getTaxonomyTree method\n");
			loContextDataMap.put(HHSConstants.LO_NODE_LIST, loNodeList);
			loContextDataMap.put(HHSConstants.LS_COMPLETE_TREE, lsCompleteTree);
			LOG_OBJECT.Debug("Entered into fetch data from taxonomy master in getTaxonomyTree::"
					+ loContextDataMap.toString());
		}
		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			loAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Transaction failed:fetch the data from taxonomy master in getTaxonomyTree method\n",
					loAppEx);
			setMoState("Transaction failed:fetch the data from taxonomy master in getTaxonomyTree method\n");
			throw loAppEx;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Transaction failed:fetch the data from taxonomy master in getTaxonomyTree method\n");
			LOG_OBJECT.Error("Error while getting program Name List :", loAppEx);
			throw new ApplicationException("Error occured while getting form data from Taxonomy Maser", loAppEx);

		}
		return lsCompleteTree;
	}

	/**
	 * This method checks for evidence flag for the services associated with
	 * procurement
	 * <ul>
	 * <li>Check whether Procurement Status is "Planned" or "Draft"</li>
	 * <li>If Statement 1 is true ,Retrieve the ElementId List against
	 * procurementId by executing query with Id "getElementIdList"</li>
	 * <li>Retrieve the Evidence Flag for All the Elements from Cache [Call
	 * getEvidenceFlag from HHSUtil ]</li>
	 * <li>Add all the Services List Name to List whom Evidence Flag is 0</li>
	 * <li>Return the List containing Service Name for which evidence flag = 0</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asProcurementId String ProcurementId
	 * @param asProcurementStatus String ProcurementStatus
	 * @return Map contains Service Name List having EvidenceFlag 0 and Status
	 *         Flag
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> checkForEvidenceFlag(SqlSession aoMybatisSession, String asProcurementId,
			String asProcurementStatus) throws ApplicationException
	{
		List<String> loElementIdList = null;
		List<String> loServiceNameList = new ArrayList<String>();
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		HashMap<String, String> loContextDataMap = new HashMap<String, String>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loContextDataMap.put(HHSConstants.PROCUREMENT_STATUS_KEY, asProcurementStatus);
		LOG_OBJECT.Debug("Entered into checking evidence flag for services::" + loContextDataMap.toString());

		try
		{
			// Getting List of Element Id ,passing ProcurementId as Input
			loElementIdList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_ELEMENT_ID_LIST,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Element List fetched successfully for NYC Agency:");
			// Getting List of Evidence bean against List of ElementId from
			// Taxonomy Cache
			List<EvidenceBean> loEvidenceBean = HHSUtil.getEvidenceFlag(loElementIdList);
			if (loEvidenceBean != null && (loEvidenceBean.size() > HHSConstants.INT_ZERO))
			{
				for (Iterator loIter = loEvidenceBean.iterator(); loIter.hasNext();)
				{
					EvidenceBean loEvidenceBean1 = (EvidenceBean) loIter.next();
					if (loEvidenceBean1.getEvidenceFlag().equals(HHSConstants.STRING_ZERO))
					{
						loServiceNameList.add(loEvidenceBean1.getElementName());
					}
				}
			}
			Boolean loEvidenceErrorFlag = Boolean.FALSE;
			Boolean loServicesListErrorFlag = Boolean.FALSE;
			if (null != loServiceNameList && loServiceNameList.size() > HHSConstants.INT_ZERO)
			{
				loEvidenceErrorFlag = Boolean.TRUE;
			}
			if (loElementIdList == null || loElementIdList.isEmpty())
			{
				loServicesListErrorFlag = Boolean.TRUE;
			}
			loServiceData.put(HHSConstants.EVIDENCE_ERROR_FLAG, loEvidenceErrorFlag);
			loServiceData.put(HHSConstants.SER_NAME_LIST, loServiceNameList);
			loServiceData.put(HHSConstants.SERV_LIST_ERROR, loServicesListErrorFlag);
			loServiceData.put(HHSConstants.ELEMENT_ID_LIST, loElementIdList);
			setMoState("Services without evidence flag fetched succesfully for procurement Id :" + asProcurementId);
		}
		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			loAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Exception occured while getting data from checkForEvidenceFlag for Procurement Id:"
					+ asProcurementId, loAppEx);
			setMoState("Exception occured while getting data from checkForEvidenceFlag for Procurement Id:\n");
			throw loAppEx;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Exception occured while getting data from checkForEvidenceFlag for Procurement Id:"
					+ asProcurementId + " \n");
			LOG_OBJECT.Error("Exception occured while getting data from checkForEvidenceFlag for Procurement Id:",
					loAppEx);
			throw new ApplicationException("Error occured while getting data from checkForEvidenceFlag", loAppEx);
		}
		return loServiceData;
	}

	/**
	 * The Method will update Procurement Data by moving Procurement Addendum
	 * data in it.
	 * <ul>
	 * <li>1.Check if Service List is Empty and Current Status is Planned</li>
	 * <li>2.if condition satisfies Call Query Id :updateProcurementData.It will
	 * update Procurement data from Procurement Addendum table corresponding to
	 * ProcurementId</li>
	 * <li>3.Return update success status</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoProcurementMap ProcurementMap
	 * @param asProcurementStatus ProcurementStatus
	 * @param aoServiceData aoServiceData
	 * @returnlbUpdateSuccessful Boolean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateProcurementDataOnPublish(SqlSession aoMybatisSession, Map<String, String> aoProcurementMap,
			String asProcurementStatus, Map<String, Object> aoServiceData) throws ApplicationException
	{
		Boolean lbUpdateSuccessful = false;
		Boolean lbEvidenceErrorFlag = (Boolean) aoServiceData.get(HHSConstants.EVIDENCE_ERROR_FLAG);
		Boolean lbServicesListError = (Boolean) aoServiceData.get(HHSConstants.SERV_LIST_ERROR);
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AO_PROC_MAP, aoProcurementMap);
		loContextDataMap.put(HHSConstants.PROCUREMENT_STATUS_KEY, asProcurementStatus);
		loContextDataMap.put(HHSConstants.AO_SERVICE_DATA, aoServiceData);
		try
		{
			if (!(lbEvidenceErrorFlag || lbServicesListError))
			{
				if (PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROCUREMENT_PLANNED).equalsIgnoreCase(asProcurementStatus)
						|| PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROCUREMENT_PROPOSALS_RECEIVED).equalsIgnoreCase(
								asProcurementStatus)
						|| PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROCUREMENT_EVALUATIONS_COMPLETE).equalsIgnoreCase(
								asProcurementStatus)
						|| PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROCUREMENT_SELECTIONS_MADE).equalsIgnoreCase(asProcurementStatus))
				{
					// updating Procurement Data from Procurement Addendum Data
					DAOUtil.masterDAO(aoMybatisSession, aoProcurementMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
							HHSConstants.UPDATE_PROC_DATA_PUBLISH, HHSConstants.JAVA_UTIL_MAP);
					loContextDataMap.put(HHSConstants.AO_PROC_MAP, aoProcurementMap);
					loContextDataMap.put(HHSConstants.PROCUREMENT_STATUS_KEY, asProcurementStatus);
					loContextDataMap.put(HHSConstants.AO_SERVICE_DATA, aoServiceData);
					setMoState("Addendum details copied from procurement_addendum to procurement table corresponding to the procurement Id");

				}
				if (PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_PROCUREMENT_DRAFT).equalsIgnoreCase(asProcurementStatus))
				{
					aoProcurementMap.put(HHSConstants.PROCUREMENT_STATUS, PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_PLANNED));
					DAOUtil.masterDAO(aoMybatisSession, aoProcurementMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
							HHSConstants.UPDATE_PROC_STATUS_PUBLISH, HHSConstants.JAVA_UTIL_MAP);
				}
				lbUpdateSuccessful = true;
			}
		}
		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while coping details from procurement_addendum to procurement table corresponding to the procurement Id ",
							loAppEx);
			setMoState("Transaction Failed:: ProcurementService:updateProcurementData method - while coping details from procurement_addendum to procurement table corresponding to the procurement Id ");
			loAppEx.setContextData(loContextDataMap);
			throw loAppEx;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while coping details from procurement_addendum to procurement table corresponding to the procurement Id ",
							loAppEx);
			setMoState("Transaction Failed:: ProcurementService:updateProcurementData method - while coping details from procurement_addendum to procurement table corresponding to the procurement Id ");
			throw new ApplicationException("Error occured while updateProcurementDataOnPublish in Publish Procurement",
					loAppEx);
		}
		return lbUpdateSuccessful;
	}

	/**
	 * The Method will update Procurement Data by moving Procurement Addendum
	 * data in it.
	 * <ul>
	 * <li>1.Check if Service List is Empty and Current Status is Planned</li>
	 * <li>2.if condition 1 satisfy Call Query Id :updateProcurementData.It will
	 * update Procurement data from Procurement Addendum table corresponding to
	 * ProcurementId</li>
	 * <li>3.Return update sucess status</li>
	 * </ul>
	 * 
	 * @param asProcurementStatus ProcurementStatus
	 * @return aoAudit HhsAuditBean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public HhsAuditBean updateAuditBean(String asProcurementStatus, HhsAuditBean aoAudit) throws ApplicationException
	{

		try
		{
			if (PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROCUREMENT_PLANNED).equalsIgnoreCase(asProcurementStatus)
					|| PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROCUREMENT_PROPOSALS_RECEIVED).equalsIgnoreCase(asProcurementStatus)
					|| PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROCUREMENT_EVALUATIONS_COMPLETE).equalsIgnoreCase(asProcurementStatus)
					|| PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROCUREMENT_EVALUATIONS_COMPLETE).equalsIgnoreCase(asProcurementStatus)
					|| PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROCUREMENT_SELECTIONS_MADE).equalsIgnoreCase(asProcurementStatus))
			{
				aoAudit.setData(HHSConstants.PROCUREMENT_PUBLISHED);

			}
		}

		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while coping details from procurement_addendum to procurement table corresponding to the procurement Id ",
							loAppEx);
			setMoState("Transaction Failed:: ProcurementService:updateProcurementData method - while coping details from procurement_addendum to procurement table corresponding to the procurement Id ");
			throw new ApplicationException("Error occured while updateProcurementDataOnPublish in Publish Procurement",
					loAppEx);
		}
		return aoAudit;
	}

	/**
	 * The Method will update Procurement Data by moving Procurement Addendum
	 * data in it.
	 * <ul>
	 * <li>1.Check if Service List is Empty and Current Status is Planned</li>
	 * <li>2.if condition satisfies then Call Query Id :
	 * deleteProcurementServiceData and updateProcurementData.It will update
	 * Procurement Service data from Procurement Service Addendum table
	 * corresponding to ProcurementId</li>
	 * <li>3.Return update sucess status</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asProcId ProcurementId
	 * @param asProcurementStatus ProcurementStatus
	 * @param aoServiceMap List of Services
	 * @param aoValidateStatus ValidateStatus
	 * @param aoProcurementStatusFlag ProcurementStatusFlag
	 * @return Boolean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public Boolean updateProcurementServiceData(SqlSession aoMybatisSession, String asProcId,
			String asProcurementStatus, Map<String, Object> aoServiceMap, Boolean aoValidateStatus,
			Boolean aoProcurementStatusFlag) throws ApplicationException
	{
		List<String> loServiceNames = (List<String>) aoServiceMap.get(HHSConstants.SER_NAME_LIST);
		Boolean lbUpdateSuccessful = false;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AS_PROC_ID, asProcId);
		loContextDataMap.put(HHSConstants.PROCUREMENT_STATUS_KEY, asProcurementStatus);
		loContextDataMap.put(HHSConstants.AO_SERVICE_MAP, aoServiceMap);
		loContextDataMap.put(HHSConstants.AB_VALIDATE_STATUS, aoValidateStatus);
		loContextDataMap.put(HHSConstants.AB_PROC_STATUS_FLAG, aoProcurementStatusFlag);
		try
		{
			if (aoProcurementStatusFlag
					&& aoValidateStatus
					&& HHSUtil.isEmptyList(loServiceNames)
					&& PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROCUREMENT_PLANNED).equalsIgnoreCase(asProcurementStatus))
			{
				// Deleting Procurement Addendum Service Data
				DAOUtil.masterDAO(aoMybatisSession, asProcId, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.DEL_PROC_SERVICE_DATA, HHSConstants.JAVA_LANG_STRING);

				// Updating Procurement data from Procurement Addendum
				// Service Data
				DAOUtil.masterDAO(aoMybatisSession, asProcId, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.UPDATE_PROC_SERVICE_DATA, HHSConstants.JAVA_LANG_STRING);

				loContextDataMap.put(HHSConstants.AS_PROC_ID, asProcId);
				loContextDataMap.put(HHSConstants.PROCUREMENT_STATUS_KEY, asProcurementStatus);
				loContextDataMap.put(HHSConstants.AO_SERVICE_MAP, aoServiceMap);
				loContextDataMap.put(HHSConstants.AB_VALIDATE_STATUS, aoValidateStatus);
				loContextDataMap.put(HHSConstants.AB_PROC_STATUS_FLAG, aoProcurementStatusFlag);
				lbUpdateSuccessful = true;
				setMoState("Addendum details copied from procurement_addendum_Services to procurement_services table corresponding to the procurement Id");

			}
		}
		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while coping details from procurement_addendum_Services to procurement_services table corresponding to the procurement Id ",
							loAppEx);
			setMoState("Transaction Failed:: ProcurementService:updateProcurementServiceData method - while coping details from procurement_addendum to procurement table corresponding to the procurement Id ");
			loAppEx.setContextData(loContextDataMap);
			throw loAppEx;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while coping details from procurement_addendum_Services to procurement_services table corresponding to the procurement Id ",
							loAppEx);
			setMoState("Transaction Failed:: ProcurementService:updateProcurementServiceData method - while coping details from procurement_addendum to procurement table corresponding to the procurement Id ");
			throw new ApplicationException("Error occured while updateProcurementServiceData in Publish Procurement",
					loAppEx);
		}

		return lbUpdateSuccessful;
	}

	/**
	 * This method checks if agency user is of procuring agency i.e rule E4
	 * <ul>
	 * <li>1. Retrieve agency user Id and procurement Id and populate them in
	 * the HashMap</li>
	 * <li>2. Execute query <b>checkIfUserOfSameAgency</b> to fetch the count</li>
	 * <li>3. If count is 0 then return user are not of same procuring agency
	 * else return true to specify that they are of procuring agency</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param asAgencyUserId - agency user id
	 * @param asProcurementId - ProcurementId
	 * @return flag if user is of procuring agency
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public boolean checkIfUserOfSameAgency(SqlSession aoMybatisSession, String asAgencyUserId, String asProcurementId)
			throws ApplicationException
	{
		Map<String, String> loMap = new HashMap<String, String>();
		loMap.put(HHSConstants.AS_AGENCY_USER_ID, asAgencyUserId);
		loMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		LOG_OBJECT.Debug("Entered into ProcurementService:checkIfUserOfSameAgency method::" + loMap.toString());
		int liCount = HHSConstants.INT_ZERO;
		try
		{
			liCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loMap,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.CHECK_IF_USER_OF_SAME_AGENCY,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Transaction passed:: ProcurementService:checkIfUserOfSameAgency method");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			// Catch the exception thrown by masterDao method and pass the
			// caught exception with input params to controller
			loAppEx.setContextData(loMap);
			LOG_OBJECT.Error("Transaction Failed:: ProcurementService:checkIfUserOfSameAgency method", loAppEx);
			setMoState("Transaction Failed:: ProcurementService:checkIfUserOfSameAgency method");
			throw loAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Transaction Failed:: ProcurementService:checkIfUserOfSameAgency method", aoExp);
			setMoState("Transaction Failed:: ProcurementService:checkIfUserOfSameAgency method");
			throw new ApplicationException("Transaction Failed:: ProcurementService:checkIfUserOfSameAgency method",
					aoExp);
		}
		return (liCount != HHSConstants.INT_ZERO);
	}

	/**
	 * This method checks if agency user is of procuring agency if user is an
	 * agency user
	 * <ul>
	 * <li>1. Check if user is an agency user based on flag</li>
	 * <li>2. If user is agency user - invoke overloaded method
	 * <b>checkUfUserOfSameAgency</b> and return its value</li>
	 * <li>3. Else return false depecting check wasnt made</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param asAgencyUserId - agency user id
	 * @param asProcurementId - ProcurementId
	 * @param abFlagIfAgency - flag depecting if user is agency user
	 * @return flag if user is of procuring agency
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public boolean checkIfUserOfSameAgency(SqlSession aoMybatisSession, String asAgencyUserId, String asProcurementId,
			boolean abFlagIfAgency) throws ApplicationException
	{
		if (abFlagIfAgency)
		{
			return checkIfUserOfSameAgency(aoMybatisSession, asAgencyUserId, asProcurementId);
		}
		return false;
	}

	/**
	 * This method checks if procurements award has been approved i.e rule
	 * E8/E10
	 * <ul>
	 * <li>1. Retrieve procurement Id and populate them in the HashMap</li>
	 * <li>2. Execute query <b>checkIfAwardApproved</b> to fetch the count</li>
	 * <li>3. If count is 0 then return false else return true</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param asProcurementId - ProcurementId
	 * @return flag if procurement has approved award
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public boolean checkIfAwardApproved(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		Map<String, String> loContextDataMap = new HashMap<String, String>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loContextDataMap.put(HHSConstants.STATUS_AWARD_REVIEW_APPROVED, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_AWARD_REVIEW_APPROVED));
		LOG_OBJECT.Debug("Entered into ProcurementService:checkIfAwardApproved method::" + loContextDataMap.toString());
		int liCount = HHSConstants.INT_ZERO;
		try
		{
			liCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loContextDataMap,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.CHECK_IF_AWARD_APPROVED,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Transaction passed:: ProcurementService:checkIfAwardApproved method");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			// Catch the exception thrown by masterDao method and pass the
			// caught exception with input params to controller
			loAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Transaction Failed:: ProcurementService:checkIfAwardApproved method", loAppEx);
			setMoState("Transaction Failed:: ProcurementService:checkIfAwardApproved method");
			throw loAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Transaction Failed:: ProcurementService:checkIfAwardApproved method", aoExp);
			setMoState("Transaction Failed:: ProcurementService:checkIfAwardApproved method");
			throw new ApplicationException("Transaction Failed:: ProcurementService:checkIfAwardApproved method", aoExp);
		}
		return (liCount != HHSConstants.INT_ZERO);
	}

	/**
	 * This method checks for E6 E7 rules for procurement navigation
	 * <ul>
	 * <li>1. Retrieve procurement Id, provider user Id, proposal status Id and
	 * populate them in a HashMap</li>
	 * <li>2. Execute query <b>getProcurementDetailsForNavE6</b> to fetch the
	 * count</li>
	 * <li>3. Populate output HashMap<String, Boolean> on the basis of count
	 * value</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param asProcurementId - Procurement Id
	 * @param asProviderUserId - Provider User id
	 * @param asProposalId ProposalId
	 * @param asOrganizationId - organization id
	 * @return map of E5, E6, E7 status
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Map<String, Boolean> getProcurementDetailsForNav(SqlSession aoMybatisSession, String asProcurementId,
			String asProviderUserId, String asProposalId, String asOrganizationId) throws ApplicationException
	{
		int liCountE5 = HHSConstants.INT_ZERO;
		int liCountE6 = HHSConstants.INT_ZERO;
		int liCountE7 = HHSConstants.INT_ZERO;
		int liCountE8 = HHSConstants.INT_ZERO;
		Object loRestrictSubmitFlag;
		int liRestrictSubmitFlag = HHSConstants.INT_ZERO;

		Map<String, Boolean> loOutputMap = new HashMap<String, Boolean>();
		Map<String, String> loMap = new HashMap<String, String>();
		loMap.put(HHSConstants.AS_PROVIDER_USER_ID, asProviderUserId);
		loMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loMap.put(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
		loMap.put(HHSConstants.ORGANIZATION_ID_KEY, asOrganizationId);
		LOG_OBJECT.Debug("Entered into ProcurementService:getProcurementDetailsForNav method:" + loMap.toString());
		try
		{
			loMap.put(HHSConstants.STATUS_AWARD_STATUS_CANCELLED, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_AWARD_STATUS_CANCELLED));
			loMap.put(HHSConstants.STATUS_CONTRACT_CANCELLED, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_CONTRACT_CANCELLED));
			loMap.put(HHSConstants.AS_STATUS_PROPOSAL_SEL_ID, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_SELECTED));
			loMap.put(HHSConstants.STATUS_PROCUREMENT_RELEASED, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_RELEASED));
			loMap.put(HHSConstants.STATUS_PROPOSAL_DRAFT, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_DRAFT));
			loMap.put(HHSConstants.STATUS_PROPOSAL_RETURNED_FOR_REVISION, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_RETURNED_FOR_REVISION));
			loMap.put(HHSConstants.STATUS_COMPETITION_POOL_SELECTIONS_MADE, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_SELECTIONS_MADE));
			liCountE8 = (Integer) DAOUtil.masterDAO(aoMybatisSession, loMap,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_PROC_DETAILS_NAV8,
					HHSConstants.JAVA_UTIL_MAP);
			liCountE7 = (Integer) DAOUtil.masterDAO(aoMybatisSession, loMap,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_PROC_DETAILS_NAV7,
					HHSConstants.JAVA_UTIL_MAP);
			liCountE6 = (Integer) DAOUtil.masterDAO(aoMybatisSession, loMap,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_PROC_DETAILS_NAV6,
					HHSConstants.JAVA_UTIL_MAP);
			loRestrictSubmitFlag = DAOUtil.masterDAO(aoMybatisSession, loMap,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_PROV_RESTRICT_SUBMIT_FLAG,
					HHSConstants.JAVA_UTIL_MAP);
			if (loRestrictSubmitFlag == null)
			{
				liRestrictSubmitFlag = 0;
			}
			else
			{
				liRestrictSubmitFlag = (Integer) loRestrictSubmitFlag;
			}
			if (asProposalId != null && !asProposalId.isEmpty())
			{
				liCountE5 = (Integer) DAOUtil.masterDAO(aoMybatisSession, loMap,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_PROC_DETAILS_NAV5,
						HHSConstants.JAVA_UTIL_MAP);
			}
			setMoState("Transaction Passed:: ProcurementService:getProcurementDetailsForNav method");
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			// Catch the exception thrown by masterDao method and pass the
			// caught exception with input params to controller
			loAppEx.addContextData(HHSConstants.AS_PROVIDER_USERID, asProviderUserId);
			loAppEx.addContextData(HHSConstants.AS_PROCUREMENT_ID, asProcurementId);
			loAppEx.setContextData(loMap);
			setMoState("Transaction Failed:: ProcurementService:getProcurementDetailsForNav method");
			LOG_OBJECT.Error("Transaction Failed:: ProcurementService:getProcurementDetailsForNav method", loAppEx);
			throw loAppEx;
		}
		catch (Exception aoExp)
		{
			setMoState("Transaction Failed:: ProcurementService:getProcurementDetailsForNav method");
			LOG_OBJECT.Error("Transaction Failed:: ProcurementService:getProcurementDetailsForNav method", aoExp);
			throw new ApplicationException("Transaction Failed:: ProcurementService:checkIfAwardApproved method", aoExp);
		}
		loOutputMap.put(HHSConstants.E8, (liCountE8 != HHSConstants.INT_ZERO));
		loOutputMap.put(HHSConstants.E7, (liCountE7 != HHSConstants.INT_ZERO));
		loOutputMap.put(HHSConstants.E6, (liCountE6 != HHSConstants.INT_ZERO));
		loOutputMap.put(HHSConstants.E5, (liCountE5 != HHSConstants.INT_ZERO));
		loOutputMap.put(HHSConstants.RESTRICT_SUBMIT_FLAG, (liRestrictSubmitFlag == HHSConstants.INT_ONE));
		return loOutputMap;
	}

	/**
	 * This method will Insert the selected document from vault details Summary
	 * into <b>RFP_DOCUMENTS</b> table
	 * <ul>
	 * <li>Get all the required Parameter from Transaction Configuration</li>
	 * <li>Execute query id <b>insertAdendumDocumentDetails</b> and
	 * <b>insertRfpDocumentDetails</b> from Procurement Mapper</li>
	 * <li>Execute query id <b>deleteRfpDocumentDetails</b> If Addendum Doc is
	 * equal to zero else Execute query id
	 * <b>deleteRfpAddendumDocumentDetails</b></li>
	 * <li>If the procurement status is Released then execute query id
	 * <b>insertAdendumDocumentDetails</b></li>
	 * </ul>
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession Object
	 * @param aoParameterMap - Procurement Map
	 * @param asProcurementStatus - Procurement status
	 * @return liRowsInserted integer count of rows inserted
	 * @throws ApplicationException Wrap any Exception into Application
	 *             Exception
	 */
	public int insertRfpDocumentDetails(SqlSession aoMybatisSession, Map<Object, Object> aoParameterMap,
			String asProcurementStatus) throws ApplicationException
	{
		int liRowsInserted = HHSConstants.INT_ZERO;
		String lsIsAddendumDoc = null;
		String lsReplacingDocumentId = null;
		String lsDocReferenceNum = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AO_PARAMETER_MAP, aoParameterMap);
		loContextDataMap.put(HHSConstants.PROCUREMENT_STATUS_KEY, asProcurementStatus);
		LOG_OBJECT.Debug("Entered into inserting Rfp document details for Procurement Id::"
				+ loContextDataMap.toString());
		try
		{
			lsIsAddendumDoc = (String) aoParameterMap.get(HHSConstants.IS_ADDENDUM_DOC);
			lsReplacingDocumentId = (String) aoParameterMap.get(HHSConstants.REPLACING_DOCUMENT_ID);
			lsDocReferenceNum = (String) aoParameterMap.get(HHSConstants.DOC_REF_NO);
			if (null != lsReplacingDocumentId && !lsReplacingDocumentId.isEmpty())
			{
				aoParameterMap.put(HHSConstants.STATUS_ID, HHSConstants.STATUS_REPLACED);
				liRowsInserted = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParameterMap,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.INS_ADENDUM_DOC_DETAILS,
						HHSConstants.JAVA_UTIL_MAP);
				// Delete the old document entry from Rfp document or Addendum
				// document table
				if (lsIsAddendumDoc.equals(HHSConstants.ZERO))
				{
					aoParameterMap.put(HHSConstants.DOC_REF_NUM, lsDocReferenceNum);
					DAOUtil.masterDAO(aoMybatisSession, aoParameterMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
							HHSConstants.DELETE_RFP_DOC_DETAILS, HHSConstants.JAVA_UTIL_MAP);
				}
				else
				{
					aoParameterMap.put(HHSConstants.AS_DEL_DOC_ID, lsReplacingDocumentId);
					aoParameterMap.put(HHSConstants.DOC_REF_NUM, lsDocReferenceNum);
					DAOUtil.masterDAO(aoMybatisSession, aoParameterMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
							HHSConstants.DEL_RFP_ADD_DOC_DETAILS, HHSConstants.JAVA_UTIL_MAP);
				}
			}
			else
			{
				liRowsInserted = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParameterMap,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.INS_ADENDUM_DOC_DETAILS,
						HHSConstants.JAVA_UTIL_MAP);
			}
			setMoState("Rfp document details inserted successfully for Procurement Id:"
					+ aoParameterMap.get(HHSConstants.PROCUREMENT_ID));
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while inserting document details for Procurement Id:"
					+ aoParameterMap.get(HHSConstants.PROCUREMENT_ID));
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while inserting document details for Procurement Id:", loExp);
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error while inserting document details for Procurement Id:"
					+ aoParameterMap.get(HHSConstants.PROCUREMENT_ID));
			LOG_OBJECT.Error("Error while inserting document details for Procurement Id:", loExp);
			throw new ApplicationException("Error while inserting document details for Procurement Id:", loExp);
		}
		return liRowsInserted;
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
	 * @param asProcurementStatus procurement status
	 * @return int number of rows deleted
	 * @throws ApplicationException throws application exception when any error
	 *             occurred.
	 */

	public int removeRfpDocs(SqlSession aoMybatisSession, Map<String, String> aoParameterMap, String asProcurementStatus)
			throws ApplicationException
	{
		int liRowsDeleted = HHSConstants.INT_ZERO;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_STATUS_KEY, asProcurementStatus);
		loContextDataMap.put(HHSConstants.AO_PARAMETER_MAP, aoParameterMap);
		LOG_OBJECT.Debug("Entered into Rfp document details successfully for Procurement Id::"
				+ loContextDataMap.toString());
		String lsProcurementId = null;
		try
		{
			lsProcurementId = aoParameterMap.get(HHSConstants.PROCUREMENT_ID);
			liRowsDeleted = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParameterMap,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.DEL_RFP_ADD_DOC_DETAILS,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Rfp document details Deleted successfully for Procurement Id:"
					+ aoParameterMap.get(HHSConstants.PROCUREMENT_ID));
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error Occured while Deleting Document Details of Procurement Id:" + lsProcurementId);
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while deleting document details for Procurement Id:", loExp);
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while deleting document details for Procurement Id:", loEx);
			setMoState("Error Occured while Deleting Document Details of Procurement Id:" + lsProcurementId);
			throw new ApplicationException("Error while deleting document details for Procurement Id:", loEx);
		}
		return liRowsDeleted;
	}

	/**
	 * <p>
	 * This method fetches the Procurement Custom Questions and there
	 * corresponding answers
	 * <ul>
	 * <li>1. Add multiple input parameters to a Map</li>
	 * <li>2. Fetch Proposal Question Answers for the provided Proposal Id,
	 * Procurement Id using query id <b>fetchProcurementCustomQuestions</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param asProposalId - Proposal Id
	 * @param asProcurementId - Procurement Id
	 * @return list of custom question answers
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<ProposalQuestionAnswerBean> fetchProcurementCustomQuestionAnswer(SqlSession aoMybatisSession,
			String asProposalId, String asProcurementId) throws ApplicationException
	{
		List<ProposalQuestionAnswerBean> loCustomQuesAnsList = null;
		Map<String, String> loMap = new HashMap<String, String>();
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		LOG_OBJECT.Debug("Entered into fetching Custom question answer List:" + loContextDataMap.toString());

		try
		{
			loMap.put(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
			loMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			loCustomQuesAnsList = (List<ProposalQuestionAnswerBean>) DAOUtil.masterDAO(aoMybatisSession, loMap,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_PROC_CUSTOM_QUE_ANS,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Custom question answers List fetched successfully");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			setMoState("Error while fetching Custom question answer List");
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching Custom question answer List:", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception loExp)
		{
			setMoState("Error while fetching Custom question answer List");
			LOG_OBJECT.Error("Error while fetching Custom question answer List:", loExp);
			throw new ApplicationException("Error while fetching Custom question answer List:", loExp);
		}
		return loCustomQuesAnsList;
	}

	/**
	 * This method fetches the list of all selected services.
	 * <ul>
	 * <li>1. Retrieve procurement id from the channel</li>
	 * <li>2. Execute <b>fetchElementId</b> to fetch element Id of selected
	 * services from the procurement_services table</li>
	 * <li>3. Convert element id details to taxonomy details.</li>
	 * <li>4. Returns selected services list.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - procurement Id
	 * @return SelectedServicesList
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<SelectedServicesBean> fetchSelectedServices(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		List<SelectedServicesBean> loSelectedServicesList = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		LOG_OBJECT.Debug("Entered into fetching Selected Services List::" + loContextDataMap.toString());
		try
		{
			loSelectedServicesList = (List<SelectedServicesBean>) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_ELEMENT_ID,
					HHSConstants.JAVA_LANG_STRING);
			org.jdom.Document loDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					HHSConstants.TAXONOMY_ELEMENT);
			if (loSelectedServicesList != null)
			{
				for (SelectedServicesBean loProcServBean : loSelectedServicesList)
				{
					String lsElementId = loProcServBean.getElementId();
					Element loCorrRuleElt = XMLUtil.getElement("//element[@id=\"" + lsElementId + "\"]", loDoc);
					String lsProcServiceName = loCorrRuleElt.getAttributeValue(HHSConstants.NAME);
					loProcServBean.setServiceName(lsProcServiceName);
				}
			}
			setMoState("Selected Services List fetched successfully");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			// Catch the exception thrown by masterDao method and pass the
			// caught exception with input params to controller
			loExp.addContextData(HHSConstants.AS_PROCUREMENT_ID, asProcurementId);
			setMoState("Transaction Failed:: ProcurementService:fetchSelectedServices method :: Error while fetching Selected Services List");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching Selected Services List", loExp);
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Transaction Failed:: ProcurementService:fetchSelectedServices method :: Error while fetching Selected Services List");
			LOG_OBJECT.Error("Error while fetching Selected Services List", loExp);
			throw new ApplicationException("Error while fetching Selected Services List", loExp);
		}
		return loSelectedServicesList;
	}

	/**
	 * This method fetches the list of all selected services.
	 * <ul>
	 * <li>1. Retrieve procurement id from the channel</li>
	 * <li>2. Execute <b>fetchServiceElementId</b> to fetch element Id of
	 * selected services from the procurement_services table</li>
	 * <li>3. Create cache object and pick details of "taxonomy" element</li>
	 * <li>4. Iterate the retrieved element id list</li>
	 * <li>5. Fetch the value of element name from the created dom object
	 * corresponding to the iterated element Id and append the same in the list</li>
	 * <li>6. Returns selected services list.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - procurement Id
	 * @return SelectedServicesList
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<SelectedServicesBean> fetchServicesList(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		List<SelectedServicesBean> loSelectedServicesList = null;
		String lsElementId = null;
		Element loCorrRuleElt = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		LOG_OBJECT.Debug("Entered into loading services details::" + loContextDataMap.toString());
		try
		{
			loSelectedServicesList = (List<SelectedServicesBean>) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_SERV_ELEMENT_ID,
					HHSConstants.JAVA_LANG_STRING);
			org.jdom.Document loDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					HHSConstants.TAXONOMY_ELEMENT);
			for (SelectedServicesBean loProcServBean : loSelectedServicesList)
			{
				lsElementId = loProcServBean.getElementId();
				loCorrRuleElt = XMLUtil.getElement("//element[@id=\"" + lsElementId + "\"]", loDoc);
				String loProcServiceName = loCorrRuleElt.getAttributeValue(HHSConstants.NAME);
				loProcServBean.setServiceName(loProcServiceName);
			}
			setMoState("Selected Services List fetched successfully");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while fetching Selected Services List");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching Selected Services List", loExp);
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error while fetching Selected Services List");
			LOG_OBJECT.Error("Error while fetching Selected Services List", loExp);
			throw new ApplicationException("Error while fetching Selected Services List", loExp);
		}
		return loSelectedServicesList;
	}

	/**
	 * This method fetches the type for which providers are approved for.
	 * <ul>
	 * <li>1. Retrieve procurement id from the channel</li>
	 * <li>2. Execute <b>fetchApprovedProvDetails</b> to fetch approved provider
	 * type - AND/OR for Procurement Mapper</li>
	 * <li>3. Returns the criteria.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - procurement Id
	 * @return lsCriteria- String
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public String fetchApprovedProvDetails(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		String lsCriteria = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		LOG_OBJECT.Debug("Entered into loading approved provider criteria - AND/OR proc id::"
				+ loContextDataMap.toString());
		try
		{
			lsCriteria = (String) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_APPROVED_PROV_DETAILS,
					HHSConstants.JAVA_LANG_STRING);

			setMoState("Successfully fetched approved provider criteria - AND/OR");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			// Catch the exception thrown by masterDao method and pass the
			// caught exception with input params to controller
			loExp.addContextData(HHSConstants.AS_PROCUREMENT_ID, asProcurementId);
			setMoState("Transaction Failed:: ProcurementService:fetchApprovedProvDetails method :: Error while fetching approved provider criteria - AND/OR ");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching approved provider criteria - AND/OR proc id::", loExp);
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Transaction Failed:: ProcurementService:fetchApprovedProvDetails method :: Error while fetching approved provider criteria - AND/OR ");
			LOG_OBJECT.Error("Error while fetching approved provider criteria - AND/OR proc id::", loExp);
			throw new ApplicationException("Error while fetching approved provider criteria - AND/OR proc id::", loExp);
		}

		return lsCriteria;
	}

	/**
	 * This method fetches the list of approved providers corresponding to
	 * selected services.
	 * <ul>
	 * <li>1. Retrieve selected drop down value and SelectedServicesBean from
	 * the channel.</li>
	 * <li>2. Execute query <b>fetchApprovedProvidersList</b> to fetch the
	 * approved and conditionally approved providers list on the basis of
	 * selected drop down value.</li>
	 * <li>3. The provider will revert to "Service App Required" status and will
	 * no longer be included on the approved list in the following cases:</li>
	 * <li>3a. If the provider becomes suspended for the related RFP service(s)
	 * or for the overall business application</li>
	 * <li>3b. If the provider withdraws the related RFP service(s) and/or
	 * withdraws the business application</li>
	 * <li>4. For both of the above cases If "At least one of the selected
	 * services(Option 1)" is selected, the provider can be suspended or
	 * withdraw from 1 of the services and still be approved for the RFP if they
	 * are approved for at least 1 of the other services on the RFP. If "All
	 * selected services (Option 0)" is selected, the provider will no longer be
	 * approved for the RFP if they are suspended or withdraw from 1 or more
	 * services.</li>
	 * <li>5. Returns providers list.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId asProcurementId
	 * @param asSelectedProvDropDownValue SelectedProvDropDownValue
	 * @param aoSelectedServiceList aoSelectedServiceList
	 * @param asElementId asElementId
	 * @param aoBaseFilter aoBaseFilter
	 * @param asForProvider asForProvider
	 * @param asProcurementStatus asProcurementStatus
	 * @return loApprovedProvidersList - a list of Approved Providers.
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<ApprovedProvidersBean> fetchApprovedProvidersList(SqlSession aoMybatisSession, String asProcurementId,
			String asSelectedProvDropDownValue, List<String> aoSelectedServiceList, String asElementId,
			BaseFilter aoBaseFilter, String asForProvider, String asProcurementStatus) throws ApplicationException
	{
		String lsQuery = HHSConstants.FETCH_APP_PROVIDERS_LIST;
		List<ApprovedProvidersBean> loApprovedProvidersList = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loContextDataMap.put(HHSConstants.AS_SELECTED_PROV_DROPDOWN_VALUE, asSelectedProvDropDownValue);
		loContextDataMap.put(HHSConstants.AO_SELECTED_SERVICE_LIST, aoSelectedServiceList);
		loContextDataMap.put(HHSConstants.AS_ELEMENT_ID, asElementId);
		loContextDataMap.put(HHSConstants.AO_BASE_FILTER, aoBaseFilter);
		LOG_OBJECT.Debug("Entered into fetching Approved Providers List :" + loContextDataMap.toString());
		try
		{
			Map<String, Object> loMap = new HashMap<String, Object>();
			loMap.put(HHSConstants.FIRST_SORT_DATE, false); // QC 5423
			loMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
			loMap.put(HHSConstants.ELEMENT_ID_LIST, aoSelectedServiceList);
			loMap.put(HHSConstants.SIZE, aoSelectedServiceList.size());
			loMap.put(HHSConstants.FIRST_SORT, aoBaseFilter.getFirstSort());
			loMap.put(HHSConstants.FIRST_SORT_TYPE, aoBaseFilter.getFirstSortType());
			loMap.put(HHSConstants.SECOND_SORT, aoBaseFilter.getSecondSort());
			loMap.put(HHSConstants.SECOND_SORT_TYPE, aoBaseFilter.getSecondSortType());
			loMap.put(HHSConstants.FOR_PROVIDER, asForProvider);
			loMap.put(HHSConstants.LS_PROC_STATUS, asProcurementStatus);
			if (asProcurementStatus != null
					&& !(asProcurementStatus.equalsIgnoreCase(PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_DRAFT)) || asProcurementStatus
							.equalsIgnoreCase(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_PROCUREMENT_PLANNED))))
			{
				lsQuery = HHSConstants.FETCH_APP_PROVIDERS_LIST_RELEASED;
			}
			if ((asSelectedProvDropDownValue != null)
					&& (asSelectedProvDropDownValue.equalsIgnoreCase(HHSConstants.ONE)))
			{
				loMap.put(HHSConstants.TYPE, HHSConstants.OR);
				if (asElementId != null && !asElementId.equalsIgnoreCase(HHSConstants.STRING_MINUS_ONE)
						&& !asElementId.equalsIgnoreCase(HHSConstants.UNDEFINED))
				{
					List<String> loElementIdListTemp = new ArrayList<String>();
					loElementIdListTemp.add(asElementId);
					loMap.put(HHSConstants.ELEMENT_ID_LIST, loElementIdListTemp);
				}
			}
			else
			{
				loMap.put(HHSConstants.TYPE, HHSConstants.AND);
			}

			if (asForProvider != null)
			{
				lsQuery = HHSConstants.FETCH_APP_PROVIDERS_LIST_PROVIDER;

			}
			loApprovedProvidersList = (List<ApprovedProvidersBean>) DAOUtil.masterDAO(aoMybatisSession, loMap,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, lsQuery, HHSConstants.JAVA_UTIL_MAP);
			setMoState("Selected Approved Providers List fetched successfully");
		}

		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			setMoState("Error while fetching Approved Providers List");
			loAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching Approved Providers List:", loAppEx);
			throw loAppEx;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Error while fetching Approved Providers List");
			LOG_OBJECT.Error("Error while fetching Approved Providers List", loAppEx);
			throw new ApplicationException("Error while fetching Approved Providers List", loAppEx);
		}

		return loApprovedProvidersList;
	}

	/**
	 * This method fetches the list of approved providers corresponding to
	 * selected services after release.
	 * <ul>
	 * <li>1. Retrieve procurement id and fetch flag(determining) if data has to
	 * be fetched.</li>
	 * <li>2. Execute query <b>fetchApprovedProvidersListAfterRelease</b> to
	 * fetch the approved and conditionally approved providers list on the basis
	 * of selected drop down value of the procurement plus the last approved
	 * providers.</li>
	 * <li>3. Returns providers list.</li>
	 * </ul>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - procurement id
	 * @param asServiceFilter - Service filter value of current procurement
	 * @param aoFetchDefault -Flag whether to fetch default data
	 * @return loApprovedProvidersList
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public List<ApprovedProvidersBean> fetchApprovedProvidersListAfterRelease(SqlSession aoMybatisSession,
			String asProcurementId, String asServiceFilter, Boolean aoFetchDefault) throws ApplicationException
	{
		List<ApprovedProvidersBean> loApprovedProvidersList = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AB_FETCH_DEF, aoFetchDefault);
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		LOG_OBJECT.Debug("Entered into fetching Approved Providers List:" + loContextDataMap.toString());
		if (aoFetchDefault)
		{
			try
			{
				Map<String, Object> loMap = new HashMap<String, Object>();
				loMap.put(HHSConstants.FIRST_SORT_DATE, false); // QC 5423
				loMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
				loMap.put(HHSConstants.FIRST_SORT, HHSConstants.ORGANIZATION_NAME);
				loMap.put(HHSConstants.FIRST_SORT_TYPE, HHSConstants.ASCENDING);
				loMap.put(HHSConstants.SERVICE_FILTER, asServiceFilter);
				loApprovedProvidersList = (List<ApprovedProvidersBean>) DAOUtil.masterDAO(aoMybatisSession, loMap,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_APP_PROVIDERS_LIST_AFTER_REL,
						HHSConstants.JAVA_UTIL_MAP);
				setMoState("Selected Approved Providers List fetched successfully - after release");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handled over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException loExp)
			{
				// Catch the exception thrown by masterDao method and pass the
				// caught exception with input params to controller
				loExp.addContextData(HHSConstants.AS_PROCUREMENT_ID, asProcurementId);
				loExp.addContextData(HHSConstants.AB_FETCH_DEFAULT, aoFetchDefault);
				setMoState("Transaction Failed:: ProcurementService:fetchSelectedServices method :: Error while fetching Approved Providers List - after release");
				loExp.setContextData(loContextDataMap);
				LOG_OBJECT.Error("Error while fetching Approved Providers List", loExp);
				throw loExp;
			}
			catch (Exception loExp)
			{
				setMoState("Transaction Failed:: ProcurementService:fetchSelectedServices method :: Error while fetching Approved Providers List - after release");
				LOG_OBJECT.Error("Error while fetching Approved Providers List", loExp);
				throw new ApplicationException("Error while fetching Approved Providers List", loExp);
			}
		}
		return loApprovedProvidersList;
	}

	/**
	 * This method will fetch the organization read only details corresponding
	 * to an organization_id
	 * <ul>
	 * <li>1. Retrieve business_application_id, organization_id and table name
	 * from the channel.</li>
	 * <li>2. Create HashMap and populate it with organization_id,
	 * business_application_id and table name.</li>
	 * <li>3. Execute <b>getFormDetails</b>, <b>getFormDetailsOfOrg</b> and
	 * <b>getCorpStructureValue</b> to fetch organization details accordingly
	 * and set it in a HashMap.</li>
	 * <li>4. Returns HashMap</li>
	 * </ul>
	 * 
	 * @param asBuisAppId - string representation of the business_application_id
	 * @param aoMybatisSession - a SqlSession object
	 * @param asOrgId - string representation of the organization_id
	 * @param asTableName - string representation of the table name.
	 * @return loHMFormValues - a HashMap populated with organization data
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public HashMap getOrganizationDetail(String asBuisAppId, SqlSession aoMybatisSession, String asOrgId,
			String asTableName) throws ApplicationException
	{

		@SuppressWarnings("unchecked")
		Map<String, String> loHMWhereClause = new HashMap();
		List<HashMap<String, Object>> loLQuestionAnswer = null;
		HashMap<String, Object> loHMFormValues = null;
		asTableName = HHSConstants.TABLE_NAME;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AS_TABLE_NAME, asTableName);
		loContextDataMap.put(HHSConstants.ORG_ID, asOrgId);
		LOG_OBJECT.Debug("Entered into selecting answer data from Basicform table:" + loContextDataMap.toString());
		try
		{
			loHMWhereClause.put(HHSConstants.ORG_ID, asOrgId);
			loHMWhereClause.put(HHSConstants.BUIZ_APP_ID, asBuisAppId);
			loHMWhereClause.put(HHSConstants.TABLE, asTableName);
			StringBuffer lsBWhereClause = new StringBuffer();
			if (loHMWhereClause.get(HHSConstants.TABLE).equalsIgnoreCase(HHSConstants.TABLE_NAME))
			{
				lsBWhereClause.append(HHSConstants.ORGANIZATIONID).append(loHMWhereClause.get(HHSConstants.ORG_ID))
						.append(HHSConstants.STR);
			}
			else
			{
				lsBWhereClause.append(HHSConstants.BUSINESS_APP_ID).append(
						loHMWhereClause.get(HHSConstants.BUIZ_APP_ID));
				lsBWhereClause.append(HHSConstants.AND_ORG_ID).append(loHMWhereClause.get(HHSConstants.ORG_ID))
						.append(HHSConstants.STR);
			}
			loHMWhereClause.put(HHSConstants.AS_WHERE, lsBWhereClause.toString());
			loLQuestionAnswer = (List<HashMap<String, Object>>) DAOUtil.masterDAO(aoMybatisSession, loHMWhereClause,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_FORM_DETAILS,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			if (loLQuestionAnswer != null && loLQuestionAnswer.size() > HHSConstants.INT_ZERO)
			{
				loHMFormValues = loLQuestionAnswer.get(HHSConstants.INT_ZERO);
			}
			else
			{
				loHMFormValues = new HashMap<String, Object>();
			}
			if (loHMWhereClause.get(HHSConstants.TABLE).equalsIgnoreCase(HHSConstants.TABLE_NAME)
					&& loHMWhereClause.get(ApplicationConstants.BUIZ_APP_ID) != null)
			{
				loHMWhereClause.put(HHSConstants.SECTION, HHSConstants.BUSINESS_APPLICATION_SECTION_BASICS);
				loHMWhereClause.put(HHSConstants.SUB_SECTION, HHSConstants.BUZ_APP_SUB_SECTION_QUESTION);
				Map<String, String> loVal = (Map<String, String>) DAOUtil.masterDAO(aoMybatisSession, loHMWhereClause,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_FORM_DETAILS_OF_ORG,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				if (loHMFormValues.keySet().size() > HHSConstants.INT_ZERO)
				{
					loHMFormValues.put(HHSConstants.FORM_NAME, loVal.get(HHSConstants.FORM_NAME));
					loHMFormValues.put(HHSConstants.FORM_VERSION, loVal.get(HHSConstants.FORM_VERSION));
					loHMFormValues.put(HHSConstants.FORM_ID, loVal.get(HHSConstants.FORM_ID));
				}
			}
			if (loHMWhereClause.get(HHSConstants.TABLE).equalsIgnoreCase(HHSConstants.FILING_FORM))
			{
				HashMap<String, Object> loVal = (HashMap<String, Object>) DAOUtil.masterDAO(aoMybatisSession,
						loHMWhereClause, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.GET_CORP_STRUCTURE_VAL, HHSConstants.JAVA_UTIL_HASH_MAP);
				if (loVal != null)
				{
					loHMFormValues.put(HHSConstants.BASIC_CS_VAL, loVal.get(HHSConstants.CORP_STRUCTURE_ID));
				}
			}
		}

		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			setMoState("Exception occured while selecting answer data from Basicform table " + "\n");
			loAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Exception occured while selecting answer data from Basicform table  :", loAppEx);
			throw loAppEx;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Exception occured while selecting answer data from Basicform table " + "\n");
			LOG_OBJECT.Error("Exception occured while selecting answer data from Basicform table", loAppEx);
			throw new ApplicationException("Exception occured while selecting answer data from Basicform table",
					loAppEx);
		}
		return loHMFormValues;
	}

	/**
	 * This method fetches the saved drop down value from the database
	 * <ul>
	 * <li>1. Retrieve procurement Id from the channel</li>
	 * <li>2. Execute query <b>fetchDropDownValue</b> to fetch service_filter
	 * from procurement_service_filter corresponding to the procurement_id</li>
	 * <li>3. Return fetched value</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcId - string representation of procurement Id
	 * @return lsDropDownValue- String representation of fetched drop-down
	 *         value.
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public String fetchDropDownValue(SqlSession aoMybatisSession, String asProcId) throws ApplicationException
	{
		String lsDropDownValue = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AS_PROC_ID, asProcId);
		LOG_OBJECT.Debug("Entered into fetching drop down value:" + loContextDataMap.toString());
		try
		{
			lsDropDownValue = (String) DAOUtil.masterDAO(aoMybatisSession, asProcId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_DROP_DOWN_VALUE,
					HHSConstants.JAVA_LANG_STRING);

			setMoState("Drop Down value fetched successfully");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while fetching drop down value");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching drop down value", loExp);
			throw loExp;
		}
		catch (Exception loAppEx)
		{
			setMoState("Error while fetching drop down value");
			LOG_OBJECT.Error("Error while fetching drop down value", loAppEx);
			throw new ApplicationException("Error while fetching drop down value", loAppEx);
		}
		return lsDropDownValue;
	}

	/**
	 * This method will save the Approved Providers details on click of "Save"
	 * button
	 * <ul>
	 * <li>1. Execute query <b>getProcurementStatus</b> to get procurement
	 * status</li>
	 * <li>2. Execute query <b>updateDropDownValueDraft</b></li>
	 * <li>3. Execute query <b>updateDropDownValuePlanned</b></li>
	 * <li>4. Execute query <b>insertDropDownValuePlanned</b></li>
	 * <li>5. If the drop down value is updated then return true status</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param aoSelectedServBean - object of SelectedServicesBean containing
	 *            attributes
	 * @return loUpdatedRows status flag
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public String saveApprovedProvDetails(SqlSession aoMyBatisSession, SelectedServicesBean aoSelectedServBean)
			throws ApplicationException
	{
		Integer loUpdatedRows = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		try
		{
			String lsProcurementStatus = (String) DAOUtil.masterDAO(aoMyBatisSession,
					aoSelectedServBean.getProcurementId(), HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER,
					HHSConstants.GET_PROC_STATUS, HHSConstants.JAVA_LANG_STRING);
			String lsDraftId = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROCUREMENT_DRAFT);
			String lsPlannedId = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_PROCUREMENT_PLANNED);
			loContextDataMap.put(HHSConstants.LS_DRAFT_ID, lsDraftId);
			loContextDataMap.put(HHSConstants.LS_PLANNED_ID, lsPlannedId);
			if (lsProcurementStatus != null
					&& (lsProcurementStatus.equalsIgnoreCase(lsDraftId) || lsProcurementStatus
							.equalsIgnoreCase(lsPlannedId)))
			{
				if (lsDraftId.equalsIgnoreCase(lsProcurementStatus))
				{
					loUpdatedRows = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoSelectedServBean,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.UPDATE_DROPDOWN_VAL_DRAFT,
							HHSConstants.COM_NYC_HHS_MODEL_SELSERV_BEAN);
				}
				else
				{
					loUpdatedRows = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoSelectedServBean,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.UPDATE_DROPDOWN_VAL_PLANNED,
							HHSConstants.COM_NYC_HHS_MODEL_SELSERV_BEAN);
					if (loUpdatedRows != null && loUpdatedRows <= HHSConstants.INT_ZERO)
					{
						loUpdatedRows = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoSelectedServBean,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.INS_DOP_DOWN_VAL_PLANNED,
								HHSConstants.COM_NYC_HHS_MODEL_SELSERV_BEAN);
					}
				}
			}
			else
			{
				return HHSConstants.ERROR_FLAG;
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			// Catch the exception thrown by masterDao method and pass the
			// caught exception with input params to controller
			loExp.addContextData(HHSConstants.PROC_ID, aoSelectedServBean.getProcurementId());
			setMoState("Transaction Failed:: ProcurementService:saveApprovedProvDetails method :: Error while inserting drop down value in procurement table");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while inserting drop down value in procurement table", loExp);
			throw loExp;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while inserting drop down value in procurement table", loEx);
			throw new ApplicationException("Error while inserting drop down value in procurement table", loEx);
		}
		if (loUpdatedRows != null && loUpdatedRows > HHSConstants.INT_ZERO)
		{
			setMoState("Drop down value inserted successfully");
			return HHSConstants.PASS_FLAG;
		}
		return HHSConstants.FAIL_FLAG;
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
	 * from the DataBase by executing the queries <b>fetchContractSource</b>
	 * query in the procurementMapper</li>
	 * <b>fetchProcurementCONDetailsForR3Contract</b>,
	 * <b>fetchProcurementDetailsForR3Contract </b>
	 * <b>fetchProcurementCONDetails</b>
	 * <li>It returns the values as ProcurementCOF Bean object</li>
	 * <li>The values returned are used in the <code>BMCController</code> which
	 * in turns helps to display the information on the contractFinancials.jsp</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @see BMCController
	 * @param aoMybatisSession SqlSession
	 * @param asContractId id on the basis of which Procurement and Contract
	 *            details will be fetched
	 * @return loProcurementCOF-ProcurementCOF Bean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public ProcurementCOF fetchProcurementCoNDetails(SqlSession aoMybatisSession, String asContractId)
			throws ApplicationException
	{
		ProcurementCOF loProcurementCOF = new ProcurementCOF();
		String lsContractSource = HHSConstants.EMPTY_STRING;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.CONTRACT_ID_KEY, asContractId);

		LOG_OBJECT.Debug("Entered into getting ProcurementCOF Details:" + loContextDataMap.toString());
		try
		{

			lsContractSource = (String) DAOUtil.masterDAO(aoMybatisSession, asContractId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.BMC_FETCH_CONTRACT_SOURCE,
					HHSConstants.JAVA_LANG_STRING);
			if (lsContractSource.equalsIgnoreCase(HHSConstants.TWO))
			{
				loProcurementCOF = (ProcurementCOF) DAOUtil.masterDAO(aoMybatisSession, asContractId,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.FETCH_PROC_CON_DETAILS_FOR_THREE_CONTRACT, HHSConstants.JAVA_LANG_STRING);
				String lsProcurementValue = (String) DAOUtil.masterDAO(aoMybatisSession, asContractId,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.FETCH_PROC_DETAILS_FOR_THREE_CONTRACT, HHSConstants.JAVA_LANG_STRING);
				if (null != lsProcurementValue)
				{
					loProcurementCOF.setProcurementValue(lsProcurementValue);
				}
			}
			else if (lsContractSource.equalsIgnoreCase(HHSConstants.ONE))
			{
				loProcurementCOF = (ProcurementCOF) DAOUtil.masterDAO(aoMybatisSession, asContractId,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_PROC_CON_DETAILS,
						HHSConstants.JAVA_LANG_STRING);
			}

			setMoState("ProcurementCON details fetched successfully for Contract Id:" + asContractId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting ProcurementCOF Details");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting ProcurementCOF Details", loExp);
			throw loExp;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while inserting drop down value in procurement table", loEx);
			throw new ApplicationException("Error while inserting drop down value in procurement table", loEx);
		}
		return loProcurementCOF;
	}

	/**
	 * This method fetches the provider status corresponding to the procurement
	 * Id
	 * <ul>
	 * <li>1. Set procurement Id, provider id and the status values in a Map.</li>
	 * <li>2. Set Map In the Channel.</li>
	 * <li>3. Set procurement id and procurement status in a map for context
	 * data to be logged in case of exception.</li>
	 * <li>4. Execute the query <b>getProviderStatus</b> with procurement Id as
	 * the parameter.</li>
	 * <li>5. Return the provider status.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - Mybatis SQL Session
	 * @param asProcurementId - string representation of Procurement Id
	 * @param asProviderId - string representation of Procurement status
	 * @return lsProviderStatus- string
	 * @throws ApplicationException If an ApplicationException occurs
	 */

	public String getProviderStatus(SqlSession aoMybatisSession, String asProcurementId, String asProviderId)
			throws ApplicationException
	{
		Map<String, String> loProcProviderDetailsMap = new HashMap<String, String>();
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		String lsProviderStatus = null;
		try
		{
			loProcProviderDetailsMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			loProcProviderDetailsMap.put(HHSConstants.PROVIDER_ID_KEY, asProviderId);
			loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			loContextDataMap.put(HHSConstants.PROVIDER_ID_KEY, asProviderId);
			LOG_OBJECT.Debug("Entered into fetching Provider Status for Provider Id::" + loContextDataMap.toString());
			loProcProviderDetailsMap.put(HHSConstants.STATUS_PROCUREMENT_PLANNED, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_PLANNED));
			loProcProviderDetailsMap.put(HHSConstants.STATUS_PROVIDER_SERVICE_APP_REQUIRED, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROVIDER_SERVICE_APP_REQUIRED));
			loProcProviderDetailsMap.put(HHSConstants.STATUS_PROCUREMENT_RELEASED, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_RELEASED));
			loProcProviderDetailsMap.put(HHSConstants.STATUS_PROVIDER_NOT_APPLICABLE, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROVIDER_NOT_APPLICABLE));

			lsProviderStatus = (String) DAOUtil.masterDAO(aoMybatisSession, loProcProviderDetailsMap,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_PROVIDER_STATUS,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Provider Status fetched successfully for Provider Id:" + asProviderId);
		}

		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting Provider details");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Exception occured while getting Provider Status", loExp);
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Exception occured while getting Provider Status", loEx);
			setMoState("Error while getting Provider details");
			throw new ApplicationException("Error while getting Provider details", loEx);
		}
		return lsProviderStatus;
	}

	/**
	 * The method will delete all provider submitted data (that was entered on
	 * S235-Proposal Details).
	 * <ul>
	 * <li>1. Get Procurement related information using
	 * <b>fetchPrevProcStatusId</b> query</li>
	 * <li>2. Check if Procurements previous status Draft or Planned or
	 * Released, and procurement is not open ended</li>
	 * <li>3. if condition at step 2 passes trigger queries
	 * <b>deleteProviderSiteData, deleteProviderQuesResponseData,
	 * deleteProposalDocument, deleteProvidersData</b></li>
	 * <li>4. if condition at step 2 fails and procurement is open ended trigger
	 * queries <b>deleteProvidersDataGroup, deleteProviderQuesResponseDataGroup,
	 * deleteProviderSiteDataGroup, deleteProposalDocumentGroup,
	 * delEvalPoolMappingData, delEvalGroupData</b></li>
	 * <li>If any exception is thrown it will be handled here in the catch block
	 * with an error message and it will be thrown again.</li>
	 * <ul>
	 * <ul>
	 * <li>Method updated in R4</li>
	 * </ul>
	 * Change: added check for open ended RFP Changed By: Pallavi Change Date 18
	 * Feb 2014
	 * 
	 * @param aoMyBatisSession aoMyBatisSession
	 * @param asProcurementId Procurement Id
	 * @param aoSaveStatus aoSaveStatus
	 * @return loDeleteDataStatus Boolean delete status
	 * @throws ApplicationException If an ApplicationException occurs Updated
	 * 
	 */

	@SuppressWarnings("unchecked")
	public Boolean deleteProvidersData(SqlSession aoMyBatisSession, String asProcurementId, Boolean aoSaveStatus)
			throws ApplicationException
	{
		Boolean loDeleteDataStatus = Boolean.FALSE;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loContextDataMap.put(HHSConstants.SAVE_STATUS, aoSaveStatus);
		LOG_OBJECT.Debug("Entered into method to delete all provider submitted data::" + loContextDataMap.toString());
		if (aoSaveStatus)
		{
			try
			{
				Map<String, Object> loResultMap = (Map<String, Object>) DAOUtil.masterDAO(aoMyBatisSession,
						asProcurementId, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.FETCH_PREV_PROC_STATUS_ID, HHSConstants.JAVA_LANG_STRING);
				if (null != loResultMap)
				{
					String lsStatusId = String.valueOf(loResultMap.get(HHSConstants.PREV_STATUS_ID));
					String lsIsOpenEnded = (String) loResultMap.get(HHSConstants.IS_OPEN_ENDED_RFP);
					if (null != lsStatusId
							&& (lsStatusId.equals(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_PROCUREMENT_DRAFT))
									|| lsStatusId.equals(PropertyLoader.getProperty(
											HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_PROCUREMENT_PLANNED)) || lsStatusId
										.equals(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
												HHSConstants.STATUS_PROCUREMENT_RELEASED)))
							&& lsIsOpenEnded.equalsIgnoreCase(HHSConstants.STRING_ZERO))
					{
						DAOUtil.masterDAO(aoMyBatisSession, asProcurementId,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.DELETE_PROV_SITE_DATA,
								HHSConstants.JAVA_LANG_STRING);
						DAOUtil.masterDAO(aoMyBatisSession, asProcurementId,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.DELETE_PROV_QUESTION_DATA,
								HHSConstants.JAVA_LANG_STRING);
						DAOUtil.masterDAO(aoMyBatisSession, asProcurementId,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.DELETE_PROPOSAL_DOCUMENT,
								HHSConstants.JAVA_LANG_STRING);
						DAOUtil.masterDAO(aoMyBatisSession, asProcurementId,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.DEL_PROVIDER_DATA,
								HHSConstants.JAVA_LANG_STRING);
					}
					else if (lsIsOpenEnded.equalsIgnoreCase(HHSConstants.ONE))
					{
						DAOUtil.masterDAO(aoMyBatisSession, asProcurementId,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.DELETE_PROV_SITE_DATA_GROUP,
								HHSConstants.JAVA_LANG_STRING);
						DAOUtil.masterDAO(aoMyBatisSession, asProcurementId,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
								HHSConstants.DELETE_PROV_QUESTION_DATA_GROUP, HHSConstants.JAVA_LANG_STRING);
						DAOUtil.masterDAO(aoMyBatisSession, asProcurementId,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
								HHSConstants.DELETE_PROPOSAL_DOCUMENT_GROUP, HHSConstants.JAVA_LANG_STRING);
						DAOUtil.masterDAO(aoMyBatisSession, asProcurementId,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.DEL_PROVIDER_DATA_GROUP,
								HHSConstants.JAVA_LANG_STRING);
					}
					loDeleteDataStatus = Boolean.TRUE;
				}
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handled over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException loExp)
			{
				loExp.setContextData(loContextDataMap);
				setMoState("Error while deleting all provider submitted data");
				LOG_OBJECT.Error("Error while deleting all provider submitted data :", loExp);
				throw loExp;
			}
			catch (Exception loEx)
			{
				setMoState("Error while deleting all provider submitted data");
				LOG_OBJECT.Error("Error while deleting all provider submitted data :", loEx);
				throw new ApplicationException("Error while deleting all provider submitted data :", loEx);
			}
		}
		return loDeleteDataStatus;
	}

	/**
	 * This method will update procurement status to cancelled
	 * <ul>
	 * <li>1.Connect to database using SqlSession</li>
	 * <li>2.Set the Status Id [Cancelled] in Procurement[QueryId
	 * :updateProcurementStatus]</li>
	 * <li>3.Update Procurement Status as Cancelled</li>
	 * If any exception is thrown it will be handled here in the catch block
	 * with an error messege and it will be thrown again.
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asProcurementId Procurement
	 * @return loCancelProcurementStatus Boolean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean preserveOldStatus(SqlSession aoMybatisSession, String asProcurementId) throws ApplicationException
	{
		Boolean loCancelProcurementStatus = Boolean.FALSE;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		LOG_OBJECT.Debug("Entered into updating procurement status to canceled:" + loContextDataMap.toString());
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, asProcurementId, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
					HHSConstants.PRESERVE_OLD_STAUS, HHSConstants.JAVA_LANG_STRING);
			loCancelProcurementStatus = true;

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while preserving the old status: ");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while preserving the old status: :", loExp);
			throw loExp;
		}
		catch (Exception loEx)
		{
			setMoState("Error while preserving the old status: ");
			LOG_OBJECT.Error("Error while preserving the old status: :", loEx);
			throw new ApplicationException("Error while preserving the old status: :", loEx);
		}
		return loCancelProcurementStatus;

	}

	/**
	 * This method gets list of program name based on input nyc agency id.
	 * 
	 * <ul>
	 * <li>1. Get the agencyId from the procurement bean.</li>
	 * <li>2. execute the query "getProgramNameList" specified in the
	 * procurementMapper.</li>
	 * <li>3. Return the program list.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asAgencyId - a string value of nyc agency id
	 * @param aoIsAgencyOrg - a boolean value to indicate execute query or not
	 * @return a list of program names
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<Procurement> getProgramNameForAgencyId(SqlSession aoMybatisSession, String asAgencyId,
			Boolean aoIsAgencyOrg) throws ApplicationException
	{
		List<Procurement> loProgramNameList = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AS_AGENCY_ID, asAgencyId);
		loContextDataMap.put(HHSConstants.AB_IS_AGENCY_ORG, aoIsAgencyOrg);
		LOG_OBJECT.Debug("Entered into getting list of program name based on input nyc agency id:"
				+ loContextDataMap.toString());
		try
		{
			if (aoIsAgencyOrg)
			{
				loProgramNameList = (List<Procurement>) DAOUtil.masterDAO(aoMybatisSession, asAgencyId,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_PROG_NAME_LIST,
						HHSConstants.JAVA_LANG_STRING);
				setMoState("Program Name List fetched successfully for NYC Agency:");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting program Name List");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting program Name List : ", loExp);
			throw loExp;
		}
		catch (Exception loEx)
		{
			setMoState("Error while getting program Name List");
			LOG_OBJECT.Error("Error while getting program Name List : ", loEx);
			throw new ApplicationException("Error while getting program Name List : ", loEx);
		}
		return loProgramNameList;
	}

	/**
	 * This method gets list of Element Ids for saved services respective to the
	 * procurement from DB *
	 * <ul>
	 * <li>1. assign selected services list as null</li>
	 * <li>2. execute the query "getSavedServicesList" specified in the
	 * procurementMapper.</li>
	 * <li>3. If there are any services respective to procurement in table
	 * PRCRMNT_ADDM_SERVICES then fetch from this table; otherwise fetch from
	 * PROCUREMENT_SERVICES table
	 * <li>4. Return the selected services list.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - a string value of procurement id
	 * @return loSavedServicesList
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	@SuppressWarnings("unchecked")
	public List<SelectedServicesBean> getSavedServicesList(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		List<SelectedServicesBean> loSavedServicesList = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		LOG_OBJECT.Debug("Entered into loading services details::" + loContextDataMap.toString());
		try
		{
			loSavedServicesList = (List<SelectedServicesBean>) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_SAVED_SERVICES_LIST,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Services List fetched successfully");
			loContextDataMap.put(HHSConstants.LO_SAVED_SERVICES_LIST, loSavedServicesList);
		}

		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loExp)
		{
			setMoState("Error while returning selected services list:");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while returning selected services list:", loExp);
			throw loExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loEx)
		{
			ApplicationException loExp = new ApplicationException("Error while returning selected services list:", loEx);
			throw loExp;
		}
		return getSelectedServiceList(loSavedServicesList);
	}

	/**
	 * This method is used to set the service name in the selected bean object
	 * and set this object in list
	 * 
	 * @param aoSelectedServiceList selected service list
	 * @return loSelectedServiceList list of selected service
	 * @throws ApplicationException
	 */
	private List<SelectedServicesBean> getSelectedServiceList(List<SelectedServicesBean> aoSelectedServiceList)
			throws ApplicationException
	{
		List<SelectedServicesBean> loSelectedServiceList = new ArrayList<SelectedServicesBean>();
		org.jdom.Document loTaxonomyDom = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.TAXONOMY_ELEMENT);

		if (aoSelectedServiceList != null && !aoSelectedServiceList.isEmpty())
		{
			for (SelectedServicesBean loSelectedServicesBean : aoSelectedServiceList)
			{
				loSelectedServicesBean.setServiceName(getTaxonomyServiceName(loSelectedServicesBean.getElementId(),
						loTaxonomyDom));
				loSelectedServiceList.add(loSelectedServicesBean);
			}
		}
		return loSelectedServiceList;
	}

	/**
	 * This method is used to get the service name from the taxonomy cache
	 * 
	 * @param asElementId element id
	 * @param aoTaxonomyDom taxonomy dom
	 * @return lsServiceName service name string
	 * @throws ApplicationException
	 */
	public String getTaxonomyServiceName(String asElementId, Document aoTaxonomyDom) throws ApplicationException
	{
		String lsXPath = "//element[(@id=\"" + asElementId + "\")]";
		String lsServiceName = null;
		Element loElement = XMLUtil.getElement(lsXPath, aoTaxonomyDom);
		if (loElement != null)
		{
			String lsEleType = loElement.getAttributeValue("type");
			if (!"TAXONOMY".equals(lsEleType))
			{
				return loElement.getAttributeValue("name");
			}
		}
		return lsServiceName;
	}

	/**
	 * This method will update procurement status to closed
	 * <ul>
	 * <li>1.Connect to database using SqlSession</li>
	 * <li>2.Set the Status Id [Closed] in Procurement</li>
	 * <li>3.Update Procurement Status as Closed</li>
	 * <li>Execute transaction Id <b>updateProcurementStatus</b></li>
	 * If any exception is thrown it will be handled here in the catch block
	 * with an error message and it will be thrown again.
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoStatusMap aoStatusMap
	 * @param aoAuthStatusFlag aoAuthStatusFlag
	 * @return Boolean loCloseProcurementStatus
	 * @throws ApplicationException If an ApplicationException occurs
	 */

	public Boolean updateProcurementStatus(SqlSession aoMybatisSession, Map aoStatusMap, Boolean aoAuthStatusFlag)
			throws ApplicationException
	{
		Boolean loCloseProcurementStatus = Boolean.FALSE;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AO_STATUS_MAP, aoStatusMap);
		loContextDataMap.put(HHSConstants.AUTH_STATUS_FLAG, aoAuthStatusFlag);
		LOG_OBJECT.Debug("Entered into updating procurement status to closed::" + loContextDataMap.toString());
		try
		{
			if (aoAuthStatusFlag)
			{
				DAOUtil.masterDAO(aoMybatisSession, aoStatusMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.UPDATE_PROC_STATUS, HHSConstants.JAVA_UTIL_MAP);
				loCloseProcurementStatus = true;
			}

		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while closing the procurement");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while closing the procurement:", loExp);
			throw loExp;
		}
		catch (Exception loEx)
		{
			setMoState("Error while closing the procurement");
			LOG_OBJECT.Error("Error while closing the procurement:", loEx);
			throw new ApplicationException("Error while closing the procurement", loEx);
		}
		return loCloseProcurementStatus;
	}

	/**
	 * This method fetches the document status.
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asProcurementId If any exception is thrown it will be handled here
	 *            in the catch block with an error messege and it will be thrown
	 *            again.
	 * 
	 * @return loDocumentIdList List
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public List<String> fetchDocumentIdList(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		LOG_OBJECT.Debug("Entered into method for fetching the document status:" + loContextDataMap.toString());
		List<String> loDocumentIdList = null;
		try
		{
			loDocumentIdList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_DOC_ID_LIST,
					HHSConstants.JAVA_LANG_STRING);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while fetching the data for Procurement Id:");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error occurred while fetching the data for Procurement Id:", loExp);
			throw loExp;
		}
		catch (Exception loEx)
		{
			setMoState("Error occurred while fetching the data for Procurement Id:");
			LOG_OBJECT.Error("Error occurred while fetching the data for Procurement Id:", loEx);
			throw new ApplicationException("Error occurred while fetching the data for Procurement Id:", loEx);
		}
		return loDocumentIdList;
	}

	/**
	 * This method gets the procurement Info corresponding to a procurement Id
	 * 
	 * <ul>
	 * <li>1. Retrieve procurement Id</li>
	 * <li>2. Retrieve procurement info by executing query
	 * <b>getProcurementSummaryForNav</b> to fetch the required procurement
	 * summary</li>
	 * <li>3. Return the Procurement Summary.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param asProcurementId - Procurement Id
	 * @return loProcurementSummary - Procurement Info Bean
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public ProcurementInfo getProcurementSummaryForNav(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		ProcurementInfo loProcurementSummary = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		LOG_OBJECT.Debug("Entered into method for getting the procurement Info corresponding to a procurement Id::"
				+ loContextDataMap.toString());
		try
		{
			loProcurementSummary = (ProcurementInfo) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_PROC_SUMMARY_FOR_NAV,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Procurement details fetched successfully for Procurement Id:" + asProcurementId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting procurement Info");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting procurement Info:", loExp);
			throw new ApplicationException("Error while getting procurement Info", loExp);
		}
		return loProcurementSummary;
	}

	/**
	 * This method fetches procurement title, status, agency primary contact and
	 * secondary contact based on input procurement Id
	 * 
	 * <ul>
	 * <li>Pass procurement Id to DAO layer</li>
	 * <li>Execute query with Id "getProcurementTitle" from procurement mapper</li>
	 * <li>Return output to controller</li>
	 * </ul>
	 * <ul>
	 * <li>Method updated in R4</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Sql Session
	 * @param asProcurementId a string value of procurement Id
	 * @param aoNotificationMap aoNotificationMap
	 * @return a map containing procurement title and status id
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getProcurementTitle(SqlSession aoMybatisSession, String asProcurementId,
			HashMap<String, Object> aoNotificationMap) throws ApplicationException
	{
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		try
		{
			loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			loContextDataMap.put(HHSConstants.AO_NOTIFICATION_MAP, aoNotificationMap);
			LOG_OBJECT.Debug("Entered into fetching procurement title, status, agency primary contact:"
					+ loContextDataMap.toString());
			HashMap<String, String> loProcurementMap = (HashMap<String, String>) DAOUtil.masterDAO(aoMybatisSession,
					asProcurementId, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_PROC_TITLE,
					HHSConstants.JAVA_LANG_STRING);
			if (null != aoNotificationMap)
			{
				HashMap<String, String> loRequestMap = (HashMap<String, String>) aoNotificationMap
						.get(TransactionConstants.REQUEST_MAP_PARAMETER_NAME);
				loRequestMap.put(HHSConstants.PROC_TITLE, loProcurementMap.get(HHSConstants.PROC_TITLE));

				List<String> loNotificationList = (List<String>) aoNotificationMap
						.get(HHSConstants.NOTIFICATION_ALERT_ID);
				List<String> loAgencyUserList = new ArrayList<String>();
				loAgencyUserList.add(loProcurementMap.get(HHSConstants.AGENCY_ID_TABLE_COLUMN));

				for (String loNotificationId : loNotificationList)
				{
					((NotificationDataBean) aoNotificationMap.get(loNotificationId)).setAgencyList(loAgencyUserList);
				}

			}
			setMoState("Proposal Title fetched successfully for Procurement Id:" + asProcurementId);
		}

		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while fetching procurement title for Procurement Id:");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error occurred while fetching procurement title for Procurement Id:", loExp);
			throw loExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Error occurred while fetching procurement title for Procurement Id:");
			LOG_OBJECT.Error("Error occurred while fetching procurement title for Procurement Id:", loAppEx);
			throw new ApplicationException("Error occured while getting form data from Taxonomy Maser", loAppEx);

		}
		return aoNotificationMap;
	}

	/**
	 * This method fetches Approved Providers list for notification
	 * <ul>
	 * <li>fetches approved providers list using fetchProcurementCONDetails
	 * query from procurement mapper</li>
	 * </ul>
	 * <ul>
	 * <li>Method updated in R4</li>
	 * </ul>
	 * @param aoMybatisSession SQL mybatis session
	 * @param asProcurementId a string value of procurement Id
	 * @param aoExecuteService a boolean flag indicates to execute service or
	 *            not <li>Execute transaction Id <b>fetchApprovedProviders</b></li>
	 * @param aoNotificationMap notification hashmap
	 * @return modified notification hashmap
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> fetchApprovedProvidersForNotification(SqlSession aoMybatisSession,
			String asProcurementId, Boolean aoExecuteService, HashMap<String, Object> aoNotificationMap)
			throws ApplicationException
	{
		List<String> loApprovedProvList = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loContextDataMap.put(HHSConstants.AB_EXE_SERVICE, aoExecuteService);
		loContextDataMap.put(HHSConstants.AO_NOTIFICATION_MAP, aoNotificationMap);
		LOG_OBJECT
				.Info("Entered into fetching Approved Providers list for notification:" + loContextDataMap.toString());

		try
		{
			if (aoExecuteService)
			{
				loApprovedProvList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_APPROVED_PROVIDERS,
						HHSConstants.JAVA_LANG_STRING);
				List<String> loNotificationList = (List<String>) aoNotificationMap
						.get(HHSConstants.NOTIFICATION_ALERT_ID);

				for (String loNotificationId : loNotificationList)
				{
					((NotificationDataBean) aoNotificationMap.get(loNotificationId))
							.setProviderList(loApprovedProvList);

				}

				setMoState("Approved Providers list fetched successfully for Procurement Id:" + asProcurementId);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while fetching Approved Providers list for notification:");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error occurred while fetching Approved Providers list for notification", loExp);
			throw loExp;
		}
		catch (Exception loEx)
		{
			setMoState("Error occurred while fetching Approved Providers list for notification:");
			LOG_OBJECT.Error("Error occurred while fetching Approved Providers list for notification", loEx);
			throw new ApplicationException("Error occurred while fetching Approved Providers list for notification:",
					loEx);
		}
		return aoNotificationMap;
	}

	/**
	 * This method modified as a part of release 3.1.0 for enhancement request
	 * 6024 This method will get Proposal Custom Question list based on
	 * procurement status
	 * 
	 * <ul>
	 * <li>Get the procurement ID as input and procurement status as input</li>
	 * <li>Get Proposal Custom Question Details by executing query with ID
	 * <b>fetchPropCustomQuesForPlanned</b> from proposal mapper, if procurement
	 * is in planned status</li>
	 * <li>Get Proposal Custom Question Details by executing query with ID
	 * <b>fetchPropCustomQuesForReleased</b> from proposal mapper, if
	 * procurement is in released status</li>
	 * <li>Get the proposal custom question list as output and return to
	 * controller</li>
	 * <ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - a string value of procurement Id
	 * @param asProcurementStatus - a string value of procurement status
	 * @return loCustomQuestionList - a list of proposal custom questions
	 * @throws ApplicationException If an Application exception occurs
	 */
	@SuppressWarnings("unchecked")
	public List<ProposalQuestionAnswerBean> fetchProposalCustomQuestions(SqlSession aoMybatisSession,
			String asProcurementId, String asProcurementStatus) throws ApplicationException
	{
		List<ProposalQuestionAnswerBean> loCustomQuestionList = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loContextDataMap.put(HHSConstants.PROCUREMENT_STATUS_KEY, asProcurementStatus);
		LOG_OBJECT.Debug("Entered into loading Proposal Custom Question Details::" + loContextDataMap.toString());
		try
		{
			HashMap<String, String> loInputMap = new HashMap<String, String>();
			loInputMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
			loInputMap.put(HHSConstants.PROCUREMENT_STATUS, asProcurementStatus);
			if (!asProcurementStatus.equalsIgnoreCase(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_RELEASED)))
			{
				loCustomQuestionList = (List<ProposalQuestionAnswerBean>) DAOUtil.masterDAO(aoMybatisSession,
						loInputMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.FETCH_PROP_QUES_FOR_PLANNED, HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			else
			{
				loCustomQuestionList = (List<ProposalQuestionAnswerBean>) DAOUtil.masterDAO(aoMybatisSession,
						loInputMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.FETCH_PROP_QUES_FOR_RELEASED, HHSConstants.JAVA_UTIL_HASH_MAP);
				if (null == loCustomQuestionList || loCustomQuestionList.isEmpty())
				{
					loCustomQuestionList = (List<ProposalQuestionAnswerBean>) DAOUtil.masterDAO(aoMybatisSession,
							loInputMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
							HHSConstants.FETCH_PROP_QUES_FOR_PLANNED, HHSConstants.JAVA_UTIL_HASH_MAP);
				}
			}
			setMoState("Proposal Custom Question Details fetched successfully for procurement Id:" + asProcurementId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while getting data from fetchProposalCustomQuestions in fetchProposalConfigurationDetails ",
							loAppEx);
			setMoState("Transaction Failed:: fetchProposalConfigurationDetails: fetchProposalCustomQuestions method - failed to get all questions where procurementId="
					+ asProcurementId + " \n");
			loAppEx.setContextData(loContextDataMap);
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT
					.Error("Exception occured while getting data from fetchProposalCustomQuestions in fetchProposalConfigurationDetails ",
							loEx);
			setMoState("Transaction Failed:: fetchProposalConfigurationDetails: fetchProposalCustomQuestions method - failed to get all questions where procurementId="
					+ asProcurementId + " \n");
			throw new ApplicationException(
					"Exception occured while getting data from fetchProposalCustomQuestions in fetchProposalConfigurationDetails",
					loEx);
		}
		return loCustomQuestionList;
	}

	/**
	 * This method modified as a part of release 3.1.0 for enhancement request
	 * 6024 The method will fetch the Proposal document Type list for proposal
	 * Configuration based on procurement Id
	 * 
	 * <ul>
	 * <li>Get the procurement ID as input and procurement status as input</li>
	 * <li>Get Proposal Document type by executing query with ID
	 * "fetchPropDocTypeForPlanned" from proposal mapper, if procurement is in
	 * planned status</li>
	 * <li>Get Proposal Document type by executing query with ID
	 * "fetchPropDocTypeForReleased" from proposal mapper, if procurement is in
	 * released status</li>
	 * <li>Get the proposal document type list as output and return to
	 * controller</li>
	 * <ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProcurementId - a string value of procurement Id
	 * @param asProcurementStatus - a string value of procurement status
	 * @return loDocumentList - a list of proposal document types
	 * @throws ApplicationException If an Application exception occurs
	 */
	@SuppressWarnings("unchecked")
	public List<ExtendedDocument> fetchProposalDocumentType(SqlSession aoMybatisSession, String asProcurementId,
			String asProcurementStatus) throws ApplicationException
	{
		List<ExtendedDocument> loDocumentList = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loContextDataMap.put(HHSConstants.PROCUREMENT_STATUS_KEY, asProcurementStatus);
		LOG_OBJECT
				.Info("Entered into getting data from fetchProposalDocumentType in fetchProposalConfigurationDetails:"
						+ loContextDataMap.toString());
		try
		{

			HashMap<String, String> loInputMap = new HashMap<String, String>();
			loInputMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
			loInputMap.put(HHSConstants.PROCUREMENT_STATUS, asProcurementStatus);
			if (!asProcurementStatus.equalsIgnoreCase(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_RELEASED)))
			{
				loDocumentList = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMybatisSession, loInputMap,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_PROP_DOC_FOR_PLANNED,
						HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			else
			{
				loDocumentList = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMybatisSession, loInputMap,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_PROP_DOC_FOR_RELEASED,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				if (null == loDocumentList || loDocumentList.isEmpty())
				{
					loDocumentList = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMybatisSession, loInputMap,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_PROP_DOC_FOR_PLANNED,
							HHSConstants.JAVA_UTIL_HASH_MAP);
				}
			}
			setMoState("Proposal Doc Type fetched successfully for procurement Id:" + asProcurementId);
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while getting data from fetchProposalDocumentType in fetchProposalConfigurationDetails ",
							loAppEx);
			setMoState("Transaction Failed:: fetchProposalConfigurationDetails: fetchProposalDocumentType method - failed to get all document Type where procurementId="
					+ asProcurementId + " \n");
			loAppEx.setContextData(loContextDataMap);
			throw loAppEx;
		}
		catch (Exception loEx)
		{
			LOG_OBJECT
					.Error("Exception occured while getting data from fetchProposalDocumentType in fetchProposalConfigurationDetails ",
							loEx);
			setMoState("Transaction Failed:: fetchProposalConfigurationDetails: fetchProposalDocumentType method - failed to get all document Type where procurementId="
					+ asProcurementId + " \n");
			throw new ApplicationException(
					"Exception occured while getting data from fetchProposalDocumentType in fetchProposalConfigurationDetails ",
					loEx);
		}
		return loDocumentList;
	}

	/**
	 * This method modified as a part of release 3.1.0 for enhancement request
	 * 6024 The method will save Proposal Custom Questions details based on
	 * procurement status Created For Addendum Enhancement
	 * 
	 * <ul>
	 * <li>Get the Proposal Custom Question List from input
	 * aoProposalDetailsBean</li>
	 * <li>check if procurement status is released or not</li>
	 * <li>If not, call savePropQuesForPlannedProc() to save the proposal custom
	 * ques</li>
	 * <li>Else call saveAddendumQuesForReleasedProc() to save proposal custom
	 * ques</li>
	 * </ul>
	 * 
	 * @author sadhna.verma
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoProposalDetailsBean - a proposal detail bean containing custom
	 *            questions list
	 * @return loSaveQueStatus - a boolean value of save status
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean saveProposalCustomQuestions(SqlSession aoMybatisSession, ProposalDetailsBean aoProposalDetailsBean)
			throws ApplicationException
	{
		Boolean loSaveQueStatus = false;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		try
		{
			if (null != aoProposalDetailsBean)
			{
				if (!aoProposalDetailsBean.getProcurementStatus().equalsIgnoreCase(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROCUREMENT_RELEASED)))
				{
					savePropQuesForPlannedProc(aoMybatisSession, aoProposalDetailsBean);
				}
				else
				{
					saveAddendumQuesForReleasedProc(aoMybatisSession, aoProposalDetailsBean);
				}
			}
			loSaveQueStatus = true;
			setMoState("Proposal Custom Questions saved successfully for procurement ID:"
					+ aoProposalDetailsBean.getProcurementId());
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while saving custom questions for procurement ID:"
					+ aoProposalDetailsBean.getProcurementId());
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error(
					"Error occurred while saving custom questions for procurement ID:"
							+ aoProposalDetailsBean.getProcurementId(), loExp);
			throw loExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Error occurred while saving custom questions for procurement ID:"
					+ aoProposalDetailsBean.getProcurementId());
			LOG_OBJECT.Error(
					"Error occurred while saving custom questions for procurement ID:"
							+ aoProposalDetailsBean.getProcurementId(), loAppEx);
			throw new ApplicationException("Error occurred while saving custom questions for procurement ID:"
					+ aoProposalDetailsBean.getProcurementId(), loAppEx);

		}
		return loSaveQueStatus;
	}

	/**
	 * This method modified as a part of release 3.1.0 for enhancement request
	 * 6024 The method will save Addendum Questions for released procurement
	 * Created For Addendum Enhancement
	 * 
	 * <ul>
	 * <li>Get the Proposal Custom Question List from input
	 * aoProposalDetailsBean</li>
	 * <li>Iterate through the list and set procurement Id, created by and
	 * modified by</li>
	 * <li>check if existing row is deleted by unchecking the check box</li>
	 * <li>If yes, execute query with id
	 * <b>deleteAddendumProposalCustomQuestions</b> to delete the existing row
	 * from procurement mapper</li>
	 * <li>Else check if the row already exists in table or it is a new entry</li>
	 * <li>If it is new, then execute insert query with Id
	 * <b>insertAddendumProposalCustomQuestions</b> from procurement mapper</li>
	 * <li>If it is old, then execute update query with Id
	 * <b>updateAddendumProposalCustomQuestions</b> from procurement mapper</li>
	 * </ul>
	 * 
	 * @author sadhna.verma
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoProposalDetailsBean - a proposal detail bean containing custom
	 *            questions list
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	private void saveAddendumQuesForReleasedProc(SqlSession aoMybatisSession, ProposalDetailsBean aoProposalDetailsBean)
			throws ApplicationException
	{
		Integer loCounter = HHSConstants.INT_ONE;
		for (ProposalQuestionAnswerBean loProposalQuestionAnswerBean : aoProposalDetailsBean
				.getQuestionAnswerBeanList())
		{
			loProposalQuestionAnswerBean.setCreatedBy(aoProposalDetailsBean.getCreatedBy());
			loProposalQuestionAnswerBean.setModifiedBy(aoProposalDetailsBean.getModifiedBy());
			loProposalQuestionAnswerBean.setProcurementId(aoProposalDetailsBean.getProcurementId());
			if ((quesAnsCond1(loProposalQuestionAnswerBean))
					&& (loProposalQuestionAnswerBean.getAddendumId() == null || loProposalQuestionAnswerBean
							.getAddendumId().isEmpty()))
			{
				continue;
			}
			if ((quesAnsCond1(loProposalQuestionAnswerBean)) && (quesAnsCond2(loProposalQuestionAnswerBean)))
			{
				DAOUtil.masterDAO(aoMybatisSession, loProposalQuestionAnswerBean,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.DEL_ADD_PROPOSAL_CUSTOM_QUE,
						HHSConstants.COM_NYC_MODEL_PROP_QUE_ANS_BEAN);
			}
			else
			{
				loProposalQuestionAnswerBean.setQuestionSeqNo(loCounter.toString());
				++loCounter;
				if (loProposalQuestionAnswerBean.getAddendumId() == null
						|| loProposalQuestionAnswerBean.getAddendumId().isEmpty())
				{
					DAOUtil.masterDAO(aoMybatisSession, loProposalQuestionAnswerBean,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.INS_ADD_PROPOSAL_CUSTOM_QUE,
							HHSConstants.COM_NYC_MODEL_PROP_QUE_ANS_BEAN);
				}
				else
				{
					DAOUtil.masterDAO(aoMybatisSession, loProposalQuestionAnswerBean,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.UPDATE_ADD_PROPOSAL_CUSTOM_QUE,
							HHSConstants.COM_NYC_MODEL_PROP_QUE_ANS_BEAN);
				}
			}
		}
	}

	/**
	 * Ques And Cond2
	 * @param loProposalQuestionAnswerBean
	 * @return
	 */
	private boolean quesAnsCond2(ProposalQuestionAnswerBean loProposalQuestionAnswerBean)
	{
		return (loProposalQuestionAnswerBean.getAddendumId() != null && !loProposalQuestionAnswerBean.getAddendumId()
				.isEmpty())
				|| (loProposalQuestionAnswerBean.getProcurementQnId() != null && !loProposalQuestionAnswerBean
						.getProcurementQnId().isEmpty());
	}

	/**
	 * Ques And Cond1
	 * @param loProposalQuestionAnswerBean
	 * @return
	 */
	private boolean quesAnsCond1(ProposalQuestionAnswerBean loProposalQuestionAnswerBean)
	{
		return loProposalQuestionAnswerBean.getQuestionText() == null
				|| loProposalQuestionAnswerBean.getQuestionText().isEmpty();
	}

	/**
	 * This method modified as a part of release 3.1.0 for enhancement request
	 * 6024 The method will save Proposal Custom Questions for Planned
	 * procurement Created For Addendum Enhancement
	 * 
	 * <ul>
	 * <li>Get the Proposal Custom Question List from input
	 * aoProposalDetailsBean</li>
	 * <li>Iterate through the list and set procurement Id, created by and
	 * modified by</li>
	 * <li>check if existing row is deleted by unchecking the check box</li>
	 * <li>If yes, execute query with id <b>deleteProposalCustomQuestions</b>
	 * and <b>deleteProposalMappingQues</b> to delete the existing row from
	 * procurement mapper</li>
	 * <li>Else check if the row already exists in table or it is a new entry</li>
	 * <li>If it is new, then execute insert query with Id
	 * <b>insertProposalCustomQuestions</b> and <b>insertProposalMappingQues</b>
	 * from procurement mapper</li>
	 * <li>If it is old, then execute update query with Id
	 * <b>updateProposalCustomQuestions</b> and <b>updateProposalMappingQues</b>
	 * from procurement mapper</li>
	 * </ul>
	 * 
	 * @author sadhna.verma
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoProposalDetailsBean - a proposal detail bean containing custom
	 *            questions list
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	private void savePropQuesForPlannedProc(SqlSession aoMybatisSession, ProposalDetailsBean aoProposalDetailsBean)
			throws ApplicationException
	{
		Integer loCounter = HHSConstants.INT_ONE;
		Integer loInsert = null;
		Integer loDelete = null;
		Integer loUpdate = null;
		for (ProposalQuestionAnswerBean loProposalQuestionAnswerBean : aoProposalDetailsBean
				.getQuestionAnswerBeanList())
		{
			loProposalQuestionAnswerBean.setCreatedBy(aoProposalDetailsBean.getCreatedBy());
			loProposalQuestionAnswerBean.setModifiedBy(aoProposalDetailsBean.getModifiedBy());
			loProposalQuestionAnswerBean.setProcurementId(aoProposalDetailsBean.getProcurementId());
			if ((quesAnsCond1(loProposalQuestionAnswerBean))
					&& (loProposalQuestionAnswerBean.getProcurementQnId() == null || loProposalQuestionAnswerBean
							.getProcurementQnId().isEmpty()))
			{
				continue;
			}
			if ((quesAnsCond1(loProposalQuestionAnswerBean))
					&& loProposalQuestionAnswerBean.getProcurementQnId() != null
					&& !loProposalQuestionAnswerBean.getProcurementQnId().isEmpty())
			{
				loDelete = (Integer) DAOUtil.masterDAO(aoMybatisSession,
						loProposalQuestionAnswerBean.getProcurementQnId(),
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.DELETE_PROPOSAL_MAPPING_QUES,
						HHSConstants.JAVA_LANG_STRING);

				if (null != loDelete && loDelete >= HHSConstants.INT_ONE)
				{
					DAOUtil.masterDAO(aoMybatisSession, loProposalQuestionAnswerBean.getProcurementQnId(),
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.DEL_PROPOSAL_CUSTOM_QUE,
							HHSConstants.JAVA_LANG_STRING);
				}
			}
			else
			{
				loProposalQuestionAnswerBean.setQuestionSeqNo(loCounter.toString());
				++loCounter;
				if (loProposalQuestionAnswerBean.getProcurementQnId() == null
						|| loProposalQuestionAnswerBean.getProcurementQnId().isEmpty())
				{
					loInsert = (Integer) DAOUtil.masterDAO(aoMybatisSession, loProposalQuestionAnswerBean,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.INS_PROPOSAL_CUSTOM_QUE,
							HHSConstants.COM_NYC_MODEL_PROP_QUE_ANS_BEAN);
					if (null != loInsert && loInsert >= HHSConstants.INT_ONE)
					{
						DAOUtil.masterDAO(aoMybatisSession, loProposalQuestionAnswerBean,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
								HHSConstants.INSERT_PROPOSAL_MAPPING_QUES, HHSConstants.COM_NYC_MODEL_PROP_QUE_ANS_BEAN);
					}
				}
				else
				{
					loUpdate = (Integer) DAOUtil.masterDAO(aoMybatisSession, loProposalQuestionAnswerBean,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.UPDATE_PROPOSAL_CUSTOM_QUE,
							HHSConstants.COM_NYC_MODEL_PROP_QUE_ANS_BEAN);
					if (null != loUpdate && loUpdate >= HHSConstants.INT_ONE)
					{
						DAOUtil.masterDAO(aoMybatisSession, loProposalQuestionAnswerBean,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
								HHSConstants.UPDATE_PROPOSAL_MAPPING_QUES, HHSConstants.COM_NYC_MODEL_PROP_QUE_ANS_BEAN);
					}
				}
			}
		}
	}

	/**
	 * This method modified as a part of release 3.1.0 for enhancement request
	 * 6024 The method will save Proposal Required Doc Types based on
	 * procurement status Created For Addendum Enhancement
	 * 
	 * <ul>
	 * <li>Get the Proposal Required Doc Types List from input
	 * aoProposalDetailsBean</li>
	 * <li>check if procurement status is released or not</li>
	 * <li>If not, call savePropDocTypeForPlannedProc() to save the proposal
	 * required doc types</li>
	 * <li>Else call saveAddendumDocTypeForReleasedProc() to save proposal
	 * required doc types</li>
	 * </ul>
	 * 
	 * @author sadhna.verma
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoProposalDetailsBean - a proposal detail bean containing required
	 *            doc types list
	 * @return loSaveDocStatus - a boolean value of save status
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean saveProposalDocumentType(SqlSession aoMybatisSession, ProposalDetailsBean aoProposalDetailsBean)
			throws ApplicationException
	{
		Boolean loSaveDocStatus = false;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		try
		{
			if (null != aoProposalDetailsBean)
			{
				if (!aoProposalDetailsBean.getProcurementStatus().equalsIgnoreCase(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROCUREMENT_RELEASED)))
				{
					savePropDocTypeForPlannedProc(aoMybatisSession, aoProposalDetailsBean);
				}
				else
				{
					saveAddendumDocTypeForReleasedProc(aoMybatisSession, aoProposalDetailsBean);
				}
				processOptionalDocumentType(aoMybatisSession, aoProposalDetailsBean);
			}
			loSaveDocStatus = true;
			setMoState("Proposal Doc Types saved successfully for procurement ID:"
					+ aoProposalDetailsBean.getProcurementId());
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while saving doc types for procurement ID:"
					+ aoProposalDetailsBean.getProcurementId());
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error(
					"Error occurred while saving doc types for procurement ID:"
							+ aoProposalDetailsBean.getProcurementId(), loExp);
			throw loExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Error occurred while saving doc types for procurement ID:"
					+ aoProposalDetailsBean.getProcurementId());
			LOG_OBJECT.Error(
					"Error occurred while saving doc types for procurement ID:"
							+ aoProposalDetailsBean.getProcurementId(), loAppEx);
			throw new ApplicationException("Error occurred while saving doc types for procurement ID:"
					+ aoProposalDetailsBean.getProcurementId(), loAppEx);

		}
		return loSaveDocStatus;
	}

	/**
	 * This method modified as a part of release 3.1.0 for enhancement request
	 * 6024 The method will save Addendum required doc types for released
	 * procurement Created For Addendum Enhancement
	 * 
	 * <ul>
	 * <li>Get the Proposal required doc type List from input
	 * aoProposalDetailsBean</li>
	 * <li>Iterate through the list and set procurement Id, created by and
	 * modified by</li>
	 * <li>check if existing row is deleted by unchecking the check box</li>
	 * <li>If yes, execute query with id
	 * <b>deleteAddendumProposalDocumentType</b> to delete the existing row from
	 * procurement mapper</li>
	 * <li>Else check if the row already exists in table or it is a new entry</li>
	 * <li>If it is new, then execute insert query with Id
	 * <b>insertAddendumProposalDocumentType</b> from procurement mapper</li>
	 * <li>If it is old, then execute update query with Id
	 * <b>updateAddendumProposalDocumentType</b> from procurement mapper</li>
	 * </ul>
	 * 
	 * @author sadhna.verma
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoProposalDetailsBean - a proposal detail bean containing required
	 *            doc type list
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	private void saveAddendumDocTypeForReleasedProc(SqlSession aoMybatisSession,
			ProposalDetailsBean aoProposalDetailsBean) throws ApplicationException
	{

		Integer loCounter = HHSConstants.INT_ONE;
		for (ExtendedDocument loDocTypeBean : aoProposalDetailsBean.getRequiredDocumentList())
		{
			loDocTypeBean.setCustomLabelName(loDocTypeBean.getCustomLabelName().trim());
			loDocTypeBean.setCreatedBy(aoProposalDetailsBean.getCreatedBy());
			loDocTypeBean.setLastModifiedById(aoProposalDetailsBean.getModifiedBy());
			loDocTypeBean.setProcurementId(aoProposalDetailsBean.getProcurementId());
			loDocTypeBean.setRequiredFlag(HHSConstants.ONE);
			if (loDocTypeBean.getDocumentType() == null
					|| loDocTypeBean.getDocumentType().isEmpty()
					&& (null == loDocTypeBean.getAddendumDocumentId() || loDocTypeBean.getAddendumDocumentId()
							.isEmpty()))
			{
				continue;
			}
			if ((saveAddDocTypeCond2(loDocTypeBean)) && (saveAddDocTypeCond1(loDocTypeBean)))
			{
				DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.DEL_ADD_PROPOSAL_DOC_TYPE, HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
			}
			else
			{
				loDocTypeBean.setDocumentSeqNumber(loCounter.toString());
				++loCounter;
				if (loDocTypeBean.getAddendumDocumentId() == null || loDocTypeBean.getAddendumDocumentId().isEmpty())
				{
					DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
							HHSConstants.INS_ADD_PROPOSAL_DOC_TYPE, HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
				}
				else
				{
					DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
							HHSConstants.UPDATE_ADD_PRO_DOC_TYPE, HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
				}
			}
		}

	}
	/**
	 * This method is used to save and add doctype condition 1
	 * @param loDocTypeBean an ExtendedDocument bean
	 * @return boolean value 
	 */
	private boolean saveAddDocTypeCond2(ExtendedDocument loDocTypeBean)
	{
		return loDocTypeBean.getDocumentType() == null || loDocTypeBean.getDocumentType().isEmpty();
	}
	/**
	 * This method is used to save and add doctype condition 2
	 * @param loDocTypeBean a ExtendedDocument bean
	 * @return boolean value
	 */
	private boolean saveAddDocTypeCond1(ExtendedDocument loDocTypeBean)
	{
		return (null != loDocTypeBean.getAddendumDocumentId() && !loDocTypeBean.getAddendumDocumentId().isEmpty())
				|| (null != loDocTypeBean.getProcurementDocumentId() && !loDocTypeBean.getProcurementDocumentId()
						.isEmpty());
	}

	/**
	 * This method modified as a part of release 3.1.0 for enhancement request
	 * 6024 The method will save Proposal Required doc type for Planned
	 * procurement Created For Addendum Enhancement
	 * 
	 * <ul>
	 * <li>Get the Proposal Required doc type List from input
	 * aoProposalDetailsBean</li>
	 * <li>Iterate through the list and set procurement Id, created by and
	 * modified by</li>
	 * <li>check if existing row is deleted by unchecking the check box</li>
	 * <li>If yes, execute query with id <b>deleteProposalDocumentType</b> and
	 * <b>deleteProposalMappingDoc</b> to delete the existing row from
	 * procurement mapper</li>
	 * <li>Else check if the row already exists in table or it is a new entry</li>
	 * <li>If it is new, then execute insert query with Id
	 * <b>insertProposalDocumentType</b> and <b>insertProposalMappingDoc</b>
	 * from procurement mapper</li>
	 * <li>If it is old, then execute update query with Id
	 * <b>updateProposalDocumentType</b> and <b>updateProposalMappingDoc</b>
	 * from procurement mapper</li>
	 * </ul>
	 * 
	 * @author sadhna.verma
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoProposalDetailsBean - a proposal detail bean containing Required
	 *            doc type list
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	private void savePropDocTypeForPlannedProc(SqlSession aoMybatisSession, ProposalDetailsBean aoProposalDetailsBean)
			throws ApplicationException
	{
		Integer loCounter = HHSConstants.INT_ONE;
		Integer loInsert = null;
		Integer loDelete = null;
		Integer loUpdate = null;

		for (ExtendedDocument loDocTypeBean : aoProposalDetailsBean.getRequiredDocumentList())
		{
			loDocTypeBean.setCustomLabelName(loDocTypeBean.getCustomLabelName().trim());
			loDocTypeBean.setCreatedBy(aoProposalDetailsBean.getCreatedBy());
			loDocTypeBean.setLastModifiedById(aoProposalDetailsBean.getModifiedBy());
			loDocTypeBean.setProcurementId(aoProposalDetailsBean.getProcurementId());
			loDocTypeBean.setRequiredFlag(HHSConstants.ONE);
			loDocTypeBean.setReferenceDocSeqNo(loCounter.toString());
			if (loDocTypeBean.getDocumentType() == null || loDocTypeBean.getDocumentType().isEmpty()
					&& (saveProcDocTypePlannedCond1(loDocTypeBean))
					|| HHSConstants.TRUE.equalsIgnoreCase(loDocTypeBean.getIsAddendum()))
			{
				continue;
			}

			if (loDocTypeBean.getDocumentType() == null
					|| loDocTypeBean.getDocumentType().isEmpty()
					&& (null != loDocTypeBean.getDocumentSeqNumber() && !loDocTypeBean.getDocumentSeqNumber().isEmpty()))
			{
				loDelete = (Integer) DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean.getProcurementDocumentId(),
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.DELETE_PROPOSAL_MAPPING_DOC,
						HHSConstants.JAVA_LANG_STRING);
				if (null != loDelete && loDelete >= HHSConstants.INT_ONE)
				{
					DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean.getProcurementDocumentId(),
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.DEL_PROPOSAL_DOC_TYPE,
							HHSConstants.JAVA_LANG_STRING);
				}
			}
			else
			{
				loDocTypeBean.setDocumentSeqNumber(loCounter.toString());
				++loCounter;
				if (loDocTypeBean.getProcurementDocumentId() == null
						|| loDocTypeBean.getProcurementDocumentId().isEmpty())
				{
					loInsert = (Integer) DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.INS_PROPOSAL_DOC_TYPE,
							HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
					if (null != loInsert && loInsert >= HHSConstants.INT_ONE)
					{
						DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.INSERT_PROPOSAL_MAPPING_DOC,
								HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
					}
				}
				else
				{
					loUpdate = (Integer) DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.UPDATE_PROPOSAL_DOC_TYPE,
							HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
					if (null != loUpdate && loUpdate >= HHSConstants.INT_ONE)
					{
						DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.UPDATE_PROPOSAL_MAPPING_DOC,
								HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
					}
				}
			}
		}
	}
	/**
	 * This method is used to save procurement doctype planned condition 1
	 * @param loDocTypeBean a ExtendedDocument bean
	 * @return boolean value
	 */
	private boolean saveProcDocTypePlannedCond1(ExtendedDocument loDocTypeBean)
	{
		return null == loDocTypeBean.getDocumentSeqNumber() || loDocTypeBean.getDocumentSeqNumber().isEmpty();
	}

	/**
	 * This method modified as a part of release 3.1.0 for enhancement request
	 * 6024 The method will save Proposal Optional Doc Types based on
	 * procurement status Created For Addendum Enhancement
	 * 
	 * <ul>
	 * <li>Get the Proposal Optional Doc Types List from input
	 * aoProposalDetailsBean</li>
	 * <li>check if procurement status is released or not</li>
	 * <li>If not, call saveOptionalDocTypeForPlannedProc() to save the proposal
	 * Optional doc types</li>
	 * <li>Else call saveAddendumOptionalDocTypeForReleasedProc() to save
	 * proposal Optional doc types</li>
	 * </ul>
	 * 
	 * @author sadhna.verma
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoProposalDetailsBean - a proposal detail bean containing Optional
	 *            doc types list
	 * @return void
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	private void processOptionalDocumentType(SqlSession aoMybatisSession, ProposalDetailsBean aoProposalDetailsBean)
			throws ApplicationException
	{
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		try
		{
			if (null != aoProposalDetailsBean)
			{
				if (!aoProposalDetailsBean.getProcurementStatus().equalsIgnoreCase(
						PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROCUREMENT_RELEASED)))
				{
					saveOptionalDocTypeForPlannedProc(aoMybatisSession, aoProposalDetailsBean);
				}
				else
				{
					saveAddendumOptionalDocTypeForReleasedProc(aoMybatisSession, aoProposalDetailsBean);
				}
			}
			setMoState("Proposal optional Doc types saved successfully for procurement ID:"
					+ aoProposalDetailsBean.getProcurementId());
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while saving optional doc types for procurement ID:"
					+ aoProposalDetailsBean.getProcurementId());
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error occurred while saving optional doc types for procurement ID:"
					+ aoProposalDetailsBean.getProcurementId(), loExp);
			throw loExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Error occurred while saving optional doc types for procurement ID:"
					+ aoProposalDetailsBean.getProcurementId());
			LOG_OBJECT.Error("Error occurred while saving optional doc types for procurement ID:"
					+ aoProposalDetailsBean.getProcurementId(), loAppEx);
			throw new ApplicationException("Error occurred while saving optional doc types for procurement ID:"
					+ aoProposalDetailsBean.getProcurementId(), loAppEx);

		}
	}

	/**
	 * This method modified as a part of release 3.1.0 for enhancement request
	 * 6024 The method will save Addendum optional doc types for released
	 * procurement Created For Addendum Enhancement
	 * 
	 * <ul>
	 * <li>Get the Proposal optional doc type List from input
	 * aoProposalDetailsBean</li>
	 * <li>Iterate through the list and set procurement Id, created by and
	 * modified by</li>
	 * <li>check if existing row is deleted by unchecking the check box</li>
	 * <li>If yes, execute query with id
	 * <b>deleteAddendumProposalDocumentType</b> to delete the existing row from
	 * procurement mapper</li>
	 * <li>Else check if the row already exists in table or it is a new entry</li>
	 * <li>If it is new, then execute insert query with Id
	 * <b>insertAddendumProposalDocumentType</b> from procurement mapper</li>
	 * <li>If it is old, then execute update query with Id
	 * <b>updateAddendumProposalDocumentType</b> from procurement mapper</li>
	 * </ul>
	 * 
	 * @author sadhna.verma
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoProposalDetailsBean - a proposal detail bean containing optional
	 *            doc type list
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	private void saveAddendumOptionalDocTypeForReleasedProc(SqlSession aoMybatisSession,
			ProposalDetailsBean aoProposalDetailsBean) throws ApplicationException
	{

		Integer loCounter = HHSConstants.INT_ONE;
		for (ExtendedDocument loDocTypeBean : aoProposalDetailsBean.getOptionalDocumentList())
		{
			loDocTypeBean.setCustomLabelName(loDocTypeBean.getCustomLabelName().trim());
			loDocTypeBean.setCreatedBy(aoProposalDetailsBean.getCreatedBy());
			loDocTypeBean.setLastModifiedById(aoProposalDetailsBean.getModifiedBy());
			loDocTypeBean.setProcurementId(aoProposalDetailsBean.getProcurementId());
			loDocTypeBean.setRequiredFlag(HHSConstants.ZERO);
			if (loDocTypeBean.getDocumentType() == null
					|| loDocTypeBean.getDocumentType().isEmpty()
					&& (null == loDocTypeBean.getAddendumDocumentId() || loDocTypeBean.getAddendumDocumentId()
							.isEmpty()))
			{
				continue;
			}
			if ((saveAddDocTypeCond2(loDocTypeBean)) && (saveAddDocTypeCond1(loDocTypeBean)))
			{
				DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.DEL_ADD_PROPOSAL_DOC_TYPE, HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
			}
			else
			{
				loDocTypeBean.setDocumentSeqNumber(loCounter.toString());
				++loCounter;
				if (loDocTypeBean.getAddendumDocumentId() == null || loDocTypeBean.getAddendumDocumentId().isEmpty())
				{
					DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
							HHSConstants.INS_ADD_PROPOSAL_DOC_TYPE, HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
				}
				else
				{
					DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
							HHSConstants.UPDATE_ADD_PRO_DOC_TYPE, HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
				}
			}
		}

	}

	/**
	 * This method modified as a part of release 3.1.0 for enhancement request
	 * 6024 The method will save Proposal Optional doc type for Planned
	 * procurement Created For Addendum Enhancement
	 * 
	 * <ul>
	 * <li>Get the Proposal Optional doc type List from input
	 * aoProposalDetailsBean</li>
	 * <li>Iterate through the list and set procurement Id, created by and
	 * modified by</li>
	 * <li>check if existing row is deleted by unchecking the check box</li>
	 * <li>If yes, execute query with id <b>deleteProposalDocumentType</b> and
	 * <b>deleteProposalMappingDoc</b> to delete the existing row from
	 * procurement mapper</li>
	 * <li>Else check if the row already exists in table or it is a new entry</li>
	 * <li>If it is new, then execute insert query with Id
	 * <b>insertProposalDocumentType</b> and <b>insertProposalMappingDoc</b>
	 * from procurement mapper</li>
	 * <li>If it is old, then execute update query with Id
	 * <b>updateProposalDocumentType</b> and <b>updateProposalMappingDoc</b>
	 * from procurement mapper</li>
	 * </ul>
	 * 
	 * @author sadhna.verma
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoProposalDetailsBean - a proposal detail bean containing Optional
	 *            doc type list
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	private void saveOptionalDocTypeForPlannedProc(SqlSession aoMybatisSession,
			ProposalDetailsBean aoProposalDetailsBean) throws ApplicationException
	{
		Integer loCounter = HHSConstants.INT_ONE;
		Integer loInsert = null;
		Integer loDelete = null;
		Integer loUpdate = null;

		for (ExtendedDocument loDocTypeBean : aoProposalDetailsBean.getOptionalDocumentList())
		{
			loDocTypeBean.setCustomLabelName(loDocTypeBean.getCustomLabelName().trim());
			loDocTypeBean.setCreatedBy(aoProposalDetailsBean.getCreatedBy());
			loDocTypeBean.setLastModifiedById(aoProposalDetailsBean.getModifiedBy());
			loDocTypeBean.setProcurementId(aoProposalDetailsBean.getProcurementId());
			loDocTypeBean.setRequiredFlag(HHSConstants.ZERO);
			if (loDocTypeBean.getDocumentType() == null || loDocTypeBean.getDocumentType().isEmpty()
					&& (saveProcDocTypePlannedCond1(loDocTypeBean))
					|| HHSConstants.TRUE.equalsIgnoreCase(loDocTypeBean.getIsAddendum()))
			{
				continue;
			}

			if (loDocTypeBean.getDocumentType() == null
					|| loDocTypeBean.getDocumentType().isEmpty()
					&& (null != loDocTypeBean.getDocumentSeqNumber() && !loDocTypeBean.getDocumentSeqNumber().isEmpty()))
			{
				loDelete = (Integer) DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean.getProcurementDocumentId(),
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.DELETE_PROPOSAL_MAPPING_DOC,
						HHSConstants.JAVA_LANG_STRING);

				if (null != loDelete && loDelete >= HHSConstants.INT_ONE)
				{
					DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean.getProcurementDocumentId(),
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.DEL_PROPOSAL_DOC_TYPE,
							HHSConstants.JAVA_LANG_STRING);
				}
			}
			else
			{
				loDocTypeBean.setDocumentSeqNumber(loCounter.toString());
				++loCounter;
				if (loDocTypeBean.getProcurementDocumentId() == null
						|| loDocTypeBean.getProcurementDocumentId().isEmpty())
				{
					loInsert = (Integer) DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.INS_PROPOSAL_DOC_TYPE,
							HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
					if (null != loInsert && loInsert >= HHSConstants.INT_ONE)
					{
						DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.INSERT_PROPOSAL_MAPPING_DOC,
								HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
					}

				}
				else
				{
					loUpdate = (Integer) DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.UPDATE_PROPOSAL_DOC_TYPE,
							HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
					if (null != loUpdate && loUpdate >= HHSConstants.INT_ONE)
					{
						DAOUtil.masterDAO(aoMybatisSession, loDocTypeBean,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.UPDATE_PROPOSAL_MAPPING_DOC,
								HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
					}
				}
			}
		}
	}

	/**
	 * This method updates procurement status to released
	 * 
	 * <ul>
	 * <li>It will check if Input Procurement status is planned and document
	 * count exists for procurement, evaluation criteria and competition pool
	 * has been configured</li>
	 * <li>If it does, add RFP documents for the procurement by using query
	 * updateProcurementData</li>
	 * <li>If it does, update procurement status to released by executing query
	 * with id "updateProcurementStatus"</li>
	 * <li>for some if condition Execute query insertRfpDocumentData</li>
	 * <li>for other if condition Execute query updateProcurementStatus</li>
	 * </ul>
	 * <ul>
	 * <li>updateProcurementData</li>
	 * </ul>
	 * 
	 * Change: Check added for minimum one competition pool configuration
	 * Changed By:Varun Aggarwal Dated:17 Jan 2014
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param asProcStatus a string value of procurement status
	 * @param aoRFPReleaseBean a rfp release bean object containing document
	 *            count
	 * @param aoValidateStatus boolean flag indicating epin is associated with
	 *            procurement
	 * @param aoCofFlag boolean flag indicating COF is approved for given
	 *            procurement
	 * @param aoProcurementMap a map of services with evidence flag unchecked
	 *            and associated with procurements
	 * @return loProcurementStatus indicating rfp is released successfully
	 * @throws ApplicationException If an ApplicationException occurs Updated
	 * 
	 */
	public Boolean updateProcurementDataWithRelease(SqlSession aoMybatisSession, String asProcStatus,
			RFPReleaseBean aoRFPReleaseBean, Boolean aoValidateStatus, Boolean aoCofFlag,
			Map<String, String> aoProcurementMap) throws ApplicationException
	{
		Boolean loProcurementStatus = Boolean.FALSE;
		String lsDocSubmittedStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.DOCUMENT_SUBMITTED);
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AB_VALIDATE_STATUS, aoValidateStatus);
		loContextDataMap.put(HHSConstants.AS_PROC_STATUS, asProcStatus);
		loContextDataMap.put(HHSConstants.AB_COF_FLAG, aoCofFlag);
		loContextDataMap.put(HHSConstants.AO_PROC_MAP, aoProcurementMap);
		LOG_OBJECT.Debug("Entered into updating procurement status to released:" + loContextDataMap.toString());
		try
		{
			if (aoValidateStatus
					&& aoCofFlag
					&& null != asProcStatus
					&& asProcStatus.equalsIgnoreCase(PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_PLANNED))
					&& null != aoRFPReleaseBean && aoRFPReleaseBean.getReqDocCount() != HHSConstants.INT_ZERO
					&& aoRFPReleaseBean.getReqDocTypeCount() != HHSConstants.INT_ZERO
					&& aoRFPReleaseBean.getEvaluationCriteriaCount() != HHSConstants.INT_ZERO
					&& aoRFPReleaseBean.getCompetitionPoolCount() != HHSConstants.INT_ZERO
					&& aoRFPReleaseBean.getDuplicatePoolCount() == HHSConstants.INT_ZERO)
			{
				aoProcurementMap.put(HHSConstants.PROC_STATUS_CODE, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_RELEASED));
				Integer liCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoProcurementMap,
						HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.UPDATE_PROC_DATA,
						HHSConstants.JAVA_UTIL_MAP);
				aoProcurementMap.put(HHSConstants.DOC_SUBMITTED_STATUS, lsDocSubmittedStatus);
				DAOUtil.masterDAO(aoMybatisSession, aoProcurementMap, HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER,
						HHSConstants.INS_RFP_DOC_DATA, HHSConstants.JAVA_UTIL_HASH_MAP);
				if (liCount != null && liCount < HHSConstants.INT_ONE)
				{
					DAOUtil.masterDAO(aoMybatisSession, aoProcurementMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
							HHSConstants.UPDATE_PROC_STATUS, HHSConstants.JAVA_UTIL_MAP);
					loProcurementStatus = Boolean.TRUE;
				}
				loProcurementStatus = Boolean.TRUE;
				setMoState("Procurement Status updated to released for procurement Id:");
			}
		}

		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loExp)
		{
			setMoState("Error while updating Procurement Status to released for procurement Id:");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while updating Procurement Status to released for procurement Id:", loExp);
			throw loExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Error while updating Procurement Status to released for procurement Id:", loAppEx);
			setMoState("Error while updating Procurement Status to released for procurement Id:");
			throw new ApplicationException("Error occured while getting form data from Taxonomy Maser", loAppEx);
		}
		return loProcurementStatus;
	}

	/**
	 * This method is used to get the document id list from document bean list
	 * @param aoDocumentBeanList document bean list
	 * @return list of document ids
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public List<String> fetchRfpReleaseDocIdsList(List<ExtendedDocument> aoDocumentBeanList)
			throws ApplicationException
	{
		List<String> loDcoumentIdList = new ArrayList<String>();
		try
		{
			if (null != aoDocumentBeanList && !aoDocumentBeanList.isEmpty())
			{
				for (Iterator<ExtendedDocument> loDocumentItr = aoDocumentBeanList.iterator(); loDocumentItr.hasNext();)
				{
					ExtendedDocument loDocumentBean = (ExtendedDocument) loDocumentItr.next();
					if (null != loDocumentBean.getDocumentId() && !loDocumentBean.getDocumentId().isEmpty())
					{
						loDcoumentIdList.add(loDocumentBean.getDocumentId());
					}
				}
			}
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching document ids from document bean ", aoAppEx);
			throw new ApplicationException("Error while fetching document ids from document bean ", aoAppEx);
		}
		return loDcoumentIdList;
	}

	/**
	 * This method will add all the properties values from filenet in the
	 * document list
	 * 
	 * @param aoDocumentPropHM document properties map from filenet
	 * @param aoRFPDocumentList extended document bean list with all values
	 * @return list of extended document bean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public List<ExtendedDocument> consolidateAllDocsProperties(HashMap<String, Object> aoDocumentPropHM,
			List<ExtendedDocument> aoRFPDocumentList) throws ApplicationException
	{
		String lsDocumentId = null;
		ExtendedDocument loExtendedDocument = null;
		HashMap<String, Object> loDocPropsBean = null;
		try
		{
			if (null != aoRFPDocumentList && aoRFPDocumentList.size() > HHSConstants.INT_ZERO)
			{
				for (Iterator<ExtendedDocument> loDocIterator = aoRFPDocumentList.iterator(); loDocIterator.hasNext();)
				{
					loExtendedDocument = (ExtendedDocument) loDocIterator.next();
					lsDocumentId = loExtendedDocument.getDocumentId();
					if (null != lsDocumentId && null != aoDocumentPropHM)
					{
						loDocPropsBean = (HashMap<String, Object>) aoDocumentPropHM.get(lsDocumentId);
						if (null != loDocPropsBean && loDocPropsBean.size() > HHSConstants.INT_ZERO)
						{
							loExtendedDocument.setDocumentTitle((String) loDocPropsBean
									.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
							loExtendedDocument.setLastModifiedById((String) loDocPropsBean
									.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID));
							loExtendedDocument.setLastModifiedByName((String) loDocPropsBean
									.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY));
							loExtendedDocument.setDocumentCreatedByUser((String) loDocPropsBean
									.get(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY_ID));
							loExtendedDocument.setModifiedDate(DateUtil.getDateMMDDYYYYFormat((Date) loDocPropsBean
									.get(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE)));
							//Added for Release 5- to set docType
							if(null != loDocPropsBean.get(HHSConstants.DOC_TYPE) && !loDocPropsBean.get(HHSConstants.DOC_TYPE).toString().isEmpty() ){
								loExtendedDocument.setDocumentType((String)loDocPropsBean.get(HHSConstants.DOC_TYPE));
							}
							//Release 5 end
							
						}
					}
				}
			}
		}
		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching document ids from document bean", aoAppEx);
			setMoState("Error while fetching document ids from document bean");
			throw aoAppEx;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while fetching document ids from document bean ", aoEx);
			throw new ApplicationException("Error while fetching document ids from document bean ", aoEx);
		}
		return aoRFPDocumentList;
	}

	/**
	 * This method is added as a part of Release 2.6.0 for defect 5571 This
	 * method sorts documents with Document type others in alphabetical order
	 * 
	 * <ul>
	 * <li>1. Iterate over document list.</li>
	 * <li>2. Segregate document list into two different list a. One having do
	 * type "RFP" and "Addenda" b. Other having doc type not in "RFP" and
	 * "Addenda"</li>
	 * <li>3. Sort the second list .</li>
	 * </ul>
	 * 
	 * @param aoRFPDocumentList - ExtendedDocument list
	 * @return loSortedList - sorted list
	 * @throws ApplicationException - throws ApplicationException
	 */
	public List<ExtendedDocument> sortOtherDocTypeDocumentList(List<ExtendedDocument> aoRFPDocumentList)
			throws ApplicationException, java.text.ParseException
	{
		List<ExtendedDocument> loRfpDocTypeList = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loAddendaDocTypeList = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loOtherDocTypeList = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loSortedList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtendedDocument = null;
		try
		{
			LOG_OBJECT.Debug("Executing sortOtherDocTypeDocumentList method start ::");
			for (Iterator<ExtendedDocument> loDocIterator = aoRFPDocumentList.iterator(); loDocIterator.hasNext();)
			{
				loExtendedDocument = (ExtendedDocument) loDocIterator.next();
				String lsDocType = loExtendedDocument.getDocumentType();
				if (null != lsDocType && lsDocType.equalsIgnoreCase(HHSConstants.RFP))
				{
					loRfpDocTypeList.add(loExtendedDocument);
				}
				else if (null != lsDocType && lsDocType.equalsIgnoreCase(HHSConstants.ADDENDA))
				{
					loAddendaDocTypeList.add(loExtendedDocument);
				}
				else
				{
					loOtherDocTypeList.add(loExtendedDocument);
				}
			}
			LOG_OBJECT.Debug("Sorting Addenda DocType documents by modified date ..");
			if (null != loAddendaDocTypeList && !loAddendaDocTypeList.isEmpty())
			{
				Collections.sort(loAddendaDocTypeList, new Comparator<ExtendedDocument>()
				{
					public int compare(ExtendedDocument c1, ExtendedDocument c2)
					{
						Date aoModifiedDate1 = HHSUtil.ConvertStringToDate(c1.getModifiedDate());
						Date aoModifiedDate2 = HHSUtil.ConvertStringToDate(c2.getModifiedDate());
						int liResult = aoModifiedDate1.compareTo(aoModifiedDate2);
						if (liResult != 0 && liResult > 0)
						{
							liResult = -1;
						}
						else if (liResult != 0 && liResult < 0)
						{
							liResult = 1;
						}
						if (liResult == 0)
						{
							liResult = c1.getReferenceDocSeqNo().compareTo(c2.getReferenceDocSeqNo());
							if (liResult != 0 && liResult > 0)
							{
								liResult = -1;
							}
							else if (liResult != 0 && liResult < 0)
							{
								liResult = 1;
							}
						}
						return liResult;
					}
				});
			}
			LOG_OBJECT.Debug("Sorted list of  Addenda DocType documents :: end ");
			LOG_OBJECT.Debug("Sorting Other DocType documents by document name start ..");
			if (null != loOtherDocTypeList && !loOtherDocTypeList.isEmpty())
			{
				Collections.sort(loOtherDocTypeList, new Comparator<ExtendedDocument>()
				{
					public int compare(ExtendedDocument c1, ExtendedDocument c2)
					{
						return c1.getDocumentTitle().toLowerCase().compareTo(c2.getDocumentTitle().toLowerCase());
					}

				});
			}
			LOG_OBJECT.Debug("Sorted list of  Other DocType documents :: end");
			// If Rfp and addenda documents are present add them first into the
			// list to maintain sort order
			// RFP, Addenda and then Others.
			if (null != loRfpDocTypeList && !loRfpDocTypeList.isEmpty())
			{
				loSortedList.addAll(loRfpDocTypeList);
			}
			if (null != loAddendaDocTypeList && !loAddendaDocTypeList.isEmpty())
			{
				loSortedList.addAll(loAddendaDocTypeList);
			}
			if (null != loOtherDocTypeList && !loOtherDocTypeList.isEmpty())
			{
				loSortedList.addAll(loOtherDocTypeList);
			}
			LOG_OBJECT.Debug("Final sorted list ::" + loSortedList);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while sorting document list ", aoEx);
			throw new ApplicationException("Error while sorting document list ", aoEx);
		}
		LOG_OBJECT.Error("Executing sortOtherDocTypeDocumentList method end ::");
		return loSortedList;
	}

	/**
	 * This method will fetch procurement title list against org list
	 * <ul>
	 * <li>Fetch procurement title list against org list by executing
	 * fetchProcTitleAndOrgList
	 * </ul>
	 * </li>
	 * <ul>
	 * <li>Method updated in R4</li>
	 * </ul>
	 * @param aoProcMap aoProcMap
	 * @param aoMybatisSession aoMybatisSession
	 * @param aoNotificationMap aoNotificationMap
	 * @return aoNotificationMap
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> fetchProcTitleAndOrgList(SqlSession aoMybatisSession,
			HashMap<String, String> aoProcMap, HashMap<String, Object> aoNotificationMap) throws ApplicationException
	{
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AO_NOTIFICATION_MAP, aoNotificationMap);
		LOG_OBJECT
				.Info("Entered into fetching Approved Providers list for notification:" + loContextDataMap.toString());

		try
		{
			List<ProcurementInfo> loProviderList = (List<ProcurementInfo>) DAOUtil.masterDAO(aoMybatisSession,
					aoProcMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
					HHSConstants.FETCH_PROC_TITLE_AND_ORG_LIST, HHSConstants.JAVA_UTIL_HASH_MAP);
			if (null != loProviderList && loProviderList.size() > HHSConstants.INT_ZERO)
			{
				List<String> loOrgIdList = new ArrayList<String>();
				for (ProcurementInfo loProcurementInfo : loProviderList)
				{
					loOrgIdList.add(loProcurementInfo.getOrgId());
				}
				List<String> loNotificationList = (List<String>) aoNotificationMap
						.get(HHSConstants.NOTIFICATION_ALERT_ID);
				for (String loNotificationId : loNotificationList)
				{
					((NotificationDataBean) aoNotificationMap.get(loNotificationId)).setProviderList(loOrgIdList);
				}
				HashMap<String, String> loRequestMap = (HashMap<String, String>) aoNotificationMap
						.get(TransactionConstants.REQUEST_MAP_PARAMETER_NAME);
				loRequestMap.put(HHSConstants.PROC_TITLE, loProviderList.get(HHSConstants.INT_ZERO)
						.getProcurementTitle());
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while fetching Approved Providers list for notification:");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error occurred while fetching procurement title for Procurement Id:", loExp);
			throw loExp;
		}
		catch (Exception loEx)
		{
			setMoState("Error occurred while fetching Approved Providers list for notification:");
			LOG_OBJECT.Error("Error occurred while fetching procurement title for Procurement Id:", loEx);
			throw new ApplicationException(
					"Exception occured while getting data from fetchProposalDocumentType in fetchProposalConfigurationDetails ",
					loEx);
		}
		return aoNotificationMap;
	}

	/**
	 * This method is used to get the document id list from document bean list 1
	 * and 2 by itertaing through these lists
	 * 
	 * @param aoDocumentBeanList1 document bean list 1
	 * @param aoDocumentBeanList2 document bean list 2
	 * 
	 * @return list of document ids
	 * @throws ApplicationException If any Exception occurs
	 */
	public List<String> fetchDocumentIdsList(List<ExtendedDocument> aoDocumentBeanList1,
			List<ExtendedDocument> aoDocumentBeanList2) throws ApplicationException
	{
		List<String> loDocumentIdList = new ArrayList<String>();
		try
		{
			if (null != aoDocumentBeanList1)
			{
				// iterate through first document list and get document ids
				for (Iterator<ExtendedDocument> loDocumentItr = aoDocumentBeanList1.iterator(); loDocumentItr.hasNext();)
				{
					ExtendedDocument loDocumentBean = (ExtendedDocument) loDocumentItr.next();
					if (null != loDocumentBean.getDocumentId() && !loDocumentBean.getDocumentId().isEmpty())
					{
						loDocumentIdList.add(loDocumentBean.getDocumentId());
					}
				}
			}
			if (null != aoDocumentBeanList2)
			{
				// iterate through second document list and get document ids
				for (Iterator<ExtendedDocument> loDocumentItr = aoDocumentBeanList2.iterator(); loDocumentItr.hasNext();)
				{
					ExtendedDocument loDocumentBean = (ExtendedDocument) loDocumentItr.next();
					if (null != loDocumentBean.getDocumentId() && !loDocumentBean.getDocumentId().isEmpty())
					{
						loDocumentIdList.add(loDocumentBean.getDocumentId());
					}
				}
			}
		}

		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while fetching document ids from document bean ", aoEx);
			throw new ApplicationException("Error while fetching document ids from document bean ", aoEx);
		}
		return loDocumentIdList;
	}

	/**
	 * This method used to check whether or not the addendum data exist for a
	 * particular procurement.
	 * 
	 * <ul>
	 * <li>Execute the query fetchProcurementAddendumData to check the existance
	 * of procurement data.</li>
	 * <li>Return the result of the query.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param asProcurementId - Procurement Id
	 * @return - loProcurementAddendumCount
	 * @throws ApplicationException - throws ApplicationException
	 */
	public Integer fetchProcurementAddendumData(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		Integer loProcurementAddendumCount = null;
		try
		{
			loProcurementAddendumCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_PROC_ADDENDUM_DATA,
					HHSConstants.JAVA_LANG_STRING);
		}

		// Catch application exception thrown from the code
		// while getting procurement addendum data from database
		// and throw it forward
		catch (ApplicationException aoEx)
		{
			setMoState("Error while fetching procurement Addendum data");
			aoEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching procurement Addendum data", aoEx);
			throw aoEx;
		}
		catch (Exception loEx)
		{
			setMoState("Error while fetching procurement Addendum data");
			LOG_OBJECT.Error("Error while fetching procurement Addendum data", loEx);
			throw new ApplicationException("Error while fetching procurement Addendum data", loEx);
		}
		return loProcurementAddendumCount;
	}

	/**
	 * Changes made for enhancement 6448 for Release 3.8.0 This method will
	 * update procurement status to closed
	 * <ul>
	 * <li>1.Connect to database using SqlSession</li>
	 * <li>Execute the query updateProcurementStatus.</li>
	 * <li>2.Set the Status Id [Closed] in Procurement</li>
	 * <li>3. If aoAwardReviewStatusFlag true , and if all the awards/contracts
	 * are either Closed, Canceled or Registered, Update Procurement Status as
	 * Closed</li>
	 * <li>If any exception is thrown it will be handled here in the catch block
	 * with an error messege and it will be thrown again.</li>
	 * </ul>
	 * <ul>
	 * <li>method updated in R4</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param aoStatusMap Map
	 * @return Boolean loCloseProcurementStatus
	 * @throws ApplicationException if any exception occured
	 */
	@SuppressWarnings("rawtypes")
	public Boolean closeProcurement(SqlSession aoMybatisSession, Boolean aoTaskAvailable,
	/* Boolean aoAwardReviewStatusFlag, */Map aoStatusMap) throws ApplicationException
	{
		Boolean loCloseProcurementStatus = Boolean.FALSE;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AO_STATUS_MAP, aoStatusMap);
		LOG_OBJECT.Debug("Entered into updating procurement status to closed::" + loContextDataMap.toString());
		try
		{
			// changes done for enhancement 6448 for Release 3.8.0
			if (!aoTaskAvailable)
			{
				DAOUtil.masterDAO(aoMybatisSession, aoStatusMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.UPDATE_PROC_STATUS, HHSConstants.JAVA_UTIL_MAP);
				loCloseProcurementStatus = true;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoEx)
		{
			setMoState("Error while closing the procurement");
			aoEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while closing the procurement:", aoEx);
			throw aoEx;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while closing the procurement:", aoEx);
			setMoState("Error while closing the procurement");
			throw new ApplicationException("Error while closing the procurement ", aoEx);
		}
		return loCloseProcurementStatus;
	}

	/**
	 * This method updating procurement last modified map
	 * <ul>
	 * <li>1.Create the map to hold the context data</li>
	 * <li>2.Set Procurement id in the map</li>
	 * <li>3. Set the procurement id in the input map</li>
	 * <li>4. Return the map</li>
	 * </ul>
	 * 
	 * @param aoLastModifiedHashMap aoLastModifiedHashMap
	 * @param asProcurementId asProcurementId
	 * @return aoLastModifiedHashMap
	 * @throws ApplicationException if any exception occurred
	 */
	public Map<Object, Object> updateLastModifiedMap(Map<Object, Object> aoLastModifiedHashMap, String asProcurementId)
			throws ApplicationException
	{
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		LOG_OBJECT.Debug("Entered into updating procurement last modified map" + loContextDataMap.toString());
		try
		{
			aoLastModifiedHashMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		}
		// Catch any exception thrown from the code while setting the
		// procurement id
		// into the input map
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while updating procurement last modified map ", aoEx);
			setMoState("Error while updating procurement last modified map ");
			throw new ApplicationException("Error while updating procurement last modified map ", aoEx);
		}
		return aoLastModifiedHashMap;
	}

	/**
	 * This method will delete the data from procurement provider table based
	 * upon the procurement id what we paased as a parameter
	 * <ul>
	 * <li>1.Create the map to hold the context data</li>
	 * <li>2.Set Procurement id in the map</li>
	 * <li>3. Run the query with the id "deleteProcurementProviderData"
	 * <li>Return the result and update the value of loDeleteStatus</li>
	 * </ul>
	 */
	/**
	 * @param aoMybatisSession session
	 * @param aoUpdateProcurementStatus update proc status
	 * @param asProcurementId procurement id
	 * @return delete status
	 * @throws ApplicationException if application occurs
	 */
	public Boolean deleteProcurementProviderData(SqlSession aoMybatisSession, Boolean aoUpdateProcurementStatus,
			String asProcurementId) throws ApplicationException
	{
		Boolean loDeleteStatus = Boolean.FALSE;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		LOG_OBJECT.Debug("Entered into updating procurement status to closed::" + loContextDataMap.toString());
		try
		{
			if (aoUpdateProcurementStatus)
			{
				DAOUtil.masterDAO(aoMybatisSession, asProcurementId, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.DELETE_PROCUREMENT_PROVIDER_DATA, HHSConstants.JAVA_LANG_STRING);
				loDeleteStatus = Boolean.TRUE;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoEx)
		{
			setMoState("Error while closing the procurement");
			aoEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while closing the procurement:", aoEx);
			throw aoEx;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while closing the procurement:", aoEx);
			setMoState("Error while closing the procurement");
			throw new ApplicationException("Error while closing the procurement ", aoEx);
		}
		return loDeleteStatus;
	}

	/**
	 * This method will the procurement status id for the given procurement.
	 * <ul>
	 * <li>1.Get the procurement Id</li>
	 * <li>2.Execute query <code>fetchProcurementStatusId</code></li>
	 * <li>Return the procurement status id</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param asProcurementId procurement id
	 * @return loProcStatusId Integer procurement status
	 * @throws ApplicationException if application occurs
	 */
	public Integer fetchProcurementStatusId(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
		Integer loProcStatusId = null;
		try
		{
			loProcStatusId = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_PROCUREMENT_STATUS_ID,
					HHSConstants.JAVA_LANG_STRING);
		}

		// Catch application exception thrown from the code
		// and throw it forward
		catch (ApplicationException aoEx)
		{
			setMoState("Error while fetching procurement status id");
			aoEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching procurement status id", aoEx);
			throw aoEx;
		}
		catch (Exception loEx)
		{
			setMoState("Error while fetching procurement status id");
			LOG_OBJECT.Error("Error while fetching procurement status id", loEx);
			throw new ApplicationException("Error while fetching procurement status id", loEx);
		}
		return loProcStatusId;
	}

	/**
	 * This method gets list of accelerator/agency users list
	 * 
	 * <ul>
	 * <li>1. Create a reference of the hashmap to hold the users detail.</li>
	 * <li>2. Execute the query "getAcceleratorUserList" specified in the
	 * procurementMapper with parameter "city_org".</li>
	 * <li>3. Execute the query "getAcceleratorUserList" specified in the
	 * procurementMapper with parameter "agency_org".</li>
	 * <li>4. Set both the result in a Map.</li>
	 * <li>5. Return the Map.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param aoUserType UserType
	 * @return - loUserMap
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<StaffDetails>> getAcceleratorContactDetails(SqlSession aoMybatisSession,
			Map<String, String> aoUserType) throws ApplicationException
	{
		List<StaffDetails> loAcceleratorUserList = null;
		Map<String, List<StaffDetails>> loUserMap = new HashMap<String, List<StaffDetails>>();
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AO_USER_TYPE, aoUserType);
		loContextDataMap.put(HHSConstants.LO_ACC_USER_LIST, loAcceleratorUserList);
		LOG_OBJECT.Debug("Entered into getting Accelerator users List ::" + loContextDataMap.toString());
		try
		{
			loAcceleratorUserList = (List<StaffDetails>) DAOUtil.masterDAO(aoMybatisSession,
					aoUserType.get(HHSConstants.USER_CITY), HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
					HHSConstants.GET_ACC_USER_LIST, HHSConstants.JAVA_LANG_STRING);
			loUserMap.put(HHSConstants.USER_CITY, loAcceleratorUserList);
			setMoState("Accelerator users List fetched successfully for NYC Agency:");

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting Accelerator/Agency users List :");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting Accelerator/Agency users List :", loExp);
			throw loExp;

		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while getting Accelerator/Agency users List :", loEx);
			setMoState("Error while getting Accelerator/Agency users List :");
			throw new ApplicationException("Error while getting Accelerator/Agency users List :", loEx);
		}
		return loUserMap;
	}

	/**
	 * This method is used to fetch agency contact details Execute query
	 * "getAgencyUserList"
	 * @param aoMybatisSession MybatisSession
	 * @param aoProcurementBean procurement bean
	 * @return loAgencyUserList agency user list
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<StaffDetails> getAgencyContactDetails(SqlSession aoMybatisSession, Procurement aoProcurementBean)
			throws ApplicationException
	{
		List<StaffDetails> loAgencyUserList = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();

		try
		{
			if (aoProcurementBean != null)
			{
				LOG_OBJECT.Debug("Entered into getting Accelerator users List ::" + aoProcurementBean.getAgencyId());
				loAgencyUserList = (List<StaffDetails>) DAOUtil.masterDAO(aoMybatisSession,
						aoProcurementBean.getAgencyId(), HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.GET_AGENCY_USER_LIST, HHSConstants.JAVA_LANG_STRING);
			}

			setMoState("Accelerator users List fetched successfully for NYC Agency:");

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting Accelerator/Agency users List :");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting Accelerator/Agency users List :", loExp);
			throw loExp;

		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while getting Accelerator/Agency users List :", loEx);
			setMoState("Error while getting Accelerator/Agency users List :");
			throw new ApplicationException("Error while getting Accelerator/Agency users List :", loEx);
		}
		return loAgencyUserList;
	}

	/**
	 * This method gets list of program name based on input nyc agency.
	 * 
	 * <ul>
	 * <li>1. Get the agencyId from the procurement bean.</li>
	 * <li>2. execute the query "getProgramNameList" specified in the
	 * procurementMapper.</li>
	 * <li>3. Return the program list.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoProcurementBean - ProcurementBean
	 * @return a list of program names
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<Procurement> getProgramName(SqlSession aoMybatisSession, Procurement aoProcurementBean)
			throws ApplicationException
	{
		List<Procurement> loProgramNameList = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		if (aoProcurementBean != null)
		{
			loContextDataMap.put(HHSConstants.AGENCYID, aoProcurementBean.getAgencyId());
			LOG_OBJECT.Debug("Entered into getting program Name List:" + loContextDataMap.toString());
			try
			{
				loProgramNameList = (List<Procurement>) DAOUtil.masterDAO(aoMybatisSession,
						aoProcurementBean.getAgencyId(), HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.GET_PROG_NAME_LIST, HHSConstants.JAVA_LANG_STRING);
				setMoState("Program Name List fetched successfully for NYC Agency:");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException loExp)
			{
				setMoState("Error while getting program Name List :" + aoProcurementBean);
				loExp.setContextData(loContextDataMap);
				LOG_OBJECT.Error("Error while getting program Name List :", loExp);
				throw loExp;
			}
			// handling exception other than Application Exception.
			catch (Exception loEx)
			{
				setMoState("Error while getting program Name List :" + aoProcurementBean);
				LOG_OBJECT.Error("Error while getting program Name List :", loEx);
				throw new ApplicationException("Error while getting program Name List :", loEx);
			}
		}
		return loProgramNameList;
	}

	/**
	 * This method is used to fetch procurement summary from award task
	 * @param aoMybatisSession MybatisSession
	 * @param aoTaskMap task map
	 * @param asWobNumber wob number
	 * @return loProcurementSummary procurement summary
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public Procurement getProcurementSummaryFromAwardTask(SqlSession aoMybatisSession,
			HashMap<String, Object> aoTaskMap, String asWobNumber) throws ApplicationException
	{
		Procurement loProcurementSummary = null;
		HashMap<String, String> loContextDataMap = new HashMap<String, String>();
		loContextDataMap.put(HHSConstants.WORKFLOW_ID, asWobNumber);
		LOG_OBJECT.Debug("Entered into getProcurementSummaryFromAwardTask:" + loContextDataMap.toString());
		try
		{
			if (null != aoTaskMap)
			{
				HashMap<String, Object> loProcurementMap = (HashMap<String, Object>) aoTaskMap.get(asWobNumber);
				if (null != loProcurementMap)
				{
					String lsProcurementId = (String) loProcurementMap.get(P8Constants.PE_WORKFLOW_PROCUREMENT_ID);
					loProcurementSummary = getProcurementSummary(aoMybatisSession, lsProcurementId, null);
				}
			}
			setMoState("Procurement details fetched successfully for WobNumber :" + asWobNumber);
		}

		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting Procurement Details");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting Procurement Details", loExp);
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while getting Procurement Details", loEx);
			setMoState("Error while getting Procurement Details");
			throw new ApplicationException("Error while getting Procurement Details", loEx);
		}
		return loProcurementSummary;
	}

	/**
	 * This method is used to fetch service unit flag Execute query
	 * "fetchServiceUnitFlag"
	 * @param aoMybatisSession MybatisSession
	 * @param asProcurementId procurement id
	 * @return lsServiceUnit service unit flag
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public String fetchServiceUnitFlag(SqlSession aoMybatisSession, String asProcurementId) throws ApplicationException
	{
		String lsServiceUnit = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		LOG_OBJECT.Debug("Entered into fetchServiceUnitFlag for Procurement Id:" + asProcurementId);
		try
		{
			lsServiceUnit = (String) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_SERVICE_UNIT_FLAG,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Service Units fetched successfully for Procurement Id:" + asProcurementId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting service units for Procurement Id:" + asProcurementId);
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting service units for Procurement Id:", loExp);
			throw loExp;
		}
		catch (Exception loEx)
		{
			setMoState("Error while getting service units for Procurement Id:" + asProcurementId);
			LOG_OBJECT.Error("Error while getting service units for Procurement Id:", loEx);
			throw new ApplicationException("Error while getting service units for Procurement Id:", loEx);
		}
		return lsServiceUnit;
	}

	/**
	 * This method fetches procurement title based on input procurement Id Added
	 * as part of Release 3.6.0 for defect#6498
	 * 
	 * <ul>
	 * <li>Pass procurement Id to DAO layer</li>
	 * <li>Execute query with Id "fetchProcurementTitle" from procurement mapper
	 * </li>
	 * <li>Return output to controller</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Sql Session
	 * @param asProcurementId a string value of procurement Id
	 * @return a string containing procurement title
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public String fetchProcurementTitle(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		String loProcurementTitle;
		try
		{
			loProcurementTitle = (String) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_PROCUREMENT_TITLE,
					HHSConstants.JAVA_LANG_STRING);

			setMoState("Procurement Title fetched successfully for Procurement Id:" + asProcurementId);
		}

		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while fetching procurement title for Procurement Id:" + asProcurementId);
			LOG_OBJECT.Error("Error occurred while fetching procurement title for Procurement Id:", loExp);
			throw loExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Error occurred while fetching procurement title for Procurement Id:" + asProcurementId);
			LOG_OBJECT.Error("Error occurred while fetching procurement title for Procurement Id:", loAppEx);
			throw new ApplicationException("Error occured while fetching procurement title for Procurement Id", loAppEx);

		}
		return loProcurementTitle;
	}

	// Release 5 change starts
	/**
	 * This method added as a part of release 5 for Enhanced Evaluation Module
	 * 
	 * <ul>
	 * This method update the column : PCOF_PSR_VERSION_NUMBER of Procurement
	 * table
	 * </ul>
	 * @param aoMybatisSession SqlSession
	 * @param asProcurementId String
	 * @return lbUpdateStatus boolean
	 * @throws ApplicationException
	 */
	public Boolean updatePcofPsrVersionNumber(SqlSession aoMybatisSession, String asProcurementId)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entering updatePcofPsrVersionNumber");
		Boolean loUpdateStatus = Boolean.FALSE;
		try
		{
			Integer loUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProcurementId,
					HHSR5Constants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSR5Constants.UPDATE_PCOF_PSR_VERSION_NUMBER,
					HHSR5Constants.JAVA_LANG_STRING);
			if (loUpdateCount > HHSR5Constants.INT_ZERO)
			{
				loUpdateStatus = Boolean.TRUE;
			}
		}

		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while updatePcofPsrVersionNumber");
			LOG_OBJECT.Error("Error occurred updatePcofPsrVersionNumber", loExp);
			throw loExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Error occurred while updatePcofPsrVersionNumber");
			LOG_OBJECT.Error("Error occurred while updatePcofPsrVersionNumber", loAppEx);
			throw new ApplicationException("Error occurred while updatePcofPsrVersionNumber", loAppEx);

		}
		return loUpdateStatus;
	}

	/**
	 * This method is part of Release 5 PSR Module.
	 * 
	 * <ul>
	 * <li>Pass procurement Id to DAO layer</li>
	 * <li>Execute query with Id "fetchProcCertOfFunds" from RFP Release Mapper</li>
	 * <li>Return output</li>
	 * </ul>
	 * @param aoMyBatisSession SqlSession
	 * @param asProcurementId String
	 * @param aoProcurementBean Procurement Bean
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean validatePCOFTaskApproved(SqlSession aoMyBatisSession, String asProcurementId,
			Procurement aoProcurementBean) throws ApplicationException
	{
		LOG_OBJECT.Info("Entering validatePSRTasksApproved");
		Boolean loApproveFlag = Boolean.FALSE;
		try
		{
			if (aoProcurementBean.getIsOpenEndedRFP().equals("0") && aoProcurementBean.getEstProcurementValue().compareTo(new BigDecimal(0)) != 0)
			{
				Integer loCertOfFundsStatus = (Integer) DAOUtil.masterDAO(aoMyBatisSession, asProcurementId,
						HHSConstants.MAPPER_CLASS_RFP_RELEASE_MAPPER, HHSConstants.FETCH_PROC_CERT_OF_FUNDS,
						HHSConstants.JAVA_LANG_STRING);
				setMoState("Fetched COF count successfully");
				if (loCertOfFundsStatus > HHSConstants.INT_ZERO)
				{
					loApproveFlag = true;
				}
			}
			else{
				loApproveFlag = true;
			}
		}
		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while validatePCOFTaskApproved");
			LOG_OBJECT.Error("Error occurred validatePCOFTaskApproved", loExp);
			throw loExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Error occurred while validatePCOFTaskApproved");
			LOG_OBJECT.Error("Error occurred while validatePCOFTaskApproved", loAppEx);
			throw new ApplicationException("Error occurred while validatePCOFTaskApproved", loAppEx);

		}
		return loApproveFlag;
	}

	/**
	 * This method is part of Release 5 PSR Module.
	 * 
	 * <ul>
	 * <li>Pass procurement Id to DAO layer</li>
	 * <li>Execute query with Id "fetchPSRApprovedCount" from Procurement Mapper
	 * </li>
	 * <li>Return output flag</li>
	 * </ul>
	 * @param aoMyBatisSession SqlSession
	 * @param asProcurementId String
	 * @return loApproveFlag Boolean
	 * @throws ApplicationException
	 */
	public Boolean validatePSRTaskApproved(SqlSession aoMyBatisSession, String asProcurementId)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entering validatePSRTasksApproved");
		Boolean loApproveFlag = Boolean.FALSE;
		try
		{
			Integer loPSRCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSR5Constants.FETCH_PSR_APPROVED_COUNT,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Fetched PSR count successfully");
			if (loPSRCount > HHSConstants.INT_ZERO)
			{
				loApproveFlag = true;
			}
		}
		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while validatePSRTaskApproved");
			LOG_OBJECT.Error("Error occurred validatePSRTaskApproved", loExp);
			throw loExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Error occurred while validatePSRTaskApproved");
			LOG_OBJECT.Error("Error occurred while validatePSRTaskApproved", loAppEx);
			throw new ApplicationException("Error occurred while validatePSRTaskApproved", loAppEx);

		}
		return loApproveFlag;
	}

	/**
	 * This method is part of Release 5 PSR Module.
	 * 
	 * <ul>
	 * <li>Pass procurement Id to DAO layer</li>
	 * <li>Execute query with Id "resetGeneratePDFFlag" from Procurement Mapper</li>
	 * <li>Set Colomn GENERATE_NEW_TASK equals zero in Procurment</li>
	 * <li>Return output flag</li>
	 * </ul>
	 * @param aoMyBatisSession
	 * @param aoHMWFRequiredProps
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean resetPSRTaskFlag(SqlSession aoMyBatisSession, HashMap aoHMWFRequiredProps)
			throws ApplicationException
	{
		try
		{
			String lsProcurementId = (String) aoHMWFRequiredProps.get(HHSConstants.PROCUREMENT_ID);
			DAOUtil.masterDAO(aoMyBatisSession, lsProcurementId, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
					HHSR5Constants.RESET_GENERATE_PDF_FLAG, HHSConstants.JAVA_LANG_STRING);
		} // catch any application exception thrown from the code and throw it
			// forward
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while resetPSRTaskFlag");
			LOG_OBJECT.Error("Error occurred resetPSRTaskFlag", loExp);
			throw loExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Error occurred while resetPSRTaskFlag");
			LOG_OBJECT.Error("Error occurred while resetPSRTaskFlag", loAppEx);
			throw new ApplicationException("Error occurred while resetPSRTaskFlag", loAppEx);

		}
		return true;
	}

	/**
	 * This method is part of Release 5 PSR Module.
	 * 
	 * <ul>
	 * <li>Pass procurement Id to DAO layer</li>
	 * <li>Execute query with Id "updatePCOFContractDates" from Procurement Mapper</li>
	 * <li>Return output flag</li>
	 * </ul>
	 * @param aoMyBatisSession
	 * @param asProcurementId
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean updatePCOFContractDates(SqlSession aoMyBatisSession, String asProcurementId)
			throws ApplicationException
	{
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, asProcurementId, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
					HHSR5Constants.UPDATE_PCOF_CONTRACT_DATES, HHSConstants.JAVA_LANG_STRING);
		} // catch any application exception thrown from the code and throw it
			// forward
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while updatePCOFContractDates");
			LOG_OBJECT.Error("Error occurred updatePCOFContractDates", loExp);
			throw loExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Error occurred while updatePCOFContractDates");
			LOG_OBJECT.Error("Error occurred while updatePCOFContractDates", loAppEx);
			throw new ApplicationException("Error occurred while updatePCOFContractDates", loAppEx);

		}
		return true;
	}

	/**
	 * This method is part of Release 5 PSR Module.
	 * 
	 * <ul>
	 * <li>Check if Task is not finished</li>
	 * <li>If TaskCount is greater than zero ,
	 * execute "ProcurementService.updatePCOFContractDates()."
	 * <li>Return output</li>
	 * </ul>
	 * @param aoFilenetSession - Filenet Session
	 * @param aoMyBatisSession - Sql Session
	 * @param asProcurementId - String Procurement Id
	 * @return Boolean Output flag
	 * @throws ApplicationException
	 */
	public Boolean updatePCOFContractDatesIfLaunched(P8UserSession aoFilenetSession, SqlSession aoMyBatisSession,
			String asProcurementId) throws ApplicationException
	{
		try
		{
			Integer loOpenTaskCount = 0;
			HashMap loHmWFProperties = new HashMap();
			loHmWFProperties.put(HHSConstants.PROPERTY_PE_PROCURMENT_ID, asProcurementId);
			loHmWFProperties.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.TASK_PROCUREMENT_COF);
			loOpenTaskCount = loOpenTaskCount
					+ new P8ProcessServiceForSolicitationFinancials().getOpenTaskCount(aoFilenetSession,
							loHmWFProperties);
			if (loOpenTaskCount > 0)
			{
				updatePCOFContractDates(aoMyBatisSession, asProcurementId);
			}
		} // catch any application exception thrown from the code and throw it
			// forward
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while updatePCOFContractDatesIfLaunched");
			LOG_OBJECT.Error("Error occurred updatePCOFContractDatesIfLaunched", loExp);
			throw loExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Error occurred while updatePCOFContractDatesIfLaunched");
			LOG_OBJECT.Error("Error occurred while updatePCOFContractDatesIfLaunched", loAppEx);
			throw new ApplicationException("Error occurred while updatePCOFContractDatesIfLaunched", loAppEx);

		}
		return true;
	}

	/**
	 * This method is used to get Procurement details.
	 * <ul>
	 * <li>Set ProcurementId to Dao layer</li>
	 * <li>Execute "validateProcurementPSRPCOF" from procurement mapper.
	 * <li>Return output</li>
	 * </ul>
	 * @param aoMyBatisSession -  Sql session
	 * @param asProcurementId - procurementId
	 * @return - Procurment Details
	 * @throws ApplicationException
	 */
	public Procurement getProcurementDetails(SqlSession aoMyBatisSession, String asProcurementId)
			throws ApplicationException
	{
		Procurement loProcurementDetails = null;
		try
		{
			loProcurementDetails = (Procurement) DAOUtil.masterDAO(aoMyBatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSR5Constants.VALIDATE_PROCUREMENT_AMOUNT_PCOF,
					HHSConstants.JAVA_LANG_STRING);
		} // catch any application exception thrown from the code and throw it
			// forward
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while getProcurementDetails");
			LOG_OBJECT.Error("Error occurred getProcurementDetails", loExp);
			throw loExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Error occurred while getProcurementDetails");
			LOG_OBJECT.Error("Error occurred while getProcurementDetails", loAppEx);
			throw new ApplicationException("Error occurred while getProcurementDetails", loAppEx);

		}
		return loProcurementDetails;
	}
	// R5 changes ends
	
	// Start QC 9401 R 5.0 Generate RFP Procurement Report
	/**
	 * This method is used to get Procurement Id for Epin.
	 * <ul>
	 * <li>Set ProcurementId to Dao layer</li>
	 * <li>Execute "getProcurementForEpin" from procurement mapper.
	 * <li>Return output</li>
	 * </ul>
	 * @param aoMyBatisSession -  Sql session
	 * @param asEpinId - epinId
	 * @return - Procurment Details
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<EPinDetailBean> getProcurementForEpin(SqlSession aoMyBatisSession, String asEpinId)
			throws ApplicationException
	{
		List<EPinDetailBean> loProcurementList = null;
		try
		{
			loProcurementList = (List<EPinDetailBean>) DAOUtil.masterDAO(aoMyBatisSession, asEpinId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSR5Constants.GET_PROCUREMENT_FOR_EPIN,
					HHSConstants.JAVA_LANG_STRING);
		} // catch any application exception thrown from the code and throw it
			// forward
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while getProcurementForEpin");
			LOG_OBJECT.Error("Error occurred getProcurementForEpin", loExp);
			throw loExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Error occurred while getProcurementDetails");
			LOG_OBJECT.Error("Error occurred while getProcurementDetails", loAppEx);
			throw new ApplicationException("Error occurred while getProcurementDetails", loAppEx);

		}
		return loProcurementList;
	}
	
	/**
	 * This method is used to generarate RFP Reort Data for  Procurement Id.
	 * <ul>
	 * <li>Set ProcurementId to Dao layer</li>
	 * <li>Execute "getRfpReportData" from procurement mapper.
	 * <li>Return output</li>
	 * </ul>
	 * @param aoMyBatisSession -  Sql session
	 * @param asProcurementId - procurementId
	 * @return - Procurment Details
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<ProposalReportBean> getRfpReportData(SqlSession aoMyBatisSession, String asProcurementId)
			throws ApplicationException
	{
		List<ProposalReportBean> loProcurementList = null;
		try
		{
			loProcurementList = (List<ProposalReportBean>) DAOUtil.masterDAO(aoMyBatisSession, asProcurementId,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSR5Constants.GET_RFP_REPORT_DATA,
					HHSConstants.JAVA_LANG_STRING);
		} // catch any application exception thrown from the code and throw it
			// forward
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while getRfpReportData");
			LOG_OBJECT.Error("Error occurred getRfpReportData", loExp);
			throw loExp;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			setMoState("Error occurred while getRfpReportData");
			LOG_OBJECT.Error("Error occurred while getRfpReportData", loAppEx);
			throw new ApplicationException("Error occurred while getRfpReportData", loAppEx);

		}
		return loProcurementList;
	}
	// Start QC 9401 R 5.0 Generate RFP Procurement Report
}