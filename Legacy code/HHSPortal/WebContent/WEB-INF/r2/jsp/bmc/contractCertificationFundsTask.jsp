<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.nyc.hhs.util.HHSUtil"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<!--Start Added in R5 -->
<%@include file="/WEB-INF/r5/jsp/assigneeListMapping.jsp" %>
<portlet:resourceURL var="setDefaultUser" id="setDefaultUser"></portlet:resourceURL>
<input type="hidden" id="setDefaultUser" value="${setDefaultUser}"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/defaultAssignee.js"></script>
<!--End Added in R5 -->
<!-- JSP UPDATED IN R7 to look alike Contract Configuration -->
<!-- Start: Added in R7 for Cost Center -->
<portlet:resourceURL var="getCostCenterDetails" id="getCostCenterDetails">
<portlet:param name="screenReadOnly" value="true"/>
<portlet:param name="renderContractCOF" value="true"/>
<portlet:param name="contractConfigurationTask" value="true"/>
</portlet:resourceURL>
<!-- End: Added in R7 for Cost Center -->
<input type="hidden" id="getCostCenterDetails" value="${getCostCenterDetails}"/>
<!-- End: Added in R7 for Cost Center -->
<script type="text/javascript" src="../resources/js/calendar.js"></script>
<d:content section="<%=HHSComponentMappingConstant.S390_SCREEN%>"  authorize="" isReadOnly="${accessScreenEnable eq false}">
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>

<style type="text/css">
.alert-box-upload, .alert-box-delete, .alert-box-viewDocumentProperties, .alert-box-addDocumentFromVault {
	position:fixed !important;
	top:25% !important
}

.accContainer .ui-widget-content{
	background: none
}
.accContainer .ui-jqgrid .ui-jqgrid-pager .ui-pg-div span.ui-icon {
    margin: 0  2px !important;
}

.formcontainer .row span.error{
	float: right;
	padding: 4px 0;
	text-align: left; 
	color:#D63301;
	width:55%
}
</style>


<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/contractfinancials.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/unsaveData.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
<script>
var totalNotEqual= "<fmt:message key='CHART_ACCOUNTS_TOTAL_NOT_EQUAL'/>";
var totalBudgetNotEqual= "<fmt:message key='TOTAL_FY_PLANNED_AMOUNT_NOT_EQUAL'/>";
// R4 Added ScreenName and BudgetCustomization validation
var _BudgetCustomizeError = "<fmt:message key='BUDGET_TEMPATE_SECTION_NONE_CHECKED'/>";
var screenName = "contractConfigurationTask";
var _BudgetCustomizeNonMandatoryError = "<fmt:message key='BUDGET_TEMPATE_SECTION_NON_MANDATORY_CHECKED'/>";
$(function() {
	 $('#contractTabs li, #comments_viewTask li').removeClass('ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
	 $( "#contractTabs, #comments_viewTask" ).tabs();
});
// This method is added for finish task validation in R7 
	function finishTaskValidation() {
		var returnVal = true;
		var internalCommentVal = "";
		if (document.getElementById("internalCommentArea") != null) {
			internalCommentVal = trim(document
					.getElementById("internalCommentArea").value);
		}
		var taskStatus = $("#finishtaskchild").val();
		if (internalCommentVal == "" && taskStatus == "Returned for Revision") {
			$("#taskErrorDiv").html(
					"Comments must be entered in the comment box");
			$("#taskErrorDiv").show();
			returnVal = false;
		}
		return returnVal;
	}
</script>

<div class="complianceWrapper">
	<task:taskContent workFlowId="" taskType="taskContractCertificationFunds" isTaskScreen="" level="header" taskDetail="" ></task:taskContent>
	<!-- Start: Added in R7 for Read only -->
	<d:content isReadOnly="true" >
	<!-- End: Added in R7 for Read only -->
	<form:form action="${submitAction}" method="post" name="taskForm1" id="taskForm1">
	<input type = "hidden" value="${submitAction1}" id="hiddenURL"/>
					<%--code updation for R4 starts--%>
	<input type = "hidden" value="${isOpenEndedRfpStartEndDateNotSet}" id="isOpenEndedRfpStartEndDateNotSet"/>
	<input type = "hidden" value="${workflowId}" id="workflowId" name="workflowId"/>
	<input type = "hidden" value="${contractId}" id="contractId" name="contractId"/>
						<%--code updation for R4 ends--%>
	<input id="firstTabId" type="hidden" onclick="showContractFinancialsTab()"/>
	<input id="secondTabId"  type="hidden" onclick="showContractBudgetTabNow()"/>	 
	  <div class="clear">&nbsp;</div>
	  					<%--code updation for R4 starts--%>
	  <c:if test="${message ne null}">
				<div class="${messageType}" id="messagediv" style="display:block">${message} <img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" 
					onclick="showMe('messagediv', this)">
				</div>
			</c:if>
			 <c:if test="${financialsMessage ne null}">
				<div class="failed" id="messagediv" style="display:block">${financialsMessage} <img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" 
					onclick="showMe('messagediv', this)">
				</div>
			</c:if>
								<%--code updation for R4 ends--%>
	  <%-- Block for Error Message --%>
			<div class="failed" id="errorGlobalMsg"></div>
	  <%-- Block for Error Message Ends--%>
	  
	<%-- Contract(s) Section Starts Here --%>
	 <div class='accContainer floatNone'>	
		<div id="contractTabs" class='financeTabbs'>
				<ul id="financeTabs" class=''>
					<li><a title="Contract Financials" href="#oontractFinancials" clickFunction="firstTabId" anothertabId="contractBudget1" >Contract Financials</a></li>
					<li><a id="contractBudgetsContractConf" title="Contract Budgets" href="#contractBudgets" clickFunction="secondTabId"  anotherTabId="contractBudget1" >Contract Budgets</a></li>										
				</ul>
				<!-- Start: Added in R7 for Cost Center -->
				<portlet:resourceURL var='contractBudgetPageVar' id='contractBudgetPage' escapeXml='false'>
				<portlet:param name="screenReadOnly" value="true"/>
				</portlet:resourceURL>	
					<input type="hidden" id="hdnCcontractBudgetPageVar" value="${contractBudgetPageVar}"/>
				<!-- End: Added in R7 for Cost Center -->
										<%--R4 updation starts--%>
					<portlet:actionURL var="saveStartEndDate" escapeXml="false">
						<portlet:param name="submit_action" value="updateContractStartEndDateForOpenEndedRfp"/>
					</portlet:actionURL>
				<input type="hidden" name="saveStartEndDate" id="hdnSaveStartEndDate"  value="${saveStartEndDate}"/>
										
										<%--R4 updation ends--%>
				<div id="contractBudgets">
					<div id="contractBudgets1"></div>
				</div>
				<c:if test="${!isOpenEndedRfpStartEndDateNotSet}"><script type="text/javascript">fillContractBudgetTab();</script></c:if>

						<div id='oontractFinancials'>
							<%-- Form Data Starts --%>
							<%-- Start Defect-5901 : Added static text --%>
							<c:if test="${isOpenEndedRfpStartEndDateNotSet}">
					The Contract Start Date and Contract End Date are required before continuing with this task. Please ensure the correct dates are</br>
					entered. They cannot be changed once saved.
					</c:if>
							<%-- End Defect-5901 : Added static text --%>
							<div class="formcontainer paymentFormWrapper widthFull">
								<c:set var="agencyParts"
									value="${fn:split(aoProcurementCOFBean.agencyName, '-')}" />
								<div class="row">
									<span class="label">Agency:</span> <span class="formfield">${agencyParts[1]}
										(${fn:trim(agencyParts[0])})</span>
								</div>
								<div class="row">
									<span class="label">Agency Code:</span> <span class="formfield">${aoProcurementCOFBean.agencyId}</span>
								</div>
								<div class="row">
									<span class="label">Contract Value:</span> <span
										class="formfield"><span id="aoContractValue">${aoProcurementCOFBean.contractValue}</span></span>
								</div>
								<div class="row">
									<span class="label">Procurement/Contract Title:</span> <span
										class="formfield">${aoProcurementCOFBean.procurementTitle}</span>
								</div>
								<div class="row">
									<span class="label">Competition Pool:</span> <span
										class="formfield">${aoProcurementCOFBean.compPoolTitle}</span>
								</div>
								<div class="row">
									<span class="label">Contract Start Date:</span> <span
										class="formfield">${aoProcurementCOFBean.contractStartDate}</span>
								</div>
								<div class="row">
									<span class="label">Contract End Date:</span> <span
										class="formfield">${aoProcurementCOFBean.contractEndDate}</span>
								</div>
								<div class="row">
									<span class="label">Provider:</span> <span class="formfield">${aoProcurementCOFBean.providerName}</span>
								</div>
							</div>

							<div>&nbsp;</div>
							<%--code updation for R4 starts--%>
							<c:choose>
								<c:when test="${isOpenEndedRfpStartEndDateNotSet}">
									<div
										style="width: 340px; margin: 0 auto; margin: 0 auto; float: right;">
										<input type="submit" class="button" value="Save" id="save"
											name="save" title="" style="padding: 5px 30px 5px 30px">
									</div>
								</c:when>
								<c:otherwise>
									<%--code updation for R4 ends--%>
									<div id="contractCOAAndFundingSource">
										<portlet:resourceURL var='mainAccountGrid'
											id='mainAccountGrid' escapeXml='false'>
										</portlet:resourceURL>

										<portlet:resourceURL var='ContractConfigGrid'
											id='subAccountGrid' escapeXml='false'>
											<portlet:param name="screenName" value="ContractConfigGrid" />
										</portlet:resourceURL>

										<portlet:resourceURL var='ContractConfigGridOperation'
											id='accountOperationGrid' escapeXml='false'>
											<portlet:param name="screenName" value="ContractConfigGrid" />
										</portlet:resourceURL>

										<div>&nbsp;</div>

										<h3>Chart of Accounts Allocation</h3>
										<div class='gridFormField gridScroll'>
											<jq:grid id="CoAAllocation" gridColNames="${GridColNames}"
												gridColProp="${MainHeaderProp}"
												subGridColProp="${SubHeaderProp}"
												gridUrl="${mainAccountGrid}"
												positiveCurrency="${columnsForTotal}"
												subGridUrl="${ContractConfigGrid}"
												cellUrl="${ContractConfigGridOperation}"
												editUrl="${ContractConfigGridOperation}" dataType="json"
												methodType="POST" columnTotalName="${columnsForTotal}"
												isPagination="true" rowsPerPage="5" isSubGrid="true"
												isReadOnly="false" notAllowDuplicateColumn="uobc,subOC,rc"
												autoWidth="false" isCOAScreen="true"
												operations="del:true,edit:true,add:true,cancel:true,save:true"
												nonEditColumnName="total"
												callbackFunction="fillContractBudgetTab();" />
										</div>

										<p>&nbsp;</p>

										<portlet:resourceURL var='mainFundingGrid'
											id='mainFundingGrid' escapeXml='false'>
											<portlet:param name="screenName"
												value="contractConfigurationFundingGrid" />
										</portlet:resourceURL>

										<portlet:resourceURL var='contractFundingSourceGrid'
											id='fundingOperationGrid' escapeXml='false'>
											<portlet:param name="screenName"
												value="contractConfigurationFundingGrid" />
										</portlet:resourceURL>

										<portlet:resourceURL var='loadContractFundingSourceGrid'
											id='subFundingGrid' escapeXml='false'>
											<portlet:param name="screenName"
												value="contractConfigurationFundingGrid" />
										</portlet:resourceURL>

										<h3>Funding Source Allocation (Optional)</h3>

										<p>The optional fields below may be used to indicate the
											funding source allocation at the point of the initial
											Certification of Funds. These fields are for reference
											purposes only.</p>

										<div class='gridFormField gridScroll'>
											<jq:grid id="ContractFundingSourceAllocation"
												gridColNames="${FundingGridColNames}"
												gridColProp="${FundingMainHeaderProp}"
												subGridColProp="${FundingSubHeaderProp}"
												gridUrl="${mainFundingGrid}"
												positiveCurrency="${columnsForTotal}"
												subGridUrl="${loadContractFundingSourceGrid}"
												cellUrl="${contractFundingSourceGrid}"
												editUrl="${contractFundingSourceGrid}" dataType="json"
												methodType="POST" columnTotalName="${columnsForTotal}"
												isPagination="true" nonEditColumnName="total"
												rowsPerPage="5" isSubGrid="true" isReadOnly="false"
												autoWidth="false" isCOAScreen="true"
												operations="del:false,edit:true,add:false,cancel:true,save:true"
												callbackFunction="clearAndHideError();" />
										</div>
										<%-- Form Data Ends --%>

										<p></p>
									</div>
								</c:otherwise>
							</c:choose>
						</div>


					</div>
	</div>
</form:form>
</d:content>
<div class='clear'></div>
	<div class='gridFormField'>
		<task:taskContent workFlowId="" taskType="taskContractCertificationFunds" isTaskScreen=""  level="footer"></task:taskContent>
	</div>
</div>
<div id="tempDivOverday" class=""></div>
<div class="alert-box-help">
				<div class="tabularCustomHead toplevelheaderHelp"></div>
	            <div id="helpPageDiv"></div>
		  		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
			</div>	
<div>&nbsp;</div>
</d:content>