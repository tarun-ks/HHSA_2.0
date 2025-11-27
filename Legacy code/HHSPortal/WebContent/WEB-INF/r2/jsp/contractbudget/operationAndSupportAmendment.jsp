<%-- This jsp is used to display amendment for operation and support tab, screen S349--%>
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
      <span class="label">Amendment Total Operations, Support and Equipment:</span>
      <span class="formfield">
      		<span class='lftAmount'>
      			<label id="fyBudgetModOTPS${subBudgetId}">${loCBOperationSupportBean.fyBudget}</label>
      		</span>
      </span>
 </div>
  
 </div>
 
<div class='clear'>&nbsp;</div>

<%-- Below resource url is for OTPS amendment grid headers --%>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="operationAndSupportAmendment.grid"/>
</portlet:resourceURL>

<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
	<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<c:set var="isNegativeAmend" value="true"></c:set>

<c:set var="gridColNames"><%=HHSUtil.getHeader("operationAndSupportAmendment.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("operationAndSupportAmendment.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("operationAndSupportAmendment.grid")%></c:set>

<%-- set the amendment Type for positive and negative --%>
<c:set var="amendmentGridOtpsOpAndSup" value="positive,positiveAmendmentMsg"></c:set>
<c:set var="subGridColPropVar" value="{editable:false,editrules:{isMandatoryField},editoptions:{maxlength:50}},{editable:true,editrules:{required:true,integer:true},editoptions:{maxlength:5,dataInit:function(elem){setTimeout(function(){$(elem).numeric_Grid('positive');},100);}}},{editable:false,editrules:{required:false}},{editable:false,editrules:{required:false}},{editable:true,editrules:{required:true,number:true}},{editable:false,editrules:{required:false}} "></c:set>
<c:if test="${amendmentType ne 'positive'}">
<c:set var="isNegativeAmend" value="false"></c:set>
<c:set var="amendmentGridOtpsOpAndSup" value="negative,negativeAmendmentMsg"></c:set>
<c:set var="subGridColPropVar" value="{editable:false,editrules:{isMandatoryField},editoptions:{maxlength:50}},{editable:true,editrules:{required:true,integer:true},editoptions:{maxlength:6,dataInit:function(elem){setTimeout(function(){$(elem).numeric_Grid('negative');},100);}}},{editable:false,editrules:{required:false}},{editable:false,editrules:{required:false}},{editable:true,editrules:{required:true,number:true}},{editable:false,editrules:{required:false}} "></c:set>
</c:if>

<%-- Below resource url is for fetching/onload of OTPS amendment grid--%>
<portlet:resourceURL var='loadOperationSupportAmendGrid' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="operationAndSupportAmendmentGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBOperationSupportBean"/>
<portlet:param name="gridLabel" value="operationAndSupportAmendment.grid"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<%-- Below resource url is for operations on OTPS amendment grid--%>
<portlet:resourceURL var='operationSupportAmendGridActions' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="operationAndSupportAmendmentGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBOperationSupportBean"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<jq:grid id="operationAndSuppAmendGrid-${subBudgetId}" 
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadOperationSupportAmendGrid}"
	     cellUrl="${operationSupportAmendGridActions}"
	     editUrl="${operationSupportAmendGridActions}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="false" lastRowEdit="false"
	     rowsPerPage="21" nonEditColumnName="fyBudget,remainingAmt"
	     modificationType="${amendmentGridOtpsOpAndSup}"
         isSubGrid="true"
         negativeCurrency="amendAmt"
         isReadOnly="${readOnlyPageAttribute}"
	     operations="del:false,edit:true,add:false,cancel:true,save:true"
	     callbackFunction="refreshNonGridDataContBudModOTPS('${subBudgetId}');"
/>
<div>&nbsp;</div>

<%-- Below resource url is for Equipment amendment grid headers --%>
<portlet:resourceURL var='SubGridHeaderRow1' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="equipmentAmendment.grid"/>
</portlet:resourceURL>

<c:set var="gridColNames1"><%=HHSUtil.getHeader("equipmentAmendment.grid")%></c:set>
<c:set var="gridColProp1"><%=HHSUtil.getHeaderProp("equipmentAmendment.grid")%></c:set>
<c:set var="subGridColProp1"><%=HHSUtil.getSubGridProp("equipmentAmendment.grid")%></c:set>

<%-- set the amendment Type for positive and negative --%>
<c:set var="amendmentGridOtpsEquipment" value="positive,positiveAmendmentMsg"></c:set>
<c:if test="${amendmentType ne 'positive'}">
<c:set var="amendmentGridOtpsEquipment" value="negative,negativeAmendmentMsg"></c:set>
</c:if>

<%-- Below resource url is for fetching/onload of Equipment amendment grid--%>
<portlet:resourceURL var='loadEquipmentGridAmend' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="equipmentDetailsAmendmentGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBEquipmentBean"/>
<portlet:param name="gridLabel" value="equipmentAmendment.grid"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<%-- Below resource url is for operations on Equipment amendment grid--%>
<portlet:resourceURL var='equipmentGridActions1' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="equipmentDetailsAmendmentGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBEquipmentBean"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<jq:grid id="equipmentGridAmend-${subBudgetId}" 
         gridColNames="${gridColNames1}" 
	     gridColProp="${gridColProp1}" 
	     subGridColProp="${subGridColPropVar}" 
		 gridUrl="${SubGridHeaderRow1}"
		 subGridUrl="${loadEquipmentGridAmend}"
	     cellUrl="${equipmentGridActions1}"
	     editUrl="${equipmentGridActions1}"
	     dataType="json" methodType="POST"
	     columnTotalName=""	     
         isPagination="true" lastRowEdit="false"
         nonEditColumnName="fyBudget,remainingAmt"
	     rowsPerPage="5"
	     modificationType="${amendmentGridOtpsEquipment}"
         isSubGrid="true"
         negativeCurrency="amendAmt"
         isReadOnly="${readOnlyPageAttribute}"
         isNewRecordDelete="true"
	     operations="del:true,edit:true,add:${isNegativeAmend},cancel:true,save:true"
	     callbackFunction="refreshNonGridDataContBudModOTPS('${subBudgetId}');"
/>
	<div>&nbsp;</div>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
<jsp:include page="programIncomeAmendment.jsp">
	<jsp:param value="2" name="entryTypeId" />
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
			$("#fyBudgetModOTPS"+subBudgetID).jqGridCurrency();
			
		});

</script>
