<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<!--Start Added in R5 -->
<%@include file="/WEB-INF/r5/jsp/assigneeListMapping.jsp" %>
<portlet:resourceURL var="setDefaultUser" id="setDefaultUser"></portlet:resourceURL>
<!-- Start: Added in R7 for Cost Center -->
<portlet:resourceURL var="getCostCenterDetails" id="getCostCenterDetails"></portlet:resourceURL>
<input type="hidden" id="getCostCenterDetails" value="${getCostCenterDetails}"/>
<portlet:resourceURL var="updateCostCenterEnabled" id="updateCostCenterEnabled"></portlet:resourceURL>
<input type="hidden" id="updateCostCenterEnabled" value="${updateCostCenterEnabled}"/>
<portlet:resourceURL var="updateSelectedServices" id="updateSelectedServices"></portlet:resourceURL>
<input type="hidden" id="updateSelectedServices" value="${updateSelectedServices}"/>
<!-- End: Added in R7 for Cost Center -->
<input type="hidden" id="setDefaultUser" value="${setDefaultUser}"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/defaultAssignee.js"></script>
<!--End Added in R5 -->
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>

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
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/contractConfigUpdate.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/unsaveData.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>

<script>
var totalBudgetCannotExceed = "<fmt:message key='TOTAL_FY_PLANNED_AMOUNT_CANNOT_EXCEED'/>";
var totalBudgetNotEqual= "<fmt:message key='TOTAL_FY_PLANNED_AMOUNT_NOT_EQUAL'/>";
// R4 Added ScreenName and BudgetCustomization validation
var screenName="ContractConfigUpdate";
var _BudgetCustomizeError = "<fmt:message key='BUDGET_TEMPATE_SECTION_NONE_CHECKED'/>";
    $(function() {
        $('#contractTabs li, #comments_viewTask li').removeClass('ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
		$( "#contractTabs, #comments_viewTask" ).tabs();
		
    });
    </script>
<c:set var="sectionName"><%=HHSComponentMappingConstant.AGENCY_S391_PAGE%></c:set>
<d:content   section="${sectionName}" authorize="" isReadOnly="${accessScreenEnable eq false}">  

<div class="complianceWrapper"> 
<task:taskContent workFlowId="" taskType="taskContractConfigurationUpdate" isTaskScreen=""  level="header" taskDetail="" ></task:taskContent>
	<d:content isReadOnly="${!detailsBeanForTaskGrid.isTaskAssigned}" > 
<form:form action="${submitAction}" method="post" name="taskForm1" id="taskForm1">
<input type = "hidden" value="${submitAction1}" id="hiddenURL"/>
	<input id="firstTabId" type="hidden" onclick="showContractFinancialsTab()"/>
	<input id="secondTabId"  type="hidden" onclick="showContractBudgetTab('tab')"/>
	<div class='clear'></div>
	
	 <%-- Block for Error Message --%>
		<div class="failed" id="errorGlobalMsg"></div>
	  <%-- Block for Error Message Ends--%>
	
	 <div class='accContainer floatNone'>	
	<%-- Tabs for Financials --%>
	<div id='contractTabs'  class='financeTabbs'>
				<ul id="financeTabs" class=''>
					<li><a href="#oontractFinancials" clickFunction="firstTabId" anothertabId="contractBudget1">Contract Financials</a></li>
					<li><a href="#contractBudget" clickFunction="secondTabId"  anotherTabId="contractBudget1">Contract Budgets</a></li>
				</ul>			
				
			
		 
				<div id='oontractFinancials'>
					<div class="formcontainer paymentFormWrapper widthFull">
						<div class="row">
							  <span class="label">Contract Value:</span>
							  <span class="formfield" id="contractValueId">$<label id="aoContractValue">${aoContractData.amendmentValue}</label></span>
						</div>
						<div class="row">
							  <span class="label">Contract Start Date:</span>
							  <span class="formfield">${aoContractData.updatedContractStartDate}</span>
						</div>
						<div class="row">
							  <span class="label">Contract End Date:</span>
							  <span class="formfield">${aoContractData.updatedContractEndDate}</span>
						</div>
					</div>
					
					<div>&nbsp;</div>
					
					<div id="contractCOAAndFundingSource">
					<portlet:resourceURL var='SubGridHeaderRow' id='mainAccountGrid'
													escapeXml='false'>
					</portlet:resourceURL>
					<portlet:resourceURL var='SubGridAmendmentHeaderRow' id='mainAccountGrid'
													escapeXml='false'>
					<portlet:param name="isAmendmentFlow" value="true"/>
					</portlet:resourceURL>
					
					<portlet:resourceURL var='ContractConfigUpdateGridOperation' id='accountOperationGrid' escapeXml='false'>
					<portlet:param name="screenName" value="ContractConfigUpdateGrid"/>
					<portlet:param name="validationScreen" value="ContractConfigUpdate"/>
					<portlet:param name="isAmendmentFlow" value="true"/>
					</portlet:resourceURL>
				
					<portlet:resourceURL var='loadContractConfigReadOnlyGrid'
						id='subAccountGrid' escapeXml='false'>
					<portlet:param name="screenName" value="contractConfigReadOnlyGrid" />
					</portlet:resourceURL>
					
					<portlet:resourceURL var='ContractConfigUpdateGrid'
							id='subAccountGrid' escapeXml='false'>
							<portlet:param name="screenName" value="ContractConfigUpdateGrid" />
							<portlet:param name="listName" value="UpdateGrid" />
							<portlet:param name="isAmendmentFlow" value="true"/>
					</portlet:resourceURL>
					
					<portlet:resourceURL var='ContractConfigGrid'
						id='subAccountGrid' escapeXml='false'>
						<portlet:param name="screenName" value="ContractConfigGridCurr" />
					</portlet:resourceURL>
					
					<portlet:resourceURL var='ContractConfigGridOperation' id='accountOperationGrid1' escapeXml='false'>
					<portlet:param name="screenName" value="ContractConfigGrid"/>
					</portlet:resourceURL>

                    <portlet:resourceURL var='ContractConfigActualsGrid'
							id='subAccountGrid' escapeXml='false'>
							<portlet:param name="screenName" value="ContractConfigActualsGrid" />
							<portlet:param name="listName" value="ActualGrid" />
					</portlet:resourceURL>
					
					<div>&nbsp;</div>
					
					<h3>Chart of Accounts Allocation - Current</h3>
					<div class='gridFormField gridScroll'>
					<jq:grid id="CoAAllocation" gridColNames="${GridColNames}"
						gridColProp="${MainHeaderProp}" subGridColProp="${SubHeaderProp}"
							gridUrl="${SubGridHeaderRow}" notAllowDuplicateColumn="uobc,subOC,rc"
							subGridUrl="${ContractConfigGrid}" cellUrl="ContractConfigGridOperation" editUrl="ContractConfigGridOperation"
							dataType="json" methodType="POST" columnTotalName="${columnsForTotal}" isCOAScreen="true"
							isPagination="true" rowsPerPage="5" isSubGrid="true" autoWidth="false" isReadOnly="true"
							operations="del:false,edit:false,add:false,cancel:false,save:false" />
					</div>

                      <p>&nbsp;</p>
					
					<h3>Chart of Account Allocation - Actual</h3>	
					<div class='gridFormField gridScroll'>				
					<jq:grid id="CoAAllocationActuals" gridColNames="${GridColNames}"
						gridColProp="${MainHeaderProp}" subGridColProp="${SubHeaderProp}"
							gridUrl="${SubGridHeaderRow}" notAllowDuplicateColumn="uobc,subOC,rc"
							subGridUrl="${ContractConfigActualsGrid}" cellUrl="ContractConfigGridOperation" editUrl="ContractConfigGridOperation"
							dataType="json" methodType="POST" autoWidth="false" columnTotalName="${columnsForTotal}"
							isPagination="true" rowsPerPage="5" isSubGrid="true" isReadOnly="true" isCOAScreen="true"
							operations="del:false,edit:false,add:false,cancel:false,save:false" />
					</div>
							
					<p>&nbsp;</p>
					
					<h3>Chart of Account Allocation - Update</h3>
					<div class='gridFormField gridScroll'>
					<jq:grid id="CoAAllocation1" gridColNames="${AmendmentGridColNames}"
						gridColProp="${AmendmentMainHeaderProp}" subGridColProp="${AmendmentSubHeaderProp}"
						gridUrl="${SubGridAmendmentHeaderRow}" isNewRecordDelete="true"
						subGridUrl="${ContractConfigUpdateGrid}" cellUrl="${ContractConfigUpdateGridOperation}" editUrl="${ContractConfigUpdateGridOperation}"
						dataType="json" methodType="POST" columnTotalName="${columnsForTotalAmendment}"
						isPagination="true" rowsPerPage="5" autoWidth="false" isSubGrid="true" isReadOnly="false"
						notAllowDuplicateColumn="uobc,subOC,rc"
						isCOAScreen="true" nonEditColumnName = "total"
						positiveCurrency="${columnsForTotalAmendment}"
						operations="del:true,edit:true,add:true,cancel:true,save:true" />
					</div>
					
			</div>	
			
			  <p></p>	
			</div>
				    
				<portlet:resourceURL var='contractBudgetPageVar' id='contractConfigUpdateBudget' escapeXml='false'/>	
				<input type="hidden" id="hdnCcontractBudgetPageVar" value="${contractBudgetPageVar}"/>
				
                <div id="contractBudget">
					<div id="contractBudgets1"></div>
				</div>		
				
				
				
			</div>	
			
			
			</div>
	
		
</form:form>
</d:content>
<div class='clear'></div>
	<div class='gridFormField'>
		<task:taskContent workFlowId="" showDocument="" taskType="taskContractConfigurationUpdate" isTaskScreen="" level="footer"></task:taskContent>
	</div>
</div>
	
<div>&nbsp;</div>
<div id="tempDivOverday" class=""></div>
	  <div class="alert-box-help">
			<div class="tabularCustomHead toplevelheaderHelp"></div>
	        <div id="helpPageDiv"></div>
		  	<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
	</div>
</d:content>