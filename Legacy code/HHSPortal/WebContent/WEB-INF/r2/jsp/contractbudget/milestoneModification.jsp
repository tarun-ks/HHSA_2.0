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

<%-- This JSP is for screen S371 Milestone - Contract Budget Modification. --%>

<portlet:defineObjects />

<H3>Milestone</H3>

<portlet:resourceURL var='MilestoneSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractBudgetModMilestone.grid"/>
</portlet:resourceURL>

<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>
<c:set var="gridColNames"><%=HHSUtil.getHeader("contractBudgetModMilestone.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractBudgetModMilestone.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractBudgetModMilestone.grid")%></c:set>

<%--This portlet:resourceURL is for Grid data load call --%>
<portlet:resourceURL var='loadBudgetMilestone' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="getContractBudgetModMilestoneGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBMileStoneBean"/>
<portlet:param name="gridLabel" value="contractBudgetModMilestone.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>

<%--This portlet:resourceURL is for Grid data operations call --%>
<portlet:resourceURL var='milestoneOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="getContractBudgetModMilestoneGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBMileStoneBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>



<jq:grid id="milestoneModificationGrid-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${MilestoneSubGridHeaderRow}"
		 subGridUrl="${loadBudgetMilestone}"
	     cellUrl="${milestoneOperationGrid}"
	     editUrl="${milestoneOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName="amount,modificationAmount"
         isPagination="true"
	     rowsPerPage="5"
         isSubGrid="true" isNewRecordDelete="true"
         nonEditColumnName="amount,remainAmt,proposedAmount"
         negativeCurrency="modificationAmount"
	     operations="del:true,edit:true,add:true,cancel:true,save:true"
/>


<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncomeModification.jsp">
	<jsp:param value="8" name="entryTypeId" />
</jsp:include>
</c:if>
<%-- R7 changes end --%>

<div>
	<%--code updation for R4 starts--%>
<div>&nbsp;</div>
<c:set var="entityType"><%=HHSConstants.AUDIT_CONTRACT_BUDGET_MODIFICATION%></c:set>
<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_BUDGET_MODIFICATION%></c:set>
<div class='gridFormField'>
<c:choose>
<c:when test="${detailsBeanForTaskGrid.isTaskScreen}">
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_milestone_${subBudgetId}"
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
	entityTypeTabLevel="TLC_milestone_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>	
</div>
	<%--code updation for R4 ends--%>
</div>