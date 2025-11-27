<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ page import="com.nyc.hhs.constants.*"%>
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties"/>
<portlet:defineObjects />


<%-- 
This jsp is used for S368 Rent Modification Rate.
--%>

<c:set var="readOnlyStatusIndirectRate">
<fmt:message key='BUDGET_PENDING_APPROVAL'/>,
<fmt:message key="BUDGET_APPROVED"/>,
<fmt:message key="BUDGET_ACTIVE"/>,
<fmt:message key="BUDGET_CANCELLED"/>,
<fmt:message key="BUDGET_SUSPENDED"/>,
<fmt:message key="BUDGET_CLOSED"/>
</c:set>


<%--
This file is designed to display Rent Grid for S368 Rent Modification module.
 --%>

<h3>OTPS - Rent</h3>
<%-- Resource URL to display Rent Grid header row --%>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="modificationBudgetRent.grid"/>
</portlet:resourceURL>


<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="modificationBudgetRent"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.Rent"/>
<portlet:param name="gridLabel" value="modificationBudgetRent.grid"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<%-- Portlet resource URL for edit Rent Grid data --%>
<portlet:resourceURL var='rentModificationOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="modificationBudgetRent"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.Rent"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>
<c:set var="gridColNames"><%=HHSUtil.getHeader("modificationBudgetRent.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("modificationBudgetRent.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("modificationBudgetRent.grid")%></c:set>

  <jq:grid id="rentModificationGrid-${subBudgetId}" 
       	 isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="{editable:false, editrules:{isMandatoryField},editoptions:{maxlength:50}},
                 {editable:false, editrules:{isMandatoryField},editoptions:{maxlength:50}},
                 {editable:false, editrules:{isMandatoryField},editoptions:{maxlength:50}},
                 {editable:false, editrules:{required:true},edittype : 'select',editoptions : {value : '1:Yes;0:No'}},
                  {editable:false, editrules:{required:true,number:true,allowOnlyPercentValue},editoptions:{dataInit:function(elem){setTimeout(function(){$(elem).autoNumeric({aSep:'',vMax:'100.00'});},100);}}},
                 {editable:false, editrules:{required:true,number:true,allowOnlyPositiveValue}},
                 {editable:false},
                 {editable:true,editrules:{required:true,number:true,allowBothSignCurrencyValue}},
                 {editable:false}"
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadGridData}"
	     cellUrl="${rentModificationOperationGrid}" 
	     editUrl="${rentModificationOperationGrid}" 
	     dataType="json" 
	     methodType="POST"
	     columnTotalName="fyBudget,modifyAmount"
	     isPagination="true"
	     isNewRecordDelete="true"
	     rowsPerPage="5"
         isSubGrid="true"
         negativeCurrency="modifyAmount"
	     nonEditColumnName="remainingAmt,fyBudget,proposedBudget"
         operations="del:true,edit:true,add:true,cancel:true,save:true"
         />
<div>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncomeModification.jsp">
	<jsp:param value="5" name="entryTypeId" />
</jsp:include>
</c:if>
<%-- R7 changes end --%>

<%-- code updation for R4 starts--%>
<div>&nbsp;</div>
<c:set var="entityType"><%=HHSConstants.AUDIT_CONTRACT_BUDGET_MODIFICATION%></c:set>
<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_BUDGET_MODIFICATION%></c:set>
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
