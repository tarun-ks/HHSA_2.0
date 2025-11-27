<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page import="com.nyc.hhs.util.HHSUtil"%>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>

<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>

<script type="text/javascript">
$("#budgetTabContractsValue").jqGridCurrency();
$("#budgetTabTotalBudgetAmount").jqGridCurrency();
</script>
<h3>Contract Budgets Setup</h3>
   <div class="formcontainer paymentFormWrapper" style='width:100%'>
	<div class="row">
		  <span class="label">Fiscal Year(FY):</span>
		  <%-- R4 added id to get budgetfiscalYear--%>
		  <span class="formfield" id="budgetfiscalYear">${budgetfiscalYear}</span>
	</div>
	<div class="row">
		  <span class="label">Contract Value:</span>
		   <span class="formfield"><label id="budgetTabContractsValue">${contractValue}</label></span>
	</div>
	<div class="row">
		  <span class="label">FY Planned Amount:</span>
		   <span class="formfield"><label id="budgetTabTotalBudgetAmount">${fYBudgetPlannedAmount}</label></span>
	</div>
</div>

<p>&nbsp;</p>
 <div class="clear">&nbsp;</div>

	<c:set var="NewFYBudgetGridColNames"><%=HHSUtil.getHeader("NewFYBudgetSetUp.grid")%></c:set>
	<c:set var="NewFYBudgetMainHeaderProp"><%=HHSUtil.getHeaderProp("NewFYBudgetSetUp.grid")%></c:set>
	<c:set var="NewFYBudgetSubHeaderProp"><%=HHSUtil.getSubGridProp("NewFYBudgetSetUp.grid")%></c:set>			
				
				
	<portlet:resourceURL var='SubGridHeaderRowSubBudget'
		id='SubGridHeaderRow' escapeXml='false'>
		<portlet:param name="gridLabel" value="NewFYBudgetSetUp.grid" />
	</portlet:resourceURL>	
	
	<portlet:resourceURL var='loadSubBudgetGrid' id='fetchContractBudgetDetails' escapeXml='false'>
		<portlet:param name="transactionName" value="newFYConfigurationSubBudgetGrid" />
		<portlet:param name="beanName" value="com.nyc.hhs.model.ContractBudgetBean" />
		<portlet:param name="gridLabel" value="NewFYBudgetSetUp.grid" />
	</portlet:resourceURL>	
	
	<portlet:resourceURL var='subBudgetGridActions' id='gridOperationForBudgetConfig' escapeXml='false'>
		<portlet:param name="transactionName" value="newFYConfigurationSubBudgetGrid" />
		<portlet:param name="beanName" value="com.nyc.hhs.model.ContractBudgetBean" />
	</portlet:resourceURL>
		<jq:grid id="contractBudget1" 
		         gridColNames="${NewFYBudgetGridColNames}" 
			     gridColProp="${NewFYBudgetMainHeaderProp}" 
			     subGridColProp="${NewFYBudgetSubHeaderProp}" 
				 gridUrl="${SubGridHeaderRowSubBudget}"
				 subGridUrl="${loadSubBudgetGrid}"
			     cellUrl="${subBudgetGridActions}"
			     editUrl="${subBudgetGridActions}"
			     dataType="json" methodType="POST"
			     columnTotalName=""
		         isPagination="true"
			     rowsPerPage="5"
		         isSubGrid="true"
		         positiveCurrency="subbudgetAmount"
		         isReadOnly="false" nonEditColumnName="${nonEditColname}"
			     operations="del:true,edit:true,add:${addEnabled},cancel:true,save:true"/>

<%-- R4 Budget Customized AJAX CALL --%>
<%-- Budget Customized Start --%>
<portlet:resourceURL var='BudgetCustomizedVar' id='UpdateBudgetCustomizedTab' escapeXml='false'/>
	<input type="hidden" id="hdnBudgetCustomizedVar" value="${BudgetCustomizedVar}"/>   
<div id="budgetCustomized">
	<jsp:include page="/WEB-INF/r2/jsp/contractbudget/contractBudgetTemplate.jsp" />
</div>
<%-- Budget Customized End --%>								