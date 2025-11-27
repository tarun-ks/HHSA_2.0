<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.nyc.hhs.util.HHSUtil"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>


<%String tabValue= (String)request.getAttribute("selectedFYId");%>
<c:set var="tabValue"><%=tabValue%></c:set> 
<h3>Contract Budgets Setup</h3>

   <div class="formcontainer paymentFormWrapper" style='width:100%'>
	<div class="row">
		  <span class="label">Fiscal Year(FY):</span> 
		  <span class="formfield">
		  <select id="fiscalYearId" name="fiscalYearId" onchange="javascript:getBugetDetailsByFYI();">
		           <option value="-1">Select FY</option> 
		    <c:forEach var="selectedFYI" items="${budgetfiscalYear}"> 
		            <option value=${selectedFYI} ${selectedFYI == tabValue ? 'selected' : ''}>${selectedFYI}</option>
	        </c:forEach>       
   		 </select></span> 
   
   		<!-- start  QC 9078 R 7.4.0 Contract Configuration Update task was created before the contract has an active budget   -->
   		<!--  show error message if no active budget was found per contract -->
   		<c:if test="${empty budgetfiscalYear}">
   			<p class='infoMessage' style='display: block;'>There is a pending contract budget that must be reviewed and approved. You will be able to complete this Contract Configuration Update task when the budget is in Active status. </p>
   			<p></p>
   		</c:if>
   		<!-- end start  QC 9078 R 7.4.0 contract Configuration Update task was created before the contract has an active budget -->	
	</div>
	<div class="row">
		  <span class="label">Contract Value:</span>
		  <span class="formfield">
		  	<input type="text" readonly="readonly" id="budgetTabContractsValue" value="${contractValue}" validate="currency"/>
		  </span>
	</div>
	<div class="row">
		  <span class="label">FY Planned Amount:</span>
		   <span class="formfield">
		  	<input type="text" readonly="readonly" id="budgetTabTotalBudgetAmount" value="${totalbudgetAmount}"/>
		  </span>
	</div>
</div>
<% if(tabValue!=null && !"tab".equals(tabValue)){%>

<p>&nbsp;</p>
 <div class="clear">&nbsp;</div>
    <portlet:resourceURL var='SubGridHeaderRowSubBudget'
		id='SubGridHeaderRow' escapeXml='false'>
		<portlet:param name="gridLabel" value="contractConfigUpdateBudgetSetUp1.grid" />
	</portlet:resourceURL>	


<c:set var="gridColNames"><%=HHSUtil.getHeader("contractConfigUpdateBudgetSetUp1.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractConfigUpdateBudgetSetUp1.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractConfigUpdateBudgetSetUp1.grid")%></c:set>

<portlet:resourceURL var='loadBudgetRate' id='fetchContractBudgetDetailsBySelectedFY' escapeXml='false'>
<portlet:param name="transactionName" value="contractConfigurationUpdateSubBudgetGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.ContractBudgetBean"/>
<portlet:param name="budgetTypeId" value="4"/>
<portlet:param name="gridLabel" value="contractConfigUpdateBudgetSetUp1.grid"/>
<portlet:param name="selectedFYId" value="${tabValue}" />
</portlet:resourceURL>

<portlet:resourceURL var='subBudgetGridActions' id='gridOperationForBudgetConfigUpdateTask' escapeXml='false'>
		<portlet:param name="transactionName" value="contractConfigurationUpdateSubBudgetGrid" />
		<portlet:param name="budgetTypeId" value="4"/>
		<portlet:param name="isAmendmentFlow" value="false"/>
		<portlet:param name="beanName" value="com.nyc.hhs.model.ContractBudgetBean" />
		<portlet:param name="selectedFYId" value="${tabValue}"/>
</portlet:resourceURL>
<jq:grid id="contractBudget1" 
         isReadOnly="false"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${SubGridHeaderRowSubBudget}" isNewRecordDelete="true"
		 subGridUrl="${loadBudgetRate}"
	     cellUrl="${subBudgetGridActions}"
	     editUrl="${subBudgetGridActions}"
	     dataType="json" methodType="POST"
	     columnTotalName="subbudgetAmount,modifiedAmount"
	     checkForZeroAndDelete=""
	     nonEditColumnName="subbudgetAmount,invoiceAmount,proposedBudgetAmount"
	     isPagination="true"
	     rowsPerPage="5"
	     negativeCurrency="modifiedAmount"
         isSubGrid="true"
         operations="del:true,edit:true,add:true,cancel:true,save:true"
/>

<%--R4 Budget Customized AJAX CALL --%>
<%-- Budget Customized Start --%>
<portlet:resourceURL var='BudgetCustomizedVar' id='UpdateBudgetCustomizedTab' escapeXml='false'/>
	<input type="hidden" id="hdnBudgetCustomizedVar" value="${BudgetCustomizedVar}"/>   
<div id="budgetCustomized">
	<jsp:include page="/WEB-INF/r2/jsp/contractbudget/contractBudgetTemplate.jsp" />
</div>
<%-- Budget Customized End --%>

<%} %>