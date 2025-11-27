<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.nyc.hhs.util.HHSUtil"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page import="java.util.*"%>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<!--Start Added in R5 -->
<%@include file="/WEB-INF/r5/jsp/assigneeListMapping.jsp" %>
<portlet:resourceURL var="setDefaultUser" id="setDefaultUser"></portlet:resourceURL>
<input type="hidden" id="setDefaultUser" value="${setDefaultUser}"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/defaultAssignee.js"></script>
<!--End Added in R5 -->


<portlet:defineObjects/>

<c:if test="${isReadOnly eq 'false'}">
<input type='hidden' value='1' id='isLevel'/>
</c:if>
<c:if test="${isReadOnly eq 'true'}">
<input type='hidden' value='2' id='isLevel'/>
</c:if>

<%-- JQuery Grid links start--%>			
<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/budgetList.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/unsaveData.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>

<%-- JQuery Grid links end--%>
	
<style type="text/css">
.alert-box-upload, .alert-box-delete, .alert-box-viewDocumentProperties, .alert-box-addDocumentFromVault{
	position:fixed !important;
	top:25% !important
}

.accContainer .ui-jqgrid .ui-jqgrid-pager .ui-pg-div span.ui-icon {
    margin: 0  2px !important;
}
.gridFormField .ui-widget-content {
    border: 1px solid #a6c9e2;
}
</style>

<%-- Accounts grid attributes starts --%>
	<portlet:resourceURL var='mainAccountGrid' id='mainAccountGrid'
		escapeXml='false'>
	</portlet:resourceURL>
	
	<portlet:resourceURL var='subAccountGrid' id='subAccountGrid' escapeXml='false'>
		<portlet:param name="screenName" value="financialsAccountGrid"/>
		<portlet:param name="procCerTaskScreen" value="true"/>
	</portlet:resourceURL>	
	
	<portlet:resourceURL var='accountOperationGrid' id='accountOperationGrid' escapeXml='false'>
		<portlet:param name="screenName" value="financialsAccountGrid"/>
		<portlet:param name="procCerTaskScreen" value="true"/>
	</portlet:resourceURL>
	
<%-- Accounts grid attributes ends --%>



<div class="clear"></div>
<portlet:actionURL var="submitAction" escapeXml="false">
	<portlet:param  name="taskcontrollerAction" value="save"/>
</portlet:actionURL>

<%-- Container Starts --%>
<div class="complianceWrapper"> 
	<div class='accContainer floatNone'>
	<div>
<d:content section="<%=HHSComponentMappingConstant.PCOF_S389_SECTION%>" isReadOnly="${accessScreenEnable eq false}" authorize="" >
<div class="failed" id="errorGlobalMsg"></div>
<div class='complianceWrapper'>
<task:taskContent workFlowId="" taskType="taskProcurementCertificationFunds" isTaskScreen=""  level="header" taskDetail="" ></task:taskContent>
<d:content isReadOnly="${isReadOnly || !detailsBeanForTaskGrid.isTaskAssigned}" >
<form:form action="${submitAction}" method="post" name="taskForm1" id="taskForm1">
<input type = "hidden" value="${submitAction1}" id="hiddenURL"/>

<jsp:useBean
	id="procurementCOFDetails" class="com.nyc.hhs.model.ProcurementCOF"
	scope="request"></jsp:useBean>
	
<h2>Procurement Certification of Funds Details</h2>
<div class='hr'></div>
<h3>Basic Information</h3>
<div class="formcontainer">
<c:set var="agencyParts" value="${fn:split(ProcurementCOF.agencyName, '-')}" />
 <div class="row">
      <span class="label">Agency:</span>
      <span class="formfield">${agencyParts[1]} (${fn:trim(agencyParts[0])})</span> 
 </div>
  <div class="row">
      <span class="label">Agency Code:</span>
      <span class="formfield">${ProcurementCOF.agencyCode}</span>
    </div>  
	<div class="row">
      <span class="label">Procurement Value:</span>
      <span class="formfield"><label id="aoProcValue">${ProcurementCOF.procurementValue}</label></span>
    </div>
	<div class="row">
       <span class="label">Procurement Title:</span>
       <span class="formfield">${ProcurementCOF.procurementTitle}</span>
    </div>
    	<div class="row">
      <span class="label">Contract Start Date:</span>
      <span class="formfield">${ProcurementCOF.contractStartDate}</span>
    </div>
    <div class="row">
      <span class="label">Contract End Date:</span>
      <span class="formfield">${ProcurementCOF.contractEndDate}</span>
    </div>
</div>

<p>&nbsp;</p>

<h3>Chart of Accounts Allocation</h3>
	<div class='gridFormField gridScroll'>
		<jq:grid id="ProcCoAAllocation" 
					gridColNames="${GridColNames}"
					gridColProp="${MainHeaderProp}"
					subGridColProp="${SubHeaderProp}"
					gridUrl="${mainAccountGrid}"
					subGridUrl="${subAccountGrid}" nonEditColumnName="total" cellUrl="${accountOperationGrid}"
	     			editUrl="${accountOperationGrid}" positiveCurrency="${columnsForTotal}"
					dataType="json" methodType="POST" columnTotalName="${columnsForTotal}" notAllowDuplicateColumn="uobc,subOC,rc"
					isPagination="true" rowsPerPage="5" isSubGrid="true" isReadOnly="false"
					operations="del:true,edit:true,add:true,cancel:true,save:true" autoWidth="false" isCOAScreen="true"
					 />
	</div>

<p>&nbsp;</p>
<%-- Funds grid attributes starts --%>

	<portlet:resourceURL var='mainFundingGrid' id='mainFundingGrid' escapeXml='false'>
		<portlet:param name="screenName" value="financialsFundingGrid"/>
	</portlet:resourceURL>
	
	<portlet:resourceURL var='financialsFundingGrid' id='subFundingGrid' escapeXml='false'>
		<portlet:param name="screenName" value="financialsFundingGrid"/>
		<portlet:param name="procCerTaskScreen" value="true"/>
	</portlet:resourceURL>
	<portlet:resourceURL var='fundingOperationGrid' id='fundingOperationGrid' escapeXml='false'>
		<portlet:param name="screenName" value="financialsFundingGrid"/>
		<portlet:param name="procCerTaskScreen" value="true"/>
	</portlet:resourceURL>

<%-- Funds grid attributes ends --%>
<h3>Funding Source Allocation (Optional)</h3>
<p>The optional fields below may be used to indicate the funding source allocation at the point of the initial Certification of Funds. These fields are for reference purposes only.</p>
	<div class='gridFormField gridScroll'>
					<jq:grid id="fundingSource" 
					gridColNames="${FundingGridColNames}"
					gridColProp="${FundingMainHeaderProp}"
					subGridColProp="${FundingSubHeaderProp}"
					cellUrl="${fundingOperationGrid}"
	                editUrl="${fundingOperationGrid}"
					gridUrl="${mainFundingGrid}" positiveCurrency="${columnsForTotal}"
					subGridUrl="${financialsFundingGrid}" nonEditColumnName="total"
					dataType="json" methodType="POST" columnTotalName="${columnsForTotal}" 
					isPagination="true" rowsPerPage="5" isSubGrid="true" isReadOnly="false"
					operations="del:false,edit:true,add:false,cancel:true,save:true"
					autoWidth="false" isCOAScreen="true"
					 />
	</div>

<%-- Form Data Ends --%>
<p>&nbsp;</p>
</form:form>
</d:content>
   <div class='clear'></div>
	<task:taskContent workFlowId="" taskType="taskProcurementCertificationFunds" isTaskScreen=""  level="footer"></task:taskContent>
</div>


<div class="overlay"></div>
	  <div class="alert-box-help">
				<div class="tabularCustomHead toplevelheaderHelp"></div>
	            <div id="helpPageDiv"></div>
		  		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
	</div>
</d:content>
</div>
</div>
</div>
<script type="text/javascript">
function formSubmit(){
	$("#hiddenURL").val("${submitAction}");
	$("#"+document.forms[0].id).attr("action",$("#hiddenURL").val());
	document.taskFooterForm.submit();
}
function finishTaskValidation(){
	var coaTotal=$('#table_ProcCoAAllocation tbody tr:eq(1) td:last').html().replace('$', '').replace(',', '');
	var procurementVal=$("#aoProcValue").html().replace('$', '').replace(',', '');

	$("#errorGlobalMsg").hide();
	$("#errorGlobalMsg").html("");

	// additional check if coa grid total and procurement value is not equal when pcof is approved from above level 1 reviewer 
	// as part of build 2.6.0, enhancement 5653
	if(procurementVal!=coaTotal && ($("#isLevel").val()=='1' || $("#finishtaskchild").val() == 'Approved'))
		{
		 $("#errorGlobalMsg").show();
		 $("#errorGlobalMsg").html("Total amount allocated to the Chart of Accounts must equal the Procurement Value.");
		return false;
		}

	if ($("#finishtaskchild").val() == 'Returned for Revision' && $("#internalCommentArea").val() == "" && $("#isLevel").val()!='1' ){
		$("#errorGlobalMsg").show();
		$("#errorGlobalMsg").html("Comments must be entered in the comment box");
		return false;
	}
	return true;
}
</script>

<%-- Container Ends --%>