<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
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

<DIV></DIV>


<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
	<portlet:param name="gridLabel" value="am.UnallocatedFunds.grid" />
</portlet:resourceURL>

<%-- Code for Security matrix and readonly value --%> 
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
	<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>
<%-- QVC 8394 R 7.8.0 apply add/delete action to Unallocated Fund --%>
<c:set var="isNegativeAmend" value="true"></c:set>
<%-- set the amendment Type for positive and negative--%>
<c:set var="amendmentGrid" value="positive,positiveAmendmentMsg"></c:set>

<c:if test="${amendmentType ne 'positive'}">
	<c:set var="isNegativeAmend" value="false"></c:set>
	<%-- QVC 8394 R 7.8.0 apply add/delete action to Unallocated Fund --%>
	<c:set var="amendmentGrid" value="negative,negativeAmendmentMsg"></c:set>
</c:if>

<c:set var="gridColNames"><%=HHSUtil.getHeader("am.UnallocatedFunds.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("am.UnallocatedFunds.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("am.UnallocatedFunds.grid")%></c:set>


<%-- loading the page <portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean" />	  --%>
<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
	<portlet:param name="transactionName" value="amendmentUnallocatedFunds" />
	<portlet:param name="beanName" value="com.nyc.hhs.model.UnallocatedFunds" />
	<portlet:param name="gridLabel" value="am.UnallocatedFunds.grid" />
	<portlet:param name="subBudgetId" value="${subBudgetId}"/>
	<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>

<%-- operations for Edit/Update/Save --%>
<portlet:resourceURL var='UnallocatedFundsOperationGrid' id='gridOperation' escapeXml='false'>
	<portlet:param name="transactionName" value="amendmentUnallocatedFunds" />
	<portlet:param name="beanName" value="com.nyc.hhs.model.UnallocatedFunds" />
	<portlet:param name="subBudgetId" value="${subBudgetId}"/>
	<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>

<%-- JGrid for adding dynamic table --%>
			<jq:grid id="unallocatedFundsAmGrid-${subBudgetId}"  
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
				columnTotalName=""
				modificationType="${amendmentGrid}"
				isPagination="true"
				rowsPerPage="5" 
				isSubGrid="true"
				isNewRecordDelete="true"
				nonEditColumnName="ammount"
				negativeCurrency="modificationAmount" 
				operations="del:true,edit:true,add:${isNegativeAmend},cancel:true,save:true" />
				<%-- 
				 QC 8394 R 7.8.0 allow add and delete row for Unallocated Funds
	    		 operations="del:false,edit:true,add:false,cancel:true,save:true" /> 
	    		 isPagination="false" add:${isNegativeAmend}
	    		 
	    		 modificationType="${amendmentGrid}"
	    		--%>
				<%-- code updation for R4 starts--%>
<div>&nbsp;</div>
<c:set var="entityType"><%=HHSConstants.AUDIT_CONTRACT_BUDGET_AMENDMENT%></c:set>
<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_BUDGET_AMENDMENT%></c:set>
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