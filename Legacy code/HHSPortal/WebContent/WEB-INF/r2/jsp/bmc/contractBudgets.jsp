<%-- This JSp is used for the content showing under the Contract Budgets Tab while Contract Configuration --%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.nyc.hhs.util.HHSUtil"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>

<%-- Include JS for Contract Budgets Tab --%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/contractBudgets.js"></script>
<!-- start: Added in R7 for Read-only -->
<d:content isReadOnly="${screenReadOnly}" >
<!-- End: Added in R7 for Read-only -->
<%-- Budgets / Sub Budgets Non-Grid Data show Starts--%>
<h3>Contract Budgets Setup</h3>
   <div class="formcontainer paymentFormWrapper" style='width:100%'>
	<div class="row">
		  <span class="label">Fiscal Year(FY):</span>
		  <%-- R4 Added id to get FiscalYear--%>
		  <span class="formfield" id="budgetfiscalYear">${budgetfiscalYear}</span>
	</div>
	<div class="row">
		  <span class="label">Contract Value:</span>
		  <span class="formfield">
		  	<input type="text" readonly="readonly" id="budgetTabContractsValue" size="24" value="${contractValue}" validate="currency"/>
		  </span>
	</div>
	<div class="row">
		  <span class="label">FY Planned Amount:</span>
		   <span class="formfield">
		  	<input type="text" readonly="readonly" id="budgetTabTotalBudgetAmount" size="24" value="${fYBudgetPlannedAmount}"/>
		  </span>
	</div>
</div>
<%-- Budgets / Sub Budgets Non-Grid Data show Ends--%>

<p>&nbsp;</p>
 <div class="clear">&nbsp;</div>

	<%-- Sub Budgets Grid Properties --%>
	<c:set var="ContractBudgetGridColNames"><%=HHSUtil.getHeader("contractBudgetSetUp1.grid")%></c:set>
	<c:set var="ContractBudgetMainHeaderProp"><%=HHSUtil.getHeaderProp("contractBudgetSetUp1.grid")%></c:set>
	<c:set var="ContractBudgetSubHeaderProp"><%=HHSUtil.getSubGridProp("contractBudgetSetUp1.grid")%></c:set>			
				
	<%-- Sub Budgets Sub Grid Properties --%>
	<portlet:resourceURL var='SubGridHeaderRowSubBudget'
		id='SubGridHeaderRow' escapeXml='false'>
		<portlet:param name="gridLabel" value="contractBudgetSetUp1.grid" />
	</portlet:resourceURL>	
	
	<%-- Sub Budgets Grid Data Load URL --%>
	<portlet:resourceURL var='loadSubBudgetGrid' id='fetchContractBudgetDetails' escapeXml='false'>
		<portlet:param name="transactionName" value="contractConfigurationSubBudgetGrid" />
		<portlet:param name="beanName" value="com.nyc.hhs.model.ContractBudgetBean" />
		<portlet:param name="gridLabel" value="contractBudgetSetUp1.grid" />
	</portlet:resourceURL>	
	
	<%-- Sub Budgets Grid Actions/Operations URL --%>
	<portlet:resourceURL var='subBudgetGridActions' id='gridOperationForBudgetConfig' escapeXml='false'>
		<portlet:param name="transactionName" value="contractConfigurationSubBudgetGrid" />
		<portlet:param name="beanName" value="com.nyc.hhs.model.ContractBudgetBean" />
	</portlet:resourceURL>
	
<%-- Sub Budgets Grid Starts --%>
		<jq:grid id="contractBudget1" 
		         gridColNames="${ContractBudgetGridColNames}" 
			     gridColProp="${ContractBudgetMainHeaderProp}" 
			     subGridColProp="${ContractBudgetSubHeaderProp}" 
				 gridUrl="${SubGridHeaderRowSubBudget}"
				 subGridUrl="${loadSubBudgetGrid}"
			     cellUrl="${subBudgetGridActions}"
			     editUrl="${subBudgetGridActions}"
			     dataType="json" methodType="POST"
			     columnTotalName=""
			     positiveCurrency="subbudgetAmount"
		         isPagination="true"
			     rowsPerPage="5"
		         isSubGrid="true"
		         isReadOnly="false" autoWidth="true"
			     operations="del:true,edit:true,add:${addEnabled},cancel:true,save:true"
			     callbackFunction="clearAndHideError();"/>
<%-- Sub Budgets Grid Ends --%>     

<%--R4 Budget Customized AJAX CALL --%>
<%-- Budget Customized Start --%>
<portlet:resourceURL var='BudgetCustomizedVar' id='UpdateBudgetCustomizedTab' escapeXml='false'/>
	<input type="hidden" id="hdnBudgetCustomizedVar" value="${BudgetCustomizedVar}"/>   
<div id="budgetCustomized">
	<jsp:include page="/WEB-INF/r2/jsp/contractbudget/contractBudgetTemplate.jsp" />
</div>
</d:content>
<%-- Budget Customized End --%>							