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

<%-- This JSP is for screen S355 Milestone - Contract Budget Amendment. --%>

<portlet:defineObjects />

<H3>Milestone</H3>

<%--This portlet:resourceURL is for Grid header properties --%>
<portlet:resourceURL var='MilestoneSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractBudgetAmendMilestone.grid"/>
</portlet:resourceURL>

<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<c:set var="isNegativeAmend" value="true"></c:set>

<%-- set the amendment Type for positive and negative--%>
<c:set var="amendmentGrid" value="positive,positiveAmendmentMsg"></c:set>
<c:if test="${amendmentType ne 'positive'}">
<c:set var="isNegativeAmend" value="false"></c:set>
<c:set var="amendmentGrid" value="negative,negativeAmendmentMsg"></c:set>
</c:if>


<c:set var="gridColNames"><%=HHSUtil.getHeader("contractBudgetAmendMilestone.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractBudgetAmendMilestone.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractBudgetAmendMilestone.grid")%></c:set>

<%--This portlet:resourceURL is for Grid data load call --%>
<portlet:resourceURL var='loadBudgetMilestone' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="getCBAmendMilestoneGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBMileStoneBean"/>
<portlet:param name="gridLabel" value="contractBudgetAmendMilestone.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>

<%--This portlet:resourceURL is for Grid data operations call --%>
<portlet:resourceURL var='milestoneOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="getCBAmendMilestoneGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBMileStoneBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>


<jq:grid id="milestoneAmendmentGrid-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${MilestoneSubGridHeaderRow}"
		 subGridUrl="${loadBudgetMilestone}"
	     cellUrl="${milestoneOperationGrid}"
	     editUrl="${milestoneOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
	     rowsPerPage="5"
	     modificationType="${amendmentGrid}"
         isSubGrid="true" isNewRecordDelete="true"
         nonEditColumnName="amount,remainAmt"
         negativeCurrency="modificationAmount"
	     operations="del:true,edit:true,add:${isNegativeAmend},cancel:true,save:true"
/>

<div>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncomeAmendment.jsp">
	<jsp:param value="8" name="entryTypeId" />
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