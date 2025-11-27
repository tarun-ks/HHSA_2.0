<%---------This jsp loads Contracted Services Screen For COntract Budget Modification --%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil" %>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />

<H3>OTPS - Contracted Services</H3>
<div class='formcontainer paymentFormWrapper widthFull'>
	<div class="row">
	      <span class="label">Amendment Total Contracted Services:  </span>
	      <span class="formfield">
	      	<span class='lftAmount'>
	      		<label id="totCS${subBudgetId}">${contractedDisplay.totalContractedServices}</label>
	      	</span>
	      </span>
	</div>
	
</div>
<div class='clear'>&nbsp;</div>
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<c:set var="isNegativeAmend" value="true"></c:set>

<c:set var="amendmentGrid" value="positive,positiveAmendmentMsg"></c:set>
<c:if test="${amendmentType ne 'positive'}">
<c:set var="amendmentGrid" value="negative,negativeAmendmentMsg"></c:set>
<c:set var="isNegativeAmend" value="false"></c:set>
</c:if>



<%--Consultants Grid Load--%>
<portlet:resourceURL var='contractedServicesConsultantsSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractedServicesAmendmentConsultants.grid"/>
</portlet:resourceURL>

<c:set var="gridColNames"><%=HHSUtil.getHeader("contractedServicesAmendmentConsultants.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractedServicesAmendmentConsultants.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractedServicesAmendmentConsultants.grid")%></c:set>

<portlet:resourceURL var='loadBudgetContractedConsultants' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="getContractedServicesAmendmentConsultants"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.ContractedServicesBean"/>
<portlet:param name="gridLabel" value="contractedServicesAmendmentConsultants.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>
<%--Consultants Grid Load Ends--%>

<%--JQGrid Mapping for Consultants Grid Operations--%>
<portlet:resourceURL var='contractedServicesConsultantsOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="getContractedServicesAmendment"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.ContractedServicesBean"/>
<portlet:param name="subHeader" value="1"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>
<%--JQGrid Mapping for Consultants Grid Operations Ends--%>
<jq:grid id="consultantsGrid-${subBudgetId}"
	isReadOnly="${readOnlyPageAttribute}" gridColNames="${gridColNames}"
	gridColProp="${gridColProp}"
	subGridColProp="${subGridColProp}"
	modificationType="${amendmentGrid}"
	gridUrl="${contractedServicesConsultantsSubGridHeaderRow}"
	subGridUrl="${loadBudgetContractedConsultants}"
	cellUrl="${contractedServicesConsultantsOperationGrid}"
	editUrl="${contractedServicesConsultantsOperationGrid}" dataType="json"
	methodType="POST" columnTotalName=""
	isPagination="true" isNewRecordDelete="true" rowsPerPage="5"
	nonEditColumnName="fyBudget,remaingAmt" 
	negativeCurrency="amendmentAmt"  
	isSubGrid="true"
	operations="del:true,edit:true,add:${isNegativeAmend},cancel:true,save:true" 
	callbackFunction="refreshNonGridContractedServicesData('${subBudgetId}');"/>

<%--Contracted Services Consultants Grid Ends--%>
<br>
<%--SubContractors Grid Load--%>
<portlet:resourceURL var='contractedServicesSubContractorsSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractedServicesAmendmentSubContractors.grid"/>
</portlet:resourceURL>

<c:set var="gridColNames"><%=HHSUtil.getHeader("contractedServicesAmendmentSubContractors.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractedServicesAmendmentSubContractors.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractedServicesAmendmentSubContractors.grid")%></c:set>

<portlet:resourceURL var='loadBudgetContractedSubContractors' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="getContractedServicesAmendmentSubContractors"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.ContractedServicesBean"/>
<portlet:param name="gridLabel" value="contractedServicesAmendmentSubContractors.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>
<%--SubContractors Grid Load Ends--%>

<%--JQGrid Mapping for SubContractors Grid Operations--%>
<portlet:resourceURL var='contractedServicesSubContractorsOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="getContractedServicesAmendment"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.ContractedServicesBean"/>
<portlet:param name="subHeader" value="2"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>
<%--JQGrid Mapping for SubContractors Grid Operations Ends--%>
<%--Contracted Services Sub-Contractors Grid----%>
<jq:grid id="subContractorsGrid-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}"
	     modificationType="${amendmentGrid}" 
		 gridUrl="${contractedServicesSubContractorsSubGridHeaderRow}"
		 subGridUrl="${loadBudgetContractedSubContractors}"
	     cellUrl="${contractedServicesSubContractorsOperationGrid}"
	     editUrl="${contractedServicesSubContractorsOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
         isNewRecordDelete="true"
	     rowsPerPage="5"
	     nonEditColumnName="fyBudget,remaingAmt"
	     negativeCurrency="amendmentAmt"
         isSubGrid="true"
	     operations="del:true,edit:true,add:${isNegativeAmend},cancel:true,save:true"
	     callbackFunction="refreshNonGridContractedServicesData('${subBudgetId}');"
/>
<%--Contracted Services Sub-Contractors Grid Ends----%>

<br>
<%--Vendors Grid Load--%>
<portlet:resourceURL var='contractedServicesVendorsSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractedServicesAmendmentVendors.grid"/>
</portlet:resourceURL>

<c:set var="gridColNames"><%=HHSUtil.getHeader("contractedServicesAmendmentVendors.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractedServicesAmendmentVendors.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractedServicesAmendmentVendors.grid")%></c:set>

<portlet:resourceURL var='loadBudgetContractedVendors' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="getContractedServicesAmendmentVendors"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.ContractedServicesBean"/>
<portlet:param name="gridLabel" value="contractedServicesAmendmentVendors.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<%--Vendors Grid Load Ends--%>
</portlet:resourceURL>
<%--Vendors Grid Load Ends--%>

<%--JQGrid Mapping for Vendors Grid Operations--%>
<portlet:resourceURL var='contractedServicesVendorsOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="getContractedServicesAmendment"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.ContractedServicesBean"/>
<portlet:param name="subHeader" value="3"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>
<%--JQGrid Mapping for Vendors Grid Operations Ends--%>
<%--Contracted Services Vendors Grid----%>
<jq:grid id="vendorsGrid-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
	     modificationType="${amendmentGrid}"
		 gridUrl="${contractedServicesVendorsSubGridHeaderRow}"
		 subGridUrl="${loadBudgetContractedVendors}"
	     cellUrl="${contractedServicesVendorsOperationGrid}"
	     editUrl="${contractedServicesVendorsOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
         isNewRecordDelete="true"
	     rowsPerPage="5"
	     isSubGrid="true"
	     negativeCurrency="amendmentAmt"
	     nonEditColumnName="fyBudget,remaingAmt"
	     operations="del:true,edit:true,add:${isNegativeAmend},cancel:true,save:true"
	     callbackFunction="refreshNonGridContractedServicesData('${subBudgetId}');"
/>
<div>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncomeAmendment.jsp">
	<jsp:param value="6" name="entryTypeId" />
</jsp:include>
</c:if>
<%-- R7 changes end --%>

	<%--code updation for R4 starts--%>
<div>&nbsp;</div>
<c:set var="entityType"><%=HHSConstants.AUDIT_CONTRACT_BUDGET_AMENDMENT%></c:set>
<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_BUDGET_AMENDMENT%></c:set>
<div class='gridFormField'>
<c:choose>
<c:when test="${detailsBeanForTaskGrid.isTaskScreen}">
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_contractedServices_${subBudgetId}"
	isTaskScreen=""
	commentsSection="comments"
	level="footer">
	</task:taskContent>
</c:when>
<c:otherwise>
<!-- Updated in R6-->
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_contractedServices_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>	
</div>
	<%--code updation for R4 ends--%>
</div>
<script>
$(document)
.ready(
		function() {				
			var subBudgetID = ${subBudgetId};
			$("#totCS"+subBudgetID).jqGridCurrency();
			
		});

</script>
