<%--
This JSP file is used for Services Tab on Contract Budge Update Page.
This JSP file is added in R7 for Cost Center.
Updated gridLabels for defect 8746.
--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil" %>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />

<H3>Services</H3>

<%--getting Sub-Grid header  --%>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="sercvicesUpdate.grid"/>
</portlet:resourceURL>

<%-- Code for Security matrix and readonly value --%> 
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:set var="lastRowEditAttribute" value="true"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
<c:set var="lastRowEditAttribute" value="false"></c:set>
</c:if>

<c:set var="gridColNames"><%=HHSUtil.getHeader("sercvicesUpdate.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("sercvicesUpdate.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("sercvicesUpdate.grid")%></c:set>

<%-- Below resource url is for fetching/onload of Services grid--%>
<portlet:resourceURL var='loadServiceGrid' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="contractServicesModificationGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
<portlet:param name="gridLabel" value="sercvicesUpdate.grid"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<%-- Below resource url is for operations on Services grid--%>
<portlet:resourceURL var='servicesGridActions' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="contractServicesModificationGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<jq:grid id="contractServicesModificationGrid-${subBudgetId}" 
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadServiceGrid}"
	     cellUrl="${servicesGridActions}"
	     editUrl="${servicesGridActions}"
	     dataType="json" methodType="POST"
	     columnTotalName="fyBudget,modificationAmt"
         isPagination="true" lastRowEdit="false"
	     rowsPerPage="10" 
	     nonEditColumnName="remUnits,fyBudget,remainingAmt,proposedIncome"
         isSubGrid="true"
         negativeCurrency="modificationAmt"
         isReadOnly="${readOnlyPageAttribute}"
	     operations="del:false,edit:true,add:false,cancel:true,save:true"
	     exportFileName="SERVICES_DETAIL"
	     callbackFnAfterLoadGrid="updateJsonForExport(parentTotal,'proposedIncome','fyBudget:modificationAmt');"
/>

<div>&nbsp;</div>
<div>&nbsp;</div>

<H3>Cost Center</H3>

<%-- Below resource url is for Cost Center modification grid headers --%>
<portlet:resourceURL var='SubGridHeaderRow1' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="costCenterUpdate.grid"/>
</portlet:resourceURL>

<c:set var="gridColNames1"><%=HHSUtil.getHeader("costCenterUpdate.grid")%></c:set>
<c:set var="gridColProp1"><%=HHSUtil.getHeaderProp("costCenterUpdate.grid")%></c:set>
<c:set var="subGridColProp1"><%=HHSUtil.getSubGridProp("costCenterUpdate.grid")%></c:set>

<%-- Below resource url is for fetching/onload of Cost Center modification grid--%>
<portlet:resourceURL var='loadCostCenterGrid' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="costCenterModificationGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
<portlet:param name="gridLabel" value="costCenterUpdate.grid"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<%-- Below resource url is for operations on Cost Center modification grid--%>
<portlet:resourceURL var='costCenterGridActions' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="costCenterModificationGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<jq:grid id="costCenterModificationGrid-${subBudgetId}" 
         gridColNames="${gridColNames1}" 
	     gridColProp="${gridColProp1}" 
	     subGridColProp="${subGridColProp1}" 
		 gridUrl="${SubGridHeaderRow1}"
		 subGridUrl="${loadCostCenterGrid}"
	     cellUrl="${costCenterGridActions}"
	     editUrl="${costCenterGridActions}"
	     dataType="json" methodType="POST"
	     columnTotalName="fyBudget,modificationAmt"	     
         isPagination="true" lastRowEdit="false"
         nonEditColumnName="fyBudget,remainingAmt,proposedIncome"
	     rowsPerPage="10"
         isSubGrid="true"
         negativeCurrency="modificationAmt"
         isReadOnly="${readOnlyPageAttribute}"
         isNewRecordDelete="true"
	     operations="del:false,edit:true,add:false,cancel:true,save:true"
	     exportFileName="COST_CENTER_DETAIL"
	     callbackFnAfterLoadGrid="updateJsonForExport(parentTotal,'proposedIncome','fyBudget:modificationAmt');"
/>
<div>&nbsp;</div>

<div>
<div>&nbsp;</div>
<c:set var="entityType"><%=HHSConstants.AUDIT_CONTRACT_BUDGET_UPDATE%></c:set>
<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_BUDGET_UPDATE%></c:set>
<div class='gridFormField'>
<c:choose>
<c:when test="${detailsBeanForTaskGrid.isTaskScreen}">
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_services_${subBudgetId}"
	isTaskScreen=""
	commentsSection="comments"
	level="footer">
	</task:taskContent>
</c:when>
<c:otherwise>
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_services_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>	
</div>
</div>