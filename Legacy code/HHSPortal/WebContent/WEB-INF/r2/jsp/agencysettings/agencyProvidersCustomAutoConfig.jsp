<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!-- The jsp is added in Release 7 for Modification Auto Approval Enhancement.
This jsp shows the agency specific providers and their custom thresholds  -->
<%@ page errorPage="/error/errorpage.jsp"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil" %>
<%@page import="com.nyc.hhs.util.DateUtil" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>

<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/configureAutoApproval.js"></script>
	
<portlet:defineObjects />

<h4 class='generateListPanel agencyGreyTitle'>Budget Modification Thresholds</h4>
<span><h3>Default Threshold</h3></span>
<div class="row">To update the default threshold, make changes to the "Threshold" field, then click Save.</div>
			
<div id='tabs-container'>
	<div class="row">
		<span class="label">Threshold:</span>
		<span class="formfield"><input type="text" id="thresholdValue" size="14" maxlength="3"
			value="${DefaultAutoConfigThreshold.thresholdPercentage}" onchange="setFormDataChange()"/>%</span>
	</div>
	<div>&nbsp;</div>
	<div id="modifiedByDiv" class="row">
		<span class="label">Last Modified By:</span>
		<span class="formfield"><input type="text" id="modified_by" size="14" value="${DefaultAutoConfigThreshold.modifiedByUserName}" disabled/></span>
	</div>
	<div>&nbsp;</div>
	<div id="modifiedDateDiv" class="row">
		<span class="label">Last Modified Date:</span>
		<span class="formfield"><input type="text" id="modifiedDate" size="14" value="${DefaultAutoConfigThreshold.modifiedDate}" disabled/></span>
	</div>
	<div class="buttonholder">
		<input id="saveThreshold" name="saveThreshold" type="button"
			class="button" value="Save" onclick="javascript:ajaxCallToSaveThreshold();" />
	</div>
	<div><span><h3>Custom Threshold</h3></span>
		<div class="row"><span>Add providers to the table below to set custom thresholds that differ from the agency default threshold above.
			 Any changes to the default threshold will not affect these providers.Threshold column takes numeric values only.</span></div>
	</div>
	<br></br>
	<div id="customAtoConfigDiv">

	<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
	<portlet:param name="gridLabel" value="customAutoConfigdetails.grid"/>
	</portlet:resourceURL>
	
	<portlet:resourceURL var='getGridOperationMessage' id='getGridOperationMessage' escapeXml='false'>
	</portlet:resourceURL>
	<input type='hidden' value='${getGridOperationMessage}' id='hiddenGetGridOperationMessageURL' />

	<portlet:resourceURL var='customAutoApprovalLoadData' id='customloadGridData' escapeXml='false'>
	<portlet:param name="transactionName" value="customAutoApprovalDetailsGrid"/>
	<portlet:param name="beanName" value="com.nyc.hhs.model.AutoApprovalConfigBean"/>
	<portlet:param name="gridLabel" value="customAutoConfigdetails.grid"/>
	<portlet:param name="agencyId" value="${asAgencyId}"/>
	</portlet:resourceURL>

	<portlet:resourceURL var='customAutoApprovalOperationGrid' id='customgridOperation' escapeXml='false'>
	<portlet:param name="transactionName" value="customAutoApprovalDetailsGrid"/>
	<portlet:param name="beanName" value="com.nyc.hhs.model.AutoApprovalConfigBean"/>
	<portlet:param name="agencyId" value="${asAgencyId}"/>
	</portlet:resourceURL>

	<c:set var="gridColNames"><%=HHSUtil.getHeader("customAutoConfigdetails.grid")%></c:set>
	<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("customAutoConfigdetails.grid")%></c:set>
	<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("customAutoConfigdetails.grid")%></c:set>
	
	
	<div id="gridDetails">
		<jq:grid id="customAutoApprovalDetailsGrid-${asAgencyId}" 
        	 isReadOnly="false"
	    	 gridColNames="${gridColNames}"
	    	 gridColProp="${gridColProp}" 
	    	 subGridColProp="{editable:false, editrules:{isMandatoryField},edittype:'select',editoptions:{}},
             	     {editable:true, editrules:{required:true,number:true,allowOnlyPercentValue}, editoptions:{maxlength:3,dataInit: function (elem){$(elem).autoNumeric({aSep:'',vMin:'0',vMax:'100'})}} },
             	     {editable:false, editrules:{required:false,text:true}},
             	     {editable:false, editrules:{required:false,text:true}}"
			 gridUrl="${SubGridHeaderRow}"
			 subGridUrl="${customAutoApprovalLoadData}"
	   	  	 cellUrl="${customAutoApprovalOperationGrid}"
	   	 	 editUrl="${customAutoApprovalOperationGrid}"
	     	 dataType="json" methodType="POST"
         	 isPagination="true"
	    	 rowsPerPage="10"
         	 isSubGrid="true"  
         	 nonEditColumnName="modifiedDate,modifiedByUserName"
   	    	 operations="del:true,edit:true,add:true,cancel:true,save:true"
   	     	 dropDownData="${providerList}"
   	     	 autoWidth="false"
   	     	 columnTotalName=""
   	     	 exportFileName="CUSTOM_THRESHOLD_"
   	     	 callbackFunction="showGridSuccessMessage();"
		/>
	</div>
	</div>
</div>
