<%-- This jsp is used to display update for operation and support tab, screen S379--%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@ taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@ page import="com.nyc.hhs.constants.*"%>
<%@ page import="com.nyc.hhs.util.HHSUtil" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<portlet:defineObjects />

<H3>OTPS - Operations and Support</H3>
<div class='formcontainer paymentFormWrapper' style='width:92%'>
 <div class="row">
      <span class="label">Update Total Operations, Support and Equipment:</span>
      <span class="formfield">
      		<span class='lftAmount'>
      			<label id="fyBudgetModOTPS${subBudgetId}">${loCBOperationSupportBean.fyBudget}</label>
      		</span>
      </span>
 </div>
  <div class="row">
      <span class="label">Total YTD Invoiced Amount:</span>
      <span class="formfield">
      	<span class='lftAmount' >
      		<label id="ytdInvAmtModOTPS${subBudgetId}">${loCBOperationSupportBean.ytdInvoicedAmt}</label>
      	</span>
      </span>
    </div>  
 </div>
 
<div class='clear'>&nbsp;</div>

<%-- Below resource url is for OTPS update grid headers --%>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="operationAndSupportUpdate.grid"/>
</portlet:resourceURL>

<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
	<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<c:set var="gridColNames"><%=HHSUtil.getHeader("operationAndSupportUpdate.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("operationAndSupportUpdate.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("operationAndSupportUpdate.grid")%></c:set>

<%-- Below resource url is for fetching/onload of OTPS update grid--%>
<portlet:resourceURL var='loadOperationSupportGrid' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="operationAndSupportModificationGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBOperationSupportBean"/>
<portlet:param name="gridLabel" value="operationAndSupportUpdate.grid"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<%-- Below resource url is for operations on OTPS update grid--%>
<portlet:resourceURL var='operationSupportGridActions' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="operationAndSupportModificationGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBOperationSupportBean"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
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
	     columnTotalName="fyBudget,modificationAmt"
         isPagination="false" lastRowEdit="false"
	     rowsPerPage="21" nonEditColumnName="fyBudget,remainingAmt,proposedBudget"
         isSubGrid="true"
         negativeCurrency="modificationAmt"
         isReadOnly="${readOnlyPageAttribute}"
	     operations="del:false,edit:true,add:false,cancel:true,save:true"
	     callbackFunction="refreshNonGridDataContBudModOTPS('${subBudgetId}');"
/>
<div>&nbsp;</div>

<%-- Below resource url is for Equipment update grid headers --%>
<portlet:resourceURL var='SubGridHeaderRow1' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="equipmentUpdate.grid"/>
</portlet:resourceURL>

<c:set var="gridColNames1"><%=HHSUtil.getHeader("equipmentUpdate.grid")%></c:set>
<c:set var="gridColProp1"><%=HHSUtil.getHeaderProp("equipmentUpdate.grid")%></c:set>
<c:set var="subGridColProp1"><%=HHSUtil.getSubGridProp("equipmentUpdate.grid")%></c:set>

<%-- Below resource url is for fetching/onload of Equipment update grid--%>
<portlet:resourceURL var='loadEquipmentGrid1' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="equipmentDetailsModificationGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBEquipmentBean"/>
<portlet:param name="gridLabel" value="equipmentUpdate.grid"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<%-- Below resource url is for operations on Equipment update grid--%>
<portlet:resourceURL var='equipmentGridActions1' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="equipmentDetailsModificationGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBEquipmentBean"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<jq:grid id="equipmentGrid123-${subBudgetId}" 
         gridColNames="${gridColNames1}" 
	     gridColProp="${gridColProp1}" 
	     subGridColProp="${subGridColProp1}" 
		 gridUrl="${SubGridHeaderRow1}"
		 subGridUrl="${loadEquipmentGrid1}"
	     cellUrl="${equipmentGridActions1}"
	     editUrl="${equipmentGridActions1}"
	     dataType="json" methodType="POST"
	     columnTotalName="fyBudget,modificationAmt"	     
         isPagination="true" lastRowEdit="false"
         nonEditColumnName="fyBudget,remainingAmt,proposedBudget"
	     rowsPerPage="5"
         isSubGrid="true"
         negativeCurrency="modificationAmt"
         isReadOnly="${readOnlyPageAttribute}"
         isNewRecordDelete="true"
	     operations="del:true,edit:true,add:true,cancel:true,save:true"
	     callbackFunction="refreshNonGridDataContBudModOTPS('${subBudgetId}');"
/>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncomeUpdate.jsp">
	<jsp:param value="2" name="entryTypeId" />
</jsp:include>
</c:if>
<%-- R7 changes end --%>

	<div>&nbsp;</div>
	<%--code updation for R4 starts--%>
<c:set var="entityType"><%=HHSConstants.AUDIT_CONTRACT_BUDGET_UPDATE%></c:set>
<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_BUDGET_UPDATE%></c:set>
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
	<%--code updation for R4 ends--%>
<script>
$(document)
.ready(
		function() {				
			var subBudgetID = ${subBudgetId};
			$("#fyBudgetModOTPS" + subBudgetID).jqGridCurrency();
			$("#ytdInvAmtModOTPS" + subBudgetID).jqGridCurrency();
		});

</script>	