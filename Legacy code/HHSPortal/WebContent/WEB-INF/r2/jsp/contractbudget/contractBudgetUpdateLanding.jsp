<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%--R7 Start: --%>
<%@taglib prefix="im" uri="/WEB-INF/tld/informationMessage.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%> 
<%--R7 End --%>
<%@page import="com.nyc.hhs.constants.HHSConstants"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@page import="com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ taglib prefix="fmtMessages" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>

<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<fmt:setLocale value="en_US"/>
<%-- The Contract Budget Update page gives an overview of the contract Update information,
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
    <c:choose>
  <%-- Release 5 changes --%>
<c:when test="${(org_type ne 'provider_org') or ((org_type eq 'provider_org') and (contractInfo.providerOrgId eq user_organization) and (contractInfo.contractAccess))}">
<%-- Release 5 changes Ends --%>
<%--  portlet tags to call different grids --%>
<portlet:resourceURL var='showCBGridTabs' id='showCBModificationGridTabs'
	escapeXml='false'>
</portlet:resourceURL>
<%--  portlet tags for ajax call--%>
<portlet:resourceURL var='getCallBackData' id='getCallBackData' escapeXml='false'>
</portlet:resourceURL>
<input type = 'hidden' value='${getCallBackData}' id='getCallBackData'/>
<input type='hidden' value='${org_type}' id='hiddenLoggedInOrgType' />

<%-- Added in R6 --%>
<%-- The following attribute is changed from existingBudget to usesFte --%>
<input type="hidden" name="existingBudget" id="existingBudget" value="${contractInfo.usesFte}"/>
<%--  Start: Added in Defect-8470 --%>
<input type='hidden' value='' id='hiddenReadOnlyPageAttribute' />
<%--  End: Added in Defect-8470 --%>
<%--  portlet tags for save button --%>
<portlet:resourceURL var="saveContractBudgetUpdate" id="saveContractBudgetUpdate" escapeXml="false">
</portlet:resourceURL>

<input type="hidden" name="saveContractBudgetUpdate" id="saveContractBudgetUpdateUrl" value="${saveContractBudgetUpdate}" />

<%--  portlet tags for submit button --%>
<portlet:resourceURL var="submitContractBudgetUpdateOverlay" id="submitContractBudgetUpdateOverlay" escapeXml="false">
</portlet:resourceURL>
<!-- R6 changes starts-->
<portlet:resourceURL var='getCallBackContractBudgetData' id='getCallBackContractBudgetData' escapeXml='false'>
</portlet:resourceURL>
<input type = 'hidden' value='${getCallBackContractBudgetData}' id='getCallBackContractBudgetData'/>
<!-- R6 changes ends-->
<input type="hidden" name="submitContractBudgetUpdateOverlay" id="submitContractBudgetUpdateOverlayUrl" value="${submitContractBudgetUpdateOverlay}"/>

<fmtMessages:setBundle basename="com/nyc/hhs/properties/messages"/>
<input type='hidden' value='${showCBGridTabs}' id='hiddenCBGridTagURL' />
<input type='hidden' value='${contractInfo.budgetStatus}' id='hiddenTaskStatus' />
<%--  Start: Added in R7: Program Income --%>
<input type='hidden' value='${contractInfo.oldPIFlag}' id='hiddenIsOldPI' />
<c:if test="${(fn:contains(entryTypeId, '11:1')) or (fn:contains(entryTypeId, '11:0'))}">
<input type='hidden' value='true' id='hiddenIsPiSelected' />
</c:if>
<%--  End R7: Program Income --%>
<c:set var="subAmounttotal" value="0"></c:set>
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if
	test="${contractBudgetReadonly eq 'true'||(accessScreenEnable eq false) }">
	<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>
<%--<c:if test="${accessScreenEnable eq false}">
	<div class="failed" id="screenlockdiv" style="display:block">This screen is locked by ${lockedByUser}</div>
</c:if>--%>

<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/accordianFunctions.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/contractUpdate.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/unsaveData.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/autoNumeric-1.7.5.js"></script>

<script type='text/javascript'>
	// JS objects for messages for JSP validations
		// JS objects for ids
	var budgetID = "${aoHashMap.budgetId}";
	var contractID = "${aoHashMap.contractId}";
	var fiscalYearID = "${aoHashMap.fiscalYearID}";
	var ctId = "${aoHashMap.ctId}";
	var errorMessage = "${errorMessage}";
	var successMessage = "${successMessage}";
	var isGridEditFn = "${readOnlyPageAttribute}";
	var isScreenLocked = "${accessScreenEnable}";
	
	$(document)
			.ready(
					function() {
						$(function() {
							//Start :Added in Defect-8470
							$('#hiddenReadOnlyPageAttribute').val('${readOnlyPageAttribute}');
							//End :Added in Defect-8470
							$("#accordion").accordion();
							$("#returnAmt").jqGridCurrency();
							<c:forEach var="subBudgetData"   items="${BudgetAccordianData}" varStatus="subBudgetCounter"  >
							$('#tabs${subBudgetCounter.count} li')
									.removeClass(
											'ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
							$('#tabs${subBudgetCounter.count}').tabs();
							</c:forEach>
						});
						//  code updation for R4 starts
						//Starts Logic For EntityTypeTab Show 
						showSelectedEntiTypeTabs();
						//Ends Logic For EntityTypeTab Show
						//code updation for R4 starts
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
	}
	/**code updation for R4 ends
	* This function is called on click of view Comments History tab on task footer
	* Release 3.6.0 Enhancement id 6484
	* Updated  in R7 : Added hiddenIsOldPI for Program Income
	**/
	function showCBGridTabsJSP(tabName, tabId, subbudgetId, parentSubBudgetId) {
		pageGreyOut();
		var v_parameter = "hdnTabName=" + tabName +"&hdnTabId=" + tabId+ "&hdnSubBudgetId="
				+ subbudgetId + "&hdnParentSubBudgetId=" + parentSubBudgetId + "&hiddenIsOldPI=" + $('#hiddenIsOldPI').val()
				+ "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
		if ('${readOnlyPageAttribute}' != "false") {
			v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
					+ subbudgetId + "&readOnlyPage=" + "&hdnParentSubBudgetId=" + parentSubBudgetId 
					+ "&hiddenIsOldPI=" + $('#hiddenIsOldPI').val()
					+ "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
		}
		var urlAppender = $("#hiddenCBGridTagURL").val();

		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				$("#" + tabId).html(e);
				//code updation for R4 starts
				if(tabName == "modificationBudgetSummary" && $("#hiddenLoggedInOrgType").val() == 'provider_org'){
					highlightProviderTabsWithAgencyCommentsCBU(tabId, subbudgetId);
				}
				//code updation for R4 ends
				removePageGreyOut();
			},
			beforeSend : function() {
			}
		});
	}

	//R4: Tab Level Comments - This function highlights the Line Item Tabs that have Agency Comments - Contract Budget Update
	function highlightProviderTabsWithAgencyCommentsCBU(tabId, subbudgetId)
	{
		if ($("#hiddenTaskStatus").val()=="Returned for Revision")
		{
			subBudgetCounterNum = tabId.split('budgetSummary');
			var subBudgetHighlightTabs = $("input[name=hdnTabHighlightList"+subbudgetId+"]").val();
			subBudgetHighlightTabs = subBudgetHighlightTabs.substring(1,subBudgetHighlightTabs.length-1);
			subBudgetHighlightTabsList = subBudgetHighlightTabs.split(', ');
			for(var count = 0; count<subBudgetHighlightTabsList.length; count++)
				$('#'+subBudgetHighlightTabsList[count]+'_'+subBudgetCounterNum[1]+' a').addClass('highlightTlcTabs');
		}
	}
	
	/**
	* This method is added in Release 6.This function is call on click of grid tabs to show the Personnel Services tab.
	* This method is moved from personnelServicesUpdateTab.jsp as part of defect 8465.
	**/
	function showPsSCreen(tabName, tabId, subbudgetId, parentSubBudgetId) {
		pageGreyOut();
		var v_parameter = "hdnTabName=" + tabName +"&hdnTabId=" + tabId+ "&hdnSubBudgetId="
				+ subbudgetId + "&hdnParentSubBudgetId=" + parentSubBudgetId+ "&hiddenIsOldPI=" 
				+ $('#hiddenIsOldPI').val() + "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
		if ($('#hiddenReadOnlyPageAttribute').val() != "false") {
			v_parameter = "hdnTabName=" + tabName + "&hdnSubBudgetId="
					+ subbudgetId + "&readOnlyPage=" + "&hdnParentSubBudgetId=" + parentSubBudgetId+ "&hiddenIsOldPI=" + $('#hiddenIsOldPI').val()
					+ "&hiddenIsPiSelected=" + $('#hiddenIsPiSelected').val();
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
<style>
.alertBoxAddSite .tabularContainer .formcontainer .row span.formfield {
    width: 48% !important 
}
.alertBoxAddSite .formcontainer label.error{
	float:left
}
</style>
<c:set var="sectionSubmitButton"><%=HHSComponentMappingConstant.PROVIDER_CONTRACT_BUDGET_PAGE_SUBMIT%></c:set>

<c:if test="${accessScreenEnable eq false}">
	<c:set var="readOnlyPageAttribute" value="true"></c:set>
	<%--code updation for R4 starts--%>
	<div class="lockMessage" style="display:block">${lockedByUser} currently has edit access to this record. This record will be displayed in Read-only format.</div>
		<%--code updation for R4 ends--%>
</c:if>
<d:content section="<%=ComponentMappingConstant.HEADER_AGENCYF%>"  isReadOnly="${readOnlyPageAttribute}" authorize="">
	<h2><label class='floatLft'>Contract Budget - Update</label>
	<div class="linkReturnValut floatRht"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=true&_urlType=action&_windowLabel=portletInstance_37&toAction=budgetListAction" title="Return to Budget List"">Return to Budget List</a></div>		
	</h2>
	<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
    <d:content section="${helpIconProvider}">
     <div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
          <input type="hidden" id="screenName" value="Budget Update" name="screenName"/>
   </d:content> 
	<div class='hr'></div>
	<%
		String lsTransactionMsg = "";
		if (null!=request.getAttribute("transactionStatus") && "success".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){
			lsTransactionMsg = (String)request.getAttribute("transactionMessage");
		%>
			<div id="transactionStatusDiv" class="passed" style="display: block"><%=lsTransactionMsg%></div>
		<%}%>
	<% String lsErrorMessage = "";
		if(null != request.getAttribute("errorMessage")){
			lsErrorMessage = (String) request.getAttribute("errorMessage");
		%>
			<div class="failed breakAll" style="display:block" id="error"><%=lsErrorMessage%></div>			
	<%} %>	
	<div class="failed" id="errorGlobalMsg"></div>
	<div class="passed" id="successGlobalMsg"></div>
	
	<div class='floatRht'>Status: ${contractInfo.budgetStatus}</div>
	<div class='clear'></div>
	<%-- R7 Start: Added for contract level message--%>
	<c:if test="${org_type eq 'agency_org' || org_type eq 'city_org'}">
	<im:message contractId="${aoHashMap.contractId}"/>
	</c:if>
	<%-- R7 End:--%>
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
	<div class="row"><span class="label"
		title="The CT# is the contract registration number issued by the City's Financial Management System (FMS). You can use this number to search for additional information in the FMS' Payee Information Portal - https://nyc.gov//pip">CT#:</span>
	<span class="formfield">${contractInfo.extCT} </span></div>
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

<div id="assignAdvanceId">
	<jsp:include page="/WEB-INF/r2/jsp/contractbudget/contractUpdateFYBudget.jsp" />
</div>

	<div class="buttonholder"><c:if
		test="${readOnlyPageAttribute ne 'true' }">
		<input type="button" title='Save' value="Save" onclick="onSaveClick();"/>
		<d:content section="${sectionSubmitButton}">
			<input type="button" title='Submit' id="BudgetSubmitId" value="Submit"  />
		</d:content>
	</c:if></div>

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
			<div id="tabs${subBudgetCounter.count}" class=''>
			<input type="hidden" id="hdnGridDivId" value="budgetSummary${subBudgetCounter.count}"/>
			<input type="hidden" id="hdnGridSubBudgetId" value="${subBudgetData.subBudgetID}"/>
			<input type="hidden" id="hdnGridParentSubBudgetId" value="${subBudgetData.parentSubBudgetId}"/>
			<ul class='procurementTabber'>
				<li><a href='#budgetSummary${subBudgetCounter.count}'
					title='Budget Summary' jspname='modificationBudgetSummary'>Budget Summary</a></li>
						<%--code updation for R4 starts--%>
						<%-- R6 change Starts --%>
						<c:choose>
						<c:when test="${contractInfo.usesFte eq 0}">
						<%--  Start: Update in Defect-8470 --%>
						<li id="1_${subBudgetCounter.count}"><a
													href='#budgetSummary${subBudgetCounter.count}'
													jspname='personnelServicesUpdateTab'>Personnel
														Services</a></li>
						<%--  End: Update in Defect-8470 --%>
						</c:when>
						<c:otherwise>
						<li id="1_${subBudgetCounter.count}"><a
													href='#budgetSummary${subBudgetCounter.count}'
													jspname='personnelServicesUpdate'>Personnel Services</a></li>
						</c:otherwise>
						</c:choose>
						<%-- R6 change Ends --%>
				<li id="2_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}'
					title='Operations &amp; Support' jspname='updateOperationAndSupport'>Operations &amp; Support</a></li>
				<li id="3_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}'
					title='Utilities' jspname='contractBudgetUpdateUtilities'>Utilities</a></li>
				<li id="4_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}'
					title='Professional Services' jspname='updateProfessionalServices'>Professional Services</a></li>
				<li id="5_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}' 
					title='Rent' jspname='rentUpdate'>Rent</a></li>
				<li id="6_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}'
					title='Contracted Services' jspname='contractedServicesUpdate'>Contracted Services</a></li>
				<li id="7_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}' 
					title='Rate' jspname='contractBudgetUpdateRate'>Rate</a></li>
				<li id="8_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}'
					title='Milestone' jspname='milestoneUpdate'>Milestone</a></li>
				<li id="9_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}'
					title='Unallocated Funds' jspname='unallocatedFundsUpdate'>Unallocated Funds</a></li>
				<li id="10_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}'
					title='Indirect Rate' jspname='updateIndirectRate'>Indirect Rate</a></li>
				<li id="11_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}'
					title='Program Income' jspname='programIncomeUpdate'>Program Income</a></li>
						<%--code updation for R4 ends--%>
				<!-- Start: Added in R7 for Cost Center -->
				<c:if test="${contractInfo.costCenterOpted eq 2}">
				<li id="12_${subBudgetCounter.count}"><a href='#budgetSummary${subBudgetCounter.count}'
					 jspname='servicesUpdate'>Services</a></li>
				</c:if>
				<!-- End: Added in R7 for Cost Center -->
			</ul>
			<div class="clear accordionWrapper">
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
			onclick="displayAccordion(this);if(divEmpty('assignmentWrapper')){showCBGridTabsJSP('advance', 'advanceWrapper', '','');}">
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
	</form:form>
	<div id="tempDocument" style="display:none"><jsp:include
			page="/WEB-INF/r2/jsp/tasks/document.jsp" /></div>
	<p class='clear'>&nbsp;</p>
	<c:set var="entityType"><%=HHSConstants.AUDIT_CONTRACT_BUDGET_UPDATE%></c:set>
	<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_BUDGET_UPDATE%></c:set>
	<div class='gridFormField'>
	<%-- Updated in R6--%>
		<task:taskContent
		workFlowId=""
		taskType="${entityTypeForAgency}"
		entityType="${entityType}"
		entityTypeForAgency="${entityTypeForAgency}"
		level="footer"
		textAreaSize="3000">
		</task:taskContent>
	</div>
</d:content>

	<%--  Overlay popup starts --%>
<div class="overlay"></div>
<%-- Overlay Pop up Starts --%>
	<div class="alert-box alertBoxAddSite">
	    <div class="tabularCustomHead">Add/Edit Site Information</div>
	    <div class="tabularContainer">
	    	<form name="addEditSiteForm" id="addEditSiteForm" action="">
				<h2 class='autoWidth'>Add/Edit Site Information</h2>
				<div class='hr'></div>
				<c:if test="${message ne null}">
					<div class="${messageType}" id="messagediv" style="display:block">${message}<img src="../framework/skins/hhsa/images/iconClose.jpg" id="box" class="message-close"  onclick="showMe('messagediv', this)"></div>
				</c:if>
			    	<div>&nbsp;</div>
				<div class="formcontainer">
					<div class="row">
						  <span class="label equalForms"><label><span class="required">*</span>Site Name:</label></span>
						  <span class="formfield equalForms"><input name="siteNameOverlay" maxlength="90" type="text" class="input" id="siteNameOverlay"/></span>
					</div>
					<div class="row">
						  <span class="label equalForms"><label><span class="required">*</span>Address 1:</label></span>
						  <span class="formfield equalForms"><input name="address1Overlay" maxlength="60" type="text" class='input' id="address1Overlay" /></span>
					</div>
					<div class="row">
						  <span class="label equalForms"><label>Address 2:</label></span>
						  <span class="formfield equalForms"><input name="address2Overlay" maxlength="60" type="text" class='input' id="address2Overlay" /></span>
					</div>
					<div class="row">
						  <span class="label equalForms"><label><span class="required">*</span>City:</label></span>
						  <span class="formfield equalForms"><input name="cityOverlay" maxlength="40" type="text" class='input' id="cityOverlay" /></span>
					</div>
					<div class="row">
						  <span class="label equalForms"><label><span class="required">*</span>State:</label></span>
						  <span class="formfield equalForms">
						  	<select name="stateOverlay" class='widthFull' id="stateOverlay">
						  		<option value=" "  selected="selected"> </option><option value="AK" >AK</option><option value="AL" >AL</option><option value="AR" >AR</option><option value="AS" >AS</option><option value="AZ" >AZ</option><option value="CA" >CA</option><option value="CO" >CO</option><option value="CT" >CT</option><option value="DC" >DC</option><option value="DE" >DE</option><option value="FL" >FL</option><option value="GA" >GA</option><option value="GU" >GU</option><option value="HI" >HI</option><option value="IA" >IA</option><option value="ID" >ID</option><option value="IL" >IL</option><option value="IN" >IN</option><option value="KS" >KS</option><option value="KY" >KY</option><option value="LA" >LA</option><option value="MA" >MA</option><option value="MD" >MD</option><option value="ME" >ME</option><option value="MI" >MI</option><option value="MN" >MN</option><option value="MO" >MO</option><option value="MP" >MP</option><option value="MS" >MS</option><option value="MT" >MT</option><option value="NC" >NC</option><option value="ND" >ND</option><option value="NE" >NE</option><option value="NH" >NH</option><option value="NJ" >NJ</option><option value="NM" >NM</option><option value="NV" >NV</option><option value="NY" >NY</option><option value="OH" >OH</option><option value="OK" >OK</option><option value="OR" >OR</option><option value="PA" >PA</option><option value="PR" >PR</option><option value="RI" >RI</option><option value="SC" >SC</option><option value="SD" >SD</option><option value="TN" >TN</option><option value="TX" >TX</option><option value="UT" >UT</option><option value="VA" >VA</option><option value="VI" >VI</option><option value="VT" >VT</option><option value="WA" >WA</option><option value="WI" >WI</option><option value="WV" >WV</option><option value="WY" >WY</option>
						  	</select>
						  </span>
					</div>
					<div class="row">
						  <span class="label equalForms"><label><span class="required">*</span>Zip Code:</label></span>
						  <span class="formfield equalForms"><input name="zipcodeOverlay" maxlength="5" type="text" class='input' validate="number" id="zipcodeOverlay" /></span>
					</div>
					<input type="hidden" id="addressRelatedData" />
					<input type="hidden" id="indexOpened" />
					
					<input type="hidden" id="hdnTabIdForSite"  value="${subBudgetCounter.count}"/>
					<input type="hidden" id="subBudgetIdForSite"  value="${subBudgetId}"/>
					<input type="hidden" id="parentSubBudgetIdForSite"  value="${parentSubBudgetId}"/>
					
				</div>
			    <div class="buttonholder">
			    	<input type="button" class="graybtutton"  value="Cancel" id="cancelOverlay"/>
			    	<input type="submit" class="button"  value="Save" id="saveOverlay"/>
			    </div>
			</form>
	    </div>
	    <a href="javascript:void(0);" class="exit-panel exit-panel-add-site"></a> 
	</div>
	<%-- Overlay Pop up Ends --%>
	
	<%-- Pop up start for Address Validation --%>
	<div class="alert-box alert-box-address">
	   <div id="newTabs">
	   		<div class="tabularCustomHead">Address Validation</div>
	  		<div id="addressDiv" class='evenRows'></div>
	  </div>
	  <a href="javascript:void(0);" class="exit-panel address-exit-panel" >&nbsp;</a>
	</div>
	<%-- Pop up Ends for Address Validation --%>
  <div class="alert-box-help">
				<div class="tabularCustomHead toplevelheaderHelp"></div>
	            <div id="helpPageDiv"></div>
		  		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
			</div>
			<div class="alert-box-contact">
				<div id="contactDiv"></div>
			</div>
<div class="alert-box-submit-contract" id="overlayDivId"></div>
<input type='hidden' value='${subAmounttotal}' id='hiddenSubAmountTotal' />

<script type="text/javascript">

</script>


</c:when>
<c:otherwise>
<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
</c:otherwise>
</c:choose>