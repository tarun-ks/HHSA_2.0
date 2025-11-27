<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil" %>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />
<%-- 
This jsp is used for S400 Assignment in Contract Invoice screen.
--%>

<DIV></DIV>

<%-- This portlet resource is for loading Assignment Grid header --%>
<portlet:resourceURL var='RateSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="paymentAssignment.grid"/>
</portlet:resourceURL>
<%-- This portlet resource is for Edit/Delete an  Assignment in the Grid --%>
<portlet:resourceURL var='AssignmentOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="getPaymentAssignmentGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.AssignmentsSummaryBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<%-- This portlet resource is called to open Add Assignee Overlay --%>
<portlet:resourceURL var="addPaymentAssigneeOverlay" id="addPaymentAssigneeOverlay" escapeXml="false" />
<input type="hidden" id="addAssigneeOverlayVar" value="${addPaymentAssigneeOverlay}"/>

<c:set var="readOnlyPageAttribute" value="true"></c:set>
<c:if
	test="${detailsBeanForTaskGrid.isTaskScreen && detailsBeanForTaskGrid.isTaskAssigned && detailsBeanForTaskGrid.level eq '1'  }">
	<c:set var="readOnlyPageAttribute" value="false"></c:set>
</c:if>

<c:set var="gridColNames"><%=HHSUtil.getHeader("paymentAssignment.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("paymentAssignment.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("paymentAssignment.grid")%></c:set>

<%-- This portlet resource is for loading Assignment Grid data --%>
<portlet:resourceURL var='loadBudgetAssignment' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="getPaymentAssignmentGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.AssignmentsSummaryBean"/>
<portlet:param name="gridLabel" value="paymentAssignment.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<jq:grid id="advanceAssignmentGrid-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${RateSubGridHeaderRow}"
		 subGridUrl="${loadBudgetAssignment}"
	     cellUrl="${AssignmentOperationGrid}"
	     editUrl="${AssignmentOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
	     rowsPerPage="5"
         isSubGrid="true"
         nonEditColumnName=""
         checkForZeroAndDelete="ytdAssignmentAmount,INVOICE_ASSIGNMENT_DELETE_CHECK"
         positiveCurrency="assignmentAmount"
	     operations="del:true,edit:true,add:false,cancel:true,save:true"
/>

<div class="buttonholder">
<c:if test="${readOnlyPageAttribute ne 'true'}">
<input type="button" class="button" title='Add Assignee' value="Add Assignee" onclick="openOverlayAssignee();" />
</c:if>
</div>