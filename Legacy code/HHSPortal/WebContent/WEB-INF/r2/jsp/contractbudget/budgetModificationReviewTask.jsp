<%-- This jsp display for agency user for reviewing modification budget --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page import="com.nyc.hhs.constants.HHSConstants"%>
<%@page import="com.nyc.hhs.constants.HHSR5Constants"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@ taglib prefix="fmtMessages" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>

<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%--R7 Start: --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%> 
<%--R7 End --%>
<%--Added in R7: Navigate to contract budget screen --%>
<portlet:actionURL var='navigateListScreenUrl' escapeXml='false'>
	<portlet:param  name='controllerAction' value='redirectFromLanding'/>
</portlet:actionURL>
<input type="hidden" name="hiddenNavigateListScreenUrl" id="hiddenNavigateListScreenUrl"  value="${navigateListScreenUrl}"/>
<%--End in R7: Navigate to contract budget screen --%>

<%-- Start changes for R5 --%>
<%@include file="/WEB-INF/r5/jsp/assigneeListMapping.jsp" %>
<portlet:resourceURL var="setDefaultUser" id="setDefaultUser"></portlet:resourceURL>
<input type="hidden" id="setDefaultUser" value="${setDefaultUser}"/>
<%-- End changes for R5 --%>
<%-- This portlet resource is for calling showbudgetModificationTabJsp method to show tab jsp--%>
<portlet:resourceURL var='showCBGridTabs' id='showCBModificationGridTabs'
	escapeXml='false'>
</portlet:resourceURL>
<%-- This portlet resource is for saving modification budget --%>
<portlet:resourceURL var="saveContractBudget" id="saveContractBudget" escapeXml="false">
</portlet:resourceURL>
<portlet:resourceURL var='getCallBackData' id='getCallBackData' escapeXml='false'>
</portlet:resourceURL>
<input type = 'hidden' value='${getCallBackData}' id='getCallBackData'/>
<input type="hidden" name="saveContractBudget" id="saveContractBudgetUrl" value="${saveContractBudget}" />
<%-- This portlet resource is for Confirmation Popup mapping --%>
<portlet:resourceURL var="submitContractBudgetOverlay" id="submitContractBudgetOverlay" escapeXml="false">
</portlet:resourceURL>
<input type="hidden" name="submitContractBudgetOverlay" id="submitContractBudgetOverlayUrl" value="${submitContractBudgetOverlay}"/>
<%-- Start: Changes for 8441 --%>
<portlet:resourceURL var='getCallBackContractBudgetData' id='getCallBackContractBudgetData' escapeXml='false'>
</portlet:resourceURL>
<input type = 'hidden' value='${getCallBackContractBudgetData}' id='getCallBackContractBudgetData'/>
<%-- End: Changes for 8441 --%>
<!-- Added in R6 -->
<!-- The following attribute is changed from existingBudget to usesFte -->
<input type="hidden" name="existingBudget" id="existingBudget" value="${contractInfo.usesFte}"/>

<fmtMessages:setBundle basename="com/nyc/hhs/properties/messages"/>
<input type='hidden' value='${showCBGridTabs}' id='hiddenCBGridTagURL' />
<%--  Start: Added in R7: Program Income --%>
<input type='hidden' value='${contractInfo.oldPIFlag}' id='hiddenIsOldPI' />
<c:if test="${(fn:contains(entryTypeId, '11:1')) or (fn:contains(entryTypeId, '11:0'))}" >
<input type='hidden' value='true' id='hiddenIsPiSelected' />
</c:if>
<%--  End R7: Program Income --%>
<c:set var="subAmounttotal" value="0"></c:set>
<c:set var="readOnlyPageAttribute" value="true"></c:set>


<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/accordianFunctions.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/contractModification.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/unsaveData.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>
<%-- Start changes for R5 --%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/defaultAssignee.js"></script>
<%-- End changes for R5 --%>
<script type='text/javascript'>
	// JS objects for messages for JSP validations
		// JS objects for ids
	var budgetID = "${aoHashMap.budgetId}";
	var contractID = "${aoHashMap.contractId}";
	//Added in R6
	var isGridEditFn = "${readOnlyPageAttribute}";
	var errorMessage = "${errorMessage}";
	var successMessage = "${successMessage}";
	

	$(document)
			.ready(
					function() {
						$(function() {
							$("#accordion").accordion();
							//Starts : R6 Format returned payment amount displayed on accordian 
							$("#returnAmt").jqGridCurrency();
							//Ends : R6 Format returned payment amount displayed on accordian 
							<c:forEach var="subBudgetData"   items="${BudgetAccordianData}" varStatus="subBudgetCounter"  >
							$('#tabs${subBudgetCounter.count} li')
									.removeClass(
											'ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
							$('#tabs${subBudgetCounter.count}').tabs();
							</c:forEach>
							// $('#commentsTabs li').addClass('ui-tabs-selected');
						});
						
						//Starts Logic For EntityTypeTab Show 
						showSelectedEntiTypeTabs();
						//Ends Logic For EntityTypeTab Show
						
						$("#documentWrapper").html( $("#tempDocument").html());
							$("#tempDocument").html("");
						if('${readOnlyPageAttribute}' != "false"){
						  $("#taskButtonsId").hide();
						 }				

					});
						//code updation for R4 starts
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
			for(var i=0; i<entryTypeData.length; i++)
				$('#'+$.trim(entryTypeData[i]).split(':')[0]+'_'+_counter[j]).show();
	}					//updation for R4 ends

	/**
	* This function is called on click of view Comments History tab on task footer
	* Updated in R6 : Returned Payment, added budget id in parameters
	* Updated in R7 :Added hiddenIsOldPI for Program Income
	**/
	function showCBGridTabsJSP(tabName, tabId, subbudgetId, parentSubBudgetId) {
		pageGreyOut();
		var v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
				+ subbudgetId + "&hdnParentSubBudgetId=" + parentSubBudgetId  + "&budgetID="
				+ budgetID + "&hiddenIsOldPI=" + $('#hiddenIsOldPI').val()+ "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
		if ('${readOnlyPageAttribute}' != "false") {
			v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
					+ subbudgetId + "&readOnlyPage=" + "&hdnParentSubBudgetId=" + parentSubBudgetId  + "&budgetID="
				+ budgetID + "&hiddenIsOldPI=" + $('#hiddenIsOldPI').val()+ "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
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
	
	/**
	* This method is added in Release 6.This function is call on click of grid tabs to show the Personnel Services tab.
	* This method is moved from personnelServicesModificationTab.jsp as part of defect 8465.
	**/
	function showPsSCreen(tabName, tabId, subbudgetId, parentSubBudgetId) {
		pageGreyOut();
		var v_parameter = "hdnTabName=" + tabName +"&hdnTabId=" + tabId+ "&hdnSubBudgetId="
				+ subbudgetId + "&hdnParentSubBudgetId=" + parentSubBudgetId+ "&hiddenIsOldPI=" 
				+ $('#hiddenIsOldPI').val()+ "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
		if ($('#hiddenReadOnlyPageAttribute').val() != "false") {
			v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
					+ subbudgetId + "&readOnlyPage=" + "&hdnParentSubBudgetId=" + parentSubBudgetId+ "&hiddenIsOldPI=" 
					+ $('#hiddenIsOldPI').val()+ "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
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
<c:set var="sectionName"><%=HHSComponentMappingConstant.AGENCY_S396_PAGE%></c:set>
	 	<!--R7 Start: added for Auto-Approval Message  -->
<c:if test="${org_type eq 'agency_org' || org_type eq 'city_org'}">  
	<c:if test="${fiscalBudgetInfo.budgetModification eq 'true'}"> 
	 		<div class="clear"></div>
	 		<div class="infoMessage" style="display:block;margin-bottom:2px;"><%=HHSR5Constants.APPROVED_MODIFICATION_MESSAGE%>&nbsp;
				<a class="activelink" style="text-decoration: underline;" href="javascript:void(0);" onclick="submitFormToModificationBudgetDetails('${aoHashMap.contractId}','${aoHashMap.budgetId}');">Budget List.</a>
	 		</div>
</c:if> 	 
</c:if>
	<!--R7 End: added for Auto-Approval Message  -->
<div class="complianceWrapper"> 
<d:content   section="${sectionName}" authorize="" isReadOnly="${accessScreenEnable eq false}">
<task:taskContent workFlowId="" taskType="taskBudgetModification" isTaskScreen="" level="header" taskDetail="" ></task:taskContent>
<d:content  isReadOnly="true"  >
	<h2 class='autoWidth'>Contract Budget</h2>
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
			<th class='alignCenter'>FY Budget</th>
			<th class='alignCenter'>YTD Invoiced Amount</th>
			<th class='alignCenter'>Remaining Amount</th>
			<th class='alignCenter'>YTD Actual Paid Amount</th>
			<!-- Fix for defect 8557 R6 : Returned payment-->
			<th class='alignCenter'>Unrecouped Advance Amount</th>
		</tr>
		<tr>
			<td><fmt:formatDate pattern="MM/dd/yyyy"
				value="${fiscalBudgetInfo.startDate}" /></td>
			<td><fmt:formatDate pattern="MM/dd/yyyy"
				value="${fiscalBudgetInfo.endDate}" /></td>
			<td class='alignRht'><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.approvedBudget}" /></td>
			<td class='alignRht'><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.invoicedAmount}" /></td>
			<td class='alignRht'><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.remainingAmount}" /></td>
			<td class='alignRht'><fmt:formatNumber type="currency"
				value="${fiscalBudgetInfo.ytdActualPaid}" /></td>
			<!-- Fix for defect 8557 R6 : Returned payment-->
			<td class='alignRht'><fmt:formatNumber type="currency"
			value="${fiscalBudgetInfo.unRecoupedAmount}" /></td>
		</tr>
	</table>
	</div>

	<p>&nbsp;</p>
	<form:form action="" method="post" name="contractBudgetForm"
		id="contractBudgetForm">
		<c:forEach var="subBudgetData" items="${BudgetAccordianData}"
			varStatus="subBudgetCounter">
			<div id="accordionTopId">
			<div class="accrodinWrapper hdng" id="accordionHeaderId"
				onclick="displayAccordion(this);if(divEmpty('budgetSummary${subBudgetCounter.count}')){showCBGridTabsJSP('modificationBudgetSummary','budgetSummary${subBudgetCounter.count}','${subBudgetData.subBudgetID}','${subBudgetData.parentSubBudgetId}');}">
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
			<input type="hidden" id="hdnGridDivId" value="budgetSummary${subBudgetCounter.count}"/>
			<input type="hidden" id="hdnGridSubBudgetId" value="${subBudgetData.subBudgetID}"/>
			<input type="hidden" id="hdnGridParentSubBudgetId" value="${subBudgetData.parentSubBudgetId}"/>
			<ul class='procurementTabber'>
				<li><a href='#budgetSummary${subBudgetCounter.count}'
					jspname='modificationBudgetSummary'
					>Budget
				Summary</a></li>
				<!-- R6 change Starts -->
				<c:choose>
					<c:when test="${contractInfo.usesFte eq 0}">
					<%-- Start: Updated in Defect-8470 --%>
					<li id="1_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}' jspname='personnelServicesModificationTab'>Personnel Services</a></li>
					</c:when>
					<%-- End: Updated in Defect-8470 --%>
					<c:otherwise>
					<li id="1_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}' jspname='personnelServicesModification'>Personnel Services</a></li>
					</c:otherwise>
				</c:choose>
				<!-- R6 change Ends -->
				<li id="2_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}'
					jspname='operationAndSupportModification' 
					>Operations
				&amp; Support</a></li>
				<li id="3_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}'
					jspname='contractBudgetModificationUtilities' 
					>
				Utilities</a></li>
				<li id="4_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}' 
					 jspname='cbmProfessionalServices' 
					>
				Professional Services</a></li>
				<li id="5_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}' jspname='rentModification' 
					>Rent</a></li>
				<li id="6_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}'
						jspname='contractedServicesModification' 
						>Contracted Services
				</a></li>
				<li id="7_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}' jspname='contractBudgetModificationRate' 
					>Rate</a></li>
				<li id="8_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}'
					jspname='milestoneModification' 
					>Milestone</a></li>
				<li id="9_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}'
					jspname='unallocatedFundsModification' 
					>
				Unallocated Funds</a></li>
				<li id="10_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}'
					jspname='modificationIndirectRate' 
					>Indirect
				Rate</a></li>
				<li id="11_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}'
					jspname='programIncomeModification'>Program Income</a></li>
				<!-- Start: Added in R7 for Cost Center -->
				<c:if test="${contractInfo.costCenterOpted eq 2}">
				<li id="12_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}'
					 jspname='servicesModification'>Services</a></li>
				</c:if>
				<!-- End: Added in R7 for Cost Center -->
			</ul>
			<div class="clear accordianWrapper">
			<div id='budgetSummary${subBudgetCounter.count}'>
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
			onclick="displayAccordion(this); if(divEmpty('advanceWrapper')){showCBGridTabsJSP('advance', 'advanceWrapper', '','');}">
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
		<div class="accrodinWrapper hdng" id="accordionHeaderId3"
			onclick="displayAccordion(this);if(divEmpty('assignmentWrapper')){showCBGridTabsJSP('assignments', 'assignmentWrapper', '','');}">
		<h5>Assignments</h5>
		<ul>
			<li><label> &nbsp;</label></li>
		</ul>
		</div>
		<div id="accordianId3" class="close">
		<div id='tabs-container' class="clearHeight">
		<div class="accContainer" id="assignmentWrapper"></div>
		</div>
		</div>
		</div>
		<!-- R6: Returned Payment change Starts -->
		<!-- Fix for defect 8555 -->
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
	<task:taskContent workFlowId="" commentsSection=" " taskType="taskBudgetModification"  isTaskScreen=""  level="footer"></task:taskContent>
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
<div class="alert-box-submit-contract" id="overlayDivId"></div>
<input type='hidden' value='${subAmounttotal}' id='hiddenSubAmountTotal' />

