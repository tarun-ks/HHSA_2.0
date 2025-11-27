<%--Added in R4--%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.HashMap" %>
<%@page import="java.util.Iterator" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/contractlist.js"></script>

<%--This JSP is for Add Contract screen S302 in Financials tab to add new contracts.  --%>

<style>
.contractPopup span.error{
	width:44% !important
}
.contractPopup .date{
	width:5% !important
}
.tabularContainer{
		overflow-y:auto;
		height:250px
	}
	.button{
		position:static !important
	}
.tabularContainer .formcontainer .row span.error{
	margin-left:70%
}
</style>


<style type="text/css">
.addContract {
	background:#FFF;
	display:none ;
	top: 20%;
	width: 50%;
	z-index: 1001;
	position: fixed
}
h2{width:82%}
 
.alert-box-amend-contract .ui-state-active .alert-box-bul-upload {
	background: #4297E2 !important
}
.alert-box-amend-contract{
	background: #FFF;
    display: none;
    z-index: 1001;
    position: fixed
}

</style>
<portlet:defineObjects />
<%--  Overlay Popup Starts --%>
<%-- This Portlet resource URL is for Submit button for adding contract. --%>


<portlet:resourceURL var="getBulkUploadTemplatePage" id="getBulkUploadTemplatePage" escapeXml="false">
</portlet:resourceURL>
<portlet:resourceURL var="getContractTypeOverlayPage" id="getContractTypeOverlayPage" escapeXml="false">
</portlet:resourceURL>

<input type = 'hidden' value='${getContractTypeOverlayPage}' id='hiddenContractTypeOverlayPageUrl'/>

<form:form id="downloadBulkUploadTemplateForm" action="" method ="post" name="addContractForm">
<div class="content" >
	<div id="newTabs">
		<div class='tabularCustomHead'>
			<span id="contractTypeId">Bulk Upload - Are you sure you are using the correct template?</span> 
			<a href="javascript:void(0);" class="exit-panel">&nbsp;</a>
		</div>
		
		<div id="bulkUploadConfirm">
			<div class="tabularContainer">
			<%if(( null != request.getAttribute("Error") ) && !"".equalsIgnoreCase((String)request.getAttribute("Error"))){%>
				     <div id="transactionStatusDiv" class="failed breakAll" style="display:block" ><%=request.getAttribute("Error")%> </div>
				<%}%>
				 <div id="ErrorDiv" class="failed breakAll"> </div>
				
				<h3 align="left" style="padding-left: 30.5px; margin-left: 37px;">Please confirm you are using the latest version of HHS<br>
				Accelerator approved template?
				 </h3>				 
				
				
				<div class="row" align="center" style="margin-left: 66px; margin-right: 0px;" >
			
				<span class="label equalForms" ><p>Please download the latest version of the</br>template using the following link:</p></span>
				 <span>
				 <%
				String tempVersion="";
				String lastModified="";
				String docId="";				
				if(request.getAttribute("docProps")!=null){
				HashMap docProps=(HashMap)request.getAttribute("docProps");
	            	  tempVersion=(String) docProps.get("TEMPLATE_VERSION_NO");
	            	  lastModified=(String)docProps.get("LAST_MODIFIED_DATE");
	            	  docId=(String) docProps.get("ID"); 
				}
				%>
			    <input type="button" class="graybtutton" id="btnDownloadBulkUploadTemplate" style = "float:left" value="Download Template" onclick="getBulkUploadTemp('Bulk Upload Template','<%=docId %>','<%=request.getContextPath()%>');"/> 
			
		        </span>
		      
				</div>
				
				<div class="row" align="center" style="margin-left: 66px; margin-right: 0px;"  >
				<div>
				<span class="label equalForms" ><p>Latest Version:</p></span>
				
				<input type='hidden' value='<%=docId %>' id='docId' />
				 <span class="" style="float:left;padding-top:10px;" ><%=tempVersion %></span>
		        </div>
				</div>
				
				<div class="row" align="center" style="margin-left: 66px; margin-right: 0px;">
				<div>
				<span class="label equalForms" ><p>Last Modified:</p></span>
				 <span class="" style="float:left;padding-top:10px;"><%=lastModified %></span>
		        </div>
				</div>
				
				
				</br>
			
		<div class="buttonholder" align="center" style="padding-right: 235px; padding-bottom: 10px; width: 414px;">
			<span><input type="button" class="graybtutton" align="top"  value="Cancel" onclick="clearAndCloseOverLay();"/> </span>
			 <span><input type="button" id="addContractButton" style = "width: 20px;"  class="button" value="Confirm" onclick="bulkUploadConfirm('Bulk Upload','');" /></span>
			 <%--   <span><input type="submit" id="addContractButton"  class="button" value="Upload" class="add marginReset"  /></span> --%>
			
		
		</div>
		
		</div>
	</div>
</div>
</form:form>

</div>

<script type="text/javascript">
var requiredKey= "<fmt:message key='REQUIRED_FIELDS'/>";
</script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/addContract.js"></script>