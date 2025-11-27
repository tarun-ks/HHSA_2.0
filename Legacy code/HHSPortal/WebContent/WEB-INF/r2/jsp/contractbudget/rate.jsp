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
This jsp is used for S321 Rate screen.
--%>
<H3>Rate</H3>
<portlet:resourceURL var='RateSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractBudgetRate.grid"/>
</portlet:resourceURL>
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<c:set var="gridColNames"><%=HHSUtil.getHeader("contractBudgetRate.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractBudgetRate.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractBudgetRate.grid")%></c:set>
<portlet:resourceURL var='loadBudgetRate' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="getContractBudgetRateGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
<portlet:param name="gridLabel" value="contractBudgetRate.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<portlet:resourceURL var='rateOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="getContractBudgetRateGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.RateBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>


 
<jq:grid id="budgetRateGrid-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${RateSubGridHeaderRow}"
		 subGridUrl="${loadBudgetRate}"
	     cellUrl="${rateOperationGrid}"
	     editUrl="${rateOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName="fyBudget,-ytdInvoiceAmt"
         isPagination="true"
	     rowsPerPage="5"
         isSubGrid="true"
         nonEditColumnName="ytdUnits,remUnits,ytdInvoiceAmt,remainAmt"
         positiveCurrency="fyBudget" 
         operations="del:true,edit:true,add:true,cancel:true,save:true"
/>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncome.jsp">
	<jsp:param value="7" name="entryTypeId" />
</jsp:include>
</c:if>
<%--R7 changes end --%>

<%-- code updation for R4 starts--%>
<div>&nbsp;</div>
<c:set var="entityType"><%=HHSConstants.BUDGET_TYPE3%></c:set>
<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_BUDGET_REVIEW%></c:set>
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
<%-- code updation for R4 ends--%>
<div>

</div>
