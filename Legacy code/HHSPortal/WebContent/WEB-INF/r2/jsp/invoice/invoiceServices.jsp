<%-- Added in R7 for Cost Center: This jsp is used to display services and Cost Center grid, screen S509--%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@ taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@ page import="com.nyc.hhs.util.HHSUtil" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ page import="com.nyc.hhs.constants.*"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<portlet:defineObjects />

 <div class='clear'>&nbsp;</div>
 
<%-- Below resource url is for Equipment grid headers --%>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="InvoiceServices.grid"/>
</portlet:resourceURL>
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<c:set var="gridColNames"><%=HHSUtil.getHeader("InvoiceServices.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("InvoiceServices.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("InvoiceServices.grid")%></c:set>

<%-- Below resource url is for fetching/onload of equipment grid data--%>
<portlet:resourceURL var='loadServicesGrid' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="servicesInvoiceGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
<portlet:param name="gridLabel" value="InvoiceServices.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<%-- Below resource url is for operations on equipment grid --%>
<portlet:resourceURL var='servicesGridActions' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="servicesInvoiceGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<H3>Services</H3>


<jq:grid id="InvoiceServicesGrid-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadServicesGrid}"
	     cellUrl="${servicesGridActions}"
	     editUrl="${servicesGridActions}"
	     dataType="json" methodType="POST"
	     columnTotalName="ytdInvoicedAmt"
         isPagination="true" 
         nonEditColumnName="remainingAmt"
	     rowsPerPage="10"
         isSubGrid="true"
         negativeCurrency="ytdInvoicedAmt"
	     operations="del:false,edit:true,add:false,cancel:true,save:true"
	     exportFileName="SERVICES_DETAIL"
/>

<div>&nbsp;</div>

<%-- Below resource url is for Services grid headers --%>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="InvoiceCostCenter.grid"/>
</portlet:resourceURL>

<c:set var="gridColNames"><%=HHSUtil.getHeader("InvoiceCostCenter.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("InvoiceCostCenter.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("InvoiceCostCenter.grid")%></c:set>


<portlet:resourceURL var='loadCostCenterGrid' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="costCenterInvoiceGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
<portlet:param name="gridLabel" value="InvoiceCostCenter.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>


<portlet:resourceURL var='costCenterGridActions' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="costCenterInvoiceGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="invoiceId" value="${invoiceId}"/>
</portlet:resourceURL>

<H3>Cost center</H3>
<jq:grid id="InvoiceCostCenterGrid-${subBudgetId}" 
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadCostCenterGrid}"
	     cellUrl="${costCenterGridActions}"
	     editUrl="${costCenterGridActions}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
	     rowsPerPage="10"
         isSubGrid="true"
         negativeCurrency="ytdInvoicedAmt"
         isReadOnly="${readOnlyPageAttribute}"
	     operations="del:false,edit:true,add:false,cancel:true,save:true"
	     exportFileName="COST_CENTER_DETAIL"
/>

<div>&nbsp;</div>	
<c:set var="entityType"><%=HHSConstants.BUDGET_TYPE3%></c:set>
<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_BUDGET_REVIEW%></c:set>
<div class='gridFormField'>
<c:choose>
<c:when test="${detailsBeanForTaskGrid.isTaskScreen}">
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_services_${subBudgetId}"
	isTaskScreen=""
	commentsSection="comments"
	level="footer">
	</task:taskContent>
</c:when>
<c:otherwise>
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_services_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>
</div>