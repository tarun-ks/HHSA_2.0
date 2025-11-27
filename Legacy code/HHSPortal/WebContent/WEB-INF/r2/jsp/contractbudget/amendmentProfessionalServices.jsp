<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@ taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@ page import="com.nyc.hhs.util.HHSUtil" %>
<%@ page import="com.nyc.hhs.constants.*"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties"/>
<portlet:defineObjects />

<%--
This file is designed to display Professional Service Grid for Contract Budget - Amendment module
 --%>
 <H3>OTPS - Professional Services</H3>

<c:set var="gridColNames"><%=HHSUtil.getHeader("amendmentProfessionalServices.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("amendmentProfessionalServices.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("amendmentProfessionalServices.grid")%></c:set>

<%-- Resource URL to display professional service Grid header row --%>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="amendmentProfessionalServices.grid"/>
</portlet:resourceURL>

<%-- Portlet resource URL to load data in professional service Grid --%>
<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="amendmentProfServicesGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBProfessionalServicesBean"/>
<portlet:param name="gridLabel" value="amendmentProfessionalServices.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>

<%-- Portlet resource URL for edit professional service Grid data --%>
<portlet:resourceURL var='professionalServicesGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="amendmentProfServicesGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBProfessionalServicesBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="amendedContractSubBudgetID" value="${amendedContractSubBudgetID}"/>
</portlet:resourceURL>

<%-- set the amendment Type for positive and negative--%>
<c:set var="amendmentGrid" value="positive,positiveAmendmentMsg"></c:set>
<c:if test="${amendmentType ne 'positive'}">
<c:set var="amendmentGrid" value="negative,negativeAmendmentMsg"></c:set>
</c:if>

<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<jq:grid id="profServicesAmendment-${subBudgetId}" 
		 isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadGridData}"
	     cellUrl="${professionalServicesGrid}"
	     editUrl="${professionalServicesGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="false"
         modificationType="${amendmentGrid}"
         negativeCurrency="modifyAmount"          
	     rowsPerPage="5"
         isSubGrid="true"
         lastRowEdit="false"
	     operations="del:false,edit:true,add:false,cancel:true,save:true"
/>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncomeAmendment.jsp">
	<jsp:param value="4" name="entryTypeId" />
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
					<%--code updation for R4 ends--%>