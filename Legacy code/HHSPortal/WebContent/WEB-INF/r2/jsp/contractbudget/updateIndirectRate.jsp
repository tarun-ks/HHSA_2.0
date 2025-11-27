<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page	import="com.nyc.hhs.constants.*"%>
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties"/>
<portlet:defineObjects />
<%-- 
This jsp is used for S373 Indirect Rate.
--%>

<c:set var="readOnlyStatusIndirectRate">
<fmt:message key='BUDGET_PENDING_APPROVAL'/>,
<fmt:message key="BUDGET_APPROVED"/>,
<fmt:message key="BUDGET_SUSPENDED"/>
</c:set>

<%-- 
This resource url used to refresh non grid data.
--%>
<input type = 'hidden' value='${subBudgetId}' id='subBudgetId'/>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="updateBudgetIndirectRate.grid"/>
</portlet:resourceURL>
<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="contractBudgetIndirectRate"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBIndirectRateBean"/>
<portlet:param name="gridLabel" value="updateBudgetIndirectRate.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>
<portlet:resourceURL var='indirectRateOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="contractBudgetIndirectRate"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBIndirectRateBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>
<c:set var="gridColNames"><%=HHSUtil.getHeader("updateBudgetIndirectRate.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("updateBudgetIndirectRate.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("updateBudgetIndirectRate.grid")%></c:set>

<h3>Indirect Rate</h3>

<div class="formcontainer paymentFormWrapper widthFull">
	<div class="row">
	      <span class="label">Indirect Rate - City Funded:</span>
	      <span class="formfield">
	      	<span id='indirectRate${subBudgetId}'>${indirectPercentage}</span><span>%</span>
	      </span>
	</div>
</div>

	<div class='clear'>&nbsp;</div>

<jq:grid id="indirectRateGrid-${subBudgetId}" 
        isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadGridData}"
	     cellUrl="${indirectRateOperationGrid}"
	     editUrl="${indirectRateOperationGrid}"
	     dataType="json" methodType="POST"
         isPagination="false"
	     rowsPerPage="5"
         isSubGrid="true"
         columnTotalName="indirectAmount,indirectModificationAmount"
   	     operations="del:false,edit:true,add:false,cancel:true,save:true"
   	     callbackFunction="refreshNonGridIndirectRateData('${subBudgetId}');"
   	     negativeCurrency="indirectModificationAmount"
/>

<%-- R7 changes --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
<%-- Added in R7 for Program income grid in budget categories --%>
<div>&nbsp;</div>
<jsp:include page="programIncomeUpdate.jsp">
	<jsp:param value="10" name="entryTypeId" />
</jsp:include>
</c:if>
<%-- R7 changes end --%>

<%-- code updation for R4 starts--%>
<div>&nbsp;</div>
<c:set var="entityType"><%=HHSConstants.AUDIT_CONTRACT_BUDGET_UPDATE%></c:set>
<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_BUDGET_UPDATE%></c:set>
<div class='gridFormField'>
<c:choose>
<c:when test="${detailsBeanForTaskGrid.isTaskScreen}">
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_indirectRate_${subBudgetId}"
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
	entityTypeTabLevel="TLC_indirectRate_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>
</div>
<%-- code updation for R4 ends--%>