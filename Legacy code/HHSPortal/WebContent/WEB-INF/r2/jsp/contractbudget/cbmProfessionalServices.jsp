<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@ page import="com.nyc.hhs.util.HHSUtil" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.constants.*"%>
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties"/>
<portlet:defineObjects />

<%--
	This file is designed to display Professional Service Grid for ContractBudgetModification module.
 --%>
 <H3>OTPS - Professional Services</H3>

<%-- Resource URL to display professional service Grid header row --%>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="cbmProfessionalServices.grid"/>
</portlet:resourceURL>

<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<c:set var="gridColNames"><%=HHSUtil.getHeader("cbmProfessionalServices.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("cbmProfessionalServices.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("cbmProfessionalServices.grid")%></c:set>

<%-- Portlet resource URL to load data in professional service Grid --%>
<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="cbmProfServicesGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBProfessionalServicesBean"/>
<portlet:param name="gridLabel" value="cbmProfessionalServices.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>

<%-- Portlet resource URL for edit professional service Grid data --%>
<portlet:resourceURL var='professionalServicesGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="cbmProfServicesGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBProfessionalServicesBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>

<jq:grid id="profServicesGrid-${subBudgetId}" 
		 isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadGridData}"
	     cellUrl="${professionalServicesGrid}"
	     editUrl="${professionalServicesGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName="fyBudget,modifyAmount"
	     negativeCurrency="modifyAmount"
         isPagination="false"
	     rowsPerPage="5"
         isSubGrid="true"
         lastRowEdit="false"
	     operations="del:false,edit:true,add:false,cancel:true,save:true"
/>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncomeModification.jsp">
	<jsp:param value="4" name="entryTypeId" />
</jsp:include>
</c:if>
<%-- R7 changes end --%>

<div>&nbsp;</div>
<c:set var="entityType"><%=HHSConstants.AUDIT_CONTRACT_BUDGET_MODIFICATION%></c:set>
<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_BUDGET_MODIFICATION%></c:set>
<div class='gridFormField'>
<c:choose>
<c:when test="${detailsBeanForTaskGrid.isTaskScreen}">
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_professionalServices_${subBudgetId}"
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
	entityTypeTabLevel="TLC_professionalServices_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>	
</div>