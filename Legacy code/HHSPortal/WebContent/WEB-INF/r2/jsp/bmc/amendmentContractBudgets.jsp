<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.nyc.hhs.util.HHSUtil"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>

<%-- 
		After an Amendment is created from the Contract List screen,
 		the system auto-generates the Amendment Configuration task.
 		In this task, agency users will be able to enter the financial 
 		information required for the Amendment Certification of Funds.
 		In the second tab within the task, the user will be able to
 		update the amount allocated to each Budget for every Fiscal Year
 		with “Active” or “Approved” Budgets where the Chart of Accounts
 		 – Amendment amount was updated. The Fiscal year dropdown will
 		dynamically populate with the list of Fiscal Years with existing
 		budgets that were affected by the amendment amount allocation.
--%>

<%String tabValue= (String)request.getAttribute("selectedFYId");%>

<c:set var="tabValue"><%=tabValue%></c:set>
<c:set var="budgetfiscalYearCount" value="${(fn:length(budgetfiscalYear))}" />
<script type="text/javascript">
$(document).ready(function() {
//	var _newContractVal1 = new Big($('#aoContractBudgetValue').html()).plus($('#aoAmendmentBudgetValue').html());
//	$("#aoNewContractBudgetValue").text(_newContractVal1);
//	$("#aoContractBudgetValue").jqGridCurrency();
//	$("#aoAmendmentBudgetValue").jqGridCurrency();
//	$("#aoNewContractBudgetValue").jqGridCurrency();
	
	$("#currentFYPlannedAmount").jqGridCurrency();
	$("#amendmentFYPlannedAmount").jqGridCurrency();
	//$("#posAmendAmt").jqGridCurrency();
	//$("#negAmendAmt").jqGridCurrency();
	//	$("#newFYPlannedAmount").jqGridCurrency();
	
});
</script>	
<h3>Contract Budgets Setup</h3>
   <div class="formcontainer paymentFormWrapper">
   <div class="row">
		  <span class="label">Fiscal Year(FY):</span>
		  <span class="formfield">
		  <select id="fiscalYearId" name="fiscalYearId" onchange="javascript:getBudgetDetailsByFYI(${startFiscalYear},${numberOfYears});" ${budgetfiscalYearCount == 0 ? 'disabled' : ''}>
		           <option value="-1">Select FY</option> 
		    <c:forEach var="selectedFYI" items="${budgetfiscalYear}"> 
		            <option value=${selectedFYI} ${selectedFYI == tabValue ? 'selected' : ''}>${selectedFYI}</option>
	  
		      </c:forEach>       
   </select></span>
	</div>
	<div class="row">
		  <span class="label">Current Approved FY Amount:</span>
		  <span class="formfield"><label id="currentFYPlannedAmount">${currentFyPlannedAmount}</label></span>
	</div>
	<div class="row">
		  <span class="label">FY Amendment Amount</span>
		  <span class="formfield">
		  	<label id="amendmentFYPlannedAmount">${amendmentFyPlannedAmount}</label>
		  </span>
	</div>
	
	</div>

<p>&nbsp;</p>
 <div class="clear">&nbsp;</div>
<% if(tabValue!=null && !"false".equals(tabValue)){%>

<p>&nbsp;</p>
 <div class="clear">&nbsp;</div>
 <%-- loading the sub grid  --%>
    <portlet:resourceURL var='SubGridHeaderRowSubBudget'
		id='SubGridHeaderRow' escapeXml='false'>
		<portlet:param name="gridLabel" value="contractConfigAmendmentBudgetSetUp1.grid" />
	</portlet:resourceURL>	

<c:set var="amendmentGrid" value="positive,positiveAmendmentMsg"></c:set>
<c:if test="${aoContractData.amendmentType ne 'positive'}">
<c:set var="amendmentGrid" value="negative,negativeAmendmentMsg"></c:set>
</c:if>

<c:set var="isNegativeAmendment" value="false"></c:set>

<fmt:parseNumber var="amendmentValue" type="number" value="${amendmentValue}" />
<c:if test="${amendmentValue ge 0 }">
	<c:set var="isNegativeAmendment" value="true"></c:set>
</c:if>

<c:set var="gridColNames"><%=HHSUtil.getHeader("contractConfigAmendmentBudgetSetUp1.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractConfigAmendmentBudgetSetUp1.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractConfigAmendmentBudgetSetUp1.grid")%></c:set>

<%-- loading the page  --%>
<portlet:resourceURL var='loadBudgetRate' id='fetchContractBudgetDetailsBySelectedFY' escapeXml='false'>
<portlet:param name="transactionName" value="contractConfigurationUpdateSubBudgetGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.ContractBudgetBean"/>
<portlet:param name="budgetTypeId" value="1"/>
<portlet:param name="gridLabel" value="contractConfigAmendmentBudgetSetUp1.grid"/>
<portlet:param name="selectedFYId" value="${tabValue}" />
</portlet:resourceURL>

<%-- operations for Edit/Update/Save --%>
<portlet:resourceURL var='subBudgetGridActions' id='gridOperationForBudgetConfigUpdateTask' escapeXml='false'>
		<portlet:param name="transactionName" value="contractConfigurationUpdateSubBudgetGrid" />
		<portlet:param name="beanName" value="com.nyc.hhs.model.ContractBudgetBean" />
		<portlet:param name="budgetTypeId" value="1"/>
		<portlet:param name="isAmendmentFlow" value="true"/>
		<portlet:param name="selectedFYId" value="${tabValue}"/>
</portlet:resourceURL>
<!-- Start: Added in R7 for Read only -->
<c:set var="gridReadonly" value="false"></c:set>
<c:if test="${(screenReadOnly eq true)}">
<c:set var="gridReadonly" value="true"></c:set>
</c:if>
<d:content isReadOnly="${screenReadOnly}" >
<%-- JGrid for adding dynamic table --%>
<jq:grid id="contractBudget1" 
         isReadOnly="${gridReadonly}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
	     negativeCurrency="modifiedAmount"
		 gridUrl="${SubGridHeaderRowSubBudget}" isNewRecordDelete="true"
		 subGridUrl="${loadBudgetRate}"
	     cellUrl="${subBudgetGridActions}"
	     editUrl="${subBudgetGridActions}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
	     checkForZeroAndDelete=""
	     nonEditColumnName="subbudgetAmount,remAmt,posAmendAmt,negAmendAmt"
	     isPagination="true"
	     rowsPerPage="5"
         isSubGrid="true" modificationType="${amendmentGrid}"
         operations="del:true,edit:true,add:${isNegativeAmendment},cancel:true,save:true"
/>

<%--R4 Budget Customized AJAX CALL --%>
<%-- Budget Customized Start --%>
<portlet:resourceURL var='BudgetCustomizedVar' id='UpdateBudgetCustomizedTab' escapeXml='false'/>
	<input type="hidden" id="hdnBudgetCustomizedVar" value="${BudgetCustomizedVar}"/>   
<div id="budgetCustomized">
	<jsp:include page="/WEB-INF/r2/jsp/contractbudget/contractBudgetTemplate.jsp" />
</div>
</d:content>
<!-- End: Added in R7 for Read only -->
<%-- Budget Customized End --%>
<%} %>		