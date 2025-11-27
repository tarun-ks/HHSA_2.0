<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page import="com.nyc.hhs.constants.HHSConstants"%>

<%@ taglib prefix="fmtMessages" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>

<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ page errorPage="/error/errorpage.jsp"%>

<%-- The Contract Budget Amendment page gives an overview of the contract Amendment information,
 fiscal year budget information, and allows the user to drill down into more
  specific views of the budget; for example, line item details for personnel
   services, OTPS, etc   Provider users will be able to view and upload
    documents to support an invoice  
    The page contains the following sections
    Contract Information : Contains general information about the contract such as 
    			Procurement name contract value startend date etc 
	Fiscal Year Budget Information : Contains general information about the Fiscal Year
		 Budget such as the fiscal year amount, available balance, etc
	Documents: the Provider will be able to upload and view documents
		 associated with the budget in this section.
	Advance Summary : In this section, the user will be able to view Advance 
		information such as advance amount and advance amount recouped to date for each approved advance.
	Assignments Summary : The Provider/Agency user will be able to view the
		 assignees selected for this budget and the relevant fiscal information. 
		  Assignments can only be added during the invoicing process.
	Comments or Comments History : The Provider user can enter comments for
		 the Agency to view when reviewing the invoice submitted by the Provider.
		   The Provider will also be able to see a history of comments previously 
		   submitted and those comments given by the Agency.
    --%>
    
    <%-- This portlet resource is used to this method is used to fetch the grid data from
 ContractBudgetAmendmentController Controller --%>
<portlet:resourceURL var='showCBGridTabs' id='showCBAmendmentGridTabs'
	escapeXml='false'>
</portlet:resourceURL>

<%-- This portlet resource is used to this method is used to fetch the non grid data from
 ContractBudgetAmendmentController Controller --%>
<portlet:resourceURL var='getCallBackData' id='getCallBackData' escapeXml='false'>
</portlet:resourceURL>
<input type = 'hidden' value='${getCallBackData}' id='getCallBackData'/>

<fmtMessages:setBundle basename="com/nyc/hhs/properties/messages"/>
<input type='hidden' value='${showCBGridTabs}' id='hiddenCBGridTagURL' />
<c:set var="subAmounttotal" value="0"></c:set>
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if
	test="${(contractBudgetReadonly ne null && contractBudgetReadonly eq 'true') || org_type eq 'agency_org' || org_type eq 'city_org' }">
	<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>


<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/accordianFunctions.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/contractAmend.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>

<script type='text/javascript'>
	// JS objects for messages for JSP validations
		// JS objects for ids
	var budgetID = "${aoHashMap.budgetId}";
	var contractID = "${aoHashMap.contractId}";

	var errorMessage = "${errorMessage}";
	var successMessage = "${successMessage}";
	

	//Initialize for Collapse and Expand Demo:
	function displayAccordion(obj) {
		if ($("#close").attr("checked") != 'checked') {
			var openShow = $(obj).next().attr("class");
				$(obj).next().removeAttr("class");
				$(obj)
						.attr(
								"style",
								"background:url('') no-repeat scroll 99% center #4297E2");
				$(obj).next().addClass("custDataRowHead1");
				$(obj).nextAll().show();
			
		}
	}
	$(document)
	.ready(
			function() {
				$(function() {
					displayAccordion('#accordionHeaderId');
					$("#accordion").accordion();
					showCBGridTabsJSP('modificationBudgetSummary','budgetSummary'+$("#subBudgetCounterCount").val(),$("#subBudgetDataSubBudgetID").val(),$("#subBudgetDataParentSubBudgetID").val());
				});
			});
	
	

	//This function is called on click of view Comments History tab on task footer
	function showCBGridTabsJSP(tabName, tabId, subbudgetId, parentSubBudgetId) {
		pageGreyOut();
		var v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
				+ subbudgetId + "&hdnParentSubBudgetId=" + parentSubBudgetId;
		if ('${readOnlyPageAttribute}' != "false") {
			v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
					+ subbudgetId + "&readOnlyPage=" + "&hdnParentSubBudgetId=" + parentSubBudgetId+ "&hdnIsPrinterFriendly=true";
		}
		var urlAppender = $("#hiddenCBGridTagURL").val();

		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				$("#" + tabId).html(e);
				$(".linkPrint").hide();
				removePageGreyOut();
			},
			beforeSend : function() {
			}
		});
	}

</script>

<d:content isReadOnly="${readOnlyPageAttribute}">
	<h2 class='autoWidth'>Contract Budget - Amendment</h2>
			
	<div class='hr'></div>
	<% String lsErrorMessage = "";
		if(null != request.getAttribute("errorMessage")){
			lsErrorMessage = (String) request.getAttribute("errorMessage");
		%>
		<input type="text"/>
			<div class="failed breakAll" style="display:block" id="error"><%=lsErrorMessage%></div>			
	<%} %>	
	<div class="failed" id="errorGlobalMsg"></div>
	<div class="passed" id="successGlobalMsg"></div>
	
	<div class='floatRht'>Status: ${contractInfo.budgetStatus}</div>
	<div class='clear'></div>

	<%-- Container Starts --%>
	<%-- Form Data Starts --%>
	<h3>Contract Information</h3>
	<div class="formcontainer paymentFormWrapper">
	<div class="row"><span class="label">Agency:</span> <span
		class="formfield">${contractInfo.contractAgencyName}</span></div>
	<div class="row"><span class="label">Procurement/Contract Title:</span> <span
		class="formfield">${contractInfo.contractTitle}</span></div>
	<div class="row"><span class="label">Provider:</span> <span
		class="formfield">${contractInfo.provider}</span></div>
	<div class="row"><span class="label">Procurement E-PIN:</span> <span
		class="formfield">${contractInfo.procEpin}</span></div>
	<div class="row"><span class="label">Award E-PIN:</span> <span
		class="formfield">${contractInfo.awardEpin}</span></div>
	<%-- Start : Added for 8430 --%>
	<div class="row"><span class="label">Amendment E-PIN:</span> <span
		class="formfield">${contractInfo.amendEpin}</span></div>
	<%-- End : Added for 8430 --%>
	</div>
	</div>

	<div class="formcontainer paymentFormWrapper">
	<div class="row"><span class="label">CT#:</span> <span
		class="formfield"> ${contractInfo.extCT} </span></div>
	<div class="row"><span class="label">Contract Start Date:</span>
	<span class="formfield"> <fmt:formatDate pattern="MM/dd/yyyy"
		value="${contractInfo.contractStartDate}" /> </span></div>
	<div class="row"><span class="label">Contract End Date:</span> <span
		class="formfield"> <fmt:formatDate pattern="MM/dd/yyyy"
		value="${contractInfo.contractEndDate}" /> </span></div>
	<div class="row"><span class="label">Contract Amount:</span> <span
		class="formfield"> <fmt:formatNumber type="currency"
		value="${contractInfo.contractValue}" /> </span></div>
	<div class="row"><span class="label">Program Name:</span> <span
		class="formfield">${contractInfo.programName}</span></div>
	</div>

	<p>&nbsp;</p>

	<%-- Fiscal Year Budget Information Starts --%>
	<div id="assignAdvanceId1">
		<jsp:include page="/WEB-INF/r2/jsp/contractbudget/amendFYBudget.jsp" />
 	</div>
	

	<p>&nbsp;</p>
	<form:form action="" method="post" name="contractBudgetForm"
		id="contractBudgetForm">
		<c:forEach var="subBudgetData" items="${BudgetAccordianData}"
			varStatus="subBudgetCounter">
			<div id="accordionTopId">
			<div class="accrodinWrapper hdng" id="accordionHeaderId">
			<h5 class="breakAll">${subBudgetData.subBudgetName}</h5>
			<ul>
				<li><label> <fmt:formatNumber type="currency"
					value="${subBudgetData.subBudgetAmount}" />
					<c:set var="subAmounttotal" value="${subAmounttotal + subBudgetData.subBudgetAmount}"></c:set>
					</label></li>
			</ul>
			</div>
		<input type="hidden" value="${subBudgetCounter.count}" id="subBudgetCounterCount">  
		<input type="hidden" value="${subBudgetData.subBudgetID}" id="subBudgetDataSubBudgetID"> 
		<input type="hidden" value="${subBudgetData.parentSubBudgetId}" id="subBudgetDataParentSubBudgetID">   
		
			<div id="accordianId" class="close">
			<div class="accContainer">
			<div id="tabs${subBudgetCounter.count}" class='accordionBorder'>
			
			<div class="clear accordionWrapper">
			<div id='budgetSummary${subBudgetCounter.count}'>
			<h3>Budget Summary</h3>
			Budget Summary JQGrids display here</div>

			
			</div>
			</div>
			</div>


			</div>
			</div>
		</c:forEach>

		
	</form:form>
	
</d:content>

