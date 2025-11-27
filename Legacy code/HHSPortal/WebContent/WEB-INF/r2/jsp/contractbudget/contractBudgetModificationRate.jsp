<%-- JSP for S370 Contract Budget Modification - Rate Grid --%>
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
<%-- 
This jsp is used for S370 Rate screen.
--%>
<H3>Rate</H3>
<%-- This portlet resource is used get the Grid Header properties --%>
<portlet:resourceURL var='RateSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="modificationBudgetRate.grid"/>
</portlet:resourceURL>
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<c:set var="gridColNames"><%=HHSUtil.getHeader("modificationBudgetRate.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("modificationBudgetRate.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("modificationBudgetRate.grid")%></c:set>
<%-- This portlet resource is used to map "loadGridData" resource in ContractBudgetModification Controller 
---- to load Grid data	--%>
<portlet:resourceURL var='loadBudgetModificationRate' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="contractBudgetModificationRate"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.RateBean"/>
<portlet:param name="gridLabel" value="modificationBudgetRate.grid"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<%-- This portlet resource is used to map "gridOperation" resource in ContractBudgetModification Controller
---- to perform grid operations --%>
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
		 subGridUrl="${loadBudgetModificationRate}"
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
<jsp:include page="programIncomeModification.jsp">
	<jsp:param value="7" name="entryTypeId" />
</jsp:include>
</c:if>
<%-- R7 changes end --%>

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
	entityTypeTabLevel="TLC_rate_${subBudgetId}"
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
	entityTypeTabLevel="TLC_rate_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>
</div>
					<%--code updation for R4 starts--%>