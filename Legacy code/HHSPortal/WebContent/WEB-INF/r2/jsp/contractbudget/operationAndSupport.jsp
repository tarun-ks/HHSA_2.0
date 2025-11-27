<%-- This jsp is used to display operation support and equipment grid, screen S316--%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@ taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@ page import="com.nyc.hhs.util.HHSUtil" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ page import="com.nyc.hhs.constants.*"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<portlet:defineObjects />

<H3>OTPS - Operations and Support</H3>
<div class='formcontainer paymentFormWrapper' style='width:100%'>
 <div class="row">
      <span class="label">Total Operations, Support and Equipment:</span>
      <span class="formfield">
      	<span class='lftAmount'>
      		<label  id="fyBudgetOTPS${subBudgetId}">${loCBOperationSupportBean.fyBudget}</label>
      	</span>
      </span>
 </div>
  <div class="row">
      <span class="label">Total YTD Invoiced Amount:</span>
      <span class="formfield">
      	<span class='lftAmount'>
      		<label  id="ytdInvAmtOTPS${subBudgetId}">${loCBOperationSupportBean.ytdInvoicedAmt}</label>
      	</span>
      </span>
    </div>  
 </div>
 
 <div class='clear'>&nbsp;</div>
 
 <%-- Below resource url is for Operation and Support grid headers --%>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="operationAndSupport.grid"/>
</portlet:resourceURL>
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<%--  For Last Row make editable dynamically --%>
<c:set var="lastRowEditable" value="false"></c:set>
<c:if test="${readOnlyPageAttribute eq 'false'}">
<c:set var="lastRowEditable" value="true"></c:set>
</c:if>


<c:set var="gridColNames"><%=HHSUtil.getHeader("operationAndSupport.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("operationAndSupport.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("operationAndSupport.grid")%></c:set>

<%-- Below resource url is for fetching/onload of operation and support grid data--%>
<portlet:resourceURL var='loadOperationSupportGrid' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="operationAndSupportGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBOperationSupportBean"/>
<portlet:param name="gridLabel" value="operationAndSupport.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<%-- Below resource url is for operations on operation and support grid --%>
<portlet:resourceURL var='operationSupportGridActions' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="operationAndSupportGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBOperationSupportBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<jq:grid id="operationAndSuppGrid-${subBudgetId}" 
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadOperationSupportGrid}"
	     cellUrl="${operationSupportGridActions}"
	     editUrl="${operationSupportGridActions}"
	     dataType="json" methodType="POST"
	     columnTotalName="fyBudget,-ytdInvoicedAmt"
         isPagination="false" lastRowEdit="${lastRowEditable}"
	     rowsPerPage="21"
         isSubGrid="true"
         positiveCurrency="fyBudget"
         isReadOnly="${readOnlyPageAttribute}"
	     operations="del:false,edit:true,add:false,cancel:true,save:true"
	     callbackFunction="refreshNonGridDataContBudOTPS('${subBudgetId}');"
/>
	<div>&nbsp;</div>

<%-- Below resource url is for Equipment grid headers --%>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="equipment.grid"/>
</portlet:resourceURL>

<c:set var="gridColNames"><%=HHSUtil.getHeader("equipment.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("equipment.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("equipment.grid")%></c:set>

<%-- Below resource url is for fetching/onload of equipment grid data--%>
<portlet:resourceURL var='loadEquipmentGrid' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="equipmentDetailsGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBEquipmentBean"/>
<portlet:param name="gridLabel" value="equipment.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<%-- Below resource url is for operations on equipment grid --%>
<portlet:resourceURL var='equipmentGridActions' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="equipmentDetailsGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBEquipmentBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>


<jq:grid id="equipmentGrid-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadEquipmentGrid}"
	     cellUrl="${equipmentGridActions}"
	     editUrl="${equipmentGridActions}"
	     dataType="json" methodType="POST"
	     columnTotalName="fyBudget,-ytdInvoicedAmt"
         isPagination="true" 
         nonEditColumnName="ytdInvoicedAmt,remainingAmt"
	     rowsPerPage="5"
         isSubGrid="true"
         positiveCurrency="fyBudget"
	     operations="del:true,edit:true,add:true,cancel:true,save:true"
	     callbackFunction="refreshNonGridDataContBudOTPS('${subBudgetId}');"
/>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncome.jsp">
	<jsp:param value="2" name="entryTypeId" />
</jsp:include>
&nbsp;
</c:if>
<%-- R7 changes end --%>

<div>&nbsp;</div>
	<%--code updation for R4 starts--%>
<c:set var="entityType"><%=HHSConstants.BUDGET_TYPE3%></c:set>
<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_BUDGET_REVIEW%></c:set>
<div class='gridFormField'>
<c:choose>
<c:when test="${detailsBeanForTaskGrid.isTaskScreen}">
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_operationAndSupport_${subBudgetId}"
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
	entityTypeTabLevel="TLC_operationAndSupport_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>
</div>
	<%--code updation for R4 starts--%>
<script>
$(document)
.ready(
		function() {				
			var subBudgetID = ${subBudgetId};
			$("#fyBudgetOTPS"+subBudgetID).jqGridCurrency();
			$("#ytdInvAmtOTPS" + subBudgetID).jqGridCurrency();
		});

</script>	