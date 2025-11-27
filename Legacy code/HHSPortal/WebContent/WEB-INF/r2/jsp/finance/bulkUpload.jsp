<%--added new in R4 --%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/contractlist.js"></script>

<portlet:defineObjects />
<portlet:actionURL var="UploadBulkContractUrl" escapeXml="false">
     <portlet:param name="action" value="contractListAction" />	
	<portlet:param name="submit_action" value="getUploadBulkContract" />
</portlet:actionURL>



<input type = 'hidden' value='${UploadBulkContractUrl}' id='UploadBulkContractUrl'/>


<style>
.contractPopup span.error{
	width:44% !important;
}
.contractPopup .date{
	width:5% !important;
}
.tabularContainer{
		overflow-y:auto;
		height:200px;
	}
	.button{
		position:static !important;
	}
.tabularContainer .formcontainer .row span.error{
	margin-left:70%;
}
</style>

<form:form action="${UploadBulkContractUrl}" enctype="multipart/form-data" method="post" name="UploadBulkContract" id="UploadBulkContract">
<div class="content">
	<div id="newTabs">
		<div class='tabularCustomHead'>
			<span id="contractTypeId">Bulk Upload</span> 
			<a href="javascript:void(0);" class="exit-panel">&nbsp;</a>
		</div>		
		<div id="uploadBulkContract">
			<div class="tabularContainerx"  style="padding-left: 20px; padding-top: 10px; height: 205px;">
				 <div id="OverlayErrorDiv" class="failed" > </div>
				<h2 align="left">Bulk Upload
				 </h2>
				<div class='hr'></div>
				<p style="width: 333px;">To add contracts with and without CoF, please upload the completed bulk upload template</p>				
				<p><label class="required">*</label>Indicates a required field</p>
				<div class="row" align="center"  >
			   
				<span class="label equalForms" style="width: 353px;" >  <p style="width: 313px; border-left-width: 0px; padding-left: 0px; margin-left: 0px;"><label class="required" >*</label><b>Upload completed bulk upload template</b></br>(Please ensure you are only uploading the HHSA-</br>provided template)
				
				</p></span>
				 <span class="formfield">
					<input type="file" id="uploadfile" name="uploadfile" value="Choose File" onchange="displayDocName(this)" style="width: 206px; padding-left: 16px; " />
			</span><span id="fileSelect" style="color:red;"></span></div>
		       
				</div>
				
			
		<div class="buttonholder" align="center" style="padding-right: 55px; padding-bottom: 10px; padding-left: 110px; height: 22px;">
			<span><input type="button" class="graybtutton" align="top"  value="Cancel" onclick="clearAndCloseOverLay();"/> </span>
			<span><input type="button" id="uploadButton"  class="button" value="Upload" class="add marginReset" onclick="bulkUploadReturn();"  /></span>
		</div>
		
		</div>
		</div>
	</div>
</form:form>
<script type="text/javascript">
var requiredKey= "<fmt:message key='REQUIRED_FIELDS'/>";
</script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/addContract.js"></script>
