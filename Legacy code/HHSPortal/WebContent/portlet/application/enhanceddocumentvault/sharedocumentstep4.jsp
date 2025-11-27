<%@page import="java.util.Iterator"%>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant" %>
<portlet:defineObjects/>
<portlet:resourceURL var="backtoStep3Sharing" id="backtoStep3Sharing" escapeXml="false"></portlet:resourceURL>
<portlet:actionURL id="finalShareDocument" var="finalShareDocument">
<portlet:param name="submit_action" value="finalShareDocument"/>
</portlet:actionURL>
<style type="text/css">
	.langCol1 {
		width: 350px;
	}
	.langCol2 {
		width: 450px;
	}
</style>
<script type="text/javascript" src="../js/sharedocuments.js"></script>
<script type="text/javascript">
var originalFormAction;
//on load function to perform various checks on loading of jsp
function onReady(){ 
	var count=0;	
	$("table[id*='defectTable'] tr td:nth-child(2)").each(function () {
		if($(this).text().toLowerCase().indexOf('folder')>-1){
			count++;
		}
		});
	if(count==0)
	{
	$('#checkForFinish1').parent().hide();
	$('#finish').removeAttr('disabled');
	}
	else{
		$("#finish").attr('disabled',true);
	}
	    originalFormAction = $("#share4").attr('action');
		if("null" != '<%= request.getAttribute("message")%>'){
			$("#errorDiv").html('<%= request.getAttribute("message")%>');
			$("#errorDiv").addClass('<%= request.getAttribute("messageType")%>').show();	
		}
		populateProviderAgencyList("<%=request.getAttribute("loProvAgencyList")%>");
	       	
}
$(function(){
	$("#checkForFinish1").on('click',function() 
	{
		allowFinishButtonEnable("<%=request.getAttribute("loProvAgencyList")%>");
		
}); 
});
	

</script>
<input type="hidden" id="backtoStep3SharingForm" value="${backtoStep3Sharing}"/>
		<input type="hidden" id="finalSharing" value="${finalShareDocument}"/>
<!-- Sharing Overlay Step 4 -->
<form name="share4" id="share4" action="<portlet:actionURL/>" method ="post" >
		<input type="hidden" id="message" name ="message"/>
		<input type="hidden" id="messageType" name ="messageType"/>
		<input type="hidden" id="nextAction4" name ="next_action"/>
		<input type="hidden" id="parentFolderId" name ="parentFolderId"/>
		<!--Start of R4 Document Vault changes: Component Mapping check added for Agency security matrix-->
		<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S042_PAGE, request.getSession()) || CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S033_AGENCY_PAGE, request.getSession())) {%>
		<!--End of R4 Document Vault changes -->
		<div class="wizardTabs" style='width:800px;'>
			<div style='padding-left:10px'>
			<div id="errorDiv" style='width:92%;'></div>
			<p>Please confirm that <b>ALL</b> the Providers and/or NYC Agencies listed below will be granted access to <b>ALL</b> 
			the following documents. Click the "Back" button to change your selections or the "Finish" button to grant access.</p>
			<div class='hr'></div>
			<b>I grant the following Providers/NYC Agencies access to the following documents:</b>
			<div class="langCol1" style="width:350px">
				<div  class="tabularWrapper" style='overflow:auto; height:300px !important;width:100%; float:left;'>
					<table id='mytable3'>
						<th class="heading">
							<b>Provider/NYC Agency Name</b>
						</th>	
					</table>
				</div>
			</div>
			<div class="langCol2" style="width:430px">
				<div class="tabularWrapper" style='overflow:auto; height:300px !important; width:100%;'>
					<table id="defectTable">
                       <tr>
                             <th>Document Name</th>
                             <th>Document Type</th>
                       </tr>
                       <c:forEach items="${shareDocumentList}" var="shareDocumentList" varStatus="counter">
                             <tr class=${counter.index % 2 eq 0?'evenRows':'oddRows'}>
                                   <td>${shareDocumentList.docName}</td>
                                    <c:choose>
                                   <c:when test="${shareDocumentList.docType ne null}">
                               			<td>${shareDocumentList.docType}</td>
                               		</c:when>
                               		<c:otherwise>
                               			<td>Folder</td>
                               		</c:otherwise>	
                               			</c:choose>
                             </tr>
                       </c:forEach>
                    </table>
				</div>
			</div>
			<div>
			<input type="checkbox" id="checkForFinish" unchecked onchange="finishEnable()"><span id='checkForFinish1'>I understand that any documents being uploaded or moved to a shared folder will also automatically be shared with the same organizations.</span></div>
			<div class="buttonholder">
		    <input type="button" id="cancelshare4" value="Cancel" title="Cancel" class="graybtutton"/>
				<input type="button" id="backshare4" value="Back" title="Back" class="graybtutton"/>
				<input type="button" value="Finish" title="Finish" id="finish"/>
			</div>
			</div>
		</div>
			<input type="hidden" name="provAgencyList" value="<%=request.getAttribute("loProvAgencyList")%>" />
			<%}else{ %>
				<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
			<%} %>
			
</form>
