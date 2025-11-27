package com.nyc.hhs.constants;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Common Constant class for the FileNet component classes
 */
public class HHSP8Constants
{
	public static final String TRANSACTION_P8CONFIG = "com/nyc/hhs/config/TransactionConfig.xml";
	public static final String EVENT_TYPE_TEMPLATE = "/com/nyc/hhs/config/EventTypeTemplate.xml";
	public static final String MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER = "com.nyc.hhs.service.db.services.application.HHSComponentP8Mapper";

	public static final String PROPERTY_PE_PROPOSAL_ID = "ProposalID";
	public static final String PROPERTY_PE_PROPOSAL_TITLE = "ProposalTitle";
	public static final String PROPERTY_PE_PROPOSAL_PROVIDERNAME = "ProviderName";
	public static final String PROPERTY_PE_PROPOSAL_ORGANIZATIONID = "ProviderID";
	public static final String BUDGET_APPROVED_STATUS = "86";
	public static final String FIND_CONTRACT_DETAILS_WF = "findContractDetailsByContractIdForWF";
	public static final String PROCUREMENT_ID_KEY = "asProcurementId";
	public static final String PROCUREMENT_ID_WF = "ProcurementID:";
	public static final String LOHMAP = "loHMap";
	public static final String PROPOSAL_ID_KEY = "asProposalIds";
	public static final String EVALUATION_STATUS_ID = "evaluationStatusId";
	public static final String EVALUATION_STATUS_ID_KEY = "EvaluationStatusId";
	public static final String EVALUATION_STATUS_BEAN = "evaluationStatusBean";
	public static final String EVALUATION_SETTINGS_INTERNAL_EXTERNAL_PROPERTY = "asEvalSettingsIntId";
	public static final String PROPOSAL_ID_LIST = "aoProposalIdList";
	public static final String CONTRACT_ID_LIST = "aoContractIdList";
	public static final String EVALUATION_ID_LIST = "aoEvalIdList";
	public static final String TASK_PROPS_MAP = "loTaskRequiredProps";
	public static final String TRANSACTION_LOWESCASE = "transaction";
	public static final String NOTIFICATION_CONTENT = "notificationContent";
	public static final String TASKVISIBILITY = "taskVisibility";
	public static final String PROPOSAL_COUNT = "aoProposalCount";
	public static final String PROC_REVIEW_STATUS_ID = "asProcReviewStatusId";
	public static final String FINANCIAL_BEAN = "aoFinancialWFBean";
	public static final String REVIEW_LEVEL = "aiReviewLevels";
	public static final String ENTITY_TYPE = "entityType";
	public static final String ENTITY_ID = "entityId";
	public static final String PROPERTY_PE_TASK_TYPE = "TaskType";
	public static final String TASK_CONTRACT_COF = "Contract Certification of Funds";
	public static final String TASK_AMENDMENT_COF = "Amendment Certification of Funds";
	public static final String PROPERTY_PE_REVIEWLEVEL = "ReviewLevel";
	public static final String TASK_INVOICE_REVIEW = "Invoice Review";
	public static final String TASK_ADVANCE_PAYMENT_REVIEW = "Advance Payment Review";
	public static final String TASK_ADVANCE_PAYMENT_REQUEST = "Advance Payment Request";
	public static final String TASK_PAYMENT_REVIEW = "Payment Review";
	public static final String CLASS = "class";
	public static final String ACCELERATOR_COMMENTS = "asAccComments";
	public static final String AGENCY_COMMENTS = "asAgencyComments";
	public static final String EVALUATOR_IDS = "evaluatorIds";
	public static final String PROVIDER_IDS = "providerIds";
	public static final String PROVIDER_BEANS = "providerBeanList";
	public static final String NOT_SELECTED = "NOT SELECTED";
	public static final String SELECTED = "SELECTED";
	public static final String APPROVED = "Approved";
	public static final String OVERRIDE = "Override";
	public static final String PROVIDER = "provider";
	public static final String EMPTY_STRING = "";
	public static final String WF307 = "WF307";
	public static final String WF305 = "WF305";
	public static final String WORKFLOW = "workflow";
	public static final String LAUNCH_BY = "LaunchBy";
	public static final String LAUNCH_DATE = "LaunchDate";

	public static final String ADD_OPERATOR = "N";
	public static final String DELETE_OPERATOR = "D";

	public static final String INTERNAL_EXTERNAL_FLAG = "both";
	public static final String INTERNAL_FLAG = "I";
	public static final String EXTERNAL_FLAG = "E";
	public static final String INT_EXT_FLAG = "intExtFlag";
	public static final String EVALUATOR_FLAG = "evalFlag";

	public static final String AGENCY_ID = "AgencyID";
	public static final String REVIEW_PROCESS_ID = "aiReviewProcId";
	public static final String AGENCY_ID_KEY = "asAgencyId";
	public static final String PROCUREMENT_EPIN = "ProcurementEPin";
	public static final String PROCUREMENT_ID = "procurementId";
	public static final String PROCUREMENT_TITLE = "ProcurementTitle";
	public static final String PROCUREMENT_TITLE_WF = "PROCUREMENT_TITLE:";
	public static final String AWARD_EPIN = "awardEpinId";
	public static final String TASK_DETAILS_BEAN = "aoTaskDetailsBean";
	public static final String CONTRACT_BUDGET_STATUS = "aoBudgetStatus";
	public static final String APPROVED_BUDGET_COUNT = "aiRowCount";
	public static final String PROPOSAL_ID = "ProposalID";
	public static final String PROPOSAL_TITLE = "ProposalTitle";
	public static final String PROVIDER_ID = "ProviderID";
	public static final String PROVIDER_NAME = "ProviderName";
	public static final String INT_EXT_EVALUATOR_ID = "intExtEvaluatorId";
	public static final String UNASSIGNED_LEVEL1 = "Unassigned Level 1";
	public static final String UNASSIGNED_LEVEL2 = "Unassigned Level 2";
	public static final String UNASSIGNED_LEVEL = "Unassigned Level ";
	public static final String SPLIT_IDENTIFIER = "#";

	public static final String CONTRACT_ID = "ContractID";
	public static final String CONTRACT_TITLE = "contractTitle";
	public static final String CONTRACT_NUMBER = "contractNumberName";
	public static final String CONTRACT_NUMBER_ID = "contractNumber";
	public static final String CONTRACT_CONFIGURATOIN_ID = "contractConfigurationId";
	public static final String FIRST_ROUND = "isFirstRound";
	public static final String EVALUATION_ID = "EvaluationID";
	public static final String AUDIENCE_TYPE_PROVIDER = "PROVIDER";
	public static final String AUDIENCE_TYPE_AGENCY = "AGENCY";
	public static final String AUDIENCE_TYPE_ACCELERATOR = "ACCELERATOR";
	public static final String LINKED_WOB_NO = "LinkedWobNo";
	public static final String INVOICE_ID = "invoiceId";
	public static final String BUDGET_ADVANCE_ID = "budgetAdvanceId";
	public static final String INVOICE_NUMBER = "invoiceNumber";
	public static final String ADVANCE_NUMBER = "advanceNumber";
	public static final String NUMBER_OF_ASSIGNMENTS = "numberOfAssignments";
	public static final String ASSIGNMENT_LIST = "asAssignmentList";
	public static final String VENDOR_ID = "vend_cust_cd";
	public static final String AMOUNT = "amount";
	public static final String INVOICE_PAYMENT_VOUCHER_IDENTIFIER = "P";
	public static final String ADVANCE_PAYMENT_VOUCHER_IDENTIFIER = "A";
	public static final String ONE = "1";
	public static final String ZERO = "0";

	public static final String TASK_STATUS = "TaskStatus";
	public static final String TASK_STATUS_FINISHED = "Finished";
	public static final String TASK_STATUS_INREVIEW = "In Review";
	public static final String TASK_STATUS_RETURNEDFORREVISION = "Returned for Revision";
	public static final String TASK_OWNER = "TaskOwner";
	public static final String TASK_OWNER_NAME = "TaskOwnerName";

	public static final String BUDGET_ID = "budgetId";
	public static final String ORGANIZATION_ID = "organizationId";
	public static final String PREV_STATUS_ID = "prevStatusId";
	public static final String CREATED_BY_USERID = "createdByUserId";
	public static final String MODIFIED_BY_USERID = "modifiedByUserId";
	public static final String PERIOD = "period";
	public static final String PROCESS_FLAG = "processFlag";
	public static final String STATUS_ID = "statusId";
	public static final String FISCAL_YEAR = "fiscalYearId";
	public static final String PAYMENT_AMOUNT = "paymentAmount";
	public static final String DELETE_FLAG = "deleteFlag";
	public static final String CT_NUMBER = "ctNumber";
	public static final String PAYMENT_ID = "paymentId";
	public static final String PAYMENT_ID_ARRAY = "paymentIds";
	public static final String FISCAL_YEAR_ID = "asFiscalYearId";
	public static final String TASK_MODIFIED_DATE = "TaskModifiedDate";
	public static final String PAYMENT_VOUCHER_NUMBER = "paymentVoucherNumber";
	public static final String STEPNAME_N_HOUR_DELAY = "n hour delay";
	public static final String STEPNAME_CONFIGURE_AWARD = "HHSA_ConfigureAwardWorkQueue (S247)";
	public static final String EVAL_REASSIGN_DETAILS = "evaluationReassignDetailsBeanList";

	public static final String ROSTER_NAME = "DefaultRoster";
	public static final String HHS_ACCELERATOR_PROCESS_QUEUE = "HHSAcceleratorProcessQueue";
	public static final String DEFAULT_SYSTEM_USER = "system";

	public static final String PROPERTY_NAME_CE_USER_ID = "CE_USER_ID";
	public static final String PROPERTY_NAME_CE_PASSWORD = "CE_PASSWORD";
	public static final String PROPERTY_NAME_FILENET_URI = "FILENET_URI";
	public static final String PROPERTY_NAME_JAAS_STANZA = "FileNetP8WSI";
	public static final String PROPERTY_NAME_CONNECTION_POINT_NAME = "CONNECTION_POINT_NAME";

	public static final String WORKFLOW_NAME_201_ACCEPT_PROPOSAL = "WF201 - Accept Proposal";
	public static final String WORKFLOW_NAME_202_REVIEW_SCORES = "WF202 - Review Scores";
	public static final String WORKFLOW_NAME_202_EVALUATION = "WF202 - Evaluate Proposal";
	public static final String WORKFLOW_NAME_201_ACCEPT_PROPOSAL_MAIN = "WF201 - Start Accept Proposal Workflows";
	public static final String WORKFLOW_NAME_202_EVALUATE_PROPOSAL_MAIN = "WF202 - Evaluate Proposal Main";
	public static final String WORKFLOW_NAME_203_AWARD = "WF203 - Award";
	public static final String WORKFLOW_NAME_301_PROCUREMENT_CERTIFICATION_FUNDS = "WF301 - Procurement CoF";
	public static final String WORKFLOW_NAME_302_CONTRACT_CONFIGURATION = "WF302 - Contract Configuration";
	public static final String WORKFLOW_NAME_303_CONTRACT_CERTIFICATION_FUNDS = "WF303 - Contract Certification of Funds";
	public static final String WORKFLOW_NAME_314_AMENDMENT_CERTIFICATION_FUNDS = "WF314 - Amendment Certification of Funds";
	public static final String WORKFLOW_NAME_304_CONTRACT_BUDGET = "WF304 - Contract Budget";
	public static final String WORKFLOW_NAME_306_PAYMENT_REVIEW = "WF306 - Payment Review";

	public static final String WF_PROP_EVALUATION_ID = "EvaluationID";
	public static final String WF_PROP_EVALUATION_STATUS_ID = "EvaluationStatusId";

	public static final String FETCH_PROPOSAL_IDS_PROCUREMENT_DB = "fetchProposalsIdsByProcurement_DB";
	public static final String FETCH_REVIEW_LEVELS = "fetchReviewLevels";
	public static final String FETCH_VENDORS = "fetchVendors";

	public static final String FETCH_EVALUATOR_ID_EVALUATION = "fetchEvaluatorIdsEvaluation";
	public static final String FETCH_EVALUATOR_ID_REVIEW_SCORE = "fetchEvaluatorIdsReviewScore";
	public static final String FETCH_PROCUREMENT_SUMMARY_TRANSACTION = "fetchProcurementSummary";
	public static final String FETCH_EVALUATION_STATUS_ID = "fetchEvaluationStatusID";
	public static final String FETCH_EVALUATION_STATUS_ID_RETURNED = "fetchEvaluationStatusIdReturned";
	public static final String GET_PROCUREMENT_SUMMARY_DATA = "ProcurementSummary";
	public static final String FETCH_MULTIPLE_PROPOSAL_DETAILS_TRANSACTION = "fetchMultipleProposalsDetails";
	public static final String FETCH_REQUIRED_PROPOSAL_DETAILS = "fetchRequiredProposalsDetails";
	public static final String GET_PROPOSAL_DETAILS_BEAN_LIST = "proposalDetailsBeanList";
	public static final String FETCH_MULTIPLE_PROPOSAL_DETAILS = "fetchMultipleProposalsDetailsByProposal";
	public static final String FETCH_CONTRACT_ID = "fetchContractIdByProcurement";
	public static final String FETCH_SELECTED_PROPOSALS_COUNT = "fetchCountofSelectedProposals";
	public static final String FETCH_REOPENED_EVALUATION_TASK_IDS = "fetchReopenedEvaluationTaskIds";
	public static final String CANCEL_EVALUATION_TASKS = "cancelEvaluationTask";
	public static final String CANCEL_PROCUREMENT = "cancelProcurements";
	public static final String CANCEL_AWARD = "cancelAwards";
	public static final String SUSPEND_ALL_FINANCIAL_TASKS = "suspendAllFinancialTasks";
	public static final String UNSUSPEND_ALL_FINANCIAL_TASKS = "unSuspendALLFinancialTasks";
	public static final String UPDATE_MODIFIED_FLAG = "updateModifiedFlag";
	public static final String FETCH_ASSIGNMENTS = "fetchAssignments";
	public static final String UPDATE_PROCESS_FLAG_ON_PPAYMENT = "updateProcessFlagOnPayment";
	public static final String FETCH_BUDGET_ID = "fetchBudgetId";
	public static final String FETCH_INVOICE_NUMBER = "fetchInvoiceNumber";
	public static final String FETCH_ADVANCE_NUMBER = "fetchAdvanceNumber";
	public static final String FETCH_AMOUNT = "fetchAmount";
	public static final String FETCH_AMOUNT_WF305 = "fetchAmountforInvoiceWF";
	public static final String FETCH_AMOUNT_WF307 = "fetchAmountforAdvanceWF";
	public static final String CANCEL_FLAG = "CancelFlag";
	public static final String FETCH_EVAL_STATUS_IDS = "fetchEvaluationStatusIdsByEvaluatorDetails";
	public static final String UPDATE_MULTIPLE_CONTRACTS_TRANSACTION = "updateMultipleContractsDetails";
	public static final String FETCH_EVAL_REASSIGN__DETAILS = "fetchEvaluationReassignDetails";

	public static final String DELETE_EVALUATOR_EXTERNAL = "deleteEvaluatorExternal";
	public static final String DELETE_EVALUATOR_INTERNAL = "deleteEvaluatorInternal";
	public static final String UPDATE_EVALUATOR_EXTERNAL = "updateEvaluatorExternal";
	public static final String UPDATE_EVALUATOR_INTERNAL = "updateEvaluatorInternal";
	public static final String INSERT_INTO_PAYMENT = "insertIntoPayment";
	public static final String INSERT_INTO_PAYMENT_ALLOCATION = "insertIntoPaymentAllocation";

	public static final String DELETE_EVALUATION_STATUS_TRANSACTION = "deleteEvaluationStatus";
	public static final String DELETE_EVALUATION_SCORE_TRANSACTION = "deleteEvaluationScore";
	public static final String FETCH_EVAL_REASSIGN_INT_DETAILS = "fetchEvaluationReassignInternalDetails";
	public static final String FETCH_EVAL_REASSIGN_EXT_DETAILS = "fetchEvaluationReassignExternalDetails";
	public static final String EVAL_STAT_IDS_BY_INT_EVAL_DETAILS = "EvaluationStatusIdsByInternalEvaluatorDetails";
	public static final String EVAL_STAT_IDS_BY_EXT_EVAL_DETAILS = "EvaluationStatusIdsByExternalEvaluatorDetails";
	public static final String FETCH_EVAL_REASSIGN_DETAILS_TRANSACTION = "fetchEvaluationReassignDetails";
	public static final String EVAL_REASSIGN_INT_DETAILS = "evaluationReassignInternalDetailsBeanList";
	public static final String EVAL_REASSING_EXT_DETAILS = "evaluationReassignExternalDetailsBeanList";
	public static final String FETCH_PROPOSAL_CONFIG_DETAILS_TRANSACTION = "fetchProposalConfigDetails";
	public static final String EVAL_STATUS_BEANS = "evaluationStatusBeans";
	public static final String INSERT_EVAL_STATUS_TRANSACTION = "insertEvaluationStatus";
	public static final String FETCH_MULTIPLE_CONTRACTS_TRANSACTION = "fetchMultipleContractDetails";
	public static final String CONTRACT_DETAILS_BEAN_LIST = "contractDetailsBeanList";
	public static final String FETCH_EVAL_STATUS_IDS_BY_INT = "fetchEvaluationStatusIdsByInternalEvaluatorDetails";
	public static final String FETCH_EVAL_STATUS_IDS_BY_EXT = "fetchEvaluationStatusIdsByExternalEvaluatorDetails";
	public static final String FETCH_FISCAL_YEAR_ID = "fetchFisalYearId";
	public static final String EVALUATION_IDS = "evaluationIds";
	public static final String FETCH_REQUIRED_INFORMATION = "fetchRequiredInformation";

	public static final String FINALIZE_EVALUATION_DATE = "FinalizeEvaluationDate";
	public static final String AWARD_SELECTION_DATE = "AwardSelectionDate";

	public static final String FETCH_PROPOSAL_ID_LIST = "fetchProposalIdList";
	public static final String FETCH_PROCUREMENT_SUMMARY_PROVIDER = "fetchProcurementSummaryProvider";
	public static final String FETCH_PROPOSAL_DETAILS = "fetchProposalDetails";
	public static final String FETCH_INTERNAL_EVALUATIONS_LIST = "fetchInternalEvaluationsList";
	public static final String FETCH_EXTERNAL_EVALUAITIONS_LIST = "fetchExternalEvaluationsList";
	public static final String FETCH_EVAL_ID_INTERNAL = "fetchEvalIdInternal";
	public static final String FETCH_EVAL_ID_EXTERNAL = "fetchEvalIdExternal";
	public static final String FETCH_ACCEPTED_PROPOSAL_ID = "fetchAcceptedProposalID";
	public static final String INSERT_AUDIT_DETAILS = "insertAuditDetails";
	public static final String FETCH_ACCELERATOR_COMMENTS = "fetchAcceleratorComments";
	public static final String FETCH_AGENCY_COMMENTS = "fetchAgencyComments";

	public static final String JAVA_UTIL_MAP = "java.util.Map";
	public static final String JAVA_LANG_STRING = "java.lang.String";
	public static final String EVALUATION_STATUS_BEAN_CLASS = "com.nyc.hhs.model.EvaluationStatusBean";

	public static final String EVALUATE_PROPOSAL_TASK_STATUS_IN_REVIEW = "41";
	public static final String EVALUATE_PROPOSAL_TASK_SCORES_RETURNED = "42";

	public static final String DEFAULT_DELIMITER = ";";

	public static final String TRANSACTION_P8_XML = "/com/nyc/hhs/config/TransactionConfigP8Component.xml";

	public static final String TRANSACTION_PARAM_COMPONENT_AUDITSTATUS = "auditStatus";
	public static final String TRANSACTION_ID_INSERT_NOTIFICATION = "insertNotificationDetail";
	public static final String TRANSACTION_ID_INSERT_ITEMS_CONTRACT_CONFIG = "insertLineItemsForContractConfigTasks";
	public static final String TRANSACTION_ID_CREATE_REPLICA_BUDGET_REVIEW_TASKS = "createReplicaForBudgetReviewTasks";
	public static final String TRANSACTION_ID_MERGE_BUDGET_MODIFICATION_REVIEW_TASKS = "mergeBudgetForModificationReviewTasks";
	public static final String TRANSACTION_ID_MERGE_BUDGET_UPDATE_REVIEW_TASKS = "mergeBudgetForUpdateReviewTasks";
	public static final String TRANSACTION_ID_MERGE_BUDGET_AMENDMENT_REVIEW_TASKS = "mergeBudgetForAmendmentReviewTasks";
	public static final String TRANSACTION_ID_INSERT_OLD_SUB_BUDGET_LINE_ITEMS_ZERO_COPY = "insertOldSubBudgetLineItemsZeroCopy";

	public static final String COMPONENT_OPERATIONS_PARAM_ACTIONFLAG_NEW = "NEW";
	public static final String COMPONENT_OPERATIONS_PARAM_ACTIONFLAG_RETURNED = "RETURNED";
	public static final String COMPONENT_OPERATIONS_PARAM_ACTIONFLAG_COMPLETED = "COMPLETED";
	public static final String TASK_STATUS_COMPLETED = "Completed";
	public static final String DELETE_EVALUATOR_DATA = "deleteEvaluatorData";

	// P8Constants

	public static final String PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG = "java.security.auth.login.config";
	public static final String PROPERTY_FILE = "com.nyc.hhs.properties.hhsservices";
	public static final String PROPERTY_PREDEFINED_LOG4J_PATH = "PROP_FILE_LOG4J_PATH";
	public static final String PROPERTY_PREDEFINED_LOG4J_COMPONENT_PATH = "PROP_FILE_LOG4J_COMPONENT_PATH";
	public static final String PE_WORKFLOW_PROPOSAL_ID = "ProposalID";
	public static final String PE_WORKFLOW_PROCUREMENT_ID = "ProcurementID";
	public static final String HSS_QUEUE_NAME = "HHSAcceleratorProcessQueue";
	public static final String PE_WORKFLOW_TASK_STATUS = "TaskStatus";
	public static final String PROP_FILE_JAVA_NAMING_FACTORY_INITIAL = "java.naming.factory.initial";
	public static final String PROP_FILE_FILENET_PE_BOOTSTRAP_CEURI = "filenet.pe.bootstrap.ceuri";
	public static final String PROPOSAL_ID_WF_KEY = "asProposalId";
	public static final String PROPERTY_PE_BOOTSTRAP_CE_URI = "filenet.pe.bootstrap.ceuri";
	public static final String PE_WORKFLOW_FIRST_ROUND_EVAL_DATE = "FirstRoundOfEvaluationCompleteDate";

	// HHSConstants

	public static final String PROPERTY_PE_PROCURMENT_ID = "ProcurementID";
	public static final String PROPERTY_PE_PROCUREMENT_TITLE = "ProcurementTitle";
	public static final String PROPERTY_PE_PROVIDER_ID = "ProviderID";
	public static final String PROPERTY_PE_PROVIDER_NAME = "ProviderName";
	public static final String PROPERTY_PE_PROGRAM_NAME = "ProgramName";
	public static final String PROPERTY_PE_AWARD_EPIN = "AwardEPin";
	public static final String PROPERTY_PE_CT = "ContractNumber";
	public static final String PROPERTY_PE_SUBMITTED_BY = "LaunchBy";
	public static final String PROPERTY_PE_AGENCY_ID = "AgencyID";
	public static final String PROPERTY_PE_CONTRACT_ID = "ContractID";
	public static final String PROPERTY_PE_INVOICE_ID = "InvoiceNumber";
	public static final String PROPERTY_PE_BUDGET_ID = "BudgetID";
	public static final String PROPERTY_PE_ADVANCE_NUMBER = "BudgetAdvanceID";
	public static final String PROPERTY_PE_NEW_FISCAL_YEAR_ID = "NewFiscalYearId";
	public static final String PROPERTY_PE_PROCUREMENT_EPIN = "ProcurementEPin";
	public static final String FETCH_INTERNAL_EVALUATOR_USERS = "fetchInternalEvaluatorUsers";
	public static final String INTERNAL_EVALUATOR_LIST = "internalEvaluatorList";
	public static final String FETCH_EXTERNAL_EVALUATOR_USERS = "fetchExternalEvaluatorUsers";
	public static final String EXTERNAL_EVALUATOR_LIST = "externalEvaluatorList";
	public static final String CONTRACT_ID_WORKFLOW = "contractId";
	public static final String AGENCY_AUDIT = "agencyAudit";

	public static final String PROPOSAL_SUMMARY_URL = "/portal/hhsweb.portal?_nfpb=true&_windowLabel=portletInstance_38&_urlType=action&topLevelFromRequest=ProposalSummary&render_action=proposalSummary&forAction=propEval&submit_action=navigationAction&_pageLabel=portlet_hhsweb_portal_page_procurement&procurementId=";
	public static final String AGENCY_TASK_INBOX_URL = "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox";
	public static final String SELECTION_DETAILS_URL = "/portal/hhsweb.portal?_nfpb=true&_windowLabel=portletInstance_38&_urlType=action&topLevelFromRequest=SelectionDetails&render_action=viewSelectionDetails&forAction=selectionDetail&submit_action=navigationAction&_pageLabel=portlet_hhsweb_portal_page_procurement&procurementId=";
	public static final String SELECTION_DETAILS_BUDGET_LIST_URL = "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=budgetListAction";
	public static final String CONTRACT_BUDGET_REVISIONS_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_contract_budget_page&budgetType=Contract%20Budget";
	public static final String INVOICE_REVISIONS_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_contract_invoice_page&invoiceId=";
	public static final String ADVANCE_REJECTED_URL = "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_st=&_windowLabel=portletInstance_37&_urlType=render&wlpportletInstance_37_action=budgetListAction";
	public static final String COMPLETE_CONTRACT_BUDGET_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_contract_budget_page&budgetType=Contract%20Budget";
	public static final String CONTRACT_BUDGET_ACTIVE_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_contract_budget_page&budgetType=Contract%20Budget";
	public static final String COMPLETE_CONTRACT_BUDGET_UPDATE_URL = "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_st=&_windowLabel=portletInstance_37&_urlType=render&wlpportletInstance_37_action=budgetListAction";
	public static final String CONTRACT_BUDGET_UPDATE_REVISIONS_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_page_budget_modification&budgetType=Budget%20Update";
	public static final String CONTRACT_BUDGET_MODIFICATION_APPROVED_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_contract_budget_page&budgetType=Contract%20Budget";
	public static final String CONTRACT_BUDGET_MODIFICATION_REVISION_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_page_budget_modification&budgetType=Budget%20Modification";
	public static final String COMPLETE_CONTRACT_BUDGET_AMENDMENT_URL = "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_st=&_windowLabel=portletInstance_37&_urlType=render&wlpportletInstance_37_action=budgetListAction";
	public static final String CONTRACT_BUDGET_AMENDMENT_REVISION_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_page_budget_amendment&budgetType=Budget%20Amendment";
	public static final String CONTRACT_BUDGET_AMENDMENT_APPROVED_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_page_budget_amendment&budgetType=Budget%20Amendment";
	public static final String ADVANCE_APPROVED_URL = "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_contract_budget_page&budgetType=Contract%20Budget";
	public static final String BUDGET_LIST_URL = "/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_st=&_windowLabel=portletInstance_37&_urlType=render&wlpportletInstance_37_action=budgetListAction";

	// R3 WF Ids
	public static final String WF_PROCUREMENT_CERTIFICATION_FUND = "WF301 - Procurement CoF";
	public static final String WF_CONTRACT_CONFIGURATION_UPDATE = "WF310 - Contract Configuration Update";
	public static final String WF_CONTRACT_CONFIGURATION = "WF302 - Contract Configuration";
	public static final String WF_CONTRACT_CERTIFICATION_FUND = "WF303 - Contract Certification of Funds";
	public static final String WF_ADVANCE_REVIEW = "WF307 - Advance Payment Request";
	public static final String WF_NEW_FY_CONFIGURATION = "WF309 - New Fiscal Year Configuration";
	public static final String WF_AMENDMENT_CONFIGURATION = "WF313 - Amendment Configuration";
	public static final String WF_CONTRACT_BUDGET_REVIEW = "WF304 - Contract Budget";
	public static final String WF_CONTRACT_BUDGET_UPDATE_REVIEW = "WF311 - Contract Budget Update Review";
	public static final String WF_CONTRACT_BUDGET_MODIFICATION = "WF312 - Contract Budget Modification (MOD)";
	public static final String WF_CONTRACT_BUDGET_AMENDMENT = "WF315 - Contract Budget Amendment (AMD)";
	public static final String WF_INVOICE_REVIEW = "WF305 - Invoice Review";
	public static final String WF_PAYMENT_REVIEW = "WF306 - Payment Review";
	public static final String WF_ADVANCE_PAYMENT_REVIEW = "WF308 - Advance Payment Review";
	public static final String WF_AMENDMENT_CERTIFICATION_FUND = "WF314 - Amendment Certification of Funds";
	public static final String WF_AWARD_TASK = "WF203 - Configure Award Documents";
	public static final String WF_FINANCIAL_UTILITY = "WF30x - Utility Workflow";

	public static final Map<String, Integer> FINANCIAL_WF_ID_MAP = new HashMap<String, Integer>();
	
	//Start || Changes done for enhancement 6574 for Release 3.10.0
	public static final String STEPNAME_APPROVE_AWARD_QUEUE = "HHSA_ApproveAwardWorkQueue (S244)";
	//End || Changes done for enhancement 6574 for Release 3.10.0

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
		FINANCIAL_WF_ID_MAP.put(WORKFLOW_NAME_303_CONTRACT_CERTIFICATION_FUNDS, 8);
		FINANCIAL_WF_ID_MAP.put(WORKFLOW_NAME_314_AMENDMENT_CERTIFICATION_FUNDS, 3);
		FINANCIAL_WF_ID_MAP.put(WORKFLOW_NAME_306_PAYMENT_REVIEW, 14);
	}

	public static final String PROPERTY_PE_TASK_TOTAL_LEVEL = "ReviewLevel";
	public static final String AUDIT_BEAN = "auditBean";
	public static final String LINK1 = "LINK1";
	public static final String LINK2 = "LINK2";
	public static final String LINK = "LINK";
	public static final String LO_HM_NOTIFY_PARAM = "loHmNotifyParam";
	public static final int INT_ZERO = 0;
	public static final String PROPERTY_PE_ASSIGNED_TO = "TaskOwner";
	public static final String F_WOB_NUM = "F_WobNum";
	public static final String CURR_LEVEL = "CurrentLevel";
	public static final String TASK_UNASSIGNED = "Unassigned Level ";
	public static final String PROPERTY_PE_ASSIGNED_TO_NAME = "TaskOwnerName";
	public static final String SCORES_RETURNED = "Scores Returned";
	public static final String AO_FILENET_SESSION = "aoFilenetSession";
	public static final String AO_HMWF_REQUIRED_PROPS = "aoHMWFRequiredProps";

	// Application Constants

	public static final String AUDIT_TASK_INTERNAL_COMMENTS = "Internal Comments";

	// Transaction Constants
	public static final String EVENT_ID_PARAMETER_NAME = "eventid";
	public static final String REQUEST_MAP_PARAMETER_NAME = "request_map";
	public static final String PROVIDER_ID_KEY = "providerId";
	public static final String AGENCY_ID_CONSTANT = "agencyId";
	public static final String ACCELERATOR_ID = "acceleratorId";
	public static final String USER_ID = "userId";
	public static final String NOTIFICATION_NAME = "notificationName";

	public static final String NT222 = "NT222";
	public static final String NT203 = "NT203";
	public static final String AL221 = "AL221";
	public static final String AL203 = "AL203";
	public static final String NT207 = "NT207";
	public static final String AL212 = "AL212";
	public static final String NT214 = "NT214";
	public static final String AL219 = "AL219";
	public static final String AL218 = "AL218";
	public static final String NT221 = "NT221";
	public static final String AL217 = "AL217";
	public static final String NT213 = "NT213";
	public static final String NT208 = "NT208";
	public static final String AL213 = "AL213";
	public static final String NT220 = "NT220";
	public static final String NT224 = "NT224";
	public static final String AL223 = "AL223";
	public static final String AL222 = "AL222";
	public static final String NT223 = "NT223";
	public static final String NT219 = "NT219";
	public static final String AL220 = "AL220";
	public static final String AL301 = "AL301";
	public static final String AL302 = "AL302";
	public static final String AL303 = "AL303";
	public static final String AL304 = "AL304";
	public static final String AL305 = "AL305";
	public static final String NT305A = "NT305a";
	public static final String NT305B = "NT305b";
	public static final String AL306 = "AL306";
	public static final String AL307 = "AL307";
	public static final String AL308 = "AL308";
	public static final String AL309 = "AL309";
	public static final String AL310 = "AL310";
	public static final String AL311 = "AL311";
	public static final String AL312 = "AL312";
	public static final String AL313 = "AL313";
	public static final String AL314 = "AL314";
	public static final String AL315 = "AL315";
	public static final String NT301 = "NT301";
	public static final String NT303 = "NT303";
	public static final String NT305 = "NT305";
	public static final String NT315 = "NT315";
	public static final String NT315A = "NT315a";
	public static final String NT315B = "NT315b";
	public static final String NT306 = "NT306";
	public static final String NT302 = "NT302";
	public static final String NT307 = "NT307";
	public static final String NT308 = "NT308";
	public static final String NT309 = "NT309";
	public static final String NT310 = "NT310";
	public static final String NT311 = "NT311";
	public static final String NT312 = "NT312";
	public static final String NT313 = "NT313";
	public static final String NT314 = "NT314";
	public static final String NT210 = "NT210";

	public static final HashMap<Integer, String> MONTH_REPRESENTATION_MAP = new HashMap<Integer, String>();
	static
	{
		MONTH_REPRESENTATION_MAP.put(0, "07");
		MONTH_REPRESENTATION_MAP.put(1, "08");
		MONTH_REPRESENTATION_MAP.put(2, "09");
		MONTH_REPRESENTATION_MAP.put(3, "10");
		MONTH_REPRESENTATION_MAP.put(4, "11");
		MONTH_REPRESENTATION_MAP.put(5, "12");
		MONTH_REPRESENTATION_MAP.put(6, "01");
		MONTH_REPRESENTATION_MAP.put(7, "02");
		MONTH_REPRESENTATION_MAP.put(8, "03");
		MONTH_REPRESENTATION_MAP.put(9, "04");
		MONTH_REPRESENTATION_MAP.put(10, "05");
		MONTH_REPRESENTATION_MAP.put(11, "06");
	}

	public static final String FETCH_CONTRACT_INFO = "fetchContractInfo";
	public static final String FIND_PROC_EPIN_R3_CONTRACT_FOR_WF = "findProcEpinR3ContractForWF";
	public static final String FLS_CONTRACT_TYPE_ID = "CONTRACT_TYPE_ID";
	public static final String CONTRACT_SOURCE_ID = "CONTRACT_SOURCE_ID";
	public static final String CONTRACT_ID_UNDERSCORE = "CONTRACT_ID";
	public static final String NA_KEY = "N/A";
	public static final String TWO = "2";
	public static final String FETCH_EMAIL_ID = "fetchAgencyCityProviderEmail";
	public static final String FETCH_AGENCY_PROVIDER_NAME = "fetchAgencyCityProviderName";
	public static final String FETCH_SERVICE_START_END_DATE = "fetchServceStartEndDate";
	public static final String INVOICE_START_DATE = "INVOICE_START_DATE";
	public static final String INVOICE_END_DATE = "INVOICE_END_DATE";
	public static final String FETCH_BASE_BUD_FROM_MOD = "fetchBaseBudIdFromModBudId";
	public static final String FETCH_BASE_CONTRACT_ID = "fetchBaseContractId";
	public static final String PROP_CITY_URL = "PROP_CITY_URL";
	public static final String PARENT_BUDGET_ID = "parentBudgetId";
	public static final String LAUNCH_ORG_TYPE = "LAUNCH_ORG_TYPE";
	public static final String PROVIDER_ORG = "Provider";
	public static final String PROPERTY_PE_COMPETITION_POOL_TITLE = "CompetitionPoolTitle";
	public static final String EVAL_POOL_MAPPING_ID = "evaluationPoolMappingId";
	public static final String PROPERTY_PE_EVAL_POOL_MAPPING_ID = "EvaluationPoolMappingId";
	public static final String JAVA_UTIL_HASHMAP = "java.util.HashMap";
	public static final String EVAL_GROUP_ID = "evalGroupId";
	public static final String IS_OPEN_ENDED_RFP = "IsOpenEndedRfp";
	public static final String PROPERTY_PE_EVAL_GRP_TITLE = "EvaluationGroupTitle";
	public static final String INPUT_PARAM_MAP = "aoInputParam";
	public static final String RFP_RELEASE_BEFORE_R4_FLAG = "rfpReleaseBeforeR4Flag";
	public static final String GET_SEL_PROP_COUNT_FOR_EVAL_POOL_MAPPING_ID = "getSelPropCountForEvalPoolMappingId";
	public static final String FETCH_RFP_BEFORE_R4_FLAG = "fetchRfpReleasedBeforeR4Flag";
	public static final String COMPETITION_POOL_STATUS = "competitionPoolStatus";
	public static final String PROPERTIES_STATUS_CONSTANT = "com.nyc.hhs.properties.statusproperties";
	public static final String STATUS_COMPETITION_POOL_PROPOSALS_RECEIVED = "COMPETITION_POOL_PROPOSALS_RECEIVED";
	public static final String STATUS_PROPOSAL_SELECTED = "PROPOSAL_SELECTED";
	public static final String INSERT_TASK_AUDIT = "insertTaskAudit";
	public static final String TASK_AUDIT_CONFIGURATION = "taskAuditConfiguration";
	public static final String TASK_AUDIT_CONFIGURATION_XML = "/com/nyc/hhs/config/TaskAuditConfiguration.xml";
	public static final String INSERT_MULTIPLE_TASK_AUDIT = "insertMultipleTaskAudit";
	public static final String ALERT_FLAG = "alertFlag";
	public static final String SERVER_NAME_FOR_PROVIDER_BATCH = "SERVER_NAME_FOR_PROVIDER_BATCH";
	public static final String SERVER_PORT_FOR_PROVIDER_BATCH = "SERVER_PORT_FOR_PROVIDER_BATCH";
	public static final String CONTEXT_PATH_FOR_PROVIDER_BATCH = "CONTEXT_PATH_FOR_PROVIDER_BATCH";
	public static final String SERVER_PROTOCOL_FOR_PROVIDER_BATCH = "SERVER_PROTOCOL_FOR_PROVIDER_BATCH";
	public static final String CANCEL_EVALUATION_TASK_EVENT = "Cancel Evaluation Task";
	public static final String BUDGET_ADVANCE = "BUDGET_ADVANCE";
	public static final String ADVANCE_NUMBER_ENTITY_TYPE = "ADVANCE_NUMBER";
	public static final String FETCH_COMPETITION_POOL_TITLE = "fetchCompetitionPoolTitle";
	public static final String TASK_EVALUATE_PROPOSAL = "Evaluate Proposal";
	//	Made changes in this method for Enhancement 6405 and Release 3.3.0
	public static final String GET_VENDOR_ADDRESS_ID_FROM_T = "getVendorAddressIdFromT";
	public static final String GET_VENDOR_ADDRESS_ID_FROM_T_MORE_THAN_ONE = "getVendorAddressIdFromTMoreThanOne";
	public static final String GET_VENDOR_ADDRESS_ID_FROM_V = "getVendorAddressIdFromV";
	public static final String GET_VENDOR_ADDRESS_ID_FROM_V_MORE_THAN_ONE = "getVendorAddressIdFromVMoreThanOne";
	public static final String VENDOR_ADDRESS_ID = "vendorAddressId";
	
	public static final String GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_T = "getPaymentVendorAddressIdFromT";
	public static final String GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_T_MORE_THAN_ONE = "getPaymentVendorAddressIdFromTMoreThanOne";
	public static final String GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_V = "getPaymentVendorAddressIdFromV";
	public static final String GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_V_MORE_THAN_ONE = "getPaymentVendorAddressIdFromVMoreThanOne";
	public static final String PAYMENT_VENDOR_ADDRESS_ID = "paymentVendorAddressId";
	
	public static final String GET_VENDOR_ADDRESS_ID_FROM_T_COND_FIRST = "getVendorAddressIdFromTCondFirst";
	public static final String GET_VENDOR_ADDRESS_ID_FROM_T_COND_SECOND = "getVendorAddressIdFromTCondSecond";
	public static final String GET_VENDOR_ADDRESS_ID_FROM_T_COND_THIRD = "getVendorAddressIdFromTCondThird";
	public static final String GET_VENDOR_ADDRESS_ID_FROM_T_COND_FOURTH = "getVendorAddressIdFromTCondFourth";
	
	public static final String GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_T_COND_FIRST = "getPaymentVendorAddressIdFromTCondFirst";
	public static final String GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_T_COND_SECOND = "getPaymentVendorAddressIdFromTCondSecond";
	public static final String GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_T_COND_THIRD = "getPaymentVendorAddressIdFromTCondThird";
	//Release 3.6.0 Enhancement 6405
	public static final String GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_T_COND_FOURTH = "getPaymentVendorAddressIdFromTCondFourth";
	
	
	
	public static final LinkedList<String> GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_T_COND = new LinkedList<String>();
	static
	{
		GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_T_COND.add(GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_T_COND_FIRST);
		GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_T_COND.add(GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_T_COND_SECOND);
		GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_T_COND.add("getPaymentVendorAddressIdFromTCondSecondHalf");
		GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_T_COND.add(GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_T_COND_THIRD);
		GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_T_COND.add(GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_T_COND_FOURTH);
	}
	
	public static final LinkedList<String> GET_VENDOR_ADDRESS_ID_FROM_T_COND = new LinkedList<String>();
	static
	{
		GET_VENDOR_ADDRESS_ID_FROM_T_COND.add(GET_VENDOR_ADDRESS_ID_FROM_T_COND_FIRST);
		GET_VENDOR_ADDRESS_ID_FROM_T_COND.add(GET_VENDOR_ADDRESS_ID_FROM_T_COND_SECOND);
		GET_VENDOR_ADDRESS_ID_FROM_T_COND.add("getVendorAddressIdFromTCondSecondHalf");
		GET_VENDOR_ADDRESS_ID_FROM_T_COND.add(GET_VENDOR_ADDRESS_ID_FROM_T_COND_THIRD);
		GET_VENDOR_ADDRESS_ID_FROM_T_COND.add(GET_VENDOR_ADDRESS_ID_FROM_T_COND_FOURTH);
	}
	
	//Made changes as a part of release 3.4.0 defect 6453
	public static final String FETCH_PROCUREMENT_STATUS = "fetchProcurementStatus";
	public static final String PROCUREMENT_STATUS = "procurementStatus";
	
	//Made changes as a part of release 3.6.0 defect 5905
	public static final String GET_EVAL_PROGRESS_STATUS_FLAG = "getAllEvalProgressStatusFlag";
	public static final String EVAL_PROGRESS_STATUS_FLAG = "evalProgressStatusFlag";
	
	//Made changes as a part of release 3.8.0 enhancement 6534
	
	public static final String UPDATE_REVIEW_PROGRESS_FLAG = "updateReviewInProgressFlag";
	public static final String INSERT_INTO_PAYMENT_ALLOCATION_FOR_BFY_GREATER_THAN_FY = "insertIntoPaymentAllocationForBFYgreaterThanFY";
	public static final String FETCH_BUDGET_FISCAL_YEAR_INFORMATION = "fetchBudgetFiscalYearInformation";
	public static final String BUDGET_FISCAL_YEAR_ID = "BUDGET_FISCAL_YEAR_ID";
	public static final String PAYMENT_FISCAL_YEAR_ID = "FISCAL_YEAR_ID";
	public static final String INTERIM_PERIOD_END_DATE = "INTERIM_PERIOD_END_DATE";
	public static final String INSERT_INTO_PAYMENT_ALLOCATION_OUTSIDE_INTERIM_FOR_BFY_GREATER_THAN_FY = "insertIntoPAOutsideInterimForBFYgreaterThanFY";
	
	//Constants added for Payment Batch - Updating Accounting Line - start
	public static final String SELECT_MAX_INTERIM_PERIOD_END_DATE = "selectMaxInterimPerionEndDate";
	public static final String FETCH_PENDING_APPROVAL_PAYMENTS = "fetchPendingApprovalPayments";
	public static final String DELETE_PENDING_APPROVAL_PAYMENTS = "deletePendingApprovalPayments";
	public static final String INTEGER_CLASS_PATH = "java.lang.Integer";
	public static final String UPDATE_PENDING_APPROVAL_PAYMENTS = "updatePendingApprovalPayments";
	public static final String INSERT_INTO_PAYMENT_ALLOCATION_OUTSIDE_INTERIM_FOR_BFY_GREATER_THAN_FY_1 = "insertIntoPAOutsideInterimForBFYgreaterThanFY1";
	//Constants added for Payment Batch - Updating Accounting Line - end
	//method added as a part of release 3.8.1 enhancement 6536
	public static final String INSERT_INTO_PAYMENT_ALLOCATION_OUTSIDE_INTERIM_FOR_FY_GREATER_THAN_BFY="insertIntoPAOutsideInterimForFYgreaterThanBFY";
	
	//Constants added for Release 3.12.0 enhancement 6644
	public static final String AS_USER_ID = "asUserId";
	// Added for R5
	public static final String NT231 = "NT231";
	public static final String AL230 = "AL230";
	public static final String WORKFLOW_NAME_204_NEGOTIATION_AWARD = "WF204 - Negotiation Award";
	public static final String FETCH_NEGOTIATIONS_CONTRACT_DETAILS = "fetchNegotiationsContractsDetails";
	public static final String FINISH_CONFIGURE_AWARD_CONTRACTS = "finishConfigureAwardContracts";
	//Added in R6 for Apt Interface
	public static final String ACTION_FLAG = "ActionFlag";
	
	//R8.7.0 QC9555 
	public static final String FETCH_SERVICE_START_END_DATE_BY_INV_ID = "fetchServceStartEndDateByInvId";
	public static final String FETCH_ALL_USER_NAME_BY_USER_ID = "fetchAgencyCityProviderNameByUserId";
	public static final String PARAM_INVOICE_SRT_END_DATE_LIST  = "aoInvSrtEndDateLst";
	public static final String PARAM_USER_NAME_LIST  =  "aoUserNameLst";
	//R8.7.0 QC9555 
	
}