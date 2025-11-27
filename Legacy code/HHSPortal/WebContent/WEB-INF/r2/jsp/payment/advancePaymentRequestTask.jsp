<%-- This jsp is used for invoice review task purpose to approve to invoice--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page import="com.nyc.hhs.util.HHSUtil" %>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@page import="com.nyc.hhs.constants.HHSConstants"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@ page errorPage="/error/errorpage.jsp" %>

<%-- This portlet resource is for Confirmation Popup mapping --%>
<portlet:resourceURL var="invoiceSubmissionOverlay" id="invoiceSubmissionOverlay" escapeXml="false" />
<input type="hidden" id="invoiceSubmissionOverlayVar" value="${invoiceSubmissionOverlay}"/>
<%-- This portlet resource is for calling validateInvoiceReviewLevel method --%>
<portlet:resourceURL var="reviewLevelValidation" id="reviewLevelValidation" escapeXml="false" />
<input type="hidden" id="reviewLevelValidationVar" value="${reviewLevelValidation}"/>

<%-- This portlet resource is for calling showCBGridTabsJSP method to show tab jsp--%>
<portlet:resourceURL var='showCBGridTabs' id='showPaymentGridTabs'
	escapeXml='false'>
</portlet:resourceURL>

<%-- This portlet resource is for calling showAssignmeGridTabsJSP method to show Assignment jsp--%>
<portlet:resourceURL var='showInvoiceGridTabs' id='showInvoiceGridTabs'
	escapeXml='false'>
</portlet:resourceURL>
<%-- This portlet resource is for calling saveContractInvoiceReview method to save agency invoice number--%>
<portlet:resourceURL var="saveContractInvoice" id="saveContractInvoiceRevew" escapeXml="false">
</portlet:resourceURL>

<input type="hidden" name="hdnSaveContractInvoice" id="hdnSaveContractInvoice" value="${saveContractInvoice}" />
<input type='hidden' value='${showCBGridTabs}' id='hiddenCBGridTagURL' />
<input type='hidden' value='${showInvoiceGridTabs}' id='hiddenAssignmentGridTagURL' />
   
<style type="text/css">
/* Fixed for Contract information as per updated wireframes......page specific fix, DO NOT INCLUDE IN EXTERNAL CSS */
.formcontainer .row span.vendorfield{
	float: left;
	min-height: 20px;
	padding: 4px 0;
	text-align: left;
	width:60%
}
</style>

<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/advancePayment.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/accordianFunctions.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/unsaveData.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>
<%-- Added in R5 start--%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/defaultAssignee.js"></script>
<%-- Added in R5 ends--%>
<%-- [Start] 9.4.0 qc 9656 Invoice Review task may create same payment more than once due to multi-tab --%>


<%
		String pageName="";
		if(request.getRequestURI()!=null && request.getRequestURI().trim().length()>0){
			pageName = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/")+1);
			if(pageName!=null && pageName.trim().length()>0)
				request.setAttribute("multipletab_taskName", pageName);
		}		
%>

<jsp:include page="/WEB-INF/jsp/multipletab.jsp">
</jsp:include>
<%-- [End] 9.4.0 qc 9656 Invoice Review task may create same payment more than once due to multi-tab --%>
<script type='text/javascript'>
//Variable added for Defect 8567
var budgetID = "";
//Variable added for Defect 8567
var isScreenLocked = "${accessScreenEnable}";
var contractBudgetReadonlyVar = "${contractBudgetReadonly}";
var isTaskAssigned = "${detailsBeanForTaskGrid.isTaskAssigned}";
/**
* function to fetch details : this further calls onload and grid operation
* Updated in R6 : Returned Payment, added budget id in parameters 
**/
function showCBGridTabsJSP(tabName, tabId, subbudgetId,parentSubBudgetId) {
		pageGreyOut();
		//Changed v_parameter for R6: Defect 8567 Fix
		var v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
				+ subbudgetId + "&budgetID=" + budgetID;
		//End:Changed v_parameter for R6: Defect 8567 Fix
		var urlAppender = $("#hiddenCBGridTagURL").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				$("#" + tabId).html(e);
				removePageGreyOut();
			},
			beforeSend : function() {
			}
		});
	}
	
	//This function is for Assignment Grid
	
function showAssignmentTabsJSP(tabName, tabId, subbudgetId, invoiceId) {
	pageGreyOut();
	var v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
			+ subbudgetId + "&hdnInvoiceId=" + invoiceId;
	var urlAppender = $("#hiddenCBGridTagURL").val();

	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#" + tabId).html(e);
			removePageGreyOut();
		},
		beforeSend : function() {
		}
	});
}
// this function is called when document get ready
$(document)
.ready(
		function() {
			//Assigned budgetID for R6: Defect 8567 Fix
			budgetID = $("#budgetId").val();
			//End:Assigned budgetID for R6: Defect 8567 Fix
			$(function() {
				$("#accordion").accordion();
				$("#returnAmt").jqGridCurrency();
				<c:forEach var="subBudgetData"   items="${BudgetAccordianData}" varStatus="subBudgetCounter"  >
				$('#tabs${subBudgetCounter.count} li')
						.removeClass(
								'ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
				//$('#tabs${subBudgetCounter.count}').tabs();
				</c:forEach>
			});
			$("#documentWrapper").html( $("#tempDocument").html());
			$("#tempDocument").html("");
		       $("#taskButtonsId").hide();
		});
		
		//This method is called for client side finish task validation
function finishTaskValidation(){
		var returnVal = true;
		var publicCommentVal = "";
		var internalCommentVal = "";
		if(document.getElementById("internalCommentArea")!=null){
			internalCommentVal=trim(document.getElementById("internalCommentArea").value);
		}if(document.getElementById("publicCommentArea")!=null){
			publicCommentVal=trim(document.getElementById("publicCommentArea").value);
		}
		var taskStatus = $("#finishtaskchild").val();
		if(taskLevel==1 && publicCommentVal=="" && taskStatus=="Returned for Revision"){
			$("#taskErrorDiv").html(publicCommentErrorMsg);
			$("#taskErrorDiv").show();
			returnVal = false;
		}else if(taskLevel>1 && internalCommentVal=="" && taskStatus=="Returned for Revision"){
			$("#taskErrorDiv").html(internalAgencyCommentErrorMsg);
			$("#taskErrorDiv").show();
			returnVal = false;
		}
		return returnVal;
	}
	
	//This method is called to trim a string
function trim(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g,"");
}
</script>



<c:set var="sectionName"><%=HHSComponentMappingConstant.AGENCY_S399_PAGE%></c:set>
<div class='complianceWrapper'>	
<d:content  section="${sectionName}" authorize="" isReadOnly="${accessScreenEnable eq false}">
<task:taskContent workFlowId="" taskType="taskAdvancePaymentRequest" isTaskScreen="" level="header" taskDetail="" ></task:taskContent>
<d:content  isReadOnly="true"  >
		
		<!-- Release 5 -->
<%@include file="/WEB-INF/r5/jsp/assigneeListMapping.jsp" %>
<portlet:resourceURL var="setDefaultUser" id="setDefaultUser"></portlet:resourceURL>
<input type="hidden" id="setDefaultUser" value="${setDefaultUser}"/>
<!-- Release 5 -->
<form:form action="" method="post" name="contractBudgetForm"
		id="contractBudgetForm">
		<%
		String lsTransactionMsg = "";
		if (null!=request.getAttribute(HHSConstants.TRANSACTION_RSLT_MSG)){
			lsTransactionMsg = (String)request.getAttribute(HHSConstants.TRANSACTION_RSLT_MSG);
		}
		if(null!=request.getAttribute(HHSConstants.TRANSACTION_STATUS) && ApplicationConstants.MESSAGE_PASS_TYPE.equalsIgnoreCase((String)request.getAttribute(HHSConstants.TRANSACTION_RSLT_STATUS))){%>
		<div id="transactionStatusDiv" class="passed" style="display: block"><%=lsTransactionMsg%>
		</div>
		<%}else if(( null != request.getAttribute(HHSConstants.TRANSACTION_RSLT_STATUS) ) && ApplicationConstants.MESSAGE_FAIL_TYPE.equalsIgnoreCase((String)request.getAttribute(HHSConstants.TRANSACTION_RSLT_STATUS))){%>
		<div id="transactionStatusDiv" class="failed" style="display: block"><%=lsTransactionMsg%>
		</div>
		<%}%>
<div id="transactionStatusDiv" class=""></div>


<input type="hidden" id="budgetId" value="${contractInfo.budgetId}" />
<input type="hidden" id="contractId" value="${contractInfo.contractId}" />
<div class="failed" id="errorGlobalMsg"></div>
<div class="passed" id="successGlobalMsg"></div>
<%-- Code need to Integrate Starts --%>

<div>&nbsp;</div>

<h2 class='autoWidth'>Advance Details</h2>
	<div class='hr'></div>

	
	<div class='clear'></div>
	
		<h3>Contract Information</h3>
		
		<div class="formcontainer paymentFormWrapper">
			<div class="row">
			<span class="label">Agency:</span> 
			<span
			class="formfield">${contractInfo.contractAgencyName}</span>
			</div>
			<div class="row">
			<span class="label">Procurement Title:</span> 
			<span
			class="formfield">${contractInfo.contractTitle}</span>
			</div>
			<div class="row">
			<span class="label">Provider:</span> 
			<span
			class="formfield">${contractInfo.provider}</span>
			</div>
			<div class="row">
			<span class="label">Procurement E-PIN:</span> 
			<span
			class="formfield">${contractInfo.procEpin}</span>
			</div>
			<div class="row">
			<span class="label">Award E-PIN:</span> 
			<span
			class="formfield">${contractInfo.awardEpin}</span>
			</div>
		</div>

	<div class="formcontainer paymentFormWrapper">
		<div class="row">
			<span class="label" title='The CT# is the contract registration number issued by the Citys Financial Management System (FMS). You can use this number to search for additional information in the FMS Payee Information Portal - https://nyc.gov/pip'>
				CT#:
			</span> 
			<span class="formfield"> ${contractInfo.extCT} </span>
		</div>
		<div class="row">
			<span class="label">Contract Start Date:</span>
			<span class="formfield"> 
				<fmt:formatDate pattern="MM/dd/yyyy" value="${contractInfo.contractStartDate}" /> 
			</span>
		</div>
		<div class="row">
			<span class="label">Contract End Date:</span> 
			<span class="formfield"> 
				<fmt:formatDate pattern="MM/dd/yyyy" value="${contractInfo.contractEndDate}" /> 
			</span>
		</div>
		<div class="row">
			<span class="label">Contract Amount:</span> 
			<span class="formfield"> 
				<fmt:formatNumber type="currency" value="${contractInfo.contractValue}" /> 
			</span>
		</div>
		<div class="row">
			<span class="label">Program Name:</span>
	 		<span class="formfield">${contractInfo.programName}</span>
		</div>
	</div>

	<p>&nbsp;</p>
<%-- Contract Information Ends --%>

<p>&nbsp;</p>

<%-- Fiscal Year Budget Information Starts --%>
<div id="assignAdvanceId">
<jsp:include
			page="/WEB-INF/r2/jsp/payment/fyBudgetInfoForPayment.jsp" />
 </div>
	 
<%-- Fiscal Year Budget Information End --%>	

<p>&nbsp;</p>

		<c:forEach var="subBudgetData" items="${BudgetAccordianData}"
			varStatus="subBudgetCounter">
			<div id="accordionTopId">
			<div class="accrodinWrapper disabledAccordion hdng" id="accordionHeaderId"
				onclick="return false;">
			<h5 class="breakAll">${subBudgetData.subBudgetName}</h5>
			<ul>
				<li><label> <fmt:formatNumber type="currency"
					value="${subBudgetData.subBudgetAmount}" />
					<c:set var="subAmounttotal" value="${subAmounttotal + subBudgetData.subBudgetAmount}"></c:set>
					</label></li>
			</ul>
			</div>

			<div id="accordianId" class="close">
			<div class="accContainer">
			<div id="tabs${subBudgetCounter.count}" class='accordionBorder'>
			<input type="hidden" id="hdnGridDivId" value="invoiceSummary${subBudgetCounter.count}"/>
			<input type="hidden" id="hdnGridSubBudgetId" value="${subBudgetData.subBudgetID}"/>
			<input type="hidden" id="hdnGridParentSubBudgetId" value="${contractInfo.invoiceId}"/>
			
			<div class="clear accordionWrapper">
			<div id='invoiceSummary${subBudgetCounter.count}'>
	        </div>

		
			</div>
			</div>
			</div>


			</div>
			</div>
		</c:forEach>

		<div id="accordionTopId1">
		<div class="accrodinWrapper hdng" id="accordionHeaderId1"
			onclick="displayAccordion(this);">
		<h5>Documents</h5>
			<ul>
				<li><label>&nbsp;</label>
				</li>
			</ul>
		</div>
		<div id="accordianId1" class="close">
		<div class="accContainer" id="documentWrapper"></div>
		</div>
		</div>
		
		<div id="accordionTopId2">
		<div class="accrodinWrapper hdng" id="accordionHeaderId2"
			onclick="displayAccordion(this);if(divEmpty('advanceWrapper')){showCBGridTabsJSP('advance', 'advanceWrapper', '','');}">
		<h5>Advances</h5>
			<ul>
				<li><label>&nbsp;</label>
				</li>
			</ul>
		</div>
		<div id="accordianId2" class="close">
			<div id='tabs-container' class="clearHeight">
				<div class="accContainer" id="advanceWrapper"></div>
			</div>
		</div>
		</div>
		
	  <div id="accordionTopId3">
			<div class="accrodinWrapper hdng" id="accordionHeaderId3" onclick="displayAccordion(this);if(divEmpty('assignmentWrapper')){showAssignmentTabsJSP('paymentAssignments', 'assignmentWrapper', '','');}">
				<h5>Assignments</h5>
					<ul>
						<li><label>&nbsp;</label>
						</li>
					</ul>
			</div>
			<div id="accordianId3" class="close">
				<div id='tabs-container' class="clearHeight">
				 <div class="accContainer" id="assignmentWrapper"></div>
				</div>
			</div>
		</div>
		<!-- R6: Returned Payment change Starts -->
		<c:if test="${(contractInfo.noOfReturnedPayments > 0)}">
			<div id="accordionTopId4">
				<div class="accrodinWrapper hdng" id="accordionHeaderId4"
					onclick="displayAccordion(this); if(divEmpty('returnedPaymentWrapper')){showCBGridTabsJSP('returnedPaymentReadOnly', 'returnedPaymentWrapper', '','');}">
					<h5>Returned Payments</h5>
					<ul>
						<li><label id="returnAmt" class='accordionDollar'>${contractInfo.returnedPaymentAmount}</label></li>
					</ul>
				</div>
				<div id="accordianId4" class="close">
					<div id='tabs-container' class="clearHeight">
						<div class="accContainer" id="returnedPaymentWrapper"></div>
					</div>
				</div>
			</div>
		</c:if>
			<!-- R6: Returned Payment change Ends -->
	</form:form>
<div id="tempDocument" style="display:none"><jsp:include
			page="/WEB-INF/r2/jsp/tasks/document.jsp" /></div>
</d:content>
<p class='clear'>&nbsp;</p>
<div class='gridFormField'>
	<task:taskContent workFlowId="" commentsSection=" " taskType="taskAdvancePaymentRequest"  isTaskScreen=""  level="footer"></task:taskContent>
	</div>
</d:content>
</div>
<%--  Overlay popup starts --%>
<div class="overlay"></div>
<div class="alert-box-help">
				<div class="tabularCustomHead toplevelheaderHelp"></div>
	            <div id="helpPageDiv"></div>
		  		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>
<div class="alert-box-submit-invoice" id="overlayDivId"></div>
<input type='hidden' value='${subAmounttotal}' id='hiddenSubAmountTotal' />


<div class="alert-box-add-assign" id="overlayDivId1"></div>

