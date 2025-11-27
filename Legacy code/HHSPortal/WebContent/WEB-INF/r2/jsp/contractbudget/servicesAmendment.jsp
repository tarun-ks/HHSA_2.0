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
<%-- This JSP is for Services Amendment grid screen --%>

<H3>Service</H3>

<%-- This portlet resource maps the Action in Base Controller to display header data in Services Amendment grid  --%>
<portlet:resourceURL var='servicesSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
	<portlet:param name="gridLabel" value="amendmentServices.grid"/>
</portlet:resourceURL>

<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<%-- set the amendment Type for positive and negative--%>
<c:set var="amendmentGrid" value="positive,positiveAmendmentMsg"></c:set>
<c:set var="subGridColProp1" value="{editable:false,editrules:{required:true}},{editable:false,editrules:{required:false,integer:true}},{editable:true,editrules:{required:true,integer:true},editoptions:{maxlength:6,dataInit:function(elem){setTimeout(function(){$(elem).numeric_Grid('positive');},100);}}},{editable:false,editrules:{required:false}},{editable:false,editrules:{required:true}},{editable:true,editrules:{required:true,number:true}}"></c:set>
<c:if test="${amendmentType ne 'positive'}">
<c:set var="amendmentGrid" value="negative,negativeAmendmentMsg"></c:set>
<c:set var="subGridColProp1" value="{editable:false,editrules:{required:true}},{editable:false,editrules:{required:false,integer:true}},{editable:true,editrules:{required:true,integer:true},editoptions:{maxlength:7,dataInit:function(elem){setTimeout(function(){$(elem).numeric_Grid('negative');},100);}}},{editable:false,editrules:{required:false}},{editable:false,editrules:{required:true}},{editable:true,editrules:{required:true,number:true}}"></c:set>
</c:if>

<%-- This portlet resource maps the Action in Base Controller to load data in services Amendment grid  --%>
<portlet:resourceURL var='loadServices' id='loadGridData' escapeXml='false'>
	<portlet:param name="transactionName" value="contractServicesModificationGrid"/>
	<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
	<portlet:param name="gridLabel" value="amendmentServices.grid"/>
	<portlet:param name="subBudgetId" value="${subBudgetId}"/>
	<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>

<%-- This portlet resource maps the Action in Base Controller when edit operation is performed in services Amendment grid  --%>
<portlet:resourceURL var='servicesOperationGrid' id='gridOperation' escapeXml='false'>
	<portlet:param name="transactionName" value="contractServicesModificationGrid"/>
	<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
	<portlet:param name="gridLabel" value="amendmentServices.grid"/>
	<portlet:param name="subBudgetId" value="${subBudgetId}"/>
	<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>

<c:set var="gridColNames"><%=HHSUtil.getHeader("amendmentServices.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("amendmentServices.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("amendmentServices.grid")%></c:set>
<jq:grid id="servicesAmendmentGrid-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp1}" 
		 gridUrl="${servicesSubGridHeaderRow}"
		 subGridUrl="${loadServices}"
	     cellUrl="${servicesOperationGrid}"
	     editUrl="${servicesOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
	     rowsPerPage="10"
	     modificationType="${amendmentGrid}"
         isSubGrid="true"
         nonEditColumnName="serviceName,remUnits,fyBudget,remainingAmt"
         negativeCurrency="modificationAmt"
	     operations="add:false,edit:true,cancel:true,save:true"
	     exportFileName="SERVICES_DETAIL"
/>
<div>&nbsp;</div>
<H3>Cost Center</H3>
<%-- This portlet resource maps the Action in Base Controller to display header data in cost-Center Amendment grid  --%>
<portlet:resourceURL var='costCenterSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
	<portlet:param name="gridLabel" value="amendmentCostCenter.grid"/>
</portlet:resourceURL>

<%-- This portlet resource maps the Action in Base Controller to load data in cost-Center Amendment grid  --%>
<portlet:resourceURL var='loadCostCenter' id='loadGridData' escapeXml='false'>
	<portlet:param name="transactionName" value="costCenterModificationGrid"/>
	<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
	<portlet:param name="gridLabel" value="amendmentCostCenter.grid"/>
	<portlet:param name="subBudgetId" value="${subBudgetId}"/>
	<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>

<%-- This portlet resource maps the Action in Base Controller when edit operation is performed in services Amendment grid  --%>
<portlet:resourceURL var='costCenterOperationGrid' id='gridOperation' escapeXml='false'>
	<portlet:param name="transactionName" value="costCenterModificationGrid"/>
	<portlet:param name="beanName" value="com.nyc.hhs.model.CBServicesBean"/>
	<portlet:param name="gridLabel" value="amendmentCostCenter.grid"/>
	<portlet:param name="subBudgetId" value="${subBudgetId}"/>
	<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>


<c:set var="gridColNames"><%=HHSUtil.getHeader("amendmentCostCenter.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("amendmentCostCenter.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("amendmentCostCenter.grid")%></c:set>

  <jq:grid id="costCenterAmendmentGrid-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${servicesSubGridHeaderRow}"
		 subGridUrl="${loadCostCenter}"
	     cellUrl="${costCenterOperationGrid}"
	     editUrl="${costCenterOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
	     rowsPerPage="10"
	     modificationType="${amendmentGrid}"
         isSubGrid="true"
         nonEditColumnName="costCenterName,fyBudget,remainingAmt"
         negativeCurrency="modificationAmt,modUnits"
	     operations="add:false,edit:true,cancel:true,save:true"
	     exportFileName="COST_CENTER_DETAIL"
/>

<div>&nbsp;</div>
<c:set var="entityType"><%=HHSConstants.AUDIT_CONTRACT_BUDGET_AMENDMENT%></c:set>
<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_BUDGET_AMENDMENT%></c:set>
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