<%-- JSP for S354 Contract Budget Amendment - Rate Grid --%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil" %>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />
<%-- 
This jsp is used for S354 Rate screen.
--%>
<H3>Rate</H3>
<%-- This portlet resource is used get the Grid Header properties --%>
<portlet:resourceURL var='RateSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="amendmentBudgetRate.grid"/>
</portlet:resourceURL>
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<c:set var="isNegativeAmend" value="true"></c:set>

<c:set var="gridColNames"><%=HHSUtil.getHeader("amendmentBudgetRate.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("amendmentBudgetRate.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("amendmentBudgetRate.grid")%></c:set>
<%-- This portlet resource is used to map "loadGridData" resource in ContractBudgetAmendment Controller 
---- to load Grid data	--%>
<portlet:resourceURL var='loadAmendmentRate' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="contractBudgetAmendmentRate"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.RateBean"/>
<portlet:param name="gridLabel" value="amendmentBudgetRate.grid"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<%-- This portlet resource is used to map "gridOperation" resource in ContractBudgetAmendment Controller
---- to perform grid operations --%>
<portlet:resourceURL var='rateOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="contractBudgetAmendmentRate"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.RateBean"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<c:set var="amendmentGrid" value="positive,positiveAmendmentMsg"></c:set>
<c:set var="subGridColPropVar" value="{editable:false,editrules:{isMandatoryField},editoptions:{maxlength:50}},{editable:false,editrules:{required:true,number:true}},{editable:false,editrules:{required:true,number:true}},{editable:false,editrules:{required:true,number:true}},{editable:true,editrules:{required:true,integer:true},editoptions:{maxlength:6,dataInit:function(elem){setTimeout(function(){$(elem).numeric_Grid('positive');},100);}}},{editable:true,editrules:{required:true,number:true}}"></c:set>
<c:if test="${amendmentType ne 'positive'}">
<c:set var="amendmentGrid" value="negative,negativeAmendmentMsg"></c:set>
<c:set var="isNegativeAmend" value="false"></c:set>
<c:set var="subGridColPropVar" value="{editable:false,editrules:{isMandatoryField},editoptions:{maxlength:50}},{editable:false,editrules:{required:true,number:true}},{editable:false,editrules:{required:true,number:true}},{editable:false,editrules:{required:true,number:true}},{editable:true,editrules:{required:true,integer:true},editoptions:{maxlength:7,dataInit:function(elem){setTimeout(function(){$(elem).numeric_Grid('negative');},100);}}},{editable:true,editrules:{required:true,number:true}}"></c:set>
</c:if>

<jq:grid id="modificationRateGrid-${subBudgetId}"
	modificationType="${amendmentGrid}"
	isReadOnly="${readOnlyPageAttribute}" gridColNames="${gridColNames}"
	gridColProp="${gridColProp}"
	subGridColProp="${subGridColPropVar}"
	gridUrl="${RateSubGridHeaderRow}" subGridUrl="${loadAmendmentRate}"
	cellUrl="${rateOperationGrid}" editUrl="${rateOperationGrid}"
	dataType="json" methodType="POST"
	columnTotalName="" isPagination="true"
	rowsPerPage="5" isSubGrid="true" isNewRecordDelete="true"
	nonEditColumnName="remUnits,remainAmt,fyBudget"
	operations="del:true,edit:true,add:${isNegativeAmend},cancel:true,save:true"
	negativeCurrency="lsModifyAmount" />

<div>

</div>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncomeAmendment.jsp">
	<jsp:param value="7" name="entryTypeId" />
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
					<%--code updation for R4 ends--%>