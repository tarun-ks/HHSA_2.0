package com.nyc.hhs.service.filenetmanager.p8constants;

/**
 * This class is responsible for handle all constants which are used throughout
 * the application. for completing any file net operations.
 * 
 * 
 */
// R5 Changes updated
public class P8Constants
{
	// R5 changes ends
	public static final String PROPERTY_FILE = "com.nyc.hhs.properties.hhsservices";
	public static final String ERROR_PROPERTY_FILE = "com.nyc.hhs.properties.errormessages";
	public static final String ALERT_NAME_PROPERTY_FILE = "com.nyc.hhs.properties.alertname";
	public static final String ALERT_SUBJECT_PROPERTY_FILE = "com.nyc.hhs.properties.alertsubject";
	public static final String ALERT_CONTENT_PROPERTY_FILE = "com.nyc.hhs.properties.alertcontent";

	public static final String STRING_USER_TOKEN = "UserToken";

	public static final String SYSTEM_PROPERTY_USER_DIR = "user.dir";
	public static final String DATE_FORMAT = "MM/dd/yyyy";

	public static final String STRING_SEPERATER = ";";
	public static final String STRING_SINGLE_SLASH = "/";

	public static final String CE_TIMESTAMP_START = "T183000Z";
	public static final String CE_TIMESTAMP_END = "T182959Z";

	public static final String PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG = "java.security.auth.login.config";
	public static final String PROP_FILE_JAVA_NAMING_FACTORY_INITIAL = "java.naming.factory.initial";
	public static final String PROP_FILE_FILENET_PE_BOOTSTRAP_CEURI = "filenet.pe.bootstrap.ceuri";
	public static final String PROP_FILE_FILENET_URI = "FILENET_URI";
	public static final String PROP_FILE_SERVICE_ACCOUNT_NAME = "CE_USER_ID";
	public static final String PROP_FILE_SERVICE_ACCOUNT_PASSWORD = "CE_PASSWORD";
	public static final String PROP_FILE_OBJECT_STORE_NAME = "OBJECT_STORE_NAME";
	public static final String PROP_FILE_SHARED_DOC_PROVIDER_CUSTOM_OBJECT = "SHARED_DOC_PROVIDER_CUSTOM_OBJECT";
	public static final String PROP_FILE_SHARED_CUSTOM_OBJECT_FOLDER_NAME = "SHARED_CUSTOM_OBJECT_FOLDER_NAME";
	public static final String PROP_FILE_PREDEFINED_FOLDER_PATH = "PREDEFINED_FOLDER_PATH";
	public static final String PROP_FILE_CONNECTION_POINT_NAME = "CONNECTION_POINT_NAME";

	public static final String DOCUMENT_TITLE = "DocumentTitle";
	public static final String MIME_TYPE = "MimeType";
	public static final String DOCUMENT_ID = "DocumentID";
	public static final String HSS_PROVIDER_ID_PROP = "HHSProviderID";
	public static final String HSS_SHARED_DOCID_PROP = "HHSSharedDocID";
	public static final String PREDEFINED_FOLDER_PATH = "PREDEFINED_FOLDER_PATH";
	public static final String PROVIDER_ID = "ProviderID";
	public static final String HSS_QUEUE_NAME = "HHSAcceleratorProcessQueue";

	public static final int PHONE_NO_LENGTH = 10;

	// Constants for Filenet for contact us

	public static final String PROVIDER_NAME = "ProviderName";
	public static final String TOPIC = "ContactUsTopic";
	public static final String LAUNCHED_BY = "LaunchBy";
	public static final String APPLICATION_ID = "ApplicationID";
	public static final String TASK_NAME = "APPLICATION_ID";

	public static final String PROPERTY_PE_WOBNUMBER = "F_WobNum";
	public static final String PROPERTY_PE_SUBJECT = "Subject";
	// 6574 for Release 3.10.0
	public static final String PROPERTY_PE_WORKFLOW_NAME = "F_Subject";
	public static final String PROPERTY_PE_ACTIVITY_GROUP = "ActivityGroup";
	public static final String REQUIRED_PE_PROPERTIES = "requiredProps";
	public static final String PROPERTY_PE_ASSIGNED_TO = "TaskOwner";
	public static final String PROPERTY_PE_ASSIGNED_TO_NAME = "TaskOwnerName";
	public static final String PROPERTY_PE_DATE_CREATED = "ldDateCreated";
	public static final String PROPERTY_PE_LAST_ASSIGNED = "TaskAssignDate";
	public static final String PROPERTY_PE_F_RESPONSE = "F_Response";
	public static final String PROPERTY_CE_DOCUMENT_TITLE = "DocumentTitle";
	public static final String PROPERTY_CE_MIME_TYPE = "MimeType";
	// Added new constants for Financial Documents
	public static final String PROPERTY_CE_CONTRACT_ID = "CONTRACT_ID";
	public static final String PROPERTY_CE_CONTRACT_TITLE = "CONTRACT_TITLE";
	public static final String PROPERTY_CE_PROCUREMENT_ID = "PROCUREMENT_ID";
	public static final String PROPERTY_CE_BUDGET_ID = "BUDGET_ID";
	public static final String PROPERTY_CE_DOCUMENT_TYPE = "DOCUMENT_TYPE";
	public static final String PREDEFINED_FOLDER_PATH_FINANCIAL_DOC = "PREDEFINED_FOLDER_PATH_FINANCIAL_DOC";

	public static final String PROPERTY_CE_DOC_TYPE = "DOC_TYPE";
	public static final String PROPERTY_CE_DOC_CATEGORY = "DOC_CATEGORY";
	public static final String PROPERTY_CE_DOC_STATUS = "DOC_STATUS";
	public static final String PROPERTY_CE_ORGANIZATION_ID = "ORGANIZATION_ID";
	public static final String PROPERTY_CE_HHS_DOC_CREATED_BY = "HHS_DOC_CREATED_BY";
	public static final String PROPERTY_CE_HHS_DOC_MODIFIED_BY = "HHS_DOC_MODIFIED_BY";
	public static final String PROPERTY_CE_HHS_DOC_CREATED_BY_ID = "HHS_DOC_CREATED_BY_ID";
	public static final String PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID = "HHS_DOC_MODIFIED_BY_ID";
	public static final String PROPERTY_CE_START_DATE = "START_DATE";
	public static final String PROPERTY_CE_EXPIRATION_DATE = "EXPIRATION_DATE";
	public static final String PROPERTY_CE_PROVIDER_ID = "PROVIDER_ID";
	public static final String PROPERTY_CE_DOCUMENT_ID = "Id";
	public static final String PROPERTY_CE_LAST_MODIFIER = "LastModifier";
	public static final String PROPERTY_CE_LAST_MODIFIED_DATE = "DateLastModified";
	public static final String PROPERTY_CE_HHS_LAST_MODIFIED_DATE = "HHS_LAST_MODIFIED_DATE";
	public static final String PROPERTY_CE_VERSION_NUMBER = "MajorVersionNumber";
	public static final String PROPERTY_CE_FILE_TYPE = "FILE_TYPE";
	public static final String PROPERTY_CE_IS_CURRENT_VERSION = "IsCurrentVersion";
	public static final String PROPERTY_CE_DOCUMENT_CLASS = "Document";
	public static final String PROPERTY_CE_IS_DOCUMENT_SHARED = "IS_SHARED";
	public static final String PROPERTY_CE_PRINT_VIEW_ID = "PRINT_VIEW_ID";
	public static final String PROPERTY_CE_CHAR500_DOC_TYPE = "CHAR 500 + 990 + Audit"; // Updated
																						// for
																						// 3.1.0,
																						// Enhancement
																						// 6021
	public static final String PROPERTY_CE_CHAR500_EXT1_DOC_TYPE = "CHAR500 - 1st Extension Document";
	public static final String PROPERTY_CE_CHAR500_EXT2_DOC_TYPE = "CHAR500 - 2nd Extension Document";
	public static final String PROPERTY_CE_CHAR500_EXTENSION = "CHAR 500 - Extension"; // Added
																						// for
																						// 3.1.0,
																						// Enhancement
																						// 6021
	public static final String PROPERTY_CE_CHAR500_EXTENSION_DOC_CLASS = "CHAR500_EXT"; // Added
																						// for
																						// 3.1.0,
																						// Enhancement
																						// 6021
	public static final String PROPERTY_CE_SYSTEM_TERMS_CONDITION_TYPE = "System Terms & Conditions";
	public static final String PROPERTY_CE_APPLICATION_TERMS_CONDITION_TYPE = "Application Terms & Conditions";
	public static final String PROPERTY_CE_PPRINTER_FRINDLY_TYPE = "PRINTER_FRINDLY_VERSI";
	public static final String PROPERTY_CE_DOC_LOCK_STATUS = "LOCK_STATUS";
	public static final String PROPERTY_CE_DOC_LINK_TO_APPLICATION = "LINK_TO_APPLICATION";
	public static final String PROPERTY_CE_DOC_CLASS_SYSTEM_TERMS_CONDITIONS = "SYSTEM_TERMS_CONDITIONS";
	public static final String PROPERTY_CE_DOC_CLASS_APPLICATION_TERMS_CONDITIONS = "APPLICATION_TERMS_CONDITIONS";
	public static final String PROPERTY_CE_DOC_CLASS_STANDARD_CONTRACT = "STANDARD_CONTRACT";
	public static final String PROPERTY_CE_DOC_CLASS_APPENDIX_A = "APPENDIX_A";
	public static final String PROPERTY_CE_DOC_TYPE_CFO = "Chief Financial Officer (CFO) Resume or Equivalent";
	public static final String PROPERTY_CE_DOC_TYPE_CEO = "Chief Executive Officer (CEO) Resume or Equivalent";
	public static final String PROPERTY_CE_DOC_TYPE_KEYSTAFF_RESUME = "Key Staff - Resume";
	public static final String PROPERTY_CE_DOC_TYPE_BAFO_DOCUMENT = "Best and Final Offer (BAFO)";

	public static final String PROPERTY_MODIFIED_FROM = "ModifiedFrom";
	public static final String PROPERTY_MODIFIED_TO = "ModifiedTo";

	public static final String ROOT_NODE = "ROOT/";
	public static final String XML_DOC_TYPE_NODE = "DocType";
	public static final String PROPERTY_CE_ROOT_DOC_CLASS_NAME = "ROOT_DOC_CLASS_NAME";

	public static final String XML_DOC_TYPE_PROPERTY = "DocType";
	public static final String XML_DOC_CATEGORY_PROPERTY = "DocumentCategory";

	public static final String XML_HELP_CATEGORY_NODE = "HelpCategory";

	public static final String XML_DOC_PROP_DISPLAY_NAME = "display_name";
	public static final String XML_DOC_PROP_SYMBOLIC_NAME = "symbolic_name";
	public static final String XML_DOC_PROP_ID = "id";
	public static final String XML_DOC_PROP_TYPE = "type";
	public static final String XML_DOC_PROP_IS_DISABLED = "isdisable";
	public static final String XML_DOC_PROP_IS_DROPDOWN = "isdropdown";
	public static final String PROP_FILE_PREDEFINED_FOLDER_PATH_PROVIDER = "PREDEFINED_FOLDER_PATH_PROVIDER";

	public static final String PROP_FILE_PREDEFINED_FOLDER_PATH_CITY = "PREDEFINED_FOLDER_PATH_CITY";
	public static final String PROP_FILE_PREDEFINED_FOLDER_PATH_AGENCY = "PREDEFINED_FOLDER_PATH_AGENCY";
	public static final String APPLICATION_PROVIDER_ORG = "provider_org";
	public static final String APPLICATION_CITY_ORG = "city_org";
	public static final String XML_DOC_ORG_ID_PROPERTY = "organizationId";
	public static final String PROPERTY_CE_SHARED_PROVIDER_ID = "HHSProviderID";
	public static final String PROPERTY_CE_SHARED_AGENCY_ID = "HHS_AGENCY_ID";
	public static final String PROPERTY_CE_SHARED_BY_ID = "HHS_SHARED_BY";
	public static final String PROPERTY_CE_SHARED_DOC_ID = "SHARED_DOCUMENT_ID";
	public static final String PROPERTY_CE_FILTER_PROVIDER_ID = "FILTER_PROVIDER_ID";
	public static final String PROPERTY_CE_FILTER_NYC_ORG = "FILTER_NYC_ORG_ID";

	public static final String PROPERTY_CE_DOC_TABLE_ALIAS = " DOC";
	public static final String PROPERTY_CE_SHARED_OBJECT_TABLE_ALIAS = " SO";

	public static final String PROPERTY_ALLOWED_FILE_SIZE_BYTES = "12582912";

	// New WF props
	public static final String PROPERTY_BR_APPLICATION_WORKFLOW_NAME = "WF01 - Application Submission";
	public static final String PROPERTY_SECTION_WORKFLOW_NAME = "WF01a - Application Section Submission";

	public static final String PROPERTY_PE_TASK_TYPE = "TaskType";
	public static final String PROPERTY_PE_TASK_NAME = "TaskName";
	public static final String PROPERTY_PE_APPLICTION_ID = "ApplicationID";
	public static final String PROPERTY_PE_PROVIDER_ID = "ProviderID";
	public static final String PROPERTY_PE_PROVIDER_NAME = "ProviderName";
	public static final String PROPERTY_PE_TASK_STATUS = "TaskStatus";
	public static final String PROPERTY_PE_LAUNCH_BY = "LaunchBy";
	public static final String PROPERTY_PE_LAUNCH_DATE = "LaunchDate";
	public static final String PROPERTY_IS_CHILD_TASK_LAUNCHED = "IsChildTaskLaunched";
	public static final String PROPERTY_PE_SECTION_IDS = "SectionIDs";
	public static final String PROPERTY_PE_SECTION_TASK_NAMES = "SectionTaskNames";
	public static final String PROPERTY_PE_SECTION_TASK_STATUS = "SectionTaskStatus";
	public static final String PROPERTY_PE_SECTION_WF_NOS = "SectionWFNos";
	public static final String PROPERTY_PE_PARENT_APP_WOB_NO = "ParentAppWFNo";
	public static final String PROPERTY_PE_SECTION_ID = "SectionID";
	public static final String PROPERTY_PE_BR_APP_OWNER = "BRAppOwner";
	public static final String PROPERTY_PE_BR_APP_OWNER_NAME = "BRAppOwnerName";
	public static final String PROPERTY_PE_IS_SECTION_TASK_UPDAED = "IsSectionsTaskUpdated";
	public static final String PROPERTY_PE_IS_MANAGER_REVIEW_STEP = "IsManagerRevStep";

	public static final String PROPERTY_PE_FINISH_ACTION = "BRFinishAction"; // For
																				// moving
																				// the
																				// task
	// to next stage

	public static final String PROPERTY_PE_FINISH_BR_OWNER_TASK_VALUE = "FinishBROwnerTask"; // will
	public static final String PROPERTY_PE_FINISH_MANAGER_TASK_VALUE = "FinishManagerTask"; // will
	public static final String PROPERTY_PE_VALUE_VERIFIED = "Verified";
	public static final String PROPERTY_PAGE_INBOX = "inbox";
	public static final String PROPERTY_PAGE_TASK_MANAGMENT = "taskmanager";

	// End New WF props
	public static final String PROPERTY_PE_COMMENTS = "Enter Comments";

	// Added for TaskHistory Audit
	public static final String PROPERTY_PE_TH_ENTITY = "ENTITY";
	public static final String PROPERTY_PE_TH_INTERNAL_COMMENT = "Internal Comments";
	public static final String PROPERTY_PE_TH_PROVIDER_COMMENT = "Provider Comments";
	public static final String PROPERTY_PE_TH_TASK_ASSIGNMENT = "Task Assignment";
	public static final String PROPERTY_PE_TH_TASK_FINISHED = "Task Finished";
	public static final String PROPERTY_PE_TH_STATUS_UPDATE = "Status Update";
	public static final String PROPERTY_PE_TH_STATUS_UPDATE_DOCUMENT = "Status Update :";
	public static final String PROPERTY_PE_TH_TASK_CREATED = "Task Created";
	public static final String PROPERTY_PE_TH_TASK_CREATION = "Task Creation";
	public static final String PROPERTY_PE_TH_TASK_OWNER_ASSIGN = "Assign Owner";
	public static final String PROPERTY_PE_TH_TASK_ASSIGNED_TO = "Task Assigned To";

	public static final String PROPERTY_HMP_SUBMITTED_FROM = "SubmittedFrom";
	public static final String PROPERTY_HMP_SUBMITTED_TO = "SubmittedTo";
	public static final String PROPERTY_HMP_ASSIGNED_FROM = "AssignedFrom";
	public static final String PROPERTY_HMP_ASSIGNED_TO = "AssignedTo";
	public static final String PROPERTY_PE_IS_SECTIONS_TASK_UPDATED = "IsSectionsTaskUpdated";

	// *** New task types
	public static final String PROPERTY_PE_TASK_TYPE_BR_APPLICATION = "Business Review Application";
	public static final String PROPERTY_PE_TASK_TYPE_SECTION_BASIC = "Business Application: Basics";
	public static final String PROPERTY_PE_TASK_TYPE_SECTION_FILINGS = "Business Application: Filings";
	public static final String PROPERTY_PE_TASK_TYPE_SECTION_BOARD = "Business Application: Board";
	public static final String PROPERTY_PE_TASK_TYPE_SECTION_POLICIES = "Business Application: Policies";
	public static final String PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION = "Service Application";
	public static final String PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST = "Withdrawal Request";
	public static final String PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION = "Withdrawal Request - Business Review Application";
	public static final String PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION = "Withdrawal Request - Service Application";
	public static final String PROPERTY_PE_TASK_TYPE_PROVIDER_ACCOUNT_REQUEST = "Provider Account Request";
	public static final String PROPERTY_PE_TASK_TYPE_EXPIRIED_DOCUMENT_LOADED = "Expired Document Loaded";
	public static final String PROPERTY_PE_TASK_TYPE_CONTACT_US = "Contact Us";
	public static final String PROPERTY_PE_TASK_TYPE_ORGANIZATION_LEGAL_NAME_UPDATE_REQUEST = "Organization Legal Name - Update Request";
	public static final String PE_GRID_PAGE_SIZE = "20";
	public static final String PE_TASK_UNASSIGNED_MANAGER = "Unassigned - Manager";
	public static final String PE_TASK_UNASSIGNED = "Unassigned";
	public static final String PROPERTY_CE_DATE_CREATED = "DateCreated";

	public static final String PROPERTY_PE_BR_SUBSECTION_QUESTION = "basics questions";
	public static final String PROPERTY_PE_BR_SUBSECTION_POLICIES_QUESTION = "policies questions";
	public static final String PROPERTY_PE_BR_SUBSECTION_BOARD_QUESTION = "board questions";
	public static final String PROPERTY_PE_BR_SUBSECTION_FILINGS_QUESTION = "filings questions";
	public static final String PROPERTY_PE_BR_SUBSECTION_GEOGRAPHY = "geography";
	public static final String PROPERTY_PE_BR_SUBSECTION_LANGUAGES = "languages";
	public static final String PROPERTY_PE_BR_SUBSECTION_POPULATIONS = "populations";

	public static final String PROPERTY_PE_SERVICE_SUBSECTION_QUESTION = "service questions";
	public static final String PROPERTY_PE_SERVICE_SPECIALIZATION = "specialization";
	public static final String PROPERTY_PE_SERVICE_SERVICE_SETTINGS = "servicesetting";
	public static final String PROPERTY_PE_SERVICE_SUBSECTION_NAME = "service";

	// ******** end New task types

	public static final String PROPERTY_PE_PARENT_BR_STATUS = "ParentBRStatus"; // This
																				// status
																				// will
	// be set to find
	// out the logical
	// condition to
	// terminate the
	// section task on
	// manager finish
	public static final String PROPERTY_PE_TH_TASK_ACTIVITY_BY_SYSTEM = "System";
	public static final String PROPERTY_PE_IS_TASK_LOCKED = "IsTaskLocked"; // Used
																			// for
																			// UI
																			// locking
	// and unlocking
	public static final String PROPERTY_PE_IS_TASK_VISSIBLE = "taskVisibility"; // Used
																				// for
	// visibility in
	// inbox
	public static final String PROPERTY_PE_TASK_MODIFIED_DATE = "TaskModifiedDate";
	public static final String PROPERTY_PE_TASK_ASSIGN_DATE = "TaskAssignDate";
	public static final String PROPERTY_PE_PARENT_APPLICATION_ID = "ParentApplicationID";
	public static final String PROPERTY_PE_SERVICE_CAPACITY_IDS = "ServiceCapacityIDs";
	public static final String PROPERTY_PE_SERVICE_CAPACITY_NAMES = "ServiceCapacityNames";
	public static final String PROPERTY_PE_APPLICATION_TASK_OWNER = "AppTaskOwner"; // Same
																					// as
																					// BR
	// Task Owner of
	// BR
	// application
	// (used for
	// other tasks)
	public static final String PROPERTY_PE_VALUE_ALL_STAFF = "All Staff"; // This
																			// value
																			// comes
																			// from
	// filter for TaskOwner
	public static final String PROPERTY_PE_VALUE_UNASSIGN = "Unassign"; // This
																		// value
																		// comes
																		// from
	// filter for TaskOwner

	public static final String PROPERTY_PE_VALUE_WITHDRAWL_REQUEST = "Withdrawal Request"; // This
	// value
	// comes
	// from
	// filter
	// for
	// TaskType

	public static final String PROPERTY_SERVICE_CAPACITY_WORKFLOW_NAME = "WF02 - Services Submission";
	public static final String PROPERTY_BUSINESS_APPLICATION_WITHDRAWL_WORKFLOW_NAME = "WF06 - Business Application Withdrawal";
	public static final String PROPERTY_WF_STRINGSEPERATOR = "###";

	public static final String PROPERTY_CE_HELP_CATEGORY = "HELP_CATEGORY";
	public static final String PROPERTY_CE_DISPLAY_HELP_ON_APP = "DISPLAY_HELP_ON_APP";
	public static final String PROPERTY_CE_DOCUMENT_DESCRIPTION = "DESCRIPTION";
	public static final String PROPERTY_CE_SAMPLE_CATEGORY = "SAMPLE_CATEGORY";
	public static final String PROPERTY_CE_SAMPLE_TYPE = "SAMPLE_TYPE";

	public static final String PROPERTY_PE_TASK_TYPE_NEW_FILING = "New Filing";
	public static final String PROPERTY_PE_CONTACT_US_TOPIC = "ContactUsTopic";
	public static final String PROPERTY_SERVICE_APPLICATION_WITHDRAWL_WORKFLOW_NAME = "WF07 - Service Application Withdrawal";
	public static final String PROPERTY_NEW_FILING_WORKFLOW_NAME = "WF03 - New Filing";
	public static final String PROPERTY_CONTACT_US_WORKFLOW_NAME = "WF08 - Contact Us";
	public static final String PROPERTY_ORG_ACC_REQ_WORKFLOW_NAME = "WF04 - Organization Account Request";
	public static final String PROPERTY_ORG_LEGAL_NAME_UPDATE_WORKFLOW_NAME = "WF05 - Organization Legal Name Update";
	public static final String PROPERTY_CONTACT_US_STATUS_VALUE = "Close";

	// New entries for New Filing tasks
	public static final String PROPERTY_PE_NEXT_EXPECTED_DOC_TYPE = "nextExpectedDocType";
	public static final String PROPERTY_PE_NEXT_PERIOD_COVERED_END_MONTH = "nextEndFiscalMonth";
	public static final String PROPERTY_PE_NEXT_PERIOD_COVERED_END_YEAR = "nextEndFiscalYear";
	public static final String PROPERTY_PE_NEXT_PERIOD_COVERED_START_MONTH = "nextStartFiscalMonth";
	public static final String PROPERTY_PE_NEXT_PERIOD_COVERED_START_YEAR = "nextStartFiscalYear";
	public static final String PROPERTY_PE_PRESENT_PERIOD_COVERED_START_MONTH = "presentStartFiscalMonth";
	public static final String PROPERTY_PE_PRESENT_PERIOD_COVERED_START_YEAR = "presentStartFiscalYear";
	public static final String PROPERTY_PE_CURRENT_DUE_DATE = "dueDate";
	public static final String PROPERTY_PE_LAST_UPLOADED_DOC_TYPE = "lastUploadedDocumentType";
	public static final String PROPERTY_PE_IS_SHORT_FILING = "isShortFiling";
	public static final String PROPERTY_PE_AFTER_SHORT_FILING = "lasUploadAfterShortFiling";
	public static final String PROPERTY_PE_UPLOADED_DOC_TYPE = "documentType";
	public static final String PROPERTY_PE_UPLOADED_DOC_NAME = "documentName";
	public static final String PROPERTY_PE_APPLICABLE_LAW = "applicableLaw";
	public static final String PROPERTY_PE_IS_FINANCIAL_DOC = "IS_FINANCIAL_DOC";

	public static final String PROPERTY_PE_PROVIDER_NEW_NAME = "ProviderNewName";

	public static final String PROPERTY_PE_ENTITY_ID = "EntityID";
	public static final String PROPERTY_PE_LAW_TYPE = "Estate Power and Trust Law";
	public static final String PROPERTY_CE_DOCUMENT_VAULT_COMPONET_NAME = "DocumentVault";
	public static final String DOCUMENT_VAULT_ALLOWED_CONTENT_SIZE_NAME = "ContentUpperLimit";
	public static final String DOCUMENT_VAULT_MAXIMUM_COUNT = "MaximumDocCount";
	public static final String DOCUMENT_VAULT_ALLOWED_OBJECT_PER_PAGE = "ObjectsPerPage";
	public static final String EVENT_TYPE_WORKFLOW = "WorkFlow";
	public static final String EVENT_TYPE_ASSIGN = "Task Assigned";
	public static final String EVENT_NAME_ASSIGN = "Task Assignment";
	public static final String PROPERTY_CE_ROOT_DOCUMENT_CLASS_NAME = "HHS_ACCELERATOR";

	// for component
	public static final String PROPERTY_PE_BOOTSTRAP_CE_URI = "filenet.pe.bootstrap.ceuri";
	public static final String PROPERTY_WEBCONTENTPATH = "WebContentPath";

	public static final String PRINTER_FRINDLY_VERSI_DOCUMENT_CLASS = "PRINTER_FRINDLY_VERSI";
	public static final String PROPERTY_PREDEFINED_PRINT_VIEW_SUB_FOLDER_PATH = "PRINT_VIEW_SUB_FOLDER_PATH";
	public static final String PROPERTY_PREDEFINED_LOG4J_PATH = "PROP_FILE_LOG4J_PATH";

	public static final String PROPERTY_PE_TASK_TYPE_ALL_APPLICATIONS = "All Applications";
	public static final String DOCUMENT_CLASS_HELP = "HELP";
	public static final String DOCUMENT_CLASS_SAMPLE = "HELP_SAMPLE";

	public static final String PROPERTY_CE_REPORT_TYPE = "ReportType";

	public static final String PROPERTY_CE_IS_PROVIDER_HELP_DOC = "HELP_DOCUMENT_FOR_PROVIDER";
	public static final String PROPERTY_CE_IS_AGENCY_HELP_DOC = "HELP_DOCUMENT_FOR_AGENCY";
	// R2 Task Constant
	public static final String PE_WORKFLOW_PROCUREMENT_TITLE = "ProcurementTitle";
	public static final String PE_WORKFLOW_PROVIDER_NAME = "ProviderName";
	public static final String PE_WORKFLOW_PROVIDER_ID = "ProviderID";
	public static final String PE_WORKFLOW_PROPOSAL_TITLE = "ProposalTitle";
	public static final String PE_WORKFLOW_PROCUREMENT_EPIN = "ProcurementEPin";
	public static final String PE_WORKFLOW_FIRST_ROUND_EVAL_DATE = "FirstRoundOfEvaluationCompleteDate";
	public static final String PE_WORKFLOW_TASK_TYPE = "TaskType";
	public static final String PE_WORKFLOW_ASSIGNED_TO = "TaskOwner";
	public static final String PE_WORKFLOW_ASSIGNED_TO_NAME = "TaskOwnerName";
	public static final String PE_WORKFLOW_DATE_ASSIGNED = "TaskAssignDate";
	public static final String PE_WORKFLOW_LAST_MODIFIED_DATE = "TaskModifiedDate";
	public static final String PE_WORKFLOW_PROCUREMENT_ID = "ProcurementID";
	public static final String PE_WORKFLOW_EVALUATION_STATUS_ID = "EntityID";
	public static final String PE_WORKFLOW_PROPOSAL_ID = "ProposalID";
	public static final String PE_WORKFLOW_TASK_ID = "TaskID";
	public static final String PE_WORKFLOW_TASK_VISIBILITY = "TaskVisibility";
	public static final String PE_ACCEPT_PROPOSAL_WORKFLOW_NAME = "WF201 - Accept Proposal";
	public static final String PE_WORKFLOW_AGENCY_ID = "AgencyID";
	public static final String PE_WORKFLOW_AGENCY_NAME = "AgencyName";
	public static final String PE_WORKFLOW_TASK_STATUS = "TaskStatus";
	public static final String PE_START_ACCEPT_PROPOSAL_WORKFLOW_NAME = "WF201 - Start Accept Proposal Workflows";
	public static final String PE_EVALUATION_PROPOSAL_MAIN_WORKFLOW_NAME = "WF202 - Evaluate Proposal Main";
	public static final String PE_EVALUATE_PROPOSAL_TASK_NAME = "WF202 - Evaluate Proposal";
	// public static final String PE_EVALUATE_SCORE_TASK_NAME =
	// "WF202 - Review Scores";
	public static final String PE_EVALUATE_AWARD_TASK_NAME = "WF203 - Award";
	public static final String PE_WORKFLOW_PROCUREMENT_PRIMARY_AGENCY_ID = "AgencyContact1";
	public static final String PE_WORKFLOW_PROCUREMENT_SECONDARY_AGENCY_ID = "AgencyContact2";
	public static final String PE_WORKFLOW_PROCUREMENT_AWARD_AMOUNT = "TotalAwardAmount";
	public static final String PE_AWARD_WORK_FLOW_SUBJECT = "Procurement Award WF";
	public static final String PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL = "Approve Award";
	public static final String PE_ACCEPT_PROPOSAL_TASK_NAME = "Accept Proposal";
	public static final String PE_EVALUATE_PROPOSAL_EVALUATION_ID = "EvaluationID";
	public static final String PE_EVALUATION_UTILITY_WORKFLOW_NAME = "WF20x - Utility Workflow";
	public static final String PE_WORKFLOW_SOCRE_RETURNED_STATUS = "Score Returned";
	public static final String PE_WORKFLOW_PROPOSAL_WORKFLOW_ID = "WorkflowNumber";
	public static final String PE_WORKFLOW_SELECTED_PROPOSAL_COUNT = "number_selected_proposals";
	public static final String PE_WORKFLOW_IS_FIRST_LAUNCH = "isFirstLaunch";
	public static final String PE_WORKFLOW_IS_FIRST_REACHED = "isFirstReached";
	public static final String PROPERTY_CE_IS_SYSTEM_DOC_CATEGORY = "IS_SYSTEM_DOCS";
	public static final String PE_WORKFLOW_TASK_STATUS_PREVIOUS = "TaskStatusPrevious";
	public static final String PROPERTY_CE_SOLICITATION_CATEGORY = "Solicitation";
	public static final String PE_WORKFLOW_CONTRACT_ID = "ContractID";
	// task types for agency inbox task count

	public static final String TASK_CONTRACT_CONFIGURATION = "Contract Configuration";
	public static final String TASK_CONTRACT_COF = "Contract Certification of Funds";
	public static final String TASK_BUDGET_REVIEW = "Contract Budget Review";
	public static final String TASK_INVOICE_REVIEW = "Invoice Review";
	public static final String TASK_PAYMENT_REVIEW = "Payment Review";
	public static final String TASK_EVALUATE_PROPOSAL = "Evaluate Proposal";
	public static final String TASK_ACCEPT_PROPOSAL = "Accept Proposal";
	public static final String TASK_CONFIGURE_AWARDS_DOCUMENTS = "Configure Award Documents";
	public static final String TASK_REVIEW_SCORES = "Review Scores";
	public static final String UNASSIGNED_ACCO_MANAGER = "Unassigned ACCO Manager";
	public static final String UNASSIGNED_ACCO_STAFF = "Unassigned ACCO Staff";
	public static final String UNASSIGNED_ALL_LEVELS = "Unassigned All Levels";

	public static final String ACTION_KEY_FOR_UTILITY_WORKFLOW = "Action";
	public static final String CANCEL_EVALUATION_ACTION_FOR_UTILITY_WORKFLOW = "CancelEvaluations";
	public static final String CANCEL_PROCUREMENT_ACTION_FOR_UTILITY_WORKFLOW = "CancelProcurement";
	public static final String CANCEL_AWARD_ACTION_FOR_UTILITY_WORKFLOW = "CancelAwardProcess";
	public static final String REASSIGN_EVALUATIONS = "ReassignEvaluations";
	public static final String FINALIZE_EVALUATION_DATE = "FinalizeEvaluationDate";
	public static final String AWARD_SELECTION_DATE = "AwardSelectionDate";
	// START || Changes made for enhancement 6574 for Release 3.10.0
	public static final String CANCEL_AFTER_AWARD_APPROVAL = "CancelAfterAwardApproval";
	// End || Changes made for enhancement 6574 for Release 3.10.0

	// START || Changes made for enhancement 6577 for Release 3.10.0
	public static final String CANCEL_COMPETITION = "cancelCompetition";
	// END || Changes made for enhancement 6577 for Release 3.10.0

	public static final String OBJECT = "Object";
	public static final String ORGANIZATION_TYPE = "oraganizationType";
	public static final String PE_WORKFLOW_PROCUREMENT_PRIMARY_AGENCY = "AgencyContact1Name";
	public static final String PE_WORKFLOW_PROCUREMENT_SECONDARY_AGENCY = "AgencyContact2Name";
	public static final String PE_WORKFLOW_PROCUREMENT_PRIMARY_ACC_ID = "AcceleratorContact1";
	public static final String PE_WORKFLOW_PROCUREMENT_SECONDARY_ACC_ID = "AcceleratorContact2";
	public static final String PE_WORKFLOW_PROCUREMENT_PRIMARY_ACC = "AcceleratorContact1Name";
	public static final String PE_WORKFLOW_PROCUREMENT_SECONDARY_ACC = "AcceleratorContact2Name";
	public static final String PE_WORKFLOW_LIST_OF_SELECTED_PROVIDERS = "ListOfSelectedProviders";
	public static final String PE_WORKFLOW_LIST_OF_NOT_SELECTED_PROVIDERS = "ListOfNotSelectedProviders";
	public static final String PE_WORKFLOW_COMPETITION_POOL_TITLE = "CompetitionPoolTitle";
	public static final String EVALUATION_GROUP_ID = "EvaluationGroupId";
	public static final String EVALUATION_POOL_MAPPING_ID = "EvaluationPoolMappingId";
	public static final String IS_OPEN_ENDED_RFP = "IsOpenEndedRfp";
	public static final String PROPERTY_PE_EVAL_GRP_TITLE = "EvaluationGroupTitle";

	// Start || Added for enhancement 6574 for Release 3.10.0
	public static final String PE_WORKFLOW_LIST_OF_SELECTED_CONTRACTS = "ListOfSelectedContracts";
	// End || Added for enhancement 6574 for Release 3.10.0

	public static final String PDF_FILE_PATH = "PDF_FILE_PATH";
	public static final String OBJECT_STORE_NAME = "ObjectStoreName";
	public static final String IS = "aoIS";
	public static final String DB_EXIST = "abDocExist";
	public static final String CHECK_LIST = "abCheckExist";
	public static final String DOC_TYPE = "lsDocType";
	public static final String DOC_TITLE = "lsDocTitle";
	public static final String FIELD_PATH = "lsFldPath";
	public static final String LS_PROVIDER_ID = "lsProviderId";
	public static final String MO4 = "M04";
	public static final String LINKED_APP = "lbLinkedToApp";
	public static final String DOC_CLASS_NAME = "lsDocClassName";
	public static final String ORG_ID = "lsOrgId";
	public static final String AS_PROVIDER_ID = "asProviderId";
	public static final String AS_DOC_TITLE = "asDocTitle";
	public static final String AS_DOC_TYPE = "asDocType";
	public static final String AO_ORG_ID = "aoOrgId";
	public static final String AS_DOC_CLASS_NAME = "asDocClassName";
	public static final String AS_PROVIDER_IDS = "asProviderID";
	public static final String AS_ORG_ID = "asOrgId";
	public static final String LS_FOLDER_PATH = "lsFolderPath";
	public static final String AS_DOC_ID = "asDocId";
	public static final String DOCUMENTS_LIST = "aoDocumentsList";
	public static final String PROVIDER_AGENCY_MAP = "aoProviderAgencyMap";
	public static final String SHARE_DOCUMENTS = "aoShareDocuments";
	public static final String HM_REQ_PROPS = "aoHmReqProps";
	public static final String FILTER_MAP = "aoFilterMap";
	public static final String ORDER_BY_MAP = "aoOrderByMap";
	public static final String FILTER_INCLUDED = "abFilterIncluded";
	public static final String DOCUMENT_PROPERTIES = "Document Properties";
	public static final String DOCUMENT_IDS = "documentId";
	public static final String FINANCIAL_PDF_DOC = "FINANCIAL_PDF_DOC";
	public static final String AO_HM = "aoHm";
	public static final String APPLICATION_PDF = "application/pdf";
	// added for R5 module Manage Organization - start
	public static final String PROPERTY_PE_TASK_TYPE_APPROVE_PSR = "Approve PSR";
	public static final String PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL_AMOUNT = "Approve Award Amount";
	public static final String PROPERTY_CE_PSR_ID = "PSR_id";
	public static final String PROPERTY_PE_TASK_TYPE_COMPLETE_PSR = "Complete PSR";
	public static final String PROPERTY_PE_TASK_TYPE_FINALIZE_AWARD_AMOUNT = "Finalize Award Amount";
	public static final String PROPERTY_PE_TASK_TYPE_APPROVE_AWARD_AMOUNT = "Approve Award Amount";
	public static final String PROPERTY_WORKFLOW_IS_NEGOTIATION_REQUIRED = "IsNegotiationRequired";
	// added for R5 module Manage Organization - end
	public static final String DOCUMENT_VAULT_RESTRICTED_DOCTYPE = "RestrictedDocType";
	public static final String PROPERTY_CE_DELETE_FLAG = "DELETE_FLAG";
	// R5 changes ends

}
