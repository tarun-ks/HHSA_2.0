<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@page import="com.nyc.hhs.util.HHSUtil"%>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<%@ page errorPage="/error/errorpage.jsp" %>
<!--Start Added in R5 -->
<%@include file="/WEB-INF/r5/jsp/assigneeListMapping.jsp" %>
<portlet:resourceURL var="setDefaultUser" id="setDefaultUser"></portlet:resourceURL>
<input type="hidden" id="setDefaultUser" value="${setDefaultUser}"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/defaultAssignee.js"></script>
<!--End Added in R5 -->
<!-- Start: Added in R7 for Cost Center -->
<portlet:resourceURL var="getCostCenterDetails" id="getCostCenterDetails">
<portlet:param name="checkForNewFY" value="true" />
</portlet:resourceURL>
<input type="hidden" id="getCostCenterDetails" value="${getCostCenterDetails}"/>
<portlet:resourceURL var="updateCostCenterEnabled" id="updateCostCenterEnabled"></portlet:resourceURL>
<input type="hidden" id="updateCostCenterEnabled" value="${updateCostCenterEnabled}"/>
<portlet:resourceURL var="updateSelectedServices" id="updateSelectedServices">
<portlet:param name="checkForNewFY" value="true" />
</portlet:resourceURL>
<input type="hidden" id="updateSelectedServices" value="${updateSelectedServices}"/>
<!-- End: Added in R7 for Cost Center -->
<style type="text/css">
.alert-box-upload, .alert-box-delete, .alert-box-viewDocumentProperties, .alert-box-addDocumentFromVault {
	position:fixed !important;
	top:25% !important
}

.accContainer .ui-jqgrid .ui-jqgrid-pager .ui-pg-div span.ui-icon {
    margin: 0  2px !important;
}
</style>

<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/newFYConfigurationTask.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/unsaveData.js"></script>
<script>
var totalNotEqual= "<fmt:message key='NEW_FY_CHART_ACCOUNTS_TOTAL_NOT_EQUAL'/>";
var totalBudgetNotEqual= "<fmt:message key='NEW_FY_TOTAL_FY_PLANNED_AMOUNT_NOT_EQUAL'/>";
var totalBudgetCannotExceed = "<fmt:message key='NEW_FY_TOTAL_FY_PLANNED_AMOUNT_CANNOT_EXCEED'/>";
// R4 Added ScreenName and BudgetCustomization validation
var _BudgetCustomizeError = "<fmt:message key='BUDGET_TEMPATE_SECTION_NONE_CHECKED'/>";
var screenName="newFYConfigurationTask";
var _BudgetCustomizeNonMandatoryError = "<fmt:message key='BUDGET_TEMPATE_SECTION_NON_MANDATORY_CHECKED'/>";
$(function() {
	$('#contractTabs li').removeClass('ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
	$('#contractTabs').tabs();
});
</script>
<c:set var="sectionName"><%=HHSComponentMappingConstant.AGENCY_S393_PAGE%></c:set>
<d:content section="${sectionName}" authorize="" isReadOnly="${accessScreenEnable eq false}">

<div class="complianceWrapper">
<task:taskContent workFlowId="" taskType="taskNewFYConfiguration" isTaskScreen="" level="header" taskDetail="" ></task:taskContent>
	<d:content isReadOnly="${!detailsBeanForTaskGrid.isTaskAssigned}" >
<form:form action="${submitAction}" method="post" name="taskForm1" id="taskForm1">
<input type = "hidden" value="${submitAction1}" id="hiddenURL"/>
<input id="firstTabId" type="hidden" onclick="showContractFinancialsTab()"/>
<input id="secondTabId"  type="hidden" onclick="showContractBudgetTab()"/>
	  <div class="clear"></div>
	  <%-- Contract(s) Section Starts Here --%>
	  <div class='accContainer'>
		<div id="contractTabs" class='financeTabbs'>
			<ul id="financeTabs" class=''>
				<li><a href="#contractFinancials" clickFunction="firstTabId" anothertabId="contractBudget1" >Contract Financials</a></li>
				<li><a href="#contractBudgets" clickFunction="secondTabId"  anotherTabId="contractBudget1" >Contract Budgets</a></li>			
			</ul>
			<div class="failed" id="errorGlobalMsg"></div>
			
			<%-- -------- Contract Financial Tab Contents ------- --%>
			<div id="contractFinancials">
				<%-- Form Data Starts --%>

				<div class="formcontainer paymentFormWrapper widthFull">
					 <div class="row">
						  <span class="label">Procurement Value:</span>
						  <span class="formfield">
						  	<span class='lftAmount'><label id="aoProcurementValue">${aoContractData.procurementValue}</label></span>
						  </span>
					 </div>
					<div class="row">
						<span class="label">Contract Value:</span>
						<span class="formfield">
							<span class='lftAmount'><label id="aoContractValue">${aoContractData.contractValue}</label></span></span>
					</div>  
					<input type="hidden" id="newConfigurableYear" value="${lsConfigurableFiscalYear}"/>
					<input type="hidden" id="newConfigurableYearAmount" value="${lsConfigurableFiscalYearAmount}"/>
					<input type="hidden" id="nonEditColname" value="${nonEditColname}"/>
				</div>
				
				<div>&nbsp;</div>

				<portlet:resourceURL var='SubGridHeaderRow' id='mainAccountGrid' escapeXml='false'>
				</portlet:resourceURL>
				<portlet:resourceURL var='NewFYConfigurationGrid' id='subAccountGrid' escapeXml='false'>
					<portlet:param name="screenName" value="newFYConfigurationAccount" />
				</portlet:resourceURL>
				<portlet:resourceURL var='NewFYConfigurationGridOperation' id='accountOperationGrid' escapeXml='false'>
					<portlet:param name="screenName" value="newFYConfigurationAccount"/>
				</portlet:resourceURL>
				<div>&nbsp;</div>
				<h3>Chart of Accounts Allocation</h3>
				<div class='gridFormField gridScroll'>
					<jq:grid id="CoAAllocation" gridColNames="${GridColNames}"
						gridColProp="${MainHeaderProp}" subGridColProp="${SubHeaderProp}"
						gridUrl="${SubGridHeaderRow}" positiveCurrency="${columnsForTotal}"
						subGridUrl="${NewFYConfigurationGrid}" cellUrl="${NewFYConfigurationGridOperation}" 
						editUrl="${NewFYConfigurationGridOperation}" nonEditColumnName="${nonEditColname}"
						dataType="json" methodType="POST" columnTotalName="${columnsForTotal}"
						isPagination="true" rowsPerPage="5" isSubGrid="true" isReadOnly="false"
						notAllowDuplicateColumn="uobc,subOC,rc" autoWidth="false" isCOAScreen="true"
						isNewRecordDelete="true"
						operations="del:true,edit:true,add:true,cancel:true,save:true" />
				</div>
				<p>&nbsp;</p>
				
				<portlet:resourceURL var='SubGridHeaderRow' id='mainFundingGrid' escapeXml='false'>
					<portlet:param name="screenName" value="newFYConfigurationFunding"/>
				</portlet:resourceURL>
				<portlet:resourceURL var='NewFYConfigurationFundingGridOperation' id='fundingOperationGrid' escapeXml='false'>
					<portlet:param name="screenName" value="newFYConfigurationFunding"/>
				</portlet:resourceURL>
				<portlet:resourceURL var='NewFYConfigurationFundingGrid' id='subFundingGrid' escapeXml='false'>
					<portlet:param name="screenName" value="newFYConfigurationFunding"/>
				</portlet:resourceURL>
				
				<h3>Funding Source Allocation (Optional)</h3>
				<p><b>The optional fields below may be used to indicate the funding source allocation at the point of the initial Certification of Funds. These fields are for reference purposes only.</b></p>
				
				<div class='gridFormField gridScroll'>
				<jq:grid id="ContractFundingSourceAllocation" 
				         gridColNames="${FundingGridColNames}" 	
					     gridColProp="${FundingMainHeaderProp}" 
					     subGridColProp="${FundingSubHeaderProp}" 
						 gridUrl="${SubGridHeaderRow}"
						 subGridUrl="${NewFYConfigurationFundingGrid}" cellUrl="${NewFYConfigurationFundingGridOperation}"
					     editUrl="${NewFYConfigurationFundingGridOperation}"
					     dataType="json" methodType="POST"
					     columnTotalName="${columnsForTotal}"
				         isPagination="true"
				         autoWidth="false"
					     rowsPerPage="5"
				         isSubGrid="true"
				         positiveCurrency="${columnsForTotal}"
				         isReadOnly="false" nonEditColumnName="${nonEditColname}"
					     operations="del:false,edit:true,add:false,cancel:true,save:true"/>
			  </div>
			</div>
			<%-- -------- Contract Financial Tab Contents End ------- --%>
			
			
			<%-- -------- Contract Budget Tab Contents Starts ------- --%>
			<portlet:resourceURL var='newFyBudgetPageVar' id='newFYBudgetPage' escapeXml='false'/>	
				<input type="hidden" id="hdnNewFYBudgetPageVar" value="${newFyBudgetPageVar}"/>
				
			<div id="contractBudgets">
				<div id="contractBudgets1"></div>
			</div>
			<%-------- Contract Budget Tab Contents Ends -------%>
		</div>
		</div>
	


<%-- Form Data Ends --%>

<p></p>

</form:form>
</d:content>
<div class='clear'></div>
<div class='gridFormField'>
	<task:taskContent workFlowId="" showDocument=""  taskType="taskNewFYConfiguration" isTaskScreen=""  level="footer"></task:taskContent>
</div>

</div>
<div id="tempDivOverday" class=""></div>
	  <div class="alert-box-help">
			<div class="tabularCustomHead toplevelheaderHelp"></div>
	        <div id="helpPageDiv"></div>
		  	<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
	</div>
</d:content>
