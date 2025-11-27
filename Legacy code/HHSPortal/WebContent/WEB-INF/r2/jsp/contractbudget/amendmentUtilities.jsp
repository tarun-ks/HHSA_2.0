<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil" %>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />
<%-- This JSP is for Utilities Modification grid screen --%>


<H3>OTPS - Utilities</H3>

<%-- This portlet resource maps the Action in Base Controller to display header data in Utilities Modification grid  --%>
<portlet:resourceURL var='RateSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
	<portlet:param name="gridLabel" value="amendmentUtilities.grid"/>
</portlet:resourceURL>

<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<%-- set the amendment Type for positive and negative--%>
<c:set var="amendmentGrid" value="positive,positiveAmendmentMsg"></c:set>
<c:if test="${amendmentType ne 'positive'}">
<c:set var="amendmentGrid" value="negative,negativeAmendmentMsg"></c:set>
</c:if>

<%-- This portlet resource maps the Action in Base Controller to load data in Utilities Modification grid  --%>
<portlet:resourceURL var='loadBudgetRate' id='loadGridData' escapeXml='false'>
	<portlet:param name="transactionName" value="utilitiesGridAmendment"/>
	<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
	<portlet:param name="gridLabel" value="amendmentUtilities.grid"/>
	<portlet:param name="subBudgetId" value="${subBudgetId}"/>
	<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>

<%-- This portlet resource maps the Action in Base Controller when edit operation is performed in Utilities Modification grid  --%>
<portlet:resourceURL var='utilityOperationGrid' id='gridOperation' escapeXml='false'>
	<portlet:param name="transactionName" value="utilitiesGridAmendment"/>
	<portlet:param name="beanName" value="com.nyc.hhs.model.CBUtilities"/>
	<portlet:param name="gridLabel" value="amendmentUtilities.grid"/>
	<portlet:param name="subBudgetId" value="${subBudgetId}"/>
	<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>


<c:set var="gridColNames"><%=HHSUtil.getHeader("amendmentUtilities.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("amendmentUtilities.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("amendmentUtilities.grid")%></c:set>


  <jq:grid id="utilitiesAmendmentGrid-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${RateSubGridHeaderRow}"
		 subGridUrl="${loadBudgetRate}"
	     cellUrl="${utilityOperationGrid}"
	     editUrl="${utilityOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="false"
	     rowsPerPage="0"
	     modificationType="${amendmentGrid}"
         isSubGrid="true"
         nonEditColumnName="fyAmount,fyAmount,proposedBudget"
         negativeCurrency="lineItemModifiedAmt"
	     operations="del:false,edit:true,add:false,cancel:true,save:true"
/>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncomeAmendment.jsp">
	<jsp:param value="3" name="entryTypeId" />
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
	entityTypeTabLevel="TLC_utilities_${subBudgetId}"
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
	entityTypeTabLevel="TLC_utilities_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>
</div>
					<%--code updation for R4 ends--%>