<%-- JSP for S352 Contract Budget Amendment - Rent Grid --%>
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
This jsp is used for S352 Rent screen.
--%>
<H3>OTPS - Rent</H3>
<%-- This portlet resource is used get the Grid Header properties --%>
<portlet:resourceURL var='RentSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="amendmentBudgetRent.grid"/>
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

<c:set var="gridColNames"><%=HHSUtil.getHeader("amendmentBudgetRent.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("amendmentBudgetRent.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("amendmentBudgetRent.grid")%></c:set>
<%-- This portlet resource is used to map "loadGridData" resource in ContractBudgetAmendment Controller 
---- to load Grid data	--%>

<portlet:resourceURL var='loadAmendmentRent' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="amendmentBudgetRent"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.Rent"/>
<portlet:param name="gridLabel" value="amendmentBudgetRent.grid"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<%-- This portlet resource is used to map "gridOperation" resource in ContractBudgetAmendment Controller
---- to perform grid operations --%>
<portlet:resourceURL var='rentOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="amendmentBudgetRent"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.Rent"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<jq:grid id="amendmentRentGrid-${subBudgetId}" 
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
                 {editable:true,editrules:{required:true,number:true}}"
		 gridUrl="${RentSubGridHeaderRow}"
		 subGridUrl="${loadAmendmentRent}"
	     cellUrl="${rentOperationGrid}"
	     editUrl="${rentOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
	     rowsPerPage="5"
	     negativeCurrency="amendmentAmount,modifyAmount"
	     modificationType="${amendmentGrid}"
         isSubGrid="true" isNewRecordDelete="true"
         nonEditColumnName="remainingAmt,fyBudget"
	     operations="del:true,edit:true,add:${isNegativeAmend},cancel:true,save:true"
/>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncomeAmendment.jsp">
	<jsp:param value="5" name="entryTypeId" />
</jsp:include>
</c:if>
<%-- R7 changes end --%>

<div>
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
