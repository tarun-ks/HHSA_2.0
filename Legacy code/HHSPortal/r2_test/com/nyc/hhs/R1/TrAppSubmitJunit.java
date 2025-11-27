package com.nyc.hhs.R1;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.jdom.Document;
import org.junit.Test;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.frameworks.filenetmanager.FilenetSessionFactory;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.TaxonomyTree;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.TaxonomyDOMUtil;
import com.nyc.hhs.util.XMLUtil;

public class TrAppSubmitJunit
{

	SqlSession moMyBatisSession = null;

	public void getTransactionManager() throws Exception
	{
		ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
		Object loCacheObject = XMLUtil.getDomObj("C:/HHSNYC/HHSPortal/src/com/nyc/hhs/config/TransactionConfig.xml");
		Object loValidationCacheObject = XMLUtil
				.getDomObj("C:/HHSNYC/HHSPortal/src/com/nyc/hhs/config/ValidationRule.xml");
		Object loObject = XMLUtil.getDomObj("C:/HHSNYC/HHSPortal/src/com/nyc/hhs/config/ValidationRule.xml");
		Object loDocObject = XMLUtil.getDomObj("C:/HHSNYC/HHSPortal/src/com/nyc/hhs/config/DocType.xml");

		loCacheManager.putCacheObject("transaction", loCacheObject);
		loCacheManager.putCacheObject("validationRule", loValidationCacheObject);
		loCacheManager.putCacheObject("filenetdoctype", loDocObject);
		loCacheManager.putCacheObject("taxonomy", loObject);

	}

	public void setTaxonomyInCache(ICacheManager aoCacheManager, String asKey) throws ApplicationException
	{

		try
		{

			if (asKey == null)
			{
				asKey = ApplicationConstants.TAXONOMY_ELEMENT;
			}
			Channel loChannelObj = new Channel();
			// Fetch Taxonomy data from DB
			TransactionManager.executeTransaction(loChannelObj, ApplicationConstants.RETRIEVE_FROM_TAXONOMY);
			List<TaxonomyTree> loTaxonomyList = (List<TaxonomyTree>) loChannelObj.getData("loTaxonomyList");

			// Instantiating TaxonomyDOM to generate DOM Tree for Taxonomy
			TaxonomyDOMUtil loTaxonomyDOM = new TaxonomyDOMUtil();
			Document loTaxonomyDom = loTaxonomyDOM.createTaxonomyDOMObj(loTaxonomyList);
			/*
			 * Document loTaxonomyDom = XMLUtil .getDomObj(PropertyUtil.class
			 * .getResourceAsStream("/testing/com/nyc/hhs/taxonomy.xml"));
			 */
			// Caching Taxonomy DOM
			aoCacheManager.putCacheObject(asKey, loTaxonomyDom);

		}
		catch (ApplicationException aoError)
		{
			throw new ApplicationException("Error occured while creating Taxonomy DOM Object Cache", aoError);
		}
	}

	/*
	 * @Test public void testTransaction() throws Exception { ICacheManager
	 * loCacheManager = BaseCacheManagerWeb.getInstance(); Object loCacheObject
	 * = XMLUtil.getDomObj(
	 * "C:/HHSNYC/HHSPortal/src/com/nyc/hhs/config/TransactionConfig.xml");
	 * loCacheManager.putCacheObject("transaction", loCacheObject); Channel
	 * loChannel = new Channel() ; //HashMap loHM = new HashMap() ;
	 * loChannel.setData("asOrgId", "accenture") ; loChannel.setData("asAppId",
	 * "app_1344577479654") ; loChannel.setData("asBussAppId",
	 * "br_1344577479654") ; loChannel.setData("asUserId", "accenture") ;
	 * loChannel.setData("asProviderName", "accenture") ; SqlSession
	 * aoMybatiSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession();
	 * loChannel.setData("aoMyBatisSession", aoMybatiSession) ;
	 * loChannel.setData("aoUserSession", getFilenetSession()) ;
	 * loChannel.setData("workFlowName",
	 * P8Constants.PROPERTY_BR_APPLICATION_WORKFLOW_NAME);
	 * //TransactionManager.executeTransaction(loChannel, "AuditInformation");
	 * TransactionManager.executeTransaction(loChannel, "getLaunchWorkflowMap");
	 * aoMyBatisSession.commit() ; aoMyBatisSession.close() ; Boolean
	 * lbWFLaunchStatus = (Boolean)loChannel.getData("lbWFLaunchStatus") ;
	 * String lsWorkflowId = (String)loChannel.getData("launchWorkflowOutput") ;
	 * assertEquals("", new Boolean(true), new Boolean(lsWorkflowId != null &&
	 * lsWorkflowId.trim().length() > 0)) ; assertEquals("", new Boolean(true),
	 * lbWFLaunchStatus); }
	 */

	@Test
	public void testRetrieveQuestionAnswer() throws Exception
	{
		getTransactionManager();
		Channel loChannel = new Channel();
		// HashMap loHM = new HashMap() ;
		loChannel.setData("asFormElementPath", null);
		loChannel.setData("asQuestionPath", null);
		loChannel.setData("asAppId", "br_1344842362050");
		loChannel.setData("asUserID", "170");
		loChannel.setData("asFormVersion", "0");
		loChannel.setData("asFormName", "Basic");
		moMyBatisSession = MyBatisConnectionFactory.getSqlSessionFactory().openSession();
		loChannel.setData("aoMyBatisSession", moMyBatisSession);
		loChannel.setData("asOrgId", "provider");
		loChannel.setData("asTableName", "basic_form");
		TransactionManager.executeTransaction(loChannel, "retrieve_questionanswer");
		moMyBatisSession.commit();
		moMyBatisSession.close();
		Map loStatusMap = (HashMap) loChannel.getData("loFormInformation");
		assertEquals("", true, loStatusMap != null);

	}

	/*
	 * @Test public void testSaveQuestionAnswer() throws Exception {
	 * Map<String,Object> aoParameters= new HashMap<String,Object>();
	 * 
	 * //aoParameters.put("fb_languageCode", "en");
	 * aoParameters.put("next_action", "save_next");
	 * aoParameters.put("websiteaddress", null); aoParameters.put("OLN", "khj");
	 * aoParameters.put("subsection", "question"); aoParameters.put("ein",
	 * null); aoParameters.put("business_app_id", "br_1344850035092");
	 * aoParameters.put("state", "Hawaii"); aoParameters.put("Zipcode",
	 * "13123"); aoParameters.put("city", "jgj");
	 * aoParameters.put("fb_language", "English");
	 * aoParameters.put("fb_fileName", "Basic"); aoParameters.put("twitter",
	 * "asd"); aoParameters.put("twittersm", "twittersmvalue1");
	 * aoParameters.put("Address2", "gh"); aoParameters.put("Address1", "ghi");
	 * aoParameters.put("phoneno", "312-312-3123");
	 * aoParameters.put("fb_languageCode", "en");
	 * aoParameters.put("fbjscounter", "1"); aoParameters.put("DBA", "h");
	 * aoParameters.put("CS", "Non Profitasd"); aoParameters.put("fb_formName",
	 * "Basic"); aoParameters.put("fb_formVersion", "0");
	 * aoParameters.put("section", "basics"); aoParameters.put("Frommonth",
	 * "Apr"); aoParameters.put("Tomonth", "Mar");
	 * aoParameters.put("Ministatement", "ghj");
	 * 
	 * 
	 * 
	 * getTransactionManager(); Channel loChannel = new Channel() ;
	 * loChannel.setData("asQuestionPath",
	 * "C:/HHSNYC/HHSPortal/WebContent/forms/Basic_0/data/Basic_FormTable.xml");
	 * loChannel.setData("asFormTemplatePath",
	 * "C:/HHSNYC/HHSPortal/WebContent/forms/Basic_0/data/Basic.xml");
	 * loChannel.setData("asBusinessRulePath",
	 * "C:/HHSNYC/HHSPortal/WebContent/forms/businessRule.xml");
	 * loChannel.setData("aoParameters", aoParameters);
	 * loChannel.setData("asUserRoles", "edit");
	 * loChannel.setData("asValidationClass", null);
	 * loChannel.setData("asFormVersion", "0"); loChannel.setData("asFormName",
	 * "Basic"); loChannel.setData("asAppId", "br_1344850035092");
	 * loChannel.setData("asUserID", "123");
	 * 
	 * aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession();
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * loChannel.setData("asTableName", "basic_form");
	 * loChannel.setData("asOrgId", "csc"); loChannel.setData("asSection",
	 * "basics"); loChannel.setData("asAppStatus", "Draft");
	 * loChannel.setData("asUserId", "123");
	 * TransactionManager.executeTransaction(loChannel, "save_questionanswer");
	 * aoMyBatisSession.commit() ; aoMyBatisSession.close() ; DomStatus
	 * loDomStatus=(DomStatus)loChannel.getData("loDomReturn");
	 * assertEquals("",new Boolean(true),new Boolean(loDomStatus!=null));
	 * 
	 * }
	 */

	/*
	 * @Test public void testDocumentListDB() throws Exception
	 * 
	 * { Map<String,String> formMap= new HashMap<String,String>();
	 * formMap.put("STATE", "Maryland"); formMap.put("USER_ID", "123");
	 * formMap.put("STATUS_ID", "Draft"); formMap.put("FORM_VERSION", "0");
	 * formMap.put("DBA", "234234"); formMap.put("OLN", "asfa");
	 * formMap.put("FORM_ID", "Basic_0"); formMap.put("CS", "Non Profit");
	 * formMap.put("TWITTER", "dsfgsdfg"); formMap.put("TWITTER", "dsfgsdfg");
	 * formMap.put("TWITTERSM", "twittersmvalue"); formMap.put("SECTION_ID",
	 * "basics"); formMap.put("WEBADDRESS", "dfgsdfgsfd");
	 * formMap.put("FROMMONTH", "Mar"); formMap.put("ZIPCODE", "21342");
	 * formMap.put("ADDRESS2", "dag"); formMap.put("ADDRESS1", "dafg");
	 * formMap.put("PHONENO", "234-534-5356"); formMap.put("FORM_NAME",
	 * "Basic"); formMap.put("APPLICATION_ID", "br_1344922755491");
	 * formMap.put("CITY", "dfg"); formMap.put("MINISTATEMENT", "sadfgdfg");
	 * formMap.put("ORGANIZATION_ID", "accenture"); formMap.put("TOMONTH",
	 * "Feb");
	 * 
	 * 
	 * List<com.nyc.hhs.model.Document> docList= new
	 * ArrayList<com.nyc.hhs.model.Document>(); com.nyc.hhs.model.Document
	 * loDocument= new com.nyc.hhs.model.Document();
	 * loDocument.setActions(null);
	 * loDocument.setApplicationId("br_1344922755491");
	 * loDocument.setCategoryList(null); loDocument.setCategoryString(null);
	 * loDocument.setCategoryString(null); loDocument.setDate(null);
	 * loDocument.setDisplayHelpOnApp(false);
	 * loDocument.setDocCategory("Corporate Structure");
	 * loDocument.setDocDescription(null); loDocument.setDocName(null);
	 * loDocument.setDocType("Certificate of Assumed Name");
	 * loDocument.setDocumentDescription(null);
	 * loDocument.setDocumentId("44411F08-3F64-4D10-B7D1-628EE45C7615");
	 * loDocument.setDocumentProperties(null);
	 * loDocument.setDocumentShared(false); loDocument.setEffDate(null);
	 * loDocument.setEffectiveDate(null); loDocument.setFiledata(null);
	 * loDocument.setFilePath(null); loDocument.setFileType(null);
	 * loDocument.setFilterModifiedFrom(null);
	 * loDocument.setFilterModifiedTo(null);
	 * loDocument.setFilterNYCAgency(null);
	 * loDocument.setFilterProviderId(null); loDocument.setFormId("Basic_0");
	 * loDocument.setFormName("Basic"); loDocument.setFormVersion("0");
	 * loDocument.setHelpCategory(null); loDocument.setHelpCategoryList(null);
	 * loDocument.setImplementationStatus(null);
	 * loDocument.setLastModifiedBy(null); loDocument.setLastModifiedDate(null);
	 * loDocument.setLinkToApplication(false);
	 * loDocument.setOrganizationId("accenture"); loDocument.setReadOnly("");
	 * loDocument.setSampleCategory(null); loDocument.setSampleType(null);
	 * loDocument.setSampleTypeList(null); loDocument.setSectionId("basics");
	 * loDocument.setSeqNo(null); loDocument.setServiceAppID(null);
	 * loDocument.setShareStatus(null); loDocument.setStatus("Completed");
	 * loDocument.setSubmissionBy(null); loDocument.setSubmissionDate(null);
	 * loDocument.setTypeList(null); loDocument.setUserId("123");
	 * loDocument.setUserOrg(null); loDocument.setVersionNo(null);
	 * docList.add(loDocument);
	 * 
	 * Map<String,String> loHmProps = new HashMap<String,String>();
	 * loHmProps.put("PROVIDER_ID", "csc"); loHmProps.put("HHS_DOC_MODIFIED_BY",
	 * "123"); loHmProps.put("DocumentTitle", "Desert");
	 * loHmProps.put("DateLastModified", "Tue Aug 14 12:27:48 IST 2012");
	 * 
	 * List<String> lolist= new ArrayList<String>();
	 * lolist.add("44411F08-3F64-4D10-B7D1-628EE45C7615");
	 * 
	 * getTransactionManager(); aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession(); Channel
	 * loChannel = new Channel() ; loChannel.setData("asapplicationid",
	 * "br_1344922755491"); loChannel.setData("asOrgId", "accenture");
	 * loChannel.setData("asTableName", "basic_form");
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * loChannel.setData("forminfo", formMap); loChannel.setData("documentList",
	 * docList); loChannel.setData("hmReqProps", loHmProps);
	 * loChannel.setData("aoDocIdList", lolist);
	 * loChannel.setData("aoFilenetSession", getFilenetSession()) ;
	 * TransactionManager.executeTransaction(loChannel, "documentlist_DB");
	 * aoMyBatisSession.commit() ; aoMyBatisSession.close() ; Map formInfoMap =
	 * (HashMap)loChannel.getData("forminfo"); List
	 * formList=(ArrayList)loChannel.getData("documentList"); List
	 * loDocList=(ArrayList)loChannel.getData("aoDocIdList"); Map
	 * documentMap=(HashMap)loChannel.getData("documentPropHM");
	 * 
	 * 
	 * assertEquals("",new Boolean(true),new Boolean(formInfoMap.size()>0));
	 * assertEquals("",new Boolean(true),new Boolean(formList.size()>0));
	 * assertEquals("",new Boolean(true),new Boolean(loDocList.size()>0));
	 * assertEquals("",new Boolean(false),new Boolean(documentMap.size()>0)); }
	 * 
	 * 
	 * @Test public void testBasicGeographyView() throws Exception {
	 * getTransactionManager(); ICacheManager loCacheManager =
	 * BaseCacheManagerWeb.getInstance(); aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession();
	 * PropertyUtil loTaxonomyUtil = new PropertyUtil();
	 * loTaxonomyUtil.setTaxonomyInCache(loCacheManager,
	 * ApplicationConstants.TAXONOMY_ELEMENT); Channel loChannel = new Channel()
	 * ; loChannel.setData("asTaxonomyType", "geography");
	 * loChannel.setData("abFromCache", "true");
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * loChannel.setData("asOrgId", "csc");
	 * TransactionManager.executeTransaction(loChannel, "BasicGeographyView");
	 * aoMyBatisSession.commit() ; aoMyBatisSession.close() ; Map
	 * loTaxonomyMap=(TreeMap)loChannel.getData("lohTaxonomyMap"); List
	 * loTaxonomyList=(ArrayList)loChannel.getData("loTaxonomyIdList");
	 * 
	 * assertEquals("",new Boolean(true),new Boolean(loTaxonomyMap!=null));
	 * assertEquals("",new Boolean(true),new Boolean(loTaxonomyList!=null));
	 * 
	 * }
	 * 
	 * @Test public void testBasicGeographySave() throws Exception {
	 * ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
	 * getTransactionManager(); PropertyUtil loTaxonomyUtil = new
	 * PropertyUtil(); loTaxonomyUtil.setTaxonomyInCache(loCacheManager,
	 * ApplicationConstants.TAXONOMY_ELEMENT);
	 * 
	 * List<String>loList= new ArrayList<String>(); loList.add("abc");
	 * aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession(); Channel
	 * loChannel = new Channel() ; loChannel.setData("asAppId",
	 * "br_1344922755494"); loChannel.setData("asOrgId", "provider");
	 * loChannel.setData("asUserID", "123"); loChannel.setData("asSection",
	 * "basics"); loChannel.setData("asElementType", "geography");
	 * loChannel.setData("aoElementIdList", loList);
	 * loChannel.setData("asBottomCheckBox", "true");
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * TransactionManager.executeTransaction(loChannel, "BasicGeographySave");
	 * aoMyBatisSession.commit() ; aoMyBatisSession.close() ; Boolean
	 * data=(Boolean)loChannel.getData("lbInsertStatus"); List
	 * aList=(ArrayList)loChannel.getData("loTaxonomyIdList");
	 * assertEquals("",new Boolean(true),new Boolean(data!=null));
	 * assertEquals("",new Boolean(true),new Boolean(aList!=null));
	 * 
	 * 
	 * }
	 * 
	 * @Test public void testBasicLanguageSave()throws Exception {
	 * getTransactionManager(); aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession(); Channel
	 * loChannel = new Channel() ; List<String>loList= new ArrayList<String>();
	 * loList.add("abc"); loChannel.setData("asAppId", "br_1344922755494");
	 * loChannel.setData("asOrgId", "provider"); loChannel.setData("asUserID",
	 * "125"); loChannel.setData("asSection", "basics");
	 * loChannel.setData("asElementType", "languages");
	 * loChannel.setData("aoElementIdList", loList);
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * TransactionManager.executeTransaction(loChannel, "basicLanguageSave");
	 * aoMyBatisSession.commit() ; aoMyBatisSession.close() ; Boolean
	 * data=(Boolean)loChannel.getData("lbInsertStatus"); assertEquals("",new
	 * Boolean(true),new Boolean(data!=null)); }
	 * 
	 * @Test public void testBasicPopulationViewSave() throws Exception {
	 * getTransactionManager(); aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession(); Population
	 * population = new Population(); population.setMsAgeFrom(null);
	 * population.setMsAgeTo(null); population.setMsElementid("121");
	 * population.setMsName(null); population.setMsOrganizationid("provider");
	 * population.setMsOther("other"); List<Population> loPopulation= new
	 * ArrayList<Population>();
	 * 
	 * loPopulation.add(population); Channel loChannel = new Channel() ;
	 * loChannel.setData("asAppId", "br_1345550742392");
	 * loChannel.setData("asOrgId", "provider"); loChannel.setData("asUserID",
	 * "121"); loChannel.setData("asSection", "basics");
	 * loChannel.setData("aoPopulationdatalist", population);
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * loChannel.setData("asTaxonomyType", "populations");
	 * loChannel.setData("abFromCache", "true"); //loChannel.setData("asOrgId",
	 * "agency_org");
	 * 
	 * TransactionManager.executeTransaction(loChannel,
	 * "BasicPopulationViewSave"); aoMyBatisSession.commit() ;
	 * aoMyBatisSession.close() ; Boolean
	 * data=(Boolean)loChannel.getData("lbSuccessStatus"); List
	 * taxonomyTreeList=(ArrayList)loChannel.getData("loTaxonomyTreeList"); List
	 * loPopulationList=(ArrayList)loChannel.getData("loPopulation");
	 * assertEquals("",new Boolean(true),new Boolean(data!=null));
	 * assertEquals("",new Boolean(true),new
	 * Boolean(taxonomyTreeList.size()>0)); assertEquals("",new
	 * Boolean(true),new Boolean(loPopulationList.size()>0)); }
	 * 
	 * @Test public void testBusinessApplicationSummary()throws Exception {
	 * getTransactionManager(); aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession(); Channel
	 * loChannel = new Channel() ; loChannel.setData("asAppId",
	 * "br_1344948758576"); loChannel.setData("asOrgId", "agency_org");
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * TransactionManager.executeTransaction(loChannel,
	 * "business_application_summary_test"); aoMyBatisSession.commit() ;
	 * aoMyBatisSession.close() ; List
	 * loDocDetails=(ArrayList)loChannel.getData("loDocDetails"); List
	 * loSubSectionDetails=(ArrayList)loChannel.getData("loSubSectionDetails");
	 * assertEquals("",new Boolean(true),new Boolean( loDocDetails.size()>=0));
	 * assertEquals("",new Boolean(true),new
	 * Boolean(loSubSectionDetails.size()>=0)); }
	 * 
	 * @Test public void testRemoveServiceSummary() throws Exception {
	 * 
	 * ServiceSummary loServiceSummary= new ServiceSummary();
	 * loServiceSummary.setMsBusinessAppId("br_1344948758576");
	 * loServiceSummary.setMsExpirationDate(null);
	 * loServiceSummary.setMsInactiveFlag("0");
	 * loServiceSummary.setMsModifiedBy(null);
	 * loServiceSummary.setMsOrgId("agency_org");
	 * loServiceSummary.setMsProcessStatus("Draft");
	 * loServiceSummary.setMsRemovedFlag("0");
	 * loServiceSummary.setMsServiceAppId("sr_13449557010112");
	 * loServiceSummary.setMsServiceElementId("38");
	 * loServiceSummary.setMsServiceName("Family Supports >  Child Care");
	 * loServiceSummary.setMsServiceStatus("NotStarted");
	 * loServiceSummary.setMsServiceType(null);
	 * loServiceSummary.setMsStartDate(null);
	 * loServiceSummary.setMsStatusId("sub-notstarted");
	 * loServiceSummary.setMsSubmissionDate("2012-08-14 00:00:00");
	 * loServiceSummary.setMsSubmittedBy("123");
	 * loServiceSummary.setMsUserId("123");
	 * 
	 * ServiceSummaryStatus loServiceSummaryStatus= new ServiceSummaryStatus();
	 * loServiceSummaryStatus.setDocumentClass("sub-notstarted");
	 * loServiceSummaryStatus.setDocumentStatus("Not Started");
	 * loServiceSummaryStatus.setQuestionClass("sub-notstarted");
	 * loServiceSummaryStatus.setQuestionStatus("Not Started");
	 * loServiceSummaryStatus.setSelectedSettigClass("sub-notstarted");
	 * loServiceSummaryStatus.setSelectedSettigNames("Not Started");
	 * loServiceSummaryStatus.setSelectedSpecizationClass("sub-notstarted");
	 * loServiceSummaryStatus.setSelectedSpecizationNames("Not Started");
	 * 
	 * loServiceSummary.setServiceSubSectionStatus(loServiceSummaryStatus);
	 * 
	 * Map<String,Object> loServiceSummaryMap= new HashMap<String,Object>();
	 * loServiceSummaryMap.put("service_doc_status", null);
	 * loServiceSummaryMap.put("service_app_status", null);
	 * loServiceSummaryMap.put("service_setting_info", null);
	 * loServiceSummaryMap.put("service_information", loServiceSummary);
	 * 
	 * 
	 * 
	 * getTransactionManager(); aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession(); Channel
	 * loChannel = new Channel() ; loChannel.setData("asServiceId", "SA1234");
	 * loChannel.setData("asOrgId", "agency_org"); loChannel.setData("asAppId",
	 * "br_1344948758576"); loChannel.setData("asBussAppId",
	 * "br_1344948758576"); loChannel.setData("aoMyBatisSession",
	 * aoMyBatisSession);
	 * 
	 * TransactionManager.executeTransaction(loChannel, "RemoveServiceSummary");
	 * aoMyBatisSession.commit() ; aoMyBatisSession.close() ; Boolean
	 * loServiceStatus= (Boolean)loChannel.getData("lbServiceStatus"); Map
	 * loSummaryMap= (HashMap)loChannel.getData("loServiceSummaryList"); List
	 * loSummaryList=(ArrayList)loChannel.getData("lohServiceSummaryMap");
	 * 
	 * assertEquals("",new Boolean(true),new Boolean(loServiceStatus.TRUE));
	 * //Need to confirm from deepak what are the values in Map
	 * assertEquals("",new Boolean(true),new Boolean(loSummaryMap.size()>0));
	 * 
	 * assertEquals("",new Boolean(true),new Boolean(loSummaryList.size()>0)); }
	 * 
	 * @Test public void testUploadDocument()throws Exception {
	 * 
	 * Map lohmReqdMap= new HashMap(); lohmReqdMap.put("PERIOD_COVER_FROM_DATE",
	 * "08/14/2012"); lohmReqdMap.put("HHS_DOC_MODIFIED_BY", "provider");
	 * lohmReqdMap.put("PERIOD_COVER_TO_DATE", "08/14/2012");
	 * 
	 * getTransactionManager(); aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession(); Channel
	 * loChannel = new Channel() ; loChannel.setData("applicationId",
	 * "br_1344947243436"); loChannel.setData("documentId",
	 * "34483F42-6F00-4A65-A983-5C0A747655BD");
	 * loChannel.setData("documentCategory", "Corporate Structure");
	 * loChannel.setData("documentType", "Certificate of Assumed Name");
	 * loChannel.setData("formName", "Basic"); loChannel.setData("formVersion",
	 * "0"); loChannel.setData("organizationId", "provider");
	 * loChannel.setData("docName", "Certificate of Assumed Name");
	 * loChannel.setData("lastModifiedBy", "170");
	 * 
	 * 
	 * 
	 * loChannel.setData("lastModifiedDate", "08/14/2012");
	 * loChannel.setData("submissionBy", "170");
	 * loChannel.setData("submissionDate", "08/14/2012");
	 * loChannel.setData("asServiceAppId", "sr_13449475214312");
	 * loChannel.setData("sectionId", "basics");
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * loChannel.setData("aoFilenetSession", getFilenetSession()); //save
	 * documet properties loChannel.setData("hmReqProps", lohmReqdMap);
	 * loChannel.setData("documentId", "34483F42-6F00-4A65-A983-5C0A747655BD");
	 * loChannel.setData("documentType", "Certificate of Assumed Name");
	 * //insert update doc status loChannel.setData("asSection", "basics");
	 * loChannel.setData("applicationId", "br_1344947243436");
	 * loChannel.setData("userId", "170");
	 * 
	 * TransactionManager.executeTransaction(loChannel, "fileupload_bapp");
	 * 
	 * aoMyBatisSession.commit() ; aoMyBatisSession.close() ;
	 * 
	 * Boolean loStatus=(Boolean)loChannel.getData("lbInsertStatus"); Boolean
	 * loSaveStatus=(Boolean)loChannel.getData("saveStatus"); DomStatus
	 * loDomStatus=(DomStatus)loChannel.getData("loDomReturn");
	 * 
	 * assertEquals("",new Boolean(true),new Boolean(loStatus==true));
	 * assertEquals("",new Boolean(true),new Boolean(loSaveStatus==true));
	 * assertEquals("",new Boolean(true),loDomStatus==null); }
	 * 
	 * @Test public void testServiceSummaryView() throws Exception {
	 * 
	 * 
	 * getTransactionManager(); aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession(); Channel
	 * loChannel = new Channel() ;
	 * 
	 * ServiceSummary loServiceSummary= new ServiceSummary();
	 * loServiceSummary.setMsBusinessAppId("br_1344948758576");
	 * loServiceSummary.setMsExpirationDate(null);
	 * loServiceSummary.setMsInactiveFlag("0");
	 * loServiceSummary.setMsModifiedBy(null);
	 * loServiceSummary.setMsOrgId("agency_org");
	 * loServiceSummary.setMsProcessStatus("Draft");
	 * loServiceSummary.setMsRemovedFlag("0");
	 * loServiceSummary.setMsServiceAppId("sr_13449557010112");
	 * loServiceSummary.setMsServiceElementId("38");
	 * loServiceSummary.setMsServiceName("Family Supports >  Child Care");
	 * loServiceSummary.setMsServiceStatus("NotStarted");
	 * loServiceSummary.setMsServiceType(null);
	 * loServiceSummary.setMsStartDate(null);
	 * loServiceSummary.setMsStatusId("sub-notstarted");
	 * loServiceSummary.setMsSubmissionDate("2012-08-14 00:00:00");
	 * loServiceSummary.setMsSubmittedBy("123");
	 * loServiceSummary.setMsUserId("123");
	 * 
	 * ServiceSummaryStatus loServiceSummaryStatus= new ServiceSummaryStatus();
	 * loServiceSummaryStatus.setDocumentClass("sub-notstarted");
	 * loServiceSummaryStatus.setDocumentStatus("Not Started");
	 * loServiceSummaryStatus.setQuestionClass("sub-notstarted");
	 * loServiceSummaryStatus.setQuestionStatus("Not Started");
	 * loServiceSummaryStatus.setSelectedSettigClass("sub-notstarted");
	 * loServiceSummaryStatus.setSelectedSettigNames("Not Started");
	 * loServiceSummaryStatus.setSelectedSpecizationClass("sub-notstarted");
	 * loServiceSummaryStatus.setSelectedSpecizationNames("Not Started");
	 * 
	 * loServiceSummary.setServiceSubSectionStatus(loServiceSummaryStatus);
	 * 
	 * Map<String,Object> loServiceSummaryMap= new HashMap<String,Object>();
	 * loServiceSummaryMap.put("service_doc_status", null);
	 * loServiceSummaryMap.put("service_app_status", null);
	 * loServiceSummaryMap.put("service_setting_info", null);
	 * loServiceSummaryMap.put("service_information", loServiceSummary);
	 * 
	 * loChannel.setData("asOrgId", "provider"); loChannel.setData("asAppId",
	 * ""); loChannel.setData("asBussAppId", "br_1345102982269");
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * 
	 * TransactionManager.executeTransaction(loChannel, "ServiceSummaryView");
	 * Map loSummaryMap= (HashMap)loChannel.getData("loServiceSummaryList");
	 * List loSummaryList=(ArrayList)loChannel.getData("lohServiceSummaryMap");
	 * assertEquals("",new Boolean(true),new Boolean(loSummaryMap.size()>0));
	 * assertEquals("",new Boolean(true),new Boolean(loSummaryList.size()>0));
	 * 
	 * }
	 * 
	 * 
	 * //AddService will be fix by Deepak then test case will be tested
	 * 
	 * @Test public void testAddService() throws Exception {
	 * getTransactionManager(); aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession(); Channel
	 * loChannel = new Channel() ; loChannel.setData("asElementType",
	 * "SERVICEAREA"); loChannel.setData("abFromCache", "true");
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * loChannel.setData("loBusinessApplicationId", "br_1345114238181");
	 * loChannel.setData("asOrgId", "provider");
	 * TransactionManager.executeTransaction(loChannel, "getSelectedService");
	 * String loService=(String)loChannel.getData("loTaxonomyTree"); List
	 * loServiceList=(ArrayList)loChannel.getData("saveServicesList");
	 * assertEquals("",new Boolean(true),new Boolean(loService!=null &&
	 * loService.length()>0)); assertEquals("",new Boolean(true),new
	 * Boolean(loServiceList.size()>0));
	 * 
	 * }
	 * 
	 * @Test public void testSearchServiceApplication() throws Exception {
	 * getTransactionManager(); aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession(); Channel
	 * loChannel = new Channel() ; loChannel.setData("asData", "Stabilization");
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * TransactionManager.executeTransaction(loChannel,
	 * "getSearchResultService"); List
	 * loListTaxonomy=(ArrayList)loChannel.getData("loListTaxonomy");
	 * assertEquals("",new Boolean(true),new Boolean(loListTaxonomy.size()>=0));
	 * }
	 * 
	 * @Test public void testPrintBusinessApplication() throws Exception {
	 * getTransactionManager(); aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession(); Channel
	 * loChannel = new Channel() ; loChannel.setData("asAppId",
	 * "br_1345550742392"); loChannel.setData("asOrgId", "provider");
	 * loChannel.setData("asUserId", null);
	 * loChannel.setData("asWebContentPath", "C:/HHSNYC/HHSPortal/WebContent");
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * TransactionManager.executeTransaction(loChannel, "printerFriendly"); Map
	 * loPrintMap=(HashMap)loChannel.getData("loPrinterFriendlyContent");
	 * assertEquals("",new Boolean(true),new Boolean(loPrintMap.size()>0)); }
	 * 
	 * @Test public void testPrintServiceApplication()throws Exception {
	 * getTransactionManager(); aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession(); Channel
	 * loChannel = new Channel() ; loChannel.setData("asAppId",
	 * "app_1345550742392"); loChannel.setData("asOrgId", "provider");
	 * loChannel.setData("asBussAppId", "br_1345550742392");
	 * loChannel.setData("service_app_id", "sr_13455514674111");
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * TransactionManager.executeTransaction(loChannel,
	 * "printerFriendlyService"); Map
	 * loPrintMap=(HashMap)loChannel.getData("loPrinterFriendlyContent");
	 * assertEquals("",new Boolean(true),new Boolean(loPrintMap.size()>0)); }
	 * 
	 * @Test public void testServiceApplicationSubmission()throws Exception {
	 * SimpleDateFormat sdfDate = new
	 * SimpleDateFormat("dd-MM-yyyy HH:mm:ss");//dd/MM/yyyy Date now = new
	 * Date(); String strDate = sdfDate.format(now); getTransactionManager();
	 * aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession(); Channel
	 * loChannel = new Channel() ; loChannel.setData("asAuditType", "Audit");
	 * loChannel.setData("orgId", "provider"); loChannel.setData("eventName",
	 * "Status Changed"); loChannel.setData("eventType", "WorkFlow");
	 * loChannel.setData("auditDate", strDate); loChannel.setData("userId",
	 * "200"); loChannel.setData("data", "Status Changed to Approved");
	 * loChannel.setData("entityType", "Section"); loChannel.setData("entityId",
	 * "Business Application: Policies"); loChannel.setData("providerFlag",
	 * "false"); loChannel.setData("appId", "app_1345614650340");
	 * loChannel.setData("documentId", "{7E272DB52528F045B611E21A9938FB18}");
	 * 
	 * Map <String,Object> loLaunchMap= new HashMap<String,Object>();
	 * 
	 * loLaunchMap.put("orgId","provider"); loLaunchMap.put("subDate",
	 * DateUtil.getSqlDate(DateUtil.getCurrentDate()));
	 * loLaunchMap.put("sunmittedBy", "123"); loLaunchMap.put("businessAppId",
	 * "br_1345695539838"); loLaunchMap.put("comments", "Application Submit");
	 * loLaunchMap.put("lsServiceAppId", "sr_13456959165011");
	 * loLaunchMap.put("workflowId", "6571F71F2B56E847846A0EDB72A951A4");
	 * loLaunchMap.put("serviceStatus", "Pending");
	 * 
	 * loLaunchMap.put("asServiceAppId", "sr_13456959165011");
	 * loLaunchMap.put("ParentApplicationID", "app_1345695539838");
	 * 
	 * loLaunchMap.put(P8Constants.PROPERTY_PE_PROVIDER_ID, "provider");
	 * loLaunchMap.put(P8Constants.PROPERTY_PE_PROVIDER_NAME, "Ashutosh");
	 * loLaunchMap.put(P8Constants.PROPERTY_PE_LAUNCH_BY, "123");
	 * loLaunchMap.put(P8Constants.PROPERTY_PE_TASK_NAME,
	 * "Withdrawal Request - Service Application -  Emergency Rental Assistance"
	 * ); loLaunchMap.put(P8Constants.PROPERTY_PE_SECTION_ID, "123");
	 * 
	 * loChannel.setData("lolaunchWorkflowMap", loLaunchMap);
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * 
	 * 
	 * WithdrawRequestDetails loWithdrawRequestDetails= new
	 * WithdrawRequestDetails();
	 * loWithdrawRequestDetails.setMsAppId("app_1345614650340");
	 * loWithdrawRequestDetails.setMsProviderName("provider");
	 * loChannel.setData("serviceName", loWithdrawRequestDetails);
	 * loChannel.setData("serviceParentApplicationId",
	 * loWithdrawRequestDetails);
	 * 
	 * loChannel.setData("aoUserSession", getFilenetSession());
	 * loChannel.setData("workFlowName",
	 * P8Constants.PROPERTY_CONTACT_US_WORKFLOW_NAME);
	 * loChannel.setData("launchWorkflowOutput",
	 * "{7E272DB52528F045B611E21A9938FB18}");
	 * 
	 * loChannel.setData("mapReqForAuditTable", loLaunchMap);
	 * 
	 * loChannel.setData("eventName", "Status Changed");
	 * loChannel.setData("eventType", "WorkFlow");
	 * loChannel.setData("auditDate", "08/12/2012"); loChannel.setData("userId",
	 * "200"); loChannel.setData("data", "Status Changed to Approved");
	 * loChannel.setData("entityType", "Section"); loChannel.setData("entityId",
	 * "entity123"); loChannel.setData("providerFlag", "false");
	 * loChannel.setData("appId", "app_1345695539838");
	 * loChannel.setData("sectionId", "basics");
	 * 
	 * loChannel.setData("asOrgId", "provider"); loChannel.setData("asAppId",
	 * "app_1345695539838"); loChannel.setData("asBussAppId",
	 * "br_1345695539838"); loChannel.setData("asUserId", "200");
	 * loChannel.setData("launchWorkflowOutput",
	 * "{21B188DF-F143-49AB-9F38-C9B8EAB9C316}");
	 * loChannel.setData("asServiceAppId", "sr_13455514674111");
	 * 
	 * 
	 * TransactionManager.executeTransaction(loChannel,
	 * "launchWorkFlowforServiceSubmission"); Boolean
	 * lbAuditStatus=(Boolean)loChannel.getData("lbAuditStatus");
	 * WithdrawRequestDetails
	 * serviceName=(WithdrawRequestDetails)loChannel.getData("serviceName");
	 * WithdrawRequestDetails
	 * serviceParentApplicationId=(WithdrawRequestDetails)
	 * loChannel.getData("serviceParentApplicationId"); Map
	 * lolaunchWorkflowMap=(HashMap)loChannel.getData("lolaunchWorkflowMap");
	 * String
	 * launchWorkflowOutput=(String)loChannel.getData("launchWorkflowOutput");
	 * Map
	 * mapReqForAuditTable=(HashMap)loChannel.getData("mapReqForAuditTable");
	 * Boolean withdrawlServiceAppUpdateStatus=(Boolean)loChannel.getData(
	 * "withdrawlServiceAppUpdateStatus"); Boolean
	 * lbWFLaunchStatus=(Boolean)loChannel.getData("lbWFLaunchStatus");
	 * 
	 * assertEquals("",new Boolean(true),new Boolean(lbAuditStatus.TRUE));
	 * assertEquals("",new Boolean(true),new Boolean(serviceName!=null));
	 * assertEquals("",new Boolean(true),new
	 * Boolean(serviceParentApplicationId!=null)); assertEquals("",new
	 * Boolean(true),new Boolean(lolaunchWorkflowMap.size()>0));
	 * assertEquals("",new Boolean(true),new Boolean(launchWorkflowOutput!=null
	 * && launchWorkflowOutput.length()>0)); assertEquals("",new
	 * Boolean(true),new Boolean(mapReqForAuditTable.size()>0));
	 * assertEquals("",new Boolean(true),new
	 * Boolean(withdrawlServiceAppUpdateStatus.TRUE)); assertEquals("",new
	 * Boolean(true),new Boolean(lbWFLaunchStatus.TRUE)); }
	 * 
	 * @Test public void testAddStaff()throws Exception { Date newDate = new
	 * java.util.Date(); newDate = new java.util.Date(12,8,16);
	 * getTransactionManager(); aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession();
	 * StaffDetails loStaffDetails= new StaffDetails();
	 * loStaffDetails.setIsAdminUser(null);
	 * loStaffDetails.setMsActions("actions");
	 * loStaffDetails.setMsAdminPermission(null);
	 * loStaffDetails.setMsMemberInactiveDate(null);
	 * loStaffDetails.setMsMemberStatus("Actve");
	 * loStaffDetails.setMsNYCUserId("User_1");
	 * loStaffDetails.setMsOrgId("agency_staff");
	 * loStaffDetails.setMsPermissionLevel(null);
	 * loStaffDetails.setMsStaffActiveFlag("Yes");
	 * loStaffDetails.setMsStaffEmail("ashu@mail.com");
	 * loStaffDetails.setMsStaffFirstName("Ashutosh");
	 * loStaffDetails.setMsStaffId("197");
	 * loStaffDetails.setMsStaffLastName("Pandey");
	 * loStaffDetails.setMsStaffMidInitial("Mr");
	 * loStaffDetails.setMsStaffPhone("312-313-1313");
	 * loStaffDetails.setMsStaffTitle("4");
	 * loStaffDetails.setMsSystemUser("No");
	 * loStaffDetails.setMsUserStatus("Pending");
	 * loStaffDetails.setOperationType("dsds");
	 * loStaffDetails.setReadOnly("false"); Channel loChannel = new Channel() ;
	 * 
	 * loChannel.setData("newStaff", loStaffDetails);
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * 
	 * TransactionManager.executeTransaction(loChannel, "insertStaffDetails");
	 * 
	 * Boolean loStaff= (Boolean)loChannel.getData("staffInsertStatus");
	 * assertEquals("",new Boolean(true),loStaff); }
	 * 
	 * @Test public void testDeleteStaff()throws Exception {
	 * getTransactionManager(); aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession(); Channel
	 * loChannel = new Channel() ; loChannel.setData("staffId", "192");
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * TransactionManager.executeTransaction(loChannel, "deleteSelectedStaff");
	 * Boolean loStaff= (Boolean)loChannel.getData("staffDeleteStatus");
	 * assertEquals("",new Boolean(true),loStaff); }
	 * 
	 * @Test public void testAddContract()throws Exception {
	 * getTransactionManager(); aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession();
	 * ContractDetails loContractDetails= new ContractDetails();
	 * loContractDetails.setMsActions("actions");
	 * loContractDetails.setMsContractBudget("7894561300");
	 * loContractDetails.setMsContractDescription("Description");
	 * loContractDetails.setMsContractEndDate(null);
	 * loContractDetails.setMsContractFunderName(null);
	 * loContractDetails.setMsContractID("asdfgh");
	 * loContractDetails.setMsContractNYCAgency
	 * ("Department of Homeless Services (DHS)");
	 * loContractDetails.setMsContractRefEmail("ash");
	 * loContractDetails.setMsContractRefFirstName("dsdsa");
	 * loContractDetails.setMsContractRefLastName("sadsa");
	 * loContractDetails.setMsContractRefMidName("s");
	 * loContractDetails.setMsContractRefPhone("sdsad");
	 * loContractDetails.setMsContractRefTitle("asdasd");
	 * loContractDetails.setMsContractStartDate(null);
	 * loContractDetails.setMsContractType("NYC Government");
	 * loContractDetails.setMsOldContractID("contractId");
	 * loContractDetails.setMsOrgId("provider");
	 * loContractDetails.setReadOnly("false");
	 * 
	 * Map<String,String> loMap= new HashMap<String,String>();
	 * loMap.put("msContractAddedOn", null); loMap.put("msServiceAppId",
	 * "app_1339674915538"); loMap.put("msContractID", "asdfgh");
	 * loMap.put("msOrgId", "provider"); loMap.put("msAppId",
	 * "app_1339674915539");
	 * 
	 * 
	 * 
	 * Channel loChannel = new Channel() ; loChannel.setData("newContract",
	 * loContractDetails); loChannel.setData("aoMyBatisSession",
	 * aoMyBatisSession); loChannel.setData("newContractMapping", loMap);
	 * TransactionManager.executeTransaction(loChannel,
	 * "insertContractDetails"); Boolean loStaff=
	 * (Boolean)loChannel.getData("contractInsertStatus"); Boolean
	 * loStaffMapping=
	 * (Boolean)loChannel.getData("contractMappingInsertStatus");
	 * assertEquals("",new Boolean(true),loStaff); assertEquals("",new
	 * Boolean(true),loStaffMapping);
	 * 
	 * }
	 * 
	 * @Test public void testDeleteContract()throws Exception {
	 * getTransactionManager(); aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession(); Channel
	 * loChannel = new Channel() ; loChannel.setData("contractId", "asdfgh");
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * TransactionManager.executeTransaction(loChannel,
	 * "deleteSelectedContract"); Boolean loStaff=
	 * (Boolean)loChannel.getData("contractDeleteStatus"); Boolean
	 * loStaffMapping=
	 * (Boolean)loChannel.getData("contractMappingDeleteStatus");
	 * 
	 * assertEquals("",new Boolean(true),loStaff); assertEquals("",new
	 * Boolean(true),loStaffMapping); }
	 * 
	 * @Test public void addSettingsToService() throws Exception {
	 * getTransactionManager(); aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession();
	 * List<String> loList= new ArrayList<String>(); loList.add("abc"); Channel
	 * loChannel = new Channel() ; loChannel.setData("asAppId",
	 * "app_1339674915539"); loChannel.setData("asOrgId", "provider");
	 * loChannel.setData("asUserID", "123"); loChannel.setData("asSection",
	 * "basics"); loChannel.setData("asServiceAppId", "app_1339674915538");
	 * loChannel.setData("asElementType", "Service Area");
	 * loChannel.setData("aoElementIdList", loList);
	 * loChannel.setData("asBottomCheckBox", "true");
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * 
	 * TransactionManager.executeTransaction(loChannel, "ServiceSettingSave");
	 * Boolean loSettings= (Boolean)loChannel.getData("lbInsertStatus");
	 * assertEquals("",new Boolean(true),loSettings); }
	 * 
	 * @Test public void testSpecializationSave() throws Exception {
	 * getTransactionManager(); aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession();
	 * List<String> loList= new ArrayList<String>(); loList.add("service");
	 * Channel loChannel = new Channel() ; loChannel.setData("asAppId",
	 * "app_1339674915540"); loChannel.setData("asOrgId", "provider");
	 * loChannel.setData("asUserID", "123"); loChannel.setData("asSection",
	 * "basics"); loChannel.setData("asServiceAppId", "app_1339674915549");
	 * loChannel.setData("asElementType", "Service Area");
	 * loChannel.setData("aoElementIdList", loList);
	 * loChannel.setData("asBottomCheckBox", "true");
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * 
	 * TransactionManager.executeTransaction(loChannel, "SpecializationSave");
	 * Boolean loSpecialization= (Boolean)loChannel.getData("lbInsertStatus");
	 * assertEquals("",new Boolean(true),loSpecialization); }
	 * 
	 * 
	 * //There is some problem with this test case need help of Saurabh
	 * 
	 * @Test public void testLinkDocument()throws Exception {
	 * 
	 * SimpleDateFormat format = new SimpleDateFormat("mm/dd/yyyy"); String
	 * modifiedDate="08/17/2012"; String submissionDate="08/17/2012";
	 * 
	 * java.util.Date date = format.parse(modifiedDate); java.util.Date date1 =
	 * format.parse(submissionDate);
	 * 
	 * java.sql.Date sqlDate; java.sql.Date sqlDate1; sqlDate = new
	 * java.sql.Date(format.parse(modifiedDate).getDate()); sqlDate1=new
	 * java.sql.Date(format.parse(submissionDate).getDate());
	 * 
	 * getTransactionManager(); aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession(); Channel
	 * loChannel = new Channel() ; loChannel.setData("asDocId",
	 * "{C24C68E5-3F93-4B89-B224-287D81BF7860}");
	 * loChannel.setData("asLastModifiedBy", "123");
	 * loChannel.setData("asLastModifiedDate", sqlDate);
	 * loChannel.setData("asSubmissionBy", "123");
	 * loChannel.setData("asSubmissionDate", sqlDate1);
	 * loChannel.setData("asDocType", "Certificate of Assumed Name");
	 * loChannel.setData("asFormName", "Basic");
	 * loChannel.setData("asFormVersion", "0"); loChannel.setData("asOrgId",
	 * "provider"); loChannel.setData("asAppId", "br_1345631040239");
	 * loChannel.setData("asDocCategory", "Service Capacity");
	 * loChannel.setData("asSectionId", "basics");
	 * loChannel.setData("asDocSatus", "Not Started");
	 * loChannel.setData("asDocTitle", "PortletGuide");
	 * loChannel.setData("asUserId", "123"); loChannel.setData("asFormId",
	 * "Basic_0"); loChannel.setData("asServiceAppId", null);
	 * loChannel.setData("asSection", "basics");
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * loChannel.setData("aoFilenetSession", getFilenetSession());
	 * 
	 * Map<String,Object> loMap=new HashMap<String,Object>();
	 * loMap.put("PERIOD_COVER_FROM_DATE",
	 * DateUtil.getSqlDate(DateUtil.getCurrentDate()));
	 * loMap.put("PERIOD_COVER_TO_DATE",
	 * DateUtil.getSqlDate(DateUtil.getCurrentDate()));
	 * 
	 * loChannel.setData("hmReqProps", loMap);
	 * TransactionManager.executeTransaction(loChannel,
	 * "updateDocIdOnRadioSelectServiceSummary");
	 * 
	 * Boolean lbSuccessStatus= (Boolean)loChannel.getData("lbSuccessStatus");
	 * Boolean saveStatus= (Boolean)loChannel.getData("saveStatus"); Boolean
	 * lbStatusService= (Boolean)loChannel.getData("lbStatusService");
	 * assertEquals("",new Boolean(true),lbSuccessStatus); assertEquals("",new
	 * Boolean(true),saveStatus); assertEquals("",new
	 * Boolean(true),lbStatusService);
	 * 
	 * }
	 * 
	 * @Test public void testShareDocumentFilenet()throws Exception {
	 * getTransactionManager(); Channel loChannel = new Channel() ;
	 * loChannel.setData("aoFilenetSession", getFilenetSession()); List<String>
	 * loList=new ArrayList<String>();
	 * loList.add("575334F5-35A2-4FE4-8A4A-DEFE3018F869");
	 * loChannel.setData("documentList",loList); Map<String,String> loMap= new
	 * HashMap<String,String>(); loMap.put("PROVIDER", null);
	 * loMap.put("AGENCY", null); loChannel.setData("documentList", loList);
	 * loChannel.setData("providerAgencyMap", loMap);
	 * TransactionManager.executeTransaction(loChannel,
	 * "shareDocument_filenet"); Boolean lbShare=
	 * (Boolean)loChannel.getData("sharedStatus"); assertEquals("",new
	 * Boolean(true),lbShare); }
	 * 
	 * @Test public void testUnsharedDocument()throws Exception {
	 * getTransactionManager(); Channel loChannel = new Channel() ;
	 * loChannel.setData("aoFilenetSession", getFilenetSession());
	 * Map<String,String> loMap= new HashMap<String,String>();
	 * loMap.put("4909AE66-9084-40E1-91AF-17F4340BFDB7", null);
	 * loChannel.setData("filterProps", loMap);
	 * TransactionManager.executeTransaction(loChannel,
	 * "unshareDocument_filenet"); Boolean lbUnShare=
	 * (Boolean)loChannel.getData("sharedStatus"); assertEquals("",new
	 * Boolean(true),lbUnShare); }
	 * 
	 * 
	 * @Test public void testTaskDetailFilter() throws Exception {
	 * getTransactionManager(); Channel loChannel = new Channel() ;
	 * loChannel.setData("aoFilenetSession", getFilenetSession());
	 * 
	 * Map<String,String>loRequiredProps= new HashMap<String,String>();
	 * loRequiredProps.put("TaskAssignDate", "");
	 * loRequiredProps.put("TaskOwner", ""); loRequiredProps.put("LaunchDate",
	 * ""); loRequiredProps.put("IsTaskLocked", "");
	 * loRequiredProps.put("ProviderName", "");
	 * loRequiredProps.put("TaskStatus", ""); loRequiredProps.put("TaskName",
	 * ""); loRequiredProps.put("IsManagerRevStep", "");
	 * 
	 * Map<String,String> aoFilter= new HashMap<String,String>();
	 * aoFilter.put("TaskOwner", "superuser");
	 * 
	 * loChannel.setData("loRequiredProps", loRequiredProps);
	 * loChannel.setData("moFilterDetails", aoFilter);
	 * 
	 * TransactionManager.executeTransaction(loChannel, "filteredrows_filenet");
	 * Map loFilterResult=(HashMap)loChannel.getData("filteredresults");
	 * assertEquals("",new Boolean(true),loFilterResult.size()>=0);
	 * 
	 * }
	 * 
	 * @Test public void testTaskReserveFilenet() throws Exception {
	 * getTransactionManager(); Channel loChannel = new Channel() ;
	 * loChannel.setData("aoFilenetSession", getFilenetSession()); List<String>
	 * lsWobNumber= new ArrayList<String>();
	 * lsWobNumber.add("570EF05EAAF3E04A8A2760B68EDF188B");
	 * loChannel.setData("aoWobNumbers", lsWobNumber);
	 * loChannel.setData("asUserName", "Manager");
	 * TransactionManager.executeTransaction(loChannel, "assign_filenet"); Map
	 * assignMap=(HashMap)loChannel.getData("lbAssigned"); assertEquals("",new
	 * Boolean(true),assignMap!=null); }
	 * 
	 * @Test public void testFinishParentTask() throws Exception {
	 * getTransactionManager(); Channel loChannel = new Channel() ;
	 * loChannel.setData("aoFilenetSession", getFilenetSession());
	 * loChannel.setData("asWobNumber", ""); loChannel.setData("orgId",
	 * "provider"); loChannel.setData("eventName", "eventName");
	 * loChannel.setData("eventType", "eventType");
	 * loChannel.setData("auditDate", "08/14/2012"); loChannel.setData("userId",
	 * "125"); loChannel.setData("data", "Data");
	 * loChannel.setData("entityType", "entityType");
	 * loChannel.setData("entityId", "entityId"); loChannel.setData("status",
	 * "Status"); loChannel.setData("providerFlag", "flag");
	 * loChannel.setData("appId", "app_1339674915538");
	 * loChannel.setData("sectionId", "basics");
	 * TransactionManager.executeTransaction(loChannel,
	 * "finishparenttask_filenet"); String
	 * lsParentTaskFinish=(String)loChannel.getData("resultparenttaskfinish");
	 * Boolean lbAuditStatus=(Boolean)loChannel.getData("lbAuditStatus");
	 * assertEquals("",new Boolean(true),new Boolean(lsParentTaskFinish!=null));
	 * assertEquals("",new Boolean(true),new Boolean(lbAuditStatus==true)); }
	 * //Sakshi will confirm from Priyanka
	 * 
	 * @Test public void testAlertFilter()throws Exception {
	 * getTransactionManager(); Channel loChannel = new Channel() ;
	 * aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession();
	 * loChannel.setData("asUserId", "agency"); loChannel.setData("asStartNode",
	 * ""); loChannel.setData("asEndNode", "");
	 * loChannel.setData("asNotificationType", "");
	 * loChannel.setData("asToDate", ""); loChannel.setData("asFromDate", "");
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession); }
	 * 
	 * @Test public void testChildTask()throws Exception { SimpleDateFormat
	 * sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");//dd/MM/yyyy Date
	 * now = new Date(); String strDate = sdfDate.format(now);
	 * getTransactionManager(); Channel loChannel = new Channel() ;
	 * loChannel.setData("aoFilenetSession", getFilenetSession());
	 * loChannel.setData("asWobNumber", "4E9107E3A2280449B67128B5DB1A187E");
	 * loChannel.setData("asChildStatus", "Approve");
	 * loChannel.setData("asAuditType", "Audit"); loChannel.setData("orgId",
	 * "provider"); loChannel.setData("eventName",
	 * "Business Application Submission"); loChannel.setData("eventType",
	 * "Business Application Submission"); loChannel.setData("auditDate",
	 * strDate); loChannel.setData("userId", "500"); loChannel.setData("data",
	 * "Business Appication Submitted, workflow initation requested.");
	 * loChannel.setData("entityType", "Business Application Submission");
	 * loChannel.setData("entityId", "br_1345631040238");
	 * loChannel.setData("status", "done"); loChannel.setData("providerFlag",
	 * "false"); loChannel.setData("appId", "br_1345631040239");
	 * loChannel.setData("sectionId", "br_1345631040239");
	 * TransactionManager.executeTransaction(loChannel,
	 * "finishchildtask_filenet"); Map loChildTaskMap=
	 * (HashMap)loChannel.getData("resultchildtaskfinish"); Boolean
	 * lbAuditStatus=(Boolean)loChannel.getData("lbAuditStatus");
	 * 
	 * assertEquals("",new Boolean(true),new Boolean(loChildTaskMap.size()>0));
	 * assertEquals("",new Boolean(true),new Boolean(lbAuditStatus.TRUE)); }
	 * 
	 * @Test public void testSubmitContactUs() throws Exception {
	 * java.lang.Integer sequenceId=127; java.lang.Integer sequence=199;
	 * java.lang.Integer topicId=5; Date date= new Date("08/14/2012");
	 * ContactUsBean contactUs= new ContactUsBean();
	 * 
	 * contactUs.setMsSequenceID(sequenceId); contactUs.setMsTopicID(topicId);
	 * contactUs.setMsStatus("open"); contactUs.setMsContactMedium("email");
	 * contactUs.setMsQuestion("comments"); contactUs.setMsCreationDate(date);
	 * contactUs.setMsCreationUser("Ashutosh"); getTransactionManager(); Channel
	 * loChannel = new Channel() ; aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession();
	 * loChannel.setData("lbSequence", sequence);
	 * loChannel.setData("aoContactUsBean", contactUs);
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * TransactionManager.executeTransaction(loChannel, "ContactView"); Boolean
	 * lbInsertStatus=(Boolean)loChannel.getData("lbInsertStatus");
	 * assertEquals("",new Boolean(true),new Boolean(lbInsertStatus==true)); }
	 * 
	 * @Test public void testFAQAddNewQuestion()throws Exception {
	 * java.lang.Integer questionId= 123; java.lang.Integer topicId= 128; Date
	 * date= new Date("08/14/2012"); getTransactionManager(); Channel loChannel
	 * = new Channel() ; aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession();
	 * 
	 * FaqFormDetailBean faqFormDetailBean = new FaqFormDetailBean();
	 * faqFormDetailBean.setMiQuestionId(questionId);
	 * faqFormDetailBean.setMiTopicId(topicId);
	 * faqFormDetailBean.setMoModifiedDate
	 * (DateUtil.getSqlDate(DateUtil.getCurrentDate()));
	 * faqFormDetailBean.setMsAnswer("Agency Answer 7");
	 * faqFormDetailBean.setMsCreatedBy("Ashutosh");
	 * faqFormDetailBean.setMsModifiedBy("Ashutosh Updated");
	 * faqFormDetailBean.setMsQuestion("Agency Question 7");
	 * 
	 * FaqFormMasterBean faqFormMasterBean = new FaqFormMasterBean();
	 * faqFormMasterBean.setMiTopicId(topicId);
	 * faqFormMasterBean.setMsTopicName("Topic Name");
	 * faqFormMasterBean.setMsModifiedBy("Ashutosh");
	 * faqFormMasterBean.setMoModifiedDate
	 * (DateUtil.getSqlDate(DateUtil.getCurrentDate()));
	 * 
	 * loChannel.setData("lsMasterBean", faqFormMasterBean);
	 * loChannel.setData("lsDetailBean", faqFormDetailBean);
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * TransactionManager.executeTransaction(loChannel,
	 * "faqMaintenanceQAInsert"); Boolean
	 * lbStatusMaster=(Boolean)loChannel.getData("lbStatus"); Boolean
	 * lbStatusDetail=(Boolean)loChannel.getData("lbStatus");
	 * assertEquals("",new Boolean(true),new Boolean(lbStatusMaster.TRUE));
	 * assertEquals("",new Boolean(true),new Boolean(lbStatusDetail.TRUE));
	 * 
	 * }
	 * 
	 * 
	 * 
	 * @Test public void testFAQPublish()throws Exception {
	 * getTransactionManager(); Channel loChannel = new Channel() ;
	 * aoMyBatisSession =
	 * MyBatisConnectionFactory.getSqlSessionFactory().openSession();
	 * 
	 * FaqFormBean faqFormBean= new FaqFormBean();
	 * faqFormBean.setMsType("agency");
	 * 
	 * loChannel.setData("aoMyBatisSession", aoMyBatisSession);
	 * loChannel.setData("aoFaqFormBeanData", faqFormBean);
	 * 
	 * Map loAuditHashMap = new HashMap() ; DateFormat dateFormat = new
	 * SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); Date date = new Date(); String
	 * lslastModifiedDateTaxonomy = dateFormat.format(date);
	 * 
	 * String lsUserId = "Ashutosh"; String lsOrgId = "Ashutosh_org";
	 * loAuditHashMap.put("orgId", lsOrgId) ;
	 * loAuditHashMap.put("eventName","FAQ Publish") ;
	 * loAuditHashMap.put("eventType", "FAQ") ; loAuditHashMap.put("auditDate",
	 * lslastModifiedDateTaxonomy); loAuditHashMap.put("userId", lsUserId) ;
	 * loAuditHashMap.put("data", "FAQ published for "+ "agency") ;
	 * loAuditHashMap.put("entityType", "FAQ") ; loAuditHashMap.put("entityId",
	 * "1") ; loAuditHashMap.put("providerFlag", "N") ;
	 * loChannel.setData("aoAuditDetailMap", loAuditHashMap);
	 * 
	 * 
	 * 
	 * TransactionManager.executeTransaction(loChannel, "faq_publish"); Boolean
	 * deleteFromfaqHelpMaster
	 * =(Boolean)loChannel.getData("deleteFromfaqHelpMaster"); Boolean
	 * deleteFromfaqHelpDetail
	 * =(Boolean)loChannel.getData("deleteFromfaqHelpDetail"); Boolean
	 * insertTofaqHelpMaster
	 * =(Boolean)loChannel.getData("insertTofaqHelpMaster"); Boolean
	 * insertFaqHelpMaster=(Boolean)loChannel.getData("insertTofaqHelpDetail");
	 * assertEquals("",new Boolean(true),new
	 * Boolean(deleteFromfaqHelpMaster.TRUE)); assertEquals("",new
	 * Boolean(true),new Boolean(deleteFromfaqHelpDetail.TRUE));
	 * assertEquals("",new Boolean(true),new
	 * Boolean(insertTofaqHelpMaster.TRUE)); assertEquals("",new
	 * Boolean(true),new Boolean(insertFaqHelpMaster.TRUE)); }
	 * 
	 * @Test public void testUploadDocumentFilenet() throws Exception {
	 * getTransactionManager(); Channel loChannel = new Channel() ;
	 * 
	 * File file = new File("C:/Users/ashutosh.b.pandey/Desktop/document.txt");
	 * FileInputStream fis = null; fis = new FileInputStream(file);
	 * 
	 * 
	 * int content; while ((content = fis.read()) != -1) { // convert to char
	 * and display it }
	 * 
	 * 
	 * BufferedReader reader = new BufferedReader(new
	 * FileReader("C:/Users/ashutosh.b.pandey/Desktop/values.txt")); String line
	 * = null; while ((line = reader.readLine()) != null) { } Map<String,Object>
	 * loPropertyMap= new HashMap<String,Object>();
	 * 
	 * loPropertyMap.put("DocumentTitle","document");
	 * loPropertyMap.put("DOC_TYPE","Fiscal Manual(for audit category)");
	 * loPropertyMap.put("ORGANIZATION_ID","provider_org");
	 * loPropertyMap.put("PROVIDER_ID","provider");
	 * loPropertyMap.put("MimeType","text/plain");
	 * loPropertyMap.put("HHS_DOC_CREATED_BY","123");
	 * loPropertyMap.put("HHS_DOC_MODIFIED_BY","123");
	 * 
	 * 
	 * loPropertyMap.put("LINK_TO_APPLICATION",new Boolean(true));
	 * 
	 * loChannel.setData("aoFilenetSession", getFilenetSession());
	 * loChannel.setData("aoIS", fis); loChannel.setData("aoPropertyMap",
	 * loPropertyMap); loChannel.setData("docExist", false);
	 * loChannel.setData("checkExist", false);
	 * TransactionManager.executeTransaction(loChannel, "fileupload_filenet");
	 * String docId=(String)loChannel.getData("documentId"); assertEquals("",new
	 * Boolean(true),new Boolean(docId!=null && docId.length()>0));
	 * 
	 * }
	 */
	public static P8UserSession getFilenetSession() throws Exception
	{
		FilenetSessionFactory loFilenetSession = new FilenetSessionFactory();
		P8UserSession loUserSession = loFilenetSession.getFilenetSession();
		HashMap loUserSessionMap = new HashMap();
		loUserSessionMap.put("aoFilenetSession", loUserSession);
		Channel loChannel = new Channel();
		loChannel.setData(loUserSessionMap);
		TransactionManager loTransactionManager = new TransactionManager();
		loTransactionManager.executeTransaction(loChannel, ApplicationConstants.FILENET_CONNECTION_TRANS);
		return (P8UserSession) loChannel.getData("P8FilenetSession");
	}

}
