/**
\ * Common Constant class for entire Application for R2, R3
 */
package com.nyc.hhs.constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractedServicesBean;
import com.nyc.hhs.model.RefContractFMSBean;

public class HHSConstants extends ApplicationConstants
{

	public static final String SQL_CONFIG = "com/nyc/hhs/config/SqlMapConfigR2.xml";
	public static final String FILENET_PE_SQL_CONFIG = "com/nyc/hhs/config/FilenetSqlMapConfig.xml";
	public static final String LOCAL_SQL_CONFIG = "com/nyc/hhs/config/BatchSqlMapConfigR2.xml";
	public static final String LOCAL_FILENET_SQL_CONFIG = "com/nyc/hhs/config/FilenetSqlMapConfigR2.xml";
	public static final String TRANSACTION_ELEMENT = "transactionR2";
	public static final String TRANSACTION_ELEMENT_COMPONENT = "transactionComponent";
	public static final String CONCURRENCY_CONFIG_CACHE_KEY = "ConcurrencyConfig";
	public static final String TRANSACTION_CONFIGR2_PATH = "/r2_src/com/nyc/hhs/config/TransactionConfigR2.xml";
	public static final String TRANSACTION_ELEMENT_PATH = "/com/nyc/hhs/config/TransactionConfigR2.xml";
	public static final String TRANSACTION_CONTRACTS_ELEMENT_PATH = "/com/nyc/hhs/config/ContractsBatchTransactionConfig.xml";
	public static final String CACHE_FILES = "com.nyc.hhs.properties.cachenew";
	public static final String FILENET_CREDENTIALS_PROPERTY_FILE = "com.nyc.hhs.properties.filenetcredentials";
	public static final String SM_HEADER_PATH = "/WEB-INF/r2/jsp/procurement/topLevelHeader.jsp";
	public static final String USESLOCALDBCONNECTION = "useslocaldbconnection";
	public static final String USESDBCONNECTION = "usesdbconnection";
	public static final String COHERENCE_ELEMENT = "coherenceElement";
	public static final Integer INT_MAX_ALLOWED_NUM_FY = 9;
	public static final String AO_BUDGETS_COUNT = "aoBudgetsCount";
	public static final String FETCH_COUNTRACT_BUDGET_COUNT_FOR_FY = "fetchCountractBudgetCountForFY";
	public static final String MSG_INVALID_CONTRACT_START_END_DATES = "MSG_INVALID_CONTRACT_START_END_DATES";
	public static final String MSG_INVALID_AMENDMENT_START_END_DATES = "MSG_INVALID_AMENDMENT_START_END_DATES";
	public static final String MSG_INVALID_AMENDMENT_END_DATE = "MSG_INVALID_AMENDMENT_END_DATE";
	public static final String MSG_INVALID_AMENDMENT_END_DATE_NEW = "MSG_INVALID_AMENDMENT_END_DATE_NEW";
	public static final String MSG_INVALID_AMENDMENT_START_DATE = "MSG_INVALID_AMENDMENT_START_DATE";
	public static final String MSG_INVALID_PROPOSED_SPAN = "MSG_INVALID_PROPOSED_SPAN";
	public static final String MSG_INVALID_CONTRACT_END_DATE = "MSG_INVALID_CONTRACT_END_DATE";	
	public static final String MSG_INVALID_INVOICE_DATE = "SERVICE_DATE_FRM_BFR_SRVC_DATE_TO";	
	public static final String MSG_INVALID_SERVICE_DATE_FROM = "SERVICE_DATE_FROM";	
	public static final String MSG_INVALID_SERVICE_DATE_TO = "SERVICE_DATE_TO";
	

	public static final String HANDLER_INSTANCE = "HANDLER_INSTANCE";
	public static final String RULE_FLAG = "RULE_FLAG";
	public static final String FILENET_URI = "FILENET_URI";
	public static final String OBJECT_STORE_NAME = "OBJECT_STORE_NAME";
	public static final String CE_USER_ID = "CE_USER_ID";
	public static final String CE_PASSWORD = "CE_PASSWORD";

	// PaymentList Controller Constants
	public static final String PY_ERROR_OCCURED_WHILE_PROCESSING_PAYMENTS_CT_ID_TYPE_HEAD = "Error occured while processing payments CT id Type head";
	public static final String PY_PAYMENT_LIST_ACTION = "paymentListAction";
	public static final String PY_ERROR_OCCURRED_WHILE_GETTING_PAYMENT_LIST_SCREEN_FOR_ORG_TYPE = "Error occurred while getting payment list screen for org type: ";
	public static final String PY_AO_PAYMENT_LIST_SIZE = "aoPaymentListSize";
	public static final String PY_AO_PAYMENT_STATUS = "aoPaymentStatus";
	public static final String AO_AGENCY_LIST = "aoAgencyList";
	public static final String PY_AO_FISCAL_INFORMATION = "aoFiscalInformation";
	public static final String PY_LO_PROGRAM_NAME_LIST = "loProgramNameList";
	public static final String PY_LI_PAYMENT_LIST_COUNT = "liPaymentListCount";
	public static final String PY_AO_PAYMENT_LIST = "aoPaymentList";
	public static final String ERROR_WHILE_PROCESSING_REQUEST = "The transaction was not successful due to technical difficulties. Please contact the system administrator.";
	public static final String ERROR_BUDGET_TYPE = "budgetMessageType";
	public static final String PY_GET_FINANCIALS_PAYMENT_LIST = "getFinancialsPaymentList";
	public static final String PY_AO_PAYMENT_BEAN = "aoPaymentBean";
	public static final String PY_PAYMENT_LIST = "paymentList";
	public static final String PROCUREMENT_CONTRACT_TITLE = "procurementContractTitle";

	public static final String FETCH_PROVIDER_ID_FIRST_ROUND = "fetchProviderIdFirstRound";
	public static final String FETCH_PROVIDER_ID_SECOND_ROUND = "fetchProviderIdSecondRound";

	// Mappers
	public static final String MAPPER_CLASS_PROCUREMENT_MAPPER = "com.nyc.hhs.service.db.services.application.ProcurementMapper";
	public static final String MAPPER_CLASS_CONTRACT_MAPPER = "com.nyc.hhs.service.db.services.application.ContractMapper";
	public static final String MAPPER_CLASS_CONTRACT_BUDGET_MAPPER = "com.nyc.hhs.service.db.services.application.ContractBudgetMapper";
	public static final String MAPPER_CLASS_CONTRACT_BUDGET_MODIFICATION_MAPPER = "com.nyc.hhs.service.db.services.application.ContractBudgetModificationMapper";
	public static final String MAPPER_CLASS_CONTRACT_BUDGET_AMENDMENT_MAPPER = "com.nyc.hhs.service.db.services.application.ContractBudgetAmendmentMapper";
	public static final String MAPPER_CLASS_CONTRACT_BUDGET_INVOICING_MAPPER = "com.nyc.hhs.service.db.services.application.ContractBudgetInvoicingMapper";
	public static final String MAPPER_CLASS_AGENCY_SETTINGS = "com.nyc.hhs.service.db.services.application.AgencySettingsMapper";
	public static final String MAPPER_CLASS_FINANCIAL_PAYMENT_MAPPER = "com.nyc.hhs.service.db.services.application.PaymentMapper";
	public static final String MAPPER_CLASS_INVOICE_MAPPER = "com.nyc.hhs.service.db.services.application.InvoiceMapper";
	public static final String MAPPER_CLASS_BUDGET_MAPPER = "com.nyc.hhs.service.db.services.application.BudgetMapper";
	public static final String MAPPER_CLASS_AWARDS_MAPPER = "com.nyc.hhs.service.db.services.application.AwardsMapper";
	public static final String MAPPER_CLASS_EVALUATION_MAPPER = "com.nyc.hhs.service.db.services.application.EvaluationMapper";
	public static final String MAPPER_CLASS_TASK_SERVICE_MAPPER = "com.nyc.hhs.service.db.services.application.TaskServiceMapper";
	public static final String MAPPER_CLASS_HHS_AUDIT_MAPPER = "com.nyc.hhs.service.db.services.application.HhsAuditMapper";
	public static final String MAPPER_CLASS_PROPOSAL_MAPPER = "com.nyc.hhs.service.db.services.application.ProposalMapper";
	public static final String MAPPER_CLASS_COMMON_MAPPER = "com.nyc.hhs.service.db.services.application.CommonMapper";
	public static final String MAPPER_CLASS_RFP_RELEASE_MAPPER = "com.nyc.hhs.service.db.services.application.RFPReleaseMapper";
	public static final String MAPPER_CLASS_CONFIGURATION_MAPPER = "com.nyc.hhs.service.db.services.application.ConfigurationMapper";
	public static final String MAPPER_CLASS_APPROVED_PROV_BATCH_MAPPER = "com.nyc.hhs.service.db.services.application.ApprovedProviderBatchMapper";
	public static final String MAPPER_CLASS_TAXONOMY_TAGGING_MAPPER = "com.nyc.hhs.service.db.services.application.TaxonomyTaggingMapper";
	public static final String MAPPER_CLASS_PAYMENT_MODULE_MAPPER = "com.nyc.hhs.service.db.services.application.PaymentModuleMapper";

	public static final String FETCH_INVOICING_SUMMARY_METHOD = "fetchInvoiceSummary";
	public static final String ADD_CONTEXT_DATA_SUB_BUDGET = "sub budget id";
	public static final String ADD_CONTEXT_DATA_BUDGET = "budget id";

	public static final String GET_CONTRACT_INFO = "getContractInfo";

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
	public static final String AUDITOR_ROLE = "AUDITOR"; //jm

	public static final List<String> ACCO_USER_ROLES = new ArrayList<String>();
	public static final String RENEW_CONTRACT_DATE_VALIDATION = "renewContractDateValidation";
	public static final String RENEW_CONTRACT_DATE_VALIDATION_ERROR = "RENEW_CONTRACT_DATE_VALIDATION_ERROR";
	public static final String FETCH_MODIFICATION_SUB_BUDGET_SUMMARY = "fetchModificationSubBudgetSummary";
	public static final String FETCH_UPDATE_SUB_BUDGET_SUMMARY = "fetchUpdateSubBudgetSummary";

	static
	{
		ACCO_USER_ROLES.add(ACCO_MANAGER_ROLE);
		ACCO_USER_ROLES.add(ACCO_STAFF_ROLE);
		ACCO_USER_ROLES.add(ACCO_ADMIN_STAFF_ROLE);
	}

	public static final List<String> PROGRAM_USERS_ROLES = new ArrayList<String>();
	static
	{
		PROGRAM_USERS_ROLES.add(PROGRAM_STAFF_ROLE);
		PROGRAM_USERS_ROLES.add(PROGRAM_ADMIN_STAFF_ROLE);
		PROGRAM_USERS_ROLES.add(PROGRAM_MANAGER_ROLE);
	}

	public static final List<String> FINANCE_USERS_ROLES = new ArrayList<String>();
	static
	{
		FINANCE_USERS_ROLES.add(FINANCE_STAFF_ROLE);
		FINANCE_USERS_ROLES.add(FINANCE_ADMIN_STAFF_ROLE);
		FINANCE_USERS_ROLES.add(FINANCE_MANAGER_ROLE);
	}
	public static final String CONTRACT_ID_WORKFLOW = "contractId";
	public static final String AMEND_CONTRACT_ID_WORKFLOW = "amendcontractid";
	public static final String CONTRACT_ID_ORIGINAL = "contractIdOrig";

	public static final String PAYMENT_ID_WORKFLOW = "paymentId";
	public static final String PAYMENT_ID = "paymentId";
	public static final String BUDGET_ADVANCE_ID_WORKFLOW = "budgetAdvanceId";

	public static final String TAXONOMY_TAGGING_MAP = "taxonomyTaggingMap";
	public static final String FETCH_TAXONOMYTAGGING_LIST = "fetchTaxonomyTaggingList";
	public static final String FETCH_TAXONOMYTAGGING_IN_BULK_LIST = "fetchTaxonomyTaggingInBulkList";
	public static final String AO_TAXONOMY_TAGGING_LIST = "aoTaxonomyTaggingList";
	public static final String TAXONOMY_TAGGING_LIST = "taxonomyTaggingList";
	public static final String TAXONOMY_TAGGING_ID = "taxonomyTaggingId";
	public static final String TAXONOMY_TAGGING_ID_FOR_FETCH = "taxonomyTaggingIdForFetch";
	public static final String TAG_ALL_SELECTED_PROPOSAL_IN_BULK = "tagAllSelectedProposalsInbulk";
	public static final String LINKED_ID_FOR_SAVE_IN_BULK = "lsLinkedId";
	public static final String TAXONOMY_TAGGING_SELECTED_SERVICE_FUNCTION = "taxonomytagging/selectedservicefunction";
	public static final String AO_TAXONOMY_TREE = "aoTaxonomyTree";
	public static final String SAVE_TAGGING_DETAILS = "saveTaggingDetails";
	public static final String SAVE_TAGGING_DETAILS_IN_BULK = "saveTaggingDetailsInBulk";
	public static final String SAVE_ALL_SELECTED_PROPOSALS_IN_BULK = "saveAllSelectedProposalsInbulk";
	// Keys
	public static final String PROPOSAL_ID = "proposalId";
	public static final String PROCUREMENT_ID = "procurementId";
	public static final String PROPOSAL_ID_KEY = "asProposalId";
	public static final String LINKAGE_BRAND_ID = "linkageBranchId";
	public static final String ON_FIRST_LOAD = "onFirstLoad";
	public static final String PROCUREMENT_ID_KEY = "asProcurementId";

	public static final String PROVIDER_ID_KEY = "asProviderId";
	public static final String PROVIDER_ID = "providerId";
	public static final String PROCUREMENT_STATUS_KEY = "asProcurementStatus";
	public static final String SERVICE_MAP = "serviceMap";
	public static final String PROC_MAP = "loProcMap";
	public static final String TRANSACTION_SUCCESS = "TransactionSuccess";
	public static final String LS_PROCUREMENT_VALUE = "lsProcurementValue";
	public static final String FETCH_PROCUREMENT_VALUE = "fetchProcurementValue";
	public static final String FETCH_AWARD_AMOUNT = "fetchAwardAmount";
	public static final String COUNT_FINALIZE_PROCUREMENT_DETAILS = "countFinalizeProcurementDetils";
	public static final String FETCH_FINALIZE_PROCUREMENT_COUNT = "fetchFinalizeProcurementCount";
	public static final String FINALIZE_PROCUREMENT = "finalizeProcurement";
	public static final String UPDATE_FINALIZE_PROCUREMENT = "updateFinalizeProcurement";
	public static final String MARK_PROPOSAL_NON_RESPONSIVE = "markProposalNonResponsive";

	public static final String SORT_MASTER_DETAILS = "sortDetails";
	public static final String RENDER_ACTION = "render_action";
	public static final String ACTION_MAPPING_FILE_NAME = "financeListMapping";
	public static final String PROPOSAL_ACTION_MAPPING_FILE_NAME = "proposalStatusToActionMapping";
	public static final String EVALUATION_ACTION_MAPPING_FILE_NAME = "EvaluationStatusToActionStatus";
	public static final String PROCUREMENT_SESSION_BEAN = "sessionProcurementBean";
	public static final String EVALUATION_SESSION_BEAN = "sessionEvaluationtBean";
	public static final String EVALUATION_SESSION_FILTER_BEAN = "sessionEvaluationfilterBean";
	public static final String CONTRACT_SESSION_BEAN = "contractProcurementBean";
	public static final String AMENDED_CONTRACT_SESSION_BEAN = "amendedContractProcurementBean";
	public static final String INVOICE_SESSION_BEAN = "invoiceProcurementBean";
	public static final String PROCUREMENT_LIST = "procurementList";
	public static final String EVALUATION_LIST = "EvaluationList";

	public static final String BUDGET_ID_KEY = "asBudgetId";
	public static final String FISCAL_YEAR_ID_ADD_SITE = "FISCAL_YEAR_ID_ADD_SITE";
	public static final String FISCAL_YEAR_ID_ADD_SITE_KEY = "FISCAL_YEAR_ID_AddSite";
	public static final String LS_SUB_BUDGET_STATUS_ID = "lsSubBudgetStatusId";
	public static final String BASE_PRINTER_FRIENDLY = "hdnIsPrinterFriendly";
	public static final String SUBBUDGET_ID_KEY = "asSubBudgetId";
	public static final String INVOICE_ID_KEY = "asInvoiceId";
	public static final String TOTAL_COUNT = "totalCount";
	public static final String FEDERAL = "Federal";
	public static final String CITY = "City";
	public static final String STATE = "State";
	public static final String OTHER = "Other";
	public static final String SELECTED_AGENCY = "selectedAgency";
	public static final String PROCUREMENT_ROADMAP = "procurementroadmap";
	public static final String PROCUREMENT_ROADMAP_KEY = "ProcurementRoadmap";
	public static final String CONTRACT_LIST_KEY = "ContractList";
	public static final String AMENDMENR_LIST_KEY = "AmendmentList";
	public static final String AMENDED_CONTRACT_LIST_KEY = "AmendedContractList";
	public static final String CONTRACT_STATUS = "contractStatus";
	public static final String CONTRACTS_VALUE = "contractsValue";
	public static final String PAYMENT_LIST_KEY = "PaymentList";
	public static final String ALL_NYC_AGENCIES = "All NYC Agencies";
	public static final String FETCH_ACTIVE_PROCUREMENTS = "fetchActiveProcurements";
	public static final String FETCH_NEXT_CONTRACTS = "fetchNextContracts";
	public static final String FETCH_NEXT_AMEND_CONTRACTS = "fetchNextAmendContracts";
	public static final String FILTER_CONTRACTS = "filterContracts";
	public static final String FILTER_AMENDED_CONTRACTS = "filterAmendedContracts";
	public static final String RFP_RELEASED_DATE = "rfpReleaseUpdatedDate";
	public static final String FETCH_NEXT_INVOICES = "fetchNextInvoices";
	public static final String INVOICE_ACTION_MAPPING = "financeInvoiceMapping";
	public static final String PROGRAM_NAME_LIST = "programNameList";
	public static final String BUDGET_SESSION_BEAN = "budgetBean";
	public static final String SORT_BUDGET_LIST = "sortBudgetList";
	public static final String WITHDRAW_INVOICE = "withdrawInvoice";
	public static final String NEXT_PAGE = "nextPage";
	public static final String ADD_PROCUREMENT = "addProcurement";
	public static final String AGENCY_DETAILS = "agencyDetails";
	public static final String SORT_CONTRACT_LIST = "sortContractList";
	public static final String SORT_AMEND_CONTRACT_LIST = "sortAmendContractList";
	public static final String INVOICE_LIST_PAGE = "InvoiceList";
	public static final String SORT_TYPE = "sortType";
	public static final String IS_AJAX = "isAjax";
	public static final String COLUMN_NAME = "columnName";
	public static final String SORT_BY = "sortBy";
	public static final String SORT_GRID_NAME = "sortGridName";
	public static final String ASCENDING = "ASC";
	public static final String DESCENDING = "DESC";
	public static final String AGENCY_ID = "Agency_ID";
	public static final String STATUS = "STATUS_ID";
	public static final String STATUS_PROCESS_TYPE_ID = "STATUS_PROCESS_TYPE_ID";
	public static final String STATUS_COLUMN = "status";
	public static final String UPDATED_RFP_RELEASE_DATE = "Upd_RFP_Release_Date";
	public static final String UPD_PROPOSAL_DUE_DATE = "UPD_PROPOSAL_DUE_DATE";
	public static final String LAST_UPDATE_DATE = "LAST_UPDATE_DATE";
	public static final String BUDGET_ACTION_MAPPING = "financeBudgetMapping";
	public static final String DISBURSEMENT_DATE = "disbursement_date";
	public static final String PAYMENT_STATUS_COLUMN = "paymentStatus";
	public static final String CHECK_EFT_NUMBER = "check_eft_num";
	public static final String PAYMENT_VOUCHER_NUMBER = "payment_voucher_num";
	public static final String SORT_PAYMENT = "sortPayment";
	public static final String SORT_INVOICE = "sortInvoice";
	public static final String SORT_INVOICE_LIST = "sortInvoiceList";
	public static final String VIEW_INVOICE = "viewInvoice";
	public static final String PAYMENT_SESSION_BEAN = "sessionPaymentBean";
	public static final String INVOICE_NUMBER = "invoiceNumber";
	public static final String INVOICE_NUMBER_SORT = "invoice_number";
	public static final String NON_AUDIT_COMMENTS = "nonAuditComments";
	public static final String PROVIDER_AUDIT = "providerAudit";
	public static final String AGENCY_AUDIT = "agencyAudit";
	public static final String ACCELERATOR_AUDIT = "acceleratorAudit";
	public static final String BUDGET_LIST_PAGE = "BudgetList";
	public static final String FILTER_BUDGET = "filterBudget";
	public static final String SELECTED_CHILD_TAB = "selectedChildTab";
	public static final String WORKFLOW_STATUS = "WFStatus";
	public static final String RFP_RELEASE = "rfpRelease";
	public static final String REVIEW_PROC_ID = "reviewProcID";
	public static final String ORIGINAL_CONTRACT_ID = "OriginalContractId";
	// FOR PAYMENT LIST
	public static final String PAYMENT_CONTRACT_TITLE = "paymentContractTitle";
	public static final String PAYMENT_LAST_UPDATE_DATE = "paymentLastUpdateDate";
	public static final String PAYMENT_CT_ID = "paymentCtId";
	public static final String PAYMENT_VOUCHER_NUM = "paymentVoucherNumber";
	public static final String PAYMENT_DIS_DATE = "paymentDisDate";
	public static final String PAYMENT_VALUE = "paymentValue";
	public static final String PAYMENT_PROCUREMENT_TITLE = "paymentProcurementTitle";
	// FOR CONTRACT BUDGET LIST
	public static final String CBL_EXCEPTION_REQUEST_ADVANCE_INFO = "Exception Occured while calling Transaction fetchRequestAdvanceInfo ";
	public static final String CBL_LO_BUDGET_ADVANCE_BEAN = "loBudgetAdvanceBean";
	public static final String CBL_FETCH_REQUEST_ADVANCE_INFO = "fetchRequestAdvanceInfo";
	public static final String CBL_IO_EXCEPTION_SEARCHING_PROVIDERS = "IOException occurred while searching providers:";
	public static final String CONTRACT_NO_LIST = "contractNoList";
	public static final String FETCH_CONTRACT_NO_LIST = "fetchContractNoList";
	public static final String FETCH_AMEND_CONTRACT_NO_LIST = "fetchAmendContractNoList";
	public static final String CBL_FETCH_BUDGET_NO_LIST = "fetchBudgetNoList";
	public static final String CBL_BUDGET_NO_QUERY_ID = "budgetNoQueryId";
	public static final String CBL_EXCEPTION_CONTRACT_BUDGET_DETAILS = "Exception Occured while displaying Contract Budget details : ";
	public static final String CBL_EXCEPTION_OCCURED_WHILE_RETURNING_MESSAGE = "Exception occured while returning message";
	public static final String CBL_EXCEPTION_UPDATE_ADVANCE_DETAILS = "Exception Occured while calling Transaction updateAdvanceDetails ";
	public static final String CBL_APPLICATION_EXCEPTION_UPDATE_ADVANCE_DETAILS = "ApplicationException occured in while calling Transaction updateAdvanceDetails";
	public static final String CBL_UPDATE_ADVANCE_DETAILS = "updateAdvanceDetails";
	public static final String CBL_AO_BUDGET_ADVANCE_BEAN = "aoBudgetAdvanceBean";
	public static final String CBL__83 = "83";
	public static final String CBL_CITY_142 = "city_142";
	public static final String CBL_BUDGET_LIST = "budgetList";
	public static final String CBL_ERROR_OCCURED_IN_GET_EPIN_LIST_RESOURCE_REQUEST = "Error occured in getEpinListResourceRequest method while fetching Epin  ";
	public static final String CBL_APPLICATION_EXCEPTION_GET_EPIN_LIST = "ApplicationException occured in getEpinListResourceRequest method while fetching Epin ";
	public static final String CBL_EXCEPTION_OCCURED_IN_ACTION_CANCEL_BUDGET_MODIFICATION = "Exception occured in actionCancelBudgetModification ";
	public static final String CBL_AS_ERROR = "asError";
	public static final String CBL_GET_MODIFY_BUDGET_FEASIBILITY = "getModifyBudgetFeasibility";
	public static final String CBL_CONFIRM_BUDGET_MODIFICATION = "confirmBudgetModification";


    /** Start QC9149  R 7.7.0 */
    public static final String CBL_NEGATIVE_AMENDMENT_CNT = "negativeAmendCnt";
    public static final String CBL_ADVANCE_WITH_NEGATIVE_AMEND = "Advance_Negative_Amend";
    public static final String SUBMIT_ADVANCE_FAILURE = "! Cannot process advance while a negative amendment is in progress and the amendment budget has not been approved.";
    /** End QC9149  R 7.7.0 */

	public static final String CBL_INITIATE_ADVANCE = "Initiate Advance";
	public static final String CBL_REQUEST_ADV = "Request Advance";
	public static final String CBL_BUDGET_ADVANCE = "budgetAdvance";
	public static final String CBL_BUDGET_ADVANCE_BEAN = "BudgetAdvanceBean";
	public static final String CBL_MESSAGE = "message";
	public static final String ERROR_MESSAGE_BUDGET_LIST = "messageBudgetList";
	public static final String CBL_LI_BUDGET_LIST_COUNT = "liBudgetListCount";
	public static final String CBL_AO_CB_BUDGET_IT_LIST_BEAN = "aoCBBudgetItListBean";
	public static final String CBL_GET_FINANCIALS_BUDGET_LIST = "getFinancialsBudgetList";
	public static final String CBL_CANCEL_BUDGET_MODIFICATION = "cancelBudgetModification";
	public static final String CBL_CANCEL_MODIFICATION = "Cancel Modification";
	public static final String CBL_REQUEST_ADVANCE = "requestAdvance";
	public static final String CBL_BUDGET_ADVANCE_REQUEST_DATE = "budgetAdvanceRequestDate";
	public static final String CBL_BUDGET_DATE_OF_LAST_UPDATE = "budgetDateOfLastUpdate";
	public static final String CLC_CT_NO = "ct";
	public static final String CBL_BUDGET_TITLE = "budgetTitle";
	public static final String CBL_BUDGET_LIST_ACTION = "budgetListAction";
	public static final String CBL_OVERLAY_PAGE_PARAM_BUDGET = "OverlayPageParamBudget";
	public static final String CBL_OVER_LAY_BUDGET = "OverLayBudget";
	public static final String PY_ERROR_OCCURED_WHILE_PROCESSING_PAYMENTS = "Error occured while processing payments";
	public static final String CBL_ERROR_OCCURED_WHILE_PROCESSING_BUDGETS = "Error occured while processing budgets";
	public static final String CBL_AO_BUDGET_LIST_LABEL = "aoBudgetListLabel";
	public static final String CBL_FINANCIALS_BUDGET_LIST = "financialsBudgetList";
	public static final String FETCH_AWARD_EPIN_DETAILS = "fetchAwardEPinDetails";
	public static final String LO_AWARD_EPIN_DETAILS = "loAwardEpinDetails";
	public static final String AWARD_EPIN_DETAILS = "awardEPinDetails";
	public static final String LO_AMOUNT_PROVIDER_DETAILS = "loAmountProviderDetails";
	public static final String AMOUNT_PROVIDER_DETAILS = "amountProviderDetails";
	public static final String ASSIGN_AWARD_EPIN = "assignAwardEpin";
	public static final String FETCH_AMOUNT_PROVIDER_DETAILS = "fetchAmountProviderDetails";
	public static final String FAILURE = "Failure|";
	public static final String TAXONOMY_TREE = "taxonomyTree";
	public static final String SERVICE_FUNCTION_LIST = "serviceFunctionList";
	public static final String TAXONOMY_HIDDEN_TREE = "taxonomyHiddenTree";
	public static final String APPLICATION_JSON = "application/json";
	// FOR PAYMENT LIST SERVICE
	public static final String PL_PAYMENT_AGENCY = "paymentAgency";
	public static final String PL_PROGRAM_NAME = "Error while fetching  Program Name for Organization Id:";
	public static final String PL_FETCHING_PROGRAM_NAME_LIST = "Exception occured while fetching Program Name List";
	public static final String PL_PROGRAM_NAME_INFORMATION = "Program Name information fetched successfully for user Type:";
	public static final String PL_GET_PROGRAM_NAME = "getProgramName";
	public static final String PL_GET_PROGRAM_NAME_AGENCY = "getProgramNameAgency";
	public static final String PL_AGENCY_LIST = "Error while fetching  agency list for Organization Id:";
	public static final String PL_EXCEPTION_AGENCY_LIST = "Exception occured while fetching agency list";
	public static final String PL_GET_AGENCY_LIST = "getAgencyList";
	public static final String PL_AGENCY_LIST_INFORMATION = "Agency List information fetched successfully for user Type:";
	public static final String PL_ERROR_STATUS_INFORMATION = "Error while fetching Status information for Organization Id:";
	public static final String PL_EXCEPTION_STATUS_INFORMATION = "Exception occured while fetching Status information";
	public static final String PL_STATUS_INFORMATION = "Status information fetched successfully for user Type:";
	public static final String PL_GET_STATUS_LIST = "getStatusList";
	public static final String PL_ERROR_FISCAL_YEAR_INFORMATION = "Error while fetching Fiscal Year information for Organization Id:";
	public static final String PL_EXCEPTION_FISCAL_YEAR_INFORMATION = "Exception occured while fetching Fiscal Year information ";
	public static final String PL_ORGANIZATION_ID = "Organization Id";
	public static final String PL_FISCAL_YEAR_INFORMATION = "Fiscal Year information fetched successfully for user Type:";
	public static final String PL_GET_FISCAL_INFORMATION = "getFiscalInformation";
	public static final String PL_ERROR_WHILE_FETCHING_PAYMENT_COUNT_FOR_USER_TYPE = "Error while fetching Payment count for user Type:";
	public static final String PL_EXCEPTION_OCCURED_WHILE_FETCHING_PAYMENT_COUNT = "Exception occured while fetching Payment count";
	public static final String PL_PAYMENT_COUNT_FETCHED_SUCCESSFULLY_FOR_USER_TYPE = "Payment count fetched successfully for user Type:";
	public static final String PL_GET_PAYMENT_COUNT_CITY = "getPaymentCountCity";
	public static final String PL_GET_PAYMENT_COUNT_AGENCY = "getPaymentCountAgency";
	public static final String PL_GET_PAYMENT_COUNT_PROVIDER = "getPaymentCountProvider";
	public static final String PL_AS_PAYMENT_ID = "asPaymentId";
	public static final String PL_FETCHING_PAYMENT = "Error while fetching Payment details for user Type:";
	public static final String PL_FETCHING_PAYMENT_DETAILS = "Exception occured while fetching Payment details";
	public static final String PL_PAYMENT_DETAILS = "Payment details fetched successfully for user Type:";
	public static final String PL_FETCH_PAYMENT_LIST_SUMMARY_CITY = "fetchPaymentListSummaryCity";
	public static final String PL_FETCH_PAYMENT_LIST_SUMMARY_AGENCY = "fetchPaymentListSummaryAgency";
	public static final String PL_PAYMENT_SORT_AND_FILTER = "com.nyc.hhs.model.PaymentSortAndFilter";
	public static final String PL_FETCH_PAYMENT_LIST_SUMMARY_PROVIDER = "fetchPaymentListSummaryProvider";
	// for taxonomy tagging
	public static final String TT_HIDDENCONTRACTBULKID = "hiddenContractIdBulk";
	public static final String TT_HIDDENPROPOSALBULKID = "hiddenProposalIdBulk";
	public static final String TT_HIDDENPROCUREMENTBULKID = "hiddenProcurementIdBulk";
	public static final String TT_SERVICEID = "serviceId";
	public static final String TT_MODIFIERID = "modifierIds";
	public static final String TT_SERVICEIDARRAY = "loServiceIdArray";
	public static final String TT_MODIFIERIDSARRAY = "loModifierIdsArray";
	public static final String TT_CONTRACTIDBULKARRAY = "loContractIdBulkArray";
	public static final String TT_PROPOSALIDBULKARRAY = "loProposalIdBulkArray";
	public static final String TT_PROCUREMENTIDBULKARRAY = "loProcurementIdArray";
	public static final String TT_USERID = "userId";
	public static final String TT_DELETEDTAXONOMYTAGGINGID = "deletedTaxonomyTaggingId";
	public static final String TT_TAXONOMYTAGGINGIDS = "taxonomyTaggingIds";
	public static final String TT_HIDDENDELETEDTAGS = "hiddenDeletedTags";
	public static final String TT_TAXONOMYTAGGINGID = "taxonomyTaggingId";
	public static final String TT_GETTAXONOMYTAGGINGIDS1 = "getTaxonomyTaggingIds1";

	// constants for contract budget summary page
	public static final String BUDGET_SUMMARY = "budgetSummary";
	public static final String INDIRECT_PERCENTAGE = "indirectPercentage";
	public static final String CONTRACT_BUDGET_SUMMARY = "contractBudgetSummary";
	public static final String INVOICE_SUMMARY = "invoiceSummary";
	public static final String INVOICE_PERSONNEL_SUMMARY = "invoicePersonnelServices";
	public static final String SUB_BUDGET_ID_KEY = "asSubBudgetID";

	// Constants for confirm budget modification
	public static final String NEGATIVE_AMENDMENT_IN_PROGRESS = "NEGATIVE_AMENDMENT_IN_PROGRESS";
	public static final String BUDGET_MODIFICATION_IN_PROGRESS = "BUDGET_MODIFICATION_IN_PROGRESS";
	public static final String CONFIGURATION_BUDGET_UPDATE_IN_PROGRESS = "CONFIGURATION_BUDGET_UPDATE_IN_PROGRESS";
	public static final String INVOICE_PAYMENT_OUTSTANDING = "INVOICE_PAYMENT_OUTSTANDING";
	public static final String MODIFY_BUDGET = "Modify Budget";
	public static final String PAYMENT_SORT_AND_FILTER = "PaymentSortAndFilter";

	public static final String FETCH_ACTIVE_PAYMENTS = "fetchActivePayments";
	public static final String FETCH_ACTIVE_BUDGETS = "fetchActiveBudgets";
	public static final String UPLOAD_RFPDOCUMENT_FILE = "uploadRfpDocument";
	public static final String STATUS_DISBURSED = "Disbursed";
	public static final String STATUS_PENDING_SUBMISSION = "Pending Submission";
	public static final String STATUS_PENDING_APPROVAL = "Pending Approval";
	public static final String STATUS_SUSPENDED = "Suspended";
	public static final String OPEN__PAYMENTS_DETAILS = "openPaymentDetails";

	public static final String VENDOR_ID = "Vendor ";
	public static final String TABLE_NAME = "ORGANIZATION";
	public static final String APPROVED_PROVIDERS_LOWERCASE = "approvedproviders";
	public static final String ACCOUNTS_ALLOCATION_LIST = "AccountsAllocationList";
	public static final String FUNDING_ALLOCATION_LIST = "FundingAllocationList";
	public static final String APPROVED_PROVIDERS_AND_SERVICES = "approvedprovidersandservices";
	public static final String SEND_EVALUATION_TASK = "sendEvaluationTask";
	public static final String SEND_EVALUATION_SUCCESSFUL = "sendEvaluationSuccessful";
	public static final String SEND_EVALUATION_ID = "sendEvaluationId";
	public static final String CONFIRM_SELECTED_PROVIDER = "confirmselectedprovider";
	public static final String CONFIRM_NOT_SELECTED_PROVIDER = "confirmNotSelectedProvider";
	public static final String VIEW_SELECTION_COMMENTS = "provider/viewSelectionComments";
	public static final String AWARD_REVIEW_COMMENTS = "evaluation/awardReviewComments";
	public static final String VIEW_RESPONSE = "viewResponse";
	public static final String VIEW_EVALUATION_SUMMARY = "evaluation/viewEvaluationSummary";
	public static final String VIEW_AWARD_DOCUMENTS = "evaluation/viewAwardDocuments";
	public static final String SEND_EVALUATION_TASKS_DETAILS = "sendEvaluationTasksDetails";
	public static final String FETCH_INT_EXT_PROPOSAL_DETAILS = "fetchIntExtProposalDetails";
	public static final String INSERT_EVALUATION_STATUS = "insertEvaluationStatus";
	public static final String RENDER_FINALIZE_PROCUREMENT = "renderFinalizeProcurement";
	public static final String RENDER_CONTRACT_COF = "renderContractCOF";
	public static final String AMENDMENT_RENDER = "amendment";
	public static final String CONTRACT_START_DATE = "contractStartDate";
	public static final String CONTRACT_END_DATE = "contractEndDate";
	public static final String AMENDMENT_START = "amendmentStart";
	public static final String AMENDMENT_END = "amendmentEnd";
	public static final String PROPOSED_CONTRACT_END = "proposedContractEnd";
	public static final String ORIGINAL_CONTRACT_END_DATE = "originalContractEndDate";
	public static final String BUDGET_DATE_OF_LAST_UPDATE = "dateOfLastUpdate";
	public static final String BUDGET_VALUE = "budgetValue";
	public static final String SINGLE_QUOTE = "\'";
	public static final String DQUOTES_COMMA = "\",";
	public static final String DOUBLE_QUOTES = "\"";
	public static final String SQUARE_BRAC_BEGIN = "[";
	public static final String PROPERTIES_GRIDTRANSACTION = "com.nyc.hhs.properties.gridtransaction";
	public static final String PROPERTIES_STATUS_CONSTANT = "com.nyc.hhs.properties.statusproperties";
	public static final Date FROM_DATE = new GregorianCalendar(1800, 01, 01).getTime();
	public static final String EVALUATION_SCORE = "EVALUATION_SCORE";
	public static final String ORGANIZATION_NAME = "ORGANIZATION_LEGAL_NAME";
	public static final String COM_NYC_HHS_PROPERTIES_GRIDHEADER = "com.nyc.hhs.properties.gridheader";
	public static final String ERROR_MESSAGES_PROPERTY_FILE = "com.nyc.hhs.properties.messages";

	public static final String LI_END_YEAR = "liEndYear";
	public static final String LI_START_YEAR = "liStartYear";
	public static final String PAGE = "page";
	public static final String FUNDING_SOURCE_TOTAL = "Funding Source Total";
	public static final String TOTAL = "total";
	public static final String RC = "rc";
	public static final String SUB_OC = "subOC";
	public static final String UOBC = "uobc";
	public static final String ID = "id";
	public static final String OPERATION_EDIT = "edit";
	public static final String OPERATION_DELETE = "del";
	public static final String OPERATION_ADD = "add";
	public static final String GRID_OPERATION = "oper";
	public static final String FETCH = "Fetch";
	public static final String SCREEN_NAME = "screenName";
	public static final String OVERALL = "Overall";
	public static final String CHART_OF_ACCOUNTS_TOTAL = "Chart of Accounts Total";
	public static final String FINANCIALS_ACCOUNT_GRID = "financialsAccountGrid";
	public static final String CONTRACT_CONFIGURATION_FUNDING_GRID = "contractConfigurationFundingGrid";
	public static final String SMALL_FY = "fy";
	public static final String LI_START_FY_COUNTER = "liStartFyCounter";
	public static final String LI_FYCOUNT = "liFYcount";
	public static final String COLUMNS_FOR_TOTAL = "columnsForTotal";
	public static final String TOTAL_COLUMN_APPENDER = "total";
	public static final String SUB_HEADER_PROP = "SubHeaderProp";
	public static final String MAIN_HEADER_PROP = "MainHeaderProp";
	public static final String GRID_COL_NAMES = "GridColNames";
	public static final String AMENDMENT_SUB_HEADER_PROP = "AmendmentSubHeaderProp";
	public static final String AMENDMENT_MAIN_HEADER_PROP = "AmendmentMainHeaderProp";
	public static final String AMENDMENT_GRID_COL_NAMES = "AmendmentGridColNames";
	public static final String FUNDING_SUB_HEADER_PROP = "FundingSubHeaderProp";
	public static final String FUNDING_MAIN_HEADER_PROP = "FundingMainHeaderProp";
	public static final String FUNDING_GRID_COL_NAMES = "FundingGridColNames";
	public static final String AMENDMENT_FUNDING_SUB_HEADER_PROP = "AmendmentFundingSubHeaderProp";
	public static final String AMENDMENT_FUNDING_MAIN_HEADER_PROP = "AmendmentFundingMainHeaderProp";
	public static final String AMENDMENT_FUNDING_GRID_COL_NAMES = "AmendmentFundingGridColNames";
	public static final String BUDGET_TYPE = "budgetType";
	public static final String GRID_LABEL = "gridLabel";
	public static final String FILTER_INVOICES = "filterInvoices";
	public static final String FILTER_INVOICE = "filterInvoice";
	public static final String TRANSACTION_NAME = "transactionName";
	public static final String BEAN_NAME = "beanName";
	public static final String PROPERTIES_TASKHANDLERS = "com.nyc.hhs.properties.taskhandlers";
	public static final String USER_CITY = "city_org";
	public static final String USER_AGENCY = "agency_org";
	public static final String NYC_AGENCY_MASTER = "nycAgencyMaster";
	public static final String PROCUREMENT_STATUS_PLANNED = "planned";
	public static final String PROC_STATUS_PLANNED = "Planned";
	public static final String PROCUREMENT_STATUS_RELEASED = "Released";
	public static final String UPDATE_LANDING_JSP = "jsp/contractbudget/contractBudgetUpdateLanding";
	public static final String MODIFICATION_REVIEW_TASK_JSP = "jsp/contractbudget/budgetModificationReviewTask";
	public static final String UPDATE_REVIEW_TASK_JSP = "jsp/contractbudget/budgetUpdateReviewTask";
	public static final String IS_AMENDMENT_FLOW = "isAmendmentFlow";
	public static final String AS_AMENDMENT = "asAmendment";

	public static final String PUBLISH_PROCUREMENT_STATUS = "publishprocurementstatus";
	public static final String BRANCH_ID = "branchid";

	public static final String NO_ERROR = "no error";

	// Navigation related starts
	public static final String PROCUREMENT_ROADMAP_DETAILS = "ProcurementRoadmapDetails";
	public static final String PROCUREMENT_INFORMATION = "ProcurementInformation";
	public static final String RFP_RELEASE_DETAILS = "RFPDetails";
	public static final String PROPOSAL_AND_EVALUATION = "ProposalsandEvaluations";
	public static final String EVALUATION_SUMMARY = "EvaluationSummary";
	public static final String AWARDS_CONTRACTS_SCREEN = "AwardsandContractsScreen";
	public static final String AWARDS_AND_CONTRACTS = "awardsAndContracts";
	public static final String PROCUREMENT_SUMMARY = "ProcurementSummaryHeader";
	public static final String SERVICES_AND_PPROVIDER = "ServicesAndProviders";
	public static final String RFP_DOCUMENTS = "RFPDocumentsHeader";
	public static final String PROPOSAL_SUMMARY = "ProposalSummary";
	public static final String SELECTION_DETAILS = "SelectionDetails";

	public static final String SECOND_LEVEL_ID[] =
	{ "ProcurementSummary", "ApprovedProviders", "Financials", "ProposalConfiguration", "CompetitionConfiguration",
			"RFPDocuments", "EvaluationCriteria", "ReleaseAddendum", "ServiceSelection", "PublishProcurement",
			"ReleaseRFP", "EvaluationSettings", "EvaluationStatus", "EvaluationResultsandSelections",
			"ProposalDetails", "ProposalDocuments", "SubmitProposal" };
	public static final List<String> HIDDEN_TABS_CANCEL_STATUS = new ArrayList<String>();
	static
	{
		HIDDEN_TABS_CANCEL_STATUS.add("PublishProcurement");
		HIDDEN_TABS_CANCEL_STATUS.add("ReleaseRFP");
		HIDDEN_TABS_CANCEL_STATUS.add("ReleaseAddendum");
		HIDDEN_TABS_CANCEL_STATUS.add("SubmitProposal");
	}
	// Navigation related end

	public static final List<String> GET_GRID_NAME = new ArrayList<String>();
	static
	{
		GET_GRID_NAME.add("professionalServices");
	}
	public static final Map<String, String> GET_TRANSACTION_NAME_HEADER_MAP = new HashMap<String, String>();
	static
	{
		GET_TRANSACTION_NAME_HEADER_MAP.put("professionalServices", "profServicesGridFetch");
	}

	public static final Map<String, String> GET_PROPERTY_MAP = new HashMap<String, String>();
	static
	{
		GET_PROPERTY_MAP.put("professionalServices", "professionalServiceName,fyBudget,ytdInvoicedAmt,remainingAmt");

	}

	public static final String REQUEST_COULD_NOT_BE_COMPLETED = "This request could not be completed. Please try again in a few minutes.";
	public static final String SUSPEND_UNSUSPEND_ERROR = "Username, Password and Reason field is required.";
	public static final String UNSUSPEND_FAILURE = "Unable to unsuspend contract. Please try again later";
	public static final String WITHDRAWINVOICE_FAILURE = "Unable to withdraw invoice. Please try again later";
	public static final String DELETE_INVOICE_FAILURE = "Unable to delete invoice. Please try again later";
	public static final String INVALID_CREDENTIALS = "The username or password you have entered is incorrect. Please enter the correct username and password to submit.";
	public static final String CBGRIDBEAN_IN_SESSION = "CBGridBeanInSession";
	public static final String AO_RETURNED_GRID_LIST = "aoReturnedGridList";
	public static final String CB_GRID_BEAN_OBJ = "aoCBGridBeanObj";
	public static final String AO_RATE_BEAN_OBJ = "aoRateBeanObj";
	public static final String AO_CB_GRID_BEAN = "aoCBGridBean";
	public static final String AO_ACCOUNTS_ALLOCATION_BEAN = "aoAccountsAllocationBean";
	public static final String AO_CB_FUNDING_BEAN_OBJ = "aoCBFundingBeanObj";
	public static final String GRID_ERROR = "gridError";
	public static final String GRID_ERROR_MESSAGE = "gridErrorMessage";
	public static final String LEVEL_ERROR_MESSAGE = "levelErrorMessage";
	public static final String ERROR_PAGE_CLOSE = "errorPageClose";
	public static final String AGENCY_INVOICE_STATUS_ID[] =
	{ "72", "71", "73", "74", "75" };
	public static final String PROVIDER_INVOICE_STATUS_ID[] =
	{ "70", "72", "71", "73", "74", "75" };
	public static final String INVOICE_STATUS_ID_INDIRECT_RATE[] =
	{ "72", "73" };
	public static final String INVOICE_STATUS_ID_REMAINING_AMOUNT[] =
	{ "70", "71", "72", "73" };
	public static final String STRING_TEN = "10";

	public static final String GET_INDIRECT_RATE_NON_GRID_DATA = "getIndirectRateData";

	// R3 WF property
	// Start || Changes done for enhancement 6636 for Release 3.12.0
	public static final String PROPERTY_PE_PROPOSAL_ID = "ProposalID";
	// End || Changes done for enhancement 6636 for Release 3.12.0
	public static final String PROPERTY_PE_PROCUREMENT_TITLE = "ProcurementTitle";
	public static final String PROPERTY_PE_CONTRACT_TITLE = "ContractTitle";
	public static final String PROPERTY_PE_PROCUREMENT_EPIN = "ProcurementEPin";
	public static final String PROPERTY_PE_PROVIDER_ID = "ProviderID";
	public static final String PROPERTY_PE_PROVIDER_NAME = "ProviderName";
	public static final String PROPERTY_PE_AWARD_EPIN = "AwardEPin";
	public static final String PROPERTY_PE_CT = "ContractNumber";
	public static final String PROPERTY_PE_TASK_TYPE = "TaskType";
	public static final String PROPERTY_PE_LAUNCH_COF = "LaunchCOF";
	public static final String PROPERTY_PE_SUBMITTED_BY = "LaunchBy";
	public static final String PROPERTY_PE_SUBMITTED_DATE = "LaunchDate";
	public static final String PROPERTY_PE_ASSIGNED_TO = "TaskOwner";
	public static final String PROPERTY_PE_LINKED_WOBNO = "LinkedWobNo";
	public static final String PROPERTY_PE_ASSIGNED_TO_NAME = "TaskOwnerName";
	public static final String PROPERTY_PE_ASSIGNED_DATE = "TaskAssignDate";
	public static final String PROPERTY_PE_TASK_ID = "TaskID";
	public static final String PROPERTY_PE_LAUNCH_ORG_TYPE = "LaunchByOrgType";
	public static final String PROPERTY_PE_TASK_TOTAL_LEVEL = "ReviewLevel";
	public static final String CURR_LEVEL = "CurrentLevel";
	public static final String PROPERTY_PE_AGENCY_ID = "AgencyID";
	public static final String PROPERTY_PE_PROCURMENT_ID = "ProcurementID";
	public static final String PROPERTY_PE_CONTRACT_ID = "ContractID";
	public static final String PROPERTY_PE_CONTRACT_CONFIG_ID = "ContractConfigurationID";
	public static final String PROPERTY_PE_NEW_FISCAL_YEAR_ID = "NewFiscalYearId";
	public static final String PROPERTY_PE_ADVANCE_NUMBER = "BudgetAdvanceID";
	public static final String PROPERTY_PE_BUDGET_ID = "BudgetID";
	public static final String PROPERTY_PE_ENTITY_ID = "EntityID";
	public static final String PROPERTY_PE_INVOICE_ID = "InvoiceNumber";
	public static final String PROPERTY_PE_TASK_STATUS_LOWER = "taskStatus";
	public static final String PROPERTY_PE_TASK_PREV_STATUS = "PrevTaskStatus";
	public static final String PROPERTY_PE_CONTRACT_CONF_WOB = "ContractConfigurationWOB";
	public static final String PROPERTY_PE_TASK_VISIBILITY = "taskVisibility";
	public static final String TASK_DETAIL_BEAN_SESSION = "detailsBeanForTaskGrid";
	public static final int INITIAL_TASK_ID = 1000;
	public static final String PROC_TASK_STATUS = "procTaskStatus";

	// R3 Audit
	public static final String PROPERTY_TASK_CREATION_EVENT = "Task Creation";
	public static final String PROPERTY_TASK_CREATION_EVENT_TYPE = "WorkFlow";
	public static final String PROPERTY_TASK_CREATION_DATA = "Task Assigned to: Unassigned Level 1";

	public static final String SERVICE_ERROR_MESSAGES = "serviceErrorMessages";
	public static final String MESSAGE_M38 = "M38";

	public static final String PENDING_SUBMISSION = "70";
	public static final String RETURNED_FOR_REVISION = "71";
	public static final String PENDING_SUBMISSION_PROVIDER = "120";
	public static final String RETURNED_FOR_REVISION_PROVIDER = "119";
	public static final String PENDING_APPROVAL_PROVIDER = "121";
	public static final String APPROVED_PROVIDER = "122";
	public static final String WITHDRAWN_PROVIDER = "123";
	public static final String SUSPENDED_PROVIDER = "124";

	public static final String INVOICE_DATE_APPROVED = "invoiceDateApproved";
	public static final String INVOICE_DATE_SUBMITTED = "invoiceDateSubmitted";
	public static final String INVOICE_VALUE = "invoiceValue";
	public static final String PENDING_APPROVAL = "72";
	public static final String APPROVED = "73";
	public static final String WITHDRAWN = "74";
	public static final String SUSPENDED = "75";
	public static final String CANCELLED = "76";
	public static final String CLOSED = "77";
	public static final String INVOICE_ACTION = "invoiceAction";
	public static final String INVOICE_CT_ID = "invoiceCtId";
	// R3 WF Ids
	public static final String WF_PROCUREMENT_CERTIFICATION_FUND = "WF301 - Procurement CoF";

	public static final String WF_CONTRACT_CONFIGURATION = "WF302 - Contract Configuration";
	public static final String WF_CONTRACT_CONFIGURATION_UPDATE = "WF310 - Contract Configuration Update";
	public static final String WF_CONTRACT_CERTIFICATION_FUND = "WF303 - Contract Certification of Funds";
	public static final String WF_FINANCIAL_UTILITY = "WF30x - Utility Workflow";
	public static final String WF_NEW_FY_CONFIGURATION = "WF309 - New Fiscal Year Configuration";
	public static final String WF_ADVANCE_REVIEW = "WF307 - Advance Payment Request";
	public static final String WF_AMENDMENT_CONFIGURATION = "WF313 - Amendment Configuration";

	public static final String WF_CONTRACT_BUDGET_UPDATE_REVIEW = "WF311 - Contract Budget Update Review";
	public static final String WF_CONTRACT_BUDGET_REVIEW = "WF304 - Contract Budget";
	public static final String WF_CONTRACT_BUDGET_MODIFICATION = "WF312 - Contract Budget Modification (MOD)";

	public static final String WF_INVOICE_REVIEW = "WF305 - Invoice Review";
	public static final String WF_CONTRACT_BUDGET_AMENDMENT = "WF315 - Contract Budget Amendment (AMD)";
	public static final String WF_PAYMENT_REVIEW = "WF306 - Payment Review";

	public static final String WF_AMENDMENT_CERTIFICATION_FUND = "WF314 - Amendment Certification of Funds";
	//Added in R6: returned payment review task
	public static final String WF_RETURNED_PAYMENT_REVIEW = "WF316 - Returned Payment Review";
	//Added in R6: returned payment review task end
	public static final String WF_ADVANCE_PAYMENT_REVIEW = "WF308 - Advance Payment Review";
	public static final String WF_AWARD_TASK = "WF203 - Configure Award Documents";

	public static final Map<String, Integer> FINANCIAL_WF_ID_MAP = new HashMap<String, Integer>();
	static
	{
		FINANCIAL_WF_ID_MAP.put(WF_PROCUREMENT_CERTIFICATION_FUND, 15);
		FINANCIAL_WF_ID_MAP.put(WF_CONTRACT_CONFIGURATION_UPDATE, 11);
		FINANCIAL_WF_ID_MAP.put(WF_CONTRACT_CONFIGURATION, 8);
		FINANCIAL_WF_ID_MAP.put(WF_CONTRACT_CERTIFICATION_FUND, 8);
		FINANCIAL_WF_ID_MAP.put(WF_ADVANCE_REVIEW, 2);
		FINANCIAL_WF_ID_MAP.put(WF_NEW_FY_CONFIGURATION, 13);
		FINANCIAL_WF_ID_MAP.put(WF_AMENDMENT_CONFIGURATION, 3);
		FINANCIAL_WF_ID_MAP.put(WF_CONTRACT_BUDGET_REVIEW, 6);
		FINANCIAL_WF_ID_MAP.put(WF_CONTRACT_BUDGET_UPDATE_REVIEW, 7);
		FINANCIAL_WF_ID_MAP.put(WF_CONTRACT_BUDGET_MODIFICATION, 5);
		FINANCIAL_WF_ID_MAP.put(WF_CONTRACT_BUDGET_AMENDMENT, 4);
		FINANCIAL_WF_ID_MAP.put(WF_INVOICE_REVIEW, 12);
		FINANCIAL_WF_ID_MAP.put(WF_PAYMENT_REVIEW, 14);
		FINANCIAL_WF_ID_MAP.put(WF_ADVANCE_PAYMENT_REVIEW, 1);
		FINANCIAL_WF_ID_MAP.put(WF_AMENDMENT_CERTIFICATION_FUND, 3);
		//Added in R6: return payment review task
		FINANCIAL_WF_ID_MAP.put(WF_RETURNED_PAYMENT_REVIEW, 16);
		//Added in R6: return payment review task end
	}

	public static final String PAYMENT_STATUS_ID_APPROVED = "64";
	public static final String PAYMENT_STATUS_ID_DISBURSED = "65";
	public static final String ONE = "1";
	public static final String ZERO = "0";
	public static final String PAYMENT = "Payment";
	public static final String INVOICE = "Invoice";
	public static final String INVOICE_PROVIDER = "Invoice Provider";
	public static final String IS_VALID_EPIN = "isValidEpin";

	public static final String TWO = "2";
	public static final String CONTRACT_RENEWAL_TYPE_ID = "3";
	public static final String CONTRACT_UPDATE_TYPE_ID = "4";
	public static final String CONTRACT_AMENDMENT_TYPE_ID = "2";
	public static final String CONTRACT_BASE_TYPE_ID = "1";
	public static final String FOUR = "4";
	public static final String THREE = "3";

	public static final String CONTRACT_SUSPEND_STATUS_ID = "67";
	public static final String BUDGET_SUSPEND_STATUS_ID = "88";
	public static final String PAYMENT_SUSPEND_STATUS_ID = "66";

	public static final String BUDGET_APPROVED_STATUS_ID = "82";
	
	public static final String BUDGET_PENDING_CONFIGURATION_STATUS_ID = "106";

	public static final String FILTER_PAYMENT = "filterPayment";

	// Review process tasks start
	public static final int ADVANCE_PAYMENT_REVIEW_ID = 1;
	public static final int ADVANCE_PAYMENT_REQUEST_IDS = 2;
	public static final int AMENDMENT_CERTIFICATION_OF_FUNDS_ID = 3;
	public static final int CONTRACT_BUDGET_AMENDMENT_REVIEW_ID = 4;
	public static final int CONTRACT_BUDGET_MODIFICATION_REVIEW_ID = 5;
	public static final int CONTRACT_BUDGET_REVIEW_ID = 6;
	public static final int CONTRACT_BUDGET_UPDATE_REVIEW_ID = 7;
	public static final int CONTRACT_CERTIFICATION_OF_FUNDS_ID = 8;
	public static final int CONTRACT_CONFIGURATION_INITIAL_REN_NEW_ID = 9;
	public static final int CONTRACT_CONFIGURATION_AMENDMENT_ID = 10;
	public static final int CONTRACT_CONFIGURATION_UPDATE_ID = 11;
	public static final Integer INVOICE_REVIEW_ID = 12;
	public static final int NEW_FISCAL_YEAR_CONFIGURATION_ID = 13;
	public static final int PAYMENT_REVIEW_ID = 14;
	public static final int PROCUREMENT_CERTIFICATION_OF_FUNDS_ID = 15;
	// Review process tasks end

	public static final String ERROR_MESSAGE = "errorMessage";
	public static final String IS_VALID_USER = "isValidUser";
	public static final String IS_VALID_DATA = "loIsDataValid";
	public static final int CONTRACT_CONFIGURATION_REGISTERED_STATUS_ID = 62;
	public static final String IS_DOCUMENT_RFP_AWARD_TYPE = "IS_RFP_AWARD_TYPE";
	public static final String SOLICITATION_CATEGORY = "Solicitation";

	public static final String AGENCY_COMMENT = "Agency Comment";
	public static final String PROVIDER_COMMENT = "Provider Comment";
	public static final String ACCELERATOR_COMMENT = "Accelerator Comment";
	public static final String CLOSE_CONTRACT = "Contract Close";
	public static final String SUSPEND_CONTRACT = "Suspend Contract";
	public static final String UNSUSPEND_CONTRACT = "Unsuspend  Contract";
	public static final String WITHDRAW_INVOICE_AUDIT = "Withdraw  Invoice";
	public static final String UPDATE_CONTRACT = "Update Contract";
	public static final String RENEW_CONTRACT = "Renew Contract";
	public static final String AMEND_CONTRACT = "Amend Contract";
	public static final String ADD_CONTRACT = "Add Contract";

	// message property constants
	public static final String PROPERTY_TASK_IN_OPEN_STATUS = "PROP_TASK_IN_OPEN_STATUS";
	public static final String PROPERTY_REVIEW_LEVELS_SAVED = "PROP_REVIEW_LEVELS_SAVED";
	public static final String PROPERTY_REVIEW_LEVELS_USER_SAVED = "PROP_REVIEW_LEVELS_USER_SAVED";
	public static final String PROPERTY_LEVELS_OF_REVIEW_NOT_SET = "PROP_LEVELS_OF_REVIEW_NOT_SET";
	public static final String PROPERTY_ERRORNEOUS_DATA = "PROP_ERRORNEOUS_DATA";

	public static final String SUSPEND_NOT_SUCCESS = "Unable to suspend contract. Please try again later.";
	public static final String SUSPEND_WORKFLOW_NOT_SUCCESS = "Error occured while suspending contract workflow.";
	public static final String UNSUSPEND_WORKFLOW_NOT_SUCCESS = "Error occured while unsuspending contract workflow.";
	public static final String CANCEL_CONTRACT = "cancelContract";
	public static final String CANCEL_AMENDMENT = "cancelAmendment";
	public static final String PASS_FLAG = "pass";
	public static final String FAIL_FLAG = "fail";
	public static final String ERROR_FLAG = "error";
	public static final String PROVIDER_IN_ACCELERATOR = "providerMustExistInAccelerator";
	public static final String PROVIDER_IN_ACCELERATOR_AMEND = "providerMustExistInAcceleratorAmend";
	public static final String PROVIDER_IN_ACCELERATOR_RENEW = "providerMustExistInAcceleratorRenew";

	public static final String FIVE = "5";
	public static final String CONTRACT_UNSUSPENDED = "Contract unsuspended.";
	public static final String CONTRACT_SUSPENDED = "Contract suspended.";

	public static final String WF_ENTITY_TYPE_CONTRACT = "CONTRACT";
	public static final String WF_ENTITY_TYPE_BUDGET = "BUDGET";
	public static final String WF_ENTITY_TYPE_PROCUREMENT_COF = "PROCUREMENT_COF";
	public static final String WF_ENTITY_TYPE_INVOICE = "INVOICE";
	public static final String WF_ENTITY_TYPE_PAYMENT = "PAYMENT";
	public static final String WF_INITIAL_REVIEWER = "Unassigned";
	public static final String SUCCESS_MESSAGE = "successMessage";
	public static final String TRANSACTION_MESSAGE = "transactionMessage";

	public static final String NEW_FY_CONFIG = "New FY Config";

	public static final String CONTRACT_COF_COA_HEADER = "COAHeader";
	public static final String CONTRACT_COF_COA_DETAILS = "COADetails";
	public static final String CONTRACT_COF_FUNDING_DETAILS = "FundingDetails";
	public static final String PROCUREMENT_COF_COA_DETAILS = "ProcCOADetails";
	public static final String PROCUREMENT_COF_FUNDING_DETAILS = "ProcFundingDetails";
	public static final String FETCH_NEW_FY_TASK_DAYS_VALUE = "fetchNewFYTaskDaysValue";

	public static final String SUBBUDGET_ID = "subBudgetId";
	public static final String PARENT_SUBBUDGET_ID = "parentSubBudgetId";
	public static final String PARENT_BUDGET_ID = "parentBudgetId";
	public static final String PARENT_CONTRACT_ID = "parentContractId";
	public static final String SUBBUDGET_ID_SESSION = "subBudgetIdSession";
	public static final String NEWLY_CREATED_SUB_BUDGET_ID = "newlyCreatedSubBudgetId";
	public static final String INVOICE_ID = "invoiceId";

	public static final String ORG_LEGAL_NAME = "organizationLegalName";

	// BudgetList Screen Status
	public static final String BUDGET_TYPE1 = "Budget Amendment";
	public static final String BUDGET_TYPE2 = "Budget Modification";
	public static final String BUDGET_TYPE3 = "Contract Budget";
	public static final String BUDGET_TYPE4 = "Budget Update";
	// BudgetList Screen Status Ends
	public static final String SUBHEADER = "subHeader";
	public static final String INDIRECT_COST = "Indirect Costs";
	public static final String CONTRACT = "Contract";
	public static final String CONTRACT_AMENDMENT = "Contract Amendment";
	public static final String PDF_FILE_PATH = "PDF_FILE_PATH";

	public static final String PDF_NOT_STARTED = "PDF_NOT_STARTED";
	public static final String PDF_IN_PROGRESS = "PDF_IN_PROGRESS";
	public static final String PDF_GENERATED = "PDF_GENERATED";
	public static final String PDF_CLASS = "com/nyc/hhs/batch/impl/PDFGenerationBatch.class";
	public static final String CASTOR_MAPPING = "com/nyc/hhs/config/castor-mapping-Entity.xml";

	public static final String UNTITLED_PROPOSAL = "Untitled Proposal";
	public static final String DEFAULT_PROPOSAL_STATUS = "17";
	public static final String APPLICATION_ERROR_MSG = "An internal Application error has occure.Please try later";
	// Max DB view length
	public static final int DB_MAX_VIEW_LEN = 30;

	public static final String DOING_BUSINESS_DATA_FORM = "Doing Business Data Form";
	public static final String TEMPORARY_FOLDER = "r2/temp";
	public static final int TEMPORARY_FOLDER_CLEAN_TIME_MIN = 60;
	// proposal Status
	public static final String PROPOSAL_STATUS_SUBMITTED_PROPOSAL = "Submitted Proposal";
	public static final String PROPOSAL_STATUS_SERVICE_APP_REQ = "Service App Required";
	public static final String PROPOSAL_STATUS_DID_NOT_PROPOSE = "Did Not Propose";
	public static final String PROPOSAL_STATUS_SELECTED = "Selected";
	public static final String PROPOSAL_STATUS_NOT_SELECTED = "Not Selected";
	public static final String PROPOSAL_STATUS_NOT_APPLICABLE = "Not Applicable";
	public static final String ORG_TYPE = "org_type";
	public static final String PROPOSAL_DETAIL_BEAN_KEY = "aoProposalDetailsBean";
	public static final String FILTER_ITEM_KEY = "filterItem";
	public static final String ADVANCE_READ_ONLY = "advanceReadOnly";

	public static final String TASK_ADVANCE_PAYMENT_REVIEW = "Advance Payment Review";
	public static final String TASK_ADVANCE_REVIEW = "Advance Payment Request";
	public static final String TASK_AMENDMENT_CONFIGURATION = "Contract Configuration Amendment";
	public static final String TASK_AMENDMENT_COF = "Amendment Certification of Funds";
	//Added in R6: returned payment review task
	public static final String TASK_RETURN_PAYMENT_REVIEW = "Returned Payment Review";
	public static final String RETURN_PAYMENT = "Returned Payment";
	//Added in R6: returned payment review task end
	public static final String TASK_BUDGET_AMENDMENT = "Contract Budget Amendment Review";
	public static final String TASK_BUDGET_MODIFICATION = "Contract Budget Modification Review";
	public static final String TASK_BUDGET_UPDATE = "Contract Budget Update Review";
	public static final String TASK_BUDGET_REVIEW = "Contract Budget Review";
	public static final String TASK_CONTRACT_COF = "Contract Certification of Funds";
	public static final String TASK_CONTRACT_CONFIGURATION = "Contract Configuration";
	public static final String TASK_CONTRACT_UPDATE = "Contract Configuration Update";
	public static final String TASK_INVOICE_REVIEW = "Invoice Review";
	public static final String TASK_NEW_FY_CONFIGURATION = "New Fiscal Year Configuration";
	public static final String TASK_PAYMENT_REVIEW = "Payment Review";
	public static final String TASK_PROCUREMENT_COF = "Procurement Certification of Funds";
	public static final String DELETE_PROPOSAL_DOCUMENT = "deleteProposalDocument";
	public static final String CHECK_PROPOSAL_STATUS = "checkProposalStatus";
	public static final String DELETE_PROPOSAL_SITE = "deleteProposalSite";
	public static final String DELETE_PROPOSAL_QUESTION_RESPONSE = "deleteProposalQuestionResponse";
	public static final String CANCEL_PROPOSAL = "cancelProposal";
	public static final String FETCH_PROPOSAL_DOCUMENT_ID = "fetchProposalDocumentId";
	public static final String CHECK_PROCUREMENT_PROPOSAL_STATUS = "checkProcurementProposalStatus";
	public static final String PROPOSAL_RETRACT = "retractProposal";
	public static final String FETCH_PROPOSAL_TITLE = "fetchProposalTitle";
	public static final String UPDATE_PROPOSAL_DOCUMENT_STATUS = "updateDocumentStatusSubmitted";
	public static final String PROPOSAL_RETRACT_SUCCESSFULLY = "proposalRetractSuccessfully";
	public static final String PROPOSAL_RETRACT_FAILED = "proposalRetractFailed";
	public static final String PROPOSAL_CANCEL_SUCCESSFULLY = "proposalCancelSuccessfully";
	public static final String PROPOSAL_CANCEL_FAILED = "proposalCancelFailed";
	public static final String UPDATE_PROPOSAL_STATUS = "updateProposalStatus";
	public static final String FETCH_PROPOSAL_STATUS_ID = "fetchProposalStatusId";
	public static final String FETCH_PROCUREMENT_STATUS_ID = "fetchProcurementStatusId";
	public static final String ADD_DOCUMENT_ERROR = "addDocumentError";
	public static final String REMOVE_DOCUMENT_ERROR = "removeDocumentError";
	public static final String SAVE_EVALUATION_SETTING = "Error was Occured while saving the evaluation setting";
	public static final String ERROR_RETRACT_PROPOSAL = "Error was Occured while retract the proposal";
	public static final String ERROR_CANCEL_PROPOSAL = "Error was Occured while cancel the proposal";
	public static final String ERROR_ASSIGN_AWARD_EPIN = "Error was Occured while assign award EPin";
	public static final String ERROR_SELECT_PROVIDER = "Error was Occured while select provider";
	public static final String ERROR_VIEW_DOCUMENT = "Error was Occured while view the document detail.";
	public static final String ERROR_FETCH_EVALUATION_DETAIL = "Error Occured while fetching the evaluation detail";
	public static final String ERROR_FETCH_PROPOSAL_DETAIL = "Error Occured while fetching the proposal detail";
	public static final String ERROR_FETCH_UPDATE_DETAIL = "Error Occured while updating the proposal detail";
	public static final String ERROR_ACTION_SORT_EVALUATION_STATUS = "Error Occured while sorting evaluation status";
	public static final String ERROR_FILTER_EVALUATION_STATUS = "Error Occured while filtering evaluation status";
	public static final String ERROR_SEND_EVALUATION = "Error occurred while sending evaluation task details";
	public static final String AS_ORGANIZATION_ID = "asOrganizationId";
	public static final String UNLOCK_PROPOSAL_SUCCESS = "unlockProposal";


	public static final Map<String, Integer> FINANCIAL_TASK_PROCESS_ID_MAP = new HashMap<String, Integer>();
	static
	{
		FINANCIAL_TASK_PROCESS_ID_MAP.put(TASK_PROCUREMENT_COF, 15);
		FINANCIAL_TASK_PROCESS_ID_MAP.put(TASK_CONTRACT_UPDATE, 11);
		FINANCIAL_TASK_PROCESS_ID_MAP.put(TASK_CONTRACT_CONFIGURATION, 9);
		FINANCIAL_TASK_PROCESS_ID_MAP.put(TASK_CONTRACT_COF, 8);
		FINANCIAL_TASK_PROCESS_ID_MAP.put(TASK_ADVANCE_REVIEW, 2);
		FINANCIAL_TASK_PROCESS_ID_MAP.put(TASK_NEW_FY_CONFIGURATION, 13);
		FINANCIAL_TASK_PROCESS_ID_MAP.put(TASK_AMENDMENT_CONFIGURATION, 10);
		FINANCIAL_TASK_PROCESS_ID_MAP.put(TASK_BUDGET_UPDATE, 7);
		FINANCIAL_TASK_PROCESS_ID_MAP.put(TASK_BUDGET_REVIEW, 6);
		FINANCIAL_TASK_PROCESS_ID_MAP.put(TASK_BUDGET_MODIFICATION, 5);
		FINANCIAL_TASK_PROCESS_ID_MAP.put(TASK_BUDGET_AMENDMENT, 4);
		FINANCIAL_TASK_PROCESS_ID_MAP.put(TASK_INVOICE_REVIEW, 12);
		FINANCIAL_TASK_PROCESS_ID_MAP.put(TASK_PAYMENT_REVIEW, 14);
		FINANCIAL_TASK_PROCESS_ID_MAP.put(TASK_ADVANCE_PAYMENT_REVIEW, 1);
		FINANCIAL_TASK_PROCESS_ID_MAP.put(TASK_AMENDMENT_COF, 3);
		//Added in R6: returned payment review task
		FINANCIAL_TASK_PROCESS_ID_MAP.put(TASK_RETURN_PAYMENT_REVIEW, 16);
		//Added in R6: returned payment review task end
	}
	//Changes made in R6 for FindBug
	public static final Map<String, Object> FINANCIAL_TASK_ID_PROCESS_MAP = new HashMap<String, Object>();
	static
	{
		Iterator loItr = HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.entrySet().iterator();
		 while(loItr.hasNext())
		 {
			 Map.Entry loEntry = (Map.Entry)loItr.next();
			 String loTaskName = (String) loEntry.getKey();
			 Integer loTaskId = (Integer)loEntry.getValue();
              if(null != loTaskName && !loTaskName.isEmpty() && null != loTaskId)
              {
            	  FINANCIAL_TASK_ID_PROCESS_MAP.put(loTaskId.toString(), loTaskName);
              }
		 }
		
	}

	public static final String TASK_IN_REVIEW = "In Review";
	public static final String TASK_RFR = "Returned for Revision";
	public static final String TASK_WITHDRAWN = "Withdrawn";
	public static final String TASK_COMPLETED = "Complete";
	public static final String TASK_FINISHED = "Finished";
	public static final String TASK_CANCELLED = "Cancelled";
	public static final String TASK_UNASSIGNED = "Unassigned Level ";
	public static final Map<String, String> TASK_STATUS_ID_MAP = new HashMap<String, String>();
	static
	{
		TASK_STATUS_ID_MAP.put(TASK_IN_REVIEW, "78");
		TASK_STATUS_ID_MAP.put(TASK_RFR, "79");
		TASK_STATUS_ID_MAP.put(TASK_COMPLETED, "80");
		TASK_STATUS_ID_MAP.put(TASK_CANCELLED, "81");

	}
	public static final String CONTRACT_DOCUMENT_TABLE = "contract_document";
	public static final String BUDGET_DOCUMENT_TABLE = "budget_document";
	public static final String INVOICE_DOCUMENT_TABLE = "invoice_document";
	public static final String HDN_TABLE_NAME = "hdnTableName";
	public static final String TRAN_FETCH_EVAL_SCORE = "fetchEvaluationScores";
	public static final String TRAN_RESULT_EVAL_SCORE = "loEvaluationList";
	public static final String TRAN_RESULT_HEADER_DETAILS = "loHeaderDetails";
	public static final String PROPOSAL_SCORE_SUM = "proposalScoreSum";
	public static final String TRAN_FETCH_PROC_SUMMARY = "fetchProcurementSummary";
	public static final String PROC_SUMMARY = "ProcurementSummary";
	public static final String PROVIDER = "Provider";
	public static final String PROVIDER_STATUS_KEY = "providerStatus";
	public static final String PROCUREMENT_BEAN = "procurementBean";
	public static final String PROPOSAL_DETAILS_MAP = "proposalDetails";
	public static final String PROPOSAL_DUE_DATE = "proposalDueDate";
	public static final String ACCELERATOR_USER_ROLE = "userRole";
	public static final String PROPOSAL_SUMMARY_JSP = "provider/proposalSummary";

	// contract budget summary screen
	public static final String UTILITIES = "utilities";
	public static final String FRINGES = "fringes";
	public static final String OTPS = "otps";
	public static final String SALARY = "salary";
	public static final String PROFESSIONAL_SERVICES = "professionalServices";
	public static final String RENT = "rent";

	public static final String TOTAL_RATE_BASED = "totalRateBased";
	public static final String TOTAL_MILESTONE_BASED = "totalMilestoneBased";
	public static final String TOTAL_UNALLOCATED_FUNDS = "unallocatedFunds";
	public static final String TOTAL_INDIRECT_COST = "totalIndirectCost";
	public static final String TOTAL_PROGRAM_INCOME = "totalProgramIncome";
	public static final String CITY_FUNDED_BUDGET = "cityFundedBudget";
	public static final String INDIRECT_RATE_KEY = "asIndirectRate";
	// Unallocated Screen
	public static final String UNALLOCATED_FUNDS = "Unallocated Funds";
	public static final String INSERT_UNALLOCATED_FUNDS = "insertUnallocatedFunds";
	public static final String FETCH_UNALLOCATED_FUNDS = "fetchUnallocatedFunds";
	public static final String UPDATE_UNALLOCATED_FUNDS = "updateUnallocatedFunds";
	public static final String UNALLOCATED_FUNDS_BEAN = "com.nyc.hhs.model.UnallocatedFunds";
	public static final String GET_SEQ_UNALLOCATED_ID = "getSeqForUnallocatedId";
	public static final String FETCH_UNALLOCATED_FUNDS_COUNT = "fetchUnallocatedFundsCount";
	public static final String INDIRECT_RATE_JSP_NAME = "indirectRate";
	public static final String MODIFICATION_INDIRECT_RATE_JSP_NAME = "modificationIndirectRate";
	public static final String AMENDMENT_INDIRECT_RATE_JSP_NAME = "amendmentIndirectRate";
	public static final String UPDATE_INDIRECT_RATE_JSP_NAME = "updateIndirectRate";
	public static final String INVOICE_INDIRECT_RATE_JSP_NAME = "invoiceIndirectRate";

	public static final String INVOICE_INSERT_UNALLOCATED_FUNDS = "insertInvoiceUnallocatedFunds";
	public static final String INVOICE_FETCH_UNALLOCATED_FUNDS = "fetchInvoiceUnallocatedFunds";
	public static final String INVOICE_UPDATE_UNALLOCATED_FUNDS = "updateInvoiceUnallocatedFunds";

	public static final String MODIFICATION_INSERT_UNALLOCATED_FUNDS = "insertModificationUnallocatedFunds";
	public static final String MODIFICATION_FETCH_UNALLOCATED_FUNDS = "fetchModificationUnallocatedFunds";
	public static final String MODIFICATION_UPDATE_UNALLOCATED_FUNDS = "updateModificationUnallocatedFunds";
	public static final String MODIFICATION_INSERT_MOD_AMOUNT = "insertModUnallocatedFunds";

	public static final String UPDATE_INSERT_UNALLOCATED_FUNDS = "insertUpdateUnallocatedFunds";
	public static final String UPDATE_FETCH_UNALLOCATED_FUNDS = "fetchUpdateUnallocatedFunds";
	public static final String UPDATE_UPDATE_UNALLOCATED_FUNDS = "updateUpdateUnallocatedFunds";
	public static final String UPDATE_INSERT_MOD_AMOUNT = "insertUpdModUnallocatedFunds";

	public static final String AMENDMENT_INSERT_UNALLOCATED_FUNDS = "insertAmendmentUnallocatedFunds";
	public static final String AMENDMENT_FETCH_UNALLOCATED_FUNDS = "fetchAmendmentUnallocatedFunds";
	public static final String GET_REMAINING_AMOUNT_UN_ALLOCATED_IN_MULTIPLE_AMENDMENTS = "getRemainingAmountUnAllocatedInMultipleAmendments";
	public static final String AMENDMENT_UPDATE_UNALLOCATED_FUNDS = "updateAmendmentUnallocatedFunds";
	public static final String AMENDMENT_INSERT_AMN_AMOUNT = "insertUpdAmnUnallocatedFunds";
	public static final String BEGINNING_BRACKET = " (";
	public static final String AO_ERROR_MAP = "aoErrorMap";
	// Professional Service Screen
	public static final String CONTRACT_REGISTERED_STATUS_ID = "62";

	public static final String EVALUATION_SETTING_MORE_EVALUATOR = "MORE_EVALUATOR_ERROR";
	public static final String EVALUATION_SEND = "abSendEvaluation";
	public static final String FETCH_EVALUATION_SETTINGS = "fetchEvaluationSettings";
	public static final String EVALUATION_LIST_INTERNAL = "evaluationListInternal";
	public static final String EVALUATION_LIST_EXTERNAL = "evaluationListExternal";
	public static final String QUERY = "query";
	public static final String AGENCYID = "agencyId";
	public static final String PREV_CONTRACT_AGENCYID = "prevContractAgencyId";
	public static final String AS_AGENCY_ID = "asAgencyId";
	public static final String FETCH_INTERNAL_EVALUATOR_USERS = "fetchInternalEvaluatorUsers";
	public static final String FETCH_PROVIDER_NAMES = "fetchProviderNames";
	public static final String INTERNAL_EVALUATOR_LIST = "internalEvaluatorList";
	public static final String PROVIDER_NAME_LIST = "providerNameList";
	public static final String EXTERNAL_EVALUATOR_LIST = "externalEvaluatorList";
	public static final String FETCH_EXTERNAL_EVALUATOR_USERS = "fetchExternalEvaluatorUsers";

	public static final String INTERNAL_EVALUATOR_NAMES = "internalEvaluatorNames";
	public static final String INTERNAL_LIST = "loInternalList";
	public static final String EXTERNAL_EVALUATOR_NAMES = "externalEvaluatorNames";
	public static final String EXTERNAL_LIST = "loExternalList";
	public static final String LO_PROCUREMENT_ID = "loProcurementId";
	public static final String EVALUATOR_COUNT_NEW = "loEvalutorsCountNew";
	public static final String ALL_EVALUATORS_MAP = "loAllEvaluatorsMap";
	public static final String SAVE_EVALUATION_SETTINGS = "saveEvaluationSettings";
	public static final String NEW_EVALUATOR_COUNT = "evaluatorCountNew";
	public static final String OLD_EVALUATOR_COUNT = "evaluatorCountOld";
	public static final String MORE_EVALUATOR_ERROR_MESSAGE = "moreEvaluatorErrorMessage";
	public static final String SAVE_EVALUATION_MAP = "saveEvaluatorMap";
	public static final String EVALUATION_SETTING_AGENCY_ID = "evaluationSettingAgencyId";
	public static final String SELECTED_FISCAL_YEAR = "selectedFYId";
	public static final String XPATH_NAME_SERVICE_OR_FUNCTION = "//element[((@type=\"Service Area\" or @type=\"Function\"))]";

	// InvoiceOperationSupport
	public static final String UNDERSCORE = "_";
	public static final String AGENCY_NAME_CHANGE = "agencyNameChange";
	public static final String FETCH_YTD_INVOICED = "fetchYTDInvoiced";
	public static final String FETCH_INVOICE_TOTAL_FOR_OTPS = "fetchInvoiceTotalForOTPS";
	public static final String FETCH_CS_YTD_INVOICED = "fetchCSYTDInvoiced";
	public static final String FETCH_INVOICE_TOTAL_FOR_CONTRACTED_SERVICES = "fetchInvoiceTotalForContractedServices";

	public static final String FETCH_INVOICE_STATUS = "fetchInvoiceStatus";

	public static final String EIGHT = "8";
	public static final String TRN_NEW_FY_CONFIG_FINISH_TASK = "finishNewFYConfigurationTask";

	public static final String QUERY_UPD_NEW_FY_BUDGET_CONFIG = "updateBudgetForNewFYConfigurationTask";
	public static final String QUERY_GET_BUDGET_ID = "fetchBudgetIdIfExists";
	public static final String QUERY_GET_SUB_BUDGET_AMOUNT = "fetchSubBudgetTotalAmount";

	// NewFY Configuration Task
	public static final Integer INT_FISCAL_YEAR_START_DAY_OF_MONTH = 1;
	public static final Integer INT_FISCAL_YEAR_START_MONTH = Calendar.JULY;
	public static final Integer INT_FISCAL_YEAR_END_DAY_OF_MONTH = 30;
	public static final Integer INT_FISCAL_YEAR_END_MONTH = Calendar.JUNE;
	public static final String TRN_GET_CONTRACT_END_DATE = "getContractEndDate";

	public static final String CONTRACT_ID_KEY = "asContractId";
	public static final String ARG_CONTRACT_END_ID = "asContractEndDate";

	public static final String FINANCIAL_PDF_DOC_PATH = "financialPDFDocPath";

	// Input Parameter Class
	public static final String INPUT_PARAM_CLASS_PROPOSAL_DETAILS_BEAN = "com.nyc.hhs.model.ProposalDetailsBean";
	public static final String BUDGET_PENDING_SUBMISSION_STATUS_ID = "84";
	public static final String AO_CONTRACT_BUDGET_BEAN = "aoContractBudgetBean";
	public static final String HHS_AUDIT_BEAN_PATH = "com.nyc.hhs.model.HhsAuditBean";

	// Contract Budget Session
	public static final String CONTRACT_BUDGET_READ_ONLY = "contractBudgetReadonly";
	public static final String INVOICE_READ_ONLY = "invoiceReadonly";
	public static final String STRING_ZERO = "0";
	// Contracted Services
	public static final String CONTRACTED_SERVICES = "contractedServices";
	public static final String CONTRACTED_INVOICING_SERVICES = "contractedServicesInvoicing";
	public static final String FETCH_NONGRID_CONTRACTED_SERVICES = "getNonGridContractedServices";
	public static final String CONTRACTED_SERVICES_DISPLAY = "contractedDisplay";
	// Contracted Services Ends

	// Operation and support start
	public static final String OPERATION_AND_SUPPORT_DATA = "loCBOperationSupportBean";
	public static final String OPERATION_AND_SUPPORT_JSP_NAME = "operationAndSupport";
	public static final String GET_OPERATION_SUPP_PAGE_DATA = "getOpAndSupportPageData";
	public static final String GET_OPERATION_SUPP_MOD_PAGE_DATA = "getOpAndSupportModPageData";
	public static final String FETCH_OPERATION_SUPP_MOD_PAGE_DATA = "fetchOpAndSupportModPageData";
	public static final String GET_OPERATION_SUPP_AMEND_PAGE_DATA = "getOpAndSupportAmendPageData";
	public static final String FETCH_OPERATION_SUPP_AMEND_PAGE_DATA = "fetchOpAndSupportAmendPageData";
	// Operation and support end

	public static final String FRINGE_TOTAL = "Fringe Total";

	public static final String PERSONNEL_SERVICES_JSP_NAME = "personnelServices";
	public static final String FETCH_PERSONNEL_SERVICES_DATA = "fetchPersonnelServiceData";
	public static final String PERSONNEL_SERVICES_DATA = "personnelServiceData";
	public static final String EVALUATION_REVIEW_TASK = "isEvaluationScoreSend";

	public static final String CONTRACT_TYPE_ID_KEY1 = "contactTypeId1";
	public static final String CONTRACT_TYPE_ID_KEY5 = "contactTypeId5";
	public static final String AWARD_MAP = "loAwardMap";
	public static final String ACTION_GET_AWARD_AND_CONTRACTS_LIST = "actionGetawardAndContractsList";
	public static final String AWARD_LIST = "awardList";
	public static final String AWARD_AND_CONTRACTS = "awardAndContracts";
	public static final String AWARD_BEAN = "AwardBean";

	public static final String AO_BUDGET_BEAN = "aoBudgetBean";
	public static final String AS_ORG_TYPE = "asOrgType";
	public static final String AS_PROCESS_TYPE = "asProcessType";
	public static final String BUDGETLIST_BUDGET = "Budget";
	public static final String AS_USER_TYPE = "asUserType";
	public static final String AGENCY_ACCO_MANAGER = "AGENCY_ACCO_MANAGER";
	public static final String EVALUATION_STATUS_NOT_SENT = "NO";

	public static final Integer HUNDRED = 100;
	public static final Integer INT_TEN = 10;

	// Accept Proposal Task
	public static final String AO_TASK_DETAILS_BEAN = "aoTaskDetailsBean";
	public static final String REQ_PROPS_HASHMAP = "reqPropsMap";
	public static final String PROPOSAL_DETAILS_KEY = "Proposal Details";
	public static final String NA_KEY = "N/A";
	public static final String REQUIRED_FLAG = "Required";
	public static final String DOC_REQUIRED_FLAG = "docReqFlag";
	public static final String PREV_PROC_STATUS = "PREV_PROC_STATUS";
	public static final String CURR_PROC_STATUS = "CURR_PROC_STATUS";
	public static final String PROPOSAL_TASK_STATUS = "proposalTaskStatus";
	public static final String PROPOSAL_DOCUMENT_LIST = "proposalDocumentDetailList";
	public static final String RFP_DOCUMENT_LIST = "rfpDocumentsList";
	public static final String TASK_HISTORY_LIST = "taskHistoryList";
	public static final String PROVIDER_COMMENTS = "providerComments";
	public static final String INTERNAL_COMMENTS = "internalComments";
	public static final String ACCEPT_PROPOSAL = "Accept Proposal";
	public static final String REVIEW_EVALUATION_TASK = "Evaluation";
	public static final String STATUS_VERIFIED = "Verified";
	public static final String STATUS_NON_RESPONSIVE = "Non-Responsive";
	public static final String ACCEPTED_FOR_EVALUATION = "Accepted for Evaluation";
	public static final String PROPOSAL_ACCEPTED_FOR_EVALUATION_KEY = "PROPOSAL_ACCEPTED_FOR_EVALUATION";
	public static final String DOCUMENT_NON_RESPONSIVE_KEY = "DOCUMENT_NON_RESPONSIVE";
	public static final String PROVIDER_COMMENTS_DATA = "Provider Comments";
	public static final String AGENCY_COMMENTS_DATA = "Agency Comments";
	public static final String DELIMITER_DOUBLE_HASH = "##";
	public static final String TASK_ASSIGNMENT = "Task Assignment";
	public static final String TASK_ASSIGNED_TO = "Task Assigned to";
	public static final String PROPOSAL_STATUS_ID_KEY = "proposalStatusId";
	public static final String CONTRACT_STATUS_ID_KEY = "contractStatusId";
	public static final String LINKAGE_ID_VALUE_FOR_SAVE_IN_BULK = "linkageIdValuesForSaveInBulk";
	public static final String NT207 = "NT207";
	public static final String AL212 = "AL212";
	public static final String FETCH_ACCEPT_PROPOSAL_TASK_DETAILS = "fetchAcceptProposalTaskDetails";
	public static final String PROPOSAL_DETAIL_MAP = "proposalDetailsMap";
	public static final String REQUIRED_QUESTION_DOCUMENT = "loRequiredQuestionDocument";
	public static final String PROP_SUBMIT_DATE = "PROP_SUBMIT_DATE";
	public static final String VIEW_RESPONSE_FROM_AGENCY_TASK = "viewResponseFromAgencyTask";
	public static final String AUDIT_BEAN_LIST = "auditBeanList";
	public static final String PROPOSAL_DOC_DETAILS = "proposalDocDetails";
	public static final String ASSIGNED_STATUS = "assignedstatus";
	public static final String SAVE_ACCEPT_PROPOSAL_TASK_DETAILS = "saveAcceptProposalTaskDetails";
	public static final String SAVE_EVALUATION_REVIEW_TASK = "saveEvaluationReviewTaskDetails";
	public static final String REASSIGNED_TO = "reassignedTo";
	public static final String REASSIGNED_TO_USER_NAME = "reassignedToUserName";
	public static final String REASSIGN_WF_TASK = "reassignWFTask";
	public static final String FINISH_ACCEPT_PROPOSAL_TASK = "finishAcceptProposalTask";
	public static final String FINISH_EVALUATION_REVIEW_TASK = "finishEvaluationReviewTask";
	public static final String NT213 = "NT213";
	public static final String AL218 = "AL218";
	public static final String RENDER_MARK_NON_RESPONSIVE = "renderMarkNonResponsive";
	public static final String STATUS_CHANGED_FROM = "Status Changed from";
	public static final String STATUS_SUBMITTED = "Submitted";
	public static final String FETCH_RFP_RELEASE_DOCS_DETAILS = "fetchRfpReleaseDocsDetails";
	public static final String PROC_SUBMIT_SUCCESS = "ProcSubmitSuccess";

	// Contract Budget Service
	public static final String CBY_INSERT_UTILITIES_DETAILS = "insertUtilitiesDetails";
	public static final String CBY_FETCH_UTILITIES_TYPE_DETAILS = "fetchUtilitiesTypeDetails";
	public static final String INPUT_PARAM_CLASS_CBGRID_BEAN = "com.nyc.hhs.model.CBGridBean";
	public static final String CS_TASK_DETAILS_BEAN = "com.nyc.hhs.model.TaskDetailsBean";
	public static final String CBY_FETCH_UTILITY_ITEMS_COUNT = "fetchUtilityItemsCount";
	public static final String CBY_ADD_PROF_SERVICES_DETAILS = "addProfServicesDetails";
	public static final String CBY_FETCH_PROF_SERVICES_ITEMS_COUNT = "fetchProfServicesItemsCount";
	public static final String CBY_INSERT_STANDARD_ROWS_OPERATION_SUPPORT = "insertStandardRowsOperationSupport";
	public static final String CBY_INSERT_CONTRACT_FIN_REPLICA_FOR_ORIGINAL = "insertContractFinReplicaForOriginal";
	public static final String CBY_INSERT_CONTRACT_FIN_FUNDING_REPLICA_FOR_ORIGINAL = "insertContractFinFundingReplicaForOriginal";
	public static final String CBY_FETCH_OPERATION_SUPP_MASTER_LIST = "fetchOperationSuppMasterList";
	public static final String CBY_FETCH_OPERATION_SUPPORT_ITEMS_COUNT = "fetchOperationSupportItemsCount";
	public static final String CBY_FETCH_SUB_BUDGET_ID_LIST = "fetchSubBudgetIDList";
	public static final String CBY_SET_CONTRACT_BUDGET_STATUS_FOR_REVIEW_TASK = "setContractBudgetStatusForReviewTask";
	public static final String FETCH_FRINGES_YTD_AMOUNT = "fetchFringesYTDInvoicedAmount";
	public static final String CBY_SEASONAL_EMPLOYEE_GRID_ADD = "seasonalEmployeeGridAdd";
	public static final String CBY_HOURLY_EMPLOYEE_GRID_ADD = "hourlyEmployeeGridAdd";
	public static final String CBY_SALARIED_EMPLOYEE_GRID_ADD = "salariedEmployeeGridAdd";
	public static final String CBY_SET_CONTRACT_BUDGET_STATUS = "setContractBudgetStatus";
	public static final String CBY_UPDATE_BUDGET_FINANCIAL_DOCUMENT_TABLE = "updateBudgetFinancialDocumentTable";
	public static final String CBY_UPDATE_CONTRACT_FINANCIAL_DOCUMENT_TABLE = "updateContractFinancialDocumentTable";
	public static final String CBY_DELETE_BUDGET_FINANCIAL_DOC = "deleteBudgetFinancialDoc";
	public static final String CBY_DELETE_INVOICE_FINANCIAL_DOC = "deleteInvoiceFinancialDoc";
	public static final String RPD_DELETE_RETURNED_PAYMENT_FINANCIAL_DOC = "deleteReturnedPaymentDoc";
	public static final String CBY_DELETE_CONTRACT_FINANCIAL_DOC = "deleteContractFinancialDoc";
	public static final String CBY_INSERT_CONTRACT_DOCUMENT_DETAILS = "insertContractDocumentDetails";
	public static final String CBY_FETCH_FINANCIAL_DOCUMENTS = "fetchFinancialDocuments";
	public static final String RPD_FETCH_RETURNED_PAYMENT_DOCUMENTS = "fetchReturnedPaymentDocuments";
	public static final String CBY_GET_SEQ_FOR_RATE = "getSeqForRate";
	public static final String JAVA_UTIL_MAP = "java.util.Map";
	public static final String CBY_INSERT_BUDGET_DOCUMENT_DETAILS = "insertBudgetDocumentDetails";
	public static final String CBY_INSERT_INVOICE_DOCUMENT_DETAILS = "insertInvoiceDocumentDetails";
	public static final String CBY_FETCH_ASSIGNMENT_SUMMARY = "fetchAssignmentSummary";
	public static final String CBY_FETCH_ASSIGNMENT_SUMMARY_FOR_PARENT_BUDGET = "fetchAssignmentSummaryForParentBudget";
	public static final String CBY_GET_ACT_PAID_AMOUNT = "getActPaidAmount";
	public static final String CBY_GET_INVOICE_AMOUNT = "getInvoiceAmount";
	public static final String CBY_FETCH_SUB_BUDGET_SUMMARY_PRINT = "fetchSubBudgetSummaryPrint";
	public static final String FETCH_CONTRACT_SUMMARY = "fetchContractSummary";
	public static final String FETCH_CONTRACT_SUMMARY_AMENDMENT = "fetchContractSummaryAmendment";
	public static final String FETCH_DATA_BASECONTRACT = "fetchDataFromBaseContract";
	public static final String CBY_DELETE_RATE_LIST = "deleteRateList";
	public static final String CBY_INSERT_RATE_LIST = "insertRateList";
	public static final String CBY_UPDATE_RATE_LIST = "updateRateList";
	public static final String CBY_COM_NYC_HHS_MODEL_RATE_BEAN = "com.nyc.hhs.model.RateBean";
	public static final String CBY_FETCH_RATE_LIST = "fetchRateList";
	public static final String CBY_FETCH_NON_GRID_CONTRACTED_SERVICES = "fetchNonGridContractedServices";
	public static final String CBY_DEL_CONTRACTED_SERVICES = "delContractedServices";
	public static final String CBY_EDIT_CONTRACTED_SERVICES = "editContractedServices";
	public static final String CBY_ADD_CONTRACTED_SERVICES = "addContractedServices";
	public static final String CBY_COM_NYC_HHS_MODEL_CONTRACTED_SERVICES_BEAN = "com.nyc.hhs.model.ContractedServicesBean";
	public static final String CBY_FETCH_CONTRACTED_SERVICES_VENDORS = "fetchContractedServicesVendors";
	public static final String CBY_FETCH_CONTRACTED_SERVICES_SUB_CONTRACTORS = "fetchContractedServicesSubContractors";
	public static final String CBY_FETCH_CONTRACTED_SERVICES_CONSULTANTS = "fetchContractedServicesConsultants";
	public static final String CBY_FETCH_CONTRACTED_SERVICES_INVOICING_VENDORS = "fetchContractedServicesInvoicingVendors";
	public static final String CBY_FETCH_CONTRACTED_SERVICES_INVOICING_SUB_CONTRACTORS = "fetchContractedServicesInvoicingSubContractors";
	public static final String CBY_FETCH_CONTRACTED_SERVICES_INVOICING_CONSULTANTS = "fetchContractedServicesInvoicingConsultants";
	public static final String CBY_FETCH_INVOICE_SEQUENCE_NUMBER = "fetchContractInvoiceSeqNo";
	public static final String CBY_FETCH_INVOICE_BUDGET_SEQUENCE_NUMBER = "fetchBudgetInvoiceSeqNo";
	public static final String CBY_FETCH_INVOICE_NUMBER = "fetchInvoiceSeqNo";
	public static final String CBY_UPDAT_BUDGET_UPDATE_STATUS = "updatBudgetUpdateStatus";
	public static final String CBY_UPDATE_BUDGET_MODIFICATION_STATUS = "updateBudgetModificationStatus";
	public static final String CBY_UPDATE_BUDGET_STATUS = "updateBudgetStatus";
	public static final String INPUT_PARAM_CLASS_CONTRACT_BUDGET_BEAN = "com.nyc.hhs.model.ContractBudgetBean";
	public static final String CBY_UPDATE_BUDGET_AMENDMENT_STATUS = "updateBudgetAmendmentStatus";
	public static final String CBY_DELETE_EQUIPMENT_DETAILS = "deleteEquipmentDetails";
	public static final String EDIT_EQUIPMENT_DETAILS = "editEquipmentDetails";
	public static final String CBY_ADD_EQUIPMENT_DETAILS = "addEquipmentDetails";
	public static final String FETCH_EQUIPMENT_DETAILS = "fetchEquipmentDetails";
	public static final String CBY_UPDATE_OP_AND_SUPPRT_FOR_OTHER = "updateOpAndSupprtForOther";
	public static final String CBY_ADD_OP_AND_SUPPRT_FOR_OTHER = "addOpAndSupprtForOther";
	public static final String CBY_FETCH_OP_AND_SUPPRT_FOR_OTHER = "fetchOpAndSupprtForOther";
	public static final String CBY_EDIT_OPERATION_AND_SUPPORT_DETAILS = "editOperationAndSupportDetails";
	public static final String CBY_FETCH_OPERATION_AND_SUPPORT_DETAILS = "fetchOperationAndSupportDetails";
	public static final String CBY_UPDATE_MILESTONE_INVOICE_DETAILS = "updateMilestoneInvoiceDetails";
	public static final String CBY_FETCH_MILESTONE_INVOICE_DETAILS = "fetchMilestoneInvoiceDetails";
	public static final String CBY_DELETE_MILESTONE_DETAILS = "deleteMilestoneDetails";
	public static final String CBY_UPDATE_MILESTONE_DETAILS = "updateMilestoneDetails";
	public static final String CBY_COM_NYC_HHS_MODEL_CB_MILE_STONE_BEAN = "com.nyc.hhs.model.CBMileStoneBean";
	public static final String CBY_INSERT_MILESTONE_DETAILS = "insertMilestoneDetails";
	public static final String CBY_GET_SEQ_FOR_MILESTONE = "getSeqForMilestone";
	public static final String CBY_FETCH_MILESTONE_DETAILS = "fetchMilestoneDetails";
	public static final String CBY_INSERT_FRINGE_BENIFITS = "insertFringeBenifits";
	public static final String CBY_INSERT_STANDARD_FRINGE_BENEFITS = "insertStandardFringeBenefits";
	public static final String CBY_UPDATE_FRINGE_BENIFITS = "updateFringeBenifits";
	public static final String CBY_FETCH_FRING_BENEFITS = "fetchFringBenifits";
	public static final String CBY_FETCH_SEASONAL_EMPLOYEE = "fetchSeasonalEmployee";
	public static final String CBY_FETCH_HOURLY_EMPLOYEE = "fetchHourlyEmployee";
	public static final String CBY_UPDATE_PERSONNEL_SERVICES = "updatePersonnelServices";
	public static final String CBY_DELETE_PERSONNEL_SERVICES = "deletePersonnelServices";
	public static final String CBY_INSERT_PERSONNEL_SERVICES = "insertPersonnelServices";
	public static final String CBY_FETCH_SALRIED_EMPLOYEE = "fetchSalariedEmployee";
	public static final String CBY_UPDATE_PROF_SERVICES_FOR_OTHER = "updateProfServicesForOther";
	public static final String CBY_ADD_PROF_SERVICES_FOR_OTHER = "addProfServicesForOther";
	public static final String CBY_FETCH_PROF_SERVICES_FOR_OTHER = "fetchProfServicesForOther";
	public static final String CBY_COM_NYC_HHS_MODEL_CB_PROFESSIONAL_SERVICES_BEAN = "com.nyc.hhs.model.CBProfessionalServicesBean";
	public static final String CBY_UPDATE_PROFESSIONAL_SERVICES_DETAILS = "updateProfessionalServicesDetails";
	public static final String CBY_ADD_PROFESSIONAL_SERVICES_DETAILS = "addProfessionalServicesDetails";
	public static final String CBY_FETCH_PROFESSIONAL_SERVICES_TYPE_ID = "fetchProfessionalServicesTypeId";
	public static final String CBY_FETCH_PROFESSIONAL_SERVICES_DETAILS = "fetchProfessionalServicesDetails";
	public static final String CBY_UPDATE_UTILITIES_UPDATE_DETAILS = "updateUtilitiesUpdateDetails";
	public static final String CBY_UPDATE_UTILITIES_MODIFY_DETAILS = "updateUtilitiesModifyDetails";
	public static final String CBY_INSERT_UTILITIES_MODIFY_DETAILS = "insertUtilitiesModifyDetails";
	public static final String CBY_FETCH_UTILITIES_TYPE_ID = "fetchUtilitiesTypeId";
	public static final String CBY_UPDATE_UTILITIES_DETAILS = "updateUtilitiesDetails";
	public static final String CBY_UPDATE_UTILITIES_AMENDMENT_DETAILS = "updateUtilitiesAmendmentDetails";
	public static final String CBY_COM_NYC_HHS_MODEL_CB_UTILITIES = "com.nyc.hhs.model.CBUtilities";
	public static final String CBY_FETCH_UTILITIES_UPDATE_DETAILS = "fetchUtilitiesUpdateDetails";
	public static final String CBY_FETCH_UTILITIES_MODIFY_DETAILS = "fetchUtilitiesModifyDetails";
	public static final String CBY_FETCH_UTILITIES_AMENDMENT_DETAILS = "fetchUtilitiesAmendmentDetails";
	public static final String CBY_FETCH_UTILITIES_DETAILS = "fetchUtilitiesDetails";
	public static final String CBY_UPDATE_INDIRECT_RATE_PERCENTAGE = "updateIndirectRatePercentage";
	public static final String CBY_UPDATE_INDIRECT_RATE_MODI_PERCENTAGE = "updateIndirectRateModificaitonPercentage";
	public static final String CBY_FETCH_INDIRECT_RATE_AMENDMENT = "fetchIndirectRateAmendment";
	public static final String CBY_FETCH_INDIRECT_RATE_MODIFICATION = "fetchIndirectRateModification";
	public static final String CBY_FETCH_INDIRECT_RATE_AMENDMENT_NEW_RECORD = "fetchIndirectRateAmendmentNewRecord";
	public static final String CBY_FETCH_RENT_AMENDMENT_NEW_RECORD = "fetchRentAmendmentNewRecord";
	public static final String FETCH_INDIRECT_RATE_UPDATION = "fetchIndirectRateUpdation";
	public static final String CBY_FETCH_INDIRECT_RATE = "fetchIndirectRate";
	public static final String CBY_UPDATE_INDIRECT_RATE_AMENDMENT = "updateIndirectRateAmendment";
	public static final String CBY_UPDATE_INDIRECT_RATE_MODIFICATION = "updateIndirectRateModification";
	public static final String CBY_UPDATE_INDIRECT_RATE_UPDATION = "updateIndirectRateUpdation";
	public static final String CBY_INSERT_INDIRECT_RATE_MODIFICATION = "insertIndirectRateModification";
	public static final String FETCH_PREV_BUDGET = "fecthPrevApprovedBudget";
	public static final String CBY_GET_SEQ_FOR_RENT = "getSeqForRent";
	public static final String CBY_DELETE_CONTRACT_FINANCIAL_ENTRIES = "deleteContractFinancialEntries";
	public static final String CBY_DELETE_CONTRACT_FIN_FUNDING_ENTRIES = "deleteContractFinFundingEntries";
	public static final String CBY_DELETE_CONTRACT_BUDGET_RENT = "deleteContractBudgetRent";
	public static final String CBY_INSERT_CONTRACT_BUDGET_RENT = "insertContractBudgetRent";
	public static final String CBY_COM_NYC_HHS_MODEL_RENT = "com.nyc.hhs.model.Rent";
	public static final String CBY_UPDATE_CONTRACT_BUDGET_RENT = "updateContractBudgetRent";
	//Added in R6: return payment review task
	public static final String CBY_UPDATE_RETURN_PAYMENT_DETAIL = "updateReturnPaymentDetail";
	//Added in R6: return payment review task end
	public static final String CBY_FETCH_CONTRACT_BUDGET_RENT = "fetchContractBudgetRent";
	public static final String FETCH_INDIRECT_RATE_PERCENTAGE = "fetchIndirectRatePercentage";
	public static final String CBY_FETCH_CITY_FUNDED_BUDGET = "fetchCityFundedBudget";
	public static final String FETCH_BUDGET_SUMMARY = "fetchBudgetSummary";
	public static final String FETCH_MODIFICATION_BUDGET_SUMMARY = "fetchModificationBudgetSummary";

	// Evaluation Filter
	public static final String EVALUATION_COMPLETED = "Evalution_Completed";
	public static final String EVALUATIONS_COMPLETED = "evalutionsCompleted";
	public static final String GET_EVALUATION_STATUS = "getEvaluationStatus";
	public static final String PROPOSAL_EVALUATION_ACTION = "propEval";
	public static final String PROVIDER_NAME_PARAM = "asProviderName";
	public static final String FETCH_PROVIDER_NAME_LIST_QUERY_ID = "fetchProviderNameList";

	public static final String JAVA_LANG_STRING = "java.lang.String";
	public static final String PERCENT = "%";
	public static final String FETCH_EVALUATION_STATUS = "fetchEvaluationStatus";
	public static final String JAVA_UTIL_HASH_MAP = "java.util.HashMap";

	public static final String FETCH_TOTAL_SALARY = "fetchTotalSalary";
	public static final String FETCH_TOTAL_FRINGES = "fetchTotalFringes";
	public static final String FETCH_SALARIED_YTD_AMOUNT = "fetchSalariedYTDInvoicedAmount";
	public static final String FETCH_PERSONNEL_SERVICE_MASTER_DATA = "fetchPersonnelServiceMasterData";
	public static final String PERSONNEL_SERVICES_MASTER_DATA = "personnelServiceMasterData";

	public static final String PROPOSAL_STATUS_ID = "PROPOSAL_STATUS_ID";

	public static final String INTEGER_CLASS_PATH = "java.lang.Integer";

	// Constants for Program Income Module Starts
	public static final String FETCH_PROGRAM_INCOME = "fetchProgramIncome";
	public static final String UPDATE_PROGRAM_INCOME = "updateProgramIncome";
	public static final String FETCH_PROGRAM_INCOME_MODIFICATION = "fetchProgramIncomeModification";
	public static final String UPDATE_PROGRAM_INCOME_MODIFICATION = "updateProgramIncomeModification";
	public static final String INSERT_PROGRAM_INCOME_MODIFICATION = "insertProgramIncomeModification";
	public static final String FETCH_PROGRAM_INCOME_MODIFICATION_PARENT_EQUAL_SUB = "fetchProgramIncomeModificationParentEqualSub";
	public static final String FETCH_PROGRAM_INCOME_MOD_AMT_DETAILS = "fetchProgramIncomeModAmtDetails";
	public static final String CBM_FETCH_PROG_INCOME_DETAILS_FOR_VALIDATION = "fetchProgIncomeDetailsForValidation";
	public static final String CBM_FETCH_PROG_INCOME_DETAILS_FOR_VALIDATION_IN_MULTIPLE_AMENDMENTS = "fetchProgIncomeDetailsForValidationInMultipleAmendments";

	public static final String FETCH_PROGRAM_INCOME_UPDATE = "fetchProgramIncomeUpdate";
	public static final String UPDATE_PROGRAM_INCOME_UPDATE = "updateProgramIncomeUpdate";
	public static final String INSERT_PROGRAM_INCOME_UPDATE = "insertProgramIncomeUpdate";

	public static final String FETCH_PROGRAM_INCOME_AMENDMENT = "fetchProgramIncomeAmendment";
	public static final String UPDATE_PROGRAM_INCOME_AMENDMENT = "updateProgramIncomeAmendment";
	public static final String INSERT_PROGRAM_INCOME_AMENDMENT = "insertProgramIncomeAmendment";

	public static final String PROPOSED_BUDGET_LESS_THAN_APPROVED = "proposedBudgetLlessThanApproved";

	public static final String FETCH_PROGRAM_INCOME_MOD_UPD_AMEND = "fetchProgramIncomeModUpdAmend";
	public static final String UPDATE_PROGRAM_INCOME_MOD_UPD_AMEND = "updateProgramIncomeModUpdAmend";

	public static final String FETCH_PROGRAM_INCOME_INVOICE_ITEMS_COUNT = "fetchProgramIncomeInvoiceItemsCount";
	public static final String PROGRAM_INCOME_BEAN = "com.nyc.hhs.model.CBProgramIncomeBean";
	public static final String INSERT_PROGRAM_INCOME = "insertProgramIncome";
	public static final String FETCH_PROGRAM_INCOME_FOR_OTHER = "fetchProgramIncomeForOther";
	public static final String ADD_PROGRAM_INCOME_FOR_OTHER = "addProgramIncomeForOther";
	public static final String UPDATE_PROGRAM_INCOME_FOR_OTHER = "updateProgramIncomeForOther";
	public static final String FETCH_PROGRAM_INCOME_MASTER_TYPES = "fetchProgramIncomeMasterTypes";
	public static final String FETCH_PROGRAM_INCOME_ITEMS_COUNT = "fetchProgramIncomeItemsCount";
	// Constants for Program Income Module Ends

	public static final String CANCEL_MODIFICATION_BUDGET_ID = "cancelModificationBudgetId";
	public static final String TRAN_CANCEL_MODIFICATION_BUDGET = "cancelModificationBudget";
	public static final String CANCEL_MODIFICATION_BUDGET_STATUS = "cancelModificationBudgetStatus";
	public static final String TERMINATION_FLAG = "lbTerminationFlag";
	public static final String CANCEL_BUDGET_MODIFICATION_ERROR = "cancelBudgetModificationErr";
	public static final String WORKFLOW_TERMINATION_ERROR = "workflowTerminationErr";
	public static final String CANCEL_BUDGET_MODIFICATION_SUCCESS = "cancelBudgetModificationSuccess";
	public static final String SET_MODIFICATION_BUDGET_STATUS = "setModificationBudgetStatus";
	public static final String FETCH_PARENT_BUDGET_ID = "fetchParentBudgetId";
	public static final String FETCH_PARENT_CONTRACT_ID1 = "fetchParentContractId";
	//Start R7 defect 8644
	public static final String FETCH_AMEND_CONTRACT_ID = "fetchAmendContractId";
	public static final String BUDGET_RETURNED_FOR_REVISION_STATUS_ID = "83";
	//End R7
	public static final String FETCH_CONTRACT_AMENDMENT_AMOUNT = "fetchContractAmendmentAmount";
	public static final String FETCH_PARENT_SUB_BUDGET_ID = "fetchParentSubBudgetId";

	// Evaluate Proposal Task

	public static final String SCORES_RETURNED = "Scores Returned";
	public static final String SCORES_ACCEPTED = "Scores Accepted";
	public static final String EVALUATOR_NAME = "evaluatorName";
	public static final String FETCH_EVALUATE_PROPOSAL_TASK = "fetchEvaluateProposalTaskDetails";
	public static final String SAVE_EVALUATE_PROPOSAL_TASK_DETAILS = "saveEvaluateProposalTaskDetails";
	public static final String FETCH_EVALUATOR_DETAILS = "fetchEvaluatorDetails";
	public static final String FINISH_EVALUATE_PROPOSAL_TASK = "finishEvaluateProposalTask";
	public static final String SCORE_LIST = "scoreDetailList";
	public static final String EVALUATE_PROPOSAL = "Evaluate Proposal";

	// Review Scores Task

	public static final String FETCH_EVALUATION_SCORES_TASK = "fetchEvaluationScoresTask";

	// Procurement Controller starts
	public static final String VIEW_PROCUREMENT = "viewProcurement";
	public static final String AGENCY_ORG = "isAgencyOrg";
	public static final String ERROR_MSG_PROCUREMENT_SAVE = "errorMsgProcurementSave";
	public static final String PROCUREMENT = "Procurement";
	public static final String PROCUREMENT_COUNT = "procurementCount";
	public static final String PROCUREMENT_STATUS_CHANGED_CLOSE = "Procurement Status has been changed to Closed";
	public static final String PROCUREMENT_STATUS_CHANGED_EVALUATION_COMPLETE = "Procurement Status has been changed to Evaluation Complete";
	public static final String FINALIZE_UPDATE = "Finalize/Update";
	public static final String FINALIZE_UPDATE_AWARDS_RESULTS = "Finalize/Update Results";
	public static final String CLOSE = "Close";
	public static final String SELECTED_PROGRAM_NAME = "selectedProgramName";
	public static final String FILTERED = "filtered";
	public static final String PROPOSAL_FILTERED = "proposalFiltered";
	public static final String PROPOSAL_FILTERED_RESULT = "proposalFilteredResult";
	public static final String PROC_FILTERED = "procFiltered";
	public static final String CLOSE_PROCUREMENT = "closeProcurement";
	public static final String CANCEL_PROCUREMENT = "cancelProcurement";
	public static final String FETCH_USER_PROGRAM_NAME_LIST = "fetchUserProgramNameList";
	public static final String ERROR_WHILE_PROCESSING_HYPERLINK = "Error Occured while processing action on click of hyperlink";
	public static final String ERROR_WHILE_RENDERING_PROC_DETAILS = "Error occurred while rendering procurement details";
	public static final String ERROR_WHILE_RENDERING_PUB_PROCUREMENT = "Error occurred while rendering Publish Procurement";
	public static final String ERROR_WHILE_CLOSING_PROCUREMENT = "Error occurred while closing procurement";
	public static final String ERROR_WHILE_RENDERING_SERVICE_PROVIDERS_PROCUREMENT = "Exception Occured while rendering Services and providers details";
	public static final String ACC_USER_LIST = "accUserList";
	public static final String LO_SERVICE_DATA = "loServiceData";
	public static final String PROC_SERVICE_ASS_ERR = "procurementServiceAssErr";
	public static final String PUBLISH_PROCUREMENT = "Publish Procurement";
	public static final String NULL = "null";
	public static final String DRAFT = "Draft";
	public static final String AGENCY_USER_DROPDOWN = "agencyUserDropDown";
	public static final String AGENCY_USER_LIST = "agencyUserList";
	public static final String SAVE_ACTION = "saveAction";
	public static final String HIDDEN_AGENCY = "hiddenAgency";
	public static final String HIDDEN_OPEN_ENDED_FLAG = "hiddenOpenEndedFlag";
	public static final String PROCUREMENT_DETAILS = "procurementDetails";
	public static final String PROCUREMENT_STATUS_FLAG = "procurementStatusFlag";
	public static final String SAVE_ROCUREMENT_SUMMARY = "saveProcurementSummary";
	public static final String AO_PROCUREMENT_BEAN = "aoProcurementBean";
	public static final String IS_RENDER_ACTION = "lsRenderAction";
	public static final String GET_NEW_PROCUREMENT_DETAILS = "getNewProcurementDetails";
	public static final String LO_PROCUREMENT_BEAN = "loProcurementBean";
	public static final String E_PIN_ID = "ePinId";
	public static final String LS_EPIN_KEY = "lsEpinKey";
	public static final String FETCH_EPIN_DETAILS = "fetchEpinDetails";
	public static final String FETCH_EPIN_DETAILS_DB = "fetchEpinDetails_db";
	public static final String AO_EPIN_DETAILS_BEAN = "aoEpinDetailsBean";
	public static final String PROCUREMENT_EPIN_SEARCH = "procurementEpinSearch";
	public static final String EPIN_DETAILS = "epinDetails";
	public static final String PROCUREMENT_START_DATE = "ProcurementStartDate";
	public static final String AGENCY_DIV = "AgencyDiv";
	public static final String DESCRIPTION = "Description";
	public static final String PROJ_PROG = "ProjProg";
	public static final String PROGRAM_NAME_UPPERCASE = "Program Name:";
	public static final String PROGRAM_NAME_LOWERCASE = "programName";
	public static final String PROGRAM_NAME_ID = "programNameId";
	public static final String NAME_TO_BE_DISPLAYED = "nameToBeDisplayed";
	public static final String NAME_OF_THE_DROPDOWN = "nameOfTheDropDown";
	public static final String DROPDOWN_TO_BE_CHANGED = "dropDownToBeChanged";
	public static final String GET_PROCUREMENT_SUMMARY = "getProcurementSummary";
	public static final String GET_PROPOSAL_SUMMARY = "getProposalSummary";
	public static final String GET_PROPOSAL_COUNT = "getProposalCount";
	public static final String GET_PROPOSAL_SUMMARY_PROPOSAL_DUE_DATE = "getProposalSummaryProposalDueDate";
	public static final String ACCELERATOR_USER_LIST = "acceleratorUserList";
	public static final String PROCUREMENT_STATUS = "procurementStatus";
	public static final String PROPOSAL_COUNT = "proposalCount";
	public static final String USER_ROLE = "user_role";
	public static final String NYC_AGENCY = "nycAgency";
	public static final String ACC_USER_DROPDOWN = "accUserDropDown";
	public static final String DISP_CAN_CLO_PROC_HYPERLINKS = "displayCancelCloseProcHyperlinks";
	public static final String DIS_FIELDS_FOR_PLANNED_STATUS = "disableFieldsForPlannedStatus";
	public static final String DIS_FIELDS_FOR_DRAFT_STATUS = "disableFieldsForDraftStatus";
	public static final String DIS_PROGRAM_NAME_DROP_DOWN = "disableProgramNameDropDown";
	public static final String DIS_ACC_USER_DROP_DOWN = "disableAccUserDropDown";
	public static final String DIS_AGENCY_USER_DROP_DOWN = "disableAgencyUserDropDown";
	public static final String MAKE_CHANGES_FOR_AGENCY = "makeChangesForAgency";
	public static final String LB_HYPERLINK_DISP_STATUS = "lbHyperlinkDisplayStatus";
	public static final String LB_PROC_STATUS_DRAFT = "lbProcurementStatusDraft";
	public static final String LB_PROC_STATUS_NOT_DRAFT = "lbProcurementStatusNotDraft";
	public static final String LB_PROG_NAME_DROPDOWN_SATUS = "lbProgramNameDropDownStatus";
	public static final String LB_ACC_USER_DROPDOWN_STATUS = "lbAccUserDropDownStatus";
	public static final String LB_AGENCY_USER_DROPDOWN_STATUS = "lbAgencyUserDropDownStatus";
	public static final String LB_AGENCY_USER_TYPE = "lbAgencyUserType";
	public static final String ERROR_MSG = "error_msg";
	public static final String SERVICE_SELECTION = "serviceSelection";
	public static final String SERVICE_SELECTION_UPPERCASE = "SERVICE_SELECTION";
	public static final String ORGTYPE = "orgType";
	public static final String AB_FROM_CACHE = "abFromCache";
	public static final String AGENCY_ROLE = "agencyRole";
	public static final String LO_ELEMENT_ID_LIST = "loElementIdList";
	public static final String SERVICE_SELECTION_SAVE_RULE = "ServiceSelectionSaveRule";
	public static final String ACTIVE_FLAG_LIST = "activeFlagList";
	public static final String GET_SELECTED_SERVICE_ACC = "getSelectedServiceAccelerator";
	public static final String LO_TAX_TREE = "loTaxonomyTree";
	public static final String IS_SAVE = "isSave";
	public static final String SAVED_SERVICES_LIST = "savedServicesList";
	public static final String SELECTED_SERVICES_LIST = "selectedServiceList";
	public static final String LO_INSERT_LIST = "loInsertList";
	public static final String FINAL_TREE_AS_STRING = "finalTreeAsString";
	public static final String SAVE_BUTTON_STATUS = "saveButtonStatus";
	public static final String PUB_PROC_FLAG = "PublishProcurementFlag";
	public static final String SER_NAME_LIST = "serviceNameList";
	public static final String PUB_PROC = "publishProcurement";
	public static final String AUTH_BEAN = "AuthenticationBean";
	public static final String AUTH_BEAN_LOWERCASE = "authenticationBean";
	public static final String YES = "YES";
	public static final String LB_AUTH_STATUS = "lbAuthStatus";
	public static final String SERV_NAME_MAP = "serviceNameMap";
	public static final String EVIDENCE_ERROR_FLAG = "evidenceErrorFlag";
	public static final String SERV_LIST_ERROR = "servicesListError";
	public static final String RENDER_PUB_PROC = "renderpublishProcurement";
	public static final String RESET_SESSION_PROC = "resetSessionProcurement";
	public static final String LB_UPDATE_SUCC = "lbUpdateSuccessful";
	public static final String PROC_PUB_SUCC = "procPublishedSuccessfully";
	public static final String SEL_SERVICES = "selectedService";
	public static final String SELECT_TAXONOMY_DETAILS_FROM_DBR2 = "selectTaxonomyDetailsFromDBR2";
	public static final String MOD_BY_USER_ID = "modifiedByUserId";
	public static final String LO_LAST_MOD_HASHMAP = "loLastModifiedHashMap";
	public static final String SAVE_SERVICES = "saveServices";
	public static final String SAVE = "save";
	public static final String LO_INSERT_SEL_SERVICES_LIST = "loInsertSelectedServiceList";
	public static final String INS_UPD_SER_ACC = "insertUpdateServiceAccelerator";
	public static final String SERVICE_SEL_RENDER = "serviceSelectionRender";
	public static final String LS_PRO_ID = "lsProcurementId";
	public static final String LO_SAVE = "loSave";
	public static final String AO_AUTH_PARAM = "aoAuthenticateParam";
	public static final String AO_AUTH_STATUS_FLAG = "aoAuthStatusFlag";
	public static final String VAL_STATUS_FLAG = "validateStatusFlag";
	public static final String ORGANIZATION_ID = "organizationId";
	public static final String IS_PUB_PROC = "isPublishProcurement";
	public static final String PROC_STATUS_CHANGED_TO_PUB = "Procurement Status has been changed to Planned";
	public static final String PROCUREMENT_PUBLISHED = "Procurement has been Published";
	public static final String NO_EPIN_ENTRIES = "There are no E-PINs that match the entry.";
	public static final String ERROR_RENDER_EPIN_DETAILS = "Error while rendering epin details";
	public static final String PUBLISH = "Publish";
	public static final String RELEASE_DATE = "releaseDate";
	public static final String AL216 = "AL216";
	public static final String LO_HM_NOTIFY_PARAM = "loHmNotifyParam";
	public static final String PRO_INPUT_MAP = "procurementInputMap";
	public static final String AUDIT_BEAN = "auditBean";
	public static final String AUTH_STATUS_FLAG = "authStatusFlag";
	public static final String TOP_LEVEL_FROM_REQ = "topLevelFromRequest";
	public static final String PROC_SUM_PROVIDER = "procurementSummaryProvider";
	public static final String DROPDOWN_STATUS = "dropDownStatus";
	public static final String GEN_BUTTON_STATUS = "generateButtonStatus";
	public static final String FLAG_UNCHECKED_STATUS = "FlagUncheckedServiceList";
	public static final String STAT = "status";
	public static final String PREV_STATUS = "prevStatus";
	public static final String STATUS_CHANNEL = "statusChannel";
	public static final String AB_FETCH_DEF = "abFetchDefault";
	public static final String FETCH_PAGE_LOAD_DETAILS = "fetchOnPageLoadDetails";
	public static final String LO_SELECTED_SER_LIST = "loSelectedServicesList";
	public static final String LS_SELECTED_TYPE = "lsSelectedType";
	public static final String LO_APPROVED_PROVIDERS_LIST = "loApprovedProvidersList";
	public static final String SELECTED_TYPE = "selectedType";
	public static final String APP_PRO_LIST = "approvedProvidersList";
	public static final String NO_APP_PROVS = "noApprovedProvs";
	public static final String CHANGE_DROPDOWN_VALUES = "changeDropDownValue";
	public static final String GET_USER_ROLES = "getUserRoles";
	public static final String APP_PROVIDERS = "approvedProviders";
	public static final String CHANGE_DROPDOWN_VAL_PROVIDER = "changeDropDownValueProvider";
	public static final String SER_ELEMENT_ID_LIST = "serviceElementIdList";
	public static final String SEL_SER_LIST = "selectedServicesList";
	public static final String SEL_PROV_DROPDOWN_VAL = "SelectedProvDropDownValue";
	public static final String BASE_FILTER = "baseFilter";
	public static final String RENDER_SERVICES_AND_PROV_INFO = "renderServicesAndProviderInfo";
	public static final String FETCH_APP_PROVIDERS_LIST = "fetchApprovedProvidersList";
	public static final String FETCH_APP_PROVIDERS_LIST_RELEASED = "fetchApprovedProvidersListReleased";
	public static final String TEXT_HTML = "text/html";
	public static final String RET_QUE_ANS_WITH_COMMENTS = "retrieve_questionanswer_withcomments";
	public static final String LO_FORM_INFO = "loFormInformation";
	public static final String BASIC = "Basic";
	public static final String ORG_PROFILE = "OrgProfile";
	public static final String OPEN_NEW_WINDOW = "openNewWindow";
	public static final String ORG_READ_ONLY_SCREEN = "organizationReadOnlyScreen";
	public static final String SEL_BOX_DROPDOWN = "selectionBoxDropDown";
	public static final String SEL_SERVE_BEAN = "selectedServBean";
	public static final String SAVE_APP_PROV_DETAILS = "saveAppProvDetails";
	public static final String LS_OUTPUT = "lsOutput";
	public static final String MID_LEVEL_FROM_REQ = "midLevelFromRequest";
	public static final String EVE_REQ_FLAG = "evidencerequiredflag";
	public static final String NAME = "name";
	public static final String SERVICE_SELECTION_VALIDATION = "serviceSelectionValidation";
	public static final String CAN_PROC_ID = "cancelProcurementId";
	public static final String FETCH_PROG_NAME_LIST = "fetchProgramNameList";
	public static final String AO_FILENET_SESSION = "aoFilenetSession";
	public static final String AO_OUTPUT_PATH = "asOutputFilePath";
	public static final String DEL_PRO_DATA_DRAFT = "DeleteProviderDataDraft";
	public static final String DEL_PRO_DATA_PLAN = "DeleteProviderDataPlanned";
	public static final String DEL_PRO_DATA_REL = "DeleteProviderDataReleased";
	public static final String PROC_STATUS_CODE = "procurementStatusCode";
	public static final String LO_DEL_PRO_DATA_MAP = "loDeleteProviderDataMap";
	public static final String LO_STATUS_UPDATE_MAP = "loStatusUpdateMap";
	public static final String LO_STATUS_UPDATE_FILENET = "loStatusUpdateFilenet";
	public static final String ELT_ID = "eltId";
	public static final String PROC_STATUS_CHANGED_CANCEL = "Procurement Status has been changed to Cancelled";
	public static final String CANCEL = "Cancel";
	public static final String APPROVED_PROVIDERS = "ApprovedProviders";
	public static final String CANCEL_PROC = "Cancel Procurement";
	public static final String PROC_SUCCESS = "ProcurementSuccess";
	public static final String DIS_SER_AND_PROV = "displayServiceAndProviders";
	public static final String DROPDOWN_VAL = "lsDropDownValue";
	public static final String LO_APP_PROV = "loApprovedProviders";
	public static final String DISP_RFF_DOC_LIST = "displayRFPDocumentList";
	public static final String HYPERLINK1_ACTION = "hyperlink1Action";
	public static final String HYPERLINK2_ACTION = "hyperlink2Action";
	public static final String CLOSE_PROC = "Close Procurement";
	public static final String CLOSE_PROC_ID = "closeProcurementId";
	public static final String FOR_ACTION = "forAction";
	public static final String AWARD_EPIN_ID = "awardEpinId";
	public static final String LS_AWARD_EPIN = "lsAwardEpin";
	public static final String LO_AWARD_EPIN_MAP = "loAwardEpinMap";
	public static final String ASSIGN_APT_AWARD_EPIN = "assignAPTAwardEpin";
	public static final String AWARD_CONTRACT = "awardContract";
	public static final String ALL_LINKAGES = "allLinkages";
	public static final String PREVIOUS_ID = "previousIds";
	public static final String ADD_NEW_TAG_FLAG = "addNewTagFlag";
	public static final String MODIFIERS = "modifiers";

	// Procurement Service starts

	public static final String FETCH_ACTIVE_PROC_FOR_PROVIDER = "fetchActiveProcurementsForProvider";
	public static final String COM_NYC_HHS_MODEL_PROC = "com.nyc.hhs.model.Procurement";
	public static final String GET_PROC_COUNT_FOR_PROVIDER = "getProcurementCountForProvider";
	public static final String GET_PROC_COUNT = "getProcurementCount";
	public static final String GET_PROG_NAME_LIST = "getProgramNameList";
	public static final String GET_PROC_STATUS = "getProcurementStatus";
	public static final String GET_PROC_PREV_STATUS = "getProcurementPrevStatus";
	public static final String GET_ELEMENT_ID_LIST = "getElementIdList";
	public static final String FETCH_APPROVED_PROVIDERS = "fetchApprovedProviders";
	public static final String FETCH_PROPOSAL_CUSTOM_QUE = "fetchProposalCustomQuestions";
	public static final String GET_PROC_ID = "getProcurementId";
	public static final String DEL_SEL_SERV_LIST = "deleteSelectedServicesList";
	public static final String YES_LOWERCASE = "yes";
	public static final String FETCH_PROPOSAL_DOC_TYPE = "fetchProposalDocumentType";
	public static final String DROP_DOWN_STAGING = "dropDownStaging";
	public static final String ADD_PROCUREMENT_LOWERCASE = "addprocurement";
	public static final String SAVE_SEL_SERV_LIST = "saveSelectedServicesList";
	public static final String EVIDENCE_SERVICE_LIST = "evidenceServiceList";
	public static final String COM_NYC_HHS_MODEL_SELSERV_BEAN = "com.nyc.hhs.model.SelectedServicesBean";
	public static final String EXCEPTION_OCCURED_RENDERING_PROC_DETAILS = "Exception Occured while rendering procurement details";
	public static final String EXCEPTION_OCCURED_RENDERING_PROC_DETAILS_COLON = "Exception Occured while rendering procurement details : ";
	public static final String ERROR_OCCURED_FETCHING_APPROVED_PROVIDERS = "Error occured while fetching approved providers";
	public static final String DEL_ADD_SERV_LIST = "deleteAddendumServicesList";
	public static final String SAVE_ADD_SERV_LIST = "saveAddendumServicesList";
	public static final String AS_EPIN_KEY = "asEpinKey";
	public static final String AO_USER_TYPE = "aoUserTpye";
	public static final String LO_ACC_USER_LIST = "loAcceleratorUserList";
	public static final String GET_ACC_USER_LIST = "getAcceleratorUserList";
	public static final String GET_AGENCY_USER_LIST = "getAgencyUserList";
	public static final String AS_STATUS_ID = "asStatusId";
	public static final String YES_UPPERCASE = "Y";
	public static final String NO_UPPERCASE = "N";
	public static final String INS_NEW_PROC_DETAILS = "insertNewProcurementDetails";
	public static final String UPDATE_DRAFT_PROC_DETAILS = "updateDraftProcurementDetails";
	public static final String UPDATE_PROC_DETAILS = "updateProcurementDetails";
	public static final String UPDATE_PROC_ADD_DETAILS = "updateProcurementAddendumDetails";
	public static final String INS_PROC_ADD_DETAILS = "insertProcurementAddendumDetails";
	public static final String EPIN = "epin";
	public static final String UPDATE_PROC_EPIN_MASTER_TABLE = "updateProcurementEpinInMasterTable";
	public static final String PROCESS_TYPE = "processType";
	public static final String GET_STATUS_ID = "getStatusId";
	public static final String GET_RELEASES_PROC_SUMMARY = "getReleasedProcurementSummary";
	public static final String FETCH_PROC_SUMMARY_PROVIDER = "fetchProcurementSummaryProvider";
	public static final String LO_NODE_LIST = "loNodeList";
	public static final String LS_COMPLETE_TREE = "lsCompleteTree";
	public static final String ELEMENT_ID_LIST = "elementIdList";
	public static final String AO_PCOF_INPUT_MAP = "aoPcofInputMap";
	public static final String AO_SERVICE_DATA = "aoServiceData";
	public static final String ADD_PROC_COF_INFO = "addProcurementCoFInfo";
	public static final String AO_PROC_MAP = "aoProcurementMap";
	public static final String AO_SERV_NAMES = "aoServiceNames";
	public static final String UPDATE_PROC_DATA = "updateProcurementData";
	public static final String UPDATE_PROC_STATUS = "updateProcurementStatus";
	public static final String UPDATE_PROPOSAL_STATUS_NON_RESPONSIVE = "updateproposalstatusnonresponsive";
	public static final String UPDATE_PROC_DATA_PUBLISH = "updateProcurementDataOnPublish";
	public static final String UPDATE_PROC_STATUS_PUBLISH = "updateProcurementStatusOnPublish";
	public static final String AS_PROC_ID = "asProcId";
	public static final String AO_SERVICE_MAP = "aoServiceMap";
	public static final String AB_VALIDATE_STATUS = "abValidateStatus";
	public static final String AB_PROC_STATUS_FLAG = "abProcurementStatusFlag";
	public static final String DEL_PROC_SERVICE_DATA = "deleteProcurementServiceData";
	public static final String UPDATE_PROC_SERVICE_DATA = "updateProcurementServiceData";
	public static final String RFP_DOC_STATUS_CODE = "rfpDocumentStatusCode";
	public static final String AS_AGENCY_USER_ID = "asAgencyUserId";
	public static final String AS_PROVIDER_USER_ID = "asProviderUserId";
	public static final String AS_STATUS_PROPOSAL_SEL_ID = "asStatusProposalSelectedId";
	public static final String GET_PROC_DETAILS_NAV8 = "getProcurementDetailsForNavE8";
	public static final String GET_PROC_DETAILS_NAV7 = "getProcurementDetailsForNavE7";
	public static final String GET_PROC_DETAILS_NAV6 = "getProcurementDetailsForNavE6";
	public static final String GET_PROC_DETAILS_NAV5 = "getProcurementDetailsForNavE5";
	public static final String AS_PROVIDER_USERID = "asProviderUserId :: ";
	public static final String AS_PROCUREMENT_ID = "asProcurementId :: ";
	public static final String EG = "EG";
	public static final String ES = "ES";
	public static final String E11 = "E11";
	public static final String E10 = "E10";
	public static final String E8 = "E8";
	public static final String EA8 = "EA8";
	public static final String E7 = "E7";
	public static final String E6 = "E6";
	public static final String E5 = "E5";
	public static final String E4 = "E4";
	public static final String H = "H";
	public static final String E = "E";
	public static final String D = "D";
	public static final String AO_PARAMETER_MAP = "aoParameterMap";
	public static final String INS_RFP_DOC_DETAILS = "insertRfpDocumentDetails";
	public static final String INS_ADENDUM_DOC_DETAILS = "insertAdendumDocumentDetails";
	public static final String FETCH_ELEMENT_ID = "fetchElementId";
	public static final String FETCH_SERV_ELEMENT_ID = "fetchServiceElementId";
	public static final String FETCH_APPROVED_PROV_DETAILS = "fetchApprovedProvDetails";
	public static final String AS_SELECTED_PROV_DROPDOWN_VALUE = "asSelectedProvDropDownValue";
	public static final String AO_SELECTED_SERVICE_LIST = "aoSelectedServiceList";
	public static final String AS_ELEMENT_ID = "asElementId";
	public static final String AO_BASE_FILTER = "aoBaseFilter";
	public static final String FORM_VERSION = "FORM_VERSION";
	public static final String SIZE = "size";
	//start qc_8927
	public static final String MOUSE_OVER_TOOL_TIP = "mouseOverToolTip";
	public static final int    DEFAULT_MAX_COLUMN_SIZE=12;
	public static final int    MAX_COLUMN_SIZE=12;
	public static final String BEGIN_PRE_TAG= "<pre>";
	public static final String END_PRE_TAG= "</pre>";
	public static final String THREE_DOTS= "...";
	public static final String TWO_DOTS= "..";
	//end qc_8927
	public static final String FIRST_SORT = "firstSort";
	public static final String FIRST_SORT_TYPE = "firstSortType";
	public static final String FIRST_SORT_DATE = "firstSortDate";
	public static final String SECOND_SORT = "secondSort";
	public static final String SECOND_SORT_TYPE = "secondSortType";
	public static final String TYPE = "type";
	public static final String PARENT_ID = "parentid";
	public static final String OR = "or";
	public static final String UNDEFINED = "undefined";
	public static final String AND = "and";
	public static final String SERVICE_FILTER = "serviceFilter";
	public static final String FETCH_APP_PROVIDERS_LIST_AFTER_REL = "fetchApprovedProvidersListAfterRelease";
	public static final String AB_FETCH_DEFAULT = "abFetchDefault :: ";
	public static final String AS_TABLE_NAME = "asTableName";
	public static final String TABLE = "TABLE";
	public static final String ORGANIZATIONID = "ORGANIZATION_ID = '";
	public static final String BUSINESS_APP_ID = "BUSINESS_APPLICATION_ID= '";
	public static final String AND_ORG_ID = "' AND ORGANIZATION_ID = '";
	public static final String AS_WHERE = "asWhere";
	public static final String GET_FORM_DETAILS = "getFormDetails";
	public static final String SECTION = "section";
	public static final String SUB_SECTION = "subSection";
	public static final String GET_FORM_DETAILS_OF_ORG = "getFormDetailsOfOrg";
	public static final String FORM_ID = "FORM_ID";
	public static final String FILING_FORM = "filing_form";
	public static final String GET_CORP_STRUCTURE_VAL = "getCorpStructureValue";
	public static final String BASIC_CS_VAL = "basic_cs_value";
	public static final String CORP_STRUCTURE_ID = "CORPORATE_STRUCTURE_ID";
	public static final String FETCH_WIDGET_DETAILS = "fetchWidgetDetails";
	public static final String CHECK_BEF_APP_PROV_DETAILS = "checkBeforeSaveApprovedProvDetails";
	public static final String LS_PROC_STATUS = "lsProcurementStatus";
	public static final String FOR_PROVIDER = "forProvider";
	public static final String LS_DRAFT_ID = "lsDraftId";
	public static final String LS_PLANNED_ID = "lsPlannedId";
	public static final String UPDATE_DROPDOWN_VAL_DRAFT = "updateDropDownValueDraft";
	public static final String UPDATE_DROPDOWN_VAL_PLANNED = "updateDropDownValuePlanned";
	public static final String INS_DOP_DOWN_VAL_PLANNED = "insertDropDownValuePlanned";
	public static final String GET_PROVIDER_STATUS = "getProviderStatus";
	public static final String PROC_ID = "procurement id :: ";
	public static final String AO_STATUS_MAP = "aoStatusMap";
	public static final String SAVE_STATUS = "saveStatus";
	public static final String DEL_PROVIDER_DATA = "deleteProvidersData";
	public static final String PRESERVE_OLD_STAUS = "preserveOldStatus";
	public static final String AI_PROC_ID = "aiProcurementId";
	public static final String GET_ATTACHED_AWARD_DOCS = "getAttachedAwardDocuments";
	public static final String AB_IS_AGENCY_ORG = "abIsAgencyOrg";
	public static final String GET_SAVED_SERVICES_LIST = "getSavedServicesList";
	public static final String LO_SAVED_SERVICES_LIST = "loSavedServicesList";
	public static final String FETCH_DOC_ID_LIST = "fetchdocumentIdList";
	public static final String AO_NOTIFICATION_MAP = "aoNotificationMap";
	public static final String GET_PROC_TITLE = "getProcurementTitle";
	public static final String GET_CONTRACT_DETAILS_FOR_NOTIFICATIONS = "getContractDetailsForNotifications";

	public static final String PROC_TITLE = "PROCUREMENT_TITLE";
	public static final String CONTRACT_TITLE = "CONTRACT_TITLE";
	public static final String AGENCY_PRIMARY_CONTACT = "AGENCY_PRIMARY_CONTACT";
	public static final String AGENCY_SEC_CONTACT = "AGENCY_SECONDARY_CONTACT";
	public static final String AB_EXE_SERVICE = "abExecuteService";
	public static final String QUESTION_FLAG = "QuestionFlag";
	public static final String DEL_ADD_PROPOSAL_CUSTOM_QUE = "deleteAddendumProposalCustomQuestions";
	public static final String QUE_SEQ_NO = "QuestionSeqNo";
	public static final String UPDATE_ADD_PROPOSAL_CUSTOM_QUE = "updateAddendumProposalCustomQuestions";
	public static final String COM_NYC_MODEL_PROP_QUE_ANS_BEAN = "com.nyc.hhs.model.ProposalQuestionAnswerBean";
	public static final String INS_ADD_PROPOSAL_CUSTOM_QUE = "insertAddendumProposalCustomQuestions";
	public static final String DEL_PROPOSAL_CUSTOM_QUE = "deleteProposalCustomQuestions";
	public static final String UPDATE_PROPOSAL_CUSTOM_QUE = "updateProposalCustomQuestions";
	public static final String INS_PROPOSAL_CUSTOM_QUE = "insertProposalCustomQuestions";
	public static final String DOCUMENT_TYPE = "documentType";
	public static final String DOC_SEQ_NO = "documentSeqNumber";
	public static final String DEL_ADD_PROPOSAL_DOC_TYPE = "deleteAddendumProposalDocumentType";
	public static final String UPDATE_ADD_PRO_DOC_TYPE = "updateAddendumProposalDocumentType";
	public static final String INS_ADD_PROPOSAL_DOC_TYPE = "insertAddendumProposalDocumentType";
	public static final String COM_NYC_HHS_MODEL_EXTENDED_DOC = "com.nyc.hhs.model.ExtendedDocument";
	public static final String DEL_PROPOSAL_DOC_TYPE = "deleteProposalDocumentType";
	public static final String UPDATE_PROPOSAL_DOC_TYPE = "updateProposalDocumentType";
	public static final String INS_PROPOSAL_DOC_TYPE = "insertProposalDocumentType";
	public static final String AS_PROC_STATUS = "asProcStatus";
	public static final String AB_COF_FLAG = "abCofFlag";
	public static final String AWARD_SELECTION_DETAILS = "loAwardSelectionDetails";
	public static final String FINALIZE_UPDATE_RESULTS = "finalizeupdateresults";
	public static final String FETCH_DROP_DOWN_VALUE = "fetchDropDownValue";
	public static final String DISPLAY_APP_PROVIDER_ON_PAGE_LOAD = "displayAppProviderOnPageLoad";
	public static final String LO_WORKFLOW_NAME = "loWorkflowName";

	// RFPRelease Controller starts
	public static final String FINAL_RFP_DOC_LIST = "finalRFPDocumentList";
	public static final String VIEW_DOC_INFO_UPPERCASE = "viewDocumentInfo";
	public static final String UPDATE_AWARD_DOC_PROPERTIES = "updateAwardDocumentProperties";
	public static final String PROPOSAL_STATUS_FLAG = "proposalStatusFlag";
	public static final String PROPOSAL_DETAILS_BEAN_UPPERCASE = "ProposalDetailsBean";
	public static final String SITE_DETAILS_BEAN_UPPERCASE = "SiteDetailsBean";

	public static final String FETCH_PROC_COF = "fetchProcurementCoF";
	public static final String PROC_COF_DETAILS = "procurementCoFDetails";
	public static final String PROCID = "ProcID";
	public static final String FINANCIALS = "financials";
	public static final String FINANCIALS_MESSAGE = "financialsMessage";
	public static final String PROC_COF = "ProcurementCOF";
	public static final String DOCUMENT_BEAN = "documentBean";
	public static final String STATUS_COF = "statusCOF";
	public static final String ERROR_OCCURED_RENDERING_PROPOSAL_DETAILS = "Error occurred while rendering proposal details";
	public static final String PROPOSAL_LOWERCASE = "proposal";
	public static final String PROPOSAL_CONFIG_UPPERCASE = "ProposalConfiguration";
	public static final String RFP_DOC = "RFPDocuments";
	public static final String GET_RFP_DOCS = "getRfpDocuments_db";
	public static final String AO_RFP_DOC_LIST = "aoRFPDocumentList";
	public static final String RFP_DOC_UPLOAD_FILE = "RFPDocumentUploadRule";
	public static final String GENERIC_ERROR_MESSAGE = "GENERIC_ERROR_MESSAGE";
	public static final String SHOW_UPLOAD_DOC = "showUploadDocument";
	public static final String UPLOAD = "upload";
	public static final String DISPLAY_UPLOAD_DOC_INFO_PROVIDER = "displayUploadingDocumentInfoProvider";
	public static final String RFP_DOC_UPLOAD_SUCCESS = "RFP_DOC_UPLOAD_SUCCESS";
	public static final String RFP_REL_DOC = "rfpReleaseDocument";
	public static final String COMPETITION_POOL_CONFIGURATION = "competitionPoolConfiguration";
	public static final String UPLOAD_DOC_TYPE = "uploadingDocumentType";
	public static final String UPLOAD_DOC_TYPE_FROM_ADD_DOCUMENT = "uploadingDocumentTypeAdd";
	public static final String HIDDEN_DOC_REF_SEQ_NO = "hiddendocRefSeqNo";
	public static final String DISP_SUCCESS = "displaySuccess";
	public static final String DOC_ID = "documentId";
	public static final String DOC_REF_NO = "docReferenceNo";
	public static final String LO_HM_DOC_REQ_PROPS = "loHmDocReqProps";
	public static final String LB_SUCCESS_STATUS = "lbSuccessStatus";
	public static final String STATUS_ID = "statusId";
	public static final String INS_RFP_DOC_DETAILS_DB = "insertRfpDocumentDetails_db";
	public static final String DOCUMENT_COMPLETED = "DOCUMENT_COMPLETED";
	public static final String DOCUMENT_DRAFT = "DOCUMENT_DRAFT";
	public static final String INS_PROPOSAL_DOC_DETAILS_DB = "insertProposalDocumentDetails_db";
	public static final String AWARD_LOWER_CASE = "award";
	public static final String AWARD_ID = "awardId";
	public static final String THIRTY_SEVEN = "37";
	public static final String INS_AWARD_DOC_DETAILS_DB = "insertAwardDocumentDetails_db";
	public static final String UPLOADING_FILE_INFO = "uploadingFileInformation";
	public static final String GET_PROC_STATUS_DB = "getProcurementStatus_db";
	public static final String PREVENT_ADD_DOC = "PREVENT_ADD_DOC";
	public static final String DIS_NEXT = "disableNext";
	public static final String UPLOAD_DOC = "uploadDocument";
	public static final String DOCTYPE = "docType";
	public static final String LS_DOC_REF_SQ_NUM = "lsdocRefSeqNum";
	public static final String UPLOAD_RFP_AWARD_DOCS = "uploadRfpAndAwardDocs";
	public static final String ADD_DOC_FROM_VAULT = "addDocumentFromVault";
	public static final String PROVIDER_CONTRACT_ID = "providerContactId";
	public static final String AS_USER_ID = "asUserId";
	public static final String GET_MEMBER_DETAILS = "getMemberDetails";
	public static final String MEMBER_DETAILS = "memberDetails";
	public static final String DOC_TITLE = "docTitle";
	public static final String ADD_DOC_TYPE = "addDocType";
	public static final String DOC_CATEGORY_LOWERCASE = "docCategory";
	public static final String SUBMISSION_BY = "submissionBy";
	public static final String CREATION_DATE = "creationDate";
	public static final String AWARD_UPPER_CASE = "Award";
	public static final String SEL_DETAIL = "selectionDetail";
	public static final String VIEW_SEL_DETAILS = "viewSelectionDetails";
	public static final String DATE_LAST_MODIFIED = "DateLastModified";
	public static final String PROC_PROPOSAL_DOC_LIST = "procurementProposalDocumentList";
	public static final String BUDGET_RENDER_METHOD = "BudgetRenderMethod";
	public static final String RFP_DOC_ADDED_SUCCESS = "RFP_DOC_ADDED_SUCCESS";
	public static final String DOC_CATEGORY = "DOC_CATEGORY";
	public static final String HHS_DOC_CREATED_BY_ID = "HHS_DOC_CREATED_BY_ID";
	public static final String DOC_MODIFIED_DATE = "docModifedDate";
	public static final String DOC_MODIFIED_BY = "docModifedBy";
	public static final String DOC_STATUS = "docStatus";
	public static final String IS_ADD_TYPE = "isAddendumType";
	public static final String VIEW_DOC_INFO = "viewdocumentinfo";
	public static final String PROC_DOC_ID = "procurementDocId";
	public static final String MODIFIED_DATE = "modifiedDate";
	public static final String IS_ADDENDUM = "isAddendum";
	public static final String AO_MODIFIED_INFO_MAP = "aoModifiedInfoMap";
	public static final String UPDATE_RFP_DOC_PROPERTIES = "updateRfpDocumentProperties";
	public static final String UPDATE_PROPOSAL_DOC_PROPERTOES = "updateProposalDocumentProperties";
	public static final String LO_DOC_UPPDATE_STATUS = "loDocumentUpdateStatus";
	public static final String HIDDEN_DOC_REF = "hiddenDocReference";
	public static final String DEL_DOC_ID = "deleteDocumentId";
	public static final String AS_DEL_DOC_ID = "asDeletedDocumentId";
	public static final String DOC_REF_NUM = "docReferenceNum";
	public static final String AO_PARAM_MAP = "aoParamMap";
	public static final String ENTITY_LIST = "entityIdList";
	public static final String ENTITY_LIST_R2 = "entityIdListR2";
	public static final String SUB_ENTITY_LIST = "subEntityIdList";
	public static final String SUB_ENTITY_LIST_R2 = "subEntityIdListR2";
	public static final String REMOVE_RFP_DOCS_DB = "removeRfpDocs_db";
	public static final String REMOVE_PROPOSAL_DOCS = "removeProposalDocs";
	public static final String READ_ONLY_SEC = "readOnlySection";
	public static final String AS_ORG_ID = "asOrganizaionId";
	public static final String PROPOSAL_DETAIL_BEAN_FOR_SUB_BUDGET = "proposalDetailBeanForSubBudget";
	public static final String FETCH_PROPOSAL_DETAILS = "fetchProposalDetails";
	public static final String FETCH_PROPOSAL_SUMMARY = "fetchProposalSummary";
	public static final String PROPOSAL_DETAILS_BEAN = "proposalDetailsBean";
	public static final String CUSTOM_QUE_LIST = "customQuestionList";
	public static final String COMMENT_LIST = "commentList";
	public static final String SITE_DETAIL_LIST = "siteDetailList";
	public static final String RECORD_BEFORE_RELEASE = "recordBeforeRelease";
	public static final String FETCH_SUB_BUDGET_STATUS_ID = "fetchSubBudgetStatusId";
	public static final String ORG_MEMBER_LIST = "orgMemberList";
	public static final String PROPOSAL_COMMENT_LIST = "proposalCommentsList";
	public static final String LO_ORG_MEM_LIST = "loOrgMemList";
	public static final String LO_COMMENT_BEAN = "loCommentBean";
	public static final String COMMENT_SIZE = "commentSize";
	public static final String PROPOSAL_STATUS = "proposalStatus";
	public static final String PROPOSALID = " proposal id:: ";
	public static final String PROCUREMENTID = " procurement id:: ";
	public static final String PROC_PROPOSAL_DETAILS = "procurementProposalDetails";
	public static final String SAVE_TYPE = "saveType";
	public static final String AO_PROP_DETAILS = "aoPropDetails";
	public static final String AO_SUB_BUDGET_DETAILS = "aoSubBudgetDetails";
	public static final String SAVE_PROP_DETAILS = "saveProposalDetails";
	public static final String SAVE_SUB_BUDGET_DETAILS = "saveSubBudgetDetails";
	public static final String SITE_NAME = "siteName";
	public static final String ADDRESS1 = "address1";
	public static final String ADDRESS2 = "address2";
	public static final String CITY_BUDGET = "city";
	public static final String STATE_BUDGET = "state";
	public static final String ZIP_CODE = "zipCode";
	public static final String ADDRESS_RELATED_DATA = "addressRelatedData";
	public static final String ACTION_TAKEN = "actionTaken";

	public static final String SAVE_NEXT_BUTTON = "saveNextButton";
	public static final String AO_USER_SESSION = "aoUserSession";
	public static final String AS_DOC_TYPE = "asDocType";
	public static final String GET_DOC_CONTENT_FOR_SUB_PROPOSAL = "getDocumentContentForSubmitProposal";
	public static final String PROPOSAL_MAP = "proposalMap";
	public static final String DISPLAY_TERMS_CONDITION = "displayTermsCondition";
	public static final String PROPOSAL_TITLE = "proposalTitle";
	public static final String PROVIDER_SUBMIT_PROPOSAL = "provider/providerSubmitProposal";
	public static final String AUTHENICATION = "Authentication";
	public static final String RENDER_PROVIDER_PROPOSAL = "renderProviderProposal";
	public static final String PROPOSAL = "Proposal";
	public static final String PROPOSAL_STATUS_CHANGED_TO_SUBMITTED = "Proposal status has been changed to submitted";
	public static final String SUBMIT = "Submit";
	public static final String PROPOSAL_SUBMIT = "Proposal Submit";
	public static final String SUBMIT_PROPOSAL = "submitProposal";
	public static final String REQ_FIELDS_FLAG = "requiredFieldsFlag";
	public static final String REQ_FIELDS_SUBMIT_PROPOSAL = "requiredFieldsSubmitProposal";
	public static final String UPDATE_PROPOSAL_FLAG = "updateProposalFlag";
	public static final String PROPOSAL_SUBMIT_SUCCESFULLY = "proposalSubmittedSuccessfully";
	public static final String PROPOSAL_SUBMIT_FAIL = "proposalSubmitFailed";
	public static final String PROPOSAL_SUMMARY_LOWERCASE = "proposalSummary";
	public static final String MISSING_INFO_LIST = "missingInfoList";
	public static final String RELEASE_RFP = "releaserfp";
	public static final String RENDER_RELEASE_RFP = "renderReleaseRfp";
	public static final String PROC_STATUS_CHANGED_RELEASED = "Procurement Status has been changed to Released";
	public static final String RELEASE = "Release";
	public static final String SELECTIONS_MADE = "Selections Made";
	public static final String PROC_RELEASE = "Procurement Release";
	public static final String AL201 = "AL201";
	public static final String AL202 = "AL202";
	public static final String NT201 = "NT201";
	public static final String NT202 = "NT202";
	public static final String LINK = "LINK";
	public static final String LINK1 = "LINK1";
	public static final String LINK2 = "LINK2";
	public static final String PROCUREMENT_MAP = "procurementMap";
	public static final String REL_RFP_EVI_CHECK = "releaseRFPEvidenceCheck";
	public static final String RFP_REQUISITES = "rfpRequisites";
	public static final String APPROVED_CFP_FLAGS = "approvedCofFlag";
	public static final String REL_RFP_MISSING_INFO = "releaseRFPMissingInfo";
	public static final String PUBLISH_RFP_MISSING_INFO = "publishRFPMissingInfo";
	public static final String ASSIGN_EPIN_BEFORE_RELEASE = "assignEpinBeforeRelease";
	public static final String PROC_SERVICE_MISSING = "procurementServiceMissing";
	public static final String PROC_COF_NOT_APPROVED = "procCOFNotApproved";
	public static final String REQ_DOC_TYPE_MISSING = "requiredDocTypeMissing";
	public static final String RFP_DOC_MISSING = "rfpDocumentMissing";
	public static final String EVAL_CRITERIA_MISSING = "evalCriteriaMissing";
	public static final String PROC_RELEASED_SUCCESFULLY = "procReleasedSuccessfully";
	public static final String NEXT_ACTION = "nextAction";
	public static final String EVAL_CRITERIA = "evaluationCriteria";
	public static final String EVAL_CRITERIA_DETAILS = "evaluationCriteriaDetails";
	public static final String SAVE_EVAL_CRITERIA = "saveEvaluationCriteria";
	public static final String IS_REL_PROCUREMENT = "isReleaseProcurement";
	public static final String RFP_REL_BEAN = "RFPReleaseBean";
	public static final String PROC_CFP_DETAILS = "procurementCOFDetails";
	public static final String PARAMETERS_MAP = "prametersMap";
	public static final String GET_PROP_DOC_LIST = "getProposalDocumentList";
	public static final String PROP_DOC_MAP = "proposaldocumentsMap";
	public static final String PROP_TITLE = "PROPOSAL_TITLE";
	public static final String STATUS_UPPERCASE = "STATUS";
	public static final String OPTIONAL_PROPOSAL_DOC_LIST = "optionalProposalDocumentList";
	public static final String PROVIDER_PROPOSAL_DOCS = "provider/proposalDocuments";
	public static final String REL_ADDENDUM = "releaseAddendum";
	public static final String AUTH_FAIL = "authenticateFail";
	public static final String RENDER_REL_ADDENDUM = "renderReleaseAddendum";
	public static final String AL214 = "AL214";
	public static final String AL215 = "AL215";
	public static final String NT209 = "NT209";
	public static final String NEW_ADD_RELEASED = "New Addendum has been released";
	public static final String ADD_RELEASE = "Addendum Release";
	public static final String LB_AUTH_FLAG = "lbAuthFlag";
	public static final String LB_ADD_DOC = "lbAddendumDoc";
	public static final String LS_USER_ORG_TYPE = "lsUserOrgType";
	public static final String LS_SORT_TYPE = "lsSortType";
	public static final String FETCH_PROPOSAL_CONFIG_DETAILS = "fetchProposalConfigurationDetails";
	public static final String PROPOSAL_CUSTOM_QUE_LIST = "proposalCustomQuestionList";
	public static final String LO_CUSTOM_QUE_LIST = "loCustomQuestionList";
	public static final String PROPOSAL_DOC_TYPE_LIST = "proposalDocTypeList";
	public static final String PROPOSAL_CONFIG_SAVE_RULE = "ProposalConfigurationSaveRule";
	public static final String DOC_TYPE_LIST = "documentTypeList";
	public static final String IS_READ_ONLY = "isReadOnly";
	public static final String PROPOSAL_CONFIG = "proposalconfiguration";
	public static final String LS_USER_ID = "lsUserId";
	public static final String LAST_MOD_HASHMAP = "lastModifiedHashMap";
	public static final String SAVE_PROPOSAL_CONGFIG_DETAILS = "saveProposalConfigurationDetails";
	public static final String CURRENT_PROC_ID = "currentProcurementId";
	public static final String PROC_ID_LOWERCASE = "procID";
	public static final String CONTRACT_START_DATE_UPPERCASE = "ContractStartDate";
	public static final String CONTRACT_END_DATE_UPPERCASE = "ContractEndDate";
	public static final String AMENDMENT_START_DATE = "AmendmentStartDate";
	public static final String AMENDMENT_END_DATE = "AmendmentEndDate";
	public static final String PROC_COF_DETAILS_FETCH = "procurementCOFDetailsFetch";
	public static final String AO_RET_COF_DETAILS = "aoReturnedCOFDetails";
	public static final String AO_RET_COA_LIST = "aoReturnedCOAList";
	public static final String RENDER_PROC_COF = "renderProcCOF";
	public static final String DETAIL_LIST = "DetailList";
	public static final String HEADER_LIST = "HeaderList";
	public static final String FUNDING_LIST = "FundingList";
	public static final String PROC_CERT_FUNDS_DOC = "procurementCertFundsDoc";
	public static final String AS_WF_STATUS_ID = "asWFStatusId";
	public static final String PROPOSAL_DOCUMENTS = "ProposalDocuments";
	public static final String DOC_DETAILS_UPDATED_SUCCESFULLY = "Document Details Updated SuccessFully";
	public static final String ERROR_OCCURED_SAVING_DOC_PROPERTIES = "Error Occured While Saving Document Properties";
	public static final String RFP_DOC_REMOVED_SUCCESS = "RFP_DOC_REMOVED_SUCCESS";
	public static final String DOC_REMOVED_SUCCESS = "Document Removed Successfully";
	public static final String AB_AUTH_STATUS_FLAG = "abAuthStatusFlag";
	public static final String SUBMIT_FINANCIALS_WF = "SubmitFinancialsWF";
	public static final String SET_PROC_COF_STATUS = "setProcurementCOFStatus";
	public static final String UPDATE_SELECTED_PROPOSAL_AWARD_AMOUNT = "updateSelectedProposalAwardAmount";
	public static final String UPDATE_NOT_SELECTED_PROPOSAL_AWARD_AMOUNT = "updateNotSelectedProposalAwardAmount";
	public static final String UPDATE_SELECTED_PROPOSAL_COMMENTS = "updateSelectedProposalComments";
	public static final String UPDATE_NOT_SELECTED_PROPOSAL_COMMENTS = "updateNotSelectedProposalComments";
	public static final String UPDATE_SELECTED_PROPOSAL_STATUS = "updateSelectedProposalStatus";
	public static final String UPDATE_PROPOSAL_REVIEW_STATUS = "updateProposalReviewStatus";
	public static final String UPDATE_NOT_SELECTED_PROPOSAL_STATUS = "updateNotSelectedProposalStatus";
	public static final String EVAL_BEAN = "EvalBean";
	public static final String AWARD_AMOUNT = "awardAmount";
	public static final String LOCAL_COUNT = "loCount";
	public static final String UPDATE_SELECTED_PROPOSAL_DETAILS = "updateSelectedProposalDetails";
	public static final String UPDATE_NOT_SELECTED_PROPOSAL_DETAILS = "updateNotSelectedProposalDetails";

	// RFPRelease Service starts
	public static final String FETCH_EPIN_VALUE = "fetchEpinValue";
	public static final String FETCH_RFP_PREREQ = "fetchRfpPreRequisites";
	public static final String FETCH_PROC_CERT_OF_FUNDS = "fetchProcCertOfFunds";
	public static final String SAVE_APPROVED_PROVIDERS = "saveApprovedProviders";
	public static final String SAVE_APPROVED_PROVIDERS_SERVICES = "saveApprovedProvidersServices";
	public static final String FETCH_ADD_EVAL_CRITERIA = "fetchAddendumEvaluationCriteria";
	public static final String FETCH_EVAL_CRIERIA = "fetchEvaluationCriteria";
	public static final String DETAIL_BEAN = "DetailBean";
	public static final String DOCUMENT_SUBMITTED = "DOCUMENT_SUBMITTED";
	public static final String DOC_SUBMITTED_STATUS = "docSubmittedStatus";
	public static final String CHECK_RFP_DOC_TYPE = "checkRfpDocumentType";
	public static final String ADDENDA = "Addenda";
	public static final String INS_RFP_DOC_DATA = "insertRfpDocumentData";
	public static final String INS_PROC_DOC_CONFIG = "insertProcDocumentConfig";
	public static final String INS_PROC_QUE_CONFIG = "insertProcQuestionConfig";
	public static final String DEL_EVAL_CRITERIA_DATA = "deleteEvaluationCriteriaData";
	public static final String INS_EVAL_CRITERIA = "insertEvaluationCriteria";
	public static final String DEL_RFP_ADD_DATA = "deleteRfpDocAddendunData";
	public static final String DEL_PROC_ADD_DOC = "deleteProcAddendumDocument";
	public static final String DEL_PROC_ADD_DATA = "deleteProcAddendunData";
	public static final String DEL_ADD_QUE_CONFIG = "deleteAddendumQuestionConfig";
	public static final String DEL_ADD_EVAL_CRITERIA = "deleteAddendumEvalCriteria";
	public static final String DEL_PROC_ADD_SERVICE = "deleteProcAddendumService";
	public static final String PROCID_LOWERCASE = "procId";
	public static final String ELIGIBLE_TO_PROPOSE = "eligibleToPropose";
	public static final String INS_NEW_APP_PROVIDERS = "insertNewAppProviders";
	public static final String UPDATE_APP_PROVIDERS = "updateAppProviders";
	public static final String DEL_EVAL_CRITERIA = "deleteEvaluationCriteria";
	public static final String UPDATE_ADD_EVAL_CRITERIA = "updateAddendumEvaluationCriteria";
	public static final String COM_NYC_HHSMODEL_EVAL_CRITERIA_BEAN = "com.nyc.hhs.model.EvaluationCriteriaBean";
	public static final String SAVE_ADD_EVAL_CRITERIA = "saveAddendumEvaluationCriteria";
	public static final String UPDATE_EVAL_CRITERIA = "updateEvaluationCriteria";
	public static final String DOCUMENT_STATUS = "documentStatus";
	public static final String DISPLAY_NEXT_BUTTON = "displayNextButton";

	public static final String STATUS_PROCUREMENT_DRAFT = "PROCUREMENT_DRAFT";
	public static final String STATUS_PROCUREMENT_PLANNED = "PROCUREMENT_PLANNED";
	public static final String STATUS_PROCUREMENT_RELEASED = "PROCUREMENT_RELEASED";
	public static final String STATUS_PROCUREMENT_PROPOSALS_RECEIVED = "PROCUREMENT_PROPOSALS_RECEIVED";
	public static final String STATUS_PROCUREMENT_EVALUATIONS_COMPLETE = "PROCUREMENT_EVALUATIONS_COMPLETE";
	public static final String STATUS_PROCUREMENT_SELECTIONS_MADE = "PROCUREMENT_SELECTIONS_MADE";
	public static final String STATUS_PROCUREMENT_CANCELLED = "PROCUREMENT_CANCELLED";
	public static final String STATUS_PROCUREMENT_CLOSED = "PROCUREMENT_CLOSED";
	public static final String STATUS_PROVIDER_ELIGIBLE_TO_PROPOSE = "PROVIDER_ELIGIBLE_TO_PROPOSE";
	public static final String STATUS_PROVIDER_SERVICE_APP_REQUIRED = "PROVIDER_SERVICE_APP_REQUIRED";
	public static final String STATUS_PROVIDER_DRAFT = "PROVIDER_DRAFT";
	public static final String STATUS_PROVIDER_SUBMITTED_PROPOSAL = "PROVIDER_SUBMITTED_PROPOSAL";
	public static final String STATUS_PROVIDER_NOT_SELECTED = "PROVIDER_NOT_SELECTED";
	public static final String STATUS_PROVIDER_SELECTED = "PROVIDER_SELECTED";
	public static final String STATUS_PROVIDER_DID_NOT_PROPOSE = "PROVIDER_DID_NOT_PROPOSE";
	public static final String STATUS_PROVIDER_NOT_APPLICABLE = "PROVIDER_NOT_APPLICABLE";
	public static final String STATUS_PROVIDER_NON_RESPONSIVE = "PROVIDER_NON_RESPONSIVE";
	public static final String STATUS_PROPOSAL_DRAFT = "PROPOSAL_DRAFT";
	public static final String STATUS_PROPOSAL_SUBMITTED = "PROPOSAL_SUBMITTED";
	public static final String STATUS_PROPOSAL_RETURNED_FOR_REVISION = "PROPOSAL_RETURNED_FOR_REVISION";
	public static final String STATUS_PROPOSAL_EVALUATED = "PROPOSAL_EVALUATED";
	public static final String STATUS_PROPOSAL_SCORES_RETURNED = "PROPOSAL_SCORES_RETURNED";
	public static final String STATUS_PROPOSAL_SELECTED = "PROPOSAL_SELECTED";
	public static final String STATUS_PROPOSAL_NOT_SELECTED = "PROPOSAL_NOT_SELECTED";
	public static final String STATUS_PROPOSAL_NON_RESPONSIVE = "PROPOSAL_NON_RESPONSIVE";
	public static final String STATUS_PROPOSAL_PENDING_REASSIGNMENT = "PROPOSAL_PENDING_REASSIGNMENT";
	public static final String STATUS_DOCUMENT_NOT_STARTED = "DOCUMENT_NOT_STARTED";
	public static final String DOCUMENT_RETURNED_KEY = "DOCUMENT_RETURNED";
	public static final String DOCUMENT_VERIFIED_KEY = "DOCUMENT_VERIFIED";
	public static final String STATUS_AWARD_REVIEW_IN_REVIEW = "AWARD_REVIEW_IN_REVIEW";
	public static final String STATUS_AWARD_REVIEW_APPROVED = "AWARD_REVIEW_APPROVED";
	public static final String STATUS_AWARD_REVIEW_RETURNED = "AWARD_REVIEW_RETURNED";
	public static final String STATUS_AWARD_REVIEW_UPDATE_IN_PROGRESS = "AWARD_REVIEW_UPDATE_IN_PROGRESS";
	public static final String STATUS_AWARD_REVIEW_UPDATE_IN_PROGRESS_TEMP = "AWARD_REVIEW_UPDATE_IN_PROGRESS_TEMP";

	public static final String STATUS_PROPOSAL_TASK_IN_REVIEW = "PROPOSAL_TASK_IN_REVIEW";
	public static final String STATUS_PROPOSAL_TASK_ACCEPTED_FOR_EVALUATION = "PROPOSAL_TASK_ACCEPTED_FOR_EVALUATION";
	public static final String STATUS_PROPOSAL_TASK_RETURNED_FOR_REVISION = "PROPOSAL_TASK_RETURNED_FOR_REVISION";
	public static final String STATUS_PROPOSAL_TASK_NON_RESPONSIVE = "PROPOSAL_TASK_NON_RESPONSIVE";
	public static final String STATUS_EVALUATE_PROPOSAL_TASK_IN_REVIEW = "EVALUATE_PROPOSAL_TASK_IN_REVIEW";
	public static final String STATUS_EVALUATE_PROPOSAL_TASK_SCORES_RETURNED = "EVALUATE_PROPOSAL_TASK_SCORES_RETURNED";
	public static final String STATUS_REVIEW_PROPOSAL_TASK_IN_REVIEW = "REVIEW_PROPOSAL_TASK_IN_REVIEW";
	public static final String STATUS_REVIEW_PROPOSAL_TASK_ACCEPTED = "REVIEW_PROPOSAL_TASK_ACCEPTED";
	public static final String STATUS_REVIEW_PROPOSAL_TASK_SCORES_RETURNED = "REVIEW_PROPOSAL_TASK_SCORES_RETURNED";
	public static final String STATUS_APPROVE_AWARD_TASK_IN_REVIEW = "APPROVE_AWARD_TASK_IN_REVIEW";
	public static final String STATUS_APPROVE_AWARD_TASK_APPROVED = "APPROVE_AWARD_TASK_APPROVED";
	public static final String STATUS_APPROVE_AWARD_TASK_RETURNED = "APPROVE_AWARD_TASK_RETURNED";
	public static final String STATUS_APPROVE_AWARD_TASK_OVERRIDE_APPROVED_NO_FINANCIALS = "APPROVE_AWARD_TASK_OVERRIDE_APPROVED_NO_FINANCIALS";
	public static final String STATUS_PCOF_IN_REVIEW = "PCOF_IN_REVIEW";
	public static final String STATUS_PCOF_NOT_SUBMITTED = "PCOF_NOT_SUBMITTED";
	public static final String STATUS_PCOF_APPROVED = "PCOF_APPROVED";
	public static final String STATUS_PCOF_CANCELLED = "PCOF_CANCELLED";
	public static final String STATUS_AWARD_STATUS_PENDING_CONFIGURATION = "AWARD_STATUS_PENDING_CONFIGURATION";
	public static final String STATUS_AWARD_STATUS_PENDING_CERTIFICATION_OF_FUNDS = "AWARD_STATUS_PENDING_CERTIFICATION_OF_FUNDS";
	public static final String STATUS_AWARD_STATUS_CERTIFICATION_OF_FUNDS_APPROVED = "AWARD_STATUS_CERTIFICATION_OF_FUNDS_APPROVED";
	public static final String STATUS_AWARD_STATUS_REGISTERED = "AWARD_STATUS_REGISTERED";
	public static final String STATUS_AWARD_STATUS_CANCELLED = "AWARD_STATUS_CANCELLED";
	public static final String STATUS_CONTRACT_PENDING_CONFIGURATION = "CONTRACT_PENDING_CONFIGURATION";
	public static final String STATUS_CONTRACT_PENDING_COF = "CONTRACT_PENDING_COF";
	public static final String STATUS_CONTRACT_PENDING_SUBMISSION = "CONTRACT_PENDING_SUBMISSION";
	public static final String STATUS_CONTRACT_PENDING_APPROVAL = "CONTRACT_PENDING_APPROVAL";
	public static final String STATUS_CONTRACT_SENT_FOR_REGISTRATION = "CONTRACT_SENT_FOR_REGISTRATION";
	public static final String STATUS_CONTRACT_PENDING_REGISTARTION = "CONTRACT_PENDING_REGISTARTION";
	// Begin R6.3 QC5690
	//public static final String STATUS_CONTRACT_PENDING_NOTIFICATION = "CONTRACT_PENDING_NOTIFICATION"; 
	// End R6.3 QC5690
	public static final String STATUS_CONTRACT_REGISTERED = "CONTRACT_REGISTERED";
	public static final String STATUS_PAYMENT_PENDING_APPROVAL = "PAYMENT_PENDING_APPROVAL";
	public static final String STATUS_PAYMENT_APPROVED = "PAYMENT_APPROVED";
	public static final String STATUS_PAYMENT_DISBURSED = "PAYMENT_DISBURSED";
	public static final String STATUS_PAYMENT_WITHDRAWN = "PAYMENT_WITHDRAWN";
	public static final String STATUS_PAYMENT_SUSPENDED = "PAYMENT_SUSPENDED";
	public static final String STATUS_PAYMENT_CANCELLED = "PAYMENT_CANCELLED";
	public static final String STATUS_PAYMENT_PENDING_FMS_ACTION = "PAYMENT_PENDING_FMS_ACTION";
	public static final String STATUS_CONTRACT_SUSPENDED = "CONTRACT_SUSPENDED";
	public static final String STATUS_CONTRACT_CLOSED = "CONTRACT_CLOSED";
	public static final String STATUS_CONTRACT_CANCELLED = "CONTRACT_CANCELLED";
	public static final String STATUS_INVOICE_PENDING_SUBMISSION = "INVOICE_PENDING_SUBMISSION";
	public static final String STATUS_INVOICE_RETURNED_FOR_REVISION = "INVOICE_RETURNED_FOR_REVISION";
	public static final String STATUS_INVOICE_PENDING_APPROVAL = "INVOICE_PENDING_APPROVAL";
	public static final String STATUS_INVOICE_APPROVED = "INVOICE_APPROVED";
	public static final String STATUS_INVOICE_WITHDRAWN = "INVOICE_WITHDRAWN";
	public static final String STATUS_INVOICE_SUSPENDED = "INVOICE_SUSPENDED";
	public static final String STATUS_INVOICE_CANCELLED = "INVOICE_CANCELLED";
	public static final String STATUS_INVOICE_CLOSED = "INVOICE_CLOSED";
	public static final String STATUS_TASK_IN_REVIEW = "TASK_IN_REVIEW";
	public static final String STATUS_TASK_RETURNED_FOR_REVISION = "TASK_RETURNED_FOR_REVISION";
	public static final String STATUS_TASK_COMPLETE = "TASK_COMPLETE";
	public static final String STATUS_TASK_CANCELLED = "TASK_CANCELLED";
	public static final String STATUS_BUDGET_APPROVED = "BUDGET_APPROVED";
	public static final String BUDGET_RETURNED_FOR_REVISION = "BUDGET_RETURNED_FOR_REVISION";
	public static final String STATUS_BUDGET_PENDING_APPROVAL = "BUDGET_PENDING_APPROVAL";
	public static final String BUDGET_PENDING_SUBMISSION = "BUDGET_PENDING_SUBMISSION";
	public static final String STATUS_BUDGET_ACTIVE = "BUDGET_ACTIVE";
	public static final String STATUS_BUDGET_CANCELLED = "BUDGET_CANCELLED";
	public static final String STATUS_BUDGET_SUSPENDED = "BUDGET_SUSPENDED";
	public static final String STATUS_BUDGET_CLOSED = "BUDGET_CLOSED";

	public static final String STATUS_EVALUATION_GROUP_RELEASED = "EVALUATION_GROUP_RELEASED";
	public static final String STATUS_EVALUATION_GROUP_PROPOSALS_RECEIVED = "EVALUATION_GROUP_PROPOSALS_RECEIVED";
	public static final String STATUS_EVALUATION_GROUP_EVALUATIONS_COMPLETE = "EVALUATION_GROUP_EVALUATION_COMPLETE";
	public static final String STATUS_EVALUATION_GROUP_SELECTIONS_MADE = "EVALUATION_GROUP_SELECTIONS_MADE";
	public static final String STATUS_EVALUATION_GROUP_NO_PROPOSALS = "EVALUATION_GROUP_NO_PROPOSALS";
	public static final String STATUS_EVALUATION_GROUP_NON_RESPONSIVE = "EVALUATION_GROUP_NON_RESPONSIVE";

	public static final String STATUS_COMPETITION_POOL_RELEASED = "COMPETITION_POOL_RELEASED";
	public static final String STATUS_COMPETITION_POOL_PROPOSALS_RECEIVED = "COMPETITION_POOL_PROPOSALS_RECEIVED";
	public static final String STATUS_COMPETITION_POOL_EVALUATIONS_COMPLETE = "COMPETITION_POOL_EVALUATION_COMPLETE";
	public static final String STATUS_COMPETITION_POOL_SELECTIONS_MADE = "COMPETITION_POOL_SELECTIONS_MADE";
	public static final String STATUS_COMPETITION_POOL_NO_PROPOSALS = "COMPETITION_POOL_NO_PROPOSALS";
	public static final String STATUS_COMPETITION_POOL_NON_RESPONSIVE = "COMPETITION_POOL_NON_RESPONSIVE";
	// FinancialsBudgetService class constants
	public static final String FIN_BDG_SR_78 = "78";
	public static final String FIN_BDG_SR_05D = "%05d";
	public static final String FIN_BDG_SR_02D = "%02d";
	public static final String FIN_BDG_SR_INSERT_BUDGET_ADVANCE_DETAIL = "insertBudgetAdvanceDetail";
	public static final String FIN_BDG_SR_FETCH_ADVANCE_COUNT = "fetchAdvanceCount";
	public static final String FIN_BDG_SR_COM_NYC_HHS_MODEL_BUDGET_ADVANCE_BEAN = "com.nyc.hhs.model.BudgetAdvanceBean";
	public static final String FIN_BDG_SR_FETCH_REQUEST_ADVANCE = "fetchRequestAdvance";
	public static final String FIN_BDG_SR_NUMBER_OF_PAYMENTS_IN_PROGRESS = "numberOfPaymentsInProgress";
	public static final String FIN_BDG_SR_NUMBER_OF_PAYMENTS_IN_PROGRESS_WHEN_INVOICE_APPROVED = "numberOfPaymentsInProgressWhenInvoiceApproved";
	public static final String FIN_BDG_SR_NUMBER_OF_INVOICES_IN_PROGRESS = "numberOfInvoicesInProgress";
	public static final String FIN_BDG_SR_NUMBER_OF_BUDGET_AMENDMENTS_OR_MODIFICATIONS_OR_UPDATES_IN_PROGRESS = "numberOfBudgetAmendmentsOrModificationsOrUpdatesInProgress";
	public static final String FIN_BDG_SR_NUMBER_OF_AMENDMENTS_IN_PROGRESS = "numberOfAmendmentsInProgress";
	public static final String FIN_BDG_SR_NUMBER_OF_NEG_AMENDMENTS_IN_PROGRESS = "numberOfNegAmendmentsInProgress";
	public static final String CONTRACT_IDS_AMENDMENTS_IN_PROGRESS = "contractAmendmentsIdsInProgress";
	public static final String CONTRACT_IDS_NEG_AMENDMENTS_IN_PROGRESS = "contractNegAmendmentsIdsInProgress";
	public static final String CONTRACT_IDS_UPDATES_IN_PROGRESS = "contractUpdateIdsInProgress";
	public static final String FIN_BDG_SR_FETCH_BUDGET_LIST_FOR_CITY_COUNT = "fetchBudgetListForCityCount";
	public static final String FIN_BDG_SR_FETCH_BUDGET_LIST_FOR_AGENCY_COUNT = "fetchBudgetListForAgencyCount";
	public static final String FIN_BDG_SR_FETCH_BUDGET_LIST_FOR_PROVIDER_COUNT = "fetchBudgetListForProviderCount";
	public static final String FIN_BDG_SR_FETCH_BUDGET_LIST_FOR_CITY = "fetchBudgetListForCity";
	public static final String FIN_BDG_SR_FETCH_BUDGET_LIST_FOR_AGENCY = "fetchBudgetListForAgency";
	public static final String FIN_BDG_SR_FETCH_BUDGET_LIST_FOR_PROVIDER = "fetchBudgetListForProvider";
	public static final String FIN_BDG_SR_FETCH_BUDGET_LIST_FOR_PROVIDER_F_AND_FP = "fetchBudgetListForProviderFandFP";
	public static final String FIN_BDG_SR_COM_NYC_HHS_MODEL_BUDGET_LIST = "com.nyc.hhs.model.BudgetList";

	// BudgetActionFinancialExtension class constants
	public static final String BDG_AC_FIN_LABEL = "</label>";
	public static final String BDG_AC_FIN_LINK_HREF_PART_TABLE_BUDGET_VALUE = "<label class='tableBudgetValue'>";
	public static final String BDG_AC_FIN_LINK_HREF_PART_BUD_UPDATE = "<label class='BudgetUpdate'>";
	public static final String BDG_AC_FIN_LINK_HREF_PART_CONTRACT_BUD = "<label class='ContractBudget'>";
	public static final String BDG_AC_FIN_LINK_HREF_PART_BUD_MODIFICATION = "<label class='budgetModification'>";
	public static final String BDG_AC_FIN_LINK_HREF_PART_BUD_AMENDMENT = "<label class='BudgetAmendment'>";
	public static final String BDG_AC_FIN_LINK_HREF_PART_6 = "</a></label>";
	public static final String BDG_AC_FIN_LINK_HREF_PART_5 = "');\">";
	public static final String BDG_AC_FIN_LINK_HREF_PART_4 = "','";
	public static final String BDG_AC_FIN_LINK_HREF_PART_4_SP = "', '";
	public static final String BDG_AC_FIN_LINK_HREF_PART_3 = "'onclick=\"javascript: launchBudget('";
	public static final String BDG_AC_FIN_LINK_HREF_2 = "<a href=\"#\" title='";

	// ProposalEvaluationController Starts
	public static final String MAX_SCORE_SUM = "maxScoreSum";
	public static final String EVAL_BEAN_LIST = "evalutionBeanList";
	public static final String HEADER_DETAILS = "headerDetails";
	public static final String PROV_EVAL_SCORES = "providerEvaluationScores";
	public static final String FETCH_PROPOSAL_COMMENTS = "fetchProposalComments";

	// BudgetAgencyActionFinancialExtension class constants
	public static final String BUD_AG_AC_FIN_BRACKET = "'>";
	public static final String BUD_AG_AC_FIN_OPTION = "</option>";
	public static final String BUD_AG_AC_FIN_SELECT = "</select>";
	public static final String BUD_AG_AC_FIN_LS_XPATH_TOP_PART_1_CITY = "//page[(@name=\"budgetCityAction\")]//type[(@name=\"";
	public static final String BUD_AG_AC_FIN_HTML_CODE_OPTION_PART = "<option value='";
	public static final String BUD_AG_AC_FIN_HTML_CODE_PART_11 = "\"><option title=" + "'I need to...'"
			+ " value=I need to... >I need to...</option>";
	
    /** Start QC9149  R 7.7.0 */
    public static final String BUD_AG_AC_FIN_HTML_CODE_PART_14 = "\" negativeAmendCnt=\"";
    /** End QC9149  R 7.7.0 */
    /** Start QC9490  R 8.4.0 */
    public static final String BUD_AG_AC_FIN_HTML_CODE_PART_15 = "\" deleteBudgetUpdateFlag=\"";
    /** End QC9490  R 8.4.0 */
	public static final String BUD_AG_AC_FIN_HTML_CODE_PART_10 = "\" functionName=\"amendContract";
	public static final String BUD_AG_AC_FIN_HTML_CODE_PART_9 = "\" status=\"";
	public static final String BUD_AG_AC_FIN_HTML_CODE_PART_12 = "\" contractId=\"";
	public static final String BUD_AG_AC_FIN_HTML_CODE_PART_13 = "\" budgetType=\"";
	public static final String BUD_AG_AC_FIN_HTML_CODE_PART_8 = "\" budgetId=\"";
	public static final String BUD_AG_AC_FIN_HTML_CODE_PART_7 = "\" dateOfLastUpdate=\"";
	public static final String BUD_AG_AC_FIN_HTML_CODE_PART_6 = "\" budgetValue=\"";
	public static final String BUD_AG_AC_FIN_HTML_CODE_PART_5 = "\" fiscalYear=\"";
	public static final String BUD_AG_AC_FIN_HTML_CODE_PART_4 = "\" agencyName=\"";
	public static final String BUD_AG_AC_FIN_HTML_CODE_PART_3 = "procurementTitle=\"";
	public static final String BUD_AG_AC_FIN_HTML_CODE_PART_2 = " class='contractAmend' id='contractAmend' style='width: 120px' ";
	public static final String BUD_AG_AC_FIN_HTML_CODE_PART_1 = "<select name=action";
	public static final String BUD_AG_AC_FIN_LS_XPATH_TOP_PART_3_AGENCY_CITY = "\")]//action";
	public static final String BUD_AG_AC_FIN_LS_XPATH_TOP_PART_2_AGENCY_CITY = "\")]//status[(@name=\"";
	public static final String BUD_AG_AC_FIN_LS_XPATH_TOP_PART_1_AGENCY = "//page[(@name=\"budgetAgencyAction\")]//type[(@name=\"";

	public static final String BMC_FETCH_CONTRACT_SOURCE = "fetchContractSource";
	public static final String CAFE_CANCEL_CONTRACT = "Cancel Contract";
	public static final String CAFE_CLOSE_CONTRACT = "Close Contract";
	public static final String VIEW_CONTRACT_COF = "View Contract CoF";
	public static final String VIEW_COF = "View CoF";
	public static final String VIEW_AMENDMENTS = "View Amendments";
	public static final String BUDGET_CONTRACT_TITLE = "contractTitle";
	public static final String BASE_BUDGET_CONTRACT_TITLE = "baseContractTitle";
	public static final String FETCH_AMENDMENT_LISTSCREEN_ACCELERATOR = "fetchAmendmentListScreenAccelerator";
	public static final String FETCH_AMENDMENT_LISTSCREEN_AGENCY = "fetchAmendmentListScreenAgency";
	public static final String FETCH_AMENDMENT_LISTSCREEN_PROVIDER = "fetchAmendmentListScreenProvider";
	public static final String FETCH_AMENDMENT_COUNT_ACCELERATOR = "fetchAmendmentCountAccelerator";
	public static final String FETCH_AMENDMENT_COUNT_PROVIDER = "fetchAmendmentCountProvider";
	public static final String FETCH_AMENDMENT_COUNT_AGENCY = "fetchAmendmentCountAgency";
	public static final String SENT_FOR_REG_OR_CAN_CHECK = "sentForRegOrCanCheck";
	public static final String GET_BASE_BUDGET_IDS = "getBaseBudgetIds";
	public static final String GET_BUDGET_IDS_FROM_CONTRACT_ID = "getBudgetIdsFromContractId";
	public static final String IS_STATUS_SENT_FOR_REG = "isStatusSentForReg";
	public static final String IS_AMENDMENT_COF_PDF_GENERATED = "isPDFAmendmentCofGenerated";
	public static final String IS_BUDGET_AMENDMENT_PDF_GENERATED = "isPDFBudgetAmendmentGenerated";
	public static final String DOCS_NOT_GENERATED = "Docs not Generated.Please try again later";
	public static final String DOCS_NOT_GENERATED_ERROR_MSG = "DOCS_NOT_GENERATED_ERROR_MSG";
	public static final String NEGATIVE_AMENDMENT_REG_OR_CAN_CHECK = "negAmendRegOrCanCheck";
	public static final String UPDATE_STATUS_TO_SENT_FOR_REG = "updateStatusToSentForReg";
	public static final String SENT_FOR_REG = "Sent for Registration";
	public static final String PENDING_REG = "Pending Registration";
	//Begin R6.3 QC5690
	//public static final String PENDING_NOTIFICATION = "Pending Notification"; 
	//End R6.3 QC5690
	public static final String ETL_REG = "ETL Registered";

	public static final String BMC_CONTRACT_VALUE = "contractValue";
	public static final String START_FISCAL_YEAR = "startFiscalYear";
	public static final String NUMBER_OF_YEARS = "numberOfYears";
	public static final String FY_YEAR = "fyYear";
	public static final String TOTAL_FY_ARRAY = "totalFyArray";
	public static final String CURRENT_CONTRACT_VALUE = "currentContractValue";

	public static final String FILS_SELECT_WITHDRAW_INVOICE_WORK_FLOW_DETAILS = "selectWithdrawInvoiceWorkFlowDetails";
	public static final String FILS_COM_NYC_HHS_MODEL_INVOICE_LIST_FILTER = "com.nyc.hhs.model.InvoiceListFilter";
	public static final String DELETE_INVOICE = "deleteInvoice";
	public static final String DELETE_INVOICE_DETAILS = "deleteInvoiceDetails";
	public static final String DELETE_INVOICE_DOCUMENT = "deleteInvoiceDocument";
	public static final String FETCH_INVOICE_DOCUMENT_IDS = "fetchInvoiceDocumentIds";
	public static final String FILS_UPDATE_INVOICE_WITHDRAWN = "updateInvoiceWithdrawn";
	public static final String FILS_FETCH_INVOICE_COUNT_ACCELERATOR = "fetchInvoiceCountAccelerator";
	public static final String FILS_FETCH_INVOICE_COUNT_AGENCY = "fetchInvoiceCountAgency";
	public static final String FILS_FETCH_INVOICE_COUNT_PROVIDER = "fetchInvoiceCountProvider";
	public static final String FILS_FETCH_INVOICE_LIST_ACCELERATOR = "fetchInvoiceListAccelerator";
	public static final String FILS_FETCH_INVOICE_LIST_AGENCY = "fetchInvoiceListAgency";
	public static final String FILS_COM_NYC_HHS_MODEL_INVOICE_LIST = "com.nyc.hhs.model.InvoiceList";
	public static final String FILS_FETCH_INVOICE_LIST_PROVIDER = "fetchInvoiceListProvider";

	// Invoice list controller Constants
	public static final String INVOICE_LIST = "invoiceList";
	public static final String MMDDYYFORMAT = "MM/dd/yyyy";
	public static final String INVOICE_FILTER_BEAN = "invoiceFilterBean";
	public static final String GET_FINANCIALS_INVOICE_LIST = "getFinancialsInvoiceList";
	public static final String AO_CB_INVOICE_LIST_BEAN = "aoCBInvoiceListBean";
	public static final String INVOICE_COUNT = "invoiceCount";
	public static final String FISCAL_INFORMATION = "loFiscalInformation";
	public static final String DATE_ORIGIN = "01/01/1800";

	public static final String DELETE_INVOICE_ID = "deleteInvoiceId";
	public static final String DELETE_INVOICE_CONFIRMATION = "deleteInvoiceConfirmation";
	public static final String INVOICE_LIST_ACTION = "invoiceListAction";
	public static final String INVOICE_LB_STATUS = "lbStatus";
	public static final String WITHDRAW_INVOICE_ID = "withdrawInvoiceId";
	public static final String AS_INVOICE_NUMBER = "asInvoiceNumber";
	public static final String INVOICE_AO_AUTH_BEAN1 = "aoAuthBean1";
	public static final String INVOICE_WITHDRAW = "invoiceWithdraw";
	public static final String OVER_LAY_INVOICE = "OverLayInvoice";
	public static final String OVERLAY_PAGE_PARAM_INVOICE = "OverlayPageParamInvoice";
	public static final String INVOICE_AO_INVOICE_COUNT = "aoInvoiceCount";
	public static final String INVOICE_AO_INVOICE_STATUS = "aoInvoiceStatus";
	public static final String PY_AO_PROGRAM_NAME_LIST = "aoProgramNameList";
	public static final String INVOICE_LB_FIRST_LOAD = "lbFirstLoad";
	public static final String INVOICE_LO_AGENCY_LIST = "loAgencyList";
	public static final String INVOICE_LO_STATUS_LIST = "loStatusList";

	public static final String NFCTH_DATE_PARSE_ERROR_MSG = "Error in parsing date to the given format";
	public static final String NFCTH_LB_AUDIT_TRUE = "lbAuditTrue";
	public static final String NFCTH_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final String PCFTH_STATUS_CHANGED_TO_APPROVED = "Status Changed to Approved";
	public static final String PCFTH_COMMENTS_EMPTY_FOR_LEVEL2_OR_HIGHER_FAILURE = "CommentsEmptyForLevel2OrHigherFailure";
	public static final String PCFTH_TOTAL_AMOUNT_MATCH_FAILURE_FOR_LEVEL2_OR_HIGHER = "totalAmountMatchFailureForLevel2OrHigher";
	public static final String PCFTH_TOTAL_AMOUNT_MATCH_FAILURE = "totalAmountMatchFailure";
	public static final String PCFTH_LEVEL_1 = "level 1";
	public static final String PCFTH_LEVEL_2 = "level 2";
	public static final String NEW_FY_CONFIGURABLE_YEAR_AMOUNT = "lsConfigurableFiscalYearAmount";
	public static final String TRN_CHK_IF_BUDGET_EXISTS = "checkIfBudgetExists";
	public static final String TRN_CONFIG_SUB_BUDGET_AMOUNT = "fetchConfigurableYearBudgetAmount";
	public static final String RSLT_BUDGET_ID = "lsBudgetId";
	public static final String RSLT_FISCAL_YEAR_AMOUNT = "lsFiscalYearAmount";

	public static final String CBRT_FINISH_CONTRACT_BUDGET_CONFIG_TASK = "finishContractBudgetConfigTask";
	public static final String AO_BUDGET_STATUS = "aoBudgetStatus";
	public static final String FINISH_CONTRACT_BUDGET_REVIEW_TASK = "finishContractBudgetReviewTask";
	public static final String CREATE_REPLICA_BUDGET_REVIEW_TASK = "createReplicaForBudgetReviewTask";
	public static final String FINISH_BUDGET_RETURN_REVIEW_TASK = "finishBudgetReturnReviewTask";
	public static final String FINISH_BUDGET_MODIFICATION_REVIEW_TASK = "finishBudgetModificationReviewTask";
	public static final String MERGE_BUDGET_MODIFICATION_REVIEW_TASK = "mergeBudgetForModificationReviewTask";
	public static final String FINISH_BUDGET_MOD_RETURNED_REVIEW_TASK = "finishBudgetModificationReturnedReviewTask";
	public static final String FINISH_BUDGET_UPDATE_REVIEW_TASK = "finishBudgetUpdateReviewTask";
	public static final String FINISH_BUDGET_UPDATE_RETURNED_TASK = "finishBudgetUpdateReturnedReviewTask";
	public static final String MERGE_BUDGET_UPDATE_REVIEW_TASK = "mergeBudgetForUpdateReviewTask";
	public static final String FINISH_BUDGET_AMENDMENT_REVIEW_TASK = "finishBudgetAmendmentReviewTask";
	public static final String FINISH_BUDGET_AMENDMENT_RETURNED_TASK = "finishBudgetAmendmentReturnedReviewTask";
	public static final String FINISH_BUDGET_AMENDMENT_NEG_REVIEW_TASK = "finishBudgetAmendmentNegReviewTask";
	public static final String MERGE_BUDGET_AMENDMENT_REVIEW_TASK = "mergeBudgetForAmendmentReviewTask";
	public static final String FETCH_COUNT_BUDGET_AMENDMENT_APPROVED = "fetchApprovedBudgetCountForAmendment";
	public static final String LB_FINAL_FINISH = "lbFinalFinish";
	public static final String LO_HM_WF_REQ_PROPS = "loHmWFReqProps";
	public static final String PAGE_ERROR = "pageError";
	public static final String TASK_ERROR = "taskError";
	public static final String PROCUREMENT_TASK_OF_FUNDS = "procurementTaskOfFundsFinishTask";

	public static final String CB_VALIDATE_ERROR = "CB_VALIDATE_ERROR";
	public static final String PASSWORD = "password";
	public static final String JSP_CONTRACTBUDGET = "jsp/contractbudget/";
	public static final String CONTRACT_ID = "contractID";
	public static final String EXT_CT_NUMBER = "extCtNumber";
	public static final String LS_SUB_BUDGET_ID = "lsSubBudgetId";
	public static final String AGENCY_ID1 = "agencyID";
	public static final String BUDGET_ID = "budgetID";
	public static final String JSP_NAME = "jspName";
	public static final String CONTRACT_BUDGET_HANDLER = "contractBudgetHandler";
	public static final String WORKFLOW_NAME = "workflowName";
	public static final String SUBMITTED_BY = "submittedBy";
	public static final String LAUNCH_WF_CONTRACT_BUDGET = "launchWFContractBudget";
	public static final String AO_HMWF_REQUIRED_PROPS = "aoHMWFRequiredProps";
	public static final String MAIL_REPORT_FILE_PATH = "lsMailReportFilePath";
	public static final String AO_CONTRACT_MERGE_HASHMAP = "aoContractMergeHashMap";
	public static final String LB_AUTH_STATUS_FLAG = "lbAuthStatusFlag";
	public static final String CB_REVIEW_LEVEL_ERROR = "CB_REVIEW_LEVEL_ERROR";
	public static final String REVIEW_LEVEL = "reviewLevel";
	public static final String FETCH_REVIEW_LEVEL_CB = "fetchReviewLevelCB";
	public static final String FETCH_REVIEW_LEVEL_INVOICE = "fetchReviewLevelInvoice";
	public static final String ACS = "ACS";
	public static final String LB_SAVE_STATUS = "lbSaveStatus";
	public static final String LB_AUTHENTICATE_USER = "lbAuthenticateUser";
	public static final String FETCH_UPDATE_BUDGET_SUMMARY = "fetchUpdateBudgetSummary";
	public static final String UPDATE_BUDGET_SUMMARY = "updateBudgetSummary";
	public static final String DISPLAY_UPDATE_BUDGET_SUMMARY = "displayUpdateBudgetSummary";
	public static final String MODIFICATION_BUDGET_SUMMARY = "modificationBudgetSummary";
	public static final String DISPLAY_MODIFICATION_BUDGET_SUMMARY = "displayModificationBudgetSummary";
	public static final String DISPLAY_BUDGET_SUMMARY = "displayBudgetSummary";
	public static final String RENDERMAP = "rendermap";
	public static final String JSP_CONTRACTBUDGET_CONTRACT_BUDGET_MODIFICATION_LANDING = "jsp/contractbudget/contractBudgetModificationLanding";
	public static final String JSP_CONTRACTBUDGET_CONTRACT_BUDGET_LANDING_PRINT = "jsp/contractbudget/contractBudgetLandingPrint";
	public static final String JSP_CONTRACT_BUDGET_AMENDMENT_LANDING_PRINT = "jsp/contractbudget/contractBudgetAmendmentLandingPrint";
	public static final String JSP_AMENDMENT_BUDGET_REVIEW_TASK = "jsp/contractbudget/budgetAmendmentReviewTask";
	public static final String LO_CB_GRID_BEAN = "loCBGridBean";
	public static final String GET_CB_GRID_DATA_FOR_SESSION = "getCbGridDataForSession";
	public static final String GET_CB_GRID_DATA_FOR_SESSION_FOR_AMEND_PDF = "getCbGridDataForSessionForAmendPdf";
	public static final String BUDGET_ACCORDIAN_DATA = "BudgetAccordianData";
	public static final String SUB_BUDGET_LIST = "subBudgetList";
	//Added in R6: return payment review task
	public static final String RETURN_PAYMENT_DETAILS = "returnPaymentDetails";
	//Added in R6: return payment review task end
	public static final String FETCH_SUB_BUDGET_SUMMARY = "fetchSubBudgetSummary";
	public static final String FISCAL_BUDGET_INFO = "fiscalBudgetInfo";
	public static final String LO_BUDGET_DETAILS = "loBudgetDetails";
	public static final String SUB_BUDGET_SITE_COUNT = "subBudgetSiteCount";
	public static final String FETCH_FY_BUDGET_SUMMARY = "fetchFyBudgetSummary";
	public static final String CONTRACT_INFO = "contractInfo";
	public static final String LO_CONTRACT_LIST = "loContractList";
	public static final String AO_HASH_MAP = "aoHashMap";
	public static final String PRINTER_VIEW = "printerView";
	public static final String JSP_CONTRACTBUDGET_CONTRACT_BUDGET_REVIEW_TASK = "jsp/contractbudget/contractBudgetReviewTask";
	//Added in R6: return payment review task
	public static final String JSP_RETURN_PAYMENT_REVIEW_TASK = "jsp/contractbudget/returnPaymentReviewTask";
	//Added in R6: return payment review task end
	public static final String CONTRACT_BUDGET_REVIEW_TASK = "contractBudgetReviewTask";
	public static final String INVOICE_REVIEW_TASK = "invoiceReviewTask";
	public static final String MODIFICATION_REVIEW_TASK = "modificationReviewTask";
	public static final String UPDATE_REVIEW_TASK = "updateReviewTask";
	public static final String JSP_CONTRACTBUDGET_CONTRACT_BUDGET_LANDING = "jsp/contractbudget/contractBudgetLanding";
	public static final String CLOSING_BRACE_1 = "\"}";
	public static final String MESSAGE_1 = "\", \"message\": \"";
	public static final String ERROR_1 = "{\"error\": \"";
	public static final String USER_ID = "userId";
	// made changes for release 3.10.0 enhancement 5686
	public static final String REUSE_EPIN = "reuseEpin";
	public static final String NOT_REUSE = "N";
	// Payment
	public static final String JSP_PAYMENT_DETAILS = "jsp/payment/paymentDetails";
	public static final String JSP_PAYMENT_REVIEW_TASK_DETAILS = "jsp/payment/paymentReviewTaskDetails";
	public static final String JSP_PAYMENT_VOUCHER_LINE_DETAILS = "jsp/payment/paymentVoucherLineDetails";

	// Advance Payment
	public static final String JSP_ADVANCE_PAYMENT_DETAILS = "jsp/payment/advancePaymentDetails";
	public static final String JSP_ADVANCE_PAYMENT_REVIEW_TASK = "jsp/payment/advancePaymentReviewTaskDetails";
	public static final String JSP_ADVANCE_PAYMENT_LINE_DETAILS = "jsp/payment/advancePaymentLineAndChartDetails";

	// Constants for BMC Controller Starts
	public static final String BMC_PROCUREMENT_FUNCD_TASK = "procurementFundTask";
	public static final String BMC_PROC_CERT_FUND_TASK_JSP_PATH = "jsp/bmc/procurementCertFundsTask";
	public static final String BMC_CONTRACT_CONFIG_TASK = "contractConfigurationTask";
	public static final String BMC_CONTRACT_FINANCIALS_JSP_PATH = "jsp/bmc/contractFinancials";
	public static final String BMC_AMENDMENT_CONFIGURATION_JSP_PATH = "jsp/bmc/amendmentConfiguration";
	public static final String BMC_AMENDMENT_CERT_FUNDS_JSP_PATH = "jsp/bmc/amendmentCertificationOfFunds";
	public static final String BMC_CONTRACT_CONFIG_UPDATE = "contractConfigurationUpdate";
	public static final String BMC_CONTRACT_CONFIG_UPDATE_JSP_PATH = "jsp/bmc/contractConfigurationUpdate";
	public static final String BMC_CONTRACTCOF_TASK = "contractCOFTask";
	public static final String BMC_CONTRACT_CERT_FUND_TASK_JSP_PATH = "jsp/bmc/contractCertificationFundsTask";
	public static final String BMC_NEWFY_CONFIG_TASK_PATH = "jsp/bmc/newFYConfigurationTask";
	public static final String BMC_NEWFY_CONFIG_TASK = "newFYConfigurationTask";
	public static final String BMC_ACTION_REQ_PARAM = "lsActionReqParam";
	public static final String BMC_RENDER_ATTR = "renderAttribute";
	public static final String BMC_PROGRAM_LIST = "programList";
	public static final String BMC_FETCH_BASE_CONTRACT_ID = "fetchBaseContractId";
	public static final String BMC_FETCH_UPDATE_CONTRACT_ID = "fetchUpdateContractId";
	public static final String BMC_FETCH_CONTRACT_CONFIGURATION_DETAILS = "fetchContractConfigurationDetails";
	public static final String BMC_GET_BUDGET_FISCAL_YEAR_ID = "getBudgetFiscalYearId";
	public static final String BMC_FETCH_CONTRACT_AMENDMENT_CONFIGURATION_DETAILS = "fetchContractAmendmentConfigurationDetails";
	public static final String IS_OPEN_ENDED_RFP_START_END_DATE_NOT_SET = "isOpenEndedRfpStartEndDateNotSet";
	public static final String UPDATE_CONTRACT_START_END_DATE_FOR_OPEN_ENDED_RFP = "updateContractStartEndDateForOpenEndedRfp";
	public static final String BMC_PROCUREMENT_CON_DETAILS = "procurementCoNDetails";
	public static final String BMC_CONTRACT_DATA_PARAM = "aoContractData";
	public static final String BMC_CURRENT_FISCAL_YEAR = "currentFiscalYear";
	public static final String BMC_CONTRACT_FIRST_YEAR = "contractFirstYear";
	public static final String BMC_FETCH_CONTRACT_COF_TASK_DETAILS = "fetchContractCofTaskDetails";
	public static final String BMC_CONTRACT_COF_TASK_DETAILS = "contractCofTaskDetails";
	public static final String AO_PROCUREMENTCOFBEAN = "aoProcurementCOFBean";
	public static final String BMC_CONTRACT_BUDGET_BEAN = "ContractBudgetBean";
	public static final String BMC_SUBBUDGETS_SUMTOTAL_PARAM = "aoSubBudgetsSumTotal";
	public static final String CS_FETCH_BUDGET_DETAILS = "fetchBudgetDetailsByFYAndContractId";
	public static final String CS_FETCH_ACTIVE_APPROVED_BUDGET_DETAILS = "fetchBudgetDetailsActiveOrApproved";
	public static final String CS_FETCH_AMENDMENT_BUDGET_DETAILS = "fetchAmendmentBudgetDetails";
	public static final String BMC_TOTAL_BUDGET_AMOUNT = "totalbudgetAmount";
	public static final String BMC_FY_PLANNED_AMOUNT = "fyPlannedAmount";
	public static final String BMC_ADD_ENABLED = "addEnabled";
	public static final String BMC_FY_BUDGET_PLANNED_AMOUNT = "fYBudgetPlannedAmount";
	public static final String BMC_CONTRACT_ID = "contractid";
	public static final String BMC_BUDGET_FISCAL_YEAR = "budgetfiscalYear";
	public static final String BMC_UPDATE_BUDGET_FY_TOTAL_BUDGET_AMOUNT = "updateBudgetFYTotalBudgetAmount";
	public static final String BMC_VIEW_TAB = "viewTab";
	public static final String NEW_FY_CONFIGURED_YEAR_TOTAL_AMOUNT = "configuredYearTotalAmount";
	public static final String BMC_NEW_FY_CONFIGURATION_BUDGET_TAB = "NewFYConfigurationBudgetTab";
	public static final String BMC_COPY_PREVIOUS_FY_SUB_BUDGET_TO_CURRENT_FY = "copyPreviousFYSubBudgetToCurrentFY";
	public static final String BMC_INSERT_UPDATED_SUB_BUDGET_DETAILS = "insertUpdatedSubBudgetDetails";
	public static final String BMC_INSERT_AMENDMENT_SUB_BUDGET_DETAILS = "insertAmendmentSubBudgetDetails";
	public static final String CS_FETCH_FY_AND_CONTRACT_ID = "fetchFYAndContractId";
	public static final String BMC_RETURNED_FYI_LIST_PARAM = "aoReturnedFYIList";
	public static final String BMC_CONTRACT_CONFIG_UPDATE_BUDGETS_JSP_PATH = "jsp/bmc/contractConfigUpdateBudgets";
	public static final String BMC_INSERT_NEW_BUDGET_DETAILS = "insertNewBudgetDetails";
	public static final String BMC_INSERT_NEW_AMENDMENT_BUDGET_DETAILS = "insertNewAmendmentBudgetDetails";
	public static final String AI_CURRENT_SEQ = "aiCurrentSeq";
	public static final String TRAILING_ZEROS = "0";
	public static final String IS_NUMBER = "lsNumber";
	public static final String BMC_FETCH_UPDATED_CONFIGURATION_DETAILS = "fetchUpdatedConfigurationDetails";
	public static final String BMC_PROP_CONTRACT_BUGET_OVERCONFIG_ERROR = "CONTRACT_BUGET_OVERCONFIG_ERROR";
	public static final String BMC_PROP_NEW_FY_OVERCONFIG_ERROR = "NEW_FY_OVERCONFIG_ERROR";
	public static final String BMC_ERROR_WHILE_LAUNCHING_TASK = "Error occured while launching task";
	public static final String BMC_CONTRACTBUDGETS_JSP_PATH = "jsp/bmc/contractBudgets";
	public static final String BMC_CONTRACTBUDGETS_AMENDMENT_JSP_PATH = "jsp/bmc/amendmentContractBudgets";
	public static final String BMC_NEW_FY_CONFIGURATION_BUDGET_JSP_PATH = "jsp/bmc/newFYConfigurationBudget";
	// Constants for BMC Controller Ends

	public static final String NEW_FY_GRID_FETCH = "NewFYGridFetch";
	public static final String NEW_FY_GRID_ADD = "NewFYGridAdd";

	public static final String NOT_SUBMITTED = "Not Submitted";
	public static final String VIEW = "View";
	public static final String RESUME = "RESUME";

	// HHS UTIL COnstants
	public static final String HHSUTIL_ROLE_MAPPING_MAP = "roleMappingMap";
	public static final String HHSUTIL_AGENCY = "agency";
	public static final String HHSUTIL_AGENCY_MASTER = "//agency_master";
	public static final String HHSUTIL_APPENDED_TEMPLATE = ",template:";
	public static final String HHSUTIL_TEMPLATE = "template";
	public static final String HHSUTIL_RECORDS = "\",\"records\":\"";
	public static final String HHSUTIL_PAGE = "\",\"page\":\"";
	public static final String HHSUTIL_TOTAL = "{\"total\":\"";
	public static final String HHSUTIL_TOTAL_PAGE_1_RECORDS_ROWS = "{\"total\":\"\",\"page\":\"0\",\"records\":\"\",\"rows\":\"\"}";
	public static final String HHSUTIL_DATA = "\"data\":";
	public static final String HHSUTIL_SUGGESTIONS = "\"suggestions\":";
	public static final String HHSUTIL_APPEND_QUERY = "\"query\":'";
	public static final String HHSUTIL_ACTIVEFLAG = "activeflag";
	public static final String HHSUTIL_E_MMM_DD_HH_MM_SS_Z_YYYY = "E MMM dd HH:mm:ss Z yyyy";
	public static final String HHSUTIL_ELEMENT = "element";
	public static final String SERVICE_STRING = "//element[((@name=\"Service Area\" or @name=\"Function\")"
			+ "and lower-case(@parentid)=\"root\")]";
	public static final String HHSUTIL_EDITRULES = "editrules";
	public static final String HHSUTIL_EDITABLE = "editable";
	public static final String HHSUTIL_DELIM_PIPE = "|";
	public static final String HHSUTIL_DELIM_ESCLAMATION = "!";
	public static final String GET_MASTER_STATUS_DB = "getMasterStatusDB";
	public static final String DOUBLE_HHSUTIL_DELIM_PIPE = "\\|";
	public static final String SELECTION_FLAG = "selectionflag";
	public static final String CLOSING_BRACKET = ")";

	// Constants for RFP Controller

	public static final String TAXONOMY_TAGGING_ADD_NEW_TAG = "taxonomytagging/addnewtag";
	public static final String DELETE_TAXONOMY_TAGGING_DETAILS = "deleteTaxonomyTaggingDetails";
	public static final String DELETE_TAXONOMY_TAGGING_DETAILS_IN_BULK = "deleteTaxonomyTaggingDetailsInBulk";
	public static final String REMOVE_ALL_TAXONOMY_TAGGING_DETAILS_IN_BULK = "removeAllTaxonomyTaggingDetailsInBulk";

	// Constants for ContractcertfundsTaskHandler

	public static final String CTH_RETURN_CONTRACT_COF_TASK_WITH_LEVEL_THREE = "returnContractCOFTaskWithLevelThree";
	public static final String LB_FLAG = "lbFlag";
	public static final String CTH_LO_WOB_NUM = "loWobNum";
	public static final String CTH_FINISH_CONTRACT_COF_TASK = "finishContractCOFTask";

	// Constants for Invoice - S332:Invoice Operation Support
	public static final String MODEL_CB_OPERATION_SUPPORT_BEAN = "com.nyc.hhs.model.CBOperationSupportBean";
	public static final String MODEL_CB_EQUIPMENT_BEAN = "com.nyc.hhs.model.CBEquipmentBean";

	public static final String MSG_KEY_INVOICE_AMOUNT_MORE_THAN_REMAINING = "invoiceAmountMoreThanRemaining";
	public static final String MSG_KEY_INVOICE_AMOUNT_MORE_THAN_REMAINING_AMOUNT = "invoiceAmountMoreThanRemainingAmount";
	public static final String MSG_KEY_INVOICE_ASSIGNMENT_DELETE_CHECK = "INVOICE_ASSIGNMENT_DELETE_CHECK";
	public static final String FETCH_INVOICE_OPERATION_SUPPORT = "fetchInvoiceOperationSupport";
	public static final String FETCH_INVOICE_OPERATION_SUPPORT_DETAILS = "fetchInvoiceOperationSupportDetails";
	public static final String FETCH_INVOICED_AMOUNT_FOR_OPERATION_SUPPORT = "fetchInvoicedAmountForOperationSupport";

	public static final String FETCH_BUDGET_ALLOCATED_FOR_AN_OPSUPPORT = "fetchBudgetAllocatedForAnOpSupport";
	public static final String FETCH_INV_AMOUNT_FOR_AN_OPSUPPORT_LINEITEM = "fetchInvAmountForAnOpSupportLineItem";
	public static final String EDIT_INVOICE_OPERATION_SUPPORT_DETAILS = "editInvoiceOperationSupportDetails";
	public static final String INSERT_INVOICE_OPERATION_SUPPORT_DETAILS = "insertInvoiceOperationSupportDetails";
	public static final String GET_SEQ_FOR_INVOICE_DETAIL = "getSeqForInvoiceDetail";
	public static final String GET_SEQ_FOR_INVOICE_ADVANCE_NUMBER = "getSeqForInvoiceAdvanceNumber";

	public static final String FETCH_EQUIPMENT_INVOICE_DETAILS = "fetchEquipmentInvoiceDetails";
	public static final String FETCH_INVOICED_TOTAL_FOR_EQUIPMENTS = "fetchInvoicedTotalForEquipments";
	public static final String FETCH_BUDGET_ALLOCATED_FOR_AN_EQUIPMENT = "fetchBudgetAllocatedForAnEquipment";
	public static final String FETCH_INV_AMOUNT_FOR_AN_EQUIPMENT = "fetchInvAmountForAnEquipment";
	public static final String INSERT_EQUIPMENT_DETAILS = "insertEquipmentDetails";

	// update remaining amount and ytd amount for line item
	public static final List<String> UPDATE_LINE_ITEMS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE = new ArrayList<String>();
	static
	{
		UPDATE_LINE_ITEMS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE
				.add("updatePersonalServiceYtdAndRemainingAmount");
		UPDATE_LINE_ITEMS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE
				.add("updateContractedServiceYtdAndRemainingAmount");
		UPDATE_LINE_ITEMS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("updateEquipmentYtdAndRemainingAmount");
		UPDATE_LINE_ITEMS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("updateFringesYtdAndRemainingAmount");
		UPDATE_LINE_ITEMS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("updateIndirectRateYtdAndRemainingAmount");
		UPDATE_LINE_ITEMS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("updateMilestoneYtdAndRemainingAmount");
		UPDATE_LINE_ITEMS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE
				.add("updateOperationAndSupportYtdAndRemainingAmount");
		UPDATE_LINE_ITEMS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE
				.add("updateProfessionalServicesYtdAndRemainingAmount");
		UPDATE_LINE_ITEMS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("updateProgramIncomeYtdAndRemainingAmount");
		UPDATE_LINE_ITEMS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("updateRateYtdAndRemainingAmount");
		UPDATE_LINE_ITEMS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("updateRentYtdAndRemainingAmount");
		UPDATE_LINE_ITEMS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("updateUtilitiesYtdAndRemainingAmount");
	}
	public static final List<String> UPDATE_LINE_ITEMS_YTD_INVOICE_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE = new ArrayList<String>();
	static
	{
		UPDATE_LINE_ITEMS_YTD_INVOICE_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("PERSONNEL_SERVICE");
		UPDATE_LINE_ITEMS_YTD_INVOICE_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("CONTRACTED_SERVICE");
		UPDATE_LINE_ITEMS_YTD_INVOICE_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("EQUIPMENT");
		UPDATE_LINE_ITEMS_YTD_INVOICE_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("FRINGE_BENEFIT");
		UPDATE_LINE_ITEMS_YTD_INVOICE_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("INDIRECT_RATE");
		UPDATE_LINE_ITEMS_YTD_INVOICE_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("MILESTONE");
		UPDATE_LINE_ITEMS_YTD_INVOICE_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("OPERATIONS_AND_SUPPORT");
		UPDATE_LINE_ITEMS_YTD_INVOICE_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("PROFESSIONAL_SERVICE");
		UPDATE_LINE_ITEMS_YTD_INVOICE_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("PROGRAM_INCOME");
		UPDATE_LINE_ITEMS_YTD_INVOICE_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("RATE");
		UPDATE_LINE_ITEMS_YTD_INVOICE_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("RENT");
		UPDATE_LINE_ITEMS_YTD_INVOICE_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE.add("UTILITIES");
	}

	public static final Map<String, String> GET_AMOUNT_DETAILS = new HashMap<String, String>();
	static
	{
		GET_AMOUNT_DETAILS.put("PERSONNEL_SERVICE", "TOTAL_SALARY");
		GET_AMOUNT_DETAILS.put("CONTRACTED_SERVICE", "AMOUNT");
		GET_AMOUNT_DETAILS.put("EQUIPMENT", "AMOUNT");
		GET_AMOUNT_DETAILS.put("FRINGE_BENEFIT", "AMOUNT");
		GET_AMOUNT_DETAILS.put("INDIRECT_RATE", "INDIRECT_AMOUNT");
		GET_AMOUNT_DETAILS.put("MILESTONE", "AMOUNT");
		GET_AMOUNT_DETAILS.put("OPERATIONS_AND_SUPPORT", "AMOUNT");
		GET_AMOUNT_DETAILS.put("PROFESSIONAL_SERVICE", "AMOUNT");
		GET_AMOUNT_DETAILS.put("PROGRAM_INCOME", "AMOUNT");
		GET_AMOUNT_DETAILS.put("RATE", "AMOUNT");
		GET_AMOUNT_DETAILS.put("RENT", "AMOUNT");
		GET_AMOUNT_DETAILS.put("UTILITIES", "AMOUNT");
		GET_AMOUNT_DETAILS.put("BC_SERVICES_DETAILS", "FY_BUDGET_INCOME"); // added in R7
		GET_AMOUNT_DETAILS.put("SERVICES_DETAIL_UNITS", "FY_BUDGET_UNITS"); // added in R7
		GET_AMOUNT_DETAILS.put("COST_CENTER_DETAILS", "FY_BUDGET_AMOUNT"); // added in R7
	}

	public static final Map<String, String> TYPE_DETAILS = new HashMap<String, String>();
	static
	{
		TYPE_DETAILS.put("PERSONNEL_SERVICE", "TOTAL_SALARY");
		TYPE_DETAILS.put("CONTRACTED_SERVICE", "SERVICE_DESCRIPTION");
		TYPE_DETAILS.put("EQUIPMENT", "");
		TYPE_DETAILS.put("FRINGE_BENEFIT", "");
		TYPE_DETAILS.put("INDIRECT_RATE", "");
		TYPE_DETAILS.put("MILESTONE", "MILESTONE");
		TYPE_DETAILS.put("OPERATIONS_AND_SUPPORT", "OPERATIONS_SUPPORT_TYPE_ID");
		TYPE_DETAILS.put("PROFESSIONAL_SERVICE", "PROFESSIONAL_SERVICE_TYPE_ID");
		TYPE_DETAILS.put("PROGRAM_INCOME", "program_income_type_id");
		TYPE_DETAILS.put("RATE", "");
		TYPE_DETAILS.put("RENT", "LOCATION");
		TYPE_DETAILS.put("UTILITIES", "UTILITIES_TYPE_ID");
	}

	public static final String AMOUNT = "AMOUNT";
	public static final String UPDATE_LINE_ITEMS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE_QUERY = "updateLineItemsYTDAndRemainingForAgencyInterface";
	public static final String UPDATE_LINE_ITEMS_YTD_AND_REMAINING_UNITS_FOR_RATE = "updateLineItemsYTDAndRemainingUnitsForRate";
	public static final String GET_BASE_BUDGET_ID_FROM_INVOICE_ID = "getBaseBudgetIdFromInvoiceId";
	public static final String UPDATE_SUB_BUDGETS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE_QUERY = "updateSubBudgetsYTDAndRemainingForAgencyInterface";
	public static final String UPDATE_BUDGETS_YTD_AND_REMAINING_AMOUNT_FOR_AGENCY_INTERFACE_QUERY = "updateBudgetsYTDAndRemainingForAgencyInterface";

	public static final Map<String, String> GET_ID_DETAILS = new HashMap<String, String>();
	static
	{
		GET_ID_DETAILS.put("PERSONNEL_SERVICE", "PERSONNEL_SERVICE_ID");
		GET_ID_DETAILS.put("CONTRACTED_SERVICE", "CONTRACTED_SERVICE_ID");
		GET_ID_DETAILS.put("EQUIPMENT", "EQUIPMENT_ID");
		GET_ID_DETAILS.put("FRINGE_BENEFIT", "FRINGE_BENEFIT_ID");
		GET_ID_DETAILS.put("INDIRECT_RATE", "INDIRECT_RATE_ID");
		GET_ID_DETAILS.put("MILESTONE", "MILESTONE_ID");
		GET_ID_DETAILS.put("OPERATIONS_AND_SUPPORT", "OPERATIONS_SUPPORT_ID");
		GET_ID_DETAILS.put("PROFESSIONAL_SERVICE", "PROFESSIONAL_SERVICE_ID");
		GET_ID_DETAILS.put("PROGRAM_INCOME", "PROGRAM_INCOME_ID");
		GET_ID_DETAILS.put("RATE", "RATE_ID");
		GET_ID_DETAILS.put("RENT", "RENT_ID");
		GET_ID_DETAILS.put("UTILITIES", "UTILITIES_ID");
		GET_ID_DETAILS.put("BC_SERVICES_DETAILS", "BC_SERVICES_DETAIL_ID");// Added in R7 for Cost Center
		GET_ID_DETAILS.put("COST_CENTER_DETAILS", "COST_CENTER_DETAILS_ID");// Added in R7 for Cost Center
	}

	public static final Map<String, String> GET_ENTRY_TYPE_ID_DETAILS = new HashMap<String, String>();
	static
	{
		GET_ENTRY_TYPE_ID_DETAILS.put("PERSONNEL_SERVICE", "1");
		GET_ENTRY_TYPE_ID_DETAILS.put("CONTRACTED_SERVICE", "6");
		GET_ENTRY_TYPE_ID_DETAILS.put("EQUIPMENT", "12");
		GET_ENTRY_TYPE_ID_DETAILS.put("FRINGE_BENEFIT", "13");
		GET_ENTRY_TYPE_ID_DETAILS.put("INDIRECT_RATE", "10");
		GET_ENTRY_TYPE_ID_DETAILS.put("MILESTONE", "8");
		GET_ENTRY_TYPE_ID_DETAILS.put("OPERATIONS_AND_SUPPORT", "2");
		GET_ENTRY_TYPE_ID_DETAILS.put("PROFESSIONAL_SERVICE", "4");
		GET_ENTRY_TYPE_ID_DETAILS.put("PROGRAM_INCOME", "11");
		GET_ENTRY_TYPE_ID_DETAILS.put("RATE", "7");
		GET_ENTRY_TYPE_ID_DETAILS.put("RENT", "5");
		GET_ENTRY_TYPE_ID_DETAILS.put("UTILITIES", "3");
	}
	// Constants for Rule Class Starts
	public static final String BUSINESS_RULE_XML_PATH = "/com/nyc/hhs/config/business-rules.xml";
	public static final String RULE_SET_XML_PATH = "/com/nyc/hhs/config/ruleset.xml";
	public static final String ALT_RULE_SET = "altRuleSet";
	public static final String IF = "if";
	public static final String ELSE = "else";
	public static final String RETURN = "return";
	public static final String STRING = "String";
	public static final String SEPARATOR = "separator";
	public static final String RULE_RESULT_FORMAT_HANDLER_LEFT_SUB_STR = "com.nyc.hhs.properties.ruleresultformat-handler";
	public static final String RULES = "rules";
	public static final String RULE = "rule";
	public static final String OPERATOR = "operator";
	public static final String CHANNEL_VARIABLE2 = "channel_variable2";
	public static final String CHANNEL_VARIABLE = "channel_variable";
	public static final String METHOD = "method";
	public static final String HANDLER_NAME = "handlerName";
	public static final String RULE_HANDLER_LEFT_SUB_STR = "com.nyc.hhs.properties.rule-handler";
	public static final String RULE_SET_XPATH_STR_LEFT_PART = "//ruleset[(@id=\"";
	public static final String RULE_SET_XPATH_STR_RIGHT_PART = "\")]";
	public static final String RULE_XPATH_STR_LEFT_PART = "//rule[(@id=\"";
	public static final String EXPRESSION = "expression";
	public static final String CHANNEL = "channel";
	public static final String ELEMENT_XPATH = "//element[(@id=\"";
	// Constants for Rule Class Ends

	// BudgetConfiguration Constants starts
	public static final String FETCH_CONTRACT_CONF_COA_CURR_DETAILS = "fetchContractConfCurrCOADetails";
	public static final String FETCH_CONTRACT_CONF_COA_DETAILS = "fetchContractConfCOADetails";
	public static final String FETCH_R2_CONTRACT_CONF_COA_DETAILS = "fetchR2ContractConfCOADetails";
	public static final String FETCH_CONTRACT_CONF_SUB_BUDGET_DETAILS = "fetchContractConfSubBudgetDetails";
	public static final String FETCH_CONTRACT_CONFIG_DETAILS = "fetchContractConfigDetails";
	public static final String FETCH_CONTRACT_CONFIG_DETAILS_R3_CONTRACT = "fetchContractConfigDetailsForR3Contract";
	public static final String DEL_CONTRACT_CONF_COA_DETAILS = "delContractConfCOADetails";
	public static final String ADD_CONTRACT_CONF_COA_DETAILS = "addContractConfCOADetails";
	public static final String EDIT_CONTRACT_CONF_SUB_BUDGET_DETAILS = "editContractConfSubBudgetDetails";
	public static final String DEL_CONTRACT_CONF_SUB_BUDGET_DETAILS = "delContractConfSubBudgetDetails";
	public static final String HYPHEN = "-";
	// BudgetConfiguration Constants ends

	// ConfigurationService Constant start
	public static final String CS_FETCH_PROCUREMENT_DETAILS = "fetchProcurementCOFDetails";
	public static final String CS_FETCH_ADDENDUM_PROCUREMENT_DETAILS = "fetchProcurementAddendumCOFDetails";
	public static final String CS_SET_PROCUREMENT_STATUS = "updateProcStatus";
	public static final String CS_FETCH_PROCUREMENT_COF_STATUS = "fetchProcurementCOFStatus";
	public static final String CS_FETCH_PROCUREMENT_COA_DETAILS = "fetchProcurementCoADetails";
	public static final String CS_FETCH_CONTRACT_CONF_FUNDING_DETAILS = "fetchContractConfFundingDetails";
	public static final String CS_ADD_CONTRACT_CONFIG_DETAILS = "addContractConfFundingDetails";
	public static final String CS_FETCH_CONTRACT_AMENDMENT_FUNDING_DETAILS = "fetchContractAmendmentFundingDetails";
	public static final String CS_ADD_CONTRACT_AMENDMENT_FUNDING_DETAILS = "addContractAmendmentFundingDetails";
	public static final String CS_EDIT_CONTRACT_AMENDMENT_FUNDING_DETAILS = "editContractAmendmentFundingDetails";
	public static final String CS_FETCH_PROCUREMENT_FUNDING_DETAILS = "fetchProcurementFundingSourceDetails";
	public static final String CS_INSERT_PROCUREMENT_FUNDING_DETAILS = "insertProcurementFundingSourceDetails";
	public static final String CS_FEDERAL_AMOUNT_STRING = "federal_amount = '";
	public static final String CS_STATE_AMOUNT_STRING = "state_amount = '";
	public static final String CS_CITY_AMOUNT_STRING = "city_amount = '";
	public static final String CS_OTHER_AMOUNT_STRING = "other_amount = '";
	public static final String CS_AMOUNT = "asAmount";
	public static final String CS_CONTRACT_WHERE_CLAUSE = "where contract_id ='";
	public static final String CS_CONTRACT_AMENDMENT_WHERE_CLAUSE = "where contract_id ='";
	public static final String CS_CONTRACT_AMENDMENT_WHERE_CLAUSE_PART1 = "and contract_id='";
	public static final String CS_CONTRACT_AMENDMENT_WHERE_CLAUSE_PART2 = "and contract_type_id ='";
	public static final String CS_FISCAL_YEAR_WHERE_CLAUSE = "and fiscal_year_id ='";
	public static final String CS_QUERY_CLAUSE = "asQuerySetClause";
	public static final String CS_EDIT_CONTRACT_CONF_FUNDING_DETAILS = "editContractConfFundingDetails";
	public static final String CS_INSERT_PROCUREMENT_COA_DETAILS = "insertProcurementCoADetails";
	public static final String CS_UPDATE_PROCUREMENT_COA_DETAILS_STATUS = "updateStatusForProcurement";
	public static final String ACCOUNTS_ALLOCATION_BEAN = "com.nyc.hhs.model.AccountsAllocationBean";
	public static final String CS_UPDATE_PROCUREMENT_COA_DETAILS = "updateProcurementCoADetails";
	public static final String CS_DELETE_PROCUREMENT_COA_DETAILS = "deleteProcurementCoADetails";
	public static final String CS_PROCUREMENT_WHERE_CLAUSE = "where procurement_id ='";
	public static final String CS_UPDATE_PROCUREMENT_FUNDING_DETAILS = "updateProcurementFundingSourceDetails";
	public static final String CS_UPDATE_CONTRACT_COF = "updateContractCofTaskStatus";
	public static final String CS_FETCH_CONTRACT_SOURCE_ID = "fetchContractSourceId";
	public static final String CS_FETCH_BUDGET_CONFIGURATION = "updateBudgetConfiguration";
	public static final String CS_APPROVED_TASK_COUNT = "fetchCountOfTaskApproved";
	public static final String CS_UPDATE_CONTRACT_STATUS = "updateContractStatus";
	public static final String CS_UPDATE_CONTRACT_PROC_SELECTIONS_MADE = "updateContractProcSelectionsMade";
	public static final String CS_UPDATE_R2_CONTRACT_STATUS_PENDING_REGISTRATION = "updateR2ContractStatusToPendReg";
	public static final String CS_UPDATE_BUDGET_PROC_SELECTIONS_MADE = "updateBudgetProcSelectionsMade";
	public static final String CS_UPDATE_R2_BUDGET_STATUS_PENDING_SUBMISSION = "updateR2BudgetStatusToPendSub";
	public static final String CS_UPDATE_CONTRACT_COF_STATUS = "updateContractStatusCof";
	public static final String CS_UPDATE_AMENDMENT_BUDGET_STATUS = "updateAmendmentBudgetStatus";
	public static final String CS_UPDATE_CONTRACT_CONF_TASK_STATUS = "updateContractConfigurationTaskStatus";
	public static final String CS_UPDATE_CONTRACT_STATUS_TO_PENDING_CONFIG = "updateContractStatusToPendingConfig";
	public static final String CS_UPDATE_CONTRACT_CONF_TASK_STATUS_TO_RETURN = "updateContractCofTaskStatusToReturnForRevision";
	public static final String EDIT_CONTRACT_CONF_COA_DETAILS = "editContractConfCOADetails";
	public static final String FISCAL_YEAR_ID_KEY = "asFiscalYearId";
	public static final String CS_INSERT_CONTRACT_CONF_SUBBUDGET_DETAILS = "insertContractConfSubBudgetDetails";
	public static final String CS_INSERT_CONTRACT_CONF_SUBBUDGET_DETAILS_WITH_PARENT = "insertContractConfSubBudgetDetailsWithParentId";
	public static final String CS_EDIT_CONTRACT_CONF_SUBBUDGET_DETAILS1 = "editContractConfSubBudgetDetails1";
	/*Start: added in release 7 for defect 6596  */
	public static final String UPDATE_SUBBUDGET_NAME = "updateSubBudgetName";
	/*Ends: added in release 7 for defect 6596  */
	public static final String CS_GET_SUBBUDGET_TOTAL = "getSubBudgetsSumTotal";
	public static final String CS_GET_WF_DETAILS = "getDataWFbyProcId";
	public static final String CS_SET_PCOF_STATUS = "setPCOFStatus";
	public static final String CS_FETCH_CONTRACT_CONF_UPDATE_DETAILS = "fetchContractConfUpdateDetails";
	public static final String CS_ADD_CONTRACT_CONF_UPDATE_TASK_DETAILS = "addContractConfUpdateTaskDetails";
	public static final String CS_EDIT_CONTRACT_CONF_UPDATE_TASK_DETAILS = "editContractConfUpdateDetails";
	public static final String CS_FETCHED_CONTRACT_DETAILS = "fetchedContractDetails";
	public static final String CS_INSERT_FETCHED_CONTRACT_DETAILS = "insertFetchedContractDetails";
	public static final String CS_CONTRACT_FINANCIAL_BEAN = "com.nyc.hhs.model.ContractFinancialBean";
	public static final String CS_FETCH_CONTRACT_FINANCIAL_DETAILS = "fetchedContractFinancialDetails";
	public static final String CS_INSERT_FETCH_CONTRACT_FINANCIAL_DETAILS = "insertFetchedContractFinancialDetails";
	public static final String CS_FETCH_CONTRACT_CONF_UPDATE_ACTUAL_DETAILS = "fetchContractConfUpdateActualDetails";
	public static final String CS_FETCH_CONTRACT_CONF_UPDATE_SUBBUDGET_DETAILS = "fetchContractConfUpdateSubBudgetDetails";
	public static final String CS_FETCH_INVOICE_AMOUNT = "fetchInvoiceAmount";
	public static final String CS_ADD_CONTRACT_CONF_UPDATE_BUDGET_DETAILS = "addContractConfUpdateBudgetDetails";
	public static final String CS_INSERT_CONTRACT_CONF_UPDATE_SUB_BUDGET_DETAILS = "insertContractConfUpdateSubBudgetDetails";
	public static final String CS_INSERT_CONTRACT_CONF_AMENDMENT_SUB_BUDGET_DETAILS = "insertContractConfAmendmentSubBudgetDetails";
	public static final String CS_FETCH_MODIFIED_AMOUNT = "fetchModifiedAmountByParentId";
	public static final String CS_EDIT_CONTRACT_CONF_UPDATE_SUB_BUDGET_DETAILS = "editContractConfUpdateSubBudgetDetails";
	public static final String CS_EDIT_NEW_FY_CONF_COA_DETAILS = "editNewFYConfCOADetails";
	public static final String CS_FETCH_CONTRACT_CONF_SUBBUDGET_DETAILS1 = "fetchContractConfSubBudgetDetails1";
	public static final String CS_FETCH_FY_PLANNED_AMOUNT = "fetchFYPlannedAmount";
	public static final String CS_FETCH_AMENDMENT_FY_PLANNED_AMOUNT = "fetchAmendmentFYPlannedAmount";
	public static final String CS_NEW_ROW_STRING = "_new_row";
	public static final String NEW_DECIMAL_FORMAT = "##########.##";

	public static final String AMENDMENT_CONTRACT_BUDGETS = "jsp/bmc/amendmentContractBudgets";
	public static final String FETCH_AWARDS_DETAILS = "fetchAwardsDetails";
	public static final String AWARD_DETAILS = "awardDetails";
	public static final String MODIFIED_FLAG = "modifiedFlag";
	public static final String PARAM_VALUE = "paramValue";
	public static final String CANCEL_AWARD = "cancelAward";
	public static final String CANCEL_AWARD_SUCESS = "cancelAwardSucess";
	public static final String AWARD_BEAN_VALUE_OBJECT = "awardBean";
	public static final String STATUS_INFO_MAP = "loStatusInfoMap";
	public static final String STATUS_INFO_MAP_ARG = "aoStatusInfo";
	public static final String UPDATED_RELATED_PROPOSAL = "updateRelatedProposal";
	public static final String UPDATED_MODIFIED_FLAG_EVAL_RESULTS = "updateModifiedFlagEvalResults";
	public static final String UPDATE_AWARD_REVIEW_STATUS = "updateAwardReviewStatus";
	// Commented for enhancement 6448 for Release 3.8.0
	// public static final String CHECK_AWARD_REVIEW_STATUS =
	// "checkAwardReviewStatus";
	public static final String INSERT_AWARD_REVIEW_STATUS = "insertAwardReviewStatus";
	public static final String UPDATE_AWARD_STATUS = "updateAwardStatus";
	public static final String CANCEL_EVALUATION_TASK = "cancelEvaluationTask";
	public static final String CANCEL_EVALUATION_TASK_SUCESS = "cancelEvaluationTaskSucess";
	public static final String TOTAL_EVALUATION_DATA = "totalEvaluationData";
	public static final String EVALUATION_BEAN = "loEvaluationBean";
	public static final String EVALUATION_BEAN_LIST = "loEvaluationDetailList";
	public static final String EVALUATION_DETAILS_LIST = "evaluationDetailList";
	public static final String SEND_EVALUATION_VISIBILTY_FLAG_KEY = "sendEvaluationVisibiltyFlag";
	public static final String SEND_EVALUATION_VISIBILTY_FLAG = "lbSendTaskVisibleStatus";
	public static final String DOWNLOAD_DBD_VISIBILTY_FLAG_KEY = "downloadDBDVisibiltyFlag";
	public static final String DOWNLOAD_DBD_VISIBILTY_FLAG = "lbDownloadVisibleStatus";
	public static final String CANCEL_EV_TASK_VISIBILTY_FLAG_KEY = "lbCancelEvTaskVisibiltyFlag";
	public static final String CANCEL_EV_TASK_VISIBILTY_FLAG = "lbCancelVisibleStatus";
	public static final String CLOSE_BUTTON_ENABLE_FLAG = "state";
	public static final String CLOSE_BUTTON_VISIBILTY_FLAG = "loCloseButtonVisibleStatus";
	public static final String PROPOSAL_EVALUATION_STATUS = "proposalsEvaluationStatus";

	// ConfigurationService Constant end
	public static final String USER_INTERNAL_COMMENT = "INTERNAL_COMMENT";
	public static final String USER_PROVIDER_COMMENT = "PROVIDER_COMMENT";
	public static final String CREATED_BY_USERID = "CREATED_BY_USERID";
	public static final String AS_TASK_LEVEL = "asTaskLevel";
	public static final String AS_PROCESS_ID = "asProcessId";
	public static final String HEADER = "header";
	public static final String IS_SUCCESS = "sucess";
	public static final String IS_STATUS_ERROR = "loStatusError";
	public static final String FETCH_LAST_COMMENT = "fetchLastComment";
	public static final String FETCH_LAST_TASK_COMMENT = "fetchLastTaskComment";
	public static final String FETCH_AGENCY_DETAILS = "fetchAgencyDetails";
	public static final String FETCH_PROVIDER_TASK_HISTORY = "fetchProviderTaskHistory";
	public static final String FETCH_PROVIDER_TASK_HISTORY_TAB_LEVEL = "fetchProviderTaskHistoryTabLevel";
	public static final String FETCH_PROVIDER_TAB_HIGHLIGHT_FROM_AGRNCY_AUDIT = "fetchProviderTabHighlightFromAgencyAudit";
	public static final String HIGHLIGHT_TAB_INSERT = "highlightTabInsert";
	public static final String HIGHLIGHT_TAB_UPDATE = "highlightTabUpdate";
	public static final String HIGHLIGHT_TAB_UPDATE_ON_AGENCY_TASK_FINISH = "highlightTabUpdateOnAgencyTaskFinish";
	public static final String HIGHLIGHT_TAB_DELETE = "highlightTabDelete";
	public static final String FETCH_AGENCY_TASK_HISTORY = "fetchAgencyTaskHistory";
	public static final String FETCH_AGENCY_TASK_HISTORY_TAB_LEVEL = "fetchAgencyTaskHistoryTabLevel";
	public static final String FETCH_TASK_ID_STATUS = "fetchTaskIdStatus";
	public static final String WOB_NUM = "wobNum";
	public static final String FETCH_WORKFLOW_ID_FROM_VIEW = "fetchWorkflowIDFromView";
	public static final String LO_HM_WF_PROPERTIES = "loHmWFProperties";
	public static final String AS_TASK_COMMENT = "asTaskComment";
	public static final String ORGANIZATION_TYPE = "organizationType";
	public static final String AO_FINANCIAL_DOCUMENT_LIST = "aoFinalFinancialDocumentList";
	public static final String GET_FINANCIAL_DOCUMENTS_DB = "getFinancialDocuments_db";
	public static final String CCT_FINISH_CONTRACT_CONFIG_TASK = "finishContractConfigTask";
	public static final String CCT_LINEITEMS_CONTRACT_CONFIG_TASK = "insertLineItemsForContractConfigTask";

	// Constants for BaseController

	public static final String FY3 = "\",\"fy";
	public static final String _0 = "\":\"0\",";
	public static final String FY2 = "\"fy";
	public static final String EDITABLE_TRUE_EDITRULES_REQUIRED_TRUE_NUMBER_TRUE = ",{editable:true,editoptions:{maxlength:20}, editrules:{required:true,number:true,allowOnlyPositiveValue}}";
	public static final String TEMPLATE_CURRENCY_TEMPLATE = "', template : currencyTemplate}";
	public static final String NAME_FY = ",{name:'fy";
	public static final String STR = "'";
	public static final String STR_DOUBLE = "''";
	public static final String FY = ",'FY";
	public static final String EDITABLE_FALSE_EDITRULES_REQUIRED_TRUE = "{editable:false, editrules:{required:true}}";
	public static final String NAME_TOTAL_FUNDING_TEMPLATE_CURRENCY_TEMPLATE = ",{name:'totalFunding',template : currencyTemplate}";
	public static final String NAME_FUNDING_TYPE = "{name:'fundingType'}";
	public static final String FUNDING_SOURCES = "'Funding Sources'";
	public static final String EDITABLE_FALSE = ",{editable:false}";
	public static final String EDITABLE_TRUE_EDITRULES_REQUIRED_TRUE_EDITABLE_TRUE_EDITABLE_TRUE = "{editable:true, editrules:{required:true,notAllowDuplicateValue}},{editable:true,editoptions:{maxlength:4}},{editable:true,editoptions:{maxlength:6}}";
	public static final String EDITABLE_FALSE_EDITRULES_REQUIRED_TRUE_EDITABLE_FALSE_EDITABLE_FALSE = "{editable:false,editrules:{required:true,notAllowDuplicateValue}},{editable:false,editoptions:{maxlength:4}}, {editable:false,editoptions:{maxlength:6}}";
	public static final String NAME_TOTAL_TEMPLATE_CURRENCY_TEMPLATE = ",{name:'total', template : currencyTemplate}";
	public static final String NAME_UOBC_NAME_SUB_OC_NAME_RC = "{name:'uobc'},{name:'subOC'},{name:'rc'}";
	public static final String UO_A_BC_OC_SUB_OC_RC = "'UoA*-BC*-OC*','SubOC','RC'";
	public static final String EPIN_CONTRACT_LIST = "epinContractList";
	public static final String FETCH_CONTRACT_EPIN_LIST = "fetchContractEpinList";
	public static final String AO_EPIN_KEYS_LIST = "aoEpinKeysList";
	public static final String FETCH_EPIN_LIST_DB = "fetchEpinList_db";
	public static final String EPIN_PROCUREMENT_LIST = "epinProcurementList";
	public static final String FETCH_PROCUREMENT_EPIN_LIST = "fetchProcurementEpinList";
	public static final String EPIN_QUERY_ID = "epinQueryId";
	public static final String DEFAULT = "default";
	public static final String SAME = "same";
	public static final String IS_SECOND_SORT_DATE = "isSecondSortDate";
	public static final String SECOND_COLUMN_NAME = "secondColumnName";
	public static final String IS_FIRST_SORT_DATE = "isFirstSortDate";
	public static final String FIRST_COLUMN_NAME = "firstColumnName";
	public static final String TASK_DETAILS = "taskDetails";
	public static final String FETCH_TASK_DETAILS = "fetchTaskDetails";
	public static final String COMMENTS_HISTORY_BEAN = "commentsHistoryBean";
	public static final String REASSIGN_TASK = "reassignTask";
	public static final String LO_AUDIT_LIST = "loAuditList";
	public static final String HDN_INTERNAL_COMMENT = "hdnInternalComment";
	public static final String HDN_PROVIDER_COMMENT = "hdnProviderComment";
	public static final String REASSIGNTOUSER_TEXT = "reassigntouserText";
	public static final String ASSIGN_USER = "assignUser";
	public static final String HDN_WORK_FLOW_ID = "hdnWorkFlowId";
	public static final String PUBLIC_COMMENT_AREA = "publicCommentArea";
	public static final String INTERNAL_COMMENT_AREA = "internalCommentArea";
	public static final String HDN_TASK_TYPE = "hdnTaskType";
	public static final String SUB_BUDGET_ID = "subBudgetID";
	public static final String CONTRACT_BUDGET_ID = "contractBudgetID";
	public static final String FISCAL_YEAR_ID = "fiscalYearID";
	public static final String INVALID_USER_MSG = "The username or password you have entered is incorrect. Please enter the correct username and password.";
	public static final String BUDGET_TYPE_ID = "budgetTypeId";
	public static final String AS_BUDGET_TYPE_ID = "asBudgetTypeId";
	public static final String CREATED_BY_USER_ID = "createdByUserId";
	public static final String CONTENT_BY_TYPE = "contentByType";
	public static final String FUNDING_TYPE = "fundingType";
	// Award Approve Task
	public static final String AWARD_APPROVAL_TASK = "Approve Award";
	public static final String AWARD_APPROVAL_TASK_WF = "Award Approval Task Workflow";
	public static final String FETCH_CURRENT_CB_STATUS = "fetchCurrentCBStatus";
	public static final String BUDGET_STATUS = "budgetStatus";
	public static final String CB_CANCELLED = "CB_CANCELLED";
	public static final String SUB_BUDGET_SITE_REQUIRED = "SUB_BUDGET_SITE_REQUIRED";
	public static final String CB_CLOSED = "CB_CLOSED";
	public static final String CB_SUSPENDED = "CB_SUSPENDED";
	public static final String CB_SAVED = "CB_SAVED";
	public static final int INT_ZERO = 0;
	public static final int INT_SEVEN = 7;
	public static final int INT_SIX = 6;

	public static final String MODIFY_BY = "modifyBy";

	public static final String NUMBER_OF_PROPOSAL = "noOfProposal";
	public static final String EVALUATION_COMPLETE_LIST = "loEvaluationCompleteList";
	public static final String CANCEL_EVALUATION_TASKS = "cancelEvaluationTasks";
	public static final String WORK_FLOW_TERMINATED = "workFlowTerminated";
	public static final String DELETE_EVALUATION_RESULTS = "deleteEvaluationResults";
	public static final String DELETE_EVALUATION_SCORE = "deleteEvaluationScore";
	public static final String DELETE_EVALUATION_STATUS = "deleteEvaluationStatus";
	public static final String DELETE_EVALUATION_SETTINGS_INTERNAL = "deleteEvaluationSettingsInternal";
	public static final String DELETE_EVALUATION_SETTINGS_EXTERNAL = "deleteEvaluationSettingsExternal";

	// updateProcurementTableRecords
	public static final String ORGANIZATION_LEGAL_NAME = "providerName";
	public static final String PROGRAM_ID = "programId";
	public static final String FETCH_TOTAL_EVALUATION_COMPLETE = "fetchTotalEvaluationComplete";
	public static final String FETCH_TOTAL_EVALUATION_INPROGRESS = "fetchTotalEvaluationInProgess";
	public static final String ARG_EVALUATION_BEAN = "aoEvaluationBean";
	public static final String ARG_EVALUATION_LIST = "aoEvalList";
	public static final String TOTAL_EVALUATION_SETTINGS_USERS = "countEvaluationSettingsUsers";
	public static final String TOTAL_EVALUATION_USERS = "countEvaluationUsers";
	public static final String ARG_EVALUATION_DETAIL_LIST = "aoEvalutionDetailsList";
	public static final String FETCH_REVIEW_SCORE_COUNT = "fetchReviewScoreCount";
	public static final String FETCH_EVALUATOR_COUNT = "fetchEvaluatorCount";
	public static final String ENABLE = "enable";
	public static final String DISABLE = "disable";
	public static final String AO_EVAL_BEAN = "aoEvalBean";
	public static final String ARG_EVAL_AWARD_REVIEW_STATUS_BEAN = "aoEvalAwardReviewStatusBean";
	public static final String FETCH_PROCUREMENT_DATES = "fetchProcurementDates";
	public static final String FETCH_REQ_DOC_COUNT = "fetchReqDocCount";
	public static final String FETCH_REQ_DOC_NAME_KEY = "reqDocName";
	public static final String FETCH_REQ_DOC_NAME = "Doing Business Data Form";
	public static final String FETCH_EVALUATION_DETAILS = "fetchEvaluationDetails";
	public static final String FETCH_PROPOSAL_COUNT = "fetchProposalCount";
	public static final String EVALUATION_IN_PROGRESS = "evalutionsInProgress";

	// Added for contract list controller
	public static final String GET_PERSONNEL_SERVICES_DATA = "getPersonnelServicesData";
	public static final String GET_CONTRACTED_SERVICES_DATA = "getContractedServicesData";
	public static final String GET_OPERATION_SUPPORT_DATA = "getOperationSupportData";
	public static final String GET_OPERATION_SUPPORT_MOD_DATA = "getOperationSupportModData";
	public static final String INSERT_NEW_BUDGET_MODIFICATION_BUDGET_DETAILS = "insertNewBudgetModificationDetails";
	public static final String INSERT_NEW_SUB_BUDGET_MODIFICATION_BUDGET_DETAILS = "insertNewSubBudgetModificationDetails";
	public static final String GET_SEQ_ID_FOR_AUB_BUDGET_ID = "getSeqForSubBudgetId";
	public static final String INSERT_MODIFICATION_BUDGET_DETAILS = "insertModificationBudgetDetails";
	public static final String INSERT_MODIFICATION_SUB_BUDGET_DETAILS = "insertModificationSubBudgetDetails";
	public static final String AO_ADD_BUDGET = "aoAddBudget";
	public static final String ERROR_MESSAGE_AMEND = "errorMessageAmendSceenInvoice";
	public static final String ERROR_MESSAGE_AMEND_PAYMENTS = "errorMessageAmendSceenPayments";
	public static final String CLC_CAP_ERROR = "Error";

	public static final String CLC_RENEW_CONTRACT = "renewContract";
	public static final String EPIN_VALUE = "epinValue";
	public static final String LO_CONTRACT_DETAILS = "loContractDetails";
	public static final String AO_EPIN_DETAIL_BEAN = "aoEPinDetailBean";
	public static final String FETCH_CONTRACT_DETAILS_BY_EPIN = "fetchContractDetailsByEPIN";
	public static final String AO_CONTRACT_DETAIL = "aoContractDetail";
	public static final String EPIN_ERROR = "No Records Found with this Epin";
	public static final String CLC_ADD_CONTRACT = "addContract";
	public static final String LO_EPIN_DETAIL = "loEPinDetailBean";
	public static final String FETCH_CONTRACT_DETAILS_BY_EPIN_NEW = "fetchContractDetailsByEPINforNew";
	public static final String AS_EPIN = "asEPin";
	public static final String LO_AUTH_STATUS_FLAG = "loAuthStatusFlag";
	public static final String AO_CONTRACT_DETAILS_BY_EPIN = "aoContractDetailByEpin";
	public static final String VALIDATE_EPIN = "validateEpin";
	public static final String CLC_CONTRACT_ID_UNDERSCORE = "contract_Id";
	public static final String CLC_STATUS_ID = "status_Id";
	public static final String CL_CONTRACT_TYPE_ID = "contract_type_Id";
	public static final String LO_BUDGET_SUMMARY = "loBudgetSummary";
	public static final String RENEWAL_RECORD_EXIST = "renewalRecordExist";
	public static final String CONTRACT_RENEWAL_STATUS = "lbContractRenewalStatus";
	public static final String RENEW_CONTRACT_DETAILS = "renewExistingContractDetails";
	public static final String IS_RENEWAL_EXITS = "isRenewalExit";
	public static final String VIEW_AWARD_DOCUMENT = "viewAwardDocument";
	public static final String CONTRACT_CONFIG_DOC_FETCH = "contractConfigCOFDocFetch";
	public static final String AO_RETURN_FUNDING_LIST = "aoReturnedFundingList";
	public static final String CONTRACT_CERT_OF_FUNDS_DOC = "contractCOFDoc";

	public static final String CONTRACT_LIST_JSP = "contractList";
	public static final String CONTRACT_FILTER_BEAN = "contractFilterBean";
	public static final String GET_FINANCIALS_LIST = "getFinancialsList";
	public static final String AMEND_REASON = "amendReason";
	public static final String CLC_AMEND_CONTRACT_BEAN = "aoAmendContractBean";

	public static final String CLC_AMEND_CONTRACT_LIST = "fetchAmendedContractList";

	public static final String USER_PROVIDER = "provider";
	public static final String CONTRACT_AMOUNT = "contractAmount";
	public static final String AMEND_AMOUNT = "amendAmount";
	public static final String NEW_FY_PLANNED_AMOUNT = "newFyPlannedAmount";

	public static final String AMEND_START_DATE = "amendStartDate";
	public static final String AMEND_END_DATE = "amendEndDate";
	public static final String CANCEL_CONTRACT_COMMENT = "cancelContractComment";
	public static final String ERROR_CHECK_RULE = "lbErrorCheckRule";
	public static final String LB_ERROR = "lbError";
	public static final String LS_ERROR_CONSTANT = "lsErrorMsgConstant";
	public static final String CANCEL_CONTRACT_RULE1 = "cancelContractRule01";
	public static final String LB_AUDIT_STATUS = "lbAuditStatus";
	public static final String CANCEL_CONTRACT_STATUS = "cancelContractSuccess";
	public static final String CLOSE_CONTRACT_COMMENT = "closeContractComment";
	public static final String CLC_CLOSE_CONTRACT = "closeContract";
	public static final String LO_ERROR_CHECK_RULE = "loErrorCheckRule";
	public static final String ERROR_CODE = "errorCode";
	public static final String CLC_ERROR_MSG = "errorMsg";
	public static final String CONTRACT_CLOSED = "contractClosed";
	public static final String QUERY_ID = "QueryId";
	public static final String CLC_CONTRACT_NO_QUERY_ID = "contractNoQueryId";
	public static final String AO_CONTRACT_NO_LIST = "aoContractNoList";
	public static final String AUTHENTICATION_BEAN_UNSUSPEND = "authenticationBeanUnsuspend";
	public static final String AS_CONTRACT_REASON = "asContractReason";
	public static final String CLC_UNSUSPEND_CONTRACT = "UnsuspendContract";
	public static final String CLC_UNSUSPEND_STATUS = "abUnsuspendStatus";
	public static final String UNSUSPEND_STATUS_WORKFLOW = "loUnSuspendStatusWorkflow";
	public static final String CAPS_UNSUSPEND_CONTRACT = "UNSUSPEND_CONTRACT";
	public static final String CLC_AUTH_BEAN_SUSPEND = "authenticationBeanSuspend";
	public static final String CLC_SUSPEND_CONTRACT = "suspendContract";
	public static final String CLC_SUSPEND_STATUS = "abSuspendStatus";
	public static final String LO_SUSPEND_STATUS_WORKFLOW = "loSuspendStatusWorkflow";
	public static final String CAPS_SUSPEND_CONTRACT = "SUSPEND_CONTRACT";
	public static final String FINANCIALS_LIST = "financialsList";
	public static final String FIRST_LOAD = "firstLoad";
	public static final String GET_AGENCY_DETAILS = "getAgencyDetails";
	public static final String AGENCY_NAMES = "aoAgencyNames";
	public static final String AGENCY_NAME = "agencyName";
	public static final String CONTRACT_COUNT = "contractsCount";
	public static final String CB_CONTRACT_LIST_BEAN = "aoCBContractListBean";
	public static final String CLC_OVERLAY = "OverLay";
	public static final String CLC_OVERLAY_PARAM = "OverlayPageParam";
	public static final String CLC_AMEND_CONTRACT_INFO = "saveAmendContractInfo";
	// Start R7: defect 8644 part:3
	public static final String CANCEL_AND_MERGE = "cancelAndMerge";
	public static final String UPDATE_REQUEST_PARTIAL_MERGE_CONTRACT_LIST = "updateRequestPartialMergeContractList";
	public static final String GET_PARTIAL_MERGE_REQUEST_LIST="getPartialMergeRequestList";
	public static final String MARK_AMENDMENT_ETL_REGISTERED_WITH_PARTIAL_MERGE_REQUEST = "markAmendmentETLRegistredWithPartialMergeRequest";
	//public static final String GET_AMENDMENT_REGISTERED_IN_FMS_WITH_PM="getAmendmentRegisterdInFmsWithPM";
	
	
	// END R7: DEFECT 8644 PART:3

	// newTotalContractAmount
	public static final String CLC_AMEND_CONTRACT = "amendContract";
	public static final String CONTRACT_TYPE_ID = "contractTypeId";
	public static final String CONFIG_FISCAL_YEAR = "fyConfigFiscalYear";
	public static final String CONTRACT_TYPE_ID_KEY = "asContractTypeId";
	public static final String AS_AGENCY_NAME = "asAgencyName";
	public static final String CLC_FISCAL_YEAR_ID = "fiscalYearId";
	public static final String FETCH_PARENT_BUDGET_AND_SUB_BUDGET_ID = "fetchParentBudgetAndSubBudgetId";
	public static final String NEW_FY_CONFIG_ERROR_CHECK = "newFYConfigErrorCheck";
	public static final String LO_CONFIG_ERROR_CHECK = "loFYConfigErrorCheckRule";
	public static final String CLC_ERROR_MESSAGE = "ErrorMsg";
	public static final String CLC_ERROR_CODE = "ErrorCode";
	public static final String CLC_ERROR_CHECK = "errorCheck";
	public static final String CONFIRM_NEW_FY_CONFIG = "confirmNewFYConfig";
	public static final String NEW_FY_TASK_CREATED = "newFYConfigTaskCreated";
	public static final String AO_AUTH_BEAN = "aoAuthBean";
	public static final String UPDATE_CONTRACT_CONF_ERROR = "updateContractConfigurationErrorCheck";
	public static final String UPDATE_CONTRACT_CONF = "updateContractConfiguration";
	public static final String CONFIGURATION_UPDATE = "configurationUpdate";
	public static final String CLC_CONTRACT_ID = "ContractId";
	public static final String EPIN_BEAN_DETAILS = "epinBeanDetails";
	public static final String CLC_CONTRACT_TYPE_ID = "ContractTypeId";
	public static final String AO_CONTRACT_STATUS = "aoContractStatus";

	public static final String STATUS_CHANGE = "Status Change";
	public static final String CONTRACT_BUDGET_STATUS_CHANGED_FROM = "Contract Budget Status changed from ";
	public static final String _TO_ = " To ";

	public static final String SERVER_NAME_FOR_PROVIDER_BATCH = "SERVER_NAME_FOR_PROVIDER_BATCH";
	public static final String SERVER_PORT_FOR_PROVIDER_BATCH = "SERVER_PORT_FOR_PROVIDER_BATCH";
	public static final String CONTEXT_PATH_FOR_PROVIDER_BATCH = "CONTEXT_PATH_FOR_PROVIDER_BATCH";
	public static final String SERVER_PROTOCOL_FOR_PROVIDER_BATCH = "SERVER_PROTOCOL_FOR_PROVIDER_BATCH";

	public static final String FETCH_PROGRAM_INCOME_INVOICE = "fetchProgramIncomeInvoice";
	public static final String UPDATE_PROGRAM_INCOME_INVOICE = "updateProgramIncomeInvoice";
	public static final String VIEW_PROPOSAL = "View Proposal";
	public static final String MARK_RETURNDED_FOR_REVISION = "Mark Returned for Revision";
	public static final String MARK_NON_RESPONSIVE = "Mark Non-Responsive";
	public static final String ACTIONS = "actions";
	public static final String PRINTER_SUB_BUDGET_ID = "printerViewSubBudgetId";
	public static final String REVIEW_PROCESS_ID_KEY = "reviewProcessId";
	public static final String CM_FIND_CONTRACT_DTLS_WF = "findContractDetailsByContractForWF";
	public static final String LAUNCH_WF_CONTRACT_INVOICE = "launchWFInvoice";
	public static final String IC_UPD_INVOICE_STATUS = "updateInvoiceStatus";
	public static final String INVOICE_STATUS_CHANGED_FROM = "Invoice Status changed from ";
	public static final String INVOICE_HANDLER = "invoiceHandler";
	public static final String INVOICE_STATUS = "invoiceStatus";
	public static final String INVOICE_STATUS_ID = "asInvoiceStatus";
	public static final String PUBLIC_CMNT_ID = "asPublicComment";
	public static final String TRANSACTION_RSLT_STATUS = "transactionBudgetStatus";
	public static final String TRANSACTION_RSLT_MSG = "transactionBudgetMessage";
	public static final String CONTRACT_INVOICE_SUBMITTED = "CONTRACT_INVOICE_SUBMITTED";
	public static final String REQUEST_NOT_COMPLETED = "REQUEST_COULD_NOT_BE_COMPLETED";
	public static final String INVOICE_PAGE_READ_ONLY = "invoicePageReadOnly";
	public static final String AO_HM_INVOICE_REQUIRED_PROPS = "aoHmInvoiceRequiredProps";

	// Invoice Service
	public static final String IS_LINE_ITEM_INVOICE_AMT = "lineItemInvoiceAmt";
	public static final String ENTRY_TYPE_ID = "entryTypeId";
	public static final String IS_INSERT_INVOICING_LINE_ITEM_DETAILS = "insertInvoicingLineItemDetails";
	public static final String IS_UPDATE_INVOICING_DETAILS = "updateInvoicingDetails";
	public static final String IS_FETCH_UTILITIES_REMAINING_AMOUNT = "fetchUtilitiesRemainingAmount";
	public static final String IS_FETCH_CONTRACTED_SERVICES_REMAINING_AMOUNT = "fetchContractedServicesRemainingAmount";
	public static final String RESET_PROPOSAL_DOCUMENT_STATUS_COMPLETED = "resetDocumentStatusCompleted";

	public static final String INV_FETCH_INVOICE_PROF_SERVICES = "fetchInvoiceProfServices";
	public static final String INV_GET_INVOICE_DETAIL_ID = "getInvoiceDetailId";
	public static final String INV_FETCH_TOTAL_REMAINING_AMNT = "fetchTotalRemainingAmnt";
	public static final String INV_FETCH_REMAINING_AMNT = "fetchRemainingAmnt";
	public static final String FETCH_PROF_SERVICE_VALIDATION_IN_MULTIPLE_AMENDMENTS = "fetchProfServiceForValidationInMultipleAmendments";

	public static final String INV_ADD_INVOICE_FOR_PROF_SERVICES = "addInvoiceForProfServices";
	public static final String INV_UPDATE_INVOICE_FOR_PROF_SERVICES = "updateInvoiceForProfServices";
	public static final String IS_FETCH_REMAINING_AMOUNT_MILESTONE = "fetchRemainingAmountMilestone";
	public static final String IS_FETCH_INVOICING_UTILITIES_DETAILS = "fetchInvoicingUtilitiesDetails";
	public static final String IS_FETCH_INVOICE_INDIRECT_DETAILS = "fetchInvoiceIndirectRate";
	public static final String IS_FETCH_INDIRECT_REMAINING_AMOUNT = "fetchIndirectRemainingAmount";
	public static final String CBY_COM_NYC_HHS_MODEL_CB_INDIRECT = "com.nyc.hhs.model.CBIndirectRateBean";

	// SelectionDetailsController starts
	public static final String USER_ORG_ID = "userOrgId";
	public static final String PARAMETERS_MAP_LOWERCASE = "parametersMap";
	public static final String FETCH_PROPOSALS_DETAILS = "fetchProposalSelectionDetails";
	public static final String FINAL_AWARD_DOC_LIST = "finalAwardDocumentList";
	public static final String FINAL_AWARD_CONFIG_DOC_LIST = "finalAwardConfigDocumentList";
	public static final String AWARD_BEAN_DETAIL = "awardBeanDetail";
	public static final String AWARD_DOC_LIST = "awardDocumentList";
	public static final String AWARD_CONFIG_OPT_DOC = "awardConfigOptDocument";
	public static final String AWARD_CONFIG_REQ_DOC = "awardConfigReqDocument";
	public static final String ORG_NAME = "organizaionName";
	public static final String PROVIDERS_SELECTION_DETAILS = "providerselectiondetails";
	public static final String VIEW_PROPOSAL_SUMMARY = "viewProposalSummary";
	public static final String REMOVE_DOC = "removeDocument";
	public static final String IS_FINANCIAL = "isFinancials";
	public static final String VIEW_MORE_DOCUMENTS = "viewMoreDocuments";
	public static final String FILE_INFO = "fileinformation";
	public static final String OS = "Operations,Support and Equipment";
	public static final String OPS = "Operations,Support";
	public static final String EQP = "Equipment";
	public static final String TS = "Total Salary";
	public static final String TF = "Total Fringe";
	public static final String PS = "Professional Services";
	public static final String RO = "Rent and Occupancy";
	public static final String CS = "Contracted Services";
	public static final String TR = "Total Rate Based";
	public static final String TM = "Total Milestone Based";
	public static final String TI = "Total Indirect Costs";
	public static final String TP = "Total Program Income";
	public static final String TSF = "Total Salary and Fringe";
	public static final String TOTPS = "Total OTPS";
	public static final String TPB = "Total Program Budget";
	public static final String TCFB = "Total City Funded Budget";
	public static final String TDC = "Total Direct Costs";

	public static final String OS_KEY = "OS";
	public static final String OPS_KEY = "OPS";
	public static final String EQP_KEY = "EQP";
	public static final String TS_KEY = "TS";
	public static final String TF_KEY = "TF";
	public static final String UT_KEY = "UT";
	public static final String PS_KEY = "PS";
	public static final String RO_KEY = "RO";
	public static final String CS_KEY = "CS";
	public static final String TR_KEY = "TR";
	public static final String TM_KEY = "TM";
	public static final String UF_KEY = "UF";
	public static final String TI_KEY = "TI";
	public static final String TP_KEY = "TP";
	public static final String TSF_KEY = "TSF";
	public static final String TOTPS_KEY = "TOTPS";
	public static final String TPB_KEY = "TPB";
	public static final String TCFB_KEY = "TCFB";
	public static final String TDC_KEY = "TDC";

	public static final String OS_ENTRY_TYPE_KEY = "OSEntryType";
	public static final String TS_ENTRY_TYPE_KEY = "TSEntryType";
	public static final String TF_ENTRY_TYPE_KEY = "TFEntryType";
	public static final String UT_ENTRY_TYPE_KEY = "UTEntryType";
	public static final String PS_ENTRY_TYPE_KEY = "PSEntryType";
	public static final String RO_ENTRY_TYPE_KEY = "ROEntryType";
	public static final String CS_ENTRY_TYPE_KEY = "CSEntryType";
	public static final String TR_ENTRY_TYPE_KEY = "TREntryType";
	public static final String TM_ENTRY_TYPE_KEY = "TMEntryType";
	public static final String UF_ENTRY_TYPE_KEY = "UF_ENTRY_TYPE";
	public static final String TI_ENTRY_TYPE_KEY = "TIEntryType";
	public static final String TP_ENTRY_TYPE_KEY = "TPEntryType";
	public static final String EQUI_ENTRY_TYPE_KEY = "EQUIEntryType";

	public static final String STRING_THIRTEEN = "13";
	public static final String CS_ENTRY_TYPE = "6";
	public static final String UF_ENTRY_TYPE = "9";
	public static final String STRING_ELEVEN = "11";
	public static final String EQUI_ENTRY_TYPE = "12";

	public static final Map<String, String> CONTRACT_BUDGET = new HashMap<String, String>();
	static
	{
		CONTRACT_BUDGET.put(OPS, "operationsAndSupportAmount");
		CONTRACT_BUDGET.put(EQP, "equipmentAmount");
		CONTRACT_BUDGET.put(TF, "totalFringes");
		CONTRACT_BUDGET.put(TS, "totalSalary");
		CONTRACT_BUDGET.put("Utilities", "utilitiesAmount");
		CONTRACT_BUDGET.put(PS, "professionalServicesAmount");
		CONTRACT_BUDGET.put(RO, "rentAndOccupancyAmount");
		CONTRACT_BUDGET.put(CS, "contractedServicesAmount");
		CONTRACT_BUDGET.put(TR, "totalRateBasedAmount");
		CONTRACT_BUDGET.put(TM, "totalMilestoneBasedAmount");
		CONTRACT_BUDGET.put("Unallocated Funds", "unallocatedFunds");
		CONTRACT_BUDGET.put(TI, "totalIndirectCosts");
		CONTRACT_BUDGET.put(TP, "totalProgramIncome");
		CONTRACT_BUDGET.put(TSF, "totalSalaryAndFringesAmount");
	}

	public static final Map<String, String> CONTRACT_BUDGET_SUMMARY_MAP = new HashMap<String, String>();
	static
	{
		CONTRACT_BUDGET_SUMMARY_MAP.put("otps", "operationsAndSupportAmount");
		CONTRACT_BUDGET_SUMMARY_MAP.put("salary", "totalSalary");
		CONTRACT_BUDGET_SUMMARY_MAP.put("fringes", "totalFringes");
		CONTRACT_BUDGET_SUMMARY_MAP.put("utilities", "utilitiesAmount");
		CONTRACT_BUDGET_SUMMARY_MAP.put("professionalServices", "professionalServicesAmount");
		CONTRACT_BUDGET_SUMMARY_MAP.put("rent", "rentAndOccupancyAmount");
		CONTRACT_BUDGET_SUMMARY_MAP.put("equipment", "equipmentAmount");
		CONTRACT_BUDGET_SUMMARY_MAP.put("contractedServices", "contractedServicesAmount");
		CONTRACT_BUDGET_SUMMARY_MAP.put("totalRateBased", "totalRateBasedAmount");
		CONTRACT_BUDGET_SUMMARY_MAP.put("totalMilestoneBased", "totalMilestoneBasedAmount");
		CONTRACT_BUDGET_SUMMARY_MAP.put("unallocatedFunds", "unallocatedFunds");
		CONTRACT_BUDGET_SUMMARY_MAP.put("totalIndirectCost", "totalIndirectCosts");
		CONTRACT_BUDGET_SUMMARY_MAP.put("totalProgramIncome", "totalProgramIncome");
	}

	// Award Service
	public static final String PARAM_MAP_LOWERCASE = "paramMap";
	public static final String FETCH_AWARD_DETAILS = "fetchAwardDetails";
	public static final String FETCH_AWARD_ID = "fetchAwardId";
	public static final String UPDATE_AWARD_APPROVE_STATUS = "updateAwardApproveStatus";
	public static final String COM_NYC_HHS_MODEL_EVALFILTERBEAN = "com.nyc.hhs.model.EvaluationFilterBean";
	public static final String EVALUATION_FILTER_BEAN = "EvaluationFilterBean";
	public static final String REMOVE_AWARD_DOCS = "removeAwardDocuments";
	public static final String FETCH_EVAL_RESULT_COUNT = "fetchEvaluationResultsCount";

	public static final String IS_FETCH_RENT_REMAINING_AMOUNT = "fetchRentRemainingAmount";
	public static final String IS_EDIT_RENT_REMAINING_AMOUNT = "updateRentRemainingAmount";
	public static final String IS_FETCH_INVOICING_RENT = "fetchInvoicingRent";
	// close submissions
	public static final String FETCH_PROVIDERS_AND_PROPOSALS_NO = "fetchProvidersAndProposalsNo";
	public static final String NO_OF_PROVIDERS = "loNoOfProviders";
	public static final String NO_OF_PROPOSALS = "loNoOfProposals";
	public static final String CURRENT_TIME_STAMP = "currentTimeStamp";
	public static final String RELEASE_TIME = "releaseTime";
	public static final String INPUT_PARAM_MAP = "aoInputParam";
	public static final String PROPOSAL_RECIEVED = "Proposals Recieved";
	public static final String PROC_STATUS_CHANGED_TO_PROPOSAL_RECIVED = "Procurement Status has been changed to Proposals Received";
	public static final String PROC_STATUS_CHANGED_TO_SELECTIONS_MADE = "Procurement Status has been changed to Selections Made";
	public static final String FETCH_NO_OF_PROVIDERS = "fetchNoOfProviders";
	public static final String FETCH_NO_OF_PROPOSALS = "fetchNoOfProposals";
	public static final String UPDATE_FOR_CLOSE_SUBMISSION = "updateProcurementForCloseSubmissions";
	public static final String CLOSE_SUBMITTION = "closeSubmittion";
	public static final String CLOSE_SUBMITTION_SUCCESS = "closeSubmittionSuccess";
	public static final String CLOSE_SUBMISSIONS = "closeSubmissions";
	public static final String ERROR_PAGE_CLOSE_SUBMISSIONS = "errorPageCloseSubmissions";
	public static final String LB_CLOSE_SUBMISSION_STATUS = "lbCloseSubmissionStatus";
	public static final String CLOSE_SUBMISSION = "closeSubmission";

	// Proposal Service
	public static final String UPDATE_PROPOSAL_DETAILS = "updateProposalDetails";
	public static final String UPDATE_PROPOSAL_ANSWERS = "updateProposalAnswers";
	public static final String INSERT_PROPOSAL_ANSWERS = "insertProposalAnswers";
	public static final String DEL_PROPOSAL_SITE_DETAILS = "deleteProposalSiteDetails";
	public static final String DEL_SUB_BUDGET_SITE_DETAILS = "deleteSubBudgetSiteDetails";
	public static final String COM_NYC_HHS_MODEL_SITEDETAILSBEAN = "com.nyc.hhs.model.SiteDetailsBean";
	public static final String INSERT = "insert";
	public static final String DELETE = "delete";
	public static final String UPDATE = "update";
	public static final String INS_PROPOSAL_SITE_DETAILS = "insertProposalSiteDetails";
	public static final String INS_SUB_BUDGET_SITE_DETAILS = "insertSubBudgetSiteDetails";
	public static final String UPDATE_PROPOSAL_SITE_DETAILS = "updateProposalSiteDetails";
	public static final String UPDATE_SUB_BUDGET_SITE_DETAILS = "updateSubBudgetSiteDetails";
	public static final String FETCH_MEMBER_DETAILS = "fetchMemberDetails";
	public static final String FETCH_ALL_ORG_MEMBERS = "fetchAllOrganizationMembers";
	public static final String GET_PROPOSALS_DETAILS_COUNT = "getProposalDetailsCount";
	public static final String GET_PROPOSALS_QUES_RESPONSE_COUNT = "getProposalQuesResponseCount";
	public static final String GET_PROPOSAL_SITE_COUNT = "getProposalSiteCount";
	public static final String GET_DUE_DATE = "getDueDate";
	public static final String GET_PROPOSALS_DOC_COUNT = "getProposalDocumentCount";
	public static final String GET_PROPOSAL_SUMMARY_STATUS_DETAILS = "getProposalSummaryStatusDetails";
	public static final String GET_ELEMENT_ID = "getElementID";
	public static final String AO_USER_MAP = "aoUserMap";
	public static final String FETCH_PERMITTED_USERS = "fetchPermittedUsers";
	public static final String FETCH_PROPOSAL_DOCS = "fetchProposalDocuments";
	public static final String PROCESS_STATUS_ID = "processStatusId";
	public static final String UPDATE_PROPOSAL_TASK_STATUS = "updateProposalTaskStatus";
	public static final String UPDATE_TASK_COMMENTS = "updateTaskComments";
	public static final String INSERT_TASK_COMMENTS = "insertTaskComments";
	public static final String FETCH_REQ_OPTIONAL_DOCS = "fetchRequiredOptionalDocuments";
	public static final String GET_PROPOSAL_DETAILS = "getProposalDetails";
	public static final String PROC_STATUS_ID = "procStatusId";
	public static final String INS_NEW_PROPOSAL_DETAILS = "insertNewProposalDetails";
	public static final String GET_PROPOSAL_ID = "getProposalId";
	public static final String GET_PROPOSAL_TITLE = "getProposalTitle";
	public static final String FETCH_PROPOSAL_TASK_DETAILS = "fetchProposalDetailsForTask";
	public static final String FETCH_REQUIRED_QUESTION_DOCUMENT_COUNT = "fetchRequiredQuestionDocumentCount";
	public static final String AS_SORT_SITE_TABLE = "asSortSiteTable";
	public static final String FETCH_PROPOSAL_SITE_DETAILS = "fetchProposalSiteDetails";
	public static final String FETCH_SUB_BUDGET_SITE_DETAILS = "fetchSubBudgetSiteDetails";
	public static final String UPDATE_PROPOSAL_DOC_STATUS_TASK = "updateProposalDocumentStatusForTask";
	public static final String LO_VIEW_PROGRESS_MAP = "loViewProgressMap";
	public static final String VIEW_APT_DETAILS = "viewAptDetails";
	public static final String LO_VIEW_APT_BEAN = "loViewAptBean";
	public static final String VIEW_APT_PROGRESS = "viewAptProgress";
	public static final String AO_INPUT_PARAMS = "aoInputParams";
	public static final String UPDATE_PROPERTY = "aoUpdateProperties";
	public static final String GET_APP_PROV_DETAIL_PROPOSAL_QUERY = "getApprovedProviderDetailForProposal";
	public static final String UPDATE_APP_PROV_STATUS_QUERY = "updateApprovedProviderStatus";
	public static final String UPDATE_PROPOSAL_DOC_STATUS = "updateProposalDocumentStatus";

	// Proposal Evaluation Controller Starts
	public static final String EVALUATION_SETTINGS = "evaluationsettings";
	public static final String EVALUATOR = "Evaluator";
	public static final String TEMP_EVALUATOR = "TempEvaluator";
	public static final String CANCEL_PROPOSAL_LOWERCASE = "cancelproposal";
	public static final String CANCEL_PROPOSAL_ID = "cancelProposalId";
	public static final String RETRACT_PROPOSAL = "retractproposal";
	public static final String RETRACT_PROPOSAL_ID = "retractProposalId";
	public static final String RETRACT_PROC_ID = "retractProcurementId";
	public static final String PROPOSAL_ID_ONE = "proposalId1";
	public static final String STATUS_FLAG = "aoStatusFlag";
	public static final String EVALUATION_BEAN_LOWERCASE = "evaluationBean";
	public static final String FETCH_EVAL_RESULTS_SEL = "fetchEvaluationResultsSelections";
	public static final String EVAL_RES_COUNT = "evaluationResultsCount";
	public static final String AWARD_REVIEW_STATUS = "awardReviewStatus";
	public static final String EVAL_RESULT_LIST = "evaluationResultList";
	public static final String FETCH_REQ_PROPOSALS_DETAILS = "fetchReqProposalDetails";
	public static final String LO_PROPOSAL_DETAILS = "loProposalDetails";
	public static final String AS_AWARD_ID = "asAwardId";
	public static final String FETCH_REVIEW_AWARD_COMMENTS = "fetchReviewAwardComments";
	public static final String LO_VIEW_AWARD_COMMENT = "loViewAwardComment";
	public static final String AWARD_REVIEW_DATE = "awardReviewDate";
	public static final String LS_AWARD_AMOUNT = "lsAwardAmount";
	public static final String NO_OF_AWARDS = "numberOfAwards";
	public static final String NO_OF_PROVIDERS_LOWERCASE = "numberOfProviders";

	public static final String LO_EVAL_BEAN = "loEvalBean";
	public static final String FETCH_EVAL_RESUTS = "fetchEvaluationResults";
	public static final String VIEW_RESPONSE_BASE_LOWERCASE = "viewResponseBase";
	public static final String INSERT_PROPOSAL_DOCS_DETAILS = "insertProposalDocumentDetails";

	public static final String CI_FETCH_CONTRACT_INVOICE_SUMMARY = "fetchContractInvoiceSummary";
	public static final String CI_FETCH_CONTRACT_INVOICE_INFORMATION = "fetchContractInvoiceInformation";
	public static final String CI_INVOICE_LIST = "loInvoiceList";
	public static final String INVOICE_INFO = "invoiceInfo";

	// Add for R3.7.0 Enhencement #
	public static final String CI_FETCH_CONTRACT_INVOICE_INFO_LIST = "fetchContractInvoiceInfoList";
	public static final String INBOUND_PARAM_INVOICE_ID_LIST = "invoiceIds";

	// ProposalEvaluationController constatnts
	public static final String PROCUREMENT_TITLE = "procurementTitle";
	public static final String FINAL_PROPOSAL_DOC_LIST = "finalProposalDocumentList";
	public static final String EVALUATION_STATUS_ID = "evaluationStatusId";
	public static final String DISPLAY_EVALUATION_SUMMARY = "displayEvaluationSummary";
	public static final String LOCAL_EVAL_CRITERIA_LIST = "loEvalCriteriaList";
	public static final String LOCAL_EVAL_COMMENTS_LIST = "loEvaluatorCommentsList";
	public static final String LOCAL_EVAL_SCORES_LIST = "loEvaluatorScoreList";
	public static final String LOCAL_ACCO_COMMENTS_LIST = "loAccoComments";
	public static final String EVAL_CRITERIA_LIST = "evalCriteriaList";
	public static final String EVAL_COMMENT_LIST = "evalCommentsList";
	public static final String EVAL_SCORES_LIST = "evaluatorScoreList";
	public static final String EVAL_STATUS_LIST = "evaluatorStatusList";
	public static final String LOCAL_ACCO_COMMENT_AND_TITLE = "loAccoCommentsAndTitle";
	public static final String FETCH_PROCUREMENT_TITLE = "fetchProcurementTitle";
	public static final String FETCH_EVALUATION_CRITERIA_DETAIL = "fetchEvaluationCriteriaDetails";
	public static final String FETCH_ACCO_COMMENTS = "fetchAccoComments";
	public static final String FETCH_EVALUATOR_COMMENTS_DETAILS = "fetchEvaluatorCommentsDetails";
	public static final String FETCH_EVALUATOR_REVIEW_COMMENTS_DETAILS = "fetchEvaluatorReviewCommentsDetails";
	public static final String AS_PROCUREMENT_TITLE = "asProcurementTitle";

	// fetchEvaluationScoresDetails
	public static final String GET_PROPOSAL_DOCUMENTS = "getProposalDocuments";

	public static final String MSG_KEY_INCOME_MORE_THAN_REMAINING = "programIncomeMoreThanRemaining";

	public static final String SEVEN = "7";

	public static final String FETCH_CURRENT_INVOICE_STATUS = "fetchCurrInvoiceStatus";
	public static final String INV_STATUS_CANCEL = "INV_STATUS_CANCEL";
	public static final String INV_STATUS_SUSPEND = "INV_STATUS_SUSPEND";
	public static final String INV_STATUS_CLOSED = "INV_STATUS_CLOSED";
	public static final String FINANCIAL_ORG_TYPE = "FinancialOrgType";
	public static final String EVALUATION_RESULT_AND_SELECTION = "EvaluationResultsandSelections";

	// Configure Award Document Task
	public static final String AGENCY_DOCUMENT = "Agency Document";
	public static final String CONFIGURE_AWARD_DOC_TASK = "configureAwardDocumentTaskDetails";
	public static final String REMOVE_STATUS = "removeStatus";
	public static final String REMOVE_AWARD_TASK_DOCS = "removeAwardTaskDocs";
	public static final String DOC_PROPS_MAP = "docPropsMap";
	public static final String INSERT_AWARD_TASK_DOC_DETAILS = "insertAwardTaskDocDetails";
	public static final String RESTRICT_UPLOAD = "RESTRICT_UPLOAD";
	public static final String M08 = "M08";
	public static final String FETCH_AWARD_DOC_TASK_DETAILS = "fetchAwardDocumentTaskDetails";
	public static final String DOCUMENT_TITLE_LOWER_CASE = "documentTitle";

	// Taxonomy Tagging
	public static final String TAXONOMY_TAGGING_ACTIVE_FLAG = "ACTIVE_FLAG";
	public static final String AWARD_APPROVAL_DATE = "AWARD_APPROVAL_DATE";
	public static final String TAXONOMY_TAGGING_KEY = "TaxonomyTagging";
	public static final String SAVE_TAXONOMY_TAGS = "Your changes have been saved successfully.";
	public static final String TAXONOMY_TAGGING_JSP = "taxonomytagging/taxonomytagging";
	public static final String REMOVE_ALL_TAGGING_JSP_IN_BULK = "taxonomytagging/removeAllTaxInBulk";
	public static final String TAXONOMY_TAGGING_PAGE = "taxonomytaggingpage";
	public static final String AO_TAXONOMY_TAGGING_BEAN = "aoTaxonomyTaggingBean";
	public static final String AO_GRID_TAXONOMY_TAGGING_LIST_IN_BULK = "aoGridTaxonomyTaggingListInBulk";
	public static final String NAVIGATE_FROM = "navigatefrom";
	public static final String FETCH_PROC_PROP_DETAIL_TRANS = "fetchProcurementProposalDetails";
	public static final String FETCH_GRID_PROC_PROP_DETAIL_IN_BULK_TRANS = "fetchGridProcurementProposalDetailsInBulk";
	public static final String AO_PROC_PROP_DETAILS = "aoProcurementProposalList";
	public static final String AI_ROW_COUNT = "aiRowCount";
	public static final String PROC_PROP_LIST = "ProcurementProposalList";
	public static final String TAXONOMY_TAGGING_BEAN = "taxonomyTaggingBean";
	public static final String IS_TAGGED = "isTagged";
	public static final String TAXONOMY_XSL_PATH = "/portlet/maintenance/taxonomymaintenance/taxonomy.xsl";
	public static final String LS_MAIN_TREE = "lsMainTree";
	public static final String COM_NYC_HHS_MODEL_TAXONOMY_TAGGING_BEAN = "com.nyc.hhs.model.TaxonomyTaggingBean";
	public static final String GET_TAXONOMY_TAGGING_LIST = "getTaxonomyTaggingList";
	public static final String GET_GRID_TAXONOMY_TAGGING_IN_BULK_LIST = "getGridTaxonomyTaggingInBulkList";
	public static final String GET_TAXONOMY_TAGGING_IN_BULK_LIST = "getTaxonomyTaggingInBulkList";
	public static final String DELETE_FROM_TAXONOMY_TAGGING = "deleteFromTaxonomyTagging";
	public static final String DELETE_FROM_TAXONOMY_TAGGING_IN_BULK = "deleteFromTaxonomyTaggingInBulk";
	public static final String DELETE_FROM_TAXONOMY_TAGGING_MODIFIERS = "deleteFromTaxonomyTaggingModifiers";
	public static final String DELETE_FROM_TAXONOMY_TAGGING_MODIFIERS_IN_BULK = "deleteFromTaxonomyTaggingModifiersInBulk";
	public static final String REMOVE_ALL_FROM_TAXONOMY_TAGGING_MODIFIERS_IN_BULK = "removeAllFromTaxonomyTaggingModifiersInBulk";
	public static final String GET_TAXONOMY_TAGGING_IDS = "getTaxonomyTaggingIds";
	public static final String REMOVE_ALL_FROM_TAXONOMY_TAGGING_IN_BULK = "removeAllFromTaxonomyTaggingInBulk";
	public static final String UPDATE_TAXONOMYTAGGING_DETAILS = "updateTaxonomyTaggingDetails";
	public static final String UPDATE_TAXONOMYTAGGING_DETAILS_IN_BULK = "updateTaxonomyTaggingDetailsInBulk";
	public static final String TAG_ALL_SELECTED_PROPOSAL_TAXONOMY_TAGGING = "tagAllProposalInBulkForTaxonomyTagging";
	public static final String TAG_ALL_SELECTED_PROPOSAL_TAXONOMY_MODIFIER = "tagAllProposalInBulkForTaxonomyModifer";
	public static final String GET_NEXT_TAGGING_ID = "getNextTaggingId";
	public static final String INSERT_TAXONOMY_TAGGING_DETAILS = "insertTaxonomyTaggingDetails";
	public static final String INSERT_TAXONOMY_TAGGING_DETAILS_CONTRACT = "insertTaxonomyTaggingDetailsContract";
	public static final String INSERT_TAXONOMY_TAGGING_DETAILS_IN_BULK_PROPOSAL = "insertTaxonomyTaggingDetailsInBulkProposal";
	public static final String UPDATE_TAXONOMY_MODIFIER_DETAIL = "updateTaxonomyModifierDetails";
	public static final String INSERT_TAXONOMY_MODIFIER_DETAIL = "insertTaxonomyModifierDetails";
	public static final String INSERT_TAXONOMY_MODIFIER_DETAIL_IN_BULK = "insertTaxonomyModifierDetailsInBulk";
	public static final String SELECT_TAXONOMY_MODIFIER_DETAIL = "selectTaxonomyModifierDetails";
	public static final String COM_NYC_HHS_MODEL_TAXONOMY_MODIFIERS_BEAN = "com.nyc.hhs.model.TaxonomyModifiersBean";
	public static final String SELECTED_PROCUREMENT_RECORD_COUNT = "selectProcurementRecordCount";

	// Taxonomy Tagging Ends

	public static final String NOT_VISIBLE = "notvisible";
	public static final String COLON = ":";
	public static final String FINISH_INVOICING_REVIEW_TASK = "finishInvoicingReviewTask";
	public static final String FINISH_INVOICING_REVIEW_TASK_ERROR_CHECK = "invoicingReviewTaskErrorCheck";
	public static final String FETCH_INVOICE_AMOUNT_ASSIGNMENT = "fetchInvoiceAmountAssignment";
	public static final String INV_ASSIGNMENT_AMOUNT_ERROR_MSG = "assignmentAmountErrorMsg";
	public static final String INV_CONTRACT_STATUS_SUSPENDED_MSG = "contactStatusSuspendedMsg";
	public static final String IS_FETCH_CONTRACT_STATUS = "fetchContractStatus";
	public static final String FETCH_AGENCY_ID = "getAgencyId";
	public static final String FETCH_AGENCY_ID_FRM_CONTRACT = "getAgencyIdByContractForWF";

	// Contract Budget Invoicing (CBI) module
	public static final String CBI_FETCH_REMAINING_AMOUNT = "fetchInvoiceRateRemainingAmt";
	public static final String CBI_FETCH_REMAINING_AMOUNT_NEGATIVE = "fetchInvoiceRateRemainingAmtNegative";
	public static final String CBI_FETCH_INVOICED_AMOUNT = "fetchInvoiceRateInvoiceAmt";
	public static final String CBI_FETCH_VALIDATION_AMOUNT = "fetchRateValidationRemAmt";

	// Agency Settings Module (AS)
	public static final String FETCH_AGENCY_NAMES = "fetchAgencyNames";
	public static final String AS_FETCH_REVIEW_PROCESS = "fetchReviewProcess";
	public static final String AS_FETCH_REVIEW_LEVELS = "fetchReviewLevels";
	public static final String AS_INSERT_REVIEW_LEVELS = "insertReviewLevels";
	public static final String AS_UPDATE_REVIEW_LEVELS = "updateReviewLevels";
	public static final String AS_FETCH_AGENCY_USER_NAMES = "fetchAgencyUserNames";
	public static final String AS_FETCH_ASSIGNED_USER_NAMES = "fetchAssgndUserNames";
	public static final String AS_INSERT_LEVEL_USERS = "insertLevelUsers";
	public static final String AS_DELETE_LEVEL_USERS = "deleteLevelUsers";
	public static final String AS_DELETE_LEVEL_USERS_VIA_ACCELERATOR = "deleteLevelUsersViaAccelerator";
	public static final String AS_FETCH_LEVEL1_USERS_COF_TASK = "fetchLevel1UsersIfCoFTask";
	public static final String AS_SET_REVIEW_LEVELS = "setReviewLevels";
	public static final String AS_AGENCY_SETTING_BEAN_FILE_PATH = "com.nyc.hhs.model.AgencySettingsBean";
	public static final String AS_OPEN_TASK_COUNT = "openedTaskCount";
	public static final String AS_INSERT_LIST = "insertList";
	public static final String AS_DELETE_LIST = "deleteList";
	public static final String AS_LEVEL = "level";
	public static final String AS_ASSIGNED_LIST = "AssgndList";
	public static final String AS_GET_ALL_LEV1_USER_LIST = "getAllLevel1UsersList";
	public static final String AS_GET_ALL_LEV2_USER_LIST = "getAllLevel2UsersList";
	public static final String AS_GET_ALL_LEV3_USER_LIST = "getAllLevel3UsersList";
	public static final String AS_GET_ALL_LEV4_USER_LIST = "getAllLevel4UsersList";
	public static final String AS_AGENCY_LOGIN = "agencylogin";

    /*[Start]   QC 9283 R 7.10.0 error StringIndexOutOfBoundsException */
    public static final String AS_AGENCY_PROVIDER_LOOK_UP = "agencyProviderLookUp";
    /*[End]      QC 9283 R 7.10.0 error StringIndexOutOfBoundsException */

   	public static final String AS_AGENCY_SETTING_LOGIN = "agencysettinglogin";
	public static final String AS_AGENCY_SETTINGS = "agencySettings";
	public static final String AS_CITY_AGENCY_SETTINGS = "cityagencysettings";
	public static final String AS_FAILED = "failed";
	public static final String AS_PASSED = "passed";
	public static final String AS_FAILURE = "failure#";
	public static final String AS_SUCCESS = "success#";
	public static final String AS_AGENCY_SET_LEVEL_USER = "agencySetLevelUsers";
	public static final String AS_LEVEL_OF_REVIEW_ASSGND = "levelOfReviewAssigned";
	public static final String AS_AI_REVIEW_PROCESS_ID = "aiReviewProcessId";
	public static final String AS_CONFIG_FLAG = "asConfigFlag";
	public static final String AS_HIDDEN_REVIEW_PROCESS_ID = "hdnReviewProcessId";
	public static final String AS_HIDDEN_CONFIG_FLAG = "hdnConfigFlag";
	public static final String AS_GET_AGENCY_SET_ASSGND_USR_DATA = "getAgencySetAssgndUsrData";
	public static final String AS_AI_LEVEL_OF_REVIEW_ASSGND = "aiLevelOfReviewAssigned";
	public static final String AS_AO_OLD_AGENCYSETTINGSBEAN = "aoOldAgencySettingsBean";
	public static final String AS_AO_NEW_AGENCYSETTINGSBEAN = "aoNewAgencySettingsBean";
	public static final String AS_SET_AGENCY_LEVEL_USERS = "setAgencyLevelUsers";
	public static final String AS_AGENCY_SETTINGS_BEAN = "AgencySettingsBean";
	public static final String AS_AGENCY_SETTING_BEAN = "agencySettingsBean";
	public static final String AS_GET_AGENCY_REVIEW_PROCESS_DATA = "getAgencyAndReviewProcessData";
	public static final String AS_AGENCY_SETTING_BEAN_OBJ = "aoAgencySettingsBean";
	public static final String AS_HIDDEN_ALL_USERS = "hdnAllUsers";
	public static final String AS_HIDDEN_LEV1_USERS = "hdnLev1Users";
	public static final String AS_HIDDEN_LEV2_USERS = "hdnLev2Users";
	public static final String AS_HIDDEN_LEV3_USERS = "hdnLev3Users";
	public static final String AS_HIDDEN_LEV4_USERS = "hdnLev4Users";
	public static final String AS_HIDDEN_AGENCY_ID = "hdnAgencyId";
	public static final String AS_HIDDEN_REVIEW_PROC_ID = "hdnReviewProcId";
	public static final String AS_NON_CONFIG_TASK = "nonConfigTask";
	public static final String AS_REVIEW_LEVEL = "reviewLevel::";
	public static final String AS_AO_SAVE_REVIEW_LEVEL_RETURNED_MAP = "aoSaveReviewLevRetrndMap";

	public static final String EVALUATION = "EvaluationStatus";
	public static final String SCORE_RETURNED_KEY = "asScoreReturned";
	public static final String SCORE_RETURNED = "scoreReturned";
	public static final String STATUS_MAP_KEY = "loStatusMap";
	public static final String STATUS_MAP = "StatusMap";
	public static final String CONFIRM_RETURN_FOR_ACTION = "confirmReturnForAction";
	public static final String RETURN_STATUS = "returnStatus";
	public static final String UPDATE_RETURN_FOR_REVISION = "updateReturnForRevision";
	public static final String UPDATE_DOC_RETURN_FOR_REVISION = "updateDocReturnForRevision";
	public static final String UPDATE_EVALUATION_STATUS_RETURNED = "updateEvaluationStatusReturned";
	public static final String REQUIRED_PROPOSAL_DOC_LIST = "requiredProposalDocumentList";
	
	/* QC9710 */
	public static final String UPDATE_INVOICE_ADVANCE_MODIFIED_DATE = "UpdateInvoiceAdvanceModifiedDate";	
	
	/* QC9721 */
	public static final String UPDATE_INVOICE_DETAILS_MODIFIED_DATE = "UpdateInvoiceDetailsModifiedDate";

	public static final String CBY_SET_STATUS_FOR_INVOICE_REVIEW_TASK = "setStatusForInvoiceReviewTask";
	public static final String FETCH_CURRENT_ASSIGNMENT_STATUS = "fetchCurrentAssignmentStatus";
	public static final String ASSIGNMENT_STATUS = "assignmentStatus";
	public static final String INV_ASSIGNMENT_VALIDATION = "INV_ASSIGNMENT_VALIDATION";
	public static final String CI_FETCH_ASSIGNMENT_SUMMARY = "contractInvAssignmentSummary";
	public static final String INPUT_PARAM_CLASS_ASSIGNMENT_BEAN = "com.nyc.hhs.model.AssignmentsSummaryBean";
	public static final String CI_EDIT_ASSIGNMENT = "editAssignment";
	public static final String CI_DEL_ASSIGNMENT = "delAssignment";
	public static final String ENABLE_SEND_EVAL_TASK_BUTTON = "enableSendEvalTaskButton";
	public static final String SHOW_SEND_EVAL_TASK_BUTTON = "showSendEvalTaskButton";
	public static final String CHECK_SEND_EVAL_TASK_BUTTON = "checkForSendEvalTaskButton";

	public static final String CI_SAVED = "CI_SAVED";
	public static final String FETCH_AWARD_STATUS_ID = "fetchAwardStatusId";
	public static final String AWARD_STATUS_ID = "awardStatusId";
	public static final String PROCUREMENT_INFO_MAP = "procurementInfoMap";

	// pagination
	public static final String AWARD_CONTRACT_HANDLER = "awardContractHandler";
	public static final String AWARD_CONTRACT_COUNT = "awardAndContractsCount";
	public static final String AWARD_COUNT = "awardCount";
	public static final String START_NODE = "startNode";
	public static final String END_NODE = "endNode";
	public static final String AWARD_LIST_COUNT = "AwardList";

	public static final String CBY_FETCH_SALRIED_EMPLOYEE_FOR_MODIFICATION = "fetchSalriedEmployeeForModification";
	public static final String CBY_INSERT_FIRST_PERSONNEL_SERVICES_FOR_MODIFICATION = "insertFirstPersonnelServicesForModification";
	public static final String CBY_UPDATE_PERSONNEL_SERVICES_FOR_MODIFICATION = "updatePersonnelServicesForModification";
	public static final String CBY_INSERT_PERSONNEL_SERVICES_MODIFICATION = "insertPersonnelServicesModification";
	public static final String CBY_FETCH_HOURLY_EMPLOYEE_FOR_MODIFICATION = "fetchHourlyEmployeeForModification";
	public static final String CBY_FETCH_SEASONAL_EMPLOYEE_FOR_MODIFICATION = "fetchSeasonalEmployeeForModification";
	public static final String PERSONNEL_SERVICES_MODIFICATION_JSP_NAME = "personnelServicesModification";
	public static final String CBY_FETCH_FRINGE_BENEFITS_FOR_MODIFICATION = "fetchFringeBenefitsForModification";
	public static final String CBY_UPDATE_FRINGE_BENIFITS_FOR_MODIFICATION = "updateFringeBenifitsForModification";
	public static final String CBY_INSERT_FIRST_FRINGE_BENEFITS_FOR_MODIFICATION = "insertFirstFringeBenefitsForModification";
	public static final String FINALIZE_VISIBILTY_STATUS = "finalizeVisibiltyStatus";
	public static final String UPDATE_VISIBILTY_STATUS = "updateVisibiltyStatus";
	public static final String GET_AWARD_REVIEW_STATUS_ID = "getAwardReviewStatusId";

	public static final String COM_NYC_HHS_MODEL_EVALUATION_BEAN = "com.nyc.hhs.model.EvaluationBean";
	public static final String ERROR_WHILE_FETCHING_PROPOSAL_DETAILS = "Error occured while fetching required proposal details";
	public static final String ERROR_WHILE_CONFIRMING_PROPOSAL_SELECTED = "Exception Occured while confirming a proposal selected";
	public static final String ERROR_WHILE_CONFIRMING_PROPOSAL_NOT_SELECTED = "Exception Occured while confirming a proposal not-selected";

	public static final String ORGANIZATION_ID_KEY = "ORGANIZATION_ID";

	public static final String INV_FETCH_FY_BUDGET_SUMMARY = "fetchInvFyBudgetSummary";
	public static final String INV_FETCH_FY_BUDGET_ACTUAL_PAID = "fetchInvFyBudgetActualPaid";
	public static final String CI_INVOICE_TABLE_INFO = "InvoiceTableInfo";

	public static final Map<String, String> FINANCIAL_WF_NAME_TYPE_MAP = new HashMap<String, String>();
	static
	{
		FINANCIAL_WF_NAME_TYPE_MAP.put(WF_PROCUREMENT_CERTIFICATION_FUND, TASK_PROCUREMENT_COF);
		FINANCIAL_WF_NAME_TYPE_MAP.put(WF_CONTRACT_CONFIGURATION_UPDATE, TASK_CONTRACT_UPDATE);
		FINANCIAL_WF_NAME_TYPE_MAP.put(WF_CONTRACT_CONFIGURATION, TASK_CONTRACT_CONFIGURATION);
		FINANCIAL_WF_NAME_TYPE_MAP.put(WF_CONTRACT_CERTIFICATION_FUND, TASK_CONTRACT_COF);
		FINANCIAL_WF_NAME_TYPE_MAP.put(WF_ADVANCE_REVIEW, TASK_ADVANCE_REVIEW);
		FINANCIAL_WF_NAME_TYPE_MAP.put(WF_NEW_FY_CONFIGURATION, TASK_NEW_FY_CONFIGURATION);
		FINANCIAL_WF_NAME_TYPE_MAP.put(WF_AMENDMENT_CONFIGURATION, TASK_AMENDMENT_CONFIGURATION);
		FINANCIAL_WF_NAME_TYPE_MAP.put(WF_CONTRACT_BUDGET_REVIEW, TASK_BUDGET_REVIEW);
		FINANCIAL_WF_NAME_TYPE_MAP.put(WF_CONTRACT_BUDGET_UPDATE_REVIEW, TASK_BUDGET_UPDATE);
		FINANCIAL_WF_NAME_TYPE_MAP.put(WF_CONTRACT_BUDGET_MODIFICATION, TASK_BUDGET_MODIFICATION);
		FINANCIAL_WF_NAME_TYPE_MAP.put(WF_CONTRACT_BUDGET_AMENDMENT, TASK_BUDGET_AMENDMENT);
		FINANCIAL_WF_NAME_TYPE_MAP.put(WF_INVOICE_REVIEW, TASK_INVOICE_REVIEW);
		FINANCIAL_WF_NAME_TYPE_MAP.put(WF_PAYMENT_REVIEW, TASK_PAYMENT_REVIEW);
		FINANCIAL_WF_NAME_TYPE_MAP.put(WF_ADVANCE_PAYMENT_REVIEW, TASK_ADVANCE_PAYMENT_REVIEW);
		FINANCIAL_WF_NAME_TYPE_MAP.put(WF_AMENDMENT_CERTIFICATION_FUND, TASK_AMENDMENT_COF);
		//Added in R6: return payment review task
		FINANCIAL_WF_NAME_TYPE_MAP.put(WF_RETURNED_PAYMENT_REVIEW, TASK_RETURN_PAYMENT_REVIEW);
	}
	
	public static final List<String> FINANCIAL_WF_NAME_TYPE_MAP_DEFAULT_ASSIGNMENT = new ArrayList<String>();
	static
	{
		FINANCIAL_WF_NAME_TYPE_MAP_DEFAULT_ASSIGNMENT.add(WF_CONTRACT_BUDGET_REVIEW);
		FINANCIAL_WF_NAME_TYPE_MAP_DEFAULT_ASSIGNMENT.add(WF_CONTRACT_BUDGET_UPDATE_REVIEW);
		FINANCIAL_WF_NAME_TYPE_MAP_DEFAULT_ASSIGNMENT.add(WF_CONTRACT_BUDGET_MODIFICATION);
		FINANCIAL_WF_NAME_TYPE_MAP_DEFAULT_ASSIGNMENT.add(WF_CONTRACT_BUDGET_AMENDMENT);
		FINANCIAL_WF_NAME_TYPE_MAP_DEFAULT_ASSIGNMENT.add(WF_INVOICE_REVIEW);
	}
	
	public static final String PROC_USER = "procUser";
	public static final String NEW_ENTITY_ID = "newEntityId";
	public static final String NEW_ENTITY_TYPE = "newEntityType";
	public static final String SUB_ENTITY_TYPE = "subEntityType";
	public static final String AUDIT_TASK_INTERNAL_COMMENTS = "Internal Comments";
	public static final String AUDIT_TASK_PUBLIC_COMMENTS = "Public Comments";
	public static final String PROVIDER_EVENT_NAMES = "'Status Change','Provider Comments'";
	public static final String ENTITY_TYPE_FOR_AGENCY = "entityTypeForAgency";
	public static final String EVENT_NAME_FOR_AGENCY = "eventNameForAgency";
	public static final String AUDIT_CONTRACT_BUDGET_MODIFICATION = "Contract Budget Modification";
	public static final String AUDIT_CONTRACT_BUDGET_AMENDMENT = "Contract Budget Amendment";
	public static final String AUDIT_CONTRACT_BUDGET_UPDATE = "Contract Budget Update";
	public static final String AUDIT_INVOICES = "Invoices";
	public static final String AUDIT_PROVIDER_INITIATED_TASK = "providerInitiatedTask";

	public static final String NO_EPIN_MATCH = "noEpinMatch";
	public static final String PROPOSAL_SELECTED_STATUS_ID = "proposalSelectedStatusId";

	public static final String CBM_LESS_THAN_INVOICE_FOR_NEG_AMEND = "lessThanInvoiceForNegAmend";
	public static final String CBM_LESS_THAN_INVOICE_INCLUDING_PENDING_FOR_NEG_AMEND = "lessThanInvoiceIncludingPendingNegativeForNegAmend";
	public static final String CBM_LESS_THAN_INVOICE_INCLUDING_PENDING_NEG_AMEND_ERROR_MESSAGE = "lessThanInvoiceIncludingPendingNegative";
	public static final String CBM_ADD_PROF_SERVICES_MODIFICATION_AMOUNT = "addProfServicesModificationAmount";
	public static final String CBM_UPDATE_PROF_SERVICES_MODIFICATION_AMOUNT = "updateProfServicesModificationAmount";
	public static final String CBM_FETCH_PROF_SERVICES_DETAILS = "cbmFetchProfServicesDetails";

	// [Start] R7.5.0 QC9146 Professional Service Grid issue for MOD
    public static final String CBM_MERGE_PROF_SERVICES_MODIFICATION_AMOUNT =  "mergeProfServicesModAmount";
    public static final String AMENDMENT_MERGE_PROF_SERVICES_AMOUNT = "mergeProfServicesAmendmentAmount";
    // [End] R7.5.0 QC9146 Professional Service Grid issue for MOD
	
	public static final String AMENDMENT_FETCH_PROF_SERVICES_DETAILS = "fetchProfServicesDetailsAmendment";
	public static final String AMENDMENT_ADD_PROF_SERVICES_AMOUNT = "addProfServicesAmendmentAmount";
	public static final String AMENDMENT_UPDATE_PROF_SERVICES_AMOUNT = "updateProfServicesAmendmentAmount";

	public static final String IC_INVOICE_MAP = "aoInvoiceMap";
	public static final String IC_SAVE_INVOICE_REVIEW_TASK_DETAIL = "saveInvoiceReviewTaskDetail";
	public static final String IS_INSERT_AGENCY_INVOICE_NUMBER = "insertAgencyInvoiceNumber";

	// Awards and Contracts
	public static final String NOT_ASSIGNED = "Not Assigned";

	public static final String MODIFICATION_UPDATE_RENT = "updateModificationRent";

	public static final String STAR = "*";
	public static final String DOUBLE_STAR = "**";
	public static final String SHOW_COMMENTS_VIS_STATUS = "showCommentVisibiltyStatus";
	public static final String NA = "NA";
	public static final String EVALUATION_RESULTS_ROLE_MAPPING = "ProposalStatusToEvaluationActionMapping";
	public static final String EVAL_INTERNAL_TYPE = "Internal";
	public static final String EVAL_EXTERNAL_TYPE = "External";
	public static final String FETCH_INTERNAL_STATUS_ID = "fetchEvaluationIntStatusId";
	public static final String FETCH_EXTERNAL_STATUS_ID = "fetchEvaluationExtStatusId";

	// Award Approval Task
	public static final String REASSIGN_STATUS = "reassignStatus";
	// InvoiceAddAssignee
	public static final String S431_REQUEST_PARAM_KEY_VENDOR = "txtVendorname";

	public static final String S431_CHANNEL_VENDOR = "lsVendorId";

	public static final String INSERT_ASSIGNEE_FOR_BUDGET = "addAssigneeForBudget";

	public static final String FETCH_ORGANIZATION_INFO_FOR_ASSIGNMENT = "fetchOrganizationInfoforAssignment";

	public static final String COA_SUM_NOT_EQUAL_TOTAL_PAYMENT_AMOUNT = "COA_SUM_NOT_EQUAL_TOTAL_PAYMENT_AMOUNT";
	public static final String MSG_KEY_DUPLICATE_ASSIGNEE_NOT_ADDED = "DuplicateAssigneesCannotBeAdded";
	public static final String FETCH_VENDOR_LIST = "fetchVendorList";
	public static final String FETCH_PROVIDER_LIST = "fetchProviderList";

	public static final String S431_CHANNEL_VENDOR_LIST = "vendorList";

	public static final String CHANNEL_PROVIDER_LIST = "providerList";
	public static final String VALIDATE_ASSIGNEE = "validateAssignee";

	public static final String INSERT_ASSIGNEE = "addAssignment";

	public static final String FETCH_ORGANIZATION_ACTIVE_FLAG_BY_ID = "fetchOrganizationActiveFlagById";

	public static final String S431_VENDOR_STATUS_KEY = "vendorStatus";

	public static final String S431_CHANNEL_KEY_LB_VALID = "lbValid";

	public static final String CONTRACT_CONFIG_ACTUAL_LIST = "ContractConfigActualList";
	public static final String CONTRACT_CONFIG_UPDATE_LIST = "ContractConfigUpdateList";
	public static final String LIST_NAME = "listName";
	public static final String ACTUAL_GRID = "ActualGrid";
	public static final String UPDATE_GRID = "UpdateGrid";
	public static final String VALIDATION_SCREEN_NAME = "validationScreen";
	public static final String CONTRACT_CONFIG_UPDATE = "ContractConfigUpdate";
	public static final String NEW_RECORD_CONTRACT_SERVICES = "_newrecord_contractServices";
	public static final String NEW_RECORD = "_newrecord";
	public static final String CURRENT_REC = "_current";
	public static final String ACTUAL_REC = "_actual";
	public static final String NEW_RECORD_RENT = "_newrecord_rent";
	public static final String JSP_PATH = "jspPath";
	public static final String XPATH_NAME_SERVICE_FUNCTION = "//element[((@name=\"Service Area\" or @name=\"Function\"))]";
	public static final String XPATH_NAME_NOT_SERVICE_FUNCTION = "//element[((@name!=\"Service Area\" and @name!=\"Function\" and @parentid=\"root\"))]";
	public static final String OLD_EVALUATOR_ID = "oldEvaluatorId";
	public static final String UPDATE_EVAL_INT_AFTER_EVAL = "updateEvaluatorInternalAfterEvaluation";
	public static final String UPDATE_EVAL_EXT_AFTER_EVAL = "updateEvaluatorExternalAfterEvaluation";
	public static final String INTERNAL_REMOVED = "internalRemoved";
	public static final String EXTERNAL_REMOVED = "externalRemoved";
	public static final String DELETE_EVAL_EXT_AFTER_EVAL = "deleteEvaluatorExternalAfterEvaluation";
	public static final String AFTER_EVAL_LESS_COUNT = "afterEvaluationLessCount";
	public static final String FIND_EVAL_TASK_SEND = "findEvaluationTaskSent";
	public static final String GET_EVAL_COUNT = "getEvaluationCount";
	public static final String GET_INT_EVAL_LIST = "getInternalEvaluationsList";
	public static final String GET_EXT_EVAL_LIST = "getExternalEvaluationsList";
	public static final String EVAL_COUNT_NEW = "aoEvaluatorsCountNew";
	public static final String UPDATE_PROC_TABLE = "updateProcurementTable";

	public static final String REQUEST_SCORE_AMENDMENT = "requestScoreAmendment";
	public static final String AMENDMENT_FLAG = "lbAmendmentFlag";
	public static final String ERROR_WHILE_REQUESTING_AMENDMENTS = "Error while requesting score amendments";

	public static final String TOTAL_INVOICE_AMOUNT_MORE_THAN_REMAINING = "totalInvoiceAmountMoreThanRemaining";
	public static final String SCREEN_READ_ONLY = "screenReadOnly";
	public static final String INSERT_AUDIT_STATUS = "insertAuditStatus";
	public static final String SCORE_DETAILS = "scoreDetails";

	public static final String FETCH_CM_SUBBUDGET_SUMMARY = "fetchCMSubBudgetSummary";
	public static final String GET_CONTRACT_BUDGET_MODIFICATION = "getContractBudgetModification";
	public static final String CBF_FETCH_CONTRACT_BUDGET_RATE_INFO = "fetchContractBudgetRateInfo";
	public static final String CBF_FETCH_CONTRACT_BUDGET_MODIFICATION_AMOUNT = "fetchContractBudgetModificationAmount";
	public static final String CBF_FETCH_CONTRACT_BUDGET_MODIFICATION_AMOUNT_FOR_AMENDMENT = "fetchContractBudgetModificationAmountForAmendment";

	public static final String CBF_INSERT_CONTRACT_BUDGET_MODIFICATION_RATE_INFO = "insertContractBudgetModificationRateInfo";
	public static final String CBF_UPDATE_CONTRACT_BUDGET_MODIFICATION_RATE_INFO = "updateContractBudgetModificationRateInfo";
	public static final String CBF_DELETE_CONTRACT_BUDGET_MODIFICATION_RATE_INFO = "deleteContractBudgetModificationRateInfo";
	public static final String BUDGET_MODIFICATION_RATE_UNIT_VALIDATION = "BUDGET_MODIFICATION_RATE_UNIT_VALIDATION";
	public static final String BUDGET_MODIFICATION_RATE_AMNT_VALIDATION = "BUDGET_MODIFICATION_RATE_AMNT_VALIDATION";
	public static final String GET_CONTRACT_BUDGET = "getContractBudget";
	public static final String CBF_INSERT_NEW_CONTRACT_BUDGET_MODIFICATION_RATE = "insertNewContractBudgetModificationRate";
	public static final String CBF_GET_UNIT_DESC_CONTRACT_BUDGET_MODIFICATION_RATE = "getUnitDescContractBudgetModificationRate";
	public static final String CBF_GET_FY_BUDGET_CONTRACT_BUDGET_MODIFICATION_RATE = "getFYBudgetContractBudgetModificationRate";
	public static final String CBF_UPDATE_BUDGET_MODIFICATION_RATE_UNIT = "updateBudgetModificationRateUnit";
	public static final String CBF_GET_BASE_UNIT_CONTRACT_BUDGET_MODIFICATION_RATE = "getBaseUnitContractBudgetModificationRate";
	public static final String CBF_GET_BASE_UNIT_CONTRACT_BUDGET_RATE_IN_MULTIPLE_AMENDMENTS = "getBaseUnitContractBudgetRateInMultipleAmendments";
	public static final String NEW_ROW_IDENTIFIER = "new_row";

	public static final String LAUNCH_WF_CONTRACT_MODIFICATION = "launchWFContractModification";
	public static final String BUDGET_MODIFICATION_STATUS_CHANGED_FROM = "Contract Budget Modification Status changed from ";
	public static final String CONTRACT_INVOICE_STATUS_CHANGED_FROM = "Invoice Status changed from ";
	public static final String BUDGET_UPDATE_STATUS_CHANGED_FROM = "Contract Budget Update Status changed from ";

	public static final String UPDATE_PROPOSAL_STATUS_QUERY_ID = "updateProposalStatusAccForEval";

	// for workflow integration with close submissions
	public static final String LAUNCH_WORKFLOW_MAP = "lolaunchWorkflowMap";
	public static final String WORK_FLOW_NAME = "workFlowName";
	public static final String SEND_EVALUATION_TASK_JSP = "sendEvaluationTasks";
	public static final String VIEW_AWARD_COMMENTS = "awardReviewComments";
	public static final String YYYY_MM_DD = "yyyy-MM-dd HH:mm:ss.S";
	public static final String FETCH_BUDGET_COUNT = "fetchBudgetCount";
	public static final String CONTRACT_START_DT = "contractStartDt";
	public static final String CONTRACT_END_DT = "contractEndDT";

	// contract list controller start
	public static final String FETCH_CM_SUB_BUDGET_SUMMARY = "fetchCMSubBudgetSummary";
	public static final String FETCH_CM_SUB_BUDGET_SUMMARY_FOR_AMENDMENT = "fetchCMSubBudgetSummaryForAmendment";
	public static final String FETCH_CM_SUB_BUDGET_PRINT_SUMMARY = "fetchCMSubBudgetPrintSummary";
	public static final String FISCAL_YEAR_COUNTER = "counter";
	public static final Double DOUBLE_HUNDRED = 100.0d;
	public static final String DELIMITER_SEMICOLON = ";";
	public static final String FETCH_MODIFIED_BUDGET_ID = "fetchModifiedBudgetId";
	public static final String AO_MODIFIED_BUDGET_ID = "aoModifiedBudgetId";
	public static final String FETCH_SUB_BUDGET_MODIFICATION_SUMMARY = "fetchSubBudgetModificationSummary";
	public static final String LOAD_MODIFICATION_FIRST = "loadModificationFirst";
	public static final String CONTRACT_BUDGET_BEAN_LIST = "loContractBudgetBeanList";
	public static final String CONFIGURED_FISCAL_YR_LIST = "loConfiguredFiscalYrList";
	public static final String JSP_CONTRACT_CONFIG_READ_ONLY = "contractConfigReadOnly";
	public static final String MAP_KEY_PROC_COF_BEAN = "procurementCOFBean";
	public static final String MAP_KEY_CONF_FISCAL_YR_LIST = "configuredFiscalYrList";
	public static final String LO_PROC_COF_BEAN = "loProcurementCOFBean";
	public static final String RENDER_ACTION_SHOWCONTRACTCONFIGREADONLYDETAILS = "showContractConfigReadOnlyDetails";

	public static final String HIDDEN_HDNCONTRACTID = "hdncontractId";
	public static final String HIDDEN_IS_VIEW_AMENDMENT = "hdnIsViewAmendment";
	public static final String HIDDEN_HDNAMENDCONTRACTID = "hdnAmendContractId";
	public static final String HIDDEN_HDNSTATUSID = "hdnstatusId";
	public static final String HIDDEN_HDNCONTRACTAMT = "hdncontractAmt";
	public static final String HIDDEN_HDNCONTRACTSTARTDT = "hdncontractStartDt";
	public static final String HIDDEN_HDNCONTRACTENDDT = "hdncontractEndDt";
	public static final String HIDDEN_HDNCONTRACTTYPEID = "hdncontractTypeId";

	public static final String IS_VIEW_AMENDMENT = "isViewAmendment";
	public static final String TRANSACTION_GETCONTRACTCONFIGDETAILS = "getContractConfigDetails";
	public static final String CLC_AWARD_EPIN = "awardEpin";
	public static final String CLC_PROCUREMENT_START_DATE = "procurementStartDate";
	public static final String AGENCY_DIVISION = "agencyDivision";
	public static final String PROCUREMENT_METHOD = "procurementMethod";
	public static final String APT_PROJECT = "aptProject";
	public static final String ACC_PROGRAM_NAME = "accProgramName";
	public static final String APT_PROCUREMENT_DESC = "aptProcurementDesc";
	public static final String VENDOR_FMS_ID = "vendorFmsId";
	public static final String VENDOR_FMS_NAME = "vendorFmsName";
	public static final String PROVIDER_LEGAL_NAME = "providerLegalName";
	public static final String HDN_CONTRACT_ID = "hdnContractId";
	public static final String AWARD_AGENCY_ID = "awardAgencyId";
	public static final String AS_VENDOR_FMS_ID = "asVendorFmsId";
	public static final String VALIDATE_PROVIDER_ACCELERATOR = "validateProviderInAccelerator";
	public static final String LB_PROVIDER_ACCELEARTOR = "lbProviderInAccelerator";
	public static final String ADD_CONTRACT_DETAILS = "addContractDetails";
	public static final String COMMENT_AREA = "commentArea";
	public static final String VALIDATE_AMEND_CONTRACT = "validateAmendContract";
	public static final String CANCELLING_NEGATIVE_AMENDMENT_CHECK = "cancellingNegativeAmendmentCheck";
	public static final String CANCELLING_NEGATIVE_AMENDMENT_CHECK_ERROR_MESSAGE = "cancellingNegativeAmendmentCheckMessage";
	public static final String CANCELLING_NEGATIVE_AMENDMENT_CHECK_ERROR_MESSAGE_SECOND = "cancellingNegativeAmendmentCheckMessageSecond";
	public static final String AB_STATUS = "abStatus";
	public static final String SELECT_CONTRACT_AMENDMENT_ID = "selectContractAmendmentId";
	public static final String LO_CONTRACT_MAP = "loContractMap";
	public static final String CONTRACT_ID_UNDERSCORE = "CONTRACT_ID";
	public static final String CLC_CONTRACT_AMOUNT = "CONTRACT_AMOUNT";
	public static final String AO_WORKFLOW_TERMINATE_MAP = "aoWorkFlowTerminateMap";
	public static final String AO_BASE_CONTRACT_WF_TERMINATE_MAP = "aoBaseContractWorkFlowTerminateMap";
	public static final String SUCCESS_CANCEL_AMENDMENT_MSG = "successCancelAmendmentMsg";
	public static final String PENDING_REGISTRATION_AMEND_EXISTS = "pendingRegisNotExistForAmend";
	public static final String AO_CONTRACT_BEAN = "aoContractBean";
	public static final String AS_CONTRACT_AMOUNT = "asContractAmount";
	public static final String AS_NEW_CONTRACT_AMOUNT = "asNewTotalContractAmount";
	public static final String NEW_TOTAL_AMOUNT = "newTotalAmount";
	public static final String AS_AMENDMENT_AMOUNT = "asAmendmentAmount";
	public static final String AMEND_VALUE = "amendValue";
	public static final String VALIDATE_CONTRACT_AMEND_BUSINESS_RULE = "validateContractAmendmentBusinessRules";
	public static final String AMENDMENT_REASON = "amendmentReason";
	public static final String AMENDMENT_TITLE = "amendmentTitle";
	public static final String AMEND_CONTRACT_DETAILS = "amendContractDetails";
	public static final String AMEND_CONTRACT_SUCCESS = "amendContractSuccess";

	// contract list controller end

	public static final String UPDATE_EVALUATION_STATUS = "updateEvaluationStatus";
	public static final String AWARD_MODIFIED_DATE = "MODIFIED_DATE";
	public static final String FETCH_AWARD_REVIEW_COMMENTS = "fetchAwardReviewComments";
	public static final String MODIFIED_BY_PROVIDER = "modifyByProvider";
	public static final String MODIFIED_BY_AGENCY = "modifyByAgency";
	public static final String MERGE_BUDGET_MODIFICATION_DOCUMENT = "mergeBudgetModificationDocument";
	public static final String MERGE_BUDGET_UPDATE_DOCUMENT = "mergeBudgetUpdateDocument";
	public static final String MERGE_CONTRACT_UPDATE_DOCUMENT = "mergeContractUpdateDocument";

	public static final String CBY_MOD_SEASONAL_EMPLOYEE_GRID_ADD = "seasonalEmployeeGridModificationAdd";
	public static final String CBY_MOD_HOURLY_EMPLOYEE_GRID_ADD = "hourlyEmployeeGridModificationAdd";
	public static final String CBY_MOD_SALARIED_EMPLOYEE_GRID_ADD = "salariedEmployeeGridModificationAdd";
	public static final String CONTRACT_MODIFIED_DATE = "contractModifiedDate";

	public static final String PERSONNEL_SERVICE_BUDGET = "com.nyc.hhs.model.PersonnelServiceBudget";
	public static final String FETCH_PERSONNEL_AMOUNT = "fetchPersonnelAmount";
	public static final String FETCH_PERSONNEL_FRINGE_AMOUNT = "fetchPersonnelFringeAmount";
	public static final String HHSUTIL_EDITOPTIONS = "editoptions";

	public static final String CBF_GET_BASE_UNIT_PERSONNEL_SERVICE = "getBaseUnitForPersonnelService";
	public static final String GET_PENDING_NEGATIVE_AMENDMENT_UNITS_PS = "getPendingNegAmendmentUnitsPS";
	public static final String GET_PENDING_NEGATIVE_AMENDMENT_UNITS_EQP = "getPendingNegAmendmentUnitsEqp";

	public static final String INT_EVAL_LIST = "loInternalEvaluatorList";
	public static final String EXT_EVAL_LIST = "loExternalEvaluatorList";
	public static final String DEL_EVAL_INT = "deleteEvaluationInternal";
	public static final String DEL_EVAL_EXT = "deleteEvaluationExternal";
	public static final String SAVE_EVAL_INT_DETAIL = "saveInternalEvaluationDetails";
	public static final String EVALUATOR_BEAN = "com.nyc.hhs.model.Evaluator";
	public static final String FROM_HOME_PAGE = "fromHomePage";
	public static final String SAVE_EVALUATOR_EXT_DET = "saveExternalEvaluationDetails";
	public static final String FILTER_CRITERIA = "filterCriteria";
	public static final String RFP_RELEASED_THIRTY_DAYS = "rfpReleased30Days";
	public static final String ELIGIBLE_TO_PROPOSE_UPPERCASE = "Eligible to Propose";
	public static final String RFP_DUE_IN_THIRTY_DAYS = "rfpDueIn30Days";
	public static final String PROPOSAL_IN_DRAFT_STATUS = "proposalInDraftStatus";
	public static final String RFP_WITH_ONE_PROPOSAL_SUBMITTTED = "rfpWith1ProposalSubmitted";
	public static final String RFP_IN_SEL_MADE_STATUS = "rfpInSelectionsMadeStatus";
	public static final String PROCUREMENT_LOWERCASE = "procurement";
	public static final String ERROR_OCCURED_WHILE_SAVING_PROCUREMENT = "Error Occured while saving procurement.";
	public static final String FROM_PROVIDER = "fromProvider";
	public static final String SELECTED_SERVICES_BEAN = "selectedServicesBean";
	public static final String ERROR_OCCURED_WHILE_SAVING_APPROVED_PROVIDER = "Error is Occurred while saving the approoved provider";
	public static final String AO_AUDIT_BEAN = "aoAuditBean";
	public static final String ERROR_OCCURED_WHILE_NAVIGATE = "Error occurred while navigate";
	public static final String BUTTON = "button";
	public static final String PCOF_STATUS_CODE = "pcofStatusCode";
	public static final String CHECK_IF_USER_OF_SAME_AGENCY = "checkIfUserOfSameAgency";
	public static final String CHECK_IF_AWARD_APPROVED = "checkIfAwardApproved";
	public static final String CHECK_IF_AWARD_APPROVED_FOR_EVAL_POOL = "checkIfAwardApprovedForEvalPool";
	public static final String DEL_RFP_ADD_DOC_DETAILS = "deleteRfpAddendumDocumentDetails";
	public static final String FETCH_PROC_CUSTOM_QUE_ANS = "fetchProcurementCustomQuestionAnswer";
	public static final String FETCH_APP_PROVIDERS_LIST_PROVIDER = "fetchApprovedProvidersListProvider";
	public static final String FETCH_PROC_CON_DETAILS_FOR_THREE_CONTRACT = "fetchProcurementCONDetailsForR3Contract";
	public static final String FETCH_PROC_DETAILS_FOR_THREE_CONTRACT = "fetchProcurementDetailsForR3Contract";
	public static final String FETCH_AMENDMENT_DETAILS = "fetchAmendmentConfigurationDetails";
	public static final String FETCH_PROC_CON_DETAILS = "fetchProcurementCONDetails";
	public static final String GET_PROC_SUMMARY_FOR_NAV = "getProcurementSummaryForNav";
	public static final String FETCH_PROC_TITLE_AND_ORG_LIST = "fetchProcTitleAndOrgList";
	public static final String PAGE_READ_ONLY = "pageReadOnly";
	public static final String ON = "on";
	public static final String HHS_AUDIT_BEAN = "hhsAuditBean";
	public static final String AO_RFP_REL_BEAN = "aoRFPReleaseBean";
	public static final String REMOVE_AWARD_DOC = "removeAwardDocument";
	public static final String DEL_ADD_EVALUATION_CRITERIA = "deleteAddendumEvaluationCriteria";

	public static final String EDIT_EQUIPMENT_AMEND_DETAILS = "editEquipmentAmendDetails";
	public static final String EDIT_INSERT_EQUIPMENT_AMEND_DETAILS = "editInsertEquipmentAmendDetails";
	public static final String ADD_EQUIPMENT_AMEND_DETAILS = "addEquipmentAmendDetails";
	public static final String DEL_EQUIPMENT_AMEND_DETAILS = "delEquipmentAmendDetails";
	public static final String FETCH_EQUIPMENT_AMEND_AMT_DETAILS = "fetchEquipmentAmendAmtDetails";

	public static final String OPERATION_AND_SUPPORT_MODIFICATION = "operationAndSupportModification";
	public static final String OPERATION_AND_SUPPORT_UPDATE = "updateOperationAndSupport";
	public static final String OPERATION_AND_SUPPORT_AMENDMENT = "operationAndSupportAmendment";
	public static final String EDIT_EQUIPMENT_MODIFICATION_DETAILS = "editEquipmentModificationDetails";
	public static final String EDIT_INSERT_EQUIPMENT_MODIFICATION_DETAILS = "editInsertEquipmentModificationDetails";
	public static final String ADD_EQUIPMENT_MODIFICATION_DETAILS = "addEquipmentModificationDetails";
	public static final String DEL_EQUIPMENT_MODIFICATION_DETAILS = "delEquipmentModificationDetails";
	public static final String EDIT_OPERATION_AND_SUPPORT_MOD_DETAILS = "editOperationAndSupportModDetails";
	public static final String INSERT_OPERATION_AND_SUPPORT_MOD_DETAILS = "insertOperationAndSupportModDetails";
	public static final String EDIT_OPERATION_AND_SUPPORT_AMEND_DETAILS = "editOperationAndSupportAmendDetails";
	public static final String INSERT_OPERATION_AND_SUPPORT_AMEND_DETAILS = "insertOperationAndSupportAmendDetails";
	public static final String GET_COUNT_FOR_OTPS_MOD = "getCountForOTPSMod";
	public static final String FETCH_EQUIPMENT_MOD_AMT_DETAILS = "fetchEquipmentModAmtDetails";
	public static final String FETCH_EQUIPMENT_AMEND_DETAILS = "fetchEquipmentAmendDetails";
	public static final String FETCH_EQUIPMENT_MOD_DETAILS = "fetchEquipmentModDetails";
	public static final String FETCH_OPERATION_AND_SUPPORT_MOD_AMT_DETAILS = "fetchOperationAndSupportModAmtDetails";
	public static final String FETCH_OPERATION_AND_SUPPORT_MOD_DETAILS = "fetchOperationAndSupportModDetails";
	public static final String FETCH_OPERATION_AND_SUPPORT_MOD_DETAILS_PARENT_EQUAL_SUB = "fetchOperationAndSupportModDetailsParentEqualSub";
	public static final String FETCH_OPERATION_AND_SUPPORT_AMEND_DETAILS = "fetchOperationAndSupportAmendDetails";
	public static final String ACTION_TAG_UNDERSCORE = "actionTag_";
	public static final String EVALUATOR_LIST = "evaluatorList";
	public static final String BMC_NEW_FY_PROPERTY_IDENTIFIER = "newFYConfigurationSubBudgetGrid";
	public static final String PROPOSAL_STATUS_RESULT_COUNT = "getProposalStatusIdCount";
	public static final String KEY_SESSION_INVOICE_IS_SUBMITTED = "isSubmitted";
	public static final String EVALUATION_RESULT_JSP = "evaluation/evaluationresultsandselections";
	public static final String FETCH_AWARD_REVIEW_STATUS = "fetchAwardReviewStatus";
	public static final String FETCH_PROPOSAL_STAFF_ID = "fetchProposalStaffId";
	public static final String FETCH_NEW_PROPOSAL_DETAILS = "fetchNewProposalDetails";
	public static final String PROPOSAL_DETAILS_READONLY_FLAG = "proposalDetailsReadonlyFlag";
	public static final String F_SUBJECT = "F_Subject";
	public static final String ELEMENT_STATE = "state";
	public static final String AGENCY_USER_ID = "agencyUserId";
	public static final String AS_STATUS = "asStatus";
	public static final String AS_WORKFLOW_ID = "asWorkFlowId";
	public static final String FINISH_EVALUATION_REVIEW_STATUS = "finishEvaluationReviewsStatus";
	public static final String FINISH_EVALUATION_REVIEW_STATUS_COMPLETED = "finishEvaluationReviewsStatusCompleted";
	public static final String UPDATE_EVALUATION_REVIEW_STATUS = "updateEvaluationReviewDetails";

	public static final String IS_ASSIGNMENT_INVOICE_AMOUNT = "ASSIGNMENT_INVOICE_AMOUNT";
	public static final String IS_TOTAL_INVOICE_AMOUNT = "TOTAL_INVOICE_AMOUNT";
	public static final String CS_FETCH_CONTRACT_CONF_UPDATE_FYI_AMOUNT = "fetchContractConfUpdateFYIAmount";
	public static final String CS_FETCH_SUB_BUDGET_AMOUNT_BY_PARENT_ID = "fetchSubBudgetAmountByParentId";
	public static final String REVIEWER_LEVEL_KEY = "asReviewLevel";
	public static final String FIND_PROCUREMENT_DETAILS_FOR_WF = "findProcurementDetailsForWF";
	public static final String HHSAUDIT_PROVIDER_INSERT = "hhsauditProviderInsert";
	public static final String HHSAUDIT_AGENCY_INSERT = "hhsauditAgencyInsert";
	public static final String HHSAUDIT_ACCELERATOR_INSERT = "hhsauditAcceleratorInsert";
	public static final String UPDATE_COMMENT_NON_AUDIT_FOR_PROVIDER = "updateCommentNonAuditForProvider";
	public static final String SAVE_COMMENT_NON_AUDIT = "saveCommentNonAudit";
	public static final String UPDATE_COMMENT_NON_AUDIT = "updateCommentNonAudit";
	public static final String INSERT_NEW_MILESTONE_FOR_MOD = "insertNewMilestoneForMod";
	public static final String FETCH_MILESTONE_FROM_ID = "fetchMilestoneFromId";
	public static final String FETCH_MILESTONE_BASE_DETAILS = "fetchMilestoneBaseDetails";
	public static final String FETCH_MILESTONE_FOR_MODIFICATION = "fetchMilestoneForModification";
	public static final String FETCH_MILESTONE_FOR_AMENDMENT = "fetchMilestoneForAmendment";
	public static final String INSERT_NEW_MILESTONE_FOR_AMD = "insertNewMilestoneForAmd";
	public static final String IS_PROC_DOCS_VISIBLE = "IsProcDocsVisible";
	public static final String UPDATE_RFP_DOCUMENT_STATUS = "updateRFPdocumentStatus";
	public static final String AUDIT_TYPE = "asAuditType";
	public static final String APPLICATION = "application";
	public static final String INSERT_APP_AUDIT = "insertInApplicationAudit";
	public static final String TNC_ENTITY_ID = "tncentityId";
	public static final String INSERT_IN_APP_AUDIT_DOC = "insertInApplicationAuditDocument";
	public static final String INSERT_GEN_AUDIT = "insertInGeneralAudit";
	public static final String AUDIT_MAPPER_PACKAGE = "com.nyc.hhs.service.db.services.application.AuditMapper";
	public static final String FETCH_PROVIDER_EVAL_SCORE = "fetchProviderEvaluationScores";
	public static final Map<String, String> INVOICE_CONSTANT = new HashMap<String, String>();
	static
	{
		INVOICE_CONSTANT.put(OPS_KEY, OPS);// OTPS
		// Operations
		// &
		// Support
		INVOICE_CONSTANT.put(EQP_KEY, EQP);// OTPS
		// Operations
		// &
		// Support
		INVOICE_CONSTANT.put(OS_KEY, OS);// OTPS
											// Operations
											// &
											// Support
		INVOICE_CONSTANT.put(TS_KEY, TS);// Personnel
											// Services
											// salary
		INVOICE_CONSTANT.put(TF_KEY, TF);// Personnel
											// Services
											// fringe
		INVOICE_CONSTANT.put(UT_KEY, "Utilities");// Utilities
		INVOICE_CONSTANT.put(PS_KEY, PS);// Professional
											// Services
		INVOICE_CONSTANT.put(RO_KEY, RO);// Rent
		INVOICE_CONSTANT.put(CS_KEY, CS);// Contracted
											// Services
		INVOICE_CONSTANT.put(TR_KEY, TR);// Rate
		INVOICE_CONSTANT.put(TM_KEY, TM);// Milestone
		INVOICE_CONSTANT.put(UF_KEY, "Unallocated Funds");// Unallocated
		// Funds
		INVOICE_CONSTANT.put(TI_KEY, TI);// Indirect
											// Rate
		INVOICE_CONSTANT.put(TP_KEY, TP);// Program
											// Income
		INVOICE_CONSTANT.put(TSF_KEY, TSF);// Personnel
											// Services
											// salary
											// and
											// fringe
		INVOICE_CONSTANT.put(TOTPS_KEY, TOTPS);// Total
												// OTPS
		INVOICE_CONSTANT.put(TPB_KEY, TPB);// Total
											// Program
											// Budget
		INVOICE_CONSTANT.put(TCFB_KEY, TCFB);// Total
												// City
												// Funded
												// Budget
		INVOICE_CONSTANT.put(TDC_KEY, TDC);// Total
											// Direct
											// Costs

		INVOICE_CONSTANT.put(OS_ENTRY_TYPE_KEY, TWO);// OTPS
														// Operations
														// &
														// Support
		INVOICE_CONSTANT.put(EQUI_ENTRY_TYPE_KEY, EQUI_ENTRY_TYPE);// OTPS
		// Operations
		// &
		// Support
		INVOICE_CONSTANT.put(TS_ENTRY_TYPE_KEY, ONE);// Personnel
														// Services
														// salary
		INVOICE_CONSTANT.put(TF_ENTRY_TYPE_KEY, STRING_THIRTEEN);// Personnel
																	// Services
																	// fringe
		INVOICE_CONSTANT.put(UT_ENTRY_TYPE_KEY, THREE);// Utilities
		INVOICE_CONSTANT.put(PS_ENTRY_TYPE_KEY, FOUR);// Professional
														// Services
		INVOICE_CONSTANT.put(RO_ENTRY_TYPE_KEY, FIVE);// Rent
		INVOICE_CONSTANT.put(CS_ENTRY_TYPE_KEY, CS_ENTRY_TYPE);// Contracted
																// Services
		INVOICE_CONSTANT.put(TR_ENTRY_TYPE_KEY, SEVEN);// Rate
		INVOICE_CONSTANT.put(TM_ENTRY_TYPE_KEY, EIGHT);// Milestone
		INVOICE_CONSTANT.put(UF_ENTRY_TYPE_KEY, UF_ENTRY_TYPE);// Unallocated
																// Funds
		INVOICE_CONSTANT.put(TI_ENTRY_TYPE_KEY, STRING_TEN);// Indirect
															// Rate
		INVOICE_CONSTANT.put(TP_ENTRY_TYPE_KEY, STRING_ELEVEN);// Program
																// Income
	}

	public static final String UPDATE_INVOICE_DETAILS = "updateInvoiceDetails";
	public static final String INVOICE_START_DATE = "invoiceStartDate";
	public static final String INVOICE_END_DATE = "invoiceEndDate";
	public static final String GET_INVOICE_DETAILS = "getInvoiceDetails";
	public static final String JSP_PATH_INVOICE_ASSIGN_ADVANCE = "jsp/invoice/invoiceAssignAdvanceTable";
	public static final String ERROR_EQUAL = "&error=";
	public static final String AGENCY_PAGE = "&_pageLabel=portlet_hhsweb_portal_page_agency_home";
	public static final String AGENCY_TASK_INBOX_PAGE = "&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow";
	public static final String PAGE_LABEL_PORTLET_URL_MOD = "&_pageLabel=portlet_hhsweb_portal_page_budget_modification";
	public static final String PAGE_LABEL_PORTAL_CONTRACT_INVOICE_PAGE_URL = "&_pageLabel=portlet_hhsweb_portal_contract_invoice_page";
	public static final String CONTRACT_ID_URL = "&contractId=";
	public static final String CT_ID_URL = "&ctId=";
	public static final String BUDGET_TYPE_URL = "&budgetType=";
	public static final String FISCAL_YEAR_ID_URL = "&fiscalYearId=";
	public static final String FISCAL_YEAR_URL = "&fiscalYearID=";
	public static final String INVOICE_ID_URL = "&invoiceId=";
	public static final String PAGE_LABEL_PORTLET_URL = "&_pageLabel=portlet_hhsweb_portal_contract_budget_page";
	public static final String BUDGET_ID_URL = "&budgetId=";
	public static final String HHSAUDIT = "hhsaudit";
	public static final String HHSAUDIT_TABLEVEL = "hhsauditTabLevel";
	public static final String HHS_DELETE_USER_COMMENTS_BLANK_COMMENTS_SAVE = "deleteUserCommentsIfEmptyCommentsSaved";
	public static final String JSP_TASKS_VIEWTASKHISTORY = "jsp/tasks/viewtaskhistory";
	public static final String GET_PROPOSAL_DOCUMENT_STATUS_QUERY = "getProposalDocumentStatus";
	public static final String UPDATE_PROPOSAL_DOCUMENT_STATUS_QUERY = "setDocumentStatusCompleted";
	public static final String JSP_PATH_CONTRACT_INVOICE_REVIEW_TASK = "jsp/invoice/contractInvoiceReviewTask";
	public static final String GET_CONTRACT_INVOICE = "getContractInvoice";

	public static final String ACCELERATOR_COMMENTS = "ACCELERATOR_COMMENTS";
	public static final String NOTIFICATION_HREF_1 = "://";
	public static final String GET_PROCUREMENT_AGENCY_ID = "getProcurementAgencyId";
	public static final String EVALUATION_SETTINGS_SCREEN = "evaluation/evaluationsettings";
	public static final String NAVIGATION_SM = "navigationSM";
	public static final String SCREEN = "screen";
	public static final String EVAL_SCREEN = "evalScreen";
	public static final String EVALUATION_RESULTS = "evaluationResults";
	public static final String ORGANIZATION_NAME_LIST = "organizationNameList";
	public static final String FETCH_ORG_NAME_EVAL_RESULTS = "fetchOrgNameEvalResults";
	public static final String FROM_AWARD_TASK = "fromAwardTask";
	public static final String EVALUATION_LOWER = "evaluation";
	public static final String IS_VIEW_DOC_INFO_FROM_AGENCY = "lsViewDocInfoFromAgency";
	public static final String GET_EVALUATION_REVIEW_SCORE = "getEvaluationReviewScore";

	public static final String PROCUMENET_ID = "procumentID";

	public static final String AO_ERROR_MSG = "aoErrorMsg";
	public static final String AO_ERROR_CODE = "aoErrorCode";

	// Review comments constants removed
	public static final String JAVAX_SERVLET_REQUEST = "javax.servlet.request";
	public static final String JAVAX_SERVLET_RESPONSE = "javax.servlet.response";
	public static final String JSP_INVOICE_CONTRACT_INVOICE = "jsp/invoice/contractInvoice";
	public static final String IN_SE_BUDGET_ID = "BudgetId";
	public static final String ASSIGNMENT_ENTRY_TYPE_ID = "14";
	public static final String INVOICE_ENTRY_TYPE_ID = "ENTRY_TYPE_ID";
	public static final String INVOICE_LINE_ITEM_ID = "LINE_ITEM_ID";
	public static final String FETCH_COUNT_INVOICE_DETAILS = "fetchCountInvoiceDetails";
	public static final String DEL_INVOICE_DETAILS = "delInvoiceDetails";
	public static final String INSERT_EVALUATION_RESULT = "insertEvaluationResult";
	public static final String UPDATE_EVALUATION_RESULT = "updateEvaluationResult";
	public static final String SCORE = "score";

	public static final String HDN_REVIEW_LEVELS = "hdnReviewLevels";
	public static final String GET_ALL_REVIEW_PROCESS_DATA = "getAllReviewProcessData";
	public static final String OLD_REVIEW_LEVELS = "oldReviewLevels";
	public static final String AI_REVIEW_LEVELS = "aiReviewLevels";
	public static final String GET_REVIEW_LEVELS = "getReviewLevels";
	public static final String AI_REVIEW_PROC_ID = "aiReviewProcId";

	// Home Page Constants Starts
	public static final String FETCH_FINANCIAL_COUNT_ACC_HOME_PAGE = "fetchFinancialCountForAccHomePage";
	public static final String FINANCIAL_SUMMARY_BEAN = "financialSummaryBean";
	public static final String FINANCIAL_SUMMARY = "financialSummary";
	public static final String USER_ORG = "userOrg";
	public static final String FETCH_FINANCIAL_COUNT_HOME_PAGE = "fetchFinancialCountForProvHomePage";
	public static final String FETCH_PROCUREMENT_COUNT_PROV_HOME_PAGE = "fetchProcurementCountForProvHomePage";
	public static final String ACTIVE_BUDGETS = "activeBudgets";
	public static final String BUDGETS_PEND_SUBMISSION = "budgetsPendSubmission";
	public static final String BUDGETS_PEND_APPROVAL = "budgetsPendApproval";
	public static final String BUDGETS_RET_REVISION = "budgetRetRevision";
	public static final String MOD_PEND_SUBMISSION = "modPendSubmission";
	public static final String MOD_PEND_APPROVAL = "modPendApproval";
	public static final String MOD_UPDATES_RET_SUBMISSION = "modUpdatesReturnedSubmission";
	public static final String INVOICES_PEND_SUBMISSION = "invoicesPendingSubmission";
	public static final String INVOICES_PEND_APPROVAL = "invoicesPendingApproval";
	public static final String INVOICES_RET_REVISION = "invoicesReturnedRevision";
	public static final String FETCH_PROC_COUNT_ACC_HOMEPAGE = "fetchProcurementCountForAccHomePage";
	public static final String PROC_SUMMARY_BEAN = "procurementSummaryBean";
	public static final String PROC_SUMMARY_LOWERCASE = "procurementSummary";

	// FinancialsListService Constants
	public static final String FETCH_CONTRACT_AMOUNT_PROVIDER = "fetchContractAmountProvider";
	public static final String FETCH_CONTRACT_COUNT_ACCELERATOR = "fetchContractCountAccelerator";
	public static final String FETCH_CONTRACT_COUNT_AGENCY = "fetchContractCountAgency";
	public static final String FETCH_CONTRACT_COUNT_PROVIDER = "fetchContractCountProvider";
	public static final String FETCH_AMENDMENT_LIST_ACCELEARATOR = "fetchAmendmentListAccelearator";
	public static final String RENEWAL_RECORD_EXIST_FOR_CONTRACT_DROP_DOWN = "renewalRecordExistForContractDropDown";
	public static final String FETCH_CONTRACT_LIST_ACCELERATOR = "fetchContractListAccelerator";
	public static final String FETCH_AMENDMENT_LIST_AGENCY = "fetchAmendmentListAgency";
	public static final String FETCH_CONTRACT_LIST_AGENCY = "fetchContractListAgency";
	public static final String FETCH_AMENDMENT_LIST_PROVIDER = "fetchAmendmentListProvider";
	// Start Release 3.8.0 Enhancement 6481
	public static final String FETCH_CONTRACT_UPDATE_APPROVED_ACTIVE_BUDGET = "fetchContractUpdateForApprovedActiveBudget";
	public static final String UPDATE_CONTRACT_CONFIGURATION = "Update Contract Configuration";
	// End Release 3.8.0 Enhancement 6481
	public static final String FETCH_CONTRACT_LIST_PROVIDER = "fetchContractListProvider";
	public static final String AMEND_CONF_TASK = "AmendConfTask";
	public static final String BUDGET_AMENDMENT_STATUS = "BudgetAmendmentStatus";
	public static final String CONTRATC_UPDATE_CHECK04 = "contratcUpdateCheck04";
	public static final String CONTRATC_UPDATE_CHECK02 = "contratcUpdateCheck02";
	public static final String UPDATE_CONFIGURATION_ERROR_RULE01 = "UpdateConfigurationErrorRule01";
	public static final String UPDATE_PAYMENT_UNSUSPEND = "updatePaymentUnsuspend";
	public static final String UPDATE_INVOICE_UNSUSPEND = "updateInvoiceUnsuspend";
	public static final String UPDATE_BUDGET_UNSUSPEND = "updateBudgetUnsuspend";
	public static final String UPDATE_CONTRACT_UNSUSPEND = "updateContractUnsuspend";
	public static final String CHECK_STATUS_FOR_SUSPENDED = "checkStatusForSuspended";
	public static final String CHECK_STATUS_FOR_SUS_OR_UN_SUS = "checkStatusForSusOrUnSus";
	public static final String CHECK_STATUS_FOR_UN_SUSPENDED = "checkStatusForUnSuspended";
	public static final String PAYMENT_STATUS_ID = "paymentStatusId";
	public static final String FETCH_CONTRACT_DISBURSED_AMOUNT = "fetchContractDisbursedAmount";
	public static final String AMEND_CONTRACT_FAILURE01 = "amendContractFailure01";
	public static final String AMEND_NEGATIVE_CONTRACT_FAILURE01 = "amendNegativeContractFailure01";
	public static final String DECIMAL_ZERO = "0.00";
	public static final Double DOUBLE_DECIMAL_ZERO = 0.0d;
	public static final Long LONG_ZERO = 0L;
	public static final Float FLOAT_ZERO = 0F;
	public static final Float FLOAT_HUNDERED = 100.f;
	public static final Float FLOAT_ZERO_ZERO = 0.00f;
	public static final int INT_HUNDRED = 100;
	public static final String AMEND_CONTRACT_FAILURE02 = "amendContractFailure02";
	public static final String AMEND_CONTRACT_ERROR_RULE_SET02 = "AmendContractErrorRuleSet02";
	public static final String AMEND_CONTRACT_FAILURE03 = "amendContractFailure03";
	public static final String AMEND_CONTRACT_ERROR_RULE_SET03 = "AmendContractErrorRuleSet03";
	public static final String AMEND_CONTRACT_FAILURE04 = "amendContractFailure04";
	public static final String BUDGET_MODIFICATION_STATUS = "BudgetModificationStatus";
	public static final String BUDGET_NEW_FY_STATUS = "BudgetNewFYStatus";
	public static final String BUDGET_UPDATE_STATUS = "BudgetUpdateStatus";
	public static final String AMENDMENT_VALUE = "amendmentValue";
	public static final String CURRENT_FY_PLANNED_AMOUNT = "currentFyPlannedAmount";
	public static final String AMENDMENT_FY_PLANNED_AMOUNT = "amendmentFyPlannedAmount";
	public static final String AMEND_CONTRACT_ERROR_RULE_SET01 = "AmendContractErrorRuleSet01";
	public static final String AMEND_CONTRACT_FAILURE05 = "amendContractFailure05";
	public static final String WORK_FLOW_ID = "workFlowId";
	public static final String INSERT_CONTRACT_CONFIGURATION = "insertContractConfiguration";
	public static final String FETCH_CANCEL_CONTRACT_DETAILS = "fetchCancelContractDetails";
	public static final String CONTRACT_SOURCE_TYPE = "ContractSourceType";
	public static final String FLS_CONTRACT_STATUS = "ContractStatus";
	public static final String CONTRACT_TYPE = "ContractType";
	public static final String CANCEL_CONTRACT_RULE_FOR_ACCELERATOR = "cancelContractRuleForAccelerator";
	public static final String CANCEL_CONTRACT_RULE_FOR_AGENCY = "cancelContractRuleForAgency";
	public static final int INT_MINUS_ONE = -1;
	public static final String CURRENT_FISCAL_YEAR = "CurrentFiscalYear";
	public static final String CONTRACT_END_FY = "ContractEndFY";
	public static final String FISCAL_YEAR = "fiscalYear";
	public static final String FISCAL_YEAR_END_DATE = "06/30/";
	public static final String IS_FY_BUDGET_ENTRY = "isFYBudgetEntry";
	public static final int INT_ONE = 1;
	public static final int INT_TWO = 2;
	public static final int INT_THREE = 3;
	public static final int INT_FOUR = 4;
	public static final int INT_FIVE = 5;
	public static final int INT_CAPACITY_256 = 256;
	public static final String CONF_UPDATE_TASK_STATUS = "ConfUpdateTaskStatus";
	public static final String FY_CONF_TASK_STATUS = "FYConfTaskStatus";
	public static final String AMEND_CONF_TASK_STATUS = "AmendConfTaskStatus";
	public static final String AMEND_CERT_FUND_TASK = "AmendCertFundTask";
	public static final String NEW_FY_CONFIG_ERROR_RULE02 = "NewFYConfigErrorRule02";
	public static final String NEW_FY_CONFIG_ERROR_RULE01 = "NewFYConfigErrorRule01";
	public static final String NEW_FY_CONFIG_ERROR_RULE03 = "NewFYConfigErrorRule03";
	public static final String FETCH_CONTRACT_BUDGET_STATUS = "fetchContractBudgetStatus";
	public static final String GET_CONTRACT_AMENDMENT_AMMOUNT = "getContractAmendmentAmmount";
	public static final String FETCH_CONTRACT_INVOICE_STATUS = "fetchContractInvoiceStatus";
	public static final String FETCH_CONTRACT_PAYMENT_STATUS = "fetchContractPaymentStatus";
	public static final String UPDATE_CONTRACT_SUSPEND = "updateContractSuspend";
	public static final String UPDATE_BUDGET_SUSPEND = "updateBudgetSuspend";
	public static final String UPDATE_INVOICE_SUSPEND = "updateInvoiceSuspend";
	public static final String UPDATE_PAYMENT_SUSPEND = "updatePaymentSuspend";

	public static final String SELECT_BUDGET_SUSPEND = "selectBudgetSuspend";
	public static final String SELECT_INVOICE_SUSPEND = "selectInvoiceSuspend";
	public static final String SELECT_PAYMENT_SUSPEND = "selectPaymentSuspend";

	public static final String SELECT_BUDGET_UNSUSPEND = "selectBudgetUnsuspend";
	public static final String SELECT_INVOICE_UNSUSPEND = "selectInvoiceUnsuspend";
	public static final String SELECT_PAYMENT_UNSUSPEND = "selectPaymentUnsuspend";

	public static final String UPDATE_CONTRACT_REASON = "updateContractReason";
	public static final String FETCH_ALL_CONTRACT_ID = "fetchAllContractId";
	//Start: R7 defect 8644 Cancel and Merge
	public static final String FETCH_ALL_CONTRACT_ID_AFTER_CANCEL_AND_MERGE = "fetchContractIdAfterCancelAndMerge";
	//End
	public static final String FETCH_ALL_CONTRACT_ID_FOR_UPDATE_CHECK = "fetchAllContractIdForUpdateCheck";
	public static final String LS_CONTRACT_ID_AMEND = "lsContractIdAmend";
	public static final String GET_CONTRACT_ID_AMEND = "getContractIdAmend";
	public static final String LS_PAYMENT_DISBURSED_STATUS_ID = "lsPaymentDisbursedStatusId";
	public static final String LS_PAYMENT_CANCELLED_STATUS_ID = "lsPaymentCancelledStatusId";
	public static final String LS_PAYMENT_APPROVED_STATUS_ID = "lsPaymentApprovedStatusId";
	public static final String LS_INVOICE_CANCELLED_STATUS_ID = "lsInvoiceCancelledStatusId";
	public static final String LS_INVOICE_APPROVED_STATUS_ID = "lsInvoiceApprovedStatusId";
	public static final String LS_BUDGET_CANCELLED_STATUS_ID = "lsBudgetCancelledStatusId";
	public static final String LS_BUDGET_APPROVED_STATUS_ID = "lsBudgetApprovedStatusId";
	public static final String LS_SUSPEND_PAYMNET_STATUS_ID = "lsSuspendPaymnetStatusId";
	public static final String LS_SUSPEND_INVOICE_STATUS_ID = "lsSuspendInvoiceStatusId";
	public static final String LS_SUSPEND_BUDGET_STATUS_ID = "lsSuspendBudgetStatusId";
	public static final String LS_SUSPEND_CONTRACT_STATUS_ID = "lsSuspendContractStatusId";
	public static final String LS_AMEND_CONTRACT_ID = "lsAmendContractId";
	public static final String FLS_RENEW_CONTRACT_DETAILS = "renewContractDetails";
	public static final String UPDATE_CONTRACT_BUDGET_STATUS = "updateContractBudgetStatus";
	public static final String SELECT_CONTRACT_BUDGET_STATUS = "selectContractBudgetStatus";
	public static final String CLOSE_BUDGET_CONTRACT = "closeBudgetContract";
	public static final String CONTRACT_BUDGET_STATUS_ID = "ContractBudgetStatusId";
	public static final String CONTRACT_STATUS_ID = "ContractStatusId";
	public static final String VALIDATE_RENEW_CONTRACT_DETAILS = "validateRenewContractDetails";
	public static final String CLOSE_CONTRACT_ERROR_RULE01 = "CloseContractErrorRule01";
	public static final String NOT_DISBURSED_PAYMENT_COUNT = "NotDisbursedPaymentCount";
	public static final String NOT_APPROVED_INVOICE_COUNT = "NotApprovedInvoiceCount";
	public static final String BUDGET_NOT_APPROVED_COUNT = "BudgetNotApprovedCount";
	public static final String OPEN_TASK_RELATED_CONTRACT = "OpenTaskRelatedContract";
	public static final String GET_NOT_DISBURSED_PAYMENT_COUNT = "getNotDisbursedPaymentCount";
	public static final String GET_NOT_APPROVED_INVOICE_COUNT = "getNotApprovedInvoiceCount";
	public static final String PAYMENT_DISBURSED = "paymentDisbursed";
	public static final String INVOICE_APPROVED = "invoiceApproved";
	public static final String CONTRACT_IDS_UPDATE_MODIFICATION_AMENDMENT = "getAllContractIds";
	public static final String GET_BUDGET_NOT_APPROVED_COUNT = "getBudgetNotApprovedCount";
	public static final String BUDGET_APPROVED = "budgetApproved";
	public static final String BUDGET_UPDATE = "budgetUpdate";
	public static final String BUDGET_MODIFICATION = "budgetModification";
	public static final String BUDGET_AMENDMENT = "budgetAmendment";
	public static final String ADD_NEW_CONTRACT = "addNewContract";
	public static final String FIND_CONTRACT_DETAILS_BY_EPI_NFOR_AMEND = "findContractDetailsByEPINforAmend";
	public static final String COM_NYC_HHS_MODEL_E_PIN_DETAIL_BEAN = "com.nyc.hhs.model.EPinDetailBean";
	public static final String FIND_CONTRACT_DETAILS_BY_EPI_NFOR_NEW = "findContractDetailsByEPINforNew";
	public static final String FIND_CONTRACT_DETAILS_BY_EPIN = "findContractDetailsByEPIN";
	public static final String FIND_CONTRACT_DETAILS_BY_CONTRACT = "findContractDetailsByContract";
	public static final String UPDATE_AMEN_BUDGET_STATUS = "updateAmenBudgetStatus";
	public static final String SELECT_AMEN_BUDGET_STATUS = "selectAmenBudgetStatus";
	public static final String UPDATE_CONTRACT_AMEND_STATUS = "updateContractAmendStatus";
	public static final String UPDATE_AMEND_CONTRACT_INFO = "updateAmendContractInfo";
	public static final String FLS_70_72_RETURN_FOR_REVISION = "70,72,Return for Revision";
	public static final String BUDGET_TYPES = "1,2,3,4";
	public static final String AMENDMENT_STATUS_IDS_FOR_ERROR_MSG = "70,72,71";
	public static final String ERROR_MESSAGE_BUDGET_IN_PROGRESS = "errorMessageBudgetInProgress";
	public static final String ERROR_MESSAGE_OUTSTANDING_INVOICE_PAYMENTS = "errorMessageOutstandingInvoicePayments";
	public static final String ERROR_MESSAGE_BUDGET_MODIFICATION_IN_PROGRESS = "errorMessageBudgetModificationInProgress";
	public static final String ERROR_MESSAGE_BUDGET_UPDATE_IN_PROGRESS = "errorMessageBudgetUpdateInProgress";
	public static final String COM_NYC_HHS_MODEL_CONTRACT_LIST = "com.nyc.hhs.model.ContractList";
	public static final String ERROR_MESSAGE_AMEND_SCEEN = "errorMessageAmendSceen";
	public static final String STATUS_THIRTY_THREE = "33";
	public static final String GET_DATA_W_FBY_CONTRACT_ID = "getDataWFbyContractId";
	public static final String FLS_CONTRACT_TYPE_ID = "CONTRACT_TYPE_ID";
	public static final String CONTRACT_SOURCE_ID = "CONTRACT_SOURCE_ID";
	public static final String EVALUATION_SCORE_COL = "evaluation_score";
	public static final String CONTRACTS_PEND_REGISTRATION = "contractsPendingRegistration";

	public static final String DELETE_FROM_USER_COMMENT = "deleteFromUserComment";
	public static final String COPY_AGENCY_TASK_COMMENT_HISTORY = "copyAgencyTaskCommentHistory";
	public static final String CHECK_DOCUMENT_EXISTS_IN_ANY_TABLE = "checkDocumentExistsInAnyTable";
	public static final String GET_PROVIDER_WIDGET_DETILS = "getProviderWidgetDetils";
	public static final String UPDATE_LAST_MODIFIED_DETAILS = "updateLastModifiedDetails";
	public static final String GET_PROCUREMENT_CHANGE_CONTROL_WIDGET = "getProcurementChangeControlWidget";
	public static final String GET_PROCUREMENT_CHANGE_CONTROL_WIDGET_DETAILED = "getProcurementChangeControlWidgetDetailed";
	public static final String GET_MASTER_STATUS = "getMasterStatus";
	public static final String GET_READ_ONLY_ACTIONS_TO_EXCLUDE = "getReadOnlyActionsToExclude";
	public static final String READ_ONLY_ACTIONS_EXCLUDE_LIST = "actionsExcludeList";
	public static final String FETCH_PROVIDER_FINANCIAL_COUNT = "fetchProviderFinancialCount";
	public static final String FETCH_ACC_FINANCIALS_PORTLET_COUNT = "fetchAccFinancialsPortletCount";
	public static final String FETCH_PROCUREMENT_COUNT_FOR_PROV = "fetchProcurementCountForProv";
	public static final String FETCH_ACC_PROCUREMENT_PORTLET_COUNT = "fetchAccProcurementPortletCount";
	public static final String CBY_FETCH_CONTRACTED_SERVICES_NEW_MODIFICATION_CONSULTANTS = "fetchContractedServicesNewModificationConsultants";
	public static final String CBY_FETCH_CONTRACTED_SERVICES_MODIFICATION_CONSULTANTS = "fetchContractedServicesModificationConsultants";
	public static final String CBY_FETCH_CONTRACTED_SERVICES_MODIFICATION_VENDORS = "fetchContractedServicesModificationVendors";
	public static final String CBY_FETCH_CONTRACTED_SERVICES_MODIFICATION_SUB_CONTRACTORS = "fetchContractedServicesModificationSubContractors";
	public static final String CBY_ADD_CONTRACTED_SERVICES_MODIFICATION = "addContractedServicesModification";
	public static final String CBY_UPDATE_CONTRACTED_SERVICES_MODIFICATION = "updateContractedServicesModification";
	public static final String CBY_EDIT_CONTRACTED_SERVICES_MODIFICATION = "editContractedServicesModification";
	public static final String CBY_DEL_CONTRACTED_SERVICES_MODIFICATION = "delContractedServicesModification";
	public static final String CBY_FETCH_INSERT_CONTRACTED_SERVICES_MODIFICATION = "fetchInsertContractedServicesModification";
	public static final String CONTRACTED_MODIFICATION_SERVICES = "contractedServicesModification";
	public static final String CONTRACTED_UPDATE_SERVICES = "contractedServicesUpdate";
	public static final String CONTRACTED_AMENDMENT_SERVICES = "contractedServicesAmendment";
	public static final String FETCH_NONGRID_CONTRACTED_SERVICES_MODIFICATION = "getNonGridContractedServicesModification";
	public static final String BASE_LS_CONFIGURABLE_FISCAL_YEAR = "lsConfigurableFiscalYear";
	public static final String DOC_CREATED_DATE = "docCreatedDate";
	public static final String DOC_TYPE = "DOC_TYPE";
	public static final String DOCUMENT_TITLE = "DocumentTitle";
	public static final String BASE_LAST_MODIFIED_DATE = "lastModifiedDate";
	public static final String BASE_LAST_MODIFIED_BY = "lastModifiedBy";
	public static final String BASE_TRANSACTION_NAME = "transaction_name";
	public static final String BASE_UPDATE_FINANCIAL_DOCUMENT_PROPERTIES = "updateFinancialDocumentProperties";
	public static final String BASE_FINANCE_DOC_REMOVED_SUCCESS = "FINANCE_DOC_REMOVED_SUCCESS";
	public static final String BASE_REMOVE_FINANCIAL_DOCS_DB = "removeFinancialDocs_db";
	public static final String BASE_REMOVE_AGENCY_FINANCIAL_DOCS_DB = "removeAgencyFinancialDocs_db";
	public static final String BASE_AS_DOCUMENT_SEQUENCE = "asDocumentSequence";
	public static final String BASE_DELETE_DOCUMENT_SEQUENCE = "deleteDocumentSequence";
	public static final String BASE_JSP_TASKS_VIEWDOCUMENTINFO = "jsp/tasks/viewdocumentinfo";
	public static final String BASE_VIEW_FINANCIAL_DOCUMENT_INFO = "viewFinancialDocumentInfo";
	public static final String BASE_HDN_EDITABLE = "hdnEditable";
	public static final String BASE_HDN_ORG_TYPE = "hdnOrgType";
	public static final String BASE_UPLOAD_FINANCIAL_DOCUMENT = "uploadFinancialDocument";
	public static final String BASE_INSERT_BUDGET_DOCUMENT_DETAILS_DB = "insertBudgetDocumentDetails_db";
	public static final String BASE_INSERT_INVOICE_DOCUMENT_DETAILS_DB = "insertInvoiceDocumentDetails_db";
	public static final String BASE_INSERT_CONTRACT_DOCUMENT_DETAILS_DB = "insertContractDocumentDetails_db";
	public static final String BASE_INSERT_RETURNED_PAYMENT_DOCUMENT_DETAILS_DB = "insertReturnedPaymentDocumentDetails_db";
	public static final String BASE_DOCUMENT_UPLOAD_TO = "documentUploadTo";
	public static final String BASE_JSP_TASKS_DOCUMENT = "jsp/tasks/document";
	public static final String BASE_FILE_UPLOADED_SUCCESSFULLY = "File Uploaded Successfully.";
	public static final String BASE_DISPLAY_DOCUMENT_SUCCESS = "displayDocumentSuccess";
	public static final String BASE_SECTION_ID = "section_id";
	public static final String BASE_SERVICE_APP_ID = "serviceAppId";
	public static final String BASE_FORM_VERSION = "formVersion";
	public static final String BASE_FORM_NAME = "formName";
	public static final String BASE_HIDDEN_SAMPLE_TYPE = "hiddenSampleType";
	public static final String BASE_HIDDEN_SAMPLE_CATEGORY = "hiddenSampleCategory";
	public static final String BASE_HIDDEN_DOC_SHARE_STATUS = "hiddenDocShareStatus";
	public static final String BASE_HIDDEN_FILTER_NYC_AGENCY = "hiddenFilterNYCAgency";
	public static final String BASE_HIDDEN_FILTER_PROVIDER_ID = "hiddenFilterProviderId";
	public static final String BASE_HIDDEN_FILTER_MODIFIED_TO = "hiddenFilterModifiedTo";
	public static final String BASE_HIDDEN_FILTER_MODIFIED_FROM = "hiddenFilterModifiedFrom";
	public static final String BASE_HIDDEN_DOC_TYPE = "hiddenDocType";
	public static final String BASE_HIDDEN_DOC_CATEGORY = "hiddenDocCategory";
	public static final String BASE_JSP_TASKS_DISPLAY_UPLOADING_DOCUMENT_INFO_PROVIDER = "jsp/tasks/displayUploadingDocumentInfoProvider";
	public static final String BASE_UPLOADING_FINANCIAL_FILE_INFORMATION = "uploadingFinancialFileInformation";
	public static final String BASE_RFP_DOCUMENTS = "rfpDocuments";
	public static final String BASE_FINANCIALS = "Financials";
	public static final String BASE_FY = "FY";
	public static final String BASE_FAILEDLOGINEXCEPTION = "failedloginexception";
	public static final String BASE_JSP_INVOICE = "jsp/invoice/";
	public static final String BASE_CB_INVOICE_SUMMARY = "CBInvoiceSummary";
	public static final String BASE_LO_CB_INVOICE_SUMMARY = "loCBInvoiceSummary";
	public static final String BASE_GET_INVOICE_SUMMARY = "getInvoiceSummary";
	public static final String BASE_YTD_INVOICED_AMOUNT = "ytdInvoicedAmount";
	public static final String BASE_INVOICE_TOTAL_AMOUNTS = "invoiceTotalAmounts";
	public static final String BASE_FETCH_INVOICE_OP_SUPPORT_AMOUNTS = "fetchInvoiceOpSupportAmounts";
	public static final String BASE_FETCH_INVOICE_CONTRACTED_SERVICES_AMOUNTS = "fetchContractedServicesAmounts";
	public static final String BASE_INVOICE_OPERATION_SUPPORT = "invoiceOperationSupport";
	public static final String BASE_HDN_INVOICE_ID = "hdnInvoiceId";
	public static final String CB_OPERATION_SUPPORT_BEAN = "aoCBOperationSupportBean";
	public static final String BASE_HDN_PARENT_SUB_BUDGET_ID = "hdnParentSubBudgetId";
	public static final String BASE_AO_CB_CONTRACTED_SERVICES_BEAN = "aoCBContractedServicesBean";
	public static final String BASE_PS_MASTER_DETAILS = "psMasterDetails";
	public static final String BASE_LO_PERSONNEL_SERVICE_DATA = "loPersonnelServiceData";
	public static final String BASE_SUB_GRID_READONLY = "subGridReadonly";
	public static final String BASE_READ_ONLY_PAGE = "readOnlyPage";
	public static final String BASE_HDN_SUB_BUDGET_ID = "hdnSubBudgetId";
	public static final String BASE_HDN_TAB_ID = "hdnTabId";
	public static final String BASE_HDN_AMENDED_CONTRACT_SUB_BUDGET_ID = "hdnAmendedContractSubBudgetID";
	public static final String BASE_HDN_TAB_NAME = "hdnTabName";
	public static final String BASE_FINANCIALS_FUNDING_GRID = "financialsFundingGrid";
	public static final String BASE_CONTRACT_CON_UPDATE_INVOICE = "CONTRACT_CON_UPDATE_INVOICE";
	public static final String BASE_GRID_ACTION_FAILED_PLEASE_TRY_AGAIN_LATER = "Grid action failed, please try again later";
	public static final String BASE_ERROR_OCCURED_IN_GET_TASK_HANDLER_OF_BASE_CONTLOLLER = "Error occured in getTaskHandler of BaseContloller";
	public static final String FETCH_PROC_DETAILS_FOR_AWARD_WF = "fetchProcurementDetailsForAwardWF";
	public static final String FETCH_UPDATED_AWARD_AMOUNT = "fetchUpdatedAwardAmount";

	public static final String CS_DEL_CONTRACT_CONF_UPDATE_SUBBUDGET_DETAILS = "delContractConfUpdateSubBudgetDetails";
	public static final String CS_FETCH_SUB_BUDGET_PARENT_ID = "fetchSubBudgetParentId";
	public static final String CS_UPDATE_SUB_BUDGET_AMOUNT = "updateContractConfSubBudgetAmt";
	public static final String CS_DEL_CONTRACT_CONF_UPDATE_SUBBUDGET_OLD_DETAILS = "delContractConfUpdateSubBudgetOldDetails";
	public static final String CS_INSERT_CONTRACT_CONF_UPDATE_FINISH_TASK = "insertContractConfUpdateFinishTask";
	public static final String CS_UPDATE_CONTRACT_CONF_UPDATE_FINISH_TASK = "updateContractConfUpdateFinishTask";
	public static final String CS_UPDATE_CONTRACT_CONF_FISCAL_AMT_FINISH_TASK = "updateContractConfFiscalAmt";
	public static final String CS_UPDATE_CONTRACT_CONF_UPDATE_NEW_RECORD_FINISH_TASK = "delContractConfUpdateNewRecordFinishTask";
	public static final String CS_UPDATE_CONTRACT_CONF_UPDATE_OLD_RECORD_FINISH_TASK = "delContractConfUpdateOldRecordFinishTask";
	public static final String CS_FINISH_CONTRACT_CONFIG_UPDATE_ALL_TASK = "finishContractConfigUpdateAllTask";
	public static final String CS_FINISH_CONTRACT_CONFIG_UPDATE_TASK = "finishContractConfigUpdateTask";
	public static final String CS_VALIDATE_CONTRACT_CONFIG_UPDATE_AMOUNT = "validateContractConfigUpdateAmount";
	public static final String CS_VALIDATE_CONTRACT_CONFIG_UPDATE_MESSAGE = "Sum of budgets cannot exceed FY Planned Amount";
	public static final String CS_VALIDATE_AMENDMENT_CONFIG_MESSAGE = "The total Amendment Amount allocated across the budgets must equal the Amendment Amount for the fiscal year.";
	public static final String CS_FETCH_CONTRACT_CONF_UPDATE_NEW_DETAILS = "fetchContractConfUpdateNewDetails";
	public static final String CS_FETCH_CONTRACT_CONF_AMENDMENT_DETAILS = "fetchContractConfAmendmentDetails";

	public static final String GET_CONTRACT_SEQ_FROM_TABLE = "getContractSeqFromTable";
	public static final String FINANCIAL_LIST_SCREEN = "FinancialsListScreen";
	public static final String FINANCIAL_VIEW_PER_PAGE = "ObjectsPerPage";
	public static final String FETCH_ACCEPTED_FOR_EVAL_PROPOSALS = "fetchAcceptedForEvalProposals";
	public static final String EVALUATION_DETAIL_BEAN = "com.nyc.hhs.model.EvaluationDetailBean";
	public static final String FETCH_PROPOSAL_DETAILS_FOR_EVAL_TASK = "fetchProposalDetailsForEvaluationTask";
	public static final String FETCH_EXT_AND_INT_EVALUATOR = "fetchExtAndIntEvaluator";
	public static final String ORGANIZATN_NAME = "organizationName";
	public static final String MODIFY_PROPOSAL_STATUS = "modifyProposalStatus";
	public static final String LO_LIST_RETURNED = "loListReturned";
	public static final String LB_FINAL_APPROVER = "lbFinalApprover";
	public static final String BEAN_PROP_DET_LIST = "loProposalDetailsBeanList";
	public static final String EVAL_SCORE = "getEvaluationScores";
	public static final String FETCH_EVAL_SET_INT = "fetchEvaluationSettingsExternal";
	public static final String UPDATE_EVAL_SCORE_DET = "updateEvaluationScoreDetails";
	public static final String FETCH_ORG_NAME_EVAL_RESULT = "fetchOrganizationNameEvalResults";
	public static final String UPDATE_EVAL_TASK_COMMENT = "updateEvaluationTaskComments";
	public static final String INSERT_EVAL_TASK_COMMENT = "insertEvaluationTaskComments";

	public static final String ACCEPT_PROPOSAL_TASK_JSP = "agencyWorkflow/acceptProposalTask";
	public static final String VIEW_DOCUMENT_INFO_JSP = "procurement/viewdocumentinfo";
	public static final String SHOW_ACCEPT_PROPOSAL_TASK_DETAILS = "showAcceptProposalTaskDetails";
	public static final String EVALUATE_PROPOSAL_TASK_JSP = "agencyWorkflow/evaluateProposalTask";
	public static final String SHOW_EVALUATE_PROPOSAL_TASK_DETAILS = "showEvaluateProposalTaskDetails";
	public static final String EVALUATE_PROPOSAL_TASK_SCORES_COMPLETED = "EVALUATE_PROPOSAL_TASK_SCORES_COMPLETED";
	public static final String EVAL_STATUS_MAP = "evalStatusMap";
	public static final String CONFIGURE_AWARD_DOC_TASK_JSP = "agencyWorkflow/configureAwardDocTask";
	public static final String UPLOAD_AWARD_TASK_DOCS = "agencyWorkflow/uploadAwardTaskDocs";
	public static final String DISPLAY_UPLOAD_DOC_INFO = "agencyWorkflow/displayUploadingDocInfo";
	public static final String DELETED_DOCUMENT_ID = "lsDeletedDocumentId";
	public static final String STATUS_UPDATE_MAP = "statusUpdateMap";
	public static final String FINISH_AWARD_DOCUMENT_TASK = "finishAwardDocumentTask";
	public static final String INSERT_SELECTED_PROPOSAL_COMMENTS = "insertSelectedProposalComments";
	public static final String FETCH_EVAL_RESULT_SCORE = "fetchEvaluationResultsScores";
	public static final String UPDATE_EVAL_TASK_SCORE = "updateEvaluationTaskScores";
	public static final String INSERT_EVAL_TASK_SCORE = "insertEvaluationTaskScores";
	public static final String AWARD_DOC_INFO = "getAwardDocumentsInfo";
	public static final String FETCH_DB_DOCS = "abFetchDBDDocs";
	public static final String DBD = "DBD";
	public static final String DB_DOC_LIST = "getDBDDocsList";
	public static final String CONTEXT_PATH = "asContextPath";
	public static final String AO_DB_DOC_LIST = "aoDBDDocList";
	public static final String INSERT_SCORE_DETAIL = "insertScoreDetails";
	public static final String SEND_NOTFICATION = "sendNotification";
	public static final String BASENEW_JSP_TASKS_ADD_DOCUMENT_FROM_VAULT = "jsp/tasks/addDocumentFromVault";
	public static final String BASENEW_JSP_TASKS_UPLOAD_DOCS = "jsp/tasks/uploadDocs";
	public static final String BASENEW_PROPERTY_LOGIN_ENVIRONMENT = "PROPERTY_LOGIN_ENVIRONMENT";
	public static final String UNDERSCORESCORE = "\":\"";
	public static final String BASENEW_PAGE_LABEL_PORTLET_HHSWEB_PORTAL_PAGE_AGENCY_HOME_ERROR_ERROR = "&_pageLabel=portlet_hhsweb_portal_page_agency_home&error=error";
	public static final String FROM = "from";
	public static final String STATUS_LIST = "statusList";
	public static final String ASC_STRING = "asc";
	public static final String THIS_NOT_VALID_USER = "this is not a valid user";
	public static final String DEL_CONTRACT_CONF_UPDATE_TASK_DETAILS = "delContractConfUpdateTaskDetails";

	public static final String FETCH_CONTRACT_BUDGET_MODIFICATION_RENT = "fetchContractBudgetModificationRent";// S368
	public static final String FETCH_CONTRACT_BUDGET_MODIFICATION_RENT_PERCENT_CHANGED = "fetchContractBudgetModificationRentPercentChanged";
	public static final String FETCH_CONTRACT_BUDGET_RENT_FOR_AMENDMENT = "fetchContractBudgetRentForAmendment";// S368
	public static final String FETCH_CONTRACT_BUDGET_MODIFICATION_RENT_AMOUNT = "fetchContractBudgetModificationRentAmount";// S368
	public static final String GET_COUNT_FOR_RENT_MOD = "getCountForRentModification";
	public static final String INSERT_RENT_MOD_DETAILS = "insertRentModDetails";
	public static final String CBF_GET_BASE_CONTRACT_BUDGET_MODIFICATION_RENT = "getBaseContractBudgetModificationRent";
	public static final String CBF_INSERT_CONTRACT_BUDGET_MODIFICATION_RENT = "insertContractBudgetModificationRent";
	public static final String FETCH_CONTRACT_BUDGET_MODIFICATION_RENT_NEW = "fetchContractBudgetModificationRentNew";
	public static final String EDIT_RENT_MODIFICATION = "editRentModification";
	public static final String CBY_DEL_RENT_MODIFICATION = "delRentModification";
	public static final String QRY_GET_REMAINING_AMOUNT_MODIFICATION_RENT = "getRemainingAmountModificationRent";
	public static final String GET_REMAINING_AMOUNT_RENT_IN_MULTIPLE_AMENDMENTS = "getRemainingAmountRentInMultipleAmendments";

	public static final String QRY_GET_REMAINING_AMOUNT_MODIFICATION_CONTRACTED_SERVICES = "getRemainingAmountModificationContractedServices";

	public static final String INSERT_INDIRECT_RATE = "insertIndirectRate";
	public static final String NOT_AUTHORIZE = "<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>";

	public static final String JSP_INVOICE_ASSIGN_ADVANCETABLE = "jsp/invoice/assignAdvanceTable";
	public static final String FILE_DOCUMENT_TITLE = "DOCUMENT_TITLE";
	public static final String FILE_TYPE = "FileType";
	public static final String PDF_FILE_TYPE = "PDF";
	public static final String CONTENT_ELEMENT = "ContentElements";
	public static final String DET_LAUNCH_PROP_WE = "getDetailsToLaunchAcceptProposalWF";
	public static final String FETCH_EVAL_SCORE_DETAIL = "fetchEvaluationScoreDetail";
	public static final String FETCH_SEL_COMMENT_FOR_AWARD_TASK = "fetchSelectionCommentsForAwardTask";
	public static final String PROC_EPIN = "procurementEpin";
	public static final String PENDING = "Pending";
	public static final String LAST_PUBLISHED_DATE = "lastPublishedDate";
	public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd hh:mm:ss";
	public static final String APPROVAL_DATE = "approvalDate";
	public static final String VIEW_EVAL_SUMMARY = "View Evaluation Summary";
	public static final String ASSIGN_STATUS = "assignStatus";
	public static final String RETURNED = "Returned";
	public static final String PROC_STA_ID = "procurementStatusId";
	public static final String DOC_STAT_ID = "documentStatusId";
	public static final String PROP_STATUS_READ_ONLY = "proposalDocScreenReadOnlyRule";
	public static final String PROPDOC_STATUS_COMPLETE = "proposalDocStatusCompletedRule";
	public static final String LAUNCH_WF_CONTRACT_UPDATE = "launchWFContractUpdate";
	public static final String CS_CHECK_UPDATE_CONTRACT_DETAILS = "checkContractDetails";
	public static final String GET_UPDATE_TYPE_CONTRACT_RECORD = "getUpdateTypeContractRecord";
	public static final String BMC_FETCH_FY_PLANNED_AMOUNT_FOR_UPDATED = "fetchPlannedAmtForUpdatedContractId";
	public static final String CS_CHECK_BUDGET_DETAILS = "checkBudgetDetails";
	public static final String CS_AMENDMENT_CHECK_BUDGET_DETAILS = "checkAmendmentBudgetDetails";
	public static final String COA_UPDATED_AMT_CANNOT_MORE_ACTUAL_AMT = "CHART_UPDATED_ACCOUNTS_NOT_GREATER_ACTUAL_AMT";
	public static final String UPDATED_BUDGET_ID = "updatedBudgetId";
	public static final String RFP_DOC_SCREEN_READ_ONLY = "rfpDocumentScreenReadonlyRule";
	public static final String FETCH_COMMENT_DET = "fetchcommentsDetails";
	public static final String VIEW_COMMENT_SECTION = "viewSelectionComments";
	public static final String XION = "xIcon";
	public static final String CANCEL_LOWERCASE = "cancel";
	public static final String STATUS_MARK_NON_RESPONSIVE = "25";
	public static final String ORGANIZATION_ID_LOWERCASE = "organization_id";
	public static final String LS_ORGANIZATION_ID = "lsOrganizationId";
	public static final String LS_EPIN_ID = "lsEpinId";
	public static final String LO_PARAM_MAP = "loParamMap";
	public static final String ASSIGN_AWARD_PIN = "assignAwardEPin";
	public static final String STAFF_ID = "staffId";
	public static final String CREATED_BY = "createdBy";
	public static final String ACTIVE_FLAG = "activeFlag";
	public static final String AO_PROPOSAL_DETAIL_MAP = "aoProposalDetailMap";
	public static final String PROPOSAL_DET = "ProposalDetails";
	public static final String ADD_NEW_PROP_FAIL = "addNewProposalFailed";
	public static final String DOUBLE_UNDER_SCORE = "--";
	public static final String EVAL_SUMMARY = "evaluationSummary";
	public static final String EVAL_SUMMARY_SCREEN_PATH = "evaluation/evaluationSummary";
	public static final int INTEGER_5 = 5;
	public static final String ERROR_PAGE_JSP = "jsp/error/errorpage";

	public static final Map<String, String> INDIRECT_RATE_FETCH_QUERY_MAP = new HashMap<String, String>();
	static
	{
		INDIRECT_RATE_FETCH_QUERY_MAP.put(TWO, CBY_FETCH_INDIRECT_RATE);
		INDIRECT_RATE_FETCH_QUERY_MAP.put(THREE, CBY_FETCH_INDIRECT_RATE_MODIFICATION);
		INDIRECT_RATE_FETCH_QUERY_MAP.put(FOUR, CBY_FETCH_INDIRECT_RATE_MODIFICATION);
		INDIRECT_RATE_FETCH_QUERY_MAP.put(ONE, CBY_FETCH_INDIRECT_RATE_AMENDMENT);
	}

	public static final Map<String, String> INDIRECT_RATE_UPDATE_PERCENTAGE_QUERY_MAP = new HashMap<String, String>();
	static
	{
		INDIRECT_RATE_UPDATE_PERCENTAGE_QUERY_MAP.put(ONE, CBY_UPDATE_INDIRECT_RATE_MODI_PERCENTAGE);
		INDIRECT_RATE_UPDATE_PERCENTAGE_QUERY_MAP.put(TWO, CBY_UPDATE_INDIRECT_RATE_PERCENTAGE);
		INDIRECT_RATE_UPDATE_PERCENTAGE_QUERY_MAP.put(THREE, CBY_UPDATE_INDIRECT_RATE_MODI_PERCENTAGE);
		INDIRECT_RATE_UPDATE_PERCENTAGE_QUERY_MAP.put(FOUR, CBY_UPDATE_INDIRECT_RATE_MODI_PERCENTAGE);
	}

	public static final Map<String, String> INDIRECT_RATE_UPDATE_QUERY_MAP = new HashMap<String, String>();
	static
	{
		INDIRECT_RATE_UPDATE_QUERY_MAP.put(TWO, "updateIndirectRate");
		INDIRECT_RATE_UPDATE_QUERY_MAP.put(THREE, CBY_UPDATE_INDIRECT_RATE_MODIFICATION);
		INDIRECT_RATE_UPDATE_QUERY_MAP.put(FOUR, CBY_UPDATE_INDIRECT_RATE_MODIFICATION);
		INDIRECT_RATE_UPDATE_QUERY_MAP.put(ONE, CBY_UPDATE_INDIRECT_RATE_MODIFICATION);
	}
	public static final String IS_REQUESTING_FROM_FINANCE_SCREEN = "isFromFinance";
	public static final String FETCH_AWARD_DETAILS_FOR_FINANCE = "fetchAwardDetailsForFinance";
	public static final String TAB = "tab";
	public static final String IS_SAME_AGENCY = "isSameAgency";
	public static final String IS_AWARD_APPROVED = "isAwardApproved";
	public static final String IS_AWARD_APPROVED_FOR_EVAL_POOL = "isAwardApprovedForEvalPool";
	public static final String IS_PROC_AGENCY = "isProcuringAgency";
	public static final String PROVIDR_USER_ID = "providerUserId";
	public static final String GET_PROC_DETAIL_FOR_NAV = "getProcurementDetailsForNav";
	public static final String OUTPUT_MAP = "outputMap";
	public static final String PROC_PATH = "procurement/";
	public static final String PROCUREMENT_ROADMAP_NOTIFICATION_LINK = "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false&app_menu_name=header_procurement&resetSessionProcurement=true";

	public static final String LO_EVALUATOR_INTERNAL_LIST = "loEvaluatorsInternalList";
	public static final String LO_EVALUATOR_EXTERNAL_LIST = "loEvaluatorsExternalList";
	public static final String DELETE_EVALUATION_SCORE_INTERNAL = "deleteFromEvaluationScoreInternal";
	public static final String DELETE_EVALUATION_STATUS_INTERNAL = "deleteFromEvaluationStatusInternal";
	public static final String DELETE_EVALUATION_INTERNAL = "deleteEvaluatorInternalAfterEvaluation";

	public static final String DELETE_EVALUATION_SCORE_EXTERNAL = "deleteFromEvaluationScoreExternal";
	public static final String DELETE_EVALUATION_STATUS_EXTERNAL = "deleteFromEvaluationStatusExternal";
	public static final String DELIMETER_SIGN = "~";
	public static final String DROPDOWN_BLANK_FORMAT = ":;";
	public static final String REQUEST_SCORE_AMENDMENT_FLAG = "lbRequestAmendmentFlag";

	public static final String UPDATE_AWARD_DETAILS_FROM_TASK = "updateAwardDetailsFromTask";
	public static final String INSERT_CONTRACT_DETAILS_FROM_AWARD_TASK = "insertContractDetailsFromAwardTask";
	public static final String FETCH_PROVIER_PROP_HEADER = "fetchProviderProposalHeader";
	public static final String VERIFY_EXTERNAL_USER_AGENCY = "verifyExternalUserAgency";
	public static final String CHECK_DB_DOWNLOAD_ALLOWED = "checkDBDDownloadAllowed";
	public static final String DOCUMENT_IDENTIFIER_ID = "DOCUMENT_IDENTIFIER_ID";
	public static final String DOCUMENT_TYPE_INVOICE = "DOCUMENT_TYPE";
	public static final String UPDATE_SCORE_DET = "updateScoreDetails";
	public static final String SHOW_PROPOSAL_COMMENT = "ShowProposalComments";
	public static final String FETCH_INVOICE_ADVANCES_DETAILS = "fetchInvoiceAdvancesDetails";
	public static final String VALIDATE_BUDGET_ADVANCE_STATUS = "validateBudgetAdvanceStatus";
	public static final String VALIDATE_INVOICE_ADVANCE_RECOUP_AMOUNT = "validateInvAdvanceRecoupAmount";
	public static final String FETCH_INVOICE_RECOUP_AMOUNT = "fetchInvoiceRecoupAmount";
	public static final String HASH_KEY_TOTAL_RECOUPED_AMOUNT = "TOTAL_RECOUPED_AMOUNT";
	public static final String HASH_KEY_AMOUNT_RECOUPED = "AMOUNT_RECOUPED";
	public static final String EDIT_INVOICE_ADVANCES_RECOUPED = "editInvoiceAdvanceRecouped";
	public static final String INSERT_INVOICE_ADVANCES_RECOUPED = "insertInvoiceAdvanceRecouped";
	public static final String MODEL_CB_ADVANCESUMMARY_BEAN = "com.nyc.hhs.model.AdvanceSummaryBean";
	public static final String MSG_KEY_INVOICE_RECOUP_AMOUNT_MORE_THAN_ADVANCE = "recoupmentExceedsAdv";
	public static final String MSG_KEY_INVOICE_RECOUP_AMOUNT_UNDER_REVIEW = "recoupmentExceedAdvUnderReview";
	public static final String HASH_KEY_TOTAL_RECOUPED_AMOUNT_REVIEW = "TOTAL_RECOUPED_REVIEW";
	public static final String MSG_KEY_RECOUP_ONLY_FOR_DISBURSED = "recoupOnlyForDisbursed";
	public static final String ADVANCE = "advance";
	public static final String GET_OPERATION_ADVANCE_PAGE_DATA = "getAdvanceSupportPageData";
	public static final String CBY_FETCH_ADVANCE_SUMMARY = "fetchAdvanceDetails";
	public static final String CBY_FETCH_ADVANCE_SUMMARY_FOR_PARENT_BUDGET = "fetchAdvanceDetailsForParentBudget";
	public static final String UPDATE_PROPOSAL_PREVIOUS_STATUS = "updateProposalPreviousStatus";
	public static final String AGENCY_WORKFLOW_MODEL_VIEW = "agencyWorkflow/reviewScore";
	public static final String CBS_NEW_GET_INDIRECT_RATE_COUNT = "getIndirectRateCount";
	public static final String CBS_GET_FRINGE_BENEFIT_COUNT = "getFringeBenefitCount";
	public static final String JSP_PATH_CONTRACT_FY_BUDGET = "jsp/contractbudget/contractFYBudget";
	public static final String FETCH_DOC_FOR_AWARD_TASK = "fetchDocumentsForAwardDocTask";
	public static final String SAVE_AWARD_DOC_TYPE = "saveAwardDocumentTypes";
	public static final String GET_REMAINING_AMOUNT_INDIRECT_RATE = "getRemainingAmountIndirectRate";
	public static final String STATUS_CLOSED = "Closed";
	public static final String STATUS_REGISTERED = "Registered";
	public static final String FETCH_PROC_TITLE_ORG_ID = "fetchProcTitleAndOrgId";

	public static final String FETCH_AWARD_DOCUMENTS = "fetchAwardDocuments";
	public static final String QRY_GET_REMAINING_AMOUNT_MODIFICATION_RATE = "getRemainingAmountModificationRate";
	public static final String QRY_GET_REMAINING_AMOUNT_RATE_IN_MULTIPLE_AMENDMENTS = "getRemainingAmountRateInMultipleAmendments";
	public static final String WIDTH = "width";
	public static final String CBM_FETCH_MILESTONE_DETAILS_FOR_VALIDATION = "fetchMilestoneDetailsForValidation";
	public static final String CBM_FETCH_MILESTONE_DETAILS_FOR_VALIDATION_IN_MULTIPLE_AMENDMENTS = "fetchMilestoneDetailsForValidationInMultipleAmendments";
	public static final String CBM_FETCH_EQUIPMENT_DETAILS_FOR_VALIDATION = "fetchEquipmentDetailsForValidation";
	public static final String CBM_FETCH_EQUIPMENT_DETAILS_FOR_VALIDATION_IN_MULTIPLE_AMENDMENTS = "fetchEquipmentDetailsForValidationInMultipleAmendments";
	public static final String CBM_FETCH_OTPS_DETAILS_FOR_VALIDATION = "fetchOTPSDetailsForValidation";
	public static final String CBM_FETCH_OTPS_DETAILS_FOR_VALIDATION_IN_MULTIPLE_AMENDMENTS = "fetchOTPSDetailsForValidationInMultipleAmendments";

	public static final String TYPE_UPDATED = "Updated";
	public static final String PROC_CERT_TASK_SCREEN = "procCerTaskScreen";
	public static final String ERROR_PAGE_CLOSE_FINALIZE = "errorPageCloseFinalize";
	public static final String GET_PROP_STAT_ID = "getProposalStatusId";
	public static final String MODEL_VIEW_PROVIDER_PROPOSAL_DET = "provider/proposalDetails";
	public static final String PROCUREMENT_SUMMARY_AGENCY_URL = "/portal/hhsweb.portal?_nfpb=true&_windowLabel=portletInstance_38&_urlType=action&topLevelFromRequest=ProcurementRoadmapDetails&midLevelFromRequest=ProcurementSummary&render_action=viewProcurement&submit_action=navigationAction&_pageLabel=portlet_hhsweb_portal_page_procurement&procurementId=";
	public static final String PROCUREMENT_SUMMARY_PROVIDER_URL = "/portal/hhsweb.portal?_nfpb=true&_windowLabel=portletInstance_38&_urlType=action&topLevelFromRequest=ProcurementSummaryHeader&render_action=procurementDetails&submit_action=navigationAction&_pageLabel=portlet_hhsweb_portal_page_procurement&procurementId=";
	public static final String CONTRACT_LIST_URL = "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=false&app_menu_name=header_financials";
	public static final String RFP_DOCUMENTS_AGENCY_URL = "/portal/hhsweb.portal?_nfpb=true&_windowLabel=portletInstance_38&_urlType=action&topLevelFromRequest=ProcurementInformation&midLevelFromRequest=RFPDocuments&render_action=displayRFPDocumentList&forAction=rfpRelease&submit_action=navigationAction&_pageLabel=portlet_hhsweb_portal_page_procurement&procurementId=";
	public static final String RFP_DOCUMENTS_PROVIDER_URL = "/portal/hhsweb.portal?_nfpb=true&_windowLabel=portletInstance_38&_urlType=action&topLevelFromRequest=RFPDocumentsHeader&render_action=displayRFPDocumentList&forAction=rfpRelease&submit_action=navigationAction&_pageLabel=portlet_hhsweb_portal_page_procurement&procurementId=";
	public static final String PROPOSAL_SUMMARY_URL = "/portal/hhsweb.portal?_nfpb=true&_windowLabel=portletInstance_38&_urlType=action&topLevelFromRequest=ProposalSummary&render_action=proposalSummary&forAction=propEval&submit_action=navigationAction&_pageLabel=portlet_hhsweb_portal_page_procurement&procurementId=";
	public static final String SELECTION_DETAILS_URL = "/portal/hhsweb.portal?_nfpb=true&_windowLabel=portletInstance_38&_urlType=action&topLevelFromRequest=SelectionDetails&render_action=viewSelectionDetails&forAction=selectionDetail&submit_action=navigationAction&_pageLabel=portlet_hhsweb_portal_page_procurement&procurementId=";
	public static final String SELECTION_DETAILS_BUDGET_LIST_URL = "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=budgetListAction";
	public static final String CONTRACT_BUDGET_REVISIONS_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_contract_budget_page&budgetType=Contract%20Budget";

	public static final String CONTRACT_BUDGET_ACTIVE_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_contract_budget_page&budgetType=Contract%20Budget";

	public static final String INVOICE_REVISIONS_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_contract_invoice_page&invoiceId=";
	public static final String PAYMENT_DISBURSED_URL = "/portal/hhsweb.portal?_nfpb=true&_st=&_windowLabel=portletInstance_37&_urlType=render&wlpportletInstance_37_action=paymentListAction";
	public static final String ADVANCE_REJECTED_URL = "/portal/hhsweb.portal?_nfpb=true&_st=&_windowLabel=portletInstance_37&_urlType=render&wlpportletInstance_37_action=budgetListAction";
	public static final String BUDGET_LIST_URL = "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_st=&_windowLabel=portletInstance_37&_urlType=render&wlpportletInstance_37_action=budgetListAction";

	public static final String COMPLETE_CONTRACT_BUDGET_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_contract_budget_page&budgetType=Contract%20Budget";
	public static final String COMPLETE_CONTRACT_BUDGET_UPDATE_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_page_budget_modification&budgetType=Budget%20Update";
	public static final String CONTRACT_BUDGET_UPDATE_REVISIONS_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_page_budget_modification&budgetType=Budget%20Update";
	public static final String CONTRACT_BUDGET_MODIFICATION_REVISION_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_page_budget_modification&budgetType=Budget%20Modification";
	public static final String COMPLETE_CONTRACT_BUDGET_AMENDMENT_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_page_budget_amendment&budgetType=Budget%20Amendment";
	public static final String CONTRACT_BUDGET_AMENDMENT_REVISION_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_page_budget_amendment&budgetType=Budget%20Amendment";
	public static final String CONTRACT_BUDGET_AMENDMENT_APPROVED_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_page_budget_amendment&budgetType=Budget%20Amendment";
	public static final String ADVANCE_APPROVED_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_contract_budget_page&budgetType=Contract%20Budget";
	public static final String FMS_PAYMENT_REJECTED_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_page_payment_detail&payment_id=11112";
	public static final String AMENDMENT_REGISTERED_URL = "/portal/hhsweb.portal?_nfpb=true&_st=&_windowLabel=portletInstance_37&_urlType=render&wlpportletInstance_37_action=budgetListAction";
	public static final String CONTRACT_BUDGET_MODIFICATION_APPROVED_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_contract_budget_page&budgetType=Contract%20Budget";
	public static final String AGENCY_TASK_INBOX_URL = "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox";
	public static final String AWARDS_CONTRACTS_URL = "/portal/hhsweb.portal?_nfpb=true&_windowLabel=portletInstance_38&_urlType=action&topLevelFromRequest=AwardsandContracts&render_action=awardsAndContracts&forAction=awardContract&submit_action=navigationAction&_pageLabel=portlet_hhsweb_portal_page_procurement&procurementId=";
	public static final String TRANSACTION = " Transaction ";
	public static final String MILLISECONDS = " milliseconds.";
	public static final String TOOK = " took ";
	public static final String SERVICE = " Service ";
	public static final String ENTERING = "ENTERING ";
	public static final String COLON_AOP = " : ";
	public static final String WITH = " with (";
	public static final String SPACE = " ";
	public static final String EXITING = "EXITING ";
	public static final String ERROR_TRACED = "Error Traced: ";

	public static final String FILE_UPLOAD_FAIL_MESSAGE = "M04";
	public static final String FILE_UPLOAD_PASS_MESSAGE = "M03";
	public static final String AWARDAMT = "AWARDAMT";
	public static final String AWARDCOUNT = "AWARDCOUNT";
	public static final String CONTEXT_PATH1 = "contextPath";
	public static final String DOWNLOAD_DB_PROCESS = "downloadDBDProcess";
	public static final String ZIP_PATH = "zipPath";
	public static final String DBD_MSG1 = "DBD Docs cannot be downloaded";
	public static final String CBM_FETCH_UTILITIES_DETAILS_FOR_VALIDATION = "fetchUtilitiesDetailsForValidation";
	public static final String CBM_FETCH_UTILITIES_DETAILS_FOR_VALIDATION_IN_MULTIPLE_AMENDMENT = "fetchUtilitiesDetailsForValidationInMultipleAmendments";
	public static final String AGENCY_WF = "agencyWorkflow/agencyWorkflow";
	public static final String DOUBLE_QUOTE = " '";
	public static final String TASK_TYPE = "taskType";
	public static final String CONFIG_AWARD_DOC = "Configure Award Documents";
	public static final String REVIEW_SCORES = "Review Scores";
	public static final String FETCH_REVIEW_SCORE_TASK = "fetchReviewScoresTask";
	public static final String GET_ORG_IDS_FOR_SELECTED_PROPOSAL = "getOrgIdsForSelectedProposals";
	public static final String LIST_OF_PROVIDERS = "ListOfProviders";

	// base controller constants start
	public static final String INTERNAL_ERROR_OCCURED_WHILE_PROCESSING_YOUR_REQUEST = "Internal Error Occured While Processing Your Request";
	public static final String INVOICE_NAME = "invoice";
	public static final String BUDGET = "budget";
	public static final String MODIFICATION = "modification";
	public static final String CONTRACT_BUDGET_NAME = "contractBudget";
	public static final String LS_INTERNAL_COMMENT = "lsInternalComment";
	public static final String LS_PUBLIC_COMMENT = "lsPublicComment";
	public static final String LS_REASSIGN_USER_NAME = "lsReassignUserName";
	public static final String LS_REASSIGN_USER_ID = "lsReassignUserId";
	public static final String LS_TASK_TYPE = "lsTaskType";
	public static final String PROC_FUNDING_OPERATION_GRID = "procFundingOperationGrid";
	public static final String FUNDING_OPERATION_GRID = "fundingOperationGrid";
	public static final String ADD_DOCUMENT_FROM_VAULT_ACTION = "addDocumentFromVaultAction";
	public static final String DOCUMENT_FINAL_UPLOAD_ACTION = "documentFinalUploadAction";
	public static final String SAVE_DOCUMENT_PROPERTIES_ACTION = "saveDocumentPropertiesAction";
	public static final String SHOW_ACCOUNT_SUB_GRID = "showAccountSubGrid";
	public static final String SHOW_FUNDING_SUB_GRID = "showFundingSubGrid";
	public static final String TOTAL_STRING = ",'Total'";
	public static final String FINISH_TASK_RETURN = "finishTaskReturn";
	public static final String FINISH_TASK_APPROVE = "finishTaskApprove";

	public static final int GET_ACCOUNTS_MAIN_HEADER = 5;
	public static final int GET_FUNDING_SUB_GRID_PROP = 3;
	public static final int GET_FUNDINGS_MAIN_HEADER_PROP = 2;
	public static final int GET_FUNDING_MAIN_HEADER = 1;
	public static final int GET_ACCOUNTS_MAIN_HEADER_PROP = 4;
	public static final int GET_FISCAL_YEAR_GRID = 3;
	public static final int GET_FISCAL_YEAR_SUB_GRID_PROP = 2;
	public static final int GET_FISCAL_YEAR_HEADER_PROP = 1;
	public static final int GET_FISCAL_YEAR_HEADER = 4;
	public static final int COLUMNS_FOR_TOTAL_COUNT = 5;
	public static final String LS_WOB_NUM = "lsWobNum";
	// base controller constants end

	public static final String CBM_GET_BASE_UNIT_EQUIPMENT = "getBaseUnitForEquipment";
	public static final String ERROR_MESSAGE_UPDATE_PROFSERVICE_ID = "errorMessageUpdateProfServiceBeanIds";

	public static final String ADDENDUM_DOC_FAIL = "addendumDocFail";
	public static final String SUCCESS_ADDENDUM = "successAddendum";
	public static final String GET_MEMBER_DETAILS_STRING_LTERAL1 = "{\"memberData\": [{";
	public static final String GET_MEMBER_DETAILS_STRING_LTERAL2 = "\": \"";
	public static final String GET_MEMBER_DETAILS_STRING_LTERAL3 = "\", ";
	public static final String GET_MEMBER_DETAILS_STRING_LTERAL4 = "}]}";
	public static final String DISPLAY_DOC_LIST_FILENET_TRANS_NAME = "displayDocList_filenet";
	public static final String FETCH_EVALUATION_SETTINGS_INTERNAL = "fetchEvaluationSettingsInternal";
	public static final String SAVE_EVALUATION_TASK_DETAILS = "saveEvaluationStatusTaskDetails";
	public static final String ERROR_OCCURED_WHILE_FETCHING_REVIEW_AWARD_COMMENTS = "Error occured while fetching review award comments";

	public static final String FILENET_FILTER_MAP = "filterMap";
	public static final String FILENET_ORDER_BY_MAP = "orderByMap";
	public static final String INCLUDE_FILENET_FILTER = "includeFilter";
	public static final String DISPLAY_PROPOSAL_DOC_LIST_TRANS_NAME = "displayProposalDocList";
	public static final String HM_REQIRED_PROPERTY_MAP = "hmReqProps";
	public static final String AO_DOC_ID_LIST = "aoDocIdList";
	public static final String DISPLAY_DOC_PROPERTIES_FILENET_TRANS_NAME = "displayDocProp_filenet";
	public static final String DOCUMENT_PROPERTY_HASH_MAP = "documentPropHM";
	public static final String AO_DOC_TYPE_XML = "aoDocTypeXML";
	public static final String AO_DOC_TYPE_DOM = "aoDocTypeDOM";

	public static final String TO_ACTION = "toAction";
	public static final String FORWARD_SLASH = "/";
	public static final String DOUBLE_FORWARD_SLASHES = "//";

	public static final String CBY_ORIGINAL_FETCH_CONTRACT_COUNT = "fetchOriginalContractCount";

	public static final String CBY_CREATE_CONTRACT_REPLICA = "createContractReplica";

	public static final String CBY_CREATE_BUDGET_REPLICA = "createBudgetReplica";

	public static final String CBY_FETCH_SUB_BUDGET_LIST = "fetchSubBudgetList";

	public static final String CBY_MOD_SEASONAL_EMPLOYEE_GRID_EDIT = "seasonalEmployeeGridModificationEdit";
	public static final String CBY_MOD_HOURLY_EMPLOYEE_GRID_EDIT = "hourlyEmployeeGridModificationEdit";
	public static final String CBY_MOD_SALARIED_EMPLOYEE_GRID_EDIT = "salariedEmployeeGridModificationEdit";
	public static final String BUDGET_MODIFICATION_PS_SAL_UNIT_VALIDATION = "BUDGET_MODIFICATION_PS_SAL_UNIT_VALIDATION";
	public static final String BUDGET_MODIFICATION_PS_HOURLY_UNIT_VALIDATION = "BUDGET_MODIFICATION_PS_HOURLY_UNIT_VALIDATION";
	public static final String DBD_DOCUMENTS = "/DBD Documents";
	public static final String AMENDMENT_DOCUMENTS = "_Amendment Documents";
	public static final String AWARD_DOCUMENT_PDF = "_Award_Documents";
	public static final String AMENDMENT_SUMMARY = "Amendment COF and Budget Summary Documents";
	public static final String CONTRACT_SUMMARY = "Contract COF and Budget Summary Documents";
	public static final String FOLDER_NAME = "asFolderName";
	public static final String ZIP = ".zip";
	public static final String DOT = ".";
	public static final String MESSAGE_FORMAT = "{1}({0})";
	public static final String UPDATE_PROPOSAL_STATUS_FROM_TASK = "updateProposalStatusFromTask";
	public static final String GET_PROPOSAL_REVIEW_STATUS_ID = "getProposalReviewStatusId";
	public static final String INS_AGENCY_SETTING_BEAN = "aoInsertAgencySettingsBean";
	public static final String LO_INS_AGENCY_SETTING_BEAN = "loInsertAgencySettingsBean";
	public static final String INS_AWARD_DOC_DET = "insertAwardDocumentDetails";
	public static final String CB_EQUIP_BEAN = "aoCBEquipmentBean";
	public static final String BUDGET_ID_PASSED = "BudgetId passed: ";
	public static final String CB_GRID_BEAN = "CBGridBean : ";
	public static final String AO_RENT = "aoRent";
	public static final String FETCH_OP_SUPPORT_PAGE_DATA = "fetchOpAndSupportPageData";
	public static final String CONTRACTID = "ContractId ";
	public static final String CBM_NEW_CONTRACTED_SERVICES = "ContractedServices : ";
	public static final String CB_GRID_BEAN_LIST = "loCBGridBeanList";
	public static final String COMMA_SPACE = ", ";
	public static final String SQUARE_BRAC_END = "]";
	public static final String THIS_COMMA = "',this, '";
	public static final String BRACKET_CLOSE_JS = "')\"";
	public static final String BRACKET_CLOSE_JS_1 = "')";
	public static final String GET_INVOICE_AMOUNT_FOR_MODIFICATION = "getInvoiceAmountForModification";
	public static final String AMEND_CONTRACT_ID = "AmendContractId";
	public static final String EPIN_KEY = "Epin ";
	public static final String BA_EXP_DATE = "baExpDate";
	public static final String FILLING_EXP_DATE = "filingExpDate";
	public static final String PROC_WIDGET = "ProcurementWidget";
	public static final String FETCH_PROVIDER_WIDGET_DET = "fetchProviderWidgetDetails";
	public static final String LO_PROVIDER_MAP = "loProviderMap";
	public static final String PROVIDER_STATUS = "PROVIDER_STATUS";
	public static final String PROVIDER_STATUS_ID = "providerStatusId";
	public static final String PROC_WIDGET_HASHMAP = "procurementWidgetHashMap";
	public static final String LAST_MODIFIED_DATE = "LAST_MODIFIED_DATE";
	public static final String LAST_PUBLISHED_DATE_VALUE = "LAST_PUBLISHED_DATE";
	public static final String MODIFIED_BY_USERID = "MODIFIED_BY_USERID";
	public static final String LAST_PUBLISHED_USERID = "LAST_PUBLISHED_USERID";
	public static final String STAFF = "staff";
	public static final String EVALUATION_SCORE_STR = "evaluationScore";
	public static final String RANK = "rank";
	public static final String EVALUATE_WORKFLOW_NUMBERS = "EvaluateWorkflowNumbers";

	public static final String PROPDOC_STATUS_SUBMITTED_RULE = "proposalDocStatusSubmittedRule";
	public static final String REPLACING_DOCUMENT_ID = "replacingDocumentId";
	public static final String UPDATE_PROPOSAL_DOC_DETAILS = "updateProposalDocDetails";
	public static final String HIDDEN_DOCUMENT_ID = "hiddenDocumentId";
	public static final String PROC = "proc";
	public static final String POSITIVE = "positive";
	public static final String NEGATIVE = "negative";
	public static final String PERCENTAGE_TEMPLATE = "percentageTemplate";
	public static final String MMDDYY_FORMAT_2 = "MM/dd/yy";
	public static final String BUD_DOT = "bud.";
	public static final String INV_DOT = "inv.";
	public static final String AS_WOB_NBR = "asWobNumber";
	public static final String AS_USER_NAME = "asUserName";
	public static final String AS_QUEUE_NAME = "asQueueName";
	public static final String REQWORKFLOW_PROP = "aoReqdWorkflowProperties";
	public static final String AS_WORK_FLOW_NAME = "asWorkflowName";
	public static final String COUNT = "count";
	public static final String TASK_STATUS = "TaskStatus";
	public static final String F_WOB_NUM = "F_WobNum";
	public static final String FILE_NET_SESSION = "filenetSession";
	public static final String AS_WOB_NO = "asWobNo";
	public static final String TO_STR = "to";
	public static final String FETCH_PROC_ADDENDUM_DATA = "fetchProcurementAddendumData";
	public static final String PROC_ADDENDUM_DATA_COUNT = "procurementAddendumDataCount";
	public static final String UNPUBLISHED_DATA_MSG_KEY = "unPublishedDataMsg";
	public static final String UNPUBLISHED_DATA_MSG = "There is information related to this procurement that has not yet been published or released to the Providers.";
	public static final String AS_TRANSACTION_DATE = "asTransactionDate";
	public static final String USER_ID_2 = "UserId";
	public static final String AS_EXCEPTION_NAME = "asExceptionName";
	public static final String AS_TRANSACTION_DETAILS = "asTransactionDetails";
	public static final String URL_TASK = "/WEB-INF/r2/jsp/tasks/task.jsp";
	public static final String URL_TASKS_DOCUMENT = "/WEB-INF/r2/jsp/tasks/document.jsp";
	public static final String AS_WOB_NBRS = "aoWobNumbers";
	public static final String AS_SESSION_USER_NAME = "asSessionUserName";
	public static final String AO_HM_REQ_WK_FLW_PROP = "aoHmReqdWorkflowProperties";
	public static final String AS_FINISH_STATUS = "asFinishStatus";
	public static final String EVAL_IDS = "evaluationIds";
	public static final String VW_VQ = "vwvq";

	public static final String SCORE_CRITERIA = "scoreCriteria";
	public static final String MAX_SCORE = "maximumScore";
	public static final String ANSWER_TEXT = "answerText";
	public static final String FIELD_REQUIRED = "fieldRequired";
	public static final String QUESTION_TEXT = "questionText";
	public static final String TOTAL_SERVICE = "totalNumberOfService";
	public static final String TOTAL_FUNDING_REQUEST = "totalFundingRequest";
	public static final String SITE_DETAILS_LIST = "siteDetailsList";
	public static final String QUES_ANS_BEAN_LIST_BRACKET = "questionAnswerBeanList[";
	public static final String EVAL_CRIT_BEAN_LIST_BRACKET = "loEvaluationCriteriaBeanList[";
	public static final String EXCEPTION_OCCURED_WHILE_UPDATING_UTILITIES = "Exception occured while updating Utilities";

	public static final String STR_PROGRAM_INCOME_ID = "programIncomeId";
	public static final String TRANSACTION_LOWESCASE = "transaction";
	public static final String RECACHE_TIMER = "recacheTimer";
	public static final String RECACHE_TIME_INTERVAL = "recacheTimerInterval";
	public static final char CHAR_E = 'E';
	public static final char CHAR_D = 'D';
	public static final String CRLI_BRACKT_START = "{";
	public static final String CRLI_BRACKT_END = "}";
	public static final String CRLI_BRACKT_END_COMMA = "},";
	public static final String AT_THE_RATE = "@";
	public static final String SQUARE_CRLI = "]}";
	public static final String MASTER_STATUS_LIST = "masterStatusList";
	public static final String STAFF_ROLE = "staff";
	public static final String UPDATE_AWARD_DOC_DETAILS = "updateAwardDocDetails";

	public static final String PAY_DOT = "pay.";
	public static final String CBM_FETCH_PS_DETAILS_FOR_VALIDATION = "fetchPSDetailsForValidation";
	public static final String CBM_FETCH_PS_DETAILS_FOR_VALIDATION_IN_MULTIPLE_AMENDMENT = "fetchPSDetailsForValidationInMultipleAmendments";
	public static final String CBM_SUB_BUDGET_ID = "SUB_BUDGET_ID";
	public static final String CBM_POSITION_ID = "POSITION_ID";
	public static final String CBM_REMAINING_AMT = "REMAININGAMOUNT";
	public static final String MODEL_ATTRIB_MEG_REQUIRED_FIELD = "! This field is required.";
	public static final double DOUBLE_ZERO = 0;
	public static final String MODEL_TAXONOMY_EDIT_TAGS = "Edit Tag(s)";
	public static final char CHAR_FORWARDSLASH = '/';
	public static final char CHAR_BACKSLASH = '\\';
	public static final String STRING_BACKSLASH = "\\";
	public static final char CHAR_COLON = ':';
	public static final String MODEL_BEAN_SCOREDETAILSBEAN = "ScoreDetailsBean";
	public static final String STRING_EQUAL = "='";

	// Home Procurement for City Starts
	public static final String CONTRACTS_PEND_CONFIG = "contractsPendingConfiguration";
	public static final String CONTRACTS_PEND_COF = "contractsPendingCOF";
	public static final String BUDGETS_AMEND_PEND_APPROVAL = "budgetsAmendmentPendApproval";
	public static final String BUDGETS_MOD_UPDATES_PEND_APPROVAL = "budgetsModUpdatesPendApproval";
	public static final String PAYMENTS_PEND_APPROVAL = "paymentsPendApproval";
	public static final String PAYMENTS_FMS_ERROR = "paymentsFMSError";

	// Home Financial for City Starts
	public static final String RFP_RELEASED_TEN_DAYS = "rfpReleased10Days";
	public static final String RFP_RELEASED_SIXTY_DAYS = "rfpReleased60Days";
	public static final String RFP_IN_RELEASED_STATUS = "rfpReleasedStatus";
	public static final String RFP_PROPOSAL_DUE_DATE_TEN_DAYS = "rfpProposalDueDate10Days";
	public static final String RFP_PROPOSAL_RECEIVED = "rfpProposalReceived";
	public static final String RFP_EVALUATION_COMPLETE = "rfpEvaluationComplete";
	public static final String RFP_SELECTION_MADE = "rfpSelectionMade";

	// Agency Inbox and Management
	public static final String PE_GRID_PAGE_SIZE = "20";
	public static final String AGENCY_TASK_CONFIG = "agencyTaskConfiguration";
	public static final String PROPERTY_HMP_SUBMITTED_FROM = "SubmittedFrom";
	public static final String PROPERTY_HMP_SUBMITTED_TO = "SubmittedTo";
	public static final String PROPERTY_HMP_ASSIGNED_FROM = "AssignedFrom";
	public static final String PROPERTY_HMP_ASSIGNED_TO = "AssignedTo";
	public static final String FISCAL_YEAR_DATE = "07/01/";
	public static final String OPOSTOPHI_COMMA = "',";
	public static final String RECTANGULAR_CLOSE_COMMA = "],";
	public static final String CBM_FETCH_FRINGE_AMOUNT_FOR_VALIDATION = "fetchFringeAmountForValidation";
	public static final String CBM_FETCH_FRINGE_AMOUNT_FOR_VALIDATION_IN_MULTIPLE_AMENDMENTS = "fetchFringeAmountForValidationInMultipleAmendments";
	public static final String TAXONOMY_TAGGING = "taxonomytagging";
	public static final String TAG_PROPOSAL_CONTRACT_LIST = "tagproposalscontractsList";
	public static final String SAVE_VALUE = "Save";
	public static final String SAVE_COMPLETE = "SaveandComplete";

	public static final String POSITIVE_AMENDMENT_MSG = "positiveAmendmentMsg";
	public static final String POSITIVE_AMENDMENT_ERROR_MESSAGE = "POSITIVE_AMENDMENT_ERROR_MESSAGE";
	public static final String NEGATIVE_AMENDMENT_MSG = "negativeAmendmentMsg";
	public static final String NEGATIVE_AMENDMENT_ERROR_MESSAGE = "NEGATIVE_AMENDMENT_ERROR_MESSAGE";
	public static final String NEGATIVE_AMENDMENT_VALIDATION_MESSAGE = "NEGATIVE_AMENDMENT_VALIDATION_MESSAGE";
	public static final String VALIDATE_AMENDMENT_AMOUNT = "validateAmendmentAmount";
	public static final String FETCH_AMENDMENT_AMOUNT_EXCEPT_CHANGED_ONE = "fetchAmendmentAmountExceptChangedOne";
	public static final String FETCH_FISCAL_YEAR_AMOUNT = "fetchFiscalYearAmount";
	public static final String FETCH_FISCAL_YEAR_AMOUNT_UPDATE_CONF = "fetchFiscalYearAmountUpdateConf";
	public static final String FETCH_BUDGET_AMOUNT = "fetchBudgetAmount";

	public static final String NO_PROPOSALS_FOR_AWARD_FLAG = "noProposalForAwardsFlag";
	public static final String SHOW_STAR = "star";
	public static final String SHOW_DOUBLE_STAR = "dStar";
	public static final String FETCH_AWARD_APPROVAL_DATE = "fetchAwardAppDate";

	public static final String MSG_KEY_SUBMIT_BUDGET_ERR = "submitBudgetErr";
	public static final String MSG_KEY_SUBMIT_BUDGET_MODIFY_ERR = "submitBudgetModificationErr";
	public static final String MSG_KEY_SUBMIT_BUDGET_UPDATE_ERR = "submitBudgetUpdateErr";
	public static final String MSG_KEY_SUBMIT_BUDGET_AMEND_ERR = "submitBudgetAmendErr";
	public static final String CBM_VALIDATE_SUBMIT_AMOUNT_TOTAL = "validateCBMAmountTotal";
	public static final String CBM_MAPPER_FETCH_MODIFICATION_AMOUNT_TOTAL = "fetchModificationAmountTotal";

	public static final String EVALUATION_SETTINGS_INSERT = "N";
	public static final String CBY_FETCH_CONTRACTED_SERVICES_NEW_AMENDMENT_CONSULTANTS = "fetchContractedServicesNewAmendmentConsultants";
	public static final String CBY_FETCH_CONTRACTED_SERVICES_AMENDMENT_CONSULTANTS = "fetchContractedServicesAmendmentConsultants";
	public static final String CBY_FETCH_CONTRACTED_SERVICES_AMENDMENT_VENDORS = "fetchContractedServicesAmendmentVendors";
	public static final String CBY_FETCH_CONTRACTED_SERVICES_AMENDMENT_SUB_CONTRACTORS = "fetchContractedServicesAmendmentSubContractors";
	public static final String CBY_ADD_CONTRACTED_SERVICES_AMENDMENT = "addContractedServicesAmendment";
	public static final String CBY_UPDATE_CONTRACTED_SERVICES_AMENDMENT = "updateContractedServicesAmendment";
	public static final String CBY_EDIT_CONTRACTED_SERVICES_AMENDMENT = "editContractedServicesAmendment";
	public static final String CBY_DEL_CONTRACTED_SERVICES_AMENDMENT = "delContractedServicesAmendment";
	public static final String CBY_FETCH_INSERT_CONTRACTED_SERVICES_AMENDMENT = "fetchInsertContractedServicesAmendment";
	public static final String FETCH_NONGRID_CONTRACTED_SERVICES_AMENDMENT = "getNonGridContractedServicesAmendment";
	public static final String QRY_GET_REMAINING_AMOUNT_AMENDMENT_CONTRACTED_SERVICES = "getRemainingAmountAmendmentContractedServices";
	public static final String QRY_GET_REMAINING_AMOUNT_AMENDMENT_CONTRACTED_SERVICES_IN_MULTIPLE_AMENDMENTS = "getRemainingAmountContractedServicesInMultipleAmendments";
	public static final String SELECTED_PROPOSAL_MESSAGE = "selectedproposalMsgFlag";
	public static final String SELECTED_PROPOSAL_MESSAGE_KEY = "selectedproposalMsgKey";
	public static final String SELECTED_PROPOSAL_MESSAGE_INFO = "No proposals were selected for award for this procurement's competition pool.";

	public static final String CBY_CREATE_SUB_BUDGET_REPLICA = "createSubBudgetReplica";
	public static final String CBY_CREATE_PS_REPLICA = "createPersonnelServiceReplica";
	public static final String CBY_CREATE_OPS_REPLICA = "createOperationAndSupportReplica";
	public static final String CBY_CREATE_UTILITIES_REPLICA = "createUtilitiesReplica";
	public static final String CBY_CREATE_PFS_REPLICA = "createProfessionalServiceReplica";
	public static final String CBY_CREATE_RENT_REPLICA = "createRentReplica";
	public static final String CBY_CREATE_CS_REPLICA = "createContractedServicesReplica";
	public static final String CBY_CREATE_RATE_REPLICA = "createRateReplica";
	public static final String CBY_CREATE_MILESTONE_REPLICA = "createMilestoneReplica";
	public static final String CBY_CREATE_UNALLOCATED_REPLICA = "createUnallocatedReplica";
	public static final String CBY_CREATE_INDIRECT_RATE_REPLICA = "createIndirectRateReplica";
	public static final String CBY_CREATE_PROGRAM_INCOME_REPLICA = "createProgramIncomeReplica";
	public static final String CBY_CREATE_FRINGE_REPLICA = "createFringeReplica";
	public static final String CBY_CREATE_EQUIPMENT_REPLICA = "createEquipmentReplica";

	// BULK upload constants -Start
	public static final String BULK_UPLOAD_STATUS_NOT_PROCESSED = "asFileStatusNotProcessed";
	public static final String BULK_UPLOAD_STATUS_IN_PROGRESS = "asFileStatusInProgreass";
	public static final String STATUS_BULK_UPLOAD_NOT_PROCESSED = "BULK_UPLOAD_NOT_PROCESSED";
	public static final String STATUS_BULK_UPLOAD_SUCCESS = "BULK_UPLOAD_SUCCESS";
	public static final String STATUS_BULK_UPLOAD_FAIL = "BULK_UPLOAD_FAIL";
	public static final String STATUS_BULK_UPLOAD_SUCCESS_WITH_ERROR = "BULK_UPLOAD_SUCCESS_WITH_ERROR";
	public static final String STATUS_BULK_UPLOAD_IN_PROGRESS = "BULK_UPLOAD_IN_PROGRESS";
	public static final String BULK_UPLOAD_TEMPLATE_MISMATCH = "BULK_UPLOAD_TEMPLATE_MISMATCH";
	public static final String BULK_UPLOAD_SYSTEM_FAILURE = "BULK_UPLOAD_SYSTEM_FAILURE";
	public static final String RENDER_ACTION_UPLOADBULKCONTRACTRESPONSE = "UploadBulkContractResponse";
	public static final String BULK_UPLOAD_RESULT = "loStatus";
	public static final String BULK_UPLOAD_MESSAGE = "bulkUploadMessage";
	public static final String BULK_UPLOAD_STATUS_SUCCESS = "successMessage";
	//[Start] R8.4.1 qc_9521 Update top level message for bulk upload
	public static final String BULK_UPLOAD_SUCCESS_MESSAGE = "The bulk template file has been uploaded. You will receive a notification email with a summary of the results.";
	//[End] R8.4.1 qc_9521 Update top level message for bulk upload
	public static final String BULK_UPLOAD_FAIL_MESSAGE = "File is not uploaded, Please check the file and try again.";
	public static final String BULK_UPLOAD_FILE_PROPERTIES_SAVE_QUERY = "insertBulkUploadDocumentProperties";
	public static final String BULK_UPLOAD_FILE_STATUS_UPDATE = "updateBulkUploadDocStatus";
	public static final String BULK_UPLOAD_FILE_LOCK_STATUS_UPDATE = "updateBulkUploadDocLockStatus";
	public static final String BULK_UPLOAD_FILE_STATUS_DOCUMENT_ID = "asDocId";
	public static final String BULK_UPLOAD_FILE_STATUS_DOCUMENT_STATUS = "asDocStatus";
	public static final String BULK_UPLOAD_FILE_NAME = "fileName";
	public static final String BULK_UPLOAD_FILE_MODIFIED_BY = "modifiedBy";
	public static final String BULK_UPLOAD_FILE_STATUS_ID = "statusId";
	public static final String BULK_UPLOAD_TRANSACTION_ID = "bulkContractUpload_filenet";
	public static final String BULK_UPLOAD_FILENET_UPLOAD_PATH = "/Bulk Upload/Files";
	public static final String BULK_UPLOAD_FILENET_UPLOAD_FILE_SIZE_ERROR = "The file must be less than 3Mb.";
	public static final String BULK_UPLOAD_FILENET_UPLOAD_FILE_NAME_ERROR = "The file name must be 50 characters or less.";
	public static final String BULK_UPLOAD_FILENET_UPLOAD_FILE_NAME_CHAR_ERROR = "The file name may only contain the following characters: a-z, A-Z, 0-9, -, and _";
	public static final String BULK_UPLOAD_NOT_FILE_SELECTED_ERROR = "You must select a file";
	public static final String BULK_UPLOAD_FILENET_UPLOAD_FILE_TYPE_ERROR = "The file uploaded must have extension of ";
	public static final String VALIDATE_EPIN_BULK = "validateEpinBulk";
	public static final String VALIDATE_PROVIDER_ACCELERATOR_BULK = "validateProviderInAcceleratorBulk";
	public static final String ADD_CONTRACT_DETAILS_BULK = "addContractDetailsBulk";
	public static final String BULK_UPLOAD_FILE_LOCK_PERIOD = "BULK_UPLOAD_LOCK_PERIOD";
	public static final String BULK_UPLOAD_FILE_LOCK_PERIOD_MULTIPLY = "multiplyBy";
	public static final String BULK_UPLOAD_EXCEL_FIRST_ROW = "BulkFirstRow";
	public static final String BULK_UPLOAD_EXCEL_FIRST_COLUMN = "BulkFirstCol";
	public static final String BULK_UPLOAD_EXCEL_COLUMN_COTRACT_TYPE = "Contract Type";
	//[Start] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
	public static final String BULK_UPLOAD_EXCEL_COLUMN_INFLIGHT_OPTION = "Inflight option";
	//[End] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
	public static final String BULK_UPLOAD_EXCEL_COLUMN_AWARD_EPIN = "Award EPIN";
	public static final String BULK_UPLOAD_EXCEL_COLUMN_AGENCY = "Agency";
	public static final String BULK_UPLOAD_EXCEL_COLUMN_ACC_PROGRAM_NAME = "Accelerator Program Name";
	public static final String BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_TITLE = "Contract Title";
	public static final String BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_VALUE = "Contract Value";
	public static final String BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_START_DATE = "Contract Start Date";
	public static final String BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_END_DATE = "Contract End Date";
	//[Start] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
	public static final String BULK_UPLOAD_EXCEL_COLUMN_BUDGET_FY_START_YEAR_NAME = "Budget FY Start Year";
	public static final String PATTERN_YEAR = "\\d{4}";
	public static final String NT_BULK_UPLOAD_ERR = "NT_BULK_UPLOAD_ERR";
	public static final String INFLIGHT_WITH_COF = "Inflight with CoF";
	public static final String INFLIGHT_WITHOUT_COF = "Inflight without CoF";
	//[End] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
	public static final String CONTRACT_TYPE_DESCRETIONARY = "Discretionary";
	public static final String CONTRACT_TYPE_INFLIGHT = "Inflight";
	public static final String CONTRACT_TYPE_DESC = "CONTRACT_TYPE_DESC";
	public static final String CONTRACT_TYPE_INFL = "CONTRACT_TYPE_INFL";
	public static final String TRANSACTION_GET_BULK_UNPROCESSED_FILE = "getBulkUploadUnprocessedFiles";
	public static final String TRANSACTION_GET_BULK_DOCUMENT_FILENET = "documentcontent_filenet_bulk";
	public static final String TRANSACTION_CONTRACT_BULK_UPLOAD_STATUS = "contractBulkUploadStatus";
	public static final String TRANSACTION_CONTRACT_BULK_UPLOAD_REVIEW_LEVEL = "getReviewLevelsBulk";
	public static final String TRANSACTION_FETCH_CONTRACT_DETAILS_BY_EPIN = "fetchContractDetailsByEPINforNewBulk";
	public static final String TRANSACTION_BULK_CONTRACT_UPLOAD_FILE_STATUS_UPDATE = "bulkContractUploadFileStatusUpdate";
	public static final String TRANSACTION_BULK_CONTRACT_UPLOAD_FILE_LOCK_STATUS_UPDATE = "updateBulkUploadDocLockStatus";
	public static final String QUERY_INSERT_BULK_UPLOAD_STATUS_BY_RECORD = "insertBulkUploadStatusByRecord";
	public static final String QUERY_GET_BULK_UPLOAD_FILE_INFO = "getBulkUploadFileInfo";
	public static final String BEAN_BULK_UPLOAD_CONTRACT_INFO = "com.batch.bulkupload.BulkUploadContractInfo";
	public static final String BULK_UPLOAD_DOCUMENT_CONTENT = "loOutputHashMap";
	public static final String BULK_UPLOAD_FLAG_ONE = "1";
	public static final String BULK_UPLOAD_FLAG_ZERO = "0";
	public static final String BULK_UPLOAD_SYSTEM_USER = "system";
	public static final String BULK_UPLOAD_CONTRACT_INFO = "aoBulkUploadContractInfo";
	public static final String LIST_BULK_UNPROCESSED_FILE = "loListUnprocessedFile";
	public static final String BULK_UPLOAD_DOCUMENT_ID = "asDocumentId";
	public static final String REVIEW_LEVEL_CERTIFICATION_OF_FUNDS = "8";
	public static final String BULK_UPLOAD_FILE_STATUS = "aoFileStatus";
	public static final String BULK_UPLOAD_FILE_STATUS_NOT_PROCESSED = "Not Processed";
	public static final String BULK_UPLOAD_FILE_STATUS_FAILED = "Failed";
	public static final String BULK_UPLOAD_FILE_STATUS_SUCCESS = "Success";
	public static final String BULK_UPLOAD_FILE_STATUS_SUCCESS_WITH_ERROR = "Success With Error";
	public static final String BULK_UPLOAD_FILE_STATUS_IN_PROGRESS = "In Progress";
	public static final String PATTERN_BULK_UPLOAD_CONTRACT_TYPE = "[a-zA-Z]*$";
	/** [Start] R 8.4.1 QC_9429 Bulk upload should accept special characters in contract titles. INC000002888250*/
	//public static final String PATTERN_BULK_UPLOAD_CONTRACT_TITLE = "^[a-zA-Z0-9\\- ]*$";
	public static final String PATTERN_BULK_UPLOAD_CONTRACT_TITLE ="^[a-zA-Z0-9\\s!@#$%^&\\*()\\-_+={}\\[\\];:“\"‘\\'<>,.\\?/`~§]*$";
	/** [End] R 8.4.1 QC_9429 Bulk upload should accept special characters in contract titles. INC000002888250*/
	public static final String PATTERN_BULK_UPLOAD_CONTRACT_EPIN = "^[a-zA-Z0-9]*$";
	public static final String PATTERN_BULK_UPLOAD_CONTRACT_AGENCY = "^[a-zA-Z]*$";
	public static final String PATTERN_BULK_UPLOAD_CONTRACT_PROGRAM = "^[a-zA-Z0-9:/\\\\!@#$%^&{}\\[\\]()_+\\-=,.~'` ]*$";
	public static final String PATTERN_BULK_UPLOAD_CONTRACT_VALUE = "^[0-9\\-]\\d{0,9}(\\.\\d+)?$";
	public static final String PATTERN_BULK_UPLOAD_CONTRACT_VALUE_LATEST = "^0*[1-9][0-9]*(\\.[0-9]+)?|0+\\.[0-9]*[1-9][0-9]*$";
	public static final String PATTERN_BULK_UPLOAD_CONTRACT_VALUE_LATEST_NEW = "^0*[1-9][0-9]*(\\.[0-9]+)?|0+\\.[0-9]*[0-9][0-9]*$";
	public static final String PATTERN_BULK_UPLOAD_FILE = "^[a-zA-Z0-9\\-\\_ ]*$";
	public static final String BULK_UPLOAD_DOC_ID = "asDocId";
	public static final String BULK_UPLOAD_TEMPLATE_SHEET_NAME = "BulkTempSheetName";
	public static final String BULK_UPLOAD_TEMP_DOWNLOAD_DIR = "BULK_UPLOAD_TEMP_DOWNLOAD_DIR";
	public static final String BULK_UPLOAD_TEMP_DATA_FILE_NAME = "BULK_UPLOAD_TEMP_DATA_FILE_NAME";
	public static final String BULK_UPLOAD_TEMP_TEMPLATE_FILE_NAME = "BULK_UPLOAD_TEMP_TEMPLATE_FILE_NAME";
	public static final String BULK_UPLOAD_ABSOLUTE_PATH = "BULK_UPLOAD_ABSOLUTE_PATH";
	public static final String BULK_UPLOAD_FILE_STATE_SUCCESS = "BULK_UPLOAD_FILE_STATE_SUCCESS";
	public static final String BULK_UPLOAD_MENDATORY_ERROR = "BULK_UPLOAD_MENDATORY_ERROR";
	public static final String BULK_UPLOAD_DATA_NOT_SHEET_DB = "BULK_UPLOAD_DATA_NOT_SHEET_DB";
	public static final String BULK_UPLOAD_CONTRACT_VALUE_ERROR = "BULK_UPLOAD_CONTRACT_VALUE_ERROR";
	public static final String EPIN_DOES_NOT_EXIST = "EPIN_DOES_NOT_EXIST";
	public static final String DUPLICATE_EPIN = "DUPLICATE_EPIN";
	public static final String BULK_UPLOAD_TYPE_ERROR = "BULK_UPLOAD_TYPE_ERROR";
	public static final String BULK_UPLOAD_VALUE_ERROR = "BULK_UPLOAD_VALUE_ERROR";
	public static final String BULK_UPLOAD_LENGTH_ERROR = "BULK_UPLOAD_LENGTH_ERROR";
	public static final String BULK_UPLOAD_CONTRACT_LESS_THAN_ZERO_CHECK = "BULK_UPLOAD_CONTRACT_LESS_THAN_ZERO_CHECK";
	public static final String BULK_UPLOAD_IMPL_VERSION = "BULK_UPLOAD_IMPL_VERSION";
	public static final String TEMPLATE_VERSION_NO = "TEMPLATE_VERSION_NO";
	public static final String TEMPLATE_LAST_MODIFIED_DATE = "LAST_MODIFIED_DATE";
	public static final String TEMPLATE_ID = "ID";
	public static final String TEMPLATE_IDEN = "Id";
	public static final String IS_BULK_UPLOAD = "IS_BULK_UPLOAD";
	public static final String TEMPLATE_TEMP_FILE_NAME = "temp";
	public static final String P8_SESSION_FILENET_URI = "FILENET_URI";
	public static final String P8_SESSION_OBJECT_STORE_NAME = "OBJECT_STORE_NAME";
	public static final String P8_SESSION_CONNECTION_POINT_NAME = "CONNECTION_POINT_NAME";
	public static final String P8_SESSION_CONNECTION_POINT_NUMBER = "CONNECTION_POINT_NUMBER";
	public static final String BULK_UPLOAD_TRANSACTION_ELEMENT_PATH_R2 = "/com/nyc/hhs/config/TransactionConfigR2.xml";
	public static final String BULK_UPLOAD_TRANSACTION_ELEMENT_PATH = "/com/nyc/hhs/config/TransactionConfig.xml";
	public static final String BULK_UPLOAD_TASK_AUDIT_ELEMENT_PATH = "/com/nyc/hhs/config/TaskAuditConfiguration.xml";
	public static final String BULK_UPLOAD_DOC_PROPS = "docProps";
	public static final String BULK_UPLOAD_FILE_TYPE = "BULK_UPLOAD_FILE_EXTENSION";
	public static final String BULK_UPLOAD_INPUT_STREAM = "aoIS";
	public static final String BULK_UPLOAD_FILE_PROPS = "aoPropertyMap";
	public static final String BULK_UPLOAD_ENTITY_TYPE = "BULK_UPLOAD";
	public static final String BULK_AS_AGENCY_ID = "asAgencyId";
	public static final String BULK_LOCAL_HASH_MAP = "loHMap";
	public static final String TRANSACTION_FETCH_AGENCY_NAME_BULK = "fetchAgencyNameBatchBulk";
	public static final String FETCH_AGENCY_NAME_RESULT = "fetchAgencyNameBatchResult";
	public static final String AGENCY_NOT_FOUND_ERROR = "Agency does not exist in the system";
	public static final String BULK_DOLLOR_SIGN = "$";
	public static final String BULK_ADD_DECIMAL = ".00";
	public static final String BULK_UPLOAD_APP_SETTING_NAME = "BulkUpload";
	public static final String BULK_UPLOAD_FILE_SIZE = "BulkSizeUpperLimit";
	public static final String TRANSACTION_BULK_DISPLAY_DOC_LIST = "displayDocList_filenet_bulk";
	public static final String BULK_DISPLAY_DOC_LIST = "documentList";
	public static final String TRANSACTION_BULK_SEND_NOTIFICATION = "insertNotificationDetailBulk";
	public static final String TRANSACTION_BULK_SYSTEM_FAILURE = "bulkUploadSystemFailure";
	public static final String BULK_FILE_NOTIFICATION_NAME = "FILENAME";
	public static final String BULK_FILE_UPLOAD_FAIL_MESSAGE = "BULK_FILE_UPLOAD_FAIL_MESSAGE";
	public static final String BULK_FILE_UPLOAD_MSG = "bulkUploadMessage";
	// BULK upload constants -End

	// line items replica constants for new fiscal year
	public static final String CBY_INSERT_OLD_SUB_BUDGET_LINE_ITEMS_ZERO_COPY = "insertOldSubBudgetLineItemsZeroCopy";
	public static final String CBY_FETCH_SUB_BUDGET_LIST_NEW_FY = "fetchSubBudgetListNewFY";
	public static final String CBY_UPDATE_PARENT_OF_DERIVED_SUB_BUDGETS = "updateParentOfDerivedSubBudgets";
	public static final String CBY_CREATE_PS_REPLICA_NEW_FY = "createPersonnelServiceReplicaNewFY";
	public static final String CBY_CREATE_OPS_REPLICA_NEW_FY = "createOperationAndSupportReplicaNewFY";
	public static final String CBY_CREATE_UTILITIES_REPLICA_NEW_FY = "createUtilitiesReplicaNewFY";
	public static final String CBY_CREATE_PFS_REPLICA_NEW_FY = "createProfessionalServiceReplicaNewFY";
	public static final String CBY_CREATE_RENT_REPLICA_NEW_FY = "createRentReplicaNewFY";
	public static final String CBY_CREATE_CS_REPLICA_NEW_FY = "createContractedServicesReplicaNewFY";
	public static final String CBY_CREATE_RATE_REPLICA_NEW_FY = "createRateReplicaNewFY";
	public static final String CBY_CREATE_MILESTONE_REPLICA_NEW_FY = "createMilestoneReplicaNewFY";
	public static final String CBY_CREATE_UNALLOCATED_REPLICA_NEW_FY = "createUnallocatedReplicaNewFY";
	public static final String CBY_CREATE_INDIRECT_RATE_REPLICA_NEW_FY = "createIndirectRateReplicaNewFY";
	public static final String CBY_CREATE_PROGRAM_INCOME_REPLICA_NEW_FY = "createProgramIncomeReplicaNewFY";
	public static final String CBY_CREATE_FRINGE_REPLICA_NEW_FY = "createFringeReplicaNewFY";
	public static final String CBY_CREATE_EQUIPMENT_REPLICA_NEW_FY = "createEquipmentReplicaNewFY";

	public static final String ORIG_CONTRACT_ID = "origContractId";
	public static final String REP_CONTRACT_ID = "repContractId";
	public static final String ORIG_BUDGET_ID = "origBudgetId";
	public static final String REP_BUDGET_ID = "repBudgetId";
	public static final String ORIG_SUB_BUDGET_ID = "origSubBudgetId";
	public static final String REP_SUB_BUDGET_ID = "repSubBudgetId";
	public static final String SOLICITATION_SUCCESS = "solicitationDisplaySuccess";
	public static final String JSP_CONTRACTBUDGET_CONTRACT_BUDGET_AMENDMENT_LANDING = "jsp/contractbudget/contractBudgetAmendmentLanding";
	public static final String GET_CONTRACT_BUDGET_AMENDMENT = "getContractBudgetAmendment";
	public static final String PAGE_LABEL_PORTAL_CONTRACT_AMENDMENT_PAGE_URL = "&_pageLabel=portlet_hhsweb_portal_page_budget_amendment";
	public static final String VISIBILITY_FLAG = "visibilityFlag";
	public static final String STR_BUDGET_APPROVED = "Approved";
	public static final String BUDGET_ACTIVE = "Active";
	public static final String BUDGET_LIST_HOME = "BudgetListHome";
	public static final String LO_INVOICE_FILTER_BEAN = "loInvoiceFilterBean";
	public static final String GET_AMEND_AMOUNT = "getAmendAmount";
	public static final String AMEND_CONTRACT_FAILURE = "amendContractFailure";
	public static final String PROPERTY_PE_PROGRAM_NAME = "ProgramName";
	public static final String INT_EXT_EVALUATORS_LIST = "loEvaluatorsList";
	public static final String FETCH_EVALUATORS_LIST = "fetchEvaluatorsList";

	public static final String CBM_MERGE_PS_REPLICA = "mergePersonnelServiceReplica";
	public static final String CBM_MERGE_OPS_REPLICA = "mergeOperationAndSupportReplica";
	public static final String CBM_MERGE_UTILITIES_REPLICA = "mergeUtilitiesReplica";
	public static final String CBM_MERGE_PFS_REPLICA = "mergeProfessionalServiceReplica";
	public static final String CBM_MERGE_RENT_REPLICA = "mergeRentReplica";
	public static final String CBM_MERGE_CS_REPLICA = "mergeContractedServicesReplica";
	public static final String CBM_MERGE_RATE_REPLICA = "mergeRateReplica";
	public static final String CBM_MERGE_MILESTONE_REPLICA = "mergeMilestoneReplica";
	public static final String CBM_MERGE_UNALLOCATED_REPLICA = "mergeUnallocatedReplica";
	public static final String CBM_MERGE_INDIRECT_RATE_REPLICA = "mergeIndirectRateReplica";
	public static final String CBM_MERGE_PROGRAM_INCOME_REPLICA = "mergeProgramIncomeReplica";
	public static final String CBM_MERGE_FRINGE_REPLICA = "mergeFringeReplica";
	public static final String CBM_MERGE_EQUIPMENT_REPLICA = "mergeEquipmentReplica";

	public static final String CBM_DELETE_PS_REPLICA = "markPersonnelServiceAsDeleted";
	public static final String CBM_DELETE_OPS_REPLICA = "markOperationAndSupportAsDeleted";
	public static final String CBM_DELETE_UTILITIES_REPLICA = "markUtilitiesAsDeleted";
	public static final String CBM_DELETE_PFS_REPLICA = "markProfessionalServiceAsDeleted";
	public static final String CBM_DELETE_RENT_REPLICA = "markRentAsDeleted";
	public static final String CBM_DELETE_CS_REPLICA = "markContractedServicesAsDeleted";
	public static final String CBM_DELETE_RATE_REPLICA = "markRateAsDeleted";
	public static final String CBM_DELETE_MILESTONE_REPLICA = "markMilestoneAsDeleted";
	public static final String CBM_DELETE_UNALLOCATED_REPLICA = "markUnallocatedAsDeleted";
	public static final String CBM_DELETE_INDIRECT_RATE_REPLICA = "markIndirectRateAsDeleted";
	public static final String CBM_DELETE_PROGRAM_INCOME_REPLICA = "markProgramIncomeAsDeleted";
	public static final String CBM_DELETE_FRINGE_REPLICA = "markFringeAsDeleted";
	public static final String CBM_DELETE_EQUIPMENT_REPLICA = "markEquipmentAsDeleted";
	public static final String CBM_DELETE_SUBBUDGET_REPLICA = "markSubBudgetsAsDeleted";
	public static final String CBM_DELETE_BUDGET_REPLICA = "markBudgetAsDeleted";

	public static final String CBM_UPDATE_BASE_SUBBUDGET_AMOUNT = "mergeSubBudgetForUpdate";
	public static final String CBM_UPDATE_BASE_BUDGET_AMOUNT = "mergeBudgetForUpdate";
	public static final String CBM_UPDATE_BASE_CONTRACT_AMOUNT = "mergeContractForUpdate";
	public static final String CBM_AMEND_BASE_CONTRACT_AMOUNT = "mergeContractForAmendment";
	public static final String CBM_FETCH_COUNT_APPROVED_BUDGET = "fetchCountOfApprovedBudget";
	public static final String CBM_UPDATE_BASE_CONTRACT_END_DATE = "updateContractEndDate";
	// Commented for enhancement 6448 for Release 3.8.0
	// public static final String ALL_AWARDS_FINALIZE_REGISTERED =
	// "ALL_AWARDS_MUST_FINALIZE_AND_REGISTERED";
	public static final String CLOSE_PROCUREMENT_SUCCESS = "closeProcurmentSuccess";
	// public static final String CT_NOT_CANCELLED_CLOSED_REGISTERED =
	// "ctNotCancelledClosedRegistered";
	// public static final String CONTRACT_COUNT_FLAG = "contractCountFlag";

	public static final String NO = "NO";
	public static final String REGISTERED_CONTRACT_STATUS = "registered";
	public static final String CLOSED_CONTRACT_STATUS = "closed";
	public static final String CANCELED_CONTRACT_STATUS = "canceled";
	public static final String REGISTERED_AWARD_STATUS = "awardRegistered";
	public static final String CANCELED_AWARD_STATUS = "awardCanceled";
	public static final Map<String, String> TASK_TYPE_CONTROLLER_MAP = new HashMap<String, String>();
	static
	{
		TASK_TYPE_CONTROLLER_MAP.put(TASK_PROCUREMENT_COF, "&_pageLabel=portlet_hhsweb_portal_page_bmc");
		TASK_TYPE_CONTROLLER_MAP.put(TASK_CONTRACT_UPDATE, "&_pageLabel=portlet_hhsweb_portal_page_bmc");
		TASK_TYPE_CONTROLLER_MAP.put(TASK_CONTRACT_CONFIGURATION, "&_pageLabel=portlet_hhsweb_portal_page_bmc");
		TASK_TYPE_CONTROLLER_MAP.put(TASK_CONTRACT_COF, "&_pageLabel=portlet_hhsweb_portal_page_bmc");
		TASK_TYPE_CONTROLLER_MAP.put(TASK_ADVANCE_REVIEW, "&_pageLabel=portlet_hhsweb_portal_advance_payment_page");
		TASK_TYPE_CONTROLLER_MAP.put(TASK_NEW_FY_CONFIGURATION, "&_pageLabel=portlet_hhsweb_portal_page_bmc");
		TASK_TYPE_CONTROLLER_MAP.put(TASK_AMENDMENT_CONFIGURATION, "&_pageLabel=portlet_hhsweb_portal_page_bmc");
		TASK_TYPE_CONTROLLER_MAP.put(TASK_BUDGET_UPDATE, "&_pageLabel=portlet_hhsweb_portal_page_budget_modification");
		TASK_TYPE_CONTROLLER_MAP.put(TASK_BUDGET_REVIEW, "&_pageLabel=portlet_hhsweb_portal_contract_budget_page");
		TASK_TYPE_CONTROLLER_MAP.put(TASK_BUDGET_MODIFICATION,
				"&_pageLabel=portlet_hhsweb_portal_page_budget_modification");
		TASK_TYPE_CONTROLLER_MAP.put(TASK_BUDGET_AMENDMENT, "&_pageLabel=portlet_hhsweb_portal_page_budget_amendment");
		TASK_TYPE_CONTROLLER_MAP.put(TASK_INVOICE_REVIEW, "&_pageLabel=portlet_hhsweb_portal_contract_invoice_page");
		TASK_TYPE_CONTROLLER_MAP.put(TASK_PAYMENT_REVIEW, "&_pageLabel=portlet_hhsweb_portal_page_payment_detail");
		TASK_TYPE_CONTROLLER_MAP.put(TASK_ADVANCE_PAYMENT_REVIEW,
				"&_pageLabel=portlet_hhsweb_portal_page_advance_payment_detail");
		TASK_TYPE_CONTROLLER_MAP.put(TASK_AMENDMENT_COF, "&_pageLabel=portlet_hhsweb_portal_page_bmc");
		//Added in R6: return payment review task
		TASK_TYPE_CONTROLLER_MAP.put(TASK_RETURN_PAYMENT_REVIEW, "&_pageLabel=portlet_hhsweb_portal_contract_budget_page");
		//Added in R6: return payment review task
	}
	public static final String JSP_PATH_AMEND_FY_BUDGET = "jsp/contractbudget/amendFYBudget";
	public static final String FETCH_AMEND_FY_BUDGET_SUMMARY = "fetchAmendFyBudgetSummary";
	public static final String GET_UPDATE_AMOUNT_FOR_BUDGET = "getUpdateAmountBudget";
	public static final String FETCH_FY_UPDATE_BUDGET_SUMMARY = "fetchUpdateFyBudgetSummary";
	public static final String FETCH_FY_MODIFICATION_BUDGET_SUMMARY = "fetchModificationFyBudgetSummary";
	public static final String LAST_ACCESS_ID = "lastAccessIdSession";
	public static final String LAST_JSP_ACCESSED_KEY = "lastJSPAccessed";
	public static final String LAST_CONTROLLER_ACCESSED_KEY = "lastControllerAccessed";
	public static final String ACCESS_SCREEN_ENABLE = "accessScreenEnable";
	public static final String NT225 = "NT225";
	public static final String AL224 = "AL224";
	public static final String NT226 = "NT226";
	public static final String AL225 = "AL225";
	public static final String TASK_ACTION = "taskAction";
	public static final String FILTER_CHECKED = "filterchecked";
	public static final String INCLUDE_MANAGEMENT_BOX = "IncludeManagementBox";
	public static final String INCLUDE_INBOX = "IncludeInbox";
	public static final String AGENCY_INBOX_JSP = "/WEB-INF/r2/jsp/agencyWorkflow/agencyInbox.jsp";
	public static final String AGENCY_TASK_MANAGEMENT_JSP = "/WEB-INF/r2/jsp/agencyWorkflow/agencyTaskManagement.jsp";
	public static final String FILTER_RETAINED = "loFilterToBeRetained";
	public static final String UNASSIGN = "unassign";
	public static final String TASK_HOME = "taskhome";
	public static final String ASSIGN = "assign";
	public static final String DISPLAY_BLOCK = "display:block";
	public static final String AGENCY_TASK_ITEM_LIST = "agencyTaskItemList";
	public static final String OPTION = "option";
	public static final String GET_PROGRAM_NAME_FOR_AGENCY = "getProgramNameForAgency";
	public static final String TASK_TYPES = "taskTypes";
	public static final String TASK_STATUS_MAP = "statusMap";
	public static final String TASK_FILTER_TYPE = "tasktype";
	public static final String TASK_FILTER_PROVIDER_NAME = "providername";
	public static final String TASK_FILTER_PROGRAM_NAME = "programname";
	public static final String TASK_FILTER_DATE_FROM = "datefrom";
	public static final String TASK_FILTER_DATE_TO = "dateto";
	public static final String TASK_FILTER_DATE_ASSIGNED_FROM = "dateassignedfrom";
	public static final String TASK_FILTER_DATE_ASSIGNED_TO = "dateassignedto";
	public static final String TASK_FILTER_ASSIGNED_TO = "assignedto";
	public static final String SELECTED_TAB = "selecetdTab";
	public static final String RETURN_TO_AGENCY_TASK = "returnToAgencyTask";
	public static final String JSP_PATH_CONTRACT_UPDATE_FY_BUDGET = "jsp/contractbudget/contractUpdateFYBudget";
	public static final String BUDGET_AMEND_STATUS_CHANGED_FROM = "Contract Budget Amendment Status changed from ";
	public static final String LAUNCH_WF_CONTRACT_AMENDMENT = "launchWFContractAmendment";
	public static final String IS_AGENCY_VIEWING_RFP_DOC = "isAgencyViewingRFPDoc";
	public static final String HIDE_EXIT_PROCUREMENT = "hideExitProcurement";
	public static final String AMENDMENT_TYPE = "amendmentType";

	public static final String CBY_FETCH_SALRIED_EMPLOYEE_FOR_AMENDMENT = "fetchSalriedEmployeeForAmendment";
	public static final String CBY_INSERT_FIRST_PERSONNEL_SERVICES_FOR_AMENDMENT = "insertFirstPersonnelServicesForAmendment";
	public static final String CBY_UPDATE_PERSONNEL_SERVICES_FOR_AMENDMENT = "updatePersonnelServicesForAmendment";
	public static final String CBY_INSERT_PERSONNEL_SERVICES_AMENDMENT = "insertPersonnelServicesAmendment";
	public static final String CBY_FETCH_HOURLY_EMPLOYEE_FOR_AMENDMENT = "fetchHourlyEmployeeForAmendment";
	public static final String CBY_FETCH_SEASONAL_EMPLOYEE_FOR_AMENDMENT = "fetchSeasonalEmployeeForAmendment";
	public static final String PERSONNEL_SERVICES_AMENDMENT_JSP_NAME = "personnelServicesAmendment";
	public static final String CBY_FETCH_FRINGE_BENEFITS_FOR_AMENDMENT = "fetchFringeBenefitsForAmendment";
	public static final String CBY_UPDATE_FRINGE_BENIFITS_FOR_AMENDMENT = "updateFringeBenifitsForAmendment";
	public static final String CBY_INSERT_FIRST_FRINGE_BENEFITS_FOR_AMENDMENT = "insertFirstFringeBenefitsForAmendment";
	public static final String CBY_AMEND_SEASONAL_EMPLOYEE_GRID_ADD = "seasonalEmployeeGridAmendmentAdd";
	public static final String CBY_AMEND_HOURLY_EMPLOYEE_GRID_ADD = "hourlyEmployeeGridAmendmentAdd";
	public static final String CBY_AMEND_SALARIED_EMPLOYEE_GRID_ADD = "salariedEmployeeGridAmendmentAdd";
	public static final String CBY_AMEND_SEASONAL_EMPLOYEE_GRID_EDIT = "seasonalEmployeeGridAmendmentEdit";
	public static final String CBY_AMEND_HOURLY_EMPLOYEE_GRID_EDIT = "hourlyEmployeeGridAmendmentEdit";
	public static final String CBY_AMEND_SALARIED_EMPLOYEE_GRID_EDIT = "salariedEmployeeGridAmendmentEdit";
	public static final String LOCKED_BY = "lockedByUser";
	public static final String AGENCY_ID_TABLE_COLUMN = "AGENCY_ID";

	public static final Map<String, String> ROLE_AGENCY = new HashMap<String, String>();
	static
	{
		ROLE_AGENCY.put("accostaff", "ACCO_STAFF");
		ROLE_AGENCY.put("accostaffsdmin", "ACCO_ADMIN_STAFF");
		ROLE_AGENCY.put("accomanager", "ACCO_MANAGER");
		ROLE_AGENCY.put("programstaff", "PROGRAM_STAFF");
		ROLE_AGENCY.put("programStaffAdmin", "PROGRAM_ADMIN_STAFF");
		ROLE_AGENCY.put("programmanager", "PROGRAM_MANAGER");
		ROLE_AGENCY.put("financialstaff", "FINANCE_STAFF");
		ROLE_AGENCY.put("financialstaffadmin", "FINANCE_ADMIN_STAFF");
		ROLE_AGENCY.put("financemanager", "FINANCE_MANAGER");
		ROLE_AGENCY.put("cfo", "CFO");
		ROLE_AGENCY.put("auditor", "AUDITOR"); //jm
	}
	public static final String FETCH_AGENCY_DETAILS_INBOX = "fetchAgencyDetailsForInbox";
	public static final String AGENCY_USER_LIST_INBOX = "fetchAgencyDetailsForInbox";
	public static final String UNASSIGNED_ACCO_STAFF = "Unassigned ACCO Staff";
	public static final String UNASSIGNED_ACCO_MANAGER = "Unassigned ACCO Manager";
	public static final String UNASSIGNED_LEVEL = "Unassigned Level ";
	public static final String UNASSIGNED_LEVEL1 = "Unassigned Level 1";
	public static final String UNASSIGNED_LEVEL2 = "Unassigned Level 2";
	public static final String TOTAL_TASK = "TotalTask";
	public static final String WOB_LIST = "loWobList";
	public static final String ASSIGN_MULTI_TASK = "reassignMultiAgencyTask";
	public static final String REASSIGN_TO_USER = "reassigntouser";
	public static final String CHECK = "check";
	public static final String DELIMITER_SINGLE_HASH = "#";
	public static final String FETCH_SM_USERS = "fetchSMUserList";
	public static final String USER_ROLE_LIST = "userRoleList";
	public static final String TASK_LEVEL = "taskLevel";
	public static final String AGENCY_TASK_BEAN = "loAgencyTaskBean";
	public static final String FETCH_AGENCY_TASK_LIST = "fetchAgencyTaskList";
	public static final String AGENCY_TASK_LIST = "agencyTaskList";
	public static final String AGENCY_TASK_COUNT = "agencyTaskCount";
	public static final String FETCH_AGENCY_USER_DETAILS = "fetchAgencyUserDetails";
	public static final String DATE_CREATED = "dateCreated";
	public static final String LAST_ASSIGNED = "lastAssigned";
	public static final String PROCUREMENT_UNDERSCORE = "Procurement_";
	public static final String EVALUATION_UNDERSCORE = "Evaluation_";
	public static final String EVALUATION_GROUP_UNDERSCORE = "EvaluationGroup_";
	public static final String PROPOSAL_UNDERSCORE = "Proposal_";
	public static final String RFP_FINANCIAL_UNDERSCORE = "RFP_Release_Financial_";
	public static final String UNDERSCORE_JSP_UNDERSCORE = "_JSP_";
	public static final String INCLUDE_FILE_PATH = "includeFilePath";
	public static final String TASK_CONTRACT_ID = "taskContractId";
	public static final String LO_BUDGET_LIST = "loBudgetList";
	public static final String BUDGET_MODIFICATION_ID = "3";
	public static final String BUDGET_UPDATE_ID = "4";
	public static final String CBY_FETCH_BUDGET_STATUS = "fetchBudgetStatus";
	public static final String CBY_FETCH_DISCRIPENCY_FLAG_AMOUNT = "fetchDiscFlagAmount";
	public static final String IS_SUBMIT_INVOICE_ERROR_MSG = "isSubmitInvoiceErrorMsg";
	public static final String SUBMITINVOICE_MODIFICATION_FAILURE = "! Cannot submit invoices while modification is in progress.";
	public static final String SUBMITINVOICE_UPDATE_FAILURE = "! Cannot submit invoices while budget update is in progress.";
	public static final String SUBMITINVOICE_FAILURE = "! Cannot submit invoices while a negative amendment is in progress and the amendment budget has not been approved.";
	public static final String SUBMIT_AMEND_AMOUNT_FAILURE = "! Cannot submit invoices at this time.";
	public static final String ADD_DELETE_STATUS = "addDeleteStatus";
	public static final String EQUAL = "=";
	public static final String AND_SIGN = "&";
	public static final String SUBMIT_CBM_CONFIRMATION = "submitCBMConfirmation";
	public static final String TAXONOMY_LIST = "TaxonomyList";
	public static final String DEL_CONTRACT_UPDATE_ID = "delContractUpdatedId";
	public static final String CHANNEL_ACCESS = "ChannelAccess";
	public static final String NEGATE_RULE = "negaterule";
	public static final String PERSONNEL_SERVICES_UPDATE_JSP_NAME = "personnelServicesUpdate";
	public static final String AL226 = "AL226";
	public static final String NT227 = "NT227";
	public static final String PROC_REVIEW_STATUS_ID = "PROC_REVIEW_STATUS_ID";
	public static final String AL219 = "AL219";
	public static final String NT214 = "NT214";
	public static final String AGENCY_NAME_COLUMN = "AGENCY_NAME";
	public static final String PROPOSAL_SUBMITTED_TIME = "PROPOSAL_SUBMITTED_TIME";
	public static final String PROVIDER_NAME = "PROVIDER_NAME";
	public static final String AGENCY_LINK = "AGENCY_LINK";
	public static final String PROVIDER_LINK = "PROVIDER_LINK";
	public static final String CB_VALIDATE_SUBMIT_AMOUNT_TOTAL = "validateCBAmountTotal";
	public static final String CBU_VALIDATE_SUBMIT_AMOUNT_TOTAL = "validateCBUAmountTotal";
	public static final String CBM_MAPPER_FETCH_UPDATE_AMOUNT_TOTAL = "fetchUpdateAmountTotal";
	public static final String CBM_MAPPER_FETCH_AMOUNT_TOTAL = "fetchAmountTotal";
	public static final String CBM_MAPPER_FETCH_SUB_BUDGET_AMOUNT = "fetchSubBudgetAmount";
	public static final String UPDATE_ADD_DELETE_FLAG = "updateAddDelFlag";
	public static final String SESSION_ID = "sessionId";
	public static final String LOCK_ID = "lockId";
	public static final String REMOVE_LOCKED_USER = "removeLockedUser";
	public static final String REMOVE_LOCKED_USER_BY_ID = "removeLockedUserById";
	public static final String CHECK_LOCK_FLAG_EXIST = "checkLockFlagExist";
	public static final String ADD_LOCK = "addLock";
	public static final String CHECK_ADD_LOCK = "checkAndAddLock";
	public static final String LO_IS_LOCK_ADDED = "loIsLockAdded";
	public static final String OUTPUT_DATA = "outputData";
	public static final String AL220 = "AL220";
	public static final String NT210 = "NT210";
	public static final String NT219 = "NT219";
	public static final String CHECK_IF_ALL_REQ_AWARD_DOCS_UPLOADED = "checkIfAllReqAwardDocsUploaded";
	public static final String GET_AGENCY_ID_FOR_AWARD_ID = "getAgencyIdForAwardId";
	public static final String EMAIL_ID = "EMAIL_ID";
	public static final String NT220 = "NT220";
	public static final String FETCH_USER_EMAIL_IDS = "fetchUserEmailIds";
	public static final String FETCH_RETURN_SCORES_USER_EMAIL_IDS = "fetchReturnedScoresUserEmailIds";
	public static final String DELETE_PROCUREMENT_PROVIDER_DATA = "deleteProcurementProviderData";

	public static final String GENERATE_AMENDMENT_BUDGET_DATA = "generateAmendmentBudgetData";
	public static final String FETCH_SUB_BUDGET_DETAILS = "fetchSubBudgetDetails";
	public static final String CBA_PARENT_ID = "PARENT_ID";
	public static final String CBA_MASTER_BEAN = "aoMasterBean";
	public static final String CASTOR_XML_PATH = "/resources/R3Castor/castor-mapping-Entity.xml";
	public static final Map<String, String> HELP_ICON_MAP = new HashMap<String, String>();
	static
	{
		HELP_ICON_MAP.put(TASK_PROCUREMENT_COF, "Procurement Certification of Funds Task");
		HELP_ICON_MAP.put(TASK_CONTRACT_UPDATE, "Configuration Update Task");
		HELP_ICON_MAP.put(TASK_CONTRACT_CONFIGURATION, "Contract Configuration (Initial/Renewal/Add Contract) Task");
		HELP_ICON_MAP.put(TASK_CONTRACT_COF, "Contract Certification of Funds (Initial/Renewal/Add Contract) Task");
		HELP_ICON_MAP.put(TASK_ADVANCE_REVIEW, "Advance Review Task");
		HELP_ICON_MAP.put(TASK_NEW_FY_CONFIGURATION, "New FY Configuration Task");
		HELP_ICON_MAP.put(TASK_AMENDMENT_CONFIGURATION, "Amendment Configuration Task");
		HELP_ICON_MAP.put(TASK_BUDGET_UPDATE, "Update Budget Review Task");
		HELP_ICON_MAP.put(TASK_BUDGET_REVIEW, "Contract Budget Review (Initial/New FY/Renewal) Task");
		HELP_ICON_MAP.put(TASK_BUDGET_MODIFICATION, "Modification Budget Review Task");
		HELP_ICON_MAP.put(TASK_BUDGET_AMENDMENT, "Amendment Budget Review Task");
		HELP_ICON_MAP.put(TASK_INVOICE_REVIEW, "Invoice Review Task");
		HELP_ICON_MAP.put(TASK_PAYMENT_REVIEW, "Payment Review Task");
		HELP_ICON_MAP.put(TASK_ADVANCE_PAYMENT_REVIEW, "Advance Payment Task");
		HELP_ICON_MAP.put(TASK_AMENDMENT_COF, "Amendment Certification of Funds Task");
		//[R6 Start] Returned Payment - Added key value for Returned Payment Review in the map for help category
		HELP_ICON_MAP.put(TASK_RETURN_PAYMENT_REVIEW, "Returned Payment Review");
		//[R6 End] Returned Payment - Added key value for Returned Payment Review in the map for help category
	}
	public static final String USER_NOT_SELECTED = "USER_NOT_SELECTED";

	public static final String NT204 = "NT204";
	public static final String NT205 = "NT205";
	public static final String NT211 = "NT211";
	public static final String NT212 = "NT212";
	public static final String NT215 = "NT215";
	public static final String NT216 = "NT216";
	public static final String NT217 = "NT217";
	public static final String NT218 = "NT218";
	public static final String AL204 = "AL204";
	public static final String AL205 = "AL205";
	public static final String AL206 = "AL206";
	public static final String AL207 = "AL207";
	public static final String AL208 = "AL208";
	public static final String AL209 = "AL209";
	public static final String AL210 = "AL210";
	public static final String PROPOSALDUEDATEFIRSTNOTIFICATION = "PropoalDue1stAlert";
	public static final String PROPOSALDUEDATESECONDNOTIFICATION = "PropoalDue2ndAlert";
	public static final String RFPRELEASEDATEFIRSTNOTIFICATION = "RfpRelease1stAlert";
	public static final String RFPRELEASEDATESECONDNOTIFICATION = "RfpRelease2ndAlert";
	public static final String FIRSTROUNDEVALUATIONDATENOTIFICATION = "FirstRndEvalDueAlert";
	public static final String FINALEVALUATIONDATENOTIFICATION = "FinalEvalDueAlert";
	public static final String NOTIFICATION_LIST_KEY = "notificationList";
	public static final String EVENT_ID = "EVENT_ID";
	public static final String UPDATE_BUDGET_STATUS = "updateBudgetStatus";

	public static final String FETCH_SUB_BUDGET_DETAILS_FOR_UPDATE_CONF_TASK = "fetchSubBudgetDetailsForUpdateConf";
	public static final String FETCH_SUB_BUDGET_DETAILS_FOR_AMENDMENT_CONF_TASK = "fetchSubBudgetDetailsForAmendmentConf";
	public static final String FETCH_SUB_BUDGET_DETAILS_FOR_BUDGET_UPDATE_TASK = "fetchSubBudgetDetailsBudgetUpdateTask";
	public static final String SOFT_DELETE_PARENT = "softDeleteParent";
	public static final String UPDATE_PARENT_SUB_BUDGET_SITE = "updateParentSubBudgetSite";
	public static final String INSERT_SUB_BUDGET_DETAILS_SITE_DETAILS_FOR_UPDATE = "insertSubBudgetSiteDetailsForUpdate";
	// Added as a part of release 3.6.0 for enhancement request 5905

	public static final String CHK_CONTRACT_CERT_FUNDS = "chkContractCertFunds";
	public static final String INVOICE_NEGATIVE_AMEND_CHECK = "invoiceNegativeAmendCheck";
	public static final String INVOICE_AMOUNT_ASSIGNEMENT_VALIDATION = "invoiceAmountAssignmentValidation";

	public static final String FETCH_CURRENT_TASK_OWNER = "fetchCurrentAgencyTaskOwner";
	public static final String TASK_OWNER_NAME = "lsTaskOwnerName";
	public static final String CAN_NOT_ASSIGN_TASK = "CAN_NOT_ASSIGN_TASK";
	public static final String TASK_CANCELLED_ERROR = "TASK_CANCELLED_ERROR";
	public static final String ERROR_FINISH = "ERROR_FINISH";
	public static final String ERROR_FINISH_SUSPEND = "ERROR_FINISH_SUSPEND";
	public static final String TO_SEARCH = "toSearch";
	public static final String STR_RETRACT_PROPOSAL = "Retract Proposal";
	public static final String STR_SUBMIT_PROPOSAL = "Submit Proposal";
	public static final String MSG_KEY_INVOICE_EXCEED_ERR = "invoice_exceed_error";
	public static final String MSG_KEY_INVOICE_EXCEED_ERR_GRIDOPERATION = "invoice_exceed_error_gridoperation";
	public static final String MSG_KEY_INVOICE_ZERO_ERR = "invoice_zero_error";
	public static final String MSG_KEY_INVOICE_NEGATIVE_AMENDMENT = "invoice_negative_amendment_error";

	public static final String BUDGET_DOC_TYPE = "BudgetAmedmentTemplate";
	public static final String XML_DOC_TITLE = "BudgetAmendmentXml";
	public static final String UPDATE_BUDGET_WITH_DOC_ID = "updateBudgetWithDocId";
	public static final String FETCH_DOC_ID_OF_BUDGET = "fetchDocIdForBudget";
	public static final String FETCH_AMENDMENT_BUDGET_STATUS = "fetchAmendmentBudgetStatus";
	public static final String XML_MIME_TYPE = "text/xml";
	public static final String XML_FILE_TYPE = "XML";
	public static final String GET_UPDATED_PROPOSAL_DUE_DATE = "getUpdatedProposalDueDate";
	public static final String UPDATED_PROPOSAL_DUE_DATE = "updatedProposalDueDate";
	public static final String INVOICE_DETAILS = "invoiceTotal";
	public static final String ASSIGNEMNT_AMOUNT = "assignmentAmount";
	public static final String ASSIGNEMNT_AMOUNT_EXCEPT_CUREENT_LINEITEM = "assignmentAmountExceptCurrentLineItem";
	public static final String AS_CONTRACT_STATUS = "asContractStatus";
	public static final String PROCUREMENT_STATUS_ID = "PROC_STATUS_ID";
	public static final String READ_ONLY_PAGE_ATTRIBUTE_RULE = "readOnlyPageAttributeRule";
	public static final String READ_ONLY_PAGE_ATTRIBUTE_RULE_INVOICE = "readOnlyPageAttributeRuleInvoice";

	public static final String FETCH_NOTIFICATION_SETTINGS_TRANS_NAME = "fetchNotificationSettings";
	public static final String FETCH_NOTIFICATION_SETTINGS_TRANS_OUTPUT = "notificationSettingDetails";
	public static final String FETCH_PROPOSAL_DUE_DATE_ALERT_DETAILS_TRANS_NAME = "fetchProposalDueDateAlertDetails";
	public static final String FETCH_FRST_RND_EVALDATE_DUE_DATE_ALERT_DETAILS_TRANS_NAME = "fetchFirstRoundEvaluationDueDateAlertDetails";
	public static final String FETCH_FINAL_RND_EVALDATE_DUE_DATE_ALERT_DETAILS_TRANS_NAME = "fetchFinalEvaluationDueDateAlertDetails";
	public static final String FETCH_RFP_RELEASE_DUE_DATE_ALERT_DETAILS_TRANS_NAME = "fetchRfpReleaseDueDateAlertDetails";
	public static final String INSERT_NOTIFICATION_TRANS_NAME = "insertSMNotificationDetail";
	public static final String FETCH_EXTERNAL_INTERNAL_EVALUATOR_TRANS_NAME = "fetchExtAndIntEvaluatorBatch";
	public static final String FETCH_EXTERNAL_INTERNAL_EVALUATOR_TRANS_OUTPUT = "ExtIntEvaluatorList";
	public static final String FILTER_NOTIFICATION_LIST_TRANS_NAME = "filterNotificationList";
	public static final String FILTER_NOTIFICATION_LIST_TRANS_OUTPUT = "aoFilteredNotification";
	public static final String NOTIFICATION_NAME = "NotificationName";
	public static final String FETCH_APPROVED_PROVIDER_LIST_TRANS_NAME = "fetchApprovedProvidersList";
	public static final String FETCH_APPROVED_PROVIDER_LIST_TRANS_OUTPUT = "loApprovedProvidersList";
	public static final String DUE_DATE_ALERT_DETAILS_TRANS_OUTPUT = "smAlertNotificationDetails";
	public static final String HHS_SERVICE_PROPERTIES_PATH = "com.nyc.hhs.properties.hhsservices";
	public static final String NO_OF_DAYS = "NoOfDays";
	public static final String LOCAL_HASH_MAP = "loHMap";

	public static final String GENERATE_MASTER_BEAN = "generateMasterBean";
	public static final String MASTERBEAN_HASHMAP = "MasterBeanHashMap";
	public static final String AO_RETURNED_MASTER_BEAN = "aoReturnedMasterBean";
	public static final String MASTERBEAN_OBJ = "aoMasterBeanObj";

	public static final String PROVIDER_ORG = "provider_org";
	public static final String GET_BASE_AMENDMENT_CONTRACT_DETAILS = "getBaseAmendmentContractDetails";
	public static final String GET_BASE_AMENDMENT_CONTRACT_DETAILS_AMENDMENT_LIST = "getBaseAmendmentContractDetailsAmendmentList";
	public static final String FETCH_BASE_AMENDMENT_CONTRACT_DETAILS = "fetchBaseAmendmentContractDetails";
	public static final String FETCH_BASE_AMENDMENT_CONTRACT_DETAILS_AMENDMENT_LIST = "fetchBaseAmendmentContractDetailsAmendmentList";
	public static final String CONTRACT_LIST_BEAN = "aoContractListBean";
	public static final String DOWNLOAD_AMENDMENT_DOCUMENT = "downloadAmendmentDocument";
	public static final String PDF_AMENDMENT_DOC_GENERATED = "pdfAmendmetDocGenerated";
	public static final String AMENDMENT_MAP = "loAmendMap";
	public static final String AWARD_EPIN_DROPDOWN = "awardEpinDropDown";
	public static final String BMC_FETCH_BASE_AMENDMENT_CONTRACT_AMOUNT = "fetchBaseAmendmentContractAmount";
	public static final String BASE_AMENDMENT_CONTRACT_AMOUNT_DETAILS = "baseAmendmentContractAmountDetails";
	public static final String CONTRACT_CONFIG_COF_DOC_RESOURSE_FETCH = "contractConfigCOFDocResourseFetch";
	public static final String CONTRACT_COF_RESOURCE_DOC = "contractCOFResourceDoc";
	public static final String SELECT_VAL = "selectVal";
	public static final String ADVANCE_SUBMITTED_STATUS = "92";

	public static final String PROPERTY_FILE_SM_BATCH_CONFIG = "com.nyc.hhs.properties.batchConfigSM";
	public static final String TRANSACTION_CONFIG_FILE_BATCH = "/com/nyc/hhs/config/SMBatchTransactionConfig.xml";
	public static final String EVENT_TYPE_TEMPLATE = "/com/nyc/hhs/config/EventTypeTemplate.xml";
	public static final String NOTIFICATION_CONTENT = "notificationContent";

	public static final String FETCH_CONTRACTS_FOR_BATCH_PROCESS = "fetchContractsForBatchProcess";
	public static final String CLOSE_BUDGET_MODIFICATION_TASK = "closeBudgetModificationTask";

	public static final String GET_CONTRACT_L2_PROVIDERS = "getContractL2Providers";
	public static final String GET_CONTRACT_AGENCY_USERS = "getContractAgencyUsers";

	public static final String FETCH_AMENDMENT_CONTRACTS_FOR_BATCH_PROCESS = "fetchAmendmentContractsForBatchProcess";
	public static final String CONTRACT_BEAN_LIST_ARG = "aoContractBeanList";
	public static final String RUN_BATCH_PROCESS_FOR_CONTRACTS = "runBatchProcessForContracts";

	public static final String RUN_BATCH_BASE_CONTRACT_WITH_NO_DISC = "runBatchBaseContractWithNoDisc";
	public static final String RUN_BATCH_BASE_CONTRACT_WITH_DISC = "runBatchBaseContractWithDisc";
	public static final String RUN_BATCH_BASE_CONTRACT_WITH_DISC_NO_BASE_STR_CHANGE = "runBatchBaseContractWithDiscNoBaseStrChange";

	public static final String RUN_BATCH_POSITIVE_AMENDMENT_WITH_NO_DISC = "runBatchPositiveAmendmentWithNoDisc";
	public static final String RUN_BATCH_POSITIVE_AMENDMENT_WITH_DISC = "runBatchPositiveAmendmentWithDisc";
	public static final String RUN_BATCH_POSITIVE_AMENDMENT_WITH_DISC_NO_BASE_STR_CHANGE = "runBatchPositiveAmendmentWithDiscNoBaseStrChange";
	public static final String RUN_BATCH_NEGATIVE_AMENDMENT_WITH_NO_DISC = "runBatchNegativeAmendmentWithNoDisc";
	public static final String RUN_BATCH_NEGATIVE_AMENDMENT_WITH_DISC = "runBatchNegativeAmendmentWithDisc";
	public static final String RUN_BATCH_NEGATIVE_AMENDMENT_WITH_DISC_NO_BASE_STR_CHANGE = "runBatchNegativeAmendmentWithDiscNoBaseStrChange";
	public static final String DELETE_FISCAL_YEARS = "deleteFiscalYear";
	public static final String ADD_NEW_FISCAL_YEARS = "addNewFiscalYear";
	public static final String CONNECTION_POINT_NAME = "CONNECTION_POINT_NAME";
	public static final String CONNECTION_POINT_NUMBER = "CONNECTION_POINT_NUMBER";

	public static final String RUN_BATCH_PROCESS_FOR_AMENDMENT_CONTRACTS = "runBatchProcessForAmendmentContracts";
	public static final String BATCH_UPDATE_BUDGET_MODIFICATION_AND_UPDATE_STATUS = "batchUpdateBudgetModificationAndUpdateStatus";
	public static final String CONTRACT_BEAN_PATH = "com.nyc.hhs.model.ContractBean";
	public static final String RESET_DISCREPANCY_STATUS = "resetDiscrepancyStatus";
	public static final String REMOVE_CONTRACT_DISCREPANCIES = "removeContractDiscrepancies";
	public static final String MERGE_AMENDMENT_DATES_IN_BASE_CONTRACT = "mergeAmendmentDatesInBaseContract";
	public static final String UPDATE_BASE_CONTRACT_FOR_ZERO_AMENDMENT = "updateBaseContractForZeroAmendment";
	public static final String REMOVE_CONTRACT_DISCREPANCY_AND_MARK_REGISTERED = "removeContractDiscrepancyAndMarkRegistered";
	public static final String UPDATE_PARENT_CONTRACT_FOR_NEGATIVE_AMEND = "updateParentContractForNegAmend";
	public static final String SET_CONTRACT_STATUS_AS_REGISTERED = "setContractStatusAsRegistered";
	public static final String MARK_APPROVED_BUDGETS_AS_ACTIVE = "markApprovedBudgetsAsActive";
	public static final String UPDATE_BUDGETS_EXT_CT_NUMBER = "updateBudgetsExtCtNumber";
	public static final String FETCH_BASE_CONTRACT_DETAILS_FOR_UPDATE = "fetchBaseContractDetailsForUpdate";
	public static final String ADD_NEW_CONTRACT_FOR_UPDATE = "addNewContractForUpdate";
	public static final String FETCH_CONTRACT_BUDGETS = "fetchContractBudgets";
	public static final String SYSTEM_USER = "system";
	public static final String PROPERTY_FILE_CONTRACT_BATCH_CONFIG = "com.nyc.hhs.contractsbatch.batchConfig";
	public static final String CONTRACTS_BATCH_ID = "contracts_batch";
	public static final String ARGUMENT_ROWS_INSERTED = "aiRowsInserted";

	public static final String EMPTY_STRING = "";
	public static final String LIKE_SIGN = "'%'";
	public static final String NON_EDIT_COLNAME = "nonEditColname";

	public static final String COMPONENT_ACTION = "Action";
	public static final String AGENCY_USER_REMOVE_COMPONENT_ACTION = "setTaskUnassignedForRemovedAgencyUsers";
	public static final String INSERT_LINE_ITEMS_CONF_COMPONENT_ACTION = "insertLineItemsForContractConfigTask";
	public static final String MERGE_BUDGET_MOD_COMPONENT_ACTION = "mergeBudgetForModificationReviewTask";
	public static final String MERGE_BUDGET_UPDATE_COMPONENT_ACTION = "mergeBudgetForUpdateReviewTask";
	public static final String MERGE_BUDGET_AMENDMENT_COMPONENT_ACTION = "mergeBudgetForAmendmentReviewTask";
	public static final String INSERT_REPLICA_BUDGET_COMPONENT_ACTION = "insertCreateReplicaForBudgetTask";
	public static final String SUSPEND_ALL_FINANCIAL_TASKS = "suspendAlllFinancialTasks";
	public static final String UNSUSPEND_ALL_FINANCIAL_TASK = "unSuspendAllFinancialTasks";
	public static final String LAST_TASK_STATUS = "LastTaskStatus";
	public static final String COMP_AGENCY_ID = "AgencyId";
	public static final String COMP_USERS = "Users";
	public static final String VALUES = "Values";
	public static final String CONTRACT_BUDGET_STATUS = "ContractBudgetStatus";
	public static final String APPROVED_BUDGET_COUNT = "ApprovedBudgetCount";
	public static final String FINISH_AMENDMENT_CONF_TASK = "finishAmendmentConfigTask";
	public static final String FINISH_AMENDMENT_COF_TASK = "finishAmendmentCofTask";

	public static final String DISPLAY_EVAL_SCORES_DETAILS = "displayEvaluationScoresDetails";
	public static final String FETCH_APPROVED_BUDGET_AMNT = "fetchApprovedBudgetAmnt";
	public static final String FETCH_EVALUATION_STATUS_COUNT = "fetchEvaluationStatusCount";
	public static final String INVOICE_AMOUNT_ZERO_VALIDATION = "invoiceAmountZeroValidation";
	public static final String LOCKED_ON = "lockedOn";
	public static final String CONCURRENCY_TYPE = "CONCURRENCY_TYPE";
	public static final String DB = "DB";
	public static final String USER_SESSION_ID = "USER_SESSION_ID";
	public static final String USER_NAME = "USER_NAME";
	public static final String BASE_AWARD_EPIN = "baseAwardEpin";
	public static final String OPEN_TASK_AMEND_CONF = "openTaskAmendConf";
	public static final String OPEN_TASK_AMEND_CERT_FUND = "openTaskAmendCertFund";
	public static final String CONTRACT_NUMBER = "contractNumber";
	public static final String PROGRAM_NAME = "programName";
	public static final String TAG_ADDED_SUCCESSFULLY = "The taxonomy tag has been successfully saved.";
	public static final String TAG_DELETED_SUCCESSFULLY = "The selected tag has been successfully deleted.";
	public static final String LAST_KEY_ACCESSED = "lastKeyAccessed";
	public static final String THIRTY_TWO = "32";
	public static final String THIRTY = "30";
	public static final String DBD_DOCUMENTS_PATH = "DBD_DOCUMENTS_PATH";
	public static final String FETCH_CITY_AGENCY_USER_NAME = "fetchCityAgencyUserName";
	public static final String FETCH_PROVIDER_USER_NAME = "fetchProviderUserName";
	public static final String UPDATED_PROVIDER_STATUS = "updateProviderStatus";
	public static final String FETCH_SELECTED_COUNT = "fetchSelectedCount";
	public static final String TASK_INBOX = "taskinbox";
	public static final String IS_PROC_CLOSED_CANCELLED = "isProcClosedOrCancelled";
	public static final String ALL_NON_RESPONSIVE_PROPOSALS_MESSAGE = "ALL_NON_RESPONSIVE_PROPOSALS_MESSAGE";
	public static final String FETCH_NOT_NON_RESPONSIVE_COUNT = "fetchNotNonResponsiveCount";
	public static final String NOT_NOT_RESPONSIVE_COUNT = "notNonResponsiveCount";
	public static final String STRING_MINUS_ONE = "-1";
	public static final String FIND_CONTRACT_DETAILS_BY_CONTRACT_FOR_AMEND = "findContractDetailsByContractForAmend";
	public static final String EVALUATION_SENT = "evaluationSent";
	public static final String EVALUATION_TASK_SENT = "evaluationTaskSent";
	public static final String UPDATE_EVALUATION_SENT_FLAG = "updateEvaluationSentFlag";
	public static final String FETCH_EVALUATION_SENT_FLAG = "fetchEvaluationSentFlag";
	public static final String LB_UPDATE_EVALUATION_STATUS = "lbUpdateEvaluationStatus";
	public static final String AL302 = "AL302";
	public static final String AL320 = "AL320";
	public static final String NT302 = "NT302";
	public static final String GET_APPROVED_BUDGETS_OF_CONTRACT = "getApprovedBudgetsOfContract";
	public static final String CONTRAT_STATUS_FOR_BATCH_PROCESS = "CONTRAT_STATUS_FOR_BATCH_PROCESS";
	public static final String AO_HM_ARGS = "aoHMArgs";
	public static final String FINISH_ADVANCE_REQUEST_RETURN_REVIEW_TASK = "finishAdvanceRequestReturnReviewTask";
	public static final String FINISH_ADVANCE_REQUEST_REVIEW_TASK = "finishAdvanceRequestReviewTask";
	public static final String SET_ADVANCE_STATUS_REVIEW_TASK = "setAdvanceStatusForReviewTask";
	public static final String BUDGET_ADVANCE_ID = "budgetAdvanceId";
	public static final String AMENDED_CONTRACT_SUB_BUDGET_ID_COMMA = "amendedContractSubBudgetIdComma";
	public static final String AMENDED_CONTRACT_SUB_BUDGET_ID = "amendedContractSubBudgetID";

	public static final String FETCH_CONTRACT_SOURCE_TYPE = "fetchContractSourceType";
	public static final String LAUNCH_ADVANCE_REVIEW_TASK = "launchAdvanceReviewTask";
	public static final String PROPERTY_PE_BUDGET_ADVANCE_ID = "BudgetAdvanceId";

	// Payment Module
	public static final String PAYMENT_HEADER_DETAILS = "getPaymentHeaderDetails";
	public static final String PAYMENT_REVIEW_HEADER_DETAILS = "getPaymentReviewHeaderDetails";
	public static final String PAYMENT_VOUCHER_DETAIL = "getPaymentVoucherNum";
	public static final String TOTAL_PAYMENT_AMOUNT = "fetchTotalPaymentAmount";
	public static final String TOTAL_INVOICE_AMOUNT = "fetchTotalInvoiceAmount";
	public static final String PAYMENT_ACTUAL_PAID = "fetchPaymentFyBudgetActualPaid";
	public static final String PAYMENT_BUDGET_SUMMARY = "fetchPaymentFyBudgetSummary";

	public static final String FETCH_PAYMENT_REVIEW_DETAILS = "fetchPaymentReviewDetails";
	public static final String FETCH_PAYMENT_DETAILS = "fetchPaymentDetails";
	public static final String PAYMENT_HEADER_DETAIL = "paymentHeaderDetail";
	public static final String PAYMENT_VOUCHER_LIST = "paymentVoucherList";
	public static final String PAYMENT_LINE_DETAIL = "paymentLineDetail";
	public static final String LO_PAYMENT_BEAN = "loPaymentBean";
	public static final String LO_PAYMENT_VOUCHER = "loPaymentVoucher";
	public static final String LO_PAYMENT_BUDGET_DETAILS = "loPaymentBudgetDetails";
	public static final String FETCH_PAYMENT_LINE_DETAILS = "fetchPaymentLineDetails";

	// Advance payment
	public static final String ADVANCE_PAYMENT_HEADER_DETAILS = "getAdvancePaymentHeaderDetails";
	public static final String ADVANCE_PAYMENT_REVIEW_HEADER_DETAILS = "getAdvancePaymentReviewHeaderDetails";
	public static final String ADVANCE_PAYMENT_VOUCHER_DETAIL = "getAdvancePaymentVoucherList";
	public static final String TOTAL_ADVANCE_AMOUNT = "fetchTotalAdvanceAmount";
	public static final String FETCH_ADVANCE_PAYMENT_REVIEW_DETAILS = "fetchAdvancePaymentReviewDetails";
	public static final String FETCH_ADVANCE_PAYMENT_DETAILS = "fetchAdvancePaymentDetails";
	public static final String FETCH_ADVANCE_PAYMENT_LINE_DETAILS = "fetchAdvancePaymentLineDetails";

	// Payment Chart of allocation constant
	public static final String PAYMENT_COF_EDIT = "paymentCOFEdit";
	public static final String PAYMENT_CHART_OF_ALLOCATION = "com.nyc.hhs.model.PaymentChartOfAllocation";
	public static final String PAYMENT_COF_FETCH = "paymentCOFFetch";
	public static final String GET_PAYMENT_VOUCHER_LIST = "getPaymentVoucherList";
	public static final String GET_COA_SUM_AMOUNT = "getCoASumAmount";
	public static final String GET_TOTAL_PAYMENT_AMOUNT = "getTotalPaymentAmount";
	public static final String PAYMENT_COF_REAMINING_AMOUNT_FETCH = "paymentCOFReaminingAmountFetch";
	public static final String ADVANCE_PENDING_APPROVAL = "ADVANCE_PENDING_APPROVAL";
	public static final String ADVANCE_APPROVED = "ADVANCE_APPROVED";
	public static final String DELETE_BUDGET_ADVANCE = "deleteBudgetAdvance";
	public static final String FINISH_PAYMENT_RETURN_REVIEW_TASK = "finishPaymentReturnReviewTask";
	public static final String FINISH_PAYMENT_REVIEW_TASK = "finishPaymentReviewTask";
	public static final String SET_PAYMENT_STATUS = "setPaymentStatus";
	public static final String SET_INVOICE_STATUS = "setInvoiceStatus";
	public static final String DELETE_PAYMENT_RECORDS = "deletePaymentRecords";
	public static final String VALIDATE_LEVEL_ONE_PAYMENT_FINISH_TASK = "validateLevelOnePaymentFinishTask";
	public static final String PERIOD = "period";
	public static final String SET_PERIOD = "setPeriod";

	public static final String CONTRACT_CREATION = "Contract Creation";
	public static final String CONTRACT_CANCELLATION = "Contract Cancellation";
	public static final String CONTRACT_REGISTRATION = "Contract Registration";
	public static final String CONTRACT_SUSPENSION = "Contract Suspension";
	public static final String CONTRACT_UNSUSPENSION = "Contract Un-Suspension";
	public static final String CONTRACT_UPDATE = "Contract Update";
	public static final String CONTRACT_UPDATE_CANCELLATION = "Contract Update Cancellation";
	public static final String CONTRACT_CLOSE = "Contract Close";
	public static final String AMENDMENT_CREATION = "Amendment Creation";
	public static final String AMENDMENT = "Amendment Cancellation";
	public static final String AMENDMENT_REGISTRATION = "Amendment Registration";
	public static final String CONTRACTS = "Contracts";

	// For S400
	public static final String PAYMENT_ASSIGNMENTS = "paymentAssignments";
	public static final String JSP_PAYMENT = "jsp/payment/";
	public static final String DELETE_PROV_QUESTION_DATA = "deleteProviderQuesResponseData";
	public static final String DELETE_PROV_SITE_DATA = "deleteProviderSiteData";
	public static final String ASSIGNMENT_ID = "assignmentId";
	public static final String ADVANCE_ASSIGNMENT_ID = "advanceAssignmentId";
	public static final String PAYMENT_REMAINING_AMOUNT_CHECK = "PAYMENT_REMAINING_AMOUNT_CHECK";
	public static final String PM_FETCH_ADVANCE_ASSIGNMET_AMOUNT = "fetchAdvanceAssignmentAmount";
	public static final String PM_UPDATE_PAYMENT_ASSIGNMET_DETAILS = "updatePaymentAssignmentDetails";
	public static final String PM_INSERT_PAYMENT_ASSIGNMET_DETAILS = "insertPaymentAssignmentDetails";
	public static final String FETCH_COUNT_PAYMENT_ASSIGNMENT = "fetchCountPaymentAssignment";
	public static final String PM_DELETE_ADVANCE_ASSIGNMENT = "delAdvanceAssignment";
	public static final String GET_CONTRACT_INFO_FOR_PAYMENT = "getContractInfoForPayment";
	public static final String JSP_ADVANCE_PAYMENT_REQUEST_TASK = "jsp/payment/advancePaymentRequestTask";
	public static final String PM_FETCH_CONTRACT_INFO_PAYMENT = "fetchContractInfoForPayment";

	public static final String CI_FETCH_INVOICE_ASSIGNMET_DETAIL = "fetchInvoiceAssignmentDetail";
	public static final String NT_PROCUREMENT_TITLE = "PROCUREMENT_TITLE";
	public static final String NT_CT = "CT";
	public static final String AWARD_E_PIN = "AWARD_E-PIN";

	public static final String CONTRACT_END_DATE_KEY = "CONTRACT_END_DATE";
	public static final String CONTRACT_START_DATE_KEY = "CONTRACT_START_DATE";
	public static final String R2_CONTRACT = "R2_Contract";
	public static final String FETCH_CONTRACT_FISCAL_YEARS = "fetchContractFiscalYears";
	public static final String R3_CONTRACT = "APT_Contract";
	public static final String FETCH_R2_CONTRACT_COF_DETAILS = "fetchR2ContractCofDetails";
	public static final String FETCH_R3_CONTRACT_COF_DETAILS = "fetchR3ContractCofDetails";
	public static final String FETCH_BASE_CONTRACT_AMOUNT = "fetchBaseContractAmount";
	public static final String PROC_EPIN_R3 = "PROC_EPIN";
	public static final String PROCUREMENT_VALUE = "PROCUREMENT_VALUE";
	public static final String FINISH_ADVANCE_PAYMENT_RETURN_REVIEW_TASK = "finishAdvancePaymentReturnReviewTask";
	public static final String FETCH_PROC_COF_DETAILS_FINANCIALS = "fetchProcurementCOFDetailsFinancials";
	public static final String FETCH_PROC_ADD_COF_DETAILS_FINANCIALS = "fetchProcurementAddendumCOFDetailsFinancials";
	public static final String FETCH_PROC_DETAILS_FINANCIALS = "fetchProcurementDetailsForFinancials";
	public static final String TO = " to ";
	public static final String EVENT_NAME_COMMENT = "eventNameComment";
	public static final String VALIDATE_LEVEL_ONE_ADVANCE_REQUEST_FINISH_TASK = "validateLevelOneAdvanceRequestFinishTask";
	public static final String MSG_INVALID_ASSIGNMENT_AMOUNT = "MSG_INVALID_ASSIGNMENT_AMOUNT";
	public static final String FETCH_ADVANCE_AND_ASSIGNMENT = "fetchAdvanceAndAssignment";
	public static final String ASSIGNMENT_AMOUNT = "ASSIGNMENT_AMOUNT";
	public static final String ADVANCE_AMOUNT = "ADVANCE_AMOUNT";
	public static final String DISABLE_REASSIGN_DROPDOWN = "disableReassignDropdown";

	public static final String UPDATE_APPROVED_INFO_FOR_COF_DOC = "updateApprovedInfoForCOFDoc";
	public static final String UPDATE_SUBMITTED_INFO_FOR_COF_DOC = "updateSubmittedInfoForCOFDoc";
	public static final String CONTRACT_START_FY = "ContractStartFY";
	public static final String AWARD_AMT_FROM_HIDDEN = "awardAmountFromHidden";
	public static final String AWARD_AMT_TO_HIDDEN = "awardAmountToHidden";
	public static final String SCORE_FROM_HIDDEN = "scoreFromHidden";
	public static final String SCORE_TO_HIDDEN = "scoreToHidden";
	public static final String FETCH_BUDGET_TYPE = "fetchBudgetType";

	public static final String CBY_FETCH_SALRIED_EMPLOYEE_REMAINING_AMT = "fetchSalariedEmployeeForRemainingAmt";
	public static final String CBY_FETCH_SALRIED_EMPLOYEE_INVOICED_AMT = "fetchSalariedEmployeeForInvoicedAmt";
	public static final String CBY_FETCH_FRINGE_REMAINING_AMT = "fetchFringeForRemainingAmt";
	public static final String CBY_FETCH_FRINGE_INVOICED_AMT = "fetchFringeForInvoicedAmt";
	public static final String NEW_RECORD_COA = "_newrecord_coa";

	public static final String UPDATE_BUDGET_FISCAL_YEAR_AMOUNT = "updateBudgetFiscalYearAmount";
	public static final String AS_MODIFICATION_AMOUNT = "asModifiedAmount";

	public static final String FIND_PROC_EPIN_R3_CONTRACT_FOR_WF = "findProcEpinR3ContractForWF";

	public static final String FETCH_ACTIVE_APPROVED_FISCAL_YEARS = "fechActiveApprovedFiscalYears";
	public static final String AMENDED_CONTRACT_ID = "asAmendContractId";
	public static final String FISCAL_YEAR_LIST = "aoFiscalYearList";
	public static final String TRANSACTION_STATUS = "transactionStatus";

	public static final String CASTER_CONFIGURATION_PATH = "CASTER_CONFIGURATION_PATH";

	public static final String STRING_ZERO_ONE = "01";
	public static final int INT_THIRTEN = 13;
	public static final String BUDGET_AMEND_TYPE = "TypeBudgetAmend";
	public static final String CONTRACT_BUDGET_TYPE = "TypeContractBudget";
	public static final String BUDGET_MOD_TYPE = "TypeBudgetModification";
	public static final String BUDGET_UPD_TYPE = "TypeBudgetUpdate";

	public static final String TASK_ROWS = "TaskRows";
	public static final String PLANNED_PROC_COUNT = "plannedProcurementCount";
	public static final String RELEASED_PROC_COUNT = "releasedProcurementCount";
	public static final String BUDGET_TYPE_CONTRACT_BUDGET = "CONTRACT_BUDGET";
	public static final String BUDGET_TYPE_BUDGET_AMENDMENT = "BUDGET_AMENDMENT";
	public static final String BUDGET_TYPE_BUDGET_MODIFICATION = "BUDGET_MODIFICATION";
	public static final String BUDGET_TYPE_BUDGET_UPDATE = "BUDGET_UPDATE";
	public static final String MSG_SUCCESSFULL_BUDGETSUBMIT = "MSG_SUCCESSFULL_BUDGETSUBMIT";
	public static final String MSG_SUCCESSFULL_INVOICESUBMIT = "MSG_SUCCESSFULL_INVOICESUBMIT";
	public static final String MSG_SUCCESSFULL_AMENDMENTSUBMIT = "MSG_SUCCESSFULL_AMENDMENTSUBMIT";
	public static final String MSG_SUCCESSFULL_MODIFICATIONSUBMIT = "MSG_SUCCESSFULL_MODIFICATIONSUBMIT";
	public static final String MSG_SUCCESSFULL_UPDATESUBMIT = "MSG_SUCCESSFULL_UPDATESUBMIT";
	public static final String SUBMIT_OVERLAY_SUCCESS = "SubmitOverlaySuccess";
	public static final String SUCCESS = "success";
	public static final String AGENCY_NOTIFY_PARAM = "agencyNotifyParam";
	
	//Addef for defect 8644 in R7
	public static final String MSG_SUCCESSFULL_MARK_AS_REGISTERED_REQUEST = "MSG_SUCCESSFULL_MARK_AS_REGISTERED_REQUEST";
	public static final String MSG_SUCCESSFULL_CANCEL_AND_MERGE_REQUEST = "MSG_SUCCESSFULL_CANCEL_AND_MERGE_REQUEST";

	public static final String AO_BASE_AWARD_EPIN = "aoBaseAwardEpin";
	public static final String GET_CONTRACT_REGISTERED = "getContractRegistered";
	public static final String CONTRACT_REGISTERED = "contractRegistered";
	public static final String PROCUREMENT_AGENCY_STATUS_ID = "statusAndAgencyId";
	public static final String PRINT_CACHE = "printCache";
	public static final String CLEAN_CACHE = "cleanCache";
	// Added as part of release 3.4.0 for unlocking with Lock Id - Starts
	public static final String CLEAN_SPECIFIC_LOCK = "cleanSpecificLock";
	public static final String PRINT_SPECIFIC_LOCK = "printSpecificLock";
	public static final String LOCK_ID_TEXT = "Lock ID: ";
	public static final String DOESNT_EXIST_TEXT = " does not exists.";
	public static final String SUCCESS_REMOVAL_TEXT = "Successfully Removed Lock!";
	public static final String REMOVE_HREF_P1 = "<br /><a href=\"/HHSPortal/CachingTransactionConfig.jsp?accoutRequestmodule=true&cleanSpecificLock=";
	public static final String REMOVE_HREF_P2 = "\" title=\"Remove Lock\">Click Here to Remove this Lock</a>";
	public static final String PRINT_HREF_P1 = "<a href=\"/HHSPortal/CachingTransactionConfig.jsp?accoutRequestmodule=true&printSpecificLock=";
	public static final String PRINT_HREF_P2 = "\" title=\"Print Details\">";
	public static final String PRINT_HREF_END = "</a>&nbsp;";
	public static final String DETAILS_TEXT = "<br />Details: ";
	public static final String PRINT_NEW_LINE = "<br /><br />";
	public static final String LOCK_DETAILS_TEXT = "Lock Details";
	public static final String REMOVING_FROM_CACHE_TEXT = "Removing From Cache - Lock ID: ";
	public static final String WITH_VALUE_TEXT = " with value: ";
	// Added as part of release 3.4.0 for unlocking with Lock Id - Starts
	public static final String REINITIALIZE_LOG4J = "reinitializeLog4j";
	public static final String CACHE_PRINT_BREAK = "<br /><br />===========<br /><br />";
	public static final String FETCH_CONTRACT_BUDGET_UPDATE_STATUS = "fetchContractBudgetUpdateStatus";
	public static final String FETCH_AWARD_AMOUNT_FOR_SELECTED_PROPOSALS = "fetchAwardAmountForSelectedProposals";
	public static final String ENTITY_TYPE_PROVIDER = "entityTypeProvider";

	public static final Map<String, String> AGENCY_PROVIDER_ENTITY_TYPE_MAP = new HashMap<String, String>();
	static
	{
		AGENCY_PROVIDER_ENTITY_TYPE_MAP.put(TASK_BUDGET_REVIEW, BUDGET_TYPE3);
		AGENCY_PROVIDER_ENTITY_TYPE_MAP.put(TASK_BUDGET_MODIFICATION, AUDIT_CONTRACT_BUDGET_MODIFICATION);
		AGENCY_PROVIDER_ENTITY_TYPE_MAP.put(TASK_BUDGET_UPDATE, AUDIT_CONTRACT_BUDGET_UPDATE);
		AGENCY_PROVIDER_ENTITY_TYPE_MAP.put(TASK_BUDGET_AMENDMENT, AUDIT_CONTRACT_BUDGET_AMENDMENT);
		AGENCY_PROVIDER_ENTITY_TYPE_MAP.put(TASK_INVOICE_REVIEW, AUDIT_INVOICES);

	}

	public static final String IS_PROC_CANCELLED = "isPrcoCancelled";
	public static final String RETURN_AMENDMENT_COF = "returnAmendmentCOFTask";

	public static final String FETCH_ADVANCE_NUMBER_COUNT = "fetchAdvanceNumberCount";

	public static final String CBY_FETCH_NEW_ADDED_SUB_BUDGET_LIST = "fetchNewlyAddedSubBudgetList";
	public static final String CBY_FETCH_NEWLY_CREATED_SUB_BUDGET_ID = "fetchNewlyCreatedSubBudgetId";
	public static final String CBY_LINK_SUB_BUDGET_TO_BASE = "linkSubBudgetToBase";
	public static final String CBY_LINK_PS_TO_BASE = "linkPersonnelServicesToBase";
	public static final String CBY_LINK_OPS_TO_BASE = "linkOperationsAndSupportToBase";
	public static final String CBY_LINK_UTILITIES_TO_BASE = "linkUtilitiesToBase";
	public static final String CBY_LINK_PFS_TO_BASE = "linkProfessionalServiceToBase";
	public static final String CBY_LINK_RENT_TO_BASE = "linkRentToBase";
	public static final String CBY_LINK_CS_TO_BASE = "linkContractedServicesToBase";
	public static final String CBY_LINK_RATE_TO_BASE = "linkRateToBase";
	public static final String CBY_LINK_MILESTONE_TO_BASE = "linkMilestoneToBase";
	public static final String CBY_LINK_UNALLOCATED_TO_BASE = "linkUnallocatedToBase";
	public static final String CBY_LINK_INDIRECT_TO_BASE = "linkIndirectToBase";
	public static final String CBY_LINK_PI_TO_BASE = "linkProgramIncomeToBase";
	public static final String CBY_LINK_FRINGE_TO_BASE = "linkFringeToBase";
	public static final String CBY_LINK_EQUIPMENT_TO_BASE = "linkEquipmemtToBase";
	public static final String CBM_DELETE_CONTRACT_REPLICA = "markContractAsDeleted";
	public static final String FINALIZE_BUTTON_ACTIVE = "finalizeButtonActive";
	public static final String SHOW_FINALIZE_BUTTON = "showFinalizeButton";
	public static final String FETCH_CONTRACT_BUDGET_AMEND_STATUS = "fetchContractBudgetAmendmentStatus";

	public static final String CBY_FETCH_HOURLY_EMPLOYEE_REMAINING_AMT = "fetchHourlyEmployeeForRemainingAmt";
	public static final String CBY_FETCH_HOURLY_EMPLOYEE_INVOICED_AMT = "fetchHourlyEmployeeForInvoicedAmt";
	public static final String CBY_FETCH_SEASONAL_EMPLOYEE_REMAINING_AMT = "fetchSeasonalEmployeeForRemainingAmt";
	public static final String CBY_FETCH_SEASONAL_EMPLOYEE_INVOICED_AMT = "fetchSeasonalEmployeeForInvoicedAmt";
	public static final String CBY_CHECK_SUBMIT_INVOICE_FEASIBILITY = "checkSubmitInvoiceFeasibility";
	public static final String PROP_CITY_URL = "PROP_CITY_URL";

	public static final String DISABLE_STATUS_FLAG = "disableStatusFlag";

	public static final String FUNDING_ALLOCATION_BEAN = "com.nyc.hhs.model.FundingAllocationBean";
	public static final String COPY_PROCUREMENT_FUNDING_SOURCE_DETAILS = "copyProcurementFundingSourceDetails";
	public static final String OBJECTS_PER_PAGE = "objectsPerPage";
	public static final String ADD_DOCUMENT_FROM_VAULT_COMPONENT_NAME = "AddDocumentFromVault";
	public static final String FETCH_LAST_FY_CONFIGURED = "fetchLastFYConfigured";
	public static final String ROADMAP_FILTER = "roadMapFilter";
	public static final String FILTER = "filter";
	public static final String FETCH_CONTRACT_INFO = "fetchContractInfo";
	public static final String REVERT_CONTRACT_TO_BASE_VALUE = "revertContractToBaseValue";
	public static final String UPDATE_PDF_STATUS_NOT_STARTED = "updatePdfStatusNotStarted";
	public static final String BUDGET_COUNT = "BUDGET_COUNT";

	public static final String CBM_UPDATE_BASE_CONTRACT_FINANCIAL_AMOUNT = "mergeContractFinancialForAmendment";
	public static final String CBM_UPDATE_BASE_CONTRACT_FINANCIALS = "mergeContractFinancialForUpdate";
	public static final String CBY_CREATE_CF_REPLICA = "createContractFinancialReplica";
	public static final String CBM_DELETE_CF_REPLICA = "markContractFinancialAsDeleted";

	public static final String INV_FETCH_INVOICETOTAL_PERSONNELSERVICES = "fetchInvoiceTotalForPersonnelServices";
	public static final String INV_FETCH_INVOICE_YTDTOTAL_PERSONNELSERVICES = "fetchYTDTotalSalaryAndFringe";
	public static final String FETCH_NONGRID_DATA_PERSONNELSERVICES = "fetchNonGridDataForPersonnelServices";

	public static final String FETCH_ADVNACE_DESC = "fetchAdvanceDesc";
	public static final String PAY_FETCH_ADVANCE_DESC = "fetchAdvanceDescForTaskHeader";
	public static final String CHANNEL_KEY_BUDGET_ADVANCE_DESC = "budgetAdvanceDesc";
	public static final String LOCK_CLEAR_TIME = "LockClearTime_LockClearTime";
	public static final String SESSION_DETAILS = "SessionDetails";
	public static final String AUDIT_PAYMENT = "Payments";
	public static final String AUDIT_ADVANCE_REQUEST = "Advance Request";
	public static final String AGENCY_TASK_TYPE_INBOX = "inbox";

	public static final Map<String, String> BUDGET_ENTITY_TYPE_MAP = new HashMap<String, String>();
	static
	{
		BUDGET_ENTITY_TYPE_MAP.put("2", BUDGET_TYPE3);
		BUDGET_ENTITY_TYPE_MAP.put("3", AUDIT_CONTRACT_BUDGET_MODIFICATION);
		BUDGET_ENTITY_TYPE_MAP.put("4", AUDIT_CONTRACT_BUDGET_UPDATE);
		BUDGET_ENTITY_TYPE_MAP.put("1", AUDIT_CONTRACT_BUDGET_AMENDMENT);
	}
	public static final String PAGINATION = "Pagination";
	public static final String FETCH_SELECTED_PROPOSALS_COUNT = "fetchCountofSelectedProposals";
	public static final String UPDATE_MODIFIED_FLAG_FROM_AWARD = "updateModifiedFlagFromAward";
	public static final String PARAM_KEY_OPERATION = "OPERATION";
	public static final String INV_INSERT_INVOICE = "initiateInvoice";

	public static final String FETCH_CONTRACT_CONF_COA_ORIGINAL_DETAILS = "fetchContractConfCOAOriginalDetails";
	public static final String FETCH_CONTRACT_TYPE = "fetchContractType";

	public static final String CANCELL_AWARD = "Cancel Award";
	public static final String AWARD_STATUS_CHANGED_UPDATE_IN_PROGRESS = "Status Changed from 'Approved' To 'Update In Progress'";
	public static final String CHANGE_STATUS = "Status Change";

	public static final String FETCH_OTHER_ENTRY_IF_EXISTS = "fetchOtherEntryIfExists";
	public static final String INSERT_AMEND_REC_IN_OTHER_DETAILS = "insertAmendRecInOtherDetails";

	public static final String PROPOSAL_STATUS_CHANGED = "Status Changed from '";
	public static final String CT_ID = "ctId";
	public static final String TO_IN_REVIEW = "' to 'In Review'";
	public static final String MAIN_TABLE = "mainTable";
	public static final String ADDENDUM_TABLE = "addendumTable";
	public static final String FETCH_CONTRACT_CONF_COA_DETAILS_AMENDMENT = "fetchContractConfCOADetailsAmendment";
	public static final String FETCH_BUDGET_ID_FROM_CONTRACT_ID = "fetchBudgetIdListFromContractId";
	public static final Map<String, String[]> CHANGE_CONTROL_SETTING = new HashMap<String, String[]>();
	static
	{
		CHANGE_CONTROL_SETTING.put("ProcurementSummary", new String[]
		{ "PROCUREMENT", "PROCUREMENT_ADDENDUM", "Modified_Date", "Modified_By_Userid" });
		CHANGE_CONTROL_SETTING.put("ServiceSelection", new String[]
		{ "PROCUREMENT_SERVICES", "PRCRMNT_ADDM_SERVICES", "Modified_Date", "Modified_By_Userid" });
		CHANGE_CONTROL_SETTING.put("ApprovedProviders", new String[]
		{ "PROCUREMENT", "PROCUREMENT_ADDENDUM", "sf_Modified_Date", "sf_Modified_Userid" });
		CHANGE_CONTROL_SETTING.put("ProposalConfiguration", new String[]
		{ "PROCUREMENT_QUESTION_CONFIG", "PRCRMNT_ADDM_QUESTION_CONFIG", "Modified_Date", "Modified_By_Userid" });
		CHANGE_CONTROL_SETTING.put("EvaluationCriteria", new String[]
		{ "EVALUATION_CRITERIA", "PRCRMNT_ADDM_EVALUATE_CRITERIA", "Modified_Date", "Modified_By_Userid" });
		CHANGE_CONTROL_SETTING.put("CompetitionConfiguration", new String[]
		{ "COMPETITION_POOL", "COMPETITION_POOL", "Modified_Date", "Modified_By_Userid" });
	}
	public static final String MODIFIED_BY = "modifiedBy";
	public static final String STATUS_SELECTED = "SELECTED";
	public static final String STATUS_NOT_SELECTED = "NOT SELECTED";
	public static final String INSERT_PROPOSAL_DOC_FOR_SUBMITTED_PROPOSAL = "insertProposalDocForSubmittedProposal";
	public static final String PROPOSAL_DOC_ADDENDUM_MESSAGE = "PROPOSAL_DOC_ADDENDUM_MESSAGE";
	public static final String SHOW_ADDENDUM_DOC_MESSAGE = "showAddendumDocMessage";
	public static final String LAST_MODIFIED_BY_NAME = "lastModifiedByName";

	public static final String FETCH_CONTRACT_CONF_COA_DELETED_ROWS = "fetchContractConfCOADeletedRows";
	public static final boolean INITIALIZATION_FALSE = false;
	public static final String BUDGET_ID_LIST = "budgetIdList";
	public static final String CT_NUMBER = "EXT_CT_NUMBER";
	public static final String PROVIDER_USER_ID = "providerUserId";
	public static final String AO_USER_HASH_MAP = "aoUserHashMap";
	public static final String DELETE_BASE_CONTRACT = "deleteBaseContract";
	public static final String DELETE_BASE_BUDGET = "deleteBaseBudget";
	public static final String DELETE_UDDATE_CONTRACT = "deleteUddateContract";
	public static final String DELETE_UPDATE_BUDGET = "deleteUpdateBudget";
	public static final String DELETE_MODIFICATION_BUDGET = "deleteModificationBudget";

	public static final String FETCH_BUDGET_LIST_TRN = "getBudgetIdListFromContractId";
	public static final String BUDGET_ID_HASH_KEY = "BUDGET_ID";
	public static final String STATUS_ID_KEY = "STATUS_ID";
	public static final String COLUMNS_FOR_TOTAL_AMENDMENT = "columnsForTotalAmendment";
	public static final String SCORES_SAVED_SUCCESSFULLY = "Your scores and comments have been saved successfully.";

	public static final String CS_INSERT_PROCUREMENT_COA_DETAILS_DUPLICATE = "insertProcurementCoADetailsDuplicate";
	public static final String CS_DELETE_ROWS_WITH_TYPE_UPDATE = "deleteRowsWithTypeUpdate";
	public static final String CS_INSERT_TASK_ROWS_AS_TYPE_UPDATE = "insertTaskRowsAsTypeUpdate";
	public static final String CS_DELETE_ROWS_WITH_TYPE_TASKROWS = "deleteRowsWithTypeTaskRows";

	public static final String CS_DELETE_PROC_FUND_ROWS_WITH_TYPE_UPDATE = "deleteProcFundRowsWithTypeUpdate";
	public static final String CS_INSERT_PROC_FUND_TASK_ROWS_AS_TYPE_UPDATE = "insertProcFundTaskRowsAsTypeUpdate";
	public static final String CS_DELETE_PROC_FUND_ROWS_WITH_TYPE_TASKROWS = "deleteProcFundRowsWithTypeTaskRows";
	public static final String FRINGE_BENEFITS_GRID_AMENDMENT_FETCH = "fringeBenefitsGridAmendmentFetch";
	public static final String FRINGE_BENEFITS = "fringeBenifits";

	public static final String CS_DELETE_ROWS_COA_DATES_CHANGED = "deleteRowsCoaDatesChanged";
	public static final String CS_INSERT_ROWS_COA_DATES_CHANGED = "insertRowsCoaDatesChanged";
	public static final String CS_DELETE_ROWS_FUNDING_DATES_CHANGED = "deleteRowsFundingDatesChanged";
	public static final String CS_INSERT_ROWS_FUNDING_DATES_CHANGED = "insertRowsFundingDatesChanged";
	public static final String FETCH_PROC_COF_DETAILS_FINANCIALS_ORIGINAL = "fetchProcurementOrigDetails";
	public static final String CS_SET_ORIGINAL_PROCUREMENT_VALUES = "setOriginalProcurementValues";
	public static final String EXISTING_FY = "existingFY";
	public static final String NEWLY_ADDED_FY_SET = "newlyAddedFY";
	public static final String AWARD_REVIEW_STATUS_ID_KEY = "AWARD_REVIEW_STATUS_ID";

	public static final String CB_PROGRAM_INCOME_BEAN = "aoCBProgramIncomeBean";
	public static final String INSERT_AMEND_REC_IN_OTHER_DETAILS_PROG_INC = "insertAmendRecInOtherDetailsProgInc";
	public static final String FETCH_OTHER_ENTRY_IF_EXISTS_PROG_INC = "fetchOtherEntryIfExistsProgInc";
	public static final String CONTRACT_TITLE_POP_UP = "contractTitlePopUp";
	public static final String FETCH_PREV_PROC_STATUS_ID = "fetchPrevProcStatusId";
	public static final String FRINGE_BENEFITS_GRID_MODIFICATION_FETCH = "fringeBenifitsGridModificationFetch";
	public static final String FETCH_INVOICE_TOTAL_CONTRACTED_SERVIES = "fetchInvoiceTotalContractedServices";
	public static final String VIEW_CONTRACT_CONFIGURATION = "View Contract Configuration";
	public static final String ACTION_SAVED = "Saved";
	public static final String FETCH_EVALUATION_LAST_COMMENT = "fetchEvaluationLastComment";
	public static final String CS_FETCH_TOT_SUB_BUDGET_AMOUNT = "fetchTotSubBudgetAmt";
	public static final String CS_FETCH_AMEND_BUDGET_DETAILS = "fetchAmendBudgetDetails";
	public static final String SAVED_EVALUATION = "SavedEvaluation";
	public static final String RESET = "reset";
	public static final String NAVIGATE_FROM_R2 = "NAVIGATE_FROM_R2";
	public static final String ZERO_AFTER_DECIMAL = ".00";

	public static final String FROM_SAVE_BUTTON = "fromSaveButton";
	public static final String CONFIRM_SCORES_TAB = "confirmScoresTab";
	public static final String FETCH_PROCURING_AGENCIES = "fetchProcuringAgenciesFromDB";
	public static final String PROCURING_AGENCIES_MAP = "procuringAgenciesMap";
	public static final String CONTRACT_TYPE_MAPPING_CONFIG_KEY = "contractTypeConfiguration";
	public static final String CONTRACT_TYPE_CONFIG_NODE_XPATH = "//contract_types";
	public static final String CONTRACT_TYPE_CONFIG_NODE_NAME = "contract";
	public static final String ERROR_PAGE_CANCEL_EVALUATION = "errorPageCancelEvaluation";
	public static final String FETCH_PROPOSAL_AND_ORG_NAME = "fetchProposalAndOrgName";
	public static final String PROPOSAL_ORG_MAP = "proposalOrgMap";
	public static final String FETCH_ORG_NAME_FROM_CONTRACT_ID = "fetchOrgNameFromContractId";
	public static final String CONTRACT_COF_APPROVED = "contractCofApproved";
	public static final String CONTRACT_BUDGET_APPROVED = "contractBudgetApproved";
	public static final String PROP_MOD_DATE = "PROP_MOD_DATE";
	public static final String FETCH_SERVICE_UNIT_FLAG = "fetchServiceUnitFlag";
	public static final String SERVICE_UNIT_VALUE = "serviceUnitValue";
	public static final String GET_PROPOSAL_SUBMITTED_DATE = "getProposalSubmittedDate";

	public static final String DELETE_RFP_DOC_DETAILS = "deleteRfpDocumentDetails";
	public static final String SAME_FILE_ERROR_MESSAGE_CHECK = "button below and rename this document";

	public static final String UPLOAD_PROCESS = "uploadProcess";
	public static final String IS_ADDENDUM_DOC = "hiddenAddendumType";
	public static final String ERROR_MESSSAGE_UNAUTHORIZED_ACCESS = "UNAUTHORIZED_ACCESS";
	public static final String ERROR_MESSAGE_PROP_FILE = "com.nyc.hhs.properties.errormessages";
	// Constants added as a part of 2.4.1 starts
	public static final String HHS_ENV = "hhs.env";
	public static final String CLIENT_IP = "10.155.43.79";
	// Constants added as a part of 2.4.1 ends

	// Constants added for enhancement 5415 Starts
	public static final String AO_EVALUATION_BEAN_LIST = "aoEvaluationBeanList";
	public static final String EVALUATION_SCORE_DETAILS_LIST = "evaluationScoreDetailsList";
	public static final String FETCH_EVALUATION_SCORE_DETAILS_FOR_EVALUATOR = "fetchEvaluationScoreDetailsForEvaluator";
	public static final String AO_QUERY_MAP = "aoQueryMap";
	public static final String AGENCY_WORKFLOW_SCOREDETAILS = "agencyWorkflow/scoredetails";
	// Constants added for enhancement 5415 ends

	// below constants are for version maintenance at point of review score task
	// (completion or scores returned)
	public static final String EVALUATION_VERSION_ARCHIVE = "evaluationVersionArchive";
	public static final String EVALUATION_SCORE_ARCHIVE = "evaluationScoreArchive";
	public static final String EVALUATION_GEN_COMMENT_ARCHIVE = "evaluationGenCommentArchive";
	public static final String REVIEW_TASK_PRESENT = "ReviewTaskPresent";
	// Constants added as part of build 2.6.0
	public static final String FETCH_PROC_DATES_PUBLISHED_AND_ADDENDUM = "fetchProcDatesPublishedAndAddendum";
	public static final String ERROR_INVALID_CHARACTERS = "errorInvalidCharacters";
	public static final String INVALID_CHARACTERS = "invalidCharacters";
	public static final String RFP = "Request for Proposals (RFP)";
	public static final String FINAL_SORTED_RFP_DOC_LIST = "finalSortedRFPDocumentList";
	public static final String OLD_DOCUMENT_ID_REQ = "OldDocumentIdReq";
	public static final String HIDDEN_IS_DOC_REQUIRED = "hiddenIsDocRequired";
	public static final String LS_IS_REQUIRED_DOC = "lsIsRequiredDoc";
	public static final String LO_IS_REQUIRED_DOC = "loIsRequiredDoc";
	public static final String MOVE_TO_NEXT_PAGE = "MoveToNextPage";
	public static final String HARD_DELETE_RFP_DOC_DETAILS = "hardDeleteRfpDocumentDetails";
	public static final String VALIDATE_PROC_VAL_ALLOC_VAL = "validateProcValueAndAllocatedValue";
	public static final String PCOF_VALIDATE_ERROR_MSG = "pcofValidateErrorMsg";
	public static final String PCOF_VALIDATE_ERROR_MESSAGE = "PCOF_VALIDATE_ERROR_MESSAGE";
	// Constants added as part of build 2.6.1
	public static final String ERROR_CONTRACT_TERM_EXCEED = "ERROR_CONTRACT_TERM_EXCEED";
	public static final String MAX_CONTRACT_TERM_SPAN = "MaxContractSpanTerm";

	// Constants added as part of build 2.7.0
	public static final String VIEW_BUDGET_DOCUMENT = "viewBudgetDocument";
	public static final String ADVANCE_REQUEST_REVIEW = "Advance Request Review";

	public static final String SELECTED_POOL = "selectedPool";
	public static final String SAVE_COMP_POOL = "saveCompetitionPool";
	public static final String GET_COMP_POOL_DATA = "getCompetitionPoolData";
	public static final String DELETE_REMOVE_COMP_POOL = "deleteRemoveCompetitionPool";
	public static final String INSERT_NEW_COMP_POOL = "insertNewCompetitionPool";

	//[Start] R6.3 QC 8683 insert new competition pool
	public static final String INSERT_NEW_COMP_POOL_NOT_EXIST = "insertNewCompetitionPoolNotIn";
	public static final String STATUS_REPLACED = "195";
	//[End] R6.3 QC 8683 insert new competition pool

	public static final String CHECK_COMP_POOL_EXISTS = "checkCompetitionPoolExists";
	public static final String FETCH_COMP_POOL_DATA = "fetchCompetitionPoolData";
	public static final String LS_SELECTED_POOL = "lsSelectedPool";
	public static final String SELECTED_COMP_POOLS = "selectedCompetitionPools";
	public static final String DISPLAY_COMP_POOL_CONF = "displayCompetitionConfiguration";
	public static final String FETCH_ALL_COMPETITION_POOL = "fetchAllCompetitionPool";
    //[Start] R6.3 QC 8899 insert new competition pool
    public static final String FETCH_ALL_COMPETITION_POOL_FLAG = "fetchAllCompetitionPoolWithProposalId";
    //[End] R6.3 QC 8899 insert new competition pool
	
	
	public static final String COMPETITION_POOL_MISSING = "ERROR_COMPETITION_POOL_MISSING";
	public static final String COMPETITION_POOL_ID = "competitionPoolId";
	public static final String COMPETITION_POOL = "competitionPool";
	public static final String PROCUREMENT_FAVORITE = "procurementFavorite";
	public static final String FAVORITE_FLAG = "favoriteFlag";
	public static final String FAVORITE_IDS = "favoriteIds";
	public static final String NON_FAVORITE_IDS = "nonFavoriteIds";
	public static final String PROCUREMENT_ID_LIST = "procurementIdList";
	public static final String FETCH_EVALUATION_SUMMARY = "fetchEvaluationSummary";
	public static final String EVALUATION_SUMMARY_LIST = "evaluationSummaryList";
	public static final String COMPETITION_POOL_TITLE = "competitionPoolTitle";
	public static final String CHECK_IF_FAV_EXISTS = "checkIfFavoriteExists";
	public static final String SAVE_FAVORITES = "saveFavorites";
	public static final String DELETE_FAVORITES = "deleteFavorites";
	public static final String UPDATE_FAVORITES_TRX = "updateFavoritesTransaction";
	public static final String INSERT_EVALUATION_GROUP = "insertEvaluationGroup";
	public static final String INSERT_GROUP_COMP_MAPPING = "insertGroupCompetitionMapping";
	public static final String INSERT_GROUP_COMP_ADDENDUM_MAPPING = "insertGroupCompetitionAddendumMapping";  
	public static final String COM_NYC_HHS_MODEL_RFP_RELEASE_BEAN = "com.nyc.hhs.model.RFPReleaseBean";
	public static final String RFP_RELEASE_BEAN = "rfpReleaseBean";
	public static final String EVALUATION_GROUP_TITLE = "Evaluation Group";
	public static final String EVALUATION_GROUP_ID = "evaluationGroupId";
	public static final String EVALUATION_POOL_MAPPING_ID = "evaluationPoolMappingId";
	public static final String UPDATE_EVALUATOR_COUNT = "updateEvaluatorCount";
	public static final String FETCH_EVALUATION_GROUP_PROPOSAL = "fetchEvaluationGroupProposal";
	public static final String AMENDMENT_MAPPING_FILE_NAME = "amendmentListMapping";
	public static final String AMENDMENT_LIST = "AmendmentList";
	public static final String GET_AMENDMENT_LIST = "getAmendmentList";
	public static final String UPDATE_EVALUATION_GROUP_FOR_PROPOSAL = "updateEvaluationGroupForProposal";
	public static final String FETCH_EVALUATION_GROUPS_PROCUREMENT = "fetchEvaluationGroupsProcurement";
	public static final String EVALUATION_GROUP_LIST = "evaluationGroupList";
	public static final String FETCH_COMPETITION_POOL_TITLE = "fetchCompetitionPoolTitle";
	public static final String FETCH_COMPETITION_POOL_TITLE_PROC = "fetchCompetitionPoolTitleProc";
	public static final String FETCH_GROUP_TITLE_DATE = "fetchGroupTitleAndDate";
	public static final String FETCH_GROUP_TITLE_DATE_GROUP = "fetchGroupTitleAndDateGroup";
	public static final String COMPETITION_POOL_LIST = "competitionPoolList";
	public static final String GROUP_TITLE_MAP = "groupTitleMap";
	public static final String EVALUATION_CLOSE_DATE = "evaluationCloseDate";
	public static final String EVALUATION_GROUP_TITLE_TABLE_COL = "EVALUATION_GROUP_TITLE";
	public static final String EVALUATION_CLOSE_DATE_TABLE_COL = "closing_date";
	public static final String EVALUATION_GROUPS_SUMMARY = "EvaluationGroupsSummary";
	public static final String EVALUATION_GROUP_PROPOSAL = "evaluationGroupProposal";
	public static final String EVALUATION_GROUPS_PROPOSAL_JSP_PATH = "evaluation/evaluationGroupsProposal";
	public static final String UPDATE_EVAL_GRP_STATUS = "updateEvalGroupStatus";
	public static final String CLOSE_GROUP_FLAG = "closeGroup";
	public static final String CLOSE_GROUP_SUBMISSION = "closeGroupSubmissions";

	// R4 amendment and update constants
	public static final String FETCH_UPDATE_CONFIGURATION_DETAILS = "fetchUpdateConfigurationDetails";
	public static final String FETCH_ALL_NEGATIVE_AMEND_AMOUNTS = "fetchAllNegativeAmendmentAmounts";
	public static final String BMC_INSERT_NEW_UPDATE_BUDGET_DETAILS = "insertNewUpdateBudgetDetails";
	public static final String CS_ADD_CONTRACT_CONF_AMEND_TASK_DETAILS = "addContractConfAmendmentTaskDetails";
	public static final String CS_EDIT_CONTRACT_CONF_AMEND_TASK_DETAILS = "editContractConfAmendmentDetails";
	public static final String DEL_CONTRACT_CONF_AMEND_TASK_DETAILS = "delContractConfAmendTaskDetails";
	public static final String CS_EDIT_CONTRACT_CONF_AMEND_SUB_BUDGET_DETAILS = "editContractConfAmendSubBudgetDetails";
	public static final String CS_FETCH_CONTRACT_CONF_AMEND_SUBBUDGET_DETAILS = "fetchContractConfAmendSubBudgetDetails";
	public static final String CS_FETCH_SUB_BUDGET_PARENT_ID_AMENDMENT = "fetchSubBudgetParentIdAmendment";
	public static final String BMC_CONTRACT_BUDGET_AMEND_FY_DATA = "contractBudgetAmendFYData";
	public static final String FY_AMEND_DETAIL_MAP = "loFYAmendDetailMap";
	public static final String POS_AMEND_FY_PENDING = "posAmendAmtforFY";
	public static final String NEG_AMEND_FY_PENDING = "negAmendAmtforFY";
	public static final String CS_FETCH_UPDATE_FY_PLANNED_AMOUNT = "fetchUpdateFYPlannedAmount";
	public static final String UPDATE_AMENDMENT_CONTRACT_STATUS = "updateAmendmentContractStatus";
	public static final String INSERT_PDF_BACTH_FOR_AMENDMENT_COF = "insertPdfBatchForAmendmentCof";
	public static final String GET_AMENDMENT_BUDGETS_COUNT = "getAmendmentBudgetsCount";
	public static final String SET_AMEND_CONTRACT_STATUS_PEND_APPROVAL = "setAmendmentContractStatusPendingApproval";
	public static final String TOTAL_BUDGET_COUNT = "TOT_BUD_COUNT";
	public static final String BUDGET_PENDING_APPROVAL_COUNT = "BUD_PEND_APPROVAL_COUNT";
	public static final String CS_ADD_CONTRACT_CONF_AMENDMENT_BUDGET_DETAILS = "addContractConfAmendmentBudgetDetails";
	public static final String UPDATE_AMEND_CONTRACT_STATUS_PEND_REGISTRATION = "updateAmendContractStatusToPendReg";
	public static final String CBL_86 = "86";
	public static final String CS_DEL_CONTRACT_CONF_AMEND_SUBBUDGET_DETAILS = "delContractConfAmendSubBudgetDetails";

	
	
	public static final String UPDATE_EVAL_GROUP_STATUS = "updateEvalGroupStatus";
	public static final String UPDATE_EVAL_POOL_MAPPING_STATUS = "updateEvalPoolMappingStatus";
	public static final String EVAL_DATA_MAP = "evalDataMap";
	public static final String EVAL_GROUP_STATUS = "evalGroupStatus";
	public static final String EVAL_POOL_MAPPING_STATUS = "evalPoolMappingStatus";
	public static final String PREV_STATUS_ID = "PREV_STATUS_ID";
	public static final String IS_OPEN_ENDED_RFP = "IS_OPEN_ENDED_RFP";
	public static final String DEL_EVAL_GROUP_DATA = "delEvalGroupData";
	public static final String DEL_EVAL_POOL_MAPPING_DATA = "delEvalPoolMappingData";
	public static final String FETCH_AMOUNT_NE_AMENDMENT_FY_BUDGET = "fetchAllNeAmendmentFYBudgetNotApprovedAmt";
	public static final String FETCH_PENDING_NE_AMENDMENT_SUBBUDGET_AMOUNT = "fetchAllAmendedSubBudgetAmount";
	public static final String ERROR_NEGATIVE_AMENDMENT_VALIDATION_MESSAGE = "ERROR_NEGATIVE_AMENDMENT_VALIDATION_MESSAGE";
	public static final String EVAL_GROUP_PROPOSAL_SESSION_BEAN = "sessionEvalGroupProposalBean";
	public static final String EVAL_GROUP_TITLE = "evaluationGroupTitle";
	public static final String EVAL_GROUP_PROPOSAL_BEAN = "evalGroupProposalBean";
	public static final String COM_NYC_HHS_MODEL_EVAL_GRP_PRP = "com.nyc.hhs.model.EvaluationGroupsProposalBean";
	public static final String FETCH_PROPOSAL_COUNT_AND_COMP_ID = "fetchProposalCountAndCompId";
	public static final String PROPOSAL_STATUS_KEY = "asProposalStatus";
	public static final String COMPETITION_POOL_PROPOSAL_RECEIVED = "asCompPoolPropReceived";
	public static final String COMPETITION_POOL_NO_PROPOSALS = "asCompPoolNoStatus";
	public static final String CLOSE_GROUP_INPUT_PARAM_MAP = "aoCloseGroupInputParam";
	public static final String UPDATE_COMP_POOL_STATUS = "updateCompPoolStatus";
	public static final String COMP_POOL_STATUS = "aoCompPoolStatus";
	public static final String COMP_CONF_ID = "aoCompConfId";
	public static final String EVAL_SUMMARY_SESSION_BEAN = "sessionEvalSummaryBean";
	public static final String EVALUATION_SUMMARY_BEAN = "evaluationSummaryBean";
	public static final String COM_NYC_HHS_MODEL_EVAL_SUMMARY = "com.nyc.hhs.model.EvaluationSummaryBean";
	public static final String COMPETITION_POOL_TITLE_TABLE_COL = "COMPETITION_POOL_TITLE";
	public static final String EVALUATION_SUMMARY_KEY = "EvaluationSummary";
	public static final String GROUP_PROPOSAL_SUMMARY_KEY = "GroupProposalSummary";
	public static final String FETCH_EVALUATION_SUMMARY_COUNT = "fetchEvaluationSummaryCount";
	public static final String EVALUATION_SUMMARY_COUNT = "evaluationSummaryCount";
	public static final String FETCH_EVAL_GRP_PROPOSAL_COUNT = "fetchEvalGroupProposalCount";
	public static final String EVAL_GRP_PROPOSAL_COUNT = "evalGroupProposalCount";
	public static final String CLOSE_GROUP_SUCCESS = "closeGroupSuccess";
	public static final String CLOSE_ALL_SUBMITTION_SUCCESS = "closeAllSubmittionSuccess";
	public static final String CLOSE_ALL_SUBMISSIONS = "closeAllSubmissions";
	public static final String FETCH_EVALUATION_GROUP_ID = "fetchEvaluationGroupId";
	public static final String COMP_POOL_STATUS_CHANGED_TO_PROPOSAL_RECEIVED = "Competition Pool Status has been changed to Proposals Received";
	public static final String PROPOSAL_COUNT_TABLE_COL = "PROPOSAL_COUNT";
	public static final String COMPETITION_POOL_ID_COL = "COMPETITION_POOL_ID";
	public static final String BUTTON_VALUE = "buttonValue";
	public static final String UPDATE_AMENDMENT_STATUS_COF = "updateAmendmentContractStatusCoF";
	public static final String GET_MIN_POOL_MAPPING_STATUS_ID = "getMinPoolMappingStatusId";
	public static final String MIN_STATUS_ID = "minStatusId";
	public static final String CLOSE_SUBMISSION_FLAG = "closeSubmissionFlag";
	public static final String COMP_POOL_TITLE = "COMPETITION_POOL_TITLE";
	public static final String EVALUATION_GROUP = "EVALUATION_GROUP_TITLE";
	public static final String IS_OPEN_ENDED_PROC = "IS_OPEN_ENDED_RFP";
	public static final String COMP_TITLE = "compPoolTitle";
	public static final String EVALUATION_TITLE = "evalGroupTitle";
	public static final String OPEN_ENDED_PROC = "isOpenEndedProc";
	public static final String GET_PROPOSAL_AND_POOL_STATUS = "getProposalAndPoolStatus";
	public static final String EVALUATION_POOL_STATUS_ID = "EVALUATION_POOL_STATUS_ID";
	public static final String CHECK_PROPOSAL_EDIT_SUBMIT = "checkProposalEditSubmit";

	// PDF Constants

	public static final String FETCH_ENTITY_ID_FOR_PDF = "fetchEntityIdForPdf";
	public static final String FETCH_ENTITY_ID_FOR_PDF_R2 = "fetchEntityIdForPdfR2";
	public static final String FETCH_SUB_ENTITY_ID_FOR_PDF = "fetchSubEntityIdForPdf";
	public static final String UPDATE_STATUS_FOR_PDF = "updateStatusForPdf";
	public static final String UPDATE_SUB_ENTITY_STATUS_FOR_PDF = "updateSubEntityStatusForPdf";
	public static final String UPDATE_STATUS_FOR_PDF_AFTER_UPLOAD = "updateStatusForPdfAfterUpload";
	public static final String GENERATED_STATE = "Generated";
	public static final String FETCH_ENTITY_ID_UPDATE_STATUS_FOR_PDF = "fetchEntityIdAndUpdateStatusForPdf";
	public static final String FETCH_SUB_ENTITY_ID_UPDATE_STATUS_FOR_PDF = "fetchSubEntityIdAndUpdateStatusForPdf";
	public static final String UPLOAD_AMENDMET_DOC_IN_FILENET = "uploadAmendmentDocumentInFilenet";
	public static final String AWARD_DOCUMENTS_FOLDER_NAME = "AWARD & RFP DOCUMENTS";
	public static final String UPLOAD_AMENDMET_DOC_IN_FILENET_ENTITY_TYPE = "uploadAmendmentDocumentInFilenetEntityType";
	public static final String UPLOAD_AMENDMET_DOC_IN_FILENET_FOR_SUB_ENTITY = "uploadAmendmentDocumentInFilenetForSubEntity";
	public static final String HEADER_LABEL = "asHeaderLabel";
	public static final String LO_SUCCESS = "loSuccess";
	public static final String DOWNLOAD_POF_BUDGET_SUMMARY_DETAILS = "DownloadPOFandBudgetSummaryDetails";
	public static final String CONTRACT_BUDGET_AMENDMENT_SUMMARY_DETAILS = "Contract Budget Amendment Summary Details";
	public static final String CONTRACT_BUDGET_SUMMARY_DETAILS = "Contract Budget Summary Details";
	public static final String CONTRACT_CERTIFICATION_FUNDS = "Contract Certification of Fund";
	public static final String CONTRACT_CERTIFICATION_FUND_AMENDMENT = "Contract Certification of Fund - Amendment";
	public static final String PROCUREMENT_CERTIFICATION_FUNDS = "Procurement Certification of Fund";
	public static final String LO_SUBBUDGET_LIST = "loSubBudgetList";
	public static final String LO_SUBBUDGET_ID = "loSubBudgetId";
	public static final String FETCH_BUDGET_SUMMARY_PDF = "fetchBudgetSummaryForPDF";
	public static final String CHART_ACCOUNT_ALLOCATION_AMENDMENT = "Chart of Accounts Allocation - Amendment";
	public static final String CHART_ACCOUNTS = "Chart of Accounts";
	public static final String FUNDING_SOURCE_ALLOCATION_AMNEDMENT = "Funding Source Allocation - Amendment";
	public static final String FUNDING_SOURCE = "Funding Sources";
	public static final String LINE_ITEM = "Line Item";
	public static final String FY_BUDGET = "FY Budget";
	public static final String APPROVED_FY_BUDGET = "Approved FY Budget";
	public static final String AMENDMENT_AMOUNT = "Amendment Amount";
	public static final String YTD_INVOIVE_AMOUNT = "YTD Invoiced Amount";
	public static final String REMAINING_AMOUNT = "Remaining Amount";
	public static final String FEILD_VALUE = "value";
	public static final String TABLE_COUNT = "tableCount";
	public static final String BUDGET_SUMMARY_DETAILS = "//fields//Budget-Summary-Details";
	public static final String INDIRECT_RATE = "Indirect Rate";
	public static final String VALUE_FOR_PDF = "values";
	public static final String BEAN_VALUE = "beanValue";
	public static final String LABEL = "Label";
	public static final String CONTRACT_ID1 = "contractId";
	public static final String BUDGET_ID_WORKFLOW = "budgetId";
	//Added in R6: return payment review task
	public static final String RETURN_PAYMENT_DETAIL_ID = "returnPaymentDetailId";
	public static final String RETURN_PE_PAYMENT_DETAIL_ID = "returnPaymentDetailID";
	//Added in R6: return payment review task end
	public static final String AMMENDMENT_BUDGET_SCREENFILED_MAPPING_XML = "/com/nyc/hhs/config/ammendmentBudgetScreenFieldMapping.xml";
	public static final String ACCOUNT_ALLOCTAION_CHART = "Chart of Accounts Allocation ";
	public static final String CONTRACT_FYMAP = "CONTRACTFYMAP";
	public static final String CONTRACT_FY_MAP = "contractFyMap";
	public static final String IS_CURRENCY = "isCurrency";
	public static final String ACC_TOTAL = "Total";
	public static final String COA = "COA";
	public static final String FUND = "FUND";
	public static final String FUNDING_SOURCE_ALLOCATION_AMENDMENT = "Funding Source Allocation - Amendment";
	public static final String FUNDING_SOURCE_ALLOCATION = "Funding Source Allocation";
	public static final String LEFT_BRACKET = "(";
	public static final String RIGHT_BRACKET = ")";
	public static final String COMMA = ",";
	public static final String PDF_CONTRACT_DETAILS = "fetchContractDetailsForPDF";
	public static final String PDF_PROCUREMENT_CONTRACT_DETAILS = "fetchProcurementDetailsForPDF";
	public static final String PDF_CONTRACT_ACCOUNT_FY_DETAILS = "fetchContractAccountAllocationFYDetails";
	public static final String CONTRACT_FY_DETAILS = "aoContractFyDetailsMap";
	public static final String CONTRACT_ACCOUNT_ALLOCATION_DETAILS = "fetchContractAccountAllocationDetails";
	public static final String PROCUREMENT_ACCOUNT_ALLOCATION_DETAILS = "fetchProcAccountAllocationDetails";
	public static final String AMENDMENT_FISCIAL_E_PIN_DETAILS = "loAmendmnetFiscialEpinDetails";
	public static final String SPACE_TOTAL_DIRECT_COST = "         Total Direct Cost";
	public static final String SPACE_INDIRECT_RATE_PERCENTGE = "           Indirect Rate";
	public static final String CBY_FETCH_AMENDMENT_FISCIAL_EPIN_DETAILS = "fetchAmendmentFiscialEpinDetails";
	public static final String DOUBLE_SPACE = "   ";
	public static final String FETCH_AMENDMENT_TITLE_FISCIAL_EPIN_DETAILS = "fetchAmendmentTitleFiscialEpinDetails";
	public static final String SPACE_PERCENT = " %";
	public static final String FETCH_SUB_BUDGET_DETAILS_MODIFICATION_SUMMARY = "fetchSubBudgetDetailsModificationSummary";
	public static final String FETCH_BUDGET_AMENDMENT_SUMMARY_PDF = "fetchBudgetSummaryAmendmentForPDF";
	public static final String SPACE_INDIRECT = "           indirect rate";
	public static final String SPACE_TOTAL_INDIRECT_COSTS = "         Total Indirect Costs";
	public static final String SPACE_TOTAL_SALARY_AND_FRINGE = "           Total Salary and Fringe";
	public static final String SPACE_TOTAL_OTPS = "           Total OTPS";
	public static final String SPACE_TOTAL_RATE_BASED = "           Total Rate Based";
	public static final String SPACE_TOTAL_MILESTONE_BASED = "           Total Milestone Based";
	public static final String SPACE_TOTAL_UNALLOCATED_FUNDS = "           Unallocated Funds";
	public static final String TOTAL_PROGRAME_INCOME = "Total Programe Income(Excleduded from city funded budget; Not invoiced)";
	public static final String TOTAL_PROGRAME_BUDGET = "Total Programe budget(City funded budget Programe Income)";
	public static final String TOTAL_CITY_FUNDED_BUDGET = "Total City Funded Budget";
	public static final String FETCH_AWARD_DOCUMENTSLIST = "awardDocumentList";
	public static final String FETCH_APPROVED_BASE_BUDGET_ID = "fetchApprovedBaseBudgetId";
	public static final String FETCH_AWARD_DOCUMENTS_TYPES = "fetchAwardDocTypes";
	public static final String AWARD_DOCUMENTS_LIST = "loAwardDocumentList";
	public static final String DOT_CLASS = ".class";
	public static final String PDF_GENERATION_BATCH_CLASS = "com/nyc/hhs/batch/impl/PDFGenerationBatch.class";
	public static final String CASTOR_MAPPING_ENTITY_XML = "com/nyc/hhs/config/castor-mapping-Entity.xml";
	public static final String PDF_BATCH = "aoPDFBatch";
	public static final String PROCUREMENT_COF_BEAN = "aoProcCofBean";
	public static final String FIELD_X_PATH = "//fields//Budget-Summary-Details";
	public static final String AMENDMENT_BUDGET_SCREEN_FIELD_MAPPING = "/com/nyc/hhs/config/ammendmentBudgetScreenFieldMapping.xml";
	public static final String SUB_FIELD_HEADING_X_PATH = "//fields//Budget-Summary-Details//header//field-name";
	public static final String FISICAL_YEAR_HEADING_PATH = "//fields//Budget-Summary-Details//header-fiscal-year-budget-information-header//field-name";
	public static final String FORWARDSLASH_RIGHT_SQUARE_BRACKET = "\"]";
	public static final String FIELD_X_PATH_TAB = "//fields//Budget-Summary-Details//field-name[@tableId=\"tab";
	public static final String CONTRACT_FIELD = "//fields//contract-fields//field-name";
	public static final String CONTRACT_COF_FIELD = "//fields//contract-Certification-fields//field-name";
	public static final String RFP_DOCUMENTS_LIST = "listOfRFPdocuments";
	public static final String CONTRACT_INFORMATION = "Contract Information";
	public static final String CONTRACT_AMOUNT_PDF = "Contract Amount:";
	public static final String AMENDMENT_FIELD_HEADING_X_PATH = "//fields//Budget-Summary-Details//header-Amendment//field-name";
	public static final String PROC_VALUE_PDF = "Procurement Value:";
	public static final String CONTRACT_VALUE_PDF = "Contract Value:";
	public static final String AMENDMENT_VALUE_PDF = "Amendment Value:";
	public static final String AMENDMENT_AMOUNT_PDF = "Amendment Amount:";
	public static final String CONTRACT_TERM = "Contract Term:";
	public static final String CONTRACT_TERM_DATE = "contractTermDate";
	public static final String AMENDMENT_TERM_DATE = "amendmentTermDate";
	public static final String DATE_SPACE = " - ";
	public static final String START_DATE = "Start Date";
	public static final String END_DATE = "End Date";
	public static final String YTD_ACTUAL_PAID_AMOUNT = "YTD Actual Paid Amount";
	public static final String FISCAL_YEAR_BUDGET_INFORMATION = "Fiscal Year Budget Information";
	// Tab Level Comments - Constants

	public static final String TAB_LEVEL_URL_PARAM = "tabLevel";
	public static final String PUBLIC_COMMENT_AREA_TAB_LEVEL = "publicCommentArea";
	public static final String PARAMATER_ENTITY_TAB_LEVEL = "entityTpeTabLevel";

	public static final Map<String, Integer> TAB_HIGHLIGHT_IDENTIFIER_MAP = new HashMap<String, Integer>();
	static
	{
		TAB_HIGHLIGHT_IDENTIFIER_MAP.put("personnelServices", 1);
		TAB_HIGHLIGHT_IDENTIFIER_MAP.put("operationAndSupport", 2);
		TAB_HIGHLIGHT_IDENTIFIER_MAP.put("utilities", 3);
		TAB_HIGHLIGHT_IDENTIFIER_MAP.put("professionalServices", 4);
		TAB_HIGHLIGHT_IDENTIFIER_MAP.put("rent", 5);
		TAB_HIGHLIGHT_IDENTIFIER_MAP.put("contractedServices", 6);
		TAB_HIGHLIGHT_IDENTIFIER_MAP.put("rate", 7);
		TAB_HIGHLIGHT_IDENTIFIER_MAP.put("milestone", 8);
		TAB_HIGHLIGHT_IDENTIFIER_MAP.put("unallocatedFunds", 9);
		TAB_HIGHLIGHT_IDENTIFIER_MAP.put("indirectRate", 10);
		TAB_HIGHLIGHT_IDENTIFIER_MAP.put("programIncome", 11);
	}
	public static final String CHANNEL_PARAM_TAB_HIGHLOGHT_LIST = "tabHighlightList";
	public static final String PARAMATER_TEXTAREA_TAB_LEVEL_IDS = "textAreaTabLevelIds";
	public static final String FETCH_FROM_USER_COMMENTS_PROVIDER = "fetchUserCommentsForTabLevelAuditProvider";
	public static final String FETCH_FROM_USER_COMMENTS_AGENCY = "fetchUserCommentsForTabLevelAuditAgency";
	public static final String DELETE_FROM_USER_COMMENTS_PROVIDER = "deleteFromUserCommentForTabLevelProvider";
	public static final String DELETE_FROM_USER_COMMENTS_AGENCY = "deleteFromUserCommentForTabLevelAgency";
	public static final String DELETE_FROM_AGENCY_AUDIT_TAB_HIGHLGHT_ENTRIES_ON_SUBMIT = "deleteTabHighlightTabLevelFromAgencyAuditOnSubmit";
	public static final String CHANNEL_PARAM_TAB_LEVEL_COMMENTS_MAP_PROVIDER = "loTabLevelCommentsMap";
	public static final String AGENCY_AUDIT_EVENT_NAME_HIGHLIGHT_TAB = "TLC: Highlight Tab";
	public static final String PARAM_USER_COMMENT_TABLE_ENTITY_ID = "ENTITY_TYPE";
	public static final String PARAM_USER_COMMENT_TABLE_PROVIDER_COMMENTS = "PROVIDER_COMMENT";
	public static final String PARAM_USER_COMMENT_TABLE_INTERNAL_COMMENTS = "INTERNAL_COMMENT";
	public static final String UNLOCK_PROPOSAL = "Unlock Proposal";
	public static final String UNLOCK_PROPOSAL_TRANS = "unlockProposal";
	public static final String CHECK_SEND_EVAL_VISIBILITY_STATUS = "checkSendEvalVisiblityStatus";
	public static final String PROPOSAL_NON_RESPONSIVE = "proposalNonResponsive";
	public static final String PROPOSAL_ACCEPTED_FOR_EVAL = "proposalAcceptedForEval";
	public static final String PROPOSAL_COUNT_NOT_ACCEPTED_OR_NON_RESP = "proposal_count";
	public static final String PROPOSAL_COUNT_TOTAL = "proposal_count_total";
	public static final String EVALUATOR_COUNT = "evaluatorCount";
	public static final String SEND_EVAL_BUTTON_STATUS = "sendEvalButtonStatus";
	public static final String EVALUATION_POOL_STATUS = "evaluationPoolStatus";
	public static final String CHECK_CANCEL_EVAL_VISIBLITY_STATUS = "checkCancelEvalVisibilityStatus";
	public static final String DELETE_AWARD_DATA = "deleteAwardData";
	public static final String CLEAR_EVAL_SENT_FLAG = "clearEvalSentFlag";
	public static final String UPDATE_PROC_STATUS_GROUP_BASIS = "updateProcurementStatusBasedOnGroup";
	public static final String GET_MIN_GROUP_STATUS_ID = "getMinGroupStatusId";
	public static final String IS_OPEN_ENDED_OR_ZERO_VALUE = "isOpenEndedOrZeroValue";
	public static final String FETCH_RFP_BEFORE_R4_FLAG = "fetchRfpReleasedBeforeR4Flag";
	public static final String NEGATIVE_AMENDMENT_COUNT = "negativeAmendment";
	public static final String BUDGET_APPROVED_COUNT = "budgetApprovedCount";
	public static final String OTHER_NEGATIVE_AMENDMENT_COUNT = "negAmendCount";
	public static final String UPDATE_BUDGET_COUNT = "updateBudgetCount";
	public static final String CLC_AMENDMENT_ID_UNDERSCORE = "amendment_contract_id";
	public static final String EVALUATION_GROUP_ID_KEY = "asEvaluationGroupId";
	public static final String PROPOSAL_STAT = "proposalStatusId";
	public static final String UPDATE_PROP_STAT_FRM_TASK_FOR_POOL_ID = "updatePropStatFromTaskForPoolId";
	public static final String COMPETITION_POOL_STATUS = "competitionPoolStatus";
	public static final String GET_PROPOSAL_STATUS = "getProposalStatus";
	public static final String COMP_POOL_NOT_AVAILABLE = "compPoolNotAvailable";
	public static final String FETCH_EVALUATION_GROUP_AWARD = "fetchEvaluationGroupAwards";
	public static final String FETCH_EVALUATION_GROUP_AWARD_LIST = "fetchEvaluationGroupAwardsList";
	public static final String FETCH_EVALUATION_GROUP_AWARD_LIST_COUNT = "fetchEvaluationGroupAwardsListCount";
	public static final String EVALUATION_GROUP_AWARDS = "evaluationGroupAward";
	public static final String SUBMISSION_CLOSE_DATE = "SUBMISSION_CLOSE_DATE";
	public static final String CLOSING_DATE = "closingDate";
	public static final String EVALUATION_GROUP_TITLE_COLUMN = "EVALUATION_GROUP_TITLE";
	public static final String AWARDS_CONTRACT_SUMMARY_KEY = "AwardsAndContractSummary";
	public static final String EVALUATION_GROUP_AWARD_BEAN = "evaluationGroupAwardBean";
	public static final String SESSION_EVALUATION_GROUP_AWARD_BEAN = "sessionEvaluationGroupAwardBean";
	public static final String EVALUATION_GROUP_AWARD_COUNT = "evaluationGroupAwardCount";
	public static final String COM_NYC_HHS_MODEL_EVAL_GROUP_AWARD = "com.nyc.hhs.model.EvaluationGroupAwardBean";
	public static final String EVALUATION_SENT_FLAG = "evaluationSentFlag";
	public static final String AWARD_CONTRACT_SUMMARY_BEAN = "awardsContractSummaryBean";
	public static final String FETCH_GROUP_AWARDS_CONTRACTS = "fetchGroupAwardsContracts";
	public static final String FETCH_GROUP_AWARDS_CONTRACTS_COUNT = "fetchGroupAwardsContractsCount";
	public static final String COM_NYC_HHS_MODEL_AWARD_CONTRACT_SUMMARY = "com.nyc.hhs.model.AwardsContractSummaryBean";
	public static final String SESSION_SELECTION_DETAILS_SUMMARY_BEAN = "sessionSelectionDetailsSummaryBean";
	public static final String SELECTION_DETAILS_SUMMARY_BEAN = "selectionDetailsSummaryBean";
	public static final String SELECTION_DETAILS_SUMMARY_KEY = "SelectionDetailsSummary";
	public static final String COM_NYC_HHS_MODEL_SELECTION_DETAIL_SUMMARY = "com.nyc.hhs.model.SelectionDetailsSummaryBean";
	public static final String FETCH_SELECTION_DETAILS_SUMMARY_LIST = "fetchSelectionDetailsSummaryList";
	public static final String FETCH_SELECTION_DETAILS_SUMMARY_LIST_COUNT = "fetchSelectionDetailsSummaryListCount";
	public static final String FETCH_EVAL_POOL_MAPPING_ID = "fetchEvalPoolMappingId";
	public static final String FETCH_DEFAULT_CONFIG_ID = "fetchDefaultConfigId";
	public static final String DEFAULT_CONFIG_ID = "defaultConfigId";
	public static final String DEFAULT_CONFIGURATIONS_CHECKED = "defaultConfigurationsChecked";
	public static final String FETCH_GROUP_SELECTION_DETAILS = "fetchGroupSelectionDetails";
	public static final String GROUP_SELECTION_DETAILS_COUNT = "groupSelectionDetailsCount";
	public static final String SELECTION_DETAILS_SUMMARY_LIST = "selectionDetailsSummaryList";
	public static final String CONTRACT_STATUS_COLUMN = "CONTRACT_STATUS";
	public static final String ACTION_SELECTION_DETAILS = "selectionDetail";
	public static final String FETCH_AWARD_DOCUMENT_TYPE_FOR_TASK = "fetchAwardDocumentTypeForTask";
	public static final String FETCH_AWARD_DOCUMENT_TYPE_LIST = "awardDocumentTypeList";
	public static final String REQUIRED_AWARD_DOCUMENT_TYPE_LIST = "requiredAwardDocTypeList";
	public static final String OPTIONAL_AWARD_DOCUMENT_TYPE_LIST = "optionalAwardDocTypeList";
	public static final String SELECTION_DETAILS_SUMMARY_JSP = "provider/selectionDetailsSummary";
	public static final String UPDATE_CONTRACT_START_END_DATE = "updateContractStartEndDate";
	public static final String IS_CHECKED = "isChecked";
	public static final String BUDGET_YEAR = "budgetYear";
	public static final String UPDATE_ENTRY_TYPE = "updateEntryType";
	public static final String PUBLISHED = "published";
	public static final String FETCH_ENTRY_TYPE_DETAILS = "fetchEntryTypeDetails";
	public static final String FETCH_ENTRY_TYPE_DETAILS_FOR_AMENDMENT = "fetchEntryTypeDetailsForAmendment";
	public static final String UPDATE_BUDGET_CUSTOMIZATION_FOR_UPDATE = "updateBudgetCustomizationForUpdate";
	public static final String UPDATE_BUDGET_CUSTOMIZATION = "updateBudgetCustomization";
	public static final String COUNT_ENTRY_TYPE_FROM_CBC = "countEntryTypeFromCBC";
	public static final String INSERT_BUDGET_CUSTOMIZATION = "insertBudgetCustomization";
	public static final String GET_BUDGET_FROM_CONTRACT_AND_FY = "getBudgetFromContractAndFiscalYEar";
	public static final String FETCH_ENTRY_TYPE_DETAILS_FOR_UPDATE_CONTRACT = "fetchEntryTypeDetailsForUpdateContract";
	public static final String FETCH_ENTRY_TYPE_DETAILS_FOR_CONTRACT_UPDATE_LANDING = "fetchEntryTypeDetailsForContractUpdateLanding";
	public static final String ENTRY_TYPE_LIST = "EntryTypeList";
	public static final String UPDATE_CONTRACT_ID = "UpdateContractId";
	public static final String PIPE_LINE = "||";
	public static final String MERGE_BUDGET_CUSTOMIZATION_CONTRACT_BUDGET_UPDATE_REVIEW = "mergeBudCustomizationCBUpdateReview";
	public static final String DOC_SEQ_ID = "docSeqID";
	public static final String DOCID = "docId";
	public static final String INSERT_BUDGET_CUSTOMIZATION_FOR_UPDATE = "insertBudgetCustomizationForUpdate";
	public static final String DELETE_BUDGET_CUSTOMIZATION_FOR_UPDATE = "deleteBudgetCustomizationForUpdate";
	public static final String DELETE_BUDGET_CUSTOMIZATION = "deleteBudgetCustomization";

	public static final String DELETE_PROV_QUESTION_DATA_GROUP = "deleteProviderQuesResponseDataGroup";
	public static final String DELETE_PROV_SITE_DATA_GROUP = "deleteProviderSiteDataGroup";
	public static final String DELETE_PROPOSAL_DOCUMENT_GROUP = "deleteProposalDocumentGroup";
	public static final String DEL_PROVIDER_DATA_GROUP = "deleteProvidersDataGroup";
	public static final String AWARDS_AND_CONTRACTS_SUMMARY_JSP = "awardsAndContractSummary";
	public static final String GROUP_AWARDS_CONTRACTS_LIST = "groupAwardContractList";
	public static final String GROUP_AWARD_CONTRACT_COUNT = "groupAwardContractCount";
	public static final String EVALUATION_GROUPS_SUMMARY_AWARD = "EvaluationGroupsSummaryAward";
	public static final String AWARDS_AND_CONTRACTS_SUMMARY = "AwardsandContractsSummary";
	public static final String EVALUATION_GROUPS_SUMMARY_AWARD_JSP = "evaluationGroupsSummaryAward";
	public static final String FETCH_ENTRY_TYPE_DETAILS_FROM_MODIFICATION = "fetchEntryTypeDetailsFromModification";
	public static final String FETCH_BAFO_DOC_IDS = "fetchBAFODocIds";
	public static final String INS_BAFO_DOC_DETAILS_DB = "insertBAFODocumentDetails";
	public static final String UPDATE_BAFO_DOC_DETAILS = "updateBAFODocDetails";
	public static final String INSERT_BAFO_DOCS_DETAILS = "insertBAFODocumentDetails";
	public static final String CBI_FETCH_VALIDATION_UNITS = "fetchRateValidationRemUnits";
	public static final String MSG_KEY_INVOICE_UNIT_MORE_THAN_REMAINING_UNIT = "invoiceUnitsMoreThanRemainingUnits";
	public static final String INVOICE_UNITS = "invUnits";
	public static final String FETCH_RATE_VALIDATION_REMNG_UNITS = "fetchRateValidationRemngUnits";
	public static final String FETCH_RATE_VALIDATION_REMNG_UNITS_FOR_MULTIPLE_AMENDMENTS = "fetchRateValidationRemngUnitsInMultipleAmendments";
	public static final String AWARD_APPROVAL_COUNT = "awardApprovalCount";
	public static final String EVAL_SUMMARY_ACCO_USER_CHECK = "evalSummaryAccoUserCheck";
	public static final String EVAL_SUMMARY_NON_ACCO_USER_CHECK = "evalSummaryNonAccoUserCheck";
	public static final String BAFO = "BAFO";
	public static final String ORGA_ID = "organizationId";
	public static final String USER = "userName";
	public static final String MERGE_FLAG = "lbMergeFlag";
	public static final String PROPERTY_PE_TASK_SOURCE = "TaskSource";
	public static final String SUBMISSION_CLOSE_DATE_COL = "submissionCloseDate";
	public static final String PAYMENT_RETURN_FOR_REVISION_ERROR = "paymentReturnForRevisionError";
	public static final String BATCH = "Batch";
	public static final List<String> ENTRY_TYPE_PUBLISHED = new ArrayList<String>();
	static
	{
		ENTRY_TYPE_PUBLISHED.add("1:1");
		ENTRY_TYPE_PUBLISHED.add("2:1");
		ENTRY_TYPE_PUBLISHED.add("3:1");
		ENTRY_TYPE_PUBLISHED.add("4:1");
		ENTRY_TYPE_PUBLISHED.add("5:1");
		ENTRY_TYPE_PUBLISHED.add("6:1");
		ENTRY_TYPE_PUBLISHED.add("7:1");
		ENTRY_TYPE_PUBLISHED.add("8:1");
		ENTRY_TYPE_PUBLISHED.add("9:1");
		ENTRY_TYPE_PUBLISHED.add("10:1");
		ENTRY_TYPE_PUBLISHED.add("11:1");
	}
	public static final String MAPPER_CLASS_TASK_AUDIT_MAPPER = "com.nyc.hhs.service.db.services.application.TaskAuditMapper";
	public static final String INSERT_TASK_AUDIT = "insertTaskAudit";
	public static final String TASK_AUDIT_BEAN = "com.nyc.hhs.model.TaskAuditBean";
	public static final String WORKFLOW_LAUNCH = "Workflow Launch";
	public static final String INSERT_TASK_AUDIT_FOR_LAUNCH = "insertTaskAuditForLaunch";
	public static final String TASK_AUDIT_CONFIGURATION = "taskAuditConfiguration";
	public static final String ENTITY_NAME = "entityName";
	public static final String USER_TYPE = "userType";
	public static final String LEVEL_ONE = "Level 1";
	public static final String LEVEL = "Level ";
	public static final String TASK_AUDIT_BEAN_KEY = "taskAuditBean";
	public static final String TASK_FINISHED_KEY = "Task Finished";
	public static final String CITY_ = "city_";
	public static final String AGENCY_ = "agency_";
	public static final String AGENCY = "Agency";
	public static final String FINANCE_OPEN_ZERO = "FINANCE_OPEN_ZERO";
	public static final String TASK_DETAILS_BEAN_LIST = "taskDetailsBeanList";
	public static final String SIX = "6";
	public static final String NINE = "9";
	public static final String TEN = "10";

	public static final String GET_AMEND_AFFECTED_BUDGET_IDS = "getAmendAffectedBudgetIds";
	public static final String FETCH_ENTRY_TYPE_FOR_BASE_AMEND_MERGE = "fetchEntryTypeForBaseAmendMerge";
	public static final String USERNAME = "User";
	public static final String MERGE_BASE_BUD_CUSTOMIZATION = "mergeBaseBudCustomization";

	public static final String FETCH_AMEND_AFFECTED_BASE_BUDGET_IDS = "fetchAmendAffectedBaseBudgetIds";
	public static final String GET_BUDGET_ADVANCE_FOR_BATCH = "getBudgetAdvanceForBatch";
	public static final String LO_BUDGET_ADVANCE_DETAILS = "loBudgetAdvanceDetails";
	public static final String BATCH_BUDGET_ADVANCE_ID = "BUDGET_ADVANCE_ID";
	public static final String UPDATE_BUDGET_ADVANCE_ID_FOR_BATCH = "updateBudgetAdvanceIdForBatch";
	public static final String UPDATE_INVOICE_FOR_BATCH = "updateInvoiceIdForBatch";
	public static final String BATCH_INVOICE_ID = "INVOICE_ID";
	public static final String LO_INVOICE_DETAILS = "loInvoiceDetails";
	public static final String GET_INVOICE_DETAILS_FOR_BATCH = "getInvoiceDetailsForBatch";
	public static final String FETCH_APPROVAL_INVOICE_FROM_ETL = "fetchApproveInvoiceFromETL";
	public static final String FETCH_BUDGET_ADVANCE_FROM_ETL = "fetchBudgetAdvanceFromETL";
	public static final String GET_COMPETITION_POOL_STATUS = "getCompetitionPoolStatus";
	public static final String GET_PROPOSAL_COUNT_AND_COMP_ID = "getProposalCountAndCompId";
	public static final String EVALUATION_POOL_MAPPING_ID_COL = "EVALUATION_POOL_MAPPING_ID";
	public static final String UPDATE_COMP_POOL_STATUS_IN_PROP_RESUBMIT = "updateCompPoolStatusInPropResubmit";
	public static final String FETCH_EVALUATION_GROUP_STATUS = "fetchEvaluationGroupStatus";
	public static final String VIEW_EVALUATION_SCORE = "View Evaluation Score";
	public static final String PROPOSAL_RELEASE_TIME_KEY = "ProposalReleaseTime_ProposalReleaseTime";
	// Commented for enhancement 6448 for Release 3.8.0
	// public static final String AWARD_REVIEW_STATUS_FLAG =
	// "awardReviewStatusFlag";
	public static final String UPDATE_BUDGET_TEMPLATE = "updateBudgetTemplate";
	public static final String BUDGET_TEMPLATE_SUCCESS_MESSAGE = "BUDGET_TEMPLATE_SUCCESS_MESSAGE";
	public static final String TAXONOMY_TAGS_SAVE_MESSAGE = "TAXONOMY_TAGS_SAVE_MESSAGE";
	public static final String TAXONOMY_TAGS_REMOVE_MESSAGE = "TAXONOMY_TAGS_REMOVE_MESSAGE";
	public static final String GREATER_THAN = ">";
	public static final String CHECK_IF_PUBLISHED_RELEASED = "checkIfPublishedReleased";
	public static final String PUBLISHED_RELEASED_COUNT = "publishedReleasedCount";
	public static final String GET_EVALUATION_GROUP_COUNT = "getEvaluationGroupCount";
	public static final String EVALUATION_GROUP_COUNT = "evaluationGroupCount";
	public static final String GET_AWARD_EPIN_AND_AMOUNT = "getAmendEpinAndAmount";
	public static final String EXT_EPIN = "EXT_EPIN";
	// Constants added for audit entry change
	public static final String UPDATE_DEFAULT_CONF_ID = "updateDefaultConfigurationId";
	public static final String HHSAUDIT_INSERT_FOR_PROC = "hhsauditInsertForProcurement";
	public static final String HHSAUDIT_INSERT_FOR_EVAL_GROUP = "hhsauditInsertForEvalGroup";
	public static final String HHSAUDIT_INSERT_FOR_COMP_POOL = "hhsauditInsertForCompPool";
	public static final String EVENT_NAME = "eventName";
	public static final String EVENT_TYPE = "eventType";
	public static final String ENTITY_ID = "entityId";
	public static final String SUB_ENTITY_ID = "subEntityId";
	public static final String IS_EVAL_GRP = "isEvalGrp";
	// R4: SUMA Constants
	public static final String INPUT_PARAM_CLASS_STAFF_DETAILS_BEAN = "com.nyc.hhs.model.StaffDetails";
	public static final String QUERY_ID_GET_STAFF_DETAILS_FROM_ID = "getStaffDetailsFromId";
	public static final String QUERY_ID_INSERT_SUBMIT_ACCESS_REQUEST = "insertSubmitAccessRequestProvider";
	public static final String QUERY_ID_UPDATE_SUBMIT_ACCESS_REQUEST = "updateStaffMappingForSubmitAccessRequest";
	public static final String QUERY_ID_GET_USER_ORG_DETAILS_MULTI_ACCOUNT = "getUserOrgDetailsMultiAccount";
	public static final String QUERY_ID_SEARCH_USER_ON_EMAIL = "searchUserOnEmailId";
	public static final String UPDATE_END_DATE_FOR_PREVIOUS_STEP = "updateEndDateForPreviousStep";
	public static final String NOTIFICATION_ALERT_ID = "NotificationAlertList";
	public static final String NAME_LIST = "nameList";
	public static final String KEY = "key";
	public static final String FETCH_TYPEAHEAD_NAME_LIST = "fetchTypeAheadNameList";
	public static final String RETURNED_TO_PROVIDER = "Returned To Provider";
	public static final String GROUP_NOTIFICATION_ID = "groupNotificationId";
	public static final String NOTIFICATION_BEAN = "com.nyc.hhs.model.NotificationBean";
	public static final String INS_INTO_GROUP_NOTI_TABLE = "insertIntoGroupNotificationTable";
	public static final String URL = "url";
	public static final String GROUP_NOTIFICATION_URL_ID = "groupNotificationUrlId";
	public static final String INS_INTO_NOTI_ALERT_URL = "insertIntoNotificationAlertUrl";
	public static final String URL_ORDER = "urlOrder";
	public static final String MSG_BODY = "msgBody";
	public static final String UPDATE_GROUP_NOTI_TABLE = "updateGroupNotificationTable";
	public static final String GET_NEXT_GROUP_NOTI_ID = "getNextGroupNotificationId";
	public static final String GET_NEXT_NOTI_ALERT_URL_ID = "getNextNotificationAlertUrlId";
	public static final String GET_UNSENT_GROUP_NOTI_LIST = "getUnsentGroupNotificationsList";
	public static final String USER_LEVELS = "userLevels";
	public static final String GET_USER_EMAIL_IDS = "getUserEmailIds";
	public static final String GET_USER_EMAIL_IDS_NT012 = "getUserEmailIds012";
	public static final String ALERT = "alert";
	public static final String EMAIL = "email";
	public static final String INS_INTO_USER_NOTI_TABLE = "insertInUserNotification";
	public static final String INS_INTO_NOTI_TABLE = "insertInNotificationTable";
	public static final String UPDATE_GROUP_NOTI_STATUS = "updateGroupNotificationStatus";
	public static final String INS_INTO_NOTI_PARAM_TABLE = "insertInNotificationParams";
	public static final String NOTI_PARAM_BEAN = "com.nyc.hhs.model.NotificationParamBean";
	public static final String DELETE_UPDATE_DOCUMENT = "deleteUpdateDocuments";
	
	/* R7.4.0 QC9008   */
	public static final String MAX_BUDGET_UPDATE_AUDIT_BY_CONTRACT_ID = "fetchMaxBudgetUpdateAuditDataByContractId";
	public static final String AUDIT_DELETING_BUDGET_UPDATE_TASK = "insertAuditForDeletingBudgetUpdate";
	
	public static final String TERMINATE_CONTRACT_BUDGET_UPDATE_TASK = "terminateContractBudgetUpdateTask";
    /* R7.4.0 QC9008   */
	
	
	
	// Notification Framework Login Constants
	public static final String SESION_URL = "sesionUrl";
	public static final String NOTIFICATION_REDIRECT_FILTER_LOGGER_ERROR = "Not able to redirect to this URL.";
	public static final String QUERY_FETCH_STAFF_ID_FOR_EMAIL = "fetchStaffIdFromEmail";
	public static final String SHOW_BAFO_BUTTON = "showBafoButton";

	public static final String CHECK_PDF_CREATED = "checkPdfCreated";
	public static final String FETCH_AMENDMENT_SUB_BUDGET_COUNT = "fetchAmendmentSubBudgetCount";
	public static final String DELETE_AMENDMENT_BUDGET_ROW = "deleteAmendmentBudgetRow";
	public static final String DELETE_AMENDMENT_SUB_BUDGET_ROW = "deleteAmendmentSubBudgetRow";
	public static final String AMEND_BUDGET_ZERO_VALUE_ERROR_MSG = "AMEND_BUDGET_ZERO_VALUE_ERROR_MSG";
	public static final String SUB_BUDGET_COUNT = "SUB_BUDGET_COUNT";
	public static final String CONT_FIN_AMT_EQUAL_ZERO = "CONT_FIN_AMT_EQUAL_ZERO";
	public static final String AM = "am";
	public static final String APPROVED_AND_MOVED_TO_NEXT_LEVEL = "Approved and Moved to Next Level";
	public static final String RETURNED_TO_PREVIOUS_LEVEL = "Returned to Previous Level";
	public static final String APPROVED_AND_LAUNCHED = "Approved and Launched";
	public static final String TASK_RELAUNCHED = "Task Relaunched";
	public static final String DBD_DOC_REAL_PATH = "/dbdDoc/";
	public static final String GET_AGENCY_CONTACTS = "getAgencyContacts";
	public static final String FETCH_INITIATORS_DETAILS = "fetchInitiatorsDetails";
	public static final String FETCH_ASSIGNED_EVALUATORS = "fetchUserEmailIdsForNT210";
	public static final String FETCH_USERS_NT220 = "fetchUserEmailIdsForNT220";
	public static final String FETCH_NYC_USER_DETAILS = "fetchDataForIndividualUser";
	public static final String NEW_USER = "new_user";
	public static final String FETCH_NOTIFICATION_PARAMS = "fetchNotificationsParams";
	public static final String FETCH_AGENCY_USERS_DOC_SHARING = "fetchAgencyUsersForDocumentSharing";
	public static final String NT039 = "NT039";
	public static final String NT037 = "NT037";
	public static final String NT038 = "NT038";
	public static final String NT034 = "NT034";
	public static final String NT020 = "NT020";
	public static final String NT011 = "NT011";
	public static final String NT012 = "NT012";
	public static final String NT019 = "NT019";

    /** BEGIN QC9134  R7.4.0*/
    public static final String NT318 = "NT318";
    /** END   QC9134  R7.4.0*/

	public static final String AL019 = "AL019";
	public static final String GET_PERSONNEL_PARENT_ID = "getPersonnelParentId";
	public static final String GET_RATE_PARENT_ID = "getRateParentId";
	public static final String GET_PARENT_SUB_ID = "getParentSubBudgetId";
	public static final String GET_PARENT_SUB_ID_UN_ALLOCATED = "getParentSubBudgetIdUnAllocated";
	public static final String GET_PARENT_BUDGET_ID = "getParentBudgetId";
	public static final String GET_UNALLOCATES_PARENT_ID = "getUnallocateParentId";
	public static final String GET_UNALLOCATED_INFO = "getUnAllocatedInfo";
	public static final String MAPPER_CLASS_DATA_CREATION_MAPPER = "com.dataCreation.DataCreationMapper";
	public static final String NT_BULK_UPLOAD = "NT_BULK_UPLOAD";
	public static final String INSERT_DEFAULT_AWARD_DOC_DETAILS = "insertDefaultAwardDocDetails";
	public static final String MOVE_COMPETITION_POOL_FROM_TEMP = "moveCompetitionPoolIdFromTemp";
	public static final String DISPLAY_DOWNLOAD_ALL_AWARD_DOCS_BUTTON = "displayDownloadDocButton";
	public static final String FETCH_EVALUATION_GROUPS_WITH_AWARDS = "fetchEvaluationGroupsWithAwards";
	public static final String NO_FINANCE_PDF_ERROR_MESSAGE = "NO_FINANCE_PDF_ERROR_MESSAGE";
	public static final String PERMISSION_TYPE = "permissionType";
	public static final String CHECK_FOR_NEW_FY = "checkForNewFY";
	public static final String GET_PARENT_SUB_BUDGET_ID = "getParentSubBudgetId";
	public static final String GET_BASE_SUB_BUDGET_ID = "getBaseSubBudgetId";
	public static final String GET_TOTAL_CONTRACTS = "getTotalContracts";
	public static final String GET_ERROR_LIST = "getErrorList";
	public static final String GET_BASE_CONTRACT_ID = "getBaseContractId";
	public static final String FETCH_BULK_UPLOAD_NOTI_DATA = "fetchBulkUploadNotificationData";
	public static final String TRAN_GET_BULK_UPLOAD_NOTI_DETAILS = "getBulkUploadNotificationDetails";
	public static final String ERROR_LIST = "errorList";
	public static final String TOTAL_CONTRACTS = "totalContracts";
	public static final String GET_URL_NOTI_DETAILS = "getUrlNotificationDetails";
	public static final String ADVACES_PAYMENT_REVIEW_APP_DB = "Advances_Payment_Review_app_db";
	public static final String MAPPER_CLASS_MAIL_REPORT_MAPPER = "com.nyc.hhs.service.db.services.application.MailReportMapper";
	public static final String GET_NOTI_ALERT_MASTER_DETAILS = "getNotificationAlertMasterDetails";
	public static final String ADD_FROM_VAULT_FROM_AWARD = "procurement/addDocumentFromVault";
	public static final String REJECTED_RECORDS = "REJECTED_RECORDS";
	public static final String BULK_UPLOAD_TEMPLATE_NAME = "Template_";
	public static final String NO_LEVEL_CONFIGURED_COF_BULK_UPLOAD_ERROR_MSG = "NO_LEVEL_CONFIGURED_COF_BULK_UPLOAD";
	public static final String GET_PROGRAM_NAME_ID = "getProgramNameId";
	public static final String AGENCY_MAPPING_WITH_PROGRAM_NAME_NOT_FOUND = "Agency and Program name mapping doest not exist in the system";
	public static final String FETCH_EVAL_GROUP_ID_FROM_POOL_ID = "fetchEvalGroupIdFromPoolMappingId";
	public static final String DOC_PROPOSAL_ID = "PROPOSAL_ID";
	public static final String NO_PROPOSALS_RECEIVED = "No Proposals Received";
	public static final String FETCH_NOT_NON_RESPONSIVE_PROP_COUNT = "fetchNotNonResponsivePropCount";
	public static final String FETCH_NON_RESPONSIVE_COMP_POOL_COUNT = "fetchNonResponsiveCompPoolCount";
	public static final String UPDATE_NON_RESPONSIVE_EVAL_GROUP = "updateNonResponsiveEvalGroup";
	public static final String DELETE_COMP_POOLS = "deleteCompetitionPools";

	//[Start] R6.3 added for QC6627 & QC8693   
	public static final String DELETE_COMP_POOLS_ADDENDUM = "deleteCompetitionPoolsForAddendum";
	public static final String PHONE = "phone";
	public static final String CONTACT_US = "contact_us";
	public static final String TASK_EMAIL = "task_email";
	public static final String TOPIC = "topic";
	public static final String CONTACT_MEDIUM = "contactMedium";
	//[End] R6.3 added for QC6627 & QC8693
	
	public static final String PROVIDER_DOES_NOT_EXIST = "PROVIDER_DOES_NOT_EXIST";
	public static final String NOT_IN_CORRECT_FORMAT = "NOT_IN_CORRECT_FORMAT";
	public static final String START_DATE_CANNOT_BE_GREATER_THAN_END_DATE = "START_DATE_CANNOT_BE_GREATER_THAN_END_DATE";
	public static final String CHK_LINE_ITEM_DEFAULT_ENTRIES = "checkLineItemDefaultEntries";
	public static final String DEL_LINE_ITEM_DEFAULT_ENTRIES = "deleteLineItemDefaultEntries";
	public static final List<String> DEFAULT_LINE_ITEM_TABLE_LIST = new ArrayList<String>();
	static
	{
		DEFAULT_LINE_ITEM_TABLE_LIST.add("OPERATIONS_AND_SUPPORT");
		DEFAULT_LINE_ITEM_TABLE_LIST.add("PROGRAM_INCOME");
		DEFAULT_LINE_ITEM_TABLE_LIST.add("PROFESSIONAL_SERVICE");
		DEFAULT_LINE_ITEM_TABLE_LIST.add("UTILITIES");
		//QC 8394 R 7.8.0 Make Unallocated non default line table
		//DEFAULT_LINE_ITEM_TABLE_LIST.add("UNALLOCATED");
		DEFAULT_LINE_ITEM_TABLE_LIST.add("INDIRECT_RATE");
		//[Start] QC 9462 & QC 9503 - add FRINGE_BENEFIT_DETAIL to the list
		DEFAULT_LINE_ITEM_TABLE_LIST.add("FRINGE_BENEFIT_DETAIL");
		//[End] QC 9462 & QC 9503 - add FRINGE_BENEFIT_DETAIL to the list
		DEFAULT_LINE_ITEM_TABLE_LIST.add("FRINGE_BENEFIT");
	}
	public static final List<String> MAIL_REPORT_LIST = new ArrayList<String>();
	static
	{
		MAIL_REPORT_LIST.add("Advances_Payment_Review_app_db");
		MAIL_REPORT_LIST.add("Advances_review_app_db");
		MAIL_REPORT_LIST.add("budget_review_app_db");
		MAIL_REPORT_LIST.add("contract_config_app_db");
		MAIL_REPORT_LIST.add("Invoice_review_app_db");
		MAIL_REPORT_LIST.add("Payment_Review_app_db");
	}

	public static final List<String> MAIL_REPORT_USERS = new ArrayList<String>();
	static
	{
		MAIL_REPORT_USERS.add("jhale@hhsaccelerator.nyc.gov");
		MAIL_REPORT_USERS.add("Aerlichman@hhs.nyc.gov");
		MAIL_REPORT_USERS.add("dgentile@hhsaccelerator.nyc.gov");
		MAIL_REPORT_USERS.add("rlundy@hhsaccelerator.nyc.gov");
		MAIL_REPORT_USERS.add("dsymon1@hhsaccelerator.nyc.gov");
		MAIL_REPORT_USERS.add("muhammad.s.nihal@accenture.com");
		MAIL_REPORT_USERS.add("HHSAccelerator_Support@hhsaccelerator.nyc.gov");
	}

	public static final String DEFAULT_TABLE_NAME = "defaultTableName";

	public static final String STATUS_PENDING_REG = "61";
	public static final String STATUS_ETL_REG = "118";
	public static final String STATUS_SENT_FOR_REG = "130";
	// begin R6.3 QC5690 
	//public static final String STATUS_PENDING_NOTIFICATION = "194";
	// end R6.3 QC5690   
	public static final String RESTRICT_SUBMIT_FLAG = "restrictSubmit";
	public static final String GET_PROV_RESTRICT_SUBMIT_FLAG = "getProvRestrictSubmitFlag";
	public static final String GET_PROV_RESTRICT_SUBMIT_FLAG_PROPOSAL = "getProvRestrictSubmitFlagProposal";
	public static final String SET_CONTRACT_STATUS = "setContractStatus";
	public static final String PROPOSAL_NOT_UNLOCKED = "proposalNotUnlocked";
	public static final String EVALUATION_GROUP_ID_COL = "EVALUATION_GROUP_ID";
	public static final String SEND_EVALUATION_TASKS = "SEND_EVALUATION_TASKS";
	public static final String PROVIDER_ORG_ID = "providerOrgID";
	public static final String PROVIDER_ORG_ID_AMEND = "provider";

	public static final String BMC_CONTRACT_BUDGET_PROCESSING = "contractBudgetProcessing";
	public static final String BMC_FETCH_CONTRACT_DETAILS = "fetchContractDetails";
	public static final String BMC_NEW_FY_BUDGET_PROCESSING = "newFYBudgetProcessing";
	public static final String NEW_FY_TASK_BUDGET_TAB = "aoNewFYTaskBudgetTab";
	public static final String COMPETITION_POOL_DUPLICATE = "competitionPoolDuplicate";
	public static final String CS_EDIT_CONTRACT_CONF_AMEND_TASK_DETAILS_OLD = "editContractConfAmendmentDetailsOld";
	public static final String ITEXT_LICENSE_FILE_PATH = "ITEXT_LICENSE_FILE_PATH";
	public static final String RETRACT_ERROR = "RETRACT_ERROR";
	// Support build 3.1.0 constants
	public static final String NEW_FY_TASK = "NewFYTask";
	public static final String DURATION_IN_DAYS = "DurationInDays";
	public static final String STRING_TRUE = "true";
	public static final String STRING_FALSE = "false";
	public static final String NEW_FY_TASK_SPAN = "NewFYTaskSpan";
	public static final String NEXT_FISCAL_YEAR = "NextFiscalYear";
	public static final String SELECT_FROM_VAULT_BUSINESS_APLLICATION_FLAG = "selectFromVaultFlag";

	// Start of constants added for Enhancement 3.1.0 :6025
	public static final String DOCUMENTS_NOT_REQUESTED = "DOCUMENTS_NOT_REQUESTED";
	public static final String DOCUMENTS_REQUESTED = "DOCUMENTS_REQUESTED";
	public static final String DOCUMENTS_READY_TO_DOWNLOAD = "DOCUMENTS_READY_TO_DOWNLOAD";
	public static final String DOCUMENTS_EXPIRED = "DOCUMENTS_EXPIRED";
	public static final String FETCH_AWARD_DOC_DETAILS = "fetchAwardDocDetails";
	public static final String FETCH_ORG_DETAILS_FROM_CONTRACT_ID = "fetchOrgDetailsFromContractId";
	public static final String AWARD_DOC_DETAILS = "awardDocDetails";
	public static final String INSERT_DOC_DOWNLOAD_REQUEST = "insertDocumentDownloadRequest";
	public static final String UPDATE_DOC_DOWNLOAD_REQUEST = "updateDocumentDownloadRequest";
	public static final String FETCH_DOCUMENT_DETAILS_FOR_ZIP_BATCH_PROCESS = "fetchDocumentDetailsforZipBatchProcess";
	public static final String FETCH_PROPOSAL_ID = "fetchProposalId";
	public static final String IS_FINANCIAL_CHECK = "isFinancialCheck";
	public static final String AO_DOCUMENTS_DETAILS_LIST = "aoDocumentDetailsList";
	public static final String AS_EVALUATION_POOL_MAPPING_ID = "asEvaluationPoolMappingId";
	public static final String AS_PROVIDER_ORG_ID = "asProviderOrgID";
	public static final String ZIP_DOCUMENTS_PATH = "ZIP_DOCUMENTS_PATH";
	public static final String AS_FOLDER_NAME_AWARD_DOC = "asFolderNameAwardDoc";
	public static final String AS_FOLDER_NAME_RFP_DOC = "asFolderNameRFPDoc";
	public static final String AS_FOLDER_NAME_PROPOSAL_DOC = "asFolderNameProposalDoc";
	public static final String UNDERSCORE_AWARD_DOCUMENTS = "_Award_Documents";
	public static final String RFP_DOCUMENT = "RFP_Documents";
	public static final String UNDERSCORE_PROPOSAL_DOCUMENTS = "_Proposal_Documents";
	public static final String DELETE_ZIP_PROCUREMENT_DOCUMENTS = "deleteZipProcurementDocuments";
	public static final String FETCH_DOCUMENT_DETAILS_FOR_ZIP_DELETE_BATCH_PROCESS = "fetchDocumentDetailsforZipDeleteBatchProcess";
	public static final String FILE_NAME_PARAMETER = "fileName";
	public static final String AS_FILE_NAME_PARAMETER = "asFileName";
	public static final String AS_DOC_STATUS = "asDocStatus";
	public static final String REQUEST_ZIP_FILE = "requestZipFile";
	public static final String AO_AWARD_DOC_DETAILS = "aoAwardDocDetails";
	public static final String SUCCESS_MSG = "successMsg";
	public static final String FAIL_MSG = "failMsg";
	public static final String DOC_REQ_MSG = "docRequestedMessage";
	public static final String DOC_REQ_ERROR_MSG = "docRequestedErrorMessage";
	public static final String GEN_FILE_MSG = "Generating file";
	public static final String ZIP_PROCUREMENT_DOCUMENTS = "ZipProcurementDocuments";
	public static final String LO_PDF_CREATED = "loPdfCreated";
	public static final String AO_DOCUMENT_TITLE = "aoDocumentTitle";
	public static final String UPDATE_PROC_STATUS_FOR_DOCS = "updateProcStatusForDocs";
	public static final String CONTENT_TYPE_ZIP = "application/zip";
	public static final String ORGANIZATION_NAME_CAPS = "ORGANIZATIONNAME";
	public static final String UPDATE_DOC_DOWNLOAD_REQUEST_FOR_BATCH = "updateDocumentDownloadRequestForBatch";
	public static final String UPDATE_DOC_DOWNLOAD_DELETE_REQUEST_FOR_BATCH = "updateDocumentDownloadDeleteRequestForBatch";
	public static final String BLANK_SYMBOL = "----";
	public static final String NOT_REQUESTED = "Not Requested";
	public static final String FETCH_PROPOSAL_DOC_FOR_ZIP = "fetchProposalDocumentsForZip";
	public static final String FETCH_DOCUMENT_STATUS = "fetchDocumentStatus";
	public static final String LS_DOC_STATUS = "lsDocStatus";
	public static final String REMOVE_SPCL_CHAR = "[^a-zA-Z0-9 ]";

	// End of constants added for Enhancement 3.1.0 :6025

	public static final String INSERT_PROPOSAL_MAPPING_QUES = "insertProposalMappingQues";
	public static final String DELETE_PROPOSAL_MAPPING_QUES = "deleteProposalMappingQues";
	public static final String FETCH_PROP_QUES_FOR_PLANNED = "fetchPropCustomQuesForPlanned";
	public static final String FETCH_PROP_QUES_FOR_RELEASED = "fetchPropCustomQuesForReleased";
	public static final String UPDATE_PROPOSAL_MAPPING_QUES = "updateProposalMappingQues";
	public static final String LAT_VER_QUES = "latestVersionQues";
	public static final String GET_MAX_VERSION_PROC_QUESTIONS = "getMaxVersionProcurementQuesitons";
	public static final String GET_MAX_VERSION_PROC_DOCUMENTS = "getMaxVersionProcurementDocuments";
	public static final String INSERT_QUESTION_MAPPING_RELEASE_ADDENDUM = "insertQuestionMappingReleaseAddendum";
	public static final String FETCH_EVAL_STATUS_FROM_PROPOSALID = "fetchEvalStatusFromProposalID";
	public static final String EVAL_STATUS_KEY = "asEvalStatus";
	public static final String FETCH_VERSION_INFORMATION = "fetchVersionInformation";
	public static final String VERSION_INFO_BEAN = "versionInfoBean";
	public static final String INSERT_PROPOSAL_MAPPING_DOC = "insertProposalMappingDoc";
	public static final String UPDATE_PROPOSAL_MAPPING_DOC = "updateProposalMappingDoc";
	public static final String DELETE_PROPOSAL_MAPPING_DOC = "deleteProposalMappingDoc";
	public static final String FETCH_PROP_DOC_FOR_PLANNED = "fetchPropDocTypeForPlanned";
	public static final String FETCH_PROP_DOC_FOR_RELEASED = "fetchPropDocTypeForReleased";
	public static final String GET_VERSION_NO_FOR_PROPOSAL_DOC = "getVersionNoForProposalDocuments";
	public static final String DOC_VERSION_NO = "docVersionNo";
	public static final String NEXT_VERSION = "nextVersion";
	public static final String GET_MAX_VERSION_EVAL_CRITERIA = "getMaxVersionEvalCriteria";
	public static final String NT401 = "NT401";
	public static final String SELECT_PROC_DOC_ADDM_CONFIG = "selectProcDocAddmConfig";
	public static final String INS_PROC_DOC_CONFIG_MAPPING = "insertProcDocumentConfigMapping";
	public static final String ADVANCE_REJECTED = "ADVANCE_REJECTED";
	public static final String PAYMENT_REJECTED = "PAYMENT_REJECTED";
	public static final String INVOICE_REJECTED = "INVOICE_REJECTED";
	public static final String STATUS_PAYMENT_REJECTED = "PAYMENT_REJECTED";
	public static final String NT400 = "NT400";
	public static final String NT402 = "NT402";
	public static final String PAYMENT_REJECTION_AGENCY_URL = "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_windowLabel=portletInstance_37&_urlType=render&wlpportletInstance_37_action=paymentListAction";
	public static final String AGENCY_SETTING_CITY = "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenanceagencysettings&agencysettinglogin=agencylogin";
	public static final String TASK_URL = "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox&choosenTab=taskmanager&taskType=Procurement Certification of Funds&taskAction=unassign";
	public static final String VOUCHER_ID = "VOUCHER_ID";
	public static final String FETCH_COMMA_SEPARATED_VOUCHER_LIST = "fetchCommaSeparatedVoucherList";
	public static final String DELETE_INTERFACE_PAYMENT_RECORDS = "deleteInterfacePaymentRecords";
	public static final String SET_ADVANCE_STATUS_FOR_INTERFACE_REVIEW_TASK = "setAdvanceStatusForInterfaceReviewTask";
	public static final String SEND_NOTIFICATION_FLAG = "lbSendNotificationFlag";
	public static final String INVOICE_PAYMENT = "Invoice_Payment";
	public static final String BUDGET_PAYMENT = "Budget_Payment";
	public static final String UPDATE_DOC_VERSION_NO = "updateDocVersionNo";
	public static final String VERSION_NUMBER_QC = "VERSION_NUMBER_QC";
	public static final String VERSION_NUMBER_DC = "VERSION_NUMBER_DC";
	public static final String EVAL_VERSION_NUMBER_PQC = "VERSION_NUMBER_PQC";
	public static final String EVAL_VERSION_NUMBER_PDC = "VERSION_NUMBER_PDC";
	public static final String PROP_AND_EVAL_SUMM_AGENCY_LINK = "/portal/hhsweb.portal?_nfpb=true&_windowLabel=portletInstance_38&_urlType=action&submit_action=navigationAction&_pageLabel=portlet_hhsweb_portal_page_procurement&ES=0&topLevelFromRequest=ProposalsandEvaluations&forAction=propEval&procurementId=";
	public static final String EVAL_GROUP_PARAMETER = "&evaluationGroupId=";
	public static final String PROP_AND_EVAL_SUMM_LINK = "PROP_AND_EVAL_SUMM_LINK";
	public static final String CHECK_EVAL_IN_PROGRESS = "checkEvaluationInProgress";
	public static final String EVAL_PROGRESS_COUNT = "EVALPROGRESSCOUNT";
	public static final String LB_EVAL_PROGRESS = "lbEvalProgress";
	public static final String RESTRICT_RELEASE_ADDENDUM = "restrictReleaseAddendum";
	public static final String AGENCY_USER_NAME = "AGENCYUSERNAME";
	public static final String AGENCY_USER_EMAIL_ID = "AGENCYUSEREMAILID";
	public static final String LS_PAYMENT_REJECTED_STATUS_ID = "lsPaymentRejectedStatusId";
	public static final String LS_INVOICE_REJECTED_STATUS_ID = "lsInvoiceRejectedStatusId";
	public static final String RELEASE_ADDENDUM_AUDIT_INSERT = "releaseAddendumAuditInsert";
	public static final String FETCH_RFP_DOCS_FOR_TASKS = "fetchRfpDocsDetailsForTasks";
	public static final String FETCH_NEW_DOCS_COUNT = "fetchNewDocsCount";
	public static final String FETCH_NEW_QUES_COUNT = "fetchNewQuesCount";
	public static final String EVAL_POOL_MAPPING_PARAMETER = "&evaluationPoolMappingId=";
	public static final String COMP_POOL_PARAMETER = "&competitionPoolId=";
	// Release 3.1.0 for defect fix 6398
	public static final String CS_FETCH_CONTRACT_FINANCIAL_DETAILS_BATCH = "fetchedContractFinancialDetailsBatch";
	public static final String CS_INSERT_FETCH_CONTRACT_FINANCIAL_DETAILS_BATCH = "insertFetchedContractFinancialDetailsBatch";
	public static final String GET_USER_EMAIL_IDS_NT019 = "getUserEmailIds019";
	public static final String AGENCY_WORKFLOW_FOR_CITY = "agencyWorkflowCity";
	public static final String CONTROLLER_ACTION = "controller_action";
	public static final String INCLUDE_AGENCY_TASK_MANAGEMENT = "IncludeAgencyTaskManagementBox";
	public static final String INCLUDE_AGENCY_TASK_MANAGEMENT_PATH = "/WEB-INF/jsp/TaskDetails/agencyTaskManagementForAccelerator.jsp";
	public static final String TASK_HOME_CITY = "TaskHome";
	public static final String ACCEPT_PROPOSAL_TASK_CITY = "acceptProposalTaskCity";
	public static final String CONFIGURE_AWARD_DOC_TASK_JSP_CITY = "configureAwardDocTaskCity";
	public static final String EVALUATE_PROPOSAL_TASK_JSP_CITY = "evaluateProposalTaskCity";
	public static final String AGENCY_WORKFLOW_MODEL_VIEW_CITY = "reviewScoreCity";
	public static final String SELECT_ALL_FLAG = "selectAll";
	public static final String CONTROLLER_BEAN_ID = "controller_bean_id";
	public static final String VIEW_RESPONSE_ACCELERATOR = "viewResponseForAccelerator";
	public static final String AGENCY_SELECT_BOX = "agencySelectBox";

	// Release 3.2.0 constants
	public static final String CBL_FETCH_CONTRACT_AMOUNT_FOR_VALIDATION = "fetchContractAmountForValidation";
	public static final String ADVANCE_AMOUNT_GREATER_THAN_BUDGET = "advanceAmountGreaterThanBudget";
	public static final String AS_BUDGET_AMOUNT = "asBudgetAmount";
	public static final String VIEW_DOC_FOR_ACCELERATOR = "evaluation/viewDocumentForAccelerator";
	public static final String JSP_PATH_EVALUATION = "evaluation/";
	public static final String CITY_WORKFLOW_SCOREDETAILS = "evaluation/scoreDetailsForCity";

	// Release 3.2.0 for defect enhancement 6384
	public static final String FETCH_INITIATORS_DETAILS_AL305 = "fetchInitiatorsDetailsForAL305";
	// Start of updates in build no 3.2.0 for enhancement#6361
	public static final String FETCH_USER_LIST_FOR_FILTERED_TASK = "fetchUserListForFilteredTask";
	public static final String FETCH_SELECTED_USER_ASSIGNED_LEVEL = "fetchSelectedUserAssignedLevel";
	public static final String ENABLE_BULK_ASSIGN = "enableBulkAssign";
	public static final String ALL_STAFF = "All Staff";
	// End of updates in build no 3.2.0 for enhancement#6361

	// Constants added for Release 3.2.0 for defect enhancement 5684
	public static final String FINANCE_SCREEN_CHECK = "FINANCE_SCREEN_CHECK";
	public static final String CHECK_IF_OPENENDED_ZEROVALUE = "checkIfOpenEndedZeroValue";
	public static final String TASK_LAUNCH_VARIABLE = "TaskLaunch";
	public static final String LAUNCH_PCOF_TASK_CITY = "launchPCOFTaskbyCity";
	public static final String FETCH_PCOF_STATUS = "fetchPCOFStatus";
	public static final String FETCH_PCOF_FIN_ENTRY = "fetchProcFinancialEntry";
	public static final String PCOF_TASK_LAUNCH_CHECK = "PCOF_TASK_LAUNCH_CHECK";
	public static final String FETCH_PCOF_TASK_COUNT = "fetchPCOFTaskCount";
	public static final String NOTIFICATION_GENERATED_FROM = "notificationGeneratedFrom";
	public static final String REDIRECT_HOME_FROM_NOTIFICATION = "redirecthomeFromNotifaction";
	public static final String ROLE_ATT = "role";
	public static final String NOTIFICATION_GENERATED_FROM_NT402 = "&notificationGeneratedFrom=NT402";
	public static final String USER_TYPE_ATTR = "user_type";
	// End of updates for Release 3.2.0 for defect enhancement 5684

	// Release 3.1.2 Defect 6420 start
	public static final String TRANSACTION_FAILURE = "TransactionFailure";
	public static final String MULTIPLE_INVOICE_OPEN = "multipleInvoiceOpen";
	public static final String PAGE_LEVEL_INVOICE_ID = "pageLevelInvoiceId";
	public static final String INVOICE_MIS_MATCHED = "invoiceIdMisMatched";
	public static final String FROM_MULTIPLE_INVOICE = "fromMultipleInvoice";
	// Release 3.1.2 Defect 6420 end

	// Constants added for Release 3.2.0 for defect enhancement 6434
	public static final String FETCH_USER_ID_FROM_EMAIL_ID = "fetchUserIdForEmailId";

	// Adding Constants for Release 3.4.0, Enhancement 5681 - Start
	public static final String PRINTER_VIEW_BUDGET = "printBudget";
	public static final String FETCH_PRINT_BUDGET_SUMMARY = "fetchPrintBudgetSummary";
	public static final String JSP_CONTRACT_BUDGET_PRINT = "jsp/contractbudget/contractBudgetPrint";
	public static final String PRINTER_VIEW_INVOICE = "printInvoice";
	public static final String FETCH_PRINT_INVOICE_SUMMARY = "fetchPrintInvoiceSummary";
	public static final String JSP_INVOICE_PRINT = "jsp/invoice/contractInvoicePrint";
	public static final String CONTRACT_BUDGET_PRINT_SUMMARY = "contractBudgetPrintSummary";
	public static final String CONTRACT_INVOICE_PRINT_SUMMARY = "contractInvoicePrintSummary";
	public static final String PRINT_TAB_TO_SHOW_LIST = "tabToShowList";
	public static final String BASE_YTD_INVOICED_AMOUNT_OTPS = "ytdInvoicedAmountOTPS";
	public static final String BASE_INVOICE_TOTAL_AMOUNTS_OTPS = "invoiceTotalAmountsOTPS";
	public static final String BASE_YTD_INVOICED_AMOUNT_CS = "ytdInvoicedAmountCS";
	public static final String BASE_INVOICE_TOTAL_AMOUNTS_CS = "invoiceTotalAmountsCS";
	// Adding Constants for Release 3.4.0, Enhancement 5681 - End
	// Start || Changes done for Enhancement #6429 for Release 3.4.0
	public static final String STRING_FOR_AWARD_SCREEN = "forAwardScreen";
	public static final String STRING_AWARD_DOC = "awardDoc";
	public static final String INSERT_AGENCY_AWARD_DOCS_DETAILS = "insertAgencyAwardDocsDetails";
	public static final String FETCH_AGENCY_AWARD_DOCUMENTS = "fetchAgencyAwardDocuments";
	public static final String FINAL_AGENCY_AWARD_DOC_LIST = "finalAgencyAwardDocList";
	public static final String AGENCY_AWARD_DOC_LIST = "agencyAwardDocList";
	public static final String DELETE_AGENCY_AWARD = "deleteAgencyAward";
	public static final String REMOVE_AGENCY_AWARD_DOCS = "removeAgencyAwardDocs";
	public static final String FETCH_AGENCY_AWARD_DOCUMENT_IDs = "fetchAgencyAwardDocumentIds";
	public static final String AGENCY_AWARD_DOC = "AgencyAward";
	public static final String AGENCY_AWARD_ERROR_MSG = "Provider's award documents";
	// End || Changes done for Enhancement #6429 for Release 3.4.0
	// Start || constants for Workitems count in queue batch in release 3.6.0
	public static final String QUEUE_NAME = "queueName";
	public static final String STUCK_TIME_PERIOD = "timeSince";
	public static final String GET_WORKITEMS_FROM_QUEUE = "getWorkItemsCountFromQueue";
	public static final String NUMBER_OF_WORKITEMS_IN_QUEUE = "numberOfItemsInQueue";
	public static final String HHS_EMAIL_PROPERTIES = "com.nyc.hhs.properties.email";
	public static final HashMap<String, String> QUEUE_NAME_MAP = new HashMap<String, String>();
	static
	{
		QUEUE_NAME_MAP.put("MAINTENANCE_QUEUE_NAME", "HHSMaintenanceQueue");
		QUEUE_NAME_MAP.put("COMPONENT_QUEUE_NAME", "HHSOperations");
	}
	public static final String COMPONENT_QUEUE = "COMPONENT_QUEUE";
	public static final String MAINTENANCE_QUEUE = "MAINTENANCE_QUEUE";
	public static final String FETCH_QUEUE_ITEM_DELAY_CONFIG = "getQueueItemDelayConfig";
	public static final String QUEUE_ITEM_DELAY_CONFIG = "queueItemDelayConfig";
	// Start || constants for Workitems count in queue batch in release 3.6.0

	// Release 3.6.0 Enhancement 6496 start
	public static final String FETCH_BUD_DETAILS_NT403 = "fetchBudgetDetailsForNT403";
	public static final String LO_NOTIFICATION_MAP = "loNotificationMap";
	public static final String NT_403 = "NT403";
	public static final String AL_403 = "AL403";
	public static final String BUDGET_DETAILS_MAP = "budgetDetailsMap";
	public static final String FISCAL_YEAR_ID_CAPS = "FISCAL_YEAR_ID";
	// Release 3.6.0 Enhancement 6496 end
	// Added as a part of release 3.6.0 for enhancement request 5905
	public static final String GET_EVALUATION_PROGRESSING = "getEvaluationProgressing";
	public static final String UPDATE_EVAL_PROGRESS_STATUS = "updateEvalProgressStatus";
	public static final String GET_EVAL_PROGRESS_STATUS = "getEvalProgressStatus";
	public static final String EVAL_PROGRESS_FLAG = "evalProgressFlag";
	public static final String EVALUATION_SENT_VAL = "EVALUATION_SENT";
	public static final String EVALUATION_PROGRESS = "EVALUATION_PROGRESS";
	public static final String ERROR_MESSSAGE_EVALUATION_PROGRESS = "EVALUATION_ALREADY_PROGRESS";
	// Constant Added for Admin Cache service
	public static final String RECACHE = "reCache";
	public static final String IS_ADMIN_USER = "isAdminUser";
	public static final String KEY_IDS = "keysIds";
	public static final String ADMIN_SETTINGS_VIEW = "AdminSettings";
	public static final String LOCK_DESCRIBE = "lockDescribe";
	// Start || Changes done for enhancement 6508 for Release 3.6.0
	public static final String GET_APP_SETTINGS_MAIN_QUEUE = "getAppSettingsForMaintenanceQueue";
	public static final String MNTNC_QUEUE_EMAILIDS = "MNTNC_QUEUE_EMAILIDS";
	public static final String MNTNC_QUEUE_SENDFLAG = "MNTNC_QUEUE_SENDFLAG";
	public static final String COMPONENT_NAME = "componentName";
	public static final String CONTENT_NAME = "contentName";
	public static final String SETTINGS_VAL = "settingsVal";
	// End || Changes done for enhancement 6508 for Release 3.6.0

	// START || Added For Enhancement 6482 for Release 3.8.0
	public static final String UPDATE_CONTRACT_INFO = "Update Contract Information";
	public static final String UPDATE_CONTRACT_JSP = "updateContract";
	public static final String UPDATE_CONTRACT_BEAN = "updateContractBean";
	public static final String FETCH_CONTRACT_DETAILS_FOR_UPDATE = "fetchContractDetailsForUpdate";
	public static final String FIND_CONTRACT_EPIN = "findContractEpin";
	public static final String FIND_AGENCY_NAME = "findAgencyName";
	public static final String UPDATE_CONTRACT_STATUS = "updateContractSuccess";
	public static final String UPDATE_CONTRACT_RULE = "updateContractRule";
	// END || Added For Enhancement 6482 for Release 3.8.0

	// START || Added For Enhancement 6000 for Release 3.8.0
	public static final String FETCH_PARENT_CONTRACT_ID_LIST = "fetchParentContractIdList";
	public static final String FETCH_CONTRACTID_FOR_UPDATE_TASK = "fetchContractForUpdateTask";
	public static final String CAFE_DELETE_CONTRACT = "Delete Contract";
	public static final String CONTRACT_DELETION = "Contract Deletion";
	public static final String DELETE_CONTRACT_STATUS = "deleteContractSuccess"; 
	public static final String FETCH_APPROVE_ACTIVE_BUDGET = "fetchApproveActiveBudget";
	public static final String DELETE_CONTRACT_RULE = "deleteContractRule";
	public static final String DELETE_PERSONNEL_SERVICE = "deletePersonnelService";
	public static final String DELETE_OP_SUPPORT_OTHERS = "deleteOpSupportOthers";
	public static final String DELETE_OP_SUPPORT = "deleteOpSupport";
	public static final String DELETE_UTILITIES = "deleteUtilities";
	public static final String DELETE_PROFF_SERVICE_OTHERS = "deleteProffServiceOthers";
	public static final String DELETE_PROFF_SERVICE = "deleteProffService";
	public static final String DELETE_RENT = "deleteRent";
	public static final String DELETE_CONTRACTED_SERVICE = "deleteContractedService";
	public static final String DELETE_RATE = "deleteRate";
	public static final String DELETE_MILESTONE = "deleteMilestone";
	public static final String DELETE_UNALLOCATED = "deleteUnallocated";
	public static final String DELETE_INDIRECT_RATE = "deleteIndirectRate";
	public static final String DELETE_PROG_INCOME_OTHERS = "deleteProgIncomeOthers";
	public static final String DELETE_PROGRAM_INCOME = "deleteProgramIncome";
	public static final String DELETE_FRINGE_BENEFIT = "deleteFringeBenefit";
	public static final String DELETE_EQUIPMENT = "deleteEquipment";
	public static final String DELETE_SUB_BUDGET_SITE = "deleteSubBudgetSite";
	public static final String DELETE_SUB_BUDGET = "deleteSubBudget";
	public static final String DELETE_BUDGET_CUSTOMIZ = "deleteBudgetCustomiz";
	public static final String DELETE_BUDGET_DOC = "deleteBudgetDoc";
	public static final String DELETE_ASSOCIATED_BUDGETS = "deleteAssociatedBudgets";
	public static final String DELETE_CONTRACT_FIN_FUNDING = "deleteContractFinFunding";
	public static final String DELETE_CONTRACT_FINANCIALS = "deleteContractFinancials";
	public static final String DELETE_CONTRACT_DOC = "deleteContractDoc";
	public static final String DELETE_CONTRACT = "deleteContract";
	public static final String NT_404 = "NT404";
	public static final String FETCH_CONTRACT_TITLE_AND_ORGID = "fetchContractTitleAndOrgID";
	public static final String FETCH_PENDING_BUDGET = "fetchPendingBudget";
	public static final String CONTRACT_DETAILS = "loContractDetails";
	public static final String FETCH_BUDGETS_FOR_CONTRACT = "fetchBudgetsForContract";
	public static final String DELETED = "deleted";
	public static final String AGENCY_ID_COL = "AGENCY_ID";
	public static final String FETCH_CONTRACT_BUDGET_DOCS = "fetchContractBudgetDocs";
	// END || Added For Enhancement 6000 for Release 3.8.0

	// Constants added as a part of release 3.8.0 enhancement 6534 - start
	public static final String REVIEW_LEVEL_CHANGE = "Review Level Change";
	public static final String SET_REVIEW_LEVEL_PROPERTIES_IN_FILENET = "setReviewLevelPropertiesInFilenet";
	public static final String NEW_REVIEW_LEVEL = "NewReviewLevel";
	public static final String OLD_REVIEW_LEVEL = "OldReviewLevel";
	public static final String UPDATE_REVIEW_PROGRESS_FLAG = "updateReviewInProgressFlag";
	public static final String FETCH_REVIEW_PROGRESS_FLAG = "fetchReviewInProgressFlag";
	public static final String REVIEW_PROGRESS_FLAG = "reviewInProgressFlag";
	public static final String AGENCY_DETAILS_MAP = "aoAgencyDetailsMap";
	public static final String ERROR_MESSSAGE_REVIEW_LEVEL = "REVIEW_LEVEL_CHANGE";
	public static final String ERROR_MESSSAGE_REVIEW_LEVEL_AGENCY = "REVIEW_LEVEL_CHANGE_AGENCY";
	public static final String USERS_CANT_BE_SAVED = "USERS_CANT_BE_SAVED";
	public static final String USER_SAVE_STATUS = "abSaveStatus";
	public static final Map<String, String> TASK_ID_PROCESS_MAP = new HashMap<String, String>();
	static
	{
		TASK_ID_PROCESS_MAP.put(TASK_PROCUREMENT_COF, "15");
		TASK_ID_PROCESS_MAP.put(TASK_CONTRACT_UPDATE, "11");
		TASK_ID_PROCESS_MAP.put(TASK_CONTRACT_CONFIGURATION, "9");
		TASK_ID_PROCESS_MAP.put(TASK_CONTRACT_COF, "8");
		TASK_ID_PROCESS_MAP.put(TASK_ADVANCE_REVIEW, "2");
		TASK_ID_PROCESS_MAP.put(TASK_NEW_FY_CONFIGURATION, "13");
		TASK_ID_PROCESS_MAP.put(TASK_AMENDMENT_CONFIGURATION, "10");
		TASK_ID_PROCESS_MAP.put(TASK_BUDGET_UPDATE, "7");
		TASK_ID_PROCESS_MAP.put(TASK_BUDGET_REVIEW, "6");
		TASK_ID_PROCESS_MAP.put(TASK_BUDGET_MODIFICATION, "5");
		TASK_ID_PROCESS_MAP.put(TASK_BUDGET_AMENDMENT, "4");
		TASK_ID_PROCESS_MAP.put(TASK_INVOICE_REVIEW, "12");
		TASK_ID_PROCESS_MAP.put(TASK_PAYMENT_REVIEW, "14");
		TASK_ID_PROCESS_MAP.put(TASK_ADVANCE_PAYMENT_REVIEW, "1");
		TASK_ID_PROCESS_MAP.put(TASK_AMENDMENT_COF, "3");
		//Added in R6 : Returned Payment review
		TASK_ID_PROCESS_MAP.put(TASK_RETURN_PAYMENT_REVIEW, "16");
		//Ended R6 : Returned payment review
	}

	public static final String UPDATE_FLAG = "updateFlag";
	public static final String TASK_CONTRACT_CONFIGURATION_FULL = "Contract Configuration (Initial/Renewal/New Contract)";
	// Constants added as a part of release 3.8.0 enhancement 6534 - end

	// Constants added as a part of release 3.8.0 enhancement 6483 - start
	public static final String DELETE_SUB_BUDGET_CANCEL_UPDATE = "deleteSubbudgetForCancelUpdate";
	public static final String DELETE_BUDGET_CUST_CANCEL_UPDATE = "deleteBudgetCustForCancelUpdate";
	public static final String DELETE_BUDGET_CANCEL_UPDATE = "deleteBudgetForCancelUpdate";
	public static final String DELETE_CONTRACT_FIN_CANCEL_UPDATE = "deleteConFinForCancelUpdate";
	public static final String DELETE_CONTRACT_FUNDING_CANCEL_UPDATE = "deleteConFundingForCancelUpdate";
	public static final String DELETE_CONTRACT_CANCEL_UPDATE = "deleteContractForCancelUpdate";
	public static final String FETCH_DISCREPENCY_DETAILS_UPDATES_TASK = "fetchDiscrepencyDetailsForUpdateTask";
	public static final String CANCEL_CONTRACT_CONF_UPDATE = "cancelContractConfUpdate";
	public static final String AO_FINAL_FINISH = "aoFinalFinish";
	public static final String AS_UPDATE_CONTRACT_ID = "asUpdateContractId";
	public static final String AS_BASE_CONTRACT_ID = "asBaseContractId";
	public static final String AS_SELECTED_CONTRACT_ID = "asSelectedContractId";
	public static final String CANCEL_CONTRACT_UPDATE = "Cancel Contract Update";
	// Constants added as a part of release 3.8.0 enhancement 6483 - end

	// [Start] release 3.9.0 enhancement 6524
	public static final String CONTRACT_DETAIL_INFO = "getContractDetailInfo";
	public static final String CONTRACT_RENEWAL_DETAIL_INFO = "getContractRenewalDetailInfo";
	public static final String CONTRACT_COF_LIST_BEAN = "aoContractCOFListBean";
	// [End] release 3.9.0 enhancement 6524

	// Constants added as a part of release 3.8.0 enhancement 6469 - start
	public static final String DOC_ID_COUNT = "documentIdCount";
	public static final String COUNT_DOC = "count_doc";
	public static final String AO_APP_STATUS_DOC_MAP = "aoAppStatusDocMap";
	// Constants added as a part of release 3.8.0 enhancement 6469 - end

	// Constants added as a part of release 3.8.0 enhancement 6481 - start
	public static final String GET_PEND_REG_CON_APPROVED_BUD_COUNT = "getPendingRegContractApprovedBudgetCount";
	public static final String FETCH_RATE_REMAINING_PAYMENT_DISBURSED = "fetchRateRemainingPaymentDisbursed";
	public static final String FETCH_RATE_REMAINING_PAYMENT_DISBURSED_UNITS = "fetchRateRemainingPaymentDisbursedUnits";
	public static final String FETCH_FY_BUDGET_LINE_ITEM = "fetchFyBudgetLineItem";
	public static final String FETCH_FY_BUDGET_LINE_ITEM_UNITS = "fetchFyBudgetLineItemUnits";
	public static final String FETCH_RATE_REMAINING_PENDING_INVOICE_NEGATIVE = "fetchRateRemainingPendingInvoiceNegative";
	public static final String REMAINING_AMOUNT_LESS_THAN_FY_BUDGET = "remainingAmountLessThanFyBudget";
	public static final String REMAINING_UNITS_LESS_THAN_FY_UNITS = "remainingUnitsLessThanFyUnits";
	public static final String PERSONNEL_SERVICE = "PERSONNEL_SERVICE";
	public static final String CONTRACTED_SERVICE = "CONTRACTED_SERVICE";
	public static final String UTILITIES_TABLE = "UTILITIES";
	public static final String RENT_TABLE = "RENT";
	public static final String INDIRECT_RATE_TABLE = "INDIRECT_RATE";
	public static final String EQUIPMENT = "EQUIPMENT";
	public static final String FRINGE_BENEFIT = "FRINGE_BENEFIT";
	public static final String MILESTONE = "MILESTONE";
	public static final String OPERATIONS_AND_SUPPORT = "OPERATIONS_AND_SUPPORT";
	public static final String PROFESSIONAL_SERVICE = "PROFESSIONAL_SERVICE";
	public static final String PROGRAM_INCOME = "PROGRAM_INCOME";
	public static final String RATE = "RATE";
	// Constants added as a part of release 3.8.0 enhancement 6481 - end

	// Constants added as a part of release 3.8.0 enhancement 6482 - start
	public static final String CONTRACT_INFORMATION_UPDATE = "Contract Information Update";
	public static final String UPDATE_CONTRACT_INFORMATION = "updateContractInformation";
	public static final String AS_UPDATED_CONTRACT_TITLE = "asUpdatedContractTitle";
	public static final String AS_PROGRAM_ID = "asProgramId";
	public static final String AS_UPDATE_CONTRACT__FOR_TITLE_PROG_NAME = "updateContractForTitleProgramName";
	public static final String AS_UPDATE_BUDGET_FOR_TITLE_PROG_NAME = "updateBudgetForTitleProgramName";
	// Constants added as a part of release 3.8.0 enhancement 6482 - end

	public static final String CHECK_CONTRACT_IS_DELETED = "chechContractIsDeleted";
	public static final String CONTRACT_DELETED = "ContractDeleted";
	public static final String AO_IS_DELETED = "aoIsDeleted";
	public static final String CONTRACT_DELETED_ERROR_MSG = "ContractDeletedErrorMessage";

	// Start || Changes done for Enhancement #6577 for Release 3.10.0
	public static final String CANCEL_COMPETITION = "cancelCompetition";
	public static final String COMMENTS = "comments";
	public static final String UPDATE_PROPOSAL_STATUS_FOR_CANCEL_COMP = "updateProposalStatusForCancelComp";
	public static final String STATUS_PROPOSAL_CANCELLED = "PROPOSAL_CANCELLED";
	public static final String STATUS_COMPETITION_POOL_CANCELLED = "COMPETITION_POOL_CANCELLED";
	public static final String STATUS_EVALUATION_GROUP_CANCELLED = "EVALUATION_GROUP_CANCELLED";
	public static final String UPDATE_COMP_POOL_CANCELLED = "updateCompetitionPoolCancelled";
	public static final String PROP_STATUS_ID = "proposalStatusId";
	public static final String COMP_POOL_STATUS_ID = "compPoolStatusId";
	public static final String FETCH_PROPOSAL_FOR_CANCEL_COMP = "fetchProposalForCancelComp";
	public static final String FETCH_COMP_POOL_INFO = "fetchCompPoolInfo";
	public static final String TOTAL_COMPS = "TOTAL_COMPS";
	public static final String CANCELLED_COMPS = "CANCELLED_COMPS";
	public static final String CANC_NOPROPOSALS_COMPS = "CANC_NOPROPOSALS_COMPS";
	public static final String UPDATE_EVAL_GROUP_CANCELLED = "updateEvalGroupCancelled";
	public static final String EVAL_GROUP_STATUS_ID = "evalGroupStatusId";
	public static final String FETCH_EVAL_GROUP_INFO = "fetchEvalGroupInfo";
	public static final String TOTAL_EVALS = "TOTAL_EVALS";
	public static final String CANCELLED_EVALS = "CANCELLED_EVALS";
	public static final String UPDATE_PROC_CANCELLED = "updateProcCancelled";
	public static final String PROC_CANCELLED_COMP_STATUS_ID = "PROC_CANCELLED_COMP_STATUS_ID";
	public static final String PROC_CANCELLED_STATUS_ID = "procStatusId";
	public static final String CHECK_COMP_POOL_STATUS = "checkCompPoolStatus";
	public static final String COMP_POOL_NOT_CANCELLED = "compPoolNotCancelledFlag";
	public static final String COMP_POOL_CANCELLED = "compPoolCancelled";
	public static final String EVALUATIONS_COMPLETE = "Evaluations Complete";
	public static String FETCH_PROVIDERS_IN_COMPETITION = "fetchProvidersInCompetition";
	public static final String NT_405 = "NT405";
	public static final String AL_405 = "AL405";
	public static final String COMPETITION_TITLE = "COMPETITION_TITLE";
	public static final String PROVIDER_ID_LIST = "loProviderIdList";
	public static final String FETCH_COMP_TITLE_AND_PROC_TITLE = "fetchCompTitleAndProcTitle";
	public static String CHECK_COMP_POOL_WITHOUT_EVAL_GROUP = "checkCompPoolWithoutEvalGroup";
	public static String EVAL_POOL_CANCELLED = "loEvalPoolCancelledStatus";
	public static String FETCH_CONTRACTS_FOR_CANCEL_TASKS = "fetchContractsForCancellingTasks";
	public static String UPDATE_CANCELLED_CONTRACT = "updateCancelledContract";
	public static String UPDATE_CANCELLED_BUDGET = "updateCancelledBudget";
	public static String FETCH_REGISTERED_CLOSD_CONTRACTS_COUNT = "fetchRegOrClosedContractsCount";
	public static String CANCEL_STATUS_FLAG = "cancelStatusFlag";
	public static final String CANCEL_COMPETITION_FAILED = "cancelCompetitionFailed";
	public static final String CANCEL_COMP = "Cancel Competition";
	public static final String CANCEL_COMP_AUDIT_MSG = "Competition Pool cancelled for competition id:";
	public static String CANCEL_COMPETITION_RENDER = "cancelCompetitionRender";
	// End || Changes done for Enhancement #6577 for Release 3.10.0

	// Constants added for enhancement 6448 for Release 3.8.0 - start
	public static final String TASK_AVAILABLE = "taskAvailable";
	public static final String PENDING_APPROVE_AWARD = "PENDING_APPROVE_AWARD_TASKS";
	// Constants added for enhancement 6448 for Release 3.8.0 - end

	// Constants added for Payment Batch - Updating Accounting Line - start
	public static final String SELECT_MAX_INTERIM_PERIOD_END_DATE = "selectMaxInterimPerionEndDate";
	public static final String AO_MAX_INTERIM_PERIOD_END_DATE = "aoMaxInterimPeriodEndDate";
	public static final String UPDATE_ACCOUNTING_LINE = "updateAccountingLine";
	// Constants added for Payment Batch - Updating Accounting Line - end

	// Constants added for enhancement 6576 for Release 3.10.0 - start
	public static final String PREVENT_PAYMENT_OVER_BUDGET_TOTAL = "preventPaymentOverBudgetTotal";

	// Start || Changes done for Enhancement #6574 for Release 3.10.0
	public static String GET_AWARD_CONFIG_DOCS = "getAwardConfigDocs";
	public static String UPDATE_AFTER_APPROVAL = "updateafterApprovalStatus";
	public static String UPDATE_AFTER_AWARD_APPROVAL = "updateAfterAwardApproval";
	public static String FETCH_UPDATED_CONTRACTS = "fetchUpdatedContracts";
	public static String UPDATED_MODIFIED_FLAG_FOR_ORG = "updateModifiedFlagForOrg";
	public static String FETCH_EVAL_POOL_MAPPING_STATUS = "fetchEvalPoolMappingStatus";
	public static String AWARD_APPROVAL_TERMINATED = "34S";
	public static String FETCH_SELECTED_PROPOSALS = "fetchSelectedProposals";
	public static String FETCH_CONTRACTS_COUNT = "fetchContractCount";
	// End || Changes done for Enhancement #6574 for Release 3.10.0

	// START || Added as a part of release 3.11.0 for enhancement request 5978
	public static String FETCH_INFO_FOR_RETURNED_PROP_NOTIFICATN = "fetchInfoForReturnedPropNotificatn";
	public static String ORGANIZATION_NAME_VAL = "ORGANIZATION_NAME";
	public static String USER_ID_VAL = "USER_ID";
	public static String USER_LEVELS_AGENCY = "userLevelsAgency";
	public static String FETCH_USERS_NT225 = "fetchUsersForNT225";
	public static String BCC_USERS = "BCC_USERS";
	public static String FETCH_AGENCY_USER_NT225 = "fetchAgencyUserIdForNT225";
	public static String FETCH_AGENCY_USER_FOR_PROC = "fetchAgencyUserIdForProc";
	public static String NOTIFICATION_ALERT = "NotificationAlert_";
	public static final String NEW_USERS = "NEW_USERS";
	public static final String NT007 = "NT007";
	// END || Added as a part of release 3.11.0 for enhancement request 5978

	public static final List<String> CURRENT_LEVEL = new ArrayList<String>();

	static
	{
		CURRENT_LEVEL.add("2");
		CURRENT_LEVEL.add("3");
		CURRENT_LEVEL.add("4");
	}

	// START || Added as a part of release 3.11.0 for enhancement request 6620
	public static final String FETCH_AGENCY_NAME_FROM_BUDGET = "fetchAgencyNameFromBudget";
	public static String FETCH_AGENCY_NAME_FROM_INVOICE = "fetchAgencyNameFromInvoice";
	public static String FETCH_COMMENTS_AL314 = "fetchCommentsForAL314";
	public static String FETCH_AGENCY_NAME_FOR_NT207_AL212 = "fetchAgencyNameForNT207AL212";
	public static String ENTITY_TYPE_CONTRACT_BUDGET_AMEND_REVIEW = "Contract Budget Amendment Review";
	// END || Added as a part of release 3.11.0 for enhancement request 6620

	// START || Added as a part of release 3.12.0 for enhancement request 6487
	public static String FETCH_PAYMENT_AMOUNT = "fetchPaymentAmount";
	public static String PAYMENT_AMOUNT = "PAYMENT_AMOUNT";
	public static String PAYMENT_ID_COL = "PAYMENT_ID";
	// END || Added as a part of release 3.12.0 for enhancement request 6487

	// Added as a part of release 3.12.0 for enhancement request 6578 - start
	public static final String UPDATE_ACCOUNTING_LINES_AS_PER_FMS_FEED = "updateAccountingLinesAsPerFmsFeed";

	public static final List<String> TASK_LIST = new ArrayList<String>();

	static
	{
		// updating TASK_LIST values and removing single quotes form prefix and suffix as part of Payment batch issue in Emergency Release 6.0.1
		//TASK_LIST.add(STR + TASK_PAYMENT_REVIEW + STR);
		//TASK_LIST.add(STR + TASK_ADVANCE_PAYMENT_REVIEW + STR);
		TASK_LIST.add(TASK_PAYMENT_REVIEW);
		TASK_LIST.add(TASK_ADVANCE_PAYMENT_REVIEW);
		
	}

	public static final String PAYMENTS_AT_LEVEL_1 = "fetchPendingApprovalPaymentsAtLevel1";
	public static final String FETCH_UPDATED_ACCOUNTING_LINES = "fetchUpdatedAccountingLines";
	public static final String UPDATED_PAYMENT_LIST_FROM_FMS_FEED = "updatedPaymentListFromFmsFeed";
	public static final String JAVA_UTIL_LIST = "java.util.List";
	public static final String DELETE_PENDING_APPROVAL_PAYMENTS_AT_LEVEL_1 = "deletePendingApprovalPaymentsAtLevel1";
	public static final String UPDATE_BATCH_IN_PROGRESS_FLAG = "updateBatchInProgressFlag";
	public static final String BATCH_IN_PROGRESS_FLAG = "batchInProgressFlag";
	public static final String FETCH_BATCH_IN_PROGRESS_FLAG = "fetchBatchInProgressFlag";
	public static final String BATCH_IN_PROGRESS_ERROR = "BATCH_IN_PROGRESS_ERROR";
	public static final String UPDATE_BATCH_IN_PROGRESS_FLAG_FOR_ACTIVE_RECORDS = "updateBatchInProgressFlagForActiveRecords";
	public static final String FETCH_PAYMENTS_AND_UPDATE_FLAG = "fetchPaymentsAtLevel1AndUpdateFlag";
	public static final String PENDING_APPROVED_PAYMENT_IDS_LIST = "loPendingApprovedPaymentIdList";
	// Added as a part of release 3.12.0 for enhancement request 6578 - end

	// START || Added as a part of release 3.12.0 for enhancement request 6602
	public static String CANCEL_CONFIGURE_NEW_FY = "cancelConfigureNewFY";
	public static String DELETE_NEWLY_ADDED_CONFIN_ROWS = "deleteNewlyAddedContractFinancialsRows";
	public static String COPY_PREVIOUS_AMOUNT_TO_AMOUNT_FOR_FIN = "copyPreviousAmountToAmountForFinancials";
	public static String DELETE_BUDGET_CUST_FOR_CANCEL_CONFIGURE_NEWFY = "deleteBudgetCustForCancelConfigureNewFY";
	public static String DELETE_SUB_BUDGET_CUST_FOR_CANCEL_CONFIGURE_NEWFY = "deleteSubbudgetForCancelConfigureNewFY";
	public static String DELETE_BUDGET_FOR_CANCEL_CONFIGURE_NEWFY = "deleteBudgetForCancelConfigureNewFY";
	public static String COPY_FY_AMOUNT_TO_PREVIOUS_AMOUNT_FOR_FIN = "copyFYAmountToPreviousAmountForFinancials";
	public static String CANCEL_CONFIGURE_NEW_FY_FOR_AUDIT = "Cancel Configure New FY";
	public static String CANCEL_CONFIGURE_NEW_FY_TEXT = "Configure New FY Cancelled for fiscal year id:";
	public static String COPY_PREVIOUS_AMOUNT_TO_AMOUNT_FOR_FIN_FUNDING = "copyPreviousAmountToAmountForFinFunding";
	public static String COPY_FY_AMOUNT_TO_PREVIOUS_AMOUNT_FOR_FIN_FUNDING = "copyFYAmountToPreviousAmountForFinFunding";
	public static String NEW_FISCAL_YEAR_ID = "newFiscalYearId";
	public static String DELETE_NEW_FY_DOCUMENTS = "deleteNewFYDocuments";

	public static String FETCH_AMENDMENT_BUDGET_ID = "fetchAmendmentBudgetIds";
	public static final String DELETE_PERSONNEL_SERVICE_CANCEL_NEWFY = "deletePersonnelServiceForNewFYCancel";
	public static final String DELETE_OP_SUPPORT_OTHERS_CANCEL_NEWFY = "deleteOpSupportOthersForNewFYCancel";
	public static final String DELETE_OP_SUPPORT_CANCEL_NEWFY = "deleteOpSupportForNewFYCancel";
	public static final String DELETE_UTILITIES_CANCEL_NEWFY = "deleteUtilitiesForNewFYCancel";
	public static final String DELETE_PROFF_SERVICE_OTHERS_CANCEL_NEWFY = "deleteProffServiceOthersForNewFYCancel";
	public static final String DELETE_PROFF_SERVICE_CANCEL_NEWFY = "deleteProffServiceForNewFYCancel";
	public static final String DELETE_RENT_CANCEL_NEWFY = "deleteRentForNewFYCancel";
	public static final String DELETE_CONTRACTED_SERVICE_CANCEL_NEWFY = "deleteContractedServiceForNewFYCancel";
	public static final String DELETE_RATE_CANCEL_NEWFY = "deleteRateForNewFYCancel";
	public static final String DELETE_MILESTONE_CANCEL_NEWFY = "deleteMilestoneForNewFYCancel";
	public static final String DELETE_UNALLOCATED_CANCEL_NEWFY = "deleteUnallocatedForNewFYCancel";
	public static final String DELETE_INDIRECT_RATE_CANCEL_NEWFY = "deleteIndirectRateForNewFYCancel";
	public static final String DELETE_PROG_INCOME_OTHERS_CANCEL_NEWFY = "deleteProgIncomeOthersForNewFYCancel";
	public static final String DELETE_PROGRAM_INCOME_CANCEL_NEWFY = "deleteProgramIncomeForNewFYCancel";
	public static final String DELETE_FRINGE_BENEFIT_CANCEL_NEWFY = "deleteFringeBenefitForNewFYCancel";
	public static final String DELETE_EQUIPMENT_CANCEL_NEWFY = "deleteEquipmentForNewFYCancel";
	public static final String DELETE_SUB_BUDGET_SITE_CANCEL_NEWFY = "deleteSubBudgetSiteForNewFYCancel";
	public static final String DELETE_SUB_BUDGET_CANCEL_NEWFY = "deleteSubBudgetForNewFYCancel";
	public static final String DELETE_BUDGET_CUSTOMIZ_CANCEL_NEWFY = "deleteBudgetCustomizForNewFYCancel";
	public static final String DELETE_BUDGET_DOC_CANCEL_NEWFY = "deleteBudgetDocForNewFYCancel";
	public static final String DELETE_ASSOCIATED_BUDGETS_CANCEL_NEWFY = "deleteAssociatedBudgetsForNewFYCancel";

	// END || Added as a part of release 3.12.0 for enhancement request 6602

	// Added as a part of release 3.15.0 for enhancement - start
	public static final String DELETE_BUDGET_WITH_ALL_SUB = "deleteBudgetWithAllSub";
	// Added as a part of release 3.15.0 for enhancement - End

	// Added as a part of release 3.12.0 for enhancement request 6631 - start
	public static final String ERROR_NEGATIVE_AMENDMENT_VALIDATION_MESSAGE_1 = "ERROR_NEGATIVE_AMENDMENT_VALIDATION_MESSAGE_1";
	// Added as a part of release 3.12.0 for enhancement request 6631 - end

	// START || Added as a part of release 3.12.0 for enhancement request 6495
	public static String GET_PAYMENT_STATUS_LIST_PROVIDER = "getPaymentStatusListProvider";
	// END || Added as a part of release 3.12.0 for enhancement request 6495

	// START || Added as a part of release 3.12.0 for enhancement request 6601
	public static final String MARK_AMENDMENT_ETL_REGISTERED_WHICH_ARE_REGISTERED_IN_FMS = "markAmendmentETLRegistredWhichAreRegisteredInFMS";
	public static final String UPDATE_FLAG_AMENDMENT_REGISTERED_IN_FMS = "updateFlagAmendmentRegisteredInFMS";
	public static final String UPDATE_AND_PARTIAL_MERGE_AMENDMENT_REGISTERED_IN_FMS = "updateAndPartialMergeAmendmentRegisteredInFMS";
	public static final String GET_AMENDMENT_REGISTERED_IN_FMS = "getAmendmentRegisterdInFms";
	public static final String LO_AMENDMENT_LIST = "loAmendmentList";
	public static final String LO_AMENDMENT_BUDGET_LIST = "loAmendmentBudgetList";
	public static final String IS_AMENDMNET_REGISTERED_IN_FMS = "isAmendmentRegisteredInFMS";
	public static final String FETCH_AMENDMENT_CONTRACT_BUDGETS = "fetchAmendmentContractBudgets";
	public static final String FETCH_AMENDMENT_CONTRACT_BUDGETS_ALREADY_MERGED = "fetchAmendmentContractBudgetsAlreadyMerged";
	public static final String MISMATCH_CONTRACT_FINANCIALS_AND_BUDGET_AMOUNT = "mismatchContractFinancialsAndBudgetAmount";
	public static final String LS_AMENDMENT_CONTRACT_LIST = "lsAmendmentContractList";
	public static final String LS_AMENDMENT_CONTRACT_ID = "lsAmendmentContractId";

	public static final String CBM_AMEND_BASE_CONTRACT_AMOUNT_FOR_FMS_REGISTERED_CONTRACT = "mergeContractForAmendmentFMSRegisteredContract";
	public static final String CBM_UPDATE_BASE_CONTRACT_END_DATE_FOR_FMS_REGISTERED_CONTRACT = "updateContractEndDateFMSRegisteredContract";
	public static final String CBM_UPDATE_BASE_CONTRACT_FINANCIAL_AMOUNT_FOR_FMS_REGISTERED_CONTRACT = "mergeContractFinancialForAmendmentFMSRegisteredContract";
	public static final String CBY_CREATE_CF_REPLICA_FOR_FMS_REGISTERED_CONTRACT = "createContractFinancialReplicaFMSRegisteredContract";
	public static final String CBM_DELETE_CF_REPLICA_FOR_FMS_REGISTERED_CONTRACT = "markContractFinancialAsDeletedFMSRegisteredContract";
	public static final String RESET_FLAG_AMENDMENT_REGISTERED_IN_FMS = "resetFlagAmendmentRegisteredInFMS";
	public static final String NT413 = "NT413";
	public static final String GET_CONTRACT_INFORMATION = "getContractInformation";
	public static final String GET_CONTRACT__FINANCIALS_INFORMATION = "getContractFinancialsInformation";
	public static final String CANCEL_AMENDMENT_REGISTERED_IN_FMS_CHECK = "CANCEL_AMENDMENT_REGISTERED_IN_FMS_CHECK";
	public static final String CS_FETCH_FY_AND_CONTRACT_ID_AMENDMENT = "fetchFYAndContractIdAmendment";
	// END || Added as a part of release 3.12.0 for enhancement request 6601

	// START || Added as a part of release 3.12.0 for enhancement request 6643
	public static final String UPDATE_PS_MODIFIED_DATE = "updatePersonnelServiceModifiedDate";
	public static final String UPDATE_OPS_MODIFIED_DATE = "updateOperationAndSupportModifiedDate";
	public static final String UPDATE_UTILITIES_MODIFIED_DATE = "updateUtilitiesModifiedDate";
	public static final String UPDATE_PFS_MODIFIED_DATE = "updateProfessionalServiceModifiedDate";
	public static final String UPDATE_RENT_MODIFIED_DATE = "updateRentModifiedDate";
	public static final String UPDATE_CS_MODIFIED_DATE = "updateContractedServicesModifiedDate";
	public static final String UPDATE_RATE_MODIFIED_DATE = "updateRateModifiedDate";
	public static final String UPDATE_MILESTONE_MODIFIED_DATE = "updateMilestoneModifiedDate";
	public static final String UPDATE_UNALLOCATED_MODIFIED_DATE = "updateUnallocatedModifiedDate";
	public static final String UPDATE_INDIRECT_RATE_MODIFIED_DATE = "updateIndirectRateModifiedDate";
	public static final String UPDATE_PROGRAM_INCOME_MODIFIED_DATE = "updateProgramIncomeModifiedDate";
	public static final String UPDATE_FRINGE_MODIFIED_DATE = "updateFringeModifiedDate";
	public static final String UPDATE_EQUIPMENT_MODIFIED_DATE = "updateEquipmentModifiedDate";
	public static final String UPDATE_SUB_BUDGET_MODIFIED_DATE = "updateSubBudgetModifiedDate";
	public static final String UPDATE_BUDGET_MODIFIED_DATE = "updateBudgetModifiedDate";
	public static final String UPDATE_CONTRACT_MODIFIED_DATE = "updatedModifiedDateInContract";
	public static final String UPDATE_SUB_BUDGET_YTD_INVOICE_AMOUNT = "updateSubBudgetYtdInvoiceAmoutnt";
	
	public static final String UPDATE_BUDGET_YTD_INVOICE_AMOUNT = "updateBudgetYtdInvoiceAmoutnt";
	public static final String UPDATE_MODIFIED_DATE_ASSIGNMENT = "updateModifiedDateInAssignment";
	public static final String UPDATE_MODIFIED_DATE_ASSIGNMENT_IN_ADVANCE_REVIEW = "updateModifiedDateInAssignmentInAdvanceReview";
	public static final String FISCAL_YEAR_ID_NT = "FISCAL_YEAR_ID";
	public static final String FISCAL_YEAR_BUDGET = "FISCAL_YEAR_BUDGET";
	public static final String FISCAL_YEAR_MERGED_VALUE = "FISCAL_YEAR_MERGED_VALUE";
	public static final String CANCEL_AMENDMENT_CHECK_REG_IN_FMS = "cancelAmendmentCheckRegisteredInFms";
	// END || Added as a part of release 3.12.0 for enhancement request 6643

	// START || Added as a part of release 3.12.0 for enhancement request 6585
	public static String AS_CONTRACT_SOURCE = "asContractSource";
	// END || Added as a part of release 3.12.0 for enhancement request 6585

	// release 3.12.0 enhancement 6601 11jan
	public static String GET_PENDING_AMENDMENT_BUDGET_FISCAL_YEAR_ID = "getPendingAmendmentBudgetFiscalYearId";
	public static String GET_CONTRACT_FINANCIALS_UPDATE_AMOUNT = "getContractFinancialUpdateAmount";
	public static String PENDING_AMENDMENT_VALIDATION_MESSAGE = "PENDING_AMENDMENT_VALIDATION_MESSAGE";
	public static final String CS_VALIDATE_CONTRACT_CONFIG_AMEND_AMOUNT = "validateContractConfigAmendmentAmount";
	// release 3.12.0 enhancement 6601 11jan

	// release 3.14.0
	public static final String CS_FETCH_NEXT_NEW_FY_BUDGET_YEAR = "getNextNewFYBudgetYear";
	public static final String CS_FETCH_LAST_CONFIGURED_FY_BUDGET_YEAR = "getLastConfiguredFYBudgetYear";
	public static final String CS_FETCH_NEXT_NEW_FY_BUDGET_DETAILS = "getNextNewFYBudgetDetails";
	public static final String CS_FETCH_NEXT_NEW_FY = "nextNewFYFiscalYear";
	public static final String CS_ADD_CONTRACT_CONF_AMENDMENT_BUDGET_DETAILS_ADD_ITS_PARENT = "addContractConfAmendmentBudgetDetailsAddItsParent";
	public static final String CS_ADD_CONTRACT_CONF_AMENDMENT_BUDGET_DETAILS_ADD_FOR_NEXT_NEW_FY = "addContractConfAmendmentBudgetDetailsForNextNewFy";
	public static final String CS_EDIT_CONTRACT_CONF_AMEND_SUB_BUDGET_DETAILS_NEXT_NEW_FY = "editContractConfAmendSubBudgetDetailsNextNewFy";
	public static final String FETCH_LAST_FY_CONFIGURED_AND_NEXT_NEW_FY = "fetchLastFYConfiguredNextNewFy";
	public static final String CS_FETCH_CONTRACT_CONF_AMEND_SUBBUDGET_DETAILS_NEXT_NEW_FY = "fetchContractConfAmendSubBudgetDetailsNextNewFY";
	public static final String CS_UPDATE_AMENDMENT_BUDGET_STATUS_NEXT_NEW_FY = "updateAmendmentBudgetStatusNextNewFY";
	public static final String CS_SELECT_AMENDMENT_BUDGET_STATUS_NEXT_NEW_FY = "selectAmendmentBudgetStatusNextNewFY";
	public static final String CS_UPDATE_AMENDMENT_REGISTERED_IN_FMS_BUDGET_STATUS_NEXT_NEW_FY = "updateAmendmentRegisteredInFMSBudgetStatusNextNewFY";
	public static final String BUDGET_AMENDMENT_REGISTERED_FMS = "budgetAmendmentRegisteredInFms";
	public static final String CBM_UPDATE_BASE_CONTRACT_FINANCIAL_AMOUNT_FOR_FMS_REGISTERED_CONTRACT_NEXT_NEW_FY = "mergeContractFinancialForAmendmentFMSRegisteredContractNextNewFY";
	public static final String CBY_CREATE_CF_REPLICA_FOR_FMS_REGISTERED_CONTRACT_NEXT_NEW_FY = "createContractFinancialReplicaFMSRegisteredContractNextNewFY";
	public static final String CBM_DELETE_CF_REPLICA_FOR_FMS_REGISTERED_CONTRACT_NEXT_NEW_FY = "markContractFinancialAsDeletedFMSRegisteredContractNextNewFY";
	public static final String CBM_AMEND_BASE_CONTRACT_AMOUNT_FOR_FMS_REGISTERED_CONTRACT_NEXT_NEW_FY = "mergeContractForAmendmentFMSRegisteredContractNextNewFY";
	public static final String CS_DEL_CONTRACT_CONF_AMEND_SUBBUDGET_DETAILS_NEXT_NEW_FY = "delContractConfAmendSubBudgetDetailsNextNewFY";
	public static final String GET_PARENT_SUB_BUDGET_ID_NEXT_NEW_FY = "getParentSubBudgetIdNextNewFY";
	public static final String GET_AMENDMENT_SUB_BUDGET_NAME_SAME_WITH_ANOTHER_AMENDMENT = "getAmendSubBudgetNameSameWithAnotherAmendment";
	public static final String UPDATE_PARENT_WHILE_EDITING_AMENDMENT = "updateParentWhileEditingAmendment";
	public static final String UPDATE_PARENT_WHILE_EDITING_AMENDMENT_WHEN_PARENT_NOT_EXIST = "updateParentWhileEditingAmendmentWhenParentNotExist";
	public static final String GET_SUB_BUDGET_NAME = "getSubBudgetName";
	public static final String GET_SUB_BUDGET_NAME_FOR_SAME_AMENDMENT_BUDGET = "getSubBudgetNameForSameAmendmentBudget";
	public static final String GET_PARENT_SUB_BUDGET_ID_FOR_ALREADY_LINKED_AMENDMENT = "getParenSubBudgetIdForAlreadyLinkedAmendment";
	public static final String CS_EDIT_CONTRACT_CONF_AMEND_SUB_BUDGET_DETAILS_PARENT_NAME_NEXT_NEW_FY = "editContractConfAmendSubBudgetDetailsParentNameNextNewFy";
	public static final String CS_DEL_CONTRACT_CONF_AMEND_SUBBUDGET_PARENT_DETAILS_NEXT_NEW_FY = "delContractConfAmendSubBudgetParentDetailsNextNewFY";
	public static final String NEW_FY_FINISH_VALIDATE = "newFYFinishValidate";
	public static final String RETURN_AMENDMENT_COF_VALIDATE = "returnAmendmentCofValidate";
	public static final String FETCH_ALL_CONTRACT_ID_FOR_NEW_FY_CHECK = "fetchAllContractIdForNewFYCheck";
	public static final String IS_NEW_FY_CREATED_WITH_MERGED_VALUE_WHEN_AMENDMENT_IS_REG_IN_FMS = "isNewFYCreatedWithMergedValuesWhenAmendmentIsRegInFMS";
	public static final String AMENDMENT_CONFIGURATION_PENDING = "AMENDMENT_CONFIGURATION_PENDING";
	public static final String AMENDMENT_COF_RETURN = "AMENDMENT_COF_RETURN";
	public static final String GET_PARENT_SUB_BUDGET_COUNT = "getParentSubBudgetCount";
	public static final String ERROR_ADD_SUB_BUDGET_WITH_SAME_NAME = "ERROR_ADD_SUB_BUDGET_WITH_SAME_NAME";
	public static final String CS_UPDATE_AMENDMENT_CONTRACT_STATUS_NEXT_NEW_FY = "updateAmendmentContractStatusNextNewFY";
	public static final String CBY_SET_CONTRACT_BUDGET_STATUS_FOR_REVIEW_TASK_FOR_AMENDMENT = "setContractBudgetStatusForReviewTaskForAmendment";
	public static final String CBY_SET_CONTRACT_BUDGET_STATUS_FOR_REVIEW_TASK_FOR_AMENDMENT_IN_CONFIGURATION_STATUS = "setContractBudgetStatusForReviewTaskForAmendmentInConfigurationStatus";
	public static final String CS_FETCH_FY_AND_CONTRACT_ID_AMENDMENT_NEGATIVE = "fetchFYAndContractIdAmendmentNegative";
	public static final String GET_CONTRACT_START_END_DATE = "getAmendmentStartEndDate";
	public static final String CS_FETCH_CONTRACT_CONF_AMENDMENT_DETAILS_PARTIAL_MERGING = "fetchContractConfAmendmentDetailsPartialMerging";
	public static final String ORIGNAL_CONTRACT_END_DATE_KEY = "ORIGNAL_CONTRACT_END_DATE";
	public static final String UPDATE_BUDGET_CUSTOMIZATION_ON_NEW_FY_BUDGET = "updateBudgetCustomizationOnNewFyBudget";
	public static final String MERGE_CF_INCREASE_END_DATE = "mergeContractFinancialIncreaseEndDate";
	public static final String SOFT_DELETE_CF_INCREASE_END_DATE = "softDeleteContractFinancialIncreaseEndDate";
	/*Change for R5 starts*/ 
	public static final String CLEAN_DB_CACHE = "cleanDBCache";
	public static final String FETCH_CITY_USERS_NT234 = "fetchCityUserNT234";
	public static final String UPDATE_SUB_BUDGET_CUST_FOR_CANCEL_CONFIGURE_NEWFY = "updateSubbudgetForCancelConfigureNewFY";
	public static final String UPDATE_BUDGET_FOR_CANCEL_CONFIGURE_NEWFY = "updateBudgetForCancelConfigureNewFY";
	public static final String SUB_BUDGET_EXISTING_AMENDMENT_ERROR_CHECK = "subBudgetExistingAmendmentErrorCheck";
	public static final String SUB_BUDGET_EXISTING_AMENDMENT_ERROR_CHECK_MESSAGE = "SUB_BUDGET_EXISTING_AMENDMENT_ERROR_CHECK_MESSAGE";
	public static final String CHART_PATH = "//charts//chart//report-datatable";
	public static final String FETCH_CITY_USERS_NT232 = "fetchCityUserNT232";
	
	/*Change for R5 ends*/ 
	
	/*R6 change starts*/
	public static final String EXISTING_BUDGET = "existingBudget";
	
	//Contract and amendment inbound interface changes
	public static final String CONTRAT_STATUS_FOR_INBOUND_APT_BATCH_PROCESS = "CONTRAT_STATUS_FOR_INBOUND_APT_BATCH_PROCESS";
	public static final String SET_CONTRACT_STATUS_AS_REGISTERED_FOR_AMENDMENT_COF = "setContractStatusAsRegisteredForAmendmentCOF";
	public static final String SET_CONTRACT_STATUS_AS_REGISTERED_FOR_COF = "setContractStatusAsRegisteredForCOF";
	public static final String PROCESS_INBOUND_CONTRACTS_FOR_BATCH = "processInboundContractsForBatch";
	public static final String GET_FINANCIAL_WF_PROPERTY = "getFinancialWFProperty";
	public static final String PROCESS_INBOUND_AMENDMENTS_FOR_BATCH = "processInboundAmendmentsForBatch";
	public static final String SYSTEM_INTERFACE = "System Interface";
	
	//Returned Payment changes
	public static final String RETURNED_PAYMENT_DOCUMENT = "RETURN_PAYMENT_DOCUMENT";
	//Returned Payment changes end
	public static final String VALIDATE_EPIN_IS_UNIQUE = "validateEpinIsUnique";
	//Column of refAptEPinId
	public static final String REF_APT_EPIN_ID = "refAptEpinId";
	public static final String REF_APT_EPIN_ID_COL = "REF_APT_EPIN_ID";
	public static final String PROCUREMENT_AGENCY_ID = "procurementAgencyId";
	public static final String CONTRACT_AGENCY_ID = "contractAgencyId";
	public static final String EPIN_VALIDATION_RESPONSE_BODY_START = "{\"isValid\":\"";
	public static final String EPIN_VALIDATION_RESPONSE_BODY_ERROR = "\"errorMessage\":\"";
	
	//In bound interface out of year amendment changes
	public static final String UPDATE_PARENT_BUDGET_ID_FOR_ETL_AMENDMENT = "updateParentBudgetIdForEtlAmendment";
	public static final String GET_NEXT_NEW_FY_BUDGET_DETAILS_FOR_BATCH = "getNextNewFYBudgetDetailsForBatch";
	public static final String AS_PARENT_BUDGET_ID = "asParentBudgetId";
	public static final String QUERY_GET_BUDGET_ID_AMEND_BATCH = "fetchBudgetIdIfExistsForAmendBatch";
	
	/*R6 change ends*/
    //emergency build 6.0.1 - INC000001386100/INC000001385777
	//Fix for Budget page not loading due to single quote in contract title
	public static final String GET_CONTRACT_SUMMARY_TRANSACTION = "getContractSummary";
	/*Start: added for  R6.1.0  */
	public static final String TRANSACTION_RECONCILE_PROVIDER_STATUS = "reconcileProviderStatus";
	public static final String PROVIDER_STATUS_PARAM = "asProviderStatus";
	public static final String SYMBOL_SEPERATOR = " - ";
	/*End: added for  R6.1.0  */
	/*Start: added for  R6.2.0  */
	// Password Reset
	public static final String PWD_RESET_TOKEN_STATUS_ID = "tokenStatus";
	public static final String INVALID_TOKEN_TO_RESET_PWD_MSG = "You have clicked on an expired Password Reset Request link. Please check your email inbox for the most recent NYC.ID &quot;Password Reset&quot; Request email click on the Reset Password link and follow the prompts.";
	public static final String VALID_PASSWORD_RESET_SVC_OUTPUT_ID = "serviceOutput";
	public static final String VALID_PASSWORD_RESET_SVC_STATUS_ID = "serviceStatus";
	/*End: added for  R6.2.0  */
	/*Start: added for  R6.3 QC8702   */
	public static final String APPROVAL_PASSWORD_RESET_TOKEN = "Approval_Password_Reset_Token";
	/*End: added for  R6.3 QC8702   */
	//Start: R7 for defects
	public static final String FETCH_FY_BASE_CONTRACT_END_DATE = "fetchFYBaseContractEndDate";
	public static final String FETCH_FY_AMEND_CONTRACT_END_DATE = "fetchFYAmendContractEndDate";
	public static final String BUDGET_END_DATE_UPDATED_IN_CBUDGET ="budgetEndDateUpdatedInCBudget";
	public static final String BUDGET_END_DATE_UPDATED_IN_AMENDMENT ="budgetEndDateUpdatedInAmendment";
	public static final String ALL_CONTRACT_LIST ="allContractList";
	public static final String CANCEL_MERGE ="Cancel and Merge";
	public static final String FETCH_BUDGET_IDS_FOR_DELETION ="fetchBudgetIdsForDeletion";
	public static final String FETCH_REQUEST_PARTIAL_MERGE_STATUS="fetchRequestPartialMergeStatus";
	public static final String REQUEST_PARTIAL_MERGE_STATUS="asRequestPartialMergeStatus";
	public static final String FETCH_REQUEST_PARTIAL_MERGE_VALUE="fetchRequestPartialMergeValue";
	public static final String BASE_CONTRACT_BUDGET_STATUS="baseContractBudgetStatus";
	public static final String FETCH_PARENT_CONTRACT_ID="fetchParentContractId";
	public static final String AS_CONTRACT_ID="asContractId";
	//Added in R7-To Fix Defect #7211(delete payment Accounting lines while doing 'Return for Revision' apart from level 1)
	public static final String PREV_DATE_HOUR = "prevDateHour";
	public static final String PREV_DATE_HOUR_KEY = "fmsFeedForPayment_hours";
	public static final String FETCH_PAYMENTS_NOT_AT_LEVEL_ONE="fetchPendingApprovalPaymentsNotAtLevel1";
	public static final String PAYMENT_DETAIL_MAP = "paymentDetailMap";
	public static final String FETCH_PAYMENTS_NOT_LEVEL1 = "fetchPaymentsnotAtLevel1";
	public static final String PENDING_APPROVED_PAYMENT_LIST= "loPendingApprovedPaymentIdList";
	//End Defect #7211
	//Added in R7 for defect 8644 for audit purpose.
	public static final String FETCH_BUDGET_LIST= "fetchBudgetList";
	//End defect 8644
	/*Start: added in release 7 for defect 8884 :it will delete newly added sub budgets during NFY task on cancellation of NFY task  */
	public static final String DELETE_NEWLY_ADDED_SUB_BUDGETS = "deleteNewlyAddedSubBudgets";
	/*Ends: added in release 7 for defect 8884  */
	// Start: Added in R7 for defect 8705
	public static final String BASE_CONTRACT_BUDGET_END_DATE ="budgetEndDate";
	public static final String FETCH_BUDGET_DETAILS = "fetchBudgetDetails";
	public static final String CONTRACT_LIST_FETCHED = "contractListFetched";
	public static final String UPDATE_BUDGET_END_DATE_WITH_AMEND_END_DATE = "updateBudgetEndDateWithAmendEndDate";
	public static final String UPDATE_AMEND_CONTRACTS_BUDGET_STATUS_REQUEST_PARTIALMERGE = "UpdateAmendContractsBudgetPartialMerge";
	public static final String BUDGET_END_DATE_CAPS ="BUDGET_END_DATE";
	public static final String AMEND_CONTRACT_END_DATE_CAPS ="AMEND_CONTRACT_END_DATE";
	//End
	
	/** BEGIN Fix multi-tab Browsing QC8691 R7.1*/
	public static final String GET_BUDGET_ID_FROM_SUB_BUDGET = "getBudgetIdFromSubBudget";
	/** END Fix multi-tab Browsing QC8691 R7.1*/
	
	/** BEGIN Fix multi-tab Browsing QC6674 R7.1*/
	public static final String GET_ORG_FROM_BUSINESS_APP = "getOrgFromBusinessApp";
	/** END Fix multi-tab Browsing QC6674 R7.1*/
	
	/** BEGIN QC8914 R7.2.0*/
	public static final String PROC_PROPOSALS_RECEIVED = "Proposals Received";
	/** END QC8914 R7.2.0*/

    /** [Start]R7.4.0 QC9008 */

    public static final String CBL_CONFIRM_DEL_BUDGET_UPDATE = "confirmDeleteBudgetUpdateDlg";
    public static final String DELETE_BUDGET_UPDATE = "Delete Budget Update";
    
	public static final String PULL_BUDGET_UPDATE_SUMMARY = "pullUpBudgetUpdateSummery";
	public static final String FETCH_BUDGET_UPDATE_SUMMARY = "fetchBudgetUpdate4Del" ;
	public static final String DELETE_BUDGET_UPDATE_TASK = "deleteBudgetUpdateTask";
    public static final String BUDGET_AMENDMENT_TYPE_VAL   =  "1";
    public static final String BUDGET_BASE_TYPE_VAL        =  "2";
    public static final String BUDGET_MOD_TYPE_VAL         =  "3";
    public static final String BUDGET_UPDATE_TYPE_VAL      =  "4";
    public static final String BUDGET_ORIGINAL_TYPE_VAL    =  "5";
    
    public static final String BUDGET_UPDATE_DELETION_AUDIT = "Budget Update task in Fiscal Year (__FISCAL_YEARS__) deleted with Contract Configuration Update: contract __CONTRACT_ID__  reasion: ";
    public static final String STATIC_PARAM_FISCAL_YEARS  = "__FISCAL_YEARS__";
    public static final String STATIC_PARAM_CONTRACT_ID   = "__CONTRACT_ID__";

    public static final String TASK_REMOVAL = "Task Removal";
    public static final String BUDGET_UPDATE_DELETION = "Budget Update Deletion";
    
    public static final String BUDGET_S_UPDATE = "Budget Update";
    public static final String REWRITE_NT318_MSG = "rewriteMsgBodyNT318ByAgency";
    
    public static final String DELETE_BUDGET_UPDATE_COMMENT = "delBudgetUpdateComment";
    
    public static final String GET_USER_EMAIL_IDS_NT318 = "getUserEmailIdsNT318";
    public static final String FISCAL_YEAR_IS_S = "fiscalYearIds";
    public static final int    MAX_HHS_AUDIT_DATA_COL_SIZE =  1000;
    /** [End]R7.4.0 QC9008 */

    /** Start: QC 7710 R 7.5.0 */
	public static final String BUDGET_CANCELLED_STATUS_ID = "89";
	public static final String BUDGET_SUSPENDED_STATUS_ID = "87";
	/** End: QC 7710 R 7.5.0 */
	
	/** Start QC 8774 R 7.5.0 */
	public static final String UPDATE_INVOICE_ADVANCE_RECOUPED_AMNT = "updateInvoiceAdvanceWith0RecoupedAmntForInvoiceWithdrawn";
	/** End QC 8774 R 7.5.0 */

	/** Start QC 8394 R 7.9.0  Add/Delete Unallocated Fund */
	public static final String LESS_THEN_APPROVED_FOR_NEGATIVE_AMENDMENT_ERROR_MESSAGE = "lessThanApprovedForNegAmend";
	public static final String CHILDID = "childId";
	public static final String INSERT_NEW_UNALLOCATED_FUNDS_FOR_AMD = "insertNewUnallocatedFundsForAmendment";
	public static final String UNALLOCATED_FUND ="unallocatedFund";
	public static final String FETCH_AMENDMENT_UNALLOCATED_FUNDS_TO_BASE_LINE  = "fetchAmendmentUnallocatedFundsToBaseLine";
	public static final String FETCH_MODIFICATION_UNALLOCATED_FUNDS_TO_BASE_LINE = "fetchModificationUnallocatedFundsToBaseLine";
	public static final String INSERT_NEW_UNALLOCATED_FUNDS_FOR_MOD = "insertNewUnallocatedFundsForModification";
	public static final String MODIFICATION_ADD_UNALLOCATED_FUNDS = "addModificationUnallocatedFunds";
	public static final String MODIFICATION_DELETE_UNALLOCATED_FUNDS = "deleteModificationUnallocatedFunds";
	public static final String AMENDMENT_ADD_UNALLOCATED_FUNDS = "addAmendmentUnallocatedFunds";
	public static final String AMENDMENT_DELETE_UNALLOCATED_FUNDS = "deleteAmendmentUnallocatedFunds";
	public static final String PARENTID = "parentId";
	public static final String FETCH_UNALLOCATED_FUNDS_CHILD_RECORD = "fetchUnallocatedFundsChildRecord";
	public static final String FETCH_UNALLOCATED_FUNDS_PARENT_RECORD = "fetchUnallocatedFundsParentRecord";
	public static final String LESS_THEN_APPROVED_BUDGET_ERROR_MESSAGE ="belowThanApprovedBudget";
	public static final String ADD_UNALLOCATED_FUNDS = "addUnallocatedFunds";
	public static final String DELETE_UNALLOCATED_FUNDS = "deleteUnallocatedFunds";
	/** End QC 8394 R 7.9.0 Add/Delete Unallocated Fund */ 


	/** [Start] QC 9165 R 7.9.0 */
    public static final String                  NYC_USER_ORG_DETAILS                                                                 = "getNYCUserOrganizationDetails";
    public static final String                  NYC_USER_ORG_DETAILS_RESULT                                                          = "aoStaffDetailBean";
    public static final String                  NYC_STAFF_DETAILS_PARAM_KEY                                                          = "aoStaffDetails";

    
    
    public static final String                  NYC_USER_FIRST_NAME                                                                  = "FirstName";
    public static final String                  NYC_USER_INITIALS                                                                    = "Initials";
    public static final String                  NYC_USER_LAST_NAME                                                                   = "LastName";
    public static final String                  NYC_USER_EMIL_VALIDATION_FLAG                                                        = "nycExtEmailValidationFlag";

    public static final String                  SECURITY_TIMESTAMP_FORMAT                                                            = "yyyy.MM.dd.HH.mm.ss";

    public static final String                  SAML_USER_PROFILE_UPDATE                                                             = "updateProfile";

    public static final String                  CREATE_NEW_STAFF_INFO                                                                = "insertStaffDetails";

    public static final String                  CHECK_PROVIDER_NYC_ID_UPDATE                                                         = "checkProviderNycIdUpdate";
    public static final String                  UPDATE_STAFF_ORGANIZATION_USER_STATUS                                                = "updateStaffOrganizationUserStatus";
    public static final String                  GET_SETTINGS_VALUE                                                                   = "getSettingsValue";
    public static final String                  UPDATE_SETTINGS_VALUE                                                                = "updateSettingsValue";
    public static final String          		NYCID_UPDATE_DATE																	 = "NYCID_UPDATE_DATE";
    public static final String          		MAXIMUM_COUNTER																	 	 = "MAXIMUM_COUNTER";
    /** [End] QC 9165 R 7.9.0 */
    /** [Start] QC 9202 R 7.10.0 */
	public static final String 					GET_FISCAL_YEAR_ID_FROM_SUB_BUDGET 													= "getFiscalYearIdFromSubBudget";
	public static final String 					GET_FISCAL_YEAR_ID_FROM_BUDGET  													= "getFiscalYearIdFromBudget";
	/** [End] QC 9202 R 7.10.0 */
		
    /** [Start] QC 9122 R 7.11.0 */
	public static final String 					FETCH_CONTRACT_BUDGET_STATUS_ID_LIST 												= "fetchContractBudgetStatusId";
	/** [End] QC 9122 R 7.11.0 */

    /** [Start] QC 9314 R7.12.0 */
	public static final String 					SET_AMENDMENT_STATUS_AS_REGISTERED 													= "setAmendmentStatusAsRegistered";
	public static final String 					SET_BUDGET_FROM_AMEND_STATUS_AS_ACTIVE 												= "setBudgetFromAmendStatusAsActive";
	/** [End] QC 9314 R7.12.0 */

    /** [Start] QC 9378 R 8.1 */
    public static final String                  SHRINK_NOTIFICATION_DATA                                                            = "shrinkNotificationData";
    public static final String                  REMOVED_NOTIFICATION_DATA_CNT                                                       = "countUserNotification";
    public static final String                  REMOVED_ALERT_URL_DATA_CNT                                                          = "countAlertUrl";

    public static final String                  IN_BOUND_PARAM_NOTIFICATION                                                         = "notification_ids";
    public static final String                  FETCH_OLD_USER_NOTIFICATION                                                         = "fetchOldUserNotification";
    public static final String                  ARCHIVE_USER_NOTIFICATION_DATA                                                      = "archiveUserNotificationData";
    public static final String                  SHRINK_USER_NOTIFICATION_DATA                                                       = "shrinkUserNotificationData";

    public static final String                  IN_BOUND_PARAM_ALERT_URL                                                            = "alert_url_ids";
    public static final String                  FETCH_OLD_NOTIFICATION_ALERT_URL                                                    = "fetchOldNotificationAlertUrl";
    public static final String                  ARCHIVE_NOTIFICATION_ALERT_URL_DATA                                                 = "archiveNotificationAlertUrlData";
    public static final String                  SHRINK_NOTIFICATION_ALERT_URL_DATA                                                  = "shrinkNotificationAlertUrlData";
    /** [End] QC 9378 R 8.1 */

    /** [Start] R8.4.1 for QC9513   */
    public static final String                  REMOVED_GROUP_NOTIFICATION_DATA_CNT                                                          = "countGroupNotification";
    
    public static final String                  IN_BOUND_PARAM_GROUP_NOTIFICATION                                                            = "group_noti_ids";
    public static final String                  FETCH_OLD_GROUP_NOTIFICATION                                                    = "fetchOldGroupNotification";
    public static final String                  ARCHIVE_GROUP_NOTIFICATION_DATA                                                 = "archiveGroupNotificationData";
    public static final String                  SHRINK_GROUP_NOTIFICATION_DATA                                                  = "shrinkGroupNotificationData";
    
    
    public static final String                  ARCHIVE_NOTIFICATION_DATA                                                       = "archiveNotificationData";              
               
    public static final String                  ARCHIVE_NOTIFICATION_PARAM_VAL_DATA                                             = "archiveNotificationParamValData";      
    public static final String                  SHRINK_NOTIFICATION_PARAM_VAL_DATA                                              = "shrinkNotificationParamValData";       
    public static final String                  ARCHIVE_RETURN_PAYMENT_NOTIF_HISTORY_DATA                                       = "archiveReturnPaymentNotifHistoryData"; 
    public static final String                  SHRINK_RETURN_PAYMENT_NOTIF_HISTORY_DATA                                        = "shrinkReturnPaymentNotifHistoryData";  
    public static final String                  ARCHIVE_RETURN_PAYMENT_NOTIF_MAPPING_DATA                                       = "archiveReturnPaymentNotifMappingData"; 
    public static final String                  SHRINK_RETURN_PAYMENT_NOTIF_MAPPING_DATA                                        = "shrinkReturnPaymentNotifMappingData";         
    /** [End] R8.4.1 for QC9513   */

    /** Start QC 9438 R 8.2 */
    public static final String                  GET_NEGATIVE_AMENDMENT_COUNT_FOR_FY                                                 = "getNegativeAmendmentCountForFY";
    /** End QC 9438 R 8.2 */
     
	public static final String FETCH_PROC_ID_ORG_ID = "fetchProcIDAndOrgId";
	public static final String SETUP_PROVIDER_PROPOSAL_STATUS = "setupProviderProposalStatus";
 
    
	/** [Start] QC 9388 R 8.4 */
	public static final String 					FETCH_LAST_BASE_APPROVED_FY															= "fetchLastBaseApprovedFY";
	public static final String 					FETCH_BASE_CONTRACT_START_END_DATE 													= "fetchBaseContractStartEndDate";
	public static final String 					FETCH_CONTRACT_BUDGET_APPROVED_STATUS_ID_LIST										= "fetchContractBudgetApprovedStatusId";
	public static final String 					FETCH_BASE_CONTRACT_STATUS_ID														= "fetchBaseContractStatusId";
	public static final String 					NEGATIVE_AMENDMENT_PROHIBITED                                                       = "NEGATIVE_AMENDMENT_PROHIBITED";
	public static final String 					ZERO_AMENDMENT_PROHIBITED															= "ZERO_AMENDMENT_PROHIBITED";
	/** [End] QC 9388 R 8.4 */
 	
	/** [Start] QC 9490 R 8.4 */
	public static final String DELETE_BUDGET_UPDATE_STATUS                                                                          = "deleteBudgetUpdateSuccess";
	public static final String DELETE_BUDGET_UPDATE_RULE                                                                            = "deleteBudgetUpdateRule";
	public static final String COUNT_BUDGET_UPDATE_APPROVED																			= "countBudgetUpdateApproved";
	/** [End] QC 9490 R 8.4 */
	
	/** [Start] R8.4.0 qc_8537 $0 Amendments do not appear in Amendment CoF task list after configuration */
	public static final String FIND_CONTRACT_AMEND_INFO= "findContractAmendInfo";
	public static final String FETCH_CONTRACT_FINANCIALS_MAX_YEAR= "fetchContractFinancialsMaxYear";
	public static final String ADD_NEXT_YEAR_CONTRACT_FINANCIALS_ZERO_DOLLAR_AMD="addNextYearContractFinancialsZeroDollarAmd";
	public static final String ADD_NEXT_YEAR_CONTRACT_FIN_FUNDING_STREAM_ZERO_DOLLAR_AMD="addNextYearContractFinFundingStreamZeroDollarAmd";
	public static final String MSG_INVALID_ZERO_DOLLAR_AMENDMENT_END_DATE="MSG_INVALID_ZERO_DOLLAR_AMENDMENT_END_DATE";
		
	/**[End] R8.4.0 qc_8537 $0 Amendments do not appear in Amendment CoF task list after configuration */
	
	
	/** [Start] R8.4.0 qc9492 XML files for approved amendments and mods */
    public static final String                  BUDGET_XML_REGEN                                                            = "budgetXMLDocRegen";
    public static final String                  FETCH_BUDGET_XML_REGEN                                                      = "getBudgetForXMLRegen";
    public static final String                  UPDATE_BUDGET_XML_INTO_REGEN                                                = "updateBudgetXMLGenWithDocId";
    public static final String                  BUDGET_XML_POSTFIX                                                          = "Regen";
    public static final String                  BUDGET_XML_VERSION_ID                                                          = "versionId";
    
    public static final String CONTRACT_BASE_TYPE_VAL        =  "1";
    public static final String CONTRACT_AMENDMENT_TYPE_VAL   =  "2";
    public static final String CONTRACT_RENEWAL_TYPE_VAL     =  "3";
    public static final String CONTRACT_UPDATE_TYPE_VAL      =  "4";
    public static final String CONTRACT_AWARD_TYPE_VAL       =  "5";
    public static final String CONTRACT_ORIGINAL_TYPE_VAL    =  "6";

	/** [End] R8.4.0 qc9492 XML files for approved amendments and mods */
    
    /** [Start] QC 9401 R 8.5.0  */
    public static final String GET_PROCUREMENT_FOR_EPIN 																	= "getProcurementForEpin";
    public static final String GET_RFP_REPORT_DATA      																	= "getRfpReportData";
    /** [End] QC 9401 R 8.5.0  */ 
    
    // Start R8.6.0 QC_9499 Multi-Tab Browsing letting Invoice and Advance tasks to be Approved Multiple times causing Duplicate Payments
    public static final String DUPLICATE_SUBMISSION = "DUPLICATE_SUBMISSION";
    public static final String GET_TOKEN_FLAG_CONFIG = "getTokenFlagConfig";
    // End R8.6.0 QC_9499 Multi-Tab Browsing letting Invoice and Advance tasks to be Approved Multiple times causing Duplicate Payments


    // Start  R8.8.0 QC9400    
    public static final String CANCEL_AMENDMENT_CHECK_STATUS = "fetchAmendmentContractStatusId";

    public static final Integer  FINALCIAL_PENDING_CONFIGURATION     =    59	  ;
    public static final Integer  FINALCIAL_PENDING_COF               =    60	  ;
    public static final Integer  FINALCIAL_PENDING_REGISTRATION      =    61	  ;
    public static final Integer  FINALCIAL_PENDING_SUBMISSION        =    128   ;
    public static final Integer  FINALCIAL_PENDING_APPROVAL          =    129   ;
    public static final Integer  FINALCIAL_SENT_FOR_REGISTRATION     =    130   ;
    // End    R8.8.0 QC9400    
    
    // Start R.8.8 QC 9145
    public static final String  FETCH_REF_CONTRACT_FMS                = "fetchRefContractFMS";
    public static final String  CLASS_REF_CONTRACT_FMS_BEAN 		  = "com.nyc.hhs.model.RefContractFMSBean";
    public static final String  FETCH_VENDOR_TIN_FMS		 		  = "fetchVendorTinFMS";
    public static final String  FETCH_AGENCY_ID_FMS		 			  = "fetchAgencyIdFMS";
    public static final String  FETCH_EIN_ID			 			  = "fetchEinId";
    public static final String  UPDATE_CONTRACT_WITH_CT_AND_STATUS	  = "updateContractWithCTandStatus";
    public static final String  UPDATE_CONTRACT_WITH_CT_AND_FMSINFO	  = "updateContractWithCTandFMSInfo";
    public static final String  FETCH_DOC_VERSION_AND_COMMODITY_CODE_FMS = "fetchDocVersionAndCommodityCodeFMS";
	
   // End R.8.8 QC 9145
    
    // Start QC 9017 R 8.9
    public static final String FINISH_REVIEW_SCORE_WF	 				= "finishReviewScoreWF";
    // End QC 9017 R 8.9
    
	/* [Start] R8.9.0 QC9531  cleaning Dup user_dn     */
    public static final String FETCH_DUP_BOTH_USER_N_ORG                    = "fetchDupBothUserNOrg";
    public static final String FETCH_DUP_USER_DN_ONLY                       = "fetchDupUserDNOnly";
    public static final String REMOVE_DUP_USER_DN                           = "removeDupUserDn";
    public static final String REPLACE_STAFF_ID_BASE_ORG                    = "replaceStaffIdBaseOrg";
    
	public static final String HHSAUDIT_INSERT_FOR_DUP_USER_DN              = "hhsauditInsertForDupUserDN";
	public static final String HHSAUDIT_PREFIX_FOR_ENTITY_ID                = "UserDN_";
	public static final String HHSAUDIT_DATA_FOR_DUP_USER_DN                = "The USER_DN [_USER_DN_] in the account [_STAFF_ID_] is reset due to Duplicated USER_DN ";
	public static final String HHSAUDIT_EVENT_TYPE_FOR_DUP_USER_DN          = "STAFF_DETAILS";
	public static final String HHSAUDIT_EVENT_NAME_FOR_DUP_USER_DN          = "DUP_USER_DN";
	/* [End] R8.9.0 QC9531  cleaning Dup user_dn     */
	
	// Start QC 9592 R 8.10.0 Remaining Amount in Sub Budgets Created in Update Config Task are NULL when merged with Base 
	public static final String UPDATE_SUB_BUDGET_YTD_INVOICE_AMOUNT_BASE 	= "updateSubBudgetYtdInvoiceAmoutntBase";  
	// End QC 9592 R 8.10.0 Remaining Amount in Sub Budgets Created in Update Config Task are NULL when merged with Base 
    
    
	/* [Start] R8.10.0 QC9399    */
	public static final String FETCH_AMD_BASE_DATE 	= "fetchAMDBaseDate";  
	/* [End] R8.10.0 QC9399    */


    /* [Start] R8.11.1      */
    public static final String                  FETCH_CONTRACT_ID_LIST_N                                                                ="loConLst";
    public static final String                  FETCH_CONTRACT_BUDGET_STATUS_ID_LIST_N                                              = "fetchContractBudgetStatusId_N";
    /* [Start] R8.11.1      */

	/* [Start] R9.0.1 QC9630    */
	public static final String FETCH_NT318_DESCRIPTION = "fetchNT318Description";
	public static final String UPDATE_NOTIFICATION_STATUS_NT318 = "updateNotificationStatusNT318";
	/* [End] R9.0.1 QC9630    */

	/* [Start] R9.2.0 QC9672 & QC9671  */
	
	public static final String FETCH_BASE_CONTRACT_START_END_DATE_DESC =   "fetchContractsForSrtEndDisc";
	
	public static final String UPDATE_BUDGET_START_DATE_FOR_DISCRIFANCY = "updateBudgetStartDateForDiscripancy";
	public static final String UPDATE_BUDGET_END_DATE_FOR_DISCRIFANCY = "updateBudgetEndDateForDiscripancy";
	public static final String UPDATE_CONTRACT_START_END_DATE_FOR_DISCRIFANCY = "updateContractStartEndDateForDiscripancy";
	public static final String UPDATE_CONTRACT_FINANCIALS_FOR_DISCRIFANCY = "updateContractFinalcialsForDiscripancy";

	public static final String CONTRACT_START_FISCAL_YEAR = "contractStartFiscalYear";
	public static final String CONTRACT_END_FISCAL_YEAR = "contractEndFiscalYear";

  	public static final String ADD_CONTRACT_FIN_FOR_DISCRIPANCY       = "addContractFinalcialsForDiscripancy";
	public static final String ADD_CONTRACT_FIN_STREAM_FOR_DISCRIPANCY = "addContractFinStreamForDiscripancy";
	public static final String FETCH_CONTRACT_FINALCIALSFY = "fetchContractFinalcialsFY";
	public static final String NEW_FISCAL_YEAR_ID_FIN = "newFiscalYearId";

	public static final String CCONTRACT_FIN_FUNDING_STREAM_ID = "CONTRACT_FIN_FUNDING_STREAM";
	public static final String CONTRACT_FINANCIALS_ID = "CONTRACT_FINANCIALS";
	/* [End] R9.2.0 QC9672 & QC9671   */

	/* [Start] R9.3.2 QC9665   */

	//public static final String CONTRACT_FINANCIALS_ID = "CONTRACT_FINANCIALS";
	/* [End] R9.3.2 QC9665   */

    //[Start] R9.4.0 QC8522
	public static final String CBY_FETCH_NON_GRID_CONTRACTED_SERVICES_AMND = "fetchNonGridContractedServices_AMND";
    //[End] R9.4.0 QC8522

	//* Start QC 9680 R 9.5  EXT QC9654 - Amendment Config Muliti-Tab Browsing Missing Extra Services and Cost Centers   */
	public static final String FETCH_CONTRACT_STATUS_ID	= "fetchContractStatusId";
	public static final String BMC_TASK_NAME	        = "BMC_TASK_NAME";
    //* End QC 9680 R 9.5  EXT QC9654 - Amendment Config Muliti-Tab Browsing Missing Extra Services and Cost Centers   */
	/*[Start] R9.5.0 QC9615 	  **/
	public static final String APT_PROCUREMENT_DESC_LENGTH	= "APT_PROCUREMENT_DESC_LENGTH";
	public static final int    APT_PROCUREMENT_DESC_MAX_LENGTH	= 120;
	public static final String APT_PROCUREMENT_DESC_AMEDMENT	= "procDescription";
	/*[End] R9.5.0 QC9615 	  **/
	
    /*--   [Start] QC9681 R9.5   Contract Update Config Muliti-Tab Browsing   -*/
	public static final String CONTRACT_UPDATE_BUDGET_STATUS =   "ContractUpdateBudgetStatus";
	public static final String CONTRACT_UPDATE_BUDGET_STATUS_MAP =   "loStatusMap";
	public static final String CONTRACT_UPDATE_BUDGET_STATUS_ID =   "BUDGET_STATUS_ID";
	public static final String CONTRACT_UPDATE_STATUS_ID =   "CONTRACT_STATUS_ID";

	public static final String FETCH_CONTRACT_UPDATE_BUDGET_STATUS ="fetchContractUpdateBudgetStatus";
    /*--   [End] QC9681 R9.5   Contract Update Config Muliti-Tab Browsing   -*/
	
	//[Start] R9.6.0 QC9663 System Allowing Duplicate Records in DEFAULT_ASSIGNMENT Table - Task Details Not Appearing as a Result 
	public static final String ASSIGN_MULTI_TASK_FILENET = "reassignMultiAgencyTaskFilenet";
	//[End] R9.6.0 QC9663 System Allowing Duplicate Records in DEFAULT_ASSIGNMENT Table - Task Details Not Appearing as a Result

    /*--   [Start] QC9605 R9.6      -*/
	public static final String CANCEL_BUDGET_MODIFICATION = "cancelBudgetMod";
    /*--   [End]   QC9605 R9.6      -*/

    /*--   [Start] QC9701       -*/
	public static final String AS_ACTION_MENU_MANAGE = "actionMenuSetDisable";
	public static final String AO_ACTION_MENU_UPDATE_STATUS = "updateActionStatus";
	public static final String AO_ACTION_MENU_UPDATE_ROW_CNT = "updatedRowCount";
	public static final String AO_ACTION_MENU_VALUE = "aoActionStat";
	public static final String AO_ACTION_SHUTDOWN_STATUS = "actionShutdownStatus";
	
    public static final String                  GET_ACTION_MENU_CTRL                                                                   = "ActioniMenuCtrl_ActioniMenuCtrl";
    /*--   [End] QC9701      -*/
    
    //<!-- [Start] R9.7.5 QC9719 -->
    public static final String                  GET_ACTION_ENABLED                                                                  = "action_enable";
    //<!-- [End] R9.7.5 QC9719 -->

    
    //<!-- [Start] R9.7.7 QC9736 -->
    public static final String                  GET_ORG_ACCOUNT_CTRL                                                                   = "ActionOrganizationAccount_Account_Request";
    /*--   [End] QC9736      -*/
    
    /*--   [Start] QC9744      -*/
    public static final String                  GET_DOC_VAULT_MENU_CITY_CTRL                                                            = "DocVaultMenuCity_DocVaultMenuCity";
    public static final String                  GET_DOC_VAULT_MENU_PROVIDER_CTRL                                                        = "DocVaultMenuProvider_DocVaultMenuProvider";
    /*--   [End] QC9744      -*/
    
    
}

