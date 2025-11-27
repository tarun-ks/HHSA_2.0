<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil"%>
<%@page	import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties" />
<portlet:defineObjects />
<%-- 
This jsp is used for unallocated Funds in Contract Budget for grids with static headers.
Unallocated Funds tab allows for the holding of an increase or 
decrease of a budget where the specificity is not yet defined.
 --%>

<H3>Unallocated Funds</H3>

<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow'escapeXml='false'>
	<portlet:param name="gridLabel" value="update.UnallocatedFunds.grid" />
</portlet:resourceURL>


<%-- Code for Security matrix and readonly value --%> 
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
	<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>


<c:set var="gridColNames"><%=HHSUtil.getHeader("update.UnallocatedFunds.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("update.UnallocatedFunds.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("update.UnallocatedFunds.grid")%></c:set>

<%-- loading the page  --%>
<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
	<portlet:param name="transactionName" value="modificationUnallocatedFunds" />
	<portlet:param name="beanName" value="com.nyc.hhs.model.UnallocatedFunds" />
	<portlet:param name="gridLabel" value="update.UnallocatedFunds.grid" />
	<portlet:param name="subBudgetId" value="${subBudgetId}"/>
	<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>

<%-- operations for Edit/Update/Save --%>
<portlet:resourceURL var='UnallocatedFundsOperationGrid' id='gridOperation' escapeXml='false'>
	<portlet:param name="transactionName" value="modificationUnallocatedFunds" />
	<portlet:param name="beanName" value="com.nyc.hhs.model.UnallocatedFunds" />
	<portlet:param name="subBudgetId" value="${subBudgetId}"/>
	<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>

<%-- JGrid for adding dynamic table --%>
			<jq:grid id="unallocatedFundsUpGrid-${subBudgetId}" 
			 isReadOnly="${readOnlyPageAttribute}"
				gridColNames="${gridColNames}"
				gridColProp="${gridColProp}" 
				subGridColProp="${subGridColProp}"
				gridUrl="${SubGridHeaderRow}"
				subGridUrl="${loadGridData}"
				cellUrl="${UnallocatedFundsOperationGrid}"
				editUrl="${UnallocatedFundsOperationGrid}"
				dataType="json"
				methodType="POST" 
				columnTotalName="ammount,modificationAmount" 
				isPagination="true"
				rowsPerPage="5" 
				isSubGrid="true"
				isNewRecordDelete="true"
				nonEditColumnName="ammount,proposedBudget"
				negativeCurrency="modificationAmount" 
				operations="del:true,edit:true,add:true,cancel:true,save:true" />

				<%-- code updation for R4 starts--%>
				<%-- 
				QC 8394 R 7.8.0 apply functionality to add/delete line to Unallocated Funds
				operations="del:false,edit:true,add:false,cancel:true,save:true" /> 
				isPagination="true" 
				--%>
<div>&nbsp;</div>
<c:set var="entityType"><%=HHSConstants.AUDIT_CONTRACT_BUDGET_UPDATE%></c:set>
<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_BUDGET_UPDATE%></c:set>
<div class='gridFormField'>
<c:choose>
<c:when test="${detailsBeanForTaskGrid.isTaskScreen}">
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_unallocatedFunds_${subBudgetId}"
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
	entityTypeTabLevel="TLC_unallocatedFunds_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>
</div>	
<%-- code updation for R4 ends--%>