package com.nyc.hhs.daomanager.service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Date;


import org.apache.ibatis.session.SqlSession;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Marshaller;
import org.jdom.Document;
import org.jdom.input.SAXHandler;

import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.UpdatingBatch;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.Properties;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Id;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.BudgetDetails;
import com.nyc.hhs.model.BudgetXMLRegenBean;
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
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8ContentOperations;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8ProcessOperations;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8services.P8ContentService;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

public class BudgetAmdModXMLRegenService  extends ServiceState{

	/**
	 * Logger object for ContractBudgetAmendmentService class.
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(BudgetAmdModXMLRegenService.class);
	private static final P8ContentService MOP8_CONTENT_SERVICE = new P8ContentService();

	protected static P8ContentOperations contentOperationHelper = new P8ContentOperations();
	protected static P8SecurityOperations filenetConnection = new P8SecurityOperations();
	protected static P8ProcessOperations peOperationHelper = new P8ProcessOperations();

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Integer generateBudgetXML(SqlSession aoMyBatisSession) throws ApplicationException
	{
		int loCntBudgetXML = 0;

		// Set up session info
		P8UserSession  aoUserSession = (new P8SecurityOperations()).setP8SessionVariables();

		
        List<BudgetXMLRegenBean> loListOfBudgetAmdMod = (List<BudgetXMLRegenBean>) DAOUtil.masterDAO(aoMyBatisSession, null ,
        		HHSConstants.MAPPER_CLASS_COMMON_MAPPER, HHSConstants.FETCH_BUDGET_XML_REGEN, null);

		if( loListOfBudgetAmdMod == null )     return 0;
		System.out.println( "---[generateAmendmentBudgetData] Begin!! target data size "  + loListOfBudgetAmdMod.size() );

         pullApprovedUser(  aoUserSession,  loListOfBudgetAmdMod ) ;

        for(BudgetXMLRegenBean loAmdModBudget : loListOfBudgetAmdMod){
System.out.println( "---[generateAmendmentBudgetData] Processing Start!!  " + loAmdModBudget.getPrevXMLDocId() + " data" );

			try{
	        	//generateAmendmentBudgetData(aoMyBatisSession , amdBudget );
				MasterBean  mb = generateMasterBeanObjectFromXML(aoMyBatisSession, String.valueOf(loAmdModBudget.getBudgetId()) , aoUserSession);
				
				if (mb == null || mb.getBudgetDetails() == null){
					System.out.println( "---[generateAmendmentBudgetData] XML may not have part of detail data!!  " + mb.toString() + " data" );
					continue;
				}
				
				/* the  point to expend fix line item. */
				mb.getBudgetDetails().setStartDate(  loAmdModBudget.getCorrectBudgetSrtDate()  );
				mb.getBudgetDetails().setEndDate(  loAmdModBudget.getCorrectBudgetEndDate()  );

				convertBean2XML( aoMyBatisSession , mb , loAmdModBudget , aoUserSession ) ;
				System.out.println( "---[generateAmendmentBudgetData] Processing End!!  " + mb.toString() + " data" );

			}catch(Exception e){
				continue;
			}

			loCntBudgetXML++;
        }

        System.out.println( "---[generateAmendmentBudgetData] End Loop!! with " + loListOfBudgetAmdMod.size() + " data \n \n" );

		return loCntBudgetXML;
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

		System.out.println( "---[generateAmendmentBudgetData]  Original:\n" + lsReturnedXml + 
				"==========================================================================");

		// Unmarshalling the String XML to MasterBean object
		MasterBean loMasterBeanObj = (MasterBean) HHSUtil.unmarshalObject(lsReturnedXml);
		return loMasterBeanObj;
	}

	/**
	 * repairs Amendment(or MOD) in MasterBean
	 * 
	 * @param SqlSession aoMyBatisSession
	 * @param MasterBean aoMasterBean
	 * @param P8UserSession  aoUserSession
	 * @return BudgetXMLRegenBean aoBudgetXmlBean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String convertBean2XML( SqlSession aoMyBatisSession , MasterBean aoMasterBean , BudgetXMLRegenBean aoBudgetXmlBean ,P8UserSession  aoUserSession ) throws ApplicationException{
		FileInputStream loFIS = null;
		HashMap<String, Object> loReturnMap = new HashMap<String, Object>();
		Map loHashMap = new HashMap();
		// Convert MasterBean object to XML String
		String lsConvertedXml = convertMasterListToXml(aoMasterBean);

		//System.out.println( "---[generateAmendmentBudgetData]  convertXmlToStream:" + lsConvertedXml );
		// Convert XML String to FileInputStream object
		loFIS = HHSUtil.convertXmlToStream(lsConvertedXml);
		
		String  loDocTitle =    (aoBudgetXmlBean.getBudgetTypeId() == 1) ? HHSConstants.XML_DOC_TITLE : HHSR5Constants.BUDGET_MODIFICATION_XML;
		String  loDocType =      (aoBudgetXmlBean.getBudgetTypeId() == 1) ? HHSConstants.BUDGET_DOC_TYPE : HHSR5Constants.BUDGET_MODIFICATION_TEMPLATE;

		// Uplaod XML documnent to FileNet
		HashMap loFileNetHashMap = new HashMap();
		loFileNetHashMap.put(P8Constants.PROPERTY_CE_DOC_TYPE, loDocType);
		loFileNetHashMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, loDocTitle + aoBudgetXmlBean.getBudgetId() 
				+ HHSConstants.BUDGET_XML_POSTFIX+ aoBudgetXmlBean.getVersionId() );
		System.out.println( "---[convertBean2XML]  DOCUMENT_TITLE:" + loFileNetHashMap.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE) );

		loFileNetHashMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID, aoBudgetXmlBean.getApprovedUserId() );
		loFileNetHashMap.put(P8Constants.PROPERTY_CE_ORGANIZATION_ID, HHSConstants.USER_AGENCY);
		loFileNetHashMap.put(HHSR5Constants.Org_Id, aoBudgetXmlBean.getApprovedUserId());
		loFileNetHashMap.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, Boolean.FALSE);
		loFileNetHashMap.put(P8Constants.MIME_TYPE, HHSConstants.XML_MIME_TYPE);
		loFileNetHashMap.put(P8Constants.PROPERTY_CE_FILE_TYPE, HHSConstants.XML_FILE_TYPE);
		loFileNetHashMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY, aoBudgetXmlBean.getApprovedUserId());
		loFileNetHashMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, aoBudgetXmlBean.getApprovedUserId());

		String lsDocId = "";

			loReturnMap = createDVdocument( aoUserSession, loFIS, loFileNetHashMap, false, false);

			//--checkDocumentExist
			String lsAmendmentModBudgetId = String.valueOf(aoBudgetXmlBean.getBudgetId());
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsAmendmentModBudgetId);
			loHashMap.put(HHSConstants.BUDGET_XML_VERSION_ID, aoBudgetXmlBean.getVersionId() );
			lsDocId = (String) loReturnMap.get(HHSConstants.DOC_ID);

			if( lsDocId != null && !lsDocId.isEmpty() ){
				System.out.println( "---[generateAmendmentBudgetData]  Updating budget table:"  );

				// putting doc-id returned from Filenet against Amendment budget  budgetId
				loHashMap.put(HHSConstants.DOC_ID, lsDocId);

				Integer loUpdateCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.UPDATE_BUDGET_WITH_DOC_ID, HHSConstants.JAVA_UTIL_MAP);
				if (loUpdateCount != 1)
				{
					throw new ApplicationException("DocumentId of XML not inserted successfully for BudgetId : " +aoBudgetXmlBean.getBudgetId());
				}

				Integer loUpdateXMLCount =(Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_COMMON_MAPPER,
						HHSConstants.UPDATE_BUDGET_XML_INTO_REGEN, HHSConstants.JAVA_UTIL_MAP);
			}

		return  lsDocId ; 
	}

	
	public void pullApprovedUser(P8UserSession  aoUserSession, List<BudgetXMLRegenBean> aoListOfBudgetAmdMod ) throws ApplicationException
	{

		System.out.println( "---[generateAmendmentBudgetData] Pulling Agency User!!"  );
		List<String> loDocumentsIdList =  new ArrayList<String> ();
		HashMap<String,BudgetXMLRegenBean> loBudgetXmlMap = new HashMap<String,BudgetXMLRegenBean> () ;
		for(BudgetXMLRegenBean amdBudget : aoListOfBudgetAmdMod ){
			loDocumentsIdList.add(amdBudget.getPrevXMLDocId()) ;
			amdBudget.resetActiveFlag();
			loBudgetXmlMap.put(amdBudget.getPrevXMLDocId(), amdBudget);
		}

		HashMap <String,String> loReqPropParamMap =  new HashMap<String,String>();
		setContentsProperties(loReqPropParamMap );
		System.out.println( "---[generateAmendmentBudgetXML] pull Properties Partameter !!  " + loReqPropParamMap.toString()  );
		
		HashMap<String, HashMap<String, String>> outMap;
		try {
			outMap = getDocProperties( aoUserSession, loReqPropParamMap, loDocumentsIdList);

			for(BudgetXMLRegenBean amdBudget : aoListOfBudgetAmdMod ){
				if( outMap.containsKey(amdBudget.getPrevXMLDocId()) && 
						outMap.get(amdBudget.getPrevXMLDocId()).containsKey(HHSR5Constants.PROVIDER_ID_DOCUMENT.toUpperCase()) ){
					amdBudget.setApprovedUserId( outMap.get(amdBudget.getPrevXMLDocId()).get(P8Constants.PROPERTY_CE_PROVIDER_ID) );
					amdBudget.setCreatedUserId( outMap.get(amdBudget.getPrevXMLDocId()).get(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY_ID) );
					amdBudget.setModifiedUserId( outMap.get(amdBudget.getPrevXMLDocId()).get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID) );
				}
			}

			System.out.println( "---[generateAmendmentBudgetXML] All Properties !! \n" + outMap.toString()  );
			
		} catch (ApplicationException aoAppex) {
			// TODO Auto-generated catch block
			ApplicationException loAppex = new ApplicationException("Error While getting Document Properties", aoAppex);

			throw aoAppex;
		}

	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HashMap<String, HashMap<String, String>> getDocProperties(P8UserSession aoUserSession, HashMap aoHmRequiredProps , List<String> asDocIdLst) throws ApplicationException
	{
		if(asDocIdLst == null || asDocIdLst.size() == 0 )  return new HashMap() ;

		InputStream loContent = null;
		HashMap loHmReqExceProp = new HashMap();

		HashMap<String, HashMap<String, String>> loHmDocumentDetails =  null;
				
		try
		{
			loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(HHSR5Constants.HM_REQUIRED_PROPS, aoHmRequiredProps);
			loHmReqExceProp.put(HHSR5Constants.DOCUMENTS_ID_LIST, asDocIdLst);
			loHmReqExceProp.put(HHSR5Constants.FOLDERS_FILED_IN, HHSR5Constants.FOLDERS_FILED_IN);
			
			System.out.println("Entered P8ContentService.getDocProperties() with parameters::"
					+ loHmReqExceProp.toString());
			if (null != aoUserSession)
			{
				// Fetching FILENET object store from FILENET Domain
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				loHmDocumentDetails = contentOperationHelper.getBRDcoumentPropertiesById(loOS, aoHmRequiredProps, asDocIdLst);

				filenetConnection.popSubject(aoUserSession);

			}
			
			System.out.println("Ended P8ContentService.getDocProperties() with parameters::"
					+ loHmDocumentDetails.toString());

		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error While getting Document Content");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting Document Content", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While getting Document Content");
			ApplicationException loAppex = new ApplicationException("Error While getting Document Content", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting Document Content", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getDocumentContent()");
		
		return loHmDocumentDetails;
	}


	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap<String, String>  getDocProperties(P8UserSession aoUserSession, String asDocId) throws ApplicationException
	{
		InputStream loContent = null;
		HashMap loHmReqExceProp = new HashMap();
		HashMap loOutputHashMap = null;
		List<String> aoDocumentsIdList =  new ArrayList<String> ();
		aoDocumentsIdList.add(asDocId);
		try
		{
			loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(P8Constants.AS_DOC_ID, asDocId);
			//*** Start QC 9585 R 8.9 do not expose password for service account in logs
			String param = CommonUtil.maskPassword(loHmReqExceProp);
			LOG_OBJECT.Debug("Entered P8ContentService.getDocumentContent() with parameters:: " + param);
			//LOG_OBJECT.Info("Entered P8ContentService.getDocumentContent() with parameters::"	+ loHmReqExceProp.toString());
			//*** End QC 9585 R 8.9 do not expose password for service account in logs
			
			if (null != aoUserSession)
			{
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				//loOutputHashMap = contentOperationHelper.getDocumentContent(loOS, asDocId);
				HashMap<String, HashMap<String, String>> loHmDocumentDetails = 
						contentOperationHelper.getBRDcoumentPropertiesById(loOS, loHmReqExceProp, aoDocumentsIdList);
			}
			filenetConnection.popSubject(aoUserSession);
			setMoState("Document Content" + loContent);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error While getting Document Content");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting Document Content", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While getting Document Content");
			ApplicationException loAppex = new ApplicationException("Error While getting Document Content", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting Document Content", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getDocumentContent()");
		return loOutputHashMap;
	}
	


	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public String generateAmendmentBudgetData(SqlSession aoMyBatisSession, BudgetXMLRegenBean aoBudgetXmlBean) throws ApplicationException
	{
		String lsAmendmentBudgetId = null;
		String lsConvertedXml = null;
		String lsDocId = null;
		FileInputStream loFIS = null;
		HashMap<String, Object> loReturnMap = new HashMap<String, Object>();
		
System.out.println( "---[generateAmendmentBudgetData] Begin!!"  );

		try
		{
				List<HashMap<String, Object>> loSubBudgetDetails;
				List<LineItemMasterBean> loLineItemList = new ArrayList<LineItemMasterBean>();
				MasterBean loMasterBean = new MasterBean();
				ContractBudgetService loCBService = new ContractBudgetService();
				P8ContentService loP8Service = new P8ContentService();
				Map loHashMap = new HashMap();
				
				//--checkDocumentExist
				lsAmendmentBudgetId = String.valueOf(aoBudgetXmlBean.getBudgetId());
				loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsAmendmentBudgetId);
				// Fetching Parent base budget id of Amendment Budget
				String lsParentBudgetId = (String) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_PARENT_BUDGET_ID, HHSConstants.JAVA_UTIL_MAP);

System.out.println( "---[generateAmendmentBudgetData] Begin!!" + lsAmendmentBudgetId );

				// Fetching list of subbudget details
				loSubBudgetDetails = (List<HashMap<String, Object>>) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.FETCH_SUB_BUDGET_DETAILS, HHSConstants.JAVA_UTIL_MAP);

if(loSubBudgetDetails == null || loSubBudgetDetails.isEmpty()   ){
	System.out.println( "---[generateAmendmentBudgetData]  sub Budget : 0 for "+ lsDocId  );
	return lsDocId;
} else { 
	System.out.println( "---[generateAmendmentBudgetData]  sub budget:" + loSubBudgetDetails.size() );
}

				// Fetching ststus of Amendment Budget
				String lsBudgetStatusId = "82" 
/*						(String) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.FETCH_AMENDMENT_BUDGET_STATUS, HHSConstants.JAVA_UTIL_MAP)*/
						;

				CBGridBean loCBGridBean = new CBGridBean();
				loCBGridBean.setAmendmentContractID( String.valueOf(aoBudgetXmlBean.getContractId()) );
				loCBGridBean.setContractBudgetID(lsAmendmentBudgetId);
				loCBGridBean.setParentBudgetId(lsParentBudgetId);
				loCBGridBean.setBudgetStatusId(lsBudgetStatusId);
				loCBGridBean.setBudgetTypeId(HHSConstants.ONE);
				// For each subbudget, generate LineItemMasterBean
				Iterator<HashMap<String, Object>> aoListIterator = loSubBudgetDetails.iterator();
				loMasterBean.setBudgetId(lsAmendmentBudgetId);
				System.out.println( "---[generateAmendmentBudgetData]  sub budget:" + loCBGridBean.toString() );

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
					System.out.println( "---[generateAmendmentBudgetData]  sub budget:" + loLineItemBean.toString() );

				}
				System.out.println( "---[generateAmendmentBudgetData]  sub budget End:" + loCBGridBean.toString() );
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

				
				System.out.println( "---[generateAmendmentBudgetData]  convertMasterListToXml:" + loMasterBean.toString() );
				
				// Convert MasterBean object to XML String
				lsConvertedXml = convertMasterListToXml(loMasterBean);

				System.out.println( "---[generateAmendmentBudgetData]  convertXmlToStream:" + lsConvertedXml );
				// Convert XML String to FileInputStream object
				loFIS = HHSUtil.convertXmlToStream(lsConvertedXml);

				String  loDocTitle =    (aoBudgetXmlBean.getBudgetTypeId() == 1) ? HHSConstants.XML_DOC_TITLE : HHSR5Constants.BUDGET_MODIFICATION_XML;
				// Uplaod XML documnent to FileNet
				HashMap loFileNetHashMap = new HashMap();
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_DOC_TYPE, HHSConstants.BUDGET_DOC_TYPE);
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, loDocTitle + aoBudgetXmlBean.getBudgetId() 
						+ HHSConstants.BUDGET_XML_POSTFIX+ aoBudgetXmlBean.getVersionId() );
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID, aoBudgetXmlBean.getCreatedUserId() );
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_ORGANIZATION_ID, HHSConstants.USER_AGENCY);
				loFileNetHashMap.put(HHSR5Constants.Org_Id, aoBudgetXmlBean.getCreatedUserId());
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, Boolean.FALSE);
				loFileNetHashMap.put(P8Constants.MIME_TYPE, HHSConstants.XML_MIME_TYPE);
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_FILE_TYPE, HHSConstants.XML_FILE_TYPE);
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY, aoBudgetXmlBean.getCreatedUserId());
				loFileNetHashMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, aoBudgetXmlBean.getCreatedUserId());

				P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
				loReturnMap = createDVdocument(loFilenetConnection.setP8SessionVariables(), loFIS, loFileNetHashMap, false, false);
				lsDocId = (String) loReturnMap.get(HHSConstants.DOC_ID);

System.out.println( "---[generateAmendmentBudgetData]  Updating budget table:"  );
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

				Integer loUpdateXMLCount =(Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_COMMON_MAPPER,
						HHSConstants.UPDATE_BUDGET_XML_INTO_REGEN, HHSConstants.JAVA_UTIL_MAP);
				
		}
		// catch any application exception thrown from the code due to SELECT
		// statement failure and throw it
		// forward
		catch (ApplicationException aoExp)
		{
			aoExp.addContextData(
					"ApplicationException occured while generating Amendment Budget data :  generateAmendmentBudgetData",
					aoExp);
			LOG_OBJECT.Error("ApplicationException occured while generating Amendment Budget data : generateAmendmentBudgetData "
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


	@SuppressWarnings("unchecked")
	private LineItemMasterBean generateMasterBean(SqlSession aoMyBatisSession, CBGridBean aoCBGridBean,
			ContractBudgetService aoCBService) throws ApplicationException
	{
		List<CBProgramIncomeBean> loProgramincomeBeanList = fetchProgramIncomeAmendment(aoCBGridBean, aoMyBatisSession, null);
		List<SiteDetailsBean> loSiteDetailsBean = fetchSubBudgetSiteDetails(aoMyBatisSession, aoCBGridBean, null, false);
		List<PersonnelServiceBudget> loPersonnelServiceEmployee = generatePersonnelService(aoMyBatisSession,aoCBGridBean);
		List<RateBean> loRateBeanList = fetchContractBudgetAmendmentRate(aoMyBatisSession, aoCBGridBean, null);
		
		// seeting entry type id
		List<CBMileStoneBean> loMilestoneBeanList = fetchMilestone(aoCBGridBean, aoMyBatisSession, null); 
		aoCBGridBean.getEntryTypeId();

		List<CBEquipmentBean> loEquipmentBeanList = fetchAmendmentOTPSEquipment(aoMyBatisSession, aoCBGridBean, null);
		List<CBIndirectRateBean> loIndirectBeanList = fetchIndirectRate(aoMyBatisSession, aoCBGridBean, null);
		List<CBOperationSupportBean> loOpsBeanList = fetchAmendmentOTPS(aoMyBatisSession, aoCBGridBean, null);
		List<CBProfessionalServicesBean> loProfserviceBeanList = fetchProfServicesDetailsAmendment(aoCBGridBean, aoMyBatisSession, null);

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
//			if (aoMasterBean != null && aoCBGridBeanObj !=null && lsBudgetStatus.equals(aoCBGridBeanObj.getBudgetStatusId()))
			/*[End] QC_9153 add null exception handling*/  
/*			{
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
			{*/
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
			//}
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
					
/*					if (aoMasterBeanObj != null
							&& aoCBGridBean.getBudgetStatusId().equals(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_BUDGET_APPROVED)))
					{
						String lsSubBudgetId = aoCBGridBean.getSubBudgetID();
						loSiteDetailList = fetchSiteDetailsListFromXML(lsSubBudgetId, aoMasterBeanObj);
					}
					else
					{*/
						Map<String, String> loMap = new HashMap<String, String>();
						loMap.put(HHSConstants.SUBBUDGET_ID_KEY, aoCBGridBean.getSubBudgetID());
						loMap.put(HHSConstants.BUDGET_ID_KEY, aoCBGridBean.getContractBudgetID());
						loSiteDetailList = (List<SiteDetailsBean>) DAOUtil.masterDAO(aoMybatisSession, loMap,
								HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_SUB_BUDGET_SITE_DETAILS,
								HHSConstants.JAVA_UTIL_MAP);
//					}
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
/*			if (aoMasterBean != null && aoPersonnelServiceBudget.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoPersonnelServiceBudget.getSubBudgetID();
				loSalariedEmployess = fetchSalariedEmployeeFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{*/
				// Fetching list of records for amendment budget
				List<PersonnelServiceBudget> loSalariedEmployessForAmendment = fetchSalariedEmployeeForAmendment(
						aoMybatisSession, aoPersonnelServiceBudget);

				// Fetching list of records for base budget
				CBGridBean loCBGridBeanObj = getCBGridBeanForBaseBudget(aoPersonnelServiceBudget);

				loSalariedEmployess = loCBService.fetchSalariedEmployeeForBase(aoMybatisSession, loCBGridBeanObj);

				setAmendmentBudgetDetailsinBean(loSalariedEmployessForAmendment, loSalariedEmployess);
			//}

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
/*			// Fetch the data from FileNet XML if Budget Status is approved
			if (aoMasterBean != null && aoPersonnelServiceBudget.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoPersonnelServiceBudget.getSubBudgetID();
				loSalariedEmployess = fetchHourlyEmployeeFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{*/
				// Fetching list of records for amendment budget
				List<PersonnelServiceBudget> loSalariedEmployessForAmendment = fetchHourlyEmployeeForAmendment(
						aoMybatisSession, aoPersonnelServiceBudget);

				// Fetching list of records for base budget
				CBGridBean loCBGridBeanObj = getCBGridBeanForBaseBudget(aoPersonnelServiceBudget);

				loSalariedEmployess = loCBService.fetchHourlyEmployeeForBase(aoMybatisSession, loCBGridBeanObj);

				setAmendmentBudgetDetailsinBean(loSalariedEmployessForAmendment, loSalariedEmployess);
//			}

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
/*			// Fetch the data from FileNet XML if Budget Status is approved
			if (aoMasterBean != null && aoPersonnelServiceBudget.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoPersonnelServiceBudget.getSubBudgetID();
				loSalariedEmployess = fetchSeasonalEmployeeFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{*/
				// Fetching list of records for amendment budget
				List<PersonnelServiceBudget> loSalariedEmployessForAmendment = fetchSeasonalEmployeeForAmendment(
						aoMybatisSession, aoPersonnelServiceBudget);

				// Fetching list of records for base budget
				CBGridBean loCBGridBeanObj = getCBGridBeanForBaseBudget(aoPersonnelServiceBudget);

				loSalariedEmployess = loCBService.fetchSeasonalEmployeeForBase(aoMybatisSession, loCBGridBeanObj);

				setAmendmentBudgetDetailsinBean(loSalariedEmployessForAmendment, loSalariedEmployess);
			//}
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
/*			if (aoMasterBean != null && aoPersonnelServiceBudget.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoPersonnelServiceBudget.getSubBudgetID();
				loSalariedEmployess = fetchFringeEmployeeFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{*/
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
/*			}*/
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

/*					// Merge Amendment Amount
					mergeAmendmentAmount(loRateBeanList, loRateBeanAmendmentAmntList);
					// Identify and Mark New rows
					markNewRowInContractBudgetBeanList(loRateBeanList, lsParentSubBudgetId);*/
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
/*			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBMileStoneBean = generateMilestoneList(lsSubBudgetId, aoMasterBean);
				
			}
			else
			{
*/				// Fetching list of records for amendment budget
				List<CBMileStoneBean> loCBMileStoneBeanForAmendment = fetchMilestoneForAmendment(aoMybatisSession,
						aoCBGridBeanObj);
				// Fetching list of records for base budget
				loCBMileStoneBean = fetchMilestoneForBase(aoMybatisSession, aoCBGridBeanObj);
				setAmendmentBudgetDetailsBean(loCBMileStoneBeanForAmendment, loCBMileStoneBean);
/*			}*/
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
/*			// Fetch the data from FileNet XML if Budget Status is approved
			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBEquipmentBeanList = fetchEquipmentFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{*/

				loCBEquipmentBeanList = (List<CBEquipmentBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.FETCH_EQUIPMENT_AMEND_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);

/*				loCBOPAmendList = (List<CBEquipmentBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.FETCH_EQUIPMENT_AMEND_AMT_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
				mergeAmendEquipmentList(loCBEquipmentBeanList, loCBOPAmendList);
			}
*/
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
/*			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBAmendOtpsList = fetchOTPSFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{*/
				aoCBGridBeanObj.setEntryTypeId(HHSConstants.TWO);
				loCBAmendOtpsList = (List<CBOperationSupportBean>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.FETCH_OPERATION_AND_SUPPORT_AMEND_DETAILS,
						HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
/*			}*/

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
/*			if (aoMasterBean != null && aoProfService.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoProfService.getSubBudgetID();
				loProfServicesDetails = fetchProfessionalServiceListFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{*/
				loProfServicesDetails = (List<CBProfessionalServicesBean>) DAOUtil.masterDAO(aoMybatisSession,
						aoProfService, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.AMENDMENT_FETCH_PROF_SERVICES_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
/*  [Start] R7.5.0 QC9146 Professional Service Grid issue for Amendment
				if (loProfServicesDetails != null && loProfServicesDetails.size() > HHSConstants.INT_ZERO)
				{
					amendmentProfServiceBeanIds(loProfServicesDetails);
				}
 [End] R7.5.0 QC9146 Professional Service Grid issue for Amendment  */
			/*}*/

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
/*			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetTypeId().equals(HHSConstants.ONE)
					&& aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBUtilities = fetchUtilitiesDataFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{*/
				loCBUtilities = (List<CBUtilities>) DAOUtil.masterDAO(aoMybatisSession, aoCBGridBeanObj,
						HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
						HHSConstants.CBY_FETCH_UTILITIES_AMENDMENT_DETAILS, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
/*			}*/

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
/*			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loRent = fetchRentFromXML(lsSubBudgetId, aoMasterBean);
				if (loRent != null)
				{
					loNewRent.addAll(loRent);
				}
			}

			else
			{*/
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

/*			}*/

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
/*			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				loCBContractedServicesBean = new ContractedServicesBean();
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBContractedServicesBean = fetchContractedServicesBeanDataFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{
*/				loCBContractedServicesBean = (ContractedServicesBean) DAOUtil
						.masterDAO(aoMyBatisSession, aoCBGridBeanObj,
								HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER,
								HHSConstants.CBY_FETCH_NON_GRID_CONTRACTED_SERVICES,
								HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
/*			}*/

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
		System.out.println( "---[convertMasterListToXml]   :Before Marshal!!"  );
		
		Document loDoc = marshalObjectXML(aoMasterBean);

		System.out.println( "---[convertMasterListToXml]   :"  );

		String lsConvertedXml = HHSUtil.convertDocumentToXML(loDoc);

		System.out.println( "---[convertMasterListToXml]   :HHSUtil.convertDocumentToXML"   );

		return lsConvertedXml;

	}

	public static Document marshalObjectXML(Object aoObject) throws ApplicationException
	{
		SAXHandler loSaxHandler = new SAXHandler();
		Mapping loMapping = new Mapping();
		try
		{
			String lsCastorPath = (String) BaseCacheManagerWeb.getInstance().getCacheObject(
					HHSConstants.CASTER_CONFIGURATION_PATH);

			loMapping.loadMapping(lsCastorPath);

			Marshaller loMarshaller = new Marshaller(loSaxHandler);
			loMarshaller.setMapping(loMapping);
			loMarshaller.marshal(aoObject);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured while marshalling object ", aoEx);
			LOG_OBJECT.Error("Error occured while marshalling object", loAppEx);
			throw loAppEx;
		}
		return loSaxHandler.getDocument();
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
/*			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loUnallocatedFunds = fetchUnallocatedFundsFromXML(lsSubBudgetId, aoMasterBean);
			}
			else if (null != aoCBGridBeanObj && null != aoCBGridBeanObj.getSubBudgetID())
			{*/
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
/*			}*/
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
/*			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBOperationSupportBean = fetchOpAndSupportAmendPageDataFromXML(lsSubBudgetId, aoMasterBean);
			}
			else
			{
*/
				loCBOperationSupportBean = (CBOperationSupportBean) DAOUtil.masterDAO(aoMyBatisSession,
						aoCBGridBeanObj, HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER,
						HHSConstants.FETCH_OPERATION_SUPP_MOD_PAGE_DATA, HHSConstants.INPUT_PARAM_CLASS_CBGRID_BEAN);
/*			}*/
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
/*			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				loNewCBContractedServicesBean = new ArrayList<ContractedServicesBean>();
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBContractedServicesBean = fetchContractedServicesConsultantsFromXML(lsSubBudgetId, aoMasterBean);
				loNewCBContractedServicesBean.addAll(loCBContractedServicesBean);
			}
			else
			{*/
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
/*			}*/

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
/*			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				loNewCBContractedServicesBean = new ArrayList<ContractedServicesBean>();
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBContractedServicesBean = fetchContractedServicesSubContractorsFromXML(lsSubBudgetId, aoMasterBean);
				loNewCBContractedServicesBean.addAll(loCBContractedServicesBean);
			}
			else
			{*/
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
/*			}*/

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
/*			if (aoMasterBean != null && aoCBGridBeanObj.getBudgetStatusId().equals(lsBudgetStatus))
			{
				loNewCBContractedServicesBean = new ArrayList<ContractedServicesBean>();
				String lsSubBudgetId = aoCBGridBeanObj.getSubBudgetID();
				loCBContractedServicesBean = fetchContractedServicesVendorsFromXML(lsSubBudgetId, aoMasterBean);
				loNewCBContractedServicesBean.addAll(loCBContractedServicesBean);
			}
			else
			{*/
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

/*		}*/
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

	private String fetchDocIdOfBudget(SqlSession aoMybatisSession, String asBudgetId) throws ApplicationException
	{
		String lsDocId = (String) DAOUtil.masterDAO(aoMybatisSession, asBudgetId,
				HHSConstants.MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER, HHSConstants.FETCH_DOC_ID_OF_BUDGET,
				HHSConstants.JAVA_LANG_STRING);
		return lsDocId;

	}
	
	public void setContentsProperties(Map<String, String> aoReqProps ){
		//aoReqProps.put(HHSR5Constants.USER_ORG_ID, asUserOrg);
		aoReqProps.put(P8Constants.PROPERTY_CE_DOC_CATEGORY, HHSR5Constants.EMPTY_STRING);
		aoReqProps.put(P8Constants.PROPERTY_CE_DOC_TYPE, HHSR5Constants.EMPTY_STRING);
		aoReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, HHSR5Constants.EMPTY_STRING);
		aoReqProps.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE, HHSR5Constants.EMPTY_STRING);

		// Updated in release 4.0.1- for removing mismatch in modified date end
		aoReqProps.put(P8Constants.PROPERTY_CE_FILE_TYPE, HHSR5Constants.EMPTY_STRING);
		aoReqProps.put(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY, HHSR5Constants.EMPTY_STRING);
		aoReqProps.put(P8Constants.PROPERTY_CE_SAMPLE_TYPE, HHSR5Constants.EMPTY_STRING);
		aoReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, HHSR5Constants.EMPTY_STRING);
		aoReqProps.put(P8Constants.PROPERTY_CE_PROVIDER_ID, HHSR5Constants.EMPTY_STRING);
		aoReqProps.put(HHSR5Constants.PARENT_PATH, HHSR5Constants.EMPTY_STRING);

		// Added for Release 5
		aoReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY, HHSR5Constants.EMPTY_STRING);
		aoReqProps.put(P8Constants.PROPERTY_CE_DATE_CREATED, HHSR5Constants.EMPTY_STRING);
		aoReqProps.put(HHSR5Constants.DELETED_DATE, HHSR5Constants.EMPTY_STRING);
		aoReqProps.put(HHSR5Constants.FILENET_DELETED_BY,HHSR5Constants.EMPTY_STRING);

	}
	
	
	
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap<String, Object> createDVdocument(P8UserSession aoUserSession, FileInputStream aoIS,
			HashMap aoPropertyMap, Boolean abDocExist, Boolean abCheckExist) throws ApplicationException
	{
		boolean lbLinkedToApp = false;
		String lsDocId = null;
		String lsDocType = null;
		String lsDocTitle = null;
		String lsProviderId = null;
		String lsOrgType = null;
		String lsDocCategory = null;
		HashMap loHmReqExceProp = new HashMap();
		HashMap<String, Object> loReturnMap = new HashMap<String, Object>();
		loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(P8Constants.IS, aoIS);
		loHmReqExceProp.put(P8Constants.DB_EXIST, abDocExist);
		loHmReqExceProp.put(P8Constants.CHECK_LIST, abCheckExist);
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		LOG_OBJECT.Info("Entered P8ContentService.createDVdocument() with parameters::" + loHmReqExceProp.toString());
		try
		{
			org.jdom.Document loXmlDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.FILENETDOCTYPE);
			// Fetching ObjectStore from FILENET Domain
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);

			lsDocType = (String) aoPropertyMap.get(P8Constants.PROPERTY_CE_DOC_TYPE);
			lsDocCategory = (String) aoPropertyMap.get(P8Constants.PROPERTY_CE_DOC_CATEGORY);
			lsDocTitle = (String) aoPropertyMap.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE);
			lsProviderId = (String) aoPropertyMap.get(P8Constants.PROPERTY_CE_PROVIDER_ID);
			lsOrgType = (String) aoPropertyMap.get(HHSR5Constants.ORGANIZATION_ID_KEY);
			lbLinkedToApp = (Boolean) aoPropertyMap.get(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION);

			loHmReqExceProp.put(P8Constants.DOC_TYPE, lsDocType);
			loHmReqExceProp.put(P8Constants.DOC_TITLE, lsDocTitle);
			loHmReqExceProp.put(P8Constants.PROVIDER_ID, lsProviderId);

			if (lsDocType == null || lsDocTitle == null || lsProviderId == null)
			{
				ApplicationException loAppex = new ApplicationException(PropertyLoader.getProperty(
						P8Constants.ERROR_PROPERTY_FILE, P8Constants.MO4));
				loAppex.setContextData(loHmReqExceProp);
				throw loAppex;
			}

			// Getting folder Path for new document
			String lsFldPath = contentOperationHelper.getFolderPath(loXmlDoc, lsProviderId, lsDocType, lsDocCategory,
					lsOrgType);
			loHmReqExceProp.put(P8Constants.FIELD_PATH, lsFldPath);

			// Getting document class for new document
			String lsDocClassName = contentOperationHelper.getDocClassName(loXmlDoc, lsDocType, lsDocCategory);
			loHmReqExceProp.put(P8Constants.LS_PROVIDER_ID, lsDocClassName);

			// Start of change for defect 6218

			// if FlagCheck in not exist then checking document manually
			if (!abCheckExist)
			{
				lsDocId = checkDocumentExist(loXmlDoc, loOS, lsDocClassName, lsProviderId, lsDocTitle, lsDocType, lsDocCategory, lsOrgType);
				System.out.println( "---[generateAmendmentBudgetData]"+ 
				" \n loXmlDoc:" +  loXmlDoc + "  \n loOS :" + loOS  + " \n lsDocClassName:" + lsDocClassName+ " \n lsDocTitle:"  + lsDocTitle + 
				" \n lsDocType:" +  lsDocType + " \n lsDocCategory:" +   lsDocCategory + " \n lsOrgType:" +  lsOrgType );

				System.out.println( "---[generateAmendmentBudgetData]"+ 
				" \n lsDocId:" +  lsDocId + "  \n lbLinkedToApp :" + lbLinkedToApp  + " \n aoPropertyMap:" + aoPropertyMap+ " \n lsDocType:"  + lsDocType + 
				" \n lsFldPath:" +  lsFldPath + " \n lsDocClassName:" +   lsDocClassName + " \n lsOrgType:" +  lsOrgType );

				 loReturnMap = createDocument( null , lbLinkedToApp, aoPropertyMap, lsDocType, lsFldPath, lsDocClassName, lsOrgType, loOS, aoIS); 

				if ((Boolean) loReturnMap.get("filedToCustomFolder"))
				{
						System.out.println( "---[generateAmendmentBudgetData] if ((Boolean) loReturnMap.get(\"filedToCustomFolder\")) ---> setParentFolderProprty" + 
								"\n==========================================================================");

					setParentFolderProprty(loOS, aoPropertyMap, (String) loReturnMap.get(HHSR5Constants.DOC_ID));
				}
			}
			
			// End of change for defect 6218
			filenetConnection.popSubject(aoUserSession);
			setMoState("Document has been Created" + lsDocId);
		}
		catch (ApplicationException loEx)
		{
			String lsMessage = loEx.getMessage();
			if (lsMessage.startsWith("Runtime Error in Fetching Filenet"))
			{
				lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
						HHSConstants.FILE_UPLOAD_FAIL_MESSAGE);
				throw new ApplicationException(lsMessage, loEx);
			}
			loEx.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.createDVdocument()::", loEx);
			throw loEx;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While creating document");
			ApplicationException loAppex = new ApplicationException(PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE, HHSConstants.FILE_UPLOAD_FAIL_MESSAGE), aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While creating document", aoEx);
			throw loAppex;
		}
		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT.Info("P8ContentService: document uploaded. method:createDVdocument. Time Taken(seconds):: "
				+ liTimediff);
		LOG_OBJECT.Info("Exiting P8ContentService.createDVdocument() ");
		return loReturnMap;
	}
	
	
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap<String, Object> createDocument(String asDocId, boolean abLinkedToApp, HashMap aoPropertyMap,
			String asDocType, String asFldPath, String asDocClassName, String asOrgId, ObjectStore aoOS,
			FileInputStream aoIS) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		HashMap<String, Object> loReturnMap = new HashMap<String, Object>();
		loHmReqExceProp.put(P8Constants.LINKED_APP, abLinkedToApp);
		loHmReqExceProp.put(P8Constants.DOC_TYPE, asDocType);
		loHmReqExceProp.put(P8Constants.FIELD_PATH, asFldPath);
		loHmReqExceProp.put(P8Constants.DOC_CLASS_NAME, asDocClassName);
		loHmReqExceProp.put(P8Constants.ORG_ID, asOrgId);
		LOG_OBJECT.Info("Entered P8ContentService.createDocument() with parameters::" + loHmReqExceProp.toString());
		try
		{
			if (asDocId != null)
			{
				// now check whether the document is linked to
				// application or not
				if (!abLinkedToApp)
				{
					// delete the document
					contentOperationHelper.deleteDocument(aoOS, asDocId);
					// create the new document
					aoPropertyMap.put(P8Constants.DOCUMENT_ID, asDocId);
					loReturnMap = contentOperationHelper.createDocument(aoOS, aoIS, asDocClassName, asDocType,
							aoPropertyMap, asFldPath);
				}
				else
				{
					ApplicationException loAppex = new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSR5Constants.MESSAGE_SAME_TYPE_EXIST));
					loAppex.setContextData(loHmReqExceProp);
					throw loAppex;
				}
			}
			else
			{
				// if document id is not present then creating new
				// document.
				loReturnMap = contentOperationHelper.createDocument(aoOS, aoIS, asDocClassName, asDocType,
						aoPropertyMap, asFldPath);
			}
			LOG_OBJECT.Info("Exiting P8ContentService.createDocument() ");
			return loReturnMap;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error While creating document");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.createDocument()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While creating document");
			ApplicationException loAppex = new ApplicationException(
					"Error while uploading new document into document vault : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while uploading new document into document vault :", aoEx);
			throw loAppex;
		}
	}
	/**
	 * This method will inherit the folder properties if the document is getting
	 * uploaded into a custom folder
	 * @param aoObjectStore Object store
	 * @param aoPropertyMap property map of the document
	 * @param asDocumentId document id
	 * @throws ApplicationException if there is any exception
	 */
	public void setParentFolderProprty(ObjectStore aoObjectStore, HashMap<String, Object> aoPropertyMap,
			String asDocumentId) throws ApplicationException
	{

		try
		{
			LOG_OBJECT.Info("Entered P8ContentService.setParentFolderProprty()");
			contentOperationHelper.setParentFolderProprty(aoObjectStore, aoPropertyMap, asDocumentId);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error Occured while uploading your Document");
			LOG_OBJECT.Error("Error Occured while uploading your Document", aoAppex);
			throw aoAppex;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error Occured while uploading your Document", aoEx);
			LOG_OBJECT.Error("Error Occured while uploading your Document", aoEx);
			throw loAppex;
		}
	}

	
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private String checkDocumentExist(Object aoXMLDom, ObjectStore aoObjStore, String asDocClassName,
			String asProviderID, String asDocTitle, String asDocType, String asDocCategory, String asOrgId)
			throws ApplicationException
	{
		String lsDocId;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoObjStore.get_Name());
		loHmReqExceProp.put(P8Constants.AS_DOC_CLASS_NAME, asDocClassName);
		loHmReqExceProp.put(P8Constants.AS_PROVIDER_IDS, asProviderID);
		loHmReqExceProp.put(P8Constants.AS_DOC_TITLE, asDocTitle);
		loHmReqExceProp.put(P8Constants.AS_DOC_TYPE, asDocType);
		loHmReqExceProp.put(P8Constants.AS_ORG_ID, asOrgId);
		LOG_OBJECT.Info("Entered P8ContentService.checkDocumentExist() " + loHmReqExceProp.toString());

		try
		{
			// Fetching Folder Path for XMLDOM
			String lsFolderPath = contentOperationHelper.getFolderPath(aoXMLDom, asProviderID, asDocType,
					asDocCategory, asOrgId);
			loHmReqExceProp.put(P8Constants.LS_FOLDER_PATH, lsFolderPath);

			// Fetching DocumentId
			lsDocId = contentOperationHelper.getDocumentId(aoObjStore, asDocClassName, lsFolderPath, asDocTitle,
					asDocType);
			setMoState("Document Exists in the filenet" + lsDocId);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while checking if the document exists in filenet");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while checking if the document exists in filenet ", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while checking if the document exists in filenet");
			ApplicationException loAppex = new ApplicationException(
					"Error while checking if the document exists in filenet: ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while checking if the document exists in filenet: ", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.checkDocumentExist()");
		return lsDocId;
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public String checkDocumentExist(P8UserSession aoUserSession, String asProviderId, String asDocTitle,
			String asDocType, String asDocCategory, String aoOrgId) throws ApplicationException
	{
		String lsDocId = null;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		LOG_OBJECT.Info("Entered P8ContentService.checkDocumentExist() with parameters::" + loHmReqExceProp.toString());
		try
		{
			org.jdom.Document loXmlDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.FILENETDOCTYPE);
			// Fetching FILENET objectstore from FILENET Domain
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			// Fetching DocumentClass Name from DocType
			String lsDocClassName = contentOperationHelper.getDocClassName(loXmlDoc, asDocType, asDocCategory);
			loHmReqExceProp.put(P8Constants.DOC_CLASS_NAME, lsDocClassName);
			loHmReqExceProp.put(P8Constants.AS_PROVIDER_ID, asProviderId);
			loHmReqExceProp.put(P8Constants.AS_DOC_TITLE, asDocTitle);
			loHmReqExceProp.put(P8Constants.AS_DOC_TYPE, asDocType);
			loHmReqExceProp.put(P8Constants.AO_ORG_ID, aoOrgId);
			LOG_OBJECT.Info("Entered P8ContentService.checkDocumentExist() with parameters::"
					+ loHmReqExceProp.toString());
			lsDocId = checkDocumentExist(loXmlDoc, loOS, lsDocClassName, asProviderId, asDocTitle, asDocType,
					asDocCategory, aoOrgId);
			filenetConnection.popSubject(aoUserSession);
			setMoState("Document Exists in the system" + lsDocId);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while checking if the document exists");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while checking if the document exists: ", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while checking if the document exists");
			ApplicationException loAppex = new ApplicationException("Error while checking if the document exists : ",
					aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while checking if the document exists::", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.checkDocumentExist() ");
		return lsDocId;
	}

	
}
