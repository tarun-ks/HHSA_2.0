<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ taglib prefix="fmtMessages" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>

<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%--R7 Start: --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%> 
<%--R7 End --%>
<portlet:resourceURL var='showCBGridTabs' id='showCBGridTabs'
	escapeXml='false'>
</portlet:resourceURL>


<portlet:resourceURL var="submitContractBudgetOverlay" id="submitContractBudgetOverlay" escapeXml="false">
</portlet:resourceURL>
<input type="hidden" name="submitContractBudgetOverlay" id="submitContractBudgetOverlay" value="${submitContractBudgetOverlay}"/>
<input type="hidden" value="${subBudgetId}" id="subBudgetId"/>
<fmtMessages:setBundle basename="com/nyc/hhs/properties/messages"/>
<input type='hidden' value='${showCBGridTabs}' id='hiddenCBGridTagURL' />

<%--  Start: Added in R7: Program Income --%>
<input type='hidden' value='${contractInfo.oldPIFlag}' id='hiddenIsOldPI' />
<c:if test="${(fn:contains(entryTypeId, '11:1')) or (fn:contains(entryTypeId, '11:0'))}">
<input type='hidden' value='true' id='hiddenIsPiSelected' />
</c:if>
<%--  End R7: Program Income --%>

<script type='text/javascript'>
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
							showCBGridTabsJSP('contractBudgetSummary','budgetSummary'+$("#subBudgetCounterCount").val(),$("#subBudgetDataSubBudgetID").val());
						});
					});
	
	//This function is called on click of view Comments History tab on task footer
	function showCBGridTabsJSP(tabName, tabId, subbudgetId) {
		pageGreyOut();
		var v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
				+ subbudgetId+ "&hdnIsPrinterFriendly=true"  + "&hiddenIsOldPI=" + $('#hiddenIsOldPI').val()
				+ "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
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
	function openOverlay() {
		var jspName = "submitCBConfirmation";
		var v_parameter = "&jspName=" + jspName + "&budgetID=" + budgetID
				+ "&contractID=" + contractID + "&agencyID=" + "ACS";
		var urlAppender = $("#submitContractBudgetOverlay").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				$("#overlayDivId").html(e);
				$(".overlay").launchOverlayNoClose(
						$(".alert-box-submit-contract"), "850px", null,
						"onReady");
				$("a.exit-panel").click(function() {
					clearAndCloseOverLay();
				});

			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	}
</script>


<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>

	<h2 class='autoWidth'>Contract Budget</h2>
	<div class='hr'></div>
	<% String lsErrorMessage = "";
		if(null != request.getAttribute("errorMessage")){
			lsErrorMessage = (String) request.getAttribute("errorMessage");
		%>
		<input type="text"/>
			<div class="failed breakAll" style="display:block" id="error"><%=lsErrorMessage%></div>			
	<%} %>	
	
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
	<h3>Fiscal Year Budget Information</h3>
	<div class='tabularWrapper'>
	<table cellspacing="0" cellpadding="0" class="grid">
		<tr>
			<th>Start Date</th>
			<th>End Date</th>
			<th>FY Budget</th>
			<th>YTD Invoiced Amount</th>
			<th>Remaining Amount</th>
			<th>YTD Actual Paid Amount</th>
			<th>Unrecouped Advance Amount</th>
		</tr>
		<tr>
			<td><fmt:formatDate pattern="MM/dd/yyyy"
				value="${fiscalBudgetInfo.startDate}" /></td>
			<td><fmt:formatDate pattern="MM/dd/yyyy"
				value="${fiscalBudgetInfo.endDate}" /></td>
			<td><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.approvedBudget}" /></td>
			<td><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.invoicedAmount}" /></td>
			<td><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.remainingAmount}" /></td>
			<td><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.proposedBudget}" /></td>
			<td><fmt:formatNumber type="currency"
					value="${fiscalBudgetInfo.unRecoupedAmount}" /></td>
		</tr>
	</table>
	</div>



	<p>&nbsp;</p>
	<form:form action="" method="post" name="contractBudgetFormPrint"
		id="contractBudgetFormPrint">
		<c:forEach var="subBudgetData" items="${BudgetAccordianData}"
			varStatus="subBudgetCounter">
			<div id="accordionTopId">
			<div class="accrodinWrapper hdng" id="accordionHeaderId">
			<h5 class="breakAll">${subBudgetData.subBudgetName}</h5>
			<ul>
				<li><label> <fmt:formatNumber type="currency"
					value="${subBudgetData.subBudgetAmount}" /> </label></li>
			</ul>
			</div>
		<input type="hidden" value="${subBudgetCounter.count}" id="subBudgetCounterCount">  
		<input type="hidden" value="${subBudgetData.subBudgetID}" id="subBudgetDataSubBudgetID">  
			<div id="accordianId" class="close">
			<div class="accContainer">
			<div id="tabs${subBudgetCounter.count}" class='accordionBorder'>
			<ul class='procurementTabber'>
			</ul>
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
<%--  Overlay popup starts --%>
<div class="overlay"></div>
<div class="alert-box-submit-contract" id="overlayDivId"></div>
