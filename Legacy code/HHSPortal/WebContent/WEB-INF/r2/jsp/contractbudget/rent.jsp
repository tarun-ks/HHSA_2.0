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
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties"/>
<portlet:defineObjects />
<%-- 
This jsp is used for S319 Rent screen.
--%>
<c:set var="readOnlyStatusRent">
<fmt:message key='BUDGET_PENDING_APPROVAL'/>,
<fmt:message key="BUDGET_APPROVED"/>,
<fmt:message key="BUDGET_ACTIVE"/>,
<fmt:message key="BUDGET_CANCELLED"/>,
<fmt:message key="BUDGET_SUSPENDED"/>,
<fmt:message key="BUDGET_CLOSED"/>
</c:set>

<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractBudgetRent.grid"/>
</portlet:resourceURL>

<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="contractBudgetRent"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.Rent"/>
<portlet:param name="gridLabel" value="contractBudgetRent.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<portlet:resourceURL var='RentOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="contractBudgetRent"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.Rent"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>
<c:set var="gridColNames"><%=HHSUtil.getHeader("contractBudgetRent.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractBudgetRent.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractBudgetRent.grid")%></c:set>

<h3>OTPS - Rent</h3>

<jq:grid id="rentGrid-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="{editable:true, editrules:{isMandatoryField},editoptions:{maxlength:50}},
                 {editable:true, editrules:{isMandatoryField},editoptions:{maxlength:50}},
                 {editable:true, editrules:{isMandatoryField},editoptions:{maxlength:50}},
                 {editable:true, editrules:{required:true},edittype : 'select',editoptions : {value : '1:Yes;0:No'}},
                 {editable:true, editrules:{required:true,number:true,allowOnlyPercentValue},editoptions:{dataInit:function(elem){setTimeout(function(){$(elem).autoNumeric({aSep:'',vMax:'100.00'});},100);}}},
                 {editable:true, editrules:{required:true,number:true,allowOnlyPositiveValue}},
                 {editable:false},
                 {editable:false}"
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadGridData}"
	     cellUrl="${RentOperationGrid}"
	     editUrl="${RentOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName="fyBudget,-ytdInvoiceAmt"
         isPagination="true"
	     rowsPerPage="5"
         isSubGrid="true" autoWidth="true"
   	     operations="del:true,edit:true,add:true,cancel:true,save:true"
   	     positiveCurrency="fyBudget" 
   	     nonEditColumnName="ytdInvoiceAmt,remainingAmt"
/>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
<div>&nbsp;</div>
<jsp:include page="programIncome.jsp">
	<jsp:param value="5" name="entryTypeId" />
</jsp:include>
</c:if>
<%--R7 changes end --%>

<div>
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
	entityTypeTabLevel="TLC_rent_${subBudgetId}"
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
	entityTypeTabLevel="TLC_rent_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>	
</div>
<%-- code updation for R4 ends--%>
</div>
