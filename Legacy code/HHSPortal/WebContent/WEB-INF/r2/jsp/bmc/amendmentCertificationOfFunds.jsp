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
<input type="hidden" id="setDefaultUser" value="${setDefaultUser}"/>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<!--End Added in R5 -->
<%-- 
This jsp is used for S392 Amendment Configuration Task- Contract Financials.

After an Amendment is created from the Contract List screen, the system auto-generates
 the Amendment Configuration task. In this task, agency users will be able to enter the
  financial information required for the Amendment Certification of Funds. In the second
   tab within the task, the user will be able to update the amount allocated to each
    Budget for every Fiscal Year with “Active” or “Approved” Budgets where the 
    Chart of Accounts – Amendment amount was updated. The Fiscal year dropdown will 
    dynamically populate with the list of Fiscal Years with existing budgets that were
     affected by the amendment amount allocation.
--%>

<style type="text/css">
.alert-box-upload, .alert-box-delete, .alert-box-viewDocumentProperties, .alert-box-addDocumentFromVault {
	position:fixed !important;
	top:25% !important
}
/* Below is customized,.../Do not include in any css */
.accContainer .ui-jqgrid .ui-jqgrid-pager .ui-pg-div span.ui-icon {
    margin: 0  2px !important;
}
</style>

<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/contractConfigAmendment.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/unsaveData.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>
<!--Start Added in R5 -->
 <script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/defaultAssignee.js"></script>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
<!--End Added in R5 -->
<script>
var totalNotEqual= "<fmt:message key='TOTAL_AMENDMENT_VALUE_CANNOT_EXCEED'/>";
// R4 Added ScreenName
var screenName = "Contract Configuration Amendment";
$(function() {
	$('#contractTabs li').removeClass('ui-state-default ui-corner-top ui-tabs-selected ui-state-active');
	$('#contractTabs').tabs();
});

 //This method is added for finish task validation in R7 
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
<c:set var="sectionName"><%=HHSComponentMappingConstant.AGENCY_S391_PAGE%></c:set>
<d:content   section="<%=HHSComponentMappingConstant.S390_SCREEN%>" authorize="" isReadOnly="${accessScreenEnable eq false}"> 
<c:set var="isNegativeAmendment" value="true"></c:set>
<c:set var="amendmentGrid" value="positive,positiveAmendmentMsg"></c:set>
<c:if test="${aoContractData.amendmentType ne 'positive'}">
<c:set var="isNegativeAmendment" value="false"></c:set>
<c:set var="amendmentGrid" value="negative,negativeAmendmentMsg"></c:set>
</c:if>

<!-- Start: Added in R7 for Cost Center -->
<portlet:resourceURL var="getCostCenterDetails" id="getCostCenterDetails">
<portlet:param name="screenReadOnly" value="true"/>
</portlet:resourceURL>
<input type="hidden" id="getCostCenterDetails" value="${getCostCenterDetails}"/>
<!-- End: Added in R7 for Cost Center -->

<div class="complianceWrapper"> 
	<div class='accContainer floatNone'>
	<task:taskContent workFlowId="" taskType="taskAmendmentCertification" isTaskScreen=""  level="header" taskDetail="" ></task:taskContent>
	<!-- Start: Added in R7 for defect 8703 for Read only -->
	<d:content isReadOnly="true" >
	<!-- End: Added in R7 for Read only -->
	<form:form action="${submitAction}" method="post" name="taskForm1" id="taskForm1">
	<input type = "hidden" value="${submitAction1}" id="hiddenURL"/>
	<input type = "hidden" value="${aoContractData.amendmentType}" id="amendmentType"/>
	<input id="firstTabId" type="hidden" onclick="showContractFinancialsTab()"/>
	<input id="secondTabId"  type="hidden" onclick="showContractBudgetTab(${startFiscalYear},${numberOfYears},'false')"/>
		<div class='clear'></div>
		<%--code updation for R4 starts--%>
		<c:if test ="${aoContractData.amendmentCount gt 1}">
		<div class='infoMessage' style="display:block">There are other amendments in progress for this contract.</div>
		</c:if>
				<%--code updation for R4 ends--%>
		
		<%-- Tabs for Financials: Added in R7 for defect 8703 --%>
		<div id='contractTabs' class='financeTabbs'>
				<ul id="financeTabs" class=''>
					<li><a href="#contractFinancials" clickFunction="firstTabId" anothertabId="contractBudget1" >Contract Financials</a></li>
					<li><a href="#contractBudget" clickFunction="secondTabId"  anotherTabId="contractBudget1" >Contract Budgets</a></li>
				</ul>			
				
				<div class="failed" id="errorGlobalMsg"></div>	
						
					<div id='contractFinancials'>
						<div class="formcontainer paymentFormWrapper widthFull">
		<%--code updation for R4 starts--%>						
						 							<table style="width:100% !important;">
							<tr>
								<td style="width:50% !important;">
									<div class="row">
										  <span class="label">Agency:</span>
										  <span class="formfield" id="agencyName">${aoContractData.agencyName}</span>
									</div>
									<div class="row">
										  <span class="label">Agency Code:</span>
										  <span class="formfield" id="agencyCode">${aoContractData.agencyCode}</span>
									</div>
									<div class="row">
										 <span class="label">Procurement/Contract Title:</span>
										 <span class="formfield">${aoContractData.procurementTitle}</span>
									</div>
									<div class="row">
										  <span class="label">Provider:</span>
										  <span class="formfield">${aoContractData.providerName}</span>
									</div>
									<div class="row">
										  <span class="label">Contract Value:</span>
										  <span class="formfield"><label id="aoContractValue">${aoContractData.contractValue}</label></span>
									</div>
									<div class="row">
										  <span class="label">Contract Term::</span>
										  <span class="formfield">${aoContractData.contractStartDate}-${aoContractData.contractEndDate}</span>
									</div>
								</td>
								<td style="width:50% !important;">
									<div class="row">
										  <span class="label">Amendment E-PIN:</span>
										  <span class="formfield">${aoContractData.amendmentEpin}</span>
									</div>
									<div class="row">
										  <span class="label">Amendment Title:</span>
										  <span class="formfield">${aoContractData.amendmentTitle}</span>
									</div>
									<div class="row">
										<span class="label">Amendment Value:</span>
										<span class="formfield"><label id="aoAmendmentValue">${aoContractData.amendmentValue}</label></span>
									</div>
									<div class="row">
										  <span class="label">Amendment Term:</span>
										  <span class="formfield">${aoContractData.amendmentStartDate}-${aoContractData.amendmentEndDate}</span>
									</div>
									<div class="row">
									 <span class="formfield"></span>
										<span class="formfield"></span>
									</div>
									<div class="row">
									 <span class="formfield"></span>
										<span class="formfield"></span>
									</div>

								</td>
							</tr>
						</table>					 	
						</div>
						
						<div>&nbsp;</div>
						
						<%-- loading the sub grid  --%>
						<portlet:resourceURL var='SubGridHeaderRow' id='mainAccountGrid'
														escapeXml='false'>
						</portlet:resourceURL>
						
						<%-- loading the sub grid  --%>
						<portlet:resourceURL var='SubGridAmendmentHeaderRow' id='mainAccountGrid'
														escapeXml='false'>
						<portlet:param name="isAmendmentFlow" value="true"/>
						</portlet:resourceURL>
						
						<%-- operations for Edit/Update/Save --%>
						<portlet:resourceURL var='ContractConfigAmendmentGridOperation' id='accountOperationGrid' escapeXml='false'>
						<portlet:param name="screenName" value="ContractConfigAmendmentGrid"/>
						<portlet:param name="isAmendmentFlow" value="true"/>
						</portlet:resourceURL>
					
						<%-- operations for Edit/Update/Save --%>
						<portlet:resourceURL var='loadContractConfigReadOnlyGrid'
							id='subAccountGrid' escapeXml='false'>
						<portlet:param name="screenName" value="contractConfigReadOnlyGrid" />
						</portlet:resourceURL>
						
						<%-- operations for Edit/Update/Save --%>
						<portlet:resourceURL var='ContractConfigAmendmentGrid'
								id='subAccountGrid' escapeXml='false'>
								<portlet:param name="screenName" value="ContractConfigAmendmentGrid" />
								<portlet:param name="listName" value="UpdateGrid" />
								<portlet:param name="isAmendmentFlow" value="true"/>
						</portlet:resourceURL>
						
						<%-- operations for Edit/Update/Save --%>
						<portlet:resourceURL var='ContractConfigGrid'
							id='subAccountGrid' escapeXml='false'>
							<portlet:param name="screenName" value="ContractConfigGridCurr" />
						</portlet:resourceURL>
						
						<%-- operations for Edit/Update/Save --%>
						<portlet:resourceURL var='ContractConfigGridOperation' id='accountOperationGrid' escapeXml='false'>
						<portlet:param name="screenName" value="ContractConfigGrid"/>
						</portlet:resourceURL>
	
						<%-- operations for Edit/Update/Save --%>
	                    <portlet:resourceURL var='ContractConfigActualsGrid'
								id='subAccountGrid' escapeXml='false'>
								<portlet:param name="screenName" value="ContractConfigActualsGrid" />
								<portlet:param name="listName" value="ActualGrid" />
						</portlet:resourceURL>
						<%-- JGrid for adding dynamic table --%>
						<h3>Chart of Accounts Allocation - Current</h3>
						<div class='gridFormField gridScroll'>
							<jq:grid id="CoAAllocation" gridColNames="${GridColNames}"
								gridColProp="${MainHeaderProp}" subGridColProp="${SubHeaderProp}"
									gridUrl="${SubGridHeaderRow}"
									subGridUrl="${ContractConfigGrid}" cellUrl="ContractConfigGridOperation" editUrl="ContractConfigGridOperation"
									dataType="json" methodType="POST" columnTotalName="${columnsForTotal}"
									isPagination="true" rowsPerPage="5" isSubGrid="true" isReadOnly="true"
									autoWidth="false" isCOAScreen="true"
									operations="del:false,edit:false,add:false,cancel:false,save:false"
									callbackFunction="clearAndHideError();" />
						</div>
	
	                      <p>&nbsp;</p>
						<%-- JGrid for adding dynamic table --%>
						<h3>Chart of Accounts Allocation - Actuals</h3>	
						<div class='gridFormField gridScroll'>				
							<jq:grid id="CoAAllocationActuals" gridColNames="${GridColNames}"
								gridColProp="${MainHeaderProp}" subGridColProp="${SubHeaderProp}"
									gridUrl="${SubGridHeaderRow}"
									subGridUrl="${ContractConfigActualsGrid}" cellUrl="ContractConfigGridOperation" editUrl="ContractConfigGridOperation"
									dataType="json" methodType="POST" columnTotalName="${columnsForTotal}"
									isPagination="true" rowsPerPage="5" isSubGrid="true" isReadOnly="true"
									autoWidth="false" isCOAScreen="true"
									operations="del:false,edit:false,add:false,cancel:false,save:false" 
									callbackFunction="clearAndHideError();" />
						</div>
						
						<p>&nbsp;</p>
						<%-- JGrid for adding dynamic table --%>
						<h3>Chart of Accounts Allocation - Amendment</h3>
						<div class='gridFormField gridScroll'>
							<jq:grid id="CoAAllocationAmendment" gridColNames="${AmendmentGridColNames}"
								gridColProp="${AmendmentMainHeaderProp}" subGridColProp="${AmendmentSubHeaderProp}"
								gridUrl="${SubGridAmendmentHeaderRow}" isNewRecordDelete="false"
								subGridUrl="${ContractConfigAmendmentGrid}" cellUrl="${ContractConfigAmendmentGridOperation}" editUrl="${ContractConfigAmendmentGridOperation}"
								dataType="json" methodType="POST" columnTotalName="${columnsForTotalAmendment}"
								isPagination="true" nonEditColumnName="total" rowsPerPage="5" isSubGrid="true" isReadOnly="false"
								notAllowDuplicateColumn="uobc,subOC,rc"
								autoWidth="false" isCOAScreen="true" negativeCurrency="${columnsForTotalAmendment}"
								operations="del:${isNegativeAmendment},edit:true,add:${isNegativeAmendment},cancel:true,save:true"
								modificationType="${amendmentGrid}"
								callbackFunction="clearAndHideError();" />
						</div>
						
					    <p>&nbsp;</p>	
					    
					<%-- JGrid for adding dynamic table --%>
					<portlet:resourceURL var='mainFundingGrid' id='mainFundingGrid' escapeXml='false'>
						<portlet:param name="screenName" value="contractConfigurationAmendmentFundingGrid"/>
						<portlet:param name="isAmendmentFlow" value="true"/>
					</portlet:resourceURL>
					
					<%-- JGrid for adding dynamic table --%>
					<portlet:resourceURL var='contractFundingSourceGrid' id='fundingOperationGrid' escapeXml='false'>
						<portlet:param name="screenName" value="contractConfigurationAmendmentFundingGrid"/>
						<portlet:param name="isAmendmentFlow" value="true"/>
					</portlet:resourceURL>
					
					<%-- JGrid for adding dynamic table --%>
					<portlet:resourceURL var='loadContractFundingSourceGrid' id='subFundingGrid' escapeXml='false'>
						<portlet:param name="screenName" value="contractConfigurationAmendmentFundingGrid"/>
						<portlet:param name="isAmendmentFlow" value="true"/>
					</portlet:resourceURL>
					
				<h3>Funding Source Allocation - Amendment (Optional)</h3>
				
				<p>The optional fields below may be used to indicate the funding source allocation at the point of the Amendment Certification of Funds. 
					These fields are for reference purposes only.
				</p>
				
				<%-- JGrid for adding dynamic table --%>
				<div class='gridFormField gridScroll'>
				<jq:grid id="ContractFundingSourceAllocation" 
				         gridColNames="${AmendmentFundingGridColNames}" 	
					     gridColProp="${AmendmentFundingMainHeaderProp}" 
					     subGridColProp="${AmendmentFundingSubHeaderProp}" 
						 gridUrl="${mainFundingGrid}"
						 subGridUrl="${loadContractFundingSourceGrid}"
					     cellUrl="${contractFundingSourceGrid}"
					     editUrl="${contractFundingSourceGrid}"
					     dataType="json" methodType="POST"
					     columnTotalName="${columnsForTotalAmendment}" 
				         isPagination="false"
				         nonEditColumnName="total"  modificationType="${amendmentGrid}"
					     rowsPerPage="5"
				         isSubGrid="true" negativeCurrency="${columnsForTotalAmendment}"
				         isReadOnly="false" autoWidth="false" isCOAScreen="true"
					     operations="del:false,edit:true,add:false,cancel:true,save:true"
					     callbackFunction="clearAndHideError();"/>
				</div>
				 <div class="clear">&nbsp;</div>
		  </div>
				<!-- R7 Changes -->
				<%-- JGrid for adding dynamic table --%>
				<portlet:resourceURL var='contractBudgetPageVar' id='contractAmendmentBudgetPage' escapeXml='false'>
				<portlet:param name="screenReadOnly" value="true"/>				
				</portlet:resourceURL>	
				<input type="hidden" id="hdnCcontractBudgetPageVar" value="${contractBudgetPageVar}"/>
					
					
				<%-- Contract Budget tab starts --%>
	              <div id="contractBudget">
					<div id="contractBudgets1"></div>
					</div>		
				</div>
		
	
	
	</form:form>
	</d:content>
   <div class='clear'></div>
	<div class='gridFormField'>
	 <task:taskContent workFlowId=""  taskType="taskAmendmentCertification" isTaskScreen=""  level="footer"></task:taskContent> 
	</div>
	
	</div>
</div>
<div id="tempDivOverday" class=""></div>
	  <div class="alert-box-help">
				<div class="tabularCustomHead toplevelheaderHelp"></div>
	            <div id="helpPageDiv"></div>
		  		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
			</div>
</d:content>