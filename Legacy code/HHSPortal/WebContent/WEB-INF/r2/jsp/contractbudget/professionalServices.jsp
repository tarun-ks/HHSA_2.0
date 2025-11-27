<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@ page import="com.nyc.hhs.util.HHSUtil" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.constants.*"%>
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties"/>
<portlet:defineObjects />
<%-- 
This jsp is used as a poc for grids with static headers.
It will serve as a reference while creating page specs jsps having grids.
 --%>
 
 <H3>OTPS - Professional Services</H3>
 
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="professionalServices.grid"/>
</portlet:resourceURL>
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<c:set var="isLastRowEdit" value="true"></c:set>

<c:if test="${readOnlyPageAttribute eq true}">
<c:set var="isLastRowEdit" value="false"></c:set>
</c:if>

<c:set var="gridColNames"><%=HHSUtil.getHeader("professionalServices.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("professionalServices.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("professionalServices.grid")%></c:set>
<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="profServicesGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBProfessionalServicesBean"/>
<portlet:param name="gridLabel" value="professionalServices.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<portlet:resourceURL var='professionalServicesGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="profServicesGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBProfessionalServicesBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<c:set var="readOnlyStatusProfService">
<fmt:message key='BUDGET_PENDING_APPROVAL'/>,
<fmt:message key="BUDGET_APPROVED"/>,
<fmt:message key="BUDGET_ACTIVE"/>,
<fmt:message key="BUDGET_CANCELLED"/>,
<fmt:message key="BUDGET_SUSPENDED"/>,
<fmt:message key="BUDGET_CLOSED"/>
</c:set>

<jq:grid id="profServicesGrid-${subBudgetId}" 
        isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadGridData}"
	     cellUrl="${professionalServicesGrid}"
	     editUrl="${professionalServicesGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName="fyBudget,-ytdInvoicedAmt"
	     positiveCurrency="fyBudget"
         isPagination="false"
	     rowsPerPage="5"
         isSubGrid="true"
         lastRowEdit="${isLastRowEdit}"
	     operations="del:false,edit:true,add:false,cancel:true,save:true"
/>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncome.jsp">
	<jsp:param value="4" name="entryTypeId" />
</jsp:include>
</c:if>
<%--R7 changes end --%>

<%--code updation starts for R4--%>
<div>&nbsp;</div>
<c:set var="entityType"><%=HHSConstants.BUDGET_TYPE3%></c:set>
<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_BUDGET_REVIEW%></c:set>
<div class='gridFormField'>
<c:choose>
<c:when test="${detailsBeanForTaskGrid.isTaskScreen}">
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_professionalServices_${subBudgetId}"
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
	entityTypeTabLevel="TLC_professionalServices_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>	
</div>
<%--code updation ends for R4--%>