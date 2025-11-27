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
<%@page import="com.nyc.hhs.constants.HHSR5Constants"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%--R7 Start: --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%> 
<%--R7 End --%>
<%-- Start changes for R5 --%>
<%@include file="/WEB-INF/r5/jsp/assigneeListMapping.jsp" %>
<portlet:resourceURL var="setDefaultUser" id="setDefaultUser"></portlet:resourceURL>
<input type="hidden" id="setDefaultUser" value="${setDefaultUser}"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/defaultAssignee.js"></script>
<%-- End changes for R5 --%>
<%-- This portlet resource is for Confirmation Popup mapping --%>
<portlet:resourceURL var="invoiceSubmissionOverlay" id="invoiceSubmissionOverlay" escapeXml="false" />
<input type="hidden" id="invoiceSubmissionOverlayVar" value="${invoiceSubmissionOverlay}"/>
<%-- This portlet resource is for calling validateInvoiceReviewLevel method --%>
<portlet:resourceURL var="reviewLevelValidation" id="reviewLevelValidation" escapeXml="false" />
<input type="hidden" id="reviewLevelValidationVar" value="${reviewLevelValidation}"/>

<%-- This portlet resource is for calling showCBGridTabsJSP method to show tab jsp--%>
<portlet:resourceURL var='showCBGridTabs' id='showInvoiceGridTabs'
	escapeXml='false'>
</portlet:resourceURL>
<%-- This portlet resource is for calling saveContractInvoiceReview method to save agency invoice number--%>
<portlet:resourceURL var="saveContractInvoice" id="saveContractInvoiceRevew" escapeXml="false">
</portlet:resourceURL>


<portlet:resourceURL var='getCallBackContractInvoiceData' id='getCallBackContractInvoiceData' escapeXml='false'>
</portlet:resourceURL>
<portlet:resourceURL var='getCallBackContractBudgetData' id='getCallBackContractBudgetData' escapeXml='false'>
</portlet:resourceURL>

<%--Added in R7: Navigate to contract budget screen --%>
<portlet:actionURL var='navigateListScreenUrl' escapeXml='false'>
	<portlet:param  name='controllerAction' value='redirectFromLanding'/>
</portlet:actionURL>
<input type="hidden" name="hiddenNavigateListScreenUrl" id="hiddenNavigateListScreenUrl"  value="${navigateListScreenUrl}"/>
<%--End in R7: Navigate to contract budget screen --%>
<input type = 'hidden' value='${getCallBackContractInvoiceData}' id='getCallBackContractInvoiceData'/>
<input type = 'hidden' value='${getCallBackContractBudgetData}' id='getCallBackContractBudgetData'/>

<input type="hidden" name="hdnSaveContractInvoice" id="hdnSaveContractInvoice" value="${saveContractInvoice}" />
<c:set var="readOnlyPageAttribute" value="true"></c:set>
<input type='hidden' value='${showCBGridTabs}' id='hiddenCBGridTagURL' />
<input type="hidden" id="invoiceId" value="${contractInfo.invoiceId}" />
<%-- Start changes for R5 --%>
<input type="hidden" id="defaultAssignmentEntity" value="${contractInfo.invoiceId}" />
<%-- End changes for R5 --%>
<%--  Start: Added in Defect-8516 --%>
<%-- The following attribute is changed from existingBudget to usesFte --%>
<input type="hidden" name="existingBudget" id="existingBudget" value="${contractInfo.usesFte}"/>
<%--  Start: Added in R7: Program Income --%>
<input type='hidden' value='${contractInfo.oldPIFlag}' id='hiddenIsOldPI' />
<c:if test="${(fn:contains(entryTypeId, '11:1')) or (fn:contains(entryTypeId, '11:0'))}">
<input type='hidden' value='true' id='hiddenIsPiSelected' />
</c:if>
<%--  End R7: Program Income --%>
<%--  Start: Added in Defect-8516 --%>
<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/contractInvoice.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/accordianFunctions.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/unsaveData.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>
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
//Defect fix : 8567
var budgetID = "";
var isScreenLocked = "${accessScreenEnable}";
var contractBudgetReadonlyVar = "${invoiceReadonly}";
var isTaskAssigned = "${detailsBeanForTaskGrid.isTaskAssigned}";
/**
* function to fetch details : this further calls onload and grid operation
* Updated in R6 : Returned Payment, added budget id in parameters 
* Updated in R7 : Added hiddenIsOldPI and hiddenIsPiSelected for Program Income
**/
function showCBGridTabsJSP(tabName, tabId, subbudgetId, invoiceId) {
	pageGreyOut();
	var v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
			+ subbudgetId + "&hdnInvoiceId=" + invoiceId + "&budgetID=" + budgetID + "&hiddenIsOldPI=" + $('#hiddenIsOldPI').val()
			+ "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
	if ('${readOnlyPageAttribute}' != "false") {
		if( taskLevel==1 && (tabName=="invoiceAssignments" || tabName=="invoiceAdvances" ) && isScreenLocked!='false' && 'true' != contractBudgetReadonlyVar && isTaskAssigned=='true'){
			var v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
			+ subbudgetId + "&hdnInvoiceId=" + invoiceId + "&budgetID=" + $("#budgetId").val() +"&advanceReadOnly="
			+ "&hiddenIsOldPI=" + $('#hiddenIsOldPI').val()+ "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
		}else {
			v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
			+ subbudgetId + "&hdnInvoiceId=" + invoiceId + "&readOnlyPage=true" + "&budgetID=" + $("#budgetId").val()
			+ "&hiddenIsOldPI=" + $('#hiddenIsOldPI').val()+ "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
		}
	}
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
			$(function() {
			//Defect Fix : 8567
				budgetID = $("#budgetId").val();
				$("#contractVal").jqGridCurrency();
				$("#accordion").accordion();
				$("#returnAmt").jqGridCurrency();
				<c:forEach var="subBudgetData"   items="${BudgetAccordianData}" varStatus="subBudgetCounter"  >
				$("#subBudAmt${subBudgetCounter.count}").jqGridCurrency();
				$('#tabs${subBudgetCounter.count} li')
						.removeClass(
								'ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
				$('#tabs${subBudgetCounter.count}').tabs();
				</c:forEach>
			});
			
			//Starts Logic For EntityTypeTab Show 
			showSelectedEntiTypeTabs();
			//Ends Logic For EntityTypeTab Show
			
			$("#documentWrapper").html( $("#tempDocument").html());
			$("#tempDocument").html("");
		  if('${readOnlyPageAttribute}' != "false"){
		       $("#taskButtonsId").hide();
		   }	
	if(	$("#hdnTaskLevel").val()==1 && isScreenLocked!="false" && 'true'!= contractBudgetReadonlyVar &&  isTaskAssigned=='true'){
		 document.getElementById("invoiceSaveButton").disabled=false;
		 document.getElementById("invoiceNumber").readOnly=false;
	 }
		});
//	code updation for R4 starts
//This function is called by default on page load.
function showSelectedEntiTypeTabs(){
	var _counter = null;
	<c:forEach var="subBudgetData" items="${BudgetAccordianData}" varStatus="subBudgetCounter">
		if(_counter != null){
			_counter = _counter + ',' + '${subBudgetCounter.count}';
		}else{
			_counter = '${subBudgetCounter.count}';
		}
		for(var _count=1; _count<=11; _count++)
			$('#'+_count+'_'+'${subBudgetCounter.count}').hide();
	</c:forEach>
	_counter=_counter.split(',');
	var entryTypeData = '${entryTypeId}';
	entryTypeData = entryTypeData.replace('[','').replace(']','').split(',');
	for(var j=0; j<_counter.length; j++)
		if(entryTypeData != '')
		for(var i=0; i<entryTypeData.length; i++)//For R4 Contract Budget
			$('#'+$.trim(entryTypeData[i]).split(':')[0]+'_'+_counter[j]).show();
		else
			for(var i=0; i<12; i++) //For R3 Contract Budget
				$('#'+(i+1)+'_'+_counter[j]).show();
}
//	code updation for R4 ends

/**
* This method is added in Release 6.This function is call on click of grid tabs to show the Personnel Services tab.
* This method is moved from invoicePersonnelServicesTab.jsp as part of defect 8465.
**/
function showPsSCreen(tabName, tabId, subbudgetId, invoiceId,budgetId) {
	pageGreyOut();
	var v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
			+ subbudgetId + "&hdnInvoiceId=" + invoiceId+ "&budgetID=" + budgetId
			+ "&hiddenIsOldPI=" + $('#hiddenIsOldPI').val()+ "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
			
	if ($('#hiddenReadOnlyPageAttribute').val() != "false") {
		v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
				+ subbudgetId + "&hdnInvoiceId=" + invoiceId + "&readOnlyPage=true"
				+ "&hiddenIsOldPI=" + $('#hiddenIsOldPI').val()+ "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
	}
	var urlAppender = $("#hiddenCBGridTagURL").val();
	
	jQuery.ajax({
		type : "POST",
		url : urlAppender,
		data : v_parameter,
		success : function(e) {
			$("#personnelServiceTab_" + subbudgetId).html(e);
			removePageGreyOut();
		},
		beforeSend : function() {
		}
	});
}
</script>

<c:set var="sectionName"><%=HHSComponentMappingConstant.AGENCY_S399_PAGE%></c:set>
<input type="hidden" id="budgetId" value="${contractInfo.budgetId}" />

 	<!--R7 Start: added for Auto-Approval Message  -->
<c:if test="${org_type eq 'agency_org' || org_type eq 'city_org'}">  
 	 <c:if test="${fiscalBudgetInfo.budgetModification eq 'true'}">
	 		<div class="clear"></div>
	 		<div class="infoMessage" style="display:block;margin-bottom:2px;"><%=HHSR5Constants.APPROVED_MODIFICATION_MESSAGE%>&nbsp;
				<a class="activelink" style="text-decoration: underline;" href="javascript:void(0);" onclick="submitFormToModificationBudgetDetails('${contractInfo.contractId}','${contractInfo.budgetId}');">Budget List.</a>
	 		</div>
	 </c:if>  	
</c:if>
	<!--R7 End: added for Auto-Approval Message  -->

<div class='complianceWrapper'>	
<d:content  section="${sectionName}" authorize="" isReadOnly="${accessScreenEnable eq false}">
<task:taskContent workFlowId="" taskType="taskContractInvoiceReview" isTaskScreen="" level="header" taskDetail="" ></task:taskContent>
<d:content  isReadOnly="true"  >
<form:form action="" method="post" name="contractBudgetForm"
		id="contractBudgetForm">
			<%
		String lsTransactionMsg = "";
		if (null!=request.getAttribute(HHSConstants.TRANSACTION_RSLT_MSG)){
			lsTransactionMsg = (String)request.getAttribute(HHSConstants.TRANSACTION_RSLT_MSG);
		}
		if(null!=request.getAttribute("transactionStatus") && ApplicationConstants.MESSAGE_PASS_TYPE.equalsIgnoreCase((String)request.getAttribute(HHSConstants.TRANSACTION_RSLT_STATUS))){%>
		<div id="transactionStatusDiv" class="passed" style="display: block"><%=lsTransactionMsg%>
		</div>
		<%}else if(( null != request.getAttribute(HHSConstants.TRANSACTION_RSLT_STATUS) ) && ApplicationConstants.MESSAGE_FAIL_TYPE.equalsIgnoreCase((String)request.getAttribute(HHSConstants.TRANSACTION_RSLT_STATUS))){%>
		<div id="transactionStatusDiv" class="failed" style="display: block"><%=lsTransactionMsg%>
		</div>
		<%}%>
<div id="transactionStatusDiv" class=""></div>

<div class="failed" id="errorGlobalMsg"></div>
<div class="passed" id="successGlobalMsg"></div>
<%-- Code need to Integrate Starts --%>

<div>&nbsp;</div>

<h2>Contract Invoicing</h2>
	<div class='hr'></div>

	<div class='floatRht'>Status: ${contractInfo.budgetStatus}</div>
	<div class='clear'></div>
	
		<h3>Contract Information</h3>
		
		<div class="formcontainer paymentFormWrapper">
			<div class="row">
			<span class="label">Agency:</span> 
			<span
			class="formfield">${contractInfo.contractAgencyName}</span>
			</div>
			<div class="row">
			<span class="label">Procurement/Contract Title:</span> 
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
		<%-- Start changes for R5 --%>
			<span class="label" title='CT# is the contract registration number issued by the Citys Financial Management System (FMS). You can use this number to search for additional information in the FMS Payee Information Portal – https://nyc.gov/pip'>CT#:</span> 
		<%-- End changes for R5 --%>
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
			<span class="formfield" id="contractVal">${contractInfo.contractValue}
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

<%-- Fiscal Year Budget Information Grid Starts --%>
<div id="assignAdvanceId">
<jsp:include
			page="/WEB-INF/r2/jsp/invoice/assignAdvanceTable.jsp" />
 </div>
	  <c:if test="${detailsBeanForTaskGrid.level eq '1' && detailsBeanForTaskGrid.isTaskAssigned && invoiceReadonly ne null && invoiceReadonly ne '' &&  invoiceReadonly ne 'true'}">
	<div class="buttonholder">
			<input type="button" id="invoiceSaveButton" class="button" value="Save" onclick="onReviewSaveClick('${contractInfo.invoiceId}');"/>
	</div>
	</c:if>
<%-- Invoice Information Ends --%>	

<p>&nbsp;</p>

		<c:forEach var="subBudgetData" items="${BudgetAccordianData}"
			varStatus="subBudgetCounter">
			<div id="accordionTopId">
			<div class="accrodinWrapper hdng" id="accordionHeaderId"
				onclick="displayAccordion(this);if(divEmpty('invoiceSummary${subBudgetCounter.count}')){showCBGridTabsJSP('invoiceSummary','invoiceSummary${subBudgetCounter.count}','${subBudgetData.subBudgetID}','${contractInfo.invoiceId}');}">
			<h5 class="breakAll">${subBudgetData.subBudgetName}</h5>
			<ul>
				<li><label id="subBudAmt${subBudgetCounter.count}"> ${subBudgetData.subBudgetAmount}
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
			<ul class='procurementTabber'>
				<li><a href='#invoiceSummary${subBudgetCounter.count}'
					jspname='invoiceSummary'>Budget Summary</a></li>
				<!-- R6 change Starts -->
				<c:choose>
					<c:when test="${contractInfo.usesFte eq 0}">
					<%--  Start: Update in Defect-8470 --%>
						<li id="1_${subBudgetCounter.count}"><a href='#invoiceSummary${subBudgetCounter.count}'
								jspname='invoicePersonnelServicesTab'>Personnel Services</a></li>
					<%--  End: Update in Defect-8470 --%>
					</c:when>
					<c:otherwise>
						<li id="1_${subBudgetCounter.count}"><a href='#invoiceSummary${subBudgetCounter.count}'
								jspname='invoicePersonnelServices'>Personnel Services</a></li>
					</c:otherwise>
				</c:choose>
				<!-- R6 change Ends -->	
				<li id="2_${subBudgetCounter.count}"><a href='#invoiceSummary${subBudgetCounter.count}'
					jspname='invoiceOperationSupport'>Operations &amp; Support</a></li>
				<li id="3_${subBudgetCounter.count}"><a href='#invoiceSummary${subBudgetCounter.count}'
					jspname='invoiceUtilities'>Utilities</a></li>
				<li id="4_${subBudgetCounter.count}"><a href='#invoiceSummary${subBudgetCounter.count}'
					jspname='invoiceProfessionalServices'>Professional Services</a></li>
				<li id="5_${subBudgetCounter.count}"><a href='#invoiceSummary${subBudgetCounter.count}'
					jspname='rentInvoicing'>Rent</a></li>
			<li id="6_${subBudgetCounter.count}"><a href='#invoiceSummary${subBudgetCounter.count}'
					jspname='contractedServicesInvoicing'>Contracted Services</a></li>
				<li id="7_${subBudgetCounter.count}"><a href='#invoiceSummary${subBudgetCounter.count}' 
					jspname='invoiceRate'>Rate</a></li>
				<li id="8_${subBudgetCounter.count}"><a href='#invoiceSummary${subBudgetCounter.count}'
					jspname='milestoneInvoice'>Milestone</a></li>
				<li id="9_${subBudgetCounter.count}"><a href='#invoiceSummary${subBudgetCounter.count}'
					jspname='invoiceUnallocatedFunds'>Unallocated Funds</a></li>
				<li id="10_${subBudgetCounter.count}"><a href='#invoiceSummary${subBudgetCounter.count}'
					jspname='invoiceIndirectRate'>Indirect Rate</a></li>
				<li id="11_${subBudgetCounter.count}"><a href='#invoiceSummary${subBudgetCounter.count}'
					jspname='programIncomeInvoice'>Program Income</a></li>
				<%-- Start: Added in R7 for Cost-Center--%>
				<c:if test="${contractInfo.costCenterOpted eq '2'}">
				<li id="cc_${subBudgetCounter.count}"><a href='#invoiceSummary${subBudgetCounter.count}'
						 jspname='invoiceServices'>Services</a></li>
				</c:if>
				<%-- End: Added in R7 for Cost-Center--%>
			</ul>
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
			<li><label> &nbsp;</label></li>
		</ul>
		</div>
		<div id="accordianId1" class="close">
		<div class="accContainer" id="documentWrapper"></div>
		</div>
		</div>
		
		<div id="accordionTopId2">
		<div class="accrodinWrapper hdng" id="accordionHeaderId2"
			onclick="displayAccordion(this);if(divEmpty('advanceWrapper')){showCBGridTabsJSP('invoiceAdvances', 'advanceWrapper', '','');}">
		<h5>Advances</h5>
			<ul>
				<li><label> &nbsp;</label></li>
			</ul>
		</div>
		<div id="accordianId2" class="close">
			<div id='tabs-container' class="clearHeight">
				<div class="accContainer" id="advanceWrapper"></div>
			</div>
		</div>
		</div>
		
	  <div id="accordionTopId3">
			<div class="accrodinWrapper hdng" id="accordionHeaderId3" onclick="displayAccordion(this);if(divEmpty('assignmentWrapper')){showCBGridTabsJSP('invoiceAssignments', 'assignmentWrapper', '','');}">
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
		<c:if test="${contractInfo.noOfReturnedPayments > 0}">
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
	<task:taskContent workFlowId="" commentsSection=" " taskType="taskContractInvoiceReview"  isTaskScreen=""  level="footer"></task:taskContent>
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
