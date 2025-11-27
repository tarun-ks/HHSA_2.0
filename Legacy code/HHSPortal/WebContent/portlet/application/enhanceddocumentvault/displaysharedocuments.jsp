<%@page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects/>
<script type="text/javascript" src="../js/sharedocuments.js"></script>
<%
String lsUserRole=(String)request.getSession().getAttribute("role");
String lsOrgType=(String)request.getSession().getAttribute("org_type");
boolean isOwnDV=request.getParameter("isOwnDV")==null?true:Boolean.valueOf(request.getParameter("isOwnDV").trim());
%>


<script type="text/javascript">
var displayShareAction = document.displaysharedform.action;
//on load function to perform various checks on loading of jsp
function onReady(){
	//Added for disabling removeSelected button on ready
	lockingForView();
	document.getElementById("removeselected").disabled = true;
	//end
	// This will execite when RemoveAll button is clicked
	$('#removeall').click(function() { // bind click event to link
				removeallprov('<%=request.getAttribute("documentId")%>','<%=request.getAttribute("documentName")%>','<%=request.getAttribute("docType")%>');
				var options = {
						success : function(responseText, statusText, xhr) {
							var response = new String(responseText);
							var responses = response.split("|");
							if(!(responses[1] == "Error" || responses[1] == "Exception"))
							{
								$(".overlay").closeOverlay();
								removePageGreyOut();
						        var data = $(responseText);
						        if(data.find(".tabularWrapper").size()>0)
						   		{
						        	$("#myform .tabularWrapper").replaceWith(data.find(".tabularWrapper"));
						 	        var _message = data.find("#message").val();
						 	        var _messageType = data.find("#messageType").val();
						 	       
						 	       
						 	        $(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
						 	       $(".messagediv").removeClass("failed passed");
						 	        $(".messagediv").addClass(_messageType);
						 			$(".messagediv").show();
						 			 $(".overlay").closeOverlay();
						 	        removePageGreyOut();
						   		}    else
						        {$(".overlay").closeOverlay();
					 	        removePageGreyOut();
						        }
							}
						},
						error : function(xhr, ajaxOptions, thrownError) {
							showErrorMessagePopup();
							removePageGreyOut();
						}
					};
		    	$(this.form).ajaxSubmit(options);
				return false;
	});
	// This will execite when Remove Selected button is clicked
	$('#removeselected').click(function() { // bind click event to link
				removeselectedprov('<%=request.getAttribute("documentId")%>','<%=request.getAttribute("documentName")%>','<%=request.getAttribute("docType")%>');
				var options = {
						success : function(responseText, statusText, xhr) {
							var response = new String(responseText);
							var responses = response.split("|");
							if(!(responses[1] == "Error" || responses[1] == "Exception"))
							{
								$(".overlay").closeOverlay();
								removePageGreyOut();
						        var data = $(responseText);
						        if(data.find(".tabularWrapper").size()>0)
						   		{
						        	$("#myform .tabularWrapper").replaceWith(data.find(".tabularWrapper"));
						 	        var _message = data.find("#message").val();
						 	        var _messageType = data.find("#messageType").val();
						 	       
						 	       
						 	        $(".messagediv").html(_message+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
						 	       $(".messagediv").removeClass("failed passed");
						 	        $(".messagediv").addClass(_messageType);
						 			$(".messagediv").show();
						 			 $(".overlay").closeOverlay();
						 	        removePageGreyOut();
						   		}    else
						        {$(".overlay").closeOverlay();
					 	        removePageGreyOut();
						        }
							}
						},
						error : function(xhr, ajaxOptions, thrownError) {
							showErrorMessagePopup();
							removePageGreyOut();
						}
					};
		    	$(this.form).ajaxSubmit(options);
		    	pageGreyOut();
				return false;
	});
	
	
}
</script>
<!-- Overlay for shared documents  -->
<div class="tabularCustomHead"><%=request.getAttribute("documentName")%></div>
<div class="overlaycontent" style="height:320px;">
	<form id="displaysharedform" name="displaysharedform" action="<portlet:actionURL/>" method ="post">
		<input type="hidden" id="message" name ="message">
		<input type="hidden" id="messageType" name ="messageType">
		<input type="hidden" id="nextDisplaySharedAction" name ="next_action">
		<input type="hidden" id="parentFolderIdUnshare" name ="parentFolderIdUnshare">
			<!--Start of R4 Document Vault changes: Component Mapping check added for Agency security matrix-->
			<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S038_PAGE, request.getSession())|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S033_AGENCY_PAGE, request.getSession())) {%>
			<!--End of R4 Document Vault changes -->
			<div style="height: 274px; overflow:auto;">
				<p><b>Your organization has chosen to share this file with the following Providers/NYC Government Agencies:</b></p>
				<div class="" style='float: left;margin-left: 10px;'>
				<c:forEach var="organization" items="${providerArray}">
						<div>
							
							<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S038_SECTION, request.getSession())){ 
							 if(isOwnDV){%>
							
							 <label class="orglist"><input type="checkbox" name="provCheck" id ="provCheck" value="${organization}" onclick="javascript:enableButton()"/>${organization}</label>
							<%}else{%>
							 <label class="orglist"><input type="checkbox" name="provCheck" id ="provCheck" value="${organization}" disabled="disabled"/>${organization}</label>
							
    						<%}}
							else{%>
							<label class="orglist"><input type="checkbox" name="provCheck" id ="provCheck" value="${organization}" disabled="disabled"/>${organization}</label>
							<%}%>
							
						</div>
					</c:forEach>
				</div>
			</div> 
				<div class='clear'>&nbsp;</div>
				<div>
				<c:set var="check"><%= session.getAttribute("permissionType") %></c:set>
			<c:choose>
				<c:when test="${check ne 'R'}">
					<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S038_SECTION, request.getSession())){ %>
					<% if (isOwnDV) {%>					
					<input class="button redbtutton" type="button" title="- Remove All" value="- Remove All" id="removeall"/>
					<input class="button graybtutton1" type="button" title="- Remove Selected" id="removeselected" value="- Remove Selected" />
					<% }else{ %>
					<input class="button redbtutton" type="button" title="- Remove All" value="- Remove All" id="removeall" disabled="disabled"/>
					<input class="button graybtutton1" type="button" title="- Remove Selected" id="removeselected" value="- Remove Selected" disabled="disabled"/>
					<%}}
					else{%>
					<input class="button redbtutton" type="button" title="- Remove All" value="- Remove All" id="removeall" disabled="disabled"/>
					<%}%>
					</c:when>
					<c:otherwise>
					<input class="button redbtutton" type="button" title="- Remove All" value="- Remove All" id="removeall" disabled="disabled"/>
					<!-- Added fix for defect #7853 -->
					<input class="button graybtutton1" type="button" title="- Remove Selected" id="removeselected" value="- Remove Selected" disabled="disabled" />
					<!-- defect #7853 end -->
					</c:otherwise>
			</c:choose>
					<input type="button" value="    Close    " title="Close" id="closeoverlay" class="button graybtutton" style="float:right;" >
				
			<%}else{ %>
				 <h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
			<%} %>
			</div>
	</form>
	<a href="javascript:void(0);" class="exit-panel nodelete" title="Exit">&nbsp;</a>
</div>
