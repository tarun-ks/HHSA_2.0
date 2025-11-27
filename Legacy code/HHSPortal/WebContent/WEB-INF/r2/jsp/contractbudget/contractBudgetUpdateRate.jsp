<%-- JSP for S384 Contract Budget Update - Rate Grid --%>
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
<%-- This jsp is used for S384 Rate screen. --%>
<H3>Rate</H3>
<portlet:resourceURL var='RateSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="updateBudgetRate.grid"/>
</portlet:resourceURL>
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<c:set var="gridColNames"><%=HHSUtil.getHeader("updateBudgetRate.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("updateBudgetRate.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("updateBudgetRate.grid")%></c:set>
<%-- This portlet resource is used to map "loadGridData" resource in Controller to load Grid data	--%>
<portlet:resourceURL var='loadBudgetUpdateRate' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="contractBudgetModificationRate"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.RateBean"/>
<portlet:param name="gridLabel" value="updateBudgetRate.grid"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<%-- This portlet resource is used to map "gridOperation" resource in Controller to perform grid operations--%>
<portlet:resourceURL var='rateOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="contractBudgetModificationRate"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.RateBean"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<jq:grid id="modificationRateGrid-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${RateSubGridHeaderRow}"
		 subGridUrl="${loadBudgetUpdateRate}"
	     cellUrl="${rateOperationGrid}"
	     editUrl="${rateOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName="fyBudget,lsModifyAmount"
         isPagination="true"
	     rowsPerPage="5"
         isSubGrid="true" isNewRecordDelete="true"
         nonEditColumnName="remUnits,remainAmt,fyBudget,lsProposedBudget"
         negativeCurrency="lsModifyAmount" 
	     operations="del:true,edit:true,add:true,cancel:true,save:true"
/>

<div>

</div>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncomeUpdate.jsp">
	<jsp:param value="7" name="entryTypeId" />
</jsp:include>
</c:if>
<%-- R7 changes end --%>

	<%--code updation for R4 starts--%>
<div>&nbsp;</div>
<c:set var="entityType"><%=HHSConstants.AUDIT_CONTRACT_BUDGET_UPDATE%></c:set>
<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_BUDGET_UPDATE%></c:set>
<div class='gridFormField'>
<c:choose>
<c:when test="${detailsBeanForTaskGrid.isTaskScreen}">
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_rate_${subBudgetId}"
	isTaskScreen=""
	commentsSection="comments"
	level="footer">
	</task:taskContent>
</c:when>
<c:otherwise>
<!-- Comments Length Updated for 8475-->
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_rate_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>
</div>
	<%--code updation for R4 ends--%>